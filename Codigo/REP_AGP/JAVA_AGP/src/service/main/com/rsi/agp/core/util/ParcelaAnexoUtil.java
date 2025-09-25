package com.rsi.agp.core.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;
import com.rsi.agp.dao.tables.anexo.CapitalDTSVariable;
import com.rsi.agp.dao.tables.anexo.Parcela;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.commons.TerminoId;
import com.rsi.agp.dao.tables.cpl.Cultivo;
import com.rsi.agp.dao.tables.cpl.CultivoId;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.cpl.VariedadId;
import com.rsi.agp.dao.tables.poliza.ParcelaCobertura;
import com.rsi.agp.dao.tables.poliza.SubvParcelaCCAA;
import com.rsi.agp.dao.tables.poliza.SubvParcelaENESA;
import com.rsi.agp.vo.CapitalAseguradoVO;
import com.rsi.agp.vo.DatoVariableParcelaVO;
import com.rsi.agp.vo.ParcelaVO;
import com.rsi.agp.vo.PrecioVO;
import com.rsi.agp.vo.ProduccionVO;
import com.rsi.agp.vo.RiesgoVO;

@SuppressWarnings("unchecked")
public class ParcelaAnexoUtil {

	private static final Log logger = LogFactory.getLog(ParcelaAnexoUtil.class);

	/**
	 * Transforma un objeto parcela de anexo en un objeto parcelaVO
	 * 
	 * @param objeto
	 *            parcela a transformar
	 * @return objeto parcelaVO para la pantalla de parcelas
	 */
	public static ParcelaVO getParcelaVO(Parcela parcela, GenericDao<?> dao)
			throws Exception {

		ParcelaVO parcelaVO = new ParcelaVO();
		String metrosCuadrados = "";
		try {

			parcelaVO.setCodPoliza(parcela.getAnexoModificacion().getPoliza().getIdpoliza().toString());
			parcelaVO.setIdAnexoModificacion(parcela.getAnexoModificacion().getId().toString());
			parcelaVO.setTipoParcela(String.valueOf(parcela.getTipoparcela()));
			parcelaVO.setCodParcela(parcela.getId().toString());
			
			// ----------------------------
			// --- DATOS PARCELA ---
			// ----------------------------
			parcelaVO.setRefIdParcela(parcela.getId().toString());
			parcelaVO.setIdparcelaanxestructura(parcela.getIdparcelaanxestructura() == null ? null
					: parcela.getIdparcelaanxestructura().toString());

			// --- Ubicacion ---
			Termino termino = null;
			try {
				TerminoId terminoId = new TerminoId();
				terminoId.setCodprovincia(parcela.getCodprovincia());
				terminoId.setCodcomarca(parcela.getCodcomarca());
				terminoId.setCodtermino(parcela.getCodtermino());
				terminoId.setSubtermino(parcela.getSubtermino());
				termino = (Termino) dao.getObject(Termino.class, terminoId);
				if (termino != null) {
					parcelaVO.setDesProvincia(termino.getProvincia().getNomprovincia());
					parcelaVO.setDesComarca(termino.getComarca().getNomcomarca());
					// Utiliza el método getNomTermino(fechaInicioContratacion, esGanado) en lugar del antiguo getNomtermino() para adaptarse a los nuevos requisitos de la P0079469
					// Esta versión ahora tiene en cuenta la fecha de inicio de contratación y si la línea es de ganado para determinar el nombre correcto del termino
					Date fechaInicioContratacion = parcela.getAnexoModificacion().getPoliza().getLinea().getFechaInicioContratacion();
					parcelaVO.setDesTermino(termino.getNomTerminoByFecha(fechaInicioContratacion, false));
				}
			} catch (Exception e) {
				// La ubicacion no existe
				logger.error(e);
			}
			parcelaVO.setCodProvincia(parcela.getCodprovincia().toString());			
			parcelaVO.setCodComarca(parcela.getCodcomarca().toString());			
			parcelaVO.setCodTermino(parcela.getCodtermino().toString());			
			parcelaVO.setCodSubTermino(parcela.getSubtermino().toString());

			// --- Sigpac ---
			if (parcela.getCodprovsigpac() != null)
				parcelaVO.setProvinciaSigpac(parcela.getCodprovsigpac().toString());
			if (parcela.getCodtermsigpac() != null)
				parcelaVO.setTerminoSigpac(parcela.getCodtermsigpac().toString());
			if (parcela.getAgrsigpac() != null)
				parcelaVO.setAgregadoSigpac(parcela.getAgrsigpac().toString());
			if (parcela.getZonasigpac() != null)
				parcelaVO.setZonaSigpac(parcela.getZonasigpac().toString());
			if (parcela.getPoligonosigpac() != null)
				parcelaVO.setPoligonoSigpac(parcela.getPoligonosigpac().toString());
			if (parcela.getParcelasigpac() != null)
				parcelaVO.setParcelaSigpac(parcela.getParcelasigpac().toString());
			if (parcela.getRecintosigpac() != null)
				parcelaVO.setRecintoSigpac(parcela.getRecintosigpac().toString());
			// Otros
			if (parcela.getNomparcela() != null)
				parcelaVO.setNombreParcela(parcela.getNomparcela());
			Cultivo cultivo = null;
			try {
				CultivoId cultivoId = new CultivoId(
						parcela.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid(),
						parcela.getCodcultivo());
				cultivo = (Cultivo) dao.getObject(Cultivo.class, cultivoId);
			} catch (Exception e) {
				// El cultivo no existe
				logger.error(e);
			}
			Variedad variedad = null;
			try {
				VariedadId variedadId = new VariedadId(
						parcela.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid(),
						parcela.getCodcultivo(), parcela.getCodvariedad());
				variedad = (Variedad) dao.getObject(Variedad.class, variedadId);
			} catch (Exception e) {
				// La variedad no existe
				logger.error(e);
			}
			if (parcela.getCodcultivo() != null) {
				if (cultivo != null) {
					parcelaVO.setCultivo(parcela.getCodcultivo().toString());
					parcelaVO.setDesCultivo(cultivo.getDescultivo());
				} else {
					parcelaVO.setCultivo(null);
					parcelaVO.setDesCultivo(null);
				}
			}
			if (parcela.getCodvariedad() != null) {
				if (variedad != null) {
					parcelaVO.setVariedad(parcela.getCodvariedad().toString());
					parcelaVO.setDesVariedad(variedad.getDesvariedad());
				} else {
					// La variedad no existe
					parcelaVO.setVariedad(null);
					parcelaVO.setDesVariedad(null);
				}
			}

			// ----------------------------
			// --- CAPITALES ASEGURADOS ---
			// ----------------------------
			for (CapitalAsegurado capitalAsegurado : parcela.getCapitalAsegurados()) {
				if (capitalAsegurado != null) {
					CapitalAseguradoVO capitalAseguradoVO = new CapitalAseguradoVO();

					if (capitalAsegurado.getTipoCapital() != null
							&& capitalAsegurado.getTipoCapital().getCodtipocapital() != null) {
						// PRECIO/PRODUCCION
						BigDecimal precioMax = new BigDecimal(0);
						BigDecimal produccionMax = new BigDecimal(0);

						// Acumulador de precios y acumulador de producciones
						ArrayList<PrecioVO> listPrecios = new ArrayList<PrecioVO>();
						ArrayList<ProduccionVO> listProducciones = new ArrayList<ProduccionVO>();

						// Es precio
						if (capitalAsegurado.getPrecio() != null) {
							if (precioMax.compareTo(capitalAsegurado.getPrecio()) == -1) {
								precioMax = capitalAsegurado.getPrecio();
							}

							// Add to list precios
							PrecioVO precioVO = new PrecioVO();
							precioVO.setCodModulo(parcela.getAnexoModificacion().getPoliza().getCodmodulo());
							precioVO.setLimMax(capitalAsegurado.getPrecio().toString());
							precioVO.setLimMin("0");
							listPrecios.add(precioVO);

						}

						// Es produccion
						if (capitalAsegurado.getProduccion() != null) {
							if (produccionMax.compareTo(capitalAsegurado.getProduccion()) == -1) {
								produccionMax = capitalAsegurado.getProduccion();
							}

							// Add to list producciones
							ProduccionVO ProduccionVO = new ProduccionVO();
							ProduccionVO.setCodModulo(parcela.getAnexoModificacion().getPoliza().getCodmodulo());
							ProduccionVO.setLimMax(capitalAsegurado.getProduccion().toString());
							ProduccionVO.setLimMin("1");
							listProducciones.add(ProduccionVO);
						}

						capitalAseguradoVO.setPrecio(precioMax.toString());
						capitalAseguradoVO.setProduccion(produccionMax.toString());
						capitalAseguradoVO.setListPrecios(listPrecios);
						capitalAseguradoVO.setListProducciones(listProducciones);

					}

					if (capitalAsegurado.getTipoCapital() != null) {
						capitalAseguradoVO
								.setCodtipoCapital(capitalAsegurado.getTipoCapital().getCodtipocapital().toString());
						capitalAseguradoVO.setDesTipoCapital(capitalAsegurado.getTipoCapital().getDestipocapital());
					}
					if (capitalAsegurado.getId() != null)
						capitalAseguradoVO.setId(capitalAsegurado.getId().toString());
					if (capitalAsegurado.getSuperficie() != null)
						capitalAseguradoVO.setSuperficie(capitalAsegurado.getSuperficie().toString());

					ArrayList<DatoVariableParcelaVO> datosVariables = new ArrayList<DatoVariableParcelaVO>();
					// ----------------------------
					// --- DATOS VARIABLES ---
					// ----------------------------

					// ---------- Creo el HashMap (listas multiseleccionables) ----------
					HashMap<BigDecimal, String> mapCodConceptos = new HashMap<BigDecimal, String>();

					for (CapitalDTSVariable datoVariableParcela : capitalAsegurado.getCapitalDTSVariables()) {
						BigDecimal cod = datoVariableParcela.getCodconcepto();
						String value = datoVariableParcela.getValor();

						if (mapCodConceptos.get(cod) == null) {
							mapCodConceptos.put(cod, value);
						} else {
							String valueMap = mapCodConceptos.get(cod);
							mapCodConceptos.remove(cod);
							mapCodConceptos.put(cod, valueMap + ";" + value);
						}
					}
					// ------------------------------ HashMap ----------------------------------

					for (CapitalDTSVariable datoVariableParcela : capitalAsegurado.getCapitalDTSVariables()) {

						boolean isMulti = isMulti(datoVariableParcela.getCodconcepto());

						BigDecimal codconcepto = datoVariableParcela.getCodconcepto();
						Long iddatovariable = datoVariableParcela.getId();
						String valor = null;

						if (isMulti) {
							valor = mapCodConceptos.get(codconcepto);
						} else {
							valor = datoVariableParcela.getValor();
						}

						// Metros cuadrados
						if (datoVariableParcela.getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_METROS_CUADRADOS))) {
							metrosCuadrados = datoVariableParcela.getValor();
						}
						
						datosVariables.add(new DatoVariableParcelaVO(codconcepto.intValue(), valor, iddatovariable));

						// set metrosCuadrados

						capitalAseguradoVO.setDatosVariablesParcela(datosVariables);
					}

					if (!StringUtils.nullToString(metrosCuadrados).equals(""))
						capitalAseguradoVO.setMetrosCuadrados(metrosCuadrados);
					else
						capitalAseguradoVO.setMetrosCuadrados(capitalAsegurado.getSuperficie().toString());

					capitalAseguradoVO.setDatosVariablesParcela(datosVariables);
					capitalAseguradoVO.setSuperficie(capitalAsegurado.getSuperficie().toString());

					parcelaVO.getCapitalesAsegurados().add(capitalAseguradoVO);
				}
			}			
			parcelaVO.setCapitalAsegurado(parcelaVO.getCapitalesAsegurados().get(0));

			// ----------------------------
			// --- SUBVENCIONES ---
			// ----------------------------
			// CCAA
			ArrayList<Integer> subCCAA = new ArrayList<Integer>();
			List<SubvParcelaCCAA> lstSubvCCAA = (List<SubvParcelaCCAA>) dao.getObjects(SubvParcelaCCAA.class,
					"parcela.idparcela", parcela.getId());
			Iterator<SubvParcelaCCAA> it1 = lstSubvCCAA.iterator();

			while (it1.hasNext()) {
				SubvParcelaCCAA aux = it1.next();
				subCCAA.add(
						new Integer(aux.getSubvencionCCAA().getTipoSubvencionCCAA().getCodtiposubvccaa().intValue()));
			}
			parcelaVO.setSubvencionesCCAA(subCCAA);

			// ENESA
			ArrayList<Integer> subENESA = new ArrayList<Integer>();
			List<SubvParcelaENESA> lstSubvEnesa = (List<SubvParcelaENESA>) dao.getObjects(SubvParcelaENESA.class,
					"parcela.idparcela", parcela.getId());
			Iterator<SubvParcelaENESA> it2 = lstSubvEnesa.iterator();
			while (it2.hasNext()) {
				SubvParcelaENESA aux = it2.next();
				subENESA.add(new Integer(
						aux.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa().intValue()));
			}
			parcelaVO.setSubvencionesENESA(subENESA);

			// ----------------------------
			// --- COBERTURAS ---
			// ----------------------------

			List<ParcelaCobertura> lstCoberturas = (List<ParcelaCobertura>) dao.getObjects(ParcelaCobertura.class,
					"parcela.idparcela", parcela.getId());
			for (ParcelaCobertura parcelaCobertura : lstCoberturas) {
				RiesgoVO riesgoVO = new RiesgoVO();

				riesgoVO.setCodConcepto(parcelaCobertura.getDiccionarioDatos().getCodconcepto().toString());
				riesgoVO.setCodConceptoPpalMod(
						parcelaCobertura.getConceptoPpalModulo().getCodconceptoppalmod().toString());
				riesgoVO.setCodModulo(parcelaCobertura.getRiesgoCubierto().getModulo().getId().getCodmodulo());
				riesgoVO.setCodRiesgoCubierto(
						parcelaCobertura.getRiesgoCubierto().getId().getCodriesgocubierto().toString());
				riesgoVO.setCodValor(parcelaCobertura.getCodvalor().toString());

				parcelaVO.getRiesgosSeleccionados().add(riesgoVO); // ADD COBERTURA
			}
		} catch (Exception exception) {
			logger.error("No se pudo cargar los datos de la parcela.", exception);
			throw (new Exception("No se pudo cargar los datos de la parcela.", exception));
		}

		return parcelaVO;
	}
	
	/**
	 * Transforma un objeto CapitalAsegurado en un objeto CapitalAseguradoVO 
	 */
	public static CapitalAseguradoVO getCapitalAseguradoVO(final CapitalAsegurado obj) throws BusinessException {
		CapitalAseguradoVO capasegVO = new CapitalAseguradoVO();
		String codModulo = obj.getParcela().getAnexoModificacion().getCodmodulo();
		capasegVO.setId(obj.getId().toString());
		capasegVO.setCodtipoCapital(obj.getTipoCapital().getCodtipocapital().toString());
		capasegVO.setDesTipoCapital(obj.getTipoCapital().getDestipocapital());
		capasegVO.setSuperficie(obj.getSuperficie() == null ? "0" : obj.getSuperficie().toString());
		Set<CapitalDTSVariable> lstDatVarParc = obj.getCapitalDTSVariables();
		if (lstDatVarParc != null && !lstDatVarParc.isEmpty()) {
			List<DatoVariableParcelaVO> datosVariables = new ArrayList<DatoVariableParcelaVO>(lstDatVarParc.size());
			for (CapitalDTSVariable datoVariableParcela : lstDatVarParc) {
				BigDecimal codConcepto = datoVariableParcela.getCodconcepto();
				String valor = datoVariableParcela.getValor();
				datosVariables
						.add(new DatoVariableParcelaVO(codConcepto.intValue(), valor, datoVariableParcela.getId()));
			}
			capasegVO.setDatosVariablesParcela(datosVariables);
		}
		ArrayList<PrecioVO> listPrecios = new ArrayList<PrecioVO>();
		ArrayList<ProduccionVO> listProducciones = new ArrayList<ProduccionVO>();
		String precio = obj.getPrecio() == null ? "0" : obj.getPrecio().toString();
		PrecioVO precioVO = new PrecioVO();
		precioVO.setCodModulo(codModulo);
		precioVO.setLimMax(precio);
		precioVO.setLimMin("0");
		listPrecios.add(precioVO);
		String produccion = obj.getProduccion() == null ? "0" : obj.getProduccion().toString();
		ProduccionVO ProduccionVO = new ProduccionVO();
		ProduccionVO.setCodModulo(codModulo);
		ProduccionVO.setLimMax(produccion);
		ProduccionVO.setLimMin("1");
		listProducciones.add(ProduccionVO);
		capasegVO.setPrecio(precio);
		capasegVO.setProduccion(produccion);
		capasegVO.setListPrecios(listPrecios);
		capasegVO.setListProducciones(listProducciones);
		return capasegVO;
	}

	private static boolean isMulti(BigDecimal codconcepto) {
		if (codconcepto.compareTo(new BigDecimal(132)) == 0 || codconcepto.compareTo(new BigDecimal(124)) == 0
				|| codconcepto.compareTo(new BigDecimal(620)) == 0) {
			return true;
		} else {
			return false;
		}
	}
}
