package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.manager.impl.anexoRC.PolizaActualizadaRCResponse;
import com.rsi.agp.core.manager.impl.anexoRC.SWAnexoRCHelper;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.managers.impl.anexoMod.PolizaActualizadaResponse;
import com.rsi.agp.core.managers.impl.anexoMod.SWAnexoModificacionHelper;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.impl.DatabaseManager;
import com.rsi.agp.dao.models.poliza.IParcelaReduccionCapitalDao;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.reduccionCap.CapitalAsegurado;
import com.rsi.agp.dao.tables.reduccionCap.Parcela;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;

import es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException;

public class ParcelasReduccionCapitalManager implements IManager {

	private Log logger = LogFactory.getLog(ParcelasReduccionCapitalManager.class);		
	private IParcelaReduccionCapitalDao parcelaReduccionCapitalDao;
	private DatabaseManager databaseManager;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	public List<CapitalAsegurado> buscarCapitalesAsegurados (CapitalAsegurado capitalAsegurado) throws BusinessException {
		
		List<CapitalAsegurado> listaParcelasRC = new ArrayList<CapitalAsegurado>();
		try {
				listaParcelasRC = parcelaReduccionCapitalDao.listReduccionCapitalParcelas(capitalAsegurado);
			
		} catch (DAOException e) {
			logger.error("Se ha producido un error durante la consulta de parcelas de reduccion de capital", e);			
			throw new BusinessException ("Se ha producido un error durante la consulta de reduccion de capital", e);
			
		}
		return listaParcelasRC;
	}
	
	public Map<String, Object> cargarParcelas(CapitalAsegurado capitalAsegurado,String referencia,
	Linea linea, String realPath) throws BusinessException {
		
		List<CapitalAsegurado> listaParcelasRC = new ArrayList<CapitalAsegurado>();
		Map<String, Object> params = new HashMap<String, Object>();
		Long idRedCap = capitalAsegurado.getParcela().getReduccionCapital().getId();
		
		try {
			listaParcelasRC = cargaParcelasFromWSMod (referencia, linea.getCodplan(),
					realPath, idRedCap, linea.getLineaseguroid());
			params.put("mensaje", bundle.getObject("mensaje.cargaParcelas.ws"));
		}catch(Exception dao){
			try {
				//Cargamos con un PL/SQL las parcelasReduccionCapital y capitalesAsegurados para una reduccion de capital, de Copy o de Poliza.
				String procedure = "PQ_COPIAR_PARCELAS_A_RED_CAP.copiarParcelasEnRedCap(P_IDREDCAP IN NUMBER)";
				//Establecemos los parametros para llamar al PL/SQL.
				Map<String, Object> parametros = new HashMap<String, Object>();			
				parametros.put("P_IDREDCAP",idRedCap );			
				//Llamada al procedimiento almacenado
				databaseManager.executeStoreProc(procedure, parametros);
							
				listaParcelasRC = parcelaReduccionCapitalDao.listReduccionCapitalParcelas(capitalAsegurado);
				
				params.put("alerta", bundle.getObject("mensaje.cargaParcelas.bbdd"));
				
			}catch(DAOException da){
				logger.error("Se ha producido un error durante la consulta de parcelas de reduccion de capital", da);			
				throw new BusinessException ("Se ha producido un error durante la consulta de reduccion de capital", da);
			} 
		}
		params.put("listCapitalesAsegurados", listaParcelasRC);
		return params;
		
	}
	
	/**
	 * obtiene las parcelas del WS
	 * 24/04/2014 U029769
	 * @param referencia
	 * @param codplan
	 * @param realPath
	 * @param idRedCap
	 * @param lineaseguroid
	 * @return
	 * @throws Exception
	 */
	private List<CapitalAsegurado> cargaParcelasFromWSMod(String referencia, BigDecimal codplan, String realPath,
			Long idRedCap, Long lineaseguroid) throws Exception {

		List<CapitalAsegurado> listCapitalesAsegurados = new ArrayList<CapitalAsegurado>();
		PolizaActualizadaRCResponse respuesta = new PolizaActualizadaRCResponse();
		CapitalAsegurado cp = null;
		Parcela parcelaRC = null;

		try {
			SWAnexoRCHelper helper = new SWAnexoRCHelper();
			respuesta = helper.consultarContratacionRC(referencia, codplan, realPath);

			// DNF:
			logger.debug("Obteniendo anexo RC con id " + idRedCap);
			ReduccionCapital rcapt = (ReduccionCapital) this.parcelaReduccionCapitalDao
					.getObject(ReduccionCapital.class, idRedCap);
			rcapt.setCodModulo(respuesta.getPolizaPrincipalUnif() == null
					? respuesta.getPolizaPrincipal().getPoliza().getCobertura().getModulo()
					: respuesta.getPolizaPrincipalUnif().getPoliza().getCobertura().getModulo());
			this.parcelaReduccionCapitalDao.saveOrUpdate(rcapt);
			
			// Pet. 57626 ** MODIF TAM (15.06.2020) ** Inicio */
			// Recorremos las explotaciones de la situación actualizada y vamos creando las
			// explotaciones del anexo
			Node currNode = respuesta.getPolizaPrincipalUnif() == null
					? respuesta.getPolizaPrincipal().getPoliza().getObjetosAsegurados().getDomNode().getFirstChild()
					: respuesta.getPolizaPrincipalUnif().getPoliza().getObjetosAsegurados().getDomNode()
							.getFirstChild();

			while (currNode != null) {
				
				if (currNode.getNodeType() == Node.ELEMENT_NODE) {

					es.agroseguro.contratacion.parcela.ParcelaDocument par = es.agroseguro.contratacion.parcela.ParcelaDocument.Factory.parse(currNode);
					if (par != null) {

						es.agroseguro.contratacion.parcela.CapitalAsegurado[] capAsegArr = par.getParcela().getCosecha().getCapitalesAsegurados().getCapitalAseguradoArray();
						for (es.agroseguro.contratacion.parcela.CapitalAsegurado capAseg : capAsegArr) {
							if (capAseg != null) {
								parcelaRC = new Parcela();
	
								// Parcela
								parcelaRC.setReduccionCapital(rcapt);
								parcelaRC.setAltaenanexo(Constants.CHARACTER_N);
								parcelaRC.setHoja(new BigDecimal(par.getParcela().getHoja()));
								parcelaRC.setNumero(new BigDecimal(par.getParcela().getNumero()));
								parcelaRC.setNomparcela(par.getParcela().getNombre());
								parcelaRC.setParcela(null);
								// Ubicacion
								parcelaRC.setCodprovincia(new BigDecimal(par.getParcela().getUbicacion().getProvincia()));
								parcelaRC.setCodtermino(new BigDecimal(par.getParcela().getUbicacion().getTermino()));
								if (!StringUtils.nullToString(par.getParcela().getUbicacion().getSubtermino()).equals("")) {
									parcelaRC.setSubtermino(par.getParcela().getUbicacion().getSubtermino().charAt(0));
								} else {
									parcelaRC.setSubtermino(' ');
								}
	
								parcelaRC.setCodcomarca(new BigDecimal(par.getParcela().getUbicacion().getComarca()));
								// Variedad
								parcelaRC.setCodvariedad(new BigDecimal(par.getParcela().getCosecha().getVariedad()));
								parcelaRC.setCodcultivo(new BigDecimal(par.getParcela().getCosecha().getCultivo()));
	
								// SIGPAC
								parcelaRC.setAgrsigpac(new BigDecimal(par.getParcela().getSIGPAC().getAgregado()));
								parcelaRC.setCodprovsigpac(new BigDecimal(par.getParcela().getSIGPAC().getProvincia()));
								parcelaRC.setCodtermsigpac(new BigDecimal(par.getParcela().getSIGPAC().getTermino()));
								parcelaRC.setParcelasigpac(new BigDecimal(par.getParcela().getSIGPAC().getParcela()));
								parcelaRC.setRecintosigpac(new BigDecimal(par.getParcela().getSIGPAC().getRecinto()));
								parcelaRC.setZonasigpac(new BigDecimal(par.getParcela().getSIGPAC().getZona()));
								parcelaRC.setPoligonosigpac(new BigDecimal(par.getParcela().getSIGPAC().getPoligono()));
	
								cp = new CapitalAsegurado();
	
								cp.setAltaenanexo(Constants.CHARACTER_N);
								cp.setCodtipocapital((short) capAseg.getTipo());
								cp.setPrecio(capAseg.getPrecio());
								cp.setProd(new BigDecimal(capAseg.getProduccion()));
								cp.setSuperficie(capAseg.getSuperficie());
								cp.setParcela(parcelaRC);
	
								parcelaRC.getCapitalAsegurados().add(cp);
	
								parcelaReduccionCapitalDao.saveOrUpdate(parcelaRC);
	
								// en esta lista guardamos todos los capitales asegurados para mostrarlos en la
								// jsp (esta lista no se reinicia)
								listCapitalesAsegurados.add(cp);
							}
						}
					}
				}
				currNode = currNode.getNextSibling();
			} /* Fin del While */
		} catch (AgrException e) {
			logger.error("El servicio ha devuelto una excepción");
			throw e;

		} catch (Exception ex) {
			logger.error("Error inesperado", ex);
			throw ex;
		}

		return listCapitalesAsegurados;
	}


	public void guardarCapitalesAsegurados (List<CapitalAsegurado> listaCapitalesAsegurados)throws BusinessException {
			
		try {			
		
			parcelaReduccionCapitalDao.saveCapitalesAsegurados(listaCapitalesAsegurados);
			
		} catch (DAOException dao) {	
			
			logger.error("Se ha producido un error durante el alta de parcelas siniestradas", dao);
			throw new BusinessException ("Se ha producido un error durante el alta de parcelas siniestradas", dao);
			
		}		
	}
	/**
	 * Guarda Reduccion de Capital
	 * @param reduccionCapital
	 * @throws BusinessException
	 */
	public void guardarReduccionCapital (ReduccionCapital reduccionCapital)throws BusinessException {
		
		try {			
		
			parcelaReduccionCapitalDao.saveReduccionCapital(reduccionCapital);
			
		} catch (DAOException dao) {	
			
			logger.error("Se ha producido un error durante al guardar el capitalAsegurado", dao);
			throw new BusinessException ("Se ha producido un error durante al guardar el capitalAsegurado", dao);
			
		}		
	}
	
	/**
	 * Consulta por id de Reduccion de Capital
	 * @param reduccionCapital
	 * @throws BusinessException
	 */
	public ReduccionCapital getReduccionCapitalById (Long id)throws DAOException {
		
		return parcelaReduccionCapitalDao.getReduccionCapitalById(id);		
	}
	
	
	
	/**
	 * Metodo que devuelve  string por referencia, con los listados de checks de alta, frutos y fechas
	 * @param capAsegSiniestrados
	 * @param altas
	 * @param frutos
	 * @param fechas
	 */
	public void getCapitalesProdPost(List<CapitalAsegurado> capitalesAsegurados,StringBuilder altas,StringBuilder producciones) {
		String alta = "alta_";		
		for(CapitalAsegurado capAseg : capitalesAsegurados){
			if(String.valueOf(capAseg.getAltaenanexo()).equals("S")){
				altas.append(alta + capAseg.getId() + "|");
			}			
			if(capAseg.getProdred() != null){
				producciones.append(capAseg.getId() + "#" + capAseg.getProdred() + "|");
			}
		}
		
	}
	
	/**
	 * Comprueba que hay al menos una parcela dada de Alta
	 * @param String
	 * @return boolean
	 */
	
	public boolean isParcelasAlta (String checksAlta){
	      if(checksAlta.equals("") || !checksAlta.contains("alta"))
	    	  return false;
	          return true;
	}
	
	public List<Parcela> listReduccionCapitalParcelas (CapitalAsegurado capitalAsegurado) throws DAOException {
		
		return new ArrayList<Parcela>();
	}
	
	public List<CapitalAsegurado> list(Long idCapitalAsegurado) throws DAOException {
		
		return new ArrayList<CapitalAsegurado>();
	}


	public IParcelaReduccionCapitalDao getParcelaReduccionCapitalDao() {
		return parcelaReduccionCapitalDao;
	}

	public void setParcelaReduccionCapitalDao(IParcelaReduccionCapitalDao parcelaReduccionCapitalDao) {
		this.parcelaReduccionCapitalDao = parcelaReduccionCapitalDao;
	}

	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}

	public void setDatabaseManager(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}
	
}
