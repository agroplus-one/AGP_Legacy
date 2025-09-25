package com.rsi.agp.dao.models.mtoinf;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.InformeFilter;
import com.rsi.agp.core.jmesa.sort.InformeSort;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.mtoinf.Informe;


public class MtoInformeDao extends BaseDaoHibernate implements IMtoInformeDao {
	
	private DataSource dataSource;
	private LobHandler lobHandler;
	
	@Override
	public int getInformesCountWithFilter(final InformeFilter filter, final String cadenaCodigosLupas) {
		logger.debug("init - [MtoInformeDao] getInformesCountWithFilter");
		Session session = obtenerSession();
		
		// Monta el select, incluyendo los joins necesarios si se filtra por visibilidad por usuarios o entidades
		String sql = filter.getSqlInnerJoin(cadenaCodigosLupas);
		// Monta el where
		sql += filter.getSqlWhere(cadenaCodigosLupas);
		
		logger.debug("end - [MtoInformeDao] getInformesCountWithFilter");
		// Ejecuta la sentencia y devuelve el count
		return ( (BigDecimal)session.createSQLQuery(sql).list().get(0) ).intValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<Informe> getInformesWithFilterAndSort(
			final InformeFilter filter, final InformeSort sort, final int rowStart, final int rowEnd,
			final String cadenaCodigosLupas) throws BusinessException {
		
	try{
		logger.debug("init - [MtoInformeDao] getInformesWithFilterAndSort");
			List<Informe> applications = (List<Informe>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(Informe.class);
        		// Filtro
                criteria = filter.execute(criteria);
                // Filtro por lista de códigos
                criteria = filter.executeCriteriaListaCodigos(criteria, cadenaCodigosLupas);
                // Ordenación
                criteria = sort.execute(criteria);
                // Primer registro
                criteria.setFirstResult(rowStart);
                // Número máximo de registros a mostrar
                criteria.setMaxResults(rowEnd - rowStart);
                // Devuelve el listado de informes
                return criteria.list();
            }
        });
		logger.debug("end - [MtoInformeDao] getInformesWithFilterAndSort");
        return applications;
		}catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
		}
	}
	
	public Informe getInformeOculto(BigDecimal idOculto) throws DAOException{
		
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(Informe.class);
			criteria.add(Restrictions.eq("oculto", idOculto));
			List<Informe> inf = criteria.list();
			
			if (inf != null && inf.size() == 1) {
				return inf.get(0);
			}
			else {
				throw new DAOException("La consulta para obtener el informe con el idOculto " + idOculto + " devuelve más de un registro");
			}
		} 
		catch (Exception e) {
			throw new DAOException("Ocurrió un error al obtener el informe con el idOculto " + idOculto);
		}
	}
	
	@Override
	public List<Long> getInformesGenCountWithFilter(final InformeFilter filter, final Usuario usuario) {
		logger.debug("init - [MtoInformeDao] getInformesGenCountWithFilter");
		String consulta = "";
		
		// Compone la sql necesaria para obtener el número de elementos que se ajustan al filtro de búsqueda
		StringBuilder sql = new StringBuilder (filter.getSqlLeftJoinGen()).append(filter.getSqlWhereGen(usuario));
		
		//DAA 19/02/2013  sumamos que sólo lo puedan ver usuarios que pertenezcan a la misma entidad
		//en el caso de que lo haya creado un perfil 1 o que sólo lo puedan ver usuarios que pertenezcan 
		//al grupo de entidades en caso de perfil 5.
		
		if (!filter.isPerfil0()) {
			int[] controlPerfiles = {Constants.COD_PERFIL_0,Constants.COD_PERFIL_1,Constants.COD_PERFIL_5};
			for(int i=0; i<controlPerfiles.length; i++){
				if(controlPerfiles[i]==Constants.COD_PERFIL_0){
					consulta += sql.toString() + " and usu.tipousuario = "+Constants.COD_PERFIL_0+" union ";
				}
				if(controlPerfiles[i]==Constants.COD_PERFIL_1){
					consulta += sql.toString() + " and usu.tipousuario= "+Constants.COD_PERFIL_1+" and usu.codentidad = "+ usuario.getOficina().getEntidad().getCodentidad() +" union ";
				}
				if(controlPerfiles[i]==Constants.COD_PERFIL_5){
					if(!usuario.getListaCodEntidadesGrupo().isEmpty()){
						consulta += sql.toString() + " and usu.tipousuario= "+Constants.COD_PERFIL_5+" and usu.codentidad in "+ StringUtils.toValoresSeparadosXComas(usuario.getListaCodEntidadesGrupo(),false);
					}else{
						consulta += sql.toString() + " and usu.tipousuario= "+Constants.COD_PERFIL_5+" and usu.codentidad in ("+ usuario.getOficina().getEntidad().getCodentidad() +")";
					}
					
				}
			}
		}else{
			consulta = sql.toString();
		}
		logger.debug("Consulta = " + consulta);
		
		logger.debug("end - [MtoInformeDao] getInformesGenCountWithFilter");
		return ((List<Long>)obtenerSession().createSQLQuery(consulta).list());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Collection<Informe> getInformesGenWithFilterAndSort(
			final InformeSort sort, final int rowStart, final int rowEnd, final List<Long> lstIdsInforme,final InformeFilter informeFilter) throws BusinessException {
		
	try{
		logger.debug("init - [MtoInformeDao] getInformesWithFilterAndSort");
			List<Informe> applications = (List<Informe>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(Informe.class);
                // Ordenación
                criteria = sort.execute(criteria);
              // Filtro
                criteria = informeFilter.execute(criteria);
                
                // Primer registro
                criteria.setFirstResult(rowStart);
                // Número máximo de registros a mostrar
                criteria.setMaxResults(rowEnd - rowStart);
                //Si la lista de ids de informe está informada se incluye en el Criteria
                if (lstIdsInforme != null && lstIdsInforme.size()>0) {
                	criteria.add(Restrictions.in("id", lstIdsInforme));
                }
                
                // Devuelve el listado de informes
                return criteria.list();
            }
        });
		logger.debug("end - [MtoInformeDao] getInformesWithFilterAndSort");
        return applications;
		}
	catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
		}
	}
	
	/**
	 * Chequea si existe un informe con el mismo nombre
	 * @return devuelve true si existe
	 */
	
	public boolean checkInformeExists(String nombre,Long idInforme) throws DAOException {
		
		List<Informe> lstInformes = new ArrayList<Informe>();
		Session session = obtenerSession();
		boolean informeExists = false;
	
		try {			
				Criteria criteria = session.createCriteria(Informe.class);
				criteria.add(Restrictions.eq("nombre", nombre));
				if (null != idInforme){
					criteria.add(Restrictions.ne("id", idInforme));
				}
				lstInformes = criteria.list();
				 
				 if (!lstInformes.isEmpty()) {	
					 Informe inf = lstInformes.get(0);
					 
					 if (inf.getNombre() != null) { informeExists = true; }
				 }		
			
		} catch (Exception e) {			
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);		
		}
	
		return informeExists;		
	}
	
	/**
	 * Método que actualiza el Blob en la tabla tb_mtoinf_informes con la sql pasada por parametro
	 */
	public void actualizaConsultaInforme(final String sql, final Long idInforme) throws DAOException {
    	logger.info("init - actualizaConsultaInforme");
    	try {	
			if (!StringUtils.nullToString(sql).equals("")) {
				JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
				jdbcTemplate.update(
					"UPDATE tb_mtoinf_informes SET consulta=? WHERE ID="
					+ idInforme, new PreparedStatementSetter() {
						public void setValues(PreparedStatement ps)
								throws SQLException {
							lobHandler.getLobCreator().setBlobAsBytes(ps, 1, sql.getBytes());
						}
					});
			}
    	}catch(Exception ex){
    		logger.error("Se ha producido un error durante el acceso a la base de datos ",ex);
    		throw new DAOException("Se ha producido un error en acceso a la BBDD", ex);
    	}
    	logger.info("end - actualizaConsultaInforme");
    }
	
	@Override
	public List<Long> getListIdsInformeByUsuario(String codUsuario){
		List<Long> lstIdsInforme = new ArrayList<Long>();
		try {
			String sql ="select * from tb_mtoinf_informes_usuario usu where codusuario = '"+codUsuario+"'";
			Session session = obtenerSession();
			List resultado = session.createSQLQuery(sql).list();
				
			for (int j = 0; j < resultado.size(); j++) {
				Object[] registro = (Object[]) resultado.get(j);
				String idInforme = ((BigDecimal)registro[0]).toString();
				if (!lstIdsInforme.contains(Long.parseLong(idInforme))){
					lstIdsInforme.add(Long.parseLong(idInforme));
				}
			}
			return lstIdsInforme;
		} catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
		}
		return lstIdsInforme;
	}
	
	/**
	 * Realiza la consulta de la sql pasada por parametro
	 * @return devuelve el listado de la consulta
	 */
	public List<Object> getConsulta(String sql){
		List<Object> listadoInforme = new ArrayList<Object>();
		try {
			Session session = obtenerSession();
			listadoInforme = (List<Object>)session.createSQLQuery(sql).list();
			
			return listadoInforme;
		} catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
		}
		return listadoInforme;
	}
	

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setLobHandler(LobHandler lobHandler) {
		this.lobHandler = lobHandler;
	}

	/** DAA 20/02/2013 A partir de una select sacamos el numero de registros que devuelve
	 *  @param sql
	 * 
	 */
	public int getCountNumRegistros(String consulta) {
		logger.debug("init - [MtoInformeDao] getInformesCountWithFilter");
		logger.debug("Valor de consulta:"+consulta);
		Session session = obtenerSession();
		List list = session.createSQLQuery(consulta).list();
		if (list != null && list.size()>0) {
			return ((BigDecimal) session.createSQLQuery(consulta).list().get(0) ).intValue();
		}else {
			return 0;
		}
	}

		
}
