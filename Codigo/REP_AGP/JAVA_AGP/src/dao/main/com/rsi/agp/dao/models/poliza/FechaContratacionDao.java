package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.CollectionUtils;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cpl.FechaContratacionAgricola;
import com.rsi.agp.dao.tables.cpl.MascaraFechaContrataAgricola;
import com.rsi.agp.dao.tables.cpl.gan.FechaContratacion;

/**
 * 
 * DAO
 * Clase para la validación de fechas de contratación de polizas, modulos y  parcelas 
 * @author 
 *
 */
public class FechaContratacionDao extends BaseDaoHibernate implements IFechaContratacionDao {

	/**
	 * Valida si el modulo esta dentro del periodo de contratación
	 * @return resultado de la validación (true or false)
	 */
	public boolean validarPorModulo(List<BigDecimal> listCultivos, String codmodulo , Long lineaseguroid) throws DAOException {
		
		boolean result = false;
		boolean hasFiltroCultivo = true;		
		
		// si viene el 999 en listCultivos no filtro pot cultivo
		// si no viene el 999 lo a�ado y filtro por cultivo
		if(listCultivos.contains(new BigDecimal(999))){
			hasFiltroCultivo = false;
		}else{	
			hasFiltroCultivo = true;
			listCultivos.add(new BigDecimal(999));
		}

		try {
			
			Date fechaMasAlta = getFechaContratacion("max", listCultivos, codmodulo ,lineaseguroid, hasFiltroCultivo);
			Date fechaMasBaja = getFechaContratacion("min", listCultivos, codmodulo ,lineaseguroid, hasFiltroCultivo);

			if(fechaMasAlta != null && fechaMasBaja != null){
				//comprobamos que la fec
				GregorianCalendar gc = new GregorianCalendar();
				int year = gc.get(Calendar.YEAR);
				int month = gc.get(Calendar.MONTH);
				int day = gc.get(Calendar.DAY_OF_MONTH);
				
				GregorianCalendar gcSinHora = new GregorianCalendar(year, month, day);
				Date hoy = gcSinHora.getTime();
				
				if(fechaMasAlta.compareTo(hoy) >= 0 && fechaMasBaja.compareTo(hoy) <= 0){
					result = true;
				}
			}
		} catch (Exception ex) {
			logger.error(ex);
		    throw new DAOException("[FechaContratacionDao]validarPorModulo() - error", ex);
		} 

		return result;	
	}
	
	/**
	 * valida si la parcela esta dentro del periodo de contratación
	 * @return resultado de la validación (true or false)
	 */
	public boolean validarPorParcela()throws DAOException {
		boolean result = false;
		
		try {
			
			
			
		} catch (Exception ex) {
			logger.error(ex);
		    throw new DAOException("[FechaContratacionDao]validarPorParcela() - error",ex);
		}
		
		return result;
	}
	
	/**
	 * Validate capital asegurado - si dentro de ambito
	 */
	
	
	
	/**
	 * valida si las parcelas estan dentro del periodo de contratación
	 * @return resultado de la validación (true or false)
	 */
	public boolean validarPorParcelas()throws DAOException {
		boolean result = false;
		
		try {
			
			
			
		} catch (Exception ex) {
			logger.error(ex);
		    throw new DAOException("[FechaContratacionDao]validarPorParcelas() - error",ex);
		}
		
		return result;
	}

	/**
	 * valida si la poliza esta dentro del periodo de contratación
	 * @return resultado de la validación (true or false)
	 */
	public boolean validarPorPolizas()throws DAOException {
		boolean result = false;
		
		try {
			
			
			
		} catch (Exception ex) {
			logger.error(ex);
		    throw new DAOException("[FechaContratacionDao]validarPorPolizas() - error",ex);
		}
		
		return result;
	}
	
	/**
	 * 
	 * Devuelve la mayor fecha de ultimodiapago o la menor de feciniciocontrata filtrando
	 * por codmodulo, lineaseguroid y una lista de cultivos que se obtienen filtrando por la clase
	 * seleccionado en la poliza.
	 */
	private Date getFechaContratacion(String maxOrMin, List<BigDecimal> listCultivos, String codmodulo,
			Long lineaseguroid, boolean hasFiltroCultivo) {
		
		Date date = null;
		
		try {

			Session session = obtenerSession();
			StringBuilder query = new StringBuilder();

			if (maxOrMin.equals("max")) {
				query.append("SELECT max(fca.ultimodiapago)  ");
				query.append("FROM O02AGPE0.TB_SC_C_FEC_CONTRAT_AGR fca ");
				query.append("WHERE ");

				if (hasFiltroCultivo) {
					query.append("fca.codcultivo in (:listCultivos_) AND ");
				}

				query.append("fca.codmodulo=:modulo_ AND ");
				query.append("fca.lineaseguroid=:lineaseguroid_");
			} else {
				query.append("SELECT min(fca.feciniciocontrata)  ");
				query.append("FROM O02AGPE0.TB_SC_C_FEC_CONTRAT_AGR fca ");
				query.append("WHERE ");

				if (hasFiltroCultivo) {
					query.append("fca.codcultivo in (:listCultivos_) AND ");
				}

				query.append("fca.codmodulo=:modulo_ AND ");
				query.append("fca.lineaseguroid=:lineaseguroid_");
			}

			SQLQuery sql = session.createSQLQuery(query.toString());

			if (hasFiltroCultivo) {
				sql.setParameterList("listCultivos_", listCultivos);
			}

			sql.setParameter("modulo_", codmodulo);
			sql.setParameter("lineaseguroid_", lineaseguroid);

			date = (Date) sql.list().get(0);

		} catch (Exception ex) {
			logger.error(
					"[PolizaDao] getFechaContratacion() - Se ha producido un error en la BBDD: " + ex.getMessage());
		}
		return date;
	}
	
	@SuppressWarnings("unused")
	private boolean isInListCod( List<BigDecimal> listcods, String cod){
		boolean result = false;
		
		for(int i=0; i < listcods.size(); i++){
			if(listcods.get(i).toString().equals(cod)){
			    result = true;
			    break;
			}
		}
		return result;
	}
	/**
	 * Obtiene la max fechaFinContratacion a partir de los datos 
	 * de una polizas
	 * @return 
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Date getFechaContratacion(List<BigDecimal> listCultivos,
			List<BigDecimal> listVariedad, List<BigDecimal> listProvs,
			List<BigDecimal> listCmc, List<BigDecimal> listTerminos,
			List<Character> listSubTerminos, String codmodulo,
			Long lineaseguroid, Object[] cp, List<BigDecimal> listCicloCultivo,
			List<BigDecimal> listSisCultivo, 
			List<BigDecimal> listtipoPlan, List<BigDecimal> listsistProt,
			List<BigDecimal> listTipoCapital,String campo) throws Exception {
		
		try{
			logger.debug("getFechaContratacion()");
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(FechaContratacionAgricola.class);
			ProjectionList proj = Projections.projectionList();
			//proj.add(Projections.min("fecfincontrata"));
			proj.add(Projections.property(campo));
			criteria.setProjection(proj);
			
			criteria.createAlias("modulo", "modulo", CriteriaSpecification.LEFT_JOIN);
			criteria.createAlias("variedad", "variedad", CriteriaSpecification.LEFT_JOIN);
			criteria.createAlias("riesgoCubierto", "riesgoCubierto", CriteriaSpecification.LEFT_JOIN);
			
			
			criteria.add(Restrictions.eq("modulo.id.codmodulo", codmodulo));
			criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
			
			criteria.add(Restrictions.in("variedad.id.codcultivo", listCultivos));
			criteria.add(Restrictions.in("variedad.id.codvariedad", listVariedad));
			criteria.add(Restrictions.in("codprovincia", listProvs));
			criteria.add(Restrictions.in("codcomarca", listCmc));
			criteria.add(Restrictions.in("codtermino", listTerminos));
			criteria.add(Restrictions.in("subtermino", listSubTerminos));
			if (listCicloCultivo.size()>0){
				criteria.createAlias("cicloCultivo", "cicloCultivo", CriteriaSpecification.LEFT_JOIN);
				if (CollectionUtils.contains(listCicloCultivo.iterator(), BigDecimal.ZERO)) {
					criteria.add(Restrictions.or(Restrictions.isNull("cicloCultivo.codciclocultivo"),
							Restrictions.in("cicloCultivo.codciclocultivo", listCicloCultivo)));
				} else {
					criteria.add(Restrictions.in("cicloCultivo.codciclocultivo", listCicloCultivo));
				}			
			}
			if (listSisCultivo.size()>0){
				criteria.createAlias("sistemaCultivo", "sistemaCultivo", CriteriaSpecification.LEFT_JOIN);
				criteria.add(Restrictions.in("sistemaCultivo.codsistemacultivo", listSisCultivo));
			}
			if (listtipoPlan.size()>0){
				criteria.createAlias("tipoPlantacion", "tipoPlantacion", CriteriaSpecification.LEFT_JOIN);
				criteria.add(Restrictions.in("tipoPlantacion.codtipoplantacion", listtipoPlan));
			}
			if (listsistProt.size()>0){
				criteria.createAlias("sistemaProteccion", "sistemaProteccion", CriteriaSpecification.LEFT_JOIN);
				criteria.add(Restrictions.in("sistemaProteccion.codsistemaproteccion", listsistProt));
			}
			if (listTipoCapital.size()>0){
				criteria.createAlias("tipoCapital", "tipoCapital", CriteriaSpecification.LEFT_JOIN);
				criteria.add(Restrictions.in("tipoCapital.codtipocapital", listTipoCapital));
			}
				
			if (cp != null){
				
				if (cp[2]!=null){
					logger.debug("riesgoCubierto.id.codriesgocubierto  "+ cp[2]);
					if (cp[2].equals(new BigDecimal(-1))){
						criteria.add(Restrictions.eq("riesgocubiertoelegible", 'S'));
					}else if (cp[2].equals(new BigDecimal(-2))){
						criteria.add(Restrictions.eq("riesgocubiertoelegible", 'N'));
					}
				}
				
				if (cp[0]!=null){
					logger.debug("conceptoPpalModulo.codconceptoppalmod  "+ cp[0]);
					criteria.add(Restrictions.eq("conceptoPpalModulo.codconceptoppalmod", cp[0]));
				}
				if (cp[1]!=null){
					logger.debug("riesgoCubierto.id.codriesgocubierto  "+ cp[1]);
					criteria.add(Restrictions.eq("riesgoCubierto.id.codriesgocubierto", cp[1]));
				}	
			}
			
			criteria.addOrder(Order.asc("variedad.id.codcultivo"))
					.addOrder(Order.asc("variedad.id.codvariedad"))
					.addOrder(Order.asc("codprovincia"))
					.addOrder(Order.asc("codtermino"))
					.addOrder(Order.asc("subtermino"))
					.addOrder(Order.asc("codcomarca"));
			
			List f = criteria.list();
			if (f.size()>0){
				logger.debug((Date) f.get(0));
				return (Date) f.get(0);
			}
			
		} catch (Exception ex) {
			logger.error("getFechaContratacion() - Se ha producido un error en la BBDD: " + ex.getMessage());
			throw ex;
		}	
		return null;	
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object[] getDatosComparativas(Long idpoliza) throws Exception {
		
		logger.debug("INICIO getDatosComparativas()");
		Object[] cp = new Object[3];
		try{
			
			Session session = obtenerSession();
			
			String sql = "select distinct codconceptoppalmod, codriesgocubierto, codvalor "
					+ " from o02agpe0.tb_comparativas_poliza  where idpoliza = "+ idpoliza 
					+ " and codconcepto =" + ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO;
			
			logger.debug("SQL ******** " + sql);
			List list = session.createSQLQuery(sql).list();
	
			if (list.size()>0){
					logger.debug("Devuelve resultados SI");
				 cp = (Object[])list.get(0);
				 
			}
			
		} catch (Exception ex) {
			logger.error("getDatosComparativas() - Se ha producido un error en la BBDD: " + ex.getMessage());
			throw ex;
			
		}
		return cp;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MascaraFechaContrataAgricola> getConceptosMascaras(List<BigDecimal> listCultivos,
			List<BigDecimal> listVariedad, List<BigDecimal> listProvs,
			List<BigDecimal> listCmc, List<BigDecimal> listTerminos,
			List<Character> listSubTerminos, Long lineaseguroid, String codmodulo) throws Exception {
		
		List<MascaraFechaContrataAgricola> f = new ArrayList<MascaraFechaContrataAgricola>();

		try{
			logger.debug("getConceptosMascaras()");
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(MascaraFechaContrataAgricola.class);
			
			criteria.add(Restrictions.eq("modulo.id.codmodulo", codmodulo));
			criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
			
			criteria.add(Restrictions.in("id.codcultivo", listCultivos));
			criteria.add(Restrictions.in("id.codvariedad", listVariedad));
			criteria.add(Restrictions.in("id.codprovincia", listProvs));
			criteria.add(Restrictions.in("id.codcomarca", listCmc));
			criteria.add(Restrictions.in("id.codtermino", listTerminos));
			criteria.add(Restrictions.in("id.subtermino", listSubTerminos));			
				
			logger.debug("modulo.id.codmodulo  "+ codmodulo);
			logger.debug("id.lineaseguroid  "+ lineaseguroid);
			logger.debug("id.codcultivo  " + listCultivos.toString());
			logger.debug("id.codvariedad  "+ listVariedad);
			logger.debug("id.codprovincia  " + listProvs);
			logger.debug("id.codcomarca  " + listCmc);
			logger.debug("id.codtermino  "+listTerminos);
			logger.debug("id.subtermino  " + listSubTerminos);
		
			logger.debug(criteria.toString());
			
			f = criteria.list();
			
		} catch (Exception ex) {
			logger.error("getConceptosMascaras() - Se ha producido un error en la BBDD: " + ex.getMessage());
			throw ex;
		}
		return f;
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public Object getFechaContratacionGan(List<Long> listEspecies,
			List<Long> listRegimenes, List<Long> listTipoCapital,
			List<Long> listGrupoRaza, List<Long> listTipoAnimal,
			List<Long> listProvs, List<Long> listCmc,
			List<Long> listTerminos, List<Character> listSubTerminos,
			String codmodulo, Long lineaseguroid,String campo)throws Exception{
		
		
		try{
			logger.debug("getFechaFinContratacionGan()");
			
			if (!listEspecies.contains(new Long(999)))
				listEspecies.add(new Long(999));
			if (!listRegimenes.contains(new Long(999)))
				listRegimenes.add(new Long(999));
			if (!listTipoCapital.contains(new Long(999)))
				listTipoCapital.add(new Long(999));
			if (!listGrupoRaza.contains(new BigDecimal(999)))
				listGrupoRaza.add(new Long(999));
			if (!listTipoAnimal.contains(new Long(999)))
				listTipoAnimal.add(new Long(999));
			
			Criteria criteria = getCriteria(listEspecies,
					listRegimenes, listTipoCapital, listGrupoRaza, listTipoAnimal,
					listProvs, listCmc, listTerminos,listSubTerminos, codmodulo, lineaseguroid,campo);
		
			logger.debug(" DEBUG CONSULTA: " +criteria.toString());
			Date f= null;
			f = (Date)criteria.uniqueResult();
			if (f == null){
				listSubTerminos.add('9');
				Criteria criteria2 = getCriteria(listEspecies,
						listRegimenes, listTipoCapital, listGrupoRaza, listTipoAnimal,
						listProvs, listCmc, listTerminos,listSubTerminos, codmodulo, lineaseguroid,campo);
				logger.debug(" DEBUG CONSULTA: " +criteria2.toString());
				f = (Date)criteria2.uniqueResult();
				if (f == null){
					listTerminos.add(new Long(999));
					Criteria criteria3 = getCriteria(listEspecies,
							listRegimenes, listTipoCapital, listGrupoRaza, listTipoAnimal,
							listProvs, listCmc, listTerminos,listSubTerminos, codmodulo, lineaseguroid,campo);
					logger.debug(" DEBUG CONSULTA: " +criteria3.toString());
					f = (Date)criteria3.uniqueResult();
					if (f == null){
						listCmc.add(new Long(99));
						Criteria criteria4 = getCriteria(listEspecies,
								listRegimenes, listTipoCapital, listGrupoRaza, listTipoAnimal,
								listProvs, listCmc, listTerminos,listSubTerminos, codmodulo, lineaseguroid,campo);
						logger.debug(" DEBUG CONSULTA: " +criteria4.toString());
						f = (Date)criteria4.uniqueResult();
						if (f == null){
							listProvs.add(new Long(99));
							Criteria criteria5 = getCriteria(listEspecies,
									listRegimenes, listTipoCapital, listGrupoRaza, listTipoAnimal,
									listProvs, listCmc, listTerminos,listSubTerminos, codmodulo, lineaseguroid,campo);
							
							logger.debug(" DEBUG CONSULTA: " +criteria5.toString());
							f = (Date)criteria5.uniqueResult();
						}
					}
				}
			}
				

					
		return f;
		} catch (Exception ex) {
			logger.error("getFechaContratacion() - Se ha producido un error en la BBDD: " + ex.getMessage());
			throw ex;
		}	
		
	}

	private Criteria getCriteria(List<Long> listEspecies,
			List<Long> listRegimenes, List<Long> listTipoCapital,
			List<Long> listGrupoRaza, List<Long> listTipoAnimal,
			List<Long> listProvs, List<Long> listCmc,
			List<Long> listTerminos, List<Character> listSubTerminos,
			String codmodulo, Long lineaseguroid,String campo) {

		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(FechaContratacion.class);
		ProjectionList proj = Projections.projectionList();
		proj.add(Projections.min(campo));
		criteria.setProjection(proj);
		
		criteria.createAlias("modulo", "modulo", CriteriaSpecification.LEFT_JOIN);
		criteria.add(Restrictions.eq("modulo.id.codmodulo", codmodulo));
		criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
		
		criteria.add(Restrictions.in("id.codespecie", listEspecies));
		criteria.add(Restrictions.in("id.codregimen", listRegimenes));
		criteria.add(Restrictions.in("id.codtipocapital", listTipoCapital));
		criteria.add(Restrictions.in("id.codgruporaza", listGrupoRaza));
		criteria.add(Restrictions.in("id.codtipoanimal", listTipoAnimal));
		
		criteria.add(Restrictions.in("id.codprovincia", listProvs));
		criteria.add(Restrictions.in("id.codcomarca", listCmc));
		criteria.add(Restrictions.in("id.codtermino", listTerminos));
		criteria.add(Restrictions.in("id.subtermino", listSubTerminos));
		
		return criteria;
	}	
}
