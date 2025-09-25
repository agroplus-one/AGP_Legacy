package com.rsi.agp.core.managers.impl.ganado;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.PrecioGanadoException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.ExplotacionCoberturaAnexoComparator;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.models.poliza.ganado.IDatosExplotacionAnexoDao;
import com.rsi.agp.dao.models.poliza.ganado.IPrecioGanadoDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.cgen.SistemaProduccion;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.commons.VistaTerminosAsegurable;
import com.rsi.agp.dao.tables.cpl.gan.Especie;
import com.rsi.agp.dao.tables.cpl.gan.GruposRazas;
import com.rsi.agp.dao.tables.cpl.gan.MascaraPrecioGanado;
import com.rsi.agp.dao.tables.cpl.gan.PrecioGanado;
import com.rsi.agp.dao.tables.cpl.gan.RegimenManejo;
import com.rsi.agp.dao.tables.cpl.gan.TiposAnimalGanado;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.explotaciones.DatosVarExplotacionAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCoberturaAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCoberturaVincAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRazaAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.PrecioAnimalesModuloAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.SWModulosCobExplotacionAnexo;

import es.agroseguro.serviciosweb.contratacionayudas.ModulosCoberturasResponse;

@SuppressWarnings("unchecked")
public class DatosExplotacionesAnexoManager implements IManager {

	private DatosExplotacionesManager datosExplotacionesManager;
	
	private IDatosExplotacionAnexoDao datosExplotacionAnexoDao;
	private IPrecioGanadoDao precioGanadoDao;
	private IPolizaDao polizaDao;
	
	private static final Log logger = LogFactory.getLog(DatosExplotacionesAnexoManager.class);
	private final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	private ContratacionAyudasHelper contratacionAyudasHelper = new ContratacionAyudasHelper();
	
	public Map<String, Object> alta(ExplotacionAnexo explotacionAnexo, boolean validar) {
		Map<String, Object> paramReturn = new HashMap<String, Object>();
		// Validaciones previas al alta de explotación
		paramReturn = validacionesPrevias (explotacionAnexo,validar);
		// Si se han encontrado errores de validación no se prosigue con el alta
		if (!paramReturn.isEmpty()) {
			paramReturn.put("explotacionAnexoBean", explotacionAnexo);
			return paramReturn;
		}
		
		// Alta de registro de explotación
		try {

			if(explotacionAnexo.getNumero()==null){
				Integer numExplotacion = datosExplotacionAnexoDao.calcularNuevoNumeroExplotacion(explotacionAnexo.getAnexoModificacion().getId());
				explotacionAnexo.setNumero(numExplotacion);
			}
			
			ExplotacionAnexo e = (ExplotacionAnexo) this.datosExplotacionAnexoDao.saveOrUpdate(explotacionAnexo);
			this.datosExplotacionAnexoDao.evict(e);
			paramReturn.put(Constants.KEY_MENSAJE, bundle.getString("mensaje.datosExplotacion.altaOK"));
			paramReturn.put("explotacionAnexoBean", e);
		} catch (Exception e) {
			logger.error("Error al guardar la explotación de anexo", e);
			paramReturn.put(Constants.KEY_ALERTA, bundle.getString("mensaje.datosExplotacion.altaKO"));
			paramReturn.put("explotacionAnexoBean", explotacionAnexo);
		}
		
		return paramReturn;
	}
	
	public void borrarListaDatosVariables(ExplotacionAnexo ex) {
		
		try {
			for (GrupoRazaAnexo grupoRazaAnexo : ex.getGrupoRazaAnexos()) {
				List<DatosVarExplotacionAnexo> lista = this.datosExplotacionAnexoDao.findFiltered(DatosVarExplotacionAnexo.class, 
						new String[] {"grupoRazaAnexo.id"}, 
						new Object[] {grupoRazaAnexo.getId()}, null);
				
				if(!lista.isEmpty()){
					grupoRazaAnexo.getDatosVarExplotacionAnexos().removeAll(lista);
					this.datosExplotacionAnexoDao.deleteAll(lista);
				}
			}
		} catch (Exception e) {
			logger.error("Error al borrar la lista de datos variables asociada a la explotación de anexo de anexo", e);
		}
		
	}
	
	public void borrarListaDatosVariables(ExplotacionAnexo ex, String gruporazaid) {
		if (!StringUtils.nullToString(gruporazaid).equals("")) {
			try {
				for (final GrupoRazaAnexo grupoRazaAnexo : ex.getGrupoRazaAnexos()) {

					if (grupoRazaAnexo.getId().equals(new Long(gruporazaid))) {
						List<DatosVarExplotacionAnexo> lista = this.datosExplotacionAnexoDao.findFiltered(
								DatosVarExplotacionAnexo.class, new String[] { "grupoRazaAnexo.id" },
								new Object[] { grupoRazaAnexo.getId() }, null);

						if (!lista.isEmpty()) {
							grupoRazaAnexo.getDatosVarExplotacionAnexos().removeAll(lista);
							this.datosExplotacionAnexoDao.deleteAll(lista);
						}
					}
				}
			} catch (Exception e) {
				logger.error("Error al borrar la lista de datos variables asociada a la explotación de anexo", e);
			}
		}
	}
	
	/**
	 * Devuelve el precio correspondiente a la explotación indicada
	 * @throws FechaPrecioGanadoException 
	 * @throws PrecioGanadoException 
	 */
	public List<PrecioAnimalesModuloAnexo> calcularPrecio(ExplotacionAnexo explotacion) throws PrecioGanadoException {
		
		logger.debug("**@@** DatosExplotacionesAnexoManager-calcularPrecio [INIT]");
		logger.debug("**@@** Valor de explotacion.id:"+explotacion.getId());
		logger.debug("**@@** Valor de explotacion.getGrupoRazaAnexos().size():"+explotacion.getGrupoRazaAnexos().size());
		
		List<PrecioAnimalesModuloAnexo> lista = new ArrayList<PrecioAnimalesModuloAnexo>();

		
		logger.debug("idPoliza asociada a la explotacion: " + explotacion.getAnexoModificacion().getPoliza().getIdpoliza());

		// Obtiene la póliza asociada a la explotación
		Poliza poliza = getPoliza(explotacion.getAnexoModificacion().getPoliza().getIdpoliza());
		if (poliza == null) {
			return lista;
		}
		explotacion.getAnexoModificacion().setPoliza(poliza);
		
		String codModulo = explotacion.getAnexoModificacion().getCodmodulo();
		
		logger.debug("**@@** Valor de codModulo:"+codModulo);
		
		// Obtener máscaras de precios asociadas a los datos de la explotación
		List<MascaraPrecioGanado> lstMascPrecio = precioGanadoDao.getMascarasPrecioExplotacion(explotacion, codModulo);
		
		// Si hay máscaras de precio para la explotación hay que comprobar que se haya informado de todos los datos variables asociados
		boolean buscarPrecio = true;
		PrecioGanado pgDv = new PrecioGanado();
		if (!lstMascPrecio.isEmpty()) {
			
			logger.debug("Dentro de lstMascPrecio != de vacío");
			
			logger.debug("Num. elem. GrupoRazaAnexos: " + explotacion.getGrupoRazaAnexos().size());
			for (GrupoRazaAnexo grupoRaza :  explotacion.getGrupoRazaAnexos()) {
				
				logger.debug("Gruporaza id: " + grupoRaza.getId());
				
				Set<DatosVarExplotacionAnexo> lstDatVariables = grupoRaza.getDatosVarExplotacionAnexos();
				
				for (MascaraPrecioGanado mpg : lstMascPrecio) {
					/*DNF: ESC-6756****06/08/2019***********/
					//le llega un null al hacer la conversion de BigDecimal a integer:
					//int codconcepto = mpg.getDiccionarioDatos().getCodconcepto().intValue();
					int codconcepto = 0;
					
					logger.debug("MascaraPrecioGanado: " + mpg);
					logger.debug("El valor del Diccionario de datos es: " + mpg.getDiccionarioDatos());
					
					if (null != mpg.getDiccionarioDatos() && null != mpg.getDiccionarioDatos().getCodconcepto()) {
						codconcepto = mpg.getDiccionarioDatos().getCodconcepto().intValue();
						logger.debug("codconcepto: " + mpg.getDiccionarioDatos().getCodconcepto().intValue());
					}
					/*FIN DNF: ESC-6756********06/08/2019********/
					
					if (ArrayUtils.contains(new int[] {
							ConstantsConceptos.CODCPTO_NUM_ANIM_ACUM_DESDE,
							ConstantsConceptos.CODCPTO_NUM_ANIM_ACUM_HASTA },
							codconcepto)) {
						
						logger.debug("**@@** ENTRAMOS EN EL IF");
						Long numAnimales = grupoRaza.getNumanimales();
						logger.debug("numAnimales: " + numAnimales);
						if (numAnimales != null) {
							logger.debug("**@@** ENTRAMOS EN EL IF(2)");
							switch (codconcepto) {
							case ConstantsConceptos.CODCPTO_NUM_ANIM_ACUM_DESDE:
								pgDv.setNumAnimalesAcumDesde(numAnimales);
								break;
							case ConstantsConceptos.CODCPTO_NUM_ANIM_ACUM_HASTA:
								pgDv.setNumAnimalesAcumHasta(numAnimales);
								break;
							default:
								break;
							}
						} else {
							logger.debug("**@@** ENTRAMOS EN EL ELSE(2)");
							buscarPrecio = false;
							logger.debug("buscarPrecio seteado a false: " + buscarPrecio);
							break;
						}
					} else {
						logger.debug("**@@** ENTRAMOS EN EL ELSE");
						logger.debug("Num. elem. lstDatVariables: " + lstDatVariables.size());
						
						if (!isDVInformado(lstDatVariables,
								mpg, pgDv)) {
							logger.debug("**@@** ENTRAMOS EN EL IF(3)");
							buscarPrecio = false;
							logger.debug("buscarPrecio seteado a false**: " + buscarPrecio);
							break;
						}
					}
					/* ESC-4437 ** MODIF TAM (11.01.2019) ** Fin */
				}
			}
			
		}
		
		logger.debug("buscarPrecio: " + buscarPrecio);
		// Si todos los DV necesarios para el cálculo de precio están informados
		if (buscarPrecio) {
			// Obtener máscaras del taller para conceptos que necesitan operadores especiales (NÚMERO DE ANIMALES) - PENDIENTE!!
			
			// Obtener el registro de la tabla de precios de Ganado para los datos de la explotacion
			PrecioGanado precioGanado = precioGanadoDao.getPrecioExplotacion(explotacion, codModulo, pgDv);
			
			logger.debug("precioGanado: " + precioGanado);
			logger.debug("explotacion: " + explotacion);
			logger.debug("codModulo: " + codModulo);
			logger.debug("pgDv: " + pgDv);
			
			// Crear el objeto PrecioAnimalesModulo correspondiente al precio obtenido y añadirlo a la lista
			// Si se ha encontrado precio
			if(precioGanado != null){
				lista.add(new PrecioAnimalesModuloAnexo(precioGanado.getModulo().getId().getCodmodulo(), precioGanado.getPrecioGanadoDesde(), precioGanado.getPrecioGanadoHasta()));
				logger.debug("precioGanado es distinto de null y creamos el objeto PrecioAnimalesModuloAnexo con los siguis valores: ");
				logger.debug("codModulo: " + precioGanado.getModulo().getId().getCodmodulo());
				logger.debug("PrecioGanadoDesde: " + precioGanado.getPrecioGanadoDesde());
				logger.debug("PrecioGanadoHasta: " + precioGanado.getPrecioGanadoHasta());
				
			} else {
				logger.debug("precioGanado es null porque no ha encontrado precio");
				// Si no se ha encontrado precio
				lista.add(new PrecioAnimalesModuloAnexo(codModulo, null, null));
			}
		} else{
			// Si no se ha buscado precio
			logger.debug("No se ha buscado precio");
			lista.add(new PrecioAnimalesModuloAnexo(codModulo, null, null));
		}
		return lista;
	}

	public Poliza getPoliza(Long idPoliza) {
		try {
			return (Poliza) precioGanadoDao.get(Poliza.class, idPoliza);
		} catch (DAOException e1) {
			logger.error("Error al obtener la póliza asociada a la explotación", e1);
			return null;
		}
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public ExplotacionAnexo obtenerExplotacionAnexoById(Long id){
		
		ExplotacionAnexo exp = null;
		
		try{
			exp = (ExplotacionAnexo)datosExplotacionAnexoDao.get(ExplotacionAnexo.class, id);
			// Si tiene datos variables, se cargan las descripciones de los configurados por lupa
			for (GrupoRazaAnexo gr : exp.getGrupoRazaAnexos()) {
				for (DatosVarExplotacionAnexo dv : gr.getDatosVarExplotacionAnexos()) {
					if (dv.getValor() != null) dv.setDesValor(datosExplotacionesManager.getDescDatoVariable(dv.getCodconcepto(), dv.getValor()));
				}
			}
		}catch (Exception e1){
			logger.error("Error al obtener la explotación de anexo con id " + id, e1);
		}
		return exp;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public Set<ExplotacionCoberturaAnexo> obtenerCoberturasExplotacionById(Long id){
		
		List<ExplotacionCoberturaAnexo> lstCob = null;
		Set <ExplotacionCoberturaAnexo> setCob = new HashSet<ExplotacionCoberturaAnexo>();
		try{
			lstCob = (List<ExplotacionCoberturaAnexo>)datosExplotacionAnexoDao.getObjects(ExplotacionCoberturaAnexo.class, "explotacionAnexo.id", id);
			
		}catch (Exception e1){
			logger.error("Error al obtener las coberturas explotación de anexo con id " + id, e1);
		}
		setCob.addAll(lstCob);
		return setCob;
	}
	
	public void borrarGrupoRazaAnexo(Long id){
		try {
			if(id!=null){
				datosExplotacionAnexoDao.delete(GrupoRazaAnexo.class, id);
			}
		} catch (Exception e) {
			logger.error("Error al borrar la lista de datos variables asociada a la explotación de anexo", e);
		}
	}
	
	
	/**
	 * Valida los datos identificativos del formulario de explotación de anexo
	 * @param explotacion
	 * @return
	 */
	public Map<String, Object> validacionesPreviasDatosIdentificativos(ExplotacionAnexo explotacion) {
		
		Map<String, Object> paramReturn = new HashMap<String, Object>();
		
		// Validación del término asociado a la explotación
		if (!esTerminoAsegurable(explotacion)) {
			logger.debug("El término asociado a la explotación no es asegurable");
			paramReturn.put(Constants.KEY_ALERTA, bundle.getString("mensaje.datosExplotacion.terminoKO"));
			return paramReturn;
		}
		
		// Validación de la especie
		if (!esValidaEspecie(explotacion)) {
			logger.debug("La especie asociada a la explotación no es válida");
			paramReturn.put(Constants.KEY_ALERTA, bundle.getString("mensaje.datosExplotacion.especieKO"));
			return paramReturn;
		}
		
		// Validación del régimen
		if (!esValidoRegimen(explotacion)) {
			logger.debug("El régimen asociado a la explotación no es válido");
			paramReturn.put(Constants.KEY_ALERTA, bundle.getString("mensaje.datosExplotacion.regimenKO"));
			return paramReturn;
		}
		return paramReturn;
	}
	
	
	private boolean isDVInformado (Set<DatosVarExplotacionAnexo> lstDatVariables, MascaraPrecioGanado mpg, PrecioGanado pgDv) {
		
		logger.debug("DatosExplotacionesAnexoManager - isDVInformado - init");
		
		if (mpg.getDiccionarioDatos() != null && mpg.getDiccionarioDatos().getCodconcepto() != null) {
			for (DatosVarExplotacionAnexo dv : lstDatVariables) {
				
				logger.debug("Recorre la lista de datos variables");
				
				if (dv.getCodconcepto() != null && dv.getCodconcepto().equals(mpg.getDiccionarioDatos().getCodconcepto().intValue())) {
					
					switch (dv.getCodconcepto().intValue()) {
						// Control Oficial Lechero - 1045
						case ConstantsConceptos.CODCPTO_CONTROL_OFICIAL_LECHERO: pgDv.setCodControlOficialLechero(new Long (dv.getValor()).longValue()); break;
						// Pureza - 1046
						case ConstantsConceptos.CODCPTO_PUREZA: pgDv.setCodPureza(new Long (dv.getValor()).longValue()); break;
						// IGP/DO Ganado - 1051
						case ConstantsConceptos.CODCPTO_IGPDO: pgDv.setCodIgpdoGanado(new Long (dv.getValor()).longValue()); break;					
						// Empresa gestora - 1049
						case ConstantsConceptos.CODCPTO_EMPRESA_GESTORA: pgDv.setCodGestora(new Long (dv.getValor()).longValue()); break;					
						// Sistema de almacenamiento - 1048
						case ConstantsConceptos.CODCPTO_SISTEMA_ALMACENAMIENTO: pgDv.setCodSistAlmacena(new Long (dv.getValor()).longValue()); break;
						// Excepción contratación - Explotación - 1063
						case ConstantsConceptos.CODCPTO_EXCP_CONTRATACION_EXPL: pgDv.setCodExcepContrExc(new Long (dv.getValor()).longValue()); break;
						// Excepción contratación - Póliza - 1111
						case ConstantsConceptos.CODCPTO_EXCP_CONTRATACION_PLZ: pgDv.setCodExcepContrPol(new Long (dv.getValor()).longValue()); break;
						// Tipo ganadería - 1052
						case ConstantsConceptos.CODCPTO_TIPO_GANADERIA: pgDv.setCodTipoGanaderia(new Long (dv.getValor()).longValue()); break;
						// Alojamiento - 1053
						case ConstantsConceptos.CODCPTO_ALOJAMIENTO: pgDv.setCodAlojamiento(new Long (dv.getValor()).longValue()); break;
						// Destino - 110
						case ConstantsConceptos.CODCPTO_DESTINO: pgDv.setCodDestino(new Long (dv.getValor()).longValue()); break;
						// Sistema de producción - 616
						case ConstantsConceptos.CODCPTO_SISTEMA_PRODUCCION: 
							pgDv.setSistemaProduccion(new SistemaProduccion (new BigDecimal (dv.getValor()), null)); break;
						case ConstantsConceptos.CODCPTO_PROD_ANUAL_MEDIA:
							pgDv.setProdAnualMedia(new BigDecimal(dv.getValor())); break;
					
						default:
							break;
					}
					
					logger.debug("Datos Variables informado");
					logger.debug("DatosExplotacionesAnexoManager - isDVInformado - end");

					return true;
				}
			}
			
		}else {
			
			logger.debug("Datos Variables informado");
			logger.debug("DatosExplotacionesAnexoManager - isDVInformado - end");
			return true;
		}
		
		logger.debug("Datos Variables no informado");
		logger.debug("DatosExplotacionesAnexoManager - isDVInformado - end");
		
		return false;
	}
	
	public Map<String, Object> validacionesPrevias (ExplotacionAnexo e, boolean validar) {
		
		Map<String, Object> paramReturn = new HashMap<String, Object>();
		if (validar){
			// Validación del término asociado a la explotación
			if (!esTerminoAsegurable(e)) {
				logger.debug("El término asociado a la explotación no es asegurable");
				paramReturn.put(Constants.KEY_ALERTA, bundle.getString("mensaje.datosExplotacion.terminoKO"));
				return paramReturn;
			}
			
			// Validación de la especie
			if (!esValidaEspecie(e)) {
				logger.debug("La especie asociada a la explotación no es válida");
				paramReturn.put(Constants.KEY_ALERTA, bundle.getString("mensaje.datosExplotacion.especieKO"));
				return paramReturn;
			}
			
			// Validación del régimen
			if (!esValidoRegimen(e)) {
				logger.debug("El régimen asociado a la explotación no es válido");
				paramReturn.put(Constants.KEY_ALERTA, bundle.getString("mensaje.datosExplotacion.regimenKO"));
				return paramReturn;
			}
		
			for (GrupoRazaAnexo grupoRaza :  e.getGrupoRazaAnexos()) {
				if (grupoRaza.getCodgruporaza() != null){
					// Validación del grupo de raza
					if (!esValidoGrupoRaza(e, grupoRaza)) {
						logger.debug("El grupo de raza asociado a la explotación no es válido");
						paramReturn.put(Constants.KEY_ALERTA, bundle.getString("mensaje.datosExplotacion.grupoRazaKO"));
						return paramReturn;
					}
				}	
					// Validación del tipo de capital
				if (grupoRaza.getCodtipocapital() != null){	
					if (!esValidoTipoCapital(grupoRaza)) {
						logger.debug("El tipo de capital asociado a la explotación no es válido");
						paramReturn.put(Constants.KEY_ALERTA, bundle.getString("mensaje.datosExplotacion.tipoCapitalKO"));
						return paramReturn;
					}
				}	
					// Validación del tipo de animal
				if (grupoRaza.getCodtipoanimal() != null){	
					if (!esValidoTipoAnimal(e, grupoRaza)) {
						logger.debug("El tipo de animal asociado a la explotación no es válido");
						paramReturn.put(Constants.KEY_ALERTA, bundle.getString("mensaje.datosExplotacion.tipoAnimalKO"));
						return paramReturn;
					}
				}
			}
		}
		return paramReturn;
	}
	
	private boolean esTerminoAsegurable(ExplotacionAnexo e){
		
		return esValidoGenerico(VistaTerminosAsegurable.class, 
			    new String[] {"id.lineaseguroid","id.codprovincia","id.codcomarca","id.codtermino","id.subtermino"}, 
			    new Object[] {e.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid(), 
							  e.getTermino().getId().getCodprovincia(),
							  e.getTermino().getId().getCodcomarca(),
							  e.getTermino().getId().getCodtermino(),
							  e.getTermino().getId().getSubtermino(),
							  });
			
	}
	
	private boolean esValidoGenerico(Class<?> clase, String[] campos, Object[] valores){
		
		try {
			List<Object> lista = this.datosExplotacionAnexoDao.findFiltered(clase, campos, valores, null);
			return (lista != null && !lista.isEmpty());
		} catch (Exception e1) {
			logger.error("Error al obtener el registro de " + clase + " filtrando por los campos " + campos + " por los valores " + valores);
		}
		return false;
	}
	
	private boolean esValidaEspecie(ExplotacionAnexo e) {
		return esValidoGenerico(Especie.class, new String[] {"id.lineaseguroid", "id.codespecie"}, new Object[] {e.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid(), e.getEspecie()});
	}
	
	private boolean esValidoGrupoRaza(ExplotacionAnexo e, GrupoRazaAnexo gr) {
		return esValidoGenerico(GruposRazas.class, new String[] {"id.lineaseguroid", "id.CodGrupoRaza"}, new Object[] {e.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid(), new Long (gr.getCodgruporaza())});
	}
	
	private boolean esValidoRegimen(ExplotacionAnexo e) {
		return esValidoGenerico(RegimenManejo.class, new String[] {"id.lineaseguroid", "id.codRegimen"}, new Object[] {e.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid(), e.getRegimen()});
	}
	
	private boolean esValidoTipoCapital(GrupoRazaAnexo gr) {
		return esValidoGenerico(TipoCapital.class, new String[] {"codtipocapital"}, new Object[] {gr.getCodtipocapital()});
	}
	
	private boolean esValidoTipoAnimal(ExplotacionAnexo e, GrupoRazaAnexo gr) {
		return esValidoGenerico(TiposAnimalGanado.class, new String[] {"id.lineaseguroid", "id.codTipoAnimal"}, new Object[] {e.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid(), new Long (gr.getCodtipoanimal())});
	}

	
	public Set<ExplotacionCoberturaAnexo> procesarCoberturasAnexo(ExplotacionAnexo exp, String coberturas){
		logger.info("Init - DatosExplotacionesManager - procesarCoberturas");
		Set<ExplotacionCoberturaAnexo> expCoberturas = exp.getExplotacionCoberturasAnexo();
		Set<ExplotacionCoberturaAnexo> setExpCoberturasFinal = new HashSet<ExplotacionCoberturaAnexo>();
		List<String> lstidsBBDD = new ArrayList<String>();
		List<String> lstidsPantalla = new ArrayList<String>();
		if (!coberturas.equals("")){
			// list ids Existentes en BBDD
			Map<String, ExplotacionCoberturaAnexo> keysBBDD = new HashMap<String, ExplotacionCoberturaAnexo>();
			for (ExplotacionCoberturaAnexo cob:expCoberturas) {
				lstidsBBDD.add(cob.getId().toString());
				keysBBDD.put(cob.getId().toString(), cob);
			}	
			
			// comprobamos si alguna de las que hay ya estaban seleccionadas
			String[] arrCobExistentes = coberturas.split(";");
			Map<String, ExplotacionCoberturaAnexo> keysPantalla = new HashMap<String, ExplotacionCoberturaAnexo>();
			for (int i = 0; i < arrCobExistentes.length; i++) {
				String[] cobExist = arrCobExistentes[i].split("\\|");
				keysPantalla.put(cobExist[0], null);
				lstidsPantalla.add(cobExist[0]);
			}
			boolean esDatoVar = false;
			for (int i = 0; i < arrCobExistentes.length; i++) {
				esDatoVar = false;
				String[] cobExist = arrCobExistentes[i].split("\\|");
				String[] cb_dv = arrCobExistentes[i].split("#");
				String[] dv = cb_dv[1].split("\\|");
				logger.debug(" Cob. ya existente: id: "+cobExist[0]+ " mod: "+cobExist[1]+ " fila: "+cobExist[2]+" CPM: "+cobExist[3]+" descCPM: "+cobExist[4]+ 
						" RC: "+cobExist[5]+" desc RC: "+cobExist[6]+ " vinculada: "+cobExist[7]+ " elegible: "+cobExist[8]+ " tipoCobertura: "+cobExist[9] + " checked: "+cobExist[10].charAt(0));
				if (dv != null && dv.length >4 && dv[0] != null && !dv[0].equals("null")  && !dv[0].equals("")){
					logger.debug(" DV: codCptoDV: "+dv[0]+ " desc DV: "+dv[1]+ " valorDV: "+dv[2]+" descValorDv: "+dv[3]+" columna: "+dv[4]+" elegido: "+dv[5]);
					esDatoVar = true;
				}
				boolean yaExiste = false;
				for (ExplotacionCoberturaAnexo cob:expCoberturas) {
					Integer fila = new Integer(cob.getFila());
					Integer cpm  = new Integer(cob.getCpm());
					Integer rc   = new Integer(cob.getRiesgoCubierto());
					logger.debug("id: "+ cob.getId()+ " fila: "+fila+ " cpm: "+cpm+ " rc: "+rc);
					// si modulo, fila, cpm, rg = igual q la q tengo, reviso si está checked
					if (cobExist[1].equals(cob.getCodmodulo()) &&  cobExist[2].equals(fila.toString()) &&  cobExist[3].equals(cpm.toString()) && cobExist[5].equals(rc.toString())) {
						
						
						
						
						if (cob.getDvCodConcepto() == null && !esDatoVar){ // es cobertura normal
							cob.setId(Long.parseLong(cobExist[0]));
							cob.setVinculada(cobExist[7]);
							if (cobExist[10].charAt(0) == 'S'){
								cob.setElegida('S');
								//break;
							}else{
								cob.setElegida('N');
								//break;
							}
						}else{
							if (cob.getDvCodConcepto() != null && esDatoVar){
								if (dv[0].equals(cob.getDvCodConcepto().toString()) && dv[2].equals(cob.getDvValor().toString())){
									cob.setId(Long.parseLong(cobExist[0]));
									if (cobExist[10].charAt(0) == 'S'){
										cob.setElegida('S');
										//break;
									}else{
										cob.setElegida('N');
										//break;
									}
									if (dv[5].charAt(0) == 'S'){						
										cob.setDvElegido('S');
										//break;
									}else{
										cob.setDvElegido('N');
									}
								}
							}
						}

					}
					if (cobExist[0].equals(cob.getId().toString())) { // cob ya la teníamos
						yaExiste = true;
						try {
							yaExiste = true;
							datosExplotacionAnexoDao.saveOrUpdate(cob);
							
							setExpCoberturasFinal.add(cob);
							
							keysBBDD.remove(cob.getId().toString());
							keysPantalla.remove(cob.getId().toString());
							lstidsBBDD.remove(cob.getId().toString());
						} catch (Exception e) {
							logger.error("Error al grabar la cobertura de anexo existente en BBDD ",e);
						}
						//lstidsExistentes.add(cob.getId().toString());
					}			
				}
				
				if (!yaExiste) {// es nueva
					ExplotacionCoberturaAnexo expCob = new ExplotacionCoberturaAnexo();
					expCob.setExplotacionAnexo(exp);
					expCob.setCodmodulo(cobExist[1]);
					expCob.setFila(Integer.parseInt(cobExist[2]));
					expCob.setCpm(Integer.parseInt(cobExist[3]));
					expCob.setCpmDescripcion(cobExist[4]);
					expCob.setRiesgoCubierto(Integer.parseInt(cobExist[5]));
					expCob.setRcDescripcion(cobExist[6]);
					expCob.setElegible(cobExist[8].trim().charAt(0));
					if (cobExist[9] != null && !cobExist[9].trim().equals(""))
						expCob.setTipoCobertura(cobExist[9].charAt(0));
					expCob.setElegida(cobExist[10].trim().charAt(0));
					// dato variable
					if (esDatoVar){
						//logger.debug(" DV: codCptoDV: "+dv[0]+ " desc DV: "+dv[1]+ " valorDV: "+dv[2]+" descValorDv: "+dv[3]+" columna: "+dv[4]]+" elegido: "+dv[5]);
						if (dv[0] != null)
							expCob.setDvCodConcepto(new Long(dv[0]));
						if (dv[1] != null)
							expCob.setDvDescripcion(dv[1]);
						if (dv[2] != null)
							expCob.setDvValor(new Long(dv[2]));
						if (dv[3] != null)
							expCob.setDvValorDescripcion(dv[3]);
						if (dv[4] != null)
							expCob.setDvColumna(new Long(dv[4]));
						if (dv[5] != null)
							expCob.setDvElegido(dv[5].charAt(0));
					}
					
					// primero grabamos
					try {
						datosExplotacionAnexoDao.saveOrUpdate(expCob);
						setExpCoberturasFinal.add(expCob);
						
						
					} catch (Exception e) {
						logger.error("Error al grabar la nueva cobertura de Anexo");
					}
					
					// Si tiene vinculada la creamos
					Set <ExplotacionCoberturaVincAnexo> setVinc = new HashSet<ExplotacionCoberturaVincAnexo>();
					if (!cobExist[7].equals("null") && !cobExist[7].equals("")){
						
						String[] cobVincs = cobExist[7].split("#");
						for (String cobVinc:cobVincs) {
							String[] cobV = cobVinc.split("\\.");
							ExplotacionCoberturaVincAnexo vi = new ExplotacionCoberturaVincAnexo();
							vi.setExplotacionCoberturaAnexo(expCob);
							vi.setFila(Integer.parseInt(cobV[1]));
							vi.setVinculacion(cobV[0].charAt(0));
							vi.setVinculacionElegida(cobV[2].charAt(0));
							if (cobV.length >5){
								vi.setDvColumna(new Long(cobV[4]));
								vi.setDvValor(new Long(cobV[5]));
							}
							try {
								datosExplotacionAnexoDao.saveOrUpdate(vi);
								setVinc.add(vi);
								
							} catch (Exception e) {
								logger.error("Error al grabar la nueva cobertura Vinculacion");
							}
						}
						
					}
					expCob.setExplotacionCoberturaVincAnexos(setVinc);
				}
			}
			logger.debug("ids a borrar: "+lstidsBBDD.toString());
			if (lstidsBBDD.size()>0){
				try {
					datosExplotacionAnexoDao.deleteCoberturasByIdsCob(exp.getId(),lstidsBBDD);
				} catch (DAOException e) {
					logger.error("Error al borrar las coberturas de anexo.",e);
				}
			}
		}else {
			logger.debug("No se han recogido coberturas de la pantalla, las borramos.");
			exp.getExplotacionCoberturasAnexo().clear();
			try {
				datosExplotacionAnexoDao.deleteCoberturasById(exp.getId());
			} catch (DAOException e) {
				logger.error("Error al grabar la nueva explotacion sin coberturas.",e);
			}
			
		}
		logger.info("Fin - DatosExplotacionesManager - procesarCoberturas");
		return setExpCoberturasFinal;
	}
	
	/**
	 * Obtiene las coberturas elegibles de una explotación de anexo
	 */
	public List<ExplotacionCoberturaAnexo> getCoberturasElegiblesExpAnexo(ExplotacionAnexo expAnexo,String realPath, String codUsuario,
			String cobExistentes, Long idPoliza, AnexoModificacion anexo) {
		logger.info("Init - DatosExplotacionesAnexoManager - getCoberturasElegiblesExpAnexo");
		String xmlData=null;
		List<ExplotacionCoberturaAnexo> lstCob = new ArrayList<ExplotacionCoberturaAnexo>();
		try {
			
			Poliza plz = getPoliza(idPoliza);
			Set<ModuloPoliza> modsPoliza = plz.getModuloPolizas();

			List<String> modulosP = new ArrayList<String>();
			for (ModuloPoliza modP:modsPoliza) {
				if (!modulosP.contains(modP.getId().getCodmodulo()))
					modulosP.add(modP.getId().getCodmodulo());				
			}

			// añadimos los grupos de raza de BBDD a la explotacion
			
			try {
				cargarRestoGruposRazasAnexo(expAnexo,anexo.getExplotacionAnexos()); // cargarRestoGruposRazasAnexo(exp,plz.getExplotacions());
			} catch (Exception e) {
				logger.error(" Error al añadir los grupos de raza de BBDD a la explotacion de la poliza: "+idPoliza, e);
			}
			// Por cada modulo llamamos al SW
			for (String modP:modulosP) {				
				String xml = WSUtils.generateXMLPolizaModulosCoberturas(plz,null,expAnexo,null,modP,polizaDao);
							
				// guardar llamada al WS en BBDD 
				String idExpAnexo = (expAnexo.getId() !=null?expAnexo.getId().toString():"");
				Long idEnvio = guardarXmlEnvioAnexo(anexo.getId(),idExpAnexo, modP, xml,codUsuario);
				logger.debug("end - generateAndSaveXMLPolizaCpl");
				
				ModulosCoberturasResponse response = contratacionAyudasHelper.doModulosCoberturas(xml, realPath);
						
				if(null!=response){				
					xmlData = com.rsi.agp.core.util.WSUtils.getStringResponse(response.getModulosCoberturas());					
				}
				
				
				// guardar respuesta al WS en BBDD 
				if (xmlData != null) {
					polizaDao.actualizaXmlCoberturasAnexo(idEnvio, "",xmlData);
				}
				
				logger.debug("Respuesta WS: "+xmlData);
				// Transformarmos las coberturas que nos vienen del WS en una lista de explotacionesCoberturas
				es.agroseguro.modulosYCoberturas.ModulosYCoberturas modYCob =  datosExplotacionesManager.getMyCFromXml(xmlData);
				if (modYCob.getExplotaciones() != null) {
					es.agroseguro.modulosYCoberturas.Explotaciones explotaciones = modYCob.getExplotaciones();
					es.agroseguro.modulosYCoberturas.Explotacion  [] explotacion = explotaciones.getExplotacionArray();
					Long  contador = new Long(2000000);
					Long  nuevas = new Long(1);
					for (es.agroseguro.modulosYCoberturas.Explotacion expo:explotacion) {
						es.agroseguro.modulosYCoberturas.Cobertura [] cobb = expo.getCoberturaArray();
						if (cobb.length>0) {
							List<es.agroseguro.modulosYCoberturas.Cobertura> lstCobReg = Arrays.asList(cobb);
							for (es.agroseguro.modulosYCoberturas.Cobertura cobertura:lstCobReg) {
								ExplotacionCoberturaAnexo expCob = new ExplotacionCoberturaAnexo();
								// CARGA COBERTURAS
								expCob.setId(contador);
								expCob.setCodmodulo(modP);
								expCob.setFila(cobertura.getFila());
								expCob.setCpm(cobertura.getConceptoPrincipalModulo());
								expCob.setCpmDescripcion(cobertura.getDescripcionCPM());
								expCob.setRiesgoCubierto(cobertura.getRiesgoCubierto());
								expCob.setRcDescripcion(cobertura.getDescripcionRC());
								expCob.setElegible(cobertura.getElegible().toString().charAt(0));
								expCob.setTipoCobertura(cobertura.getTipoCobertura().charAt(0));
								if (cobertura.getVinculacionFilaArray() != null) {
									String vinculadas = "";
									es.agroseguro.modulosYCoberturas.VinculacionFila [] vinFila =  cobertura.getVinculacionFilaArray();
									for (es.agroseguro.modulosYCoberturas.VinculacionFila vFila:vinFila) {
										vinculadas += vFila.getElegida().toString().charAt(0);
										es.agroseguro.modulosYCoberturas.Fila [] fila = vFila.getFilaArray();
										for (es.agroseguro.modulosYCoberturas.Fila fi:fila) {
											vinculadas += "."+fi.getFila()+"."+fi.getElegida().toString().charAt(0)+"."+modP+".."+"|";
										}
									}
									if (!vinculadas.equals("")) {
										vinculadas=vinculadas.substring(0, vinculadas.length()-1);
									}
									expCob.setVinculada(vinculadas);
								}
								lstCob.add(expCob);
								contador++;
								nuevas++;
									
								// Comprobamos si la cobertura tiene datos variables. Si son de tipo E se guardan como tipo cobertura
								es.agroseguro.modulosYCoberturas.DatoVariable [] datArr = cobertura.getDatoVariableArray();
								if (datArr.length>0) {
									List<es.agroseguro.modulosYCoberturas.DatoVariable> lstDatVar = Arrays.asList(datArr);
									for (es.agroseguro.modulosYCoberturas.DatoVariable datVar:lstDatVar) {
										if (datVar.getTipoValor() != null && datVar.getTipoValor().equals("E")){ // recogemos los datosvariables con tipovalor a E
											// CARGA DATO VAR COMO COBERTURAS
											
											es.agroseguro.modulosYCoberturas.Valor [] valArr = datVar.getValorArray();
											List<es.agroseguro.modulosYCoberturas.Valor> lstValores = Arrays.asList(valArr);
											for (es.agroseguro.modulosYCoberturas.Valor valor:lstValores) {
												expCob = new ExplotacionCoberturaAnexo();
												expCob.setId(contador);
												expCob.setCodmodulo(modP);
												expCob.setFila(cobertura.getFila());
												expCob.setCpm(cobertura.getConceptoPrincipalModulo());
												expCob.setCpmDescripcion(cobertura.getDescripcionCPM());
												expCob.setRiesgoCubierto(cobertura.getRiesgoCubierto());
												expCob.setRcDescripcion(cobertura.getDescripcionRC());
												expCob.setElegible(cobertura.getElegible().toString().charAt(0));
												expCob.setTipoCobertura(cobertura.getTipoCobertura().charAt(0));
												expCob.setDvCodConcepto(new Long(datVar.getCodigoConcepto()));
												expCob.setDvDescripcion(datVar.getNombre());
												expCob.setDvValor(new Long(valor.getValor()));
												expCob.setDvValorDescripcion(valor.getDescripcion());
												expCob.setDvColumna(new Long(datVar.getColumna()));
												
												// coberturas datos variables	
												String vinculadas = "";
												if (valor.getVinculacionCelda() != null){
													es.agroseguro.modulosYCoberturas.VinculacionCelda vinCelda = valor.getVinculacionCelda();
													vinculadas += "X"+"."+vinCelda.getFilaMadre()+"."+"X"+"."+modP+"."+vinCelda.getColumnaMadre()+"."+vinCelda.getValorMadre();
												
//													if (!vinculadas.equals("")) {
//														vinculadas=vinculadas.substring(0, vinculadas.length()-1);
//													}
													expCob.setVinculada(vinculadas);
												}
												// fin coberturas datos variables
												
												lstCob.add(expCob);
												contador++;
												nuevas++;		
											}
										}
									}
								}
								// FIN dato array
							}
						}else {
							logger.debug("el SW no devuelve coberturas de explotaciónAnexo.");
						}
					}
				}
				
				// comprobamos si alguna de las que hay ya estaban seleccionadas
				if (!cobExistentes.equals("")){
					boolean esDatoVar = false;
					String[] arrCobExistentes = cobExistentes.split(";");
					logger.debug("ARRAY COBERTURAS EXISTENTES: " + Arrays.toString(arrCobExistentes));
					for (int i = 0; i < arrCobExistentes.length; i++) {
						esDatoVar = false;
						String[] cobExist = arrCobExistentes[i].split("\\|");
						String[] cb_dv = arrCobExistentes[i].split("#");
						String[] dv = cb_dv[1].split("\\|");
						logger.debug("Cob. ya existente: id: "+cobExist[0]+ " mod: "+cobExist[1]+ " fila: "+cobExist[2]+" CPM: "+cobExist[3]+" descCPM: "+cobExist[4]+ 
								" RC: "+cobExist[5]+" desc RC: "+cobExist[6]+ " vinculada: "+cobExist[7]+ " elegible: "+cobExist[8]+ " tipoCobertura: "+cobExist[9] + " checked: "+cobExist[10].charAt(0));
						if (dv != null && dv.length >4 && dv[0] != null && !dv[0].equals("")){
							logger.debug(" DV: codCptoDV: "+dv[0]+ " desc DV: "+dv[1]+ " valorDV: "+dv[2]+" descValorDv: "+dv[3]+" elegido: "+dv[4]+" elegido: "+dv[5]);
							esDatoVar = true;
						}
						for (ExplotacionCoberturaAnexo cob:lstCob) {
								Integer fila = new Integer(cob.getFila());
								Integer cpm  = new Integer(cob.getCpm());
								Integer rc   = new Integer(cob.getRiesgoCubierto());
								logger.debug("id: "+ cob.getId()+ " fila: "+fila+ " cpm: "+cpm+ " rc: "+rc);
								// si modulo-fila-cpm-rg = igual q la q tengo, reviso si está checked
								// compruebo que no sea cob. de tipo dato variable
								if (cobExist[1].equals(cob.getCodmodulo()) &&  cobExist[2].equals(fila.toString()) &&  cobExist[3].equals(cpm.toString()) && cobExist[5].equals(rc.toString())) {
									if (cob.getDvCodConcepto() == null && !esDatoVar){ // es cobertura normal	
										cob.setId(Long.parseLong(cobExist[0]));
										if (cobExist[10].charAt(0) == 'S'){
											cob.setElegida('S');
											break;
										}else{
											cob.setElegida('N');
											break;
										}
									}else{
										if (cob.getDvCodConcepto() != null && esDatoVar){
											if (dv[0].equals(cob.getDvCodConcepto().toString()) && dv[2].equals(cob.getDvValor().toString())){
												cob.setId(Long.parseLong(cobExist[0]));
												if (cobExist[10].charAt(0) == 'S'){
													cob.setElegida('S');													
												}else{
													cob.setElegida('N');
													
												}
												if (dv[5].charAt(0) == 'S'){						
													cob.setDvElegido('S');
													break;
												}else{
													cob.setDvElegido('N');
													break;
												}
											}
										}
									}
								}							
						}	// fin lstCob
					}
				}
			}
			
			Collections.sort(lstCob, new ExplotacionCoberturaAnexoComparator());
			logger.info("End - DatosExplotacionesManager - getCoberturasElegiblesExpAnexo");
			return lstCob;			
		} catch (Exception e1) {
			logger.error(" Error al obtener las coberturas elegibles de la explotacion de la poliza: "+idPoliza, e1);
			return null;
		}
		
	}
	
	/**
	 * Unifica en un set todos los grupos de raza de la explotacionAnexo
	 * @param exp explotacion de Anexo
	 * @param setExpBBDD set Explotacion Anexo
	 */
	private void cargarRestoGruposRazasAnexo(ExplotacionAnexo exp, Set<ExplotacionAnexo> setExpBBDD) {
		logger.debug("Inicio - DatosExplotacionesAnexoManager - cargarRestoGruposRazasAnexo");
		GrupoRazaAnexo grInicial = exp.getGrupoRazaAnexos().iterator().next();
		Set<GrupoRazaAnexo> setExp = exp.getGrupoRazaAnexos();
		if (setExpBBDD != null && setExpBBDD.size() > 0 && exp.getId() != null) {
			for (ExplotacionAnexo exBBDD : setExpBBDD) {
				if (exBBDD.getId().toString().equals(exp.getId().toString())) {
					Set<GrupoRazaAnexo> setGrBBDD = exBBDD.getGrupoRazaAnexos();
					for (GrupoRazaAnexo grBBDD : setGrBBDD) {
						if (grBBDD.getCodgruporaza().toString().equals(grInicial.getCodgruporaza().toString())
								&& grBBDD.getCodtipocapital().toString()
										.equals(grInicial.getCodtipocapital().toString())
								&& grBBDD.getCodtipoanimal().toString()
										.equals(grInicial.getCodtipoanimal().toString())) {
							logger.debug(
									"grupoRazaAnexo: " + grBBDD.getCodgruporaza() + "-" + grInicial.getCodtipocapital()
											+ "-" + grInicial.getCodtipoanimal() + " ya introducido");
						} else {
							logger.debug("grupoRazaAnexo: " + grBBDD.getCodgruporaza() + "-"
									+ grBBDD.getCodtipocapital() + "-" + grBBDD.getCodtipoanimal() + " nuevo, inserto");
							GrupoRazaAnexo gr = new GrupoRazaAnexo();
							gr.setCodgruporaza(grBBDD.getCodgruporaza());
							gr.setCodtipoanimal(grBBDD.getCodtipoanimal());
							gr.setCodtipocapital(grBBDD.getCodtipocapital());
							gr.setNumanimales(grBBDD.getNumanimales());
							// FALTA METER LOS DATOS VARIABLES
							Set<DatosVarExplotacionAnexo> setDVOrigen = new HashSet<DatosVarExplotacionAnexo>();
							Set<DatosVarExplotacionAnexo> setDVBBDD = grBBDD.getDatosVarExplotacionAnexos();
							for (DatosVarExplotacionAnexo dtBBDD : setDVBBDD) {
								DatosVarExplotacionAnexo dv = new DatosVarExplotacionAnexo(null, gr, null, null,
										dtBBDD.getCodconcepto(), dtBBDD.getValor());
								setDVOrigen.add(dv);
								logger.debug("DV de explotacionAnexo " + grBBDD.getCodgruporaza() + "-"
										+ grBBDD.getCodtipocapital() + "-" + grBBDD.getCodtipoanimal() + " cargado: "
										+ dv.getCodconcepto() + " = " + dv.getValor());
							}
							gr.setDatosVarExplotacionAnexos(setDVOrigen);
							setExp.add(gr);
						}
					}
					exp.setGrupoRazaAnexos(setExp);
				}
			}

		}
		logger.debug("End - DatosExplotacionesAnexoManager - cargarRestoGruposRazasAnexo");
	}
	
	private Long guardarXmlEnvioAnexo(Long idAnexo, String idExpAnexo, String codmodulo,String envio, String codUsuario) throws DAOException {
		logger.debug("init - guardarXmlEnvioAnexo");
		SWModulosCobExplotacionAnexo doc = new SWModulosCobExplotacionAnexo();
		doc.setIdanexo(idAnexo);
		doc.setFecha((new GregorianCalendar()).getTime());
		if (!idExpAnexo.equals(""))
			doc.setIdExplotAnexo(Long.parseLong(idExpAnexo));
		doc.setEnvio(Hibernate.createClob(envio));
		doc.setRespuesta(Hibernate.createClob(" "));
		doc.setCodmodulo(codmodulo);
		doc.setUsuario(codUsuario);
		
		SWModulosCobExplotacionAnexo newEnvio = (SWModulosCobExplotacionAnexo) this.datosExplotacionAnexoDao.saveEnvioCobExplotacion(doc);
		this.polizaDao.actualizaXmlCoberturas(newEnvio.getId(), envio, "");
		logger.debug("end - guardarXmlEnvioAnexo");
		return newEnvio.getId();
	}
	
	//SETTERs Spring
	public void setDatosExplotacionesManager(DatosExplotacionesManager datosExplotacionesManager) {
		this.datosExplotacionesManager = datosExplotacionesManager;
	}
	
	public void setDatosExplotacionAnexoDao(IDatosExplotacionAnexoDao datosExplotacionAnexoDao) {
		this.datosExplotacionAnexoDao = datosExplotacionAnexoDao;
	}

	public void setPrecioGanadoDao(IPrecioGanadoDao precioGanadoDao) {
		this.precioGanadoDao = precioGanadoDao;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}
	
	
}