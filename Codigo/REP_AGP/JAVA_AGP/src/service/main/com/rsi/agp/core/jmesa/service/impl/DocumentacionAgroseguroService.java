package com.rsi.agp.core.jmesa.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jmesa.core.filter.MatcherKey;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.ExportType;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.component.Column;
import org.jmesa.view.component.Row;
import org.jmesa.view.component.Table;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.DocsAgroseguroFilter;
import com.rsi.agp.core.jmesa.service.IDocumentacionAgroseguroService;
import com.rsi.agp.core.jmesa.sort.DocAgroseguroSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomToolbarMarcarTodos;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.BigDecimalFilterMatcher;
import com.rsi.agp.dao.filters.CharacterFilterMatcher;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.filters.DocAgroseguroSetFilterMatcher;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.StringFilterMatcher;
import com.rsi.agp.dao.filters.StringLikeFilterMatcher;
import com.rsi.agp.dao.models.doc.IDocumentacionAgroseguroDao;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.tables.commons.Perfil;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.doc.DocAgroseguro;
import com.rsi.agp.dao.tables.doc.DocAgroseguroExtPerm;
import com.rsi.agp.dao.tables.doc.DocAgroseguroFichero;
import com.rsi.agp.dao.tables.doc.DocAgroseguroPerfiles;
import com.rsi.agp.dao.tables.doc.DocAgroseguroTipo;
import com.rsi.agp.dao.tables.doc.FormCargaDoc;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.rc.EspeciesRC;

@SuppressWarnings("deprecation")
public class DocumentacionAgroseguroService implements IDocumentacionAgroseguroService {

	private Log logger = LogFactory.getLog(getClass());

	private HashMap<String, String> columnas = new HashMap<String, String>();
	private String id;

	// Constantes para los nombres de las columnas del listado
	private final static String ID_STR = "ID";
	private final static String PLAN = "PLAN";
	private final static String LINEA = "LINEA";
	private final static String ENTIDAD = "ENTIDAD";
	private final static String TIPODOC = "TIPODOC";
	private final static String TIPODOC_DESC = "TIPODOC_DESC";
	private final static String DESCRIPCION = "DESCRIPCION";

	public static final BigDecimal USUARIO_EXTERNO = new BigDecimal(1);

	private final static Long TIPO_CONDICIONES_RC = new Long(6);
	/* Pet. 79014 ** MODIF TAM (28.03.2022) ** Inicio */
	private final static String FECHAVALIDEZ = "fechavalidez";
	private final static String FECHA = "fecha";
	private static final String DD_MM_YYYY = "dd/MM/yyyy";

	// Literales columnas excel
	private static final String FECHA_MODIFICACION = "Fecha Modificacion";
	private static final String FECHA_VALIDEZ = "Fecha Validez";
	private static final String DESCRIPCION2 = "Descripcion";
	private static final String TIPO_DOCUMENTO = "Tipo documento";
	private static final String ENTIDAD2 = "Entidad";
		private static final String LINEA2 = "Linea";
	private static final String PLAN2 = "Plan";
	private static final String DOCUMENTACION_DE_AGROSEGURO = "DOCUMENTACION DE AGROSEGURO";
	private final String NBSP = "&nbsp;";
	/* Pet. 79014 ** MODIF TAM (28.03.2022) ** Fin */

	private IDocumentacionAgroseguroDao documentacionAgroseguroDao;
	private ILineaDao lineaDao;

	public void setDocumentacionAgroseguroDao(IDocumentacionAgroseguroDao documentacionAgroseguroDao) {
		this.documentacionAgroseguroDao = documentacionAgroseguroDao;
	}

	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public Collection<DocAgroseguroTipo> getTiposDocumento() throws BusinessException {

		Collection<DocAgroseguroTipo> result = null;

		try {

			result = documentacionAgroseguroDao.getTiposDocumento();

		} catch (Exception ex) {

			logger.error("getTiposDocumento error. " + ex);
			throw new BusinessException("Se ha producido al obtener getTiposDocumento:", ex);
		}

		return result;
	}

	@Override
	public Collection<DocAgroseguroTipo> getTiposDocumentoNoAdmin() throws BusinessException {

		Collection<DocAgroseguroTipo> result = null;
		try {
			result = documentacionAgroseguroDao.getTiposDocumentoNoAdmin();
		} catch (Exception ex) {
			logger.error(new StringBuilder("getTiposDocumentoNoAdmin error. ").append(ex).toString());
			throw new BusinessException("Se ha producido al obtener getTiposDocumento:", ex);
		}
		return result;
	}

	@Override
	public Collection<DocAgroseguroExtPerm> getExtensiones() throws BusinessException {

		Collection<DocAgroseguroExtPerm> result = null;

		try {

			result = documentacionAgroseguroDao.getExtensiones();

		} catch (Exception ex) {

			logger.error("getExtensiones error. " + ex);
			throw new BusinessException("Se ha producido al obtener getExtensiones:", ex);
		}

		return result;
	}

	@Override
	public String validateDocAgroseguro(final FormCargaDoc docAgroseguroBean, final String perfil)
			throws BusinessException {

		String errorMsg = null;
		BigDecimal PlanGenerico = new BigDecimal(9999);
		boolean lineaVal = false;
		BigDecimal codEntidad = null;

		/* Pet. 79014 ** MODIF TAM (31.03.2022) ** Inicio */
		boolean incluirPerfil = false;
		if (docAgroseguroBean.getListaPerfiles() != null) {
			if (docAgroseguroBean.getListaPerfiles().size() > 0) {
				incluirPerfil = true;
			}
		}
		if (docAgroseguroBean.getCodentidad() != null) {
			codEntidad = docAgroseguroBean.getCodentidad();
		}
		/* Pet. 79014 ** MODIF TAM (31.03.2022) ** Fin */

		try {

			if ((docAgroseguroBean.getCodplan().compareTo(PlanGenerico)) == 0) {
				lineaVal = true;
			} else {
				Linea linea = this.lineaDao.getLinea(docAgroseguroBean.getCodlinea(), docAgroseguroBean.getCodplan());
				if (linea == null) {
					errorMsg = "El Plan/Linea introducido no existe.";
					lineaVal = false;
				} else {
					lineaVal = true;
				}
			}

			if (lineaVal) {

				DocsAgroseguroFilter filter = new DocsAgroseguroFilter();
				filter.addFilter("codplan", docAgroseguroBean.getCodplan().toString());

				if (docAgroseguroBean.getCodlinea() != null) {
					filter.addFilter("codlinea", docAgroseguroBean.getCodlinea().toString());
				}
				filter.addFilter("docAgroseguroTipo.id", docAgroseguroBean.getDocAgroseguroTipo().getId().toString());

				int numDocs = this.documentacionAgroseguroDao.getDocsAgroseguroCountWithFilter(filter, incluirPerfil,
						perfil, codEntidad);

				if (numDocs >= docAgroseguroBean.getDocAgroseguroTipo().getNumMaxLinea().intValue()) {

					errorMsg = "Alcanzado numero maximo de documentos de ese tipo para el Plan/Linea seleccionados.";
				} else {

					// COMPROBAMOS QUE NO EXISTE EL MISMO PLAN/LINEA/TIPO/DESCRIPCION YA EN BBDD
					filter.setDescripccionExacta(Boolean.TRUE);
					filter.addFilter("descripcion", docAgroseguroBean.getDescripcion());

					numDocs = this.documentacionAgroseguroDao.getDocsAgroseguroCountWithFilter(filter, incluirPerfil,
							perfil, codEntidad);

					if (numDocs > 0) {

						errorMsg = "Ya existe un fichero para ese plan/Linea/tipo/descripcion.";
					}
				}

				if (docAgroseguroBean.getDocAgroseguroTipo().getId().equals(TIPO_CONDICIONES_RC)) {
					String descripcion = docAgroseguroBean.getDescripcion().trim().toUpperCase(new Locale("es", "ES"));
					EspeciesRC especie = (EspeciesRC) this.documentacionAgroseguroDao.getObject(EspeciesRC.class,
							descripcion);
					if (especie == null) {
						errorMsg = "No existe la Especie RC";
					}
				}
			}
		} catch (Exception ex) {

			logger.error("validateDocAgroseguro error. " + ex);
			throw new BusinessException("Se ha producido al obtener validateDocAgroseguro:", ex);
		}

		return errorMsg;
	}

	@Override
	public DocAgroseguro grabarDocAgroseguro(final FormCargaDoc docAgroseguroBean) throws BusinessException {

		DocAgroseguro docAgroseguro = new DocAgroseguro();

		try {

			String fileName = docAgroseguroBean.getFile().getOriginalFilename();
			String fileExt = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());

			docAgroseguro.setDescripcion(docAgroseguroBean.getDescripcion());
			docAgroseguro.setNombre(fileName);

			// Pet. 79014 ** MODIF TAM (25.03.2022) ** Inicio
			if (docAgroseguroBean.getCodlinea() != null) {
				docAgroseguro.setCodlinea(docAgroseguroBean.getCodlinea());
			}
			docAgroseguro.setCodplan(docAgroseguroBean.getCodplan());

			/* Pet. 79014 ** MODIF TAM (26.04.2022) ** Defecto N13 ** Inicio */
			if (docAgroseguroBean.getCodentidad() == null) {
				docAgroseguro.setCodentidad(new BigDecimal(0));
			} else {
				docAgroseguro.setCodentidad(docAgroseguroBean.getCodentidad());
			}
			/* Pet. 79014 ** MODIF TAM (26.04.2022) ** Defecto N13 ** Fin */

			docAgroseguro.setCodusuario(docAgroseguroBean.getCodusuario());
			docAgroseguro.setFechavalidez(docAgroseguroBean.getFechavalidez());
			docAgroseguro.setFecha(docAgroseguroBean.getFecha());
			docAgroseguro.setListaPerfiles(docAgroseguroBean.getListaPerfiles());
			// Pet. 79014 ** MODIF TAM (25.03.2022) ** Inicio

			DocAgroseguroTipo docAgroseguroTipo = (DocAgroseguroTipo) this.documentacionAgroseguroDao
					.get(DocAgroseguroTipo.class, docAgroseguroBean.getDocAgroseguroTipo().getId());
			docAgroseguro.setDocAgroseguroTipo(docAgroseguroTipo);

			Collection<DocAgroseguroExtPerm> listaExtensiones = this.getExtensiones();
			for (DocAgroseguroExtPerm docAgroseguroExtPerm : listaExtensiones) {
				if (docAgroseguroExtPerm.getExtension().equals(fileExt)) {
					docAgroseguro.setDocAgroseguroExtPerm(docAgroseguroExtPerm);
					break;
				}
			}

			DocAgroseguroFichero docAgroseguroFichero = new DocAgroseguroFichero();
			docAgroseguroFichero.setFichero(Hibernate.createBlob(docAgroseguroBean.getFile().getInputStream()));
			docAgroseguroFichero.setDocAgroseguro(docAgroseguro);
			docAgroseguro.setDocAgroseguroFichero(docAgroseguroFichero);

			this.documentacionAgroseguroDao.saveOrUpdate(docAgroseguro);

			documentacionAgroseguroDao.evict(docAgroseguro);

			/* Pet. 79014 ** MODIF TAM (28.03.2022) ** Fin */

		} catch (Exception ex) {

			logger.error("validateDocAgroseguro error. " + ex);
			throw new BusinessException("Se ha producido al obtener validateDocAgroseguro:", ex);
		}

		return docAgroseguro;
	}

	@Override
	public void grabarPerfilesDocAgr(final DocAgroseguro docAgroseguroBean) throws BusinessException, DAOException {

		/* Pet. 79014 ** MODIF TAM (28.03.2022) ** Inicio */
		Set<DocAgroseguroPerfiles> codDocAgroseguroPerfiles = new HashSet<DocAgroseguroPerfiles>(0);

		List<Perfil> listaPerfiles = new ArrayList<Perfil>();
		if (docAgroseguroBean.getListaPerfiles() != null) {
			for (String perf : docAgroseguroBean.getListaPerfiles()) {
				if (!StringUtils.isNullOrEmpty(perf)) {
					Perfil perfil = new Perfil();
					perfil.setId(new BigDecimal(perf));
					listaPerfiles.add(perfil);
				}
			}

			/* Sino solo los marcados */
			if (listaPerfiles.size() > 0) {
				for (Perfil per : listaPerfiles) {
					DocAgroseguroPerfiles docAgrPerf = new DocAgroseguroPerfiles();
					docAgrPerf.setPerfil(per);

					docAgrPerf.setDocAgroseguro(docAgroseguroBean);
					codDocAgroseguroPerfiles.add(docAgrPerf);
				}
			}
		}

		if (codDocAgroseguroPerfiles.size() > 0) {
			this.documentacionAgroseguroDao.grabarPerfilesDocAgr(codDocAgroseguroPerfiles);
		}
	}

	@Override
	public void modificarPerfilesDocAgr(Long id, DocAgroseguro docAgroseguroBean)
			throws BusinessException, DAOException {

		logger.debug("DocumentacionAgroseguroService- modificarPerfilesDocAgro [INIT]");
		Set<DocAgroseguroPerfiles> docAgrPerfiles = new HashSet<DocAgroseguroPerfiles>(0);

		List<Perfil> listaPerfiles = new ArrayList<Perfil>();
		if (docAgroseguroBean.getListaPerfiles() != null) {
			for (String perf : docAgroseguroBean.getListaPerfiles()) {
				if (!StringUtils.isNullOrEmpty(perf)) {
					Perfil perfil = new Perfil();
					perfil.setId(new BigDecimal(perf));
					listaPerfiles.add(perfil);
				}

			}

			if (listaPerfiles.size() > 0) {
				for (Perfil perfil : listaPerfiles) {
					DocAgroseguroPerfiles docAgrPerf = new DocAgroseguroPerfiles();
					docAgrPerf.setPerfil(perfil);

					docAgrPerf.setDocAgroseguro(docAgroseguroBean);
					docAgrPerfiles.add(docAgrPerf);
				}
			}
		}

		this.documentacionAgroseguroDao.ModifPerfilesDocAgr(id, docAgrPerfiles);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void borrarDocsAgroseguro(final Long[] listaIds) throws BusinessException {

		try {

			List<DocAgroseguro> lista = this.documentacionAgroseguroDao
					.getObjects(new com.rsi.agp.dao.filters.Filter() {
						@Override
						public Criteria getCriteria(final Session sesion) {
							Criteria criteria = sesion.createCriteria(DocAgroseguro.class);
							criteria.add(Restrictions.in("id", listaIds));
							return criteria;
						}
					});

			this.documentacionAgroseguroDao.deleteAll(lista);

		} catch (Exception ex) {

			logger.error("borrarDocsAgroseguro error. " + ex);
			throw new BusinessException("Se ha producido al obtener borrarDocsAgroseguro:", ex);
		}
	}

	@Override
	public DocAgroseguro getDocAgroseguro(final Long idDocAgroseguro) throws BusinessException {

		DocAgroseguro docAgroseguro = null;

		try {

			docAgroseguro = (DocAgroseguro) this.documentacionAgroseguroDao.get(DocAgroseguro.class, idDocAgroseguro);

			if (docAgroseguro.getCodlinea() != null) {
				BigDecimal codlinea = docAgroseguro.getCodlinea();
				docAgroseguro.setNomlinea(documentacionAgroseguroDao.getNombLinea(codlinea));
			}

			if (docAgroseguro.getCodentidad() != null) {
				BigDecimal codentidad = docAgroseguro.getCodentidad();
				docAgroseguro.setNombreEntidad(documentacionAgroseguroDao.getNombreEntidad(codentidad));
			}

		} catch (Exception ex) {

			logger.error("borrarDocsAgroseguro error. " + ex);
			throw new BusinessException("Se ha producido al obtener borrarDocsAgroseguro:", ex);
		}

		return docAgroseguro;
	}

	@Override
	public void modificarDocAgroseguro(Long id, DocAgroseguro docAgroModif, String codusuario)
			throws BusinessException {
		try {

			this.documentacionAgroseguroDao.modificarDocAgroseguro(id, docAgroModif, codusuario);

		} catch (Exception ex) {

			logger.error("borrarDocsAgroseguro error. " + ex);
			throw new BusinessException("Se ha producido al obtener borrarDocsAgroseguro:", ex);
		}

	}

	@Override
	public String getTablaDocumentos(final HttpServletRequest request, final HttpServletResponse response,
			final DocAgroseguro docAgroseguroBean, final String origenLlamada, final boolean esUsuarioAdmin,
			final Usuario usuario) throws BusinessException {

		try {

			String perfil = "";
			perfil = usuario.getPerfil();

			TableFacade tableFacade = crearTableFacade(request, response, docAgroseguroBean, origenLlamada, perfil,
					usuario);

			Limit limit = tableFacade.getLimit();
			DocsAgroseguroFilter consultaFilter = getConsultaDocsAgroseguroFilter(limit, esUsuarioAdmin, perfil);

			/* Pet. 79014 ** MODIF TAM (31.03.2022) ** Inicio */
			boolean incluirPerfil = false;
			if (docAgroseguroBean.getListaPerfiles() != null) {
				if (docAgroseguroBean.getListaPerfiles().size() > 0) {
					String listPerf = docAgroseguroBean.getListaPerfiles().toString();
					incluirPerfil = true;
				}
			}
			/* Volvemos a comprobar si se ha incluido Filtro */
			/* GDLD-79014 ** MODIF TAM (21.04.2022) * Defecto N13 ** Inicio */
			BigDecimal codEntidad = null;

			if (!incluirPerfil) {
				FilterSet filterSet = limit.getFilterSet();
				Collection<Filter> filters = filterSet.getFilters();
				for (Filter filter : filters) {
					if (filter.getProperty().equals("codDocAgroseguroPerfiles")) {
						incluirPerfil = true;
					}
				}
			}

			if (!perfil.equals("AGR-0")) {
				codEntidad = usuario.getOficina().getEntidad().getCodentidad();
			} else {
				FilterSet filterSet = limit.getFilterSet();
				Collection<Filter> filters = filterSet.getFilters();
				for (Filter filter : filters) {
					if (filter.getProperty().equals("codentidad")) {
						codEntidad = new BigDecimal(filter.getValue().toString());
					}
				}
			}
			
			/* Pet. 79014 ** MODIF TAM (31.03.2022) ** Fin */

			setDataAndLimitVariables(tableFacade, consultaFilter, limit, incluirPerfil, perfil, codEntidad);
			
			String listaIdsTodos = getlistaIdsTodos(consultaFilter, usuario);
			String script = "<script>$(\"#listaIdsTodos\").val(\"" + listaIdsTodos + "\");</script>";

			String ajax = request.getParameter("ajax");
			if (!"false".equals(ajax) && request.getParameter("export") == null) {
				if (esUsuarioAdmin) {
					tableFacade.setToolbar(new CustomToolbarMarcarTodos());
				} else {
					tableFacade.setToolbar(new CustomToolbar());
				}
				tableFacade.setView(new CustomView());
			} 
			else {
				return html(tableFacade, esUsuarioAdmin, perfil); 
			}

			return html(tableFacade, esUsuarioAdmin, perfil) + script;

		} catch (Exception ex) {

			logger.error("getTablaDocumentos error. " + ex);
			throw new BusinessException("Se ha producido al obtener getTablaDocumentos:", ex);
		}
	}

	private TableFacade crearTableFacade(final HttpServletRequest request, final HttpServletResponse response,
			final DocAgroseguro docAgroseguroBean, final String origenLlamada, final String perfil,
			final Usuario usuario) {

		// Crea el objeto TableFacade
		TableFacade tableFacade = new TableFacade(id, request);
		
		// Carga las columnas a mostrar en el listado en el TableFacade y
		// devuelve un Map con ellas
		HashMap<String, String> columnas = cargarColumnas(tableFacade);

		tableFacade.setExportTypes(response, ExportType.EXCEL);			

		tableFacade.setStateAttr("restore");// return to the table in the same
											// state that the user left it.

		tableFacade.addFilterMatcher(new MatcherKey(String.class), new StringFilterMatcher());

		tableFacade.addFilterMatcher(new MatcherKey(String.class), new StringLikeFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(Date.class), new DateFromFilterMatcher("d/M/yyyy"));
		tableFacade.addFilterMatcher(new MatcherKey(BigDecimal.class), new BigDecimalFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(Set.class), new DocAgroseguroSetFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(Character.class), new CharacterFilterMatcher());

		// Si no es una llamada a traves de ajax
		if (request.getParameter("ajax") == null) {
			if (origenLlamada == null) {
				if (request.getSession().getAttribute("docsAgroseguro_LIMIT") != null) {
					// Si venimos por aqui es que ya hemos pasado por el filtro
					// en algun momento
					tableFacade.setLimit((Limit) request.getSession().getAttribute("docsAgroseguro_LIMIT"));
				}
			} else {
				// Carga en el TableFacade los filtros de busqueda introducidos
				// en el formulario
				cargarFiltrosBusqueda(columnas, docAgroseguroBean, tableFacade, perfil, usuario);
			}
		}

		request.getSession().setAttribute("docsAgroseguro_LIMIT", tableFacade.getLimit());

		return tableFacade;
	}

	private HashMap<String, String> cargarColumnas(final TableFacade tableFacade) {
		// Crea el Map con las columnas del listado y los campos del filtro de
		// busqueda si no se ha hecho anteriormente
		if (this.columnas.size() == 0) {
			this.columnas.put(ID_STR, "id");
			this.columnas.put(PLAN, "codplan");
			this.columnas.put(LINEA, "codlinea");
			this.columnas.put(ENTIDAD, "codentidad");
			this.columnas.put(TIPODOC, "docAgroseguroTipo.id");
			this.columnas.put(TIPODOC_DESC, "docAgroseguroTipo.descripcion");
			this.columnas.put(DESCRIPCION, "descripcion");
			this.columnas.put(FECHAVALIDEZ, "fechavalidez");
			this.columnas.put(FECHA, "fecha");
		}
		tableFacade.setColumnProperties(this.columnas.get(ID_STR), this.columnas.get(PLAN), this.columnas.get(LINEA),
				this.columnas.get(ENTIDAD), this.columnas.get(TIPODOC_DESC), this.columnas.get(DESCRIPCION),
				this.columnas.get(FECHAVALIDEZ), this.columnas.get(FECHA));

		return this.columnas;
	}

	private void cargarFiltrosBusqueda(final HashMap<String, String> columnas, final DocAgroseguro docAgroseguroBean,
			final TableFacade tableFacade, final String perfil, final Usuario usuario) {

		// PLAN
		if (docAgroseguroBean.getCodplan() != null && FiltroUtils.noEstaVacio(docAgroseguroBean.getCodplan()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(PLAN), docAgroseguroBean.getCodplan().toString()));
		// LINEA
		if (docAgroseguroBean.getCodlinea() != null && FiltroUtils.noEstaVacio(docAgroseguroBean.getCodlinea()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(LINEA), docAgroseguroBean.getCodlinea().toString()));
		// TIPO DOCUMENTO
		if (docAgroseguroBean.getDocAgroseguroTipo() != null
				&& FiltroUtils.noEstaVacio(docAgroseguroBean.getDocAgroseguroTipo().getId()))
			tableFacade.getLimit().getFilterSet().addFilter(
					new Filter(columnas.get(TIPODOC), docAgroseguroBean.getDocAgroseguroTipo().getId().toString()));
		// DESCRIPCION
		if (FiltroUtils.noEstaVacio(docAgroseguroBean.getDescripcion()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(DESCRIPCION), docAgroseguroBean.getDescripcion()));

		if (perfil.equals("AGR-0")) {
			// CODENTIDAD
			if (FiltroUtils.noEstaVacio(docAgroseguroBean.getCodentidad()))
				tableFacade.getLimit().getFilterSet()
						.addFilter(new Filter(columnas.get(ENTIDAD), docAgroseguroBean.getCodentidad().toString()));
		}
		// FECHA VALIDEZ

		if (FiltroUtils.noEstaVacio(docAgroseguroBean.getFechavalidez())) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(FECHAVALIDEZ,
					new SimpleDateFormat("dd/MM/yyyy").format(docAgroseguroBean.getFechavalidez())));
		}

		// LISTA_PERFILES
		if (perfil.equals("AGR-0")) {
			if (FiltroUtils.noEstaVacio(docAgroseguroBean.getListaPerfiles())) {
				String listPerf = docAgroseguroBean.getListaPerfiles().toString();
				String val = listPerf.replace("[", "").replace("]", "").replace(" ", "");
				if (!StringUtils.isNullOrEmpty(val)) {
					tableFacade.getLimit().getFilterSet().addFilter(
							new Filter("codDocAgroseguroPerfiles", docAgroseguroBean.getListaPerfiles().toString()));
				}
			}
		} else {
			if (usuario.getExterno().equals(Constants.USUARIO_EXTERNO)) {
				String value = "1" + perfil.substring(4) + ",";
				tableFacade.getLimit().getFilterSet().addFilter(new Filter("codDocAgroseguroPerfiles", value));
			} else {
				String value = perfil.substring(4) + ",";
				tableFacade.getLimit().getFilterSet().addFilter(new Filter("codDocAgroseguroPerfiles", value));
			}
		}

	}

	private DocsAgroseguroFilter getConsultaDocsAgroseguroFilter(final Limit limit, final boolean esUsuarioAdmin,
			final String perfil) {
		DocsAgroseguroFilter consultaFilter = new DocsAgroseguroFilter();
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();

		for (Filter filter : filters) {
			if (!perfil.equals("AGR-0")) {
				String property = filter.getProperty();
				String value = filter.getValue();
				if (!property.equals("fechavalidez")) {
					consultaFilter.addFilter(property, value);
				}
			} else {

				String property = filter.getProperty();
				String value = filter.getValue();
				consultaFilter.addFilter(property, value);
			}
		}

		if (!perfil.equals("AGR-0")) {
			String property = "fechavalidez";
			String value = "perfil0";
			consultaFilter.addFilter(property, value);
		}

		// se agrega este filtro para ocultar los documentos no visibles a los usuarios
		// no administradores
		if (!esUsuarioAdmin) {
			consultaFilter.addFilter("docAgroseguroTipo.visible", Constants.DOC_VISIBLE);
		}
		return consultaFilter;
	}

	private void setDataAndLimitVariables(final TableFacade tableFacade, final DocsAgroseguroFilter consultaFilter,
			final Limit limit, boolean incluirPerfil, String perfil, BigDecimal codEntidad) {

		Collection<DocAgroseguro> items = new ArrayList<DocAgroseguro>();
		try {

			logger.debug("DocumentacionAgroseguroService - setDataAndLimitVariable");
			int totalRows = getDocsAgroseguroCountWithFilter(consultaFilter, incluirPerfil, perfil, codEntidad);
			logger.debug("********** count filas para DocsAgroseguro = " + totalRows + " **********");

			tableFacade.setTotalRows(totalRows);

			DocAgroseguroSort consultaSort = getConsultaDocsAgroseguroSort(limit);
			int rowStart = limit.getRowSelect().getRowStart();
			int rowEnd = limit.getRowSelect().getRowEnd();

			items = getDocsAgroseguroWithFilterAndSort(consultaFilter, consultaSort, rowStart, rowEnd, incluirPerfil,
					perfil, codEntidad);
			logger.debug("********** list items para DocsAgroseguro = " + items.size() + " **********");

		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
		tableFacade.setItems(items);
	}

	private DocAgroseguroSort getConsultaDocsAgroseguroSort(final Limit limit) {
		DocAgroseguroSort consultaSort = new DocAgroseguroSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			consultaSort.addSort(property, order);
		}
		return consultaSort;
	}

	public String getlistaIdsTodos(final DocsAgroseguroFilter consultaFilter, Usuario usuario) throws DAOException {

		String listaIdsTodos = this.documentacionAgroseguroDao.getlistaIdsTodos(consultaFilter, usuario);
		return listaIdsTodos;
	}

	@Override
	public Collection<DocAgroseguro> getDocsAgroseguroWithFilterAndSort(final DocsAgroseguroFilter filter,
			final DocAgroseguroSort sort, final int rowStart, final int rowEnd, final boolean incluirPerfil,
			final String perfil, final BigDecimal codEntidad) throws BusinessException {

		try {

			return documentacionAgroseguroDao.getDocsAgroseguroWithFilterAndSort(filter, sort, rowStart, rowEnd,
					incluirPerfil, perfil, codEntidad);

		} catch (Exception ex) {

			logger.error("getDocsAgroseguroWithFilterAndSort error. " + ex);
			throw new BusinessException("Se ha producido al obtener getDocsAgroseguroWithFilterAndSort:", ex);
		}
	}

	@Override
	public int getDocsAgroseguroCountWithFilter(final DocsAgroseguroFilter filter, final boolean incluirPerfil,
			String perfil, BigDecimal codEntidad) throws BusinessException {

		try {
			return documentacionAgroseguroDao.getDocsAgroseguroCountWithFilter(filter, incluirPerfil, perfil,
					codEntidad);
		} catch (Exception ex) {
			logger.error("getDocsAgroseguroCountWithFilter error. " + ex);
			throw new BusinessException("Se ha producido al obtener getDocsAgroseguroCountWithFilter:", ex);
		}
	}

	private String html(final TableFacade tableFacade, final boolean esUsuarioAdmin, final String perfil) {

		Limit limit = tableFacade.getLimit();
		if (limit.isExported()) {

			Table exportTable = tableFacade.getTable();
			// eliminamos la columna "Id"
			eliminarColumnaId(tableFacade, exportTable);

			// renombramos las cabeceras
			configurarCabecerasColumnasExport(exportTable);

			// configuramos cada columna
			configurarColumnasExport(exportTable);
			
			tableFacade.render(); // Will write the export data out to the response.
			return null; // In Spring return null tells the controller not to do anything.

		} else {
			
			HtmlTable table = (HtmlTable) tableFacade.getTable();
			table.getRow().setUniqueProperty("id");
			configurarColumnas(table);

			table.getRow().getColumn(columnas.get(ID_STR)).getCellRenderer()
					.setCellEditor(getCellEditorAcciones(esUsuarioAdmin, perfil));
			table.getRow().getColumn(columnas.get(ENTIDAD)).getCellRenderer().setCellEditor(getCellEditorCodEntidad());
			table.getRow().getColumn(columnas.get(FECHA)).getCellRenderer().setCellEditor(getCellEditorFecha());
			table.getRow().getColumn(columnas.get(FECHAVALIDEZ)).getCellRenderer()
					.setCellEditor(getCellEditorFechaVal());
		}

		return tableFacade.render();
	}
	
	private void configurarCabecerasColumnasExport(Table table) {
		
		table.setCaption(DOCUMENTACION_DE_AGROSEGURO);
		Row row = table.getRow();
		
		Column colPlan = row.getColumn(columnas.get(PLAN));
		colPlan.setTitle(PLAN2);
		
		Column colLinea = row.getColumn(columnas.get(LINEA));
		colLinea.setTitle(LINEA2);
		
		Column colEntidad  = row.getColumn(columnas.get(ENTIDAD));
		colEntidad.setTitle(ENTIDAD2);
		
		Column colTipoDocumento  = row.getColumn(columnas.get(TIPODOC_DESC));
		colTipoDocumento.setTitle(TIPO_DOCUMENTO);

		Column colDescripcion  = row.getColumn(columnas.get(DESCRIPCION));
		colDescripcion.setTitle(DESCRIPCION2);

		Column colFechaValidez  = row.getColumn(columnas.get(FECHAVALIDEZ));
		colFechaValidez.setTitle(FECHA_VALIDEZ);

		Column colFechaModificacion  = row.getColumn(columnas.get(FECHA));
		colFechaModificacion.setTitle(FECHA_MODIFICACION);
		
	}

	private void configurarColumnasExport(Table table) {
		
		table.getRow().getColumn(columnas.get(FECHAVALIDEZ)).getCellRenderer().setCellEditor(getCellEditorFecha());
		table.getRow().getColumn(columnas.get(FECHA)).getCellRenderer().setCellEditor(getCellEditorFecha());
	}

	/**
	 * metodo para eliminar la columna Id en los informes
	 * 
	 * @param tableFacade
	 * @param table
	 */
	private void eliminarColumnaId(TableFacade tableFacade, Table table) {
		Row row = table.getRow();
		Row rowFinal = new Row();
		List<Column> lstColumns = row.getColumns();
		for (Column col : lstColumns) {
			if (!col.getProperty().equals("id")) {
				rowFinal.addColumn(col);
			}
		}
		table.setRow(rowFinal);
		tableFacade.setTable(table);
	}

	private void configurarColumnas(final HtmlTable table) {

		configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false, false, "10%");
		configColumna(table, columnas.get(PLAN), PLAN2, true, true, "10%");
		configColumna(table, columnas.get(LINEA), "L&iacute;nea", true, true, "10%");
		configColumna(table, columnas.get(ENTIDAD), ENTIDAD2, true, true, "10%");
		configColumna(table, columnas.get(TIPODOC_DESC), TIPO_DOCUMENTO, false, true, "20%");
		configColumna(table, columnas.get(DESCRIPCION), "Descripci&oacute;n", true, true, "30%");
		configColumna(table, columnas.get(FECHAVALIDEZ), FECHA_VALIDEZ, false, true, "10%");
		configColumna(table, columnas.get(FECHA), "Fecha Modificaci&oacute;n", false, true, "10%");
	}

	private void configColumna(final HtmlTable table, final String idCol, final String title, final boolean filterable,
			final boolean sortable, final String width) {

		table.getRow().getColumn(idCol).setTitle(title);
		table.getRow().getColumn(idCol).setFilterable(filterable);
		table.getRow().getColumn(idCol).setSortable(sortable);
		table.getRow().getColumn(idCol).setWidth(width);
	}

	@Override
	public DocAgroseguro getDocumentoParaPolizasRC(String especieRC, BigDecimal plan) throws BusinessException {
		try {
			return this.documentacionAgroseguroDao.getDocsPolizasRC(especieRC, plan);
		} catch (DAOException e) {
			throw new BusinessException();
		}
	}

	@Override
	public DocAgroseguroTipo updateDescTipoDoc(Long id, String desc) throws BusinessException {
		try {
			return this.documentacionAgroseguroDao.modifTipoDocumento(id, desc);
		} catch (DAOException e) {
			throw new BusinessException();
		}
	}

	@Override
	public DocAgroseguroTipo insertDescTipoDoc(DocAgroseguroTipo docAgroTipo) throws BusinessException {
		try {
			return this.documentacionAgroseguroDao.altaTipoDocumento(docAgroTipo);
		} catch (DAOException e) {
			throw new BusinessException();
		}
	}

	/* Pet. 79014 ** MODIF TAM (22.03.2022) ** Inicio */
	@Override
	public List<Perfil> obtenerListaPerfiles() throws DAOException {

		List<Perfil> listaPerfiles = this.documentacionAgroseguroDao.obtenerListaPerfiles();
		return listaPerfiles;
	}

	private CellEditor getCellEditorFecha() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				// Muestra la fecha de envio
				String value = "";

				// Obtiene el codigo de estado de la poliza actual
				try {
					// Si la tiene fecha de envio se formatea y se muestra, si no la tiene nos se
					// muestra nada
					Date dateAux = (Date) new BasicCellEditor().getValue(item, columnas.get(FECHA), rowcount);
					value = (dateAux == null) ? ("") : new SimpleDateFormat(DD_MM_YYYY).format(dateAux);
				} catch (Exception e) {
					logger.error("DocumentacionAgroseguroService - Ocurrio un error al obtener la fecha del documento",
							e);
				}

				HtmlBuilder html = new HtmlBuilder();
				html.append(FiltroUtils.noEstaVacioSinEspacios(value) ? value : NBSP);
				return html.toString();
			}
		};
	}

	private CellEditor getCellEditorCodEntidad() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				// Muestra la fecha de envio
				String value = "";
				HtmlBuilder html = new HtmlBuilder();
				// Obtiene el codigo de estado de la poliza actual
				try {
					// Se comprueba si la Entidad contiene datos, en caso contrario se muestra vacio
					BigDecimal codEntidad = (BigDecimal) new BasicCellEditor().getValue(item, columnas.get(ENTIDAD),
							rowcount);
					BigDecimal codEntZero = new BigDecimal(0);
					if (codEntidad != null && codEntidad.compareTo(codEntZero) != 0) {
						html.append(codEntidad);
					} else {
						html.append("");
					}
				} catch (Exception e) {
					logger.error("DocumentacionAgroseguroService - Ocurrio un error al obtener la Descripcion", e);
				}

				return html.toString();
			}
		};
	}

	private CellEditor getCellEditorFechaVal() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				// Muestra la fecha de envio
				String value = "";

				// Obtiene el codigo de estado de la poliza actual
				try {
					// Si la tiene fecha de envio se formatea y se muestra, si no la tiene nos se
					// muestra nada
					Date dateAux = (Date) new BasicCellEditor().getValue(item, columnas.get(FECHAVALIDEZ), rowcount);
					value = (dateAux == null) ? ("") : new SimpleDateFormat(DD_MM_YYYY).format(dateAux);
				} catch (Exception e) {
					logger.error(
							"DocumentacionAgroseguroService - Ocurrio un error al obtener la fecha validez del Documento",
							e);
				}

				HtmlBuilder html = new HtmlBuilder();
				html.append(FiltroUtils.noEstaVacioSinEspacios(value) ? value : NBSP);
				return html.toString();
			}
		};
	}

	private CellEditor getCellEditorAcciones(final boolean esUsuarioAdmin, final String perfil) {
		return new CellEditor() {
			public Object getValue(final Object item, final String property, final int rowcount) {

				HtmlBuilder html = new HtmlBuilder();

				Long id = (Long) new BasicCellEditor().getValue(item, columnas.get(ID_STR), rowcount);

				if (esUsuarioAdmin) {

					// checkbox
					html.append("<input type=\"checkbox\" id=\"check_" + id + "\"  name=\"check_" + id
							+ "\" onClick =\"listaCheckId(\'" + id + "')\" class=\"dato\"/>");
					html.append("&nbsp;");

					// boton borrar
					html.a().href().quote().append("javascript:borrar(" + id + ");").quote().close();
					html.append(
							"<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Documento\" title=\"Borrar Documento\"/>");
					html.aEnd();
					html.append("&nbsp;");
				}

				// boton descargar
				html.a().href().quote().append("javascript:descargar(" + id + ");").quote().close();
				html.append(
						"<img src=\"jsp/img/displaytag/download.png\" alt=\"Descargar Documento\" title=\"Descargar Documento\"/>");
				html.aEnd();
				html.append("&nbsp;");

				/* Pet. 79014 ** MODIF TAM (18.03.2022) ** Inicio */
				if (Constants.PERFIL_USUARIO_ADMINISTRADOR.equals(perfil)) {

					try {
						DocAgroseguro docAgroseguro = getDocAgroseguro(id);

						String listPerfilStr = documentacionAgroseguroDao.obtenerStringListPerf(docAgroseguro.getId());

						logger.debug("Valor de listPerfilStr:" + listPerfilStr);

						BigDecimal codPlan = docAgroseguro.getCodplan();
						BigDecimal codLinea = docAgroseguro.getCodlinea();
						String nombLinea = docAgroseguro.getNomlinea();
						Long tipoDoc = docAgroseguro.getDocAgroseguroTipo().getId();
						String nombTipoDoc = docAgroseguro.getDocAgroseguroTipo().getDescripcion();

						BigDecimal codEntidad = docAgroseguro.getCodentidad();
						if (codEntidad == null) {
							codEntidad = new BigDecimal(0);
						}
						String nombEnt = docAgroseguro.getNombreEntidad();

						if (!StringUtils.isNullOrEmpty(nombLinea)) {
							nombLinea = getNombLinea(codLinea);
						}

						String desc = docAgroseguro.getDescripcion();
						Date fechaValidez = docAgroseguro.getFechavalidez();

						SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
						String nulo = "";

						if (desc == null) {
							desc = nulo;
						}

						if (nombEnt == null) {
							nombEnt = nulo;
						}

						if (nombLinea == null) {
							nombLinea = nulo;
						}
						// logger.debug("Valor de listaPerfStr:-" + listaPerfStr + "-");
						BigDecimal codEntZero = new BigDecimal(0);
						String editarTx = "";

						if (codEntidad.compareTo(codEntZero) == 0) {
							editarTx = "javascript:editar(" + id + "," + codPlan + "," + codLinea + ",'" + nombLinea
									+ "'," + tipoDoc + ",'" + nombTipoDoc + "', " + "''" + ",'" + nombEnt + "', '"
									+ desc + "','" + ((fechaValidez == null) ? nulo : formato.format(fechaValidez))
									+ "', '" + listPerfilStr + "'" + ");";

						} else {
							editarTx = "javascript:editar(" + id + "," + codPlan + "," + codLinea + ",'" + nombLinea
									+ "'," + tipoDoc + ",'" + nombTipoDoc + "', " + codEntidad + ",'" + nombEnt + "', '"
									+ desc + "','" + ((fechaValidez == null) ? nulo : formato.format(fechaValidez))
									+ "', '" + listPerfilStr + "'" + ");";
						}

						logger.debug("Valor de editarTx:" + editarTx);

						// boton editar
						html.a().href().quote().append(editarTx).quote().close();

					} catch (Exception ex) {
						logger.debug("Error al los datos del Deocumento.", ex);
					}

					html.append(
							"<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Documento\" title=\"Editar Documento\"/>");
					html.aEnd();
					html.append("&nbsp;");
				}
				/* Pet. 79014 ** MODIF TAM (18.03.2022) ** Fin */

				return html.toString();

			}
		};
	}

	/**
	 * DAA 28/05/2013 Obtenemos la lista de Perfiles de los Documentos
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@Override
	public List<String> obtenerListaPerfilesDoc(Long id) throws DAOException {

		List<String> listaPerfilesDoc = this.documentacionAgroseguroDao.obtenerListaPerfilesDoc(id);
		return listaPerfilesDoc;
	}

	@Override
	public String getNombLinea(final BigDecimal codlinea) throws BusinessException {

		String nombLinea = "";
		try {

			if (codlinea != null) {
				return documentacionAgroseguroDao.getNombLinea(codlinea);
			}

		} catch (Exception ex) {

			logger.error("borrarDocsAgroseguro error. " + ex);
			throw new BusinessException("Se ha producido al obtener borrarDocsAgroseguro:", ex);
		}

		return nombLinea;
	}

	@Override
	public String getNombEntidad(final BigDecimal codEntidad) throws BusinessException {

		String nombEntidad = "";
		try {

			if (codEntidad != null) {
				return documentacionAgroseguroDao.getNombreEntidad(codEntidad);
			}

		} catch (Exception ex) {

			logger.error("borrarDocsAgroseguro error. " + ex);
			throw new BusinessException("Se ha producido al obtener borrarDocsAgroseguro:", ex);
		}

		return nombEntidad;
	}

	/* Pet. 79014 ** MODIF TAM (22.03.2022) ** Inicio */
}