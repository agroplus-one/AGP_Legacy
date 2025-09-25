/*
**************************************************************************************************
*
*  CREACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034  19/10/2010  Ernesto Laura		Controller para la realizacion del alta de subvenciones de     
*											asegurados. 
*											Version 2: realizada desde cero
*
 **************************************************************************************************
*/
package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.AseguradoManager;
import com.rsi.agp.core.managers.impl.AseguradoSubvencionManager;
import com.rsi.agp.core.managers.impl.SocioSubvencionManager;
import com.rsi.agp.core.managers.impl.ganado.SocioSubvManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Socio;
import com.rsi.agp.dao.tables.cgen.SubvencionesAseguradosView;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.SubvencionEnesa;
import com.rsi.agp.dao.tables.cpl.gan.SubvencionEnesaGanado;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.SubAseguradoCCAA;
import com.rsi.agp.dao.tables.poliza.SubAseguradoCCAAGanado;
import com.rsi.agp.dao.tables.poliza.SubAseguradoENESA;
import com.rsi.agp.dao.tables.poliza.SubAseguradoENESAGanado;
import com.rsi.agp.dao.tables.poliza.SubvencionSocio;
import com.rsi.agp.dao.tables.poliza.SubvencionSocioGanado;

import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.AseguradoDatosYMedidasDocument;
import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.ControlAccesoSubvenciones;
import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.Organismo;
import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.Subvencion;

public class AseguradoSubvencionController extends BaseSimpleController implements Controller {

	private AseguradoManager aseguradoManager;
	private AseguradoSubvencionManager aseguradoSubvencionManager;
	private SocioSubvencionManager socioSubvencionManager;
	private SocioSubvManager socioSubvManager;
	private static final Log        logger           = LogFactory.getLog(AseguradoSubvencionController.class);
	
	public AseguradoSubvencionController() {
		super();
		setCommandClass(Object.class);
		setCommandName("string");
	}

	@Override 
	protected final ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response, final Object object,
			final BindException exception) {
		
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		//Hay que recargar la poliza desde la BDD para que se nos actualicen las parcelas
		final Long idPoliza = Long.parseLong(request.getParameter("idpoliza"));
		Poliza poliza = socioSubvencionManager.getPolizaById(idPoliza);
		//TMR
		String modoLectura = StringUtils.nullToString(request.getParameter("modoLectura"));
		String vieneDeUtilidades =  StringUtils.nullToString(request.getParameter("vieneDeUtilidades"));
		Boolean esModoLectura =(modoLectura!=null && modoLectura.compareTo("modoLectura")==0);
		
		Asegurado aseguradoSesion ;
		if (usuario.getAsegurado()!= null && modoLectura.equals("")){
			aseguradoSesion = aseguradoManager.getAsegurado(usuario.getAsegurado().getId());
		}else{
			aseguradoSesion = poliza.getAsegurado();
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		aseguradoSesion.getSubAseguradoENESAs();
		List<Object> listaSubvencionesEnesaAseg;
		List<Object> listaSubvencionesCCAAAseg;
		ResourceBundle bundle = ResourceBundle.getBundle("agp");
		ModelAndView mv = null;
		parameters.put("AseguradoBean", aseguradoSesion);

		final String operacion = StringUtils.nullToString(request.getParameter("operacion"));
		
		parameters.put("modoLectura",modoLectura);
		parameters.put("vieneDeUtilidades",vieneDeUtilidades);
		if(poliza.getTipoReferencia().equals('C')){
			parameters.put("esCpl","true");
		}
		
		// En el caso de que desmarquemos las subvenciones que piden el popup.
		String idCheck = "";
		
		if ("volver".equalsIgnoreCase(operacion)){
			//ACTUALMENTE NO SE USA ESTA OPERACIÃ“N
			return new ModelAndView("redirect:/socioSubvencion.html","idpoliza",poliza.getIdpoliza());
		}
		else if ("continuar".equalsIgnoreCase(operacion) || "desmarcarSubvencion".equalsIgnoreCase(operacion))
		{
			
			ArrayList<String> erroresWeb = new ArrayList<String>();
			
			if (!esModoLectura) {
				//recogemos los codigos de subvencion seleccionados por el usuario
				String seleccionadas = StringUtils.nullToString(request.getParameter("subsSeleccionadas"));
				List<Object> subvEnesaSelec = null;
				List<Object> subvCCAASelec = null;
				if (!seleccionadas.equalsIgnoreCase("")){
					String[] subvsSelect = seleccionadas.split(",");
					//separamos las enesa y las ccaa
					String codenesa = "";
					String codccaa = "";
					for (String subv : subvsSelect){
						if (subv.indexOf("E") > -1){
							codenesa += subv.substring(0, subv.indexOf("/")) + ",";
						}
						else if (subv.indexOf("C") > -1){
							codccaa += subv.substring(0, subv.indexOf("/")) + ",";
						}
					}
					if (!codenesa.equals(""))
						subvEnesaSelec = aseguradoSubvencionManager.getSubvEnesaInsertar(aseguradoSesion, poliza, codenesa);
					
					if (!codccaa.equals(""))
						subvCCAASelec = aseguradoSubvencionManager.getSubvCCAAInsertar(aseguradoSesion, poliza, codccaa);
				}
				
				/* P0078846 ** MODIF TAM (15.02.2022) ** Inicio */		
				//Hay que recargar la poliza desde la BDD para que se nos actualicen las parcelas
				final String listaSubvAsegMarcadasInicialmente = request.getParameter("subsSeleccionadasAnt");
				
				ArrayList<String> errorSubvenciones = aseguradoSubvencionManager.altaSubvenciones(aseguradoSesion, poliza, subvEnesaSelec, 
													 subvCCAASelec, seleccionadas, usuario.getCodusuario(), listaSubvAsegMarcadasInicialmente);
				/* P0078846 ** MODIF TAM (15.02.2022) ** Fin */
				
				
				if (errorSubvenciones.size() > 0)
				{
					for (String mens : errorSubvenciones)
						erroresWeb.add(mens);
				}
			} 
			
			if (erroresWeb.size() > 0)
			{
				parameters.put("alerta2", erroresWeb);
				//en este caso hay que continuar para cargar las subvenciones de nuevo
			}
			else
			{
				parameters.put("operacion", "validar");
				parameters.put("idpoliza", poliza.getIdpoliza());
				
				
				// P20328 - Si la poliza es de linea ganadera se redirige a la pantalla de seleccion de comparativas
				// 	      - Si es agraria, se llama a los servicios de validacion y calculo
				if ("continuar".equalsIgnoreCase(operacion)) {
					if (poliza.getLinea().isLineaGanado()) {
						// Se indica al controlador de comparativas que no se esta redirigiendo desde la pantalla de importes
						parameters.put("vieneDeImportes", false);
						return new ModelAndView("redirect:/seleccionComparativasSW.html", parameters);
					}
					else {
						/* Pet. 63485 ** MODIF TAM (15.07.2020) ** Inicio */
						/* Por los desarrollos de esta peticion desde la ventana de subvenciones las 
						 * polizas de Agricola iran a la de comparativas
						 */
						parameters.put("vieneDeImportes", false);
						return new ModelAndView("redirect:/seleccionComparativasSW.html", parameters);
					}	
				}
				else {
					logger.debug("Init - Desmarcar Subvencion");
					idCheck = StringUtils.nullToString(request.getParameter("idCheck"));
					
					if (idCheck.equals("20/E") || idCheck.equals("73/E") || idCheck.equals("30/E")) {
						aseguradoSesion.setAtp("N");
					}
					else if(idCheck.equals("10/E") || idCheck.equals("11/E")) {
						aseguradoSesion.setJovenagricultor('N');
					}
				}
			}
		}
		
		//Accion por defecto, cargamos TODAS las subvenciones y las enviamos a la pagina para pintarlas
		//Subvenciones disponibles
		List<SubvencionesAseguradosView> listaSubvencionesAsegurado = aseguradoSubvencionManager.getSubvencionesAsegurado(aseguradoSesion, poliza, usuario, esModoLectura);
		parameters.put("listaSubvenciones", listaSubvencionesAsegurado);
		
		//Subvenciones Enesa ya dadas de alta para el asegurado
		listaSubvencionesEnesaAseg = aseguradoSubvencionManager.cargaSubvencionesEnesa(poliza, aseguradoSesion);
		//Subvenciones CCAA ya dadas de alta para el asegurado
		listaSubvencionesCCAAAseg = aseguradoSubvencionManager.cargaSubvencionesCCAA(poliza, aseguradoSesion);
		
		/* P00078846 ** MODIF TAM (14/02/2022) ** Inicio */
		
		/* 1º: Comprobamos si es primer acceso */
		boolean isPrimerAcceso = aseguradoSubvencionManager.isPrimerAcceso(poliza.getIdpoliza());
		
		StringBuilder listaSubvAsegMarcadasInicialmente = new StringBuilder();
		
		/* Si devuelve true es que es primerAcceso, y que contiene datos, y por lo tanto lanzamos llamada*/
		if (isPrimerAcceso) {
			/* Es primer acceso*/
			String nifCifAseg = poliza.getAsegurado().getNifcif();
			String codPlan = poliza.getLinea().getCodplan().toString();
			String codLinea = poliza.getLinea().getCodlinea().toString();
			String realPath = this.getServletContext().getRealPath("/WEB-INF/");
			String codUsuario = usuario.getCodusuario();
			AseguradoDatosYMedidasDocument xmlRespuesta = aseguradoSubvencionManager.getSubvencionesAsegurado(nifCifAseg, codPlan, codLinea, realPath, idPoliza, codUsuario);
			
			ControlAccesoSubvenciones ControlAccesoSubv = xmlRespuesta.getAseguradoDatosYMedidas().getDatosPersonales().getControlAccesoSubvenciones();
			
			if(ControlAccesoSubv!= null && ControlAccesoSubv.getOrganismoArray() != null){
				Organismo[] organismos = ControlAccesoSubv.getOrganismoArray();
				for (SubvencionesAseguradosView sav : listaSubvencionesAsegurado) {
					BigDecimal tipo = sav.getCodtiposubvencion();
					// SI ES LA SUBV 10 O LA SUBV 20, LA MARCAMOS EN FUNCION DE LA CARACTERISTICA DEL ASEGURADO
					boolean marcar = (Constants.SUBVENCION_JOVEN_HOMBRE.equals(tipo)
							&& Constants.CHARACTER_S.equals(aseguradoSesion.getJovenagricultor()))
							|| (Constants.SUBVENCION20.equals(tipo)
									&& (Constants.VALOR_SI.equals(aseguradoSesion.getAtp())
											|| Constants.CHARACTER_S.toString().equals(aseguradoSesion.getAtp())));
					if (marcar) {
						sav.setMarcada(true);
						listaSubvAsegMarcadasInicialmente.append(tipo);
						listaSubvAsegMarcadasInicialmente.append("/");
						listaSubvAsegMarcadasInicialmente.append(sav.getTipoSubvencion());
						listaSubvAsegMarcadasInicialmente.append(";");
					} else {
						for (Organismo org : organismos) {
							Subvencion[] subvenciones = org.getSubvencionArray();
							for (Subvencion subv : subvenciones) {							
								BigDecimal tipoSubv = BigDecimal.valueOf(subv.getTipo());								
								/* Si el tipo coincide, lo marcamos */
								if ((tipo).compareTo(tipoSubv) == 0) {
									sav.setMarcada(true);
									listaSubvAsegMarcadasInicialmente.append(tipoSubv);
									listaSubvAsegMarcadasInicialmente.append("/");
									listaSubvAsegMarcadasInicialmente.append(sav.getTipoSubvencion());
									listaSubvAsegMarcadasInicialmente.append(";");
								}
							}
						}
					}
				}				
			}
		} else {			
			//Si el asegurado ya tenia alguna subvencion, marco solo las que tenga.
			if (listaSubvencionesCCAAAseg.size() > 0 || listaSubvencionesEnesaAseg.size() > 0) {
				//Marcamos las que tiene el asegurado
				BigDecimal codTipoSubvEnesa;
				BigDecimal codTipoSubvCCAA;
				for (SubvencionesAseguradosView sav : listaSubvencionesAsegurado){
					// Marcamos las subvenciones enesa
					for (Object sae : listaSubvencionesEnesaAseg) {
						if (poliza.getLinea().isLineaGanado()) {
							codTipoSubvEnesa = ((SubAseguradoENESAGanado) sae)
									.getSubvencionEnesaGanado()
									.getTipoSubvencionEnesa().getCodtiposubvenesa();
						} else {
							codTipoSubvEnesa = ((SubAseguradoENESA) sae)
									.getSubvencionEnesa().getTipoSubvencionEnesa()
									.getCodtiposubvenesa();
						}
						if (sav.getTipoSubvencion().equals("E")
								&& !sav.isNoEdit()
								&& sav.getCodtiposubvencion().equals(
										codTipoSubvEnesa)) {
							if (!idCheck.isEmpty()) {
								String id = idCheck.substring(0, 2);
								if (!id.equals(sav.getCodtiposubvencion().toString())) {
									sav.setMarcada(true);
								}
							} else {
								sav.setMarcada(true);
							}
							listaSubvAsegMarcadasInicialmente.append(codTipoSubvEnesa);
							listaSubvAsegMarcadasInicialmente.append("/E;");
							break;
						}
					}	
					// Marcamos las subvenciones de la comunidad autonoma
					for (Object sac : listaSubvencionesCCAAAseg) {
						if (poliza.getLinea().isLineaGanado()) {
							codTipoSubvCCAA = ((SubAseguradoCCAAGanado) sac)
									.getSubvencionCCAAGanado()
									.getTipoSubvencionCCAA().getCodtiposubvccaa();
						} else {
							codTipoSubvCCAA = ((SubAseguradoCCAA) sac)
									.getSubvencionCCAA().getTipoSubvencionCCAA()
									.getCodtiposubvccaa();
						}
						if (sav.getTipoSubvencion().equals("C")
								&& !sav.isNoEdit()
								&& sav.getCodtiposubvencion().equals(
										codTipoSubvCCAA)) {
							sav.setMarcada(true);
							listaSubvAsegMarcadasInicialmente.append(codTipoSubvCCAA);
							listaSubvAsegMarcadasInicialmente.append("/C;");
							break;
						}
					}
				}
			}
		}
		
		parameters.put("subsSeleccionadasAnt", listaSubvAsegMarcadasInicialmente.toString());
		
		String tabla = aseguradoSubvencionManager.pintarTablaSubv(listaSubvencionesAsegurado, esModoLectura); 	
		
		if(!tabla.equals("")) {
			parameters.put("tabla", tabla);
			parameters.put("NoData", false);
		} else {
			parameters.put("NoData", true);
		}
		//comprobamos si el asegurado tiene nif o cif
		if ("CIF".equalsIgnoreCase(aseguradoSesion.getTipoidentificacion()))
			parameters.put("tieneCIF", "true");
		parameters.put("idpoliza", idPoliza);
		
		Integer numSocios = 0;
		
		try {
			//actualizar subvenciones de socios
			ArrayList<String> erroresWeb = new ArrayList<String>();
			
			//Si hay algun socio para la poliza no hace falta actualizar ya que se ha hecho desde la pantalla de administracion
			Set<Socio> sociosAseguradoPoliza = socioSubvencionManager.getSociosByAsegPoliza(aseguradoSesion, poliza);
			
			// Si hay algun socio se obtiene el numero de ellos
			if (sociosAseguradoPoliza != null
					&& !sociosAseguradoPoliza.isEmpty()) {
				numSocios = sociosAseguradoPoliza.size();
			} else if ((sociosAseguradoPoliza == null)
					|| (sociosAseguradoPoliza != null && sociosAseguradoPoliza
							.isEmpty())) {
				numSocios = this.actualizaSociosPoliza(poliza, aseguradoSesion);
			}	 		
	 		
			logger.debug("Numero de socios de la poliza: " + numSocios);
			if (numSocios > 0){
				if(poliza.getLinea().isLineaGanado()){
					erroresWeb=this.actualizaSubvSociosGanado(poliza, aseguradoSesion);
				}
				else{
					erroresWeb=this.actualizaSubvSocios(poliza, aseguradoSesion);
				}
	    	}
		
			if (erroresWeb.size() > 0)
			{
				parameters.put("alerta2", erroresWeb);
			}
		
		} catch (BusinessException e) {
			parameters.put("alerta", bundle.getString("mensaje.poliza.SubvencionesSocio.KO"));
		}	
		//Si se ha pulsado el boton de Subvenciones socios, muestra un mensaje si no tiene socios
		

		if (numSocios == 0){
			parameters.put("mensaje", bundle.getString("mensaje.asegurado.NoSocios"));
			parameters.put("muestraBotonSocios" , "false");
		}else{
			parameters.put("muestraBotonSocios" , "true");
		}
		if (poliza.getLinea().isLineaGanado()) {
			parameters.put("esGanado" , true);
		}
		mv = new ModelAndView("moduloPolizas/polizas/subvenciones/aseguradoSubvenciones", "subvencionesAsegurado", parameters);
		return mv;
	}

	public final void setAseguradoSubvencionManager(final AseguradoSubvencionManager aseguradoSubvencionManager) {
		this.aseguradoSubvencionManager = aseguradoSubvencionManager;
	}

	public void setSocioSubvencionManager(
			SocioSubvencionManager socioSubvencionManager) {
		this.socioSubvencionManager = socioSubvencionManager;
	}
	
	public void setSocioSubvManager(SocioSubvManager socioSubvManager) {
		this.socioSubvManager = socioSubvManager;
	}

	public Integer actualizaSociosPoliza(Poliza poliza, Asegurado aseguradoSesion) throws BusinessException{
		return socioSubvencionManager.actualizaSociosPoliza(poliza, aseguradoSesion);
	}
	
	public ArrayList<String> actualizaSubvSocios(Poliza poliza, Asegurado aseguradoSesion) throws BusinessException{
		logger.debug("init - actualizarSubvSocios");
		ArrayList<String> erroresWeb = new ArrayList<String>();
		//Recogemos los socios del asegurado
		Set<Socio> socios = socioSubvencionManager.getSociosByAsegPoliza(aseguradoSesion, poliza);
		List<SubvencionEnesa> subvSeleccionadas = null;
		Iterator<Socio> iter = socios.iterator();
		boolean subvencionDeclarada = false;
		String codSubvsSeleccionadas = "";
    	while (iter.hasNext()) {
    		Socio sc = (Socio) iter.next();
    		subvencionDeclarada = false;
    		Poliza p = null;
    		//Compruebo si el socio tiene subvenciones para la misma.
    		for (Poliza pol: sc.getAsegurado().getPolizas()){
    			if (pol.getIdpoliza().equals(poliza.getIdpoliza())){
    				p = pol;
    				break;
    			}
    		}
    		if (p != null && sc.getSubvencionSocios()!= null && sc.getSubvencionSocios().size() > 0)
    		{
    			logger.debug("El socio " + sc.getId().getNif() + " SI tiene subvenciones " + sc.getSubvencionSocios().size());
    			for (SubvencionSocio ss: sc.getSubvencionSocios()){
    				if (ss.getSubvencionEnesa().getId().getLineaseguroid().equals(p.getLinea().getLineaseguroid()) &&
    						ss.getPoliza().getIdpoliza().equals(p.getIdpoliza())){
    					subvencionDeclarada = true;
    					break;
    				}
    			}
    		}
    		if (!subvencionDeclarada){
    			logger.debug("El socio " + sc.getId().getNif() + " NO tiene subvenciones " + sc.getSubvencionSocios().size());
	    		if (sc.getAtp()!=null){
	    			if (sc.getNumsegsocial()!=null && sc.getRegimensegsocial() !=null && sc.getAtp().equals("SI")){
	    			codSubvsSeleccionadas = "20,";
	    			}
	    		}
	    		if (sc.getJovenagricultor()!=null){
		    		if (sc.getJovenagricultor().equals('H')){
		    			codSubvsSeleccionadas += "10,";
		    		}
		    		if (sc.getJovenagricultor().equals('M')){
		    			codSubvsSeleccionadas += "11,";
		    		}
	    		}
	    		if (!codSubvsSeleccionadas.equalsIgnoreCase("")){
	    			codSubvsSeleccionadas = codSubvsSeleccionadas.substring(0, codSubvsSeleccionadas.lastIndexOf(','));
	    		
	    		//Obtenemos las SubvencionesEnesa para los codigos recogidos
	    		subvSeleccionadas = socioSubvencionManager.getSubvencionesInsertar(sc, poliza, codSubvsSeleccionadas);
	    		}
	    		logger.debug("Damos de alta las subvenciones " + codSubvsSeleccionadas);
	    		ArrayList<String> errorSubvenciones = socioSubvencionManager.altaSubvenciones(sc, poliza, subvSeleccionadas);
	    		
	    		
	    		if (errorSubvenciones.size() <= 0){
	    			//modifico el asegurado de la sesion para anhadirle las subvenciones
	    			for (Socio soc : aseguradoSesion.getSocios()){
	    				if (soc.getId().equals(sc.getId())){
	    					soc.setSubvencionSocios(sc.getSubvencionSocios());
	    					break;
	    				}
	    			}
	    		}else{
	    			for (String mens : errorSubvenciones){
	    				erroresWeb.add(mens);
	    				//errores
	    				logger.info("Se ha producido un error al actualizar las subvenciones de socios.durante el borrado de una poliza:" + mens);
	    			}
    				return erroresWeb;
	    		}
    		}
    	//inicializo valores
    	codSubvsSeleccionadas = "";
    	subvSeleccionadas=null;
    	}// fin iter
    	logger.debug("end - actualizarSubvSocios");
    	return erroresWeb;
	}
	
	public ArrayList<String> actualizaSubvSociosGanado(Poliza poliza, Asegurado aseguradoSesion) throws BusinessException{
		logger.debug("init - actualizarSubvSociosGanado");
		ArrayList<String> erroresWeb = new ArrayList<String>();
		//Recogemos los socios del asegurado
		Set<Socio> socios = socioSubvManager.getSociosByAsegPoliza(aseguradoSesion, poliza);
		List<SubvencionEnesaGanado> subvSeleccionadas = null;
		Iterator<Socio> iter = socios.iterator();
		boolean subvencionDeclarada = false;
		String codSubvsSeleccionadas = "";
    	while (iter.hasNext()) {
    		Socio sc = (Socio) iter.next();
    		subvencionDeclarada = false;
    		Poliza p = null;
    		//Compruebo si el socio tiene subvenciones para la misma.
    		for (Poliza pol: sc.getAsegurado().getPolizas()){
    			if (pol.getIdpoliza().equals(poliza.getIdpoliza())){
    				p = pol;
    				break;
    			}
    		}
    		if (p != null && sc.getSubvencionSocioGanados()!= null && sc.getSubvencionSocioGanados().size() > 0)
    		{
    			logger.debug("El socio " + sc.getId().getNif() + " SI tiene subvenciones " + sc.getSubvencionSocioGanados().size());
    			for (SubvencionSocioGanado ss: sc.getSubvencionSocioGanados()){
    				if (ss.getSubvencionEnesaGanado().getId().getLineaseguroid().equals(p.getLinea().getLineaseguroid()) &&
    						ss.getPoliza().getIdpoliza().equals(p.getIdpoliza())){
    					subvencionDeclarada = true;
    					break;
    				}
    			}
    		}
    		if (!subvencionDeclarada){
    			logger.debug("El socio " + sc.getId().getNif() + " NO tiene subvenciones " + sc.getSubvencionSocioGanados().size());
	    		if (sc.getAtp()!=null){
	    			if (sc.getNumsegsocial()!=null && sc.getRegimensegsocial() !=null && sc.getAtp().equals("SI")){
	    			codSubvsSeleccionadas = "20,";
	    			}
	    		}
	    		if (sc.getJovenagricultor()!=null){
		    		if (sc.getJovenagricultor().equals('H')){
		    			codSubvsSeleccionadas += "10,";
		    		}
		    		if (sc.getJovenagricultor().equals('M')){
		    			codSubvsSeleccionadas += "11,";
		    		}
	    		}
	    		if (!codSubvsSeleccionadas.equalsIgnoreCase("")){
	    			codSubvsSeleccionadas = codSubvsSeleccionadas.substring(0, codSubvsSeleccionadas.lastIndexOf(','));
	    		
	    		//Obtenemos las SubvencionesEnesa para los codigos recogidos
	    		subvSeleccionadas = socioSubvManager.getSubvencionesInsertar(sc, poliza, codSubvsSeleccionadas);
	    		}
	    		logger.debug("Damos de alta las subvenciones " + codSubvsSeleccionadas);
	    		ArrayList<String> errorSubvenciones = socioSubvManager.altaSubvenciones(sc, poliza, subvSeleccionadas);
	    		
	    		
	    		if (errorSubvenciones.size() <= 0){
	    			//modifico el asegurado de la sesion para anhadirle las subvenciones
	    			for (Socio soc : aseguradoSesion.getSocios()){
	    				if (soc.getId().equals(sc.getId())){
	    					soc.setSubvencionSocioGanados(sc.getSubvencionSocioGanados());
	    					break;
	    				}
	    			}
	    		}else{
	    			for (String mens : errorSubvenciones){
	    				erroresWeb.add(mens);
	    				//errores
	    				logger.info("Se ha producido un error al actualizar las subvenciones de socios.durante el borrado de una poliza:" + mens);
	    			}
    				return erroresWeb;
	    		}
    		}
    	//inicializo valores
    	codSubvsSeleccionadas = "";
    	subvSeleccionadas=null;
    	}// fin iter
    	logger.debug("end - actualizarSubvSociosGanado");
    	return erroresWeb;
	}	
	
	public void setAseguradoManager(AseguradoManager aseguradoManager) {
		this.aseguradoManager = aseguradoManager;
	}

}
