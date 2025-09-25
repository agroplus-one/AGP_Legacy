/**
 * MANAGER
 * 
 * métodos para validar ambitos de contratracion
 */

package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.filters.cpl.ModuloFiltro;
import com.rsi.agp.dao.models.admin.IClaseDao;
import com.rsi.agp.dao.models.poliza.IDatosParcelaDao;
import com.rsi.agp.dao.models.poliza.IFechaContratacionDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class FechaContratacionManager implements IManager {
	
	/* CONSTANTS
	 ------------------------------------------------------------------------ */
	private static final Log logger = LogFactory.getLog(PolizaManager.class);
	
	/* VARIABLES
	 ------------------------------------------------------------------------ */
	private IFechaContratacionDao fechaContratacionDao;
	private IDatosParcelaDao datosParcelaDao;
	private IClaseDao claseDao;
	private IPolizaDao polizaDao;

	/* MÉTODOS PÚBLICOS
	 ------------------------------------------------------------------------ */
	
	/**
	 * Valida si un método esta dentro del ambito de contratacion
	 * @return true or false
	 */
	public boolean validateOneModulo(String modulo, Poliza poliza)throws BusinessException {
		boolean result = true;
		
		try{
			
			// obtengo los cultivos por clase seleccionada
			List<BigDecimal> listCultivo = polizaDao.getCultivosClase(poliza.getClase(), poliza.getLinea().getLineaseguroid());
			
			if(listCultivo.size() > 0 && poliza.getClase()!= null){
				result = fechaContratacionDao.validarPorModulo(listCultivo, modulo ,poliza.getLinea().getLineaseguroid());	
			}
			
		}catch(Exception ex){
			logger.error(ex);
		    throw new BusinessException("[FechasContratacionManager] validateOneModulo() - error al validar si el modulo seleccionado esta dentro del ambito de contratacion ",ex);
		}

		return result;
	}
	/**
	 * Valida si el modulo esta dentro del periodo de contratacion
	 * @return resultado de la validacion (true or false)
	 */
	@SuppressWarnings("unchecked") 
	public HashMap<String, String> validateAllModulos(Poliza poliza)throws BusinessException {
		boolean result = true;
		List<Modulo> modulos = null;
		HashMap<String, String> isInPerioContratMods = new HashMap<String, String>();
		
		try {
		
			//filtrado de modulos por lineaseguroid en la tabla clase
			
			List<String> lstModulos = new ArrayList<String>();
			lstModulos= claseDao.dameListaModulosClase(new Long(poliza.getLinea().getLineaseguroid().toString()), poliza.getClase());
			
			ModuloFiltro filtro = new ModuloFiltro(poliza.getLinea().getLineaseguroid(), lstModulos);
			filtro.setPpalComplementario(Constants.MODULO_POLIZA_PRINCIPAL);
			
			modulos = fechaContratacionDao.getObjects(filtro);

			for(int i=0; i < modulos.size(); i++) { 
				// obtengo los cultivos por clase seleccionada 
				List<BigDecimal> listCultivo = polizaDao.getCultivosClase(poliza.getClase(), poliza.getLinea().getLineaseguroid()); 
				if(listCultivo.size() > 0 && poliza.getClase()!= null) { 
						try { 
							result = fechaContratacionDao.validarPorModulo(listCultivo, modulos.get(i).getId().getCodmodulo() ,poliza.getLinea().getLineaseguroid());							 
							if(result) { 
								isInPerioContratMods.put(modulos.get(i).getId().getCodmodulo(), ""); 
							} else { 
								isInPerioContratMods.put(modulos.get(i).getId().getCodmodulo(), "&nbsp;(fuera del periodo de contrataci&oacute;n)"); 
							} 
						} catch(Exception ex) { 
							logger.error("fechaContratacionDao.validarPorModulo - " + ex.getMessage()); 
						} 
				} 
			}
		} catch (Exception ex) {
			logger.error(ex);
		    throw new BusinessException("[FechasContratacionManager] validateAllModulos() - error ",ex);
		} 

		return isInPerioContratMods;	
	}
	
	/**
	 * valida si la parcela esta dentro del periodo de contratacion
	 * @return resultado de la validacion (true or false)
	 */
	public boolean validarPorParcela(Parcela parcela)throws BusinessException {
		boolean result = false;
		
		try {
			
			result = fechaContratacionDao.validarPorParcela();
			
		} catch (Exception ex) {
			logger.error(ex);
		    throw new BusinessException("[FechasContratacionManager] validarPorParcela() - error ",ex);
		}
		
		return result;
	}
	
	/**
	 * valida si las parcelas estan dentro del periodo de contratacion
	 * @return resultado de la validacion (true or false)
	 */
	public boolean validarPorParcelas(Poliza poliza)throws BusinessException {
		boolean result = false;
		
		try {
			
			
			
			
		} catch (Exception ex) {
			logger.error(ex);
		    throw new BusinessException("[FechasContratacionManager] validarPorParcelas() - error ",ex);
		}
		
		return result;
	}

	/**
	 * valida si la poliza esta dentro del periodo de contratacion
	 * @return resultado de la validacion (true or false)
	 */
	public boolean validarPorPolizas()throws BusinessException  {
		boolean result = false;
		
		try {
			
			
			
		} catch (Exception ex) {
			logger.error(ex);
		    throw new BusinessException("[FechasContratacionManager] validarPorPolizas() - error ",ex);
		}
		
		return result;
	}
	
	/**
	 * Genera una tabla con el modulo fuera del alcance contratacion
	 * @param fechaContratacionDao
	 */
    public String getTableModsParcsNoDefin(List<Poliza> listPolizas) throws BusinessException {
    	
    	String result = "";
    	StringBuilder table = new StringBuilder();
    	List<Poliza> listModulosFueraAmbito  = null;
		List<Parcela> listParcelasFueraAmbito = null;
        
    	try{
    		
    		listModulosFueraAmbito  = getModulosFueraAmbito(listPolizas);
    		listParcelasFueraAmbito = getParcelasFueraAmbito(listPolizas);
    		
	    	table.append("<div style=\"color:black;border:1px solid #DD3C10;font-size:12px;text-align:center;");
	    	table.append("line-height:20px;background-color:#FFEBE8;\">");
	    	table.append("Modulos o Parcelas fuera del ambito de contratacion. Imposible pasar a definitiva.");
	    	table.append("</div>");
	    	
	    	/* MODULOS POLIZA */
	    	if( listModulosFueraAmbito.size() > 0)
	    	{
			    	table.append("<div style=\"padding:10px 3px;font-size:12px;color:black;\">Modulos afectados: &nbsp; <span style=\"font-weight:bold\">(" + listModulosFueraAmbito.size() + ")</span></div>");
			    	table.append("<table class=\"LISTA\">");
			    	table.append("<thead>");
			    	table.append("<th class=\"cblistaImg\">Modulo</th>");
			    	table.append("<th class=\"cblistaImg\">Nif Aseg.</th>");
			    	table.append("<th class=\"cblistaImg\">Id Colect.</th>");
			    	table.append("<th class=\"cblistaImg\">Plan</th>");
			    	table.append("<th class=\"cblistaImg\">Linea</th>");
			    	table.append("<th class=\"cblistaImg\">Seleccionar</th>");
			    	table.append("<th class=\"cblistaImg\"></th>");
			    	table.append("</thead>");
	
			    	for(int i=0; i < listModulosFueraAmbito.size(); i++ )
			    	{
			    		Poliza poliza = listModulosFueraAmbito.get(i);
			    		table.append("<tr>");
			    		table.append("<td class=\"literal\">" + 
			    				poliza.getCodmodulo() + "</td><td class=\"literal\">" +  
			    				poliza.getAsegurado().getNifcif() + "</td><td class=\"literal\">" + 
			    				poliza.getColectivo().getIdcolectivo() + "</td><td class=\"literal\">" + 
			    				poliza.getLinea().getCodplan()+"</td><td class=\"literal\">" + 
			    				poliza.getLinea().getCodlinea() + "</td>" +
			    				"<td><input type='checkbox' id='"+ poliza.getIdpoliza() +"' name='" + poliza.getIdpoliza() + "' value='" + poliza.getIdpoliza() + "' onClick=\"muestraOcultaBoton();\"/></td>");
			    		table.append("</tr>");
			    	}
			    	
			    	table.append("</table>");
	    	}
	    	
	    	/* PARCELAS */
	    	if( listParcelasFueraAmbito.size() > 0)
	    	{
			    	table.append("<div style=\"padding:10px 3px;font-size:12px;color:black;\">Parcelas afectados:&nbsp; <span style=\"font-weight:bold\">(" + listParcelasFueraAmbito.size() + ")</span></div>");
			    	table.append("<table class=\"LISTA\">");
			    	table.append("<thead>");
			    	table.append("<th class=\"cblistaImg\">Nif Aseg.</th>");
			    	table.append("<th class=\"cblistaImg\">Id Colect.</th>");
			    	table.append("<th class=\"cblistaImg\">PROV</th>");
			    	table.append("<th class=\"cblistaImg\">CMC</th>");
			    	table.append("<th class=\"cblistaImg\">TRC</th>");
			    	table.append("<th class=\"cblistaImg\">SBT</th>");
			    	table.append("<th class=\"cblistaImg\">CUL</th>");
			    	table.append("<th class=\"cblistaImg\">VAR</th>");
			    	table.append("<th class=\"cblistaImg\">Nombre</th>");
			    	table.append("</thead>");
	
			    	for(int j=0; j < listParcelasFueraAmbito.size(); j++)
			    	{
			    		Parcela parcela = listParcelasFueraAmbito.get(j);
				    	table.append("<tr>");
				    	table.append("<td class=\"literal\">" + 
				    			parcela.getPoliza().getAsegurado().getNifcif() + "</td><td class=\"literal\">" + 
				    			parcela.getPoliza().getColectivo().getIdcolectivo() + "</td><td class=\"literal\">" + 
				    			parcela.getTermino().getId().getCodprovincia() + "</td><td class=\"literal\">" + 
				    			parcela.getTermino().getId().getCodcomarca() + "</td><td class=\"literal\">" + 
				    			parcela.getTermino().getId().getCodtermino() + "</td><td class=\"literal\">" + 
				    			parcela.getTermino().getId().getSubtermino()+ "</td>");
				    	table.append("<td class=\"literal\">1</td><td class=\"literal\">3</td><td class=\"literal\">parcela cereales</td>");
				    	table.append("</tr>");
			    	}
			    	
			    	table.append("</table>");
	    	}
	    	
	    	if(listModulosFueraAmbito.size() > 0 || listParcelasFueraAmbito.size() > 0){
	    		result = table.toString();
	    	}else{
	    		result = "-1";
	    	}
    	
	    	
    	}catch(Exception ex){
    		logger.error(ex);
    		throw new BusinessException("[FechasContratacionManager] getTableModsParcsNoDefin() - error ",ex);
    	}
    	
    	return result;
    }
    
    /**
     * Obtiene una lista con los modulos fuera de ambito de la lista de polizas.
     * @param listPolizas
     * @return
     */
    public List<Poliza> getModulosFueraAmbito(List<Poliza> listPolizas) throws BusinessException {
    	List<Poliza> listPolsModsFueraAmbito = new ArrayList<Poliza>();
    	
    	try{
    		
	    	for(int i=0;i < listPolizas.size(); i++)
	    	{
	    		Poliza poliza = listPolizas.get(i);
	    		// si no es valido el modulo de la poliza lo añado a la lista
	    		if(!validateOneModulo(poliza.getCodmodulo(), poliza)){
	    			listPolsModsFueraAmbito.add(poliza);
	    		}	
	    	}
	    	
    	}catch(Exception ex){
    		logger.error(ex);
    		throw new BusinessException("[FechasContratacionManager] getModulosFueraAmbito() - error ",ex);
    	}

    	return listPolsModsFueraAmbito;
    }
    
    /**
     * Obtiene una lista de parcelas fuera de ambito de la lista de parcelas.
     */
	@SuppressWarnings("unchecked")
	public List<Parcela> getParcelasFueraAmbito(List<Poliza> listPolizas) throws BusinessException {
		List<Parcela> listParcelaFueraAmbito = new ArrayList<Parcela>();
		boolean resultVal = true;
		try {
			for (int i = 0; i < listPolizas.size(); i++) {
				Poliza poliza = listPolizas.get(i);
				List<String> modulos = this.datosParcelaDao.getCodsModulosPoliza(poliza.getIdpoliza());
				for (Parcela parcela : poliza.getParcelas()) {
					resultVal = validateOneParcela(poliza.getLinea().getLineaseguroid(), parcela, modulos);
					if (resultVal == false) {
						listParcelaFueraAmbito.add(parcela);
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex);
			throw new BusinessException("[FechasContratacionManager] getParcelasFueraAmbito() - error ", ex);
		}
		return listParcelaFueraAmbito;
	}
    
    /**
     * Comprueba si los capitales asegurados de la parcela estan dentro de fechas de contratacion
     */
	public boolean validateOneParcela(Long lineaseguroId, Parcela parcela, List<String> modulos)
			throws BusinessException {
		boolean result = true;
		for (CapitalAsegurado ca : parcela.getCapitalAsegurados()) {
			result = validateCapitalAsegurado(lineaseguroId, modulos, parcela.getCodcultivo(), parcela.getCodvariedad(),
					parcela.getTermino().getId().getCodprovincia(), parcela.getTermino().getId().getCodcomarca(),
					parcela.getTermino().getId().getCodtermino(), parcela.getTermino().getId().getSubtermino(), ca);
			if (result == false) {
				break;
			}
		}
		return result;
	}
    
    /**
     * Comprueba si el capital asegurado esta dentro de las fechas de contratacion correspondientes
     */
	public boolean validateCapitalAsegurado(Long lineaseguroId, List<String> modulos, BigDecimal codcultivo,
			BigDecimal codvariedad, BigDecimal codprovincia, BigDecimal codcomarca, BigDecimal codtermino,
			Character subtermino, CapitalAsegurado capitalAsegurado) throws BusinessException {
		boolean isValid = false;

		try {

			// Comprueba si el CA asociado a la parcela esta dentro de fechas de
			// contratacion
			isValid = datosParcelaDao.dentroDeFechasContratacion(lineaseguroId, modulos, codcultivo, codvariedad,
					codprovincia, codcomarca, codtermino, subtermino, capitalAsegurado);

		} catch (Exception ex) {
			logger.error(ex);
			throw new BusinessException("[FechasContratacionManager] validateCapitalAsegurado() - error ", ex);
		}
		return isValid;
	}
	
	/* SETTERS MANAGERS FOR SPRING IOC
	 ------------------------------------------------------------------------ */
	public void setFechaContratacionDao(IFechaContratacionDao fechaContratacionDao) {
		this.fechaContratacionDao = fechaContratacionDao;
	}
	
	public void setClaseDao(IClaseDao claseDao) {
		this.claseDao = claseDao;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}
	public void setDatosParcelaDao(IDatosParcelaDao datosParcelaDao) {
		this.datosParcelaDao = datosParcelaDao;
	}
}
