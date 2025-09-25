package com.rsi.agp.core.managers.impl.ganado;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.managers.impl.SocioManager;
import com.rsi.agp.dao.filters.cpl.SubvencionesGrupoFiltro;
import com.rsi.agp.dao.filters.poliza.SubvencionEnesaGanPolizaFiltro;
import com.rsi.agp.dao.models.admin.IAseguradoDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Socio;
import com.rsi.agp.dao.tables.cgen.ganado.TipoSubvencionEnesaGanadoView;
import com.rsi.agp.dao.tables.cpl.SubvencionesGrupo;
import com.rsi.agp.dao.tables.cpl.gan.SubvencionEnesaGanado;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizaSocio;
import com.rsi.agp.dao.tables.poliza.SubvencionSocio;
import com.rsi.agp.dao.tables.poliza.SubvencionSocioGanado;

public class SocioSubvManager extends SocioManager implements IManager 
{
	private static final Log LOGGER = LogFactory.getLog(SocioSubvManager.class);
	private IAseguradoDao aseguradoDao;
	
	/**
	 * Metodo para obtener los socios de un asegurado para una poliza en concreto
	 * @param asegurado
	 * @param poliza
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
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
	 * Metodo que actualiza los socios que deben aparecer en la poliza actual
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
				
				// Llama al pl de actualizacion de orden de socios para esta poliza
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
	 * Metodo para obtener la lista de todas las subvenciones disponibles en funcion del socio
	 * 
	 * @param socioBean Socio para el que se desea obtener la lista de subvenciones
	 * @return Listado de Subvenciones con informacion acerca de si se debe seleccionar o no en la pantalla y de si es seleccionable o no.
	 */
	@SuppressWarnings("unchecked")
	public final List<TipoSubvencionEnesaGanadoView> getSubvenciones(Socio socioBean, Poliza poliza){
		//Obtenemos las subvenciones
		String codmodulo = "";
		for (ModuloPoliza mp : poliza.getModuloPolizas()){
			codmodulo += mp.getId().getCodmodulo() + ";";
		}
		codmodulo += "99999;";
		SubvencionEnesaGanPolizaFiltro filtro = new SubvencionEnesaGanPolizaFiltro(
				poliza.getLinea().getLineaseguroid(), "1", socioBean.getTipoidentificacion(), codmodulo, false, poliza.getCodigosTipoAnimalExplotaciones(), new Date());
		List<SubvencionEnesaGanado> subvenciones = this.socioDao.getObjects(filtro);
		
		//variable para llevar el control de los codtiposubvenesa ya añadidos a la lista de subvenciones
		List<BigDecimal> codigosAniadidos = new ArrayList<BigDecimal>();
		
		//Tratamiento de las subvenciones para obtener la lista deseada: No añadir las subvenciones repetidas
		List<TipoSubvencionEnesaGanadoView> subvencionesEnesaPantalla = new ArrayList<TipoSubvencionEnesaGanadoView>();
		for (SubvencionEnesaGanado subv : subvenciones){
			
			TipoSubvencionEnesaGanadoView tenDef = new TipoSubvencionEnesaGanadoView();
			tenDef.setSubvEnesaGanado(subv);
			
			//compruebo si ya la hemos añadido a la lista de subvenciones
			if (codigosAniadidos.contains(subv.getTipoSubvencionEnesa().getCodtiposubvenesa())){
				int indice = -1;
				for (int i = 0; i < subvencionesEnesaPantalla.size(); i++){
					if (subvencionesEnesaPantalla.get(i).getSubvEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa().equals(subv.getTipoSubvencionEnesa().getCodtiposubvenesa())){
						indice = i;
						break;
					}
				}
				// comprobamos cual es la que tiene mayor porcentaje de subvencion para quedarnos con esa
				if (indice >= 0 && subvencionesEnesaPantalla.get(indice).getSubvEnesaGanado().getPorcSubvSeguroInd().compareTo(subv.getPorcSubvSeguroInd()) <= 0){
					//borramos la subvencion anterior e insertamos la nueva
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
			//y eliminamos de la lista 'subvencionesEnesaPantalla' las que NO esten en el resultado de la consulta.
			return filtraSubvencionesGrupo(poliza, subvencionesEnesaPantalla);
		}
		else
			return subvencionesEnesaPantalla;
	}

	/**
	 * Metodo para obtener los codigos de subvencion que aplican a un determinado plan y grupo
	 * @param poliza Poliza para la que queremos las subvenciones
	 * @param subvencionesEnesaPantalla Lista de subvenciones a tratar
	 * @return Subvenciones de la lista que cumplen  que esten en el grupo deseado.
	 */
	private List<TipoSubvencionEnesaGanadoView> filtraSubvencionesGrupo(
			Poliza poliza,
			List<TipoSubvencionEnesaGanadoView> subvencionesEnesaPantalla) {
		SubvencionesGrupoFiltro filtroGrupo = new SubvencionesGrupoFiltro(poliza.getLinea().getCodplan(), new BigDecimal(1));
		List<BigDecimal> subvsGrupo = this.socioDao.getObjects(filtroGrupo);
		//recorremos las subvenciones para "hacer limpieza"
		List<TipoSubvencionEnesaGanadoView> subvsfiltrogrupo = new ArrayList<TipoSubvencionEnesaGanadoView>();
		for (TipoSubvencionEnesaGanadoView tsev: subvencionesEnesaPantalla){
			if (subvsGrupo.contains(tsev.getSubvEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa())){
				subvsfiltrogrupo.add(tsev);
			}
		}
		return subvsfiltrogrupo;
	}
	
	
	/**
	 * Metodo para obtener la lista de todas las subvenciones atendiento a los codigos indicados como parametro. 
	 * Utilizaremos este metodo cuando actualicemos las subvenciones de un socio: se recibiran los codigos de las
	 * subvenciones seleccionadas para el socio y se obtendran los objetos "SubvencionEnesa" asociados a los mismos.
	 * 
	 * @param socioBean Socio para el que se desea obtener la lista de subvenciones
	 * @param poliza Poliza para la que estamos obteniendo las subvenciones
	 * @param codSubvsSeleccionadas Codigos de subvencion a buscar en la base de datos
	 * @return Listado de Subvenciones con informacion acerca de si se debe seleccionar o no en la pantalla y de si es seleccionable o no.
	 */
	@SuppressWarnings("unchecked")
	public final List<SubvencionEnesaGanado> getSubvencionesInsertar(Socio socioBean, Poliza poliza, String codSubvsSeleccionadas){
		//Obtenemos las subvenciones
		String codmodulo = "";
		for (ModuloPoliza mp : poliza.getModuloPolizas()){
			codmodulo += mp.getId().getCodmodulo() + ";";
		}
		codmodulo += "99999;";
		SubvencionEnesaGanPolizaFiltro filtro = new SubvencionEnesaGanPolizaFiltro(
				poliza.getLinea().getLineaseguroid(), "1", socioBean.getTipoidentificacion(), codmodulo, false, poliza.getCodigosTipoAnimalExplotaciones(), new Date());
		
		filtro.setCodSubvenciones(codSubvsSeleccionadas);
		
		List<SubvencionEnesaGanado> subvenciones = this.socioDao.getObjects(filtro);
		
		return subvenciones;
	}

	/**
	 * Metodo para dar de alta las subvenciones de socios. Este metod se utilizara tanto para dar de alta como para
	 * modificar las subvenciones de los socios
	 * @param socioBean Socio del que se desea modificar las subvenciones
	 * @param poliza Poliza para la que se insertan/modifican las subvenciones
	 * @param subsSeleccionadas Subvenciones seleccionadas para asignar al socio
	 * @return Posibles errores.
	 */
	public ArrayList<String> altaSubvenciones(Socio socioBean, Poliza poliza, List<SubvencionEnesaGanado> subvSeleccionadas)
	{
		ArrayList<String> error = new ArrayList<String>();
		try {
			List<SubvencionSocioGanado> subvencionesSocio = socioDao.getObjects(SubvencionSocioGanado.class, "socio", socioBean);
			
			//Primero se eliminan las subvenciones que tuviera el socio anteriormente para esta poliza
			for (SubvencionSocioGanado ss : subvencionesSocio){
				if (ss.getPoliza().getIdpoliza().compareTo(poliza.getIdpoliza()) == 0){
					socioBean.getSubvencionSocioGanados().remove(ss);
					socioDao.delete(SubvencionSocioGanado.class, ss.getId());
				}
			}
			
			Set<SubvencionSocioGanado> newSubvenciones = new HashSet<SubvencionSocioGanado>();
			if (subvSeleccionadas != null){
				//recorremos las subvenciones seleccionadas para darlas de alta en bbdd y en el objeto
				for (SubvencionEnesaGanado subv : subvSeleccionadas)
				{
					
					//Procedemos a la insercion
					SubvencionSocioGanado subvencionGrabar = new SubvencionSocioGanado();
					subvencionGrabar.setPoliza(poliza);
					subvencionGrabar.setSocio(socioBean);
					subvencionGrabar.setSubvencionEnesaGanado(subv);
					
					newSubvenciones.add(subvencionGrabar);
					
					LOGGER.debug("Guardamos la subvencion " + subv.getTipoSubvencionEnesa().getCodtiposubvenesa() + 
							" del socio " + socioBean.getId().getNif());
					socioDao.saveOrUpdate(subvencionGrabar);
				}
			}			
			socioBean.setSubvencionSocioGanados(newSubvenciones);
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
	private void getGruposSubv(List<TipoSubvencionEnesaGanadoView> resultadoOrdenado,Poliza poliza) {
		try {
//			Listado de grupos de subv para mi plan
			List<SubvencionesGrupo> subvgrupo= aseguradoDao.getGruposSubv(poliza.getLinea().getCodplan());
			
//			Asigno los grupos a mi listado de subv final
			for(TipoSubvencionEnesaGanadoView vista:resultadoOrdenado){
					for(SubvencionesGrupo grupo:subvgrupo){
						if(vista.getSubvEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa().equals(grupo.getId().getCodtiposubv())){
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
	public String pintarTablaSubv(List<TipoSubvencionEnesaGanadoView> listaSubvencionesAsegurado) {
		TreeMap<BigDecimal,String> listadoGrupos = new TreeMap<BigDecimal, String>();
		HashMap<String, List<TipoSubvencionEnesaGanadoView>> gruposSubv = new HashMap<String, List<TipoSubvencionEnesaGanadoView>>();
		String resultado = "";
		
		listadoGrupos = getGruposDescSubv(listaSubvencionesAsegurado);
		gruposSubv = getListadoSubvGrupo(listadoGrupos, listaSubvencionesAsegurado);
				
		if(gruposSubv.size() > 0){
			for(BigDecimal grupo:listadoGrupos.keySet()){
				if(!grupo.equals(new BigDecimal(9999))){
					resultado+= "<fieldset style='width:70%' align='center'>";								
					resultado+="<legend class='literal'>" + grupo + "-" +listadoGrupos.get(grupo) +  "</legend>";
				}
				resultado+=	"<table align='center'>";
				int contador = 0;
				for(TipoSubvencionEnesaGanadoView obj:gruposSubv.get(grupo.toString())){
					LOGGER.debug("pintarTablaSubv " + obj.getSubvEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa() + ", " + obj.isMarcada());
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
					resultado+=" value='" + obj.getSubvEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa() + "' />";
					resultado+= obj.getSubvEnesaGanado().getTipoSubvencionEnesa().getDestiposubvenesa()+ "("+obj.getSubvEnesaGanado().getPorcSubvSeguroInd() +"%)";
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
	private TreeMap<BigDecimal,String> getGruposDescSubv(List<TipoSubvencionEnesaGanadoView> listaSubvencionesAsegurado){
		TreeMap<BigDecimal,String> listadoGrupos = new TreeMap<BigDecimal, String>();
		
		for(TipoSubvencionEnesaGanadoView vista :listaSubvencionesAsegurado){
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
	private HashMap<String, List<TipoSubvencionEnesaGanadoView>>  getListadoSubvGrupo (TreeMap<BigDecimal,String> listadoGrupos,List<TipoSubvencionEnesaGanadoView> listaSubvencionesAsegurado){
		HashMap<String, List<TipoSubvencionEnesaGanadoView>> gruposSubv = new HashMap<String, List<TipoSubvencionEnesaGanadoView>>();
		List<TipoSubvencionEnesaGanadoView> listaaux= null;
		
		for(BigDecimal grupo:listadoGrupos.keySet()){
			if(!grupo.equals(new BigDecimal(9999))){
				listaaux = new ArrayList<TipoSubvencionEnesaGanadoView>();
				for(TipoSubvencionEnesaGanadoView vista :listaSubvencionesAsegurado){
					if(null != vista.getCodgruposubvencion() && vista.getCodgruposubvencion().equals(grupo)){
						listaaux.add(vista);
					}
				}
				gruposSubv.put(grupo.toString(), listaaux);
			}else{
				listaaux = new ArrayList<TipoSubvencionEnesaGanadoView>();
				for(TipoSubvencionEnesaGanadoView vistaNull :listaSubvencionesAsegurado){
					if(vistaNull.getCodgruposubvencion() == null){
						listaaux.add(vistaNull);
					}
				}
				gruposSubv.put(grupo.toString(), listaaux);
			}
		}
		return gruposSubv;
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
