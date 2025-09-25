package com.rsi.agp.core.managers.impl.coberturas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.managers.ICuadroCoberturasGanadoManager;
import com.rsi.agp.core.managers.impl.ModuloManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.models.coberturas.ICuadroCoberturasDao;
import com.rsi.agp.dao.models.cpl.ICaracteristicasModuloDao;
import com.rsi.agp.dao.models.poliza.IRiesgoCubiertoModuloGanadoDao;
import com.rsi.agp.dao.tables.coberturas.CuadroCobertura;
import com.rsi.agp.dao.tables.cpl.CaracteristicaModulo;
import com.rsi.agp.dao.tables.cpl.ConceptoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloCeldaView;
import com.rsi.agp.dao.tables.cpl.ModuloFilaView;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.ModuloValorCeldaView;
import com.rsi.agp.dao.tables.cpl.ModuloView;
import com.rsi.agp.dao.tables.cpl.gan.RiesgoCubiertoModuloGanado;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.VistaComparativas;
import com.rsi.agp.dao.tables.poliza.VistaComparativasId;

public class CuadroCoberturasGanadoManager implements ICuadroCoberturasGanadoManager{
	
	private static final Log logger = LogFactory.getLog(CuadroCoberturasGanadoManager.class);

	
	private final String ELEGIBLE = "<font color=\"red\">ELEGIBLE</font><br/>";
	
	private ModuloManager moduloManager;
	private IRiesgoCubiertoModuloGanadoDao riesgoCubiertoModuloGanadoDao;
	private ICaracteristicasModuloDao caracteristicaModuloDao;
	private ICuadroCoberturasDao cuadroCoberturasDao;
	
	
	/**
	 * MÃƒÂ©todo para obtener el objeto para pintar el cuadro de coberturas mediante AJAX.
	 * @param modulo Objeto con la estrucutra del cuadro.
	 * @param idtabla Identificador de la capa en la que se incluirÃƒÂ¡ el cuadro.
	 * @return Objeto JSON que representa un cuadro de coberturas.
	 * @throws JSONException
	 */
	public JSONObject getCoberturasJSON (ModuloView modulo, String idtabla) throws JSONException{
		JSONObject objeto = new JSONObject();
		objeto.put("modulo", moduloManager.crearTablaPoliza(modulo, idtabla));
		
		return objeto;
	}
		
	/**
	 * Devuelve los datos de la tabla de coberturas para el módulo indicado de líneas de Ganado
	 */
	public ModuloView getCoberturasModulo(String codmodulo, Poliza poliza, boolean informes) {
			
		// Obtiene el objeto módulo correspondiente al plan/línea y módulo indicados
		Modulo modulo = moduloManager.getModulo(codmodulo, poliza.getLinea().getLineaseguroid()); 
		
		// Genera el objeto que encapsula los datos de la tabla de coberturas para el módulo
		return (modulo != null) ? this.getCondicionesModuloGanado(modulo) : null;
	}
	
	public ModuloView getCoberturasModulo(String codmodulo, Long lineaseguroid, boolean informes) {
		
		// Obtiene el objeto módulo correspondiente al plan/línea y módulo indicados
		Modulo modulo = moduloManager.getModulo(codmodulo, lineaseguroid); 
		
		// Genera el objeto que encapsula los datos de la tabla de coberturas para el módulo
		return (modulo != null) ? this.getCondicionesModuloGanado(modulo) : null;
	}

	
	/**
	 * Genera las condiciones de cobertura de un módulo perteneciente a líneas de ganado
	 * @return
	 */
	private ModuloView getCondicionesModuloGanado (Modulo modulo) {
		
		// Objeto que encapsula la información de coberturas de un módulo
		ModuloView moduloView = new ModuloView();
		
		// Código y descripción del módulo
		moduloView.setCodModulo(modulo.getId().getCodmodulo());
		moduloView.setDescripcionModulo(modulo.getId().getCodmodulo() + " - " + modulo.getDesmodulo());
		
		// Las cabeceras del cuadro de coberturas serán los conceptos cubiertos por el módulo
		for (ConceptoCubiertoModulo ccm: modulo.getConceptoCubiertoModulos()){
			moduloView.getListaCabeceras().add(ccm.getDiccionarioDatos().getNomconcepto());
		}
		
		// Obtiene la lista de riesgos cubiertos del módulo de ganado
		List<RiesgoCubiertoModuloGanado> lstRcmGan = riesgoCubiertoModuloGanadoDao.getRiesgosCubiertosModuloGanado(modulo.getId());
		
		// Por cada riesgo cubierto del módulo se crea una fila en el cuadro de coberturas
		for (RiesgoCubiertoModuloGanado rcm : lstRcmGan){
			// Objecto que encapsula la información de una fila del cuadro de coberturas
			ModuloFilaView filaView = new ModuloFilaView();
			
			// Se asignan los datos generales de la fila
			filaView.setFilamodulo(new BigDecimal (rcm.getId().getFilamodulo()));
			filaView.setConceptoPrincipalModulo(rcm.getConceptoPpalModulo().getDesconceptoppalmod());
			filaView.setCodConceptoPrincipalModulo(rcm.getConceptoPpalModulo().getCodconceptoppalmod());
			filaView.setRiesgoCubierto((rcm.getElegible().equals(Constants.CHARACTER_S) ? ELEGIBLE : "") + rcm.getRiesgoCubierto().getDesriesgocubierto());
			filaView.setCodRiesgoCubierto(rcm.getRiesgoCubierto().getId().getCodriesgocubierto());
			
			// Obtiene la lista de características del módulo asocidas al riesgo cubierto del módulo actual
			List<CaracteristicaModulo> lstCarMod = this.caracteristicaModuloDao.getCaracteristicasModulo(rcm);
			
			List<ModuloCeldaView> valoresFila = new ArrayList<ModuloCeldaView>(moduloView.getListaCabeceras().size());
			
			// Recorre las características del módulo obtenidas
			for (CaracteristicaModulo carmod : lstCarMod){
				
				ModuloCeldaView tmpCelda = new ModuloCeldaView();				
				//Elegible
				tmpCelda.setElegible(carmod.getTipovalor().equals(new Character('E')));
				//Observaciones
				tmpCelda.setObservaciones((!StringUtils.nullToString(carmod.getObservaciones()).equals("")) ? carmod.getObservaciones() : null);
				
				logger.debug("###################################################");
				logger.debug("ESC-16842 - Valores celdas en el cuadro coberturas");
				// Recorre los registros de cuadro de coberturas que aplican por plan/línea, módulo, fila módulo y columna módulo 
				for (CuadroCobertura cc : this.cuadroCoberturasDao.getCoberturas(carmod)) {
					//Valor
					ModuloValorCeldaView tmpValor = new ModuloValorCeldaView();
					tmpValor.setFila(cc.getFilamodulo());
					tmpValor.setColumna(cc.getColumnamodulo());
					tmpValor.setDescripcion(cc.getDescripcion());
					logger.debug(cc.getDescripcion());
					tmpValor.setCodigo(cc.getCodigo());
					if (StringUtils.nullToString(tmpCelda.getCodconcepto()).equals(""))
						tmpCelda.setCodconcepto(cc.getCodconcepto());
					tmpCelda.getValores().add(tmpValor);
				}
				
				logger.debug("###################################################");

				
				// Añade a la fila la celda creada
				valoresFila.add(tmpCelda);
			}
			filaView.setCeldas(valoresFila);
			
			// Añadimos la fila a la vista de módulos
			moduloView.getListaFilas().add(filaView);
		}
		
		return moduloView;
	}
	
	
	/**
	 * Indica si hay comparativas elegibles para el plan/línea y módulos indicados
	 * @param codModulos Array de códigos de módulo
	 * @param Objeto línea asociado a los módulos
	 */
	@Override
	public boolean hayComparativasElegibles(String[] codModulos, Linea linea) {
		// Recorre los módulos seleccionados para determinar si alguno de ellos tiene comparativas elegibles
		// Si se detecta que un módulo tiene comparativas elegibles, no hay que seguir comprobando los demás
		for (String codModulo : codModulos) {
			// Lanza la consulta para comprobar si para el plan/línea y módulo hay comparativas elegibles
			if (this.riesgoCubiertoModuloGanadoDao.hayComparativasElegibles(codModulo, linea.getLineaseguroid())) return true;
		}
		
		return false;
	}
	
	
	@Override
	public HashMap<String, Object> crearComparativas(Poliza poliza, String[] modulos) {
		
		HashMap<String, Object> comparativas = new HashMap<String, Object>(); //padre de las comparativas
		LinkedHashMap<String, Object> comparativaModulo = new LinkedHashMap<String, Object>(); //una entrada por cada mÃƒÂ³dulo seleccionado
		
		for(String modulo : modulos){
			
			// MPM - 28/04/16
			// Si para el módulo actual no hay elegibles se crea una comparativa única
			if (!this.riesgoCubiertoModuloGanadoDao.hayComparativasElegibles(modulo, poliza.getLinea().getLineaseguroid())) {
				crearComparativaUnica(comparativaModulo, poliza.getLinea(), modulo);
			}
			// Si hay elegibles se monta la comparativa con todos los elegidos
			else {
				
				// Recorre las comparativas de la póliza para el módulo actual
				Set<ComparativaPoliza> comparativaPolizas = poliza.getComparativaPolizas();
				
				List<List<VistaComparativas>> combinacionesElementosComparativa = new ArrayList<List<VistaComparativas>>();
				List<VistaComparativas> combinacionVaciaElementosComparativa = new ArrayList<VistaComparativas>();
				
				for (ComparativaPoliza cp : comparativaPolizas) {
					
					// No se incluyen los no elegidos
					if (!(new BigDecimal (-2)).equals(cp.getId().getCodvalor())) {
						
						
						VistaComparativas vista0 = new VistaComparativas();
						VistaComparativasId vista0Id = new VistaComparativasId();
						
						vista0Id.setCodconceptoppalmod(cp.getId().getCodconceptoppalmod());
						vista0Id.setCodriesgocubierto(cp.getId().getCodriesgocubierto());
						vista0Id.setFilamodulo(cp.getId().getFilamodulo());
						
						vista0Id.setCodconcepto(cp.getId().getCodconcepto());
						vista0Id.setNomconcepto("Nom concepto");
						vista0Id.setCodmodulo(modulo);
						vista0Id.setCodvalor(cp.getId().getCodvalor());
						vista0Id.setColumnamodulo(new BigDecimal(0));
						vista0Id.setColumnamodulovinc(new BigDecimal(0));
						vista0Id.setDatovinculado('N');
						vista0Id.setDesconceptoppalmod("Desc CPM");
						vista0Id.setDesmodulo("Desc Modulo");
						vista0Id.setDesriesgocubierto("Desc RC");
						vista0Id.setDesvalor(cp.getDescvalor());
						vista0Id.setElegible('S');
						vista0Id.setFilamodulovinc(new BigDecimal(0));
						vista0Id.setLineaseguroid(poliza.getLinea().getLineaseguroid());
								
						vista0.setId(vista0Id);
						
						
						combinacionVaciaElementosComparativa.add(vista0);
						
					}
					
				}
				
				combinacionesElementosComparativa.add(combinacionVaciaElementosComparativa);
				
				comparativaModulo.put(modulo + " - Descripcion del modulo pendiente ", combinacionesElementosComparativa);
				
				 
				 
				 
				 
				 
				 
				 
				 
				 
				 
				 				
			}
			
		}

		comparativas.put("comparativa", comparativaModulo);
		
		return comparativas;
	}
	
	/**
	 * Crea la comparativa única asociada al plan/línea y módulo indicados
	 * @param comparativaModulo
	 * @param lineaseguroid
	 * @param modulo
	 */
	private void crearComparativaUnica(LinkedHashMap<String, Object> comparativaModulo, Linea linea, String modulo) {
		
		ModuloId auxId = new ModuloId();
		auxId.setCodmodulo(modulo);
		auxId.setLineaseguroid(linea.getLineaseguroid());
		String desModulo = ((Modulo) this.cuadroCoberturasDao.getObject(Modulo.class, auxId)).getDesmodulo();
		
		List<RiesgoCubiertoModuloGanado> lstRcmDef = this.riesgoCubiertoModuloGanadoDao.getRiesgosCubiertosModuloGanado(auxId);
		
		VistaComparativas vista0 = new VistaComparativas();
		VistaComparativasId vista0Id = new VistaComparativasId();
		
		vista0Id.setCodconceptoppalmod(lstRcmDef.get(0).getConceptoPpalModulo().getCodconceptoppalmod());
		vista0Id.setCodriesgocubierto(lstRcmDef.get(0).getRiesgoCubierto().getId().getCodriesgocubierto());
		vista0Id.setFilamodulo(new BigDecimal (lstRcmDef.get(0).getId().getFilamodulo()));
		
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
		vista0Id.setLineaseguroid(linea.getLineaseguroid());
		vista0Id.setNomconcepto("");
				
		vista0.setId(vista0Id);
		
		List<List<VistaComparativas>> combinacionesElementosComparativa = new ArrayList<List<VistaComparativas>>();
		List<VistaComparativas> combinacionVaciaElementosComparativa = new ArrayList<VistaComparativas>();
		combinacionVaciaElementosComparativa.add(vista0);
		combinacionesElementosComparativa.add(combinacionVaciaElementosComparativa);
		
		comparativaModulo.put(modulo + " - " + desModulo, combinacionesElementosComparativa);
		
	}
	
	
	
	
	
	
	
	
	public void setModuloManager(ModuloManager moduloManager) {
		this.moduloManager = moduloManager;
	}


	public void setRiesgoCubiertoModuloGanadoDao(
			IRiesgoCubiertoModuloGanadoDao riesgoCubiertoModuloGanadoDao) {
		this.riesgoCubiertoModuloGanadoDao = riesgoCubiertoModuloGanadoDao;
	}


	public void setCaracteristicaModuloDao(
			ICaracteristicasModuloDao caracteristicaModuloDao) {
		this.caracteristicaModuloDao = caracteristicaModuloDao;
	}


	public void setCuadroCoberturasDao(ICuadroCoberturasDao cuadroCoberturasDao) {
		this.cuadroCoberturasDao = cuadroCoberturasDao;
	}


	


}
