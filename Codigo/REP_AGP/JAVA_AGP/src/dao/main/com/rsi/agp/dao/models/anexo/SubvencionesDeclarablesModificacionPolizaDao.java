package com.rsi.agp.dao.models.anexo;

import java.math.BigDecimal;
import java.util.Set;

import org.hibernate.Session;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.SubvDeclarada;

public class SubvencionesDeclarablesModificacionPolizaDao extends BaseDaoHibernate implements ISubvencionesDeclarablesModificacionPolizaDao {

	@Override
	public void actualizaSubvencionesAnexo(AnexoModificacion anexo, Set<SubvDeclarada> subvDefinitivas) throws DAOException{
		
		//borraremos las subvenciones que tenga el anexo e insertaremos las subvenciones incluidas en subvDefinitivas
		Session session = obtenerSession();
		try {
			// Eliminamos todas las subvenciones anteriores
			for (SubvDeclarada sd : anexo.getSubvDeclaradas()){
				session.delete(sd);
			}
	
			// Insertamos las nuevas subvenciones
			for (SubvDeclarada sd : subvDefinitivas){
				session.save(sd);
			}
			
		} catch(Exception ex){
			logger.error("Excepcion : SubvencionesDeclarablesModificacionPolizaDao - actualizaSubvencionesAnexo", ex);
			throw new DAOException("Se ha producido un error al guardar las subvenciones del Anexo de Modificación", ex);
		} finally{
		}
	}
	
	/**
	 * Insertamos las subvenciones declaradas
	 * @author U029114 24/07/2017
	 * @param subvDecl	 
	 * @throws DAOException
	 */
	public void insertamosSubvencionesDeclaradasAnexo(SubvDeclarada subvDecl) throws DAOException {
			
			//insertamos las subvenciones declaradas que tengamos en el xml
			Session session = obtenerSession();
			try {
				session.save(subvDecl);
			} catch(Exception ex) {
				logger.error(
						"Excepcion : SubvencionesDeclarablesModificacionPolizaDao - insertamosSubvencionesDeclaradasAnexo",
						ex);
				throw new DAOException("Se ha producido un error al insertar las subvenciones declaradas del Anexo de Modificación", ex);
			} finally{
			}
	}
	
	/**
	 * Comprobamos si existen subvenciones declaradas para el anexo.
	 * @author U029114 24/07/2017
	 * @param idAnexo
	 * @return boolean
	 * @throws DAOException
	 */
	public boolean getsubvDeclarada(Long idAnexo) throws DAOException {

		final Session sesion = obtenerSession();
		try {
			boolean existeSubvencionesDeclarada= false;
			StringBuilder stringQuery = new StringBuilder();
			stringQuery.append( "select count(*) from tb_Anexo_Mod_Subv_Decl subv_decl where 1=1 ");
			stringQuery.append(" and subv_decl.idanexo = " + idAnexo);
			BigDecimal existeSubv = (BigDecimal) sesion.createSQLQuery(stringQuery.toString()).uniqueResult();
			
			existeSubvencionesDeclarada = (existeSubv.intValue() == 0) ? false:true;
			return existeSubvencionesDeclarada;

		}catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}
	
}
