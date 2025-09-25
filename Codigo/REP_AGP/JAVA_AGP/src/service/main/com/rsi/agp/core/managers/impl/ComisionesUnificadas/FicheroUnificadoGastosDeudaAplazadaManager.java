package com.rsi.agp.core.managers.impl.ComisionesUnificadas;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlObject;

import com.rsi.agp.dao.tables.comisiones.unificado.AplicacionUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.ColectivoUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.FaseUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.GastosAbonadosDeudaAplazadaUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.GrupoNegocioUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.IndividualColectivoUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.IndividualUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.ReciboUnificado;
import com.rsi.agp.dao.tables.commons.Usuario;


public class FicheroUnificadoGastosDeudaAplazadaManager  extends FicheroUnificadoGastosManager{
	
	private static final Log LOGGER = LogFactory.getLog(FicheroUnificadoGastosDeudaAplazadaManager.class);

	public FicheroUnificado getFicheroUnificado(XmlObject xml, 
			Usuario usuario,Character tipoFichero, String nombreFichero, Blob blob) throws Exception {
				//es.agroseguro.recibos.gastosCobroDeudaAplazada.FasesDocument fase
		LOGGER.debug("init - getFicheroUnificado");
		FicheroUnificado file=null;
		try {
			//Datos generales del fichero
			file=new FicheroUnificado();
			this.llenaDatosGeneralesFichero(file, usuario.getCodusuario(), tipoFichero, nombreFichero, new Date());
			//----------------------------
			
			//FASES
			es.agroseguro.recibos.gastosCobroDeudaAplazada.FasesDocument fases=(es.agroseguro.recibos.gastosCobroDeudaAplazada.FasesDocument)xml;
			for(es.agroseguro.recibos.gastosCobroDeudaAplazada.FaseAbono fase:fases.getFases().getFaseAbonoArray()) {
				Date fechaEmisionRecibo=null;
				FaseUnificado faseUnificado= this.getFaseUnificado(fase.getFase(),fechaEmisionRecibo, fase.getPlan(), file);
				Set<IndividualColectivoUnificado> indColUnif=new HashSet<IndividualColectivoUnificado>();
				if(null!=fase.getColectivoArray() && fase.getColectivoArray().length>0) {
					indColUnif.addAll(this.getColectivos(fase.getColectivoArray(),  fase.getPlan()));
				}
				if(null!=fase.getIndividualArray() && fase.getIndividualArray().length>0) {
					indColUnif.addAll(this.getIndividuales(fase.getIndividualArray(), fase.getPlan()));
				}
				
				faseUnificado.setIndividualColectivos(indColUnif);
				file.getFases().add(faseUnificado);
			}
			Long idFichero=null;
			file.setFicheroContenido(this.getFicheroContenidoUnificado(file, blob, idFichero));
			
		} catch (Exception ex) {
			LOGGER.debug("Se ha producido un error en la creacion del fichero unificado de gastos de deuda aplazada", ex);
			throw new Exception("", ex);
		}
					
		return file;
	
}
	
	private Set<IndividualColectivoUnificado> getIndividuales(es.agroseguro.recibos.gastosCobroDeudaAplazada.Individual[] individuales, int plan){
		//Set<ParcelaCobertura> coberturasParcela = new HashSet<ParcelaCobertura>(0);
		Set<IndividualColectivoUnificado> individualColectivoUnificado= new HashSet<IndividualColectivoUnificado>();
		
		for(es.agroseguro.recibos.gastosCobroDeudaAplazada.Individual ind:individuales) {
			//NombreApellidos o razón social
			IndividualColectivoUnificado indColUnif =new IndividualColectivoUnificado();
			IndividualUnificado iu=this.getIndividualUnificado(ind);
			indColUnif.setIndividual(iu);
			//Aplicaciones
			this.getAplicaciones(indColUnif, ind, plan);
			
			individualColectivoUnificado.add(indColUnif);
		}
		
		return individualColectivoUnificado;
	}
	
	private Set<IndividualColectivoUnificado> getColectivos(es.agroseguro.recibos.gastosCobroDeudaAplazada.Colectivo[] colectivos, int plan){
		Set<IndividualColectivoUnificado> individualColectivoUnificado= new HashSet<IndividualColectivoUnificado>();
		
		for(es.agroseguro.recibos.gastosCobroDeudaAplazada.Colectivo col:colectivos) {
			//NombreApellidos o razón social
			IndividualColectivoUnificado indColUnif =new IndividualColectivoUnificado();
			ColectivoUnificado colUnif=this.getColectivoUnificado(col);
			indColUnif.setColectivo(colUnif);
			//Aplicaciones
			getAplicaciones(indColUnif, col, plan);
			
			individualColectivoUnificado.add(indColUnif );
		}
		
		
		return individualColectivoUnificado;
	}
	
	private IndividualUnificado getIndividualUnificado(es.agroseguro.recibos.gastosCobroDeudaAplazada.Individual ind) {
		es.agroseguro.iTipos.NombreApellidos na = ind.getNombreApellidos();
		es.agroseguro.iTipos.RazonSocial rz= ind.getRazonSocial();
		
		IndividualUnificado individualUnificado=this.getIndividualUnificado(na, rz);
		return individualUnificado;
	}
	
	private ColectivoUnificado getColectivoUnificado(es.agroseguro.recibos.gastosCobroDeudaAplazada.Colectivo co ) {
		es.agroseguro.iTipos.NombreApellidos na= co.getNombreApellidos();
		es.agroseguro.iTipos.RazonSocial rz =co.getRazonSocial();
		ColectivoUnificado colectivoUnificado = this.getColectivoUnificado(na, rz, co.getReferencia(), co.getCodigoInterno(), co.getDigitoControl());
		return colectivoUnificado;
	}
	
	private void getAplicaciones(IndividualColectivoUnificado indColUnif, es.agroseguro.recibos.gastosCobroDeudaAplazada.Colectivo col, int plan) {
		for(es.agroseguro.recibos.gastosCobroDeudaAplazada.Aplicacion apl:col.getAplicacionArray()) {
			AplicacionUnificado aplUnif = null;
			
			 aplUnif=this.getAplicacionUnificado(apl.getNombreApellidos(),apl.getRazonSocial(),
					apl.getAnuladaRefundida().toString().charAt(0),apl.getCodigoInterno(),apl.getDigitoControl(),
					apl.getReferencia(), apl.getTipo().toString().charAt(0));
			//apl.getLinea() OJO no lo contiene el objeto Unificado
			for(es.agroseguro.recibos.gastosCobroDeudaAplazada.Recibo re:apl.getReciboArray()) {
				ReciboUnificado reciboUnificado= new ReciboUnificado();
				reciboUnificado.setRecibo(re.getRecibo());
				reciboUnificado.setTipo(re.getTipo());
				reciboUnificado.setPlan(plan);
				reciboUnificado.setLinea(apl.getLinea());
				for(es.agroseguro.recibos.gastosCobroDeudaAplazada.GrupoNegocio gn:re.getGrupoNegocioArray()) {
					GrupoNegocioUnificado gnu=this.getGrupoNegocioUnificado(gn.getGastosAbonarArray(),
							gn.getGastosDevengados(), gn.getGrupoNegocio(), gn.getPrimaComercialNeta());
					reciboUnificado.getGrupoNegocios().add(gnu);
				}
				
				aplUnif.getRecibos().add(reciboUnificado);
			}
			
			indColUnif.getAplicacions().add(aplUnif);	
		}
	}
	
	private void getAplicaciones(IndividualColectivoUnificado indColUnif, es.agroseguro.recibos.gastosCobroDeudaAplazada.Individual iu, int plan) {
		for(es.agroseguro.recibos.gastosCobroDeudaAplazada.Aplicacion apl:iu.getAplicacionArray()) {
			AplicacionUnificado aplUnif = null;
			
			 aplUnif=this.getAplicacionUnificado(apl.getNombreApellidos(),apl.getRazonSocial(),
					apl.getAnuladaRefundida().toString().charAt(0),apl.getCodigoInterno(),apl.getDigitoControl(),
					apl.getReferencia(), apl.getTipo().toString().charAt(0));
			//apl.getLinea() OJO no lo contiene el objeto Unificado
			for(es.agroseguro.recibos.gastosCobroDeudaAplazada.Recibo re:apl.getReciboArray()) {
				ReciboUnificado reciboUnificado= new ReciboUnificado();
				reciboUnificado.setRecibo(re.getRecibo());
				reciboUnificado.setTipo(re.getTipo());
				reciboUnificado.setPlan(plan);
				reciboUnificado.setLinea(apl.getLinea());
				for(es.agroseguro.recibos.gastosCobroDeudaAplazada.GrupoNegocio gn:re.getGrupoNegocioArray()) {
					GrupoNegocioUnificado gnu=this.getGrupoNegocioUnificado(gn.getGastosAbonarArray(),
							gn.getGastosDevengados(), gn.getGrupoNegocio(), gn.getPrimaComercialNeta());
					reciboUnificado.getGrupoNegocios().add(gnu);
				}
				
				aplUnif.getRecibos().add(reciboUnificado);
			}
			
			indColUnif.getAplicacions().add(aplUnif);	
		}
	}
	
	
	
	private GrupoNegocioUnificado getGrupoNegocioUnificado(
			es.agroseguro.recibos.gastosCobroDeudaAplazada.GastosAbonar gastosAbonar[],
			es.agroseguro.recibos.gastosCobroDeudaAplazada.ComisionesGastos gastosDevengados,
			String grupoNegocio, 
			BigDecimal primaComercialNeta) {
		
		
		GrupoNegocioUnificado gnu = new GrupoNegocioUnificado();
		gnu.setGrupoNegocio(grupoNegocio.charAt(0));
		gnu.setPrimaComercialNeta(primaComercialNeta);
		
		this.llenaGastosDevengados(gnu, gastosDevengados.getGastosAdminEntidad(), gastosDevengados.getGastosAdqEntidad(), gastosDevengados.getComisionesMediador(),
				null, null);
		
		for(es.agroseguro.recibos.gastosCobroDeudaAplazada.GastosAbonar ga:gastosAbonar) {
			GastosAbonadosDeudaAplazadaUnificado gada = llenaGastosAbonarDeudaAplazada(gnu,ga);
			gnu.getGastosAbonadosDeudaAplazadaUnificados().add(gada);
		}
		return gnu;
	}
	
	
	private  GastosAbonadosDeudaAplazadaUnificado  llenaGastosAbonarDeudaAplazada(GrupoNegocioUnificado gnu,es.agroseguro.recibos.gastosCobroDeudaAplazada.GastosAbonar gastosAbonar ) {
		GastosAbonadosDeudaAplazadaUnificado gd=new GastosAbonadosDeudaAplazadaUnificado();
		gd.setGaComisionMediador(gastosAbonar.getComisionesMediador());
		gd.setGaAdmin(gastosAbonar.getGastosAdminEntidad());
		gd.setGaAdq(gastosAbonar.getGastosAdqEntidad());
		gd.setGaPlazoDomiciliacion(gastosAbonar.getPlazoDomiciliacion());
		gd.setGaReciboCompensacion(gastosAbonar.getRecibo());
		// OJO - Los llenamos a cero porque son no nullables pero hay que calcular su importe
		gd.setGaCommedEntidad(new BigDecimal(0));
		gd.setGaCommedEsmed(new BigDecimal(0));
		//
		gd.setGrupoNegocioUnificado(gnu);
		return gd;
	}
	
}
