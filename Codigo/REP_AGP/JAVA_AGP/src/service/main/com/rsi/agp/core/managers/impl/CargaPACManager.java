package com.rsi.agp.core.managers.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.admin.IClaseDao;
import com.rsi.agp.dao.models.config.ICargaPACDao;
import com.rsi.agp.dao.models.config.IDatoVariableDao;
import com.rsi.agp.dao.models.poliza.ICapitalAseguradoDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.models.poliza.ISeleccionPolizaDao;
import com.rsi.agp.dao.tables.admin.ClaseDetalle;
import com.rsi.agp.dao.tables.cgen.SistemaCultivo;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.config.DatoVariableCargaParcela;
import com.rsi.agp.dao.tables.cpl.RelEspeciesSCEspeciesST;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;
import com.rsi.agp.dao.tables.pac.FormPacCargasBean;
import com.rsi.agp.dao.tables.pac.PacCargas;
import com.rsi.agp.dao.tables.pac.PacParcelas;
import com.rsi.agp.dao.tables.poliza.CapAsegRelModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class CargaPACManager implements IManager {

	private static final Log             logger = LogFactory.getLog(CargaPACManager.class);
	private static final ResourceBundle  bundle = ResourceBundle.getBundle("agp");
	
	private ICargaPACDao          cargaPACDao;
	private IClaseDao             claseDao;
	private ISeleccionPolizaDao   seleccionPolizaDao;
	private IPolizaDao            polizaDao;
	private IDatoVariableDao	  datoVariableDao;
	private ICapitalAseguradoDao  capitalAseguradoDao;
	
	public String cargarArchivoPAC(FormPacCargasBean formPacCargasBean, String codUsuario) throws BusinessException{

		try	{			        
			// Obtiene el fichero a procesar
			String fileName = formPacCargasBean.getFile().getOriginalFilename();
		
			// Sube el fichero 
			uploadFicheroPAC(formPacCargasBean, fileName);
			
			// Ejecuta el procedimiento almacenado encargado de cargar los datos del fichero en el modelo de datos de PAC
			return cargaPACDao.executeStoreProcCargarPAC(fileName, codUsuario, formPacCargasBean);
		}
		catch(IOException ioe){
			throw new BusinessException("[ERROR](cargarArchivoPAC) - IOException - Se ha producido un error durante el tratamiento del archivo PAC", ioe);
		}
		catch(Exception e){
			logger.debug("Error durante la carga del fichero de PAC", e);
			throw new BusinessException("[ERROR](cargarArchivoPAC) - Exception - Se ha producido un error durante el tratamiento del archivo PAC", e);
		}
		
	} 
	
	public String cargaParcelasPolizaDesdePAC (Long idPoliza, Long idClase, String listaIdPacAseg, String listaDVDefecto) {
		
		try {
			return cargaPACDao.cargaParcelasPolizaDesdePAC(idPoliza, idClase, listaIdPacAseg, listaDVDefecto);
		} catch (DAOException e) {
			logger.error("Ha ocurrido un error al cargar las parcelas de póliza a partir de las parcelas de PAC" , e);
			return "Error no controlado";
		}
		
	}


	/**
	 * @param formPacCargasBean
	 * @param fileName
	 * @throws IOException
	 * @throws BusinessException
	 */
	private void uploadFicheroPAC(FormPacCargasBean formPacCargasBean,	String fileName) throws IOException, BusinessException {		
		try {
			String serverLocation = bundle.getString("ruta.defecto.fichero.PAC2");
			File file = new File(serverLocation, fileName);
			try (BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(formPacCargasBean.getFile().getInputStream()));
					BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
				String linea;
				while ((linea = bufferedReader.readLine()) != null) {
					linea = linea.trim();
					bufferedWriter.write(linea + "\n");
				}
			}			
		}
		catch(Exception ex){
			logger.error("Error al subir el archivo de PAC al servidor", ex);
			throw new IOException("Error al subir el archivo de PAC al servidor", ex);
		}
	}
	

    @SuppressWarnings("unchecked")
	public List<PacParcelas> getParcelasPac(String nifcif, BigDecimal codlinea, BigDecimal codplan, Long claseId, BigDecimal codentidad) throws BusinessException {
		try {
			return cargaPACDao.getParcelasPac(nifcif, codlinea, codplan, claseId, codentidad);						
		}
		catch (DAOException ex){			
			logger.error("Se ha producido un error durante el acceso a la base de datos.", ex);
			throw new BusinessException ("[ERROR] en CargaPACManager.java metodo getParcelasPac.", ex);
		}
		catch (Exception ex){			
			logger.error("Se ha producido un error inesperado al cargar las parcelas de la PAC", ex);
			throw new BusinessException ("[ERROR] en CargaPACManager.java metodo getParcelasPac.", ex);
		}
	}
    
    public List<String> getListNombresFicheros(){
    	
    	List<String> listNombresFicheros = new ArrayList<String>();
    	String location = bundle.getString("ruta.defecto.fichero.PAC2");
    	//String location = "D:\\Documents and Settings\\u028827\\Escritorio\\aab";

    	logger.debug("[CargaPACManager]location: " + location);

    	File dir = new File(location);
		String[] ficheros = dir.list();
		
		if (ficheros != null){
			logger.debug("[CargaPACManager] Nï¿½ de ficheros: " + ficheros.length);
			for (int z = 0; z < ficheros.length; z++){
				File fich = new File(ficheros[z]);
				if (fich.getName().endsWith(".txt")||fich.getName().endsWith(".TXT")){
					listNombresFicheros.add(fich.getName());	
				}
			}
		}
    	
    	return listNombresFicheros; 
    }
    
	
	@SuppressWarnings("unchecked")
	public List<PacParcelas> getListParPacFiltro(Long lineaSegID, BigDecimal clasePol, List<PacParcelas>listParcelasPac) throws DAOException {
		
		List<PacParcelas> listPacAsegParcelasFiltro = new ArrayList<PacParcelas>(0);
		Set<ClaseDetalle> detalles = new HashSet<ClaseDetalle>(0);
		Integer indice = 0;
		ArrayList<String> listProv = new ArrayList<String>(0);
		ArrayList<String> listCom = new ArrayList<String>(0);
		ArrayList<String> listTer = new ArrayList<String>(0);
		ArrayList<Character> listSubTer = new ArrayList<Character>(0);
		Map<String, Object> filtro = new HashMap<String, Object>();
		
		try {
			// 1. Se recupera el Set de claseDetalle con la linea y la clase
			detalles = claseDao.getClaseDetalle(lineaSegID,clasePol);
			
			// 2. Se recorre el set de ClaseDetalle y formo listas: Provincia, comarca, termino, subtermino
			if (detalles != null && detalles.size()> 0)
			{ for (ClaseDetalle AuxclaseDetalles : detalles){
				listProv.add(AuxclaseDetalles.getCodprovincia().toString());
				listCom.add(AuxclaseDetalles.getCodcomarca().toString());
				listTer.add(AuxclaseDetalles.getCodtermino().toString());
				listSubTer.add(AuxclaseDetalles.getSubtermino());
				indice = indice +1;
				}
			
			// 3. Se comprueba si alguno de los valores es 99 (Todos)
			// En el caso de que no aparezca 999 se anade al filtro del criteria
				if (listProv.indexOf("99") == -1){ 	filtro.put("codprovincia", listProv);   } 
				if (listCom.indexOf("99") == -1) {	filtro.put("codcomarca",   listCom);	} 
				if (listTer.indexOf("999") == -1){	filtro.put("codtermino",   listTer);    }
				if (listSubTer.indexOf('9') == -1){	filtro.put("subtermino",   listSubTer); }
				
			// 4. Se pasa el filtro a la lista de Parcelas de Pac
				listPacAsegParcelasFiltro = cargaPACDao.filtrarPacProvComTerSubterm(filtro, listParcelasPac);
			}
			
			return listPacAsegParcelasFiltro;
			
		} catch (DAOException ex) {
			throw new DAOException ("Se ha producido un error durante el acceso a la base de datos", ex);
		}
			
	}
	
	/**
	 * Guarda la lista de los datos variables en la Poliza
	 * @param poliza
	 * @param lstDatosVar
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public void guardarDatosVariablesPAC(Poliza poliza,List<DatoVariableCargaParcela> lstDatosVar, CapitalAsegurado cap) throws BusinessException{
		logger.debug("init - guardarDatosVariables");
		
		// Obtenemos una lista con los datos variables que ya tenemos de la pac
		// para no insertarlos dos veces cuando asignemos los datos variables por defecto
		List<BigDecimal> codConceptoEnParcela = new ArrayList<BigDecimal>();
		for (DatoVariableParcela dvp : cap.getDatoVariableParcelas()){
			codConceptoEnParcela.add(dvp.getDiccionarioDatos().getCodconcepto());
		}
		
		try{
			for (DatoVariableCargaParcela dat: lstDatosVar){
				DatoVariableParcela dVarParcela= new DatoVariableParcela();
				BigDecimal CodConcepto= dat.getCodconcepto();
				
				//Si no lo tengo de la pac, anadimos el valor por defecto
				if (!codConceptoEnParcela.contains(CodConcepto)){
	
					if (CodConcepto.toString().equals("123")){
						if (("").equals(StringUtils.nullToString(dat.getValor()))){
							dVarParcela.setValor(null);
						}else{
							List<SistemaCultivo> lstSisCultivo=cargaPACDao.getlstSisCultClaseDetalle(poliza.getClase(), poliza.getLinea().getLineaseguroid(), "claseDetalle.sistemaCultivo");
							if (lstSisCultivo.size()>0){
								dVarParcela.setValor(lstSisCultivo.get(0).getCodsistemacultivo().toString());
							}else{
								dVarParcela.setValor(dat.getValor());
							}
						}
					}else{
						dVarParcela.setValor(StringUtils.nullToString(dat.getValor()));
					}
					dVarParcela.getDiccionarioDatos().setCodconcepto(CodConcepto);
					dVarParcela.setCapitalAsegurado(cap);
					
					cargaPACDao.saveDatoVarParcela(dVarParcela);
					cargaPACDao.evict(dVarParcela);
				}
			}
		}
		catch (DAOException ex){
			logger.error("Se ha producido un error durante el acceso a la base de datos " + ex.getMessage());
			throw new BusinessException ("[ERROR] en CargaPACManager.java metodo guardarDatosVariables.", ex);
		}
		catch (Exception ex){
			logger.error("Se ha producido un error inesperado al guardar los datos variables de la PAC",ex);
			throw new BusinessException ("[ERROR] en CargaPACManager.java metodo guardarDatosVariables.", ex);
		}
		
		logger.debug("Fin - guardarDatosVariables");
	}
	
	public String arrastrarParcelasPac(Poliza poliza, List<PacParcelas> parcelasPac, List<DatoVariableCargaParcela> lstDatosVar) throws BusinessException {
		
		String errorMsg = null;
		
		logger.debug("SeleccionPolizaManager.arrastrarParcelasPac - INI");
		try {
			// recuperamos los tipos de capital para asignarle a la parcela el menor de todos
			logger.debug("SeleccionPolizaManager.arrastrarParcelasPac - Recupera los tipos de capital para asignarle a la parcela el menor de todos");
			TipoCapital tipoCapital = getTipoCapital(poliza.getLinea().getLineaseguroid());
			
			logger.debug("SeleccionPolizaManager.arrastrarParcelasPac - Obtiene la lineaseguroid de la poliza");
			Long lineaSeguroId = poliza.getLinea().getLineaseguroid();
			logger.debug("SeleccionPolizaManager.arrastrarParcelasPac - Obtiene los modulos de la poliza ");
			List<ModuloPoliza>  modulos = cargaPACDao.getModulosPoliza(poliza.getIdpoliza(), poliza.getLinea().getLineaseguroid());
			logger.debug("SeleccionPolizaManager.arrastrarParcelasPac - Recorre la lista de parcelas de la PAC");
			Parcela parcela;
			CapitalAsegurado capAseg;
			boolean hasErrors = false;
			for (PacParcelas parcelaPac : parcelasPac) {
				parcela = null;
				capAseg = null;
				try {
					// convertimos de PacAsegParcelas a Parcela 
					logger.debug("SeleccionPolizaManager.arrastrarParcelasPac - Convertir de PacAsegParcelas a Parcela");
					parcela = convertirParcelaPac(parcelaPac, lineaSeguroId);
		
					// asociamos la poliza a la parcela
					parcela.setPoliza(poliza);
		
					// guardamos las parcelas
					seleccionPolizaDao.saveParcela(parcela);
					
					// formamos el Capital Asegurado
					logger.debug("SeleccionPolizaManager.arrastrarParcelasPac - Forma el Capital Asegurado");
					convertirCapAseg(parcela, parcelaPac,tipoCapital,modulos);
					// asociamos la parcela al Capital Asegurado
					capAseg = (CapitalAsegurado) parcela.getCapitalAsegurados().toArray()[0];
					capAseg.setParcela(parcela);

					// guardamos el Capital Asegurado
					logger.debug("SeleccionPolizaManager.arrastrarParcelasPac - Guarda el Capital Asegurado");
					seleccionPolizaDao.saveCapAseg(capAseg);
					
					// guardamos el los datos variables
					logger.debug("SeleccionPolizaManager.arrastrarParcelasPac - Guarda el Capital Asegurado");
					this.guardarDatosVariablesPAC(poliza,lstDatosVar, capAseg);
					poliza.getParcelas().add(parcela);
					seleccionPolizaDao.evict(parcela);
					seleccionPolizaDao.evict(capAseg);
				} catch (Exception e) {
					//IGT 21/04/2014 La carga continua aunque haya errores (rollback manual)
					if (capAseg != null) seleccionPolizaDao.delete(capAseg);
					if (parcela != null) seleccionPolizaDao.delete(parcela);			
					hasErrors = true;
				}
			}
			//IGT 21/04/2014 Se notifica error en la carga
			if (hasErrors) {
				logger.debug("No pudieron cargarse todas las parcelas correctamente. Cargadas "
						+ poliza.getParcelas().size()
						+ " de "
						+ parcelasPac.size() + " parcelas.");
				errorMsg = "No pudieron cargarse todas las parcelas correctamente. Cargadas "
						+ poliza.getParcelas().size()
						+ " de "
						+ parcelasPac.size() + " parcelas.";
			}
			//DAA 11/07/2013 actualizamos el estado de la poliza a cargada S
			polizaDao.actualizaPacCargadaPoliza(poliza.getIdpoliza(), Constants.PAC_CARGADA_SI); 
			logger.debug("SeleccionPolizaManager.arrastrarParcelasPac - FIN");
		} catch (Exception e) {
			logger.error("Se ha producido un error en arrastrarParcelasPac", e);
			//IGT 15/04/2014 actualizamos el estado de la poliza a cargada N/PDTE
			if (poliza.getParcelas().size() > 0) {
				polizaDao.actualizaPacCargadaPoliza(poliza.getIdpoliza(), Constants.PAC_CARGADA_NO);
			} else {
				polizaDao.actualizaPacCargadaPoliza(poliza.getIdpoliza(), Constants.PAC_CARGADA_PDTE);
			}
			throw new BusinessException("[ERROR](cargarArchivoPAC) Se ha producido un error en arrastrarParcelasPac", e);
		}
		return errorMsg;
	}
	
	/** DAA 11/09/2013
	 * @param lstDatosVar 
	 * @param idPoliza 
	 * 
	 */
	public void grabarDatosVariablesCopy(Poliza poliza, List<DatoVariableCargaParcela> lstDatosVar) throws BusinessException {
		logger.debug("CargaPACManager.grabarDatosVariablesCopy - INI");
		try {
			List<CapitalAsegurado> lstCapAsg = capitalAseguradoDao.getListCapitalesAsegurados(poliza.getIdpoliza());
			
			for (CapitalAsegurado capAseg : lstCapAsg) {
				this.guardarDatosVariablesPAC(poliza,lstDatosVar, capAseg);
			}
			logger.debug("CargaPACManager.grabarDatosVariablesCopy - FIN");
			
		}catch (Exception e) {
			logger.error("Se ha producido un error en grabarDatosVariablesCopy", e);
			throw new BusinessException("[ERROR](grabarDatosVariablesCopy) Se ha producido un error en arrastrarParcelasPac", e);
		}
			
	}

	
	/**
	 * Recuperamos el menor codigo de Tipo de Capital
	 * 
	 * @param lineaseguroid
	 * @return
	 * @throws BusinessException
	 */
	private TipoCapital getTipoCapital(Long lineaseguroid) throws BusinessException {
		TipoCapital codTipCapMenor = null;
		try {
			List<TipoCapital> tiposCapital = seleccionPolizaDao.getTiposCapitales(lineaseguroid);
			
			for (TipoCapital codTipCap : tiposCapital) {
				if (codTipCapMenor == null || 
						codTipCapMenor.getCodtipocapital().compareTo(codTipCap.getCodtipocapital()) > 0) {
					codTipCapMenor = new TipoCapital();
					codTipCapMenor.setCodtipocapital(codTipCap.getCodtipocapital());
					codTipCapMenor.setDestipocapital(codTipCap.getDestipocapital());
					codTipCapMenor.setCodconcepto(codTipCap.getCodconcepto());
					codTipCapMenor.setDiccionarioDatos(codTipCap.getDiccionarioDatos());
				}
			}
			
			return codTipCapMenor;
			
		} catch (DAOException e) {
			throw new BusinessException("Se ha producido un error al recuperar el Tipo de Capital", e);
		}
	}

	
	private Parcela convertirParcelaPac(PacParcelas parcelaPac, Long lineaSeguroId) {//modulo sirve para algo??
		logger.debug("Init -- convertirParcelaPac" );
		
		Parcela parcela = new Parcela();
		
		BigDecimal codLinea=null;
		BigDecimal codPlan=null;
		BigDecimal codCultST=null;
		BigDecimal codVarST=null;
		BigDecimal lineaSId=null;
		
		// MPM - 19/06/12
		// Se limpian los espacios en blanco de los codigos de provincia, comarca y termino de la parcela de la PAC para que
		// no de problemas al convertirlo a numero
		try {
			// Tipo Parcela
			parcela.setTipoparcela('P');
			
			// Provincia
			if (parcelaPac.getProvincia()!=null)
				parcela.getTermino().getId().setCodprovincia(new BigDecimal(parcelaPac.getProvincia()));
			// Comarca
			if (parcelaPac.getComarca()!=null)
				parcela.getTermino().getId().setCodcomarca(new BigDecimal(parcelaPac.getComarca()));
			// Termino
			if (parcelaPac.getTermino()!=null)
				parcela.getTermino().getId().setCodtermino(new BigDecimal(parcelaPac.getTermino()));
			// Subtermino
			if (parcelaPac.getSubtermino()!=null){
				if(parcelaPac.getSubtermino().equals(new Character('0'))){
					parcela.getTermino().getId().setSubtermino(new Character(' '));
				}else{
					parcela.getTermino().getId().setSubtermino(parcelaPac.getSubtermino());
				}
			}
			//Si se trata de seguro tradicional se transforma el cultivo y la variedad			
			if (parcelaPac.getPacAsegurados().getPacCargas().getLinea()!=null)
				codLinea = new BigDecimal (parcelaPac.getPacAsegurados().getPacCargas().getLinea());
			if (parcelaPac.getPacAsegurados().getPacCargas().getPlan()!=null)
				codPlan= new BigDecimal (parcelaPac.getPacAsegurados().getPacCargas().getPlan());
		}
		catch (Exception e) {
			logger.debug("Ocurrio un error al convertir parcelas", e);
		}
		
		logger.debug("CodLinea = "+ codLinea + " CodPlan = "+ codPlan);
		
		if (parcelaPac.getCultivo()!=null)
			codCultST = new BigDecimal (parcelaPac.getCultivo());
		if (parcelaPac.getVariedad()!=null)
			codVarST = new BigDecimal (parcelaPac.getVariedad());
		if (lineaSeguroId!=null)
			lineaSId = new BigDecimal (lineaSeguroId);
		
		logger.debug("codCultST = "+ codCultST + " codVarST = "+ codVarST + " lineaSId = "+ lineaSId);
		
		try {
			List<RelEspeciesSCEspeciesST> rel =  cargaPACDao.buscarST(lineaSId, codLinea, codPlan, codCultST, codVarST);
			boolean existeCulVar = false;
			if (rel != null && rel.size() > 0) { //seguro tradicional
				logger.debug("Parcela seguro tradicional" );
				// Cultivo
				parcela.setCodcultivo(rel.get(0).getId().getCodcultivo());
				// Variedad
				parcela.setCodvariedad(rel.get(0).getId().getCodvariedad());
				
			} else {  // seguro creciente

				existeCulVar = this.polizaDao.existeCultivoVariedad(lineaSeguroId, parcelaPac.getCultivo(), parcelaPac.getVariedad());
				
				if (existeCulVar){
					// Cultivo
					if (parcelaPac.getCultivo()!=null)
						parcela.setCodcultivo(new BigDecimal(parcelaPac.getCultivo()));
					// Variedad
					if (parcelaPac.getVariedad()!=null)
						parcela.setCodvariedad(new BigDecimal(parcelaPac.getVariedad()));
				}
				else{
					parcela.setCodcultivo(new BigDecimal(999));
					parcela.setCodvariedad(new BigDecimal(999));
				}
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage()); 
		}
		
		// Nombre Parcela
		if (parcelaPac.getNombre()!=null)
			parcela.setNomparcela(parcelaPac.getNombre());
		// Poligono
		if (parcelaPac.getPoligono() != null)
			parcela.setPoligono(parcelaPac.getPoligono().toString());
		// Parcela
		if (parcelaPac.getParcela() != null)
			parcela.setParcela(parcelaPac.getParcela().toString());
		//SIGPAC
		if (parcelaPac.getAgregadoSigpac()!=null)
			parcela.setAgrsigpac(new BigDecimal (parcelaPac.getAgregadoSigpac()));
		if (parcelaPac.getProvinciaSigpac()!=null)
			parcela.setCodprovsigpac(new BigDecimal (parcelaPac.getProvinciaSigpac()));
		if (parcelaPac.getTerminoSigpac()!=null)
			parcela.setCodtermsigpac(new BigDecimal (parcelaPac.getTerminoSigpac()));
		if (parcelaPac.getParcelaSigpac()!=null)
			parcela.setParcelasigpac(new BigDecimal (parcelaPac.getParcelaSigpac()));
		if (parcelaPac.getPoligonoSigpac()!=null)
			parcela.setPoligonosigpac(new BigDecimal (parcelaPac.getPoligonoSigpac()));
		if (parcelaPac.getRecintoSigpac()!=null)
			parcela.setRecintosigpac(new BigDecimal (parcelaPac.getRecintoSigpac()));
		if (parcelaPac.getZonaSigpac()!=null)
			parcela.setZonasigpac(new BigDecimal (parcelaPac.getZonaSigpac()));
		
		logger.debug("Fin -- convertirParcelaPac" );
		return parcela;
	}
	
	private void convertirCapAseg(Parcela parcela, PacParcelas parcelaPac,TipoCapital tipoCapital, List<ModuloPoliza> modulos) throws BusinessException  
	{
		logger.debug("SeleccionPolizaManager.convertirCapAseg - INI");
		
		// Capital Asegurado
		CapitalAsegurado capAseg = new CapitalAsegurado();
		capAseg.setTipoCapital(tipoCapital);
		capAseg.setParcela(parcela); 
		/*if (parcelaPac.getNumhectareas()!=null){
			if (parcelaPac.getNumhectareas().indexOf(",") >= 0){
				parcelaPac.setNumhectareas(parcelaPac.getNumhectareas().replaceAll(",", "."));
			}
		}
		
		if (parcelaPac.getNumhectareas() != null
				&& !"".equals(parcelaPac.getNumhectareas())
				&& new BigDecimal(parcelaPac.getNumhectareas()).compareTo(new BigDecimal(0)) > 0){
			capAseg.setSuperficie(new BigDecimal(parcelaPac.getNumhectareas()));
		}
		else{
			capAseg.setSuperficie(new BigDecimal(0));
		}*/
		
		BigDecimal divisor = new BigDecimal(100);
		divisor = capAseg.getSuperficie().divide(divisor);
		capAseg.setSuperficie(divisor);
		
		// TMR 30/07/2013 Mejora 291. Carga PAC datos variables
		
		try{
			Map<BigDecimal, BigDecimal> datosVarValidos = cargaPACDao.getDatosVarPantalla(parcela.getPoliza().getLinea().getLineaseguroid());
			Class<?> clase = PacParcelas.class;
			
			boolean numArboles = false;
			Set<DatoVariableParcela> datosVariables = new HashSet<DatoVariableParcela>();
			Iterator<Entry<String, Integer>> it = ConstantsConceptos.CODCPTO_DATOS_VARIABLES_PAC.entrySet().iterator();
	    	while(it.hasNext()) {
	    		Map.Entry<String,Integer> key = (Map.Entry<String,Integer>)it.next();
	    		
	    		if (datosVarValidos.containsKey(BigDecimal.valueOf(key.getValue()))) {
	    		
		    		String campo = key.getKey().substring(0, 1).toUpperCase() + key.getKey().substring(1,key.getKey().length());
		    		String metodo = "get" + campo;
		    		
		    		logger.debug("metodo: " + metodo);
		    		Method method = clase.getMethod(metodo);
		    		Object valor = method.invoke(parcelaPac);
		    		
		    		DatoVariableParcela dvp = new DatoVariableParcela();
		    		DiccionarioDatos dd = new DiccionarioDatos();
		    		
		    		boolean creaDV = false;
					
		    		if (valor != null && ConstantsConceptos.TIPO_DATOS_VARIABLES_PAC.get(key.getKey()).equals(BigDecimal.class)){
		    			BigDecimal numero;
		    			try {
							numero = new BigDecimal(valor.toString());
						} catch (Exception e) {
							numero = new BigDecimal(0);
						}
						if (numero.compareTo(new BigDecimal(0)) > 0){
							if (!numArboles || !key.getKey().equals(ConstantsConceptos.NUMCEPAS_FIELD)){
			    				dvp.setValor(numero.toString());
			    				creaDV = true;
			    				if(key.getKey().equals(ConstantsConceptos.NUMARBOLES_FIELD)){
									numArboles = true;
								}
							}
		    			}
						
		    		}
		    		else if (valor != null && (ConstantsConceptos.TIPO_DATOS_VARIABLES_PAC.get(key.getKey()).equals(Character.class))||
		    			      			(ConstantsConceptos.TIPO_DATOS_VARIABLES_PAC.get(key.getKey()).equals(String.class))){
		    			dvp.setValor(valor.toString());
		    			creaDV  = true;
					}
		    		
		    		if (creaDV){
		    			dvp.setCapitalAsegurado(capAseg);
		    			dd.setCodconcepto(BigDecimal.valueOf(key.getValue()));
		    			dvp.setDiccionarioDatos(dd);
		    			datosVariables.add(dvp);
		    		}
	    		}
	    	}
	    	capAseg.getDatoVariableParcelas().addAll(datosVariables);
			// capital Asegurado RelModulos
			Set<CapAsegRelModulo> capAsegRelModulos = new HashSet<CapAsegRelModulo>(0);
		    capAseg.setCapAsegRelModulos(capAsegRelModulos);
			
			Set<CapitalAsegurado> capitalAsegurados = new HashSet<CapitalAsegurado>(0);
			capitalAsegurados.add(capAseg);
			parcela.setCapitalAsegurados(capitalAsegurados);
			
		}catch (Exception e) {
			logger.error("Se ha producido un error en convertirCapAseg", e);
			throw new BusinessException("[ERROR](cargarArchivoPAC) Se ha producido un error en convertirCapAseg", e);
		}
		logger.debug("SeleccionPolizaManager.convertirCapAseg - FIN");
	}
	
	public Reader getContenidoArchivoCargaPAC(Long idCargaPAC) throws BusinessException {
		try{
		
			return cargaPACDao.getContenidoArchivoCargaPAC(idCargaPAC);
		
		}catch (DAOException daoe){
			throw new BusinessException("Se ha producido un error durante la lectura del archivo PAC", daoe);
		}	
	}
	
	public List<Long> existeParcelasPACAsegurado(BigDecimal codlinea, BigDecimal codplan, String cifnifAsegurado, BigDecimal codentidad,
											  BigDecimal codentidadMed, BigDecimal codsubentidadMed) throws BusinessException{
		try{
		
			logger.debug("Comprueba si hay PAC para los valores: " + codplan + "/" + codlinea + ", " + cifnifAsegurado +
						 ", " + codentidad + ", " + codentidadMed + "-" + codsubentidadMed);
			
			return cargaPACDao.existeParcelasPACAsegurado(codlinea, codplan, cifnifAsegurado, codentidad, codentidadMed, codsubentidadMed);
		
		}catch (DAOException daoe){
			throw new BusinessException ("Se ha producido un error durante el acceso a la base de datos", daoe);
		}
	}
	
	public PacCargas getById(Long idCargaPAC) throws BusinessException{
		try{
			
			return (PacCargas)cargaPACDao.get(PacCargas.class, idCargaPAC);
		
		}catch (DAOException daoe){
			throw new BusinessException ("Se ha producido un error durante el acceso a la base de datos", daoe);
		}
	}
	
	public List<PacCargas> listarCargas(PacCargas pacCarga) throws BusinessException{
		try{
			
			return cargaPACDao.listarCargas(pacCarga);
		
		}catch (DAOException daoe){
			throw new BusinessException ("Se ha producido un error durante el acceso a la base de datos", daoe);
		}
	}
	/** DAA 10/09/2013
	 *  Devuelve una lista de codConceptos de Datos variables de las parcelas de la poliza
	 * @param idpoliza
	 * @return
	 */
	public List<BigDecimal> getDatosVariableParcelas(Long idPoliza) throws BusinessException {
		try{
			return datoVariableDao.getDatosVariableParcelas(idPoliza);
			
		}catch (DAOException daoe){
			throw new BusinessException ("Se ha producido un error durante el acceso a la base de datos", daoe);
		}
	}
	
	/**
	 * Comprueba si la pac se ha cargado anteriormente para esta pï¿½liza
	 * @param idPoliza
	 * @return
	 */
	public Character isPacCargada (Long idPoliza) {
		
		try {
			return polizaDao.isPacCargada(idPoliza);
		} catch (Exception e) {
			logger.debug("Ocurriï¿½ un error inesperado al comprobar si la pac estï¿½ cargada para la pï¿½liza " + idPoliza, e);
		}
		
		return null;
	}
	
	public boolean existeESMedEntUsuario (BigDecimal entMed, BigDecimal subentMed, Usuario usuario) {
		
		try {
			List<BigDecimal> listaEntidades = new ArrayList<BigDecimal>();
			if (!usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SEMIADMINISTRADOR)) {
				listaEntidades.add(usuario.getOficina().getEntidad().getCodentidad());
			}
			else {
				listaEntidades.addAll(usuario.getListaCodEntidadesGrupo());
			}
			
			
			return this.cargaPACDao.existeESMedEntUsuario(entMed, subentMed, listaEntidades);
		} catch (Exception e) {
			logger.debug("Ha ocurrido un error inesperado al comprobar si la E-S Mediadora está asociada a la lista de entidades", e);
		}
		
		return false;
	}
	
	public boolean existeArchivoCargado(String nombreArchivo) throws BusinessException{
		
		return this.cargaPACDao.existeArchivoCargado(nombreArchivo);
	}
	
	public void eliminarCargaPac(List<BigDecimal> listaIdsCargaPAC) throws DAOException{
		
		for (BigDecimal idCargaPAC : listaIdsCargaPAC) {
			this.cargaPACDao.dropCargaPAC(idCargaPAC);
		}
		
		
	}
	
	public void setCargaPACDao(ICargaPACDao cargaPACDao) {
		this.cargaPACDao = cargaPACDao;
	}

	public void setClaseDao(IClaseDao claseDao) {
		this.claseDao = claseDao;
	}

	public void setSeleccionPolizaDao(ISeleccionPolizaDao seleccionPolizaDao) {
		this.seleccionPolizaDao = seleccionPolizaDao;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}

	public void setDatoVariableDao(IDatoVariableDao datoVariableDao) {
		this.datoVariableDao = datoVariableDao;
	}

	public void setCapitalAseguradoDao(ICapitalAseguradoDao capitalAseguradoDao) {
		this.capitalAseguradoDao = capitalAseguradoDao;
	}


	
}
