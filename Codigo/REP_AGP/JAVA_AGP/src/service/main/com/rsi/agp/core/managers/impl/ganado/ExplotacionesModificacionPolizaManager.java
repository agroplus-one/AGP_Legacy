package com.rsi.agp.core.managers.impl.ganado;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlCalendar;
import org.apache.xmlbeans.XmlException;
import org.w3c.dom.Node;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.managers.impl.anexoMod.solicitud.ISolicitudModificacionManager;
import com.rsi.agp.core.report.anexoMod.RelacionEtiquetaTabla;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.anexo.ICuponDao;
import com.rsi.agp.dao.models.config.IDiccionarioDatosDao;
import com.rsi.agp.dao.models.poliza.IAnexoModificacionDao;
import com.rsi.agp.dao.models.poliza.ganado.ICargaExplotacionesDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.Cupon;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.commons.TerminoId;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.explotaciones.DatosVarExplotacionAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCobertura;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCoberturaAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRazaAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.PrecioAnimalesModuloAnexo;

import es.agroseguro.contratacion.explotacion.ExplotacionDocument;

public class ExplotacionesModificacionPolizaManager implements IManager {
	
	private static final Log logger = LogFactory.getLog(ExplotacionesModificacionPolizaManager.class);
	
	private ISolicitudModificacionManager solicitudModificacionManager;
	ICargaExplotacionesDao cargaExplotacionesDao;
	private ICuponDao cuponDao;
	private IAnexoModificacionDao anexoModificacionDao;
	private IDiccionarioDatosDao diccionarioDatosDao;
	
	
	/**
	 * Crea explotaciones de anexo conforme a los datos de la situaci�n actualizada de la p�liza
	 * @param idPoliza
	 * @param idAnexo
	 * @param idCupon
	 * @param lineaseguroid
	 * @throws BusinessException
	 */
	public void copiarExplotacionesAnexoFromPolizaActualizada(Long idPoliza, Long idAnexo, String idCupon, Long lineaseguroid) throws BusinessException{
		try {
			if(!existenExplotacionesEnAnexo(idAnexo)){
				//Obtengo el codigo de cup�n de la base de datos
				Cupon cupon = (Cupon) cuponDao.get(Cupon.class, Long.parseLong(idCupon));
				
				//Obtengo la poliza actualizada
				es.agroseguro.contratacion.Poliza poliza = ((es.agroseguro.contratacion.PolizaDocument) solicitudModificacionManager
						.getPolizaActualizadaFromCupon(cupon.getIdcupon())).getPoliza();
				
				// Mapa auxiliar con los codigos de concepto de los datos variables y sus etiquetas y tablas asociadas.
				Map<BigDecimal, RelacionEtiquetaTabla> auxEtiquetaTabla = 
						diccionarioDatosDao.getCodConceptoEtiquetaTablaExplotaciones(lineaseguroid);
							
				List<ExplotacionAnexo> listaExplotacionesAnexo = getListaExplotacionesAnexoFromPolizaActualizada(idPoliza, poliza, idAnexo, auxEtiquetaTabla);
				
				anexoModificacionDao.saveOrUpdateList(listaExplotacionesAnexo);
				
				//elimino las nuevas explotaciones de la sesi�n de hibernate
				for (ExplotacionAnexo ea : listaExplotacionesAnexo){
					anexoModificacionDao.evict(ea);
				}
			}
		}
		catch(DAOException dao){
			logger.error("Error al copiar las explotaciones al anexo", dao);
			throw new BusinessException ("Se ha producido un error al copiarExplotacionesFromPolizaActualizada", dao);
		} catch (XmlException e) {
			logger.error("Error al copiar las explotaciones al anexo", e);
			throw new BusinessException ("Se ha producido un error al copiarExplotacionesFromPolizaActualizada", e);
		}
	}
	
	/**
	 * M�todo que informa de si un anexo de modificaci�n tiene explotaciones o no
	 * @param idAnexoModificacion
	 * @return
	 * @throws BusinessException
	 */
	public boolean existenExplotacionesEnAnexo(Long idAnexoModificacion) throws BusinessException {
		boolean exist = false;
		
		try {
			exist = anexoModificacionDao.existenExplotacionesEnAnexo(idAnexoModificacion);
		} 
		catch (DAOException dao){
			logger.error("Excepcion : ExplotacionesModificacionPolizaManager - existenExplotacionesEnAnexo", dao);
			throw new BusinessException ("Se ha producido un error al consultar si existen explotaciones en un anexo", dao);
			
		}
		return exist;
	}


	/**
	 * M�todo para obtener una lista de explotaciones de anexo a partir de los datos de la situaci�n actualizada de agroseguro.
	 * @param idPoliza
	 * @param poliza
	 * @param idAnexo
	 * @param dvCodConceptoEtiqueta
	 * @return
	 * @throws XmlException
	 * @throws DAOException
	 */
	public List<ExplotacionAnexo> getListaExplotacionesAnexoFromPolizaActualizada(Long idPoliza, es.agroseguro.contratacion.Poliza poliza, 
			Long idAnexo, Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta) throws XmlException, DAOException{
		
		List<ExplotacionAnexo> listaExplotacionesAnexo = new ArrayList<ExplotacionAnexo>();
		AnexoModificacion anexo = (AnexoModificacion)anexoModificacionDao.get(AnexoModificacion.class, idAnexo);

		//Recorremos las explotaciones de la situaci�n actualizada y vamos creando las explotaciones del anexo
		Node currNode = poliza.getObjetosAsegurados().getDomNode().getFirstChild();
		
		while (currNode != null) {
			if (currNode.getNodeType() == Node.ELEMENT_NODE) {
				
				ExplotacionDocument xmlExplotacion = null;
				xmlExplotacion = ExplotacionDocument.Factory.parse(currNode);

				if (xmlExplotacion != null){
					ExplotacionAnexo explotacionAnexo = buildExplotacionAnexoFromExplotacionDocument(dvCodConceptoEtiqueta, anexo, xmlExplotacion, poliza.getCobertura().getDatosVariables());
					listaExplotacionesAnexo.add(explotacionAnexo);
				}
			}
			currNode = currNode.getNextSibling();
		}
		
		return listaExplotacionesAnexo;
	}

	
	/**
	 * M�todo para obtener los datos de una explotaci�n de la situaci�n actualizada de Agroseguro
	 * @param idPoliza
	 * @param poliza
	 * @param idAnexo
	 * @param dvCodConceptoEtiqueta
	 * @param numero
	 * @return
	 * @throws XmlException
	 * @throws DAOException
	 */
	public ExplotacionAnexo getExplotacionAnexoFromPolizaActualizada(Long idPoliza, es.agroseguro.contratacion.Poliza poliza, 
			Long idAnexo, Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta, Integer numero) throws XmlException, DAOException{
		
		AnexoModificacion anexo = (AnexoModificacion)anexoModificacionDao.get(AnexoModificacion.class, idAnexo);
		ExplotacionAnexo explotacionAnexo = null;

		//Recorremos las explotaciones de la situaci�n actualizada y vamos creando las explotaciones del anexo
		Node currNode = poliza.getObjetosAsegurados().getDomNode().getFirstChild();
		
		boolean encontrado = false;
		while (currNode != null && !encontrado) {
			if (currNode.getNodeType() == Node.ELEMENT_NODE) {
				
				ExplotacionDocument xmlExplotacion = null;
				xmlExplotacion = ExplotacionDocument.Factory.parse(currNode);

				if (xmlExplotacion != null){
					
					if(xmlExplotacion.getExplotacion().getNumero()==numero.intValue()){
						explotacionAnexo = buildExplotacionAnexoFromExplotacionDocument(dvCodConceptoEtiqueta, anexo, xmlExplotacion, poliza.getCobertura().getDatosVariables());
						encontrado = true;
					}
				}
			}
			currNode = currNode.getNextSibling();
		}
		
		return explotacionAnexo;
	}
	
	/**
	 * 
	 * @param dvCodConceptoEtiqueta
	 * @param anexo
	 * @param xmlExplotacionAnexo
	 * @param datosVariablesCob
	 * @return
	 */
	private ExplotacionAnexo buildExplotacionAnexoFromExplotacionDocument(Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta,
			AnexoModificacion anexo, es.agroseguro.contratacion.explotacion.ExplotacionDocument xmlExplotacionAnexo,
			es.agroseguro.contratacion.datosVariables.DatosVariables datosVariablesCob) {
			
		ExplotacionAnexo explotacionAnexo = null;
		
		if (xmlExplotacionAnexo != null && xmlExplotacionAnexo.getExplotacion() != null) {
			
			es.agroseguro.contratacion.explotacion.ExplotacionDocument.Explotacion explotacion = xmlExplotacionAnexo.getExplotacion();
			explotacionAnexo = new ExplotacionAnexo();
			explotacionAnexo.setAnexoModificacion(anexo);
			explotacionAnexo.setNumero(explotacion.getNumero());
			explotacionAnexo.setRega(explotacion.getRega());
			explotacionAnexo.setSigla(explotacion.getSigla());
			
			//Al ser un int devuelve un 0 aunque no exista, por lo que entenderemos el 0 como sin dato
			if(explotacion.getSubexplotacion()!=0){
				explotacionAnexo.setSubexplotacion(explotacion.getSubexplotacion());
			}
			
			explotacionAnexo.setEspecie(Long.valueOf(explotacion.getEspecie()));
			explotacionAnexo.setRegimen(Long.valueOf(explotacion.getRegimen()));
			
			Termino termino = new Termino();
			
			Character subtermino;
			if (!StringUtils.nullToString(explotacion.getUbicacion().getSubtermino()).equals("")) {
				subtermino = explotacion.getUbicacion().getSubtermino().charAt(0);
			}else{
				subtermino = ' ';
			}
			
			TerminoId id = new TerminoId(new BigDecimal(explotacion.getUbicacion().getProvincia()),
					new BigDecimal(explotacion.getUbicacion().getTermino()),
					subtermino,
					new BigDecimal(explotacion.getUbicacion().getComarca()));
			
			termino.setId(id);
			explotacionAnexo.setTermino(termino);
			
			if (explotacion.getCoordenadas() != null) {
				explotacionAnexo.setLatitud(explotacion.getCoordenadas().getLatitud());
				explotacionAnexo.setLongitud(explotacion.getCoordenadas().getLongitud());
			}
			
			//
			//A�adimos coberturas de la explotaci�n
			Set<ExplotacionCoberturaAnexo> coberturas=null;
			try {
				coberturas = explotacion.getDatosVariables() != null ? getExplotacionesCoberturas(explotacion,explotacionAnexo) : null;
			} catch (DAOException e) {
				logger.error(
						"Excepcion : ExplotacionesModificacionPolizaManager - buildExplotacionAnexoFromExplotacionDocument",
						e);
			}
			if(null!=coberturas){
				//explotacion.getExplotacionCoberturas().clear();//borramos el primer elemento de la colecci�n de inicializaci�n
				explotacionAnexo.getExplotacionCoberturasAnexo().addAll(coberturas);
			}
			
			
			//janv 18/05/2016   se cambia el tratamiento para completar los grupos de razas.
			es.agroseguro.contratacion.explotacion.GrupoRaza[] grupoRazaArr = explotacion.getGrupoRazaArray();
			Set<GrupoRazaAnexo> grupoRazas = new HashSet<GrupoRazaAnexo>(grupoRazaArr.length);			
			for (es.agroseguro.contratacion.explotacion.GrupoRaza grupoRazaXML : grupoRazaArr) {
				es.agroseguro.contratacion.explotacion.CapitalAsegurado capitalesAseg[]=grupoRazaXML.getCapitalAseguradoArray();
							
				for (es.agroseguro.contratacion.explotacion.CapitalAsegurado capitalAseguradoXML : capitalesAseg) {
					es.agroseguro.contratacion.explotacion.Animales precioAnimales[]=capitalAseguradoXML.getAnimalesArray();
				
					for (es.agroseguro.contratacion.explotacion.Animales animalXML : precioAnimales) {
						Set<DatosVarExplotacionAnexo> datosVariables = new HashSet<DatosVarExplotacionAnexo>();
						Set<PrecioAnimalesModuloAnexo> precioAnimalesModulos = new HashSet<PrecioAnimalesModuloAnexo>();
						//Datos del grupo de raza
						GrupoRazaAnexo grupoRaza = new GrupoRazaAnexo();
						grupoRaza.setCodgruporaza(Long.valueOf(grupoRazaXML.getGrupoRaza()));
						addDatosVariables(datosVariables, grupoRazaXML.getDatosVariables(), dvCodConceptoEtiqueta, grupoRaza);
						//Capital asegurado
						grupoRaza.setCodtipocapital(BigDecimal.valueOf(capitalAseguradoXML.getTipo()));
						addDatosVariables(datosVariables, capitalAseguradoXML.getDatosVariables(),
								dvCodConceptoEtiqueta, grupoRaza);
						
						// datos variables a nivel de explotacion
						addDatosVariables(datosVariables, explotacion.getDatosVariables(),
								dvCodConceptoEtiqueta, grupoRaza);
						
						
						//Precio animales m�dulo
						grupoRaza.setCodtipoanimal(Long.valueOf(animalXML.getTipo()));
						grupoRaza.setNumanimales(Long.valueOf(animalXML.getNumero()));					
						PrecioAnimalesModuloAnexo precioAnimalesModulo = new PrecioAnimalesModuloAnexo();
						precioAnimalesModulo.setCodmodulo(anexo.getCodmodulo());
						precioAnimalesModulo.setPrecio(animalXML.getPrecio());
						precioAnimalesModulo.setPrecioMin(animalXML.getPrecio());
						precioAnimalesModulo.setPrecioMax(animalXML.getPrecio());
						precioAnimalesModulo.setGrupoRazaAnexo(grupoRaza);
						precioAnimalesModulos.add(precioAnimalesModulo);
						addDatosVariables(datosVariables, animalXML.getDatosVariables(), dvCodConceptoEtiqueta, grupoRaza);						
						grupoRaza.setPrecioAnimalesModuloAnexos(precioAnimalesModulos);						
						grupoRaza.setDatosVarExplotacionAnexos(datosVariables);
						grupoRaza.setExplotacionAnexo(explotacionAnexo);
						grupoRazas.add(grupoRaza);
						
					}
				}				
			}
			
			explotacionAnexo.setGrupoRazaAnexos(grupoRazas);

		}
		return explotacionAnexo;
	}
	
	private Set<ExplotacionCoberturaAnexo> getExplotacionesCoberturas(
			es.agroseguro.contratacion.explotacion.ExplotacionDocument.Explotacion exp, ExplotacionAnexo explotacionAnexo) throws DAOException{		
		es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido riesgos[] = exp.getDatosVariables().getRiesgCbtoElegArray();
		
		Set<ExplotacionCoberturaAnexo> coberturas =null;
		if (null!=riesgos && riesgos.length>0){
			coberturas = new HashSet<ExplotacionCoberturaAnexo>();
			for (int i = 0; i < riesgos.length; i++) {
				ExplotacionCoberturaAnexo cob = new ExplotacionCoberturaAnexo();		
				
				cob.setRiesgoCubierto((short) riesgos[i].getCodRCub());
				cob.setCpm((short) riesgos[i].getCPMod());
				String cpmDescripcion=cargaExplotacionesDao.getDescripcionConceptoPpalMod(riesgos[i].getCPMod());
				cob.setCpmDescripcion(cpmDescripcion);
				cob.setElegida(riesgos[i].getValor().toCharArray()[0]);
				cob.setElegible(new Character('S'));
				cob.setCodmodulo(explotacionAnexo.getAnexoModificacion().getPoliza().getCodmodulo());
				short fila = cargaExplotacionesDao.getFilaExplotacionCobertura(
						explotacionAnexo.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid(), explotacionAnexo.getAnexoModificacion().getPoliza().getCodmodulo(), riesgos[i].getCPMod(), riesgos[i].getCodRCub());
				cob.setFila(fila);
				String rcDescripcion=cargaExplotacionesDao.getDescripcionRiesgoCubierto(explotacionAnexo.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid(), 
						explotacionAnexo.getAnexoModificacion().getPoliza().getCodmodulo(), riesgos[i].getCodRCub());
				cob.setRcDescripcion(rcDescripcion);
				cob.setExplotacionAnexo(explotacionAnexo);
				coberturas.add(cob);
		
				
				/*

				String rcDescripcion=cargaExplotacionesDao.getDescripcionRiesgoCubierto(lineaSeguroId, 
						modulo, cobertura.getRiesgoCubierto());
		
				if(null==rcDescripcion)rcDescripcion=new String("");
				cobertura.setRcDescripcion(rcDescripcion);
				cobertura.setExplotacion(explotacion);
				*/
			}
		}
		
		
		// calculo indemnizacion
		Poliza pol= anexoModificacionDao.getPolizaById(explotacionAnexo.getAnexoModificacion().getPoliza().getIdpoliza());
		Set<Explotacion> explotaciones = pol.getExplotacions();
		
		es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion calc[]= exp.getDatosVariables().getCalcIndemArray();
		boolean encontrada = false;
		for (int i = 0; i < calc.length; i++) {
			encontrada = false;
			for (Explotacion ex: explotaciones){
				if (!encontrada){
					Set<ExplotacionCobertura> cobExp = ex.getExplotacionCoberturas();
					if (cobExp != null && cobExp.size()>0){
						for (ExplotacionCobertura co: cobExp){
							if (co.getCpm() == calc[i].getCPMod() && co.getRiesgoCubierto() == calc[i].getCodRCub()){
								ExplotacionCoberturaAnexo cob = new ExplotacionCoberturaAnexo();									
								cob.setRiesgoCubierto(co.getRiesgoCubierto());
								cob.setCpm(co.getCpm());
								cob.setCpmDescripcion(co.getCpmDescripcion());
								cob.setElegida(co.getElegida());
								cob.setElegible(co.getElegible());
								cob.setCodmodulo(explotacionAnexo.getAnexoModificacion().getPoliza().getCodmodulo());
								cob.setFila(co.getFila());
								cob.setRcDescripcion(co.getRcDescripcion());
								cob.setTipoCobertura(co.getTipoCobertura());
								if (co.getDvCodConcepto() != null)
									cob.setDvCodConcepto(co.getDvCodConcepto());
								cob.setDvDescripcion(co.getDvDescripcion());
								if (co.getDvValor() != null){
									cob.setDvValor(co.getDvValor());
									if (calc[i].getValor() == co.getDvValor().intValue())
										cob.setDvElegido('S');
									else
										cob.setDvElegido('N');
								}
								cob.setDvValorDescripcion(co.getDvValorDescripcion());
								if (co.getDvColumna() != null)
									cob.setDvColumna(co.getDvColumna());							
								cob.setExplotacionAnexo(explotacionAnexo);
								coberturas.add(cob);
								encontrada = true;
							}
						}
					}
				}
			}
		
		}
		return coberturas;
	} 
	
	private static void addDatosVariables(
			final Set<DatosVarExplotacionAnexo> result,
			es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables,
			final Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta,
			final GrupoRazaAnexo grupoRaza) {
		
		if (datosVariables != null && dvCodConceptoEtiqueta != null	&& !dvCodConceptoEtiqueta.isEmpty()) {
			// 1. Recorrer las claves de auxEtiquetaTabla
			for (BigDecimal codconcepto : dvCodConceptoEtiqueta.keySet()) {
				try {
					// 2. Buscar en los datos variables del capital asegurado el
					// valor correspondiente
					// primero obtengo el objeto que representa al dato variable
					Class<?> clase = es.agroseguro.contratacion.datosVariables.DatosVariables.class;
					Method method = clase.getMethod("get"
							+ dvCodConceptoEtiqueta.get(codconcepto).getEtiqueta());
					
					Object objeto = method.invoke(datosVariables);
					
					if (objeto != null) {
						// despu�s obtengo el valor que tiene el objeto en el
						// dato variable.
						Class<?> claseValor = objeto.getClass();
						Method methodValor = claseValor.getMethod("getValor");
						Object valor = methodValor.invoke(objeto);
						// 3. asigno el valor al dato variable
						if (!StringUtils.nullToString(valor).equals("")) {
							DatosVarExplotacionAnexo datoVariable = new DatosVarExplotacionAnexo();
							datoVariable.setCodconcepto(codconcepto.intValue());
							if (valor instanceof XmlCalendar) {
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
								SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
								Date d = new Date();
								String fecha = "";
								try {
									d = sdf.parse(valor.toString());
									fecha = sdf2.format(d);
								} catch (ParseException e) {
									logger.error("Error al parsear la fecha en los datos variables", e);
								}
								datoVariable.setValor(fecha);
							} else {
								datoVariable.setValor(StringUtils.nullToString(valor));
							}
							if (grupoRaza != null) {
								datoVariable.setGrupoRazaAnexo(grupoRaza);
							}
							if (result != null) {
								result.add(datoVariable);
							}
						}
					}
				} catch (SecurityException e) {
					logger.debug("Error de seguridad " + e.getMessage());
				} catch (NoSuchMethodException e) {
					logger.debug("El m�todo no existe para esta clase "
							+ e.getMessage());
				} catch (IllegalArgumentException e) {
					logger.debug("El m�todo acepta los argumentos "
							+ e.getMessage());
				} catch (IllegalAccessException e) {
					logger.debug("Error " + e.getMessage());
				} catch (InvocationTargetException e) {
					logger.debug("Error " + e.getMessage());
				}
			}
		}
	}
	
	public void setSolicitudModificacionManager(ISolicitudModificacionManager solicitudModificacionManager) {
		this.solicitudModificacionManager = solicitudModificacionManager;
	}

	public void setCuponDao(ICuponDao cuponDao) {
		this.cuponDao = cuponDao;
	}
	
	public void setAnexoModificacionDao(IAnexoModificacionDao anexoModificacionDao) {
		this.anexoModificacionDao = anexoModificacionDao;
	}

	public void setDiccionarioDatosDao(IDiccionarioDatosDao diccionarioDatosDao) {
		this.diccionarioDatosDao = diccionarioDatosDao;
	}

	public ICargaExplotacionesDao getCargaExplotacionesDao() {
		return cargaExplotacionesDao;
	}

	public void setCargaExplotacionesDao(
			ICargaExplotacionesDao cargaExplotacionesDao) {
		this.cargaExplotacionesDao = cargaExplotacionesDao;
	}
}