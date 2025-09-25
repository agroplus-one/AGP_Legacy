package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.mtoinf.IGeneracionInformeService;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoInformeService;
import com.rsi.agp.core.report.layout.DisenoTablaInformeGenerico;
import com.rsi.agp.core.report.layout.DynamicTableModel;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.comisiones.InformeRecibos;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.mtoinf.Informe;

public class InformesRecibosController extends BaseMultiActionController{
	
	private IGeneracionInformeService generacionInformeService;
	private IMtoInformeService mtoInformeService;
	
	private Integer MAX_CHAR_PER_ROW = 18;
	private Float MAX_CHAR_PER_COL = new Float("0.3");
	private Integer altoCabeceras = 30;
	
	private Log logger = LogFactory.getLog(InformesRecibosController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_inf");
	
	/** DAA 30/09/2013
	 *  Metodo que pinta la jsp de seleccion de condiciones y campos para el informe
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, InformeRecibos informeRecibosBean) {
		
		ModelAndView mv = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String perfil = usuario.getPerfil();
		String lstCodEntidades = "";
		String entMed = "";
		String subEntMed = "";
		if ((Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES).equals(perfil)){
			lstCodEntidades = usuario.getOficina().getId().getCodentidad().toString();
			entMed = usuario.getSubentidadMediadora().getId().getCodentidad().toString();
			subEntMed = usuario.getSubentidadMediadora().getId().getCodsubentidad().toString();
		}else{
			if((Constants.PERFIL_USUARIO_SEMIADMINISTRADOR).equals(perfil)){
				lstCodEntidades = StringUtils.toValoresSeparadosXComas(usuario.getListaCodEntidadesGrupo(),false, false);
			}
		}
		parameters.put("lstCodEntidades", lstCodEntidades);
		parameters.put("entMed", entMed);
		parameters.put("subEntMed", subEntMed);
		parameters.put("esExterno", usuario.getExterno());
		parameters.put("perfil", perfil.substring(4));
		mv = new ModelAndView("moduloComisiones/informesRecibos", "informeRecibosBean", informeRecibosBean).addAllObjects(parameters);
		return mv;
		
	}
	/** DAA 01/10/2013
	 *  Metodo para la generacion del Informe final que se imprimira
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView doGenerar(HttpServletRequest request,HttpServletResponse response, InformeRecibos informeRecibosBean) {
		
		logger.debug("init - doGenerar");
		ModelAndView mv = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String formatoInforme = informeRecibosBean.getFormato();
		Long idCampoOculto = new Long(bundle.getString("informes.recibos.idInforme"));
		Informe informe = new Informe();
		informe.setOculto(new BigDecimal (idCampoOculto));
		Float numRows = null;
		try {			
			Map<String, Object> map = generacionInformeService.generarInforme(informe, usuario, null , informeRecibosBean);
			map.put("formatoInforme", formatoInforme);
			if(Integer.parseInt(formatoInforme) == ConstantsInf.COD_FORMATO_PDF){
				map.put("orientacionInforme", StringUtils.nullToString(ConstantsInf.COD_ORIENTACION_HORIZONTAL));
			}else{
				map.put("orientacionInforme", StringUtils.nullToString(ConstantsInf.COD_ORIENTACION_VERTICAL));
			}
			List<Object> listadoInforme = (List<Object>)map.get("listadoInforme");
			List<String> cabeceras = (List<String>)map.get("cabeceras");
			List<String> cabecerasNombre = (List<String>)map.get("cabecerasNombre");
			List<String> tipo = (List<String>)map.get("tipo");
			
			//************************ Imprimimos el informe ****************************//
			
			//Formato HTML
			if(Integer.parseInt(formatoInforme) == (ConstantsInf.COD_FORMATO_HTML)){
				logger.info("Exportamos el informe a HTML");
				//guardamos el informe generado en HTML por si posteriormente se va a ordenar.
				Informe informeHTML = (Informe) map.get("informe");
				request.getSession().setAttribute("informeHTML", informeHTML);
				//guardamos el bean de filtros/condiciones por si posteriormente se va a generar detalle
				request.getSession().setAttribute("informeRecibosBean", informeRecibosBean);
				
				List totaliza = (List) map.get("totaliza");
				if(totaliza.contains(true)){
					listadoInforme = generacionInformeService.setSumatorioToInforme(totaliza, listadoInforme);
				}	
				parameters.put("listadoInforme", listadoInforme);
				parameters.put("listadoCabecera", cabeceras);
				parameters.put("listadoCabeceraNombre", cabecerasNombre);
				return mv = new ModelAndView("moduloComisiones/informesRecibosHTML", "informeRecibosBean", informeRecibosBean ).addAllObjects(parameters);
			}
			
			//Resto de Formatos
			DynamicTableModel tabla = new DynamicTableModel(listadoInforme.toArray(),cabeceras,tipo);
			
			String nombreInforme = "InformeRecibos";
			Integer numColumnas = ((List)map.get("cabeceras")).size();
			if(Integer.parseInt(informeRecibosBean.getFormato()) == ConstantsInf.COD_FORMATO_TXT){
				numRows = new Float (map.get("numRegistros").toString());
			}
			DisenoTablaInformeGenerico tablaInformeGen = new DisenoTablaInformeGenerico();
			
			JRProperties.setProperty("net.sf.jasperreports.awt.ignore.missing.font", "true");
			DynamicReport dr = tablaInformeGen.buildReport(tabla,map);
			JRDataSource ds = null;
			JasperPrint jp = new JasperPrint();
			if (tabla.getColumnNames() != null && tabla.getData() != null)
				ds = new JRTableModelDataSource(tabla);
			jp = DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(), ds);
			ServletOutputStream out = null;
	        switch (Integer.parseInt(formatoInforme)) {
	        	//Formato PDF
	    		case ConstantsInf.COD_FORMATO_PDF:
	    			logger.info("Exportamos el informe a PDF");
					JRPdfExporter pdfExporter = new JRPdfExporter();
					out = response.getOutputStream();
					pdfExporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
					pdfExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);	
					pdfExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme + ".pdf");
					pdfExporter.exportReport();
					break;	
				//Formato EXCEL	
				case ConstantsInf.COD_FORMATO_XLS:
					logger.info("Exportamos el informe a XLS");
					JRXlsExporter xlsExporter = new JRXlsExporter();
					out = response.getOutputStream();
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
				//Formato TXT	
				case ConstantsInf.COD_FORMATO_TXT:
					logger.info("Exportamos el informe a TXT");
					JRTextExporter txtExporter = new JRTextExporter();
					out = response.getOutputStream();
					txtExporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
					//txtExporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH    , new Float());
				    //txtExporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT   , new Float());
					int height = (int) (numRows* MAX_CHAR_PER_COL)+ altoCabeceras;
				    txtExporter.setParameter(JRTextExporterParameter.PAGE_WIDTH         , new Integer(MAX_CHAR_PER_ROW * numColumnas));
					txtExporter.setParameter(JRTextExporterParameter.PAGE_HEIGHT        , new Integer(height));
					txtExporter.setParameter(JRTextExporterParameter.IGNORE_PAGE_MARGINS, true);
				    txtExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
				    txtExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme + ".txt");
					response.setContentType("text/plain");
					txtExporter.exportReport();
					break;
					
				default: break;		
	        }
			logger.debug("Informe exportado correctamente");
			logger.debug("end - InformesReciboController - doGenerar");
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
	/** DAA 01/10/2013
	 *  Metodo para la ordenacion del Informe ya impreso
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView doOrdenar(HttpServletRequest request,HttpServletResponse response, InformeRecibos informeRecibosBean) {
		logger.debug("init - doOrdenar");
		ModelAndView mv = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		Informe informeHTML = (Informe) request.getSession().getAttribute("informeHTML");
		try {
			Map<String, Object> map = mtoInformeService.generarConsultaInforme(informeHTML, usuario, null, informeRecibosBean);
			
			String sql = (String) map.get("sql");
			List listadoInforme = mtoInformeService.getConsulta(sql);
			List<String> cabeceras = (List<String>)map.get("cabeceras");
			
			//guardamos el informe generado en HTML por si posteriormente se va a ordenar.
			Informe informeHTMLOrdenado = (Informe) map.get("informe");
			request.getSession().setAttribute("informeHTML", informeHTMLOrdenado);
				
			List totaliza = (List) map.get("totaliza");
			if(totaliza.contains(true)){
				listadoInforme = generacionInformeService.setSumatorioToInforme(totaliza, listadoInforme);
			}	
			parameters.put("listadoInforme", listadoInforme);
			parameters.put("listadoCabecera", cabeceras);
			parameters.put("campoOrdenar", informeRecibosBean.getCampoOrdenar());
			parameters.put("sentido", informeRecibosBean.getSentido());
		
		} catch (BusinessException e) {
			Map<String, Object> parametros = new HashMap<String, Object>();
			logger.error("Se ha producido un error al generar el informe: ", e);
			parametros.put("textoMensaje", "Se ha producido un error durante la generacion del Informe. "+ StringUtils.stack2string(e));
			return new ModelAndView("error","result",parametros);
		}

		logger.debug("end - InformesReciboController - doOrdenar");
		return mv = new ModelAndView("moduloComisiones/informesRecibosHTML", "informeRecibosBean", informeRecibosBean).addAllObjects(parameters);
	}
	
	/** DAA 23/10/2013
	 *  Metodo para mostrar el detalle del Informe ya impreso
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public void doDetalle(HttpServletRequest request,HttpServletResponse response) {
		logger.debug("init - doDetalle");
		JSONArray registros = new JSONArray();
		JSONArray cabecera = new JSONArray();
    	JSONObject object = new JSONObject();
    	
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String stringRegistro = request.getParameter("stringRegistro");
		Informe informeHTML = (Informe) request.getSession().getAttribute("informeHTML");
		InformeRecibos informeRecibosBean = (InformeRecibos) request.getSession().getAttribute("informeRecibosBean");
		
		try {
			informeRecibosBean = mtoInformeService.setInformeRecibosBeanDetalle(informeRecibosBean, stringRegistro);
			Map<String, Object> map = generacionInformeService.generarInforme(informeHTML, usuario, null , informeRecibosBean);
			List listadoInforme = (List)map.get("listadoInforme");
			List<String> cabeceras = (List<String>)map.get("cabeceras");
			
			List totaliza = (List) map.get("totaliza");
			if(totaliza.contains(true)){
				listadoInforme = generacionInformeService.setSumatorioToInforme(totaliza, listadoInforme);
			}	
			
			for(int i=0; i<listadoInforme.size(); i++){
    			registros.put(listadoInforme.get(i)); 
    		}
			for(int i=0; i<cabeceras.size(); i++){
    			cabecera.put(cabeceras.get(i)); 
    		}			
			object.put("cabecera", cabecera);
			object.put("registros", registros);
			 
			getWriterJSON(response, object);
			logger.debug("end - InformesReciboController - doDetalle");
			return;
		
		
		} catch (BusinessException e) {
			Map<String, Object> parametros = new HashMap<String, Object>();
			logger.error("Se ha producido un error al generar el informe: ", e);
			parametros.put("textoMensaje", "Se ha producido un error durante la generacion del Informe. "+ StringUtils.stack2string(e));
			return;
		} catch(Exception excepcion){
	    		logger.error("Error al comprobar el plan/línea de la póliza asociada",excepcion);
	    }
	}
	
	
	public void setGeneracionInformeService(IGeneracionInformeService generacionInformeService) {
		this.generacionInformeService = generacionInformeService;
	}
	public void setMtoInformeService(IMtoInformeService mtoInformeService) {
		this.mtoInformeService = mtoInformeService;
	}
}
