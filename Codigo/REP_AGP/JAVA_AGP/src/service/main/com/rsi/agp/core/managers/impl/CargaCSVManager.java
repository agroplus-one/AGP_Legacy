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
import com.rsi.agp.dao.models.config.ICargaCSVDao;
import com.rsi.agp.dao.models.config.IDatoVariableDao;
import com.rsi.agp.dao.models.poliza.ICapitalAseguradoDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.models.poliza.ISeleccionPolizaDao;
import com.rsi.agp.dao.tables.admin.ClaseDetalle;
import com.rsi.agp.dao.tables.cgen.SistemaCultivo;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.config.DatoVariableCargaParcela;
import com.rsi.agp.dao.tables.cpl.RelEspeciesSCEspeciesST;
import com.rsi.agp.dao.tables.cvs.CvsCarga;
import com.rsi.agp.dao.tables.cvs.CvsParcela;
import com.rsi.agp.dao.tables.cvs.FormCsvCargasBean;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;
import com.rsi.agp.dao.tables.poliza.CapAsegRelModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class CargaCSVManager implements IManager {

	private static final Log             logger = LogFactory.getLog(CargaCSVManager.class);
	private static final ResourceBundle  bundle = ResourceBundle.getBundle("agp");
	
	private ICargaCSVDao          cargaCSVDao;
	private IClaseDao             claseDao;
	private ISeleccionPolizaDao   seleccionPolizaDao;
	private IPolizaDao            polizaDao;
	private IDatoVariableDao	  datoVariableDao;
	private ICapitalAseguradoDao  capitalAseguradoDao;
	
	public String cargarArchivoCSV(FormCsvCargasBean formCsvCargasBean, String codUsuario) throws BusinessException{

		try	{			        
			// Obtiene el fichero a procesar
			String fileName = formCsvCargasBean.getFile().getOriginalFilename();
		
			// Sube el fichero 
			uploadFicheroCSV(formCsvCargasBean, fileName);
			
			// Ejecuta el procedimiento almacenado encargado de cargar los datos del fichero en el modelo de datos de PAC
			return cargaCSVDao.executeStoreProcCargarCSV(fileName, codUsuario, formCsvCargasBean);
		}
		catch(IOException ioe){
			logger.debug("Error durante la carga del fichero CSV: Fallo en la estructura del fichero", ioe);
			throw new BusinessException(ioe.getMessage(), ioe);
		}
		catch(Exception e){
			logger.debug("Error durante la carga del fichero CSV", e);
			throw new BusinessException("[ERROR](cargarArchivoCSV) - Exception - Se ha producido un error durante el tratamiento del archivo CSV", e);
		}
		
	} 
	
	public String cargaParcelasPolizaDesdeCSV (Long idPoliza, Long idClase, String listaIdCsvAseg, String listaDVDefecto) {
		
		try {
			return cargaCSVDao.cargaParcelasPolizaDesdeCSV(idPoliza, idClase, listaIdCsvAseg, listaDVDefecto);
		} catch (DAOException e) {
			logger.error("Ha ocurrido un error al cargar las parcelas de póliza a partir de las parcelas de CSV" , e);
			return "Error no controlado";
		}
		
	}


	/**
	 * @param formCsvCargasBean
	 * @param fileName
	 * @throws IOException
	 * @throws BusinessException
	 */
	private void uploadFicheroCSV(FormCsvCargasBean formCsvCargasBean, String fileName)
			throws IOException, BusinessException {
		try {
			String serverLocation = bundle.getString("ruta.defecto.fichero.PAC2");
			File file = new File(serverLocation, fileName);
			try (BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(formCsvCargasBean.getFile().getInputStream()));
					BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
				String linea;
				while ((linea = bufferedReader.readLine()) != null) {
					linea = linea.trim();
					bufferedWriter.write(linea + "\n");
				}
			}
		} catch (Exception ex) {
			logger.error("Error al subir el archivo CSV al servidor", ex);
			throw new IOException("Error al subir el archivo CSV al servidor", ex);
		}
	}
	

    @SuppressWarnings("unchecked")
	public List<CvsParcela> getParcelasCsv(String nifcif, BigDecimal codlinea, BigDecimal codplan, BigDecimal codentidad) throws BusinessException {
		try {
			return cargaCSVDao.getParcelasCsv(nifcif, codlinea, codplan, codentidad);						
		}
		catch (DAOException ex){			
			logger.error("[getParcelasCsv] Se ha producido un error durante el acceso a la base de datos.", ex);
			throw new BusinessException ("[ERROR] en CargaCSVManager.java metodo getParcelasCsv.", ex);
		}
		catch (Exception ex){			
			logger.error("[getParcelasCsv] Se ha producido un error inesperado al cargar las parcelas de CSV", ex);
			throw new BusinessException ("[ERROR] en CargaCSVManager.java metodo getParcelasCsv.", ex);
		}
	}
    
    public List<String> getListNombresFicheros(){
    	
    	List<String> listNombresFicheros = new ArrayList<String>();
    	String location = bundle.getString("ruta.defecto.fichero.PAC2");

    	logger.debug("[CargaCsvManager]location: " + location);

    	File dir = new File(location);
		String[] ficheros = dir.list();
		
		if (ficheros != null){
			logger.debug("[CargaCSVManager] Nï¿½ de ficheros: " + ficheros.length);
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
	public List<CvsParcela> getListParCsvFiltro(Long lineaSegID, BigDecimal clasePol, List<CvsParcela>listParcelasCsv) throws DAOException {
		
		List<CvsParcela> listCsvAsegParcelasFiltro = new ArrayList<CvsParcela>(0);
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
				
			// 4. Se pasa el filtro a la lista de Parcelas de Csv
				listCsvAsegParcelasFiltro = cargaCSVDao.filtrarCsvProvComTerSubterm(filtro, listParcelasCsv);
			}
			
			return listCsvAsegParcelasFiltro;
			
		} catch (DAOException ex) {
			throw new DAOException ("[getListParCsvFiltro] Se ha producido un error durante el acceso a la base de datos", ex);
		}
			
	}
	
	/**
	 * Guarda la lista de los datos variables en la Poliza
	 * @param poliza
	 * @param lstDatosVar
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public void guardarDatosVariablesCSV(Poliza poliza,List<DatoVariableCargaParcela> lstDatosVar, CapitalAsegurado cap) throws BusinessException{
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
				BigDecimal codConcepto = dat.getCodconcepto();
				
				//Si no lo tengo del csv, anadimos el valor por defecto
				if (!codConceptoEnParcela.contains(codConcepto)){
	
					if ("123".equals(codConcepto.toString())){
						if (("").equals(StringUtils.nullToString(dat.getValor()))){
							dVarParcela.setValor(null);
						}else{
							List<SistemaCultivo> lstSisCultivo=cargaCSVDao.getlstSisCultClaseDetalle(poliza.getClase(), poliza.getLinea().getLineaseguroid(), "claseDetalle.sistemaCultivo");
							if (lstSisCultivo.size()>0){
								dVarParcela.setValor(lstSisCultivo.get(0).getCodsistemacultivo().toString());
							}else{
								dVarParcela.setValor(dat.getValor());
							}
						}
					}else{
						dVarParcela.setValor(StringUtils.nullToString(dat.getValor()));
					}
					dVarParcela.getDiccionarioDatos().setCodconcepto(codConcepto);
					dVarParcela.setCapitalAsegurado(cap);
					
					cargaCSVDao.saveDatoVarParcela(dVarParcela);
					cargaCSVDao.evict(dVarParcela);
				}
			}
		}
		catch (DAOException ex){
			logger.error("[guardarDatosVariablesCSV] Se ha producido un error durante el acceso a la base de datos " + ex.getMessage());
			throw new BusinessException ("[ERROR] en CargaCSVManager.java metodo guardarDatosVariables.", ex);
		}
		catch (Exception ex){
			logger.error("[guardarDatosVariablesCSV] Se ha producido un error inesperado al guardar los datos variables del CSV",ex);
			throw new BusinessException ("[ERROR] en CargaCSVManager.java metodo guardarDatosVariables.", ex);
		}
		
		logger.debug("Fin - guardarDatosVariables");
	}
	
	public String arrastrarParcelasCsv(Poliza poliza, List<CvsParcela> parcelasCsv, List<DatoVariableCargaParcela> lstDatosVar) throws BusinessException {
		
		String errorMsg = null;
		
		logger.debug("SeleccionPolizaManager.arrastrarParcelasCsv - INI");
		try {
			// recuperamos los tipos de capital para asignarle a la parcela el menor de todos
			logger.debug("SeleccionPolizaManager.arrastrarParcelasCsvd - Recupera los tipos de capital para asignarle a la parcela el menor de todos");
			TipoCapital tipoCapital = getTipoCapital(poliza.getLinea().getLineaseguroid());
			
			logger.debug("SeleccionPolizaManager.arrastrarParcelasCsv - Obtiene la lineaseguroid de la poliza");
			Long lineaSeguroId = poliza.getLinea().getLineaseguroid();
			logger.debug("SeleccionPolizaManager.arrastrarParcelasCsv - Recorre la lista de parcelas de CSV");
			Parcela parcela;
			CapitalAsegurado capAseg;
			boolean hasErrors = false;
			for (CvsParcela parcelaCsv : parcelasCsv) {
				parcela = null;
				capAseg = null;
				try {
					// convertimos de CsvAsegParcelas a Parcela 
					logger.debug("SeleccionPolizaManager.arrastrarParcelasScv - Convertir de CsvAsegParcelas a Parcela");
					parcela = convertirParcelaCsv(parcelaCsv, lineaSeguroId);
		
					// asociamos la poliza a la parcela
					parcela.setPoliza(poliza);
		
					// guardamos las parcelas
					seleccionPolizaDao.saveParcela(parcela);
					
					// formamos el Capital Asegurado
					logger.debug("SeleccionPolizaManager.arrastrarParcelasCsv - Forma el Capital Asegurado");
					convertirCapAseg(parcela, parcelaCsv, tipoCapital);
					// asociamos la parcela al Capital Asegurado
					capAseg = (CapitalAsegurado) parcela.getCapitalAsegurados().toArray()[0];
					capAseg.setParcela(parcela);

					// guardamos el Capital Asegurado
					logger.debug("SeleccionPolizaManager.arrastrarParcelasCsv - Guarda el Capital Asegurado");
					seleccionPolizaDao.saveCapAseg(capAseg);
					
					// guardamos el los datos variables
					logger.debug("SeleccionPolizaManager.arrastrarParcelasCsv - Guarda el Capital Asegurado");
					this.guardarDatosVariablesCSV(poliza,lstDatosVar, capAseg);
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
						+ parcelasCsv.size() + " parcelas.");
				errorMsg = "No pudieron cargarse todas las parcelas correctamente. Cargadas "
						+ poliza.getParcelas().size()
						+ " de "
						+ parcelasCsv.size() + " parcelas.";
			}
			//DAA 11/07/2013 actualizamos el estado de la poliza a cargada S
			polizaDao.actualizaCsvCargadoPoliza(poliza.getIdpoliza(), Constants.PAC_CARGADA_SI);
			logger.debug("SeleccionPolizaManager.arrastrarParcelasCsv - FIN");
		} catch (Exception e) {
			logger.error("Se ha producido un error en arrastrarParcelasCsv", e);
			//IGT 15/04/2014 actualizamos el estado de la poliza a cargada N/PDTE
			if (poliza.getParcelas().size() > 0) {
				polizaDao.actualizaCsvCargadoPoliza(poliza.getIdpoliza(), Constants.PAC_CARGADA_NO);
			} else {
				polizaDao.actualizaCsvCargadoPoliza(poliza.getIdpoliza(), Constants.PAC_CARGADA_PDTE);
			}
			throw new BusinessException("[ERROR](cargarArchivoCSV) Se ha producido un error en arrastrarParcelasCsv", e);
		}
		return errorMsg;
	}
	
	/** DAA 11/09/2013
	 * @param lstDatosVar 
	 * @param idPoliza 
	 * 
	 */
	public void grabarDatosVariablesCopy(Poliza poliza, List<DatoVariableCargaParcela> lstDatosVar) throws BusinessException {
		logger.debug("CargaCSVManager.grabarDatosVariablesCopy - INI");
		try {
			List<CapitalAsegurado> lstCapAsg = capitalAseguradoDao.getListCapitalesAsegurados(poliza.getIdpoliza());
			
			for (CapitalAsegurado capAseg : lstCapAsg) {
				this.guardarDatosVariablesCSV(poliza,lstDatosVar, capAseg);
			}
			logger.debug("CargaCSVManager.grabarDatosVariablesCopy - FIN");
			
		}catch (Exception e) {
			logger.error("Se ha producido un error en grabarDatosVariablesCopy", e);
			throw new BusinessException("[ERROR](grabarDatosVariablesCopy) Se ha producido un error en arrastrarParcelasCsv", e);
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

	
	private Parcela convertirParcelaCsv(CvsParcela parcelaCsv, Long lineaSeguroId) {//modulo sirve para algo??
		logger.debug("Init -- convertirParcelaCsv" );
		
		Parcela parcela = new Parcela();
		
		BigDecimal codLinea=null;
		BigDecimal codPlan=null;
		BigDecimal codCultST=null;
		BigDecimal codVarST=null;
		BigDecimal lineaSId=null;
		
		// MPM - 19/06/12
		// Se limpian los espacios en blanco de los codigos de provincia, comarca y termino de la parcela del CSV para que
		// no de problemas al convertirlo a numero
		try {
			// Tipo Parcela
			parcela.setTipoparcela('P');
			
			// Provincia
			if (parcelaCsv.getProvincia()!=null)
				parcela.getTermino().getId().setCodprovincia(new BigDecimal(parcelaCsv.getProvincia()));
			// Comarca
			if (parcelaCsv.getComarca()!=null)
				parcela.getTermino().getId().setCodcomarca(new BigDecimal(parcelaCsv.getComarca()));
			// Termino
			if (parcelaCsv.getTermino()!=null)
				parcela.getTermino().getId().setCodtermino(new BigDecimal(parcelaCsv.getTermino()));
			// Subtermino
			if (parcelaCsv.getSubtermino()!=null){
				if(parcelaCsv.getSubtermino().equals('0')){
					parcela.getTermino().getId().setSubtermino(' ');
				}else{
					parcela.getTermino().getId().setSubtermino(parcelaCsv.getSubtermino());
				}
			}
			//Si se trata de seguro tradicional se transforma el cultivo y la variedad			
			if (parcelaCsv.getCvsAsegurado().getCvsCarga().getLinea()!=null)
				codLinea = new BigDecimal (parcelaCsv.getCvsAsegurado().getCvsCarga().getLinea());
			if (parcelaCsv.getCvsAsegurado().getCvsCarga().getPlan()!=null)
				codPlan= new BigDecimal (parcelaCsv.getCvsAsegurado().getCvsCarga().getPlan());
		}
		catch (Exception e) {
			logger.debug("Ocurrio un error al convertir parcelas", e);
		}
		
		logger.debug("CodLinea = "+ codLinea + " CodPlan = "+ codPlan);
		
		if (parcelaCsv.getCultivo()!=null)
			codCultST = new BigDecimal (parcelaCsv.getCultivo());
		if (parcelaCsv.getVariedad()!=null)
			codVarST = new BigDecimal (parcelaCsv.getVariedad());
		if (lineaSeguroId!=null)
			lineaSId = new BigDecimal (lineaSeguroId);
		
		logger.debug("codCultST = "+ codCultST + " codVarST = "+ codVarST + " lineaSId = "+ lineaSId);
		
		try {
			List<RelEspeciesSCEspeciesST> rel =  cargaCSVDao.buscarST(lineaSId, codLinea, codPlan, codCultST, codVarST);
			boolean existeCulVar = false;
			if (rel != null && rel.size() > 0) { //seguro tradicional
				logger.debug("Parcela seguro tradicional" );
				// Cultivo
				parcela.setCodcultivo(rel.get(0).getId().getCodcultivo());
				// Variedad
				parcela.setCodvariedad(rel.get(0).getId().getCodvariedad());
				
			} else {  // seguro creciente

				existeCulVar = this.polizaDao.existeCultivoVariedad(lineaSeguroId, parcelaCsv.getCultivo(), parcelaCsv.getVariedad());
				
				if (existeCulVar){
					// Cultivo
					if (parcelaCsv.getCultivo()!=null)
						parcela.setCodcultivo(new BigDecimal(parcelaCsv.getCultivo()));
					// Variedad
					if (parcelaCsv.getVariedad()!=null)
						parcela.setCodvariedad(new BigDecimal(parcelaCsv.getVariedad()));
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
		if (parcelaCsv.getNombre()!=null)
			parcela.setNomparcela(parcelaCsv.getNombre());

		//SIGPAC
		if (parcelaCsv.getAgregadoSigpac()!=null)
			parcela.setAgrsigpac(new BigDecimal (parcelaCsv.getAgregadoSigpac()));
		if (parcelaCsv.getProvinciaSigpac()!=null)
			parcela.setCodprovsigpac(new BigDecimal (parcelaCsv.getProvinciaSigpac()));
		if (parcelaCsv.getTerminoSigpac()!=null)
			parcela.setCodtermsigpac(new BigDecimal (parcelaCsv.getTerminoSigpac()));
		if (parcelaCsv.getParcelaSigpac()!=null)
			parcela.setParcelasigpac(new BigDecimal (parcelaCsv.getParcelaSigpac()));
		if (parcelaCsv.getPoligonoSigpac()!=null)
			parcela.setPoligonosigpac(new BigDecimal (parcelaCsv.getPoligonoSigpac()));
		if (parcelaCsv.getRecintoSigpac()!=null)
			parcela.setRecintosigpac(new BigDecimal (parcelaCsv.getRecintoSigpac()));
		if (parcelaCsv.getZonaSigpac()!=null)
			parcela.setZonasigpac(new BigDecimal (parcelaCsv.getZonaSigpac()));
		
		//PARCELA AGRICOLA
		if (parcelaCsv.getParcAgricola()!=null)
			parcela.setParcAgricola(parcelaCsv.getParcAgricola());
		
		logger.debug("Fin -- convertirParcelaPac" );
		return parcela;
	}
	
	private void convertirCapAseg(Parcela parcela, CvsParcela parcelaCsv, TipoCapital tipoCapital) throws BusinessException {
		logger.debug("SeleccionPolizaManager.convertirCapAseg - INI");
		
		// Capital Asegurado
		CapitalAsegurado capAseg = new CapitalAsegurado();
		capAseg.setTipoCapital(tipoCapital);
		capAseg.setParcela(parcela); 
		
		BigDecimal divisor = new BigDecimal(100);
		divisor = capAseg.getSuperficie().divide(divisor);
		capAseg.setSuperficie(divisor);
		
		// TMR 30/07/2013 Mejora 291. Carga CSV datos variables
		
		try{
			Map<BigDecimal, BigDecimal> datosVarValidos = cargaCSVDao.getDatosVarPantalla(parcela.getPoliza().getLinea().getLineaseguroid());
			Class<?> clase = CvsParcela.class;
			
			boolean numArboles = false;
			Set<DatoVariableParcela> datosVariables = new HashSet<DatoVariableParcela>();
			Iterator<Entry<String, Integer>> it = ConstantsConceptos.CODCPTO_DATOS_VARIABLES_PAC.entrySet().iterator();
	    	while(it.hasNext()) {
	    		Map.Entry<String, Integer> key = it.next();
	    		
	    		if (datosVarValidos.containsKey(BigDecimal.valueOf(key.getValue()))) {
	    		
		    		String campo = key.getKey().substring(0, 1).toUpperCase() + key.getKey().substring(1,key.getKey().length());
		    		String metodo = "get" + campo;
		    		
		    		logger.debug("metodo: " + metodo);
		    		Method method = clase.getMethod(metodo);
		    		Object valor = method.invoke(parcelaCsv);
		    		
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
						if (numero.compareTo(new BigDecimal(0)) > 0
								&& (!numArboles || !key.getKey().equals(ConstantsConceptos.NUMCEPAS_FIELD))) {
		    				dvp.setValor(numero.toString());
		    				creaDV = true;
		    				if(key.getKey().equals(ConstantsConceptos.NUMARBOLES_FIELD)){
								numArboles = true;
							}
		    			}
						
					} else if (valor != null && (ConstantsConceptos.TIPO_DATOS_VARIABLES_PAC.get(key.getKey())
							.equals(Character.class)
							|| ConstantsConceptos.TIPO_DATOS_VARIABLES_PAC.get(key.getKey()).equals(String.class))) {
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
			throw new BusinessException("[ERROR](cargarArchivoCSV) Se ha producido un error en convertirCapAseg", e);
		}
		logger.debug("SeleccionPolizaManager.convertirCapAseg - FIN");
	}
	
	public Reader getContenidoArchivoCargaCSV(Long idCargaCSV) throws BusinessException {
		try{
		
			return cargaCSVDao.getContenidoArchivoCargaCSV(idCargaCSV);
		
		}catch (DAOException daoe){
			throw new BusinessException("Se ha producido un error durante la lectura del archivo CSV", daoe);
		}	
	}
	
	public List<Long> existeParcelasCSVAsegurado(BigDecimal codlinea, BigDecimal codplan, String cifnifAsegurado, BigDecimal codentidad,
											  BigDecimal codentidadMed, BigDecimal codsubentidadMed) throws BusinessException{
		try{
		
			logger.debug("Comprueba si hay CSV para los valores: " + codplan + "/" + codlinea + ", " + cifnifAsegurado +
						 ", " + codentidad + ", " + codentidadMed + "-" + codsubentidadMed);
			
			return cargaCSVDao.existeParcelasCSVAsegurado(codlinea, codplan, cifnifAsegurado, codentidad, codentidadMed, codsubentidadMed);
		
		}catch (DAOException daoe){
			throw new BusinessException ("[existeParcelasCSVAsegurado] Se ha producido un error durante el acceso a la base de datos", daoe);
		}
	}
	
	public CvsCarga getById(Long idCargaCSV) throws BusinessException{
		try{
			
			return (CvsCarga)cargaCSVDao.get(CvsCarga.class, idCargaCSV);
		
		}catch (DAOException daoe){
			throw new BusinessException ("[getById] Se ha producido un error durante el acceso a la base de datos", daoe);
		}
	}
	
	public List<CvsCarga> listarCargas(CvsCarga csvCarga) throws BusinessException{
		try{
			
			return cargaCSVDao.listarCargas(csvCarga);
		
		}catch (DAOException daoe){
			throw new BusinessException ("[listarCargas] Se ha producido un error durante el acceso a la base de datos", daoe);
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
			throw new BusinessException ("[getDatosVariableParcelas] Se ha producido un error durante el acceso a la base de datos", daoe);
		}
	}
	
	/**
	 * Comprueba si el csv se ha cargado anteriormente para esta poliza
	 * @param idPoliza
	 * @return
	 */
	public Character isCsvCargado (Long idPoliza) {
		
		try {
			return polizaDao.isCsvCargado(idPoliza);
		} catch (Exception e) {
			logger.debug("Ocurriï¿½ un error inesperado al comprobar si el csv estï¿½ cargado para la pï¿½liza " + idPoliza, e);
		}
		
		return null;
	}
	
	public boolean existeESMedEnt (BigDecimal entMed, BigDecimal subentMed) {
		
		try {
			
			return this.cargaCSVDao.existeESMedEnt(entMed, subentMed);
		} catch (Exception e) {
			logger.debug("Ha ocurrido un error inesperado al comprobar si la E-S Mediadora está asociada a la lista de entidades", e);
		}
		
		return false;
	}
	
	public boolean existeArchivoCargado(String nombreArchivo) throws BusinessException{
		
		return this.cargaCSVDao.existeArchivoCargado(nombreArchivo);
	}
	
	public void eliminarCargaCsv(List<BigDecimal> listaIdsCargaCSV) throws DAOException{
		
		for (BigDecimal idCargaCSV : listaIdsCargaCSV) {
			this.cargaCSVDao.dropCargaCSV(idCargaCSV);
		}
		
		
	}
	
	public boolean existePlanLinea(BigDecimal plan, BigDecimal linea) {
		
		return this.cargaCSVDao.existePlanLinea(plan, linea);
	}
	
	public void setCargaCSVDao(ICargaCSVDao cargaCSVDao) {
		this.cargaCSVDao = cargaCSVDao;
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