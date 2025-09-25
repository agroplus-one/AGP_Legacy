package com.rsi.agp.dao.models.poliza;

import java.io.StringReader;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.webapp.util.VistaImportesPorGrupoNegocio;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.poliza.DistCosteParcela;
import com.rsi.agp.dao.tables.poliza.DistCosteSubvencion;
import com.rsi.agp.dao.tables.poliza.DistribucionCoste;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.dc2015.BonificacionRecargo2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteExplotaciones;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteExplotacionesBonifRec;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteExplotacionesGrupoNegocio;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteExplotacionesSubvencion;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteParcela2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteParcela2015BonifRec;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteParcela2015GrupoNegocio;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteParcela2015Subvencion;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteSubvencion2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;

import es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio;
import es.agroseguro.contratacion.costePoliza.CostePoliza;
import es.agroseguro.seguroAgrario.distribucionCoste.BonificacionRecargo;
import es.agroseguro.seguroAgrario.distribucionCoste.SubvencionCCAA;
import es.agroseguro.seguroAgrario.distribucionCoste.SubvencionEnesa;
import es.agroseguro.seguroAgrario.distribucionCoste.parcela.ParcelaDocument;
import es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.ObjetoAsegurado;
import es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Parcela;
import es.agroseguro.seguroAgrario.financiacion.FinanciacionDocument;

public class DistribucionCosteDao extends BaseDaoHibernate implements
		IDistribucionCosteDAO {

	private Log logger = LogFactory.getLog(getClass());
	
	
	public DistribucionCoste getDistribucionCosteById(final Long id)
			throws DAOException {
		DistribucionCoste distCoste = null;
		try {
			super.get(DistribucionCoste.class, id);
		} catch (Exception e) {
			throw new DAOException(
					"Se ha producido un error durante la obtenci�n de la distribuci�n de coste.",
					e);
		}
		return distCoste;
	}

	public DistribucionCoste getDistribucionCoste(final Long idPoliza,
			final String codModulo, final BigDecimal filaComparativa)
			throws DAOException {
		final String[] parametros = new String[] { "poliza.idpoliza",
				"codmodulo", "filacomparativa" };
		final Object[] valores = new Object[] { idPoliza, codModulo,
				filaComparativa };
		DistribucionCoste distCoste = null;
		try {
			@SuppressWarnings("unchecked")
			List<DistribucionCoste> auxList = (List<DistribucionCoste>) super
					.findFiltered(DistribucionCoste.class, parametros, valores,
							null);
			if (auxList != null && auxList.size() > 0) {
				distCoste = auxList.get(0);
			}
		} catch (Exception e) {
			throw new DAOException(
					"Se ha producido un error durante la obtenci�n de la distribuci�n de coste.",
					e);
		}
		return distCoste;
	}

	public void deleteDistribucionCoste(final Long idPoliza,
			final String codModulo, final BigDecimal filaComparativa)
			throws DAOException {
		DistribucionCoste distCoste = getDistribucionCoste(idPoliza, codModulo,
				filaComparativa);
		if (distCoste != null) {
			super.delete(distCoste);
		}
	}

	public void deleteDistribucionCosteById(final Long id) throws DAOException {
		try {
			super.delete(DistribucionCoste.class, id);
		} catch (Exception e) {
			throw new DAOException(
					"Se ha producido un error durante el borrado de la distribuci�n de coste.",
					e);
		}
	}

	public void saveDistribucionCoste(final DistribucionCoste distCoste,
			final Long idPoliza) throws DAOException {
		if (distCoste == null) {
			throw new DAOException(
					"La distribuci�n de coste recibida como par�metro de entrada es nula.");
		}
		try {
			if (idPoliza == null) {
				throw new Exception(
						"Par�metro de entrada obligatorio \"idPoliza\" no recibido.");
			}
			distCoste.setPoliza((Poliza) super.get(Poliza.class, idPoliza));

			super.saveOrUpdate(distCoste);

		} catch (Exception e) {
			throw new DAOException(
					"Se ha producido un error durante la modificaci�n de la distribuci�n de coste.",
					e);
		}
	}
	public void saveDistribucionCoste2015(final DistribucionCoste2015 distCoste,
			final Long idPoliza) throws DAOException {
		if (distCoste == null) {
			throw new DAOException(
					"La distribuci�n de coste 2015 recibida como par�metro de entrada es nula.");
		}
		try {
			if (idPoliza == null) {
				throw new Exception(
						"Par�metro de entrada obligatorio \"idPoliza\" no recibido.");
			}
			distCoste.setPoliza((Poliza) super.get(Poliza.class, idPoliza));

			super.saveOrUpdate(distCoste);
			
			for (DistCosteParcela2015 dcp : distCoste.getDistCosteParcela2015s()) {
				
				super.saveOrUpdate(dcp);
				
				for (DistCosteParcela2015GrupoNegocio dcpgn : dcp.getDistCosteGns()) {
					
					super.saveOrUpdate(dcpgn);
				}
				for (DistCosteParcela2015BonifRec dcpbr : dcp.getDistCosteBonifRecs()) {

					super.saveOrUpdate(dcpbr);
				}
				for (DistCosteParcela2015Subvencion dcpsubv : dcp.getDistCosteSubvs()) {

					super.saveOrUpdate(dcpsubv);
				}
			}
		} catch (Exception e) {
			throw new DAOException(
					"Se ha producido un error durante la modificaci�n de la distribuci�n de coste.",
					e);
		}
	}
	
	

	public DistribucionCoste saveDistribucionCoste(
			final es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza polizaXML,
			final Long idPoliza, final String codModulo,
			final BigDecimal filaComparativa) throws DAOException {
		DistribucionCoste distCosteHb = null;
		if (polizaXML == null) {
			throw new DAOException(
					"La p�liza recibida como par�metro de entrada es nula.");
		}
		try {
			if (checkInParams(idPoliza, codModulo, filaComparativa)) {

				es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.DistribucionCoste distribCosteXML = polizaXML
						.getDatosCalculo().getDistribucionCoste();
				if (distribCosteXML == null) {
					throw new DAOException(
							"La p�liza recibida como par�metro de entrada no tiene distribuci�n de costes.");
				}

				distCosteHb = new DistribucionCoste();
				distCosteHb.setPoliza((Poliza) super
						.get(Poliza.class, idPoliza));
				distCosteHb.setCodmodulo(codModulo);
				distCosteHb.setFilacomparativa(filaComparativa);

				if (distribCosteXML.getBonificacionAsegurado() != null) {
					distCosteHb.setBonifasegurado(distribCosteXML
							.getBonificacionAsegurado().getBonifAsegurado());
					distCosteHb.setPctbonifasegurado(distribCosteXML
							.getBonificacionAsegurado().getPctBonifAsegurado());
				}
				if (distribCosteXML.getRecargoAsegurado() != null) {
					distCosteHb.setRecargoasegurado(distribCosteXML
							.getRecargoAsegurado().getRecargoAsegurado());
					distCosteHb.setPctrecargoasegurado(distribCosteXML
							.getRecargoAsegurado().getPctRecargoAsegurado());
				}
				if (distribCosteXML.getBonificacionMedidasPreventivas() != null) {
					distCosteHb.setBonifmedpreventivas(distribCosteXML
							.getBonificacionMedidasPreventivas()
							.getBonifMedPreventivas());
				}
				distCosteHb.setCargotomador(distribCosteXML.getCargoTomador());
				distCosteHb.setCosteneto(distribCosteXML.getCosteNeto());
				if (distribCosteXML.getDescuento() != null) {
					distCosteHb.setDtocolectivo(distribCosteXML.getDescuento()
							.getDescuentoColectivo());
					distCosteHb.setVentanilla(distribCosteXML.getDescuento()
							.getVentanilla());
				}
				distCosteHb.setPrimacomercial(distribCosteXML
						.getPrimaComercial());
				distCosteHb.setPrimaneta(distribCosteXML.getPrimaNeta());
				if (distribCosteXML.getConsorcio() != null) {
					distCosteHb.setReaseguro(distribCosteXML.getConsorcio()
							.getReaseguro());
					distCosteHb.setRecargo(distribCosteXML.getConsorcio()
							.getRecargo());
				}
				// Subvenciones
				distCosteHb.getDistCosteSubvencions().clear();

				es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.SubvencionCCAA subCCAA[] = distribCosteXML
						.getSubvencionCCAAArray();
				es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.SubvencionEnesa subEnesa[] = distribCosteXML
						.getSubvencionEnesaArray();

				// Subvenciones CCAA
				if (subCCAA != null) {
					// Carga el mapa cuya clave es el codigo del organismo y el
					// valor es otro mapa cuya clave es el tipo de subvencion
					// y el valor es el importe y crea el iterador
					Iterator<Map.Entry<String, Map<Integer, BigDecimal>>> it = cargaMapaSubvCCAA(
							subCCAA).entrySet().iterator();

					// Bucle para a�adir las distribuciones de coste de las
					// subvenciones ccaa
					while (it.hasNext()) {
						Map.Entry<String, Map<Integer, BigDecimal>> e = (Map.Entry<String, Map<Integer, BigDecimal>>) it
								.next();

						// Clave - Codigo de organismo
						String key = e.getKey();
						// Value - Mapa de tipo subvencion - importe
						Map<Integer, BigDecimal> mapaAux = e.getValue();
						// Iterador sobre el mapa anterior
						Iterator<Map.Entry<Integer, BigDecimal>> itInterno = mapaAux
								.entrySet().iterator();
						while (itInterno.hasNext()) {
							// Obtiene el mapa asociado
							Map.Entry<Integer, BigDecimal> v = (Map.Entry<Integer, BigDecimal>) itInterno
									.next();
							// Carga el objeto de distribucion de costes y lo
							// añade a la lista
							DistCosteSubvencion subv = new DistCosteSubvencion();
							subv.setCodorganismo(new Character(key.charAt(0)));
							subv.setCodtiposubv(new BigDecimal(v.getKey()));
							subv.setImportesubv(v.getValue());
							subv.setDistribucionCoste(distCosteHb);
							distCosteHb.getDistCosteSubvencions().add(subv);
						}
					}
				}

				// Subvenciones ENESA
				if (subEnesa != null) {
					// Bucle para a�adir las distribuciones de coste de las
					// subvenciones enesa
					for (int i = 0; i < subEnesa.length; i++) {
						DistCosteSubvencion subv = null;
						subv = new DistCosteSubvencion();
						subv.setCodtiposubv(new BigDecimal(subEnesa[i]
								.getTipo()));
						subv.setImportesubv(subEnesa[i].getSubvencionEnesa());
						subv.setCodorganismo(new Character('0'));
						subv.setDistribucionCoste(distCosteHb);
						distCosteHb.getDistCosteSubvencions().add(subv);
					}
				}

				// Bucle para a�adir las distribuciones de coste Parcela
				distCosteHb.getDistCosteParcelas().clear();

				for (int i = 0; i < polizaXML.getParcelaArray().length; i++) {
					DistCosteParcela dCPar = null;
					dCPar = new DistCosteParcela();
					Parcela par = polizaXML.getParcelaArray(i);
					dCPar.setDistribucionCoste(distCosteHb);
					dCPar.setTipo(new BigDecimal(par.getTipo()));
					dCPar.setHoja(new BigDecimal(par.getHoja()));
					dCPar.setNumero(new BigDecimal(par.getNumero()));
					dCPar.setCosteneto(par.getCostesParcela().getCosteNeto());
					dCPar.setTasacomercial(par.getCostesParcela()
							.getTasaComercial());

					distCosteHb.getDistCosteParcelas().add(dCPar);
				}

				super.saveOrUpdate(distCosteHb);
			}
		} catch (Exception e) {
			throw new DAOException(
					"Se ha producido un error durante la modificaci�n de la distribuci�n de coste.",
					e);
		}
		return distCosteHb;
	}

	public DistribucionCoste updateDistribucionCoste(
			final es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza polizaXML,
			final Long idPoliza, final String codModulo,
			final BigDecimal filaComparativa) throws DAOException {
		DistribucionCoste distCosteHb = null;
		if (polizaXML == null) {
			throw new DAOException(
					"La p�liza recibida como par�metro de entrada es nula.");
		} else {
			try {
				if (checkInParams(idPoliza, codModulo, filaComparativa)) {

					es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.DistribucionCoste distribCosteXML = polizaXML
							.getDatosCalculo().getDistribucionCoste();
					if (distribCosteXML == null) {
						throw new DAOException(
								"La p�liza recibida como par�metro de entrada no tiene distribuci�n de costes.");
					}

					distCosteHb = getDistribucionCoste(idPoliza, codModulo,
							filaComparativa);

					if (distribCosteXML.getBonificacionAsegurado() != null) {
						distCosteHb
								.setBonifasegurado(distribCosteXML
										.getBonificacionAsegurado()
										.getBonifAsegurado());
						distCosteHb.setPctbonifasegurado(distribCosteXML
								.getBonificacionAsegurado()
								.getPctBonifAsegurado());
					}
					if (distribCosteXML.getRecargoAsegurado() != null) {
						distCosteHb.setRecargoasegurado(distribCosteXML
								.getRecargoAsegurado().getRecargoAsegurado());
						distCosteHb
								.setPctrecargoasegurado(distribCosteXML
										.getRecargoAsegurado()
										.getPctRecargoAsegurado());
					}
					if (distribCosteXML.getBonificacionMedidasPreventivas() != null) {
						distCosteHb.setBonifmedpreventivas(distribCosteXML
								.getBonificacionMedidasPreventivas()
								.getBonifMedPreventivas());
					}
					distCosteHb.setCargotomador(distribCosteXML
							.getCargoTomador());
					distCosteHb.setCosteneto(distribCosteXML.getCosteNeto());
					if (distribCosteXML.getDescuento() != null) {
						distCosteHb.setDtocolectivo(distribCosteXML
								.getDescuento().getDescuentoColectivo());
						distCosteHb.setVentanilla(distribCosteXML
								.getDescuento().getVentanilla());
					}
					distCosteHb.setPrimacomercial(distribCosteXML
							.getPrimaComercial());
					distCosteHb.setPrimaneta(distribCosteXML.getPrimaNeta());
					if (distribCosteXML.getConsorcio() != null) {
						distCosteHb.setReaseguro(distribCosteXML.getConsorcio()
								.getReaseguro());
						distCosteHb.setRecargo(distribCosteXML.getConsorcio()
								.getRecargo());
					}

					// Borramos las distribuciones de coste de subvenciones para
					// recrearlas
					super.deleteAll(distCosteHb.getDistCosteSubvencions());

					// Subvenciones
					distCosteHb.getDistCosteSubvencions().clear();

					es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.SubvencionCCAA subCCAA[] = distribCosteXML
							.getSubvencionCCAAArray();
					es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.SubvencionEnesa subEnesa[] = distribCosteXML
							.getSubvencionEnesaArray();

					// Subvenciones CCAA
					if (subCCAA != null) {
						// Carga el mapa cuya clave es el codigo del organismo y
						// el
						// valor es otro mapa cuya clave es el tipo de
						// subvencion
						// y el valor es el importe y crea el iterador
						Iterator<Map.Entry<String, Map<Integer, BigDecimal>>> it = cargaMapaSubvCCAA(
								subCCAA).entrySet().iterator();

						// Bucle para a�adir las distribuciones de coste de las
						// subvenciones ccaa
						while (it.hasNext()) {
							Map.Entry<String, Map<Integer, BigDecimal>> e = (Map.Entry<String, Map<Integer, BigDecimal>>) it
									.next();

							// Clave - Codigo de organismo
							String key = e.getKey();
							// Value - Mapa de tipo subvencion - importe
							Map<Integer, BigDecimal> mapaAux = e.getValue();
							// Iterador sobre el mapa anterior
							Iterator<Map.Entry<Integer, BigDecimal>> itInterno = mapaAux
									.entrySet().iterator();
							while (itInterno.hasNext()) {
								// Obtiene el mapa asociado
								Map.Entry<Integer, BigDecimal> v = (Map.Entry<Integer, BigDecimal>) itInterno
										.next();
								// Carga el objeto de distribucion de costes y
								// lo
								// añade a la lista
								DistCosteSubvencion subv = new DistCosteSubvencion();
								subv.setCodorganismo(new Character(key
										.charAt(0)));
								subv.setCodtiposubv(new BigDecimal(v.getKey()));
								subv.setImportesubv(v.getValue());
								subv.setDistribucionCoste(distCosteHb);
								distCosteHb.getDistCosteSubvencions().add(subv);
							}
						}
					}

					// Subvenciones ENESA
					if (subEnesa != null) {
						// Bucle para a�adir las distribuciones de coste de las
						// subvenciones enesa
						for (int i = 0; i < subEnesa.length; i++) {
							DistCosteSubvencion subv = null;
							subv = new DistCosteSubvencion();
							subv.setCodtiposubv(new BigDecimal(subEnesa[i]
									.getTipo()));
							subv.setImportesubv(subEnesa[i]
									.getSubvencionEnesa());
							subv.setCodorganismo(new Character('0'));
							subv.setDistribucionCoste(distCosteHb);
							distCosteHb.getDistCosteSubvencions().add(subv);
						}
					}

					// Borramos las distribuciones de coste de parcelas para
					// recrearlas
					super.deleteAll(distCosteHb.getDistCosteParcelas());

					// Bucle para a�adir las distribuciones de coste Parcela
					distCosteHb.getDistCosteParcelas().clear();

					for (int i = 0; i < polizaXML.getParcelaArray().length; i++) {
						DistCosteParcela dCPar = null;
						dCPar = new DistCosteParcela();
						Parcela par = polizaXML.getParcelaArray(i);
						dCPar.setDistribucionCoste(distCosteHb);
						dCPar.setTipo(new BigDecimal(par.getTipo()));
						dCPar.setHoja(new BigDecimal(par.getHoja()));
						dCPar.setNumero(new BigDecimal(par.getNumero()));
						dCPar.setCosteneto(par.getCostesParcela()
								.getCosteNeto());
						dCPar.setTasacomercial(par.getCostesParcela()
								.getTasaComercial());

						distCosteHb.getDistCosteParcelas().add(dCPar);
					}

					super.saveOrUpdate(distCosteHb);
				}
			} catch (Exception e) {
				throw new DAOException(
						"Se ha producido un error durante la modificaci�n de la distribuci�n de coste.",
						e);
			}
		}
		return distCosteHb;
	}

	private boolean checkInParams(final Long idPoliza, final String codModulo,
			final BigDecimal filaComparativa) throws Exception {
		if (idPoliza == null) {
			throw new Exception(
					"Par�metro de entrada obligatorio \"idPoliza\" no recibido.");
		}
		if (codModulo == null || "".equals(codModulo)) {
			throw new Exception(
					"Par�metro de entrada obligatorio \"codModulo\" no recibido.");
		}
		return true;
	}

	/**
	 * Crea el mapa cuya clave es el codigo del organismo y el valor es otro
	 * mapa cuya clave es el tipo de subvencion y el valor es el importe
	 * 
	 * @param subCCAA
	 *            Array de subvenciones de CCAA
	 * @return
	 */
	private Map<String, Map<Integer, BigDecimal>> cargaMapaSubvCCAA(
			es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.SubvencionCCAA[] subCCAA) {
		Map<String, Map<Integer, BigDecimal>> ccaa = new HashMap<String, Map<Integer, BigDecimal>>();
		for (int i = 0; i < subCCAA.length; i++) {
			// Si ya existe una clave para el codigo de organismo
			Map<Integer, BigDecimal> mapaOrgAux = ccaa.get(subCCAA[i]
					.getCodigoOrganismo());
			if (mapaOrgAux != null) {
				// Si ya existe un registro con la clave correspondiente al tipo
				// de subvencion se suma el importe
				BigDecimal importe = mapaOrgAux.get(subCCAA[i].getTipo());
				if (importe != null) {
					mapaOrgAux.put(subCCAA[i].getTipo(),
							importe.add(subCCAA[i].getSubvencionCA()));
				}
				// Si no existe el registro se inserta el importe
				else {
					mapaOrgAux.put(subCCAA[i].getTipo(),
							subCCAA[i].getSubvencionCA());
				}

			}
			// Si no existe la clave se crea
			else {
				// Crea el mapa tipo de subvencion - importe
				mapaOrgAux = new HashMap<Integer, BigDecimal>();
				mapaOrgAux.put(subCCAA[i].getTipo(),
						subCCAA[i].getSubvencionCA());
			}

			// Se actualiza el registro en el mapa general
			ccaa.put(subCCAA[i].getCodigoOrganismo(), mapaOrgAux);
		}
		return ccaa;
	}
	

	//es.agroseguro.seguroAgrario.financiacion.FinanciacionDocument  financiacion
	public DistribucionCoste2015 saveDistribucionCoste2015(
			es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza polizaXML,
			Long idpoliza, String codModulo, BigDecimal filaComparativa, 
			FinanciacionDocument  financiacion, int periodoFracc, int opcionFracc, BigDecimal valorOpcionFracc,Long IdComparativa) throws DAOException {
		DistribucionCoste2015 dc=null;
		
		try {
			dc=this.saveDistribucionCoste2015(polizaXML, idpoliza, codModulo, filaComparativa,IdComparativa);
			dc.setRecargoaval(financiacion.getFinanciacion().getPeriodoArray(0).getDistribucionCoste().getRecargoAval());
			dc.setRecargofraccionamiento(financiacion.getFinanciacion().getPeriodoArray(0).getDistribucionCoste().getRecargoFraccionamiento());
			dc.setImportePagoFracc(financiacion.getFinanciacion().getPeriodoArray(0).getPago().getImporte());
			dc.setTotalcostetomador(financiacion.getFinanciacion().getPeriodoArray(0).getDistribucionCoste().getTotalCosteTomador());
			dc.setCostetomador(financiacion.getFinanciacion().getCosteTomador());
			dc.setPeriodoFracc(periodoFracc);
			dc.setOpcionFracc(opcionFracc);
			dc.setValorOpcionFracc(valorOpcionFracc);			
			super.saveOrUpdate(dc);
		}catch (Exception e) {
			throw new DAOException(
					"Se ha producido un error durante la modificaci�n de la distribuci�n de coste.",
					e);
		}
		return dc;
	}
	
	@Override
	public Set<DistribucionCoste2015> saveDistribucionCoste2015Unificado(
			es.agroseguro.distribucionCostesSeguro.Poliza plzUnificada,
			Long idpoliza, String codModulo, BigDecimal filaComparativa,Long idComparativa,
			FinanciacionDocument financiacionDocument, int periodoFracc, int opcionFracc, BigDecimal valorOpcionFracc, Boolean esGanado) throws DAOException {
		
		Set<DistribucionCoste2015> listaDc = null;
		es.agroseguro.distribucionCostesSeguro.CalculoAlternativoFinanciacion calculoAltFinan =null;
		BigDecimal totalCosteTomadorAFinanciar=null;
		
		if (plzUnificada == null) {
			throw new DAOException("La p�liza recibida como par�metro de entrada es nula.");
		}		
		
		try {
			
			if (esGanado){
				/****************************/
				/** DIST. DE COSTES GANADO **/
				/****************************/
				// Comprueba que los par�metros de entrada son correctos
				if (checkInParams(idpoliza, codModulo, filaComparativa)) {
					
					// Obtiene los datos del c�lculo
					CostePoliza costePoliza = plzUnificada.getDatosCalculo().getCostePoliza();
					CosteGrupoNegocio costeGrupoNegocio = null;
					//Financiacion financiacion = null;				
					es.agroseguro.distribucionCostesSeguro.ObjetoAsegurado objetosAsegurados[]= plzUnificada.getObjetoAseguradoArray();				
					if(null!=plzUnificada.getCalculoAlternativoFinanciacion()){
						calculoAltFinan=plzUnificada.getCalculoAlternativoFinanciacion();
						totalCosteTomadorAFinanciar=calculoAltFinan.getTotalCosteTomadorAFinanciar();					
					}
						
					
					if (costePoliza != null) {
						listaDc = new HashSet<DistribucionCoste2015>(0);
						CosteGrupoNegocio[] costeGrupoNegocioArray = costePoliza.getCosteGrupoNegocioArray();
						if (costeGrupoNegocioArray != null) {
							for (CosteGrupoNegocio costeGrupoNegocioFor : costeGrupoNegocioArray) {
								costeGrupoNegocio = costeGrupoNegocioFor;
								DistribucionCoste2015 dc = null;
								// Rellena el objeto DistribucionCoste2015
								es.agroseguro.seguroAgrario.financiacion.Financiacion financiacion=null;
								if(null!=financiacionDocument && null!=financiacionDocument.getFinanciacion())
									financiacion=financiacionDocument.getFinanciacion();
								dc = crearDC2015Unificada(idpoliza, codModulo, filaComparativa,	costePoliza, costeGrupoNegocio, 
										financiacion, periodoFracc, opcionFracc, valorOpcionFracc, idComparativa);
								
								if(null!=totalCosteTomadorAFinanciar)dc.setTotalcostetomadorafinanciar(totalCosteTomadorAFinanciar);			
								
								// Subvenciones CCAA y Subvenciones Enesa
								dc.getDistCosteSubvencion2015s().clear();
								// Carga las subvenciones de CCAA y ENESA en el objeto de la distribuci�n de costes
								cargaSubvencionesDCUnificado(dc, costeGrupoNegocio);
								// Bonificaciones y recargos
								cargaBonifRecargUnificado(dc, costeGrupoNegocio);

								Set<DistCosteExplotaciones> distCosteExpl = getDistribucionCostesExplotaciones(objetosAsegurados, dc.getGrupoNegocio());
															
								super.saveOrUpdate(dc);
								
								for (DistCosteExplotaciones distCosteExplotacion : distCosteExpl) {
									
									distCosteExplotacion.setDistribucionCoste2015(dc);
					  				super.saveOrUpdate(distCosteExplotacion);	
					  				
					  				for (DistCosteExplotacionesGrupoNegocio dcegn : distCosteExplotacion.getDistCosteGns()) {
					  					super.saveOrUpdate(dcegn);	
					  				}
					  				for (DistCosteExplotacionesSubvencion dces : distCosteExplotacion.getDistCosteSubvs()) {
					  					super.saveOrUpdate(dces);	
					  				}
					  				for (DistCosteExplotacionesBonifRec dcebr : distCosteExplotacion.getDistCosteBonifRecs()) {
					  					super.saveOrUpdate(dcebr);	
					  				}
								}				
								dc.setDistCosteExplotaciones(distCosteExpl);
								listaDc.add(dc);
							}
						}
					}				
				}
			}else{
				/*******************************/
				/** DIST. DE COSTES AGRICOLAS **/
				/*******************************/
				// Comprueba que los par�metros de entrada son correctos
				if (checkInParams(idpoliza, codModulo, filaComparativa)) {
					
					// Obtiene los datos del c�lculo
					CostePoliza costePoliza = plzUnificada.getDatosCalculo().getCostePoliza();
					CosteGrupoNegocio costeGrupoNegocio = null;
				
					es.agroseguro.distribucionCostesSeguro.ObjetoAsegurado objetosAsegurados[]= plzUnificada.getObjetoAseguradoArray();
					
					if(null!=plzUnificada.getCalculoAlternativoFinanciacion()){
						calculoAltFinan=plzUnificada.getCalculoAlternativoFinanciacion();
						totalCosteTomadorAFinanciar=calculoAltFinan.getTotalCosteTomadorAFinanciar();					
					}
						
					
					if (costePoliza != null) {
						listaDc = new HashSet<DistribucionCoste2015>(0);
						CosteGrupoNegocio[] costeGrupoNegocioArray = costePoliza.getCosteGrupoNegocioArray();
						if (costeGrupoNegocioArray != null) {
							for (CosteGrupoNegocio costeGrupoNegocioFor : costeGrupoNegocioArray) {
								costeGrupoNegocio = costeGrupoNegocioFor;
								DistribucionCoste2015 dc=null;
								// Rellena el objeto DistribucionCoste2015
								es.agroseguro.seguroAgrario.financiacion.Financiacion financiacion=null;
								if(null!=financiacionDocument && null!=financiacionDocument.getFinanciacion())
									financiacion=financiacionDocument.getFinanciacion();
								
								dc = crearDC2015UnificadaAgric(idpoliza, codModulo, filaComparativa,	costePoliza, costeGrupoNegocio, 
										financiacion, periodoFracc, opcionFracc, valorOpcionFracc, idComparativa);
								
								if(null!=totalCosteTomadorAFinanciar)dc.setTotalcostetomadorafinanciar(totalCosteTomadorAFinanciar);			
								
								// Subvenciones CCAA y Subvenciones Enesa
								dc.getDistCosteSubvencion2015s().clear();
								
								// Carga las subvenciones de CCAA y ENESA en el objeto de la distribuci�n de costes
								cargaSubvencionesDCUnificado(dc, costeGrupoNegocio);
								
								// Bonificaciones y recargos
								cargaBonifRecargUnificado(dc, costeGrupoNegocio);
								
								Set<DistCosteParcela2015> distCosteParc= getDistribucionCostesParcelas(objetosAsegurados);
					  			super.saveOrUpdate(dc);
					  			
					  			for (DistCosteParcela2015 distCosteParcela : distCosteParc) {
					  				
					  				distCosteParcela.setDistribucionCoste2015(dc);
					  				logger.debug("Guardamos DC de parcela");
					  				super.saveOrUpdate(distCosteParcela);
					  				
					  				for (DistCosteParcela2015GrupoNegocio dcpgn : distCosteParcela.getDistCosteGns()) {
					  					logger.debug("Guardamos DC de parcela por GN");
					  					super.saveOrUpdate(dcpgn);
					  				}
					  				for (DistCosteParcela2015Subvencion dcps : distCosteParcela.getDistCosteSubvs()) {
					  					logger.debug("Guardamos DC de parcela por SUBV");
					  					super.saveOrUpdate(dcps);
					  				}
					  				for (DistCosteParcela2015BonifRec dcpbr : distCosteParcela.getDistCosteBonifRecs()) {
					  					logger.debug("Guardamos DC de parcela por BR");
					  					super.saveOrUpdate(dcpbr);
					  				}
								}				
								dc.setDistCosteParcela2015s(distCosteParc);
					  			listaDc.add(dc);
							}
						}
					}				
				}				
			}
		}
		catch (Exception e) {
			throw new DAOException(
					"Se ha producido un error durante la modificaci�n de la distribuci�n de coste.",
					e);
		}
		return listaDc;
	}
	
	private Set<DistCosteExplotaciones> getDistribucionCostesExplotaciones(
			final es.agroseguro.distribucionCostesSeguro.ObjetoAsegurado objetosAsegurados[],
			final Character grupoNegocio) throws XmlException {
		
		logger.debug("getDistribucionCostesExplotaciones [INIT]");
		Set<DistCosteExplotaciones> distCosteExplotaciones = new HashSet<DistCosteExplotaciones>(0);	
		
		if(null!=objetosAsegurados && objetosAsegurados.length>0){
			
			for (int i = 0; i < objetosAsegurados.length; i++) {
				
				es.agroseguro.costePoliza.explotacion.ExplotacionDocument explDoc= null;
				NodeList nodoExplList = null;
				nodoExplList= objetosAsegurados[i].getDomNode().getChildNodes();		
				
				for (int j = 0; j < nodoExplList.getLength(); j++) {  
					
					if(nodoExplList.item(j).getNodeType()==Node.ELEMENT_NODE){
						
						explDoc = es.agroseguro.costePoliza.explotacion.ExplotacionDocument.Factory.parse(nodoExplList.item(j));
						
						logger.debug("Procesando XML explotacion.................");
						
						if (null != explDoc.getExplotacion()) {
							
							es.agroseguro.costePoliza.explotacion.ExplotacionDocument.Explotacion expl=explDoc.getExplotacion();
							
							if (null != expl.getCosteExplotacion()) {
							
								es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio[] costeGNArr = expl.getCosteExplotacion().getCosteGrupoNegocioArray();
								if(!ArrayUtils.isEmpty(costeGNArr)){
									
									logger.debug("Procesando XML coste explotacion.................");
									
									for (es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio costeGN : costeGNArr) {
										
										if (grupoNegocio.equals(costeGN.getGrupoNegocio().charAt(0))) {
											DistCosteExplotaciones distCosteExpl= new DistCosteExplotaciones();
											distCosteExpl.setGruporaza(expl.getGrupoRaza());
											distCosteExpl.setTipocapital(expl.getTipoCapital());
											if (null != expl.getNumero())
												distCosteExpl.setNumexplotacion(expl.getNumero().intValue());
											if (null != expl.getAnimales())
												distCosteExpl.setTipoanimal(expl.getAnimales().getTipo());
											if (null != expl.getCosteExplotacion().getTasaComercialBase())
												distCosteExpl.setTasacomercialbase(expl.getCosteExplotacion().getTasaComercialBase());
											if (null != expl.getCosteExplotacion().getTasaComercial())
												distCosteExpl.setTasacomercial(expl.getCosteExplotacion().getTasaComercial());
											if (null != costeGNArr[0].getGrupoNegocio())
												distCosteExpl.setGruponegocio(costeGN.getGrupoNegocio().charAt(0));
											if (null != costeGNArr[0].getCosteTomador())
												distCosteExpl.setCostetomador(costeGN.getCosteTomador());
											if (null != costeGNArr[0].getPrimaComercial())
												distCosteExpl.setPrimacomercial(costeGN.getPrimaComercial());
											if (null != costeGNArr[0].getPrimaComercialNeta())
												distCosteExpl.setPrimacomercialneta(costeGN.getPrimaComercialNeta());
											if (null != costeGNArr[0].getRecargoConsorcio())
												distCosteExpl.setRecargoconsorcio(costeGN.getRecargoConsorcio());
											if (null != costeGNArr[0].getReciboPrima())
												distCosteExpl.setReciboprima(costeGN.getReciboPrima());
											
											logger.debug("Procesando XML coste explotacion GN.................");
											DistCosteExplotacionesGrupoNegocio expGN = new DistCosteExplotacionesGrupoNegocio();
											expGN.setGruponegocio(costeGN.getGrupoNegocio().charAt(0));
											expGN.setPrimacomercial(costeGN.getPrimaComercial());
											expGN.setPrimacomercialneta(costeGN.getPrimaComercialNeta());
											expGN.setRecargoconsorcio(costeGN.getRecargoConsorcio());
											expGN.setReciboprima(costeGN.getReciboPrima());
											expGN.setCostetomador(costeGN.getCosteTomador());
											expGN.setDistCosteExplotaciones(distCosteExpl);
											distCosteExpl.getDistCosteGns().add(expGN);
											
											es.agroseguro.contratacion.costePoliza.SubvencionEnesa[] subvEnesaArr = costeGN.getSubvencionEnesaArray();
											if (!ArrayUtils.isEmpty(subvEnesaArr)) {
												
												for (es.agroseguro.contratacion.costePoliza.SubvencionEnesa subv : subvEnesaArr) {
													
													logger.debug("Procesando XML coste explotacion Subv Enesa.................");
													DistCosteExplotacionesSubvencion expSubv = new DistCosteExplotacionesSubvencion();
													expSubv.setCodTipo('E');
													expSubv.setCodOrganismo('0');
													expSubv.setCodTipoSubv(BigDecimal.valueOf(subv.getTipo()));
													expSubv.setImporte(subv.getImporte());
													expSubv.setPctSubvencion(BigDecimal.ZERO);
													expSubv.setValorUnitario(BigDecimal.ZERO);
													expSubv.setDistCosteExplotaciones(distCosteExpl);
													distCosteExpl.getDistCosteSubvs().add(expSubv);
												}
											}
											es.agroseguro.contratacion.costePoliza.SubvencionCCAA[] subvCCAAArr = costeGN.getSubvencionCCAAArray();
											if (!ArrayUtils.isEmpty(subvCCAAArr)) {
												
												for (es.agroseguro.contratacion.costePoliza.SubvencionCCAA subv : subvCCAAArr) {
													
													logger.debug("Procesando XML coste explotacion Subv CCAA.................");
													DistCosteExplotacionesSubvencion expSubv = new DistCosteExplotacionesSubvencion();
													expSubv.setCodTipo('C');
													expSubv.setCodOrganismo(subv.getCodigoOrganismo().charAt(0));
													expSubv.setCodTipoSubv(BigDecimal.ONE);
													expSubv.setImporte(subv.getImporte());
													expSubv.setPctSubvencion(BigDecimal.ZERO);
													expSubv.setValorUnitario(BigDecimal.ZERO);
													expSubv.setDistCosteExplotaciones(distCosteExpl);
													distCosteExpl.getDistCosteSubvs().add(expSubv);
												}
											}
											es.agroseguro.contratacion.costePoliza.BonificacionRecargo[] bonRecArr = costeGN.getBonificacionRecargoArray();
											if (!ArrayUtils.isEmpty(bonRecArr)) {
												
												for (es.agroseguro.contratacion.costePoliza.BonificacionRecargo bonRec : bonRecArr) {
													
													logger.debug("Procesando XML coste explotacion BR.................");
													DistCosteExplotacionesBonifRec br = new DistCosteExplotacionesBonifRec();
													br.setCodigo(BigDecimal.valueOf(bonRec.getCodigo()));
													br.setImporte(bonRec.getImporte());
													br.setDistCosteExplotaciones(distCosteExpl);
													distCosteExpl.getDistCosteBonifRecs().add(br);
												}
											}
											es.agroseguro.costePoliza.costeObjetoAsegurado.SubvencionDesglose[] subvDesgloseArr = expl.getCosteExplotacion().getSubvencionDesgloseArray();
											if (!ArrayUtils.isEmpty(subvDesgloseArr)) {										
												
												for (es.agroseguro.costePoliza.costeObjetoAsegurado.SubvencionDesglose subv : subvDesgloseArr) {
													
													logger.debug("Procesando XML coste explotacion Subv desglose.................");
													DistCosteExplotacionesSubvencion expSubv = new DistCosteExplotacionesSubvencion();
													expSubv.setCodTipo('D');
													expSubv.setCodOrganismo(subv.getCodigoOrganismo().charAt(0));
													expSubv.setCodTipoSubv(BigDecimal.valueOf(subv.getTipo()));
													expSubv.setImporte(subv.getImporte());
													expSubv.setPctSubvencion(subv.getPctSubvencion());
													expSubv.setValorUnitario(subv.getValorUnitario());
													expSubv.setDistCosteExplotaciones(distCosteExpl);
													distCosteExpl.getDistCosteSubvs().add(expSubv);
												}
											}
											distCosteExplotaciones.add(distCosteExpl);
										}
									}
								}								
							}							
						}
					}
				}
			}
		}
		
		logger.debug("getDistribucionCostesExplotaciones [END]");
		return distCosteExplotaciones;
	}

	private void cargaBonifRecargUnificado(DistribucionCoste2015 dc,
			CosteGrupoNegocio costeGrupoNegocio) {
		es.agroseguro.contratacion.costePoliza.BonificacionRecargo[] boniRecargo = costeGrupoNegocio.getBonificacionRecargoArray();
		if (boniRecargo != null) {
			// Bucle para a�adir las distribuciones de coste de las
			// Bonificaciones Recargo
			for (int i = 0; i < boniRecargo.length; i++) {
				BonificacionRecargo2015 bon = new BonificacionRecargo2015();
				bon.setDistribucionCoste2015(dc);
				bon.setCodigo(new BigDecimal(boniRecargo[i].getCodigo()));
				bon.setImporte(boniRecargo[i].getImporte());
				dc.getBonificacionRecargo2015s().add(bon);
			}
		}
	}

	private void cargaSubvencionesDCUnificado(DistribucionCoste2015 dc,
			CosteGrupoNegocio costeGrupoNegocio) {
		es.agroseguro.contratacion.costePoliza.SubvencionCCAA[] subCCAA = costeGrupoNegocio.getSubvencionCCAAArray();
		es.agroseguro.contratacion.costePoliza.SubvencionEnesa[] subEnesa = costeGrupoNegocio.getSubvencionEnesaArray();
		
		// Subvenciones CCAA
		if (subCCAA != null) {
			for (int i = 0; i < subCCAA.length; i++) {
				// Bucle para a�adir las distribuciones de coste de las
				// subvenciones CCAA
				DistCosteSubvencion2015 subv = new DistCosteSubvencion2015();
				subv.setDistribucionCoste2015(dc);
				subv.setCodorganismo(subCCAA[i].getCodigoOrganismo().charAt(0));
				subv.setImportesubv(subCCAA[i].getImporte());
				dc.getDistCosteSubvencion2015s().add(subv);
			}
		}
		// Subvenciones ENESA
		if (subEnesa != null) {
			
			for (int i = 0; i < subEnesa.length; i++) {
				DistCosteSubvencion2015 subv = new DistCosteSubvencion2015();
				subv.setDistribucionCoste2015(dc);
				subv.setCodorganismo('0');
				subv.setCodtiposubv(new BigDecimal(subEnesa[i].getTipo()));
				subv.setImportesubv(subEnesa[i].getImporte());
				dc.getDistCosteSubvencion2015s().add(subv);
			}
		}
	}

	private DistribucionCoste2015 crearDC2015Unificada(Long idpoliza, String codModulo, BigDecimal filaComparativa,
													   CostePoliza costePoliza, CosteGrupoNegocio costeGrupoNegocio,
													   es.agroseguro.seguroAgrario.financiacion.Financiacion financiacion,  int periodoFracc, int opcionFracc, BigDecimal valorOpcionFracc, Long idComparativa) throws DAOException {
		
		DistribucionCoste2015 dc = new DistribucionCoste2015();
		dc.setPoliza((Poliza) super.get(Poliza.class, idpoliza));
		if(null!=costeGrupoNegocio.getGrupoNegocio() && !costeGrupoNegocio.getGrupoNegocio().isEmpty()){
			dc.setGrupoNegocio(costeGrupoNegocio.getGrupoNegocio().toCharArray()[0]);
		}			
		dc.setCodmodulo(codModulo);
		dc.setFilacomparativa(filaComparativa);
		dc.setCostetomador(costeGrupoNegocio.getCosteTomador());
		dc.setPrimacomercial(costeGrupoNegocio.getPrimaComercial());
		dc.setPrimacomercialneta(costeGrupoNegocio.getPrimaComercialNeta());
		dc.setRecargoconsorcio(costeGrupoNegocio.getRecargoConsorcio());
		dc.setReciboprima(costeGrupoNegocio.getReciboPrima());
		dc.setTotalcostetomador(costePoliza.getTotalCosteTomador());
		dc.setIdcomparativa(new BigDecimal(idComparativa));
		
		//opciones de financiaci�n
		if(null!=financiacion){		
			if(financiacion.getPeriodoArray().length>0){
				if(null!=financiacion.getPeriodoArray(0).getDistribucionCoste()){
					dc.setRecargoaval(financiacion.getPeriodoArray(0).getDistribucionCoste().getRecargoAval());
					dc.setRecargofraccionamiento(financiacion.getPeriodoArray(0).getDistribucionCoste().getRecargoFraccionamiento());
					dc.setTotalcostetomador(financiacion.getPeriodoArray(0).getDistribucionCoste().getTotalCosteTomador());
				}
				if(null!=financiacion.getPeriodoArray(0).getPago()){
					dc.setImportePagoFracc(financiacion.getPeriodoArray(0).getPago().getImporte());
				}				
			}
			dc.setPeriodoFracc(periodoFracc);
			dc.setOpcionFracc(opcionFracc);
			dc.setValorOpcionFracc(valorOpcionFracc);
		}
		
		return dc;
	}
	
	/****** Pet. 57626 ** MODIF TAM (26.05.20209 ** Inicio ******/
	/** Se a�aden nuevas funciones necesarias para el formateo del nuevo Formato Unificado de las Agr�colas.*/
	
	private DistribucionCoste2015 crearDC2015UnificadaAgric(Long idpoliza, String codModulo, BigDecimal filaComparativa,
			   CostePoliza costePoliza, CosteGrupoNegocio costeGrupoNegocio,
			   es.agroseguro.seguroAgrario.financiacion.Financiacion financiacion,  int periodoFracc, int opcionFracc, BigDecimal valorOpcionFracc, Long idComparativa) throws DAOException {

		DistribucionCoste2015 dc = new DistribucionCoste2015();
		dc.setPoliza((Poliza) super.get(Poliza.class, idpoliza));
		
		dc.setGrupoNegocio(new Character('1'));//Esquema Unificado. Agr�colas (Todas 1);
		
		dc.setCodmodulo(codModulo);
		dc.setFilacomparativa(filaComparativa);
		dc.setCostetomador(costeGrupoNegocio.getCosteTomador());
		dc.setPrimacomercial(costeGrupoNegocio.getPrimaComercial());
		dc.setPrimacomercialneta(costeGrupoNegocio.getPrimaComercialNeta());
		dc.setRecargoconsorcio(costeGrupoNegocio.getRecargoConsorcio());
		dc.setReciboprima(costeGrupoNegocio.getReciboPrima());
		dc.setTotalcostetomador(costePoliza.getTotalCosteTomador());
		if(idComparativa != null)
			dc.setIdcomparativa(new BigDecimal(idComparativa));
		
		//opciones de financiaci�n
		if(null!=financiacion){		
			if(financiacion.getPeriodoArray().length>0){
				if(null!=financiacion.getPeriodoArray(0).getDistribucionCoste()){
					dc.setRecargoaval(financiacion.getPeriodoArray(0).getDistribucionCoste().getRecargoAval());
					dc.setRecargofraccionamiento(financiacion.getPeriodoArray(0).getDistribucionCoste().getRecargoFraccionamiento());
					dc.setTotalcostetomador(financiacion.getPeriodoArray(0).getDistribucionCoste().getTotalCosteTomador());
				}
				if(null!=financiacion.getPeriodoArray(0).getPago()){
					dc.setImportePagoFracc(financiacion.getPeriodoArray(0).getPago().getImporte());
				}				
			}
			dc.setPeriodoFracc(periodoFracc);
			dc.setOpcionFracc(opcionFracc);
			dc.setValorOpcionFracc(valorOpcionFracc);
		}
		
		return dc;
	}
	
	private Set<DistCosteParcela2015> getDistribucionCostesParcelas(es.agroseguro.distribucionCostesSeguro.ObjetoAsegurado objetosAsegurados[]) throws XmlException{
		
		logger.debug("getDistribucionCostesParcelas [INIT]");
		Set<DistCosteParcela2015> distCosteParcela = new HashSet<DistCosteParcela2015>(0);		
		
		if(null!=objetosAsegurados && objetosAsegurados.length>0){
			
			for (int i = 0; i < objetosAsegurados.length; i++) {
				
				es.agroseguro.costePoliza.parcela.ParcelaDocument parcelaDoc = null;				
				
				NodeList nodoParcList = null;
				nodoParcList= objetosAsegurados[i].getDomNode().getChildNodes();		
				
				for (int j = 0; j < nodoParcList.getLength(); j++) {  
					
					if(nodoParcList.item(j).getNodeType()==Node.ELEMENT_NODE){
						
						DistCosteParcela2015 dcp2015= new DistCosteParcela2015 ();  
						parcelaDoc = es.agroseguro.costePoliza.parcela.ParcelaDocument.Factory.parse(nodoParcList.item(j));
						
						logger.debug("Procesando XML parcela.................");
						
						if(null!=parcelaDoc.getParcela()){
							
							es.agroseguro.costePoliza.parcela.ParcelaDocument.Parcela parc = parcelaDoc.getParcela();
							
							if (parc.getNumero() != 0) {
								dcp2015.setNumero(new BigDecimal(parc.getNumero()));
							}
							
							dcp2015.setHoja(new BigDecimal(parc.getHoja()));							
							dcp2015.setCapitalAsegurado(parc.getCosteParcela().getCapitalAsegurado());							
							dcp2015.setTipo(new BigDecimal(parc.getTipo()));
							
							dcp2015.setPrecio(parc.getPrecio());
							dcp2015.setProduccion(new BigDecimal(parc.getProduccion()));
							dcp2015.setTasacom(parc.getCosteParcela().getTasaComercial());
							dcp2015.setTasacombase(parc.getCosteParcela().getTasaComercialBase());
							
							es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio[] costeGNArr = parc.getCosteParcela().getCosteGrupoNegocioArray();
							if(!ArrayUtils.isEmpty(costeGNArr)) {
								
								logger.debug("Procesando XML coste parcela.................");
								
								if(null!=costeGNArr[0].getCosteTomador()) {
									dcp2015.setCostetomador(costeGNArr[0].getCosteTomador());
								}
								if(null!=costeGNArr[0].getPrimaComercial()) {
									dcp2015.setPrimacomercial(costeGNArr[0].getPrimaComercial());
								}								
								if(null!=costeGNArr[0].getPrimaComercialNeta()) {
									dcp2015.setPrimacomercialneta(costeGNArr[0].getPrimaComercialNeta());
								}								
								if(null!=costeGNArr[0].getRecargoConsorcio()) {
									dcp2015.setRecargoconsorcio(costeGNArr[0].getRecargoConsorcio());
								}
								if(null!=costeGNArr[0].getReciboPrima()) {
									dcp2015.setReciboprima(costeGNArr[0].getReciboPrima());
								}
								
								for (es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio costeGN : costeGNArr) {
									
									logger.debug("Procesando XML coste parcela GN.................");
									
									DistCosteParcela2015GrupoNegocio parcGN = new DistCosteParcela2015GrupoNegocio();
									parcGN.setGruponegocio(costeGN.getGrupoNegocio().charAt(0));
									parcGN.setPrimacomercial(costeGN.getPrimaComercial());
									parcGN.setPrimacomercialneta(costeGN.getPrimaComercialNeta());
									parcGN.setRecargoconsorcio(costeGN.getRecargoConsorcio());
									parcGN.setReciboprima(costeGN.getReciboPrima());
									parcGN.setCostetomador(costeGN.getCosteTomador());
									parcGN.setDistCosteParcela2015(dcp2015);
									dcp2015.getDistCosteGns().add(parcGN);
									
									es.agroseguro.contratacion.costePoliza.SubvencionEnesa[] subvEnesaArr = costeGN.getSubvencionEnesaArray();
									if (!ArrayUtils.isEmpty(subvEnesaArr)) {
										
										for (es.agroseguro.contratacion.costePoliza.SubvencionEnesa subv : subvEnesaArr) {
											
											logger.debug("Procesando XML coste parcela Subv Enesa.................");
											DistCosteParcela2015Subvencion parcSubv = new DistCosteParcela2015Subvencion();
											parcSubv.setCodTipo('E');
											parcSubv.setCodOrganismo('0');
											parcSubv.setCodTipoSubv(BigDecimal.valueOf(subv.getTipo()));
											parcSubv.setImporte(subv.getImporte());
											parcSubv.setPctSubvencion(BigDecimal.ZERO);
											parcSubv.setValorUnitario(BigDecimal.ZERO);
											parcSubv.setDistCosteParcela2015(dcp2015);
											dcp2015.getDistCosteSubvs().add(parcSubv);
										}
									}
									es.agroseguro.contratacion.costePoliza.SubvencionCCAA[] subvCCAAArr = costeGN.getSubvencionCCAAArray();
									if (!ArrayUtils.isEmpty(subvCCAAArr)) {
										
										for (es.agroseguro.contratacion.costePoliza.SubvencionCCAA subv : subvCCAAArr) {
											
											logger.debug("Procesando XML coste parcela Subv CCAA.................");
											DistCosteParcela2015Subvencion parcsubv = new DistCosteParcela2015Subvencion();
											parcsubv.setCodTipo('C');
											parcsubv.setCodOrganismo(subv.getCodigoOrganismo().charAt(0));
											parcsubv.setCodTipoSubv(BigDecimal.ONE);
											parcsubv.setImporte(subv.getImporte());
											parcsubv.setPctSubvencion(BigDecimal.ZERO);
											parcsubv.setValorUnitario(BigDecimal.ZERO);
											parcsubv.setDistCosteParcela2015(dcp2015);
											dcp2015.getDistCosteSubvs().add(parcsubv);
										}
									}
									es.agroseguro.contratacion.costePoliza.BonificacionRecargo[] bonRecArr = costeGN.getBonificacionRecargoArray();
									if (!ArrayUtils.isEmpty(bonRecArr)) {
										
										for (es.agroseguro.contratacion.costePoliza.BonificacionRecargo bonRec : bonRecArr) {
											
											logger.debug("Procesando XML coste parcela BR.................");
											DistCosteParcela2015BonifRec br = new DistCosteParcela2015BonifRec();
											br.setCodigo(BigDecimal.valueOf(bonRec.getCodigo()));
											br.setImporte(bonRec.getImporte());
											br.setDistCosteParcela2015(dcp2015);
											dcp2015.getDistCosteBonifRecs().add(br);
										}
									}
								}
								
								es.agroseguro.costePoliza.costeObjetoAsegurado.SubvencionDesglose[] subvDesgloseArr = parc.getCosteParcela().getSubvencionDesgloseArray();
								if (!ArrayUtils.isEmpty(subvDesgloseArr)) {
									
									for (es.agroseguro.costePoliza.costeObjetoAsegurado.SubvencionDesglose subv : subvDesgloseArr) {
										
										logger.debug("Procesando XML coste parcela Subv desglose.................");
										DistCosteParcela2015Subvencion parcSubv = new DistCosteParcela2015Subvencion();
										parcSubv.setCodTipo('D');
										parcSubv.setCodOrganismo(subv.getCodigoOrganismo().charAt(0));
										parcSubv.setCodTipoSubv(BigDecimal.valueOf(subv.getTipo()));
										parcSubv.setImporte(subv.getImporte());
										parcSubv.setPctSubvencion(subv.getPctSubvencion());
										parcSubv.setValorUnitario(subv.getValorUnitario());
										parcSubv.setDistCosteParcela2015(dcp2015);
										dcp2015.getDistCosteSubvs().add(parcSubv);
									}
								}
							}
							
							dcp2015.setTasacom(parc.getCosteParcela().getTasaComercial());
							dcp2015.setTasacombase(parc.getCosteParcela().getTasaComercialBase());
							
							dcp2015.setNumero(new BigDecimal(parc.getNumero()));
							distCosteParcela.add(dcp2015);
						}
					}
				}
			}
		}
		logger.debug("getDistribucionCostesParcelas [END]");
		return distCosteParcela;
	}
	
	
	
	@Override
	public DistribucionCoste2015 saveDistribucionCoste2015(
			es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza polizaXML,
			Long idpoliza, String codModulo, BigDecimal filaComparativa, Long idComparativa) throws DAOException {


		DistribucionCoste2015 dc = null;
		if (polizaXML == null) {
			throw new DAOException(
					"La p�liza recibida como par�metro de entrada es nula.");
		}
		try {
			if (checkInParams(idpoliza, codModulo, filaComparativa)) {

				es.agroseguro.seguroAgrario.distribucionCoste.DistribucionCoste distribCosteXML = polizaXML
						.getDatosCalculo().getDistribucionCoste1();
				if (distribCosteXML == null) {
					throw new DAOException(
							"La p�liza recibida como par�metro de entrada no tiene distribuci�n de costes1.");
				}

				dc = new DistribucionCoste2015();
				dc.setGrupoNegocio(new Character('1'));//Esquema no unificado. Agr�colas (Todas 1);
				dc.setPoliza((Poliza) super.get(Poliza.class, idpoliza));
				dc.setCodmodulo(codModulo);
				dc.setFilacomparativa(filaComparativa);
				dc.setCostetomador(distribCosteXML.getCosteTomador());
				dc.setPrimacomercial(distribCosteXML.getPrimaComercial());
				dc.setPrimacomercialneta(distribCosteXML.getPrimaComercialNeta());
				dc.setRecargoconsorcio(distribCosteXML.getRecargoConsorcio());
				dc.setReciboprima(distribCosteXML.getReciboPrima());
				if(idComparativa != null)
					dc.setIdcomparativa(new BigDecimal(idComparativa));
				// MPM - 28/05/2015
				//dc.setTotalcostetomador(distribCosteXML.getTotalCosteTomador());
				dc.setTotalcostetomador(distribCosteXML.getTotalCosteTomador());
				if (distribCosteXML.getRecargoAval()!= null) {
					dc.setRecargoaval(distribCosteXML.getRecargoAval());
	  			}
	  			if (distribCosteXML.getRecargoFraccionamiento()!= null) {
	  				dc.setRecargofraccionamiento(distribCosteXML.getRecargoFraccionamiento());
	  			}
				
	  			dc.getDistCosteSubvencion2015s().clear();

	  			// Carga las subvenciones de CCAA y ENESA en el objeto de la distribuci�n de costes
				cargarSubvencionesDC(dc, distribCosteXML);
				
				//BonificacionRecargo
				BonificacionRecargo[] boniRecargo = distribCosteXML.getBonificacionRecargoArray();
				if (boniRecargo != null) {
					// Bucle para a�adir las distribuciones de coste de las
					// Bonificaciones Recargo
					for (int i = 0; i < boniRecargo.length; i++) {
						BonificacionRecargo2015 bon = new BonificacionRecargo2015();
						bon.setDistribucionCoste2015(dc);
						bon.setCodigo(new BigDecimal(boniRecargo[i].getCodigo()));
						bon.setImporte(boniRecargo[i].getImporte());
						dc.getBonificacionRecargo2015s().add(bon);
					}
				}
				
				// Bucle para a�adir las distribuciones de coste Parcela
				dc.getDistCosteParcela2015s().clear();
				
				for (ObjetoAsegurado objetoAsegurado : polizaXML.getObjetoAseguradoArray()) {
					ParcelaDocument parcelaDoc = ParcelaDocument.Factory.parse(new StringReader(objetoAsegurado.xmlText()));
					ParcelaDocument.Parcela parcela = parcelaDoc.getParcela();	
					DistCosteParcela2015 dCPar = new DistCosteParcela2015();
					
					
					
					dCPar.setDistribucionCoste2015(dc);
					dCPar.setCapitalAsegurado( parcela.getCapitalAsegurado());
					dCPar.setHoja(new BigDecimal(parcela.getHoja()));
					dCPar.setNumero(new BigDecimal(parcela.getNumero()));
					dCPar.setPrecio(parcela.getPrecio());
					dCPar.setProduccion(new BigDecimal(parcela.getProduccion()));
					dCPar.setTasacom(parcela.getTasaComercial());
					dCPar.setTasacombase(parcela.getTasaComercialBase());
					dCPar.setTipo(new BigDecimal (parcela.getTipo()));
					
					dCPar.setCostetomador(parcela.getDistribucionCoste1().getCosteTomador());
					dCPar.setPrimacomercial(parcela.getDistribucionCoste1().getPrimaComercial()) ;
					dCPar.setPrimacomercialneta(parcela.getDistribucionCoste1().getPrimaComercialNeta() );
					dCPar.setRecargoaval(parcela.getDistribucionCoste1().getRecargoAval());
					dCPar.setRecargoconsorcio(parcela.getDistribucionCoste1().getRecargoConsorcio());
					dCPar.setRecargofraccionamiento(parcela.getDistribucionCoste1().getRecargoFraccionamiento() );
					dCPar.setReciboprima(parcela.getDistribucionCoste1().getReciboPrima() ) ;
					dCPar.setTotalcostetomador(parcela.getDistribucionCoste1().getTotalCosteTomador()); 
					
					dc.getDistCosteParcela2015s().add(dCPar);
				}
				
				super.saveOrUpdate(dc);
			}
		} catch (Exception e) {
			throw new DAOException(
					"Se ha producido un error durante la modificaci�n de la distribuci�n de coste.",
					e);
		}
		return dc;
	}

	private void cargarSubvencionesDC(DistribucionCoste2015 dc,	es.agroseguro.seguroAgrario.distribucionCoste.DistribucionCoste distribCosteXML) {
		SubvencionCCAA[] subCCAA = distribCosteXML
				.getSubvencionCCAAArray();
		SubvencionEnesa[] subEnesa = distribCosteXML
				.getSubvencionEnesaArray();
		// Subvenciones CCAA
		if (subCCAA != null) {
			for (int i = 0; i < subCCAA.length; i++) {
				// Bucle para a�adir las distribuciones de coste de las
				// subvenciones CCAA
				DistCosteSubvencion2015 subv = new DistCosteSubvencion2015();
				subv.setDistribucionCoste2015(dc);
				subv.setCodorganismo(subCCAA[i].getCodigoOrganismo().charAt(0));
				subv.setImportesubv(subCCAA[i].getImporte());
				dc.getDistCosteSubvencion2015s().add(subv);
			}
		}
		// Subvenciones ENESA
		if (subEnesa != null) {
			
			for (int i = 0; i < subEnesa.length; i++) {
				DistCosteSubvencion2015 subv = new DistCosteSubvencion2015();
				subv.setDistribucionCoste2015(dc);
				subv.setCodorganismo('0');
				subv.setCodtiposubv(new BigDecimal(subEnesa[i].getTipo()));
				subv.setImportesubv(subEnesa[i].getImporte());
				dc.getDistCosteSubvencion2015s().add(subv);
			}
		}
	}

	@Override
	public void deleteDistribucionCoste2015(Long idpoliza, String codModulo, Long idComparativa) throws DAOException {		
		
		Session session = obtenerSession();
		
		Criteria criteria = session.createCriteria(DistribucionCoste2015.class);

		criteria.createAlias("poliza", "poliza");
		criteria.add(Restrictions.eq("poliza.idpoliza", idpoliza));
		
		if(codModulo!=null){
			criteria.add(Restrictions.eq("codmodulo", codModulo));
		}else{
			criteria.add(Restrictions.isNull("codmodulo"));
		}
		if(idComparativa!=null){
			criteria.add(Restrictions.eq("idcomparativa", BigDecimal.valueOf(idComparativa)));
		}else{
			criteria.add(Restrictions.isNull("idcomparativa"));
		}
				
		super.deleteAll(criteria.list());
	}
	
	public DistribucionCoste2015 getDistribucionCoste2015(final Long idPoliza,
			final String codModulo, final BigDecimal filaComparativa)
			throws DAOException {
		final String[] parametros = new String[] { "poliza.idpoliza",
				"codmodulo", "filacomparativa" };
		final Object[] valores = new Object[] { idPoliza, codModulo,
				filaComparativa };
		DistribucionCoste2015 distCoste = null;
		try {
			@SuppressWarnings("unchecked")
			List<DistribucionCoste2015> auxList = (List<DistribucionCoste2015>) super
					.findFiltered(DistribucionCoste2015.class, parametros, valores,
							null);
			if (auxList != null && auxList.size() > 0) {
				distCoste = auxList.get(0);
			}
		} catch (Exception e) {
			throw new DAOException(
					"Se ha producido un error durante la obtenci�n de la distribuci�n de coste.",
					e);
		}
		return distCoste;
	}
	
	@SuppressWarnings("unchecked")
	public List<DistribucionCoste2015> getDistribucionCoste2015ByIdPoliza(final Long idPoliza)
			throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(DistribucionCoste2015.class);

			criteria.createAlias("poliza", "poliza");
			criteria.add(Restrictions.eq("poliza.idpoliza", idPoliza));
			
			List<DistribucionCoste2015> dc =  criteria.list();
			return dc;

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					"Se ha producido un error durante la obtenci�n de la distribuci�n de coste.",
					ex);
		} 
	}

	@SuppressWarnings("rawtypes")
	@Override
	public BigDecimal getTotalCosteTomadorAFinanciar(Long idPoliza)throws DAOException {
		BigDecimal res=null;
		String sql ="select extractvalue(value(CalculoAlternativoFinanciacion), '//@totalCosteTomadorAFinanciar') as totalCosteTomadorAFinanciar " +
					"from TB_ENVIOS_AGROSEGURO e, " +
					"TABLE (xmlsequence(extract(xmltype(e.calculo),'ns7:Poliza', 'xmlns:ns7=http://www.agroseguro.es/DistribucionCostesSeguro'))) docs, " +
					"TABLE (xmlsequence(extract(value(docs),'/ns7:Poliza/CalculoAlternativoFinanciacion', 'xmlns:ns7=http://www.agroseguro.es/DistribucionCostesSeguro '))) CalculoAlternativoFinanciacion " +
					"WHERE e.id = (Select max(ea.id) from tb_envios_agroseguro ea WHERE ea.idpoliza= " + idPoliza + " and ea.tipoenvio='CL')";
		
		Session session = obtenerSession();
		logger.debug(sql);		
		List resultado = session.createSQLQuery(sql).list();
		if(null!=resultado && resultado.size()>0){
			res = new BigDecimal((String) resultado.get(0));
			
		}
		return res;
	}

	@Override
	public void updateComsDistCoste2015(Set<DistribucionCoste2015> distCostes,
			List<VistaImportesPorGrupoNegocio> vistasImportes, String codModulo, BigDecimal idComparativa)
			throws DAOException {
		NumberFormat nf = NumberFormat.getInstance(new Locale("es", "ES"));
		

		for (VistaImportesPorGrupoNegocio vistaImportesGN : vistasImportes) {

			for (DistribucionCoste2015 distCoste : distCostes) {
				// ESC-25609: Se actualiza los importes del modulo correspondiente
				if (distCoste.getGrupoNegocio().equals(vistaImportesGN.getCodGrupoNeg().charAt(0))
						&& codModulo.equals(distCoste.getCodmodulo())
						&& idComparativa.equals(distCoste.getIdcomparativa())) {
					logger.debug("Actualizar distribucion de coste de grupo " + distCoste.getGrupoNegocio());
					logger.debug("Comisi�n Entidad -> " + vistaImportesGN.getComMediadorE());
					logger.debug("Comisi�n E-S Mediadora -> " + vistaImportesGN.getComMediadorE_S());
					try {
						if (!vistaImportesGN.getComMediadorE().equals("N/D")) {
							distCoste.setImpComsEntidad(
									new BigDecimal(nf.parse(vistaImportesGN.getComMediadorE()).toString()));
						}
					} catch (ParseException e) {
						logger.error("Error al actualizar el importe de la comision de entidad: "
								+ vistaImportesGN.getComMediadorE());
					}
					try {
						if (!vistaImportesGN.getComMediadorE_S().equals("N/D")) {
							distCoste.setImpComsESMed(
									new BigDecimal(nf.parse(vistaImportesGN.getComMediadorE_S()).toString()));
						}
					} catch (ParseException e) {
						logger.error("Error al actualizar el importe de la comision de E-S Mediadora: "
								+ vistaImportesGN.getComMediadorE_S());
					}
					break;
				}
			}
		}
	}
	
	public String getLiteralBonificacion (String codRecargo) {
		
		Session session = obtenerSession();
		String literal = null;
		String sql ="SELECT INITCAP(descripcion)" + 
					"FROM TB_SC_C_BONIF_RECARG br WHERE br.cod_bon_rec =" + codRecargo; 
			
		if(session.createSQLQuery(sql).list().size() > 0){
			literal =  (String) session.createSQLQuery(sql).list().get(0);
		}
		return literal;
	}
}