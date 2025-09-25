package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.managers.impl.FinanciacionManager;
import com.rsi.agp.core.webapp.action.utilidades.ConsultaDetallePolizaController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.VistaImportes;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.ImporteFraccionamiento;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class FinanciacionController extends BaseMultiActionController {
	private FinanciacionManager financiacionManager;
	private ConsultaDetallePolizaController consultaDetallePolizaController;
	
	@SuppressWarnings("unchecked")
	public ModelAndView doFinanciar(HttpServletRequest request,
			HttpServletResponse response, Poliza polizabean) throws Exception {
		
		ModelAndView mv = null;
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		String idPoliza = StringUtils.nullToString(request.getParameter("idpoliza"));
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		//Extraemos parámetros de financiacion
		String seleccionada = request.getParameter("financiacionSeleccionada");
		String[] cadena = seleccionada.split("\\|");
		String codModulo = cadena[1];
		BigDecimal filaComp = new BigDecimal(cadena[7]);
		Long idComparativa = new Long(cadena[0]);
		
		
		//Llamamos procedimiento de proceso en el manager
		String resProcesoFinanciacion = financiacionManager.procesoFinanciacion(realPath, codModulo, filaComp, idComparativa, 
				new Long(idPoliza), usuario, request.getParameterMap());
		
		//Llamamos al procedimiento en manager que crea lista de Vista Importes desde BBDD
		mv=consultaDetallePolizaController.doVerImportes(request, response, null);
		
		//Llamamos a método privado que genera los parámetros necesarios de la jsp
		if(null!=resProcesoFinanciacion){
			mv.addObject("alerta", resProcesoFinanciacion);
		}
		return mv;
		
	}
	
	
public void muestraFinanciar(Set<VistaImportes> fluxCondensatorHolder, ImporteFraccionamiento impFrac) {
		
		for(VistaImportes fco : fluxCondensatorHolder){
		
				if(fco.getImporteTomador() != null){
					String impTomStr=fco.getImporteTomador().replace(".", "");
					impTomStr=impTomStr.replace(",", ".");
					BigDecimal impTom = new BigDecimal(impTomStr);
					
					// Si el importe de la póliza supera el mínimo para financiar
					if(impFrac != null){
						if( impTom.compareTo(impFrac.getImporte())!=-1) {
							fco.setMuestraBotonFinanciar(true);
							fco.setEsFraccAgr (false);
						}
					}else{
						fco.setMuestraBotonFinanciar(true);
						fco.setEsFraccAgr (false);
					}
				}
			}
		}
	
	public FinanciacionManager getFinanciacionManager() {
		return financiacionManager;
	}

	public void setFinanciacionManager(FinanciacionManager financiacionManager) {
		this.financiacionManager = financiacionManager;
	}

	public ConsultaDetallePolizaController getConsultaDetallePolizaController() {
		return consultaDetallePolizaController;
	}

	public void setConsultaDetallePolizaController(
			ConsultaDetallePolizaController consultaDetallePolizaController) {
		this.consultaDetallePolizaController = consultaDetallePolizaController;
	}

	
}
