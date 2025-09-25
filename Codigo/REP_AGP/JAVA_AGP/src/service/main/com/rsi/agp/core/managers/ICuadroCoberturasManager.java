package com.rsi.agp.core.managers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloView;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.poliza.Poliza;

public interface ICuadroCoberturasManager {
	
	/**
	 * Metodo para obtener los objetos necesarios para la lista de modulos y sus condiciones
	 * @param poliza Poliza
	 * @param idAnexo 
	 * @param modulos Modulos a mostrar
	 * @return
	 */
	public List<ModuloView> generaCondicionesModulos(Long idpoliza, Long lineaseguroid, String codlinea, BigDecimal clase, 
			String nifAsegurado, Long idAnexo, List<Modulo> modulos, boolean informes);
	
	public JSONObject getCoberturasJSON (ModuloView modulo, String idtabla) throws JSONException;
	
	public HashMap<String, Object> crearComparativas(Long lineaseguroid, String codlinea, BigDecimal clase, String nifAsegurado, String[] modulos);
	
	public ModuloView getCoberturasModulo(String codmodulo, String idtabla, Long lineaseguroid, boolean informes);
	public ModuloView getCoberturasModulo(String codmodulo, Poliza poliza, boolean informes);
	public JSONObject getCoberturas(Poliza poliza)throws BusinessException;
	/* Pet. 63485-Fase II ** MODIF TAM (21.09.2020) ** Inicio */
	public List<RiesgoCubiertoModulo> getRiesgosCubModuloCalcRendimiento(Long lineaseguroid, String codModulo) throws Exception;
	
	public JSONObject getCoberturasPpalCpl(Poliza poliza, String idCupon, String realPath) throws Exception;
	public JSONObject getCoberturasLectura(Poliza poliza) throws Exception;

	public AnexoModificacion getAnexo(String idAnexo);
}
