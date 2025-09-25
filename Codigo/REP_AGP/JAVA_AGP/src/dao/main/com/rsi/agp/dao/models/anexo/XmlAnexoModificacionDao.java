package com.rsi.agp.dao.models.anexo;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.Session;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.lob.LobHandler;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;

public class XmlAnexoModificacionDao extends BaseDaoHibernate implements IXmlAnexoModificacionDao {
	
	private DataSource dataSource;
	private LobHandler lobHandler;

	@Override
	public Map<BigDecimal, List<String>> getDatosVariablesParcelaRiesgo(AnexoModificacion am) throws BusinessException {
		
		/* ESC-12885 ** MODIF TAM (11.03.2021) ** Inicio */
		logger.debug( "XmlAnexoModificacionDao - getDatosVariablesParcelaRiesgo [INIT]");
		/* Se produce error al recuperar los datos Variables del riesgo en Anexos de pólizas con muchas parcelas */
		/* Se implementa el mismo metodo pero en Java*/
		/*String procedure = "PQ_DATOS_VARIABLES_RIESGO.getDatVarParcelaModifRiesgo (IDANEXOPARAM IN NUMBER) RETURN VARCHAR2";
		//Establecemos los parÃ¡metros para llamar al PL
		Map<String, Object> parametros = new HashMap<String, Object>();*/

		/*parametros.put("IDANEXOPARAM", am.getId());
		
		logger.debug("Llamada al procedimiento PQ_DATOS_VARIABLES_RIESGO.getDatVarParcelaModifRiesgo con los siguientes parÃ¡metros: ");
		logger.debug("    IDANEXOPARAM: " + am.getId());*/
		
		//Ejecutamos el PL. El resultado estÃ¡ en la Clave del Map RESULT
		/*Map<String, Object> resultado = databaseManager.executeStoreProc(procedure, parametros);
		String strDatVar2 = (String) resultado.get("RESULT");
		logger.debug("Valor de strDatVar(forma antigua:"+strDatVar2);*/
		
		logger.debug( "XmlAnexoModificacionDao - Antes de llamar a la nueva función getDatVarParcelaModifRiesgoJavaImpl");
		logger.debug( "XmlAnexoModificacionDao - Valor de IdAnexo:"+am.getId());
		String strDatVar = getDatVarParcelaModifRiesgoJavaImpl(am.getId());
		logger.debug("Valor de strDatVar(forma nueva:"+strDatVar);
		
		//Montamos un mapa indexado por cÃ³digo de concepto y con una lista de string con cada 
		//combinaciÃ³n CAPITAL_ASEGURADO#CONCEPTO_PPAL_MOD#RIESGO_CUBIERTO#VALOR
		/*String strDatVar = (String) resultado.get("RESULT");*/
		/* ESC-12885 ** MODIF TAM (11.03.2021) ** Fin */
		
		//SIGPE-9015
		// Se llama al método que obtiene la combinación "CAPITAL_ASEGURADO#CONCEPTO_PPAL_MOD#RIESGO_CUBIERTO#VALOR"
		// para los códigos de concepto 120 (% FRANQUICIA) Y 121 (% MINIMO INDEMNIZABLE)
		// como ya se venía haciendo para el ciclo de contratación póliza
		strDatVar = strDatVar + getDatosVariablesParcelaRiesgoEspeciales(am);
		
		Map<BigDecimal, List<String>> lstDatVar = new HashMap<BigDecimal, List<String>>();
		
		if (!StringUtils.nullToString(strDatVar).equals("")){
			for (String concepto : strDatVar.split("\\|")){
				String[] cod_valor = concepto.split(":");
				if (cod_valor.length == 2)
					lstDatVar.put(new BigDecimal(cod_valor[0]), Arrays.asList(cod_valor[1].split(";")));
			}
		}
		
		logger.debug( "XmlAnexoModificacionDao - getDatosVariablesParcelaRiesgo [END]");
		logger.debug ("Valor de lstDatVar:"+lstDatVar);
		return lstDatVar;
		
		
	}
	
	/* ESC-12885 ** MODIF TAM (11.03.2021) ** Inicio **/
	@SuppressWarnings("rawtypes")
	/* Se duplica en JAva la funcionalidad del PL PQ_DATOS_VARIABLES_RIESGO.getDatVarParcelaModifRiesgo*/
	@Override
	public String getDatVarParcelaModifRiesgoJavaImpl(Long idAnx) throws BusinessException {
		
		String resultado = "";
		Session session = obtenerSession();
		
		BigDecimal lineaseguroId = new BigDecimal(0);
		String codmodulo = "";
		String consulta = "";
		
		List registros = new ArrayList();
		/*Consulta del 'lineaseguroid' para la póliza indicada como parámetro*/
		String sql = "SELECT PO.LINEASEGUROID, nvl(A.CODMODULO, PO.CODMODULO) " + 
							 " FROM TB_ANEXO_MOD A, TB_POLIZAS PO " + 
							 " WHERE A.IDPOLIZA = PO.IDPOLIZA AND A.ID = " + idAnx;
		
		registros = session.createSQLQuery(sql).list();
		
		if(registros.size()>0){
			Object[] paramsGen =null;
			paramsGen = (Object[]) registros.get(0);
			lineaseguroId =((BigDecimal)paramsGen[0]);
			codmodulo = ((String)paramsGen[1]);
		}
		
		String sqlConcepto = "SELECT OI.CODCONCEPTO FROM TB_SC_OI_ORG_INFO OI "+
						     " WHERE OI.LINEASEGUROID = " + lineaseguroId +
						     " AND OI.CODUSO = 31 AND OI.CODUBICACION = 16 " +
						     "AND OI.CODCONCEPTO IN (134, 135, 136, 137, 138, 139, 140)";
			
		
		List listConcepto = session.createSQLQuery(sqlConcepto).list();
		for (int i = 0; i < listConcepto.size(); i++){
			BigDecimal codconcepto = (BigDecimal)listConcepto.get(i);
			String sqlWhere = "";
			
			if (codconcepto.equals(new BigDecimal(ConstantsConceptos.CODCPTO_DIAS_INI_GARANT))) { /*140*/
				//DIAS INICIO GARANTIAS
				sqlWhere = "DV.CODCONCEPTO = 140 AND p.numdiasdesde = DV.VALOR";
			} else if (codconcepto.equals(new BigDecimal(ConstantsConceptos.CODCPTO_MES_DUR_MAX_GARANT))) { /*137*/
				//DURACION MAX.GARAN(MESES)
		        sqlWhere = "DV.CODCONCEPTO = 137 AND p.nummeseshasta = DV.VALOR";
			} else if (codconcepto.equals(new BigDecimal(ConstantsConceptos.CODCPTO_DIAS_DUR_MAX_GARANT))) { /*136*/
				//DURACION MAX.GARANT(DIAS)
		        sqlWhere = "DV.CODCONCEPTO = 136 AND p.numdiashasta = DV.VALOR";			
		    } else if (codconcepto.equals(new BigDecimal(ConstantsConceptos.CODCPTO_FEC_FIN_GARANT))) { /* 134 */
		        //FECHA FIN GARANTIAS	
		        sqlWhere = "DV.CODCONCEPTO = 134 AND TO_CHAR(p.fgaranthasta, 'DD/MM/YYYY') = dv.valor";
			} else if (codconcepto.equals(new BigDecimal(ConstantsConceptos.CODCPTO_FEC_INI_GARANT))) { /*138*/
				//FECHA INICIO GARANTIAS
		         sqlWhere = "DV.CODCONCEPTO = 138 AND TO_CHAR(p.fgarantdesde, 'DD/MM/YYYY') = dv.valor";
			} else if (codconcepto.equals(new BigDecimal(ConstantsConceptos.CODCPTO_EST_FENOLOGICO_F_GARANT))) { /*135*/
		        //EST.FEN.FIN GARANTIAS
		        sqlWhere = "DV.CODCONCEPTO = 135 AND p.codestfenhasta = DV.VALOR";
			} else if (codconcepto.equals(new BigDecimal(ConstantsConceptos.CODCPTO_EST_FENOLOGICO_I_GARANT))) { /*139*/
		        //EST.FEN.INICIO GARANTIAS
		        sqlWhere = "DV.CODCONCEPTO = 135 AND p.codestfendesde = DV.VALOR";
			}
			
			
		    consulta = " select ca.id, dv.valor, p.codconceptoppalmod, p.codriesgocubierto" +
		      		   " FROM  o02agpe0.tb_anexo_mod a, " +
		      		         " o02agpe0.tb_anexo_mod_parcelas par, " +
		      		         " o02agpe0.tb_anexo_mod_capitales_aseg ca, " +
		      		         " o02agpe0.tb_sc_c_riesgo_cbrto_mod r, " +
		      		         " o02agpe0.Tb_Sc_c_Fecha_Fin_Garantia p, " +
		      		         " o02agpe0.Tb_Anexo_Mod_Capitales_Dts_Vbl DV " +
		      		   " WHERE ca.idparcelaanexo = par.id and p.lineaseguroid = r.lineaseguroid AND r.codmodulo = p.codmodulo" +
		                 " AND r.codriesgocubierto = p.codriesgocubierto AND r.codconceptoppalmod = p.codconceptoppalmod"+
		                 " AND a.id = " +idAnx +" and par.idanexo = a.id"+ 
		                 " AND r.lineaseguroid = " +  lineaseguroId +
		      		     " AND r.codmodulo = '" + codmodulo + "' AND p.codcultivo = par.codcultivo"+
		      		     " AND (p.codvariedad = par.codvariedad OR p.codvariedad = 999)"+
		      		     " AND (p.codprovincia = par.codprovincia or P.CODPROVINCIA = 99)"+
		      		     " AND (p.codcomarca = par.codcomarca or P.CODCOMARCA = 99)"+
		      		     " AND (p.codtermino = par.codtermino or P.CODTERMINO = 999)"+
		      		     " AND (p.subtermino = par.subtermino or P.SUBTERMINO = '9')"+
		      	 	     " AND p.codtipocapital = ca.codtipocapital AND r.elegible = 'N'"+
		                 " AND DV.IDCAPITALASEGURADO = CA.Id " +
		      		     " AND " + sqlWhere +
		          " union " +
		            " select ca.id, dv.valor, p.codconceptoppalmod, p.codriesgocubierto" +
		      		   " FROM  o02agpe0.tb_anexo_mod a, " +
		                     " o02agpe0.tb_anexo_mod_parcelas par, " +
		                     " o02agpe0.tb_anexo_mod_coberturas cob, " +
		                     " o02agpe0.tb_anexo_mod_capitales_aseg ca, " +
		                     " o02agpe0.tb_sc_c_riesgo_cbrto_mod r, " +
		                     " o02agpe0.Tb_Sc_c_Fecha_Fin_Garantia p," +
		                     " o02agpe0.Tb_Anexo_Mod_Capitales_Dts_Vbl DV" +
				       " WHERE ca.idparcelaanexo = par.id and p.lineaseguroid = r.lineaseguroid AND r.codmodulo = p.codmodulo" +
		                 " AND r.codriesgocubierto = p.codriesgocubierto AND r.codconceptoppalmod = p.codconceptoppalmod" +
		                 " AND a.id = " + idAnx + " and par.idanexo = a.id and cob.idanexo = a.id" +
		                 " AND r.lineaseguroid = " + lineaseguroId +
		                 " AND r.codmodulo = '" + codmodulo + "' AND p.codcultivo = par.codcultivo" +
		                 " AND (p.codvariedad = par.codvariedad OR p.codvariedad = 999)" +
		                 " AND (p.codprovincia = par.codprovincia or P.CODPROVINCIA = 99)" +
		                 " AND (p.codcomarca = par.codcomarca or P.CODCOMARCA = 99)" +
		                 " AND (p.codtermino = par.codtermino or P.CODTERMINO = 999)" +
		                 " AND (p.subtermino = par.subtermino or P.SUBTERMINO = '9')" +
		                 " AND p.codtipocapital = ca.codtipocapital AND r.elegible = 'S' AND cob.codconcepto = 363 AND cob.codriesgocubierto = r.codriesgocubierto" +
		                 " AND (cob.tipomodificacion is null OR cob.tipomodificacion = 'A' OR (cob.tipomodificacion = 'M' AND cob.codvalor = -1))" +
		                 " AND DV.IDCAPITALASEGURADO = CA.Id AND " + sqlWhere +
		         " union " +
		            " SELECT ca.id, dv.valor, p.codconceptoppalmod, p.codriesgocubierto" +
		      		   " FROM  o02agpe0.tb_anexo_mod a," +
		                     " o02agpe0.tb_anexo_mod_parcelas par," +
		                     " o02agpe0.tb_anexo_mod_capitales_aseg ca," +
		                     " o02agpe0.Tb_Anexo_Mod_Capitales_Dts_Vbl dv," +
		                     " o02agpe0.tb_sc_c_fecha_fin_garantia p," +
		                     " o02agpe0.tb_sc_c_riesgo_cbrto_mod r "+
					   " WHERE a.id = par.idanexo" +
		                 " AND par.id = ca.idparcelaanexo " +
		                 " AND ca.id = dv.idcapitalasegurado " +
		                 " AND (p.codvariedad = par.codvariedad OR p.codvariedad = 999)" +
		                 " AND p.codcultivo = par.codcultivo" +
		                 " AND ca.codtipocapital = p.codtipocapital" +
		                 " AND r.codriesgocubierto = p.codriesgocubierto " +
		                 " AND r.codconceptoppalmod = p.codconceptoppalmod " +
		                 " AND r.codmodulo = '" + codmodulo + "'" +
		                 " AND r.lineaseguroid = "+ lineaseguroId +
		                 " AND a.id = " +idAnx +
		                 " AND DV.CODCONCEPTO = 134" +
		                 " AND r.elegible = 'S' "+
		                 " AND (SELECT count(*) " +
		                        " FROM o02agpe0.tb_anexo_mod a1," +
		                        "      o02agpe0.tb_anexo_mod_parcelas par1, "+ 
		                        "      o02agpe0.tb_anexo_mod_capitales_aseg ca1, " +
		                        "      o02agpe0.Tb_Anexo_Mod_Capitales_Dts_Vbl dv1 "+ 
		                        " WHERE a1.id = par1.idanexo "+
		                          " AND par1.id = ca1.idparcelaanexo "+
		                          " AND ca1.id = dv1.idcapitalasegurado "+
		                          " AND dv1.valor = '-1' "+
		                          " AND a1.id = " +idAnx + 
		                          " AND ca1.id = ca.id" +
		                          " AND DV1.CODCONCEPTO = 363) > 0" +
		            " group by ca.id, dv.valor, p.codconceptoppalmod, p.codriesgocubierto";
	
		    logger.debug("Consulta para obtener los datos variables que dependen del riesgo: " + consulta);
		
		   List lista = session.createSQLQuery(consulta).list();
		   
		   resultado += codconcepto + ":";
		
		   for (int j = 0; j < lista.size(); j++){
				Object[] registro = (Object[]) lista.get(j);
				resultado += registro[0] + "#" + registro[2] + "#" + registro[3] + "#" + registro[1] + ";";
		   }
			
		}
	
		resultado +=  "|";
		return resultado;
		
		
	}
	/* ESC-12885 ** MODIF TAM (11.03.2021) ** Fin **/
		
	@Override
	public Map<BigDecimal, List<String>> getDatosVariablesParcelaRiesgoAnexoCPL(Long idAnx, String moduloPPal) throws BusinessException {
		GregorianCalendar gcI = new GregorianCalendar();
		Map<String, Object> resultado = null;
		String procedure = "PQ_DATOS_VARIABLES_RIESGO.getDatVarParcelaModifCPL(IDANEXOPARAM IN NUMBER,CODMODULOPARAM IN VARCHAR2) RETURN VARCHAR2";
		//Establecemos los parÃ¡metros para llamar al PL
		Map<String, Object> parametros = new HashMap<String, Object>();

		parametros.put("IDANEXOPARAM", idAnx);
		parametros.put("CODMODULOPARAM",moduloPPal);
		
		//Ejecutamos el PL. El resultado estÃ¡ en la Clave del Map RESULT
		resultado = databaseManager.executeStoreProc(procedure, parametros);
		
		//Montamos un mapa indexado por cÃ³digo de concepto y con una lista de string con cada 
		//combinaciÃ³n CAPITAL_ASEGURADO#CONCEPTO_PPAL_MOD#RIESGO_CUBIERTO#VALOR
		String strDatVar = (String) resultado.get("RESULT");
		Map<BigDecimal, List<String>> lstDatVar = new HashMap<BigDecimal, List<String>>();
		if (!StringUtils.nullToString(strDatVar).equals("")){
			for (String concepto : strDatVar.split("\\|")){
				String[] cod_valor = concepto.split(":");
				if (cod_valor.length == 2)
					lstDatVar.put(new BigDecimal(cod_valor[0]), Arrays.asList(cod_valor[1].split(";")));
			}
		}
		
		GregorianCalendar gcF = new GregorianCalendar();
		Long tiempo = gcF.getTimeInMillis() - gcI.getTimeInMillis();
		logger.debug("Tiempo de la llamada a PQ_DATOS_VARIABLES_RIESGO.getDatVarParcelaModifCPL: " + tiempo + " milisegundos");
		
		return lstDatVar;
	}
	
	@Override
	public Map<BigDecimal, List<String>> getDatosVariablesParcela(AnexoModificacion am) throws BusinessException {
		/* ESC-12885 ** MODIF TAM (11.03.2021) ** Inicio */
		logger.debug( "XmlAnexoModificacionDao - getDatosVariablesParcela [INIT]");
		/* Se produce error al recuperar los datos Variables del riesgo en Anexos de pólizas con muchas parcelas */
		/* Se implementa el mismo metodo pero en Java*/
		/*String procedure = "PQ_DATOS_VARIABLES_RIESGO.getDatVarParcelaModifRiesgo (IDANEXOPARAM IN NUMBER) RETURN VARCHAR2";
		//Establecemos los parÃ¡metros para llamar al PL
		/*Map<String, Object> parametros = new HashMap<String, Object>();

		parametros.put("IDANEXOPARAM", am.getId());
		
		logger.debug("Llamada al procedimiento PQ_DATOS_VARIABLES_RIESGO.getDatVarParcelaModifRiesgo con los siguientes parÃ¡metros: ");
		logger.debug("    IDANEXOPARAM: " + am.getId());
		
		//Ejecutamos el PL. El resultado estÃ¡ en la Clave del Map RESULT
		Map<String, Object> resultado = databaseManager.executeStoreProc(procedure, parametros);*/
		
		logger.debug( "XmlAnexoModificacionDao - Antes de llamar a la nueva función getDatVarParcelaModifRiesgoJavaImpl");
		logger.debug( "XmlAnexoModificacionDao - Valor de IdAnexo:"+am.getId());
		String strDatVar = getDatVarParcelaModifRiesgoJavaImpl(am.getId());
		
		//Montamos un mapa indexado por cÃ³digo de concepto y con una lista de string con cada 
		//combinaciÃ³n CAPITAL_ASEGURADO#CONCEPTO_PPAL_MOD#RIESGO_CUBIERTO#VALOR
		/*String strDatVar = (String) resultado.get("RESULT");*/
		/* ESC-12885 ** MODIF TAM (11.03.2021) ** Fin */
		
		//SIGPE-9015
		// Se llama al método que obtiene la combinación "CAPITAL_ASEGURADO#CONCEPTO_PPAL_MOD#RIESGO_CUBIERTO#VALOR"
		// para los códigos de concepto 120 (% FRANQUICIA) Y 121 (% MINIMO INDEMNIZABLE)
		// como ya se venía haciendo para el ciclo de contratación póliza
		//strDatVar = strDatVar + getDatosVariablesParcelaRiesgoEspeciales(am);
		
		Map<BigDecimal, List<String>> lstDatVar = new HashMap<BigDecimal, List<String>>();
		
		if (!StringUtils.nullToString(strDatVar).equals("")){
			for (String concepto : strDatVar.split("\\|")){
				String[] cod_valor = concepto.split(":");
				if (cod_valor.length == 2)
					lstDatVar.put(new BigDecimal(cod_valor[0]), Arrays.asList(cod_valor[1].split(";")));
			}
		}
		
		logger.debug( "XmlAnexoModificacionDao - getDatosVariablesParcela [END]");
		logger.debug("Valor de lstDatVar:"+lstDatVar);
		return lstDatVar;
	}

	@Override
	public void saveXmlAnexoModificacion(Long idAnexo, final String xml)
			throws DAOException {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
	    jdbcTemplate.update("UPDATE TB_ANEXO_MOD SET XML=? WHERE ID=" + idAnexo, 
	    					new PreparedStatementSetter() {
	      public void setValues(PreparedStatement ps) throws SQLException {
	         lobHandler.getLobCreator().setClobAsString(ps, 1, xml);
	      }
	    });
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setLobHandler(LobHandler lobHandler) {
		this.lobHandler = lobHandler;
	}
	
	/**
	 * Obtener los datos variables asociados a los conceptos 120 y 121.
	 * @author U029114 19/07/2017
	 * @param am
	 * @return String
	 */
	@SuppressWarnings("rawtypes")
	private String getDatosVariablesParcelaRiesgoEspeciales (AnexoModificacion am) {
		
		String resultado = "";
		Session session = obtenerSession();
		try {
			String sqlConceptoEsp = "SELECT OI.CODCONCEPTO FROM o02agpe0.TB_SC_OI_ORG_INFO OI WHERE OI.LINEASEGUROID = "
					+ am.getPoliza().getLinea().getLineaseguroid()	+ " AND OI.CODUSO = 31 AND OI.CODUBICACION = 16 AND OI.CODCONCEPTO IN (120, 121)";
			
			logger.debug("**@@** Dentro de getDatosVAriablesParcelaRiesgoEspeciales-XmlAnexoModificacionDao"); 
			String codmodulo = "P"; 

			List listConceptoEsp = session.createSQLQuery(sqlConceptoEsp).list();
			
			for (int i = 0; i < listConceptoEsp.size(); i++) {
				// Código de concepto a procesar
				BigDecimal codconcepto = (BigDecimal) listConceptoEsp.get(i);
				
				String consulta = "";
				
				/* Pet. 63497 (REQ.04) ** MODIF TAM (14/04/2020) ** Inicio */
				/* Incluimos la validación para que no se incluyan las combinaciones de %Franquicia t %Minimo Indemnizable que no son elegibles */
				/* Para los anexos de Modificación de polizas Principales cuyo modulo es 'P' */
				if( (am.getPoliza().getCodmodulo() != null && am.getPoliza().getCodmodulo().equals(codmodulo) ) &&  
						(!Constants.MODULO_POLIZA_COMPLEMENTARIO.equals(am.getPoliza().getTipoReferencia()))){
					
					consulta = "SELECT CA_ANEX.ID, DV_ANEX.VALOR, R.CODCONCEPTOPPALMOD, R.CODRIESGOCUBIERTO "
							+ "FROM o02agpe0.TB_ANEXO_MOD_PARCELAS PAR_ANEX, "
							+ "o02agpe0.TB_ANEXO_MOD_CAPITALES_ASEG CA_ANEX, "
							+ "o02agpe0.TB_ANEXO_MOD_CAPITALES_DTS_VBL DV_ANEX, "
							+ "o02agpe0.TB_SC_C_RIESGO_CBRTO_MOD R, "
							+ "o02agpe0.TB_SC_C_VINC_VALORES_MOD V, "
							+ "o02agpe0.TB_SC_C_CARACT_MODULO CM " 
							+ "WHERE PAR_ANEX.ID = CA_ANEX.IDPARCELAANEXO "
							+ "AND CA_ANEX.ID = DV_ANEX.IDCAPITALASEGURADO "
							+ "AND R.LINEASEGUROID = V.LINEASEGUROID "
							+ "AND R.CODMODULO = V.CODMODULO "
							+ "AND R.FILAMODULO = V.FILAMODULO "
							+ "AND CM.FILAMODULO = R.FILAMODULO "  
							+ "AND CM.LINEASEGUROID = R.LINEASEGUROID " 
						    + "AND CM.CODMODULO = R.CODMODULO " 
						    + "AND CM.COLUMNAMODULO = V.COLUMNAMODULO " 
							+ "AND R.LINEASEGUROID = " + am.getPoliza().getLinea().getLineaseguroid() + " "
							+ "AND R.CODMODULO = '" + am.getCodmodulo() + "' "
							+ "AND R.ELEGIBLE = 'S' "
							+ "AND R.NIVELECCION = 'D' "
							+ "AND PAR_ANEX.IDANEXO = " + am.getId() + " "
							+ "AND DV_ANEX.CODCONCEPTO = " + codconcepto+ " " 
							+ "AND CM.TIPOVALOR = 'E' "; 
				}else {
					
					consulta = "SELECT CA_ANEX.ID, DV_ANEX.VALOR, R.CODCONCEPTOPPALMOD, R.CODRIESGOCUBIERTO "
							+ "FROM TB_ANEXO_MOD_PARCELAS              PAR_ANEX, "
							+ "TB_ANEXO_MOD_CAPITALES_ASEG  CA_ANEX, "
							+ "TB_ANEXO_MOD_CAPITALES_DTS_VBL     DV_ANEX, "
							+ "TB_SC_C_RIESGO_CBRTO_MOD R, "
							+ "TB_SC_C_VINC_VALORES_MOD V "
							+ "WHERE PAR_ANEX.ID = CA_ANEX.IDPARCELAANEXO "
							+ "AND CA_ANEX.ID = DV_ANEX.IDCAPITALASEGURADO "
							+ "AND R.LINEASEGUROID = V.LINEASEGUROID "
							+ "AND R.CODMODULO = V.CODMODULO "
							+ "AND R.FILAMODULO = V.FILAMODULO "
							+ "AND R.LINEASEGUROID = " + am.getPoliza().getLinea().getLineaseguroid() + " "
							+ "AND R.CODMODULO = '" + am.getCodmodulo() + "' "
							+ "AND R.ELEGIBLE = 'S' "
							+ "AND R.NIVELECCION = 'D' "
							+ "AND PAR_ANEX.IDANEXO = " + am.getId() + " "
							+ "AND DV_ANEX.CODCONCEPTO = " + codconcepto;
				}

				if (codconcepto.compareTo(new BigDecimal(120)) == 0) {
					// % FRANQUICIA
					consulta += " AND V.CODPCTFRANQUICIAELEG = DV_ANEX.VALOR";
				} else if (codconcepto.compareTo(new BigDecimal(121)) == 0) {
					// % MINIMO INDEMNIZABLE
					consulta += " AND V.PCTMININDEMNELEG = DV_ANEX.VALOR";
				}

				logger.debug("Consulta para obtener los datos variables que dependen del riesgo los conceptos 120 y 121: "
						+ consulta);

				List lista = session.createSQLQuery(consulta).list();

				resultado += codconcepto + ":";

				for (int j = 0; j < lista.size(); j++) {
					Object[] registro = (Object[]) lista.get(j);
					resultado += registro[0] + "#" + registro[2] + "#"
							+ registro[3] + "#" + registro[1] + ";";
				}
				resultado += "|";

			}
		} catch (Exception e) {
			logger.error("Error al obtener los datos variables asociados a los conceptos 120 y 121", e);
		}
		
		return resultado;
	}

}
