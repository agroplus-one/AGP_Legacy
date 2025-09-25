package com.rsi.agp.core.util;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;
import com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital.PolizaReduccionCapital;
import com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital.ObjetosAsegurados;
import com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital.ParcelaReducida;
import com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital.CapitalesAsegurados;
/**
 * Clase para transformar una poliza de base de datos en una poliza para enviar a Agroseguro
 * @author U028783
 *
 */
public class ReduccionCapitalTransformer {
	
	private static Log logger = LogFactory.getLog(ReduccionCapitalTransformer.class);
	
	public static PolizaReduccionCapital transformar(ReduccionCapital redCap) throws Exception{
		//Poliza polizaAS = Poliza.Factory.newInstance();
		PolizaReduccionCapital polizaRedCap = new PolizaReduccionCapital ();
		

		//Rellenamos el anexo con los datos de la poliza
		polizaRedCap.setPlan(redCap.getPoliza().getLinea().getCodplan().intValue());
		polizaRedCap.setReferencia(redCap.getPoliza().getReferencia());
		polizaRedCap.setModulo(String.format("%-5s", redCap.getPoliza().getCodmodulo().trim()));
		
		//MOTIVO
		GregorianCalendar gc = new GregorianCalendar();	
		gc.setTime(redCap.getFechadanios());
		XMLGregorianCalendar xmlFechaDanios = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(gc);
		
		es.agroseguro.tipos.MotivoReduccionCapital motivo = new es.agroseguro.tipos.MotivoReduccionCapital();
		motivo.setCausaDanio(redCap.getCodmotivoriesgo());
		motivo.setFechaOcurrencia(xmlFechaDanios);
		polizaRedCap.setMotivo(motivo);
		
		//INICIO DATOS QUE PUEDEN SER MODIFICADOS EN EL ANEXO
		
		//OBJETOS ASEGURADOS
		if (redCap.getParcelas() != null && redCap.getParcelas().size() > 0){
			ObjetosAsegurados objAseg = new ObjetosAsegurados();
			List<ParcelaReducida> parcelasArr = getParcelasArray(redCap);
			if (parcelasArr != null){
				objAseg.setParcela(parcelasArr);
				polizaRedCap.setObjetosAsegurados(objAseg);
			}
		}
		//FIN OBJETOS ASEGURADOS

		return polizaRedCap;
	}
	

	
	/**
	 * Metodo para obtener una lista de parcelas enviar a Agroseguro a partir de una coleccion de parcelas de poliza
	 * @param redCap Poliza de la aplicacion
	 */
	private static List<ParcelaReducida> getParcelasArray(ReduccionCapital redCap) throws Exception {
		logger.debug("getParcelasArray INICIO");
		List<ParcelaReducida> lstParcelas = new ArrayList<ParcelaReducida>();
		
		for (com.rsi.agp.dao.tables.reduccionCap.Parcela parcela : redCap.getParcelas()){
			
			ParcelaReducida parcRed = new ParcelaReducida();
			List<com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital.CapitalAsegurado> parcRed_CapAsegList = new ArrayList<com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital.CapitalAsegurado>(); 
			
			for(com.rsi.agp.dao.tables.reduccionCap.CapitalAsegurado capAseg : parcela.getCapitalAsegurados()) {
				if(null != capAseg.getProdred()) {
					
					com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital.CapitalAsegurado parcRed_CapAseg = new com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital.CapitalAsegurado();
					parcRed_CapAseg.setProduccionTrasReduccion(capAseg.getProdred().intValue());
					parcRed_CapAseg.setTipo(capAseg.getCodtipocapital().intValue());
					parcRed_CapAsegList.add(parcRed_CapAseg);
				}
			}
			if(!parcRed_CapAsegList.isEmpty()) {
				CapitalesAsegurados capAsegurados = new CapitalesAsegurados();
				capAsegurados.setCapitalAsegurado(parcRed_CapAsegList);
				parcRed.setCapitalesAsegurados(capAsegurados);
				parcRed.setHoja(parcela.getHoja().intValue());
				parcRed.setNumero(parcela.getNumero().intValue());
				lstParcelas.add(parcRed);
			}
			
		
		}
		logger.debug("getParcelasArray FIN");
		
		return lstParcelas;
	}
}