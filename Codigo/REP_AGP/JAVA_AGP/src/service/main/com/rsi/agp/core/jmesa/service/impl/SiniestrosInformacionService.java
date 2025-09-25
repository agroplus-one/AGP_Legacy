package com.rsi.agp.core.jmesa.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.ExportType;
import org.jmesa.limit.Limit;
import org.jmesa.view.component.Column;
import org.jmesa.view.component.Row;
import org.jmesa.view.component.Table;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.core.jmesa.filter.IGenericoFilter;
import com.rsi.agp.core.jmesa.service.ISiniestrosInformacionService;
import com.rsi.agp.core.jmesa.sort.IGenericoSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.webapp.action.DatosParcelaController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.VistaPlzHojaCampoActaTasacion;

public class SiniestrosInformacionService extends GetTablaService implements ISiniestrosInformacionService {
	
	private final Log logger = LogFactory.getLog(DatosParcelaController.class);
	
	private final static String ID="id";
	private final static String IDPOLIZA="idpoliza";
	private final static String REFERENCIA="referencia";
	private final static String PLAN="plan"; 
	private final static String TIPOREGISTRO="tipoRegistro";
	private final static String NIF="nif";
	private final static String NUMHOJACAMPO="numHojaCampo";
	private final static String FECHATASACION="fechaTasacion";
	private final static String TIPOHOJA="tipoHoja";
	private final static String TIPOHOJADESC="tipoHojaDesc";
	private final static String SITUACIONHOJA="situacionHoja";
	private final static String SITUACIONHOJADESC="situacionHojaDesc";
	private final static String SERIE="serie";
	private final static String NUMACTA="numActa";
	private final static String FECHAACTA="fechaActa";
	private final static String SITUACIONACTA="situacionActa";
	private final static String SITUACIONACTADES="situacionActaDes";
	private final static String IMPORTE="importe";
	private final static String IMPORTEREGULARIZADO="importeRegularizado";
	private final static String FECHAPAGO="fechaPago";
	
	// Titulos columnas del informe excel
	private static final String LISTADO_SINIESTROS_INFORMACION = "LISTADO SINIESTROS INFORMACIÓN";
	private static final String TIT_COL_TIPO_REGISTRO = "Tipo Registro";
	private static final String TIT_COL_NIF = "NIF CIF";
	private static final String TIT_COL_NUM_HOJACAMPO = "Num. Hoja Campo";
	private static final String TIT_COL_FECHA_TASACION = "Fecha Tasación";
	private static final String TIT_COL_TIPO_HOJA = "Tipo Hoja";
	private static final String TIT_COL_SITUACION_HOJA_DESC = "Situación Hoja Desc";
	private static final String TIT_COL_SERIE =  "Serie";
	private static final String TIT_COL_NUMACTA = "Num. Acta";
	private static final String TIT_COL_FECHA_ACTA = "Fecha Acta";
	private static final String TIT_COL_SITUACION_ACTA = "Situación Acta";
	private static final String TIT_COL_IMPORTE = "Importe";
	private static final String TIT_COL_IMPORTE_REGULARIZADO = "Importe regularizado";
	private static final String TIT_COL_FECHA_PAGO = "Fecha Pago";

	private IGenericoDao siniestrosInformacionDao;
	private IGenericoFilter siniestrosInformacionFilter;
	private IGenericoSort siniestrosInformacionSort;
	
	@Override
	public Poliza getPoliza(Long id) throws Exception {
		Poliza poliza=null;
		try {
			poliza=(Poliza) siniestrosInformacionDao.getObject(Poliza.class, id);
		} catch (Exception e) {
			throw e;
		}		
		return poliza;
	}
	
	@Override
	public String getTabla(HttpServletRequest request,
			HttpServletResponse response, java.io.Serializable objetoBean,
			String origenLlamada,List<BigDecimal> listaGrupoEntidades, IGenericoDao genericoDao){
		
		VistaPlzHojaCampoActaTasacion vistaBean=(VistaPlzHojaCampoActaTasacion)objetoBean;
		
		cargarColumnas();
		TableFacade tableFacade= this.crearTableFacade(request, response, origenLlamada, columnas);
		tableFacade.autoFilterAndSort(false);
		tableFacade.setExportTypes(response, ExportType.EXCEL);
		
		setColumnasVisibles(tableFacade);
		
		if (origenLlamada != null) {
			siniestrosInformacionFilter.clear();
			if(null!=tableFacade.getLimit().getFilterSet().getFilters()) {
				tableFacade.getLimit().getFilterSet().getFilters().clear();
			}			
			try {
				cargarFiltrosBusqueda(vistaBean, tableFacade);
			} catch (ParseException e) {
				logger.error("Excepcion : SiniestrosInformacionService - getTabla", e);
			}
		}		
		
		siniestrosInformacionSort.clear();		

		this.setDataAndLimitVariables(tableFacade, siniestrosInformacionFilter, siniestrosInformacionDao, 
				siniestrosInformacionSort, siniestrosInformacionSort);
		
		String ajax = request.getParameter("ajax");
		if (!"false".equals(ajax)){
			tableFacade.setToolbar(new CustomToolbar());
			tableFacade.setView(new CustomView());
		}

		return html(request, tableFacade);//+ script;
		
	}
	

	private void cargarColumnas() {
		// Crea el Map con las columnas del listado y los campos del filtro de
		// busqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID, ID);
			columnas.put(IDPOLIZA,IDPOLIZA);			
			columnas.put(REFERENCIA,REFERENCIA);
			columnas.put(PLAN,PLAN);			
			columnas.put(TIPOREGISTRO,TIPOREGISTRO);
			columnas.put(NIF,NIF);
			columnas.put(NUMHOJACAMPO,NUMHOJACAMPO);
			columnas.put(FECHATASACION,FECHATASACION);
			columnas.put(TIPOHOJA,TIPOHOJA);
			columnas.put(TIPOHOJADESC,TIPOHOJADESC);
			columnas.put(SITUACIONHOJA,SITUACIONHOJA);
			columnas.put(SITUACIONHOJADESC,SITUACIONHOJADESC);
			columnas.put(SERIE,SERIE);
			columnas.put(NUMACTA,NUMACTA);
			columnas.put(FECHAACTA,FECHAACTA);
			columnas.put(SITUACIONACTA,SITUACIONACTA);
			columnas.put(SITUACIONACTADES,SITUACIONACTADES);
			columnas.put(IMPORTE,IMPORTE);
			columnas.put(IMPORTEREGULARIZADO,IMPORTEREGULARIZADO);
			columnas.put(FECHAPAGO,FECHAPAGO);
		}
	}	
	
	@SuppressWarnings("deprecation")
	private void setColumnasVisibles(TableFacade tableFacade){
		tableFacade.setColumnProperties(
				columnas.get(ID),				columnas.get(TIPOREGISTRO),			columnas.get(NIF),				
				columnas.get(NUMHOJACAMPO),		columnas.get(FECHATASACION),		columnas.get(TIPOHOJA),			
				columnas.get(SITUACIONHOJADESC ),	columnas.get(SERIE),				columnas.get(NUMACTA ),				
				columnas.get(FECHAACTA),		columnas.get(SITUACIONACTA ),		columnas.get(IMPORTE),
				columnas.get(IMPORTEREGULARIZADO),columnas.get(FECHAPAGO ));
	}
	
	private void cargarFiltrosBusqueda(VistaPlzHojaCampoActaTasacion vistaBean, TableFacade tableFacade) throws ParseException {
		if(null!=vistaBean.getIdpoliza()) {
			this.addColumnaFiltro(tableFacade, IDPOLIZA, vistaBean.getIdpoliza(), siniestrosInformacionFilter);
		}
		if(null!= vistaBean.getTipoRegistro()) {
			this.addColumnaFiltro(tableFacade, TIPOREGISTRO, vistaBean.getTipoRegistro(), siniestrosInformacionFilter);
		}
		if(null!=vistaBean.getNif()) {
			this.addColumnaFiltro(tableFacade, NIF, vistaBean.getNif(), siniestrosInformacionFilter);
		}
		if(null!=vistaBean.getNumHojaCampo()) {
			this.addColumnaFiltro(tableFacade, NUMHOJACAMPO, vistaBean.getNumHojaCampo(), siniestrosInformacionFilter);
		}
		if(null!=vistaBean.getFechaTasacion()) {
			this.addColumnaFiltro(tableFacade, FECHATASACION, vistaBean.getFechaTasacion(), siniestrosInformacionFilter);
		}
		if(null!=vistaBean.getTipoHoja()) {
			this.addColumnaFiltro(tableFacade, TIPOHOJA, vistaBean.getTipoHoja(), siniestrosInformacionFilter);
		}		
		if(null!=vistaBean.getSituacionHoja()) {
			this.addColumnaFiltro(tableFacade, SITUACIONHOJA, vistaBean.getSituacionHoja(), siniestrosInformacionFilter);
		}		
		if(null!=vistaBean.getSerie()) {
			this.addColumnaFiltro(tableFacade, SERIE, vistaBean.getSerie(), siniestrosInformacionFilter);
		}		
		if(null!=vistaBean.getNumActa()) {
			this.addColumnaFiltro(tableFacade, NUMACTA, vistaBean.getNumActa(), siniestrosInformacionFilter);
		}
		if(null!=vistaBean.getFechaActa()) {
			this.addColumnaFiltro(tableFacade, FECHAACTA, vistaBean.getFechaActa(), siniestrosInformacionFilter);
		}
		if(null!=vistaBean.getSituacionActa()) {
			this.addColumnaFiltro(tableFacade, SITUACIONACTA, vistaBean.getSituacionActa(), siniestrosInformacionFilter);
		}
		if(null!=vistaBean.getImporte()) {
			this.addColumnaFiltro(tableFacade, IMPORTE, vistaBean.getImporte(), siniestrosInformacionFilter);
		}
		if(null!=vistaBean.getImporteRegularizado()) {
			this.addColumnaFiltro(tableFacade, IMPORTEREGULARIZADO, vistaBean.getImporteRegularizado(), siniestrosInformacionFilter);
		}
		if(null!=vistaBean.getFechaPago()) {
			this.addColumnaFiltro(tableFacade, FECHAPAGO, vistaBean.getFechaPago(), siniestrosInformacionFilter);
		}
	}
	
	
	@SuppressWarnings("deprecation")
	private String html(HttpServletRequest request, TableFacade tableFacade) {
		
		Limit limit = tableFacade.getLimit();
		
		if (limit.isExported()) {

			Table exportTable = tableFacade.getTable();
			// eliminamos la columna "Id"
			eliminarColumnaId(tableFacade, exportTable);
			
			// renombramos las cabeceras
			configurarCabecerasColumnasExport(exportTable);
			
			// configuramos cada columna
			configurarColumnasExport(request, exportTable);

			tableFacade.render();
			
			return null;

		} else {
			
			HtmlTable table = (HtmlTable) tableFacade.getTable();
			table.getRow().setUniqueProperty(ID);

			configurarColumnas(table);
			table.getRow().getColumn(columnas.get(TIPOREGISTRO)).getCellRenderer().setCellEditor(getCellEditorTipoRegistro(TIPOREGISTRO));		
			table.getRow().getColumn(columnas.get(TIPOHOJA)).getCellRenderer().setCellEditor(getCellEditorTipoDescripcion(TIPOHOJA, TIPOHOJADESC));
			//table.getRow().getColumn(columnas.get(SITUACIONHOJA)).getCellRenderer().setCellEditor(getCellEditorTipoDescripcion(SITUACIONHOJA,SITUACIONHOJADESC));
			table.getRow().getColumn(columnas.get(SITUACIONACTA)).getCellRenderer().setCellEditor(getCellEditorTipoDescripcion(SITUACIONACTA,SITUACIONACTADES));

			table.getRow().getColumn(columnas.get(ID)).getCellRenderer().setCellEditor(getCellEditorAcciones());
		}
		
		return tableFacade.render();
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
	
	/**
	 * 
	 * @param exportTable
	 */
	private void configurarCabecerasColumnasExport(Table table) {
		
		table.setCaption(LISTADO_SINIESTROS_INFORMACION);
		Row row = table.getRow();

		Column colTipoRegistro = row.getColumn(columnas.get(TIPOREGISTRO));
		colTipoRegistro.setTitle(TIT_COL_TIPO_REGISTRO);
		
		Column colNif = row.getColumn(columnas.get(NIF));
		colNif.setTitle(TIT_COL_NIF);
		
		Column colNumHojaCampo = row.getColumn(columnas.get(NUMHOJACAMPO));
		colNumHojaCampo.setTitle(TIT_COL_NUM_HOJACAMPO);
		
		Column colFechaTasacion = row.getColumn(columnas.get(FECHATASACION));
		colFechaTasacion.setTitle(TIT_COL_FECHA_TASACION);
		
		Column colTipoHoja = row.getColumn(columnas.get(TIPOHOJA));
		colTipoHoja.setTitle(TIT_COL_TIPO_HOJA);

		Column colSituacionHojaDesc = row.getColumn(columnas.get(SITUACIONHOJADESC));
		colSituacionHojaDesc.setTitle(TIT_COL_SITUACION_HOJA_DESC);		
		
		Column colSerie = row.getColumn(columnas.get(SERIE));
		colSerie.setTitle(TIT_COL_SERIE);
		
		Column colNumActa = row.getColumn(columnas.get(NUMACTA));
		colNumActa.setTitle(TIT_COL_NUMACTA);
		
		Column colFechaActa = row.getColumn(columnas.get(FECHAACTA));
		colFechaActa.setTitle(TIT_COL_FECHA_ACTA);
		
		Column colSituacionActa = row.getColumn(columnas.get(SITUACIONACTA));
		colSituacionActa.setTitle(TIT_COL_SITUACION_ACTA);
		
		Column colImporte = row.getColumn(columnas.get(IMPORTE));
		colImporte.setTitle(TIT_COL_IMPORTE);
		
		Column colImporteRegularizado = row.getColumn(columnas.get(IMPORTEREGULARIZADO));
		colImporteRegularizado.setTitle(TIT_COL_IMPORTE_REGULARIZADO);
		
		Column colFechaPago = row.getColumn(columnas.get(FECHAPAGO));
		colFechaPago.setTitle(TIT_COL_FECHA_PAGO);

	}
	
	/**
	 * 
	 * @param exportTable
	 */
	private void configurarColumnasExport(HttpServletRequest request, Table table) {
		
		// columna tipo registro
		table.getRow().getColumn(columnas.get(TIPOREGISTRO)).getCellRenderer().setCellEditor(getCellEditorTipoRegistro((TIPOREGISTRO)));
		
		// columna tipo hoja
		table.getRow().getColumn(columnas.get(TIPOHOJA)).getCellRenderer().setCellEditor(getCellEditorTipoDescripcion(TIPOHOJA, TIPOHOJADESC));
		
		// columna situacion
		table.getRow().getColumn(columnas.get(SITUACIONACTA)).getCellRenderer().setCellEditor(getCellEditorTipoDescripcion(SITUACIONACTA,SITUACIONACTADES));
		
		// Para las columnas de fechas:
		table.getRow().getColumn(columnas.get(FECHATASACION)).getCellRenderer().setCellEditor(getCellEditorFecha(FECHATASACION));
		table.getRow().getColumn(columnas.get(FECHAACTA)).getCellRenderer().setCellEditor(getCellEditorFecha(FECHAACTA));
		table.getRow().getColumn(columnas.get(FECHAPAGO)).getCellRenderer().setCellEditor(getCellEditorFecha(FECHAPAGO));
		
	}

	private void configurarColumnas(HtmlTable table) {
		this.configColumna(table, columnas.get(ID), "&nbsp;&nbsp;Acciones", false, false, "3%");		
		this.configColumna(table, columnas.get(TIPOREGISTRO),"Tipo",true,true,"10%");
		this.configColumna(table, columnas.get(NIF),"NIF CIF",true,true,"7%");
		this.configColumna(table, columnas.get(NUMHOJACAMPO),"N&uacutem. HC",true,true,"6%");
		this.configColumnaFecha(table, FECHATASACION, "F. Tasaci&oacuten", true, true, "7%", "dd/MM/yyyy");
		this.configColumna(table, columnas.get(TIPOHOJA),"Tipo Hoja",true,true,"10%");
		this.configColumna(table, columnas.get(SITUACIONHOJADESC),"Situaci&oacuten",true,true,"10%");
		this.configColumna(table, columnas.get(SERIE),"Serie",true,true,"4%");
		this.configColumna(table, columnas.get(NUMACTA),"N&uacutem. AT",true,true,"6%");
		this.configColumnaFecha(table, FECHAACTA, "Fecha", true, true, "6%", "dd/MM/yyyy");
		this.configColumna(table, columnas.get(SITUACIONACTA),"Situaci&oacuten",true,true,"7%");
		this.configColumna(table, columnas.get(IMPORTE),"Importe",true,true,"4%");
		this.configColumna(table, columnas.get(IMPORTEREGULARIZADO),"Importe Regularizado",true,true,"4%");
		this.configColumnaFecha(table, FECHAPAGO, "Fecha Pago", true, true, "6%", "dd/MM/yyyy");
	}
	
	private CellEditor getCellEditorTipoDescripcion(final String TIPO, final String DESCRIPCION) {
		
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
							
				Long _tipo = (Long) new BasicCellEditor().getValue(item,TIPO, rowcount);
				String _descripcion = (String) new BasicCellEditor().getValue(item, DESCRIPCION, rowcount);
				
            	HtmlBuilder html = new HtmlBuilder();
            	if (_tipo!= null && _descripcion != null) {
            		html.append(StringUtils.nullToString(_tipo)+"-"+StringUtils.nullToString(_descripcion));
            	}else {
            		html.append("");
            	}
            	return html.toString();
            }
		};
	}
	
	private CellEditor getCellEditorTipoRegistro(final String tipoRegistro) {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {
				Long tipo = (Long) new BasicCellEditor().getValue(item, tipoRegistro, rowcount);
				String descripcion = null;
				if(tipo.compareTo(new Long(0))==0){
					descripcion="Hoja de campo";
				}else{
					descripcion="Acta de tasación";
				}
				
				
            	HtmlBuilder html = new HtmlBuilder();
            	if (tipo!= null && descripcion != null) {
            		html.append(StringUtils.nullToString(tipo)+"-"+StringUtils.nullToString(descripcion));
            	}else {
            		html.append("");
            	}
            	return html.toString();
            }
		};
	}

	private CellEditor getCellEditorImporte(String importe) {

		return new CellEditor() {

			public Object getValue(Object item, String importe, int rowcount) {

				BigDecimal pctImporte = (BigDecimal) new BasicCellEditor().getValue(item, importe, rowcount);
				String imp = "";
				
				if (pctImporte!=null) {
					imp = pctImporte.setScale(0, BigDecimal.ROUND_DOWN) + " ";
				}
				
				HtmlBuilder html = new HtmlBuilder();
				html.append(imp);
				return html.toString();
			}
		};
	}
	
	
	private CellEditor getCellEditorAcciones() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				HtmlBuilder html = new HtmlBuilder();
				Long tipoReg =(Long)new BasicCellEditor().getValue(item, TIPOREGISTRO, rowcount);
				
				String refPoliza =StringUtils.nullToString((String)new BasicCellEditor().getValue(item, REFERENCIA, rowcount));
				Long codPlan = (Long)new BasicCellEditor().getValue(item, PLAN, rowcount);
				Long numHojaCampo =(Long)new BasicCellEditor().getValue(item, NUMHOJACAMPO, rowcount);
				Long tipoHoja =(Long)new BasicCellEditor().getValue(item, TIPOHOJA, rowcount);
				Long serie =(Long)new BasicCellEditor().getValue(item, SERIE, rowcount);
				Long numActa =(Long)new BasicCellEditor().getValue(item, NUMACTA, rowcount);
				
				if(tipoReg.compareTo(new Long(0))==0){//Hojas de Campo
					html.a().href().quote().append("javascript:pdfHoja('"+refPoliza+"','"+codPlan+"','"+
							numHojaCampo+"','"+tipoHoja+"');").quote().close();	
					html.append("<img src=\"jsp/img/jmesa/pdf.gif\" alt=\"Pdf Hojas de Campo\" title=\"Pdf Hojas de Campo\"/>");
				}else{//Actas de tasaciï¿½n
					html.a().href().quote().append("javascript:pdfActa('"+serie+"','" +numActa+"');").quote().close();	
					html.append("<img src=\"jsp/img/jmesa/pdf.gif\" alt=\"Pdf Actas de Tasacion\" title=\"Pdf Actas de Tasacion\"/>");
				}
											
	            html.aEnd();
	            html.append("&nbsp;");
	              	            
				return html.toString();
			}
		};
	}
	
	public void setSiniestrosInformacionDao(IGenericoDao siniestrosInformacionDao) {
		this.siniestrosInformacionDao = siniestrosInformacionDao;
	}

	public void setSiniestrosInformacionFilter(
			IGenericoFilter siniestrosInformacionFilter) {
		this.siniestrosInformacionFilter = siniestrosInformacionFilter;
	}

	public void setSiniestrosInformacionSort(IGenericoSort siniestrosInformacionSort) {
		this.siniestrosInformacionSort = siniestrosInformacionSort;
	}



}
