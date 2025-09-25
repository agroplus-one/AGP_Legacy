package com.rsi.agp.core.managers.impl;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.CalculoServiceException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.AseguradoUtil;
import com.rsi.agp.core.util.ComparativaPolizaComparator;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.util.FluxCondensatorObject;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.VistaImportes;
import com.rsi.agp.dao.models.commons.IUserDao;
import com.rsi.agp.dao.models.poliza.IConsultaDetallePolizaDao;
import com.rsi.agp.dao.models.poliza.IDistribucionCosteDAO;
import com.rsi.agp.dao.tables.admin.Clase;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.config.DatosCabecera;
import com.rsi.agp.dao.tables.cpl.Medida;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaId;
import com.rsi.agp.dao.tables.poliza.EnvioAgroseguro;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;

import es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.PolizaDocument;

public class ConsultaDetallePolizaManager implements IManager {

	private static final String RDTO_HIST = "rdtoHist";
	private static final String SISTEMA_CULTIVO = "sistemaCultivo";
	private static final String VIENE_DE_UTILIDADES = "vieneDeUtilidades";
	private static final String MODO_LECTURA = "modoLectura";
	private static final String TIPO_LISTADO_GRID = "tipoListadoGrid";
	private static final String PARCELA_BUSQUEDA = "parcelaBusqueda";
	private static final String CAPITAL = "capital";
	
	private static final Log LOGGER = LogFactory.getLog(ConsultaDetallePolizaManager.class);
	
	private SeleccionPolizaManager seleccionPolizaManager;
	private IConsultaDetallePolizaDao consultaDetallePolizaDao;	
	private WebServicesManager webServicesManager;
	private WebServicesCplManager webServicesCplManager;
	private IUserDao usuarioDao;
	private ClaseManager claseManager;
	private IDistribucionCosteDAO distribucionCosteDAO;

	public Map<String, Object> getDatosParcela(HttpServletRequest request,Long idPoliza)throws Exception{
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		try{
			String isClickInListado  = StringUtils.nullToString(request.getParameter("isClickInListado"));
			String idsRowsChecked    = StringUtils.nullToString(request.getParameter("idsRowsChecked"));
			String marcarTodosChecks = StringUtils.nullToString(request.getParameter("marcarTodosChecks"));
			
			// si viene de la pantalla de parcelas tiene que dejar senalada la fila que edito
			String vieneDeParcela = request.getParameter("vieneDeParcela"); 
			String estaEditando = request.getParameter("estaEditando");
			String idParcelEdic = request.getParameter("idParcelEdic");
			
			if("true".equals(vieneDeParcela) && "true".equals(estaEditando.trim()))
			{
				parameters.put("selectedRow", true);
				parameters.put("idRow", idParcelEdic);
			}else{
				parameters.put("selectedRow", false);
			}
			// es que no esta paginando, reset valores checkeds
			if(isClickInListado.equals("si")){
				idsRowsChecked = "";
			}
			//Mantenemos filtros y ordenacion de la tabla
			String clearStatus = "false";
			List<Parcela> listaParcelasFiltro = null;
			List<Parcela> listaParcelasFiltroFinal = null;
			String parcelasStringFiltro = null;
			String sistcultivo = (String)request.getSession().getAttribute("sistcultivo");
			//String codCapital=  request.getParameter("capital");
			String codCapital=  (String)request.getSession().getAttribute(CAPITAL);
			
			if("true".equals(vieneDeParcela) && "true".equals(estaEditando.trim())){
				Parcela parcelaBusqueda = (Parcela) request.getSession().getAttribute(PARCELA_BUSQUEDA);
				
				if (parcelaBusqueda != null){
					parameters.put("filtro", request.getSession().getAttribute(PARCELA_BUSQUEDA));
					String columna = (String) request.getSession().getAttribute("columna");
					String orden = (String) request.getSession().getAttribute("orden");
					listaParcelasFiltro = seleccionPolizaManager.getParcelas(parcelaBusqueda, columna, orden);
					//ademas filtro tambien por el sistema de cultivo.
					listaParcelasFiltroFinal = seleccionPolizaManager.getParcelasFiltradas(listaParcelasFiltro, sistcultivo);
					parcelasStringFiltro = getListParcelasString(listaParcelasFiltroFinal);
					clearStatus = "false";
					
				}
			}
			if (estaEditando == null){
				request.getSession().removeAttribute(PARCELA_BUSQUEDA);
				clearStatus = "true";
			}
			// Recuperamos todas las parcelas
			double superficieTotal        = 0;
			CapitalAsegurado capital      = null;
			Iterator<CapitalAsegurado> it = null;
			
			Parcela parcela = new Parcela();
			parcela.getPoliza().setIdpoliza(idPoliza);
			String columna = (String) request.getSession().getAttribute("columna");
			String orden = (String) request.getSession().getAttribute("orden");
			List<Parcela> listaParcelas   = seleccionPolizaManager.getParcelas(parcela, columna, orden);
			
			for(int i = 0; i < listaParcelas.size(); i++){
				it = listaParcelas.get(i).getCapitalAsegurados().iterator();
				while(it.hasNext()){
					capital = (CapitalAsegurado) it.next();
					if(capital.getSuperficie() != null){
						superficieTotal += capital.getSuperficie().doubleValue();
					}						
				}
			}
			String parcelasString = getListParcelasString(listaParcelas);
			DecimalFormat df      = new DecimalFormat("0.00");
			
			String listCodModulos = seleccionPolizaManager.getListModulesWithComparativas(new Long(idPoliza));
			parameters.put("listCodModulos",     listCodModulos);
//			//para la lupa
//			String listCodModulos_cm=listCodModulos.replace(new Character(';'), new Character(','));
//			parameters.put("listCodModulos_cm",     listCodModulos_cm);
			
			if (!"".equals(StringUtils.nullToString(request.getParameter("alerta")))) {
				parameters.put("alerta",request.getParameter("alerta"));
			} 
			
			if (listaParcelasFiltroFinal != null && listaParcelasFiltroFinal.size()>0)
			{
				parameters.put("listadoParcelas",     listaParcelasFiltroFinal);
				parameters.put("numParcelasListado",  listaParcelasFiltroFinal.size());
				parameters.put("parcelasString",      parcelasStringFiltro);
			}else{
				parameters.put("listadoParcelas",     listaParcelas);
				parameters.put("numParcelasListado",  listaParcelas.size());
				parameters.put("parcelasString",      parcelasString);
			}
			
			String tipoListadoGrid = getTipoListadoGrid(request.getParameter(TIPO_LISTADO_GRID));
			String modelTableDecorator = getModelTableDecorator (tipoListadoGrid);
			String codEstadoPolizaMayor3 = getCodEstadoPolizaMayor3(new Long(idPoliza));
			
			parameters.put(CAPITAL, 	  		  codCapital);
			parameters.put(TIPO_LISTADO_GRID,     tipoListadoGrid);
			parameters.put("codEstadoPoliza",     codEstadoPolizaMayor3);
			parameters.put("modelTableDecorator", modelTableDecorator);
			parameters.put("marcarTodosChecks",   marcarTodosChecks);
			parameters.put("idsRowsChecked",      idsRowsChecked);
			parameters.put("idpoliza",            idPoliza);
			parameters.put("sistcultivo", 		  sistcultivo);
			parameters.put(MODO_LECTURA, StringUtils.nullToString(request.getParameter(MODO_LECTURA)));
			parameters.put(VIENE_DE_UTILIDADES, StringUtils.nullToString(request.getParameter(VIENE_DE_UTILIDADES)));
			parameters.put("clearStatus",   clearStatus);
			request.getSession().setAttribute("superficieTotal", df.format(superficieTotal));
		
		}catch (Exception e) {
			LOGGER.error("Error al listar las parcelas" + e.getMessage());
			throw e;
		}
		return parameters;
	}
	
	public Map<String, Object> consulta(HttpServletRequest request,
			Long idPoliza) throws Exception{
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<Parcela> listaParcelas = null;
		List<Parcela> listaParcelasFinal = null;
		List<Parcela> listaParcelasFinalesFiltradas=null;
		
		Parcela parcelaBusqueda= new Parcela();
		try{
			String despantalla = request.getParameter("despantalla");
			String idParcela = request.getParameter("codParcela");
			String sistcultivo = request.getParameter(SISTEMA_CULTIVO);
			String rdtoHist=request.getParameter(RDTO_HIST);
			String tipoListadoGrid = getTipoListadoGrid(request.getParameter(TIPO_LISTADO_GRID));
			String modelTableDecorator = getModelTableDecorator (tipoListadoGrid);
			String codEstadoPolizaMayor3 = getCodEstadoPolizaMayor3(new Long(idPoliza));
			String capital= request.getParameter(CAPITAL);
			String desc_capital= request.getParameter("desc_capital");
			String dessistemaCultivo=request.getParameter("dessistemaCultivo");
			
			Poliza polizaBean = new Poliza();
			polizaBean = seleccionPolizaManager.getPolizaById(idPoliza);
			parcelaBusqueda = getBeanParcelaFromRequest(request, idPoliza, tipoListadoGrid);
			
			// MPM - 08/05/12
			// Comprobamos si se ha seleccionado limpiar el formulario de busqueda
			boolean limpiar = StringUtils.nullToString(request.getParameter("limpiar")).equals("true");
			
			// DAA 24/04/12 cuando le damos a consultar para filtrar guardamos el filtro en session
			// y obtengo la lista de parcelas tras filtrar por parcelaBusqueda y el sistema de cultivo
			if (limpiar) {
				// Se limpiar los objetos de busqueda de sesion
				request.getSession().removeAttribute(PARCELA_BUSQUEDA);
				request.getSession().removeAttribute(SISTEMA_CULTIVO);
				request.getSession().removeAttribute(RDTO_HIST);
			}
			else {
				request.getSession().setAttribute(PARCELA_BUSQUEDA, parcelaBusqueda);
				request.getSession().setAttribute(SISTEMA_CULTIVO, sistcultivo);
				request.getSession().setAttribute(RDTO_HIST, rdtoHist);
			}
			String columna = (String) request.getSession().getAttribute("columna");
			String orden = (String) request.getSession().getAttribute("orden");
			listaParcelas = seleccionPolizaManager.getParcelas(parcelaBusqueda, columna, orden);
			listaParcelasFinal= seleccionPolizaManager.getParcelasFiltradas(listaParcelas, sistcultivo);
			listaParcelasFinalesFiltradas=seleccionPolizaManager.getParcelasFiltradasRdtoHist(listaParcelasFinal,rdtoHist);
			
			String listCodModulos = seleccionPolizaManager	.getListModulesWithComparativas(new Long(idPoliza));
			
//			//para la lupa
//			String listCodModulos_cm=listCodModulos.replace(new Character(';'), new Character(','));
//			parameters.put("listCodModulos_cm",     listCodModulos_cm);
			
			String parcelasString = getListParcelasString(listaParcelasFinal);
			
			parameters.put("filtro",              parcelaBusqueda);
			parameters.put("listCodModulos",      listCodModulos);
			parameters.put("numParcelasListado",  listaParcelasFinalesFiltradas.size());
			parameters.put("parcelasString",      parcelasString);
			parameters.put("polizaBean",          polizaBean);
			parameters.put(TIPO_LISTADO_GRID,     tipoListadoGrid);
			parameters.put("codEstadoPoliza",     codEstadoPolizaMayor3);
			parameters.put("modelTableDecorator", modelTableDecorator);
			parameters.put("listadoParcelas",     listaParcelasFinalesFiltradas);
			parameters.put("idpoliza",            idPoliza);
			parameters.put("despantalla",         despantalla);
			parameters.put("idParcela",           idParcela);
			parameters.put("totalProd",           new Long(0));
			parameters.put(SISTEMA_CULTIVO, 	  sistcultivo);
			parameters.put("dessistemaCultivo",	  dessistemaCultivo);
			parameters.put(CAPITAL, 	  		  capital);
			parameters.put("desc_capital", 	  	  desc_capital);
			parameters.put(MODO_LECTURA,         StringUtils.nullToString(request.getParameter(MODO_LECTURA)));
			parameters.put(VIENE_DE_UTILIDADES,   StringUtils.nullToString(request.getParameter(VIENE_DE_UTILIDADES)));
			parameters.put(RDTO_HIST, rdtoHist!=null?rdtoHist:"0");
		
		}catch (Exception e) {
			LOGGER.error("Error al consultar las parcelas" + e.getMessage());
			throw e;
		}
		return parameters;
		
	}
	public Map<String, Object> getXMLCalculoImportes(Poliza polizaBean) throws Exception  {
		
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		try {
			
			EnvioAgroseguro envio =consultaDetallePolizaDao.getXMLCalculoImportes(polizaBean);
			if (envio == null || envio.getCalculo() == null) throw new CalculoServiceException("No se ha podido obtener el XML de la Poliza");
			
			String xml = WSUtils.convertClob2String(envio.getCalculo());
			byte[] array = null;
			Base64Binary base64Binary = new Base64Binary();
			base64Binary.setContentType("text/xml");
			try {
				array = xml.getBytes(Constants.DEFAULT_ENCODING);
				base64Binary.setValue(array);
			} catch (UnsupportedEncodingException e2) {
				LOGGER.error("Se esperaba un XML en formato " + Constants.DEFAULT_ENCODING, e2);
				throw new CalculoServiceException("Se esperaba un XML en formato " + Constants.DEFAULT_ENCODING, e2);
			}
			byte[] byteArray = base64Binary.getValue();
			String xmlData = new String (byteArray, Constants.DEFAULT_ENCODING);
			xmlData = new String (byteArray, Constants.DEFAULT_ENCODING);
			
			// Si la póliza es de Ganado se utiliza el formato unificado
			if (polizaBean.getLinea().isLineaGanado()) {
				es.agroseguro.distribucionCostesSeguro.PolizaDocument polizaDoc = es.agroseguro.distribucionCostesSeguro.PolizaDocument.Factory.parse(new StringReader(xmlData));
				parameters.put("calculo", polizaDoc.getPoliza());				
			}
			// Póliza de Agrario
			else {
				PolizaDocument polizaDoc = PolizaDocument.Factory.parse(new StringReader(xmlData));
				parameters.put("calculo", polizaDoc.getPoliza());				
			}
			//PolizaDocument polizaDoc = PolizaDocument.Factory.parse(new StringReader(xmlData));
			//parameters.put("calculo", polizaDoc.getPoliza());					
			parameters.put("idEnvio", envio.getId());
			
			Poliza polizaDefinitiva = new Poliza ();
			polizaDefinitiva.setIdpoliza(polizaBean.getIdpoliza());
			parameters.put("polizaDefinitiva", polizaDefinitiva);
			parameters.put("numeroCuenta",AseguradoUtil.getFormattedBankAccount(polizaBean, true));
			parameters.put("numeroCuenta2",AseguradoUtil.getFormattedBankAccount(polizaBean, false));
			
			
		} catch (DAOException e) {
			LOGGER.error("Error al consultar el xml de Calculo ", e);
			throw e;
		} catch (Exception ex){
			LOGGER.error("Error al consultar el xml de  Calculo", ex);
			throw ex;
		}
		return parameters;
		
	}
	
	
	public List<DistribucionCoste2015> getDistribucionCoste2015ByIdPoliza(Long idpoliza) throws DAOException {
			
		List<DistribucionCoste2015> dc2015 = null;
		try {
			dc2015 = distribucionCosteDAO.getDistribucionCoste2015ByIdPoliza(idpoliza);
		} catch (DAOException e) {
			LOGGER.error("Error al consultar la distribucion de costes de la poliza", e);
			throw e;
		}
		return dc2015;
	}
	
	
	public Set<VistaImportes> getDataImportes(List<DistribucionCoste2015> listDc, Poliza polizaBean, Usuario usuario,
			String realPath) {
		
		
		List<ComparativaPoliza> listComparativasPoliza = polizaBean.getComparativaPolizas() != null
				&& !polizaBean.getComparativaPolizas().isEmpty()
						? Arrays.asList(polizaBean.getComparativaPolizas().toArray(new ComparativaPoliza[] {}))
						: new ArrayList<ComparativaPoliza>();
		
		Collections.sort(listComparativasPoliza, new ComparativaPolizaComparator());
		
		Map<String, String> comparativaIds = new HashMap<String, String>();

		Set<VistaImportes> fluxCondensatorHolder = new LinkedHashSet<VistaImportes>();
		
		LOGGER.debug("Obteniendo importes para poliza " + polizaBean.getIdpoliza());

		for (ComparativaPoliza cp : listComparativasPoliza) {

			String cpId = cp.getId().getIdpoliza() + "-" + cp.getId().getLineaseguroid() + "-"
					+ cp.getId().getCodmodulo() + "-" + cp.getId().getIdComparativa();

			LOGGER.debug("Comparativa " + cpId);

			if (!comparativaIds.containsKey(cpId)) {
				comparativaIds.put(cpId, cpId);
				List<DistribucionCoste2015> listDcPorIdComparativa = new ArrayList<DistribucionCoste2015>();
				for (DistribucionCoste2015 dc2015 : listDc) {
					LOGGER.debug("dc2015 codmodulo " + dc2015.getCodmodulo());
					LOGGER.debug("dc2015 idcomparativa" + dc2015.getIdcomparativa());
					if (dc2015.getCodmodulo().compareTo(cp.getId().getCodmodulo()) == 0 && dc2015.getIdcomparativa()
							.compareTo(new BigDecimal(cp.getId().getIdComparativa())) == 0) {
						listDcPorIdComparativa.add(dc2015);
						if (null != dc2015.getImportePagoFracc()) {
							cp.setEsFinanciada(true);
						}
					}
				}
				// unificamos en un solo metodo ya que la pantalla de importes se
				// muestra con los datos de la distribucion de costes, no del xml de calculo
				// como se hacia antes.
				LOGGER.debug("Montando fluxCondensatorHolder");
				fluxCondensatorHolder.add(webServicesManager.generateDataForImportesByDC(listDcPorIdComparativa,
						polizaBean, cp, usuario, realPath));
			}
		}
		
		// ESC-7773 U028612 - RENOVABLES SIN COMPARATIVA
		// SE GENERA UNA COMPARATIVA DE PEGA PARA QUE MUESTRE IMPORTES
		if (polizaBean.getLinea().isLineaGanado() && Constants.CHARACTER_S.equals(polizaBean.getRenovableSn())
				&& fluxCondensatorHolder.isEmpty()) {
			DistribucionCoste2015 dc2015 = listDc.get(0);
			ComparativaPoliza fake = new ComparativaPoliza();
			ComparativaPolizaId fakeId = new ComparativaPolizaId();
			fakeId.setIdpoliza(polizaBean.getIdpoliza());
			fakeId.setLineaseguroid(polizaBean.getLinea().getLineaseguroid());
			fakeId.setCodmodulo(polizaBean.getCodmodulo());
			fakeId.setFilacomparativa(dc2015.getFilacomparativa());
			fakeId.setIdComparativa(dc2015.getIdcomparativa() != null ? dc2015.getIdcomparativa().longValue() : null);
			fake.setId(fakeId);
			fake.setEsFinanciada(dc2015.getImportePagoFracc() != null);
			fluxCondensatorHolder
					.add(webServicesManager.generateDataForImportesByDC(listDc, polizaBean, fake, usuario, realPath));
		}else {
		
			/* Defecto 30 de la 63482 MODIF TAM (29.07.2021) ** Inicio */
			/* si no es ganado, y la poliza está en estado enviada correcta */
			if (!polizaBean.getLinea().isLineaGanado() && polizaBean.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA)
					&& fluxCondensatorHolder.isEmpty() && listComparativasPoliza.size() <= 0) {
				DistribucionCoste2015 dc2015 = listDc.get(0);
				ComparativaPoliza fake = new ComparativaPoliza();
				ComparativaPolizaId fakeId = new ComparativaPolizaId();
				fakeId.setIdpoliza(polizaBean.getIdpoliza());
				fakeId.setLineaseguroid(polizaBean.getLinea().getLineaseguroid());
				fakeId.setCodmodulo(polizaBean.getCodmodulo());
				fakeId.setFilacomparativa(dc2015.getFilacomparativa());
				fakeId.setIdComparativa(dc2015.getIdcomparativa() != null ? dc2015.getIdcomparativa().longValue() : null);
				fake.setId(fakeId);
				fake.setEsFinanciada(dc2015.getImportePagoFracc() != null);
				fluxCondensatorHolder
						.add(webServicesManager.generateDataForImportesByDC(listDc, polizaBean, fake, usuario, realPath));
			}

		}/* Fin del if */
		
		return fluxCondensatorHolder;
	}
	
	
	
	public FluxCondensatorObject getDataImportesCpl (Poliza polizaBean,Usuario usuario) throws Exception{
		FluxCondensatorObject fluxCondensator = null;
		//Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			/*EnvioAgroseguro envio =consultaDetallePolizaDao.getXMLCalculoImportes(polizaBean);
			if (envio.getCalculo() == null) throw new CalculoServiceException("No se ha podido obtener el XML de la Poliza");
			String xml = WSUtils.convertClob2String(envio.getCalculo());
			if (polizaBean.isPlanMayorIgual2015()) {
				xml = xml.replace("xml-fragment", "ns5:Poliza");
			}else {
				xml = xml.replace("xml-fragment", "ns2:Poliza");
					
			}
			byte[] array = null;
			Base64Binary base64Binary = new Base64Binary();
			base64Binary.setContentType("text/xml");
			try {
				array = xml.getBytes(Constants.DEFAULT_ENCODING);
				base64Binary.setValue(array);
			} catch (UnsupportedEncodingException e2) {
				LOGGER.error("Se esperaba un XML en formato " + Constants.DEFAULT_ENCODING, e2);
				throw new CalculoServiceException("Se esperaba un XML en formato " + Constants.DEFAULT_ENCODING, e2);
			}
			byte[] byteArray = base64Binary.getValue();
			String xmlData = new String (byteArray, Constants.DEFAULT_ENCODING);
			xmlData = new String (byteArray, Constants.DEFAULT_ENCODING);
			
			PolizaDocument polizaDoc = PolizaDocument.Factory.parse(new StringReader(xmlData));
			parameters.put("calculo", polizaDoc.getPoliza());					
			parameters.put("idEnvio", envio.getId());*/
			
			// se modifica para obtener los datos de la distribucion de costes,
			// no del xml de calculo
			//fluxCondensator = webServicesCplManager.generarCondensadorDeFlujo(parameters,polizaBean, null);
			List<DistribucionCoste2015> listDc = this.
					getDistribucionCoste2015ByIdPoliza (polizaBean.getIdpoliza());	
			
			fluxCondensator = webServicesCplManager.generarCondensadorDeFlujoDistCoste(listDc,polizaBean);
			
		} catch (DAOException e) {
			LOGGER.error("Error al consultar el xml  de Calculo" + e.getMessage());
			throw e;
		} catch (Exception ex){
			LOGGER.error("Error al consultar el xml de Calculo" + ex.getMessage());
			throw ex;
		}
		return fluxCondensator;
	}
	
		
	public String getTipoListadoGrid(String tipoListadoGrid){
		
		if (tipoListadoGrid != null) {
			if (tipoListadoGrid.equals("")){
				tipoListadoGrid = "todas";
			}
		} else {
			tipoListadoGrid = "todas";
		}
		return tipoListadoGrid;
		
	}
	
	public String getListParcelasString(List<Parcela> listParcelas) {

		StringBuilder result = new StringBuilder("");
		for (int i = 0; i < listParcelas.size(); i++) {
			String id = "";
			String tipoPar = "";

			// get id
			if (listParcelas.get(i).getIdparcela() != null) {
				id = listParcelas.get(i).getIdparcela().toString();
			} else {
				id = " ";
			}
			// get tipo
			if (listParcelas.get(i).getTipoparcela() != null) {
				tipoPar = listParcelas.get(i).getTipoparcela().toString();
			} else {
				tipoPar = " ";
			}

			// get localizacion: format: @@prov--comarca--termino--subtermino@@
			String localizacion = listParcelas.get(i).getTermino().getId().getCodprovincia() + "--"
					+ listParcelas.get(i).getTermino().getComarca().getId().getCodcomarca() + "--"
					+ listParcelas.get(i).getTermino().getId().getCodtermino() + "--"
					+ getSubterminoParcela(listParcelas.get(i));

			result.append(id + "_" + tipoPar + "_" + localizacion + ";");
		}

		return result.toString();
	}
	
	private String getSubterminoParcela(Parcela parcela){
		if (parcela.getTermino() == null || parcela.getTermino().getId() == null) {
			return "";
		} else {
			return StringUtils.nullToString(parcela.getTermino().getId().getSubtermino());
		}
	}

	public String getModelTableDecorator(String tipoListadoGrid) {
		// [solo Listado parcelas]set Model table decorator
		String modelTableDecorator = "";
		
		if (tipoListadoGrid.equals("parcelas") || tipoListadoGrid.equals("instalaciones")){
			modelTableDecorator = "com.rsi.agp.core.decorators.ModelTableDecoratorListaParcelas";
		} 
		else {
			modelTableDecorator = "com.rsi.agp.core.decorators.ModelTableDecoratorListaParcelasTodas";
		}
		
		return modelTableDecorator;
		
	}

	public String getCodEstadoPolizaMayor3(Long idPoliza) {
		String codEstadoPolizaMayor3 = "false";
		// estado de la poliza
		if (idPoliza != null) 
		{
			BigDecimal codEstado = seleccionPolizaManager.getEstadoPoliza(new Long(idPoliza));
			if (codEstado != null) {
				if (codEstado.compareTo(new BigDecimal("3")) == 1){
					codEstadoPolizaMayor3 = "true";
				}
			}
		}
		return codEstadoPolizaMayor3;
	}

	public void setSeleccionPolizaManager(
			SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}
	
	/**
	 * 
	 * @param HttpServletRequest
	 * @return RelacionCampo
	 */
	public Parcela getBeanParcelaFromRequest(HttpServletRequest request, Long idpoliza, String tipoListadoGrid) {
		Parcela pc = new Parcela();
		Termino tm = new Termino();

		// recogemos los parametros de consulta
		String provincia      = StringUtils.nullToString(request.getParameter("provincia"));
		String desc_provincia = StringUtils.nullToString(request.getParameter("desc_provincia"));
		String comarca        = StringUtils.nullToString(request.getParameter("comarca"));
		String desc_comarca   = StringUtils.nullToString(request.getParameter("desc_comarca"));
		String termino        = StringUtils.nullToString(request.getParameter("termino"));
		String desc_termino   = StringUtils.nullToString(request.getParameter("desc_termino"));
		String subtermino     = StringUtils.nullToString(request.getParameter("subtermino"));
		String cultivo        = StringUtils.nullToString(request.getParameter("cultivo"));
		String desc_cultivo   = StringUtils.nullToString(request.getParameter("desc_cultivo"));
		String variedad       = StringUtils.nullToString(request.getParameter("variedad"));
		String desc_variedad  = StringUtils.nullToString(request.getParameter("desc_variedad"));
		String nombre         = StringUtils.nullToString(request.getParameter("nombre"));
		String poligono       = StringUtils.nullToString(request.getParameter("poligono"));
		String parcela        = StringUtils.nullToString(request.getParameter("parcela"));
		String provSig        = StringUtils.nullToString(request.getParameter("provSig"));
		String TermSig        = StringUtils.nullToString(request.getParameter("TermSig"));
		String agrSig         = StringUtils.nullToString(request.getParameter("agrSig"));
		String zonaSig        = StringUtils.nullToString(request.getParameter("zonaSig"));
		String polSig         = StringUtils.nullToString(request.getParameter("polSig"));
		String parcSig        = StringUtils.nullToString(request.getParameter("parcSig"));
		String recSig         = StringUtils.nullToString(request.getParameter("recSig"));
		String capital        = StringUtils.nullToString(request.getParameter(CAPITAL));
		String desc_capital   = StringUtils.nullToString(request.getParameter("desc_capital"));
		
		try {

			// ************ tipo listado ************
			if (tipoListadoGrid != null) {
				if (tipoListadoGrid.equals("instalaciones"))
					pc.setTipoparcela('E');
				else if (tipoListadoGrid.equals("parcelas"))
					pc.setTipoparcela('P');
			}

			pc.getPoliza().setIdpoliza(idpoliza);
			// ************ Ubicacion ************
			// provincia
			if (!provincia.equals("")) {
				tm.getId().setCodprovincia(new BigDecimal(provincia));
				tm.getProvincia().setNomprovincia(desc_provincia);
			}
			// comarca
			if (!comarca.equals("")) {
				tm.getId().setCodcomarca(new BigDecimal(comarca));
				tm.getComarca().setNomcomarca(desc_comarca);
			}

			// termino
			if (!termino.equals("")) {
				tm.getId().setCodtermino(new BigDecimal(termino));
				tm.setNomtermino(desc_termino);
			}

			// subtermino
			if (!subtermino.equals("")) {
				tm.getId().setSubtermino(subtermino.charAt(0));
			}
			// cultivo
			if (!cultivo.equals("")) {
				pc.setCodcultivo(new BigDecimal(cultivo));
				pc.getVariedad().getCultivo().setDescultivo(desc_cultivo);
			}
			if(!capital.equals("")){
				TipoCapital tc = new TipoCapital();
				tc.setCodtipocapital(new BigDecimal(capital));
				if(!desc_capital.isEmpty())tc.setDestipocapital(desc_capital);
				CapitalAsegurado cp=new CapitalAsegurado();
				cp.setTipoCapital(tc);
				pc.getCapitalAsegurados().clear();
				pc.getCapitalAsegurados().add(cp);			
			}
			// variedad
			if (!variedad.equals("")) {
				pc.setCodvariedad(new BigDecimal(variedad));
				pc.getVariedad().setDesvariedad(desc_variedad);
			}
			// Nombre
			if (!nombre.equals("")) {
				pc.setNomparcela(nombre);
			}
			// ************ Ident. catastral ************
			// Poligono
			if (!poligono.equals("")) {
				pc.setPoligono(poligono);
			}
			// Parcela
			if (!parcela.equals("")) {
				pc.setParcela(parcela);
			}
			// ************ Sigpac ************
			if (!provSig.equals("")) {
				pc.setCodprovsigpac(new BigDecimal(provSig));
			}
			if (!TermSig.equals("")) {
				pc.setCodtermsigpac(new BigDecimal(TermSig));
			}
			if (!agrSig.equals("")) {
				pc.setAgrsigpac(new BigDecimal(agrSig));
			}
			if (!zonaSig.equals("")) {
				pc.setZonasigpac(new BigDecimal(zonaSig));
			}
			if (!polSig.equals("")) {
				pc.setPoligonosigpac(new BigDecimal(polSig));
			}
			if (!parcSig.equals("")) {
				pc.setParcelasigpac(new BigDecimal(parcSig));
			}
			if (!recSig.equals("")) {
				pc.setRecintosigpac(new BigDecimal(recSig));
			}
			pc.setTermino(tm);

		} catch (Exception excepcion) {
			LOGGER.error("Excepcion : ConsultaDetallePolizaManager - getBeanParcelaFromRequest", excepcion);
		}

		return pc;
	}
	
	public void cargaCabecera(Poliza polizaBean, HttpServletRequest request) throws Exception {

		DatosCabecera datos = new DatosCabecera();
		String nombre="";
		
		try{
			
			if (polizaBean.getAsegurado().getTipoidentificacion().equals("CIF"))
				nombre = polizaBean.getAsegurado().getRazonsocial();
			else
				nombre = polizaBean.getAsegurado().getNombre()+" "+polizaBean.getAsegurado().getApellido1()+" "+polizaBean.getAsegurado().getApellido2();
			
			datos.setAsegurado(nombre);
			datos.setNifCif(polizaBean.getAsegurado().getNifcif());
			datos.setColectivo(polizaBean.getColectivo().getNomcolectivo());
			datos.setPlanLinea(polizaBean.getLinea().getCodplan()+"/"+polizaBean.getLinea().getCodlinea());
			datos.setClase(polizaBean.getClase().toString());
			
			Usuario usuario = polizaBean.getUsuario();
			
			datos.setUsuario(usuario.getNombreusu());
			datos.setEntidad(polizaBean.getColectivo().getTomador().getId().getCodentidad().toString()+" "+
					polizaBean.getAsegurado().getEntidad().getNomentidad());
			Medida medida = new Medida();
			String nifcif = polizaBean.getAsegurado().getNifcif();
			Long lineaseguroid = polizaBean.getLinea().getLineaseguroid();
			medida = usuarioDao.getMedida(lineaseguroid, nifcif);
			if (medida == null)
				medida = new Medida();
			Clase clase = new Clase();
			clase = claseManager.getClase(polizaBean);
			//calculamos el intervalo de CoefReduccRdto
			
			if (clase != null){

				Usuario usu = new Usuario();
				usu.setAsegurado(polizaBean.getAsegurado());
				usu.setColectivo(polizaBean.getColectivo());
				String intervaloCoefReduccionRdtoStr = seleccionPolizaManager.calcularIntervaloCoefReduccionRdtoPoliza(usu,clase.getId());
				request.getSession().setAttribute("intervaloCoefReduccionRdto", intervaloCoefReduccionRdtoStr);
			}
			request.getSession().setAttribute("datosCabecera", datos);
			request.getSession().setAttribute("medida", medida);
		}catch(Exception e){
			LOGGER.error("Error al cargar los datos de la cabecera" + e.getMessage());
			throw e;
		}
	}
	
	/* Pet. 63485 ** MODIF TAM (28.07.2020) ** Inicio */
	/* Nos declaramos una función nueva para la consulta de Polizas de Agrícolas No confirmadas por Agroseguro */
	public Set<VistaImportes> getDataImportesConsultaAgri(List<DistribucionCoste2015> listDc, Poliza polizaBean, Usuario usuario,
			String realPath) {
		
		List<ComparativaPoliza> listComparativasPoliza = polizaBean.getComparativaPolizas() != null
				&& !polizaBean.getComparativaPolizas().isEmpty()
						? Arrays.asList(polizaBean.getComparativaPolizas().toArray(new ComparativaPoliza[] {}))
						: new ArrayList<ComparativaPoliza>();
		
		Collections.sort(listComparativasPoliza, new ComparativaPolizaComparator());
		
		Map<String, String> comparativaIds = new HashMap<String, String>();

		Set<VistaImportes> fluxCondensatorHolder = new LinkedHashSet<VistaImportes>();

		LOGGER.debug("Obteniendo importes para poliza " + polizaBean.getIdpoliza());
		
		for (ComparativaPoliza cp : listComparativasPoliza) {

			String cpId = cp.getId().getIdpoliza() + "-" + cp.getId().getLineaseguroid() + "-"
					+ cp.getId().getCodmodulo() + "-" + cp.getId().getIdComparativa();

			LOGGER.debug("Comparativa " + cpId);
			
			if (!comparativaIds.containsKey(cpId)) {
				comparativaIds.put(cpId, cpId);
				List<DistribucionCoste2015> listDcPorIdComparativa = new ArrayList<DistribucionCoste2015>();
				for (DistribucionCoste2015 dc2015 : listDc) {
					LOGGER.debug("dc2015 codmodulo " + dc2015.getCodmodulo());
					LOGGER.debug("dc2015 idcomparativa" + dc2015.getIdcomparativa());
					if (dc2015.getCodmodulo().compareTo(cp.getId().getCodmodulo()) == 0 && dc2015.getIdcomparativa()
							.compareTo(new BigDecimal(cp.getId().getIdComparativa())) == 0) {
						listDcPorIdComparativa.add(dc2015);
						if (null != dc2015.getImportePagoFracc()) {
							cp.setEsFinanciada(true);
						}
						break;
					}
				}
				// unificamos en un solo metodo ya que la pantalla de importes se
				// muestra con los datos de la distribucion de costes, no del xml de calculo
				// como se hacia antes.
				LOGGER.debug("Montando fluxCondensatorHolder");
				fluxCondensatorHolder.add(webServicesManager.consultaDataForImportesByDCAgri(listDcPorIdComparativa,
						polizaBean, cp, usuario, realPath));
			}
		}
		
		// ESC-7773 U028612 - RENOVABLES SIN COMPARATIVA
		// SE GENERA UNA COMPARATIVA DE PEGA PARA QUE MUESTRE IMPORTES
		if (polizaBean.getLinea().isLineaGanado() && Constants.CHARACTER_S.equals(polizaBean.getRenovableSn())
				&& fluxCondensatorHolder.isEmpty()) {

			for (DistribucionCoste2015 dc2015 : listDc) {

				ComparativaPoliza fake = new ComparativaPoliza();
				ComparativaPolizaId fakeId = new ComparativaPolizaId();
				fakeId.setIdpoliza(polizaBean.getIdpoliza());
				fakeId.setLineaseguroid(polizaBean.getLinea().getLineaseguroid());
				fakeId.setCodmodulo(polizaBean.getCodmodulo());
				fakeId.setFilacomparativa(dc2015.getFilacomparativa());
				fakeId.setIdComparativa(
						dc2015.getIdcomparativa() != null ? dc2015.getIdcomparativa().longValue() : null);
				fake.setId(fakeId);
				fake.setEsFinanciada(dc2015.getImportePagoFracc() != null);

				fluxCondensatorHolder.add(webServicesManager.generateDataForImportesByDC(
						Arrays.asList(new DistribucionCoste2015[] { dc2015 }), polizaBean, fake, usuario, realPath));
			}
		}
		
		return fluxCondensatorHolder;
	}
	
	public boolean isFechaEnvioPosteriorSep2020(Date fechaEnvio) throws ParseException {
		
		boolean valor = false;
		String fecStr = "2020-09-16";
		Date fecEfecto;
		
		fecEfecto = new SimpleDateFormat("yyyy-MM-dd").parse(fecStr);
		
		if(fechaEnvio.compareTo(fecEfecto) > 0) { //si la fechaEnvio es posterior a fecEfecto (2020-09-16)
			valor = true;
		}
		
		return valor;
	}
	
	
	/* Pet. 63485 ** MODIF TAM (28.07.2020) ** Fin */

	public void setConsultaDetallePolizaDao(
			IConsultaDetallePolizaDao consultaDetallePolizaDao) {
		this.consultaDetallePolizaDao = consultaDetallePolizaDao;
	}

	public void setWebServicesManager(WebServicesManager webServicesManager) {
		this.webServicesManager = webServicesManager;
	}
	
	public void setUsuarioDao(IUserDao usuarioDao) {
		this.usuarioDao = usuarioDao;
	}

	public void setClaseManager(ClaseManager claseManager) {
		this.claseManager = claseManager;
	}

	public void setWebServicesCplManager(WebServicesCplManager webServicesCplManager) {
		this.webServicesCplManager = webServicesCplManager;
	}

	public void setDistribucionCosteDAO(IDistribucionCosteDAO distribucionCosteDAO) {
		this.distribucionCosteDAO = distribucionCosteDAO;
	}

	


}
