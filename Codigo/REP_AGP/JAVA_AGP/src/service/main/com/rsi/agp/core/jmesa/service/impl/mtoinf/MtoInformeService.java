package com.rsi.agp.core.jmesa.service.impl.mtoinf;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import com.rsi.agp.core.jmesa.filter.InformeFilter;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoInformeService;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoVistasService;
import com.rsi.agp.core.jmesa.sort.InformeSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.util.DateUtil;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.mtoinf.IMtoInformeDao;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.comisiones.InformeComisiones;
import com.rsi.agp.dao.tables.comisiones.InformeComisiones2015;
import com.rsi.agp.dao.tables.comisiones.InformeRecibos;
import com.rsi.agp.dao.tables.comisiones.InformeRecibos2015;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.mtoinf.CamposCalculados;
import com.rsi.agp.dao.tables.mtoinf.CamposPermitidos;
import com.rsi.agp.dao.tables.mtoinf.ClasificacionRupturaCamposCalculados;
import com.rsi.agp.dao.tables.mtoinf.ClasificacionRupturaCamposPermitidos;
import com.rsi.agp.dao.tables.mtoinf.CondicionCamposCalculados;
import com.rsi.agp.dao.tables.mtoinf.CondicionCamposPermitidos;
import com.rsi.agp.dao.tables.mtoinf.DatoInformes;
import com.rsi.agp.dao.tables.mtoinf.FormatoCampoGenerico;
import com.rsi.agp.dao.tables.mtoinf.Informe;
import com.rsi.agp.dao.tables.mtoinf.OperadorCamposPermitido;
import com.rsi.agp.dao.tables.mtoinf.RelVistaCampos;
import com.rsi.agp.dao.tables.mtoinf.RelVistaPerfil;
import com.rsi.agp.dao.tables.mtoinf.Vista;
import com.rsi.agp.dao.tables.mtoinf.VistaCampo;

@SuppressWarnings("deprecation")
public class MtoInformeService implements IMtoInformeService {
	
	private IMtoVistasService mtoVistasService;
	private IMtoInformeDao mtoInformeDao;	
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_inf");
	
	// Constantes para los nombres de las columnas del listado
	private String id;
	private Log logger = LogFactory.getLog(getClass());
	private final static String ID_STR = "ID";
	private final static String NOMBRE = "NOMBRE";
	private final static String TITULO1 = "TITULO1";
	private final static String TITULO2 = "TITULO2";
	private final static String TITULO3 = "TITULO3";
	private final static String VISIBILIDAD = "VISIBILIDAD";
	private final static String VISIBILIDADENT = "VISIBILIDADENT";
	private final static String CUENTA = "CUENTA";
	private final static String USUARIO = "USUARIO";
	private final static String FECHA_ALTA = "FECHA_ALTA";
	
	// Constantes para los nombres de los campos para filtrar por perfil de usuario
	private final static String CODENTIDAD = "CODENTIDAD";
	private final static String OFICINA = "OFICINA";
	private final static String CODUSUARIO = "CODUSUARIO";
	private final static String CODENTMED = "CODENTIDADMEDIADORA";
	private final static String CODSUBENTMED = "CODSUBENTIDADMEDIADORA";
	private final static String DELEGACION = "DELEGACION";
	
	private final static String ALERTA = "alerta";
	private final static String ESPACIO = "&nbsp;";
	private final static String ESQUEMA = "o02agpe0";
	private final static String LISTA_RELACIONES_TABLAS = "lstRelacionesTablas";
	private final static String FECCAR = "FECCAR";
	private final static String ACTUALIZAR_SQL = "actualizar sql";
	private final static String NUM_REGISTROS = "numRegistros";
	private final static String NUM_REGISTROS_EQUALS = "numRegistros = ";
	private final static String INFORME = "informe";
	private final static String CABECERAS = "cabeceras";
	private final static String CABECERAS_NOMBRE = "cabecerasNombre";
	private final static String TOTALIZA = "totaliza";
	private final static String TOTAL_POR_GRUPO = "totalPorGrupo";
	private final static String RUPTURA = "ruptura";
	private final static String FORMATO = "formato";
	private final static String DECIMALES = "decimales";
	private final static String SIN_DATOS_INFORME = "sin datosInforme";
	private final static String DETALLE = "detalle";
	private final static String RESUMEN = "resumen";
	private final static String PRUEBA_FICHERO_COMISIONES = "PRUEBA FICHERO COMISIONES";
	
	
	private final static String QUERY_SELECT_ALL = "SELECT * FROM (";
	private final static String QUERY_SELECT = "SELECT ";
	private final static String QUERY_SELECT_DISTINCT = "SELECT DISTINCT ";
	private final static String QUERY_NVL = " NVL(";
	private final static String QUERY_TO_NUMBER = "TO_NUMBER((NVL(";
	private final static String QUERY_0 = ",'0') ";
	private final static String QUERY_0_AS = ",'0'))) AS \"";
	private final static String QUERY_TO_CHAR = "TO_CHAR(";
	private final static String QUERY_AS = "') AS \"";
	private final static String QUERY_FROM = " FROM ";
	private final static String QUERY_WHERE = " WHERE ";
	private final static String QUERY_AND = " AND ";
	private final static String QUERY_CONCAT = " (+) = ";
	private final static String QUERY_WHERE_1 = " WHERE 1 = 1 ";
	private final static String QUERY_AND_DATE = " AND TO_DATE(";
	private final static String QUERY_GROUP = " GROUP BY ";
	private final static String QUERY_ORDER = " ORDER BY ";
	
	// Mapa con las columnas del listado y los campos del filtro de busqueda
	private Map<String, String> columnas = new HashMap<>();
	
	// Mapa con el nombre de los setter del nombre de los campos y condiciones
	private static HashMap<String, String> mapaSettersCampos = new HashMap<>();
	private static HashMap<String, String> mapaSettersCondi = new HashMap<>();
	static{
		mapaSettersCampos.put("FASE", "setFase");
		mapaSettersCampos.put("REF_COLECTIVO", "setColectivo");
		mapaSettersCampos.put("CODLINEA", "setLinea");
		mapaSettersCampos.put("CODPLAN", "setPlan");
		mapaSettersCampos.put("CODENTIDAD_MED", "setEntidad");
		mapaSettersCampos.put("CODSUBENT_MED", "setSubent");
		mapaSettersCampos.put("FECHA_EMISION", "setFecha");
		mapaSettersCampos.put("NUM_RECIBO", "setRecibo");
		mapaSettersCampos.put("REF_POLIZA", "setReferencia");
		mapaSettersCampos.put("NIF_ASEGURADO", "setNif");
		
		mapaSettersCondi.put("setFase", "setCondiFase");
		mapaSettersCondi.put("setColectivo", "setCondiColectivo");
		mapaSettersCondi.put("setLinea", "setCondiLinea");
		mapaSettersCondi.put("setPlan", "setCondiPlan");
		mapaSettersCondi.put("setEntidad", "setCondiEntidad");
		mapaSettersCondi.put("setSubent", "setCondiSubent");
		mapaSettersCondi.put("setFecha", "setCondiFecha");
		mapaSettersCondi.put("setRecibo", "setCondiRecibo");
		mapaSettersCondi.put("setReferencia", "setCondiReferencia");
		mapaSettersCondi.put("setNif", "setCondiNif");
	}
	
	private static List<String> listCamposResumen = new ArrayList<>();
	static {
		listCamposResumen.add("RS_TOMADOR");
		listCamposResumen.add("SALDO_TOMADOR");
		listCamposResumen.add("COMPENSACION_TOM");
		listCamposResumen.add("COMPENSACION_IMP");
		listCamposResumen.add("PAGO_RECIBO");
		listCamposResumen.add("LIQUIDO_RECIBO");
	}
	
	/**
	 * Setter de propiedad
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Metodo que da de alta un nuevo informe
	 */
	public Map<String, Object> altaInforme(Informe informe) throws BusinessException {
		
		Map<String, Object> parameters = new HashMap<>();	
		try {
			if (mtoInformeDao.checkInformeExists(informe.getNombre(),null)) {
				parameters.put(ALERTA, bundle.getObject(ConstantsInf.ALERTA_INFORME_ALTA_EXISTE_KO));
			}else{
				mtoInformeDao.saveOrUpdate(informe);
				parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_INFORME_ALTA_OK));
			}					
		} catch (Exception ex) {
			throw new BusinessException("MtoInformeService - altaInforme - Error al dar de alta el informe", ex);
		}
		
		return parameters;
	}
	
	public Map<String, Object> editarInforme(Informe informeEdit) throws BusinessException {
		Map<String, Object> parameters = new HashMap<>();	
		try {
			if (mtoInformeDao.checkInformeExists(informeEdit.getNombre(),informeEdit.getId())) {
				parameters.put(ALERTA, bundle.getObject(ConstantsInf.ALERTA_INFORME_ALTA_EXISTE_KO));
			}else{
				mtoInformeDao.saveOrUpdate(informeEdit);
				parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_INFORME_MODIF_OK));
				logger.debug("Informe editado con id= "+informeEdit.getId());
			}
		} catch (DataIntegrityViolationException ex) {
			logger.error("Error al editar el informe ", ex);
			throw new BusinessException("Error al editar el  informe", ex);
		} catch (Exception ex) {
			logger.error("Error al  editar el informe", ex);
			throw new BusinessException("Error al editar el informe", ex);
		}
		return parameters;
	}
	
	public boolean bajaInforme(Informe informe) throws BusinessException {		
		try { // borramos primero los datos informes y condiciones/clasificacionRuptura asociados al informe
			for (DatoInformes datInf : informe.getDatoInformeses()) {
				deleteSet(datInf.getCondicionCamposCalculadoses());
				deleteSet(datInf.getCondicionCamposPermitidoses());
				deleteSet(datInf.getClasificacionRupturaCamposCalculadoses());
				deleteSet(datInf.getClasificacionRupturaCamposPermitidoses());
				// borramos el datInforme
				mtoInformeDao.delete(datInf);
				mtoInformeDao.evict(datInf);
			}
			mtoInformeDao.delete(informe);
			logger.debug("Informe borrado = " + informe.getId());
		} catch (Exception ex) {
			logger.error("Error al eliminar el Informe", ex);
			throw new BusinessException("Error al eliminar el Informe", ex);
		}
		return false;
	}
	
	private void deleteSet(Set<?> auxSet) throws DAOException {
		for (Object aux : auxSet){
			if (aux != null){
				mtoInformeDao.delete(aux);
				mtoInformeDao.evict(aux);
			}
		}
	}

	
	public Informe getInforme(Long id) throws BusinessException {
		try {
			return (Informe) mtoInformeDao.getObject(Informe.class, id);
		} catch (Exception dao) {
			logger.error("Se ha producido al obtener el Informe", dao);
			throw new BusinessException("Se ha producido al obtener el informe:", dao);
		}
	}
	
	public Informe getInformeOculto (BigDecimal idOculto) throws BusinessException {
		try {
			return (Informe) mtoInformeDao.getInformeOculto(idOculto);
		} catch (Exception dao) {
			logger.error("Se ha producido al obtener el Informe", dao);
			throw new BusinessException("Se ha producido al obtener el informe:", dao);
		}
}

	@Override
	public String getTablaInformes(HttpServletRequest request,	HttpServletResponse response, 
								   Informe informe, String origenLlamada, String cadenaCodigosLupas, final Usuario usuario) {
		
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response, informe, origenLlamada);
		
		// Configura el filtro y la ordenacion, busca las polizas y las carga en el TableFacade
		setDataAndLimitVariables(tableFacade, cadenaCodigosLupas, usuario);
		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());

		// Genera el html de la tabla y lo devuelve	
		return html (tableFacade, request);
	}
	
	/**
     * Metodo para construir el html de la tabla a mostrar
     * @param tableFacade
     * @return
     */
    private String html(TableFacade tableFacade, HttpServletRequest request){	
    	HtmlTable table = (HtmlTable) tableFacade.getTable();
    	table.getRow().setUniqueProperty("id");
    	
        // Configuracion de las columnas de la tabla    
    	configurarColumnas(table);    
    	
    	Limit limit = tableFacade.getLimit();
        if (limit.hasExport()) {
            tableFacade.render(); 
            return null; 
        } else {
        	// Configuracion de los datos de las columnas que requieren un tratamiento para mostrarse
        	
        	// campo acciones
        	table.getRow().getColumn(columnas.get(ID_STR)).getCellRenderer().setCellEditor(getCellEditorAcciones(request));
        	// campo visibilidad
        	table.getRow().getColumn(columnas.get(VISIBILIDAD)).getCellRenderer().setCellEditor(getCellEditorVisibilidad());
        	// campo cuenta
        	table.getRow().getColumn(columnas.get(CUENTA)).getCellRenderer().setCellEditor(getCellEditorCuenta());
        	
        }
        
    	// Devuelve el html de la tabla
    	return tableFacade.render();
    }
  
    /**
	 * Devuelve el objeto que muestra la informacion de la columna 'Acciones'
	 * @return
	 */
    private CellEditor getCellEditorAcciones(final HttpServletRequest request) {		
		return new CellEditor() {
			@SuppressWarnings("unchecked")
			public Object getValue(Object item, String property, int rowcount) {
				
				String cadenaCodigosLupas = "";
				BigDecimal perfilInforme = null;
				Long idInformeLong = (Long) new BasicCellEditor().getValue(item, columnas.get(ID_STR), rowcount);
            	String idInforme = idInformeLong.toString();
            	String nombre = StringUtils.nullToString(new BasicCellEditor().getValue(item, columnas.get(NOMBRE), rowcount));
            	String titulo1 = StringUtils.nullToString(new BasicCellEditor().getValue(item, columnas.get(TITULO1), rowcount));
            	String titulo2 = StringUtils.nullToString(new BasicCellEditor().getValue(item, columnas.get(TITULO2), rowcount));
            	String titulo3 = StringUtils.nullToString(new BasicCellEditor().getValue(item, columnas.get(TITULO3), rowcount));
				BigDecimal visibilidad = (BigDecimal)new BasicCellEditor().getValue(item, columnas.get(VISIBILIDAD), rowcount);
				BigDecimal visibilidadEnt = (BigDecimal)new BasicCellEditor().getValue(item, columnas.get(VISIBILIDADENT), rowcount);
				BigDecimal cuenta = (BigDecimal)new BasicCellEditor().getValue(item, columnas.get(CUENTA), rowcount);
				String propietario = StringUtils.nullToString(new BasicCellEditor().getValue(item, columnas.get(USUARIO), rowcount));

				// Obtiene la fecha de alta del informe y la formatea para anhadirla a la llamada de la funcion javascript
				Date fAux = (Date) new BasicCellEditor().getValue(item, columnas.get(FECHA_ALTA), rowcount);
				String fechaAlta = StringUtils.nullToString(DateUtil.date2String(fAux, DateUtil.FORMAT_DATE_DEFAULT));

				// Si tiene visibilidad de tipo 'Perfil', se obtiene el codigo de perfil seleccionado
				if (new BigDecimal(ConstantsInf.COD_VISIBILIDAD_PERFIL).equals(visibilidad)){
					perfilInforme = ((BigDecimal)new BasicCellEditor().getValue(item, "perfil", rowcount));
				}
				// Si tiene visibilidad de tipo 'Usuarios' se carga el listado y se convierte en cadena separada por '#'
				else if (new BigDecimal(ConstantsInf.COD_VISIBILIDAD_USUARIOS).equals(visibilidad)) {
					Set<Usuario> usuariosInforme = (Set<Usuario>) new BasicCellEditor().getValue(item, "usuarios", rowcount); 
					for (Usuario usu: usuariosInforme){
						cadenaCodigosLupas += ((cadenaCodigosLupas.length() > 0) ? "#" : "") + usu.getCodusuario();
	            	}
				}
				// Si tiene visibilidad de tipo 'Entidades' se carga el listado y se convierte en cadena separada por '#'
				if (new BigDecimal(ConstantsInf.COD_VISIBILIDAD_ENTIDADES_SI).equals(visibilidadEnt)) {
					Set<Entidad> entidadesInforme = (Set<Entidad>) new BasicCellEditor().getValue(item, "entidades", rowcount); 
					for (Entidad ent: entidadesInforme){
						cadenaCodigosLupas += ((cadenaCodigosLupas.length() > 0) ? "#" : "") + ent.getCodentidad();
	            	}
				}
				
				
            	HtmlBuilder html = new HtmlBuilder();
            	// boton editar
    			html.a().href().quote().append("javascript:editar('"+idInforme+"','"+nombre+"','"+titulo1+"','"+titulo2+"', '"+titulo3
    					+"','"+ StringUtils.nullToString (visibilidad)+"','"+ StringUtils.nullToString (visibilidadEnt)+"','"
    					+ StringUtils.nullToString (perfilInforme)+ "', '"+cadenaCodigosLupas+"', '"+cuenta	+ "','" 
    					+ propietario + "','" + fechaAlta + "');").quote().close();
                
    			html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Informe\" title=\"Editar Informe\"/>");
                html.aEnd();
                html.append(ESPACIO);
                    
                // boton borrar
	            html.a().href().quote().append("javascript:borrar('"+ idInforme +"');").quote().close();
	            html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Informe\" title=\"Borrar Informe\"/>");
	            html.aEnd();
	            html.append(ESPACIO);
	            
	            // boton duplicar
	            html.a().href().quote().append("javascript:openPopupDuplicar('"+ idInforme +"');").quote().close();
	            html.append("<img src=\"jsp/img/displaytag/duplicar.png\" alt=\"Duplicar Informe\" title=\"Duplicar Informe\"/>");
	            html.aEnd();
	            html.append(ESPACIO);
        		
		         // boton visualizar
    			html.a().href().quote().append("javascript:visualizar('"+idInforme+"','"+nombre+"','"+titulo1+"','"+titulo2+"', '"+titulo3
					+"','"+ StringUtils.nullToString (visibilidad)+"','"+ StringUtils.nullToString (visibilidadEnt)+"','"
					+ StringUtils.nullToString (perfilInforme)+ "', '"+cadenaCodigosLupas+"', '"+ cuenta + "','" + propietario + "');").quote().close();
		        html.append("<img src=\"jsp/img/magnifier.png\" alt=\"Visualizar Informe\" title=\"Visualizar Informe\"/>");
	            html.aEnd();
	            html.append(ESPACIO);  

                return html.toString();
            }
		};
	}
	
	/**
	 * Devuelve el objeto que muestra la informacion de la columna 'Visibilidad'
	 * @return
	 */
	private CellEditor getCellEditorVisibilidad() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
		    	
				int visibilidad = getVisibilidadValue(item, rowcount, VISIBILIDAD);
				int visibilidadEnt = getVisibilidadValue(item, rowcount, VISIBILIDADENT);
		    	
		    	String value = "";		 
		    	// Visibilidad
		    	switch (visibilidad) {
		    		case ConstantsInf.COD_VISIBILIDAD_TODOS: value = ConstantsInf.VISIBILIDAD_TODOS; break;	
					case ConstantsInf.COD_VISIBILIDAD_PERFIL: value = ConstantsInf.VISIBILIDAD_PERFIL; break;										
					case ConstantsInf.COD_VISIBILIDAD_USUARIOS: value = ConstantsInf.VISIBILIDAD_USUARIOS; break;
					default: break;
				}
		    	// Visibilidad entidad
		    	if (visibilidadEnt == ConstantsInf.COD_VISIBILIDAD_ENTIDADES_SI) {
		    		value += (((value.length()>0) ? ", " : "") + ConstantsInf.VISIBILIDAD_ENTIDADES); 
				}
		    	
		        HtmlBuilder html = new HtmlBuilder();
		        html.append(value);
		        return html.toString();
		    }

		/**
		 * Devuelve el entero correspondiente a la columna 'campo' si no es nul
		 * @param item
		 * @param rowcount
		 * @param campo
		 * @return
		 */
		private int getVisibilidadValue(Object item, int rowcount, String campo) {
				int visibilidad = -1;
				try {
					Object obj = new BasicCellEditor().getValue(item, columnas.get(campo), rowcount);
					// Si el campo no es nulo se devuelve el entero
					if (obj != null) visibilidad = ((BigDecimal) obj).intValue();
				} catch (Exception e) {
					logger.error("MtoInformeService - Ocurrio un error al obtener el valor del campo " + campo + " del informe" , e);
				}
				return visibilidad;
			}
		};
	}
	
	/**
	 * Devuelve el objeto que muestra la informacion de la columna 'Cuenta'
	 * @return
	 */
	private CellEditor getCellEditorCuenta() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
		    	int cuenta = 0;
				try {
					cuenta = ((BigDecimal)new BasicCellEditor().getValue(item, columnas.get(CUENTA), rowcount)).intValue();
				} catch (Exception e) {
					logger.error("MtoInformeService - Ocurrio un error al obtener el campo Cuenta del informe" , e);
				}
		    	String value = cuenta == ConstantsInf.COD_CUENTA_NO ? "No" : "Si";		    
		        HtmlBuilder html = new HtmlBuilder();
		        html.append(value);
		        return html.toString();
		    }
		};
	}
	
    /**
	 * Configuracion de las columnas de la tabla
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table) {
		// 1 - Acciones
    	configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false, false, "1%");
    	// 2 - Nombre
    	configColumna(table, columnas.get(NOMBRE), "Nombre", true, true, "16%");
    	// 3 - Titulo1
    	configColumna(table, columnas.get(TITULO1), "Titulo 1", true, true, "12%");
    	// 4 - Titulo2
    	configColumna(table, columnas.get(TITULO2), "Titulo 2", true, true, "12%");
    	// 5 - Titulo3
    	configColumna(table, columnas.get(TITULO3), "Titulo 3", true, true, "12%");
    	// 6 - Visibilidad
    	configColumna(table, columnas.get(VISIBILIDAD), "Visibilidad", true, true, "6%");
    	// 7 - Cuenta
    	configColumna(table, columnas.get(CUENTA), "Cuenta", true, true, "6%");
    	// 7 - usuario
    	configColumna(table, columnas.get(USUARIO), "Usuario", true, true, "12%");
	}
    
	/**
	 * Configura la columna de la tabla identificada por 'idCol' con los valores pasados como parametro y los incluye en la tabla
	 * @param table Objeto que contiene la tabla
	 * @param idCol Id de la columna
	 * @param title TÃ­tulo de la columna
	 * @param filterable Indica si se podra buscar por esa columna
	 * @param sortable Indica si se podra ordenar por esa columna
	 * @param width Ancho de la columna
	 */
	private void configColumna (HtmlTable table, String idCol, String title, boolean filterable, boolean sortable, String width) {
		table.getRow().getColumn(idCol).setTitle(title);
        table.getRow().getColumn(idCol).setFilterable(filterable);
        table.getRow().getColumn(idCol).setSortable(sortable);
        table.getRow().getColumn(idCol).setWidth(width);
	}
	
	/**
	 * Crea y configura el objeto TableFacade que encapsulara la tabla de informes
	 * @param request
	 * @param response
	 * @return
	 */
	private TableFacade crearTableFacade (HttpServletRequest request, HttpServletResponse response, Informe informe, String origenLlamada) {
		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id, request);				
		//Carga las columnas a mostrar en el listado en el TableFacade y devuelve un Map con ellas
		cargarColumnas(tableFacade);
		
        tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.
        // Si no es una llamada a traves de ajax        
    	if (request.getParameter("ajax") == null){
    		if (origenLlamada == null || origenLlamada.equals("sesion")){
	    		if (request.getSession().getAttribute("mtoInforme_LIMIT") != null){
	    			//Si venimos por aquÃ­ es que ya hemos pasado por el filtro en algun momento
	    			tableFacade.setLimit((Limit) request.getSession().getAttribute("mtoInforme_LIMIT"));
	    		}
    		}else{
				// Carga en el TableFacade los filtros de busqueda introducidos en el formulario 
				cargarFiltrosBusqueda(columnas, informe, tableFacade);
    		}
    	}                
        return tableFacade;
		
	}
	
	/**
	 * Carga las columnas a mostrar en el listado en el TableFacade y devuelve un Map con ellas
	 * @param tableFacade
	 * @return
	 */
	@SuppressWarnings("all")
	private Map<String, String> cargarColumnas(TableFacade tableFacade) {
		// Crea el Map con las columnas del listado y los campos del filtro de busqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID_STR, "id");
			columnas.put(NOMBRE, "nombre");
			columnas.put(TITULO1, "titulo1");
			columnas.put(TITULO2, "titulo2");
			columnas.put(TITULO3, "titulo3");
			columnas.put(VISIBILIDAD, "visibilidad");
			columnas.put(VISIBILIDADENT, "visibilidadEnt");
			columnas.put(CUENTA, "cuenta");
			columnas.put(USUARIO, "usuario.codusuario");
			columnas.put(FECHA_ALTA, "fechaAlta");
		}
		tableFacade.setColumnProperties(columnas.get(ID_STR), columnas.get(NOMBRE), columnas.get(TITULO1), columnas.get(TITULO2),
				columnas.get(TITULO3), columnas.get(VISIBILIDAD), columnas.get(CUENTA),columnas.get(USUARIO)); 
        // Devuelve el mapa
        return columnas;
	}
	
	/**
	 * Carga en el TableFacade los filtros de busqueda introducidos en el formulario
	 * @param poliza
	 * @param tableFacade
	 */
		 
	private void cargarFiltrosBusqueda(Map<String,String> columnas, Informe informe, TableFacade tableFacade) {
		
		// Nombre
		if (FiltroUtils.noEstaVacio (informe.getNombre()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(NOMBRE), informe.getNombre()));
		// Titulo1
		if (FiltroUtils.noEstaVacio (informe.getTitulo1()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(TITULO1), informe.getTitulo1()));
		// Titulo2
		if (FiltroUtils.noEstaVacio (informe.getTitulo2()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(TITULO2), informe.getTitulo2()));
		// Titulo3
		if (FiltroUtils.noEstaVacio (informe.getTitulo3()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(TITULO3), informe.getTitulo3()));
		// Visibilidad
		if (FiltroUtils.noEstaVacio (informe.getVisibilidad()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(VISIBILIDAD), informe.getVisibilidad().toString()));
		// Visibilidad entidad
		if (FiltroUtils.noEstaVacio (informe.getVisibilidadEnt()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(VISIBILIDADENT), informe.getVisibilidadEnt().toString()));
		// Perfil
		if (FiltroUtils.noEstaVacio (informe.getPerfil()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter("perfil", informe.getPerfil().toString()));
		// Cuenta
		if (FiltroUtils.noEstaVacio (informe.getCuenta()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter("cuenta", informe.getCuenta().toString()));
		// codusuario
		if (FiltroUtils.noEstaVacio (informe.getUsuario().getCodusuario()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(USUARIO), informe.getUsuario().getCodusuario()));
	}
	
	/**
	 * Crea los objetos de filtro y ordenacion, llama al dao para obtener los datos de las polizas y carga el TableFacade con ellas
	 * @param tableFacade
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade, String cadenaCodigosLupas, final Usuario usuario){
		
		// Obtiene el Filter para la busqueda de informes
		Limit limit = tableFacade.getLimit();
		Collection<Informe> items = new ArrayList<>();
		// Crea el Sort para la busqueda de informes
        InformeSort informeSort = getInformeSort(limit);
		// Crea el Filter para la busqueda de informes
		InformeFilter informeFilter = getInformeFilter(limit, usuario);

        // Obtiene el numero de filas que cumplen el filtro        
        int totalRows = getInformeCountWithFilter(informeFilter, cadenaCodigosLupas);
        logger.debug("********** count filas de informes = "+totalRows+" **********");
        // y lo establecemos al tableFacade antes de obtener la fila de inicio y la de fin
        tableFacade.setTotalRows(totalRows);
        
        int rowStart = limit.getRowSelect().getRowStart();
        int rowEnd = limit.getRowSelect().getRowEnd();
		// Obtiene los registros que cumplen el filtro
        try {
			items = getInformeWithFilterAndSort(informeFilter, informeSort, rowStart, rowEnd, cadenaCodigosLupas);
			logger.debug("********** list items de informes = "+items.size()+" **********");
		} catch (BusinessException e) {
			logger.error("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos de la bd en la tabla
        tableFacade.setItems(items); 
    }
	
	/**
	 * Crea y configura el Filter para la consulta de informes
	 * @param limit
	 * @return
	 */
	private InformeFilter getInformeFilter(Limit limit, final Usuario usuario) {
		
		// Recorre el objeto Limit y carga los filtros de busqueda insertados en el formulario
		InformeFilter consultaFilter = new InformeFilter();
        FilterSet filterSet = limit.getFilterSet();
        Collection<Filter> filters = filterSet.getFilters();
        for (Filter filter : filters) {
            String property = filter.getProperty();
            String value = filter.getValue();
            consultaFilter.addFilter(property, value);
        }
        
        // Se anhaden las restricciones de visualizacion de informes dependiendo del usuario conectado
        // Si el usuario es perfil 1 o 5, se anhaden restricciones
        if (usuario != null) {
        	// Perfil 1 - Se mostraran los informes dados de alta por usuarios con perfil 1 y que pertenezcan 
        	// a la misma entidad que el usuario conectado
        	if (Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES.equals(usuario.getPerfil())) {
        		consultaFilter.addFilter("perfil1.codperfil", usuario.getTipousuario());
        		consultaFilter.addFilter("perfil1.codentidad", usuario.getOficina().getEntidad().getCodentidad());
        	}
        	// Perfil 5 - Se mostraran los informes dados de alta por usuarios que pertenezcan a entidades incluidas
        	// dentro del grupo de entidades del usuario conectado
        	else if (Constants.PERFIL_USUARIO_SEMIADMINISTRADOR.equals(usuario.getPerfil())) {
        		consultaFilter.addFilter("perfil5.entidades", usuario.getListaCodEntidadesGrupo());
        	}
        }
        //DAA 28/10/2013 Siempre mostramos los informes visibles 
        consultaFilter.addFilter("oculto", ConstantsInf.COD_VISIBILIDAD_TODOS);
        
        
        return consultaFilter;
	}
	
	/**
	 * Crea y configura el Sort para la consulta de Informes
	 * @param limit
	 * @return
	 */
	private InformeSort getInformeSort(Limit limit) {
		InformeSort consultaSort = new InformeSort();
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
	 * Metodo que genera la consulta del informe y la guarda en BBDD
	 * @param idInforme
	 * @return mapa con los datos del informe
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> generarConsultaInformeComisiones(Informe informe, final Usuario usuario, String consultaYaGenerada, InformeComisiones informeComisionesBean) throws BusinessException{
		logger.debug("init - generarConsultaInformeComisiones ");
		Map<String, Object> parameters = new HashMap<>();
		Map<String, List<String>> mCampo_Condiciones = new HashMap<>();
		Map<String, String> mSentidoPerm = new HashMap<>();
		Map<String, String> mSentidoCalc = new HashMap<>();
		List<String> tipo = new ArrayList<String>();
		List<String> cabeceras = new ArrayList<>();		
		List<String> cabecerasNombre = new ArrayList<>();
		List<String> lstTablas = new ArrayList<>();
		List<String> lstCampos = new ArrayList<>();
		List<String> lstTabCamp = new ArrayList<>();
		List<String> lstRupPerm = new ArrayList<>();
		List<String> lstRupCalc = new ArrayList<>();
		List<String> lstCondPerm = new ArrayList<>();
		List<String> lstCampCalc = new ArrayList<>();
		List<Boolean> totaliza = new ArrayList<>();
		String[] condArr,cCalcArr,cDivision = null;
		List<String> groupBy = new ArrayList<>();
		List<String> orderBy = new ArrayList<>();
		TreeSet<Vista> setVistas = new TreeSet<>();
		boolean orderGroupBy = false;
		
		try {
			String vista1 = "";
			String vista2 = "";
			String campo1 = "";
			String campo2 = "";
			String opAritmetico = ""; 
			String sql = "";
			List<Boolean> totalPorGrupo = new ArrayList<>();
			List<Boolean> ruptura = new ArrayList<>();
			List<BigDecimal> formato = new ArrayList<>();
			List<BigDecimal> decimales = new ArrayList<>();
			List<String> lstCondEspCalc = new ArrayList<>();
			if(informeComisionesBean != null ){
				if(("").equals(StringUtils.nullToString(informeComisionesBean.getCampoOrdenar())))
					informe = this.getInformeOculto(informe.getOculto());
			}else{
				informe = this.getInforme(informe.getId());
			}
			boolean informeParaRecibos = false;
			
			String filtroAsegBloq = "";
			
			// DAA 02/10/2013
			// Obtiene el nombre del esquema contra el que se lanzara la consulta en la generacion del informe
			// y evalua si esta es para Recibos de comisiones, en ese caso establece los campos que no hemos marcado en la jsp
			String esquema = "";
			if(informeComisionesBean != null){
				informeParaRecibos = true;
				esquema = ESQUEMA;
				if(("").equals(StringUtils.nullToString(informeComisionesBean.getCampoOrdenar()))){
					informe = setCamposParaInformecomisiones(informeComisionesBean, informe);
					informe = setFiltroParaInformeComisiones (informeComisionesBean, informe);
				}else{
						informe = setOrdenacionParaInformeComisiones (informeComisionesBean, informe);
				}
					
			}else{
				esquema = cargarEsquemaConsultaInforme();
			}

			if (informe != null && null != informe.getDatoInformeses() && informe.getDatoInformeses().size()>0){
				Set<DatoInformes> lstDatInf = informe.getDatoInformeses();
				//ordenamos la lista
				List<DatoInformes> lstDatOrdenada = ordenarDatosInforme(lstDatInf);
				
				for (DatoInformes datInf:lstDatOrdenada){
					
						CamposPermitidos camPer = datInf.getCamposPermitidos();
						CamposCalculados camCalc = datInf.getCamposCalculados();
						if (camPer !=null){ //CAMPO PERMITIDO
							VistaCampo visC = camPer.getVistaCampo();
							
							// Inserta la vista relacionada en el set
							setVistas.add(visC.getVista());
	
							if (!lstTablas.contains(visC.getVista().getNombreReal())){
								lstTablas.add(visC.getVista().getNombreReal());
							}
							
							if (!lstCampos.contains(visC.getNombreRealCampo()) && datInf.isVisible()){
								lstCampos.add(visC.getNombreRealCampo());
								if(informeComisionesBean !=null && informeComisionesBean.getDatosDe().equals(RESUMEN)){
									lstTabCamp.add(visC.getVista().getNombreReal()+"#"+visC.getNombreRealCampo()+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales()+"#"+datInf.getTotaliza());
								}else{
									lstTabCamp.add(visC.getVista().getNombreReal()+"#"+visC.getNombreRealCampo()+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales());
								}
								cabeceras.add(datInf.getAbreviado());
								cabecerasNombre.add(datInf.getCamposPermitidos().getVistaCampo().getNombre());
								tipo.add(camPer.getVistaCampo().getVistaCampoTipo().getIdtipo().toString());
								if (datInf.getTotaliza().compareTo(ConstantsInf.COD_BIG_TOTALIZA_SUMA) < 0){
									groupBy.add(datInf.getCamposPermitidos().getVistaCampo().getNombre());
								}	
							}

							List<String> lstPermCondiciones = new ArrayList<>();
							// condiciones permitidos
					    	for (CondicionCamposPermitidos cond:datInf.getCondicionCamposPermitidoses()){
					    		String value = "";
					    		value = getCondicion(cond.getOperadorCamposPermitido().getIdoperador().intValue(), cond.getCondicion(),datInf.getFormato(),camPer.getVistaCampo().getVistaCampoTipo().getIdtipo());
					    		
					    		if(informeParaRecibos){
					    			if (!lstPermCondiciones.contains(value) && (cond.getDatoInformes().getId()).equals(datInf.getId())){
							    		lstPermCondiciones.add(value);
							    	}
	
					    		}else{
					    			if (!lstPermCondiciones.contains(value) && cond.getDatoInformes().getCamposPermitidos().getId().toString().equals(camPer.getId().toString())){
							    		lstPermCondiciones.add(value);
							    	}
					    		}
					    	}
					    	
					    	lstCondPerm.add(camPer.getVistaCampo().getVista().getNombreReal()+"#"+camPer.getVistaCampo().getNombreRealCampo()+"#"+datInf.getFormato());
					    	mCampo_Condiciones.put(camPer.getVistaCampo().getVista().getNombreReal()+"#"+camPer.getVistaCampo().getNombreRealCampo()+"#"+datInf.getFormato(),lstPermCondiciones);
					    	
					    	// CLASIFIC Y RUPTURA CAMPOS PERMITIDOS
							String sentido = "";
							Set<ClasificacionRupturaCamposPermitidos> clasRupturaPerm = datInf.getClasificacionRupturaCamposPermitidoses();
							if (clasRupturaPerm != null && clasRupturaPerm.size()>0){
								for (ClasificacionRupturaCamposPermitidos clasRupPerm:clasRupturaPerm){
									if (clasRupturaPerm != null && !lstRupPerm.contains(visC.getVista().getNombreReal()+"#"+visC.getNombreRealCampo())){
										
										if (informeParaRecibos) {
											lstRupPerm.add(datInf.getAbreviado());
										}
										else {
											lstRupPerm.add(visC.getVista().getNombreReal()+"#"+visC.getNombreRealCampo());
										}
										// ordenacion
										if (clasRupPerm.getSentido().compareTo(ConstantsInf.COD_ORDENACION_ASC) == 0){
											sentido= "ASC";
											orderBy.add(datInf.getAbreviado());
										}else if (clasRupPerm.getSentido().compareTo(ConstantsInf.COD_ORDENACION_DESC) == 0){
											sentido= "DESC";
											orderBy.add(datInf.getAbreviado());
										}
										
										// ruptura
										if (informeParaRecibos) {
											mSentidoPerm.put(datInf.getAbreviado(),sentido);
										}
										else {
											mSentidoPerm.put(visC.getVista().getNombreReal()+"#"+visC.getNombreRealCampo(),sentido);
										}
										ruptura.add(clasRupPerm.getRuptura().compareTo(ConstantsInf.COD_RUPTURA_SI) == 0);										
									}
								}
							}else{
								ruptura.add(false);
							}
							// totaliza PERMITIDO
							if(datInf.isVisible()){
								totaliza.add(datInf.getTotaliza().compareTo(ConstantsInf.COD_BIG_TOTALIZA_NO) != 0);
							}	
							// total por Grupos PERMITIDO
							totalPorGrupo.add(datInf.getTotaliza().compareTo(ConstantsInf.COD_BIG_TOTALIZA_NO) != 0 && datInf.getTotalPorGrupo().compareTo(ConstantsInf.COD_BIG_TOTAL_POR_GRUPO_SI) == 0);

							// anhadimos el formato y decimales al campo permitido
							if (datInf.isVisible()) {
								formato.add(datInf.getFormato());
								decimales.add(datInf.getDecimales());
							}
							
							
						}
						else if (camCalc !=null){ // CAMPO CALCULADO
					    	vista1 = camCalc.getCamposPermitidosByIdoperando1().getVistaCampo().getVista().getNombreReal();
					    	vista2 = camCalc.getCamposPermitidosByIdoperando2().getVistaCampo().getVista().getNombreReal();
					    	campo1 = camCalc.getCamposPermitidosByIdoperando1().getVistaCampo().getNombreRealCampo();
					    	campo2 = camCalc.getCamposPermitidosByIdoperando2().getVistaCampo().getNombreRealCampo();
					    	opAritmetico = this.getOpAritmetico(camCalc.getIdoperador().intValue());
					    	if (!lstTablas.contains(vista1)){
								lstTablas.add(vista1);
							}
					    	if (!lstTablas.contains(vista2)){
								lstTablas.add(vista2);
							}
					    	
				    		// condiciones calculados
				    		if (datInf.getCondicionCamposCalculadoses() != null){
					    		for (CondicionCamposCalculados condCalc:datInf.getCondicionCamposCalculadoses()){
					    			lstCampCalc.add(vista1+"#"+campo1+"#"+opAritmetico+"#"+vista2+"#"+campo2+"#"+datInf.getAbreviado()+"#"+getCondicion(condCalc.getOperadorCamposCalculados().getIdoperador().intValue(),condCalc.getCondicion(),null,null));
					    			if (!lstTabCamp.contains(vista1+"#"+campo1+"#"+opAritmetico+"#"+vista2+"#"+campo2+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales())){
										lstTabCamp.add(vista1+"#"+campo1+"#"+opAritmetico+"#"+vista2+"#"+campo2+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales());
										cabeceras.add(datInf.getAbreviado());
										tipo.add(Integer.toString(ConstantsInf.CAMPO_TIPO_NUMERICO));
									}
					    		}
				    		}
					    	
					    	// CLASIFIC Y RUPTURA CALCULADOS
							String sentido = "";
							if (datInf.getClasificacionRupturaCamposCalculadoses() != null){
								Set<ClasificacionRupturaCamposCalculados> clasRupturaCalc = datInf.getClasificacionRupturaCamposCalculadoses();
								if (clasRupturaCalc != null && clasRupturaCalc.size()>0){
									for (ClasificacionRupturaCamposCalculados clasRupCalc:clasRupturaCalc){
										if (clasRupCalc != null && !lstRupCalc.contains(datInf.getAbreviado())){
											lstRupCalc.add(datInf.getAbreviado());
											// ordenacion
											if (clasRupCalc.getSentido().compareTo(ConstantsInf.COD_ORDENACION_ASC) == 0){
												sentido= "ASC";
											}else if (clasRupCalc.getSentido().compareTo(ConstantsInf.COD_ORDENACION_DESC) == 0){
												sentido= "DESC";
											}
											// ruptura
											if (clasRupCalc.getRuptura().compareTo(ConstantsInf.COD_RUPTURA_SI) == 0){
												ruptura.add(true);
											}else{
												ruptura.add(false);
											}
											
											mSentidoCalc.put(datInf.getAbreviado(),sentido);
										}
									}
								}else{
									ruptura.add(false);
								}
							}
							if (!lstTabCamp.contains(vista1+"#"+campo1+"#"+opAritmetico+"#"+vista2+"#"+campo2+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales())){
								lstTabCamp.add(vista1+"#"+campo1+"#"+opAritmetico+"#"+vista2+"#"+campo2+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales());
								cabeceras.add(datInf.getAbreviado());
								tipo.add(Integer.toString(ConstantsInf.CAMPO_TIPO_NUMERICO));
							}
							
							// condicion especial Division calculados
							if (opAritmetico.equals(ConstantsInf.OPERADOR_ARIT_DIV)){
								if (!lstCondEspCalc.contains(vista2+"#"+campo2)){
									lstCondEspCalc.add(vista2+"#"+campo2);
								}
							}
							
							// totaliza Calculados
							totaliza.add(false);  // de momento no se totaliza un campo calculado
							// totalPorGrupo Calculados
							totalPorGrupo.add(false); // lo mismo para el totalPorGrupo
							formato.add(datInf.getFormato());
							decimales.add(datInf.getDecimales());
						}
					
				} // FIN datosInformes
				
				// RELACIONAR TABLAS
				HashMap<String, Object> mapRelTablas = this.relacionarTablasInforme(lstTablas,false);
				List<RelVistaCampos> lstRelacionesTablas = (List)mapRelTablas.get(LISTA_RELACIONES_TABLAS);
				
				
				if (!("true").equals(StringUtils.nullToString(consultaYaGenerada))){
				
					//*********************
					//CONSTRUCCION CONSULTA
					//*********************
					boolean inicio = true;
					//si estoy ordenando y el formato es resumen y el campo por el que ordeno no esta en la agrupacion
					if(!orderBy.isEmpty() && (informeComisionesBean!= null && (RESUMEN).equals(informeComisionesBean.getDatosDe())) && !groupBy.contains(orderBy.get(0))){
						sql = QUERY_SELECT_ALL;
						orderGroupBy = true;
					}
					
					sql += QUERY_SELECT_DISTINCT;
					
					// CAMPOS
					for (String tabCamp:lstTabCamp){
						String[] campArray = tabCamp.split("#");
						if (campArray.length >5 && !informeComisionesBean.getDatosDe().equals(RESUMEN)){ // calculado
							sql += QUERY_TO_NUMBER+campArray[0] +"$." +campArray[1] +QUERY_0+campArray[2]+QUERY_NVL+campArray[3] +"$." +campArray[4]+QUERY_0_AS+campArray[5]+"\",";
						}else{ // permitido
							if (campArray[3].equals(Integer.toString(ConstantsInf.COD_FORMATO_FECHA_DDMMYYYY)) && !campArray[1].equals(FECCAR)){
								sql += QUERY_TO_CHAR+campArray[0] +"$." +campArray[1] +",'"+ConstantsInf.FORMATO_FECHA_DDMMYYYY+QUERY_AS+campArray[2]+"\",";
							}else if (campArray[3].equals(Integer.toString(ConstantsInf.COD_FORMATO_FECHA_YYYYMMDD)) && !campArray[1].equals(FECCAR)){
								sql += QUERY_TO_CHAR+campArray[0] +"$." +campArray[1] +",'"+ConstantsInf.FORMATO_FECHA_YYYYMMDD+QUERY_AS+campArray[2]+"\",";
							}else if (campArray[1].equals(FECCAR)) {
								sql += "TO_CHAR(TO_DATE("+campArray[0] +"$." +campArray[1] +",'DD/MM/YY'),'DD/MM/YYYY') AS \""+campArray[2]+"\",";
							}else if(informeComisionesBean != null && informeComisionesBean.getDatosDe().equals(RESUMEN) && campArray[5].equals(Integer.toString(ConstantsInf.COD_TOTALIZA_SUMA))){
								sql += "SUM("+campArray[0] +"$." +campArray[1] +") \""+campArray[2]+"\",";
							}else{
								if (campArray.length >5 && PRUEBA_FICHERO_COMISIONES.equals(informe.getNombre())) {
								    //sql += campArray[0] +"$." + campArray[1] +" "+ campArray[2] +" "+ campArray[3] +"$." + campArray[4] +" \""+campArray[5]+"\",";
								    sql += QUERY_TO_NUMBER+campArray[0] +"$." +campArray[1] +QUERY_0+campArray[2]+QUERY_NVL+campArray[3] +"$." +campArray[4]+QUERY_0_AS+campArray[5]+"\",";
								} else {
								    sql += campArray[0] +"$." +campArray[1] +" \""+campArray[2]+"\",";
								}
							}
						}
					}
					
					sql = deleteLastChar(sql);
					sql += QUERY_FROM;
					
					// TABLAS
					for (String tabla:lstTablas){
						sql += esquema + "." + tabla + " " + tabla +"$,";
					}
					
					// RELACIONES TABLAS
					if (lstRelacionesTablas.size() >0){
						for (RelVistaCampos relTablas:lstRelacionesTablas){
							vista1 = relTablas.getVistaByIdvista1().getNombreReal();
							vista2 = relTablas.getVistaByIdvista2().getNombreReal();
							campo1 = relTablas.getVistaCampoByIdcampo1().getNombreRealCampo();
							campo2 = relTablas.getVistaCampoByIdcampo2().getNombreRealCampo();
							sql = deleteLastChar(sql);
							if (inicio){
								/* Pet. 62719 ** MODIF TAM (01.02.2021) ** Inicio */
								/* Se incluyen los join y el filtro para no sacar en el informe polizas de asegurados
								 * que estén bloqueados */
								
								sql = deleteLastChar(sql);
								filtroAsegBloq = obtener_filtro_asegBloqueado(lstTablas, true);
								
								sql += filtroAsegBloq;
								/* Pet. 62719 ** MODIF TAM (01.02.2021) ** Fin */

								sql += QUERY_WHERE;
								inicio = false;
							}else{
								sql += QUERY_AND;
							}
							if (relTablas.getLeftJoin().compareTo(new BigDecimal(0)) == 0){
								sql += vista1+"$."+campo1+ " = " + vista2+"$."+campo2;
							}else{ // LEFT JOIN
								sql += vista1+"$."+campo1+ QUERY_CONCAT + vista2+"$."+campo2;
							}
						}
					}
					sql = deleteLastChar(sql);
					if (inicio){
						/* Pet. 62719 ** MODIF TAM (01.02.2021) ** Inicio */
						/* Se incluyen los join y el filtro para no sacar en el informe polizas de asegurados
						 * que estén bloqueados */
						
						sql = deleteLastChar(sql);
						filtroAsegBloq = obtener_filtro_asegBloqueado(lstTablas, true);
						
						sql += filtroAsegBloq;
						/* Pet. 62719 ** MODIF TAM (01.02.2021) ** Fin */

						sql += QUERY_WHERE_1;
					}
					// CONDICIONES PERMITIDOS
					for (String cond:lstCondPerm){
						condArr = cond.split("#");
						if (mCampo_Condiciones.get(cond) != null){
							for (String value:mCampo_Condiciones.get(cond)){
								if (!StringUtils.nullToString(condArr[2]).equals("") && (new BigDecimal(condArr[2]).compareTo(new BigDecimal(ConstantsInf.COD_FORMATO_FECHA_DDMMYYYY))== 0 ||
										new BigDecimal(condArr[2]).compareTo(new BigDecimal(ConstantsInf.COD_FORMATO_FECHA_YYYYMMDD))== 0)){
									sql += QUERY_AND_DATE+condArr[0] +"$." +condArr[1]+") " + value;
								}else if (condArr[1].equals(FECCAR)) {
									String[] fec = value.split("'");
									if (fec!=null) 
										if (fec[1]!=null)
											sql += QUERY_AND_DATE+condArr[0] +"$." +condArr[1]+",'DD/MM/YY') = TO_DATE('"+fec[1]+"','DD/MM/YY')";
									
								}else{
									sql += QUERY_AND+condArr[0] +"$." +condArr[1] + value;
								}
								
							}
						}
					}
					
					// CONDICIONES CALCULADOS
					for (String cCalc:lstCampCalc){
						cCalcArr = cCalc.split("#");
						sql += QUERY_AND+"("+cCalcArr[0] +"$." +cCalcArr[1] +" "+cCalcArr[2]+" "+cCalcArr[3] +"$." +cCalcArr[4]+")"+ cCalcArr[6];
					}
					
					// condicion especial division calculados
					for (String condCalc:lstCondEspCalc){
						cDivision = condCalc.split("#");
						sql += QUERY_AND+cDivision[0] +"$." +cDivision[1] +" <>0";
					}
					
					// Si el usuario no es perfil 0, se anhaden las anhaden las restricciones indicadas para cada perfil y origen de datos
					if (!Constants.PERFIL_USUARIO_ADMINISTRADOR.equals(usuario.getPerfil())) {
						sql += addRestriccionesPorPerfilYTabla(usuario, setVistas);
					}
					
					/* Pet. 62719 ** MODIF TAM (01.02.2021) ** Inicio */
					if (!filtroAsegBloq.equals("")) {
						sql = deleteLastChar(sql);
						
						filtroAsegBloq = obtener_filtro_asegBloqueado(lstTablas, false);
						
						sql += filtroAsegBloq;
					}
					/* Pet. 62719 ** MODIF TAM (01.02.2021) ** Fin */	
					
					if(informeComisionesBean != null && informeComisionesBean.getDatosDe().equals(RESUMEN) && !groupBy.isEmpty()){
						sql += QUERY_GROUP;
						for(int i=0; i<groupBy.size(); i++){
							sql += groupBy.get(i);
							if(i<groupBy.size()-1){
								sql+=", ";
							}
						}
					}
					
					// SENTIDO PERMITIDOS
					inicio = true;
					for (String clasRup:lstRupPerm){
						String[] clasRupArray = clasRup.split("#");
						if (mSentidoPerm.get(clasRup) != null){
							if (inicio){
								if(orderGroupBy){
									sql += ")";
								}	
								sql += QUERY_ORDER;
								inicio = false;
							}
							
							if (informeParaRecibos) {
								sql += "\"" + clasRupArray[0] + "\" " + mSentidoPerm.get(clasRup) + ",";
							}
							else {
								if(orderGroupBy){
									sql += clasRupArray[1] +" "+ mSentidoPerm.get(clasRup) +",";
								}else{
									sql += clasRupArray[0] +"$." +clasRupArray[1] +" "+ mSentidoPerm.get(clasRup) +",";
								}	
							}
						}
					}
					
					// SENTIDO CALCULADOS
					for (String cCalc:lstRupCalc){
						if (mSentidoCalc.get(cCalc) != null){
							if (inicio){
								sql += QUERY_ORDER;
								inicio = false;
							}
							sql += "\""+cCalc+ "\" "+ mSentidoCalc.get(cCalc) +",";
						}
					}
					
					sql = deleteLastChar(sql);
					
					try {
						mtoInformeDao.actualizaConsultaInforme(sql, informe.getId());
					} catch (DAOException e) {
						logger.error(" Se ha producido un error al actualizar la consulta del informe,  " + e);
						throw new BusinessException(ACTUALIZAR_SQL, e);
					}
					if(!informeParaRecibos || (informeParaRecibos && (Integer.parseInt(informeComisionesBean.getFormato()) == ConstantsInf.COD_FORMATO_TXT))){
						//DAA 20/02/2013  Calculamos el numero de registros para evaluar el maximo permitido
						int numRegistros = getCountNumRegistros(sql);
						parameters.put(NUM_REGISTROS,numRegistros);
						logger.info(NUM_REGISTROS_EQUALS + numRegistros);
					}
				//si se ha generado previamente la consulta la obtengo del objeto informe guardado previamente	
				}else{
					try {
						sql = StringUtils.convertStreamToString(informe.getConsulta().getBinaryStream());
					} catch (SQLException e) {
						logger.error(" Se ha producido un error al recuperar la consulta del informe,  ", e);
					}
				}
				
				// pasamos los datos del informe a un mapa
				parameters.put(INFORME, informe);
				parameters.put(CABECERAS,cabeceras);
				parameters.put(CABECERAS_NOMBRE,cabecerasNombre);
				parameters.put("tipo",tipo);
				parameters.put(TOTALIZA, totaliza);
				parameters.put(TOTAL_POR_GRUPO, totalPorGrupo);
				parameters.put(RUPTURA, ruptura);
				parameters.put(FORMATO, formato);
				parameters.put(DECIMALES, decimales);
				parameters.put("sql",sql);
				
				logger.info("************SQL*************** ");
				logger.info(sql);
				logger.info("****************************** ");
				logger.debug("end - generarConsultaInforme ");
				return parameters;
			}else{ // sin datos en el informe
				logger.info(" Sin datos en el informe");
				logger.debug(" end - generarConsultaInformeComisiones");
				throw new BusinessException(SIN_DATOS_INFORME);
				//return parameters;
			}
				
		} catch (BusinessException e) {
			logger.error("Se ha producido un error al crear la consulta del informe,  " + e);
			if (e.getMessage().equals(SIN_DATOS_INFORME)){
				throw new BusinessException(" El informe no tiene DatosInforme", e);
			}else if (e.getMessage().equals(ACTUALIZAR_SQL)){
				throw new BusinessException("Se ha producido un error al actualizar la consulta del informe en  BBDD", e);
			}else{
				throw new BusinessException("Se ha producido un error al crear la consulta del informe: ", e);
			}
		}
	}
	

	/**
	 * Método que genera la consulta del informe y la guarda en BBDD (versión 2015+)
	 * @param informe
	 * @param usuario
	 * @param consultaYaGenerada
	 * @param informeComisionesBean
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HashMap<String, Object> generarConsultaInformeComisiones2015(Informe informe, final Usuario usuario, String consultaYaGenerada,
			InformeComisiones2015 informeComisionesBean) throws BusinessException{
		
		logger.debug(" init - generarConsultaInformeComisiones2015");
		HashMap<String, Object> parameters = new HashMap<>();
		Map<String, List<String>> mCampo_Condiciones = new HashMap<>();
		Map<String, String> mSentidoPerm = new HashMap<>();
		Map<String, String> mSentidoCalc = new HashMap<>();
		List<String> tipo = new ArrayList<>();
		List<String> cabeceras = new ArrayList<>();		
		List<String> cabecerasNombre = new ArrayList<>();
		List<String> lstTablas = new ArrayList<>();
		List<String> lstCampos = new ArrayList<>();
		List<String> lstTabCamp = new ArrayList<>();
		List<String> lstRupPerm = new ArrayList<>();
		List<String> lstRupCalc = new ArrayList<>();
		List<String> lstCondPerm = new ArrayList<>();
		List<String> lstCampCalc = new ArrayList<>();
		List<Boolean> totaliza = new ArrayList<>();
		String[] condArr,cCalcArr,cDivision = null;
		List<String> groupBy = new ArrayList<>();
		List<String> orderBy = new ArrayList<>();
		TreeSet<Vista> setVistas = new TreeSet<>();
		boolean orderGroupBy = false;
		
		try {
			String vista1 = "";
			String vista2 = "";
			String campo1 = "";
			String campo2 = "";
			String opAritmetico = ""; 
			String sql = "";
			List<Boolean> totalPorGrupo = new ArrayList<>();
			List<Boolean> ruptura = new ArrayList<>();
			List<BigDecimal> formato = new ArrayList<>();
			List<BigDecimal> decimales = new ArrayList<>();
			List<String> lstCondEspCalc = new ArrayList<>();
			if(informeComisionesBean != null ){
				if(("").equals(StringUtils.nullToString(informeComisionesBean.getCampoOrdenar())))
					informe = this.getInformeOculto(informe.getOculto());
			}else{
				informe = this.getInforme(informe.getId());
			}
			boolean informeParaRecibos = false;
			
			// DAA 02/10/2013
			// Obtiene el nombre del esquema contra el que se lanzara la consulta en la generacion del informe
			// y evalua si esta es para Recibos de comisiones, en ese caso establece los campos que no hemos marcado en la jsp
			String esquema = "";
			if(informeComisionesBean != null){
				informeParaRecibos = true;
				esquema = ESQUEMA;
				if(("").equals(StringUtils.nullToString(informeComisionesBean.getCampoOrdenar()))){
					informe = setCamposParaInformeComisiones2015(informeComisionesBean, informe);
					informe = setFiltroParaInformeComisiones2015 (informeComisionesBean, informe);
				}else{
						informe = setOrdenacionParaInformeComisiones2015 (informeComisionesBean, informe);
				}
					
			}else{
				esquema = cargarEsquemaConsultaInforme();
			}

			if (informe != null && null != informe.getDatoInformeses() && informe.getDatoInformeses().size()>0){
				Set<DatoInformes> lstDatInf = informe.getDatoInformeses();
				//ordenamos la lista
				List<DatoInformes> lstDatOrdenada = ordenarDatosInforme(lstDatInf);
				
				for (DatoInformes datInf:lstDatOrdenada){
					
						CamposPermitidos camPer = datInf.getCamposPermitidos();
						CamposCalculados camCalc = datInf.getCamposCalculados();
						if (camPer !=null){ //CAMPO PERMITIDO
							VistaCampo visC = camPer.getVistaCampo();
							
							// Inserta la vista relacionada en el set
							setVistas.add(visC.getVista());
	
							if (!lstTablas.contains(visC.getVista().getNombreReal())){
								lstTablas.add(visC.getVista().getNombreReal());
							}
							
							if (!lstCampos.contains(visC.getNombreRealCampo()) && datInf.isVisible()){
								lstCampos.add(visC.getNombreRealCampo());
								if(informeComisionesBean !=null && informeComisionesBean.getDatosDe().equals(RESUMEN)){
									lstTabCamp.add(visC.getVista().getNombreReal()+"#"+visC.getNombreRealCampo()+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales()+"#"+datInf.getTotaliza());
								}else{
									lstTabCamp.add(visC.getVista().getNombreReal()+"#"+visC.getNombreRealCampo()+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales());
								}
								cabeceras.add(datInf.getAbreviado());
								cabecerasNombre.add(datInf.getCamposPermitidos().getVistaCampo().getNombre());
								tipo.add(camPer.getVistaCampo().getVistaCampoTipo().getIdtipo().toString());
								if (datInf.getTotaliza().compareTo(ConstantsInf.COD_BIG_TOTALIZA_SUMA) < 0){
									groupBy.add(datInf.getCamposPermitidos().getVistaCampo().getNombre());
								}	
							}

							List<String> lstPermCondiciones = new ArrayList<String>();
							// condiciones permitidos
					    	for (CondicionCamposPermitidos cond:datInf.getCondicionCamposPermitidoses()){
					    		String value = "";
					    		value = getCondicion(cond.getOperadorCamposPermitido().getIdoperador().intValue(), cond.getCondicion(),datInf.getFormato(),camPer.getVistaCampo().getVistaCampoTipo().getIdtipo());
					    		
					    		if(informeParaRecibos){
					    			if (!lstPermCondiciones.contains(value) && (cond.getDatoInformes().getId()).equals(datInf.getId())){
							    		lstPermCondiciones.add(value);
							    	}
	
					    		}else{
					    			if (!lstPermCondiciones.contains(value) && cond.getDatoInformes().getCamposPermitidos().getId().toString().equals(camPer.getId().toString())){
							    		lstPermCondiciones.add(value);
							    	}
					    		}
					    	}
					    	
					    	lstCondPerm.add(camPer.getVistaCampo().getVista().getNombreReal()+"#"+camPer.getVistaCampo().getNombreRealCampo()+"#"+datInf.getFormato());
					    	mCampo_Condiciones.put(camPer.getVistaCampo().getVista().getNombreReal()+"#"+camPer.getVistaCampo().getNombreRealCampo()+"#"+datInf.getFormato(),lstPermCondiciones);
					    	
					    	// CLASIFIC Y RUPTURA CAMPOS PERMITIDOS
							String sentido = "";
							Set<ClasificacionRupturaCamposPermitidos> clasRupturaPerm = datInf.getClasificacionRupturaCamposPermitidoses();
							if (clasRupturaPerm != null && clasRupturaPerm.size()>0){
								for (ClasificacionRupturaCamposPermitidos clasRupPerm:clasRupturaPerm){
									if (clasRupturaPerm != null && !lstRupPerm.contains(visC.getVista().getNombreReal()+"#"+visC.getNombreRealCampo())){
										
										if (informeParaRecibos) {
											lstRupPerm.add(datInf.getAbreviado());
										}
										else {
											lstRupPerm.add(visC.getVista().getNombreReal()+"#"+visC.getNombreRealCampo());
										}
										// ordenacion
										if (clasRupPerm.getSentido().compareTo(ConstantsInf.COD_ORDENACION_ASC) == 0){
											sentido= "ASC";
											orderBy.add(datInf.getAbreviado());
										}else if (clasRupPerm.getSentido().compareTo(ConstantsInf.COD_ORDENACION_DESC) == 0){
											sentido= "DESC";
											orderBy.add(datInf.getAbreviado());
										}										
										// ruptura
										if (informeParaRecibos) {
											mSentidoPerm.put(datInf.getAbreviado(),sentido);
										}
										else {
											mSentidoPerm.put(visC.getVista().getNombreReal()+"#"+visC.getNombreRealCampo(),sentido);
										}
										ruptura.add(clasRupPerm.getRuptura().compareTo(ConstantsInf.COD_RUPTURA_SI) == 0);
									}
								}
							}else{
								ruptura.add(false);
							}
							// totaliza PERMITIDO
							if(datInf.isVisible()){
								totaliza.add(datInf.getTotaliza().compareTo(ConstantsInf.COD_BIG_TOTALIZA_NO) != 0);
							}	
							// total por Grupos PERMITIDO
							totalPorGrupo.add(datInf.getTotaliza().compareTo(ConstantsInf.COD_BIG_TOTALIZA_NO) != 0 && datInf.getTotalPorGrupo().compareTo(ConstantsInf.COD_BIG_TOTAL_POR_GRUPO_SI) == 0);
							// anhadimos el formato y decimales al campo permitido
							if (datInf.isVisible()) {
								formato.add(datInf.getFormato());
								decimales.add(datInf.getDecimales());
							}
							
							
						}
						else if (camCalc !=null){ // CAMPO CALCULADO
					    	vista1 = camCalc.getCamposPermitidosByIdoperando1().getVistaCampo().getVista().getNombreReal();
					    	vista2 = camCalc.getCamposPermitidosByIdoperando2().getVistaCampo().getVista().getNombreReal();
					    	campo1 = camCalc.getCamposPermitidosByIdoperando1().getVistaCampo().getNombreRealCampo();
					    	campo2 = camCalc.getCamposPermitidosByIdoperando2().getVistaCampo().getNombreRealCampo();
					    	opAritmetico = this.getOpAritmetico(camCalc.getIdoperador().intValue());
					    	if (!lstTablas.contains(vista1)){
								lstTablas.add(vista1);
							}
					    	if (!lstTablas.contains(vista2)){
								lstTablas.add(vista2);
							}
					    	
				    		// condiciones calculados
				    		if (datInf.getCondicionCamposCalculadoses() != null){
					    		for (CondicionCamposCalculados condCalc:datInf.getCondicionCamposCalculadoses()){
					    			lstCampCalc.add(vista1+"#"+campo1+"#"+opAritmetico+"#"+vista2+"#"+campo2+"#"+datInf.getAbreviado()+"#"+getCondicion(condCalc.getOperadorCamposCalculados().getIdoperador().intValue(),condCalc.getCondicion(),null,null));
					    			if (!lstTabCamp.contains(vista1+"#"+campo1+"#"+opAritmetico+"#"+vista2+"#"+campo2+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales())){
										lstTabCamp.add(vista1+"#"+campo1+"#"+opAritmetico+"#"+vista2+"#"+campo2+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales());
										cabeceras.add(datInf.getAbreviado());
										tipo.add(Integer.toString(ConstantsInf.CAMPO_TIPO_NUMERICO));
									}
					    		}
				    		}
					    	
					    	// CLASIFIC Y RUPTURA CALCULADOS
							String sentido = "";
							if (datInf.getClasificacionRupturaCamposCalculadoses() != null){
								Set<ClasificacionRupturaCamposCalculados> clasRupturaCalc = datInf.getClasificacionRupturaCamposCalculadoses();
								if (clasRupturaCalc != null && clasRupturaCalc.size()>0){
									for (ClasificacionRupturaCamposCalculados clasRupCalc:clasRupturaCalc){
										if (clasRupCalc != null && !lstRupCalc.contains(datInf.getAbreviado())){
											lstRupCalc.add(datInf.getAbreviado());
											// ordenacion
											if (clasRupCalc.getSentido().compareTo(ConstantsInf.COD_ORDENACION_ASC) == 0){
												sentido= "ASC";
											}else if (clasRupCalc.getSentido().compareTo(ConstantsInf.COD_ORDENACION_DESC) == 0){
												sentido= "DESC";
											}
											// ruptura
											if (clasRupCalc.getRuptura().compareTo(ConstantsInf.COD_RUPTURA_SI) == 0){
												ruptura.add(true);
											}else{
												ruptura.add(false);
											}
											
											mSentidoCalc.put(datInf.getAbreviado(),sentido);
										}
									}
								}else{
									ruptura.add(false);
								}
							}
							if (!lstTabCamp.contains(vista1+"#"+campo1+"#"+opAritmetico+"#"+vista2+"#"+campo2+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales())){
								lstTabCamp.add(vista1+"#"+campo1+"#"+opAritmetico+"#"+vista2+"#"+campo2+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales());
								cabeceras.add(datInf.getAbreviado());
								tipo.add(Integer.toString(ConstantsInf.CAMPO_TIPO_NUMERICO));
							}
							
							// condicion especial Division calculados
							if (opAritmetico.equals(ConstantsInf.OPERADOR_ARIT_DIV) && !lstCondEspCalc.contains(vista2+"#"+campo2)){
								lstCondEspCalc.add(vista2+"#"+campo2);
							}
							
							// totaliza Calculados
							totaliza.add(false);  // de momento no se totaliza un campo calculado
							// totalPorGrupo Calculados
							totalPorGrupo.add(false); // lo mismo para el totalPorGrupo
							formato.add(datInf.getFormato());
							decimales.add(datInf.getDecimales());
						}
					
				} // FIN datosInformes
				
				// RELACIONAR TABLAS
				HashMap<String, Object> mapRelTablas = this.relacionarTablasInforme(lstTablas,false);
				List<RelVistaCampos> lstRelacionesTablas = (List)mapRelTablas.get(LISTA_RELACIONES_TABLAS);
				
				
				if (!("true").equals(StringUtils.nullToString(consultaYaGenerada))){
				
					//*********************
					//CONSTRUCCION CONSULTA
					//*********************
					boolean inicio = true;
					//si estoy ordenando y el formato es resumen y el campo por el que ordeno no esta en la agrupacion
					if(!orderBy.isEmpty() && (informeComisionesBean!= null && (RESUMEN).equals(informeComisionesBean.getDatosDe())) && !groupBy.contains(orderBy.get(0))){
						sql = QUERY_SELECT_ALL;
						orderGroupBy = true;
					}
					
					sql += QUERY_SELECT;
					
					// CAMPOS
					for (String tabCamp:lstTabCamp){
						String[] campArray = tabCamp.split("#");
						if (campArray.length >5 && !informeComisionesBean.getDatosDe().equals(RESUMEN)){ // calculado
							sql += QUERY_TO_NUMBER+campArray[0] +"$." +campArray[1] +QUERY_0+campArray[2]+QUERY_NVL+campArray[3] +"$." +campArray[4]+QUERY_0_AS+campArray[5]+"\",";
						}else{ // permitido
							if (campArray[3].equals(Integer.toString(ConstantsInf.COD_FORMATO_FECHA_DDMMYYYY)) && !campArray[1].equals(FECCAR)){
								sql += QUERY_TO_CHAR+campArray[0] +"$." +campArray[1] +",'"+ConstantsInf.FORMATO_FECHA_DDMMYYYY+QUERY_AS+campArray[2]+"\",";
							}else if (campArray[3].equals(Integer.toString(ConstantsInf.COD_FORMATO_FECHA_YYYYMMDD)) && !campArray[1].equals(FECCAR)){
								sql += QUERY_TO_CHAR+campArray[0] +"$." +campArray[1] +",'"+ConstantsInf.FORMATO_FECHA_YYYYMMDD+QUERY_AS+campArray[2]+"\",";
							}else if (campArray[1].equals(FECCAR)) {
								sql += "TO_CHAR(TO_DATE("+campArray[0] +"$." +campArray[1] +",'DD/MM/YY'),'DD/MM/YYYY') AS \""+campArray[2]+"\",";
							}else if(informeComisionesBean != null && informeComisionesBean.getDatosDe().equals(RESUMEN) && campArray[5].equals(Integer.toString(ConstantsInf.COD_TOTALIZA_SUMA))){
								sql += "SUM("+campArray[0] +"$." +campArray[1] +") \""+campArray[2]+"\",";
							}else{
								if (campArray.length >5 && PRUEBA_FICHERO_COMISIONES.equals(informe.getNombre())) {
								    //sql += campArray[0] +"$." + campArray[1] +" "+ campArray[2] +" "+ campArray[3] +"$." + campArray[4] +" \""+campArray[5]+"\",";
								    sql += QUERY_TO_NUMBER+campArray[0] +"$." +campArray[1] +QUERY_0+campArray[2]+QUERY_NVL+campArray[3] +"$." +campArray[4]+QUERY_0_AS+campArray[5]+"\",";
								} else {
								    sql += campArray[0] +"$." +campArray[1] +" \""+campArray[2]+"\",";
								}
							}
						}
					}
					
					sql = deleteLastChar(sql);
					sql += QUERY_FROM;
					
					// TABLAS
					for (String tabla:lstTablas){
						sql += esquema + "." + tabla + " " + tabla +"$,";
					}
					
					// RELACIONES TABLAS
					if (lstRelacionesTablas.size() >0){
						for (RelVistaCampos relTablas:lstRelacionesTablas){
							vista1 = relTablas.getVistaByIdvista1().getNombreReal();
							vista2 = relTablas.getVistaByIdvista2().getNombreReal();
							campo1 = relTablas.getVistaCampoByIdcampo1().getNombreRealCampo();
							campo2 = relTablas.getVistaCampoByIdcampo2().getNombreRealCampo();
							sql = deleteLastChar(sql);
							if (inicio){
								sql += QUERY_WHERE;
								inicio = false;
							}else{
								sql += QUERY_AND;
							}
							if (relTablas.getLeftJoin().compareTo(new BigDecimal(0)) == 0){
								sql += vista1+"$."+campo1+ " = " + vista2+"$."+campo2;
							}else{ // LEFT JOIN
								sql += vista1+"$."+campo1+ QUERY_CONCAT + vista2+"$."+campo2;
							}
						}
					}
					sql = deleteLastChar(sql);
					if (inicio){
						sql += QUERY_WHERE_1;
					}
					// CONDICIONES PERMITIDOS
					for (String cond:lstCondPerm){
						condArr = cond.split("#");
						if (mCampo_Condiciones.get(cond) != null){
							for (String value:mCampo_Condiciones.get(cond)){
								if (!StringUtils.nullToString(condArr[2]).equals("") && (new BigDecimal(condArr[2]).compareTo(new BigDecimal(ConstantsInf.COD_FORMATO_FECHA_DDMMYYYY))== 0 ||
										new BigDecimal(condArr[2]).compareTo(new BigDecimal(ConstantsInf.COD_FORMATO_FECHA_YYYYMMDD))== 0)){
									sql += QUERY_AND_DATE+condArr[0] +"$." +condArr[1]+") " + value;
								}else if (condArr[1].equals(FECCAR)) {
									String[] fec = value.split("'");
									if (fec!=null) 
										if (fec[1]!=null)
											sql += QUERY_AND_DATE+condArr[0] +"$." +condArr[1]+",'DD/MM/YY') = TO_DATE('"+fec[1]+"','DD/MM/YY')";
									
								}else{
									sql += QUERY_AND+condArr[0] +"$." +condArr[1] + value;
								}
								
							}
						}
					}
					
					// CONDICIONES CALCULADOS
					for (String cCalc:lstCampCalc){
						cCalcArr = cCalc.split("#");
						sql += QUERY_AND+"("+cCalcArr[0] +"$." +cCalcArr[1] +" "+cCalcArr[2]+" "+cCalcArr[3] +"$." +cCalcArr[4]+")"+ cCalcArr[6];
					}
					
					// condicion especial division calculados
					for (String condCalc:lstCondEspCalc){
						cDivision = condCalc.split("#");
						sql += QUERY_AND+cDivision[0] +"$." +cDivision[1] +" <>0";
					}
					
					// Si el usuario no es perfil 0, se anhaden las anhaden las restricciones indicadas para cada perfil y origen de datos
					if (!Constants.PERFIL_USUARIO_ADMINISTRADOR.equals(usuario.getPerfil())) {
						sql += addRestriccionesPorPerfilYTabla(usuario, setVistas);
					}
					
					if(informeComisionesBean != null && informeComisionesBean.getDatosDe().equals(RESUMEN) && !groupBy.isEmpty()){
						sql += QUERY_GROUP;
						for(int i=0; i<groupBy.size(); i++){
							sql += groupBy.get(i);
							if(i<groupBy.size()-1){
								sql+=", ";
							}
						}
					}
					
					// SENTIDO PERMITIDOS
					inicio = true;
					for (String clasRup:lstRupPerm){
						String[] clasRupArray = clasRup.split("#");
						if (mSentidoPerm.get(clasRup) != null){
							if (inicio){
								if(orderGroupBy){
									sql += ")";
								}	
								sql += QUERY_ORDER;
								inicio = false;
							}
							
							if (informeParaRecibos) {
								sql += "\"" + clasRupArray[0] + "\" " + mSentidoPerm.get(clasRup) + ",";
							}
							else {
								if(orderGroupBy){
									sql += clasRupArray[1] +" "+ mSentidoPerm.get(clasRup) +",";
								}else{
									sql += clasRupArray[0] +"$." +clasRupArray[1] +" "+ mSentidoPerm.get(clasRup) +",";
								}	
							}
						}
					}
					
					// SENTIDO CALCULADOS
					for (String cCalc:lstRupCalc){
						if (mSentidoCalc.get(cCalc) != null){
							if (inicio){
								sql += QUERY_ORDER;
								inicio = false;
							}
							sql += "\""+cCalc+ "\" "+ mSentidoCalc.get(cCalc) +",";
						}
					}
					
					sql = deleteLastChar(sql);
					
					try {
						mtoInformeDao.actualizaConsultaInforme(sql, informe.getId());
					} catch (DAOException e) {
						logger.error("  Se ha producido un error al actualizar la consulta del informe, " + e);
						throw new BusinessException(ACTUALIZAR_SQL, e);
					}
					if(!informeParaRecibos || (informeParaRecibos && (Integer.parseInt(informeComisionesBean.getFormato()) == ConstantsInf.COD_FORMATO_TXT))){
						//DAA 20/02/2013  Calculamos el numero de registros para evaluar el maximo permitido
						int numRegistros = getCountNumRegistros(sql);
						parameters.put(NUM_REGISTROS,numRegistros);
						logger.info(NUM_REGISTROS_EQUALS + numRegistros);
					}
				//si se ha generado previamente la consulta la obtengo del objeto informe guardado previamente	
				}else{
					try {
						sql = StringUtils.convertStreamToString(informe.getConsulta().getBinaryStream());
					} catch (SQLException e) {
						logger.error("  Se ha producido un error al recuperar la consulta del informe, ", e);
					}
				}
				
				// pasamos los datos del informe a un mapa
				parameters.put(INFORME, informe);
				parameters.put(CABECERAS,cabeceras);
				parameters.put(CABECERAS_NOMBRE,cabecerasNombre);
				parameters.put("tipo",tipo);
				parameters.put(TOTALIZA, totaliza);
				parameters.put(TOTAL_POR_GRUPO, totalPorGrupo);
				parameters.put(RUPTURA, ruptura);
				parameters.put(FORMATO, formato);
				parameters.put(DECIMALES, decimales);
				parameters.put("sql",sql);
				
				logger.info(" ************SQL***************");
				logger.info(sql);
				logger.info(" ******************************");
				logger.debug("end - generarConsultaInformeComisiones2015");
				return parameters;
			}else{ // sin datos en el informe
				logger.info("Sin datos en el  informe");
				logger.debug("end - generarConsultaInformeComisiones2015");
				throw new BusinessException(SIN_DATOS_INFORME);
				//return parameters;
			}
				
		} catch (BusinessException e) {
			logger.error(" Se ha producido un error al crear la consulta del informe, " + e);
			if (e.getMessage().equals(SIN_DATOS_INFORME)){
				throw new BusinessException("El informe no tiene  DatosInforme", e);
			}else if (e.getMessage().equals(ACTUALIZAR_SQL)){
				throw new BusinessException("Se ha producido un error al actualizar la consulta del informe en BBDD", e);
			}else{
				throw new BusinessException("Se ha producido un error al crear la consulta del informe:  ", e);
			}
		}
	}
	
	/**
	 * Metodo que genera la consulta del informe y la guarda en BBDD
	 * @param idInforme
	 * @return mapa con los datos del informe
	 */
	@SuppressWarnings("rawtypes")
	public HashMap<String, Object> generarConsultaInforme(Informe informe, final Usuario usuario, String consultaYaGenerada, InformeRecibos informeRecibos) throws BusinessException{
		logger.debug("init - generarConsultaInforme");
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		Map<String, List<String>> mCampo_Condiciones = new HashMap<String, List<String>>();
		Map<String, String> mSentidoPerm = new HashMap<String, String>();
		Map<String, String> mSentidoCalc = new HashMap<String, String>();
		List<String> tipo = new ArrayList<String>();
		List<String> cabeceras = new ArrayList<String>();		
		List<String> cabecerasNombre = new ArrayList<String>();
		List<String> lstTablas = new ArrayList<String>();
		List<String> lstCampos = new ArrayList<String>();
		List<String> lstTabCamp = new ArrayList<String>();
		List<String> lstRupPerm = new ArrayList<String>();
		List<String> lstRupCalc = new ArrayList<String>();
		List<String> lstCondPerm = new ArrayList<String>();
		List<String> lstCampCalc = new ArrayList<String>();
		List<Boolean> totaliza = new ArrayList<Boolean>();
		String[] condArr,cCalcArr,cDivision = null;
		List<String> groupBy = new ArrayList<String>();
		List<String> orderBy = new ArrayList<String>();
		TreeSet<Vista> setVistas = new TreeSet<Vista>();
		boolean orderGroupBy = false;
		
		try {
			String vista1 = "";
			String vista2 = "";
			String campo1 = "";
			String campo2 = "";
			String opAritmetico = ""; 
			String sql = "";
			List<Boolean> totalPorGrupo = new ArrayList<Boolean>();
			List<Boolean> ruptura = new ArrayList<Boolean>();
			List<BigDecimal> formato = new ArrayList<BigDecimal>();
			List<BigDecimal> decimales = new ArrayList<BigDecimal>();
			List<String> lstCondEspCalc = new ArrayList<String>();
			if(informeRecibos != null ){
				if(("").equals(StringUtils.nullToString(informeRecibos.getCampoOrdenar())))
					informe = this.getInformeOculto(informe.getOculto());
			}else{
				informe = this.getInforme(informe.getId());
			}
			boolean informeParaRecibos = false;
			
			// DAA 02/10/2013
			// Obtiene el nombre del esquema contra el que se lanzara la consulta en la generacion del informe
			// y evalua si esta es para Recibos de comisiones, en ese caso establece los campos que no hemos marcado en la jsp
			String esquema = "";
			if(informeRecibos != null){
				informeParaRecibos = true;
				esquema = ESQUEMA;
				if(("").equals(StringUtils.nullToString(informeRecibos.getCampoOrdenar()))){
					informe = setCamposParaInformeRecibos(informeRecibos, informe);
					informe = setFiltroParaInformeRecibos (informeRecibos, informe);
				}else{
						informe = setOrdenacionParaInformeRecibos (informeRecibos, informe);
				}
					
			}else{
				esquema = cargarEsquemaConsultaInforme();
			}

			if (informe != null && null != informe.getDatoInformeses() && informe.getDatoInformeses().size()>0){
				Set<DatoInformes> lstDatInf = informe.getDatoInformeses();
				//ordenamos la lista
				List<DatoInformes> lstDatOrdenada = ordenarDatosInforme(lstDatInf);
				
				for (DatoInformes datInf:lstDatOrdenada){
					
						CamposPermitidos camPer = datInf.getCamposPermitidos();
						CamposCalculados camCalc = datInf.getCamposCalculados();
						if (camPer !=null){ //CAMPO PERMITIDO
							VistaCampo visC = camPer.getVistaCampo();
							
							// Inserta la vista relacionada en el set
							setVistas.add(visC.getVista());
	
							if (!lstTablas.contains(visC.getVista().getNombreReal())){
								lstTablas.add(visC.getVista().getNombreReal());
							}
							
							//if (!lstCampos.contains(visC.getNombreRealCampo()) && datInf.isVisible()){
							// MPM - 13/09/16
							// Modifico esta comprobación para que se incluyan en el mismo informe dos columnas con el mismo nombre de columna
							// (campo ABREVIADO) en vez del comprobar el nombre del campo real de la tabla
							if (!lstCampos.contains(datInf.getAbreviado()) && datInf.isVisible()){
								lstCampos.add(datInf.getAbreviado());
								if(informeRecibos !=null && informeRecibos.getDatosDe().equals(RESUMEN)){
									lstTabCamp.add(visC.getVista().getNombreReal()+"#"+visC.getNombreRealCampo()+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales()+"#"+datInf.getTotaliza());
								}else{
									lstTabCamp.add(visC.getVista().getNombreReal()+"#"+visC.getNombreRealCampo()+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales());
								}
								cabeceras.add(datInf.getAbreviado());
								cabecerasNombre.add(datInf.getCamposPermitidos().getVistaCampo().getNombre());
								tipo.add(camPer.getVistaCampo().getVistaCampoTipo().getIdtipo().toString());
								// si el dato informe no es suma aÃƒÂ±ado al group by
								if (datInf.getTotaliza().compareTo(ConstantsInf.COD_BIG_TOTALIZA_SUMA) < 0){
									groupBy.add(datInf.getCamposPermitidos().getVistaCampo().getNombre());
								}	
							}

							List<String> lstPermCondiciones = new ArrayList<String>();
							// condiciones permitidos
					    	for (CondicionCamposPermitidos cond:datInf.getCondicionCamposPermitidoses()){
					    		String value = "";
					    		value = getCondicion(cond.getOperadorCamposPermitido().getIdoperador().intValue(), cond.getCondicion(),datInf.getFormato(),camPer.getVistaCampo().getVistaCampoTipo().getIdtipo());
					    		
					    		if(informeParaRecibos){
					    			if (!lstPermCondiciones.contains(value) && (cond.getDatoInformes().getId()).equals(datInf.getId())){
							    		lstPermCondiciones.add(value);
							    	}
	
					    		}else{
					    			if (!lstPermCondiciones.contains(value) && cond.getDatoInformes().getCamposPermitidos().getId().toString().equals(camPer.getId().toString())){
							    		lstPermCondiciones.add(value);
							    	}
					    		}
					    	}
					    	
					    	lstCondPerm.add(camPer.getVistaCampo().getVista().getNombreReal()+"#"+camPer.getVistaCampo().getNombreRealCampo()+"#"+datInf.getFormato());
					    	mCampo_Condiciones.put(camPer.getVistaCampo().getVista().getNombreReal()+"#"+camPer.getVistaCampo().getNombreRealCampo()+"#"+datInf.getFormato(),lstPermCondiciones);
					    	
					    	// CLASIFIC Y RUPTURA CAMPOS PERMITIDOS
							String sentido = "";
							Set<ClasificacionRupturaCamposPermitidos> clasRupturaPerm = datInf.getClasificacionRupturaCamposPermitidoses();
							if (clasRupturaPerm != null && clasRupturaPerm.size()>0){
								for (ClasificacionRupturaCamposPermitidos clasRupPerm:clasRupturaPerm){
									if (clasRupturaPerm != null && !lstRupPerm.contains(visC.getVista().getNombreReal()+"#"+visC.getNombreRealCampo())){
										
										if (informeParaRecibos) {
											lstRupPerm.add(datInf.getAbreviado());
										}
										else {
											lstRupPerm.add(visC.getVista().getNombreReal()+"#"+visC.getNombreRealCampo());
										}
										// ordenacion
										if (clasRupPerm.getSentido().compareTo(ConstantsInf.COD_ORDENACION_ASC) == 0){
											sentido= "ASC";
											orderBy.add(datInf.getAbreviado());
										}else if (clasRupPerm.getSentido().compareTo(ConstantsInf.COD_ORDENACION_DESC) == 0){
											sentido= "DESC";
											orderBy.add(datInf.getAbreviado());
										}
										
										// ruptura
										if (informeParaRecibos) {
											mSentidoPerm.put(datInf.getAbreviado(),sentido);
										}
										else {
											mSentidoPerm.put(visC.getVista().getNombreReal()+"#"+visC.getNombreRealCampo(),sentido);
										}
										
										
										
										if (clasRupPerm.getRuptura().compareTo(ConstantsInf.COD_RUPTURA_SI) == 0){
											ruptura.add(true);
										}else{
											ruptura.add(false);
										}
									}
								}
							}else{
								ruptura.add(false);
							}
							// totaliza PERMITIDO
							if(datInf.isVisible()){
								if (datInf.getTotaliza().compareTo(ConstantsInf.COD_BIG_TOTALIZA_NO) != 0){
									totaliza.add(true);
								}else{
									totaliza.add(false);
								}
							}	
							// total por Grupos PERMITIDO
							if (datInf.getTotaliza().compareTo(ConstantsInf.COD_BIG_TOTALIZA_NO) != 0 && datInf.getTotalPorGrupo().compareTo(ConstantsInf.COD_BIG_TOTAL_POR_GRUPO_SI) == 0){
								totalPorGrupo.add(true);
							}else{
								totalPorGrupo.add(false);
							}
							// anhadimos el formato y decimales al campo permitido
							if (datInf.isVisible()) {
								formato.add(datInf.getFormato());
								decimales.add(datInf.getDecimales());
							}
							
						}
						else if (camCalc !=null){ // CAMPO CALCULADO
					    	vista1 = camCalc.getCamposPermitidosByIdoperando1().getVistaCampo().getVista().getNombreReal();
					    	vista2 = camCalc.getCamposPermitidosByIdoperando2().getVistaCampo().getVista().getNombreReal();
					    	campo1 = camCalc.getCamposPermitidosByIdoperando1().getVistaCampo().getNombreRealCampo();
					    	campo2 = camCalc.getCamposPermitidosByIdoperando2().getVistaCampo().getNombreRealCampo();
					    	opAritmetico = this.getOpAritmetico(camCalc.getIdoperador().intValue());
					    	if (!lstTablas.contains(vista1)){
								lstTablas.add(vista1);
							}
					    	if (!lstTablas.contains(vista2)){
								lstTablas.add(vista2);
							}
					    	
				    		// condiciones calculados
				    		if (datInf.getCondicionCamposCalculadoses() != null){
					    		for (CondicionCamposCalculados condCalc:datInf.getCondicionCamposCalculadoses()){
					    			lstCampCalc.add(vista1+"#"+campo1+"#"+opAritmetico+"#"+vista2+"#"+campo2+"#"+datInf.getAbreviado()+"#"+getCondicion(condCalc.getOperadorCamposCalculados().getIdoperador().intValue(),condCalc.getCondicion(),null,null));
					    			if (!lstTabCamp.contains(vista1+"#"+campo1+"#"+opAritmetico+"#"+vista2+"#"+campo2+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales())){
										lstTabCamp.add(vista1+"#"+campo1+"#"+opAritmetico+"#"+vista2+"#"+campo2+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales());
										cabeceras.add(datInf.getAbreviado());
										tipo.add(Integer.toString(ConstantsInf.CAMPO_TIPO_NUMERICO));
									}
					    		}
				    		}
					    	
					    	// CLASIFIC Y RUPTURA CALCULADOS
							String sentido = "";
							if (datInf.getClasificacionRupturaCamposCalculadoses() != null){
								Set<ClasificacionRupturaCamposCalculados> clasRupturaCalc = datInf.getClasificacionRupturaCamposCalculadoses();
								if (clasRupturaCalc != null && clasRupturaCalc.size()>0){
									for (ClasificacionRupturaCamposCalculados clasRupCalc:clasRupturaCalc){
										if (clasRupCalc != null && !lstRupCalc.contains(datInf.getAbreviado())){
											lstRupCalc.add(datInf.getAbreviado());
											// ordenacion
											if (clasRupCalc.getSentido().compareTo(ConstantsInf.COD_ORDENACION_ASC) == 0){
												sentido= "ASC";
											}else if (clasRupCalc.getSentido().compareTo(ConstantsInf.COD_ORDENACION_DESC) == 0){
												sentido= "DESC";
											}
											// ruptura
											if (clasRupCalc.getRuptura().compareTo(ConstantsInf.COD_RUPTURA_SI) == 0){
												ruptura.add(true);
											}else{
												ruptura.add(false);
											}
											
											mSentidoCalc.put(datInf.getAbreviado(),sentido);
										}
									}
								}else{
									ruptura.add(false);
								}
							}
							if (!lstTabCamp.contains(vista1+"#"+campo1+"#"+opAritmetico+"#"+vista2+"#"+campo2+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales())){
								lstTabCamp.add(vista1+"#"+campo1+"#"+opAritmetico+"#"+vista2+"#"+campo2+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales());
								cabeceras.add(datInf.getAbreviado());
								tipo.add(Integer.toString(ConstantsInf.CAMPO_TIPO_NUMERICO));
							}
							
							// condicion especial Division calculados
							if (opAritmetico.equals(ConstantsInf.OPERADOR_ARIT_DIV)){
								if (!lstCondEspCalc.contains(vista2+"#"+campo2)){
									lstCondEspCalc.add(vista2+"#"+campo2);
								}
							}
							
							// totaliza Calculados
							totaliza.add(false);  // de momento no se totaliza un campo calculado
							// totalPorGrupo Calculados
							totalPorGrupo.add(false); // lo mismo para el totalPorGrupo
							formato.add(datInf.getFormato());
							decimales.add(datInf.getDecimales());
						}
					
				} // FIN datosInformes
				
				// RELACIONAR TABLAS
				HashMap<String, Object> mapRelTablas = this.relacionarTablasInforme(lstTablas,false);
				@SuppressWarnings("unchecked")
				List<RelVistaCampos> lstRelacionesTablas = (List)mapRelTablas.get(LISTA_RELACIONES_TABLAS);
				
				
				if (!("true").equals(StringUtils.nullToString(consultaYaGenerada))){
				
					//*********************
					//CONSTRUCCION CONSULTA
					//*********************
					boolean inicio = true;
					//si estoy ordenando y el formato es resumen y el campo por el que ordeno no esta en la agrupacion
					if(!orderBy.isEmpty() && (informeRecibos!= null && (RESUMEN).equals(informeRecibos.getDatosDe())) && !groupBy.contains(orderBy.get(0))){
						sql = QUERY_SELECT_ALL;
						orderGroupBy = true;
					}
					
					sql += QUERY_SELECT;
					
					// CAMPOS
					for (String tabCamp:lstTabCamp){
						logger.debug("[ESC-25609] tabCamp: " + tabCamp);
						String[] campArray = tabCamp.split("#");
						logger.debug("[ESC-25609] campArray: " + campArray);
						logger.debug("[ESC-25609] informeRecibos: " + informeRecibos);
						if (campArray.length >5 && informeRecibos != null && !RESUMEN.equals(informeRecibos.getDatosDe())){ // calculado
							sql += QUERY_TO_NUMBER+campArray[0] +"$." +campArray[1] +QUERY_0+campArray[2]+QUERY_NVL+campArray[3] +"$." +campArray[4]+QUERY_0_AS+campArray[5]+"\",";
						}else{ // permitido
							if (Integer.toString(ConstantsInf.COD_FORMATO_FECHA_DDMMYYYY).equals(campArray[3])){
								sql += QUERY_TO_CHAR+campArray[0] +"$." +campArray[1] +",'"+ConstantsInf.FORMATO_FECHA_DDMMYYYY+QUERY_AS+campArray[2]+"\",";
							}else if (Integer.toString(ConstantsInf.COD_FORMATO_FECHA_YYYYMMDD).equals(campArray[3])){
								sql += QUERY_TO_CHAR+campArray[0] +"$." +campArray[1] +",'"+ConstantsInf.FORMATO_FECHA_YYYYMMDD+QUERY_AS+campArray[2]+"\",";
							}else if(informeRecibos != null && RESUMEN.equals(informeRecibos.getDatosDe()) && Integer.toString(ConstantsInf.COD_TOTALIZA_SUMA).equals(campArray[5])){
								sql += "SUM("+campArray[0] +"$." +campArray[1] +") \""+campArray[2]+"\",";
							}else{
								if (campArray.length >5 && PRUEBA_FICHERO_COMISIONES.equals(informe.getNombre())) {
								    //sql += campArray[0] +"$." + campArray[1] +" "+ campArray[2] +" "+ campArray[3] +"$." + campArray[4] +" \""+campArray[5]+"\",";
								    sql += QUERY_TO_NUMBER+campArray[0] +"$." +campArray[1] +QUERY_0+campArray[2]+QUERY_NVL+campArray[3] +"$." +campArray[4]+QUERY_0_AS+campArray[5]+"\",";
								} else {
								    sql += campArray[0] +"$." +campArray[1] +" \""+campArray[2]+"\",";
								}
							}
						}
					}
					
					sql = deleteLastChar(sql);
					sql += QUERY_FROM;
					
					// TABLAS
					for (String tabla:lstTablas){
						sql += esquema + "." + tabla + " " + tabla +"$,";
					}
					
					/* Pet. 62719 ** MODIF TAM (01.02.2021) ** Inicio */
					/* Se incluyen los join y el filtro para no sacar en el informe polizas de asegurados
					 * que estén bloqueados */
					String filtro_asegurado_bloqueado = "";
					
					sql = deleteLastChar(sql);
					filtro_asegurado_bloqueado = obtener_filtro_asegBloqueado(lstTablas, true);
					
					sql += filtro_asegurado_bloqueado;
					/* Pet. 62719 ** MODIF TAM (01.02.2021) ** Fin */
					
					// RELACIONES TABLAS
					if (lstRelacionesTablas.size() >0){
						for (RelVistaCampos relTablas:lstRelacionesTablas){
							vista1 = relTablas.getVistaByIdvista1().getNombreReal();
							vista2 = relTablas.getVistaByIdvista2().getNombreReal();
							campo1 = relTablas.getVistaCampoByIdcampo1().getNombreRealCampo();
							campo2 = relTablas.getVistaCampoByIdcampo2().getNombreRealCampo();
							sql = deleteLastChar(sql);
							if (inicio){
								sql += QUERY_WHERE;
								inicio = false;
							}else{
								sql += QUERY_AND;
							}
							if (relTablas.getLeftJoin().compareTo(new BigDecimal(0)) == 0){
								sql += vista1+"$."+campo1+ " = " + vista2+"$."+campo2;
							}else{ // LEFT JOIN
								sql += vista1+"$."+campo1+ QUERY_CONCAT + vista2+"$."+campo2;
							}
						}
					}
					sql = deleteLastChar(sql);
					if (inicio){
						sql += QUERY_WHERE_1;
					}
					// CONDICIONES PERMITIDOS
					for (String cond:lstCondPerm){
						condArr = cond.split("#");
						if (mCampo_Condiciones.get(cond) != null){
							for (String value:mCampo_Condiciones.get(cond)){
								if (!StringUtils.nullToString(condArr[2]).equals("") && (new BigDecimal(condArr[2]).compareTo(new BigDecimal(ConstantsInf.COD_FORMATO_FECHA_DDMMYYYY))== 0 ||
										new BigDecimal(condArr[2]).compareTo(new BigDecimal(ConstantsInf.COD_FORMATO_FECHA_YYYYMMDD))== 0)){
									sql += QUERY_AND_DATE+condArr[0] +"$." +condArr[1]+") " + value;
								}else{
									sql += QUERY_AND+condArr[0] +"$." +condArr[1] + value;
								}
								
							}
						}
					}
					
					// CONDICIONES CALCULADOS
					for (String cCalc:lstCampCalc){
						cCalcArr = cCalc.split("#");
						sql += QUERY_AND+"("+cCalcArr[0] +"$." +cCalcArr[1] +" "+cCalcArr[2]+" "+cCalcArr[3] +"$." +cCalcArr[4]+")"+ cCalcArr[6];
					}
					
					// condicion especial division calculados
					for (String condCalc:lstCondEspCalc){
						cDivision = condCalc.split("#");
						sql += QUERY_AND+cDivision[0] +"$." +cDivision[1] +" <>0";
					}
					
					// Si el usuario no es perfil 0, se anhaden las anhaden las restricciones indicadas para cada perfil y origen de datos
					if (!Constants.PERFIL_USUARIO_ADMINISTRADOR.equals(usuario.getPerfil())) {
						sql += addRestriccionesPorPerfilYTabla(usuario, setVistas);
					}
					
					/* Pet. 62719 ** MODIF TAM (01.02.2021) ** Inicio */
					if (!filtro_asegurado_bloqueado.equals("")) {
						sql = deleteLastChar(sql);
						
						filtro_asegurado_bloqueado = obtener_filtro_asegBloqueado(lstTablas, false);
						
						sql += filtro_asegurado_bloqueado;
					}
					/* Pet. 62719 ** MODIF TAM (01.02.2021) ** Fin */	
					
					if(informeRecibos != null && informeRecibos.getDatosDe().equals(RESUMEN) && !groupBy.isEmpty()){
						sql += QUERY_GROUP;
						for(int i=0; i<groupBy.size(); i++){
							sql += groupBy.get(i);
							if(i<groupBy.size()-1){
								sql+=", ";
							}
						}
					}
					
					// SENTIDO PERMITIDOS
					inicio = true;
					for (String clasRup:lstRupPerm){
						String[] clasRupArray = clasRup.split("#");
						if (mSentidoPerm.get(clasRup) != null){
							if (inicio){
								if(orderGroupBy){
									sql += ")";
								}	
								sql += QUERY_ORDER;
								inicio = false;
							}
							
							if (informeParaRecibos) {
								sql += "\"" + clasRupArray[0] + "\" " + mSentidoPerm.get(clasRup) + ",";
							}
							else {
								if(orderGroupBy){
									sql += clasRupArray[1] +" "+ mSentidoPerm.get(clasRup) +",";
								}else{
									sql += clasRupArray[0] +"$." +clasRupArray[1] +" "+ mSentidoPerm.get(clasRup) +",";
								}	
							}
						}
					}
					
					// SENTIDO CALCULADOS
					for (String cCalc:lstRupCalc){
						if (mSentidoCalc.get(cCalc) != null){
							if (inicio){
								sql += QUERY_ORDER;
								inicio = false;
							}
							sql += "\""+cCalc+ "\" "+ mSentidoCalc.get(cCalc) +",";
						}
					}
					
					sql = deleteLastChar(sql);
					
					try {
						mtoInformeDao.actualizaConsultaInforme(sql, informe.getId());
					} catch (DAOException e) {
						logger.error(" Se ha producido un error al actualizar la consulta del informe , " + e);
						throw new BusinessException(ACTUALIZAR_SQL, e);
					}
					if(!informeParaRecibos || (informeParaRecibos && (Integer.parseInt(informeRecibos.getFormato()) == ConstantsInf.COD_FORMATO_TXT))){
						//DAA 20/02/2013  Calculamos el numero de registros para evaluar el maximo permitido
						int numRegistros = getCountNumRegistros(sql);
						parameters.put(NUM_REGISTROS,numRegistros);
						logger.info(NUM_REGISTROS_EQUALS + numRegistros);
					}
				//si se ha generado previamente la consulta la obtengo del objeto informe guardado previamente	
				}else{
					try {
						sql = StringUtils.convertStreamToString(informe.getConsulta().getBinaryStream());
					} catch (SQLException e) {
						logger.error(" Se ha producido un error al recuperar la consulta del informe , ", e);
					}
				}
				
				// pasamos los datos del informe a un mapa
				parameters.put(INFORME, informe);
				parameters.put(CABECERAS,cabeceras);
				parameters.put(CABECERAS_NOMBRE,cabecerasNombre);
				parameters.put("tipo",tipo);
				parameters.put(TOTALIZA, totaliza);
				parameters.put(TOTAL_POR_GRUPO, totalPorGrupo);
				parameters.put(RUPTURA, ruptura);
				parameters.put(FORMATO, formato);
				parameters.put(DECIMALES, decimales);
				parameters.put("sql",sql);
				
				logger.info("************SQL************** *");
				logger.info(sql);
				logger.info("***************************** *");
				logger.debug("end -  generarConsultaInforme");
				return parameters;
			}else{ // sin datos en el informe
				logger.info("Sin datos en el informe");
				logger.debug("end  - generarConsultaInforme");
				throw new BusinessException(SIN_DATOS_INFORME);
				//return parameters;
			}
				
		} catch (BusinessException e) {
			logger.error("Se ha producido un error al crear la consulta del informe , " + e);
			if (e.getMessage().equals(SIN_DATOS_INFORME)){
				throw new BusinessException("El informe no tiene DatosInforme", e);
			}else if (e.getMessage().equals(ACTUALIZAR_SQL)){
				throw new BusinessException(" Se ha producido un error al actualizar la consulta del informe en BBDD", e);
			}else{
				throw new BusinessException(" Se ha producido un error al crear la consulta del informe: ", e);
			}
		}
	}
	

	/**
	 * Metodo que genera la consulta del informe y la guarda en BBDD (versión 2015+)
	 * @param informe
	 * @param usuario
	 * @param consultaYaGenerada
	 * @param informeRecibos
	 * @return mapa con los datos del informe
	 * @throws BusinessException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HashMap<String, Object> generarConsultaInforme2015(Informe informe, final Usuario usuario, String consultaYaGenerada, InformeRecibos2015 informeRecibos) throws BusinessException{
		logger.debug("init - generarConsultaInforme2015");
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		Map<String, List<String>> mCampo_Condiciones = new HashMap<String, List<String>>();
		Map<String, String> mSentidoPerm = new HashMap<String, String>();
		Map<String, String> mSentidoCalc = new HashMap<String, String>();
		List<String> tipo = new ArrayList<String>();
		List<String> cabeceras = new ArrayList<String>();		
		List<String> cabecerasNombre = new ArrayList<String>();
		List<String> lstTablas = new ArrayList<String>();
		List<String> lstCampos = new ArrayList<String>();
		List<String> lstTabCamp = new ArrayList<String>();
		List<String> lstRupPerm = new ArrayList<String>();
		List<String> lstRupCalc = new ArrayList<String>();
		List<String> lstCondPerm = new ArrayList<String>();
		List<String> lstCampCalc = new ArrayList<String>();
		List<Boolean> totaliza = new ArrayList<Boolean>();
		String[] condArr,cCalcArr,cDivision = null;
		List<String> groupBy = new ArrayList<String>();
		List<String> orderBy = new ArrayList<String>();
		TreeSet<Vista> setVistas = new TreeSet<Vista>();
		boolean orderGroupBy = false;
		
		String filtro_asegurado_bloqueado = ""; 
		
		try {
			String vista1 = "";
			String vista2 = "";
			String campo1 = "";
			String campo2 = "";
			String opAritmetico = ""; 
			String sql = "";
			List<Boolean> totalPorGrupo = new ArrayList<Boolean>();
			List<Boolean> ruptura = new ArrayList<Boolean>();
			List<BigDecimal> formato = new ArrayList<BigDecimal>();
			List<BigDecimal> decimales = new ArrayList<BigDecimal>();
			List<String> lstCondEspCalc = new ArrayList<String>();
			if(informeRecibos != null ){
				if(("").equals(StringUtils.nullToString(informeRecibos.getCampoOrdenar())))
					informe = this.getInformeOculto(informe.getOculto());
			}else{
				informe = this.getInforme(informe.getId());
			}
			boolean informeParaRecibos = false;
			
			// DAA 02/10/2013
			// Obtiene el nombre del esquema contra el que se lanzara la consulta en la generacion del informe
			// y evalua si esta es para Recibos de comisiones, en ese caso establece los campos que no hemos marcado en la jsp
			String esquema = "";
			if(informeRecibos != null){
				informeParaRecibos = true;
				esquema = ESQUEMA;
				if(("").equals(StringUtils.nullToString(informeRecibos.getCampoOrdenar()))){
					informe = setCamposParaInformeRecibos2015 (informeRecibos, informe);
					informe = setFiltroParaInformeRecibos2015 (informeRecibos, informe);
				}else{
					informe = setOrdenacionParaInformeRecibos2015 (informeRecibos, informe);
				}
					
			}else{
				esquema = cargarEsquemaConsultaInforme();
			}

			if (informe != null && null != informe.getDatoInformeses() && informe.getDatoInformeses().size()>0){
				Set<DatoInformes> lstDatInf = informe.getDatoInformeses();
				//ordenamos la lista
				List<DatoInformes> lstDatOrdenada = ordenarDatosInforme(lstDatInf);
				
				for (DatoInformes datInf:lstDatOrdenada){
					
						CamposPermitidos camPer = datInf.getCamposPermitidos();
						CamposCalculados camCalc = datInf.getCamposCalculados();
						if (camPer !=null){ //CAMPO PERMITIDO
							VistaCampo visC = camPer.getVistaCampo();
							
							// Inserta la vista relacionada en el set
							setVistas.add(visC.getVista());
	
							if (!lstTablas.contains(visC.getVista().getNombreReal())){
								lstTablas.add(visC.getVista().getNombreReal());
							}
							
							if (!lstCampos.contains(visC.getNombreRealCampo()) && datInf.isVisible()){
								lstCampos.add(visC.getNombreRealCampo());
								if(informeRecibos !=null && informeRecibos.getDatosDe().equals(RESUMEN)){
									lstTabCamp.add(visC.getVista().getNombreReal()+"#"+visC.getNombreRealCampo()+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales()+"#"+datInf.getTotaliza());
								}else{
									lstTabCamp.add(visC.getVista().getNombreReal()+"#"+visC.getNombreRealCampo()+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales());
								}
								cabeceras.add(datInf.getAbreviado());
								cabecerasNombre.add(datInf.getCamposPermitidos().getVistaCampo().getNombre());
								tipo.add(camPer.getVistaCampo().getVistaCampoTipo().getIdtipo().toString());
								// si el dato informe no es suma aÃƒÂ±ado al group by
								if (datInf.getTotaliza().compareTo(ConstantsInf.COD_BIG_TOTALIZA_SUMA) < 0){
									groupBy.add(datInf.getCamposPermitidos().getVistaCampo().getNombre());
								}	
							}

							List<String> lstPermCondiciones = new ArrayList<String>();
							// condiciones permitidos
					    	for (CondicionCamposPermitidos cond:datInf.getCondicionCamposPermitidoses()){
					    		String value = "";
					    		value = getCondicion(cond.getOperadorCamposPermitido().getIdoperador().intValue(), cond.getCondicion(),datInf.getFormato(),camPer.getVistaCampo().getVistaCampoTipo().getIdtipo());
					    		
					    		if(informeParaRecibos){
					    			if (!lstPermCondiciones.contains(value) && (cond.getDatoInformes().getId()).equals(datInf.getId())){
							    		lstPermCondiciones.add(value);
							    	}
	
					    		}else{
					    			if (!lstPermCondiciones.contains(value) && cond.getDatoInformes().getCamposPermitidos().getId().toString().equals(camPer.getId().toString())){
							    		lstPermCondiciones.add(value);
							    	}
					    		}
					    	}
					    	
					    	lstCondPerm.add(camPer.getVistaCampo().getVista().getNombreReal()+"#"+camPer.getVistaCampo().getNombreRealCampo()+"#"+datInf.getFormato());
					    	mCampo_Condiciones.put(camPer.getVistaCampo().getVista().getNombreReal()+"#"+camPer.getVistaCampo().getNombreRealCampo()+"#"+datInf.getFormato(),lstPermCondiciones);
					    	
					    	// CLASIFIC Y RUPTURA CAMPOS PERMITIDOS
							String sentido = "";
							Set<ClasificacionRupturaCamposPermitidos> clasRupturaPerm = datInf.getClasificacionRupturaCamposPermitidoses();
							if (clasRupturaPerm != null && clasRupturaPerm.size()>0){
								for (ClasificacionRupturaCamposPermitidos clasRupPerm:clasRupturaPerm){
									if (clasRupturaPerm != null && !lstRupPerm.contains(visC.getVista().getNombreReal()+"#"+visC.getNombreRealCampo())){
										
										if (informeParaRecibos) {
											lstRupPerm.add(datInf.getAbreviado());
										}
										else {
											lstRupPerm.add(visC.getVista().getNombreReal()+"#"+visC.getNombreRealCampo());
										}
										// ordenacion
										if (clasRupPerm.getSentido().compareTo(ConstantsInf.COD_ORDENACION_ASC) == 0){
											sentido= "ASC";
											orderBy.add(datInf.getAbreviado());
										}else if (clasRupPerm.getSentido().compareTo(ConstantsInf.COD_ORDENACION_DESC) == 0){
											sentido= "DESC";
											orderBy.add(datInf.getAbreviado());
										}
										
										// ruptura
										if (informeParaRecibos) {
											mSentidoPerm.put(datInf.getAbreviado(),sentido);
										}
										else {
											mSentidoPerm.put(visC.getVista().getNombreReal()+"#"+visC.getNombreRealCampo(),sentido);
										}
										
										
										
										if (clasRupPerm.getRuptura().compareTo(ConstantsInf.COD_RUPTURA_SI) == 0){
											ruptura.add(true);
										}else{
											ruptura.add(false);
										}
									}
								}
							}else{
								ruptura.add(false);
							}
							// totaliza PERMITIDO
							if(datInf.isVisible()){
								if (datInf.getTotaliza().compareTo(ConstantsInf.COD_BIG_TOTALIZA_NO) != 0){
									totaliza.add(true);
								}else{
									totaliza.add(false);
								}
							}	
							// total por Grupos PERMITIDO
							if (datInf.getTotaliza().compareTo(ConstantsInf.COD_BIG_TOTALIZA_NO) != 0 && datInf.getTotalPorGrupo().compareTo(ConstantsInf.COD_BIG_TOTAL_POR_GRUPO_SI) == 0){
								totalPorGrupo.add(true);
							}else{
								totalPorGrupo.add(false);
							}
							// anhadimos el formato y decimales al campo permitido
							if (datInf.isVisible()) {
								formato.add(datInf.getFormato());
								decimales.add(datInf.getDecimales());
							}
							
						}
						else if (camCalc !=null){ // CAMPO CALCULADO
					    	vista1 = camCalc.getCamposPermitidosByIdoperando1().getVistaCampo().getVista().getNombreReal();
					    	vista2 = camCalc.getCamposPermitidosByIdoperando2().getVistaCampo().getVista().getNombreReal();
					    	campo1 = camCalc.getCamposPermitidosByIdoperando1().getVistaCampo().getNombreRealCampo();
					    	campo2 = camCalc.getCamposPermitidosByIdoperando2().getVistaCampo().getNombreRealCampo();
					    	opAritmetico = this.getOpAritmetico(camCalc.getIdoperador().intValue());
					    	if (!lstTablas.contains(vista1)){
								lstTablas.add(vista1);
							}
					    	if (!lstTablas.contains(vista2)){
								lstTablas.add(vista2);
							}
					    	
				    		// condiciones calculados
				    		if (datInf.getCondicionCamposCalculadoses() != null){
					    		for (CondicionCamposCalculados condCalc:datInf.getCondicionCamposCalculadoses()){
					    			lstCampCalc.add(vista1+"#"+campo1+"#"+opAritmetico+"#"+vista2+"#"+campo2+"#"+datInf.getAbreviado()+"#"+getCondicion(condCalc.getOperadorCamposCalculados().getIdoperador().intValue(),condCalc.getCondicion(),null,null));
					    			if (!lstTabCamp.contains(vista1+"#"+campo1+"#"+opAritmetico+"#"+vista2+"#"+campo2+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales())){
										lstTabCamp.add(vista1+"#"+campo1+"#"+opAritmetico+"#"+vista2+"#"+campo2+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales());
										cabeceras.add(datInf.getAbreviado());
										tipo.add(Integer.toString(ConstantsInf.CAMPO_TIPO_NUMERICO));
									}
					    		}
				    		}
					    	
					    	// CLASIFIC Y RUPTURA CALCULADOS
							String sentido = "";
							if (datInf.getClasificacionRupturaCamposCalculadoses() != null){
								Set<ClasificacionRupturaCamposCalculados> clasRupturaCalc = datInf.getClasificacionRupturaCamposCalculadoses();
								if (clasRupturaCalc != null && clasRupturaCalc.size()>0){
									for (ClasificacionRupturaCamposCalculados clasRupCalc:clasRupturaCalc){
										if (clasRupCalc != null && !lstRupCalc.contains(datInf.getAbreviado())){
											lstRupCalc.add(datInf.getAbreviado());
											// ordenacion
											if (clasRupCalc.getSentido().compareTo(ConstantsInf.COD_ORDENACION_ASC) == 0){
												sentido= "ASC";
											}else if (clasRupCalc.getSentido().compareTo(ConstantsInf.COD_ORDENACION_DESC) == 0){
												sentido= "DESC";
											}
											// ruptura
											if (clasRupCalc.getRuptura().compareTo(ConstantsInf.COD_RUPTURA_SI) == 0){
												ruptura.add(true);
											}else{
												ruptura.add(false);
											}
											
											mSentidoCalc.put(datInf.getAbreviado(),sentido);
										}
									}
								}else{
									ruptura.add(false);
								}
							}
							if (!lstTabCamp.contains(vista1+"#"+campo1+"#"+opAritmetico+"#"+vista2+"#"+campo2+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales())){
								lstTabCamp.add(vista1+"#"+campo1+"#"+opAritmetico+"#"+vista2+"#"+campo2+"#"+datInf.getAbreviado()+"#"+datInf.getFormato()+"#"+datInf.getDecimales());
								cabeceras.add(datInf.getAbreviado());
								tipo.add(Integer.toString(ConstantsInf.CAMPO_TIPO_NUMERICO));
							}
							
							// condicion especial Division calculados
							if (opAritmetico.equals(ConstantsInf.OPERADOR_ARIT_DIV)){
								if (!lstCondEspCalc.contains(vista2+"#"+campo2)){
									lstCondEspCalc.add(vista2+"#"+campo2);
								}
							}
							
							// totaliza Calculados
							totaliza.add(false);  // de momento no se totaliza un campo calculado
							// totalPorGrupo Calculados
							totalPorGrupo.add(false); // lo mismo para el totalPorGrupo
							formato.add(datInf.getFormato());
							decimales.add(datInf.getDecimales());
						}
					
				} // FIN datosInformes
				
				// RELACIONAR TABLAS
				HashMap<String, Object> mapRelTablas = this.relacionarTablasInforme(lstTablas,false);
				List<RelVistaCampos> lstRelacionesTablas = (List)mapRelTablas.get(LISTA_RELACIONES_TABLAS);
				
				
				if (!("true").equals(StringUtils.nullToString(consultaYaGenerada))){
				
					//*********************
					//CONSTRUCCION CONSULTA
					//*********************
					boolean inicio = true;
					//si estoy ordenando y el formato es resumen y el campo por el que ordeno no esta en la agrupacion
					if(!orderBy.isEmpty() && (informeRecibos!= null && (RESUMEN).equals(informeRecibos.getDatosDe())) && !groupBy.contains(orderBy.get(0))){
						sql = QUERY_SELECT_ALL;
						orderGroupBy = true;
					}
					
					sql += QUERY_SELECT;
					
					// CAMPOS
					for (String tabCamp:lstTabCamp){
						String[] campArray = tabCamp.split("#");
						if (campArray.length >5 && !informeRecibos.getDatosDe().equals(RESUMEN)){ // calculado
							sql += QUERY_TO_NUMBER+campArray[0] +"$." +campArray[1] +QUERY_0+campArray[2]+QUERY_NVL+campArray[3] +"$." +campArray[4]+QUERY_0_AS+campArray[5]+"\",";
						}else{ // permitido
							if (campArray[3].equals(Integer.toString(ConstantsInf.COD_FORMATO_FECHA_DDMMYYYY))){
								sql += QUERY_TO_CHAR+campArray[0] +"$." +campArray[1] +",'"+ConstantsInf.FORMATO_FECHA_DDMMYYYY+QUERY_AS+campArray[2]+"\",";
							}else if (campArray[3].equals(Integer.toString(ConstantsInf.COD_FORMATO_FECHA_YYYYMMDD))){
								sql += QUERY_TO_CHAR+campArray[0] +"$." +campArray[1] +",'"+ConstantsInf.FORMATO_FECHA_YYYYMMDD+QUERY_AS+campArray[2]+"\",";
							}else if(informeRecibos != null && informeRecibos.getDatosDe().equals(RESUMEN) && campArray[5].equals(Integer.toString(ConstantsInf.COD_TOTALIZA_SUMA))){
								sql += "SUM("+campArray[0] +"$." +campArray[1] +") \""+campArray[2]+"\",";
							}else{
								if (campArray.length >5 && PRUEBA_FICHERO_COMISIONES.equals(informe.getNombre())) {
								    //sql += campArray[0] +"$." + campArray[1] +" "+ campArray[2] +" "+ campArray[3] +"$." + campArray[4] +" \""+campArray[5]+"\",";
								    sql += QUERY_TO_NUMBER+campArray[0] +"$." +campArray[1] +QUERY_0+campArray[2]+QUERY_NVL+campArray[3] +"$." +campArray[4]+QUERY_0_AS+campArray[5]+"\",";
								} else {
								    sql += campArray[0] +"$." +campArray[1] +" \""+campArray[2]+"\",";
								}
							}
						}
					}
					
					sql = deleteLastChar(sql);
					sql += QUERY_FROM;
					
					// TABLAS
					for (String tabla:lstTablas){
						sql += esquema + "." + tabla + " " + tabla +"$,";
					}
					
					// RELACIONES TABLAS
					if (lstRelacionesTablas.size() >0){
						for (RelVistaCampos relTablas:lstRelacionesTablas){
							vista1 = relTablas.getVistaByIdvista1().getNombreReal();
							vista2 = relTablas.getVistaByIdvista2().getNombreReal();
							campo1 = relTablas.getVistaCampoByIdcampo1().getNombreRealCampo();
							campo2 = relTablas.getVistaCampoByIdcampo2().getNombreRealCampo();
							sql = deleteLastChar(sql);
							if (inicio){
								
								/* Pet. 62719 ** MODIF TAM (01.02.2021) ** Inicio */
								/* Se incluyen los join y el filtro para no sacar en el informe polizas de asegurados
								 * que estén bloqueados */
								filtro_asegurado_bloqueado = obtener_filtro_asegBloqueado(lstTablas, true);
								
								sql += filtro_asegurado_bloqueado;
								/* Pet. 62719 ** MODIF TAM (01.02.2021) ** Fin */

								
								sql += QUERY_WHERE;
								inicio = false;
							}else{
								sql += QUERY_AND;
							}
							if (relTablas.getLeftJoin().compareTo(new BigDecimal(0)) == 0){
								sql += vista1+"$."+campo1+ " = " + vista2+"$."+campo2;
							}else{ // LEFT JOIN
								sql += vista1+"$."+campo1+ QUERY_CONCAT + vista2+"$."+campo2;
							}
						}
					}
					sql = deleteLastChar(sql);
					if (inicio){

						/* Pet. 62719 ** MODIF TAM (01.02.2021) ** Inicio */
						/* Se incluyen los join y el filtro para no sacar en el informe polizas de asegurados
						 * que estén bloqueados */
						filtro_asegurado_bloqueado = obtener_filtro_asegBloqueado(lstTablas, true);
						
						sql += filtro_asegurado_bloqueado;
						/* Pet. 62719 ** MODIF TAM (01.02.2021) ** Fin */

						sql += QUERY_WHERE_1;
					}
					// CONDICIONES PERMITIDOS
					for (String cond:lstCondPerm){
						condArr = cond.split("#");
						if (mCampo_Condiciones.get(cond) != null){
							for (String value:mCampo_Condiciones.get(cond)){
								if (!StringUtils.nullToString(condArr[2]).equals("") && (new BigDecimal(condArr[2]).compareTo(new BigDecimal(ConstantsInf.COD_FORMATO_FECHA_DDMMYYYY))== 0 ||
										new BigDecimal(condArr[2]).compareTo(new BigDecimal(ConstantsInf.COD_FORMATO_FECHA_YYYYMMDD))== 0)){
									sql += QUERY_AND_DATE+condArr[0] +"$." +condArr[1]+") " + value;
								}else{
									sql += QUERY_AND+condArr[0] +"$." +condArr[1] + value;
								}
								
							}
						}
					}
					
					// CONDICIONES CALCULADOS
					for (String cCalc:lstCampCalc){
						cCalcArr = cCalc.split("#");
						sql += QUERY_AND+"("+cCalcArr[0] +"$." +cCalcArr[1] +" "+cCalcArr[2]+" "+cCalcArr[3] +"$." +cCalcArr[4]+")"+ cCalcArr[6];
					}
					
					// condicion especial division calculados
					for (String condCalc:lstCondEspCalc){
						cDivision = condCalc.split("#");
						sql += QUERY_AND+cDivision[0] +"$." +cDivision[1] +" <>0";
					}
					
					// Si el usuario no es perfil 0, se anhaden las anhaden las restricciones indicadas para cada perfil y origen de datos
					if (!Constants.PERFIL_USUARIO_ADMINISTRADOR.equals(usuario.getPerfil())) {
						sql += addRestriccionesPorPerfilYTabla(usuario, setVistas);
					}
					
					/* Pet. 62719 ** MODIF TAM (01.02.2021) ** Inicio */
					if (!filtro_asegurado_bloqueado.equals("")) {
						sql = deleteLastChar(sql);
						
						filtro_asegurado_bloqueado = obtener_filtro_asegBloqueado(lstTablas, false);
						
						sql += filtro_asegurado_bloqueado;
					}
					/* Pet. 62719 ** MODIF TAM (01.02.2021) ** Fin */						
					
					if(informeRecibos != null && informeRecibos.getDatosDe().equals(RESUMEN) && !groupBy.isEmpty()){
						sql += QUERY_GROUP;
						for(int i=0; i<groupBy.size(); i++){
							sql += groupBy.get(i);
							if(i<groupBy.size()-1){
								sql+=", ";
							}
						}
					}
					
					// SENTIDO PERMITIDOS
					inicio = true;
					for (String clasRup:lstRupPerm){
						String[] clasRupArray = clasRup.split("#");
						if (mSentidoPerm.get(clasRup) != null){
							if (inicio){
								if(orderGroupBy){
									sql += ")";
								}	
								sql += QUERY_ORDER;
								inicio = false;
							}
							
							if (informeParaRecibos) {
								sql += "\"" + clasRupArray[0] + "\" " + mSentidoPerm.get(clasRup) + ",";
							}
							else {
								if(orderGroupBy){
									sql += clasRupArray[1] +" "+ mSentidoPerm.get(clasRup) +",";
								}else{
									sql += clasRupArray[0] +"$." +clasRupArray[1] +" "+ mSentidoPerm.get(clasRup) +",";
								}	
							}
						}
					}
					
					// SENTIDO CALCULADOS
					for (String cCalc:lstRupCalc){
						if (mSentidoCalc.get(cCalc) != null){
							if (inicio){
								sql += QUERY_ORDER;
								inicio = false;
							}
							sql += "\""+cCalc+ "\" "+ mSentidoCalc.get(cCalc) +",";
						}
					}
					
					sql = deleteLastChar(sql);
					
					try {
						mtoInformeDao.actualizaConsultaInforme(sql, informe.getId());
					} catch (DAOException e) {
						logger.error(" Se ha producido un error al actualizar la consulta del informe, " + e);
						throw new BusinessException(ACTUALIZAR_SQL, e);
					}
					if(!informeParaRecibos || (informeParaRecibos && (Integer.parseInt(informeRecibos.getFormato()) == ConstantsInf.COD_FORMATO_TXT))){
						//DAA 20/02/2013  Calculamos el numero de registros para evaluar el maximo permitido
						int numRegistros = getCountNumRegistros(sql);
						parameters.put(NUM_REGISTROS,numRegistros);
						logger.info(NUM_REGISTROS_EQUALS + numRegistros);
					}
				//si se ha generado previamente la consulta la obtengo del objeto informe guardado previamente	
				}else{
					try {
						sql = StringUtils.convertStreamToString(informe.getConsulta().getBinaryStream());
					} catch (SQLException e) {
						logger.error(" Se ha producido un error al recuperar la consulta del informe, ", e);
					}
				}
				
				// pasamos los datos del informe a un mapa
				parameters.put(INFORME, informe);
				parameters.put(CABECERAS,cabeceras);
				parameters.put(CABECERAS_NOMBRE,cabecerasNombre);
				parameters.put("tipo",tipo);
				parameters.put(TOTALIZA, totaliza);
				parameters.put(TOTAL_POR_GRUPO, totalPorGrupo);
				parameters.put(RUPTURA, ruptura);
				parameters.put(FORMATO, formato);
				parameters.put(DECIMALES, decimales);
				parameters.put("sql",sql);
				
				logger.info("* ***********SQL***************");
				logger.info(sql);
				logger.info("* *****************************");
				logger.debug("end - generarConsultaInforme2015");
				return parameters;
			}else{ // sin datos en el informe
				logger.info("Sin datos en el informe ");
				logger.debug("end - generarConsultaInforme2015");
				throw new BusinessException(SIN_DATOS_INFORME);
				//return parameters;
			}
				
		} catch (BusinessException e) {
			logger.error("Se ha producido un error al crear la consulta del informe, " + e);
			if (e.getMessage().equals(SIN_DATOS_INFORME)){
				throw new BusinessException("El informe no tiene DatosInforme ", e);
			}else if (e.getMessage().equals(ACTUALIZAR_SQL)){
				throw new BusinessException("Se ha producido un error al actualizar la consulta del informe en BBDD ", e);
			}else{
				throw new BusinessException("Se ha producido un error al crear la consulta del  informe: ", e);
			}
		}
	}
	
	/**	DAA 17/10/2013
	 * 	Metodo que aï¿½ade la ordenacion para el informe
	 * @param informe 
	 * @return 
	 */
	private Informe setOrdenacionParaInformeRecibos(InformeRecibos informeRecibos, Informe informe) {
		logger.debug("setOrdenacionParaInformeRecibos - init");
		
		
		for(DatoInformes dato: informe.getDatoInformeses()){
			if((informeRecibos.getCampoOrdenar()).equals(dato.getAbreviado())){
				Set<ClasificacionRupturaCamposPermitidos> aux = new HashSet<ClasificacionRupturaCamposPermitidos>();
				ClasificacionRupturaCamposPermitidos clasRupturaPerm = new ClasificacionRupturaCamposPermitidos();
				clasRupturaPerm.setRuptura(new BigDecimal(0));
				if((informeRecibos.getSentido()).equals(ConstantsInf.ORDENACION_ASC)){
					clasRupturaPerm.setSentido(ConstantsInf.COD_ORDENACION_ASC);
				}else{
					if((informeRecibos.getSentido()).equals(ConstantsInf.ORDENACION_DESC)){
						clasRupturaPerm.setSentido(ConstantsInf.COD_ORDENACION_DESC);
					}else{
						dato.setClasificacionRupturaCamposPermitidoses(null);
						break;
					}
				}
				clasRupturaPerm.setDatoInformes(dato);
				aux.add(clasRupturaPerm);
				dato.setClasificacionRupturaCamposPermitidoses(aux);
			}
			//si no deberemos borrar cualquier ordenacion anterior
			else{
				dato.setClasificacionRupturaCamposPermitidoses(null);
			}
		}
		logger.debug("end - setOrdenacionParaInformeRecibos");
		return informe;
	}
	
	/**
	 * Metodo que aï¿½ade la ordenacion para el informe (versión 2015+)
	 * @param informeRecibos
	 * @param informe
	 * @return
	 */
	private Informe setOrdenacionParaInformeRecibos2015(InformeRecibos2015 informeRecibos, Informe informe) {
		logger.debug("setOrdenacionParaInformeRecibos - init");
		
		
		for(DatoInformes dato: informe.getDatoInformeses()){
			if((informeRecibos.getCampoOrdenar()).equals(dato.getAbreviado())){
				Set<ClasificacionRupturaCamposPermitidos> aux = new HashSet<ClasificacionRupturaCamposPermitidos>();
				ClasificacionRupturaCamposPermitidos clasRupturaPerm = new ClasificacionRupturaCamposPermitidos();
				clasRupturaPerm.setRuptura(new BigDecimal(0));
				if((informeRecibos.getSentido()).equals(ConstantsInf.ORDENACION_ASC)){
					clasRupturaPerm.setSentido(ConstantsInf.COD_ORDENACION_ASC);
				}else{
					if((informeRecibos.getSentido()).equals(ConstantsInf.ORDENACION_DESC)){
						clasRupturaPerm.setSentido(ConstantsInf.COD_ORDENACION_DESC);
					}else{
						dato.setClasificacionRupturaCamposPermitidoses(null);
						break;
					}
				}
				clasRupturaPerm.setDatoInformes(dato);
				aux.add(clasRupturaPerm);
				dato.setClasificacionRupturaCamposPermitidoses(aux);
			}
			//si no deberemos borrar cualquier ordenacion anterior
			else{
				dato.setClasificacionRupturaCamposPermitidoses(null);
			}
		}
		logger.debug("end - setOrdenacionParaInformeRecibos");
		return informe;
	}

	/**	DAA 02/102013
	 * 	Metodo que aï¿½ade el filtro del informe para el caso de la consulta de recibos
	 * @param informe 
	 * @return 
	 */
	private Informe setFiltroParaInformeRecibos(InformeRecibos informeRecibos, Informe informe) throws BusinessException{
		
		logger.debug("setFiltroParaInformeRecibos - init");
		if(!informeRecibos.getMapaFiltro().isEmpty()){
			
			Set<String> keysFiltro = informeRecibos.getMapaFiltro().keySet();
			
			for (String key : keysFiltro) {
				String[] valores = informeRecibos.getMapaFiltro().get(key);
				
				for(DatoInformes dato: informe.getDatoInformeses()){
					try{
						if(dato.getCamposPermitidos().getVistaCampo().getNombreRealCampo().equals(key)){
							logger.debug("setFiltroParaInformeRecibos - "+ key);
							CondicionCamposPermitidos aux = new CondicionCamposPermitidos();
							aux.setDatoInformes(dato);
							
							if(aux.getOperadorCamposPermitido() == null){
								OperadorCamposPermitido opCamposPermitido = new OperadorCamposPermitido(); 
								aux.setOperadorCamposPermitido(opCamposPermitido);
							}	
							aux.getOperadorCamposPermitido().setIdoperador(new BigDecimal(valores[0]));
							logger.debug("setFiltroParaInformeRecibos - Operador = "+ valores[0]);
							aux.setCondicion(valores[1]);
							logger.debug("setFiltroParaInformeRecibos - "+ valores[1]);
							dato.getCondicionCamposPermitidoses().add(aux);
							
						}else{
							
							
						}
					}
					catch (NumberFormatException e) {
						logger.error("Error al convertir a numero estableciendo los campos para el filtro", e);
						throw new BusinessException();
					}	
					catch (Exception e1) {
						logger.error("Error generico al establecer los campos para el filtro", e1);
						throw new BusinessException();
					}
				}
			}			
		}
		logger.debug("end - setFiltroParaInformeRecibos");
		return informe;
	}
	
	
	/**
	 * Metodo que aï¿½ade el filtro del informe para el caso de la consulta de recibos (versión 2015+)
	 * @param informeRecibos
	 * @param informe
	 * @return
	 * @throws BusinessException
	 */
	private Informe setFiltroParaInformeRecibos2015(InformeRecibos2015 informeRecibos, Informe informe) throws BusinessException{
		
		logger.debug("setFiltroParaInformeRecibos2015 - init");
		if(!informeRecibos.getMapaFiltro().isEmpty()){
			
			Set<String> keysFiltro = informeRecibos.getMapaFiltro().keySet();
			
			for (String key : keysFiltro) {
				String[] valores = informeRecibos.getMapaFiltro().get(key);
				
				for(DatoInformes dato: informe.getDatoInformeses()){
					try{
						if(dato.getCamposPermitidos().getVistaCampo().getNombreRealCampo().equals(key)){
							logger.debug("setFiltroParaInformeRecibos2015 - "+ key);
							CondicionCamposPermitidos aux = new CondicionCamposPermitidos();
							aux.setDatoInformes(dato);
							
							if(aux.getOperadorCamposPermitido() == null){
								OperadorCamposPermitido opCamposPermitido = new OperadorCamposPermitido(); 
								aux.setOperadorCamposPermitido(opCamposPermitido);
							}	
							aux.getOperadorCamposPermitido().setIdoperador(new BigDecimal(valores[0]));
							logger.debug("setFiltroParaInformeRecibos2015 - Operador = "+ valores[0]);
							aux.setCondicion(valores[1]);
							logger.debug("setFiltroParaInformeRecibos2015 - "+ valores[1]);
							dato.getCondicionCamposPermitidoses().add(aux);
							
						}else{
							
							
						}
					}
					catch (NumberFormatException e) {
						logger.error("Error al convertir a numero estableciendo los campos para el filtro", e);
						throw new BusinessException();
					}	
					catch (Exception e1) {
						logger.error("Error generico al establecer los campos para el filtro", e1);
						throw new BusinessException();
					}
				}
			}			
		}
		logger.debug("end - setFiltroParaInformeRecibos2015");
		return informe;
	}

	/**	DAA 02/102013
	 * 	Metodo que elimina los campos del informe para el caso de la consulta de recibos
	 * @param informe 
	 * @return 
	 */
	private Informe setCamposParaInformeRecibos(InformeRecibos informeRecibos, Informe informe) {
	
		logger.debug("setCamposParaInformeRecibos - init ");
		Set<String> camposFinalesAMostrar = new HashSet<String>();
		Set<DatoInformes> aux = new HashSet<DatoInformes>();
		
		if((RESUMEN).equals(informeRecibos.getDatosDe())){
			camposFinalesAMostrar.addAll(informeRecibos.getCamposComunesMostrar());
			
			if(informeRecibos.getDatosDe().equals(RESUMEN)){
				camposFinalesAMostrar.addAll(informeRecibos.getCamposResumenMostrar());
			}else{
				camposFinalesAMostrar.addAll(informeRecibos.getCamposDetalleMostrar());
			}
			
		}else{
			camposFinalesAMostrar.addAll(informeRecibos.getCamposDetalleMostrar());
			camposFinalesAMostrar.addAll(informeRecibos.getCamposComunesMostrar());
		}

		for(DatoInformes dato: informe.getDatoInformeses()){
			String datoInforme = dato.getCamposPermitidos().getVistaCampo().getNombre();
			//si el dato esta en el set de campos lo meto en la lista auxiliar que ira en el informe posteriormente
			if(camposFinalesAMostrar.contains(datoInforme)){
				logger.debug("setCamposParaInformeRecibos - set campo: "+datoInforme);
				aux.add(dato);
			}else{
				if(informeRecibos.getMapaFiltro().containsKey(datoInforme)){
					dato.setVisible(false);
					logger.debug("setCamposParaInformeRecibos - set campo invisible: "+datoInforme);
					aux.add(dato);
				}
			}
		}
		informe.setDatoInformeses(aux);
		logger.debug("end - setCamposParaInformeRecibos");
		return informe;
	}
	

	/**
	 * Metodo que elimina los campos del informe para el caso de la consulta de recibos (versión 2015+)
	 * @param informeRecibos
	 * @param informe
	 * @return
	 */
	private Informe setCamposParaInformeRecibos2015(InformeRecibos2015 informeRecibos, Informe informe) {
	
		logger.debug("setCamposParaInformeRecibos2015 - init");
		Set<String> camposFinalesAMostrar = new HashSet<String>();
		Set<DatoInformes> aux = new HashSet<DatoInformes>();
		
		camposFinalesAMostrar.addAll(informeRecibos.getCamposComunesMostrar());

		for(DatoInformes dato: informe.getDatoInformeses()){
			String datoInforme = dato.getCamposPermitidos().getVistaCampo().getNombre();
			//si el dato esta en el set de campos lo meto en la lista auxiliar que ira en el informe posteriormente
			if(camposFinalesAMostrar.contains(datoInforme)){
				logger.debug("setCamposParaInformeRecibos2015 - set campo: "+datoInforme);
				aux.add(dato);
			}else{
				if(informeRecibos.getMapaFiltro().containsKey(datoInforme)){
					dato.setVisible(false);
					logger.debug("setCamposParaInformeRecibos2015 - set campo invisible: "+datoInforme);
					aux.add(dato);
				}
			}
		}
		informe.setDatoInformeses(aux);
		logger.debug("end - setCamposParaInformeRecibos2015");
		return informe;
	}
	
	/**	TMR 
	 * 	Metodo que elimina los campos del informe para el caso de la consulta de comisioes
	 * @param informe 
	 * @return 
	 */
	private Informe setCamposParaInformecomisiones(InformeComisiones informecomisiones, Informe informe) {
	
		logger.debug("setCamposParainformecomisiones - init");
		Set<String> camposFinalesAMostrar = new HashSet<String>();
		Set<DatoInformes> aux = new HashSet<DatoInformes>();
		
		if((RESUMEN).equals(informecomisiones.getDatosDe())){
			camposFinalesAMostrar.addAll(informecomisiones.getCamposComunesMostrar());
			
			if(informecomisiones.getDatosDe().equals(RESUMEN)){
				camposFinalesAMostrar.addAll(informecomisiones.getCamposResumenMostrar());
			}else{
				camposFinalesAMostrar.addAll(informecomisiones.getCamposDetalleMostrar());
			}
			
		}else{
			camposFinalesAMostrar.addAll(informecomisiones.getCamposDetalleMostrar());
			camposFinalesAMostrar.addAll(informecomisiones.getCamposComunesMostrar());
		}

		for(DatoInformes dato: informe.getDatoInformeses()){
			String datoInforme = dato.getCamposPermitidos().getVistaCampo().getNombre();
			//si el dato esta en el set de campos lo meto en la lista auxiliar que ira en el informe posteriormente
			if(camposFinalesAMostrar.contains(datoInforme)){
				logger.debug("setCamposParainformecomisiones - set campo: "+datoInforme);
				aux.add(dato);
			}else{
				if(informecomisiones.getMapaFiltro().containsKey(datoInforme)){
					dato.setVisible(false);
					logger.debug("setCamposParainformecomisiones - set campo invisible: "+datoInforme);
					aux.add(dato);
				}
			}
		}
		informe.setDatoInformeses(aux);
		logger.debug("end - setCamposParainformecomisiones");
		return informe;
	}
	
	/**
	 * Metodo que elimina los campos del informe para el caso de la consulta de comisiones (versión 2015+)
	 * @param informecomisiones
	 * @param informe
	 * @return
	 */
	private Informe setCamposParaInformeComisiones2015(InformeComisiones2015 informeComisiones, Informe informe) {
		
		logger.debug("setCamposParaInformeComisiones2015 - init");
		Set<String> camposFinalesAMostrar = new HashSet<String>();
		Set<DatoInformes> aux = new HashSet<DatoInformes>();
		logger.debug("getCamposComunesMostrar - " + informeComisiones.getCamposComunesMostrar());
		camposFinalesAMostrar.addAll(informeComisiones.getCamposComunesMostrar());

		for(DatoInformes dato: informe.getDatoInformeses()){
			String datoInforme = dato.getCamposPermitidos().getVistaCampo().getNombre();
			logger.debug("datoInforme - " + datoInforme);
			//si el dato esta en el set de campos lo meto en la lista auxiliar que ira en el informe posteriormente
			if(camposFinalesAMostrar.contains(datoInforme)){
				logger.debug("setCamposParaInformeComisiones2015 - set campo: "+datoInforme);
				aux.add(dato);
			}else{
				if(informeComisiones.getMapaFiltro().containsKey(datoInforme)){
					dato.setVisible(false);
					logger.debug("setCamposParaInformeComisiones2015 - set campo invisible: "+datoInforme);
					aux.add(dato);
				}
			}
		}
		informe.setDatoInformeses(aux);
		logger.debug("end - setCamposParaInformeComisiones2015");
		return informe;
	}
	
	/**	TMR
	 * 	Metodo que aï¿½ade el filtro del informe para el caso de la consulta de comisiones
	 * @param informe 
	 * @return 
	 */
	private Informe setFiltroParaInformeComisiones(InformeComisiones informeComisiones, Informe informe) throws BusinessException{
		
		logger.debug("setFiltroParainformeComisiones - init");
		if(!informeComisiones.getMapaFiltro().isEmpty()){
			
			Set<String> keysFiltro = informeComisiones.getMapaFiltro().keySet();
			
			for (String key : keysFiltro) {
				String[] valores = informeComisiones.getMapaFiltro().get(key);
				
				for(DatoInformes dato: informe.getDatoInformeses()){
					try{
						if(dato.getCamposPermitidos().getVistaCampo().getNombreRealCampo().equals(key)){
							logger.debug("setFiltroParainformeComisiones -  "+ key);
							CondicionCamposPermitidos aux = new CondicionCamposPermitidos();
							aux.setDatoInformes(dato);
							
							if(aux.getOperadorCamposPermitido() == null){
								OperadorCamposPermitido opCamposPermitido = new OperadorCamposPermitido(); 
								aux.setOperadorCamposPermitido(opCamposPermitido);
							}	
							aux.getOperadorCamposPermitido().setIdoperador(new BigDecimal(valores[0]));
							logger.debug("setFiltroParainformeComisiones - Operador = "+ valores[0]);
							aux.setCondicion(valores[1]);
							logger.debug("setFiltroParainformeComisiones  - "+ valores[1]);
							dato.getCondicionCamposPermitidoses().add(aux);
							
						}else{
							
							
						}
					}
					catch (NumberFormatException e) {
						logger.error("Error al convertir a numero estableciendo los campos para el filtro", e);
						throw new BusinessException();
					}	
					catch (Exception e1) {
						logger.error("Error generico al establecer los campos para el filtro", e1);
						throw new BusinessException();
					}
				}
			}			
		}
		logger.debug("end - setFiltroParainformeComisiones");
		return informe;
	}
	
	/**
	 * Metodo que añade el filtro del informe para el caso de la consulta de comisiones (versión 2015+)
	 * @param informeComisiones
	 * @param informe
	 * @return
	 * @throws BusinessException
	 */
	private Informe setFiltroParaInformeComisiones2015(InformeComisiones2015 informeComisiones, Informe informe) throws BusinessException{
		
		logger.debug("setFiltroParainformeComisiones - init");
		if(!informeComisiones.getMapaFiltro().isEmpty()){
			
			Set<String> keysFiltro = informeComisiones.getMapaFiltro().keySet();
			
			for (String key : keysFiltro) {
				String[] valores = informeComisiones.getMapaFiltro().get(key);
				
				for(DatoInformes dato: informe.getDatoInformeses()){
					try{
						if(dato.getCamposPermitidos().getVistaCampo().getNombreRealCampo().equals(key)){
							logger.debug(" setFiltroParainformeComisiones - "+ key);
							CondicionCamposPermitidos aux = new CondicionCamposPermitidos();
							aux.setDatoInformes(dato);
							
							if(aux.getOperadorCamposPermitido() == null){
								OperadorCamposPermitido opCamposPermitido = new OperadorCamposPermitido(); 
								aux.setOperadorCamposPermitido(opCamposPermitido);
							}	
							aux.getOperadorCamposPermitido().setIdoperador(new BigDecimal(valores[0]));
							logger.debug("setFiltroParainformeComisiones - Operador = "+ valores[0]);
							aux.setCondicion(valores[1]);
							logger.debug("setFiltroParainformeComisiones - "+ valores[1]);
							dato.getCondicionCamposPermitidoses().add(aux);
							
						}else{
							
							
						}
					}
					catch (NumberFormatException e) {
						logger.error("Error al convertir a numero estableciendo los campos para el filtro", e);
						throw new BusinessException();
					}	
					catch (Exception e1) {
						logger.error("Error generico al establecer los campos para el filtro", e1);
						throw new BusinessException();
					}
				}
			}			
		}
		logger.debug("end - setFiltroParainformeComisiones");
		return informe;
	}

	/**	DAA 17/10/2013
	 * 	Metodo que aï¿½ade la ordenacion para el informe
	 * @param informe 
	 * @return 
	 */
	private Informe setOrdenacionParaInformeComisiones(InformeComisiones informeComisiones, Informe informe) {
		logger.debug("setOrdenacionParainformeComisiones - init");
		
		
		for(DatoInformes dato: informe.getDatoInformeses()){
			if((informeComisiones.getCampoOrdenar()).equals(dato.getAbreviado())){
				Set<ClasificacionRupturaCamposPermitidos> aux = new HashSet<ClasificacionRupturaCamposPermitidos>();
				ClasificacionRupturaCamposPermitidos clasRupturaPerm = new ClasificacionRupturaCamposPermitidos();
				clasRupturaPerm.setRuptura(new BigDecimal(0));
				if((informeComisiones.getSentido()).equals(ConstantsInf.ORDENACION_ASC)){
					clasRupturaPerm.setSentido(ConstantsInf.COD_ORDENACION_ASC);
				}else{
					if((informeComisiones.getSentido()).equals(ConstantsInf.ORDENACION_DESC)){
						clasRupturaPerm.setSentido(ConstantsInf.COD_ORDENACION_DESC);
					}else{
						dato.setClasificacionRupturaCamposPermitidoses(null);
						break;
					}
				}
				clasRupturaPerm.setDatoInformes(dato);
				aux.add(clasRupturaPerm);
				dato.setClasificacionRupturaCamposPermitidoses(aux);
			}
			//si no deberemos borrar cualquier ordenacion anterior
			else{
				dato.setClasificacionRupturaCamposPermitidoses(null);
			}
		}
		logger.debug("end - setOrdenacionParainformeComisiones");
		return informe;
	}
	
	/**
	 * Metodo que añade la ordenacion para el informe (versión 2015+)
	 * @param informeComisiones
	 * @param informe
	 * @return
	 */
	private Informe setOrdenacionParaInformeComisiones2015(InformeComisiones2015 informeComisiones, Informe informe) {
		logger.debug("setOrdenacionParainformeComisiones - init");
		
		
		for(DatoInformes dato: informe.getDatoInformeses()){
			if((informeComisiones.getCampoOrdenar()).equals(dato.getAbreviado())){
				Set<ClasificacionRupturaCamposPermitidos> aux = new HashSet<ClasificacionRupturaCamposPermitidos>();
				ClasificacionRupturaCamposPermitidos clasRupturaPerm = new ClasificacionRupturaCamposPermitidos();
				clasRupturaPerm.setRuptura(new BigDecimal(0));
				if((informeComisiones.getSentido()).equals(ConstantsInf.ORDENACION_ASC)){
					clasRupturaPerm.setSentido(ConstantsInf.COD_ORDENACION_ASC);
				}else{
					if((informeComisiones.getSentido()).equals(ConstantsInf.ORDENACION_DESC)){
						clasRupturaPerm.setSentido(ConstantsInf.COD_ORDENACION_DESC);
					}else{
						dato.setClasificacionRupturaCamposPermitidoses(null);
						break;
					}
				}
				clasRupturaPerm.setDatoInformes(dato);
				aux.add(clasRupturaPerm);
				dato.setClasificacionRupturaCamposPermitidoses(aux);
			}
			//si no deberemos borrar cualquier ordenacion anterior
			else{
				dato.setClasificacionRupturaCamposPermitidoses(null);
			}
		}
		logger.debug("end - setOrdenacionParainformeComisiones");
		return informe;
	}
	
	
	
	/**	DAA 02/102013
	 * 	Metodo que anhade los campos del detalle para el informe previamente generado 
	 * @param informeRecibosBean 
	 * @return 
	 */
	public InformeRecibos setInformeRecibosBeanDetalle(InformeRecibos informeRecibosBean, String stringRegistro){
		
		logger.debug(" setCamposParaInformeRecibos - init");
		Map<String, String> filtro = new HashMap<String, String>();
		informeRecibosBean.setDatosDe(DETALLE);
		
		//descomponemos la cadena del registro 
		String[] aux = stringRegistro.split(",");
		for(int i=0; i<aux.length; i++ ){
			String[] reg = aux[i].split("=");
			filtro.put(reg[0], reg[1]);
		}

		try {
			//montamos los set de cada propiedad que vendra en las keys
			for (String key : filtro.keySet()) {
				if(mapaSettersCampos.containsKey(key)){
					String metodo = mapaSettersCampos.get(key);
					logger.debug( metodo );
					
					Method method = null;					
					method = informeRecibosBean.getClass().getMethod(metodo, String.class);
					method.invoke(informeRecibosBean,filtro.get(key));
					
					//set de las condiciones (en este caso siempre sera ("Igual a") )
					String metodoCondi = mapaSettersCondi.get(metodo);
					logger.debug( metodoCondi );
					method = informeRecibosBean.getClass().getMethod(metodoCondi, String.class);
					method.invoke(informeRecibosBean,new Integer(ConstantsInf.COD_OPERADOR_BD_IGUAL).toString());
				}
			}
			
		} catch (Exception e) {
			logger.error("Fallo al crear los set para el detalle de InformeRecibosBean -  ", e);
		}
		
		logger.debug("end - setInformeRecibosBeanDetalle ");
		return informeRecibosBean;
	}
	
	/**
	 * Metodo que anhade los campos del detalle para el informe previamente generado (versión 2015)
	 * @param informeRecibosBean
	 * @param stringRegistro
	 * @return
	 */
	public InformeRecibos2015 setInformeRecibosBeanDetalle2015(InformeRecibos2015 informeRecibosBean, String stringRegistro){
		
		logger.debug("setCamposParaInformeRecibos -  init");
		Map<String, String> filtro = new HashMap<String, String>();
		informeRecibosBean.setDatosDe(DETALLE);
		
		//descomponemos la cadena del registro 
		String[] aux = stringRegistro.split(",");
		for(int i=0; i<aux.length; i++ ){
			String[] reg = aux[i].split("=");
			filtro.put(reg[0], reg[1]);
		}

		try {
			//montamos los set de cada propiedad que vendra en las keys
			for (String key : filtro.keySet()) {
				if(mapaSettersCampos.containsKey(key)){
					String metodo = mapaSettersCampos.get(key);
					logger.debug( metodo );
					
					Method method = null;					
					method = informeRecibosBean.getClass().getMethod(metodo, String.class);
					method.invoke(informeRecibosBean,filtro.get(key));
					
					//set de las condiciones (en este caso siempre sera ("Igual a") )
					String metodoCondi = mapaSettersCondi.get(metodo);
					logger.debug( metodoCondi );
					method = informeRecibosBean.getClass().getMethod(metodoCondi, String.class);
					method.invoke(informeRecibosBean,new Integer(ConstantsInf.COD_OPERADOR_BD_IGUAL).toString());
				}
			}
			
		} catch (Exception e) {
			logger.error("Fallo al crear los set para el detalle de InformeRecibosBean  - ", e);
		}
		
		logger.debug("end -  setInformeRecibosBeanDetalle");
		return informeRecibosBean;
	}
	
	
	public InformeComisiones setInformeComisionesBeanDetalle(InformeComisiones informeComisionesBean, String stringRegistro){
		
		logger.debug("setCamposParaInformeRecibos - init");
		Map<String, String> filtro = new HashMap<String, String>();
		informeComisionesBean.setDatosDe(DETALLE);
		
		//descomponemos la cadena del registro 
		String[] aux = stringRegistro.split(",");
		for(int i=0; i<aux.length; i++ ){
			String[] reg = aux[i].split("=");
			filtro.put(reg[0], reg[1]);
		}

		try {
			//montamos los set de cada propiedad que vendra en las keys
			for (String key : filtro.keySet()) {
				if(mapaSettersCampos.containsKey(key)){
					String metodo = mapaSettersCampos.get(key);
					logger.debug( metodo );
					
					Method method = null;					
					method = informeComisionesBean.getClass().getMethod(metodo, String.class);
					method.invoke(informeComisionesBean,filtro.get(key));
					
					//set de las condiciones (en este caso siempre sera ("Igual a") )
					String metodoCondi = mapaSettersCondi.get(metodo);
					logger.debug( metodoCondi );
					method = informeComisionesBean.getClass().getMethod(metodoCondi, String.class);
					method.invoke(informeComisionesBean,new Integer(ConstantsInf.COD_OPERADOR_BD_IGUAL).toString());
				}
			}
			
		} catch (Exception e) {
			logger.error(" Fallo al crear los set para el detalle de InformeRecibosBean - ", e);
		}
		
		logger.debug("end - setInformeRecibosBeanDetalle");
		return informeComisionesBean;
	}
	
	public InformeComisiones2015 setInformeComisionesBeanDetalle2015(InformeComisiones2015 informeComisionesBean, String stringRegistro){
		
		logger.debug("setInformeComisionesBeanDetalle2015 - init");
		Map<String, String> filtro = new HashMap<String, String>();
		informeComisionesBean.setDatosDe(DETALLE);
		
		//descomponemos la cadena del registro 
		String[] aux = stringRegistro.split(",");
		for(int i=0; i<aux.length; i++ ){
			String[] reg = aux[i].split("=");
			filtro.put(reg[0], reg[1]);
		}

		try {
			//montamos los set de cada propiedad que vendra en las keys
			for (String key : filtro.keySet()) {
				if(mapaSettersCampos.containsKey(key)){
					String metodo = mapaSettersCampos.get(key);
					logger.debug( metodo );
					
					Method method = null;					
					method = informeComisionesBean.getClass().getMethod(metodo, String.class);
					method.invoke(informeComisionesBean,filtro.get(key));
					
					//set de las condiciones (en este caso siempre sera ("Igual a") )
					String metodoCondi = mapaSettersCondi.get(metodo);
					logger.debug( metodoCondi );
					method = informeComisionesBean.getClass().getMethod(metodoCondi, String.class);
					method.invoke(informeComisionesBean,new Integer(ConstantsInf.COD_OPERADOR_BD_IGUAL).toString());
				}
			}
			
		} catch (Exception e) {
			logger.error("Fallo al crear los set para el detalle de InformeRecibosBean - ", e);
		}
		
		logger.debug("end - setInformeComisionesBeanDetalle2015");
		return informeComisionesBean;
	}


	/** DAA 20/02/2013 A partir de una select sacamos el numero de registros que devuelve
	 *  @param sql
	 * 
	 */
	public int getCountNumRegistros(String sql) {
		logger.debug("**@@** MtoInformesService - getCountNumRegistro [INIT]");
		logger.debug("**@@** Valor de sql: "+sql);
		int numRegistros = 0;
		String consulta = sql.substring(sql.lastIndexOf("FROM"),sql.length());
		consulta = "SELECT COUNT(*) " + consulta;
		numRegistros = mtoInformeDao.getCountNumRegistros(consulta);		
		return numRegistros;
	}

	/**
	 * Anhade las restricciones correspondientes a la tabla implicada en la consulta y al perfil del usuario que lanza el informe
	 * @param usuario Objeto que encapsula la informacion del usuario
	 * @param setVistas Conjunto de tablas implicadas en la consulta
	 */
	private String addRestriccionesPorPerfilYTabla(final Usuario usuario, final TreeSet<Vista> setVistas) {
		
		StringBuilder sqlPerfil = new StringBuilder("");
		
		// Recorre las tablas implicadas en la consulta
		for (Vista vista : setVistas) {
			
			RelVistaPerfil filtroTabla = null;
			
			// Obtiene los campos por los que hay que filtrar esta tabla 
			for (RelVistaPerfil relVistaPerfil : vista.getRelVistaPerfils()) {
				// Nos quedamos con el registro correspondiente al perfil del usuario conectado
				// Si el usuario es EXTERNO
				if (usuario.isUsuarioExterno()) {
					// Si es perfil 1 Externo
					if (relVistaPerfil.getPerfil() != null &&
						relVistaPerfil.getPerfil().equals(Constants.PERFIL_1_EXT_INFORMES) &&
						usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES)) {
						filtroTabla = relVistaPerfil;
						break;
					}
					// Si es perfil 1 Externo
					else if (relVistaPerfil.getPerfil() != null &&
							 relVistaPerfil.getPerfil().equals(Constants.PERFIL_3_EXT_INFORMES) &&
							 usuario.getPerfil().equals(Constants.PERFIL_USUARIO_OFICINA)) {
						filtroTabla = relVistaPerfil;
						break;
					}
				}
				// Si el usuario es INTERNO
				else {
					if (relVistaPerfil.getPerfil() != null && usuario.getPerfil().contains(relVistaPerfil.getPerfil().toString())) {
						filtroTabla = relVistaPerfil;
						break;
					}
				}
			}
			
			// Se compone el filtro dependiendo de la configuracon del objeto 'filtroTabla'
			if (filtroTabla != null) {
				// Si se ha configurado que se filtre por el campo CODENTIDAD
				if (ConstantsInf.FILTRO_SI.equals(filtroTabla.getFiltroEntidad())) {
					// Si el usuario es perfil 5 la entidad debe estar incluida en el grupo de entidades del usuario
					if (Constants.PERFIL_USUARIO_SEMIADMINISTRADOR.equals(usuario.getPerfil())) {
						sqlPerfil.append(QUERY_AND + vista.getNombreReal() + "$." + CODENTIDAD + " IN " + StringUtils.toValoresSeparadosXComas(usuario.getListaCodEntidadesGrupo(), false, true));  
					}
					// Si no, se filtra por la entidad del usuario
					else {
						sqlPerfil.append(QUERY_AND + vista.getNombreReal() + "$." + CODENTIDAD + "=" + usuario.getOficina().getEntidad().getCodentidad());
					}
				}
				
				// Si se ha configurado que se filtre por el campo OFICINA
				if (ConstantsInf.FILTRO_SI.equals(filtroTabla.getFiltroOficina())) {
					if(Constants.PERFIL_USUARIO_JEFE_ZONA.equals(usuario.getPerfil())){
						if(null!=usuario.getListaCodOficinasGrupo() && !(usuario.getListaCodOficinasGrupo()).isEmpty()){
							sqlPerfil.append(QUERY_AND + vista.getNombreReal() + "$." + OFICINA + " IN " + StringUtils.toValoresSeparadosXComas(usuario.getListaCodOficinasGrupo(), false, true));  

						}else{
							sqlPerfil.append(QUERY_AND + vista.getNombreReal() + "$." + OFICINA + "=" + usuario.getOficina().getId().getCodoficina());
						}
					}
				}
				// Si se ha configurado que se filtre por el campo CODUSUARIO
				if (ConstantsInf.FILTRO_SI.equals(filtroTabla.getFiltroUsuario())) {
					sqlPerfil.append(QUERY_AND + vista.getNombreReal() + "$." + CODUSUARIO + "='" + usuario.getCodusuario() + "'");
				}
				// Si se ha configurado que se filtre por el campo CODENTMED y el usuario tiene informada la entidad mediadora 
				if (ConstantsInf.FILTRO_SI.equals(filtroTabla.getFiltroEntMed()) &&
					usuario.getSubentidadMediadora() != null && usuario.getSubentidadMediadora().getId() != null &&
					usuario.getSubentidadMediadora().getId().getCodentidad() != null) {
					sqlPerfil.append(QUERY_AND + vista.getNombreReal() + "$." + CODENTMED + "=" + usuario.getSubentidadMediadora().getId().getCodentidad());
				}
				// Si se ha configurado que se filtre por el campo CODSUBENTMED y el usuario tiene informada la subentidad mediadora 
				if (ConstantsInf.FILTRO_SI.equals(filtroTabla.getFiltroSubEntMed()) &&
					usuario.getSubentidadMediadora() != null && usuario.getSubentidadMediadora().getId() != null &&
					usuario.getSubentidadMediadora().getId().getCodsubentidad() != null) {
					sqlPerfil.append(QUERY_AND + vista.getNombreReal() + "$." + CODSUBENTMED + "=" + usuario.getSubentidadMediadora().getId().getCodsubentidad());
				}
				// Si se ha configurado que se filtre por el campo DELEGACION y el usuario tiene informada dicho campo 
				if (ConstantsInf.FILTRO_SI.equals(filtroTabla.getFiltroDelegacion()) &&	usuario.getDelegacion() != null) {
					sqlPerfil.append(QUERY_AND + vista.getNombreReal() + "$." + DELEGACION + "=" + usuario.getDelegacion());
				}
			}
		}
		
		return sqlPerfil.toString();
	}

	/**
	 * Obtiene el nombre del esquema contra el que se lanzara la consulta en la generacion del informe del properties
	 * @return
	 * @throws BusinessException 
	 */
	private String cargarEsquemaConsultaInforme() throws BusinessException {
		
		String aux = null;
		
		try {
			aux = bundle.getString(ConstantsInf.ESQUEMA_GENERACION_INFORMES);
		}
		catch (NullPointerException e1) {
			logger.error("MtoInformeService.cargarEsquemaConsultaInforme - La clave a buscar en el properties es nula.", e1);
		}
		catch (MissingResourceException e2) {
			logger.error("MtoInformeService.cargarEsquemaConsultaInforme - La clave '" + ConstantsInf.ESQUEMA_GENERACION_INFORMES + "' no existe en el properties.", e2);
		}
		catch (ClassCastException e3) {
			logger.error("MtoInformeService.cargarEsquemaConsultaInforme - La valor de la clave a buscar no es una cadena.", e3);
		}
		// Si ha habido algun error, se escala la excepcion para cancelar la generacion del informe y mostrar el error al usuario
		if (aux == null) throw new BusinessException();
		
		return aux;
	}
	
	/** DAA 21/02/2013 Obtiene el valor del max registros del properties en la generacion del informe
	 * 
	 */ 
	public int getConstantMaxRegistros()throws BusinessException {
		
		int max = 0;
		
		try {
			String maxReg = bundle.getString("maxRegistros.informe");
			max = Integer.parseInt(maxReg);
		}
		catch (NullPointerException e1) {
			logger.error("MtoInformeService.getNumMaxRegistros - La clave a buscar en el properties es nula.", e1);
		}
		catch (MissingResourceException e2) {
			logger.error("MtoInformeService.getNumMaxRegistros - La clave 'maxRegistros.informe' no existe en el properties.", e2);
		}
		catch (ClassCastException e3) {
			logger.error("MtoInformeService.getNumMaxRegistros - El valor de la clave a buscar no es una cadena.", e3);
		}
		catch (NumberFormatException e4) {
			logger.error("MtoInformeService.getNumMaxRegistros - El maximo de registros no es numerico.", e4);
		}
		// Si ha habido algun error, se escala la excepcion para cancelar la generacion del informe y mostrar el error al usuario
		if (max == 0) throw new BusinessException();
		
		return max;
	}
	
	
	/**
	 * Elimina la ultima coma en un String
	 * @param sql
	 * @return
	 */
	private String deleteLastChar(String cadena){
		String temp = cadena.substring(cadena.length()-1,cadena.length());
		if (temp.equals(",")){
			cadena = cadena.substring(0,cadena.length()-1);
		}
		return cadena;
	}
	
	private String getCondicion(int idOperador, String condicion, BigDecimal formato, BigDecimal tipo){
		String[] condArr = condicion.split(",");
		String value = "";
		if (formato != null && (formato.compareTo(new BigDecimal(ConstantsInf.COD_FORMATO_FECHA_DDMMYYYY))== 0 ||
				formato.compareTo(new BigDecimal(ConstantsInf.COD_FORMATO_FECHA_YYYYMMDD))== 0)){
			if (idOperador == ConstantsInf.COD_OPERADOR_BD_ENTRE){
				SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
				try {
					if (condArr.length > 1){
						Date fecha1 = sdf.parse(condArr[0]);
						Date fecha2=sdf.parse(condArr[1]);
						if (fecha1.compareTo(fecha2) >0){
							String fecTemp = condArr[1];
							condArr[1] = condArr[0];
							condArr[0] = fecTemp;
						}
					}
				} catch (ParseException e) {
					logger.error("Excepcion : MtoInformeService - getCondicion", e);
				}
			}
			String fec1 = "TO_DATE('";
			String fec2 = "','DD/MM/YYYY')";
			condicion = "";
			for (int i=0;i<condArr.length;i++){
				condArr[i] = fec1+condArr[i]+fec2;
				condicion +=condArr[i]+",";
			}
			condicion = deleteLastChar(condicion);
		}
		
		
    	switch (idOperador) {
    		// Si el campo es alfanumerico se anhaden comillas simples para que funcione correctamente en la consulta
    		case ConstantsInf.COD_OPERADOR_BD_IGUAL: value = " = " + ((new BigDecimal (ConstantsInf.CAMPO_TIPO_TEXTO).equals(tipo)) ? '\'' + condicion + '\'' : condicion); break;
    		case ConstantsInf.COD_OPERADOR_BD_MAYOR_QUE: value = " > "+condicion; break;
			case ConstantsInf.COD_OPERADOR_BD_MAYOR_IGUAL_QUE: value = " >= "+condicion; break;
			case ConstantsInf.COD_OPERADOR_BD_MENOR_QUE: value = " < "+condicion; break;
			case ConstantsInf.COD_OPERADOR_BD_MENOR_IGUAL_QUE: value = " <= "+condicion; break;
			case ConstantsInf.COD_OPERADOR_BD_ENTRE: 
				if (tipo != null && tipo.compareTo(new BigDecimal(Integer.toString(ConstantsInf.CAMPO_TIPO_NUMERICO))) == 0){
					if (condArr.length > 1){
						BigDecimal num1 = new BigDecimal(condArr[0]);
						BigDecimal num2 = new BigDecimal(condArr[1]);
						if (num1.compareTo(num2) >0){
							BigDecimal numTemp = num2;
							condArr[1] = num1.toString();
							condArr[0] =numTemp.toString();
						}
					}
				}
				value = " BETWEEN "+condArr[0]+QUERY_AND+condArr[1]; break;
			case ConstantsInf.COD_OPERADOR_BD_CONTENIDO_EN: 
				if (tipo != null && tipo.compareTo(new BigDecimal(Integer.toString(ConstantsInf.CAMPO_TIPO_TEXTO))) == 0){
					condicion = "";
					for (int i=0;i<condArr.length;i++){
						condArr[i] = "'"+condArr[i].toUpperCase()+"'";
						condicion +=condArr[i]+",";
					}
					condicion = deleteLastChar(condicion);
				}
				value = " IN (" +condicion+")"; break;
			case ConstantsInf.COD_OPERADOR_BD_CAD_EMPIEZAN_POR: value = " LIKE '"+condicion.toUpperCase()+"%'" ; break;	
			case ConstantsInf.COD_OPERADOR_BD_CAD_TERMINAN_POR: value = " LIKE '%"+condicion.toUpperCase()+"'"; break;
			case ConstantsInf.COD_OPERADOR_CAD_CONTIENEN: value = " LIKE '%"+condicion.toUpperCase()+"%'" ; break;
			default: value = ""; break;
		}
		return value;
	}
	
	private String getOpAritmetico(int idOperador){
		String value = "";
		switch (idOperador) {
			case ConstantsInf.COD_OPERADOR_ARIT_SUMA: value = ConstantsInf.OPERADOR_ARIT_SUMA; break;
			case ConstantsInf.COD_OPERADOR_ARIT_RESTA: value = ConstantsInf.OPERADOR_ARIT_RESTA; break;	
			case ConstantsInf.COD_OPERADOR_ARIT_MULT: value = ConstantsInf.OPERADOR_ARIT_MULT; break;
			case ConstantsInf.COD_OPERADOR_ARIT_DIV: value = ConstantsInf.OPERADOR_ARIT_DIV; break;
			default: value = ""; break;
		}
		return value;
	}
	
	/**
	 *  metodo para ordenar los datos del Informe segun el campo Orden
	 * @param lstDatInf
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<DatoInformes> ordenarDatosInforme(Set<DatoInformes> lstDatInf){
		DatoInformes[] ArrDatInf = null;
		ArrDatInf = new DatoInformes[lstDatInf.size()];
		ArrDatInf = lstDatInf.toArray(ArrDatInf);
		//Ordenamos el array
		Arrays.sort(ArrDatInf, new Comparator<DatoInformes>()
		{
			public int compare(DatoInformes o1, DatoInformes o2) {
				return o1.getOrden().compareTo(o2.getOrden());
			}
		});
		List<DatoInformes> lstDatOrdenada = new ArrayList(Arrays.asList(ArrDatInf));
		return lstDatOrdenada;
	}
	
	/**
	 * Metodo que devuelve un listado de los tipos de formato del informe
	 */
	public List<FormatoCampoGenerico> getFormatosInforme(){
		List<FormatoCampoGenerico> lstFormatosInforme = new ArrayList<FormatoCampoGenerico>();
		//f1
		FormatoCampoGenerico f1 = new FormatoCampoGenerico();
		f1.setIdFormato(new BigDecimal(ConstantsInf.COD_FORMATO_PDF));
		f1.setNombreFormato(ConstantsInf.FORMATO_PDF);
		lstFormatosInforme.add(f1);
		//f2
		FormatoCampoGenerico f2 = new FormatoCampoGenerico();
		f2.setIdFormato(new BigDecimal(ConstantsInf.COD_FORMATO_XLS));
		f2.setNombreFormato(ConstantsInf.FORMATO_XLS);
		lstFormatosInforme.add(f2);
		//f3
		FormatoCampoGenerico f3 = new FormatoCampoGenerico();
		f3.setIdFormato(new BigDecimal(ConstantsInf.COD_FORMATO_HTML));
		f3.setNombreFormato(ConstantsInf.FORMATO_HTML);
		lstFormatosInforme.add(f3);
		//f4
		FormatoCampoGenerico f4 = new FormatoCampoGenerico();
		f4.setIdFormato(new BigDecimal(ConstantsInf.COD_FORMATO_CSV));
		f4.setNombreFormato(ConstantsInf.FORMATO_CSV);
		lstFormatosInforme.add(f4);
		
		return lstFormatosInforme;
	}
	
	/**
	 * metodo que devuelve un listado con las orientaciones de un informe ( pdf )
	 */
	public List<FormatoCampoGenerico> getOrientacionesInforme(){
		List<FormatoCampoGenerico> lstOrientacionesInforme = new ArrayList<FormatoCampoGenerico>();
		//o1
		FormatoCampoGenerico o1 = new FormatoCampoGenerico();
		o1.setIdFormato(new BigDecimal(ConstantsInf.COD_ORIENTACION_VERTICAL));
		o1.setNombreFormato(ConstantsInf.ORIENTACION_VERTICAL);
		lstOrientacionesInforme.add(o1);
		//o2
		FormatoCampoGenerico o2 = new FormatoCampoGenerico();
		o2.setIdFormato(new BigDecimal(ConstantsInf.COD_ORIENTACION_HORIZONTAL));
		o2.setNombreFormato(ConstantsInf.ORIENTACION_HORIZONTAL);
		lstOrientacionesInforme.add(o2);
		
		return lstOrientacionesInforme;
	}
	
	/**
	 * checkea si las tablas del informe estan todas relacionadas
	 * @param informe
	 * @return true si todas sus tablas estan relacionadas
	 */
	public boolean checkRelTablas (Informe informe){
		boolean todasRelacionadas = true;
		String vista1 = "";
		String vista2 = "";
		List<String> lstTablas = new ArrayList<String>();
		try {
			Set<DatoInformes> lstDatInf = informe.getDatoInformeses();
			for (DatoInformes datInf:lstDatInf){
				CamposPermitidos camPer = datInf.getCamposPermitidos();
					CamposCalculados camCalc = datInf.getCamposCalculados();
					if (camPer !=null){ //CAMPO PERMITIDO
						VistaCampo visC = camPer.getVistaCampo();
							if (!lstTablas.contains(visC.getVista().getNombreReal())){
								lstTablas.add(visC.getVista().getNombreReal());
							}
					}else{ // CAMPO CALCULADO
						vista1 = camCalc.getCamposPermitidosByIdoperando1().getVistaCampo().getVista().getNombreReal();
						vista2 = camCalc.getCamposPermitidosByIdoperando2().getVistaCampo().getVista().getNombreReal();
						if (!lstTablas.contains(vista1)){
							lstTablas.add(vista1);
						}
						if (!lstTablas.contains(vista2)){
							lstTablas.add(vista2);
						}
					}
			}
			// Verificamos las relaciones entre las tablas
			HashMap<String, Object> mapRelTablas = relacionarTablasInforme(lstTablas,true);
			todasRelacionadas = (Boolean)mapRelTablas.get("todasRelacionadas");
			return todasRelacionadas;
		} catch (Exception e) {
			logger.error("MtoInformeService.checkRelTablas - Ocurrio un error al comprobar la relacion de las tablas del informe", e);
		}
		return todasRelacionadas;
	}
	
	/**
	 *  relaciona las tablas del informe entre ellas
	 *  @return devuelve un  mapa con la lista de relaciones entre tablas 
	 *  y un boolean con true si todas ellas estan relacionadas
	 */
	public HashMap<String, Object> relacionarTablasInforme(List<String> lstTablas,boolean checkearRelaciones){
		// BUSCAR RELACION ENTRE TABLAS
		String vista1 = "";
		String vista2 = "";
		HashMap<String, Object> mapRelTablas = new HashMap<String, Object>();
		
		//recogemos la relacion entre tablas del Singleton
		List<RelVistaCampos> lstRelVistaCampos = this.getRelVistaCampos();
		
		List<RelVistaCampos> lstRelacionesTablas = new ArrayList<RelVistaCampos>();
		List<String> lstDuoTablas = new ArrayList<String>();
		String[] ArrTablas = new String[lstTablas.size()];
		ArrTablas = lstTablas.toArray(ArrTablas);
		for (int i=0;i<lstTablas.size();i++){
				vista1 = ArrTablas[i];
				for (int j=0;j<lstTablas.size();j++){
						vista2 = ArrTablas[j];
						// buscamos relacion  vista1 con vista 2
						for (RelVistaCampos rel:lstRelVistaCampos){
							if ((rel.getVistaByIdvista1().getNombreReal().equals(vista1) &&
									rel.getVistaByIdvista2().getNombreReal().equals(vista2)) ||
									(rel.getVistaByIdvista2().getNombreReal().equals(vista1) &&
											rel.getVistaByIdvista1().getNombreReal().equals(vista2))){
								if (!lstRelacionesTablas.contains(rel)){
									lstRelacionesTablas.add(rel);
									lstDuoTablas.add(rel.getVistaByIdvista1().getNombreReal()+"#"+rel.getVistaByIdvista2().getNombreReal());									
								} 
							}
						}
						
					}
		}
		mapRelTablas.put(LISTA_RELACIONES_TABLAS, lstRelacionesTablas);
		
		// verificar que todas las tablas estan relacionadas
		if (checkearRelaciones){
		boolean todasRelacionadas = true;
		if (ArrTablas.length > 1){
			for (int j=0;j<ArrTablas.length;j++){
				boolean encontrado = false;
				for (String duoTablas:lstDuoTablas){
					String[] tab = duoTablas.split("#");
					if (tab[0].equals(ArrTablas[j]) || tab[1].equals(ArrTablas[j])){
						encontrado = true;
						break;
					}
				}
				if (!encontrado){ // la tabla no tiene relacion
					logger.debug("la tabla: "+ ArrTablas[j] +" no esta relacionada");
					todasRelacionadas = false;
				}
			}
		}
		mapRelTablas.put("todasRelacionadas", todasRelacionadas);
		}
		return mapRelTablas;
	}
	
	@Override
	public Map<String, Object> duplicarInforme(Informe informeBean,  String tituloInfoDuplicado) throws BusinessException {
		
		Map<String, Object> parameters = new HashMap<String, Object>();	
		List<Usuario> listUsu = new ArrayList<Usuario>();
		List<Entidad> listEnt = new ArrayList<Entidad>();
		try {
			// si no existe un informe con el mismo nombre
			if (!mtoInformeDao.checkInformeExists(tituloInfoDuplicado,null)) {
				Informe informeduplicado = new Informe();
				informeduplicado.setNombre(tituloInfoDuplicado);
				informeduplicado.setTitulo1(informeBean.getTitulo1());
				informeduplicado.setTitulo2(informeBean.getTitulo2());
				informeduplicado.setTitulo3(informeBean.getTitulo3());
				informeduplicado.setConsulta(informeBean.getConsulta());
				informeduplicado.setCuenta(informeBean.getCuenta());
				informeduplicado.setOculto(informeBean.getOculto());
				informeduplicado.setPerfil(informeBean.getPerfil());
				informeduplicado.setUsuario(informeBean.getUsuario());
				informeduplicado.setVisibilidad(informeBean.getVisibilidad());
				informeduplicado.setVisibilidadEnt(informeBean.getVisibilidadEnt());
				
				
				for (Usuario u :informeBean.getUsuarios()){
					listUsu.add(u);
				}
				informeduplicado.setUsuarios(new HashSet<Usuario>(listUsu));
				for (Entidad e:informeBean.getEntidades()){
					listEnt.add(e);
				}
				informeduplicado.setEntidades(new HashSet<Entidad>(listEnt));
				informeduplicado.setFechaAlta(new Date());
				mtoInformeDao.saveOrUpdate(informeduplicado);
				
				// guardamos los datos de los informes
				for (DatoInformes datos: informeBean.getDatoInformeses()){
					mtoInformeDao.evict(datos);
					datos.setId(null);
					datos.setInforme(informeduplicado);
					mtoInformeDao.saveOrUpdate(datos);
					for (CondicionCamposCalculados coc :datos.getCondicionCamposCalculadoses()){
						mtoInformeDao.evict(coc);
						coc.setId(null);
						coc.setDatoInformes(datos);
						mtoInformeDao.saveOrUpdate(coc);
					}
					for (CondicionCamposPermitidos cop :datos.getCondicionCamposPermitidoses()){
						mtoInformeDao.evict(cop);
						cop.setId(null);
						cop.setDatoInformes(datos);
						mtoInformeDao.saveOrUpdate(cop);
					}
					for (ClasificacionRupturaCamposCalculados crc :datos.getClasificacionRupturaCamposCalculadoses()){
						mtoInformeDao.evict(crc);
						crc.setId(null);
						crc.setDatoInformes(datos);
						mtoInformeDao.saveOrUpdate(crc);
					}
					for (ClasificacionRupturaCamposPermitidos cro :datos.getClasificacionRupturaCamposPermitidoses()){
						mtoInformeDao.evict(cro);
						cro.setId(null);
						cro.setDatoInformes(datos);
						mtoInformeDao.saveOrUpdate(cro);
					}
				}
				parameters.put("informeduplicado", informeduplicado);
			}else{
				parameters.put(ALERTA, bundle.getObject(ConstantsInf.ALERTA_INFORME_ALTA_EXISTE_KO));
			}
			
		} catch (DAOException e) {
			logger.error("MtoInformeService - duplicarInforme :Error al duplicar un informe" + e);
			throw new BusinessException();
		}
		return parameters;
	}
	
	/* Pet. 62719 ** MODIF TAM (01.02.2021) ** Inicio */
	public String obtener_filtro_asegBloqueado(List<String> lstTablas, boolean inner) throws BusinessException {
		
		String filtroAsegurado = "";
		
		if (inner) {
			/* Construimos el inner join */
			for (String tabla:lstTablas){
				logger.debug ("Valor de tabla: -"+tabla);
				
				String tablaAux = tabla+"$";
				
				if (tabla.equals("TB_COMS_INFORMES_RECIBOS") || tabla.equals("TB_COMS_INFORMES_RECIBOS_2015")){
					filtroAsegurado = " INNER JOIN O02AGPE0.TB_POLIZAS PO ON PO.REFERENCIA = " +tablaAux +".REF_POLIZA AND PO.TIPOREF = 'P'"  
							        + " INNER JOIN O02AGPE0.TB_LINEAS LI ON LI.LINEASEGUROID = PO.LINEASEGUROID "  
							          + " AND LI.CODLINEA = " +tablaAux +".CODLINEA " 
							          + " AND LI.CODPLAN = " + tablaAux + ".CODPLAN ";
				
				}else if (tabla.equals("VW_INFORME_COMISIONES_UNION")) {
					filtroAsegurado = " INNER JOIN O02AGPE0.TB_POLIZAS PO ON PO.REFERENCIA = " +tablaAux +".REFPLZ AND PO.TIPOREF = 'P'"
						     		+ " INNER JOIN O02AGPE0.TB_LINEAS LI ON LI.LINEASEGUROID = PO.LINEASEGUROID "     
						              + " AND LI.CODLINEA = "+tablaAux+".CODLIN "     
						              + " AND LI.CODPLAN = "+tablaAux+".CODPLN " ;    
					
		
				}

			}
			
		}else {
			/* Construimos el filtro */
			filtroAsegurado = " AND PO.IDASEGURADO NOT IN (SELECT BLQ.ID_ASEGURADO FROM O02AGPE0.TB_BLOQUEOS_ASEGURADOS BLQ" + 
			                  " WHERE BLQ.IDESTADO_ASEG ='B' )";
		}
			
		
		
				
		return filtroAsegurado;		
		
	}
	/* Pet. 62719 ** MODIF TAM (01.02.2021) ** Fin */
	
	@SuppressWarnings("unchecked")
	public List<Object> getConsulta(String sql){
		return mtoInformeDao.getConsulta(sql);
	}

	public void setMtoInformeDao(IMtoInformeDao mtoInformeDao) {
		this.mtoInformeDao = mtoInformeDao;
	}
	
	public int getInformeCountWithFilter(InformeFilter filter, String cadenaCodigosLupas) {
		return mtoInformeDao.getInformesCountWithFilter(filter, cadenaCodigosLupas);
	}
	
	
	public Collection<Informe> getInformeWithFilterAndSort(
			InformeFilter filter, InformeSort sort, int rowStart,
			int rowEnd, String cadenaUsuarios) throws BusinessException {
		
		return mtoInformeDao.getInformesWithFilterAndSort(filter, sort, rowStart, rowEnd, cadenaUsuarios);
	}
	
	public List<RelVistaCampos> getRelVistaCampos() {
		List<RelVistaCampos> lstRelVistaCampos = mtoVistasService.getRelVistaCampos();
		return lstRelVistaCampos;
	}

	public void setMtoVistasService(IMtoVistasService mtoVistasService) {
		this.mtoVistasService = mtoVistasService;
	}

	
}