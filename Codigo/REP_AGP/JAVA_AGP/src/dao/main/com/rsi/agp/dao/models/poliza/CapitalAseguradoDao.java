package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.properties.SortOrderEnum;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.filters.poliza.CapitalAseguradoFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.poliza.CapAsegRelModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.DatosTotalesCapital;
import com.rsi.agp.dao.tables.poliza.TipoCapitalComparativa;
import com.rsi.agp.pagination.PageProperties;
import com.rsi.agp.pagination.PaginatedListImpl;

@SuppressWarnings("unchecked")
public class CapitalAseguradoDao extends BaseDaoHibernate implements ICapitalAseguradoDao {
	
	private static final Log LOGGER = LogFactory.getLog(CapitalAseguradoDao.class);
	private final static String VACIO = "";
	
	
	public List<CapAsegRelModulo> listCapAsegRelModuloByIdCapAseg(Long idCapitalAsegurado) throws DAOException {
		try {
			return findFiltered(CapAsegRelModulo.class,"capitalAsegurado.idcapitalasegurado", idCapitalAsegurado);
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos",e);
		}
	}
	
	
	public void deleteCapAsegRelModById(Long id) throws DAOException {
		try {
			delete(CapAsegRelModulo.class, id);
		} catch (DAOException e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos",e);
		}
	}

	
	@Override
	public PaginatedListImpl<CapitalAsegurado> getPaginatedListCapitalesAsegurados(PageProperties pageProperties,CapitalAseguradoFiltro capitalAseguradoFiltro) throws DAOException {
		
		PaginatedListImpl<CapitalAsegurado> paginatedListImpl = new PaginatedListImpl<CapitalAsegurado>();
		List<CapitalAsegurado> listCapitalesAsegurados = null;
		try {

			listCapitalesAsegurados = getPageCapitalesAsegurados(pageProperties, capitalAseguradoFiltro);

			paginatedListImpl.setFullListSize(pageProperties.getFullListSize());
			paginatedListImpl.setObjectsPerPage(pageProperties.getPageSize());
			paginatedListImpl.setPageNumber(pageProperties.getPageNumber());
			paginatedListImpl.setList(listCapitalesAsegurados);
			paginatedListImpl.setSortCriterion(pageProperties.getSort());
			if(pageProperties.getDir().equals("asc")){
				paginatedListImpl.setSortDirection(SortOrderEnum.ASCENDING);
			}else if(pageProperties.getDir().equals("desc")){
				paginatedListImpl.setSortDirection(SortOrderEnum.DESCENDING);
			}
			
		} catch (Exception e) {
			LOGGER.error("Se ha producido un error durante el acceso a la base de datos ",e);
		}

		return paginatedListImpl;
	}
	
	
	/** DAA 04/12/2012
	 *	Obtiene la lista de Capitales asegurados ordenados.  
	 * @param pageProperties
	 * @param capitalAseguradoFiltro
	 * @return criteria.list()
	 */	
	public List<CapitalAsegurado> getPageCapitalesAsegurados(PageProperties pageProperties,CapitalAseguradoFiltro capitalAseguradoFiltro) {
		Session session = obtenerSession(); 
		
		Criteria criteria = capitalAseguradoFiltro.getCriteria(session);
		
		String columna = pageProperties.getSort();
		String orden = pageProperties.getDir();
		
		Map<String, String> columnas = new HashMap<String, String>();
			columnas.put("parcelaHoja","p.hoja#p.numero");
			columnas.put("codProvSigpac","p.poligono#p.parcela");
			columnas.put("sigPac","p.codprovsigpac#p.codtermsigpac#p.agrsigpac#p.zonasigpac#p.poligonosigpac#p.parcelasigpac#p.recintosigpac#p.tipoparcela");
			columnas.put("desTipoCapital","tipo.destipocapital");
			columnas.put("superficie","superficie");
			columnas.put("produccionInt","capAsegRelModulos.produccion");
			columnas.put("precioInt","capAsegRelModulos.precio");
		
		//ordenacion por defecto por hoja y numero
		if(VACIO.equals(columna) && VACIO.equals(orden)){
			criteria.addOrder(Order.asc("p.hoja"));
			criteria.addOrder(Order.asc("p.numero"));
		}
		else{
			columna = columnas.get(columna);
			String[] columnaOrden = columna.split("#");
			
			//añadimos los alias
			if(("tipo.destipocapital").equals(columna)){
				criteria.createAlias("tipoCapital", "tipo");
			}
			if(("capAsegRelModulos.produccion").equals(columna) || ("capAsegRelModulos.precio").equals(columna) ){
				criteria.createAlias("capAsegRelModulos", "capAsegRelModulos");
			}
			
			//ordenacion
			if(("desc").equals(orden)){
				for(int i=0; i<columnaOrden.length; i++){
					criteria.addOrder(Order.desc(columnaOrden[i]));
				}
			}
			else{
				for(int i=0; i<columnaOrden.length; i++){
					criteria.addOrder(Order.asc(columnaOrden[i]));
				}
			}			
		}
		
		criteria.setFirstResult(pageProperties.getIndexRowMin());
		criteria.setMaxResults(pageProperties.getPageSize());
		
		return criteria.list();
	}

	
	@SuppressWarnings("rawtypes")
	public List getTiposCapitalbyPoliza(Long idPoliza,BigDecimal hoja) throws DAOException {
		Session session = obtenerSession();
		List list = new ArrayList();
		String sql ="select * from o02agpe0.tb_sc_c_tipo_capital where codtipocapital in (SELECT distinct(ca.codtipocapital) "+
	    			"FROM TB_PARCELAS PA, TB_CAPITALES_ASEGURADOS CA "+
	    			"WHERE CA.IDPARCELA = PA.IDPARCELA "+
	    			"AND PA.IDPOLIZA = " + idPoliza + 
	    			"AND PA.HOJA = " + hoja +")";
		
		if(session.createSQLQuery(sql).list().size() > 0){
			return session.createSQLQuery(sql).list();
		}
		return list;
	}
	
	public List<Object[]> getTiposCapitalParcelasComparativa(Long idPoliza) throws DAOException {
		Session session = obtenerSession();
		List<Object[]> list = new ArrayList<Object[]>();
		String sql ="select * from o02agpe0.tb_sc_c_tipo_capital where codtipocapital in (SELECT distinct(ca.codtipocapital) "+
	    			"FROM TB_PARCELAS PA, TB_CAPITALES_ASEGURADOS CA "+
	    			"WHERE CA.IDPARCELA = PA.IDPARCELA "+
	    			"AND PA.IDPOLIZA = " + idPoliza +")"; 
		
		if(session.createSQLQuery(sql).list().size() > 0){
			list = session.createSQLQuery(sql).list();
		}
		return list;
	}
	
	public List<Object[]> getTiposCapitalExplotacionesComparativa(Long idPoliza) throws DAOException {
		Session session = obtenerSession();
		List<Object[]> list = new ArrayList<Object[]>();
		String sql ="select * from o02agpe0.tb_sc_c_tipo_capital where codtipocapital in (SELECT distinct(GR.codtipocapital)  " + 
				" FROM TB_EXPLOTACIONES EX, TB_GRUPO_RAZA_EXPLOTACION GR  " + 
				" WHERE GR.IDEXPLOTACION = EX.ID " + 
				" AND EX.IDPOLIZA = " + idPoliza +")"; 
		
		if(session.createSQLQuery(sql).list().size() > 0){
			list = session.createSQLQuery(sql).list();
		}
		return list;
	}
	
	public DatosTotalesCapital getTotalPlantones(Long idPoliza, BigDecimal tipoCapital,String descripcion,BigDecimal hoja,BigDecimal codConcepto) throws DAOException{
		Session session = obtenerSession();
		DatosTotalesCapital datosTotalesCapital = null;
		String sql = "SELECT SUM (REL.PRODUCCION) AS TOTALPRODUCCION,"+
				      " SUM (REL.Precio * dv.valor) AS totalvalorasegurable,"+
				      " SUM (CA.SUPERFICIE) AS TOTALSUPERFICIE"+
				      " FROM TB_PARCELAS PA, TB_CAPITALES_ASEGURADOS CA, TB_SC_C_TIPO_CAPITAL TC,TB_CAP_ASEG_REL_MODULO REL, tb_datos_var_parcela dv"+
				      " WHERE CA.IDPARCELA = PA.IDPARCELA"+
				      " AND CA.CODTIPOCAPITAL = TC.CODTIPOCAPITAL"+
				      " AND CA.IDCAPITALASEGURADO = REL.IDCAPITALASEGURADO"+
				      " AND ca.idcapitalasegurado = dv.idcapitalasegurado"+
				      " AND CA.CODTIPOCAPITAL = "+tipoCapital+
				      " AND PA.IDPOLIZA = " + idPoliza +
				      " AND PA.HOJA = " + hoja+
				      " AND dv.codconcepto = "+codConcepto;
		
		if(session.createSQLQuery(sql).list().size() > 0){
			
			datosTotalesCapital = new DatosTotalesCapital();
			datosTotalesCapital.setDescripcion(descripcion);
			datosTotalesCapital.setHoja(hoja);
			Object[] res = (Object[]) session.createSQLQuery(sql).list().get(0);
			datosTotalesCapital.setTotalProduccion((BigDecimal) res[0]);
			datosTotalesCapital.setTotalValorAsegurable((BigDecimal) res[1]);
			datosTotalesCapital.setTotalSuperficie((BigDecimal) res[2]);
			
		}
		return datosTotalesCapital;
	}

	
	public DatosTotalesCapital getTotalProduccion(Long idPoliza, BigDecimal tipoCapital, String descripcion,BigDecimal hoja)throws DAOException {
		DatosTotalesCapital datosTotalesCapital = null;
		Session session = obtenerSession();
		String sql ="SELECT SUM (NVL(REL.Produccionmodif,REL.PRODUCCION)) AS TOTALPRODUCCION,"+
				    " SUM (NVL(REL.Preciomodif * REL.Produccionmodif,REL.PRECIO * REL.PRODUCCION)) AS totalvalorasegurable,"+
					" SUM (CA.SUPERFICIE) AS TOTALSUPERFICIE"+
					" FROM TB_PARCELAS PA, TB_CAPITALES_ASEGURADOS CA, TB_SC_C_TIPO_CAPITAL TC,TB_CAP_ASEG_REL_MODULO REL"+
					" WHERE CA.IDPARCELA = PA.IDPARCELA"+
					" AND CA.CODTIPOCAPITAL = TC.CODTIPOCAPITAL"+
					" AND CA.IDCAPITALASEGURADO = REL.IDCAPITALASEGURADO"+
					" AND CA.CODTIPOCAPITAL = "+tipoCapital+
					" AND PA.HOJA = " + hoja +
					" AND PA.IDPOLIZA =" + idPoliza;
		
		if(session.createSQLQuery(sql).list().size() > 0){
			
			datosTotalesCapital = new DatosTotalesCapital();
			datosTotalesCapital.setDescripcion(descripcion);
			datosTotalesCapital.setHoja(hoja);
			Object[] res = (Object[]) session.createSQLQuery(sql).list().get(0);
			datosTotalesCapital.setTotalProduccion((BigDecimal) res[0]);
			datosTotalesCapital.setTotalValorAsegurable((BigDecimal) res[1]);
			datosTotalesCapital.setTotalSuperficie((BigDecimal) res[2]);
		}
		return datosTotalesCapital;
	}
	
	public List<Object[]> getTiposCapitalGrupoNegocioComparativa(Long idPoliza, String tipoCapital)throws DAOException {
		List<Object[]> list = new ArrayList<Object[]>();
		Session session = obtenerSession();

		/*String sql = "SELECT SUM(VALORCONCEPTO) AS PRODUCCION," + 
				"  SUM(VALORCONCEPTO * exg.PRECIO) AS VALORPRODUCCION," + 
				"  SUM(exg.COSTETOMADOR)," + 
				"  (CASE " + 
				"  		WHEN SUM(VALORCONCEPTO) <> 0 THEN ROUND(SUM(exg.COSTETOMADOR) / SUM(VALORCONCEPTO),4)" + 
				"       ELSE 0" + 
				"  END) AS COSTEUNITARIO," + 
				" '1'  as CODMODULO," + 
				" exg.GRUPO_NEGOCIO " + 
				" FROM  VW_INF_EXPLOTACIONES_GANADO exg" + 
				" WHERE exg.IDPOLIZA = "+ idPoliza +
				" AND tipocapital = '" + tipoCapital + "' " +
				" GROUP BY exg.GRUPO_NEGOCIO";*/
		String sql = " SELECT SUM(VALORCONCEPTO) AS PRODUCCION, " +
				" 		SUM(VALORCONCEPTO * PRECIO) AS VALORPRODUCCION, " +
				" 		SUM(COSTETOMADOR), " +
				"    	(CASE   " +
				" 			WHEN SUM(VALORCONCEPTO) <> 0 THEN ROUND(SUM(COSTETOMADOR) / SUM(VALORCONCEPTO),4) " +
				" 			ELSE 0 " +
				"      	END) AS COSTEUNITARIO,  " +
				"      	'1'  AS CODMODULO, " + 
				"  		GRUPO_NEGOCIO, " +
				" 		idcomparativa " +
				" FROM ( " +
				" 	SELECT e.id AS id, " +
				" 		e.idpoliza AS idpoliza, " +
				" 		(gr.codtipocapital || ' - ' || ctc.destipocapital) AS TipoCapital, " +
				//" 		(gr.codtipoanimal || ' - ' || cta.descripcion) AS TipoAnimal, " +
				" 		gr.numanimales AS valorConcepto, " +
				" 		pam.precio AS precio, " +
				" 		dcex.COSTETOMADOR AS CosteTomador, " +
				" 		dc.grupo_negocio AS grupo_negocio, " +
				" 		'NUMERO ANIMALES' AS nomConcepto, " +
				" 		idcomparativa " +
				" 	FROM O02AGPE0.tb_polizas p " +
				" 	INNER JOIN O02AGPE0.tb_explotaciones e ON p.idpoliza = e.idpoliza " +
				" 	INNER JOIN O02AGPE0.tb_grupo_raza_explotacion gr ON e.id = gr.idexplotacion " +
				" 	LEFT OUTER JOIN O02AGPE0.tb_sc_c_tipo_capital ctc ON gr.codtipocapital = ctc.codtipocapital " +
				//" 	LEFT OUTER JOIN O02AGPE0.tb_sc_c_tipos_animal_ganado cta ON p.lineaseguroid = cta.lineaseguroid " +
				//"		AND gr.codtipoanimal = cta.codtipoanimal " +
				" 	LEFT OUTER JOIN O02AGPE0.tb_precios_animales_modulos pam ON gr.id = pam.idgruporaza " +
				" 	INNER JOIN o02agpe0.TB_SC_C_TIPO_CAPITAL_GRUPO_NEG tg ON tg.codtipocapital = gr.codtipocapital " +
				"		AND tg.codconcepto = 1097 " +
				" 	LEFT OUTER JOIN O02AGPE0.TB_DISTRIBUCION_COSTES_2015 dc ON e.IDPOLIZA = dc.IDPOLIZA " +
				"		AND dc.grupo_negocio = tg.grupo_negocio " +
				" 	LEFT OUTER JOIN O02AGPE0.TB_DIST_COSTE_EXPLOTACIONES dcex ON dc.ID = dcex.IDDISTCOSTE " +
				"		AND e.NUMERO = dcex.NUMEXPLOTACION " +
				" 		AND gr.CODGRUPORAZA = dcex.GRUPORAZA " +
				" 		AND gr.CODTIPOCAPITAL = dcex.TIPOCAPITAL " +
				" 		AND gr.CODTIPOANIMAL = dcex.TIPOANIMAL " +
				"   WHERE p.idpoliza =  " + idPoliza +
				" UNION  " +
				" 	SELECT  " +
				" 		e.id AS id,  " +
				" 		e.idpoliza AS idpoliza,  " +
				" 		(gr.codtipocapital || ' - ' || ctc.destipocapital) AS TipoCapital, " +
				//" 		(gr.codtipoanimal || ' - ' || cta.descripcion) AS TipoAnimal, " +
				" 		TO_NUMBER(ve.valor) AS valorConcepto, " +
				" 		pam.precio AS precio, " +
				" 		dcex.COSTETOMADOR AS CosteTomador,  " +
				" 		dc.grupo_negocio AS grupo_negocio, " +
				" 		d.nomconcepto AS nomConcepto, " +
				"    idcomparativa " +
				" 	FROM O02AGPE0.tb_polizas p" +
				" 	INNER JOIN O02AGPE0.tb_explotaciones e ON p.idpoliza = e.idpoliza " +
				" 	INNER JOIN O02AGPE0.tb_grupo_raza_explotacion gr ON e.id = gr.idexplotacion " +
				" 	INNER JOIN o02agpe0.tb_datos_var_explotacion ve ON gr.id = ve.idgruporaza " +
				" 	INNER JOIN o02agpe0.tb_sc_dd_dic_datos d ON ve.codconcepto = d.codconcepto " +
				" 	LEFT OUTER JOIN O02AGPE0.tb_sc_c_tipo_capital ctc ON gr.codtipocapital = ctc.codtipocapital " +
				//" 	LEFT OUTER JOIN O02AGPE0.tb_sc_c_tipos_animal_ganado cta ON p.lineaseguroid = cta.lineaseguroid " +
				//"		AND gr.codtipoanimal = cta.codtipoanimal " +
				" 	LEFT OUTER JOIN O02AGPE0.tb_precios_animales_modulos pam ON gr.id = pam.idgruporaza " +
				" 	INNER JOIN o02agpe0.TB_SC_C_TIPO_CAPITAL_GRUPO_NEG tg ON tg.codtipocapital = gr.codtipocapital " +
				" 	LEFT OUTER JOIN O02AGPE0.TB_DISTRIBUCION_COSTES_2015 dc ON e.IDPOLIZA = dc.IDPOLIZA " +
				"		AND dc.grupo_negocio = tg.grupo_negocio " +
				" 	LEFT OUTER JOIN O02AGPE0.TB_DIST_COSTE_EXPLOTACIONES dcex ON dc.ID = dcex.IDDISTCOSTE " +
				"		AND e.NUMERO = dcex.NUMEXPLOTACION " +
				"		AND gr.CODGRUPORAZA = dcex.GRUPORAZA AND gr.CODTIPOCAPITAL = dcex.TIPOCAPITAL " +
				"		AND gr.CODTIPOANIMAL = dcex.TIPOANIMAL " +
				" 	WHERE (ve.codconcepto = 1076 " +
				"		OR ve.codconcepto = 1065 " +
				"		OR ve.codconcepto = 1071 " +
				"		OR ve.codconcepto = 1072) " +
				"    AND p.idpoliza =  " + idPoliza +
				"  ) " +
				" WHERE IDPOLIZA =  " + idPoliza +
				" AND TipoCapital = '" + tipoCapital +  "' " +
				" GROUP BY TIPOCAPITAL,idcomparativa,GRUPO_NEGOCIO ";
		logger.debug(sql);
		if(session.createSQLQuery(sql).list().size() > 0){
			list = session.createSQLQuery(sql).list();
		}
		return list;
		
	}
	public List<Object[]> getTiposCapitalModulosComparativa(Long idPoliza, BigDecimal tipoCapital)throws DAOException {
		
		List<Object[]> list = new ArrayList<Object[]>();
		Session session = obtenerSession();
		
		String sql = "SELECT SUM(PRODUCCION) AS PRODUCCION, " + 
				" SUM(capital_asegurado) AS valorProduccion, " + 
				" SUM(dpar.costetomador) AS costeTomador, "  + 
				" (CASE" + 
				"   WHEN SUM(PRODUCCION) <> 0 THEN ROUND(SUM(dpar.costetomador) / SUM(PRODUCCION),4)" + 
				"   ELSE 0" + 
				" END ) AS costeUnitario, "  + 
				" CODMODULO,  "  +
				" GRUPO_NEGOCIO, " +
				" IDCOMPARATIVA " +
				" FROM TB_DIST_COSTE_PARCELAS_2015 dpar  "  + 
				" INNER JOIN tb_distribucion_costes_2015 dc on dc.id =  dpar.iddistcoste "  + 
				" WHERE dc.idpoliza = "+ idPoliza + 
				" AND tipo = " + tipoCapital  + 
				" GROUP BY CODMODULO, GRUPO_NEGOCIO, IDCOMPARATIVA "  + 
				" ORDER BY CODMODULO";

		if(session.createSQLQuery(sql).list().size() > 0){
			list = session.createSQLQuery(sql).list();
		}
		return list;
	}
	
	/** DAA 11/09/2013
	 * 	recoge la lista de capitales asegurados de una poliza
	 * @throws DAOException 
	 */
    public List<CapitalAsegurado> getListCapitalesAsegurados(Long idPoliza) throws DAOException{
    	try{
    		Session session = obtenerSession(); 
    		Criteria criteria = session.createCriteria(CapitalAsegurado.class);
    		criteria.createAlias("parcela", "par");
    		criteria.createAlias("par.poliza", "pol");
    		criteria.add(Restrictions.eq("pol.idpoliza", idPoliza));
    		List<CapitalAsegurado> lstCapAseg = criteria.list();
    		return lstCapAseg;
    		
	    } catch (Exception e) {
	    	throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
    }	
    
    public void actualizarTipoRdto(Long idCapitalAsegurado, Long valor, Long prod) throws DAOException{
    	Session session = obtenerSession();
		try {
			String sql= "update TB_CAP_ASEG_REL_MODULO ca set TIPO_RDTO='"+valor+"', PRODUCCION='"+prod+"' where ca.ID ='"+idCapitalAsegurado+"'";
			
			session.createSQLQuery(sql).executeUpdate();
			
			String sql2= "update tb_capitales_asegurados  set PRODUCCION='"+prod+"' where IDCAPITALASEGURADO= '"+ idCapitalAsegurado +"'";
			session.createSQLQuery(sql2).executeUpdate();
			
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
			
		}
		
    }
    
    public void actualizarTipoRdtoAnexo(Long idCapitalAsegurado, Long valor,Long prod, Long idparcelaAnexo) throws DAOException{
    	Session session = obtenerSession();
		try {
			String sql= "update TB_ANEXO_MOD_CAPITALES_ASEG ca set TIPO_RDTO='"+valor+"'," +
					" PRODUCCION='"+prod+"', TIPOMODIFICACION='M' where ca.ID ='"+idCapitalAsegurado+"'";
			
			session.createSQLQuery(sql).executeUpdate();
			
			
			String sql2= "update Tb_Anexo_Mod_Parcelas c set TIPOMODIFICACION='M' where c.id= '"+ idparcelaAnexo +"'";
			session.createSQLQuery(sql2).executeUpdate();
			
			
			
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
			
		}
		
    }
}