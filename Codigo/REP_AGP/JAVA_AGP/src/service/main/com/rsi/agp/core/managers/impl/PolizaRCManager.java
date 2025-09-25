package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.CollectionsAndMapsUtil;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsRC;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.models.rc.IDatosRCDao;
import com.rsi.agp.dao.models.rc.IImpuestosRCDao;
import com.rsi.agp.dao.models.rc.IPolizasRCDao;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.admin.SubentidadMediadoraId;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRaza;
import com.rsi.agp.dao.tables.rc.DatosRC;
import com.rsi.agp.dao.tables.rc.EspeciesRC;
import com.rsi.agp.dao.tables.rc.EstadosRC;
import com.rsi.agp.dao.tables.rc.ImpuestosRC;
import com.rsi.agp.dao.tables.rc.PolizasHistEstadosRC;
import com.rsi.agp.dao.tables.rc.PolizasRC;
import com.rsi.agp.dao.tables.rc.RegimenRC;

public class PolizaRCManager implements IManager {

	private static final Log LOGGER = LogFactory.getLog(PolizaRCManager.class);

	private IPolizasRCDao polizasRCDao;
	private ILineaDao lineaDao;
	private IDatosRCDao datosRCDao;
	private IImpuestosRCDao impuestosRCDao;
	private IPolizaDao polizaDao;
	
	/**
	 * Obtiene la especie RC y el numero que animales
	 * @param explotaciones
	 * @param lineaseguroid
	 * @return
	 * @throws BusinessException
	 */
	public ListadoEspeciesRCNumAnimales getListadoEspeciesRCNumAnimales(Set<Explotacion> explotaciones, Long lineaseguroid) throws BusinessException {
		Map<EspeciesRC, Long> especiesRC = this.obtenerEspecies(explotaciones, lineaseguroid);
		return this.procesarEspecies(especiesRC);
	}

	private Map<EspeciesRC, Long> obtenerEspecies(Set<Explotacion> explotaciones, Long lineaseguroid) {
		Map<EspeciesRC, Long> resultado = new HashMap<EspeciesRC, Long>();
		for (Explotacion exp : explotaciones) {
			BigDecimal codEspecie = new BigDecimal(exp.getEspecie());
			BigDecimal regimen = new BigDecimal(exp.getRegimen());
			Set<GrupoRaza> grupoRazas = exp.getGrupoRazas();
			for (GrupoRaza gr : grupoRazas) {
				try {
					BigDecimal codTipoCapital = gr.getCodtipocapital();
					List<EspeciesRC> especiesRC = this.polizasRCDao.obtenerEspecieRC(lineaseguroid, codEspecie,	regimen, codTipoCapital);
					interno: for (EspeciesRC especieRC : especiesRC) {
						if(resultado.containsKey(especieRC)){
							Long numeroAnimales = resultado.get(especieRC) + gr.getNumanimales();
							resultado.put(especieRC, numeroAnimales);
							continue interno;
						}
						resultado.put(especieRC, gr.getNumanimales());
					}
				} catch (DAOException e) {
					LOGGER.error("Excepcion : PolizaRCManager - obtenerEspecies", e);
				}
			}
		}
		return resultado;
	}

	private ListadoEspeciesRCNumAnimales procesarEspecies(Map<EspeciesRC, Long> datosOrigen) {
		if(CollectionsAndMapsUtil.isEmpty(datosOrigen)){
			return new ListadoEspeciesRCNumAnimales(Boolean.FALSE, Boolean.FALSE);
		}
		List<EspeciesRC> especiesList = new ArrayList<EspeciesRC>(datosOrigen.keySet());
		if(!this.especiesIguales(especiesList)) {
			return new ListadoEspeciesRCNumAnimales(Boolean.TRUE, Boolean.FALSE);
		}
		EspeciesRC especiesRC = especiesList.get(0);
		Long numeroAnimales = datosOrigen.get(especiesRC);		
		return new ListadoEspeciesRCNumAnimales(especiesRC, numeroAnimales);
	}

	private boolean especiesIguales(List<EspeciesRC> especiesList) {
		boolean iguales = true;
		String codEspecieReferencia = especiesList.get(0).getCodespecie();
		for(int i = 1; i < especiesList.size(); i++){
			String codEspeciePrueba = especiesList.get(i).getCodespecie();
			if(!codEspecieReferencia.equals(codEspeciePrueba)){
				iguales = false;
				break;
			}
		}
		return iguales;
	}

	/**
	 * Obtiene los regímenes RC
	 * @param lineaSeguroId
	 * @param codSubentidad
	 * @param codEspecieRC
	 * @return
	 * @throws BusinessException
	 */
	public List<RegimenRC> getListadoRegimenesRC(Long lineaSeguroId, BigDecimal codSubentidad, BigDecimal codEntidad, 
			String codEspecieRC) throws BusinessException {
		try {
			return this.polizasRCDao.obtenerRegimenRC(lineaSeguroId, codSubentidad, codEntidad, codEspecieRC);
		} catch (DAOException e) {
			LOGGER.error("Excepcion : PolizaRCManager - getListadoRegimenesRC", e);
			return null;
		}
	}

	/**
	 * Guarda una poliza RC
	 * @param polizaRCVista
	 * @param codUsuario
	 * @throws BusinessException
	 */
	public void guardarPolizaRC(PolizasRC polizaRCVista, String codUsuario)
			throws BusinessException {
		try {

			RegimenRC regimenRCHibernate = (RegimenRC)this.polizasRCDao.getObject(RegimenRC.class, polizaRCVista.getRegimenRC().getCodregimen());
			EspeciesRC especieRCHibernate = (EspeciesRC)this.polizasRCDao.getObject(EspeciesRC.class, polizaRCVista.getEspeciesRC().getCodespecie());
			Poliza polizaHibernate = (Poliza)this.polizasRCDao.getObject(Poliza.class, polizaRCVista.getPoliza().getIdpoliza());
			EstadosRC estadoRCHibernate = (EstadosRC)this.polizasRCDao.getObject(EstadosRC.class, polizaRCVista.getEstadosRC().getId());
			PolizasRC polizaRCHibernate = (PolizasRC)this.polizasRCDao.getObject(PolizasRC.class, polizaRCVista.getId());
			
			if(polizaRCHibernate == null){
				this.grabarPolizaRCConHistorico(polizaRCVista, regimenRCHibernate, especieRCHibernate, polizaHibernate, estadoRCHibernate, codUsuario);
			} else {
				this.grabarPolizaRCSinHistorico(polizaRCHibernate, regimenRCHibernate, especieRCHibernate, polizaHibernate, estadoRCHibernate, codUsuario);
			}
			LOGGER.debug("Poliza e historico guardados correctamente");			
		} catch (DAOException e) {
			throw new BusinessException(e);
		}
	}
	
	private void grabarPolizaRCSinHistorico(PolizasRC polizaRC, RegimenRC regimen, EspeciesRC especie, Poliza poliza, 
			EstadosRC estadoRC, String codUsuario) throws DAOException {
		
		polizaRC.setRegimenRC(regimen);
		polizaRC.setEspeciesRC(especie);
		polizaRC.setPoliza(poliza);
		polizaRC.setEstadosRC(estadoRC);
		
		this.polizasRCDao.saveOrUpdate(polizaRC);
	}
	
	private void grabarPolizaRCConHistorico(PolizasRC polizaRC, RegimenRC regimen, EspeciesRC especie, Poliza poliza, 
			EstadosRC estadoRC, String codUsuario) throws DAOException {
		
		polizaRC.setRegimenRC(regimen);
		polizaRC.setEspeciesRC(especie);
		polizaRC.setPoliza(poliza);
		polizaRC.setEstadosRC(estadoRC);

		PolizasHistEstadosRC historico = new PolizasHistEstadosRC();
		historico.setCodusuario(codUsuario);
		historico.setPolizasRC(polizaRC);
		historico.setEstadosRC(estadoRC);
		historico.setFecha(Calendar.getInstance().getTime());
		
		this.polizasRCDao.saveOrUpdate(polizaRC);
		this.polizasRCDao.saveOrUpdate(historico);
	}
	
	public void cambiaEstadoPolizaRC(final Long idPoliza, final BigDecimal codEstado, String codUsuario) throws BusinessException {

		PolizasRC polizaRC;
		
		try {
			
			polizaRC = this.getPolizaRC(idPoliza);
			
			if (polizaRC != null) {
				
				EstadosRC estadoRC = (EstadosRC)this.polizasRCDao.getObject(EstadosRC.class, codEstado);
				polizaRC.setEstadosRC(estadoRC);
				
				PolizasHistEstadosRC historico = new PolizasHistEstadosRC();
				polizaRC.getPolizasHistEstadosRCs().add(historico);
	
				historico.setCodusuario(codUsuario);
				historico.setPolizasRC(polizaRC);
	
				historico.setEstadosRC(estadoRC);
				polizaRC.getEstadosRC().getPolizasHistEstadosRCs().add(historico);
	
				historico.setFecha(Calendar.getInstance().getTime());
				
				this.polizasRCDao.saveOrUpdate(polizaRC);
				this.polizasRCDao.saveOrUpdate(historico);
			}
		} catch (DAOException e) {
			
			throw new BusinessException(e);
		}
		
	}
	
	public void cambiaEstadoPolizaRC(final PolizasRC polizaRC, final BigDecimal codEstado, String codUsuario) throws BusinessException {
		
		try {
			
			EstadosRC estadoRC = (EstadosRC)this.polizasRCDao.getObject(EstadosRC.class, codEstado);
			polizaRC.setEstadosRC(estadoRC);
			
			PolizasHistEstadosRC historico = new PolizasHistEstadosRC();
			polizaRC.getPolizasHistEstadosRCs().add(historico);

			historico.setCodusuario(codUsuario);
			historico.setPolizasRC(polizaRC);

			historico.setEstadosRC(estadoRC);
			polizaRC.getEstadosRC().getPolizasHistEstadosRCs().add(historico);

			historico.setFecha(Calendar.getInstance().getTime());
			
			this.polizasRCDao.saveOrUpdate(polizaRC);
			this.polizasRCDao.saveOrUpdate(historico);
			
		} catch (DAOException e) {
			
			throw new BusinessException(e);
		}
	}
	
	/**
	 * Obtiene una poliza RC
	 * @param polizaId
	 * @return
	 * @throws BusinessException
	 */
	public PolizasRC getPolizaRC(Long polizaId) throws BusinessException {
		LOGGER.debug(new StringBuilder("Obteniendo PolizaRC asociada a la poliza ").append(polizaId.toString()));
		try {
			return this.polizasRCDao.obtenerPolizaRC(polizaId);
		} catch (DAOException e) {
			throw new BusinessException("Error al recuperar Poliza RC");
		}
	}
	
	
	/* Pet. 63485 ** MODIF TAM (17.07.2020) ** Inicio */
	/**
	 * Obtiene una poliza RC
	 * @param polizaId
	 * @return
	 * @throws BusinessException
	 */
	public Poliza getPoliza(Long polizaId) throws BusinessException {
		LOGGER.debug(new StringBuilder("Obteniendo Poliza asociada a la poliza ").append(polizaId.toString()));
		try {
			return this.polizaDao.getPolizaById(polizaId);
		} catch (DAOException e) {
			throw new BusinessException("Error al recuperar Poliza ");
		}
	}
	/* Pet. 63485 ** MODIF TAM (17.07.2020) ** Fin */
	
	/**
	 * Determina si una poliza es de ganado
	 * @param lineaseguroid
	 * @return
	 * @throws BusinessException
	 */
	public Boolean esPolizaGanado(Long lineaseguroid) throws BusinessException{
		try {
			return this.lineaDao.esLineaGanado(lineaseguroid);
		} catch (DAOException e) {
			LOGGER.error("Excepcion : PolizaRCManager - esPolizaGanado", e);
			return false;
		}
		
	}
	
	public List<PolizasRC> getListadoCalculosRC(final BigDecimal plan,
			final BigDecimal linea, final BigDecimal codentidad,
			final BigDecimal codsubentidad, final String codespecieRC,
			final BigDecimal codregimenRC, final Long numAnimales) throws BusinessException {
		
		return getListadoCalculosRC(plan, linea, codentidad, codsubentidad,
				codespecieRC, codregimenRC, numAnimales, null);
	}
	
	public PolizasRC getCalculoRC(final BigDecimal plan,
			final BigDecimal linea, final BigDecimal codentidad,
			final BigDecimal codsubentidad, final String codespecieRC,
			final BigDecimal codregimenRC, final Long numAnimales,
			final BigDecimal codSumaAsegurada) throws BusinessException {

		List<PolizasRC> calculosRC = getListadoCalculosRC(plan, linea,
				codentidad, codsubentidad, codespecieRC, codregimenRC,
				numAnimales, codSumaAsegurada);
		
		if (calculosRC != null && !calculosRC.isEmpty()) {
			
			return calculosRC.get(0);
			
		} else {
			
			return null;
		}
	}
	
	private List<PolizasRC> getListadoCalculosRC(final BigDecimal plan,
			final BigDecimal linea, final BigDecimal codentidad,
			final BigDecimal codsubentidad, final String codespecieRC,
			final BigDecimal codregimenRC, final Long numAnimales,
			final BigDecimal codSumaAsegurada) throws BusinessException {
	
		List<PolizasRC> polizasRC = null;
		PolizasRC polizaRC;
		BigDecimal importe;
		
		List<DatosRC> datosRC = getListadoSumasAseguradasRC(plan, linea, codentidad, codsubentidad, codespecieRC, codregimenRC);
		List<ImpuestosRC> impuestosRC = getListadoImpuestosRC(plan);
		
		if (datosRC != null && !datosRC.isEmpty()) {

			polizasRC = new ArrayList<PolizasRC>(datosRC.size());
			
			for (DatosRC datoRC: datosRC) {
				
				if (codSumaAsegurada == null
						|| (codSumaAsegurada != null && codSumaAsegurada
								.equals(datoRC.getSumaAseguradaRC()
										.getCodsuma()))) {
					
					polizaRC = new PolizasRC();
					polizaRC.getEspeciesRC().setCodespecie(codespecieRC);
					polizaRC.getRegimenRC().setCodregimen(codregimenRC);
					polizaRC.setCodSumaAsegurada(datoRC.getSumaAseguradaRC().getCodsuma());
					polizaRC.setSumaAsegurada(datoRC.getSumaAseguradaRC().getValor());
					polizaRC.setTasa(datoRC.getTasa());
					polizaRC.setFranquicia(datoRC.getFranquicia());
					polizaRC.setPrimaMinima(datoRC.getPrimaMinima());
					polizaRC.setPrimaNeta(datoRC.getTasa().multiply(BigDecimal.valueOf(numAnimales)));
					// SI LA PRIMA NETA ES INFERIOR A LA PRIMA MINIMA, NOS QUEDAMOS CON LA MINIMA
					if (polizaRC.getPrimaNeta().compareTo(polizaRC.getPrimaMinima()) < 0) {
						polizaRC.setPrimaNeta(polizaRC.getPrimaMinima());
					} 
					// IMPORTE BASE = PRIMA NETA
					importe = polizaRC.getPrimaNeta();
					// IMPUESTOS
					if (impuestosRC != null) {					
						for (ImpuestosRC impuestoRC: impuestosRC) {
							// CALCULO DE IMPUESTO (% SOBRE BASE)
							BigDecimal impuesto;
							BigDecimal valor_pct = impuestoRC.getValor().divide(BigDecimal.valueOf(100));
							if(ConstantsRC.BASE_IMP_SUMA_ASEGURADA.equals(impuestoRC.getBaseSbp().getId())) {
								impuesto = polizaRC.getSumaAsegurada().multiply(valor_pct);	
							} else {
								// POR DEFECTO BASE = PRIMA NETA
								impuesto = polizaRC.getPrimaNeta().multiply(valor_pct);		
							}
							// APLICACION DE IMPUESTOS
							importe = importe.add(impuesto);
						}
					}
					polizaRC.setImporte(importe);
					
					polizasRC.add(polizaRC);
				}
			}
		} else {
			
			throw new BusinessException("No hay datos de RC configurados.");
		}
		
		return polizasRC;		
	}
	
	private List<DatosRC> getListadoSumasAseguradasRC(final BigDecimal plan,
			final BigDecimal linea, final BigDecimal codentidad,
			final BigDecimal codsubentidad, final String codespecieRC,
			final BigDecimal codregimenRC) throws BusinessException {

		try {

			return (List<DatosRC>) this.datosRCDao.getDatosRC(plan, linea,
					codentidad, codsubentidad, codespecieRC, codregimenRC);

		} catch (DAOException e) {

			throw new BusinessException(e);
		}
	}
	
	private List<ImpuestosRC> getListadoImpuestosRC(final BigDecimal plan)
			throws BusinessException {

		try {

			return (List<ImpuestosRC>) this.impuestosRCDao.getImpuestosRC(plan);

		} catch (DAOException e) {

			throw new BusinessException(e);
		}
	}
	
	public void deletePolizaRC(final PolizasRC polizaRC) throws BusinessException {		
		try {
			if (polizaRC != null && polizaRC.getId() != null) this.polizasRCDao.delete(polizaRC);		
		} catch (DAOException e) {
			throw new BusinessException(e);
		}
	}
	
	public Boolean puedeCalcularRCGanado(final BigDecimal codentidad,
			final BigDecimal codsubentidad) throws BusinessException {

		Boolean result = Boolean.FALSE;

		try {

			SubentidadMediadoraId subentidadMediadoraId = new SubentidadMediadoraId(
					codentidad, codsubentidad);
			SubentidadMediadora subentidadMediadora = (SubentidadMediadora) this.polizasRCDao
					.get(SubentidadMediadora.class, subentidadMediadoraId);

			result = Constants.VALOR_1.equals(subentidadMediadora
					.getCalcularRcGanado());

		} catch (DAOException e) {

			throw new BusinessException(e);
		}

		return result;
	}
	
	public boolean polizaPplTieneRC(Long idPolizaPpl) throws DAOException{
		boolean tieneRC = false;
		boolean esPolizaGanado = this.polizaDao.esPolizaGanadoByIdPoliza(idPolizaPpl);
		if(esPolizaGanado){
			PolizasRC polizaRC = this.polizasRCDao.obtenerPolizaRC(idPolizaPpl);
			if(polizaRC != null){
				tieneRC = true;
			}
		}
		return tieneRC;
	}
	
	public SumaAseguraImportePolizaRC getSumaAseguradaImportePolizaRCPorPolizaPpl(Long polizaId) throws BusinessException{
		PolizasRC polizaRC = this.getPolizaRC(polizaId);		
		return new SumaAseguraImportePolizaRC(polizaRC.getSumaAseguradaFrmtd(), polizaRC.getImporteFrmtd());
	}
	
	public class SumaAseguraImportePolizaRC{
		private String sumaAsegurada;
		private String importe;
		
		public SumaAseguraImportePolizaRC(String sumaAsegurada,
				String importe) {
			super();
			this.sumaAsegurada = sumaAsegurada;
			this.importe = importe;
		}

		public String getSumaAsegurada() {
			return sumaAsegurada;
		}

		public String getImporte() {
			return importe;
		}
	}
		
	/**
	 * Clase donde se encierra el resultado del procesamiento del metodo getListadoEspeciesRCNumAnimales
	 * @author srojo
	 *
	 */
	public class ListadoEspeciesRCNumAnimales{
		private Boolean registros;
		private EspeciesRC especieRC;
		private Long totalAnimales;
		private Boolean especieUnica;
		
		public ListadoEspeciesRCNumAnimales(EspeciesRC especie, Long totalAnimales) {
			this.registros = Boolean.TRUE;
			this.especieRC = especie;
			this.totalAnimales = totalAnimales;
			this.especieUnica = Boolean.TRUE;
		}

		public ListadoEspeciesRCNumAnimales(Boolean registros, Boolean especieUnica) {
			super();
			this.registros = registros;
			this.especieUnica = especieUnica;
		}
		
		public Boolean hayRegistros(){
			return this.registros;
		}
				
		public EspeciesRC getEspecie() {
			return this.especieRC;
		}

		public Long getTotalAnimales() {
			return this.totalAnimales;
		}

		public Boolean esEspecieUnica() {
			return this.especieUnica;
		}
	}

	public void setPolizasRCDao(IPolizasRCDao polizasRCDao) {
		this.polizasRCDao = polizasRCDao;
	}

	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}
	
	public void setDatosRCDao(IDatosRCDao datosRCDao) {
		this.datosRCDao = datosRCDao;
	}

	public void setImpuestosRCDao(IImpuestosRCDao impuestosRCDao) {
		this.impuestosRCDao = impuestosRCDao;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}
	
	
}
