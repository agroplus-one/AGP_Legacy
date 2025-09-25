package com.rsi.agp.core.jmesa.dao.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.IPolizasRenovablesDao;
import com.rsi.agp.core.jmesa.filter.PolizasRenovablesFilter;
import com.rsi.agp.core.jmesa.sort.PolizasRenovablesSort;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroplus;
import com.rsi.agp.dao.tables.renovables.GastosRenovacion;
import com.rsi.agp.dao.tables.renovables.PolizaRenovable;
import com.rsi.agp.dao.tables.renovables.PolizaRenovableHistoricoEstados;
import com.rsi.agp.dao.tables.renovables.PolizaRenovableValidacionEnvioIBAN;
import com.rsi.agp.dao.tables.renovables.VistaPolizaRenovable;
/**
 * @author U029769
 *
 */
public class PolizasRenovablesDao extends BaseDaoHibernate implements IPolizasRenovablesDao { 
	
	
	private static final String OR_REN_ID_IN = " or ren.id in (";
	private static final String AND_REN_ID_IN = " and (ren.id in (";
	private static final String DD_MM_YYYY2 = "','DD/MM/YYYY')";
	private static final String FECHA_ENVIO_IBAN_AGRO = "fechaEnvioIbanAgro";
	private static final String FECHA_RENOVACION = "fechaRenovacion";
	private static final String FECHA_CARGA = "fechaCarga";
	private static final String DD_MM_YYYY = "dd/MM/yyyy";
	private static final String SE_HA_PRODUCIDO_UN_ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS = "Se ha producido un error durante el acceso a la base de datos";

	@SuppressWarnings("unchecked") 
	public Collection<VistaPolizaRenovable> getPolizasRenovablesWithFilterAndSort(
			final PolizasRenovablesFilter filter, final PolizasRenovablesSort sort, final int rowStart,
			final int rowEnd) throws BusinessException {
		try {
			logger.debug("init - [MtoPolizasRenovablesDao] getPolRenovablesWithFilterAndSort");
			List<VistaPolizaRenovable> applications = (List<VistaPolizaRenovable>) getHibernateTemplate().execute(new HibernateCallback() {

						public Object doInHibernate(final Session session)
								throws HibernateException, SQLException {
							Criteria criteria = session.createCriteria(VistaPolizaRenovable.class);

							// Filtro
							criteria = filter.execute(criteria);
							// Ordenacion
							criteria = sort.execute(criteria);
							if (rowStart != -1 && rowEnd != -1) {
						        // Primer registro
						        criteria.setFirstResult(rowStart);
						        // N�mero m�ximo de registros a mostrar
						        criteria.setMaxResults(rowEnd - rowStart);
						    }
							final List<Usuario> lista = criteria.list();
							return lista;
						}
					});
			logger.debug("end - [MtoPolizasRenovablesDao] getPolRenovablesWithFilterAndSort");
			return applications;
		} catch (Exception e) {
			throw new BusinessException(SE_HA_PRODUCIDO_UN_ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS, e);
		}
	}
	

	@Override
	public int getPolRenovablesCountWithFilter(final PolizasRenovablesFilter filter,
			final String fecCargaIni,final String fecCargaFin,final String fecRenoIni,final String fecRenoFin,
			final String fecEnvioIBANIni,final String fecEnvioIBANFin, final String grupoNegocio,final String estAgroplus) {
		logger.debug("init - [MtoPolizasRenovablesDao] getPolRenovablesCountWithFilter");
		
		Integer count = (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						
						Criteria criteria = session.createCriteria(VistaPolizaRenovable.class);
						
						// GRUPO NEGOCIO
						if (grupoNegocio != null && !grupoNegocio.equals("")){
							criteria.add(Restrictions.eq("gruponegocio",grupoNegocio.charAt(0)));
						}
						
						// estado Agroplus
						if (estAgroplus != null && !estAgroplus.equals("")){
							criteria.add(Restrictions.eq("estagroplus", new BigDecimal(estAgroplus)));
						}
						
						
						// parametros Fechas carga
						try {
							if (fecCargaIni != null && !fecCargaIni.equals("")){
								DateFormat df = new SimpleDateFormat(DD_MM_YYYY);
								Date date = df.parse(fecCargaIni);
								if (!StringUtils.nullToString(fecCargaIni).equals("")) 
									criteria.add(Restrictions.ge(FECHA_CARGA, date));
							}
							if (fecCargaFin != null && !fecCargaFin.equals("")){
								DateFormat df1 = new SimpleDateFormat(DD_MM_YYYY);
								Date date1 = df1.parse(fecCargaFin);
								if (!StringUtils.nullToString(fecCargaFin).equals("")) 
									criteria.add(Restrictions.le(FECHA_CARGA, date1));
							}
						} catch (ParseException e) {
							logger.error(" [MtoPolizasRenovablesDao] getPolRenovablesCountWithFilter - error en fechas carga",e);
						}
						
						
						// parametros Fechas renovacion
						try {
							if (fecRenoIni != null && !fecRenoIni.equals("")){
								DateFormat df = new SimpleDateFormat(DD_MM_YYYY);
								Date date = df.parse(fecRenoIni);
								if (!StringUtils.nullToString(fecRenoIni).equals("")) 
									criteria.add(Restrictions.ge(FECHA_RENOVACION, date));
							}
							if (fecRenoFin != null && !fecRenoFin.equals("")){
								DateFormat df1 = new SimpleDateFormat(DD_MM_YYYY);
								Date date1 = df1.parse(fecRenoFin);
								if (!StringUtils.nullToString(fecRenoFin).equals("")) 
									criteria.add(Restrictions.le(FECHA_RENOVACION, date1));
							}
						} catch (ParseException e) {
							logger.error(" [MtoPolizasRenovablesDao] getPolRenovablesCountWithFilter - error en fechas renovacion",e);
						}
						
						// parametros Fechas envio IBAN
						try {
							if (fecEnvioIBANIni != null && !fecEnvioIBANIni.equals("")){
								DateFormat df = new SimpleDateFormat(DD_MM_YYYY);
								Date date = df.parse(fecEnvioIBANIni);
								if (!StringUtils.nullToString(fecEnvioIBANIni).equals("")) 
									criteria.add(Restrictions.ge(FECHA_ENVIO_IBAN_AGRO, date));
							}
							if (fecEnvioIBANFin != null && !fecEnvioIBANFin.equals("")){
								DateFormat df1 = new SimpleDateFormat(DD_MM_YYYY);
								Date date1 = df1.parse(fecEnvioIBANFin);
								if (!StringUtils.nullToString(fecEnvioIBANFin).equals("")) 
									criteria.add(Restrictions.le(FECHA_ENVIO_IBAN_AGRO, date1));
							}
						} catch (ParseException e) {
							logger.error(" [MtoPolizasRenovablesDao] getPolRenovablesCountWithFilter - error en fechas envio IBAN",e);
						}
						
						criteria = filter.execute(criteria);
						
						return criteria.setProjection(Projections.rowCount()).uniqueResult();
					}
				});
		logger.debug("end - [MtoPolizasRenovablesDao] getPolRenovablesCountWithFilter");
		return count.intValue();
	}

	@Override
	public Collection<VistaPolizaRenovable> getPolRenovablesWithFilterAndSort(
			final PolizasRenovablesFilter filter, final PolizasRenovablesSort sort,
			final int rowStart,final int rowEnd,final String fecCargaIni,
			final String fecCargaFin,final String fecRenoIni,final String fecRenoFin,
			final String fecEnvioIBANIni,final String fecEnvioIBANFin,final String grupoNegocio, final String estAgroplus) throws BusinessException {
		try {
			logger.debug("init - [PolizasRenovablesDao] getPolRenovablesWithFilterAndSort");
			@SuppressWarnings("unchecked")
			List<VistaPolizaRenovable> applications = (List<VistaPolizaRenovable>) getHibernateTemplate().execute(new HibernateCallback() {

						public Object doInHibernate(final Session session) throws HibernateException, SQLException {
							Criteria criteria = session.createCriteria(VistaPolizaRenovable.class);
							
							
							// GRUPO NEGOCIO
							if (grupoNegocio != null && !grupoNegocio.equals("")){
								criteria.add(Restrictions.eq("gruponegocio",grupoNegocio.charAt(0)));
							}
							
							// estado Agroplus
							if (estAgroplus != null && !estAgroplus.equals("")){
								criteria.add(Restrictions.eq("estagroplus", new BigDecimal(estAgroplus)));
							}
							
							// parametros Fechas carga
							try {
								if (fecCargaIni != null && !fecCargaIni.equals("")){
									DateFormat df = new SimpleDateFormat(DD_MM_YYYY);
									Date date = df.parse(fecCargaIni);
									if (!StringUtils.nullToString(fecCargaIni).equals("")) 
										criteria.add(Restrictions.ge(FECHA_CARGA, date));
								}
								if (fecCargaFin != null && !fecCargaFin.equals("")){
									DateFormat df1 = new SimpleDateFormat(DD_MM_YYYY);
									Date date1 = df1.parse(fecCargaFin);
									if (!StringUtils.nullToString(fecCargaFin).equals("")) 
										criteria.add(Restrictions.le(FECHA_CARGA, date1));
								}
							} catch (ParseException e) {
								logger.error(" [MtoPolizasRenovablesDao] getPolRenovablesWithFilterAndSort - error en fechas carga",e);
							}
							
							
							// parametros Fechas renovacion
							try {
								if (fecRenoIni != null && !fecRenoIni.equals("")){
									DateFormat df = new SimpleDateFormat(DD_MM_YYYY);
									Date date = df.parse(fecRenoIni);
									if (!StringUtils.nullToString(fecRenoIni).equals("")) 
										criteria.add(Restrictions.ge(FECHA_RENOVACION, date));
								}
								if (fecRenoFin != null && !fecRenoFin.equals("")){
									DateFormat df1 = new SimpleDateFormat(DD_MM_YYYY);
									Date date1 = df1.parse(fecRenoFin);
									if (!StringUtils.nullToString(fecRenoFin).equals("")) 
										criteria.add(Restrictions.le(FECHA_RENOVACION, date1));
								}
							} catch (ParseException e) {
								logger.error(" [MtoPolizasRenovablesDao] getPolRenovablesWithFilterAndSort - error en fechas renovacion",e);
							}
							
							// parametros Fechas envio IBAN
							try {
								if (fecEnvioIBANIni != null && !fecEnvioIBANIni.equals("")){
									DateFormat df = new SimpleDateFormat(DD_MM_YYYY);
									Date date = df.parse(fecEnvioIBANIni);
									if (!StringUtils.nullToString(fecEnvioIBANIni).equals("")) 
										criteria.add(Restrictions.ge(FECHA_ENVIO_IBAN_AGRO, date));
								}
								if (fecEnvioIBANFin != null && !fecEnvioIBANFin.equals("")){
									DateFormat df1 = new SimpleDateFormat(DD_MM_YYYY);
									Date date1 = df1.parse(fecEnvioIBANFin);
									if (!StringUtils.nullToString(fecEnvioIBANFin).equals("")) 
										criteria.add(Restrictions.le(FECHA_ENVIO_IBAN_AGRO, date1));
								}
							} catch (ParseException e) {
								logger.error(" [MtoPolizasRenovablesDao] getPolRenovablesCountWithFilter - error en fechas envio IBAN",e);
							}
							
							
							
							// Filtro
							criteria = filter.execute(criteria);
							// Ordenacion
							criteria = sort.execute(criteria);
							// Primer registro
					        criteria.setFirstResult(rowStart);
					        // N�mero m�ximo de registros a mostrar
					        criteria.setMaxResults(rowEnd - rowStart);	
							
							return criteria.list();
						}
					});
			logger.debug("end - [PolizasRenovablesDao] getPolRenovablesWithFilterAndSort");
			return applications;
		} catch (Exception e) {
			throw new BusinessException(SE_HA_PRODUCIDO_UN_ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS, e);
		}
	}

	
	public String getlistaIdsTodos(PolizasRenovablesFilter consultaFilter,String fecCargaIni,String fecCargaFin,String fecRenoIni,String fecRenoFin,
			String fecEnvioIBANIni,String fecEnvioIBANFin,String grupoNegocio, String estAgroplus) {
		String listaids="";
		Session session = obtenerSession();

		String sql = 	"SELECT ren.id " +
				 		"FROM vw_polizas_renovables ren " + consultaFilter.getSqlWhere();
		if (fecCargaIni != null && !fecCargaIni.equals("")){
			sql += " AND ren.fechacarga >= TO_DATE('" + fecCargaIni + DD_MM_YYYY2;
		}
		if (fecCargaFin != null && !fecCargaFin.equals("")){
			sql += " AND ren.fechacarga <= TO_DATE('" + fecCargaFin+ DD_MM_YYYY2;
		}
		if (fecRenoIni != null && !fecRenoIni.equals("")){
			sql += " AND ren.fecharenovacion >= TO_DATE('" + fecRenoIni + DD_MM_YYYY2;
		}
		if (fecRenoFin != null && !fecRenoFin.equals("")){
			sql += " AND ren.fecharenovacion <= TO_DATE('" + fecRenoFin + DD_MM_YYYY2;
		}
		if (fecEnvioIBANIni != null && !fecEnvioIBANIni.equals("")){
			sql += " AND ren.fechaEnvioIbanAgro >= TO_DATE('" + fecEnvioIBANIni + DD_MM_YYYY2;
		}
		if (fecEnvioIBANFin != null && !fecEnvioIBANFin.equals("")){
			sql += " AND ren.fechaEnvioIbanAgro <= TO_DATE('" + fecEnvioIBANFin + DD_MM_YYYY2;
		}
		// GRUPO NEGOCIO
		if (grupoNegocio != null && !grupoNegocio.equals("")){
			sql += " AND ren.gruponegocio = " + grupoNegocio;
		}
		// estados agroplus
		if (estAgroplus != null && !estAgroplus.equals("")){
			sql += " AND ren.estagroplus = " + estAgroplus;
		}
		
		@SuppressWarnings("rawtypes")
		List lista = session.createSQLQuery(sql).list();
		logger.debug("********** list IDS para checks = "+ lista.size() + " ************************");
		for(int i=0;i<lista.size();i++){
			listaids += lista.get(i)+",";
		}
		return listaids;
	}
	
	
	@Override
	public PolizaRenovable getPolizaById(Long id) throws DAOException {
		return (PolizaRenovable) get(PolizaRenovable.class, id);
	}
	
	@Override
	public GastosRenovacion getGastosRenovacionById(Long id) throws DAOException {
		return (GastosRenovacion) get(GastosRenovacion.class, id);
	}
	
	@Override
	public EstadoRenovacionAgroplus getEstadorenovacionAgroplus(Long codigo) throws DAOException {
		return (EstadoRenovacionAgroplus) get(EstadoRenovacionAgroplus.class, codigo);
	}
		
	public Long getLineaSeguroId(Long codPlan, Long codLinea) {
		Session session = obtenerSession();
		String sql = " select lineaseguroid from o02agpe0.tb_lineas where codplan=" + codPlan + " and codlinea="
				+ codLinea;
		SQLQuery query = session.createSQLQuery(sql);
		return (new Long((query.uniqueResult()).toString()));
	}
	
	@Override
	public void cambioMasivo(GastosRenovacion gr,PolizaRenovable polRen, EstadoRenovacionAgroplus estAgpGastosAsignados, 
			EstadoRenovacionAgroplus estAgpPendienteAsigGastos, String usuario) throws Exception {		
		try {								
				// Modifica el estado de los gastos renovaci�n
			    gr.setComisionEntidad(gr.getComEntidad());
			    gr.setComisionESMediadora(gr.getComESMed());
			    gr.setComisionMediador(gr.getComMediador());
				gr.setEstadoRenovacionAgroplus(estAgpGastosAsignados);

				// Modifica el registro de gastos Renovacion
				if (gr != null) {					
					this.saveOrUpdate(gr);
					
				}
				
				// Inserta el registro en el hist�rico
				PolizaRenovableHistoricoEstados hist = new PolizaRenovableHistoricoEstados();
				hist.setEstadoRenovacionAgroplus(gr.getEstadoRenovacionAgroplus());
				hist.setEstadoRenovacionAgroseguro(polRen.getEstadoRenovacionAgroseguro());
				hist.setFecha(new Date());
				hist.setPolizaRenovable(polRen);
				hist.setUsuario(usuario);
				hist.setPctComisionMediador(gr.getComisionMediador());
				hist.setPctComisionEntidad(gr.getComisionEntidad());
				hist.setPctComisionESMed(gr.getComisionESMediadora());
				hist.setGrupoNegocio(gr.getGrupoNegocio());
				this.saveOrUpdate(hist);
				
		}
		catch (Exception e) {
			logger.error("Error en cambioMasivo: ", e);
			if (gr != null) {				
				gr.setEstadoRenovacionAgroplus(estAgpPendienteAsigGastos);
				this.saveOrUpdate(gr);
				logger.error("Se cambia el estado Agroplus del gasto renovacion " +gr.getId() +" a pendiente de asignar gastos");
			}
			//throw e;
		}
		
		
	}
	
	@Override
	public boolean validacionesPreviasEnvioIBAN(List<String> lstCadenasIds, boolean marcar,String usuario, int total) throws Exception {
		boolean res = false;
		Integer totales = total;
		Session session = obtenerSession();
		Integer count = 0;
		boolean primera = true;
		try {
			StringBuilder stringQuery = new StringBuilder();
			if (marcar) {
			   stringQuery.append("select count(*) from tb_Polizas_Renovables ren"+ 
               " where ren.estado_agroseguro in (" +
			   Constants.ES_POL_REN_AGSEGURO_BORRADOR_PRECARTERA+","+      //1
			   Constants.ES_POL_REN_AGSEGURO_PRIMERA_COMUNICACION+","+     //2
			   Constants.ES_POL_REN_AGSEGURO_PRECARTERA_PRECALCULADA+","+  //8
			   Constants.ES_POL_REN_AGSEGURO_PRECARTERA_GENERADA+")"+      //9
			   " and ren.estado_envio_iban_agro in (" +
			   Constants.ES_POL_REN_ENVIO_IBAN_NO+","+					   //1	
			   Constants.ES_POL_REN_ENVIO_IBAN_CORRECTO+","+			   //4
			   Constants.ES_POL_REN_ENVIO_IBAN_ERRONEO+")");			   //5	
			   if (lstCadenasIds.size()>0) {
				   primera = true;
				   for (String cadenaIds:lstCadenasIds) {
						if (primera) {
							stringQuery.append(AND_REN_ID_IN + cadenaIds + ")");
							primera = false;
						} else {
							stringQuery.append(OR_REN_ID_IN + cadenaIds + ")");
						}
				   }
				   stringQuery.append(")");
			   }
			   SQLQuery query = session.createSQLQuery(stringQuery.toString());
			   count = new Integer((query.uniqueResult()).toString());  
		   }else { // desmarcar
				stringQuery.append("select count(*) from tb_Polizas_Renovables ren"+ 
			                       " where ren.estado_envio_iban_agro=" +Constants.ES_POL_REN_ENVIO_IBAN_PREPARADO);
				if (lstCadenasIds.size()>0){
					   primera = true;
					   for (String cadenaIds:lstCadenasIds) {
						   if (primera) {
							   stringQuery.append(AND_REN_ID_IN+cadenaIds+")");
							   primera = false;
					   	   }else {
							   stringQuery.append(OR_REN_ID_IN+cadenaIds+")");
					   	   }
					   }
					   stringQuery.append(")");
			   }
			   SQLQuery query = session.createSQLQuery(stringQuery.toString());
			   count = new Integer((query.uniqueResult()).toString()); 
		   }
		   if (count != null){
				if (count.compareTo(totales) == 0) {
					res = true;
				}
				else {
					res = false;
				}
			}	 			
			return res;
		} catch (Exception e) {
			logger.error("Error en validar envio IBAN: ", e);
			throw e;
		}
	}
	
	
	@Override
	public boolean modificarEstadoEnvioIBAN(List<String> lstCadenasIds, String estado,String usuario) throws Exception {
		boolean res = false;
		Session session = obtenerSession();
		boolean primera = true;
		try {
			StringBuilder stringQuery = new StringBuilder();
			   stringQuery.append("update tb_Polizas_Renovables ren set estado_envio_iban_agro = "+estado+" where 1=1 ");		 
			   if (lstCadenasIds.size()>0) {
				   primera = true;
				   for (String cadenaIds:lstCadenasIds) {
						if (primera) {
							stringQuery.append(AND_REN_ID_IN + cadenaIds + ")");
							primera = false;
						} else {
							stringQuery.append(OR_REN_ID_IN + cadenaIds + ")");
						}
				   }
				   stringQuery.append(")");
			   }
			   Query query = session.createSQLQuery(stringQuery.toString());
			   query.executeUpdate();
			return res;
		}catch (Exception e) {
			logger.error("Error en la modificaci�n de los estados Env�o IBAN, al actualizar el estado a: "+estado, e);
			throw e;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public List<PolizaRenovableValidacionEnvioIBAN> getPolRenValidacionEnvioIBAN(String idErroresIBAN){
		List<PolizaRenovableValidacionEnvioIBAN> lstErroresEnvioIBAN = null;
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(PolizaRenovableValidacionEnvioIBAN.class);
			criteria.add(Restrictions.eq("id.id", Long.parseLong(idErroresIBAN)));

			lstErroresEnvioIBAN = criteria.list();
			return lstErroresEnvioIBAN;
		}catch (Exception e) {
			logger.error("PolizasRenovablesDao - getPolRenValidacionEnvioIBAN -Se ha producido un error al recoger de PolizaRenovableValidacionEnvioIBAN en p�lizas renovables: ", e);
		}
		return lstErroresEnvioIBAN;
	}	
	
	public BigDecimal recogerSecuenciaValidaEnvioIBAN() {
		Session session = obtenerSession();
		String sql = "select sq_plz_renov_valida_envio_iban.nextval from dual";
		BigDecimal secuencia = (BigDecimal)session.createSQLQuery(sql).uniqueResult();
		return secuencia;
	}
	
	
	public String getAcuseReciboGastos(Long idPolRen) {
		Session session = obtenerSession();
		String sql = "select desc_error_envio from tb_polizas_renovables where id="+idPolRen;
		String acuseRecibo = (String)session.createSQLQuery(sql).uniqueResult();	
		
		//ESC-6606 DNF 21/08/2019
		String sqlGastosRenovacion = "select desc_error_envio from tb_gastos_renovacion where idpolizarenovable = " + idPolRen;
		
		if ("".equals(acuseRecibo) || null == acuseRecibo) {
			
			acuseRecibo = (String) session.createSQLQuery(sqlGastosRenovacion).uniqueResult();
			
			if ("".equals(acuseRecibo) || null == acuseRecibo) {
				
				acuseRecibo = Constants.STRING_NA;
				
			}
		}
		//FIN ESC-6606 DNF 21/08/2019
		
		return acuseRecibo;
	}
	
	public Object[] recogerParametrosPolizaRenovable(String idPolRen) {
		Session session = obtenerSession();
		String sql = "select ren.nif_asegurado,col.codentidad,col.codentidadmed,col.codsubentmed,ren.linea,ren.referencia,ren.dc"+
				     " from tb_polizas_renovables ren, tb_colectivos_renovacion col where ren.idcolectivo = col.id and ren.id = "+idPolRen;
		@SuppressWarnings("unchecked")
		List<Object> resultado = session.createSQLQuery(sql).list();
		Object[] registro = (Object[]) resultado.get(0);
		return registro;
	}
	
	public Long comprobarAsegurado(String nifCif, BigDecimal codEntidad, BigDecimal codEntMed, BigDecimal subEntMed) {
		Session session = obtenerSession();
		BigDecimal idAsegurado  = null;
		String sql = "select ase.id from tb_asegurados ase, tb_usuarios usu where ase.codusuario = usu.codusuario and ase.nifcif ='"+nifCif+"'"+
		             " and ase.codentidad ="+codEntidad+ " and usu.entmediadora ="+codEntMed+ " and usu.subentmediadora ="+subEntMed;
		@SuppressWarnings("unchecked")
		List<Object> resultado = session.createSQLQuery(sql).list();
		if (resultado != null && resultado.size()>0) {
			idAsegurado = (BigDecimal)resultado.get(0);
			return idAsegurado.longValue();
		}
		return null;
	}
	
	public Integer comprobarCuentasAsegurado(Long idAsegurado,BigDecimal codLinea) {
		Session session = obtenerSession();
		String sql = "select count(*) from tb_datos_asegurados dat where dat.idasegurado="+idAsegurado+" and (codlinea ="+codLinea+ " or codlinea = 999)";
		SQLQuery query = session.createSQLQuery(sql);
		Integer count = new Integer((query.uniqueResult()).toString()); 
		logger.debug(" Total cuentas del asegurado:"+idAsegurado+": "+count);
		return count;
	}

	public void updatePolRenEnvioIBANHisEstados(String id,String estado,String usuario) {
		Session session = obtenerSession();
		String insert = " insert into tb_plz_renov_env_iban_hist_est values(sq_plz_renov_env_iban_hist_est.nextval,"+id+","+estado+",'"+usuario+"',"+" sysdate)";
		Query query = session.createSQLQuery(insert);
		query.executeUpdate();
		logger.debug("ACT.POL REN: " + id );
	}
	
	@Override
	public int getCountPlzGastosMasivo (List<String> idsPoliza) throws DAOException {
		Session sesion = obtenerSession();
		try {
			String sqlQuery = "SELECT COUNT(*) FROM TB_POLIZAS_RENOVABLES P WHERE";			
			sqlQuery += " P.ID IN " + StringUtils.toValoresSeparadosXComas(idsPoliza,false);
			sqlQuery += " AND P.ESTADO_AGROSEGURO IN (" + Constants.ES_POL_REN_AGSEGURO_COMUNICACION_DEFINITIVA + "," 
						+ Constants.ES_POL_REN_AGSEGURO_EMITIDA + ")";
			
			// Lanza la consulta y devuelve el valor del count
			return ((BigDecimal)sesion.createSQLQuery (sqlQuery).list().get(0)).intValue();			
			
		} 
		catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException(SE_HA_PRODUCIDO_UN_ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS, ex);
		}
	}
	
	
	public List<String> getListaIdsRenovables (List<String> lstCadenasIds){
		Session sesion = obtenerSession();
		List<String> lstIdsPol = new ArrayList<String>();
		boolean primera = true;
		try {
			StringBuilder stringQuery = new StringBuilder();
			stringQuery.append("select distinct pr.id from o02agpe0.tb_polizas_renovables pr, o02agpe0.tb_gastos_renovacion gr" +
					" where pr.id = gr.idpolizarenovable ");
			if (lstCadenasIds.size()>0) {
				   primera = true;
				   for (String cadenaIds:lstCadenasIds) {
					   if (primera){
						   stringQuery.append(" and (gr.id in ("+cadenaIds+")");
					       primera = false;
				       }else {
						   stringQuery.append(" or gr.id in ("+cadenaIds+")");
				       }
				   }
				   stringQuery.append(")");
			   }
						
			// Lanza la consulta y devuelve el valor del count
			@SuppressWarnings("unchecked")
			List<BigDecimal> lstIdsp = ((List<BigDecimal>)sesion.createSQLQuery (stringQuery.toString()).list());
			
			for (BigDecimal id: lstIdsp){
				lstIdsPol.add(id.toString());
			}
				
			return lstIdsPol;		
			
		} 
		catch (Exception ex) {
			logger.info("getListaIdsRenovables - Se ha producido un error durante el acceso a la base de datos: " , ex);
	
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<GruposNegocio> getGruposNegocio(final boolean listaGenerico) {
		Session session = obtenerSession();
		List<GruposNegocio> lista = new ArrayList<GruposNegocio>();
		try {
			Criteria criteria = session.createCriteria(GruposNegocio.class);
			if (!listaGenerico) {
				criteria.add(Restrictions.not(Restrictions.eq("grupoNegocio", '9')));
			}
			lista = criteria.list();
		}catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: ",ex);
		}
		return lista;
	}
}