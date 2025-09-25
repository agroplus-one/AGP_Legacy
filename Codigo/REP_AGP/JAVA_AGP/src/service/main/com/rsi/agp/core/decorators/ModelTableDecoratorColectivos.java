package com.rsi.agp.core.decorators;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.commons.Usuario;



public class ModelTableDecoratorColectivos extends TableDecorator
{
	
	
	public String getColectivoSelec()
	{	
		//DAA 22/05/2013	Obtenemos el perfil del usuario en sesion
		PageContext pageContext = (PageContext) getPageContext();
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String perfil = usuario.getPerfil();
		
		Colectivo co = (Colectivo)getCurrentRowObject();
		String id = "";
		String entidad = "";
		String plan = "";
		String linea = "";
		String cifTomador = "";
		String idColectivo = "";
		String dcColectivo = "";
		String nombreColectivo = "";
		String entMediadora = "";
		String subEntMediadora = "";
		String activo = "";
		String colectivoCalculo = "";
		String primerPago = "";
		String segundoPago = "";	
		String fecPrimerPago = "";
		String fecSegundoPago = "";	
		String desc_entidad = "";
		String desc_linea = "";
		String desc_tomador = "";
		String desc_entidadMed = "";
		String desc_subEntMed = "";
		String fecCambio = "";
		String fecEfecto = "";
		String cccEntidad = "";
		String cccOficina = "";
		String cccDc = "";
		String cccCuenta = "";
		String tx_observaciones = "";
		String isCRM = "";
				
		String repreNombre = "";
		String repreAp1 = "";
		String repreAp2 = "";
		String repreNif = "";
		String bajaLogica = "false";
		String iban = "";
		String ccc = "";
		String tipoDescRecarg ="";
		String pctDescRecarg = "";
		String envioIbanAgro = "";
		/* PTC. 78845 ** CAMPOS NUEVOS (28.02.2022) ** Inicio */
		String estadoAgroseguro = "";
		String fechaEnvio = "";
		/* PTC. 78845 ** CAMPOS NUEVOS (28.02.2022) ** Fin */
		
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		
		if(co.getTomador().getEntidad().getNomentidad()!=null)
			desc_entidad = StringUtils.forHTML(co.getTomador().getEntidad().getNomentidad());
		
		if(co.getLinea().getNomlinea()!= null)
			desc_linea = StringUtils.forHTML(co.getLinea().getNomlinea());
		
		if(co.getTomador().getRazonsocial() != null)
			desc_tomador = StringUtils.forHTML(co.getTomador().getRazonsocial());
		
		if(co.getSubentidadMediadora().getEntidadMediadora().getNomentidad() != null)
			desc_entidadMed = StringUtils.forHTML(co.getSubentidadMediadora().getEntidadMediadora().getNomentidad());
		
		if(co.getSubentidadMediadora().getNomSubentidadCompleto() != null)
			desc_subEntMed = StringUtils.forHTML(co.getSubentidadMediadora().getNomSubentidadCompleto());
		
		if (co.getId() != null)
			id = StringUtils.forHTML(co.getId().toString());
			
		if (co.getTomador().getId().getCodentidad() != null)
			entidad = StringUtils.forHTML(co.getTomador().getId().getCodentidad().toString());
		
		if (co.getLinea().getCodlinea() != null)
			linea = StringUtils.forHTML(co.getLinea().getCodlinea().toString());
		
		if (co.getLinea().getCodplan() != null)
			plan = StringUtils.forHTML(co.getLinea().getCodplan().toString());
		
		if (co.getTomador().getId().getCiftomador() != null)
			cifTomador = StringUtils.forHTML(co.getTomador().getId().getCiftomador());
		
		if (co.getIdcolectivo() != null)
			idColectivo = StringUtils.forHTML(co.getIdcolectivo());
		
		if (co.getDc() != null)
			dcColectivo = StringUtils.forHTML(co.getDc());
		
		if (co.getNomcolectivo() != null){
			nombreColectivo = StringUtils.forHTML(co.getNomcolectivo());
		}
		
		if (co.getSubentidadMediadora().getId().getCodentidad() != null)
			entMediadora = StringUtils.forHTML(co.getSubentidadMediadora().getId().getCodentidad().toString());
		
		if (co.getSubentidadMediadora().getId().getCodsubentidad() != null)
			subEntMediadora = StringUtils.forHTML(co.getSubentidadMediadora().getId().getCodsubentidad().toString());
		
		if (co.getActivo() != null)
			activo = StringUtils.forHTML(co.getActivo().toString());
		
		if (co.getPctdescuentocol() != null)
			colectivoCalculo = StringUtils.forHTML(co.getPctdescuentocol().toString());
		
		if (co.getPctprimerpago() != null)
			primerPago = StringUtils.forHTML(co.getPctprimerpago().toString());
		
		if (co.getPctsegundopago() != null)
			segundoPago = StringUtils.forHTML(co.getPctsegundopago().toString());
		
		if (co.getFechaprimerpago() != null)
		{
			fecPrimerPago = StringUtils.forHTML(formato.format(co.getFechaprimerpago()));
		}
		
		if (co.getFechasegundopago() != null)
			fecSegundoPago = StringUtils.forHTML(formato.format(co.getFechasegundopago()));
		
		if (co.getFechacambio() != null)
			fecCambio = StringUtils.forHTML(formato.format(co.getFechacambio()));
		
		if (co.getFechaefecto() != null)
			fecEfecto = StringUtils.forHTML(formato.format(co.getFechaefecto()));
		
		if (co.getCccEntidad() != null)
			cccEntidad = StringUtils.forHTML(co.getCccEntidad());
		
		if (co.getCccOficina() != null)
			cccOficina = StringUtils.forHTML(co.getCccOficina());
		
		if (co.getCccDc() != null)
			cccDc = StringUtils.forHTML(co.getCccDc());
		
		if (co.getCccCuenta() != null)
			cccCuenta = StringUtils.forHTML(co.getCccCuenta());
		
		if (co.getIban()!= null)
			iban = StringUtils.forHTML(co.getIban());
		
		ccc = iban+cccEntidad+cccOficina+cccDc+cccCuenta;
		
		if (co.getObservaciones() != null)
			tx_observaciones = StringUtils.forHTML(co.getObservaciones());
		
		if (co.getIsCRM() != null && co.getIsCRM() > 0){
			isCRM = "true";
		}
		else{
			isCRM = "false";
		}
		
		if (co.getTomador() != null){
			if(co.getTomador().getRepreNombre() !=null)
				repreNombre = StringUtils.forHTML(co.getTomador().getRepreNombre());
			
			if(co.getTomador().getRepreAp1() !=null)
				repreAp1 = StringUtils.forHTML(co.getTomador().getRepreAp1());
			
			if(co.getTomador().getRepreAp2() !=null)
				repreAp2 = StringUtils.forHTML(co.getTomador().getRepreAp2());
			
			if(co.getTomador().getRepreNif() !=null)
				repreNif = StringUtils.forHTML(co.getTomador().getRepreNif());
		}
		
		//DAA 22/05/2013
		if(co.getFechabaja()!= null)
			bajaLogica = "true";
		
		if (co.gettipoDescRecarg()!=null) {
			tipoDescRecarg= StringUtils.forHTML(co.gettipoDescRecarg().toString());		
		}
		if (co.getpctDescRecarg()!= null) {
			pctDescRecarg = StringUtils.forHTML(co.getpctDescRecarg().toString());					
		}
		
		if (co.getEnvioIbanAgro() != null){
			envioIbanAgro = StringUtils.forHTML(co.getEnvioIbanAgro().toString());
		}
		
		/* PTC. 78845 ** CAMPOS NUEVOS (28.02.2022) ** Inicio */
		if (co.getEstadoAgroseguro() != null){
			estadoAgroseguro = StringUtils.forHTML(co.getEstadoAgroseguro().toString());
		}
		
		if (co.getFechaEnvio() != null){
			fechaEnvio = StringUtils.forHTML(formato.format(co.getFechaEnvio()));
		}
		/* PTC. 78845 ** CAMPOS NUEVOS (28.02.2022) ** Fin */
			
		
		String modif = "<a href=\"javascript:modificar('"+id+"','"+entidad+"','"+plan+"',"+
		"'"+linea+"','"+cifTomador+"','"+idColectivo+"','"+dcColectivo+"','"+
		nombreColectivo+"','"+entMediadora+"','"+subEntMediadora+"','"+activo+"','"+colectivoCalculo
		+"','"+primerPago+"','"+segundoPago
		+"','"+fecPrimerPago+"','"+fecSegundoPago+"','"+ desc_entidad +"','"+desc_linea +"','"+desc_tomador +"','"+ 
		desc_entidadMed +"','"+ desc_subEntMed +"','"+fecCambio+"','"+fecEfecto+"','"+ 
		ccc +"','"+iban+"','"+tx_observaciones+"','"+isCRM+"','"+bajaLogica+"','"+tipoDescRecarg+"','"+pctDescRecarg
		+"','"+envioIbanAgro+"','"+estadoAgroseguro+"','"+fechaEnvio+"')\">"
		+"<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\" /></a>";
		
		String baja = "<a href=\"javascript:enviarForm('baja','"+id+"')\">" +
				"<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar\" title=\"Borrar\" /></a>&nbsp;";
		
		String historico = "<a href=\"javascript:consultarHistorico('"+id+"')\">" +
		"<img src=\"jsp/img/magnifier.png\" alt=\"Consultar Hist&oacute;rico\" title=\"Consultar Hist&oacute;rico\"/></a>";
		String imprimir = "<a href=\"#\" onclick=\"javascript:imprimirAlta('"+id+"','"+repreNombre+"','"+repreAp1+"','"+repreAp2+"','"+repreNif+"','"+
		cccEntidad +"','"+ cccOficina +"','"+cccDc+"','"+cccCuenta+"','"+idColectivo+"','"+dcColectivo
		+"')\"><img src='jsp/img/displaytag/imprimir.png' alt='Imprimir colectivo' title='Imprimir colectivo'/></a>&nbsp;";
		
		// Si el usuario es de perfil 0 y el colectivo no está activo, se puede activar directament desde el listado
		String activar="";
		if (co.getActivo().equals(Constants.COLECTIVO_NO_ACTIVO) && (Constants.PERFIL_USUARIO_ADMINISTRADOR).equals(perfil))
			 activar =  "<a href=\"javascript:openPopupActivarColectivo('"+id+"','"+idColectivo+"','"+ccc+"')\">" +
				"<img src=\"jsp/img/displaytag/accept.png\" alt=\"Activar Colectivo\" title=\"Activar Colectivo\"/></a>";
		
		String registrarColectivo="";
		if ((co.getEstadoAgroseguro() == null || Constants.COLECTIVO_AGRO_RECH.equals(co.getEstadoAgroseguro())) && Constants.PERFIL_USUARIO_ADMINISTRADOR.equals(perfil))
			registrarColectivo = "<a href=\"javascript:registrarColectivo('"+id+"')\">" +
					"<img src=\"jsp/img/displaytag/portalMediador.png\" alt=\"Registrar colectivo\" title=\"Registrar colectivo\"/></a>";
		
		//DAA 22/05/2013
		if ((Constants.PERFIL_USUARIO_ADMINISTRADOR).equals(perfil) && !("").equals(StringUtils.nullToString(co.getFechabaja()))){
			return modif + historico + activar + registrarColectivo;
		}else{
			return modif + baja + historico + imprimir + activar + registrarColectivo;
		}
		

	}
	
	public String getCargaColectivoSelec(){
		Colectivo co = (Colectivo)getCurrentRowObject();
		String id = "";
		String entidad = "";
		String plan = "";
		String linea = "";
		String cifTomador = "";
		String idColectivo = "";
		String dcColectivo = "";
		String activo = "";
		String nombreColectivo = "";
		String entMediadora = "";
		String subEntMediadora = "";
		String colectivoCalculo = "";
		String primerPago = "";
		String segundoPago = "";
		String fecPrimerPago = "";
		String fecSegundoPago = "";		
		String desc_entidad = "";
		String desc_linea = "";
		String desc_tomador = "";
		String desc_entidadMed = "";
		String desc_subEntMed = "";
		String envio_iban_colec = "";
		/* PTC. 78845 ** CAMPOS NUEVOS (28.02.2022) ** Inicio */
		String estadoAgroseguro = "";
		String fechaEnvio = "";
		/* PTC. 78845 ** CAMPOS NUEVOS (28.02.2022) ** Fin */
		
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		if (co.getActivo() != null)
			activo = StringUtils.forHTML(co.getActivo().toString());
		
		if(co.getTomador().getEntidad().getNomentidad()!=null)
			desc_entidad = StringUtils.forHTML(co.getTomador().getEntidad().getNomentidad());
		
		if(co.getLinea().getNomlinea()!= null)
			desc_linea = StringUtils.forHTML(co.getLinea().getNomlinea());
		
		if(co.getTomador().getRazonsocial() != null)
			desc_tomador = StringUtils.forHTML(co.getTomador().getRazonsocial());
		
		if(co.getSubentidadMediadora().getEntidadMediadora().getNomentidad() != null)
			desc_entidadMed = StringUtils.forHTML(co.getSubentidadMediadora().getEntidadMediadora().getNomentidad());
		
		if(co.getSubentidadMediadora().getNomsubentidad() != null)
			desc_subEntMed = StringUtils.forHTML(co.getSubentidadMediadora().getNomsubentidad());
		
		if (co.getId() != null)
			id = StringUtils.forHTML(co.getId().toString());
			
		if (co.getTomador().getId().getCodentidad() != null)
			entidad = StringUtils.forHTML(co.getTomador().getId().getCodentidad().toString());
		
		if (co.getLinea().getCodlinea() != null)
			linea = StringUtils.forHTML(co.getLinea().getCodlinea().toString());
		
		if (co.getLinea().getCodplan() != null)
			plan = StringUtils.forHTML(co.getLinea().getCodplan().toString());
		
		if (co.getTomador().getId().getCiftomador() != null)
			cifTomador = StringUtils.forHTML(co.getTomador().getId().getCiftomador());
		
		if (co.getIdcolectivo() != null)
			idColectivo = StringUtils.forHTML(co.getIdcolectivo());
		
		if (co.getDc() != null)
			dcColectivo = StringUtils.forHTML(co.getDc());
		
		if (co.getNomcolectivo() != null)
			nombreColectivo = StringUtils.forHTML(co.getNomcolectivo());
		
		if (co.getSubentidadMediadora().getId().getCodentidad() != null)
			entMediadora = StringUtils.forHTML(co.getSubentidadMediadora().getId().getCodentidad().toString());
		
		if (co.getSubentidadMediadora().getId().getCodsubentidad() != null)
			subEntMediadora = StringUtils.forHTML(co.getSubentidadMediadora().getId().getCodsubentidad().toString());
		
		if (co.getPctdescuentocol() != null)
			colectivoCalculo = StringUtils.forHTML(co.getPctdescuentocol().toString());
		
		if (co.getPctprimerpago() != null)
			primerPago = StringUtils.forHTML(co.getPctprimerpago().toString());
		
		if (co.getPctsegundopago() != null)
			segundoPago = StringUtils.forHTML(co.getPctsegundopago().toString());
		
		if (co.getFechaprimerpago() != null)
			fecPrimerPago = StringUtils.forHTML(formato.format(co.getFechaprimerpago()));
		
		if (co.getFechasegundopago() != null)
			fecSegundoPago = StringUtils.forHTML(formato.format(co.getFechasegundopago()));
		
		if (co.getEnvioIbanAgro() != null)
			envio_iban_colec = StringUtils.forHTML(co.getEnvioIbanAgro().toString());
		
		/* PTC. 78845 ** CAMPOS NUEVOS (28.02.2022) ** Inicio */
		if (co.getEstadoAgroseguro() != null)
			estadoAgroseguro = StringUtils.forHTML(co.getEstadoAgroseguro().toString());
		
		if (co.getFechaEnvio() != null)
			fechaEnvio = StringUtils.forHTML(co.getFechaEnvio().toString());
		/* PTC. 78845 ** CAMPOS NUEVOS (28.02.2022) ** Fin */
		
		String modif = "<a href=\"javascript:modificar('"+id+"','"+entidad+"','"+plan+"',"+
		"'"+linea+"','"+cifTomador+"','"+idColectivo+"','"+dcColectivo+"','"+
		nombreColectivo+"','"+entMediadora+"','"+subEntMediadora+"','"+colectivoCalculo
		+"','"+primerPago+"','"+segundoPago
		+"','"+fecPrimerPago+"','"+fecSegundoPago+"','"+activo +"','"+ desc_entidad +"','"+desc_linea +"','"+desc_tomador
		+"','"+ desc_entidadMed +"','"+ desc_subEntMed +"','"+ envio_iban_colec +"','"+ estadoAgroseguro +"','"+ fechaEnvio +"')\">"
		+"<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/></a>&nbsp;";
		String carga = "";
		if (co.getFechabaja() == null) {
			 carga = "<a href=\"javascript:cargarColectivo('"+id
					 +"')\"><img src=\"jsp/img/displaytag/load.png\" alt=\"Cargar colectivo\" title=\"Cargar colectivo\"/></a>";
		}
		return modif + carga;
	}
	
	public String getColEntidad ()
	{
		Colectivo co = (Colectivo)getCurrentRowObject();
		return co.getTomador().getId().getCodentidad().toString();
	}
	
	public String getColPlan ()
	{
		Colectivo co = (Colectivo)getCurrentRowObject();
		return co.getLinea().getCodplan().toString();
	}
	
	public String getColLinea ()
	{
		Colectivo co = (Colectivo)getCurrentRowObject();
		return co.getLinea().getCodlinea().toString();
	}
	
	public String getColId ()
	{
		Colectivo co = (Colectivo)getCurrentRowObject();
		return co.getIdcolectivo() + "-" + co.getDc();
	}
	
	public String getColNombre ()
	{
		Colectivo co = (Colectivo)getCurrentRowObject();
		return co.getNomcolectivo();
	}
	
	public String getColEntMed ()
	{
		Colectivo co = (Colectivo)getCurrentRowObject();
		return co.getSubentidadMediadora().getId().getCodentidad().toString()+" - "+co.getSubentidadMediadora().getId().getCodsubentidad().toString();
	}
	
	public String getColCifTom ()
	{
		Colectivo co = (Colectivo)getCurrentRowObject();
		return co.getTomador().getId().getCiftomador();
	}
	
	public String getColActivo ()
	{
		Colectivo co = (Colectivo)getCurrentRowObject();
		String dev = "";
		if (co.getActivo() == '1')
		{
			dev = "Si";
		}
		else
		{
			dev = "No";
		}
		return dev;			
	}
	public String getColFechaCambio(){
		Colectivo co = (Colectivo)getCurrentRowObject();
		String res = "";
		if (co.getFechacambio() != null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaCambio = co.getFechacambio();
			res = sdf.format(fechaCambio);
		}
		return res;
	}
	public String getColFechaEfecto(){
		Colectivo co = (Colectivo)getCurrentRowObject();
		String res = "";
		if (co.getFechaefecto() != null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaEfecto = co.getFechaefecto();
			res = sdf.format(fechaEfecto);
		}
		return res;
	}
	
	public String getColFechaBaja(){
		Colectivo co = (Colectivo)getCurrentRowObject();
		String res = "";
		if (co.getFechabaja() != null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaBaja = co.getFechabaja();
			res = sdf.format(fechaBaja);
		}
		return res;
	}
	
	public String getColEnvioIbanAgro ()
	{
		Colectivo co = (Colectivo)getCurrentRowObject();
		String res = "";
		if (co.getEnvioIbanAgro() != null){
			if (co.getEnvioIbanAgro() == 'O'){
				res = "Dom. Agroseguro Obligatorio";
			}else if (co.getEnvioIbanAgro() == 'S'){
				res = "Dom. Agroseguro Opcional";
			}else if (co.getEnvioIbanAgro() == 'N'){
				res = "No domiciliar";
			}
		}
		
		return res;			
	}
	
	public String getFechaEnvio(){
		Colectivo co = (Colectivo)getCurrentRowObject();
		String res = "";
		if (co.getFechaEnvio() != null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaEnvio = co.getFechaEnvio();
			res = sdf.format(fechaEnvio);
		}
		return res;
	}
	
	public String getEstadoAgroseguro(){
		String res;
		Colectivo co = (Colectivo)getCurrentRowObject();
		if (Constants.COLECTIVO_AGRO_OK.equals(co.getEstadoAgroseguro())
				|| Constants.COLECTIVO_AGRO_KO.equals(co.getEstadoAgroseguro())) {
			res = "Correcto"; 
		} else if (Constants.COLECTIVO_AGRO_RECH.equals(co.getEstadoAgroseguro())) {
			res = "Rechazado"; 
		} else {
			res = "";
		}
		return res;
	}
}