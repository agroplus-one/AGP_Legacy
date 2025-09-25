package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlObject;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.comisiones.Fase;
import com.rsi.agp.dao.tables.comisiones.Fichero;
import com.rsi.agp.dao.tables.comisiones.FicheroContenido;
import com.rsi.agp.dao.tables.comisiones.FormFicheroComisionesBean;
import com.rsi.agp.dao.tables.comisiones.comisiones.Comision;
import com.rsi.agp.dao.tables.comisiones.comisiones.ComisionAplicacion;
import com.rsi.agp.dao.tables.comisiones.comisiones.ComisionAplicacionCondicionParticular;
import com.rsi.agp.dao.tables.comisiones.comisiones.ComisionCondicionParticular;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMult;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMultContenido;
import com.rsi.agp.dao.tables.commons.Usuario;

import es.agroseguro.iTipos.NombreApellidos;
import es.agroseguro.iTipos.RazonSocial;
import es.agroseguro.recibos.comisiones.Aplicacion;
import es.agroseguro.recibos.comisiones.Aplicaciones;
import es.agroseguro.recibos.comisiones.Colectivo;
import es.agroseguro.recibos.comisiones.CondicionesParticulares;
import es.agroseguro.recibos.comisiones.DatosNuevos;
import es.agroseguro.recibos.comisiones.DatosNuevos1;
import es.agroseguro.recibos.comisiones.DatosRegularizados;
import es.agroseguro.recibos.comisiones.DatosRegularizados1;
import es.agroseguro.recibos.comisiones.DatosTotales;
import es.agroseguro.recibos.comisiones.DatosTotales1;
import es.agroseguro.recibos.comisiones.DatosTotalesAplicacion;
import es.agroseguro.recibos.comisiones.Individual;
import es.agroseguro.recibos.comisiones.Recibo;

public class ImportacionFicheroComisionesDao extends BaseDaoHibernate implements IImportacionFicherosComisionesDao {
	private static final Log LOGGER = LogFactory.getLog(ImportacionFicheroComisionesDao.class);
	
	private IFaseDao faseDao; 
	private IUtilidadesComisionesDao utilidadesComisionesDao;
	
	@Override
	public Long importarYValidarFicheroComisiones(XmlObject xmlObject,Usuario usuario,Character tipo,String nombreFic,HttpServletRequest request) throws DAOException {
		LOGGER.debug("init - importarYValidarFicheroComisiones");
		try {
			Set<Comision> listrecibos = new HashSet<Comision>();
			FicheroContenido ficheroContenido = new FicheroContenido();
			Comision reciboComisiones = null;
			
			Fichero fichero = new Fichero();
			LOGGER.debug("datos comunes del fichero");
			fichero.setUsuario(usuario);
			fichero.setTipofichero(tipo);
			fichero.setFechacarga(new Date());
			fichero.setNombrefichero(nombreFic);
			
			LOGGER.debug("casting de nuestro XMLObject al tipo Fase del nodo principal del XML");
			es.agroseguro.recibos.comisiones.FaseDocument FaseDocumento = (es.agroseguro.recibos.comisiones.FaseDocument) xmlObject;
			
			LOGGER.debug("contenido xml");
			ficheroContenido.setFichero(fichero);
			ficheroContenido.setContenido(xmlObject.xmlText());
			fichero.setFicheroContenido(ficheroContenido);

			LOGGER.debug("FASE comisiones");
			String fase = FaseDocumento.getFase().getFase() + "";
			Date fecha = FaseDocumento.getFase().getFechaEmisionRecibo().getTime();
			BigDecimal plan = new BigDecimal(FaseDocumento.getFase().getPlan());
			Fase faseFichero = new Fase();
			
			faseFichero.setFechaemision(fecha);
			faseFichero.setPlan(plan);
			faseFichero.setFase(fase);
			fichero.setFase(faseFichero);
			
			request.getSession().setAttribute("progressStatus", "UPLOADING");
			request.getSession().setAttribute("progress",60);
			
			LOGGER.debug("Recibos de comisiones. Size:" + FaseDocumento.getFase().getReciboArray().length);
			for(int i=0;i<FaseDocumento.getFase().getReciboArray().length;i++){
				Recibo recibo = FaseDocumento.getFase().getReciboArray(i);
				
				reciboComisiones = new Comision();
				reciboComisiones.setFichero(fichero);
				
				LOGGER.debug("Atributos comunes: linea, recibo, localidad, grupo negocio");
				reciboComisiones.setLinea(new BigDecimal(recibo.getLinea()));
				reciboComisiones.setRecibo(new BigDecimal(recibo.getRecibo()));
				reciboComisiones.setLocalidad(recibo.getLocalidad());
				reciboComisiones.setGruponegocio(new Character(recibo.getGrupoNegocio().toString().charAt(0)));
				
				LOGGER.debug("Atributos no comunes");
				if( recibo.getNombreApellidos()!= null){
					LOGGER.debug("Nombre y Apellidos");
					NombreApellidos nomApell = recibo.getNombreApellidos();
					
					reciboComisiones.setApellido1(nomApell.getApellido1());
					if(nomApell.getApellido2() != null)
						reciboComisiones.setApellido2(nomApell.getApellido2());
					reciboComisiones.setNombre(nomApell.getNombre());
				}else{
					LOGGER.debug("Razon Social");
					RazonSocial razonSocial = (RazonSocial) recibo.getRazonSocial();
					
					reciboComisiones.setRazonsocial(razonSocial.getRazonSocial());
				}
				
				if(recibo.getColectivo() != null){
					LOGGER.debug("Colectivo");
					Colectivo col = recibo.getColectivo();
					
					reciboComisiones.setColectivodc(new BigDecimal(col.getDigitoControl()));
					reciboComisiones.setColectivocodinterno(col.getCodigoInterno());
					reciboComisiones.setColectivoreferencia(col.getReferenciaColectivo());
				}else{
					LOGGER.debug("Individual atributos comunes: referencia,dc,tiporef,codinterno,anuladarefundida,pcts");
					Individual ind = recibo.getIndividual();
					
					if(ind.getAnuladaRefundida()!=null){
						reciboComisiones.setIndividualanuladarefundida(new Character(ind.getAnuladaRefundida().toString().charAt(0)));
					}
					
					reciboComisiones.setIndividualcodinterno(ind.getCodigoInterno());
					reciboComisiones.setIndividualdc(new BigDecimal(ind.getDigitoControl()));
					reciboComisiones.setIndividualpctogtoscomis(ind.getPorcentajeGtosComisAplic());
					reciboComisiones.setIndividualpctogtosent(ind.getPorcentajeGtosEntAplic());
					reciboComisiones.setIndividualreferencia(ind.getReferencia());
					
					if(ind.getTipoReferencia()!=null){
						reciboComisiones.setIndividualtiporef(new Character(ind.getTipoReferencia().toString().charAt(0)));
					}
					
					LOGGER.debug("Condiciones Particulares Individual. Marcas");
					if(ind.getCondicionesParticularesArray() != null){
						Set<ComisionCondicionParticular> marcas = new HashSet<ComisionCondicionParticular>();
						ComisionCondicionParticular marca = null;
						for(int j =0; j<ind.getCondicionesParticularesArray().length;j++){
							CondicionesParticulares cp = ind.getCondicionesParticularesArray(j);
							
							marca = new ComisionCondicionParticular();
							marca.setMarcacondiciones(new BigDecimal(cp.getMarcaCondicionesParticulares()));
							marca.setComision(reciboComisiones);
							
							marcas.add(marca);
						}
						reciboComisiones.setComisionCondicionParticulars(marcas);
					}
				}
				
				LOGGER.debug("Datos nuevos");
				if(recibo.getDatosNuevos() != null){
					DatosNuevos datosnuevos = recibo.getDatosNuevos();
					
					reciboComisiones.setDnComisiones(datosnuevos.getComisiones());
					reciboComisiones.setDnGastosextentidad(datosnuevos.getGastosExternosEntidad());
					reciboComisiones.setDnPrimabasecalculo(datosnuevos.getPrimaBaseCalculo());
					reciboComisiones.setDnTotal(datosnuevos.getTotal());
				}
				
				LOGGER.debug("Datos regularizados");
				if(recibo.getDatosRegularizados() != null){
					DatosRegularizados datosRegularizados = recibo.getDatosRegularizados();
					
					reciboComisiones.setDrComisiones(datosRegularizados.getComisiones());
					reciboComisiones.setDrGastosextentidad(datosRegularizados.getGastosExternosEntidad());
					reciboComisiones.setDrPrimabasecalculo(datosRegularizados.getPrimaBaseCalculo());
					reciboComisiones.setDrTotal(datosRegularizados.getTotal());
				}
				
				DatosTotales datosTotales = recibo.getDatosTotales();
				
				if(datosTotales!=null){
					LOGGER.debug("Datos totales");
					reciboComisiones.setDtComisiones(datosTotales.getComisiones());
					reciboComisiones.setDtGastospagados(datosTotales.getGastosPagados());
					reciboComisiones.setDtGastospendientes(datosTotales.getGastosPendientes());
					reciboComisiones.setDtGastosextentidad(datosTotales.getGastosExternosEntidad());
					reciboComisiones.setDtPrimabasecalculo(datosTotales.getPrimaBaseCalculo());
					reciboComisiones.setDtTotal(datosTotales.getTotal());
				}
				
				request.getSession().setAttribute("progressStatus", "UPLOADING");
				request.getSession().setAttribute("progress",70);
				
				LOGGER.debug("Nodo principal de aplicaciones [polizas]");
				if(recibo.getAplicaciones() != null){
					Aplicaciones aplicaciones = recibo.getAplicaciones();
					if(aplicaciones.getAplicacionArray() != null){
						LOGGER.debug("polizas. Size: " + aplicaciones.getAplicacionArray().length );
						Set<ComisionAplicacion> comisionesaplicacion = new HashSet<ComisionAplicacion>();
						
						ComisionAplicacion comisionaplicacion = null;
						request.getSession().setAttribute("progressStatus", "UPLOADING");
						request.getSession().setAttribute("progress",75);
						
						BigDecimal bigDecimalCien		= new BigDecimal("100.00");//Para comparar luego
						BigDecimal bigDecimalCero		= new BigDecimal("0.00");//Para comparar luego
						
						for(int j= 0; j< aplicaciones.getAplicacionArray().length; j++){
							
							Aplicacion aplicacion = aplicaciones.getAplicacionArray(j);
						
							BigDecimal[] arrayPorcentajes 	= utilidadesComisionesDao.obtenerPorcentajesComision(aplicacion.getReferencia(), fecha);
							BigDecimal porComEntidad 		= arrayPorcentajes[0];
							BigDecimal porComES 			= arrayPorcentajes[1];
							BigDecimal porComMax 			= arrayPorcentajes[2];
							LOGGER.debug("Porcentaje Entidad = " + porComEntidad + " | Porcentaje E-S = " + porComES);
							LOGGER.debug("Porcentaje Comision Máximo = " + porComMax);
							
							//Comprobamos que al menos uno de los porcentajes es distinto de cero
							boolean esPorcentajesValidos = utilidadesComisionesDao.validarArrayPorcentajesComision(arrayPorcentajes);
							
							comisionaplicacion = new ComisionAplicacion();
							
							comisionaplicacion.setComision(reciboComisiones);
							LOGGER.debug("Atributos comunes aplicacion: referencia,dc,tiporef,codigointerno,anulada refundada"); 
							if(aplicacion.getAnuladaRefundida() != null)								
								comisionaplicacion.setAnuladarefundida(new Character(aplicacion.getAnuladaRefundida().toString().charAt(0)));
							
							comisionaplicacion.setCodinterno(aplicacion.getCodigoInterno());
							comisionaplicacion.setReferencia(aplicacion.getReferencia());
							comisionaplicacion.setTiporeferencia(new Character(aplicacion.getTipoReferencia().toString().charAt(0)));
							comisionaplicacion.setDc(new BigDecimal(aplicacion.getDigitoControl()));
							
							if(aplicacion.getNombreApellidos() != null){
								LOGGER.debug("Nombre y Apellidos aplicacion");
								NombreApellidos nombreApellidos = aplicacion.getNombreApellidos();
								
								comisionaplicacion.setApellido1(nombreApellidos.getApellido1());
								if(nombreApellidos.getApellido2() != null)
									comisionaplicacion.setApellido2(nombreApellidos.getApellido2());
								comisionaplicacion.setNombre(nombreApellidos.getNombre());
							}else{
								LOGGER.debug("Razon Social aplicacion");
								RazonSocial razonSocial = (RazonSocial) aplicacion.getRazonSocial();
								
								comisionaplicacion.setRazonsocial(razonSocial.getRazonSocial());
							}
							
							LOGGER.debug("Datos nuevos aplicacion");
							if(aplicacion.getDatosNuevos() != null){
								DatosNuevos datosnuevos = aplicacion.getDatosNuevos();
								
								comisionaplicacion.setDnComisiones(datosnuevos.getComisiones());
								comisionaplicacion.setDnGastosextentidad(datosnuevos.getGastosExternosEntidad());
								comisionaplicacion.setDnPrimabasecalculo(datosnuevos.getPrimaBaseCalculo());
								comisionaplicacion.setDnTotal(datosnuevos.getTotal());
							}
							
							// 2015 y posteriores (opcional)
							if(aplicacion.getDatosNuevos1() != null){
								LOGGER.debug("Datos nuevos 1 aplicacion");
								DatosNuevos1 datosNuevos1 = aplicacion.getDatosNuevos1();
								
								comisionaplicacion.setDn1PrimaComercialNeta(datosNuevos1.getPrimaComercialNeta());
								comisionaplicacion.setDn1GastosAdminEntidad(datosNuevos1.getGastosAdminEntidad());
								comisionaplicacion.setDn1GastosAdqEntidad(datosNuevos1.getGastosAdqEntidad());
								comisionaplicacion.setDn1ComisionesMediador(datosNuevos1.getComisionesMediador());
								comisionaplicacion.setDn1Total(datosNuevos1.getTotal());
								
								//Campos calculados
								if(esPorcentajesValidos){									
									
									// Se calcula la comisión de la entidad y de la es mediadora a partir de la prima comercial neta para
									// tener en cuenta los posibles descuentos/recargos aplicados a la mediadora
									BigDecimal dn1ComisionesMediadorEnt = datosNuevos1.getPrimaComercialNeta().multiply(porComMax.divide(bigDecimalCien))
															   .multiply(porComEntidad).divide(bigDecimalCien)
															   .setScale(2, BigDecimal.ROUND_FLOOR);
									// La comisión del mediador correspondiente a la e-s mediadora es la comisión total menos la comision correspondiente a la entidad
									BigDecimal dn1ComisionesMediadorEsmed = datosNuevos1.getComisionesMediador().subtract(dn1ComisionesMediadorEnt).setScale(2, BigDecimal.ROUND_FLOOR);
									// Se establecen los valores obtenidos
									comisionaplicacion.setDn1ComisionesMediadorEnt(dn1ComisionesMediadorEnt);
									comisionaplicacion.setDn1ComisionesMediadorEsmed(dn1ComisionesMediadorEsmed);
									
									LOGGER.debug("dn1ComisionesMediador = " + datosNuevos1.getComisionesMediador());
									LOGGER.debug("dn1ComisionesMediadorEnt = " + dn1ComisionesMediadorEnt);
									LOGGER.debug("dn1ComisionesMediadorEsmed = " + dn1ComisionesMediadorEsmed);
									LOGGER.debug("prima comercial neta = " + datosNuevos1.getPrimaComercialNeta());
									
								}else{
									LOGGER.error("DatosNuevos1: Algunos campos no han sido calculados porque no se obtuvieron los porcentajes de entidad y E-S Mediadora");
								}
							}
							
							if(aplicacion.getDatosRegularizados() != null){
								LOGGER.debug("Datos regularizados aplicacion");
								DatosRegularizados datosRegularizados = aplicacion.getDatosRegularizados();
								
								comisionaplicacion.setDrComisiones(datosRegularizados.getComisiones());
								comisionaplicacion.setDrGastosextentidad(datosRegularizados.getGastosExternosEntidad());
								comisionaplicacion.setDrPrimabasecalculo(datosRegularizados.getPrimaBaseCalculo());
								comisionaplicacion.setDrTotal(datosRegularizados.getTotal());
							}
							
							// 2015 y posteriores (opcional)
							if(aplicacion.getDatosRegularizados1() != null){
								LOGGER.debug("Datos regularizados 1 aplicacion");
								DatosRegularizados1 datosRegularizados1 = aplicacion.getDatosRegularizados1();
								
								comisionaplicacion.setDr1PrimaComercialNeta(datosRegularizados1.getPrimaComercialNeta());
								comisionaplicacion.setDr1GastosAdminEntidad(datosRegularizados1.getGastosAdminEntidad());
								comisionaplicacion.setDr1GastosAdqEntidad(datosRegularizados1.getGastosAdqEntidad());
								comisionaplicacion.setDr1ComisionesMediador(datosRegularizados1.getComisionesMediador());
								comisionaplicacion.setDr1Total(datosRegularizados1.getTotal());
								
								//Campos calculados
								if(esPorcentajesValidos){
									
									BigDecimal dr1ComisionesMediadorEnt = datosRegularizados1.getPrimaComercialNeta().multiply(porComMax.divide(bigDecimalCien))
											   .multiply(porComEntidad).divide(bigDecimalCien)
											   .setScale(2, BigDecimal.ROUND_FLOOR);
									comisionaplicacion.setDr1ComisionesMediadorEnt(dr1ComisionesMediadorEnt);
									
									BigDecimal dr1ComisionesMediadorEsmed = datosRegularizados1.getComisionesMediador().subtract(dr1ComisionesMediadorEnt).setScale(2, BigDecimal.ROUND_FLOOR);
									comisionaplicacion.setDr1ComisionesMediadorEsmed(dr1ComisionesMediadorEsmed);
									
									LOGGER.debug("dr1ComisionesMediador = " + datosRegularizados1.getComisionesMediador());
									LOGGER.debug("dr1ComisionesMediadorEnt = " + dr1ComisionesMediadorEnt);
									LOGGER.debug("dr1ComisionesMediadorEsmed = " + dr1ComisionesMediadorEsmed);
								}else{
									LOGGER.error("DatosRegularizados1: Algunos campos no han sido calculados porque no se obtuvieron los porcentajes de entidad y E-S Mediadora");
								}
							}
							
							if(aplicacion.getDatosTotales() != null){
								LOGGER.debug("Datos totales aplicacion");								
								DatosTotalesAplicacion datos = aplicacion.getDatosTotales();
								
								comisionaplicacion.setDtComisiones(datos.getComisiones());
								comisionaplicacion.setDtGastospagados(datos.getPorcentajeGtosEntAplic());
								comisionaplicacion.setDtGastospendientes(datos.getPorcentajeGtosComisAplic());
								comisionaplicacion.setDtGastosextentidad(datos.getGastosExternosEntidad());
								comisionaplicacion.setDtPrimabasecalculo(datos.getPrimaBaseCalculo());
								comisionaplicacion.setDtTotal(datos.getTotal());
							}
							
							// 2015 y posteriores (opcional)
							if(aplicacion.getDatosTotales1() != null){
								LOGGER.debug("Datos totales 1 aplicacion");
								DatosTotales1 datos1 = aplicacion.getDatosTotales1();
								
								comisionaplicacion.setDt1PrimaComercialNeta(datos1.getPrimaComercialNeta());
								comisionaplicacion.setDt1GastosAdminEntidad(datos1.getGastosAdminEntidad());
								comisionaplicacion.setDt1GastosAdqEntidad(datos1.getGastosAdqEntidad());
								comisionaplicacion.setDt1ComisionesMediador(datos1.getComisionesMediador());
								comisionaplicacion.setDt1GastosAdminEntAbon(datos1.getGastosAdminEntidadAbon());
								comisionaplicacion.setDt1GastosAdqEntAbon(datos1.getGastosAdqEntidadAbon());
								comisionaplicacion.setDt1ComisionesMediadorAbon(datos1.getComisionesMediadorAbon());
								comisionaplicacion.setDt1GastosAdminEntPdte(datos1.getGastosAdminEntidadPdte());
								comisionaplicacion.setDt1GastosAdqEntPdte(datos1.getGastosAdqEntidadPdtes());
								comisionaplicacion.setDt1ComisionesMediadorPdte(datos1.getComisionesMediadorPdtes());
								
								// Pct de comisiones abonadas y pendientes con respecto al total de comisión
								//Inicializamos por si comisionesMediador viene a cero
								BigDecimal pctAbonSobreTotal = new BigDecimal("1.00");
								BigDecimal pctPdteSobreTotal = new BigDecimal("0.00");

								if(bigDecimalCero.compareTo(datos1.getComisionesMediador())!=0){
									pctAbonSobreTotal = datos1.getComisionesMediadorAbon().divide(datos1.getComisionesMediador(), 2, RoundingMode.FLOOR).setScale(2, BigDecimal.ROUND_FLOOR);
									pctPdteSobreTotal = new BigDecimal(1).subtract(pctAbonSobreTotal);
								}
								
								//Campos calculados
								if(esPorcentajesValidos){

									//Normal
									BigDecimal dt1ComisionesMediadorEnt = datos1.getPrimaComercialNeta().multiply(porComMax.divide(bigDecimalCien))
											   .multiply(porComEntidad).divide(bigDecimalCien)
											   .setScale(2, BigDecimal.ROUND_FLOOR);
									comisionaplicacion.setDt1ComisionesMediadorEnt(dt1ComisionesMediadorEnt);
									
									//En vez de porcentaje, restamos sobre el total para que no haya pérdida al redondear
									BigDecimal dt1ComisionesMediadorEsmed = datos1.getComisionesMediador().subtract(dt1ComisionesMediadorEnt).setScale(2, BigDecimal.ROUND_FLOOR);
									comisionaplicacion.setDt1ComisionesMediadorEsmed(dt1ComisionesMediadorEsmed);
									
									LOGGER.debug("dt1ComisionesMediador = " + datos1.getComisionesMediador());
									LOGGER.debug("dt1ComisionesMediadorEnt = " + dt1ComisionesMediadorEnt);
									LOGGER.debug("dt1ComisionesMediadorEsmed = " + dt1ComisionesMediadorEsmed);
									
									//Abonado
									BigDecimal dt1ComisionesMediadorAbonEnt = dt1ComisionesMediadorEnt.multiply(pctAbonSobreTotal).setScale(2, BigDecimal.ROUND_FLOOR);;
									comisionaplicacion.setDt1ComisMediadorAbonEnt(dt1ComisionesMediadorAbonEnt);
									
									//En vez de porcentaje, restamos sobre el total para que no haya pérdida al redondear
									BigDecimal dt1ComisionesMediadorAbonEsmed = datos1.getComisionesMediadorAbon().subtract(dt1ComisionesMediadorAbonEnt).setScale(2, BigDecimal.ROUND_FLOOR);
									comisionaplicacion.setDt1ComisMediadorAbonEsmed(dt1ComisionesMediadorAbonEsmed);
									
									LOGGER.debug("dt1ComisionesMediador = " + datos1.getComisionesMediadorAbon());
									LOGGER.debug("dt1ComisionesMediadorAbonEnt = " + dt1ComisionesMediadorAbonEnt);
									LOGGER.debug("dt1ComisionesMediadorAbonEsmed = " + dt1ComisionesMediadorAbonEsmed);
									
									//Pendiente
									BigDecimal dt1ComisionesMediadorEntPdte = dt1ComisionesMediadorEnt.multiply(pctPdteSobreTotal).setScale(2, BigDecimal.ROUND_FLOOR);;
									comisionaplicacion.setDt1ComisMediadorPdteEnt(dt1ComisionesMediadorEntPdte);
									
									//En vez de porcentaje, restamos sobre el total para que no haya pérdida al redondear
									BigDecimal dt1ComisMediadorPdteEsmed = datos1.getComisionesMediadorPdtes().subtract(dt1ComisionesMediadorEntPdte);
									comisionaplicacion.setDt1ComisMediadorPdteEsmed(dt1ComisMediadorPdteEsmed);
									
									LOGGER.debug("dt1ComisionesMediadorPdtes = " + datos1.getComisionesMediador());
									LOGGER.debug("dt1ComisionesMediadorEntPdte = " + dt1ComisionesMediadorEntPdte);
									LOGGER.debug("dt1ComisMediadorPdteEsmed = " + dt1ComisMediadorPdteEsmed);
								}else{
									LOGGER.error("DatosTotales1: Algunos campos no han sido calculados porque no se obtuvieron los porcentajes de entidad y E-S Mediadora");
								}
							}
							
							if(aplicacion.getCondicionesParticularesArray() != null){
								LOGGER.debug("Recibo Comisiones Aplicacion Marca");
								Set<ComisionAplicacionCondicionParticular> marcas = new HashSet<ComisionAplicacionCondicionParticular>();
								ComisionAplicacionCondicionParticular marca = null;
								for(int z= 0 ; z<aplicacion.getCondicionesParticularesArray().length;z++){
									marca = new ComisionAplicacionCondicionParticular();
									CondicionesParticulares marcacondicionespartscoms = aplicacion.getCondicionesParticularesArray(z);
									
									marca.setMarcacondiciones(new BigDecimal(marcacondicionespartscoms.getMarcaCondicionesParticulares()));
									marca.setComisionAplicacion(comisionaplicacion);
									
									marcas.add(marca);
								}
								comisionaplicacion.setComisionAplicacionCondicionParticulars(marcas);
							}
							
							LOGGER.debug("Aï¿½adimos la aplicacion al Set de aplicaciones");
							comisionesaplicacion.add(comisionaplicacion);
						}
						
						LOGGER.debug("Aï¿½adimos las aplicaciones al Set de recibos aplicaciones");
						reciboComisiones.setComisionAplicacions(comisionesaplicacion);
					}
				}
				LOGGER.debug("Aï¿½adimos los recibos de comisiones al Set");
				listrecibos.add(reciboComisiones);
			}
			
			request.getSession().setAttribute("progressStatus", "UPLOADING");
			request.getSession().setAttribute("progress",80);
			
			LOGGER.debug("Aï¿½adimos nuestro set de recibos de comisiones  al Set de recibos del fichero");
			fichero.setReciboComisioneses(listrecibos);
			
			Fase faseaux = faseDao.isExistFase(fase, plan);
			if (faseaux.getId() == null){
				fichero.setFase(faseFichero);
				faseDao.saveFaseFichero(faseFichero);
			} 
			else {
				fichero.setFase(faseaux);
			}
			
			//TMR 30-05-2012 Facturacion
			this.saveOrUpdateFacturacion(fichero, usuario);
			
			Long idFichero = fichero.getId();
			LOGGER.debug("ID del fichero procesado: " +idFichero);
			
			xmlObject = null;
			request.getSession().setAttribute("progressStatus", "UPLOADING");
			request.getSession().setAttribute("progress",85);
			LOGGER.debug("end - importarYValidarFicheroComisiones");
			return idFichero;
			
		}catch (DAOException dao) {
			request.getSession().setAttribute("progressStatus", "FAILED");
			LOGGER.debug("Se ha producido un error al guardar en la BBDD el fichero", dao);
			throw new DAOException("Se ha producido un error al guardar en la BBDD el fichero",dao);
		} catch (Exception ex) {
			request.getSession().setAttribute("progressStatus", "FAILED");
			LOGGER.debug("Se ha producido un error en la creacion del fichero", ex);
			throw new DAOException("Se ha producido un error en la creacion del fichero",ex);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Fichero> list(FormFicheroComisionesBean ffcb,Character tipo) throws DAOException {
		
		List<Fichero> result;
		
		LOGGER.debug("init - list");
		Session session = obtenerSession();

		try {
			
			Criteria criteria = session.createCriteria(Fichero.class);
			criteria.createAlias("fase", "fase");
			
			if(FiltroUtils.noEstaVacio(ffcb.getFechaAceptacion()))
				criteria.add(Restrictions.eq("fechaaceptacion", ffcb.getFechaAceptacion()));
			if(FiltroUtils.noEstaVacio(ffcb.getFechaCarga()))
				criteria.add(Restrictions.eq("fechacarga", ffcb.getFechaCarga()));
			if(FiltroUtils.noEstaVacio(ffcb.getFechaCierre())){
				criteria.createAlias("fase.cierre","cierre");
				criteria.add(Restrictions.eq("cierre.fechacierre", ffcb.getFechaCierre()));
			}
			if(FiltroUtils.noEstaVacio(ffcb.getFechaEmision())){
				criteria.add(Restrictions.eq("fase.fechaemision", ffcb.getFechaEmision()));
			}
			if(FiltroUtils.noEstaVacio(ffcb.getNombreFichero()))
				criteria.add(Restrictions.ilike("nombrefichero", ffcb.getNombreFichero(), MatchMode.ANYWHERE));
			if(tipo != null)
				criteria.add(Restrictions.eq("tipofichero", tipo));
			if(ffcb.getReglamentoProduccionEmitidaSituacion().getCodigo()	!=null){
				
				criteria.createAlias("ficheroReglamentos", "ficheroReglamentos").
				createAlias("ficheroReglamentos.reglamentoProduccionEmitidaSituacions", "reglamentoAplicacions");
				
				criteria.add(Restrictions.eq("reglamentoAplicacions.codigo",ffcb.getReglamentoProduccionEmitidaSituacion().getCodigo()));
			}
			//ASF: Anhado los filtros para "plan" y "fase".
			if (!StringUtils.nullToString(ffcb.getPlan()).equals("")){
				criteria.add(Restrictions.eq("fase.plan", ffcb.getPlan()));
			}
			if (!StringUtils.nullToString(ffcb.getFase()).equals("")){
				criteria.add(Restrictions.eq("fase.fase", ffcb.getFase()));
			}

			criteria.setProjection(Projections.projectionList().add(Projections.property("id"))
					.add(Projections.property("usuario")).add(Projections.property("fase"))
					.add(Projections.property("nombrefichero")).add(Projections.property("tipofichero"))
					.add(Projections.property("fechacarga")).add(Projections.property("fechaaceptacion")));
			
			criteria.addOrder(Order.desc("fechacarga"));
			
			LOGGER.debug("end - list");
			List<Object[]> aux = criteria.list();
			result = new ArrayList<Fichero>(aux.size());
			
			for (Object[] obj : aux) {
				if (obj != null && obj.length == 7) {
					Fichero fichero = new Fichero();
					fichero.setId((Long) obj[0]);
					fichero.setUsuario((Usuario) obj[1]);
					fichero.setFase((Fase) obj[2]);
					fichero.setNombrefichero((String) obj[3]);
					fichero.setTipofichero((Character) obj[4]);
					fichero.setFechacarga((Date) obj[5]);
					fichero.setFechaaceptacion((Date) obj[6]);
					result.add(fichero);
				}
			}
			
			return result;
			
		} catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: ", ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD", ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<FicheroMult> listDeuda(FormFicheroComisionesBean ffcb) throws DAOException {
		LOGGER.debug("init - list");
		Calendar c = Calendar.getInstance();
		Date dt;
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(FicheroMult.class);
			
			if(FiltroUtils.noEstaVacio(ffcb.getFechaAceptacion())){
				c.setTime(ffcb.getFechaAceptacion()); 
				c.add(Calendar.DATE, 1);
				dt = c.getTime();
				criteria.add(Restrictions.ge("fechaAceptacion", ffcb.getFechaAceptacion()));
				criteria.add(Restrictions.lt("fechaAceptacion", dt));
			}
			if(FiltroUtils.noEstaVacio(ffcb.getFechaCarga())){
				c.setTime(ffcb.getFechaCarga()); 
				c.add(Calendar.DATE, 1);
				dt = c.getTime();
				criteria.add(Restrictions.ge("fechaCarga", ffcb.getFechaCarga()));
				criteria.add(Restrictions.lt("fechaCarga", dt));
			}
			if(FiltroUtils.noEstaVacio(ffcb.getNombreFichero()))
				criteria.add(Restrictions.ilike("nombreFichero", ffcb.getNombreFichero(),MatchMode.ANYWHERE));
			
			criteria.addOrder(Order.desc("fechaCarga"));
			
			LOGGER.debug("end - listDeuda");
			return criteria.list();
		
		} catch (Exception e) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: ", e);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD", e);
		}
	}

	@Override
	public Integer ficheroImportado(String nomFichero) throws DAOException {
		LOGGER.debug("init - ficheroImportado");
		Session session = obtenerSession();
		try {
			
			Criteria criteria = session.createCriteria(Fichero.class);
			
			criteria.add(Restrictions.eq("nombrefichero", nomFichero.trim()).ignoreCase());
			criteria.setProjection(Projections.rowCount());

			LOGGER.debug("end - ficheroImportado");
			return (Integer) criteria.uniqueResult();
			
		} catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD", ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}

	public FicheroContenido getFicheroContenido(Long idfichero) throws DAOException{
		FicheroContenido ficheroCont;
		try {
			ficheroCont = (FicheroContenido) this.getObject(FicheroContenido.class, idfichero);
		} catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD", ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD", ex);
		}
		return ficheroCont;
	}
	
	public FicheroMultContenido getFicheroMultContenido(Long idfichero) throws DAOException{
		Session session = obtenerSession();
		FicheroMultContenido fichero = new FicheroMultContenido();
		try {
			Criteria criteria = session.createCriteria(FicheroMultContenido.class);
			criteria.add(Restrictions.eq("id",idfichero));
			if (criteria.list().size() > 0)
				fichero =  (FicheroMultContenido) criteria.list().get(0);
		}
		catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD", ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD", ex);
		}
		return fichero;
	}
	
	public int obtenerFicherosFase(Long idfase) throws DAOException{
		Session session = obtenerSession();
		
		try {
			Criteria criteria = session.createCriteria(Fichero.class);
			criteria.createAlias("fase", "fase");
			
			criteria.add(Restrictions.eq("fase.id",idfase));
			
			return criteria.list().size();			
		}
		catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD", ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD", ex);
		}		
	}
	
	/**
	 * Borra de la tabla de informes de recibos que corresponda los registros con el idfichero pasado por parámetro
	 */
	public void deleteFromTablaInformesRecibos(Long id, BigDecimal codPlan) throws DAOException {

		String sql = null;
		Session session = obtenerSession();
		LOGGER.debug("codPlan = " + codPlan);
		if(codPlan!=null && codPlan.intValue()<2015){
			try{
				sql = "DELETE TB_COMS_INFORMES_RECIBOS WHERE IDFICHERO = " + id;
				session.createSQLQuery(sql).executeUpdate();
				LOGGER.debug("SQL de borrado de informes: "+ sql);
			}catch (Exception excepcion) {
				LOGGER.error("Se ha producido un error al borrar los datos de la tabla TB_COMS_INFORMES_RECIBOS ", excepcion);
				throw new DAOException("Se ha producido un error al borrar los datos de la tabla TB_COMS_INFORMES_RECIBOS", excepcion);
			}
		}else{
			try{
				sql = "DELETE TB_COMS_INFORMES_RECIBOS_2015 WHERE IDFICHERO = " + id;
				session.createSQLQuery(sql).executeUpdate();
				LOGGER.debug("SQL de borrado de informes: "+ sql);
			}catch (Exception excepcion) {
				LOGGER.error("Se ha producido un error al borrar los datos de la tabla TB_COMS_INFORMES_RECIBOS_2015 ", excepcion);
				throw new DAOException("Se ha producido un error al borrar los datos de la tabla TB_COMS_INFORMES_RECIBOS_2015", excepcion);
			}
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