
package com.rsi.agp.core.decorators;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.poliza.CapAsegRelModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.Parcela;

public class ModelTableDecoratorListaParcelasTodas extends TableDecorator {

	private static final Log logger = LogFactory.getLog(ModelTableDecoratorListaParcelasTodas.class);

	private static final String STYLE_DIV = "background-color:#A9F5A9;height:18px;";
	private static final int TEXT_HEIGHT = 13;

	/** CONSTANTES SONAR Q ** MODIF TAM (26.10.2021) ** Inicio **/
	private static final String TRANSPARENT_GIF = "<img src='jsp/img/displaytag/transparente.gif' style='width:16;height:16' />";
	private static final String COLOR_VERDE = "background-color:#A9F5A9;height:";
	private static final String DIV = "</div>";
	private static final String DIV_STYLE = "<div style='";
	private static final String BR = "<br/>";
	/** CONSTANTES SONAR Q ** MODIF TAM (26.10.2021) ** Fin **/

	public String getAdmActionsConsulta() {
		String acciones = "";
		Parcela parcela = (Parcela) getCurrentRowObject();
		acciones += "<a href=\"javascript:visualizarDatosRegistro('";
		acciones += parcela.getTipoparcela();
		acciones += "','";
		acciones += parcela.getIdparcela();
		acciones += "')\"><img src=\"jsp/img/displaytag/information.png\" alt=\"Visualizar informaci&oacute;n\" title=\"Visualizar informaci&oacute;n\"/></a>";

		return acciones;
	}

	public String getAdmActions() {

		Parcela parcela = (Parcela) getCurrentRowObject();

		String value = "";
		String acciones = "";
		String tipoParcela = "";

		if (parcela.getTipoparcela() != null)
			tipoParcela = parcela.getTipoparcela().toString();

		// Si la poliza tiene idestado = 8
		// solo se mostrara un 煤nico icono que servira para
		// visualizar los datos de las parcelas (ni editar ni borrar ni replicar)
		/// mejora 112 Angel 01/02/2012 a帽adida la opci贸n de ver la p贸liza sin
		// opci贸n a editarla tambi茅n con estado grabaci贸n definitiva
		if (parcela.getPoliza().getEstadoPoliza().getIdestado().intValue() == 8
				|| parcela.getPoliza().getEstadoPoliza().getIdestado().intValue() == 3) {
			acciones += "<a href=\"javascript:visualizarDatosRegistro('";
			acciones += parcela.getTipoparcela();
			acciones += "','";
			acciones += parcela.getIdparcela();
			acciones += "')\"><img src=\"jsp/img/displaytag/information.png\" alt=\"Visualizar informaci&oacute;n\" title=\"Visualizar informaci&oacute;n\"/></a>";
			acciones += TRANSPARENT_GIF+"&nbsp;";
			acciones += TRANSPARENT_GIF+"&nbsp;";
			acciones += TRANSPARENT_GIF;
		} else {
			acciones += "<a href=\"javascript:updateParcela('";
			acciones += parcela.getTipoparcela();
			acciones += "','";
			acciones += parcela.getIdparcela();
			acciones += "')\"><img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/></a>&nbsp;";

			// Si es parcela --> mostramos icono alta instalaci贸n y duplicar parcela
			if ("P".equals(tipoParcela)) {
				acciones += "<a href=\"javascript:altaEstructuraParcela('";
				acciones += parcela.getPoliza().getIdpoliza();
				acciones += "','";
				acciones += parcela.getIdparcela();
				acciones += "')\"><img src=\"jsp/img/displaytag/instalaciones.jpg\" alt=\"Alta instalacion\" title=\"Alta instalacion\" width=\"16\" height=\"16\" /></a>&nbsp;";

				acciones += "<a href=\"javascript:duplicateParcela('";
				acciones += parcela.getPoliza().getIdpoliza();
				acciones += "','";
				acciones += parcela.getIdparcela();
				acciones += "')\"><img src=\"jsp/img/displaytag/duplicar.png\" alt=\"Duplicar\" title=\"Duplicar\"/></a>&nbsp;";
			} else {
				acciones += TRANSPARENT_GIF;
				acciones += TRANSPARENT_GIF;
			}

			acciones += "<a href=\"javascript:deleteParcela('";
			acciones += parcela.getPoliza().getIdpoliza();
			acciones += "','";
			acciones += parcela.getIdparcela();
			acciones += "','";

			if ("P".equals(tipoParcela))
				acciones += "true";
			else
				acciones += "false";

			acciones += "')\"><img src=\"jsp/img/displaytag/delete.png\" alt=\"Eliminar\" title=\"Eliminar\"/></a>";
			acciones += "<input type='hidden' name='idRow' value='' />";
			acciones += "<input type='hidden' name='idRow_cm' id='idRow_cm' value='" + parcela.getIdparcela() + "' />";
		}
		
		/** SONAR Q ** MODIF TAM(03.11.2021) * Inicio */
		/* A馻dimos nuevo m閠odo para descargar de ifs/for */
		value = obtenerValue(tipoParcela, parcela, acciones);
		/** SONAR Q ** MODIF TAM(03.11.2021) * Inicio */
		
		if ("E".equals(tipoParcela)) {
			acciones += "<input type='hidden' name='tipoParcela' id='tipoParcela' value='E@E'/>";
		} else {
			acciones += "<input type='hidden' name='tipoParcela' id='tipoParcela' value='P@P'/>";
		}

		// localizacion: @@prov;;comarca;;termino;;subtermino@@
		String localizacion = "@@" + parcela.getTermino().getId().getCodprovincia() + ";;"
				+ parcela.getTermino().getComarca().getId().getCodcomarca() + ";;"
				+ parcela.getTermino().getId().getCodtermino() + ";;" + getSubterminoParcela(parcela) + "@@";
		acciones += "<input type='hidden' name='localizacion_cm' id='localizacion_cm' value='" + localizacion + "'/>";

		value = acciones;

		return value;
	}

	private String getSubterminoParcela(Parcela parcela) {
		if (parcela.getTermino() == null && parcela.getTermino().getId() == null) {
			return "";
		} else {
			return StringUtils.nullToString(parcela.getTermino().getId().getSubtermino());
		}
	}

	public String getNombre() {
		String resultado = "";
		String value = "";
		String tipoParcela = "";

		Parcela parcela = (Parcela) getCurrentRowObject();

		if (parcela.getTipoparcela() != null)
			tipoParcela = parcela.getTipoparcela().toString();

		resultado = StringUtils.nullToString(parcela.getNomparcela());

		if ("E".equals(tipoParcela)) {
			int count = 1;
			int div_height = 13;

			if (!parcela.getCapitalAsegurados().isEmpty()) {
				count = parcela.getCapitalAsegurados().size();
			}

			div_height = TEXT_HEIGHT * count;
			String div_style = COLOR_VERDE + div_height + "px";

			value = DIV_STYLE + div_style + "'>" + resultado + DIV;
		} else
			value = resultado;

		return value;
	}

	public String getHojaNum() {
		String resultado = "";
		String value = "";
		String tipoParcela = "";

		Parcela parcela = (Parcela) getCurrentRowObject();

		if (parcela.getTipoparcela() != null)
			tipoParcela = parcela.getTipoparcela().toString();

		resultado = StringUtils.nullToString(parcela.getHoja()) + "-" + StringUtils.nullToString(parcela.getNumero());

		if ("E".equals(tipoParcela)) {
			int count = 1;
			int div_height = 13;

			if (!parcela.getCapitalAsegurados().isEmpty()) {
				count = parcela.getCapitalAsegurados().size();
			}

			div_height = TEXT_HEIGHT * count;
			String div_style = COLOR_VERDE + div_height + "px";

			value = DIV_STYLE + div_style + "'>" + resultado + DIV;
		} else
			value = resultado;

		return value;
	}

	public String getCodtermino() {
		String resultado = "";
		String value = "";
		String tipoParcela = "";

		Parcela parcela = (Parcela) getCurrentRowObject();

		if (parcela.getTipoparcela() != null)
			tipoParcela = parcela.getTipoparcela().toString();

		if (parcela.getTermino() == null && parcela.getTermino().getId() == null) {
			resultado = "";
		} else {
			resultado = StringUtils.nullToString(parcela.getTermino().getId().getCodtermino());
		}

		if ("E".equals(tipoParcela)) {
			int count = 1;
			int div_height = 13;

			if (!parcela.getCapitalAsegurados().isEmpty()) {
				count = parcela.getCapitalAsegurados().size();
			}

			div_height = TEXT_HEIGHT * count;
			String div_style = COLOR_VERDE + div_height + "px";

			value = "<div title='" + parcela.getTermino().getNomtermino() + "' style='" + div_style + "'>" + resultado
					+ DIV;
		} else {
			
			Date fechaInicioContratacion = parcela.getPoliza().getLinea().getFechaInicioContratacion();
			
			// Utiliza el m閠odo getNomTermino(fechaInicioContratacion, esGanado) en lugar del antiguo getNomtermino() para adaptarse a los nuevos requisitos de la P0079469
			// Esta versi髇 ahora tiene en cuenta la fecha de inicio de contrataci髇 y si la l韓ea es de ganado para determinar el nombre correcto del termino
			value = "<div title='" + parcela.getTermino().getNomTerminoByFecha(fechaInicioContratacion, false) + "'>" + resultado + DIV;
		}
		
		return value;

	}

	public String getCodsubtermino() {
		String resultado = "<span>&nbsp</span>";
		String value = "";
		String tipoParcela = "";

		Parcela parcela = (Parcela) getCurrentRowObject();

		if (parcela.getTipoparcela() != null)
			tipoParcela = parcela.getTipoparcela().toString();

		if (parcela.getTermino() == null && parcela.getTermino().getId() == null) {
			resultado = "";
		} else {
			resultado = StringUtils.nullToString(parcela.getTermino().getId().getSubtermino());
		}

		if ("E".equals(tipoParcela)) {
			int count = 1;
			int div_height = 13;

			if (!parcela.getCapitalAsegurados().isEmpty()) {
				count = parcela.getCapitalAsegurados().size();
			}

			div_height = TEXT_HEIGHT * count;
			String div_style = COLOR_VERDE + div_height + "px";

			value = DIV_STYLE + div_style + "'>" + resultado + DIV;
		} else
			value = resultado;

		return value;
	}

	public String getIdCat() {
		String resultado = "";
		String value = "";
		String tipoParcela = "";

		Parcela parcela = (Parcela) getCurrentRowObject();

		if (parcela.getTipoparcela() != null)
			tipoParcela = parcela.getTipoparcela().toString();

		if (parcela.getPoligono() != null && parcela.getParcela() != null) {
			resultado = parcela.getPoligono() + " - " + parcela.getParcela();
		} else {
			resultado = getSigPac(parcela);
		}

		if ("E".equals(tipoParcela)) {
			int count = 1;
			int div_height = 13;

			if (!parcela.getCapitalAsegurados().isEmpty()) {
				count = parcela.getCapitalAsegurados().size();
			}

			div_height = TEXT_HEIGHT * count;

			String div_style = "background-color:#A9F5A9;padding:0;margin:0;height:" + div_height + "px";

			value = DIV_STYLE + div_style + "'>" + resultado + DIV;
		} else
			value = resultado;

		return value;
	}

	public String getIdCat(Parcela parcela) {
		String resultado = "";
		String value = "";

		if (parcela.getPoligono() != null && parcela.getParcela() != null) {
			resultado = parcela.getPoligono() + " - " + parcela.getParcela();
		} else {
			resultado = getSigPac(parcela);
		}

		value = resultado;

		return value;
	}

	public String getCodcultivo() {
		String resultado = "";
		String value = "";
		String tipoParcela = "";

		Parcela parcela = (Parcela) getCurrentRowObject();

		if (parcela.getTipoparcela() != null)
			tipoParcela = parcela.getTipoparcela().toString();

		if (parcela.getCodcultivo() != null)
			resultado = parcela.getCodcultivo().toString();
		else
			resultado = "";

		if ("E".equals(tipoParcela)) {
			int count = 1;
			int div_height = 13;

			if (!parcela.getCapitalAsegurados().isEmpty()) {
				count = parcela.getCapitalAsegurados().size();
			}

			div_height = TEXT_HEIGHT * count;
			String div_style = COLOR_VERDE + div_height + "px";

			value = DIV_STYLE + div_style + "'>" + resultado + DIV;
		} else
			value = resultado;

		return value;
	}

	public String getCodvariedad() {
		String resultado = "";
		String value = "";
		String tipoParcela = "";

		Parcela parcela = (Parcela) getCurrentRowObject();

		if (parcela.getTipoparcela() != null)
			tipoParcela = parcela.getTipoparcela().toString();

		if (parcela.getCodvariedad() != null)
			resultado = parcela.getCodvariedad().toString();
		else
			resultado = "";

		if ("E".equals(tipoParcela)) {
			int count = 1;
			int div_height = 13;

			if (!parcela.getCapitalAsegurados().isEmpty()) {
				count = parcela.getCapitalAsegurados().size();
			}

			div_height = TEXT_HEIGHT * count;
			String div_style = COLOR_VERDE + div_height + "px";

			value = DIV_STYLE + div_style + "'>" + resultado + DIV;
		} else
			value = resultado;

		return value;
	}

	public String getSigPac(Parcela parcela) {
		String resultado = "";

		resultado = StringUtils.nullToString(parcela.getCodprovsigpac());
		resultado += "-" + StringUtils.nullToString(parcela.getCodtermsigpac());
		resultado += "-" + StringUtils.nullToString(parcela.getAgrsigpac());
		resultado += "-" + StringUtils.nullToString(parcela.getZonasigpac());
		resultado += "-" + StringUtils.nullToString(parcela.getPoligonosigpac());
		resultado += "-" + StringUtils.nullToString(parcela.getParcelasigpac());
		resultado += "-" + StringUtils.nullToString(parcela.getRecintosigpac());

		return resultado;
	}

	/**
	 * M閠odo para obtener la identificaci髇 sigpac formateada para poderla ordenar
	 * como String
	 * 
	 * @param pa
	 *            Parcela
	 * @return
	 */
	public static String getSigPacFormateado(Parcela parcela) {
		String resultado = "";

		resultado = parcela.getCodprovsigpac() != null ? String.format("%02d", parcela.getCodprovsigpac().intValue())
				: "";
		resultado += parcela.getCodtermsigpac() != null
				? "-" + String.format("%03d", parcela.getCodtermsigpac().intValue())
				: "";
		resultado += parcela.getAgrsigpac() != null ? "-" + String.format("%03d", parcela.getAgrsigpac().intValue())
				: "";
		resultado += parcela.getZonasigpac() != null ? "-" + String.format("%03d", parcela.getZonasigpac().intValue())
				: "";
		resultado += parcela.getPoligonosigpac() != null
				? "-" + String.format("%03d", parcela.getPoligonosigpac().intValue())
				: "";
		resultado += parcela.getParcelasigpac() != null
				? "-" + String.format("%05d", parcela.getParcelasigpac().intValue())
				: "";
		resultado += parcela.getRecintosigpac() != null
				? "-" + String.format("%05d", parcela.getRecintosigpac().intValue())
				: "";

		return resultado;
	}

	public String getCodprovincia() {
		String value = "";
		String resultado = "";
		String tipoParcela = "";

		Parcela parcela = (Parcela) getCurrentRowObject();

		if (parcela.getTipoparcela() != null)
			tipoParcela = parcela.getTipoparcela().toString();

		if (parcela.getTermino() == null || parcela.getTermino().getId() == null) {
			resultado = "";
		} else {
			resultado = StringUtils.nullToString(parcela.getTermino().getId().getCodprovincia());
		}

		if ("E".equals(tipoParcela)) {
			int count = 1;
			int div_height = 13;

			if (!parcela.getCapitalAsegurados().isEmpty()) {
				count = parcela.getCapitalAsegurados().size();
			}

			div_height = TEXT_HEIGHT * count;
			String div_style = COLOR_VERDE + div_height + "px";
			value = DIV_STYLE + div_style + "'>" + resultado + DIV;
		} else
			value = resultado;

		return value;
	}

	public String getCodcomarca() {
		String value = "";
		String resultado = "";
		String tipoParcela = "";

		Parcela parcela = (Parcela) getCurrentRowObject();

		if (parcela.getTipoparcela() != null)
			tipoParcela = parcela.getTipoparcela().toString();

		if (parcela.getTermino().getComarca().getId() == null) {
			resultado = "";
		} else {
			resultado = parcela.getTermino().getComarca().getId().getCodcomarca().toString();
		}

		if ("E".equals(tipoParcela)) {
			int count = 1;
			int div_height = 13;

			if (!parcela.getCapitalAsegurados().isEmpty()) {
				count = parcela.getCapitalAsegurados().size();
			}

			div_height = TEXT_HEIGHT * count;
			String div_style = COLOR_VERDE + div_height + "px";

			value = DIV_STYLE + div_style + "'>" + resultado + DIV;
		} else
			value = resultado;

		return value;

	}

	public String getTcapital() {
		String value = "";
		String resultado = "";
		String tipoParcela = "";

		Parcela parcela = (Parcela) getCurrentRowObject();

		if (parcela.getTipoparcela() != null)
			tipoParcela = parcela.getTipoparcela().toString();

		Set<CapitalAsegurado> capitales = parcela.getCapitalAsegurados();
		int count = 1;
		int div_height = 13;

		if (!capitales.isEmpty()) {
			for (CapitalAsegurado capital : capitales) {
				String descripcion = capital.getTipoCapital().getDestipocapital();
				// corto la cadena
				if (descripcion.length() > 15)
					descripcion = descripcion.substring(0, 14) + ".";

				resultado += descripcion + BR;
				count++;
			}
		}

		div_height = TEXT_HEIGHT * count;
		String div_style = COLOR_VERDE + div_height + "px";

		if ("E".equals(tipoParcela))
			value = DIV_STYLE + div_style + "'>" + resultado + DIV;
		else
			value = resultado;

		return value;
	}

	public String getTcapital(CapitalAsegurado capital) {
		String resultado = "";

		if (capital != null && capital.getTipoCapital() != null) {
			resultado = capital.getTipoCapital().getDestipocapital();
		}
		return resultado;

	}

	public String getPrecio() {
		String resultado = "";
		String value = "";
		String tipoParcela = "";

		Parcela parcela = (Parcela) getCurrentRowObject();

		if (parcela.getTipoparcela() != null)
			tipoParcela = parcela.getTipoparcela().toString();

		/** SONAR Q ** MODIF TAM(03.11.2021) * Inicio */
		/* A馻dimos nuevo m閠odo para descargar de ifs/for */
		resultado = obtenerResultado(parcela);
		/** SONAR Q ** MODIF TAM(03.11.2021) * Fin */

		if ("E".equals(tipoParcela)) {
			int count = 1;
			int div_height = 13;

			if (!parcela.getCapitalAsegurados().isEmpty()) {
				count = parcela.getCapitalAsegurados().size();
			}

			div_height = TEXT_HEIGHT * count;
			String div_style = COLOR_VERDE + div_height + "px";
			value = DIV_STYLE + div_style + "'>" + resultado + DIV;
		} else
			value = resultado;

		return value;
	}

	public String getPrecio(CapitalAsegurado capital) {
		String resultado = "";

		Float precioMax = new Float(0);
		// recuperamos la relacion de capital-modulo para obtener el precio
		// maximo de todos los modulos seleccionados
		for (int i = 0; i < capital.getCapAsegRelModulos().size(); i++) {
			CapAsegRelModulo capAsegRelMod = (CapAsegRelModulo) capital.getCapAsegRelModulos().toArray()[i];
			if (capAsegRelMod.getPrecio() != null) {
				if (capAsegRelMod.getPrecio().floatValue() > precioMax) {
					precioMax = capAsegRelMod.getPrecio().floatValue();
				}
			}
		}

		if (precioMax != null && !precioMax.equals(new Float(0))) {
			resultado = precioMax.toString();
		}

		return resultado;
	}

	public String getProduccion() {
		String resultado = "";
		String value = "";
		String tipoParcela = "";

		Parcela parcela = (Parcela) getCurrentRowObject();

		if (parcela.getTipoparcela() != null)
			tipoParcela = parcela.getTipoparcela().toString();

		Set<CapitalAsegurado> capitales = parcela.getCapitalAsegurados();
		for (CapitalAsegurado capital : capitales) {
			Long produccionMax = new Long(0);
			Long tipoRendimiento = new Long(0);
			// recuperamos la relacion de capital-modulo para obtener el precio
			// maximo de todos los modulos seleccionados
			for (int i = 0; i < capital.getCapAsegRelModulos().size(); i++) {
				CapAsegRelModulo capAsegRelMod = (CapAsegRelModulo) capital.getCapAsegRelModulos().toArray()[i];
				if (capAsegRelMod.getProduccion() != null) {
					if (capAsegRelMod.getProduccion().longValue() > produccionMax) {
						produccionMax = capAsegRelMod.getProduccion().longValue();
					}
					
					/** SONAR Q ** MODIF TAM(03.11.2021) * Inicio */
					/* A馻dimos nuevo m閠odo para descargar de ifs/for */
					tipoRendimiento = obtenerTipoRdto(capAsegRelMod);
					/** SONAR Q ** MODIF TAM(03.11.2021) * Fin */				
				}

			}
			if (produccionMax == null || produccionMax.equals(new Long(0))) {
				resultado += BR;
			} else {
				resultado += produccionMax.toString() + BR;
			}

			/** SONAR Q ** MODIF TAM(03.11.2021) * Inicio */
			/* A馻dimos nuevo m閠odo para descargar de ifs/for */
			value = obtenerValue(tipoParcela, parcela, resultado);
			/** SONAR Q ** MODIF TAM(03.11.2021) * Inicio */
			
			//ESC-30568 / GD-18421
			if (tipoRendimiento.equals(Constants.TIPO_RDTO_HISTORICO)) {
				value = "<div id='tipProd' style='color:#3C74CB'>" + resultado + DIV;
			} else if (tipoRendimiento.equals(
					Constants.TIPO_RDTO_HISTORICO_SIN_ACTUALIZAR)) {
				value = "<div id='tipProd' style='color:#EA192E'>" + resultado + DIV;
				/* Pet. 78877 ** MODIF TAM (10.11.2021) ** Inicio */
			} else if (tipoRendimiento.equals(
					Constants.TIPO_RDTO_SIN_RENDIMIENTO_ASIGNADO)) {
				value = "<div id='tipProd' style='color:#48AB48'>" + resultado + DIV;
			}
			//ESC-30568 / GD-18421
			
			/* Pet. 78877 ** MODIF TAM (10.11.2021) ** Fin */

		}
		return value;
	}

	/**
	 * @param resultado
	 * @param capital
	 * @return
	 */
	public String getProduccion(CapitalAsegurado capital) {
		String resultado = "";
		Long produccionMax = new Long(0);
		// recuperamos la relacion de capital-modulo para obtener el precio
		// maximo de todos los modulos seleccionados
		for (int i = 0; i < capital.getCapAsegRelModulos().size(); i++) {
			CapAsegRelModulo capAsegRelMod = (CapAsegRelModulo) capital.getCapAsegRelModulos().toArray()[i];
			if (capAsegRelMod.getProduccion() != null) {
				if (capAsegRelMod.getProduccion().longValue() > produccionMax) {
					produccionMax = capAsegRelMod.getProduccion().longValue();
				}
			}
		}

		if (produccionMax != null && !produccionMax.equals(new Long(0))) {
			resultado = produccionMax.toString();
		}

		return resultado;
	}

	public String getSuperf(CapitalAsegurado capital) {
		return (capital.getSuperficie() != null) ? capital.getSuperficie().toString() : "";
	}

	public String getSuperf() {
		String resultado = "";
		String value = "";
		Character tipoParcela = null;

		Parcela parcela = (Parcela) getCurrentRowObject();

		if (parcela.getTipoparcela() != null)
			tipoParcela = parcela.getTipoparcela();

		Set<CapitalAsegurado> capitales = parcela.getCapitalAsegurados();

		// ------- METROS CUADRADOS/METROS LISOS -------
		if (Constants.TIPO_PARCELA_INSTALACION.equals(tipoParcela)) {
			/** SONAR Q ** MODIF TAM(03.11.2021) * Inicio */
			/* A馻dimos nuevo m閠odo para descargar de ifs/for */
			resultado = obtenerResulMetros(capitales);
			/** SONAR Q ** MODIF TAM(03.11.2021) * Fin */
		}
		// ------- SUPERFICIE -------
		else {
			for (CapitalAsegurado capital : capitales) {
				if (capital.getSuperficie() == null) {
					resultado = BR;
				} else {
					resultado += capital.getSuperficie() + BR;
				}
			}
		}

		/** SONAR Q ** MODIF TAM(03.11.2021) * Inicio */
		/* A馻dimos nuevo m閠odo para descargar de ifs/for */
		value = obtenerVal(tipoParcela, parcela, resultado);
		/** SONAR Q ** MODIF TAM(03.11.2021) * Fin */
		
		return value;
	}

	public String getNomPar() {
		String value = "<span>&nbsp</span>";
		String tipoParcela = "";
		int count = 1;
		int div_height = 13;
		String div_style = "";

		Parcela parcela = (Parcela) getCurrentRowObject();

		// Calculo altura DIV
		if (!parcela.getCapitalAsegurados().isEmpty()) {
			count = parcela.getCapitalAsegurados().size();
		}

		div_height = TEXT_HEIGHT * count;
		div_style = "background-color:#A9F5A9;padding:0;margin:0;height:" + div_height + "px";

		// Tipo parcela
		if (parcela.getTipoparcela() != null)
			tipoParcela = parcela.getTipoparcela().toString();

		if (parcela.getNomparcela() != null) {
			if ("E".equals(tipoParcela)) {
				value = DIV_STYLE + div_style + "'>" + parcela.getNomparcela() + DIV;
			} else {
				value = parcela.getNomparcela();
			}
		}

		if (parcela.getNomparcela() == null && "E".equals(tipoParcela)) {
			value = DIV_STYLE + div_style + "'>" + value + DIV;
		}

		return value;

	}

	public String getNumero() {
		String resultado = "";
		String value = "";
		String hoja = "";
		String numero = "";
		String tipoParcela = "";

		Parcela parcela = (Parcela) getCurrentRowObject();

		if (parcela.getTipoparcela() != null)
			tipoParcela = parcela.getTipoparcela().toString();

		if (parcela.getNumero() != null)
			numero = parcela.getNumero().toString();
		if (parcela.getHoja() != null)
			hoja = parcela.getHoja().toString();

		resultado = hoja + " - " + numero;

		if ("E".equals(tipoParcela))
			value = DIV_STYLE + STYLE_DIV + "'>" + resultado + DIV;
		else
			value = resultado;

		return value;
	}

	public String getFechaFin() {
		String resultado = "";
		Parcela parcela = (Parcela) getCurrentRowObject();
		Set<CapitalAsegurado> capitales = parcela.getCapitalAsegurados();

		// ------- Fecha Fin Garantias de Parcelas y Todas-------
		if (parcela.getTipoparcela().equals('P')) {
			for (CapitalAsegurado capital : capitales) {
				// Busco entre los datos variables

				String fecha = "";

				// Fecha fin garantia --> cod.concepto:134
				for (DatoVariableParcela datovar : capital.getDatoVariableParcelas()) {
					if (datovar.getDiccionarioDatos().getCodconcepto().toString().equals("134")) {
						fecha = datovar.getValor();
					}
				}

				if (fecha.equals("")) {
					resultado = BR;
				} else {
					resultado += fecha + BR;
				}
			}
		}

		return resultado;
	}

	public String getFechaFin(CapitalAsegurado capital) {
		String resultado = "";

		// Fecha fin garantia --> cod.concepto:134
		for (DatoVariableParcela datovar : capital.getDatoVariableParcelas()) {
			if (datovar.getDiccionarioDatos().getCodconcepto()
					.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_FEC_FIN_GARANT))) {
				resultado = datovar.getValor();
				break;
			}
		}

		return resultado;
	}

	public String getNumUnidades(CapitalAsegurado capital) {
		String resultado = "0";

		// Busco entre los datos variables
		for (DatoVariableParcela datovar : capital.getDatoVariableParcelas()) {
			if (datovar.getDiccionarioDatos().getCodconcepto()
					.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_NUMARBOLES))) {
				resultado = datovar.getValor();
				break;
			}
		}

		return resultado;
	}

	public String getSistemaCultivo(CapitalAsegurado capital) {
		String resultado = null;

		// Busco entre los datos variables
		for (DatoVariableParcela datovar : capital.getDatoVariableParcelas()) {
			if (datovar.getDiccionarioDatos().getCodconcepto()
					.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_SISTCULTIVO))) {
				resultado = datovar.getValor();
				break;
			}
		}

		return resultado;
	}

	public String getSistemaConduccion(CapitalAsegurado capital) {
		String resultado = null;

		// Busco entre los datos variables
		for (DatoVariableParcela datovar : capital.getDatoVariableParcelas()) {
			if (datovar.getDiccionarioDatos().getCodconcepto()
					.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_SISTCOND))) {
				resultado = datovar.getValor();
				break;
			}
		}

		return resultado;
	}

	public String getCheckCambioMasivo() {
		String result;
		String tipoParcela = "";
		Parcela parcela = (Parcela) getCurrentRowObject();

		if (parcela.getTipoparcela() != null)
			tipoParcela = parcela.getTipoparcela().toString();

		// si no es parcela(P) lo deshabilito
		if ("P".equals(tipoParcela)) {
			result = "<input type=\"checkbox\" id=\"checkParcela_" + parcela.getIdparcela() + "\"  name=\"checkParcela_"
					+ parcela.getIdparcela() + "\" onClick =\"onClickInCheck2( \'checkParcela_" + parcela.getIdparcela()
					+ "')\" class=\"dato\"/>";
		} else {
			result = "<input type=\"checkbox\" id=\"checkParcela_" + parcela.getIdparcela() + "\"  name=\"checkParcela_"
					+ parcela.getIdparcela() + "\" onClick =\"onClickInCheck2( \'checkParcela_" + parcela.getIdparcela()
					+ "')\" class=\"dato\" disabled=\"false\"/>";
		}

		return result;
	}

	// DAA 31/01/2013 Ordenacion por defecto del displayTag
	public String getOrdenacion() {
		Parcela parcela = (Parcela) getCurrentRowObject();
		Set<CapitalAsegurado> capitales = parcela.getCapitalAsegurados();
		String codTCapital = "0";
		Character subtermino = ' ';

		String div_style = "display:none";
		String invisible = DIV_STYLE + div_style + "'>";
		invisible += StringUtils.nullToString(parcela.getTermino().getId().getCodprovincia()) + "-"
				+ StringUtils.nullToString(parcela.getTermino().getComarca().getId().getCodcomarca()) + "-"
				+ StringUtils.nullToString(parcela.getTermino().getId().getCodtermino()) + "-";
		// Subtermino
		if (parcela.getTermino().getId().getSubtermino() != null) {
			subtermino = parcela.getTermino().getId().getSubtermino();
		}

		if (!Character.isDigit(subtermino)) { // si es una letra
			try {
				byte[] b = subtermino.toString().getBytes("US-ASCII");
				invisible += b[0] + "-";
			} catch (UnsupportedEncodingException e) {
				logger.error("Excepcion : ModelTableDecoratorListaParcelasTodas - getOrdenacion", e);
			}
		} else {
			invisible += subtermino + "-";
		}

		invisible += StringUtils.nullToString(parcela.getCodcultivo()) + "-"
				+ StringUtils.nullToString(parcela.getCodvariedad()) + "-"
				+ ModelTableDecoratorListaParcelasTodas.getSigPacFormateado(parcela) + "-";
		// T.Capital
		if (!capitales.isEmpty()) {
			for (CapitalAsegurado capital : capitales) {
				codTCapital = capital.getTipoCapital().getCodtipocapital().toString();
				break;
			}
		}

		codTCapital = convierteCodTCapital(codTCapital);

		invisible += codTCapital + DIV;

		return invisible;
	}

	/* Convierte el String donde esta el tipo capital a 3 cifras */
	private String convierteCodTCapital(String codTCapital) {
		String tipoCapitalNuevo;

		switch (codTCapital.length()) {
		case 1:
			tipoCapitalNuevo = "00" + codTCapital;
			break;
		case 2:
			tipoCapitalNuevo = "0" + codTCapital;
			break;
		default:
			tipoCapitalNuevo = codTCapital;
		}
		return tipoCapitalNuevo;
	}
	
	
	/****/
	/** SONAR Q ** MODIF TAM(03.11.2021) **/
	/* A馻dimos nuevo m閠odo para descargar de ifs/for */
	private String obtenerValue(String tipoParcela, Parcela parcela, String acciones) {
		String value ="";
		
		if ("E".equals(tipoParcela)) {
			int count = 1;
			int div_height = 13;
	
			if (!parcela.getCapitalAsegurados().isEmpty()) {
				count = parcela.getCapitalAsegurados().size();
			}
	
			div_height = TEXT_HEIGHT * count;
			String div_style = COLOR_VERDE + div_height + "px";
	
			value = DIV_STYLE + div_style + "'>" + acciones + DIV;
		} else {
			value = acciones;
		}
		return value;
	}
	
	private String obtenerResultado(Parcela parcela) {
	
		Set<CapitalAsegurado> capitales = parcela.getCapitalAsegurados();
		String resultado = "";
	
		for (CapitalAsegurado capital : capitales) {
			Float precioMax = new Float(0);
			// recuperamos la relacion de capital-modulo para obtener el precio
			// maximo de todos los modulos seleccionados
			for (int i = 0; i < capital.getCapAsegRelModulos().size(); i++) {
				CapAsegRelModulo capAsegRelMod = (CapAsegRelModulo) capital.getCapAsegRelModulos().toArray()[i];
				if (capAsegRelMod.getPrecio() != null) {
					if (capAsegRelMod.getPrecio().floatValue() > precioMax) {
						precioMax = capAsegRelMod.getPrecio().floatValue();
					}
				}
			}
	
			if (precioMax == null || precioMax.equals(new Float(0))) {
				resultado = BR;
			} else {
				resultado += precioMax.toString() + BR;
			}
		}
		return resultado;
	}	
	
	/** SONAR Q ** MODIF TAM(03.11.2021) * Inicio */
	/* A馻dimos nuevo m閠odo para descargar de ifs/for */
	private Long obtenerTipoRdto(CapAsegRelModulo capAsegRelMod) {
	
		Long tipoRdto = new Long(0);
		if (null != capAsegRelMod.getTipoRdto()
				&& Constants.TIPO_RDTO_HISTORICO.equals(capAsegRelMod.getTipoRdto())) {
			tipoRdto = Constants.TIPO_RDTO_HISTORICO;
		} else if (null != capAsegRelMod.getTipoRdto()
				&& Constants.TIPO_RDTO_HISTORICO_SIN_ACTUALIZAR.equals(capAsegRelMod.getTipoRdto())) {
			tipoRdto = Constants.TIPO_RDTO_HISTORICO_SIN_ACTUALIZAR;
		/* P0078877 ** MODIF TAM (26.10.2021) ** Inicio */
		} else if (null != capAsegRelMod.getTipoRdto()
				&& Constants.TIPO_RDTO_SIN_RENDIMIENTO_ASIGNADO.equals(capAsegRelMod.getTipoRdto())) {
			tipoRdto = Constants.TIPO_RDTO_SIN_RENDIMIENTO_ASIGNADO;
		}
		/* P0078877 ** MODIF TAM (26.10.2021) ** Fin */
		return tipoRdto;
	}
	
	private String obtenerResulMetros(Set<CapitalAsegurado> capitales) {
		String metros = "";
		String resultado = "";
		
		// Busco entre los datos variables metros cuadrados y metros lineales
		for (CapitalAsegurado capital : capitales) {
			// metros cuadrados --> cod.concepto:767
			for (DatoVariableParcela datovar : capital.getDatoVariableParcelas()) {
				if (datovar.getDiccionarioDatos().getCodconcepto()
						.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_METROS_CUADRADOS))) {
					metros = datovar.getValor();
				} else if (datovar.getDiccionarioDatos().getCodconcepto()
						.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_METROS_LINEALES))) {
					metros = datovar.getValor();
				}
			}
			
			if (StringUtils.isNullOrEmpty(metros)) {
				resultado = BR;
			} else {
				resultado += metros + BR;
			}
		}
		return resultado;
	}	
	
	/** SONAR Q ** MODIF TAM(03.11.2021) * Inicio */
	/* A馻dimos nuevo m閠odo para descargar de ifs/for */
	private String obtenerVal(Character tipoParcela, Parcela parcela, String resultado) {
		String value = "";
		
		if (Constants.TIPO_PARCELA_INSTALACION.equals(tipoParcela)) {
			int count = 1;
			int div_height = 13;
			resultado = "";
			
			if (!parcela.getCapitalAsegurados().isEmpty()) {
				for (CapitalAsegurado capital : parcela.getCapitalAsegurados()) {
					if (capital.getSuperficie() == null) {
						resultado = BR;
					} else {
						resultado += capital.getSuperficie();
					}
					count++;
				}
			}

			div_height = TEXT_HEIGHT * count;
			String div_style = COLOR_VERDE + div_height + "px";

			value = DIV_STYLE + div_style + "'>" + resultado + DIV;
		} else {
			value = resultado;
		}
			
		return value;
	}	
	
	/** SONAR Q ** MODIF TAM(03.11.2021) * Fin */
}