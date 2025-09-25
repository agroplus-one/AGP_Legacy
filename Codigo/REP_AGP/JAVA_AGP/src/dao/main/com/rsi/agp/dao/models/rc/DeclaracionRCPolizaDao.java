package com.rsi.agp.dao.models.rc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.copy.Asegurado;
import com.rsi.agp.dao.tables.poliza.Comunicaciones;
import com.rsi.agp.dao.tables.reduccionCap.Estado;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;

@SuppressWarnings("unchecked") 
public class DeclaracionRCPolizaDao extends BaseDaoHibernate implements IDeclaracionRCPolizaDao {
	
	/*public void eliminarDeclaracionModificacionPoliza(Long idAnexoModificacion) throws DAOException{
		this.delete(AnexoModificacion.class, idAnexoModificacion);
	}
	
	@Override
	public boolean listByIdPolizaBorradorYDefinitivo(Long idPoliza) throws DAOException{
		Session session = obtenerSession();
		List<BigDecimal> lstEstados = new ArrayList<BigDecimal>();
		lstEstados.add(Constants.ANEXO_MODIF_ESTADO_BORRADOR);
		lstEstados.add(Constants.ANEXO_MODIF_ESTADO_DEFINITIVO);
		try {
			Criteria criteria =	session.createCriteria(AnexoModificacion.class);
		
			criteria.add(Restrictions.eq("poliza.idpoliza", idPoliza));
			criteria.add(Restrictions.in("estado.idestado", lstEstados));
			criteria.add(Restrictions.eq("tipoEnvio", Constants.ANEXO_MODIF_TIPO_ENVIO_FTP));
			
			return (!criteria.list().isEmpty());
		} catch(Exception ex){
			throw new DAOException("Se ha producido un error listando las declaraciones de modificaci贸n de p贸liza", ex);
		} finally{
		}
	}
	
	@Override
	public List<AnexoModificacion> listarByIdPolizaBorradorYDefinitivo(Long idPoliza) throws DAOException {
		Session session = obtenerSession();
		List<BigDecimal> lstEstados = new ArrayList<BigDecimal>();
		lstEstados.add(Constants.ANEXO_MODIF_ESTADO_BORRADOR);
		lstEstados.add(Constants.ANEXO_MODIF_ESTADO_DEFINITIVO);
		try {
			Criteria criteria =	session.createCriteria(AnexoModificacion.class);
		
			criteria.add(Restrictions.eq("poliza.idpoliza", idPoliza));
			criteria.add(Restrictions.in("estado.idestado", lstEstados));
			
			return criteria.list();
		} catch(Exception ex){
			throw new DAOException("Se ha producido un error listando las declaraciones de modificaci贸n de p贸liza", ex);
		} finally{
		}
	}

	@Override
	public List<AnexoModificacion> listAnexModifByIdPoliza(Long idPoliza)throws DAOException {
		
		return findFiltered(AnexoModificacion.class,"poliza.idpoliza" ,idPoliza);
	}
	
	@Override
	public AnexoModificacion getAnexoModifById(Long idAnexo)
			throws DAOException {

		return (AnexoModificacion) get(AnexoModificacion.class, idAnexo);
	}
	
	public AnexoModificacion getAnexoModifById(Long idAnexo, boolean evict)
			throws DAOException {
		AnexoModificacion anM = null;
		if (evict){
			Session session = obtenerSession(); 
			try{
				anM =(AnexoModificacion) session.get(AnexoModificacion.class, idAnexo);
			    session.evict(anM);

			}
			catch(Exception ex){
			}
			finally{
			}
	
		}else{
			anM=getAnexoModifById(idAnexo);
		}
		return anM;
		
		
	}
	
	public List<Parcela> getParcelas(Long idAnexo)throws DAOException {		
		List<Parcela> parcelas = null;		
			Session session = obtenerSession(); 			
			try{		
				Criteria criteria = session.createCriteria(Parcela.class);
				criteria.add(Restrictions.eq("anexoModificacion.id", idAnexo));	
				parcelas= criteria.list();
			    for (Parcela pa:parcelas){
			    	this.evict(pa);
			    	getCapitalesAsegurados(pa);	
			    	getSIGPAC(pa);
			    }
			    return parcelas;
			}
			catch(Exception ex){
			}
			finally{
			}
	
		
		return parcelas;
		
		
	}
	
	private void getCapitalesAsegurados(Parcela parcela){
		List<CapitalAsegurado> lstCapAseg =null;
		Session session = obtenerSession(); 			
		try{		
			Criteria criteria = session.createCriteria(CapitalAsegurado.class);
			criteria.add(Restrictions.eq("parcela.idparcela", parcela.getId()));	
			lstCapAseg= criteria.list();
			parcela.getCapitalAsegurados().clear();
		    for (CapitalAsegurado cap:lstCapAseg){
		    	this.evict(cap);
		    	parcela.getCapitalAsegurados().add(cap);
		    	getDatosVariables(cap);
		    }
		}
		catch(Exception ex){
		}
		finally{
		}
		
	}
	
	private void getDatosVariables(CapitalAsegurado cap){
		List<CapitalDTSVariable> lstDatVar =null;
		Session session = obtenerSession(); 
		try{		
			Criteria criteria = session.createCriteria(CapitalAsegurado.class);
			criteria.add(Restrictions.eq("id", cap.getId()));	
			lstDatVar= criteria.list();
			cap.getCapitalDTSVariables().clear();
		    for (CapitalDTSVariable dat:lstDatVar){
		    	this.evict(dat);
		    	cap.getCapitalDTSVariables().add(dat);    	
		    }
		}
		catch(Exception ex){
		}
		finally{
		}
	}
	
	private void getSIGPAC(Parcela parcela){
		List<ParcelaAMSWZonifSIGPAC> lstSigPac =null;
		Session session = obtenerSession(); 			
		try{		
			Criteria criteria = session.createCriteria(ParcelaAMSWZonifSIGPAC.class);
			criteria.add(Restrictions.eq("parcela.idparcela", parcela.getId()));	
			lstSigPac= criteria.list();
			parcela.getParcelaSWZonifSIGPACs().clear();
		    for (ParcelaAMSWZonifSIGPAC sigpac:lstSigPac){
		    	this.evict(sigpac);
		    	parcela.getParcelaSWZonifSIGPACs().add(sigpac);
		    }
		}
		catch(Exception ex){
		}
		finally{
		}
	}
	/**
	 * Alta o modificacion de un anexo de modificacion
	 * 14/08/2013 U029769
	 * @param anexo
	 * @param codUsuario
	 * @param estado
	 * @param esAlta
	 * @return AnexoModificacion
	 * @throws DAOException
	 */
	public ReduccionCapital saveAnexoModificacion (ReduccionCapital reduccionCap ,String codUsuario,Estado estado,boolean esAlta) throws DAOException {
		try {
			Session sesion = this.obtenerSession();
			if (estado!= null) {
				reduccionCap.setEstado(estado);
			}
			/*Si es alta:Comprobamos si el objeto "comunicaciones" tiene id, 
			porque si lo tiene hay que quitarlo para que no de error al guardar*/
			if (esAlta) {
				if (reduccionCap.getComunicaciones() != null && reduccionCap.getComunicaciones().getIdenvio() == null){
					reduccionCap.setComunicaciones(null);
				}
			}
			sesion.saveOrUpdate(reduccionCap);
			
			this.evict(reduccionCap);
			
			return reduccionCap;
			
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}
	
	@Override
	public BigDecimal isEditableAMCuponCaducado (Long idAnexo, Long idPoliza) throws DAOException {
		
		Session session = obtenerSession();
		
		try {
		// Comprueba si existe alg煤n AM por cup贸n en provisional y con el cup贸n en activo para la p贸liza en cuesti贸n
		BigDecimal resultado = (BigDecimal) session.createSQLQuery (
					"SELECT COUNT(*) FROM TB_ANEXO_RED A, TB_ANEXO_RED_CUPON ARC " + 
				    "WHERE A.IDPOLIZA = " + idPoliza + " AND A.IDESTADO = " 
					+ Constants.REDUCCION_CAPITAL_ESTADO_BORRADOR+ 
				    " AND A.IDCUPON = ARC.ID AND ARC.ESTADO = " + Constants.AM_CUPON_ESTADO_ABIERTO).uniqueResult();
		
		// Si el resultado es diferente de 0 no se puede editar el AM
		if (resultado != null && (new BigDecimal (0)).compareTo(resultado) < 0) return new BigDecimal (1);
		
		} 
		catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
		
		// Si llega hasta aqu铆 el AM con cup贸n caducado es editable
		return new BigDecimal (0);
	}
	/*
	@Override
	public Asegurado getAseguradoCopy(Long idcopy) throws DAOException {
		Session session = obtenerSession();
		try {
			
			Criteria criteria =	session.createCriteria(Asegurado.class);
			criteria.add(Restrictions.eq("poliza.id", idcopy));
			
			return (Asegurado)criteria.uniqueResult();
			
		} catch(Exception ex){
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		} 
	}
	/** DAA 20/03/2013 
	 *  Recuperamos el Numsegsocial y el regimen social de A.M. anterior de la misma p贸liza
	 * /
	@SuppressWarnings("rawtypes")
	public List getSegSocialAnexoAnterior(Long idPoliza) throws DAOException {
		Session session = obtenerSession();
		List resultado = new ArrayList();
		try {
			String sql = "select a.numsegsocial,a.regimensegsocial " +
						"from tb_anexo_mod a left join tb_comunicaciones c on c.idenvio = a.idenvio where " +
						"a.numsegsocial is not null and a.regimensegsocial is not null and " +
						"a.idpoliza = "+ idPoliza +" and a.estado = 3 order by c.fecha_envio desc";
			List list = session.createSQLQuery(sql.toString()).list();
			
			if(list.size()>0){
				resultado.add(list.get(0));
			}
			

		} catch(Exception ex){
			throw new DAOException("No se ha podido obtener el numero de seg. Social para el Anexo ", ex);
		}
		 return resultado;
	}
	
	
	@Override
	public Comunicaciones getComunicaciones(BigDecimal idEnvio) throws DAOException {		
		try {
					
			return (Comunicaciones) get(Comunicaciones.class, idEnvio);
			
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	public List<com.rsi.agp.dao.tables.anexo.Estado> getEstadosAnexoModificacion() throws DAOException {
		try {
			
			return findAll(com.rsi.agp.dao.tables.anexo.Estado.class);
			
		} catch (Exception e) {
			
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
			
		}		
	}

	@Override
	public BigDecimal isEditableAMCuponCaducado (Long idAnexo, Long idPoliza) throws DAOException {
		
		Session session = obtenerSession();
		
		try {
		// Comprueba si existe alg煤n AM por cup贸n en provisional y con el cup贸n en activo para la p贸liza en cuesti贸n
		BigDecimal resultado = (BigDecimal) session.createSQLQuery (
					"SELECT COUNT(*) FROM TB_ANEXO_MOD A, TB_ANEXO_MOD_CUPON AMC " + 
				    "WHERE A.IDPOLIZA = " + idPoliza + "AND A.TIPO_ENVIO = '" + Constants.ANEXO_MODIF_TIPO_ENVIO_SW +"' AND A.ESTADO = " 
					+ Constants.ANEXO_MODIF_ESTADO_BORRADOR + 
				    " AND A.IDCUPON = AMC.ID AND AMC.ESTADO = " + Constants.AM_CUPON_ESTADO_ABIERTO).uniqueResult();
		
		// Si el resultado es diferente de 0 no se puede editar el AM
		if (resultado != null && (new BigDecimal (0)).compareTo(resultado) < 0) return new BigDecimal (1);
		
		// Comprueba si existe alg煤n AM por ftp enviado correcto en fecha posterior a la del anexo en cuesti贸n
		resultado = (BigDecimal) session.createSQLQuery (
					"SELECT COUNT(*) FROM TB_ANEXO_MOD A, TB_COMUNICACIONES COM " +
					"WHERE A.IDPOLIZA = " + idPoliza + " AND A.TIPO_ENVIO = '" + Constants.ANEXO_MODIF_TIPO_ENVIO_FTP + 
					"' AND A.ESTADO = " + Constants.ANEXO_MODIF_ESTADO_CORRECTO + " AND A.IDENVIO = COM.IDENVIO  AND COM.FECHA_ENVIO >= " +
					"(SELECT FECHA_ALTA FROM TB_ANEXO_MOD A WHERE A.ID =" + idAnexo + ")").uniqueResult();
		
		// Si el resultado es diferente de 0 no se puede editar el AM
		if (resultado != null && (new BigDecimal (0)).compareTo(resultado) < 0) return new BigDecimal (2);
		
		// Comprueba si existe alg煤n AM por sw enviado correcto en fecha posterior a la del anexo en cuesti贸n
		resultado = (BigDecimal) session.createSQLQuery (
					"SELECT COUNT(*)  FROM TB_ANEXO_MOD A, TB_ANEXO_MOD_SW_ENVIOS_CONF AEC " +
					"WHERE A.IDPOLIZA = " + idPoliza + " AND A.TIPO_ENVIO = '" + Constants.ANEXO_MODIF_TIPO_ENVIO_SW +
					"' AND A.ESTADO = " + Constants.ANEXO_MODIF_ESTADO_CORRECTO + " AND AEC.IDANEXO = A.ID AND AEC.FECHA >= " +
					"(SELECT FECHA_ALTA FROM TB_ANEXO_MOD A WHERE A.ID = " + idAnexo + ")").uniqueResult();
		
		// Si el resultado es diferente de 0 no se puede editar el AM
		if (resultado != null && (new BigDecimal (0)).compareTo(resultado) < 0) return new BigDecimal (3);
		
		} 
		catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
		
		// Si llega hasta aqu铆 el AM con cup贸n caducado es editable
		return new BigDecimal (0);
	}

	@Override
	public boolean isValidoAnexoRenovable(BigDecimal idPoliza) {
		
		Session session = obtenerSession();
		
		try {
			// Comprueba que todos los campos de gastos de la p髄iza renovable asociada a la p髄iza sobre la que se quiere dar de alta el anexo
			// est醤 correctamente informados
			BigDecimal resultado = (BigDecimal) session.createSQLQuery (
									"SELECT COUNT(*) " +
									"FROM TB_POLIZAS P, TB_POLIZAS_PCT_COMISIONES PCT " +
									"WHERE P.IDPOLIZA = " + idPoliza + " " +
									"AND P.IDPOLIZA = PCT.IDPOLIZA(+) " +
									"AND (PCTADMINISTRACION IS NULL OR PCTADQUISICION IS NULL OR " +
									     "PCTCOMMAX IS NULL OR PCTENTIDAD IS NULL OR PCTESMEDIADORA IS NULL)").uniqueResult();
			
			// Si el resultado es diferente de 0 no se puede dar de alta el AM
			return (resultado != null && (new BigDecimal (0)).compareTo(resultado) == 0);
		} 
		catch (Exception e) {
			logger.error("Se ha producido un error al comprobar si la renovable asociada tiene configurados los gastos correctamente", e);			
		}
		
		return false;
	}
	
	/**
	 * Alta o modificacion de las coberturas del anexo
	 * 21/06/2021 U028975
	 * @param anexo
	 * @param codUsuario
	 * @param estado
	 * @param esAlta
	 * @return AnexoModificacion
	 * @throws DAOException
	 * /
	public void saveCoberturasAnexo (Cobertura cob) throws DAOException {
		try {
			Session sesion = this.obtenerSession();
			sesion.saveOrUpdate(cob);
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}
	
	/* ESC-14671 ** MODIF TAM (26.07.2021) ** Inicio * /
	public BigDecimal getfilaRiesgoCubModulo(Long lineaseguroid, String codmodulo, BigDecimal cPmodulo,BigDecimal codRiesgoCub) throws Exception{
		logger.debug("init - [RiesgoCubiertoModuloDao] getfilaRiesgoCubModulo");
		Session session = obtenerSession();
		try{			
			 String sql = "select c.filamodulo from tb_sc_c_riesgo_cbrto_mod_g c where c.LINEASEGUROID = "+ lineaseguroid +
					 " and c.CODMODULO = " + codmodulo + " and c.CODCONCEPTOPPALMOD = " + cPmodulo + " and c.CODRIESGOCUBIERTO = " + codRiesgoCub;	       
			 SQLQuery query = session.createSQLQuery(sql);			 
			 
			 return ((BigDecimal) query.uniqueResult());
		} catch (Exception e) {
			logger.fatal("Error al obtener la descripcion del Riesgo cubierto elegido: getRiesgoCubiertosModulo()", e);
			throw e;
		}		
	}
	/* ESC-14671 ** MODIF TAM (26.07.2021) ** Inicio */

}
