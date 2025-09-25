package com.rsi.agp.core.decorators;

import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.commons.Usuario;

public class ModelTableDecoratorAsegurados extends TableDecorator
{
	private static final Log logger = LogFactory.getLog(ModelTableDecoratorAsegurados.class);
	
	private static final String COMA = "','";

	public String getAseguradoSelec () {
		Asegurado as = (Asegurado)getCurrentRowObject();
		StringBuilder cadenaDev = new StringBuilder();
		
		String idAsegurado = "";
		String entidad = "";
		String tipoIndent = "";
		String cifNif ="";
		String nombre = "";
		String apellido1 = "";
		String apellido2 = "";
		String razSocial = "";
		String via = "";
		String domicilio = "";
		String numero = "";
		String piso = "";
		String bloque = "";
		String escalera = "";
		String provincia = "";
		String localidad = "";
		String sublocalidad = "";
		String codPostal = "";
		String telefono = "";
		String movil = "";
		String email = "";
		String numSS = "";
		String regimen = "";
		String ATP = "";
		String jovenAgr = "";
		String descEntidad = "";
		String desc_via = "";
		String descProvincia = "";
		String descLocalidad = "";
		String codUsuario = "";
		String codEntMed = "";
		String codSubentMed = "";
		
		idAsegurado = as.getId().toString();
		
		if(as.getEntidad().getNomentidad() != null)
			descEntidad = StringUtils.forHTML(as.getEntidad().getNomentidad());
		
		if (as.getVia() != null) {
			if(as.getVia().getNombre() !=null)
				desc_via = StringUtils.forHTML(as.getVia().getNombre());
		}
		
		if(as.getLocalidad().getProvincia().getNomprovincia() != null)
			descProvincia = StringUtils.forHTML(as.getLocalidad().getProvincia().getNomprovincia());
		
		if(as.getLocalidad().getNomlocalidad() !=null)
			descLocalidad = StringUtils.forHTML(as.getLocalidad().getNomlocalidad().replaceAll("'", " "));
		
		if (as.getEntidad().getCodentidad() != null)
			entidad = StringUtils.forHTML(as.getEntidad().getCodentidad().toString());
		
		if (as.getTipoidentificacion() != null)
			tipoIndent = StringUtils.forHTML(as.getTipoidentificacion());
		
		if (as.getNifcif() != null)
			cifNif = StringUtils.forHTML(as.getNifcif());

		if (as.getNombre() != null)
			nombre = StringUtils.forHTML(as.getNombre());
		
		if (as.getApellido1() != null)
			apellido1 = StringUtils.forHTML(as.getApellido1());
		
		if (as.getApellido2() != null)
			apellido2 = StringUtils.forHTML(as.getApellido2());
		
		if (as.getRazonsocial() != null)
			razSocial = StringUtils.forHTML(as.getRazonsocial());
		
		if (as.getVia() != null)
		{
			if (as.getVia().getClave() != null)
				via = StringUtils.forHTML(as.getVia().getClave());			
		}
		
		if (as.getDireccion() != null)
			domicilio =  StringUtils.forHTML(as.getDireccion());
		
		if (as.getNumvia() != null)
			numero = StringUtils.forHTML(as.getNumvia());
		
		if (as.getPiso() != null)
			piso = StringUtils.forHTML(as.getPiso());
		
		if (as.getBloque() != null)
			bloque = StringUtils.forHTML(as.getBloque());
		
		if (as.getEscalera() != null)
			escalera = StringUtils.forHTML(as.getEscalera());
		
		if (as.getLocalidad() != null)
		{
			if (as.getLocalidad().getId().getCodprovincia() != null)
				provincia = StringUtils.forHTML(as.getLocalidad().getId().getCodprovincia().toString());
			if (as.getLocalidad().getId().getCodlocalidad() != null)
				localidad = StringUtils.forHTML(as.getLocalidad().getId().getCodlocalidad().toString());
			if (as.getLocalidad().getId().getSublocalidad() != null)
				sublocalidad = StringUtils.forHTML(as.getLocalidad().getId().getSublocalidad()); 
		}
		
		if (as.getCodpostal() != null)
			codPostal = StringUtils.forHTML(as.getCodpostalstr().toString());

		if (as.getTelefono() != null)
			telefono = StringUtils.forHTML(as.getTelefono());
		
		if (as.getMovil() != null)
			movil = StringUtils.forHTML(as.getMovil());
		
		if (as.getEmail() != null)
			email = StringUtils.forHTML(as.getEmail());
		
		if (as.getNumsegsocial() != null)
			numSS = StringUtils.forHTML(as.getNumsegsocial());
		
		if (as.getRegimensegsocial() != null)
			regimen = StringUtils.forHTML(as.getRegimensegsocial().toString());
		
		if (as.getAtp() != null)
			ATP = StringUtils.forHTML(as.getAtp());
		
		if (as.getJovenagricultor() != null)
			jovenAgr = StringUtils.forHTML(as.getJovenagricultor().toString());
		
		if (as.getUsuario() != null) {
			codUsuario = as.getUsuario().getCodusuario();
			if (as.getUsuario().getSubentidadMediadora() != null) {
				codEntMed = StringUtils.forHTML(as.getUsuario().getSubentidadMediadora().getId().getCodentidad().toString());
				codSubentMed = StringUtils.forHTML(as.getUsuario().getSubentidadMediadora().getId().getCodsubentidad().toString());
			}
		}
		String fechaRev="";
		if (as.getFechaRevision()!= null){
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				fechaRev = sdf.format(as.getFechaRevision());
			} catch (Exception e) {
				logger.error("Excepcion : ModelTableDecoratorAsegurados - getAseguradoSelec", e);
			}
		}

		cadenaDev.append("<input type='checkbox' id='checkAsegurado_").append(idAsegurado+":"+cifNif).append("' value='").append(idAsegurado+":"+cifNif).append("' onclick=\"onClickInCheck2('").append(idAsegurado+":"+cifNif).append("');\">");
		
		cadenaDev.append("<a href=\"javascript:modificar('").append(idAsegurado).append(COMA).append(entidad).append(COMA)
			.append(tipoIndent).append(COMA).append(cifNif).append(COMA).append(nombre).append(COMA).append(apellido1)
			.append(COMA).append(apellido2).append(COMA).append(razSocial).append(COMA).append(via).append(COMA).append(domicilio)
			.append(COMA).append(numero).append(COMA).append(piso).append(COMA).append(bloque).append(COMA).append(escalera)
			.append(COMA).append(provincia).append(COMA).append(localidad).append(COMA).append(sublocalidad).append(COMA)
			.append(codPostal).append(COMA).append(telefono).append(COMA).append(movil).append(COMA).append(email).append(COMA)
			.append(numSS).append(COMA).append(regimen).append(COMA).append(ATP).append(COMA).append(jovenAgr).append(COMA)
			.append(descEntidad).append(COMA).append(desc_via).append(COMA).append(descProvincia).append(COMA)
			.append(descLocalidad).append(COMA).append(codUsuario).append("', '").append(codEntMed).append("', '").append(codSubentMed)
			.append("', '").append(fechaRev).append("')\">");
		cadenaDev.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/></a>");
		
		cadenaDev.append("<a href=\"javascript:eliminar('").append(idAsegurado).append("')\">");
		cadenaDev.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar\" title=\"Borrar\"/></a>");
		cadenaDev.append(" ");
		cadenaDev.append("<a href=\"javascript:getDatosAseguradoWS('").append(idAsegurado).append(COMA).append(cifNif).append("')\">");
		cadenaDev.append("<img src=\"jsp/img/jmesa/clear.gif\" alt=\"Actualizar\" title=\"Actualizar\"/></a>");
		cadenaDev.append(" ");
		cadenaDev.append("<a href=\"#\" class=\"abrir-pop-fecha-estudio\"  onclick=\"abrirPopUpFechaEstudio('").append(cifNif).append("');\">");
		cadenaDev.append("<img src=\"jsp/img/displaytag/detalle_asegurado.png\" alt=\"Detalle Asegurado\" title=\"Detalle Asegurado\"/></a>&nbsp;");
		return cadenaDev.toString();    	
	}
	public String getAseguradoSelecPerfil0 () {
		StringBuilder cadenaDev = new StringBuilder();
		Asegurado as = (Asegurado)getCurrentRowObject();
		cadenaDev.append(getAseguradoSelec());
		
		//Añadimos el desbloquear usuario a parte de los demas botones
		if (as.getUsuarios()!=null && !as.getUsuarios().isEmpty()){
			String usuCarga="";
			for (Usuario usu : as.getUsuarios()){
				usuCarga = usu.getCodusuario();
			}
			cadenaDev.append("<a href=\"javascript:desbloquearUsuario('").append(usuCarga)
				.append("')\"><img src=\"jsp/img/desbloquear.jpg\" alt=\"Desbloqear asegurado\" title=\"Desbloquear asegurado\"/></a>");
		}
		
		return cadenaDev.toString();   
	}
	public String getEntSubEnt(){
		Asegurado as = (Asegurado)getCurrentRowObject();
		
		if (as.getUsuario().getSubentidadMediadora() != null) {
			return StringUtils.forHTML(as.getUsuario().getSubentidadMediadora().getId().getCodentidad().toString())
				+ "-" + StringUtils.forHTML(as.getUsuario().getSubentidadMediadora().getId().getCodsubentidad().toString());
		}else{
			return "";
		}
	}
	
	public String getCargaAseguradoSelec () {
		Asegurado as = (Asegurado)getCurrentRowObject();
		StringBuilder cadenaDev = new StringBuilder();
		
		String idAsegurado = "";
		String entidad = "";
		String tipoIndent = "";
		String cifNif ="";
		//String discriminante = "";
		String nombre = "";
		String apellido1 = "";
		String apellido2 = "";
		String razSocial = "";
		String via = "";
		String domicilio = "";
		String numero = "";
		String piso = "";
		String bloque = "";
		String escalera = "";
		String provincia = "";
		String localidad = "";
		String sublocalidad = "";
		String codPostal = "";
		String telefono = "";
		String movil = "";
		String email = "";
		String numSS = "";
		String regimen = "";
		String ATP = "";
		String jovenAgr = "";
		String descEntidad = "";
		String descVia = "";
		String descProvincia = "";
		String descLocalidad = "";
		String codEntMed = "";
		String codSubentMed = "";
		//String fechaRevision = "";
		String codUsuario="";
		
		idAsegurado = as.getId().toString();
		
		if(as.getEntidad().getNomentidad() != null)
			descEntidad = StringUtils.forHTML(as.getEntidad().getNomentidad());
		
		if (as.getVia() != null) {
			if(as.getVia().getNombre() !=null)
				descVia = StringUtils.forHTML(as.getVia().getNombre());
		}
		if(as.getLocalidad().getProvincia().getNomprovincia() != null)
			descProvincia = StringUtils.forHTML(as.getLocalidad().getProvincia().getNomprovincia());
		
		if(as.getLocalidad().getNomlocalidad() !=null)
			descLocalidad = StringUtils.forHTML(as.getLocalidad().getNomlocalidad().replaceAll("'", " "));
		
		if (as.getEntidad().getCodentidad() != null)
			entidad = StringUtils.forHTML(as.getEntidad().getCodentidad().toString());
		
		if (as.getTipoidentificacion() != null)
			tipoIndent = StringUtils.forHTML(as.getTipoidentificacion());
		
		if (as.getNifcif() != null)
			cifNif = StringUtils.forHTML(as.getNifcif());
		
//		if (as.getDiscriminante() != null)
//			discriminante = as.getDiscriminante();
		
		if (as.getNombre() != null)
			nombre = StringUtils.forHTML(as.getNombre());
		
		if (as.getApellido1() != null)
			apellido1 = StringUtils.forHTML(as.getApellido1());
		
		if (as.getApellido2() != null)
			apellido2 = StringUtils.forHTML(as.getApellido2());
		
		if (as.getRazonsocial() != null)
			razSocial = StringUtils.forHTML(as.getRazonsocial());
		
		if (as.getVia() != null)
		{
			if (as.getVia().getClave() != null)
				via = StringUtils.forHTML(as.getVia().getClave());			
		}
		
		if (as.getDireccion() != null)
			domicilio =  StringUtils.forHTML(as.getDireccion());
		
		if (as.getNumvia() != null)
			numero = StringUtils.forHTML(as.getNumvia());
		
		if (as.getPiso() != null)
			piso = StringUtils.forHTML(as.getPiso());
		
		if (as.getBloque() != null)
			bloque = StringUtils.forHTML(as.getBloque());
		
		if (as.getEscalera() != null)
			escalera = StringUtils.forHTML(as.getEscalera());
		
		if (as.getLocalidad() != null)
		{
			if (as.getLocalidad().getId().getCodprovincia() != null)
				provincia = StringUtils.forHTML(as.getLocalidad().getId().getCodprovincia().toString());
			if (as.getLocalidad().getId().getCodlocalidad() != null)
				localidad = StringUtils.forHTML(as.getLocalidad().getId().getCodlocalidad().toString());
			if (as.getLocalidad().getId().getSublocalidad() != null)
				sublocalidad = StringUtils.forHTML(as.getLocalidad().getId().getSublocalidad()); 
		}
		
		if (as.getCodpostal() != null)
			codPostal = StringUtils.forHTML(as.getCodpostal().toString());

		if (as.getTelefono() != null)
			telefono = StringUtils.forHTML(as.getTelefono());
		
		if (as.getMovil() != null)
			movil = StringUtils.forHTML(as.getMovil());
		
		if (as.getEmail() != null)
			email = StringUtils.forHTML(as.getEmail());
		
		if (as.getNumsegsocial() != null)
			numSS = StringUtils.forHTML(as.getNumsegsocial());
		
		if (as.getRegimensegsocial() != null)
			regimen = StringUtils.forHTML(as.getRegimensegsocial().toString());
		
		if (as.getAtp() != null)
			ATP = StringUtils.forHTML(as.getAtp());
		
		if (as.getJovenagricultor() != null)
			jovenAgr = StringUtils.forHTML(as.getJovenagricultor().toString());
		
		if (as.getUsuario() != null) {
			if (as.getUsuario().getSubentidadMediadora() != null) {
				codEntMed = StringUtils.forHTML(as.getUsuario().getSubentidadMediadora().getId().getCodentidad().toString());
				codSubentMed = StringUtils.forHTML(as.getUsuario().getSubentidadMediadora().getId().getCodsubentidad().toString());
			}
		}
		String fechaRev="";
		if (as.getFechaRevision()!= null){
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				fechaRev = sdf.format(as.getFechaRevision());
			} catch (Exception e) {
				logger.error("Excepcion : ModelTableDecoratorAsegurados - getCargaAseguradoSelec", e);
			}
		}
			
		
		if (as.getUsuario() != null) {
			codUsuario = as.getUsuario().getCodusuario();
			if (as.getUsuario().getSubentidadMediadora() != null) {
				codEntMed = StringUtils.forHTML(as.getUsuario().getSubentidadMediadora().getId().getCodentidad().toString());
				codSubentMed = StringUtils.forHTML(as.getUsuario().getSubentidadMediadora().getId().getCodsubentidad().toString());
			}
		}
		
		
		
		cadenaDev.append("<a href=\"javascript:modificar('").append(idAsegurado).append(COMA).append(entidad).append(COMA)
			.append(tipoIndent).append(COMA).append(cifNif).append(COMA).append(nombre).append(COMA).append(apellido1)
			.append(COMA).append(apellido2).append(COMA).append(razSocial).append(COMA).append(via).append(COMA)
			.append(domicilio).append(COMA).append(numero).append(COMA).append(piso).append(COMA).append(bloque)
			.append(COMA).append(escalera).append(COMA).append(provincia).append(COMA).append(localidad).append(COMA)
			.append(sublocalidad).append(COMA).append(codPostal).append(COMA).append(telefono).append(COMA).append(movil)
			.append(COMA).append(email).append(COMA).append(numSS).append(COMA).append(regimen).append(COMA)
			.append(ATP).append(COMA).append(jovenAgr).append(COMA).append(descEntidad).append(COMA).append(descVia)
			.append(COMA).append(descProvincia).append(COMA).append(descLocalidad).append(COMA).append(codUsuario)
			.append("', '").append(codEntMed).append("', '").append(codSubentMed).append("', '").append(fechaRev).append("')\"> ");
		cadenaDev.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/></a>&nbsp;");
		
		cadenaDev.append("<a href=\"javascript:cargarAsegurado('").append(as.getId()).append("')\">");
		cadenaDev.append("<img src=\"jsp/img/displaytag/load.png\" alt=\"Cargar asegurado\" title=\"Cargar asegurado\"/></a>");
		cadenaDev.append(" ");
		cadenaDev.append("<a href=\"javascript:getDatosAseguradoWS('").append(idAsegurado).append(COMA).append(cifNif).append("')\">");
		cadenaDev.append("<img src=\"jsp/img/jmesa/clear.gif\" alt=\"Actualizar\" title=\"Actualizar\"/></a>");
		cadenaDev.append("<a href=\"javascript:getDatosAseguradoWS('").append(idAsegurado).append(COMA).append(cifNif).append("')\">");
		cadenaDev.append("<a href=\"#\" class=\"abrir-pop-fecha-estudio\"  onclick=\"llamadaAjaxDatosAsegurado('', '").append(cifNif).append("', 'carga');\">");
		cadenaDev.append("<img src=\"jsp/img/displaytag/detalle_asegurado.png\" alt=\"Detalle Asegurado\" title=\"Detalle Asegurado\"/></a>&nbsp;");
		
		return cadenaDev.toString();    	
	}
	
	public String getAseguradoEntidad () 
	{
		Asegurado as = (Asegurado)getCurrentRowObject();
		String cadena = "";
		if (as.getEntidad().getCodentidad() != null)
			cadena = as.getEntidad().getCodentidad().toString();
		
		return cadena;
	}
	
	public String getAseguradoCif () 
	{
		Asegurado as = (Asegurado)getCurrentRowObject();
		String cadena = "";
		if (as.getNifcif() != null)
			cadena = as.getNifcif();
		
		return cadena;
	}
	
	public String getAseguradoNombre ()
	{
		Asegurado as = (Asegurado)getCurrentRowObject();
		String cadena = "";
		if (as.getTipoidentificacion().equalsIgnoreCase("CIF"))
		{
			cadena = as.getRazonsocial();
		}
		else
		{
			if(as.getApellido2() == null){
				cadena = as.getNombre()+" "+as.getApellido1();
			}else{
				cadena = as.getNombre()+" "+as.getApellido1()+" "+as.getApellido2();
			}
		}
		
		return cadena;
	}
	
	public String getAsegProv () 
	{
		Asegurado as = (Asegurado)getCurrentRowObject();
		String cadena = "";
		if (as.getLocalidad() != null)
		{
			if (as.getLocalidad().getId() != null)
			{
				if (as.getLocalidad().getId().getCodprovincia() != null)
					cadena = as.getLocalidad().getId().getCodprovincia().toString();
			}
		}		
		return cadena;
	}
	
	public String getAsegLocalidad ()
	{
		Asegurado as = (Asegurado)getCurrentRowObject();
		String cadena = "";
		if (as.getLocalidad() != null)
		{
			if (as.getLocalidad().getId() != null)
			{
				if (as.getLocalidad().getId().getCodlocalidad() != null)
					cadena = as.getLocalidad().getId().getCodlocalidad().toString();
			}
		}		
		return cadena;
	}
	
	public String getAsegCodPostal ()
	{
		Asegurado as = (Asegurado)getCurrentRowObject();
		String cadena = "";
		if (as.getCodpostal() != null)
			cadena = as.getCodpostalstr().toString();
		
		return cadena;
	}
	
	public String getAsegTelefono ()
	{
		Asegurado as = (Asegurado)getCurrentRowObject();
		String cadena = "";
		if (as.getTelefono() != null)
			cadena = as.getTelefono();
		
		return cadena;
	}
		
}
