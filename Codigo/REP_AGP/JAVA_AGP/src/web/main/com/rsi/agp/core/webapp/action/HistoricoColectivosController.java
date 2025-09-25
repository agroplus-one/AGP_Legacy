package com.rsi.agp.core.webapp.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.impl.HistoricoColectivosManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.HistoricoColectivos;

public class HistoricoColectivosController extends BaseMultiActionController{

	private static final Log logger = LogFactory.getLog(HistoricoColectivosController.class);
	private HistoricoColectivosManager historicoColectivosManager;
	
	
	public ModelAndView doConsulta(HttpServletRequest request,HttpServletResponse response, HistoricoColectivos historicoColectivoBean) throws Exception {
		logger.debug("init: HistoricoColectivos - doConsulta");
		ModelAndView mv = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<HistoricoColectivos> listHistoricoColectivos = null;
		
		String operacion = request.getParameter("operacion");

		try{
			
			/* MODIF TAM (02.01.2020) - Se ha sacado a otra función */
			/* Si venimos de pulsar el boton de borrado*/
			/*if ((operacion != null) && (operacion.equals("baja"))){
				Long idHistorico = historicoColectivoBean.getId();
				historicoColectivosManager.borrarHistoricoColectivo(idHistorico);
				historicoColectivoBean.setColectivo(new Colectivo());
				historicoColectivoBean.getColectivo().setIdcolectivo(request.getParameter("idHistorico"));
				
			} else {*/
				//Se filtra el historico por Id
				if (!StringUtils.nullToString(request.getParameter("idHistorico")).equals("")){
					
					historicoColectivoBean.setColectivo(new Colectivo());
					historicoColectivoBean.getColectivo().setIdcolectivo(request.getParameter("idHistorico"));
				}
				
				// Se comprueban las fechas primer pago y segundo pago
				if (historicoColectivoBean != null && historicoColectivoBean.getFechaprimerpago() == null){
					String fechaPrimerPago = request.getParameter("fechaIni");
					if (!StringUtils.nullToString(fechaPrimerPago).equals("")) {
						DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
						historicoColectivoBean.setFechaprimerpago(df.parse(fechaPrimerPago));
					}
				}
				if (historicoColectivoBean != null && historicoColectivoBean.getFechasegundopago() == null){
					String fechaSegundoPago = request.getParameter("fechaFin");
					if (!StringUtils.nullToString(fechaSegundoPago).equals("")) {
						DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
						historicoColectivoBean.setFechasegundopago(df.parse(fechaSegundoPago));
					}
				}
				
				// Se comprueban las fechas de cambio y efecto
				if (historicoColectivoBean != null && historicoColectivoBean.getFechacambio() == null){
					String fechaCambio = request.getParameter("fechaCambio");
					if (!StringUtils.nullToString(fechaCambio).equals("")) {
						DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
						historicoColectivoBean.setFechacambio(df.parse(fechaCambio));
					}
				}
				
				if (historicoColectivoBean != null && historicoColectivoBean.getFechaefecto() == null){
					String fechaEfecto = request.getParameter("fechaEfecto");
					if (!StringUtils.nullToString(fechaEfecto).equals("")) {
						DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
						historicoColectivoBean.setFechaefecto(df.parse(fechaEfecto));
					}
				}
			/*}*/
			
			
			listHistoricoColectivos = historicoColectivosManager.getListHistoricoColectivos(historicoColectivoBean);
			
			parameters.put("totalListSize", listHistoricoColectivos.size());
			parameters.put("listHistoricoColectivos", listHistoricoColectivos);
			
			parameters.put("idHistorico", request.getParameter("idHistorico"));
			
			mv = new ModelAndView("moduloAdministracion/colectivos/historicoColectivos", "historicoColectivoBean", historicoColectivoBean);
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error: " + be.getMessage());
			throw new Exception("Se ha producido un error: " + be.getMessage());
		}
		
		logger.debug("end: HistoricoColectivos - doConsulta");
		return mv.addAllObjects(parameters);
	
	}
	
	/* MODIF TAM (02.01.2020) */
	/* Damos de alta una nueva función que se encarga de la baja del historico del colectivo */
	public ModelAndView doBajaHistorico(HttpServletRequest request,HttpServletResponse response, HistoricoColectivos historicoColectivoBean) throws Exception {
		logger.debug("init: HistoricoColectivos - doBajaHistorico");
		ModelAndView mv = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<HistoricoColectivos> listHistoricoColectivos = null;
		
		String operacion = request.getParameter("operacion");

		try{
			
			Long idHistorico = historicoColectivoBean.getId();
			historicoColectivosManager.borrarHistoricoColectivo(idHistorico);
			historicoColectivoBean.setColectivo(new Colectivo());
			historicoColectivoBean.getColectivo().setIdcolectivo(request.getParameter("idHistorico"));
				
			listHistoricoColectivos = historicoColectivosManager.getListHistoricoColectivos(historicoColectivoBean);
			
			parameters.put("totalListSize", listHistoricoColectivos.size());
			parameters.put("listHistoricoColectivos", listHistoricoColectivos);
			
			parameters.put("idHistorico", request.getParameter("idHistorico"));
			
			mv = new ModelAndView("moduloAdministracion/colectivos/historicoColectivos", "historicoColectivoBean", historicoColectivoBean);
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error: " + be.getMessage());
			throw new Exception("Se ha producido un error: " + be.getMessage());
		} catch (DAOException de) {
			logger.error("Se ha producido un error: " + de.getMessage());
			throw new Exception("Se ha producido un error: " + de.getMessage());
		}
		
		logger.debug("end: HistoricoColectivos - doBajaColectivo");
		return mv.addAllObjects(parameters);
	
	}
	/* FIN MODIF TAM (02.01.2020) */


	public void setHistoricoColectivosManager(HistoricoColectivosManager historicoColectivosManager) {
		this.historicoColectivosManager = historicoColectivosManager;
	}
	
}