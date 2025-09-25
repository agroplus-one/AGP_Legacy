package com.rsi.agp.core.managers.impl; 
 
import java.lang.reflect.Method; 
import java.math.BigDecimal; 
import java.util.ArrayList; 
import java.util.HashMap; 
import java.util.HashSet; 
import java.util.LinkedHashMap; 
import java.util.List; 
import java.util.Map; 
import java.util.Set; 
 
import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory; 
import org.apache.xmlbeans.XmlObject; 
import org.hibernate.Criteria; 
import org.hibernate.Session; 
import org.hibernate.criterion.Restrictions; 
 
import com.rsi.agp.core.exception.BusinessException; 
import com.rsi.agp.core.exception.DAOException; 
import com.rsi.agp.core.exception.ValidacionAnexoModificacionException; 
import com.rsi.agp.core.managers.ICuadroCoberturasManager; 
import com.rsi.agp.core.managers.IManager; 
import com.rsi.agp.core.managers.impl.anexoMod.ISeleccionComparativasAnexoSWManager; 
import com.rsi.agp.core.managers.impl.anexoMod.solicitud.ISolicitudModificacionManager; 
import com.rsi.agp.core.managers.impl.anexoMod.util.AnexoModificacionUtils; 
import com.rsi.agp.core.util.CollectionsAndMapsUtil; 
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.OrganizadorInfoConstants; 
import com.rsi.agp.core.util.XmlTransformerUtil; 
import com.rsi.agp.core.webapp.util.StringUtils; 
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException; 
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject; 
import com.rsi.agp.dao.filters.Filter; 
import com.rsi.agp.dao.models.anexo.ICoberturasModificacionPolizaDao; 
import com.rsi.agp.dao.models.anexo.IDeclaracionModificacionPolizaDao; 
import com.rsi.agp.dao.models.anexo.IXmlAnexoModificacionDao; 
import com.rsi.agp.dao.models.copy.IPolizaCopyDao; 
import com.rsi.agp.dao.models.cpm.ICPMTipoCapitalDAO; 
import com.rsi.agp.dao.models.poliza.ILineaDao; 
import com.rsi.agp.dao.tables.anexo.AnexoModSWComparativas;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion; 
import com.rsi.agp.dao.tables.anexo.Cobertura; 
import com.rsi.agp.dao.tables.anexo.CoberturaSeleccionada; 
import com.rsi.agp.dao.tables.commons.Usuario; 
import com.rsi.agp.dao.tables.copy.CoberturaPoliza; 
import com.rsi.agp.dao.tables.copy.Poliza; 
import com.rsi.agp.dao.tables.cpl.CaracteristicaModuloId; 
import com.rsi.agp.dao.tables.cpl.Modulo; 
import com.rsi.agp.dao.tables.cpl.ModuloCeldaView; 
import com.rsi.agp.dao.tables.cpl.ModuloFilaView; 
import com.rsi.agp.dao.tables.cpl.ModuloValorCeldaView; 
import com.rsi.agp.dao.tables.cpl.ModuloView; 
import com.rsi.agp.dao.tables.cpl.VinculacionValoresModulo; 
import com.rsi.agp.dao.tables.org.OrganizadorInformacion; 
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza; 
import com.rsi.agp.dao.tables.poliza.ModuloPoliza; 
 
import es.agroseguro.modulosYCoberturas.ModulosYCoberturas; 
 
public class CoberturasModificacionPolizaManager implements IManager { 
	
	private static final String IF_VALOR = "if(valor == ";
	private static final String SELECT_ANEXO_1_2_362_VAL_1 = "$('#selectAnexo_1_2_362').val(1);";
	private static final String SELECT_ANEXO_1_2_362_VAL_2 = "$('#selectAnexo_1_2_362').val(2);";
	private static final String SELECT_ANEXO_1_2_170_VAL_1 = "$('#selectAnexo_1_2_170').val(1);";
	private static final String SELECT_ANEXO_1_2_174_VAL_2 = "$('#selectAnexo_1_2_174').val(2);";
	private static final String SELECT_ANEXO_1_2_120_VAL_2 = "$('#selectAnexo_1_2_120').val(2);}";
	private static final String SELECT_ANEXO_1_2_121_VAL_3 = "$('#selectAnexo_1_2_121').val(3);";
	private static final String SELECT_ANEXO_1_2_174_VAL_1 = "$('#selectAnexo_1_2_174').val(1);";
	private static final String RIESGOS_POLIZA = "riesgosPoliza";
	private static final String CONCEPTOS_POLIZA = "conceptosPoliza";
	private static final String COBERT = "cobert : ";
	
	private Log logger = LogFactory.getLog(CoberturasModificacionPolizaManager.class); 
	private ICoberturasModificacionPolizaDao coberturasModificacionPolizaDao; 
	private IDeclaracionModificacionPolizaDao declaracionModificacionPolizaDao; 
	private IPolizaCopyDao polizaCopyDao; 
	private IXmlAnexoModificacionDao xmlAnexoModDao; 
	private PolizaManager polizaManager; 
	private ICuadroCoberturasManager cuadroCoberturasManager; 
	private ModuloManager moduloManager; 
	private ICPMTipoCapitalDAO cpmTipoCapitalDao; 
	private ILineaDao lineaDao; 
 
	// private IDiccionarioDatosDao diccionarioDatosDao; 
	private ISolicitudModificacionManager solicitudModificacionManager; 
	private ISeleccionComparativasAnexoSWManager seleccionComparativasAnexoSWManager; 
 
	private ModulosYCoberturas modulosYCoberturas;
	
	/** 
	 * Recupera las coberturas de la Poliza o de la Copy y las del Anexo 
	 *  
	 * @param idAnexo 
	 * @return 
	 * @throws BusinessException 
	 */ 
	public HashMap<String, Object> getCoberturas(AnexoModificacion anexo) throws BusinessException { 
		HashMap<String, Object> parametros = new HashMap<String, Object>(); 
		Poliza polizaCopy; 
		List<Modulo> modulosPoliza; 
		List<Cobertura> coberturas = new ArrayList<Cobertura>(); 
		String codModulo; 
		Modulo moduloPoliza = new Modulo(); 
		Modulo moduloAnexo = new Modulo(); 
		boolean isSubvencionable = false; 
		Long lineaseguroid; 
		String nifcif; 
		logger.debug("[INIT] getCoberturas"); 
		try { 
			lineaseguroid = anexo.getPoliza().getLinea().getLineaseguroid(); 
			nifcif = anexo.getPoliza().getAsegurado().getNifcif(); 
 
			// 2. Recuperamos las coberturas de la Poliza o de la Copy 
			if (anexo.getIdcopy() != null && anexo.getIdcopy() > 0) { 
				// Obtenemos la Copy 
				polizaCopy = polizaCopyDao.getPolizaCopyById(anexo.getIdcopy()); 
 
				codModulo = polizaCopy.getCodmodulo().trim(); 
 
				List<CoberturaPoliza> coberturasCopy = coberturasModificacionPolizaDao 
						.getCoberturasCopy(polizaCopy.getId()); 
 
				// Transformamos la CoberturaPoliza a Cobertura 
				for (CoberturaPoliza cobCopy : coberturasCopy) { 
					Cobertura cobertura = new Cobertura();
					cobertura.setCodconceptoppalmod(cobCopy.getCodconceptoppalmod()); 
					cobertura.setCodriesgocubierto(cobCopy.getCodriesgocubierto()); 
					cobertura.setCodconcepto(cobCopy.getCodconcepto()); 
					cobertura.setCodvalor(cobCopy.getCodvalor()); 
					logger.debug(cobertura.getCodconcepto() + " - " + cobertura.getCodvalor()); 
					coberturas.add(cobertura); 
				} 
			} else { 
				codModulo = ((ModuloPoliza) anexo.getPoliza().getModuloPolizas().toArray()[0]).getId().getCodmodulo(); 
 
				List<ComparativaPoliza> coberturasPoliza = coberturasModificacionPolizaDao 
						.getCoberturasPoliza(anexo.getPoliza().getIdpoliza()); 
 
				for (ComparativaPoliza cobPoliza : coberturasPoliza) { 
					Cobertura cobertura = new Cobertura(); 
 
					// Transformamos la ComparativaPoliza a Cobertura, sin 
					// tener en cuenta la Caracteristica de Explotacion 
					if (!BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CARACT_EXPLOTACION).equals(cobPoliza.getId().getCodconcepto())) {
						cobertura.setCodconceptoppalmod(cobPoliza.getId().getCodconceptoppalmod()); 
						cobertura.setCodriesgocubierto(cobPoliza.getId().getCodriesgocubierto()); 
						cobertura.setCodconcepto(cobPoliza.getId().getCodconcepto()); 
						cobertura.setCodvalor(cobPoliza.getId().getCodvalor() != null ? cobPoliza.getId().getCodvalor().toString() : ""); 
						logger.debug(cobertura.getCodconcepto() + " - " + cobertura.getCodvalor()); 
						coberturas.add(cobertura); 
					} 
				} 
			} 
 
			parametros.put("coberturas", coberturas); 
 
			// 3. Recuperamos todos los modulos de la Poliza 
			modulosPoliza = coberturasModificacionPolizaDao.getModulosPoliza(lineaseguroid); 
			parametros.put("modulos", modulosPoliza); 
 
			// 4. Obtenemos el modulo seleccionado de la Poliza 
			if (modulosPoliza.size() > 0) { 
				for (Modulo modulo : modulosPoliza) { 
					if (codModulo.equals(modulo.getId().getCodmodulo())) { 
						moduloPoliza = modulo; 
					} 
 
					if (anexo.getCodmodulo() != null && anexo.getCodmodulo().equals(modulo.getId().getCodmodulo())) { 
						moduloAnexo = modulo; 
					} 
				} 
			} 
			// modulo seleccionado de la poliza 
			parametros.put("moduloPoliza", moduloPoliza); 
 
			// 5. Obtenemos las coberturas del Anexo 
			if (anexo.getCoberturas().size() == 0) { 
				moduloAnexo = moduloPoliza; 
 
				Set<Cobertura> newCoberturas = new HashSet<Cobertura>(coberturas.size()); 
				for (Cobertura cob : coberturas) { 
					newCoberturas.add(cob); 
				} 
 
				anexo.setCoberturas(newCoberturas); 
			} 
 
			// modulo seleccionado del anexo 
			parametros.put("moduloAnexo", moduloAnexo); 
			// coberturas seleccionadas del anexo 
			parametros.put("coberturasAnexo", anexo.getCoberturas()); 
 
			// 7. Comprobamos si el Asegurado esta autoriza a contratar todas las coberturas 
			isSubvencionable = coberturasModificacionPolizaDao.isUsuarioAutorizado(lineaseguroid, nifcif); 
			parametros.put("autorizado", isSubvencionable); 
			logger.debug("[END] getCoberturas"); 
			return parametros; 
 
		} catch (DAOException dao) { 
			throw new BusinessException( 
					"Se ha producido un error al recuperar las Coberturas del Anexo de Modificacion", dao); 
		} 		
	} 
 
	public Modulo getModulo(Long lineaseguroid, String codmodulo) throws BusinessException { 
		try { 
 
			return coberturasModificacionPolizaDao.getModulo(lineaseguroid, codmodulo); 
 
		} catch (DAOException dao) { 
			throw new BusinessException("Se ha producido un error al cargar el Modulo " + codmodulo, dao); 
		} 
	} 
 
	public boolean guardarCoberturas(AnexoModificacion anexo, com.rsi.agp.dao.tables.poliza.Poliza poliza, Usuario usuario, 
			String coberturasModificadas, HashMap<String, HashMap<String, Cobertura>> coberturas, String comboModulo) 
			throws ValidacionAnexoModificacionException, BusinessException { 
 
		boolean isGrabado = false;
		List<Cobertura> listCoberturas = new ArrayList<Cobertura>(); 
		HashMap<String, Object> comparativas = null; 
		boolean hayComprativasElegibles = false; 
		String[] modSelec = null; 
		
		/**** ESC-14587 ** MODIF TAM (15.07.2021) ** Inicio ***/
		/*1º Identificamos si se han dado de alta parcelas nuevas */
		/* En cuyo caso se dan de alta las coberturas nuevamente por si se han añadido nuevas coberturas */
		boolean revisarCoberturasAnx = false;
		boolean esGanado = anexo.getPoliza().getLinea().isLineaGanado();
		
		if(anexo.getParcelas() != null && !anexo.getParcelas().isEmpty() && !esGanado) {
			for (com.rsi.agp.dao.tables.anexo.Parcela p: anexo.getParcelas()){
				if (p.getTipomodificacion()!= null){
			
					if (p.getTipomodificacion().equals('A') || p.getTipomodificacion().equals('B')){
						revisarCoberturasAnx = true;
						break;
					}
				}
			}	
		}
		/**** ESC-14587 ** MODIF TAM (15.07.2021) ** Inicio ***/
		
		
		// DAA 05/07/2013 Si se ha modificado el cuadro de coberturas 
		logger.debug("guardarCoberturas con coberturasModificadas " + coberturasModificadas);
		if (("true").equals(coberturasModificadas) || (revisarCoberturasAnx && coberturas.size()>0)) { 
			/*PET.63485.FIII 15/01/2021 cambios para guardar el anexo cuando es modificado*/
			modSelec = StringUtils.isNullOrEmpty(comboModulo) ? poliza.getCodmodulo().split(",") : comboModulo.split(",");
 
			String seleccionados = ""; 
 
			comparativas = this.cuadroCoberturasManager.crearComparativas(poliza.getLinea().getLineaseguroid(), 
					poliza.getLinea().getCodlinea() + "", poliza.getClase(), poliza.getAsegurado().getNifcif(), 
					modSelec); 
 
			@SuppressWarnings("unchecked") 
			LinkedHashMap<String, Object> comparativasModulo = (LinkedHashMap<String, Object>) comparativas 
					.get("comparativa"); 
			hayComprativasElegibles = this.polizaManager.hayComparativasElegiblesModulo(comparativasModulo); 
			logger.debug("hayComprativasElegibles : " + hayComprativasElegibles);
			if (hayComprativasElegibles) { 
				logger.debug("coberturas.size() : " + coberturas.size());
				if (coberturas.size() > 0) { 
					// Obtenemos los cambios de los Riesgos Cubiertos Elegibles 
					HashMap<String, Boolean> riesgosElegidos = getRiesgosCubiertos(coberturas, listCoberturas, anexo); 
					
					logger.debug("riesgosElegidos : " + riesgosElegidos);
					logger.debug("coberturas : " + coberturas);
					
					// Obtenemos los cambios de los Conceptos Cubiertos Elegibles 
					getConceptosCubiertos(coberturas, listCoberturas, anexo, riesgosElegidos); 
				} 
			} else { // no hay comparativas elegibles 
				seleccionados = polizaManager.seleccionaComparativas(comparativasModulo); 
				List<Cobertura> lstCoberturas = new ArrayList<Cobertura>(); 
				lstCoberturas = getCoberturasAnexoActual(seleccionados, anexo); 
 
				Set<Cobertura> newCoberturas = new HashSet<Cobertura>(coberturas.size()); 
				for (Cobertura cob : lstCoberturas) { 
					newCoberturas.add(cob); 
					listCoberturas.add(cob); 
					logger.debug(CoberturasModificacionPolizaManager.COBERT + cob.getCodmodulo());
					logger.debug(CoberturasModificacionPolizaManager.COBERT + cob.getCodvalor());
					logger.debug(CoberturasModificacionPolizaManager.COBERT + cob.getCodconceptoppalmod());
					logger.debug(CoberturasModificacionPolizaManager.COBERT + cob.getCodconcepto());
				} 
				anexo.setCoberturas(newCoberturas); 
			} 
			// Si la poliza tiene tipo asegurado y el anexo no
			// hay que anhadirla manualmente
			Integer tipoAsegGanado = poliza.getModuloPolizas().iterator().next().getTipoAsegGanado();
			logger.debug("Tipo asegurado en la poliza: " + tipoAsegGanado);
			if (tipoAsegGanado != null) {
				logger.debug("Anhadimos la tipologia de asegurado al anexo");
				Cobertura cobTa = new Cobertura();
				cobTa.setAnexoModificacion(anexo);
				cobTa.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_TIPO_ASEG_GAN));
				cobTa.setCodvalor(tipoAsegGanado.toString());
				listCoberturas.add(cobTa);
			}
			// Si la poliza tiene caracteristica explotacion y el anexo no
			// hay que anhadirla manualmente
			ComparativaPoliza compCe = null;
			Set<ComparativaPoliza> compPoliza = poliza.getComparativaPolizas();
			logger.debug("Buscamos caracteristica explotacion en la poliza");
			for (ComparativaPoliza comp : compPoliza) {
				if (ConstantsConceptos.CODCPTO_CARACT_EXPLOTACION == comp.getId().getCodconcepto().intValue()) {
					compCe = comp;
					logger.debug("Encontrada caracteristica explotacion");
					break;
				}
			}
			if (compCe != null) {
				boolean hasCaracExplAnx = false;
				logger.debug("Buscamos caracteristica explotacion en el anexo");
				for (Cobertura cob : listCoberturas) {
					if (ConstantsConceptos.CODCPTO_CARACT_EXPLOTACION == cob.getCodconcepto().intValue()) {
						hasCaracExplAnx = true;
						logger.debug("Encontrada caracteristica explotacion");
						break;
					}
				}
				if (!hasCaracExplAnx) {
					logger.debug("Anhadimos la caracteristica explotacion al anexo");
					Cobertura cobCe = new Cobertura();
					cobCe.setAnexoModificacion(anexo);
					cobCe.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CARACT_EXPLOTACION));
					cobCe.setCodconceptoppalmod(compCe.getId().getCodconceptoppalmod());
					cobCe.setCodriesgocubierto(compCe.getId().getCodriesgocubierto());
					cobCe.setCodvalor(compCe.getId().getCodvalor().toString());
					cobCe.setCodmodulo(compCe.getId().getCodmodulo());
					cobCe.setFilamodulo(compCe.getId().getFilamodulo().intValue());
					cobCe.setFilacomparativa(compCe.getId().getFilacomparativa().intValue());
					cobCe.setIdComparativa(compCe.getId().getIdComparativa());
					listCoberturas.add(cobCe);
				}
			}
			try { 
				// Guardamos el modulo al anexo 
				anexo.setCodmodulo(StringUtils.isNullOrEmpty(comboModulo) ? poliza.getCodmodulo() : comboModulo); 
				declaracionModificacionPolizaDao.saveAnexoModificacion(anexo, usuario.getCodusuario(), null, false); 
				isGrabado = coberturasModificacionPolizaDao.saveCoberturasAnexo(anexo.getId(), listCoberturas); 
 
				// Caculo de CPM permitidos 
				logger.debug("Se cargan los CPM permitidos para la poliza y el anexo relacionado - idPoliza: " 
						+ poliza.getIdpoliza() + ", idAnexo: " + anexo.getId() + ", codModulo: " 
						+ poliza.getCodmodulo()); 
				List<BigDecimal> listaCPM = cpmTipoCapitalDao.getCPMDePolizaAnexoMod(anexo.getPoliza().getIdpoliza(), 
						anexo.getId(), comboModulo); 
 
				// Actualizamos el xml 
				boolean modAseg = false; 
				for (BigDecimal valor : listaCPM) {
					logger.debug("listaCPM: " + valor.longValue());
				}
				XmlTransformerUtil.updateXMLAnexoMod(xmlAnexoModDao, polizaCopyDao, anexo, modAseg, listaCPM, false); 
 
			} catch (ValidacionAnexoModificacionException e) { 
				logger.error("Error validando el xml de Anexos de Modificacion" + e.getMessage()); 
				throw new ValidacionAnexoModificacionException(e.getMessage()); 
			} catch (Exception ee) { 
				logger.error("Error generico al guardar coberturas" + ee.getMessage()); 
				throw new BusinessException("Error generico al guardar coberturas"); 
			} 
		}
		return isGrabado;
	} 
 
	private List<Cobertura> getCoberturasAnexoActual(String seleccionados, AnexoModificacion anexo) { 
		logger.debug("[INIT] getCoberturasAnexoActual"); 
		List<Cobertura> coberturas = new ArrayList<Cobertura>(); 
		String[] cobSeleccionadasTemp = seleccionados.split(";"); 
		for (int i = 0; i < cobSeleccionadasTemp.length; i++) { 
			String[] coberturaSeleccionadaTroceada = cobSeleccionadasTemp[i].split("\\|"); 
			Cobertura cobertura = new Cobertura();
			cobertura.setAnexoModificacion(anexo); 
			cobertura.setCodconceptoppalmod(new BigDecimal(coberturaSeleccionadaTroceada[2])); 
			cobertura.setCodriesgocubierto(new BigDecimal(coberturaSeleccionadaTroceada[3])); 
			cobertura.setCodconcepto(new BigDecimal(coberturaSeleccionadaTroceada[4])); 
			if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO).equals(cobertura.getCodconcepto())) {
				cobertura.setCodvalor(coberturaSeleccionadaTroceada[5] != null && "S".equals(coberturaSeleccionadaTroceada[5]) ? Constants.RIESGO_ELEGIDO_SI : Constants.RIESGO_ELEGIDO_NO); 
			} else {
				cobertura.setCodvalor(coberturaSeleccionadaTroceada[5] != null ? coberturaSeleccionadaTroceada[5] : ""); 
			}
			cobertura.setTipomodificacion(new Character('M')); 
			logger.debug(cobertura.getCodconcepto() + " - " + cobertura.getCodvalor()); 
			coberturas.add(cobertura); 
		} 
		logger.debug("[END] getCoberturasAnexoActual"); 
		return coberturas; 
	} 
 
	public void getConceptosCubiertos(HashMap<String, HashMap<String, Cobertura>> coberturas, 
			List<Cobertura> listCoberturas, AnexoModificacion anexo, HashMap<String, Boolean> riesgosElegidos) { 
		Cobertura coberturaAnexo = new Cobertura(); 
		Cobertura coberturaPoliza = new Cobertura(); 
		HashMap<String, Cobertura> conceptosAnexo = new HashMap<String, Cobertura>(); 
		HashMap<String, Cobertura> conceptosPoliza = new HashMap<String, Cobertura>(); 
 
		if (coberturas.get("conceptosAnexo") != null) 
			conceptosAnexo = coberturas.get("conceptosAnexo"); 
		if (coberturas.get(CoberturasModificacionPolizaManager.CONCEPTOS_POLIZA) != null) 
			conceptosPoliza = coberturas.get(CoberturasModificacionPolizaManager.CONCEPTOS_POLIZA); 
 
		if (conceptosAnexo != null) { 
			for (String key : conceptosAnexo.keySet()) { 
				coberturaAnexo = new Cobertura(); 
				coberturaPoliza = new Cobertura(); 
 
				coberturaAnexo = conceptosAnexo.get(key); 
				coberturaPoliza = conceptosPoliza.get(key); 
 
				// Si el concepto ha cambiado se guarda como modificacion 
				if (coberturaPoliza != null) { 
					if (coberturaPoliza.getCodvalor() != null) 
						coberturaAnexo.setTipomodificacion(new Character('M')); 
 
				} else { 
					// Se guarda concepto con tipo modificacion a null 
					coberturaAnexo.setTipomodificacion(new Character('M')); 
				} 
				coberturaAnexo.setAnexoModificacion(anexo); 
 
				// El concepto solo se incluira en las coberturas si el riesgo asociado 
				// esta elegido o no hay riesgo 
				Boolean elegido = riesgosElegidos 
						.get(coberturaAnexo.getCodconceptoppalmod() + "_" + coberturaAnexo.getCodriesgocubierto()); 
				if (elegido == null || elegido.equals(Boolean.TRUE)) { 
					listCoberturas.add(coberturaAnexo); 
				} 
 
			} 
		} 
	} 
 
	public HashMap<String, Boolean> getRiesgosCubiertos(HashMap<String, HashMap<String, Cobertura>> coberturas, 
			List<Cobertura> listCoberturas, AnexoModificacion anexo) { 
		Cobertura cobertura = new Cobertura(); 
		HashMap<String, Cobertura> riesgosAnexo = new HashMap<String, Cobertura>(); 
		HashMap<String, Cobertura> riesgosPoliza = new HashMap<String, Cobertura>(); 
		HashMap<String, Boolean> riesgosElegidos = new HashMap<String, Boolean>(); 
 
		if (coberturas.get("riesgosAnexo") != null) 
			riesgosAnexo = coberturas.get("riesgosAnexo"); 
		if (coberturas.get(CoberturasModificacionPolizaManager.RIESGOS_POLIZA) != null) 
			riesgosPoliza = coberturas.get(CoberturasModificacionPolizaManager.RIESGOS_POLIZA); 
 
		for (String key : riesgosAnexo.keySet()) { 
			cobertura = riesgosAnexo.get(key); 
 
			// Se guarda en un mapa si se ha elegido el riesgo para usarlo al recorrer los 
			// conceptos 
			riesgosElegidos.put(cobertura.getCodconceptoppalmod() + "_" + cobertura.getCodriesgocubierto(), 
					Constants.RIESGO_ELEGIDO_SI.equals(cobertura.getCodvalor())); 
 
			cobertura.setTipomodificacion(new Character('M')); 
			cobertura.setAnexoModificacion(anexo); 
			listCoberturas.add(cobertura); 
		} 
		for (String key : riesgosPoliza.keySet()) { 
			cobertura = riesgosPoliza.get(key); 
			cobertura.setTipomodificacion(new Character('M')); 
			// cobertura.setCodvalor("-2"); 
			cobertura.setAnexoModificacion(anexo); 
			listCoberturas.add(cobertura); 
		} 
 
		return riesgosElegidos; 
 
	} 
 
	/** 
	 * metodo que inserta las coberturas en una lista a partir de las coberturas de 
	 * la poliza 
	 *  
	 * @param coberturas 
	 * @param listCoberturas 
	 * @param anexo 
	 */ 
	public void getCoberturasNewModuloDePoliza(HashMap<String, HashMap<String, Cobertura>> coberturas, 
			List<Cobertura> listCoberturas, AnexoModificacion anexo) { 
		Cobertura cobertura = new Cobertura(); 
		HashMap<String, Cobertura> conceptosAnexo = new HashMap<String, Cobertura>(); 
		HashMap<String, Cobertura> riesgosAnexo = new HashMap<String, Cobertura>(); 
 
		if (coberturas.containsKey(CoberturasModificacionPolizaManager.CONCEPTOS_POLIZA)) { 
			conceptosAnexo = coberturas.get(CoberturasModificacionPolizaManager.CONCEPTOS_POLIZA); 
			// Conceptos Cubiertos 
			for (String key : conceptosAnexo.keySet()) { 
				cobertura = new Cobertura(); 
				cobertura = conceptosAnexo.get(key); 
				cobertura.setAnexoModificacion(anexo); 
				cobertura.setTipomodificacion(new Character('M')); 
 
				listCoberturas.add(cobertura); 
			} 
		} 
		if (coberturas.containsKey(CoberturasModificacionPolizaManager.RIESGOS_POLIZA)) { 
			riesgosAnexo = coberturas.get(CoberturasModificacionPolizaManager.RIESGOS_POLIZA); 
			// Riesgos Cubiertos 
			for (String key : riesgosAnexo.keySet()) { 
				cobertura = new Cobertura(); 
 
				cobertura = riesgosAnexo.get(key); 
				cobertura.setAnexoModificacion(anexo); 
				cobertura.setTipomodificacion(new Character('M')); 
 
				listCoberturas.add(cobertura); 
			} 
		} 
	} 
 
	/** 
	 * metodo con la llamada al metodo que genera la tabla de coberturas de la 
	 * poliza 
	 *  
	 * @param poliza 
	 * @return 
	 * @throws BusinessException 
	 */ 
	public Map<String, String> getCoberturasPoliza(com.rsi.agp.dao.tables.poliza.Poliza poliza, AnexoModificacion anexo, 
			List<CoberturaSeleccionada> lstCob, String realPath, Usuario usuario) throws BusinessException { 
 
		Map<String, String> tabla = new HashMap<String, String>(); 
		List<VinculacionValoresModulo> lstVincValMod = null; 
 
		/*** PET.63485.FIII DNF 04/02/2021 llamada al SW para recuperar las coberturas de la poliza*/ 
		//ModuloView modulo = cuadroCoberturasManager.getCoberturasModulo(poliza.getCodmodulo(), poliza, false);  
		 
		
		ModuloView modulo = null;  
		ModulosYCoberturas modYCob = null;  
				  
		XmlObject plzAct = solicitudModificacionManager.getPolizaActualizadaFromCupon(anexo.getCupon().getIdcupon());  
		AnexoModSWComparativas amc = this.seleccionComparativasAnexoSWManager.getAnexoModSWComparativas(usuario, anexo); 
		 
		try { 
			modYCob = this.seleccionComparativasAnexoSWManager.getMyCPolizaAgricola(plzAct, realPath, amc); 
		} catch (Exception e1) { 
			logger.error(e1);
		} 
		
		ModuloPoliza mp = null;
		if (null!=anexo.getPoliza()&& null!= anexo.getPoliza().getModuloPolizas()&& anexo.getPoliza().getModuloPolizas().size()>0){ 
			mp = anexo.getPoliza().getModuloPolizas().iterator().next(); 
		} 
		
		modulo = this.seleccionComparativasAnexoSWManager.getModuloViewFromModulosYCobertAgricolas ( 
				modYCob,  
				mp,0,poliza.getCodmodulo());
		
		/*** FIN PET.63485.FIII DNF 04/02/2021*/ 
		 
		// MPM - 21/08/12 
		logger.debug("getCoberturasPoliza - Se comprueba si la poliza con id=" + poliza.getIdpoliza() + " tiene copy"); 
 
		// Cargamos la lista de coberturas: 
		// si hay cupon => de la situacion actualizada 
		// si no => buscamos en la copy y si no, en la poliza 
		if (anexo.getCupon() != null && anexo.getCupon().getId() != null) { 
			try { 
				lstCob = getListaCoberturasPolizaActualizada(anexo.getCupon().getIdcupon(), 
						anexo.getPoliza().getLinea().getLineaseguroid()); 
			} catch (DAOException e) { 
				throw new BusinessException("Error al obtener las coberturas de la poliza actualizada.", e); 
			} 
		} else { 
			// Comprueba si la poliza tiene copy, ya que en ese caso se cargarán las 
			// coberturas elegidas en ella 
			lstCob = getListaCoberturasPoliza(poliza, anexo.getIdcopy()); 
		} 
		 
		/* Pet. 63485-Fase II ** MODIF TAM (15.09.2020) ** Inicio */  
		boolean esGanado = anexo.getPoliza().getLinea().isLineaGanado(); 
		Long lineaseguroid = anexo.getPoliza().getLinea().getLineaseguroid(); 
 
		try { 
			tabla.put("tabla", moduloManager.crearTablaModuloAnexo(modulo, lstCob, lstVincValMod, false, esGanado, lineaseguroid, anexo)); 
			/* Pet. 63485-Fase II ** MODIF TAM (15.09.2020) ** Fin */ 
		} catch (Exception ex) { 
			logger.error("Se ha producido un error al recuperar las coberturas de la poliza", ex); 
		} 
		/*PET.63485 11/02/2021 dnf error incidencia 3 cabecera del modulo de la poliza*/
		List<Modulo> modulosPolizaDisponibles = getModulosDisponibles(poliza.getLinea().getLineaseguroid());
		String descripcionModuloActual = "";
		for(Modulo modulPoli : modulosPolizaDisponibles) {
			if(modYCob.getModuloArray(0).getModulo().equals(modulPoli.getId().getCodmodulo()))
				descripcionModuloActual = modulPoli.getDesmodulo();
		}
		tabla.put("cabecera", modYCob.getModuloArray(0).getModulo() +" - "+ descripcionModuloActual); 
		/*fin PET.63485 11/02/2021 dnf error incidencia 3 cabecera del modulo de la poliza*/
		return tabla; 
	} 
 
	/** 
	 * Obtiene la lista de coberturas elegidas en la copy o en la poliza 
	 *  
	 * @param poliza 
	 * @param idCopy 
	 * @return 
	 */ 
	private List<CoberturaSeleccionada> getListaCoberturasPoliza(com.rsi.agp.dao.tables.poliza.Poliza poliza, 
			Long idCopy) { 
		List<CoberturaSeleccionada> lstCob = new ArrayList<CoberturaSeleccionada>(); 
		if (idCopy != null && idCopy > 0) { 
			logger.debug("getCoberturasPoliza - La poliza tiene copy, se cargan las coberturas de la copy con id =" 
					+ idCopy); 
 
			// Obtenemos las coberturas de la Copy 
			try { 
				List<CoberturaPoliza> coberturasCopy = coberturasModificacionPolizaDao.getCoberturasCopy(idCopy); 
 
				// Recorre las coberturas y las transforma a objetos CoberturaSeleccionada 
				for (CoberturaPoliza coberturaPoliza : coberturasCopy) { 
 
					CoberturaSeleccionada cs = new CoberturaSeleccionada(); 
					cs.setCodconceptoppalmod(coberturaPoliza.getCodconceptoppalmod()); 
					cs.setCodriesgocubierto(coberturaPoliza.getCodriesgocubierto()); 
					cs.setCodconcepto(coberturaPoliza.getCodconcepto()); 
					cs.setCodvalor(coberturaPoliza.getCodvalor()); 
					lstCob.add(cs); 
				} 
 
			} catch (DAOException e) { 
				logger.debug("getCoberturasPoliza - Ocurrio un error al obtener la copy de la poliza"); 
				logger.error("getCoberturasPoliza - Ocurrio un error al obtener la copy de la poliza", e); 
			} 
		} 
		// Si no tiene, se cargan las coberturas de la poliza 
		else { 
			logger.debug("getCoberturasPoliza - La poliza no tiene copy, se cargan las coberturas de la poliza."); 
			// Transformamos comparativasElegidas a objeto tipo coberturaSeleccionada 
			lstCob = transformarCompACobSeleccionada(poliza.getComparativaPolizas()); 
		} 
		return lstCob; 
	} 
 
	/** 
	 * Obtiene la lista de coberturas de la situacion actualizada de la poliza 
	 *  
	 * @param cupon 
	 *            Cupon asociado a la peticion del anexo de modificacion 
	 * @return Lista de coberturas a marcar en el cuadro de poliza 
	 * @throws DAOException 
	 */ 
	private List<CoberturaSeleccionada> getListaCoberturasPolizaActualizada(final String cupon, 
			final Long lineaseguroid) throws DAOException { 
		List<CoberturaSeleccionada> lstCob = new ArrayList<CoberturaSeleccionada>(); 
		// es.agroseguro.seguroAgrario.contratacion.Poliza 
		// Obtengo la poliza actualizada 
		XmlObject poliza = null; 
		boolean esLineaGanado = lineaDao.esLineaGanado(lineaseguroid); 
 
		/* Pet. 57626 ** MODIF TAM (11.06.2020) ** Inicio */ 
		/* 
		 * Por los desarrollos de esta peticion tanto las polizas agricolas como las de 
		 * ganado iran por el mismo end-point y con formato Unificado 
		 */ 
		XmlObject polizaDoc = this.solicitudModificacionManager.getPolizaActualizadaFromCupon(cupon); 
		if (polizaDoc instanceof es.agroseguro.contratacion.PolizaDocument) { 
			poliza = ((es.agroseguro.contratacion.PolizaDocument) polizaDoc).getPoliza(); 
		} else { 
			poliza = ((es.agroseguro.seguroAgrario.contratacion.PolizaDocument) polizaDoc).getPoliza(); 
		} 
 
		if (poliza != null) { 
			if (esLineaGanado) { 
				if (((es.agroseguro.contratacion.Poliza) poliza).getCobertura() != null && !StringUtils 
						.nullToString(((es.agroseguro.contratacion.Poliza) poliza).getCobertura().getModulo()) 
						.equals("")) { 
					// SE BUSCAN AQUELLOS CONCEPTOS QUE APLIQUEN AL USO POLIZA 
					// (31) Y A 
					// LA UBICACION DE COBERTURAS (18) 
					Filter oiFilter = new Filter() { 
						@Override 
						public Criteria getCriteria(final Session sesion) { 
							Criteria criteria = sesion.createCriteria(OrganizadorInformacion.class); 
							criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid)); 
							criteria.add(Restrictions.eq("id.coduso", OrganizadorInfoConstants.USO_POLIZA)); 
							criteria.add(Restrictions.in("id.codubicacion", 
									new Object[] { OrganizadorInfoConstants.UBICACION_COBERTURA_DV })); 
							return criteria; 
						} 
					}; 
					@SuppressWarnings("unchecked") 
					List<OrganizadorInformacion> oiList = (List<OrganizadorInformacion>) lineaDao.getObjects(oiFilter); 
					es.agroseguro.contratacion.datosVariables.DatosVariables dvs = ((es.agroseguro.contratacion.Poliza) poliza) 
							.getCobertura().getDatosVariables(); 
 
					if (dvs != null) { 
						try { 
							for (OrganizadorInformacion oi : oiList) { 
								Method method = dvs.getClass() 
										.getMethod("get" + oi.getDiccionarioDatos().getEtiquetaxml() + "Array"); 
								Class<?> dvClass = dvs.getClass() 
										.getMethod("addNew" + oi.getDiccionarioDatos().getEtiquetaxml()) 
										.getReturnType(); 
								Object[] result = (Object[]) method.invoke(dvs); 
								for (Object obj : result) { 
									BigDecimal valor = new BigDecimal("" + dvClass.getMethod("getValor").invoke(obj)); 
									BigDecimal cpMod = new BigDecimal("" + dvClass.getMethod("getCPMod").invoke(obj)); 
									BigDecimal rCub = new BigDecimal("" + dvClass.getMethod("getCodRCub").invoke(obj)); 
									lstCob.add(generarCoberturaSeleccionada(cpMod, rCub, oi.getId().getCodconcepto(), 
											valor)); 
								} 
							} 
						} catch (Exception e) { 
							logger.error("Error al obtener los datos variables de la cobertura.", e); 
						} 
					} 
				} 
			} else { 
				if (poliza instanceof es.agroseguro.contratacion.Poliza) { 
					if (((es.agroseguro.contratacion.Poliza) poliza).getCobertura() != null && !StringUtils 
							.nullToString(((es.agroseguro.contratacion.Poliza) poliza).getCobertura().getModulo()) 
							.equals("")) { 
 
						// Con los datos variables generamos los registros de coberturas 
						es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables = ((es.agroseguro.contratacion.Poliza) poliza) 
								.getCobertura().getDatosVariables(); 
 
						if (datosVariables != null) 
							lstCob = AnexoModificacionUtils.getCoberturasFromPolizaActualizada(datosVariables); 
					} 
				} else { 
					if (((es.agroseguro.seguroAgrario.contratacion.Poliza) poliza).getCobertura() != null 
							&& !StringUtils.nullToString(((es.agroseguro.seguroAgrario.contratacion.Poliza) poliza) 
									.getCobertura().getModulo()).equals("")) { 
 
						// Con los datos variables generamos los registros de coberturas 
						es.agroseguro.seguroAgrario.contratacion.datosVariables.DatosVariables datosVariables = ((es.agroseguro.seguroAgrario.contratacion.Poliza) poliza) 
								.getCobertura().getDatosVariables(); 
 
						if (datosVariables != null) 
							lstCob = AnexoModificacionUtils.getCoberturasFromPolizaActualizadaAnt(datosVariables); 
					} 
				} 
			} 
		} 
		return lstCob; 
	} 
 
	/** 
	 * Crear un objeto CoberturaSeleccionada con los datos indicados por parametro 
	 *  
	 * @param cpm 
	 * @param rCub 
	 * @param codConcepto 
	 * @param valor 
	 * @return 
	 */ 
	private CoberturaSeleccionada generarCoberturaSeleccionada(final BigDecimal cpm, final BigDecimal rCub, 
			final BigDecimal codConcepto, final BigDecimal valor) { 
		CoberturaSeleccionada cobertura = new CoberturaSeleccionada(); 
		cobertura.setCodconceptoppalmod(cpm); 
		cobertura.setCodriesgocubierto(rCub); 
		cobertura.setCodconcepto(codConcepto); 
		cobertura.setCodvalor(valor.toString()); 
		return cobertura; 
	} 
 
	/** 
	 * metodo que manda la tabla de coberturas para el anexo por ajax 
	 *  
	 * @param poliza 
	 * @param codmodulo 
	 * @return 
	 * @throws BusinessException 
	 */ 
	public JSONObject getCoberturasAnexo(com.rsi.agp.dao.tables.poliza.Poliza poliza, String codmodulo, 
			AnexoModificacion anexoModificacion) throws BusinessException { 
		JSONObject datos = new JSONObject(); 
		JSONObject objeto = new JSONObject(); 
 
		try { 
			// recoger vincvalmod dado el modulo y la lineaseguroid 
			List<VinculacionValoresModulo> lstVincValMod = coberturasModificacionPolizaDao 
					.getLstVincValMod(poliza.getLinea().getLineaseguroid(), codmodulo); 
			// montar funcion de vinculados 
 
			ModuloView modulo = cuadroCoberturasManager.getCoberturasModulo(codmodulo, poliza, false); 
			List<CoberturaSeleccionada> lstCob = new ArrayList<CoberturaSeleccionada>(); 
			// Cargamos la lista de coberturas: 
			// si hay cupon => de la situacion actualizada 
			// si no => buscamos en la copy y si no, en la poliza 
			if (anexoModificacion.getCupon() != null && anexoModificacion.getCupon().getId() != null) { 
				lstCob = getListaCoberturasPolizaActualizada(anexoModificacion.getCupon().getIdcupon(), 
						anexoModificacion.getPoliza().getLinea().getLineaseguroid()); 
			} else { 
				// DAA 05/07/13 si no tengo codmodulo la lista de coberturas debe ser la de la 
				// poliza 
				// siempre y cuando el codmodulo elegido sea igual al de la poliza 
				if (anexoModificacion.getCodmodulo() != null && !anexoModificacion.getCoberturas().isEmpty()) { 
					if (anexoModificacion.getCodmodulo().equals(codmodulo)) 
						// transformamos las coberturas del anexo a objetos tipo coberturaSeleccionada 
						lstCob = transformarCobACoberturaSeleccionada(anexoModificacion.getCoberturas()); 
				} else { 
					if (poliza.getCodmodulo().equals(codmodulo)) { 
						// lstCob = transformarCompACobSeleccionada(poliza.getComparativaPolizas()); 
						lstCob = this.getListaCoberturasPoliza(poliza, anexoModificacion.getIdcopy()); 
					} 
				} 
			} 
			 
			/* Pet. 63485-Fase II ** MODIF TAM (15.09.2020) ** Inicio */ 
			boolean esGanado = anexoModificacion.getPoliza().getLinea().isLineaGanado(); 
			Long lineaseguroid = anexoModificacion.getPoliza().getLinea().getLineaseguroid(); 
 
			objeto.put("tabla", moduloManager.crearTablaModuloAnexo(modulo, lstCob, lstVincValMod, true, esGanado, lineaseguroid, anexoModificacion)); 
			/* Pet. 63485-Fase II ** MODIF TAM (15.09.2020) ** Fin */ 
			objeto.put("cabecera", modulo.getDescripcionModulo()); 
 
			datos.put("tablaDatos", objeto); 
 
		} catch (Exception ex) { 
			logger.error("Se ha producido un error al recuperar las coberturas del anexo ", ex); 
			throw new BusinessException("Se ha producido un error al recuperar las coberturas del  anexo", ex); 
		} 
		return datos; 
	} 
	
	
	/** 
	 * metodo que manda la tabla de coberturas para el anexo por ajax 
	 *  
	 * @param poliza 
	 * @param codmodulo 
	 * @return 
	 * @throws BusinessException 
	 */ 
	public JSONObject getCoberturasAnexoAgri(com.rsi.agp.dao.tables.poliza.Poliza poliza, String codmodulo, 
			AnexoModificacion anexoModificacion, String realPath, Usuario usuario) throws BusinessException { 
		JSONObject datos = new JSONObject(); 
		JSONObject objeto = new JSONObject(); 
		 
		/**** Taty *****/ 
		try { 
			 
			ModuloView modulo = null; 
				 
			List<ModuloPoliza> mFinal = new ArrayList<ModuloPoliza>(); 
				 
			mFinal.addAll(poliza.getModuloPolizas()); 
					 
			for (ModuloPoliza mp : mFinal) { 
				/***DNF PET.63485.FIII 19/01/2021 le paso el codmodulo solicitado en el combo*/
				setModulosYCoberturas(this.seleccionComparativasAnexoSWManager.getModulosYCoberturasAgriSW(mp, poliza, realPath, usuario, codmodulo, anexoModificacion));				
				/*** fin DNF PET.63485.FIII 19/01/2021 */ 
				
				modulo = this.seleccionComparativasAnexoSWManager.getModuloViewFromModulosYCobertAgricolas(getModulosYCoberturas(), mp, mp.getId() 
					.getNumComparativa().intValue(), codmodulo); 
					 
			}
			/*** DNF 22/01/2021 pet.63485.FIII*/
			List<String> listaIdCeldaConVin = getListaCeldasConVinculacion(modulo);
			
			objeto.put("listaIdCeldaConVin", listaIdCeldaConVin);
			/*** fin DNF 22/01/2021 pet.63485.FIII*/
			 
			/* Pet. 63485-Fase II ** MODIF TAM (10.09.2020) ** Fin */ 
			 
			// recoger vincvalmod dado el modulo y la lineaseguroid 
			List<VinculacionValoresModulo> lstVincValMod = coberturasModificacionPolizaDao 
					.getLstVincValMod(poliza.getLinea().getLineaseguroid(), codmodulo); 
			// montar funcion de vinculados 
			 
			 
			List<CoberturaSeleccionada> lstCob = new ArrayList<CoberturaSeleccionada>(); 
			// Cargamos la lista de coberturas: 
			// si hay cupon => de la situacion actualizada 
			// si no => buscamos en la copy y si no, en la poliza 
			if (anexoModificacion.getCupon() != null && anexoModificacion.getCupon().getId() != null) { 
				lstCob = getListaCoberturasPolizaActualizada(anexoModificacion.getCupon().getIdcupon(), 
						anexoModificacion.getPoliza().getLinea().getLineaseguroid()); 
			} else { 
				// DAA 05/07/13 si no tengo codmodulo la lista de coberturas debe ser la de la 
				// poliza 
				// siempre y cuando el codmodulo elegido sea igual al de la poliza 
				if (anexoModificacion.getCodmodulo() != null && !anexoModificacion.getCoberturas().isEmpty()) { 
					if (anexoModificacion.getCodmodulo().equals(codmodulo)) 
						// transformamos las coberturas del anexo a objetos tipo coberturaSeleccionada 
						lstCob = transformarCobACoberturaSeleccionada(anexoModificacion.getCoberturas()); 
				} else { 
					if (poliza.getCodmodulo().equals(codmodulo)) { 
						// lstCob = transformarCompACobSeleccionada(poliza.getComparativaPolizas()); 
						lstCob = this.getListaCoberturasPoliza(poliza, anexoModificacion.getIdcopy()); 
					} 
				} 
			} 
			 
			/* Pet. 63485-Fase II ** MODIF TAM (15.09.2020) ** Inicio */ 
			boolean esGanado = anexoModificacion.getPoliza().getLinea().isLineaGanado(); 
			Long lineaseguroid = anexoModificacion.getPoliza().getLinea().getLineaseguroid(); 
 
			objeto.put("tabla", moduloManager.crearTablaModuloAnexo(modulo, lstCob, lstVincValMod, true, esGanado,lineaseguroid, anexoModificacion)); 
			/* Pet. 63485-Fase II ** MODIF TAM (15.09.2020) ** Fin */ 
			 
			objeto.put("cabecera", modulo.getDescripcionModulo()); 
			/*** DNF 22/01/2021 pet.63485.FIII*/
			//objeto.put("lstIdCob", getListaIdCobDisp(lstCob));
			objeto.put("lstIdCeldasEleg", getlstIdCeldasEleg(modulo));
			/*** fin DNF 22/01/2021 pet.63485.FIII*/
			/*** DNF 01/02/2021 pet.63485.FIII*/
			objeto.put("lstIdChecksEleg", getlstIdChecksEleg(modulo));
			/*** fin DNF 01/02/2021 pet.63485.FIII*/
			
			datos.put("tablaDatos", objeto); 
 
		} catch (Exception ex) { 
			logger.error(" Se ha producido un error al recuperar las coberturas del anexo", ex); 
			throw new BusinessException("Se  ha producido un error al recuperar las coberturas del anexo", ex); 
		} 
		return datos; 
	} 
 
 
	private List<CoberturaSeleccionada> transformarCompACobSeleccionada(Set<ComparativaPoliza> comparativasElegidas) { 
		List<CoberturaSeleccionada> lstCob = new ArrayList<CoberturaSeleccionada>(); 
		for (ComparativaPoliza comp : comparativasElegidas) { 
			CoberturaSeleccionada cob = new CoberturaSeleccionada(); 
			cob.setCodconceptoppalmod(comp.getId().getCodconceptoppalmod()); 
			cob.setCodriesgocubierto(comp.getId().getCodriesgocubierto()); 
			cob.setCodconcepto(comp.getId().getCodconcepto()); 
			cob.setCodvalor(comp.getId().getCodvalor() != null ? comp.getId().getCodvalor().toString() : ""); 
 
			lstCob.add(cob); 
		} 
 
		return lstCob; 
	} 
 
	private List<CoberturaSeleccionada> transformarCobACoberturaSeleccionada(Set<Cobertura> coberturas) { 
		List<CoberturaSeleccionada> lstCob = new ArrayList<CoberturaSeleccionada>(); 
		for (Cobertura cober : coberturas) { 
			CoberturaSeleccionada cob = new CoberturaSeleccionada(); 
			cob.setCodconceptoppalmod(cober.getCodconceptoppalmod()); 
			cob.setCodriesgocubierto(cober.getCodriesgocubierto()); 
			cob.setCodconcepto(cober.getCodconcepto()); 
			cob.setCodvalor(cober.getCodvalor() != null ? cober.getCodvalor() : ""); 
 
			lstCob.add(cob); 
		} 
		return lstCob; 
	} 
 
	private String calculaIdComboVinculado(BigDecimal fila, BigDecimal columna, ModuloView moduloView) { 
		StringBuilder idComboVinc = new StringBuilder(); 
		int moduloViewListSize = moduloView.getListaFilas().size(); 
		int lineTryingToAccess = fila.intValue() - 1; 
 
		if (moduloViewListSize > lineTryingToAccess) { 
			ModuloFilaView filaModulo = moduloView.getListaFilas().get(fila.intValue() - 1); 
			String codConcepto = ""; 
 
			boolean encontrado = false; 
			for (ModuloCeldaView mcv : filaModulo.getCeldas()) { 
				for (ModuloValorCeldaView mvcv : mcv.getValores()) { 
					if (mvcv.getColumna().equals(columna)) { 
						// Si es esta columna paramos el bucle 
						encontrado = true; 
						break; 
					} 
				} 
				if (encontrado) { 
					codConcepto = mcv.getCodconcepto() + ""; 
					break; 
				} 
			} 
			idComboVinc.append("selectAnexo_"); 
			idComboVinc.append(filaModulo.getCodConceptoPrincipalModulo().toString()); 
			idComboVinc.append("_"); 
			idComboVinc.append(filaModulo.getCodRiesgoCubierto()); 
			idComboVinc.append("_"); 
			idComboVinc.append(codConcepto); 
		} 
		return idComboVinc.toString(); 
	} 
 
	/** 
	 * MEJORA 5 ANGEL 26/01/2012 Metodo que genera una funcion javascript que se 
	 * ejecutara como flujo de cambio de los valores de los combos 
	 *  
	 * @param datosvinculados 
	 * @return 
	 * @throws JSONException 
	 */ 
	public String creaFuncionesJSCombos(List<VinculacionValoresModulo> lstVincValMod, ModuloView moduloview) 
			throws JSONException { 
		StringBuilder funcion = new StringBuilder(); 
		// String funcion = ""; 
		String idComboT = ""; 
		String idComboVincT = ""; 
		BigDecimal auxB = new BigDecimal(0); 
		funcion.append("function cambiarCombo(combo){"); 
		if (moduloview.getCodModulo().equals("P") && !CollectionsAndMapsUtil.isEmpty(lstVincValMod) 
				&& lstVincValMod.get(0).getLinea().getCodlinea().equals(new BigDecimal(312))) { 
			funcion.append("if (combo == 'selectAnexo_1_2_362' && $('#selectAnexo_1_2_362').val() == 2){") 
					.append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_174_VAL_1).append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_121_VAL_3) 
					.append("$('#selectAnexo_1_2_170').val(2);").append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_120_VAL_2) 
					.append("else if (combo == 'selectAnexo_1_2_362' && $('#selectAnexo_1_2_362').val() == 1){") 
					.append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_174_VAL_2).append("$('#selectAnexo_1_2_121').val(5);") 
					.append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_170_VAL_1).append("$('#selectAnexo_1_2_120').val(6);}") 
					.append("else if (combo == 'selectAnexo_1_2_174' && $('#selectAnexo_1_2_174').val() == 1){") 
					.append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_362_VAL_2).append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_121_VAL_3) 
					.append("$('#selectAnexo_1_2_170').val(2);").append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_120_VAL_2) 
					.append("else if (combo == 'selectAnexo_1_2_174' && $('#selectAnexo_1_2_174').val() == 2){") 
					.append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_362_VAL_1).append("$('#selectAnexo_1_2_121').val(5);") 
					.append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_170_VAL_1).append("$('#selectAnexo_1_2_120').val(6);}") 
					.append("else if (combo == 'selectAnexo_1_2_121' && $('#selectAnexo_1_2_121').val() == 2){") 
					.append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_362_VAL_2).append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_174_VAL_1) 
					.append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_170_VAL_1).append("$('#selectAnexo_1_2_120').val(3);}") 
					.append("else if (combo == 'selectAnexo_1_2_121' && $('#selectAnexo_1_2_121').val() == 3){") 
					.append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_362_VAL_2).append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_174_VAL_1) 
					.append("$('#selectAnexo_1_2_170').val(2);").append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_120_VAL_2) 
					.append("else if (combo == 'selectAnexo_1_2_121' && $('#selectAnexo_1_2_121').val() == 4){") 
					.append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_362_VAL_1).append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_174_VAL_2) 
					.append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_170_VAL_1).append("$('#selectAnexo_1_2_120').val(5);}") 
					.append("else if (combo == 'selectAnexo_1_2_121' && $('#selectAnexo_1_2_121').val() == 5){") 
					.append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_362_VAL_1).append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_174_VAL_2) 
					.append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_170_VAL_1).append("$('#selectAnexo_1_2_120').val(6);}") 
					.append("else if (combo == 'selectAnexo_1_2_120' && $('#selectAnexo_1_2_120').val() == 2){") 
					.append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_362_VAL_2).append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_174_VAL_1) 
					.append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_121_VAL_3).append("$('#selectAnexo_1_2_170').val(2);}") 
					.append("else if (combo == 'selectAnexo_1_2_120' && $('#selectAnexo_1_2_120').val() == 3){") 
					.append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_362_VAL_2).append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_174_VAL_1) 
					.append("$('#selectAnexo_1_2_121').val(2);").append("$('#selectAnexo_1_2_170').val(1);}") 
					.append("else if (combo == 'selectAnexo_1_2_120' && $('#selectAnexo_1_2_120').val() == 5){") 
					.append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_362_VAL_1).append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_174_VAL_2) 
					.append("$('#selectAnexo_1_2_121').val(4);").append("$('#selectAnexo_1_2_170').val(1);}") 
					.append("else if (combo == 'selectAnexo_1_2_120' && $('#selectAnexo_1_2_120').val() == 6){") 
					.append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_362_VAL_1).append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_174_VAL_2) 
					.append("$('#selectAnexo_1_2_121').val(5);").append("$('#selectAnexo_1_2_170').val(1);}") 
					.append("else if (combo == 'selectAnexo_1_2_170' && $('#selectAnexo_1_2_170').val() == 2){") 
					.append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_362_VAL_2).append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_174_VAL_1) 
					.append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_121_VAL_3).append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_120_VAL_2) 
					.append("else if (combo == 'selectAnexo_1_2_170' && $('#selectAnexo_1_2_170').val() == 1){") 
					.append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_362_VAL_2).append(CoberturasModificacionPolizaManager.SELECT_ANEXO_1_2_174_VAL_1) 
					.append("$('#selectAnexo_1_2_121').val(2);").append("$('#selectAnexo_1_2_120').val(3);}"); 
		} else { 
			List<String> lstCombos = new ArrayList<String>(); 
 
			if (lstVincValMod != null) { 
 
				for (VinculacionValoresModulo vincValMod : lstVincValMod) { 
					CaracteristicaModuloId idMod1 = vincValMod.getCaracteristicaModuloByFkVincValModCaracMod1().getId(); 
					CaracteristicaModuloId idMod2 = vincValMod.getCaracteristicaModuloByFkVincValModCaracMod2().getId(); 
					idComboT = calculaIdComboVinculado(idMod1.getFilamodulo(), idMod1.getColumnamodulo(), moduloview); 
					idComboVincT = calculaIdComboVinculado(idMod2.getFilamodulo(), idMod2.getColumnamodulo(), 
							moduloview); 
					if (!lstCombos.contains(idComboT + "_c")) { 
						lstCombos.add(idComboT + "_c"); 
					} 
					if (!lstCombos.contains(idComboVincT + "_c")) { 
						lstCombos.add(idComboVincT + "_c"); 
					} 
					for (String combo : lstCombos) { 
						funcion.append("var ").append(combo).append(" = 'false';"); 
					} 
				} 
				for (VinculacionValoresModulo vincValMod : lstVincValMod) { 
					// asginamos a cada select una funcion que debe ejecutar, para 
					// todos aquellos combos con combos vinculados 
					CaracteristicaModuloId idMod1 = vincValMod.getCaracteristicaModuloByFkVincValModCaracMod1().getId(); 
					CaracteristicaModuloId idMod2 = vincValMod.getCaracteristicaModuloByFkVincValModCaracMod2().getId(); 
					idComboT = calculaIdComboVinculado(idMod1.getFilamodulo(), idMod1.getColumnamodulo(), moduloview); 
					idComboVincT = calculaIdComboVinculado(idMod2.getFilamodulo(), idMod2.getColumnamodulo(), 
							moduloview); 
					funcion.append("if (combo == '").append(idComboT).append("'){"); 
					funcion.append(idComboT).append("_c = 'true';"); 
					funcion.append("var valor = $('#").append(idComboT).append("').val();"); 
					boolean cerrarLlave = true; 
					if (vincValMod.getCapitalAseguradoElegibleByPctcapitalasegeleg() != null) { 
						funcion.append(CoberturasModificacionPolizaManager.IF_VALOR).append( 
								vincValMod.getCapitalAseguradoElegibleByPctcapitalasegeleg().getPctcapitalaseg()) 
								.append("){"); 
					} else if (vincValMod.getCalculoIndemnizacionByCalcindemneleg() != null) { 
						funcion.append(CoberturasModificacionPolizaManager.IF_VALOR) 
								.append(vincValMod.getCalculoIndemnizacionByCalcindemneleg().getCodcalculo()) 
								.append("){"); 
					} else if (vincValMod.getMinimoIndemnizableElegibleByPctminindemneleg() != null) { 
						funcion.append(CoberturasModificacionPolizaManager.IF_VALOR) 
								.append(vincValMod.getMinimoIndemnizableElegibleByPctminindemneleg().getPctminindem()) 
								.append("){"); 
					} else if (vincValMod.getTipoFranquiciaByTipofranquiciaeleg() != null) { 
						funcion.append(CoberturasModificacionPolizaManager.IF_VALOR) 
								.append(vincValMod.getTipoFranquiciaByTipofranquiciaeleg().getCodtipofranquicia()) 
								.append("){"); 
					} else if (vincValMod.getPctFranquiciaElegibleByCodpctfranquiciaeleg() != null) { 
						funcion.append(CoberturasModificacionPolizaManager.IF_VALOR).append( 
								vincValMod.getPctFranquiciaElegibleByCodpctfranquiciaeleg().getCodpctfranquiciaeleg()) 
								.append("){"); 
					} else if (vincValMod.getGarantizadoByGarantizadoeleg() != null) { 
						funcion.append(CoberturasModificacionPolizaManager.IF_VALOR) 
								.append(vincValMod.getGarantizadoByGarantizadoeleg().getCodgarantizado()).append("){"); 
					} else { 
						cerrarLlave = false; 
					} 
					// vinculados 
					if (vincValMod.getCapitalAseguradoElegibleByPctcapitalasegvinc() != null) { 
						auxB = vincValMod.getCapitalAseguradoElegibleByPctcapitalasegvinc().getPctcapitalaseg(); 
					} else if (vincValMod.getCalculoIndemnizacionByCalcindemnvinc() != null) { 
						auxB = vincValMod.getCalculoIndemnizacionByCalcindemnvinc().getCodcalculo(); 
					} else if (vincValMod.getMinimoIndemnizableElegibleByPctminindemnvinc() != null) { 
						auxB = vincValMod.getMinimoIndemnizableElegibleByPctminindemnvinc().getPctminindem(); 
					} else if (vincValMod.getTipoFranquiciaByTipofranquiciavinc() != null) { 
						auxB = new BigDecimal( 
								vincValMod.getTipoFranquiciaByTipofranquiciavinc().getCodtipofranquicia().toString()); 
					} else if (vincValMod.getPctFranquiciaElegibleByPctfranquiciavinc() != null) { 
						auxB = vincValMod.getPctFranquiciaElegibleByPctfranquiciavinc().getCodpctfranquiciaeleg(); 
					} else if (vincValMod.getGarantizadoByGarantizadovinc() != null) { 
						auxB = vincValMod.getGarantizadoByGarantizadovinc().getCodgarantizado(); 
					} 
					// asignamos dicho codigo buscado al combovinculado 
					funcion.append("if (").append(idComboVincT).append("_c == 'false'){"); 
					funcion.append("$('#").append(idComboVincT).append("').val(").append(auxB).append(");") 
							.append(idComboVincT).append("_c = 'true';}}"); 
 
					if (cerrarLlave) { 
						funcion.append("}"); 
					} 
				} 
				// realizamos el mismo proceso pero a la inversa 
				for (VinculacionValoresModulo vvm : lstVincValMod) { 
					boolean cerrarLlave = true; 
					// asginamos a cada select una funcion que debe ejecutar, para 
					// todos aquellos combos con combos vinculados 
					CaracteristicaModuloId idMod1 = vvm.getCaracteristicaModuloByFkVincValModCaracMod1().getId(); 
					CaracteristicaModuloId idMod2 = vvm.getCaracteristicaModuloByFkVincValModCaracMod2().getId(); 
					idComboT = calculaIdComboVinculado(idMod1.getFilamodulo(), idMod1.getColumnamodulo(), moduloview); 
					idComboVincT = calculaIdComboVinculado(idMod2.getFilamodulo(), idMod2.getColumnamodulo(), 
							moduloview); 
					// funcion += "if (combo == '" + idComboVincT + "'){"; 
 
					funcion.append("var valor = $('#").append(idComboVincT).append("').val();"); 
					if (vvm.getCapitalAseguradoElegibleByPctcapitalasegvinc() != null) { 
						funcion.append(CoberturasModificacionPolizaManager.IF_VALOR) 
								.append(vvm.getCapitalAseguradoElegibleByPctcapitalasegvinc().getPctcapitalaseg()) 
								.append("){"); 
					} else if (vvm.getCalculoIndemnizacionByCalcindemnvinc() != null) { 
						funcion.append(CoberturasModificacionPolizaManager.IF_VALOR) 
								.append(vvm.getCalculoIndemnizacionByCalcindemnvinc().getCodcalculo()).append("){"); 
					} else if (vvm.getMinimoIndemnizableElegibleByPctminindemnvinc() != null) { 
						funcion.append(CoberturasModificacionPolizaManager.IF_VALOR) 
								.append(vvm.getMinimoIndemnizableElegibleByPctminindemnvinc().getPctminindem()) 
								.append("){"); 
					} else if (vvm.getTipoFranquiciaByTipofranquiciavinc() != null) { 
						funcion.append(CoberturasModificacionPolizaManager.IF_VALOR) 
								.append(vvm.getTipoFranquiciaByTipofranquiciavinc().getCodtipofranquicia()) 
								.append("){"); 
					} else if (vvm.getPctFranquiciaElegibleByPctfranquiciavinc() != null) { 
						funcion.append(CoberturasModificacionPolizaManager.IF_VALOR) 
								.append(vvm.getPctFranquiciaElegibleByPctfranquiciavinc().getCodpctfranquiciaeleg()) 
								.append("){"); 
					} else if (vvm.getGarantizadoByGarantizadovinc() != null) { 
						funcion.append(CoberturasModificacionPolizaManager.IF_VALOR).append(vvm.getGarantizadoByGarantizadovinc().getCodgarantizado()) 
								.append("){"); 
					} else { 
						cerrarLlave = false; 
					} 
 
					// vinculados 
					if (vvm.getCapitalAseguradoElegibleByPctcapitalasegeleg() != null) { 
						auxB = vvm.getCapitalAseguradoElegibleByPctcapitalasegeleg().getPctcapitalaseg(); 
					} else if (vvm.getCalculoIndemnizacionByCalcindemneleg() != null) { 
						auxB = vvm.getCalculoIndemnizacionByCalcindemneleg().getCodcalculo(); 
					} else if (vvm.getMinimoIndemnizableElegibleByPctminindemneleg() != null) { 
						auxB = vvm.getMinimoIndemnizableElegibleByPctminindemneleg().getPctminindem(); 
					} else if (vvm.getTipoFranquiciaByTipofranquiciaeleg() != null) { 
						auxB = new BigDecimal( 
								vvm.getTipoFranquiciaByTipofranquiciaeleg().getCodtipofranquicia().toString()); 
					} else if (vvm.getPctFranquiciaElegibleByCodpctfranquiciaeleg() != null) { 
						auxB = vvm.getPctFranquiciaElegibleByCodpctfranquiciaeleg().getCodpctfranquiciaeleg(); 
					} else if (vvm.getGarantizadoByGarantizadoeleg() != null) { 
						auxB = vvm.getGarantizadoByGarantizadoeleg().getCodgarantizado(); 
					} 
					// asignamos dicho codigo buscado al combovinculado 
					funcion.append("if (").append(idComboT).append("_c == 'false'){"); 
					funcion.append("$('#").append(idComboT).append("').val(").append(auxB).append(");").append(idComboT) 
							.append("_c = 'true';}"); 
					if (cerrarLlave) { 
						funcion.append("}"); 
					} 
				} 
				// Fin de bucles para lstVincValMod 
			} 
 
		} 
		// DAA 05/07/2013 
		// Combos 
		funcion.append( 
				"$('select').each(function(){if($(this).attr('id').indexOf('selectAnexo')==0){actualizaArrayCobEleAnexo($(this).attr('id'));}});"); 
		// Checks 
		funcion.append( 
				"$('input[type=checkbox]').each(function(){if($(this).attr('id').indexOf('checkAnexo')>=0){actualizaArrayCobEleAnexoCheck($(this).attr('id'), $(this).is(':checked'));}});"); 
 
		funcion.append("controlCambioCoberturas();"); 
		funcion.append("}"); 
 
		logger.debug("creaFuncionesJSCombos: " + funcion.toString()); 
		return funcion.toString(); 
	} 
 
	/** 
	 * Metodo que devuelve un listado con todos los modulos disponibles para la 
	 * linea 
	 *  
	 * @param lineaseguroid 
	 * @return 
	 * @throws BusinessException 
	 */ 
	public List<Modulo> getModulosDisponibles(Long lineaseguroid) throws BusinessException { 
		List<Modulo> modulosPoliza = null; 
		try { 
 
			modulosPoliza = coberturasModificacionPolizaDao.getModulosPoliza(lineaseguroid); 
 
			return modulosPoliza; 
 
		} catch (DAOException dao) { 
			logger.error("Se ha producido un error al recuperar el listado de modulos", dao); 
			throw new BusinessException("Se ha producido un error al recuperar el listado de modulos", dao); 
		} 
	} 
 
	/*** DNF PET.63485.FIII 22/01/2021*/
	public List<String> getListaCeldasConVinculacion(ModuloView modulo){
		
		List<String> listaCodCeldasConVin = new ArrayList<String>();
		BigDecimal codConceptoPrincipalModulo = new BigDecimal(0);
		BigDecimal codRiesgoCubierto          = new BigDecimal(0);
		BigDecimal codConcepto                = new BigDecimal(0);
		
		for(ModuloFilaView mfl : modulo.getListaFilas()) {
			codConceptoPrincipalModulo = mfl.getCodConceptoPrincipalModulo();
			codRiesgoCubierto = mfl.getCodRiesgoCubierto();
			
			for(ModuloCeldaView mcv : mfl.getCeldas()) {
				codConcepto = mcv.getCodconcepto();
				
				for(ModuloValorCeldaView mvcv : mcv.getValores()) {
					if(null != mvcv.getColumnaVinculada() && null != mvcv.getFilaVinculada()) {
						String identificador = "selectAnexo_" + codConceptoPrincipalModulo + "_" + codRiesgoCubierto + "_" + codConcepto ;
						listaCodCeldasConVin.add(identificador);
						break;
					}
				}
			}
		}
		return listaCodCeldasConVin;
	}
	/***01/02/2021 PET.63485.FIII DNF CAPTURAMOS EL IDENTIFICADOR DE LA FILA CON RC ELEGIBLE*/
	public List<String> getlstIdChecksEleg(ModuloView modulo){
		
		List<String> listaIdChecksElegibles = new ArrayList<String>();
		
		for(ModuloFilaView mfl : modulo.getListaFilas()) {
			
			if(mfl.isRcElegible() == true) {
			
				String identificadorCheckAnexo = "checkAnexo_" + mfl.getCodConceptoPrincipalModulo() + "_" + mfl.getCodRiesgoCubierto() + "_363" ;
				listaIdChecksElegibles.add(identificadorCheckAnexo);	
			}
		}	
		return listaIdChecksElegibles;
	}
	/***FIN 01/02/2021 PET.63485.FIII DNF*/
	
	public List<String> getlstIdCeldasEleg(ModuloView modulo){
		
		List<String> listaIdCeldasElegibles = new ArrayList<String>();
		BigDecimal codConceptoPrincipalModulo = new BigDecimal(0);
		BigDecimal codRiesgoCubierto          = new BigDecimal(0);
		BigDecimal codConcepto                = new BigDecimal(0);
			
		for(ModuloFilaView mfl : modulo.getListaFilas()) {
			
			codConceptoPrincipalModulo = mfl.getCodConceptoPrincipalModulo();
			codRiesgoCubierto = mfl.getCodRiesgoCubierto();
			
			for(ModuloCeldaView mcv : mfl.getCeldas()) {
				
				if(mcv.isElegible()) {
					
					codConcepto = mcv.getCodconcepto();
					
					String identificador = "selectAnexo_" + codConceptoPrincipalModulo + "_" + codRiesgoCubierto + "_" + codConcepto ;
					listaIdCeldasElegibles.add(identificador);
						
				}
			}
		}
		return listaIdCeldasElegibles;
	}
	/*** FIN DNF PET.63485.FIII 22/01/2021*/
	
	public void setCoberturasModificacionPolizaDao(ICoberturasModificacionPolizaDao coberturasModificacionPolizaDao) { 
		this.coberturasModificacionPolizaDao = coberturasModificacionPolizaDao; 
	} 
 
	public void setDeclaracionModificacionPolizaDao( 
			IDeclaracionModificacionPolizaDao declaracionModificacionPolizaDao) { 
		this.declaracionModificacionPolizaDao = declaracionModificacionPolizaDao; 
	} 
 
	public void setPolizaCopyDao(IPolizaCopyDao polizaCopyDao) { 
		this.polizaCopyDao = polizaCopyDao; 
	} 
 
	public void setXmlAnexoModDao(IXmlAnexoModificacionDao xmlAnexoModDao) { 
		this.xmlAnexoModDao = xmlAnexoModDao; 
	} 
 
	public void setPolizaManager(PolizaManager polizaManager) { 
		this.polizaManager = polizaManager; 
	} 
 
	public void setModuloManager(ModuloManager moduloManager) { 
		this.moduloManager = moduloManager; 
	} 
 
	public void setCpmTipoCapitalDao(ICPMTipoCapitalDAO cpmTipoCapitalDao) { 
		this.cpmTipoCapitalDao = cpmTipoCapitalDao; 
	} 
 
	public void setCuadroCoberturasManager(ICuadroCoberturasManager cuadroCoberturasManager) { 
		this.cuadroCoberturasManager = cuadroCoberturasManager; 
	} 
 
	public void setSolicitudModificacionManager(ISolicitudModificacionManager solicitudModificacionManager) { 
		this.solicitudModificacionManager = solicitudModificacionManager; 
	} 
 
	public void setLineaDao(final ILineaDao lineaDao) { 
		this.lineaDao = lineaDao; 
	} 
	 
	/* Pet. 63485-Fase II ** MODIF TAM (10.09.2020) ** Inicio */ 
	public void setSeleccionComparativasAnexoSWManager( 
			ISeleccionComparativasAnexoSWManager seleccionComparativasAnexoSWManager) { 
		this.seleccionComparativasAnexoSWManager = seleccionComparativasAnexoSWManager; 
	}

	public ModulosYCoberturas getModulosYCoberturas() {
		return modulosYCoberturas;
	}

	public void setModulosYCoberturas(ModulosYCoberturas modulosYCoberturas) {
		this.modulosYCoberturas = modulosYCoberturas;
	} 
	
	/* Pet. 63485-Fase II ** MODIF TAM (10.09.2020) ** Fin */ 
} 
