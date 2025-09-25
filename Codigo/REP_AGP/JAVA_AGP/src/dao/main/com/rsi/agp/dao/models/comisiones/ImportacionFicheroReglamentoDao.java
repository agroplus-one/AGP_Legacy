package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
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
import org.hibernate.Session;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.comisiones.Fase;
import com.rsi.agp.dao.tables.comisiones.Fichero;
import com.rsi.agp.dao.tables.comisiones.FicheroContenido;
import com.rsi.agp.dao.tables.comisiones.reglamento.ReglamentoProduccionEmitida;
import com.rsi.agp.dao.tables.comisiones.reglamento.ReglamentoProduccionEmitidaSituacion;
import com.rsi.agp.dao.tables.commons.Usuario;

import es.agroseguro.recibos.reglamentoProduccionEmitida.Colectivo;
import es.agroseguro.recibos.reglamentoProduccionEmitida.DatosEconomicos;
import es.agroseguro.recibos.reglamentoProduccionEmitida.DatosReglamento;
import es.agroseguro.recibos.reglamentoProduccionEmitida.IndividualAplicacion;
import es.agroseguro.recibos.reglamentoProduccionEmitida.Situacion;

public class ImportacionFicheroReglamentoDao extends BaseDaoHibernate implements IImportacionFicherosReglamentoDao {
	private static final Log LOGGER = LogFactory.getLog(ImportacionFicheroReglamentoDao.class);
	
	private IFaseDao faseDao;

	@Override
	public Long importarYValidarFicheroReglamento(XmlObject xmlObject, Usuario usuario, Character tipo, String nombre, HttpServletRequest request) throws DAOException{
		LOGGER.debug("init - importarYValidarFicheroReglamento");
		Fase faseFichero = new Fase();
		FicheroContenido ficheroContenido = new FicheroContenido();
		
		try {
			Fichero fichero = new Fichero();
			LOGGER.debug("datos comunes del fichero");
			fichero.setUsuario(usuario);
			fichero.setTipofichero(tipo);
			fichero.setFechacarga(new Date());
			fichero.setNombrefichero(nombre);

			LOGGER.debug("casting de nuestro XMLObject al tipo Fase del nodo principal del XML");
			es.agroseguro.recibos.reglamentoProduccionEmitida.FaseDocument temp = (es.agroseguro.recibos.reglamentoProduccionEmitida.FaseDocument) xmlObject;
			
			LOGGER.debug("FASE Reglamento");

			String fase = temp.getFase().getFase() + "";
			Date fecha = temp.getFase().getFechaEmisionRecibo().getTime();
			BigDecimal plan = new BigDecimal(temp.getFase().getPlan());
			
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
			
			LOGGER.debug("contenido xml");
			ficheroContenido.setFichero(fichero);
			ficheroContenido.setContenido(xmlObject.xmlText());
			fichero.setFicheroContenido(ficheroContenido);
			
			request.getSession().setAttribute("progressStatus", "UPLOADING");
			request.getSession().setAttribute("progress",60);
			
			IndividualAplicacion[] indApps =  temp.getFase().getIndividualAplicacionArray();
			ReglamentoProduccionEmitida regProdEmitida = null;
			Set<ReglamentoProduccionEmitida> regProdEmitidaSituacions = new HashSet<ReglamentoProduccionEmitida>();
			Situacion[] sits = null ;
			if(indApps.length > 0){
				for (IndividualAplicacion individualAplicacion : indApps) {
					
					regProdEmitida = new ReglamentoProduccionEmitida();
					regProdEmitida.setFichero(fichero);
					
					LOGGER.debug("Atributos comunes aplicacion: referencia,dc,tiporef,codigointerno,...");				
					regProdEmitida.setComisiones(individualAplicacion.getComisiones());
					regProdEmitida.setGastosextEnt(individualAplicacion.getGastosExternosEntidad());
					regProdEmitida.setGruponegocio(individualAplicacion.getGrupoNegocio().toString().charAt(0));
					regProdEmitida.setDc(new BigDecimal(individualAplicacion.getDigitoControl()));
					regProdEmitida.setCodigointerno(individualAplicacion.getCodigoInterno());
					regProdEmitida.setReferencia(individualAplicacion.getReferencia());
					regProdEmitida.setTiporecibo(individualAplicacion.getTipoRecibo().toString().charAt(0));
					regProdEmitida.setTiporeferencia(individualAplicacion.getTipoReferencia().toString().charAt(0));
					
					request.getSession().setAttribute("progressStatus", "UPLOADING");
					request.getSession().setAttribute("progress",70);
					
					regProdEmitida.setLinea(new BigDecimal(individualAplicacion.getLinea()));
					
					LOGGER.debug("Colectivo");
					Colectivo tempColec = individualAplicacion.getColectivo();
					if (tempColec != null) {
						regProdEmitida.setColCodigointerno(tempColec.getCodigoInterno());
						regProdEmitida.setColDc(new BigDecimal(tempColec.getDigitoControl()));
						regProdEmitida.setColReferencia(tempColec.getReferencia());
					}else {
						// sacamos el colectivo de la poliza
						regProdEmitida.setColCodigointerno(individualAplicacion.getCodigoInterno());
						com.rsi.agp.dao.tables.admin.Colectivo auxColec = this.getColectivoPoliza(individualAplicacion.getReferencia(),temp.getFase().getFechaEmisionRecibo());
						if (auxColec != null) {
							regProdEmitida.setColDc (new BigDecimal(auxColec.getDc()));
							regProdEmitida.setColReferencia (auxColec.getIdcolectivo());
						}
					}
					
					LOGGER.debug("Datos de Reglamento");
					DatosReglamento dr = individualAplicacion.getDatosReglamento();
					LOGGER.debug("Atributos comunes reglamento: referencia,dc,tiporef,codigointerno,...");	
					regProdEmitida.setDrFecharecepcion(dr.getFechaRecepcion().getTime());
					regProdEmitida.setDrFechapago(dr.getFechaPago().getTime());
					regProdEmitida.setDrFechaentvigor(dr.getFechaEntradaVigor().getTime());
					regProdEmitida.setDrComputodias(dr.getCodigoFechaComputoDias().charAt(0));
					regProdEmitida.setDrLimite(dr.getLimite());
					
					request.getSession().setAttribute("progressStatus", "UPLOADING");
					request.getSession().setAttribute("progress",75);
					
					LOGGER.debug("Situaciones de Reglamento");		
					ReglamentoProduccionEmitidaSituacion situacionAux = null;
					Set<ReglamentoProduccionEmitidaSituacion> listSituaciones = new HashSet<ReglamentoProduccionEmitidaSituacion>();
					sits= individualAplicacion.getSituaciones().getSituacionArray();
					DatosEconomicos de;
					
					for (Situacion situacion : sits) {
						situacionAux = new ReglamentoProduccionEmitidaSituacion();
						situacionAux.setCodigo(situacion.getCodigo().toString().charAt(0));
						
						// una situacion puede tener 0-1 datos tramitacion y 0-1 datos
						// calidad
						LOGGER.debug("Datos de Calidad");
						de = situacion.getDatosCalidad();
						if (de != null) {
							situacionAux.setDcMedida(de.getCodigoMedida());
							situacionAux.setDcImporteApl(de.getImporteAplicado());
							situacionAux.setDcImporteSRed(de.getImporteSinReduccion());
							situacionAux.setDcPorcentaje(de.getPorcentaje());
							situacionAux.setDcMedreglamento(de.getMedidaReglamento().charAt(0));
						}
						
						LOGGER.debug("Datos de Tramitacion");
						de = situacion.getDatosTramitacion();
						if (de != null) {
							situacionAux.setDtMedida(de.getCodigoMedida());
							situacionAux.setDtImporteApl(de.getImporteAplicado());
							situacionAux.setDtImporteSRed(de.getImporteSinReduccion());
							situacionAux.setDtPorcentaje(de.getPorcentaje());
							situacionAux.setDtMedreglamento(de.getMedidaReglamento().charAt(0));
						}
					
						situacionAux.setReglamentoProduccionEmitida(regProdEmitida);
						
					}
					listSituaciones.add(situacionAux);
					regProdEmitida.setReglamentoProduccionEmitidaSituacions(listSituaciones);
					regProdEmitidaSituacions.add(regProdEmitida);
				}
			}
			
			request.getSession().setAttribute("progressStatus", "UPLOADING");
			request.getSession().setAttribute("progress",80);
			
			fichero.setFicheroReglamentos(regProdEmitidaSituacions);
			
			LOGGER.debug("Procedemos a guardar en la BBDD el fichero completo");
			//TMR 30-05-2012 Facturacion
			this.saveOrUpdateFacturacion(fichero, usuario);
	
			Long idFichero = fichero.getId();
			
			LOGGER.debug("ID del fichero procesado: " +idFichero);
			
			xmlObject = null;
			
			request.getSession().setAttribute("progressStatus", "UPLOADING");
			request.getSession().setAttribute("progress",85);
			
			LOGGER.debug("end - importarYValidarFicheroReglamento");
			return idFichero;
			
		}catch (DAOException dao) {
			request.getSession().setAttribute("progressStatus", "FAILED");
			LOGGER.debug("Se ha producido un error al guardar en la BBDD el fichero", dao);
			throw new DAOException("Se ha producido un error al guardar en la BBDD el fichero", dao);
		} catch (Exception ex) {
			request.getSession().setAttribute("progressStatus", "FAILED");
			LOGGER.debug("Se ha producido un error en la creacion del fichero", ex);
			throw new DAOException("Se ha producido un error en la creacion del fichero", ex);
		}
		
	}

	private com.rsi.agp.dao.tables.admin.Colectivo getColectivoPoliza(String referencia, Calendar fecha) {
		
		List<Object> resultado = new ArrayList<Object>();
		Session session = obtenerSession();
		com.rsi.agp.dao.tables.admin.Colectivo c = new com.rsi.agp.dao.tables.admin.Colectivo();
		String sql ="select referencia, dc "+ 
					"from( " + 
				    "   select c.referencia as referencia, c.dc" + 
				    "        from o02agpe0.tb_historico_colectivos c, o02agpe0.tb_lineas l" + 
				    "        where c.idcolectivo in  " + 
				    "             (select idcolectivo from o02agpe0.tb_polizas p where p.referencia= '"+referencia+"') " + 
					"             and l.lineaseguroid = c.lineaseguroid" + 
					"             and c.fechaefecto<= TO_DATE('"+ fecha +"','YYYY-MM-DD')"+
				    "        order by c.fechacambio desc)" + 
					"        where  rownum=1 "; 
	
	
		logger.info("sql " + sql);
		resultado =  session.createSQLQuery(sql).list();
		
		if (resultado.size()>0) {
			
			Object[] registro =  (Object[]) resultado.get(0);
			c.setIdcolectivo((String) registro[0]);
			c.setDc((String) registro[1]);
		}else {
			return null;
		}
		
		return c;
	}

	public void setFaseDao(IFaseDao faseDao) {
		this.faseDao = faseDao;
	}
}
