package com.rsi.agp.core.managers.ged.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartFile;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.ged.IDocumentacionGedManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.exception.RestWSException;
import com.rsi.agp.core.webapp.util.DocumentacionGedHelper;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.models.ged.IDocumentacionGedDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.Tomador;
import com.rsi.agp.dao.tables.config.ConfigAgp;
import com.rsi.agp.dao.tables.ged.GedDocPoliza;
import com.rsi.agp.dao.tables.ged.GedDocPolizaSbp;
import com.rsi.agp.dao.tables.poliza.CanalFirma;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;

public class DocumentacionGedManager implements IDocumentacionGedManager {

	private static final Log LOGGER = LogFactory.getLog(DocumentacionGedManager.class);

	protected IDocumentacionGedDao documentacionGedDao;

	private static final String DOC_AGRARIOS = "agrarios";
	private static final String DOC_GENERALES = "generales";

	private static final String _1 = "1";
	private static final String EE_O_MODIFICACION = "EE_O_Modificacion";
	private static final String NOMBRE_DOCUMENTO = "nombreDocumento";
	private static final String RESPUESTA = "Respuesta";
	private static final String CODIGO_RETORNO = "codigoRetorno";
	private static final String EE_O_ALTA = "EE_O_Alta";
	private static final String MENSAJE = "mensaje";

	@Override
	public byte[] getDocumentoPoliza(final Long idPoliza, final String codEntidad, final String codOficina,
			final String usuario) throws BusinessException {
		byte[] doc = null;
		LOGGER.debug("getDocumentoPoliza - INIT");
		String idDocumentum;
		try {
			idDocumentum = this.documentacionGedDao.getIdDocumentum(idPoliza);
			if (!StringUtils.isNullOrEmpty(idDocumentum) && !Constants.STRING_NA.equals(idDocumentum)) {
				ConfigAgp configAgp = (ConfigAgp) this.documentacionGedDao.getObject(ConfigAgp.class, "agpNemo",
						"GED_API_KEY");
				String apiKey = configAgp.getAgpValor();
				if (StringUtils.isNullOrEmpty(apiKey)) {
					throw new BusinessException("Sin valor de configuraci�n API-KEY para el SW");
				}
				DocumentacionGedHelper helper = new DocumentacionGedHelper();
				doc = helper.getDocumentoGed(idDocumentum, codEntidad, codOficina, usuario, apiKey);
			}
		} catch (DAOException e) {
			throw new BusinessException(e);
		} catch (RestWSException e) {
			throw new BusinessException(e);
		}
		LOGGER.debug("getDocumentoPoliza - END");
		return doc;
	}

	@Override
	public byte[] getDocumentoPolizaSbp(final Long idPolizaSbp, final String codEntidad, final String codOficina,
			final String usuario) throws BusinessException {
		byte[] doc = null;
		LOGGER.debug("getDocumentoPolizaSbp - INIT");
		String idDocumentum;
		try {
			idDocumentum = this.documentacionGedDao.getIdDocumentumSbp(idPolizaSbp);
			if (!StringUtils.isNullOrEmpty(idDocumentum) && !Constants.STRING_NA.equals(idDocumentum)) {
				ConfigAgp configAgp = (ConfigAgp) this.documentacionGedDao.getObject(ConfigAgp.class, "agpNemo",
						"GED_API_KEY");
				String apiKey = configAgp.getAgpValor();
				if (StringUtils.isNullOrEmpty(apiKey)) {
					throw new BusinessException("Sin valor de configuraci�n API-KEY para el SW");
				}
				DocumentacionGedHelper helper = new DocumentacionGedHelper();
				doc = helper.getDocumentoSbpGed(idDocumentum, codEntidad, codOficina, usuario, apiKey);
			}
		} catch (DAOException e) {
			throw new BusinessException(e);
		} catch (RestWSException e) {
			throw new BusinessException(e);
		}
		LOGGER.debug("getDocumentoPolizaSbp - END");
		return doc;
	}

	public void setDocumentacionGedDao(final IDocumentacionGedDao documentacionGedDao) {
		this.documentacionGedDao = documentacionGedDao;
	}

	@Override
	public String getDocBarcode() {
		return generateBarCode('A');
	}

	@Override
	public String getDocBarcodeSBP() {
		return generateBarCode('D');
	}

	private String generateBarCode(final Character producto) {
		String barCode = "";
		LOGGER.debug("generateBarCode - INIT");
		try {
			DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
			String year = df.format(Calendar.getInstance().getTime());
			ConfigAgp configAgp = (ConfigAgp) this.documentacionGedDao.getObject(ConfigAgp.class, "agpNemo",
					"SEQ_BARCODE_" + producto);
			if (configAgp == null || !year.equals(configAgp.getAgpValor().substring(1, 3))) {
				barCode = generateNewBarCode(producto, year);
				configAgp.setAgpValor(barCode);
			} else {
				barCode = configAgp.getAgpValor().substring(0, 3);
				barCode += org.apache.commons.lang.StringUtils
						.leftPad(String.valueOf(Integer.valueOf(configAgp.getAgpValor().substring(3)) + 1), 6, '0');
				configAgp.setAgpValor(barCode);
			}
			this.documentacionGedDao.saveOrUpdate(configAgp);
		} catch (Exception e) {
			LOGGER.error(e);
		}
		LOGGER.debug("generateBarCode - END");
		return barCode;
	}

	private String generateNewBarCode(final Character producto, final String year) {
		StringBuilder barCode = new StringBuilder();
		barCode.append(producto);
		barCode.append(year);
		barCode.append(producto.equals('A') ? "000001" : "170000");
		return barCode.toString();
	}

	@Override
	public CanalFirma getCanalFirma(final Long codCanal) throws DAOException {
		return (CanalFirma) this.documentacionGedDao.get(CanalFirma.class, codCanal);
	}

	@Override
	public void saveNewGedDocPoliza(final Long idPoliza, final String codUsuario) throws BusinessException {
		try {
			this.documentacionGedDao.saveNewGedDocPoliza(idPoliza, codUsuario, getDocBarcode());
		} catch (DAOException e) {
			throw new BusinessException(e);
		}
	}
	
	@Override
	public void saveNewGedDocPolizaBatch(final Long idPoliza, final String codUsuario) throws BusinessException {
		try {
			this.documentacionGedDao.saveNewGedDocPolizaBatch(idPoliza, codUsuario, getDocBarcode());
		} catch (DAOException e) {
			throw new BusinessException(e);
		}
	}

	@Override
	public void saveNewGedDocPolizaSBP(final Long idPolizaSbp, final String codUsuario) throws BusinessException {
		try {
			this.documentacionGedDao.saveNewGedDocPolizaSBP(idPolizaSbp, codUsuario, getDocBarcodeSBP());
		} catch (DAOException e) {
			throw new BusinessException(e);
		}
	}

	/**
	 * P0073325 - RQ.04, RQ.05, RQ.06, RQ.10, RQ.11 y RQ.12
	 */
	public List<CanalFirma> getCanalesFirma() {
		LOGGER.debug("DocumentacionGedManager - getCanalesFirma() - init");
		try {
			return documentacionGedDao.getCanalesFirma();
		} catch (DAOException e) {
			LOGGER.error(e);
		}
		LOGGER.debug("DocumentacionGedManager - getCanalesFirma() - end");
		return null;
	}

	/**
	 * P0073325 - RQ. 19
	 * 
	 * @throws BusinessException
	 */
	@Override
	public String uploadDocumentoPoliza(final String codUsuario, final MultipartFile file, final Poliza poliza)
			throws BusinessException {
		try {
			return uploadDocumentoPoliza(codUsuario, file.getBytes(), poliza,
					Constants.STRING_N, Constants.CHARACTER_S, Constants.CANAL_FIRMA_PAPEL);
		} catch (IOException e) {
			throw new BusinessException(e);
		}
	}

	@Override
	public String uploadDocumentoPoliza(final String codUsuario, final byte[] file, final Poliza poliza,
			final String firmaTableta, final Character docFirmada, final Long canal) throws BusinessException {
		String idDocumentum = "";
		LOGGER.debug("DocumentacionGedManager - uploadDocumentoPoliza - init");
		DocumentacionGedHelper helper = new DocumentacionGedHelper();
		String resultado = "";
		try {
			ConfigAgp configAgp = (ConfigAgp) this.documentacionGedDao.getObject(ConfigAgp.class, "agpNemo",
					"GED_API_KEY");
			String apiKey = configAgp.getAgpValor();
			if (StringUtils.isNullOrEmpty(apiKey)) {
				throw new BusinessException("Sin valor de configuraci�n API-KEY para el SW");
			}
			GedDocPoliza gedDocPoliza = (GedDocPoliza) this.documentacionGedDao.getObject(GedDocPoliza.class,
					poliza.getIdpoliza());
			String idDocumentumPol = gedDocPoliza.getIdDocumentum();
			LOGGER.debug("DocumentacionGedManager - idDocumentumPol: " + idDocumentumPol);
			boolean noTieneDocFirmada = Constants.STRING_NA.equals(idDocumentumPol) || StringUtils.isNullOrEmpty(idDocumentumPol);
			Colectivo colectivo = (Colectivo) this.documentacionGedDao.getObject(Colectivo.class,
					poliza.getColectivo().getId());
			// Si no se ha subido documento, se llama al alta
			if (noTieneDocFirmada) {				
				Asegurado asegurado = poliza.getAsegurado();
				String nomAsegurado;
				String apellido1;
				String apellido2;
				if (Constants.TIPO_IDENTIFICACION_NIF.equals(asegurado.getTipoidentificacion()) || 
						Constants.TIPO_IDENTIFICACION_NIE.equals(asegurado.getTipoidentificacion())) {
					nomAsegurado = asegurado.getNombre();
					apellido1 = asegurado.getApellido1();
					apellido2 = asegurado.getApellido2();
				} else {
					nomAsegurado = asegurado.getRazonsocial();
					apellido1 = "";
					apellido2 = "";
				}
				Tomador tomador = colectivo.getTomador();
				resultado = helper.uploadAltaDocumento(file, DOC_AGRARIOS, firmaTableta,
						Constants.CHARACTER_S.equals(docFirmada) ? "3" : "0", apiKey,
						colectivo.getSubentidadMediadora().getId().getCodentidad(), poliza.getOficina(),
						poliza.getLinea().getCodplan(), new Date(), poliza.getLinea().isLineaGanado(),
						poliza.getReferencia(), poliza.getTipoReferencia(), asegurado.getNifcif(),
						asegurado.getTipoidentificacion(), nomAsegurado, apellido1, apellido2,
						tomador.getId().getCiftomador(), tomador.getRazonsocial(), gedDocPoliza.getCodBarras());
				if (!StringUtils.isNullOrEmpty(resultado)) {
					JSONObject json = new JSONObject(resultado.toString());
					JSONObject jsonAlta = (JSONObject) json.get(EE_O_ALTA);
					String codigoRetorno = jsonAlta.getString(CODIGO_RETORNO);
					JSONObject jsonIdDocumentum = jsonAlta.getJSONObject(RESPUESTA);
					if (_1.equals(codigoRetorno)) {
						String idDocumentumSW = jsonIdDocumentum.getString(NOMBRE_DOCUMENTO);
						documentacionGedDao.updateGedDocPoliza(poliza.getIdpoliza(), canal, docFirmada,
								codUsuario, new Date(), idDocumentumSW);
						idDocumentum = idDocumentumSW;
					} else {
						throw new BusinessException(jsonIdDocumentum.getString(MENSAJE));
					}
				}
			} else {				
				resultado = helper.borrarDocumento(idDocumentumPol, colectivo.getSubentidadMediadora().getId().getCodentidad(), DOC_AGRARIOS, apiKey);
				if (!StringUtils.isNullOrEmpty(resultado)) {
					JSONObject json = new JSONObject(resultado.toString());
					JSONObject jsonAlta = (JSONObject) json.get(EE_O_MODIFICACION);
					String codigoRetorno = jsonAlta.getString(CODIGO_RETORNO);
					if (_1.equals(codigoRetorno)) {
						documentacionGedDao.updateGedDocPoliza(poliza.getIdpoliza(), Constants.CANAL_FIRMA_PDTE,
								Constants.CHARACTER_N, codUsuario, new Date(), Constants.STRING_NA);
					} else {
						throw new BusinessException(jsonAlta.getJSONObject(RESPUESTA).getString(MENSAJE));
					}
					idDocumentum = uploadDocumentoPoliza(codUsuario, file, poliza, firmaTableta,
							docFirmada, canal);
				}
			}
		} catch (DAOException e) {
			throw new BusinessException(e);
		} catch (JSONException e) {
			throw new BusinessException(e);
		} catch (RestWSException e) {
			throw new BusinessException(e);
		}
		LOGGER.debug("DocumentacionGedManager - uploadDocumentoPoliza - end");
		return idDocumentum;
	}

	/**
	 * P0073325 - RQ. 19
	 * 
	 * @param usuario
	 * @param idPolizaSbp
	 * @throws BusinessException
	 * 
	 */
	@Override
	public String uploadDocumentoPolizaSbp(final String codUsuario, final MultipartFile file, final PolizaSbp poliza)
			throws BusinessException {
		try {
			return uploadDocumentoPolizaSbp(codUsuario, file.getBytes(), poliza,
					Constants.STRING_N, Constants.CHARACTER_S, Constants.CANAL_FIRMA_PAPEL);
		} catch (IOException e) {
			throw new BusinessException(e);
		}
	}

	@Override
	public String uploadDocumentoPolizaSbp(final String codUsuario, final byte[] file, final PolizaSbp poliza,
			final String firmaTableta, final Character docFirmada, final Long canal) throws BusinessException {
		String idDocumentum = "";
		LOGGER.debug("DocumentacionGedManager - uploadDocumentoPolizaSbp - init");
		DocumentacionGedHelper helper = new DocumentacionGedHelper();
		String resultado = "";
		try {
			ConfigAgp configAgp = (ConfigAgp) this.documentacionGedDao.getObject(ConfigAgp.class, "agpNemo",
					"GED_API_KEY");
			String apiKey = configAgp.getAgpValor();
			if (StringUtils.isNullOrEmpty(apiKey)) {
				throw new BusinessException("Sin valor de configuraci�n API-KEY para el SW");
			}
			GedDocPolizaSbp gedDocPolizaSbp = (GedDocPolizaSbp) this.documentacionGedDao
					.getObject(GedDocPolizaSbp.class, poliza.getId());
			String idDocumentumPol = gedDocPolizaSbp.getIdDocumentum();
			boolean noTieneDocFirmada = Constants.STRING_NA.equals(idDocumentumPol) || StringUtils.isNullOrEmpty(idDocumentumPol);
			Colectivo colectivo = (Colectivo) this.documentacionGedDao.getObject(Colectivo.class,
					poliza.getPolizaPpal().getColectivo().getId());
			// Si no se ha subido documento, se llama al alta
			if (noTieneDocFirmada) {				
				Asegurado asegurado = poliza.getPolizaPpal().getAsegurado();
				String nomAsegurado;
				String apellido1;
				String apellido2;
				if (Constants.TIPO_IDENTIFICACION_NIF.equals(asegurado.getTipoidentificacion())) {
					nomAsegurado = asegurado.getNombre();
					apellido1 = asegurado.getApellido1();
					apellido2 = asegurado.getApellido2();
				} else {
					nomAsegurado = asegurado.getRazonsocial();
					apellido1 = "";
					apellido2 = "";
				}
				Tomador tomador = colectivo.getTomador();
				resultado = helper.uploadAltaDocumento(file, DOC_GENERALES, firmaTableta, "0", apiKey,
						colectivo.getSubentidadMediadora().getId().getCodentidad(), poliza.getPolizaPpal().getOficina(),
						poliza.getPolizaPpal().getLinea().getCodplan(), new Date(), false,
						poliza.getPolizaPpal().getReferencia(), poliza.getPolizaPpal().getTipoReferencia(),
						asegurado.getNifcif(), asegurado.getTipoidentificacion(), nomAsegurado, apellido1, apellido2,
						tomador.getId().getCiftomador(), tomador.getRazonsocial(), gedDocPolizaSbp.getCodBarras());
				if (!StringUtils.isNullOrEmpty(resultado)) {
					JSONObject json = new JSONObject(resultado.toString());
					JSONObject jsonAlta = (JSONObject) json.get(EE_O_ALTA);
					String codigoRetorno = jsonAlta.getString(CODIGO_RETORNO);
					JSONObject jsonIdDocumentum = jsonAlta.getJSONObject(RESPUESTA);
					if (_1.equals(codigoRetorno)) {
						String idDocumentumSW = jsonIdDocumentum.getString(NOMBRE_DOCUMENTO);
						documentacionGedDao.updateGedDocPolizaSbp(poliza.getId(), canal, docFirmada, codUsuario,
								new Date(), idDocumentumSW);
						idDocumentum = idDocumentumSW;
					} else {
						throw new BusinessException(jsonIdDocumentum.getString(MENSAJE));
					}
				}
			} else {
				resultado = helper.borrarDocumento(idDocumentumPol, colectivo.getSubentidadMediadora().getId().getCodentidad(), DOC_GENERALES, apiKey);
				if (!StringUtils.isNullOrEmpty(resultado)) {
					JSONObject json = new JSONObject(resultado.toString());
					JSONObject jsonAlta = (JSONObject) json.get(EE_O_MODIFICACION);
					String codigoRetorno = jsonAlta.getString(CODIGO_RETORNO);
					if (_1.equals(codigoRetorno)) {
						documentacionGedDao.updateGedDocPolizaSbp(poliza.getId(), canal, docFirmada, codUsuario,
								new Date(), Constants.STRING_NA);
					} else {
						throw new BusinessException(jsonAlta.getJSONObject(RESPUESTA).getString(MENSAJE));
					}
					idDocumentum = uploadDocumentoPolizaSbp(codUsuario, file, poliza, firmaTableta,
							docFirmada, canal);
				}
			}
		} catch (RestWSException e) {
			throw new BusinessException(e);
		} catch (JSONException e) {
			throw new BusinessException(e);
		} catch (DAOException e) {
			throw new BusinessException(e);
		}
		LOGGER.debug("DocumentacionGedManager - uploadDocumentoPolizaSbp - end");
		return idDocumentum;
	}

	public String getIdDocumentum(final Long idPoliza) throws BusinessException {
		LOGGER.debug("DocumentacionGedManager - getIdDocumentum - init");
		String idDocumentum;
		try {
			idDocumentum = this.documentacionGedDao.getIdDocumentum(idPoliza);
		} catch (DAOException e) {
			throw new BusinessException(e);
		}
		LOGGER.debug("DocumentacionGedManager - getIdDocumentum - end");
		return idDocumentum;
	}
	
	@Override
	public void marcarComoDiferida(final Long idPoliza) throws BusinessException {
		LOGGER.debug("DocumentacionGedManager - marcarComoDiferida - init");
		try {
			documentacionGedDao.marcarComoDiferida(idPoliza);
		} catch (DAOException e) {
			throw new BusinessException(e);
		}
		LOGGER.debug("DocumentacionGedManager - marcarComoDiferida - end");
	}
}