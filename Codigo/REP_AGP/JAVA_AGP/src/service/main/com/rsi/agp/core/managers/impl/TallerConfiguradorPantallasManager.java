package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.models.config.ITallerConfiguracionPantallasDao;
import com.rsi.agp.dao.models.poliza.IDatosParcelaDao;
import com.rsi.agp.dao.tables.config.ConfiguracionCampo;
import com.rsi.agp.dao.tables.config.ConfiguracionCampoId;
import com.rsi.agp.dao.tables.config.OrigenDatos;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;
import com.rsi.agp.dao.tables.config.TipoCampo;
import com.rsi.agp.dao.tables.org.OrganizadorInformacion;
import com.rsi.agp.vo.CampoPantallaConfigurableVO;
import com.rsi.agp.vo.ComboDataVO;
import com.rsi.agp.vo.PantallaConfigurableVO;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class TallerConfiguradorPantallasManager implements IManager {

	protected final Log logger = LogFactory.getLog(getClass());

	final String MENSAJE_SAVE_OK = "Alta realizada";
	final String MENSAJE_SAVE_ERROR = "El alta no se pudo realizar";

	// Constantes para determinar si la ubicación de un registro del organizador de
	// información se corresponde con datos variables o no
	final String DATOS_VARIABLES = "DATOS VARIABLES";
	final String D_VBLES = "D.VBLES";

	protected ITallerConfiguracionPantallasDao tallerConfiguracionPantallasDao;
	private IDatosParcelaDao datosParcelaDao;

	public List<ComboDataVO> getUsos(Long idLinea) {
		List<ComboDataVO> listUsos = null;
		try {
			listUsos = this.tallerConfiguracionPantallasDao.getUsos(idLinea);
		} catch (Exception excepcion) {
			logger.error(excepcion);
		}
		return listUsos;
	}

	public List<ComboDataVO> getOrigenDatos() {
		List<OrigenDatos> listOrigenDatos = null;
		List<ComboDataVO> listOrigenDatosVO = null;
		try {
			listOrigenDatos = this.tallerConfiguracionPantallasDao.getObjects(OrigenDatos.class, null, null);
			listOrigenDatosVO = new ArrayList<ComboDataVO>();
			ComboDataVO comboDataVO = null;
			for (OrigenDatos origenDatos : listOrigenDatos) {
				comboDataVO = new ComboDataVO(Long.toString(origenDatos.getIdorigendatos()),
						origenDatos.getDescorigendatos());
				listOrigenDatosVO.add(comboDataVO);
			}
			Collections.sort(listOrigenDatosVO, new Comparator() {
				public int compare(Object o1, Object o2) {
					ComboDataVO comboDataVO1 = (ComboDataVO) o1;
					ComboDataVO comboDataVO2 = (ComboDataVO) o2;
					return comboDataVO1.getDescripcion().compareToIgnoreCase(comboDataVO2.getDescripcion());
				}
			});
		} catch (Exception excepcion) {
			logger.error(excepcion);
		}
		return listOrigenDatosVO;
	}

	public List<ComboDataVO> getTiposCampo() {
		List<TipoCampo> listTiposCampos = null;
		List<ComboDataVO> listTiposCamposVO = null;
		try {
			listTiposCampos = this.tallerConfiguracionPantallasDao.getObjects(TipoCampo.class, null, null);
			listTiposCamposVO = new ArrayList<ComboDataVO>();
			ComboDataVO comboDataVO = null;
			for (TipoCampo tipoCampo : listTiposCampos) {
				comboDataVO = new ComboDataVO(Long.toString(tipoCampo.getIdtipo()), tipoCampo.getDesctipo());
				listTiposCamposVO.add(comboDataVO);
			}
			Collections.sort(listTiposCamposVO, new Comparator<ComboDataVO>() {
				@Override
				public int compare(ComboDataVO arg0, ComboDataVO arg1) {
					return arg0.getDescripcion().compareTo(arg1.getDescripcion());
				}
			});
		} catch (Exception excepcion) {
			logger.error(excepcion);
		}
		return listTiposCamposVO;
	}

	public JSONArray getEstructuraCampos(Long idLinea, BigDecimal codUso) {
		JSONArray jsonArr = new JSONArray();
		try {
			List<OrganizadorInformacion> listOrganizadorInformacion = this.tallerConfiguracionPantallasDao
					.getEstructuraCampos(idLinea, codUso);
			Map<BigDecimal, JSONObject> ubicaciones = new HashMap<BigDecimal, JSONObject>();
			if (listOrganizadorInformacion.size() > 0) {
				JSONObject jsonOi;
				Collections.sort(listOrganizadorInformacion, new Comparator<OrganizadorInformacion>() {
					@Override
					public int compare(OrganizadorInformacion arg0, OrganizadorInformacion arg1) {
						int c = arg0.getUbicacion().getDesubicacion().compareTo(arg1.getUbicacion().getDesubicacion());
						if (c == 0) {
							c = arg0.getDiccionarioDatos().getNomconcepto()
									.compareTo(arg1.getDiccionarioDatos().getNomconcepto());
						}
						return c;
					}
				});
				for (OrganizadorInformacion oiItem : listOrganizadorInformacion) {
					if (ubicaciones.containsKey(oiItem.getUbicacion().getCodubicacion())) {
						jsonOi = ubicaciones.get(oiItem.getUbicacion().getCodubicacion());
					} else {
						jsonOi = new JSONObject();
						jsonOi.put("codUbicacion", oiItem.getUbicacion().getCodubicacion());
						jsonOi.put("desUbicacion", oiItem.getUbicacion().getDesubicacion());
						jsonOi.put("conceptos", new JSONArray());
						ubicaciones.put(oiItem.getUbicacion().getCodubicacion(), jsonOi);
					}
					JSONObject jsonConcepto = new JSONObject();
					jsonConcepto.put("codConcepto", oiItem.getDiccionarioDatos().getCodconcepto());
					jsonConcepto.put("nomConcepto", oiItem.getDiccionarioDatos().getNomconcepto());
					jsonConcepto.put("desTiponaturaleza",
							oiItem.getDiccionarioDatos().getTipoNaturaleza().getDestiponaturaleza());
					jsonConcepto.put("numTabla", oiItem.getDiccionarioDatos().getNumtabla());
					jsonConcepto.put("tamanho", oiItem.getDiccionarioDatos().getLongitud());
					jsonConcepto.put("multiple", oiItem.getDiccionarioDatos().getMultiple());
					jsonConcepto
							.put("datoVariable",
									(oiItem.getUbicacion().getDesubicacion().indexOf(DATOS_VARIABLES) >= 0
											|| oiItem.getUbicacion().getDesubicacion().indexOf(D_VBLES) >= 0) ? "S"
													: "N");
					jsonConcepto.put("codUbicacion", oiItem.getUbicacion().getCodubicacion());
					jsonConcepto.put("codUso", codUso);
					jsonConcepto.put("descripcion", oiItem.getDiccionarioDatos().getDesconcepto());
					jsonOi.getJSONArray("conceptos").put(jsonConcepto);
				}
			}
			List<JSONObject> auxCol = new ArrayList<JSONObject>(ubicaciones.values());
			// ORDENAMOS LAS UBICACIONES
			Collections.sort(auxCol, new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject arg0, JSONObject arg1) {
					try {
						return arg0.getString("desUbicacion").compareTo(arg1.getString("desUbicacion"));
					} catch (JSONException e) {
						return 0;
					}
				}
			});
			for (JSONObject jsonObj : auxCol) {
				jsonArr.put(jsonObj);
			}
		} catch (Exception excepcion) {
			logger.error(excepcion);
		}
		return jsonArr;
	}

	public PantallaConfigurableVO getPantallaConfigurada(final Long idPantallaConfigurable) {
		PantallaConfigurableVO pantallaConfigurableVO = new PantallaConfigurableVO();
		try {
			PantallaConfigurable pantallaConfigurable = this.tallerConfiguracionPantallasDao
					.getPantallaConfigurada(idPantallaConfigurable);
			pantallaConfigurableVO.setIdPantalla(pantallaConfigurable.getPantalla().getIdpantalla().intValue());
			pantallaConfigurableVO
					.setIdPantallaConfigurable(pantallaConfigurable.getIdpantallaconfigurable().intValue());
			List<ConfiguracionCampo> listPantallas = this.tallerConfiguracionPantallasDao.getObjects(
					ConfiguracionCampo.class, "id.idpantallaconfigurable", new BigDecimal(idPantallaConfigurable));
			List<CampoPantallaConfigurableVO> listConfiguracionCampo = new ArrayList<CampoPantallaConfigurableVO>();
			for (ConfiguracionCampo configuracionCampo : listPantallas) {
				CampoPantallaConfigurableVO campoPantallaConfigurableVO = new CampoPantallaConfigurableVO();
				campoPantallaConfigurableVO.setAlto(configuracionCampo.getAlto().intValue());
				campoPantallaConfigurableVO.setAncho(configuracionCampo.getAncho().intValue());
				campoPantallaConfigurableVO.setX(configuracionCampo.getX().intValue());
				campoPantallaConfigurableVO.setY(configuracionCampo.getY().intValue());
				campoPantallaConfigurableVO.setEtiqueta(configuracionCampo.getEtiqueta());
				campoPantallaConfigurableVO.setIdtipo(configuracionCampo.getTipoCampo().getIdtipo().intValue());
				campoPantallaConfigurableVO.setDescripcion_tipo(configuracionCampo.getTipoCampo().getDesctipo());
				campoPantallaConfigurableVO.setCodUso(configuracionCampo.getId().getCoduso().intValue());
				campoPantallaConfigurableVO.setCodTipoNaturaleza(configuracionCampo.getOrganizadorInformacion()
						.getDiccionarioDatos().getTipoNaturaleza().getCodtiponaturaleza().intValue());
				campoPantallaConfigurableVO.setDesTipoNaturaleza(configuracionCampo.getOrganizadorInformacion()
						.getDiccionarioDatos().getTipoNaturaleza().getDestiponaturaleza());
				campoPantallaConfigurableVO
						.setMostrar(Constants.CHARACTER_S.equals(configuracionCampo.getMostrarsiempre()) ? "S" : "N");
				campoPantallaConfigurableVO.setMostrarCarga(
						Constants.CHARACTER_S.equals(configuracionCampo.getMostrarcargapac()) ? "S" : "N");
				campoPantallaConfigurableVO
						.setDeshabilitado(Constants.CHARACTER_S.equals(configuracionCampo.getDisabled()) ? "S" : "N");
				campoPantallaConfigurableVO
						.setValorCargaPac(StringUtils.nullToString(configuracionCampo.getValorcargapac()));
				campoPantallaConfigurableVO.setTabla_asociada(String
						.valueOf(configuracionCampo.getOrganizadorInformacion().getDiccionarioDatos().getNumtabla()));
				campoPantallaConfigurableVO.setMultiple(
						configuracionCampo.getOrganizadorInformacion().getDiccionarioDatos().getMultiple());
				campoPantallaConfigurableVO.setTamanio(
						configuracionCampo.getOrganizadorInformacion().getDiccionarioDatos().getLongitud().intValue());
				campoPantallaConfigurableVO.setCodConcepto(configuracionCampo.getOrganizadorInformacion()
						.getDiccionarioDatos().getCodconcepto().intValue());
				campoPantallaConfigurableVO.setDescripcion(
						configuracionCampo.getOrganizadorInformacion().getDiccionarioDatos().getDesconcepto());
				if (configuracionCampo.getOrigenDatos() != null) {
					campoPantallaConfigurableVO
							.setIdorigendedatos(configuracionCampo.getOrigenDatos().getIdorigendatos().intValue());
				}
				campoPantallaConfigurableVO
						.setIdpantallaconfigurable(configuracionCampo.getId().getIdpantallaconfigurable().intValue());
				campoPantallaConfigurableVO.setIdseccion(configuracionCampo.getSeccion().getIdseccion().intValue());
				campoPantallaConfigurableVO.setNombre(
						configuracionCampo.getOrganizadorInformacion().getDiccionarioDatos().getNomconcepto());
				campoPantallaConfigurableVO.setUbicacion_codigo(
						configuracionCampo.getOrganizadorInformacion().getUbicacion().getCodubicacion().intValue());
				campoPantallaConfigurableVO.setUbicacion_descripcion(
						configuracionCampo.getOrganizadorInformacion().getUbicacion().getDesubicacion());

				listConfiguracionCampo.add(campoPantallaConfigurableVO);
			}
			pantallaConfigurableVO.setListCampos(listConfiguracionCampo);
		} catch (Exception excepcion) {
			logger.error(excepcion);
		}
		return pantallaConfigurableVO;
	}
	
	public CampoPantallaConfigurableVO getNuevoCampoPantalla(final Long lineaseguroid, final BigDecimal codConcepto,
			final BigDecimal codUbicacion, final BigDecimal codUso) {
		CampoPantallaConfigurableVO campoPantallaConfigurableVO = new CampoPantallaConfigurableVO();		
		campoPantallaConfigurableVO.setCodConcepto(codConcepto.intValue());
		try {
			OrganizadorInformacion oi = this.tallerConfiguracionPantallasDao.getOrgInformacion(lineaseguroid,
					codConcepto, codUbicacion, codUso);
			campoPantallaConfigurableVO.setNombre(oi.getDiccionarioDatos().getNomconcepto());
			campoPantallaConfigurableVO.setEtiqueta(oi.getDiccionarioDatos().getNomconcepto());		
			campoPantallaConfigurableVO.setDesTipoNaturaleza(oi.getDiccionarioDatos().getTipoNaturaleza().getDestiponaturaleza());
			campoPantallaConfigurableVO.setTamanio(oi.getDiccionarioDatos().getLongitud().intValue());
			campoPantallaConfigurableVO.setUbicacion_codigo(oi.getUbicacion().getCodubicacion().intValue());
			campoPantallaConfigurableVO.setUbicacion_descripcion(oi.getUbicacion().getDesubicacion());
			campoPantallaConfigurableVO.setTabla_asociada(oi.getDiccionarioDatos().getNumtabla().toString());
			campoPantallaConfigurableVO.setMultiple(oi.getDiccionarioDatos().getMultiple());
			campoPantallaConfigurableVO.setDescripcion(oi.getDiccionarioDatos().getDesconcepto());
			campoPantallaConfigurableVO.setCodConcepto(codConcepto.intValue());
			campoPantallaConfigurableVO.setCodUso(codUso.intValue());
		} catch (Exception excepcion) {
			logger.error(excepcion);
		}
		return campoPantallaConfigurableVO;
	}
	
	public void savePantallaconfigurada(final Long lineaseguroid, final Long idPantallaConfigurable, final List<CampoPantallaConfigurableVO> camposPantallaVO)
			throws BusinessException {
		List<ConfiguracionCampo> campos = new ArrayList<ConfiguracionCampo>(camposPantallaVO.size());
		for (CampoPantallaConfigurableVO campoVO : camposPantallaVO) {
			ConfiguracionCampo campo = new ConfiguracionCampo();
			ConfiguracionCampoId id = new ConfiguracionCampoId();
			id.setCodconcepto(BigDecimal.valueOf(campoVO.getCodConcepto()));
			id.setCodubicacion(BigDecimal.valueOf(campoVO.getUbicacion_codigo()));
			id.setCoduso(BigDecimal.valueOf(campoVO.getCodUso()));
			id.setIdpantallaconfigurable(BigDecimal.valueOf(idPantallaConfigurable));
			id.setIdseccion(BigDecimal.ONE);
			id.setLineaseguroid(lineaseguroid);
			campo.setId(id);
			OrigenDatos origenDatos = new OrigenDatos();
			origenDatos.setIdorigendatos(Long.valueOf(campoVO.getIdorigendedatos()));
			campo.setOrigenDatos(origenDatos);
			TipoCampo tipoCampo = new TipoCampo();
			tipoCampo.setIdtipo(Long.valueOf(campoVO.getIdtipo()));
			campo.setTipoCampo(tipoCampo);
			campo.setX(BigDecimal.valueOf(campoVO.getX()));
			campo.setY(BigDecimal.valueOf(campoVO.getY()));
			campo.setEtiqueta(campoVO.getEtiqueta());
			campo.setAncho(BigDecimal.valueOf(campoVO.getAncho()));
			campo.setAlto(BigDecimal.valueOf(campoVO.getAlto()));
			campo.setMostrarsiempre(campoVO.getMostrar().charAt(0));
			campo.setMostrarcargapac(campoVO.getMostrarCarga().charAt(0));
			campo.setDisabled(campoVO.getDeshabilitado().charAt(0));
			campo.setValorcargapac(campoVO.getValorCargaPac());
			campos.add(campo);
		}
		this.tallerConfiguracionPantallasDao.saveCamposPantalla(idPantallaConfigurable, campos);
	}
	
	public List<CampoPantallaConfigurableVO> getListConfigCampos(final BigDecimal idPantallaConfigurable) throws BusinessException {
		List<CampoPantallaConfigurableVO> listConfiguracionCampo = new ArrayList<CampoPantallaConfigurableVO>();
		try {
			List<ConfiguracionCampo> listCampos = this.datosParcelaDao.getListConfigCampos(idPantallaConfigurable);
			for (ConfiguracionCampo configuracionCampo : listCampos) {
				CampoPantallaConfigurableVO campoPantallaConfigurableVO = new CampoPantallaConfigurableVO();
				campoPantallaConfigurableVO.setAlto(configuracionCampo.getAlto().intValue());
				campoPantallaConfigurableVO.setAncho(configuracionCampo.getAncho().intValue());
				campoPantallaConfigurableVO.setX(configuracionCampo.getX().intValue());
				campoPantallaConfigurableVO.setY(configuracionCampo.getY().intValue());
				campoPantallaConfigurableVO.setEtiqueta(WordUtils.capitalizeFully(configuracionCampo.getEtiqueta()));
				campoPantallaConfigurableVO.setIdtipo(configuracionCampo.getTipoCampo().getIdtipo().intValue());
				campoPantallaConfigurableVO.setDescripcion_tipo(configuracionCampo.getTipoCampo().getDesctipo());
				if (Constants.CHARACTER_S.equals(configuracionCampo.getMostrarsiempre())) {
					campoPantallaConfigurableVO.setMostrar(Constants.CHARACTER_S.toString());
				} else {
					campoPantallaConfigurableVO.setMostrar(Constants.CHARACTER_N.toString());
				}
				if (Constants.CHARACTER_S.equals(configuracionCampo.getDisabled())) {
					campoPantallaConfigurableVO.setDeshabilitado(Constants.CHARACTER_S.toString());
				} else {
					campoPantallaConfigurableVO.setDeshabilitado(Constants.CHARACTER_N.toString());
				}
				campoPantallaConfigurableVO.setTamanio(
						configuracionCampo.getOrganizadorInformacion().getDiccionarioDatos().getLongitud().intValue());
				campoPantallaConfigurableVO.setCodConcepto(
						configuracionCampo.getOrganizadorInformacion().getDiccionarioDatos().getCodconcepto().intValue());
				campoPantallaConfigurableVO
						.setDescripcion(configuracionCampo.getPantallaConfigurable().getPantalla().getDescpantalla());
				if (configuracionCampo.getOrigenDatos() != null) {
					campoPantallaConfigurableVO
							.setIdorigendedatos(configuracionCampo.getOrigenDatos().getIdorigendatos().intValue());
					campoPantallaConfigurableVO.setTabla_asociada(configuracionCampo.getOrigenDatos().getSql());
				}
				campoPantallaConfigurableVO
						.setIdpantallaconfigurable(configuracionCampo.getId().getIdpantallaconfigurable().intValue());
				campoPantallaConfigurableVO.setIdseccion(configuracionCampo.getSeccion().getIdseccion().intValue());
				campoPantallaConfigurableVO
						.setNombre(configuracionCampo.getPantallaConfigurable().getPantalla().getDescpantalla());
				campoPantallaConfigurableVO.setUbicacion_codigo(
						configuracionCampo.getOrganizadorInformacion().getUbicacion().getCodubicacion().intValue());
				campoPantallaConfigurableVO.setUbicacion_descripcion(
						configuracionCampo.getOrganizadorInformacion().getUbicacion().getDesubicacion());
				campoPantallaConfigurableVO.setCodTipoNaturaleza(configuracionCampo.getOrganizadorInformacion()
						.getDiccionarioDatos().getTipoNaturaleza().getCodtiponaturaleza().intValue());
				campoPantallaConfigurableVO.setDecimales(
						configuracionCampo.getOrganizadorInformacion().getDiccionarioDatos().getDecimales().intValue());
				listConfiguracionCampo.add(campoPantallaConfigurableVO);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new BusinessException(e);
		}
		return listConfiguracionCampo;
	}

	public void setTallerConfiguracionPantallasDao(ITallerConfiguracionPantallasDao tallerConfiguracionPantallasDao) {
		this.tallerConfiguracionPantallasDao = tallerConfiguracionPantallasDao;
	}
	
	public void setDatosParcelaDao(final IDatosParcelaDao datosParcelaDao) {
		this.datosParcelaDao = datosParcelaDao;
	}
}