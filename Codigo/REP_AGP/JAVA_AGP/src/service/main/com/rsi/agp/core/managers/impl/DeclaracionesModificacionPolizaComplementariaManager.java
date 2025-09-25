package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.ValidacionAnexoModificacionException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.managers.impl.anexoMod.solicitud.ISolicitudModificacionManager;
import com.rsi.agp.core.managers.impl.anexoMod.util.AnexoModificacionUtils;
import com.rsi.agp.core.report.anexoMod.RelacionEtiquetaTabla;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.XmlTransformerUtil;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.anexo.IDeclaracionesModificacionPolizaComplementariaDao;
import com.rsi.agp.dao.models.anexo.IXmlAnexoModificacionDao;
import com.rsi.agp.dao.models.config.IDiccionarioDatosDao;
import com.rsi.agp.dao.models.copy.IPolizaCopyDao;
import com.rsi.agp.dao.models.cpm.ICPMTipoCapitalDAO;
import com.rsi.agp.dao.models.impl.DatabaseManager;
import com.rsi.agp.dao.models.poliza.IPolizaComplementariaDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;
import com.rsi.agp.dao.tables.anexo.CapitalDTSVariable;
import com.rsi.agp.dao.tables.anexo.Estado;
import com.rsi.agp.dao.tables.anexo.Parcela;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;

public class DeclaracionesModificacionPolizaComplementariaManager implements IManager{
	private static final Log logger = LogFactory.getLog(DeclaracionesModificacionPolizaComplementariaManager.class);
	
	private IDeclaracionesModificacionPolizaComplementariaDao modificacionPolizaComplementariaDao;
	private IXmlAnexoModificacionDao xmlAnexoModDao;
	private IPolizaCopyDao polizaCopyDao;
	private IPolizaComplementariaDao polizaComplementariaDao;
	private ICPMTipoCapitalDAO cpmTipoCapitalDao;
	private DatabaseManager databaseManager;
	private DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager;
	
	private IDiccionarioDatosDao diccionarioDatosDao;
	private ISolicitudModificacionManager solicitudModificacionManager;	
	
	public List<CapitalAsegurado> getCapitalesAsegPolCpl(CapitalAsegurado capitalAseguradoBean) throws BusinessException  {
		logger.debug("init - getCapitalesAsegPolCpl");
		try {
				
			logger.debug("end - getCapitalesAsegPolCpl");
			return modificacionPolizaComplementariaDao.getCapitalesAsegPolCpl(capitalAseguradoBean);
					
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar el listado de parcelas",dao);
			throw new BusinessException ("Se ha producido un error al recuperar el listado de parcelas",dao);
		}
	}

	/**
	 * Copia las parcelas de una poliza CPL a anexo
	 * @param anexoModificacion
	 * @throws DAOException
	 */
	public void copiarParcelasFromPolizaOrCopy(Long  idanexoModificacion) throws DAOException {
		logger.debug("init - copiarParcelasPolizaCopy");
		try {
			
			String procedure = "pq_copiar_parcelas_cpl_a_anexo.copiarParcelasEnAnexo(P_IDANEXO IN NUMBER)";			
			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("P_IDANEXO", idanexoModificacion);
			logger.debug("llamada al PL: pq_copiar_parcelas_cpl_a_anexo.copiarParcelasEnAnexo(" + idanexoModificacion + ")");
			
			databaseManager.executeStoreProc(procedure, parametros); 
			
		}catch(Exception excepcion) {
			logger.error("Se ha producido un error al copiar las parcelas al anexo ",excepcion);
			throw new DAOException("Se ha producido un error al copiar las parcelas al anexo ",excepcion);
		}
		logger.debug("end - copiarParcelasPolizaCopy");
	}
	
	/**
	 * Copia las parcelas de la p贸liza actualizada complementaria al anexo
	 * @param anexoModificacion
	 * @throws DAOException
	 * @throws XmlException 
	 */
	public void copiarParcelasFromPolizaActualizada(Long idAnexo, String idCupon, Long lineaseguroid) throws DAOException, XmlException {
		//Obtengo el codigo de cup贸n de la base de datos
		//Cupon cupon = (Cupon) this.cuponDao.get(Cupon.class, Long.parseLong(idCupon));
		
		//Obtengo la poliza principal actualizada
		
		/* Pet. 57626 ** MODIF TAM (11.06.2020) ** Inicio */
		/* Por los desarrollos de esta petici髇 tanto las polizas agricolas como las de ganado
		 * ir醤 por el mismo end-point y con formato Unificado
		 */
		es.agroseguro.contratacion.Poliza polizaPpl = ((es.agroseguro.contratacion.PolizaDocument) this.solicitudModificacionManager.getPolizaActualizadaFromCupon(idCupon)).getPoliza();
		
		//Obtengo la poliza complementaria actualizada
		es.agroseguro.contratacion.PolizaDocument pd = (es.agroseguro.contratacion.PolizaDocument) this.solicitudModificacionManager.getPolizaActualizadaCplFromCupon(idCupon);
		
		es.agroseguro.contratacion.Poliza polizaCpl = null;
		if (pd != null){
			polizaCpl= pd.getPoliza();
		}
		// Mapa auxiliar con los codigos de concepto de los datos variables y sus etiquetas y tablas asociadas.
		Map<BigDecimal, RelacionEtiquetaTabla> auxEtiquetaTabla = 
				this.diccionarioDatosDao.getCodConceptoEtiquetaTablaParcelas(lineaseguroid);
		
		List<Parcela> parcelasPpl = AnexoModificacionUtils.getParcelasAnexoFromPolizaActualizada(polizaPpl, idAnexo, auxEtiquetaTabla);
		List<Parcela> parcelasCpl = new ArrayList<Parcela>();
		if (polizaCpl != null)
			parcelasCpl = AnexoModificacionUtils.getParcelasAnexoCplFromPolizaActualizada(polizaCpl, idAnexo);
		
		List<Parcela> parcelas = AnexoModificacionUtils.getParcelasAnexoCpl(parcelasPpl, parcelasCpl);
		
		this.modificacionPolizaComplementariaDao.saveOrUpdateList(parcelas);
		
		//elimino las nuevas parcelas de la sesi贸n de hibernate
		for (Parcela p : parcelas){
			this.modificacionPolizaComplementariaDao.evict(p);
		}
		
		//Instalaciones: identificar y asignar la parcela asociada ????????????????????????????
		//this.parcelaModificacionPolizaDao.asignarInstalacionesFromPolizaActualizada(idAnexo);
	}
	
	/**
	 * Metodo que genera los listado de checks seleccionados y los incrementos para su proceso en java
	 * @param request
	 * @throws BusinessException 
	 */
	public void guardarIncrementoParcelas(HttpServletRequest request,CapitalAsegurado capitalBean) throws BusinessException {
		logger.debug("init - guardarIncrementoParcelas");
		
		String[] listaChecksAux =  null;
		String[] listaIncrAux  = null;
		
		String listaChecks = StringUtils.nullToString(request.getParameter("checksSel"));
		String listaIncr = StringUtils.nullToString(request.getParameter("incrSel"));
		logger.debug("listado de checks seleccionados: " + listaChecks);
		logger.debug("listado de incrementos: " + listaIncr);
		
		HashMap<String, String> checks = new HashMap<String, String>();
		HashMap<String, BigDecimal> incr = new HashMap<String, BigDecimal>();
		
		if(!listaChecks.equals("")){
			listaChecksAux = listaChecks.split("\\|");
			for(int i=0;i<listaChecksAux.length;i++){
				checks.put(listaChecksAux[i].split("#")[0], listaChecksAux[i].split("#")[1]);
			}
		}
		if(!listaIncr.equals("")){
			listaIncrAux = listaIncr.split("\\|");
			for(int i=0;i<listaIncrAux.length;i++){
				incr.put(listaIncrAux[i].split("#")[0], new BigDecimal(listaIncrAux[i].split("#")[1]));
			}
		}
		
		this.updateParcelaCplAnexo(checks,incr,capitalBean);
		logger.debug("end - guardarIncrementoParcelas");
	}
	
	
	private void updateParcelaCplAnexo(HashMap<String, String> checks,HashMap<String, BigDecimal> incr,CapitalAsegurado capitalBean) throws BusinessException {
		logger.debug("init - updateParcelaCplAnexo");
		
		CapitalAsegurado capitalAnexo = null;
		List<CapitalAsegurado> listCapitales = new ArrayList<CapitalAsegurado>();
		try {
//			REINICIAMOS ALTACOMPLEMENTARIO DE TODAS LAS PARCELAS A NO
			List<CapitalAsegurado> listCapAsegFiltro = modificacionPolizaComplementariaDao.getCapitalesAsegPolCpl(capitalBean);
			for(CapitalAsegurado cap : listCapAsegFiltro){
				cap.setAltaencomplementario('N');
				cap.setIncrementoproduccion(null);
				cap.getParcela().setAltaencomplementario('N');
				cap.getParcela().setTipomodificacion(null);
				cap.setTipomodificacion(null);
				modificacionPolizaComplementariaDao.saveOrUpdate(cap);
			}
//			GENERAMOS UN LISTADO CON TODOS LOS CAPITALES SELECIONADOS Y ACTUALIZAMOS SU ESTADO
			for(String idCapital:checks.keySet()){
				capitalAnexo = (CapitalAsegurado) modificacionPolizaComplementariaDao.get(CapitalAsegurado.class, new Long(idCapital));
				
				if(checks.get(idCapital).equals("A")){
					
					capitalAnexo.setAltaencomplementario(new Character('S'));
					capitalAnexo.setIncrementoproduccion(incr.get(idCapital));
					capitalAnexo.setTipomodificacion(new Character('A'));
					capitalAnexo.getParcela().setAltaencomplementario(new Character('S'));
					
				}else if (checks.get(idCapital).equals("B")) {
					
					capitalAnexo.setAltaencomplementario(new Character('N'));
					capitalAnexo.setIncrementoproduccion(null);
					capitalAnexo.setTipomodificacion(new Character('B'));
					
				}else if (checks.get(idCapital).equals("M")) {
					
					capitalAnexo.setAltaencomplementario(new Character('S'));
					capitalAnexo.setIncrementoproduccion(incr.get(idCapital));
					capitalAnexo.setTipomodificacion(new Character('M'));
					capitalAnexo.getParcela().setAltaencomplementario(new Character('S'));
					
				}
				listCapitales.add(capitalAnexo);
			}
			
			modificacionPolizaComplementariaDao.saveOrUpdateList(listCapitales);
			
			this.actualizarModifParcelas(capitalBean.getParcela().getAnexoModificacion().getId());
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al actualizar el anexo de parcelas complementarias",dao);
			throw new BusinessException ("Se ha producido un error al actualizar el anexo de parcelas complementarias",dao);
		}
		
		logger.debug("end - updateParcelaCplAnexo");
	}
	
	private void actualizarModifParcelas(Long idAnexo) throws DAOException {
		logger.debug("init - actualizarModifParcelas");
		
		AnexoModificacion anexo = (AnexoModificacion) modificacionPolizaComplementariaDao.get(AnexoModificacion.class, idAnexo);
		Set<Character> modif = null;
		for(Parcela parcela:anexo.getParcelas()){
			modif = new HashSet<Character>();
			for(CapitalAsegurado capital:parcela.getCapitalAsegurados()){
				if(capital.getTipomodificacion() != null){
					modif.add(capital.getTipomodificacion());
				}
			}
			
			Iterator<Character> iter = modif.iterator();
			while(iter.hasNext()){
				if(modif.size() == 1){
					Character val = (Character) iter.next();
					parcela.setTipomodificacion(val);
				}else if (modif.size() > 1) {
					Character val = new Character('M');
					parcela.setTipomodificacion(val);
				}
			}
			modificacionPolizaComplementariaDao.saveOrUpdate(parcela);
		}
		
		logger.debug("end - actualizarModifParcelas");
	}

	public void getListas(List<CapitalAsegurado> listCapAseg,StringBuilder capitales, StringBuilder incrementos) {
		logger.debug("init - getListas");
		for(CapitalAsegurado cap: listCapAseg){
				if(cap.getTipomodificacion() != null && !cap.getTipomodificacion().equals(new Character(' '))){
					capitales.append(cap.getId().toString() + "#" + cap.getTipomodificacion().toString()+ "|");
					if(cap.getIncrementoproduccion() != null)
						incrementos.append(cap.getId() + "#" +  cap.getIncrementoproduccion().toString() + "|"); 
				}
		}
		logger.debug("end - getListas");
	}
	
	/**
	 * M茅todo para actualizar el xml de un anexo
	 * 
	 * @param anexo   Anexo a actualizar
	 * @throws BusinessException
	 * @throws DAOException 
	 * @throws ValidacionAnexoModificacionException 
	 */
	private void actualizaXml(AnexoModificacion anexo, boolean validarEstructura) 
	throws BusinessException, DAOException, ValidacionAnexoModificacionException {
		logger.debug("init - actualizaXml");
			
		ModuloId idModulo = new ModuloId();			
		idModulo.setCodmodulo(anexo.getPoliza().getCodmodulo());
		idModulo.setLineaseguroid(anexo.getPoliza().getLinea().getLineaseguroid());
		Modulo modulo = polizaComplementariaDao.getModuloPPalPoliza(idModulo);
		logger.debug("parametros del modulo: codmodulo - " + anexo.getPoliza().getCodmodulo() + "codmoduloPPAL - " + modulo.getCodmoduloasoc() + " lineaseguroid - "+ anexo.getPoliza().getLinea().getLineaseguroid());
		
		// C谩lculo de CPM permitidos			
		logger.debug("Se cargan los CPM permitidos para la p贸liza y el anexo relacionado - idPoliza: " + anexo.getPoliza().getIdpoliza() + ", idAnexo: " + anexo.getId() + ", codModulo: " + anexo.getPoliza().getCodmodulo());
		List<BigDecimal> listaCPM = cpmTipoCapitalDao.getCPMDePolizaAnexoMod(anexo.getPoliza().getIdpoliza(), anexo.getId(), anexo.getPoliza().getCodmodulo());
		
		logger.debug("actualizamos el xml del anexo");
		boolean modAseg=false;
		try{
			XmlTransformerUtil.updateXMLAnexoModCpl(xmlAnexoModDao, polizaCopyDao,anexo,modulo.getCodmoduloasoc(),modAseg, listaCPM, validarEstructura);
		}catch (ValidacionAnexoModificacionException e){
			logger.error("Error validando el xml de de Anexos de Modificaci贸n" + e.getMessage());
			throw new ValidacionAnexoModificacionException (e.getMessage() );
		}catch (Exception ee){
			logger.error("Error generico al actualizar el xml de Anexos de Modificaci贸n" + ee.getMessage());
			throw new BusinessException ("Error gen茅rico al actualizar el xml de Anexos de Modificacion");
		}
		logger.debug("end - actualizaXml");
	}
	/**
	 *  Pasa a definitiva un A.M de una poliza CPL
	 *  TMR 28-06-2013 llamada al PL para insertar el estado y usuario en el historico (historicoEstadosManager.insertaEstado ) 
	 * @author U029769 28/06/2013
	 * @param idAnexo
	 * @param codUsuario
	 * @throws BusinessException
	 * @throws DAOException
	 * @throws ValidacionAnexoModificacionException
	 */
	public void pasarDefinitiva(Long idAnexo,String codUsuario) throws BusinessException,
	DAOException, ValidacionAnexoModificacionException{
		
		try{
			AnexoModificacion anexoModificacion =  (AnexoModificacion)modificacionPolizaComplementariaDao.get(AnexoModificacion.class, idAnexo);
			actualizaXml(anexoModificacion, true);
		
			Estado estado = new Estado(Constants.ANEXO_MODIF_ESTADO_DEFINITIVO);
			anexoModificacion.setFechafirmadoc(new Date());
			declaracionesModificacionPolizaManager.saveAnexoModificacion(anexoModificacion, codUsuario, estado, false);
			
		}catch (ValidacionAnexoModificacionException e){
			logger.error("Error validando el xml de de Anexos de Modificaci贸n" + e.getMessage());
			throw new ValidacionAnexoModificacionException (e.getMessage() );
		}catch (Exception ee){
			logger.error("Error generico al actualizar el xml de Anexos de Modificaci贸n" + ee.getMessage());
			throw new BusinessException ("Error gen茅rico al pasar a definitiva");
		}
	}
	
	/* Pet. 78691 ** MODIF TAM (21.12.2021) ** Inicio */
	/**
	 * Recorre las lista de parcelas de Anexos filtradas y devuelve una nueva
	 * lista filtrada ademas por sistema de cultivo
	 * 
	 * @param ListCapitalAsegurado
	 * @param sistcultivo
	 * @return ListCapitalAsegurado
	 */
	public List<CapitalAsegurado> getParcelasAnxCplFiltradas(List<CapitalAsegurado> listaparcelas, String sistcultivo) {
		
		List<CapitalAsegurado> listaParcelasFinal = new ArrayList<CapitalAsegurado>();

		logger.debug("**@@** ParcelasModificacionPolizaManager - getParcelasAnxFiltradas [INIT]");

		// Si se ha filtrado el sistema de cultivo por algun valor
		if (!"".equals(StringUtils.nullToString(sistcultivo))) {

			/** SONAR Q ** MODIF TAM (28.10.2021) ** Inicio **/
			/* Sacamos c骴igo fuera para descargar la funci髇 de ifs/fors */
			listaParcelasFinal = obtenerListParcAnxCplFinal(listaparcelas, sistcultivo);
			/** SONAR Q ** MODIF TAM (28.10.2021) ** Final **/

			return listaParcelasFinal;
		}
		// Si no filtro por sistcultivo devuelvo el listado de parcelas original
		else {
			return listaparcelas;
		}
	}
	
	private List<CapitalAsegurado> obtenerListParcAnxCplFinal(List<CapitalAsegurado> listparcAnx, String sistcultivo) {

		List<CapitalAsegurado> listParcFinal = new ArrayList<CapitalAsegurado>();
		
		for (CapitalAsegurado cap : listparcAnx) {
			for (CapitalDTSVariable datvar : cap.getCapitalDTSVariables()) {
				// Si la parcela tiene "Sistema de cultivo"
				if (datvar.getCodconcepto()
						.equals(new BigDecimal(ConstantsConceptos.CODCPTO_SISTCULTIVO))) {
					logger.debug("La parcela de Anexotiene sistema de cultivo");

					// Si el sistema de cultivo es igual que el
					// introducido en el filtro de busqueda se inserta
					// la parcela en la lista
					if (datvar.getValor().equals(sistcultivo)) {
						// Si la lista final no tiene la parcela
						// encontrada la meto
						if (!listParcFinal.contains(cap)) {
							listParcFinal.add(cap);
						}
					}
				}
			}
		}
		
		return listParcFinal;
	}
	
	public String obtenerDescSistCultivo(String sistCultivo) throws DAOException {
		return modificacionPolizaComplementariaDao.getdescSistCultivo(sistCultivo);
	}
	/* Pet. 78691 ** MODIF TAM (21.12.2021) ** Fin */



	public void setModificacionPolizaComplementariaDao(	IDeclaracionesModificacionPolizaComplementariaDao modificacionPolizaComplementariaDao) {
		this.modificacionPolizaComplementariaDao = modificacionPolizaComplementariaDao;
	}

	public void setDatabaseManager(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}

	public void setXmlAnexoModDao(IXmlAnexoModificacionDao xmlAnexoModDao) {
		this.xmlAnexoModDao = xmlAnexoModDao;
	}

	public void setPolizaCopyDao(IPolizaCopyDao polizaCopyDao) {
		this.polizaCopyDao = polizaCopyDao;
	}

	public void setPolizaComplementariaDao(IPolizaComplementariaDao polizaComplementariaDao) {
		this.polizaComplementariaDao = polizaComplementariaDao;
	}
	
	public void setCpmTipoCapitalDao(ICPMTipoCapitalDAO cpmTipoCapitalDao) {
		this.cpmTipoCapitalDao = cpmTipoCapitalDao;
	}

	public void setDeclaracionesModificacionPolizaManager(
			DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager) {
		this.declaracionesModificacionPolizaManager = declaracionesModificacionPolizaManager;
	}

	public void setDiccionarioDatosDao(IDiccionarioDatosDao diccionarioDatosDao) {
		this.diccionarioDatosDao = diccionarioDatosDao;
	}

	public void setSolicitudModificacionManager(
			ISolicitudModificacionManager solicitudModificacionManager) {
		this.solicitudModificacionManager = solicitudModificacionManager;
	}
}
