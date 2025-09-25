package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.admin.IClaseDao;
import com.rsi.agp.dao.tables.admin.Clase;
import com.rsi.agp.dao.tables.admin.ClaseDetalle;
import com.rsi.agp.dao.tables.admin.ClaseDetalleGanado;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.AseguradoAutorizadoSC;
import com.rsi.agp.dao.tables.cpl.Medida;
import com.rsi.agp.dao.tables.cpl.gan.AseguradoAutorizadoGanado;
import com.rsi.agp.dao.tables.cpl.gan.MedidaG;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.LineaGrupoNegocio;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class ClaseManager implements IManager {
	
	private Log logger = LogFactory.getLog(ClaseManager.class);
	final ResourceBundle bundle = ResourceBundle.getBundle("displaytag");
	final ResourceBundle bundle_agp = ResourceBundle.getBundle("agp");
	private IClaseDao claseDao;
	private CargaAseguradoManager cargaAseguradoManager;
	private SeleccionPolizaManager seleccionPolizaManager;
	
	public final Clase getClase(final Long id) {
		return (Clase)claseDao.getObject(Clase.class, id);
	}
	
	/**
	 * Obtiene el objeto Clase correspondiente a la poliza
	 * @param p
	 * @return
	 */
	public final Clase getClase(Poliza p) {
		try {
			return claseDao.getClase(p);
		} catch (DAOException e) {
			logger.error("Ocurrio un error al obtener la clase asociada a la poliza", e);
			return null;
		}
	}
	
	
	public final ClaseDetalle getClaseDetalle(final Long id) {
		return (ClaseDetalle)claseDao.getObject(ClaseDetalle.class, id);
	}
	
	public void guardarUsuario(Usuario usuario) {
		try {
			claseDao.saveOrUpdate(usuario);
		} catch (DAOException e) {
			logger.error("Se ha producido un error al guardar el usuario",e);
		}
	}
	
	public List<Clase> getListaClases(Clase claseBean) throws DAOException{
		try{
			return claseDao.getListaClases(claseBean);
		
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}
	
	public List<ClaseDetalle> getListaDetalleClases(ClaseDetalle claseDetalleBean) {
		return claseDao.getListaDetalleClases(claseDetalleBean);
	}

	public void setClaseDao(IClaseDao claseDao) {
		this.claseDao = claseDao;
	}
	
	/**
	 * Obtiene el idClase a partir de lineaseguroid y clase
	 * @param p
	 * @return
	 */
	public Long getClase(long lineaseguroid,BigDecimal clase) {
		Long idClase = null;
		Clase cl = claseDao.getClase(lineaseguroid, clase);
		if (cl != null)
			idClase = cl.getId();
		return idClase;
			
	}
	
	public Map<String, Object> cargaClase(HttpServletRequest request, Long id) throws BusinessException, DAOException{
		try{
			Map<String, Object> parameters = new HashMap<String, Object>();
			List<Clase> listaClases = null;
			
			Clase clase = getClase(id);
			Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
			
			// AMG 20/09/2012 
			// ----> Asegurado antiguo: (existe en AAC) se continua con la carga de la clase
			// ----> Asegurado nuevo: si comprobarAac de la clase es 'S' buscar en AAC un registro con nif nulo y lineaseguroid correspondiente
			//       si existe algun registro se carga la clase
			Boolean cargarClase = false;
			if (clase.getComprobarAac() != null && "S".equals(clase.getComprobarAac().toString())) {
				Boolean puedeContratar = Boolean.FALSE;
				Object[] aacArr;
				if (clase.getLinea().isLineaGanado()) {
					aacArr = cargaAseguradoManager.getAACGan(usuario.getAsegurado().getNifcif(),
							clase.getLinea().getLineaseguroid());
					Set<ClaseDetalleGanado> claseDetSet = clase.getClaseDetallesGanado();
					if (aacArr != null && claseDetSet != null) {
						for (AseguradoAutorizadoGanado aac : (AseguradoAutorizadoGanado[]) aacArr) {
							String codmodulo = aac.getId().getCodmodulo();
							if (Constants.TODOS_MODULOS.equals(codmodulo)) {
								puedeContratar = Boolean.TRUE;
								break;
							} else {
								for (ClaseDetalleGanado claseDet : claseDetSet) {
									if (Constants.TODOS_MODULOS.equals(codmodulo)
											|| codmodulo.equals(claseDet.getCodmodulo())) {
										puedeContratar = Boolean.TRUE;
										break;
									}
								}
							}
						}
					}
				} else {
					aacArr = cargaAseguradoManager.getAAC(usuario.getAsegurado().getNifcif(),
							clase.getLinea().getLineaseguroid());
					Set<ClaseDetalle> claseDetSet = clase.getClaseDetalles();
					if (aacArr != null && claseDetSet != null) {
						for (AseguradoAutorizadoSC aac : (AseguradoAutorizadoSC[]) aacArr) {
							String codmodulo = aac.getModulo().getId().getCodmodulo();
							if (Constants.TODOS_MODULOS.equals(codmodulo)) {
								puedeContratar = Boolean.TRUE;
								break;
							} else {
								for (ClaseDetalle claseDet : claseDetSet) {
									if (Constants.TODOS_MODULOS.equals(claseDet.getCodmodulo())
											|| codmodulo.equals(claseDet.getCodmodulo())) {
										puedeContratar = Boolean.TRUE;
										break;
									}
								}
							}
						}
					}
				}
				
				if(puedeContratar) {
					cargarClase = true;
				} else {
					logger.debug("El asegurado con la linea: " + clase.getLinea().getLineaseguroid().toString() + " no permite la contratacion");
					parameters.put("alerta", bundle_agp.getString("mensaje.clase.asegurado.noexisteaac.KO"));
					Linea lineaTemp = clase.getLinea();
					clase = new Clase();
					clase.setLinea(lineaTemp);
				}
			} else {
				cargarClase = true;
			}
			
			Clase claseBusqueda = new Clase();
			claseBusqueda = clase;
			listaClases = getListaClases(claseBusqueda);
			parameters.put("listaClases", listaClases);
			
			if (cargarClase) {	
				// asociamos la clase al usuario
				usuario.setClase(clase);
				
				// guardamos el usuario con la clase cargada
				guardarUsuario(usuario);
				parameters.put("mensaje", bundle_agp.getString("mensaje.clase.cargada.OK"));					
					
				//calculamos el intervalo de CoefReduccRdto
					String intervaloCoefReduccionRdtoStr = seleccionPolizaManager.calcularIntervaloCoefReduccionRdtoPoliza(usuario,id);
					
					request.getSession().setAttribute("intervaloCoefReduccionRdto", intervaloCoefReduccionRdtoStr);
				// fin calculo intervalo coefReduccRdto
					
				// P19876
				// Calculamos la medida	
				Long ganadoCount = usuario.getColectivo().getLinea().getEsLineaGanadoCount();
				if (ganadoCount >0) {
					Medida medida = calcularMedidaGanadoAsegurado(clase, usuario.getAsegurado().getNifcif());		
					request.getSession().setAttribute("medida", medida);
				}	
				// FIN P19876
			}	
			return parameters;
			
		}catch (BusinessException e) {
			logger.error("Error en la carga de la clase");
			throw new BusinessException();
		} catch (DAOException e) {
			logger.error("Error al acceder a bbdd");
			throw new DAOException();
		}
	}

public Medida calcularMedidaGanadoAsegurado(Clase clase, String nifCif) {
	Medida med = new Medida();
	med.setEsGanado(1);
	// 1- Obtenemos los grupos de negocio de la vista tipo_capital_ganado	
	List <MedidaG> lstMedidasG    = new ArrayList<MedidaG>();
	List<Character> lstGruposNeg = new ArrayList<Character>();
	Long lineaseguroid = clase.getLinea().getLineaseguroid();
	StringBuilder resultado = new StringBuilder();
	ArrayList<String> listMod        = new ArrayList<String>(0);				
	ArrayList<Long> listEspecies     = new ArrayList<Long>(0);
	ArrayList<Long> listRegimen      = new ArrayList<Long>(0);
	ArrayList<Long> listGrupoRaza    = new ArrayList<Long>(0);
	ArrayList<Long> listTipAnimal    = new ArrayList<Long>(0);
	ArrayList<Long> listTipCapital   = new ArrayList<Long>(0);
	ArrayList<Long> listProvincias   = new ArrayList<Long>(0);
	ArrayList<Long> listComarcas     = new ArrayList<Long>(0);
	ArrayList<Long> listTerminos     = new ArrayList<Long>(0);
	ArrayList<Character> listSubterminos  = new ArrayList<Character>(0);	
	Set<ClaseDetalleGanado> detalles = clase.getClaseDetallesGanado();
	Map <String,List<Long>> mapListas = new HashMap <String,List<Long>>();
	int totalTipo1 = 0;
	int totalTipo2 = 0;
	Map <String,BigDecimal> mapMaxMin = new HashMap <String,BigDecimal>();
	
	if (detalles != null && detalles.size()> 0) {
		for (ClaseDetalleGanado AuxclaseDetalles : detalles){
			if (AuxclaseDetalles.getCodmodulo() != null && !listMod.contains(AuxclaseDetalles.getCodmodulo().toString())) {
				listMod.add(AuxclaseDetalles.getCodmodulo().toString());
			}
			if (AuxclaseDetalles.getCodespecie() != null && !listEspecies.contains(AuxclaseDetalles.getCodespecie().longValue())) {
				listEspecies.add(AuxclaseDetalles.getCodespecie().longValue());
			}
			if (AuxclaseDetalles.getCodregimen() != null && !listRegimen.contains(AuxclaseDetalles.getCodregimen().longValue())) {
				listRegimen.add(AuxclaseDetalles.getCodregimen().longValue());
			}
			if (AuxclaseDetalles.getCodgruporaza() != null && !listGrupoRaza.contains(AuxclaseDetalles.getCodgruporaza().longValue())) {
				listGrupoRaza.add(AuxclaseDetalles.getCodgruporaza().longValue());
			}
			if (AuxclaseDetalles.getCodtipoanimal() != null && !listTipAnimal.contains(AuxclaseDetalles.getCodtipoanimal().longValue())) {
				listTipAnimal.add(AuxclaseDetalles.getCodtipoanimal().longValue());
			}
			if (AuxclaseDetalles.getCodtipocapital() != null && !listTipCapital.contains(AuxclaseDetalles.getCodtipocapital().longValue())) {
				listTipCapital.add(AuxclaseDetalles.getCodtipocapital().longValue());
			}
			if (AuxclaseDetalles.getCodprovincia() != null && !listProvincias.contains(AuxclaseDetalles.getCodprovincia().longValue())) {
				listProvincias.add(AuxclaseDetalles.getCodprovincia().longValue());
			}
			if (AuxclaseDetalles.getCodcomarca() != null && !listComarcas.contains(AuxclaseDetalles.getCodcomarca().longValue())) {
				listComarcas.add(AuxclaseDetalles.getCodcomarca().longValue());
			}
			if (AuxclaseDetalles.getCodtermino() != null && !listTerminos.contains(AuxclaseDetalles.getCodtermino().longValue())) {
				listTerminos.add(AuxclaseDetalles.getCodtermino().longValue());
			}
			if (AuxclaseDetalles.getSubtermino() != null && !listSubterminos.contains(AuxclaseDetalles.getSubtermino())) {
				if (!AuxclaseDetalles.getSubtermino().toString().trim().equals("")) { 
					listSubterminos.add(AuxclaseDetalles.getSubtermino());
				}
			}
		}
	
		// 2. Se comprueba si alguno de los valores de la clase detalle son genericos, si no existe se añade al mapa
		if (listMod.indexOf("99999") == -1) {} else { listMod = null;}						
		if (listEspecies.indexOf(new Long(999)) == -1) {mapListas.put("listEspecies",listEspecies);} else { mapListas.put("listEspecie",null);listEspecies = null;}					
		if (listRegimen.indexOf(new Long(999)) == -1) {mapListas.put("listRegimen",listRegimen);} else { mapListas.put("listRegimen",null);}
		if (listGrupoRaza.indexOf(new Long(999)) == -1) {mapListas.put("listGrupoRaza",listGrupoRaza);} else { mapListas.put("listGrupoRaza",null);}
		if (listTipAnimal.indexOf(new Long(999)) == -1) {mapListas.put("listTipAnimal",listTipAnimal);} else { mapListas.put("listTipAnimal",null);}
		if (listTipCapital.indexOf(new Long(999)) == -1) {mapListas.put("listTipCapital",listTipCapital);} else { mapListas.put("listTipCapital",null);}
		if (listProvincias.indexOf(new Long(99)) == -1) {mapListas.put("listProvincias",listProvincias);} else { mapListas.put("listProvincias",null);}
		if (listComarcas.indexOf(new Long(99)) == -1) {mapListas.put("listComarcas",listComarcas);} else { mapListas.put("listComarcas",null);}
		if (listTerminos.indexOf(new Long(999)) == -1) {mapListas.put("listTerminos",listTerminos);} else { mapListas.put("listTerminos",null);}
		if (listSubterminos.indexOf('9') == -1 &&  listSubterminos.size() >0) {} else {listSubterminos=null;}
		
		if(clase.getLinea()!=null && clase.getLinea().getLineaGrupoNegocios()!=null && clase.getLinea().getLineaGrupoNegocios().size()>0){
			for (LineaGrupoNegocio lgn : clase.getLinea().getLineaGrupoNegocios()) {
				lstGruposNeg.add(lgn.getId().getGrupoNegocio());
			}
		}else{
			//No debería pasar por aquí
			logger.debug("## claseDao.obtenerGruposNegocio ");
			lstGruposNeg =claseDao.obtenerGruposNegocio(mapListas,listMod,listSubterminos);
		}
				
		resultado.append("[ClaseManager]getMedidasGanado: lineaseguroid: " + lineaseguroid + " asegurado: "+ nifCif);
		resultado.append(" listGruposNegocio: ");if (lstGruposNeg != null)resultado.append(StringUtils.nullToString(lstGruposNeg.toString()));else resultado.append(" null" );
		resultado.append(" listModulos: ");if (listMod != null)resultado.append(StringUtils.nullToString(listMod.toString()));else resultado.append(" null" );
		resultado.append(" listEspecies: ");if (listEspecies != null)resultado.append(StringUtils.nullToString(listEspecies.toString()));else resultado.append(" null" );
		logger.debug(resultado.toString());
		
		// 3. Recogemos las medidas de ganado
		lstMedidasG = claseDao.getMedidasGanado(lineaseguroid,lstGruposNeg,listMod,listEspecies, nifCif);
		
	}else {
		logger.debug("Clase id "+ clase.getId() + " sin detalleClase");
	}
	
	
	//recorremos las medidas y vemos la cantidad de tipos diferentes que hay
	for (MedidaG medG : lstMedidasG) {
		if (medG.getCodtipomedida().compareTo(new Long(1))==0)
			totalTipo1++;//= medG.getCodtipomedida().intValue();
		
		if (medG.getCodtipomedida().compareTo(new Long(2))==0)
			totalTipo2++;//= medG.getCodtipomedida().intValue();	
	}
	
	if (lstMedidasG != null && lstMedidasG.size()>0){
		if (totalTipo1 >0 && totalTipo2 ==0) {// tipos solo bonificaciones
			mapMaxMin = getMaxMin(lstMedidasG);
			if (mapMaxMin.get("min").compareTo(mapMaxMin.get("max"))==0) { //solo un único valor
				String  tipoMedida = lstMedidasG.get(0).getCodtipomedida().toString();
				med.setTipomedidaclub(tipoMedida.charAt(0));
				med.setPctbonifrecargo(lstMedidasG.get(0).getPctmedida());
			}else { // distintos valores
				med.setTipomedidaclub(new Character('9'));
				med.setPctMin("+"+mapMaxMin.get("min").toString());
				med.setPctMax("+"+mapMaxMin.get("max").toString());
			}
		}else if (totalTipo2 >0 && totalTipo1 ==0) {// tipos solo recargos
			mapMaxMin = getMaxMin(lstMedidasG);
			if (mapMaxMin.get("min").compareTo(mapMaxMin.get("max"))==0) { //solo un único valor
				String  tipoMedida = lstMedidasG.get(0).getCodtipomedida().toString();
				med.setTipomedidaclub(tipoMedida.charAt(0));
				med.setPctbonifrecargo(lstMedidasG.get(0).getPctmedida());
			}else { // distintos valores
				med.setTipomedidaclub(new Character('9'));
				med.setPctMin("-"+mapMaxMin.get("min").toString());
				med.setPctMax("-"+mapMaxMin.get("max").toString());
			}
		}else if (totalTipo1 >0 && totalTipo2>0) { // hay de los dos tipos, calculamos el max de cada tipo
			mapMaxMin = getMaximosBonRec(lstMedidasG);
			med.setTipomedidaclub(new Character('9'));
			med.setPctMin("+"+mapMaxMin.get("maxB").toString());
			med.setPctMax("-"+mapMaxMin.get("maxR").toString());
		}
	}

	if 	(med.getPctMax() != null && med.getPctMin() != null){
		logger.debug("Medida del asegurado " + nifCif + ": Bon/Rec: "+ StringUtils.nullToString(med.getPctMin()) + " / " +
				StringUtils.nullToString(med.getPctMax()));
	}else if (med.getTipomedidaclub() != null && med.getPctbonifrecargo() != null){
		logger.debug("Medida del asegurado " + nifCif + ": tipomedida: " + StringUtils.nullToString(med.getTipomedidaclub()) + 
				", " + StringUtils.nullToString(med.getPctbonifrecargo()));
	}
	else{
		med = new Medida();
		logger.debug("Sin Medida");
	}
	return med;
}

public Map <String,BigDecimal> getMaxMin(List <MedidaG> lstMedidasG) {
	Map <String,BigDecimal> mapMaxMin = new HashMap <String,BigDecimal>();
	BigDecimal pctMax = new BigDecimal(0);
	BigDecimal pctMin = new BigDecimal(0);
	boolean primera = true;
	for (MedidaG medG : lstMedidasG) {
		if (primera){
			pctMin = medG.getPctmedida();
			primera = false;
		}
		if (medG.getPctmedida().compareTo(pctMax)== 1)
			pctMax = medG.getPctmedida();
		if (medG.getPctmedida().compareTo(pctMin)== -1)
			pctMin = medG.getPctmedida();
	}
	mapMaxMin.put("min", pctMin);
	mapMaxMin.put("max", pctMax);
	return mapMaxMin;	
}

public Map <String,BigDecimal> getMaximosBonRec(List <MedidaG> lstMedidasG) {
	Map <String,BigDecimal> mapMaxBonRec = new HashMap <String,BigDecimal>();
	BigDecimal pctMaxB = new BigDecimal(0);
	BigDecimal pctMaxR = new BigDecimal(0);
	for (MedidaG medG : lstMedidasG) {
		if (medG.getCodtipomedida().compareTo(new Long(1))==0 && medG.getPctmedida().compareTo(pctMaxB)== 1)
			pctMaxB = medG.getPctmedida();
		if (medG.getCodtipomedida().compareTo(new Long(2))==0 && medG.getPctmedida().compareTo(pctMaxR)== 1)
			pctMaxR = medG.getPctmedida();
	}
	mapMaxBonRec.put("maxB", pctMaxB);
	mapMaxBonRec.put("maxR", pctMaxR);
	return mapMaxBonRec;
	
}

public void setSeleccionPolizaManager(
		SeleccionPolizaManager seleccionPolizaManager) {
	this.seleccionPolizaManager = seleccionPolizaManager;
}

public void setCargaAseguradoManager(CargaAseguradoManager cargaAseguradoManager) {
	this.cargaAseguradoManager = cargaAseguradoManager;
}


}

