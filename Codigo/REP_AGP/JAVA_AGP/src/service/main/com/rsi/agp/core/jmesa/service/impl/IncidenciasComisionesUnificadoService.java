package com.rsi.agp.core.jmesa.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Blob;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.ExportType;
import org.jmesa.limit.Limit;
import org.jmesa.view.component.Row;
import org.jmesa.view.component.Table;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.core.jmesa.dao.impl.IImportacionComisionesUnificadoDao;
import com.rsi.agp.core.jmesa.dao.impl.IIncidenciasComisionesUnificadoDao;
import com.rsi.agp.core.jmesa.filter.IGenericoFilter;
import com.rsi.agp.core.jmesa.service.IIncidenciasComisionesUnificadoService;
import com.rsi.agp.core.jmesa.sort.IGenericoSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbarMarcarTodos;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.managers.impl.ComisionesUnificadas.IImportacionComisionesUnificadoManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroContenidoUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroIncidenciasUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroUnificado;
import com.rsi.agp.dao.tables.commons.Usuario;

public class IncidenciasComisionesUnificadoService extends GetTablaService 
	implements IIncidenciasComisionesUnificadoService {
	
	//Constantes para las columnas de la tabla
	private final static String ID= "id";
	private final static String PLAN= "linea.codplan";
	private final static String LINEA= "linea.codlinea";
	private final static String COLECTIVO= "idcolectivo";
	private final static String E_S_MED_FICH= "subentidad";
	private final static String OFICINA= "oficina";
	private final static String FASE= "fase"; 
	private final static String POLIZA= "refpoliza";
	private final static String ESTADO= "estado";
	private final static String E_S_MED_COL= "esMedColectivo";
	private final static String MENSAJE= "mensaje";
	private final static String ID_FICHEROUNIF="ficheroUnificado.id";
												
	
	private static final Log logger = LogFactory.getLog(IncidenciasComisionesUnificadoService.class);
	
	private IGenericoFilter incidenciasComisionesUnificadoFilter;
	private IGenericoSort incidenciasComisionesUnificadoSort;
	private IImportacionComisionesUnificadoDao importacionComisionesUnificadoDao;
	private IImportacionComisionesUnificadoManager importacionComisionesUnificadoManager;
	private static final Character ESTADO_ERRONEO = new Character('E');
	
	
	
	//MÉTODOS JMESA ----------------------------------------------------------------------------------------
	@SuppressWarnings("deprecation")
	@Override
	public String getTabla(HttpServletRequest request,
			HttpServletResponse response,
			Serializable ficheroIncidenciasUnificado, String origenLlamada,
			List<BigDecimal> listaGrupoEntidades, IGenericoDao genericoDao) {
		
		try {
			
			FicheroIncidenciasUnificado incidenciasFichero = (FicheroIncidenciasUnificado)ficheroIncidenciasUnificado;
				
			cargarColumnas();
			TableFacade tableFacade= this.crearTableFacade(request, response, origenLlamada, columnas);
	
			tableFacade.autoFilterAndSort(false);
			tableFacade.setExportTypes(response, ExportType.EXCEL);
			setColumnasVisibles(tableFacade);
			
			if (origenLlamada != null) {
				incidenciasComisionesUnificadoFilter.clear();
				if(null!=tableFacade.getLimit().getFilterSet().getFilters()) {
					tableFacade.getLimit().getFilterSet().getFilters().clear();
				}			
				cargarFiltrosBusqueda(incidenciasFichero, tableFacade);
			}
			
			incidenciasComisionesUnificadoSort.clear();		
			this.setDataAndLimitVariables(tableFacade, incidenciasComisionesUnificadoFilter, genericoDao, 
					incidenciasComisionesUnificadoSort, incidenciasComisionesUnificadoSort);
			
			String ajax = request.getParameter("ajax");
			if (!"false".equals(ajax)){
				tableFacade.setToolbar(new CustomToolbarMarcarTodos());
				tableFacade.setView(new CustomView());
			}
			
			String listaIdsTodos = getListaIdsTodos(genericoDao);
			String script = "<script>$(\"#listaIdsTodos\").val(\""
					+ listaIdsTodos + "\");</script>";
	
			return html(tableFacade) + script;
			
		} catch (Exception ex) {
	
			logger.error("getTabla error. " + ex);
			return "";
		}
	}
	
	
	private String getListaIdsTodos(IGenericoDao genericoDao)
			throws DAOException {

		String listaIdsTodos = ((IIncidenciasComisionesUnificadoDao) genericoDao)
				.getListaIdsTodos(this.incidenciasComisionesUnificadoFilter);
		return listaIdsTodos;
	}


	private void cargarColumnas() {
		// Crea el Map con las columnas del listado y los campos del filtro de
		// busqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID, ID);			
			columnas.put(PLAN, PLAN);
			columnas.put(LINEA, LINEA);
			columnas.put(COLECTIVO, COLECTIVO);
			columnas.put(E_S_MED_FICH,E_S_MED_FICH);
			columnas.put(OFICINA, OFICINA);
			columnas.put(FASE, FASE);			
			columnas.put(POLIZA, POLIZA);			
			columnas.put(ESTADO, ESTADO);	
			columnas.put(E_S_MED_COL, E_S_MED_COL);
			columnas.put(MENSAJE, MENSAJE);
			columnas.put(ID_FICHEROUNIF, ID_FICHEROUNIF);
		}
	}	
	
	@SuppressWarnings("deprecation")
	private void setColumnasVisibles(TableFacade tableFacade) {
		Limit limit = tableFacade.getLimit();
		
		if (!limit.isExported()){
			setColumnasVisiblesGrid(tableFacade);
		}
		else {
			setColumnasVisiblesExport(tableFacade);
		}

	}
	
	private void cargarFiltrosBusqueda(FicheroIncidenciasUnificado incidenciasFichero, TableFacade tableFacade) {

		if(null!=incidenciasFichero.getFicheroUnificado() && null!=incidenciasFichero.getFicheroUnificado().getId())
			this.addColumnaFiltro(tableFacade, "ficheroUnificado.id", incidenciasFichero.getFicheroUnificado().getId(), incidenciasComisionesUnificadoFilter);
		
		if(null!=incidenciasFichero.getLinea() && null!=incidenciasFichero.getLinea().getCodlinea())
			this.addColumnaFiltro(tableFacade, LINEA, incidenciasFichero.getLinea().getCodlinea(), incidenciasComisionesUnificadoFilter);
			
		if(null!=incidenciasFichero.getLinea() && null!=incidenciasFichero.getLinea().getCodplan())
			this.addColumnaFiltro(tableFacade, PLAN, incidenciasFichero.getLinea().getCodplan(), incidenciasComisionesUnificadoFilter);
				
		if(null!=incidenciasFichero.getIdcolectivo())
			this.addColumnaFiltro(tableFacade, COLECTIVO, incidenciasFichero.getIdcolectivo(), incidenciasComisionesUnificadoFilter);
		
		if(null!= incidenciasFichero.getSubentidad())
			this.addColumnaFiltro(tableFacade, E_S_MED_FICH, incidenciasFichero.getSubentidad(), incidenciasComisionesUnificadoFilter);
			
		if(null!= incidenciasFichero.getOficina())	
			this.addColumnaFiltro(tableFacade, OFICINA,  incidenciasFichero.getOficina(), incidenciasComisionesUnificadoFilter);
			
		if(null!= incidenciasFichero.getFase())
			this.addColumnaFiltro(tableFacade, FASE, incidenciasFichero.getFase(), incidenciasComisionesUnificadoFilter);
			
		if(null!= incidenciasFichero.getRefpoliza())
			this.addColumnaFiltro(tableFacade, POLIZA, incidenciasFichero.getRefpoliza(), incidenciasComisionesUnificadoFilter);
			
		if(null!=incidenciasFichero.getEstado())
			this.addColumnaFiltro(tableFacade, ESTADO, incidenciasFichero.getEstado(), incidenciasComisionesUnificadoFilter);
			
		if(null!=incidenciasFichero.getEsMedColectivo())
			this.addColumnaFiltro(tableFacade, E_S_MED_COL, incidenciasFichero.getEsMedColectivo(), incidenciasComisionesUnificadoFilter);
		
		if(null!=incidenciasFichero.getMensaje())
			this.addColumnaFiltro(tableFacade, MENSAJE, incidenciasFichero.getMensaje(), incidenciasComisionesUnificadoFilter);
		
	}
	
	@SuppressWarnings("deprecation")
	private String html(TableFacade tableFacade) {
		Limit limit = tableFacade.getLimit();
		if (limit.isExported()) {
			if (limit.getExportType() == ExportType.EXCEL) {
				Table table = tableFacade.getTable();
	        	// Quita la columna Id del informe
	        	//eliminarColumnaId(tableFacade, table);
	        	// renombramos las cabeceras
	            configurarCabecerasColumnasExport(table);
	            // Escribe los datos generados en el response
	            tableFacade.render(); 
	            // Devuelve nulo para que el controller no haga nada
	            return null; 			
			}
		}else {
			HtmlTable table = (HtmlTable) tableFacade.getTable();
			table.getRow().setUniqueProperty(ID);
	
			configurarColumnas(table);
			
			table.getRow().getColumn(columnas.get(ID)).getCellRenderer().setCellEditor(getCellEditorAcciones());
			table.getRow().getColumn(columnas.get(ESTADO)).getCellRenderer().setCellEditor(getCellEditorEstado());
		}
		
		return tableFacade.render();
	}
	
	private void configurarColumnas(HtmlTable table) {
		
		this.configColumna(table, columnas.get(ID), "&nbsp;&nbsp;Acciones", false, false, "6%");
		this.configColumna(table, columnas.get(PLAN),"Plan",true,true, "6%");
		this.configColumna(table, columnas.get(LINEA),"Linea",true,true, "8%");
		this.configColumna(table, columnas.get(COLECTIVO),"Colectivo",true,true, "8%");
		this.configColumna(table, columnas.get(E_S_MED_FICH), "E-S Med Fich", true, true, "9%");
		this.configColumna(table, columnas.get(OFICINA),"Oficina",true,true, "6%");
		this.configColumna(table, columnas.get(FASE),"Fase",true,true, "6%");		
		this.configColumna(table, columnas.get(POLIZA),"Poliza",true,true, "8%");
		this.configColumna(table, columnas.get(ESTADO),"Estado",true,true, "8%");
		this.configColumna(table, columnas.get(E_S_MED_COL), "E-S Med Col", true, true, "9%");		
		this.configColumna(table, columnas.get(MENSAJE),"Mensaje",true,true, "26%");
		
	}
	
	private CellEditor getCellEditorEstado() {
		return new CellEditor() {
		    public Object getValue(Object item, String property, int rowcount) {
		    	// Obtiene el codigo de estado de la poliza actual
		    	Character estado = null;
				
				estado = ((Character)new BasicCellEditor().getValue(item, columnas.get(ESTADO), rowcount));
				
		    	// Muestra el mensaje correspondiente al estado
		    	String value = "";	
		    	if(estado!=null) {
		    	
		    		if(estado.toString().compareTo("A")==0) value="Aviso";
		    		if(estado.toString().compareTo("E")==0) value="Erróneo";
		    		if(estado.toString().compareTo("C")==0) value="Correcto";	 
		    		if(estado.toString().compareTo("R")==0) value="Revisado";	   
		    	}
		    	
		    			    	
		        HtmlBuilder html = new HtmlBuilder();
		        html.append(value);
		        return html.toString();
		    }
		};
	}
	
	private CellEditor getCellEditorAcciones() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				HtmlBuilder html = new HtmlBuilder();
				Long id = (Long) new BasicCellEditor().getValue(item, property,
						rowcount);
				BigDecimal codLinea = (BigDecimal)new BasicCellEditor().getValue(item, LINEA, rowcount);
				BigDecimal codPlan = (BigDecimal)new BasicCellEditor().getValue(item, PLAN, rowcount);
            	String idColectivo =StringUtils.nullToString((String)new BasicCellEditor().getValue(item, COLECTIVO, rowcount));
            	String esMedFich =StringUtils.nullToString((String)new BasicCellEditor().getValue(item, E_S_MED_FICH, rowcount));
            	String oficina =StringUtils.nullToString((String)new BasicCellEditor().getValue(item, OFICINA, rowcount));
            	Integer fase =(Integer)new BasicCellEditor().getValue(item, FASE, rowcount);
            	String refPoliza =StringUtils.nullToString((String)new BasicCellEditor().getValue(item, POLIZA, rowcount));
            	Character estado =(Character)new BasicCellEditor().getValue(item, ESTADO, rowcount);
            	String mensaje =StringUtils.nullToString((String)new BasicCellEditor().getValue(item, MENSAJE, rowcount));
            	String esMedCol =StringUtils.nullToString((String)new BasicCellEditor().getValue(item, E_S_MED_COL, rowcount));
				
            	// checkbox
				html.append("<input type=\"checkbox\" id=\"check_" + id
						+ "\"  name=\"check_" + id
						+ "\" onClick =\"listaCheckId(\'" + id
						+ "')\" class=\"dato\"/>");
				html.append("&nbsp;");
            	
				html.a().href().quote().append("javascript:modificar('"+id+"','"+codLinea+"','"+idColectivo+"','"+esMedFich+"','"+oficina+"','" +
						fase+"','"+refPoliza+"','"+estado+"','"+mensaje+"','"+esMedCol+"','"+ codPlan+"');").quote().close();	
				html.append("<img src=\"jsp/img/magnifier.png\" alt=\"Consultar\" title=\"Consultar\"/>");        	
	            html.aEnd();
	            html.append("&nbsp;");
	            
	         
	            if(estado != null && estado == 'C') {
	            	html.a().href().quote().append("javascript:showMensajeError('"+mensaje+"');").quote().close();
	                html.append("<img src=\"jsp/img/folderopen.gif\" alt=\"Mensajes\" title=\"Mensajes\"/>");
	                html.aEnd();
	            } else {
	            	 html.append("<img src=\"jsp/img/displaytag/transparente.gif\"  width='16' height='16'/>");
	            }
	            
	            if(estado != null && estado != 'C' && estado != 'R') {	                
	                html.a().href().quote().append("javascript:revisar(" + id + ");").quote().close();
	                html.append("<img src=\"jsp/img/displaytag/accept.png\" alt=\"Revisar\" title=\"Revisar\"/>");
	                html.aEnd();
	            } else {
	            	 html.append("<img src=\"jsp/img/displaytag/transparente.gif\"  width='16' height='16'/>");
	            }
	            	            
				return html.toString();
			}
		};
	}

	public void setIncidenciasComisionesUnificadoFilter(
			IGenericoFilter incidenciasComisionesUnificadoFilter) {
		this.incidenciasComisionesUnificadoFilter = incidenciasComisionesUnificadoFilter;
	}

	public void setIncidenciasComisionesUnificadoSort(
			IGenericoSort incidenciasComisionesUnificadoSort) {
		this.incidenciasComisionesUnificadoSort = incidenciasComisionesUnificadoSort;
	}	
	
	public List<FicheroIncidenciasUnificado> gestListaIncidencias (FicheroIncidenciasUnificado ficheroIncidenciasUnificadoBean, IIncidenciasComisionesUnificadoDao dao)throws BusinessException{
		logger.debug("init - IncidenciasComisionesUnificadoService.gestListaIncidencias");
		List<FicheroIncidenciasUnificado> lista = null;
		try {
			this.cargarFiltrosBusqueda(ficheroIncidenciasUnificadoBean, null);
			lista = (List<FicheroIncidenciasUnificado>) dao.getIncidenciasFicheroUnificado(incidenciasComisionesUnificadoFilter, incidenciasComisionesUnificadoSort);			
		} catch (Exception ex) {
			logger.debug("Se ha producido un error al recuperar la lista de incidencias de comisiones del fichero unificado:" + ex.getMessage());
			throw new BusinessException("Se ha producido un error al recuperar la lista de incidencias de comisiones del fichero unificado", ex);
		}
		logger.debug("end - IncidenciasComisionesUnificadoService.gestListaIncidencias");
		return lista;
	}
	
	
	/** 
	 * Carga un fichero con fecha de aceptaciÃ³n actual siempre que no haya incidencias en estado erroneo
	 * @param ficheroIncidenciaBean
	 * @param fechaAceptacionFichero
	 * @return
	 * @throws BusinessException 
	 */
	public boolean cargarFichero(FicheroUnificado ficheroUnificado, Date fechaAceptacionFichero, 
			IIncidenciasComisionesUnificadoDao dao) throws BusinessException {
		logger.debug("init - cargarFichero");
		FicheroIncidenciasUnificado fichero = new FicheroIncidenciasUnificado();		
		boolean resultado = false;
		List<FicheroIncidenciasUnificado> list = null;
		
		try {
			logger.debug("Se obtienen las incidencias asociadas al fichero de comisiones unificado" + fichero + " con estado erroneo");
			//ficheroIncidencia.getFichero().setId(fichero.getId());
			fichero.setFicheroUnificado(ficheroUnificado);
			fichero.setEstado(ESTADO_ERRONEO);
			
			cargarFiltrosBusqueda(fichero, null);
			
			list = (List<FicheroIncidenciasUnificado>) dao.getIncidenciasFicheroUnificado(incidenciasComisionesUnificadoFilter, incidenciasComisionesUnificadoSort);
			
			logger.debug("Si no hay ninguna incidencia en estado erroneo entonces se puede aceptar el fichero");
			if (list.size() == 0){
				ficheroUnificado.setFechaAceptacion(fechaAceptacionFichero);
				dao.saveOrUpdate(ficheroUnificado);
				resultado = true;
			} 					
			
		} catch (Exception e) {
			logger.error("Se ha producido un error al aceptar el fichero de comisiones unificado" + fichero);
			throw new BusinessException("Se ha producido un error al aceptar el fichero de comisiones unificado" + fichero, e);			
		}		
		logger.debug("end - cargarFichero");
		return resultado;
	}
	
	public void verificarTodos(FicheroUnificado fichero, IIncidenciasComisionesUnificadoDao dao) throws BusinessException {
		logger.debug("init - verificarTodos");	
		
		try {
			logger.debug("se borran todos los registros correspondientes al fichero en la tabla de incidencias");
			dao.borraIncidencias(fichero.getId());
			logger.debug("se realizan de nuevo las validaciones.");	
			importacionComisionesUnificadoDao.validarFicheroComisiones(fichero.getId(), fichero.getTipoFichero());		
			
		} catch (Exception e) {
			logger.error("Se ha producido un error al verificar los registros del fichero de comisiones unificadas " + fichero.getId());
			throw new BusinessException("Se ha producido un error al verificar el fichero de comisiones unificadas " + fichero.getId(), e);		
		}
		
		// Borra el objeto de la sesión de Hibernate para que al volver a cargarlo de la BBDD esté actualizado
		try {
			importacionComisionesUnificadoDao.evict(fichero);
		}
		catch (Exception e) {
			logger.error("Error al eliminar el objeto fichero de la sesión de Hibernate", e);
		}
		
		logger.debug("end - verificarTodos");
	}


	public void setImportacionComisionesUnificadoDao(
			IImportacionComisionesUnificadoDao importacionComisionesUnificadoDao) {
		this.importacionComisionesUnificadoDao = importacionComisionesUnificadoDao;
	}
	
	public void setImportacionComisionesUnificadoManager(IImportacionComisionesUnificadoManager importacionComisionesUnificadoManager) {
		this.importacionComisionesUnificadoManager = importacionComisionesUnificadoManager;
	}
	
	@SuppressWarnings("deprecation")
	private void setColumnasVisiblesGrid(TableFacade tableFacade) {
		
		tableFacade.setColumnProperties(columnas.get(ID), columnas.get(PLAN), columnas.get(LINEA), columnas.get(COLECTIVO),
									    columnas.get(E_S_MED_FICH), columnas.get(OFICINA), columnas.get(FASE),
									    columnas.get(POLIZA), columnas.get(ESTADO), columnas.get(E_S_MED_COL), columnas.get(MENSAJE));
	}

	@SuppressWarnings("deprecation")
	private void setColumnasVisiblesExport(TableFacade tableFacade) {
		
		tableFacade.setColumnProperties(columnas.get(PLAN), columnas.get(LINEA), columnas.get(COLECTIVO),
			    						columnas.get(E_S_MED_FICH), columnas.get(OFICINA), columnas.get(FASE),
			    						columnas.get(POLIZA), columnas.get(ESTADO), columnas.get(E_S_MED_COL), columnas.get(MENSAJE));
	}

	@SuppressWarnings("deprecation")
	private void configurarCabecerasColumnasExport(Table table) {
		
		table.setCaption("Revision de incidencias de comisiones 2015+ ");
    	
		Row row = table.getRow();

		row.getColumn(PLAN).setTitle("Plan");
		row.getColumn(LINEA).setTitle("Linea");
		row.getColumn(COLECTIVO).setTitle("Colectivo");
		row.getColumn(E_S_MED_FICH).setTitle("E-S Med. Fich.");
		row.getColumn(OFICINA).setTitle("Oficina");
		row.getColumn(FASE).setTitle("Fase");
		row.getColumn(POLIZA).setTitle("Poliza");		
		row.getColumn(columnas.get(ESTADO)).getCellRenderer().setCellEditor(getCellEditorEstado());
		row.getColumn(E_S_MED_COL).setTitle("E-S Med. Col.");
		row.getColumn(MENSAJE).setTitle("Mensaje");		
	}


	@Override
	public void revisarIncidencia(final Long idIncidencia, final char estado,
			final IIncidenciasComisionesUnificadoDao dao)
			throws BusinessException {
		
		logger.debug("init - revisarIncidencia");	
		
		try {
			
			dao.revisarIncidencia(idIncidencia, estado);
			
		} catch (Exception e) {
			
			logger.error("Se ha producido un error al revisar la incidencia " + idIncidencia);
			throw new BusinessException("Se ha producido un error al revisar la incidencia " + idIncidencia, e);		
		}
		
		logger.debug("end - revisarIncidencia");
		
	}


	@Override
	public void recargarFichero(FicheroUnificado fichero, Usuario usuario, HttpServletRequest request, final IIncidenciasComisionesUnificadoDao dao) throws BusinessException {
		

		logger.debug("init - recargarFichero");	

		
		FicheroContenidoUnificado fich = fichero.getFicheroContenido();
		
		String nombre = fichero.getNombreFichero();
		Long ficheroId = fichero.getId();
		Character tipoFichero = fichero.getTipoFichero();
		Blob contenido = fich.getContenido();
		
		try {

			dao.deleteAll(fichero.getFicheroIncidenciasUnificados());
			dao.deleteAll(fichero.getFases());
			dao.evict(fichero);


			importacionComisionesUnificadoManager.procesaFichero(contenido,nombre, ficheroId, tipoFichero, usuario, request);
			
		}  catch (DAOException e) {

			e.printStackTrace();
			throw new BusinessException("Se ha producido un error al eliminar las incidencias y fases del fichero " + ficheroId, e);		
		} catch (Exception e) {
			logger.error("Se ha producido un error al recargar el fichero " + ficheroId);

			throw new BusinessException("Se ha producido un error al recargar el fichero " + ficheroId, e);
			
		}

		
		logger.debug("end - recargarFichero");	

	}		
}