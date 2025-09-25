package com.rsi.agp.core.jmesa.service.impl.ganado;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmesa.facade.TableFacade;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.core.jmesa.filter.IGenericoFilter;
import com.rsi.agp.core.jmesa.service.impl.GetTablaService;
import com.rsi.agp.core.jmesa.sort.IGenericoSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.models.poliza.ganado.IExplotacionAnexoDao;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo;

public class ExplotacionesAnexoService extends GetTablaService {

	private IExplotacionAnexoDao explotacionAnexoDao;
	private IGenericoFilter explotacionesAnexoFilter;
	private IGenericoSort explotacionesAnexoSort;

	private Log logger = LogFactory.getLog(getClass());

	// Constantes para las columnas de la tabla
	private final static String ID = "id";
	private final static String ID_ANEXO_MOD = "anexoModificacion.id";
	private final static String NUMEROEXP="numero";
	private final static String CODPROVINCIA = "termino.id.codprovincia";
	private final static String CODCOMARCA = "termino.id.codcomarca";
	private final static String CODTERMINO = "termino.id.codtermino";
	private final static String CODSUBTERMINO = "termino.id.subtermino";
	private final static String LATITUD = "latitud";
	private final static String LONGITUD = "longitud";
	private final static String REGA = "rega";
	private final static String SIGLA = "sigla";
	private final static String SUBEXPLOTACION = "subexplotacion";
	private final static String ESPECIE = "especie";
	private final static String NOMESPECIE = "nomespecie";
	private final static String REGIMEN = "regimen";
	private final static String NOMREGIMEN = "nomregimen";
	private final static String GRUPORAZA = "grupoRazasCols("
			+ Explotacion.GR_RAZA + ")";
	private final static String TIPOCAPITAL = "grupoRazasCols("
			+ Explotacion.GR_TIPOCAPITAL + ")";
	private final static String TIPOANIMAL = "grupoRazasCols("
			+ Explotacion.GR_TIPOANIMAL + ")";
	private final static String NUMERO = "grupoRazasCols("
			+ Explotacion.GR_NUMERO + ")";
	private final static String PRECIO = "grupoRazasCols("
			+ Explotacion.GR_PRECIO + ")";
	private final static String TIPO_MODIF = "tipoModificacion";
	
	private final static String VACIO = "";

	public void borrarExplotacionAnexo(final Long idExplotacionAnexo) throws Exception {
		try {
			ExplotacionAnexo explotacionAnexo = (ExplotacionAnexo) explotacionAnexoDao.get(
					ExplotacionAnexo.class, idExplotacionAnexo);
			if (explotacionAnexo != null) {
				explotacionAnexoDao.delete(explotacionAnexo);
			}
		} catch (Exception ex) {
			logger.debug("Error al borrar la explotación anexo.", ex);
			throw ex;
		}
	}

	@Override
	public String getTabla(final HttpServletRequest request,
			final HttpServletResponse response,
			final Serializable explotacionAnexoBean, final String origenLlamada,
			final List<BigDecimal> listaGrupoEntidades,
			final IGenericoDao genericoDao) {
		return getTabla(request, response, explotacionAnexoBean, origenLlamada);
	}

	private String getTabla(final HttpServletRequest request,
			final HttpServletResponse response,
			final Serializable explotacionAnexoBean, final String origenLlamada) {
		ExplotacionAnexo explotacion = (ExplotacionAnexo) explotacionAnexoBean;
		cargarColumnas();
		TableFacade tableFacade = this.crearTableFacade(request, response,
				origenLlamada, columnas);
		setColumnasVisibles(tableFacade);
		if (origenLlamada != null) {
			explotacionesAnexoFilter.clear();
			if (null != tableFacade.getLimit().getFilterSet().getFilters()) {
				tableFacade.getLimit().getFilterSet().getFilters().clear();
			}
			cargarFiltrosBusqueda(explotacion, tableFacade);
			//Fijo
			addColumnaFiltro(tableFacade, ID_ANEXO_MOD, explotacion.getAnexoModificacion().getId(), explotacionesAnexoFilter);
		}

		explotacionesAnexoSort.clear();
		this.setDataAndLimitVariables(tableFacade, explotacionesAnexoFilter,
				explotacionAnexoDao, explotacionesAnexoSort, explotacionesAnexoSort);
		String ajax = request.getParameter("ajax");
		if (!"false".equals(ajax)) {
			tableFacade.setToolbar(new CustomToolbar());
			tableFacade.setView(new CustomView());
		}
		return html(tableFacade, request);
	}

	@SuppressWarnings("deprecation")
	private String html(final TableFacade tableFacade, final HttpServletRequest request) {
		// Configuracion de los datos de las columnas que requieren un
		// tratamiento para mostrarse
		// campo acciones
		HtmlTable table = (HtmlTable) tableFacade.getTable();
		table.getRow().setUniqueProperty("id");
		configurarColumnas(table);
		
		String modoLectura = request.getParameter("modoLectura");
		//Ajuste por si viene por getAttribute en vez de getParameter
		if(modoLectura==null){
			modoLectura = (String)request.getAttribute("modoLectura");
		}
		
		if(modoLectura!=null && (modoLectura.equals("true") || modoLectura.equals("modoLectura"))){
			table.getRow().getColumn(columnas.get(ID)).getCellRenderer()
				.setCellEditor(getCellEditorAccionesSoloLectura());			
		}else{
			table.getRow().getColumn(columnas.get(ID)).getCellRenderer()
				.setCellEditor(getCellEditorAcciones());
		}
		table.getRow().getColumn(columnas.get(ESPECIE)).getCellRenderer()
				.setCellEditor(getCellEditorCodDesc(NOMESPECIE));
		table.getRow().getColumn(columnas.get(REGIMEN)).getCellRenderer()
				.setCellEditor(getCellEditorCodDesc(NOMREGIMEN));
		table.getRow().getColumn(columnas.get(GRUPORAZA)).getCellRenderer()
				.setCellEditor(getCellEditorList());
		table.getRow().getColumn(columnas.get(TIPOCAPITAL)).getCellRenderer()
				.setCellEditor(getCellEditorList());
		table.getRow().getColumn(columnas.get(TIPOANIMAL)).getCellRenderer()
				.setCellEditor(getCellEditorList());
		table.getRow().getColumn(columnas.get(NUMERO)).getCellRenderer()
				.setCellEditor(getCellEditorList());
		table.getRow().getColumn(columnas.get(PRECIO)).getCellRenderer()
				.setCellEditor(getCellEditorList());
		return tableFacade.render();
	}

	private CellEditor getCellEditorAcciones() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				HtmlBuilder html = new HtmlBuilder();
				
				Character tipoModificacion = (Character) new BasicCellEditor().getValue(item, TIPO_MODIF, rowcount);
				
				Long id = (Long) new BasicCellEditor().getValue(item, property, rowcount);

				if(tipoModificacion==null){//Sin modificar
					
					html.a().href().quote().append("#").quote()
						.onclick("editarExplotacionAnexo('" + id + "');").close();
					html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Explotación\" title=\"Editar Explotación\"/>");
					html.aEnd();
					html.append("&nbsp;");
					
					html.a().href().quote().append("#").quote()
						.onclick("borrarExplotacionAnexo('" + id + "');").close();
					html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Explotación\" title=\"Borrar Explotación\"/>");
					html.aEnd();
				
				}else if(Constants.ALTA.equals(tipoModificacion)){//Es un alta
					
					html.a().href().quote().append("#").quote()
						.onclick("editarExplotacionAnexo('" + id + "');").close();
					html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Explotación\" title=\"Editar Explotación\"/>");
					html.aEnd();
					html.append("&nbsp;");
					
					html.a().href().quote().append("#").quote()
						.onclick("deshacerExplotacionAnexo('" + id + "');").close();
					html.append("<img src=\"jsp/img/displaytag/deshacer.png\" alt=\"Deshacer Cambios\" title=\"Deshacer Cambios\"/>");
					html.aEnd();
					html.append("&nbsp;");
					
				}else if(Constants.MODIFICACION.equals(tipoModificacion)){//Es un alta
					
					html.a().href().quote().append("#").quote()
						.onclick("editarExplotacionAnexo('" + id + "');").close();
					html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Explotación\" title=\"Editar Explotación\"/>");
					html.aEnd();
					html.append("&nbsp;");
					
					html.a().href().quote().append("#").quote()
						.onclick("deshacerExplotacionAnexo('" + id + "');").close();
					html.append("<img src=\"jsp/img/displaytag/deshacer.png\" alt=\"Deshacer Cambios\" title=\"Deshacer Cambios\"/>");
					html.aEnd();
					html.append("&nbsp;");
					
				}else if(Constants.BAJA.equals(tipoModificacion)){//Es una baja
					
					html.a().href().quote().append("#").quote()
							.onclick("deshacerExplotacionAnexo('" + id + "');").close();
					html.append("<img src=\"jsp/img/displaytag/deshacer.png\" alt=\"Deshacer Cambios\" title=\"Deshacer Cambios\"/>");
					html.aEnd();
					html.append("&nbsp;");
					
				}
				
				return html.toString();
			}
		};
	}

	private CellEditor getCellEditorAccionesSoloLectura() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				HtmlBuilder html = new HtmlBuilder();

				Long id = (Long) new BasicCellEditor().getValue(item, property, rowcount);
			
				html.a().href().quote().append("#").quote()
						.onclick("visualizarDatosRegistro('" + id + "');").close();
				html.append("<img src=\"jsp/img/displaytag/information.png\" alt=\"Visualizar información\" title=\"Visualizar informacion\"/>");
				html.aEnd();
				html.append("&nbsp;");
				
				return html.toString();
			}
		};
	}

	
	private CellEditor getCellEditorCodDesc(final String descKey) {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				Long codigo = (Long) new BasicCellEditor().getValue(item,
						property, rowcount);
				String descripcion = (String) new BasicCellEditor().getValue(
						item, descKey, rowcount);
				HtmlBuilder html = new HtmlBuilder();
				html.append(codigo + "-" + descripcion);
				return html.toString();
			}
		};
	}

	@SuppressWarnings("unchecked")
	private CellEditor getCellEditorList() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				List<String> lista = (List<String>) new BasicCellEditor()
						.getValue(item, property, rowcount);
				HtmlBuilder html = new HtmlBuilder();
				for (String element : lista) {
					html.append(getElementLabel(element));
					html.br();
				}
				return html.toString();
			}
		};
	}

	private String getElementLabel(final String element) {
		String shortText = element;
		if (shortText.length() > 10) {
			shortText = shortText.substring(0, 10).concat("...");
		}
		HtmlBuilder html = new HtmlBuilder();
		html.label().title(element).close().append(shortText).labelEnd();
		return html.toString();
	}

	private void configurarColumnas(final HtmlTable table) {
		this.configColumna(table, columnas.get(ID), "Acciones", false, false, "8%");
		this.configColumna(table, columnas.get(NUMEROEXP), "Nº", false,true, "2%");	
		this.configColumna(table, columnas.get(CODPROVINCIA), "PRV", false,	true, "3%");
		this.configColumna(table, columnas.get(CODCOMARCA), "CMC", false, true, "3%");
		this.configColumna(table, columnas.get(CODTERMINO), "TRM", false, true, "3%");
		this.configColumna(table, columnas.get(CODSUBTERMINO), "SBT", false, true, "3%");
		this.configColumna(table, columnas.get(REGA), "REGA", false, true, "8%");
		this.configColumna(table, columnas.get(SIGLA), "Sigla", false, true, "3%");
		this.configColumna(table, columnas.get(SUBEXPLOTACION), "Subexpl.", false, true, "3%");
		this.configColumna(table, columnas.get(ESPECIE), "Especie", false, true, "12%");
		this.configColumna(table, columnas.get(REGIMEN), "Régimen", false, true, "12%");
		this.configColumna(table, columnas.get(GRUPORAZA), "Grupo Raza", false, false, "12%");
		this.configColumna(table, columnas.get(TIPOCAPITAL), "T. Capital", false, false, "11%");
		this.configColumna(table, columnas.get(TIPOANIMAL), "T. Animal", false, false, "11%");
		this.configColumna(table, columnas.get(NUMERO), "Núm.", false, false, "3%");
		this.configColumna(table, columnas.get(PRECIO), "Precio", false, false, "3%");
		this.configColumna(table, columnas.get(TIPO_MODIF), "T.Mod", false, true, "3%");
	}

	private void cargarColumnas() {
		// Crea el Map con las columnas del listado y los campos del filtro de
		// busqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			//columnas.put(POLIZAID, POLIZAID);
			columnas.put(ID, ID);
			columnas.put(ID_ANEXO_MOD, ID_ANEXO_MOD);
			columnas.put(NUMEROEXP, NUMEROEXP);
			columnas.put(CODPROVINCIA, CODPROVINCIA);
			columnas.put(CODCOMARCA, CODCOMARCA);
			columnas.put(CODTERMINO, CODTERMINO);
			columnas.put(CODSUBTERMINO, CODSUBTERMINO);
			columnas.put(LATITUD, LATITUD);
			columnas.put(LONGITUD, LONGITUD);
			columnas.put(REGA, REGA);
			columnas.put(SIGLA, SIGLA);
			columnas.put(SUBEXPLOTACION, SUBEXPLOTACION);
			columnas.put(ESPECIE, ESPECIE);
			columnas.put(NOMESPECIE, NOMESPECIE);
			columnas.put(REGIMEN, REGIMEN);
			columnas.put(NOMREGIMEN, NOMREGIMEN);
			columnas.put(GRUPORAZA, GRUPORAZA);
			columnas.put(TIPOCAPITAL, TIPOCAPITAL);
			columnas.put(TIPOANIMAL, TIPOANIMAL);
			columnas.put(NUMERO, NUMERO);
			columnas.put(PRECIO, PRECIO);
			columnas.put(TIPO_MODIF, TIPO_MODIF);
		}
	}

	@SuppressWarnings("deprecation")
	private void setColumnasVisibles(final TableFacade tableFacade) {
		tableFacade.setColumnProperties(columnas.get(ID),columnas.get(NUMEROEXP),
				columnas.get(CODPROVINCIA), columnas.get(CODCOMARCA),
				columnas.get(CODTERMINO), columnas.get(CODSUBTERMINO),
				columnas.get(REGA), columnas.get(SIGLA),
				columnas.get(SUBEXPLOTACION), columnas.get(ESPECIE),
				columnas.get(REGIMEN), columnas.get(GRUPORAZA),
				columnas.get(TIPOCAPITAL), columnas.get(TIPOANIMAL),
				columnas.get(NUMERO), columnas.get(PRECIO),
				columnas.get(TIPO_MODIF));
	}

	private void cargarFiltrosBusqueda(final ExplotacionAnexo explotacionAnexo,
			final TableFacade tableFacade) {

		if (explotacionAnexo.getId() != null) {
			this.addColumnaFiltro(tableFacade, ID, explotacionAnexo.getId(),
					explotacionesAnexoFilter);
		}
		if (explotacionAnexo.getTermino() != null
				&& explotacionAnexo.getTermino().getId() != null
				&& explotacionAnexo.getTermino().getId().getCodprovincia() != null) {
			this.addColumnaFiltro(tableFacade, CODPROVINCIA, explotacionAnexo
					.getTermino().getId().getCodprovincia(),
					explotacionesAnexoFilter);
		}
		if (explotacionAnexo.getTermino() != null
				&& explotacionAnexo.getTermino().getId() != null
				&& explotacionAnexo.getTermino().getId().getCodcomarca() != null) {
			this.addColumnaFiltro(tableFacade, CODCOMARCA, explotacionAnexo
					.getTermino().getId().getCodcomarca(), explotacionesAnexoFilter);
		}
		if (explotacionAnexo.getTermino() != null
				&& explotacionAnexo.getTermino().getId() != null
				&& explotacionAnexo.getTermino().getId().getCodtermino() != null) {
			this.addColumnaFiltro(tableFacade, CODTERMINO, explotacionAnexo
					.getTermino().getId().getCodtermino(), explotacionesAnexoFilter);
		}
		if (explotacionAnexo.getTermino() != null
				&& explotacionAnexo.getTermino().getId() != null
				&& explotacionAnexo.getTermino().getId().getSubtermino() != null) {
			this.addColumnaFiltro(tableFacade, CODSUBTERMINO, explotacionAnexo
					.getTermino().getId().getSubtermino(), explotacionesAnexoFilter);
		}
		if (explotacionAnexo.getLatitud() != null) {
			this.addColumnaFiltro(tableFacade, LATITUD,
					explotacionAnexo.getLatitud(), explotacionesAnexoFilter);
		}
		if (explotacionAnexo.getLongitud() != null) {
			this.addColumnaFiltro(tableFacade, LONGITUD,
					explotacionAnexo.getLongitud(), explotacionesAnexoFilter);
		}
		if (explotacionAnexo.getRega() != null
				&& !VACIO.equals(explotacionAnexo.getRega().trim())) {
			this.addColumnaFiltro(tableFacade, REGA, explotacionAnexo.getRega(),
					explotacionesAnexoFilter);
		}
		if (explotacionAnexo.getSigla() != null
				&& !VACIO.equals(explotacionAnexo.getSigla().trim())) {
			this.addColumnaFiltro(tableFacade, SIGLA, explotacionAnexo.getSigla(),
					explotacionesAnexoFilter);
		}
		if (explotacionAnexo.getSubexplotacion() != null) {
			this.addColumnaFiltro(tableFacade, SUBEXPLOTACION,
					explotacionAnexo.getSubexplotacion(), explotacionesAnexoFilter);
		}
		if (explotacionAnexo.getEspecie() != null) {
			this.addColumnaFiltro(tableFacade, ESPECIE,
					explotacionAnexo.getEspecie(), explotacionesAnexoFilter);
		}
		if (explotacionAnexo.getRegimen() != null) {
			this.addColumnaFiltro(tableFacade, REGIMEN,
					explotacionAnexo.getRegimen(), explotacionesAnexoFilter);
		}
		if (explotacionAnexo.getTipoModificacion() != null) {
			this.addColumnaFiltro(tableFacade, TIPO_MODIF,
					explotacionAnexo.getTipoModificacion(), explotacionesAnexoFilter);
		}
	}

	public void setExplotacionAnexoDao(final IExplotacionAnexoDao explotacionAnexoDao) {
		this.explotacionAnexoDao = explotacionAnexoDao;
	}

	public void setExplotacionesAnexoFilter(final IGenericoFilter explotacionesAnexoFilter) {
		this.explotacionesAnexoFilter = explotacionesAnexoFilter;
	}

	public void setExplotacionesAnexoSort(final IGenericoSort explotacionesAnexoSort) {
		this.explotacionesAnexoSort = explotacionesAnexoSort;
	}
}