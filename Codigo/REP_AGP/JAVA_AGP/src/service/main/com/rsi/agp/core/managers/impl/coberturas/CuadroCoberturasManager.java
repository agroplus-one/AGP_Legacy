package com.rsi.agp.core.managers.impl.coberturas;

import java.math.BigDecimal;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlObject;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.ICuadroCoberturasManager;
import com.rsi.agp.core.managers.impl.ModuloManager;
import com.rsi.agp.core.managers.impl.SeleccionComparativasSWManager;
import com.rsi.agp.core.managers.impl.anexoMod.PolizaActualizadaResponse;
import com.rsi.agp.core.managers.impl.anexoMod.SWAnexoModificacionHelper;
import com.rsi.agp.core.managers.impl.anexoMod.solicitud.ISolicitudModificacionManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.DatoVariableComparator;
import com.rsi.agp.core.util.ModuloFilaViewComparator;
import com.rsi.agp.core.util.VistaComparativaComparator;
import com.rsi.agp.core.util.WSRUtils;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.util.CoberturasUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.filters.poliza.VistaComparativaFiltro;
import com.rsi.agp.dao.models.admin.IClaseDao;
import com.rsi.agp.dao.models.coberturas.ICuadroCoberturasDao;
import com.rsi.agp.dao.models.config.IClaseDetalleDao;
import com.rsi.agp.dao.models.cpl.IAseguradoAutorizadoDao;
import com.rsi.agp.dao.models.cpl.ICaracteristicasModuloDao;
import com.rsi.agp.dao.models.cpm.ICPMTipoCapitalDAO;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.models.poliza.IRiesgoCubiertoModuloDao;
import com.rsi.agp.dao.models.poliza.ganado.ISeleccionComparativaSWDao;
import com.rsi.agp.dao.models.poliza.ganado.SeleccionComparativaSWDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.cgen.ConceptoPpalModulo;
import com.rsi.agp.dao.tables.cgen.SistemaCultivo;
import com.rsi.agp.dao.tables.coberturas.CuadroCobertura;
import com.rsi.agp.dao.tables.cpl.AseguradoAutorizado;
import com.rsi.agp.dao.tables.cpl.CaracteristicaModulo;
import com.rsi.agp.dao.tables.cpl.ConceptoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloCeldaView;
import com.rsi.agp.dao.tables.cpl.ModuloFilaView;
import com.rsi.agp.dao.tables.cpl.ModuloFilaVinculacionView;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.ModuloValorCeldaView;
import com.rsi.agp.dao.tables.cpl.ModuloView;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.TablaExternaCultivo;
import com.rsi.agp.dao.tables.cpm.CPMTipoCapital;
import com.rsi.agp.dao.tables.poliza.ComparativaFija;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.VistaComparativas;
import com.rsi.agp.dao.tables.poliza.VistaComparativasId;

import es.agroseguro.modulosYCoberturas.Cobertura;
import es.agroseguro.modulosYCoberturas.DatoVariable;
import es.agroseguro.modulosYCoberturas.Fila;
import es.agroseguro.modulosYCoberturas.ModulosYCoberturas;
import es.agroseguro.modulosYCoberturas.ModulosYCoberturasDocument;
import es.agroseguro.modulosYCoberturas.Valor;
import es.agroseguro.modulosYCoberturas.VinculacionFila;

public class CuadroCoberturasManager implements ICuadroCoberturasManager {
	
	private static final String TD = "</td>";
	private static final String TD_CLASS_LITERALBORDE_ALIGN_CENTER_WIDTH_15 = "<td class='literalborde' align='center' width='15%'>";
	
	private final String ELEGIBLE = "E";
	private final String BASICA = "B";

	private static final Log logger = LogFactory.getLog(CuadroCoberturasManager.class);
	
	private IClaseDao claseDao;
	private IClaseDetalleDao claseDetalleDao;
	private IAseguradoAutorizadoDao aseguradoAutorizadoDao;
	private ICPMTipoCapitalDAO cpmTipoCapitalDao;
	private IRiesgoCubiertoModuloDao riesgoCubiertoModuloDao;
	private ICaracteristicasModuloDao caracteristicaModuloDao;
	private ICuadroCoberturasDao cuadroCoberturasDao;
	private ModuloManager moduloManager;
	
	private IPolizaDao polizaDao;
	
	private ISolicitudModificacionManager solicitudModificacionManager;
	private ISeleccionComparativaSWDao seleccionComparativaSWDao;
	private SeleccionComparativasSWManager seleccionComparativasSWManager;
	
	/**
	 * Metodo para obtener el objeto para pintar el cuadro de coberturas mediante AJAX.
	 * @param modulo Objeto con la estrucutra del cuadro.
	 * @param idtabla Identificador de la capa en la que se incluira¡ el cuadro.
	 * @return Objeto JSON que representa un cuadro de coberturas.
	 * @throws JSONException
	 */
	public JSONObject getCoberturasJSON (ModuloView modulo, String idtabla) throws JSONException{
		JSONObject objeto = new JSONObject();
		objeto.put("modulo", moduloManager.crearTablaPoliza(modulo, idtabla));
		
		return objeto;
	}
	
	/**
	 * @param codmodulo
	 * @param idtabla
	 * @param poliza
	 * @param informes
	 * @return
	 * @throws JSONException
	 */
	public ModuloView getCoberturasModulo(String codmodulo, Poliza poliza, boolean informes) {
		
		List<Modulo> modulos = moduloManager.dameModulosDisponibles(poliza.getIdpoliza(), poliza.getLinea().getLineaseguroid(),
				 poliza.getTipoReferencia(), codmodulo, true, informes);
		
		if (modulos.size() > 0) {
			// Tenemos los modulos elegibles, procedemos a montar los datos de coberturas para la vista...
			
			//Montamos un mapa indexado por codmodulo y cuyo valor sera¡ una lista de 'ModuloView' que
			//representa cada una de las filas que se muestran para cada modulo.
			//DAA 19/07/2012 idAnexo
			List<ModuloView> vistaModulos = this.generaCondicionesModulos(poliza.getIdpoliza(), poliza.getLinea().getLineaseguroid(), 
					poliza.getLinea().getCodlinea().toString(), poliza.getClase(), poliza.getAsegurado().getNifcif(), null, modulos, informes);
			return vistaModulos.get(0);
		}

		return null;
	}
	
	/**
	 * @param codmodulo
	 * @param idtabla
	 * @param idlinea
	 * @param informes
	 * @return
	 * @throws JSONException
	 */
	public ModuloView getCoberturasModulo(String codmodulo, String idtabla, Long lineaseguroid, boolean informes) {
		
		List<Modulo> modulos = moduloManager.dameModulosDisponibles(null, lineaseguroid, null, codmodulo, true, informes);
		
		if (modulos.size() > 0) {
			// Tenemos los modulos elegibles, procedemos a montar los datos de
			// coberturas para la vista...
			List<ModuloView> vistaModulos = this.generaCondicionesModulos(null, null, 
					null, null, null, null, modulos, informes);
			return vistaModulos.get(0);
		}
		
		return null;
	}
	
	/**
	 * Metodo que devuelve un JSONObject con los datos de coberturas de una poliza principal o complementaria
	 * @param poliza
	 * @return
	 * @throws BusinessException
	 */
	public JSONObject getCoberturas(Poliza poliza)throws BusinessException{
		List<Modulo> modulos;
		List<ModuloView> vistaModulos = new ArrayList<ModuloView>();
		JSONObject objeto =  new JSONObject();
		try {
			//DAA 19/07/2012 idAnexo
			modulos = moduloManager.dameModulosDisponibles(poliza.getIdpoliza(), poliza.getLinea().getLineaseguroid(), 
					poliza.getTipoReferencia(), poliza.getCodmodulo(), true, false);
			if (modulos.size() > 0){
				vistaModulos = this.generaCondicionesModulos(poliza.getIdpoliza(), poliza.getLinea().getLineaseguroid(),
						poliza.getLinea().getCodlinea() + "", poliza.getClase(), poliza.getAsegurado().getNifcif(),
						null, modulos, false);
			}
			
			if(!vistaModulos.isEmpty()){
				for (ModuloView moduloView : vistaModulos) {
					if(moduloView.getCodModulo().equals(poliza.getCodmodulo())){
						if (poliza.getTipoReferencia().equals(Constants.MODULO_POLIZA_COMPLEMENTARIO)){
							objeto.put("tabla", crearTablaCpl(moduloView));
							objeto.put("descripcionCPL", moduloView.getDescripcionModulo());
						}
						else{
							objeto.put("tabla", crearTablaPpl(moduloView, poliza.getComparativaPolizas()));
							objeto.put("descripcion", moduloView.getDescripcionModulo());
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error("Se ha producido un error al recuperar las coberturas de la poliza complementaria: " + ex.getMessage());
			throw new BusinessException ("Se ha producido un error al recuperar las coberturas de la poliza complementaria",ex);
		}
		
		return objeto;
	}
	
	/**
	 * Metodo que devuelve un JSONObject con los datos de coberturas de una poliza principal
	 * @param poliza
	 * @return
	 * @throws BusinessException
	 */
	/*public JSONObject getCoberturasPpal(Poliza poliza) throws BusinessException{
		List<Modulo> modulos;
		HashMap<String, Object> modulosPpal = new HashMap<String, Object>();
		JSONObject objeto =  new JSONObject();
		try {
			//DAA 19/07/2012 idAnexo
			modulos = moduloManager.dameModulosDisponibles(poliza.getIdpoliza(), poliza.getLinea().getLineaseguroid(),
					poliza.getAsegurado().getNifcif(), poliza.getTipoReferencia(), poliza.getCodmodulo(), true, false);
			if (modulos.size() > 0){
				List<ModuloView> vistaModulos = this.generaCondicionesModulos(null, null, modulos, false);
				modulosPpal.put("plan", modulos.get(0).getLinea().getCodplan());
				modulosPpal.put("lineaDesc", modulos.get(0).getLinea().getCodlinea()+" - "+modulos.get(0).getLinea().getNomlinea());
				modulosPpal.put("idlinea", modulos.get(0).getLinea().getLineaseguroid());
				modulosPpal.put("coberturas", vistaModulos);
			}
			else {
				modulosPpal.put("coberturas", null);
			}
			Set<ComparativaPoliza> comparativasElegidas = poliza.getComparativaPolizas();
			
			if(modulosPpal.get("coberturas") != null){
				List<ModuloView> vistaModulosCpl = (List<ModuloView>) modulosPpal.get("coberturas");
				for (ModuloView moduloView : vistaModulosCpl) {
					if(moduloView.getCodModulo().equals(poliza.getCodmodulo())){
							objeto.put("tabla", crearTablaPpl(moduloView,comparativasElegidas));
							objeto.put("descripcion", moduloView.getDescripcionModulo());	
						}
					}
				}
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error al recuperar las coberturas de la poliza principal: " + ex.getMessage());
			throw new BusinessException ("Se ha producido un error al recuperar las coberturas de la poliza principal",ex);
		}
		return objeto;
	}*/

	@Override
	public List<ModuloView> generaCondicionesModulos(Long idpoliza, Long lineaseguroid, String codlinea, BigDecimal clase, 
			String nifAsegurado, Long idAnexo, List<Modulo> modulos, boolean informes){
		logger.debug("init - [metodo] generaCondicionesModulos");
		// Filtrar por fichvinculacionexterna  para sesgar o no
		List<ModuloView> listaModulos = new ArrayList<ModuloView>();
		
		// Montamos una lista de AseguradosAurorizados para comprobar posteriormente 
		// si la caracteristica elegible ha de estar tachada en la columna de garantizado.
		boolean checkGarantizado = false;
		boolean checkGarantizadoNif = false;
		List<AseguradoAutorizado> lstAsegGarantizadosAplicables = new ArrayList<AseguradoAutorizado>();
		// ASF - 17/10/2012 - Adaptaciones 314
		String comprobarAAC = "";
		if (idpoliza != null)
			comprobarAAC = this.claseDao.getComprobarAac(lineaseguroid, clase);

		if(idpoliza!=null && StringUtils.nullToString(comprobarAAC).equals("S")){
			checkGarantizado = this.aseguradoAutorizadoDao.checkAseguradoAutorizadoGarantizado(lineaseguroid);
		}
		
		if (checkGarantizado){
			checkGarantizadoNif = this.aseguradoAutorizadoDao.checkAseguradoAutorizadoNif(lineaseguroid, nifAsegurado);
			if (checkGarantizadoNif){
				lstAsegGarantizadosAplicables = this.aseguradoAutorizadoDao.lstAsegGarantizadosAplicables(lineaseguroid, nifAsegurado);
			}else{
				lstAsegGarantizadosAplicables = this.aseguradoAutorizadoDao.lstAsegGarantizadosAplicables(lineaseguroid,null);
			}
		}			
		
		//Para cada modulo vamos rellenando las propiedades.
		for (Modulo mod: modulos){
			ModuloView moduloView = getCondicionesModulo(idpoliza, lineaseguroid, codlinea, clase, nifAsegurado, 
					idAnexo, informes, checkGarantizado, lstAsegGarantizadosAplicables, mod);
			listaModulos.add(moduloView);
		}
		
		//Antes de devolver la lista debemos ver si tenemos tachados los valores vinculados.
		comprobarVinculados(listaModulos);
		
		logger.debug("end - [metodo] generaCondicionesModulos");
		return listaModulos;
	}

	/**
	 * Metodo para generar las condiciones de un unico modulo
	 * @param poliza
	 * @param idAnexo
	 * @param informes
	 * @param checkGarantizado
	 * @param lstAsegGarantizadosAplicables
	 * @param modulo
	 * @return
	 */
	private ModuloView getCondicionesModulo(Long idpoliza, Long lineaseguroid, String codlinea, BigDecimal clase, 
			String nifAsegurado, Long idAnexo, boolean informes, boolean checkGarantizado,
			List<AseguradoAutorizado> lstAsegGarantizadosAplicables, Modulo modulo) {
		
		Boolean sesgarTemp;
		ModuloView moduloView = new ModuloView();
		moduloView.setCodModulo(modulo.getId().getCodmodulo());
		moduloView.setDescripcionModulo(modulo.getId().getCodmodulo() + " - " + modulo.getDesmodulo());
		
		// Si viene de la generacion del informe, se carga la lista de CPM permitidos para la poliza
		List<BigDecimal> listaCPM = new ArrayList<BigDecimal> ();
		if (informes) 
			listaCPM = this.getListaCPMPermitidos(idpoliza,idAnexo,modulo.getId().getCodmodulo());

		for (ConceptoCubiertoModulo ccm: modulo.getConceptoCubiertoModulos()){
			moduloView.getListaCabeceras().add(ccm.getDiccionarioDatos().getNomconcepto());
		}
		
		moduloView.setTotcomplementarios(modulo.getTotcomplementarios());

		List<RiesgoCubiertoModulo> lstRcm = this.riesgoCubiertoModuloDao.getRiesgosCubiertosModuloPoliza(modulo.getId());
		List<RiesgoCubiertoModulo> lstRcmDef = new ArrayList<RiesgoCubiertoModulo>();
		
		// MPM - 16/10/12
		// Anhade al objeto de busqueda el listado de tipos de capital permitidos para la clase y modulo actual
		List<BigDecimal> listaTiposCapital = new ArrayList<BigDecimal>();
		if (idpoliza != null) {
			listaTiposCapital = getListaTiposCapital(lineaseguroid, clase, modulo.getId().getCodmodulo());
		}
		
		// Recorre el listado de riesgos cubiertos para el modulo y lineaseguroid seleccionados
		for (RiesgoCubiertoModulo rcmDef : lstRcm){
			// Si el CPM de este riesgo cubierto junto con los de la clase asociada a la poliza 
			// (codCultivo y codSistemaCultivo) no aparece en la tabla TB_CPM_TIPO_CAPITAL, no se muestra la linea correspondiente
			// en el cuadro de coberturas            	
			// Si el objeto poliza no es nulo, se obtienen el lineaseguroid y la clase de el
			if (idpoliza != null) {
				logger.debug("El objeto poliza está cargado, se obtienen los datos de el para comprobar si el CPM está permitido.");
				// Si el CPM no esta¡ permitido se va a la siguiente iteracion del bucle
				if (!this.isCPMPermitido(lineaseguroid, clase, modulo.getId().getCodmodulo(),
						rcmDef.getConceptoPpalModulo().getCodconceptoppalmod(), BigDecimal.ZERO, informes, listaCPM,
						listaTiposCapital)) {
					continue;
				}
			}
			// Si es nulo, se obtiene lineaseguroid del objeto modulo y la clase se pasa a null
			
			sesgarTemp=false;
			if  (StringUtils.nullToString(rcmDef.getFichvinculacionexterna()).equals("135") || idpoliza==null) { //tiene el 135
				sesgarTemp=sesgarFilaModulos(lineaseguroid, clase, rcmDef.getConceptoPpalModulo().getCodconceptoppalmod(), 
						rcmDef.getRiesgoCubierto().getId().getCodriesgocubierto(), rcmDef.getModulo().getId().getCodmodulo());
				if (sesgarTemp==true){
					lstRcmDef.add(rcmDef);
				}
			} else {
				lstRcmDef.add(rcmDef); // si no tiene el 135
			}
		}
		
		logger.debug("size original:" + lstRcm.size());
		logger.debug("size definitivo:" + lstRcmDef.size());
		
		// sesgar en la linea 309 los que no coinciden el sistema de cultivo de la clase con el conceptoPpalmod.
		if (idpoliza != null && "309".equals(codlinea)){
			List<RiesgoCubiertoModulo> lstRcmParaSesgar= new ArrayList<RiesgoCubiertoModulo>();
			boolean sesgarSecanoRegadio=false;
			for (RiesgoCubiertoModulo rcmDef2 : lstRcmDef){
				sesgarSecanoRegadio=false;
				if ((rcmDef2.getConceptoPpalModulo().getCodconceptoppalmod().equals(Constants.CONCEPTO_PPAL_MODULO_SECANO) ||
						rcmDef2.getConceptoPpalModulo().getCodconceptoppalmod().equals(Constants.CONCEPTO_PPAL_MODULO_REGADIO))){
					
					sesgarSecanoRegadio=sesgar309SecanoRegadio(lineaseguroid, clase, rcmDef2.getConceptoPpalModulo().getCodconceptoppalmod());
					if (sesgarSecanoRegadio){
						lstRcmParaSesgar.add(rcmDef2);
					}
				}
			}
			//elimino la de la lista lstRcmDef los q no cumplen con:mismo conceptoppalmod y sistema cultivo d la clase
			lstRcmDef.removeAll(lstRcmParaSesgar);
		}
		logger.debug("size tras sesgar:" + lstRcmDef.size());
		// FIn sesgar 309

		List<ModuloFilaView> lstModulosFView = new ArrayList<ModuloFilaView>();
		for (RiesgoCubiertoModulo rcm : lstRcmDef){
			ModuloFilaView filaView = new ModuloFilaView();
			//DAA 05/07/2012 setFilamodulo
			filaView.setFilamodulo(rcm.getId().getFilamodulo());
			filaView.setConceptoPrincipalModulo(rcm.getConceptoPpalModulo().getDesconceptoppalmod());
			filaView.setCodConceptoPrincipalModulo(rcm.getConceptoPpalModulo().getCodconceptoppalmod());
			String auxRiesgo;
			if (rcm.getElegible().equals(new Character('S'))){
				auxRiesgo = "<font color=\"red\">ELEGIBLE</font><br/>" + rcm.getRiesgoCubierto().getDesriesgocubierto();
			}
			else{
				auxRiesgo = rcm.getRiesgoCubierto().getDesriesgocubierto();
			}
			filaView.setRiesgoCubierto(auxRiesgo);
			filaView.setCodRiesgoCubierto(rcm.getRiesgoCubierto().getId().getCodriesgocubierto());
			//INICIO CONDICIONES DE COBERTURAS
			
			List<CaracteristicaModulo> lstCarMod = this.caracteristicaModuloDao.getCaracteristicasModulo(rcm);
			
			List<ModuloCeldaView> valoresFila = new ArrayList<ModuloCeldaView>(moduloView.getListaCabeceras().size());
			for (CaracteristicaModulo carmod : lstCarMod){
				//Obtenemos las coberturas
				List<CuadroCobertura> lstCoberturas = this.cuadroCoberturasDao.getCoberturas(carmod);
				ModuloCeldaView tmpCelda = new ModuloCeldaView();
				//Elegible
				if (carmod.getTipovalor().equals(new Character('E'))){
					tmpCelda.setElegible(true);
				}
				//Observaciones
				if (!StringUtils.nullToString(carmod.getObservaciones()).equals("")){
					tmpCelda.setObservaciones(carmod.getObservaciones());
				}
				
				//Recorremos las coberturas para montar el objeto para mostrar el cuadro.
				if (!lstCoberturas.isEmpty()){
					for (CuadroCobertura cc : lstCoberturas){
						//Valor
						ModuloValorCeldaView tmpValor = new ModuloValorCeldaView();
						tmpValor.setFila(cc.getFilamodulo());
						tmpValor.setColumna(cc.getColumnamodulo());
						tmpValor.setDescripcion(cc.getDescripcion());
						tmpValor.setCodigo(cc.getCodigo());
						if (StringUtils.nullToString(tmpCelda.getCodconcepto()).equals("") && !StringUtils.nullToString(cc.getCodconcepto()).equals(""))
							tmpCelda.setCodconcepto(cc.getCodconcepto());
						if (!StringUtils.nullToString(cc.getColumnamoduloVinc()).equals("")){
							tmpValor.setFilaVinculada(cc.getFilamoduloVinc());
							tmpValor.setColumnaVinculada(cc.getColumnamoduloVinc());
						}
						//Comprobamos si el asegurado puede elegir esa caracteristica.
						if (!StringUtils.nullToString(cc.getFichvinculacionexterna()).equals("") && 
								!StringUtils.nullToString(lineaseguroid).equals("") && !StringUtils.nullToString(nifAsegurado).equals("")){
							tmpValor.setTachar(
								this.comprobarCaracteristicaElegible(lineaseguroid, nifAsegurado, modulo.getId().getCodmodulo(), 
									cc.getFichvinculacionexterna(), cc.getCodconcepto(), new BigDecimal(cc.getCodigo()))
							);
						}
						
						// combprobamos si el asegurado puede elegir esa caracteristica desde la tabla AseguradoAutorizado
						if (checkGarantizado && cc.getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_GARANTIZADO))){
							tmpValor.setTachar(this.comprobarAsegAutorizado(lstAsegGarantizadosAplicables, modulo.getId().getCodmodulo(), 
									new BigDecimal(cc.getCodigo()), filaView.getCodConceptoPrincipalModulo(),filaView.getCodRiesgoCubierto()));
						}
						tmpCelda.getValores().add(tmpValor);
						
					}
				}
				//Anhadimos la celda a la fila
				valoresFila.add(tmpCelda);
			}
			filaView.setCeldas(valoresFila);
			//FIN CONDICIONES DE COBERTURAS
			
			lstModulosFView.add(filaView);
		}
		moduloView.setListaFilas(lstModulosFView);
		return moduloView;
	}
	
	/**
	 * Llama al DAO para obtener el listado de CPM permitidos para la poliza
	 * @param poliza
	 * @param idAnexo 
	 * @return
	 */
	private List<BigDecimal> getListaCPMPermitidos (Long idpoliza, Long idAnexo, String codModulo) {
		
		logger.debug("getListaCPMPermitidos - idPoliza: " + idpoliza + " - codModulo: " + codModulo);
		
		List<BigDecimal> listaCPM = new ArrayList<BigDecimal> ();
		//DAA 19/07/2012 idAnexo
		try {
			if(idAnexo!=null)
				listaCPM = cpmTipoCapitalDao.getCPMDePolizaAnexoMod(idpoliza, idAnexo, codModulo);
			else	
				listaCPM = cpmTipoCapitalDao.getCPMDePoliza(null, idpoliza, codModulo);
		} catch (DAOException e) {
			logger.error("getListaCPMPermitidos - Error al obtener el listado de CPM asociados a la poliza. Se mostrara¡n todos los CPM en la tabla de coberturas.", e);
		}
		
		return listaCPM;
	}
	
	/**
	 * Devuelve la lista de codigos de tipo de capital permitidos por clase y modulo. Si algun registro tiene el tipo de capital nulo
	 * se devolvera¡ la lista vacia
	 * @param lineaseguroid
	 * @param clase
	 * @param codModulo
	 * @return
	 */
	private List<BigDecimal> getListaTiposCapital (Long lineaseguroid, BigDecimal clase, String codModulo) {
		
		// Obtiene el listado de tipos de capital permitidos por la clase y el modulo
		List<BigDecimal> lista = claseDetalleDao.getClaseDetallePorClaseModulo(lineaseguroid, clase, codModulo);
		
		// Si contiene un tipo de capital nulo, se devuelve el listado vacio
		if (lista.contains(null)) return new ArrayList<BigDecimal>();
		// Si todos los tipos de capital esta¡n informados, se devuelve la lista 
		else return lista; 
	}
	
	/**
	 * Realiza la llamada al metodo de comprobacion de CPM dependiendo de si la invocacion viene de la generacion de informes 
	 * o del cuadro de coberturas de la poliza
	 * @param poliza
	 * @param codModulo
	 * @param cpm
	 * @param informes
	 * @param listaTC Lista de codigos de tipo capital permitidos por la clase y el modulo
	 * @return
	 */
	private boolean isCPMPermitido (Long lineaseguroid, BigDecimal clase, String codModulo, BigDecimal cpm, BigDecimal codConcepto, boolean informes, List<BigDecimal> listaCPM, List<BigDecimal> listaTC) {
		
		// Llamada el metodo de comprobacion de CPM
		boolean res = (informes) ? (isCPMPermitidoInformes(lineaseguroid, clase, cpm, codConcepto, listaCPM)) : (isCPMPermitidoCuadroCoberturas (lineaseguroid, clase, codModulo, cpm, listaTC));
		logger.debug("isCPMPermitido - Registro " + (res ? "" : "no ") + "encontrado en TB_CPM_TIPO_CAPITAL.");
		logger.debug("isCPMPermitido - " + (res ? "Se " : "No se ") + "mostrará ¬a l?a en el cuadro de coberturas.");			
		
		// Realiza la llamada al DAO para obtener la informacion de BD
		return res;			
	}
	
	/**
	 * Comprueba si el CPM indicado existe en la tabla TB_CPM_TIPO_CAPITAL para los dema¡s valores pasados como para¡metro
	 * Se usa al montar el cuadro de coberturas del informe de impresion, ya que obtiene los valores de las parcelas
	 * @param poliza
	 * @return
	 */
	private boolean isCPMPermitidoInformes (Long lineaseguroid, BigDecimal clase, BigDecimal codConceptoPpalMod, BigDecimal codConcepto, List<BigDecimal> listaCPM) {
		logger.debug("isCPMPermitidoInformes - lineaseguroid: " + lineaseguroid + " - clase: " + 
				clase + " - codConceptoPpalMod: " + codConceptoPpalMod);
		
		// Comprueba si el CPM esta¡ en la lista de CPM permitidos para esta poliza			
		boolean permitido = CoberturasUtils.isCPMPermitido(codConceptoPpalMod, codConcepto, listaCPM);
		logger.debug("isCPMPermitidoInformes - codConceptoPpalMod: " + codConceptoPpalMod + " - permitido: " + permitido);
		
		return permitido;
		
	}
	
	/**
	 * Comprueba si el CPM indicado existe en la tabla TB_CPM_TIPO_CAPITAL para los dema¡s valores pasados como para¡metro
	 * Se usa al montar el cuadro de coberturas de la poliza, ya que obtiene el listado de cultivos y de sistema de cultivo de la clase
	 * @param lineaseguroid
	 * @param codModulo
	 * @param cpm
	 * @param codCultivo
	 * @param codSC
	 * @param listaTC 
	 * @return
	 */
	private boolean isCPMPermitidoCuadroCoberturas (Long lineaseguroid, BigDecimal clase, String codModulo, BigDecimal cpm, List<BigDecimal> listaTC) {
		
		logger.debug("isCPMPermitidoCuadroCoberturas - inicio");	
		
		// Obtiene los cultivos por clase seleccionada - usado para comprobar si el CPM se mostrara¡ en el cuadro de coberturas
		List<BigDecimal> listaCultivos = polizaDao.getCultivosClase(clase, lineaseguroid);
		// Obtiene el sistema de cultivo - usado para comprobar si el CPM se mostrara¡ en el cuadro de coberturas
		BigDecimal sistCultivo = polizaDao.getSistCultivoClase(clase, lineaseguroid);
		
		List<BigDecimal> listaCicloCultivos = polizaDao.getCiclosCultivoClase (clase,lineaseguroid);
		// Objetos necesarios para la creacion de CPMTipoCapital
		Linea linea = new Linea ();
		linea.setLineaseguroid(lineaseguroid);
		ConceptoPpalModulo cppalm = new ConceptoPpalModulo (cpm,null);						
		SistemaCultivo sistemaCultivo = new SistemaCultivo (sistCultivo,null);
		List<String> listaModulos = new ArrayList<String> ();
		// Si la lista de cultivos no contiene el valor correspondiente a 'Todos los cultivos' se incluye
		if (listaCultivos != null){
			if (!listaCultivos.contains(new BigDecimal (Constants.TODOS_CULTIVOS))) listaCultivos.add(new BigDecimal (Constants.TODOS_CULTIVOS));
		}
		// Si la lista de cultivos es nula, se crea y se inserta el valor de 'Todos los cultivos'
		else {
			listaCultivos = new ArrayList<BigDecimal> ();
			listaCultivos.add(new BigDecimal (Constants.TODOS_CULTIVOS));
		}
		// Carga la lista de modulos con el pasado por para¡metro y el de 'Todos los modulos'			
		listaModulos.add(codModulo);
		listaModulos.add(Constants.TODOS_MODULOS);
		
		logger.debug("isCPMPermitidoCuadroCoberturas - lineaseguroid: " + linea.getLineaseguroid() + " - modulo: " + 
				StringUtils.toValoresSeparadosXComas(listaModulos,true) + " - conceptoPpalModulo: " + cpm + 
				" - cultivos: " + StringUtils.toValoresSeparadosXComas(listaCultivos,false) + " - sistemaCultivo: " + sistCultivo +
				" - tipos de capital: " + StringUtils.toValoresSeparadosXComas(listaTC,false) + " ciclos Cultivos: " +
				StringUtils.toValoresSeparadosXComas(listaCicloCultivos,false) );
		
		// Carga el objeto CPMTipoCapital con el filtro de la busqueda
		CPMTipoCapital cpmTC = new CPMTipoCapital ();
	    cpmTC.getCultivo().setLinea(linea);
		cpmTC.setListaModulos(listaModulos);
		cpmTC.setConceptoPpalModulo(cppalm);
		cpmTC.setListaCultivos(listaCultivos);
		cpmTC.setSistemaCultivo(sistemaCultivo);
		cpmTC.setListaTiposCapital(listaTC);
		cpmTC.setListaCicloCultivos(listaCicloCultivos);
		
		// Realiza la llamada al DAO para obtener la informacion de BD
		return cpmTipoCapitalDao.isCPMPermitido(cpmTC);			
	}
	
	public Boolean sesgarFilaModulos(Long lineaseguroid, BigDecimal clase, BigDecimal codconceptoppalmod, BigDecimal codriesgocubierto, String codmodulo){
		Boolean sesgarTemp=false;
		try{
    		if(clase!=null){
    			List<TablaExternaCultivo> listatbExtCultivo = new ArrayList<TablaExternaCultivo>();
    			List<BigDecimal> listaCultivosClase = new ArrayList<BigDecimal>();
    			listaCultivosClase=this.polizaDao.getCultivosClase(clase, lineaseguroid);
    			listatbExtCultivo=this.polizaDao.getTablaExtCultivo(lineaseguroid, codconceptoppalmod, codriesgocubierto, codmodulo);
    			for (int i = 0; i < listaCultivosClase.size(); i++) {
					try{
						if ((listaCultivosClase.get(i).toString().equals("999")) || (listatbExtCultivo.get(0).getCultivo().getId().getCodcultivo().toString().equals(listaCultivosClase.get(i).toString()))){
							sesgarTemp=true;
	                		logger.debug(" añ¡¤©do 1 por coincidir ambos cultivos, de la tabla y de la clase, con fichavincultacionexterna 135");
	                		break;
	                	}
					}catch (NullPointerException e) {
						sesgarTemp=true;
	    				logger.debug(" nullpointerException al comparar listatbExtCultivo con listaCultivosClase");
	    				break;
					}catch (IndexOutOfBoundsException e) {
						sesgarTemp=true;
	    				
	    				break;
					}
    			}
    		}
		}catch (NullPointerException e) {
			sesgarTemp=true;
		}
		return sesgarTemp;
	}
	
	public Boolean sesgar309SecanoRegadio(Long lineaseguroid, BigDecimal clase, BigDecimal codconceptoppalmod){
		
		logger.debug("INIT sesgar309SecanoRegadio ...");
		
		logger.debug("lineaseguroid: " + lineaseguroid);
		logger.debug("clase: " + clase);
		logger.debug("codconceptoppalmod: " + codconceptoppalmod);
		
		String sisCultivoPplmod="";
		if (codconceptoppalmod.toString().equals("27")){
			sisCultivoPplmod="1";
		}else if (codconceptoppalmod.toString().equals("26")){
			sisCultivoPplmod="2";
		}
		
		logger.debug("sisCultivoPplmod: " + sisCultivoPplmod);
		
		BigDecimal sisCultivoClase = null;
		sisCultivoClase = this.polizaDao.getSistCultivoClase(clase, lineaseguroid);
		
		logger.debug("sisCultivoClase: " + sisCultivoClase);
		
		if (sisCultivoClase!=null){
			if (!sisCultivoClase.toString().equals("3") && !sisCultivoClase.toString().equals("9")){
				if (!sisCultivoClase.toString().equals(sisCultivoPplmod)){
					logger.debug("devuelve true");
					return true;
				}else { 
					logger.debug("devuelve false .");
					return false;
				}	
			}else {
				logger.debug("devuelve false ..");
				return false;
			}
		}else {
			logger.debug("devuelve false ...");
			return false;
		}	
	}
	
	/**
	 * Metodo que comprueba para cada dato que tiene vinculacion con otro, si se debe tachar o no.
	 * @param listaModulos Listado de todos los modulos y sus coberturas
	 */
	private void comprobarVinculados(List<ModuloView> listaModulos) {
		
		//COLUMNAS:
		//		1. % CAPITAL ASEGURADO
		//		2. CALCULO INDEMNIZACION
		//		3. % MINIMO INDEMNIZABLE
		//		4. TIPO FRANQUICIA
		//		5. % FRANQUICIA
		//      6. GARANTIZADO
		for (ModuloView mv : listaModulos){
			//Recorremos las filas
			for (ModuloFilaView mfv : mv.getListaFilas()){
				//Para cada celda, miro si hay valores que esten vinculados y si lo esta¡n, compruebo si el vinculado esta¡ tachado.
				for (ModuloCeldaView mcv : mfv.getCeldas()){
					boolean elegible = mcv.isElegible();
					for (ModuloValorCeldaView mvcv : mcv.getValores()){
						if (elegible){
							if (mvcv.getFilaVinculada() != null && 
									this.tacharVinculado(mv.getListaFilas(), mvcv.getFilaVinculada(), mvcv.getColumnaVinculada(), mvcv.getDescripcion())){
								mvcv.setTachar(true);
							}
						}
						else{
							mvcv.setTachar(false);
						}
					}
				}
			}
		}
		logger.debug("end - [metodo] comprobarVinculados");
	}
	
	/**
	 * Metodo para saber si un dato que tiene otro vinculado debe tacharse en la pantalla o no.
	 * @param listaFilas Filas de coberturas de un modulo
	 * @param fila Fila a la que esta¡ vinculado el valor
	 * @param columna Columna a la que esta¡ vinculado el valor
	 * @param descripcion El propio valor
	 * @return true cuando se deba tachar el valor y false en caso contrario.
	 */
	private boolean tacharVinculado(List<ModuloFilaView> listaFilas, BigDecimal fila, BigDecimal columna, String descripcion) {
		
		// MPM - 21/06/2012
		// Controlar la posible excepcion al acceder a una posicion inexistente de la lista
		ModuloFilaView filas = null;
		try {
			//DAA 05/07/2012   //filas = listaFilas.get((fila.intValue()-1));
			for(ModuloFilaView mfv : listaFilas){
				if(mfv.getFilamodulo().equals(fila)){
					filas = mfv;
					break;
				}
			}
			
		} 
		catch (Exception e) {
			logger.error("Ocurrio un error al obtener la posicion " + fila + " de la lista de filas.", e);
			return false;
		}
		if (filas != null){
			for(ModuloCeldaView mcv : filas.getCeldas()){
				if (mcv.isElegible()){
					for (ModuloValorCeldaView mvcv : mcv.getValores()){
						if (mvcv.getColumna().equals(columna) && mvcv.getDescripcion().equals(descripcion) && mvcv.isTachar()){
							return true;
						}
					}
				}
			}
		}
		logger.debug("end - tacharVinculado");
		return false;
	}
	
	private boolean comprobarCaracteristicaElegible(Long lineaseguroid, String nifAsegurado, 
			String codmodulo, BigDecimal ficheroVinculacionExterna, BigDecimal codconcepto, BigDecimal codValor) {
		
		boolean tachar = true;
		if (ficheroVinculacionExterna != null && 
				ficheroVinculacionExterna.equals(new BigDecimal(111))){
			//Buscamos en TablaBonus
			BigDecimal codHistAseg = null;
			if (codconcepto.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PCT_FRANQUICIA)) && !StringUtils.nullToString(codValor).equals(""))
				codHistAseg = polizaDao.getHistAsegBonus(lineaseguroid, codValor);
			
			if (codHistAseg != null){
				//Con el codigo obtenido, buscamos en Med_Asoc_Hist_Aseg y nos quedamos los porcentajes
				List<BigDecimal> porcentajes = polizaDao.getMedAsocHistAseg(lineaseguroid, codmodulo, codHistAseg);
				//Busco en medidas el registro que contiene el nif del asegurado y me quedo con pctBonifRecargo
				BigDecimal pctBonifRecargo = polizaDao.getPctBonifRecargo(lineaseguroid, nifAsegurado);
				for (BigDecimal pct: porcentajes){
					if (pctBonifRecargo.compareTo(pct) >= 0){
						tachar = false;
						break;
					}
				}
			}
		}
		else{
			tachar = false;
		}
		
		logger.debug("end - [metodo] comprobarCaracteristicaElegible");
		return tachar;
	}
	
	//-------------------------------------------------------- COMPARATIVAS ------------------------------------------------------------
	public HashMap<String, Object> crearComparativas(Long lineaseguroid, String codlinea, BigDecimal clase, String nifAsegurado, String[] modulos){
		logger.debug("init - [metodo] crearComparativas");	
		
		HashMap<String, Object> comparativas = new HashMap<String, Object>(); //padre de las comparativas
		LinkedHashMap<String, Object> comparativaModulo = new LinkedHashMap<String, Object>(); //una entrada por cada modulo seleccionado
		HashMap<String, String> moduloMSGaclaracion = new HashMap<String, String>();
		HashMap<String, Boolean> mapIsCPMpermitido = new HashMap<String, Boolean>();
		
		// filtrar por fichvinculacionexterna
		Boolean sesgarTemp=false;
		for(String modulo : modulos){
			
			// MPM - 16/10/12
			// Anhade al objeto de busqueda el listado de tipos de capital permitidos para la clase y modulo actual
			List<BigDecimal> listaTiposCapital = getListaTiposCapital(lineaseguroid, clase, modulo);
			
			List<ComparativaFija> lstCompFijas = polizaDao.getListComparativasFijas(lineaseguroid, modulo, clase);

			if ((lstCompFijas!=null) && (lstCompFijas.size()>0)){
				this.crearComparativaFija(lineaseguroid,modulo,comparativaModulo, 
						StringUtils.nullToString(nifAsegurado),lstCompFijas, clase, listaTiposCapital);
			}else{				
				VistaComparativaFiltro filter = new VistaComparativaFiltro(lineaseguroid, modulo);
				List<VistaComparativas> elementosComparativa = new ArrayList<VistaComparativas>();
				List<VistaComparativas> elementosComparativaTemp = new ArrayList<VistaComparativas>();
				
				// Obtiene las comparativas de la poliza
				List<VistaComparativas> elementosComparativaInicial = (ArrayList<VistaComparativas>) polizaDao.getComparativa(filter);
				Collections.sort(elementosComparativaInicial, new VistaComparativaComparator());
				// MPM - 23/08/12
				long tiempoInicio = System.currentTimeMillis();
				// Elimina las filas de la comparativa que no se pueden mostrar dependiendo de lo indicado en la tabla de CPM y tipo capital
				for (VistaComparativas vistaComparativas : elementosComparativaInicial) {
					String key="M"+ modulo + "CP" + vistaComparativas.getId().getCodconceptoppalmod().toString() + 
							"C" + vistaComparativas.getId().getCodconcepto().toString();
					Boolean res=false;
					if(mapIsCPMpermitido.containsKey(key)){
						res=mapIsCPMpermitido.get(key);
					}else{
						res=this.isCPMPermitido (lineaseguroid, clase, modulo, vistaComparativas.getId().getCodconceptoppalmod(),
								vistaComparativas.getId().getCodconcepto(), false, null, listaTiposCapital);
						mapIsCPMpermitido.put(key, res);
					}					
					if (res){
						elementosComparativaTemp.add(vistaComparativas);
					}
				}
				long totalTiempo = System.currentTimeMillis() - tiempoInicio;
				logger.debug("Tiempo para crear comparativas: " + totalTiempo + " miliseg");
				sesgarTemp=false;
				// sesgar en la linea 309
				if (lineaseguroid != null && !StringUtils.nullToString(nifAsegurado).equals("") && codlinea.equals("309")){
					List<VistaComparativas> elementosComparativaParaSesgar = new ArrayList<VistaComparativas>();
					boolean sesgarSecanoRegadio=false;
					for (VistaComparativas elemComTemp   : elementosComparativaTemp){
						sesgarSecanoRegadio=false;
						if ((elemComTemp.getId().getCodconceptoppalmod().toString().equals("26") || 
								elemComTemp.getId().getCodconceptoppalmod().toString().equals("27"))){
							sesgarSecanoRegadio=sesgar309SecanoRegadio(lineaseguroid, clase, elemComTemp.getId().getCodconceptoppalmod());
							if (sesgarSecanoRegadio){
								elementosComparativaParaSesgar.add(elemComTemp);
							}
						}
					}
					//elimino la de la lista lstRcmDef los q no cumplen con:mismo conceptoppalmod y sistema cultivo d la clase
					elementosComparativaTemp.removeAll(elementosComparativaParaSesgar);
				}
				// Fin sesgar linea 309
				
				if (elementosComparativaTemp != null && elementosComparativaTemp.size() > 0){
					for (VistaComparativas elemComTemp   : elementosComparativaTemp){
						if  (!StringUtils.nullToString(elemComTemp.getId().getFichvinculacionexterna()).equals("135")){
							elementosComparativa.add(elemComTemp); // si no tiene el 135
						}else{
							sesgarTemp=sesgarFilaModulos(lineaseguroid, clase, elemComTemp.getId().getCodconceptoppalmod(), 
									elemComTemp.getId().getCodriesgocubierto(), elemComTemp.getId().getCodmodulo());
							if (sesgarTemp==true){
								elementosComparativa.add(elemComTemp);
							}
						}
					}
					
					if (elementosComparativa.size() > 0){
					
						// FIN filtrar por fichvinculacioniexterna
						boolean todosLosElementosSonElegibles = todosLosElementosSonElegibles(elementosComparativa);
						List<VistaComparativas> elementosComparativaVFE = establecerCodigosConceptoValorFilasElegibles(elementosComparativa);
						String descModulo = obtenerDesModulo(elementosComparativaVFE);
						List<VistaComparativas> coberturasPermitidas = coberturasPermitidas(lineaseguroid, clase, 
								nifAsegurado, elementosComparativaVFE);
						datosVinculadosNUEVO(coberturasPermitidas);
						
						List<List<VistaComparativas>> gruposElementosComparativa = agruparElementosComparativa(coberturasPermitidas);
						Integer vectorContadores [] = generarVectorContadores(gruposElementosComparativa);
						
						List<List<VistaComparativas>> combinacionesElementosComparativa = new ArrayList<List<VistaComparativas>>();
						
						tiempoInicio = System.currentTimeMillis();
						
						crearCombinacionElementosComparativa(gruposElementosComparativa, vectorContadores, combinacionesElementosComparativa);
						
						totalTiempo = System.currentTimeMillis() - tiempoInicio;
						logger.debug("Tiempo paso 1: " + totalTiempo + " miliseg");
						tiempoInicio = System.currentTimeMillis();
						
						List<List<Integer>> combinacionesPosicionGruposElementosSoloElegibles = crearCombinacionesGruposElementosElegibles(gruposElementosComparativa);
						
						totalTiempo = System.currentTimeMillis() - tiempoInicio;
						logger.debug("Tiempo paso 2: " + totalTiempo + " miliseg");
						tiempoInicio = System.currentTimeMillis();
						
						for(List<Integer> combinacionPosicionGruposElementosSoloElegibles : combinacionesPosicionGruposElementosSoloElegibles){
							List<List<VistaComparativas>> combinacionesElementosComparativaAux = crearCombinacion(gruposElementosComparativa, combinacionPosicionGruposElementosSoloElegibles);
							vectorContadores = generarVectorContadores(combinacionesElementosComparativaAux);
							crearCombinacionElementosComparativa(combinacionesElementosComparativaAux, vectorContadores, combinacionesElementosComparativa);
						}
						
						totalTiempo = System.currentTimeMillis() - tiempoInicio;
						logger.debug("Tiempo paso 3: " + totalTiempo + " miliseg");
						tiempoInicio = System.currentTimeMillis();
						
						comprobarHijosCorrectos(combinacionesElementosComparativa);
						
						totalTiempo = System.currentTimeMillis() - tiempoInicio;
						logger.debug("Tiempo paso 4: " + totalTiempo + " miliseg");
						tiempoInicio = System.currentTimeMillis();
						
						if(!combinacionesPosicionGruposElementosSoloElegibles.isEmpty()){
							//el modulo tiene elementos elegibles
							
							//Anadir elementos -2
							filter.setElegible(Character.valueOf('S'));
							ArrayList<VistaComparativas> elementosElegiblesModulo = (ArrayList<VistaComparativas>) polizaDao.getComparativa(filter);
							establecerCodigosConceptoValorFilasElegibles(elementosElegiblesModulo);
							
							for(List<VistaComparativas> combinacionElementosComparativa : combinacionesElementosComparativa){
								
								anadirElementosElegiblesNoSeleccionados(combinacionElementosComparativa, elementosElegiblesModulo);
							}
						}
						
						totalTiempo = System.currentTimeMillis() - tiempoInicio;
						logger.debug("Tiempo paso 5: " + totalTiempo + " miliseg");
						tiempoInicio = System.currentTimeMillis();
						
						if(todosLosElementosSonElegibles){
							filter.setElegible(Character.valueOf('S'));
							ArrayList<VistaComparativas> elementosElegiblesModulo = (ArrayList<VistaComparativas>) polizaDao.getComparativa(filter);
							List<VistaComparativas> combinacionVaciaElementosComparativa = new ArrayList<VistaComparativas>();
							anadirElementosElegiblesNoSeleccionados(combinacionVaciaElementosComparativa, elementosElegiblesModulo);
							combinacionesElementosComparativa.add(combinacionVaciaElementosComparativa);
						}
						
						totalTiempo = System.currentTimeMillis() - tiempoInicio;
						logger.debug("Tiempo paso 6: " + totalTiempo + " miliseg");
						tiempoInicio = System.currentTimeMillis();
						
						comparativaModulo.put(modulo+" - "+descModulo, combinacionesElementosComparativa);						
					}
					else{
						crearComparativaUnica(comparativaModulo, lineaseguroid, modulo, clase);
					}
					
					totalTiempo = System.currentTimeMillis() - tiempoInicio;
					logger.debug("Tiempo paso 7: " + totalTiempo + " miliseg");
					tiempoInicio = System.currentTimeMillis();
					
				}else{
					crearComparativaUnica(comparativaModulo, lineaseguroid, modulo, clase);
				}
				
				totalTiempo = System.currentTimeMillis() - tiempoInicio;
				logger.debug("Tiempo paso 8: " + totalTiempo + " miliseg");
				tiempoInicio = System.currentTimeMillis();
				
				for (VistaComparativas vc: elementosComparativa){
					polizaDao.evict(vc);
				}
				
				totalTiempo = System.currentTimeMillis() - tiempoInicio;
				logger.debug("Tiempo paso 9: " + totalTiempo + " miliseg");
				tiempoInicio = System.currentTimeMillis();
			}
			
			// HashMap con los mensajes especiales de cada modulo
			moduloMSGaclaracion.put(modulo, getMSGAclaracionModulo(modulo,lineaseguroid));
		
		}
		
		comparativas.put("comparativa", comparativaModulo);
		comparativas.put("msjModulo", moduloMSGaclaracion);
		
		logger.debug("end - [metodo] crearComparativas");	
		return comparativas;
	}
	
	public void crearComparativaFija(Long lineaseguroid,String modulo,
			LinkedHashMap<String, Object> comparativaModulo,String nifCifAseg,List<ComparativaFija> lstCompFijas,
			BigDecimal clase, List<BigDecimal> listaTiposCapital){
		logger.debug("init - [metodo] crearComparativaFija");
		List<List<VistaComparativas>> combinacionesElementosComparativa = new ArrayList<List<VistaComparativas>>();
		List<VistaComparativas> vistaL = new ArrayList<VistaComparativas>();
		HashMap<String, Boolean> mapIsCPMpermitido = new HashMap<String, Boolean>();
		String cAux= lstCompFijas.get(0).getComparativa().toString();
		String cActual=null;
		boolean checkSesgar = true;
		boolean sesgarComparativa=false;
		boolean sesgarAllComparativa=false;
		String descModulo = "";
		logger.debug("[metodo] crearComparativaFija - isCPMPermitido");
		// MODIF TAM (21.11.2018) ** Añ¡¤©mos los displays */
		logger.debug("**@@** Valor de lineaseguroid:"+lineaseguroid);
		logger.debug("**@@** Valor de nifCifAseg:"+nifCifAseg);
		logger.debug("**@@** Valor de modulo:"+modulo);
		long tiempoInicio = System.currentTimeMillis();
		for (ComparativaFija cFija : lstCompFijas) {
			// MODIF TAM (21.11.2018) ** Inicio //
			//sesgarComparativa=false;
			checkSesgar=true;
			// MODIF TAM (21.11.2018) ** Fin //
			cActual= cFija.getComparativa().toString();
			logger.debug("**@@** Valor de cActual:"+cActual);
			
			if ((sesgarAllComparativa) && (cAux.equals(StringUtils.nullToString(cActual)))){
				checkSesgar=false;
			}else{
				sesgarAllComparativa=false;
				if (!cActual.equals(cAux) || (checkSesgar)){
					logger.debug("[metodo] crearComparativaFija - sesgarComparativa");
					//sesgarComparativa=this.checkSesgarComparativaFija(lineaseguroid, nifCifAseg, modulo,cFija);
					// MODIF TAM (21.11.2018) ** Añ¡¤©mos los displays */
					logger.debug("**@@** Entramos a comprobarCaracteristicaElegible");
					logger.debug("**@@** Valor de FichVinculacionexterna:"+cFija.getFichvinculacionexterna());
					logger.debug("**@@** Valor de Codconcepto:"+cFija.getCodconcepto());
					logger.debug("**@@** Valor de CodValor:"+cFija.getCodvalor());
					sesgarComparativa = this.comprobarCaracteristicaElegible(lineaseguroid, nifCifAseg, modulo, 
							cFija.getFichvinculacionexterna(), cFija.getCodconcepto(), cFija.getCodvalor());
					logger.debug("**@@** Valor de sesgarComparativa:"+sesgarComparativa);
					
					checkSesgar=false;
				}
				if (!sesgarComparativa){
					descModulo = cFija.getDesmodulo();					
					VistaComparativas vista = ComparativaUtil.generateVistaComparativas(cFija);
									
					String key="M"+ modulo + "CP" + vista.getId().getCodconceptoppalmod().toString() + 
							"C" + vista.getId().getCodconcepto().toString();
					Boolean res=false;
					if(mapIsCPMpermitido.containsKey(key)){
						res=mapIsCPMpermitido.get(key);
					}else{
						res=this.isCPMPermitido (lineaseguroid, clase, modulo, vista.getId().getCodconceptoppalmod(),
								vista.getId().getCodconcepto(), false, null, listaTiposCapital);
						mapIsCPMpermitido.put(key, res);
					}
					logger.debug("cActual -> " + cActual + " || res -> " + res.toString());
					if (res){
						//Compruebo si la tengo que anhadir a los elementos de la comparativa actual o debo crear otra
						if (!cActual.equals(cAux)){ 
							if (vistaL.size()>0)
								combinacionesElementosComparativa.add(vistaL);
							vistaL = new ArrayList<VistaComparativas>();
						}
						vistaL.add(vista);
						cAux=cActual;
					}
				}else{
					sesgarAllComparativa=true;
					// MODIF TAM (21.11.2018) ** Inicio //
					cAux=cActual;
				}
			}
		}
		long totalTiempo = System.currentTimeMillis() - tiempoInicio;
		logger.debug("Tiempo para crear comparativas: " + totalTiempo + " miliseg");
		combinacionesElementosComparativa.add(vistaL);
		comparativaModulo.put(descModulo, combinacionesElementosComparativa);
		logger.debug("end - [metodo] crearComparativaFija" + " || combinacionesElementosComparativa.size() -> " + combinacionesElementosComparativa.size());
	}
	
	private boolean todosLosElementosSonElegibles(List<VistaComparativas> elementosComparativa){
		for(VistaComparativas elementoComparativa :  elementosComparativa){
			if(!Character.valueOf('S').equals(elementoComparativa.getId().getElegible()))
				return false;
		}
		return true;
	}
	
	private List<VistaComparativas> establecerCodigosConceptoValorFilasElegibles(final List<VistaComparativas> elementosComparativa){
		
		List<VistaComparativas> elementos = new ArrayList<VistaComparativas>();
		for(VistaComparativas elementoComparativa : elementosComparativa){
			if(Character.valueOf('S').equals(elementoComparativa.getId().getElegible())){
				
				elementoComparativa.getId().setCodconcepto(new BigDecimal(363));
				elementoComparativa.getId().setCodvalor(new BigDecimal(-1));
				elementoComparativa.getId().setNomconcepto("");
				elementoComparativa.getId().setDesvalor("");
				
			}
			elementos.add(elementoComparativa);
		}
		
		return elementos;
	}
	
	/**
	 * Obtiene de la vista comparativa la descipcion del modulo
	 * 
	 * @param
	 * @return
	 */
	private String obtenerDesModulo(final List<VistaComparativas> comparativas) {
		
		String desModulo = "";

		for (VistaComparativas comparativa : comparativas) {
			desModulo = comparativa.getId().getDesmodulo();
			break;
		}

		return desModulo;
	}
	
	/**
	 * A partir de las comparativas obtenidas, comprobamos si el asegurado puede
	 * contratarlas, para mostrarla o no
	 * 
	 * @param comparativas
	 * @return
	 */
	private ArrayList<VistaComparativas> coberturasPermitidas(Long lineaseguroid, BigDecimal clase, String nifasegurado,
			final List<VistaComparativas> comparativas) {
		logger.debug("init - [metodo] coberturasPermitidas");
		ArrayList<VistaComparativas> comparativasSubvencionables = new ArrayList<VistaComparativas>();
		ArrayList<VistaComparativas> comparativasFichVincExt = new ArrayList<VistaComparativas>();
		ArrayList<VistaComparativas> comparativasSubvencionablesDef = new ArrayList<VistaComparativas>();
		
		//para cada comparativa, comprobar si tiene ficherovinculacionexterna y, en tal caso, hay que 
		//comprobar si dicha comparativa es elegible por el usuario o no.
		
		//********montamos una lista de AseguradosAutorizados para posteriormente filtrar las comparativas subvencionables
		boolean checkGarantizado=false;
		boolean checkGarantizadoNif=false;
		List<AseguradoAutorizado> lstAsegGarantizadosAplicables= new ArrayList<AseguradoAutorizado>();
		
		//ASF - 24/10/2012 - Adaptaciones 314
		String comprobarAAC = this.claseDao.getComprobarAac(lineaseguroid, clase);
		if(StringUtils.nullToString(comprobarAAC).equals("S")){
			checkGarantizado = this.aseguradoAutorizadoDao.checkAseguradoAutorizadoGarantizado(lineaseguroid);
		}
		
		if (checkGarantizado){
			checkGarantizadoNif = this.aseguradoAutorizadoDao.checkAseguradoAutorizadoNif(lineaseguroid,nifasegurado);
			if (checkGarantizadoNif){
				lstAsegGarantizadosAplicables = this.aseguradoAutorizadoDao.lstAsegGarantizadosAplicables(lineaseguroid,nifasegurado);
			}else{
				lstAsegGarantizadosAplicables = this.aseguradoAutorizadoDao.lstAsegGarantizadosAplicables(lineaseguroid,null);
			}
		}
		for(VistaComparativas vComp : comparativas){
			//Obtengo las vinculaciones por lineaseguroid, codmodulo, filamodulo y columnamodulo
			//List<VinculacionValoresModulo> lstVincValMod;
			boolean aniadir = true;
			try {
				logger.debug("getVinculacionesValoresModulo(lineaseguroid="+lineaseguroid+", codmodulo="+vComp.getId().getCodmodulo()+
						", codconcepto="+vComp.getId().getCodconcepto()+", codvalor="+vComp.getId().getCodvalor()+
						", filamodulo="+vComp.getId().getFilamodulo()+", columnamodulo="+vComp.getId().getColumnamodulo()+");");
				/*lstVincValMod = this.polizaDao.getVinculacionesValoresModulo(lineaseguroid, vComp.getId().getCodmodulo(), 
						vComp.getId().getCodconcepto(), vComp.getId().getCodvalor(), vComp.getId().getFilamodulo(), 
						vComp.getId().getColumnamodulo());*/
				
				List<CuadroCobertura> lstCoberturas = this.cuadroCoberturasDao.getCoberturas(lineaseguroid, 
						vComp.getId().getCodmodulo(), vComp.getId().getFilamodulo(), vComp.getId().getColumnamodulo(), 
						vComp.getId().getCodconcepto(), vComp.getId().getCodvalor());
				
				for (CuadroCobertura cc : lstCoberturas){
					if (this.comprobarCaracteristicaElegible(lineaseguroid, nifasegurado, vComp.getId().getCodmodulo(), 
							cc.getFichvinculacionexterna(), cc.getCodconcepto(), new BigDecimal(cc.getCodigo()))){
						aniadir = false;
						break;
					}
					if (checkGarantizado && cc.getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_GARANTIZADO))){
						if (this.comprobarAsegAutorizado(lstAsegGarantizadosAplicables, vComp.getId().getCodmodulo(), 
								new BigDecimal(cc.getCodigo()), vComp.getId().getCodconceptoppalmod(),vComp.getId().getCodriesgocubierto())){
							aniadir = false;
							break;
						}
					}
				}
				
				if (aniadir)
					comparativasSubvencionables.add(vComp);
				else{
					comparativasFichVincExt.add(vComp);
					if (comparativasSubvencionables.contains(vComp)){
						comparativasSubvencionables.remove(vComp);
					}
				}
			} catch (Exception e) {
				//Si hay un error, lo intentamos con la siguiente comparativa????
			}
		}
		
		//En comparativasSubvencionables ya hemos quitado aquellas que tienen fichvinculacionexterna.
		//Ahora hay que quitar las que esta¡n vinculadas a estas.
		if (comparativasFichVincExt.size() > 0){
			comparativasSubvencionablesDef = limpiaComparativasVinculadas(comparativasSubvencionables, comparativasFichVincExt);
		}
		else{
			comparativasSubvencionablesDef = comparativasSubvencionables;
		}
		
		logger.debug("end - [metodo] coberturasPermitidas");
		return comparativasSubvencionablesDef;
	}
	
	private void datosVinculadosNUEVO(List<VistaComparativas> comparativas){
		
		ArrayList<VistaComparativas> padres = new ArrayList<VistaComparativas>();
		ArrayList<VistaComparativas> hijos = new ArrayList<VistaComparativas>();
		
		//obtengo las comparativas q estan vinculadas entre si 
		if (comparativas != null) {
			for(VistaComparativas vistaComparativa : comparativas){
				if(vistaComparativa.getId().getDatovinculado().toString().equals("S")){
					if(vistaComparativa.getId().getFilamodulovinc().intValue() == 0
							&& vistaComparativa.getId().getColumnamodulovinc().intValue() == 0) {
						//comparativa preferente
						padres.add(vistaComparativa);
					}else{
						//comparativa vinculada a la preferente
						hijos.add(vistaComparativa);
					}
				}
			}
		}
		
		// -- para cada hijo busco el valorVinculado y se lo guardo. De esta forma se comparara¡ posteriormente codValorVinculado hijo con 
		// el codValor del padre; la comparacion por descripcion entre padre/hijo se mantiene 
		if (comparativas!=null){
			if (comparativas.size()>0){
				List<ConceptoCubiertoModulo> lstConCubMod = new ArrayList<ConceptoCubiertoModulo>();
				lstConCubMod =polizaDao.getMapConceptoCubMod(comparativas.get(0).getId().getLineaseguroid(),
						      comparativas.get(0).getId().getCodmodulo());
				for (VistaComparativas hijo : hijos) {
					int posicionVinc= hijo.getId().getColumnamodulovinc().intValue()-1;
					int posCol= hijo.getId().getColumnamodulo().intValue()-1;
					BigDecimal codConcCubModVinc= lstConCubMod.get(posicionVinc).getDiccionarioDatos().getCodconcepto();
					BigDecimal codConcCubMod= lstConCubMod.get(posCol).getDiccionarioDatos().getCodconcepto();
					
					BigDecimal codValor= polizaDao.getValorVincValMod(hijo.getId().getLineaseguroid(),hijo.getId().getCodmodulo(),
							hijo.getId().getFilamodulo(),hijo.getId().getColumnamodulo(), codConcCubModVinc, codConcCubMod, hijo.getId().getCodvalor());
					hijo.getId().setCodValorVinc(codValor);
				}
			}
		}
		
		// de las comparativas vinculadas se obtiene el concepto vinculado 
		// y se elimina las comparativas vinculadas
		for (VistaComparativas hijo : hijos) {
			for (VistaComparativas padre : padres) {
				if (	hijo.getId().getFilamodulovinc().equals(padre.getId().getFilamodulo()) 
						&& hijo.getId().getColumnamodulovinc().equals(padre.getId().getColumnamodulo())
						&& (
								((hijo.getId().getCodValorVinc()!= null) && 
									hijo.getId().getCodValorVinc().toString().equals(padre.getId().getCodvalor().toString())) 
					    		||
					    		((hijo.getId().getCodValorVinc()== null) && 
					    				hijo.getId().getDesvalor().equals(padre.getId().getDesvalor())))
					    ){
					padre.getFilasVinculadas().add(hijo);
					comparativas.remove(hijo);
					break;
				}
			}
		}
		
		ArrayList<VistaComparativas> hijosHuerfanos = new ArrayList<VistaComparativas>();
		
		if (comparativas != null) {
			for (VistaComparativas vistaComparativa : comparativas) {
				if (vistaComparativa.getId().getDatovinculado().toString()
						.equals("S")) {
					if (vistaComparativa.getId().getFilamodulovinc().intValue() != 0
							&& vistaComparativa.getId().getColumnamodulovinc()
									.intValue() != 0) {

						hijosHuerfanos.add(vistaComparativa);
					}
				}
			}
		}
		for(VistaComparativas hijoHuerfano : hijosHuerfanos){
			for (VistaComparativas padre : padres) {
				for(VistaComparativas hijo : padre.getFilasVinculadas()){
					if (hijo.getId().getDesvalor().equals(hijoHuerfano.getId().getDesvalor()) && hijoHuerfano.getId().getFilamodulovinc().equals(hijo.getId().getFilamodulo()) && hijoHuerfano.getId().getColumnamodulovinc().equals(hijo.getId().getColumnamodulo())) {
						padre.getFilasVinculadas().add(hijoHuerfano);
						comparativas.remove(hijoHuerfano);
						break;
					}
				}
			}
		}
		
		logger.debug("end - [metodo] datosVinculadosNUEVO");
	}

	private List<List<VistaComparativas>> agruparElementosComparativa(List<VistaComparativas> elementosComparativa){
		
		Map<BigDecimal, List<VistaComparativas>> mapGruposElementosComparativa = new HashMap<BigDecimal, List<VistaComparativas>>();
		List<List<VistaComparativas>> listaGruposElementosComparativa = new ArrayList<List<VistaComparativas>>();
		
		for(VistaComparativas elementoComparativa : elementosComparativa){
			
			List<VistaComparativas> grupoElementosComparativa = null;
			BigDecimal filaModulo = elementoComparativa.getId().getFilamodulo();
			
			if(mapGruposElementosComparativa.containsKey(filaModulo)){
			
				grupoElementosComparativa = mapGruposElementosComparativa.get(filaModulo);
			
			}
			else {	
				
				grupoElementosComparativa = new ArrayList<VistaComparativas>();
				listaGruposElementosComparativa.add(grupoElementosComparativa);
				mapGruposElementosComparativa.put(filaModulo, grupoElementosComparativa);
			}
			
			grupoElementosComparativa.add(elementoComparativa);
			
		}
		
		logger.debug("end - [metodo] agruparElementosComparativa");
		return listaGruposElementosComparativa;
	}
	
	private Integer[] generarVectorContadores(List<List<VistaComparativas>> gruposElementosComparativa){
		
		Integer [] vectorContadores = new Integer[gruposElementosComparativa.size()];
		Arrays.fill(vectorContadores, 0);
		
		return vectorContadores;
	}
	
	private void crearCombinacionElementosComparativa(List<List<VistaComparativas>> gruposElementosComparativa, Integer [] vectorContadores, List<List<VistaComparativas>> combinacionesElementosComparativa){
		if(gruposElementosComparativa.size() > 1 ) {
		
			if(vectorContadores[0] >= gruposElementosComparativa.get(0).size())
				return;
		
			List<VistaComparativas> combinacionElementosComparativa = new ArrayList<VistaComparativas>();
			
			for(int pos = 0; pos < vectorContadores.length ; pos++) {
				combinacionElementosComparativa.add(gruposElementosComparativa.get(pos).get(vectorContadores[pos]));
			}
			
			Collections.sort(combinacionElementosComparativa, new VistaComparativaComparator());
			
			combinacionesElementosComparativa.add(combinacionElementosComparativa);
			
			aumentarVectorContadores(gruposElementosComparativa, vectorContadores);
			
			crearCombinacionElementosComparativa(gruposElementosComparativa, vectorContadores, combinacionesElementosComparativa);
			
		} else if(gruposElementosComparativa.size() == 1 ) {
				
			for(VistaComparativas elementoComparativa : gruposElementosComparativa.get(0)){
				
				List<VistaComparativas> combinacionElementosComparativaSimple = new ArrayList<VistaComparativas>();
				combinacionElementosComparativaSimple.add(elementoComparativa);
				combinacionesElementosComparativa.add(combinacionElementosComparativaSimple);
				
			}
				
		}
	}
	
	private void aumentarVectorContadores(List<List<VistaComparativas>> gruposElementosComparativa, Integer [] vectorContadores){
		
		for(int i = vectorContadores.length-1 ; i>=0 ; i--){

			if(vectorContadores[i] == gruposElementosComparativa.get(i).size()-1 && i != 0){
				vectorContadores[i] = 0;
			} else {
				vectorContadores[i]++;
				break;
			}
		}
		
	}
	
	private List<List<Integer>> crearCombinacionesGruposElementosElegibles(List<List<VistaComparativas>> gruposElementosComparativa){
		
		List<Integer> posicionGruposElementosSoloElegibles = new ArrayList<Integer>();
		List<List<Integer>> combinacionesPosicionGruposElementosSoloElegibles = new ArrayList<List<Integer>>();
		
		for(int i=0; i<gruposElementosComparativa.size(); i++){
			if(gruposElementosComparativa.get(i).size() == 1 && gruposElementosComparativa.get(i).get(0).getId().getElegible() == 'S')
				posicionGruposElementosSoloElegibles.add(i);
		}
		
		if(!posicionGruposElementosSoloElegibles.isEmpty()) {

			Integer [] prefix = {};
			comb4(prefix, posicionGruposElementosSoloElegibles, combinacionesPosicionGruposElementosSoloElegibles);
		
		}
		
		logger.debug("end - [metodo] crearCombinacionesGruposElementosElegibles");
		return combinacionesPosicionGruposElementosSoloElegibles;
	}
	
	private void comprobarHijosCorrectos(List<List<VistaComparativas>> combinacionesElementosComparativa){
		
		for(List<VistaComparativas> combinacionElementos : combinacionesElementosComparativa){
			
			for(int j=0;j<combinacionElementos.size();j++){ 
				
				VistaComparativas elementoPadreComparativa = combinacionElementos.get(j);
				
				if(!elementoPadreComparativa.getFilasVinculadas().isEmpty()){
					
					List<VistaComparativas> hijosBastardos = new ArrayList<VistaComparativas>();
					
					int i=0;
					
					while(i<elementoPadreComparativa.getFilasVinculadas().size()){
						
						VistaComparativas elementoHijoComparativa = elementoPadreComparativa.getFilasVinculadas().get(i);
						
						if(!elementoHijoComparativa.getId().getCodconceptoppalmod().equals(elementoPadreComparativa.getId().getCodconceptoppalmod()) ||
						   !elementoHijoComparativa.getId().getCodriesgocubierto().equals(elementoPadreComparativa.getId().getCodriesgocubierto())){
							
							elementoPadreComparativa.getFilasVinculadas().remove(elementoHijoComparativa);
							hijosBastardos.add(elementoHijoComparativa);
							
						} else{
							i++;
						}
					}
					
					if(!hijosBastardos.isEmpty()){
						
						agruparHijosBastardos(hijosBastardos, combinacionElementos);
						
					}
				}
			}
		}
	}
	
	private List<List<VistaComparativas>> crearCombinacion(List<List<VistaComparativas>> combinacionesElementosComparativa, List<Integer> combinacionPosicionGruposElementosSoloElegibles){
		
		List<List<VistaComparativas>> combinacionesElementosComparativaAux = new ArrayList<List<VistaComparativas>>();
		for(int i=0; i<combinacionesElementosComparativa.size(); i++){
			
			if(!combinacionPosicionGruposElementosSoloElegibles.contains(i))
				combinacionesElementosComparativaAux.add(combinacionesElementosComparativa.get(i));
		}
		
		return combinacionesElementosComparativaAux; 
	}
	
	private void anadirElementosElegiblesNoSeleccionados(List<VistaComparativas> comparativa, ArrayList<VistaComparativas> elementosElegiblesModulo){
		for(int i=0;i<elementosElegiblesModulo.size();i++){
			
			VistaComparativas elementoElegibleModulo = elementosElegiblesModulo.get(i);
			
			boolean elegibleEnComparativa = false;
			
			for(int j=0;j<comparativa.size() && ! elegibleEnComparativa;j++){
				
				VistaComparativas elementoComparativa = comparativa.get(j);
				if(elementoElegibleModulo.getId().equals(elementoComparativa.getId()))
					elegibleEnComparativa = true;
			}
			
			if(!elegibleEnComparativa){
				elementoElegibleModulo.getId().setCodvalor(new BigDecimal(-2));
				comparativa.add(elementoElegibleModulo);
			}
			
		}
	}
	
	/**
	 * Metodo para crear la comparativa cuando para un modulo no hay combinaciones posibles
	 * @param comparativaModulo Mapa donde se anhadira¡ la comparativa
	 * @param lineaseguroid Identificador de plan/linea
	 * @param modulo Modulo para el que queremos crear la comparativa
	 */
	private void crearComparativaUnica(LinkedHashMap<String, Object> comparativaModulo, Long lineaseguroid, String modulo, BigDecimal clase) {
		ModuloId auxId = new ModuloId();
		auxId.setCodmodulo(modulo);
		auxId.setLineaseguroid(lineaseguroid);
		String desModulo = ((Modulo) this.polizaDao.getObject(Modulo.class, auxId)).getDesmodulo();
		String codlinea = ((Linea) this.polizaDao.getObject(Linea.class, lineaseguroid)).getCodlinea().toString();
		
		List<RiesgoCubiertoModulo> lstRcm = this.riesgoCubiertoModuloDao.getRiesgosCubiertosModuloPoliza(auxId);
		List<RiesgoCubiertoModulo> lstRcmDef = new ArrayList<RiesgoCubiertoModulo>();

		// #############################################################
		// IGT 24/04/2014 
		// Obtenció® ¤el Riesgo Cubierto a aplicar en funció® ¤e la clase
		boolean sesgarTemp = false;		
		List<BigDecimal> listaTiposCapital = getListaTiposCapital(
				lineaseguroid, clase, modulo);
		// Recorre el listado de riesgos cubiertos para el modulo y
		// lineaseguroid seleccionados
		for (RiesgoCubiertoModulo rcmDef : lstRcm) {
			// Si el CPM de este riesgo cubierto junto con los de la clase
			// asociada a la pó¬©ºa
			// (codCultivo y codSistemaCultivo) no aparece en la tabla
			// TB_CPM_TIPO_CAPITAL, no se muestra la l?a correspondiente
			// en el cuadro de coberturas
			// Si el CPM no esta¡ permitido se va a la siguiente iteracion del
			// bucle
			if (!this.isCPMPermitido(lineaseguroid, clase, modulo, rcmDef
					.getConceptoPpalModulo().getCodconceptoppalmod(),
					new BigDecimal(0), false, new ArrayList<BigDecimal>(),
					listaTiposCapital))
				continue;

			// Si es nulo, se obtiene lineaseguroid del objeto modulo y la
			// clase se pasa a null
			sesgarTemp = false;
			if (!StringUtils.nullToString(rcmDef.getFichvinculacionexterna())
					.equals("135")) {
				lstRcmDef.add(rcmDef); // si no tiene el 135
			} else {// tiene el 135
				sesgarTemp = sesgarFilaModulos(lineaseguroid, clase, rcmDef
						.getConceptoPpalModulo().getCodconceptoppalmod(),
						rcmDef.getRiesgoCubierto().getId()
								.getCodriesgocubierto(), rcmDef.getModulo()
								.getId().getCodmodulo());
				if (sesgarTemp == true) {
					lstRcmDef.add(rcmDef);
				}
			}
		}
		// sesgar en la linea 309 los que no coinciden el sistema de cultivo de
		// la clase con el conceptoPpalmod.
		if (codlinea.equals("309")) {
			List<RiesgoCubiertoModulo> lstRcmParaSesgar = new ArrayList<RiesgoCubiertoModulo>();
			boolean sesgarSecanoRegadio = false;
			for (RiesgoCubiertoModulo rcmDef2 : lstRcmDef) {
				sesgarSecanoRegadio = false;
				if ((rcmDef2.getConceptoPpalModulo().getCodconceptoppalmod()
						.equals(Constants.CONCEPTO_PPAL_MODULO_SECANO) || rcmDef2
						.getConceptoPpalModulo().getCodconceptoppalmod()
						.equals(Constants.CONCEPTO_PPAL_MODULO_REGADIO))) {

					sesgarSecanoRegadio = sesgar309SecanoRegadio(lineaseguroid,
							clase, rcmDef2.getConceptoPpalModulo()
									.getCodconceptoppalmod());
					if (sesgarSecanoRegadio) {
						lstRcmParaSesgar.add(rcmDef2);
					}
				}
			}
			// elimino la de la lista lstRcmDef los q no cumplen con:mismo
			// conceptoppalmod y sistema cultivo d la clase
			lstRcmDef.removeAll(lstRcmParaSesgar);
		}
		
		VistaComparativas vista0 = new VistaComparativas();
		VistaComparativasId vista0Id = new VistaComparativasId();
		
		vista0Id.setCodconceptoppalmod(lstRcmDef.get(0).getConceptoPpalModulo().getCodconceptoppalmod());
		vista0Id.setCodriesgocubierto(lstRcmDef.get(0).getRiesgoCubierto().getId().getCodriesgocubierto());
		vista0Id.setFilamodulo(lstRcmDef.get(0).getId().getFilamodulo());
		// FIN IGT 24/04/2014
		// #############################################################
		
		//PRUEBA DE CODIGO DE CONCEPTO (A VER SI FUNCIONA)
		vista0Id.setCodconcepto(new BigDecimal(2));		
		vista0Id.setCodmodulo(modulo);
		vista0Id.setCodvalor(new BigDecimal(-2));
		vista0Id.setColumnamodulo(new BigDecimal(0));
		vista0Id.setColumnamodulovinc(new BigDecimal(0));
		vista0Id.setDatovinculado('N');
		vista0Id.setDesconceptoppalmod("");
		vista0Id.setDesmodulo(desModulo);
		vista0Id.setDesriesgocubierto("");
		vista0Id.setDesvalor("");
		vista0Id.setElegible('N');
		vista0Id.setFilamodulovinc(new BigDecimal(0));
		vista0Id.setLineaseguroid(lineaseguroid);
		vista0Id.setNomconcepto("");
				
		vista0.setId(vista0Id);
		
		List<List<VistaComparativas>> combinacionesElementosComparativa = new ArrayList<List<VistaComparativas>>();
		List<VistaComparativas> combinacionVaciaElementosComparativa = new ArrayList<VistaComparativas>();
		combinacionVaciaElementosComparativa.add(vista0);
		combinacionesElementosComparativa.add(combinacionVaciaElementosComparativa);
		
		comparativaModulo.put(modulo + " - " + desModulo, combinacionesElementosComparativa);
	}
	
	private String getMSGAclaracionModulo (String codmodulo,Long lineaseguroid){
		String msg = "";
		try {
			msg = polizaDao.getMSGAclaracionModulo(codmodulo,lineaseguroid);
		} 
		catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar el mensaje de aclaracion del modulo",dao);
		}
		return msg;
	}
	
	//metodo para comprobar si la caracteristica elegible ha de estar tachada en la columna de garantizado.
	private boolean comprobarAsegAutorizado(List<AseguradoAutorizado> lstAsegGarantizadosAplicables, String codmodulo, BigDecimal codGarantizado,BigDecimal ConceptoPrincipalModulo, BigDecimal CodRiesgoCubierto) {
		logger.debug("comprobarAsegAutorizado(modulo="+codmodulo+", codgarantizado="+codGarantizado + ", CPMod="+ConceptoPrincipalModulo+", CRCub="+CodRiesgoCubierto+")");
		boolean tachar=true;
		for (AseguradoAutorizado asegAut : lstAsegGarantizadosAplicables){
			if (asegAut.getModulo().getId().getCodmodulo().equals(codmodulo) && asegAut.getCodgarantizado().toString().equals(codGarantizado.toString()) && asegAut.getCpmodcg().toString().equals(ConceptoPrincipalModulo.toString()) && asegAut.getCodrcubcg().toString().equals(CodRiesgoCubierto.toString())){
				tachar= false;
				break;
			}
		}
		return tachar;
	}
	
	/**
	 * Metodo para borrar las comparativas que el asegurado no puede elegir y las vinculadas a estas.
	 * @param comparativasSubvencionables Lista total de comparativas
	 * @param comparativasFichVincExt Comparativas que no se pueden elegir
	 * @return
	 */
	private ArrayList<VistaComparativas> limpiaComparativasVinculadas(
			ArrayList<VistaComparativas> comparativasSubvencionables,
			ArrayList<VistaComparativas> comparativasFichVincExt) {
		logger.debug("init - [metodo] limpiaComparativasVinculadas");
		
		ArrayList<VistaComparativas> comparativasSubvencionablesDef = new ArrayList<VistaComparativas>();
		
		for (VistaComparativas vComp : comparativasSubvencionables){
			//Si la comparativa tiene fila y columna vinculadas, la busco en comparativasFichVincExt para saber si debo eliminarla
			if (vComp.getId().getFilamodulovinc() != null && vComp.getId().getColumnamodulovinc() != null){
				for (VistaComparativas vCompExt : comparativasFichVincExt){
					//Si fila, columna y valor coinciden con los vinculados => esta comparativa no se devuelve
					if (vCompExt.getId().getFilamodulo().equals(vComp.getId().getFilamodulovinc()) &&
							vCompExt.getId().getColumnamodulo().equals(vComp.getId().getColumnamodulovinc()) &&
							vCompExt.getId().getDesvalor().equals(vComp.getId().getDesvalor())){
						//anhado esta comparativa a las de fichero de vinculacion externa por si hay alguna otra comparativa vinculada a esta.
						comparativasFichVincExt.add(vComp);
						if (comparativasSubvencionablesDef.contains(vComp))
							comparativasSubvencionablesDef.remove(vComp);
						break;
					}
					else{
						if (!comparativasSubvencionablesDef.contains(vComp))
							comparativasSubvencionablesDef.add(vComp);
					}
				}
			}
			else{
				comparativasSubvencionablesDef.add(vComp);
			}
		}
		
		logger.debug("end - [metodo] limpiaComparativasVinculadas");
		return comparativasSubvencionablesDef;
	}
	
	private void agruparHijosBastardos(List<VistaComparativas> hijosBastardos, List<VistaComparativas> combinacionesElementosComparativa){
		for(VistaComparativas hijoBastardo : hijosBastardos){
			
			boolean hijoAdoptado = false;
			
			for(VistaComparativas combinacionElementosComparativa : combinacionesElementosComparativa){
				
				if(hijoBastardo.getId().getCodconceptoppalmod().equals(combinacionElementosComparativa.getId().getCodconceptoppalmod()) &&
				   hijoBastardo.getId().getCodriesgocubierto().equals(combinacionElementosComparativa.getId().getCodriesgocubierto())){
					
					combinacionElementosComparativa.getFilasVinculadas().add(hijoBastardo);
					hijoAdoptado = true;
					break;
				}
				
			}
			
			if(!hijoAdoptado)
				combinacionesElementosComparativa.add(hijoBastardo);
		}
	
		logger.debug("end - [metodo] agruparHijosBastardos");
	}
	
	private void comb4(Integer[] prefix, List<Integer> vector, List<List<Integer>> combinaciones) {
		
    	if(prefix.length > 0)
    		combinaciones.add(Arrays.asList(prefix));
    	
        for (int i = 0; i < vector.size(); i++){
        	
        	Integer[] prefixAux = new Integer[prefix.length+1];
        	System.arraycopy(prefix, 0, prefixAux, 0, prefix.length);
        	
        	prefixAux[prefixAux.length-1] = vector.get(i);
        	
            comb4(prefixAux, vector.subList(i+1, vector.size()), combinaciones);
        }
    }
	
	/**
	 * Metodo que crea un tabla HTML con los datos de las coberturas de una poliza complementaria
	 * @param moduloView
	 * @return
	 */
	private String crearTablaCpl(ModuloView moduloView){
		logger.debug("init - crearTablaCpl");
		String resultado = "";
		resultado = "<table width='70%' id='tablaCpl'>" +
		"<tr>" +
			"<td class='literalbordeCabecera' align='center' width='15%'>CONCEPTO PRINCIPAL DEL MODULO</td>" + 
			"<td class='literalbordeCabecera' align='center' width='15%'>RIESGO CUBIERTO</td>" +
		"</tr>";
		for(ModuloFilaView fila:moduloView.getListaFilas()){
			resultado+="<tr>"+ 
				TD_CLASS_LITERALBORDE_ALIGN_CENTER_WIDTH_15+
				fila.getConceptoPrincipalModulo() +
				TD+
				TD_CLASS_LITERALBORDE_ALIGN_CENTER_WIDTH_15+
				fila.getRiesgoCubierto() + 
				TD +
			"</tr>";
		}
		resultado+="</table>";
		logger.debug("end - crearTablaCpl");
		return resultado;
	}
	
	/**
	 * tabla de coberturas actual de nuestra poliza
	 * @param moduloView
	 * @param comparativasElegidas
	 * @return
	 */
	private String crearTablaPpl(ModuloView moduloView, Set<ComparativaPoliza> comparativasElegidas) {
		
		logger.debug("CuadroCoberturasManager - crearTablaPpl - init");
		
		String resultado = "";
		int contador = 1;
		
		int numeroColumnas = moduloView.getListaCabeceras().size();
		int dimensionColumna;
		
		if(numeroColumnas!=0){
		    dimensionColumna = 100/numeroColumnas;
		}else{
			dimensionColumna = 0;
		}
		
		resultado = "<table id='tablaPoliza'>" +
			"<tr>" +
			"<td class='literalbordeCabecera' align='center' width='15%'>CONCEPTO PRINCIPAL DEL MODULO</td>" + 
			"<td class='literalbordeCabecera' align='center' width='15%'>RIESGO CUBIERTO</td>" +
			"<td class='literalbordeCabecera' width='70%'>" +
			"<table width='100%'>" +
			"<tr><td colspan='" + numeroColumnas + "' class='literalbordeCabecera' align='center'>CONDICIONES COBERTURAS</td></tr>" +
			"<tr>";
		for(String  cabeceras : moduloView.getListaCabeceras()){
			resultado +="<td class='literalbordeCabecera' align='center' width='"+dimensionColumna+"%'>" + cabeceras +TD;
		}
		resultado +="</tr></table></td></tr>";
		for(ModuloFilaView fila:moduloView.getListaFilas()){
			resultado+="<tr>" + TD_CLASS_LITERALBORDE_ALIGN_CENTER_WIDTH_15 + fila.getConceptoPrincipalModulo() + TD+
				TD_CLASS_LITERALBORDE_ALIGN_CENTER_WIDTH_15;
			if(fila.getRiesgoCubierto().indexOf("ELEGIBLE") != -1){
				for(ComparativaPoliza cp:comparativasElegidas){
					if(!cp.getId().getCodconcepto().equals(new BigDecimal(ConstantsConceptos.CODCPTO_CARACT_EXPLOTACION)) && 
							!cp.getId().getCodconcepto().equals(new BigDecimal(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO))){
						if(contador == cp.getId().getFilamodulo().intValue()){
							resultado += "" + fila.getRiesgoCubierto() + "<br/><input type='checkbox' disabled='disabled' checked='checked' id='checkPoliza_"+fila.getCodConceptoPrincipalModulo().toString() + "_" + fila.getCodRiesgoCubierto().toString()+"' name='checkPoliza_"+fila.getCodConceptoPrincipalModulo().toString() + "_" + fila.getCodRiesgoCubierto().toString()+"'>";	
						}
					}
				}
			}else{
				resultado += "" + fila.getRiesgoCubierto() + "";	
			}
			resultado += TD + "<td class='literalborde' align='center' width='70%'>" + "<table width='100%'>" + "<tr>";
			//Recorremos las celdas
			for (ModuloCeldaView mcv : fila.getCeldas()){
				resultado += "<td class='literalborde' align='center' width='"+dimensionColumna+"%'>";
				if (mcv.isElegible()){
					resultado +="<font color='red'>ELEGIBLE</font><br/>";
					for (ModuloValorCeldaView mvcv : mcv.getValores()) {
						resultado +="<font>" + mvcv.getDescripcion() + "</font><br/>";
					}				
				} else{
					for(ModuloValorCeldaView valor : mcv.getValores()){
						if(valor.isTachar()){
							resultado +="<strike>" + valor.getDescripcion() +"</strike>";
						}else{
							resultado += valor.getDescripcion();
						}
					}
				}
				if(mcv.getObservaciones()!= null){
					resultado+="<br/>" + mcv.getObservaciones();
				}
				resultado+="&nbsp;</td>";
			}
			resultado += "</tr></table></td>";	
			contador ++;
		}
		resultado+="</tr>" + "</table>";	
		

		logger.debug("TABLA HTML" + resultado);
		
		logger.debug("CuadroCoberturasManager - crearTablaPpl - init");

		
		return resultado;
	}
	
	/* Pet. 63485-Fase II ** MODIF TAM (21.09.2020) ** Inicio */
	public List<RiesgoCubiertoModulo> getRiesgosCubModuloCalcRendimiento(Long lineaseguroid, String codmodulo) throws Exception{
		return this.riesgoCubiertoModuloDao.getRiesgosCubModuloCalcRendimiento(lineaseguroid, codmodulo);
	}
	/* Pet. 63485-Fase II ** MODIF TAM (21.09.2020) ** Fin */
	
	
	public JSONObject getCoberturasPpalCpl(Poliza poliza, String idCupon, String realPath) throws Exception {
		
		logger.debug("CuadroCoberturasManager - getCoberturasPpalCpl - init");
		
		
		JSONObject salida = new JSONObject();
		String xmlPoliza = "";
		String codMod;
		
		try {			
			if (StringUtils.isNullOrEmpty(idCupon)) {
				if (Constants.MODULO_POLIZA_COMPLEMENTARIO.equals(poliza.getTipoReferencia())) {
					codMod = poliza.getCodmodulo();
					xmlPoliza = WSUtils.generateXMLPolizaCoberturasContratadas(poliza, null, null, null, codMod, polizaDao, cpmTipoCapitalDao);
				} else {
					// AQUI SOLO SE LLEGA DESDE LAS PANTALLAS DE COMPLEMENTARIAS
					// ASI QUE USAMOS SIEMPRE EL XML DE LA SITUACION ACTUALIZADA
					PolizaActualizadaResponse response = new SWAnexoModificacionHelper().getPolizaActualizada(poliza.getReferencia(), poliza.getLinea().getCodplan(), realPath);
					xmlPoliza = response.getPolizaPrincipalUnif().getPoliza().toString();
					xmlPoliza = xmlPoliza.replace("xml-fragment", "ns2:Poliza").replace("xmlns:ns2=\"http://www.agroseguro.es/Contratacion\"", "xmlns:ns2=\"http://www.agroseguro.es/PresupuestoContratacion\"");
					codMod = response.getPolizaPrincipalUnif().getPoliza().getCobertura().getModulo();
				}
			} else {
				logger.debug("ES ANEXO");
				XmlObject xmlObj;
				if (Constants.MODULO_POLIZA_COMPLEMENTARIO.equals(poliza.getTipoReferencia())) {
					xmlObj = solicitudModificacionManager.getPolizaActualizadaCplFromCupon(idCupon);
					try {
						codMod = ((es.agroseguro.contratacion.PolizaDocument)xmlObj).getPoliza().getCobertura().getModulo();
					} catch (Exception e) {
						codMod = ((es.agroseguro.seguroAgrario.contratacion.complementario.PolizaDocument)xmlObj).getPoliza().getCobertura().getModulo();
					}
				} else {
					xmlObj = solicitudModificacionManager.getPolizaActualizadaFromCupon(idCupon);
					try {
						codMod = ((es.agroseguro.contratacion.PolizaDocument)xmlObj).getPoliza().getCobertura().getModulo();
					} catch (Exception e) {						
						codMod = ((es.agroseguro.seguroAgrario.contratacion.PolizaDocument)xmlObj).getPoliza().getCobertura().getModulo();
					}
				}
				xmlPoliza = xmlObj.toString().replace("xmlns:ns2=\"http://www.agroseguro.es/Contratacion\"", "xmlns:ns2=\"http://www.agroseguro.es/PresupuestoContratacion\"");
			}			
			
			logger.debug("XML POLIZA: " + xmlPoliza);
			
			// Llama al SW de Ayudas a la contratacion por REST para obtener el xml de coberturascontratadas
			String xmlRespuesta = null;
			
			try {
				codMod = StringUtils.isNullOrEmpty(codMod) ? codMod : codMod.trim();
				logger.debug("codMod: " + codMod);
				
				es.agroseguro.modulosYCoberturas.Modulo modCobXmlResp = WSRUtils.getCoberturasContratadas(xmlPoliza);
				xmlRespuesta = modCobXmlResp.toString();
				logger.debug("getMyCPoliza:xml de respuesta del SW Ayudas contratacion doCoberturasContratadas : " + xmlRespuesta);	
				
				ModulosYCoberturas myc = getMyCFromXml(xmlRespuesta);
				
				ModuloPoliza mp = poliza.getModuloPolizas().iterator().next();
				logger.debug("numComparativa: " + mp.getId().getNumComparativa());
				
				ModuloView mv = getModuloViewFromModulosYCoberturas(myc, mp, mp.getId().getNumComparativa().intValue(), codMod, false);
				
				salida.put("descripcion", mv.getCodModulo() + " - " +  mv.getDescripcionModulo());
				salida.put("tabla", crearTablaPpl(mv, poliza.getComparativaPolizas()));
				
				
				logger.debug("SALIDA JSON:" + salida.toString());
				
				// Se guarda la llamada al SW
				seleccionComparativasSWManager.registrarComunicacionSW(poliza.getIdpoliza(),
						poliza.getLinea().getLineaseguroid(), codMod, mp.getId().getNumComparativa(),
						poliza.getUsuario(), xmlPoliza, xmlRespuesta, SeleccionComparativaSWDao.COBERTURAS_CONTRATADAS);
				
				
			} catch (Exception e) {
				logger.debug("CuadroCoberturasManager - getCoberturasPpalCpl - end");
				logger.error("Ha ocurrido un error en la llamada al SW de Ayudas a la contratacion", e);
				throw e;
			}
			
		} catch (Exception e) {
			logger.error("Ha ocurrido un error al obtener el xml asociado a la poliza", e);
			logger.debug("CuadroCoberturasManager - getCoberturasPpalCpl - end");
			throw e;
		}

		logger.debug("CuadroCoberturasManager - getCoberturasPpalCpl - end");

		return salida;
		
	}	
	
	
	/**
	 * @throws BusinessException 
	 * 
	 */
	@Override
	public JSONObject getCoberturasLectura(Poliza poliza) {
		
		logger.debug("CuadroCoberturasManager - getCoberturasLectura - init");
		
		Clob respuesta = null;
		
		try {
			respuesta = seleccionComparativaSWDao.getRespuestaModulosPolizaCoberturaSW(poliza.getIdpoliza(), poliza.getCodmodulo(), SeleccionComparativaSWDao.COBERTURAS_CONTRATADAS);
			if (null!=respuesta) {
				
				String resp = WSUtils.convertClob2String(respuesta);		
				ModulosYCoberturas myc = seleccionComparativasSWManager.getMyCFromXml(resp);				
				
				ModuloPoliza mp = poliza.getModuloPolizas().iterator().next();
				
				ModuloView mv = getModuloViewFromModulosYCoberturas(myc, mp, mp.getId().getNumComparativa().intValue(), poliza.getCodmodulo(), false);
				
				JSONObject salida = new JSONObject();
				salida.put("descripcion", mv.getCodModulo() + " - " + mv.getDescripcionModulo());
				salida.put("tabla", crearTablaPpl(mv, poliza.getComparativaPolizas()));
				return salida;
			}
			else {
				return getCoberturas(poliza);
			}
		} catch (DAOException e) {
			logger.error("Se ha producido un error al recuperar el xml de la poliza", e);
			logger.debug("CuadroCoberturasManager - getCoberturasLectura - end");
		} catch (JSONException e) {
			logger.error("Se ha producido un error al parsear el xml de la poliza", e);
			logger.debug("CuadroCoberturasManager - getCoberturasLectura - end");
		} catch (BusinessException e) {
			logger.error(e.getMessage(), e);
			logger.debug("CuadroCoberturasManager - getCoberturasLectura - end");
		}
		
		logger.debug("CuadroCoberturasManager - getCoberturasLectura - end");

		return null;
	}
	
	
	/**
	 * Convierte el xml recibido en un objeto ModulosYCoberturas
	 * @param xml
	 * @return
	 */
	protected ModulosYCoberturas getMyCFromXml(String xml) {
		
		ModulosYCoberturas myc = null;
		
		// Convierte el xml recibido en un objeto ModulosYCoberturas
		try {
			myc = ModulosYCoberturasDocument.Factory.parse(xml).getModulosYCoberturas();
		} 
		catch (Exception e) {
			logger.error("Ha ocurrido un error al parsar el xml a un objeto ModulosYCoberturas", e);
		}
		
		return myc;
	}
	
	/**
	 * Procesa el objeto 'ModulosYCoberturas' recibido como parametro y genera un objeto 'ModuloView', que encapsula la informacion
	 * que se mostrara en pantalla de las comparativas de un modulo
	 * @param myc
	 * @param mp
	 * @return
	 */
	public ModuloView getModuloViewFromModulosYCoberturas (final ModulosYCoberturas myc, final ModuloPoliza mp, 
			int numComparativa, String codMod, boolean llenaRenovable) {
		
		logger.debug("SeleccionComparativasSWManager- getModuloViewFromModulosYCoberturas - init");
		
		ModuloView mv = new ModuloView();
		
		logger.debug("mp: " + mp); 
		logger.debug("mp.getId(): " + mp.getId()); 
		logger.debug("mp.getId().getCodmodulo(): " + mp.getId().getCodmodulo()); 
		logger.debug("mp.getId().getLineaseguroid(): " + mp.getId().getLineaseguroid()); 
		logger.debug("moduloManager: " + moduloManager);
		
		// Obtiene el objeto modulo correspondiente al plan/linea y modulo indicados
		Modulo modulo = moduloManager.getModulo(codMod, mp.getId().getLineaseguroid());
		
		// Establece el codigo, la descripcion, el indicador de renovable del modulo y el tipo de asegurado
		mv.setCodModulo(codMod);
		mv.setDescripcionModulo(modulo != null ? modulo.getDesmodulo() : "");
		if(llenaRenovable){
			mv.setRenovable(mp.getRenovable());
			mv.setIdModulo(mp.getId().getNumComparativa());
		}
		mv.setTipoAsegGanado(mp.getTipoAsegGanado());
		mv.setNumComparativa(numComparativa);
		
		// Si no existe el elemento 'Modulo' en el xml no continua el proceso
		if (myc.getModuloArray().length == 0) return mv; 		
		
		// Recorre la lista de coberturas
		Cobertura[] coberturaArray = myc.getModuloArray(0).getCoberturaArray();	
		logger.debug("SeleccionComparativasSWManager- obtenemos coberturaArray");
		
		
		List<ModuloFilaView> listaFilas = new ArrayList<ModuloFilaView>();
		List<DatoVariable> lstCabVariables = new ArrayList<DatoVariable>();
		List<Integer> codCptos = new ArrayList<Integer>();
		List<String> listCabs = new ArrayList<String>();
		// buscamos max cabeceras de cada cobertura
		for (Cobertura cobertura : coberturaArray) {
			for (DatoVariable dv : cobertura.getDatoVariableArray()) {
				if (!codCptos.contains(dv.getCodigoConcepto())){
					lstCabVariables.add(dv);
					codCptos.add(dv.getCodigoConcepto());
				}
			}
		}
		if (mv.getListaCabeceras().isEmpty())
			mv.setListaCabeceras(listCabs);		
		
		Collections.sort(lstCabVariables, new DatoVariableComparator());
		for (DatoVariable dvCab : lstCabVariables) {
			listCabs.add(dvCab.getNombre());
		}
		
		for (Cobertura cobertura : coberturaArray) {
			// Compone el objeto 'ModuloFilaView' que encapsula la informacion de una fila del cuadro de comparativas
			ModuloFilaView mfv = getModuloFilaView(cobertura);
			
			// Compone la lista de celdas variables asociadas a la fila del cuadro de comparativas 
			mfv.setCeldas(getListaCeldas(cobertura,lstCabVariables));
			
			// Compone las listas de vinculaciones al objeto de la fila del cuadro de comparativas
			mfv.setListVinculaciones(getListaVinculaciones(cobertura));
			
			listaFilas.add (mfv);
		}
		Collections.sort(listaFilas, new ModuloFilaViewComparator());
		mv.setListaFilas(listaFilas);
		
		logger.debug("Hay " + mv.getListaFilas().size() +  " filas");
		
		logger.debug("SeleccionComparativasSWManager- Antes de retornar mv");
		
		logger.debug("SeleccionComparativasSWManager- getModuloViewFromModulosYCoberturas - end");

		
		return mv;
	}
	
	/**
	 * @param cobertura
	 * @param mfv
	 */
	private ModuloFilaView getModuloFilaView(Cobertura cobertura) {
		
		ModuloFilaView mfv = new ModuloFilaView();
		
		// Compone la lista de celdas fijas asociadas a la fila del cuadro de comparativas (CPM y RC)
		mfv.setConceptoPrincipalModulo(cobertura.getDescripcionCPM());
		mfv.setRiesgoCubierto(cobertura.getDescripcionRC());
		mfv.setCodConceptoPrincipalModulo(new BigDecimal (cobertura.getConceptoPrincipalModulo()));
		mfv.setCodRiesgoCubierto(new BigDecimal (cobertura.getRiesgoCubierto()));
		mfv.setRcElegible("S".equals(cobertura.getElegible().toString()));
		mfv.setBasica(BASICA.equals(cobertura.getTipoCobertura()));		
		mfv.setFilamodulo(new BigDecimal (cobertura.getFila()));
		mfv.setCodCptoRCE(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO));
		mfv.setFilaComparativa(new BigDecimal (1));
		
		return mfv;
	}
	
	
	/**
	 * @param cobertura
	 * @return
	 */
	private List<ModuloCeldaView> getListaCeldas(Cobertura cobertura,List<DatoVariable> lstCabVariables) {
		List<ModuloCeldaView> celdas = new ArrayList<ModuloCeldaView>();
		boolean existe = false;
		
		for (DatoVariable dvCab : lstCabVariables) {
			existe = false;
			for (DatoVariable dv : cobertura.getDatoVariableArray()) {
				if (dvCab.getNombre().equals(dv.getNombre())){
					existe = true;
					ModuloCeldaView mcv = new ModuloCeldaView();
					
					// Codigo de concepto
					mcv.setCodconcepto(new BigDecimal (dv.getCodigoConcepto()));
					// Valor fijo a 1 porque este valor se corresponde con la fila comparativa de la tabla y solo va a haber una comparativa por modulo
					mcv.setColumna(new BigDecimal (1)); 
					// Indica si la celda tiene valor/es elegible/s (E - Si, otro valor - No)
					mcv.setElegible(ELEGIBLE.equals(dv.getTipoValor()));
					
					// Valor/es
					List<ModuloValorCeldaView> valores = new ArrayList<ModuloValorCeldaView>();
					for (Valor valor : dv.getValorArray()) {
						
						ModuloValorCeldaView mvcv = new ModuloValorCeldaView(valor.getValor(), valor.getDescripcion());
						
						valores.add(mvcv);
					}
					mcv.setValores(valores);
					
					// Observaciones
					if (!"".equals(StringUtils.nullToString(dv.getObservaciones()))) {
						mcv.setObservaciones(dv.getObservaciones());
					}
					celdas.add(mcv);
				}
			}
			if (!existe){ // pintar una celda en blanco
				logger.debug("celda en blanco");
				ModuloCeldaView mcv = new ModuloCeldaView();
				mcv.setObservaciones("");
				celdas.add(mcv);
			}
		}
		return celdas;
	}
	
	
	/**
	 * @param cobertura
	 * @return
	 */
	private List<ModuloFilaVinculacionView> getListaVinculaciones(Cobertura cobertura) {
		int grupoVinc = 0;
		// Compone la lista de vinculaciones de la fila
		List<ModuloFilaVinculacionView> listVinculaciones = new ArrayList<ModuloFilaVinculacionView>();
		
		// Recorre las vinculaciones de la fila y rellena las listas de vinculados
		for (VinculacionFila vf : cobertura.getVinculacionFilaArray()) {
			grupoVinc++;
	
			// Indica si la vinculacion aplica cuando el riesgo se ha elegido o cuando no 
			boolean elegida = "S".equals(vf.getElegida().toString());
			
			for (Fila fila : vf.getFilaArray()) {
				ModuloFilaVinculacionView mfvv = new ModuloFilaVinculacionView();
				mfvv.setGrupoVinculacion(grupoVinc);
				mfvv.setElegida(elegida);
				mfvv.setVincFila(fila.getFila());
				mfvv.setVincElegida("S".equals(fila.getElegida().toString()));
				listVinculaciones.add(mfvv);
			}
			
			
		}
		return listVinculaciones;
	}
	

	public void setClaseDao(IClaseDao claseDao) {
		this.claseDao = claseDao;
	}

	public void setClaseDetalleDao(IClaseDetalleDao claseDetalleDao) {
		this.claseDetalleDao = claseDetalleDao;
	}

	public void setAseguradoAutorizadoDao(
			IAseguradoAutorizadoDao aseguradoAutorizadoDao) {
		this.aseguradoAutorizadoDao = aseguradoAutorizadoDao;
	}

	public void setCpmTipoCapitalDao(ICPMTipoCapitalDAO cpmTipoCapitalDao) {
		this.cpmTipoCapitalDao = cpmTipoCapitalDao;
	}

	public void setRiesgoCubiertoModuloDao(
			IRiesgoCubiertoModuloDao riesgoCubiertoModuloDao) {
		this.riesgoCubiertoModuloDao = riesgoCubiertoModuloDao;
	}

	public void setCaracteristicaModuloDao(
			ICaracteristicasModuloDao caracteristicaModuloDao) {
		this.caracteristicaModuloDao = caracteristicaModuloDao;
	}

	public void setCuadroCoberturasDao(ICuadroCoberturasDao cuadroCoberturasDao) {
		this.cuadroCoberturasDao = cuadroCoberturasDao;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}
	public void setModuloManager(ModuloManager moduloManager) {
		this.moduloManager = moduloManager;
	}

	public ISeleccionComparativaSWDao getSeleccionComparativaSWDao() {
		return seleccionComparativaSWDao;
	}

	public void setSeleccionComparativaSWDao(ISeleccionComparativaSWDao seleccionComparativaSWDao) {
		this.seleccionComparativaSWDao = seleccionComparativaSWDao;
	}

	public SeleccionComparativasSWManager getSeleccionComparativasSWManager() {
		return seleccionComparativasSWManager;
	}

	public void setSeleccionComparativasSWManager(SeleccionComparativasSWManager seleccionComparativasSWManager) {
		this.seleccionComparativasSWManager = seleccionComparativasSWManager;
	}
	
	public ISolicitudModificacionManager getSolicitudModificacionManager() {
		return solicitudModificacionManager;
	}

	public void setSolicitudModificacionManager(ISolicitudModificacionManager solicitudModificacionManager) {
		this.solicitudModificacionManager = solicitudModificacionManager;
	}

	@Override
	public AnexoModificacion getAnexo(String idAnexo) {
		return cuadroCoberturasDao.getAnexo(idAnexo);
	}
	
}
