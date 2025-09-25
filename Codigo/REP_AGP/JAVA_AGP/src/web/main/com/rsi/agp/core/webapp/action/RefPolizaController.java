package com.rsi.agp.core.webapp.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.dao.models.poliza.IRefPolizaDao;


public class RefPolizaController extends BaseMultiActionController{
	private static final Log logger = LogFactory.getLog(RefPolizaController.class);
	
	private IRefPolizaDao refPolizaDao;
	//private ServicioPolizaOrigenPDFHelper     servicioPolizaOrigenPDFHelper       = new ServicioPolizaOrigenPDFHelper();
	
	@SuppressWarnings(value={"all"})
	
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response) {
		
		String ipdelhijoputa = request.getRemoteAddr();
		String hostdelhijoputa = request.getRemoteHost();
		
		logger.info("Acceso a refPolizaController desde la ip " + ipdelhijoputa + " - host " + hostdelhijoputa);
		
		String msg = "Acceso a refPolizaController desde la ip " + ipdelhijoputa + " - host " + hostdelhijoputa;
		this.refPolizaDao.sendMail(msg);
		
		/*String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		List refs = refPolizaDao.getReferenciasAgricolas();
		
		List listPolNoExisten = new ArrayList(); 
		
		//recorremos todas las referencias y buscamos su poliza origen
		for (int i=0; i<refs.size();i++){
			Base64Binary pdf = null;
			Object[] ob = (Object[]) refs.get(i);
			String refpoliza = (String) ob[0];
			
			//compruebo si existe la principal con el plan 2012
			Poliza poliza = new Poliza();
			poliza.setReferencia(refpoliza);
			poliza.setTipoReferencia('P');
			Linea l = new Linea();
			l.setCodplan(new BigDecimal(2012));
			poliza.setLinea(l);
			//logger.info("referencia: "+refpoliza+ " tipo: "+poliza.getTipoReferencia()+" plan: "+ poliza.getLinea().getCodplan());
			
			try {
				pdf = servicioPolizaOrigenPDFHelper.doWork(poliza,realPath);
			} catch (Exception e) {
				pdf = null;
			}
			
			if (pdf!= null){ //si existe la P,2012
				pdf = null;
				//miramos si la Principal esta en tb_polizas
				Poliza pol = refPolizaDao.getPoliza(refpoliza,poliza.getTipoReferencia(),poliza.getLinea().getCodplan());
				if (pol == null)//no existe en bbdd
					listPolNoExisten.add(refpoliza+";"+poliza.getTipoReferencia()+";"+poliza.getLinea().getCodplan());
				
				//buscamos la complementaria
				poliza.setTipoReferencia('C');
				//logger.info("referencia: "+refpoliza+ " tipo: "+poliza.getTipoReferencia()+" plan: "+ poliza.getLinea().getCodplan());
				try {
					pdf = servicioPolizaOrigenPDFHelper.doWork(poliza,realPath);
				} catch (Exception e) {
					pdf = null;
				}
				if (pdf!= null){ //existe la cpl
					//miramos si esta en tb_polizas
					Poliza pol2 = refPolizaDao.getPoliza(refpoliza,poliza.getTipoReferencia(),poliza.getLinea().getCodplan());
					if (pol2 == null){//no existe en bbdd
						listPolNoExisten.add(refpoliza+";"+poliza.getTipoReferencia()+";"+poliza.getLinea().getCodplan());
					}
				}
				
			}else{ //No existe 2012,P miramos el plan 2011,P
				
				l.setCodplan(new BigDecimal(2011));
				poliza.setLinea(l);
				//logger.info("referencia: "+refpoliza+ " tipo: "+poliza.getReferencia()+"plan: "+ poliza.getLinea().getCodplan());
				try {
					pdf = servicioPolizaOrigenPDFHelper.doWork(poliza,realPath);
				} catch (Exception e) {
					pdf = null;
				}
				if (pdf!=null){ //existe P,2011
					pdf = null;
					//miramos si esta en tb_polizas
					Poliza pol3 = refPolizaDao.getPoliza(refpoliza,poliza.getTipoReferencia(),poliza.getLinea().getCodplan());
					if (pol3 == null)//no existe en bbdd
						listPolNoExisten.add(refpoliza+";"+poliza.getTipoReferencia()+";"+poliza.getLinea().getCodplan());
					
					//miramos a ver si hay cpl
					poliza.setTipoReferencia('C');
					//logger.info("referencia: "+refpoliza+ " tipo: "+poliza.getReferencia()+"plan: "+ poliza.getLinea().getCodplan());
					try {
						pdf = servicioPolizaOrigenPDFHelper.doWork(poliza,realPath);
					} catch (Exception e) {
						pdf = null;
					}
					if (pdf!=null){
						//miramos si esta en tb_polizas
						Poliza pol4 = refPolizaDao.getPoliza(refpoliza,poliza.getTipoReferencia(),poliza.getLinea().getCodplan());
						if (pol4 == null){//no existe en bbdd
							listPolNoExisten.add(refpoliza+";"+poliza.getTipoReferencia()+";"+poliza.getLinea().getCodplan());
						}
					}
				}
			}
		}
		logger.info("[ASF] - Proceso de verificacion finalizado - [ASF]");
		if (listPolNoExisten.size() == 0){
			logger.info("************** No se han encontrado polizas *********************");
		}
		for (int j=0;j<listPolNoExisten.size();j++){
			logger.info("************** Listado de polizas *********************");
			logger.info(listPolNoExisten.get(j));
		}*/
		
		return null;
		
	}
		
		
	public void setRefPolizaDao(IRefPolizaDao refPolizaDao) {
		this.refPolizaDao = refPolizaDao;
	}
	
	
	

}
