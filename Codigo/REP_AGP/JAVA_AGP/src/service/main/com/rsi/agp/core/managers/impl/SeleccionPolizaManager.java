package com.rsi.agp.core.managers.impl;

import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Clob;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IConsultaSbpManager;
import com.rsi.agp.core.managers.IHistoricoEstadosManager;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.managers.ISimulacionSbpManager;
import com.rsi.agp.core.managers.impl.HistoricoEstadosManager.Tabla;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.NumberUtils;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.VistaImportes;
import com.rsi.agp.core.webapp.util.VistaImportesPorGrupoNegocio;
import com.rsi.agp.dao.filters.cesp.impl.ModuloCompatibleFiltro;
import com.rsi.agp.dao.filters.commons.LineasFiltro;
import com.rsi.agp.dao.filters.poliza.EstadoPolizaFilter;
import com.rsi.agp.dao.filters.poliza.PolizaFiltro;
import com.rsi.agp.dao.filters.poliza.SeleccionPolizaFiltro;
import com.rsi.agp.dao.models.admin.IAseguradoDao;
import com.rsi.agp.dao.models.anexo.IXmlAnexoModificacionDao;
import com.rsi.agp.dao.models.config.IPantallaConfigurableDao;
import com.rsi.agp.dao.models.copy.IPolizaCopyDao;
import com.rsi.agp.dao.models.cpm.ICPMTipoCapitalDAO;
import com.rsi.agp.dao.models.impl.DatabaseManager;
import com.rsi.agp.dao.models.poliza.CambioMasivoVO;
import com.rsi.agp.dao.models.poliza.ICapitalAseguradoDao;
import com.rsi.agp.dao.models.poliza.IDistribucionCosteDAO;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.models.poliza.ISeleccionPolizaDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.ClaseDetalle;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.DatoAsegurado;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.cesp.ModuloCompatibleCe;
import com.rsi.agp.dao.tables.cgen.Organismo;
import com.rsi.agp.dao.tables.cgen.SistemaCultivo;
import com.rsi.agp.dao.tables.cgen.TipoSubvencionEnesa;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.poliza.CapAsegRelModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.Comunicaciones;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.DatosAval;
import com.rsi.agp.dao.tables.poliza.DistCosteSubvencion;
import com.rsi.agp.dao.tables.poliza.DistribucionCoste;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.ParcelaCobertura;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.SubvParcelaCCAA;
import com.rsi.agp.dao.tables.poliza.SubvParcelaENESA;
import com.rsi.agp.dao.tables.poliza.TipoRdto;
import com.rsi.agp.dao.tables.poliza.dc2015.BonificacionRecargo2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteSubvencion2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;
import com.rsi.agp.dao.tables.ref.ReferenciaAgricola;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;
import com.rsi.agp.pagination.PageProperties;
import com.rsi.agp.pagination.PaginatedListImpl;
import com.rsi.agp.vo.ProduccionVO;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.acuseRecibo.AcuseReciboDocument;
import es.agroseguro.acuseRecibo.Documento;
import es.agroseguro.seguroAgrario.rendimientosCalculo.Cosecha;
import es.agroseguro.seguroAgrario.rendimientosCalculo.PolizaDocument;
import es.agroseguro.serviciosweb.contratacionscrendimientos.AgrException;
import es.agroseguro.serviciosweb.contratacionscrendimientos.CalcularRendimientosRequest;
import es.agroseguro.tipos.AjustarProducciones;

public class SeleccionPolizaManager implements IManager {

	private ISeleccionPolizaDao seleccionPolizaDao;
	private IPolizaDao polizaDao;
	private IAseguradoDao aseguradoDao;
	private DatabaseManager databaseManager;
	private IDistribucionCosteDAO distribucionCosteDAO;
	private IPantallaConfigurableDao pantallaConfigurableDao;
	private IConsultaSbpManager consultaSbpManager;
	private ISimulacionSbpManager simulacionSbpManager;
	private IHistoricoEstadosManager historicoEstadosManager;
	private PolizaManager polizaManager;
	private ICPMTipoCapitalDAO cpmTipoCapitalDao;
	private ICapitalAseguradoDao capitalAseguradoDao;
	private IXmlAnexoModificacionDao xmlAnexoModDao;
	private CalculoPrecioProduccionManager calculoPrecioProduccionManager;

	private IPolizaCopyDao polizaCopyDao;

	protected final Log logger = LogFactory.getLog(getClass());
	private final ResourceBundle bundle = ResourceBundle.getBundle("agp");

	/**
	 * SONAR Q ** MODIF TAM(28.10.2021) ** Se ha eliminado todo el c�digo comentado
	 **/

	/** CONSTANTES SONAR Q ** MODIF TAM (28.10.2021) ** Inicio **/
	private final static String ALERT = "alerta";
	private final static String ERROR_UTL = "Error inesperado al obtener el listado de utilidades";
	private final static String ERROR_LIST_TL = "Error al obtener el listado de utilidades";
	/** CONSTANTES SONAR Q ** MODIF TAM (28.10.2021) ** Fin **/
	private final String MODO_LECT = "modoLectura";

	private final static String VACIO = "";
	
	public ModelAndView doEditar(Long idPoliza, Asegurado asegurado, String modoLectura, String borrarPolizaSbp, 
			Usuario usuario, Map<String, Object> parameters, String realPath) throws BusinessException, DAOException{
		
		Poliza polizaBean = this.getPolizaById(idPoliza);
		List<Poliza> listaPolizas = null;
		ModelAndView resultado;
		
		//Llamada al procedimiento en pl/sql CHECK_ASEG_AUTORIZADOS
		this.check_aseg_autorizados(polizaBean.getLinea().getLineaseguroid(), asegurado.getNifcif());

		// Si hemos pulsado el boton ver, podemos hacer el flujo entero de
		// la poliza, pero sin modificar datos
		/// mejora 112 Angel 01/02/2012 aiadida la opcion de ver la poliza sin opcion a editarla tambien con estado grabacion definitiva
		
		
		if (!polizaBean.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA) && !modoLectura.equals(MODO_LECT)) 
		{
			if ((usuario.getPerfil().equals(Constants.PERFIL_USUARIO_ADMINISTRADOR)
					|| usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SEMIADMINISTRADOR) 
					|| usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES))
					&& (polizaBean.getBloqueadopor() != null)
					&& !polizaBean.getBloqueadopor().equals(usuario.getCodusuario())) 
			{

				 // Preguntamos al usuario si desea qeu desbloqueemos la poliza
				listaPolizas = this.getPolizas(polizaBean);
				parameters.put("confirmarBloqueo", "SI");
				parameters.put("polizaBean", polizaBean);
				parameters.put("listaPolizas", listaPolizas);

				resultado = new ModelAndView("moduloPolizas/polizas/seleccion/seleccionPolizas","polizaBean", polizaBean);
				// Comprueba si tiene que mostrar el boton de sobreprecio
				resultado.addObject("polizaSbp", mostrarBotonSbp(parameters, listaPolizas,	usuario, polizaBean.getLinea().isLineaGanado()));

			} 
			else if (polizaBean.getBloqueadopor() == null || polizaBean.getBloqueadopor().equals(usuario.getCodusuario())) 
			{
				 // ---- Cada vez que editamos una poliza la bloqueamos,si no esta bloqueada ----
				polizaBean.setBloqueadopor(usuario.getCodusuario());
				polizaBean.setFechabloqueo((new GregorianCalendar()).getTime());

				// ---- si tiene poliza Sbp asociada en estado Definitiva, la borramos
				if ("true".equals(borrarPolizaSbp)){
					// *** SBP: borramos la poliza de Sbp asociada ***
					consultaSbpManager.borrarPolizaSbpByPoliza(polizaBean, usuario, realPath);
				}
				
				//REP-AGP.P000019038@055 [Inicio]
				//polizaBean.getDistribucionCostes().clear(); // delete distribucion de costes
				//polizaBean.getPagoPolizas().clear();//Para borrar el pago cuando se pasa a pendiente de validaciin y no interfiera luego					
				deleteDistribucionCostes(polizaBean);
				DatosAval dv = polizaManager.GetDatosAval(polizaBean.getIdpoliza());
				if (null!=dv) {
					polizaManager.DeleteDatosAval(dv);
					polizaBean.setDatosAval(null);
				}
				//REP-AGP.P000019038@055 [Fin]				
				
				polizaBean.setTotalsuperficie(null);		// Mejora 96 Angel 26/01/2012 - delete totalSuperficie
				
				// ---- estado poliza = 1(pendiente de validacion) ----
				EstadoPoliza estadoPolNuevo = new EstadoPoliza();
				estadoPolNuevo.setIdestado(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION);

				this.savePoliza(polizaBean, estadoPolNuevo, usuario.getCodusuario());

				resultado = new ModelAndView("redirect:/polizaController.html").addObject("idpoliza", idPoliza);
			} 
			else 
			{
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				listaPolizas = this.getPolizas(polizaBean);
				String codUsuario = polizaBean.getBloqueadopor();
				String nomUsuario = this.getUsuario(codUsuario).getNombreusu();
				String fechaBloqueo = sdf.format(polizaBean.getFechabloqueo());

				parameters.put("alerta", MessageFormat.format(
						ResourceBundle.getBundle("agp").getString("mensaje.poliza.bloqueada.usuario.KO"), 
						codUsuario, nomUsuario, fechaBloqueo));
				parameters.put("polizaBean", polizaBean);
				parameters.put("listaPolizas", listaPolizas);

				resultado = new ModelAndView("moduloPolizas/polizas/seleccion/seleccionPolizas","polizaBean", polizaBean);
				 // Comprueba si tiene que mostrar el boton de sobreprecio
				resultado.addObject("polizaSbp", mostrarBotonSbp(parameters, listaPolizas,	usuario, polizaBean.getLinea().isLineaGanado()));
			}
		} 
		else 
		{
			resultado = new ModelAndView("redirect:/polizaController.html").addObject("idpoliza", idPoliza).addObject(MODO_LECT, MODO_LECT);
		}
		return resultado;
	}

	public PolizaSbp mostrarBotonSbp(Map<String, Object> parameters, List<Poliza> listaPolizas, Usuario usuario,
			boolean isLineaGanado) throws DAOException, BusinessException {

		// Fase 4.habilitamos o no el boton en funcion de su lineaSeguroid
		PolizaSbp polizaSbp = new PolizaSbp();
		polizaSbp.setIncSbpComp('N');

		// Si es una linea de ganado no se puede hacer sobreprecio
		if (isLineaGanado) {
			parameters.put("addBotonSbp", false);
			parameters.put("existePolizaSbp", false);
			return polizaSbp;
		}

		for (Poliza pol : listaPolizas) {
			if (pol.getTipoReferencia().equals('P')) {
				polizaSbp.getPolizaPpal().setIdpoliza(pol.getIdpoliza());
				polizaSbp.getPolizaPpal().getEstadoPoliza().setIdestado(pol.getEstadoPoliza().getIdestado());
				parameters.put("addBotonSbp", this.addBotonSbp(pol, usuario.getPerfil()));
				PolizaSbp p = simulacionSbpManager.existePolizaSbp(polizaSbp);

				parameters.put("existePolizaSbp", p != null ? true : false);

			} else if (pol.getTipoReferencia().equals('C')) {
				polizaSbp.setPolizaCpl(new Poliza());
				polizaSbp.getPolizaCpl().setIdpoliza(pol.getIdpoliza());
				polizaSbp.getPolizaCpl().getEstadoPoliza().setIdestado(pol.getEstadoPoliza().getIdestado());
			}
		}
		return polizaSbp;
	}

	/**
	 * Metodo para clonar los datos de una parcela
	 * 
	 * @param parcela
	 * @return clonParcela
	 */
	public Parcela clonarParcela(Long idParcela) {
		Parcela parcela = null;
		Parcela clonParcela = new Parcela();

		try {
			parcela = (Parcela) seleccionPolizaDao.getObject(Parcela.class, idParcela);

			clonParcela.setIdparcela(null);

			/* Poliza */
			Poliza poliza = parcela.getPoliza();
			clonParcela.setPoliza(poliza);
			clonParcela.setTipoparcela('P');
			/* Hoja y Numero */
			clonParcela.setHoja(null);
			clonParcela.setNumero(null);
			/* Cultivo y Variedad */
			clonParcela.setCodcultivo(parcela.getCodcultivo());
			clonParcela.setCodvariedad(parcela.getCodvariedad());
			Variedad variedad = parcela.getVariedad();
			clonParcela.setVariedad(variedad);
			/* SIGPAC */
			clonParcela.setAgrsigpac(parcela.getAgrsigpac());
			clonParcela.setCodprovsigpac(parcela.getCodprovsigpac());
			clonParcela.setCodtermsigpac(parcela.getCodtermsigpac());
			clonParcela.setParcelasigpac(parcela.getParcelasigpac());
			clonParcela.setPoligonosigpac(parcela.getPoligonosigpac());
			clonParcela.setRecintosigpac(parcela.getRecintosigpac());
			clonParcela.setZonasigpac(parcela.getZonasigpac());
			/* Ident catas */
			clonParcela.setParcela(parcela.getParcela());
			clonParcela.setPoligono(parcela.getPoligono());
			/* Ubicacion */
			Termino termino = parcela.getTermino();
			clonParcela.setTermino(termino);
			/* Nombre */
			clonParcela.setNomparcela(parcela.getNomparcela());

			/* Subvenciones CCAA */
			Set<SubvParcelaCCAA> subvParcelaCCAAs = new HashSet<SubvParcelaCCAA>();
			SubvParcelaCCAA objSubv = null;

			for (SubvParcelaCCAA ccaa : parcela.getSubvParcelaCCAAs()) {
				objSubv = new SubvParcelaCCAA();
				objSubv.setParcela(clonParcela);
				objSubv.setSubvencionCCAA(ccaa.getSubvencionCCAA());
				subvParcelaCCAAs.add(objSubv);
			}
			clonParcela.setSubvParcelaCCAAs(subvParcelaCCAAs);

			/* Subvenciones ENESA */
			Set<SubvParcelaENESA> subvParcelaENESAs = new HashSet<SubvParcelaENESA>();
			SubvParcelaENESA objSubvE = null;

			for (SubvParcelaENESA enesa : parcela.getSubvParcelaENESAs()) {
				objSubvE = new SubvParcelaENESA();
				objSubvE.setParcela(clonParcela);
				objSubvE.setSubvencionEnesa(enesa.getSubvencionEnesa());
				subvParcelaENESAs.add(objSubvE);
			}
			clonParcela.setSubvParcelaENESAs(subvParcelaENESAs);

			/* Coberturas */
			Set<ParcelaCobertura> coberturasParcela = new HashSet<ParcelaCobertura>();
			ParcelaCobertura objCob = null;

			for (ParcelaCobertura pc : parcela.getCoberturasParcela()) {
				objCob = new ParcelaCobertura();
				objCob.setCodvalor(pc.getCodvalor());
				objCob.setConceptoPpalModulo(pc.getConceptoPpalModulo());
				objCob.setDiccionarioDatos(pc.getDiccionarioDatos());
				objCob.setParcela(clonParcela);
				objCob.setRiesgoCubierto(pc.getRiesgoCubierto());
				coberturasParcela.add(objCob);
			}
			clonParcela.setCoberturasParcela(coberturasParcela);

			/* Capitales asegurados */
			Set<CapitalAsegurado> capitalAsegurados = new HashSet<CapitalAsegurado>();
			CapitalAsegurado objCapAs = null;

			for (CapitalAsegurado ca : parcela.getCapitalAsegurados()) {
				objCapAs = new CapitalAsegurado();
				objCapAs.setParcela(clonParcela);
				objCapAs.setPrecio(ca.getPrecio());
				objCapAs.setPreciomodif(ca.getPreciomodif());
				objCapAs.setProduccion(ca.getProduccion());
				objCapAs.setProduccionmodif(ca.getProduccionmodif());
				objCapAs.setSuperficie(ca.getSuperficie());
				objCapAs.setTipoCapital(ca.getTipoCapital());

				Set<CapAsegRelModulo> capAsegRelModulos = new HashSet<CapAsegRelModulo>();
				CapAsegRelModulo objCapAsegRel = null;

				for (CapAsegRelModulo carm : ca.getCapAsegRelModulos()) {
					objCapAsegRel = new CapAsegRelModulo();
					objCapAsegRel.setCapitalAsegurado(objCapAs);
					objCapAsegRel.setCodmodulo(carm.getCodmodulo());
					objCapAsegRel.setPrecio(carm.getPrecio());
					objCapAsegRel.setPreciomodif(carm.getPreciomodif());
					objCapAsegRel.setProduccion(carm.getProduccion());
					objCapAsegRel.setProduccionmodif(carm.getProduccionmodif());
					capAsegRelModulos.add(objCapAsegRel);
				}
				objCapAs.setCapAsegRelModulos(capAsegRelModulos);

				Set<DatoVariableParcela> datoVariableParcelas = new HashSet<DatoVariableParcela>();
				DatoVariableParcela objDatoVar = null;

				for (DatoVariableParcela dvp : ca.getDatoVariableParcelas()) {
					objDatoVar = new DatoVariableParcela();
					objDatoVar.setCapitalAsegurado(objCapAs);
					objDatoVar.setDiccionarioDatos(dvp.getDiccionarioDatos());
					objDatoVar.setRelacionCampo(dvp.getRelacionCampo());
					objDatoVar.setValor(dvp.getValor());
					datoVariableParcelas.add(objDatoVar);
				}
				objCapAs.setDatoVariableParcelas(datoVariableParcelas);

				capitalAsegurados.add(objCapAs);
			}
			clonParcela.setCapitalAsegurados(capitalAsegurados);

			clonParcela.setIndRecalculoHojaNumero(new Integer(1));
			seleccionPolizaDao.saveParcela(clonParcela);

		} catch (Exception ex) {
			logger.error(" Se ha producido un error al clonar la parcela.", ex);
		}
		return clonParcela;
	}

	/**
	 * Obtiene un objeto poliza de la BD
	 * 
	 * @param idPoliza
	 *            Identificador de la poliza en la BD
	 */
	public final Poliza getPolizaById(final Long idPoliza) {
		return (Poliza) seleccionPolizaDao.getObject(Poliza.class, idPoliza);
	}

	/**
	 * Obtiene un listado de objetos poliza de la BD
	 * 
	 * @param poliza
	 */
	@SuppressWarnings("unchecked")
	public final List<Poliza> getPolizas(final Poliza poliza) {
		final SeleccionPolizaFiltro filter = new SeleccionPolizaFiltro(poliza);
		BigDecimal[] estadosPolizaNoIncluir = new BigDecimal[1];
		Arrays.fill(estadosPolizaNoIncluir, Constants.ESTADO_POLIZA_BAJA);
		filter.setEstadosPolizaNoIncluir(estadosPolizaNoIncluir);
		return seleccionPolizaDao.getObjects(filter);
	}

	@SuppressWarnings("unchecked")
	public final List<Poliza> getPolizasButEstados(final Poliza poliza, BigDecimal estadosPolizaNoIncluir[]) {
		final SeleccionPolizaFiltro filter = new SeleccionPolizaFiltro(poliza);
		filter.setEstadosPolizaNoIncluir(estadosPolizaNoIncluir);
		return seleccionPolizaDao.getObjects(filter);
	}

	public List<Parcela> getParcelas(Parcela parcela, String columna, String orden) {
		List<Parcela> listparcelas = null;
		try {
			listparcelas = seleccionPolizaDao.getParcelas(parcela, columna, orden);
		} catch (Exception exception) {
			logger.error("Error al obtener el listado de parcelas", exception);
		}
		return listparcelas;
	}

	/**
	 * DAA 24/04/12 Recorre las lista de parcelas filtradas y devuelve una nueva
	 * lista filtrada ademas por sistema de cultivo
	 * 
	 * @param listaparcelas
	 * @param sistcultivo
	 * @return listaParcelasFinal o listaparcelas
	 */
	public List<Parcela> getParcelasFiltradas(List<Parcela> listaparcelas, String sistcultivo) {
		List<Parcela> listaParcelasFinal = new ArrayList<Parcela>();

		logger.debug("SeleccionPolizaManager - getParcelasFiltradas");

		// MPM 08/05/2012
		// Anadida la modificacion para que se filtre por el dato variable
		// "Sistema de cultivo", que es el que corresponde al codconcepto 123

		// Si se ha filtrado el sistema de cultivo por algun valor
		if (!"".equals(StringUtils.nullToString(sistcultivo))) {

			/** SONAR Q ** MODIF TAM (28.10.2021) ** Inicio **/
			/* Sacamos c�digo fuera para descargar la funci�n de ifs/fors */
			listaParcelasFinal = obtenerListParcFinal(listaparcelas, sistcultivo);
			/** SONAR Q ** MODIF TAM (28.10.2021) ** Final **/

			return listaParcelasFinal;
		}
		// Si no filtro por sistcultivo devuelvo el listado de parcelas original
		else {
			return listaparcelas;
		}
	}

	/**
	 * Recorre las lista de parcelas filtradas y devuelve una nueva lista filtrada
	 * ademas por Rendimiento Historico
	 * 
	 * @param listaparcelas
	 * @param RdtoHist
	 * @return listaParcelasFinal o listaparcelas
	 */
	public List<Parcela> getParcelasFiltradasRdtoHist(List<Parcela> listaparcelas, String RdtoHist) {
		List<Parcela> listaParcelasFinal = new ArrayList<Parcela>();

		logger.debug("SeleccionPolizaManager - getParcelasFiltradas");

		if (!"".equals(StringUtils.nullToString(RdtoHist))) {
			/* SONAR Q ** MODIF TAM (28.10.2021) ** Inicio */
			/* Sacamos c�digo fuera para descargar la funci�n de ifs/fors */
			listaParcelasFinal = obtenerListParcFinalRdtoHist(listaparcelas, RdtoHist);
			/* SONAR Q ** MODIF TAM (28.10.2021) ** Fin */
			return listaParcelasFinal;
		} else {
			return listaparcelas;
		}
	}

	/**
	 * Guarda un objeto poliza en la BD
	 * 
	 * @param polizaBean
	 */
	public Long savePoliza(Poliza polizaBean) {
		Long idpoliza = seleccionPolizaDao.savePoliza(polizaBean);
		this.seleccionPolizaDao.evict(polizaBean);
		this.seleccionPolizaDao.evict(polizaBean.getUsuario());
		return idpoliza;
	}

	public Long savePoliza(Poliza polizaBean, EstadoPoliza newEstado, String codusuario) {

		boolean insertaHistorico = false;
		Long idpoliza;

		// Actualizo el historico de estados si el estado es distinto al de la poliza
		if (newEstado != null && (polizaBean.getEstadoPoliza().getIdestado() == null
				|| !polizaBean.getEstadoPoliza().getIdestado().equals(newEstado.getIdestado()))) {
			insertaHistorico = true;

		}
		// Guardo la poliza
		if (newEstado != null) {
			polizaBean.setEstadoPoliza(newEstado);
		}
		idpoliza = this.savePoliza(polizaBean);

		if (insertaHistorico) {
			historicoEstadosManager.insertaEstado(Tabla.POLIZAS, idpoliza, codusuario, newEstado.getIdestado());
		}
		return idpoliza;
	}

	/**
	 * Elimina una parcela de la BD
	 * 
	 * @param codParcela
	 *            Codigo o id de la parcela a eliminar.
	 */
	public void deleteParcela(Long codParcela) throws DAOException {
		this.seleccionPolizaDao.deleteParcela(codParcela);
	}

	/**
	 * Elimina una lista de parcelas de la BD
	 * 
	 * @param listaParcelas,
	 *            lista de las parcelas a eliminar.
	 */
	public void deleteParcelas(List<Long> listaParcelas) throws DAOException {
		this.seleccionPolizaDao.deleteParcelas(listaParcelas);
	}

	/**
	 * Metodo para obtener la pantalla configuracion de la seccion de datos
	 * variables de poliza para montar la pantalla de parcela
	 * 
	 * @param lineaseguroid
	 *            - Plan/linea a comprobar
	 * @param idPantalla
	 *            - Identificador de pantalla a comprobar
	 * @return PantallaConfigurable: la pantalla configurable.
	 */
	public PantallaConfigurable getPantallaVarPoliza(Long lineaseguroid, Long idPantalla) {
		return this.pantallaConfigurableDao.getPantallaVarPoliza(null, idPantalla, lineaseguroid);
	}

	/**
	 * DAA 12/06/2012 Establece la hoja y numero de las parcelas de una poliza
	 * 
	 * @param poliza
	 * @return Mapa con las hoja-numero de cada id de parcela
	 * @throws DAOException
	 */
	public void obtenerHojaNumero(Poliza poliza) throws DAOException {

		// Para almacenar la hoja-numero de cada idParcela
		int hoja = 0;
		int num = 0;
		String terminoAnterior = "";

		// MPM 09/12/2013
		// Las parcelas se ordenan por los siguientes campos, segin las indicaciones
		// recibidas en el sigpe 6003
		// Provincia, comarca, tirmino, subtirmino, cultivo, variedad, SIGPAC (por valor
		// numirico) y Cidigo Tipo Capital
		// Obtiene las parcelas asociadas a la poliza
		Parcela parcelaAux = new Parcela();
		parcelaAux.setPoliza(poliza);
		List<Parcela> listaParcelas = this.getParcelas(parcelaAux, null, "asc");
		Set<Parcela> parcelas = poliza.getParcelas(); // susana
		// Recorre las parcelas, que vienen ordenadas por los campos indicados
		for (Parcela parcela : listaParcelas) {
			// Se obtiene la ubicaciin para esta parcela (provincia, comarca, tirmino y
			// subtirmino)
			String terminoNuevo = parcela.getTermino().getId().getCodprovincia().toString().trim() + "|"
					+ parcela.getTermino().getId().getCodcomarca().toString().trim() + "|"
					+ parcela.getTermino().getId().getCodtermino().toString().trim() + "|"
					+ parcela.getTermino().getId().getSubtermino().toString().trim();

			logger.debug(parcela.getIdparcela() + " - Termino nuevo " + terminoNuevo + ", Termino antiguo "
					+ terminoAnterior);

			// Si la ubicaciin ha cambiado con respecto a la parcela anterior, metemos nueva
			// hoja
			if (!terminoNuevo.equals(terminoAnterior)) {
				hoja++;
				num = 1;
				logger.debug("Hoja nueva: " + hoja);
			} else {
				// si he llegado al limite de num por hoja metemos nueva hoja
				if ((num % Constants.MAX_NUM_HOJA) == 0) {
					hoja++;
					num = 1;
					logger.debug("Hoja nueva: " + hoja);
				} else {
					num++;
				}
			}
			// En todo caso asignamos a la parcela la hoja y el numero.
			// ya estan ordenadas previamente por provincia, termino, subtermino
			// y comarca.
			seleccionPolizaDao.actualizaHojaNumero(hoja, num, parcela.getIdparcela());

			terminoAnterior = terminoNuevo;
			Iterator<Parcela> it = parcelas.iterator();
			boolean encontrado = false;
			while (it.hasNext() && !encontrado) {
				Parcela parcelaSet = it.next();
				if (parcela.getIdparcela().equals(parcelaSet.getIdparcela())) {
					parcelaSet.setHoja(hoja);// susana
					parcelaSet.setNumero(num);// susana
					encontrado = true;
				}

			}

		}
	}

	@SuppressWarnings("unchecked")
	public List<TipoRdto> getTiposRendimiento() {
		return this.seleccionPolizaDao.getObjects(TipoRdto.class, null, null);
	}

	@SuppressWarnings("unchecked")
	public List<EstadoPoliza> getEstadosPoliza(BigDecimal estadosPolizaExcluir[]) {
		EstadoPolizaFilter estadoPolizaFilter = new EstadoPolizaFilter();
		estadoPolizaFilter.setEstadosPolizaExcluir(estadosPolizaExcluir);
		return this.seleccionPolizaDao.getObjects(estadoPolizaFilter);
	}

	public String getListCodModulos(final Long idpoliza) {
		StringBuilder listCodModulos = new StringBuilder("");
		try {
			int control = 1;
			List<ModuloPoliza> modulos = seleccionPolizaDao.getModulosPoliza(idpoliza);
			for (ModuloPoliza item : modulos) {
				if (control < modulos.size()) {
					listCodModulos.append(item.getId().getCodmodulo()).append(",");
				} else { // el ultimo
					listCodModulos.append(item.getId().getCodmodulo());
				}
				control++;
			}
		} catch (Exception exception) {
			logger.error("Excepcion : SeleccionPolizaManager - getListCodModulos", exception);
		}
		return listCodModulos.toString();
	}

	public String getListCodCPModulos(final Long lineaseguroid, final String listCodModulos) {
		StringBuffer sb = new StringBuffer("");
		List<RiesgoCubiertoModulo> rcModulos = seleccionPolizaDao.getRiesgosCubiertosModulos(lineaseguroid,
				listCodModulos);
		List<BigDecimal> auxLst = new ArrayList<BigDecimal>(rcModulos.size());
		for (RiesgoCubiertoModulo rcm : rcModulos) {
			BigDecimal codRC = rcm.getConceptoPpalModulo().getCodconceptoppalmod();
			if (!auxLst.contains(codRC)) {
				auxLst.add(codRC);
				sb.append(codRC).append(",");
			}
		}
		if (sb.length() > 0)
			sb.deleteCharAt(sb.lastIndexOf(","));
		return sb.toString();
	}

	public String getListCodRCModulos(final Long lineaseguroid, final String listCodModulos) {
		StringBuffer sb = new StringBuffer("");
		List<RiesgoCubiertoModulo> rcModulos = seleccionPolizaDao.getRiesgosCubiertosModulos(lineaseguroid,
				listCodModulos);
		List<BigDecimal> auxLst = new ArrayList<BigDecimal>(rcModulos.size());
		for (RiesgoCubiertoModulo rcm : rcModulos) {
			BigDecimal codRC = rcm.getRiesgoCubierto().getId().getCodriesgocubierto();
			if (!auxLst.contains(codRC)) {
				auxLst.add(codRC);
				sb.append(codRC).append(",");
			}
		}
		if (sb.length() > 0)
			sb.deleteCharAt(sb.lastIndexOf(","));
		return sb.toString();
	}

	public String getListModulesWithComparativas(Long idpoliza) {
		StringBuilder listCodigosModulos = new StringBuilder("");
		List<String> acumulador = new ArrayList<String>();
		String result = "";
		try {
			List<ComparativaPoliza> listComparativaPoliza = (seleccionPolizaDao
					.getModulosPolizaWithComparativa(idpoliza));
			for (int i = 0; i < listComparativaPoliza.size(); i++) {
				ComparativaPoliza comparativaPoliza = listComparativaPoliza.get(i);
				if (!acumulador.contains(comparativaPoliza.getId().getCodmodulo())) {
					listCodigosModulos.append(comparativaPoliza.getId().getCodmodulo()).append(";");
					acumulador.add(comparativaPoliza.getId().getCodmodulo());
				}
			}
			if (listCodigosModulos.length() > 0)
				result = listCodigosModulos.substring(0, listCodigosModulos.length() - 1);
		} catch (Exception exception) {
			logger.error("Error en getListModulesWithComparativas2", exception);
		}
		return result;
	}

	public void deleteDistribucionCostes(Poliza poliza) {
		try {
			BigDecimal codplan = new BigDecimal("2015");
			if (poliza.getLinea().getCodplan().compareTo(codplan) == -1) {
				Set<DistribucionCoste> distCostes = poliza.getDistribucionCostes();
				for (DistribucionCoste distCoste : distCostes) {
					distribucionCosteDAO.deleteDistribucionCoste(distCoste.getPoliza().getIdpoliza(),
							distCoste.getCodmodulo(), distCoste.getFilacomparativa());
				}
				poliza.getDistribucionCostes().clear();
			} else {
				deleteDistribucionCostes2015(poliza);
			}
		} catch (Exception exception) {
			// Void
		}
	}

	public void deleteDistribucionCoste(Poliza poliza, String codModulo, BigDecimal fila, Long idComparativa) {
		try {

			BigDecimal codplan = new BigDecimal("2015");
			if (poliza.getLinea().getCodplan().compareTo(codplan) == -1) {

				/* MODIF TAM (28.10.2021) ** SONAR Q ** Inicio */
				/*
				 * Sacamos a otra funci�n el proceso de eliminar parcela, para descargar el
				 * handle de ifs
				 */
				deleteDistCoste(poliza, codModulo, fila);
				/* MODIF TAM (28.10.2021) ** SONAR Q ** Fin */
			} else {

				/* MODIF TAM (28.10.2021) ** SONAR Q ** Inicio */
				/*
				 * Sacamos a otra funci�n el proceso de eliminar parcela, para descargar el
				 * handle de ifs
				 */
				deleteDistCoste2015(poliza, codModulo, fila, idComparativa);
				/* MODIF TAM (28.10.2021) ** SONAR Q ** Fin */
			}
		} catch (Exception exception) {
			// Void
		}
	}

	@SuppressWarnings("unchecked")
	public final Long getLineaseguroId(final BigDecimal codplan, final BigDecimal codlinea) {
		LineasFiltro filtroLinea = new LineasFiltro(codplan, codlinea);

		List<Linea> lineas = seleccionPolizaDao.getObjects(filtroLinea);

		if (!lineas.isEmpty())
			return lineas.get(0).getLineaseguroid();

		return null;
	}

	// TMR Facturacion.Le anadimos el usuario por que al borrar una poliza se
	// tiene que facturar
	public void borrarPoliza(Poliza poliza, Usuario usuario) throws BusinessException {
		try {
			seleccionPolizaDao.borrarPoliza(poliza, usuario);
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error en el borrado de la p�liza", e);
		}

	}

	public int getCountPolizas(Poliza polizaBean, BigDecimal[] estados, Usuario usuario, Date fechaEnvio,
			String tipoPago, ArrayList<BigDecimal> oficinas) {
		int fullListSize = 0;
		try {
			PolizaFiltro polizaFiltro = generatePolizaFiltro(polizaBean, estados, usuario.getListaCodEntidadesGrupo(),
					tipoPago, oficinas);
			fullListSize = seleccionPolizaDao.getCountPolizas(polizaFiltro);
		} catch (Exception e) {
			logger.error(ERROR_UTL, e);
		}

		return fullListSize;
	}

	/**
	 * DAA 18/05/2012 Selecciona todos los ids de las polizas segun el filtro de
	 * busqueda
	 * 
	 * @param polizaBusqueda
	 * @param estados
	 * @param usuario
	 * @param fechaenvio
	 * @return Cadena de texto con los identificadores de las pilizas
	 */
	public String getListPolizasString(Poliza polizaBusqueda, BigDecimal[] estados, Usuario usuario, String tipoPago) {
		String polizasString = "";
		try {

			PolizaFiltro polizaFiltro = generatePolizaFiltro(polizaBusqueda, estados,
					usuario.getListaCodEntidadesGrupo(), tipoPago, usuario.getListaCodOficinasGrupo());
			polizasString = seleccionPolizaDao.getIdsPolizas(polizaFiltro);

		} catch (DAOException dao) {
			logger.error(ERROR_LIST_TL, dao);
		} catch (Exception e) {
			logger.error(ERROR_UTL, e);
		}

		return polizasString;
	}

	/**
	 * DAA 25/07/2012 Metodo para actualizar el total de superficie para todas las
	 * polizas filtradas a la hora de imprimir el informe del listado de polizas
	 * 
	 * @param usuario
	 * @param bigDecimals
	 * @param polizaBusqueda
	 * @throws DAOException
	 */
	public void actualizaTotalSuperficie(Poliza polizaBusqueda, BigDecimal[] bigDecimals, Usuario usuario,
			String tipoPago) throws DAOException {

		String polizasString = getListPolizasString(polizaBusqueda, bigDecimals, usuario, tipoPago);
		int limite = 999;
		String listaFinal = "";
		String[] stringIds = polizasString.split(";");
		String[] aux = new String[limite];
		int j = 0;

		for (int i = 0; i < stringIds.length; i++) {

			aux[j] = stringIds[i];
			listaFinal += aux[j] + ",";
			j++;
			if (j == limite || i == stringIds.length - 1) {
				listaFinal = listaFinal.substring(0, (listaFinal.length() - 1));
				logger.debug("actualizaTotalSuperficie - stringIds=" + listaFinal);
				seleccionPolizaDao.actualizaTotSuperficie(listaFinal);
				aux = new String[limite];
				j = 0;
				listaFinal = "";
			}
		}
		return;
	}

	// TMR Facturacion
	/**
	 * Guarda un objeto poliza en la BD y factura.
	 * 
	 * @param polizaBean
	 */
	public Long savePolizaFacturacion(final Poliza polizaBean, Usuario usuario) {

		Poliza poliza = null;
		try {
			poliza = (Poliza) seleccionPolizaDao.saveOrUpdateFacturacion(polizaBean, usuario);
			return poliza.getIdpoliza();

		} catch (DAOException e) {
			logger.error("error al guardar la poliza y facturar", e);
		}
		return null;
	}

	/**
	 * Metodo para cambiar el usuario de las polizas
	 * 
	 * @param arrayCheck
	 *            Identificadores de las polizas a cambiar
	 * @param codUsuario
	 *            Usuario que se asignara a las polizas
	 * @param usuario
	 *            Usuario que realiza la operacion
	 * @return True en caso de error, false en caso contrario
	 */
	public Map<String, Object> cambiarUsuario(String[] arrayCheck, String codUsuario, Usuario usuario) {

		Map<String, Object> parameters = new HashMap<String, Object>();
		boolean cambiaUsu = false;
		List<Poliza> polizasUsu = new ArrayList<Poliza>();
		Usuario usuarioNuevo = this.getUsuario(codUsuario);
		if (usuarioNuevo != null) {
			for (String idPol : arrayCheck) {
				if (!idPol.equals("")) {
					Poliza polizaBuscada = this.getPolizaById(new Long(idPol));
					if (polizaBuscada != null) {

						/** MODIF TAM (28.10.2021) ** SONAR Q ** Inicio **/
						/**
						 * Sacamos a otra funci�n el proceso de eliminar parcela, para descargar el
						 * handle de ifs/for
						 */
						cambiaUsu = obtenercambiaUsu(usuario, usuarioNuevo);
						if (!cambiaUsu) {
							break;
						}
						parameters = obtenerParametros(usuario, usuarioNuevo);
						/** MODIF TAM (28.10.2021) ** SONAR Q ** Fin **/

						if (cambiaUsu) {
							Usuario usuarioNuevo2 = new Usuario();
							usuarioNuevo2.setCodusuario(codUsuario);
							polizaBuscada.setUsuario(usuarioNuevo2);

							polizasUsu.add(polizaBuscada);
						}
					}
				}
			}

			if (cambiaUsu) {
				for (int i = 0; i < polizasUsu.size(); i++) {
					Poliza p = polizasUsu.get(i);
					// TMR FActuracion. por cada poliza que cambiamos el usuario, facturamos
					Long idpoliza = this.savePolizaFacturacion(p, usuario);

					if (idpoliza == null)
						parameters.put("alerta2", bundle.getString("mensaje.cambioUsuario.KO"));
				}
			}
		} else {
			parameters.put(ALERT, bundle.getString("cambioUsuario.noExisteUsuario"));
		}
		return parameters;
	}

	/**
	 * metodo que llama al pl de facturacion.
	 * 
	 * @param usuario
	 * @throws DAOException
	 */
	public void callFacturacion(Usuario usuario, String tipo) throws DAOException {
		try {
			polizaDao.callFacturacion(usuario, tipo);
		} catch (DAOException e) {
			logger.error("error al facturar en la carga de polizas del aio anterior o PAC", e);
		}

	}

	/* -------------------------------------------------------- */
	/* DISPLAYTAG PAGINATION */
	/* -------------------------------------------------------- */
	public PaginatedListImpl<Poliza> getPaginatedListPolizasButEstadosGrupoEnt(Poliza polizaBean, BigDecimal[] estados,
			Usuario usuario, int numPageRequest, String sort, String dir, String tipoPago) {
		PaginatedListImpl<Poliza> paginatedListImpl = null;

		int pageSize = (int) Long.parseLong(ResourceBundle.getBundle("displaytag").getString("numElementsPag"));
		logger.info("SeleccionPolizaManager.getPaginatedListPolizasButEstadosGrupoEnt (pageSize) 916" + pageSize);
		try {

			PolizaFiltro polizaFiltro = generatePolizaFiltro(polizaBean, estados, usuario.getListaCodEntidadesGrupo(),
					tipoPago, usuario.getListaCodOficinasGrupo());
			int fullListSize = seleccionPolizaDao.getCountPolizas(polizaFiltro);

			PageProperties pageProperties = new PageProperties();
			pageProperties.setFullListSize(fullListSize);
			pageProperties.setIndexRowMax((numPageRequest - 1) * pageSize + pageSize - 1);
			pageProperties.setIndexRowMin((numPageRequest - 1) * pageSize);
			pageProperties.setPageNumber(numPageRequest);
			pageProperties.setPageSize(pageSize);
			pageProperties.setDir(dir);
			pageProperties.setSort(sort);
			logger.info("SeleccionPolizaManager.getPaginatedListPolizasButEstadosGrupoEnt (pageSize) 931" + pageSize);
			paginatedListImpl = seleccionPolizaDao.getPaginatedListPolizasButEstadosGrupoEnt(polizaBean, pageProperties,
					polizaFiltro, usuario);

		} catch (DAOException dao) {
			logger.error(ERROR_LIST_TL, dao);
		} catch (Exception e) {
			logger.error(ERROR_UTL, e);
		}
		logger.info("FIN SeleccionPolizaManager.getPaginatedListPolizasButEstadosGrupoEnt (pageSize) 931");
		return paginatedListImpl;
	}

	private PolizaFiltro generatePolizaFiltro(Poliza polizaBean, BigDecimal[] estados, List<BigDecimal> entidades,
			String valorPago, List<BigDecimal> oficinas) {

		PolizaFiltro polizaFilter = null;

		try {
			BigDecimal[] listaEnt = new BigDecimal[entidades.size()];

			for (int i = 0; i < entidades.size(); i++)
				listaEnt[i] = entidades.get(i);

			Map<String, Object> mapaPoliza = seleccionPolizaDao.getMapaPoliza(polizaBean);

			polizaFilter = new PolizaFiltro();
			polizaFilter.setPolizaBean(polizaBean);
			polizaFilter.setEstados(estados);
			polizaFilter.setListaEnt(listaEnt);
			// a�adimos las oficinas
			if (null != oficinas && oficinas.size() > 0) {
				polizaFilter.setListaOfi(oficinas);
			}
			// polizaFilter.setPerfil(perfil);
			polizaFilter.setMapaPoliza(mapaPoliza);
			if (valorPago != null && !valorPago.equals("")) {
				if (valorPago.equals("1")) {
					polizaFilter.setTipoPago(new BigDecimal(1));
				} else if (valorPago.equals("2")) {
					polizaFilter.setTipoPago(new BigDecimal(2));
				} else {
					polizaFilter.setTipoPago(new BigDecimal(0));
				}
			}

		} catch (DAOException dao) {
			logger.error("Excepcion : SeleccionPolizaManager - generatePolizaFiltro", dao);
		}

		return polizaFilter;
	}

	/* -------------- fin displaytag pagination ----------------- */

	public List<Poliza> getPolizasButEstadosGrupoEnt(Poliza polizaBean, BigDecimal[] estados, Usuario usuario,
			int numPageRequest, String sort, String dir, String tipoPago, List<BigDecimal> oficinas) {
		List<Poliza> lstPolizas = null;

		try {
			PolizaFiltro polizaFiltro = generatePolizaFiltro(polizaBean, estados, usuario.getListaCodEntidadesGrupo(),
					tipoPago, oficinas);

			lstPolizas = seleccionPolizaDao.getPolizasButEstadosGrupoEnt(polizaBean, polizaFiltro);

		} catch (DAOException dao) {
			logger.error(ERROR_LIST_TL, dao);
		} catch (Exception e) {
			logger.error(ERROR_UTL, e);
		}

		return lstPolizas;
	}

	/**
	 * Metodo para liberar una referencia asignada a una poliza. Se utilizara cuando
	 * se borren polizas que tengan la referencia asignada.
	 * 
	 * @param referencia
	 *            Referencia a liberar.
	 * @throws DAOException
	 */
	public void liberarReferencia(String referencia) throws DAOException {
		logger.debug("inicio liberarReferencia " + referencia);
		try {
			ReferenciaAgricola ref = (ReferenciaAgricola) seleccionPolizaDao
					.getObjects(ReferenciaAgricola.class, "referencia", referencia).get(0);
			ref.setFechaenvio(null);
			seleccionPolizaDao.saveOrUpdate(ref);
		} catch (DAOException e) {
			logger.error("Error al liberar la referencia " + referencia, e);
			throw e;
		} catch (Exception e) {
			logger.error("Error inesperado al liberar la referencia " + referencia, e);
			throw new DAOException("Error inesperado al liberar la referencia " + referencia);
		}
		logger.debug("fin liberarReferencia");
	}

	/**
	 * Metodo que devuelve los datos de un usuario
	 * 
	 * @param codUsuario
	 * @return Usuario
	 */
	public Usuario getUsuario(String codUsuario) {
		logger.debug("inicio getUsuario");
		try {
			Usuario usuario = (Usuario) seleccionPolizaDao.getObject(Usuario.class, codUsuario);
			return usuario;

		} catch (Exception e) {
			logger.error("Se ha producido un erro al recuperar los datos del Usuario", e);
		}
		logger.debug("fin getUsuario");
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Parcela> getListInstalaciones(Long idParcela) {
		logger.debug("inicio getListInstalaciones");
		List<Parcela> listInstalaciones = new ArrayList<Parcela>();

		try {
			listInstalaciones = seleccionPolizaDao.getObjects(Parcela.class, "idparcelaestructura", idParcela);

		} catch (Exception e) {
			logger.error("Se ha producido un erro al recuperar el tipo de la parcela", e);
		}

		logger.debug("fin getListInstalaciones");
		return listInstalaciones;
	}

	public BigDecimal getEstadoPoliza(Long idPoliza) {
		BigDecimal codEstado = null;
		Poliza poliza = (Poliza) seleccionPolizaDao.getObject(Poliza.class, idPoliza);

		if (poliza != null)
			codEstado = poliza.getEstadoPoliza().getIdestado();

		return codEstado;
	}

	public es.agroseguro.acuseRecibo.Error[] getFicheroContenido(BigDecimal idEnvio, String refPoliza, BigDecimal linea,
			BigDecimal plan) throws BusinessException {

		logger.debug("init - getFicheroContenido");
		AcuseReciboDocument acuseRecibo = null;
		es.agroseguro.acuseRecibo.Error[] errores = null;
		Comunicaciones comunicaciones = null;

		// Se monta la referencia que luego se comparara
		String referencia = refPoliza.toString() + "" + plan.toString() + "" + linea.toString();
		logger.debug("Referencia a comparar :  " + referencia);
		try {

			comunicaciones = seleccionPolizaDao.getComunicaciones(idEnvio);

			if (comunicaciones == null) {
				errores = new es.agroseguro.acuseRecibo.Error[0];
			} else {
				Clob fichero = comunicaciones.getFicheroContenido();

				errores = obtenerError(referencia, fichero, acuseRecibo);

			}

		} catch (DAOException dao) {
			throw new BusinessException(
					"Se ha producido un error al recuperar el fichero_contenido de una Reduccion de Capital", dao);
		}
		logger.debug("fin - getFicheroContenido");
		return errores;
	}

	public int verPlazosPoliza(Poliza polizaBean) throws DAOException {
		int resFecha = 0;
		resFecha = polizaDao.validarFecha(polizaBean);
		return resFecha;
	}

	/**
	 * Devuelve true si existen polizas con el mismo lineaseguroid, asegurado y
	 * distinta clase
	 * 
	 * @return boolean
	 */
	public boolean existenPolizasDistClase(Poliza polizaBean) throws BusinessException {
		logger.debug("init - existenPolizasDistClase");
		boolean hayMultiPoliza = false;
		try {
			List<Poliza> lstPolizas = this.getListaPolizas(polizaBean);
			if (lstPolizas.size() > 0) {
				hayMultiPoliza = true;
			}
		} catch (Exception ex) {
			logger.error("Error al buscar la piliza de distinta clase", ex);
			throw new BusinessException("[ERROR] al obtener los datos  - metodo existenPolizasDistClase]", ex);
		}
		logger.debug("init - existenPolizasDistClase");
		return hayMultiPoliza;
	}

	/**
	 * Devuelve un listado de polizas con el mismo lineaseguroid, asegurado y
	 * distinta clase, q tengan al menos una parcela
	 */
	public List<Poliza> getListaPolizas(Poliza polizaBean) throws BusinessException {
		List<Poliza> lstPolizas = new ArrayList<Poliza>();
		List<Poliza> lstPolizasSinFiltrar = new ArrayList<Poliza>();
		try {
			lstPolizasSinFiltrar = (List<Poliza>) seleccionPolizaDao.getVerificarPolizas(polizaBean);
			// filtramos de la lista de polizas aquellas que tengan al menos una parcela
			for (Poliza p : lstPolizasSinFiltrar) {
				if (p.getParcelas().size() > 0) {
					lstPolizas.add(p);
				}
			}
		} catch (DAOException ex) {
			logger.error("Error al obtener la lista de pilizas", ex);
			throw new BusinessException("[ERROR] al obtener los datos  - metodo getListaParcelas]", ex);
		}
		return lstPolizas;
	}

	/**
	 * Replicamos las parcelas de una poliza a otra
	 */
	public void guardarParcelasPoliza(Poliza polizaBean, Poliza polizaParaCopiar) throws BusinessException {

		/* [inicio]Miguel 1-2-2012 */

		logger.debug("init - guardarParcelasPoliza");
		List<ClaseDetalle> lstClaseDetalle = new ArrayList<ClaseDetalle>();
		Map<Long, List<BigDecimal>> maxPrecioProduccion;
		boolean copiar = false;

		try {

			maxPrecioProduccion = getPrecioProduccMax(polizaParaCopiar);

			long lineaseg = polizaBean.getLinea().getLineaseguroid();
			BigDecimal claseBean = polizaBean.getClase();
			lstClaseDetalle = seleccionPolizaDao.getClaseDetalle(lineaseg, claseBean);

			// PARCELAS
			for (Parcela par : polizaParaCopiar.getParcelas()) {
				// Copiamos solo las parcelas que tienen mismo cultivo,
				// variedad,provincia,comarca,termino y subtermino que la clase
				// de la polizaBean
				copiar = false;
				copiar = checkCumpleParcelasClase(par, lstClaseDetalle);

				if (copiar) {

					/**** SONAR Q ** MODIF TAM (28.10.2021) ** Inicio ***/
					/** Lo sacamos a otra funcion para descargar de ifs/for el metodo */
					copiarParcela(par, polizaBean, lstClaseDetalle, maxPrecioProduccion);
					/**** SONAR Q ** MODIF TAM (28.10.2021) ** Fin ***/
				}
			}
		} catch (Exception ex) {
			logger.error("Se ha producido un error durante el acceso a la base de datos " + ex.getMessage());
			throw new BusinessException("[ERROR] en SeleccionPolizaManager- metodo guardarParcelasPoliza.", ex);
		}

		logger.debug("Fin - guardarParcelasPoliza");

		/* [fin]Miguel 1-2-2012 */
	}

	// TMR Facturacion. Le pasamos el usuario para la facturacion
	public Set<Long> cambioMasivo(CambioMasivoVO cambioMasivoVO, Poliza poliza, Usuario usuario,
			boolean recalcularRendimientoConSW, String recalcular, List<String> lstCadenasIds,
			boolean guardaSoloPrecioYProd)
			throws BusinessException, SQLIntegrityConstraintViolationException, TransactionSystemException {
		logger.debug("init - cambioMasivo");

		Set<Long> colIdParcelasParaRecalculo = new HashSet<Long>();
		try {
			Map<Long, List<Long>> mapaParcelasInstalaciones = seleccionPolizaDao
					.getMapaParcelasInstalaciones(lstCadenasIds);

			// ESC-11147 DNF 23/10/2020
			// seteo el valor tipoRdto a cero para que no pinte el valor de azul en pantalla
			// despues de aplicar el cambio masivo
			List<String> ids = new ArrayList<String>();
			for (String idString : lstCadenasIds) {
				ids.addAll(Arrays.asList(idString.split(",")));
			}

			for (Parcela p : poliza.getParcelas()) {
				if (ids.contains(p.getIdparcela().toString())) {
					for (CapitalAsegurado ca : p.getCapitalAsegurados()) {
						for (CapAsegRelModulo carm : ca.getCapAsegRelModulos()) {
							carm.setTipoRdto(Constants.TIPO_RDTO_MAXIMO);
						}
					}
				}
			}
			// FIN ESC-11147 DNF 23/10/2020

			/* MODIF TAM (28.10.2021) ** SONAR Q ** Inicio */
			/*
			 * Sacamos a otra funci�n el proceso de eliminar parcela, para descargar el
			 * handle de ifs
			 */
			colIdParcelasParaRecalculo = execCambioMasivo(cambioMasivoVO, poliza, usuario, recalcular,
					mapaParcelasInstalaciones, guardaSoloPrecioYProd, recalcularRendimientoConSW);
			/* MODIF TAM (28.10.2021) ** SONAR Q ** Inicio */

		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			logger.error("Error de integridad ", e);
			throw new SQLIntegrityConstraintViolationException(
					"Error de integridad al actualizar los datos del cambio masivo", e);
		} catch (TransactionSystemException t) {
			logger.error("Clave principal no encontrada ", t);
			throw new TransactionSystemException("Clave pricipal no encontrada");
		} catch (Exception ex) {
			logger.error("Se ha producido un error durante el cambio masivo", ex);
			throw new BusinessException("Se ha producido un error durante el cambio masivo", ex);
		}
		logger.debug("Fin - cambioMasivo");
		return colIdParcelasParaRecalculo;

	}

	/**
	 * Silo guarda cambios, pero no recalcula precio/producciin
	 * 
	 * @param cambioMasivoVO
	 * @param poliza
	 * @param usuario
	 * @return Lista de parcelas a las que hay que recalcular el precio/produccion
	 * @throws DAOException
	 * @throws SQLIntegrityConstraintViolationException
	 */
	private Set<Long> cambioMasivo(CambioMasivoVO cambioMasivoVO, Poliza poliza, Usuario usuario, String recalcular,
			Map<Long, List<Long>> mapaParcelasInstalaciones, boolean guardaSoloPrecioYProd)
			throws DAOException, SQLIntegrityConstraintViolationException, TransactionSystemException {

		Set<Long> colIdParcelasParaRecalculo = new HashSet<Long>();
		int i = 0;
		boolean tieneEstructuras = false;

		try {
			// modifico todas las parcelas checked (y sus instalaciones)

			// Compruebo si las parcelas marcadas tienen instalaciones.
			if (mapaParcelasInstalaciones != null && mapaParcelasInstalaciones.size() > 0)
				tieneEstructuras = true;

			/* MODIF TAM (28.10.2021) ** SONAR Q ** Inicio */
			/*
			 * Sacamos a otra funci�n el proceso de eliminar parcela, para descargar el
			 * handle de ifs
			 */
			colIdParcelasParaRecalculo = obtenerParcelas(poliza, cambioMasivoVO, i, tieneEstructuras,
					mapaParcelasInstalaciones, usuario, guardaSoloPrecioYProd, recalcular);
			/* MODIF TAM (28.10.2021) ** SONAR Q ** Fin */

		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			logger.error("Error de integridad ", e);
			throw new SQLIntegrityConstraintViolationException(
					"Error de integridad al actualizar los datos del cambio masivo", e);
		} catch (TransactionSystemException t) {
			logger.error("Clave principal no encontrada ", t);
			throw new TransactionSystemException("Clave pricipal no encontrada");
		} catch (Exception ex) {
			logger.error("Se ha producido un error durante el acceso a la base de datos ", ex);
			throw new DAOException("[ERROR] al acceder a la BBDD.", ex);
		}
		return colIdParcelasParaRecalculo;
	}

	@SuppressWarnings("unused")
	private List<Parcela> getListInstalaciones(Collection<Parcela> parcelas, Long idParcela) {
		logger.debug("init - [SeleccionPolizaDao] getListInstalaciones");
		List<Parcela> lstInstalaciones = new ArrayList<Parcela>();

		for (Parcela parcela : parcelas) {
			if (parcela.getIdparcelaestructura() != null && parcela.getIdparcelaestructura().equals(idParcela)) {
				lstInstalaciones.add(parcela);
			}
		}

		return lstInstalaciones;
	}

	public void duplicadoMasivo(String idParcela, Poliza poliza, Long cantDuplicar) throws BusinessException {
		logger.debug("init - duplicadoMasivo");
		try {
			seleccionPolizaDao.duplicadoMasivo(idParcela, poliza, cantDuplicar);
			marcarRecalculoHojaYNum(poliza.getIdpoliza());
		} catch (Exception ex) {
			logger.error("Se ha producido un error durante el duplicado masivo", ex);
			throw new BusinessException("[ERROR] en SeleccionPolizaManager - metodo duplicadoMasivo.", ex);
		}
		logger.debug("Fin - duplicadoMasivo");

	}

	/**
	 * verificamos si la parcela tiene mismo
	 * cultivo,variedad,provincia,comarca,termino y subtermino que la clase
	 * 
	 * @param par
	 * @param lstClaseDetalles
	 * @return boolean
	 */
	public boolean checkCumpleParcelasClase(Parcela par, List<ClaseDetalle> lstClaseDetalles) {
		boolean copiar = false;
		for (ClaseDetalle cl : lstClaseDetalles) {
			if ((par.getCodcultivo().toString().equals(cl.getVariedad().getId().getCodcultivo().toString())
					|| "999".equals(cl.getVariedad().getId().getCodcultivo().toString()))
					&& (par.getCodvariedad().toString().equals(cl.getVariedad().getId().getCodvariedad().toString())
							|| "999".equals(cl.getVariedad().getId().getCodvariedad().toString()))
					&& (par.getTermino().getId().getCodprovincia().toString().equals(cl.getCodprovincia().toString())
							|| "99".equals(cl.getCodprovincia().toString()))
					&& (par.getTermino().getId().getCodcomarca().toString().equals(cl.getCodcomarca().toString())
							|| "99".equals(cl.getCodcomarca().toString()))
					&& (par.getTermino().getId().getCodtermino().toString().equals(cl.getCodtermino().toString())
							|| "999".equals(cl.getCodtermino().toString()))
					&& (par.getTermino().getId().getSubtermino().toString().equals(cl.getSubtermino().toString())
							|| "9".equals(cl.getSubtermino().toString()))) {
				copiar = true;
				break;
			}
		}
		return copiar;
	}

	/**
	 * Calculamos el precio y produccion de las parcelas incluidas en el parametro
	 * 
	 * @param par
	 */
	public void recalculoPrecioProduccion(Collection<Parcela> parcelas, List<String> codsModuloPoliza, boolean esConWS,
			Map<String, ProduccionVO> mapaRendimientosProd) throws Exception {
		seleccionPolizaDao.reCalculoPrecioProduccion(parcelas, codsModuloPoliza, esConWS, mapaRendimientosProd);
	}

	public void recalculoPrecioProduccion(Collection<Parcela> parcelas, List<String> codsModuloPoliza)
			throws Exception {
		seleccionPolizaDao.reCalculoPrecioProduccion(parcelas, codsModuloPoliza, false, null);
	}

	public String calcularIntervaloCoefReduccionRdtoPoliza(Usuario usuario, Long idClase) throws BusinessException {

		BigDecimal intervaloCoefReduccionRdtoPoliza[] = new BigDecimal[2];

		try {
			Long lineaseguroid = usuario.getColectivo().getLinea().getLineaseguroid();
			String nifcifAsegurado = usuario.getAsegurado().getNifcif();
			List<String> lstCodmodulos = seleccionPolizaDao.getListCodModulosClase(idClase);
			List<BigDecimal> lstCodCultivos = seleccionPolizaDao.getListCodCultivosClase(idClase);
			List<BigDecimal> lstCodVariedades = seleccionPolizaDao.getListCodVariedadesClase(idClase);
			boolean cultivo999aniadido = false;
			boolean variedad999aniadida = false;

			intervaloCoefReduccionRdtoPoliza = getIntervaloCoefReduccionRdtoPoliza("coefsobrerdtos", lineaseguroid,
					nifcifAsegurado, lstCodmodulos, lstCodCultivos, lstCodVariedades);

			if (intervaloCoefReduccionRdtoPoliza[0] == null) {

				// anadimos el cultivo 999 y la variedad 999 al filtro
				if (!lstCodCultivos.contains(new BigDecimal(999))) {
					lstCodCultivos.add(new BigDecimal(999));
					cultivo999aniadido = true;
				}
				if (!lstCodVariedades.contains(new BigDecimal(999))) {
					lstCodVariedades.add(new BigDecimal(999));
					variedad999aniadida = true;
				}
				if (cultivo999aniadido || variedad999aniadida) {
					// solo si se han anadido nuevos valores volvemos a lanzar
					// la consulta
					intervaloCoefReduccionRdtoPoliza = getIntervaloCoefReduccionRdtoPoliza("coefsobrerdtos",
							lineaseguroid, nifcifAsegurado, lstCodmodulos, lstCodCultivos, lstCodVariedades);
				}
			}

			if (intervaloCoefReduccionRdtoPoliza[0] == null) {
				// eliminamos el cultivo 999 y la variedad 999 del filtro si lo
				// tiene y buscamos por rendimiento permitodo
				if (cultivo999aniadido && lstCodCultivos.contains(new BigDecimal(999))) {
					lstCodCultivos.remove(new BigDecimal(999));
				}
				if (variedad999aniadida && lstCodVariedades.contains(new BigDecimal(999))) {
					lstCodVariedades.remove(new BigDecimal(999));
				}
				intervaloCoefReduccionRdtoPoliza = getIntervaloCoefReduccionRdtoPoliza("rdtopermitido", lineaseguroid,
						nifcifAsegurado, lstCodmodulos, lstCodCultivos, lstCodVariedades);
			}

			if (intervaloCoefReduccionRdtoPoliza[0] == null) {
				// anadimos el cultivo 999 y la variedad 999 al filtro y
				// buscamos por rendimiento permitido
				if (!lstCodCultivos.contains(new BigDecimal(999))) {
					lstCodCultivos.add(new BigDecimal(999));
					cultivo999aniadido = true;
				}
				if (!lstCodVariedades.contains(new BigDecimal(999))) {
					lstCodVariedades.add(new BigDecimal(999));
					variedad999aniadida = true;
				}
				if (cultivo999aniadido || variedad999aniadida) {
					// solo si se han anadido nuevos valores volvemos a lanzar
					// la consulta
					intervaloCoefReduccionRdtoPoliza = getIntervaloCoefReduccionRdtoPoliza("rdtopermitido",
							lineaseguroid, nifcifAsegurado, lstCodmodulos, lstCodCultivos, lstCodVariedades);
				}
			}

			String intervaloCoefReduccionRdtoStr = "---";

			if (intervaloCoefReduccionRdtoPoliza[0] != null) {
				intervaloCoefReduccionRdtoStr = intervaloCoefReduccionRdtoPoliza[0]
						.equals(intervaloCoefReduccionRdtoPoliza[1]) ? intervaloCoefReduccionRdtoPoliza[0].toString()
								: "(" + intervaloCoefReduccionRdtoPoliza[0] + " - "
										+ intervaloCoefReduccionRdtoPoliza[1] + ")";
			}

			return intervaloCoefReduccionRdtoStr;

		} catch (Exception ex) {

			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", ex);

		}

	}

	private BigDecimal[] getIntervaloCoefReduccionRdtoPoliza(String tipo, Long lineaseguroid, String nifcifAsegurado,
			List<String> lstCodModulos, List<BigDecimal> lstCodCultivos, List<BigDecimal> lstCodVariedades)
			throws BusinessException {

		BigDecimal intervaloCoefReduccionRdtoPoliza[] = new BigDecimal[2];

		try {
			BigDecimal intervaloCoefReduccionRdtoModulo[] = aseguradoDao.obtenerIntervaloCoefReduccionRdtos(tipo,
					lineaseguroid, nifcifAsegurado, lstCodModulos, lstCodCultivos, lstCodVariedades);

			// Si no se han obtenido resultados se hace la misma consulta pero
			// para un nifcif Asegurado nulo
			if (intervaloCoefReduccionRdtoModulo[0] == null) {
				intervaloCoefReduccionRdtoModulo = aseguradoDao.obtenerIntervaloCoefReduccionRdtos(tipo, lineaseguroid,
						null, lstCodModulos, lstCodCultivos, lstCodVariedades);
			}

			if (intervaloCoefReduccionRdtoPoliza[0] != null && intervaloCoefReduccionRdtoModulo[0] != null) {
				intervaloCoefReduccionRdtoPoliza[0] = intervaloCoefReduccionRdtoPoliza[0]
						.min(intervaloCoefReduccionRdtoModulo[0]);
			} else if (intervaloCoefReduccionRdtoModulo[0] != null) {
				intervaloCoefReduccionRdtoPoliza[0] = intervaloCoefReduccionRdtoModulo[0];
			}

			if (intervaloCoefReduccionRdtoPoliza[1] != null && intervaloCoefReduccionRdtoModulo[1] != null) {
				intervaloCoefReduccionRdtoPoliza[1] = intervaloCoefReduccionRdtoPoliza[1]
						.min(intervaloCoefReduccionRdtoModulo[1]);
			} else if (intervaloCoefReduccionRdtoModulo[1] != null) {
				intervaloCoefReduccionRdtoPoliza[1] = intervaloCoefReduccionRdtoModulo[1];
			}

		} catch (Exception ex) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", ex);
		}

		return intervaloCoefReduccionRdtoPoliza;
	}

	public String getDescripcionEnesa(BigDecimal tipo) {
		String dev = "";
		try {
			TipoSubvencionEnesa tipoEnesa = (TipoSubvencionEnesa) polizaDao.getObject(TipoSubvencionEnesa.class, tipo);
			if (tipoEnesa != null)
				dev = tipoEnesa.getDestiposubvenesa();
		} catch (Exception e) {
			logger.error("Error al obtener la descripcion de la subvencion ENESA " + tipo, e);
		}
		return dev;
	}

	public String getCCAA(Character codOrganismo) {
		String desc = "";
		try {

			Organismo organismo = (Organismo) polizaDao.getObject(Organismo.class, codOrganismo);
			if (organismo != null) {
				desc = organismo.getDesorganismo();
			}
		} catch (Exception e) {
			logger.error("Error al obtener la descripcion de la subvencion CCAA ", e);
		}
		return desc;
	}

	/**
	 * Metodo que llama al PL Check_aseg_autorizados
	 * 
	 * @param id
	 * @throws BusinessException
	 */
	public void check_aseg_autorizados(Long lineaseguroid, String cifnifAseg) throws BusinessException {
		logger.debug("init - check_aseg_autorizados");
		Map<String, Object> parametros = new HashMap<String, Object>();

		String procedure = "pq_check_aseg_autorizados.PR_CHECK_ASEG_AUTORIZADOS(P_LINEASEGUROID IN NUMBER, P_NIFCIF IN VARCHAR2)";
		parametros.put("P_NIFCIF", cifnifAseg);
		parametros.put("P_LINEASEGUROID", lineaseguroid);

		logger.debug(
				"Llamada al procedimiento pq_check_aseg_autorizados.PR_CHECK_ASEG_AUTORIZADOS con los siguientes parametros: ");
		logger.debug(" NIFCIF: " + cifnifAseg);
		logger.debug(" LINEASEGUROID: " + lineaseguroid);

		databaseManager.executeStoreProc(procedure, parametros);

		logger.debug("end - validarFicheroComisiones");
	}

	/**
	 * Indica si tenemos alguna poliza con modulos compatibles y aun no tenemos
	 * tantas polizas como como modulos compatibles disponibles.
	 * 
	 * @param listaPolizas
	 *            La lista de polizas del usuario.
	 */
	public boolean isPermiteAltaPorModulosCompatibles(List<Poliza> listaPolizas) {
		Iterator<Poliza> itListaPolizas = listaPolizas.iterator();

		boolean poseeProvisionalODefinitivo = false;
		boolean poseeEnviada = false;
		while (itListaPolizas.hasNext()) {
			Poliza poliza = (Poliza) itListaPolizas.next();
			/* MODIF TAM (28.10.2021) ** SONAR Q ** Inicio */
			/*
			 * Sacamos a otra funci�n el proceso de eliminar parcela, para descargar el
			 * handle de ifs
			 */
			poseeProvisionalODefinitivo = validarPoseeProvisionalODefinitivo(poliza);
			poseeEnviada = validarPoseeEnv(poliza);

			/* MODIF TAM (28.10.2021) ** SONAR Q ** Inicio */
		}

		// Si habiendo una poliza en estado enviada y ninguna en estado
		// provisional
		// o definitivo permite continuar las comprobaciones
		if (!poseeEnviada || poseeProvisionalODefinitivo)
			return false;

		// Miramos si cumple la condicion de poseer modulos compatibles y no
		// hemos superado el cupo.
		itListaPolizas = listaPolizas.iterator();

		/* MODIF TAM (28.10.2021) ** SONAR Q ** Inicio */
		/* Sacamos a otra funci�n par descargar de ifs/fors */
		boolean resultado = obtenerResult(itListaPolizas, listaPolizas);

		if (resultado) {
			return true;
		}
		/* MODIF TAM (28.10.2021) ** SONAR Q ** Inicio */

		return false;
	}

	/**
	 * Si algun capital asegurado no tiene precio produccion para uno de los modulos
	 * de la poliza le doy precio produccion asignandole el mayor.
	 * 
	 * @param idPoliza
	 */
	public void setProduccionModulosNuevos(Poliza polizaOrigen) {
		Poliza poliza = (Poliza) polizaDao.getObject(Poliza.class, polizaOrigen.getIdpoliza());
		int numModulos = poliza.getModuloPolizas().size();

		Map<Long, List<BigDecimal>> mapProdPrec = getPrecioProduccMax(poliza);

		// recorro las parcelas
		for (Parcela parcela : poliza.getParcelas()) {
			// recorro los capitales asegurados
			for (CapitalAsegurado ca : parcela.getCapitalAsegurados()) {
				// si al capital asegurado le falta algun CapAsegRelModulo
				if (ca.getCapAsegRelModulos().size() < numModulos) {

					/* MODIF TAM (28.10.2021) ** SONAR Q ** Inicio */
					/*
					 * Sacamos a otra funci�n el proceso de eliminar parcela, para descargar el
					 * handle de ifs
					 */
					guardarCapAsegRelModulo(poliza, ca, mapProdPrec);
					/* MODIF TAM (28.10.2021) ** SONAR Q ** Fin */
				}
			}
		}

	}

	/**
	 * @Metodo que en funcion de la linea y cultivos de la poliza muestra o no el
	 *         boton sobreprecio. Solo se mostrara para la linea 309 (de momento)
	 * @param Poliza
	 *            poliza
	 * @return boolean muestraBtnSbp
	 * @throws DAOException
	 */

	public boolean addBotonSbp(Poliza poliza, String perfil) throws DAOException {
		// List<Sobreprecio> lineas = new ArrayList<Sobreprecio>();
		boolean mostrarBtnSbp = false;
		PolizaSbp polSbp = new PolizaSbp();
		polSbp.setPolizaPpal(poliza);
		
		/* P00063473 ** MODIF TAM (26/11/2021) **/
		/* Los usuarios de perfil 4, en ning�n caso tendr�n visible el bot�n de Sobreprecios */
		if(Constants.PERFIL_USUARIO_OTROS.equals(perfil) ){ 
			mostrarBtnSbp = false;
			logger.debug(" Perfil 4 - No mostrar bot�n- mostrarBtnSbp: " + mostrarBtnSbp);
			return mostrarBtnSbp;
		}

		// ---------------------------------------------------------------------------
		// -- Busqueda de las cultivos por lineaseguroid que cumplen el sobreprecio --
		// ---------------------------------------------------------------------------
		Map<Long, List<BigDecimal>> cultivosPorLinea = new HashMap<Long, List<BigDecimal>>();
		boolean cumpleLinea = false;

		BigDecimal codPlan = poliza.getLinea().getCodplan();
		logger.debug(" maxPlan: " + codPlan);
		cultivosPorLinea = consultaSbpManager.getCultivosPorLineaseguroid(codPlan);
		logger.debug(" cultivosPorLinea: " + cultivosPorLinea.toString());
		// comprobamos si la linea de la poliza esta disponible para el sobreprecio
		if (cultivosPorLinea.containsKey(poliza.getLinea().getLineaseguroid())) {
			cumpleLinea = true;
			logger.debug(" cumpleLinea: " + poliza.getLinea().getLineaseguroid());
		}

		// comprobamos que el estado de la poliza sea valido
		if (cumpleLinea) {
			BigDecimal estado = poliza.getEstadoPoliza().getIdestado();
			if (estado.compareTo(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL) == 0
					|| estado.compareTo(Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA) == 0
					|| estado.compareTo(Constants.ESTADO_POLIZA_DEFINITIVA) == 0
					|| estado.compareTo(Constants.ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR) == 0) {
				mostrarBtnSbp = true;
			} else {
				mostrarBtnSbp = false;
			}
		}
		logger.debug(" mostrarBtnSbp: " + mostrarBtnSbp);
		return mostrarBtnSbp;
	}

	/**
	 * valida si un codmodulo tiene precio produccion.
	 * 
	 * @param ca
	 * @param codmodulo
	 * @return boolean
	 */
	private boolean hasCapAsegRelModulo(CapitalAsegurado ca, String codmodulo) {
		boolean result = false;

		for (CapAsegRelModulo carm : ca.getCapAsegRelModulos()) {
			if (carm.getCodmodulo().equals(codmodulo)) {
				result = true;
			}
		}

		return result;
	}

	/* [inicio]Miguel 1-2-2012 */
	/**
	 * Busca la maxima produccion y el maximo precio para cada capital asegurado
	 * 
	 * @param poliza
	 * @return Mapa: clave idcapitalasegurado, valor lista con dos valores
	 *         [max.precio (0), max.produccion (1)]
	 */
	private Map<Long, List<BigDecimal>> getPrecioProduccMax(Poliza poliza) {

		Map<Long, List<BigDecimal>> mapProdPrec = new HashMap<Long, List<BigDecimal>>();

		for (Parcela parcela : poliza.getParcelas()) {
			for (CapitalAsegurado ca : parcela.getCapitalAsegurados()) {
				List<BigDecimal> maxPrecioProduc = new ArrayList<BigDecimal>();
				BigDecimal precioMax = new BigDecimal(0);
				BigDecimal producMax = new BigDecimal(0);

				for (CapAsegRelModulo carm : ca.getCapAsegRelModulos()) {
					// precioMax menor que el nuevo
					if (precioMax.compareTo(carm.getPrecio()) == -1) {
						precioMax = carm.getPrecio();
					}

					// producMax menor que el nuevo
					if (producMax.compareTo(carm.getProduccion()) == -1) {
						producMax = carm.getProduccion();
					}
				}
				maxPrecioProduc.add(precioMax);
				maxPrecioProduc.add(producMax);

				mapProdPrec.put(ca.getIdcapitalasegurado(), maxPrecioProduc);
			}
		}

		return mapProdPrec;
	}

	/**
	 * Metodo para eliminar una parcela
	 * 
	 * @param idParcela
	 *            Identificador de la parcela a eliminar
	 * @param reasignar
	 *            Flag para indicar si se deben reasignar las instalaciones o no. Se
	 *            usa principalmente para el cambio masivo para poder reasingar
	 *            primero todas las instalaciones y luego hacer el borrado. En caso
	 *            de ser una unica parcela, siempre se le llamara con "true".
	 * @return true en caso de que haya ido bien.
	 * @throws DAOException
	 */
	public boolean borrarParcela(Long idPoliza, Long idParcela, boolean reasignar) throws DAOException {

		// 1. Llamamos al metodo que reasigna las instalaciones
		if (reasignar) {
			List<Long> listaIdsParcelas = new ArrayList<Long>();
			listaIdsParcelas.add(idParcela);

			this.reasignarInstalaciones(idPoliza, listaIdsParcelas);
		}

		// 2. Borrar la parcela/instalacion
		try {
			deleteParcela(idParcela);
		} catch (DAOException e) {
			throw new DAOException("Se ha producido un error al borrar la parcela", e);
		}

		return true;
	}

	private boolean borrarParcelas(Long polizaId, List<Long> listaParcelas, boolean reasignar) throws DAOException {

		// 2. Borrar la parcela/instalacion
		try {
			deleteParcelas(listaParcelas);
		} catch (DAOException e) {
			throw new DAOException("Se ha producido un error al borrar la parcela", e);
		}

		return true;
	}

	/**
	 * Metodo para reasignar las instalaciones asociadas a las parcelas indicadas
	 * 
	 * @param listaIdsParcelas
	 *            Lista de identificadores de parcleas a reasignar
	 * @throws DAOException
	 */
	private void reasignarInstalaciones(Long idPoliza, List<Long> listaIdsParcelas) throws DAOException {
		// 1. Comprobamos si hay instalaciones que no se vayan a eliminar y que
		// esten asociadas a parcelas que si se vayan a eliminar
		List<Long> listaInstalacionesReasignar = this.seleccionPolizaDao.getParcelasDeInstalaciones(idPoliza,
				listaIdsParcelas);

		// 2. Recorremos la lista de instalaciones y las reasignamos
		for (Long idInstalacion : listaInstalacionesReasignar) {
			Parcela instalacion = seleccionPolizaDao.getParcela(idInstalacion);

			// 2.1. Obtenemos las posibles parcelas a las que se puede reasignar
			// la instalacion
			List<Parcela> listaParcelas = seleccionPolizaDao.getParcelasMismoSigpac(instalacion,
					instalacion.getPoliza().getIdpoliza(), listaIdsParcelas);

			if (listaParcelas.size() > 0) {
				// 2.2. Si hay parcelas q cumplen las condiciones muevo la
				// instalacion
				Long idNuevaParcela = listaParcelas.get(0).getIdparcela();
				seleccionPolizaDao.actualizaInstalacion(idInstalacion, idNuevaParcela);
			} else {
				// si no hay => borro la instalacion
				try {
					deleteParcela(idInstalacion);
				} catch (Exception e) {
					throw new DAOException("Se ha producido un error al borrar la Instalacion " + idInstalacion, e);
				}
			}
		}
	}

	/**
	 * DAA 19/12/2012
	 * 
	 * @param cambioMasivoVO
	 * @param poliza
	 * @return
	 * @throws BusinessException
	 */
	public boolean borradoMasivo(CambioMasivoVO cambioMasivoVO, Poliza poliza) throws BusinessException {
		boolean correcto = true;
		GregorianCalendar fechaInicio = new GregorianCalendar();
		try {
			// Reasignamos las posibles instalaciones
			this.reasignarInstalaciones(cambioMasivoVO.getPolizaId(), cambioMasivoVO.getListaParcelas());
			GregorianCalendar fechaFinReasignar = new GregorianCalendar();
			Long tiempoReasignar = fechaFinReasignar.getTimeInMillis() - fechaInicio.getTimeInMillis();
			logger.debug("Tiempo de reasignar las instalaciones: " + tiempoReasignar);
			// Eliminamos las parcelas
			correcto = borrarParcelas(cambioMasivoVO.getPolizaId(), cambioMasivoVO.getListaParcelas(), false);

			GregorianCalendar fechaFinBorrar = new GregorianCalendar();
			Long tiempoBorrar = fechaFinBorrar.getTimeInMillis() - fechaInicio.getTimeInMillis();
			logger.debug("Tiempo de borrado de parcelas: " + tiempoBorrar);
		} catch (Exception ex) {
			correcto = false;
			logger.error("Se ha producido un error durante el borrado masivo", ex);
			throw new BusinessException("[ERROR] en SeleccionPolizaManager - metodo borradoMasivo.", ex);
		}
		GregorianCalendar fechaFin = new GregorianCalendar();
		Long tiempo = fechaFin.getTimeInMillis() - fechaInicio.getTimeInMillis();
		logger.debug("Tiempo total del borrado masivo de parcelas: " + tiempo);
		return correcto;
	}

	public boolean isOficinaPagoManual(BigDecimal oficina, BigDecimal codEntidad) throws Exception {

		try {

			return seleccionPolizaDao.isOficinaPagoManual(oficina, codEntidad);

		} catch (DAOException d) {
			logger.error("error al acceder a bbdd isOficinaPagoManual", d);
			throw d;
		} catch (Exception e) {
			logger.error("Error generico en isOficinaPagoManual", e);
			throw e;
		}
	}

	/** SONAR Q ** MODIF TAM(03.11.2021) * Inicio */
	/* Eliminamos el metodo generaDistribucionCostes por que no se utiliza */
	/** SONAR Q ** MODIF TAM(03.11.2021) * Fin */
	/**
	 * Obtiene la descripcion de BonificacionRecargo
	 * 
	 * @param codigoOrganismo
	 * @return
	 */
	public String getDescBoniRecar(BigDecimal codigo) {

		String desc = "";
		try {
			com.rsi.agp.dao.tables.cpl.BonificacionRecargo br = (com.rsi.agp.dao.tables.cpl.BonificacionRecargo) polizaDao
					.getObject(com.rsi.agp.dao.tables.cpl.BonificacionRecargo.class, codigo.longValue());
			if (br != null)
				desc = br.getDescripcion();
		} catch (Exception e) {
			logger.error("Error al obtener la descripcion de BonificacionRecargo", e);
		}
		return desc;
	}

	public String getOficina(Usuario usuario, Asegurado asegurado, Colectivo colectivo) {

		// Si el usuario que da de alta la piliza es externo, la oficina se coge del
		// propio usuario
		if (usuario != null && usuario.isUsuarioExterno() && usuario.getOficina() != null
				&& usuario.getOficina().getId() != null && usuario.getOficina().getId().getCodoficina() != null) {
			return String.format("%04d", usuario.getOficina().getId().getCodoficina().intValue());
		} else {
			// Si es interno, la oficina de la poliza la recojo de la CCC del asegurado para
			// esa linea
			// o linea 999 que es la de por defecto.

			/** SONAR Q ** MODIF TAM(03.11.2021) **/
			/* A�adimos nuevo m�todo para descargar de ifs/for */
			String ofiAseg = obtenerOfiAseg(asegurado, colectivo);
			if (!VACIO.equals(ofiAseg)) {
				return ofiAseg;
			}
			/** SONAR Q ** MODIF TAM(03.11.2021) * Fin */

			if (VACIO.equals(ofiAseg)) {
				if (usuario != null && usuario.getOficina() != null && usuario.getOficina().getId() != null
						&& usuario.getOficina().getId().getCodoficina() != null) {
					ofiAseg = String.format("%04d", usuario.getOficina().getId().getCodoficina().intValue());
					return ofiAseg;
				}
			}
		}
		return null;
	}

	public void deleteDistribucionCostes2015(Poliza poliza) {
		try {
			Set<DistribucionCoste2015> distCostes = poliza.getDistribucionCoste2015s();
			for (DistribucionCoste2015 distCoste : distCostes) {
				long valor = 0;
				if (null != distCoste.getIdcomparativa()) {
					valor = distCoste.getIdcomparativa().longValue();
				}
				distribucionCosteDAO.deleteDistribucionCoste2015(distCoste.getPoliza().getIdpoliza(),
						distCoste.getCodmodulo(), valor);
			}
			poliza.getDistribucionCoste2015s().clear();
		} catch (Exception exception) {
			logger.debug(exception);
		}
	}

	/**
	 * Devuelve un booleano indicando si se puede hacer complementarias sobre la
	 * piliza principal indicada
	 * 
	 * @return
	 */
	public boolean habilitarComplementaria(Map<String, Object> parameters, List<Poliza> listaPolizas) {

		for (Poliza pol : listaPolizas) {
			if (pol.getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL)) {
				parameters.put("idPolPr", pol.getIdpoliza().toString());
			}

			if ((pol.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL))
					|| (pol.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA))) {
				return true;
			}
		}

		return false;
	}

	public void generaDistribucionCostes(Poliza polizaActual, VistaImportes vistaImportes, Long idComparativaAux) {

		Set<DistribucionCoste> distribucionesCostes = polizaActual.getDistribucionCostes();

		for (DistribucionCoste distribCostes : distribucionesCostes) {// s�lo va a ver uno
			vistaImportes.setImporteTomador(distribCostes.getCargotomador().toString());

			/** SONAR Q ** MODIF TAM(03.11.2021) **/
			/* A�adimos nuevo m�todo para descargar de ifs/for */
			VistaImportesPorGrupoNegocio vistaImpoPorGrNeg = null;
			vistaImpoPorGrNeg = obtenerVistaImpPorGrNegocio(distribCostes, polizaActual);
			/** SONAR Q ** MODIF TAM(03.11.2021) **/

			if (distribCostes != null) {
				vistaImportes.setImporteTomador(distribCostes.getCargotomador().toString());
			}

			// Subvenciones. Formato mapa: cod subvencion/organismo, importe
			Map<BigDecimal, BigDecimal> mapSuvEnesa = new HashMap<BigDecimal, BigDecimal>();
			Map<Character, BigDecimal> mapSuvCCAA = new HashMap<Character, BigDecimal>();

			Set<DistCosteSubvencion> listDistCosteSubvencion = distribCostes.getDistCosteSubvencions();

			// enesa --> es enesa si tiene organismo = 0
			for (DistCosteSubvencion distCosteSubvencion : listDistCosteSubvencion) {
				if (distCosteSubvencion.getCodorganismo().equals('0')) {

					mapSuvEnesa.put(distCosteSubvencion.getCodtiposubv(), distCosteSubvencion.getImportesubv());

					BigDecimal cod = distCosteSubvencion.getCodtiposubv();
					String des = this.getDescripcionEnesa(cod);
					BigDecimal impor = mapSuvEnesa.get(cod);

					vistaImpoPorGrNeg.addSubEnesa(cod.toString() + "-" + des, NumberUtils.formatear(impor, 2));
				} else {
					mapSuvCCAA.put(distCosteSubvencion.getCodorganismo(), distCosteSubvencion.getImportesubv());
				}
			}

			// ccaa --> es ccaa si tiene organismo != 0
			for (DistCosteSubvencion distCosteSubvencion : listDistCosteSubvencion) {
				if (!distCosteSubvencion.getCodorganismo().equals('0')) {
					Character cod = distCosteSubvencion.getCodorganismo();
					String des = this.getCCAA(cod);
					BigDecimal impor = mapSuvCCAA.get(cod);

					vistaImpoPorGrNeg.addSubCCAA(cod.toString() + "-" + des, NumberUtils.formatear(impor, 2));
				}
			}
			vistaImportes.getVistaImportesPorGrupoNegocio().add(vistaImpoPorGrNeg);
		}

		Set<DistribucionCoste2015> distribucionesCostes2015 = polizaActual.getDistribucionCoste2015s();
		BigDecimal totalCosteTomador = new BigDecimal(0.0).setScale(2);
		BigDecimal viImpTomador = new BigDecimal(0.00);
		BigDecimal totalcostetomadorafinanciar = new BigDecimal(0.00);
		viImpTomador.setScale(2);

		boolean reacargoAval = false;
		boolean recargoFracc = false;

		for (DistribucionCoste2015 distribCostes1 : distribucionesCostes2015) {
			VistaImportesPorGrupoNegocio vistaImpoPorGrNeg = null;

			if (distribCostes1 != null) {
				if (distribCostes1.getIdcomparativa().equals(new BigDecimal(idComparativaAux))) {
					vistaImpoPorGrNeg = new VistaImportesPorGrupoNegocio();
					if (null != distribCostes1.getGrupoNegocio()) {
						vistaImpoPorGrNeg.setCodGrupoNeg(distribCostes1.getGrupoNegocio().toString());
						vistaImpoPorGrNeg.setDescGrupNeg(this.getDesGruponegocio(distribCostes1.getGrupoNegocio()));
					}

					vistaImpoPorGrNeg.setPrimaComercial(NumberUtils.formatear(distribCostes1.getPrimacomercial(), 2));
					vistaImpoPorGrNeg.setPrimaNeta(NumberUtils.formatear(distribCostes1.getPrimacomercialneta(), 2));
					vistaImpoPorGrNeg.setPrimaNetaB(distribCostes1.getPrimacomercialneta());
					vistaImpoPorGrNeg
							.setRecargoConsorcio(NumberUtils.formatear(distribCostes1.getRecargoconsorcio(), 2));
					vistaImpoPorGrNeg.setReciboPrima(NumberUtils.formatear(distribCostes1.getReciboprima(), 2));
					vistaImpoPorGrNeg.setCosteTomador(NumberUtils.formatear(distribCostes1.getCostetomador(), 2));

					totalCosteTomador = distribCostes1.getTotalcostetomador();

					if (polizaActual.getLinea().isLineaGanado() && polizaActual.getEsFinanciada().equals('S')
							&& null != distribCostes1.getTotalcostetomadorafinanciar()) {
						if (distribCostes1.getTotalcostetomadorafinanciar().compareTo(new BigDecimal(0)) == 0) {
							// del totalCosteTomador restamos el recargoAval y recargoFraccionamiento
							viImpTomador = (distribCostes1.getTotalcostetomador()
									.subtract(distribCostes1.getRecargoaval()))
											.subtract(distribCostes1.getRecargofraccionamiento());
						} else {
							viImpTomador = distribCostes1.getTotalcostetomadorafinanciar();

						}
						totalcostetomadorafinanciar = viImpTomador;
					} else {
						viImpTomador = viImpTomador.add(distribCostes1.getCostetomador());
						if (polizaActual.getLinea().isLineaGanado()) {
							totalcostetomadorafinanciar = totalcostetomadorafinanciar
									.add(distribCostes1.getCostetomador());
						} else {
							totalcostetomadorafinanciar = distribCostes1.getTotalcostetomador();
						}

					}

					if (distribCostes1.getPeriodoFracc() != null) {
						vistaImportes.setPeriodoFracc(distribCostes1.getPeriodoFracc().toString());
					}

					if (distribCostes1.getRecargoaval() != null && reacargoAval == false) {// solo lo pintamos en un
																							// grupo de negocio
						vistaImpoPorGrNeg.setRecargoAval(NumberUtils.formatear(distribCostes1.getRecargoaval(), 2));
						reacargoAval = true;
					}

					if (distribCostes1.getRecargofraccionamiento() != null && recargoFracc == false) {
						vistaImpoPorGrNeg.setRecargoFraccionamiento(
								NumberUtils.formatear(distribCostes1.getRecargofraccionamiento(), 2));
						recargoFracc = true;
					}

					// Subvenciones. Formato mapa: cod subvencion/organismo, importe
					Map<BigDecimal, BigDecimal> mapSuvEnesa = new HashMap<BigDecimal, BigDecimal>();
					Map<Character, BigDecimal> mapSuvCCAA = new HashMap<Character, BigDecimal>();

					Set<DistCosteSubvencion2015> listDistCosteSubvencion = distribCostes1.getDistCosteSubvencion2015s();

					// enesa --> es enesa si tiene organismo = 0
					for (DistCosteSubvencion2015 distCosteSubvencion : listDistCosteSubvencion) {
						if (distCosteSubvencion.getCodorganismo().equals('0')) {
							mapSuvEnesa.put(distCosteSubvencion.getCodtiposubv(), distCosteSubvencion.getImportesubv());

							BigDecimal cod = distCosteSubvencion.getCodtiposubv();
							String des = this.getDescripcionEnesa(cod);
							BigDecimal impor = mapSuvEnesa.get(cod);

							vistaImpoPorGrNeg.addSubEnesa(des, NumberUtils.formatear(impor, 2));

						} else {
							mapSuvCCAA.put(distCosteSubvencion.getCodorganismo(), distCosteSubvencion.getImportesubv());

							Character cod = distCosteSubvencion.getCodorganismo();
							String des = this.getCCAA(cod);
							BigDecimal impor = mapSuvCCAA.get(cod);

							vistaImpoPorGrNeg.addSubCCAA(des, NumberUtils.formatear(impor, 2));
						}
					}

					Set<BonificacionRecargo2015> boniRecargo1 = distribCostes1.getBonificacionRecargo2015s();

					if (boniRecargo1 != null) {
						for (BonificacionRecargo2015 b : boniRecargo1) {
							vistaImpoPorGrNeg.addBoniRecargo1(this.getDescBoniRecar(b.getCodigo()),
									NumberUtils.formatear(b.getImporte(), 2));
						}
					}

					// Primera fraccion del importe
					if (distribCostes1.getImportePagoFraccAgr() != null) {
						vistaImportes.setImportePagoFraccAgr(
								NumberUtils.formatear(distribCostes1.getImportePagoFraccAgr(), 2));
					}

					if (distribCostes1.getImportePagoFracc() != null) {
						vistaImportes
								.setImportePagoFracc(NumberUtils.formatear(distribCostes1.getImportePagoFracc(), 2));
					}

					vistaImportes.getVistaImportesPorGrupoNegocio().add(vistaImpoPorGrNeg);
				}
			}
		}

		if (totalCosteTomador.compareTo(new BigDecimal(0)) > 0)
			vistaImportes.setTotalCosteTomador(NumberUtils.formatear(totalCosteTomador, 2));

		if (viImpTomador.compareTo(new BigDecimal(0)) > 0)
			vistaImportes.setImporteTomador(NumberUtils.formatear(viImpTomador, 2));

		if (totalcostetomadorafinanciar.compareTo(new BigDecimal(0)) > 0)
			vistaImportes.setTotalCosteTomadorAFinanciar(NumberUtils.formatear(totalcostetomadorafinanciar, 2));
	}

	public boolean isParcelasCorrectas(Long idPoliza) {
		boolean correcto = false;
		try {
			correcto = seleccionPolizaDao.isParcelasCorrectas(idPoliza);
		} catch (Exception e) {
			logger.error("Error al comprobar si las parcelas de la p�liza est�n correctas", e);
		}

		return correcto;
	}

	public String getDesGruponegocio(Character codGrupoNegocio) {
		String desc = "";
		try {

			com.rsi.agp.dao.tables.cgen.GruposNegocio gn = (com.rsi.agp.dao.tables.cgen.GruposNegocio) polizaDao
					.getObject(com.rsi.agp.dao.tables.cgen.GruposNegocio.class, codGrupoNegocio);
			if (gn != null) {
				desc = gn.getDescripcion();
			}
		} catch (Exception e) {
			logger.error("Error al obtener la descripcion del grupo de negocio ", e);
		}
		return desc;
	}

	public boolean checkRecalculoHojaYNumPoliza(Long idPoliza) {
		boolean res = seleccionPolizaDao.checkRecalculoHojaYNumPoliza(idPoliza);
		return res;
	}

	public void inicializarRecalculoHojaYNumPoliza(Long idPoliza) {
		seleccionPolizaDao.inicializarRecalculoHojaYNumPoliza(idPoliza);
	}

	public void marcarRecalculoHojaYNum(Long idPoliza) {
		seleccionPolizaDao.marcarRecalculoHojaYNum(idPoliza);
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}

	public void setAseguradoDao(IAseguradoDao aseguradoDao) {
		this.aseguradoDao = aseguradoDao;
	}

	public void setDatabaseManager(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}

	public final void setSeleccionPolizaDao(final ISeleccionPolizaDao seleccionPolizaDao) {
		this.seleccionPolizaDao = seleccionPolizaDao;
	}

	public void setConsultaSbpManager(IConsultaSbpManager consultaSbpManager) {
		this.consultaSbpManager = consultaSbpManager;
	}

	public void setSimulacionSbpManager(ISimulacionSbpManager simulacionSbpManager) {
		this.simulacionSbpManager = simulacionSbpManager;
	}

	public void setHistoricoEstadosManager(IHistoricoEstadosManager historicoEstadosManager) {
		this.historicoEstadosManager = historicoEstadosManager;
	}

	public void setPantallaConfigurableDao(IPantallaConfigurableDao pantallaConfigurableDao) {
		this.pantallaConfigurableDao = pantallaConfigurableDao;
	}

	public void setDistribucionCosteDAO(IDistribucionCosteDAO distribucionCosteDAO) {
		this.distribucionCosteDAO = distribucionCosteDAO;
	}

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}

	public void setCpmTipoCapitalDao(ICPMTipoCapitalDAO cpmTipoCapitalDao) {
		this.cpmTipoCapitalDao = cpmTipoCapitalDao;
	}

	public void setCapitalAseguradoDao(ICapitalAseguradoDao capitalAseguradoDao) {
		this.capitalAseguradoDao = capitalAseguradoDao;
	}

	/**
	 * Devuelve un mapa cuyas claves se forman con "codModulo-hoja-numero" de la
	 * parcela de p�liza, y cuyo contenido son los ProduccionVO
	 * 
	 * @param idPoliza
	 * @param colIdParcelasParaRecalculo
	 * @param realPath
	 * @param codUsuario
	 * @param esAnexo
	 * @param indiceCapital
	 * @return
	 * @throws Exception
	 */
	public List<Parcela> calculoRtoHist(Long idPoliza, Set<Long> colIdParcelasParaRecalculo, String realPath,
			Usuario usuario, int indiceCapital, List<Parcela> parcelasEnPantalla, AnexoModificacion anexo)
			throws Throwable {

		CalcularRendimientosRequest request = null;
		ContratacionRendimientosResponse respuesta = null;
		Poliza poliza = null;
		String xmlPoliza = "";
		String error = null;

		poliza = (Poliza) polizaDao.getPolizaById(idPoliza);

		this.obtenerHojaNumero(poliza);

		String rutaWebInfDecod = URLDecoder.decode(realPath, "UTF-8");

		List<ModuloPoliza> colComparativas = polizaDao.getLstModulosPoliza(poliza.getIdpoliza());

		Set<String> colModulos = new HashSet<String>();

		for (ModuloPoliza mp : colComparativas) {
			String codModulo = mp.getId().getCodmodulo();
			logger.debug("M�dulo: " + codModulo);

			if (!colModulos.contains(codModulo)) {

				/* SONAR Q */
				xmlPoliza = obtenerxml(anexo, poliza, mp, usuario, colIdParcelasParaRecalculo);
				/* SONAR Q */

				request = SWContratacionRendimientosHelper.obtenerRendimientosRequest(xmlPoliza, true,
						AjustarProducciones.MAX, true);

				try {
					respuesta = new SWContratacionRendimientosHelper().getProduccionRendimientos(request,
							rutaWebInfDecod, usuario.getCodusuario());
					logger.debug("respuesta ws: " + respuesta.getRendimientoPolizaDocument().toString());

					/** SONAR Q ** MODIF TAM(03.11.2021) **/
					/* A�adimos nuevo m�todo para descargar de ifs/for */
					actualizarTipoRdto(respuesta, parcelasEnPantalla);
					/** SONAR Q ** MODIF TAM(03.11.2021) * Fin */

				} catch (AgrException e) {
					error = e.getMessage();
					if (error != null && !"".equals(error)) {
						logger.debug("Error : " + error);
						throw e;
					}

				} finally {
					calculoPrecioProduccionManager.registrarEnHistoricoDesdePoliza(usuario.getCodusuario(), request,
							respuesta, poliza, xmlPoliza, error, null, null);
				}
				colModulos.add(codModulo);

			}
		}
		return parcelasEnPantalla;
	}

	/** SONAR Q ** MODIF TAM(03.11.2021) **/
	/* Se renombra el nombre del m�todo y le quitamos la may�scula */
	public List<com.rsi.agp.dao.tables.anexo.CapitalAsegurado> calculoRtoHist(Set<Long> colIdParcelasParaRecalculo,
			String realPath, Usuario usuario, int indiceCapital,
			List<com.rsi.agp.dao.tables.anexo.CapitalAsegurado> capitalesEnPantalla, AnexoModificacion anexo)
			throws Exception {

		CalcularRendimientosRequest request = null;
		ContratacionRendimientosResponse respuesta = null;

		String xmlPoliza = "";
		String error = null;

		try {

			String rutaWebInfDecod = null;
			rutaWebInfDecod = URLDecoder.decode(realPath, "UTF-8");
			/* ESC-16025 ** MODIF TAM (23.11.2021) ** Inicio */
			/* Pasamos parametro tipo boolean para saber que venimos de Calculo de Rendimiento Orientativo */
			boolean calcRendOriHist = true;
			
			xmlPoliza = SWContratacionRendimientosHelper.polizaRendimientosAnexoToXml(anexo, colIdParcelasParaRecalculo,
					usuario, xmlAnexoModDao, cpmTipoCapitalDao, polizaCopyDao, calcRendOriHist);
			/* ESC-16025 ** MODIF TAM (23.11.2021) ** Fin */
			
			request = SWContratacionRendimientosHelper.obtenerRendimientosRequest(xmlPoliza, true,
					AjustarProducciones.MAX, true);

			respuesta = new SWContratacionRendimientosHelper().getProduccionRendimientos(request, rutaWebInfDecod,
					usuario.getCodusuario());

			PolizaDocument pd = respuesta.getRendimientoPolizaDocument();
			for (com.rsi.agp.dao.tables.anexo.CapitalAsegurado cap : capitalesEnPantalla) {
				Cosecha[] cosechas = pd.getPoliza().getCosechaArray();

				for (Cosecha c : cosechas) {
					for (es.agroseguro.seguroAgrario.rendimientosCalculo.Parcela p : c.getParcelaArray()) {
						if (p.getHoja() == cap.getParcela().getHoja().intValue()
								&& p.getNumero() == cap.getParcela().getNumero().intValue()) {
							
							
							/****/
							/* P0078877 ** MODIF TAM (18/11/2021) ** Defecto N�4 */
							/*obtenemos el tipo de Rendimiento que ya tiene la parcela */
							logger.debug ("Valor de tipoRdto Anterior (cap.getTipoRdto()): "+ cap.getTipoRdto());
							Long tipoRendCapAnterior = cap.getTipoRdto();
							logger.debug ("Valor de tipoRdto Anterior (tipoRendCapAnterior): "+ tipoRendCapAnterior);
							
							BigDecimal produccionAnt = cap.getProduccion();
							
							Long tipoRdto = obtenerTipoRendimiento(p, tipoRendCapAnterior);
							Long produccion = obtenerProduccion(p, tipoRendCapAnterior, produccionAnt);
							
							/* P0078877 ** MODIF TAM (18/11/2021) ** Defecto N�4 */
							cap.setTipoRdto(tipoRdto);
							cap.setProduccion(BigDecimal.valueOf(produccion));
							cap.setTipomodificacion(Constants.MODIFICACION);
							cap.getParcela().setTipomodificacion(Constants.MODIFICACION);
							capitalAseguradoDao.actualizarTipoRdtoAnexo(cap.getId(), tipoRdto, produccion,
									cap.getParcela().getId());
						}
					}
				}
			}

		} catch (AgrException e) {
			error = e.getMessage();
			if (error != null && !"".equals(error)) {
				logger.debug("Error : " + error);
				throw e;
			}

		} finally {
			calculoPrecioProduccionManager.registrarEnHistoricoDesdeAnexo(usuario.getCodusuario(), request, respuesta,
					anexo, xmlPoliza, error, null);
		}

		return capitalesEnPantalla;
	}

	private Long obtenerTipoRendimiento(es.agroseguro.seguroAgrario.rendimientosCalculo.Parcela parcela, Long tipoRendCapAnterior) {
		
		/* P0078877 ** MODIF TAM (18/11/2021) ** Defecto N�4 */
		/* Si Anteriormente se ha calculado el Rendimiento y se ha asignado el Rendimiento Orientativo, este no se cambia */
		logger.debug("SeleccionPolizaManager - obtenerTipoRendimiento [INIT]");
		logger.debug("Valro de tipoRendimiento Anterior: "+tipoRendCapAnterior);
		
		/* P0078877 ** MODIF TAM (18/11/2021) ** Defecto N�4 */
		/* Si Anteriormente se ha calculado el Rendimiento y se ha asignado el Rendimiento Orientativo, este no se cambia */
		Long tipoRendimiento;
		
		BigDecimal tipoRend;
		if (tipoRendCapAnterior == null) {
			tipoRend = new BigDecimal(0);
		}else {
			tipoRend = new BigDecimal(tipoRendCapAnterior);	
		}
		
		BigDecimal tipoRdtoOrient = new BigDecimal(4);
		
		if ((tipoRend).compareTo(tipoRdtoOrient) == 0) { 
			tipoRendimiento = tipoRendCapAnterior;
		}else {
			tipoRendimiento = Constants.TIPO_RDTO_MAXIMO;
		}
		/* P0078877 ** MODIF TAM (18/11/2021) ** Defecto N�4 */
		
		if (parcela.getHistoricoNoAplicado() != null) {

			tipoRendimiento = Long.valueOf(parcela.getHistoricoNoAplicado().getCodigo());

		} else if (parcela.getControlParcelaAjustado() != null) {
			
			
			/* P0078877 - Resoluci�n Defecto N�3 ** MODIF TAM (17.11.2021) */
			/* En el caso del Calculo de Rendimiento Hist�rico, no se puede asignar el Rendimiento Orientativo */
			if (Constants.TIPO_RDTO_SIN_RENDIMIENTO_ASIGNADO != Long.valueOf(parcela.getControlParcelaAjustado().getCodigo())){
				
				/* Si ya teniamos Rdto. Orientativo informado, no se cambia el valor por el del Rdto hist�rico */
				if ((tipoRend).compareTo(tipoRdtoOrient) != 0) {
					tipoRendimiento = Long.valueOf(parcela.getControlParcelaAjustado().getCodigo());	
				}
					
			}
			/* P0078877 - Resoluci�n Defecto N�3 ** MODIF TAM (17.11.2021) */
			
		}
		logger.debug("Valro de tipoRendimiento Devuelto: "+tipoRendimiento);
		logger.debug("SeleccionPolizaManager - obtenerTipoRendimiento [END]");
		
		return tipoRendimiento;
	}

	private Long obtenerProduccion(es.agroseguro.seguroAgrario.rendimientosCalculo.Parcela parcela, Long tipoRendCapAnterior, BigDecimal produccionAnt) {

		/* P0078877 ** MODIF TAM (18/11/2021) ** Defecto N�4 */
		logger.debug("SeleccionPolizaManager - obtenerProduccion [INIT]");
		logger.debug("Valor de produccion Anterior: "+produccionAnt);
		
		/* Si Anteriormente se ha calculado el Rendimiento y se ha asignado el Rendimiento Orientativo, este no se cambia */
		
		
		/* P0078877 ** MODIF TAM (18/11/2021) ** Defecto N�4 */
		/* Si Anteriormente se ha calculado el Rendimiento y se ha asignado el Rendimiento Orientativo, este no se cambia */
		Long produccion;
		
		
		BigDecimal tipoRend;
		if (tipoRendCapAnterior == null) {
			tipoRend = new BigDecimal(0);
		}else {
			tipoRend = new BigDecimal(tipoRendCapAnterior);	
		}
		
		BigDecimal tipoRdtoOrient = new BigDecimal(4);
		
		if ((tipoRend).compareTo(tipoRdtoOrient) == 0) {
			produccion = produccionAnt.longValue();
		}else {
			produccion = Long.valueOf(parcela.getProduccion());	
		}
		logger.debug("Valor de produccion (1): "+produccion);
		/* P0078877 ** MODIF TAM (18/11/2021) ** Defecto N�4 */
		

		if (parcela.getHistoricoNoAplicado() != null) {

			produccion = Long.valueOf(parcela.getHistoricoNoAplicado().getProduccion());

		} else if (parcela.getControlParcelaAjustado() != null) {

			/* P0078877 - Resoluci�n Defecto N�3 ** MODIF TAM (17.11.2021) */
			/* En el caso del Calculo de Rendimiento Hist�rico, no se puede asignar el Rendimiento Orientativo */
			if (Constants.TIPO_RDTO_SIN_RENDIMIENTO_ASIGNADO != Long.valueOf(parcela.getControlParcelaAjustado().getCodigo())){
				produccion = Long.valueOf(parcela.getControlParcelaAjustado().getProduccionMaxima());	
			}
			/* P0078877 - Resoluci�n Defecto N�3 ** MODIF TAM (17.11.2021) */
			
		}

		logger.debug("Valor de produccion Devuelto: "+produccion);
		logger.debug("SeleccionPolizaManager - obtenerProduccion [END]");

		return produccion;
	}

	/*** Pet. 78877 ** MODIF TAM (25.10.2021) ** Inicio */
	
	/*** Declaramos un nuevo m�todo para obtener el Rendimiento orientativo */
	public List<Parcela> calculoRdtoOrientativo(Long idPoliza, Set<Long> colIdParcelasParaRecalculo, String realPath,
			Usuario usuario, int indiceCapital, List<Parcela> parcelasEnPantalla, AnexoModificacion anexo)
			throws Throwable {

		
		logger.debug("SeleccionPolizaManager-calculoRdtoOrientativo [INIT]");
		
		CalcularRendimientosRequest request = null;
		ContratacionRendimientosResponse respuesta = null;
		Poliza poliza = null;
		String xmlPoliza = "";
		String error = null;

		poliza = (Poliza) polizaDao.getPolizaById(idPoliza);

		this.obtenerHojaNumero(poliza);

		String rutaWebInfDecod = URLDecoder.decode(realPath, "UTF-8");

		List<ModuloPoliza> colComparativas = polizaDao.getLstModulosPoliza(poliza.getIdpoliza());

		Set<String> colModulos = new HashSet<String>();

		for (ModuloPoliza mp : colComparativas) {
			String codModulo = mp.getId().getCodmodulo();
			logger.debug("M�dulo: " + codModulo);

			if (!colModulos.contains(codModulo)) {
				
				/* SONAR Q */
				xmlPoliza = obtenerxml(anexo, poliza, mp, usuario, colIdParcelasParaRecalculo);
				/* SONAR Q */
				
				request = SWContratacionRendimientosHelper.obtenerRendimientosRequest(xmlPoliza, true,
						AjustarProducciones.MAX, true);

				try {
					respuesta = new SWContratacionRendimientosHelper().getProduccionRendimientos(request,
							rutaWebInfDecod, usuario.getCodusuario());
					logger.debug("respuesta ws: " + respuesta.getRendimientoPolizaDocument().toString());
					
					/** SONAR Q ** MODIF TAM(03.11.2021) **/
					/* A�adimos nuevo m�todo para descargar de ifs/for */
					actualizarTipoRdtoOri(respuesta, parcelasEnPantalla);
					/** SONAR Q ** MODIF TAM(03.11.2021) * Fin */

				} catch (AgrException e) {
					error = e.getMessage();
					if (error != null && !"".equals(error)) {
						logger.debug("Error : " + error);
						throw e;
					}

				} finally {
					calculoPrecioProduccionManager.registrarEnHistoricoDesdePoliza(usuario.getCodusuario(), request,
							respuesta, poliza, xmlPoliza, error, null, null);
				}
				colModulos.add(codModulo);

			}
		}
		
		logger.debug("SeleccionPolizaManager-calculoRdtoOrientativo [INIT]");
		return parcelasEnPantalla;
	}
	
	
	/** Nueva Funci�n para obtener el calculo del Rendimiento Orientativo en anexos de modificaci�n ***/
	public List<com.rsi.agp.dao.tables.anexo.CapitalAsegurado> calculoRtoOrient(Set<Long> colIdParcelasParaRecalculo,
			String realPath, Usuario usuario, int indiceCapital,
			List<com.rsi.agp.dao.tables.anexo.CapitalAsegurado> capitalesEnPantalla, AnexoModificacion anexo)
			throws Exception {

		CalcularRendimientosRequest request = null;
		ContratacionRendimientosResponse respuesta = null;

		String xmlPoliza = "";
		String error = null;
		
		logger.debug("SeleccionPolizaManager - calculoRtoOrient [INIT]");
		logger.debug("Valor de colIdParcelasParaRecalculo:"+colIdParcelasParaRecalculo.toString());

		try {

			String rutaWebInfDecod = null;
			rutaWebInfDecod = URLDecoder.decode(realPath, "UTF-8");
			
			/* ESC-16025 ** MODIF TAM (23.11.2021) ** Inicio */
			/* Pasamos parametro tipo boolean para saber que venimos de Calculo de Rendimiento Orientativo */
			boolean calcRendOriHist = true;
			
			xmlPoliza = SWContratacionRendimientosHelper.polizaRendimientosAnexoToXml(anexo, colIdParcelasParaRecalculo,
					usuario, xmlAnexoModDao, cpmTipoCapitalDao, polizaCopyDao, calcRendOriHist);
			/* ESC-16025 ** MODIF TAM (23.11.2021) ** Fin */
			
			request = SWContratacionRendimientosHelper.obtenerRendimientosRequest(xmlPoliza, true,
					AjustarProducciones.MAX, true);

			respuesta = new SWContratacionRendimientosHelper().getProduccionRendimientos(request, rutaWebInfDecod,
					usuario.getCodusuario());
			
			/** SONAR Q ** MODIF TAM(05.11.2021) **/
			/* A�adimos nuevo m�todo para descargar de ifs/for */
			actualizarTipoRdtoOriAnexo(respuesta, capitalesEnPantalla);
			/** SONAR Q ** MODIF TAM(05.11.2021) * Fin */


		} catch (AgrException e) {
			error = e.getMessage();
			if (error != null && !"".equals(error)) {
				logger.debug("Error : " + error);
				throw e;
			}

		} finally {
			logger.debug("Antes de registrarEnHistoricoDesdeAnexo");
			calculoPrecioProduccionManager.registrarEnHistoricoDesdeAnexo(usuario.getCodusuario(), request, respuesta,
					anexo, xmlPoliza, error, null);
		}

		return capitalesEnPantalla;
	}
	
	private Long obtenerTipoRdtoOrientativo(es.agroseguro.seguroAgrario.rendimientosCalculo.Parcela parcela, Long tipoRendCapAnterior) {

		logger.debug("SeleccionPolizaManager - obtenerTipoRdtoOrientativo [INIT]");
		logger.debug("Valor de parcela: " + parcela.toString());
		logger.debug("Valor de tipoRendimiento Anterior:" +tipoRendCapAnterior );
		
		
		/* P0078877 ** MODIF TAM (18/11/2021) ** Defecto N�4 */
		/* Si Anteriormente se ha calculado el Rendimiento y se ha asignado el Rendimiento Orientativo, este no se cambia */
		Long tipoRendimiento;
		
		BigDecimal tipoRend;
		if (tipoRendCapAnterior == null) {
			tipoRend = new BigDecimal(0);
		}else {
			tipoRend = new BigDecimal(tipoRendCapAnterior);	
		}
		
		
		BigDecimal tipoRdtoHist = new BigDecimal(1);
		BigDecimal tipoRdtoHistSinAct = new BigDecimal(2);
		
		if ((tipoRend).compareTo(tipoRdtoHist) == 0 || (tipoRend).compareTo(tipoRdtoHistSinAct) == 0) { 
			tipoRendimiento = tipoRendCapAnterior;
		}else {
			tipoRendimiento = Constants.TIPO_RDTO_MAXIMO;	
		}
		
		logger.debug("Valor de tipoRendimiento (1): "+tipoRendimiento);
		/* P0078877 ** MODIF TAM (18/11/2021) ** Defecto N�4 */
		
		if (parcela.getHistoricoNoAplicado() != null) {
			tipoRendimiento = Long.valueOf(parcela.getHistoricoNoAplicado().getCodigo());
		}else if (parcela.getControlParcelaAjustado() != null) {
			logger.debug("Entramos dentro del else if");
			logger.debug("Valor del codigo:"+Long.valueOf(parcela.getControlParcelaAjustado().getCodigo()));
			
			/* Obtenemos la producci�n del Rendiento Orientativo */
			if (Constants.TIPO_RDTO_SIN_RENDIMIENTO_ASIGNADO == Long.valueOf(parcela.getControlParcelaAjustado().getCodigo())){
				logger.debug("Entramos en el if de Rdto Orientativo (codigo4)");
				tipoRendimiento = Long.valueOf(parcela.getControlParcelaAjustado().getCodigo());
				logger.debug("Asignamos tipo Rendimiento: " + tipoRendimiento);
			/* Resoluci�n Defecto 1 (17.11.2021) Inicio*/
			/* En el calculo de Rendimiento Orientativo hay que tener en cuenta el Rdto M�ximo */	
			}else if (Constants.TIPO_RDTO_MAXIMO == Long.valueOf(parcela.getControlParcelaAjustado().getCodigo())){
				logger.debug("Entramos en el if de Rdto Maximo (codigo3)");
				tipoRendimiento = Long.valueOf(parcela.getControlParcelaAjustado().getCodigo());
				logger.debug("Asignamos tipo Rendimiento: " + tipoRendimiento);
				
			}
			/* En el calculo de Rendimiento Orientativo hay que tener en cuenta el Rdto M�ximo */
		}
		
		logger.debug("Retornamos tipoRendimiento: " + tipoRendimiento);
		return tipoRendimiento;
	}

	private Long obtenerProdRdtoOrientativo(es.agroseguro.seguroAgrario.rendimientosCalculo.Parcela parcela, Long tipoRendCapAnterior, BigDecimal produccionAnt) {

		logger.debug("SeleccionPolizaManager - obtenerProdRdtoOrientativo [INIT]");
		logger.debug("Valor de parcela: " + parcela.toString());
		
		/* Si Anteriormente se ha calculado el Rendimiento y se ha asignado el Rendimiento Orientativo, este no se cambia */
		Long produccion;
		
		BigDecimal tipoRend;
		if (tipoRendCapAnterior == null) {
			tipoRend = new BigDecimal(0);
		}else {
			tipoRend = new BigDecimal(tipoRendCapAnterior);	
		}
		BigDecimal tipoRdtoHist = new BigDecimal(1);
		BigDecimal tipoRdtoHistSinAct = new BigDecimal(2);
		
		if ((tipoRend).compareTo(tipoRdtoHist) == 0 || (tipoRend).compareTo(tipoRdtoHistSinAct) == 0) { 
			produccion = produccionAnt.longValue();
		}else {
			produccion = Long.valueOf(parcela.getProduccion());	
		}			
				
		logger.debug("Valor de produccion (1): "+produccion);
		/* P0078877 ** MODIF TAM (18/11/2021) ** Defecto N�4 */

		if (parcela.getHistoricoNoAplicado() != null) {

			produccion = Long.valueOf(parcela.getHistoricoNoAplicado().getProduccion());
			
		} else if (parcela.getControlParcelaAjustado() != null) {
			Long tipoRendimiento = Long.valueOf(parcela.getControlParcelaAjustado().getCodigo());
			
			/* Obtenemos la producci�n del Rendiento Orientativo */
			if (Constants.TIPO_RDTO_SIN_RENDIMIENTO_ASIGNADO == tipoRendimiento){
				produccion = Long.valueOf(parcela.getControlParcelaAjustado().getProduccionMaxima());
			/* Resoluci�n Defecto 1 (17.11.2021) Inicio*/
			/* En el calculo de Rendimiento Orientativo hay que tener en cuenta el Rdto M�ximo */	
			}else if (Constants.TIPO_RDTO_MAXIMO == Long.valueOf(parcela.getControlParcelaAjustado().getCodigo())){
				produccion = Long.valueOf(parcela.getControlParcelaAjustado().getProduccionMaxima());
			}
			/* En el calculo de Rendimiento Orientativo hay que tener en cuenta el Rdto M�ximo */
 
		}

		logger.debug("Retornamos produccion: " + produccion);
		
		return produccion;
	}

	/*** Pet. 78877 ** MODIF TAM (25.10.2021) ** Inicio */

	/** SONAR Q ** MODIF TAM (28.10.2021) ** Inicio */
	private List<Parcela> obtenerListParcFinal(List<Parcela> listparc, String sistcultivo) {

		List<Parcela> listParcFinal = new ArrayList<Parcela>();
		for (Parcela par : listparc) {
			for (CapitalAsegurado cap : par.getCapitalAsegurados()) {
				for (DatoVariableParcela datvar : cap.getDatoVariableParcelas()) {
					// Si la parcela tiene "Sistema de cultivo"
					if (datvar.getDiccionarioDatos().getCodconcepto()
							.equals(new BigDecimal(ConstantsConceptos.CODCPTO_SISTCULTIVO))) {
						logger.debug("La parcela tiene sistema de cultivo");

						// Si el sistema de cultivo es igual que el
						// introducido en el filtro de busqueda se inserta
						// la parcela en la lista
						if (datvar.getValor().equals(sistcultivo)) {
							// Si la lista final no tiene la parcela
							// encontrada la meto
							if (!listParcFinal.contains(par)) {
								listParcFinal.add(par);
							}
						}
					}
				}
			}
		}
		return listParcFinal;
	}

	/* Sacamos c�digo fuera para descargar la funci�n de ifs/fors */
	private List<Parcela> obtenerListParcFinalRdtoHist(List<Parcela> listparc, String RdtoHist) {

		List<Parcela> listParcFinalRdtoHist = new ArrayList<Parcela>();

		/* P0078877 ** MODIF TAM (17.11.2021) ** Inicio */
		BigDecimal plantones = new BigDecimal(1);
		
		/* Si el tipo de Rendimiento a filtrar es RdtoHistorico, debemos filtrar tambi�n para que no salgan los plantones */
		if (RdtoHist.equals("4")){
			for (Parcela par : listparc) {
				for (CapitalAsegurado cap : par.getCapitalAsegurados()) {
					/* No salgan en la lista los Plantones */
					if (cap.getTipoCapital().getCodtipocapital().compareTo(plantones) != 0) { 
						for (int i = 0; i < cap.getCapAsegRelModulos().size(); i++) {
							CapAsegRelModulo capAsegRelMod = (CapAsegRelModulo) cap.getCapAsegRelModulos().toArray()[i];
							if ((capAsegRelMod != null && new Long(RdtoHist).equals(capAsegRelMod.getTipoRdto()))
									|| (RdtoHist.equals("0"))) {
								if (!listParcFinalRdtoHist.contains(par)) {
									listParcFinalRdtoHist.add(par);
								}
							}
						}
					}	
				}
			}
	
		}else {
			for (Parcela par : listparc) {
				for (CapitalAsegurado cap : par.getCapitalAsegurados()) {
					for (int i = 0; i < cap.getCapAsegRelModulos().size(); i++) {
						CapAsegRelModulo capAsegRelMod = (CapAsegRelModulo) cap.getCapAsegRelModulos().toArray()[i];
						if ((capAsegRelMod != null && new Long(RdtoHist).equals(capAsegRelMod.getTipoRdto()))
								|| (RdtoHist.equals("0"))) {
							if (!listParcFinalRdtoHist.contains(par)) {
								listParcFinalRdtoHist.add(par);
							}
						}
					}
				}
			}
		}
		return listParcFinalRdtoHist;
	}

	private void deleteDistCoste(Poliza poliza, String codModulo, BigDecimal fila) throws DAOException {
		boolean encontrado = false;

		Set<DistribucionCoste> colDistCostes = poliza.getDistribucionCostes();
		Iterator<DistribucionCoste> it = colDistCostes.iterator();
		while (it.hasNext() && !encontrado) {
			DistribucionCoste distCoste = it.next();
			if (distCoste.getCodmodulo().equals(codModulo) && distCoste.getFilacomparativa().compareTo(fila) == 0) {
				distribucionCosteDAO.deleteDistribucionCoste(distCoste.getPoliza().getIdpoliza(),
						distCoste.getCodmodulo(), distCoste.getFilacomparativa());
				encontrado = true;
			}
		}
	}

	private void deleteDistCoste2015(Poliza poliza, String codModulo, BigDecimal fila, Long idComparativa)
			throws DAOException {

		Set<DistribucionCoste2015> colDistCostes = poliza.getDistribucionCoste2015s();
		Iterator<DistribucionCoste2015> it = colDistCostes.iterator();
		while (it.hasNext()) {
			DistribucionCoste2015 distCoste = it.next();
			if (distCoste.getCodmodulo().equals(codModulo) && distCoste.getFilacomparativa().compareTo(fila) == 0
					&& distCoste.getIdcomparativa().compareTo(new BigDecimal(idComparativa)) == 0) {
				it.remove();
				distribucionCosteDAO.deleteDistribucionCoste2015(distCoste.getPoliza().getIdpoliza(),
						distCoste.getCodmodulo(), distCoste.getIdcomparativa().longValue());
			}
		}
	}

	/* MODIF TAM (28.10.2021) ** SONAR Q ** Inicio */
	/*
	 * Sacamos a otra funci�n el proceso de eliminar parcela, para descargar el
	 * handle de ifs
	 */
	private boolean obtenercambiaUsu(Usuario usu, Usuario usuNuevo) {

		boolean cambiaUsu = false;

		if (usu.getExterno().equals(Constants.USUARIO_EXTERNO)) {
			if (!usuNuevo.getSubentidadMediadora().getId().getCodentidad()
					.equals(usu.getSubentidadMediadora().getId().getCodentidad())
					|| !usuNuevo.getSubentidadMediadora().getId().getCodsubentidad()
							.equals(usu.getSubentidadMediadora().getId().getCodsubentidad())) {

				cambiaUsu = false;

			} else if (!usuNuevo.getOficina().getId().getCodentidad()
					.equals(usu.getOficina().getId().getCodentidad())) {
				cambiaUsu = false;

			} else {
				cambiaUsu = true;
			}
		} else {
			cambiaUsu = true;
		}
		return cambiaUsu;
	}

	private Map<String, Object> obtenerParametros(Usuario usu, Usuario usuNuevo) {

		final Map<String, Object> params = new HashMap<String, Object>();

		if (usu.getExterno().equals(Constants.USUARIO_EXTERNO)) {
			if (!usuNuevo.getSubentidadMediadora().getId().getCodentidad()
					.equals(usu.getSubentidadMediadora().getId().getCodentidad())
					|| !usuNuevo.getSubentidadMediadora().getId().getCodsubentidad()
							.equals(usu.getSubentidadMediadora().getId().getCodsubentidad())) {

				params.put(ALERT, bundle.getString("mensaje.cambioUsuario.externo.esMediadora.KO"));
			} else if (!usuNuevo.getOficina().getId().getCodentidad()
					.equals(usu.getOficina().getId().getCodentidad())) {

				params.put(ALERT, bundle.getString("mensaje.cambioUsuario.externo.entidad.KO"));
			}
		}
		return params;
	}

	/* MODIF TAM (28.10.2021) ** SONAR Q ** Inicio */
	/*
	 * Sacamos a otra funci�n el proceso de eliminar parcela, para descargar el
	 * handle de ifs
	 */
	private String obtenerxml(Clob fichero) {

		// Recuperamos el Clob y lo convertimos en String
		String xml = WSUtils.convertClob2String(fichero);

		// Se comprueba si existe cabecera, sino se inserta al principio
		if (xml.indexOf("<?xml version=\"1.0") == -1) {
			String cabecera = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
			xml = cabecera + xml;
		}

		// Se Reempla
		String namespace = "http://www.agroseguro.es/AcuseRecibo";
		String reemplazar1 = "<AcuseRecibo xmlns=\"" + namespace + "\"";

		String reemplazar2 = "</AcuseRecibo>";
		if (xml.indexOf("<xml-fragment") == -1) {
			if (xml.indexOf("http://www.agroseguro.es/AcuseRecibo") == -1) {
				// Buscamos Acuse Recibo
				xml = xml.replace("<AcuseRecibo", reemplazar1);
			}
		} else {

			xml = xml.replace("<xml-fragment", reemplazar1).replace("</xml-fragment>", reemplazar2)
					.replace("xmlns:acus=\"http://www.agroseguro.es/AcuseRecibo\"", "").replace("acus:", "");
		}

		return xml;

	}

	private es.agroseguro.acuseRecibo.Error[] obtenerError(String referencia, Clob fichero,
			AcuseReciboDocument acuseRecibo) throws BusinessException {

		es.agroseguro.acuseRecibo.Error[] errores = null;

		if (fichero == null) {
			errores = new es.agroseguro.acuseRecibo.Error[0];
		} else {

			/* MODIF TAM (28.10.2021) ** SONAR Q ** Inicio */
			/*
			 * Sacamos a otra funci�n el proceso de eliminar parcela, para descargar el
			 * handle de ifs
			 */
			String xml = obtenerxml(fichero);
			/* MODIF TAM (28.10.2021) ** SONAR Q ** Fin */

			try {

				acuseRecibo = AcuseReciboDocument.Factory.parse(new StringReader(xml)); // String
																						// parseado a
																						// AcuseReciboDocument
			} catch (Exception e) {
				logger.error("Se ha producido un error al recuperar el XML de Acuse de Recibo", e);
				throw new BusinessException("Error al convertir el XML a XML Bean", e);
			}

			if (acuseRecibo != null) {

				AcuseRecibo ac = acuseRecibo.getAcuseRecibo();
				ArrayList<es.agroseguro.acuseRecibo.Error> ArrayE = new ArrayList<es.agroseguro.acuseRecibo.Error>();

				// Recorremos Acuse de Recibo para hacer Array con
				// errores

				for (int i = 0; i < ac.getDocumentoArray().length; i++) {
					Documento documentoRecibido = ac.getDocumentoArray(i);
					int j = 0;

					// Si el documento del acuse de recibo tiene estado
					// 2 (rechazado) y coincide
					// "idPoliza + linea + plan"
					if (documentoRecibido.getEstado() == Constants.ACUSE_RECIBO_ESTADO_RECHAZADO
							&& documentoRecibido.getId().equals(referencia)) {
						// Formamos la lista de Errores
						while (j < documentoRecibido.getErrorArray().length) {
							try {
								ArrayE.add((es.agroseguro.acuseRecibo.Error) documentoRecibido.getErrorArray(j));
								j = j + 1;

							} catch (Exception ex) {
								logger.error("Se ha producido un error al recuperar los Documentos del Acuse de Recibo",
										ex);
								throw new BusinessException("Se ha producido un error al visualizar Acuse de Recibo ",
										ex);
							}
						}
					}
				}

				errores = new es.agroseguro.acuseRecibo.Error[ArrayE.size()];
				for (int i = 0; i < ArrayE.size(); i++) {
					errores[i] = ArrayE.get(i);
				}

			} else {
				logger.error("--El Acuse Recibo es null");
				errores = new es.agroseguro.acuseRecibo.Error[0];
			}
		}

		return errores;
	}

	/**
	 * @throws DAOException
	 **/
	private void copiarParcela(Parcela par, Poliza polizaBean, List<ClaseDetalle> lstClaseDetalle,
			Map<Long, List<BigDecimal>> maxPrecioProduccion) throws DAOException {

		Parcela nPar = new Parcela();

		nPar.setVariedad(par.getVariedad());
		nPar.setTermino(par.getTermino());
		nPar.setPoliza(polizaBean);
		nPar.setIdparcelaestructura(par.getIdparcelaestructura());
		nPar.setPoligono(par.getPoligono());
		nPar.setParcela(par.getParcela());
		nPar.setCodprovsigpac(par.getCodprovsigpac());
		nPar.setCodtermsigpac(par.getCodtermsigpac());
		nPar.setAgrsigpac(par.getAgrsigpac());
		nPar.setZonasigpac(par.getZonasigpac());
		nPar.setPoligonosigpac(par.getPoligonosigpac());
		nPar.setParcelasigpac(par.getParcelasigpac());
		nPar.setRecintosigpac(par.getRecintosigpac());
		nPar.setNomparcela(par.getNomparcela());
		nPar.setCodcultivo(par.getCodcultivo());
		nPar.setCodvariedad(par.getCodvariedad());
		nPar.setHoja(par.getHoja());
		nPar.setNumero(par.getNumero());
		nPar.setTipoparcela(par.getTipoparcela());
		nPar.setAltaencomplementario(par.getAltaencomplementario());
		seleccionPolizaDao.saveParcela2(nPar);

		// CAPITALES
		for (CapitalAsegurado cap : par.getCapitalAsegurados()) {
			CapitalAsegurado nCap = new CapitalAsegurado();

			nCap.setTipoCapital(cap.getTipoCapital());
			nCap.setParcela(nPar);
			nCap.setSuperficie(cap.getSuperficie());
			nCap.setPrecio(cap.getPrecio());
			nCap.setProduccion(cap.getProduccion());
			nCap.setPreciomodif(cap.getPreciomodif());
			nCap.setProduccionmodif(cap.getProduccionmodif());
			nCap.setAltaencomplementario(cap.getAltaencomplementario());
			nCap.setIncrementoproduccion(cap.getIncrementoproduccion());
			seleccionPolizaDao.saveCapAsegurado(nCap);

			// DATOS VARIABLES
			for (DatoVariableParcela dat : cap.getDatoVariableParcelas()) {
				DatoVariableParcela nDat = new DatoVariableParcela();

				/**** SONAR Q ** MODIF TAM (28.10.2021) ** Inicio ***/
				/** Lo sacamos a otra funcion para descargar de ifs/for el metodo */
				nDat = informarDatVarPar(dat, lstClaseDetalle);

				nDat.setCapitalAsegurado(nCap);
				/**** SONAR Q ** MODIF TAM (28.10.2021) ** Fin ***/

				seleccionPolizaDao.saveDatoVarParcela(nDat);
				seleccionPolizaDao.evict(nDat);
			}

			/**** SONAR Q ** MODIF TAM (28.10.2021) ** Inicio ***/
			/** Lo sacamos a otra funcion para descargar de ifs/for el metodo */
			// un cap.aseg.rel.modulo por modulo de la poliza
			guardaCapAsegRelModulo(polizaBean, maxPrecioProduccion, cap, nCap);
			/**** SONAR Q ** MODIF TAM (28.10.2021) ** Fin ***/

			seleccionPolizaDao.evict(nCap);
		}

		seleccionPolizaDao.evict(nPar);
	}

	// un cap.aseg.rel.modulo por modulo de la poliza
	private void guardaCapAsegRelModulo(Poliza polizaBean, Map<Long, List<BigDecimal>> maxPrecioProduccion,
			CapitalAsegurado cap, CapitalAsegurado nCap) throws DAOException {

		for (ModuloPoliza modulo : polizaBean.getModuloPolizas()) {
			CapAsegRelModulo carm = new CapAsegRelModulo();
			carm.setPrecio(maxPrecioProduccion.get(cap.getIdcapitalasegurado()).get(0));
			carm.setProduccion(maxPrecioProduccion.get(cap.getIdcapitalasegurado()).get(1));
			carm.setCodmodulo(modulo.getId().getCodmodulo());
			carm.setCapitalAsegurado(nCap);

			seleccionPolizaDao.saveOrUpdate(carm);

		}
	}

	/***** Fin SONAR Q *****/
	private DatoVariableParcela informarDatVarPar(DatoVariableParcela dat, List<ClaseDetalle> lstClaseDetalle) {

		DatoVariableParcela nDat = new DatoVariableParcela();

		nDat.getDiccionarioDatos().setCodconcepto(dat.getDiccionarioDatos().getCodconcepto());

		// comprobar sistema cultivo de ambas clases, si el
		// sistema de cultivo no aplica en mi clase se
		// cambia al que aplique.
		if ("123".equals(dat.getDiccionarioDatos().getCodconcepto().toString())) {

			boolean existeSistCultDetalle = false;
			SistemaCultivo sc = null;

			for (ClaseDetalle cl : lstClaseDetalle) {
				if (cl.getSistemaCultivo() != null) {
					existeSistCultDetalle = true;
					if (sc == null) {
						// en caso de que exista sistema de
						// cultivo en la clase, nos quedamos
						// con el primero
						// por si tenemos que asignarselo al
						// valor del dato variable.
						sc = cl.getSistemaCultivo();
					}
					if (cl.getSistemaCultivo().getCodsistemacultivo().toString().equals(dat.getValor())) {
						nDat.setValor(dat.getValor());
						break;
					}
				}
			}

			// Si no hemos asignado el valor al sistema de
			// cultivo y en la clase tenemos valor para ese
			// DV => lo asignamos
			if (existeSistCultDetalle && nDat.getValor() == null) {
				nDat.setValor(sc.getCodsistemacultivo().toString());
			}
			// si no existe el sistema de cultivo en el
			// detalle => asignamos el valor del DV origen
			if (!existeSistCultDetalle) {
				nDat.setValor(dat.getValor());
			}
		} else {
			nDat.setValor(dat.getValor());
		}

		return nDat;
	}

	/* MODIF TAM (28.10.2021) ** SONAR Q ** Inicio */
	/*
	 * Sacamos a otra funci�n el proceso de eliminar parcela, para descargar el
	 * handle de ifs
	 */
	private Set<Long> execCambioMasivo(CambioMasivoVO cambioMasivoVO, Poliza poliza, Usuario usuario, String recalcular,
			Map<Long, List<Long>> mapaParcelasInstalaciones, boolean guardaSoloPrecioYProd,
			boolean recalcularRendimientoConSW)
			throws TransactionSystemException, SQLIntegrityConstraintViolationException, DAOException {

		Set<Long> colIdParcelasParaRecalculo = new HashSet<Long>();

		if (recalcularRendimientoConSW) {
			// Coleccion con los id
			colIdParcelasParaRecalculo = cambioMasivo(cambioMasivoVO, poliza, usuario, recalcular,
					mapaParcelasInstalaciones, guardaSoloPrecioYProd);
			obtenerHojaNumero(poliza);
			polizaDao.evict(poliza);

		} else {
			seleccionPolizaDao.cambioMasivo(cambioMasivoVO, poliza, usuario, false, null, recalcular,
					mapaParcelasInstalaciones, guardaSoloPrecioYProd);
		}

		return colIdParcelasParaRecalculo;
	}

	/* MODIF TAM (28.10.2021) ** SONAR Q ** Inicio */
	/*
	 * Sacamos a otra funci�n el proceso de eliminar parcela, para descargar el
	 * handle de ifs
	 */
	private Set<Long> obtenerParcelas(Poliza poliza, CambioMasivoVO cambioMasivoVO, int i, boolean tieneEstructuras,
			Map<Long, List<Long>> mapaParcelasInstalaciones, Usuario usuario, boolean guardaSoloPrecioYProd,
			String recalcular) throws SQLIntegrityConstraintViolationException, DAOException {

		Set<Long> colIdParcelasParaRecalculo = new HashSet<Long>();

		for (Parcela parcela : poliza.getParcelas()) {
			if (cambioMasivoVO.getListaParcelas().contains(parcela.getIdparcela())) {

				seleccionPolizaDao.modificarParcelaCambioMasivo(cambioMasivoVO, i, parcela,
						cambioMasivoVO.getMaxProduccion(), cambioMasivoVO.getMinProduccion(), usuario,
						guardaSoloPrecioYProd);

				if (tieneEstructuras && mapaParcelasInstalaciones.containsKey(parcela.getIdparcela())) {
					List<Parcela> lstInstalaciones = new ArrayList<Parcela>();
					List<Long> lstIdsInstall = (List<Long>) mapaParcelasInstalaciones.get(parcela.getIdparcela());
					if (lstIdsInstall != null && lstIdsInstall.size() > 0) {
						lstInstalaciones = seleccionPolizaDao.getInstalaciones(lstIdsInstall);
						seleccionPolizaDao.modificarInstalacionesCambioMasivo(cambioMasivoVO, lstInstalaciones,
								usuario);
					}
				}

				if (recalcular.equalsIgnoreCase("true")) {
					colIdParcelasParaRecalculo.add(parcela.getIdparcela());
				}
				i++;
			}
		}

		return colIdParcelasParaRecalculo;
	}

	/* MODIF TAM (28.10.2021) ** SONAR Q ** Inicio */
	/*
	 * Sacamos a otra funci�n el proceso de eliminar parcela, para descargar el
	 * handle de ifs
	 */
	private boolean validarPoseeProvisionalODefinitivo(Poliza poliza) {

		boolean poseeProvODef = false;

		if (null != poliza.getEstadoPoliza().getIdestado()) {
			if (poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL)
					|| poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA)) {
				poseeProvODef = true;
			}
		}
		return poseeProvODef;
	}

	private boolean validarPoseeEnv(Poliza poliza) {

		boolean poseeEnv = false;

		if (null != poliza.getEstadoPoliza().getIdestado()) {
			if (poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA)
					|| poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_ENVIADA_ERRONEA))
				poseeEnv = true;
		}
		return poseeEnv;
	}

	/* MODIF TAM (28.10.2021) ** SONAR Q ** Inicio */
	/* Sacamos a otra funci�n par descargar de ifs/fors */
	@SuppressWarnings("unchecked")
	private boolean obtenerResult(Iterator<Poliza> itListaPolizas, List<Poliza> listaPolizas) {

		while (itListaPolizas.hasNext()) {

			Poliza poliza = (Poliza) itListaPolizas.next();
			if (null != poliza.getCodmodulo() && null != poliza.getLinea().getLineaseguroid()) {

				ModuloCompatibleFiltro filtro = new ModuloCompatibleFiltro();
				ModuloCompatibleCe moduloCompatibleCe = new ModuloCompatibleCe();
				Long lineaSeguroId = poliza.getLinea().getLineaseguroid();
				String codModulo = poliza.getCodmodulo();
				moduloCompatibleCe.getLinea().setLineaseguroid(lineaSeguroId);
				moduloCompatibleCe.getModuloPrincipal().getId().setCodmodulo(codModulo);
				filtro.setModuloCompatibleCe(moduloCompatibleCe);

				List<ModuloCompatibleCe> listaModulosCompatibles = this.seleccionPolizaDao.getObjects(filtro);
				Integer numModulosCompatiblesDisponibles = listaModulosCompatibles.size();

				// Si el numero de modulos compatibles que permite es mayor de
				// 1, mirar para cada uno si ya ha sido creada su poliza.
				// Si no ha sido creada, se permite el alta.
				if (numModulosCompatiblesDisponibles > 1) {
					Integer numModulosCompatiblesCreados = 0;
					Iterator<Poliza> itSubListaPolizas = listaPolizas.iterator();
					while (itSubListaPolizas.hasNext()) {
						Poliza subPoliza = (Poliza) itSubListaPolizas.next();

						// Miramos cuantos modulos tenemos con el codModulo y
						// lineaSeguroId dentro de la lista de polizas
						// Por cada uno que encontremos incrementamos el
						// contador que indica que tenemos un modulo
						// compatible.
						if ((null != subPoliza.getCodmodulo() && null != subPoliza.getLinea().getLineaseguroid()
								&& subPoliza.getCodmodulo().equals(codModulo)
								&& subPoliza.getLinea().getLineaseguroid().equals(lineaSeguroId))) {
							numModulosCompatiblesCreados++;
						}
					}
					if (numModulosCompatiblesCreados < numModulosCompatiblesDisponibles) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/* MODIF TAM (28.10.2021) ** SONAR Q ** Inicio */
	/*
	 * Sacamos a otra funci�n el proceso de eliminar parcela, para descargar el
	 * handle de ifs
	 */
	private void guardarCapAsegRelModulo(Poliza poliza, CapitalAsegurado ca, Map<Long, List<BigDecimal>> mapProdPrec) {
		// recorro los modulos de la poliza
		for (ModuloPoliza mp : poliza.getModuloPolizas()) {
			// si no hay precio produccion para ese modulo, meto
			// el mayor
			if (!hasCapAsegRelModulo(ca, mp.getId().getCodmodulo())) {
				CapAsegRelModulo camr = new CapAsegRelModulo();
				camr.setCapitalAsegurado(ca);
				camr.setCodmodulo(mp.getId().getCodmodulo());

				List<BigDecimal> prePro = mapProdPrec.get(ca.getIdcapitalasegurado());
				if (prePro != null) {
					camr.setPrecio(prePro.get(0));
					camr.setProduccion(prePro.get(1));
				}

				try {
					seleccionPolizaDao.saveOrUpdate(camr);
				} catch (Exception ex) {

				}
			}
		}
	}

	/**
	 * SONAR Q ** MODIF TAM(03.11.2021)
	 * 
	 * @throws DAOException
	 **/
	/* A�adimos nuevo m�todo para descargar de ifs/for */
	private void actualizarTipoRdto(ContratacionRendimientosResponse respuesta, List<Parcela> parcelasEnPantalla)
			throws DAOException {

		PolizaDocument pd = respuesta.getRendimientoPolizaDocument();

		Cosecha[] cosechas = pd.getPoliza().getCosechaArray();
		for (Parcela pp : parcelasEnPantalla) {
			for (Cosecha c : cosechas) {
				for (es.agroseguro.seguroAgrario.rendimientosCalculo.Parcela p : c.getParcelaArray()) {
					if ((pp.getHoja().equals(p.getHoja()) && (pp.getNumero().equals(p.getNumero())))) {
						for (CapitalAsegurado ca : pp.getCapitalAsegurados()) {
							for (CapAsegRelModulo carm : ca.getCapAsegRelModulos()) {
								/* P0078877 ** MODIF TAM (18/11/2021) ** Defecto N�4 */
								/*obtenemos el tipo de Rendimiento que ya tiene la parcela */
								Long tipoRendCapAnterior = carm.getTipoRdto();
								BigDecimal produccionAnt = ca.getProduccion();
								
								Long tipoRdto = obtenerTipoRendimiento(p, tipoRendCapAnterior);
								Long produccion = obtenerProduccion(p, tipoRendCapAnterior, produccionAnt);
								
								/* P0078877 ** MODIF TAM (18/11/2021) ** Defecto N�4 */
								carm.setTipoRdto(tipoRdto);
								carm.setProduccion(BigDecimal.valueOf(produccion));
								carm.getCapitalAsegurado().setProduccion(BigDecimal.valueOf(produccion));
								capitalAseguradoDao.actualizarTipoRdto(carm.getId(), tipoRdto, produccion);
							}
						}
					}
				}
			}
		}
	}
	
	/* A�adimos nuevo m�todo para descargar de ifs/for */
	private void actualizarTipoRdtoOri(ContratacionRendimientosResponse respuesta, List<Parcela> parcelasEnPantalla)
			throws DAOException {

		PolizaDocument pd = respuesta.getRendimientoPolizaDocument();
	
		Cosecha[] cosechas = pd.getPoliza().getCosechaArray();
		for (Parcela pp : parcelasEnPantalla) {
			for (Cosecha c : cosechas) {
				for (es.agroseguro.seguroAgrario.rendimientosCalculo.Parcela p : c.getParcelaArray()) {
					if ((pp.getHoja().equals(p.getHoja()) && (pp.getNumero().equals(p.getNumero())))) {
						for (CapitalAsegurado ca : pp.getCapitalAsegurados()) {
							for (CapAsegRelModulo carm : ca.getCapAsegRelModulos()) {
								
								/* P0078877 ** MODIF TAM (18/11/2021) ** Defecto N�4 */
								/*obtenemos el tipo de Rendimiento que ya tiene la parcela */
								Long tipoRendCapAnterior = carm.getTipoRdto();
								BigDecimal produccionAnt = carm.getProduccion();
								
								Long tipoRdto = obtenerTipoRdtoOrientativo(p, tipoRendCapAnterior);
								Long produccion = obtenerProdRdtoOrientativo(p, tipoRendCapAnterior, produccionAnt);
								/* P0078877 ** MODIF TAM (18/11/2021) ** Defecto N�4 */

								carm.setTipoRdto(tipoRdto);
								carm.setProduccion(BigDecimal.valueOf(produccion));
								carm.getCapitalAsegurado().setProduccion(BigDecimal.valueOf(produccion));
								capitalAseguradoDao.actualizarTipoRdto(carm.getId(), tipoRdto, produccion);
							}
						}
					}
				}
			}
		}
	}

	private VistaImportesPorGrupoNegocio obtenerVistaImpPorGrNegocio(DistribucionCoste distribCostes,
			Poliza polizaActual) {

		VistaImportesPorGrupoNegocio vistaImpoPorGrNeg = null;

		if (distribCostes != null) {
			vistaImpoPorGrNeg = new VistaImportesPorGrupoNegocio();
			vistaImpoPorGrNeg.setCodGrupoNeg("1");
			vistaImpoPorGrNeg.setDescGrupNeg(getDesGruponegocio('1'));

			vistaImpoPorGrNeg.setPctDescContColectiva(
					StringUtils.formatPercent(polizaActual.getColectivo().getPctdescuentocol()));
			vistaImpoPorGrNeg.setPrimaComercial(NumberUtils.formatear(distribCostes.getPrimacomercial(), 2));
			vistaImpoPorGrNeg.setPrimaNeta(NumberUtils.formatear(distribCostes.getPrimaneta(), 2));
			vistaImpoPorGrNeg.setCosteNeto(NumberUtils.formatear(distribCostes.getCosteneto(), 2));
			vistaImpoPorGrNeg
					.setBonifMedidaPreventiva(NumberUtils.formatear(distribCostes.getBonifmedpreventivas(), 2));
			vistaImpoPorGrNeg.setDescuentoContColectiva(NumberUtils.formatear(distribCostes.getDtocolectivo(), 2));

			if (distribCostes.getBonifasegurado() != null) {
				vistaImpoPorGrNeg.setBonifAsegurado(NumberUtils.formatear(distribCostes.getBonifasegurado(), 2));
				vistaImpoPorGrNeg.setPctBonifAsegurado(StringUtils.formatPercent(distribCostes.getPctbonifasegurado()));
			}

			if (distribCostes.getRecargoasegurado() != null) {
				vistaImpoPorGrNeg.setRecargoAsegurado(NumberUtils.formatear(distribCostes.getRecargoasegurado(), 2));
				vistaImpoPorGrNeg
						.setPctRecargoAsegurado(NumberUtils.formatear(distribCostes.getPctrecargoasegurado(), 2));
			}

			if (distribCostes.getReaseguro() != null) {
				vistaImpoPorGrNeg.setConsorcioReaseguro(NumberUtils.formatear(distribCostes.getReaseguro(), 2));
				vistaImpoPorGrNeg.setConsorcioRecargo(NumberUtils.formatear(distribCostes.getRecargo(), 2));
			}

		}
		return vistaImpoPorGrNeg;
	}

	/****/
	/** SONAR Q ** MODIF TAM(03.11.2021) **/
	/* A�adimos nuevo m�todo para descargar de ifs/for */
	private String obtenerOfiAseg(Asegurado asegurado, Colectivo colectivo) {

		String cccAseg = "";
		String ofiAseg = "";

		for (DatoAsegurado datAseg : asegurado.getDatoAsegurados()) {
			if (datAseg.getLineaCondicionado().getCodlinea().toString()
					.equals(colectivo.getLinea().getCodlinea().toString())) {
				if (datAseg.getCcc() != null) {
					cccAseg = datAseg.getCcc();
					if (!VACIO.equals(cccAseg)) {
						ofiAseg = cccAseg.substring(4, 8);
						return ofiAseg;

					}
				}
			}
		}

		if (VACIO.equals(ofiAseg)) { // se coge la oficina por defecto cuya linea es 999 del asegurado
			for (DatoAsegurado datAseg : asegurado.getDatoAsegurados()) {
				if ("999".equals(datAseg.getLineaCondicionado().getCodlinea().toString())) {
					if (datAseg.getCcc() != null) {
						cccAseg = datAseg.getCcc();
						if (!VACIO.equals(cccAseg)) {
							ofiAseg = cccAseg.substring(4, 8);
							return ofiAseg;
						}
					}
				}
			}
		}
		return ofiAseg;
	}
	
	/* SONAR Q */
	private String obtenerxml(AnexoModificacion anexo, Poliza poliza, ModuloPoliza mp, Usuario usuario, Set<Long> colIdParcelasParaRecalculo) throws Exception {
		String xmlPoliza = "";
		
		if (anexo != null) {
			/* ESC-16025 ** MODIF TAM (23.11.2021) ** Inicio */
			/* Pasamos parametro tipo boolean para saber que venimos de Calculo de Rendimiento Orientativo */
			boolean calcRendOriHist = true;

			xmlPoliza = SWContratacionRendimientosHelper.polizaRendimientosAnexoToXml(anexo,
					colIdParcelasParaRecalculo, usuario, xmlAnexoModDao, cpmTipoCapitalDao, polizaCopyDao, calcRendOriHist);
			/* ESC-16025 ** MODIF TAM (23.11.2021) ** Fin*/
		} else {
			xmlPoliza = SWContratacionRendimientosHelper.polizaRendimientosToXml(null, poliza, mp,
					cpmTipoCapitalDao, polizaDao, colIdParcelasParaRecalculo);
		}
		return xmlPoliza;
	}
	
	
	/** SONAR Q ** MODIF TAM(03.11.2021) 
	 * @throws DAOException **/
	/* A�adimos nuevo m�todo para descargar de ifs/for */
	private void actualizarTipoRdtoOriAnexo(ContratacionRendimientosResponse respuesta, 
				List<com.rsi.agp.dao.tables.anexo.CapitalAsegurado> capitalesEnPantalla) throws DAOException {
	
		PolizaDocument pd = respuesta.getRendimientoPolizaDocument();
		for (com.rsi.agp.dao.tables.anexo.CapitalAsegurado cap : capitalesEnPantalla) {
			Cosecha[] cosechas = pd.getPoliza().getCosechaArray();
	
			for (Cosecha c : cosechas) {
				for (es.agroseguro.seguroAgrario.rendimientosCalculo.Parcela p : c.getParcelaArray()) {
					if (p.getHoja() == cap.getParcela().getHoja().intValue()
							&& p.getNumero() == cap.getParcela().getNumero().intValue()) {
						/* P0078877 ** MODIF TAM (18/11/2021) ** Defecto N�4 */
						/*obtenemos el tipo de Rendimiento que ya tiene la parcela */
						Long tipoRendCapAnterior = cap.getTipoRdto();
						BigDecimal produccionAnt = cap.getProduccion();
						
						Long tipoRdto = obtenerTipoRdtoOrientativo(p, tipoRendCapAnterior);
						Long produccion = obtenerProdRdtoOrientativo(p, tipoRendCapAnterior, produccionAnt);
						/* P0078877 ** MODIF TAM (18/11/2021) ** Defecto N�4 */
						
						cap.setTipoRdto(tipoRdto);
						
						cap.setProduccion(BigDecimal.valueOf(produccion));
						cap.setTipomodificacion(Constants.MODIFICACION);
						cap.getParcela().setTipomodificacion(Constants.MODIFICACION);
						capitalAseguradoDao.actualizarTipoRdtoAnexo(cap.getId(), tipoRdto, produccion,
								cap.getParcela().getId());
					}
				}
			}
		}
	}



	/** SONAR Q ** MODIF TAM (28.10.2021) ** Fin **/

	public void setXmlAnexoModDao(IXmlAnexoModificacionDao xmlAnexoModDao) {
		this.xmlAnexoModDao = xmlAnexoModDao;
	}

	public void setPolizaCopyDao(IPolizaCopyDao polizaCopyDao) {
		this.polizaCopyDao = polizaCopyDao;
	}

	public void setCalculoPrecioProduccionManager(CalculoPrecioProduccionManager calculoPrecioProduccionManager) {
		this.calculoPrecioProduccionManager = calculoPrecioProduccionManager;
	}
}