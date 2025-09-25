package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.poliza.MascaraPrecioFiltro;
import com.rsi.agp.dao.filters.poliza.PrecioFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cpl.MascaraPrecio;
import com.rsi.agp.dao.tables.cpl.Precio;
import com.rsi.agp.vo.ItemVO;
import com.rsi.agp.vo.ParcelaVO;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CalculoPrecioDao extends BaseDaoHibernate implements ICalculoPrecioDao {
	
	private static final Log logger = LogFactory.getLog(CalculoPrecioDao.class);	
	
	@Override
	public boolean calcularConPlSql() {
		boolean conPl = false;
		Session session = obtenerSession();
		
		String sql= "select AGP_VALOR from TB_CONFIG_AGP where AGP_NEMO = 'CALCULO_PRECIO_PLSQL'";
		List list = session.createSQLQuery(sql).list();
		
		if (list.get(0).toString().equals("SI")){
			conPl = true;
		}
		
		return conPl;
	}

	@Override
	public String[] getPrecioPlSql(Long lineaseguroid, List<ItemVO> datosVariablesCapAseg,  
			String codmodulo, String codcultivo, String codvariedad, 
			String codprovincia, String codcomarca, String codtermino, String subtermino) throws BusinessException {
		
		Map<String,Object> resultado = new HashMap<String, Object>();
		
		//Obtenemos los datos variables del capital asegurado y los vamos concatenando de la siguiente manera:
		// codconcepto1#valor1|codconcepto2#valor2....
		String datosVariables = getStrDatosVariables(datosVariablesCapAseg);
		
		logger.info("Inicio del calculo de precio mediante pl/sql");
		String procedure = "PQ_CALCULA_PRECIO.fn_getPrecios(P_LINEASEGUROID IN VARCHAR2" +
							 ",P_DATOSVARIABLES IN      VARCHAR2" +
							 ",P_MODULO IN              VARCHAR2" +
							 ",P_CODCULTIVO IN          VARCHAR2" +
							 ",P_CODVARIEDAD IN         VARCHAR2" +
							 ",P_PROVINCIA IN           VARCHAR2" +
							 ",P_COMARCA IN             VARCHAR2" +
							 ",P_TERMINO IN             VARCHAR2" +
							 ",P_SUBTERMINO IN          VARCHAR2) RETURN VARCHAR2";
		
		Map<String, Object> inParameters = new HashMap<String, Object>();
		inParameters.put("P_LINEASEGUROID", lineaseguroid.toString());
		inParameters.put("P_DATOSVARIABLES", datosVariables);
		inParameters.put("P_MODULO", codmodulo);
		inParameters.put("P_CODCULTIVO", codcultivo);
		inParameters.put("P_CODVARIEDAD", codvariedad);
		inParameters.put("P_PROVINCIA", codprovincia);
		inParameters.put("P_COMARCA", codcomarca);
		inParameters.put("P_TERMINO", codtermino);
		inParameters.put("P_SUBTERMINO", subtermino);
		
		logger.info("Llamada al procedimiento " + procedure);
		logger.info("Con parametros:");
		logger.info("p_lineaseguroid: "+lineaseguroid.toString());
		logger.info("p_capitalesasegurados: "+datosVariables);
		logger.info("p_modulo: "+codmodulo);
		logger.info("p_codcultivo: "+codcultivo);
		logger.info("p_codvariedad: "+codvariedad);
		logger.info("provincia: "+codprovincia);
		logger.info("p_comarca: "+codcomarca);
		logger.info("p_termino: "+codtermino);
		logger.info("p_subtermino: "+subtermino);
		
		try {
			resultado = this.databaseManager.executeStoreProc(procedure, inParameters);
			String strProducciones = (String) resultado.get("RESULT");
			if (!StringUtils.nullToString(strProducciones).equals("")){
				return strProducciones.split("#");
			}
		} catch (Exception e) {
			logger.error("Error al obtener el precio de la parcela.", e);
			throw new BusinessException("Error al obtener el precio de la parcela " + e.getMessage());
		}
		
		logger.info("Fin del calculo de precios mediante pl/sql.");
		
		return null;
	}

	private String getStrDatosVariables(List<ItemVO> datosVariablesCapAseg) {
		String datosVariables = "";
		for (ItemVO item : datosVariablesCapAseg){
			if (!StringUtils.nullToString(item.getValor()).equals("")){
				if (item.getValor().indexOf(";") > 0){
					for (String val : item.getValor().split(";")){
						datosVariables += item.getCodigo() + "|" + val + "#";
					}
				}
				else{
					datosVariables += item.getCodigo() + "|" + item.getValor() + "#";
				}
			}
		}
		
		datosVariables = datosVariables.substring(0, datosVariables.length()-1);
		
		return datosVariables;
	}
	
	@Override
	public List getMascaraPrecio(Long lineaseguroid, String codmodulo,ParcelaVO parcela) {
    	
    	List<MascaraPrecio> mascaras = new ArrayList<MascaraPrecio>();
    	
    	BigDecimal codcultivo = (parcela.getCultivo() != null && !parcela
				.getCultivo().equals("")) ? new BigDecimal(parcela.getCultivo())
				: null;
		BigDecimal codvariedad = (parcela.getVariedad() != null && !parcela
				.getVariedad().equals("")) ? new BigDecimal(parcela
				.getVariedad()) : null;
		BigDecimal codprovincia = (parcela.getCodProvincia() != null && !parcela
				.getCodProvincia().equals("")) ? new BigDecimal(parcela
				.getCodProvincia()) : null;
		BigDecimal codtermino = (parcela.getCodTermino() != null && !parcela
				.getCodTermino().equals("")) ? new BigDecimal(parcela
				.getCodTermino()) : null;
		Character subtermino = (parcela.getCodSubTermino() != null && !parcela
				.getCodSubTermino().equals("")) ? new Character(parcela
				.getCodSubTermino().charAt(0)) : null;
		BigDecimal codcomarca = (parcela.getCodComarca() != null && !parcela
				.getCodComarca().equals("")) ? new BigDecimal(parcela
				.getCodComarca()) : null;
				
		MascaraPrecioFiltro mascaraFiltro = new MascaraPrecioFiltro(lineaseguroid, codcultivo, codvariedad,
				codprovincia, codcomarca, codtermino, subtermino, codmodulo);
		
    	mascaras = getObjects(mascaraFiltro);
    	
    	if (mascaras.isEmpty()) {
    		// Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
			//	1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
			//	4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
    		mascaras = filtroMascPrecio(mascaraFiltro);
		}
    	
    	return mascaras;
    }
	
	/**
	 * Filtra por los datos genericos. Segun el siguiente orden: 
	 * 	1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
	 *  4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
	 *  
	 * @param filtroMascPrecio
	 * @return
	 */
	private List<MascaraPrecio> filtroMascPrecio(MascaraPrecioFiltro filtroMascPrecio){
		
		List<MascaraPrecio> listMascPrecio = new ArrayList<MascaraPrecio>();

		filtroMascPrecio.setAllsubterminos(true);
		listMascPrecio = this.getObjects(filtroMascPrecio);
		
		if(listMascPrecio.isEmpty()){
			filtroMascPrecio.setAllterminos(true);
			listMascPrecio = this.getObjects(filtroMascPrecio);
			
			if(listMascPrecio.isEmpty()){
				filtroMascPrecio.setAllcomarcas(true);
				listMascPrecio = this.getObjects(filtroMascPrecio);
			
				if(listMascPrecio.isEmpty()){
					filtroMascPrecio.setAllprovincias(true);
					listMascPrecio = this.getObjects(filtroMascPrecio);		
					
					if(listMascPrecio.isEmpty()){
						filtroMascPrecio.setAllvariedades(true);
						listMascPrecio = this.getObjects(filtroMascPrecio);
						
						if(listMascPrecio.isEmpty()){
							filtroMascPrecio.setAllcultivos(true);
							listMascPrecio = this.getObjects(filtroMascPrecio);
						}
					}
				}
			}
		}
	
		return listMascPrecio;
	}
	
	@Override
	public List<Precio> getPrecio(PrecioFiltro precioFiltro) {
    	
		List<Precio> precios = new ArrayList<Precio>();
		
		precios = getObjects(precioFiltro);
		
		if(precios.isEmpty()){
			precioFiltro.setAllsubterminos(true);
			precios = getObjects(precioFiltro);
			
			if(precios.isEmpty()){
				precioFiltro.setAllterminos(true);
				precios = getObjects(precioFiltro);
				
				if(precios.isEmpty()){
					precioFiltro.setAllcomarcas(true);
					precios = getObjects(precioFiltro);
					
					if(precios.isEmpty()){
						precioFiltro.setAllprovincias(true);
						precios = getObjects(precioFiltro);
					
						if(precios.isEmpty()){
							precioFiltro.setAllvariedades(true);
							precios = getObjects(precioFiltro);
							
							if(precios.isEmpty()){
								precioFiltro.setAllcultivos(true);
								precios = getObjects(precioFiltro);
							}
						}
					}
				}
			}
		}
		
		
		return precios;
	}

}
