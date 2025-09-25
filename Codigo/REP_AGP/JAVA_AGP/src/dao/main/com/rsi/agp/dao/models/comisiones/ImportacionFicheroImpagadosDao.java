package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlObject;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.comisiones.Fichero;
import com.rsi.agp.dao.tables.comisiones.FicheroContenido;
import com.rsi.agp.dao.tables.comisiones.impagados.ReciboImpagado;
import com.rsi.agp.dao.tables.commons.Usuario;

import es.agroseguro.iTipos.NombreApellidos;
import es.agroseguro.iTipos.RazonSocial;
import es.agroseguro.recibos.gastos.CobroActual;
import es.agroseguro.recibos.gastos.CobroActual1;
import es.agroseguro.recibos.gastos.Colectivo;
import es.agroseguro.recibos.gastos.Fase;
import es.agroseguro.recibos.gastos.GastosEntidad;
import es.agroseguro.recibos.gastos.GastosExternos;
import es.agroseguro.recibos.gastos.Individual;
import es.agroseguro.recibos.gastos.PendienteAbono;
import es.agroseguro.recibos.gastos.PendienteAbono1;
import es.agroseguro.recibos.gastos.PendienteAbonoReciboImpagado;
import es.agroseguro.recibos.gastos.PendienteAbonoReciboImpagado1;
import es.agroseguro.recibos.gastos.Poliza;
import es.agroseguro.recibos.gastos.Recibo;

public class ImportacionFicheroImpagadosDao extends BaseDaoHibernate implements IImportacionFicherosImpagadosDao {
	private static final Log LOGGER = LogFactory.getLog(ImportacionFicheroImpagadosDao.class);
	
	private IFaseDao faseDao;
	private IUtilidadesComisionesDao utilidadesComisionesDao;
	
	@Override
	public Long importarYValidarFicheroImpagados(XmlObject xmlObject,Usuario usuario, Character tipo, String nombre,HttpServletRequest request) throws DAOException {
		LOGGER.debug("init - importarYValidarFicheroImpagados");
		Set<ReciboImpagado> impagos = new HashSet<ReciboImpagado>();
		ReciboImpagado reciboImpagado = null;
		Fichero fichero = new Fichero();
		FicheroContenido ficheroContenido = new FicheroContenido();
		
		try {
			LOGGER.debug("Contenido del Fichero");
			
			LOGGER.debug("Datos comunes del fichero");
			fichero.setUsuario(usuario);
			fichero.setTipofichero(tipo);
			fichero.setFechacarga(new Date());
			fichero.setNombrefichero(nombre);
			
			LOGGER.debug("casting de nuestro XMLObject al tipo Fase del nodo principal del XML");
			es.agroseguro.recibos.gastos.FaseDocument FaseDocumento = (es.agroseguro.recibos.gastos.FaseDocument) xmlObject;
			
			LOGGER.debug("FASE impagos");
			Fase faseXML = FaseDocumento.getFase();
			
			fichero.getFase().setFase(faseXML.getFase()+"");
			Date fecha = faseXML.getFechaFaseCobros().getTime();
			fichero.getFase().setFechaemision(fecha);
			fichero.getFase().setPlan(new BigDecimal(faseXML.getEjercicioPago()));
			
			com.rsi.agp.dao.tables.comisiones.Fase faseaux = faseDao.isExistFase(faseXML.getFase()+"", new BigDecimal(faseXML.getEjercicioPago()));
			if (faseaux.getId() == null){
				fichero.setFase(fichero.getFase());
				faseDao.saveFaseFichero(fichero.getFase());
			} 
			else {
				fichero.setFase(faseaux);
			}
			LOGGER.debug("contenido xml");
			ficheroContenido.setFichero(fichero);
			ficheroContenido.setContenido(xmlObject.xmlText());
			fichero.setFicheroContenido(ficheroContenido);
			
			request.getSession().setAttribute("progressStatus", "UPLOADING");
			request.getSession().setAttribute("progress",60);
			
			BigDecimal bigDecimalCien		= new BigDecimal("100.00");//Para comparar luego
			
			LOGGER.debug("Recibos de impagos. Size:" + faseXML.getReciboArray().length);
			for(int i=0;i<faseXML.getReciboArray().length;i++){
				
				boolean esPorcentajesValidos = false;
				BigDecimal[] arrayPorcentajes = null;
				String refPoliza = null;
				BigDecimal porComEntidad = null;
				BigDecimal porComES = null;
				
				Recibo recibo = faseXML.getReciboArray(i);
				reciboImpagado = new ReciboImpagado();
				reciboImpagado.setFichero(fichero);
				
				LOGGER.debug("Atributos comunes: linea, recibo, ejercicio pago (plan), grupo negocio");
				
				reciboImpagado.setGruponegocio(	recibo.getGrupoNegocio().toString().charAt(0));
				reciboImpagado.setLinea(new BigDecimal(recibo.getLinea()));
				reciboImpagado.setPlan(new BigDecimal(recibo.getPlan()));
				reciboImpagado.setRecibo(new BigDecimal(recibo.getRecibo()));
				
				LOGGER.debug("Atributos no comunes");
				
				if( recibo.getNombreApellidos()!= null){
					LOGGER.debug("Nombre y Apellidos");
					NombreApellidos nomApell = recibo.getNombreApellidos();
					
					reciboImpagado.setApellido1(nomApell.getApellido1());
					if(nomApell.getApellido2() != null)
						reciboImpagado.setApellido2(nomApell.getApellido2());
					reciboImpagado.setNombre(nomApell.getNombre());
				}else{
					LOGGER.debug("Razon Social");
					RazonSocial razonSocial = (RazonSocial) recibo.getRazonSocial();
					reciboImpagado.setRazonsocial(razonSocial.getRazonSocial());
				}
				if(recibo.getColectivo()!=null){
					LOGGER.debug("Colectivo");
					Colectivo col = recibo.getColectivo();
					
					reciboImpagado.setColectivocodinterno(col.getCodigoInterno());
					reciboImpagado.setColectivodc(new BigDecimal(col.getDigitoControl()));
					reciboImpagado.setColectivolocalidad(col.getLocalidad());
					reciboImpagado.setColectivoreferencia(col.getReferencia());
					
				}else if(recibo.getIndividual()!=null){
					LOGGER.debug("Individual atributos comunes: referencia,dc,tiporef,codinterno,anuladarefundida,pcts");
					Individual ind = recibo.getIndividual();
					
					reciboImpagado.setIndividualcodinterno(ind.getCodigoInterno());
					reciboImpagado.setIndividualdc(new BigDecimal(ind.getDigitoControl()));
					reciboImpagado.setIndividuallocalidad(ind.getLocalidad());
					reciboImpagado.setIndividualreferencia(ind.getReferencia());

					if(ind.getTipoReferencia()!=null){
						reciboImpagado.setIndividualtiporeferencia(new Character(ind.getTipoReferencia().toString().charAt(0)));
					}
					
				}else if(recibo.getPoliza()!=null){
					LOGGER.debug("Poliza");
					Poliza pol = recibo.getPoliza();
					reciboImpagado.setPolizaRefColectivo(pol.getReferenciaColectivo());
					reciboImpagado.setPolizaDcColectivo(new BigDecimal(pol.getDigitoControlColectivo()));
					reciboImpagado.setPolizaCodintColectivo(pol.getCodigoInternoColectivo());
					reciboImpagado.setPolizaLocalidad(pol.getLocalidad());

					refPoliza = pol.getReferenciaPoliza();
					reciboImpagado.setPolizaRefPoliza(refPoliza);
					reciboImpagado.setPolizaDcPoliza(new BigDecimal(pol.getDigitoControlPoliza()));
					reciboImpagado.setPolizaCodintPoliza(pol.getCodigoInternoPoliza());
					
					if(pol.getTipoReferencia()!=null){
						reciboImpagado.setPolizaTipoReferencia(pol.getTipoReferencia().toString().charAt(0));	
					}
					
					//Como ya tenemos la referencia de la póliza, calculamos los % de comisión
					arrayPorcentajes = utilidadesComisionesDao.obtenerPorcentajesComision(refPoliza, fecha);
					porComEntidad = arrayPorcentajes[0];
					porComES = arrayPorcentajes[1];
					esPorcentajesValidos = utilidadesComisionesDao.validarArrayPorcentajesComision(arrayPorcentajes);
					LOGGER.debug("Porcentaje Entidad = " + porComEntidad + " | Porcentaje E-S = " + porComES);
				}

				GastosExternos gastos = recibo.getGastosExternos();
				if(gastos!=null){
					LOGGER.debug("Gastos externos");
					
					LOGGER.debug("Cobro Actual");
					CobroActual cobroActual = gastos.getCobroActual();
					
					reciboImpagado.setCaGastoscomisiones(cobroActual.getGastosComisiones());
					reciboImpagado.setCaGastosentidad(cobroActual.getGastosEntidad());
					reciboImpagado.setCaImporterecibo(cobroActual.getImporteCobroRecibido());
					reciboImpagado.setCaTotalgastos(cobroActual.getTotalGastos());
					
					LOGGER.debug("Pendiente Abono");
					PendienteAbono pendienteAbono = gastos.getPendienteAbono();
					reciboImpagado.setPaGastoscomisiones(pendienteAbono.getGastosComisiones());
					reciboImpagado.setPaGastosentidad(pendienteAbono.getGastosEntidad());
					reciboImpagado.setPaTotalgastos(pendienteAbono.getTotalGastos());
					
					LOGGER.debug("Pendiente Abono Recibo Impagado");
					PendienteAbonoReciboImpagado pendienteAbonoReciboI = gastos.getPendienteAbonoReciboImpagado();
					reciboImpagado.setPariGastoscomisiones(pendienteAbonoReciboI.getGastosComisiones());
					reciboImpagado.setPariGastosentidad(pendienteAbonoReciboI.getGastosEntidad());
					reciboImpagado.setPariImportepdte(pendienteAbonoReciboI.getImporteSaldoPendiente());
					reciboImpagado.setPariTotalgastos(pendienteAbonoReciboI.getTotalGastos());
				}
				
				GastosEntidad gastosEnt = recibo.getGastosEntidad();
				if(gastosEnt!=null){
					LOGGER.debug("Gastos entidad");
					
					PendienteAbonoReciboImpagado1 pendAbonoReciboImpagado1 = gastosEnt.getPendienteAbonoReciboImpagado1();
					reciboImpagado.setPari1ComisMediador(pendAbonoReciboImpagado1.getComisionesMediador());
					reciboImpagado.setPari1GastosAdminEntidad(pendAbonoReciboImpagado1.getGastosAdminEntidad());
					reciboImpagado.setPari1GastosAdqEntidad(pendAbonoReciboImpagado1.getGastosAdqEntidad());
					reciboImpagado.setPari1ImporteSaldoPendiente(pendAbonoReciboImpagado1.getImporteSaldoPendiente());
					reciboImpagado.setPari1TotalGastos(pendAbonoReciboImpagado1.getTotalGastos());
					
					//Campos calculados
					if(esPorcentajesValidos){
						BigDecimal pari1ComisMediadorEnt = pendAbonoReciboImpagado1.getComisionesMediador().multiply(porComEntidad).divide(bigDecimalCien).setScale(2, BigDecimal.ROUND_FLOOR);
						reciboImpagado.setPari1ComisMediadorEnt(pari1ComisMediadorEnt);
						BigDecimal pari1ComisMediadorEsmed = pendAbonoReciboImpagado1.getComisionesMediador().subtract(pari1ComisMediadorEnt);
						reciboImpagado.setPari1ComisMediadorEsmed(pari1ComisMediadorEsmed);
						LOGGER.debug("pari1ComisMediador = " + pendAbonoReciboImpagado1.getComisionesMediador());
						LOGGER.debug("pari1ComisMediadorEnt = " + pari1ComisMediadorEnt);
						LOGGER.debug("pari1ComisMediadorEsmed = " + pari1ComisMediadorEsmed);
					}

					CobroActual1 cobroActual1 = gastosEnt.getCobroActual1();
					reciboImpagado.setCa1ComisMediador(cobroActual1.getComisionesMediador());
					reciboImpagado.setCa1GastosAdminEntidad(cobroActual1.getGastosAdminEntidad());
					reciboImpagado.setCa1GastosAdqEntidad(cobroActual1.getGastosAdqEntidad());
					reciboImpagado.setCa1ImporteCobroRecibido(cobroActual1.getImporteCobroRecibido());
					reciboImpagado.setCa1TotalGastos(cobroActual1.getTotalGastos());

					//Campos calculados
					if(esPorcentajesValidos){
						BigDecimal ca1ComisMediadorEnt = cobroActual1.getComisionesMediador().multiply(porComEntidad).divide(bigDecimalCien).setScale(2, BigDecimal.ROUND_FLOOR);
						reciboImpagado.setCa1ComisMediadorEnt(ca1ComisMediadorEnt);
						BigDecimal ca1ComisMediadorEsmed = cobroActual1.getComisionesMediador().subtract(ca1ComisMediadorEnt);
						reciboImpagado.setCa1ComisMediadorEsmed(ca1ComisMediadorEsmed);
						LOGGER.debug("ca1ComisMediador = " + cobroActual1.getComisionesMediador());
						LOGGER.debug("ca1ComisMediadorEnt = " + ca1ComisMediadorEnt);
						LOGGER.debug("ca1ComisMediadorEsmed = " + ca1ComisMediadorEsmed);
					}
					
					
					//Pendiente de abono
					PendienteAbono1 pendienteAbono1 = gastosEnt.getPendienteAbono1();
					reciboImpagado.setPa1ComisMediador(pendienteAbono1.getComisionesMediador());
					reciboImpagado.setPa1GastosAdminEntidad(pendienteAbono1.getGastosAdminEntidad());
					reciboImpagado.setPa1GastosAdqEntidad(pendienteAbono1.getGastosAdqEntidad());
					reciboImpagado.setPa1TotalGastos(pendienteAbono1.getTotalGastos());
					
					//Campos calculados
					if(esPorcentajesValidos){
						BigDecimal pa1ComisMediadorEnt = pendienteAbono1.getComisionesMediador().multiply(porComEntidad).divide(bigDecimalCien).setScale(2, BigDecimal.ROUND_FLOOR);
						reciboImpagado.setPa1ComisMediadorEnt(pa1ComisMediadorEnt);
						BigDecimal pa1ComisMediadorEsmed = pendienteAbono1.getComisionesMediador().subtract(pa1ComisMediadorEnt);
						reciboImpagado.setPa1ComisMediadorEsmed(pa1ComisMediadorEsmed);
						LOGGER.debug("pa1ComisMediador = " + pendienteAbono1.getComisionesMediador());
						LOGGER.debug("pa1ComisMediadorEnt = " + pa1ComisMediadorEnt);
						LOGGER.debug("pa1ComisMediadorEsmed = " + pa1ComisMediadorEsmed);
					}
				}
				
				LOGGER.debug("Add del recibo impago al set de impagos");
				impagos.add(reciboImpagado);
				
			}
			fichero.setReciboImpagados(impagos);
			
			request.getSession().setAttribute("progressStatus", "UPLOADING");
			request.getSession().setAttribute("progress",80);
			
			LOGGER.debug("Procedemos a guardar en la BBDD el fichero completo");
			//TMR 30-05-2012 Facturacion
			this.saveOrUpdateFacturacion(fichero, usuario);
			
			LOGGER.debug("ID del fichero procesado: " +fichero.getId());
			
			request.getSession().setAttribute("progressStatus", "UPLOADING");
			request.getSession().setAttribute("progress",90);
			
			LOGGER.debug("end - importarYValidarFicheroImpagados");
			return fichero.getId();
			
		} catch (DAOException dao) {
			request.getSession().setAttribute("progressStatus", "FAILED");
			LOGGER.debug("Se ha producido un error al guardar en la BBDD el fichero", dao);
			throw new DAOException("Se ha producido un error al guardar en la BBDD el fichero", dao);
		} catch (Exception ex) {
			request.getSession().setAttribute("progressStatus", "FAILED");
			LOGGER.debug("Se ha producido un error en la creacion del fichero", ex);
			throw new DAOException("Se ha producido un error en la creacion del fichero", ex);
		}
	}

	
	//Sets de los DAO
	public void setFaseDao(IFaseDao faseDao) {
		this.faseDao = faseDao;
	}
	
	public void setUtilidadesComisionesDao(IUtilidadesComisionesDao utilidadesComisionesDao) {
		this.utilidadesComisionesDao = utilidadesComisionesDao;
	}
}