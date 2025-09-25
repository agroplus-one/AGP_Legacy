package com.rsi.agp.core.managers.impl.ComisionesUnificadas;

import java.sql.Blob;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlObject;

import com.rsi.agp.dao.tables.comisiones.unificado.AplicacionUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.ColectivoUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.FaseUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.GrupoNegocioUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.IndividualUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.ReciboUnificado;
import com.rsi.agp.dao.tables.commons.Usuario;

public class FicheroUnificadoGastosRecibosImpagadosManager extends FicheroUnificadoGastosManager{
	private static final Log LOGGER = LogFactory.getLog(FicheroUnificadoGastosRecibosImpagadosManager.class);
	public FicheroUnificado getFicheroUnificado(XmlObject xml,
			Usuario usuario, Character tipoFichero, String nombreFichero, Blob blob) throws Exception {
			//es.agroseguro.recibos.gastosRecibosImpagados.FaseDocument fase, 	
		LOGGER.debug("init - getFicheroUnificado");
		FicheroUnificado file=null;
		try {
			//Datos generales del fichero
			file=new FicheroUnificado();
			this.llenaDatosGeneralesFichero(file, usuario.getCodusuario(), tipoFichero, nombreFichero, new Date());
			//----------------------------
			es.agroseguro.recibos.gastosRecibosImpagados.FaseDocument fase =(es.agroseguro.recibos.gastosRecibosImpagados.FaseDocument)xml;
			FaseUnificado faseUnificado=this.getFaseUnificado(fase.getFase().getFase(), fase.getFase().getFechaFaseCobros().getTime(), 
					fase.getFase().getEjercicioPago(), file);
			
			for(es.agroseguro.recibos.gastosRecibosImpagados.Recibo re:fase.getFase().getReciboArray()) {
				ReciboUnificado reciboUnificado = new ReciboUnificado();
				//Datos propios
				if(re.getLinea()!=0)reciboUnificado.setLinea(re.getLinea());
				if(re.getRecibo()!=0)reciboUnificado.setRecibo(re.getRecibo());
				if (re.getPlan()!=0)reciboUnificado.setPlan(re.getPlan());
				
				if(null!=re.getColectivo() ) {
					ColectivoUnificado colUnif=getColectivoUnificado(re.getColectivo());
					reciboUnificado.setColectivo(colUnif);
				}else {//Individual
					IndividualUnificado indUnif=this.getIndividualUnificado(re.getIndividual());
					reciboUnificado.setIndividual(indUnif);
				}
				
				//Datos de Aplicación
				AplicacionUnificado aplicacionUnificado=null;
				es.agroseguro.recibos.gastosRecibosImpagados.Aplicacion apl = re.getAplicacion();
			
				//o tiene nombre y apellidos o tiene razón social
				aplicacionUnificado=this.getAplicacionUnificado(apl.getNombreApellidos(), apl.getRazonSocial(),
						null, apl.getCodigoInterno(), apl.getDigitoControl(),
						apl.getReferencia(), apl.getTipoReferencia().toString().charAt(0), apl.getImporteSaldoPendiente(),
						apl.getImporteCobroRecibido());
				
				//Grupos de negocio					
				for(es.agroseguro.recibos.gastosRecibosImpagados.GrupoNegocio gn:apl.getGrupoNegocioArray()) {
					GrupoNegocioUnificado grupoNegocioUnificado=getGrupoNegocioUnificado(gn);
					aplicacionUnificado.getGrupoNegocios().add(grupoNegocioUnificado);
					reciboUnificado.setAplicacion(aplicacionUnificado);
					}
				reciboUnificado.setFase(faseUnificado);
				faseUnificado.getReciboUnificados().add(reciboUnificado);
				
			}
			file.getFases().add(faseUnificado);
			Long idFichero=null;
			file.setFicheroContenido(this.getFicheroContenidoUnificado(file, blob, idFichero));
			
		} catch (Exception ex) {
			LOGGER.debug("Se ha producido un error en la creacion del fichero unificado de gastos de recibos impagados", ex);
			throw new Exception("", ex);
		}
					
		return file;
	}
	
	private ColectivoUnificado getColectivoUnificado(es.agroseguro.recibos.gastosRecibosImpagados.Colectivo col) {
		
		es.agroseguro.iTipos.NombreApellidos na= col.getNombreApellidos();
		es.agroseguro.iTipos.RazonSocial rz =col.getRazonSocial();
				
		ColectivoUnificado colectivoUnificado = this.getColectivoUnificado(na, rz, col.getReferencia(), col.getCodigoInterno(), col.getDigitoControl());
		return colectivoUnificado;
		
	}
	
	private IndividualUnificado getIndividualUnificado(es.agroseguro.recibos.gastosRecibosImpagados.Individual ind) {
		es.agroseguro.iTipos.NombreApellidos na = ind.getNombreApellidos();
		es.agroseguro.iTipos.RazonSocial rz= ind.getRazonSocial();
		IndividualUnificado individualUnificado=this.getIndividualUnificado(na, rz);
		return individualUnificado;
	}
	
	private AplicacionUnificado getAplicaccionUnificada(es.agroseguro.recibos.gastosRecibosImpagados.Aplicacion apl) {
		 Character anuladaRefundida=null;
		AplicacionUnificado aplicacionUnificado=this.getAplicacionUnificado(apl.getNombreApellidos(), apl.getRazonSocial(),
				anuladaRefundida, apl.getCodigoInterno(), apl.getDigitoControl(), apl.getReferencia(), apl.getTipoReferencia().toString().charAt(0));
		
		aplicacionUnificado.setImporteCobroRecibido(apl.getImporteCobroRecibido());
		aplicacionUnificado.setImporteSaldoPdte(apl.getImporteSaldoPendiente());
		
		for(es.agroseguro.recibos.gastosRecibosImpagados.GrupoNegocio gn: apl.getGrupoNegocioArray()) {
			GrupoNegocioUnificado grupoNegocioUnif=getGrupoNegocioUnificado(gn);
			aplicacionUnificado.getGrupoNegocios().add(grupoNegocioUnif);
		}
		
				
		return aplicacionUnificado;
	}
	
	private GrupoNegocioUnificado getGrupoNegocioUnificado(es.agroseguro.recibos.gastosRecibosImpagados.GrupoNegocio gn) {
		GrupoNegocioUnificado grupoNegocioUnif= new GrupoNegocioUnificado();
		
		this.llenaGastosAbonar(grupoNegocioUnif, gn.getGastosAbonarCobroActual().getGastosAdminEntidad(), 
				gn.getGastosAbonarCobroActual().getGastosAdqEntidad(), gn.getGastosAbonarCobroActual().getComisionesMediador(),
				null, null, null, null);
		
		this.llenaGastosPendientesAbonar(grupoNegocioUnif, gn.getGastosPendientesAbonar().getGastosAdminEntidad(), 
				gn.getGastosPendientesAbonar().getGastosAdqEntidad(), gn.getGastosPendientesAbonar().getComisionesMediador(), null, null);
		
		this.llenaGastosPendientesAbonarRecibosImpagados(grupoNegocioUnif, gn.getGastosPendientesAbonarReciboImpagado().getGastosAdminEntidad(), 
				gn.getGastosPendientesAbonarReciboImpagado().getGastosAdqEntidad(), gn.getGastosPendientesAbonarReciboImpagado().getComisionesMediador(), 
				null, null);
		
		grupoNegocioUnif.setGrupoNegocio(gn.getGrupoNegocio().charAt(0));	
		
		return grupoNegocioUnif;
	}
	
}
