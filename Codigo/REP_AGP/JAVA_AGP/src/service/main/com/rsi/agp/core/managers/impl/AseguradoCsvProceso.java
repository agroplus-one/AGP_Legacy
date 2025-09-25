package com.rsi.agp.core.managers.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.csvreader.CsvReader;
import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.tables.admin.Asegurado;

public class AseguradoCsvProceso {

	
	
//	public Map<String, String>  importarCSV(MultipartFile file, HttpServletRequest request)throws Exception{
//		Map<String, String> resFichero = new HashMap<String, String>();
//		int contadorRegistros=0;
//		int contadorRegistrosKO=0;
//		int contadorRegistrosOK=0;
//		String validacion=null;
//		String nifCif=null;
//		CsvReader csv = new CsvReader("****",';'); 
//		
//		while (csv.readRecord()){	
//			Map<String, String> resRegistro = new HashMap<String, String>();
//			contadorRegistros+=1;
//			AseguradoCsvRegistro regAdeg = new AseguradoCsvRegistro(csv); 
//			resRegistro=validaCampos(regAdeg);
//			if(regAdeg.getEsRegistroValido()){//Sin errores
//				//Convertimos en objeto Asegurado e intentamos grabar
//				Asegurado aseg = new Asegurado(regAdeg);
//			}else{//Con errores
//				contadorRegistrosKO+=1;
//				resFichero.putAll(resRegistro);
//			}				
//		}
//		
//		
//		return resFichero;
//		
//	}
//	
//	private Map<String, String> validaCampos(AseguradoCsvRegistro regAseg){
//		Map<String, String> resRegistro = new HashMap<String, String>();
//		
//		//NIF/CIF/NIE: cadena de caracteres de 9 posiciones. Obligatorio.
//		resRegistro.putAll(regAseg.isValidCampo(regAseg.getIdentificacion(),"NIF/CIF/NIE", true, false,9,0, null));
//		
//		//Entidad: numérico de 4 posiciones. Obligatorio.
//		resRegistro.putAll(regAseg.isValidCampo(regAseg.getEntidad(), "Entidad", true, true, 4,0, null));
//		
//		//Tipo identificación: se podrán indicar los valores NIF, CIF o NIE. Obligatorio.
//		resRegistro.putAll(regAseg.isValidCampo(regAseg.getTipoIdentificacion(), "Tipo identificacion",true,false,0, 0,"NIF;CIF;NIE"));
//		
//		//Nombre: cadena de caracteres de 20 posiciones. Obligatorio si tipo identificación es NIF o NIE.
//		if(regAseg.getTipoIdentificacion().compareTo("NIF")==0 || regAseg.getTipoIdentificacion().compareTo("NIE")==0){
//			resRegistro.putAll(regAseg.isValidCampo(regAseg.getNombre(),"Nombre", true, false,0, 20, null));
//			resRegistro.putAll(regAseg.isValidCampo(regAseg.getApellido1(),"Primer apellido", true, false,0, 40, null));
//		}else{
//			resRegistro.putAll(regAseg.isValidCampo(regAseg.getNombre(),"Nombre", false, false,0, 20, null));
//			resRegistro.putAll(regAseg.isValidCampo(regAseg.getApellido1(),"Primer apellido", false, false,0, 40, null));
//		}
//		
//		//Segundo apellido: cadena de caracteres de 40 posiciones.
//		resRegistro.putAll(regAseg.isValidCampo(regAseg.getApellido2(),"Segundo apellido", false, false,0, 40, null));
//		
//		//Razón social: cadena de caracteres de 50 posiciones. Obligatorio si tipo identificación es CIF.
//		if(regAseg.getTipoIdentificacion().compareTo("CIF")==0){
//			resRegistro.putAll(regAseg.isValidCampo(regAseg.getRazonSocial(),"Razón social:", true, false,0, 50, null));
//		}else{
//			resRegistro.putAll(regAseg.isValidCampo(regAseg.getRazonSocial(),"Razón social:", false, false,0, 50, null));
//		}
//	
//		//Usuario: cadena de caracteres de 8 posiciones. Obligatorio.
//		resRegistro.putAll(regAseg.isValidCampo(regAseg.getUsuario(), "Usuario", true, false, 8,0, null));
//		
//		//Tipo de vía: cadena de caracteres de 2 posiciones. Obligatorio.
//		resRegistro.putAll(regAseg.isValidCampo(regAseg.getTipoVia(), "Tipo de vía", true, false, 2,0, null));
//			
//		//Domicilio: cadena de caracteres de 200 posiciones. Obligatorio.
//		resRegistro.putAll(regAseg.isValidCampo(regAseg.getDomicilio(), "Domicilio", true, false, 0,200, null));
//		
//		
//		//Número: cadena de caracteres de 5 posiciones. Obligatorio.
//		resRegistro.putAll(regAseg.isValidCampo(regAseg.getNumero(), "Número", true, false, 0,5, null));		
//		
//		//Piso: cadena de caracteres de 12 posiciones.
//		resRegistro.putAll(regAseg.isValidCampo(regAseg.getPiso(), "Piso", false, false, 0,12, null));
//	
//		
//		//Bloque: cadena de caracteres de 10 posiciones.
//		resRegistro.putAll(regAseg.isValidCampo(regAseg.getBloque(), "Bloque", false, false, 0,10, null));
//	
//		
//		//Escalera: cadena de caracteres de 10 posiciones.
//		resRegistro.putAll(regAseg.isValidCampo(regAseg.getEscalera(), "Escalera", false, false, 0, 10, null));
//	
//		
//		//Código de provincia: numérico de 2 posiciones. Obligatorio.
//		resRegistro.putAll(regAseg.isValidCampo(regAseg.getCodProvincia(), "Provincia", true, true, 2, 0, null));
//		
//		
//		//Código de localidad: numérico de 3 posiciones. Obligatorio.
//		resRegistro.putAll(regAseg.isValidCampo(regAseg.getCodLocalidad(), "Localidad", true, true, 3, 0, null));
//	
//		
//		//Sublocalidad: cadena de caracteres de 1 posición. Obligatorio.
//		resRegistro.putAll(regAseg.isValidCampo(regAseg.getCosSublocalidad(), "Sublocalidad", true, true, 1, 0, null));
//		
//		
//		//Código postal: cadena de caracteres de 5 posiciones. Obligatorio.
//		resRegistro.putAll(regAseg.isValidCampo(regAseg.getCodPostal(), "Código postal", true, true, 5, 0, null));
//	
//		
//		//Teléfono fijo: cadena de caracteres de 9 posiciones. Obligatorio.
//		resRegistro.putAll(regAseg.isValidCampo(regAseg.getTelefonoFijo(), "Teléfono fijo", true, true, 9, 0, null));
//
//		
//		//Teléfono móvil: cadena de caracteres de 9 posiciones.
//		resRegistro.putAll(regAseg.isValidCampo(regAseg.getTelefonoMovil(), "Teléfono móvil", false, true, 9, 0, null));
//		
//		//e-mail: cadena de caracteres de 50 posiciones.
//		resRegistro.putAll(regAseg.isValidCampo(regAseg.getEmail(), "e-mail", false, false, 0, 50, null));
//	
//		//Número de la seguridad social: cadena de caracteres de 12 posiciones.
//		resRegistro.putAll(regAseg.isValidCampo(regAseg.getNumSeguridadSoc(), "Número de la seguridad social", false, true, 12, 0, null));
//
//		//Indicador de régimen: se permitirá elegir entre los valores 0, 1 ó 2: 'Autónomo', 'Rea cuenta Ajena' o 'Rea cuenta propia' respectivamente.
//		resRegistro.putAll(regAseg.isValidCampo(regAseg.getRegSeguridadSoc(), "Indicador de régimen", false, true, 1, 0, "0;1;2"));
//
//		//ATP: se permitirá elegir entre los valores 'SI' y 'NO'. Obligatorio.
//		resRegistro.putAll(regAseg.isValidCampo(regAseg.getAtp(), "ATP", true, false, 2, 0, "SI;NO"));
//	
//		
//		//Joven Agricultor: se permitirá elegir entre los valores 'SI' y 'NO'. Obligatorio.
//		resRegistro.putAll(regAseg.isValidCampo(regAseg.getJovenAgricultor(), "Joven Agricultor", true, false, 2, 0, "SI;NO"));
//
//		//IBAN: cadena de caracteres de 24 posiciones.
//		resRegistro.putAll(regAseg.isValidCampo(regAseg.getIban(), "IBAN", false, false, 24, 0, null));			
//
//		
//		return resRegistro;
//	}
	
	
	
}
