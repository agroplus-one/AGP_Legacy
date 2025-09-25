package com.rsi.agp.core.decorators;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.siniestro.Siniestro;

public class ModelTableDecoratorSiniestros extends TableDecorator {

	/*** SONAR Q ** MODIF TAM(14.11.2021) ***/
	/**
	 * - Se ha eliminado todo el codigo comentado 
	 * - Se crean metodos nuevos para descargar de ifs/fors 
	 * - Se crean constantes locales nuevas
	 **/

	public String getAcciones() {
		String acciones = "";
		Siniestro siniestro = (Siniestro) getCurrentRowObject();

		/* Pet. 63473 ** MODIF TAM (26.11.2021) ** Inicio */
		PageContext pageContext = (PageContext) getPageContext();
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String perfil = usuario.getPerfil();
		/* Pet. 63473 ** MODIF TAM (26.11.2021) ** Fin */

		// si esta en estado 4 (erroneo) o 1 (borrador) modo edicion
		// para el resto de estados solo modo visualizacion
		if (siniestro.getEstadoSiniestro().getIdestado().equals(Constants.SINIESTRO_ESTADO_ENVIADO_ERROR)
				|| siniestro.getEstadoSiniestro().getIdestado().equals(Constants.SINIESTRO_ESTADO_PROVISIONAL)
				|| siniestro.getEstadoSiniestro().getIdestado().equals(Constants.SINIESTRO_ESTADO_DEFINITIVO)) {
			acciones += "<a href=\"#\" onclick=\"javascript:editar(" + siniestro.getId() + ","
					+ siniestro.getEstadoSiniestro().getIdestado()
					+ ")\"><img src='jsp/img/displaytag/edit.png' alt='Editar' title='Editar'/></a>&nbsp;";
		} else {
			acciones = "<a href=\"#\" onclick=\"javascript:ver('" + siniestro.getId()
					+ "')\"/><img src=\"jsp/img/displaytag/information.png\" alt=\"Ver informacion\" title=\"Ver informacion\"/></a>";
		}

		if (!siniestro.getEstadoSiniestro().getIdestado().equals(Constants.SINIESTRO_ESTADO_ENVIADO_PDT_ACEPTACION)
				&& !siniestro.getEstadoSiniestro().getIdestado().equals(Constants.SINIESTRO_ESTADO_ENVIADO_CORRECTO))
			acciones += "<a href=\"#\" onclick=\"javascript:eliminar(" + siniestro.getId()
					+ ")\"><img src='jsp/img/displaytag/delete.png' alt='Eliminar' title='Eliminar'/></a>&nbsp;";
		else
			acciones += "<img src=\"jsp/img/displaytag/transparente.gif\" width='16' height='16'/>&nbsp;";

		acciones += "<a href=\"#\" rel=\"external\" onclick=\"javascript:imprimir(" + siniestro.getId()
				+ ")\"><img src='jsp/img/displaytag/imprimir.png' alt='Imprimir Siniestro' title='Imprimir Siniestro'/></a>&nbsp;";

		if (siniestro.getEstadoSiniestro().getIdestado().equals(Constants.SINIESTRO_ESTADO_ENVIADO_ERROR)) {
			acciones += "<a href=\"#\" onclick=\"javascript:verAcuseRecibo(" + siniestro.getId()
					+ ")\"><img src='jsp/img/displaytag/acuserecibo.png' alt='Consultar Acuse Recibo' title='Consultar Acuse Recibo'/></a>";
		}

		// si esta en estado GRABADO (idem borrador)
		if (siniestro.getEstadoSiniestro().getIdestado().equals(Constants.SINIESTRO_ESTADO_PROVISIONAL)) {
			acciones += "<a href=\"javascript:pasarDefinitivo(" + siniestro.getId() + ")\">";
			acciones += "<img src=\"jsp/img/displaytag/accept.png\" alt=\"Pasar a definitiva\" title=\"Pasar a definitiva\"/></a>";
		}

		// Enviado correcto y enviado por servicio web (para solicitar parte del
		// siniestro - Pdf del SW)
		if (null != siniestro.getNumerosiniestro()
				&& siniestro.getEstadoSiniestro().getIdestado().equals(Constants.SINIESTRO_ESTADO_ENVIADO_CORRECTO)) {
			acciones += "<a href=\"javascript:verDetalleLineaSiniestro(" + siniestro.getNumeroSerie() + ", "
					+ siniestro.getNumerosiniestro() + "," + siniestro.getId() + ","
					+ siniestro.getPoliza().getIdpoliza() + ")\">";
			acciones += "<img src=\"jsp/img/displaytag/imprimir_poliza_modificada.png\" alt=\"Pdf - Parte del siniestro\" title=\"Pdf - Parte del siniestro\"/></a>";
		}

		/* Pet. 63473 ** MODIF TAM (26.11.2021) ** Inicio */
		/*
		 * Inclcuir una nueva accion para dar de baja (logica) el siniestro,
		 * independientemente del estado del siniestro
		 */
		if ((Constants.PERFIL_USUARIO_ADMINISTRADOR).equals(perfil)) {
			acciones += "<a href=\"javascript:bajaSiniestro(" + siniestro.getId() + ")\">";
			acciones += "<img src=\"jsp/img/displaytag/cancel.png\" alt=\"Baja Siniestro\" title=\"Baja Siniestro\"/></a>";
		}
		/* Pet. 63473 ** MODIF TAM (26.11.2021) ** Fin */

		return acciones;
	}

	public String getColumnaOrden() {
		Siniestro siniestro = (Siniestro) getCurrentRowObject();
		return StringUtils.leftPad(siniestro.getNumsiniestro().toString(), 2, '0');
	}

	public Date getColumnaOcurrencia() {
		Siniestro siniestro = (Siniestro) getCurrentRowObject();
		Date fecha = null;
		if (siniestro.getFechaocurrencia() != null) {
			fecha = siniestro.getFechaocurrencia();
		}
		return fecha;
	}

	public String getColumnaRiesgo() {
		Siniestro siniestro = (Siniestro) getCurrentRowObject();
		if (siniestro.getCodriesgo() != null)
			return String.format("%02d", Integer.parseInt(siniestro.getCodriesgo().toString()));
		else
			return "";
	}

	public String getColumnaDes() {
		String descripcion = "";
		return descripcion;
	}

	public String getColumnaEstado() {
		Siniestro siniestro = (Siniestro) getCurrentRowObject();
		if (siniestro.getEstadoSiniestro() == null) {
			return " - ";
		} else {
			return siniestro.getEstadoSiniestro().getDescestado().toString();
		}
	}

	public Date getColumnaEnvio() {
		// se ha añadido el campo a Siniestro desde vw_siniestros_utilidades
		Siniestro siniestro = (Siniestro) getCurrentRowObject();
		return siniestro.getFechaEnvio();
	}

	public String getColumnaNumSn() {
		String descripcion = "";
		Siniestro siniestro = (Siniestro) getCurrentRowObject();
		if (siniestro.getNumerosiniestro() != null
				&& siniestro.getNumerosiniestro().compareTo(Constants.SINIESTRO_WS_SIN_NUMERO) == 0) {
			descripcion = "";
		} else if (siniestro.getNumerosiniestro() != null) {
			descripcion = siniestro.getNumerosiniestro().toString();
		} else {
			descripcion = "FTP";
		}
		return descripcion;
	}

	/* Pet. 63473 ** MODIF TAM (30/11/2021) ** Inicio */
	public String getColumnaFecBaja() {
		Siniestro siniestro = (Siniestro) getCurrentRowObject();
		String res = "";
		if (siniestro.getFechaBaja() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaBaja = siniestro.getFechaBaja();
			res = sdf.format(fechaBaja);
		}
		return res;
	}
	/* Pet. 63473 ** MODIF TAM (30/11/2021) ** Fin */

}
