package com.rsi.agp.core.jmesa.service.impl.utilidades;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmesa.core.filter.MatcherKey;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Order;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.component.Column;
import org.jmesa.view.component.Row;
import org.jmesa.view.component.Table;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.editor.DateCellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.LongFilterMatcher;
import com.rsi.agp.core.jmesa.filter.ReduccionCapitalFilter;
import com.rsi.agp.core.jmesa.service.utilidades.IReduccionCapitalUtilidadesService;
import com.rsi.agp.core.jmesa.sort.ReduccionCapitalSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.filters.BigDecimalFilterMatcher;
import com.rsi.agp.dao.filters.CharacterFilterMatcher;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.poliza.IReduccionCapitalDao;
import com.rsi.agp.dao.models.poliza.IRiesgosDao;
import com.rsi.agp.dao.tables.cgen.Riesgo;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapitalUtilidades;

public class ReduccionCapitalUtilidadesService implements IReduccionCapitalUtilidadesService {
	
	private IReduccionCapitalDao reduccionCapitalDao;
	private IRiesgosDao riesgosDao;
	private String id = "listadoReduccionCapital";
	private Log logger = LogFactory.getLog(getClass());
	 
	// Nombres de los campos de la tabla
	public static final String CAMPO_ID = "id";
	public static final String CAMPO_IDPOLIZA = "idpoliza";
	public static final String CAMPO_ENTIDAD = "codentidad";
	public static final String CAMPO_OFICINA = "oficina";
	public static final String CAMPO_PLAN = "codplan";
	public static final String CAMPO_LINEA = "codlinea";
	public static final String CAMPO_POLIZA = "referencia";
	public static final String CAMPO_DC = "dc";
    public static final String CAMPO_NIF = "nifcif";
    public static final String CAMPO_NOMBRE = "nombre";
    public static final String CAMPO_FEC_ENVIO_POLIZA = "fenvpol";
    public static final String CAMPO_ORDEN = "numanexo";
    public static final String CAMPO_FEC_DANIOS = "fdanios";
    public static final String CAMPO_IDESTADO = "idestado";
    public static final String CAMPO_ESTADO = "descestado";
    public static final String CAMPO_FEC_ENVIO = "fenv";
    public static final String CAMPO_CODRIESGO = "codriesgo";
    public static final String CAMPO_RIESGO = "desriesgo";
    public static final String CAMPO_USUARIO = "codusuario";
    public static final String CAMPO_LISTADOGRUPOENT = "listaGrupoEntidades";
    public static final String CAMPO_LISTADOGRUPOOFI = "listaGrupoOficinas";
    public static final String CAMPO_ENTMEDIADORA = "entmediadora";
    public static final String CAMPO_SUBENTMEDIADORA = "subentmediadora";
    public static final String CAMPO_DELEGACION = "delegacion";
    //P0079361
    public static final String CAMPO_NUMEROCUPON = "idcupon";
    public static final String CAMPO_ESTADOCUPON = "estado";
    
    public static final String CAMPO_FEC_ENVIO_POLIZA_HASTA = "fenvpolHasta";
    public static final String CAMPO_FEC_DANIOS_HASTA = "fdaniosHasta";
    public static final String CAMPO_FEC_ENVIO_HASTA = "fenvHasta";
    //P0079361
    
    
    
    // Posición de los campos de la tabla
	public static final int POS_ENTIDAD = 1;
	public static final int POS_OFICINA = 2;
	public static final int POS_PLAN = 3;
	public static final int POS_LINEA = 4;
	public static final int POS_POLIZA = 5;
	public static final int POS_NIF = 6;
	public static final int POS_ASEGURADO = 7;
	public static final int POS_FEC_ENVIO_POLIZA = 8;	
	public static final int POS_ORDEN = 9;
	public static final int POS_FEC_DANIOS = 10;
	public static final int POS_ESTADO = 11;
	public static final int POS_FEC_ENVIO = 12;
	
	private ReduccionCapitalFilter reduccionCapitalFilter;
	private ReduccionCapitalSort reduccionCapitalSort;
	
	@Override
	public int getReduccionCapitalCountWithFilter(ReduccionCapitalFilter filter
			//P0079361
			,String fechadanioId, 
			String fechadanioIdHasta,
			String fechaEnvioId,
			String fechaEnvioIdHasta,
			String fechaEnvioPolId,
			String fechaEnvioPolIdHasta,
			String strTipoEnvioId
			//P0079361
			) throws BusinessException{
		return this.reduccionCapitalDao.getReduccionCapitalCountWithFilter(filter
				//P0079361
				,fechadanioId, 
				fechadanioIdHasta,
				fechaEnvioId,
				fechaEnvioIdHasta,
				fechaEnvioPolId,
				fechaEnvioPolIdHasta,
				strTipoEnvioId
				//P0079361
				);
	}

	@Override
	public Collection<ReduccionCapitalUtilidades> getReduccionCapitalWithFilterAndSort(ReduccionCapitalFilter filter, ReduccionCapitalSort sort, int rowStart,	int rowEnd
			//P0079361
			,String fechadanioId, 
			String fechadanioIdHasta,
			String fechaEnvioId,
			String fechaEnvioIdHasta,
			String fechaEnvioPolId,
			String fechaEnvioPolIdHasta,
			String strTipoEnvioId
			//P0079361
			) throws BusinessException {
		return this.reduccionCapitalDao.getReduccionCapitalWithFilterAndSort(filter, sort, rowStart, rowEnd
				//P0079361
				,fechadanioId, 
				fechadanioIdHasta,
				fechaEnvioId,
				fechaEnvioIdHasta,
				fechaEnvioPolId,
				fechaEnvioPolIdHasta,
				strTipoEnvioId
				//P0079361
				);
	}
	
	@Override
	public List<Riesgo> getRiesgos () {
		try {
			return riesgosDao.getRiesgosConTasables();
		} catch (DAOException e) {
			log ("getRiesgos", "Ocurrió un error al obtener el listado de riesgos", e);
		}
		
		return new ArrayList<Riesgo>();
	}

	public String getTablaReduccionCapital (HttpServletRequest request, HttpServletResponse response, ReduccionCapitalUtilidades redCapital, 
										    String primeraBusqueda, List<BigDecimal> listaGrupoEntidades, List<BigDecimal> listaGrupoOficinas) {
		
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response, redCapital, primeraBusqueda);
		
		String strTipoEnvioId = "";
		
		//P0079361
		if (request.getParameter("ftpNumCupon") == null || request.getParameter("ftpNumCupon").equals("") ){
			if(redCapital.getEstadoCuponId() != null ) {
				strTipoEnvioId = redCapital.getEstadoCuponId();
			}
		}
		else {
			strTipoEnvioId = request.getParameter("ftpNumCupon");
		}
		
		//String strTipoEnvioId = request.getParameter("ftpNumCupon") != null ? ( request.getParameter(arg0)(String) request.getParameter("ftpNumCupon") : "";
		
		/*
		if(redCapital.getCupon() != null && redCapital.getCupon().getEstadoCupon() != null 
				&& redCapital.getCupon().getEstadoCupon().getId() != null) {
			String strEstadoCuponId = Long.toString(redCapital.getCupon().getEstadoCupon().getEstado());
			
			if(strEstadoCuponId != null && !Constants.STR_EMPTY.equals(strEstadoCuponId)) {
				redCapital.getCupon().getEstadoCupon().setId(Long.valueOf(WSUtils.obtenerCodEstadoCuponByNumber(strEstadoCuponId)));
			}
		}
		*/
		
		logger.debug("[ DATOS RC ] idcupon: " + redCapital.getTipoEnvio() 
				+ " / estadoID: " + redCapital.getCupon().getEstadoCupon().getId()
				+ " / estado: " + redCapital.getCupon().getEstadoCupon().getEstado());
		//P0079361
		
		//P0079361
		//no parece obtener el dato de la misma forma que renovables
		String fechadanioId = (String) request.getParameter("fechadanioId");
		String fechadanioIdHasta = (String) request.getParameter("fechadanioIdHasta");
		String fechaEnvioId = (String) request.getParameter("fechaEnvioId");
		String fechaEnvioIdHasta = (String) request.getParameter("fechaEnvioIdHasta");
		String fechaEnvioPolId = (String) request.getParameter("fechaEnvioPolId");
		String fechaEnvioPolIdHasta = (String) request.getParameter("fechaEnvioPolIdHasta");
		
		
		
		Date fechaActual = Calendar.getInstance().getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaInit = WSUtils.parseoFechaRangos(Constants.STR_FECHA_INI);

		if(redCapital.getFdaniosNY()!=null && !Constants.STR_EMPTY.equals(redCapital.getFdaniosNY())) {
			redCapital.setFdanios(WSUtils.parseoFechaRangos(redCapital.getFdaniosNY()));
		}
		if (redCapital.getFdanios() != null) {
			fechadanioId = dateFormat.format(redCapital.getFdanios());
			redCapital.setFdaniosNY(fechadanioId);
		} else if (fechadanioId == null || Constants.STR_EMPTY.equals(fechadanioId)) {
			fechadanioId = dateFormat.format(fechaInit);
		} 
		

		if(redCapital.getFdaniosHastaNY()!=null && !Constants.STR_EMPTY.equals(redCapital.getFdaniosHastaNY())) {
			redCapital.setFdaniosHasta(WSUtils.parseoFechaRangos(redCapital.getFdaniosHastaNY()));
		}
		if (redCapital.getFdaniosHasta() != null) {
			fechadanioIdHasta = dateFormat.format(redCapital.getFdaniosHasta());
			redCapital.setFdaniosHastaNY(fechadanioIdHasta);
		} else if (fechadanioIdHasta == null || Constants.STR_EMPTY.equals(fechadanioIdHasta)) {
			fechadanioIdHasta = dateFormat.format(fechaActual);
		} 
		
		
		if(redCapital.getFenvNY()!=null && !Constants.STR_EMPTY.equals(redCapital.getFenvNY())) {
			redCapital.setFenv(WSUtils.parseoFechaRangos(redCapital.getFenvNY()));
		}
		if (redCapital.getFenv() != null) {
			fechaEnvioId = dateFormat.format(redCapital.getFenv());
			redCapital.setFenvNY(fechaEnvioId);
		} else if (fechaEnvioId == null || Constants.STR_EMPTY.equals(fechaEnvioId)) {
			fechaEnvioId = dateFormat.format(fechaInit);
		}

		
		
		if(redCapital.getFenvHastaNY()!=null && !Constants.STR_EMPTY.equals(redCapital.getFenvHastaNY())) {
			redCapital.setFenvHasta(WSUtils.parseoFechaRangos(redCapital.getFenvHastaNY()));
		}
		if (redCapital.getFenvHasta() != null) {
			fechaEnvioIdHasta = dateFormat.format(redCapital.getFenvHasta());
			redCapital.setFenvHastaNY(fechaEnvioIdHasta);
		} else if (fechaEnvioIdHasta == null || Constants.STR_EMPTY.equals(fechaEnvioIdHasta)) {
			fechaEnvioIdHasta = dateFormat.format(fechaActual);
		}
		
		if(redCapital.getFenvpolNY()!=null && !Constants.STR_EMPTY.equals(redCapital.getFenvpolNY())) {
			redCapital.setFenvpol(WSUtils.parseoFechaRangos(redCapital.getFenvpolNY()));
		}
		if (redCapital.getFenvpol() != null) {
			fechaEnvioPolId = dateFormat.format(redCapital.getFenvpol());
			redCapital.setFenvpolNY(fechaEnvioPolId);
		} else if (fechaEnvioPolId == null || Constants.STR_EMPTY.equals(fechaEnvioPolId)) {
			fechaEnvioPolId = dateFormat.format(fechaInit);
		}
		
		
		if(redCapital.getFenvpolHastaNY()!=null && !Constants.STR_EMPTY.equals(redCapital.getFenvpolHastaNY())) {
			redCapital.setFenvpolHasta(WSUtils.parseoFechaRangos(redCapital.getFenvpolHastaNY()));
		}
		if (redCapital.getFenvpolHasta() != null) {
			fechaEnvioPolIdHasta = dateFormat.format(redCapital.getFenvpolHasta());
			redCapital.setFenvpolHastaNY(fechaEnvioPolIdHasta);
		} else if (fechaEnvioPolIdHasta == null || Constants.STR_EMPTY.equals(fechaEnvioPolIdHasta)) {
			fechaEnvioPolIdHasta = dateFormat.format(fechaActual);
		}
		
		logger.debug("[ DATOS FECHAS ] fechadanioId: " + fechadanioId +" / fechadanioIdHasta: " + fechadanioIdHasta + " / fechaEnvioId: " + fechaEnvioId + " / fechaEnvioIdHasta: "
				+ fechaEnvioIdHasta + " / fechaEnvioPolId: " + fechaEnvioPolId + " / fechaEnvioPolIdHasta: " + fechaEnvioPolIdHasta+ " / NumCupon: " + strTipoEnvioId);
		//P0079361
		
		// Configura el filtro y la ordenación, busca los Red. Capital y los carga en el TableFacade
		setDataAndLimitVariables(tableFacade, 
				//P0079361
				fechadanioId, 
				fechadanioIdHasta,
				fechaEnvioId,
				fechaEnvioIdHasta,
				fechaEnvioPolId,
				fechaEnvioPolIdHasta,
				strTipoEnvioId,
				//strEstadoCuponId,
				//P0079361
				listaGrupoEntidades,listaGrupoOficinas);
		
		// Si se está generando un informe no se establecen los custom
		String ajax = request.getParameter("ajax");
		if (!"false".equals(ajax)){
			tableFacade.setToolbar(new CustomToolbar());
			tableFacade.setView(new CustomView());
		}

		// Genera el html de la tabla y lo devuelve	
		return html (tableFacade);		
	}
	
	/**
     * Método para construir el html de la tabla a mostrar
     * @param tableFacade
     * @return
     */
    @SuppressWarnings("deprecation")
	private String html(TableFacade tableFacade){
    	    	    
    	Limit limit = tableFacade.getLimit();
    	
    	// Si se va a exportar a un informe el listado
        if (limit.isExported()) {
        	Table table = tableFacade.getTable();
        	// Quita la columna Id del informe
        	eliminarColumnaId(tableFacade, table);
        	// renombramos las cabeceras
            configurarCabecerasColumnasExport(table);
            // Escribe los datos generados en el response
            tableFacade.render(); 
            // Devuelve nulo para que el controller no haga nada
            return null; 
        } 
        // Si se muestra el listado en la pantalla
        else {
        	HtmlTable table = (HtmlTable) tableFacade.getTable();
        	// Establece el id
        	table.getRow().setUniqueProperty("id");
            // Configuración de las columnas de la tabla    
        	configurarColumnas(table);
        	// Configuración de los datos de las columnas que requieren un tratamiento para mostrarse
        	// Acciones
        	table.getRow().getColumn(CAMPO_ID).getCellRenderer().setCellEditor(getCellEditorAcciones()); 
        	// Referencia
        	table.getRow().getColumn(CAMPO_POLIZA).getCellRenderer().setCellEditor(getCellEditorPoliza());
        	// Riesgo del RedCapital
        	table.getRow().getColumn(CAMPO_RIESGO).getCellRenderer().setCellEditor(getCellEditorRiesgo());
        }
    	
    	// Devuelve el html de la tabla
    	return tableFacade.render();
    }
        
    
    /**
     * Método que configura los nombres de las columnas para los informes
     * @param table
     */
    private void configurarCabecerasColumnasExport(Table table) {
		table.setCaption("Listado de Red. Capital");
    	
		Row row = table.getRow();
    	row.getColumn(CAMPO_ENTIDAD).setTitle("Entidad");
		row.getColumn(CAMPO_OFICINA).setTitle("Oficina");
		row.getColumn(CAMPO_PLAN).setTitle("Plan");
		row.getColumn(CAMPO_LINEA).setTitle("Linea");
		row.getColumn(CAMPO_POLIZA).setTitle("Poliza");
		row.getColumn(CAMPO_NIF).setTitle("NIF/CIF");
		row.getColumn(CAMPO_NOMBRE).setTitle("Asegurado");
		row.getColumn(CAMPO_FEC_ENVIO_POLIZA).setTitle("Fecha de envio de la poliza");
		row.getColumn(CAMPO_ORDEN).setTitle("Orden");
		row.getColumn(CAMPO_FEC_DANIOS).setTitle("Fecha daños");
		row.getColumn(CAMPO_ESTADO).setTitle("Estado");
		row.getColumn(CAMPO_FEC_ENVIO).setTitle("Fecha de envio del RedCapital");
		//faltaria anyadir cupon y estado para la extraccion de excel??
    }
    
    
    
    /**
     * Método que formatea los datos que se muestran en las celdas de la columna 'Poliza'
     * @return
     */
    private CellEditor getCellEditorPoliza() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				
				Object objref = new BasicCellEditor().getValue(item, CAMPO_POLIZA, rowcount);
				Object objdc = new BasicCellEditor().getValue(item, CAMPO_DC, rowcount);
				
				// Control de la referencia de póliza del RedCapital
				String ref = objref != null ? objref.toString() : "";
				String dc = objdc != null ? objdc.toString() : "";
				
				HtmlBuilder html = new HtmlBuilder();
				
				html.append("".equals(dc) ? ref : ref + "-" + dc);
				
				return html.toString();
            }
		};
	}
    
    /**
     * Método que formatea los datos que se muestran en las celdas de la columna 'Riesgo RedCapital'
     * @return
     */
    private CellEditor getCellEditorRiesgo() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				
				// DAA 30/07/2012 Control riesgo del Red. Capital
				ReduccionCapitalUtilidades rc = (ReduccionCapitalUtilidades)item;
				
				if(rc.getCodriesgo() != null){
					String codRiesgo = new BasicCellEditor().getValue(item, CAMPO_CODRIESGO, rowcount).toString();
					String riesgo = new BasicCellEditor().getValue(item, CAMPO_RIESGO, rowcount).toString();
					
					HtmlBuilder html = new HtmlBuilder();
					
					html.append(codRiesgo + " - " + riesgo);
					return html.toString();
				}else
					return "&nbsp";
            }
		};
	}
    
    /**
	 * Metodo que formatea los datos que se muestran en las celdas de la columna
	 * 'Acciones'
	 * 
	 * @return
	 */
    private CellEditor getCellEditorAcciones() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) { 
				
				// Estado del idRedCapital
				Short estado = new Short (new BasicCellEditor().getValue(item, CAMPO_IDESTADO, rowcount).toString());
				// Id del RedCapital
				String idRedCapital = new BasicCellEditor().getValue(item, CAMPO_ID, rowcount).toString();
				// Id de la poliza asociada al redCapital
				String idPoliza = new BasicCellEditor().getValue(item, CAMPO_IDPOLIZA, rowcount).toString();
				// Referencia de la poliza asociada al redCapital
				String referencia = new BasicCellEditor().getValue(item, CAMPO_POLIZA, rowcount).toString();
				// Fecha de envio a Agroseguro del redCapital
				Date fenv = (Date) new BasicCellEditor().getValue(item, CAMPO_FEC_ENVIO, rowcount);
				
				String plan = new BasicCellEditor().getValue(item, CAMPO_PLAN, rowcount).toString();
				
				//P0079361 validar si es correcto
				String estadoCuponRC = "";
				String idCuponRC = "";
				try {
					estadoCuponRC = new BasicCellEditor().getValue(item, CAMPO_ESTADOCUPON, rowcount).toString();
					idCuponRC = new BasicCellEditor().getValue(item, CAMPO_NUMEROCUPON, rowcount).toString();
				} catch (Exception e) {
					estadoCuponRC = "";
					idCuponRC = "";
					logger.debug("Estado de cupon Null" + e.getMessage());
				}
				// P0079361 validar si es correcto

				int numIconos = 0;
				
				HtmlBuilder html = new HtmlBuilder();
				
				if (!Constants.REDUCCION_CAPITAL_ESTADO_ENVIADO.equals(estado)
						&& !Constants.REDUCCION_CAPITAL_ESTADO_RECIBIDO_CORRECTO.equals(estado)) {
					// EDITAR
					numIconos++;
					
					if (Constants.AM_CUPON_ESTADO_CADUCADO_S.equals(estadoCuponRC.trim()) || idCuponRC.isEmpty()) {
						html.a().href().quote().append("javascript:editarRCCuponCaducado(" + idRedCapital + "," + idPoliza 
								+ ",'" + referencia + "'," + plan + ")").quote().close();
					}
					else {
						html.a().href().quote().append("javascript:editar(" + idRedCapital + "," + idPoliza  + "," + estado + ");").quote().close();
					}
					html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/>");
					html.aEnd();
					html.append("&nbsp;");
				
					// ELIMINAR
					numIconos ++;
					html.a().href().quote().append("javascript:eliminar(" + idRedCapital + "," + idPoliza + ");").quote().close();
					html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar\" title=\"Borrar\"/>");
	                html.aEnd();
	                html.append("&nbsp;");
				} else {
					// INFORMACION
					numIconos ++;
					html.a().href().quote().append("javascript:informacion(" + idRedCapital + "," + idPoliza + ");").quote().close();
					html.append("<img src=\"jsp/img/displaytag/information.png\" alt=\"Informaci&oacute;n de la RC\" title=\"Informaci&oacute;n de la RC\"/>");
	                html.aEnd();
	                html.append("&nbsp;");
				}
				
				// IMPRIMIR
				numIconos ++;
				//tipo sw
				if (estadoCuponRC.trim().equals(Constants.AM_CUPON_ESTADO_CONFIRMADO_APLICADO_S)) {
						html.a().href().quote().append("javascript:imprimirSwPDFIncidencia('"+idCuponRC+"');").quote().close();
						html.append("<img src=\"jsp/img/displaytag/imprimir.png\" alt=\"Imprimir Anexo\" title=\"Imprimir Anexo\"/>");		
						html.aEnd();
		                html.append("&nbsp;");
	                if (numIconos == 3) {
	                	html.append("<br>");
	                	numIconos = 0;
		            }
					html.a().href().quote().append("javascript:imprimir(" + idRedCapital + ");").quote().close();
					html.append("<img src=\"jsp/img/displaytag/imprimir_poliza_modificada.png\" alt=\"Imprimir P&oacute;liza Modificada\" title=\"Imprimir P&oacute;liza Modificada\"/>");
					html.aEnd();
	                html.append("&nbsp;");
	                if (numIconos == 3) {
	                	html.append("<br>");
	                	numIconos = 0;
		            }
				} else {
					html.a().href().quote().append("javascript:imprimir(" + idRedCapital + ");").quote().close();
					html.append("<img src=\"jsp/img/displaytag/imprimir.png\" alt=\"Imprimir RC\" title=\"Imprimir RC\"/>");
					html.aEnd();
					html.append("&nbsp;");
					if (numIconos == 3) {
						html.append("<br>");
						numIconos = 0;
					}
				}
				
                //P0079361
				// Acuse de Recibo
				if (fenv != null && (estadoCuponRC.trim().equals(Constants.AM_CUPON_ESTADO_ERROR_RECHAZADO_S)
						|| estadoCuponRC.trim().equals(Constants.AM_CUPON_ESTADO_ERROR_TRAMITE_S)
						|| estadoCuponRC.trim().contains(Constants.AM_CUPON_ESTADO_CONFIRMADO_TR_S)
						|| estadoCuponRC.trim().equals(Constants.AM_CUPON_ESTADO_CONFIRMADO_APLICADO_S))) {
					numIconos++;
					html.a().href().quote().append("javascript:verAcuseRecibo(" + idRedCapital + ");").quote().close();
					html.append("<img src=\"jsp/img/displaytag/acuserecibo.png\" alt=\"Consultar Acuse de Recibo\" title=\"Consultar Acuse de Recibo\"/>");
					html.aEnd();
					html.append("&nbsp;");
					if (numIconos == 3) {
						html.append("<br>");
						numIconos = 0;
					}
				}
				//P0079361
				
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
    	configColumna(table, CAMPO_ID, "&nbsp;&nbsp;Acciones", false, false, "3%");
    	// 1 - Entidad
    	configColumna(table, CAMPO_ENTIDAD, "Ent.</br>", true, true, "3%");
    	// 2 - Oficina
    	configColumna(table, CAMPO_OFICINA, "Ofi.</br>", true, true, "3%");
    	// 3 - Plan
    	configColumna(table, CAMPO_PLAN, "Plan</br>", true, true, "3%");
    	// 4 - Línea
    	configColumna(table, CAMPO_LINEA, "L&iacute;nea</br>", true, true, "3%");  
    	// 5 - Poliza
    	configColumna(table, CAMPO_POLIZA, "P&oacute;liza</br>", true, true, "10%");
    	// 6 - NIF/CIF
    	configColumna(table, CAMPO_NIF, "NIF/CIF</br>", true, true, "7%");
    	// 7 - Asegurado
    	configColumna(table, CAMPO_NOMBRE, "Asg. Nombre</br>", true, true, "10%");
    	// 8 - Fecha de envio de la póliza
    	configColumnaFecha(table, CAMPO_FEC_ENVIO_POLIZA, "Fec.Env.Pol</br>", true, true, "10%", "dd/MM/yyyy");
    	// 9 - Orden del Red.Capital dentro de la póliza
    	configColumna(table, CAMPO_ORDEN, "Orden</br>", true, true, "2%");
    	// 10 - Riesgo del Red.Capital
    	configColumna(table, CAMPO_RIESGO, "Riesgo Red.Cap</br>", true, true, "15%");
    	// 11 - Fecha de ocurrencia
    	configColumnaFecha(table, CAMPO_FEC_DANIOS, "Fec.Da&ntilde;os</br>", true, true, "7%", "dd/MM/yyyy");
		//P0079361
    	//12 - Numero de cupon
    	configColumna(table, CAMPO_NUMEROCUPON, "Tipo RC</br>", true, true, "7%");
		//P0079361
    	// 13 - Estado
    	configColumna(table, CAMPO_ESTADO, "Estado RC</br>", true, true, "10%");
		//P0079361
		//14 - Estado de Cupon
    	configColumna(table,CAMPO_ESTADOCUPON, "Estado Cup&oacute;n</br>", true, true, "7%");
    	//P0079361
    	// 15 - Fecha de envio
    	configColumnaFecha(table, CAMPO_FEC_ENVIO, "Fec.Env</br>", true, true, "7%", "dd/MM/yyyy");
    	//P0079361
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
			table.getRow().getColumn(idCol).getCellRenderer().setCellEditor(new DateCellEditor(fFecha));
		} catch (Exception e) {
			logger.error("Ocurrió un error al configurar el formato de fecha de la columna " + idCol, e);
		}
	}
	
	/**
	 * Crea y configura el objeto TableFacade que encapsulará la tabla de Red. Capital
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("deprecation")	
	public TableFacade crearTableFacade (HttpServletRequest request, HttpServletResponse response, ReduccionCapitalUtilidades redCapital, String primeraBusqueda) {
		
		// Crea el objeto TableFacade
		TableFacade tableFacade = new TableFacade(id, request);			
    	
		//Carga las columnas a mostrar en el listado en el TableFacade
		tableFacade.addFilterMatcher(new MatcherKey(Long.class), new LongFilterMatcher());
        tableFacade.addFilterMatcher(new MatcherKey(BigDecimal.class), new BigDecimalFilterMatcher());
        tableFacade.addFilterMatcher(new MatcherKey(Character.class), new CharacterFilterMatcher());
       
		cargarColumnas(tableFacade);
		
		//tableFacade.setExportTypes(response, ExportType.EXCEL);
        
        tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.
        
        //Defino los tipos para los filtros. Habrá que redefinir en el filter la forma
		tableFacade.addFilterMatcher(new MatcherKey(Date.class), new DateFromFilterMatcher("d/M/yyyy"));
        
        // Si no es una llamada a través de ajax        
    	if (request.getParameter("ajax") == null){
    		//P0079361
    		/*if(Constants.STR_EMPTY.equals(primeraBusqueda)) {
    			primeraBusqueda=null;
    		}*/
    		//P0079361
    		if (primeraBusqueda == null){
	    		if (request.getSession().getAttribute("listadoReduccionCapital_LIMIT") != null){
	    			//Si venimos por aquí es que ya hemos pasado por el filtro en algun momento
	    			tableFacade.setLimit((Limit) request.getSession().getAttribute("listadoReduccionCapital_LIMIT"));
	    		}
    		}
    		else{    			
    			// Carga en el TableFacade los filtros de búsqueda introducidos en el formulario 
    			cargarFiltrosBusqueda(redCapital, tableFacade);
    			
    			 // -- ORDENACIÓN POR DEFECTO --> Entidad asc, Plan desc, Linea asc, Poliza asc, orden asc
    			tableFacade.getLimit().getSortSet().addSort(new Sort (POS_ENTIDAD, CAMPO_ENTIDAD, Order.ASC));
    			tableFacade.getLimit().getSortSet().addSort(new Sort (POS_PLAN, CAMPO_PLAN, Order.DESC));  			    		
    			tableFacade.getLimit().getSortSet().addSort(new Sort (POS_LINEA, CAMPO_LINEA, Order.ASC));
    			tableFacade.getLimit().getSortSet().addSort(new Sort (POS_POLIZA, CAMPO_POLIZA, Order.ASC));
    			tableFacade.getLimit().getSortSet().addSort(new Sort (POS_ORDEN, CAMPO_ORDEN, Order.ASC));
    		}
    	}                
        
        return tableFacade;
	}
	
	/**
	 * Crea los objetos de filtro y ordenación, llama al dao para obtener los datos de Red. Capital y carga el TableFacade con ellos
	 * @param tableFacade
	 */
	public void setDataAndLimitVariables(TableFacade tableFacade, 
			//P0079361
			String fechadanioId, 
			String fechadanioIdHasta,
			String fechaEnvioId,
			String fechaEnvioIdHasta,
			String fechaEnvioPolId,
			String fechaEnvioPolIdHasta,
			String strTipoEnvioId,
			//String strEstadoCuponId,
			//P0079361
			List<BigDecimal> listaGrupoEntidades,List<BigDecimal> listaGrupoOficinas){
        
		// Obtiene el Filter para la búsqueda de pólizas
		Limit limit = tableFacade.getLimit();
		reduccionCapitalFilter = getReduccionCapitalFilter(limit, listaGrupoEntidades,listaGrupoOficinas); 

        // Obtiene el número de filas que cumplen el filtro        
        int totalRows = 0;
		try {
			totalRows = getReduccionCapitalCountWithFilter(reduccionCapitalFilter
					//P0079361
					,fechadanioId, 
					fechadanioIdHasta,
					fechaEnvioId,
					fechaEnvioIdHasta,
					fechaEnvioPolId,
					fechaEnvioPolIdHasta,
					strTipoEnvioId
					//P0079361
					);
			log ("setDataAndLimitVariables", "Numero de Red. Capital obtenidos = " + totalRows);
		} catch (BusinessException e1) {
			log ("setDataAndLimitVariables", "Error al obtener el numero de Red. Capital", e1);		
		}  
        
        tableFacade.setTotalRows(totalRows);

        // Crea el Sort para la búsqueda de Red. Capital
        reduccionCapitalSort = getReduccionCapitalSort(limit);
        int rowStart = limit.getRowSelect().getRowStart();
        int rowEnd = limit.getRowSelect().getRowEnd();
        
        // Obtiene los registros que cumplen el filtro
        Collection<ReduccionCapitalUtilidades> items = new ArrayList<ReduccionCapitalUtilidades>();		
        try {
        	//P0079361
        	//Falta que el int rows de arriba de la vista devuelve mayor que cero para indicar en la consulta el mismo filtro de fechas si no se envia, hay que depurar mas
        	//P0079361
			items = getReduccionCapitalWithFilterAndSort(reduccionCapitalFilter, reduccionCapitalSort, rowStart, rowEnd
					//P0079361
					,fechadanioId, 
					fechadanioIdHasta,
					fechaEnvioId,
					fechaEnvioIdHasta,
					fechaEnvioPolId,
					fechaEnvioPolIdHasta,
					strTipoEnvioId
					//P0079361
					);
			log ("setDataAndLimitVariables", "Registros en la lista de Red. Capital = " + items.size());
		} 
        catch (BusinessException e) {
			log ("setDataAndLimitVariables", "Error al obtener el listado de Red. Capital", e);
		}
        
        //P0079361
        //condicion si cupon informado y estado distinto de todos
        /*Collection<ReduccionCapitalUtilidades> listaCopiaToReturn = new ArrayList<ReduccionCapitalUtilidades>();	
        
        logger.debug("strTipoEnvioId: "+strTipoEnvioId);
        logger.debug("strEstadoCuponId: "+strEstadoCuponId);

        if(!Constants.STR_EMPTY.equals(strTipoEnvioId) || !Constants.STR_EMPTY.equals(strEstadoCuponId)) {
        	int totalRowsAux = 0;
	        for (ReduccionCapitalUtilidades item : items) {
	        	logger.debug("item.getEstadoCupon()"+item.getEstadoCupon());
	        	logger.debug("item.getNumeroCupon()"+item.getNumeroCupon());
	        	
	        	String estadoCuponSinEspaciosDelanteDetras = Constants.STR_EMPTY;
	        	String codEstadoCuponTexto = Constants.STR_EMPTY;
	        	
	        	if(item.getEstadoCupon()!=null && !item.getEstadoCupon().isEmpty() && !Constants.STR_EMPTY.equals(strEstadoCuponId)) {
	        		estadoCuponSinEspaciosDelanteDetras = item.getEstadoCupon().replaceAll("\\s","");
	        		codEstadoCuponTexto = obtenerCodEstadoCuponByNumber(strEstadoCuponId);
	        	}
	        	
	        	if(!Constants.STR_EMPTY.equals(strTipoEnvioId) && !Constants.STR_EMPTY.equals(strEstadoCuponId) 
	        			&& strTipoEnvioId.equals(item.getNumeroCupon()) && codEstadoCuponTexto.equals(estadoCuponSinEspaciosDelanteDetras)) {
	        		//los dos informados
	        		totalRowsAux++;
	        		listaCopiaToReturn.add(item);
	        	}else if(!Constants.STR_EMPTY.equals(strTipoEnvioId) && Constants.STR_EMPTY.equals(strEstadoCuponId) 
	        			&& strTipoEnvioId.equals(item.getNumeroCupon())) {
	        		//solo informado el codigo cupon getNumeroCupon
	        		totalRowsAux++;
	        		listaCopiaToReturn.add(item);
	        	}else if(Constants.STR_EMPTY.equals(strTipoEnvioId) && !Constants.STR_EMPTY.equals(strEstadoCuponId)  
	        			&& !Constants.STR_EMPTY.equals(codEstadoCuponTexto)
	        			&& !Constants.STR_EMPTY.equals(estadoCuponSinEspaciosDelanteDetras)
	        			&& codEstadoCuponTexto.equals(estadoCuponSinEspaciosDelanteDetras)) {
	        		//solo informado el codigo cupon getEstadoCupon
	        		totalRowsAux++;
	        		listaCopiaToReturn.add(item);
	        	}
	        }
	        tableFacade.setTotalRows(totalRowsAux);
	        tableFacade.setItems(listaCopiaToReturn);
        }else {
        	tableFacade.setItems(items);
        }*/
        tableFacade.setItems(items);
      //P0079361
        
    }
	
	/**
	 * Crea y configura el Filter para la consulta de Red. Capital
	 * @param limit
	 * @return
	 */
	public ReduccionCapitalFilter getReduccionCapitalFilter(Limit limit, List<BigDecimal> listaGrupoEntidades,List<BigDecimal> listaGrupoOficinas) {
		ReduccionCapitalFilter reduccionCapitalFilter = new ReduccionCapitalFilter();
        FilterSet filterSet = limit.getFilterSet();
        Collection<Filter> filters = filterSet.getFilters();
        for (Filter filter : filters) {
            String property = filter.getProperty();
            String value = filter.getValue();
            
            //P0079361
            //Problema filter con estado cupon RC
            /*if(Constants.STR_ESTADO_KEY.equals(property)) {
            	value = WSUtils.obtenerCodEstadoCuponByNumber(value);
            }*/
            //P0079361
            
            log ("getreduccionCapitalFilter" , "Añade al filtro - property: " + property + " - value: " + value);
            
            ////P0079361 evitar setteo properties hasta
            if(!Constants.STR_fdaniosHasta.equals(property) || !Constants.STR_fenvHasta.equals(property) 
            		|| !Constants.STR_fenvpolHasta.equals(property)) {
            	reduccionCapitalFilter.addFilter(property, value);
            }
            ////P0079361 evitar setteo properties hasta
        }
        
        // Si la lista de grupos de entidades no está vacía se incluye en el filtro de búsqueda
        if (listaGrupoEntidades!= null && listaGrupoEntidades.size()>0) {
        	reduccionCapitalFilter.addFilter(CAMPO_LISTADOGRUPOENT, listaGrupoEntidades);
        }
        // Si la lista de grupos de oficinas no está vacía se incluye en el filtro de búsqueda
        if (listaGrupoOficinas!= null && listaGrupoOficinas.size()>0) {
        	reduccionCapitalFilter.addFilter(CAMPO_LISTADOGRUPOOFI, listaGrupoOficinas);
        }
        
        return reduccionCapitalFilter;
	}
	
	/**
	 * Crea y configura el Sort para la consulta de Red. Capital
	 * @param limit
	 * @return
	 */
	public ReduccionCapitalSort getReduccionCapitalSort(Limit limit) {
		ReduccionCapitalSort reduccionCapitalSort = new ReduccionCapitalSort();
        SortSet sortSet = limit.getSortSet();
        Collection<Sort> sorts = sortSet.getSorts();
        for (Sort sort : sorts) {
            String property = sort.getProperty();
            String order = sort.getOrder().toParam();
            reduccionCapitalSort.addSort(property, order);
            
            log ("getreduccionCapitalSort" , "Añade la ordenacion - property: " + property + " - order: " + order);
        }

        return reduccionCapitalSort;
	}
	
	/**
	 * Carga las columnas a mostrar en el listado en el TableFacade
	 * @param tableFacade 
	 */
	@SuppressWarnings("all")
	private void cargarColumnas(TableFacade tableFacade) {		
				
    	// Configura el TableFacade con las columnas que se quieren mostrar
		//P0079361
        tableFacade.setColumnProperties(CAMPO_ID,CAMPO_ENTIDAD,CAMPO_OFICINA,CAMPO_PLAN,CAMPO_LINEA, CAMPO_POLIZA,CAMPO_NIF,CAMPO_NOMBRE,CAMPO_FEC_ENVIO_POLIZA,
        								CAMPO_ORDEN, CAMPO_RIESGO, CAMPO_FEC_DANIOS, CAMPO_NUMEROCUPON, CAMPO_ESTADO, CAMPO_ESTADOCUPON, CAMPO_FEC_ENVIO);
        //P0079361 
	}
	
	/**
	 * Carga en el TableFacade los filtros de búsqueda introducidos en el formulario
	 * @param redCapital
	 * @param tableFacade
	 */
	private void cargarFiltrosBusqueda(ReduccionCapitalUtilidades redCapital, TableFacade tableFacade) {		
		
		// Entidad
		if (FiltroUtils.noEstaVacio (redCapital.getCodentidad()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_ENTIDAD, redCapital.getCodentidad().toString());
		// Oficina
		if (FiltroUtils.noEstaVacio (redCapital.getOficina()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_OFICINA, redCapital.getOficina());
		// Usuario
		if (FiltroUtils.noEstaVacio (redCapital.getCodusuario()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_USUARIO, redCapital.getCodusuario());
		// Plan
		if (FiltroUtils.noEstaVacio (redCapital.getCodplan()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_PLAN, redCapital.getCodplan().toString());
		// Linea
		if (FiltroUtils.noEstaVacio (redCapital.getCodlinea()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_LINEA, redCapital.getCodlinea().toString());
		// Poliza
		if (FiltroUtils.noEstaVacio (redCapital.getReferencia())) 
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_POLIZA, redCapital.getReferencia());
		// NIF/CIF
		if (FiltroUtils.noEstaVacio (redCapital.getNifcif()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_NIF, redCapital.getNifcif());
		// Asegurado
		if (FiltroUtils.noEstaVacio (redCapital.getNombre()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_NOMBRE, redCapital.getNombre());
		// Riesgo
		if (FiltroUtils.noEstaVacio (redCapital.getCodriesgo()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_CODRIESGO, redCapital.getCodriesgo());
		// Fecha de ocurrencia
		if (FiltroUtils.noEstaVacio (redCapital.getFdanios())) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(CAMPO_FEC_DANIOS, new SimpleDateFormat("dd/MM/yyyy").format(redCapital.getFdanios()) + ">="));
		}
		// Fecha de envío del redCapital
		if (FiltroUtils.noEstaVacio (redCapital.getFenv())) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(CAMPO_FEC_ENVIO, new SimpleDateFormat("dd/MM/yyyy").format(redCapital.getFenv()) + ">="));
		}
		// Fecha de envío de la póliza
		if (FiltroUtils.noEstaVacio (redCapital.getFenvpol())) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(CAMPO_FEC_ENVIO_POLIZA, new SimpleDateFormat("dd/MM/yyyy").format(redCapital.getFenvpol()) + ">="));
		}
		//P0079361
		if (FiltroUtils.noEstaVacio (redCapital.getFdaniosHasta())) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(CAMPO_FEC_DANIOS,"<=" + new SimpleDateFormat("dd/MM/yyyy").format(redCapital.getFdaniosHasta())));
		}
		// Fecha de envío del redCapital
		if (FiltroUtils.noEstaVacio (redCapital.getFenvHasta())) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(CAMPO_FEC_ENVIO,"<=" + new SimpleDateFormat("dd/MM/yyyy").format(redCapital.getFenvHasta())));
		}
		// Fecha de envío de la póliza
		if (FiltroUtils.noEstaVacio (redCapital.getFenvpolHasta())) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(CAMPO_FEC_ENVIO_POLIZA,"<=" + new SimpleDateFormat("dd/MM/yyyy").format(redCapital.getFenvpolHasta())));
		}
		//P0079361
		// Estado
		if (FiltroUtils.noEstaVacio (redCapital.getIdestado()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_IDESTADO, redCapital.getIdestado().toString());
		//delegacion
		if (FiltroUtils.noEstaVacio (redCapital.getDelegacion()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_DELEGACION, redCapital.getDelegacion().toString());
		//entidad mediadora
		if (FiltroUtils.noEstaVacio (redCapital.getEntmediadora()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_ENTMEDIADORA, redCapital.getEntmediadora().toString());
		//subentidad mediadora
		if (FiltroUtils.noEstaVacio (redCapital.getSubentmediadora()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_SUBENTMEDIADORA, redCapital.getSubentmediadora().toString());
		//P0079361
		//numero cupon
		if (FiltroUtils.noEstaVacio (redCapital.getTipoEnvio()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_NUMEROCUPON, redCapital.getTipoEnvio().toString());
		//estado cupon
		if (FiltroUtils.noEstaVacio(redCapital.getCupon().getEstadoCupon().getId())) {
			String textoCuponPrimeraConsulta = WSUtils.obtenerCodEstadoCuponByNumber(Long.toString(redCapital.getCupon().getEstadoCupon().getId()));
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_ESTADOCUPON, textoCuponPrimeraConsulta);
		}
		//P0079361
	}
	
	
	
	
	/**
     * método para eliminar la columna Id en los informes
     * @param tableFacade
     * @param table
     */
    private void eliminarColumnaId(TableFacade tableFacade, Table table){
    	Row row = table.getRow();
        Row rowFinal = new Row();
        List<Column> lstColumns = row.getColumns();
        for (Column col:lstColumns){
        	if (!col.getProperty().equals("id")){
        		rowFinal.addColumn(col);
        	}
        }
        table.setRow(rowFinal);
        tableFacade.setTable(table);
    }
	
	/**
	 * Escribe en el log indicando la clase y el método.
	 * @param method
	 * @param msg
	 */
	private void log (String method, String msg) {
		logger.debug("reduccionCapitalUtilidadesService." + method + " - " + msg);
	}
	
	/**
	 * Escribe en el log indicando la clase, el método y la excepción.
	 * @param method
	 * @param msg
	 * @param e
	 */
	private void log (String method, String msg, Throwable e) {
		logger.error("reduccionCapitalUtilidadesService." + method + " - " + msg, e);
	}

	/**
	 * Setter del Dao para Spring
	 * @param riesgosDao
	 */
	public void setRiesgosDao(IRiesgosDao riesgosDao) {
		this.riesgosDao = riesgosDao;
	}

	/**
	 * Setter del Dao para Spring
	 * @param reduccionCapitalDao
	 */
	public void setReduccionCapitalDao(IReduccionCapitalDao reduccionCapitalDao) {
		this.reduccionCapitalDao = reduccionCapitalDao;
	}

	@Override
	public List<ReduccionCapitalUtilidades> getAllFilteredAndSorted(String estadoCuponRC, String tipoEnvioRC, String fEEnvio, String fEEnvioHasta, String fEdanio, String fEdanioHasta, String fEEnvioPol, String fEEnvioPolHasta) throws BusinessException {
		// Obtener todos los registros filtrados y ordenados sin l�mites de paginaci�n
		
		//P0079361
		//EVITAR ERROR aL MODIFICAR FUNCION QUE SE LLAMA DESDE LA EXPORTACION, no se menciona en RQs
		//se mandan todas inicio y fin por defecto
		Date fechaActual = Calendar.getInstance().getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaInit = WSUtils.parseoFechaRangos(Constants.STR_FECHA_INI);
		
		String fechadanioId = dateFormat.format(fechaInit);
		String fechadanioIdHasta = dateFormat.format(fechaActual);
		String fechaEnvioId = dateFormat.format(fechaInit);
		String fechaEnvioIdHasta = dateFormat.format(fechaActual);
		String fechaEnvioPolId = dateFormat.format(fechaInit);
		String fechaEnvioPolIdHasta = dateFormat.format(fechaActual);
		String strTipoEnvioId = "";
		
		//sobreescritura con datos si se lanza desde extraccion excel
		/*if(estadoCuponRC != null && Constants.STR_EMPTY.equals(estadoCuponRC)) {
			
		}*/
		
		if(tipoEnvioRC != null && !Constants.STR_EMPTY.equals(tipoEnvioRC)) {
			strTipoEnvioId = tipoEnvioRC;
		}
		
		if(fEEnvio != null && !Constants.STR_EMPTY.equals(fEEnvio)) {
			fechaEnvioId = fEEnvio;
		}
		
		if(fEEnvioHasta != null && !Constants.STR_EMPTY.equals(fEEnvioHasta)) {
			fechaEnvioIdHasta = fEEnvioHasta;
		}
		
		if(fEdanio != null && !Constants.STR_EMPTY.equals(fEdanio)) {
			fechadanioId = fEdanio;
		}
		
		if(fEdanioHasta != null && !Constants.STR_EMPTY.equals(fEdanioHasta)) {
			fechadanioIdHasta = fEdanioHasta;
		}
		
		if(fEEnvioPol != null && !Constants.STR_EMPTY.equals(fEEnvioPol)) {
			fechaEnvioPolId = fEEnvioPol;
		}
		
		if(fEEnvioPolHasta != null && !Constants.STR_EMPTY.equals(fEEnvioPolHasta)) {
			fechaEnvioPolIdHasta = fEEnvioPolHasta;
		}
		//sobreescritura con datos si se lanza desde extraccion excel
		
		logger.debug("[DATOS FECHAS ] fechadanioId: " + fechadanioId +" / fechadanioIdHasta: " + fechadanioIdHasta + " / fechaEnvioId: " + fechaEnvioId + " / fechaEnvioIdHasta: "
				+ fechaEnvioIdHasta + " / fechaEnvioPolId: " + fechaEnvioPolId + " / fechaEnvioPolIdHasta: " + fechaEnvioPolIdHasta + " / strTipoEnvioId: " + strTipoEnvioId);
		//P0079361
		
	    Collection<ReduccionCapitalUtilidades> allResults = reduccionCapitalDao.getReduccionCapitalWithFilterAndSort(reduccionCapitalFilter, reduccionCapitalSort, -1, -1
	    		//P0079361
				,fechadanioId, 
				fechadanioIdHasta,
				fechaEnvioId,
				fechaEnvioIdHasta,
				fechaEnvioPolId,
				fechaEnvioPolIdHasta,
				strTipoEnvioId
				//P0079361
				);
	    return (List<ReduccionCapitalUtilidades>) allResults;
	}
}
