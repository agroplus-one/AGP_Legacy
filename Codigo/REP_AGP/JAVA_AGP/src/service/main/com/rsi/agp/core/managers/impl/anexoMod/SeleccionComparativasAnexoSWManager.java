package com.rsi.agp.core.managers.impl.anexoMod; 
 
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlObject;
import org.hibernate.Hibernate;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.impl.DeclaracionesModificacionPolizaManager;
import com.rsi.agp.core.managers.impl.SeleccionComparativasSWManager;
import com.rsi.agp.core.managers.impl.anexoMod.solicitud.ISolicitudModificacionManager;
import com.rsi.agp.core.managers.impl.ganado.ContratacionAyudasHelper;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.DatoVariableComparator;
import com.rsi.agp.core.util.ModuloFilaViewComparator;
import com.rsi.agp.core.util.WSRUtils;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.anexo.IXmlAnexoModificacionDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.models.poliza.ganado.ISeleccionComparativaSWDao;
import com.rsi.agp.dao.models.poliza.ganado.SeleccionComparativaSWDao;
import com.rsi.agp.dao.tables.anexo.AnexoModSWComparativas;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.cgen.DatosBuzonGeneral;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloCeldaView;
import com.rsi.agp.dao.tables.cpl.ModuloFilaView;
import com.rsi.agp.dao.tables.cpl.ModuloValorCeldaView;
import com.rsi.agp.dao.tables.cpl.ModuloView;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaSimple;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.modulosYCoberturas.Cobertura;
import es.agroseguro.modulosYCoberturas.DatoVariable;
import es.agroseguro.modulosYCoberturas.ModulosYCoberturas;
import es.agroseguro.modulosYCoberturas.Valor;
import es.agroseguro.modulosYCoberturas.VinculacionCelda;
import es.agroseguro.serviciosweb.contratacionayudas.AgrException;
import es.agroseguro.serviciosweb.contratacionayudas.CoberturasContratadasResponse;
import es.agroseguro.serviciosweb.contratacionayudas.ModulosCoberturasResponse; 
 
public class SeleccionComparativasAnexoSWManager extends SeleccionComparativasSWManager implements ISeleccionComparativasAnexoSWManager { 
 
	private ISolicitudModificacionManager solicitudModificacionManager; 
	private DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager; 
	private IPolizaDao polizaDao; 
	private ISeleccionComparativaSWDao seleccionComparativaSWDao; 
 
	private IXmlAnexoModificacionDao xmlAnexoModDao = null;
	
	private static final Log logger = LogFactory.getLog(SeleccionComparativasAnexoSWManager.class); 
	 
	/* Pet. 63485-Fase II ** MODIF TAM (14.09.2020) ** Inicio */ 
	private final String ELEGIBLE = "E"; 
	private final String BASICA = "B"; 
	 
	@Override 
	public Map<String, Object> generarListaComparativas(long idAnexo, String realPath, Usuario usuario, boolean modoLectura) { 
		 
		Map<String, Object> respuesta = new HashMap<String, Object>(); 
		 
		// Carga los datos del anexo de modificacion a partir de su id 
		AnexoModificacion am = null; 
		try { 
			am = declaracionesModificacionPolizaManager.getAnexoModifById(idAnexo); 
		} catch (Exception e) { 
			logger.error("Error al cargar el anexo de bbdd", e); 
		} 
		 
		respuesta.put("anexo", am); 
		 
		// Obtiene el objeto ModuloPoliza correspondiente a la poliza asociada al anexo en cuestion 
		ModuloPoliza mp = getModuloPoliza (am); 
		 
		// Crea el objeto para registrar la comunicacion con el SW en BD 
		AnexoModSWComparativas amc = getAnexoModSWComparativas(usuario, am); 
		 
		if (modoLectura) { 
			 
			// Si no hay datos guardados de las coberturas del anexo o de la poliza, vuelve a la pagina mostrando el aviso correspondiente 
			if (amc.getRespuestaAnexo() == null || amc.getRespuestaPlz() == null) { 
				respuesta.put("alerta", "No hay datos de coberturas para el anexo"); 
				return respuesta; 
			} 
			 
			// Genera el cuadro de coberturas de la situacion actualizada de la poliza asociada al anexo a partir del xml almacenado en BBDD 
			respuesta.put("moduloViewPoliza",
					getModuloViewFromModulosYCoberturas(
							getMyCFromXml(WSUtils.convertClob2String(amc.getRespuestaPlz())), mp, 0,
							mp.getId().getCodmodulo(), true)); 
			 
			// Genera el cuadro de coberturas del anexo de modificacion a partir del xml almacenado en BBDD 
			respuesta.put("moduloViewAnexo",
					getModuloViewFromModulosYCoberturas(
							getMyCFromXml(WSUtils.convertClob2String(amc.getRespuestaAnexo())), mp, 1,
							mp.getId().getCodmodulo(), true));
		} else { 
			// Obtiene la situacion actualizada de la poliza asociada al anexo 
			XmlObject plzAct = solicitudModificacionManager.getPolizaActualizadaFromCupon(am.getCupon().getIdcupon()); 
			 
			// Genera el cuadro de coberturas de la situacion actualizada de la poliza asociada al anexo 
			try { 
				respuesta.put("moduloViewPoliza", getModuloViewFromModulosYCoberturas(
						getMyCPoliza(plzAct, realPath, amc), mp, 0, mp.getId().getCodmodulo(), true));
			} catch (Exception e) { 
				logger.error("Error al obtener las comparativas de la p&oacute;liza", e); 
				 
				// Si es un error controlado del SW se muestra la descripcion del mensaje en la pantalla 
				if (e instanceof AgrException) { 
					respuesta.put("alerta", WSUtils.debugAgrException(e)); 
				} 
				// Si no, se devuelve un error generico 
				else { 
					respuesta.put("alerta", "Error al obtener las coberturas contratadas de la p&oacute;liza asociada al anexo"); 
				} 
			} 
			 
			// Genera el cuadro de coberturas del anexo de modificacion 
			try { 
				respuesta.put("moduloViewAnexo", getModuloViewFromModulosYCoberturas(getMyCAnexo(realPath, am, amc), mp,
						1, mp.getId().getCodmodulo(), true));
			} catch (Exception e) { 
				logger.error("Error al obtener las comparativas del anexo", e); 
				 
				// Si es un error controlado del SW se muestra la descripcion del mensaje en la pantalla 
				if (e instanceof AgrException) { 
					respuesta.put("alerta", WSUtils.debugAgrException(e)); 
				} 
				// Si no, se devuelve un error generico 
				else { 
					respuesta.put("alerta", "Error al obtener las comparativas del anexo"); 
				} 
			} 
		} 
		 
		// Obtiene la lista de comparativas guardadas en BBDD para el anexo 
		//List<com.rsi.agp.dao.tables.anexo.Cobertura> lstCoberturasBBDD = new ArrayList<com.rsi.agp.dao.tables.anexo.Cobertura>(); 
		for (com.rsi.agp.dao.tables.anexo.Cobertura cob:am.getCoberturas()){ 
			cob.setIdComparativa(new Long(1)); 
			//lstCoberturasBBDD.add(cob); 
		} 
		respuesta.put("listaCoberturasElegidas", am.getCoberturas()); 
		 
		// Comprueba si aplica la validacion por tipo de capital de retirada 
		respuesta.put("validarTCRetirada", isAnexoTCRetirada(am.getId())); 
		 
		// Registra la comunicacion con el SW en BD 
		try { 
			getPolizaDao().saveOrUpdate(amc); 
		} catch (Exception e) { 
			logger.error("Error al guardar la comunicacion con el SW de ayudas a la contrataci&oacute;n para el anexo " + am.getId(), e); 
		} 
		
		List<DatosBuzonGeneral> lista = generarListaTipoAseguradoGanado(mp.getPoliza().getLinea().getLineaseguroid());
		
		respuesta.put("listaTipoAseguradoGanado", generarListaTipoAseguradoGanado(mp.getPoliza().getLinea().getLineaseguroid()));
		 
		return respuesta; 
	} 
	
	/**
	 * Comprueba si aplica el dato variable de cabecera 'Tipo asegurado ganado' para el plan/linea de la poliza accediendo al organizador
	 * y en caso afirmativo obtiene los valores correspondientes del buzon general 
	 * @param lineaseguroid
	 * @return
	 */
	private List<DatosBuzonGeneral> generarListaTipoAseguradoGanado(long lineaseguroid) {
		
		List<DatosBuzonGeneral> listaTipoAseguradoGanado = new ArrayList<DatosBuzonGeneral>();
		
		try {
			// Si aplica el dato variable de cabecera 'Tipo asegurado ganado' para el plan/linea
			if (seleccionComparativaSWDao.aplicaTipoAseguradoGanado(lineaseguroid)) {
				
				// Obtiene los valores correspondientes del buzon general
				listaTipoAseguradoGanado = seleccionComparativaSWDao.obtenerListaTipoAseguradoGanado();
			}
		} 
		catch (Exception e) {
			logger.error("Error al obtener el listado de tipos de asegurado de ganado al lineaseguroid " + lineaseguroid, e);
		}
		return listaTipoAseguradoGanado;
	}
 
	 
	public Map<String, Object> guardarComparativas(long idanexo, String[] infoCoberturas, String tipologia) { 
		 
		// Mapa de objetos que se devuelven al controlador 
		Map<String, Object> mapa = new HashMap<String, Object>(); 
		 
		// Genera la lista de comparativas del anexo a partir de la informacion obtenida de la pantalla y la guarda en BBDD 
		try { 
			List<ComparativaPolizaSimple> listCp = new ArrayList<ComparativaPolizaSimple>(); 
			 
			for (String infoCob : infoCoberturas) { 
				ComparativaPolizaSimple cp = creaComparativaPolizaSimple(idanexo, infoCob.split("_")); 
				if (cp != null)	listCp.add(cp); 
			} 
			 
			if (!tipologia.isEmpty()) {
				ComparativaPolizaSimple cpTipo = new ComparativaPolizaSimple();
				cpTipo.setConcepto(ConstantsConceptos.CODCPTO_TIPO_ASEG_GAN);
				cpTipo.setIdanexo(idanexo);
				cpTipo.setValor(Integer.parseInt(tipologia));
				listCp.add(cpTipo);
			}
			
			
			// Si no hay coberturas elegibles que guardar vuelve al controlador 
			if (listCp.isEmpty()) return mapa; 
			 
			seleccionComparativaSWDao.guardaListaComparativasAnexo(idanexo, listCp); 
		} 
		catch (Exception e) { 
			logger.error("Error al guardar las comparativas elegidas del anexo " + idanexo, e); 
			mapa.put("alerta", "Error al guardar las comparativas"); 
			return mapa; 
		} 
		 
		return mapa; 
	} 
	 
	/** 
	 * Genera el objeto que encapsula la informacion correspondiente a la comparativa del anexo 
	 * @param idanexo 
	 * @param info 
	 * @return 
	 */ 
	private ComparativaPolizaSimple creaComparativaPolizaSimple(long idanexo, String[] info) { 
		 
		try { 
			ComparativaPolizaSimple cp = new ComparativaPolizaSimple(); 
			cp.setIdanexo(idanexo); 
			cp.setCodmodulo(info[1]); 
			cp.setFilamodulo(new Integer (info[2])); 
			cp.setCpm(new Integer (info[3])); 
			cp.setRc(new Integer (info[4])); 
			cp.setConcepto(new Integer(info[5])); 
			cp.setFilacomparativa(new Integer (info[6])); 
			cp.setValor(new Integer (info[7])); 
			// Si viene informada la descripcion del valor 
			if (info.length == 9) cp.setDescValor(info[8]); 
			 
			return cp; 
		} catch (Exception e) { 
			logger.error("Error al crear el objeto que encapsula la informacion de la comparativa simple del anexo", e); 
			return null; 
		} 
		 
	} 
	 
	/** 
	 * Obtiene el objeto ModulosYCoberturas que devuelve el SW de ayudas a la contratacion para el anexo en cuestion 
	 * @param realPath 
	 * @param am 
	 * @return 
	 * @throws Exception  
	 */ 
	private ModulosYCoberturas getMyCAnexo (String realPath, AnexoModificacion am, AnexoModSWComparativas amc) throws Exception { 
		 
		// Obtiene el xml del anexo 
		String xml = null; 
		  
		try { 
			xml = WSUtils.generateXMLPolizaModulosCoberturas(am.getPoliza(), null, null, am.getExplotacionAnexos(), am.getCodmodulo(), getPolizaDao());			 
			 
		} catch (Exception e) { 
			logger.error("Error al genera el xml del anexo", e); 
		} 
		logger.debug(xml); 
		 
		 
		amc.setEnvioAnexo(Hibernate.createClob(xml)); 
		 
		// Llama al metodo de 'modulosCoberturas' del SW de Ayudas a la contratacion con el xml del anexo 
		ModulosCoberturasResponse response = null; 
		try { 
			response = new ContratacionAyudasHelper().doModulosCoberturas(xml, realPath); 
		} catch (Exception e) { 
			logger.error("Error al obtener las comparativas del anexo a trav&eacute;s de SW", e); 
			throw e; 
		} 
		 
		// Procesa la respuesta del servicio y genera el objeto utilizado para mostrar las comparativas para devolverlo 
		String respuesta = null; 
		try { 
			respuesta = WSUtils.getStringResponse(response.getModulosCoberturas()); 
			logger.debug(respuesta); 
		} catch (Exception e) { 
			logger.error("Error al obtener el xml de respuesta del servicio", e); 
			throw e; 
		} 
		 
		amc.setRespuestaAnexo(Hibernate.createClob(respuesta)); 
		 
		return getMyCFromXml (respuesta); 
	} 
	 
	 
 
	/** 
	 * Obtiene el objeto ModulosYCoberturas que devuelve el SW de ayudas a la contratacion para la situacion actualizada de la poliza asociada 
	 * al anexo de modificacion en cuestion 
	 * @param realPath 
	 * @param am 
	 * @return 
	 * @throws Exception  
	 */ 
	private ModulosYCoberturas getMyCPoliza(XmlObject plzAct, String realPath, AnexoModSWComparativas amc) throws Exception { 
		
		logger.debug("SeleccionComparativasAnexoSWManager- Dentro de getMyCPoliza INIT");
		
		boolean esGanado = false;
		
		if(amc.getAnexoModificacion().getPoliza().getLinea().getEsLineaGanadoCount()> 0) {
			esGanado = true;
		}
		
		logger.debug("Valor de esGanado."+esGanado);
		String xml = ""; 

		/* ESC-12461 */
		/* En Ganado. Buscamos si se encuentra ns4 para la poliza*/
		if (esGanado) {
		   logger.debug ("Entramos por esGanado");
		   
		   String plzActString = plzAct.toString();
		   logger.debug ("Valor de plzActString:"+plzActString);
		   
		   int intEncontrado = plzActString.indexOf("ns2:Poliza"); 	   
           logger.debug ("Valor de intEncontrado: "+intEncontrado);
		   
	          if(intEncontrado < 0){
	        	  logger.debug ("No lo ha encontado y no se cambia nada");
	        	  xml = plzAct.toString();
	          }else {
	        	  logger.debug ("Lo ha encontrado y lo cambiamos");
	        	  xml = plzAct.toString().replaceFirst("xmlns:ns2=\"http://www.agroseguro.es/Contratacion", "xmlns:ns2=\"http://www.agroseguro.es/PresupuestoContratacion"); 
	          }
           
           	  logger.debug("Valor de xml para Ganado:"+xml);

		}else {
			// Modifica el namespace del xml recibido para la llamada al servicio 
			xml = plzAct.toString().replaceFirst("xmlns:ns4=\"http://www.agroseguro.es/Contratacion", "xmlns:ns4=\"http://www.agroseguro.es/PresupuestoContratacion"); 
			logger.debug(xml);
		}
		/* ESC-12461 FIN */
		 
		amc.setEnvioPlz(Hibernate.createClob(xml)); 
		 
		//Llama al metodo de 'coberturasContratadas' del SW de Ayudas a la contratacion con el xml del anexo 
		CoberturasContratadasResponse response = null; 
		try { 
			response = new ContratacionAyudasHelper().doCoberturasContratadas(xml, realPath); 
		} catch (Exception e) { 
			logger.error("Error al obtener las coberturas contratadas de la poliza a traves de SW", e); 
			throw e; 
		} 
		 
		// Procesa la respuesta del servicio y genera el objeto utilizado para mostrar las comparativas para devolverlo 
		String respuesta = null; 
		try { 
			respuesta = WSUtils.getStringResponse(response.getCoberturasContratadas()); 
			logger.debug(respuesta); 
		} catch (Exception e) { 
			logger.error("Error al obtener el xml de respuesta del servicio", e); 
			throw e; 
		} 
		 
		amc.setRespuestaPlz(Hibernate.createClob(respuesta)); 
		logger.debug("SeleccionComparativasAnexoSWManager- Dentro de getMyCPoliza END");
		return getMyCFromXml (respuesta); 
	} 
	 
	 
	 
	/** 
	 * Genera el objeto 'ModuloPoliza' correspondiente al plan/linea y modulo pasados como parametro 
	 * @param lineaseguroid 
	 * @param codmodulo 
	 * @return 
	 */ 
	private ModuloPoliza getModuloPoliza (AnexoModificacion am) { 
		ModuloPoliza mp = null; 
		 
		if (null != am.getPoliza() && null != am.getPoliza().getModuloPolizas()
				&& am.getPoliza().getModuloPolizas().size() > 0) {
			mp = am.getPoliza().getModuloPolizas().iterator().next();
		}
		 
		return mp; 
	} 
	 
	/** 
	 * Obtiene el objeto AnexoModSWComparativas para registrar la comunicacion con el SW en BD 
	 * @param usuario 
	 * @param am 
	 * @return 
	 */ 
	public AnexoModSWComparativas getAnexoModSWComparativas(Usuario usuario, AnexoModificacion am) { 
		 
		if (am.getAnexoModSWComparativases() != null && !am.getAnexoModSWComparativases().isEmpty()) { 
			for (AnexoModSWComparativas amswc : am.getAnexoModSWComparativases()) { 
				amswc.setCodusuario(usuario.getCodusuario()); 
				amswc.setFecha(new Date()); 
				return amswc; 
			} 
		} 
		else { 
			return new AnexoModSWComparativas(am, usuario.getCodusuario(), new Date()); 
		} 
		 
		return null; 
	} 
	 
	 
	/** 
	 * Devuelve un booleano indicando si el anexo tiene alguna explotacion que incluya tipo de capital de retirada 
	 * @param idAnexo 
	 * @return 
	 */ 
	public boolean isAnexoTCRetirada (long idAnexo) { 
		 
		try { 
			return seleccionComparativaSWDao.aplicaValidacionCapitalRetirada(idAnexo); 
		} catch (DAOException e) { 
			logger.error("Error al comprobar si aplica la validacion de tipo de capital de retirada", e); 
		} 
		 
		return false; 
	} 
	 
	/* Pet. 63485-Fase II  MODIF TAM (09.09.2020) ** Inicio */ 
	/* Damos de alta los metodos necesarios para lanzar llamada al S.W de modulos y coberturas por REST 
	 * para las agricolas  
	 */ 
	@Override 
	public Map<String, Object> generarListaComparativasAgri(long idAnexo, String realPath, Usuario usuario, boolean modoLectura) { 
		 
		Map<String, Object> respuesta = new HashMap<String, Object>(); 
		 
		// Carga los datos del anexo de modificacion a partir de su id 
		AnexoModificacion am = null; 
		try { 
			am = declaracionesModificacionPolizaManager.getAnexoModifById(idAnexo); 
		} catch (Exception e) { 
			logger.error("Error al cargar el anexo de bbdd", e); 
		} 
		 
		respuesta.put("anexo", am); 
		 
		// Obtiene el objeto ModuloPoliza correspondiente a la poliza asociada al anexo en cuestion 
		ModuloPoliza mp = getModuloPoliza (am); 
		 
		// Crea el objeto para registrar la comunicacion con el SW en BD 
		AnexoModSWComparativas amc = getAnexoModSWComparativas(usuario, am); 
		 
		if (modoLectura) { 
			 
			// Si no hay datos guardados de las coberturas del anexo o de la poliza, vuelve a la pagina mostrando el aviso correspondiente 
			if (amc.getRespuestaAnexo() == null || amc.getRespuestaPlz() == null) { 
				respuesta.put("alerta", "No hay datos de coberturas para el anexo"); 
				return respuesta; 
			} 
			 
			// Genera el cuadro de coberturas de la situacion actualizada de la poliza asociada al anexo a partir del xml almacenado en BBDD 
			respuesta.put("moduloViewPoliza", getModuloViewFromModulosYCobertAgricolas( 
					getMyCFromXml(WSUtils.convertClob2String(amc.getRespuestaPlz())),  
							mp,0,mp.getId().getCodmodulo())); 
			 
			// Genera el cuadro de coberturas del anexo de modificacion a partir del xml almacenado en BBDD 
			respuesta.put("moduloViewAnexo", getModuloViewFromModulosYCobertAgricolas ( 
					getMyCFromXml(WSUtils.convertClob2String(amc.getRespuestaAnexo())),  
							mp,1,mp.getId().getCodmodulo())); 
		} 
		else { 
			// Obtiene la situacion actualizada de la poliza asociada al anexo 
			XmlObject plzAct = solicitudModificacionManager.getPolizaActualizadaFromCupon(am.getCupon().getIdcupon()); 
			 
			// Genera el cuadro de coberturas de la situacion actualizada de la poliza asociada al anexo 
			try { 
				respuesta.put("moduloViewPoliza", getModuloViewFromModulosYCobertAgricolas ( 
						getMyCPolizaAgricola (plzAct, realPath, amc),  
						mp,0,mp.getId().getCodmodulo())); 
			} catch (Exception e) { 
				logger.error("Error al obtener las comparativas de la p&oacute;liza", e); 
				 
				// Si es un error controlado del SW se muestra la descripcion del mensaje en la pantalla 
				if (e instanceof AgrException) { 
					respuesta.put("alerta", WSUtils.debugAgrException(e)); 
				} 
				// Si no, se devuelve un error generico 
				else { 
					respuesta.put("alerta", "Error al obtener las coberturas contratadas de la p&oacute;liza asociada al anexo"); 
				} 
			} 
			 
			// Genera el cuadro de coberturas del anexo de modificacion 
			try { 
				respuesta.put("moduloViewPoliza", getModuloViewFromModulosYCobertAgricolas ( 
						getMyCAnexoAgri(realPath, am, amc),  
						mp,1,mp.getId().getCodmodulo())); 
			} catch (Exception e) { 
				logger.error("Error al obtener las comparativas del anexo", e); 
				 
				// Si es un error controlado del SW se muestra la descripcion del mensaje en la pantalla 
				if (e instanceof AgrException) { 
					respuesta.put("alerta", WSUtils.debugAgrException(e)); 
				} 
				// Si no, se devuelve un error generico 
				else { 
					respuesta.put("alerta", "Error al obtener las comparativas del anexo"); 
				} 
			} 
		} 
		 
		// Obtiene la lista de comparativas guardadas en BBDD para el anexo 
		//List<com.rsi.agp.dao.tables.anexo.Cobertura> lstCoberturasBBDD = new ArrayList<com.rsi.agp.dao.tables.anexo.Cobertura>(); 
		for (com.rsi.agp.dao.tables.anexo.Cobertura cob:am.getCoberturas()){ 
			cob.setIdComparativa(new Long(1)); 
			//lstCoberturasBBDD.add(cob); 
		} 
		respuesta.put("listaCoberturasElegidas", am.getCoberturas()); 
		 
		// Comprueba si aplica la validacion por tipo de capital de retirada 
		respuesta.put("validarTCRetirada", isAnexoTCRetirada(am.getId())); 
		 
		// Registra la comunicacion con el SW en BD 
		try { 
			getPolizaDao().saveOrUpdate(amc); 
		} catch (Exception e) { 
			logger.error("Error al guardar la comunicacion con el SW de ayudas a la contrataci&oacute;n para el anexo " + am.getId(), e); 
		} 
		 
		return respuesta; 
	} 
	 
	 
	/** 
	 * Obtiene el objeto ModulosYCoberturas que devuelve el SW de ayudas a la contratacion para la poliza asociada 
	 * al anexo de modificacion en cuestion 
	 * @param ModuloPoliza mp,Poliza p, String realPath 
	 * @return 
	 * @throws Exception  
	 */ 
	public ModulosYCoberturas getMyCPolizaAgricola(XmlObject plzAct, String realPath, AnexoModSWComparativas amc) throws Exception { 
		 
		logger.debug("SeleccionComparativasAnexoSWManager - getMyCPolizaAgricola"); 
		logger.debug("Lanzamos llamada por REST para recuperar los Modulos y Coberturas Contratadas "); 
		 
		String xmlPoliza = null; 
		// Obtiene el xml asociado a la poliza 
		try { 
			//xmlPoliza = WSUtils.generateXMLPolizaModulosCoberturas(p, null,null,null, mp.getId().getCodmodulo(), polizaDao); 
			xmlPoliza = plzAct.toString().replaceFirst("xmlns:ns4=\"http://www.agroseguro.es/Contratacion", "xmlns:ns4=\"http://www.agroseguro.es/PresupuestoContratacion"); 
			
			xmlPoliza = xmlPoliza.replaceFirst("xmlns:ns2=\"http://www.agroseguro.es/Contratacion", "xmlns:ns2=\"http://www.agroseguro.es/PresupuestoContratacion");
			
			xmlPoliza = xmlPoliza.replaceFirst("xmlns:ns2=\"http://www.agroseguro.es/PresupuestoContratacion/Parcela", "xmlns:ns2=\"http://www.agroseguro.es/Contratacion/Parcela");
			logger.debug(xmlPoliza); 
		} catch (Exception e) { 
			logger.error("Ha ocurrido un error al obtener el xml asociado a la poliza", e); 
			throw e; 
		} 
		 
		// Llama al SW de Ayudas a la contratacion por REST para obtener el xml de coberturascontratadas 
		String xmlRespuesta = null; 
		try { 
			es.agroseguro.modulosYCoberturas.Modulo ModulosCoberturasXmlRespuesta = WSRUtils.getCoberturasContratadas(xmlPoliza); 
			xmlRespuesta = ModulosCoberturasXmlRespuesta.toString(); 
			logger.debug("getMyCPoliza:xml de respuesta del SW Ayudas contratacion doCoberturasContratadas : "+xmlRespuesta);	 
		} catch (Exception e) { 
			logger.error("Ha ocurrido un error en la llamada al SW de Ayudas a la contratacion", e); 
			throw e; 
		} 
	
		
		try { 
			
			amc.setEnvioPlz(Hibernate.createClob(xmlPoliza)); 
			amc.setRespuestaPlz(Hibernate.createClob(xmlRespuesta)); 
			
			// Registra la comunicacion con el SW en BD 
			try { 
				getPolizaDao().saveOrUpdate(amc); 
			} catch (Exception e) { 
				logger.error("Error al guardar la comunicacion con el SW de ayudas a la contrataci&oacute;n para la poliza " + amc.getId(), e); 
			}
			
		} catch (Exception e) { 
			logger.error("Ha ocurrido un error al registrar la comunicaci&oacute;n con el SW en BBDD", e); 
			throw e; 
		} 
		
		// Convierte el xml recibido en un objeto ModulosYCoberturas 
		return getMyCFromXml(xmlRespuesta); 
 
	} 
	 
	/** 
	 * Obtiene el objeto ModulosYCoberturas que devuelve el SW de ayudas a la contratacion para el anexo en cuestion 
	 * @param realPath 
	 * @param am 
	 * @return 
	 * @throws Exception  
	 */ 
	private ModulosYCoberturas getMyCAnexoAgri (String realPath, AnexoModificacion am, AnexoModSWComparativas amc) throws Exception { 
		 
		// Obtiene el xml del anexo 
		String xml = null; 
		  
		try { 
			xml = WSUtils.generateXMLPolizaModulosCoberturas(am.getPoliza(), null, null, am.getExplotacionAnexos(), am.getCodmodulo(), getPolizaDao());			 
			 
		} catch (Exception e) { 
			logger.error("Error al genera el xml del anexo", e); 
		} 
		logger.debug(xml); 
		 
		 
		amc.setEnvioAnexo(Hibernate.createClob(xml)); 
		 
		// Llama al SW de Ayudas a la contratacion por REST para obtener el xml de coberturascontratadas 
		String xmlRespuesta = null; 
		try { 
			es.agroseguro.modulosYCoberturas.Modulo ModulosCoberturasXmlRespuesta = WSRUtils.getModulosCoberturas(xml); 
			xmlRespuesta = ModulosCoberturasXmlRespuesta.toString(); 
			logger.debug("getMyCPoliza:xml de respuesta del SW Ayudas contratacion doCoberturasContratadas : "+xmlRespuesta);	 
		} catch (Exception e) { 
			logger.error("Ha ocurrido un error en la llamada al SW de Ayudas a la contratacion", e); 
			throw e; 
		} 
		 
		amc.setRespuestaAnexo(Hibernate.createClob(xmlRespuesta)); 
		 
		return getMyCFromXml (xmlRespuesta); 
	} 
	 
	/** 
	 * Metodo que devuelve un objeto FicheroMultContenido 
	 * @param idfichero 
	 * @return 
	 * @throws BusinessException 
	 */ 
	public Clob getxmlSWModyCobert(Long idpoliza, String codModulo) throws BusinessException{ 
		logger.debug("SeleccionComparativasSWManager - getxmlSWModyCobert [INIT]"); 
		 
		Clob fichero = null; 
		try { 
			fichero = seleccionComparativaSWDao.getRespuestaModulosPolizaCoberturaSW(idpoliza, codModulo, SeleccionComparativaSWDao.MODULOS_Y_COBERTURAS);
			 
		} catch (DAOException dao) { 
			logger.debug("Se ha producido un error al recuperar el fichero  :" + dao.getMessage()); 
			throw new BusinessException("Se ha producido un error al recuperar el fichero ", dao); 
		} 
		logger.debug("SeleccionComparativasSWManager - getxmlSWModyCobert [END]"); 
		return fichero; 
	} 
	 
	 
	/** Pet. 63485 ** MODIF TAM (17/07/2020) ** Inicio */ 
	/** 
	 * Procesa el objeto 'ModulosYCoberturas' recibido como parametro y genera un objeto 'ModuloView', que encapsula la informacion 
	 * que se mostrara en pantalla de las comparativas de un modulo para las Polizas AGRICOLAS 
	 * @param myc 
	 * @param mp 
	 * @return 
	 */ 
	 
	public ModuloView getModuloViewFromModulosYCobertAgricolas(final ModulosYCoberturas myc, final ModuloPoliza mp,  
			int numComparativa, String codMod) { 
		 
		logger.debug ("SeleccionComparativasSWManager - getModuloViewFromModulosYCobertAgricolas [INIT]"); 
		 
		ModuloView mv = new ModuloView(); 
		 
		// Obtiene el objeto modulo correspondiente al plan/linea y modulo indicados 
		Modulo modulo =super.moduloManager.getModulo(mp.getId().getCodmodulo()/*codMod*/ , mp.getId().getLineaseguroid()); 
		 
		// Establece el codigo, la descripcion, el indicador de renovable del modulo y el tipo de asegurado 
		mv.setCodModulo(codMod); 
		mv.setDescripcionModulo(modulo != null ? modulo.getDesmodulo() : ""); 
		 
		mv.setIdModulo(mp.getId().getNumComparativa()); 
		mv.setTipoAsegGanado(mp.getTipoAsegGanado()); 
		mv.setNumComparativa(numComparativa); 
		 
		// Si no existe el elemento 'Modulo' en el xml no continua el proceso 
		if (myc.getModuloArray().length == 0) return mv; 		 
		 
		// Recorre la lista de coberturas 
		Cobertura[] coberturaArray = myc.getModuloArray(0).getCoberturaArray();				 
		 
		List<ModuloFilaView> listaFilas = new ArrayList<ModuloFilaView>(); 
		List<DatoVariable> lstCabVariables = new ArrayList<DatoVariable>(); 
		List<Integer> codCptos = new ArrayList<Integer>(); 
		List<String> listCabs = new ArrayList<String>(); 
 
		// buscamos max cabeceras de cada cobertura 
		logger.debug("Recorremos bucle Coberturas");		 
		 
		//// CABECERAS DE LAS COBERTURAS  
		/// -------------------------------------- 
		for (Cobertura cobertura : coberturaArray) { 
			//logger.debug("Valor de cobertura:"+cobertura); 
			for (DatoVariable dv : cobertura.getDatoVariableArray()) { 
				if (!codCptos.contains(dv.getCodigoConcepto())){ 
					lstCabVariables.add(dv); 
					codCptos.add(dv.getCodigoConcepto()); 
				} 
			} 
		} 
		if (mv.getListaCabeceras().isEmpty()) 
			mv.setListaCabeceras(listCabs); 
		 
		 
		Collections.sort(lstCabVariables, new DatoVariableComparator()); 
		for (DatoVariable dvCab : lstCabVariables) { 
			listCabs.add(dvCab.getNombre()); 
		} 
		 
		//// DETALLE DE LAS COBERTURAS  
		/// -------------------------------------- 
		for (Cobertura cobertura : coberturaArray) { 
			 
			// Compone el objeto 'ModuloFilaView' que encapsula la informacion de una fila del cuadro de comparativas 
			ModuloFilaView mfv = getModuloFilaView(cobertura); 
			 
			// Compone la lista de celdas variables asociadas a la fila del cuadro de comparativas  
			mfv.setCeldas(getListaCeldasAgri(cobertura, lstCabVariables)); 
			 
			listaFilas.add (mfv); 
		} 
		 
		 
		Collections.sort(listaFilas, new ModuloFilaViewComparator()); 
		mv.setListaFilas(listaFilas); 
		 
		logger.debug ("SeleccionComparativasSWManager - getModuloViewFromModulosYCobertAgricolas [END]"); 
		 
		return mv; 
	} 
	 
	/** 
	 * @param cobertura 
	 * @param mfv 
	 */ 
	private ModuloFilaView getModuloFilaView(Cobertura cobertura) { 
		 
		ModuloFilaView mfv = new ModuloFilaView(); 
		 
		// Compone la lista de celdas fijas asociadas a la fila del cuadro de comparativas (CPM y RC) 
		mfv.setConceptoPrincipalModulo(cobertura.getDescripcionCPM()); 
		mfv.setRiesgoCubierto(cobertura.getDescripcionRC()); 
		mfv.setCodConceptoPrincipalModulo(new BigDecimal (cobertura.getConceptoPrincipalModulo())); 
		mfv.setCodRiesgoCubierto(new BigDecimal (cobertura.getRiesgoCubierto())); 
		mfv.setRcElegible("S".equals(cobertura.getElegible().toString())); 
		mfv.setBasica(BASICA.equals(cobertura.getTipoCobertura()));		 
		mfv.setFilamodulo(new BigDecimal (cobertura.getFila())); 
		//mfv.setCodCptoRCE(Constants.CODCONCEPTO_RIESGO_CUBIERTO_ELEGIDO); 
		mfv.setFilaComparativa(new BigDecimal (1)); 
		 
		return mfv; 
	} 
	 
	/** 
	 * @param cobertura 
	 * @return 
	 */ 
	private List<ModuloCeldaView> getListaCeldasAgri(Cobertura cobertura,List<DatoVariable> lstCabVariables) { 
		List<ModuloCeldaView> celdas = new ArrayList<ModuloCeldaView>(); 
		boolean existe = false; 
		 
		for (DatoVariable dvCab : lstCabVariables) { 
			existe = false; 
			for (DatoVariable dv : cobertura.getDatoVariableArray()) { 
				if (dvCab.getNombre().equals(dv.getNombre())){ 
					existe = true; 
					ModuloCeldaView mcv = new ModuloCeldaView(); 
					 
					// Codigo de concepto 
					mcv.setCodconcepto(new BigDecimal(dv.getCodigoConcepto())); 
 
					// Este valor se corresponde con la fila comparativa de la tabla  
					mcv.setColumna(new BigDecimal(dv.getColumna())); 					 
					 
					// Indica si la celda tiene valor/es elegible/s (E - Si, otro valor - No) 
					mcv.setElegible(ELEGIBLE.equals(dv.getTipoValor())); 
					 
					// Valor/es 
					List<ModuloValorCeldaView> valores = new ArrayList<ModuloValorCeldaView>(); 
					for (Valor valor : dv.getValorArray()) { 
						 
						ModuloValorCeldaView mvcv = new ModuloValorCeldaView(valor.getValor(), valor.getDescripcion()); 
						 
						/* Pet. 63485-Fase II ** MODIF TAM (14.09.2020) ** Inicio */ 
						mvcv.setColumna(new BigDecimal (dv.getColumna())); 
						/* Pet. 63485-Fase II ** MODIF TAM (14.09.2020) ** Fin */ 
						 
						/* Pet. 63485 ** MODIF TAM (29.07.2020) ** Inicio */ 
						/* Anhadimos las Vinculaciones de las celdas en caso de que las tenga */ 
						VinculacionCelda vc = valor.getVinculacionCelda();						 
						if (vc != null) { 
							//logger.debug("Tiene vinculaciones"); 
							 
							/* El codigo de la columna Madre de la que esta vinculada */ 
							BigDecimal columnaVinculada = new BigDecimal(valor.getVinculacionCelda().getColumnaMadre()); 
							mvcv.setColumnaVinculada(columnaVinculada); 
							 
							/* El codigo de la fila madre de la que esta vinculada */ 
							BigDecimal filaVinculada = new BigDecimal(valor.getVinculacionCelda().getFilaMadre()); 
							mvcv.setFilaVinculada(filaVinculada); 
							 
							/* El valor de la Fila Madre al que esta vinculado*/ 
							BigDecimal filaSelec = new BigDecimal (valor.getVinculacionCelda().getValorMadre()); 
							mvcv.setFila(filaSelec); 
							 
						}else { 
							//logger.debug("** NO TIENE VINCULACIONES "); 
						} 
						 
						/* Pet. 63485 ** MODIF TAM (29.07.2020) ** Fin */ 
						 
						valores.add(mvcv); 
					} 
					Collections.sort(valores, new Comparator<ModuloValorCeldaView>() { 
						@Override 
						public int compare(ModuloValorCeldaView arg0, ModuloValorCeldaView arg1) { 
							return arg0.getDescripcion().compareToIgnoreCase(arg1.getDescripcion()); 
						} 
					}); 
					mcv.setValores(valores); 
					 
					// Observaciones 
					if (!"".equals(StringUtils.nullToString(dv.getObservaciones()))) { 
						mcv.setObservaciones(dv.getObservaciones()); 
					} 
					celdas.add(mcv); 
				} 
			} 
			if (!existe){ // pintar una celda en blanco 
				logger.debug("celda en blanco"); 
				ModuloCeldaView mcv = new ModuloCeldaView(); 
				mcv.setObservaciones(""); 
				celdas.add(mcv); 
			} 
		} 
		return celdas; 
	} 
	 
	/** 
	 * Genera el xml asociado a la poliza, llama al metodo 'modulosCoberturas' del SW de Ayudas a la contratacion por REST y almacena 
	 * la comunicacion con el servicio en la BBDD (tb_envios_sw_mod_y_coberturas) 
	 * @param mp Objeto 'ModuloPoliza' utilizado para obtener el modulo que se esta tratando y para almacenar la comunicacion con el SW en BBDD 
	 * @param p Poliza de la cual se quieren obtener las comparativas 
	 * @param realPath Ruta a los wsdl para las llamadas al SW 
	 * @param usuario Usuario que inicia la accion 
	 * @return Objeto 'ModulosYCoberturas' que encapsula la respuesta del SW  
	 * @throws Exception  
	 */ 
	public ModulosYCoberturas getModulosYCoberturasAgriSW (ModuloPoliza mp, Poliza p, String realPath, Usuario usuario, String codmodulo, AnexoModificacion anexM) throws Exception { 
		 
		logger.debug("Obteniendo el XML de ModulosYCoberturas del SW para el m&oacute;dulo " + /*mp.getId().getCodmodulo()*/ codmodulo); 
		 
		// Obtiene el xml asociado a la poliza 
		String xmlPoliza = null; 
		try { 
			/***DNF PET.63485.FIII 19/01/2021 le paso el codmodulo solicitado en el combo que es el recibido por par�metros*/
			Map<BigDecimal, List<String>> listaDatosVariables = new HashMap<BigDecimal, List<String>>();
			try {
				listaDatosVariables = xmlAnexoModDao.getDatosVariablesParcelaRiesgo(anexM);
			} catch (BusinessException e1) {
				logger.error(
						"Error al obtener la lista de datos variables dependientes del riesgo y cpm",
						e1);
			}
			
			xmlPoliza = WSUtils.generateXMLPolizaModulosCoberturasAgriAnx(anexM, p, null, null , anexM.getParcelas(), codmodulo, polizaDao, listaDatosVariables);
			/***fin DNF PET.63485.FIII 19/01/2021 */
		} catch (Exception e) { 
			logger.error("Ha ocurrido un error al obtener el xml asociado a la p&oacute;liza", e); 
			throw e; 
		} 
		 
		// Llama al SW de Ayudas a la contratacion para obtener el xml de ModulosYCoberturas 
		String xmlRespuesta = null; 
		try { 
		 
			//XmlObject polizaXML = null; 
			//polizaXML = XmlObject.Factory.parse(xmlPoliza); 
			 
			//es.agroseguro.presupuestoContratacion.Poliza poliza = ((PolizaDocument) polizaXML).getPoliza(); 
			 
			es.agroseguro.modulosYCoberturas.Modulo ModulosCoberturasXmlRespuesta = WSRUtils.getModulosCoberturas(xmlPoliza); 
			xmlRespuesta = ModulosCoberturasXmlRespuesta.toString(); 
			 
		} catch (Exception e) { 
			logger.error("Ha ocurrido un error en la llamada al SW de Ayudas a la contrataci&oacute;n", e); 
			throw e; 
		} 
		 
		// Registra la comunicacion con el SW en BBDD 
		try { 
			//InsertCallSW(mp, usuario, xmlPoliza, xmlRespuesta); 
			
			//AnexoModificacion am = declaracionesModificacionPolizaManager.getAnexoModifById(mp.getPoliza().getAnexoModificacions()); 
			AnexoModSWComparativas amc = getAnexoModSWComparativas(usuario, anexM);
			amc.setEnvioAnexo(Hibernate.createClob(xmlPoliza)); 
			amc.setRespuestaAnexo(Hibernate.createClob(xmlRespuesta)); 
			
			// Registra la comunicacion con el SW en BD 
			try { 
				getPolizaDao().saveOrUpdate(amc); 
			} catch (Exception e) { 
				logger.error("Error al guardar la comunicacion con el SW de ayudas a la contrataci&oacute;n para el anexo " + anexM.getId(), e); 
			}
			
			
			
			
			
		} catch (Exception e) { 
			logger.error("Ha ocurrido un error al registrar la comunicaci&oacute;n con el SW en BBDD", e); 
			throw e; 
		} 
		 
		// Convierte el xml recibido en un objeto ModulosYCoberturas 
		return getMyCFromXml(xmlRespuesta); 
	} 
 
	/* Pet. 63485-Fase II ** MODIF TAM (15.07.2020) ** Fin */ 
 
	/** 
	 * Setter para Spring 
	 */ 
	public void setSeleccionComparativaSWDao(ISeleccionComparativaSWDao seleccionComparativaSWDao) { 
		this.seleccionComparativaSWDao = seleccionComparativaSWDao; 
	} 
 
	public void setSolicitudModificacionManager(ISolicitudModificacionManager solicitudModificacionManager) { 
		this.solicitudModificacionManager = solicitudModificacionManager; 
	} 
 
	public void setDeclaracionesModificacionPolizaManager(DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager) { 
		this.declaracionesModificacionPolizaManager = declaracionesModificacionPolizaManager; 
	} 
	 
	/* Pet. 63485-Fase II ** MODIF TAM (14.09.2020) ** Inicio */ 
	
	public void setPolizaDao(IPolizaDao polizaDao) { 
		this.polizaDao = polizaDao; 
	} 
	public IPolizaDao getPolizaDao() { 
		return polizaDao; 
	} 
	/* Pet. 63485-Fase II ** MODIF TAM (14.09.2020) ** Fin */ 
 
	public void setXmlAnexoModDao(IXmlAnexoModificacionDao xmlAnexoModDao) {
		this.xmlAnexoModDao = xmlAnexoModDao;
	}
	 
} 
