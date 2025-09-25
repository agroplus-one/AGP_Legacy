package com.rsi.agp.core.jmesa.service.impl.utilidades;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
import com.rsi.agp.core.jmesa.filter.AnexoModificacionFilter;
import com.rsi.agp.core.jmesa.filter.LongFilterMatcher;
import com.rsi.agp.core.jmesa.service.utilidades.IAnexoModificacionUtilidadesService;
import com.rsi.agp.core.jmesa.sort.AnexoModificacionSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.DateUtil;
import com.rsi.agp.dao.filters.BigDecimalFilterMatcher;
import com.rsi.agp.dao.filters.CharacterFilterMatcher;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.poliza.IAnexoModificacionDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.SubvDeclarada;
import com.rsi.agp.dao.tables.commons.Usuario;


public class AnexoModificacionUtilidadesService implements IAnexoModificacionUtilidadesService{
	
	private String id = "listadoAnexoModificacion";
	private Log logger = LogFactory.getLog(getClass());
	private IAnexoModificacionDao anexoModificacionDao; 
	
	
	// Nombres de los campos de la tabla
	public static final String CAMPO_ID = "id";
	public static final String CAMPO_IDPOLIZA = "poliza.idpoliza";
	public static final String CAMPO_ENTIDAD = "poliza.colectivo.tomador.id.codentidad";
	public static final String CAMPO_OFICINA = "poliza.oficina";
	public static final String CAMPO_USUARIO = "poliza.usuario.codusuario";
	public static final String CAMPO_PLAN = "poliza.linea.codplan";
	public static final String CAMPO_LINEA = "poliza.linea.codlinea";
	public static final String CAMPO_POLIZA = "poliza.referencia";
	public static final String CAMPO_MODULO = "poliza.codmodulo";
	public static final String CAMPO_DC = "poliza.dc";
    public static final String CAMPO_NIF = "poliza.asegurado.nifcif";
    public static final String CAMPO_FULLNAME = "poliza.asegurado.fullName";
    public static final String CAMPO_RAZONSOCIAL = "poliza.asegurado.razonsocial";
    public static final String CAMPO_ASUNTO = "asunto";
    public static final String CAMPO_IDESTADO = "estado.idestado";
    public static final String CAMPO_ESTADO = "estado.descestado";
    public static final String CAMPO_TIPOREF = "poliza.tipoReferencia";    
    public static final String CAMPO_FEC_ENVIO_ANEXO = "fechaEnvioAnexo";    
    
    public static final String CAMPO_COBERTURAS = "coberturas";
    public static final String CAMPO_PARCELAS = "parcelas";
    public static final String CAMPO_SUBDECLA = "subvDeclaradas";
    public static final String CAMPO_IDENVIO = "comunicaciones.idenvio";
    
    public static final String CAMPO_LISTADOGRUPOENT = "listaGrupoEntidades";   
    public static final String CAMPO_LISTADOGRUPOOFI = "listaGrupoOficinas";  
    
    public static final String CAMPO_IDCUPON = "cupon.idcupon";
    public static final String CAMPO_IDCUPON_NUM = "cupon.id";
    public static final String CAMPO_TIPO_AM = "tipoEnvio";
    public static final String CAMPO_ESTADO_CUPON = "cupon.estadoCupon.id";
    public static final String CAMPO_ESTADO_CUPON_DESC = "cupon.estadoCupon.estado";
    
    public static final String CAMPO_ENTMEDIADORA = "poliza.colectivo.subentidadMediadora.id.codentidad";
    public static final String CAMPO_SUBENTMEDIADORA = "poliza.colectivo.subentidadMediadora.id.codsubentidad";
    public static final String CAMPO_DELEGACION = "poliza.usuario.delegacion";
    
    public static final String CAMPO_FECHA_ENVIO_AM_CUPON = "cupon.fechaEnvio";
    // MODIF TAM (29.03.2019) ** Inicio 
    //public static final String CAMPO_ESTADO_AGROSEGURO = "poliza.estadoAgroseguro.descEstado";
    public static final String CAMPO_ESTADO_AGROSEGURO = "estadoAgroseguro.descripcion";
    //public static final String CAMPO_FECHA_SEGUIMIENTO = "poliza.fechaSeguimiento";
    public static final String CAMPO_FECHA_SEGUIMIENTO = "fechaSeguimiento";
    
    public AnexoModificacionFilter anexoModificacionFilter;
    public AnexoModificacionSort anexoModificacionSort;
    
    @Override
	public int getAnexoModificacionCountWithFilter(AnexoModificacionFilter filter) throws BusinessException{
		return this.anexoModificacionDao.getAnexoModificacionCountWithFilter(filter);
	}

	@Override
	public Collection<AnexoModificacion> getAnexoModificacionWithFilterAndSort(AnexoModificacionFilter filter,
			AnexoModificacionSort sort, int rowStart,	int rowEnd) throws BusinessException {
		
		return this.anexoModificacionDao.getAnexoModificacionWithFilterAndSort(filter, sort, rowStart, rowEnd);
	}
	
	@Override
	public String getTablaAnexoModificacion(HttpServletRequest request,
			HttpServletResponse response, AnexoModificacion anexoMod,
			String primeraBusqueda, List<BigDecimal> listaGrupoEntidades,List<BigDecimal> listaGrupoOficinas) {
		
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response, anexoMod, primeraBusqueda);
		
		// Configura el filtro y la ordenacion, busca los Red. Capital y los carga en el TableFacade
		setDataAndLimitVariables(tableFacade, listaGrupoEntidades,listaGrupoOficinas);
		
		// Si se esta generando un informe no se establecen los custom
		String ajax = request.getParameter("ajax");
		if (!"false".equals(ajax)){
			tableFacade.setToolbar(new CustomToolbar());
			tableFacade.setView(new CustomView());
		}
		
		// Obtiene el perfil del usuario para pasarlo al metodo que pinta el listado
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		// Genera el html de la tabla y lo devuelve	
		return html (tableFacade, usuario != null ? usuario.getPerfil() : null);	
	}

	@SuppressWarnings("deprecation")
	private String html(TableFacade tableFacade, String codUsuario) {
		Limit limit = tableFacade.getLimit();
    	
    	// Si se va a exportar a un informe el listado
        if (limit.isExported()) {
        	Table table = tableFacade.getTable();
        	// Quita la columna Id del informe
        	eliminarColumnaId(tableFacade, table);
        	// renombramos las cabeceras
            configurarCabecerasColumnasExport(table);
            
            // Para el Tipo AM, tenemos que controlar el valor del campo llamando a getCellEditorTipoAM() para que muestre el cupon en 
            // lugar del tipoEnvio y asi coincida el valor del tablefacade con el excel
        	table.getRow().getColumn(CAMPO_TIPO_AM).getCellRenderer().setCellEditor(getCellEditorTipoAM());
            
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
            // Configuracion de las columnas de la tabla    
        	configurarColumnas(table);
        	// Configuracion de los datos de las columnas que requieren un tratamiento para mostrarse
        	// Acciones
        	table.getRow().getColumn(CAMPO_ID).getCellRenderer().setCellEditor(getCellEditorAcciones(codUsuario)); 
        	// Referencia
        	table.getRow().getColumn(CAMPO_POLIZA).getCellRenderer().setCellEditor(getCellEditorPoliza());
        	// Modulo
        	table.getRow().getColumn(CAMPO_MODULO).getCellRenderer().setCellEditor(getCellEditorModulo());
        	// Asegurado
        	table.getRow().getColumn(CAMPO_FULLNAME).getCellRenderer().setCellEditor(getCellEditorAsegurado());
        	//ASUNTO
        	table.getRow().getColumn(CAMPO_ASUNTO).getCellRenderer().setCellEditor(getCellEditorModificacion());
        	// Tipo AM
        	table.getRow().getColumn(CAMPO_TIPO_AM).getCellRenderer().setCellEditor(getCellEditorTipoAM());
        	// Fecha de envio
        	table.getRow().getColumn(CAMPO_FEC_ENVIO_ANEXO).getCellRenderer().setCellEditor(getCellEditorFechaEnvioAM(null));
        }
    	// Devuelve el html de la tabla
    	return tableFacade.render();
	}
	
	
	 /**
     * Metodo que formatea los datos que se muestran en las celdas de la columna 'Acciones'
     * @return
     */
    private CellEditor getCellEditorAcciones(final String codUsuario) {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) { 
				
				// Estado del idRedCapital
				BigDecimal estado = (BigDecimal)new BasicCellEditor().getValue(item, CAMPO_IDESTADO, rowcount);
				// Id del RedCapital
				String idAnexoMod = new BasicCellEditor().getValue(item, CAMPO_ID, rowcount).toString();
				// Id de la poliza asociada al anexo Mod
				String idPoliza = new BasicCellEditor().getValue(item, CAMPO_IDPOLIZA, rowcount).toString();
				//Tipo poliza Normal o CPL
				Character tipo = (Character) new BasicCellEditor().getValue(item,CAMPO_TIPOREF,rowcount);
				// Id de envio del anexo
				BigDecimal  idEnvio= (BigDecimal) new BasicCellEditor().getValue(item,CAMPO_IDENVIO,rowcount);
				// Referencia de la poliza asociada
				String referencia = new BasicCellEditor().getValue(item,CAMPO_POLIZA,rowcount).toString();
				// Plan de la poliza asociada
				String plan = new BasicCellEditor().getValue(item,CAMPO_PLAN,rowcount).toString();
				// ftp o sw
				String tipoAM = (String) new BasicCellEditor().getValue(item, CAMPO_TIPO_AM, rowcount);
				// idCupon
				String idCupon = (String) new BasicCellEditor().getValue(item, CAMPO_IDCUPON, rowcount);
				// Id cupon numerico
				Long idCuponNum = (Long) new BasicCellEditor().getValue(item, CAMPO_IDCUPON_NUM, rowcount);
				// Id del estado del cupon
				Long idEstadoCupon = (Long) new BasicCellEditor().getValue(item, CAMPO_ESTADO_CUPON, rowcount);
				int numIconos = 0;
				// Lista de estados de am por cupon que permite ver el acuse de recibo
				List<Long> lstEstadosCuponAcuse = new ArrayList<Long>();
				lstEstadosCuponAcuse.add(Constants.AM_CUPON_ESTADO_ERROR_RECHAZADO);
				lstEstadosCuponAcuse.add(Constants.AM_CUPON_ESTADO_ERROR_TRAMITE);
				lstEstadosCuponAcuse.add(Constants.AM_CUPON_ESTADO_CONFIRMADO_TRAMITE);
				lstEstadosCuponAcuse.add(Constants.AM_CUPON_ESTADO_CONFIRMADO_APLICADO);
				lstEstadosCuponAcuse.add(Constants.AM_CUPON_ESTADO_ERROR);
				
				HtmlBuilder html = new HtmlBuilder();
				boolean editar = true;
				// EDITAR
				if (!estado.equals(Constants.ANEXO_MODIF_ESTADO_ENVIADO) && !estado.equals(Constants.ANEXO_MODIF_ESTADO_CORRECTO)) {
					if (Constants.ANEXO_MODIF_TIPO_ENVIO_SW.equals(tipoAM)) {
						if (idEstadoCupon.equals(Constants.AM_CUPON_ESTADO_CADUCADO)) {
							
							html.a().href().quote().append("javascript:editarAMCuponCaducado(" + idAnexoMod + "," + idPoliza 
									+ ",'" + referencia + "'," + plan + ")").quote().close();
							html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Anexo\" title=\"Editar Anexo\"/>");
			                html.aEnd();
			                html.append("&nbsp;");
			                
							editar = false;
						}
					}
					if (editar) {
						numIconos ++;
						if(tipo.equals(Constants.MODULO_POLIZA_PRINCIPAL))
							html.a().href().quote().append("javascript:editar(" + idAnexoMod + "," + idPoliza + "," + estado + ");").quote().close();
						else if (tipo.equals(Constants.MODULO_POLIZA_COMPLEMENTARIO))
							html.a().href().quote().append("javascript:editarCpl(" + idAnexoMod + "," + estado + "," + idPoliza + ");").quote().close();
						html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Anexo\" title=\"Editar Anexo\"/>");
		                html.aEnd();
		                html.append("&nbsp;");
					}
				}
				
				// ELIMINAR				
				if (estado.equals(Constants.ANEXO_MODIF_ESTADO_BORRADOR) || estado.equals(Constants.ANEXO_MODIF_ESTADO_ERROR)){
					boolean eliminar = true;
					
					if (eliminar) {	
						numIconos ++;
						html.a().href().quote().append("javascript:eliminar(" + idAnexoMod + "," + idPoliza + ");").quote().close();
						html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Anexo\" title=\"Borrar Anexo\"/>");
		                html.aEnd();
		                html.append("&nbsp;");
					}
				}
				
				// IMPRIMIR
				numIconos ++;
				boolean imprimirNormal = false;
				//tipo sw
				if (Constants.ANEXO_MODIF_TIPO_ENVIO_SW.equals(tipoAM)) {
					if (idEstadoCupon.equals(Constants.AM_CUPON_ESTADO_CONFIRMADO_APLICADO)) {
						html.a().href().quote().append("javascript:imprimirSwPDFIncidencia('"+idCupon+"');").quote().close();
						html.append("<img src=\"jsp/img/displaytag/imprimir.png\" alt=\"Imprimir Anexo\" title=\"Imprimir Anexo\"/>");
							
						html.aEnd();
		                html.append("&nbsp;");
					}else {
						imprimirNormal = true;
					}
				}else {
					imprimirNormal = true;
				}
				if (imprimirNormal) {
					if(tipo.equals(Constants.MODULO_POLIZA_PRINCIPAL))
						html.a().href().quote().append("javascript:imprimir(" + idAnexoMod + "," + idPoliza + ");").quote().close();
					else if (tipo.equals(Constants.MODULO_POLIZA_COMPLEMENTARIO))
						html.a().href().quote().append("javascript:imprimirCpl(" + idAnexoMod + "," + idPoliza + ");").quote().close();
				
					html.append("<img src=\"jsp/img/displaytag/imprimir.png\" alt=\"Imprimir Anexo\" title=\"Imprimir Anexo\"/>");
					html.aEnd();
					html.append("&nbsp;");
				}
                
                
                if (numIconos == 3) {
                	html.append("<br>");
                	numIconos = 0;
	            }
                
	           if (Constants.ANEXO_MODIF_TIPO_ENVIO_SW.equals(tipoAM)) {
					if(tipo.equals(Constants.MODULO_POLIZA_PRINCIPAL))
						html.a().href().quote().append("javascript:imprimirSw('" + idCupon + "'," + idAnexoMod + ",'"+referencia+"');").quote().close();
					else if (tipo.equals(Constants.MODULO_POLIZA_COMPLEMENTARIO))
						html.a().href().quote().append("javascript:imprimirCplSw('" + idCupon + "'," + idAnexoMod + ",'"+referencia+"');").quote().close();
					html.append("<img src=\"jsp/img/displaytag/imprimir_poliza_modificada.png\" alt=\"Imprimir P&oacute;liza Modificada\" title=\"Imprimir P&oacute;liza Modificada\"/>");
	                html.aEnd();
	                html.append("&nbsp;");
	                if (numIconos == 3) {
	                	html.append("<br>");
	                	numIconos = 0;
		            }
				}
                
				// PASAR A DEFINITIVA/CONFIRMAR
				if (estado.equals(Constants.ANEXO_MODIF_ESTADO_BORRADOR)) {
					
					
					// Tipo FTP
					if (Constants.ANEXO_MODIF_TIPO_ENVIO_FTP.equals(tipoAM)) {
							//if (!StringUtils.nullToString(codUsuario).equals("") && Constants.PERFIL_USUARIO_ADMINISTRADOR.equals(codUsuario)) {
							numIconos ++;
							if(tipo.equals(Constants.MODULO_POLIZA_PRINCIPAL))
								html.a().href().quote().append("javascript:pasarDefinitivo(" + idAnexoMod + "," + idPoliza + ");").quote().close();
							else if (tipo.equals(Constants.MODULO_POLIZA_COMPLEMENTARIO))
								html.a().href().quote().append("javascript:pasarDefinitivoCpl(" + idAnexoMod + "," + idPoliza + ");").quote().close();
							html.append("<img src=\"jsp/img/displaytag/accept.png\" alt=\"Pasar a definitiva el Anexo\" title=\"Pasar a definitiva el Anexo\"/>");
			                html.aEnd();
			                html.append("&nbsp;");
						//}
					}
					// Tipo Cupon
					else {
						
						if (!idEstadoCupon.equals(Constants.AM_CUPON_ESTADO_CADUCADO)) {
							html.a().href().quote().append("javascript:validarAMCupon (" + idCuponNum + ","+ idAnexoMod + ");").quote().close();
							html.append("<img src=\"jsp/img/displaytag/accept.png\" alt=\"Confirmar el Anexo\" title=\"Confirmar el Anexo\"/>");
			                html.aEnd();
			                html.append("&nbsp;");
			                numIconos ++;
						}
					}
				// Si no esta en estado 'Borrador' pero es un anexo ws con cupon en estado 'Error-Tramite' se permite confirmar
				}else {
					if (Constants.ANEXO_MODIF_TIPO_ENVIO_SW.equals(tipoAM)) {
						if (Constants.AM_CUPON_ESTADO_ERROR_TRAMITE.equals(idEstadoCupon)) {
							Long id = (Long) new BasicCellEditor().getValue(item, CAMPO_IDCUPON_NUM, rowcount);
							html.a().href().quote().append("javascript:validarAMCupon (" + id + ","+ idAnexoMod + ");").quote().close();
							html.append("<img src=\"jsp/img/displaytag/accept.png\" alt=\"Confirmar el Anexo\" title=\"Confirmar el Anexo\"/>");
			                html.aEnd();
			                html.append("&nbsp;");
			                numIconos ++;
						}
					}
				}
				if (numIconos == 3) {
                	html.append("<br>");
                	numIconos = 0;
                }
				// VER ERRORES
				// Tipo FTP
				if (Constants.ANEXO_MODIF_TIPO_ENVIO_FTP.equals(tipoAM)) {
					if (idEnvio!= null && !estado.equals(Constants.ANEXO_MODIF_ESTADO_CORRECTO)) {
						numIconos ++;
						html.a().href().quote().append("javascript:verErrores(" + idAnexoMod + ");").quote().close();
						html.append("<img src=\"jsp/img/displaytag/acuserecibo.png\" alt=\"Consultar Acuse de Recibo\" title=\"Consultar Acuse de Recibo\"/>");
		                html.aEnd();
		                html.append("&nbsp;");
		                if (numIconos == 3) {
		                	html.append("<br>");
		                	numIconos = 0;
		                }
					}
				}
				// Tipo Cupon
				else {
					// Se puede visualizar el acuse de recibo si el cupon tiene estado 'Error-Rechazado', 'Error-Tramite', 'Confirmado-Tramite'
					// o 'Confirmado-Aplicado' o Error
					if (lstEstadosCuponAcuse.contains(idEstadoCupon)) {
						
						numIconos ++;
						html.a().href().quote().append("javascript:verAcuseConfirmacion(" + idAnexoMod +","+ idPoliza+","+idCuponNum + ");").quote().close();
						html.append("<img src=\"jsp/img/displaytag/acuserecibo.png\" alt=\"Consultar Acuse de Recibo\" title=\"Consultar Acuse de Recibo\"/>");
		                html.aEnd();
		                html.append("&nbsp;");
		                if (numIconos == 3) {
		                	html.append("<br>");
		                	numIconos = 0;
		                }
					}
				}
				boolean info=false;
				// Informacion
				if (estado.equals(Constants.ANEXO_MODIF_ESTADO_ENVIADO) || estado.equals(Constants.ANEXO_MODIF_ESTADO_CORRECTO)){
					info= true;
				}
				if (idEstadoCupon != null) {
					 if (idEstadoCupon.equals(Constants.AM_CUPON_ESTADO_CADUCADO)){
						 info = true;
					 }
				}
				if (info) {
					numIconos ++;
					if (tipo.equals(Constants.MODULO_POLIZA_PRINCIPAL))
						html.a().href().quote().append("javascript:verInformacion(" + idAnexoMod + "," + idPoliza + ");").quote().close();
					else if(tipo.equals(Constants.MODULO_POLIZA_COMPLEMENTARIO))
						html.a().href().quote().append("javascript:verInformacionCpl(" + idAnexoMod + "," + idPoliza + ");").quote().close();
					html.append("<img src=\"jsp/img/displaytag/information.png\" alt=\"Ver\" title=\"Ver\"/>");
	                html.aEnd();
	                html.append("&nbsp;");
	                if (numIconos == 3) {
	                	html.append("<br>");
	                	numIconos = 0;
	                }
				}
				// Ver situacion actual de la poliza				
				html.a().href().quote().append("javascript:verSituacionActual(" + idAnexoMod + ",'" + referencia + "','" + plan +"','"+tipo+"');").quote().close();
				html.append("<img src=\"jsp/img/displaytag/visualizar.png\" alt=\"Ver situaci&oacute;n actual de la p&oacute;liza\" title=\"Ver situaci&oacute;n actual de la p&oacute;liza\"/>");
                html.aEnd();
                        	
                return  html.toString();
            }
		};
	}
    
    /**
     * Metodo que formatea los datos que se muestran en las celdas de la columna 'modulo'
     * @return
     */
    private CellEditor getCellEditorModulo() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				Object modulo = new BasicCellEditor().getValue(item, "codmodulo", rowcount);
				if (modulo == null){
					modulo = new BasicCellEditor().getValue(item, CAMPO_MODULO, rowcount);
				}
				
				HtmlBuilder html = new HtmlBuilder();
				
				String moduloStr = modulo != null ? modulo.toString() : "&nbsp;";
				html.append(moduloStr);
				return html.toString();
            }
		};
	}
    
    /**
     * Metodo que formatea los datos que se muestran en las celdas de la columna 'Poliza'
     * @return
     */
    private CellEditor getCellEditorPoliza() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				
				Object objref = new BasicCellEditor().getValue(item, CAMPO_POLIZA, rowcount);
				Object objdc = new BasicCellEditor().getValue(item, CAMPO_DC, rowcount);
				
				// Control de la referencia de poliza del anexoMod
				String ref = objref != null ? objref.toString() : "";
				String dc = objdc != null ? objdc.toString() : "";
				
				HtmlBuilder html = new HtmlBuilder();
				if ("".equals(ref))
					html.append("&nbsp;");
				else
					html.append("".equals(dc) ? ref : ref + "-" + dc);
				
				return html.toString();
            }
		};
	}
    /**
     * Metodo que formatea los datos que se muestran en las celdas de la columna 'Asegurado'
     * @return
     */
    private CellEditor getCellEditorAsegurado() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				
				Object fullName = new BasicCellEditor().getValue(item, CAMPO_FULLNAME, rowcount);
				Object razonsocial = new BasicCellEditor().getValue(item, CAMPO_RAZONSOCIAL, rowcount);
				
				HtmlBuilder html = new HtmlBuilder();
				
				String fullNameAux = fullName != null ? fullName.toString() : "";
				String razonsocialAux  = razonsocial != null ? razonsocial.toString() : "";
				
				if (fullNameAux.trim().equals(""))
					html.append(razonsocialAux);
				else
					html.append(fullNameAux);
				
				return html.toString();
            }
		};
	}
    
    /**
     * Formatea los datos mostrados en la celda 'Tipo A.M'
     * @return
     */
    private CellEditor getCellEditorTipoAM() {
    	return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				
				Object tipoAM = new BasicCellEditor().getValue(item, CAMPO_TIPO_AM, rowcount);
				Object idCupon = new BasicCellEditor().getValue(item, CAMPO_IDCUPON, rowcount);
				
				HtmlBuilder html = new HtmlBuilder();				
				html.append(tipoAM != null ? (Constants.ANEXO_MODIF_TIPO_ENVIO_FTP.equals(tipoAM) ? (tipoAM) : (idCupon)) : "");
				
				return html.toString();
            }
		};
    }
    
    private CellEditor getCellEditorFechaEnvioAM(final String destino) {
    	return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				
				Date fechaEnvio = (Date) new BasicCellEditor().getValue(item, CAMPO_FEC_ENVIO_ANEXO, rowcount);
								
				HtmlBuilder html = new HtmlBuilder();	
				html.append(fechaEnvio != null ? (DateUtil.date2String(fechaEnvio, DateUtil.FORMAT_DATE_DEFAULT)) : destino!=null? " ":"&nbsp;");
				
				return html.toString();
            }
		};
    }
    
    
    private CellEditor getCellEditorFechaSeguimiento(final String destino) {
    	return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				
				
				Date fechaSeguimiento = (Date) new BasicCellEditor().getValue(item, CAMPO_FECHA_SEGUIMIENTO, rowcount); 		
				
				HtmlBuilder html = new HtmlBuilder();	
				html.append(fechaSeguimiento != null ? (DateUtil.date2String(fechaSeguimiento, DateUtil.FORMAT_DATE_DEFAULT)) : destino!=null? " ":"&nbsp;");
				
				return html.toString();
            }
		};
    }
    
     
    /**
     * Metodo que formatea los datos que se muestran en las celdas de la columna 'Asegurado'
     * @return
     */
    private CellEditor getCellEditorModificacion() {
		return new CellEditor() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public Object getValue(Object item, String property, int rowcount) {
				HtmlBuilder html = new HtmlBuilder();
				String asunto = (String) new BasicCellEditor().getValue(item, CAMPO_ASUNTO, rowcount);
				Set coberturas = (Set) new BasicCellEditor().getValue(item, CAMPO_COBERTURAS, rowcount);
				Set<com.rsi.agp.dao.tables.anexo.Parcela> parcelas = (Set) new BasicCellEditor().getValue(item, CAMPO_PARCELAS, rowcount);
				Set<SubvDeclarada> subvDeclaras = (Set) new BasicCellEditor().getValue(item, CAMPO_SUBDECLA, rowcount);
				
			    StringBuffer modificaciones = new StringBuffer("");
				
				if (asunto!=null){
					List<String> lstAsunto = Arrays.asList(asunto.split(";"));
					if (lstAsunto.get(0).toString().equals("DOMICM")){ 	
						modificaciones.append("Asegurado, ");
					}
				}
			
					
				if(coberturas!= null && !coberturas.isEmpty()) {
					modificaciones.append("Coberturas, ");
				}
				if(parcelas!= null && !parcelas.isEmpty()) {
					boolean anadir = false;
					for (com.rsi.agp.dao.tables.anexo.Parcela p: parcelas){
						if (p.getTipomodificacion()!= null){
							anadir = true;
							break;
						}
					}
					if (anadir)
						modificaciones.append("Parcelas, ");
				}
				if(subvDeclaras!= null && !subvDeclaras.isEmpty()) {
					boolean anadir = false;
					for (SubvDeclarada s: subvDeclaras){
						if (s.getTipomodificacion()!= null){
							anadir = true;
							break;
						}
					}
					if (anadir)
						modificaciones.append("Subvenciones, ");
				}
				if(!modificaciones.toString().equals("")){
					modificaciones.deleteCharAt(modificaciones.lastIndexOf(", "));
				}
			if (modificaciones.length() >0)
				html.append(modificaciones.toString());
			else
				html.append("&nbsp;");
			return html.toString();
		}
	};
    }
	/**
     * metodo para eliminar la columna Id en los informes
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
     * Metodo que configura los nombres de las columnas para los informes
     * @param table
     */
    @SuppressWarnings("deprecation")
	private void configurarCabecerasColumnasExport(Table table) {
		table.setCaption("Listado de Anexos de Modificacion");
    	
		Row row = table.getRow();
    	row.getColumn(CAMPO_ENTIDAD).setTitle("Entidad");
		row.getColumn(CAMPO_OFICINA).setTitle("Oficina");
		row.getColumn(CAMPO_PLAN).setTitle("Plan");
		row.getColumn(CAMPO_LINEA).setTitle("Linea");
		row.getColumn(CAMPO_POLIZA).setTitle("Poliza");
		row.getColumn(CAMPO_MODULO).setTitle("Modulo");
		row.getColumn(CAMPO_TIPOREF).setTitle("Tipo");
		row.getColumn(CAMPO_NIF).setTitle("NIF/CIF");
		row.getColumn(CAMPO_FULLNAME).setTitle("Asegurado");
		
		Column fechaEnvio=row.getColumn(CAMPO_FEC_ENVIO_ANEXO);
		fechaEnvio.setTitle("Fecha de envio del anexo");
		fechaEnvio.getCellRenderer().setCellEditor(getCellEditorFechaEnvioAM("excel"));
		row.getColumn(CAMPO_ASUNTO).setTitle("Modificaciones");
		row.getColumn(CAMPO_ESTADO).setTitle("Estado");
		row.getColumn(CAMPO_TIPO_AM).setTitle("Tipo A.M");
		row.getColumn(CAMPO_ESTADO_CUPON_DESC).setTitle("Estado Cupon");
		row.getColumn(CAMPO_ESTADO_AGROSEGURO).setTitle("Estado Agroseguro");
		//row.getColumn(CAMPO_FECHA_SEGUIMIENTO).setTitle("Fecha Seguimiento");
		
		Column fechaSeguimiento = row.getColumn(CAMPO_FECHA_SEGUIMIENTO);
		fechaSeguimiento.setTitle("Fec. Actualizacion");
		fechaSeguimiento.getCellRenderer().setCellEditor(getCellEditorFechaSeguimiento("excel"));
			
	}
    
    
    /**
	 * Configuracion de las columnas de la tabla
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table) {
		// Acciones
    	configColumna(table, CAMPO_ID, "&nbsp;&nbsp;Acciones", false, false, "3%");
    	// Entidad
    	configColumna(table, CAMPO_ENTIDAD, "Ent.</br>", true, true, "3%");
    	// Oficina
    	configColumna(table, CAMPO_OFICINA, "Ofi.</br>", true, true, "3%");
    	// Plan
    	configColumna(table, CAMPO_PLAN, "Plan</br>", true, true, "3%");
    	// Linea
    	configColumna(table, CAMPO_LINEA, "L&iacute;nea</br>", true, true, "3%");  
    	// Poliza
    	configColumna(table, CAMPO_POLIZA, "P&oacute;liza</br>", true, true, "8%");
    	// Modulo
    	configColumna(table, CAMPO_MODULO, "M&oacute;dulo</br>", true, true, "6%");
    	// Tipo referencia
    	configColumna(table, CAMPO_TIPOREF, "Tipo Ref.Pol.</br>", true, true, "6%");
    	// NIF/CIF
    	configColumna(table, CAMPO_NIF, "NIF/CIF</br>", true, true, "6%");
    	// Asegurado
    	configColumna(table, CAMPO_FULLNAME, "Asegurado</br>", true, true, "8%");
    	// Fecha de envio de la poliza
    	configColumnaFecha(table, CAMPO_FEC_ENVIO_ANEXO, "Fec. Env.</br>", true, true, "8%", "dd/MM/yyyy");
    	// Modificaciones
    	configColumna(table, CAMPO_ASUNTO, "Modificaciones</br>", true, true, "13%");    	
    	// Estado
    	configColumna(table, CAMPO_ESTADO, "Estado A.M</br>", true, true, "8%");
    	// Tipo de A.M
    	configColumna (table,CAMPO_TIPO_AM,"Tipo A.M</br>",true,true,"8%");
    	// Estado cupon
    	configColumna(table, CAMPO_ESTADO_CUPON_DESC, "Estado Cup&oacute;n</br>", true, true, "7%");
    	
    	// Estado Agroseguro
    	configColumna(table, /*"poliza.estadoAgroseguro.descEstado"*/ CAMPO_ESTADO_AGROSEGURO, "Estado Agroseguro</br>", true, true, "7%");
    
    	// Fecha seguimiento
    	//configColumnaFecha(table, CAMPO_FECHA_SEGUIMIENTO, "Fecha Seguimiento", true, true, "0%", "dd/MM/yyyy");
    	table.getRow().getColumn(CAMPO_FECHA_SEGUIMIENTO).setHeaderStyle("display:none");
    	table.getRow().getColumn(CAMPO_FECHA_SEGUIMIENTO).setStyle("display:none");
	
	}
	
	/**
	 * Configura la columna de la tabla identificada por 'idCol' con los valores pasados como parametro y los incluye en la tabla
	 * @param table Objeto que contiene la tabla
	 * @param idCol Id de la columna
	 * @param title Titulo de la columna
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
	 * Configura la columna de la tabla identificada por 'idCol' con los valores pasados como parametro y los incluye en la tabla
	 * @param table Objeto que contiene la tabla
	 * @param idCol Id de la columna
	 * @param title Titulo de la columna
	 * @param filterable Indica si se podra buscar por esa columna
	 * @param sortable Indica si se podra ordenar por esa columna
	 * @param width Ancho de la columna
	 * @param fFecha Formato de fecha con la que se mostraran los datos de esta columna
	 */
	@SuppressWarnings("deprecation")
	private void configColumnaFecha (HtmlTable table, String idCol, String title, boolean filterable, boolean sortable, String width, String fFecha) {
		// Configura los valores generales de la columna
		configColumna(table, idCol, title, filterable, sortable, width);
		// Añade el formato de la fecha a la columna
		try {
			table.getRow().getColumn(idCol).getCellRenderer().setCellEditor(new DateCellEditor(fFecha));
		} catch (Exception e) {
			logger.error("Ocurrio un error al configurar el formato de fecha de la columna " + idCol, e);
		}
	}
	
	
	public void setDataAndLimitVariables(TableFacade tableFacade, List<BigDecimal> listaGrupoEntidades,List<BigDecimal> listaGrupoOficinas) {
		// Obtiene el Filter para la busqueda de polizas
		Limit limit = tableFacade.getLimit();
		anexoModificacionFilter = getAnexoModificacionFilter(limit, listaGrupoEntidades,listaGrupoOficinas); 

        // Obtiene el numero de filas que cumplen el filtro        
        int totalRows = 0;
		try {
			totalRows = getAnexoModificacionCountWithFilter(anexoModificacionFilter);
			log ("setDataAndLimitVariables", "Numero de Anexos de modificacion obtenidos = " + totalRows);
		} catch (BusinessException e1) {
			log ("setDataAndLimitVariables", "Error al obtener el numero anexos de modificacion", e1);		
		}  
        
        tableFacade.setTotalRows(totalRows);

        // Crea el Sort para la busqueda de Anexos de Modificacion
        anexoModificacionSort = getAnexoModificacionSort(limit);
        int rowStart = limit.getRowSelect().getRowStart();
        int rowEnd = limit.getRowSelect().getRowEnd();
        
        // Obtiene los registros que cumplen el filtro
        Collection<AnexoModificacion> items = new ArrayList<AnexoModificacion>();		
        try {
			items = getAnexoModificacionWithFilterAndSort(anexoModificacionFilter, anexoModificacionSort, rowStart, rowEnd);
			log ("setDataAndLimitVariables", "Registros en la lista de Anexos de modificacion = " + items.size());
		} 
        catch (BusinessException e) {
			log ("setDataAndLimitVariables", "Error al obtener el listado de Anexos de modificacion", e);
		}
		
		// Carga los registros obtenidos del bd en la tabla
        tableFacade.setItems(items); 
		
	}
	
	/**
	 * Crea y configura el Filter para la consulta de Red. Capital
	 * @param limit
	 * @return
	 */
	public AnexoModificacionFilter getAnexoModificacionFilter(Limit limit, List<BigDecimal> listaGrupoEntidades,List<BigDecimal> listaGrupoOficinas) {
		AnexoModificacionFilter anexoModificacionFilter = new AnexoModificacionFilter();
        FilterSet filterSet = limit.getFilterSet();
        Collection<Filter> filters = filterSet.getFilters();
        for (Filter filter : filters) {
            String property = filter.getProperty();
            String value = filter.getValue();
            
            log ("getAnexoModificacionFilter" , "Añade al filtro - property: " + property + " - value: " + value);
            
            anexoModificacionFilter.addFilter(property, value);
        }
        
        // Si la lista de grupos de entidades no esta vacia se incluye en el filtro de busqueda
        if (listaGrupoEntidades!= null && listaGrupoEntidades.size()>0) {
        	anexoModificacionFilter.addFilter(CAMPO_LISTADOGRUPOENT, listaGrupoEntidades);
        }
        // Si la lista de grupos de entidades no esta vacia se incluye en el filtro de busqueda
        if (listaGrupoOficinas!= null && listaGrupoOficinas.size()>0) {
        	anexoModificacionFilter.addFilter(CAMPO_LISTADOGRUPOOFI, listaGrupoOficinas);
        }
        return anexoModificacionFilter;
	}
	
	/**
	 * Crea y configura el Sort para la consulta de Anexo de modificacion
	 * @param limit
	 * @return
	 */
	public AnexoModificacionSort getAnexoModificacionSort(Limit limit) {
		AnexoModificacionSort anexoModificacionSort = new AnexoModificacionSort();
        SortSet sortSet = limit.getSortSet();
        Collection<Sort> sorts = sortSet.getSorts();
        for (Sort sort : sorts) {
            String property = sort.getProperty();
            String order = sort.getOrder().toParam();
            anexoModificacionSort.addSort(property, order);
            
            log ("getAnexoModificacionSort" , "Añade la ordenacion - property: " + property + " - order: " + order);
        }
        return anexoModificacionSort;
	}

	@SuppressWarnings("deprecation")
	public TableFacade crearTableFacade(HttpServletRequest request,
			HttpServletResponse response, AnexoModificacion anexoMod,
			String primeraBusqueda) {
		
		// Crea el objeto TableFacade
		TableFacade tableFacade = new TableFacade(id, request);			
		//Carga las columnas a mostrar en el listado en el TableFacade
		tableFacade.addFilterMatcher(new MatcherKey(Long.class), new LongFilterMatcher());
	    tableFacade.addFilterMatcher(new MatcherKey(BigDecimal.class), new BigDecimalFilterMatcher());
	    tableFacade.addFilterMatcher(new MatcherKey(Character.class), new CharacterFilterMatcher());
	    tableFacade.addFilterMatcher(new MatcherKey(Date.class), new DateFromFilterMatcher("d/M/yyyy"));
	    
		cargarColumnas(tableFacade);
		
		//tableFacade.setExportTypes(response, ExportType.EXCEL);
	    
	    tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.
	    
	    // Si no es una llamada a traves de ajax        
		if (request.getParameter("ajax") == null){
			if (primeraBusqueda == null){
	    		if (request.getSession().getAttribute("listadoAnexoModificacion_LIMIT") != null){
	    			//Si venimos por aqui es que ya hemos pasado por el filtro en algun momento
	    			tableFacade.setLimit((Limit) request.getSession().getAttribute("listadoAnexoModificacion_LIMIT"));
	    		}
			}
			else{    			
				// Carga en el TableFacade los filtros de busqueda introducidos en el formulario 
				cargarFiltrosBusqueda(anexoMod, tableFacade);
				
				 // -- ORDENACIÓN POR DEFECTO --> Entidad asc, Plan desc, Linea asc, Poliza asc, orden asc
				tableFacade.getLimit().getSortSet().addSort(new Sort (1, CAMPO_ENTIDAD, Order.ASC));
				tableFacade.getLimit().getSortSet().addSort(new Sort (2, CAMPO_PLAN, Order.DESC));  			    		
				tableFacade.getLimit().getSortSet().addSort(new Sort (3, CAMPO_LINEA, Order.ASC));
				tableFacade.getLimit().getSortSet().addSort(new Sort (4, CAMPO_POLIZA, Order.ASC));				
			}
		}                
	    return tableFacade;
	}
	/**
	 * Carga las columnas a mostrar en el listado en el TableFacade
	 * @param tableFacade 
	 */
	@SuppressWarnings("all")
	private void cargarColumnas(TableFacade tableFacade) {		
		// Configura el TableFacade con las columnas que se quieren mostrar
        tableFacade.setColumnProperties(CAMPO_ID,CAMPO_ENTIDAD,CAMPO_OFICINA,CAMPO_PLAN,CAMPO_LINEA, CAMPO_POLIZA,CAMPO_MODULO,CAMPO_TIPOREF,
        		CAMPO_NIF,CAMPO_FULLNAME,CAMPO_TIPO_AM,CAMPO_ASUNTO, CAMPO_ESTADO, CAMPO_ESTADO_CUPON_DESC,CAMPO_FEC_ENVIO_ANEXO, CAMPO_ESTADO_AGROSEGURO, CAMPO_FECHA_SEGUIMIENTO);
    }
	
	/**
	 * Carga en el TableFacade los filtros de busqueda introducidos en el formulario
	 * @param anexoMod
	 * @param tableFacade
	 */
	private void cargarFiltrosBusqueda(AnexoModificacion anexoMod, TableFacade tableFacade) {		
		
		// Entidad
		if (FiltroUtils.noEstaVacio (anexoMod.getPoliza().getColectivo().getTomador().getId().getCodentidad()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_ENTIDAD, anexoMod.getPoliza().getColectivo().getTomador().getId().getCodentidad().toString());
		// Oficina
		if (FiltroUtils.noEstaVacio (anexoMod.getPoliza().getOficina()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_OFICINA,anexoMod.getPoliza().getOficina());
		// Usuario
		if (FiltroUtils.noEstaVacio (anexoMod.getPoliza().getUsuario().getCodusuario()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_USUARIO,anexoMod.getPoliza().getUsuario().getCodusuario());
		// Plan
		if (FiltroUtils.noEstaVacio (anexoMod.getPoliza().getLinea().getCodplan()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_PLAN, anexoMod.getPoliza().getLinea().getCodplan().toString());
		// Linea
		if (FiltroUtils.noEstaVacio (anexoMod.getPoliza().getLinea().getCodlinea()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_LINEA, anexoMod.getPoliza().getLinea().getCodlinea().toString());
		// Poliza
		if (FiltroUtils.noEstaVacio (anexoMod.getPoliza().getReferencia())) 
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_POLIZA, anexoMod.getPoliza().getReferencia());
		// Tipo Referencia
		if (FiltroUtils.noEstaVacio (anexoMod.getPoliza().getTipoReferencia())) 
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_TIPOREF, anexoMod.getPoliza().getTipoReferencia().toString());
		// NIF/CIF
		if (FiltroUtils.noEstaVacio (anexoMod.getPoliza().getAsegurado().getNifcif()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_NIF,anexoMod.getPoliza().getAsegurado().getNifcif());
		// Asegurado
		if (FiltroUtils.noEstaVacio (anexoMod.getPoliza().getAsegurado().getFullName()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_FULLNAME, anexoMod.getPoliza().getAsegurado().getFullName());
		// Fecha de envio
		if (FiltroUtils.noEstaVacio (anexoMod.getFechaEnvioAnexo())){
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_FEC_ENVIO_ANEXO, new SimpleDateFormat("dd/MM/yyyy").format(anexoMod.getFechaEnvioAnexo()));			
		}
		// Asunto
		if (FiltroUtils.noEstaVacio (anexoMod.getAsunto()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_ASUNTO, anexoMod.getAsunto());
		// Estado
		if (FiltroUtils.noEstaVacio (anexoMod.getEstado().getIdestado()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_IDESTADO, anexoMod.getEstado().getIdestado().toString());
		
		// Tipo de envio
		if (FiltroUtils.noEstaVacio (anexoMod.getTipoEnvio())) {
			if (Constants.ANEXO_MODIF_TIPO_ENVIO_FTP.equals(anexoMod.getTipoEnvio())) {
				tableFacade.getLimit().getFilterSet().addFilter(CAMPO_TIPO_AM, Constants.ANEXO_MODIF_TIPO_ENVIO_FTP);
			}
			else {
				tableFacade.getLimit().getFilterSet().addFilter(CAMPO_TIPO_AM, Constants.ANEXO_MODIF_TIPO_ENVIO_SW);
				tableFacade.getLimit().getFilterSet().addFilter(CAMPO_IDCUPON, anexoMod.getTipoEnvio());
			}
		}		
		// Estado del cupon
		if (FiltroUtils.noEstaVacio (anexoMod.getCupon().getEstadoCupon().getId())) {
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_ESTADO_CUPON, anexoMod.getCupon().getEstadoCupon().getId().toString());
		}
		//delegacion
		if (FiltroUtils.noEstaVacio (anexoMod.getPoliza().getUsuario().getDelegacion()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_DELEGACION, anexoMod.getPoliza().getUsuario().getDelegacion().toString());
		//entidad mediadora
		if (FiltroUtils.noEstaVacio (anexoMod.getPoliza().getColectivo().getSubentidadMediadora().getId().getCodentidad()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_ENTMEDIADORA, anexoMod.getPoliza().
					getColectivo().getSubentidadMediadora().getId().getCodentidad().toString());
		//subentidad mediadora
		if (FiltroUtils.noEstaVacio (anexoMod.getPoliza().getColectivo().getSubentidadMediadora().getId().getCodsubentidad()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_SUBENTMEDIADORA, anexoMod.getPoliza().
					getColectivo().getSubentidadMediadora().getId().getCodsubentidad().toString());
	}
	
	
	/**
	 * Escribe en el log indicando la clase y el metodo.
	 * @param method
	 * @param msg
	 */
	private void log (String method, String msg) {
		logger.debug("AnexoModificacionService." + method + " - " + msg);
	}
	
	/**
	 * Escribe en el log indicando la clase, el metodo y la excepcion.
	 * @param method
	 * @param msg
	 * @param e
	 */
	private void log (String method, String msg, Throwable e) {
		logger.error("AnexoModificacionService." + method + " - " + msg, e);
	}

	public void setAnexoModificacionDao(IAnexoModificacionDao anexoModificacionDao) {
		this.anexoModificacionDao = anexoModificacionDao;
	}

	@Override
	public List<AnexoModificacion> getAllFilteredAndSorted() throws BusinessException {
		
		 // Obtener todos los registros filtrados y ordenados sin l�mites de paginaci�n
	    Collection<AnexoModificacion> allResults = anexoModificacionDao.getAnexoModificacionWithFilterAndSort(anexoModificacionFilter, anexoModificacionSort, -1, -1);
	    return (List<AnexoModificacion>) allResults;
	}
	

}
