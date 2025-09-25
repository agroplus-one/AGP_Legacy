package com.rsi.agp.core.manager.impl.anexoRC.calculo;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.hibernate.Hibernate;
import org.w3._2005._05.xmlmime.Base64Binary;
import org.w3c.dom.Node;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.manager.impl.anexoRC.SWAnexoRCHelper;
import com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital.PolizaReduccionCapital;
import com.rsi.agp.core.managers.impl.ParametrizacionManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.managers.impl.PolizasPctComisionesManager;
import com.rsi.agp.core.managers.impl.SeleccionPolizaManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.PolizaUnificadaTransformer;
import com.rsi.agp.core.util.ReduccionCapitalTransformer;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.VistaImportes;
import com.rsi.agp.core.webapp.util.VistaImportesPorGrupoNegocio;
import com.rsi.agp.dao.models.comisiones.IPolizasPctComisionesDao;
import com.rsi.agp.dao.models.poliza.IAnexoModificacionDao;
import com.rsi.agp.dao.models.poliza.IReduccionCapitalDao;

import com.rsi.agp.dao.tables.comisiones.Descuentos;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizaPctComisiones;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalBonifRecargos;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalBonifRecargosId;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalDistribucionCostes;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalDistribucionCostesId;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalSWCalculo;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalSubvCCAA;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalSubvCCAAId;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalSubvEnesa;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalSubvEnesaId;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;

import es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio;
import es.agroseguro.contratacion.costePoliza.CostePoliza;
import es.agroseguro.contratacion.explotacion.CapitalAsegurado;
import es.agroseguro.contratacion.explotacion.ExplotacionDocument;
import es.agroseguro.contratacion.explotacion.GrupoRaza;
import es.agroseguro.iTipos.Gastos;
import es.agroseguro.seguroAgrario.diferenciasCosteUnificado.DiferenciasCostePolizasOriginalModificada;
import es.agroseguro.seguroAgrario.diferenciasCosteUnificado.DiferenciasCosteUnificado;
import es.agroseguro.seguroAgrario.diferenciasCosteUnificado.DiferenciasCosteUnificadoDocument;


public class CalculoRCManager implements ICalculoRCManager {
	
	private static final String MUESTRA_BOTON_RECARGOS = "muestraBotonRecargos";
	private static final String MUESTRA_BOTON_DESCUENTOS = "muestraBotonDescuentos";
	private static final String DIFERENCIAS_COSTE = "diferenciasCoste";
	private static final String TIPO_DC = " tipoDc: ";
	private static final String CALCULO_MODIFICACION = "calculoModificacion";
	private static final String RED_CAPITAL = "redCapital";
	private static final String ALERTA = "alerta";
	
	private static final Log logger = LogFactory.getLog(CalculoRCManager.class);
	private ParametrizacionManager parametrizacionManager;
	private SeleccionPolizaManager seleccionPolizaManager;
	private IReduccionCapitalDao reduccionCapitalDao;
//	private ISolicitudModificacionManager solicitudModificacionManager;
	private PolizasPctComisionesManager polizasPctComisionesManager;
//	private IPolizasPctComisionesDao polizasPctComisionesDao;
	private PolizaManager polizaManager;
//
//	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
//	
	private final int TIPO_DC_CALCULO_MODIF = 0; // Identificador de distribucion de costes de tipo 'Calculo de modificacion'
	private final int TIPO_DC_DIFERENCIA = 1; // Identificador de distribucion de costes de tipo 'Diferencia respecto de la poliza'
	private final int MAX_MSG_ERROR = 500; // Tamanho miximo del mensaje de error que se registre en la tabla que almacena las comunicaciones con el servicio
//	
//	/**
//	 * Llama al SW de calculo de modificacion para el anexo indicado por parametros y devuelve un mapa de objetos con la respuesta
//	 * @param realPath Ruta para acceder al .wsdl del SW de modificacion
//	 * @param idAnexo Identificador del anexo que se va a calcular
//	 * @param codUsuario Codigo de usuario que inicia el calculo del anexo
//	 * @return Mapa de objetos con la respuesta del servicio o el mensaje de error en caso de que ocurra
//	 */ 
	public Map<String, Object> calcularModificacion (final String realPath, final long idRC, final Usuario usuario, final boolean actualizaComMediadora) {
		
		Map<String, Object> resultado = new HashMap<String, Object>();
		
		// Cargar el anexo asociado al id recibido para obtener los parametros necesarios en la llamda al SW  
		ReduccionCapital rc = (ReduccionCapital) reduccionCapitalDao.getObject(ReduccionCapital.class, idRC);
		
		// Obtiene el indicador de calculo de situacion actual de la parametrizacion 
		//boolean calcularSituacionActual = getCalcularSituacionActual();
		boolean calcularSituacionActual = true;
		PolizaReduccionCapital polDoc = null;
		
		String xmlStr = "";
		
		try {
			
			xmlStr = WSUtils.convertClob2String(rc.getXml());
			xmlStr = xmlStr.replace("xml-fragment", "ns2:PolizaReduccionCapital");
			//polDoc = ReduccionCapitalTransformer.transformar(rc);
			//polDoc = es.agroseguro.contratacion.PolizaDocument.Factory.parse(new StringReader(xmlStr));

		} catch (Exception e) {
			logger.error(" Error al parsear Poliza: "
					+ e);
		}
		
			
		// Obtiene el calculo de la modificacion llamando al SW
		Base64Binary base64 = procesarXML(xmlStr);
		Map<String, Object> respuestaSW = null;
		try {
			respuestaSW = new SWAnexoRCHelper().calculoModificacionCuponActivoRC(realPath,
					rc.getCupon().getIdcupon(), calcularSituacionActual, base64);
		}catch(Exception ex) {
			logger.error("Error al llamar al SW de cálculo: " + ex);
		}

		// Registrar la comunicacion con el SW en BD
		registrarComunicacionSW(usuario.getCodusuario(), rc, Hibernate.createClob(xmlStr),
				calcularSituacionActual, respuestaSW);
		
		// Si el SW ha devuelto algun error controlado, se vuelve a la pantalla para mostrarlo
		if (respuestaSW.containsKey(CalculoRCManager.ALERTA)) {
			resultado.put(CalculoRCManager.ALERTA, respuestaSW.get(CalculoRCManager.ALERTA));
			
			// Envia el anexo actual a la pagina para los formulario de redireccion
			resultado.put(CalculoRCManager.RED_CAPITAL, rc);
			
			return resultado;
		}
				
		List<RedCapitalDistribucionCostes> lstRCDistCostes = null;
		List<RedCapitalDistribucionCostes> lstRCDifCostes = null;

//		// Formato clasico

		
		// Comprueba si la poliza asociada se ha financiado
		boolean isPlzFinanciada = isPlzFinanciada(rc.getPoliza());
		logger.debug("La poliza asociada a la reduccion es financiada: " + isPlzFinanciada);
		
		// Transforma el resultado del calculo devuelto por el SW en una distribucion de costes de anexos y la inserta en BD
		lstRCDistCostes = transformarYGuardarDistribucionCostes(rc, respuestaSW, calcularSituacionActual, isPlzFinanciada);
		// Completa el objeto distribucion de costes antes de mostrarlo en la pagina
		completarDistribucionCostes (lstRCDistCostes);
		
		// Transforma la diferencia de costes devuelta por el SW en una distribucion de costes de anexos y la inserta en BD
		lstRCDifCostes = transformarYGuardarDiferenciaCostes(rc, respuestaSW, isPlzFinanciada);
		// Completa el objeto distribucion de costes antes de mostrarlo en la pagina
		completarDistribucionCostes (lstRCDifCostes);
//		
//		
//		// Obtiene el desglose de comisiones que aplican al A.M y lo envia a la pagina para su visualizacion
		resultado.put("desgloseComisiones", obtenerComisionesRC(usuario, rc, lstRCDistCostes));
//
//		// Envia el objeto a la pagina para su visualizacion
		resultado.put("distribucionCostes", lstRCDistCostes);
//		
//		// Envia el objeto a la pagina para su visualizacion
		resultado.put("diferenciaCostes", lstRCDifCostes);
//		
//		// Envia el anexo actual a la pagina para los formulario de redireccion
		resultado.put(CalculoRCManager.RED_CAPITAL, rc);
		
		return resultado;
	}
	
	
	public void setReduccionCapitalDao(IReduccionCapitalDao reduccionCapitalDao) {
		this.reduccionCapitalDao = reduccionCapitalDao;
	}


	//
	/**
	 * Comprueba si la poliza recibida como parametro es financiada
	 * @param p Poliza a comprobar
	 * @return Boolean
	 */
	private boolean isPlzFinanciada(Poliza p) {
		try {
			DistribucionCoste2015 dc = this.polizaManager.getDistCosteSaeca(p);
			return (dc != null && dc.getImportePagoFracc() != null && dc.getOpcionFracc() != null && dc.getPeriodoFracc() != null);
		} catch (Exception e) {
			logger.error("Error al comprobar si la poliza asociada al anexo es financiada", e);
		}
		
		return false;
	}
//
	/**
	 * Obtiene el desglose de comisiones que aplica al RC recibido como parametro
	 * @param usuario Usuario conectado (utilizado para comprobar los permisos que tiene para visualizar comisiones)
	 * @param am AM en custion
	 * @param lstAmDistCostes Lista de distribuciones de coste del AM recibidas en la llamada al SW de calculo
	 * @return Objeto VistaImportes que encapsula el desglose de comisiones
	 */
	private VistaImportes obtenerComisionesRC(final Usuario usuario, ReduccionCapital rc,	List<RedCapitalDistribucionCostes> lstRCDistCostes) {
		
		VistaImportes vistaImportes = new VistaImportes();
		
		List<VistaImportesPorGrupoNegocio> listaVIPGN = new ArrayList<VistaImportesPorGrupoNegocio>();
		for (RedCapitalDistribucionCostes rcDC : lstRCDistCostes) {
			VistaImportesPorGrupoNegocio vipgn = new VistaImportesPorGrupoNegocio();
			vipgn.setCodGrupoNeg(rcDC.getGrupoNegocio().toString());
			vipgn.setPrimaNetaB(rcDC.getPrimaComercialNeta());
			vipgn.setDescGrupNeg(rcDC.getDescGrupoNegocio());
			listaVIPGN.add(vipgn);
		}
		
		vistaImportes.setVistaImportesPorGrupoNegocio(listaVIPGN);
		
		return this.polizasPctComisionesManager.dameComisiones(vistaImportes, rc.getPoliza(), usuario, null);
	}
//	
//	/**
//	 * procesa el xml para posteriormente llamar al servicio de calculo del anexo, segun las siguientes opciones:
//	 * CASO 1 	mismos grupos de negocio tanto en la situacion actualizada como en el anexo: no se realiza ninguna modificacion
//	 * CASO 2 	En la poliza no se incluyo RyD y el usuario la incluye por anexo de modificacion: se anhaden los gastos de RyD al anexo
//	 * CASO 3 	En la poliza se incluia Vida y RyD y el usuario elimina RyD mediante anexo de modificacion: se quitan los gastos de RyD en el anexo
//	 * @param polAnexoDoc polizaDocument del anexo
//	 * @param am anexomodificacion
//	 */
//	@SuppressWarnings("unchecked")
//	private void procesarXMLGrupoNegocio(es.agroseguro.contratacion.PolizaDocument polAnexoDoc, AnexoModificacion am ) throws Exception{
//		try{
//			am.getPoliza().getSetPolizaPctComisiones();
//			List<Character> lstGN = new ArrayList<Character>();
//			List<Character> lstGNActual = new ArrayList<Character>();
//			// Obtiene el objeto Poliza correspondiente al xml de la situacion actualizada
//			es.agroseguro.contratacion.Poliza pActualizada = ((es.agroseguro.contratacion.PolizaDocument) solicitudModificacionManager
//					.getPolizaActualizadaFromCupon(am.getCupon().getIdcupon())).getPoliza();
//			es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio [] costeGNActualArray =pActualizada.getCostePoliza().getCosteGrupoNegocioArray();
//			for (es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio costeGNActual:costeGNActualArray) {
//				logger.debug(" Grupo de negocio Pol.Actualizada:" +costeGNActual.getGrupoNegocio());
//				lstGNActual.add(costeGNActual.getGrupoNegocio().charAt(0));
//			}
//	
//			// ANEXO
//			es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio [] costeGNArray =polAnexoDoc.getPoliza().getCostePoliza().getCosteGrupoNegocioArray();
//			for (es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio costeGN:costeGNArray) {
//				logger.debug(" Grupo de negocio Anexo:" +costeGN.getGrupoNegocio());
//				lstGN.add(costeGN.getGrupoNegocio().charAt(0));
//			}	
//			logger.debug("entramos al 1 for");
//			//CASO 2 	En la poliza no se incluyo RyD y el usuario la incluye por anexo de modificacion: 
//			boolean  yaTieneRyD = comprobarRyDGastosAnexo(polAnexoDoc.getPoliza().getEntidad().getGastosArray());
//			
//		
//			if (!yaTieneRyD){
//				PolizaPctComisiones pc = new PolizaPctComisiones();
//				logger.debug("lstGN size: "+lstGN.size());
//				logger.debug("lstGNActual size: "+lstGNActual.size());
//				if (lstGN.contains(Constants.GRUPO_NEGOCIO_RYD) && !lstGNActual.contains(Constants.GRUPO_NEGOCIO_RYD)){
//					// BUSCAMOS LOS GASTOS DE BBDDD Y LOS ANHADIMOS
//					List<PolizaPctComisiones> lstPct= (List<PolizaPctComisiones>) anexoModificacionDao.getObjects(PolizaPctComisiones.class, "poliza.idpoliza", am.getPoliza().getIdpoliza());
//					// Buscamos las pct comisiones de grupo negocio RyD
//					for (PolizaPctComisiones pct:lstPct) {
//						if (pct.getGrupoNegocio().equals(Constants.GRUPO_NEGOCIO_RYD)){
//							if (pct.getId() != null)
//								pc.setId(pct.getId());
//							if (pct.getDescGrupoNegocio() != null)
//								pc.setDescGrupoNegocio(pct.getDescGrupoNegocio());
//							if (pct.getEstado() != null)
//								pc.setEstado(pct.getEstado());
//							if (pct.getGrupoNegocio() != null)
//								pc.setGrupoNegocio(pct.getGrupoNegocio());
//							if (pct.getPctadministracion() != null)
//								pc.setPctadministracion(pct.getPctadministracion());
//							if (pct.getPctadquisicion() != null)
//								pc.setPctadquisicion(pct.getPctadquisicion());
//							if (pct.getPctcommax() != null)
//								pc.setPctcommax(pct.getPctcommax());
//							if (pct.getPctdescelegido() != null)
//								pc.setPctdescelegido(pct.getPctdescelegido());
//							if (pct.getPctdescmax() != null)
//								pc.setPctdescmax(pct.getPctdescmax());
//							if (pct.getPctentidad() != null)
//								pc.setPctentidad(pct.getPctentidad());
//							if (pct.getPctesmediadora() != null)
//								pc.setPctesmediadora(pct.getPctesmediadora());
//							if (pct.getPctrecarelegido() != null)	
//								pc.setPctrecarelegido(pct.getPctrecarelegido());
//							if (pct.getPoliza() != null)
//								pc.setPoliza(pct.getPoliza());
//						}
//					}
//					logger.debug("Salimos del 1 for");
//					es.agroseguro.contratacion.Entidad ent = polAnexoDoc.getPoliza().getEntidad();
//					BigDecimal comMediador = PolizaUnificadaTransformer.obtenerComisionMediador(pc, true, null);
//					es.agroseguro.iTipos.Gastos [] gastosOrg = ent.getGastosArray();
//					es.agroseguro.iTipos.Gastos [] gastosFinal = new  es.agroseguro.iTipos.Gastos [gastosOrg.length];
//					logger.debug("despues del comMediador");
//					// Amhadimos los gastos RyD
//					es.agroseguro.iTipos.Gastos gas = es.agroseguro.iTipos.Gastos.Factory.newInstance();
//					gas.setGrupoNegocio(Constants.GRUPO_NEGOCIO_RYD.toString());
//					gas.setAdministracion(pc.getPctadministracion());
//					gas.setAdquisicion(pc.getPctadquisicion());
//					gas.setComisionMediador(comMediador);
//					int k = 0;
//					for (int i= 0; i<gastosOrg.length; i++) {
//						logger.debug("dentro del for: i: " + i);
//						logger.debug("gastosOrg[i] " + gastosOrg[i]);
//						gastosFinal[i] = gastosOrg[i];
//						
//						k++;
//					}
//					gastosFinal[k] = gas;	
//					ent.setGastosArray(gastosFinal);
//					
//				}
//				logger.debug("FIN");
//			}
//			//CASO 3 	En la poliza se incluia Vida y RyD y el usuario elimina RyD mediante anexo de modificacion
//			if ((lstGNActual.contains(Constants.GRUPO_NEGOCIO_RYD)  && lstGNActual.contains(Constants.GRUPO_NEGOCIO_VIDA)) 
//					&& !lstGN.contains(Constants.GRUPO_NEGOCIO_RYD)){
//				// Quitamos los gastos RyD
//				
//				es.agroseguro.contratacion.Entidad ent = polAnexoDoc.getPoliza().getEntidad();
//				es.agroseguro.iTipos.Gastos [] gastosOrg = ent.getGastosArray();
//				es.agroseguro.iTipos.Gastos [] gastosFinal=new  es.agroseguro.iTipos.Gastos [gastosOrg.length];
//				logger.debug("gastosFinal. length: " + gastosFinal.length);
//				int j = 0;
//				logger.debug("gastosOrg. length: " + gastosOrg.length);
//				for (int i= 0; i<gastosOrg.length; i++) {
//					if (gastosOrg[i].getGrupoNegocio().equals(Constants.GRUPO_NEGOCIO_VIDA.toString())) {
//						gastosFinal[j] = gastosOrg[i];
//						j++;
//					}
//				}
//				ent.setGastosArray(gastosFinal);
//			}
//		}catch (Exception e) {
//			logger.error("ERROR procesarXMLGrupoNegocio", e);
//			throw e;
//		}
//	}
//	
//	public boolean comprobarRyDGastosAnexo(es.agroseguro.iTipos.Gastos [] gastosOrg){
//		boolean yaTieneRyD = false;
//		for (int i= 0; i<gastosOrg.length; i++) {
//			if (gastosOrg[i].getGrupoNegocio().equals(Constants.GRUPO_NEGOCIO_RYD.toString())) {
//				yaTieneRyD = true;
//				break;
//			}
//		}
//		return yaTieneRyD;
//		
//	}
//	
//	private void actualizaComMediadora(es.agroseguro.contratacion.PolizaDocument polAnexoDoc, AnexoModificacion am ) throws Exception{
//		try{
//			
//			// BUSCAMOS LOS GASTOS DE BBDDD Y LOS ANHADIMOS
//			List<PolizaPctComisiones> comisionesActualizadas = obtenerPorcentajesComisiones(am);
//			
//			// MPM
//			//Gastos[] gastosFinal = new Gastos[nuevaLstPct.size()];
//			List<Gastos> listaGastos = new ArrayList<Gastos>();
//			
//			//int posArray = 0;
//			
//			for (PolizaPctComisiones comision : comisionesActualizadas) {
//				// Obtiene el valor de comision de mediador para el grupo de negocio correspondiente
//				BigDecimal comMediador = PolizaUnificadaTransformer.obtenerComisionMediador(comision, true, null);
//				
//				// Crea el objeto 'Gastos' correspondiente al grupo de negocio
//				Gastos gasto = Gastos.Factory.newInstance();
//				gasto.setGrupoNegocio(comision.getGrupoNegocio().toString());
//				gasto.setAdministracion(comision.getPctadministracion().setScale(2, BigDecimal.ROUND_DOWN));
//				gasto.setAdquisicion(comision.getPctadquisicion().setScale(2, BigDecimal.ROUND_DOWN));
//				gasto.setComisionMediador(comMediador);
//				
//				listaGastos.add(gasto);
//				
//				// Se inserta el nuevo objeto 'Gastos' en el array de gastos
//				//gastosFinal[posArray] = gas;
//				//posArray++;
//				
//				// Se actualizan los datos del anexo
//				this.updatePorcentajesAnexo(gasto, am.getId());
//				
//				// Se actualiza el objeto anexo
//				if (Constants.GRUPO_NEGOCIO_RYD.equals(comision.getGrupoNegocio())) {
//					am.setPctadministracion(gasto.getAdministracion());
//					am.setPctadquisicion(gasto.getAdquisicion());
//					am.setPctcomisionmediador(gasto.getComisionMediador());
//				} else if (Constants.GRUPO_NEGOCIO_VIDA.equals(comision.getGrupoNegocio())) {
//					am.setPctadministracionResto(gasto.getAdministracion());
//					am.setPctadquisicionResto(gasto.getAdquisicion());
//					am.setPctcomisionmediadorResto(gasto.getComisionMediador());
//				}
//			}
//			
//			Gastos[] gastosFinal = listaGastos.toArray(new Gastos[listaGastos.size()]);
//			polAnexoDoc.getPoliza().getEntidad().setGastosArray(gastosFinal);
//			
//			//es.agroseguro.contratacion.Entidad ent = polAnexoDoc.getPoliza().getEntidad();
//			//ent.setGastosArray(gastosFinal);
//			// Fin MPM
//			
//		} catch (Exception e) {
//			logger.error("ERROR procesarXMLGrupoNegocio", e);
//			throw e;
//		}
//	}
//
//	private List<PolizaPctComisiones> obtenerPorcentajesComisiones(AnexoModificacion am) {
//		@SuppressWarnings("unchecked")
//		List<PolizaPctComisiones> lstPct= (List<PolizaPctComisiones>) anexoModificacionDao
//						.getObjects(PolizaPctComisiones.class, "poliza.idpoliza", am.getPoliza().getIdpoliza());
//		
//		List<PolizaPctComisiones> porcentajesActualizados = new ArrayList<PolizaPctComisiones>();
//		
//		for (PolizaPctComisiones pct : lstPct) {
//			PolizaPctComisiones pc = new PolizaPctComisiones();
//			
//			if (pct.getId() != null){
//				pc.setId(pct.getId());
//			}
//			if (pct.getDescGrupoNegocio() != null){ 
//				pc.setDescGrupoNegocio(pct.getDescGrupoNegocio());
//			}
//			if (pct.getEstado() != null){
//				pc.setEstado(pct.getEstado());
//			}
//			if (pct.getGrupoNegocio() != null){
//				pc.setGrupoNegocio(pct.getGrupoNegocio());
//			}
//			
//			if (pct.getPctdescmax() != null){ 
//				pc.setPctdescmax(pct.getPctdescmax());
//			}
//			if (pct.getPctentidad() != null) {
//				pc.setPctentidad(pct.getPctentidad());
//			}
//			if (pct.getPctesmediadora() != null){
//				pc.setPctesmediadora(pct.getPctesmediadora());
//			}
//			if (pct.getPctadministracion() != null){
//				pc.setPctadministracion(pct.getPctadministracion());
//			}
//			if (pct.getPctadquisicion() != null){
//				pc.setPctadquisicion(pct.getPctadquisicion());
//			}
//			if (pct.getPctcommax() != null){
//				pc.setPctcommax(pct.getPctcommax());
//			}
//			
//			// Grupo de negocio de RyD
//			if (pct.getGrupoNegocio().equals(Constants.GRUPO_NEGOCIO_RYD)) {
//				
//				// Descuento RyD
//				if (am.getPctdescelegido() != null){
//					pc.setPctdescelegido(am.getPctdescelegido()); // del anexo
//				}
//				else if (pct.getPctdescelegido() != null){
//					pc.setPctdescelegido(pct.getPctdescelegido());
//				}
//				// Recargo RyD
//				if (am.getPctrecarelegido() != null){
//					pc.setPctrecarelegido(am.getPctrecarelegido()); // del anexo
//				}
//				else if (pct.getPctrecarelegido() != null){
//					pc.setPctrecarelegido(pct.getPctrecarelegido());
//				}
//				// Grupo de negocio de Resto
//			} else if(pct.getGrupoNegocio().equals(Constants.GRUPO_NEGOCIO_VIDA)) {
//				
//				// Descuento Resto
//				if (am.getPctdescelegidoResto() != null){
//					pc.setPctdescelegido(am.getPctdescelegidoResto()); // del anexo
//				}
//				else if (pct.getPctdescelegido() != null){
//					pc.setPctdescelegido(pct.getPctdescelegido());
//				}
//				// Recargo Resto
//				if (am.getPctrecarelegidoResto() != null){
//					pc.setPctrecarelegido(am.getPctrecarelegidoResto()); // del anexo
//				}
//				else if (pct.getPctrecarelegido() != null){
//					pc.setPctrecarelegido(pct.getPctrecarelegido());
//				}
//			}					
//			porcentajesActualizados.add(pc);
//		}
//		return porcentajesActualizados;
//	}
//	
//	/**
//	 * Obtiene de BBDD las distribuciones de coste asociadas al anexo indicado por parametros y devuelve un mapa de objetos con la respuesta
//	 * @param am Anexo del que se quiere obtener las distribucion de costes
//	 * @return Mapa de objetos con la informacion de la BBDD o el mensaje de error en caso de que ocurra
//	 */
//	public Map<String, Object> consultaDistribucionCoste(AnexoModificacion am) {
//		
//		Map<String, Object> resultado = new HashMap<String, Object>();
//		
//		// Comprueba si el parametro contiene el id del anexo (si viene de algun boton de solo lectura) o el id del cupon (si viene de la 
//		// pantalla de relacion de modificaciones)
//		AnexoModificacion anexo = null;
//		try {
//			if (am.getId() != null) {
//				anexo = (AnexoModificacion) anexoModificacionDao.get(AnexoModificacion.class, am.getId());
//			}
//			else if (am.getCupon() != null && am.getCupon().getIdcupon() != null) {
//				anexo = anexoModificacionDao.getAnexoByIdCupon(am.getCupon().getIdcupon());
//			}
//		} catch (Exception e) {
//			logger.error("Ha ocurrido un error al obtener el anexo asociado", e);
//		}
//		
//		// Si ha habido algun problema al obtener el anexo de BBDD
//		if (anexo == null) {
//				resultado.put(CalculoRCManager.ALERTA, bundle.getString("mensaje.calculoModificacion.anexo.noExisteAnexo"));
//				// Se carga el objeto poliza asociado al anexo para que funcione la opcion 'Volver' desde la pantalla de calculo de modificacion
//				Poliza p = null;
//				try {
//					p = (Poliza) anexoModificacionDao.get(Poliza.class, am.getPoliza().getIdpoliza());
//				} catch (DAOException e) {
//					logger.error("Error al cargar la poliza asociada al anexo", e);
//				}
//				am.setPoliza(p);
//				resultado.put(CalculoRCManager.ANEXO_MODIFICACION, am);
//				return resultado;
//		}
//		
//		// Devuelve el anexo cargado de BD
//		resultado.put(CalculoRCManager.ANEXO_MODIFICACION, anexo);
//		
//		// Obtiene la distribuciones de coste asociadas al anexo y las devuelve para su visualizacion en pantalla
//		Set<AnexoModDistribucionCostes> amDC = anexo.getAnexoModDistribucionCosteses();
//		List<AnexoModDistribucionCostes> lstAnexoDistCostes = new ArrayList<AnexoModDistribucionCostes>();
//		if (amDC != null && !amDC.isEmpty()) {
//			List<AnexoModDistribucionCostes> lstAmDistCostes = new ArrayList<AnexoModDistribucionCostes>();
//			List<AnexoModDistribucionCostes> lstAmDifCostes = new ArrayList<AnexoModDistribucionCostes>();
//			for (AnexoModDistribucionCostes dc : amDC) {
//				// Completa la informacion contenida en la distribucion de costes para visualizacion en pantalla
//				lstAnexoDistCostes.add(dc);
//			}
//			if (anexo.getPoliza().getLinea().isLineaGanado())
//				completarListaDistribucionCostes (lstAnexoDistCostes);
//			else
//				completarDistribucionCostes (lstAnexoDistCostes);
//			for (AnexoModDistribucionCostes dc : lstAnexoDistCostes) {			
//				// Envia el objeto a la pagina para su visualizacion
//				if (TIPO_DC_CALCULO_MODIF == dc.getId().getTipoDc()) {					
//					lstAmDistCostes.add(dc);					
//				}
//				else if (TIPO_DC_DIFERENCIA == dc.getId().getTipoDc()) {
//					lstAmDifCostes.add(dc);		
//				}
//			}
//			resultado.put("distribucionCostes", lstAmDistCostes);
//			resultado.put("diferenciaCostes", lstAmDifCostes);
//		}
//		// Si no hay distribucion de costes asociada al anexo indicado
//		else {
//			resultado.put(CalculoRCManager.ALERTA, bundle.getString("mensaje.calculoModificacion.agricola.noExisteDCModificacion"));
//			return resultado;
//		}
//		
//		return resultado;
//	}
//	
//	
//	/**
//	 * Transforma el resultado del calculo devuelto por el SW en una lista de distribucion de costes de anexos de formato unificado y la inserta en BD
//	 * @param rc Reduccion de capital que se ha enviado al calcular
//	 * @param respuesta Respuesta del servicio de calculo
//	 * @param calcularSituacionActual Indicador de calculo de situacion actual
//	 * @return
//	 */
//	private List<AnexoModDistribucionCostes> transformarYGuardarDistribucionCostesUnificado(ReduccionCapital rc, Map<String, Object> respuesta, boolean calcularSituacionActual) {		
//		List<AnexoModDistribucionCostes> lstAnexoDistCostes = new ArrayList<AnexoModDistribucionCostes>();
//		try {
//			String res = (String)respuesta.get(CalculoRCManager.CALCULO_MODIFICACION);
//			es.agroseguro.distribucionCostesSeguro.PolizaDocument plzd = es.agroseguro.distribucionCostesSeguro.PolizaDocument.Factory.parse(res);
//			
//			// Distribucion de costes
//			CostePoliza costePoliza = plzd.getPoliza().getDatosCalculo().getCostePoliza();
//			// Recorremos el array de CostesGruposNegocios devueltos en la respuesta
//			CosteGrupoNegocio[] costeGrupoNegocioArray = costePoliza.getCosteGrupoNegocioArray();
//			for (CosteGrupoNegocio costeGrupoNegocio:costeGrupoNegocioArray) {			
//				RedCapitalDistribucionCostes dc = new RedCapitalDistribucionCostes();
//				//dc.setAnexoModificacion(am);
//				RedCapitalDistribucionCostesId dci = new RedCapitalDistribucionCostesId(rc.getId(), TIPO_DC_CALCULO_MODIF,costeGrupoNegocio.getGrupoNegocio().charAt(0));
//				dc.setId(dci);
//				logger.debug("    DC - idAnexo:"+ rc.getId()+CalculoRCManager.TIPO_DC+ dci.getTipoDc()+" CosteGrupoNegocio: "+  costeGrupoNegocio.getGrupoNegocio());
//				dc.setGrupoNegocio(costeGrupoNegocio.getGrupoNegocio().charAt(0));
//				dc.setCosteTomador(costeGrupoNegocio.getCosteTomador());
//				dc.setPrimaComercial(costeGrupoNegocio.getPrimaComercial());
//				dc.setPrimaComercialNeta(costeGrupoNegocio.getPrimaComercialNeta());
//				dc.setRecargoAval(costePoliza.getFinanciacion() != null ? costePoliza.getFinanciacion().getRecargoAval() : null);
//				dc.setRecargoConsorcio(costeGrupoNegocio.getRecargoConsorcio());
//				dc.setRecargoFraccionamiento(costePoliza.getFinanciacion() != null ? costePoliza.getFinanciacion().getRecargoFraccionamiento() : null);
//				dc.setReciboPrima(costeGrupoNegocio.getReciboPrima());
//				dc.setTotalCosteTomador(costePoliza.getTotalCosteTomador());
//				
//				
//				// Subvenciones Enesa
//				es.agroseguro.contratacion.costePoliza.SubvencionEnesa[] seArray = costeGrupoNegocio.getSubvencionEnesaArray();
//				if (seArray != null && seArray.length > 0) {
//					Set<RedCapitalSubvEnesa> setSubvEnesa = new HashSet<RedCapitalSubvEnesa>(0);
//					
//					for (es.agroseguro.contratacion.costePoliza.SubvencionEnesa se : seArray) {
//						RedCapitalSubvEnesa sEne = new RedCapitalSubvEnesa(new RedCapitalSubvEnesaId (dci, se.getTipo(),costeGrupoNegocio.getGrupoNegocio().charAt(0)), dc, se.getImporte());
//						setSubvEnesa.add(sEne);
//					}
//					
//					dc.setRedCapitalSubvEnesa(setSubvEnesa);
//				}
//				
//				// Subvenciones CCAA
//				es.agroseguro.contratacion.costePoliza.SubvencionCCAA[] sCCAAArray = costeGrupoNegocio.getSubvencionCCAAArray();
//				if (sCCAAArray != null && sCCAAArray.length > 0) {
//					
//					Set<RedCapitalSubvCCAA> setSubvCCAA = new HashSet<RedCapitalSubvCCAA>(0);
//					
//					for (es.agroseguro.contratacion.costePoliza.SubvencionCCAA sc : sCCAAArray) {
//						AnexoModSubvCCAA anexosCCAA = new AnexoModSubvCCAA(new AnexoModSubvCCAAId(dci, sc.getCodigoOrganismo().charAt(0),costeGrupoNegocio.getGrupoNegocio().charAt(0)), dc, sc.getImporte());
//						setSubvCCAA.add(anexosCCAA);
//					}
//					dc.setRedCapitalSubvCCAA(setSubvCCAA);
//				}
//				
//				reduccionCapitalDao.saveOrUpdate(dc);
//				reduccionCapitalDao.evict(dc);
//				
//				lstAnexoDistCostes.add(dc);
//				
//				
//			}
//			
//			
//			
//			
//		} catch (Exception e) {
//			logger.error("Error al transformar y guardar la diferencia de costes de la modificacion de formato unificado", e);
//			return null;
//		}
//		
//		return lstAnexoDistCostes;
//	}
//	
//	
//	/**
//	 * Transforma la diferencia de costes devuelta por el SW en una lista de distribucion de costes de anexos de formato unificado y la inserta en BD 
//	 * @param am Anexo de modificacion que se ha enviado al calcular
//	 * @param respuesta Respuesta del servicio de calculo
//	 * @return
//	 */
//	private List<RedCapitalDistribucionCostes>  transformarYGuardarDiferenciaCostesUnificado (ReduccionCapital rc, Map<String, Object> respuesta) {
//		List<RedCapitalDistribucionCostes> lstAnexoDistCostes = new ArrayList<RedCapitalDistribucionCostes>();
//		
//		
//		try {
//			String res = (String)respuesta.get(CalculoRCManager.DIFERENCIAS_COSTE);
//			DiferenciasCosteUnificadoDocument dfcd = DiferenciasCosteUnificadoDocument.Factory.parse(res);
//			DiferenciasCosteUnificado dfc = dfcd.getDiferenciasCosteUnificado();
//			
//			// Distribucion de costes
//			DiferenciasCostePolizasOriginalModificada diferencias = dfc.getDiferenciasCostePolizasOriginalModificada();
//
//			es.agroseguro.seguroAgrario.diferenciasCosteUnificado.CosteGrupoNegocio[] costeGrupoNegocioArray = diferencias.getCosteGrupoNegocioArray();
//			for (es.agroseguro.seguroAgrario.diferenciasCosteUnificado.CosteGrupoNegocio costeGrupoNegocio:costeGrupoNegocioArray) {
//				RedCapitalDistribucionCostes dc = new RedCapitalDistribucionCostes();
//				//dc.setAnexoModificacion(am);
//				RedCapitalDistribucionCostesId dci = new RedCapitalDistribucionCostesId(rc.getId(), TIPO_DC_DIFERENCIA,costeGrupoNegocio.getGrupoNegocio().charAt(0));
//				dc.setId(dci);
//				logger.debug("    DF - idAnexo:"+ rc.getId()+CalculoRCManager.TIPO_DC+ dci.getTipoDc()+" CosteGrupoNegocio: "+  costeGrupoNegocio.getGrupoNegocio());
//				dc.setGrupoNegocio(costeGrupoNegocio.getGrupoNegocio().charAt(0));
//				dc.setCosteTomador(costeGrupoNegocio.getDiferenciaCosteTomador());
//				dc.setPrimaComercial(costeGrupoNegocio.getDiferenciaPrimaComercial());
//				dc.setPrimaComercialNeta(costeGrupoNegocio.getDiferenciaPrimaComercialNeta());
//				dc.setRecargoConsorcio(costeGrupoNegocio.getDiferenciaRecargoConsorcio());
//				dc.setReciboPrima(costeGrupoNegocio.getDiferenciaReciboPrima());
//				dc.setTotalCosteTomador(diferencias.getDiferenciaTotalCosteTomador());
//				
//							
//				// Subvenciones Enesa
//				es.agroseguro.seguroAgrario.diferenciasCosteUnificado.SubvencionEnesa[] seArray = costeGrupoNegocio.getSubvencionEnesaArray();
//				if (seArray != null && seArray.length > 0) {
//					
//					Set<RedCapitalSubvEnesa> setSubvEnesa = new HashSet<RedCapitalSubvEnesa>(0);
//					
//					for (es.agroseguro.seguroAgrario.diferenciasCosteUnificado.SubvencionEnesa se : seArray) {
//						setSubvEnesa.add(new AnexoModSubvEnesa(new AnexoModSubvEnesaId (dci, se.getTipo(),costeGrupoNegocio.getGrupoNegocio().charAt(0)), dc, se.getDiferenciaImporte()));
//					}
//					dc.setRedCapitalSubvEnesa(setSubvEnesa);
//				}
//				
//				// Subvenciones CCAA
//				es.agroseguro.seguroAgrario.diferenciasCosteUnificado.SubvencionCCAA[] sCCAAArray = costeGrupoNegocio.getSubvencionCCAAArray();
//				if (sCCAAArray != null && sCCAAArray.length > 0) {
//					
//					Set<RedCapitalSubvCCAA> setSubvCCAA = new HashSet<RedCapitalSubvCCAA>(0);
//					
//					for (es.agroseguro.seguroAgrario.diferenciasCosteUnificado.SubvencionCCAA sc : sCCAAArray) {
//						setSubvCCAA.add(new AnexoModSubvCCAA(new AnexoModSubvCCAAId(dci, sc.getCodigoOrganismo().charAt(0),costeGrupoNegocio.getGrupoNegocio().charAt(0)), dc, sc.getDiferenciaImporte()));
//					}
//					dc.setRedCapitalSubvCCAA(setSubvCCAA);
//				}
//				
//				reduccionCapitalDao.saveOrUpdate(dc);
//				reduccionCapitalDao.evict(dc);
//				
//				lstAnexoDistCostes.add(dc);
//			}
//			
//			
//		} catch (Exception e) {
//			logger.error("Error al transformar y guardar la diferencia de costes de la modificacion ", e);
//			return null;
//		}
//		
//		return lstAnexoDistCostes;
//	}
//
//
	/**
	 * Transforma el resultado del calculo devuelto por el SW en una distribucion de costes de anexos y la inserta en BD
	 * @param am Anexo de modificacion que se ha enviado al calcular
	 * @param respuesta Respuesta del servicio de calculo
	 * @param calcularSituacionActual Indicador de calculo de situacion actual
	 * @return
	 */
	private List<RedCapitalDistribucionCostes> transformarYGuardarDistribucionCostes(ReduccionCapital rc, Map<String, Object> respuesta, boolean calcularSituacionActual, boolean isPlzFinanciada) {
		List<RedCapitalDistribucionCostes> lstRCDistCostes = new ArrayList<RedCapitalDistribucionCostes>();
		RedCapitalDistribucionCostes dc = new RedCapitalDistribucionCostes();
		//dc.setAnexoModificacion(am)
		
		/* Pet. 57626 ** MODIF TAM (16.06.2020) *** Inicio */
		/* Por los desarrollos de esta peticion tanto las polizas de ganado como las de agricolas
		 * van ahora con Formato Unificado.
		 */
		
		RedCapitalDistribucionCostesId dci = new RedCapitalDistribucionCostesId(rc.getId(), TIPO_DC_CALCULO_MODIF,Constants.GRUPO_NEGOCIO_VIDA);
		dc.setId(dci);
		
		try {
			
			es.agroseguro.distribucionCostesSeguro.PolizaDocument plzd = es.agroseguro.distribucionCostesSeguro.PolizaDocument.Factory.parse((String)respuesta.get(CalculoRCManager.CALCULO_MODIFICACION));
			
			// Distribucion de costes
			CostePoliza costePoliza = plzd.getPoliza().getDatosCalculo().getCostePoliza();
			// Recorremos el array de CostesGruposNegocios devueltos en la respuesta
			CosteGrupoNegocio[] costeGrupoNegocioArray = costePoliza.getCosteGrupoNegocioArray();
			for (CosteGrupoNegocio costeGrupoNegocio:costeGrupoNegocioArray) {
				
				// Distribucion de costes
				//DistribucionCoste distribucionCoste1 = plzd.getPoliza().getD.getDatosCalculo().getDistribucionCoste1();
				dc.setGrupoNegocio(Constants.GRUPO_NEGOCIO_VIDA);
				dc.setCosteTomador(costeGrupoNegocio.getCosteTomador());
				dc.setPrimaComercial(costeGrupoNegocio.getPrimaComercial());
				dc.setPrimaComercialNeta(costeGrupoNegocio.getPrimaComercialNeta());
				// TAM (16.06.2020)
				// (TAM) - dc.setRecargoAval(costeGrupoNegocio.getRecargoAval());
				dc.setRecargoAval(costePoliza.getFinanciacion() != null ? costePoliza.getFinanciacion().getRecargoAval() : null);
				
				dc.setRecargoConsorcio(costeGrupoNegocio.getRecargoConsorcio());
				// (TAM) - dc.setRecargoFraccionamiento(costeGrupoNegocio.getRecargoFraccionamiento());
				dc.setRecargoFraccionamiento(costePoliza.getFinanciacion() != null ? costePoliza.getFinanciacion().getRecargoFraccionamiento() : null);
				dc.setReciboPrima(costeGrupoNegocio.getReciboPrima());
				dc.setTotalCosteTomador(isPlzFinanciada ? costePoliza.getTotalCosteTomador() : costeGrupoNegocio.getCosteTomador());
				
				
				// Bonificaciones y recargos
				es.agroseguro.contratacion.costePoliza.BonificacionRecargo[] bonificacionRecargoArray = costeGrupoNegocio.getBonificacionRecargoArray(); 
				if (bonificacionRecargoArray != null && bonificacionRecargoArray.length > 0) {
					Set<RedCapitalBonifRecargos> setBonifRecarg = new HashSet<RedCapitalBonifRecargos>(0);
					
					for (es.agroseguro.contratacion.costePoliza.BonificacionRecargo br : bonificacionRecargoArray) {
						setBonifRecarg.add (new RedCapitalBonifRecargos(new RedCapitalBonifRecargosId (dci, br.getCodigo(),Constants.GRUPO_NEGOCIO_VIDA), dc, br.getImporte()));
					}
					
					dc.setRedCapitalBonifRecargos(setBonifRecarg);
				}
				
				// Subvenciones Enesa
				es.agroseguro.contratacion.costePoliza.SubvencionEnesa[] subvencionEnesaArray = costeGrupoNegocio.getSubvencionEnesaArray();
				if (subvencionEnesaArray != null && subvencionEnesaArray.length > 0) {
					
					Set<RedCapitalSubvEnesa> setSubvEnesa = new HashSet<RedCapitalSubvEnesa>(0);
					
					for (es.agroseguro.contratacion.costePoliza.SubvencionEnesa se : subvencionEnesaArray) {
						setSubvEnesa.add(new RedCapitalSubvEnesa(new RedCapitalSubvEnesaId(dci, se.getTipo(),Constants.GRUPO_NEGOCIO_VIDA), dc, se.getImporte()));
					}
					dc.setRedCapitalSubvEnesas(setSubvEnesa);
					
				}
				
				// Subvenciones CCAA
				es.agroseguro.contratacion.costePoliza.SubvencionCCAA[] subvencionCCAAArray = costeGrupoNegocio.getSubvencionCCAAArray();
				if (subvencionCCAAArray != null && subvencionCCAAArray.length > 0) {
					
					Set<RedCapitalSubvCCAA> setSubvCCAA = new HashSet<RedCapitalSubvCCAA>(0);
					
					for (es.agroseguro.contratacion.costePoliza.SubvencionCCAA sc : subvencionCCAAArray) {
						setSubvCCAA.add(new RedCapitalSubvCCAA(new RedCapitalSubvCCAAId(dci, sc.getCodigoOrganismo().charAt(0),Constants.GRUPO_NEGOCIO_VIDA), dc, sc.getImporte()));
					}
					
					dc.setRedCapitalSubvCCAAs(setSubvCCAA);
				}
				
				reduccionCapitalDao.saveOrUpdate(dc);
				lstRCDistCostes.add(dc);
			}
		} catch (Exception e) {
			logger.error(" Error al transformar y guardar la diferencia de costes de la modificacion", e);
			return null;
		}
		
		return lstRCDistCostes;
	}
//	
//	/**
//	 * Completa la informacion contenida en la distribucion de costes para visualizacion en pantalla
//	 * @param dc
//	 */
//	private void completarListaDistribucionCostes (List<AnexoModDistribucionCostes> lstDc) {
//		
//		for (AnexoModDistribucionCostes dc:lstDc) {
//			
//			// Descripcion del Grupo de Negocio
//			if (dc.getGrupoNegocio() != null)
//				dc.setDescGrupoNegocio(anexoModificacionDao.getDescGrupoNegocio(dc.getGrupoNegocio()));
//			else if (dc.getId() != null && dc.getId().getGrupoNegocio() != null)
//				dc.setDescGrupoNegocio(anexoModificacionDao.getDescGrupoNegocio(dc.getId().getGrupoNegocio()));
//			else
//				dc.setDescGrupoNegocio(anexoModificacionDao.getDescGrupoNegocio(Constants.GRUPO_NEGOCIO_VIDA));
//			// Descripciones de bonificaciones y recargos
//			if (dc.getAnexoModBonifRecargoses() != null && !dc.getAnexoModBonifRecargoses().isEmpty()) {
//				
//				Set<AnexoModBonifRecargos> bonifRecCompleto = new HashSet<AnexoModBonifRecargos>(0);
//				
//				for (AnexoModBonifRecargos ambr : dc.getAnexoModBonifRecargoses()) {
//					ambr.setDescripcion(seleccionPolizaManager.getDescBoniRecar(new BigDecimal (ambr.getId().getCodigo())));
//					bonifRecCompleto.add(ambr);
//				}
//				
//				dc.setAnexoModBonifRecargoses(bonifRecCompleto);
//			}
//			
//			// Descripciones para subvenciones de Enesa
//			if (dc.getAnexoModSubvEnesas() != null && !dc.getAnexoModSubvEnesas().isEmpty()) {
//				
//				Set<AnexoModSubvEnesa> subvEnesaCompleto = new HashSet<AnexoModSubvEnesa>(0);
//				
//				for (AnexoModSubvEnesa se : dc.getAnexoModSubvEnesas()) {
//					se.setDescripcion(seleccionPolizaManager.getDescripcionEnesa(new BigDecimal (se.getId().getTipoSubvencion())));
//					subvEnesaCompleto.add(se);
//				}
//				
//				dc.setAnexoModSubvEnesas(subvEnesaCompleto);
//			}
//			
//			// Descripciones para subvenciones CCAA
//			if (dc.getAnexoModSubvCCAAs() != null && !dc.getAnexoModSubvCCAAs().isEmpty()) {
//				
//				Set<AnexoModSubvCCAA> subvCCAACompleto = new HashSet<AnexoModSubvCCAA>(0);
//				
//				for (AnexoModSubvCCAA sc : dc.getAnexoModSubvCCAAs()) {
//					sc.setDescripcion(seleccionPolizaManager.getCCAA(sc.getId().getCodOrganismo()));
//					subvCCAACompleto.add(sc);
//				}
//				
//				dc.setAnexoModSubvCCAAs(subvCCAACompleto);
//			}
//		}
//	}
//	
//	
	/**
	 * Completa la informacion contenida en la distribucion de costes para visualizacion en pantalla
	 * @param dc
	 */
	private void completarDistribucionCostes  (List<RedCapitalDistribucionCostes> lstDc) {		
		for (RedCapitalDistribucionCostes dc:lstDc) {
			
			// Descripcion del Grupo de Negocio
			if (dc.getGrupoNegocio() != null)
				dc.setDescGrupoNegocio(reduccionCapitalDao.getDescGrupoNegocio(dc.getGrupoNegocio()));
			else if (dc.getId() != null && dc.getId().getGrupoNegocio() != null)
				dc.setDescGrupoNegocio(reduccionCapitalDao.getDescGrupoNegocio(dc.getId().getGrupoNegocio()));
			else
				dc.setDescGrupoNegocio(reduccionCapitalDao.getDescGrupoNegocio(Constants.GRUPO_NEGOCIO_VIDA));
			
			//Descripciones de bonificaciones y recargos
			if (dc.getRedCapitalBonifRecargos() != null && !dc.getRedCapitalBonifRecargos().isEmpty()) {
				
				Set<RedCapitalBonifRecargos> bonifRecCompleto = new HashSet<RedCapitalBonifRecargos>(0);
				
				for (RedCapitalBonifRecargos ambr : dc.getRedCapitalBonifRecargos()) {
					ambr.setDescripcion(seleccionPolizaManager.getDescBoniRecar(new BigDecimal (ambr.getId().getCodigo())));
					bonifRecCompleto.add(ambr);
				}
				
				dc.setRedCapitalBonifRecargos(bonifRecCompleto);
			}
			
			// Descripciones para subvenciones de Enesa
			if (dc.getRedCapitalSubvEnesas() != null && !dc.getRedCapitalSubvEnesas().isEmpty()) {
				
				Set<RedCapitalSubvEnesa> subvEnesaCompleto = new HashSet<RedCapitalSubvEnesa>(0);
				
				for (RedCapitalSubvEnesa se : dc.getRedCapitalSubvEnesas()) {
					se.setDescripcion(seleccionPolizaManager.getDescripcionEnesa(new BigDecimal (se.getId().getTipoSubvencion())));
					subvEnesaCompleto.add(se);
				}
				
				dc.setRedCapitalSubvEnesas(subvEnesaCompleto);
			}
			
			// Descripciones para subvenciones CCAA
			if (dc.getRedCapitalSubvCCAAs()  != null && !dc.getRedCapitalSubvCCAAs().isEmpty()) {
				
				Set<RedCapitalSubvCCAA> subvCCAACompleto = new HashSet<RedCapitalSubvCCAA>(0);
				
				for (RedCapitalSubvCCAA sc : dc.getRedCapitalSubvCCAAs()) {
					sc.setDescripcion(seleccionPolizaManager.getCCAA(sc.getId().getCodOrganismo()));
					subvCCAACompleto.add(sc);
				}
				
				dc.setRedCapitalSubvCCAAs(subvCCAACompleto);
			}
		}
	}
//
//
	/**
	 * Transforma la diferencia de costes devuelta por el SW en una distribucion de costes de anexos y la inserta en BD 
	 * @param am Anexo de modificacion que se ha enviado al calcular
	 * @param respuesta Respuesta del servicio de calculo
	 * @return
	 */
	private List<RedCapitalDistribucionCostes> transformarYGuardarDiferenciaCostes(ReduccionCapital rc, Map<String, Object> respuesta, boolean isPlzFinanciada) {
		
		/* Pet. 57626 ** MODIF TAM (16.06.2020) *** Inicio */
		/* Por los desarrollos de esta peticion tanto las polizas de ganado como las de agricolas
		 * van ahora con Formato Unificado.
		 */
		
		List<RedCapitalDistribucionCostes> lstAnexoDistCostes = new ArrayList<RedCapitalDistribucionCostes>();
		RedCapitalDistribucionCostes dc = new RedCapitalDistribucionCostes();
		//dc.setAnexoModificacion(am);
		
		RedCapitalDistribucionCostesId dci = new RedCapitalDistribucionCostesId(rc.getId(), TIPO_DC_DIFERENCIA,Constants.GRUPO_NEGOCIO_VIDA);
		dc.setId(dci);
		
		try {
			
			//DiferenciasCosteDocument dfcd = DiferenciasCosteDocument.Factory.parse((String)respuesta.get("diferenciasCoste"));
			
			//DiferenciasCoste dfc = dfcd.getDiferenciasCoste();
			
			DiferenciasCosteUnificadoDocument dfcd = DiferenciasCosteUnificadoDocument.Factory.parse((String)respuesta.get(CalculoRCManager.DIFERENCIAS_COSTE));
			DiferenciasCosteUnificado dfc = dfcd.getDiferenciasCosteUnificado();
			
			// Distribucion de costes
			DiferenciasCostePolizasOriginalModificada diferencias = dfc.getDiferenciasCostePolizasOriginalModificada();
			es.agroseguro.seguroAgrario.diferenciasCosteUnificado.CosteGrupoNegocio[] costeGrupoNegocioArray = diferencias.getCosteGrupoNegocioArray();
			
			for (es.agroseguro.seguroAgrario.diferenciasCosteUnificado.CosteGrupoNegocio costeGrupoNegocio:costeGrupoNegocioArray) {
			
				// Distribucion de costes
				//DiferenciasCostePolizasOriginalModificada1 diferencias = dfc.getDiferenciasCostePolizasOriginalModificada1();
				dc.setGrupoNegocio(Constants.GRUPO_NEGOCIO_VIDA);
				dc.setCosteTomador(costeGrupoNegocio.getDiferenciaCosteTomador());
				dc.setPrimaComercial(costeGrupoNegocio.getDiferenciaPrimaComercial());
				dc.setPrimaComercialNeta(costeGrupoNegocio.getDiferenciaPrimaComercialNeta());
				//dc.setRecargoAval(costeGrupoNegocio.get.getDiferenciaRecargoAval());
				dc.setRecargoConsorcio(costeGrupoNegocio.getDiferenciaRecargoConsorcio());
				//dc.setRecargoFraccionamiento(costeGrupoNegocio.getDiferenciaRecargoFraccionamiento());
				dc.setReciboPrima(costeGrupoNegocio.getDiferenciaReciboPrima());
				dc.setTotalCosteTomador(isPlzFinanciada ? diferencias.getDiferenciaTotalCosteTomador() : costeGrupoNegocio.getDiferenciaCosteTomador());
				
				// Bonificaciones y recargos
				es.agroseguro.seguroAgrario.diferenciasCosteUnificado.BonificacionRecargo[] bonificacionRecargoArray = costeGrupoNegocio.getBonificacionRecargoArray(); 
				if (bonificacionRecargoArray != null && bonificacionRecargoArray.length > 0) {
					Set<RedCapitalBonifRecargos> setBonifRecarg = new HashSet<RedCapitalBonifRecargos>(0);
					
					for (es.agroseguro.seguroAgrario.diferenciasCosteUnificado.BonificacionRecargo br : bonificacionRecargoArray) {
						setBonifRecarg.add (new RedCapitalBonifRecargos(new RedCapitalBonifRecargosId (dci, br.getCodigo(),Constants.GRUPO_NEGOCIO_VIDA), dc, br.getDiferenciaImporte()));
					}
					
					dc.setRedCapitalBonifRecargos(setBonifRecarg);
				}
				
				
				// Subvenciones Enesa
				es.agroseguro.seguroAgrario.diferenciasCosteUnificado.SubvencionEnesa[] subvencionEnesaArray = costeGrupoNegocio.getSubvencionEnesaArray();
				
				if (subvencionEnesaArray != null && subvencionEnesaArray.length > 0) {
					
					Set<RedCapitalSubvEnesa> setSubvEnesa = new HashSet<RedCapitalSubvEnesa>(0);
					
					for (es.agroseguro.seguroAgrario.diferenciasCosteUnificado.SubvencionEnesa se : subvencionEnesaArray) {
						setSubvEnesa.add(new RedCapitalSubvEnesa(new RedCapitalSubvEnesaId (dci, se.getTipo(),Constants.GRUPO_NEGOCIO_VIDA), dc, se.getDiferenciaImporte()));
					}
					
					dc.setRedCapitalSubvEnesas(setSubvEnesa);
					
				}
				
				// Subvenciones CCAA
				es.agroseguro.seguroAgrario.diferenciasCosteUnificado.SubvencionCCAA[] subvencionCCAAArray = costeGrupoNegocio.getSubvencionCCAAArray();
				if (subvencionCCAAArray != null && subvencionCCAAArray.length > 0) {
					
					Set<RedCapitalSubvCCAA> setSubvCCAA = new HashSet<RedCapitalSubvCCAA>(0);
					
					for (es.agroseguro.seguroAgrario.diferenciasCosteUnificado.SubvencionCCAA sc : subvencionCCAAArray) {
						setSubvCCAA.add(new RedCapitalSubvCCAA(new RedCapitalSubvCCAAId(dci, sc.getCodigoOrganismo().charAt(0),Constants.GRUPO_NEGOCIO_VIDA), dc, sc.getDiferenciaImporte()));
					}
					dc.setRedCapitalSubvCCAAs(setSubvCCAA);
				}
				
				reduccionCapitalDao.saveOrUpdate(dc);
				lstAnexoDistCostes.add(dc);
			} /* Fin del For */
		} catch (Exception e) {
			logger.error("Error al transformar y guardar la diferencia de costes de la modificacion", e);
			return null;
		}
		
		return lstAnexoDistCostes;
	}

	/**
	 * @param codUsuario Usuario que solicita el calculo de la modificacion
	 * @param am Anexo de modificacion que se ha calculado
	 * @param calcularSituacionActual Indicador de calculo de situacion actual
	 * @param respuesta Objeto que encapsula la respuesta del SW
	 */
	private void registrarComunicacionSW(final String codUsuario, ReduccionCapital rc, Clob xml,
			boolean calcularSituacionActual, Map<String, Object> respuesta) {
		
		RedCapitalSWCalculo rcCalc = new RedCapitalSWCalculo();
		rcCalc.setReduccionCapital(rc);
		rcCalc.setCodusuario(codUsuario);
		rcCalc.setFecha(new Date());
		rcCalc.setCupon(rc.getCupon().getIdcupon());
		rcCalc.setTipoPoliza(rc.getPoliza().getTipoReferencia());
		rcCalc.setCalcularSitAct(calcularSituacionActual ? 1 : 0);
		rcCalc.setModificacionPlz(xml);
		if (respuesta != null) {
			rcCalc.setCalculoModificacion(respuesta.containsKey(CalculoRCManager.CALCULO_MODIFICACION) ? Hibernate.createClob((String)respuesta.get(CalculoRCManager.CALCULO_MODIFICACION)) : null);
			rcCalc.setCalculoOriginal(respuesta.containsKey("calculoOriginal") ? Hibernate.createClob((String)respuesta.get("calculoOriginal")) : null);
			rcCalc.setDiferenciasCoste(respuesta.containsKey(CalculoRCManager.DIFERENCIAS_COSTE) ? Hibernate.createClob((String)respuesta.get(CalculoRCManager.DIFERENCIAS_COSTE)) : null);
			rcCalc.setMsgErrorSw(respuesta.containsKey(CalculoRCManager.ALERTA) ? StringUtils.cortarCadena((String) respuesta.get(CalculoRCManager.ALERTA), MAX_MSG_ERROR) : null);
		}
		
		try {
			reduccionCapitalDao.saveOrUpdate(rcCalc);
		} catch (Exception e) {
			logger.error("Error al registrar la comunicacion con el SW de calculo de la modificacion en BD", e);
		} 
		
	}
	
	/**
	 * Convierte el Clob recibido en el objeto Base64Binary requerido para la llamada al SW 
	 * @param xml Clob a convertir
	 * @return
	 */
	private Base64Binary procesarXML (String xmlStr) {
		
		logger.debug("CalculoRCManager - Dentro de procesarXML");
		logger.debug("xml: " + xmlStr);
		
		// Xml de la modificacion
		Base64Binary base64Binary = new Base64Binary();
		base64Binary.setContentType("text/xml");
		
		try {
			base64Binary.setValue(xmlStr.getBytes("UTF-8"));
		} catch (Exception e) {
			logger.error("Error al convertir el XML del anexo a binario" , e);
		}
		
		return base64Binary;
	}

	/**
	 * Obtiene el parametro de calculo de situacion actual
	 * @return 
	 */
	private boolean getCalcularSituacionActual() {
		try {
			return parametrizacionManager.getParametro().getCalculoSitActSwCalculoAm().intValue()==1;
		} catch (Exception e) {
			logger.error("Error al obtener el parametro de calculo de situacion actual", e);
		}
		
		// Por defecto a true
		return true;
	}
//	
//	public static void main (String[] args) {
//		System.out.println(new BigDecimal (2014).compareTo(Constants.PLAN_2015));
//	}
//	
//	
//	
//public Map<String,Object> muestraBotonDescuentoAnexo(Poliza poliza, AnexoModificacion anexo, Usuario usuario) throws Exception {
//		
//		Map<String,Object> params = new HashMap<String, Object>();
//		params.put(CalculoRCManager.MUESTRA_BOTON_DESCUENTOS, false);
//		params.put(CalculoRCManager.MUESTRA_BOTON_RECARGOS, false);
//		
//		// Comprueba si la poliza esta en uno de los estados de renovables que permiten aplicar descuentos/recargos a ambos grupos de negocio
//		boolean isEstadosRenovable = Constants.ESTADO_POLIZA_PRECARTERA_GENERADA.equals(poliza.getEstadoPoliza().getIdestado()) || 
//									 Constants.ESTADO_POLIZA_PRECARTERA_PRECALCULADA.equals(poliza.getEstadoPoliza().getIdestado()) ||
//									 Constants.ESTADO_POLIZA_PRIMERA_COMUNICACION.equals(poliza.getEstadoPoliza().getIdestado());
//		
//		logger.debug("MPM --> isEstadosRenovable: " + isEstadosRenovable);
//		
//		try {	
//			// No se pueden aplicar descuentos/recargos si se cumple cualquiera de los siguientes casos:
//			// - El plan de la poliza asociada al anexo es menor de 2015
//			// - La poliza asociada al anexo es agricola 
//			// - La poliza asociada al anexo es de ganado y de la linea 415 (a no ser que esta en alguno de los estados de las renovables que lo permiten)
//			if (poliza.getLinea().getCodplan().compareTo(Constants.PLAN_2015)== -1	|| !anexo.getPoliza().getLinea().isLineaGanado() || 
//				(anexo.getPoliza().getLinea().getCodlinea().equals(new BigDecimal("415")) && !isEstadosRenovable)) {
//				params.put(CalculoRCManager.MUESTRA_BOTON_DESCUENTOS, false);
//				params.put(CalculoRCManager.MUESTRA_BOTON_RECARGOS, false);
//			}
//			else {
//					// COMPROBACIONES POR PERFIL
//					// Perfil 0,5 o 1 interno
//					if (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_ADMINISTRADOR)|| // perfil 0
//						usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SEMIADMINISTRADOR)|| //perfil 5
//						(!usuario.isUsuarioExterno()&& usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES))){//perfil 1 interno 
//						
//						BigDecimal dtoMax=new BigDecimal(0);
//						boolean encontrado=false;
//						for(PolizaPctComisiones ppc: anexo.getPoliza().getSetPolizaPctComisiones()){
//							if((!isEstadosRenovable && ppc.getGrupoNegocio().equals(Constants.GRUPO_NEGOCIO_RYD)) || isEstadosRenovable){
//								if(null!=ppc.getPctdescmax() && dtoMax.compareTo(ppc.getPctdescmax())==(-1)){
//									dtoMax=ppc.getPctdescmax();
//									encontrado=true;
//									break;
//								}
//							}
//						}
//						params.put(CalculoRCManager.MUESTRA_BOTON_DESCUENTOS, true);
//						params.put(CalculoRCManager.MUESTRA_BOTON_RECARGOS, true);
//						if(!encontrado){
//							dtoMax=new BigDecimal(100);
//						}
//						params.put("miDtoMax", dtoMax);
//						params.put("pctdescmax",dtoMax.intValue());
//						params.put("validarRango", false);
//					}
//					// perfil 	2,3,4 o externo (1 o 3)
//					else if (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_JEFE_ZONA)||//Perfil 2
//							usuario.getPerfil().equals(Constants.PERFIL_USUARIO_OFICINA)|| // Perfil 3 int/ext
//							  usuario.getPerfil().equals(Constants.PERFIL_USUARIO_OTROS)|| // perfil 4
//							  (usuario.isUsuarioExterno() && usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES))){ //perfil 1 ext
//						params=this.checkRestriccionesPorPerfil(poliza, usuario);						
//					}
//					else {
//						params.put(CalculoRCManager.MUESTRA_BOTON_DESCUENTOS, false);
//						params.put(CalculoRCManager.MUESTRA_BOTON_RECARGOS, false);
//					}
//					
//					// Si el usuario tiene permiso para ver algun boton, se comprueban el estado de la poliza asociada y las explotaciones para verificar 
//					// si aplica que se muestren los botones
//					logger.debug("MPM --> Antes de comprobar permisos para botones");
//					if ((Boolean) params.get(CalculoRCManager.MUESTRA_BOTON_DESCUENTOS) || (Boolean) params.get(CalculoRCManager.MUESTRA_BOTON_RECARGOS))  {
//						
//						logger.debug("MPM --> Algun permiso para botones");
//						
//						// Si el estado de la poliza asociada es uno de los siguientes (Precartera generada, Precartera precalculada o Primera comunicacion)  
//						// o al comprobar las explotaciones se determina que se pueden aplicar descuentos/recargos se muestran los botones correspondientes
//						if (isEstadosRenovable || this.checkRestriccionesPorExplotacion(anexo.getId(),anexo.getCupon().getIdcupon())) {
//							// Los datos de la poliza/anexo permiten que se muestren los botones descuento/recargo, segun las comprobaciones de perfil previas 
//							logger.debug("MPM --> Los datos de la poliza/anexo permiten que se muestren los botones descuento/recargo");
//							params.put("pctComisiones", getPolizasPctComisionesPodDistCostes(anexo, (BigDecimal)params.get("miDtoMax"), isEstadosRenovable));
//						}
//						else {
//							// Los datos de la poliza/anexo NO permiten que se muestren los botones descuento/recargo, segun las comprobaciones de perfil previas
//							logger.debug("MPM --> Los datos de la poliza/anexo NO permiten que se muestren los botones descuento/recargo");
//							params.put(CalculoRCManager.MUESTRA_BOTON_DESCUENTOS, false);
//							params.put(CalculoRCManager.MUESTRA_BOTON_RECARGOS, false);
//						}
//					}
//		}
//			
//		}catch (Exception e) {
//			logger.error("Error comprobar muestraBotonDescuento-Recargo");
//			throw e;
//		}
//		
//		
//		return params;
//	}
//	
//	private List<PolizaPctComisiones>getPolizasPctComisionesPodDistCostes(AnexoModificacion anexo, BigDecimal dto, boolean isEstadosRenovable) {
//
//		List<PolizaPctComisiones>pctComisiones= new ArrayList<PolizaPctComisiones>();
//		for (PolizaPctComisiones pctComis : anexo.getPoliza().getSetPolizaPctComisiones()) {
//			logger.debug("MPM --> Dentro del bucle de PolizaPctComisiones - GN=" + pctComis.getGrupoNegocio());
//			if (null != pctComis.getGrupoNegocio() && (pctComis.getGrupoNegocio().equals(Constants.GRUPO_NEGOCIO_RYD) || isEstadosRenovable)){
//				logger.debug("MPM --> Dentro del IF");
//				for (AnexoModDistribucionCostes dstCos : anexo.getAnexoModDistribucionCosteses()) {
//					logger.debug("MPM --> Dentro del bucle de AnexoModDistribucionCostes");
//					if (dstCos != null && dstCos.getId() != null){
//						logger.debug("tipoDC: "+dstCos.getId().getTipoDc()+" gr: "+dstCos.getId().getGrupoNegocio());
//						if(dstCos.getId().getTipoDc().compareTo(new Integer(0))==0 && pctComis.getGrupoNegocio().equals(dstCos.getId().getGrupoNegocio())){
//							logger.debug("MPM --> Dentro del IF de GN");
//							String descGrNeg=null;
//							try {
//								descGrNeg= polizasPctComisionesDao.getDescripcionGrupoNegocio(pctComis.getGrupoNegocio());
//							} catch (DAOException e) {
//								logger.error("Error al recoger la descripcion del grupo de negocio",e);
//							}
//							
//							PolizaPctComisiones pPctAuxiliar = new PolizaPctComisiones();
//							pPctAuxiliar.setId(pctComis.getId());
//							pPctAuxiliar.setGrupoNegocio(pctComis.getGrupoNegocio());
//							pPctAuxiliar.setDescGrupoNegocio(descGrNeg);
//							pPctAuxiliar.setPctdescelegido(Constants.GRUPO_NEGOCIO_RYD.equals(pctComis.getGrupoNegocio()) ? anexo.getPctdescelegido() : anexo.getPctdescelegidoResto());
//							pPctAuxiliar.setPctrecarelegido(Constants.GRUPO_NEGOCIO_RYD.equals(pctComis.getGrupoNegocio()) ? anexo.getPctrecarelegido() : anexo.getPctrecarelegidoResto());
//							pPctAuxiliar.setPctdescmax(dto);
//							
//							pctComisiones.add(pPctAuxiliar);
//							logger.info("Para descuentos y Recargos, gr: "+pctComis.getGrupoNegocio()+" descGR: "+pctComis.getDescGrupoNegocio()+" pcdDescMax: "+pctComis.getPctdescmax() + " descuento: "+pctComis.getPctdescelegido() + " recargo: "+pctComis.getPctrecarelegido());
//							//break;
//						}
//					}
//				}
//			}
//		}	
//		return pctComisiones;		
//	}
//		
////#####################DESCUENTOS Y RECARGOS ##########################################
//	public void actualizaAnexoDescuento(final String[] varRequest, final Long idAnexo) throws Exception{
//		//dctoPctComisiones contiene unidades de cuatro valores, es decir, cada cuatro valores pertenecen a un grupo de negocio;
//		//Nos interesan el primer valor(porcentaje decuento elegido) y el segundo(id de PolizaPctComisiones):
//		//Los datos provienen del popup de descuentos
//		//pctelegido	idPctComisiones	pctMax	grupoNegocio pctrecelegido
//		//12, 			5406, 					, 1			
//		//35, 			5405, 			2.35, 	  2
//		Map<Long,ObjDesRecarGN> valoresPctComis = getValoresPolizaPctComisionesRequest(varRequest,2,1,5,4);
//		
//		for(Map.Entry<Long,ObjDesRecarGN> entry : valoresPctComis.entrySet()){
//			updateDescuentoAnexo(entry.getValue(), idAnexo);
//		}
//	}
//	
//	public void actualizaAnexoRecargo(final String[] varRequest, final Long idAnexo) throws Exception{
//		//dctoPctComisiones contiene unidades de cuatro valores, es decir, cada cuatro valores pertenecen a un grupo de negocio;
//		//Nos interesan el primer valor(porcentaje decuento elegido) y el segundo(id de PolizaPctComisiones):
//		//Los datos provienen del popup de descuentos
//		//pctelegido	idPctComisiones	pctMax	grupoNegocio
//		//12, 			5406, 					, 1			
//		//35, 			5405, 			2.35, 	  2
//		Map<Long,ObjDesRecarGN> valoresPctComis = getValoresPolizaPctComisionesRequest(varRequest,2,1,4,3);
//		for(Map.Entry<Long,ObjDesRecarGN> entry : valoresPctComis.entrySet()){
//			updateRecargoAnexo(entry.getValue(), idAnexo);
//		}
//	}
//	
//	private Map<Long,ObjDesRecarGN>getValoresPolizaPctComisionesRequest(String [] varRequest, int indId, int indValor, int indGrupoValores, int indGN){
//		Map<Long,ObjDesRecarGN> res = new HashMap<Long, ObjDesRecarGN>();
//		//5.resto = numero1%numero2;
//		int ii=1;
//		Long idPctComisiones=null;
//		ObjDesRecarGN obj = null;
//		BigDecimal valorPctComisiones=null;
//		Character grupoNegocio = null;
//		
//		for (int i = 0; i < varRequest.length; i++) {
//			
//			if (ii == 1) obj = new ObjDesRecarGN();
//			
//			if(ii==indId){
//				idPctComisiones=new Long(varRequest[i]);
//			}
//			if(ii==indValor){
//				if(varRequest[i].isEmpty() || varRequest[i].equals(new String("0")) ){
//					valorPctComisiones=null;
//				}else{
//					valorPctComisiones=new BigDecimal(varRequest[i]);
//				}				
//			}
//			if (ii==indGN) {
//				grupoNegocio = new Character (varRequest[i].charAt(0));
//			}
//			if(ii%indGrupoValores==0){
//				obj.setGrupoNegocio(grupoNegocio);
//				obj.setValorPctComisiones(valorPctComisiones);
//				res.put(idPctComisiones, obj);
//				idPctComisiones=null; valorPctComisiones=null; ii=0;
//			}
//			ii+=1;
//		}
//		
//		return res;
//	}
//	
//	private class ObjDesRecarGN {
//		private Character grupoNegocio;
//		private BigDecimal valorPctComisiones;
//		public Character getGrupoNegocio() {
//			return grupoNegocio;
//		}
//		public void setGrupoNegocio(Character grupoNegocio) {
//			this.grupoNegocio = grupoNegocio;
//		}
//		public BigDecimal getValorPctComisiones() {
//			return valorPctComisiones;
//		}
//		public void setValorPctComisiones(BigDecimal valorPctComisiones) {
//			this.valorPctComisiones = valorPctComisiones;
//		}
//		
//	}
//	
//	public void updateDescuentoAnexo(ObjDesRecarGN descuento, Long idAnexo) throws Exception{		
//		try {
//			polizasPctComisionesDao.updateDescuentoAnexo(descuento.getValorPctComisiones(),idAnexo, descuento.getGrupoNegocio());
//			
//		}catch (Exception e) {
//			logger.error("Error al actualizar el descuento de la poliza - updateDescuento");
//			throw e;
//		}
//	}
//	
//	public void updateRecargoAnexo(ObjDesRecarGN recargo, Long idAnexo) throws Exception{		
//		try {
//			polizasPctComisionesDao.updateRecargoAnexo(recargo.getValorPctComisiones(),idAnexo, recargo.getGrupoNegocio());
//			
//		}catch (Exception e) {
//			logger.error("Error al actualizar el descuento de la poliza - updateRecargoAnexo");
//			throw e;
//		}
//	}
//		
//	public void updatePorcentajesAnexo(es.agroseguro.iTipos.Gastos gas, Long idAnexo) throws Exception{		
//		try {
//			polizasPctComisionesDao.updatePorcentajesAnexo(gas,idAnexo);
//			
//		}catch (Exception e) {
//			logger.error("Error al actualizar los porcentajes del anexo - updatePorcentajesAnexo");
//			throw e;
//		}
//	}
//	public BigDecimal comprobarTotalRecargo(Long idAnexo,BigDecimal recargo) throws Exception{
//		BigDecimal comision=null;
//		BigDecimal pctEntidad=null;
//		
//		try {
//			// Cargar el anexo asociado al id recibido para obtener los parametros necesarios en la llamda al SW  
//			AnexoModificacion am =this.getAnexo(idAnexo);
//			PolizaPctComisiones pc = new PolizaPctComisiones();
//			@SuppressWarnings("unchecked")
//			List<PolizaPctComisiones> lstPct= (List<PolizaPctComisiones>) anexoModificacionDao
//						.getObjects(PolizaPctComisiones.class, "poliza.idpoliza", am.getPoliza().getIdpoliza());
//			// Buscamos las pct comisiones de grupo negocio RyD
//			for (PolizaPctComisiones pct:lstPct) {
//				if (pct.getGrupoNegocio().equals(Constants.GRUPO_NEGOCIO_RYD)){
//					if (pct.getId() != null)
//						pc.setId(pct.getId());
//					if (pct.getDescGrupoNegocio() != null)
//						pc.setDescGrupoNegocio(pct.getDescGrupoNegocio());
//					if (pct.getEstado() != null)
//						pc.setEstado(pct.getEstado());
//					if (pct.getGrupoNegocio() != null)
//						pc.setGrupoNegocio(pct.getGrupoNegocio());
//					if (pct.getPctadministracion() != null)
//						pc.setPctadministracion(pct.getPctadministracion());
//					if (pct.getPctadquisicion() != null)
//						pc.setPctadquisicion(pct.getPctadquisicion());
//					if (pct.getPctcommax() != null)
//						pc.setPctcommax(pct.getPctcommax());
//					if (pct.getPctdescelegido() != null)
//						pc.setPctdescelegido(pct.getPctdescelegido());
//					if (pct.getPctdescmax() != null)
//						pc.setPctdescmax(pct.getPctdescmax());
//					if (pct.getPctentidad() != null){
//						pc.setPctentidad(pct.getPctentidad());
//						pctEntidad=pct.getPctentidad();
//					}
//					if (pct.getPctesmediadora() != null)
//						pc.setPctesmediadora(pct.getPctesmediadora());
//					
//						pc.setPctrecarelegido(recargo);
//					if (pct.getPoliza() != null)
//						pc.setPoliza(pct.getPoliza());
//				}
//			}
//			comision = PolizaUnificadaTransformer.obtenerComisionMediador(pc, true, null);
//			
//		}catch (Exception e) {
//			logger.error("Error al actualizar los porcentajes del anexo - updatePorcentajesAnexo");
//			throw e;
//		}
//		logger.info("comision calculada: "+comision + " pctEntidad: "+pctEntidad);
//		return pctEntidad != null?comision.add(pctEntidad):comision;
//	}
//	
//	public Map<String,Object> checkRestriccionesPorPerfil(Poliza poliza, Usuario usuario){
//		Map<String,Object> params = new HashMap<String, Object>();
//	try{
//		List<BigDecimal> oficinas=new ArrayList<BigDecimal>();
//		if(usuario.getPerfil().equals(Constants.PERFIL_USUARIO_JEFE_ZONA)){
//			oficinas.addAll(usuario.getListaCodOficinasGrupo());
//		}else{
//			oficinas.add(usuario.getOficina().getId().getCodoficina());
//		}
//			Descuentos dto=polizasPctComisionesDao.getDescuentos(
//			poliza.getColectivo().getTomador().getId().getCodentidad(),
//			oficinas,
//			poliza.getColectivo().getSubentidadMediadora().getId().getCodentidad(),
//			poliza.getColectivo().getSubentidadMediadora().getId().getCodsubentidad(),
//			usuario.getDelegacion(),poliza.getLinea().getCodplan(),poliza.getLinea().getCodlinea());
//			if(null==dto){
//				params.put(CalculoRCManager.MUESTRA_BOTON_DESCUENTOS, false);
//				params.put(CalculoRCManager.MUESTRA_BOTON_RECARGOS, false);
//			}else{
//				if(dto.getPctDescMax().compareTo(new BigDecimal(0))>0){
//					params.put(CalculoRCManager.MUESTRA_BOTON_DESCUENTOS, true);
//					params.put("pctdescmax",dto.getPctDescMax());
//					params.put("validarRango", true);
//				}else{
//					params.put(CalculoRCManager.MUESTRA_BOTON_DESCUENTOS, false);
//					params.put("validarRango", false);
//				}
//				if(dto.getPermitirRecargo().compareTo(new Integer(1))==0){
//					params.put(CalculoRCManager.MUESTRA_BOTON_RECARGOS, true);
//				}
//				params.put("miDtoMax",dto.getPctDescMax());
////				List<PolizaPctComisiones> listaPctCom=new ArrayList<PolizaPctComisiones> ();
////				PolizaPctComisiones polPctCom=new PolizaPctComisiones();
////				polPctCom.setPctdescmax(dto.getPctDescMax());
////				listaPctCom.add(polPctCom);
////				params.put("pctComisiones", listaPctCom);
//			}
//		
//	}catch(Exception ex){
//		logger.error("Error en CalculoRCManager checkRestriccionesPorPerfil ",ex);
//	}
//	return params;
//	}
//	
//	/**
//	 *@param idAnexo
//	 * retorna true si se cumple
//	 * 1.- La primera explotacion del anexo modificada o dad de alta contiene algun
//	 *     capital de retirada
//	 * 2.- La primera explotacion de la situacion actualizada asociada al anexo no 
//	 *     contiene tipo de capital de retirada
//	 */
//	private boolean checkRestriccionesPorExplotacion(Long idAnexo,String idCupon){
//	boolean resultado=false;
//		try{
//		if(anexoModificacionDao.checkExplotacionesTCRyD(idAnexo))	
//			return !this.checkSitActRyD(idCupon);
//		}catch(Exception ex){
//			logger.error("Error en CalculoRCManager checkRestriccionesPorExplotacion ",ex);
//		
//		}
//		return resultado;
//	}
//	/**
//	 * 
//	 * @param idCupon
//	 * @return 
//	 * Retorna un booleano indicando si en la situacion actualizada almacenada en BBDD
//	 *  asociada al id de cupon aparece el grupo de negocio de RyD
//	 */
//	private boolean checkSitActRyD(String idCupon){
//		boolean resultado=false;
//		List<Integer> tipos=new ArrayList<Integer>();
//			try{
//			
//			es.agroseguro.contratacion.PolizaDocument polizaAct = (es.agroseguro.contratacion.PolizaDocument) solicitudModificacionManager
//					.getPolizaActualizadaFromCupon(idCupon);
//		        es.agroseguro.contratacion.Poliza poliza=polizaAct.getPoliza();
//		      
//				Node currNode = poliza.getObjetosAsegurados().getDomNode().getFirstChild();
//				
//				while (currNode != null) {
//					if (currNode.getNodeType() == Node.ELEMENT_NODE) {
//						
//						ExplotacionDocument xmlExplotacion = null;
//						xmlExplotacion = ExplotacionDocument.Factory.parse(currNode);
//
//						if (xmlExplotacion != null){
//							for(GrupoRaza gr:xmlExplotacion.getExplotacion().getGrupoRazaArray()){
//								for(CapitalAsegurado ca:gr.getCapitalAseguradoArray()){
//									tipos.add(new Integer(ca.getTipo()));
//								}
//							}
//							break;
//						}
//					}
//					currNode = currNode.getNextSibling();
//				}
//				
//		     
//			resultado=anexoModificacionDao.checkTCRyD(tipos);
//			}catch(Exception ex){
//				logger.error("Error en CalculoRCManager checkRestriccionesPorExplotacion ",ex);
//			
//			}
//			  return resultado;
//		}
//	
	/**
	 * Setter para Spring
	 * @param parametrizacionManager
	 */
	public void setParametrizacionManager(ParametrizacionManager parametrizacionManager) {
		this.parametrizacionManager = parametrizacionManager;
	}
//
//	/**
//	 * Setter para Spring
//	 * @param anexoModificacionDao
//	 */
//	public void setAnexoModificacionDao(IAnexoModificacionDao anexoModificacionDao) {
//		this.anexoModificacionDao = anexoModificacionDao;
//	}
//	
	public Poliza getPoliza(Long id) {
		Poliza poliza = (Poliza) reduccionCapitalDao.getObject(Poliza.class, id);
		return poliza;
	}
	
	public ReduccionCapital getRC(Long id) {
		ReduccionCapital rc = (ReduccionCapital) reduccionCapitalDao.getObject(ReduccionCapital.class, id);
		return rc;
	}
	
//	public AnexoModificacion getAnexoPorIdCupon(String idCupon) throws DAOException{
//		return (AnexoModificacion)this.anexoModificacionDao.getAnexoByIdCupon(idCupon);
//	}
//	
	/**
	 * Setter para Spring
	 * @param seleccionPolizaManager
	 */
	public void setSeleccionPolizaManager(SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}
//
//	public void setSolicitudModificacionManager(
//			ISolicitudModificacionManager solicitudModificacionManager) {
//		this.solicitudModificacionManager = solicitudModificacionManager;
//	}

	public void setPolizasPctComisionesManager(
			PolizasPctComisionesManager polizasPctComisionesManager) {
		this.polizasPctComisionesManager = polizasPctComisionesManager;
	}
//
//	public void setPolizasPctComisionesDao(
//			IPolizasPctComisionesDao polizasPctComisionesDao) {
//		this.polizasPctComisionesDao = polizasPctComisionesDao;
//	}
	
	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}

}
