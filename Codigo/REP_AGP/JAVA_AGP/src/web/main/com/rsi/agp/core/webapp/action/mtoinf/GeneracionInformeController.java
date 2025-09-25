package com.rsi.agp.core.webapp.action.mtoinf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.DatosInformeFilter;
import com.rsi.agp.core.jmesa.service.mtoinf.IGeneracionInformeService;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoCondicionCamposGenericosService;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoDatosInformeService;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoInformeService;
import com.rsi.agp.core.report.layout.DisenoTablaInformeGenerico;
import com.rsi.agp.core.report.layout.DynamicTableModel;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.mtoinf.FormatoCampoGenerico;
import com.rsi.agp.dao.tables.mtoinf.Informe;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRProperties;

public class GeneracionInformeController extends GenericInformeMultiActionController {
	
	private IGeneracionInformeService generacionInformeService;
	private IMtoInformeService mtoInformeService;
	private IMtoDatosInformeService mtoDatosInformeService;
	private IMtoCondicionCamposGenericosService mtoCondicionCamposGenericosService;
	private String successView;
	private Log logger = LogFactory.getLog(GeneracionInformeController.class);

	
	/**
	 * Realiza la consulta de informes que se ajustan al filtro de búsqueda
	 * @param request
	 * @param response
	 * @param informe Objeto que encapsula el filtro de búsqueda
	 * @return ModelAndView que contiene la redireccion a la página de generacion de informes
	 */
	public ModelAndView doConsulta (HttpServletRequest request, HttpServletResponse response, Informe informeBean) {
		logger.debug("init - GeneracionInformeController - doConsulta");
		
		// Comprueba que el usuario tiene permiso para acceder a este modulo
		if (!checkPermisoGenerador(request)) return devolverError();
		
		// Obtiene el usuario de la sesion
    	final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
    	// Map para guardar los parámetros que se pasarán a la jsp
    	final Map<String, Object> parameters = new HashMap<String, Object>();
    	String html = null;
    	String origenLlamada = request.getParameter("origenLlamada");
    	String sqlInforme = request.getParameter("sqlInforme");
    	
    	parameters.put("usuarioSession", usuario.getCodusuario());
    	parameters.put("filtrarUsuSession", usuario);
    	
    	Informe informeBusqueda = (Informe) informeBean;
    	// ---------------------------------------------------------------------------------
    	// -- Búsqueda de informes y generacion de la tabla de presentacion --
        // ---------------------------------------------------------------------------------
    	logger.debug("Comienza la búsqueda Informes");    	
    	
    	html = generacionInformeService.getTablaInformes(request, response, informeBusqueda, origenLlamada, usuario);
        
    	if (html == null) {
            return null; // an export
        } else {
            String ajax = request.getParameter("ajax");
            // Llamada desde ajax
            if (ajax != null && ajax.equals("true")) {
                byte[] contents;
				try {
					contents = html.getBytes("UTF-8");
					response.getOutputStream().write(contents);
				} catch (UnsupportedEncodingException e) {
					logger.error("Excepcion : GeneracionInformeController - doConsulta", e);
				} catch (IOException e) {
					logger.error("Excepcion : GeneracionInformeController - doConsulta", e);
				}
                return null;
             }else {
            	// Pasa a la jsp el codigo de la tabla a traves de este atributo
    			request.setAttribute("generacionInformes", html);
             }
            }
    	 		
    	// --- carga de la lista de formatos de informe
    	List<FormatoCampoGenerico> lstFormatosInforme = mtoInformeService.getFormatosInforme();
    	parameters.put("lstFormatosInforme", lstFormatosInforme);
    	parameters.put("codFormatoPDF", ConstantsInf.COD_FORMATO_PDF);
    	parameters.put("codOrientacionV", ConstantsInf.COD_ORIENTACION_VERTICAL);
    	
    	// --- carga de la lista de orientaciones del informe
    	List<FormatoCampoGenerico> lstOrientacionesInforme = mtoInformeService.getOrientacionesInforme();
    	parameters.put("lstOrientacionesInforme", lstOrientacionesInforme);
    	
		// ----------------------------------
    	// -- Carga del mapa de parámetros --
        // ----------------------------------
		
        Informe mtoInforme = new Informe();
		parameters.put("informe", mtoInforme);
		if (sqlInforme != null && sqlInforme.equals("")){
			parameters.put("sqlInforme", sqlInforme);
			logger.debug(sqlInforme);
		}
		String mensaje = request.getParameter("mensaje");
		String alerta = request.getParameter("alerta");
        if (alerta!=null){
        	parameters.put("alerta", alerta);
        }
        if (mensaje !=null){
			parameters.put("mensaje", mensaje);
		}

        // Lista de entidades asociadas al usuario de perfil 5 separadas por comas, necesarias para la lupa de usuario
		// Si el usuario es de cualquier otro perfil el parámetro se envía vacío
        parameters.put("grupoEntidades", StringUtils.toValoresSeparadosXComas(usuario.getListaCodEntidadesGrupo(), false, false));
        // Perfil del usuario conectado
		parameters.put("perfil", usuario.getPerfil().substring(4));
		
		// -----------------------------------------------------------------
    	// -- Se crea el objeto que contiene la redireccion y se devuelve --
        // -----------------------------------------------------------------
    	ModelAndView mv = new ModelAndView(successView);
    	
       	mv = new ModelAndView(successView, "informeBean", informeBusqueda);
       	mv.addAllObjects(parameters);
       	
       	logger.debug("end - GeneracionInformeController - doConsulta");
    	
    	return mv; 
	}
	
	/**
	 * Realiza varias verificaciones en el informe:
	 * Comprueba si el informe correspondiente al 'idInforme' tiene datos de informe asociados
	 * Comprueba si el informe correspondiente al 'idInforme' tiene todas sus tablas relacionadas
	 * Comprueba si el informe correspondiente al 'idInforme' tiene al menos una condicion
	 * @param request
	 * @param response
	 * @return 0 - sin datos en el informe; 1 - alguna tabla no está relacionada; 2 - no tiene condiciones; 3 - informe correcto
	 */
	public void verificarInforme (HttpServletRequest request, HttpServletResponse response) {
		boolean res = false;
		JSONObject objeto = new JSONObject();
		try {
			// Obtiene el id del informe de la request
			Long idInforme = new Long (StringUtils.nullToString(request.getParameter("idInforme")));
			Informe informe = mtoInformeService.getInforme(idInforme);
			res = this.doCheckDatosInforme(new BigDecimal(idInforme.toString()));
			if (res){ // tiene datosInforme
				res = this.doCheckRelacionTablas(informe);
				if (res){ // tiene todas sus tablas relacionadas
					res = this.doCheckCondicionesInforme(new BigDecimal(informe.getId().toString()));
					if (res){ 
						objeto.put("datos", "3"); // informe correcto, cumple con los 3 requisitos anteriores
					}else{
						objeto.put("datos", "2"); // no tiene condiciones
					}
				}else {
					objeto.put("datos", "1"); // alguna tabla no está relacionada
				}
			}else{
				objeto.put("datos", "0"); // sin datos en el informe
			}
			getWriterJSON(response, objeto);
		} catch (JSONException e) {
			logger.error("Excepcion : GeneracionInformeController - verificarInforme", e);
		}catch (Exception e) {
			logger.debug("GeneracionInformeController.doCheckDatosInforme - Ocurrio un error al escribir el resultado de la verificacion");
			logger.error("Ocurrio un error al escribir el resultado de la verificacion.", e);
		}
	}
	
	/** DAA 20/02/2013 Verifica si el numero de registros es el permitido para cada formato de informe.
	 * @param request
	 * @param response
	 * @return 
	 */
	public void verificarNumRegistros (HttpServletRequest request, HttpServletResponse response) {
		JSONObject objeto = new JSONObject();
		try {
			// Obtiene el id del informe el usuario y el formato de la request
			final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
			Long idInforme = new Long (StringUtils.nullToString(request.getParameter("idInforme")));
			Informe informe = mtoInformeService.getInforme(idInforme);
			int formatoInforme = Integer.parseInt(StringUtils.nullToString(request.getParameter("formato")));
			
			Map<String, Object> map = mtoInformeService.generarConsultaInforme(informe, usuario, null, null);
			
			// Obtiene el valor del max registros del properties en la generacion del informe
			int maxReg = mtoInformeService.getConstantMaxRegistros();
			int numRegistros = Integer.parseInt((map.get("numRegistros").toString()));
			
			objeto = generacionInformeService.getControlErrorMaxReg(numRegistros, maxReg, formatoInforme); 
			getWriterJSON(response, objeto);
			
		}catch (Exception e) {
			logger.debug("GeneracionInformeController.verificarNumRegistros - Ocurrio un error al verificar el numero de registros");
			logger.error("Ocurrio un error al verificar el numero de registros.", e);
		}
	}

	/**
	 * Metodo que va a ser invocado desde cada controller para estribir una lista JSON en su response
	 * @param response --> objeto response en el que se va a escribir la lista
	 * @param listaJSON --> la lista JSON que tiene que ser escrita
	 */
	protected void getWriterJSON(HttpServletResponse response, JSONObject listaJSON){		
		try {
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(listaJSON.toString());
		} catch (IOException e) {			
			logger.warn("Fallo al escribir la lista en el contexto", e);
		}
	}
	
	
	/**
	 * Comprueba si el informe correspondiente al 'idInforme' tiene condiciones
	 * @return true si tiene datosInforme
	 */
	private boolean doCheckCondicionesInforme (BigDecimal idInforme) {
		boolean tieneCondiciones = false;
		try {
			// Comprueba que el informe seleccionado tiene alguna condicion dada de alta
			int numCondiciones = mtoCondicionCamposGenericosService.getCondicionesCountWithFilter(idInforme);
			logger.debug("GeneracionInformeController.doCheckCondicionesInforme - El informe con id " + idInforme + ((numCondiciones == 0) ? " no" : " si") +
						 " tiene condiciones dadas de alta.");
			if (numCondiciones > 0){
				tieneCondiciones = true;
			}
			return tieneCondiciones;
		} 
		catch (NumberFormatException e) {
			logger.debug("GeneracionInformeController.doCheckCondicionesInforme - Ocurrio un error al obtener el id del informe");
			logger.error("Ocurrio un error obtener el id del informe.", e);
		}
		return false;
	}
	
	/**
	 * Comprueba si el informe correspondiente al 'idInforme' tiene datos de informe asociados
	 * @return true si tiene datosInforme
	 */
	private boolean doCheckDatosInforme (BigDecimal idInforme) {
		boolean tieneDatos = false;
		try {
			// Comprueba que el informe seleccionado tiene algún dato asociado que mostrar
			int numDatos = mtoDatosInformeService.getDatosInformeCountWithFilter(new DatosInformeFilter(), idInforme);
			logger.debug("GeneracionInformeController.doCheckDatosInforme - El informe con id " + idInforme + ((numDatos == 0) ? " no" : " si") +
						 " tiene datos de informe asociados.");
			if (numDatos > 0){
				tieneDatos = true;
			}
			return tieneDatos;
		} 
		catch (NumberFormatException e) {
			logger.debug("GeneracionInformeController.doCheckDatosInforme - Ocurrio un error al obtener el id del informe");
			logger.error("Ocurrio un error obtener el id del informe.", e);
		}
		return tieneDatos;
	}
	
	/**
	 * Comprueba si el informe correspondiente al 'idInforme' tiene todas sus tablas relacionadas
	 * @return true si todas sus tablas están relacionadas
	 */
	private boolean doCheckRelacionTablas (Informe informe) {
		try {
			// Comprueba que el informe seleccionado tiene todas sus tablas relacionadas
			boolean todasRelacionados = mtoInformeService.checkRelTablas(informe);
			if (todasRelacionados){
				logger.debug("GeneracionInformeController.doCheckRelacionTablas - El informe con id " + informe.getId() + ((todasRelacionados == true) ? " si" : " no") +
				 " tiene todas sus tablas relacionadas.");
				return true;
			}else{
				return false;
			}
		} 
		catch (NumberFormatException e) {
			logger.debug("GeneracionInformeController.doCheckRelacionTablas - Ocurrio un error al obtener el id del informe");
			logger.error("Ocurrio un error obtener el id del informe.", e);
		}
		return false;
	}
	
	
	public ModelAndView doGenerar(HttpServletRequest request, HttpServletResponse response, Informe informe){
		logger.debug("init - GeneracionInformeController - doGenerar");
		
		ModelAndView mv = null;
		String idInforme = StringUtils.nullToString(request.getParameter("idInforme"));
		String formatoInforme = StringUtils.nullToString(request.getParameter("formato"));
		String orientacionInforme = StringUtils.nullToString(request.getParameter("orientacion"));
		String consultaYaGenerada = StringUtils.nullToString(request.getParameter("consultaYaGenerada"));
		informe.setId(Long.parseLong(idInforme));
		
		// Obtiene el usuario de la sesion
    	final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		try {
			Map<String, Object> map = generacionInformeService.generarInforme(informe, usuario, consultaYaGenerada , null);
			map.put("formatoInforme", formatoInforme);
			map.put("orientacionInforme", orientacionInforme);
			List listadoInforme = (List)map.get("listadoInforme");
			List<String> cabeceras = (List<String>)map.get("cabeceras");
			List<String> tipo = (List<String>)map.get("tipo");
			DynamicTableModel tabla = new DynamicTableModel(listadoInforme.toArray(),cabeceras,tipo);
			
			String nombreInforme = "InformeGenerico";
			
			DisenoTablaInformeGenerico tablaInformeGen = new DisenoTablaInformeGenerico();
			
			JRProperties.setProperty("net.sf.jasperreports.awt.ignore.missing.font", "true");
			DynamicReport dr = tablaInformeGen.buildReport(tabla,map);
			JRDataSource ds = null;
			JasperPrint jp = new JasperPrint();
			if (tabla.getColumnNames() != null && tabla.getData() != null)
				ds = new JRTableModelDataSource(tabla);
			jp = DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(), ds);
			ServletOutputStream out = response.getOutputStream();
	        switch (Integer.parseInt(formatoInforme)) {
	    		case ConstantsInf.COD_FORMATO_PDF:
	    			logger.info("Exportamos el informe a PDF");
					JRPdfExporter pdfExporter = new JRPdfExporter();
					pdfExporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
					pdfExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);	
					pdfExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme + ".pdf");
					pdfExporter.exportReport();
					break;	
				case ConstantsInf.COD_FORMATO_XLS:
					logger.info("Exportamos el informe a XLS");
					JRXlsExporter xlsExporter = new JRXlsExporter();
					xlsExporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
					xlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
					xlsExporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
					xlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
					xlsExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
					xlsExporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, Boolean.TRUE);
					xlsExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
					xlsExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme + ".xls");
					response.setContentType("application/vnd.ms-excel"); // para el popup de abrir o guardar
					xlsExporter.exportReport();
					break;
				case ConstantsInf.COD_FORMATO_HTML:
					logger.info("Exportamos el informe a HTML");
					JRHtmlExporter expHTML = new JRHtmlExporter();
					expHTML.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
					expHTML.setParameter(JRHtmlExporterParameter.IMAGES_URI,"image?image=");  
					expHTML.setParameter(JRHtmlExporterParameter.JASPER_PRINT, jp);
					expHTML.setParameter(JRHtmlExporterParameter.OUTPUT_STREAM, out);	
					expHTML.setParameter(JRHtmlExporterParameter.OUTPUT_FILE_NAME, nombreInforme + ".html");
					expHTML.exportReport();
					break;
				case ConstantsInf.COD_FORMATO_CSV: 
					logger.info("Exportamos el informe a CSV");
					JRCsvExporter expCSV = new JRCsvExporter();
					expCSV.setParameter(JRExporterParameter.JASPER_PRINT, jp);
					expCSV.setParameter(JRExporterParameter.OUTPUT_STREAM, out);	
					expCSV.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme + ".csv");
					expCSV.exportReport();
					break;
				default: break;		
	        }
			logger.debug("Informe exportado correctamente");
			logger.debug("end - GeneracionInformeController - doGenerar");
			return mv;
			
			} catch (BusinessException e) {
				Map<String, Object> parametros = new HashMap<String, Object>();
				logger.error("Se ha producido un error al generar el informe: ", e);
				parametros.put("textoMensaje", "Se ha producido un error durante la generacion del Informe. "+ StringUtils.stack2string(e));
				return new ModelAndView("error","result",parametros);
			}catch (Exception e) {
				Map<String, Object> parametros = new HashMap<String, Object>();
				logger.error("Se ha producido un error al generar el informe: ", e);
				parametros.put("textoMensaje", "Se ha producido un error durante la generacion del Informe. "+ StringUtils.stack2string(e));
				return new ModelAndView("error","result",parametros);
			}
		}
	
	/**
	 * Setter del Service para Spring
	 * @param generacionInformeService
	 */
	public void setGeneracionInformeService(IGeneracionInformeService generacionInformeService) {
		this.generacionInformeService = generacionInformeService;
	}

	/**
	 * Setter del Service para Spring
	 * @param mtoInformeService
	 */
	public void setMtoInformeService(IMtoInformeService mtoInformeService) {
		this.mtoInformeService = mtoInformeService;
	}
	
	/**
	 * Setter de atributo para Spring
	 * @param successView
	 */
	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	public void setMtoDatosInformeService(
			IMtoDatosInformeService mtoDatosInformeService) {
		this.mtoDatosInformeService = mtoDatosInformeService;
	}

	public void setMtoCondicionCamposGenericosService(
			IMtoCondicionCamposGenericosService mtoCondicionCamposGenericosService) {
		this.mtoCondicionCamposGenericosService = mtoCondicionCamposGenericosService;
	}
}
