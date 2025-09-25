package com.rsi.agp.core.jmesa.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.service.impl.ImportacionPolizasService.RelacionEtiquetaTabla;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;
import com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCobertura;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRaza;

import es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio;
import es.agroseguro.contratacion.datosVariables.DatosVariables;

public interface IImportacionPolizasService {

	public Boolean populateAndValidatePoliza(final Poliza polizaHbm, final es.agroseguro.contratacion.Poliza poliza,
			final Long idEnvio, final Session session, final Boolean isBatch, final String codUsuario) throws Exception;

	public Boolean populateAndValidatePolizaComp(final Poliza polizaHbm, final es.agroseguro.contratacion.Poliza poliza,
			final Long idEnvio, final Session session, final Boolean isBatch, final String codUsuario) throws Exception;

	public Boolean populateAndValidatePolizaGanado(final Poliza polizaHbm,
			final es.agroseguro.contratacion.Poliza poliza, final Long idEnvio, final Session session,
			final Boolean isBatch, final String codUsuario) throws Exception;

	public SubentidadMediadora getESMediadora(int codplan, Colectivo colectivo, Session session, Boolean isBatch);

	public BigDecimal toBigDecimal(String numero);

	public void populateSubvenciones(final Poliza polizaHbm,
			final es.agroseguro.contratacion.declaracionSubvenciones.SubvencionesDeclaradas subvsDeclaradas,
			final Session session, final Boolean isBatch) throws Exception;

	public void populateCoberturas(final Poliza polizaHbm, final es.agroseguro.contratacion.Cobertura cobertura,
			final Session session, final boolean isPrincipal, final Boolean isBatch, final Long secuencia)
			throws DAOException;

	public ComparativaPoliza generarComparativaPoliza(final Poliza polizaHbm, final BigDecimal cpm,
			final BigDecimal rCub, final BigDecimal codConcepto, final BigDecimal filaModulo, final BigDecimal valor,
			final String descValor, final BigDecimal filaComparativa, final Long idComparativa);

	public void populateParcelasPpal(final Poliza polizaHbm,
			final es.agroseguro.contratacion.ObjetosAsegurados objetosAsegurados,
			final es.agroseguro.contratacion.Cobertura cobertura, final Session session, final Boolean isBatch)
			throws Exception;

	public void populateDatosVariables(final CapitalAsegurado capitalAseguradoHbm,
			final es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables, final Long lineaseguroid,
			final Session session, final Boolean isBatch) throws Exception;

	public void asignarParcelasInstalaciones(final Set<Parcela> parcelas);

	public void populateCostes(final Poliza polizaHbm,
			final es.agroseguro.contratacion.costePoliza.CostePoliza costePoliza, final Session session,
			final Boolean isBatch, final Long idComparativa) throws Exception;

	public DistribucionCoste2015 cargaBonifRecargUnificado(DistribucionCoste2015 dc,
			CosteGrupoNegocio costeGrupoNegocio);

	public DistribucionCoste2015 cargarSubvencionesDC(final DistribucionCoste2015 dc,
			final CosteGrupoNegocio costeGrupoNegocio);

	public void populatePagos(final Poliza polizaHbm, final es.agroseguro.contratacion.Pago pago,
			final String cccSiniestros, final Session session, final Boolean isBatch) throws Exception;

	public void populateFraccionamiento(final Poliza polizaHbm, es.agroseguro.contratacion.Pago pago,
			final Session session, final Boolean isBatch) throws Exception;

	public void populateComisiones(final Poliza polizaHbm, int plan, int linea, Calendar fechaEfecto,
			es.agroseguro.iTipos.Gastos gastos[], SubentidadMediadora sm, final Session session, Boolean isBatch)
			throws Exception;

	public void populateSocios(final Poliza polizaHbm,
			final es.agroseguro.contratacion.declaracionSubvenciones.RelacionSocios relacionSocios,
			final Session session, final Boolean isBatch) throws Exception;

	public void populateParcelasComp(final Poliza polizaHbm,
			final es.agroseguro.contratacion.ObjetosAsegurados objetosAsegurados, final Session session,
			final Boolean isBatch) throws Exception;

	public void populatePagoGanado(final Poliza polizaHbm, final es.agroseguro.contratacion.Pago pagoAct,
			final es.agroseguro.contratacion.CuentaCobroSiniestros ccsAct, final Calendar fechaFirmaSeguro,
			final boolean isBatch);

	public void populateModulo(final es.agroseguro.contratacion.Poliza poliza, final Session session,
			final Poliza polizaHbm, Long lineaSeguroId, final Boolean isBatch);

	public Explotacion populateExplotaciones(final Poliza polizaHbm, final es.agroseguro.contratacion.Poliza poliza,
			final Session session, final Long lineaId, final Boolean isBatch) throws Exception;

	public void agregarCoordenadas(es.agroseguro.contratacion.explotacion.ExplotacionDocument.Explotacion explotacion,
			Explotacion explotacionBean);

	public Set<DatosVariable> addDatVar(final Set<DatosVariable> datosVariables, final DatosVariables datt,
			final GrupoRaza grupRBean, Session sessionWW, Map<String, RelacionEtiquetaTabla> auxEtiquetaTabla);

	public String getCodconcepto(final String nombre, final Map<String, RelacionEtiquetaTabla> auxEtiquetaTabla);

	public Map<String, RelacionEtiquetaTabla> getCodConceptoEtiquetaTablaExplotaciones(final Long lineaseguroid,
			final Session session, final Boolean isBatch);

	public String nullToString(Object cad);

	public List<ComparativaPoliza> getComparativasRiesgCubEleg(final Poliza polizaHbm,
			final es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido[] riesgCbtoElegArray,
			final Session session, final Long secuencia, Boolean isBatch) throws DAOException;

	/* Pet. 63482 ** MODIF TAM (14.06.2021) * Inicio */
	public List<ComparativaPoliza> getComparativasRiesgCubElegGanado(Poliza polizaHbm, DatosVariables dvs,
			Session session, Long secuencia, Boolean isBatch, final Long lineaseguroid) throws DAOException;

	/* ESC-15182 ** MODIF TAM (20.09.2021) ** Inicio */
	public Set<ExplotacionCobertura> populateExplotacionesCoberturas(final Poliza polizaHbm,
			final es.agroseguro.contratacion.Poliza poliza, final Session session, final Long lineaId,
			final Boolean isBatch, final Explotacion explotacionBean) throws Exception;

	/* P79009 - REQ 2 */
	public List<ComparativaPoliza> getComparativas(Poliza poliza, Session session, Long secuencia, final es.agroseguro.contratacion.Cobertura cobertura, boolean isBatch);
}
