package com.rsi.agp.core.util;

import com.rsi.agp.dao.tables.admin.SubentidadMediadora;

import es.agroseguro.iTipos.NombreApellidos;
import es.agroseguro.iTipos.RazonSocial;
import es.agroseguro.seguroAgrario.calculoSeguroAgrario.Mediador;


public class TransformerUtil {
	
	public static void setAttrNombreRSMediador(SubentidadMediadora subentidad, Mediador mediador) {
		if (subentidad.getTipoMediador().getId().equals(Constants.TIPO_MEDIADOR_OPERADOR_BANCA_SEGURO) ||
				subentidad.getTipoMediador().getId().equals(Constants.TIPO_MEDIADOR_COLABORADOR_EXTERNO)) {
			
			RazonSocial rs = RazonSocial.Factory.newInstance();
			rs.setRazonSocial(Constants.RAZON_SOCIAL_RGA_MEDIACION);
			mediador.setRazonSocial(rs);
			
		} else if (subentidad.getTipoidentificacion().equals("CIF")){
			RazonSocial rs = RazonSocial.Factory.newInstance();
			rs.setRazonSocial(subentidad.getNomsubentidad());
			mediador.setRazonSocial(rs);
		}
		else{
			
			NombreApellidos nom = NombreApellidos.Factory.newInstance();
			nom.setNombre(subentidad.getNombre());
			nom.setApellido1(subentidad.getApellido1());
			nom.setApellido2(subentidad.getApellido2());
			mediador.setNombreApellidos(nom);
			
		}
	}
	
	public static void setAttrNombreRSMediadorCpl(SubentidadMediadora subentidad, es.agroseguro.seguroAgrario.contratacion.complementario.Mediador mediador) {
		if (subentidad.getTipoMediador().getId().equals(Constants.TIPO_MEDIADOR_OPERADOR_BANCA_SEGURO) ||
				subentidad.getTipoMediador().getId().equals(Constants.TIPO_MEDIADOR_COLABORADOR_EXTERNO)) {
			
			RazonSocial rs = RazonSocial.Factory.newInstance();
			rs.setRazonSocial(Constants.RAZON_SOCIAL_RGA_MEDIACION);
			mediador.setRazonSocial(rs);
			
		} else if (subentidad.getTipoidentificacion().equals("CIF")){
			RazonSocial rs = RazonSocial.Factory.newInstance();
			rs.setRazonSocial(subentidad.getNomsubentidad());
			mediador.setRazonSocial(rs);
		}
		else{
			
			NombreApellidos nom = NombreApellidos.Factory.newInstance();
			nom.setNombre(subentidad.getNombre());
			nom.setApellido1(subentidad.getApellido1());
			nom.setApellido2(subentidad.getApellido2());
			mediador.setNombreApellidos(nom);
			
		}
	}
	
	public static void setAttrNombreRSMediadorAnexo(SubentidadMediadora subentidad, es.agroseguro.seguroAgrario.modificacion.Mediador mediador) {
		if (subentidad.getTipoMediador().getId().equals(Constants.TIPO_MEDIADOR_OPERADOR_BANCA_SEGURO) ||
				subentidad.getTipoMediador().getId().equals(Constants.TIPO_MEDIADOR_COLABORADOR_EXTERNO)) {
			
			RazonSocial rs = RazonSocial.Factory.newInstance();
			rs.setRazonSocial(Constants.RAZON_SOCIAL_RGA_MEDIACION);
			mediador.setRazonSocial(rs);
			
		} else if (subentidad.getTipoidentificacion().equals("CIF")){
			RazonSocial rs = RazonSocial.Factory.newInstance();
			rs.setRazonSocial(subentidad.getNomsubentidad());
			mediador.setRazonSocial(rs);
		}
		else{
			
			 
			NombreApellidos nom = NombreApellidos.Factory.newInstance();
			nom.setNombre(subentidad.getNombre());
			nom.setApellido1(subentidad.getApellido1());
			nom.setApellido2(subentidad.getApellido2());
			mediador.setNombreApellidos(nom);
			
		}
	}
	
	public static void setAttrTipoMediador(SubentidadMediadora subentidad, Mediador mediador) {
		try {
			if (subentidad != null && subentidad.getTipoMediadorAgro() != null && subentidad.getTipoMediadorAgro().getId() != null){
				mediador.setTipo(subentidad.getTipoMediadorAgro().getId().intValue());
			}
		} catch (Exception e) {
			mediador.setTipo(1);
		}
	}
	
	public static void setAttrTipoMediadorCpl(SubentidadMediadora subentidad, es.agroseguro.seguroAgrario.contratacion.complementario.Mediador mediador) {
		try {
			if (subentidad != null && subentidad.getTipoMediadorAgro() != null && subentidad.getTipoMediadorAgro().getId() != null){
				mediador.setTipo(subentidad.getTipoMediadorAgro().getId().intValue());
			}
		} catch (Exception e) {
			mediador.setTipo(1);
		}
	}
	
	public static void setAttrTipoMediadorAnexo(SubentidadMediadora subentidad, es.agroseguro.seguroAgrario.modificacion.Mediador mediador) {
		try {
			if (subentidad != null && subentidad.getTipoMediadorAgro() != null && subentidad.getTipoMediadorAgro().getId() != null){
				mediador.setTipo(subentidad.getTipoMediadorAgro().getId().intValue());
			}
		} catch (Exception e) {
			mediador.setTipo(1);
		}
	}
	
	/* Pet. 57626 ** MODIF TAM (16.06.2020) ** Inicio */
	public static void setAttrTipoMediadorAnexoUnif(SubentidadMediadora subentidad, es.agroseguro.contratacion.Mediador mediador) {
		try {
			if (subentidad != null && subentidad.getTipoMediadorAgro() != null && subentidad.getTipoMediadorAgro().getId() != null){
				mediador.setTipo(subentidad.getTipoMediadorAgro().getId().intValue());
			}
		} catch (Exception e) {
			mediador.setTipo(1);
		}
	}
	public static void setAttrNombreRSMediadorAnexoUnif(SubentidadMediadora subentidad, es.agroseguro.contratacion.Mediador mediador) {
		if (subentidad.getTipoMediador().getId().equals(Constants.TIPO_MEDIADOR_OPERADOR_BANCA_SEGURO) ||
				subentidad.getTipoMediador().getId().equals(Constants.TIPO_MEDIADOR_COLABORADOR_EXTERNO)) {
			
			RazonSocial rs = RazonSocial.Factory.newInstance();
			rs.setRazonSocial(Constants.RAZON_SOCIAL_RGA_MEDIACION);
			mediador.setRazonSocial(rs);
			
		} else if (subentidad.getTipoidentificacion().equals("CIF")){
			RazonSocial rs = RazonSocial.Factory.newInstance();
			rs.setRazonSocial(subentidad.getNomsubentidad());
			mediador.setRazonSocial(rs);
		}
		else{
			 
			NombreApellidos nom = NombreApellidos.Factory.newInstance();
			nom.setNombre(subentidad.getNombre());
			nom.setApellido1(subentidad.getApellido1());
			nom.setApellido2(subentidad.getApellido2());
			mediador.setNombreApellidos(nom);
			
		}
	}
	/* Pet. 57626 ** MODIF TAM (16.06.2020) ** Fin */
	
}
