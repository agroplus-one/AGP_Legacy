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





public class FicheroUnificadoGastosRecibosEmtidosManager extends FicheroUnificadoGastosManager{
	
	private static final Log LOGGER = LogFactory.getLog(FicheroUnificadoGastosRecibosEmtidosManager.class);
	
	public FicheroUnificado getFicheroUnificado(XmlObject xml, 
			Usuario usuario, Character tipoFichero, String nombreFichero, Blob blob) throws Exception {
		
		LOGGER.debug("init - getFicheroUnificado");
		FicheroUnificado file=null;
		try {
			//Datos generales del fichero
			file=new FicheroUnificado();
			this.llenaDatosGeneralesFichero(file, usuario == null ? "@BATCH" : usuario.getCodusuario(), tipoFichero, nombreFichero, new Date());
			//----------------------------
			
			//FASES
			es.agroseguro.recibos.gastosRecibos.FasesDocument fases =(es.agroseguro.recibos.gastosRecibos.FasesDocument)xml;
			for(es.agroseguro.recibos.gastosRecibos.Fase fase:fases.getFases().getFaseArray()) {
				FaseUnificado faseUnificado= this.getFaseUnificado(fase.getFase(),fase.getFechaEmisionRecibo().getTime(), fase.getPlan(), file);
				for(es.agroseguro.recibos.gastosRecibos.Recibo re:fase.getReciboArray()){
					ReciboUnificado reciboUnificado = new ReciboUnificado();
					
					//Datos propios
					if(re.getLinea()!=0)reciboUnificado.setLinea(re.getLinea());
					if(re.getRecibo()!=0)reciboUnificado.setRecibo(re.getRecibo());
					if(re.getTipo()!=0)reciboUnificado.setTipo(re.getTipo());
					if (fase.getPlan()!=0)reciboUnificado.setPlan(fase.getPlan());
					
					//o tiene datos de colectivo o datos individuales
					if(null!=re.getColectivo()) {
						ColectivoUnificado colectivoUnificado = getColectivoUnificado(re.getColectivo());
						reciboUnificado.setColectivo(colectivoUnificado);
					}else {//Individual
						IndividualUnificado individualUnificado=getIndividualUnificado(re.getIndividual());
						reciboUnificado.setIndividual(individualUnificado);
					}
					
					
					//Datos de Aplicación
					AplicacionUnificado aplicacionUnificado=null;
					es.agroseguro.recibos.gastosRecibos.Aplicacion apl = re.getAplicacion();
					
					//o tiene nombre y apellidos o tiene razón social
					aplicacionUnificado=this.getAplicacionUnificado(apl.getNombreApellidos(), apl.getRazonSocial(),
							apl.getAnuladaRefundida().toString().charAt(0), apl.getCodigoInterno(), apl.getDigitoControl(),
							apl.getReferencia(), apl.getTipoReferencia().toString().charAt(0));
					
					
					
					//Grupos de negocio					
					for(es.agroseguro.recibos.gastosRecibos.GrupoNegocio gn:apl.getGrupoNegocioArray()) {
						GrupoNegocioUnificado grupoNegocioUnificado=getGrupoNegocioUnificado(gn);
						aplicacionUnificado.getGrupoNegocios().add(grupoNegocioUnificado);
						reciboUnificado.setAplicacion(aplicacionUnificado);
						}
					reciboUnificado.setFase(faseUnificado);
					faseUnificado.getReciboUnificados().add(reciboUnificado);
					
					}
				
				file.getFases().add(faseUnificado);
			}
			
			Long idFichero=null;			
			file.setFicheroContenido(this.getFicheroContenidoUnificado(file, blob, idFichero));
			
			
		} catch (Exception ex) {
			LOGGER.debug("Se ha producido un error en la creacion del fichero unificado de gastos de recibos emitidos", ex);
			throw new Exception("",ex);
		}
					
		return file;
		
	}
	
	private GrupoNegocioUnificado getGrupoNegocioUnificado(es.agroseguro.recibos.gastosRecibos.GrupoNegocio gn) {
		es.agroseguro.recibos.gastosRecibos.ComisionesGastos ga= gn.getGastosAbonar();
		es.agroseguro.recibos.gastosRecibos.ComisionesGastos gd= gn.getGastosDevengados();
		es.agroseguro.recibos.gastosRecibos.ComisionesGastos gpa =gn.getGastosPendientesAbonar();
		
		Integer marcaCondParticulares=null;
		if(null!=gn.getCondicionesParticulares())marcaCondParticulares=new Integer(gn.getCondicionesParticulares().getMarcaCondicionesParticulares());
		GrupoNegocioUnificado gnu= new GrupoNegocioUnificado();
		gnu.setMarcaCondParticulares(marcaCondParticulares);
		gnu.setGrupoNegocio(gn.getGrupoNegocio().charAt(0));
		gnu.setPrimaComercialNeta(gn.getPrimaComercialNeta());
		
		this.llenaGastosAbonar(gnu, ga.getGastosAdminEntidad(), ga.getGastosAdqEntidad(), ga.getComisionesMediador(), 
				null, null, null, null);
		
		this.llenaGastosDevengados(gnu, gd.getGastosAdminEntidad(), gd.getGastosAdqEntidad(), gd.getComisionesMediador(), null, null);
		
		this.llenaGastosPendientesAbonar(gnu, gpa.getGastosAdminEntidad(), gpa.getGastosAdqEntidad(), gpa.getComisionesMediador(), null, null);
			
		return gnu;
	}
	
	private ColectivoUnificado getColectivoUnificado(es.agroseguro.recibos.gastosRecibos.Colectivo co ) {
		es.agroseguro.iTipos.NombreApellidos na= co.getNombreApellidos();
		es.agroseguro.iTipos.RazonSocial rz =co.getRazonSocial();
		ColectivoUnificado colectivoUnificado=this.getColectivoUnificado(na, rz, co.getReferencia(), co.getCodigoInterno(), co.getDigitoControl());
		
		return colectivoUnificado;
	}
	
	private IndividualUnificado getIndividualUnificado(es.agroseguro.recibos.gastosRecibos.Individual ind) {
		es.agroseguro.iTipos.NombreApellidos na = ind.getNombreApellidos();
		es.agroseguro.iTipos.RazonSocial rz= ind.getRazonSocial();
		IndividualUnificado individualUnificado=this.getIndividualUnificado(na, rz);
		return individualUnificado;
	}
}
