package com.rsi.agp.dao.models.cpm;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.CPMTipoCapitalFilter;
import com.rsi.agp.core.jmesa.sort.CPMTipoCapitalSort;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cpm.CPMTipoCapital;
import com.rsi.agp.dao.tables.cpm.CPMTipoCapitalItem;

public class CPMTipoCapitalDAO extends BaseDaoHibernate implements ICPMTipoCapitalDAO {
	
	@Override
	//DAA 19/07/2012
	public List<BigDecimal> getCPMDePolizaAnexoMod(Long idPoliza, Long idAnexo, String codModulo)	throws DAOException {
		return (codModulo == null) ? (new ArrayList<BigDecimal> ()) : (getCPM (null, idPoliza, idAnexo, true, codModulo));
	}
	
	@Override
	public List<BigDecimal> getCPMDePoliza(Long codtipoCapital, Long idPoliza, String codModulo) throws DAOException {
		return getCPM (codtipoCapital, idPoliza, null, false, codModulo);
	}
	
	@Override
	public boolean isCPMPermitido(CPMTipoCapital cpmTipoCapital) {
		// Llama al metodo que obtiene el numero de registros de la tabla que se ajustan a los criterios de busqueda y 
		// devuelve false en el caso de que sea 0 y true en caso contrario
		try {
			return (getCountCPM(cpmTipoCapital)>0);
		} catch (DAOException e) {
			logger.debug("Se ha producido un error durante la consulta para obtener numero de registros de CPM que se ajustan a la busqueda");
			return false;
		}
	}
	
	/**
	 * Devuelve el listado de CPM permitidos para una pliza y sus parcelas asociadas (de la poliza o de anexo)
	 * configurados en la tabla TB_CPM_TIPO_CAPITAL
	 * @param codtipoCapital
	 * @param idPoliza
	 * @param idAnexo
	 * @param isAnexo Indica si se comprobaran las parcelas del AM (true) o de la poliza principal (false)
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private List<BigDecimal> getCPM(Long codtipoCapital, Long idPoliza, Long idAnexo, boolean isAnexo, String codModulo) throws DAOException {
		
		
		logger.debug ("**@@** Dentro de getCPM");
		// Listas para resultados parciales
		List<Object[]> resultado_sist_cult = new ArrayList<Object[]>();
		List<Object[]> resultado_f_fin_garantia = new ArrayList<Object[]>();
		List<Object[]> resultado_sin_dv = new ArrayList<Object[]>();
		List<Object[]> resultado_ciclo_cultivo = new ArrayList<Object[]>();
		
		// Lista de CPM registrados en TB_CPM_TIPO_CAPITAL para los datos encontrados en las bï¿½squedas parciales
		List<BigDecimal> resultado = new ArrayList<BigDecimal>();				
		
		Session session = obtenerSession();
		
		// Busqueda para concepto 'Sistema de cultivo'
		try {									
			// Se lanza una consulta u otra dependiendo si se hace el join por parcelas de poliza o de anexo de modificacion	
			logger.debug ("**@@** Antes de obtener el sistema de cultivos");
			
			resultado_sist_cult = session.createSQLQuery(isAnexo ? (getSQLBusquedasParcialesAnexo(idPoliza, idAnexo, ConstantsConceptos.CODCPTO_SISTCULTIVO, codModulo, false)) : (getSQLBusquedasParciales(codtipoCapital, idPoliza, ConstantsConceptos.CODCPTO_SISTCULTIVO, codModulo, false))).list();
		} catch (Exception ex) {
			logger.error("Se ha producido un error al hacer la consulta para el concepto 'Sistema de cultivo'");
			logger.error("Parámetros de la consulta - idPoliza:" + idPoliza + " , idAnexo: " + idAnexo +  " , codConcepto:" + ConstantsConceptos.CODCPTO_SISTCULTIVO);
			logger.error(ex);
			throw new DAOException("Se ha producido un error al hacer la consulta para el concepto 'Sistema de cultivo'",ex);
		}
			
		// Busqueda para concepto 'Fecha fin de garantia'	
		try {
			logger.debug ("**@@** Antes de obtener la fecha fin garantias");
			// Se lanza una consulta u otra dependiendo si se hace el join por parcelas de poliza o de anexo de modificacion
			resultado_f_fin_garantia = session.createSQLQuery(isAnexo ? (getSQLBusquedasParcialesAnexo(idPoliza, idAnexo, ConstantsConceptos.CODCPTO_FEC_FIN_GARANT, codModulo, false)) : (getSQLBusquedasParciales(codtipoCapital, idPoliza, ConstantsConceptos.CODCPTO_FEC_FIN_GARANT, codModulo, false))).list();
		} catch (Exception ex) {
			logger.error("Se ha producido un error al hacer la consulta para el concepto 'Fecha fin de garantia'");
			logger.error("Parámetros de la consulta - idPoliza:" + idPoliza + " , idAnexo: " + idAnexo +  " , codConcepto:" + ConstantsConceptos.CODCPTO_FEC_FIN_GARANT);
			logger.error(ex);
			throw new DAOException("Se ha producido un error al hacer la consulta para el concepto 'Fecha fin de garantia'",ex);
		}
		// Busqueda para concepto 'ciclo cultivo'	
		try {
			logger.debug ("**@@** Antes de obtener el ciclo de cultivo");
			// Se lanza una consulta u otra dependiendo si se hace el join por parcelas de poliza o de anexo de modificacion
			resultado_ciclo_cultivo = session.createSQLQuery(isAnexo ? (getSQLBusquedasParcialesAnexo(idPoliza, idAnexo, ConstantsConceptos.CODCPTO_CICLOCULTIVO, codModulo, false)) : (getSQLBusquedasParciales(codtipoCapital, idPoliza, ConstantsConceptos.CODCPTO_CICLOCULTIVO, codModulo, false))).list();
		} catch (Exception ex) {
			logger.error("Se ha producido un error al hacer la consulta para el concepto 'ciclo cultivo'");
			logger.error("Parametros de la consulta - idPoliza:" + idPoliza + " , idAnexo: " + idAnexo +  " , codConcepto:" + ConstantsConceptos.CODCPTO_CICLOCULTIVO);
			logger.error(ex);
			throw new DAOException("Se ha producido un error al hacer la consulta para el concepto 'ciclo cultivo'",ex);
		}
			
		// Se repite la busqueda sin filtrar por datos variables
		try {
			logger.debug ("**@@** Antes de obtener los datos variables");
			resultado_sin_dv = session.createSQLQuery(isAnexo ? (getSQLBusquedasParcialesAnexo(idPoliza, idAnexo, null, codModulo, true)) : (getSQLBusquedasParciales(codtipoCapital, idPoliza, null, codModulo, true))).list();
		} catch (Exception e) {
			logger.error("Se ha producido un error al hacer la consulta sin filtrar por datos variables");
			throw new DAOException("Se ha producido un error al hacer la consulta sin tener en cuenta los datos variables", e);
		}
				
		try {
			logger.debug ("**@@** Antes de consultar TB_CPM_TIPO_CAPITAL");
			// Consulta a TB_CPM_TIPO_CAPITAL para obtener los CPM con las condiciones indicadas por las bï¿½squedas parciales
			resultado = getCPMTipoCapital (resultado_sist_cult, resultado_f_fin_garantia, resultado_sin_dv, session,resultado_ciclo_cultivo);															
		
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("Se ha producido un error al hacer la consulta sobre TB_CPM_TIPO_CAPITAL",ex);
		}
		
		return resultado;
	}

	/**
	 * Genera la consulta SQL para obtener los valores necesarios para montar la condicion de la consulta sobre TB_CPM_TIPO_CAPITAL
	 * a partir de los datos de la poliza y sus parcelas
	 * @param codtipoCapital
	 * @param idPoliza
	 * @param concepto
	 * @param sinDV Indica si se realiza la consulta sin filtrar por datos variables o no
	 * @return
	 */
	private String getSQLBusquedasParciales(Long codtipoCapital, Long idPoliza, final Integer concepto, String codModulo, boolean sinDV) {
				
		String select = "SELECT DISTINCT P.LINEASEGUROID, MP.CODMODULO, CA.CODTIPOCAPITAL, PAR.CODCULTIVO, " + ((sinDV) ? "'NULL' " : "DVP.VALOR ") +
								"FROM O02AGPE0.TB_POLIZAS P, O02AGPE0.TB_LINEAS L , O02AGPE0.TB_PARCELAS PAR, O02AGPE0.TB_CAPITALES_ASEGURADOS CA, " + ((sinDV) ? "" : "O02AGPE0.TB_DATOS_VAR_PARCELA DVP, ") + " O02AGPE0.TB_MODULOS_POLIZA MP " +
								"WHERE P.LINEASEGUROID = L.LINEASEGUROID " +
								"AND PAR.IDPOLIZA = P.IDPOLIZA  " +
								"AND PAR.IDPARCELA = CA.IDPARCELA " +
								((sinDV) ? "" : "AND DVP.IDCAPITALASEGURADO = CA.IDCAPITALASEGURADO ") +
								"AND MP.IDPOLIZA = P.IDPOLIZA " +
								"AND MP.CODMODULO = '" + codModulo + "' " +
								((sinDV) ? "" : "AND DVP.CODCONCEPTO = " + concepto + " ") +
								"AND P.IDPOLIZA=" + idPoliza;
		
		if (codtipoCapital != null) {
			select = select + "AND CA.CODTIPOCAPITAL=" + codtipoCapital;
		}
		
		return select;		
	}
	
	/** DAA 19/07/2012
	 * Genera la consulta SQL para obtener los valores necesarios para montar la condicion de la consulta sobre TB_CPM_TIPO_CAPITAL
	 * a partir de los datos de la poliza de las parcelas del anexo indicado
	 * @param idPoliza
	 * @param idAnexo
	 * @param concepto
	 * @param sinDV Indica si se realiza la consulta sin filtrar por datos variables o no
	 * @return
	 */
	private String getSQLBusquedasParcialesAnexo(Long idPoliza, Long idAnexo, final Integer concepto, String codModulo, boolean sinDV) {
	
		String query = "SELECT DISTINCT P.LINEASEGUROID, AM.CODMODULO, ACA.CODTIPOCAPITAL, APAR.CODCULTIVO, " + ((sinDV) ? "'NULL' " : "ADVP.VALOR ") +
		   				"FROM O02AGPE0.TB_POLIZAS P, O02AGPE0.TB_LINEAS L , O02AGPE0.TB_ANEXO_MOD AM, O02AGPE0.Tb_Anexo_Mod_Parcelas APAR," +  
		   					"O02AGPE0.TB_ANEXO_MOD_CAPITALES_ASEG ACA " + ((sinDV) ? "" : ",O02AGPE0.TB_ANEXO_MOD_CAPITALES_DTS_VBL ADVP ") + 
		   				"WHERE P.LINEASEGUROID = L.LINEASEGUROID " +
			   				"AND APAR.IDANEXO = AM.ID " +
			   				"AND AM.IDPOLIZA = P.IDPOLIZA " +
			   				"AND APAR.ID =ACA.IDPARCELAANEXO " +
			   				((sinDV) ? "" : "AND ADVP.IDCAPITALASEGURADO = ACA.ID ") +
			   				"AND AM.CODMODULO = '" + codModulo + "' " +
			   				((sinDV) ? "" : "AND ADVP.CODCONCEPTO = " + concepto + " ") + 
			   				"AND P.IDPOLIZA = " + idPoliza + " " + 
			   				"AND AM.ID = " + idAnexo;
 
		return query;		 
	}
	
	/**
	 * Devuelve las condiciones del where para buscar en la tabla TB_CPM_TIPO_CAPITAL los registros asociados a los datos de las listas
	 * pasadas como parï¿½metro
	 * @param resultado_sist_cult
	 * @param resultado_f_fin_garantia
	 * @return
	 */
	private List<BigDecimal> getCPMTipoCapital (List<Object[]> resultado_sist_cult, List<Object[]> resultado_f_fin_garantia, 
												List<Object[]> resultado_sin_dv, Session session,List<Object[]> resultado_ciclo_cultivo) {
		
		List<BigDecimal> listaCPM = new ArrayList<BigDecimal>();						
		
		// Where correspondiente al concepto 'Sistema cultivo'
		for (Object[] o: resultado_sist_cult) {										
			cargarListaCPM(session, listaCPM, o, true, false,false);							
		}		
		
		// Where correspondiente al concepto 'Fecha fin de garantia'
		for (Object[] o : resultado_f_fin_garantia) {
			cargarListaCPM(session, listaCPM, o, false, false,false);
		}
		
		// Where sin filtrar por datos variables
		for (Object[] o : resultado_sin_dv) {
			cargarListaCPM(session, listaCPM, o, false, true,false);
		}
		// Where correspondiente al concepto 'Ciclo cultivo'
		for (Object[] o: resultado_ciclo_cultivo) {										
			cargarListaCPM(session, listaCPM, o, false, false,true);							
		}		
				
		return listaCPM;
	}

	/**
	 * @param session
	 * @param listaCPM
	 * @param isSC Indica si se filtra por sistema de cultivo o por fecha de fin de garantia
	 * @param sinDV Si este parametro es null no se filtra por ninguno de los dos datos variables
	 * @param o
	 */
	@SuppressWarnings("unchecked")
	private void cargarListaCPM(Session session, List<BigDecimal> listaCPM,	Object[] o, 
			boolean isSC, boolean sinDV,boolean isCicloCult) {
		String select = "SELECT C.CODCONCEPTOPPALMOD FROM O02AGPE0.TB_CPM_TIPO_CAPITAL C ";
		// Se consulta sobre la tabla TB_CPM_TIPO_CAPITAL
		// Para el cultivo y el modulo, se filtrara por el valor indicado o el correspondiente a todos los valores
		List<BigDecimal> aux = session.createSQLQuery(select + 
				componeWhere(o[0], o[1], o[2], o[3], (isSC && !sinDV && !isCicloCult) ? o[4] : null, 
						(!isSC && !sinDV && !isCicloCult) ? o[4] : null ,(!isSC && !sinDV && isCicloCult) ? o[4] : null)).list();		
		
		// Se aÃ±ade la lista auxiliar a la principal evitando los registros repetidos
		if (aux.size() != 0){
			// No se insertan los registros de aux que ya estan en listaCPM
			for (BigDecimal bigDecimal : aux) {
				if (!listaCPM.contains(bigDecimal)) listaCPM.add(bigDecimal);
			}												
		}
	}
	
	/**
	 * Compone el where para la consulta sobre TB_CPM_TIPO_CAPITAL dependiendo de los parametros del metodo
	 * @param lineaseguroid
	 * @param codModulo
	 * @param codTC
	 * @param codCultivo
	 * @param codSC
	 * @param ffg
	 * @return
	 */
	private String componeWhere (Object lineaseguroid, Object codModulo, 
			Object codTC, Object codCultivo, Object codSC, Object ffg, Object codCicloCultivo) {
		
		logger.debug("componeWhere - lineaseguroid: " + StringUtils.nullToString(lineaseguroid));
		logger.debug("componeWhere - codModulo: " + StringUtils.nullToString(codModulo) + ", " + Constants.TODOS_MODULOS);
		logger.debug("componeWhere - codTC: " + StringUtils.nullToString(codTC));
		logger.debug("componeWhere - codCultivo: " + StringUtils.nullToString(codCultivo) + ", " + Constants.TODOS_CULTIVOS);
		logger.debug("componeWhere - codCicloCultivo: " + StringUtils.nullToString(codCicloCultivo) );
		
		
		String query = " WHERE C.LINEASEGUROID= " + lineaseguroid.toString() + 
				   " AND C.CODMODULO IN ('" + codModulo.toString() + "','" + Constants.TODOS_MODULOS + "')" +
				   " AND C.CODTIPOCAPITAL=" + codTC.toString() + 
				   " AND C.CODCULTIVO IN (" + codCultivo.toString() + "," + Constants.TODOS_CULTIVOS + ")" + 
				   " AND (C.CODSISTEMACULTIVO IS NULL " + (codSC != null ? ("OR C.CODSISTEMACULTIVO=" + codSC.toString()) : "") + ") " +
				   " AND (C.FECHAFINGARANTIA IS NULL " + (ffg != null ? ("OR C.FECHAFINGARANTIA= TO_DATE('" + ffg + "','DD/MM/YYYY')") : "") + ")" +
				   " AND (C.CODCICLOCULTIVO IS NULL "+ (codCicloCultivo!= null ? ("OR C.CODCICLOCULTIVO =" +codCicloCultivo.toString()):"")+")";
		
						
		logger.debug("Consulta en TB_CPM_TIPO_CAPITAL");
		logger.debug(query);
		
		return query;
	}
	
	/**
	 * Obtiene el numero de registros de la tabla que se ajustan a los criterios de busqueda
	 * @param cpmTipoCapital
	 * @return
	 * @throws DAOException 
	 */
	private int getCountCPM (CPMTipoCapital cpmTC) throws DAOException {
		Session sesion = obtenerSession();
		try {
			String sqlQuery = "SELECT COUNT(*) FROM TB_CPM_TIPO_CAPITAL C WHERE";
			// AÃ±ade los criterios de busqueda
			sqlQuery += " C.LINEASEGUROID = " + cpmTC.getCultivo().getLinea().getLineaseguroid();
			sqlQuery += " AND C.CODMODULO IN " + StringUtils.toValoresSeparadosXComas(cpmTC.getListaModulos(),true);
			sqlQuery += " AND C.CODCONCEPTOPPALMOD = " + cpmTC.getConceptoPpalModulo().getCodconceptoppalmod();
			sqlQuery += " AND C.CODCULTIVO IN " + StringUtils.toValoresSeparadosXComas(cpmTC.getListaCultivos(),false);
			
			sqlQuery += (cpmTC.getListaTiposCapital() != null && cpmTC.getListaTiposCapital().size()>0) 
						? " AND C.CODTIPOCAPITAL IN " + StringUtils.toValoresSeparadosXComas(cpmTC.getListaTiposCapital(),false): "";
			sqlQuery += (cpmTC.getListaCicloCultivos() != null && cpmTC.getListaCicloCultivos().size()>0) 
					? " AND (C.CODCICLOCULTIVO IN " + StringUtils.toValoresSeparadosXComas(cpmTC.getListaCicloCultivos(),false) 
							+ " OR C.CODCICLOCULTIVO IS NULL)" : "" ;
			
			// Lanza la consulta y devuelve el valor del count
			return ((BigDecimal)sesion.createSQLQuery (sqlQuery).list().get(0)).intValue();
			
		} catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}

	@Override
	public int getConsultaCPMTipoCapitalCountWithFilter(final CPMTipoCapitalFilter filter) {
		logger.debug("init - [CPMTipoCapitalDao] getConsultaCPMTipoCapitalCountWithFilter");
		
		Integer count = (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria criteria = session.createCriteria(CPMTipoCapital.class);
						// Alias
						criteria.createAlias("cultivo", "cultivo");
						criteria.createAlias("cultivo.linea", "lin");
						criteria.createAlias("conceptoPpalModulo", "conceptoPpalModulo");
						criteria.createAlias("tipoCapital", "tipoCapital");
						//criteria.createAlias("cicloCultivo", "cicloCultivo", CriteriaSpecification.LEFT_JOIN);
						
						// Filtro
						criteria = filter.execute(criteria);
						criteria.setProjection(Projections.rowCount()).uniqueResult();
						return criteria.uniqueResult();
					}
				});
		logger.debug("end - [CPMTipoCapitalDao] getConsultaCPMTipoCapitalCountWithFilter");
		return count.intValue();
		
	}

	@Override
	@SuppressWarnings("all")
	public Collection<CPMTipoCapital> getCPMTipoCapitalWithFilterAndSort(
			final CPMTipoCapitalFilter filter,final CPMTipoCapitalSort sort,final int rowStart,
			final int rowEnd) throws BusinessException {
		try {
			logger.debug("init - [CPMTipoCapitalDao] getCPMTipoCapitalWithFilterAndSort");
			List<CPMTipoCapital> applications = (List) getHibernateTemplate().execute(new HibernateCallback() {

						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							Criteria criteria = session.createCriteria(CPMTipoCapital.class);
							// Alias
							criteria.createAlias("cultivo", "cultivo");
							criteria.createAlias("cultivo.linea", "lin");
							criteria.createAlias("conceptoPpalModulo", "conceptoPpalModulo");
							criteria.createAlias("tipoCapital", "tipoCapital");
							criteria.createAlias("cicloCultivo", "cicloCultivo", CriteriaSpecification.LEFT_JOIN);
							// Filtro
							criteria = filter.execute(criteria);
							// Ordenacion
							criteria = sort.execute(criteria);
							// Primer registro
							criteria.setFirstResult(rowStart);
							// Numero maximo de registros a mostrar
							criteria.setMaxResults(rowEnd - rowStart);
							List<CPMTipoCapital> lista = criteria.list();
							
							ArrayList<CPMTipoCapitalItem> listaItem = new ArrayList<CPMTipoCapitalItem>();
							for (CPMTipoCapital cpmTipoCapital : lista){
								listaItem.add(new CPMTipoCapitalItem(cpmTipoCapital));
							}
							return listaItem;
						}
					});
			logger.debug("end - [CPMTipoCapitalDao] getCPMTipoCapitalWithFilterAndSort");
			return applications;
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	@Override
	public String replicar(BigDecimal origen, BigDecimal destino) throws DAOException {
		
		int resultado=0;
		
		try {			
			String procedimiento = "O02AGPE0.PQ_REPLICAR.replicarCPMTipoCapital (LINEASEGUROID_DESTINO IN NUMBER, LINEASEGUROID_ORIGEN IN NUMBER, P_RESULT OUT NUMBER)";
			
			Map<String, Object> parametros = new HashMap<String, Object>(); // parï¿½metros PL
			parametros.put("LINEASEGUROID_DESTINO", destino);
			parametros.put("LINEASEGUROID_ORIGEN", origen);
			parametros.put("P_RESULT", resultado);
		
			parametros = databaseManager.executeStoreProc(procedimiento, parametros); // ejecutamos PL
			return parametros.get("P_RESULT").toString();
			
		} catch (Exception e) {
			logger.error("Error al replicar CPMTipoCapital ",e);
			throw new DAOException("Error al replicar CPMTipoCapital ", e);
		}        
		
	}

	@SuppressWarnings("rawtypes")
	public boolean numRegDestinoIgualNumRegOrigen(Long lineaSeguroIdDestino, Long lineaSeguroIdOrigen) {
		
		boolean numregIguales = true;
		Session session = obtenerSession();
		
		String sql = "select count(*) from O02AGPE0.tb_cpm_tipo_capital t where t.lineaseguroid in ("+
					lineaSeguroIdDestino+","+lineaSeguroIdOrigen+ ") group by t.lineaseguroid";
		List list = session.createSQLQuery(sql).list();
		
		BigDecimal reg1 = new BigDecimal(list.get(0).toString());
		BigDecimal reg2 = new BigDecimal(list.get(1).toString());
		
		if(reg1.compareTo(reg2)!= 0)
			numregIguales = false;
		
		return numregIguales;
	}

	@SuppressWarnings("rawtypes")
	public Long getCPMTipoCapital(CPMTipoCapital cpmTipoCapital) {
		
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(CPMTipoCapital.class);
		List lista = new ArrayList();
		
		criteria.createAlias("linea", "linea");
		criteria.createAlias("conceptoPpalModulo", "conceptoPpalModulo");
		criteria.createAlias("tipoCapital", "tipoCapital");
		
        if (null != cpmTipoCapital.getCultivo().getLinea()){
        	if (null != cpmTipoCapital.getCultivo().getLinea().getCodlinea()){
        		criteria.add(Restrictions.eq("linea.codlinea",cpmTipoCapital.getCultivo().getLinea().getCodlinea()));
        	}
        	if (null != cpmTipoCapital.getCultivo().getLinea().getCodplan()){
        		criteria.add(Restrictions.eq("linea.codplan",cpmTipoCapital.getCultivo().getLinea().getCodplan()));
        	}
        }
        if (null != cpmTipoCapital.getConceptoPpalModulo()){
        	if (null != cpmTipoCapital.getConceptoPpalModulo().getCodconceptoppalmod()){
        		criteria.add(Restrictions.eq("conceptoPpalModulo.codconceptoppalmod", cpmTipoCapital.getConceptoPpalModulo().getCodconceptoppalmod()));
        	}
        }
		if (null != cpmTipoCapital.getTipoCapital()){
			if(null != cpmTipoCapital.getTipoCapital().getCodtipocapital()){
				criteria.add(Restrictions.eq("tipoCapital.codtipocapital", cpmTipoCapital.getTipoCapital().getCodtipocapital()));
			}
		}
		
		lista = criteria.list();
		session.flush();
		
		if(lista.size()>0){
			session.evict((CPMTipoCapital) lista.get(0));
			session.clear();
			if (lista.size()>0){
				CPMTipoCapital c =(CPMTipoCapital) lista.get(0); 
				return c.getId();
			}			
		}	
		return null;
			
	}

}
