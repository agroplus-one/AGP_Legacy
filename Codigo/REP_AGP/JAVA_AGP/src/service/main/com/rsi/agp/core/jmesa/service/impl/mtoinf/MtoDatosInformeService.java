package com.rsi.agp.core.jmesa.service.impl.mtoinf;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

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
import org.jmesa.limit.Order;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;
import org.springframework.dao.DataIntegrityViolationException;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.DatosInformeFilter;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoDatosInformeService;
import com.rsi.agp.core.jmesa.sort.DatosInformeSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.webapp.util.InformeUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.mtoinf.IMtoCamposCalculadosDao;
import com.rsi.agp.dao.models.mtoinf.IMtoCamposPermitidosDao;
import com.rsi.agp.dao.models.mtoinf.IMtoDatosInformeDao;
import com.rsi.agp.dao.models.mtoinf.IMtoInformeDao;
import com.rsi.agp.dao.tables.mtoinf.CampoInforme;
import com.rsi.agp.dao.tables.mtoinf.CamposCalculados;
import com.rsi.agp.dao.tables.mtoinf.CamposPermitidos;
import com.rsi.agp.dao.tables.mtoinf.ClasificacionRupturaCamposCalculados;
import com.rsi.agp.dao.tables.mtoinf.ClasificacionRupturaCamposPermitidos;
import com.rsi.agp.dao.tables.mtoinf.CondicionCamposCalculados;
import com.rsi.agp.dao.tables.mtoinf.CondicionCamposPermitidos;
import com.rsi.agp.dao.tables.mtoinf.DatoInformes;
import com.rsi.agp.dao.tables.mtoinf.Informe;
import com.rsi.agp.dao.tables.mtoinf.VistaMtoinfDatosInformes;

@SuppressWarnings("deprecation")
public class MtoDatosInformeService implements IMtoDatosInformeService {

	private IMtoInformeDao mtoInformeDao;
	private IMtoDatosInformeDao mtoDatosInformeDao;	
	private IMtoCamposCalculadosDao mtoCamposCalculadosDao; 
	private IMtoCamposPermitidosDao mtoCamposPermitidosDao;
	private int numItems;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_inf");

	


	public IMtoDatosInformeDao getMtoDatosInformeDao() {
		return mtoDatosInformeDao;
	}

	public void setMtoDatosInformeDao(IMtoDatosInformeDao mtoDatosInformeDao) {
		this.mtoDatosInformeDao = mtoDatosInformeDao;
	}

	public IMtoCamposCalculadosDao getMtoCamposCalculadosDao() {
		return mtoCamposCalculadosDao;
	}

	public void setMtoCamposCalculadosDao(
			IMtoCamposCalculadosDao mtoCamposCalculadosDao) {
		this.mtoCamposCalculadosDao = mtoCamposCalculadosDao;
	}

	public IMtoCamposPermitidosDao getMtoCamposPermitidosDao() {
		return mtoCamposPermitidosDao;
	}

	public void setMtoCamposPermitidosDao(
			IMtoCamposPermitidosDao mtoCamposPermitidosDao) {
		this.mtoCamposPermitidosDao = mtoCamposPermitidosDao;
	}

	
	/**
	 * Setter del DAO para Spring
	 * @param mtoDatosInformeDao
	 */
	
	private String id = "consultaDatosInforme";
	private Log logger = LogFactory.getLog(getClass());
	
	// Constantes para los nombres de las columnas del listado
	private final String ID_STR = "ID";
	private final String ORDEN = "ORDEN";
	private final String ABREVIADO = "ABREVIADO";
	private final String FORMATO = "FORMATO";
	private final String DECIMALES = "DECIMALES";
	private final String TOTALIZA = "TOTALIZA";
	private final String TOTAL_POR_GRUPO = "TOTAL_POR_GRUPO";
	private final String TIPO = "TIPO";
	private final String ORIGEN_DATOS = "ORIGEN_DATOS";
		
	// Mapa con las columnas del listado y los campos del filtro de búsqueda
	HashMap<String, String> columnas = new HashMap<String, String>();
	

	
	@Override
	public Collection<VistaMtoinfDatosInformes> getDatosInformeWithFilterAndSort(
			DatosInformeFilter filter,DatosInformeSort sort,BigDecimal informeId,  int rowStart,
			int rowEnd) throws BusinessException {
		logger.debug("init - getDatosInformeWithFilterAndSort en MtoDatosInformeService");
		Collection<VistaMtoinfDatosInformes> colectDatoInformes = null;
		try{
			
			colectDatoInformes = mtoDatosInformeDao.getDatosInformeWithFilterAndSort(filter, sort,informeId, rowStart, rowEnd);
		
		}catch(Exception e){
			logger.error("getDatosInformeWithFilterAndSort error : " + e);
			throw new BusinessException("Error a recuperar la lista de datos informes", e);

		}
		logger.debug("end - getDatosInformeWithFilterAndSort en MtoDatosInformeService");
		return colectDatoInformes;
	}

	
	public List<CampoInforme> getListaCampos(BigDecimal informeId) throws BusinessException		{
		
		logger.debug("init - getListaCampos en MtoDatosInformeService");
		List<CampoInforme> ListaCampos = null;
		try {
		
			ListaCampos = mtoDatosInformeDao.getListaCampos(informeId);
		
		} catch (Exception ex) {
			
			logger.error("Error a recuperar la lista de operadores", ex);
			throw new BusinessException("Error a recuperar la lista de campo de datos informe", ex);

		}
		logger.debug("end - getListaCampos en MtoDatosInformeService");
		
		return ListaCampos;	
	}
	
	
	@SuppressWarnings("unchecked")
	public List<CampoInforme> getListCamposInforme() throws BusinessException {
		
		
		logger.debug("init - getListCamposInforme en MtoDatosInformeService");
		CampoInforme campoInforme = null;
		List<CampoInforme> listaCampoInforme = null;
		try {
			
			List<CamposCalculados> listaCamposCalculados  = mtoCamposCalculadosDao.getListaCamposCalculados();
			List<CamposPermitidos> listaCamposPermitidos = mtoCamposPermitidosDao.getListaCamposPermitidos();
			listaCampoInforme = new ArrayList<CampoInforme>();
		
			for(CamposCalculados camposCalculados:listaCamposCalculados){
				campoInforme = new CampoInforme(); 
				campoInforme.setId(camposCalculados.getId());
				campoInforme.setNombre(camposCalculados.getNombre());
				campoInforme.setNombreVista("Campos Calculados");
				campoInforme.setTipo(new BigDecimal(ConstantsInf.CAMPO_TIPO_NUMERICO));
				campoInforme.setDescTipo(ConstantsInf.CAMPO_TIPO_NUMERICO_STR);
				campoInforme.setPermitidoOCalculado(new BigDecimal(1));
				listaCampoInforme.add(campoInforme);
			}
			
			for(CamposPermitidos camposPermitidos:listaCamposPermitidos){
				if(null!=camposPermitidos.getVistaCampo() && 
						camposPermitidos.getVistaCampo().getVisible().compareTo(new BigDecimal(1))==0){
					campoInforme = new CampoInforme(); 
					campoInforme.setId(camposPermitidos.getId());
					campoInforme.setNombre(camposPermitidos.getVistaCampo().getNombre());
					campoInforme.setNombreVista(camposPermitidos.getVistaCampo().getVista().getNombre());
					campoInforme.setPermitidoOCalculado(new BigDecimal(2));
					campoInforme.setTipo(camposPermitidos.getVistaCampo().getVistaCampoTipo().getIdtipo());
					campoInforme.setDescTipo(camposPermitidos.getVistaCampo().getVistaCampoTipo().getNombreTipo()); 
					listaCampoInforme.add(campoInforme);
				}
			}
			
			Collections.sort(listaCampoInforme);   
		
			logger.debug("end - getListCamposInforme en MtoDatosInformeService");
		}
		
	 catch (Exception e) {
		logger.debug("getListCamposInforme error. " + e);
		throw new BusinessException(
				"Error a recuperar la lista de campos de informe", e);

	}
		
		return listaCampoInforme;
	}
	
	/**
	 * Realiza el alta del dato del informe pasado como parámetro
	 * @param informeId
	 * @param datosInformeBean
	 */
	public Map<String, Object> altaCampoInforme(VistaMtoinfDatosInformes vistaMtoinfDatosInformes) throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();	
	
			
			
			try{
				logger.debug("init - [MtoDatosInformeService] altaCampoInforme");
				
				// Si ya existe el dato del informe no se continúa con el alta y se muestra la alerta
				if(mtoDatosInformeDao.existeDatosInforme(vistaMtoinfDatosInformes))
					parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_DATOSINFORME_EXISTE_KO));
				// Si no existe, se continúa con el alta
				else {
					
					DatoInformes datosInforme = new DatoInformes();
					Informe informe = (Informe)mtoInformeDao.getObject(Informe.class,vistaMtoinfDatosInformes.getIdinforme().longValue());
					datosInforme.setInforme(informe);
					
					// Campo calculado
					if(vistaMtoinfDatosInformes.getId().getPermitidocalculado().intValue() == 1 ){
						CamposCalculados campoCalculado= 	(CamposCalculados)mtoCamposCalculadosDao.getObject(CamposCalculados.class,new Long(vistaMtoinfDatosInformes.getIdcampo().longValue()));
						datosInforme.setCamposCalculados(campoCalculado);
						datosInforme.setCamposPermitidos(null);	
					}
					// Campo permitido
					else if(vistaMtoinfDatosInformes.getId().getPermitidocalculado().intValue() == 2 ){
						CamposPermitidos campoPermitido = (CamposPermitidos)mtoCamposPermitidosDao.getObject(CamposPermitidos.class,new Long(vistaMtoinfDatosInformes.getIdcampo().longValue()));	
						datosInforme.setCamposPermitidos(campoPermitido);
						datosInforme.setCamposCalculados(null);	
					}
					
					// Datos comunes
					datosInforme.setOrden(new BigDecimal(mtoDatosInformeDao.getOrden(vistaMtoinfDatosInformes)));
					datosInforme.setAbreviado(vistaMtoinfDatosInformes.getAbreviado());
					datosInforme.setFormato(vistaMtoinfDatosInformes.getFormato());
					datosInforme.setDecimales(vistaMtoinfDatosInformes.getDecimales());
					datosInforme.setTotaliza(vistaMtoinfDatosInformes.getTotaliza());
					datosInforme.setTotalPorGrupo(vistaMtoinfDatosInformes.getTotal_por_grupo());
					
					datosInforme = (DatoInformes)mtoDatosInformeDao.saveOrUpdate(datosInforme);
					vistaMtoinfDatosInformes.getId().setId(new BigDecimal(datosInforme.getId()));
					
				parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_DATOSINFORME_ALTA_OK));
				}
		
				logger.debug("end - [MtoDatosInformeService] altaCampoInforme");
			
			}
		
			 catch (Exception e) {
				 parameters.put("alerta", bundle
							.getObject(ConstantsInf.ALERTA_DATOSINFORME_ALTA_KO));
					logger.debug("getListCamposInforme error. " + e);
				

				}
			
		
			return parameters;
	}
	
	/**
	 * Setter de propiedad
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * devuelve la tabla html del listado de jmesa
	 * @param request
	 * @param response
	 * @param  VistaMtoinfDatosInformes vistaMtoinfDatosInformes
	 * @param String origenLlamada
	 * @return String
	 * */
	public String getTablaDatosInforme (HttpServletRequest request, HttpServletResponse response,VistaMtoinfDatosInformes  vistaMtoinfDatosInformes,  String origenLlamada) {
	
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response, vistaMtoinfDatosInformes ,origenLlamada);
		
		// Configura el filtro y la ordenación, busca las datos de informes y las carga en el TableFacade
		setDataAndLimitVariables(tableFacade,request,vistaMtoinfDatosInformes,origenLlamada);
		if (request.getSession().getAttribute("pageSession") != null && origenLlamada != null && origenLlamada.equals("borrar")){
	
			int pageSession = (Integer)(request.getSession().getAttribute("pageSession"));
			tableFacade.getLimit().getRowSelect().setPage(pageSession);
	
		}
		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());

		request.getSession().setAttribute("pageSession", tableFacade.getLimit().getRowSelect().getPage());
        request.getSession().setAttribute("rowStart", tableFacade.getLimit().getRowSelect().getRowStart());
        request.getSession().setAttribute("rowEnd", tableFacade.getLimit().getRowSelect().getRowEnd());
	

		// Genera el html de la tabla y lo devuelve	
		//return html (tableFacade, lineas);
		return html (tableFacade);
	}
	
	

	
	
	
	/**
	 * Crea y configura el objeto TableFacade que encapsulará la tabla de datos de informes
	 * @param request
	 * @param response
	 * @return
	 */	
	public TableFacade crearTableFacade (HttpServletRequest request, HttpServletResponse response,VistaMtoinfDatosInformes vistaMtoinfDatosInformes, String origenLlamada) {
		
	
		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id, request);				
        tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.
        
        //Carga las columnas a mostrar en el listado en el TableFacade
      	cargarColumnas(tableFacade);
         
        // Si no es una llamada a través de ajax        
    	if (request.getParameter("ajax") == null){
    		if (origenLlamada == null){
    			
	    		if (request.getSession().getAttribute("consultaDatosInforme_LIMIT") != null){
	    			//Si venimos por aquí es que ya hemos pasado por el filtro en algun momento
	    			tableFacade.setLimit((Limit) request.getSession().getAttribute("consultaDatosInforme_LIMIT"));
	    		}
    		
    		}
    		if("menuGeneral".equals(origenLlamada)|| "clasificacionRuptura".equals(origenLlamada) || "condiciones".equals(origenLlamada) || "informe".equals(origenLlamada) || "alta".equals(origenLlamada)|| "modificar".equals(origenLlamada) || "borrar".equals(origenLlamada)){
    			Sort sortOrdent = new Sort(1,"orden",Order.ASC);
				tableFacade.getLimit().getSortSet().addSort(sortOrdent);
				if (vistaMtoinfDatosInformes.getIdcampo() !=null && !"alta".equals(origenLlamada) && !"modificar".equals(origenLlamada)){
		    		Filter filterIdCampo = new Filter("idcampo", vistaMtoinfDatosInformes.getIdcampo().toString());
		    		tableFacade.getLimit().getFilterSet().addFilter(filterIdCampo);
		    	}
			}
    	}
        return tableFacade;
		
	}
	
    private String html(TableFacade tableFacade){	
    	HtmlTable table = (HtmlTable) tableFacade.getTable();
    	table.getRow().setUniqueProperty("id");
    	
    	
    	
    	table.getRow().getColumn(columnas.get(ORDEN)).getCellRenderer().setCellEditor(getCellEditorOrden());
    	table.getRow().getColumn(columnas.get(ABREVIADO)).getCellRenderer().setCellEditor(getCellEditorAbreviado());
    	table.getRow().getColumn(columnas.get(FORMATO)).getCellRenderer().setCellEditor(getCellEditorFormato());
    	table.getRow().getColumn(columnas.get(DECIMALES)).getCellRenderer().setCellEditor(getCellEditorDecimales());
    	table.getRow().getColumn(columnas.get(TOTALIZA)).getCellRenderer().setCellEditor(getCellEditorTotaliza());
    	table.getRow().getColumn(columnas.get(TOTAL_POR_GRUPO)).getCellRenderer().setCellEditor(getCellEditorTotalPorGrupo());
    	
        // Configuración de las columnas de la tabla    
    	configurarColumnas(table);    
    	
    	Limit limit = tableFacade.getLimit();
    	
    	
        if (limit.isExported()) {
            tableFacade.render(); // Will write the export data out to the response.
            return null; // In Spring return null tells the controller not to do anything.
        } else {
        	// Configuración de los datos de las columnas que requieren un tratamiento para mostrarse
        	// campo acciones
        	table.getRow().getColumn(columnas.get(ID_STR)).getCellRenderer().setCellEditor(getCellEditorAcciones(numItems));
        }
           	
    	// Devuelve el html de la tabla
    	return tableFacade.render();
    }
    
    
    /**
	 * Crea los objetos de filtro y ordenación, llama al dao para obtener los datos de las datos de informes y carga el TableFacade con ellas
	 * @param tableFacade
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade,HttpServletRequest request,VistaMtoinfDatosInformes  vistaMtoinfDatosInformes,String origenLlamada){
        
		// Obtiene el Filter para la búsqueda de datos de informes
		Limit limit = tableFacade.getLimit();
		DatosInformeFilter consultaFilter = getConsultaDatosInformeFilter(limit);

        // Obtiene el número de filas que cumplen el filtro        
        int totalRows = getDatosInformeCountWithFilter(consultaFilter,vistaMtoinfDatosInformes.getIdinforme());
        logger.debug("********** count filas para Dato de informe = "+totalRows+" **********");
        // y lo establecemos al tableFacade antes de obtener la fila de inicio y la de fin
        tableFacade.setTotalRows(totalRows);
        
        DatosInformeSort consultaSort = getConsultaDatoInformesSort(limit);
 	   int rowStart = 0;
       int rowEnd = 0; 
       if (origenLlamada != null && origenLlamada.equals("borrar")){
    	   rowStart = (Integer)request.getSession().getAttribute("rowStart");
       	   rowEnd = (Integer)request.getSession().getAttribute("rowEnd");
       }else{
    	   rowStart = limit.getRowSelect().getRowStart();
       	   rowEnd = limit.getRowSelect().getRowEnd();
       }
        Collection<VistaMtoinfDatosInformes> items = new ArrayList<VistaMtoinfDatosInformes>();
		
        try {
        	// Esta variable se usa para pintar en el listado las flechas de subir/bajar del último dato del informe y debe contener el 
        	// número total de datos que tiene el informe. Si se ha incluido algún filtro de búsqueda en el formulario hay que lanzar la 
        	// consulta para obtener este total, pero no se ha buscado por nada se puede utilizar el valor de totalRows        	
        	numItems = (consultaFilter.tieneAlgunFiltro()) ? getDatosInformeCountWithFilter(null,vistaMtoinfDatosInformes.getIdinforme())
        												   : totalRows;
			
        	items = getDatosInformeWithFilterAndSort(consultaFilter, consultaSort,vistaMtoinfDatosInformes.getIdinforme(), rowStart, rowEnd);
			logger.debug("********** list items  = "+items.size()+" **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e);
		}
		
		// Carga los registros obtenidos del bd en la tabla
        tableFacade.setItems(items);
     
    }
	
	

    
	/**
	 * Configuración de las columnas de la tabla
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table) {
		
		// 1 - Id
    	configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false, false, "15%");
    	// 2 - Orden
    	configColumna(table, columnas.get(ORDEN), "Orden", true, true, "10%");
    	// 3 - Abreviado	
    	configColumna(table, columnas.get(ABREVIADO), "Abreviado", true, true, "15%");
    	// 4 - Origen Datos	
    	configColumna(table, columnas.get(ORIGEN_DATOS), "Origen Datos", true, true, "20%");
    	// 5 - Tipo	
    	configColumna(table, columnas.get(TIPO), "Tipo", true, true, "10%");
    	// 6 - Formato
    	configColumna(table, columnas.get(FORMATO), "Formato", true, true, "10%");
    	// 7 - Decimales
    	configColumna(table, columnas.get(DECIMALES), "Decimales", true, true, "10%");
    	// 8 - Totaliza
    	configColumna(table, columnas.get(TOTALIZA), "Totaliza", true, true, "10%");
    	// 9 - Total por grupo
    	configColumna(table, columnas.get(TOTAL_POR_GRUPO), "Total por grupo", true, true, "10%");
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
	 * Devuelve el objeto que muestra la información de la columna 'Acciones'
	 * @return
	 */
	private CellEditor getCellEditorAcciones(final int numItems) {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				
				BigDecimal id = (BigDecimal)new BasicCellEditor().getValue(item, "id.id", rowcount);
				BigDecimal orden = (BigDecimal)new BasicCellEditor().getValue(item, "orden", rowcount);
				BigDecimal informeId = (BigDecimal) new BasicCellEditor().getValue(item, "idinforme", rowcount);
				BigDecimal formato = (BigDecimal) new BasicCellEditor().getValue(item, "formato", rowcount);
				BigDecimal campoPermitidOCalculado = (BigDecimal) new BasicCellEditor().getValue(item, "id.permitidocalculado", rowcount);
				BigDecimal campoId	= (BigDecimal)new BasicCellEditor().getValue(item, "idcampo", rowcount);
				BigDecimal idTipo = (BigDecimal)new BasicCellEditor().getValue(item, "idtipo", rowcount);
				String abreviado = (String)new BasicCellEditor().getValue(item, "abreviado", rowcount);
				String nombre = (String)new BasicCellEditor().getValue(item, "nombre", rowcount);
				BigDecimal decimales = (BigDecimal)new BasicCellEditor().getValue(item, "decimales", rowcount);
				BigDecimal totaliza = (BigDecimal)new BasicCellEditor().getValue(item, "totaliza", rowcount);
				BigDecimal total_por_grupo = (BigDecimal)new BasicCellEditor().getValue(item, "total_por_grupo", rowcount);
				
				HtmlBuilder html = new HtmlBuilder();
            	if(informeId != null){
            		// botón editar un dato del informe
				    html.a().href().quote().append("javascript:editar('"+id+"','"+informeId+"','"+campoId+"','"+formato+"','"+campoPermitidOCalculado+"','"+
				    			idTipo + "','" + abreviado + "','" + nombre + "','" + decimales + "','" + totaliza + "','"  + total_por_grupo + "');").quote().close();
                    html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar un dato de informe\" title=\"Editar un dato de informe\"/>");
                    html.aEnd();
                    html.append("&nbsp;");

                    // botón borrar un dato del informe
		            html.a().href().quote().append("javascript:borrar('"+id+"','"+informeId+"');").quote().close();
		            html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar un dato de informe\" title=\"Borrar un dato de informe\"/>");
		            html.aEnd();
		            html.append("&nbsp;");
		            
		            // botón visualizar un dato del informe 
		            html.a().href().quote().append("javascript:visualizar('"+id+"','"+informeId+"','"+campoId+"','"+formato+"','"+campoPermitidOCalculado+"','"+
		            		idTipo + "','" + abreviado + "','" + nombre + "','" + decimales + "','" + totaliza + "','"  + total_por_grupo + "');").quote().close();
                    html.append("<img src=\"jsp/img/magnifier.png\" alt=\"Visualizar un dato de informe\" title=\"Visualizar un dato de informe\"/>");
                    html.aEnd();
                    html.append("&nbsp;");    

		            if(orden.intValue() != 1){
			            html.a().href().quote().append("javascript:subir('"+id+"','"+informeId+"');").quote().close();
			            html.append("<img src=\"jsp/img/meca_subir.gif\" alt=\"Subir un dato del informe\" title=\"Subir un dato del informe\"/>");
			            html.aEnd();
			            html.append("&nbsp;");
		            }
	             
		            if(orden.intValue() != numItems){
			            html.a().href().quote().append("javascript:bajar('"+id+"','"+informeId+"');").quote().close();
			            html.append("<img src=\"jsp/img/meca_bajar.gif\" alt=\"Bajar un dato del informe \" title=\"Bajar un dato del informe \"/>");
			            html.aEnd();
			            html.append("&nbsp;");
		            }
            	}
                return html.toString();
            }
		};
	}
	
	
    

	/**
	 * Crea y configura el Filter para la consulta de los datos del informe
	 * @param limit
	 * @return
	 */
	private DatosInformeFilter getConsultaDatosInformeFilter(Limit limit) {
		DatosInformeFilter consultaFilter = new DatosInformeFilter();
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
	 * Carga las columnas a mostrar en el listado en el TableFacade y devuelve un Map con ellas
	 * @param tableFacade
	 * @return
	 */
	@SuppressWarnings("all")
	private HashMap<String, String> cargarColumnas(TableFacade tableFacade) {
		
		// Crea el Map con las columnas del listado y los campos del filtro
		if (columnas.size() == 0) {
			columnas.put(ID_STR, "id");
			columnas.put(ORDEN, "orden");
			columnas.put(ABREVIADO, "abreviado");	
			columnas.put(ORIGEN_DATOS, "origen_datos");	
			columnas.put(TIPO, "tipo");
			columnas.put(FORMATO, "formato");
			columnas.put(DECIMALES, "decimales");
			columnas.put(TOTALIZA, "totaliza");
			columnas.put(TOTAL_POR_GRUPO, "total_por_grupo");
		}
				
		tableFacade.setColumnProperties(columnas.get(ID_STR), columnas.get(ORDEN),columnas.get(ABREVIADO),columnas.get(ORIGEN_DATOS),columnas.get(TIPO),
				columnas.get(FORMATO), columnas.get(DECIMALES),columnas.get(TOTALIZA), columnas.get(TOTAL_POR_GRUPO)); 
        
        // Devuelve el mapa
        return columnas;
	}
	
	

	
		 
	
	@Override
	public int getDatosInformeCountWithFilter(DatosInformeFilter filter, final BigDecimal informeId) {
		int count = 0;
		try{
			
			count = mtoDatosInformeDao.getDatosInformeCountWithFilter(filter,informeId);		
		}catch(DAOException e){
			logger.debug("getDatosInformeWithFilterAndSort error. " + e);
		}
		return count;
		
	}
	
	/**
	 * Crea y configura el Sort para la consulta de los datos del informe
	 * @param limit
	 * @return
	 */
	private DatosInformeSort getConsultaDatoInformesSort(Limit limit) {
		DatosInformeSort consultaSort = new DatosInformeSort();
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
	 * Realiza la baja del dato del informe pasado como parámetro
	 * @param datoInformes
	 */
	
	
	public  Map<String, Object>  bajaDatoInformesyActualizar(VistaMtoinfDatosInformes vistaMtoinfDatosInformes) throws BusinessException
		{
	
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			
			DatoInformes datInf = (DatoInformes)mtoDatosInformeDao.getObject(DatoInformes.class,vistaMtoinfDatosInformes.getId().getId().longValue());
			// verificar que no tiene asociadas condiciones ni clasif. ruptura el dato informe para ese informe.
			//Long idInforme = datInf.getInforme().getId();
			boolean res = checkCondicionesYClasifDatoInforme(datInf);
			if (!res){
				mtoDatosInformeDao.delete(datInf);
				List<DatoInformes> listaDatoInforme = mtoDatosInformeDao.getListaDatoInformes(vistaMtoinfDatosInformes.getIdinforme().longValue());
				
				for(DatoInformes datoI :listaDatoInforme ){
					if(datInf.getOrden().intValue() < datoI.getOrden().intValue()){
					
						datoI.setOrden(new BigDecimal(datoI.getOrden().intValue()- 1));
						mtoDatosInformeDao.saveOrUpdate(datoI);;
				
					}
				
				parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_DATOSINFORME_BAJA_OK));	
				
				}
				logger.debug("bajaDatoInformesyActualizar");
			}else{
				logger.debug("El dato informe no se puede borrar ya q existe una condicion o clasificacion asociada");
				parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_DATOSINFORME_BAJA_KO_EXISTE_CONDICION));
			}
		}catch (ConstraintViolationException ex) {
		logger.error("bajaCamposDatoInformes : no se puede borrar un dato de informe",ex);
		parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_DATOSINFORME_BAJA_KO_EXISTE_CONDICION));	
		
		}
		
		catch (DataIntegrityViolationException ex) {
			logger.error("bajaCamposDatoInformes : no se puede borrar un dato de informe",ex);
			parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_DATOSINFORME_BAJA_KO_EXISTE_CONDICION));	
			
		}
		
		 catch (Exception ex) {
			parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_DATOSINFORME_BAJA_KO));	
			logger.error("bajaCamposDatoInformes : error a borrar un dato de informe", ex); 
			
		}
			return parameters;
	}
	
	/**
	 * comprueba si el datoInforme tiene al menos una condición o clasificación/ruptura
	 * @param informe
	 * @return
	 */
	public boolean checkCondicionesYClasifDatoInforme (DatoInformes datInf){
		boolean tieneCondiciones = false;
		CamposPermitidos camPer = datInf.getCamposPermitidos();
		CamposCalculados camCalc = datInf.getCamposCalculados();
		if (camPer !=null){
			Set<ClasificacionRupturaCamposPermitidos> clasRupturaPerm = datInf.getClasificacionRupturaCamposPermitidoses();
			if (clasRupturaPerm.size()>0){
				tieneCondiciones = true;
			}
			Set<CondicionCamposPermitidos> condCamPer = datInf.getCondicionCamposPermitidoses();
			if (condCamPer.size()>0){
				tieneCondiciones = true;
			}
		}else if (camCalc !=null){
			Set<ClasificacionRupturaCamposCalculados> clasRupturaCalc = datInf.getClasificacionRupturaCamposCalculadoses();
			if (clasRupturaCalc.size()>0){
				tieneCondiciones = true;
			}
			Set<CondicionCamposCalculados> condCamCalc = datInf.getCondicionCamposCalculadoses();
			if (condCamCalc.size()>0){
				tieneCondiciones = true;
			}
		}
		return tieneCondiciones;
	}
	
	/**
	 * Realiza la subida de orden  dato del informe pasado como parámetro
	 * @param datoInformes
	 */
	
	public Map<String, Object>  subirNivelDatoInformesyActualizar(VistaMtoinfDatosInformes vistaMtoinfDatosInformes) throws BusinessException
	{
	Map<String, Object> parameters = new HashMap<String, Object>();
	
	try {
		
		DatoInformes datoInformes = (DatoInformes)mtoDatosInformeDao.getObject(DatoInformes.class,vistaMtoinfDatosInformes.getId().getId().longValue());
		List<DatoInformes>  listaDatoInforme = mtoDatosInformeDao.getListaDatoInformes(vistaMtoinfDatosInformes.getIdinforme().longValue());
		
		for(DatoInformes datoI :listaDatoInforme ){
			if(datoInformes.getOrden().intValue() ==  datoI.getOrden().intValue() +1){
		
				datoI.setOrden(new BigDecimal(datoI.getOrden().intValue() + 1));
				mtoDatosInformeDao.saveOrUpdate(datoI);
		
			}
		
		}
		
		datoInformes.setOrden(new BigDecimal(datoInformes.getOrden().intValue()-1));
		mtoDatosInformeDao.saveOrUpdate(datoInformes);
		parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_DATOSINFORME_MODIF_ORDEN_OK));	
		logger.debug("subirNivelDatoInformesyActualizar");
		} 
	
	catch (Exception ex) {
	 
		parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_DATOSINFORME_MODIF_ORDEN_KO));		
		logger.error("Error al subir de nivel de la lista de datos de l informe", ex);
		throw new BusinessException("Error al subir de nivel de la lista de datos de l informe", ex);
	
		}
	return parameters;
}
	/**
	 * Realiza la baja de orden  dato del informe pasado como parámetro
	 * @param datoInformes
	 */
	public Map<String, Object>  bajarNivelDatoInformesyActualizar(VistaMtoinfDatosInformes vistaMtoinfDatosInformes) throws BusinessException
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
	try {
		
		DatoInformes datoInformes = (DatoInformes)mtoDatosInformeDao.getObject(DatoInformes.class,vistaMtoinfDatosInformes.getId().getId().longValue());
		List<DatoInformes> listaDatoInforme = mtoDatosInformeDao.getListaDatoInformes(vistaMtoinfDatosInformes.getIdinforme().longValue());
		
		for(DatoInformes datoI :listaDatoInforme ){
			if(datoInformes.getOrden().intValue() ==  datoI.getOrden().intValue() - 1 )	
			{
			
				datoI.setOrden(new BigDecimal(datoI.getOrden().intValue()- 1));
				mtoDatosInformeDao.saveOrUpdate(datoI);
		
			}
		}
		
		datoInformes.setOrden(new BigDecimal(datoInformes.getOrden().intValue() + 1));
		mtoDatosInformeDao.saveOrUpdate(datoInformes);
		parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_DATOSINFORME_MODIF_ORDEN_OK));	
		logger.debug("bajaDatoInformesyActualizar");
		
	} catch (Exception ex) {
		
		parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_DATOSINFORME_MODIF_ORDEN_KO));	
		logger.error("Error al bajar de nivel de la lista de datos de l informe", ex);
		throw new BusinessException("Error al bajar de nivel de la lista de datos de l informe", ex);
	}
	return parameters;
}	
	

	/**
	 * Realiza la modificacion del dato del informe pasado como parámetro
	 * @param vistaMtoinfDatosInformes
	 * @return Map
	 */
	public Map<String, Object> modificarDatoInformes(VistaMtoinfDatosInformes vistaMtoinfDatosInformes) throws BusinessException{
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
				logger.debug("init - [MtoDatosInformeDao] modificarDatoInformes");
				
				if(mtoDatosInformeDao.existeDatosInforme(vistaMtoinfDatosInformes)){
					parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_DATOSINFORME_EXISTE_KO));	
				}
				else{
					DatoInformes datosInforme = (DatoInformes)mtoDatosInformeDao.getObject(DatoInformes.class,vistaMtoinfDatosInformes.getId().getId().longValue());
					
					// Si es un campo calculado
					if(vistaMtoinfDatosInformes.getId().getPermitidocalculado().intValue() == ConstantsInf.CAMPO_CALCULADO.intValue()){
						CamposCalculados campoCalculado= 	(CamposCalculados)mtoCamposCalculadosDao.getObject(CamposCalculados.class,new Long(vistaMtoinfDatosInformes.getIdcampo().longValue()));
						datosInforme.setCamposCalculados(campoCalculado);
						datosInforme.setCamposPermitidos(null);	
						
					}
					// Si es un campo permitido
					else if(vistaMtoinfDatosInformes.getId().getPermitidocalculado().intValue() == ConstantsInf.CAMPO_PERMITIDO.intValue() ){
						CamposPermitidos campoPermitido = (CamposPermitidos)mtoCamposPermitidosDao.getObject(CamposPermitidos.class,new Long(vistaMtoinfDatosInformes.getIdcampo().longValue()));				
						datosInforme.setCamposPermitidos(campoPermitido);
						datosInforme.setCamposCalculados(null);	
					}
				
				// Carga los datos del formulario en el objeto
				datosInforme.setId(vistaMtoinfDatosInformes.getId().getId().longValue());
				datosInforme.setAbreviado(vistaMtoinfDatosInformes.getAbreviado());
				datosInforme.setFormato(vistaMtoinfDatosInformes.getFormato());
				datosInforme.setDecimales(vistaMtoinfDatosInformes.getDecimales());
				datosInforme.setTotaliza(vistaMtoinfDatosInformes.getTotaliza());
				datosInforme.setTotalPorGrupo(vistaMtoinfDatosInformes.getTotal_por_grupo());
					
				// Actualiza el registro en base de datos
				mtoDatosInformeDao.saveOrUpdate(datosInforme);
			}
			
			parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_DATOSINFORME_MODIF_OK));
				
		} catch (Exception ex) {
			logger.error("Error a modificar un dato de los informes", ex);
			}
		return parameters;
	}
	
	/**
	 * Devuelve el objeto que muestra la columna 'Orden'
	 * @return
	 */
	private CellEditor getCellEditorOrden() {
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
				BigDecimal orden = null;
				try {
					orden = (BigDecimal) new BasicCellEditor().getValue(
							item, "orden", rowcount);
				} catch (Exception e) {
					logger.debug("orden nulo");
				}
            	
            	HtmlBuilder html = new HtmlBuilder();
            	// botón editar
            	html.append(StringUtils.nullToString(orden) + "&nbsp;");
            			
            	return html.toString();
            }
		};
	}
	
	/**
	 * Devuelve el objeto que muestra la columna 'Abreviado'
	 * @return
	 */
	private CellEditor getCellEditorAbreviado() {
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
            	String abreviadoValue = null;
				try {
					abreviadoValue =(String) new BasicCellEditor().getValue(item, "abreviado", rowcount);
		    		
				} catch (Exception e) {
					logger.debug("abreviado nulo");
				}
            	
            	HtmlBuilder html = new HtmlBuilder();
            	// botón editar
            	html.append(StringUtils.nullToString(abreviadoValue) + "&nbsp;");
            			
            	return html.toString();
            }
		};
	}
	/**
	 * Devuelve el objeto que muestra la columna 'Formato'
	 * @return
	 */
	private CellEditor getCellEditorFormato() {
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
				BigDecimal formatoValue = null;
            	
				try {
					formatoValue =(BigDecimal) new BasicCellEditor().getValue(item, "formato", rowcount);
				}catch (Exception e) {
					logger.debug("formato nulo");
				}
            	
            	HtmlBuilder html = new HtmlBuilder();
            	
            	if(formatoValue != null){
            	html.append(StringUtils.nullToString(InformeUtils.getValueFormato(formatoValue.toString())) + "&nbsp;");
            	}else{
            	html.append(StringUtils.nullToString(formatoValue)+ "&nbsp;");	
            	}
            	return html.toString();
            }
		};
	}
	
	
	
	
	
	/**
	 * Devuelve el objeto que muestra la columna 'Decimales'
	 * @return
	 */
	private CellEditor getCellEditorDecimales() {
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
				BigDecimal decimalesValue = null;
				try {
					decimalesValue =(BigDecimal) new BasicCellEditor().getValue(item, "decimales", rowcount);	
				} catch (Exception e) {
					logger.debug("decimales nulo");
				}
            	
            	HtmlBuilder html = new HtmlBuilder();
            	
            	html.append(StringUtils.nullToString(decimalesValue) + "&nbsp;");
            			
            	return html.toString();
            }
		};
	}
	
	/**
	 * Devuelve el objeto que muestra la columna 'Totaliza'
	 * @return
	 */
	private CellEditor getCellEditorTotaliza() {
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
				BigDecimal totalizaValue = null;
				String totalizaValueStr="";
				try {
					totalizaValue = (BigDecimal) new BasicCellEditor().getValue(item, "totaliza", rowcount) ;        
					totalizaValueStr = (totalizaValue.intValue() == ConstantsInf.COD_TOTALIZA_SUMA) ? "Suma" : "No";	
					}
				catch (Exception e) {
					logger.debug("totaliza nulo");
					
				}
		    	    	
		    	    		
            	HtmlBuilder html = new HtmlBuilder();
            	
            	html.append(StringUtils.nullToString(totalizaValueStr) + "&nbsp;");
            			
            	return html.toString();
            }
		};
	}
	
	/**
	 * Devuelve el objeto que muestra la columna 'Totaliza'
	 * @return
	 */
	private CellEditor getCellEditorTotalPorGrupo() {
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
				BigDecimal totalizaValue = null;
				String totalizaValueStr="";
				try {
					totalizaValue = (BigDecimal) new BasicCellEditor().getValue(item, "total_por_grupo", rowcount) ;        
					totalizaValueStr = (totalizaValue.intValue() == 1) ? "Si":"No";	
					}
				catch (Exception e) {
					logger.debug("total por grupo nulo");
					
				}
		    	    	
		    	    		
            	HtmlBuilder html = new HtmlBuilder();
            	
            	html.append(StringUtils.nullToString(totalizaValueStr) + "&nbsp;");
            			
            	return html.toString();
            }
		};
	}
	
	/**
	 * mapa de formatos para el combo de la jsp
	 */
	public Map<String, String> getMapFormatos() {
		Map<String, String> mapFormatos= new HashMap<String, String>();
		String formatosNum = "";
		String codFormatosNum = "";
		String formatosFec = "";
		String codFormatosFec = "";
		
		//1
		codFormatosFec += ConstantsInf.COD_FORMATO_FECHA_DDMMYYYY+"#";
		formatosFec += ConstantsInf.FORMATO_FECHA_DDMMYYYY+"#";
    	//2
		codFormatosFec += ConstantsInf.COD_FORMATO_FECHA_YYYYMMDD;
		formatosFec += ConstantsInf.FORMATO_FECHA_YYYYMMDD;
    	//3
		codFormatosNum += ConstantsInf.COD_FORMATO_NUM_NNNN+"#";
		formatosNum += ConstantsInf.FORMATO_NUM_NNNN+"#";
    	//4
		codFormatosNum += ConstantsInf.COD_FORMATO_NUM_N_NNN+"#";
		formatosNum += ConstantsInf.FORMATO_NUM_N_NNN+"#";
    	//5
		codFormatosNum += ConstantsInf.COD_FORMATO_NUM_NNNN_DD+"#";
		formatosNum += ConstantsInf.FORMATO_NUM_NNNN_DD+"#";
    	//6
		codFormatosNum += ConstantsInf.COD_FORMATO_NUM_N_NNN_DD;
		formatosNum += ConstantsInf.FORMATO_NUM_N_NNN_DD;
		
		mapFormatos.put("codFormatosFec", codFormatosFec);
		mapFormatos.put("formatosFec", formatosFec);
		mapFormatos.put("codFormatosNum", codFormatosNum);
		mapFormatos.put("formatosNum", formatosNum);
		
		return mapFormatos;
	}
	

	public IMtoInformeDao getMtoInformeDao() {
		return mtoInformeDao;
	}

	public void setMtoInformeDao(IMtoInformeDao mtoInformeDao) {
		this.mtoInformeDao = mtoInformeDao;
	}
	
	
}

