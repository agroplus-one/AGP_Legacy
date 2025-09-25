package com.rsi.agp.core.jmesa.service.impl.ganado;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmesa.facade.TableFacade;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.jmesa.dao.IExplotacionDAO;
import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.core.jmesa.filter.IGenericoFilter;
import com.rsi.agp.core.jmesa.service.impl.GetTablaService;
import com.rsi.agp.core.jmesa.sort.IGenericoSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;

public class ExplotacionesService extends GetTablaService 
	implements IExplotacionesService{
	private IExplotacionDAO explotacionDAO;
	private IGenericoFilter explotacionesFilter;
	private IGenericoSort explotacionesSort;

	// Constantes para las columnas de la tabla
	private final static String POLIZAID = "poliza.idpoliza";
	private final static String ID = "id";
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
	private final static String VACIO = "";


	@Override
	public String getTabla(final HttpServletRequest request,
			final HttpServletResponse response,
			final Serializable explotacionBean, final String origenLlamada,
			final List<BigDecimal> listaGrupoEntidades,
			final IGenericoDao genericoDao, final Boolean esModoLectura) {
		return getTabla(request, response, explotacionBean, origenLlamada, esModoLectura);
	}

	private String getTabla(final HttpServletRequest request,
			final HttpServletResponse response,
			final Serializable explotacionBean, final String origenLlamada, final Boolean esModoLectura) {
		Explotacion explotacion = (Explotacion) explotacionBean;
		cargarColumnas();
		TableFacade tableFacade = this.crearTableFacade(request, response,
				origenLlamada, columnas);
		setColumnasVisibles(tableFacade);
		
		// MPM - 20150527 - 21:00h - Corrección para evitar la mezcla de explotaciones al volver al listado de explotaciones
		String ajax = request.getParameter("ajax");
		//if (origenLlamada != null) {
		if (!"true".equals(ajax)) {
		explotacionesFilter.clear();
		if (null != tableFacade.getLimit().getFilterSet().getFilters()) {
			tableFacade.getLimit().getFilterSet().getFilters().clear();
		}
		cargarFiltrosBusqueda(explotacion, tableFacade);
		}
		
		explotacionesSort.clear();
		this.setDataAndLimitVariables(tableFacade, explotacionesFilter,
				explotacionDAO, explotacionesSort, explotacionesSort);
		
		if (ajax!=null && !"false".equals(ajax)) {
			tableFacade.setToolbar(new CustomToolbar());
			tableFacade.setView(new CustomView());
		}
		return html(tableFacade, esModoLectura);
	}

	@SuppressWarnings("deprecation")
	private String html(final TableFacade tableFacade, final Boolean esModoLectura) {
		// Configuracion de los datos de las columnas que requieren un
		// tratamiento para mostrarse
		// campo acciones
		HtmlTable table = (HtmlTable) tableFacade.getTable();
		table.getRow().setUniqueProperty("id");
		configurarColumnas(table);
		table.getRow().getColumn(columnas.get(ID)).getCellRenderer()
				.setCellEditor(getCellEditorAcciones(esModoLectura));
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
				.setCellEditor(getCellEditorPrecio());
		return tableFacade.render();
	}

	private CellEditor getCellEditorAcciones(final Boolean esModoLectura) {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				HtmlBuilder html = new HtmlBuilder();
				
				Long id = (Long) new BasicCellEditor().getValue(item, property,
						rowcount);
				
				String codigoRega = (String) new BasicCellEditor().getValue(item, REGA,
						rowcount);
				
				if(!esModoLectura) {
					html.a().href().quote().append("#").quote().onclick("editar('" + id + "');").close();
					html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Explotaci&oacute;n\" title=\"Editar Explotaci&oacute;n\"/>");
					html.aEnd();
					
					html.append("&nbsp;");
					html.a().href().quote().append("#").quote().onclick("duplicar('" + id + "');").close();
					html.append("<img src=\"jsp/img/displaytag/duplicar.png\" alt=\"Duplicar Explotaci&oacute;n\" title=\"Duplicar Explotaci&oacute;n\"/>");
					html.aEnd();
					html.append("&nbsp;");
					
					html.a().href().quote().append("#").quote().onclick("borrar('" + id + "');").close();
					html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Explotaci&oacute;n\" title=\"Borrar Explotaci&oacute;n\"/>");
					html.aEnd();
				}else {
					html.a().href().quote().append("#").quote().onclick("editar('" + id + "','"+esModoLectura+"');").close();					                                            
					html.append("<img src=\"jsp/img/displaytag/information.png\" alt=\"Consultar Explotaci&oacute;n\" title=\"Consultar Explotaci&oacute;n\"/>");
					html.aEnd();
				}
				
				html.a().href().quote().append("#").quote().onclick("verInformacionRega('" + codigoRega + "');").close();					                                            
				html.append("<img src=\"jsp/img/displaytag/report.png\" alt=\"Ver informaci&oacute;n REGA\" title=\"Ver informaci&oacute;n REGA\"/>");
				html.aEnd();
				
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
	
	private CellEditor getCellEditorPrecio() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
//				BigDecimal precio = (BigDecimal) new BasicCellEditor().getValue(item,
//						property, rowcount);
//				precio.stripTrailingZeros();
//				HtmlBuilder html = new HtmlBuilder();
//				html.append(precio);
//				return html.toString();
//				
				List<String> lista = (List<String>) new BasicCellEditor()
					.getValue(item, property, rowcount);
				
				HtmlBuilder html = new HtmlBuilder();
				if(null!=lista && lista.size()>0){
					for (String element : lista) {
						element=element.replace(".", "");//quitamos los puntos de los miles
						element=element.replace(",", ".");
						BigDecimal precio= new BigDecimal(element);
						BigDecimal prec = precio.stripTrailingZeros();
						html.append(getElementLabel(prec.toPlainString()));
						html.br();
					}					
				}else{
					html.append("&nbsp;");
				}
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
		this.configColumna(table, columnas.get(ID), "Acciones", false, false,
				"8%");
		
		this.configColumna(table, columnas.get(NUMEROEXP), "Nº", false,
				true, "2%");		
		this.configColumna(table, columnas.get(CODPROVINCIA), "PRV", false,
				true, "3%");
		this.configColumna(table, columnas.get(CODCOMARCA), "CMC", false, true,
				"3%");
		this.configColumna(table, columnas.get(CODTERMINO), "TRM", false, true,
				"3%");
		this.configColumna(table, columnas.get(CODSUBTERMINO), "SBT", false,
				true, "3%");
		this.configColumna(table, columnas.get(REGA), "REGA", false, true, "8%");
		this.configColumna(table, columnas.get(SIGLA), "Sigla", false, true,
				"3%");
		this.configColumna(table, columnas.get(SUBEXPLOTACION), "Subexpl.",
				false, true, "3%");
		this.configColumna(table, columnas.get(ESPECIE), "Especie", false,
				true, "12%");
		this.configColumna(table, columnas.get(REGIMEN), "Régimen", false,
				true, "12%");
		this.configColumna(table, columnas.get(GRUPORAZA), "Grupo Raza", false,
				false, "12%");
		this.configColumna(table, columnas.get(TIPOCAPITAL), "T. Capital",
				false, false, "11%");
		this.configColumna(table, columnas.get(TIPOANIMAL), "T. Animal", false,
				false, "11%");
		this.configColumna(table, columnas.get(NUMERO), "Núm.", false, false,
				"3%");		
		this.configColumna(table, columnas.get(PRECIO), "Precio", false, false,
				"3%");
	}

	private void cargarColumnas() {
		// Crea el Map con las columnas del listado y los campos del filtro de
		// busqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(POLIZAID, POLIZAID);
			columnas.put(ID, ID);
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
				columnas.get(NUMERO), columnas.get(PRECIO));
	}

	private void cargarFiltrosBusqueda(final Explotacion explotacion,
			final TableFacade tableFacade) {
		if (explotacion.getPoliza() != null
				&& explotacion.getPoliza().getIdpoliza() != null) {
			this.addColumnaFiltro(tableFacade, POLIZAID, explotacion
					.getPoliza().getIdpoliza(), explotacionesFilter);
		}
		if (explotacion.getId() != null) {
			this.addColumnaFiltro(tableFacade, ID, explotacion.getId(),
					explotacionesFilter);
		}
		if (explotacion.getTermino() != null
				&& explotacion.getTermino().getId() != null
				&& explotacion.getTermino().getId().getCodprovincia() != null) {
			this.addColumnaFiltro(tableFacade, CODPROVINCIA, explotacion
					.getTermino().getId().getCodprovincia(),
					explotacionesFilter);
		}
		if (explotacion.getTermino() != null
				&& explotacion.getTermino().getId() != null
				&& explotacion.getTermino().getId().getCodcomarca() != null) {
			this.addColumnaFiltro(tableFacade, CODCOMARCA, explotacion
					.getTermino().getId().getCodcomarca(), explotacionesFilter);
		}
		if (explotacion.getTermino() != null
				&& explotacion.getTermino().getId() != null
				&& explotacion.getTermino().getId().getCodtermino() != null) {
			this.addColumnaFiltro(tableFacade, CODTERMINO, explotacion
					.getTermino().getId().getCodtermino(), explotacionesFilter);
		}
		if (explotacion.getTermino() != null
				&& explotacion.getTermino().getId() != null
				&& explotacion.getTermino().getId().getSubtermino() != null) {
			this.addColumnaFiltro(tableFacade, CODSUBTERMINO, explotacion
					.getTermino().getId().getSubtermino(), explotacionesFilter);
		}
		if (explotacion.getLatitud() != null) {
			this.addColumnaFiltro(tableFacade, LATITUD,
					explotacion.getLatitud(), explotacionesFilter);
		}
		if (explotacion.getLongitud() != null) {
			this.addColumnaFiltro(tableFacade, LONGITUD,
					explotacion.getLongitud(), explotacionesFilter);
		}
		if (explotacion.getRega() != null
				&& !VACIO.equals(explotacion.getRega().trim())) {
			this.addColumnaFiltro(tableFacade, REGA, explotacion.getRega(),
					explotacionesFilter);
		}
		if (explotacion.getSigla() != null
				&& !VACIO.equals(explotacion.getSigla().trim())) {
			this.addColumnaFiltro(tableFacade, SIGLA, explotacion.getSigla(),
					explotacionesFilter);
		}
		if (explotacion.getSubexplotacion() != null) {
			this.addColumnaFiltro(tableFacade, SUBEXPLOTACION,
					explotacion.getSubexplotacion(), explotacionesFilter);
		}
		if (explotacion.getEspecie() != null) {
			this.addColumnaFiltro(tableFacade, ESPECIE,
					explotacion.getEspecie(), explotacionesFilter);
		}
		if (explotacion.getRegimen() != null) {
			this.addColumnaFiltro(tableFacade, REGIMEN,
					explotacion.getRegimen(), explotacionesFilter);
		}
	}

	public void setExplotacionDAO(final IExplotacionDAO explotacionDAO) {
		this.explotacionDAO = explotacionDAO;
	}

	public void setExplotacionesFilter(final IGenericoFilter explotacionesFilter) {
		this.explotacionesFilter = explotacionesFilter;
	}

	public void setExplotacionesSort(final IGenericoSort explotacionesSort) {
		this.explotacionesSort = explotacionesSort;
	}
}