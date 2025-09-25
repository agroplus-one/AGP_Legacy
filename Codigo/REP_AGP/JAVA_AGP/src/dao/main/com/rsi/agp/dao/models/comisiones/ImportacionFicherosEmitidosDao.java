package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlObject;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.CollectionUtils;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.comisiones.Fase;
import com.rsi.agp.dao.tables.comisiones.Fichero;
import com.rsi.agp.dao.tables.comisiones.FicheroContenido;
import com.rsi.agp.dao.tables.comisiones.recibosEmitidos.ReciboEmitido;
import com.rsi.agp.dao.tables.comisiones.recibosEmitidos.ReciboEmitidoApliBonRec;
import com.rsi.agp.dao.tables.comisiones.recibosEmitidos.ReciboEmitidoApliSubvCCAA;
import com.rsi.agp.dao.tables.comisiones.recibosEmitidos.ReciboEmitidoAplicacion;
import com.rsi.agp.dao.tables.comisiones.recibosEmitidos.ReciboEmitidoDetComp;
import com.rsi.agp.dao.tables.comisiones.recibosEmitidos.ReciboEmitidoSubvCCA;
import com.rsi.agp.dao.tables.commons.Usuario;

import es.agroseguro.iTipos.NombreApellidos;
import es.agroseguro.iTipos.RazonSocial;
import es.agroseguro.recibos.emitidos.BonificacionRecargo;
import es.agroseguro.recibos.emitidos.BonificacionesRecargos;
import es.agroseguro.recibos.emitidos.Colectivo;
import es.agroseguro.recibos.emitidos.DatosEconomicos1;
import es.agroseguro.recibos.emitidos.DatosEconomicosAplicacion;
import es.agroseguro.recibos.emitidos.DatosEconomicosRecibo;
import es.agroseguro.recibos.emitidos.DetalleCompensac;
import es.agroseguro.recibos.emitidos.Individual;
import es.agroseguro.recibos.emitidos.IndividualAplicacion;
import es.agroseguro.recibos.emitidos.IndividualAplicaciones;
import es.agroseguro.recibos.emitidos.Recibo;
import es.agroseguro.recibos.emitidos.SubvencionCCAA;

public class ImportacionFicherosEmitidosDao extends BaseDaoHibernate implements IImportacionFicherosEmitidosDao {
	private static final Log LOGGER = LogFactory.getLog(ImportacionFicherosEmitidosDao.class);
	
	private IFaseDao faseDao;

	@Override
	public Long importarYValidarFicheroEmitidos(XmlObject xmlObject,Usuario usuario, Character tipo, String nombre, Boolean saveXML) throws DAOException {
		LOGGER.debug("init - importarYValidarFicheroEmitidos");
		try {
			Set<ReciboEmitido> listRecibosEmitidos = new HashSet<ReciboEmitido>();
			ReciboEmitido reciboEmitido = null;
			FicheroContenido ficheroContenido = null;
			if (saveXML) {
				ficheroContenido = new FicheroContenido();
			}
			Fichero fichero = new Fichero();
			
			LOGGER.debug("datos comunes del fichero");
			fichero.setUsuario(usuario);
			fichero.setTipofichero(tipo);
			fichero.setFechacarga(new Date());
			fichero.setNombrefichero(nombre);
			fichero.setFechaaceptacion(new Date());
			
			LOGGER.debug("casting de nuestro XMLObject al tipo Fase del nodo principal del XML");
			es.agroseguro.recibos.emitidos.FaseDocument FaseDocumento = (es.agroseguro.recibos.emitidos.FaseDocument) xmlObject;
			
			LOGGER.debug("FASE recibos emitidos");
		
			fichero.setFechacarga(new Date());
			
			Fase faseFichero = new Fase();
			
			String fase = FaseDocumento.getFase().getFase() + "";
			Date fecha = FaseDocumento.getFase().getFechaEmisionRecibo().getTime();
			BigDecimal plan = new BigDecimal(FaseDocumento.getFase().getPlan());
			
			faseFichero.setFase(fase);
			faseFichero.setFechaemision(fecha);
			faseFichero.setPlan(plan);
			
			Fase faseaux = faseDao.isExistFase(fase, plan);
			
			if (faseaux.getId() == null){
				fichero.setFase(faseFichero);
				faseDao.saveFaseFichero(faseFichero);
			} else {
				fichero.setFase(faseaux);
			}
			if (saveXML && ficheroContenido != null) {
				LOGGER.debug("contenido xml");
				ficheroContenido.setFichero(fichero);
				ficheroContenido.setContenido(xmlObject.xmlText());
				fichero.setFicheroContenido(ficheroContenido);
			}
			for(Recibo re:FaseDocumento.getFase().getReciboArray()){
				reciboEmitido = new ReciboEmitido();
				reciboEmitido.setFichero(fichero);
				LOGGER.debug("Atributos comunes: linea, recibo,...");
				//Linea linea = getLineaseguroId(new BigDecimal(re.getLinea()), plan);
				
				reciboEmitido.setLinea(new BigDecimal(re.getLinea()));
				reciboEmitido.setCodigo(re.getCodigo());
				reciboEmitido.setRecibo(new BigDecimal(re.getRecibo()));
				
				if(re.getDeudor() != null){
					reciboEmitido.setDeudor(re.getDeudor().toString());
				}
				
				reciboEmitido.setTipificacionRecibo(re.getTipo());
				
				LOGGER.debug("Colectivo");
				Colectivo colTemp = re.getColectivo();
				if(colTemp != null){
					reciboEmitido.setColectivodc(new BigDecimal(colTemp.getDigitoControl()));
					reciboEmitido.setColectivoref(colTemp.getReferencia());
				}
				
				LOGGER.debug("Individual");
				Individual indvTemp = re.getIndividual();
				if(indvTemp != null){
					reciboEmitido.setIndividualdc(new BigDecimal(indvTemp.getDigitoControl()));
					reciboEmitido.setIndividualreferencia(indvTemp.getReferencia());
				}
				
				LOGGER.debug("Nombre y Apellidos");
				if( re.getNombreApellidos()!= null){
					es.agroseguro.iTipos.NombreApellidos nomApell = re.getNombreApellidos();
					
					reciboEmitido.setApellido1(nomApell.getApellido1());
					if(nomApell.getApellido2() != null)
						reciboEmitido.setApellido2(nomApell.getApellido2());
					reciboEmitido.setNombre(nomApell.getNombre());
				}else{
					LOGGER.debug("Razon Social");
					RazonSocial razonSocial = (RazonSocial) re.getRazonSocial();
					reciboEmitido.setRazonsocial(razonSocial.getRazonSocial());
				}
				LOGGER.debug("Direccion");
				es.agroseguro.iTipos.Direccion dirAux= re.getDireccion();				
				if(dirAux.getBloque() != null)
					reciboEmitido.setBloque(dirAux.getBloque());	
				if(dirAux.getEscalera() != null)
					reciboEmitido.setEscalera(dirAux.getEscalera());	
				if(dirAux.getPiso() != null)
					reciboEmitido.setPiso(dirAux.getPiso());	
				
				reciboEmitido.setCodigopostal(dirAux.getCp());				
				reciboEmitido.setLocalidad(dirAux.getNumero());
				reciboEmitido.setVianombre(dirAux.getVia());
				reciboEmitido.setVianumero(dirAux.getNumero());				
				reciboEmitido.setProvincia(new BigDecimal(dirAux.getProvincia()));
				
				LOGGER.debug("Datos econÃ³micos");
				DatosEconomicosRecibo datosEconomicosRecibo = re.getDatosEconomicos();
				
				if (datosEconomicosRecibo != null){
					if (datosEconomicosRecibo.getCompensacionSaldoTomador() != null)
						reciboEmitido.setCompsaldotomador(datosEconomicosRecibo.getCompensacionSaldoTomador());
					if (datosEconomicosRecibo.getCompensacionRecibosImpagados() != null)
						reciboEmitido.setComprecibosimpagados(datosEconomicosRecibo.getCompensacionRecibosImpagados());
					if (datosEconomicosRecibo.getLiquido() != null)
						reciboEmitido.setLiquido(datosEconomicosRecibo.getLiquido());					
					if (datosEconomicosRecibo.getPrimaComercial() != null)
						reciboEmitido.setPrimacomercial(datosEconomicosRecibo.getPrimaComercial());
					if (datosEconomicosRecibo.getBonificacionSistProteccion() != null)
						reciboEmitido.setBonsistproteccion(datosEconomicosRecibo.getBonificacionSistProteccion());
					if (datosEconomicosRecibo.getBonificacion() != null)
						reciboEmitido.setBonificacion(datosEconomicosRecibo.getBonificacion());
					if (datosEconomicosRecibo.getRecargo() != null)
						reciboEmitido.setRecargo(datosEconomicosRecibo.getRecargo());
					if (datosEconomicosRecibo.getDescuentoColectivo() != null)
						reciboEmitido.setDtoColectivo(datosEconomicosRecibo.getDescuentoColectivo());
					if (datosEconomicosRecibo.getDescuentoVentanilla() != null)
						reciboEmitido.setDtoVentanilla(datosEconomicosRecibo.getDescuentoVentanilla());
					if (datosEconomicosRecibo.getPrimaNeta() != null)
						reciboEmitido.setPrimaneta(datosEconomicosRecibo.getPrimaNeta());
					if (datosEconomicosRecibo.getConsorcio() != null)
						reciboEmitido.setConsorcio(datosEconomicosRecibo.getConsorcio());
					if (datosEconomicosRecibo.getClea() != null)
						reciboEmitido.setClea(datosEconomicosRecibo.getClea());
					if (datosEconomicosRecibo.getCosteNeto() != null)
						reciboEmitido.setCosteneto(datosEconomicosRecibo.getCosteNeto());
					if (datosEconomicosRecibo.getSubvencionEnesa() != null)
						reciboEmitido.setSubvenesa(datosEconomicosRecibo.getSubvencionEnesa());
					if (datosEconomicosRecibo.getCosteTomador() != null)
						reciboEmitido.setCostetomador(datosEconomicosRecibo.getCosteTomador());
					if (datosEconomicosRecibo.getPagos() != null)
						reciboEmitido.setPagos(datosEconomicosRecibo.getPagos());							
				}
				
				LOGGER.debug("Detalle de compensacion");
				Set<ReciboEmitidoDetComp> listDetallesComp = new HashSet<ReciboEmitidoDetComp>();
				ReciboEmitidoDetComp detalle = null;
				if(re.getDetalleCompensacArray() != null && re.getDetalleCompensacArray().length > 0){
					for (DetalleCompensac dc : re.getDetalleCompensacArray()){
						detalle = new ReciboEmitidoDetComp();
						
						detalle.setCobro(dc.getCobro());
						detalle.setLinea(new BigDecimal(dc.getLinea()));
						detalle.setPlan(new BigDecimal(dc.getPlan()));
						detalle.setRecibo(new BigDecimal(dc.getRecibo()));
						detalle.setReciboEmitido(reciboEmitido);
						
						listDetallesComp.add(detalle);
					}
					reciboEmitido.setReciboEmitidoDetComps(listDetallesComp);
				}
				
				LOGGER.debug("Subvenciones CCAA");
				Set<ReciboEmitidoSubvCCA> listsubccaa = new HashSet<ReciboEmitidoSubvCCA>();
				ReciboEmitidoSubvCCA subcca = null;
				if(re.getSubvencionCCAAArray() != null && re.getSubvencionCCAAArray().length > 0){
					for(SubvencionCCAA subaux:re.getSubvencionCCAAArray()){
						subcca = new ReciboEmitidoSubvCCA();
						
						if(subaux.getCodigo() != null)
							subcca.setCodigo(subaux.getCodigo().charAt(0));
						
						subcca.setReciboEmitido(reciboEmitido);
						subcca.setSubvcomunidades(subaux.getSubvencionComunidades());
						
						listsubccaa.add(subcca);
					}
					reciboEmitido.setReciboEmitidoSubvCCAs(listsubccaa);
				}
				
				LOGGER.debug("Aplicaciones");
				Set<ReciboEmitidoAplicacion> listRecibosEmitidosAplicacion = new HashSet<ReciboEmitidoAplicacion>();
				ReciboEmitidoAplicacion aplicacion = null;
				if(re.getPolizasAplicaciones() != null){
					IndividualAplicaciones individualAplicacionesAux = re.getPolizasAplicaciones();
					for(IndividualAplicacion apliAux: individualAplicacionesAux.getIndividualAplicacionArray()){
						aplicacion = new ReciboEmitidoAplicacion();
						
						LOGGER.debug("Atributos comunes Aplicacion: referencia,dc,nif,...");
						aplicacion.setReferencia(apliAux.getReferencia());
						aplicacion.setDigitocontrol(new BigDecimal(apliAux.getDigitoControl()));
						aplicacion.setNifcif(apliAux.getNif());
						aplicacion.setTiporeferencia(apliAux.getTipoReferencia().toString().charAt(0));
						aplicacion.setTiporecibo(apliAux.getTipoRecibo().toString().charAt(0));
						
						if (apliAux.getDomiciliado() != null && !"".equals(apliAux.getDomiciliado()))
							aplicacion.setDomiciliado(apliAux.getDomiciliado().charAt(0));
						if (apliAux.getDestinatario() != null && !"".equals(apliAux.getDestinatario()))
							aplicacion.setDestDomiciliacion(apliAux.getDestinatario().charAt(0));
						
						LOGGER.debug("Nombre y Apellidos");
						if(apliAux.getNombreApellidos() != null){
							NombreApellidos nomAux = apliAux.getNombreApellidos();
							
							aplicacion.setApellido1(nomAux.getApellido1());
							if(nomAux.getApellido2() != null)
								aplicacion.setApellido2(nomAux.getApellido2());
							aplicacion.setNombre(nomAux.getNombre());
							
						}else{
							LOGGER.debug("Razon Social");
							RazonSocial razonAux = apliAux.getRazonSocial();
							
							aplicacion.setRazonsocial(razonAux.getRazonSocial());
						}
						
						LOGGER.debug("Datos Económicos");
						DatosEconomicosAplicacion datosAux = apliAux.getDatosEconomicos();
						if (datosAux != null){
							if (datosAux.getSaldoPoliza() != null)
								aplicacion.setSaldopoliza(datosAux.getSaldoPoliza());
							if (datosAux.getPrimaComercial() != null)
								aplicacion.setPrimacomercial(datosAux.getPrimaComercial());
							if (datosAux.getBonificacionSistProteccion() != null)
								aplicacion.setBonsistproteccion(datosAux.getBonificacionSistProteccion());
							if (datosAux.getBonificacion() != null)
								aplicacion.setBonificacion(datosAux.getBonificacion());
							if (datosAux.getRecargo() != null)
								aplicacion.setRecargo(datosAux.getRecargo());
							if (datosAux.getDescuentoColectivo() != null)
								aplicacion.setDtocolectivo(datosAux.getDescuentoColectivo());
							if (datosAux.getDescuentoVentanilla() != null)
								aplicacion.setDtoventanilla(datosAux.getDescuentoVentanilla());
							if (datosAux.getPrimaNeta() != null)
								aplicacion.setPrimaneta(datosAux.getPrimaNeta());
							if (datosAux.getConsorcio() != null)
								aplicacion.setConsorcio(datosAux.getConsorcio());
							if (datosAux.getClea() != null)
								aplicacion.setClea(datosAux.getClea());
							if (datosAux.getCosteNeto() != null)
								aplicacion.setCosteneto(datosAux.getCosteNeto());
							if (datosAux.getSubvencionEnesa() != null)
								aplicacion.setSubenesa(datosAux.getSubvencionEnesa());
							if (datosAux.getCosteTomador() != null)
								aplicacion.setCostetomador(datosAux.getCosteTomador());
							if (datosAux.getPagos() != null)
								aplicacion.setPagos(datosAux.getPagos());							
						}
						
						DatosEconomicos1 datosEco1 = apliAux.getDatosEconomicos1();
						if(datosEco1!=null){
							LOGGER.debug("Datos Económicos 1");
							aplicacion.setDe1CosteTomador(datosEco1.getCosteTomador());
							aplicacion.setDe1Diferencia(datosEco1.getDiferencia());
							aplicacion.setDe1Pagos(datosEco1.getPagos());
							aplicacion.setDe1PrimaComercial(datosEco1.getPrimaComercial());
							aplicacion.setDe1PrimaComercialNeta(datosEco1.getPrimaComercialNeta());
							aplicacion.setDe1RecargoAval(datosEco1.getRecargoAval());
							aplicacion.setDe1RecargoConsorcio(datosEco1.getRecargoConsorcio());
							aplicacion.setDe1RecargoFracc(datosEco1.getRecargoFraccionamiento());
							aplicacion.setDe1ReciboPrima(datosEco1.getReciboPrima());
							aplicacion.setDe1SubvEnesa(datosEco1.getSubvencionEnesa());
							aplicacion.setDe1TotalCosteTomador(datosEco1.getTotalCosteTomador());
							aplicacion.setDe1ImpDomiciliado(datosEco1.getImporteADomiciliar());
							
							Set<ReciboEmitidoApliBonRec> reciboEmitidoApliBonRecs = new HashSet<ReciboEmitidoApliBonRec>();
							BonificacionesRecargos boniRecargos = datosEco1.getBonificacionesRecargos();
							
							if(boniRecargos!=null){
								BonificacionRecargo[] bonificacionRecargoArray = boniRecargos.getBonificacionRecargoArray();
								
								for (int i = 0; i < bonificacionRecargoArray.length; i++) {
									BonificacionRecargo bonificacionRecargo = bonificacionRecargoArray[i];
									ReciboEmitidoApliBonRec recEmitidoApliBonRec = new ReciboEmitidoApliBonRec();
									recEmitidoApliBonRec.setCodigo(bonificacionRecargo.getCodigo());
									recEmitidoApliBonRec.setImporte(bonificacionRecargo.getImporte());
									recEmitidoApliBonRec.setReciboEmitidoAplicacion(aplicacion);
									reciboEmitidoApliBonRecs.add(recEmitidoApliBonRec);
								}
							}
							
							aplicacion.setReciboEmitidoApliBonRecs(reciboEmitidoApliBonRecs);
						}
						
						
						LOGGER.debug("SubvencionCCAA");
						Set<ReciboEmitidoApliSubvCCAA> listRecibosEmitiosApliSubCCAA = new HashSet<ReciboEmitidoApliSubvCCAA>();
						ReciboEmitidoApliSubvCCAA recibosEmitiosApliSubCCAA = null;
						if(apliAux.getSubvencionCCAAArray() != null && apliAux.getSubvencionCCAAArray().length > 0){
							for(SubvencionCCAA subAux: apliAux.getSubvencionCCAAArray()){
								recibosEmitiosApliSubCCAA = new ReciboEmitidoApliSubvCCAA();
								
								if(subAux.getCodigo() != null)
									recibosEmitiosApliSubCCAA.setCodigo(subAux.getCodigo().charAt(0));
								recibosEmitiosApliSubCCAA.setReciboEmitidoAplicacion(aplicacion);
								recibosEmitiosApliSubCCAA.setSubvcomunidades(subAux.getSubvencionComunidades());
								
								recibosEmitiosApliSubCCAA.setReciboEmitidoAplicacion(aplicacion);
								listRecibosEmitiosApliSubCCAA.add(recibosEmitiosApliSubCCAA);
							}
							aplicacion.setReciboEmitidoApliSubvCCAAs(listRecibosEmitiosApliSubCCAA);
						}
						aplicacion.setReciboEmitido(reciboEmitido);
						listRecibosEmitidosAplicacion.add(aplicacion);
					}
					reciboEmitido.setReciboEmitidoAplicacions(listRecibosEmitidosAplicacion);
				}
				listRecibosEmitidos.add(reciboEmitido);
			}
			
			fichero.setRecibosEmitidos(listRecibosEmitidos); 
			
			LOGGER.debug("Procedemos a guardar en la BBDD el fichero completo");
			//TMR 30-05-2012 Facturacion
			this.saveOrUpdateFacturacion(fichero,usuario);
			
			Long idFichero = fichero.getId();
			LOGGER.debug("ID del fichero procesado: " +idFichero);
			xmlObject = null;
			
			LOGGER.debug("end - importarYValidarFicheroEmitidos");
			return idFichero;
			
		}catch (DAOException dao) {
			LOGGER.debug("Se ha producido un error al guardar en la BBDD el fichero", dao);
			throw new DAOException("Se ha producido un error al guardar en la BBDD el fichero", dao);
		} catch (Exception ex) {
			LOGGER.debug("Se ha producido un error en la creacion del fichero", ex);
			throw new DAOException("Se ha producido un error en la creacion del fichero", ex);
		}
	}
	
	/** DAA 24/09/2013
	 *  Metodo para que cada vez que se importe un fichero comisiones, guarde los datos en la tabla TB_COMS_INFORMES_RECIBOS. 
	 * @throws DAOException 
	 */
	public void actualizarInformesFicheroEmitidos(Long id, int plan) throws DAOException {
		
		try {
			String procedimiento = null;
			
			if(plan<2015){
				//Para planes antiguos
				procedimiento = "O02AGPE0.PQ_INFORMES_RECIBOS.UPDATE_INF_FICHERO_EMITIDOS (IDFICHERO IN NUMBER)"; 
			}else{
				//2015+
				procedimiento = "O02AGPE0.PQ_INFORMES_RECIBOS.UPDATE_INF_FICH_EMIT_2015 (IDFICHERO IN NUMBER)";
			}
			
			Map<String, Object> parametros = new HashMap<String, Object>(); // parametros PL
			parametros.put("IDFICHERO", id);
		
			databaseManager.executeStoreProc(procedimiento, parametros); // ejecutamos PL
		} catch (Exception e) {
			logger.error("Error al actualizar la Tabla Informes Recibos para el Fichero Comisiones",e);
			throw new DAOException("Error al actualizar la Tabla Informes Recibos para el Fichero Comisiones", e);
		}  
		
	}
	
	@Override
	public boolean existeFicheroCargado(final String nomFichero, final Character tipofichero) {

		Criteria crit = this.getSession().createCriteria(Fichero.class);
		crit.add(Restrictions.eq("nombrefichero", nomFichero.trim()).ignoreCase());
		crit.add(Restrictions.eq("tipofichero", tipofichero));
		
		return !CollectionUtils.isEmpty(crit.list());
	}
	
	public void setFaseDao(IFaseDao faseDao) {
		this.faseDao = faseDao;
	}
	
}