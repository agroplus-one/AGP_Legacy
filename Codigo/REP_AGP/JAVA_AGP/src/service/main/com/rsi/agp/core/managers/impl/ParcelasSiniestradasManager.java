package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.managers.impl.anexoMod.PolizaActualizadaResponse;
import com.rsi.agp.core.managers.impl.anexoMod.SWAnexoModificacionHelper;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.impl.DatabaseManager;
import com.rsi.agp.dao.models.poliza.IParcelasSiniestradasDao;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.siniestro.CapAsegSiniestradoDV;
import com.rsi.agp.dao.tables.siniestro.CapAsegSiniestro;
import com.rsi.agp.dao.tables.siniestro.DatVarCapAsegSiniestro;
import com.rsi.agp.dao.tables.siniestro.ParcelaSiniestro;
import com.rsi.agp.dao.tables.siniestro.Siniestro;

import es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException;

public class ParcelasSiniestradasManager implements IManager {

	private static final Log logger = LogFactory.getLog(ParcelasSiniestradasManager.class);
	private IParcelasSiniestradasDao parcelasSiniestradasDao;
	private DatabaseManager databaseManager;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");

	/**
	 * carga las parcelas de siniestros de bbdd
	 * 23/04/2014 U029769
	 * @param capitalAsegSiniestradoDV
	 * @param referencia
	 * @param linea
	 * @param realPath
	 * @return
	 * @throws BusinessException
	 */
	public List<CapAsegSiniestradoDV> buscarCapAsegSiniestradoDV (CapAsegSiniestradoDV capitalAsegSiniestradoDV) throws BusinessException {

		List<ParcelaSiniestro> listaParcelasSiniestro = new ArrayList<ParcelaSiniestro>();
		List<CapAsegSiniestradoDV> capitalesAsegSiniestrados = new ArrayList<CapAsegSiniestradoDV>(0);
		try {
				
				listaParcelasSiniestro = parcelasSiniestradasDao.listSiniestroParcelas(capitalAsegSiniestradoDV);
			
			} catch (DAOException e) {
				logger.error("Se ha producido un error durante la consulta de parcelas siniestradas", e);
				throw new BusinessException ("Se ha producido un error durante la consulta de parcelas siniestradas", e);
			}
		
		if(!listaParcelasSiniestro.isEmpty()) {
			//Creamos el listado de capitales siniestrados por cada parcela que es lo que se muestra en el listado
			capitalesAsegSiniestrados = getCapAsegSiniestro(listaParcelasSiniestro);
		}
		
		return capitalesAsegSiniestrados;
	}
	/**
	 * Primero intentamos cargar las parcelas del WS de Modificacion, si da error llamamos
	 *		   al PL PQ_COPIAR_PARCELAS_A_SINIESTRO que se traera las parcelas de bbdd.
	 * 24/04/2014 U029769
	 * @param capitalAsegSiniestradoDV
	 * @param referencia
	 * @param linea
	 * @param realPath
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, Object> cargarParcelas(
			CapAsegSiniestradoDV capitalAsegSiniestradoDV, String referencia,
			Linea linea, String realPath) throws BusinessException {
		
		List<ParcelaSiniestro> listaParcelasSiniestro = new ArrayList<ParcelaSiniestro>();
		List<CapAsegSiniestradoDV> capitalesAsegSiniestrados = new ArrayList<CapAsegSiniestradoDV>(0);
		Map<String, Object> params = new HashMap<String, Object>();
		Long idSiniestro = capitalAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getSiniestro().getId();
		
		try {
			listaParcelasSiniestro = cargaParcelasFromWSMod (referencia,linea.getCodplan(),
					realPath,idSiniestro,linea.getLineaseguroid());
			params.put("mensaje", bundle.getObject("mensaje.cargaParcelas.ws"));
		}catch(Exception dao){
			logger.info("Error al cargar las parcelas del WS de modificacion. Intentamos cargarlas de bbdd",dao);
			try {
				String procedure = "PQ_COPIAR_PARCELAS_A_SINIESTRO.copiarParcelasEnSiniestro(P_IDSINIESTRO IN NUMBER)";
				Map<String, Object> parametros = new HashMap<String, Object>();			
				parametros.put("P_IDSINIESTRO",idSiniestro);			
				
				databaseManager.executeStoreProc(procedure, parametros);
				
				listaParcelasSiniestro = parcelasSiniestradasDao.listSiniestroParcelas(capitalAsegSiniestradoDV);
				params.put("alerta", bundle.getObject("mensaje.cargaParcelas.bbdd"));
			}catch(Exception ex){	
				logger.error("Se ha producido un error durante la consulta de parcelas siniestradas", ex);
				throw new BusinessException ("Se ha producido un error durante la consulta de parcelas siniestradas", ex);
			}
		}
		if(!listaParcelasSiniestro.isEmpty()) {
			//Creamos el listado de capitales siniestrados por cada parcela que es lo que se muestra en el listado
			capitalesAsegSiniestrados = getCapAsegSiniestro(listaParcelasSiniestro);
		}
		params.put("capitalesAsegSiniestrados", capitalesAsegSiniestrados);
		return params;
	}
	
	
	private List<ParcelaSiniestro> cargaParcelasFromWSMod(String referencia, BigDecimal plan,  
			String realPath, Long idSiniestro, Long lineaseguroid) throws Exception { 
		 
		PolizaActualizadaResponse respuesta = new PolizaActualizadaResponse(); 
		List<CapAsegSiniestro> listaCapAseg = new ArrayList<CapAsegSiniestro>(); 
		List<ParcelaSiniestro> listaParcelasSiniestro = new ArrayList<ParcelaSiniestro>(); 
		ParcelaSiniestro parcelaSiniestro = null; 
		CapAsegSiniestro capAsegSiniestro = null; 
		 
		try { 
			SWAnexoModificacionHelper helper = new SWAnexoModificacionHelper(); 
			respuesta = helper.getPolizaActualizada(referencia, plan, realPath); 
			 
			/* Pet. 57626 ** MODIF TAM (15.06.2020) ** Inicio */ 
			//Recorremos las Parcelas de la situacion actualizada y vamos creando las Parcelas del anexo 
			Node currNode = respuesta.getPolizaPrincipalUnif().getPoliza().getObjetosAsegurados().getDomNode().getFirstChild(); 
			 
			while (currNode != null) { 
				if (currNode.getNodeType() == Node.ELEMENT_NODE) { 
					 
					es.agroseguro.contratacion.parcela.ParcelaDocument  par = null; 
					par = es.agroseguro.contratacion.parcela.ParcelaDocument.Factory.parse(currNode); 
 
					if (par != null){ 
			 
						//for (Parcela par : respuesta.getPolizaPrincipal().getPoliza().getObjetosAsegurados().getParcelaArray()){ 
						listaCapAseg = new ArrayList<CapAsegSiniestro>(); 
					 
					 
						parcelaSiniestro = new ParcelaSiniestro(); 
						 
						// Parcela 
						parcelaSiniestro.getSiniestro().setId(idSiniestro); 
						parcelaSiniestro.setAltaensiniestro(Constants.CHARACTER_N); 
						parcelaSiniestro.setHoja(new BigDecimal(par.getParcela().getHoja())); 
						parcelaSiniestro.setNumero(new BigDecimal(par.getParcela().getNumero())); 
						parcelaSiniestro.setNomparcela(par.getParcela().getNombre()); 
						parcelaSiniestro.setParcela(null); 
						// Ubicacion 
						parcelaSiniestro.setCodprovincia(new BigDecimal(par.getParcela().getUbicacion().getProvincia())); 
						parcelaSiniestro.setCodtermino(new BigDecimal(par.getParcela().getUbicacion().getTermino())); 
						if (!StringUtils.nullToString(par.getParcela().getUbicacion().getSubtermino()).equals("")){ 
							parcelaSiniestro.setSubtermino(par.getParcela().getUbicacion().getSubtermino().charAt(0)); 
						} 
						else{ 
							parcelaSiniestro.setSubtermino(' '); 
						} 
						 
						parcelaSiniestro.setCodcomarca(new  BigDecimal(par.getParcela().getUbicacion().getComarca())); 
						// Variedad 
						parcelaSiniestro.setCodvariedad(new BigDecimal(par.getParcela().getCosecha().getVariedad())); 
						parcelaSiniestro.setCodcultivo(new BigDecimal(par.getParcela().getCosecha().getCultivo())); 
	 
						/*if (par.getIdentificacionCatastral()!= null) { 
							parcelaSiniestro.setPoligono(par.getIdentificacionCatastral().getPoligono()); 
						}*/ 
						 
						//SIGPAC 
						parcelaSiniestro.setAgrsigpac(new BigDecimal(par.getParcela().getSIGPAC().getAgregado())); 
						parcelaSiniestro.setCodprovsigpac(new BigDecimal(par.getParcela().getSIGPAC().getProvincia())); 
						parcelaSiniestro.setCodtermsigpac(new BigDecimal(par.getParcela().getSIGPAC().getTermino())); 
						parcelaSiniestro.setParcelasigpac(new BigDecimal(par.getParcela().getSIGPAC().getParcela())); 
						parcelaSiniestro.setRecintosigpac(new BigDecimal(par.getParcela().getSIGPAC().getRecinto())); 
						parcelaSiniestro.setZonasigpac(new BigDecimal(par.getParcela().getSIGPAC().getZona())); 
						parcelaSiniestro.setPoligonosigpac(new BigDecimal(par.getParcela().getSIGPAC().getPoligono())); 
						 
						 
						for (es.agroseguro.contratacion.parcela.CapitalAsegurado capAseg : par.getParcela().getCosecha().getCapitalesAsegurados().getCapitalAseguradoArray()){ 
							capAsegSiniestro = new CapAsegSiniestro(); 
							 
							capAsegSiniestro.setSuperficie(capAseg.getSuperficie()); 
							capAsegSiniestro.setCodtipocapital(new BigDecimal(capAseg.getTipo())); 
							capAsegSiniestro.setAltaensiniestro(Constants.CHARACTER_N); 
							 
							capAsegSiniestro.setParcelaSiniestro(parcelaSiniestro); 
							/* SIGPE 6608 Los datos variables no se copian en el siniestro para que no salgan en el infome ni en el xml*/ 
							/*List<DatVarCapAsegSiniestro> datosVariables = getDatosVariables( 
									capAseg, respuesta.getPolizaPrincipal().getPoliza().getCobertura().getDatosVariables(), 
									lineaseguroid,capAsegSiniestro); 
							 
							//guardamos los datos variables 
							Set<DatVarCapAsegSiniestro> setCapDatosVar = new HashSet<DatVarCapAsegSiniestro>(datosVariables); 
							capAsegSiniestro.setDatVarCapAsegSiniestros(setCapDatosVar);*/ 
							 
							listaCapAseg.add(capAsegSiniestro); 
						} 
						Set<CapAsegSiniestro> setCap = new HashSet<CapAsegSiniestro>(listaCapAseg); 
						parcelaSiniestro.setCapAsegSiniestros((Set<CapAsegSiniestro>) setCap); 
						 
						parcelasSiniestradasDao.saveOrUpdate(parcelaSiniestro); 
						 
						listaParcelasSiniestro.add(parcelaSiniestro); 
						 
					} 
				} 
				currNode = currNode.getNextSibling(); 
			}/* Fin del While*/ 
		}catch (AgrException e){ 
			logger.error("El servicio ha devuelto una excepcion"); 
			throw e; 
			 
		}catch (Exception ex) { 
			logger.error("Error inesperado"); 
			throw ex; 
		} 
		return listaParcelasSiniestro; 
	} 
	
	/**
	 * Obtenemos un listado de las capitales asegurados siniestrados. Por cada parcela
	 * recuperada de la poliza formamos  capitales Asegurados Siniestrados con los datos
	 * variables del capital asegurado.
	 * 
	 * @param listaParcelasSiniestro	
	 * @return
	 */	
	private List<CapAsegSiniestradoDV> getCapAsegSiniestro(List<ParcelaSiniestro> listaParcelasSiniestro){
		List<CapAsegSiniestradoDV> capAsegSiniestradoDV = new ArrayList<CapAsegSiniestradoDV>();
		final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
// 		Recorremos la listaParcelasSiniestro para formar la capAsegSiniestradoDV
		for (ParcelaSiniestro parcela : listaParcelasSiniestro) {					
			for(CapAsegSiniestro capital : parcela.getCapAsegSiniestros()){
				CapAsegSiniestradoDV CapAsegSiDV = new CapAsegSiniestradoDV();
				CapAsegSiDV.setCapAsegSiniestro(capital);
//				Recogemos los DV por cada capital asegurado.
				for(DatVarCapAsegSiniestro dvcapital : capital.getDatVarCapAsegSiniestros()){
					if(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_FRUTOS_CAIDOS).equals(dvcapital.getCodconcepto())){
						if(dvcapital.getValor().equals("S")){
// 							Activamos la check de Frutos Caidos
							CapAsegSiDV.setFrutos(true);
						}							
					}else if(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_FECHA_RECOLEC).equals(dvcapital.getCodconcepto())){
// 						Establecemos la fecha de recoleccion	
						String sFecha = dvcapital.getValor();
						if(sFecha!= null){
							try {
								CapAsegSiDV.setFechaRecoleccion(df.parse(sFecha));
							} catch (ParseException ignore) {}
						}		
					}					
				}				
				capAsegSiniestradoDV.add(CapAsegSiDV);
			}
		}
		
		return capAsegSiniestradoDV;
	}
	
	/**
	 * Método para generar los datos variables de los capitales asegurados según los valores indicados
	 * @param idsSeleccionados Identificadores de los capitales a actualizar
	 * @param frutosSeleccionados Valor que indica si se ha marcado el check de frutos caídos
	 * @param fechaRecolSelect Valor para la fecha de recolección
	 * @throws BusinessException
	 */
	public void generateAndSaveListaCapitalesSiniestrados(String idsSeleccionados, String frutosSeleccionados, String fechaRecolSelect) throws BusinessException{
		
		List<DatVarCapAsegSiniestro> dv_baja = new ArrayList<DatVarCapAsegSiniestro>();
		
		try {
		
			if(!idsSeleccionados.equals("")){
				List<Long> listaIdsSeleccionados = new ArrayList<Long>();
				for(String id: idsSeleccionados.split(";")){
					if (!StringUtils.nullToString(id).equals(""))
						listaIdsSeleccionados.add(new Long(id));
				}
				
				//Obtengo los capitales de la base de datos segun los idsSeleccionados
				List<CapAsegSiniestro> lista = this.parcelasSiniestradasDao.getCapitalesAseguradosSiniestro(listaIdsSeleccionados);
				
				if (!StringUtils.nullToString(frutosSeleccionados).equals("") || !StringUtils.nullToString(fechaRecolSelect).equals("")){
					//En este caso se dan las parcelas de alta en el siniestro
					for(CapAsegSiniestro cas : lista){
						cas.setAltaensiniestro('S');
						
						boolean tieneFrutos = false;
						boolean tieneFecha = false;
						
						//Recorro los datos variables por si ya lo tenia de antes
						for (DatVarCapAsegSiniestro dvca : cas.getDatVarCapAsegSiniestros()){
							if (dvca.getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_FRUTOS_CAIDOS))){
								tieneFrutos = true;
								if (StringUtils.nullToString(frutosSeleccionados).equals("S")){
									dvca.setValor("S");
								}
								else{
									dvca.setValor("N");
								}
							}
							else if (dvca.getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_FECHA_RECOLEC))){
								tieneFecha = true;
								if (!StringUtils.nullToString(fechaRecolSelect).equals("")){
									dvca.setValor(fechaRecolSelect);
								}
								else{
									//Hay que borrar este dato variable
									dv_baja.add(dvca);
								}
							}
						}
						
						//Si no tenia el Dato Variable de frutos, se lo anado
						if (!tieneFrutos){
							DatVarCapAsegSiniestro dvFrutos = getDatoVariableFrutos(frutosSeleccionados, cas);
							cas.getDatVarCapAsegSiniestros().add(dvFrutos);
						}
						
						//Si no tenia el Dato Variable fecha de recoleccion, se lo anado
						if (!tieneFecha && !StringUtils.nullToString(fechaRecolSelect).equals("")){
							DatVarCapAsegSiniestro dvFecha = getDatoVariableFechaRecoleccion(fechaRecolSelect, cas);
							cas.getDatVarCapAsegSiniestros().add(dvFecha);
						}
						//Guardo el capital asegurado del siniestro
						this.parcelasSiniestradasDao.saveOrUpdate(cas);
						this.parcelasSiniestradasDao.evict(cas);
					}
				}
				else{
					//Damos de baja las parcelas del siniestro
					for(CapAsegSiniestro cas : lista){
						cas.setAltaensiniestro('N');
						
						//Recorro los datos variables y los anado a la lista para eliminarlos
						for (DatVarCapAsegSiniestro dvca : cas.getDatVarCapAsegSiniestros()){
							dv_baja.add(dvca);
						}
						//Guardo el capital asegurado del siniestro
						this.parcelasSiniestradasDao.saveOrUpdate(cas);
						this.parcelasSiniestradasDao.evict(cas);
					}
				}
				
				//Eliminamos los datos variables que se han quitado
				for (DatVarCapAsegSiniestro datVarEliminar : dv_baja){
					this.parcelasSiniestradasDao.delete(datVarEliminar);
				}
				
				//Actualizamos el dato "AltaEnSiniestro" de las parcelas.
				List<ParcelaSiniestro> parcelasSiniestro = this.parcelasSiniestradasDao.getParcelasSiniestradas(listaIdsSeleccionados);
				for (ParcelaSiniestro ps : parcelasSiniestro){
					boolean alta = false;
					for (CapAsegSiniestro cas : ps.getCapAsegSiniestros()){
						if (cas.getAltaensiniestro().equals('S')){
							ps.setAltaensiniestro('S');
							alta = true;
						}
						else{
							ps.setAltaensiniestro('N');
						}
						this.parcelasSiniestradasDao.saveOrUpdate(ps);
						if (alta)
							break;
					}
				}
			}
		} catch (Exception e) {
			logger.error("Se ha producido un error general", e);
			throw new BusinessException(e);
		} 
	}

	private DatVarCapAsegSiniestro getDatoVariableFechaRecoleccion(String fechaRecolSelect, CapAsegSiniestro cas) {
		
		DatVarCapAsegSiniestro dvFecha = new DatVarCapAsegSiniestro();
		dvFecha.setCapAsegSiniestro(cas);
		dvFecha.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_FECHA_RECOLEC));
		dvFecha.setValor(fechaRecolSelect);
		return dvFecha;
	}

	private DatVarCapAsegSiniestro getDatoVariableFrutos(String frutosSeleccionados, CapAsegSiniestro cas) {
		
		DatVarCapAsegSiniestro dvFrutos = new DatVarCapAsegSiniestro();
		dvFrutos.setCapAsegSiniestro(cas);
		dvFrutos.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_FRUTOS_CAIDOS));
		if (StringUtils.nullToString(frutosSeleccionados).equals("S")){
			dvFrutos.setValor("S");
		}
		else{
			dvFrutos.setValor("N");
		}
		return dvFrutos;
	}
	
	public boolean validarFechasRecoleccion(String idSiniestro){
		boolean valid = true;
		
		Siniestro siniestro = (Siniestro) this.parcelasSiniestradasDao.getObject(Siniestro.class, new Long(idSiniestro));
		for (ParcelaSiniestro parcela : siniestro.getParcelasSiniestros()){
			for (CapAsegSiniestro cas: parcela.getCapAsegSiniestros()){
				if (cas.getAltaensiniestro().equals('S')){
					boolean tieneFecha = false;
					for (DatVarCapAsegSiniestro dv : cas.getDatVarCapAsegSiniestros()){
						if (dv.getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_FECHA_RECOLEC)) && 
								!StringUtils.nullToString(dv.getValor()).equals("")){
							tieneFecha = true;
						}
					}
					if (!tieneFecha){
						valid = false;
						break;
					}
				}
			}
			if (!valid)
				break;
		}
		
		return valid;
	}
	
	/**DAA 15/06/2012
	 * Metodo que devuelve un String de los ids de capAsegSiniestrados
	 * @param capAsegSiniestrados 
	 * @return listaIdsCap
	 */
	public String getIdsCap(List<CapAsegSiniestradoDV> capAsegSiniestrados) {
		
		String listaIdsCap="";
		int i = 0; 
		for(i=0; i< capAsegSiniestrados.size(); i++){
			CapAsegSiniestradoDV capAseg = capAsegSiniestrados.get(i);
			listaIdsCap += capAseg.getCapAsegSiniestro().getId()+ ";";
		}
		
		return listaIdsCap;
	}

	public void setParcelasSiniestradasDao(IParcelasSiniestradasDao parcelasSiniestradasDao) {
		this.parcelasSiniestradasDao = parcelasSiniestradasDao;
	}

	public void setDatabaseManager(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}
}