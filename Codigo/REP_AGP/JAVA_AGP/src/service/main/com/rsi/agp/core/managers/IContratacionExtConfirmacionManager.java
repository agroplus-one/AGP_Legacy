package com.rsi.agp.core.managers;

import java.math.BigDecimal;

import org.apache.xmlbeans.XmlObject;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital.PolizaReduccionCapital;
import com.rsi.agp.core.managers.confirmarext.AnularCuponExtBean;
import com.rsi.agp.core.managers.confirmarext.CalcularAnexoExtBean;
import com.rsi.agp.core.managers.confirmarext.CalcularExtBean;
import com.rsi.agp.core.managers.confirmarext.ConfirmarCuponExtBean;
import com.rsi.agp.core.managers.confirmarext.ConfirmarExtBean;
import com.rsi.agp.core.managers.confirmarext.ConfirmarExtException.AgrWSException;
import com.rsi.agp.core.managers.confirmarext.ConfirmarSiniestroExtBean;
import com.rsi.agp.core.managers.confirmarext.SolicitarCuponExtBean;
import com.rsi.agp.core.managers.confirmarext.ValidarExtBean;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.acuseRecibo.AcuseRecibo;

public interface IContratacionExtConfirmacionManager {

	public ConfirmarExtBean doConfirmar(final Base64Binary poliza, final String realPath)
			throws AgrWSException, Exception;

	public SolicitarCuponExtBean doSolicitarCupon(final BigDecimal plan, final String referencia, final String realPath)
			throws AgrWSException, Exception;

	public AnularCuponExtBean doAnularCupon(final String idCupon, final String realPath)
			throws AgrWSException, Exception;

	public ConfirmarCuponExtBean doConfirmarCupon(final String idCupon, final Boolean flgRevAdmin,
			final Base64Binary polizaPpal, final Base64Binary polizaComp, final String realPath)
			throws AgrWSException, Exception;

	public ConfirmarSiniestroExtBean doConfirmarSiniestro(final Base64Binary siniestro, final String realPath)
			throws AgrWSException, Exception;

	/* Pet. 73328 ** MODIF TAM (16/03/2021) ** Inicio */
	public CalcularExtBean doCalcular(final Base64Binary poliza, final String realPath)
			throws AgrWSException, Exception;
	/* Pet. 73328 ** MODIF TAM (16/03/2021) ** Fin */

	public CalcularAnexoExtBean doCalcularAnexo(final String idCupon, final String tipoPoliza,
			final boolean calculoSituacionActual, final Base64Binary modificacionPoliza, final String realPath)
			throws AgrWSException, Exception;

	// P0079361 Inicio
	public void guardarEnAgroplus(final String idCupon, final String referencia, final String codUsuario,
			final Long estadoCupon, Poliza poliza, boolean complementaria, final String realPath, final BigDecimal plan,
			final Boolean isAnexRedCap, final String entradaXML, final PolizaReduccionCapital polizaRC,
			final AcuseRecibo acuseReciboObj) throws DAOException;
	// P0079361 Fin
	
	public AnexoModificacion getAnexoFromXml(final String idCupon, final XmlObject poliza, Long estadoCupon);

	public AnexoModificacion saveAnexo(AnexoModificacion anexo);

	public void validarAnexoPolizaSbp(Poliza poliza, AnexoModificacion anexo, boolean complementaria);

	public ValidarExtBean doValidar(final Base64Binary poliza, final String realPath) throws AgrWSException, Exception;
}
