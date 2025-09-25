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

public class ModelTableDecoratorPolizasAseg extends TableDecorator {	
	
	private final Log logger = LogFactory.getLog(ModelTableDecoratorPolizasAseg.class);
	
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	public String getPolSelec() {
		
		Poliza po = (Poliza)getCurrentRowObject();
		//String codentidad = "";
		String codusuario = "";
		String codplan = "";
		String colectivo = "";
		String codlinea = "";
		String nomlinea = "";
		//String oficina = "";
	    String codmodulo = "";
	    //String nifcif = "";
	    //String estado = "";
	    String fechaenvioStr = "";
	    String dc = "";
	    //Date fechaenvio = new Date();
	    
		//if (null != po.getColectivo().getTomador().getId().getCodentidad())
		//	codentidad = po.getColectivo().getTomador().getId().getCodentidad().toString();
		if (null != po.getUsuario())
			codusuario	= po.getUsuario().getCodusuario();
		if (null != po.getColectivo() 
				&& null != po.getColectivo().getIdcolectivo())
			colectivo	= po.getColectivo().getIdcolectivo();
		if (null != po.getColectivo() 
				&& null != po.getColectivo().getLinea() 
				&& null != po.getColectivo().getLinea().getCodplan())
			codplan = po.getColectivo().getLinea().getCodplan().toString();
		if (null != po.getColectivo()
				&& null != po.getColectivo().getLinea()
				&& null != po.getColectivo().getLinea().getCodlinea())
			codlinea = po.getColectivo().getLinea().getCodlinea().toString();
		if (null != po.getColectivo()
				&& null != po.getColectivo().getLinea()
				&& null != po.getColectivo().getLinea().getNomlinea())
			nomlinea = po.getColectivo().getLinea().getNomlinea();
		//if (null != po.getUsuarioAlta())	
		//	oficina	= po.getOficina()getUsuario().getOficina().getId().getCodoficina().toString();
		if (null != po.getCodmodulo())	
			codmodulo = po.getCodmodulo().toString();
		if (null != po.getFechaenvio()){	
			
			fechaenvioStr = sdf.format(po.getFechaenvio());
		}
		//if (null != po.getAsegurado() 
			//	&& null != po.getAsegurado().getNifcif())	
			//nifcif = po.getAsegurado().getNifcif().toString();
		//if (null != po.getEstadoPoliza())			
			//estado = po.getEstadoPoliza().getIdestado()+"";
		if (null != po.getColectivo()
				&& null != po.getColectivo().getDc())
			dc = po.getColectivo().getDc();
			
		
		String modif = "";
		
		modif = "<a href=\"#\" onclick=\"javascript:modificar('"+StringUtils.nullToString(po.getOficina())+"', '"+codusuario+"','"+colectivo+"',"+
				"'"+dc+"','"+codplan+"','"+codlinea+"','"+nomlinea+"','"+codmodulo+"','"+
				StringUtils.nullToString(po.getReferencia())+"','"+fechaenvioStr+"')\">"
				+"<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/></a>&nbsp;";
		return  modif;		
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
				importe = pago.getImporte().toString();
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
	
	public String getPolTipoRef(){
		Poliza po = (Poliza)getCurrentRowObject();
		String res = "";
		if (po.getTipoReferencia() != null) {
			if(po.getTipoReferencia().equals('P')){
				res="P";
			}else{
				res = "C";
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