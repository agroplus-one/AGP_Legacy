package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.cpl.SubvencionesGrupoFiltro;
import com.rsi.agp.dao.filters.poliza.SubAseguradoCcaaFiltro;
import com.rsi.agp.dao.filters.poliza.SubAseguradoCcaaGanadoFiltro;
import com.rsi.agp.dao.filters.poliza.SubAseguradoEnesaFiltro;
import com.rsi.agp.dao.filters.poliza.SubAseguradoEnesaGanadoFiltro;
import com.rsi.agp.dao.filters.poliza.SubvencionCCAAGanPolizaFiltro;
import com.rsi.agp.dao.filters.poliza.SubvencionCCAAPolizaFiltro;
import com.rsi.agp.dao.filters.poliza.SubvencionEnesaGanPolizaFiltro;
import com.rsi.agp.dao.filters.poliza.SubvencionEnesaPolizaFiltro;
import com.rsi.agp.dao.models.IAseguradoSubvencionDao;
import com.rsi.agp.dao.models.admin.IAseguradoDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Socio;
import com.rsi.agp.dao.tables.cgen.SubvencionesAseguradosView;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.SubvencionCCAA;
import com.rsi.agp.dao.tables.cpl.SubvencionEnesa;
import com.rsi.agp.dao.tables.cpl.SubvencionesGrupo;
import com.rsi.agp.dao.tables.cpl.gan.SubvencionCCAAGanado;
import com.rsi.agp.dao.tables.cpl.gan.SubvencionEnesaGanado;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.SubAseguradoCCAA;
import com.rsi.agp.dao.tables.poliza.SubAseguradoCCAAGanado;
import com.rsi.agp.dao.tables.poliza.SubAseguradoENESA;
import com.rsi.agp.dao.tables.poliza.SubAseguradoENESAGanado;
import com.rsi.agp.dao.tables.poliza.SubvencionSocio;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;

import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.AseguradoDatosYMedidasDocument;
import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.ControlAccesoSubvenciones;
import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.Organismo;
import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.Subvencion;

public class AseguradoSubvencionManager implements IManager {

	private final String VACIO = "";
	
	/* P00078846 ** MODIF TAM (14/02/2022) ** Inicio */ 
	private IAseguradoSubvencionDao aseguradoSubvencionDao;
	/* P00078846 ** MODIF TAM (14/02/2022) ** Inicio */
	private IAseguradoDao aseguradoDao;
	
	private static final Log LOGGER = LogFactory.getLog(AseguradoSubvencionManager.class);
	
	final ResourceBundle bundleAgp = ResourceBundle.getBundle("agp");
	
	// ERNESTO
	@SuppressWarnings("unchecked")
	private List<Object> getSubvencionesEnesa(final Asegurado asegurado,
			final Poliza poliza) {
		List<Object> subvenciones;
		Filter filtro;
		// Obtenemos las subvenciones
		String codmodulo = VACIO;
		for (ModuloPoliza mp : poliza.getModuloPolizas()) {
			codmodulo += mp.getId().getCodmodulo() + ";";
		}
		codmodulo += "99999;";
		if (poliza.getLinea().isLineaGanado()) {
			filtro = new SubvencionEnesaGanPolizaFiltro(poliza.getLinea()
					.getLineaseguroid(), "1",
					asegurado.getTipoidentificacion(), codmodulo, true, poliza.getCodigosTipoAnimalExplotaciones(), null!=poliza.getFechaenvio()?poliza.getFechaenvio():new Date());
		} else {
			filtro = new SubvencionEnesaPolizaFiltro(poliza.getLinea()
					.getLineaseguroid(), "1",
					asegurado.getTipoidentificacion(), codmodulo, true, null!=poliza.getFechaenvio()?poliza.getFechaenvio():new Date());
		}
		subvenciones = (List<Object>) this.aseguradoSubvencionDao.getObjects(filtro);
		return subvenciones;
	}

	@SuppressWarnings("unchecked")
	private List<Object> getSubvencionesCCAA(final Asegurado asegurado,
			final Poliza poliza, final List<BigDecimal> listaCodTipos) {
		List<Object> subvenciones;
		Filter filtro;
		// TMR. 29-10-2012 . Filtramos las subvenciones por codOrganismo que lo
		// obtenemos de las provincias de las parcelas
		ArrayList<BigDecimal> codsProv = new ArrayList<BigDecimal>();
		boolean isLineaGanado = poliza.getLinea().isLineaGanado();
		if (isLineaGanado) {
			for (Explotacion explotacion : poliza.getExplotacions()) {
				if (!codsProv.contains(explotacion.getTermino().getProvincia()
						.getCodprovincia())) {
					codsProv.add(explotacion.getTermino().getProvincia()
							.getCodprovincia());
				}
			}
		} else {
			// recuperamos las provincias de las parcelas
			for (com.rsi.agp.dao.tables.poliza.Parcela par : poliza
					.getParcelas()) {
				if (!codsProv.contains(par.getTermino().getProvincia()
						.getCodprovincia())) {
					codsProv.add(par.getTermino().getProvincia()
							.getCodprovincia());
				}
			}
		}
		// obtenemos los codsOrganismos de las provincias
		List<BigDecimal> codsOrganismos = null;
		try {
			codsOrganismos = (List<BigDecimal>) this.aseguradoDao
					.getCodsOrganismos(codsProv);
		} catch (DAOException e) {
			LOGGER.error("Excepcion : AseguradoSubvencionManager - getSubvencionesCCAA", e);
		}
		// Obtenemos las subvenciones
		String codmodulo = VACIO;
		for (ModuloPoliza mp : poliza.getModuloPolizas()) {
			codmodulo += mp.getId().getCodmodulo() + ";";
		}
		codmodulo += "99999;";
		if (isLineaGanado) {
			filtro = new SubvencionCCAAGanPolizaFiltro(poliza.getLinea()
					.getLineaseguroid(), "1",
					asegurado.getTipoidentificacion(), codmodulo, null,
					codsOrganismos, listaCodTipos, poliza.getCodigosTipoAnimalExplotaciones(), null!=poliza.getFechaenvio()?poliza.getFechaenvio():new Date());
		} else {
			filtro = new SubvencionCCAAPolizaFiltro(poliza.getLinea()
					.getLineaseguroid(), "1",
					asegurado.getTipoidentificacion(), codmodulo, null,
					codsOrganismos, listaCodTipos, null!=poliza.getFechaenvio()?poliza.getFechaenvio():new Date());
		}
		subvenciones = (List<Object>) this.aseguradoSubvencionDao.getObjects(filtro);
		return subvenciones;
	}

	public List<SubvencionesAseguradosView> getSubvencionesAsegurado(
			final Asegurado asegurado, final Poliza poliza,
			final Usuario usuario, Boolean esModoLectura) {
		
		
		LOGGER.debug("AseguradoSubvencionManager - getSubvencionesAsegurado - init");

		
		boolean isLineaGanado = poliza.getLinea().isLineaGanado();
		// Entidades asociativas solo para usuarios 0 y 1
		BigDecimal codEntidadesAsocitivas = new BigDecimal(15);

		// obtenemos todas las subvenciones enesa y de la comunidad
		List<Object> subvsEnesa = getSubvencionesEnesa(asegurado, poliza);

		// DAA 15/02/2013 De la lista de subvsEnesa sacamos los ids de los tipos
		// para no incuirlos en las de la comunidad
		List<BigDecimal> listaCodTipos = AseguradoSubvencionManager
				.getListaCodTipoSubvsEnesa(subvsEnesa);

		List<Object> subvsCCAA = getSubvencionesCCAA(asegurado, poliza,
				listaCodTipos);

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

		List<BigDecimal> codUsers = Arrays.asList(new BigDecimal[] {
				new BigDecimal(0), new BigDecimal(1), new BigDecimal(5) });

		// recorremos la lista de subvenciones enesa y comprobamos que no estan
		// en la lista de subvenciones de la comunidad
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
						subvsCCAAEliminar.add(subvCCAA);// la buena es ENESA
					} else {
						subvsEnesaEliminar.add(subvEnesa);// la buena es CCAA
					}
				}
				// Eliminar la subvencion 15 de ccaa
				if (codTipoSubvCCAA.toString().equals(
						codEntidadesAsocitivas.toString())) {
					if (!codUsers.contains(usuario.getTipousuario())) {
						subvsCCAAEliminar.add(subvCCAA);
					}
				}
			}// fin for ccaa

			// Eliminar la subvencion 15 de enesa
			if (codTipoSubvEnesa.toString().equals(
					codEntidadesAsocitivas.toString())) {
				if (!codUsers.contains(usuario.getTipousuario())) {
					subvsEnesaEliminar.add(subvEnesa);
				}
			}
		} // fin for enesa

		subvsCCAASinRep.removeAll(subvsCCAAEliminar);
		subvsEnesaSinRep.removeAll(subvsEnesaEliminar);

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
			sav.setMarcada(false);
			sav.setSubvEnesa(subvEnesa);
			sav.setTipoSubvencion("E");
			sav.setCodtiposubvencion(codTipoSubvEnesa);					
			resultado.add(sav);
		}			
		
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
			sav.setMarcada(false);
			sav.setSubvCCAA(subvCCAA);
			sav.setTipoSubvencion("C");
			sav.setCodtiposubvencion(codTipoSubvCCAA);
			
			resultado.add(sav);
		}
		// INICIO DE LA ORDENACION DE LAS SUBVENCIONES
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
		// FIN DE LA ORDENACION DE LAS SUBVENCIONES

		if (asegurado.getTipoidentificacion().equals("CIF")) {
			// Una vez obtenidas todas las subvenciones, obtenemos de
			// 'Subv_Grupos' las subvenciones para el grupo 1
			// y eliminamos de la lista 'subvencionesEnesaPantalla' las que SI
			// estan en el resultado de la consulta.
			filtraSubvencionesGrupo(asegurado, poliza, resultadoOrdenado);
		}

		getGruposSubv(resultadoOrdenado, poliza);
		
		LOGGER.debug("AseguradoSubvencionManager - getSubvencionesAsegurado - end");
		
		return resultadoOrdenado;
	}

	public static List<BigDecimal> getListaCodTipoSubvsEnesa(
			final List<Object> subvsEnesa) {
		List<BigDecimal> listaCodTipos = new ArrayList<BigDecimal>();
		for (Object subvs : subvsEnesa) {
			BigDecimal tipo;
			if (subvs instanceof SubvencionEnesaGanado) {
				tipo = ((SubvencionEnesaGanado) subvs).getTipoSubvencionEnesa()
						.getCodtiposubvenesa();
			} else {
				tipo = ((SubvencionEnesa) subvs).getTipoSubvencionEnesa()
						.getCodtiposubvenesa();
			}
			listaCodTipos.add(tipo);
		}
		return listaCodTipos;
	}

	/**
	 * Metodo que crea un TreeMap con nuestro Grupos + Descripcion ordenados
	 * Ascendentemente
	 * 
	 * @param listaSubvencionesAsegurado
	 * @return
	 */
	private TreeMap<BigDecimal, String> getGruposSubv(
			final List<SubvencionesAseguradosView> listaSubvencionesAsegurado) {
		TreeMap<BigDecimal, String> listadoGrupos = new TreeMap<BigDecimal, String>();
		for (SubvencionesAseguradosView vista : listaSubvencionesAsegurado) {
			if (vista.getCodgruposubvencion() != null) {
				listadoGrupos.put(vista.getCodgruposubvencion(),
						vista.getDescgrupo());
			} else {
				listadoGrupos.put(new BigDecimal(9999), "Sin Grupo");
			}
		}
		return listadoGrupos;
	}

	/**
	 * Metodo que crea una HashMap con el listado de subvenciones agrupadas por
	 * clave grupo
	 * 
	 * @param listadoGrupos
	 * @param listaSubvencionesAsegurado
	 * @return
	 */
	private HashMap<String, List<SubvencionesAseguradosView>> getListadoSubvGrupo(
			final TreeMap<BigDecimal, String> listadoGrupos,
			final List<SubvencionesAseguradosView> listaSubvencionesAsegurado) {
		HashMap<String, List<SubvencionesAseguradosView>> gruposSubv = new HashMap<String, List<SubvencionesAseguradosView>>();
		List<SubvencionesAseguradosView> listaaux = null;
		for (BigDecimal grupo : listadoGrupos.keySet()) {
			if (!grupo.equals(new BigDecimal(9999))) {
				listaaux = new ArrayList<SubvencionesAseguradosView>();
				for (SubvencionesAseguradosView vista : listaSubvencionesAsegurado) {
					if (null != vista.getCodgruposubvencion()
							&& vista.getCodgruposubvencion().equals(grupo)) {
						listaaux.add(vista);
					}
				}
				gruposSubv.put(grupo.toString(), listaaux);
			} else {
				listaaux = new ArrayList<SubvencionesAseguradosView>();
				for (SubvencionesAseguradosView vistaNull : listaSubvencionesAsegurado) {
					if (vistaNull.getCodgruposubvencion() == null) {
						listaaux.add(vistaNull);
					}
				}
				gruposSubv.put(grupo.toString(), listaaux);
			}
		}
		return gruposSubv;
	}

	/**
	 * Metodo que crea las tablas HTML para mostrarlas en la JSP
	 * 
	 * @param listaSubvencionesAsegurado
	 * @return
	 */
	public String pintarTablaSubv(final List<SubvencionesAseguradosView> listaSubvencionesAsegurado, final Boolean isModoLectura) {
		
		StringBuffer resultado = new StringBuffer(VACIO);
		List<SubvencionesAseguradosView> enesaSub = new ArrayList<SubvencionesAseguradosView>();
		List<SubvencionesAseguradosView> ccaaSub = new ArrayList<SubvencionesAseguradosView>();
		TreeMap<BigDecimal, String> listadoGruposEnesa = new TreeMap<BigDecimal, String>();
		TreeMap<BigDecimal, String> listadoGruposCcaa = new TreeMap<BigDecimal, String>();
		HashMap<String, List<SubvencionesAseguradosView>> gruposSubvEnesa = new HashMap<String, List<SubvencionesAseguradosView>>();
		HashMap<String, List<SubvencionesAseguradosView>> gruposSubvCcaa = new HashMap<String, List<SubvencionesAseguradosView>>();
		
		for( SubvencionesAseguradosView sav: listaSubvencionesAsegurado){
			if(sav.getTipoSubvencion().equals("E")){
				enesaSub.add(sav);
			}
			else{
				ccaaSub.add(sav);
			}
		}
		listadoGruposEnesa = getGruposSubv(enesaSub);
		listadoGruposCcaa = getGruposSubv(ccaaSub);
		gruposSubvEnesa = getListadoSubvGrupo(listadoGruposEnesa, enesaSub);
		gruposSubvCcaa = getListadoSubvGrupo(listadoGruposCcaa, ccaaSub);
		if (gruposSubvEnesa.size() > 0) {
			resultado.append("<fieldset><legend class='literal'>ENESA</legend>");
			for (BigDecimal grupo : listadoGruposEnesa.keySet()) {
				if (!grupo.equals(new BigDecimal(9999))) {
					resultado.append("<fieldset style='width:98%' align='center'>");
					resultado.append("<legend class='literal'>");
					resultado.append(grupo);
					resultado.append("-");
					resultado.append(listadoGruposEnesa.get(grupo));
					resultado.append("</legend>");
				}
				resultado.append("<table align='center'>");
				int contador = 0;
				for (SubvencionesAseguradosView obj : gruposSubvEnesa.get(grupo.toString())) {
					if (contador == 2) {
						resultado.append("</tr>");
						contador = 0;
					}
					if (contador == 0) {
						resultado.append("<tr>");
					}
					resultado.append("<td class='literal'>"
							+ "<input type='checkbox' ");
					if (obj.isMarcada()) {
						resultado.append("onchange='desmarcarSubvencion(this)' checked ");
					}
					if (obj.isNoEdit() || isModoLectura) {
						resultado.append("disabled ");
					}
					resultado.append("value='");
					resultado.append(obj.getCodtiposubvencion());
					resultado.append("/");
					resultado.append(obj.getTipoSubvencion());
					resultado.append("'/>");
					
					if (obj.isLineaGanado()) {
						/* P79408 RQ.03 y RQ.04 Inicio */
						resultado.append(((SubvencionEnesaGanado) (obj
								.getSubvEnesa())).getTipoSubvencionEnesa()
								.getCodtiposubvenesa());
						resultado.append(" - ");
						/* P79408 RQ.03 y RQ.04 Fin */
						resultado.append(((SubvencionEnesaGanado) (obj
								.getSubvEnesa())).getTipoSubvencionEnesa()
								.getDestiposubvenesa());
						resultado.append(" (");
						resultado.append(((SubvencionEnesaGanado) (obj
								.getSubvEnesa())).getPorcSubvSeguroInd());
						resultado.append("%)");
					} else {
						/* P79408 RQ.03 y RQ.04 Inicio */
						resultado.append(((SubvencionEnesa) (obj
								.getSubvEnesa())).getTipoSubvencionEnesa()
								.getCodtiposubvenesa());
						resultado.append(" - ");
						/* P79408 RQ.03 y RQ.04 Fin */
						resultado.append(((SubvencionEnesa) (obj
								.getSubvEnesa())).getTipoSubvencionEnesa()
								.getDestiposubvenesa());
						resultado.append(" (");
						resultado.append(((SubvencionEnesa) (obj
								.getSubvEnesa())).getPctsubvindividual());
						resultado.append("%)");
					}
					
					resultado.append("</td>");
					contador++;
				}
				if (contador != 0)
					resultado.append("</tr>");
				resultado.append("</table>");
				if (!grupo.equals(new BigDecimal(9999))) {
					resultado.append("</fieldset>");
				}
				
			}
			resultado.append("</fieldset>");
		}
		
		if (gruposSubvCcaa.size() > 0) {
			resultado.append("<fieldset><legend class='literal'>CCAA</legend>");
			for (BigDecimal grupo : listadoGruposCcaa.keySet()) {
				if (!grupo.equals(new BigDecimal(9999))) {
					resultado.append("<fieldset style='width:98%' align='center'>");
					resultado.append("<legend class='literal'>");
					resultado.append(grupo);
					resultado.append("-");
					resultado.append(listadoGruposCcaa.get(grupo));
					resultado.append("</legend>");
				}
				resultado.append("<table align='center'>");
				int contador = 0;
				for (SubvencionesAseguradosView obj : gruposSubvCcaa.get(grupo.toString())) {
					if (contador == 2) {
						resultado.append("</tr>");
						contador = 0;
					}
					if (contador == 0) {
						resultado.append("<tr>");
					}
					resultado.append("<td class='literal'>"
							+ "<input type='checkbox' ");
					if (obj.isMarcada()) {
						resultado.append("checked ");
					}
					if (obj.isNoEdit() || isModoLectura) {
						resultado.append("disabled ");
					}
					resultado.append("value='");
					resultado.append(obj.getCodtiposubvencion());
					resultado.append("/");
					resultado.append(obj.getTipoSubvencion());
					resultado.append("'/>");
					
					if (obj.isLineaGanado()) {
						resultado.append(((SubvencionCCAAGanado) (obj
								.getSubvCCAA())).getTipoSubvencionCCAA()
								.getCodtiposubvccaa());
						resultado.append(" - ");
						resultado.append(((SubvencionCCAAGanado) (obj
								.getSubvCCAA())).getTipoSubvencionCCAA()
								.getDestiposubvccaa());
						resultado.append(" (");
						resultado.append(((SubvencionCCAAGanado) (obj
								.getSubvCCAA())).getPorcSubvSeguroInd());
						resultado.append("%)");
					} else {
						resultado.append(((SubvencionCCAA) (obj
								.getSubvCCAA())).getTipoSubvencionCCAA()
								.getCodtiposubvccaa());
						resultado.append(" - ");
						resultado.append(((SubvencionCCAA) (obj
								.getSubvCCAA())).getTipoSubvencionCCAA()
								.getDestiposubvccaa());
						resultado.append(" (");
						resultado.append(((SubvencionCCAA) (obj
								.getSubvCCAA())).getPctsubvindividual());
						resultado.append("%)");
					}
					
					resultado.append("</td>");
					contador++;
				}
				if (contador != 0)
					resultado.append("</tr>");
				resultado.append("</table>");
				if (!grupo.equals(new BigDecimal(9999))) {
					resultado.append("</fieldset>");
				}
				
			}
			resultado.append("</fieldset>");
		}

		return resultado.toString();
	}

	/**
	 * Metodo con el que asignamos a nuestra Vista de Subv los grupos a los que
	 * pertenece cada subvencion
	 * 
	 * @param resultadoOrdenado
	 * @param poliza
	 */
	private void getGruposSubv(
			final List<SubvencionesAseguradosView> resultadoOrdenado,
			final Poliza poliza) {
		try {
			// Listado de grupos de subv para mi plan
			List<SubvencionesGrupo> subvgrupo = aseguradoDao.getGruposSubv(poliza.getLinea().getCodplan());

			// Asigno los grupos a mi listado de subv final
			for (SubvencionesAseguradosView vista : resultadoOrdenado) {
				for (SubvencionesGrupo grupo : subvgrupo) {
					if (vista.getCodtiposubvencion().equals(
							grupo.getId().getCodtiposubv())) {
						vista.setCodgruposubvencion(grupo.getId().getGruposubv());
						vista.setDescgrupo(grupo.getGrupoSubvenciones().getDescripcion());
						break;
					}
				}
			}
		} catch (DAOException e) {
			LOGGER.error("Error en getGruposSubv", e);
		}
	}

	/**
	 * Metodo para obtener los codigos de subvencion que aplican a un
	 * determinado plan y grupo
	 * 
	 * @param asegurado
	 *            Asegurado de la poliza
	 * @param poliza
	 *            Poliza para la que queremos las subvenciones
	 * @param subvenciones
	 *            Lista de subvenciones a tratar. Al finalizar el proceso,
	 *            quedaran unicamente las subvenciones a mostrar en la pantalla
	 */
	@SuppressWarnings("unchecked")
	private void filtraSubvencionesGrupo(final Asegurado asegurado,
			final Poliza poliza,
			final List<SubvencionesAseguradosView> subvenciones) {
		SubvencionesGrupoFiltro filtroGrupo = new SubvencionesGrupoFiltro(
				poliza.getLinea().getCodplan(), new BigDecimal(1));
		List<BigDecimal> subvsGrupo = (List<BigDecimal>) this.aseguradoSubvencionDao
				.getObjects(filtroGrupo);		
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
			if (tsev.getSubvCCAA() != null && nivelDepenSubvCCAA != null
					&& nivelDepenSubvCCAA.equals(new Character('J'))
					&& !subvsGrupo.contains(codTipoSubvCCAA)) {
				subvsfiltrogrupo.add(tsev);
			} else if (tsev.getSubvEnesa() != null
					&& nivelDepenSubvEnesa != null
					&& nivelDepenSubvEnesa.equals(new Character('J'))
					&& !subvsGrupo.contains(codTipoSubvEnesa)) {
				subvsfiltrogrupo.add(tsev);
			}
		}
		// Limpiamos las subvenciones
		subvenciones.removeAll(subvsfiltrogrupo);
		
		// RATIO
		// Se recorren las subvenciones y se busca en las enesa si tiene ratio.
		// En tal caso, se cuentan los socios que tienen seleccionada al menos
		// una subvencion del grupo 1.
		int contadorsocios = 0;
		for (SubvencionesAseguradosView tsev : subvenciones) {
			BigDecimal ratio = null;
			if (tsev.getSubvEnesa() != null) {
				if (tsev.isLineaGanado()) {
					ratio = ((SubvencionEnesaGanado) (tsev.getSubvEnesa())).getRatio();
				} else {
					ratio = ((SubvencionEnesa) (tsev.getSubvEnesa())).getRatio();
				}
			}
			if (tsev.getSubvEnesa() != null && ratio != null
					&& asegurado.getSocios() != null
					&& asegurado.getSocios().size() > 0) {
				// Recorro los socios del asegurado
				for (Socio s : asegurado.getSocios()) {
					for (SubvencionSocio ss : s.getSubvencionSocios()) {
						// Si la subvencion es para esta poliza y tiene ratio
						// (la subvencion esta en 'subvsGrupo')
						if (ss.getSubvencionEnesa().getId().getLineaseguroid()
								.equals(poliza.getLinea().getLineaseguroid())
								&& subvsGrupo.contains(ss.getSubvencionEnesa()
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
	 * Metodo para obtener la lista de todas las subvenciones atendiento a los
	 * codigos indicados como parametro. Utilizaremos este metodo cuando
	 * actualicemos las subvenciones Enesa de un asegurado: se recibiran los
	 * codigos de las subvenciones seleccionadas para el asegurado y se
	 * obtendran los objetos "SubvencionEnesa" asociados a los mismos.
	 * 
	 * @param asegurado
	 *            Asegurado para el que se desea obtener la lista de
	 *            subvenciones
	 * @param poliza
	 *            Poliza para la que estamos obteniendo las subvenciones
	 * @param codSubvsSelec
	 *            Codigos de subvencion a buscar en la base de datos
	 * @return Listado de Subvenciones.
	 */
	@SuppressWarnings("unchecked")
	public final List<Object> getSubvEnesaInsertar(final Asegurado asegurado,
			final Poliza poliza, final String codSubvsSelec) {
		Filter filtro;
		// Obtenemos las subvenciones
		String codmodulo = VACIO;
		for (ModuloPoliza mp : poliza.getModuloPolizas()) {
			codmodulo += mp.getId().getCodmodulo() + ";";
		}
		codmodulo += "99999;";
		if (poliza.getLinea().isLineaGanado()) {
			filtro = new SubvencionEnesaGanPolizaFiltro(poliza.getLinea()
					.getLineaseguroid(), "1",
					asegurado.getTipoidentificacion(), codmodulo, true, poliza.getCodigosTipoAnimalExplotaciones(), null!=poliza.getFechaenvio()?poliza.getFechaenvio():new Date());
			((SubvencionEnesaGanPolizaFiltro) filtro)
					.setCodSubvenciones(codSubvsSelec);
		} else {
			filtro = new SubvencionEnesaPolizaFiltro(poliza.getLinea()
					.getLineaseguroid(), "1",
					asegurado.getTipoidentificacion(), codmodulo, true, null!=poliza.getFechaenvio()?poliza.getFechaenvio():new Date());
			((SubvencionEnesaPolizaFiltro) filtro)
					.setCodSubvenciones(codSubvsSelec);
		}
		List<Object> subvenciones = (List<Object>) this.aseguradoSubvencionDao
				.getObjects(filtro);
		return subvenciones;
		// ASF - 19/3/2013 - No eliminamos las repetidas porque al quedarnos con
		// una comparativa podemos estar eliminando las subvenciones
		// de algun modulo y luego no se envia en el xml de contratacion
		// return this.limpiaSubvEnesaRepetidas(subvenciones);
	}

	/**
	 * Metodo para obtener la lista de todas las subvenciones de la comunidad
	 * atendiento a los codigos indicados como parametro. Utilizaremos este
	 * metodo cuando actualicemos las subvenciones CCAA de un asegurado: se
	 * recibiran los codigos de las subvenciones seleccionadas para el
	 * asegurado y se obtendran los objetos "SubvencionCCAA" asociados a los
	 * mismos.
	 * 
	 * @param asegurado
	 *            Asegurado para el que se desea obtener la lista de
	 *            subvenciones
	 * @param poliza
	 *            Poliza para la que estamos obteniendo las subvenciones
	 * @param codSubvsSelec
	 *            Codigos de subvencion a buscar en la base de datos
	 * @return Listado de Subvenciones.
	 */
	@SuppressWarnings("unchecked")
	public final List<Object> getSubvCCAAInsertar(final Asegurado asegurado,
			final Poliza poliza, final String codSubvsSelec) {
		Filter filtro;
		// Obtenemos las subvenciones
		String codmodulo = VACIO;
		for (ModuloPoliza mp : poliza.getModuloPolizas()) {
			codmodulo += mp.getId().getCodmodulo() + ";";
		}
		codmodulo += "99999;";
		if (poliza.getLinea().isLineaGanado()) {
			filtro = new SubvencionCCAAGanPolizaFiltro(poliza.getLinea()
					.getLineaseguroid(), "1",
					asegurado.getTipoidentificacion(), codmodulo,
					poliza.getExplotacions(), null, null, poliza.getCodigosTipoAnimalExplotaciones(), null!=poliza.getFechaenvio()?poliza.getFechaenvio():new Date());
			((SubvencionCCAAGanPolizaFiltro) filtro)
					.setCodSubvenciones(codSubvsSelec);
		} else {
			filtro = new SubvencionCCAAPolizaFiltro(poliza.getLinea()
					.getLineaseguroid(), "1",
					asegurado.getTipoidentificacion(), codmodulo,
					poliza.getParcelas(), null, null, null!=poliza.getFechaenvio()?poliza.getFechaenvio():new Date());
			((SubvencionCCAAPolizaFiltro) filtro)
					.setCodSubvenciones(codSubvsSelec);
		}
		List<Object> subvenciones = (List<Object>) this.aseguradoSubvencionDao
				.getObjects(filtro);
		return subvenciones;
		// ASF - 19/3/2013 - No eliminamos las repetidas porque al quedarnos con
		// una comparativa podemos estar eliminando las subvenciones
		// de algun modulo y luego no se envia en el xml de contratacion
	}

	/**
	 * Metodo para dar de alta las subvenciones de asegurados. Este metod se
	 * utilizara tanto para dar de alta como para modificar las subvenciones de
	 * los asegurados
	 * 
	 * @param asegurado
	 *            Asegurado del que se desea modificar las subvenciones
	 * @param poliza
	 *            Poliza para la que se insertan/modifican las subvenciones
	 * @param subvSelecEnesa
	 *            Subvenciones Enesa seleccionadas para asignar al asegurado
	 * @param subvSelecCCAA
	 *            Subvenciones CCAA seleccionadas para asignar al asegurado
	 * @return Posibles errores.
	 */
	public ArrayList<String> altaSubvenciones(final Asegurado asegurado,
			final Poliza poliza, final List<Object> subvSelecEnesa,
			final List<Object> subvSelecCCAA, final String subSeleccionadas,
			final String codUsuario,
			final String listaSubvAsegMarcadasInicialmente) {
		
		Set<?> subvAsegEnesa;
		Set<?> subvAsegCcaa;
		Long idpoliza;
		Long id;
		ArrayList<String> error = new ArrayList<String>();
		
		try {
			
			/* P0078846 ** MODIF TAM (15.02.2022) ** Inicio */
			LOGGER.debug("AseguradoSubvencionManager - altaSubvenciones [INIT]");
				
			if (null!=listaSubvAsegMarcadasInicialmente) {
				
				/* Obtenemos en un string los tipoSubvenciones que se han obtenido en el S.web que deberian estar marcados y se han desmarcados */
				String detalle = obtenerDetalle(listaSubvAsegMarcadasInicialmente, subSeleccionadas);
				
				/* Guardamos Auditoria con el detalle */
				this.aseguradoSubvencionDao.guardaAuditCambioSubvs(poliza.getIdpoliza(), codUsuario, detalle);
			}
			
			if (poliza.getLinea().isLineaGanado()) {
				subvAsegEnesa = poliza.getSubAseguradoENESAGanados();
				subvAsegCcaa = poliza.getSubAseguradoCCAAGanados();
			} else {
				subvAsegEnesa = poliza.getSubAseguradoENESAs();
				subvAsegCcaa = poliza.getSubAseguradoCCAAs();
			}
			
			// PRIMERO TRATAMOS LAS SUBVENCIONES ENESA
			// Se eliminan las subvenciones que tuviera el asegurado
			// anteriormente para esta poliza
			for (Object ss : subvAsegEnesa) {
				if (poliza.getLinea().isLineaGanado()) {
					idpoliza = ((SubAseguradoENESAGanado) ss).getPoliza()
							.getIdpoliza();
					id = ((SubAseguradoENESAGanado) ss).getId();
					if (idpoliza.compareTo(poliza.getIdpoliza()) == 0) {
						aseguradoSubvencionDao.removeObject(
								SubAseguradoENESAGanado.class, id);
					}
				} else {
					idpoliza = ((SubAseguradoENESA) ss).getPoliza()
							.getIdpoliza();
					id = ((SubAseguradoENESA) ss).getId();
					if (idpoliza.compareTo(poliza.getIdpoliza()) == 0) {
						aseguradoSubvencionDao.removeObject(SubAseguradoENESA.class, id);
					}
				}
			}
			
			LOGGER.debug("Subvenciones ENESA eliminadas correctamente.");
			
			Set<SubAseguradoENESA> newSubvencionesEnesa = new HashSet<SubAseguradoENESA>();
			Set<SubAseguradoENESAGanado> newSubvencionesEnesaGanado = new HashSet<SubAseguradoENESAGanado>();
			if (subvSelecEnesa != null) {
				// recorremos las subvenciones enesa seleccionadas para darlas
				// de alta en bbdd y en el objeto
				for (Object subv : subvSelecEnesa) {
					// Procedemos a la insercion
					Object subvGrabar;
					if (poliza.getLinea().isLineaGanado()) {
						subvGrabar = new SubAseguradoENESAGanado();
						((SubAseguradoENESAGanado) subvGrabar)
								.setAsegurado(asegurado);
						((SubAseguradoENESAGanado) subvGrabar)
								.setPoliza(poliza);
						((SubAseguradoENESAGanado) subvGrabar)
								.setSubvencionEnesaGanado((SubvencionEnesaGanado) subv);
						newSubvencionesEnesaGanado
								.add((SubAseguradoENESAGanado) subvGrabar);

					} else {
						subvGrabar = new SubAseguradoENESA();
						((SubAseguradoENESA) subvGrabar)
								.setAsegurado(asegurado);
						((SubAseguradoENESA) subvGrabar).setPoliza(poliza);
						((SubAseguradoENESA) subvGrabar)
								.setSubvencionEnesa((SubvencionEnesa) subv);
						newSubvencionesEnesa
								.add((SubAseguradoENESA) subvGrabar);
					}
					aseguradoSubvencionDao.saveOrUpdate(subvGrabar);
				}
			}
			
			// AHORA TRATAMOS LAS SUBVENCIONES DE LA COMUNIDAD
			// Se eliminan las subvenciones que tuviera el asegurado
			// anteriormente para esta poliza
			for (Object ss : subvAsegCcaa) {
				if (poliza.getLinea().isLineaGanado()) {
					idpoliza = ((SubAseguradoCCAAGanado) ss).getPoliza()
							.getIdpoliza();
					id = ((SubAseguradoCCAAGanado) ss).getId();
					if (idpoliza.compareTo(poliza.getIdpoliza()) == 0) {
						aseguradoSubvencionDao.delete(SubAseguradoCCAAGanado.class, id);
					}
				} else {
					idpoliza = ((SubAseguradoCCAA) ss).getPoliza()
							.getIdpoliza();
					id = ((SubAseguradoCCAA) ss).getId();
					if (idpoliza.compareTo(poliza.getIdpoliza()) == 0) {
						aseguradoSubvencionDao.delete(SubAseguradoCCAA.class, id);
					}
				}
			}
			
			LOGGER.debug("Subvenciones de la comunidad eliminadas correctamente.");
			
			Set<SubAseguradoCCAA> newSubvencionesCcaa = new HashSet<SubAseguradoCCAA>();
			Set<SubAseguradoCCAAGanado> newSubvencionesCcaaGanado = new HashSet<SubAseguradoCCAAGanado>();
			if (subvSelecCCAA != null) {
				// recorremos las subvenciones enesa seleccionadas para darlas
				// de alta en bbdd y en el objeto
				for (Object subv : subvSelecCCAA) {
					Object subvGrabar;
					// Procedemos a la insercion
					if (poliza.getLinea().isLineaGanado()) {
						subvGrabar = new SubAseguradoCCAAGanado();
						((SubAseguradoCCAAGanado) subvGrabar)
								.setAsegurado(asegurado);
						((SubAseguradoCCAAGanado) subvGrabar).setPoliza(poliza);
						((SubAseguradoCCAAGanado) subvGrabar)
								.setSubvencionCCAAGanado((SubvencionCCAAGanado) subv);
						newSubvencionesCcaaGanado
								.add((SubAseguradoCCAAGanado) subvGrabar);
					} else {
						subvGrabar = new SubAseguradoCCAA();
						((SubAseguradoCCAA) subvGrabar).setAsegurado(asegurado);
						((SubAseguradoCCAA) subvGrabar).setPoliza(poliza);
						((SubAseguradoCCAA) subvGrabar)
								.setSubvencionCCAA((SubvencionCCAA) subv);
						newSubvencionesCcaa.add((SubAseguradoCCAA) subvGrabar);
					}
					aseguradoSubvencionDao.saveOrUpdate(subvGrabar);
				}
			}
			
			LOGGER.debug("Subvenciones ENESA dadas de alta correctamente.");
			
		} catch (Exception e) {
			error.add("Error grave");
			LOGGER.error("Error en altaSubvenciones", e);
		}
		return error;
	}

	/**
	 * Metodo para obtener las subvenciones enesa de un asegurado
	 * 
	 * @param poliza
	 * @param asegurado
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Object> cargaSubvencionesEnesa(final Poliza poliza,
			final Asegurado asegurado) {
		List<Object> listaSubvEnesaAsegurado;
		Filter filtro;
		if (poliza.getLinea().isLineaGanado()) {
			filtro = new SubAseguradoEnesaGanadoFiltro();
			((SubAseguradoEnesaGanadoFiltro) filtro).setPoliza(poliza);
			((SubAseguradoEnesaGanadoFiltro) filtro).setAsegurado(asegurado);
		} else {
			filtro = new SubAseguradoEnesaFiltro();
			((SubAseguradoEnesaFiltro) filtro).setPoliza(poliza);
			((SubAseguradoEnesaFiltro) filtro).setAsegurado(asegurado);
		}
		listaSubvEnesaAsegurado = (List<Object>) aseguradoSubvencionDao
				.getObjects(filtro);
		return listaSubvEnesaAsegurado;
	}

	/**
	 * Metodo para obtener las subvenciones de la comunidad de un asegurado
	 * 
	 * @param poliza
	 * @param asegurado
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Object> cargaSubvencionesCCAA(final Poliza poliza,
			final Asegurado asegurado) {
		List<Object> listaSubvCCAAAsegurado;
		Filter filtro;
		if (poliza.getLinea().isLineaGanado()) {
			filtro = new SubAseguradoCcaaGanadoFiltro();
			((SubAseguradoCcaaGanadoFiltro) filtro).setPoliza(poliza);
			((SubAseguradoCcaaGanadoFiltro) filtro).setAsegurado(asegurado);
		} else {
			filtro = new SubAseguradoCcaaFiltro();
			((SubAseguradoCcaaFiltro) filtro).setPoliza(poliza);
			((SubAseguradoCcaaFiltro) filtro).setAsegurado(asegurado);
		}
		listaSubvCCAAAsegurado = (List<Object>) aseguradoSubvencionDao.getObjects(filtro);
		return listaSubvCCAAAsegurado;
	}
	
	/* P00078846 ** MODIF TAM (14/02/2022) ** Inicio */
	public boolean isPrimerAcceso (Long idPoliza) {
		return this.aseguradoSubvencionDao.existeAuditAsegDatMed(idPoliza);		
	}
	
	public AseguradoDatosYMedidasDocument getSubvencionesAsegurado (String nifCifAseg, String codPlan, String codLinea, String realPath, Long idPoliza, String codUsuario) {
		
		LOGGER.debug("AseguradoSubvencionManager - getSubvencionesAsegurado[INIT]");
		WSResponse<AseguradoDatosYMedidasDocument> respuesta = null;
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			respuesta = new SWAsegDatosYMedidasHelper()
					.mostrarProximasSubvenciones(nifCifAseg, codPlan, codLinea, realPath);
			
			AseguradoDatosYMedidasDocument data = respuesta.getData();
			
			if (null != data) {
				LOGGER.debug("AseguradoSubvencionManager - guardamos en Auditoria");
				String xml = data.toString();
				this.aseguradoSubvencionDao.guardaAuditAsegDatosMedida(idPoliza, codUsuario, xml);
			}
			
		} catch (Exception e) {
			LOGGER.error("Ocurrio un error inesperado en la llamada al SW de "
					+ "AseguradoSubvencionManager -getSubvencionesAsegurado",
					e);
			parameters.put("error",
					bundleAgp.getString("mensaje.asegurado.obtenerDatos.KO"));
			
		}
		
		LOGGER.debug("AseguradoSubvencionManager - getSubvencionesAsegurado[END]");
		return respuesta.getData();
	}
	
	/**
	 * Convierte el xml recibido en un objeto ModulosYCoberturas
	 * 
	 * @param xml
	 * @return
	 */
	public ControlAccesoSubvenciones getcAySubvFromXml(String xml) {

		ControlAccesoSubvenciones cAySubv = null;

		// Convierte el xml recibido en un objeto ModulosYCoberturas
		try {
			cAySubv = AseguradoDatosYMedidasDocument.Factory.parse(xml).getAseguradoDatosYMedidas().getDatosPersonales().getControlAccesoSubvenciones();
		} catch (Exception e) {
			LOGGER.error("Ha ocurrido un error al parsar el xml a un objeto ModulosYCoberturas (explotaciones)", e);
		}

		return cAySubv;
	}
	
	/* Construimos un String con los tipo de Subvenciones obtenidos en el xml */
	public String  obtenerTipoSubvencionesXmlSw(ControlAccesoSubvenciones AySubv) {
		
		String tipoSubvXml = "";
		
		Organismo[] organismos = AySubv.getOrganismoArray();
		for(Organismo org : organismos){
			if (org.getOrganismo().equals(Constants.ORGANISMO_ENESA)) {
				Subvencion[] subvenciones = org.getSubvencionArray();
				for(Subvencion subv : subvenciones){
					String tipoSub = String.valueOf(subv.getTipo());
					tipoSubvXml += tipoSub + "/E"+ ";";
				}
			}else {
				Subvencion[] subvenciones = org.getSubvencionArray();
				for(Subvencion subv : subvenciones){
					String tipoSub = String.valueOf(subv.getTipo());
					tipoSubvXml += tipoSub + "/C"+ ";";
				}
			}
		}
		
		return tipoSubvXml;
	}	
	
	public String obtenerDetalle(String listaSubvAsegMarcadasInicialmente, String subSeleccionadas) {
		String detalle ="";
		LOGGER.debug("AseguradoSubvencionManager- obtenerDetalle [INIT]");
		LOGGER.debug("Valor de lista de Subvenciones por Asegurado - listaSubvAseg:"+listaSubvAsegMarcadasInicialmente);
		LOGGER.debug("Valor de lista de Subvenciones Seleccionadas - subSeleccionadas:"+subSeleccionadas);
		
		
		String[] ids_listaSubvAseg = listaSubvAsegMarcadasInicialmente.split(";");
		String[] ids_subSelec = subSeleccionadas.split(",");
		
			/* Recorremos la lista de Subvenciones por Asegurado para comprobar si existe en la lista de marcados*/
			for (int i=0; i<ids_listaSubvAseg.length;i++){
				
				if (!"".equals(ids_listaSubvAseg[i])) {
					if (!getSubvencionSeleccionada(ids_subSelec, ids_listaSubvAseg[i])) {
						detalle += ids_listaSubvAseg[i] + ";"; 
					}
				}
			}

		LOGGER.debug("Valor de detalle: "+detalle);
		LOGGER.debug("AseguradoSubvencionManager- obtenerDetalle [END]");
		return detalle;
	}
		
	/**
	 * 	
	 * @param ids_subSelec
	 * @param string
	 * @return
	 */
	private boolean getSubvencionSeleccionada(String[] ids_subSelec, String subvencion) {
		for (int i=0; i<ids_subSelec.length;i++){
			
			if (ids_subSelec[i].equals(subvencion)) {
				return true;
			}
		}
		
		return false;
	}

	public List<String> getControlSubvenciones(final List<String> nifsCifs, final BigDecimal codPlan, final BigDecimal codLinea)
			throws BusinessException {
		try {
			return this.aseguradoSubvencionDao.getControlSubvenciones(nifsCifs, codPlan, codLinea);
		} catch (DAOException ex) {
			throw new BusinessException(ex);
		}
	}
	
	/* Pet. 57622 ** MODIF TAM (03.06.2019) ** Fin */
	
	public void setAseguradoSubvencionDao(IAseguradoSubvencionDao aseguradoSubvencionDao) {
		this.aseguradoSubvencionDao = aseguradoSubvencionDao;
	}
	
	public void setAseguradoDao(final IAseguradoDao aseguradoDao) {
		this.aseguradoDao = aseguradoDao;
	}
	/* P00078846 ** MODIF TAM (14/02/2022) ** Fin */
}