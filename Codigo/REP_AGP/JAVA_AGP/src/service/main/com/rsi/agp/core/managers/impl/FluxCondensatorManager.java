package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.webapp.util.FluxCondensatorObject;
import com.rsi.agp.core.webapp.util.StringUtils;

public class FluxCondensatorManager implements IManager{
	
	private static final Log logger = LogFactory.getLog(FluxCondensatorManager.class);	
		
	//	Función que a partir de una lista de distribuciones de coste, obtiene la correspondiente
	//	a la comparativa que se le pasa por parametro y formatea los datos para mostrarlos en
	//	el informe de comparativas
	public FluxCondensatorObject getDistCosteComparativa(Set<FluxCondensatorObject> fluxCondensatorHolder, String strModulo, char separador){
		logger.debug("init - getDistCosteComparativa");
		FluxCondensatorObject distCoste = null;
		String valor = "";		
		
		// Se escoge la distribucion de costes correspondiente al modulo y fila seleccionadoss
		for (FluxCondensatorObject fco : fluxCondensatorHolder){
			if (fco.getComparativaSeleccionada().equals(strModulo)){
				distCoste = fco;
				break;
			}					
		}
		
		//	Se elimina el formato de moneda en aquellos campos en los que se guardo de esa forma
		//	También se adaptan otros campos de la distribución de coste, para que se muestren igual que en el informe de la poliza
		if (distCoste != null){
			if ((distCoste.getPrimaComercial() != null) && (distCoste.getPrimaComercial().indexOf(separador) > -1)){
				valor = distCoste.getPrimaComercial().substring(0, distCoste.getPrimaComercial().indexOf(separador) - 1);
				distCoste.setPrimaComercial(valor);
			} else if ((distCoste.getPrimaComercial() != null) && (distCoste.getPrimaComercial().equals("N/D"))){
				distCoste.setPrimaComercial("0,00");
			}
			
			if ((distCoste.getPrimaNeta() != null) && (distCoste.getPrimaNeta().indexOf(separador) > -1)){
				valor = distCoste.getPrimaNeta().substring(0, distCoste.getPrimaNeta().indexOf(separador) - 1);
				distCoste.setPrimaNeta(valor);
			} else if ((distCoste.getPrimaNeta() != null) && (distCoste.getPrimaNeta().equals("N/D"))){
				distCoste.setPrimaNeta("0,00");
			}
			
			if ((distCoste.getCosteNeto() != null) && (distCoste.getCosteNeto().indexOf(separador) > -1)){
				valor = distCoste.getCosteNeto().substring(0, distCoste.getCosteNeto().indexOf(separador) - 1);
				distCoste.setCosteNeto(valor);
			} else if ((distCoste.getCosteNeto() != null) && (distCoste.getCosteNeto().equals("N/D"))){
				distCoste.setCosteNeto("0,00");
			}
			
			if ((distCoste.getBonifMedidaPreventiva() != null) && (distCoste.getBonifMedidaPreventiva().indexOf(separador) > -1)){
				valor = distCoste.getBonifMedidaPreventiva().substring(0, distCoste.getBonifMedidaPreventiva().indexOf(separador) - 1);
				distCoste.setBonifMedidaPreventiva(valor);
			} else if ((distCoste.getBonifMedidaPreventiva() != null) && (distCoste.getBonifMedidaPreventiva().equals("N/D"))){
				distCoste.setBonifMedidaPreventiva("0,00");
			}
			
			if ((distCoste.getDescuentoContColectiva() != null) && (distCoste.getDescuentoContColectiva().indexOf(separador) > -1)){
				valor = distCoste.getDescuentoContColectiva().substring(0, distCoste.getDescuentoContColectiva().indexOf(separador) - 1);
				distCoste.setDescuentoContColectiva(valor);
			} else if ((distCoste.getDescuentoContColectiva() != null) && (distCoste.getDescuentoContColectiva().equals("N/D"))){
				distCoste.setDescuentoContColectiva("0,00");
			}
			
			if ((distCoste.getBonifAsegurado() != null) && (distCoste.getBonifAsegurado().indexOf(separador) > -1)){
				valor = distCoste.getBonifAsegurado().substring(0, distCoste.getBonifAsegurado().indexOf(separador) - 1);
				distCoste.setBonifAsegurado(valor);
			} else if ((distCoste.getRecargoAsegurado() != null) && (distCoste.getRecargoAsegurado().equals("N/D"))){
				distCoste.setBonifAsegurado(null);
			}
			
			if ((distCoste.getPctBonifAsegurado() != null) && (distCoste.getPctBonifAsegurado().indexOf(separador) > -1)){
				valor = distCoste.getPctBonifAsegurado().substring(0, distCoste.getPctBonifAsegurado().indexOf(separador) - 1);
				distCoste.setPctBonifAsegurado(valor);
			} else if ((distCoste.getPctBonifAsegurado() != null) && (distCoste.getPctBonifAsegurado().equals("N/D"))){
				distCoste.setPctBonifAsegurado("0,00");
			}
			
			if ((distCoste.getRecargoAsegurado() != null) && (distCoste.getRecargoAsegurado().indexOf(separador) > -1)){
				valor = distCoste.getRecargoAsegurado().substring(0, distCoste.getRecargoAsegurado().indexOf(separador) - 1);
				distCoste.setRecargoAsegurado(valor);
			} else if ((distCoste.getRecargoAsegurado() != null) && (distCoste.getRecargoAsegurado().equals("N/D"))){
				distCoste.setRecargoAsegurado(null);
			}
			
			if ((distCoste.getPctRecargoAsegurado() != null) && (distCoste.getPctRecargoAsegurado().indexOf(separador) > -1)){
				valor = distCoste.getPctRecargoAsegurado().substring(0, distCoste.getPctRecargoAsegurado().indexOf(separador) - 1);
				distCoste.setPctRecargoAsegurado(valor);
			} else if ((distCoste.getPctRecargoAsegurado() != null) && (distCoste.getPctRecargoAsegurado().equals("N/D"))){
				distCoste.setPctRecargoAsegurado("0,00");
			}
			
			if ((distCoste.getConsorcioReaseguro() != null) && (distCoste.getConsorcioReaseguro().indexOf(separador) > -1)){
				valor = distCoste.getConsorcioReaseguro().substring(0, distCoste.getConsorcioReaseguro().indexOf(separador) - 1);
				distCoste.setConsorcioReaseguro(valor);
			} else if ((distCoste.getConsorcioReaseguro() != null) && (distCoste.getConsorcioReaseguro().equals("N/D"))){
				distCoste.setConsorcioReaseguro("0,00");
			}
			
			if ((distCoste.getConsorcioRecargo() != null) && (distCoste.getConsorcioRecargo().indexOf(separador) > -1)){
				valor = distCoste.getConsorcioRecargo().substring(0, distCoste.getConsorcioRecargo().indexOf(separador) - 1);
				distCoste.setConsorcioRecargo(valor);
			} else if ((distCoste.getConsorcioRecargo() != null) && (distCoste.getConsorcioRecargo().equals("N/D"))){
				distCoste.setConsorcioRecargo("0,00");
			}
			
			if ((distCoste.getImporteTomador() != null) && (distCoste.getImporteTomador().indexOf(separador) > -1)){
				valor = distCoste.getImporteTomador().substring(0, distCoste.getImporteTomador().indexOf(separador) - 1);
				distCoste.setImporteTomador(valor);
			} else if ((distCoste.getImporteTomador() != null) && (distCoste.getImporteTomador().equals("N/D"))){
				distCoste.setImporteTomador("0,00");
			}
			
			
			/*if ((distCoste.getSubvCCAA() != null) && (distCoste.getSubvCCAA().entrySet()..size() > 0)){
				distCoste.set
				Iterator<Entry<String,String>> it = distCoste.getSubvCCAA().entrySet().iterator();
				Entry<String,String> valorAux = null;
				while (it.hasNext()){
					valorAux = it.next();
					if valorAux.getValue()
				}
			}
			
			
			distCoste.addSubCCAA(this.getCCAA(key.charAt(0)),StringUtils.formatMoney(valor));
			distCoste.addSubEnesa(this.getDescripcionEnesa(new BigDecimal(subEnesa[i].getTipo())), 
					 StringUtils.formatMoney(subEnesa[i].getSubvencionEnesa()));*/
		}		
		
		logger.debug("end - getDistCosteComparativa");
		return distCoste;
	}
	
}
