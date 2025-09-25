package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.models.admin.IComarcaDao;
import com.rsi.agp.dao.models.admin.IProvinciaDao;
import com.rsi.agp.dao.models.commons.ITerminoDao;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.tables.anexo.ParcelaAMSWZonifSIGPAC;
import com.rsi.agp.dao.tables.commons.ComarcaId;
import com.rsi.agp.dao.tables.poliza.ParcelaSWZonifSIGPAC;
import com.rsi.agp.vo.LocalCultVarVO;
import com.rsi.agp.vo.SigpacVO;

import es.agroseguro.seguroAgrario.equivalenciaSIGPACAgroseguro.AmbitoAgroseguro;
import es.agroseguro.seguroAgrario.equivalenciaSIGPACAgroseguro.AmbitoEquivalenteAgroseguro;
import es.agroseguro.seguroAgrario.equivalenciaSIGPACAgroseguro.Comarca;
import es.agroseguro.seguroAgrario.equivalenciaSIGPACAgroseguro.EquivalenciaSIGPACAgroseguro;
import es.agroseguro.seguroAgrario.equivalenciaSIGPACAgroseguro.Provincia;
import es.agroseguro.seguroAgrario.equivalenciaSIGPACAgroseguro.Subtermino;
import es.agroseguro.seguroAgrario.equivalenciaSIGPACAgroseguro.Termino;
import es.agroseguro.serviciosweb.contratacionscsigpaczonificacion.Error;
import es.agroseguro.serviciosweb.contratacionscsigpaczonificacion.SIGPACRequest;

public class SigpacManager implements IManager {

	public static final String ORIGEN_LLAMADA_WS_POLIZA = "POL";
	public static final String ORIGEN_LLAMADA_WS_ANEXOMOD = "ANXMOD";

	private IProvinciaDao provinciaDao;
	private IComarcaDao comarcaDao;
	private ITerminoDao terminoDao;
	
	// ILineaDao requerido para obtener el objeto 'Linea' y acceder a su atributo 'fechaInicioContratacion'
	private ILineaDao lineaDao;

	private static final Log logger = LogFactory.getLog(SigpacManager.class);

	/**
	 * Realiza una llamada al WS de Zonificación SIGPAC y devuelve una lista de
	 * objetos LocalCultVarVO con los datos obtenidos
	 * @throws Exception 
	 */
	public List<LocalCultVarVO> getLocalCultVar(SigpacVO sigpacVO, String realPath, String codUsuario, Long codParcela, String origenLlamadaWS) throws Exception {

		List<LocalCultVarVO> listaLocalCultVarVO = new ArrayList<LocalCultVarVO>();
		SIGPACRequest request = null;
		SIGPACResponse respuesta = null;
		String error = "";

		try {
			String rutaWebInfDecod = URLDecoder.decode(realPath, "UTF-8");
			request = SWZonificacionSIGPACHelper.obtenerSIGPACRequest(sigpacVO);
			respuesta = new SWZonificacionSIGPACHelper().getDatosUbicacionSIGPAC(request, rutaWebInfDecod, codUsuario);
			listaLocalCultVarVO = convertirRespuesta(respuesta);
		} catch (es.agroseguro.serviciosweb.contratacionscsigpaczonificacion.AgrException e) {
			error = getMsgAgrException(e);
			logger.debug("getLocalCultVar: " + error);
			throw new BusinessException(error);
		} catch (javax.xml.ws.soap.SOAPFaultException e) {
			error = e.getMessage();
			logger.debug("getLocalCultVar: " + error);
			throw new BusinessException(error);
		} catch (Exception e) {
			error = e.getMessage();
			logger.error("getLocalCultVar: ", e);
			throw e;
		} finally {
			try {
				if (ORIGEN_LLAMADA_WS_POLIZA.equals(origenLlamadaWS)) {
					registrarEnHistoricoDesdePoliza(codUsuario, request, respuesta, codParcela, error);
				} else if (ORIGEN_LLAMADA_WS_ANEXOMOD.equals(origenLlamadaWS)) {
					registrarEnHistoricoDesdeAnexo(codUsuario, request, respuesta, codParcela, error);
				}
			} catch (Exception e) {
				logger.error("Error al intentar grabar el histórico de llamadas al WS de Zonificación SIGPAC");
			}
		}

		return listaLocalCultVarVO;
	}

	/**
	 * Obtiene el mensaje de error para un AgrException
	 * 
	 * @param exc
	 * @return
	 */
	private String getMsgAgrException(es.agroseguro.serviciosweb.contratacionscsigpaczonificacion.AgrException exc) {
		String msg = ""; 
		if (exc.getFaultInfo() != null && exc.getFaultInfo().getError() != null) {
			for (Error error : exc.getFaultInfo().getError()) {
				msg += error.getMensaje() + " ";
			}
		}
		return msg;
	}

	/**
	 * Rellena una lista de LocalCultVarVO dado un SIGPACResponse
	 * 
	 * @param respuesta
	 * @param cod 
	 * @return
	 * @throws DAOException
	 */
	private List<LocalCultVarVO> convertirRespuesta(SIGPACResponse respuesta) throws DAOException {

		List<LocalCultVarVO> listaLocalCultVarVO = new ArrayList<LocalCultVarVO>();

		EquivalenciaSIGPACAgroseguro eqSigpac = respuesta.getEquivalenciaSIGPACAgroseguroDocument()
				.getEquivalenciaSIGPACAgroseguro();

		AmbitoEquivalenteAgroseguro[] ambitoEqAgroArray = eqSigpac.getAmbitoEquivalenteAgroseguroArray();

		for (int i = 0; i < ambitoEqAgroArray.length; i++) {

			LocalCultVarVO localCultVarVO = new LocalCultVarVO();
			AmbitoEquivalenteAgroseguro ambitoEqAgro = ambitoEqAgroArray[i];
			AmbitoAgroseguro ambitoAgro = ambitoEqAgro.getAmbitoAgroseguro();

			Provincia provincia = ambitoAgro.getProvincia();
			Comarca comarca = ambitoAgro.getComarca();
			Termino termino = ambitoAgro.getTermino();
			Subtermino subtermino = ambitoAgro.getSubTermino();
			Character subterminoChr = ' ';// Es el valor por defecto de subtérmino

			// Las descripciones de provincia, comarca y término deben ir validadas contra
			// base de datos
			String provDescr = ((com.rsi.agp.dao.tables.commons.Provincia) provinciaDao
					.get(com.rsi.agp.dao.tables.commons.Provincia.class, new BigDecimal(provincia.getCodigo())))
							.getNomprovincia();

			ComarcaId comarcaId = new ComarcaId();
			comarcaId.setCodprovincia(new BigDecimal(provincia.getCodigo()));
			comarcaId.setCodcomarca(new BigDecimal(comarca.getCodigo()));
			String comaDescr = ((com.rsi.agp.dao.tables.commons.Comarca) comarcaDao
					.get(com.rsi.agp.dao.tables.commons.Comarca.class, comarcaId)).getNomcomarca();

			if (subtermino != null && subtermino.getCodigo() != null && subtermino.getCodigo().length() > 0) {
				subterminoChr = subtermino.getCodigo().charAt(0);
			}
			
			// Se recupera una instancia específica de la entidad "Linea" a través del DAO.
			// Los identificadores de "linea" y "plan" se obtienen de la entidad "eqSigpac"
			com.rsi.agp.dao.tables.poliza.Linea linea = lineaDao.getLinea(BigDecimal.valueOf(eqSigpac.getLinea()), BigDecimal.valueOf(eqSigpac.getPlan()));
			// Obtenemos la fecha de fin de contratación.
			Date fechaInicioContratacion = linea.getFechaInicioContratacion();

			// Utiliza el método getNomTermino(fechaInicioContratacion, esGanado) en lugar del antiguo getNomtermino() para adaptarse a los nuevos requisitos de la P0079469
			// Esta versión ahora tiene en cuenta la fecha de inicio de contratación y si la línea es de ganado para determinar el nombre correcto del termino
			String termDescr = ((com.rsi.agp.dao.tables.commons.Termino) terminoDao.getTermino(
					new BigDecimal(provincia.getCodigo()), new BigDecimal(comarca.getCodigo()),
					new BigDecimal(termino.getCodigo()), subterminoChr)).getNomTerminoByFecha(fechaInicioContratacion, linea.isLineaGanado());

			localCultVarVO.setCodProvincia(String.valueOf(provincia.getCodigo()));
			localCultVarVO.setNomProvincia(provDescr);
			localCultVarVO.setCodComarca(String.valueOf(comarca.getCodigo()));
			localCultVarVO.setNomComarca(comaDescr);
			localCultVarVO.setCodTermino(String.valueOf(termino.getCodigo()));
			localCultVarVO.setNomTermino(termDescr);
			localCultVarVO.setSubTermino(subterminoChr.toString());

			listaLocalCultVarVO.add(localCultVarVO);
		}
		return listaLocalCultVarVO;
	}

	/**
	 * Escribe en la tabla de logs de llamadas al WS de Zonificación (desde Póliza)
	 * @throws DAOException 
	 */
	private void registrarEnHistoricoDesdePoliza(String codUsuario, SIGPACRequest sigpacRequest,
			com.rsi.agp.core.managers.impl.SIGPACResponse sigpacRespuesta, Long codParcela, String error) throws DAOException {

		if (sigpacRequest != null) {
			
			ParcelaSWZonifSIGPAC parcelaSWZonifSIGPAC = new ParcelaSWZonifSIGPAC();
	
			// Si es alta, la parcela no tendrá id aún
			if (codParcela != null && codParcela > 0) {
				com.rsi.agp.dao.tables.poliza.Parcela parcela = (com.rsi.agp.dao.tables.poliza.Parcela) this.provinciaDao
						.get(com.rsi.agp.dao.tables.poliza.Parcela.class, new Long(codParcela));
				parcelaSWZonifSIGPAC.setParcela(parcela);
			}
	
			parcelaSWZonifSIGPAC.setUsuario(codUsuario);
			parcelaSWZonifSIGPAC.setFecha(new Date());
			parcelaSWZonifSIGPAC
					.setXmlEnvio(Hibernate.createClob(WSUtils.generateXMLLlamadaZonificacionSIGPAC(sigpacRequest)));
	
			if (sigpacRespuesta != null) {
				parcelaSWZonifSIGPAC.setXmlRespuesta(
						Hibernate.createClob(sigpacRespuesta.getEquivalenciaSIGPACAgroseguroDocument().toString()));
			} else {
				parcelaSWZonifSIGPAC.setXmlRespuesta(Hibernate.createClob(error));
			}
	
			try {
				this.provinciaDao.saveOrUpdate(parcelaSWZonifSIGPAC);
			} catch (DAOException e) {
				logger.error("Error al insertar el registro de la comunicacion con el SW de Zonificación SIGPAC de póliza",
						e);
			}
		}
	}

	/**
	 * Escribe en la tabla de logs de llamadas al WS de Zonificación (desde Anexo)
	 * @throws DAOException 
	 */
	private void registrarEnHistoricoDesdeAnexo(String codUsuario, SIGPACRequest sigpacRequest,
			com.rsi.agp.core.managers.impl.SIGPACResponse sigpacRespuesta, Long codParcela, String error) throws DAOException {

		if (sigpacRequest != null) {
			
			ParcelaAMSWZonifSIGPAC parcelaAMSWZonifSIGPAC = new ParcelaAMSWZonifSIGPAC();
	
			// Si es alta, la parcela no tendrá id aún
			if (codParcela != null && codParcela > 0) {
				com.rsi.agp.dao.tables.anexo.Parcela parcela = (com.rsi.agp.dao.tables.anexo.Parcela) this.comarcaDao
						.get(com.rsi.agp.dao.tables.anexo.Parcela.class, new Long(codParcela));
				parcelaAMSWZonifSIGPAC.setParcela(parcela);
			}
	
			parcelaAMSWZonifSIGPAC.setUsuario(codUsuario);
			parcelaAMSWZonifSIGPAC.setFecha(new Date());
			parcelaAMSWZonifSIGPAC
					.setXmlEnvio(Hibernate.createClob(WSUtils.generateXMLLlamadaZonificacionSIGPAC(sigpacRequest)));
	
			if (sigpacRespuesta != null) {
				parcelaAMSWZonifSIGPAC.setXmlRespuesta(
						Hibernate.createClob(sigpacRespuesta.getEquivalenciaSIGPACAgroseguroDocument().toString()));
			} else {
				parcelaAMSWZonifSIGPAC.setXmlRespuesta(Hibernate.createClob(error));
			}
	
			try {
				this.provinciaDao.saveOrUpdate(parcelaAMSWZonifSIGPAC);
			} catch (DAOException e) {
				logger.error("Error al insertar el registro de la comunicacion con el SW de Zonificación SIGPAC de anexo",
						e);
			}
		}
	}

	/**
	 * Para la inicialización del DAO
	 * 
	 * @param provinciaDao
	 */
	public void setProvinciaDao(IProvinciaDao provinciaDao) {
		this.provinciaDao = provinciaDao;
	}

	/**
	 * Para la inicialización del DAO
	 * 
	 * @param comarcaDao
	 */
	public void setComarcaDao(IComarcaDao comarcaDao) {
		this.comarcaDao = comarcaDao;
	}

	/**
	 * Para la inicialización del DAO
	 * 
	 * @param terminoDao
	 */
	public void setTerminoDao(ITerminoDao terminoDao) {
		this.terminoDao = terminoDao;
	}
	
	/**
	 * Para la inicialización del DAO
	 * 
	 * @param lineaDao
	 */
	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}
}