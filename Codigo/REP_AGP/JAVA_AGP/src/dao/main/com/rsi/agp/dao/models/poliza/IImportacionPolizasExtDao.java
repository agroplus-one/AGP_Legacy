package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.Socio;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.comisiones.CultivosSubentidades;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.SubvencionCCAA;
import com.rsi.agp.dao.tables.cpl.SubvencionEnesa;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.importacion.ImportacionPolizasExt;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;
import com.rsi.agp.dao.tables.org.OrganizadorInformacion;
import com.rsi.agp.dao.tables.poliza.EstadoPagoAgp;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.Poliza;

@SuppressWarnings("rawtypes")
public interface IImportacionPolizasExtDao extends GenericDao {

	public void saveImportacionPolizaExt(ImportacionPolizasExt envio) throws DAOException;

	public void updateIdEnvio(ImportacionPolizasExt envio) throws DAOException;

    public Usuario getUsuarioPolizaBBDD(Session session, Boolean isBatch, String usuario, final BigDecimal codentidad,
            final BigDecimal codsubentidad) throws DAOException;

	public Linea getLineaSeguroBBDD(BigDecimal valueOf, BigDecimal valueOf2, Session session, Boolean isBatch)
			throws Exception;

	public Colectivo getColectivoBBDD(es.agroseguro.contratacion.Colectivo colectivo, Session session,
			SubentidadMediadora sm, Boolean isBatch) throws Exception;

	public Asegurado getAseguradoBBDD(es.agroseguro.contratacion.Asegurado asegurado, BigDecimal codentidad,
			Session session, SubentidadMediadora sm, Boolean isBatch) throws Exception;

	public EstadoPoliza getEstadoPolizaBBDD(Session session, Boolean isBatch) throws Exception;

	public EstadoPagoAgp getEstadoPagoBBDD(Session session, Boolean isBatch) throws Exception;

	public SubvencionEnesa getSubvEnesaBBDD(BigDecimal valueOf, Long lineaseguroid, String codmodulo, Session session,
			Boolean isBatch);

	public SubvencionCCAA getSubvCCAABBDD(BigDecimal valueOf, Long lineaseguroid, String codmodulo, Session session,
			Boolean isBatch);

	public Object saveOrUpdateEntity(Object entity, Session session, Boolean isBatch) throws DAOException;

	public void deleteEntity(Object entity, Session session, Boolean isBatch) throws DAOException;

	public Long getSecuenciaComparativa(Session session, Boolean isBatch) throws DAOException;

	public String getDescValorCodGarantizado(Session session, int valor, Map<String, String> colDescripciones,
			Boolean isBatch);

	public Object getFilaModulo(Long lineaseguroid, String codmodulo, BigDecimal valueOf, BigDecimal valueOf2,
			Session session, Map<String, BigDecimal> colFilaModulo, Boolean isBatch);

	public String getDescValorCalculoIndemnizacion(Session session, int valor, Map<String, String> colDescripciones,
			Boolean isBatch);

	public String getDescValorPctFranquiciaElegible(Session session, int valor, Map<String, String> colDescripciones,
			Boolean isBatch);

	public String getDescValorMinimoIndemnizableElegible(Session session, int valor,
			Map<String, String> colDescripciones, Boolean isBatch);

	public String getDescValoTipoFranquicia(Session session, String valor, Map<String, String> colDescripciones,
			Boolean isBatch);

	public String getDescValoCapitalAseguradoElegible(Session session, int valor, Map<String, String> colDescripciones,
			Boolean isBatch);

	public List<RiesgoCubiertoModulo> getListaRiesgoCubiertoModulo(final Poliza polizaHbm, final Session session,
			final Boolean isBatch);

	public Variedad getVariedad(es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela parcela,
			final Poliza polizaHbm, final Session session, final Boolean isBatch) throws Exception;

	public Termino getTermino(es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela parcela,
			final Poliza polizaHbm, final Session session, final Boolean isBatch) throws Exception;

	public TipoCapital getTipoCapital(es.agroseguro.contratacion.parcela.CapitalAsegurado capitalAsegurado,
			final Session session, final Boolean isBatch) throws Exception;

	public List<DiccionarioDatos> getDiccionarioDatosVariablesParcela(Long lineaseguroid, Session session,
			final Boolean isBatch);

	public CultivosSubentidades getComisionesSubentidades(int plan, int linea, Calendar fechaEfecto, BigDecimal entMed,
			BigDecimal subEntMed, Session session, Boolean isBatch) throws Exception;

	public Socio getSocio(es.agroseguro.contratacion.declaracionSubvenciones.Socio socio, final Long idAsegurado,
			final Session session, final Boolean isBatch) throws Exception;

	public void actualizaHistEstado(Poliza polizaHbm, Date time, Session session, String string, Boolean isBatch);

	public void guardaSituacionActual(Poliza polizaHbm, String xmlText, Session session);

	public Poliza getPolizaPpalBBDD(final BigDecimal plan, final BigDecimal linea, final String referencia,
			final String moduloComp, final Session session, final Boolean isBatch) throws Exception;

	public void actualizaSbp(final Long idPoliza, final Session session, final Boolean isBatch) throws DAOException;

	public Asegurado getAseguradoBBDDGanado(final String nif, final BigDecimal codentidad, final Session session,
			final Boolean isBatch) throws Exception;

	public Termino obtenerTermino(final Session session,
			es.agroseguro.contratacion.explotacion.ExplotacionDocument.Explotacion explotacion, final Boolean isBatch);

	public List<Object> getCodConceptoEtiquetaTablaExplotacionesBBDD(final Long lineaseguroid, final Session session,
			final Boolean isBatch);

	public void actualizarHistoricoPoliza(final Poliza polizaHbm, final Session session, final Boolean isBatch);

	/* Tatiana (24.05.2021) */
	public Colectivo getColectivoBBDDGan(final String referencia, final int dc, final Session session) throws Exception;

	public boolean existePolizaHbm(int codplan, String referencia, Character tipoRef);

	public void guardarAuditoriaImportacionPoliza(Boolean isBatch, BigDecimal codPlan, String refPoliza, String xml,
			String usuario) throws DAOException;

	public BigDecimal getfilaRiesgoCubModulo(Long lineaseguroid, String codmodulo, BigDecimal cPmodulo,
			BigDecimal codRiesgoCub) throws Exception;

	public Colectivo getColectivoBBDDonline(es.agroseguro.contratacion.Colectivo colectivo, Session session,
			BigDecimal codPlan, Boolean isBatch) throws Exception;

	public String getDesGarantizado(BigDecimal valor);

	public String getDesCalcIndem(BigDecimal valor);

	public String getDesPctFranquicia(BigDecimal valor);

	public String getDesMinIndem(BigDecimal valor);

	public String getDesTipoFranqIndem(String string);

	public String getDesCapitalAseg(BigDecimal valor);

	public Asegurado getAseguradoBBDDGanadoOnline(final String nif, final BigDecimal codentidad, final Session session,
			final Boolean isBatch) throws Exception;

	public Object[] obtenerSubEntColectivo(Colectivo col, int codPlan, Session session, Boolean isBatch)
			throws Exception;

	public EstadoPoliza getEstadoPolizaBBDDGanado(Session session, Boolean isBatch, String refPoliza) throws Exception;

	public List<OrganizadorInformacion> obtenerlistOrgInformacion(Filter filter, Boolean isBatch, Session session);

	public List getOListOrg(Filter filter, boolean isBatch, Session sessionUse);

	/* ESC-15182 ** MODIF TAM (20.09.2021) ** Inicio */
	public Map<BigDecimal, com.rsi.agp.core.jmesa.service.impl.ImportacionPolizasService.RelacionEtiquetaTabla> getCodConceptoEtiquetaTablaExplotaciones(
			final Long lineaseguroid);

	public short getFilaExplotacionCobertura(Long lineaSeguroId, String modulo, int conceptoPpalMod, int riesgoCubierto)
			throws DAOException;

	public String getDescripcionConceptoPpalMod(int conceptoPpalMod) throws DAOException;

	public String getDescripcionRiesgoCubierto(Long lineaSeguroId, String modulo, int riesgoCubierto)
			throws DAOException;
	/* ESC-15182 ** MODIF TAM (20.09.2021) ** Fin */
}