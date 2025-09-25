package com.rsi.agp.core.jmesa.service.impl;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Limit;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;
import org.springframework.web.multipart.MultipartFile;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.core.jmesa.dao.impl.IClaseDetalleGanadoDao;
import com.rsi.agp.core.jmesa.filter.gan.ClaseDetalleGanadoFilter;
import com.rsi.agp.core.jmesa.service.utilidades.IClaseDetalleGanadoService;
import com.rsi.agp.core.jmesa.sort.IGenericoSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomToolbarMarcarTodos;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.poliza.TerminoFiltro1;
import com.rsi.agp.dao.models.admin.IComarcaDao;
import com.rsi.agp.dao.models.admin.IProvinciaDao;
import com.rsi.agp.dao.models.config.IClaseDetalleDao;
import com.rsi.agp.dao.tables.admin.Clase;
import com.rsi.agp.dao.tables.admin.ClaseDetalleGanado;
import com.rsi.agp.dao.tables.cgen.TipoCapitalConGrupoNegocio;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.gan.Especie;
import com.rsi.agp.dao.tables.cpl.gan.GruposRazas;
import com.rsi.agp.dao.tables.cpl.gan.RegimenManejo;
import com.rsi.agp.dao.tables.cpl.gan.TiposAnimalGanado;
import com.rsi.agp.dao.tables.poliza.Linea;

@SuppressWarnings({"deprecation", "rawtypes", "unchecked"})
public class ClaseDetalleGanadoService extends GetTablaService implements IClaseDetalleGanadoService{
	
	// Constantes para los nombres de las columnas del listado
	private static final String ID = "id";
	private static final String MODULO = "codmodulo";
	private static final String ESPECIE = "codespecie";
	private static final String REGIMEN = "codregimen";
	private static final String GRUPORAZA = "codgruporaza";
	private static final String TIPOANIMAL = "codtipoanimal";
	private static final String TIPOCAPITAL = "codtipocapital";
	private static final String PROVINCIA = "codprovincia";
	private static final String COMARCA = "codcomarca";
	private static final String TERMINO = "codtermino";
	private static final String SUBTERMINO = "subtermino";
	private static final String CLASEID = "clase.id";
	private static final String ID_LINEASEGUROID = "id.lineaseguroid";
	private static final String NBSP = "&nbsp;";

	ClaseDetalleGanadoFilter claseDetalleGanadoFilter;
	IGenericoSort claseDetalleGanadoSort;
	
	private IClaseDetalleGanadoDao claseDetalleGanadoDao;
	private IProvinciaDao provinciaDao;
	private IComarcaDao comarcaDao;
	private IClaseDetalleDao claseDetalleDao;

	private Log logger = LogFactory.getLog(getClass());
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");


	public String getTabla(HttpServletRequest request,
			HttpServletResponse response,
			Serializable claseDetalleGanadoBean, String origenLlamada, String vieneDeCargaClases,
			List<BigDecimal> listaGrupoEntidades, IGenericoDao genericoDao) {
		
		ClaseDetalleGanado claseDetalleGanado=(ClaseDetalleGanado)claseDetalleGanadoBean;
			
		cargarColumnas();
		TableFacade tableFacade= this.crearTableFacade(request, response, origenLlamada, columnas);
			
		setColumnasVisibles(tableFacade, vieneDeCargaClases);
		/*claseDetalleGanadoFilter.clear();
		if (origenLlamada != "doBorrar") {			
			cargarFiltrosBusqueda(claseDetalleGanado, tableFacade, claseDetalleGanadoFilter);
		}else {
			if(null!=tableFacade.getLimit().getFilterSet().getFilters()) {
				tableFacade.getLimit().getFilterSet().getFilters().clear();
			}
		}*/
		if (origenLlamada.compareTo("Consultar")!=0) {
			claseDetalleGanadoFilter.clear();
			if(null!=tableFacade.getLimit().getFilterSet().getFilters()) {
				tableFacade.getLimit().getFilterSet().getFilters().clear();
			}			
			cargarFiltrosBusqueda(claseDetalleGanado, tableFacade, claseDetalleGanadoFilter);
			claseDetalleGanadoSort.clear();		
			this.setDataAndLimitVariables(tableFacade, claseDetalleGanadoFilter, genericoDao, claseDetalleGanadoSort, claseDetalleGanadoSort);
		}else {
			claseDetalleGanadoFilter.clear();
			ClaseDetalleGanado claseDetalleGanadoLimit = this.getBeanFromLimit(tableFacade.getLimit());
			cargarFiltrosBusqueda(claseDetalleGanadoLimit, tableFacade, claseDetalleGanadoFilter);
			claseDetalleGanadoSort.clear();		
			this.setDataAndLimitVariables(tableFacade, claseDetalleGanadoFilter, genericoDao, claseDetalleGanadoSort, claseDetalleGanadoSort);
		}
		
		
		
		String listaIdsTodos = getlistaIdsTodos(claseDetalleGanadoFilter);
		String script = "<script>$(\"#listaIdsTodos\").val(\"" + listaIdsTodos + "\");</script>";
		
		if (!"true".equals(vieneDeCargaClases)){
			tableFacade.setToolbar(new CustomToolbarMarcarTodos());
			tableFacade.setView(new CustomView());
		}else{
			tableFacade.setToolbar(new CustomToolbar());
			tableFacade.setView(new CustomView());
		}
	
		return html(tableFacade, vieneDeCargaClases)+ script;

	}
	
	private String getlistaIdsTodos(ClaseDetalleGanadoFilter consultaFilter) {
		String listaIdsTodos =claseDetalleGanadoDao.getlistaIdsTodos(consultaFilter);
		return listaIdsTodos;
		
	}
	
	private void cargarColumnas() {
		// Crea el Map con las columnas del listado y los campos del filtro de
		// busqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID, ID);			
			columnas.put(MODULO, MODULO);
			columnas.put(ESPECIE, ESPECIE);
			columnas.put(REGIMEN, REGIMEN);
			columnas.put(GRUPORAZA, GRUPORAZA);
			columnas.put(TIPOANIMAL, TIPOANIMAL);
			columnas.put(TIPOCAPITAL, TIPOCAPITAL);
			columnas.put(PROVINCIA,PROVINCIA);
			columnas.put(COMARCA, COMARCA);
			columnas.put(TERMINO, TERMINO);
			columnas.put(SUBTERMINO, SUBTERMINO);
			columnas.put(CLASEID, CLASEID);
		}
	}	
	
	
	private void setColumnasVisibles(TableFacade tableFacade, String vieneDeCargaClases){
		
		if(!"true".equals(vieneDeCargaClases)){
			tableFacade.setColumnProperties(columnas.get(ID),
					columnas.get(MODULO),columnas.get(ESPECIE),columnas.get(REGIMEN),
					columnas.get(GRUPORAZA),columnas.get(TIPOANIMAL),
					columnas.get(TIPOCAPITAL),columnas.get(PROVINCIA),columnas.get(COMARCA),
					columnas.get(TERMINO),columnas.get(SUBTERMINO));
		}else{
			tableFacade.setColumnProperties(
					columnas.get(MODULO),columnas.get(ESPECIE),columnas.get(REGIMEN),
					columnas.get(GRUPORAZA),columnas.get(TIPOANIMAL),
					columnas.get(TIPOCAPITAL),columnas.get(PROVINCIA),columnas.get(COMARCA),
					columnas.get(TERMINO),columnas.get(SUBTERMINO));
		}
	}
	
	private String html(TableFacade tableFacade,String vieneDeCargaClases) {

		// Configuracion de los datos de las columnas que requieren un
		// tratamiento para mostrarse
		// campo acciones
		HtmlTable table = (HtmlTable) tableFacade.getTable();
		table.getRow().setUniqueProperty(ID);	
		configurarColumnas(table, vieneDeCargaClases);
		
		if (!"true".equals(vieneDeCargaClases)){
        	// campo acciones
        	table.getRow().getColumn(columnas.get(ID)).getCellRenderer().setCellEditor(getCellEditorAcciones());
    	}
		
		// Subtermino
    	table.getRow().getColumn(columnas.get(SUBTERMINO)).getCellRenderer().setCellEditor(getCellEditorSubtermino());

		return tableFacade.render();
	}
	
	
	private CellEditor getCellEditorAcciones() {
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
				
				
				Long id = (Long)new BasicCellEditor().getValue(item, ID, rowcount);
				Long idClase= (Long)new BasicCellEditor().getValue(item, CLASEID, rowcount);
				Integer lineaSeguroId= (Integer)new BasicCellEditor().getValue(item, "lineaseguroid", rowcount);
				Integer codProvincia = (Integer)new BasicCellEditor().getValue(item, PROVINCIA, rowcount);
				Integer codComarca = (Integer)new BasicCellEditor().getValue(item, COMARCA, rowcount);
				Integer codTermino = (Integer)new BasicCellEditor().getValue(item, TERMINO, rowcount);
				Character codSubtermino = (Character)new BasicCellEditor().getValue(item, SUBTERMINO, rowcount);
				String codModulo = (String)new BasicCellEditor().getValue(item, MODULO, rowcount);
				Integer codEspecie = (Integer)new BasicCellEditor().getValue(item, ESPECIE, rowcount);
				Integer codRegimen= (Integer)new BasicCellEditor().getValue(item, REGIMEN, rowcount);
				Integer codGrupoRaza= (Integer)new BasicCellEditor().getValue(item, GRUPORAZA, rowcount);
				Integer codTipoAnimal= (Integer)new BasicCellEditor().getValue(item, TIPOANIMAL, rowcount);
				Integer codTipoCapital= (Integer)new BasicCellEditor().getValue(item, TIPOCAPITAL, rowcount);
				
				String descEspecie=(String)new BasicCellEditor().getValue(item, "descespecie", rowcount);
				String descRegimen=(String)new BasicCellEditor().getValue(item, "descregimen", rowcount);
				String descGrupoRaza=(String)new BasicCellEditor().getValue(item, "descgruporaza", rowcount);
				String descTipoAnimal=(String)new BasicCellEditor().getValue(item, "desctipoanimal", rowcount);
				String descTipoCapital=(String)new BasicCellEditor().getValue(item, "desctipocapital", rowcount);
				String descProvincia=(String)new BasicCellEditor().getValue(item, "descprovincia", rowcount);
				String descComarca=(String)new BasicCellEditor().getValue(item, "desccomarca", rowcount);
				String descTermino=(String)new BasicCellEditor().getValue(item, "desctermino", rowcount);
				
            	HtmlBuilder html = new HtmlBuilder();
            	
            	//DAA 05/02/2013 checkbox
            	html.append("<input type=\"checkbox\" id=\"check_" + id + "\"  name=\"check_" + id + "\" onClick =\"listaCheckId(\'" + id + "')\" class=\"dato\"/>");
                html.append(NBSP);
            	
            	// botón editar
            	html.a().href().quote().append("javascript:editar('"+id+"','"+idClase+"','"+lineaSeguroId+
            			"','"+codProvincia+"'," +"'"+codComarca+"','"+codTermino+"','"+codSubtermino+"','"+
            			codModulo+"','"+codEspecie+"','" +StringUtils.nullToString(descEspecie)+"','"+
            			codRegimen+"', '"+StringUtils.nullToString(descRegimen)+"','"+codGrupoRaza+"','"+
            			StringUtils.nullToString(descGrupoRaza)+"','"+codTipoAnimal+"','"+
            			StringUtils.nullToString(descTipoAnimal)+"','"+StringUtils.nullToString(codTipoCapital)+
            			"', '"+StringUtils.nullToString(descTipoCapital)+
            			"', '"+StringUtils.nullToString(descProvincia)+
            			"', '"+StringUtils.nullToString(descComarca)+
            			"', '"+StringUtils.nullToString(descTermino)+
            			"');").quote().close();
            	html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Detalle\" title=\"Editar Detalle\"/>");
                html.aEnd();
                html.append(NBSP);
            			
                // botón borrar 
            	html.a().href().quote().append("javascript:borrar('"+id+"');").quote().close();
                html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Detalle\" title=\"Borrar Detalle\"/>");
                html.aEnd();
                html.append(NBSP);
                	                	
            	return html.toString();
            }
		};
	}
	
	private CellEditor getCellEditorSubtermino() {
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
				Character codSubtermino = (Character)new BasicCellEditor().getValue(item, SUBTERMINO, rowcount);
            	
            	HtmlBuilder html = new HtmlBuilder();
              	html.append((codSubtermino != null && (!new Character (' ').equals(codSubtermino))) ? codSubtermino : NBSP);
            			
                	                	
            	return html.toString();
            }
		};
	}
	

	private void configurarColumnas(HtmlTable table ,String vieneDeCargaClases) {
		BigDecimal porcentaje= new BigDecimal(100.00);
		String porcentajeS=new String();
		
		
		if (!"true".equals(vieneDeCargaClases)){
			// Acciones
			BigDecimal por2= porcentaje.divide(new BigDecimal(11.00),2,RoundingMode.CEILING);
			porcentajeS=por2.toString()+"%";
	    	configColumna(table, columnas.get(ID), "&nbsp;&nbsp;Acciones", false, false, porcentajeS);
		}else {
			BigDecimal por2=porcentaje.divide(new BigDecimal(10.00),2,RoundingMode.CEILING );
			porcentajeS=por2.toString()+"%";
		}
		this.configColumna(table, columnas.get(MODULO), "Modulo", true, true, porcentajeS);		
		this.configColumna(table, columnas.get(ESPECIE), "Especie", false, true, porcentajeS);
		this.configColumna(table, columnas.get(REGIMEN), "Regimen", false, true, porcentajeS);
		this.configColumna(table, columnas.get(GRUPORAZA), "Grupo de raza", true, true, porcentajeS);
		this.configColumna(table, columnas.get(TIPOANIMAL), "Tipo de animal", true, true, porcentajeS);
		this.configColumna(table, columnas.get(TIPOCAPITAL), "Tipo de capital", true, true, porcentajeS);
		this.configColumna(table, columnas.get(PROVINCIA), "Provincia", true, true, porcentajeS);
		this.configColumna(table, columnas.get(COMARCA), "Comarca", false, true, porcentajeS);
		this.configColumna(table, columnas.get(TERMINO), "Termino", false, true, porcentajeS);
		this.configColumna(table, columnas.get(SUBTERMINO), SUBTERMINO, false, true, porcentajeS);		
	}
	
	private void cargarFiltrosBusqueda(ClaseDetalleGanado clase, TableFacade tableFacade, ClaseDetalleGanadoFilter claseDetalleGanadoFilter) {
		//clase.getClase().getLinea().getLineaseguroid()
		if(null!=clase.getClase().getId())
			this.addColumnaFiltro(tableFacade, CLASEID, clase.getClase().getId(), claseDetalleGanadoFilter);
		
		if(null!=clase.getCodmodulo())
			this.addColumnaFiltro(tableFacade, MODULO, clase.getCodmodulo(), claseDetalleGanadoFilter);
				
		if(null!=clase.getSubtermino())
			this.addColumnaFiltro(tableFacade, SUBTERMINO, clase.getSubtermino(), claseDetalleGanadoFilter);
					
		if(null!=clase.getCodtermino())
			this.addColumnaFiltro(tableFacade, TERMINO, clase.getCodtermino(), claseDetalleGanadoFilter);
						
		if(null!=clase.getCodcomarca())
			this.addColumnaFiltro(tableFacade, COMARCA, clase.getCodcomarca(), claseDetalleGanadoFilter);
		
		if(null!=clase.getCodprovincia())
			this.addColumnaFiltro(tableFacade, PROVINCIA, clase.getCodprovincia(), claseDetalleGanadoFilter);
		
		
		if(null!=clase.getCodregimen())
			this.addColumnaFiltro(tableFacade, REGIMEN, clase.getCodregimen(), claseDetalleGanadoFilter);
		
		if(null!=clase.getCodtipoanimal())
			this.addColumnaFiltro(tableFacade, TIPOANIMAL, clase.getCodtipoanimal(), claseDetalleGanadoFilter);
			
		if(null!=clase.getCodtipocapital() )
			this.addColumnaFiltro(tableFacade, TIPOCAPITAL, clase.getCodtipocapital(), claseDetalleGanadoFilter);
		
		if(null!=clase.getCodgruporaza() )
			this.addColumnaFiltro(tableFacade, GRUPORAZA, clase.getCodgruporaza(), claseDetalleGanadoFilter);
		
		if(null!=clase.getCodespecie()) 
			this.addColumnaFiltro(tableFacade, ESPECIE, clase.getCodespecie(), claseDetalleGanadoFilter);
		
		
	}

	public Map<String, Object> insertOrUpdate(ClaseDetalleGanado claseDetalleGanado) throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();	
		
		try {
						
			asignaValoresPorDefecto(claseDetalleGanado);	
			//Controlamos duplicados por gestión de errores
			if(this.isValid(claseDetalleGanado, parameters)){
				claseDetalleGanadoDao.saveOrUpdate(claseDetalleGanado);
				claseDetalleGanadoDao.evict(claseDetalleGanado);
			}			
			//iniciamos el valor de TipoCapital si es nulo porque lo hemos puesto nosostros nulo para la validación 
			/*if (null==claseDetalleGanado.getCodtipocapital())
				claseDetalleGanado.setCodtipocapital(new Integer(0));*/
			
		}catch (DAOException ex) {
			parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.clase.detalle.existe.KO"));
		} catch (Exception ex) {
			parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.clase.detalle.existe.KO"));
			logger.debug("Error al dar de alta la Clase", ex);
		} finally {

		}
		
		return parameters;
	}
	
	
	public void bajaClaseDetalle(ClaseDetalleGanado claseDetalleGanado) throws BusinessException {
		try {
			claseDetalleGanadoDao.delete(ClaseDetalleGanado.class, claseDetalleGanado.getId());
			logger.debug("ClaseDetalleGanado borrada  = " + claseDetalleGanado.getId());
		} catch (Exception ex) {
			throw new BusinessException(
					"Error al borrar la ClaseDetalleGanado",ex);
		}
	}
		
	private void asignaValoresPorDefecto(ClaseDetalleGanado claseDetalleGanado) throws DAOException {
		Long lineaSeguroId=claseDetalleGanado.getClase().getLinea().getLineaseguroid();
		
		if (claseDetalleGanado.getClase().getLinea() == null) {
			claseDetalleGanado.getClase().setLinea(new Linea());
		}
		claseDetalleGanado.getClase().getLinea().setLineaseguroid(lineaSeguroId);
		
		if (null==claseDetalleGanado.getCodespecie()) 
			claseDetalleGanado.setCodespecie(Constants.TODAS_ESPECIES);
		
		
		if (null==claseDetalleGanado.getCodregimen())
			claseDetalleGanado.setCodregimen(Constants.TODOS_REGIMEN_MANEJO);
		
		
		if (null==claseDetalleGanado.getCodgruporaza()) 
			claseDetalleGanado.setCodgruporaza(Constants.TODOS_GRUPOS_RAZAS);
		
		
		if (null==claseDetalleGanado.getCodtipoanimal()) 
			claseDetalleGanado.setCodtipoanimal(Constants.TODOS_TIPO_ANIMAL);
		
		
		//if (null==claseDetalleGanado.getCodtipocapital()||claseDetalleGanado.getCodtipocapital()==0)
		//	claseDetalleGanado.setCodtipocapital(Constants.TODAS_TIPOS_CAPITAL);
		
		claseDetalleGanado.setLineaseguroid(lineaSeguroId.intValue());
				
	}
	
	
	private boolean isValid(ClaseDetalleGanado clase, Map<String, Object> parameters) throws BusinessException, DAOException {
		
		boolean res = true;
		
		try{
			//Si no es provincia generica
			if(Constants.PROVINCIA_GENERICA.compareTo(new BigDecimal(clase.getCodprovincia()))!=0){
				res = provinciaDao.checkProvinciaExists(new BigDecimal(clase.getCodprovincia()));	
				
				if(!res){
					parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.clase.detalle.provincia.KO"));
				
				}else{
					//Si no es comarca generica
					if(Constants.COMARCA_GENERICA.compareTo(new BigDecimal(clase.getCodcomarca()))!=0){
						res = comarcaDao.checkComarcaExists(new BigDecimal(clase.getCodcomarca()), new BigDecimal(clase.getCodprovincia()));
						
						if(!res){
							parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.clase.detalle.comarca.KO"));
						
						}else{
							//Si no es termino o subtermino generico
							if(Constants.TERMINO_GENERICO.compareTo(new BigDecimal(clase.getCodtermino()))!=0
									&& !Constants.SUBTERMINO_GENERICO.equals(clase.getSubtermino())){
								Termino term = getTermino(new BigDecimal(clase.getCodprovincia()), new BigDecimal(clase.getCodcomarca()),new BigDecimal(clase.getCodtermino()), clase.getSubtermino().toString());
								res = (null!=term);

								if(!res){
									parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.clase.detalle.termino.KO"));
								}			
							}
						}
					}
				}
			}

			if(res){
				
				//Comprobación módulo
				Modulo mod = claseDetalleDao.getModulo(clase.getClase().getLinea().getLineaseguroid(), clase.getCodmodulo());
				res = (null!=mod);
				
				if(!res){
					parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.clase.detalle.modulo.KO"));
					return res;
				}
				
				//Comprobación especie
				List listaE = claseDetalleGanadoDao.getListaClase(Especie.class, new String[] {ID_LINEASEGUROID,"id.codespecie"}, 
						new Object[] {new Long(clase.getLineaseguroid()), new Long(clase.getCodespecie())}, null);
				res = (null!=listaE && listaE.size()>0);
				
				if(!res){
					parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.clase.detalleGanado.especie.KO"));
					return res;
				}
				
				//Comprobación regimen
				List listaR = claseDetalleGanadoDao.getListaClase(RegimenManejo.class, new String[] {ID_LINEASEGUROID,"id.codRegimen"}, 
						new Object[] {new Long(clase.getLineaseguroid()),new Long(clase.getCodregimen())}, null);
				res =(null!=listaR && listaR.size()>0);
				
				if(!res){
					parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.clase.detalleGanado.regimenManejo.KO"));
					return res;
				}
			
				//Comprobación módulo
				List listaGr = claseDetalleGanadoDao.getListaClase(GruposRazas.class, new String[] {ID_LINEASEGUROID,"id.CodGrupoRaza"}, 
						new Object[] {new Long(clase.getLineaseguroid()), new Long(clase.getCodgruporaza())}, null);
				res = (null!=listaGr && listaGr.size()>0);
				
				if(!res){
					parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.clase.detalleGanado.gruposRazas.KO"));
					return res;
				}
				
				//Comprobación tipo animal
				List listaTa = claseDetalleGanadoDao.getListaClase(TiposAnimalGanado.class, new String[] {ID_LINEASEGUROID,"id.codTipoAnimal"}, 
						new Object[] {new Long(clase.getLineaseguroid()), new Long(clase.getCodtipoanimal())}, null);
				res = (null!=listaTa && listaTa.size()>0);
				
				if(!res){
					parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.clase.detalleGanado.tipoAnimal.KO"));
					return res;
				}

				//Comprobación tipo capital
				if(null!=clase.getCodtipocapital()){
					List listaTc = claseDetalleGanadoDao.getListaClase(TipoCapitalConGrupoNegocio.class, new String[] {TIPOCAPITAL}, 
							new Object[] {new Long(clase.getCodtipocapital())}, null);
					res = (null!=listaTc && listaTc.size()>0);
					
					if(!res){
						parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.clase.detalleGanado.tipoCapital.KO"));
						return res;
					}
				}
				
				//Existe ClaseDetalleGanado
				ClaseDetalleGanadoFilter filtroExiste = new ClaseDetalleGanadoFilter();			
				cargarFiltrosBusqueda(clase,null, filtroExiste);
				
				if(claseDetalleGanadoDao.existeClaseDetalleGanado(filtroExiste, clase.getId())){
					parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.clase.detalle.existe.KO"));
					res = false;
				}
			}

		}catch(DAOException ex){
			parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.clase.detalleGanado.generalValidacion.KO"));
			res = false;
		}
		return res;
	}
	
	private Termino getTermino(BigDecimal provincia, BigDecimal comarca, BigDecimal termino, String  subtermino){
		List<Termino> listaTerminos=null;
		Termino term=null;
		
		if(null!=subtermino && subtermino.trim().isEmpty())subtermino=null;
		TerminoFiltro1 filtro = new TerminoFiltro1(provincia,comarca,termino,subtermino);
		
		listaTerminos= claseDetalleGanadoDao.getObjects(filtro);
		if(null!=listaTerminos && listaTerminos.size()>0)
			term=listaTerminos.get(0);
		return term;
	}
	
	
	public Map<String, Object> cambioMasivo(String listaIdsMarcados_cm, ClaseDetalleGanado beanCM, String tipoCapitalCheck) {
		Map<String, Object> parameters = new HashMap<String, Object>();	
		boolean isAlgunoActualizado = false;
		boolean isAlgunoSinActualizar = false;
		
		try {
			String listaIds = listaIdsMarcados_cm.substring(0,listaIdsMarcados_cm.length()-1);
			
			//recorro la lista de ids y cargo los objetos de bbdd y los voy modificando y si existe no lo guardo
			String[] ids = listaIds.split(",");
			ClaseDetalleGanado cd = new ClaseDetalleGanado();
			
			//por alguna razón viene inicializado a ""
			if (beanCM.getCodmodulo()!=null && beanCM.getCodmodulo().equals("")) {
				beanCM.setCodmodulo(null);
			}
			
			for (int i=0; i<ids.length;i++){
				cd = (ClaseDetalleGanado) claseDetalleGanadoDao.getObject(ClaseDetalleGanado.class, new Long(ids[i]));
				claseDetalleGanadoDao.evict(cd);
				//MODULO
				if(beanCM.getCodmodulo()!=null) {
					cd.setCodmodulo(beanCM.getCodmodulo());
				}
				//PROVINCIA
				if (beanCM.getCodprovincia()!= null){
					cd.setCodprovincia(beanCM.getCodprovincia());
				}
				//COMARCA
				if (beanCM.getCodcomarca()!= null){
					cd.setCodcomarca(beanCM.getCodcomarca());
				}
				//TERMINO
				if (beanCM.getCodtermino()!= null){
					cd.setCodtermino(beanCM.getCodtermino());
				}
				//SUBTERMINO
				if (beanCM.getSubtermino()!= null){
					cd.setSubtermino(beanCM.getSubtermino());
				}
				//ESPECIE
				Integer codEspecie=beanCM.getCodespecie();
				if(null!=codEspecie) {
					cd.setCodespecie(codEspecie);
				}
			
				//GRUPO DE RAZA
				Integer codGrupoRaza=beanCM.getCodgruporaza();
				if(null!=codGrupoRaza) {
					cd.setCodgruporaza(codGrupoRaza);
				}

				//REGIMEN MANEJO
				Integer codRegimen=beanCM.getCodregimen();
				if(null!=codRegimen) {
					cd.setCodregimen(codRegimen);
				}

				//TIPO DE ANIMAL
				Integer codTipoAnimal=beanCM.getCodtipoanimal();
				if(null!=codTipoAnimal) {
					cd.setCodtipoanimal(codTipoAnimal);
				}

				//TIPO DE CAPITAL
				Integer codTipoCapital=beanCM.getCodtipocapital();
				if(null!=codTipoCapital) {
					cd.setCodtipocapital(codTipoCapital);					
				}
				if (tipoCapitalCheck.equals("tipoCapitalCheck")){ //Es que ha pinchado el check y hay que actualizar a valor por defecto
					cd.setCodtipocapital(null);	
				}
				
				if(this.isValid(cd, parameters)){
					claseDetalleGanadoDao.saveOrUpdate(cd);
					isAlgunoActualizado = true;
				}else {
					isAlgunoSinActualizar = true;
				}		
			}
			
			if (isAlgunoActualizado && !isAlgunoSinActualizar){
				parameters.put(Constants.KEY_MENSAJE, bundle.getString("mensaje.clase.detalle.edicion.OK"));
			
			}else if(isAlgunoActualizado && isAlgunoSinActualizar){
				parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.clase.detalle.edicion.soloAlgunosOK"));
				parameters.remove(Constants.KEY_MENSAJE);
			
			}else if(!isAlgunoActualizado){
				parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.clase.detalle.edicion.KO"));
			}

		} catch ( BusinessException e) {
			parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.clase.detalle.edicion.KO"));
			logger.debug("Error al ejecutar el Cambio Masivo en la clase de detalle de ganado. ", e);
		} catch (DAOException e) {
			parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.clase.detalle.edicion.KO"));
			logger.debug("Error al ejecutar el Cambio Masivo en la clase de detalle de ganado. ", e);
		}
		return parameters;
	
	}
	
	
	public ClaseDetalleGanado getBeanFromLimit(Limit consulta_LIMIT) {
		ClaseDetalleGanado bean = new ClaseDetalleGanado();
		
		//CLASEID
		if(null != consulta_LIMIT.getFilterSet().getFilter(CLASEID)){
			bean.getClase().setId(new Long(consulta_LIMIT.getFilterSet().getFilter(CLASEID).getValue()));
			Clase c = (Clase) claseDetalleDao.getObject(Clase.class, bean.getClase().getId());
			bean.setClase(c);
		}
		// MODULO
		if(null != consulta_LIMIT.getFilterSet().getFilter(columnas.get(MODULO))){
			bean.setCodmodulo(consulta_LIMIT.getFilterSet().getFilter(columnas.get(MODULO)).getValue());
		}
	
		// PROVINCIA
		if(null != consulta_LIMIT.getFilterSet().getFilter(columnas.get(PROVINCIA))){
			bean.setCodprovincia(new Integer(consulta_LIMIT.getFilterSet().getFilter(columnas.get(PROVINCIA)).getValue()));
		}
		// COMARCA
		if(null != consulta_LIMIT.getFilterSet().getFilter(columnas.get(COMARCA))){
			bean.setCodcomarca(new Integer(consulta_LIMIT.getFilterSet().getFilter(columnas.get(COMARCA)).getValue()));
		}
		// TERMINO
		if(null != consulta_LIMIT.getFilterSet().getFilter(columnas.get(TERMINO))){
			bean.setCodtermino(new Integer(consulta_LIMIT.getFilterSet().getFilter(columnas.get(TERMINO)).getValue()));
		}
		// SUBTERMINO
		if(null != consulta_LIMIT.getFilterSet().getFilter(columnas.get(SUBTERMINO))){
			bean.setSubtermino(consulta_LIMIT.getFilterSet().getFilter(columnas.get(SUBTERMINO)).getValue().charAt(0));
		}
		//ESPECIE
		if(null != consulta_LIMIT.getFilterSet().getFilter(columnas.get(ESPECIE))){
			bean.setCodespecie(new Integer (consulta_LIMIT.getFilterSet().getFilter(columnas.get(ESPECIE)).getValue()));
		}
		//RÓ‰GIMEN MANEJO
		if(null != consulta_LIMIT.getFilterSet().getFilter(columnas.get(REGIMEN))){
			bean.setCodregimen(new Integer (consulta_LIMIT.getFilterSet().getFilter(columnas.get(REGIMEN)).getValue()));
		}
		//GRUPOS RAZAS
		if(null != consulta_LIMIT.getFilterSet().getFilter(columnas.get(GRUPORAZA))){
			bean.setCodgruporaza(new Integer (consulta_LIMIT.getFilterSet().getFilter(columnas.get(GRUPORAZA)).getValue()));
		}
		//TIPO DE ANIMAL
		if(null != consulta_LIMIT.getFilterSet().getFilter(columnas.get(TIPOANIMAL))){
			bean.setCodtipoanimal(new Integer (consulta_LIMIT.getFilterSet().getFilter(columnas.get(TIPOANIMAL)).getValue()));
		}
		//TIPO DE CAPITAL
		if(null != consulta_LIMIT.getFilterSet().getFilter(columnas.get(TIPOCAPITAL))){
			bean.setCodtipocapital(new Integer (consulta_LIMIT.getFilterSet().getFilter(columnas.get(TIPOCAPITAL)).getValue()));
		}		
		return bean;
	}	
	
	public void importaFichero( Map<String, Object> parametros, MultipartFile file) throws Exception , IOException{
		/**
		* ESPECIFICACION
		*0 - Plan: numerico de 4 posiciones.
		*1 - Linea: numerico de 3 posiciones.
		*2 - Clase: numerico de 2 posiciones.
		*3 - Modulo: alfanumerico de 5 posiciones.
		*4 - Especie: numerico de 3 posiciones.
		*5 - Regimen: numerico de 3 posiciones.
		*6 - Grupo de raza: numerico de 3 posiciones.
		*7 - Tipo de animal: numerico de 3 posiciones.
		*8 - Tipo de capital: numerico de 3 posiciones.
		*9 - Provincia: numerico de 2 posiciones.
		*10 - Comarca: numerico de 2 posiciones.
		*11 - Termino: numerico de 3 posiciones.
		*12 - Subtermino: alfanumerico de 1 posición.
		*/
		String str= "";
		int numRegTotal = 0; 
		int numRegOK=0; 
		int numRegKO=0;
		try (DataInputStream input = new DataInputStream (file.getInputStream())) {
			
			String codplan=""; 
			String codlinea=""; 
			String codclase="";
			Long lineaseguroid=null; 
			Long idclase=null;
			
			//recorremos el fichero linea a linea
			while (null != ((str = input.readLine()))){
				numRegTotal++;
				//transformar str y grabar
				String[] strSplit= str.split(";");
				try {
					if(codplan.compareTo(strSplit[0])!=0 && codlinea.compareTo(strSplit[1])!=0) {
						List<Linea> lineas;
						
							lineas = claseDetalleGanadoDao.getListaClase(Linea.class, new String[] {"codplan","codlinea"}, 
									new Object[] {new BigDecimal(strSplit[0]), new BigDecimal(strSplit[1])}, null);
							if(null!=lineas && lineas.size()>0)
								lineaseguroid=lineas.get(0).getLineaseguroid();
							
							
					}
					if(codclase.compareTo(strSplit[2])!=0) {
						List<Clase> clases= claseDetalleGanadoDao.getListaClase(Clase.class, new String[] {"linea.lineaseguroid","clase"}, 
								new Object[] {lineaseguroid, new BigDecimal(strSplit[2])}, null);
						
						if(null!=clases && clases.size()>0)
							idclase=clases.get(0).getId();			
					}
					ClaseDetalleGanado claseDetalleGanado = getClaseDetalleGanado(lineaseguroid, idclase, strSplit);
					claseDetalleGanadoDao.saveOrUpdate(claseDetalleGanado);
					numRegOK++;
				
				} catch (DAOException e) {
					numRegKO++;
					logger.error("Error en el registro " + numRegTotal + ". " + e.getMessage(), e);
				}catch (Exception ex) {
					numRegKO++;
					logger.error("Error en el registro " + numRegTotal + ". " + ex.getMessage(), ex);
				}
				codplan= strSplit[0];codlinea= strSplit[1];codclase= strSplit[2];
				
			}
			
			if(numRegKO!=0) {//Errores
				if(numRegKO==numRegTotal) {//Todo error
					parametros.put(Constants.KEY_ALERTA, bundle.getString("mensaje.clase.detalleGanado.importacion.KO") + ". No se ha grabado ningÓºn registro de " + numRegTotal);
				}else {
					parametros.put(Constants.KEY_ALERTA, bundle.getString("mensaje.clase.detalleGanado.importacion.KO") + ". Registros totales: " +numRegTotal + ". Registros erróneos: " + numRegKO + ". Registros grabados correctamente:" + numRegOK);
				}
			}else {
				parametros.put(Constants.KEY_MENSAJE, bundle.getString("mensaje.clase.detalleGanado.importacion.OK"));	
			}
					
			logger.debug("Importación del fichero de clases de detalle de ganado. Registros totales: " +numRegTotal + ". Registros erróneos: " + numRegKO + ". Registros grabados correctamente:" + numRegOK);
		}catch (IOException ioe) {
			throw new IOException(ioe);	
		}catch (Exception exn) {
			throw new Exception(exn);	
		}
	}
	
	
	private ClaseDetalleGanado getClaseDetalleGanado(Long lineaseguroid, Long idClase, String[] strSplit) {
		/**
		* ESPECIFICACION
		*0 - Plan: numerico de 4 posiciones.
		*1 - Linea: numerico de 3 posiciones.
		*2 - Clase: numerico de 2 posiciones.
		*3 - Modulo: alfanumerico de 5 posiciones.
		*4 - Especie: numerico de 3 posiciones.
		*5 - Regimen: numerico de 3 posiciones.
		*6 - Grupo de raza: numerico de 3 posiciones.
		*7 - Tipo de animal: numerico de 3 posiciones.
		*8 - Tipo de capital: numerico de 3 posiciones.
		*9 - Provincia: numerico de 2 posiciones.
		*10 - Comarca: numerico de 2 posiciones.
		*11 - Termino: numerico de 3 posiciones.
		*12 - Subtermino: alfanumerico de 1 posición.
		*/
		ClaseDetalleGanado clase = new ClaseDetalleGanado();
		clase.setClase(new Clase());
		clase.getClase().setClase(new BigDecimal(strSplit[2]));
		clase.getClase().setId(idClase);
		clase.setCodmodulo(strSplit[3]);
		clase.setCodespecie(new Integer(strSplit[4]));
		clase.setLineaseguroid(new Integer(lineaseguroid.intValue()));
		clase.setCodregimen(new Integer(strSplit[5]));		
		clase.setCodgruporaza(new Integer(strSplit[6]));		
		clase.setCodtipoanimal(new Integer(strSplit[7]));		
		clase.setCodtipocapital(new Integer(strSplit[8]));		
		clase.setCodprovincia(new Integer(strSplit[9]));
		clase.setCodcomarca(new Integer(strSplit[10]));
		clase.setCodtermino(new Integer(strSplit[11]));
		clase.setSubtermino(strSplit[12].charAt(0));	
		
		return clase;
	}

	public void setClaseDetalleGanadoFilter(ClaseDetalleGanadoFilter claseDetalleGanadoFilter) {
		this.claseDetalleGanadoFilter = claseDetalleGanadoFilter;
	}

	public void setClaseDetalleGanadoSort(IGenericoSort claseDetalleGanadoSort) {
		this.claseDetalleGanadoSort = claseDetalleGanadoSort;
	}
	
	public void setClaseDetalleDao(IClaseDetalleDao claseDetalleDao) {
		this.claseDetalleDao = claseDetalleDao;
	}
	
	public void setClaseDetalleGanadoDao(IClaseDetalleGanadoDao claseDetalleGanadoDao) {
		this.claseDetalleGanadoDao = claseDetalleGanadoDao;
	}
	
	public void setProvinciaDao(IProvinciaDao provinciaDao) {
		this.provinciaDao = provinciaDao;
	}

	public void setComarcaDao(IComarcaDao comarcaDao) {
		this.comarcaDao = comarcaDao;
	}
}