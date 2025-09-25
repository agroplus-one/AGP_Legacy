package com.rsi.agp.core.decorators;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;
import com.rsi.agp.dao.tables.siniestro.Siniestro;

public class ModelTableDecoratorListaPolizas extends TableDecorator {	
	
	private final Log logger = LogFactory.getLog(ModelTableDecoratorListaPolizas.class);
	
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");

	public String getPolSelec() {
		String cadena = "";
		Poliza po = (Poliza)getCurrentRowObject();
		cadena = "<input type='radio' name='id' value='"+po.getIdpoliza().toString()+ "'/>";
		return cadena;
    }
	
	public String getPolEntidad() {
		Poliza po = (Poliza)getCurrentRowObject();
		return po.getColectivo().getTomador().getEntidad().getCodentidad().toString();
	}
	
	public String getColectivo_tomador_id_codentidad() {
		Poliza po = (Poliza)getCurrentRowObject();
		return po.getColectivo().getTomador().getEntidad().getCodentidad().toString();
	}
	
	public String getPolOficina() {
		String oficina = "";
		Poliza po = (Poliza)getCurrentRowObject();
		if (po.getUsuario() != null)
			oficina = po.getOficina();
		return oficina;
	}
	
	public String getOficina() {
		String oficina = "";
		Poliza po = (Poliza)getCurrentRowObject();
		if (po.getUsuario() != null)
			oficina = po.getOficina();
		return oficina;
	}
	
	public String getPolUsuario () {
		String usuario = "";
		Poliza po = (Poliza)getCurrentRowObject();
		if (po.getUsuario() != null)
		{
			usuario = po.getUsuario().getCodusuario();
		}
		return usuario;
	}
	
	public String getPolPlan() {
		Poliza po = (Poliza)getCurrentRowObject();
		return po.getColectivo().getLinea().getCodplan().toString();
	}
	
	public String getPolLinea() {
		Poliza po = (Poliza)getCurrentRowObject();
		return po.getColectivo().getLinea().getCodlinea().toString();
	}
	
	public String getPolColectivo() {
		Poliza po = (Poliza)getCurrentRowObject();
		return po.getColectivo().getIdcolectivo() + "-" + po.getColectivo().getDc();
	}
	
	public String getPolPoliza() {
		Poliza po = (Poliza)getCurrentRowObject();
		//return StringUtils.nullToString(po.getReferencia());
		if ((!"".equals(StringUtils.nullToString(po.getReferencia()))) && (!"".equals(StringUtils.nullToString(po.getDc())))){
			return StringUtils.nullToString(po.getReferencia()) + "-" + po.getDc();
		} else {
			return StringUtils.nullToString(po.getReferencia());
		}
	}
	
	public String getImporte() {
		String importe = "";
		Poliza po = (Poliza)getCurrentRowObject();
		if (po.getPagoPolizas() != null){
			for (PagoPoliza pago : po.getPagoPolizas()) {
				// MPM 13/08/12 - Control sobre el importe nulo
				importe = (pago.getImporte() == null) ? "" : pago.getImporte().toString();
			}
		}
			
		return importe;
	}
	
	public String getPolModulo() {
		String modulo = "";
		Poliza po = (Poliza)getCurrentRowObject();
		if (po.getCodmodulo() != null)
			modulo = po.getCodmodulo();
		return modulo;
	}
	
	public String getPolCifNif() {
		Poliza po = (Poliza)getCurrentRowObject();
		return po.getAsegurado().getNifcif();
	}
	
	public String getPolNombreAseg() {
		Poliza po = (Poliza)getCurrentRowObject();
		String nombre = "";
		if (po.getAsegurado().getTipoidentificacion().equals("CIF"))
			nombre = po.getAsegurado().getRazonsocial();
		else
			nombre = po.getAsegurado().getNombre()+" "+po.getAsegurado().getApellido1()+" "+po.getAsegurado().getApellido2();
		return nombre;
	}
	
	public String getPolEstado() {
		Poliza po = (Poliza)getCurrentRowObject();
		
		String estado = "";		
		if (po.getEstadoPoliza() != null)
		{
			//estado = estados.get(po.getEstado());
			estado = po.getEstadoPoliza().getDescEstado();
		}
		
		return estado;
	}
	public String getPolStr(){
		Poliza po = (Poliza)getCurrentRowObject();
		String res = "N";
		Set<Siniestro> siniestros = po.getSiniestros();		
		if (siniestros.size() >0)
			res = "S";
		
		return res;
	}
	public String getPolRc(){
		Poliza po = (Poliza)getCurrentRowObject();
		String res = "N";
		Set<ReduccionCapital> redSet = po.getReduccionesCapital();
		if (redSet.size() >0)
			res = "S";
				
		return res;
	}
	public String getPolMOD(){
		Poliza po = (Poliza)getCurrentRowObject();
		String res = "N";
		Set<AnexoModificacion> anexMOD = po.getAnexoModificacions();
		if (anexMOD.size() >0)
			res = "S";
				
		return res;
	}
	public String getPolTipoRef(){
		Poliza po = (Poliza)getCurrentRowObject();
		String res = "";
		if (po.getTipoReferencia() != null) {
			if(po.getTipoReferencia().equals('P')){
				res="PRINCIPAL";
			}else{
				res = "COMPLEMENTARIA";
			}
		}
		return res;
	}
	public String getPolFechaEnvio(){
		Poliza po = (Poliza)getCurrentRowObject();
		String res = "";
		if (po.getFechaenvio() != null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaEnvio = po.getFechaenvio();
			res = sdf.format(fechaEnvio);
		}
		return res;
	}
	
	public String getPolClase() {
		String res = "";
		try {
			Poliza po = (Poliza)getCurrentRowObject();
			res=po.getClase().toString();
		} catch (NullPointerException e) {
			logger.error(e);
		}
		return res;
	}
	
}
