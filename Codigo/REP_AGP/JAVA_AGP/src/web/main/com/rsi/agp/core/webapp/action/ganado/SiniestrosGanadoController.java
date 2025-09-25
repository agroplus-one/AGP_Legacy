package com.rsi.agp.core.webapp.action.ganado;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.SiniestrosGanadoManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ExcelUtils;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizaPctComisiones;
import com.rsi.agp.dao.tables.siniestro.Siniestro;
import com.rsi.agp.dao.tables.siniestro.SiniestroGanado;
import com.rsi.agp.dao.tables.siniestro.SiniestroGanadoActas;
import com.rsi.agp.dao.tables.siniestro.SiniestroGanadoRyD;
import com.rsi.agp.dao.tables.siniestro.SiniestroGanadoVida;

public class SiniestrosGanadoController extends BaseMultiActionController {

	private Log logger = LogFactory.getLog(SiniestrosGanadoController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");

	private SiniestrosGanadoManager siniestrosGanadoManager;

	private static final String ATTACHMENT_FILENAME_LISTADO_SINIESTROS_GANADO_XLS = "attachment; filename=listadoSiniestrosGanado.xls";
	private static final String ATTACHMENT_FILENAME_LISTADO_ACTAS_SINIESTROS_GANADO_XLS = "attachment; filename=listadoActasSiniestrosGanado.xls";

	private static final String CONTENT_DISPOSITION = "Content-Disposition";
	private static final String APPLICATION_VND_MS_EXCEL = "application/vnd.ms-excel";
	private static final String LISTADO_SINIESTROS_GANADO = "LISTADO SINIESTROS GANADO";
	private static final String LISTADO_ACTAS_SINIESTROS_GANADO = "LISTADO ACTAS SINIESTROS GANADO";

	private static final String DD_MM_YYYY = "dd/MM/yyyy";
	
	private static final String NO = "No";
	private static final String SI = "Sí";
	
	private static final String G_N = "G.N.";
	private static final String PROVINCIA = "Provincia";
	private static final String TERMINO = "Término";
	private static final String SERIE = "Serie";
	private static final String NUMERO = "Número";
	private static final String FECHA_COMUNICACION = "F. Comunicación";
	private static final String LIBRO = "Libro";
	private static final String IDANIMAL = "IdAnimal";
	private static final String TLF_PERITO = "Tlf. Perito";
	private static final String TASADO = "Tasado";
	private static final String FECHA_RETIRADA = "F. Retirada";
	private static final String KG = "Kg";
	private static final String COSTE_RETIRADA = "Coste Retirada";
	private static final String PAGO_GESTORA = "Pago Gestora";	
	private static final String LETRA = "Letra"; 		
	private static final String ESTADO = "Estado"; 		
	private static final String FECHA_ACTA = "F.Acta"; 		
	private static final String IMPORTE_ACTA = "Importe Acta"; 	
	private static final String IMPORTE_DEVOLVER = "Importe Devolver"; 
	private static final String FECHA_PAGO = "F.Pago";
	
    private static final HashMap<Integer, String> titulosColumnasActas = new HashMap<>();
    
    static {
    	titulosColumnasActas.put(0, PROVINCIA);
    	titulosColumnasActas.put(1, TERMINO);
    	titulosColumnasActas.put(2, SERIE);
    	titulosColumnasActas.put(3, NUMERO);
    	titulosColumnasActas.put(4, LETRA);
    	titulosColumnasActas.put(5, ESTADO);
    	titulosColumnasActas.put(6, LIBRO);
    	titulosColumnasActas.put(7, IDANIMAL);
    	titulosColumnasActas.put(8, FECHA_ACTA);
    	titulosColumnasActas.put(9, IMPORTE_ACTA);
    	titulosColumnasActas.put(10, IMPORTE_DEVOLVER);
    	titulosColumnasActas.put(11, FECHA_PAGO);

    }
    
	private static final HashMap<Integer, String> titulosColumnas = new HashMap<>();

    static {
    	titulosColumnas.put(0, G_N);
    	titulosColumnas.put(1, PROVINCIA);
    	titulosColumnas.put(2, TERMINO);
    	titulosColumnas.put(3, SERIE);
    	titulosColumnas.put(4, NUMERO);
    	titulosColumnas.put(5, FECHA_COMUNICACION);
    	titulosColumnas.put(6, LIBRO);
    	titulosColumnas.put(7, IDANIMAL);
    	titulosColumnas.put(8, TLF_PERITO);
    	titulosColumnas.put(9, TASADO);
    	titulosColumnas.put(10, FECHA_RETIRADA);
    	titulosColumnas.put(11, KG);
    	titulosColumnas.put(12, COSTE_RETIRADA);
    	titulosColumnas.put(13, PAGO_GESTORA);
    }
	
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(Date.class, null, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}

	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, Siniestro siniestro)
			throws Exception {

		boolean mostrarActa = false;

		final Map<String, String> filtro_siniestrosGan = new HashMap<String, String>();

		List<SiniestroGanado> listaFiltroSiniestro = new ArrayList<SiniestroGanado>();
		List<SiniestroGanado> listaSiniestroGanado = new ArrayList<SiniestroGanado>();

		List<SiniestroGanadoRyD> lstSiniestrosGanRyD = new ArrayList<SiniestroGanadoRyD>();
		List<SiniestroGanadoVida> lstSiniestrosGanVida = new ArrayList<SiniestroGanadoVida>();

		logger.debug("**@@** SiniestrosGanadoController - doConsulta [INIT]");

		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");

		String realPath = this.getServletContext().getRealPath("/WEB-INF/");

		/* Obtenemos los campos del filtro y los guardamos en un Hashmap */
		String grupoNeg_consulta = request.getParameter("grupoNeg_consFiltro");
		if (!StringUtils.isNullOrEmpty(grupoNeg_consulta)) {
			filtro_siniestrosGan.put("GrupoNegSin", grupoNeg_consulta);
		}

		String serieSin_consulta = request.getParameter("serie_consFiltro");
		if (!StringUtils.isNullOrEmpty(serieSin_consulta)) {
			filtro_siniestrosGan.put("SerieSin", serieSin_consulta);
		}

		String numeroSin_consulta = request.getParameter("numero_consFiltro");
		if (!StringUtils.isNullOrEmpty(numeroSin_consulta)) {
			filtro_siniestrosGan.put("NumSin", numeroSin_consulta);
		}

		String libroSin_consulta = request.getParameter("libro_consFiltro");
		if (!StringUtils.isNullOrEmpty(libroSin_consulta)) {
			filtro_siniestrosGan.put("LibroSin", libroSin_consulta);
		}

		String fechaCom_consulta = request.getParameter("fechaCom_consFiltro");
		if (!StringUtils.isNullOrEmpty(fechaCom_consulta)) {
			filtro_siniestrosGan.put("FechaSin", fechaCom_consulta);
		}

		String fechaRet_consulta = request.getParameter("fechaRet_consFiltro");
		if (!StringUtils.isNullOrEmpty(fechaRet_consulta)) {
			filtro_siniestrosGan.put("FechaRet", fechaRet_consulta);
		}

		String idPoliza = request.getParameter("idPolizaSinGan");
		if (idPoliza == null) {
			idPoliza = (request.getParameter("idPol"));
		}

		if (idPoliza == null) {
			idPoliza = request.getParameter("idPoliza_consFil");
		}

		Poliza poliza = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		String mensajeAlerta = request.getParameter("mensaje");
		if (!StringUtils.nullToString(mensajeAlerta).equals("")) {
			parametros.put("mensaje", mensajeAlerta);
		}
		String alerta = request.getParameter("alerta");
		if (!StringUtils.nullToString(alerta).equals("")) {
			parametros.put("alerta", alerta);
		}

		if (idPoliza != null)
			siniestro.getPoliza().setIdpoliza(Long.parseLong(idPoliza));
		else
			idPoliza = siniestro.getPoliza().getIdpoliza().toString();

		try {
			poliza = siniestrosGanadoManager.getPoliza(new Long(idPoliza));

			siniestro.setPoliza(poliza);

			List<GruposNegocio> gruposNegocio = new ArrayList<GruposNegocio>();
			gruposNegocio = siniestrosGanadoManager.getGruposNegocio();
			parametros.put("gruposNegocio", gruposNegocio);

			List<PolizaPctComisiones> comisiones = poliza.getLstPolizaPctComisiones();
			boolean isResto = false;
			boolean isRyD = false;

			/* Obentemos el Grupo de Negocio de la póliza */
			for (PolizaPctComisiones comision : comisiones) {
				logger.debug("Valor del grupo de Negocio: " + comision.getGrupoNegocio());

				isResto = Constants.GRUPO_NEGOCIO_VIDA.equals(comision.getGrupoNegocio());
				isRyD = Constants.GRUPO_NEGOCIO_RYD.equals(comision.getGrupoNegocio());

				if (isResto) {
					logger.debug("Buscamos Siniestos de Vida");
					lstSiniestrosGanVida = siniestrosGanadoManager.getListSiniestrosGanadoVida(poliza, realPath,
							usuario);
					mostrarActa = true;
				}

				if (isRyD) {
					logger.debug("Buscamos Siniestos de RyD");
					lstSiniestrosGanRyD = siniestrosGanadoManager.getListSiniestrosGanadoRetirada(poliza, realPath,
							usuario);
				}

			}

			listaSiniestroGanado = siniestrosGanadoManager.cargarDatosSiniestrosGanado(lstSiniestrosGanVida,
					lstSiniestrosGanRyD);

			if (listaSiniestroGanado.size() <= 0) {
				parametros.put("alerta", "No se han encontrado Siniestros");
			}

			if (filtro_siniestrosGan.size() > 0) {
				listaFiltroSiniestro = siniestrosGanadoManager.aplicarFiltroenLista(listaSiniestroGanado,
						filtro_siniestrosGan);

				parametros.put("listaSiniestroGanado", listaFiltroSiniestro);
			} else {
				parametros.put("listaSiniestroGanado", listaSiniestroGanado);
			}

		} catch (BusinessException be) {
			logger.error("Se ha producido un error durante la consulta de siniestros", be);
			parametros.put("alerta", bundle.getString("mensaje.error.general"));

		} catch (Exception be) {
			logger.error("Se ha producido un error indefinido durante la consulta de siniestros", be);
			parametros.put("alerta", bundle.getString("mensaje.error.general"));

			String descripcionError = be.toString();

			if (descripcionError.contains("404")) {
				parametros.put("alerta", "No se han encontrado Siniestros");
			} else if (descripcionError.contains("400")) {
				parametros.put("alerta", "Errores de Validación al obtener los Siniestros");
			} else {
				parametros.put("alerta", bundle.getString("mensaje.error.general"));
			}

		}

		parametros.put("mostrarActa", mostrarActa);

		/* Volvemos a recargar los datos del filtro */
		parametros.put("grupoNegocio.grupoNegocio", grupoNeg_consulta);
		parametros.put("serieSin", serieSin_consulta);
		parametros.put("numeroSin", numeroSin_consulta);
		parametros.put("libroSin", libroSin_consulta);
		parametros.put("fecha_comunicacion", fechaCom_consulta);
		parametros.put("fecha_retirada", fechaRet_consulta);
		parametros.put("grupoNegocioSel", grupoNeg_consulta);

		return new ModelAndView("/moduloUtilidadesGanado/siniestrosGanado/declaracionesSiniestroGanado",
				"siniestroBean", siniestro).addAllObjects(parametros);

	}

	public ModelAndView doActas(HttpServletRequest request, HttpServletResponse response, Siniestro siniestro)
			throws Exception {

		logger.debug("**@@** SiniestrosGanadoController - doConsulta [INIT]");

		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");

		String realPath = this.getServletContext().getRealPath("/WEB-INF/");

		/* Recuperamos los datos del filtro para devolverlos */
		String serieActa = request.getParameter("serieActa_consFiltro");
		String numActa = request.getParameter("numeroActa_consFiltro");
		String libroActa = request.getParameter("libroActa_consFiltro");
		String fechaActa = request.getParameter("fechaActa_consFiltro");
		String fechaPagoActa = request.getParameter("fechaPagActa_consFiltro");

		String idPoliza = request.getParameter("idPoliza");
		if (idPoliza == null) {
			idPoliza = (request.getParameter("idPol"));
		}

		if (idPoliza == null) {
			idPoliza = request.getParameter("idPoliza_consFilActa");
		}

		Poliza poliza = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		String mensajeAlerta = request.getParameter("mensaje");
		if (!StringUtils.nullToString(mensajeAlerta).equals("")) {
			parametros.put("mensaje", mensajeAlerta);
		}
		String alerta = request.getParameter("alerta");
		if (!StringUtils.nullToString(alerta).equals("")) {
			parametros.put("alerta", alerta);
		}

		if (idPoliza != null)
			siniestro.getPoliza().setIdpoliza(Long.parseLong(idPoliza));
		else
			idPoliza = siniestro.getPoliza().getIdpoliza().toString();

		try {
			poliza = siniestrosGanadoManager.getPoliza(new Long(idPoliza));

			siniestro.setPoliza(poliza);

			List<SiniestroGanadoActas> listaSiniestrosGanadoActa = obtenerlistaActasGanado(request, realPath, poliza,
					usuario);
			parametros.put("listaSinGanadoActas", listaSiniestrosGanadoActa);

		} catch (BusinessException be) {
			logger.error("Se ha producido un error durante la consulta de Actas de siniestros", be);
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
		} catch (Exception be) {
			logger.error("Se ha producido un error indefinido durante la consulta de Actas de siniestros", be);

			String descripcionError = be.toString();
			if (descripcionError.contains("404")) {
				parametros.put("alerta", "No se han encontrado Actas del Siniestros");

			} else if (descripcionError.contains("400")) {
				parametros.put("alerta", "Errores de Validación al obtener las Actas del Siniestros");
			} else {
				parametros.put("alerta", bundle.getString("mensaje.error.general"));
			}
		}

		/* Volvemos a recargar los datos del filtro */
		parametros.put("serieSiniestroActa", serieActa);
		parametros.put("numeroSiniestroActa", numActa);
		parametros.put("libroSiniestroActa", libroActa);
		parametros.put("fecha_acta", fechaActa);
		parametros.put("fecha_pago", fechaPagoActa);

		return new ModelAndView("/moduloUtilidadesGanado/siniestrosGanado/actaSiniestroGanado", "siniestroBean",
				siniestro).addAllObjects(parametros);

	}

	public List<SiniestroGanadoActas> obtenerlistaActasGanado(HttpServletRequest request, String realPath,
			Poliza poliza, Usuario usuario) throws Exception {

		final Map<String, String> filtro_siniestrosGanActa = new HashMap<String, String>();

		/* Obtenemos los campos del filtro y los guardamos en un Hashmap */
		String serieActa = request.getParameter("serieActa_consFiltro");
		if (!StringUtils.isNullOrEmpty(serieActa)) {
			filtro_siniestrosGanActa.put("serieActa", serieActa);
		}

		String numActa = request.getParameter("numeroActa_consFiltro");
		if (!StringUtils.isNullOrEmpty(numActa)) {
			filtro_siniestrosGanActa.put("numActa", numActa);
		}

		String libroActa = request.getParameter("libroActa_consFiltro");
		if (!StringUtils.isNullOrEmpty(libroActa)) {
			filtro_siniestrosGanActa.put("libroActa", libroActa);
		}

		String fechaActa = request.getParameter("fechaActa_consFiltro");
		if (!StringUtils.isNullOrEmpty(fechaActa)) {
			filtro_siniestrosGanActa.put("fechaActa", fechaActa);
		}

		String fechaPagoActa = request.getParameter("fechaPagActa_consFiltro");
		if (!StringUtils.isNullOrEmpty(fechaPagoActa)) {
			filtro_siniestrosGanActa.put("fechaPagoActa", fechaPagoActa);
		}

		try {
			logger.debug("Buscamos Las Actas del Siniestro");
			List<SiniestroGanadoActas> listaSiniestrosActas = siniestrosGanadoManager.getActasGanado(poliza, realPath,
					usuario);

			/* Si tenemos algún dato de filtro, aplicamos el filtro en la lista */
			if (filtro_siniestrosGanActa.size() > 0) {
				List<SiniestroGanadoActas> listaFiltroSiniestroActa = siniestrosGanadoManager
						.aplicarFiltroenListaActa(listaSiniestrosActas, filtro_siniestrosGanActa);

				return listaFiltroSiniestroActa;

			} else {

				return listaSiniestrosActas;
			}

		} catch (BusinessException be) {
			logger.error("Se ha producido un error durante la consulta de Actas de siniestros", be);
			throw be;
		} catch (Exception be) {
			logger.error("Se ha producido un error indefinido durante la consulta de Actas de siniestros", be);
			throw be;
		}

	}

	public ModelAndView doPdfActaGanado(HttpServletRequest request, HttpServletResponse response, Siniestro siniestro) {
		logger.debug("**@@** SiniestrosGanadoController - doPdfActaGanado [INIT]");

		Map<String, Object> parametros = new HashMap<String, Object>();

		/* Obtenemos los parametros para lanzar consulta del pdf */
		Integer serieSinGanado = Integer.parseInt(request.getParameter("serieSinGanadoPdf"));
		Integer numSinGanado = Integer.parseInt(request.getParameter("numSinGanadoPdf"));
		String letraSinGanado = request.getParameter("letraSinGanadoPdf");

		if (letraSinGanado.equals("")) {
			letraSinGanado = "";
		}

		try {

			logger.debug("Buscamos el pdf de Actas del Siniestro");
			byte[] pdf = siniestrosGanadoManager.getPDFActaGanado(serieSinGanado, numSinGanado, letraSinGanado);

			if (pdf != null) {

				response.setContentType("application/pdf");
				response.setContentLength(pdf.length);
				response.setHeader("Content-Disposition",
						"filename=ActaTasacionGanado" + serieSinGanado + "_" + numSinGanado + ".pdf");
				response.setHeader("Cache-Control", "cache, must-revalidate");
				response.setHeader("Pragma", "public");

				try (ServletOutputStream out = response.getOutputStream();
						BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out)) {
					bufferedOutputStream.write(pdf);
				}

				return null;

			}

		} catch (BusinessException be) {
			logger.error("Se ha producido un error indefinido durante la impresion del Pdf de Acta de Tasación de Ganado", be);

			/* Incidencia RGA (12.11.2021) ** Inicio */
			parametros.put("alerta", "Se ha producido un error indefinido durante la impresion del Pdf de Acta de Tasación de Ganado");
			return new ModelAndView("errorMensaje", "result", parametros);
			/* Incidencia RGA (12.11.2021) ** Fin */
		} catch (Exception be) {
			logger.error("Se ha producido un error indefinido durante la consulta del pdf de Acta", be);

			parametros.put("alerta", be.getMessage());
			
			/* Incidencia RGA (12.11.2021) ** Inicio */
			logger.error("Se ha producido un error durante la impresion del Pdf de Acta de Tasación de Ganado", be);
			return new ModelAndView("errorMensaje", "result", parametros);
			/* Incidencia RGA (12.11.2021) ** Fin */
		}

		return null;
	}

	public ModelAndView doPdfCartaPagoGanado(HttpServletRequest request, HttpServletResponse response,
			Siniestro siniestro) {

		logger.debug("**@@** SiniestrosGanadoController - doPdfCartaPagoGanado [INIT]");

		Map<String, Object> parametros = new HashMap<String, Object>();

		/* Obtenemos los parametros para lanzar consulta del pdf */
		Integer serieSinGanado = Integer.parseInt(request.getParameter("serieSinPdfCartaGan"));
		Integer numSinGanado = Integer.parseInt(request.getParameter("numSinPdfCartaGan"));
		String letraSinGanado = request.getParameter("letraSinPdfCartaGan");

		if (letraSinGanado.equals("")) {
			letraSinGanado = " ";
		}

		try {

			byte[] pdf = siniestrosGanadoManager.getPDFCartaPagoGanado(serieSinGanado, numSinGanado, letraSinGanado);

			if (pdf != null) {

				response.setContentType("application/pdf");
				response.setContentLength(pdf.length);
				response.setHeader("Content-Disposition",
						"filename=CartaPagoGanado" + serieSinGanado + "_" + numSinGanado + ".pdf");
				response.setHeader("Cache-Control", "cache, must-revalidate");
				response.setHeader("Pragma", "public");

				try (ServletOutputStream out = response.getOutputStream();
						BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out)) {
					bufferedOutputStream.write(pdf);
				}

				return null;

			}

			logger.debug("Terminada la generacion del Pdf de Carta de Pago de Ganado ");

		} catch (BusinessException be) {
			logger.error("Se ha producido un error indefinido durante la impresión del pdf de la Carta de Pago", be);
			
			/* Incidencia RGA (12.11.2021) ** Inicio */
			parametros.put("alerta", be.getMessage());
			return new ModelAndView("errorMensaje", "result", parametros);
			/* Incidencia RGA (12.11.2021) ** Fin */
		} catch (Exception be) {
			logger.error("Se ha producido un error indefinido durante la consulta del pdf de la Carta de Pago", be);

			parametros.put("alerta", be.getMessage());
			
			/* Incidencia RGA (12.11.2021) ** Inicio */
			logger.error("Se ha producido un error durante la impresion del Pdf de Acta de Tasación de Ganado", be);
			return new ModelAndView("errorMensaje", "result", parametros);
			/* Incidencia RGA (12.11.2021) ** Fin */
		}

		return null;
	}
	
	/**
	 * 
	 * @return
	 * @throws FileNotFoundException 
	 */
	public ModelAndView doExportarExcel(HttpServletRequest request, HttpServletResponse response, Siniestro siniestroBean) throws FileNotFoundException {
		
		logger.debug("SiniestrosGanadoController - doExportarExcel - init");
				
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		Map<String, Object> parametros = new HashMap<String, Object>();
				
		try {
			
			List<SiniestroGanado> listaFiltroSiniestro = new ArrayList<SiniestroGanado>();
			List<SiniestroGanado> listaSiniestroGanado = new ArrayList<SiniestroGanado>();

			List<SiniestroGanadoRyD> lstSiniestrosGanRyD = new ArrayList<SiniestroGanadoRyD>();
			List<SiniestroGanadoVida> lstSiniestrosGanVida = new ArrayList<SiniestroGanadoVida>();
			
			String idPoliza = request.getParameter("idPolizaSinGan");
			if (idPoliza == null) {
				idPoliza = (request.getParameter("idPol"));
			}
			
			if (idPoliza == null) {
				idPoliza = request.getParameter("idPoliza_consFil");
			}
			
			if (idPoliza != null)
				siniestroBean.getPoliza().setIdpoliza(Long.parseLong(idPoliza));
			else
				idPoliza = siniestroBean.getPoliza().getIdpoliza().toString();
			
			Poliza poliza = siniestrosGanadoManager.getPoliza(new Long(idPoliza));
						
			List<PolizaPctComisiones> comisiones = poliza.getLstPolizaPctComisiones();
			boolean isResto = false;
			boolean isRyD = false;

			/* Obentemos el Grupo de Negocio de la póliza */
			for (PolizaPctComisiones comision : comisiones) {

				isResto = Constants.GRUPO_NEGOCIO_VIDA.equals(comision.getGrupoNegocio());
				isRyD = Constants.GRUPO_NEGOCIO_RYD.equals(comision.getGrupoNegocio());
				
				if (isResto) {
					lstSiniestrosGanVida = siniestrosGanadoManager.getListSiniestrosGanadoVida(poliza, realPath,
							usuario);
				}

				if (isRyD) {
					lstSiniestrosGanRyD = siniestrosGanadoManager.getListSiniestrosGanadoRetirada(poliza, realPath,
							usuario);
				}
			}
			
			logger.debug("isResto: " + isResto);
			logger.debug("isRyD:" + isRyD);
			
			listaSiniestroGanado = siniestrosGanadoManager.cargarDatosSiniestrosGanado(lstSiniestrosGanVida,
					lstSiniestrosGanRyD);
			
			logger.debug("listaSiniestroGanado.size = " + listaSiniestroGanado.size());

			Map<String, String> filtro_siniestrosGan = obtenerFiltrosConsulta(request);
			
			logger.debug("filtro_siniestrosGan.size = " + filtro_siniestrosGan.size());
			
			if (filtro_siniestrosGan.size() > 0) {
				listaFiltroSiniestro = siniestrosGanadoManager.aplicarFiltroenLista(listaSiniestroGanado,filtro_siniestrosGan);
				generarFicheroExcel(response, listaFiltroSiniestro);

			} else {
				generarFicheroExcel(response, listaSiniestroGanado);
			}

		} catch (BusinessException e) {
			logger.error("Se ha producido un error indefinido durante la exportación a Excel", e);
			parametros.put("alerta", e.getMessage());
			return new ModelAndView("errorMensaje", "result", parametros);
		} catch (Exception e) {
			logger.error("Se ha producido un error indefinido durante la exportación a Excel", e);
			parametros.put("alerta", e.getMessage());
			return new ModelAndView("errorMensaje", "result", parametros);
		} 
				
		logger.debug("SiniestrosGanadoController - doExportarExcel - end");

		return null;
		
	}
		
	/**
	 * 
	 * @param listaSiniestros
	 */
	private void generarFicheroExcel(HttpServletResponse response, List<SiniestroGanado> listaSiniestros) {
		
		logger.debug("SiniestrosGanadoController - generarFicheroExcel - init");
		
		response.setContentType(APPLICATION_VND_MS_EXCEL);
        response.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME_LISTADO_SINIESTROS_GANADO_XLS);
        
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(LISTADO_SINIESTROS_GANADO); 
		sheet = wb.getSheetAt(0);								
		
		insertarCabeceras(wb, false);
		insertarFilas(wb, listaSiniestros);
		ExcelUtils.autoajustarColumnas(sheet, 10);	
		
		try {
			wb.write(response.getOutputStream());
			response.getOutputStream().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		logger.debug("SiniestrosGanadoController - generarFicheroExcel - end");
		
	}

	/**
	 * 
	 * @param wb
	 * @param listaSiniestros
	 */
	private void insertarFilas(HSSFWorkbook wb, List<SiniestroGanado> listaSiniestros) {
		
		logger.debug("SiniestrosGanadoController - insertarFilas - init");

		HSSFCellStyle estiloFila = ExcelUtils.getEstiloFila(wb);
		HSSFCellStyle estiloFilaNumeroEntero = ExcelUtils.getEstiloFilaNumeroEntero(wb);
		HSSFCellStyle estiloFilaNumero = ExcelUtils.getEstiloFilaNumero(wb);
    	
		HSSFCell cell;
		
		for (int i=0; i<listaSiniestros.size(); i++){
			
			HSSFRow row = wb.getSheetAt(wb.getActiveSheetIndex()).createRow(i+1); 

			cell = row.createCell(0);
			cell.setCellStyle(estiloFila); 
			if ( null!=listaSiniestros.get(i).getGrupoNegocio()&& null!=listaSiniestros.get(i).getGrupoNegocio()) { 
				cell.setCellValue(listaSiniestros.get(i).getGrupoNegocio());					
			}
			
			cell = row.createCell(1);
			cell.setCellStyle(estiloFila); 
			if ((null!=listaSiniestros.get(i).getProvincia())) {			
				cell.setCellValue(listaSiniestros.get(i).getProvincia());					
			}
			
			cell = row.createCell(2);
			cell.setCellStyle(estiloFila); 
			if ((null!=listaSiniestros.get(i).getTermino())) {			
				cell.setCellValue(listaSiniestros.get(i).getTermino());					
			}
			
			cell = row.createCell(3);
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(estiloFilaNumeroEntero);
			if ((null!=listaSiniestros.get(i).getSerieSiniestro())) {			
				cell.setCellValue(listaSiniestros.get(i).getSerieSiniestro());					
			}
			
			cell = row.createCell(4);
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(estiloFilaNumeroEntero);
			if ((null!=listaSiniestros.get(i).getNumeroSiniestro())) {			
				cell.setCellValue(listaSiniestros.get(i).getNumeroSiniestro());					
			}
			
			cell = row.createCell(5);
			cell.setCellStyle(estiloFila); 
			if ( null!=listaSiniestros.get(i).getFechaComunicacion()) { 
				SimpleDateFormat formatoFecha = new SimpleDateFormat(DD_MM_YYYY);
				String strFecha = formatoFecha.format(listaSiniestros.get(i).getFechaComunicacion());
				cell.setCellValue(strFecha);	
			}
			
			cell = row.createCell(6);
			cell.setCellStyle(estiloFila);
			if ((null!=listaSiniestros.get(i).getLibro())) {			
				cell.setCellValue(listaSiniestros.get(i).getLibro());					
			}
			
			cell = row.createCell(7);
			cell.setCellStyle(estiloFila);
			if ((null!=listaSiniestros.get(i).getIdAnimal())) {			
				cell.setCellValue(listaSiniestros.get(i).getIdAnimal());					
			}
			
			cell = row.createCell(8);
			cell.setCellStyle(estiloFila);
			if ((null!=listaSiniestros.get(i).getTlfPerito())) {			
				cell.setCellValue(listaSiniestros.get(i).getTlfPerito());					
			}
			
			cell = row.createCell(9);
			cell.setCellStyle(estiloFila);
			cell.setCellValue(listaSiniestros.get(i).getTasado() ? SI : NO);					

			cell = row.createCell(10);
			cell.setCellStyle(estiloFila); 
			if ( null!=listaSiniestros.get(i).getFechaRetirada()) { 
				SimpleDateFormat formatoFecha = new SimpleDateFormat(DD_MM_YYYY);
				String strFecha = formatoFecha.format(listaSiniestros.get(i).getFechaRetirada());
				cell.setCellValue(strFecha);	
			}
			
			cell = row.createCell(11);
			cell.setCellStyle(estiloFilaNumero);
			if ( null!=listaSiniestros.get(i).getKilos()) { 
				cell.setCellValue(listaSiniestros.get(i).getKilos().doubleValue());	
			}
			
			cell = row.createCell(12);
			cell.setCellStyle(estiloFila);
			if ( null!=listaSiniestros.get(i).getCosteRetirada()) { 
				cell.setCellValue(listaSiniestros.get(i).getCosteRetirada().doubleValue());	
			}
			
			cell = row.createCell(13);
			cell.setCellStyle(estiloFila);
			if ( null!=listaSiniestros.get(i).getPagoGestora()) { 
				cell.setCellValue(listaSiniestros.get(i).getPagoGestora().doubleValue());	
			}
			
		}		
		
		logger.debug("SiniestrosGanadoController - insertarFilas - end");
		
	}

	/**
	 * 
	 * @param wb
	 */
	private void insertarCabeceras(HSSFWorkbook wb, boolean actas) {
		
		logger.debug("SiniestrosGanadoController - insertarCabeceras - init");

		HashMap<Integer, String> columnas;
		
		if (actas) {
			columnas = titulosColumnasActas;
		}
		else {
			columnas = titulosColumnas;
		}
		
		HSSFCellStyle estiloCabecera = ExcelUtils.getEstiloCabecera(wb);
		HSSFRow cabecera = wb.getSheetAt(wb.getActiveSheetIndex()).createRow(0);
		HSSFCell cell;
		
		for (int i=0;i<columnas.size();i++) {
			cell = cabecera.createCell(i);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString(columnas.get(i)));
		}
		
		logger.debug("SiniestrosGanadoController - insertarCabeceras - end");
	}

	
	public ModelAndView doExportarActasExcel(HttpServletRequest request, HttpServletResponse response, Siniestro siniestroBean) throws FileNotFoundException {
		
		
		logger.debug("SiniestrosGanadoController - exportarActasExcel - init");
		
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");

		String idPoliza = request.getParameter("idPoliza");
		if (idPoliza == null) {
			idPoliza = (request.getParameter("idPol"));
		}

		if (idPoliza == null) {
			idPoliza = request.getParameter("idPoliza_consFilActa");
		}

		Poliza poliza = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		String mensajeAlerta = request.getParameter("mensaje");
		if (!StringUtils.nullToString(mensajeAlerta).equals("")) {
			parametros.put("mensaje", mensajeAlerta);
		}
		String alerta = request.getParameter("alerta");
		if (!StringUtils.nullToString(alerta).equals("")) {
			parametros.put("alerta", alerta);
		}

		if (idPoliza != null)
			siniestroBean.getPoliza().setIdpoliza(Long.parseLong(idPoliza));
		else
			idPoliza = siniestroBean.getPoliza().getIdpoliza().toString();

		try {
			poliza = siniestrosGanadoManager.getPoliza(new Long(idPoliza));

			siniestroBean.setPoliza(poliza);

			List<SiniestroGanadoActas> listaSiniestrosGanadoActa = obtenerlistaActasGanado(request, realPath, poliza,
					usuario);
		
			if (listaSiniestrosGanadoActa.size() > 0) {
				generarFicheroExcelActas(response, listaSiniestrosGanadoActa);
			}
			logger.debug("SiniestrosGanadoController - exportarActasExcel - end");
			
		} catch (BusinessException e) {
			logger.error("Se ha producido un error indefinido durante la exportación a Excel", e);
			parametros.put("alerta", e.getMessage());
			return new ModelAndView("errorMensaje", "result", parametros);
		} catch (Exception e) {
			logger.error("Se ha producido un error indefinido durante la exportación a Excel", e);
			parametros.put("alerta", e.getMessage());
			return new ModelAndView("errorMensaje", "result", parametros);
		} 

		return null;
		
	}
	
	private void generarFicheroExcelActas(HttpServletResponse response, List<SiniestroGanadoActas> listaActas) {
		
		logger.debug("SiniestrosGanadoController - generarFicheroExcel - init");
		
		response.setContentType(APPLICATION_VND_MS_EXCEL);
        response.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME_LISTADO_ACTAS_SINIESTROS_GANADO_XLS);
        
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(LISTADO_ACTAS_SINIESTROS_GANADO); 
		sheet = wb.getSheetAt(0);								
		
		insertarCabeceras(wb, true);
		insertarFilasActas(wb, listaActas); 
		
		ExcelUtils.autoajustarColumnas(sheet, 10);	
		
		try {
			wb.write(response.getOutputStream());
			response.getOutputStream().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		logger.debug("SiniestrosGanadoController - generarFicheroExcel - end");
	}
	
	private void insertarFilasActas(HSSFWorkbook wb, List<SiniestroGanadoActas> listaActas) {
		
		logger.debug("SiniestrosGanadoController - insertarFilasActas - init");

		HSSFCellStyle estiloFila = ExcelUtils.getEstiloFila(wb);
		HSSFCellStyle getEstiloFilaNumeroEntero = ExcelUtils.getEstiloFilaNumeroEntero(wb);

		HSSFCell cell;
		
		for (int i=0; i<listaActas.size(); i++){
			
			HSSFRow row = wb.getSheetAt(wb.getActiveSheetIndex()).createRow(i+1); 

			cell = row.createCell(0);
			cell.setCellStyle(estiloFila); 
			if ( null!=listaActas.get(i).getProvincia()) { 
				cell.setCellValue(listaActas.get(i).getProvincia());					
			}
			
			cell = row.createCell(1);
			cell.setCellStyle(estiloFila); 
			if ((null!=listaActas.get(i).getTermino())) {			
				cell.setCellValue(listaActas.get(i).getTermino());					
			}
			
			cell = row.createCell(2);
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(getEstiloFilaNumeroEntero);
			if ((null!=listaActas.get(i).getSerieActa())) {			
				cell.setCellValue(listaActas.get(i).getSerieActa());					
			}
			
			cell = row.createCell(3);
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(getEstiloFilaNumeroEntero);
			if ((null!=listaActas.get(i).getNumActa())) {			
				cell.setCellValue(listaActas.get(i).getNumActa());					
			}
			
			cell = row.createCell(4);
			cell.setCellStyle(estiloFila); 
			if ((null!=listaActas.get(i).getLetra())) {			
				cell.setCellValue(listaActas.get(i).getLetra());					
			}
			
			cell = row.createCell(5);
			cell.setCellStyle(estiloFila); 
			if ( null!=listaActas.get(i).getEstado()) { 
				cell.setCellValue(listaActas.get(i).getEstado());					
			}
			
			cell = row.createCell(6);
			cell.setCellStyle(estiloFila);
			if ((null!=listaActas.get(i).getLibro())) {			
				cell.setCellValue(listaActas.get(i).getLibro());					
			}
			
			cell = row.createCell(7);
			cell.setCellStyle(estiloFila);
			if ((null!=listaActas.get(i).getIdAnimal())) {			
				cell.setCellValue(listaActas.get(i).getIdAnimal());					
			}
			
			cell = row.createCell(8);
			cell.setCellStyle(estiloFila);
			if ( null!=listaActas.get(i).getFechaActa()) { 
				SimpleDateFormat formatoFecha = new SimpleDateFormat(DD_MM_YYYY);
				String strFecha = formatoFecha.format(listaActas.get(i).getFechaActa());
				cell.setCellValue(strFecha);	
			}
			
			cell = row.createCell(9);
			cell.setCellStyle(estiloFila);
			if ((null!=listaActas.get(i).getImporteActa())) {			
				cell.setCellValue(listaActas.get(i).getImporteActa().doubleValue());					
			}

			cell = row.createCell(10);
			cell.setCellStyle(estiloFila);
			if ((null!=listaActas.get(i).getImporteDevolver())) {			
				cell.setCellValue(listaActas.get(i).getImporteDevolver().doubleValue());					
			}
			
			cell = row.createCell(11);
			cell.setCellStyle(estiloFila);
			if ( null!=listaActas.get(i).getFechaPago()) { 
				SimpleDateFormat formatoFecha = new SimpleDateFormat(DD_MM_YYYY);
				String strFecha = formatoFecha.format(listaActas.get(i).getFechaPago());
				cell.setCellValue(strFecha);	
			}
			
		}		
		
		logger.debug("SiniestrosGanadoController - insertarFilasActas - end");
		
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public	final Map<String, String> obtenerFiltrosConsulta(HttpServletRequest request) {
		
		logger.debug("SiniestrosGanadoController - obtenerFiltrosConsulta - init");

		final Map<String, String> filtro_siniestrosGan = new HashMap<String, String>();
		
		/* Obtenemos los campos del filtro y los guardamos en un Hashmap */
		String grupoNeg_consulta = request.getParameter("grupoNeg_consFiltro");
		if (!StringUtils.isNullOrEmpty(grupoNeg_consulta)) {
			filtro_siniestrosGan.put("GrupoNegSin", grupoNeg_consulta);
		}

		String serieSin_consulta = request.getParameter("serie_consFiltro");
		if (!StringUtils.isNullOrEmpty(serieSin_consulta)) {
			filtro_siniestrosGan.put("SerieSin", serieSin_consulta);
		}

		String numeroSin_consulta = request.getParameter("numero_consFiltro");
		if (!StringUtils.isNullOrEmpty(numeroSin_consulta)) {
			filtro_siniestrosGan.put("NumSin", numeroSin_consulta);
		}

		String libroSin_consulta = request.getParameter("libro_consFiltro");
		if (!StringUtils.isNullOrEmpty(libroSin_consulta)) {
			filtro_siniestrosGan.put("LibroSin", libroSin_consulta);
		}

		String fechaCom_consulta = request.getParameter("fechaCom_consFiltro");
		if (!StringUtils.isNullOrEmpty(fechaCom_consulta)) {
			filtro_siniestrosGan.put("FechaSin", fechaCom_consulta);
		}

		String fechaRet_consulta = request.getParameter("fechaRet_consFiltro");
		if (!StringUtils.isNullOrEmpty(fechaRet_consulta)) {
			filtro_siniestrosGan.put("FechaRet", fechaRet_consulta);
		}
		
		logger.debug("SiniestrosGanadoController - obtenerFiltrosConsulta - end");

		return filtro_siniestrosGan;
	}


	public void setSiniestrosGanadoManager(SiniestrosGanadoManager siniestrosGanadoManager) {
		this.siniestrosGanadoManager = siniestrosGanadoManager;
	}

}
