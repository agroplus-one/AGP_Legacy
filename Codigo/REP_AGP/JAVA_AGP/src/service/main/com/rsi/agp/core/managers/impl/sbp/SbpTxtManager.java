package com.rsi.agp.core.managers.impl.sbp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.ISbpTxtManager;
import com.rsi.agp.core.managers.ISimulacionSbpManager;
import com.rsi.agp.core.managers.impl.PolizaCopyManager;
import com.rsi.agp.core.managers.impl.SeleccionPolizaManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.copy.IPolizaCopyDao;
import com.rsi.agp.dao.models.sbp.ISimulacionSbpDao;
import com.rsi.agp.dao.models.sbp.ISobrePrecioDao;
import com.rsi.agp.dao.models.sbp.ITxtDescripcionRiesgoDao;
import com.rsi.agp.dao.models.sbp.ITxtFranquiciaDao;
import com.rsi.agp.dao.models.sbp.ITxtPeriodoCarenciaDao;
import com.rsi.agp.dao.models.sbp.ITxtPrimaTotalDao;
import com.rsi.agp.dao.models.sbp.ITxtRiesgoGarantizadoDao;
import com.rsi.agp.dao.models.sbp.TxtFranquiciaDao;
import com.rsi.agp.dao.tables.commons.ComarcaId;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.sbp.EstadoPlzSbp;
import com.rsi.agp.dao.tables.sbp.MtoImpuestoSbp;
import com.rsi.agp.dao.tables.sbp.ParcelaSbp;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;
import com.rsi.agp.dao.tables.sbp.PrimaMinimaSbp;
import com.rsi.agp.dao.tables.sbp.Sobreprecio;
import com.rsi.agp.dao.tables.sbp.TasasSbp;
import com.rsi.agp.dao.tables.sbp.TipoEnvio;

public class SbpTxtManager implements ISbpTxtManager {

	protected final Log logger = LogFactory.getLog(getClass());
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_sbp");
	private ITxtDescripcionRiesgoDao txtDescripcionRiesgoDao;
	private ITxtFranquiciaDao txtFranquiciaDao;
	private ITxtPeriodoCarenciaDao txtPeriodoCarenciaDao;
	private ITxtRiesgoGarantizadoDao txtRiesgoGarantizadoDao;
	private ITxtPrimaTotalDao txtPrimaTotalDao;
	
	

	/** DAA 06/05/2013
	 * Obtiene los txt descriptivos del informe de la Poliza de Sbp
	 * @return 
	 *  
	 */
	public HashMap<String, Object> getTxtInformePolizaSbp(BigDecimal codPlan){
		logger.debug("getTxtInformePolizaSbp - Obtenemos las descripciones para el plan " + codPlan);
		HashMap<String,Object> parametros = new HashMap<String, Object>();
		
		String descRiesgo = txtDescripcionRiesgoDao.getTxtDescRiesgo(codPlan);
		String franquicia = txtFranquiciaDao.getTxtFranquicia(codPlan);
		String periodoCarencia = txtPeriodoCarenciaDao.getTxtPeriodoCarencia(codPlan);
		String riesgoGarantizado = txtRiesgoGarantizadoDao.getTxtRiesgoGarantizado(codPlan);
		String primaTotal = txtPrimaTotalDao.getTxtPrimaTotal(codPlan);
			
		parametros.put("descRiesgo",descRiesgo);
		parametros.put("franquicia",franquicia);
		parametros.put("periodoCarencia",periodoCarencia);
		parametros.put("riesgoGarantizado",riesgoGarantizado);
		parametros.put("primaTotal", primaTotal);
		
		return parametros;
	}



	public void setTxtDescripcionRiesgoDao(
			ITxtDescripcionRiesgoDao txtDescripcionRiesgoDao) {
		this.txtDescripcionRiesgoDao = txtDescripcionRiesgoDao;
	}



	public void setTxtFranquiciaDao(ITxtFranquiciaDao txtFranquiciaDao) {
		this.txtFranquiciaDao = txtFranquiciaDao;
	}



	public void setTxtPeriodoCarenciaDao(
			ITxtPeriodoCarenciaDao txtPeriodoCarenciaDao) {
		this.txtPeriodoCarenciaDao = txtPeriodoCarenciaDao;
	}



	public void setTxtRiesgoGarantizadoDao(
			ITxtRiesgoGarantizadoDao txtRiesgoGarantizadoDao) {
		this.txtRiesgoGarantizadoDao = txtRiesgoGarantizadoDao;
	}



	public void setTxtPrimaTotalDao(ITxtPrimaTotalDao txtPrimaTotalDao) {
		this.txtPrimaTotalDao = txtPrimaTotalDao;
	}

}
