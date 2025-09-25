package com.rsi.agp.core.managers.impl.anexoMod;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.impl.CalculoPrecioProduccionManager;
import com.rsi.agp.core.managers.impl.DeclaracionesModificacionPolizaManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.ParcelaAnexoUtil;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.anexo.IAnexoModCambioMasivoSWDao;
import com.rsi.agp.dao.models.poliza.ICalculoPrecioDao;
import com.rsi.agp.dao.models.poliza.ISeleccionPolizaDao;
import com.rsi.agp.dao.tables.anexo.AnexoModSWCambioMasivo;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;
import com.rsi.agp.dao.tables.anexo.CapitalDTSVariable;
import com.rsi.agp.dao.tables.anexo.Parcela;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.Precio;
import com.rsi.agp.vo.ParcelaVO;
import com.rsi.agp.vo.PrecioVO;
import com.rsi.agp.vo.PreciosProduccionesVO;
import com.rsi.agp.vo.ProduccionVO;

public class AnexoModCambioMasivoSWManager implements IAnexoModCambioMasivoSWManager {

	private static final Log logger = LogFactory.getLog(AnexoModCambioMasivoSWManager.class);

	private IAnexoModCambioMasivoSWDao anexoModCambioMasivoSWDao;
	private ISeleccionPolizaDao seleccionPolizaDao;
	private ICalculoPrecioDao calculoPrecioDao;
	private DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager;
	private CalculoPrecioProduccionManager calculoPrecioProduccionManager;

	@Override
	public Set<Long> cambioMasivoSW(AnexoModSWCambioMasivo anexoModSWCambioMasivo, String idsRowsCheckedCM,
			HashMap<String, String> mensajesError, boolean guardarSoloPrecioYProd) throws BusinessException {

		Set<Long> colIdParcelasAnexo = new HashSet<Long>();
		try {
			logger.debug("Init: cambioMasivoSW - AnexoModCambioMasivoSWManager");
			anexoModSWCambioMasivo.setListParcelas(getListParcelas(idsRowsCheckedCM));
			AnexoModificacion am = declaracionesModificacionPolizaManager
					.getAnexoModifById(anexoModSWCambioMasivo.getIdAnexoMod());

			for (Parcela p : am.getParcelas()) {
				if (anexoModSWCambioMasivo.getListParcelas().contains(p.getId())) {

					setUpdates(anexoModSWCambioMasivo, p, mensajesError, guardarSoloPrecioYProd);
					colIdParcelasAnexo.add(p.getId());
					anexoModCambioMasivoSWDao.evict(p);
					anexoModCambioMasivoSWDao.evict(am);
				}
			}
			if (!mensajesError.containsKey("alerta")) {
				// Modificamos el estado de las parcela modificadas
				anexoModCambioMasivoSWDao.cambiaEstadoParcela(anexoModSWCambioMasivo.getListParcelas());
			}

			logger.debug("End: cambioMasivoSW - AnexoModCambioMasivoSWManager");

		} catch (Exception e) {
			logger.error("Ha ocurrido un error en el cambio masivo de parcelas de anexo de modificacion", e);
			throw new BusinessException("Ha ocurrido un error en el cambio masivo de parcelas de anexo de modificacion",
					e);
		}
		return colIdParcelasAnexo;
	}

	public void recalculaPrecioProduccion(Collection<Parcela> parcelas, boolean esConWS,
			Map<String, ProduccionVO> mapaRendimientosProd) throws Exception {

		String valor = "";
		Map<String, BigDecimal> rdtosGrp = new HashMap<String, BigDecimal>();
		Map<String, BigDecimal> preciosGrp = new HashMap<String, BigDecimal>();
		Map<BigDecimal, BigDecimal> mapLineas = new HashMap<BigDecimal, BigDecimal>();
		boolean addIdentificador;

		try {
			Iterator<Parcela> itParcelaAux = parcelas.iterator();
			Parcela auxParcela = itParcelaAux.next();
			String modulo = auxParcela.getAnexoModificacion().getCodmodulo();
			itParcelaAux = null;

			BigDecimal codConceptoProduccion = new BigDecimal(
					ResourceBundle.getBundle("agp").getString("codConceptoRendimiento"));

			boolean aplica = seleccionPolizaDao.aplicaSistemaCultivoARendimiento(
					auxParcela.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid());
			
			logger.debug("**@@** Valor de aplica:"+aplica);

			mapLineas = seleccionPolizaDao.getLineasRecalculo();

			if (mapLineas.containsKey(auxParcela.getAnexoModificacion().getPoliza().getLinea().getCodlinea())) {
				addIdentificador = false;

			} else {
				addIdentificador = true;
			}

			// -- PARCELAS --
			logger.debug ("**@@** Antes del bucle de parcelas");
			for (Parcela parcela : parcelas) {

				logger.debug ("**@@** Valor de parcela:"+parcela.getId());
				// Para evitar OutOfMemory al cargar las parcelas de la pac
				// cuando no hay cultivo o variedad
				if (!parcela.getCodcultivo().equals(new BigDecimal(999))
						&& !parcela.getCodvariedad().equals(new BigDecimal(999))) {
					ParcelaVO parcelaVO = ParcelaAnexoUtil.getParcelaVO(parcela, anexoModCambioMasivoSWDao);
					int indiceCapital = 0;

					for (CapitalAsegurado capitalAsegurado : parcela.getCapitalAsegurados()) {
						logger.debug("**@@** Valor de capitalAsegurado:"+capitalAsegurado.getId());

						for (CapitalDTSVariable datoVariable : capitalAsegurado.getCapitalDTSVariables()) {
							logger.debug("**@@** Valor de datoVariable.getCodConcepto:"+datoVariable.getCodconcepto());
							if (aplica) {
								if ((datoVariable.getCodconcepto()).equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_SISTCULTIVO))) {
									valor = "#" + datoVariable.getValor().toString();
									logger.debug ("**@@** Valor de valor:"+valor);
									logger.debug("**@@** Antes del break");
									break;
								}
							}
						}/* ESC-16599 Cerramos el for antes*/
						String identificador = parcela.getAnexoModificacion().getCodmodulo() + "#"
								+ parcela.getCodcultivo() + "#" + parcela.getCodvariedad() + "#"
								+ parcela.getCodprovincia() + "#" + parcela.getCodcomarca() + "#"
								+ parcela.getCodtermino() + "#" + parcela.getSubtermino() + "#"
								+ capitalAsegurado.getTipoCapital().getCodtipocapital() + valor;
						logger.debug("Antes de setPrecioProduccionCapAseg con capitalAsegurado.getId()"+capitalAsegurado.getId());
						logger.debug ("Valor de identificador:"+identificador);
						
						setPrecioProduccionCapAseg(rdtosGrp, preciosGrp, codConceptoProduccion, parcela, parcelaVO,
								indiceCapital, capitalAsegurado, identificador, addIdentificador, modulo, esConWS,
								mapaRendimientosProd);
						/*}*/
					}
				}
			}

		} catch (Exception e) {
			logger.error("Ha ocurrido un error al recalcular precio/produccion de las parcelas de los anexos", e);
			throw e;
		}

	}

	private void setPrecioProduccionCapAseg(Map<String, BigDecimal> rdtosGrp, Map<String, BigDecimal> preciosGrp,
			BigDecimal codConceptoProduccion, Parcela par, ParcelaVO parcelaVO, int indiceCapital,
			CapitalAsegurado capitalAsegurado, String identificador, boolean addIdentificador, String modulo,
			boolean esConWS, Map<String, ProduccionVO> mapaRendimientosProd) throws Exception {

		List<PrecioVO> precios;
		BigDecimal auxProduccion = new BigDecimal(0);
		BigDecimal nuevaProduccion;
		BigDecimal nuevoRdto;
		BigDecimal nuevoPrecio;
		String[] producciones = new String[2];

		ProduccionVO prodVO = null;
		String clave = modulo + "-" + par.getHoja() + "-" + par.getNumero() + "-"
				+ capitalAsegurado.getTipoCapital().getCodtipocapital();
		logger.debug("CLAVE DEL GET:" + clave + " capitalAsegurado:" + capitalAsegurado.getId());
		try {

			if (!rdtosGrp.containsKey(identificador)
					&& capitalAsegurado.getTipoCapital().getCodconcepto().equals(codConceptoProduccion)) {
				logger.debug("************************* CASO 1");
				PreciosProduccionesVO precProd = calculoPrecioProduccionManager.getProduccionPrecio(parcelaVO,
						indiceCapital, codConceptoProduccion, esConWS);

				if (esConWS) {

					List<ProduccionVO> listaProdVO = new ArrayList<ProduccionVO>();
					prodVO = mapaRendimientosProd.get(clave);

					if (prodVO == null) {
						logger.debug(" ************************* anexoModCambioMasivoSWManager -> entro por el null");
						prodVO = new ProduccionVO();
						prodVO.setCodModulo(modulo);
						prodVO.setLimMin("0");
						prodVO.setLimMax("");
						prodVO.setRdtoMin("0");
						prodVO.setRdtoMax("");
						prodVO.setProduccion("");
					} else {
						logger.debug(" ************************* anexoModCambioMasivoSWManager -> entro por else");
						logger.debug(" ************************* capital asegurado: " + capitalAsegurado.getId());
						logger.debug(" ************************* parcela: " + capitalAsegurado.getParcela().getId());
						// Asignamos los valores redondeando los decimales
						logger.debug(" ************************* Asignamos los valores");

						String limMin = prodVO.getLimMin();
						prodVO.setLimMin(new BigDecimal(limMin.replace(",", ".")).setScale(0, BigDecimal.ROUND_FLOOR)
								.toString());

						String limMax = prodVO.getLimMax();
						if (!StringUtils.nullToString(limMax).equals(""))
							prodVO.setLimMax(new BigDecimal(limMax.replace(",", "."))
									.setScale(0, BigDecimal.ROUND_FLOOR).toString());
						else
							prodVO.setLimMax("");

						String rdtoMin = prodVO.getRdtoMin();
						prodVO.setRdtoMin(new BigDecimal(rdtoMin.replace(",", "."))
								.setScale(0, BigDecimal.ROUND_FLOOR).toString());

						String rdtoMax = prodVO.getRdtoMax();
						if (!StringUtils.nullToString(rdtoMax.trim()).equals(""))
							prodVO.setRdtoMax(new BigDecimal(rdtoMax.replace(",", "."))
									.setScale(0, BigDecimal.ROUND_FLOOR).toString());
						else
							prodVO.setRdtoMax("");

					}

					logger.debug(" ************************* limMin: " + prodVO.getLimMin());
					logger.debug(" ************************* limMax: " + prodVO.getLimMax());
					logger.debug(" ************************* rdtoMin: " + prodVO.getRdtoMin());
					logger.debug(" ************************* rdtoMax: " + prodVO.getRdtoMax());
					listaProdVO.add(prodVO);
					precProd.setListProducciones(listaProdVO);
				}

				precios = precProd.getListPrecios();
				for (PrecioVO precioTam : precios) {
					logger.debug(
							" ************************* precioTam: precioTam.getLimMax() :" + precioTam.getLimMax());
				}

				int i = 0;
				for (ProduccionVO pvo : precProd.getListProducciones()) {
					if (pvo.getCodModulo().equals(modulo)) {
						break;
					}
					i++;
				}

				producciones[0] = precProd.getListProducciones().get(i).getRdtoMin().replaceAll(",", ".");
				producciones[1] = precProd.getListProducciones().get(i).getRdtoMax().replaceAll(",", ".");
				logger.debug(" ************************* producciones[0]: " + producciones[0]);
				logger.debug(" ************************* producciones[1]: " + producciones[1]);
				nuevoRdto = getProduccionAGuardar(producciones, capitalAsegurado);
				logger.debug("************************* nuevoRdto : " + nuevoRdto);
				nuevoPrecio = getPrecioAGuardar(precios, i);
				logger.debug("************************* nuevoPrecio : " + nuevoRdto);
				logger.debug("************************* addIdentificador : " + addIdentificador);
				if (addIdentificador) {

					rdtosGrp.put(identificador, nuevoRdto);
					preciosGrp.put(identificador, nuevoPrecio);
				} else {
					// ASF - 317 - Para el caso de que en la línea se busquen
					// producciones para todas las parcelas, me quedo también
					// con la producción.
					try {
						auxProduccion = new BigDecimal(
								precProd.getListProducciones().get(i).getLimMax().replaceAll(",", "."));
						logger.debug("************************* auxProduccion : " + auxProduccion);
					} catch (Exception e) {
						auxProduccion = new BigDecimal(0);
					}
				}
			} else if (!rdtosGrp.containsKey(identificador)
					&& !capitalAsegurado.getTipoCapital().getCodconcepto().equals(codConceptoProduccion)) {
				logger.debug("************************* CASO 2");
				nuevoRdto = new BigDecimal(0);
				logger.debug("************************* CASO 2 nuevoRdto : " + nuevoRdto);
				nuevoPrecio = new BigDecimal(
						getPrecio(par.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid(),
								par.getAnexoModificacion().getPoliza().getIdpoliza(), par,
								par.getAnexoModificacion().getPoliza().getCodmodulo(), indiceCapital));
				logger.debug("************************* CASO 2 nuevoPrecio : " + nuevoPrecio);
				if (addIdentificador) {
					rdtosGrp.put(identificador, nuevoRdto);
					preciosGrp.put(identificador, nuevoPrecio);
				}
			} else {
				logger.debug("************************* CASO 3");
				// cogemos precio y produccion de los mapas por cultivo,
				// variedad y ubicacion
				nuevoRdto = rdtosGrp.get(identificador);
				nuevoPrecio = preciosGrp.get(identificador);
				logger.debug("************************* CASO 3 nuevoRdto" + nuevoRdto);
				logger.debug("************************* CASO 3 nuevoPrecio" + nuevoPrecio);
			}

			// Se obtiene el campo aplicacion del rendimiento correspondiente al
			// capital asegurado
			logger.debug(
					"************************* Se obtiene el campo aplicacion del rendimiento correspondiente al Capital ase");
			String apprdto = anexoModCambioMasivoSWDao.getAplRdto(capitalAsegurado,
					par.getAnexoModificacion().getPoliza().getCodmodulo());
			logger.debug(
					"setPrecioProduccionCapAsegRelModulo - Aplicacion del rendimiento obtenida: '" + apprdto + "'");

			BigDecimal superficie_unidades = new BigDecimal(0);
			if (par.getAnexoModificacion().getPoliza().getLinea().getCodlinea().equals(new BigDecimal(314))
					&& ("U".equals(apprdto) || "P".equals(apprdto) || "".equals(apprdto))) {
				// por unidades
				for (CapitalDTSVariable dvp : capitalAsegurado.getCapitalDTSVariables()) {
					if (dvp.getCodconcepto().equals(new BigDecimal(117))) {
						superficie_unidades = new BigDecimal(dvp.getValor());
						break;
					}
				}
			} else {
				// por superficie
				if (capitalAsegurado.getSuperficie() != null) {
					superficie_unidades = capitalAsegurado.getSuperficie();
				}
			}
			logger.debug("************************* ya casi al final addIdentificador:" + addIdentificador);
			if (!addIdentificador) {
				// ASF - 317
				nuevaProduccion = auxProduccion.setScale(0, BigDecimal.ROUND_FLOOR);
				logger.debug("************************* IF -  nuevaProduccion:" + nuevaProduccion);
			} else {
				nuevaProduccion = nuevoRdto.multiply(superficie_unidades).setScale(0, BigDecimal.ROUND_FLOOR);
				logger.debug("************************* ELSE-  nuevaProduccion:" + nuevaProduccion);
			}

			// establecemos los valores
			logger.debug("************************* esConWS:" + esConWS);
			if (esConWS) {
				try {

					if (prodVO == null) {
						prodVO = mapaRendimientosProd.get(clave);
					}
					nuevaProduccion = new BigDecimal(prodVO.getProduccion()).setScale(0, BigDecimal.ROUND_FLOOR);
					logger.debug("************************* nuevaProduccion:" + nuevaProduccion);
				} catch (Exception e) {
					nuevaProduccion = new BigDecimal(0);
					logger.debug("*************************CATCH nuevaProduccion:" + nuevaProduccion);
				}
			}
			logger.debug("************************* FINAL FINALISIMO");
			// establecemos los valores
			capitalAsegurado.setProduccion(nuevaProduccion);
			capitalAsegurado.setPrecio(nuevoPrecio);
			capitalAsegurado.setTipoRdto(Constants.TIPO_RDTO_MAXIMO);
			logger.debug("************************* FINAL FINALISIMO nuevaProduccion:" + nuevaProduccion);
			logger.debug("************************* FINAL FINALISIMO nuevoPrecio:" + nuevoPrecio);
			logger.debug("************************* FINAL FINALISIMO nuevoTipoRdto:"
					+ Constants.TIPO_RDTO_MAXIMO.toString());
			anexoModCambioMasivoSWDao.saveOrUpdate(capitalAsegurado);

		} catch (Exception e) {
			logger.error("Ha ocurrido un error al recalcular precio/produccion de las parcelas de los anexos", e);
			throw e;
		}
	}

	private BigDecimal getProduccionAGuardar(String[] producciones, CapitalAsegurado capitalAsegurado) {
		BigDecimal nuevaProduccion = new BigDecimal(0);
		if (!"".equals(producciones[1].trim())) {

			// Tengo limite superior
			nuevaProduccion = new BigDecimal(producciones[1]);
			logger.debug(" *************************Tengo limite superior: " + nuevaProduccion);

		} else if ("".equals(producciones[1].trim()) && !"".equals(producciones[0].trim())) {
			// 1. Sin límite superior y con límite inferior
			// MPM - 27/08/12
			// Si el rendimiento es libre, la produccion es 0
			nuevaProduccion = new BigDecimal(0);
			logger.debug(" *************************Sin limite superior y con limite inferior: " + nuevaProduccion);
		} else if ("0".equals(producciones[0].trim()) && "0".equals(producciones[1].trim())
				&& capitalAsegurado.getProduccion() == null) {
			// 2. no asegurable (max y min = 0) y NO lo tengo de antes
			nuevaProduccion = new BigDecimal("0");
			logger.debug(" *************************no asegurable (max y min = 0) y NO lo tengo de antes: "
					+ nuevaProduccion);
		} else if ("0".equals(producciones[0].trim()) && "0".equals(producciones[1].trim())
				&& capitalAsegurado.getProduccion() != null) {
			// 2. no asegurable (max y min = 0) y SI lo tengo de antes
			nuevaProduccion = capitalAsegurado.getProduccion();
			logger.debug(" *************************no asegurable (max y min = 0) y SI lo tengo de antes: "
					+ nuevaProduccion);
		} else if (producciones[0].equals(producciones[1])) {
			// 3.fijo (los dos iguales)
			nuevaProduccion = new BigDecimal(producciones[1]);
			logger.debug(" *************************fijo (los dos iguales): " + nuevaProduccion);
		} else if (producciones.length == 0 && capitalAsegurado.getProduccion() != null) {
			// 4. array sin datos
			nuevaProduccion = capitalAsegurado.getProduccion();
			logger.debug(" *************************. array sin datos: " + nuevaProduccion);
		} else if ("".equals(producciones[0].trim()) && "".equals(producciones[0].trim())
				&& capitalAsegurado.getProduccion() != null) {
			// 5. array con las dos ""
			nuevaProduccion = capitalAsegurado.getProduccion();
			logger.debug(" *************************. rray con las dos '': " + nuevaProduccion);
		} else {
			nuevaProduccion = new BigDecimal(0);
			logger.debug(" *************************getProduccionAGuardar.else final 0: " + nuevaProduccion);
		}

		return nuevaProduccion;
	}

	private BigDecimal getPrecioAGuardar(List<PrecioVO> precios, int indice) {
		BigDecimal nuevoPrecio = new BigDecimal(0);

		if (precios.size() > 0 && indice < precios.size()) {
			PrecioVO precio = precios.get(indice);
			// Si el límite superior es igual al inferior es que el precio es
			// fijo y si no, nos quedamos
			// con el maximo, por lo que no es necesario hacer distingcion =>
			// nos quedaremos siempre con el maximo.
			nuevoPrecio = new BigDecimal(precio.getLimMax());
			logger.debug(" ************************* getPrecioAGuardar :(IF)nuevoPrecio :" + nuevoPrecio);
		} else {
			nuevoPrecio = new BigDecimal(0);
			logger.debug(" ************************* getPrecioAGuardar :(ELSE)nuevoPrecio :" + nuevoPrecio);
		}

		return nuevoPrecio;
	}

	/**
	 * Metodo que devuelve el precio maximo para un capital aseguroado concreto.
	 * 
	 * @param lineaseguroid
	 * @param idPoliza
	 * @param parcela
	 * @param modulo
	 */
	public Float getPrecio(Long lineaseguroid, Long idPoliza, Parcela parcela, String modulo, int indiceCapital)
			throws BusinessException {
		Float precioMax = new Float(0);
		ParcelaVO parcelaVO = null;
		try {
			parcelaVO = ParcelaAnexoUtil.getParcelaVO(parcela, anexoModCambioMasivoSWDao);
			// Obtenemos la lista de precios de la base de datos
			boolean conPl = this.calculoPrecioDao.calcularConPlSql();

			List<Precio> precios;
			if (!conPl) {
				Modulo m = new Modulo();
				ModuloId idmod = new ModuloId();
				idmod.setCodmodulo(modulo);
				idmod.setLineaseguroid(lineaseguroid);
				m.setId(idmod);

				precios = calculoPrecioProduccionManager.getPreciosBBDD(lineaseguroid, m, parcelaVO, indiceCapital);
			} else {
				String[] precios_arr = calculoPrecioDao.getPrecioPlSql(lineaseguroid,
						calculoPrecioProduccionManager.getDatosParcela(parcelaVO, indiceCapital), modulo,
						parcela.getCodcultivo() + "", parcela.getCodvariedad() + "", parcela.getCodprovincia() + "",
						parcela.getCodcomarca() + "", parcela.getCodtermino() + "", parcela.getSubtermino() + "");

				Precio p = new Precio();
				p.setPreciodesde(new BigDecimal(precios_arr[0]));
				p.setPreciohasta(new BigDecimal(precios_arr[1]));
				p.setPreciofijo(new BigDecimal(precios_arr[2]));

				precios = new ArrayList<Precio>();
				precios.add(p);
			}

			// Si se recupera mas de un precio hay que mostrar para el precio
			// desde el mas bajo de todos y para el precio hasta el mas alto de
			// todos.
			if (precios.size() == 1) {
				Precio precio = precios.get(0);
				if (precio.getPreciofijo().floatValue() > 0) {
					precioMax = precio.getPreciofijo().floatValue();
				} else {
					precioMax = precio.getPreciohasta().floatValue();
				}
			}

		} catch (Exception excepcion) {
			logger.error("Se ha producido un error al calcular el precio", excepcion);
			throw new BusinessException("Se ha producido un error al calcular el precio", excepcion);
		}
		return precioMax;
	}

	private HashMap<String, String> setUpdates(AnexoModSWCambioMasivo amCm, Parcela parcelaAnexo,
			HashMap<String, String> mensajesError, boolean guardarSoloPrecioYProd) throws Exception {

		BigDecimal codcultivo = amCm.getCultivo().getId().getCodcultivo();
		BigDecimal codVariedad = amCm.getVariedad().getId().getCodvariedad();
		if (!guardarSoloPrecioYProd) {
			// Ubicacion
			if (amCm.getTermino_cm().getId().getCodprovincia() != null
					|| amCm.getTermino_cm().getId().getCodcomarca() != null
					|| amCm.getTermino_cm().getId().getCodtermino() != null || amCm.getSubtermino_cm() != null) {
				anexoModCambioMasivoSWDao.updateUbicacion(amCm, parcelaAnexo, mensajesError);
			}
			// si la ubicacion es incorrecta paramos la ejecucion
			if (mensajesError.containsKey("alerta")) {
				return mensajesError;
			}

			// Sigpac
			anexoModCambioMasivoSWDao.updateSigpac(amCm, parcelaAnexo);

			// Cultivo y Variedad
			if (!StringUtils.nullToString(codcultivo).equals("") || !StringUtils.nullToString(codVariedad).equals("")) {
				anexoModCambioMasivoSWDao.updateParcelaAnexo(parcelaAnexo, codcultivo, codVariedad);
			}

			// increha e increParcela
			if (!StringUtils.nullToString(amCm.getIncreHa()).equals("")
					|| !StringUtils.nullToString(amCm.getIncreParcela()).equals("")) {

				anexoModCambioMasivoSWDao.updateProduccion(amCm.getIncreHa(), amCm.getIncreParcela(), parcelaAnexo);
			}
			// incre unidades
			if (!StringUtils.nullToString(amCm.getInc_unidades_cm()).equals("")) {
				anexoModCambioMasivoSWDao.updateIncUnidades(amCm.getUnidades_cm(), amCm.getInc_unidades_cm(),
						parcelaAnexo);
			}

			// Precio y superficie
			if (!StringUtils.nullToString(amCm.getPrecio_cm()).equals("")) {
				anexoModCambioMasivoSWDao.updatePrecio(amCm.getPrecio_cm(), parcelaAnexo);
			}
			if (!StringUtils.nullToString(amCm.getSuperficie()).equals("")) {
				anexoModCambioMasivoSWDao.updateSuperficie(amCm.getSuperficie(), parcelaAnexo);
			}

			// Datos Variables
			// destino
			if (!StringUtils.nullToString(amCm.getDestino().getCoddestino()).equals("")) {
				anexoModCambioMasivoSWDao.updateDatosVariables(amCm.getDestino().getCoddestino().toString(),
						parcelaAnexo, ConstantsConceptos.CODCPTO_DESTINO);
			}
			// tipo plantacion
			if (!StringUtils.nullToString(amCm.getTipoPlantacion().getCodtipoplantacion()).equals("")) {
				anexoModCambioMasivoSWDao.updateDatosVariables(
						amCm.getTipoPlantacion().getCodtipoplantacion().toString(), parcelaAnexo,
						ConstantsConceptos.CODCPTO_TIPO_PLANTACION);
			}
			// sistema cultivo
			if (!StringUtils.nullToString(amCm.getSistemaCultivo().getCodsistemacultivo()).equals("")) {
				anexoModCambioMasivoSWDao.updateDatosVariables(
						amCm.getSistemaCultivo().getCodsistemacultivo().toString(), parcelaAnexo,
						ConstantsConceptos.CODCPTO_SISTCULTIVO);
			}
			// Tipo Marco plantacion
			if (!StringUtils.nullToString(amCm.getCodtipomarcoplantac_cm()).equals("")) {
				anexoModCambioMasivoSWDao.updateDatosVariables(amCm.getCodtipomarcoplantac_cm().toString(),
						parcelaAnexo, ConstantsConceptos.CODCPTO_TIPMARCOPLANT);
			}
			// practica cultural
			if (!StringUtils.nullToString(amCm.getCodpracticacultural_cm()).equals("")) {
				anexoModCambioMasivoSWDao.updateDatosVariables(amCm.getCodpracticacultural_cm().toString(),
						parcelaAnexo, ConstantsConceptos.CODCPTO_PRACTCULT);
			}
			// fechaSiembra
			if (!StringUtils.nullToString(amCm.getFechaSiembra()).equals("")) {
				anexoModCambioMasivoSWDao.updateDatosVariables(amCm.getFechaSiembra(), parcelaAnexo,
						ConstantsConceptos.CODCPTO_FECSIEMBRA);
			}
			// fecha fin garantias
			if (!StringUtils.nullToString(amCm.getFechaFinGarantia_cm()).equals("")) {
				anexoModCambioMasivoSWDao.updateDatosVariables(amCm.getFechaFinGarantia_cm(), parcelaAnexo,
						ConstantsConceptos.CODCPTO_FEC_FIN_GARANT);
			}
			// unidades
			if (!StringUtils.nullToString(amCm.getUnidades_cm()).equals("")) {
				anexoModCambioMasivoSWDao.updateDatosVariables(amCm.getUnidades_cm(), parcelaAnexo,
						ConstantsConceptos.CODCPTO_NUMARBOLES);
			}

			// edad
			if (!StringUtils.nullToString(amCm.getEdad_cm()).equals("")) {
				anexoModCambioMasivoSWDao.setEdad(amCm, parcelaAnexo, false);
			}
			// incremento edad
			if (!StringUtils.nullToString(amCm.getIncEdad_cm()).equals("")) {
				anexoModCambioMasivoSWDao.setEdad(amCm, parcelaAnexo, true);
			}

			// sistema de producci�n
			if (!StringUtils.nullToString(amCm.getSistemaProduccion().getCodsistemaproduccion()).equals("")) {
				anexoModCambioMasivoSWDao.updateDatosVariables(
						amCm.getSistemaProduccion().getCodsistemaproduccion().toString(), parcelaAnexo,
						ConstantsConceptos.CODCPTO_SISTEMA_PRODUCCION);
			}
		} else {
			// Precio y superficie
			if (!StringUtils.nullToString(amCm.getPrecio_cm()).equals("")) {
				anexoModCambioMasivoSWDao.updatePrecio(amCm.getPrecio_cm(), parcelaAnexo);
			}
			// increha e increParcela
			if (!StringUtils.nullToString(amCm.getIncreHa()).equals("")
					|| !StringUtils.nullToString(amCm.getIncreParcela()).equals("")) {

				anexoModCambioMasivoSWDao.updateProduccion(amCm.getIncreHa(), amCm.getIncreParcela(), parcelaAnexo);
			}
			// incre unidades
			if (!StringUtils.nullToString(amCm.getInc_unidades_cm()).equals("")) {
				anexoModCambioMasivoSWDao.updateIncUnidades(amCm.getUnidades_cm(), amCm.getInc_unidades_cm(),
						parcelaAnexo);
			}
			// unidades
			if (!StringUtils.nullToString(amCm.getUnidades_cm()).equals("")) {
				anexoModCambioMasivoSWDao.updateDatosVariables(amCm.getUnidades_cm(), parcelaAnexo,
						ConstantsConceptos.CODCPTO_NUMARBOLES);
			}

		}
		return mensajesError;
	}

	private List<Long> getListParcelas(String cadenaParcelas) throws DAOException {
		List<Long> listaParcelas = new ArrayList<Long>();
		StringTokenizer token = new StringTokenizer(cadenaParcelas, ";");
		Parcela p = new Parcela();
		boolean anadir = false;
		while (token.hasMoreTokens()) {
			anadir = false;
			Long id = new Long(token.nextToken());
			p = (Parcela) this.seleccionPolizaDao.get(Parcela.class, id);;
			if (p.getTipomodificacion() == null)
				anadir = true;
			else {
				if (p.getTipomodificacion().equals('B'))
					anadir = false;
				else if (p.getTipomodificacion().equals('A') || p.getTipomodificacion().equals('M'))
					anadir = true;
			}
			if (anadir)
				listaParcelas.add(id);
		}
		return listaParcelas;
	}

	// SETs
	public void setAnexoModCambioMasivoSWDao(IAnexoModCambioMasivoSWDao anexoModCambioMasivoSWDao) {
		this.anexoModCambioMasivoSWDao = anexoModCambioMasivoSWDao;
	}

	public void setDeclaracionesModificacionPolizaManager(
			DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager) {
		this.declaracionesModificacionPolizaManager = declaracionesModificacionPolizaManager;
	}

	public void setCalculoPrecioProduccionManager(CalculoPrecioProduccionManager calculoPrecioProduccionManager) {
		this.calculoPrecioProduccionManager = calculoPrecioProduccionManager;
	}

	public void setSeleccionPolizaDao(ISeleccionPolizaDao seleccionPolizaDao) {
		this.seleccionPolizaDao = seleccionPolizaDao;
	}

	public void setCalculoPrecioDao(ICalculoPrecioDao calculoPrecioDao) {
		this.calculoPrecioDao = calculoPrecioDao;
	}
}