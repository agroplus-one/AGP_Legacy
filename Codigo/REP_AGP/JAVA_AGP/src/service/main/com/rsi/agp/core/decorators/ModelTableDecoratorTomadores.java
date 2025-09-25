package com.rsi.agp.core.decorators;

import org.displaytag.decorator.TableDecorator;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.Tomador;

public class ModelTableDecoratorTomadores  extends TableDecorator 
{
	public String getTomadorSelec ()
	{
		Tomador to = (Tomador)getCurrentRowObject();
		String entidad = "";
		String desc_entidad= "";
		String cifTomador = "";
		String razSoc = "";
		String via = "";
		String desc_via = "";
		String domicilio = "";
		String numVia = "";
		String piso = "";
		String bloque = "";
		String escalera = "";
		String codPostal = "";
		String provincia = "";
		String desc_provincia = "";
		String localidad = "";
		String desc_localidad = "";
		String subLocalidad = "";
		String telefono = "";
		String movil = "";
		String email = "";
		/* PTC. 78845 ** CAMPOS NUEVOS (03.03.2022) ** Inicio */
		String email2 = "";
		String email3 = "";
		/* PTC. 78845 ** CAMPOS NUEVOS (03.03.2022) ** Fin */
		String repreNombre = "";
		String repreAp1 = "";
		String repreAp2 = "";
		String repreNif = "";
		String envioAPagos = "";
		
		if(to.getEntidad() != null && to.getEntidad().getNomentidad() !=null)
			desc_entidad = StringUtils.forHTML(to.getEntidad().getNomentidad());
		
		if (to.getVia() != null && to.getVia().getClave() != null)
			via = StringUtils.forHTML(to.getVia().getClave());
		
		if(to.getVia() != null && to.getVia().getNombre() !=null)
			desc_via = StringUtils.forHTML(to.getVia().getNombre());
		
		if(to.getLocalidad() != null && to.getLocalidad().getProvincia().getNomprovincia() !=null)
			desc_provincia = StringUtils.forHTML(to.getLocalidad().getProvincia().getNomprovincia());
		
		if(to.getLocalidad() != null && to.getLocalidad().getNomlocalidad() !=null)
			desc_localidad = StringUtils.forHTML(to.getLocalidad().getNomlocalidad());
		
		if (to.getId() != null && to.getId().getCodentidad() != null)
			entidad = StringUtils.forHTML(to.getId().getCodentidad().toString());
		
		if (to.getId() != null && to.getId().getCiftomador() != null)
			cifTomador = StringUtils.forHTML(to.getId().getCiftomador());
		
		if (to.getRazonsocial()!=null)
			razSoc = StringUtils.forHTML(to.getRazonsocial());
		
		if (to.getDomicilio() != null)
			domicilio = StringUtils.forHTML(to.getDomicilio());
		
		if (to.getNumvia() != null)
			numVia = StringUtils.forHTML(to.getNumvia());
		
		if (to.getPiso() != null)
			piso = StringUtils.forHTML(to.getPiso());
		
		if (to.getBloque() != null)
			bloque = StringUtils.forHTML(to.getBloque());
		
		if (to.getEscalera() != null)
			escalera = StringUtils.forHTML(to.getEscalera());
		
		if (to.getCodpostal() != null)
			codPostal = StringUtils.forHTML(to.getCodpostalstr().toString());
		
		if (to.getLocalidad() != null && to.getLocalidad().getId().getCodprovincia() != null)
			provincia = StringUtils.forHTML(to.getLocalidad().getId().getCodprovincia().toString());
		
		if (to.getLocalidad() != null && to.getLocalidad().getId().getCodlocalidad() != null)
			localidad = StringUtils.forHTML(to.getLocalidad().getId().getCodlocalidad().toString());
		
		if (to.getLocalidad() != null && to.getLocalidad().getId().getSublocalidad() != null)
			subLocalidad = StringUtils.forHTML(to.getLocalidad().getId().getSublocalidad());
		
		if (to.getTelefono() != null)
			telefono = StringUtils.forHTML(to.getTelefono());
		
		if (to.getMovil() != null)
			movil = StringUtils.forHTML(to.getMovil());
		
		if (to.getEmail() != null)
			email = StringUtils.forHTML(to.getEmail());
		
		/* PTC. 78845 ** CAMPOS NUEVOS (03.03.2022) ** Inicio */
		if (to.getEmail2() != null)
			email2 = StringUtils.forHTML(to.getEmail2());

		if (to.getEmail3() != null)
			email3 = StringUtils.forHTML(to.getEmail3());
		/* PTC. 78845 ** CAMPOS NUEVOS (03.03.2022) ** Fin */
		
		if(to.getRepreNombre() !=null)
			repreNombre = StringUtils.forHTML(to.getRepreNombre());
		
		if(to.getRepreAp1() !=null)
			repreAp1 = StringUtils.forHTML(to.getRepreAp1());
		
		if(to.getRepreAp2() !=null)
			repreAp2 = StringUtils.forHTML(to.getRepreAp2());
		
		if(to.getRepreNif() !=null)
			repreNif = StringUtils.forHTML(to.getRepreNif());
		if(to.getEnvioAPagos() !=null)
			envioAPagos = StringUtils.forHTML(to.getEnvioAPagos().toString());
		
		
		String modif = "<a href=\"javascript:modificar('"+entidad+"','" +desc_entidad+"','"+ desc_via + "','" + desc_provincia +"','"
						+ desc_localidad+"','"+cifTomador+"',"+"'"+razSoc+"','"+via+"','"+domicilio+"','"+numVia+"','"+
						piso+"','"+bloque+"','"+escalera+"','"+codPostal+"','"+provincia
						+"','"+localidad+"','"+subLocalidad
						+"','"+telefono+"','"+movil+"','"+email+"','"+email2+"','"+email3+"','"+repreNombre+"','"+repreAp1
						+"','"+repreAp2+"','"+repreNif+"','"+envioAPagos+"')\"><img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/></a>";

		return modif+"<a href=\"javascript:enviarForm('baja','"+to.getId().getCiftomador()+"','"+to.getId().getCodentidad()+"')\">" +
				"<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar\" title=\"Borrar\"/></a>";
	}
	
	public String getTomCodEnt ()
	{
		Tomador to = (Tomador)getCurrentRowObject();
		return to.getId().getCodentidad().toString();
	}

	public String getTomCif ()
	{
		Tomador to = (Tomador)getCurrentRowObject();
		return to.getId().getCiftomador();
	}
	
	public String getTomRazSoc ()
	{
		String dev = "";
		Tomador to = (Tomador)getCurrentRowObject();
		if (to.getRazonsocial() != null)
			dev = to.getRazonsocial();
		return dev;
	}
	
	public String getTomProv () 
	{
		String dev = "";
		Tomador to = (Tomador)getCurrentRowObject();
		if (to.getLocalidad() != null && to.getLocalidad().getId().getCodprovincia()!=null)
			dev = to.getLocalidad().getId().getCodprovincia().toString();
		return dev;
	}
	
	public String getTomLocalidad ()
	{
		String dev = "";
		Tomador to = (Tomador)getCurrentRowObject();
		if (to.getLocalidad() != null && to.getLocalidad().getId().getCodlocalidad()!=null)
			dev = to.getLocalidad().getId().getCodlocalidad().toString();
		return dev;
	}
	
	public String getTomCodPostal ()
	{
		String dev = "";
		Tomador to = (Tomador)getCurrentRowObject();
		if (to.getCodpostal()!=null)
			dev = to.getCodpostalstr().toString();
		return dev;
	}
	
	public String getTomTelefono ()
	{
		String dev = "";
		Tomador to = (Tomador)getCurrentRowObject();
		if (to.getTelefono()!=null)
			dev = to.getTelefono();
		return dev;
	}
	
	public String getRepreNif ()
	{
		String RepNif = "";
		Tomador to = (Tomador)getCurrentRowObject();
		if (to.getRepreNif()!=null)
			RepNif = to.getRepreNif();
		return RepNif;
	}
	
	public String getRepNombreCompleto ()
	{
		String nombre = "";
		Tomador to = (Tomador)getCurrentRowObject();
	
		nombre = StringUtils.nullToString(to.getRepreNombre())+" "+StringUtils.nullToString(to.getRepreAp1())+" "+StringUtils.nullToString(to.getRepreAp2());
		
		return nombre;
	}
}
