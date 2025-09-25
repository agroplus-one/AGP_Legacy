package com.rsi.agp.core.jmesa.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmesa.core.filter.MatcherKey;
import org.jmesa.facade.TableFacade;
import org.jmesa.facade.TableFacadeFactory;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.editor.DateCellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.service.IPolizaActualizadaService;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.core.jmesa.sort.PolizaActualizadaSort;

@SuppressWarnings("deprecation")
public class PolizaActualizadaService implements IPolizaActualizadaService{

	private IPolizaDao polizaDao;
	private String id;
	private Log logger = LogFactory.getLog(getClass());
	
	// Constantes para los nombres de las columnas del listado
	private final String ID_STR = "ID";
	private final String ENTIDAD = "ENTIDAD";
	private final String OFICINA = "OFICINA";
	private final String USUARIO = "USUARIO";
	private final String PLAN = "PLAN";
	private final String LINEA = "LINEA";
	private final String COLECTIVO = "COLECTIVO";
	private final String DCCOLECTIVO = "DCCOLECTIVO";
	private final String REFERENCIA = "REFERENCIA";
	private final String IMPORTE = "IMPORTE";
	private final String DCREFERENCIA = "DCREFERENCIA";
	private final String NIF = "NIF";
	private final String NOMBRE = "NOMBRE";
	private final String CLASE = "CLASE";
	private final String MODULO = "MODULO";
	private final String ESTADO = "ESTADO";
	private final String FECHA = "FECHA";
	
	// &nbsp;
	private final String NBSP = "&nbsp;";
	
	// Mapa con las columnas del listado y los campos del filtro de búsqueda
	private HashMap<String, String> columnas = new HashMap<String, String>();

	
	/**
	 * Busca las pólizas para sobreprecio que se ajusten al filtro y genera la tabla para presentarlas 
	 * @param request
	 * @param response
	 * @param poliza Objeto que encapsula el filtro de la búsqueda
	 * @return Código de la tabla de presentación de las pólizas
	 */
	public String getTablaPolizas(HttpServletRequest request, HttpServletResponse response, Poliza poliza, String origenLlamada) {
						
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response, origenLlamada);
		
		// Configura el filtro y la ordenación, busca las pólizas y las carga en el TableFacade
		if ("doSituacionAct".equals(origenLlamada)) {
			BigDecimal codEntidad =new BigDecimal(request.getParameter("codEntidad"));
			BigDecimal codPlan =new BigDecimal(request.getParameter("linea.codplan"));
			BigDecimal codLinea=new BigDecimal(request.getParameter("linea.codlinea"));
			String nifAsegurado=request.getParameter("asegurado.nifcif");
			
			this.setDataAndLimitVariables(tableFacade, nifAsegurado, codPlan, codLinea, codEntidad);
			
		}else {
			setDataAndLimitVariables(tableFacade,poliza);
		}

		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());

		// Genera el html de la tabla y lo devuelve	
		return html (tableFacade);
		
	}
	
	/**
	 * Crea y configura el objeto TableFacade que encapsulará la tabla de pólizas
	 * @param request
	 * @param response
	 * @return
	 */
	private TableFacade crearTableFacade (HttpServletRequest request, HttpServletResponse response, String origenLlamada) {
		
		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id, request);				
    	
		//Carga las columnas a mostrar en el listado en el TableFacade y devuelve un Map con ellas
		HashMap<String,String> columnas = cargarColumnas(tableFacade);
        
        //tableFacade.setExportTypes(response, ExportType.CSV, ExportType.EXCEL, ExportType.PDF);
        tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.

        //Defino los tipos para los filtros. Habrá que redefinir en el filter la forma
        //de filtrar los campos que tienen un tratamiento especial (distinto de 'like %valor%')
        tableFacade.addFilterMatcher(new MatcherKey(Date.class), new DateFromFilterMatcher("d/M/yyyy"));
        
        // Si no es una llamada a través de ajax        
    	if (request.getParameter("ajax") == null){
    		if (origenLlamada == null){
	    		if (request.getSession().getAttribute("polizas_LIMIT") != null){
	    			//Si venimos por aquí es que ya hemos pasado por el filtro en algun momento
	    			tableFacade.setLimit((Limit) request.getSession().getAttribute("polizas_LIMIT"));
	    		}
    		}
    		Poliza pol = new Poliza();
    		if ("true".equals(request.getParameter("recogerPolSesion"))){
    			pol = (Poliza) request.getSession().getAttribute("polBusqueda");
	    		//String idPoliza = StringUtils.nullToString(request.getParameter("idPoliza"));
				if (pol != null){
					cargarFiltrosBusqueda(columnas, pol, tableFacade);
				}
    		}else{
    			// -- FILTROS POR DEFECTO --
    			
    			// Carga en el TableFacade los filtros de búsqueda introducidos en el formulario 
    			//cargarFiltrosBusqueda(columnas, poliza, tableFacade);
    			//guardo filtro en sesion
				//request.getSession().setAttribute("polBusqueda", poliza);
    			
    			 // -- ORDENACIÓN POR DEFECTO --> ORDER ASC -> Entidad,oficina,póliza y módulo
	    		/*Sort sortPlan = new Sort(4, "linea.codplan", Order.DESC);
	    		Sort sortEstado = new Sort(12, "estadoPoliza.idestado", Order.DESC);
	    		Sort sortEntidad = new Sort(1, "colectivo.tomador.id.codentidad", Order.ASC);
	    		Sort sortOficina = new Sort(2, "oficina", Order.ASC);
	    		Sort sortReferencia = new Sort(7, "referencia", Order.ASC);
	    		Sort sortModulo = new Sort(9, "codmodulo", Order.ASC);
	    		tableFacade.getLimit().getSortSet().addSort(sortPlan);
	    		tableFacade.getLimit().getSortSet().addSort(sortEstado);
	    		tableFacade.getLimit().getSortSet().addSort(sortEntidad);
	    		tableFacade.getLimit().getSortSet().addSort(sortOficina);
	    		tableFacade.getLimit().getSortSet().addSort(sortReferencia);
	    		tableFacade.getLimit().getSortSet().addSort(sortModulo);*/
    		}
    	}   
    	// guardamos el filtro
    	guardarFiltro(request,tableFacade);
        return tableFacade;
		
	}
	
	/**
	 * Método para guardar el filtro en sesión
	 * @param request
	 * @param tableFacade
	 */
	private void guardarFiltro(HttpServletRequest request, TableFacade tableFacade){
		Poliza poliza = new Poliza();
    	FilterSet filter = tableFacade.getLimit().getFilterSet();
    	Collection<Filter> colFil = filter.getFilters();
		Iterator<Filter> it = (colFil.iterator());
		while(it.hasNext()) {
			Filter fil = it.next();
    		// Entidad
    		if (fil.getProperty().equals("colectivo.tomador.id.codentidad")){	        				        				        				        			
    			poliza.getColectivo().getTomador().getId().setCodentidad(new BigDecimal(fil.getValue().toString()));
    		}
    		// Oficina
    		if (fil.getProperty().equals("oficina")){
    			poliza.setOficina(fil.getValue().toString());
    		}
    		// Usuario
    		if (fil.getProperty().equals("usuario.codusuario")){
    			poliza.getUsuario().setCodusuario(fil.getValue().toString());
    		}
    		// Plan
    		if (fil.getProperty().equals("linea.codplan")){
    			poliza.getLinea().setCodplan(new BigDecimal(fil.getValue().toString()));
    		}
    		// Linea
    		if (fil.getProperty().equals("linea.codlinea")){
    			poliza.getLinea().setCodlinea(new BigDecimal(fil.getValue().toString()));
    		}
    		// Clase
    		if (fil.getProperty().equals("clase")){
    			poliza.setClase(new BigDecimal(fil.getValue().toString()));
    		}
    		// Referencia de póliza
    		if (fil.getProperty().equals("referencia")){
    			poliza.setReferencia(fil.getValue().toString());
    		}
    		// Referencia colectivo
    		if (fil.getProperty().equals("colectivo.idcolectivo")){	    
    			poliza.getColectivo().setIdcolectivo(fil.getValue().toString());
    		}
    		// Módulo
    		if (fil.getProperty().equals("codmodulo")){	        			
    			poliza.setCodmodulo(fil.getValue().toString());
    		}
    		// CIF/NIF asegurado
    		if (fil.getProperty().equals("asegurado.nifcif")){
    			poliza.getAsegurado().setNifcif(fil.getValue().toString());
    		}
    		// Nombre asegurado
    		if (fil.getProperty().equals("asegurado.nombre")){
    			poliza.getAsegurado().setNombreCompleto(fil.getValue().toString());
    		}
    		// Estado de la póliza
    		if (fil.getProperty().equals("estadoPoliza.idestado")){	        			
    			poliza.getEstadoPoliza().setIdestado(new BigDecimal(fil.getValue().toString()));
    		}	
    		// Fecha de envío
    		if (fil.getProperty().equals("fechaenvio")){	  
    			poliza.setFechaenvio(new Date(fil.getValue().toString()));
    		}	
		}
		//guardo filtro en sesion
		request.getSession().setAttribute("polBusqueda", poliza);
	}
	
	/**
	 * Carga en el TableFacade los filtros de búsqueda introducidos en el formulario
	 * @param poliza
	 * @param tableFacade
	 */
	private void cargarFiltrosBusqueda(HashMap<String,String> columnas, Poliza poliza, TableFacade tableFacade) {
		// Entidad
		if (FiltroUtils.noEstaVacio (poliza.getColectivo().getTomador().getId().getCodentidad()))     				
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(ENTIDAD), poliza.getColectivo().getTomador().getId().getCodentidad().toString()));
		// Oficina
		if (FiltroUtils.noEstaVacio (poliza.getOficina()))     				
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(OFICINA), poliza.getOficina()));	
		// Usuario
		if (FiltroUtils.noEstaVacio (poliza.getUsuario().getCodusuario()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(USUARIO), poliza.getUsuario().getCodusuario()));
		// Plan
		if (FiltroUtils.noEstaVacio (poliza.getLinea().getCodplan()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(PLAN), poliza.getLinea().getCodplan().toString()));
		// Linea
		if (FiltroUtils.noEstaVacio (poliza.getLinea().getCodlinea()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(LINEA), poliza.getLinea().getCodlinea().toString()));
		// Clase
		if (FiltroUtils.noEstaVacio (poliza.getClase()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(CLASE), poliza.getClase().toString()));
		// Referencia
		if (FiltroUtils.noEstaVacio (poliza.getReferencia()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(REFERENCIA), poliza.getReferencia()));
		// Referencia de colectivo
		if (FiltroUtils.noEstaVacio (poliza.getColectivo().getIdcolectivo()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(COLECTIVO), poliza.getColectivo().getIdcolectivo()));
		// DC de colectivo
		if (FiltroUtils.noEstaVacio (poliza.getColectivo().getDc()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(DCCOLECTIVO), poliza.getColectivo().getDc()));
		// Módulo
		if (FiltroUtils.noEstaVacio (poliza.getCodmodulo()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(MODULO), poliza.getCodmodulo()));
		// Fecha de envío
		if (FiltroUtils.noEstaVacio (poliza.getFechaenvio())) 			
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(FECHA), new SimpleDateFormat("dd/MM/yyyy").format(poliza.getFechaenvio())));
		// CIF/NIF
		if (FiltroUtils.noEstaVacio (poliza.getAsegurado().getNifcif())) 			
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(NIF), poliza.getAsegurado().getNifcif()));
		// Nombre asegurado
		if (FiltroUtils.noEstaVacioSinEspacios (poliza.getAsegurado().getNombreCompleto())) 			
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(NOMBRE), poliza.getAsegurado().getNombreCompleto()));
		// Estado
		if (FiltroUtils.noEstaVacio (poliza.getEstadoPoliza().getIdestado())) 			
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(ESTADO), poliza.getEstadoPoliza().getIdestado().toString()));
	}

	/**
	 * Carga las columnas a mostrar en el listado en el TableFacade y devuelve un Map con ellas
	 * @param tableFacade
	 * @return
	 */
	@SuppressWarnings("all")
	private HashMap<String, String> cargarColumnas(TableFacade tableFacade) {
		
		// Crea el Map con las columnas del listado y los campos del filtro de búsqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID_STR, "id");
			columnas.put(ENTIDAD, "colectivo.tomador.id.codentidad");
			columnas.put(OFICINA, "oficina");
			columnas.put(USUARIO, "usuario.codusuario");
			columnas.put(PLAN, "linea.codplan");
			columnas.put(LINEA, "linea.codlinea");
			columnas.put(COLECTIVO, "colectivo.idcolectivo");			
			columnas.put(REFERENCIA, "referencia");
			columnas.put(IMPORTE, "importe");
			columnas.put(MODULO, "codmodulo");
			columnas.put(NIF, "asegurado.nifcif");
			columnas.put(NOMBRE, "asegurado.nombre");
			columnas.put(ESTADO, "estadoPoliza.idestado");		
			columnas.put(FECHA, "fechaenvio");
			columnas.put(CLASE, "clase");
			columnas.put(DCREFERENCIA, "dc");
			columnas.put(DCCOLECTIVO, "colectivo.dc");
		}
				
    	// Configura el TableFacade con las columnas que se quieren mostrar
        tableFacade.setColumnProperties(columnas.get(ID_STR), columnas.get(ENTIDAD), columnas.get(OFICINA), columnas.get(USUARIO),
        								columnas.get(PLAN), columnas.get(LINEA), columnas.get(COLECTIVO), columnas.get(REFERENCIA),
        								columnas.get(IMPORTE), columnas.get(MODULO), columnas.get(NIF), columnas.get(NOMBRE),
        								columnas.get(ESTADO),	columnas.get(FECHA),columnas.get(CLASE));
        // Devuelve el mapa
        return columnas;
	}
	
	/**
	 * Crea los objetos de filtro y ordenación, llama al dao para obtener los datos de las pólizas y carga el TableFacade con ellas
	 * @param tableFacade
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade, Poliza poliza){
		
		Poliza polizaAux = null;
		
		try {
			polizaAux = polizaDao.getPolizaById(poliza.getIdpoliza());
		} catch (DAOException e1) {
			logger.debug("setDataAndLimitVariables error. " + e1.getMessage());
		}
		
		// Obtiene el Filter para la búsqueda de pólizas
		Limit limit = tableFacade.getLimit();
		
        // Obtiene el número de filas que cumplen el filtro        
        int totalRows = polizaDao.getPolizasParaActualizarCount(polizaAux);
        logger.debug("********** count filas = "+totalRows+" **********");
        // y lo establecemos al tableFacade antes de obtener la fila de inicio y la de fin
        tableFacade.setTotalRows(totalRows);
        
        PolizaActualizadaSort polizaSort=getPolizasActualizadasSort(limit);

        int rowStart = limit.getRowSelect().getRowStart();
        int rowEnd = limit.getRowSelect().getRowEnd();
        Collection<Poliza> items = new ArrayList<Poliza>();
		// Obtiene los registros que cumplen el filtro
        try {
			items = polizaDao.getPolizasParaActualizar(polizaAux, rowStart, rowEnd, polizaSort);
			logger.debug("********** list items = "+items.size()+" **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
        tableFacade.setItems(items); 
    }
	
	
	private void setDataAndLimitVariables(TableFacade tableFacade, String nifAsegurado, BigDecimal codPlan, BigDecimal codLinea, BigDecimal codEntidad){
		
		// Obtiene el Filter para la búsqueda de pólizas
		Limit limit = tableFacade.getLimit();
		
        // Obtiene el número de filas que cumplen el filtro        
        int totalRows = polizaDao.getPolizasDefinitivasCount(codPlan, codLinea, nifAsegurado, codEntidad);
        logger.debug("********** count filas = "+totalRows+" **********");
        // y lo establecemos al tableFacade antes de obtener la fila de inicio y la de fin
        tableFacade.setTotalRows(totalRows);

        PolizaActualizadaSort polizaSort=getPolizasActualizadasSort(limit);
        
        int rowStart = limit.getRowSelect().getRowStart();
        int rowEnd = limit.getRowSelect().getRowEnd();
        Collection<Poliza> items = new ArrayList<Poliza>();
		// Obtiene los registros que cumplen el filtro
        try {
			items = polizaDao.getPolizasDefinitivas(codPlan,codLinea, nifAsegurado, codEntidad,polizaSort,rowStart, rowEnd);
			logger.debug("********** list items = "+items.size()+" **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
        tableFacade.setItems(items); 
    }
	
	private PolizaActualizadaSort getPolizasActualizadasSort(Limit limit) {
		PolizaActualizadaSort consultaSort = new PolizaActualizadaSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			consultaSort.addSort(property, order);
		}

		return consultaSort;
	}
	
	/**
     * Método para construir el html de la tabla a mostrar
     * @param tableFacade
     * @return
     */
	private String html(TableFacade tableFacade){
    	
    	HtmlTable table = (HtmlTable) tableFacade.getTable();
    	table.getRow().setUniqueProperty("id");
    	
        // Configuración de las columnas de la tabla    
    	configurarColumnas(table);    
    	
    	Limit limit = tableFacade.getLimit();
        if (limit.isExported()) {
            tableFacade.render(); // Will write the export data out to the response.
            return null; // In Spring return null tells the controller not to do anything.
        } else {
        	// Configuración de los datos de las columnas que requieren un tratamiento para mostrarse
        	// campo acciones
        	table.getRow().getColumn(columnas.get(ID_STR)).getCellRenderer().setCellEditor(getCellEditorAcciones());
        	// Campo colectivo - dc
        	table.getRow().getColumn(columnas.get(COLECTIVO)).getCellRenderer().setCellEditor(getCellEditorColectivo());
        	// Referencia
        	table.getRow().getColumn(columnas.get(REFERENCIA)).getCellRenderer().setCellEditor(getCellEditorReferencia());        	
        	// Campo estado
        	table.getRow().getColumn(columnas.get(ESTADO)).getCellRenderer().setCellEditor(getCellEditorEstado());
        	// Fecha de envío
        	table.getRow().getColumn(columnas.get(FECHA)).getCellRenderer().setCellEditor(getCellEditorFechaEnvio());
        	// Nombre Asegurado
        	table.getRow().getColumn(columnas.get(NOMBRE)).getCellRenderer().setCellEditor(getCellEditorNombreAsegurado());
        }
    	
    	// Devuelve el html de la tabla
    	return tableFacade.render();
    }
    
    /**
	 * Devuelve el objeto que muestra la información de la columna 'Acciones'
	 * @return
	 */
	private CellEditor getCellEditorAcciones() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
            	Long idPolizaLong = (Long) new BasicCellEditor().getValue(item, "idpoliza", rowcount);
            	Long idAseg = (Long) new BasicCellEditor().getValue(item, "asegurado.id", rowcount);
            	Long idCol = (Long) new BasicCellEditor().getValue(item, "colectivo.id", rowcount);
            	BigDecimal clase = (BigDecimal) new BasicCellEditor().getValue(item, "clase", rowcount);
            	String idPoliza = idPolizaLong.toString();
            	Long lineaseguroid = (Long)new BasicCellEditor().getValue(item, "linea.lineaseguroid", rowcount);
            	
            	String tipoRef = StringUtils.nullToString((Character)new BasicCellEditor().getValue(item, "tipoReferencia", rowcount));
            	if (tipoRef.equals("C")){
            		Poliza pol = new Poliza();
            		pol.getLinea().setLineaseguroid(lineaseguroid);
            		pol.getAsegurado().setId(idAseg);
            		pol.getColectivo().setId(idCol);
            		pol.setClase(clase);
            	}
            	HtmlBuilder html = new HtmlBuilder();
            	// botón radio
            	html.append("&nbsp;&nbsp;&nbsp;");
                html.append("<input type='radio' id='idRadios' name='idRadios' value='"+idPoliza+"'/>");
                html.aEnd();
                html.append("&nbsp;");
            	
                return html.toString();
            }
		};
	}
    
	/**
	 * Devuelve el objeto que muestra la información de la columna 'Colectivo'
	 * @return
	 */
	private CellEditor getCellEditorColectivo() {
		return new CellEditor() {
		    public Object getValue(Object item, String property, int rowcount) {
		    	String nifCif, dc;
		    	nifCif = (String) new BasicCellEditor().getValue(item, columnas.get(COLECTIVO), rowcount);
		    	dc = (String) new BasicCellEditor().getValue(item, columnas.get(DCCOLECTIVO), rowcount);
		    	String value = nifCif + "-" + dc;
		        HtmlBuilder html = new HtmlBuilder();
		        html.append(value);
		        return html.toString();
		    }
		};
	}
	
	/**
	 * Devuelve el objeto que muestra la información de la columna 'Estado'
	 * @return
	 */
	private CellEditor getCellEditorEstado() {
		return new CellEditor() {
		    public Object getValue(Object item, String property, int rowcount) {
		    	// Obtiene el código de estado de la póliza actual
		    	int estado = 0;
				try {
					estado = ((BigDecimal)new BasicCellEditor().getValue(item, columnas.get(ESTADO), rowcount)).intValue();
				} catch (Exception e) {
					logger.error("PolizaActualizadaService - Ocurrió un error al obtener el estado de la póliza" , e);
				}
		    	// Muestra el mensaje correspondiente al estado
		    	String value = "";		    
		    	switch (estado) {
					case 1: value = "Pendiente Validaci&oacute;n"; break;										
					case 2: value = "Grabaci&oacute;n Provisional"; break;
					case 3: value = "Grabaci&oacute;n Definitiva"; break;
					case 4: value = "Anulada"; break;
					case 5: value = "Enviada Pendiente de Confirmar"; break;															
					case 7: value = "Enviada Err&oacute;nea"; break;
					case 8: value = "Enviada Correcta"; break;
					default: break;
				}
		    			    	
		        HtmlBuilder html = new HtmlBuilder();
		        html.append(value);
		        return html.toString();
		    }
		};
	}
	
	/**
	 * Devuelve el objeto que muestra la información de la columna 'Fecha de envío'
	 * @return
	 */
	private CellEditor getCellEditorFechaEnvio() {
		return new CellEditor() {
		    public Object getValue(Object item, String property, int rowcount) {
		    	// Muestra la fecha de envío
		    	String value = "";		    		    	

		    	// Obtiene el código de estado de la póliza actual		    	
				try {
					// Si la tiene fecha de envío se formatea y se muestra, si no la tiene nos se muestra nada
					Date dateAux = (Date)new BasicCellEditor().getValue(item, columnas.get(FECHA), rowcount);
					value = (dateAux == null) ? ("") : new SimpleDateFormat("dd/MM/yyyy").format(dateAux);
				} catch (Exception e) {
					logger.error("PolizaActualizadaService - Ocurrió un error al obtener la fecha de envío de la póliza" , e);
				}
		    			    	
		        HtmlBuilder html = new HtmlBuilder();
		        html.append (FiltroUtils.noEstaVacioSinEspacios(value) ? value : NBSP);
		        return html.toString();
		    }
		};
	}
	
	/**
	 * Devuelve el objeto que muestra la información de la columna 'Póliza'
	 * @return
	 */
	private CellEditor getCellEditorReferencia() {
		return new CellEditor() {
		    public Object getValue(Object item, String property, int rowcount) {

		    	// Muestra la referencia de la póliza y su dígito de control
		    	String value = "";
		    	String referencia = (String) new BasicCellEditor().getValue(item, columnas.get(REFERENCIA), rowcount);		    		    	
		    	Object o = new BasicCellEditor().getValue(item, columnas.get(DCREFERENCIA), rowcount);
		    	String dc = (o == null) ? "" : "-" + ((BigDecimal)o).toString();		
		    	value = referencia == null ? "" : referencia + dc;
		    			    	
		        HtmlBuilder html = new HtmlBuilder();
		        html.append (FiltroUtils.noEstaVacioSinEspacios(value) ? value : NBSP);
		        return html.toString();
		    }
		};
	}
	
	/**
	 * Devuelve el objeto que muestra la información de la columna 'Asegurado'
	 * @return
	 */
	private CellEditor getCellEditorNombreAsegurado() {
		return new CellEditor() {
		    public Object getValue(Object item, String property, int rowcount) {

		    	// Muestra la referencia de la póliza y su dígito de control
		    	String value = "";
		    	
		    	String asegTipo = (String) new BasicCellEditor().getValue(item, "asegurado.tipoidentificacion", rowcount);
		    	if (asegTipo.equals("CIF")){
		    		value = (String) new BasicCellEditor().getValue(item, "asegurado.razonsocial", rowcount);
		    	}else{
			    	String asegNombre = (String) new BasicCellEditor().getValue(item, "asegurado.nombre", rowcount);
			    	String asegApe1 = (String) new BasicCellEditor().getValue(item, "asegurado.apellido1", rowcount);
			    	String asegApe2 = (String) new BasicCellEditor().getValue(item, "asegurado.apellido2", rowcount);
			    	value = asegNombre + " " + asegApe1 + " " + asegApe2;
		    	}
		    	//String referencia = (String) new BasicCellEditor().getValue(item, columnas.get(NOMBRE), rowcount);		    		    	
		        HtmlBuilder html = new HtmlBuilder();
		        html.append (FiltroUtils.noEstaVacioSinEspacios(value) ? value : NBSP);
		        return html.toString();
		    }
		};
	}
	

	/**
	 * Configuración de las columnas de la tabla
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table) {
		// Acciones
    	configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false, false, "6%");
    	// 1 - Entidad
    	configColumna(table, columnas.get(ENTIDAD), "Ent", true, true, "4%");
    	// 2 - Oficina
    	configColumna(table, columnas.get(OFICINA), "Ofi", true, true, "4%");
    	// 3 - Usuario
    	configColumna(table, columnas.get(USUARIO), "Usu", true, true, "6%");
    	// 4 - Plan
    	configColumna(table, columnas.get(PLAN), "Plan", true, true, "4%");
    	// 5 - Línea
    	configColumna(table, columnas.get(LINEA), "L&iacute;nea", true, true, "3%");
    	// 6 - Colectivo
    	configColumna(table, columnas.get(COLECTIVO), "Colectivo", true, true, "8%");
    	// 7 - Póliza
    	configColumna(table, columnas.get(REFERENCIA), "P&oacute;liza", true, true, "8%");
    	// 8 - Importe
    	configColumna(table, columnas.get(IMPORTE), "importe", true, true, "3%");
    	// 9 - Modulo
    	configColumna(table, columnas.get(MODULO), "M&oacute;d", true, true, "3%");
    	// 10 - NIF/CIF
    	configColumna(table, columnas.get(NIF), "NIF/CIF", true, true, "8%");
    	// 11 - Asegurado
    	configColumna(table, columnas.get(NOMBRE), "Asegurado", true, true, "20%");    	
    	// 12 - Estado
    	configColumna(table, columnas.get(ESTADO), "Estado", true, true, "10%");
    	// 13 - Fecha de envío
    	configColumnaFecha(table, columnas.get(FECHA), "Fec.Env", true, true, "8%", "dd/MM/yyyy");
    	// 14 - Clase
    	configColumna(table, columnas.get(CLASE), "Clase", true, true, "10%");
	}

	/**
	 * Configura la columna de la tabla identificada por 'idCol' con los valores pasados como parámetro y los incluye en la tabla
	 * @param table Objeto que contiene la tabla
	 * @param idCol Id de la columna
	 * @param title Título de la columna
	 * @param filterable Indica si se podrá buscar por esa columna
	 * @param sortable Indica si se podrá ordenar por esa columna
	 * @param width Ancho de la columna
	 */
	private void configColumna (HtmlTable table, String idCol, String title, boolean filterable, boolean sortable, String width) {
		table.getRow().getColumn(idCol).setTitle(title);
        table.getRow().getColumn(idCol).setFilterable(filterable);
        table.getRow().getColumn(idCol).setSortable(sortable);
        table.getRow().getColumn(idCol).setWidth(width);
	}
	
	/**
	 * Configura la columna de la tabla identificada por 'idCol' con los valores pasados como parámetro y los incluye en la tabla
	 * @param table Objeto que contiene la tabla
	 * @param idCol Id de la columna
	 * @param title Título de la columna
	 * @param filterable Indica si se podrá buscar por esa columna
	 * @param sortable Indica si se podrá ordenar por esa columna
	 * @param width Ancho de la columna
	 * @param fFecha Formato de fecha con la que se mostrarán los datos de esta columna
	 */
	private void configColumnaFecha (HtmlTable table, String idCol, String title, boolean filterable, boolean sortable, String width, String fFecha) {
		// Configura los valores generales de la columna
		configColumna(table, idCol, title, filterable, sortable, width);
		// Añade el formato de la fecha a la columna
		try {
			table.getRow().getColumn(idCol).getCellRenderer().setCellEditor(new DateCellEditor("dd/MM/yyyy"));
		} catch (Exception e) {
			logger.error("Ocurrió un error al configurar el formato de fecha de la columna " + idCol, e);
		}
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}
	
	/**
	 * Obtiene un objeto poliza de la BD
	 * @param idPoliza: PK de la poliza en la BD
	 */
	public final Poliza getPolizaById(final Long idPoliza) {
		return (Poliza) polizaDao.getObject(Poliza.class, idPoliza);
	}
}
