package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.ReduccionCapitalFilter;
import com.rsi.agp.core.jmesa.service.impl.utilidades.ReduccionCapitalUtilidadesService;
import com.rsi.agp.core.jmesa.sort.ReduccionCapitalSort;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.reduccionCap.EstadoCuponRC;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.EnviosSWConfirmacion;
import com.rsi.agp.dao.tables.poliza.Comunicaciones;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.reduccionCap.AnexoRCSWComparativas;
import com.rsi.agp.dao.tables.reduccionCap.AnexoRCSWValidacion;
import com.rsi.agp.dao.tables.reduccionCap.CuponRC;
import com.rsi.agp.dao.tables.reduccionCap.EnviosSWAnulacionRC;
import com.rsi.agp.dao.tables.reduccionCap.EnviosSWConfirmacionRC;
import com.rsi.agp.dao.tables.reduccionCap.Estado;
import com.rsi.agp.dao.tables.reduccionCap.Parcela;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalBonifRecargos;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalDistribucionCostes;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalDistribucionCostesId;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalSWCalculo;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalSubvCCAA;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalSubvEnesa;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapitalUtilidades;
import java.lang.reflect.Field;

public class ReduccionCapitalDao extends BaseDaoHibernate implements IReduccionCapitalDao {
	
	@Override
	@SuppressWarnings("rawtypes")
	public boolean getCountgruposNegocio(Long lineaseguroId) throws DAOException {
		boolean result = false;
		Session session = obtenerSession();

		try {
			String sql = "select count(*) from TB_LINEAS_GRUPO_NEGOCIO where lineaseguroid = " + lineaseguroId;
			List list = session.createSQLQuery(sql).list();

			if (((BigDecimal) list.get(0)).intValue() > 0)
				result = true;
			else
				result = false;
		} catch (Exception excepcion) {
			logger.error("Error al comprobar los grupos de negocio ", excepcion);
			throw new DAOException("comprobar los grupos de negocio", excepcion);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ReduccionCapital> list(ReduccionCapital reduccionCapital) throws DAOException {
		Session session = obtenerSession();

		try{
			
			Criteria criteria =	session.createCriteria(ReduccionCapital.class,"reduccionCapital");
					
			//Se listaran solamente las reducciones de capital correspondientes a la poliza que se pase
			//como parametro.
			Long idPoliza = Constants.POLIZA_VALOR_VACIO;
			if(reduccionCapital.getPoliza().getIdpoliza()!= null){
				idPoliza = reduccionCapital.getPoliza().getIdpoliza();
			}
			
			if (reduccionCapital.getId() != null ) {
				ReduccionCapital rc = (ReduccionCapital) session.get(ReduccionCapital.class, reduccionCapital.getId());
				session.evict(rc);
			}
			
			if(!Constants.POLIZA_VALOR_VACIO.equals(idPoliza)){
				criteria.add(Restrictions.eq("reduccionCapital.poliza.idpoliza", idPoliza));
			}
			
			return criteria.list();
		
		} catch (Exception ex) {
			
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
			
		}
	}

	@Override
	public Poliza getPoliza(Long idPoliza) throws DAOException {
		try {
			
			return (Poliza) get(Poliza.class, idPoliza);
			
		} catch (Exception e) {
			
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
			
		}
		
	}

	@Override
	public Comunicaciones getComunicaciones(BigDecimal idEnvio) throws DAOException {		
		try {
					
			return (Comunicaciones) get(Comunicaciones.class, idEnvio);
			
		} catch (Exception e) {
			
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
			
		}
		
	}

	@Override
	public void eliminarReduccionCapital(ReduccionCapital rc) throws DAOException {
		try {
			
			List <AnexoRCSWValidacion> validaciones = this.getObjects(AnexoRCSWValidacion.class, "reduccionCapital", rc);
			List <EnviosSWConfirmacionRC> confirmaciones = this.getObjects(EnviosSWConfirmacionRC.class, "reduccionCapital", rc);
			List <RedCapitalSWCalculo> calculos = this.getObjects(RedCapitalSWCalculo.class, "reduccionCapital", rc);
			List<Object[]> distribucionCostes = this.getObjectsBySQLQuery("select * from tb_anexo_red_dist_costes where idanexo=" + rc.getId());
			
			for (Object[] ob : distribucionCostes) {
				RedCapitalDistribucionCostesId redID = new RedCapitalDistribucionCostesId();
				redID.setIdanexo(((BigDecimal)ob[0]).longValue());
				redID.setTipoDc(((BigDecimal)ob[1]).intValue());
				redID.setGrupoNegocio(((String)ob[ob.length-1]).charAt(0));
				
				List <RedCapitalDistribucionCostes> distCostes = this.getObjects(RedCapitalDistribucionCostes.class, "id", redID);
				
				for (RedCapitalDistribucionCostes dC : distCostes) {
					delete(dC);
				}
				
			}
			
			
			
			
			for (AnexoRCSWValidacion val : validaciones) {
				this.delete(val);
			}
			
			for (EnviosSWConfirmacionRC conf : confirmaciones) {
				this.delete(conf);
			}
			
			for (RedCapitalSWCalculo calc : calculos) {
				this.delete(calc);
			}
			
			
			
			delete(ReduccionCapital.class,rc.getId());
			
			
		} catch (Exception e) {
			
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
			
		}
	}

	@Override
	public ReduccionCapital getReduccionCapital(Long idReduccionCapital) throws DAOException {
		try {
			
			return (ReduccionCapital)get(ReduccionCapital.class, idReduccionCapital);
			
		} catch (Exception e) {
			
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
			
		}
	}
	
	@Override
	public CuponRC nuevoCupon(String idCupon) throws DAOException{
		Session session = obtenerSession();
		BigDecimal nuevaId = null;
		String sqlQuery = 
			"SELECT MAX(ID) FROM TB_ANEXO_RED_CUPON ";
		CuponRC cupon = new CuponRC();
		try{
			nuevaId = (BigDecimal)session.createSQLQuery(sqlQuery).uniqueResult();
			Long auxNuevaId = nuevaId.longValue();
			
			cupon.setId(auxNuevaId);
			EstadoCuponRC ec = new EstadoCuponRC();
			ec.setId(Constants.AM_CUPON_ESTADO_ABIERTO);
			cupon.setEstadoCupon(ec);
			cupon.setIdcupon(idCupon);
			
			
		} catch (Exception ex) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
			
		}finally{
		}
		
		return cupon;
	}

	@Override
	public void guardarReduccionCapital (ReduccionCapital reduccionCapital) throws DAOException {		
		Session session = obtenerSession();
		BigDecimal nuevaId = null;
		String sqlQuery = 
			"SELECT MAX(NUMANEXO) FROM TB_ANEXO_RED " +
			"WHERE IDPOLIZA = " + reduccionCapital.getPoliza().getIdpoliza();		
	
		try{
			if (reduccionCapital.getId() == null) {
				//ALTA
				//Se genera un nuevo identificador para la nueva reduccion de capital, dentro de la poliza.
				//Se obtiene el mayor de los identificadores existentes...
				nuevaId = (BigDecimal)session.createSQLQuery(sqlQuery).uniqueResult();
				if (nuevaId == null) {
					nuevaId = BigDecimal.ZERO;
				}
				Long auxNuevaId = nuevaId.longValue();				
				//... y se suma 1
				auxNuevaId++;
				nuevaId = BigDecimal.valueOf(auxNuevaId);				
				reduccionCapital.setNumAnexo(nuevaId);
			} else {
			}
			
			if (reduccionCapital.getId() != null ) {
				ReduccionCapital rc = (ReduccionCapital) session.get(ReduccionCapital.class, reduccionCapital.getId());
				
				session.evict(rc);
			}
			session.saveOrUpdate(reduccionCapital);
			
		} catch (Exception ex) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}finally{
		}
	}
	
	// P0079361 Inicio
	/**
	 * Damos de alta la Reduccion de Capital, obteniendo el ID
	 * 
	 * @param reduccionCapital
	 * @return
	 * @throws DAOException 
	 */
	public ReduccionCapital saveReduccionCapital(ReduccionCapital reduccionCapital) throws DAOException {
		try {
			Session sesion = this.obtenerSession();
			this.evict(reduccionCapital);
			sesion.saveOrUpdate(reduccionCapital);
			
			return reduccionCapital;
			
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}
	
	/**
	 * Damos de alta las Parcelas de la Reduccion de Capital, obtenemos el ID
	 * 
	 * @param parcelas
	 * @throws DAOException 
	 */
	public void saveParcelas(Set<Parcela> parcelas) throws DAOException {
		Session session = obtenerSession();
		//Long idParcela = new Long(-1);

		for (Parcela parcela : parcelas) {
			try {
				//idParcela = (Long) session.save(parcela);
				session.save(parcela);				
			} catch (Exception ex) {
				logger.error(ex);
				throw new DAOException("Se ha producido un error durante el guardado de las parcelas", ex);
			}
		}
	}
	
	/**
	 * Damos de alta la Dsitribucion de Costes de la Reduccion de Capital
	 * 
	 * @param distribucionCostes
	 * @throws DAOException 
	 */
	public void saveDistCostes(Set<RedCapitalDistribucionCostes> distribucionCostes) throws DAOException {
		Session sesion = this.obtenerSession();
		
		for (RedCapitalDistribucionCostes distCoste : distribucionCostes) {
			// 1. Guardamos las Bonificaciones/Recargos
			if (distCoste.getRedCapitalBonifRecargos() != null && !distCoste.getRedCapitalBonifRecargos().isEmpty()) {
				// Guardamos en BBDD las Bonificaciones/Recargos
				for (RedCapitalBonifRecargos bonifRec : distCoste.getRedCapitalBonifRecargos()) {
					this.evict(bonifRec);
					sesion.saveOrUpdate(bonifRec);
				}
			}
			// 2. Guardamos las Subvenciones Enesa
			if (distCoste.getRedCapitalSubvEnesas() != null && !distCoste.getRedCapitalSubvEnesas().isEmpty()) {
				// Guardamos en BBDD las Bonificaciones/Recargos
				for (RedCapitalSubvEnesa subvEnesa : distCoste.getRedCapitalSubvEnesas()) {
					this.evict(subvEnesa);
					sesion.saveOrUpdate(subvEnesa);
				}
			}
			
			// 3. Guardamos las Subvenciones CCAA
			if (distCoste.getRedCapitalSubvCCAAs() != null && !distCoste.getRedCapitalSubvCCAAs().isEmpty()) {
				// Guardamos en BBDD las Bonificaciones/Recargos
				for (RedCapitalSubvCCAA subvCCAA : distCoste.getRedCapitalSubvCCAAs()) {
					this.evict(subvCCAA);
					sesion.saveOrUpdate(subvCCAA);
				}
			}
			
			// 4. Guardamos la Distribucion de Costes
			try {
				this.evict(distCoste);
				sesion.saveOrUpdate(distCoste);
			} catch (Exception ex) {
				logger.error(ex);
				ex.printStackTrace();
				throw new DAOException("Se ha producido un error durante el guardado de la distribucion de costes", ex);
			}
		}
	}
	// P0079361 Fin
	
	@SuppressWarnings("rawtypes")
	public String getDescGrupoNegocio(Character grupoNegocio) {
		String descGr = "";
		Session session = obtenerSession();

		try {
			String sql = "select descripcion from TB_SC_C_GRUPOS_NEGOCIO where grupo_negocio = " + grupoNegocio;
			List list = session.createSQLQuery(sql).list();
			descGr = (String) list.get(0);

		} catch (Exception e) {
			logger.error("Error al obtener la descripcion del grupo de negocio ", e);

		}
		return descGr;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Estado> getEstadosReduccionCapital() throws DAOException {
		try {
			
			return findAll(Estado.class);
			
		} catch (Exception e) {
			
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
			
		}		
	}

	@Override
	public boolean tieneEstado(Long idReduccionCapital,Short estado) throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria =	session.createCriteria(ReduccionCapital.class);
			
			criteria.add(Restrictions.eq("id", idReduccionCapital));
			criteria.add(Restrictions.eq("estado.idestado", estado));
			criteria.setProjection(Projections.rowCount());
						
			return ((Integer)criteria.uniqueResult()).intValue() != 0;
			
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}
	
	
	public boolean tieneReduccionesCapital(Long idPoliza) throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria =	session.createCriteria(ReduccionCapital.class);
			
			criteria.createAlias("poliza", "poliza");
			criteria.add(Restrictions.eq("poliza.idpoliza", idPoliza));							
			criteria.setProjection(Projections.rowCount()); 
			return ((Integer)criteria.uniqueResult()).intValue() != 0;	
			
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	@Override
	public int getReduccionCapitalCountWithFilter(ReduccionCapitalFilter filter
			//P0079361
			,String fechadanioId, 
			String fechadanioIdHasta,
			String fechaEnvioId,
			String fechaEnvioIdHasta,
			String fechaEnvioPolId,
			String fechaEnvioPolIdHasta,
			String strTipoEnvioId
			//P0079361
			) throws BusinessException {

		try {
			log ("getReduccionCapitalCountWithFilter", "Inicio");
			
			Session session = obtenerSession();
			
			Criteria criteria = session.createCriteria(ReduccionCapitalUtilidades.class);
            // Filtro
			
			//P0079361
			buildCondicionesFechasDesdeHasta(criteria, fechadanioId, fechadanioIdHasta,
						fechaEnvioId, fechaEnvioIdHasta, fechaEnvioPolId, fechaEnvioPolIdHasta, strTipoEnvioId);
			//P0079361 Comentado por @Joauquin
			
            criteria = filter.execute(criteria);
            criteria.setProjection(Projections.rowCount()).uniqueResult();
            return ((Integer) criteria.uniqueResult()).intValue();	                
			
		}
		
		catch (Exception e) {
			log ("getReduccionCapitalCountWithFilter", "Se ha producido un error durante el acceso a la base de datos", e);
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
		}
	}
	
		//P0079361
		private void buildCondicionesFechasDesdeHasta(Criteria criteria, String fechadanioId, 
				String fechadanioIdHasta, String fechaEnvioId, String fechaEnvioIdHasta, 
				String fechaEnvioPolId, String fechaEnvioPolIdHasta, String strTipoEnvioId) {
				
			Date dateFechadanioId = WSUtils.parseoFechaRangos(fechadanioId);
			Date dateFechadanioIdHasta = WSUtils.parseoFechaRangos(fechadanioIdHasta);
			Date dateFechaEnvioId = WSUtils.parseoFechaRangos(fechaEnvioId); 
			Date dateFechaEnvioIdHasta = WSUtils.parseoFechaRangos(fechaEnvioIdHasta); 
			Date dateFechaEnvioPolId = WSUtils.parseoFechaRangos(fechaEnvioPolId);
			Date dateFechaEnvioPolIdHasta = WSUtils.parseoFechaRangos(fechaEnvioPolIdHasta);
			
			boolean isNuloFechaDanio = WSUtils.logicaObtenerNulosFechas(fechadanioId,fechadanioIdHasta);
			boolean isNuloFechaEnvio = WSUtils.logicaObtenerNulosFechas(fechaEnvioId,fechaEnvioIdHasta);
			boolean isNuloFechaEnvioPol = WSUtils.logicaObtenerNulosFechas(fechaEnvioPolId,fechaEnvioPolIdHasta);
			
			//F.DANO
            if(!isNuloFechaDanio) {
                Date dateFechadanioIdHMS = WSUtils.configHoraInit(dateFechadanioId);
                Date dateFechadanioIdHastaHMS = WSUtils.configHoraFin(dateFechadanioIdHasta);
                //criteria.add(Restrictions.between(ReduccionCapitalUtilidadesService.CAMPO_FEC_DANIOS, dateFechadanioIdHMS, dateFechadanioIdHastaHMS));        
                criteria.add(
                		Restrictions.sqlRestriction(
                			"TRUNC(this_.FDANIOS) BETWEEN ? AND ?", 
                			new Object[] {dateFechadanioIdHMS, dateFechadanioIdHastaHMS}, 
                			new org.hibernate.type.Type[] { Hibernate.DATE, Hibernate.DATE }	
                		)
                );
            }
            //F.DANO
            
            //F.ENVIO
            if(!isNuloFechaEnvio) {
                Date dateFechaEnvioIdHMS = WSUtils.configHoraInit(dateFechaEnvioId);
                Date dateFechaEnvioIdHastaHMS = WSUtils.configHoraFin(dateFechaEnvioIdHasta);
                //criteria.add(Restrictions.between(ReduccionCapitalUtilidadesService.CAMPO_FEC_ENVIO, dateFechaEnvioIdHMS, dateFechaEnvioIdHastaHMS));
                criteria.add(
                		Restrictions.sqlRestriction(
                			"TRUNC(this_.FENV) BETWEEN ? AND ?", 
                			new Object[] {dateFechaEnvioIdHMS, dateFechaEnvioIdHastaHMS}, 
                			new org.hibernate.type.Type[] { Hibernate.DATE, Hibernate.DATE }	
                		)
                );
            }
            //F.ENVIO
            
            //F.ENVIO.POL
            if(!isNuloFechaEnvioPol) {
                Date dateFechaEnvioPolIdHMS = WSUtils.configHoraInit(dateFechaEnvioPolId);
                Date dateFechaEnvioPolIdHastaHMS = WSUtils.configHoraFin(dateFechaEnvioPolIdHasta);
                //criteria.add(Restrictions.between(ReduccionCapitalUtilidadesService.CAMPO_FEC_ENVIO_POLIZA, dateFechaEnvioPolIdHMS,dateFechaEnvioPolIdHastaHMS));
                criteria.add(
                		Restrictions.sqlRestriction(
                			"TRUNC(this_.FENVPOL) BETWEEN ? AND ?", 
                			new Object[] {dateFechaEnvioPolIdHMS, dateFechaEnvioPolIdHastaHMS}, 
                			new org.hibernate.type.Type[] { Hibernate.DATE, Hibernate.DATE }	
                		)
                );
            }
            //F.ENVIO.POL
			
			//FTP o SW
			if(!Constants.STR_EMPTY.equals(strTipoEnvioId)) {
				if(Constants.ANEXO_MODIF_TIPO_ENVIO_FTP.equals(strTipoEnvioId.trim().toUpperCase())) {
					criteria.add(Restrictions.isNull(ReduccionCapitalUtilidadesService.CAMPO_NUMEROCUPON));					
				}else {
					criteria.add(Restrictions.eq(ReduccionCapitalUtilidadesService.CAMPO_NUMEROCUPON, strTipoEnvioId.trim()));	
				}
			}
			//FTP o SW
		}
		
		//P0079361
	
	@SuppressWarnings("unchecked")
	public Collection<ReduccionCapitalUtilidades> getReduccionCapitalWithFilterAndSort(final ReduccionCapitalFilter filter,
			final ReduccionCapitalSort sort, final int rowStart, final int rowEnd
			//P0079361
			,final String fechadanioId, 
			final String fechadanioIdHasta,
			final String fechaEnvioId,
			final String fechaEnvioIdHasta,
			final String fechaEnvioPolId,
			final String fechaEnvioPolIdHasta,
			final String strTipoEnvioId
			//P0079361
			) throws BusinessException {
		try {
			log ("getReduccionCapitalWithFilterAndSort", "Inicio");

			List<ReduccionCapitalUtilidades> applications = (List<ReduccionCapitalUtilidades>) 
					getHibernateTemplate().execute(new HibernateCallback() {
			
				public Object doInHibernate(final Session session) throws HibernateException, SQLException {
					Criteria criteria = session.createCriteria(ReduccionCapitalUtilidades.class);     
					
					//P0079361
					buildCondicionesFechasDesdeHasta(criteria, fechadanioId, fechadanioIdHasta,
							fechaEnvioId, fechaEnvioIdHasta, fechaEnvioPolId, fechaEnvioPolIdHasta, strTipoEnvioId);
					//P0079361
					
					// Filtro
					criteria = filter.execute(criteria);
					
					// Ordenacion
					criteria = sort.execute(criteria);
					if (rowStart != -1 && rowEnd != -1) {
				        // Primer registro
				        criteria.setFirstResult(rowStart);
				        // Número máximo de registros a mostrar
				        criteria.setMaxResults(rowEnd - rowStart);
				    }
					// Devuelve el listado de siniestros
					return criteria.list();
				}
		});
		logger.debug("-- aplications size -- "+ applications.size());
		return applications;
		
		}
		
		catch (Exception e) {
			log ("getReduccionCapitalWithFilterAndSort", "Se ha producido un error durante el acceso a la base de datos", e);
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
		}
	}
	
	public boolean isRCconParcelas(Long id) throws DAOException {
		
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(Parcela.class); 
			criteria.add(Restrictions.eq("reduccionCapital.id", id));
			if (criteria.list().size()>=1) {
				return true;
			}
			return false;
		}catch (Exception e) {
				log ("isRCconParcelas","Se ha producido un error durante el acceso a la base de datos", e);
				throw new DAOException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
		}	
			
	}
	/**
	 * Escribe en el log indicando la clase y el metodo.
	 * @param method
	 * @param msg
	 */
	private void log (String method, String msg) {
		logger.debug("ReduccionCapitalDao." + method + " - " + msg);
	}
	
	/**
	 * Escribe en el log indicando la clase, el metodo y la excepcion.
	 * @param method
	 * @param msg
	 * @param e
	 */
	private void log (String method, String msg, Throwable e) {
		logger.error("ReduccionCapitalDao." + method + " - " + msg, e);
	}

	@Override
	public CuponRC getCuponRCByIdCuponRC(Long idCuponRC) throws DAOException {
		try {
			
		    Session session = obtenerSession();

		    Criteria criteria = session.createCriteria(CuponRC.class, "cuponRC")
		            .add(Restrictions.eq("cuponRC.id", idCuponRC));

		    return (CuponRC) criteria.uniqueResult();
			
		} catch (Exception e) {
			
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
			
		}
	}
	
	@Override
	public ReduccionCapital getRCByIdRC(Long id) throws DAOException {
		try {
			
		    Session session = obtenerSession();

		    Criteria criteria = session.createCriteria(ReduccionCapital.class, "rc")
		            .add(Restrictions.eq("rc.id", id));

		    return (ReduccionCapital) criteria.uniqueResult();
			
		} catch (Exception e) {
			
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
			
		}
	}

	public EstadoCuponRC getEstadoCupon(Long idEstado) throws DAOException {
		EstadoCuponRC estadoCupon = new EstadoCuponRC();
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(EstadoCuponRC.class);
		 
    		criteria.add(Restrictions.eq("id", idEstado));
			
    		estadoCupon = (EstadoCuponRC)criteria.uniqueResult();
			
		}catch (Exception e){
			logger.error("Error al obtener el objeto estadosCuponRC", e);
			throw new DAOException(e);
		}
		return estadoCupon;
	}
	
	public void actualizar(ReduccionCapital redCap) throws DAOException {
		Session sesion = obtenerSession();
		
		try {
			Query queryUpdateRC = sesion.createSQLQuery("UPDATE o02agpe0.tb_anexo_red "
					+ "SET IDESTADO_AGRO = :idestadoagro "
					+ ", IDESTADO = :idestado"
					+ ", FECHAENVIO = :fechaenvio "
					+ "WHERE id = :id");
			
			queryUpdateRC.setParameter("idestadoagro", redCap.getEstadoAgroseguro().getCodestado());
			queryUpdateRC.setParameter("idestado", redCap.getEstado().getIdestado()); //en principio es este
			queryUpdateRC.setParameter("fechaenvio", redCap.getFechaenvio());
			queryUpdateRC.setParameter("id", redCap.getId());
		    
		    int result = queryUpdateRC.executeUpdate();
		    sesion.refresh(redCap);
		    
		    System.out.println("Filas actualizadas: " + result);
			
		} catch (Exception e) {
			logger.error("Error al actualizar la reduccion de capital", e);
			throw new DAOException("Error al actualizar la reduccion de capital",e);
		}
	}

	@Override
	public Clob getAcuseConfirmacion(long idRC) throws DAOException {
		try {
			Criteria criteria = getSession().createCriteria(EnviosSWConfirmacionRC.class);
			criteria.createAlias("reduccionCapital", "reduccionCapital");
			criteria.add(Restrictions.eq("reduccionCapital.id", idRC));
			criteria.addOrder(Order.desc("fecha"));

			List<EnviosSWConfirmacionRC> listaEnvios = criteria.list();
			if (listaEnvios != null && listaEnvios.size() > 0) {
				return listaEnvios.get(0).getEnviosSWXMLRCByIdxmlAcuse().getXml();
			}
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return null;
	}
}
