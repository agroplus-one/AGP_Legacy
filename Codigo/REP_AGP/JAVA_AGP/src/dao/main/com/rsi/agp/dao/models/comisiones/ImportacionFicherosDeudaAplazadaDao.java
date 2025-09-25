package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlObject;
import org.hibernate.Hibernate;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.DetalleAbonoColectivo;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.DetalleAbonoIndividual;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.DetalleAbonoPoliza;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FaseMult;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMult;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMultContenido;
import com.rsi.agp.dao.tables.commons.Usuario;

import es.agroseguro.iTipos.NombreApellidos;
import es.agroseguro.iTipos.RazonSocial;
import es.agroseguro.recibos.comisionesCobroDeudaAplazada.Asegurado;
import es.agroseguro.recibos.comisionesCobroDeudaAplazada.Colectivo;
import es.agroseguro.recibos.comisionesCobroDeudaAplazada.DetallesAbonos;
import es.agroseguro.recibos.comisionesCobroDeudaAplazada.FaseAbono;
import es.agroseguro.recibos.comisionesCobroDeudaAplazada.Gastos;
import es.agroseguro.recibos.comisionesCobroDeudaAplazada.Individual;
import es.agroseguro.recibos.comisionesCobroDeudaAplazada.Poliza;
import es.agroseguro.recibos.comisionesCobroDeudaAplazada.Polizas;


public class ImportacionFicherosDeudaAplazadaDao extends BaseDaoHibernate implements IImportacionFicherosDeudaAplazadaDao {

	private static final Log LOGGER = LogFactory.getLog(ImportacionFicherosDeudaAplazadaDao.class);

	private IPolizaDao polizaDao;
	private IUtilidadesComisionesDao utilidadesComisionesDao;

	@Override
	public Long importarYValidarFicheroDeudaAplazada(XmlObject xmlObject,Usuario usuario, Character tipo, String nombre,HttpServletRequest request) throws DAOException {
		
		LOGGER.debug("init - importarYValidarFicheroDeudaAplazada");
		BigDecimal bigDecimalCien = new BigDecimal("100.00");//Para comparar luego
		
		FicheroMultContenido ficheroMultContenido = new FicheroMultContenido();
		
		FicheroMult ficMult = new FicheroMult();
		LOGGER.debug("datos comunes del ficheroMult");
		ficMult.setCodusuario(usuario.getCodusuario());
		ficMult.setFechaAceptacion(new Date());
		ficMult.setFechaCarga(new Date());
		ficMult.setNombreFichero(nombre);
		ficMult.setTipoFichero(tipo);
		Set<FaseMult> coleccionFaseMult = new HashSet<FaseMult>();
		
		LOGGER.debug("contenido xml");
		ficheroMultContenido.setContenido(Hibernate.createClob(xmlObject.xmlText()));
		ficheroMultContenido.setFicheroMult(ficMult);

		LOGGER.debug("casting de nuestro XMLObject al tipo Fase del nodo principal del XML");
		es.agroseguro.recibos.comisionesCobroDeudaAplazada.FasesDocument faseDocumento = (es.agroseguro.recibos.comisionesCobroDeudaAplazada.FasesDocument) xmlObject;
	
		FaseAbono[] faseAbonArray = faseDocumento.getFases().getFaseAbonoArray();
			
		//Inicio bucle 1
		for (int i = 0; i < faseAbonArray.length; i++) {
				DetallesAbonos[] detallesAbonosArray = faseAbonArray[i].getDetallesAbonosArray();
				Set<DetalleAbonoIndividual> colDetalleAbonoIndividual = new HashSet<DetalleAbonoIndividual>();
				Set<DetalleAbonoColectivo> colDetalleAbonoColectivo = new HashSet<DetalleAbonoColectivo>();
				Integer numFase = faseAbonArray[i].getFase();
				Integer plan = faseAbonArray[i].getPlan();
				
				FaseMult faseMult = new FaseMult();
				faseMult.setFase(numFase);
				faseMult.setFicheroMult(ficMult);
				faseMult.setPlan(plan);
				
				//Inicio bucle 2
				for (int j = 0; j < detallesAbonosArray.length; j++) {
						DetalleAbonoIndividual detalleAbonoIndividualAux = null;
						DetalleAbonoColectivo detalleAbonoColectivoAux = null;
						Set<DetalleAbonoPoliza> colDetalleAbonoPoliza = new HashSet<DetalleAbonoPoliza>();
						DetallesAbonos detallesAbonos = detallesAbonosArray[j];
						Individual individual = detallesAbonos.getIndividual();
		
						Polizas polizas = null;
						
						if(individual!=null){
							detalleAbonoIndividualAux = new DetalleAbonoIndividual();
							detalleAbonoIndividualAux.setFaseMult(faseMult);
							detalleAbonoIndividualAux.setRefPoliza(individual.getReferencia());
							detalleAbonoIndividualAux.setDcRefPoliza(String.valueOf(individual.getDigitoControl()).charAt(0));
							RazonSocial indRazonSocial = individual.getRazonSocial();
							
							if(indRazonSocial!=null){
								detalleAbonoIndividualAux.setRazonSocial(indRazonSocial.getRazonSocial());
							}else{
								NombreApellidos indNombreApellidos = individual.getNombreApellidos();
								
								if(indNombreApellidos!=null){
									detalleAbonoIndividualAux.setNombre(indNombreApellidos.getNombre());
									detalleAbonoIndividualAux.setApellido1(indNombreApellidos.getApellido1());
									detalleAbonoIndividualAux.setApellido2(indNombreApellidos.getApellido2());
								}
							}
		
							polizas = individual.getPolizas();

						}else{
							detalleAbonoColectivoAux = new DetalleAbonoColectivo();
							detalleAbonoColectivoAux.setFaseMult(faseMult);
							Colectivo colectivo = detallesAbonos.getColectivo();
							
							if(colectivo!=null){
								detalleAbonoColectivoAux.setCodigoInterno(colectivo.getCodigoInterno());
								detalleAbonoColectivoAux.setDcRefColectivo(String.valueOf(colectivo.getDigitoControl()).charAt(0));
								detalleAbonoColectivoAux.setRefColectivo(colectivo.getReferencia());
								RazonSocial colRazonSocial = colectivo.getRazonSocial();
								
								if(colRazonSocial!=null){
									detalleAbonoColectivoAux.setRazonSocial(colRazonSocial.getRazonSocial());
								}else{
									NombreApellidos colNombreApellidos = colectivo.getNombreApellidos();
									
									if(colNombreApellidos!=null){
										detalleAbonoColectivoAux.setNombre(colNombreApellidos.getNombre());
										detalleAbonoColectivoAux.setApellido1(colNombreApellidos.getApellido1());
										detalleAbonoColectivoAux.setApellido2(colNombreApellidos.getApellido2());
									}
								}
								polizas = colectivo.getPolizas();
							}
						}
						
						Poliza[] polizasArray = polizas.getPolizaArray();
						
						//Inicio bucle 3
						for (int k = 0; k < polizasArray.length; k++) {
								DetalleAbonoPoliza dap = new DetalleAbonoPoliza();
								Poliza polizaFic = polizasArray[k];
								dap.setCodigoInterno(polizaFic.getCodigoInterno());
								dap.setLinea(new Long(polizaFic.getLinea()));
								dap.setReferencia(polizaFic.getReferencia());
								dap.setDc(String.valueOf(polizaFic.getDigitoControl()).charAt(0));
								dap.setTipoReferencia(polizaFic.getTipoReferencia().toString().charAt(0));
								dap.setRecibo(new BigDecimal(polizaFic.getRecibo()));
								Asegurado asegurado = polizaFic.getAsegurado();
								RazonSocial asegRs = asegurado.getRazonSocial();
								
								if(asegRs!=null){
									dap.setRazonSocialAseg(asegRs.getRazonSocial());
								}else{
									NombreApellidos asegColectivo = asegurado.getNombreApellidos();
									
									if(asegColectivo!=null){
										dap.setNombreAseg(asegColectivo.getNombre());
										dap.setApellido1Aseg(asegColectivo.getApellido1());
										dap.setApellido2Aseg(asegColectivo.getApellido2());
									}
								}
								
								com.rsi.agp.dao.tables.poliza.Poliza polizaAgro = polizaDao.getPolizaByReferencia(dap.getReferencia(), dap.getTipoReferencia());
								dap.setPoliza(polizaAgro);
								
								Gastos gastos = polizaFic.getGastos();
								dap.setComisionMediador(gastos.getComisionesMediador());
								dap.setComisionMediadorAbon(gastos.getComisionesMediadorAbon());
								dap.setGastosAdmEntidad(gastos.getGastosAdminEntidad());
								dap.setGastosAdmEntidadAbon(gastos.getGastosAdminEntidadAbon());
								dap.setGastosAdqEntidad(gastos.getGastosAdqEntidad());
								dap.setGastosAdqEntidadAbon(gastos.getGastosAdqEntidadAbon());
								dap.setPrimaComercialNeta(gastos.getPrimaComercialNeta());
								dap.setTotal(gastos.getTotal());
								dap.setTotalAbonados(gastos.getTotalAbonados());
								
								//Campos calculados:
								BigDecimal[] arrayPorcentajes 	= utilidadesComisionesDao.obtenerPorcentajesComision(dap.getReferencia(), null);
								BigDecimal porComEntidad 		= arrayPorcentajes[0];
								BigDecimal porComES 			= arrayPorcentajes[1];
								BigDecimal porComMax 			= arrayPorcentajes[2];
								LOGGER.debug("Porcentaje Entidad = " + porComEntidad + " | Porcentaje E-S = " + porComES);
								LOGGER.debug("Porcentaje Comision Máximo = " + porComMax);
								
								//Comprobamos que al menos uno de los porcentajes es distinto de cero
								boolean esPorcentajesValidos = utilidadesComisionesDao.validarArrayPorcentajesComision(arrayPorcentajes);
								
								if(esPorcentajesValidos){
									
									// Pct de comisiones abonadas con respecto al total de comisión
									BigDecimal pctAbonSobreTotal = dap.getComisionMediadorAbon().divide(dap.getComisionMediador()).setScale(2, BigDecimal.ROUND_FLOOR);
									
									BigDecimal comisionMediadorEnt = dap.getPrimaComercialNeta().multiply(porComMax.divide(bigDecimalCien)).multiply(porComEntidad).divide(bigDecimalCien).setScale(2, BigDecimal.ROUND_FLOOR);
									dap.setComisionMediadorEnt(comisionMediadorEnt);
									
									// La comisión del mediador correspondiente a la e-s mediadora es la comisión total menos la comision correspondiente a la entidad
									BigDecimal comisionMediadorEsmed = dap.getComisionMediador().subtract(comisionMediadorEnt).setScale(2, BigDecimal.ROUND_FLOOR);
									dap.setComisionMediadorEsmed(comisionMediadorEsmed);
									
									//Abonado
									BigDecimal comisionMediadorAbonEnt = comisionMediadorEnt.multiply(pctAbonSobreTotal).setScale(2, BigDecimal.ROUND_FLOOR);;
									dap.setComisionMediadorAbonEnt(comisionMediadorAbonEnt);
									
									//En vez de porcentaje, restamos sobre el total para que no haya pérdida al redondear
									BigDecimal comisionesMediadorAbonEsmed = dap.getComisionMediadorAbon().subtract(comisionMediadorAbonEnt).setScale(2, BigDecimal.ROUND_FLOOR);
									dap.setComisionMediadorAbonEsmed(comisionesMediadorAbonEsmed);
								}
								
								//Arrastrado de otros bucles
								
								//añadir dap
								dap.setDetalleAbonoColectivo(detalleAbonoColectivoAux);
								dap.setDetalleAbonoIndividual(detalleAbonoIndividualAux);
								colDetalleAbonoPoliza.add(dap);
						}//Fin bucle 3
						
						if(detalleAbonoIndividualAux!=null){
							detalleAbonoIndividualAux.setDetalleAbonoPolizas(colDetalleAbonoPoliza);
							colDetalleAbonoIndividual.add(detalleAbonoIndividualAux);
						}

						if(detalleAbonoColectivoAux!=null){
							detalleAbonoColectivoAux.setDetalleAbonoPolizas(colDetalleAbonoPoliza);
							colDetalleAbonoColectivo.add(detalleAbonoColectivoAux);
						}
						
				}//Fin bucle 2
				//ir añadiendo las fases a la colección
				faseMult.setDetalleAbonoColectivos(colDetalleAbonoColectivo);
				faseMult.setDetalleAbonoIndividuals(colDetalleAbonoIndividual);
				coleccionFaseMult.add(faseMult);
		}//Fin bucle 1
		
		request.getSession().setAttribute("progressStatus", "UPLOADING");
		request.getSession().setAttribute("progress",80);
		
		ficMult.setFicheroMultContenido(ficheroMultContenido);
		ficMult.setFaseMults(coleccionFaseMult);

		this.saveOrUpdateFacturacion(ficMult, usuario);
		
		Long idFichero = ficMult.getId();
		LOGGER.debug("ID del ficMult procesado: " +idFichero);
		
		xmlObject = null;
		request.getSession().setAttribute("progressStatus", "UPLOADING");
		request.getSession().setAttribute("progress",85);
		LOGGER.debug("end - importarYValidarFicheroDeudaAplazada");
		return ficMult.getId();
	}

	
	//Sets de los DAO
	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}
	
	public void setUtilidadesComisionesDao(IUtilidadesComisionesDao utilidadesComisionesDao) {
		this.utilidadesComisionesDao = utilidadesComisionesDao;
	}
}