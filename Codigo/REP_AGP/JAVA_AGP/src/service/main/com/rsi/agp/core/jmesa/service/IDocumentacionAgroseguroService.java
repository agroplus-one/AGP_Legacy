package com.rsi.agp.core.jmesa.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.DocsAgroseguroFilter;
import com.rsi.agp.core.jmesa.sort.DocAgroseguroSort;
import com.rsi.agp.dao.tables.commons.Perfil;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.doc.DocAgroseguro;
import com.rsi.agp.dao.tables.doc.DocAgroseguroExtPerm;
import com.rsi.agp.dao.tables.doc.DocAgroseguroTipo;
import com.rsi.agp.dao.tables.doc.FormCargaDoc;

public interface IDocumentacionAgroseguroService {

	public Collection<DocAgroseguroTipo> getTiposDocumento()
			throws BusinessException;

	public Collection<DocAgroseguroTipo> getTiposDocumentoNoAdmin()
			throws BusinessException;

	public Collection<DocAgroseguroExtPerm> getExtensiones()
			throws BusinessException;

	public String getTablaDocumentos(final HttpServletRequest request,
			final HttpServletResponse response,
			final DocAgroseguro docAgroseguroBean, final String origenLlamada,
			final boolean esUsuarioAdmin, final Usuario usuario) throws BusinessException;

	public Collection<DocAgroseguro> getDocsAgroseguroWithFilterAndSort(
			final DocsAgroseguroFilter filter, final DocAgroseguroSort sort,
			int rowStart, int rowEnd, final boolean incluirPerfil,
			final String perfil, final BigDecimal codEntidad) throws BusinessException;

	public int getDocsAgroseguroCountWithFilter(
			final DocsAgroseguroFilter filter, final boolean incluirPerfil, final String perfil, final BigDecimal codEntidad) throws BusinessException;

	public String validateDocAgroseguro(final FormCargaDoc docAgroseguroBean, final String perfil)
			throws BusinessException;

	public DocAgroseguro grabarDocAgroseguro(
			final FormCargaDoc docAgroseguroBean) throws BusinessException;

	public void borrarDocsAgroseguro(final Long[] listaIds)
			throws BusinessException;

	public DocAgroseguro getDocAgroseguro(final Long idDocAgroseguro)
			throws BusinessException;
	
	DocAgroseguro getDocumentoParaPolizasRC(String especieRC, BigDecimal plan) throws BusinessException;

	/* Pet. 79014 ** MODIF TAM (22.03.2022) ** Inicio */
	public List<Perfil> obtenerListaPerfiles() throws DAOException;
	public DocAgroseguroTipo updateDescTipoDoc(Long id, String desc) throws BusinessException;
	public DocAgroseguroTipo insertDescTipoDoc(DocAgroseguroTipo docAgroTipo) throws BusinessException;
	public void modificarDocAgroseguro (Long id, DocAgroseguro docAgroModif, String codusuario) throws BusinessException;
	
	public void grabarPerfilesDocAgr(DocAgroseguro docAgroseguroBean) throws BusinessException, DAOException;
	public void modificarPerfilesDocAgr(Long id, DocAgroseguro docAgroseguroBean) throws BusinessException, DAOException;
	public List<String> obtenerListaPerfilesDoc(Long id) throws DAOException;

	public String getNombLinea(BigDecimal codlinea) throws BusinessException;
	public String getNombEntidad(final BigDecimal codEntidad) throws BusinessException;
	/* Pet. 79014 ** MODIF TAM (22.03.2022) ** Fin */

	
	
}
