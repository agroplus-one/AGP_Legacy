package com.rsi.agp.core.jmesa.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.jmesa.facade.TableFacade;
import org.jmesa.facade.TableFacadeFactory;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;
import org.springframework.dao.DataIntegrityViolationException;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.SobreprecioSbpFilter;
import com.rsi.agp.core.jmesa.service.ISobreprecioSbpService;
import com.rsi.agp.core.jmesa.sort.SobreprecioSbpSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.admin.IProvinciaDao;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.models.sbp.ISobrePrecioDao;
import com.rsi.agp.dao.tables.commons.Provincia;
import com.rsi.agp.dao.tables.cpl.CultivoId;
import com.rsi.agp.dao.tables.sbp.Sobreprecio;

@SuppressWarnings("deprecation")
public class SobreprecioSbpService implements ISobreprecioSbpService {
	
	private ISobrePrecioDao sobrePrecioDao;
	private ILineaDao lineaDao;
	private IProvinciaDao provinciaDao;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_sbp");
	
	private String id;
	// Mapa con las columnas del listado y los campos del filtro de b煤squeda
	private HashMap<String, String> columnas = new HashMap<String, String>();
	private Log logger = LogFactory.getLog(getClass());
	
	// Constantes para los nombres de las columnas del listado
	private final static String ID_STR = "ID";
	private final static String PLAN = "PLAN";
	private final static String LINEA = "LINEA";
	private final static String CODPROVINCIA = "CODPROVINCIA";
	private final static String NOMPROVINCIA = "NOMPROVINCIA";
	private final static String CODCULTIVO = "CODCULTIVO";
	private final static String NOMCULTIVO = "NOMCULTIVO";
	private final static String CODTIPOCAPITAL = "CODTIPOCAPITAL";
	private final static String DESCTIPOCAPITAL = "DESCTIPOCAPITAL";
	private final static String PRECIOMINIMO = "PRECIOMINIMO";
	private final static String PRECIOMAXIMO = "PRECIOMAXIMO";
	
	
	@Override
	public int getConsultaPolizaSbpCountWithFilter(SobreprecioSbpFilter filter) {
		
		return sobrePrecioDao.getConsultaPolizaSbpCountWithFilter(filter);
	}

	@Override
	public Collection<Sobreprecio> getSobreprecioSbpWithFilterAndSort(
			SobreprecioSbpFilter filter, SobreprecioSbpSort sort, int rowStart,
			int rowEnd) throws BusinessException {
		
		return sobrePrecioDao.getSobreprecioSbpWithFilterAndSort(filter, sort, rowStart, rowEnd);
	}
	
	public String getTablaSobreprecios (HttpServletRequest request, HttpServletResponse response, Sobreprecio sobreprecio, String origenLlamada){
		
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response, sobreprecio, origenLlamada);

		// Configura el filtro y la ordenaci贸n, busca los datos y las carga en el TableFacade
		setDataAndLimitVariables(tableFacade);
		
		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());

		return html (tableFacade);
		
	}
	/**
	 * Crea y configura el objeto TableFacade que encapsular谩 la tabla de p贸lizas
	 * @param request
	 * @param response
	 * @return
	 */
	private TableFacade crearTableFacade (HttpServletRequest request, HttpServletResponse response, Sobreprecio sobreprecio,String origenLlamada) {
		
		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id, request);				
    	
		//Carga las columnas a mostrar en el listado en el TableFacade y devuelve un Map con ellas
		HashMap<String,String> columnas = cargarColumnas(tableFacade);
        
        tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.

        // Si no es una llamada a traves de ajax        
    	if (request.getParameter("ajax") == null){
    		if (origenLlamada == null){
	    		if (request.getSession().getAttribute("consultaSobreprecioSbp_LIMIT") != null){
	    			//Si venimos por aqu铆 es que ya hemos pasado por el filtro en algun momento
	    			tableFacade.setLimit((Limit) request.getSession().getAttribute("consultaSobreprecioSbp_LIMIT"));
	    		}
    		}
    		else{
    			// Carga en el TableFacade los filtros de b煤squeda introducidos en el formulario 
    			cargarFiltrosBusqueda(columnas, sobreprecio, tableFacade);
    		}
    	}                
        
        return tableFacade;
	}
	@SuppressWarnings("all")
	private HashMap<String, String> cargarColumnas(TableFacade tableFacade) {
		
		// Crea el Map con las columnas del listado y los campos del filtro de b煤squeda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID_STR, "id");
			columnas.put(PLAN, "linea.codplan");
			columnas.put(LINEA, "linea.codlinea");
			columnas.put(CODPROVINCIA, "provincia.codprovincia");
			columnas.put(NOMPROVINCIA, "provincia.nomprovincia");
			columnas.put(CODCULTIVO, "cultivo.id.codcultivo");
			columnas.put(NOMCULTIVO, "cultivo.descultivo");
			
			columnas.put(CODTIPOCAPITAL, "tipoCapital.codtipocapital");
			columnas.put(DESCTIPOCAPITAL, "tipoCapital.destipocapital");
			
			columnas.put(PRECIOMINIMO, "precioMinimo");
			columnas.put(PRECIOMAXIMO, "precioMaximo");
		}	
		tableFacade.setColumnProperties(columnas.get(ID_STR),columnas.get(PLAN), columnas.get(LINEA),
				columnas.get(CODPROVINCIA),columnas.get(NOMPROVINCIA),columnas.get(CODCULTIVO),
				columnas.get(NOMCULTIVO),columnas.get(CODTIPOCAPITAL),
				columnas.get(DESCTIPOCAPITAL),columnas.get(PRECIOMINIMO),columnas.get(PRECIOMAXIMO)); 
        
        return columnas;
	}
	/**
	 * Carga en el TableFacade los filtros de b煤squeda introducidos en el formulario
	 * @param poliza
	 * @param tableFacade
	 */
	private void cargarFiltrosBusqueda(HashMap<String,String> columnas, Sobreprecio sobreprecio, TableFacade tableFacade) {
		
		// Plan
		if (FiltroUtils.noEstaVacio (sobreprecio.getLinea().getCodplan()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(PLAN), sobreprecio.getLinea().getCodplan().toString()));
		// Linea
		if (FiltroUtils.noEstaVacio (sobreprecio.getLinea().getCodlinea()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(LINEA), sobreprecio.getLinea().getCodlinea().toString()));
		// Cod Provincia
		if (sobreprecio.getProvincia() != null && FiltroUtils.noEstaVacio (sobreprecio.getProvincia().getCodprovincia())) 			
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(CODPROVINCIA), sobreprecio.getProvincia().getCodprovincia().toString()));
		// Nombre provincia
		if (sobreprecio.getProvincia() != null && FiltroUtils.noEstaVacio (sobreprecio.getProvincia().getNomprovincia())) 			
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(NOMPROVINCIA), sobreprecio.getProvincia().getNomprovincia()));
		// cod cultivo
		if (FiltroUtils.noEstaVacio (sobreprecio.getCultivo().getId().getCodcultivo())) 			
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(CODCULTIVO), sobreprecio.getCultivo().getId().getCodcultivo().toString()));
		// Nombre Cultivo
		if (FiltroUtils.noEstaVacio (sobreprecio.getCultivo().getDescultivo())) 			
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(NOMCULTIVO), sobreprecio.getCultivo().getDescultivo()));
		
		// cod tipo capital
		if (FiltroUtils.noEstaVacio (sobreprecio.getTipoCapital().getCodtipocapital())) 			
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(CODTIPOCAPITAL), sobreprecio.getTipoCapital().getCodtipocapital().toString()));
		// Desc tipo capital
		if (FiltroUtils.noEstaVacio (sobreprecio.getTipoCapital().getDestipocapital())) 			
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(DESCTIPOCAPITAL), sobreprecio.getTipoCapital().getDestipocapital()));
				
		// PrecioMinimo
		if (FiltroUtils.noEstaVacio (sobreprecio.getPrecioMinimo())) 			
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(PRECIOMINIMO), sobreprecio.getPrecioMinimo().toString()));
		// PrecioMaximo
		if (FiltroUtils.noEstaVacio (sobreprecio.getPrecioMaximo())) 			
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(PRECIOMAXIMO), sobreprecio.getPrecioMaximo().toString()));
		
	}
	/**
	 * Crea los objetos de filtro y ordenaci贸n, llama al dao para obtener los datos de Sobreprecio y carga el TableFacade con ellas
	 * @param tableFacade
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade){
        
		// Obtiene el Filter para la b煤squeda de sobreprecio
		Limit limit = tableFacade.getLimit();
		SobreprecioSbpFilter consultaFilter = getConsultaSobreprecioFilter(limit);

        // Obtiene el n煤mero de filas que cumplen el filtro        
        int totalRows = getConsultaPolizaSbpCountWithFilter(consultaFilter);
        logger.debug("********** count filas para sobreprecio = "+totalRows+" **********");
        
        // y lo establecemos al tableFacade antes de obtener la fila de inicio y la de fin
        tableFacade.setTotalRows(totalRows);

        // Crea el Sort para la b煤squeda sobreprecioSbp
        SobreprecioSbpSort consultaSort = getConsultaSobreprecioSort(limit);
        int rowStart = limit.getRowSelect().getRowStart();
        int rowEnd = limit.getRowSelect().getRowEnd();
        Collection<Sobreprecio> items = new ArrayList<Sobreprecio>();
		// Obtiene los registros que cumplen el filtro
        try {
			items = getSobreprecioSbpWithFilterAndSort(consultaFilter, consultaSort, rowStart, rowEnd);
			logger.debug("********** list items para Sobreprecio = "+items.size()+" **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
        tableFacade.setItems(items); 
        
    }
	
	/**
     * Metodo para construir el html de la tabla a mostrar
     * @param tableFacade
     * @return
     */
	private String html(TableFacade tableFacade){
    	
    	HtmlTable table = (HtmlTable) tableFacade.getTable();
    	table.getRow().setUniqueProperty("id");
    	
        // Configuraci贸n de las columnas de la tabla    
    	configurarColumnas(table);    
    	
    	Limit limit = tableFacade.getLimit();
        if (limit.isExported()) {
            tableFacade.render(); // Will write the export data out to the response.
            return null; // In Spring return null tells the controller not to do anything.
        }else{
        	// Configuraci贸n de los datos de las columnas que requieren un tratamiento para mostrarse
        	// campo acciones
        	table.getRow().getColumn(columnas.get(SobreprecioSbpService.ID_STR)).getCellRenderer().setCellEditor(getCellEditorAcciones());
        	table.getRow().getColumn(columnas.get(SobreprecioSbpService.CODPROVINCIA)).getCellRenderer().setCellEditor(getCellEditorCodProvincia());
        	table.getRow().getColumn(columnas.get(SobreprecioSbpService.NOMPROVINCIA)).getCellRenderer().setCellEditor(getCellEditorNomProvincia());
        }
        
    	return tableFacade.render();
    }
    
    /**
	 * Devuelve el objeto que muestra la informaci贸n de la columna 'Acciones'
	 * @return
	 */
	private CellEditor getCellEditorAcciones() {
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
				Long id = (Long)new BasicCellEditor().getValue(item, "id", rowcount);
            	BigDecimal codLinea = (BigDecimal)new BasicCellEditor().getValue(item, "linea.codlinea", rowcount);
            	BigDecimal codplan = (BigDecimal)new BasicCellEditor().getValue(item, "linea.codplan", rowcount);
            	String nomLinea = (String)new BasicCellEditor().getValue(item, "linea.nomlinea", rowcount);
            	BigDecimal codProvincia = null;
            	String nomProvincia = null;
				try {
					codProvincia = (BigDecimal) new BasicCellEditor().getValue(
							item, "provincia.codprovincia", rowcount);
					nomProvincia = (String) new BasicCellEditor().getValue(
							item, "provincia.nomprovincia", rowcount);
				} catch (Exception e) {
					logger.debug("provincia nula");
				}
            	BigDecimal codCultivo = (BigDecimal)new BasicCellEditor().getValue(item, "cultivo.id.codcultivo", rowcount);
            	String nomCultivo = (String)new BasicCellEditor().getValue(item, "cultivo.descultivo", rowcount);

            	BigDecimal codTipoCapital = (BigDecimal)new BasicCellEditor().getValue(item, "tipoCapital.codtipocapital", rowcount);
            	String descTipoCapital = (String)new BasicCellEditor().getValue(item, "tipoCapital.destipocapital", rowcount);
            	            	
            	BigDecimal precioMinimo = (BigDecimal)new BasicCellEditor().getValue(item, "precioMinimo", rowcount);
            	BigDecimal precioMaximo = (BigDecimal)new BasicCellEditor().getValue(item, "precioMaximo", rowcount);
            	
            	HtmlBuilder html = new HtmlBuilder();
            	// bot贸n editar
            	html.a().href().quote().append("javascript:editar('"+id+"','"+codLinea+"','"+codplan+"','"+nomLinea+"'," +
            			"'"+StringUtils.nullToString(codProvincia)+"'," + "'"+StringUtils.nullToString(nomProvincia)+"'," + 
            			"'"+codCultivo+"'," + "'"+nomCultivo+"'" + "," + "'"+precioMinimo+ "','" + precioMaximo +"'," + 
            			"'"+codTipoCapital+"'," + "'"+descTipoCapital+"'" + ");").quote().close();
            	html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar p&oacute;liza Sbp\" title=\"Editar p&oacute;liza Sbp\"/>");
                html.aEnd();
                html.append("&nbsp;");
            			
                // bot贸n borrar 
            	html.a().href().quote().append("javascript:borrar('"+id+"');").quote().close();
                html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar l&iacute;nea Sbp\" title=\"Borrar l&iacute;nea Sbp\"/>");
                html.aEnd();
                html.append("&nbsp;");
	                	
            	return html.toString();
            }
		};
	}
	
	/**
	 * Devuelve el objeto que muestra la columna 'C贸digo de provincia'
	 * @return
	 */
	private CellEditor getCellEditorCodProvincia() {
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
            	BigDecimal codProvincia = null;
				try {
					codProvincia = (BigDecimal) new BasicCellEditor().getValue(
							item, "provincia.codprovincia", rowcount);
				} catch (Exception e) {
					logger.debug("codprovincia nulo");
				}
            	
            	HtmlBuilder html = new HtmlBuilder();
            	// bot贸n editar
            	html.append(StringUtils.nullToString(codProvincia) + "&nbsp;");
            			
            	return html.toString();
            }
		};
	}
	
	/**
	 * Devuelve el objeto que muestra la columna 'Nombre de provincia'
	 * @return
	 */
	private CellEditor getCellEditorNomProvincia() {
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
            	String nomProvincia = null;
				try {
					nomProvincia = (String) new BasicCellEditor().getValue(
							item, "provincia.nomprovincia", rowcount);
				} catch (Exception e) {
					logger.debug("nomprovincia nulo");
				}
            	
            	HtmlBuilder html = new HtmlBuilder();
            	// bot贸n editar
            	html.append(StringUtils.nullToString(nomProvincia) + "&nbsp;");
            			
            	return html.toString();
            }
		};
	}
	
    /**
	 * Configuraci贸n de las columnas de la tabla
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table) {
		
		// Acciones
    	configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false, false, "2%");

    	// 1 - Plan
    	configColumna(table, columnas.get(PLAN), "Plan", true, true, "5%"); //2
    	// 2- L铆nea
    	configColumna(table, columnas.get(LINEA), "L&iacute;nea", true, true, "5%"); //2
    	// 3 - Cod. Provincia
    	configColumna(table, columnas.get(CODPROVINCIA), "Cod. Prov.", true, true, "8%"); //7
    	// 4 - Nom. Provincia
    	configColumna(table, columnas.get(NOMPROVINCIA), "Provincia", true, true, "10%"); //18
    	// 5 - cod cultivo 
    	configColumna(table, columnas.get(CODCULTIVO), "Cod. Cultivo", true, true, "8%");
    	// 6 - Nom cultivo
    	configColumna(table, columnas.get(NOMCULTIVO), "Cultivo", true, true, "16%"); //18
    	// 7 - cod tipo Capital 
    	configColumna(table, columnas.get(CODTIPOCAPITAL), "Cod. T.C", true, true, "7%");
    	// 8 - Desc tipo Capital
    	configColumna(table, columnas.get(DESCTIPOCAPITAL), "Tipo Capital", true, true, "17%"); //17
    	// 9 - precioMinimo
    	configColumna(table, columnas.get(PRECIOMINIMO), "Precio M&iacute;nimo", true, true, "9%");
    	// 10 - precioMaximo
    	configColumna(table, columnas.get(PRECIOMAXIMO), "Precio M&aacute;ximo", true, true, "13%");   	
	}
	/**
	 * Configura la columna de la tabla identificada por 'idCol' con los valores pasados como par谩metro y los incluye en la tabla
	 * @param table Objeto que contiene la tabla
	 * @param idCol Id de la columna
	 * @param title T铆tulo de la columna
	 * @param filterable Indica si se podr谩 buscar por esa columna
	 * @param sortable Indica si se podr谩 ordenar por esa columna
	 * @param width Ancho de la columna
	 */
	private void configColumna (HtmlTable table, String idCol, String title, boolean filterable, boolean sortable, String width) {
	   table.getRow().getColumn(idCol).setTitle(title);
       table.getRow().getColumn(idCol).setFilterable(filterable);
       table.getRow().getColumn(idCol).setSortable(sortable);
       table.getRow().getColumn(idCol).setWidth(width);
	}
    
	/**
	 * Crea y configura el Filter para la consulta de sobreprecio
	 * @param limit
	 * @return
	 */
	private SobreprecioSbpFilter getConsultaSobreprecioFilter(Limit limit) {
		SobreprecioSbpFilter consultaFilter = new SobreprecioSbpFilter();
        FilterSet filterSet = limit.getFilterSet();
        Collection<Filter> filters = filterSet.getFilters();
        for (Filter filter : filters) {
            String property = filter.getProperty();
            String value = filter.getValue();
            consultaFilter.addFilter(property, value);
        }
        return consultaFilter;
	}
	
	/**
	 * Crea y configura el Sort para la consulta de p贸lizas
	 * @param limit
	 * @return
	 */
	private SobreprecioSbpSort getConsultaSobreprecioSort(Limit limit) {
		
		SobreprecioSbpSort consultaSort = new SobreprecioSbpSort();
		SortSet sortSet = limit.getSortSet();
        Collection<Sort> sorts = sortSet.getSorts();
        for (Sort sort : sorts) {
            String property = sort.getProperty();
            String order = sort.getOrder().toParam();
            consultaSort.addSort(property, order);
        }

        return consultaSort;
	}
	
	public Sobreprecio getSobreprecio(Long idSobreprecio)
	throws BusinessException {
		try {
			return (Sobreprecio) sobrePrecioDao.get(Sobreprecio.class, idSobreprecio);
		
		} catch (Exception dao) {
			logger.error("Se ha producido error al obtener el sobreprecio: "
						+ dao.getMessage());
		throw new BusinessException(
				"Se ha producido al obtener el sobreprecio:",
					dao);
		}
	}
	
	public void bajaSobreprecio(
			Sobreprecio sobreprecio) throws BusinessException {
		try {
			sobrePrecioDao.delete(Sobreprecio.class, sobreprecio.getId());
			logger.debug("sobreprecio borrado  = " + sobreprecio.getId());
		} catch (Exception ex) {
			throw new BusinessException(
					"Error al borrar la fechaContratacionSbp de Sobreprecio",
					ex);
		}
	}
	
	public Map<String, Object> editaSobreprecio(Sobreprecio sobreprecio) throws BusinessException {
		
		Map<String, Object> parameters = new HashMap<String, Object>();	
		
		try {
				Long lineaSeguroId = lineaDao.getLineaSeguroId(sobreprecio.getLinea().getCodlinea(), sobreprecio.getLinea()
						.getCodplan());
				
				BigDecimal codCultivo = sobreprecio.getCultivo().getId().getCodcultivo();
				
				BigDecimal codTipoCapital = sobreprecio.getTipoCapital().getCodtipocapital();
				
				// if (sobrePrecioDao.existeLineaSeguroId(lineaSeguroId, codCultivo) || !sobrePrecioDao.existeCultivo(lineaSeguroId, codCultivo)) {
				if (StringUtils.nullToString(lineaSeguroId).equals("")) {
					// mensaje de error, l铆nea no existe
					parameters.put("alerta", bundle.getObject(ConstantsSbp.ERROR_SOBREPRECIO_PLAN_LINEA_KO));	
	
				} else if (!StringUtils.nullToString(sobreprecio.getProvincia().getCodprovincia()).equals("") && 
						!provinciaDao.checkProvinciaExists(sobreprecio.getProvincia().getCodprovincia())){
					// mensaje de error, Provincia no existe
					parameters.put("alerta", bundle.getObject(ConstantsSbp.ERROR_SOBREPRECIO_PROVINCIA_KO));					
		
				} else if (!sobrePrecioDao.existeCultivo(lineaSeguroId, codCultivo)){
					// mensaje de error, Cultivo no existe
					parameters.put("alerta", bundle.getObject(ConstantsSbp.ERROR_SOBREPRECIO_CULTIVO_KO));					
		
				} else if (!sobrePrecioDao.existeTipoCapital(codTipoCapital)){
					// mensaje de error, TipoCapital no existe
					parameters.put("alerta", bundle.getObject(ConstantsSbp.ERROR_SOBREPRECIO_TIPO_CAPITAL_KO));					
		
				} else {							
							CultivoId cultivoId = new CultivoId(lineaSeguroId, codCultivo);
			 		
							sobreprecio.getLinea().setLineaseguroid(lineaSeguroId);
							sobreprecio.getCultivo().setId(cultivoId);
							
							sobrePrecioDao.actualizaSobreprecio(sobreprecio);
							logger.debug("sobreprecio modificado con  id = " + sobreprecio.getId());
							parameters.put("mensaje", bundle.getObject(ConstantsSbp.MSJ_EDITA_SOBREPRECIO_OK));
				}
					
		} catch (ConstraintViolationException ex) {
			logger.debug("Error en la ediccion del sobreprecio", ex);
			parameters.put("alerta", bundle.getObject(ConstantsSbp.MSJ_SOBREPRECIO_DUPLICADO_KO));
		} catch (DataIntegrityViolationException ex) {
			logger.debug("Error al editar el sobreprecio", ex);
			parameters.put("alerta", bundle.getObject(ConstantsSbp.MSJ_SOBREPRECIO_DUPLICADO_KO));
		} catch (Exception ex) {
			logger.debug("Error al dar de alta el sobreprecio", ex);
			parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_EDITA_SOBREPRECIO_KO));
		}
		

		return parameters;
	}

	
	public Map<String, Object> altaSobreprecio(Sobreprecio sobreprecio) throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();	
		
		try {
				// obtener Linea de Seguro asociada
				Long lineaSeguroId = lineaDao.getLineaSeguroId(sobreprecio
						.getLinea().getCodlinea(), sobreprecio.getLinea()
						.getCodplan());
				
				// obtener Cultivo asociado
				BigDecimal codCultivo = sobreprecio.getCultivo().getId().getCodcultivo();
				
				BigDecimal codTipoCapital = sobreprecio.getTipoCapital().getCodtipocapital();
				
				// comprobar si Linea de Seguro asociada ya existe
				if (StringUtils.nullToString(lineaSeguroId).equals("")) {
					// mensaje de error, l铆nea ya existe
					parameters.put("alerta", bundle.getObject(ConstantsSbp.ERROR_SOBREPRECIO_PLAN_LINEA_KO));	
				
				} else if (!StringUtils.nullToString(sobreprecio.getProvincia().getCodprovincia()).equals("") &&
						!provinciaDao.checkProvinciaExists(sobreprecio.getProvincia().getCodprovincia())){
					// mensaje de error, Provincia no existe
					parameters.put("alerta", bundle.getObject(ConstantsSbp.ERROR_SOBREPRECIO_PROVINCIA_KO));					
		
				} else if (!sobrePrecioDao.existeCultivo(lineaSeguroId, codCultivo)){
					// mensaje de error, Cultivo no existe
					parameters.put("alerta", bundle.getObject(ConstantsSbp.ERROR_SOBREPRECIO_CULTIVO_KO));					
				
				} else if (!sobrePrecioDao.existeTipoCapital(codTipoCapital)){
					// mensaje de error, TipoCapital no existe
					parameters.put("alerta", bundle.getObject(ConstantsSbp.ERROR_SOBREPRECIO_TIPO_CAPITAL_KO));					
		
				} else {
					if (sobreprecio.getId() != null)
						sobreprecio.setId(null);
					// Linea de Seguro no existe, podemos dar de Alta Sobreprecio
			 		CultivoId cultivoId = new CultivoId(lineaSeguroId, codCultivo);
			 		
					sobreprecio.getLinea().setLineaseguroid(lineaSeguroId);
					sobreprecio.getCultivo().setId(cultivoId);
					
					if (StringUtils.nullToString(sobreprecio.getProvincia().getCodprovincia()).equals("")){
						sobreprecio.setProvincia(null);
					}
					
					sobrePrecioDao.saveOrUpdate(sobreprecio);
					sobrePrecioDao.evict(sobreprecio);
					
					if (sobreprecio.getProvincia() == null){
						sobreprecio.setProvincia(new Provincia());
					}
					
					parameters.put("mensaje", bundle.getObject(ConstantsSbp.MSJ_ALTA_SOBREPRECIO_OK));
			}
		} catch (DataIntegrityViolationException ex) {
			logger.debug("Error al dar de alta el sobreprecio", ex);
			if (sobreprecio.getProvincia() == null){
				sobreprecio.setProvincia(new Provincia());
			}
			parameters.put("alerta", bundle.getObject(ConstantsSbp.MSJ_SOBREPRECIO_DUPLICADO_KO));
		} catch (Exception ex) {
			logger.debug("Error al dar de alta el sobreprecio", ex);
			parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_ALTA_SOBREPRECIO_KO));
		}
		
		return parameters;
	}
	
	@Override
	public Map<String, Object> replicar(BigDecimal planOrig, BigDecimal lineaOrig, BigDecimal planDest, BigDecimal lineaDest) throws BusinessException {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		Long lineaSeguroIdOrigen = null;
		Long lineaSeguroIdDestino = null;
		
		// Obtiene el lineaseguroid de los plan/linea origen y destino
		try {
			// Validaci贸n del plan/linea origen
			lineaSeguroIdOrigen = lineaDao.getLineaSeguroId(lineaOrig, planOrig);
			if (lineaSeguroIdOrigen == null) {
				logger.debug("El plan/l韓ea origen no existe, no se contin鷄 con la r閜lica");
				parameters.put("alerta", bundle.getString("mensaje.replicarSobreprecio.planlinea.origen.KO"));
				return parameters;
			}
			
			// Validaci贸n del plan/linea destino
			lineaSeguroIdDestino = lineaDao.getLineaSeguroId(lineaDest, planDest);
			if (lineaSeguroIdDestino == null) {
				logger.debug("El plan/l韓ea destino no existe, no se contin鷄 con la r閜lica");
				parameters.put("alerta", bundle.getString("mensaje.replicarSobreprecio.planlinea.destino.KO"));
				return parameters;
			}
			
			// Valida que el plan/linea destino no tenga Sbp dados de alta previamente
			SobreprecioSbpFilter filter = new SobreprecioSbpFilter ();
			filter.addFilter("linea.codplan", planDest);
			filter.addFilter("linea.codlinea", lineaDest);
			
			if (getConsultaPolizaSbpCountWithFilter(filter) != 0) {
				logger.debug("El plan/l韓ea destino tiene Sbp dados de alta, no se contin鷄 con la r閜lica");
				parameters.put("alerta", bundle.getString("mensaje.replicarSobreprecio.planlinea.KO"));
				return parameters;
			}
			
			// Llamada al metodo del DAO que realiza la replica 
			String numregCopiados = sobrePrecioDao.replicar(new BigDecimal(lineaSeguroIdOrigen), new BigDecimal(lineaSeguroIdDestino));
			logger.debug("El proceso de r閜lica ha finalizado correctamente. Numero de registros Copiados = " + numregCopiados);
			if (numregCopiados.equals("0")){
				parameters.put("mensaje", bundle.getString("mensaje.replicarSobreprecio.sin.registros.copiados.OK"));
				return parameters;
			}
		} 
		catch (DAOException e) {
			logger.error("Ocurrio un error al replicar los Sbp ", e);
			parameters.put("alerta", bundle.getString("mensaje.replicarSobreprecio.KO"));
			return parameters;
		}
		
		// Comprobamos si se han copiado todos los registros
		if(sobrePrecioDao.numRegDestinoIgualNumRegOrigen (lineaSeguroIdDestino,lineaSeguroIdOrigen)){
			parameters.put("mensaje", bundle.getString("mensaje.replicarSobreprecio.OK"));
		}else{
			parameters.put("mensaje", bundle.getString("mensaje.replicarSobreprecio.registros.pendientes.OK"));
		}
		
		return parameters;
	}
		
	public void setSobrePrecioDao(ISobrePrecioDao sobrePrecioDao) {
		this.sobrePrecioDao = sobrePrecioDao;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}

	public void setProvinciaDao(IProvinciaDao provinciaDao) {
		this.provinciaDao = provinciaDao;
	}

}
