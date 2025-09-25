package com.rsi.agp.core.decorators;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.Socio;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.SubvencionSocio;


public class ModelTableDecoratorSocios extends TableDecorator 
{
	public String getSocioSelec()
	{
		Socio so = (Socio)getCurrentRowObject();
				
		String idAsegurado = "";
		String cifnif = "";
		String tipoIdent = "";
		String nombre = "";
		String apellido1 = "";
		String apellido2 = "";
		String razonSocial = "";
		String ss = "";
		String indRegimen = "";
		String atp = "";
		String jovenAgricultor = "";		
		String modif = "";
		
		if (so.getId().getIdasegurado() != null)
			idAsegurado = so.getId().getIdasegurado().toString();
		
		if (so.getId().getNif() != null)
			cifnif = so.getId().getNif();
		
		if (so.getTipoidentificacion() != null)
			tipoIdent = so.getTipoidentificacion();
		
		if (so.getNombre() != null)
			nombre = so.getNombre();
		
		if (so.getApellido1() != null)
			apellido1 = so.getApellido1();
		
		if (so.getApellido2() != null)
			apellido2 = so.getApellido2();
		
		if (so.getRazonsocial() != null)
			razonSocial = so.getRazonsocial();
		
		if (so.getNumsegsocial() != null)
			ss = so.getNumsegsocial();
		
		if (so.getRegimensegsocial() != null)
			indRegimen = so.getRegimensegsocial().toString();
		
		if (so.getAtp() != null)
			atp = so.getAtp();
		
		if (so.getJovenagricultor() != null)
			jovenAgricultor = so.getJovenagricultor().toString();
		
		if ((so.getBaja() == null) || (so.getBaja() != 'S')){
			modif = "<a href=\"javascript:modificar('"+idAsegurado+"','"+cifnif+"','"+tipoIdent+"','"+nombre+"','"+apellido1+"','"
			+apellido2+"','"+razonSocial+"','"+ss+"','"+indRegimen+"','"+atp+"','"+jovenAgricultor+"')\">";
			modif += "<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/></a>";
			modif += "<a href=\"javascript:baja('"+idAsegurado+"','"+cifnif+"')\">";
			modif += "<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar\" title=\"Borrar\"/></a>";
		} else {
			modif += "<a href=\"#\" onclick=\"javascript:deshacerSocio('"+idAsegurado+"','"+cifnif+"')\">";
			modif += "<img src='jsp/img/displaytag/deshacer.png' alt='Deshacer' title='Deshacer'/></a>";
		}		
		
		return modif;
	}
	
	public String getSocioSoloSelec ()
	{
		Socio so = (Socio)getCurrentRowObject();
		
		String idAsegurado = "";
		String cifnif = "";
		String nombre = "";
		String cadenaDev = "";
		String esModif = "NO";
		if (so.getId().getIdasegurado() != null)
			idAsegurado = so.getId().getIdasegurado().toString();
		
		if (so.getId().getNif() != null)
			cifnif = so.getId().getNif();
		
		if ((so.getTipoidentificacion().equals("NIF") || so.getTipoidentificacion().equalsIgnoreCase("NIE")) && so.getNombre() != null)
			nombre = so.getNombre() + " " + StringUtils.nullToString(so.getApellido1()) + " " + StringUtils.nullToString(so.getApellido2());
		else
			nombre = so.getRazonsocial();
		if (so.getSubvencionSocios()!= null && so.getSubvencionSocios().size() > 0)
		{
			esModif = "SI";
		}
		cadenaDev += "<a href=\"javascript:modificar('"+idAsegurado+"','"+cifnif+"','"+nombre.trim()+"','"+esModif+"')\">";
		cadenaDev += "<img src=\"jsp/img/displaytag/edit.png\" alt=\"Elegir\" title=\"Elegir\"/></a>";
		
		return cadenaDev;
	}
	
	public String getSocioNombre ()
	{
		Socio so = (Socio)getCurrentRowObject();		
		String cadena = "";
		
		if(so.getTipoidentificacion().equalsIgnoreCase("CIF") && so.getRazonsocial()!=null){
			cadena = so.getRazonsocial().toString();
		}else if ((so.getTipoidentificacion().equalsIgnoreCase("NIF") || so.getTipoidentificacion().equalsIgnoreCase("NIE")) && 
			  (so.getNombre()!= null && so.getApellido1()!=null)){
			   cadena = so.getNombre().toString() + " " +  so.getApellido1().toString()+ " "+ StringUtils.nullToString(so.getApellido2());
		      }
		
		return cadena;
	}
	
	public String getSocioCif ()
	{
		Socio so = (Socio)getCurrentRowObject();		
		String cadena = "";
		
		if (so.getId().getNif() != null)
			cadena = so.getId().getNif();
		
		return cadena;
	}
	
	public String getSocioSS ()
	{
		Socio so = (Socio)getCurrentRowObject();		
		String cadena = "";
		
		if (so.getNumsegsocial() != null)
			cadena = so.getNumsegsocial();
		
		return cadena;
	}
	
	public String getSocioRegimen ()
	{
		Socio so = (Socio)getCurrentRowObject();		
		String cadena = "";
		
		if (so.getRegimensegsocial() != null)
		{
			switch (so.getRegimensegsocial().intValue())
			{
				case 0: cadena = "Aut&oacute;nomo";
						break;
				case 1: cadena = "Rea cuenta Ajena";
						break;
				case 2: cadena = "Rea cuenta propia";
						break;	
				default:
						break;
			}
		}
		
		return cadena;
	}
	
	public String getSocioATP ()
	{
		Socio so = (Socio)getCurrentRowObject();		
		String cadena = "";
		
		if (so.getAtp() != null){
			if (so.getAtp().equalsIgnoreCase("S"))
				cadena = "SI";
			else if (so.getAtp().equalsIgnoreCase("N"))
				cadena = "NO";
		}
			
		
		return cadena;
	}
	
	public String getSocioJAgr ()
	{
		Socio so = (Socio)getCurrentRowObject();		
		String cadena = "";
		
		if (so.getJovenagricultor() != null)
		{
			if (so.getJovenagricultor().equals('S'))
				cadena = "SI";
			else if (so.getJovenagricultor().equals('N'))
				cadena = "NO";
		}
		
		return cadena;
	}
	
	
	public String getSubvDeclaradas ()
	{
		Socio so = (Socio)getCurrentRowObject();		
		String cadena = "";
		
		//Obtengo el identificador de la póliza para saber si el socio tiene subvenciones para la misma.
		Long idpoliza = new Long(this.getPageContext().getRequest().getParameter("idpoliza"));
		Poliza p = null;
		for (Poliza pol: so.getAsegurado().getPolizas()){
			if (pol.getIdpoliza().equals(idpoliza)){
				p = pol;
				break;
			}
		}
		
		if (p != null && so.getSubvencionSocios()!= null && so.getSubvencionSocios().size() > 0)
		{
			for (SubvencionSocio ss: so.getSubvencionSocios()){
				if (ss.getSubvencionEnesa().getId().getLineaseguroid().equals(p.getLinea().getLineaseguroid()) &&
						ss.getPoliza().getIdpoliza().equals(p.getIdpoliza())){
					cadena = "SI";
					break;
				}
			}
		}
		
		return cadena;
	}
	
	public String getBaja ()
	{
		Socio so = (Socio)getCurrentRowObject();		
		String cadena = "SI";
		
		if ((so.getBaja() == null) || (!so.getBaja().equals('S'))){
			cadena = "NO";
		}
		
		return cadena;
	}
}
