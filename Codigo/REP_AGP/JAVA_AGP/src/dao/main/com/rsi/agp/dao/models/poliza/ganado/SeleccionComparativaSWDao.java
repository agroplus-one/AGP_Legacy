package com.rsi.agp.dao.models.poliza.ganado;

import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.ModuloPolizaComparator;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cgen.DatosBuzonGeneral;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaSimple;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.ModuloPolizaId;

public class SeleccionComparativaSWDao extends BaseDaoHibernate implements ISeleccionComparativaSWDao {
	
	public static final Integer MODULOS_Y_COBERTURAS = Integer.valueOf(0);
	public static final Integer COBERTURAS_CONTRATADAS = Integer.valueOf(1);
	
	@SuppressWarnings("unchecked")
	@Override
	public void borrarComparativasNoElegidas(long idpoliza, long lineaseguroid, String[] infoModulos)
			throws DAOException {
		try {
			List<ModuloPoliza> mpLst = this.findFiltered(ModuloPoliza.class,
					new String[] { "id.idpoliza", "id.lineaseguroid" }, new Object[] { idpoliza, lineaseguroid }, null);
			Collections.sort(mpLst, new ModuloPolizaComparator());
			int numComparativa = 0;
			String codModulo = "";
			outer: for (ModuloPoliza mp : mpLst) {
				if (codModulo.equals(mp.getId().getCodmodulo())) {
					numComparativa++;
				} else {
					codModulo = mp.getId().getCodmodulo();
					numComparativa = 1;
				}
				for (String infoMod : infoModulos) {
					String[] info = infoMod.split("#");
					if (mp.getId().getCodmodulo().equals(info[0]) && numComparativa == Integer.valueOf(info[1])) {
						continue outer;
					}
				}
				this.delete(mp);
			}
		} catch (Exception e) {
			throw new DAOException("Error al actualizas las comparativas asociadas a la póliza " + idpoliza, e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Long actualizaModuloRenovable(long idpoliza, long lineaseguroid, String codModulo, int numComparativa,
			int renovable, Integer tipoAsegGanado) throws DAOException {
		Long result = null;
		ModuloPoliza mp = null;
		try {
			List<ModuloPoliza> mpLst = this.findFiltered(ModuloPoliza.class,
					new String[] { "id.idpoliza", "id.lineaseguroid", "id.codmodulo" },
					new Object[] { idpoliza, lineaseguroid, codModulo }, null);
			if (mpLst == null || (mpLst != null && mpLst.size() < numComparativa)) {
				// ES NUEVA COMPARATIVA
				mp = new ModuloPoliza();	
				ModuloPolizaId mpId = new ModuloPolizaId();
				mpId.setCodmodulo(codModulo);
				mpId.setIdpoliza(idpoliza);
				mpId.setLineaseguroid(lineaseguroid);
				mpId.setNumComparativa(this.getSecuenciaComparativa().longValue());
				mp.setId(mpId);
			} else {
				// ES COMPARATIVA YA EXISTENTE
				Collections.sort(mpLst, new ModuloPolizaComparator());
				for (int i = 1; i <= mpLst.size(); i++) {
					if (i == numComparativa) {
						mp = mpLst.get(i - 1);
						break;
					}
				}				
			}	
			if (mp != null) {
				mp.setRenovable(renovable);
				mp.setTipoAsegGanado(tipoAsegGanado);
				this.saveOrUpdate(mp);
				result = mp.getId().getNumComparativa();
			}
		} catch (Exception e) {
			throw new DAOException("Error al actualizas las comparativas asociadas a la póliza " + idpoliza, e);
		}
		return result;
	}

	@Override
	public void guardaListaComparativasPoliza(long idpoliza, List<ComparativaPolizaSimple> listCp) throws DAOException {

		Session session = this.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			
			// Elimina las comparativas de póliza anteriores
			Query queryDelete = session.createSQLQuery("delete o02agpe0.tb_comparativas_poliza where idpoliza =:idpoliza").setLong("idpoliza", idpoliza);
			queryDelete.executeUpdate();

			//BigDecimal secuencia = getSecuenciaComparativa();
			
			// Inserta la lista de nuevas comparativas
			for (ComparativaPolizaSimple cps : listCp) {
				
				logger.debug("IDPOLIZA :" + cps.getIdpoliza());
				logger.debug("LINEASEGUROID :" + cps.getLineaseguroid());
				logger.debug("CODMODULO :" + cps.getCodmodulo());
				logger.debug("FILAMODULO :" + cps.getFilamodulo());
				logger.debug("CODCONCEPTOPPALMOD :" + cps.getCpm());
				logger.debug("CODRIESGOCUBIERTO :" + cps.getRc());
				logger.debug("FILACOMPARATIVA :" + cps.getFilacomparativa());
				logger.debug("DESCVALOR :" + cps.getDescValor());
				logger.debug("CODCONCEPTO :" + cps.getConcepto());
				logger.debug("CODVALOR :" + cps.getValor());
				logger.debug("IDMODULO :" + cps.getNumComparativa());
				
				Query queryInsert = session.createSQLQuery("INSERT INTO o02agpe0.TB_COMPARATIVAS_POLIZA VALUES (:IDPOLIZA, :LINEASEGUROID, :CODMODULO, " +
						":FILAMODULO, :CODCONCEPTOPPALMOD, :CODRIESGOCUBIERTO, :FILACOMPARATIVA, :DESCVALOR, :CODCONCEPTO, :CODVALOR ,:IDMODULO)")
						.setLong("IDPOLIZA", cps.getIdpoliza())
						.setLong("LINEASEGUROID", cps.getLineaseguroid())
						.setString("CODMODULO", cps.getCodmodulo())
						.setLong("FILAMODULO", cps.getFilamodulo())
						.setLong("CODCONCEPTOPPALMOD", cps.getCpm())
						.setLong("CODRIESGOCUBIERTO", cps.getRc())
						.setLong("FILACOMPARATIVA", cps.getFilacomparativa())
						.setString("DESCVALOR", cps.getDescValor())
						.setLong("CODCONCEPTO", cps.getConcepto())
						.setLong("CODVALOR", cps.getValor())
						.setBigDecimal("IDMODULO", new BigDecimal(cps.getNumComparativa().toString()));
				
				queryInsert.executeUpdate();
			}
			
			tx.commit();
		} 
		catch (Exception e) {
			if (tx != null && tx.isActive()) tx.rollback();
			throw new DAOException("Error al actualiza la lista de comparativas de la póliza " + idpoliza, e);
		}
		finally {
			if (session != null) session.close();
		}
	}
	
	@Override
	public void guardaListaComparativasAnexo(long idanexo,	List<ComparativaPolizaSimple> listCp) throws DAOException {
		
		Session session = this.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			
			// Elimina las comparativas anteriores del anexo
			Query queryDelete = session.createSQLQuery("delete o02agpe0.tb_anexo_mod_coberturas where idanexo =:idanexo").setLong("idanexo", idanexo);
			queryDelete.executeUpdate();
			
			// Inserta la lista de nuevas comparativas
			for (ComparativaPolizaSimple cps : listCp) {
				Query queryInsert = session.createSQLQuery("INSERT INTO o02agpe0.TB_ANEXO_MOD_COBERTURAS VALUES (SQ_TB_ANEXO_MOD_COBERTURAS.NEXTVAL," +
						":IDANEXO, :CODCONCEPTOPPALMOD, :CODRIESGOCUBIERTO, :CODCONCEPTO, :CODVALOR, :CODMODIFICACION, :CODMODULO, :FILAMODULO, :FILACOMPARATIVA)")
						.setLong("IDANEXO", cps.getIdanexo())
						.setLong("CODCONCEPTOPPALMOD", cps.getCpm())
						.setLong("CODRIESGOCUBIERTO", cps.getRc())
						.setLong("CODCONCEPTO", cps.getConcepto())
						.setString("CODVALOR", String.valueOf(cps.getValor()))
						.setString("CODMODIFICACION", "M")
						.setString("CODMODULO", cps.getCodmodulo())
						.setInteger("FILAMODULO", cps.getFilamodulo())
						.setInteger("FILACOMPARATIVA", cps.getFilacomparativa())
						;
				
				queryInsert.executeUpdate();
			}
			
			tx.commit();
		} 
		catch (Exception e) {
			if (tx != null && tx.isActive()) tx.rollback();
			throw new DAOException("Error al actualiza la lista de comparativas del anexo " + idanexo, e);
		}
		finally {
			if (session != null) session.close();
		}
		
	}

	@Override
	public boolean aplicaTipoAseguradoGanado(long lineaseguroid) throws DAOException {
		
		try {
			Session session = obtenerSession();		

			Query queryCount = session.createSQLQuery("select count(*) from o02agpe0.tb_sc_oi_org_info where " +
													  "codconcepto=:codconcepto and " +
													  "coduso=:coduso and " +
													  "codubicacion=:codubicacion and " +
													  "lineaseguroid=:lineaseguroid")
					.setLong("codconcepto", CODCPTO_TIPO_ASEG_GANADO)
					.setLong("coduso", CODUSO_POLIZA)
					.setLong("codubicacion", CODUBICACION_CABECERA_DV)
					.setLong("lineaseguroid", lineaseguroid);
			
			return ((BigDecimal) queryCount.uniqueResult()).intValue() > 0;
			
		}
		catch (Exception e) {
			throw new DAOException("Error al comprobar si aplica el dv tipo asegurado de ganado al lineaseguroid " + lineaseguroid, e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DatosBuzonGeneral> obtenerListaTipoAseguradoGanado() throws DAOException {
		
		try {
			List<Object> lista = findFiltered(DatosBuzonGeneral.class, new String[] {"id.codcpto"}, new Object[] {new BigDecimal (CODCPTO_TIPO_ASEG_GANADO)}, "id.valorCpto");
			
			if (lista != null && !lista.isEmpty()) {
				return (List<DatosBuzonGeneral>) (Object)lista;
			}
			else {
				return null;
			}
			
		} 
		catch (Exception e) {
			throw new DAOException("Error al obtener la lista de tipos de asegurado de ganado del buzón general", e);
		}
		
		
	}

	@Override
	public boolean aplicaValidacionCapitalRetirada(long idAnexo) throws DAOException {
		try {
			Session session = obtenerSession();		

			Query queryCount = session.createSQLQuery("SELECT COUNT(*) " +
													  "FROM o02agpe0.TB_ANEXO_MOD_EXPLOTACIONES E, o02agpe0.TB_ANEXO_MOD_GRUPO_RAZA GR " +
													  "WHERE E.ID_ANEXO = :ID_ANEXO " +
													  "AND GR.ID_EXPLOTACION_ANEXO = E.ID " +
													  "AND GR.CODTIPOCAPITAL IN (SELECT GN.CODTIPOCAPITAL FROM o02agpe0.TB_SC_C_TIPO_CAPITAL_GRUPO_NEG GN " +
													  "WHERE GN.GRUPO_NEGOCIO='2')")
													  .setLong("ID_ANEXO", idAnexo);
			
			return ((BigDecimal) queryCount.uniqueResult()).intValue() > 0;
			
		}
		catch (Exception e) {
			throw new DAOException("Error al comprobar si aplica la validación de tipo de capital de retirada al anexo " + idAnexo, e);
		}
	}

	// 
	public BigDecimal  getMaxNumComparativas() throws DAOException {		
    Session session = obtenerSession();	
		try {
			Query queryCount = session.createSQLQuery("select maxcomparativas from o02agpe0.tb_parametros par");			
				return (BigDecimal) queryCount.uniqueResult();
		}
		catch (Exception e) {
			throw new DAOException("Error al recuperar el max. numero de comparativas a duplicar por módulo ", e);
		}
	}
	
	/**
	 * Devuelve el campo respuesta del servicio del último registro de una póliza y un módulo determinado
	 * @param idPoliza
	 * @param codModulo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("deprecation")
	public Clob  getRespuestaModulosPolizaCoberturaSW(Long idPoliza, String codModulo, Integer operacion) throws DAOException {		
	    Session session = obtenerSession();	
			try {
				Clob respuesta=null;
				String consulta="Select * from (select RESPUESTA from o02agpe0.TB_MODULOS_POLIZA_COBERTURA_SW " +
						"where IDPOLIZA= " + idPoliza + " and CODMODULO='" + codModulo + 
						"' and OPERACION=" + operacion + " order by idmodulo desc, fecha desc) where rownum=1";
				
				logger.info(consulta);
			    Statement stmt = session.connection().createStatement();
			    ResultSet rs = stmt.executeQuery(consulta);	
			    
		         while (rs.next()) {		        	 
		        	 respuesta = (Clob) rs.getClob(1);
		         }  
		         rs.close();
		         stmt.close();
		          		       		 		
		         return respuesta;
			}
			catch (Exception e) {
				throw new DAOException("Error al recuperar el campo respuesta del SW de la póliza: " 
						+ idPoliza + " y el módulo:" + codModulo, e);
			}
		}
	
	
	public Long getSecuenciaComparativa() throws DAOException {
		try {
			Session session = obtenerSession();
			String sql = "select o02agpe0.SQ_MODULOS_POLIZA.nextval from dual";
			BigDecimal secuencia = (BigDecimal) session.createSQLQuery(sql).uniqueResult();
			return secuencia.longValue();
		} catch (Exception e) {
			throw new DAOException("Error al crear la secuencia de la comparativa ", e);
		}
	}	
}
