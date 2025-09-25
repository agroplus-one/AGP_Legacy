/**
 * 
 */
package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.managers.IPolizasPctComisionesManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.NumberUtils;
import com.rsi.agp.core.webapp.util.FluxCondensatorObject;
import com.rsi.agp.core.webapp.util.VistaImportes;
import com.rsi.agp.core.webapp.util.VistaImportesPorGrupoNegocio;
import com.rsi.agp.dao.models.comisiones.IPolizasPctComisionesDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.comisiones.Descuentos;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.ComsPctCalculado;
import com.rsi.agp.dao.tables.poliza.ComsPctCalculadoId;
import com.rsi.agp.dao.tables.poliza.LineaGrupoNegocio;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizaPctComisiones;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;

/**
 * @author U029769
 *
 */
public class PolizasPctComisionesManager implements IPolizasPctComisionesManager {

	private Log logger = LogFactory.getLog(getClass());
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");

	private IPolizasPctComisionesDao polizasPctComisionesDao;

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> validaComisiones(Poliza poliza, Usuario usuario) throws Exception {

		Map<String, Object> parameters = new HashMap<String, Object>();
		Object[] paramsGen = null;
		Boolean comisionesInformadas = false;
		try {
			// recogemos los datos del mto de parametros generales
			List resultado = polizasPctComisionesDao.getParamsGen(poliza.getLinea().getLineaseguroid(),
					poliza.getColectivo().getSubentidadMediadora().getId().getCodentidad(),
					poliza.getColectivo().getSubentidadMediadora().getId().getCodsubentidad());

			if (resultado != null && resultado.size() > 0) {
				comisionesInformadas = comisionesPorGrupoNegocioInformadas(poliza.getLinea().getLineaseguroid(),
						resultado);
				paramsGen = (Object[]) resultado.get(0);
			}

			if (paramsGen != null && comisionesInformadas) {
				// recogemos los datos del mto de comisiones por E-S Mediadora
				Object[] comisionesESMed = polizasPctComisionesDao.getComisionesESMed(
						poliza.getLinea().getLineaseguroid(),
						poliza.getColectivo().getSubentidadMediadora().getId().getCodentidad(),
						poliza.getColectivo().getSubentidadMediadora().getId().getCodsubentidad(),
						poliza.getLinea().getCodlinea(), poliza.getLinea().getCodplan(), null);

				if (comisionesESMed != null) {
					// recogemos los datos de mto de descuentos.Si no hay descuentos se sigue con el
					// alta
					List<BigDecimal> oficinas = new ArrayList<BigDecimal>();
					Descuentos descuentos = null;
					if (usuario != null && usuario.getPerfil() != null) {
						logger.debug("obteniendo descuentos...");
						if (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_JEFE_ZONA)) {
							oficinas.addAll(usuario.getListaCodOficinasGrupo());
						} else {
							oficinas.add(usuario.getOficina().getId().getCodoficina());
						}

						descuentos = polizasPctComisionesDao.getDescuentos(
								poliza.getColectivo().getTomador().getId().getCodentidad(), oficinas,
								poliza.getColectivo().getSubentidadMediadora().getId().getCodentidad(),
								poliza.getColectivo().getSubentidadMediadora().getId().getCodsubentidad(),
								usuario.getDelegacion(), poliza.getLinea().getCodplan(),
								poliza.getLinea().getCodlinea());
						if (null != descuentos) {
							logger.debug("descuento obtenido en el alta:" + descuentos.getPctDescMax());
						} else {
							logger.debug("sin dtos definidos en el momento del alta de poliza");
						}
					} else {
						logger.debug("datos usuario a null");
					}
					List<PolizaPctComisiones> porcentajesComis = this.generaPolizaComisiones(resultado, comisionesESMed,
							descuentos, poliza.getIdpoliza(), poliza.getLinea().getLineaseguroid());

					// PolizaPctComisiones ppc = this.generaPolizaComisiones
					// (paramsGen,comisionesESMed,descuentos);

					// formulacion 2.0
					Integer tipoDescRecarg = poliza.getColectivo().gettipoDescRecarg();
					BigDecimal pctDescRecarg = poliza.getColectivo().getpctDescRecarg();
					log("validaComisiones",
							"Datos del colectivo " + poliza.getColectivo().getIdcolectivo() + " tipoDescRecarg: "
									+ poliza.getColectivo().gettipoDescRecarg() + " pctDescRecarg: "
									+ poliza.getColectivo().getpctDescRecarg());
					if (tipoDescRecarg != null && pctDescRecarg != null) {
						for (PolizaPctComisiones polizaPctComisiones : porcentajesComis) {
							BigDecimal pctEsMedNueva = this.calculaPctesmediadora(polizaPctComisiones, tipoDescRecarg,
									pctDescRecarg);
							if (pctEsMedNueva != null)
								polizaPctComisiones.setPctesmediadora(pctEsMedNueva);
						}
					}

					// fin formulacion 2.0
					parameters.put("polizaPctComisiones", porcentajesComis);
				} else {
					parameters.put("alerta", bundle.getString("comisiones.alta.poliza.comisionesESMed.KO"));
				}
			} else {
				parameters.put("alerta", bundle.getString("comisiones.alta.poliza.parametrosGenerales.KO"));
			}
		} catch (Exception e) {
			logger.error("Error al validar las comisiones de la poliza", e);
			throw e;
		}
		return parameters;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Boolean comisionesPorGrupoNegocioInformadas(Long lineaSeguroId, List paramsGrales) throws Exception {
		Boolean res = true;
		try {
			// List<LineaGrupoNegocio>gruposNeg= (List<LineaGrupoNegocio>)
			// polizasPctComisionesDao.get(LineaGrupoNegocio.class,"id.lineaseguroid",
			// lineaSeguroId);
			List<LineaGrupoNegocio> gruposNeg = ((List<LineaGrupoNegocio>) polizasPctComisionesDao
					.getObjects(LineaGrupoNegocio.class, "id.lineaseguroid", lineaSeguroId));

			res = comprobarSiHayGenerica(paramsGrales);

			if (!res) {
				if (null == gruposNeg || gruposNeg.size() < 1) {
					res = false;
				} else {

					for (int j = 0; j < gruposNeg.size(); j++) {
						for (int i = 0; i < paramsGrales.size(); i++) {
							Object[] params = (Object[]) paramsGrales.get(i);
							String grNeg = ((String) params[3]);
							Character cgrNeg = grNeg.charAt(0);
							if (gruposNeg.get(j).getId() != null && gruposNeg.get(j).getId().getGrupoNegocio() != null
									&& cgrNeg.equals(gruposNeg.get(j).getId().getGrupoNegocio())) {
								res = true;
								break;

							} else {
								res = false;
							}
						}
						if (!res) {
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			throw new Exception(
					"comisionesPorGrupoNegocioInformadas - Error comprobando las comisiones por grupo de negocio informadas. ",
					e);
		}

		return res;
	}

	@SuppressWarnings("rawtypes")
	private boolean comprobarSiHayGenerica(List paramsGrales) {
		boolean res = false;
		for (int i = 0; i < paramsGrales.size(); i++) {
			Object[] params = (Object[]) paramsGrales.get(i);
			String grNeg = ((String) params[3]);
			Character cgrNeg = grNeg.charAt(0);
			if (cgrNeg.equals(Constants.GRUPO_NEGOCIO_GENERICO)) {
				res = true;
				// si esta la nueve, no hace falta comprobar las otras.
				break;
			}
		}
		return res;
	}

	private BigDecimal calculaPctesmediadora(PolizaPctComisiones ppc, Integer tipoDescRecarg,
			BigDecimal pctDescRecarg) {
		BigDecimal res = new BigDecimal(0);
		Integer tipoDesc = new Integer(Constants.TIPO_DESC);
		Integer tipoRecarg = new Integer(Constants.TIPO_RECARG);
		if (tipoDescRecarg.compareTo(tipoDesc) == 0) {
			// PCTESMEDIADORA nueva = (1 - (PCT_DESC_RECARG/100))* PCTESMEDIADORA
			res = (new BigDecimal(1).subtract((pctDescRecarg.divide(new BigDecimal(100)))))
					.multiply(ppc.getPctesmediadora()).setScale(2, BigDecimal.ROUND_HALF_UP);
		} else if (tipoDescRecarg.compareTo(tipoRecarg) == 0) {
			// PCTESMEDIADORA nueva = (1 + (PCT_DESC_RECARG/100))* PCTESMEDIADORA
			res = ((pctDescRecarg.divide(new BigDecimal(100))).add(new BigDecimal(1))).multiply(ppc.getPctesmediadora())
					.setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		log("calculaPctesmediadora", "Pctesmediadora calculada: " + res);
		return res;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<PolizaPctComisiones> generaPolizaComisiones(List paramsGen, Object[] comisionesESMed,
			Descuentos descuentos, Long idPoliza, Long lineaSeguroId) {

		List<LineaGrupoNegocio> gruposNeg = ((List<LineaGrupoNegocio>) polizasPctComisionesDao
				.getObjects(LineaGrupoNegocio.class, "id.lineaseguroid", lineaSeguroId));

		Object[] paramGral = null;
		List<PolizaPctComisiones> res = new ArrayList<PolizaPctComisiones>();

		// damos de alta un registro en polizas_pct_comisiones por cada grupo de negocio
		// si esta en parametros generales, ese valor, si no, el del grupo 9 de
		// parametros generales.
		logger.debug("generaPolizaComisiones -gruposNeg.size(): " + gruposNeg.size());
		for (int i = 0; i < gruposNeg.size(); i++) {
			LineaGrupoNegocio temp = gruposNeg.get(i);
			Character grupoN = temp.getId().getGrupoNegocio();
			logger.debug("generaPolizaComisiones -grupoN: " + grupoN);
			paramGral = obtenerParametrosGeneralesDelGrupo(paramsGen, grupoN);
			if (paramGral == null) {
				// debe tener la generica
				paramGral = obtenerParametrosGeneralesDelGrupo(paramsGen, '9');
			}
			if (paramGral != null) {
				logger.debug("generaPolizaComisiones -paramGra[0]: " + paramGral[0] + " paramGral[1] " + paramGral[1]
						+ " paramGral[2] " + paramGral[2]);
				PolizaPctComisiones ppc = new PolizaPctComisiones();
				ppc.setPctadministracion((BigDecimal) paramGral[0]);
				ppc.setPctadquisicion((BigDecimal) paramGral[1]);
				ppc.setPctcommax((BigDecimal) paramGral[2]);
				ppc.setGrupoNegocio(grupoN);
				ppc.getPoliza().setIdpoliza(idPoliza);

				if (descuentos != null) {
					logger.debug(" con descuentos..");
					ppc.setPctdescmax(descuentos.getPctDescMax());
				} else
					ppc.setPctdescmax(null);
				ppc.setPctentidad((BigDecimal) comisionesESMed[0]);
				ppc.setPctesmediadora((BigDecimal) comisionesESMed[1]);
				res.add(ppc);
				logger.debug("generaPolizaComisiones -res added..");
			}
		}
		logger.debug("generaPolizaComisiones - res.size" + res.size());
		
		return res;
	}

	@SuppressWarnings("rawtypes")
	private Object[] obtenerParametrosGeneralesDelGrupo(List paramsGen, Character grupoN) {
		for (int x = 0; x < paramsGen.size(); x++) {
			Object[] paramGral = (Object[]) paramsGen.get(x);
			if (grupoN.charValue() == ((String) paramGral[3]).charAt(0)) {
				return (Object[]) paramsGen.get(x);
			}
		}
		return null;
	}

	@Override
	public void savePolPctComs(PolizaPctComisiones ppc) throws Exception {
		try {
			polizasPctComisionesDao.saveOrUpdate(ppc);

		} catch (Exception e) {
			logger.error("Error al guardar la polizaPctComisiones - savePolPctComs", e);
			throw e;
		}
	}

	/**
	 * Calcula la comision del mediador segun el porcentaje de la Entidad y la
	 * correspondiente a la E-S Mediadora
	 * 
	 * @param polizam
	 *            pctCom pctComisiones de la poliza
	 * @return mapa de comisionEntidad y comisionE-S
	 */
	public Map<String, String> obtenerDesgloseComisiones(final PolizaPctComisiones ppc, BigDecimal primaNeta, final BigDecimal pctComsCalculado) {
		Map<String, String> param = new HashMap<String, String>();

		if (ppc.getPctentidad() != null && ppc.getPctesmediadora() != null) {
			if (null == primaNeta) {
				primaNeta = new BigDecimal(0);
			}

			// Porcentaje maximo de comision sobre la prima neta que se puede asignar a la
			// entidad y mediador
			BigDecimal comisionMax = pctComsCalculado == null ? ppc.getPctcommax() : pctComsCalculado;
			// Porcentaje (sobre porcentaje maximo de comision) que corresponde a la entidad
			BigDecimal comisionEnt = ppc.getPctentidad();
			// Porcentaje (sobre porcentaje maximo de comision) que corresponde al mediador
			BigDecimal comisionE_S = ppc.getPctesmediadora();

			BigDecimal dtoElegido = null;
			BigDecimal recElegido = null;
			BigDecimal zero = new BigDecimal(0);
			BigDecimal cien = new BigDecimal(100);
			BigDecimal comTotalE = zero;
			BigDecimal comTotalE_S = zero;
			BigDecimal comTotalGn = zero;

			// Descuento aplicado sobre la poliza
			if (ppc.getPctdescelegido() != null)
				dtoElegido = ppc.getPctdescelegido();
			// Recargo aplicado sobre la poliza
			if (ppc.getPctrecarelegido() != null)
				recElegido = ppc.getPctrecarelegido();

			logger.debug("DATOS PARA DESGLOSE COMISIONES: primaComercialNeta: " + primaNeta + " comisionMax: "
					+ comisionMax + " comisionEnt: " + comisionEnt + " comisionE_S: " + comisionE_S + " dtoElegido: "
					+ dtoElegido + " recElegido: " + recElegido);

			// Comision total (sin gastos RGA) = Prima Comercial Neta * (% comisionMax) * (% de comisionMediador que tiene asignada la poliza) * (% de descuento/recargo aplicado en poliza).

			// Calculo comTotal Entidad - Aqui no se aplican ni recargos ni descuentos
			comTotalE = primaNeta.multiply(comisionMax.divide(cien)).multiply(comisionEnt.divide(cien));
			comTotalGn.add(comTotalE);
			// Calculo comTotal Entidad-Subentidad Mediadora - Se aplica el
			// recargo/descuento
			if (dtoElegido != null && dtoElegido.compareTo(zero) != 0) {
				comTotalE_S = primaNeta.multiply(comisionMax.divide(cien)).multiply(
						comisionE_S.divide(cien).multiply(new BigDecimal(1).subtract(dtoElegido.divide(cien))));
			} else if (recElegido != null && recElegido.compareTo(zero) != 0) {
				comTotalE_S = primaNeta.multiply(comisionMax.divide(cien))
						.multiply(comisionE_S.divide(cien).multiply(new BigDecimal(1).add(recElegido.divide(cien))));
			} else {
				comTotalE_S = primaNeta.multiply(comisionMax.divide(cien)).multiply(comisionE_S.divide(cien));
			}
			comTotalGn.add(comTotalE_S);

			if (comTotalE != null)
				param.put("comTotalE", comTotalE.setScale(2, BigDecimal.ROUND_DOWN).toString());// .replace('.', ','));
			if (comTotalE_S != null)
				param.put("comTotalE_S", comTotalE_S.setScale(2, BigDecimal.ROUND_DOWN).toString());// .replace('.',
																									// ','));
			if (comTotalGn != null)
				param.put("comTotalGn", comTotalE_S.setScale(2, BigDecimal.ROUND_DOWN).toString().replace('.', ','));

			logger.debug("DESGLOSE COMS: comTotalE: " + param.get("comTotalE") + " comTotalE_S: "
					+ param.get("comTotalE_S"));
		}

		return param;
	}

	/**
	 * Devuelve la comision del mediador segun el porcentaje de la Entidad y la
	 * correspondiente a la E-S Mediadora, segun el perfil del usuario
	 * 
	 * @param FluxCondensatorObject
	 *            flux, Poliza pol, Usuario usu
	 * @return FluxCondensatorObject
	 */
	public FluxCondensatorObject dameComisiones(FluxCondensatorObject flux, Poliza pol, Usuario usu,
			BigDecimal primaNeta) {
		/*
		 * Dependiendo del perfil del usuario: - Perfil 0 - vera todas las comisiones de
		 * las polizas que procese. - Perfil 1 interno - vera todas las comisiones de
		 * las polizas de su misma entidad. - Perfil 1 externo - vera todas las
		 * comisiones de las polizas de su misma entidad y E-S mediadora - Perfil 5 -
		 * vera todas las comisiones de las polizas que pertenezcan a su grupo de
		 * entidades.
		 */
		String idComparativa = "";
		String codModulo = "";
		
		if (flux != null) {
			codModulo = flux.getIdModulo();
			logger.debug("codModulo -> " + codModulo);
		}
		
		Set<ModuloPoliza> mpLst = pol.getModuloPolizas();
		for (ModuloPoliza mp : mpLst) {
			idComparativa = mp.getId().getNumComparativa().toString();
			break;
		}
		
		PolizaPctComisiones ppc = pol.getPolizaPctComisiones();
		Integer verComisiones = Constants.VER_COMISIONES_NO;
		if (usu.getPerfil().equals(Constants.PERFIL_USUARIO_ADMINISTRADOR)) { // pefil 0
			verComisiones = Constants.VER_COMISIONES_TODAS;
		} else if (!usu.isUsuarioExterno() && usu.getPerfil().equals(Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES)) { 
			// pefil 1 int
			if (usu.getSubentidadMediadora().getEntidad().getCodentidad()
					.compareTo(pol.getColectivo().getTomador().getId().getCodentidad()) == 0)
				verComisiones = Constants.VER_COMISIONES_TODAS;
		} else if (usu.getPerfil().equals(Constants.PERFIL_USUARIO_SEMIADMINISTRADOR)) { // perfil 5
			List<BigDecimal> listEntidadesGrupo = usu.getListaCodEntidadesGrupo();
			if (listEntidadesGrupo.contains(pol.getColectivo().getTomador().getId().getCodentidad()))
				verComisiones = Constants.VER_COMISIONES_TODAS;
		} else {
			try {
				Descuentos descuentos = polizasPctComisionesDao.getDescuentos(
						pol.getColectivo().getTomador().getId().getCodentidad(),
						usu.getOficina().getId().getCodoficina().toString(),
						pol.getColectivo().getSubentidadMediadora().getId().getCodentidad(),
						pol.getColectivo().getSubentidadMediadora().getId().getCodsubentidad(), usu.getDelegacion(),
						pol.getLinea().getCodplan(), pol.getLinea().getCodlinea());
				if (descuentos != null) {
					verComisiones = descuentos.getVerComisiones();
				}
			} catch (Exception e) {
				logger.error("Se ha producido un error en el calculo de comisiones", e);
			}
		}

		// permite ver comisiones
		Map<String, String> param = new HashMap<String, String>();
		BigDecimal pctComsCalculado = null;
		try {
			logger.debug("Obteniendo el % de comision del calculo:");
			logger.debug("idComparativa -> " + idComparativa);
			logger.debug("grupoNegocio -> " + ppc.getGrupoNegocio());
			ComsPctCalculadoId id = new ComsPctCalculadoId(Long.valueOf(idComparativa), ppc.getGrupoNegocio());
			ComsPctCalculado comsPctCalculado;
			comsPctCalculado = (ComsPctCalculado) this.polizasPctComisionesDao.get(ComsPctCalculado.class, id);
			pctComsCalculado = comsPctCalculado == null ? null : comsPctCalculado.getPctCalculado();
		} catch (Exception e1) {
			logger.error("Error al obtener el % de comision del calculo.", e1);
		}
		param = this.obtenerDesgloseComisiones(ppc, primaNeta, pctComsCalculado);
		
		String comTotalE = param.get("comTotalE");
		String comTotalE_S = param.get("comTotalE_S");

		// ESC-25609: Se asigna las comisiones a la distribución de costes de la poliza.
		// Esto se hace porque el usuario puede no ver las comisiones y auqnue no las
		// puede ver se tienen que gauradar
		for (DistribucionCoste2015 distCoste : pol.getDistribucionCoste2015s()) {
			if (distCoste.getGrupoNegocio().toString().equals(ppc.getGrupoNegocio().toString())
					&& codModulo.equals(distCoste.getCodmodulo())
					&& idComparativa.equals(distCoste.getIdcomparativa().toString())) {
				BigDecimal comTotalEBD = new BigDecimal(comTotalE.replace(',', '.'));
				distCoste.setImpComsEntidad(comTotalEBD.setScale(2));

				BigDecimal comTotalE_SBD = new BigDecimal(comTotalE_S.replace(',', '.'));
				distCoste.setImpComsESMed(comTotalE_SBD.setScale(2));
			}
		}
					
		if (verComisiones.compareTo(Constants.VER_COMISIONES_ENTIDAD) == 0
				|| verComisiones.compareTo(Constants.VER_COMISIONES_TODAS) == 0) {
			if (comTotalE != null && !comTotalE.equalsIgnoreCase("")) {
				BigDecimal comTotalEBD = new BigDecimal(comTotalE.replace(',', '.'));
				flux.setComMediadorE(NumberUtils.formatear(comTotalEBD, 2));
			}
		}
		if (verComisiones.compareTo(Constants.VER_COMISIONES_ENTIDAD_MEDIADORA) == 0
				|| verComisiones.compareTo(Constants.VER_COMISIONES_TODAS) == 0) {
			if (comTotalE_S != null && !comTotalE_S.equalsIgnoreCase("")) {
				BigDecimal comTotalE_SBD = new BigDecimal(comTotalE_S.replace(',', '.'));
				flux.setComMediadorE_S(NumberUtils.formatear(comTotalE_SBD, 2));
			}
		}
		
		return flux;
	}

	public VistaImportes dameComisiones(VistaImportes vistaImportes, Poliza pol, Usuario usu) {
		return dameComisiones(vistaImportes, pol, usu, null);
	}

	public VistaImportes dameComisiones(VistaImportes vistaImportes, Poliza pol, Usuario usu, AnexoModificacion am) {
		/*
		 * Dependiendo del perfil del usuario: - Perfil 0 - vera todas las comisiones de
		 * las polizas que procese. - Perfil 1 interno - vera todas las comisiones de
		 * las polizas de su misma entidad. - Perfil 1 externo - vera todas las
		 * comisiones de las polizas de su misma entidad y E-S mediadora - Perfil 5 -
		 * vera todas las comisiones de las polizas que pertenezcan a su grupo de
		 * entidades.
		 */
		String idComparativa = "";
		String codModulo = "";
		
		if (vistaImportes != null) {
			codModulo = vistaImportes.getIdModulo();
			logger.debug("codModulo -> " + codModulo);
			
			if (vistaImportes.getComparativaSeleccionada() != null && !"".equals(vistaImportes.getComparativaSeleccionada())){
				idComparativa = vistaImportes.getComparativaSeleccionada().split("\\|")[0];
			}
		}
		
		Set<PolizaPctComisiones> ppcComisiones = pol.getSetPolizaPctComisiones();
		Integer verComisiones = Constants.VER_COMISIONES_NO;
		if (usu.getPerfil().equals(Constants.PERFIL_USUARIO_ADMINISTRADOR)) { // pefil 0
			verComisiones = Constants.VER_COMISIONES_TODAS;
		} else if (!usu.isUsuarioExterno() && usu.getPerfil().equals(Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES)) { 
			// pefil 1 int
			if (usu.getSubentidadMediadora().getEntidad().getCodentidad()
					.compareTo(pol.getColectivo().getTomador().getId().getCodentidad()) == 0)
				verComisiones = Constants.VER_COMISIONES_TODAS;
		} else if (usu.getPerfil().equals(Constants.PERFIL_USUARIO_SEMIADMINISTRADOR)) { // perfil 5
			List<BigDecimal> listEntidadesGrupo = usu.getListaCodEntidadesGrupo();
			if (listEntidadesGrupo.contains(pol.getColectivo().getTomador().getId().getCodentidad()))
				verComisiones = Constants.VER_COMISIONES_TODAS;
		} else {
			try {
				Descuentos descuentos = polizasPctComisionesDao.getDescuentos(
						pol.getColectivo().getTomador().getId().getCodentidad(),
						usu.getOficina().getId().getCodoficina().toString(),
						pol.getColectivo().getSubentidadMediadora().getId().getCodentidad(),
						pol.getColectivo().getSubentidadMediadora().getId().getCodsubentidad(), usu.getDelegacion(),
						pol.getLinea().getCodplan(), pol.getLinea().getCodlinea());
				if (descuentos != null) {
					verComisiones = descuentos.getVerComisiones();
				}
			} catch (Exception e) {
				logger.error("Se ha producido un error en el calculo de comisiones", e);
			}
		}
		VistaImportesPorGrupoNegocio e = new VistaImportesPorGrupoNegocio();
		e.setCodGrupoNeg("TOTALES");
		vistaImportes.getVistaImportesPorGrupoNegocio().add(e);
		BigDecimal sumES = new BigDecimal(0);
		BigDecimal sumE = new BigDecimal(0);
				
		// permite ver comisiones
		for (PolizaPctComisiones ppc : ppcComisiones) {
			Map<String, String> param = new HashMap<String, String>();
			BigDecimal primaNeta = new BigDecimal(0.0);
			for (VistaImportesPorGrupoNegocio viGn : vistaImportes.getVistaImportesPorGrupoNegocio()) {
				if (viGn.getCodGrupoNeg().equals(ppc.getGrupoNegocio().toString())) {
					if (null != viGn.getPrimaNetaB())
						primaNeta = viGn.getPrimaNetaB();

					break;
				}
			}

			// MPM
			// Si se recibe anexo como parametro se incluyen sus descuentos/recargos
			// aplicados en el calculo del desglose de comisiones
			if (am != null) {
				// Se utiliza un bean de PolizaPctComisiones nuevo para que no se actualice el
				// registro de BBDD correspondiente a 'ppc' en BBDD
				PolizaPctComisiones ppcAux = new PolizaPctComisiones();
				ppcAux.setPctcommax(ppc.getPctcommax());
				ppcAux.setPctentidad(ppc.getPctentidad());
				ppcAux.setPctesmediadora(ppc.getPctesmediadora());

				if (Constants.GRUPO_NEGOCIO_RYD.equals(ppc.getGrupoNegocio())) {
					ppcAux.setPctdescelegido(am.getPctdescelegido());
					ppcAux.setPctrecarelegido(am.getPctrecarelegido());
				} else if (Constants.GRUPO_NEGOCIO_VIDA.equals(ppc.getGrupoNegocio())) {
					ppcAux.setPctdescelegido(am.getPctdescelegidoResto());
					ppcAux.setPctrecarelegido(am.getPctrecarelegidoResto());
				}

				param = this.obtenerDesgloseComisiones(ppcAux, primaNeta, null);
			} else {
				BigDecimal pctComsCalculado = null;
				try {
					logger.debug("Obteniendo el % de comision del calculo:");
					logger.debug("idComparativa -> " + idComparativa);
					logger.debug("grupoNegocio -> " + ppc.getGrupoNegocio());
					ComsPctCalculadoId id = new ComsPctCalculadoId(Long.valueOf(idComparativa), ppc.getGrupoNegocio());
					ComsPctCalculado comsPctCalculado;
					comsPctCalculado = (ComsPctCalculado) this.polizasPctComisionesDao.get(ComsPctCalculado.class, id);
					pctComsCalculado = comsPctCalculado == null ? null : comsPctCalculado.getPctCalculado();
				} catch (Exception e1) {
					logger.error("Error al obtener el % de comision del calculo.", e1);
				}
				param = this.obtenerDesgloseComisiones(ppc, primaNeta, pctComsCalculado);
			}

			String comTotalE = param.get("comTotalE");
			String comTotalE_S = param.get("comTotalE_S");
			String comTotalGn = param.get("comTotalGn");

			// ESC-25609: Se asigna las comisiones a la distribución de costes de la poliza.
			// Esto se hace porque el usuario puede no ver las comisiones y auqnue no las
			// puede ver se tienen que gauradar
			for (DistribucionCoste2015 distCoste : pol.getDistribucionCoste2015s()) {
				if (distCoste.getGrupoNegocio().toString().equals(ppc.getGrupoNegocio().toString())
						&& codModulo.equals(distCoste.getCodmodulo())
						&& idComparativa.equals(distCoste.getIdcomparativa().toString())) {
					BigDecimal comTotalEBD = new BigDecimal(comTotalE.replace(',', '.'));
					distCoste.setImpComsEntidad(comTotalEBD.setScale(2));

					BigDecimal comTotalE_SBD = new BigDecimal(comTotalE_S.replace(',', '.'));
					distCoste.setImpComsESMed(comTotalE_SBD.setScale(2));
				}
			}

			// En función del usuario rellenamos las comisiones que puede ver
			if (null != vistaImportes.getVistaImportesPorGrupoNegocio()
					&& vistaImportes.getVistaImportesPorGrupoNegocio().size() > 0) {
				for (VistaImportesPorGrupoNegocio vign : vistaImportes.getVistaImportesPorGrupoNegocio()) {
					if (vign.getCodGrupoNeg().equals(ppc.getGrupoNegocio().toString())) {
						if (verComisiones.compareTo(Constants.VER_COMISIONES_ENTIDAD) == 0
								|| verComisiones.compareTo(Constants.VER_COMISIONES_TODAS) == 0) {
							if (comTotalE != null && !comTotalE.equalsIgnoreCase("")) {
								sumE = sumE.add(new BigDecimal(comTotalE));
								BigDecimal comTotalEBD = new BigDecimal(comTotalE.replace(',', '.'));
								vign.setComMediadorE(NumberUtils.formatear(comTotalEBD, 2));
							}

						}
						if (verComisiones.compareTo(Constants.VER_COMISIONES_ENTIDAD_MEDIADORA) == 0
								|| verComisiones.compareTo(Constants.VER_COMISIONES_TODAS) == 0) {
							if (comTotalE_S != null && !comTotalE_S.equalsIgnoreCase("")) {
								sumES = sumES.add(new BigDecimal(comTotalE_S));
								BigDecimal comTotalE_SBD = new BigDecimal(comTotalE_S.replace(',', '.'));
								vign.setComMediadorE_S(NumberUtils.formatear(comTotalE_SBD, 2));

							}
						}
						if ((verComisiones.compareTo(Constants.VER_COMISIONES_ENTIDAD) == 0
								|| verComisiones.compareTo(Constants.VER_COMISIONES_TODAS) == 0)
								|| (verComisiones.compareTo(Constants.VER_COMISIONES_ENTIDAD_MEDIADORA) == 0
										|| verComisiones.compareTo(Constants.VER_COMISIONES_TODAS) == 0)) {
							if (comTotalGn != null && !comTotalGn.equalsIgnoreCase("")) {
								BigDecimal comTotalGnBD = new BigDecimal(comTotalGn.replace(',', '.'));
								vign.setTotalComisiones(NumberUtils.formatear(comTotalGnBD, 2));
							}
						}
					}
				}

			}

		}
		
		//TOTALES
		for (VistaImportesPorGrupoNegocio vign2 : vistaImportes.getVistaImportesPorGrupoNegocio()) {
			if (vign2.getCodGrupoNeg().equals("TOTALES")) {
				if (verComisiones.compareTo(Constants.VER_COMISIONES_ENTIDAD_MEDIADORA) == 0
						|| verComisiones.compareTo(Constants.VER_COMISIONES_TODAS) == 0) {
					vign2.setTotalMediadorE_S(NumberUtils.formatear(sumES, 2));
				}
				if (verComisiones.compareTo(Constants.VER_COMISIONES_ENTIDAD) == 0
						|| verComisiones.compareTo(Constants.VER_COMISIONES_TODAS) == 0) {
					vign2.setTotalMediadorE(NumberUtils.formatear(sumE, 2));
				}
			}
		}
		
		return vistaImportes;
	}

	/**
	 * Escribe en el log indicando la clase y el metodo.
	 * 
	 * @param method
	 * @param msg
	 */
	private void log(String method, String msg) {
		logger.debug("PolizasPctComisionesManager." + method + " - " + msg);
	}

	public void setPolizasPctComisionesDao(IPolizasPctComisionesDao polizasPctComisionesDao) {
		this.polizasPctComisionesDao = polizasPctComisionesDao;
	}
}