/**
 * ----------------------------------------------------------------------------
 * Autor:       Miguel Granadino 
 * Fecha:       08/02/2012
 * Versión:     null
 * Descripción: null
 * Proyecto:    Agroplus
 * 
 * ----------------------------------------------------------------------------
 */
package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;


/**
 * DAO
 * Clase para el control de incompatibilidad por clase en polizas
 * tb_incompatibilidad_clases
 */
public class IncompatibilidadClaseDao extends BaseDaoHibernate implements IIncompatibilidadClaseDao {

	/* CONSTANTS
	 ------------------------------------------------------------------------ */
	private static final Log logger = LogFactory.getLog(IncompatibilidadClaseDao.class);
	
	/* MÉTODOS PÚBLICOS
	 ------------------------------------------------------------------------ */
	
	/**DAA 02/11/12
	 * Comprueba si una poliza es compatible consultando tb_incompatibilidad_clases
	 */	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean isCompatible(Poliza poliza) throws DAOException {
		logger.debug("init - [IncompatibilidadClaseDao] isCompatible");
		boolean isCompatible = true;
		List<Poliza> listaPolizas = new ArrayList<Poliza> ();
		List<String> listaCodModulos = new ArrayList<String> ();
		List<BigDecimal> listaSistCult = new ArrayList<BigDecimal> ();
		
		Session session = obtenerSession();
			
		//Me traigo todas las polizas que cumplan las condiciones para comprobarlas después
		BigDecimal[] estados= {Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA,
						  Constants.ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR,
						  Constants.ESTADO_POLIZA_DEFINITIVA};
		
		try {
		
			Criteria criteria = session.createCriteria(Poliza.class);
			criteria.createAlias("asegurado", "aseg");
			criteria.createAlias("estadoPoliza", "estado");
			criteria.createAlias("linea", "lin");
			criteria.add(Restrictions.eq("aseg.id", poliza.getAsegurado().getId()));
			criteria.add(Restrictions.ne("idpoliza",poliza.getIdpoliza()));
			criteria.add(Restrictions.eq("lin.lineaseguroid", poliza.getLinea().getLineaseguroid()));
			criteria.add(Restrictions.in("estado.idestado", estados));
			listaPolizas = criteria.list();
			
			// Preparamos las listas con la poliza original para luego comparar con las incompatibles  
			listaCodModulos = traerCodModulos (poliza); 
			listaSistCult = traerSistCult (poliza);
			
			/* para cada poliza que cumple las condiciones deberemos traernos el modulo + el modulo 9999 
			 * y los sitemas de cultivo de cada parcela. De esa manera obtengo una lista de modulos incompatibles y 
			 * una lista de sistemas de cultivo incompatibles */
			
			List<String> listaCodModulosIncompatibles = new ArrayList<String> ();
			List<BigDecimal> listaSistCultIncompatibles = new ArrayList<BigDecimal> ();
			
			for(int i = 0; i < listaPolizas.size(); i++ ){
				
				Poliza polizaComprobar = (Poliza) listaPolizas.get(i);
				listaCodModulosIncompatibles = traerCodModulos (polizaComprobar); 
				listaSistCultIncompatibles = traerSistCult (polizaComprobar);
				
				//Montamos la consulta
				List listaIncompatibilidades = getListaIncompatibilidades(poliza, listaCodModulos, listaSistCult,listaCodModulosIncompatibles,listaSistCultIncompatibles, polizaComprobar);
				if(listaIncompatibilidades.size()>0){
					isCompatible = false;
					break;
				}
			}
			
						
		}catch(Exception ex){
			logger.error(ex);
		    throw new DAOException("[IncompatibilidadClaseDao] isCompatible() - error ",ex);
		}
		logger.debug("end - [IncompatibilidadClaseDao] isCompatible "+ isCompatible);
		return isCompatible;
	}


	/** DAA 02/11/12
	 * @param poliza
	 * @param listaCodModulos
	 * @param listaSistCult
	 * @param session
	 * @param listaCodModulosIncompatibles
	 * @param listaSistCultIncompatibles
	 * @param polizaComprobar
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private List getListaIncompatibilidades(Poliza poliza,
			List<String> listaCodModulos, List<BigDecimal> listaSistCult,
			List<String> listaCodModulosIncompatibles, List<BigDecimal> listaSistCultIncompatibles,
			Poliza polizaComprobar) {
		Session session = obtenerSession();
		
		String sql= "select I.* from TB_INCOMPATIBILIDAD_CLASES I inner join TB_LINEAS L ON (I.LINEASEGUROID = L.LINEASEGUROID) " +
				"inner join TB_SC_C_MODULOS M ON (I.LINEASEGUROID = M.LINEASEGUROID AND I.CODMODULO = M.CODMODULO) " +
				"inner join TB_SC_C_MODULOS MI ON (I.LINEASEGUROID = MI.LINEASEGUROID AND I.CODMODULO_INCOMP = MI.CODMODULO) " +
				"WHERE I.LINEASEGUROID = " + poliza.getLinea().getLineaseguroid() + " AND I.CLASE = " + poliza.getClase() + 
				" AND I.CLASE_INCOMP = " + polizaComprobar.getClase() + " AND I.CODMODULO IN " + 
				StringUtils.toValoresSeparadosXComas(listaCodModulos, true) + " AND I.CODMODULO_INCOMP IN " + 
				StringUtils.toValoresSeparadosXComas(listaCodModulosIncompatibles, true);
		//SISTEMA DE CULTIVO
		if (listaSistCult != null && listaSistCult.size() > 0){
			sql += " AND (I.CODSISTEMACULTIVO IS NULL OR I.CODSISTEMACULTIVO IN (" + 
				org.apache.commons.lang.StringUtils.join(listaSistCult.iterator(), ",") + ")) ";
		}
		//SISTEMA DE CULTIVO INCOMPATIBLE
		if (listaSistCultIncompatibles != null && listaSistCultIncompatibles.size() > 0){
			sql += "AND (I.CODSISTEMACULTIVO_INCOMP IS NULL OR I.CODSISTEMACULTIVO_INCOMP IN (" + 
				org.apache.commons.lang.StringUtils.join(listaSistCultIncompatibles.iterator(), ",") + "))";
		}
		
		logger.debug("Consulta de incompatibilidades: " + sql);
		List listaIncompatibilidades = session.createSQLQuery(sql).list();
		return listaIncompatibilidades;
	} 
	
		
	/**
	* Guardo los modulos de las polizas a comprobar
	*/	
	private List<String> traerCodModulos(Poliza polizaComprobar) {
		List<String> listaCodModulos = new ArrayList<String> ();
		if (polizaComprobar != null){
			listaCodModulos.add(polizaComprobar.getCodmodulo());
		}
		listaCodModulos.add("99999");
		return listaCodModulos;
	}
	
	/**
	* Guardo los sistemas de cultivo de todas las parcelas de las polizas a comprobar
	*/
	private List<BigDecimal> traerSistCult(Poliza polizaComprobar) {
		List<BigDecimal> listaSistCult= new ArrayList<BigDecimal> ();
		if (polizaComprobar != null){
			for (Parcela p : polizaComprobar.getParcelas()){
				for (CapitalAsegurado c : p.getCapitalAsegurados()){
					for(DatoVariableParcela d : c.getDatoVariableParcelas()){
						DiccionarioDatos dic = d.getDiccionarioDatos();
						if(dic.getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_SISTCULTIVO))){
							BigDecimal valor = new BigDecimal(d.getValor());
							if (!listaSistCult.contains(valor))
								listaSistCult.add(valor);
						} 
					}
				}
			}			
		}
		return listaSistCult;
	}

	/**
	 * Comprueba si las polizas de una lista consultando tb_incompatibilidad_clases
	 */
	public boolean isCompatible(List<Poliza> listPolizas)throws DAOException{
		boolean result = true;
		
		try {
			for(int i = 0; i < listPolizas.size(); i++ ){
				Poliza poliza = listPolizas.get(i);
				result = isCompatible(poliza);
			}
		}catch(Exception ex){
			logger.error(ex);
		    throw new DAOException("[IncompatibilidadClaseDao] isCompatible() - error ",ex);
		}
		
		return result;
	}
	
}
