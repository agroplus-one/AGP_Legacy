package com.rsi.agp.core.decorators;

import java.util.ResourceBundle;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class ModelTableDecoratorCopys extends TableDecorator {	
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	
	// para perfiles 0 o 5
	public String getPolSelec() {
		String cadena = "";
		Poliza po = (Poliza)getCurrentRowObject();
		cadena += "<a href=\"#\" onclick=\"javascript:imprimirInforme('"+po.getIdpoliza().toString()+"','"+po.getTipoReferencia()+"','"+po.getLinea().getCodplan()+"','"+po.getReferencia()+"')\"><img src='jsp/img/displaytag/imprimir.png' alt='Imprimir p&oacute;liza' title='Imprimir p&oacute;liza'/></a>";
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
	
	
	
	public String getPolColectivo() {
		Poliza po = (Poliza)getCurrentRowObject();
		return po.getColectivo().getIdcolectivo() + "-" + po.getColectivo().getDc();
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

	
	public String getPolPoliza() {
		Poliza po = (Poliza)getCurrentRowObject();
		//return StringUtils.nullToString(po.getReferencia());
		if ((!"".equals(StringUtils.nullToString(po.getReferencia()))) && (!"".equals(StringUtils.nullToString(po.getReferencia())))){
			return StringUtils.nullToString(po.getReferencia()) + "-" + po.getDc();
		} else {
			return StringUtils.nullToString(po.getReferencia());
		}
	}
	
	public String getPolPlan() {
		Poliza po = (Poliza)getCurrentRowObject();
		return po.getLinea().getCodplan().toString();
	}
	
	public String getPolLinea() {
		Poliza po = (Poliza)getCurrentRowObject();
		return po.getLinea().getCodlinea().toString();
	}
	
	public String getPolTipoRef(){
		Poliza po = (Poliza)getCurrentRowObject();
		String res = "";
		if (po.getTipoReferencia() != null) {
			if(po.getTipoReferencia().equals('P')){
				res = "P";
			}else{
				res = "C";
			}
		}
		return res;
	}
	
}
