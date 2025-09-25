package com.rsi.agp.dao.models.doc;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.DocsAgroseguroFilter;
import com.rsi.agp.core.jmesa.sort.DocAgroseguroSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.commons.Perfil;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.doc.DocAgroseguro;
import com.rsi.agp.dao.tables.doc.DocAgroseguroExtPerm;
import com.rsi.agp.dao.tables.doc.DocAgroseguroPerfiles;
import com.rsi.agp.dao.tables.doc.DocAgroseguroTipo;

@SuppressWarnings("rawtypes")
public interface IDocumentacionAgroseguroDao extends GenericDao {

	public Collection<DocAgroseguroTipo> getTiposDocumento()
			throws DAOException;

	public Collection<DocAgroseguroTipo> getTiposDocumentoNoAdmin()
			throws DAOException;

	public Collection<DocAgroseguroExtPerm> getExtensiones()
			throws DAOException;

	public String getlistaIdsTodos(final DocsAgroseguroFilter consultaFilter, final Usuario usuario)
			throws DAOException;

	public int getDocsAgroseguroCountWithFilter(
			final DocsAgroseguroFilter filter, final boolean incluirPerfil, final String perfil, final BigDecimal codEntidad) throws DAOException;

	public Collection<DocAgroseguro> getDocsAgroseguroWithFilterAndSort(
			final DocsAgroseguroFilter filter, final DocAgroseguroSort sort,
			final int rowStart, final int rowEnd, final boolean incluirPerfil,
			final String perfil, final BigDecimal codEntidad) throws DAOException;
	
	DocAgroseguro getDocsPolizasRC(String especieRC, BigDecimal plan) throws DAOException;

	/* Pet. 79014 ** MODIF TAM (22.03.2022) ** Inicio */
	List<Perfil> obtenerListaPerfiles() throws DAOException;
	public DocAgroseguroTipo modifTipoDocumento(Long id, String descripcion) throws DAOException;
	public DocAgroseguroTipo altaTipoDocumento(DocAgroseguroTipo docAgrTipo) 	throws DAOException;
	public String getNombLinea(BigDecimal codLinea) throws DAOException;
	public String getNombreEntidad(BigDecimal codEntidad) throws DAOException;
	public void modificarDocAgroseguro(Long id, DocAgroseguro docAgroModif, String codusuario) throws DAOException;
	public void grabarPerfilesDocAgr(Set<DocAgroseguroPerfiles> docAgrPerfiles) throws DAOException;
	public void ModifPerfilesDocAgr(Long id, Set<DocAgroseguroPerfiles> docAgrPerfiles) throws DAOException;
	public List<String> obtenerListaPerfilesDoc(Long id) throws DAOException;

	public String obtenerStringListPerf(Long id) throws DAOException;
	public List<Perfil> obtenerListaTodosPerfiles() throws DAOException;
	/* Pet. 79014 ** MODIF TAM (22.03.2022) ** Fin */
}