package com.rsi.agp.dao.models.anexo;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.SubvDeclarada;
import com.rsi.agp.dao.tables.poliza.SubAseguradoCCAA;
import com.rsi.agp.dao.tables.poliza.SubAseguradoENESA;

public class SubvDeclaradaDao extends BaseDaoHibernate implements ISubvDeclaradaDao{

	@Override
	public List<SubvDeclarada> getAll(Long idAnexo) throws DAOException {		
		try {
			
			return findFiltered(SubvDeclarada.class, "anexoModificacion.id", idAnexo);
			
		} catch (Exception e) {
			
			throw new DAOException("Se ha producido un error listando las subvenciones declaradas de la póliza", e);
			
		}
	}

	@Override
	public void bajaSubvIncompatible(AnexoModificacion anexo, BigDecimal codSubv)throws DAOException {
		Session session = obtenerSession();
		try {
			boolean update = false;
			List<SubAseguradoENESA> subvenesa =  findFiltered(SubAseguradoENESA.class, "poliza.idpoliza", anexo.getPoliza().getIdpoliza());
			List<SubAseguradoCCAA> subccaa = findFiltered(SubAseguradoCCAA.class, "poliza.idpoliza", anexo.getPoliza().getIdpoliza());
			
			for( SubAseguradoENESA enesa:subvenesa){
				if(enesa.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa().equals(codSubv)){
					update = true;
				}
			}
			for( SubAseguradoCCAA cca:subccaa){
				if(cca.getSubvencionCCAA().getTipoSubvencionCCAA().getCodtiposubvccaa().equals(codSubv)){
					update = true;
				}
			}
			
			if(update){
				Query updateQuery = session.createSQLQuery("update tb_anexo_mod_subv_decl set tipomodificacion = 'B' where idanexo = :idanx and codsubvencion = :codsubv")
																																.setLong("idanx", anexo.getId())
																																.setBigDecimal("codsubv", codSubv);
				updateQuery.executeUpdate();
			}else{
				Query deleteQuery = session.createSQLQuery("delete from tb_anexo_mod_subv_decl where idanexo = :idanx and codsubvencion = :codsubv")
																																.setLong("idanx", anexo.getId())
																																.setBigDecimal("codsubv", codSubv);
				deleteQuery.executeUpdate();
			}
			
			
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error al borrar la subvención", e);
		}finally{
		}
	}
	

}
