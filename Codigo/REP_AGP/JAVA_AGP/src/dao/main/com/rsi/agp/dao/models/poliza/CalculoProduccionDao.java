package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.cpl.LimiteRendimientoFiltro;
import com.rsi.agp.dao.filters.poliza.MascaraLimiteRendimientoFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cpl.LimiteRendimiento;
import com.rsi.agp.dao.tables.cpl.MascaraLimiteRendimiento;
import com.rsi.agp.dao.tables.cpl.MascaraRendimientoCaracteristicaEspecifica;
import com.rsi.agp.dao.tables.cpl.RendimientoCaracteristicaEspecifica;
import com.rsi.agp.dao.tables.org.OrganizadorInformacion;
import com.rsi.agp.vo.ItemVO;
import com.rsi.agp.vo.ParcelaVO;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CalculoProduccionDao extends BaseDaoHibernate implements ICalculoProduccionDao {
	
	private static final Log logger = LogFactory.getLog(CalculoProduccionDao.class);
	
	
	@Override
	public boolean calcularConPlSql() {
		boolean conPl = false;
		Session session = obtenerSession();
		
		String sql= "select AGP_VALOR from TB_CONFIG_AGP where AGP_NEMO = 'CALCULO_PROD_PLSQL'";
		List list = session.createSQLQuery(sql).list();
		
		if (list.get(0).toString().equals("SI")){
			conPl = true;
		}
		
		return conPl;
	}

	@Override
	public boolean calcularRendimientoProdConSW() {
		boolean esConSW = false;
		Session session = obtenerSession();
		
		String sql= "select AGP_VALOR from TB_CONFIG_AGP where AGP_NEMO = 'CALCULO_PROD_SW'";
		List list = session.createSQLQuery(sql).list();
		
		if (list.size()>0 && list.get(0).toString().equals("SI")){
			esConSW = true;
		}
		
		return esConSW;
	}
	
	@Override
	public String[] getProduccionPlSql(Long lineaseguroid, Long idpoliza, List<ItemVO> datosVariablesCapAseg,
			String nifasegurado, String codmodulo, String codcultivo, String codvariedad, String codprovincia,
			String codcomarca, String codtermino, String subtermino, String sigpac) throws BusinessException {
		
		Map<String,Object> resultado = new HashMap<String, Object>();
		
		//Obtenemos los datos variables del capital asegurado y los vamos concatenando de la siguiente manera:
		// codconcepto1#valor1|codconcepto2#valor2....
		String datosVariables = getStrDatosVariables(datosVariablesCapAseg);
		
		logger.info("Inicio del calculo de produccion mediante pl/sql");
		String procedure = "PQ_CALCULA_PRODUCCION.fn_getproducciones(P_LINEASEGUROID IN VARCHAR2" +
							 ",P_IDPOLIZA IN            VARCHAR2" +
							 ",P_CAPITALESASEGURADOS IN VARCHAR2" +
							 ",P_NIF IN                 VARCHAR2" +
							 ",P_MODULO IN              VARCHAR2" +
							 ",P_CODCULTIVO IN          VARCHAR2" +
							 ",P_CODVARIEDAD IN         VARCHAR2" +
							 ",P_PROVINCIA IN           VARCHAR2" +
							 ",P_COMARCA IN             VARCHAR2" +
							 ",P_TERMINO IN             VARCHAR2" +
							 ",P_SUBTERMINO IN          VARCHAR2" +
							 ",P_SIGPAC IN              VARCHAR2 ) RETURN VARCHAR2";
		
		Map<String, Object> inParameters = new HashMap<String, Object>();
		inParameters.put("P_LINEASEGUROID", lineaseguroid.toString());
		inParameters.put("P_IDPOLIZA", idpoliza.toString());
		inParameters.put("P_CAPITALESASEGURADOS", datosVariables);
		inParameters.put("P_NIF", nifasegurado);
		inParameters.put("P_MODULO", codmodulo);
		inParameters.put("P_CODCULTIVO", codcultivo);
		inParameters.put("P_CODVARIEDAD", codvariedad);
		inParameters.put("P_PROVINCIA", codprovincia);
		inParameters.put("P_COMARCA", codcomarca);
		inParameters.put("P_TERMINO", codtermino);
		inParameters.put("P_SUBTERMINO", subtermino);
		inParameters.put("P_SIGPAC", sigpac);
		
		logger.info("Llamada al procedimiento " + procedure);
		logger.info("Con parametros:");
		logger.info("   p_lineaseguroid: "+lineaseguroid.toString());
		logger.info("   p_idpoliza: "+idpoliza.toString());
		logger.info("   p_capitalesasegurados: "+datosVariables);
		logger.info("   p_nif: "+nifasegurado);
		logger.info("   p_modulo: "+codmodulo);
		logger.info("   p_codcultivo: "+codcultivo);
		logger.info("   p_codvariedad: "+codvariedad);
		logger.info("   p_provincia: "+codprovincia);
		logger.info("   p_comarca: "+codcomarca);
		logger.info("   p_termino: "+codtermino);
		logger.info("   p_subtermino: "+subtermino);
		logger.info("   p_sigpac: "+sigpac);
		
		try {
			resultado = this.databaseManager.executeStoreProc(procedure, inParameters);
			String strProducciones = (String) resultado.get("RESULT");
			if (!StringUtils.nullToString(strProducciones).equals("")){
				return strProducciones.split("#");
			}
		} catch (Exception e) {
			logger.error("Error al obtener la produccion de la parcela.", e);
			throw new BusinessException("Error al obtener la produccion de la parcela " + e.getMessage());
		}
		
		logger.info("Fin del cálculo de produccion mediante pl/sql.");
		
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
	public Integer getOrganizadorInformacion(Long lineaseguroid) throws DAOException {
		Session session = obtenerSession();
		try {
			
			Criteria criteria = session.createCriteria(OrganizadorInformacion.class);
			criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
			criteria.add(Restrictions.eq("id.codubicacion", new BigDecimal(18)));
			criteria.add(Restrictions.eq("id.codconcepto", new BigDecimal(175)));
			criteria.add(Restrictions.eq("id.coduso", new BigDecimal(31)));
			
			criteria.setProjection(Projections.rowCount());
			
			return (Integer) criteria.uniqueResult();
			
		} catch (Exception e) {
			logger.debug("Se ha producido un error en la BBDD: " + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD",e);
		}
	}
	
	@Override
	public Integer getMedidasAplicableModulo(Long lineaseguroid,String codmodulo) throws DAOException {
		
		Session session = obtenerSession();
		
		String sql= "select count(*) from tb_sc_c_modulos_apl_medidas where " +
					"lineaseguroid = " + lineaseguroid + " and codmodulo = '" + codmodulo + "'";
		List list = session.createSQLQuery(sql).list();
		
		return ( (BigDecimal)list.get(0) ).intValue();
	}
	
	@Override
	public boolean existeLimiteRendimientoByLineaseguroid(Long lineaseguroid) {
		
		Session session = obtenerSession();
		
		String sql= "select count(*) from TB_SC_C_LIMITES_RDTOS where " +
					"lineaseguroid = " + lineaseguroid;
		List list = session.createSQLQuery(sql).list();
		
		return ( (BigDecimal)list.get(0) ).intValue() != 0;
		
	}
	
	public boolean checkRendCaracEsp(Long lineaseguroid){
		
		Session session = obtenerSession();
		
		String sql= "select count(*) from TB_SC_C_RDTOS_CARACT_ESP where " +
					"lineaseguroid = " + lineaseguroid;
		List list = session.createSQLQuery(sql).list();
		
		return ( (BigDecimal)list.get(0) ).intValue() > 0;
		
	}
	
	public List<MascaraRendimientoCaracteristicaEspecifica> getMascaraRdtosCaracEsp(Long lineaseguroid, String codmodulo,ParcelaVO parcela) {
    	logger.debug("init - [DatosParcelaFLDao] getMascaraRdtosCaracEsp");
    	List<MascaraRendimientoCaracteristicaEspecifica> mascaras = new ArrayList<MascaraRendimientoCaracteristicaEspecifica>();
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
				
				List modulos = null;
				boolean allcultivos = false;
				boolean allvariedades = false;
				boolean allprovincias = false;
				boolean allterminos = false;
				boolean allsubterminos = false;
				boolean allcomarcas = false;
				mascaras = getCriteriaMascaraRdtosCaracEsp(lineaseguroid, codmodulo, codcultivo, codvariedad,
				codprovincia, codtermino, subtermino, codcomarca, modulos, allcultivos,allvariedades,allprovincias,
				allterminos,allsubterminos,allcomarcas).list();
    	
    	if (mascaras.isEmpty()) {
    		// Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
			//	1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
			//	4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
    		allsubterminos=true;
    		mascaras = getCriteriaMascaraRdtosCaracEsp(lineaseguroid, codmodulo, codcultivo, codvariedad,
    				codprovincia, codtermino, subtermino, codcomarca, modulos, allcultivos,allvariedades,allprovincias,
    				allterminos,allsubterminos,allcomarcas).list();
    		
    		if(mascaras.isEmpty()){
    			allsubterminos=true;
    			allterminos=true;
    			mascaras = getCriteriaMascaraRdtosCaracEsp(lineaseguroid, codmodulo, codcultivo, codvariedad,
        				codprovincia, codtermino, subtermino, codcomarca, modulos, allcultivos,allvariedades,allprovincias,
        				allterminos,allsubterminos,allcomarcas).list();
    			
    			if(mascaras.isEmpty()){
    				allsubterminos=true;
        			allterminos=true;
    				allcomarcas=true;
    				mascaras = getCriteriaMascaraRdtosCaracEsp(lineaseguroid, codmodulo, codcultivo, codvariedad,
            				codprovincia, codtermino, subtermino, codcomarca, modulos, allcultivos,allvariedades,allprovincias,
            				allterminos,allsubterminos,allcomarcas).list();
    			
    				if(mascaras.isEmpty()){
    					allsubterminos=true;
            			allterminos=true;
        				allcomarcas=true;
    					allprovincias=true;
    					mascaras = getCriteriaMascaraRdtosCaracEsp(lineaseguroid, codmodulo, codcultivo, codvariedad,
                				codprovincia, codtermino, subtermino, codcomarca, modulos, allcultivos,allvariedades,allprovincias,
                				allterminos,allsubterminos,allcomarcas).list();		
    					
    					if(mascaras.isEmpty()){
    						allsubterminos=true;
                			allterminos=true;
            				allcomarcas=true;
        					allprovincias=true;
    						allvariedades=true;
    						mascaras = getCriteriaMascaraRdtosCaracEsp(lineaseguroid, codmodulo, codcultivo, codvariedad,
                    				codprovincia, codtermino, subtermino, codcomarca, modulos, allcultivos,allvariedades,allprovincias,
                    				allterminos,allsubterminos,allcomarcas).list();	
    						
    						if(mascaras.isEmpty()){
    							allsubterminos=true;
                    			allterminos=true;
                				allcomarcas=true;
            					allprovincias=true;
        						allvariedades=true;
    							allcultivos=true;
    							mascaras = getCriteriaMascaraRdtosCaracEsp(lineaseguroid, codmodulo, codcultivo, codvariedad,
                        				codprovincia, codtermino, subtermino, codcomarca, modulos, allcultivos,allvariedades,allprovincias,
                        				allterminos,allsubterminos,allcomarcas).list();	
    						}
    					}						
    				}					
    			}
    		}		
    	}
    	
    	logger.debug("end - [DatosParcelaFLDao] getMascaraRdtosCaracEsp");
    	return mascaras;
    }
	
	private Criteria getCriteriaMascaraRdtosCaracEsp(Long lineaseguroid, String codmodulo,
			BigDecimal codcultivo, BigDecimal codvariedad, BigDecimal codprovincia,
			BigDecimal codtermino,Character subtermino,BigDecimal codcomarca,List modulos, boolean allcultivos,
			boolean allvariedades, boolean allprovincias, boolean allterminos, boolean allsubterminos, boolean allcomarcas){
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(MascaraRendimientoCaracteristicaEspecifica.class);
		
		if(modulos!=null && modulos.size()>0){
			Criterion crit = Restrictions.in("modulo.id.codmodulo", modulos);
			criteria.add(crit);
		}		
		
		if(codmodulo.equals("")){
			criteria.setProjection(Projections.property("id.codconcepto"));
		}
		// Lineaseguroid
		if (FiltroUtils.noEstaVacio(lineaseguroid)) {
			Criterion crit = Restrictions.eq("id.lineaseguroid",
					lineaseguroid);
			criteria.add(crit);
		}
		// Modulo
		if (FiltroUtils.noEstaVacio(codmodulo)) {
			Criterion crit = Restrictions.eq("id.codmodulo", codmodulo);
			criteria.add(crit);
		}
		// Cultivo
		if (FiltroUtils.noEstaVacio(codcultivo)) {
			if (allcultivos) {
				Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
				coll.add(codcultivo);
				coll.add(new BigDecimal("999"));
				Criterion crit = Restrictions.in("id.codcultivo", coll);
				criteria.add(crit);
			} else {
				Criterion crit = Restrictions.eq("id.codcultivo", codcultivo);
				criteria.add(crit);
			}
		}
		// Variedad
		if (FiltroUtils.noEstaVacio(codvariedad)) {
			if (allvariedades) {
				Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
				coll.add(codvariedad);
				coll.add(new BigDecimal("999"));
				Criterion crit = Restrictions.in("id.codvariedad", coll);
				criteria.add(crit);
			} else {
				Criterion crit = Restrictions.eq("id.codvariedad",
						codvariedad);
				criteria.add(crit);
			}
		}
		// Provincia
		if (FiltroUtils.noEstaVacio(codprovincia)) {
			if (allprovincias) {
				Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
				coll.add(codprovincia);
				coll.add(new BigDecimal("99"));
				Criterion crit = Restrictions.in("id.codprovincia", coll);
				criteria.add(crit);
			} else {
				Criterion crit = Restrictions.eq("id.codprovincia",
						codprovincia);
				criteria.add(crit);
			}
		}
		// Termino Municipal
		if (FiltroUtils.noEstaVacio(codtermino)) {
			if (allterminos) {
				Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
				coll.add(codtermino);
				coll.add(new BigDecimal("999"));
				Criterion crit = Restrictions.in("id.codtermino", coll);
				criteria.add(crit);
			} else {
				Criterion crit = Restrictions.eq("id.codtermino", codtermino);
				criteria.add(crit);
			}
		}
		// Subtermino
		if (FiltroUtils.noEstaVacio(subtermino) || allsubterminos) {
			if(allsubterminos){
				Collection<Character> coll = new ArrayList<Character>();
				coll.add(subtermino);
				coll.add("9".charAt(0));
				Criterion crit = Restrictions.in("id.subtermino", coll);
				criteria.add(crit);					
			} else {
				Criterion crit = Restrictions.eq("id.subtermino", subtermino);
				criteria.add(crit);
			}			
		}
		// Comarca
		if (FiltroUtils.noEstaVacio(codcomarca)) {
			if (allcomarcas) {
				Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
				coll.add(codcomarca);
				coll.add(new BigDecimal("99"));
				Criterion crit = Restrictions.in("id.codcomarca", coll);
				criteria.add(crit);
			} else {
				Criterion crit = Restrictions.eq("id.codcomarca", codcomarca);
				criteria.add(crit);
			}
		}

		return criteria;
	}
	
	public List<RendimientoCaracteristicaEspecifica> dameListaRenCaracEsp(BigDecimal tablaRdto,
			Long lineaseguroid, String codmodulo, ParcelaVO parcela, HashMap<String, String> filtroMascara){
		logger.debug("Init - [DatosParcelaFLDao] dameListaRenCaracEsp");
		List<RendimientoCaracteristicaEspecifica> lstRenCaracEsp= new ArrayList<RendimientoCaracteristicaEspecifica>();

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

		BigDecimal codsistemaconduccion=null;
		BigDecimal codpracticacultural=null;
		BigDecimal coddenominacionorigen=null;
		if (filtroMascara != null && filtroMascara.size() > 0) {
			// Rellenamos los datos variables de la parcela, obtenidos de la mascara de limites de rendimiento
			for (String key : filtroMascara.keySet()) {
				String valor = filtroMascara.get(key);
				if(valor != null && !"".equals(valor)) {
					if ("131".equals(key)) {
						// Sistema Conducción
							codsistemaconduccion=new BigDecimal(valor);
					} else if ("133".equals(key)) {
						// Practica Cultural
							codpracticacultural=new BigDecimal(valor);
					}else if ("107".equals(key)) {
						// Denominacion Origen
						coddenominacionorigen=new BigDecimal(valor);
					}else{
						logger.fatal("El condigo concepto "  + key + " no esta en el filtro para el calculo de los limites de rendimiento");
					}
				}
			}
		}
    	//limRendFiltro.getLimiteRendimiento().setTablardtos(tablaRdto);
		boolean allcultivos = false;
		boolean allvariedades = false;
		boolean allprovincias = false;
		boolean allterminos = false;
		boolean allsubterminos = false;
		boolean allcomarcas = false;
		
		// 3.2. Obtenemos los limites
		lstRenCaracEsp = getCriteriaRdtosCaracEsp(lineaseguroid, codmodulo, codcultivo, codvariedad,
				codprovincia, codtermino, subtermino, codcomarca, codsistemaconduccion, codpracticacultural, coddenominacionorigen, allcultivos,allvariedades,allprovincias,
				allterminos,allsubterminos,allcomarcas).list();

		if (lstRenCaracEsp.isEmpty()) {
			allsubterminos=true;
			lstRenCaracEsp = getCriteriaRdtosCaracEsp(lineaseguroid, codmodulo, codcultivo, codvariedad,
					codprovincia, codtermino, subtermino, codcomarca, codsistemaconduccion, codpracticacultural, coddenominacionorigen, allcultivos,allvariedades,allprovincias,
					allterminos,allsubterminos,allcomarcas).list();

			if (lstRenCaracEsp.isEmpty()) {
				allsubterminos=true;
				allterminos=true;
				lstRenCaracEsp = getCriteriaRdtosCaracEsp(lineaseguroid, codmodulo, codcultivo, codvariedad,
						codprovincia, codtermino, subtermino, codcomarca, codsistemaconduccion, codpracticacultural, coddenominacionorigen, allcultivos,allvariedades,allprovincias,
						allterminos,allsubterminos,allcomarcas).list();

				if (lstRenCaracEsp.isEmpty()) {
					allsubterminos=true;
					allterminos=true;
					allcomarcas=true;
					lstRenCaracEsp = getCriteriaRdtosCaracEsp(lineaseguroid, codmodulo, codcultivo, codvariedad,
							codprovincia, codtermino, subtermino, codcomarca, codsistemaconduccion, codpracticacultural, coddenominacionorigen, allcultivos,allvariedades,allprovincias,
							allterminos,allsubterminos,allcomarcas).list();

					if (lstRenCaracEsp.isEmpty()) {
						allsubterminos=true;
						allterminos=true;
						allcomarcas=true;
						allvariedades=true;
						lstRenCaracEsp = getCriteriaRdtosCaracEsp(lineaseguroid, codmodulo, codcultivo, codvariedad,
								codprovincia, codtermino, subtermino, codcomarca, codsistemaconduccion, codpracticacultural, coddenominacionorigen, allcultivos,allvariedades,allprovincias,
								allterminos,allsubterminos,allcomarcas).list();

						if (lstRenCaracEsp.isEmpty()) {
							allsubterminos=true;
							allterminos=true;
							allcomarcas=true;
							allvariedades=true;
							allprovincias=true;
							lstRenCaracEsp = getCriteriaRdtosCaracEsp(lineaseguroid, codmodulo, codcultivo, codvariedad,
									codprovincia, codtermino, subtermino, codcomarca, codsistemaconduccion, codpracticacultural, coddenominacionorigen, allcultivos,allvariedades,allprovincias,
									allterminos,allsubterminos,allcomarcas).list();

							if (lstRenCaracEsp.isEmpty()) {
								allsubterminos=true;
								allterminos=true;
								allcomarcas=true;
								allvariedades=true;
								allprovincias=true;
								allcultivos=true;
								lstRenCaracEsp = getCriteriaRdtosCaracEsp(lineaseguroid, codmodulo, codcultivo, codvariedad,
										codprovincia, codtermino, subtermino, codcomarca, codsistemaconduccion, codpracticacultural, coddenominacionorigen, allcultivos,allvariedades,allprovincias,
										allterminos,allsubterminos,allcomarcas).list();
							}
						}
					}
				}
			}
		}
		logger.debug("Fin - [DatosParcelaFLDao] dameListaRenCaracEsp");
		return lstRenCaracEsp;
	}
	
	
	private Criteria getCriteriaRdtosCaracEsp(Long lineaseguroid,String codmodulo,BigDecimal codcultivo,
			BigDecimal codvariedad, BigDecimal codprovincia, BigDecimal codtermino, Character subtermino,
			BigDecimal codcomarca, BigDecimal codsistemaconduccion, BigDecimal codpracticacultural,
			BigDecimal coddenominacionorigen,
			boolean allcultivos, boolean allvariedades, boolean allprovincias,
			 boolean allterminos, boolean allsubterminos, boolean allcomarcas){
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(RendimientoCaracteristicaEspecifica.class);
		
		    // Datos Fijos de la Parcela
		    // Linea de Seguro
			if (FiltroUtils.noEstaVacio(lineaseguroid)) {
				Criterion crit = Restrictions.eq("id.lineaseguroid",
						lineaseguroid);
				criteria.add(crit);
			}
			// Modulo
			if (FiltroUtils.noEstaVacio(codmodulo)) {
				criteria.createAlias("modulo", "m");
				Criterion crit = Restrictions.eq("m.id.codmodulo",
						codmodulo);
				criteria.add(crit);
			}
			boolean hayAliasVariedad=false;
			// Cultivo
			if (FiltroUtils.noEstaVacio(codcultivo) || allcultivos) {
				criteria.createAlias("variedad", "v");
				hayAliasVariedad=true;
				
				if (allcultivos) {
					Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
					coll.add(codcultivo);
					coll.add(new BigDecimal("999"));
					Criterion crit = Restrictions.in("v.id.codcultivo", coll);
					criteria.add(crit);
				} else {
					Criterion crit = Restrictions.eq("v.id.codcultivo",
							codcultivo);
					criteria.add(crit);
				}
			}
			// Variedad
			if (FiltroUtils.noEstaVacio(codvariedad) || allvariedades) {
				if(!hayAliasVariedad) {
					criteria.createAlias("variedad", "v");
					hayAliasVariedad=true;
				}
				
				if (allvariedades) {
					Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
					coll.add(codvariedad);
					coll.add(new BigDecimal("999"));
					Criterion crit = Restrictions.in("v.id.codvariedad", coll);
					criteria.add(crit);
				} else {
					Criterion crit = Restrictions.eq("v.id.codvariedad",
							codvariedad);
					criteria.add(crit);
				}
			}
			// Provincia
			if (FiltroUtils.noEstaVacio(codprovincia) || allprovincias) {
				if (allprovincias) {
					Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
					coll.add(codprovincia);
					coll.add(new BigDecimal("99"));
					Criterion crit = Restrictions.in("codprovincia", coll);
					criteria.add(crit);
				} else {
					Criterion crit = Restrictions.eq("codprovincia", codprovincia);
					criteria.add(crit);
				}
			}
			// Termino Municipal
			if (FiltroUtils.noEstaVacio(codtermino) || allterminos) {
				if (allterminos) {
					Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
					coll.add(codtermino);
					coll.add(new BigDecimal("999"));
					Criterion crit = Restrictions.in("codtermino", coll);
					criteria.add(crit);
				} else {
					Criterion crit = Restrictions.eq("codtermino", codtermino);
					criteria.add(crit);
				}
			}
			// Subtermino
			if (FiltroUtils.noEstaVacio(subtermino) || allsubterminos) {
				if(allsubterminos){
					Collection<Character> coll = new ArrayList<Character>();
					coll.add(subtermino);
					coll.add("9".charAt(0));
					Criterion crit = Restrictions.in("subtermino", coll);
					criteria.add(crit);					
				} else {
					Criterion crit = Restrictions.eq("subtermino", subtermino);
					criteria.add(crit);
				}
			}
			// Comarca
			if (FiltroUtils.noEstaVacio(codcomarca) || allcomarcas) {
				if (allcomarcas) {
					Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
					coll.add(codcomarca);
					coll.add(new BigDecimal("99"));
					Criterion crit = Restrictions.in("codcomarca", coll);
					criteria.add(crit);
				} else {
					Criterion crit = Restrictions.eq("codcomarca", codcomarca);
					criteria.add(crit);
				}
			}
			if (FiltroUtils.noEstaVacio(codsistemaconduccion)) {
				criteria.createAlias("sistemaConduccion", "sConduccion");
				Criterion crit = Restrictions.eq("sConduccion.codsistemaconduccion", codsistemaconduccion);
				criteria.add(crit);
			}
			if (FiltroUtils.noEstaVacio(codpracticacultural)) {
				criteria.createAlias("practicaCultural", "pCultural");
				Criterion crit = Restrictions.eq("pCultural.codpracticacultural", codpracticacultural);
				criteria.add(crit);
			}
			if (FiltroUtils.noEstaVacio(coddenominacionorigen)) {
				criteria.createAlias("codigoDenominacionOrigen", "cDenOrigen");
				Criterion crit = Restrictions.eq("cDenOrigen.id.coddenomorigen", coddenominacionorigen);
				criteria.add(crit);
		}
		return criteria;
	}
	
	public List<MascaraLimiteRendimiento> getMascaraLimiteRendimiento(Long lineaseguroid, String codmodulo,ParcelaVO parcela) {
    	logger.debug("init - [DatosParcelaFLDao] getMascaraLimiteRendimiento");
    	
    	List<MascaraLimiteRendimiento> mascaras = new ArrayList<MascaraLimiteRendimiento>();
    	
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
		
		MascaraLimiteRendimientoFiltro mascaraFiltro = new MascaraLimiteRendimientoFiltro(lineaseguroid, codcultivo,
				codvariedad, codprovincia, codcomarca, codtermino, subtermino, codmodulo);
		
    	mascaras = this.getObjects(mascaraFiltro);
    	
    	if (mascaras.isEmpty()) {
    		// Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
			//	1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
			//	4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
    		mascaras = filtroMascLimRendimiento(mascaraFiltro);
    	}
    	
    	logger.debug("end - [DatosParcelaFLDao] getMascaraLimiteRendimiento");
    	return mascaras;
    }
	
	/**
	 * Filtra por los datos genericos. Segun el siguiente orden: 
	 * 	1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
	 *  4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
	 * 
	 * @param filtroMascLimRend
	 * @return
	 */
	private List<MascaraLimiteRendimiento> filtroMascLimRendimiento(MascaraLimiteRendimientoFiltro filtroMascLimRend){
		logger.debug("init - [DatosParcelaFLDao] filtroMascLimRendimiento");
		
		List<MascaraLimiteRendimiento> listMascLimRendimiento = new ArrayList<MascaraLimiteRendimiento>();
		
		filtroMascLimRend.setAllsubterminos(true);
		listMascLimRendimiento = this.getObjects(filtroMascLimRend);
		
		if(listMascLimRendimiento.isEmpty()){
			filtroMascLimRend.setAllterminos(true);
			listMascLimRendimiento = this.getObjects(filtroMascLimRend);
			
			if(listMascLimRendimiento.isEmpty()){
				filtroMascLimRend.setAllcomarcas(true);
				listMascLimRendimiento = this.getObjects(filtroMascLimRend);
			
				if(listMascLimRendimiento.isEmpty()){
					filtroMascLimRend.setAllvariedades(true);
					listMascLimRendimiento = this.getObjects(filtroMascLimRend);
					
					if(listMascLimRendimiento.isEmpty()){
						filtroMascLimRend.setAllprovincias(true);
						listMascLimRendimiento = this.getObjects(filtroMascLimRend);		
					
						if(listMascLimRendimiento.isEmpty()){
							filtroMascLimRend.setAllcultivos(true);
							listMascLimRendimiento = this.getObjects(filtroMascLimRend);
						}
					}						
				}					
			}
		}		
	
		logger.debug("end - [DatosParcelaFLDao] filtroMascLimRendimiento");
		return listMascLimRendimiento;
	}
	
	public List<LimiteRendimiento> getRendimientos(LimiteRendimientoFiltro limRendFiltro) {
    	logger.debug("init - [DatosParcelaFLDao] getRendimientos");
    	
		List<LimiteRendimiento> rendimientos = new ArrayList<LimiteRendimiento>();

		// 3.2. Obtenemos los limites
		rendimientos = getObjects(limRendFiltro);

		if (rendimientos.isEmpty()) {
			limRendFiltro.setAllsubterminos(true);
			rendimientos = getObjects(limRendFiltro);

			if (rendimientos.isEmpty()) {
				limRendFiltro.setAllterminos(true);
				rendimientos = getObjects(limRendFiltro);

				if (rendimientos.isEmpty()) {
					limRendFiltro.setAllcomarcas(true);
					rendimientos = getObjects(limRendFiltro);

					if (rendimientos.isEmpty()) {
						limRendFiltro.setAllvariedades(true);
						rendimientos = getObjects(limRendFiltro);

						if (rendimientos.isEmpty()) {
							limRendFiltro.setAllprovincias(true);
							rendimientos = getObjects(limRendFiltro);

							if (rendimientos.isEmpty()) {
								limRendFiltro.setAllcultivos(true);
								rendimientos = getObjects(limRendFiltro);
							}
						}
					}
				}
			}
		}
		
		logger.debug("end - [DatosParcelaFLDao] getRendimientos");
		return rendimientos;
    }
	
	/* Pet. 63485-Fase II ** MODIF TAM (21.09.2020) ** Inicio */
	@Override
	public boolean getRiesgoCubElegCalculoRendi(Long lineaseguroid,String codmodulo, List<BigDecimal> lstCodCultivos) throws DAOException {
		
		logger.debug("**@@** CalculoProduccionDao - getRiesgoCubElegCalculoRendi [INIT]");
		
		Session session = obtenerSession();
		
		List<MascaraLimiteRendimiento> list = null;
		
		String sql= "select * from tb_sc_c_masc_limites_rdtos masc where " +
					"masc.lineaseguroid = " + lineaseguroid + " and masc.codmodulo = '" + codmodulo + "'" +
					" and masc.codconcepto = 363 AND MASC.CODCULTIVO IN (";
		
		
		
		//Creamos el conjunto de valores para la claúsula IN
		for (int i = 0; i < lstCodCultivos.size(); i++) {
			sql = sql + lstCodCultivos.get(i).toString();
			if(i+1<lstCodCultivos.size())
				sql=sql + ",";
		}
		sql=sql + ")";
		
		logger.debug("Valor de sql:"+sql);
		
		list = session.createSQLQuery(sql).list();
		
		logger.debug("**@@** CalculoProduccionDao - getRiesgoCubElegCalculoRendi [END]");
		
		if (list.size() >= 1) {
			return true;
		}else {
			return false;
		}
		
	}
	
	/* Pet. 63485-Fase II ** MODIF TAM (21.09.2020) ** Fin */

}
