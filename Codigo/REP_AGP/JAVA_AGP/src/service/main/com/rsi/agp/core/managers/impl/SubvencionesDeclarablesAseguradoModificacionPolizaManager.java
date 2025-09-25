package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.ValidacionAnexoModificacionException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.managers.impl.anexoMod.solicitud.ISolicitudModificacionManager;
import com.rsi.agp.core.managers.impl.poliza.util.PolizaUtils;
import com.rsi.agp.core.report.anexoMod.RelacionEtiquetaTabla;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.XmlTransformerUtil;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.cpl.SubvencionesGrupoFiltro;
import com.rsi.agp.dao.filters.poliza.SubvencionCCAAGanPolizaFiltro;
import com.rsi.agp.dao.filters.poliza.SubvencionCCAAPolizaFiltro;
import com.rsi.agp.dao.filters.poliza.SubvencionEnesaGanPolizaFiltro;
import com.rsi.agp.dao.filters.poliza.SubvencionEnesaPolizaFiltro;
import com.rsi.agp.dao.models.admin.IAseguradoDao;
import com.rsi.agp.dao.models.anexo.ISubvencionesDeclarablesModificacionPolizaDao;
import com.rsi.agp.dao.models.anexo.IXmlAnexoModificacionDao;
import com.rsi.agp.dao.models.config.IDiccionarioDatosDao;
import com.rsi.agp.dao.models.copy.IPolizaCopyDao;
import com.rsi.agp.dao.models.cpm.ICPMTipoCapitalDAO;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Socio;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.SubvDeclarada;
import com.rsi.agp.dao.tables.cgen.SubvencionesAseguradosView;
import com.rsi.agp.dao.tables.cpl.SubvencionCCAA;
import com.rsi.agp.dao.tables.cpl.SubvencionEnesa;
import com.rsi.agp.dao.tables.cpl.SubvencionesGrupo;
import com.rsi.agp.dao.tables.cpl.gan.SubvencionCCAAGanado;
import com.rsi.agp.dao.tables.cpl.gan.SubvencionEnesaGanado;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.SubvencionSocio;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;

public class SubvencionesDeclarablesAseguradoModificacionPolizaManager
		implements IManager {

	private final Log logger = LogFactory.getLog(SubvencionesDeclarablesAseguradoModificacionPolizaManager.class);

	private ISubvencionesDeclarablesModificacionPolizaDao subvencionesDeclarablesModificacionPolizaDao;
	private IPolizaCopyDao polizaCopyDao;
	private IXmlAnexoModificacionDao xmlAnexoModDao;
	private IAseguradoDao aseguradoDao;
	private ICPMTipoCapitalDAO cpmTipoCapitalDao;

	private IDiccionarioDatosDao diccionarioDatosDao;
	private ISolicitudModificacionManager solicitudModificacionManager;

	public void setSubvencionesDeclarablesModificacionPolizaDao(
			ISubvencionesDeclarablesModificacionPolizaDao subvencionesDeclarablesModificacionPolizaDao) {
		this.subvencionesDeclarablesModificacionPolizaDao = subvencionesDeclarablesModificacionPolizaDao;
	}

	/**
	 * Metodo para obtener todas las posibles subvenciones del asegurado de un
	 * anexo de modificacion. Se marcaran aquellas que tuviera el asegurado en
	 * la copy.
	 * 
	 * @param anexo
	 * @return
	 * @throws XmlException 
	 */
	public List<SubvencionesAseguradosView> getSubvencionesAsegurado(
			final AnexoModificacion anexo, final BigDecimal perfilUsuario) throws XmlException {
		
		boolean isLineaGanado = anexo.getPoliza().getLinea().isLineaGanado();
		// Obtengo la lista de parcelas a utilizar para calcular las
		// subvenciones
		Set<Parcela> parcelas = null;
		Set<Explotacion> explotaciones = null;
		if (anexo.getCupon() != null && anexo.getCupon().getId() != null) {
			if (isLineaGanado) {
				// Obtengo las parcelas de la situacion actualizada de
				// agroseguro
				// Obtengo la poliza actualizada
				es.agroseguro.contratacion.Poliza poliza = ((es.agroseguro.contratacion.PolizaDocument) this.solicitudModificacionManager
						.getPolizaActualizadaFromCupon(anexo.getCupon().getIdcupon())).getPoliza();
				// Mapa auxiliar con los codigos de concepto de los datos
				// variables
				// y sus etiquetas y tablas asociadas.
				Map<BigDecimal, RelacionEtiquetaTabla> auxEtiquetaTabla = this.diccionarioDatosDao
						.getCodConceptoEtiquetaTablaExplotaciones(anexo
								.getPoliza().getLinea().getLineaseguroid());
				explotaciones = new HashSet<Explotacion>(
						PolizaUtils
								.getExplotacionesPolizaFromPolizaActualizada(
										poliza,
										anexo.getPoliza().getIdpoliza(),
										auxEtiquetaTabla));
							
					es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada[] subvsDeclaradasArr = poliza
							.getSubvencionesDeclaradas() != null ? poliza
							.getSubvencionesDeclaradas()
							.getSubvencionDeclaradaArray()
							 		: new es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada[] {};
				
				boolean existeSubvencionDeclarada = false;
				try {
					 existeSubvencionDeclarada = subvencionesDeclarablesModificacionPolizaDao.getsubvDeclarada(anexo.getId());
				
					if(!existeSubvencionDeclarada) {
						for (es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada subv : subvsDeclaradasArr) {
							SubvDeclarada subvDeclarada = new SubvDeclarada();
							subvDeclarada.setAnexoModificacion(anexo);
							subvDeclarada.setCodsubvencion(new BigDecimal(subv.getTipo()));
							//insertamos las subvenciones declaradas que tengamos en el xml
							subvencionesDeclarablesModificacionPolizaDao.insertamosSubvencionesDeclaradasAnexo(subvDeclarada);
						}
					}
				} catch (DAOException e1) {
					logger.error(
							"Excepcion : SubvencionesDeclarablesAseguradoModificacionPolizaManager - getSubvencionesAsegurado",
							e1);
				}
			} else {
				
				/* Pet. 57626 ** MODIF TAM (11.06.2020) ** Inicio */
				/* Por los desarrollos de esta peticion tanto las polizas agricolas como las de ganado
				 * iran por el mismo end-point y con formato Unificado
				 */
				// Obtengo las parcelas de la situacion actualizada de
				// agroseguro
				// Obtengo la poliza actualizada
				
				XmlObject polizaDoc = this.solicitudModificacionManager.getPolizaActualizadaFromCupon(anexo.getCupon().getIdcupon());

				/* Anexos de modificacion de contratacion con Formato Unificado (Nuevas) */
				if (polizaDoc instanceof es.agroseguro.contratacion.PolizaDocument) {
					
					es.agroseguro.contratacion.Poliza poliza = ((es.agroseguro.contratacion.PolizaDocument) polizaDoc).getPoliza();
					
					es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada[] subvsDeclaradasArr = poliza
							.getSubvencionesDeclaradas() != null ? poliza
							.getSubvencionesDeclaradas()
							.getSubvencionDeclaradaArray()
							 		: new es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada[] {};

					/*es.agroseguro.seguroAgrario.contratacion.SubvencionDeclarada[] subvsDeclaradasArr = poliza
							.getSubvencionesDeclaradas() != null ? poliza
							.getSubvencionesDeclaradas()
							.getSubvencionDeclaradaArray()
					
							 		: new es.agroseguro.seguroAgrario.contratacion.SubvencionDeclarada[] {};*/
					boolean existeSubvencionDeclarada = false;
					try {
						 existeSubvencionDeclarada = subvencionesDeclarablesModificacionPolizaDao.getsubvDeclarada(anexo.getId());
					
						if(!existeSubvencionDeclarada) {
							for (es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada subv : subvsDeclaradasArr) {
								SubvDeclarada subvDeclarada = new SubvDeclarada();
								subvDeclarada.setAnexoModificacion(anexo);
								subvDeclarada.setCodsubvencion(new BigDecimal(subv.getTipo()));
								//insertamos las subvenciones declaradas que tengamos en el xml
								subvencionesDeclarablesModificacionPolizaDao.insertamosSubvencionesDeclaradasAnexo(subvDeclarada);
							}
						}
					} catch (DAOException e1) {
						logger.error(
								"Excepcion : SubvencionesDeclarablesAseguradoModificacionPolizaManager - getSubvencionesAsegurado",
								e1);
					}
					
					// Mapa auxiliar con los codigos de concepto de los datos
					// variables
					// y sus etiquetas y tablas asociadas.
					Map<BigDecimal, RelacionEtiquetaTabla> auxEtiquetaTabla = this.diccionarioDatosDao
							.getCodConceptoEtiquetaTablaParcelas(anexo.getPoliza()
									.getLinea().getLineaseguroid());
					
					parcelas = new HashSet<Parcela>(
							PolizaUtils.getParcelasPolizaFromPolizaActualizada(
									poliza, anexo.getPoliza().getIdpoliza(),
									auxEtiquetaTabla));
					
				/* Anexos de modificacion de contratacion con Formato Unificado (Nuevas) */
				}else {
					
					es.agroseguro.seguroAgrario.contratacion.Poliza poliza = ((es.agroseguro.seguroAgrario.contratacion.PolizaDocument) polizaDoc).getPoliza();
					
					es.agroseguro.seguroAgrario.contratacion.SubvencionDeclarada[] subvsDeclaradasArr = poliza
							.getSubvencionesDeclaradas() != null ? poliza
							.getSubvencionesDeclaradas()
							.getSubvencionDeclaradaArray()
							: new es.agroseguro.seguroAgrario.contratacion.SubvencionDeclarada[] {};
					boolean existeSubvencionDeclarada = false;
					try {
						 existeSubvencionDeclarada = subvencionesDeclarablesModificacionPolizaDao.getsubvDeclarada(anexo.getId());
					
						if(!existeSubvencionDeclarada) {
							for (es.agroseguro.seguroAgrario.contratacion.SubvencionDeclarada subv : subvsDeclaradasArr) {
								SubvDeclarada subvDeclarada = new SubvDeclarada();
								subvDeclarada.setAnexoModificacion(anexo);
								subvDeclarada.setCodsubvencion(new BigDecimal(subv.getTipo()));
								//insertamos las subvenciones declaradas que tengamos en el xml
								subvencionesDeclarablesModificacionPolizaDao.insertamosSubvencionesDeclaradasAnexo(subvDeclarada);
							}
						}
					} catch (DAOException e1) {
						logger.error(
								"Excepcion : SubvencionesDeclarablesAseguradoModificacionPolizaManager - getSubvencionesAsegurado",
								e1);
					}
					
					// Mapa auxiliar con los codigos de concepto de los datos
					// variables
					// y sus etiquetas y tablas asociadas.
					Map<BigDecimal, RelacionEtiquetaTabla> auxEtiquetaTabla = this.diccionarioDatosDao
							.getCodConceptoEtiquetaTablaParcelas(anexo.getPoliza()
									.getLinea().getLineaseguroid());
					parcelas = new HashSet<Parcela>(
							PolizaUtils.getParcelasPolizaFromPolizaActualizadaAnt(
									poliza, anexo.getPoliza().getIdpoliza(),
									auxEtiquetaTabla));
					
				}
			}
		} else {
			if (isLineaGanado) {
				// Obtengo las parcelas de la poliza
				explotaciones = anexo.getPoliza().getExplotacions();
			} else {
				// Obtengo las parcelas de la poliza
				parcelas = anexo.getPoliza().getParcelas();
			}
		}

		// obtenemos todas las subvenciones enesa y de la comunidad
		List<Object> subvsEnesa = getSubvencionesEnesa(anexo);

		// DAA 15/02/2013 De la lista de subvsEnesa sacamos los ids de los tipos
		// para no incuirlos en las de la comunidad
		List<BigDecimal> listaCodTipos = AseguradoSubvencionManager
				.getListaCodTipoSubvsEnesa(subvsEnesa);

		List<Object> subvsCCAA = getSubvencionesCCAA(anexo, parcelas,
				explotaciones, listaCodTipos);

		List<Object> subvsEnesaEliminar = new ArrayList<Object>();
		List<Object> subvsCCAAEliminar = new ArrayList<Object>();

		// Comprobar que no se repiten subvenciones dentro de cada tipo
		List<Object> subvsEnesaSinRep = limpiaSubvEnesaRepetidas(subvsEnesa,
				isLineaGanado);
		List<Object> subvsCCAASinRep = limpiaSubvCCAARepetidas(subvsCCAA,
				isLineaGanado);

		// hacemos la mezcla para eliminar las que estan repetidas. Si una
		// subvencion la podemos obtener por enesa y por
		// comunidad autonoma, nos quedaremos con aquella que tenga mayor
		// porcentaje de cobertura.
		List<SubvencionesAseguradosView> resultado = new ArrayList<SubvencionesAseguradosView>();

		Asegurado asegurado = anexo.getPoliza().getAsegurado();

		// recorremos la lista de subvenciones enesa y comprobamos que no estan
		// en la lista de subvenciones de la comunidad
		logger.debug("recorremos la lista de subvenciones enesa y comprobamos que no estan en la lista de subvenciones de la comunidad");
		for (Object subvEnesa : subvsEnesaSinRep) {
			BigDecimal codTipoSubvEnesa;
			BigDecimal pctSubvIndEnesa;
			if (isLineaGanado) {
				codTipoSubvEnesa = ((SubvencionEnesaGanado) subvEnesa)
						.getTipoSubvencionEnesa().getCodtiposubvenesa();
				pctSubvIndEnesa = ((SubvencionEnesaGanado) subvEnesa)
						.getPorcSubvSeguroInd();
			} else {
				codTipoSubvEnesa = ((SubvencionEnesa) subvEnesa)
						.getTipoSubvencionEnesa().getCodtiposubvenesa();
				pctSubvIndEnesa = ((SubvencionEnesa) subvEnesa)
						.getPctsubvindividual();
			}
			for (Object subvCCAA : subvsCCAASinRep) {
				BigDecimal codTipoSubvCCAA;
				BigDecimal pctSubvIndCCAA;
				if (isLineaGanado) {
					codTipoSubvCCAA = ((SubvencionCCAAGanado) subvCCAA)
							.getTipoSubvencionCCAA().getCodtiposubvccaa();
					pctSubvIndCCAA = ((SubvencionCCAAGanado) subvCCAA)
							.getPorcSubvSeguroInd();
				} else {
					codTipoSubvCCAA = ((SubvencionCCAA) subvCCAA)
							.getTipoSubvencionCCAA().getCodtiposubvccaa();
					pctSubvIndCCAA = ((SubvencionCCAA) subvCCAA)
							.getPctsubvindividual();
				}
				if (codTipoSubvEnesa.compareTo(codTipoSubvCCAA) == 0) {
					// Si existe en las dos listas, miramos con cual no tenemos
					// que quedar
					if (pctSubvIndEnesa.compareTo(pctSubvIndCCAA) >= 0) {
						// la buena es ENESA
						subvsCCAAEliminar.add(subvCCAA);
					} else {
						// la buena es CCAA
						subvsEnesaEliminar.add(subvEnesa);
					}
					break;
				}
			}
		}

		logger.debug("Eliminamos subvenciones enesa o ccaa, si estan repetidas, y nos quedamos la de mayor porcentaje");
		subvsCCAASinRep.removeAll(subvsCCAAEliminar);
		subvsEnesaSinRep.removeAll(subvsEnesaEliminar);
		boolean marcadaEnesa = false;
		boolean marcadaCCAA = false;
		// Marcamos las subvenciones ENESA que tuviera el asegurado en la copy o
		// en la poliza
		logger.debug("Marcamos las subvenciones ENESA que tuviera el asegurado en la copy o en la poliza");
		for (Object subvEnesa : subvsEnesaSinRep) {
			BigDecimal codTipoSubvEnesa;
			if (isLineaGanado) {
				codTipoSubvEnesa = ((SubvencionEnesaGanado) subvEnesa)
						.getTipoSubvencionEnesa().getCodtiposubvenesa();
			} else {
				codTipoSubvEnesa = ((SubvencionEnesa) subvEnesa)
						.getTipoSubvencionEnesa().getCodtiposubvenesa();
			}
			SubvencionesAseguradosView sav = new SubvencionesAseguradosView();
			sav.setLineaGanado(isLineaGanado);
			if (anexo.getSubvDeclaradas() != null
					&& !anexo.getSubvDeclaradas().isEmpty()) {
				// marcamos las subvenciones que estuvieran en la copy con
				// tipomodificacion a null o 'ALTA'
				for (SubvDeclarada sd : anexo.getSubvDeclaradas()) {
					if (sd.getCodsubvencion().equals(codTipoSubvEnesa)
							&& (sd.getTipomodificacion() == null || sd
									.getTipomodificacion().equals(
											new Character('A')))) {
						sav.setMarcada(true);
						marcadaEnesa = true;
						break;
					}
				}
			}
			if (codTipoSubvEnesa.equals(Constants.SUBVENCION20)) {
				if (asegurado.getNumsegsocial() != null) {
					sav.setMarcada(!subvBajaEnAnexo(anexo.getSubvDeclaradas(), codTipoSubvEnesa));
				} else {
					sav.setMarcada(false);
					sav.setNoEdit(true);
				}
			} else if (asegurado.getJovenagricultor() != null
					&& asegurado.getJovenagricultor()
							.equals(new Character('H'))
					&& codTipoSubvEnesa
							.equals(Constants.SUBVENCION_JOVEN_HOMBRE)) {
				sav.setMarcada(!subvBajaEnAnexo(anexo.getSubvDeclaradas(), codTipoSubvEnesa));
			} else if (asegurado.getJovenagricultor() != null
					&& asegurado.getJovenagricultor()
							.equals(new Character('H'))
					&& codTipoSubvEnesa
							.equals(Constants.SUBVENCION_JOVEN_MUJER)) {
				sav.setNoEdit(true);
			} else if (asegurado.getJovenagricultor() != null
					&& asegurado.getJovenagricultor()
							.equals(new Character('M'))
					&& codTipoSubvEnesa
							.equals(Constants.SUBVENCION_JOVEN_MUJER)) {
				sav.setMarcada(!subvBajaEnAnexo(anexo.getSubvDeclaradas(), codTipoSubvEnesa));
			} else if (asegurado.getJovenagricultor() != null
					&& asegurado.getJovenagricultor()
							.equals(new Character('M'))
					&& codTipoSubvEnesa
							.equals(Constants.SUBVENCION_JOVEN_HOMBRE)) {
				sav.setNoEdit(true);
			} else if (asegurado.getJovenagricultor() == null
					&& (codTipoSubvEnesa
							.equals(Constants.SUBVENCION_JOVEN_MUJER) || codTipoSubvEnesa
							.equals(Constants.SUBVENCION_JOVEN_HOMBRE))) {
				sav.setNoEdit(true);
			} else if (codTipoSubvEnesa
					.equals(Constants.SUBVENCION_ENTIDADES_ASOCIATIVAS)
					&& !perfilUsuario.equals(Constants.PERFIL_0)
					&& !perfilUsuario.equals(Constants.PERFIL_1)
					&& !perfilUsuario.equals(Constants.PERFIL_5)) {
				sav.setNoEdit(true);
				sav.setMarcada(false);
			} else {
				if (!marcadaEnesa) {
					sav.setMarcada(false);
				}
			}
			sav.setSubvEnesa(subvEnesa);
			sav.setTipoSubvencion("E");
			sav.setCodtiposubvencion(codTipoSubvEnesa);
			resultado.add(sav);
		}
		// Marcamos las subvenciones CCAA que tuviera el asegurado en la copy o
		// en la poliza
		logger.debug("Marcamos las subvenciones CCAA que tuviera el asegurado en la copy o en la poliza");
		for (Object subvCCAA : subvsCCAASinRep) {
			BigDecimal codTipoSubvCCAA;
			if (isLineaGanado) {
				codTipoSubvCCAA = ((SubvencionCCAAGanado) subvCCAA)
						.getTipoSubvencionCCAA().getCodtiposubvccaa();
			} else {
				codTipoSubvCCAA = ((SubvencionCCAA) subvCCAA)
						.getTipoSubvencionCCAA().getCodtiposubvccaa();
			}
			SubvencionesAseguradosView sav = new SubvencionesAseguradosView();
			sav.setLineaGanado(isLineaGanado);
			if (anexo.getSubvDeclaradas() != null
					&& !anexo.getSubvDeclaradas().isEmpty()) {
				// marcamos las subvenciones que estuvieran en la copy
				for (SubvDeclarada sd : anexo.getSubvDeclaradas()) {
					if (sd.getCodsubvencion().equals(codTipoSubvCCAA)
							&& (sd.getTipomodificacion() == null || sd
									.getTipomodificacion().equals(
											new Character('A')))) {
						sav.setMarcada(true);
						marcadaCCAA = true;
						break;
					}
				}
			}
			if (asegurado.getNumsegsocial() != null
					&& codTipoSubvCCAA.equals(Constants.SUBVENCION20)) {
				sav.setMarcada(!subvBajaEnAnexo(anexo.getSubvDeclaradas(), codTipoSubvCCAA));
			} else if (asegurado.getJovenagricultor() != null
					&& asegurado.getJovenagricultor()
							.equals(new Character('H'))
					&& codTipoSubvCCAA
							.equals(Constants.SUBVENCION_JOVEN_HOMBRE)) {
				sav.setMarcada(!subvBajaEnAnexo(anexo.getSubvDeclaradas(), codTipoSubvCCAA));
			} else if (asegurado.getJovenagricultor() != null
					&& asegurado.getJovenagricultor()
							.equals(new Character('H'))
					&& codTipoSubvCCAA.equals(Constants.SUBVENCION_JOVEN_MUJER)) {
				sav.setNoEdit(true);
			} else if (asegurado.getJovenagricultor() != null
					&& asegurado.getJovenagricultor()
							.equals(new Character('M'))
					&& codTipoSubvCCAA.equals(Constants.SUBVENCION_JOVEN_MUJER)) {
				sav.setMarcada(!subvBajaEnAnexo(anexo.getSubvDeclaradas(), codTipoSubvCCAA));
			} else if (asegurado.getJovenagricultor() != null
					&& asegurado.getJovenagricultor()
							.equals(new Character('M'))
					&& codTipoSubvCCAA
							.equals(Constants.SUBVENCION_JOVEN_HOMBRE)) {
				sav.setNoEdit(true);
			} else if (asegurado.getJovenagricultor() == null
					&& (codTipoSubvCCAA
							.equals(Constants.SUBVENCION_JOVEN_MUJER) || codTipoSubvCCAA
							.equals(Constants.SUBVENCION_JOVEN_HOMBRE))) {
				sav.setNoEdit(true);
			} else if (codTipoSubvCCAA
					.equals(Constants.SUBVENCION_ENTIDADES_ASOCIATIVAS)
					&& !perfilUsuario.equals(Constants.PERFIL_0)
					&& !perfilUsuario.equals(Constants.PERFIL_1)
					&& !perfilUsuario.equals(Constants.PERFIL_5)) {
				sav.setNoEdit(true);
				sav.setMarcada(false);
			} else {
				if (!marcadaCCAA) {
					sav.setMarcada(false);
				}
			}
			sav.setSubvCCAA(subvCCAA);
			sav.setTipoSubvencion("C");
			sav.setCodtiposubvencion(codTipoSubvCCAA);
			resultado.add(sav);
		}

		// INICIO DE LA ORDENACION DE LAS SUBVENCIONES
		logger.debug("INICIO DE LA ORDENACION DE LAS SUBVENCIONES");
		SubvencionesAseguradosView[] array = new SubvencionesAseguradosView[resultado
				.size()];
		for (int i = 0; i < resultado.size(); i++) {
			array[i] = resultado.get(i);
		}

		// Ordenamos la lista de subvenciones por codigo de tipo de subvencion
		// ascendente
		Arrays.sort(array, new Comparator<SubvencionesAseguradosView>() {
			public int compare(SubvencionesAseguradosView o1,
					SubvencionesAseguradosView o2) {
				return o1.getCodtiposubvencion().compareTo(
						o2.getCodtiposubvencion());
			}
		});

		List<SubvencionesAseguradosView> resultadoOrdenado = new ArrayList<SubvencionesAseguradosView>();
		for (SubvencionesAseguradosView sav : array) {
			resultadoOrdenado.add(sav);
		}
		logger.debug("FIN DE LA ORDENACION DE LAS SUBVENCIONES");
		// FIN DE LA ORDENACION DE LAS SUBVENCIONES
		
		if (anexo.getPoliza().getAsegurado().getTipoidentificacion()
				.equals("CIF")) {
			logger.info("SubvencionesDeclarablesAseguradoModificacionPolizaManager - 400");
			// Una vez obtenidas todas las subvenciones, obtenemos de
			// 'Subv_Grupos' las subvenciones para el grupo 1
			// y eliminamos de la lista 'subvencionesEnesaPantalla' las que SI
			// estan en el resultado de la consulta.
			filtraSubvencionesGrupo(anexo, resultadoOrdenado);
			logger.info("SubvencionesDeclarablesAseguradoModificacionPolizaManager - 406");
		}

		try {
			logger.debug("Asignamos los grupos a las subvenciones que tenemos");
			getGruposSubv(resultadoOrdenado, anexo.getPoliza());
			logger.debug("terminada la asociacion de los grupos devolvemos el listado");
		} catch (BusinessException e) {
			// throw new BusinessException(e);
		}
		logger.debug("end - getSubvencionesAsegurado");
		return resultadoOrdenado;
	}
	
	private boolean subvBajaEnAnexo(final Set<SubvDeclarada> subvsAnexo,
			final BigDecimal codTipoSubv) {
		if (subvsAnexo != null) {
			for (SubvDeclarada sd : subvsAnexo) {
				if (sd != null && codTipoSubv.equals(sd.getCodsubvencion())
						&& new Character('B').equals(sd.getTipomodificacion())) {
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}

	@SuppressWarnings("unchecked")
	private List<Object> getSubvencionesEnesa(final AnexoModificacion anexo) {
		List<Object> subvenciones;
		Filter filtro;
		// Obtenemos las subvenciones
		String codmodulo = "";
		if (!StringUtils.nullToString(anexo.getCodmodulo()).equals("")) {
			codmodulo += anexo.getCodmodulo() + ";";
		} else {
			codmodulo += anexo.getPoliza().getCodmodulo() + ";";
		}
		codmodulo += "99999;";
		if (anexo.getPoliza().getLinea().isLineaGanado()) {
			filtro = new SubvencionEnesaGanPolizaFiltro(anexo.getPoliza().getLinea().getLineaseguroid(), "1",
					anexo.getPoliza().getAsegurado().getTipoidentificacion(), codmodulo, true,
					anexo.getCodigosTipoAnimalExplotaciones(), null);
		} else {
			filtro = new SubvencionEnesaPolizaFiltro(anexo.getPoliza().getLinea().getLineaseguroid(), "1",
					anexo.getPoliza().getAsegurado().getTipoidentificacion(), codmodulo, true, null);
		}
		subvenciones = (List<Object>) this.subvencionesDeclarablesModificacionPolizaDao
				.getObjects(filtro);
		return subvenciones;
	}

	@SuppressWarnings("unchecked")
	private List<Object> getSubvencionesCCAA(final AnexoModificacion anexo,
			final Set<Parcela> parcelas, final Set<Explotacion> explotaciones,
			final List<BigDecimal> listaCodTipos) {
		List<Object> subvenciones;
		Filter filtro;
		// Obtenemos las subvenciones
		String codmodulo = "";
		if (!StringUtils.nullToString(anexo.getCodmodulo()).equals("")) {
			codmodulo += anexo.getCodmodulo() + ";";
		} else {
			codmodulo += anexo.getPoliza().getCodmodulo() + ";";
		}
		codmodulo += "99999;";
		if (anexo.getPoliza().getLinea().isLineaGanado()) {
			filtro = new SubvencionCCAAGanPolizaFiltro(anexo.getPoliza()
					.getLinea().getLineaseguroid(), "1", anexo.getPoliza()
					.getAsegurado().getTipoidentificacion(), codmodulo,
					explotaciones, null, listaCodTipos, anexo.getCodigosTipoAnimalExplotaciones(), new Date());
		} else {
			filtro = new SubvencionCCAAPolizaFiltro(anexo.getPoliza()
					.getLinea().getLineaseguroid(), "1", anexo.getPoliza()
					.getAsegurado().getTipoidentificacion(), codmodulo,
					parcelas, null, listaCodTipos, new Date());
		}
		subvenciones = (List<Object>) this.subvencionesDeclarablesModificacionPolizaDao
				.getObjects(filtro);
		return subvenciones;
	}

	/**
	 * Metodo con el que asignamos a nuestra Vista de Subv los grupos a los que
	 * pertenece cada subvencion
	 * 
	 * @param resultadoOrdenado
	 * @param poliza
	 * @throws BusinessException
	 */
	private void getGruposSubv(
			List<SubvencionesAseguradosView> resultadoOrdenado, Poliza poliza)
			throws BusinessException {
		try {
			// Listado de grupos de subv para mi plan
			logger.debug("poliza codplan: " + poliza.getLinea().getCodplan());
			List<SubvencionesGrupo> subvgrupo = aseguradoDao
					.getGruposSubv(poliza.getLinea().getCodplan());
			logger.debug("subv grupo size : " + subvgrupo.size());
			// Asigno los grupos a mi listado de subv final
			for (SubvencionesAseguradosView vista : resultadoOrdenado) {
				for (SubvencionesGrupo grupo : subvgrupo) {
					logger.debug("vista id :" + vista.getCodtiposubvencion());
					logger.debug("grupo id subv : "
							+ grupo.getId().getCodtiposubv());
					logger.debug("descripcion : "
							+ grupo.getGrupoSubvenciones().getDescripcion());
					logger.debug("grupo : " + grupo.getId().getGruposubv());
					if (vista.getCodtiposubvencion().equals(
							grupo.getId().getCodtiposubv())) {
						vista.setCodgruposubvencion(grupo.getId()
								.getGruposubv());
						vista.setDescgrupo(grupo.getGrupoSubvenciones()
								.getDescripcion());
						break;
					}
				}
			}

		} catch (DAOException e) {
			logger.error("Se ha producido un error al recueprar los grupos:"
					+ e.getMessage());
			throw new BusinessException(
					"Se ha producido un error al recueprar los grupos", e);
		}
	}

	/**
	 * Elimina las subvenciones repetidas. Se comprueba unicamente el
	 * codtiposubvencionccaa
	 * 
	 * @param subvsCCAA
	 *            Lista de subvenciones a limpiar
	 * @return Lista de subvenciones sin elementos repetidos
	 */
	private List<Object> limpiaSubvCCAARepetidas(final List<Object> subvsCCAA,
			final boolean isLineaGanado) {
		BigDecimal codTipo;
		List<BigDecimal> codSubvsCCAA = new ArrayList<BigDecimal>(
				subvsCCAA.size());
		List<Object> subvsCCAASinRep = new ArrayList<Object>(subvsCCAA.size());
		for (Object subv : subvsCCAA) {
			if (isLineaGanado) {
				codTipo = ((SubvencionCCAAGanado) subv).getTipoSubvencionCCAA()
						.getCodtiposubvccaa();
			} else {
				codTipo = ((SubvencionCCAA) subv).getTipoSubvencionCCAA()
						.getCodtiposubvccaa();
			}
			if (!codSubvsCCAA.contains(codTipo)) {
				subvsCCAASinRep.add(subv);
			}
			codSubvsCCAA.add(codTipo);
		}
		return subvsCCAASinRep;
	}

	/**
	 * Elimina las subvenciones repetidas. Se comprueba unicamente el
	 * codtiposubvencionenesa
	 * 
	 * @param subvsEnesa
	 *            Lista de subvenciones a limpiar
	 * @return Lista de subvenciones sin elementos repetidos
	 */
	private List<Object> limpiaSubvEnesaRepetidas(
			final List<Object> subvsEnesa, final boolean isLineaGanado) {
		BigDecimal codTipo;
		List<BigDecimal> codSubvsEnesa = new ArrayList<BigDecimal>(
				subvsEnesa.size());
		List<Object> subvsEnesaSinRep = new ArrayList<Object>(subvsEnesa.size());
		for (Object subv : subvsEnesa) {
			if (isLineaGanado) {
				codTipo = ((SubvencionEnesaGanado) subv)
						.getTipoSubvencionEnesa().getCodtiposubvenesa();
			} else {
				codTipo = ((SubvencionEnesa) subv).getTipoSubvencionEnesa()
						.getCodtiposubvenesa();
			}
			if (!codSubvsEnesa.contains(codTipo)) {
				subvsEnesaSinRep.add(subv);
			}
			codSubvsEnesa.add(codTipo);
		}
		return subvsEnesaSinRep;
	}

	/**
	 * Metodo para obtener los codigos de subvencion que aplican a un
	 * determinado plan y grupo
	 * 
	 * @param anexo
	 *            Anexo de modificacion de poliza
	 * @param subvenciones
	 *            Lista de subvenciones a tratar. Al finalizar el proceso,
	 *            quedaran unicamente las subvenciones a mostrar en la pantalla
	 */
	@SuppressWarnings("unchecked")
	private void filtraSubvencionesGrupo(final AnexoModificacion anexo,
			final List<SubvencionesAseguradosView> subvenciones) {
		
		SubvencionesGrupoFiltro filtroGrupo = new SubvencionesGrupoFiltro(anexo
				.getPoliza().getLinea().getCodplan(), new BigDecimal(1));
		List<BigDecimal> subvsGrupo = (List<BigDecimal>) this.subvencionesDeclarablesModificacionPolizaDao
				.getObjects(filtroGrupo);
		// recorremos las subvenciones para "hacer limpieza"
		List<SubvencionesAseguradosView> subvsfiltrogrupo = new ArrayList<SubvencionesAseguradosView>();
		for (SubvencionesAseguradosView tsev : subvenciones) {
			BigDecimal codTipoSubvEnesa = null;
			Character nivelDepenSubvEnesa = null;
			BigDecimal codTipoSubvCCAA = null;
			Character nivelDepenSubvCCAA = null;
			if (tsev.isLineaGanado()) {
				if (tsev.getSubvEnesa() != null) {
					codTipoSubvEnesa = ((SubvencionEnesaGanado) (tsev
							.getSubvEnesa())).getTipoSubvencionEnesa()
							.getCodtiposubvenesa();
					nivelDepenSubvEnesa = ((SubvencionEnesaGanado) (tsev
							.getSubvEnesa())).getTipoSubvencionEnesa()
							.getNiveldependencia();
				}
				if (tsev.getSubvCCAA() != null) {
					codTipoSubvCCAA = ((SubvencionCCAAGanado) (tsev
							.getSubvCCAA())).getTipoSubvencionCCAA()
							.getCodtiposubvccaa();
					nivelDepenSubvCCAA = ((SubvencionCCAAGanado) (tsev
							.getSubvCCAA())).getTipoSubvencionCCAA()
							.getNiveldependencia();
				}
			} else {
				if (tsev.getSubvEnesa() != null) {
					codTipoSubvEnesa = ((SubvencionEnesa) (tsev.getSubvEnesa()))
							.getTipoSubvencionEnesa().getCodtiposubvenesa();
					nivelDepenSubvEnesa = ((SubvencionEnesa) (tsev
							.getSubvEnesa())).getTipoSubvencionEnesa()
							.getNiveldependencia();
				}
				if (tsev.getSubvCCAA() != null) {
					codTipoSubvCCAA = ((SubvencionCCAA) (tsev.getSubvCCAA()))
							.getTipoSubvencionCCAA().getCodtiposubvccaa();
					nivelDepenSubvCCAA = ((SubvencionCCAA) (tsev.getSubvCCAA()))
							.getTipoSubvencionCCAA().getNiveldependencia();
				}
			}
			if (tsev.getSubvCCAA() != null
					&& !new Character('J').equals(nivelDepenSubvCCAA)
					&& subvsGrupo.contains(codTipoSubvCCAA)) {
				subvsfiltrogrupo.add(tsev);
			} else if (tsev.getSubvEnesa() != null
					&& !new Character('J').equals(nivelDepenSubvEnesa)
					&& subvsGrupo.contains(codTipoSubvEnesa)) {
				subvsfiltrogrupo.add(tsev);
				break;
			}
		}
		
		subvenciones.removeAll(subvsfiltrogrupo);
		// RATIO
		// Se recorren las subvenciones y se busca en las enesa si tiene ratio.
		// En tal caso, se cuentan los socios que tienen seleccionada al menos
		// una subvencion del grupo 1.
		int contadorsocios = 0;
		Asegurado asegurado = anexo.getPoliza().getAsegurado();
		for (SubvencionesAseguradosView tsev : subvenciones) {
			BigDecimal ratio=null;
			if (tsev.isLineaGanado()) {
				if(null!=tsev.getSubvEnesa() && null!=((SubvencionEnesaGanado) tsev.getSubvEnesa()).getRatio())
					ratio = ((SubvencionEnesaGanado) tsev.getSubvEnesa()).getRatio();
			} else {
				if(null!= tsev.getSubvEnesa() && null!=((SubvencionEnesa) tsev.getSubvEnesa()).getRatio())
					ratio = ((SubvencionEnesa) tsev.getSubvEnesa()).getRatio();
			}
			if (tsev.getSubvEnesa() != null && ratio != null
					&& asegurado.getSocios() != null
					&& asegurado.getSocios().size() > 0) {
				// Recorro los socios del asegurado
				for (Socio s : asegurado.getSocios()) {
					for (SubvencionSocio ss : s.getSubvencionSocios()) {
						if (subvsGrupo
								.contains(ss.getSubvencionEnesa()
										.getTipoSubvencionEnesa()
										.getCodtiposubvenesa())) {
							contadorsocios++;
							break;
						}
					}
				}
				BigDecimal pctRatio = (new BigDecimal(contadorsocios)).divide(
						new BigDecimal(asegurado.getSocios().size()),
						new MathContext(2)).multiply(new BigDecimal(100));
				if (pctRatio.compareTo(ratio) >= 0) {
					tsev.setMarcada(true);
				} else {
					tsev.setMarcada(false);
				}
			}
		}
	}

	/**
	 * Metodo para actualizar las subvenciones del anexo de modificacion. Se
	 * tendra en cuenta lo siguiente: Subvenciones seleccionadas: - Si
	 * tipomodificacion = null o 'ALTA' => lo dejamos como esta. - Si
	 * tipomodificacion = 'BAJA' => lo dejamos a null. Subvenciones no
	 * seleccionadas: - Si tipomodificacion = null o 'BAJA' => lo dejamos en
	 * 'BAJA' - Si tipomodificacion = 'ALTA' => la eliminamos de las
	 * subvenciones
	 * 
	 * @param anexo
	 *            Anexo a modificar
	 * @param subvSeleccionadas
	 *            Cadena de texto con los codigos de subvenciones seleccionadas
	 *            en pantalla separadas por ','. Por cada codigo tambien vendra
	 *            especificado si es enesa ('/E') o de la comunidad ('/C').
	 * @throws DAOException
	 *             Error durante la acutalizacion de la base de datos.
	 * @throws BusinessException
	 * @throws XmlException 
	 */
	public void actualizaSubvencionesAnexoModificacion(
			final AnexoModificacion anexo, final String subvSeleccionadas,
			final BigDecimal perfilUsuario) throws BusinessException,
			DAOException, ValidacionAnexoModificacionException, XmlException {
		Set<SubvDeclarada> subvAnexo = anexo.getSubvDeclaradas();
		// Separamos los codigos seleccionados para saber si son enesa o ccaa.
		List<String> codenesa = new ArrayList<String>();
		List<String> codccaa = new ArrayList<String>();
		if (!subvSeleccionadas.equalsIgnoreCase("")) {
			String[] subvsSelect = subvSeleccionadas.split(",");
			for (String subv : subvsSelect) {
				if (subv.indexOf("E") > -1) {
					codenesa.add(subv.substring(0, subv.indexOf("/")));
				} else if (subv.indexOf("C") > -1) {
					codccaa.add(subv.substring(0, subv.indexOf("/")));
				}
			}
		}
		// Obtenemos las subvenciones de la pantalla y vemos cuales estaban
		// seleccionadas en la pantalla y cuales no.
		List<SubvencionesAseguradosView> listaSubvenciones = this
				.getSubvencionesAsegurado(anexo, perfilUsuario);
		// Vamos recorriendo la lista de subvenciones y las vamos añadiendo a
		// la lista definitiva de subvenciones del anexo.
		Set<SubvDeclarada> subvDefinitivas = new HashSet<SubvDeclarada>();
		boolean tratada = false;
		for (SubvencionesAseguradosView sav : listaSubvenciones) {
			BigDecimal codTipoSubvEnesa = null;
			BigDecimal codTipoSubvCCAA = null;
			if (sav.isLineaGanado()) {
				if (sav.getSubvEnesa() != null) {
					codTipoSubvEnesa = ((SubvencionEnesaGanado) (sav
							.getSubvEnesa())).getTipoSubvencionEnesa()
							.getCodtiposubvenesa();
				}
				if (sav.getSubvCCAA() != null) {
					codTipoSubvCCAA = ((SubvencionCCAAGanado) (sav
							.getSubvCCAA())).getTipoSubvencionCCAA()
							.getCodtiposubvccaa();
				}
			} else {
				if (sav.getSubvEnesa() != null) {
					codTipoSubvEnesa = ((SubvencionEnesa) (sav.getSubvEnesa()))
							.getTipoSubvencionEnesa().getCodtiposubvenesa();
				}
				if (sav.getSubvCCAA() != null) {
					codTipoSubvCCAA = ((SubvencionCCAA) (sav.getSubvCCAA()))
							.getTipoSubvencionCCAA().getCodtiposubvccaa();
				}
			}
			SubvDeclarada sdDef = new SubvDeclarada();
			for (SubvDeclarada sd : subvAnexo) {
				if (sav.getTipoSubvencion().equals("E")
						&& codTipoSubvEnesa.equals(sd.getCodsubvencion())
						&& codenesa.contains(sd.getCodsubvencion() + "")
						&& (sd.getTipomodificacion() == null || sd
								.getTipomodificacion().equals(
										new Character('A')))) {
					// Si es enesa y esta en las subvenciones del anexo, ha sido
					// seleccionada y tipomodificacion es null o 'A'
					sdDef.setCodsubvencion(sd.getCodsubvencion());
					sdDef.setTipomodificacion(sd.getTipomodificacion());
					sdDef.setAnexoModificacion(anexo);
					tratada = true;
					break;
				} else if (sav.getTipoSubvencion().equals("E")
						&& codTipoSubvEnesa.equals(sd.getCodsubvencion())
						&& codenesa.contains(sd.getCodsubvencion() + "")
						&& sd.getTipomodificacion().equals(new Character('B'))) {
					// Si es enesa y esta en las subvenciones del anexo, ha sido
					// seleccionada y tipomodificacion es 'B'
					sdDef.setCodsubvencion(sd.getCodsubvencion());
					sdDef.setTipomodificacion(null);
					sdDef.setAnexoModificacion(anexo);
					tratada = true;
					break;
				} else if (sav.getTipoSubvencion().equals("E")
						&& codTipoSubvEnesa.equals(sd.getCodsubvencion())
						&& !codenesa.contains(sd.getCodsubvencion() + "")
						&& (sd.getTipomodificacion() == null || sd
								.getTipomodificacion().equals(
										new Character('B')))) {
					// Si es enesa y esta en las subvenciones del anexo, NO ha
					// sido seleccionada y tipomodificacion es null o 'B'
					sdDef.setCodsubvencion(sd.getCodsubvencion());
					sdDef.setTipomodificacion(new Character('B'));
					sdDef.setAnexoModificacion(anexo);
					tratada = true;
					break;
				} else if (sav.getTipoSubvencion().equals("C")
						&& codTipoSubvCCAA.equals(sd.getCodsubvencion())
						&& codccaa.contains(sd.getCodsubvencion() + "")
						&& (sd.getTipomodificacion() == null || sd
								.getTipomodificacion().equals(
										new Character('A')))) {
					// Si es CCAA y esta en las subvenciones del anexo, ha sido
					// seleccionada y tipomodificacion es null o 'A'
					sdDef.setCodsubvencion(sd.getCodsubvencion());
					sdDef.setTipomodificacion(sd.getTipomodificacion());
					sdDef.setAnexoModificacion(anexo);
					tratada = true;
					break;
				} else if (sav.getTipoSubvencion().equals("C")
						&& codTipoSubvCCAA.equals(sd.getCodsubvencion())
						&& codccaa.contains(sd.getCodsubvencion() + "")
						&& sd.getTipomodificacion().equals(new Character('B'))) {
					// Si es CCAA y esta en las subvenciones del anexo, ha sido
					// seleccionada y tipomodificacion es 'B'
					sdDef.setCodsubvencion(sd.getCodsubvencion());
					sdDef.setTipomodificacion(null);
					sdDef.setAnexoModificacion(anexo);
					tratada = true;
					break;
				} else if (sav.getTipoSubvencion().equals("C")
						&& codTipoSubvCCAA.equals(sd.getCodsubvencion())
						&& !codccaa.contains(sd.getCodsubvencion() + "")
						&& (sd.getTipomodificacion() == null || sd
								.getTipomodificacion().equals(
										new Character('B')))) {
					// Si es CCAA y esta en las subvenciones del anexo, NO ha
					// sido seleccionada y tipomodificacion es null o 'B'
					sdDef.setCodsubvencion(sd.getCodsubvencion());
					sdDef.setTipomodificacion(new Character('B'));
					sdDef.setAnexoModificacion(anexo);
					tratada = true;
					break;
				}
			}
			// si no esta en la lista de subvenciones anteriores del anexo y ha
			// sido marcada, la tratamos ahora
			if (!tratada) {
				if (sav.getTipoSubvencion().equals("E")
						&& codenesa.contains(codTipoSubvEnesa + "")) {
					sdDef.setCodsubvencion(codTipoSubvEnesa);
					sdDef.setTipomodificacion(new Character('A'));
					sdDef.setAnexoModificacion(anexo);
					tratada = true;
				} else if (sav.getTipoSubvencion().equals("C")
						&& codccaa.contains(codTipoSubvCCAA + "")) {
					sdDef.setCodsubvencion(codTipoSubvCCAA);
					sdDef.setTipomodificacion(new Character('A'));
					sdDef.setAnexoModificacion(anexo);
					tratada = true;
				}
			}
			if (tratada) {
				subvDefinitivas.add(sdDef);
			}
			tratada = false;
		}
		// Borramos las subvenciones anteriores del anexo e insertamos las
		// nuevas (subvDefinitivas)
		this.subvencionesDeclarablesModificacionPolizaDao.actualizaSubvencionesAnexo(anexo, subvDefinitivas);
		anexo.setSubvDeclaradas(subvDefinitivas);
		// Calculo de CPM permitidos
		logger.debug("Se cargan los CPM permitidos para la poliza y el anexo relacionado - idPoliza: "
				+ anexo.getPoliza().getIdpoliza()
				+ ", idAnexo: "
				+ anexo.getId() + ", codModulo: " + anexo.getCodmodulo());
		List<BigDecimal> listaCPM = cpmTipoCapitalDao.getCPMDePolizaAnexoMod(anexo.getPoliza().getIdpoliza(), anexo.getId(), anexo.getCodmodulo());
		// Actualizamos el xml
		boolean modAseg = false;
		try {
			XmlTransformerUtil.updateXMLAnexoMod(xmlAnexoModDao, polizaCopyDao, anexo, modAseg, listaCPM, false);
		} catch (ValidacionAnexoModificacionException e) {
			logger.error("Error validando el xml de Anexos de Modificacion"
					+ e.getMessage());
			throw new ValidacionAnexoModificacionException(e.getMessage());
		} catch (Exception ee) {
			logger.error("Error generico al deshacerCambiosParcelas"
					+ ee.getMessage());
			throw new BusinessException(
					"Error generico al deshacerCambiosParcelas");
		}
		// TMR mejora 230 actualizamos el asunto del xml
		anexo.setSubvDeclaradas(subvDefinitivas);
		anexo.setAsunto(XmlTransformerUtil.generarAsuntoAnexo(anexo, modAseg));
		this.xmlAnexoModDao.saveOrUpdate(anexo);
	}

	public void setPolizaCopyDao(IPolizaCopyDao polizaCopyDao) {
		this.polizaCopyDao = polizaCopyDao;
	}

	public void setXmlAnexoModDao(IXmlAnexoModificacionDao xmlAnexoModDao) {
		this.xmlAnexoModDao = xmlAnexoModDao;
	}

	public void setAseguradoDao(IAseguradoDao aseguradoDao) {
		this.aseguradoDao = aseguradoDao;
	}

	public void setCpmTipoCapitalDao(ICPMTipoCapitalDAO cpmTipoCapitalDao) {
		this.cpmTipoCapitalDao = cpmTipoCapitalDao;
	}

	public void setDiccionarioDatosDao(IDiccionarioDatosDao diccionarioDatosDao) {
		this.diccionarioDatosDao = diccionarioDatosDao;
	}

	public void setSolicitudModificacionManager(
			ISolicitudModificacionManager solicitudModificacionManager) {
		this.solicitudModificacionManager = solicitudModificacionManager;
	}
}