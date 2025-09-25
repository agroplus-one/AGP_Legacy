package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.cpl.SubvencionesGrupoFiltro;
import com.rsi.agp.dao.filters.poliza.SubvencionEnesaPolizaFiltro;
import com.rsi.agp.dao.models.admin.IAseguradoDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Socio;
import com.rsi.agp.dao.tables.cgen.TipoSubvencionEnesaView;
import com.rsi.agp.dao.tables.cpl.SubvencionEnesa;
import com.rsi.agp.dao.tables.cpl.SubvencionesGrupo;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizaSocio;
import com.rsi.agp.dao.tables.poliza.SubvencionSocio;
import com.rsi.agp.dao.tables.poliza.SubvencionSocioGanado;

public class SocioSubvencionManager extends SocioManager implements IManager 
{
	private static final Log LOGGER = LogFactory.getLog(SocioSubvencionManager.class);
	private IAseguradoDao aseguradoDao;
	
	/**
	 * Método para obtener los socios de un asegurado para una póliza en concreto
	 * @param asegurado
	 * @param poliza
	 * @return
	 * @throws BusinessException
	 */
	public final Set<Socio> getSociosByAsegPoliza(Asegurado asegurado, Poliza poliza) throws BusinessException 
	{
		try {
			return socioDao.getSociosByAseguradoPoliza(asegurado, poliza);
		} catch (DAOException dao) {
			LOGGER.error("Se ha producido un error al acceder a base de datos ", dao);
			throw new BusinessException("Se ha producido un error al acceder a base de datos ", dao);
		}		
	}
	
	public final Set<Socio> getSociosByPolizaConSubvenciones(Poliza poliza) throws BusinessException {
		try {
			return socioDao.getSociosByPolizaConSubvenciones(poliza);
		} catch (DAOException dao) {
			LOGGER.error("Se ha producido un error al acceder a base de datos ", dao);
			throw new BusinessException("Se ha producido un error al acceder a base de datos ", dao);
		}
		
	}
	
	
	/**
	 * Método que actualiza los socios que deben aparecer en la póliza actual
	 * @param poliza
	 * @param aseguradoSesion
	 * @return
	 * @throws BusinessException
	 */
	public Integer actualizaSociosPoliza(Poliza poliza,	Asegurado aseguradoSesion) throws BusinessException {
		Integer resultado = 0;
		
		try {
			List<Socio> listSociosPoliza = socioDao.getSociosActivosByAsegurado(aseguradoSesion);
			if ((listSociosPoliza != null) && (listSociosPoliza.size() > 0)){
				for (Socio socio:listSociosPoliza){
					PolizaSocio socioPoliza = new PolizaSocio();
					socioPoliza.setPoliza(poliza);
					socioPoliza.setSocio(socio);
					socioDao.saveOrUpdate(socioPoliza);
				}
				
				// Llama al pl de actualización de orden de socios para esta póliza
				socioDao.actualizaOrdenPolizaSocio(poliza.getIdpoliza());
				
				resultado = listSociosPoliza.size();
			}
			
		} catch (DAOException dao) {
			LOGGER.error("Se ha producido un error al acceder a base de datos ", dao);
			throw new BusinessException("Se ha producido un error al acceder a base de datos ", dao);
		}
		
		return resultado;
	}
	
	
	
	/**
	 * Método para obtener la lista de todas las subvenciones disponibles en función del socio
	 * 
	 * @param socioBean Socio para el que se desea obtener la lista de subvenciones
	 * @return Listado de Subvenciones con información acerca de si se debe seleccionar o no en la pantalla y de si es seleccionable o no.
	 */
	@SuppressWarnings("unchecked")
	public final List<TipoSubvencionEnesaView> getSubvenciones(Socio socioBean, Poliza poliza){
		//Obtenemos las subvenciones
		String codmodulo = "";
		for (ModuloPoliza mp : poliza.getModuloPolizas()){
			codmodulo += mp.getId().getCodmodulo() + ";";
		}
		codmodulo += "99999;";
		SubvencionEnesaPolizaFiltro filtro = new SubvencionEnesaPolizaFiltro(
				poliza.getLinea().getLineaseguroid(), "1", socioBean.getTipoidentificacion(), codmodulo, false, null!=poliza.getFechaenvio()?poliza.getFechaenvio():new Date());
		List<SubvencionEnesa> subvenciones = this.socioDao.getObjects(filtro);
		
		//variable para llevar el control de los codtiposubvenesa ya añadidos a la lista de subvenciones
		List<BigDecimal> codigosAniadidos = new ArrayList<BigDecimal>();
		
		//Tratamiento de las subvenciones para obtener la lista deseada: No añadir las subvenciones repetidas
		List<TipoSubvencionEnesaView> subvencionesEnesaPantalla = new ArrayList<TipoSubvencionEnesaView>();
		for (SubvencionEnesa subv : subvenciones){
			TipoSubvencionEnesaView tenDef = new TipoSubvencionEnesaView();
			tenDef.setSubvEnesa(subv);
			
			//compruebo si ya la hemos añadido a la lista de subvenciones
			if (codigosAniadidos.contains(subv.getTipoSubvencionEnesa().getCodtiposubvenesa())){
				int indice = -1;
				for (int i = 0; i < subvencionesEnesaPantalla.size(); i++){
					if (subvencionesEnesaPantalla.get(i).getSubvEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa().equals(subv.getTipoSubvencionEnesa().getCodtiposubvenesa())){
						indice = i;
						break;
					}
				}
				// comprobamos cual es la que tiene mayor porcentaje de subvención para quedarnos con esa
				if (indice >= 0 && subvencionesEnesaPantalla.get(indice).getSubvEnesa().getPctsubvindividual().compareTo(subv.getPctsubvindividual()) <= 0){
					//borramos la subvención anterior e insertamos la nueva
					subvencionesEnesaPantalla.remove(indice);
					subvencionesEnesaPantalla.add(tenDef);
				}
			}
			else{
				subvencionesEnesaPantalla.add(tenDef);
				codigosAniadidos.add(subv.getTipoSubvencionEnesa().getCodtiposubvenesa());
			}
			
			/*String jovenAG = "";
			if (socioBean.getJovenagricultor() != null)
				jovenAG = socioBean.getJovenagricultor().toString();
			
			if (!jovenAG.equalsIgnoreCase("") && (jovenAG.equalsIgnoreCase("H") || jovenAG.equalsIgnoreCase("M"))){
				if (subv.getTipoSubvencionEnesa().getCodtiposubvenesa().equals(new BigDecimal(10)) 
						|| subv.getTipoSubvencionEnesa().getCodtiposubvenesa().equals(new BigDecimal(11))){
					tenDef.setMarcada(true);
				}
			}
			else {
				if (jovenAG.equalsIgnoreCase("")){
					if (subv.getTipoSubvencionEnesa().getCodtiposubvenesa().equals(new BigDecimal(10)) 
							|| subv.getTipoSubvencionEnesa().getCodtiposubvenesa().equals(new BigDecimal(11))){
						tenDef.setNoEdit(true);
					}
				}
			}*/
		}
		getGruposSubv(subvencionesEnesaPantalla,poliza);
		if (socioBean.getTipoidentificacion().equals("NIF")){
			//Una vez obtenidas todas las subvenciones, obtenemos de 'Subv_Grupos' las subvenciones para el grupo 1
			//y eliminamos de la lista 'subvencionesEnesaPantalla' las que NO estén en el resultado de la consulta.
			return filtraSubvencionesGrupo(poliza, subvencionesEnesaPantalla);
		}
		else
			return subvencionesEnesaPantalla;
	}

	/**
	 * Método para obtener los códigos de subvención que aplican a un determinado plan y grupo
	 * @param poliza Póliza para la que queremos las subvenciones
	 * @param subvencionesEnesaPantalla Lista de subvenciones a tratar
	 * @return Subvenciones de la lista que cumplen  que están en el grupo deseado.
	 */
	@SuppressWarnings("unchecked")
	private List<TipoSubvencionEnesaView> filtraSubvencionesGrupo(
			Poliza poliza,
			List<TipoSubvencionEnesaView> subvencionesEnesaPantalla) {
		SubvencionesGrupoFiltro filtroGrupo = new SubvencionesGrupoFiltro(poliza.getLinea().getCodplan(), new BigDecimal(1));
		List<BigDecimal> subvsGrupo = this.socioDao.getObjects(filtroGrupo);
		//recorremos las subvenciones para "hacer limpieza"
		List<TipoSubvencionEnesaView> subvsfiltrogrupo = new ArrayList<TipoSubvencionEnesaView>();
		for (TipoSubvencionEnesaView tsev: subvencionesEnesaPantalla){
			if (subvsGrupo.contains(tsev.getSubvEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa())){
				subvsfiltrogrupo.add(tsev);
			}
		}
		return subvsfiltrogrupo;
	}
	
	
	/**
	 * Método para obtener la lista de todas las subvenciones atendiento a los códigos indicados como parámetro. 
	 * Utilizaremos este método cuando actualicemos las subvenciones de un socio: se recibirán los códigos de las
	 * subvenciones seleccionadas para el socio y se obtendrán los objetos "SubvencionEnesa" asociados a los mismos.
	 * 
	 * @param socioBean Socio para el que se desea obtener la lista de subvenciones
	 * @param poliza Póliza para la que estamos obteniendo las subvenciones
	 * @param codSubvsSeleccionadas Códigos de subvención a buscar en la base de datos
	 * @return Listado de Subvenciones con información acerca de si se debe seleccionar o no en la pantalla y de si es seleccionable o no.
	 */
	@SuppressWarnings("unchecked")
	public final List<SubvencionEnesa> getSubvencionesInsertar(Socio socioBean, Poliza poliza, String codSubvsSeleccionadas){
		//Obtenemos las subvenciones
		String codmodulo = "";
		for (ModuloPoliza mp : poliza.getModuloPolizas()){
			codmodulo += mp.getId().getCodmodulo() + ";";
		}
		codmodulo += "99999;";
		SubvencionEnesaPolizaFiltro filtro = new SubvencionEnesaPolizaFiltro(
				poliza.getLinea().getLineaseguroid(), "1", socioBean.getTipoidentificacion(), codmodulo, false, null!=poliza.getFechaenvio()?poliza.getFechaenvio():new Date());
		
		filtro.setCodSubvenciones(codSubvsSeleccionadas);
		
		List<SubvencionEnesa> subvenciones = this.socioDao.getObjects(filtro);
		
		return subvenciones;
	}

	/**
	 * Método para dar de alta las subvenciones de socios. Este métod se utilizará tanto para dar de alta como para
	 * modificar las subvenciones de los socios
	 * @param socioBean Socio del que se desea modificar las subvenciones
	 * @param poliza Póliza para la que se insertan/modifican las subvenciones
	 * @param subsSeleccionadas Subvenciones seleccionadas para asignar al socio
	 * @return Posibles errores.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> altaSubvenciones(Socio socioBean, Poliza poliza, List<SubvencionEnesa> subvSeleccionadas)
	{
		ArrayList<String> error = new ArrayList<String>();
		try {
			List<SubvencionSocio> subvencionesSocio = socioDao.getObjects(SubvencionSocio.class, "socio", socioBean);
			
			//Primero se eliminan las subvenciones que tuviera el socio anteriormente para esta póliza
			for (SubvencionSocio ss : subvencionesSocio){
				if (ss.getPoliza().getIdpoliza().compareTo(poliza.getIdpoliza()) == 0){
					socioBean.getSubvencionSocios().remove(ss);
					socioDao.delete(SubvencionSocio.class, ss.getId());
				}
			}
			
			Set<SubvencionSocio> newSubvenciones = new HashSet<SubvencionSocio>();
			if (subvSeleccionadas != null){
				//recorremos las subvenciones seleccionadas para darlas de alta en bbdd y en el objeto
				for (SubvencionEnesa subv : subvSeleccionadas)
				{
					//Procedemos a la insercion
					SubvencionSocio subvencionGrabar = new SubvencionSocio();
					subvencionGrabar.setPoliza(poliza);
					subvencionGrabar.setSocio(socioBean);
					subvencionGrabar.setSubvencionEnesa(subv);
					
					newSubvenciones.add(subvencionGrabar);
					
					LOGGER.debug("Guardamos la subvencion " + subv.getTipoSubvencionEnesa().getCodtiposubvenesa() + 
							" del socio " + socioBean.getId().getNif());
					socioDao.saveOrUpdate(subvencionGrabar);
				}
			}			
			socioBean.setSubvencionSocios(newSubvenciones);
		} 
		catch (Exception e) {
			error.add("Error al guardar las subvenciones del socio " + socioBean.getNombre() + socioBean.getApellido1());
			LOGGER.error("Error al guardar las subvenciones del socio " + socioBean.getNombre() + socioBean.getApellido1(), e);
		}
		
		return error;
	}
	
	/**
	 * Metodo con el que asignamos a nuestra Vista de Subv los grupos a los que pertenece cada subvencion
	 * @param resultadoOrdenado
	 * @param poliza
	 */
	private void getGruposSubv(List<TipoSubvencionEnesaView> resultadoOrdenado,Poliza poliza) {
		try {
//			Listado de grupos de subv para mi plan
			List<SubvencionesGrupo> subvgrupo= aseguradoDao.getGruposSubv(poliza.getLinea().getCodplan());
			
//			Asigno los grupos a mi listado de subv final
			for(TipoSubvencionEnesaView vista:resultadoOrdenado){
					for(SubvencionesGrupo grupo:subvgrupo){
						if(vista.getSubvEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa().equals(grupo.getId().getCodtiposubv())){
							vista.setCodgruposubvencion(grupo.getId().getGruposubv());
							vista.setDescgrupo(grupo.getGrupoSubvenciones().getDescripcion());
							break;
						}
					}
			}
			
		} catch (DAOException e) {
		}
	}
	
	/**
	 * Metodo que crea las tablas HTML para mostrarlas en la JSP
	 * @param listaSubvencionesAsegurado
	 * @return
	 */
	public String pintarTablaSubv(List<TipoSubvencionEnesaView> listaSubvencionesAsegurado) {
		TreeMap<BigDecimal,String> listadoGrupos = new TreeMap<BigDecimal, String>();
		HashMap<String, List<TipoSubvencionEnesaView>> gruposSubv = new HashMap<String, List<TipoSubvencionEnesaView>>();
		String resultado = "";
		
		listadoGrupos = getGruposSubv(listaSubvencionesAsegurado);
		gruposSubv = getListadoSubvGrupo(listadoGrupos, listaSubvencionesAsegurado);
				
		if(gruposSubv.size() > 0){
			for(BigDecimal grupo:listadoGrupos.keySet()){
				if(!grupo.equals(new BigDecimal(9999))){
					resultado+= "<fieldset style='width:70%' align='center'>";								
					resultado+="<legend class='literal'>" + grupo + "-" +listadoGrupos.get(grupo) +  "</legend>";
				}
				resultado+=	"<table align='center'>";
				int contador = 0;
				for(TipoSubvencionEnesaView obj:gruposSubv.get(grupo.toString())){
					LOGGER.debug("pintarTablaSubv " + obj.getSubvEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa() + ", " + obj.isMarcada());
					if(contador == 2){
						resultado += "</tr>";
						contador = 0;
					}
					if(contador == 0){
						resultado += "<tr>";
					}
					resultado +="<td class='literal'>" +
								"<input type='checkbox' "; 
					if(obj.isMarcada())
						resultado+="checked";
					if(obj.isNoEdit())
						resultado+="disabled";
					resultado+=" value='" + obj.getSubvEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa() + "' />";
					resultado+= obj.getSubvEnesa().getTipoSubvencionEnesa().getDestiposubvenesa()+ "("+obj.getSubvEnesa().getPctsubvindividual() +"%)";
					resultado+="</td>";
					contador++;
				}
				if(contador != 0)
					resultado+="</tr>";
				resultado +="</table>";
				if(!grupo.equals(new BigDecimal(9999))){
					resultado += "</fieldset>";
				}
			}
		}
		return resultado;
	}
	
	/**
	 * Metodo que crea un TreeMap con nuestro Grupos + Descripcion ordenados Ascendentemente
	 * @param listaSubvencionesAsegurado
	 * @return
	 */
	private TreeMap<BigDecimal,String> getGruposSubv(List<TipoSubvencionEnesaView> listaSubvencionesAsegurado){
		TreeMap<BigDecimal,String> listadoGrupos = new TreeMap<BigDecimal, String>();
		
		for(TipoSubvencionEnesaView vista :listaSubvencionesAsegurado){
			if(vista.getCodgruposubvencion() != null)
				listadoGrupos.put(vista.getCodgruposubvencion(), vista.getDescgrupo());
			else
				listadoGrupos.put(new BigDecimal(9999), "Sin Grupo");
		}
		return listadoGrupos;
	}
	
	/**
	 * Metodo que crea una HashMap con el listado de subvenciones agrupadas por clave grupo
	 * @param listadoGrupos
	 * @param listaSubvencionesAsegurado
	 * @return
	 */
	private HashMap<String, List<TipoSubvencionEnesaView>>  getListadoSubvGrupo (TreeMap<BigDecimal,String> listadoGrupos,List<TipoSubvencionEnesaView> listaSubvencionesAsegurado){
		HashMap<String, List<TipoSubvencionEnesaView>> gruposSubv = new HashMap<String, List<TipoSubvencionEnesaView>>();
		List<TipoSubvencionEnesaView> listaaux= null;
		
		for(BigDecimal grupo:listadoGrupos.keySet()){
			if(!grupo.equals(new BigDecimal(9999))){
				listaaux = new ArrayList<TipoSubvencionEnesaView>();
				for(TipoSubvencionEnesaView vista :listaSubvencionesAsegurado){
					if(null != vista.getCodgruposubvencion() && vista.getCodgruposubvencion().equals(grupo)){
						listaaux.add(vista);
					}
				}
				gruposSubv.put(grupo.toString(), listaaux);
			}else{
				listaaux = new ArrayList<TipoSubvencionEnesaView>();
				for(TipoSubvencionEnesaView vistaNull :listaSubvencionesAsegurado){
					if(vistaNull.getCodgruposubvencion() == null){
						listaaux.add(vistaNull);
					}
				}
				gruposSubv.put(grupo.toString(), listaaux);
			}
		}
		return gruposSubv;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Socio> getInformeSociosPoliza(Long idPoliza, Boolean esGanado) {
		Poliza poliza = this.getPolizaById(idPoliza);
		Long idAseg = poliza.getAsegurado().getId();
		List<Socio> lstSociosFinal = new ArrayList<Socio>();
		List<String> lstSociosNoSub = new ArrayList<String>();
		Asegurado asegurado = (Asegurado)this.getDatosAsegurado(idAseg);
		boolean tieneSubv = false;
		try {
			Set<Socio> lstSocios= this.getSociosByAsegPoliza(asegurado, poliza);
			for(Socio soc:lstSocios){
				if (soc.getSubvencionSocios() !=null) {
					//soc.setOrden(orden);
					//Set<SubvencionSocio> setSub = soc.getSubvencionSocios();
					Set<PolizaSocio> setPolSoc = soc.getPolizaSocios();
					Map<String,String> mapSocios = new HashMap<String,String>();
					
					if (esGanado) {
						for(PolizaSocio pSoc:setPolSoc){
							tieneSubv = false;
							// vemos que existe el socio y no est� dado de baja
							if (pSoc.getSocio()!= null &&((null == pSoc.getSocio().getBaja() || !pSoc.getSocio().getBaja().equals('S'))) && pSoc.getPoliza() != null && pSoc.getPoliza().getIdpoliza() == idPoliza) {
								// comprobamos las subvenciones del socio para ese idpoliza, idaseguardo y lineaseguroid
								Set<SubvencionSocioGanado> setSub = pSoc.getSocio().getSubvencionSocioGanados();
								LOGGER.debug("## socio:"+  pSoc.getSocio().getNombre() + " nif:" + pSoc.getSocio().getId().getNif()+ " ##");
								if (setSub != null && setSub.size()>0) {
									for(SubvencionSocioGanado sub:setSub){
										if (!mapSocios.containsKey(pSoc.getSocio().getId().getNif()+"-"+sub.getSubvencionEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa())) {
											if (sub.getPoliza().getIdpoliza() == idPoliza) {												
												tieneSubv = true;
												LOGGER.debug(" socio:"+  pSoc.getSocio().getNombre() +" nif:" + pSoc.getSocio().getId().getNif()+ " sub: " + sub.getSubvencionEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa()+" "+
														sub.getSubvencionEnesaGanado().getTipoSubvencionEnesa().getDestiposubvenesa());
												Socio socioNuevo = new Socio();
												socioNuevo.setNombre(pSoc.getSocio().getNombre()+ " "+pSoc.getSocio().getApellido1()+ " "+StringUtils.nullToString(pSoc.getSocio().getApellido2()));
												socioNuevo.setRazonsocial(pSoc.getSocio().getRazonsocial());
												socioNuevo.setTipoidentificacion(pSoc.getSocio().getTipoidentificacion());
												socioNuevo.getId().setNif(pSoc.getSocio().getId().getNif());
												socioNuevo.setNumsegsocial(StringUtils.nullToString(pSoc.getSocio().getNumsegsocial()));
												socioNuevo.setRegimensegsocial(pSoc.getSocio().getRegimensegsocial());
												socioNuevo.setDescripcionSubvencion(sub.getSubvencionEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa()+" - "+
													sub.getSubvencionEnesaGanado().getTipoSubvencionEnesa().getDestiposubvenesa());
												socioNuevo.setOrden(pSoc.getOrden().intValue());
												if (!lstSociosFinal.contains(socioNuevo)){
													lstSociosFinal.add(socioNuevo);
													mapSocios.put(pSoc.getSocio().getId().getNif()+"-"+sub.getSubvencionEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa(),"OK");
												}	
											}
										}
									}
								}
								if (!tieneSubv){
									if (!lstSociosNoSub.contains(pSoc.getSocio().getId().getNif())){
										LOGGER.debug("  socio:"+  pSoc.getSocio().getNombre() +" nif:" + pSoc.getSocio().getId().getNif()+ " SIN SUBVENCION ");
										Socio socioNuevo = new Socio();
										socioNuevo.setNombre(pSoc.getSocio().getNombre()+ " "+pSoc.getSocio().getApellido1()+ " "+StringUtils.nullToString(pSoc.getSocio().getApellido2()));
										socioNuevo.setRazonsocial(pSoc.getSocio().getRazonsocial());
										socioNuevo.setTipoidentificacion(pSoc.getSocio().getTipoidentificacion());
										socioNuevo.getId().setNif(pSoc.getSocio().getId().getNif());
										socioNuevo.setNumsegsocial(StringUtils.nullToString(pSoc.getSocio().getNumsegsocial()));
										socioNuevo.setRegimensegsocial(pSoc.getSocio().getRegimensegsocial());
										socioNuevo.setDescripcionSubvencion("");
										socioNuevo.setOrden(pSoc.getOrden().intValue());
										if (!lstSociosFinal.contains(socioNuevo)) {
											lstSociosFinal.add(socioNuevo);
										}
											
									}
								}
								
							}
						}
					}else {
						for(PolizaSocio pSoc:setPolSoc){
							tieneSubv = false;
							// vemos que existe el socio y no est� dado de baja
							if (pSoc.getSocio()!= null &&((null == pSoc.getSocio().getBaja() || !pSoc.getSocio().getBaja().equals('S'))) && pSoc.getPoliza() != null && pSoc.getPoliza().getIdpoliza() == idPoliza) {
								// comprobamos las subvenciones del socio para ese idpoliza, idaseguardo y lineaseguroid
								Set<SubvencionSocio> setSub = pSoc.getSocio().getSubvencionSocios();
								LOGGER.debug("## idSocio: " + pSoc.getSocio().getId() + " socio:"+  pSoc.getSocio().getNombre() + " nif:" + pSoc.getSocio().getId().getNif()+ " ##");
								if (setSub != null && setSub.size()>0) {
									for(SubvencionSocio sub:setSub){
										if (!mapSocios.containsKey(pSoc.getSocio().getId().getNif()+"-"+sub.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa())) {
											if (sub.getPoliza().getIdpoliza() == idPoliza) {
												tieneSubv = true;
												LOGGER.debug(" socio:"+  pSoc.getSocio().getNombre() +" nif:" + pSoc.getSocio().getId().getNif()+ " sub: " + sub.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa()+" "+
														sub.getSubvencionEnesa().getTipoSubvencionEnesa().getDestiposubvenesa());
												Socio socioNuevo = new Socio();
												socioNuevo.setNombre(pSoc.getSocio().getNombre()+ " "+pSoc.getSocio().getApellido1()+ " "+StringUtils.nullToString(pSoc.getSocio().getApellido2()));
												socioNuevo.setRazonsocial(pSoc.getSocio().getRazonsocial());
												socioNuevo.setTipoidentificacion(pSoc.getSocio().getTipoidentificacion());
												socioNuevo.getId().setNif(pSoc.getSocio().getId().getNif());
												socioNuevo.setNumsegsocial(StringUtils.nullToString(pSoc.getSocio().getNumsegsocial()));
												socioNuevo.setRegimensegsocial(pSoc.getSocio().getRegimensegsocial());
												socioNuevo.setDescripcionSubvencion(sub.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa()+" - "+
													sub.getSubvencionEnesa().getTipoSubvencionEnesa().getDestiposubvenesa());
												socioNuevo.setOrden(pSoc.getOrden().intValue());
											if (!lstSociosFinal.contains(socioNuevo))
												lstSociosFinal.add(socioNuevo);
												mapSocios.put(pSoc.getSocio().getId().getNif()+"-"+sub.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa(),"OK");
											}
										}
									}
								}
								if (!tieneSubv){
									if (!lstSociosNoSub.contains(pSoc.getSocio().getId().getNif())){
										LOGGER.debug("  socio:"+  pSoc.getSocio().getNombre() +" nif:" + pSoc.getSocio().getId().getNif()+ " SIN SUBVENCION ");
										Socio socioNuevo = new Socio();
										socioNuevo.setNombre(pSoc.getSocio().getNombre()+ " "+pSoc.getSocio().getApellido1()+ " "+StringUtils.nullToString(pSoc.getSocio().getApellido2()));
										socioNuevo.setRazonsocial(pSoc.getSocio().getRazonsocial());
										socioNuevo.setTipoidentificacion(pSoc.getSocio().getTipoidentificacion());
										socioNuevo.getId().setNif(pSoc.getSocio().getId().getNif());
										socioNuevo.setNumsegsocial(StringUtils.nullToString(pSoc.getSocio().getNumsegsocial()));
										socioNuevo.setRegimensegsocial(pSoc.getSocio().getRegimensegsocial());
										socioNuevo.setDescripcionSubvencion("");
										socioNuevo.setOrden(pSoc.getOrden().intValue());
										if (!lstSociosFinal.contains(socioNuevo))
											lstSociosFinal.add(socioNuevo);
									}
								}
								
							}
						}
					}
					/*
					if (setSub != null && setSub.size()>0) {
						for(SubvencionSocio sub:setSub){
							soc.setDescripcionSubvencion(sub.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa()+" "+
									sub.getSubvencionEnesa().getTipoSubvencionEnesa().getDestiposubvenesa());
							lstSociosFinal.add(soc);
						}
					}
					*/
				}else {
					lstSociosFinal.add(soc);
				}
			}
		} catch (BusinessException e) {		
			LOGGER.error("Error al recoger los socios de la p�liza " + idPoliza, e);
		}
		Collections.sort(lstSociosFinal, new Comparator(){
			@Override
			public int compare(Object o1, Object o2) {
				Socio socio1 = (Socio)o1;
				Socio socio2 = (Socio)o2;
				int orden1 = socio1.getOrden();
				int orden2 = socio2.getOrden();

				if (orden1>orden2)
					return 1;
				else if (orden1<orden2)
					return -1;
				else return 0;
			}
        });
		
		
		return lstSociosFinal;
	}
	
	public Socio getSocioById (Socio socio)
	{
		Socio resultado = (Socio)socioDao.getObject(Socio.class, socio.getId());
		return resultado;
	}
	
	public final Poliza getPolizaById(final Long idPoliza) {
		return (Poliza) socioDao.getObject(Poliza.class, idPoliza);
	}

	public void setAseguradoDao(IAseguradoDao aseguradoDao) {
		this.aseguradoDao = aseguradoDao;
	}
	
}