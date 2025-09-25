package com.rsi.agp.core.decorators;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;
import com.rsi.agp.dao.tables.anexo.CapitalDTSVariable;
import com.rsi.agp.dao.tables.anexo.Parcela;

/**
 * 
 * @author u028827 Listado de parcelas y sus instalaciones de un anexo. Las
 *         instalaciones deben ir en diferente color
 */
public class ModelTableDecoratorParcelasModificadas extends TableDecorator {

	private static final Log logger = LogFactory.getLog(ModelTableDecoratorParcelasModificadas.class);

	/** CONSTANTES SONAR Q ** MODIF TAM (03.11.2021) ** Inicio **/
	private static final String DIV = "</div>";
	private final static String VACIO = "";
	/** CONSTANTES SONAR Q ** MODIF TAM (03.11.2021) ** Fin **/

	public String getAdmActions() {
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		String tipoParcela = capitalAsegurado.getParcela().getTipoparcela().toString();

		// Variables auxiliares para las acciones
		String transparente = "<img src=\"jsp/img/displaytag/transparente.gif\"  style=\"width:16px;height:16px\" />";
		String deshacer = "<a href=\"#\" onclick=\"javascript:deshacerCambiosParcela('"
				+ capitalAsegurado.getParcela().getId() + "')\">"
				+ "<img src=\"jsp/img/displaytag/deshacer.png\" alt=\"Deshacer\" title=\"Deshacer\"/></a>";
		String editar = "<a href=\"#\" onclick=\"javascript:editarParcela("
				+ capitalAsegurado.getParcela().getAnexoModificacion().getPoliza().getIdpoliza() + ","
				+ capitalAsegurado.getParcela().getId() + ")\">"
				+ "<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/></a>&nbsp;";
		String altaInstalacion = "<a href=\"javascript:altaEstructuraAnexoParcela('"
				+ capitalAsegurado.getParcela().getAnexoModificacion().getPoliza().getIdpoliza() + "','"
				+ capitalAsegurado.getParcela().getId() + "')\">"
				+ "<img src=\"jsp/img/displaytag/instalaciones.jpg\" alt=\"Alta instalacion\" title=\"Alta instalacion\" width=\"16\" height=\"16\" /></a>&nbsp;";
		String eliminar = "<a href=\"#\" onclick=\"javascript:eliminarParcela("
				+ capitalAsegurado.getParcela().getAnexoModificacion().getPoliza().getIdpoliza() + ","
				+ capitalAsegurado.getParcela().getId() + ")\">"
				+ "<img src=\"jsp/img/displaytag/delete.png\" alt=\"Eliminar\" title=\"Eliminar\"/></a>";
		String visualizar = "<a href=\"#\" onclick=\"javascript:visualizarDatosRegistro('"
				+ capitalAsegurado.getParcela().getTipoparcela() + "'," + capitalAsegurado.getParcela().getId() + ")\">"
				+ "<img src=\"jsp/img/displaytag/information.png\" alt=\"Visualizar informacion\" title=\"Visualizar informacion\"/></a>";

		String acciones = "";
		boolean edito = true;
		BigDecimal estadoAnexo = capitalAsegurado.getParcela().getAnexoModificacion().getEstado().getIdestado();
		Long estadoCupon = null;
		if (capitalAsegurado.getParcela().getAnexoModificacion().getCupon() != null) {
			estadoCupon = capitalAsegurado.getParcela().getAnexoModificacion().getCupon().getEstadoCupon().getId();
		}

		// solo se permite edicion en el estado borrador y error
		if (!estadoAnexo.equals(Constants.ANEXO_MODIF_ESTADO_ENVIADO)
				&& !estadoAnexo.equals(Constants.ANEXO_MODIF_ESTADO_CORRECTO)) {
			
			/** SONAR Q ** MODIF TAM(03.11.2021) **/
			/* Añadimos nuevo método para descargar de ifs/for */
			acciones = obtenerAcciones(capitalAsegurado, transparente, deshacer, altaInstalacion, 
										editar, visualizar, tipoParcela, edito, eliminar, estadoCupon);
			/** SONAR Q ** MODIF TAM(03.11.2021) **/
		} else {
			acciones = visualizar + transparente + transparente + transparente;
		}
		// localizacion: @@prov;;comarca;;termino;;subtermino@@
		String localizacion = "@@" + capitalAsegurado.getParcela().getCodprovincia() + ";;"
				+ capitalAsegurado.getParcela().getCodcomarca() + ";;" + capitalAsegurado.getParcela().getCodtermino()
				+ ";;" + getSubterminoParcela(capitalAsegurado.getParcela()) + "@@";
		acciones += "<input type='hidden' name='localizacion_cm' id='localizacion_cm' value='" + localizacion + "'/>";
		return acciones;
	}

	private String getSubterminoParcela(Parcela parcela) {
		if (parcela.getCodtermino() == null) {
			return "";
		} else {
			return StringUtils.nullToString(parcela.getSubtermino());
		}
	}

	public String getHojaNumero() {
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		String result = "";

		if (capitalAsegurado.getParcela().getHoja() != null && capitalAsegurado.getParcela().getNumero() != null)
			result = capitalAsegurado.getParcela().getHoja() + "-" + capitalAsegurado.getParcela().getNumero();

		return result;
	}

	public String getNombre() {
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		return StringUtils.nullToString(capitalAsegurado.getParcela().getNomparcela());
	}

	public String getHojaNum() {
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		return StringUtils.nullToString(capitalAsegurado.getParcela().getHoja()) + "-"
				+ StringUtils.nullToString(capitalAsegurado.getParcela().getNumero());
	}

	public String getCodtermino() {
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		if (capitalAsegurado.getParcela().getCodtermino() == null)
			return "null";
		else
			return StringUtils.nullToString(capitalAsegurado.getParcela().getCodtermino());
	}

	public String getCodsubtermino() {
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		if (capitalAsegurado.getParcela().getSubtermino() == null)
			return "null";
		else
			return StringUtils.nullToString(capitalAsegurado.getParcela().getSubtermino());
	}

	public String getCodcultivo() {
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		if (capitalAsegurado.getParcela().getCodcultivo() == null)
			return "null";
		else
			return StringUtils.nullToString(capitalAsegurado.getParcela().getCodcultivo());
	}

	public String getCodvariedad() {
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		if (capitalAsegurado.getParcela().getCodvariedad() == null)
			return "null";
		else
			return StringUtils.nullToString(capitalAsegurado.getParcela().getCodvariedad());
	}

	public String getIdCat() {
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		if (capitalAsegurado.getParcela().getPoligono() != null && capitalAsegurado.getParcela().getParcela_1() != null)
			return capitalAsegurado.getParcela().getPoligono() + " - " + capitalAsegurado.getParcela().getParcela_1();
		else
			return getSigPac(capitalAsegurado.getParcela());
	}

	public String getSigPac(Parcela pa) {
		String sigPac = "";
		if (pa.getCodprovsigpac() != null)
			sigPac = pa.getCodprovsigpac().toString();
		if (pa.getCodtermsigpac() != null)
			sigPac += "-" + pa.getCodtermsigpac().toString();
		if (pa.getAgrsigpac() != null)
			sigPac += "-" + pa.getAgrsigpac().toString();
		if (pa.getZonasigpac() != null)
			sigPac += "-" + pa.getZonasigpac().toString();
		if (pa.getPoligonosigpac() != null)
			sigPac += "-" + pa.getPoligonosigpac().toString();
		if (pa.getParcelasigpac() != null)
			sigPac += "-" + pa.getParcelasigpac().toString();
		if (pa.getRecintosigpac() != null)
			sigPac += "-" + pa.getRecintosigpac().toString();
		return sigPac;
	}

	public String getCodprovincia() {
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		if (capitalAsegurado.getParcela().getCodprovincia() == null)
			return "null";
		else
			return StringUtils.nullToString(capitalAsegurado.getParcela().getCodprovincia());
	}

	public String getCodcomarca() {
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		if (capitalAsegurado.getParcela().getCodcomarca() == null)
			return "null";
		else
			return capitalAsegurado.getParcela().getCodcomarca().toString();
	}

	public String getTcapital() {
		String resultado = "";
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();

		if (capitalAsegurado.getTipoCapital().getDestipocapital() != null)
			resultado += capitalAsegurado.getTipoCapital().getDestipocapital();
		else
			resultado += "";

		return resultado;
	}

	public String getPrecio() {
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		String result = "";

		if (capitalAsegurado.getProduccion() != null) {
			result = capitalAsegurado.getPrecio().toString();
		}
		return result;

	}

	public String getProduccion() {

		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		String result = "";
		if (capitalAsegurado.getProduccion() != null) {
			result = capitalAsegurado.getProduccion().toString();
			if (null != capitalAsegurado.getTipoRdto()
					&& Constants.TIPO_RDTO_HISTORICO.equals(capitalAsegurado.getTipoRdto())) {
				result = "<div style='color:#3C74CB'>" + result + DIV;
			} else if (null != capitalAsegurado.getTipoRdto()
					&& Constants.TIPO_RDTO_HISTORICO_SIN_ACTUALIZAR.equals(capitalAsegurado.getTipoRdto())) {
				result = "<div style='color:#EA192E'>" + result + DIV;
			/* Pet. 78877 ** MODIF TAM (26.10.2021) ** Inicio */
			} else if (null != capitalAsegurado.getTipoRdto()
					&& Constants.TIPO_RDTO_SIN_RENDIMIENTO_ASIGNADO.equals(capitalAsegurado.getTipoRdto())) {
				result = "<div style='color:#48AB48'>" + result + DIV;
			}
		/* Pet. 78877 ** MODIF TAM (26.10.2021) ** Fin */
		}
		return result;

	}

	public String getSuperf() {
		String resultado = "";
		String tipoParcela = "";
		boolean isMetrosCuadrados = false;

		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();

		if (capitalAsegurado.getParcela().getTipoparcela() != null)
			tipoParcela = capitalAsegurado.getParcela().getTipoparcela().toString();

		// ------- METROS CUADRADOS/METROS LISOS -------
		if ("E".equals(tipoParcela)) {
			// Busco entre los datos variables metros cuadrados y metros lineales

			String metros = "";

			// metros cuadrados --> cod.concepto:767
			for (CapitalDTSVariable datovar : capitalAsegurado.getCapitalDTSVariables()) {
				if (datovar.getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_METROS_CUADRADOS))) {
					metros = datovar.getValor();
					isMetrosCuadrados = true;
					break;
				}
			}

			if (!isMetrosCuadrados) {
				/** SONAR Q ** MODIF TAM(03.11.2021) * Inicio */
				/* Añadimos nuevo método para descargar de ifs/for */
				metros = obtenerMetros(capitalAsegurado);
				/** SONAR Q ** MODIF TAM(03.11.2021) * Fin */
			}

			resultado = metros;
		} else {
			// SUPERFICIE
			resultado = StringUtils.nullToString(capitalAsegurado.getSuperficie());
		}

		return resultado;
	}

	/**
	 * Igual que el getSuperf(), pero preparado para el Jasper
	 * 
	 * @param capitalAsegurado
	 * @return
	 */
	public BigDecimal getSuperf(CapitalAsegurado capitalAsegurado) {
		BigDecimal resultado = null;
		;
		String tipoParcela = "";
		boolean isMetrosCuadrados = false;

		if (capitalAsegurado.getParcela().getTipoparcela() != null)
			tipoParcela = capitalAsegurado.getParcela().getTipoparcela().toString();

		// ------- METROS CUADRADOS/METROS LISOS -------
		if ("E".equals(tipoParcela)) {
			// Busco entre los datos variables metros cuadrados y metros lineales

			String metros = "";

			// metros cuadrados --> cod.concepto:767
			for (CapitalDTSVariable datovar : capitalAsegurado.getCapitalDTSVariables()) {
				if (datovar.getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_METROS_CUADRADOS))) {
					metros = datovar.getValor();
					isMetrosCuadrados = true;
					break;
				}
			}

			if (!isMetrosCuadrados) {
				// metros lineales --> cod.concepto:766
				for (CapitalDTSVariable datovar : capitalAsegurado.getCapitalDTSVariables()) {
					if (datovar.getCodconcepto()
							.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_METROS_LINEALES))) {
						metros = datovar.getValor();
						break;
					}
				}
			}
			if (metros != null && !VACIO.equals(metros)) {
				resultado = new BigDecimal(metros);
			}

		} else {
			// SUPERFICIE
			resultado = capitalAsegurado.getSuperficie();
		}

		return resultado;
	}

	public String getEstado() {
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		String resultado = "";
		if (capitalAsegurado.getParcela().getTipomodificacion() != null)
			resultado = capitalAsegurado.getParcela().getTipomodificacion().toString();

		return resultado;
	}

	public String getColumnaCheck() {
		String value = null;
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		BigDecimal estadoAnexo = capitalAsegurado.getParcela().getAnexoModificacion().getEstado().getIdestado();

		// deshabilito check si es estado "Enviado" o "Enviado correcto"
		if (estadoAnexo.equals(Constants.ANEXO_MODIF_ESTADO_ENVIADO)
				|| estadoAnexo.equals(Constants.ANEXO_MODIF_ESTADO_CORRECTO)) {
			value = "<input type=\"checkbox\" id=\"checkParcela_" + capitalAsegurado.getParcela().getId()
					+ "\" name=\"checkParcela_" + capitalAsegurado.getParcela().getId()
					+ "\" class=\"dato\" disabled=\"true\" onClick =\"onClickInCheck('checkParcela_"
					+ capitalAsegurado.getParcela().getId() + "')\" />";
		} else {
			value = "<input type=\"checkbox\" id=\"checkParcela_" + capitalAsegurado.getId() + "\" name=\"checkParcela_"
					+ capitalAsegurado.getParcela().getId()
					+ "\" class=\"dato\" onclick =\"onClickInCheck('checkParcela_"
					+ capitalAsegurado.getParcela().getId() + "', 'checkParcela_" + capitalAsegurado.getId()
					+ "')\" />";
		}

		return value;
	}

	public String getOrdenacion() {
		// Parcela parcela = (Parcela) getCurrentRowObject();
		CapitalAsegurado cap = (CapitalAsegurado) getCurrentRowObject();
		Parcela parcela = cap.getParcela();
		Set<CapitalAsegurado> capitales = parcela.getCapitalAsegurados();
		String codTCapital = "0";
		Character subtermino = ' ';

		String div_style = "display:none";
		String invisible = "<div style='" + div_style + "'>";
		invisible += StringUtils.nullToString(String.format("%02d", parcela.getCodprovincia().intValue())) + "-"
				+ StringUtils.nullToString(String.format("%02d", parcela.getCodcomarca().intValue())) + "-"
				+ StringUtils.nullToString(String.format("%03d", parcela.getCodtermino().intValue())) + "-";
		// Subtermino
		if (parcela.getSubtermino() != null) {
			subtermino = parcela.getSubtermino();
		}

		if (!Character.isDigit(subtermino)) { // si es una letra
			try {
				byte[] b = subtermino.toString().getBytes("US-ASCII");
				invisible += b[0] + "-";
			} catch (UnsupportedEncodingException e) {
				logger.error("Excepcion : ModelTableDecoratorParcelasModificadas - getOrdenacion", e);
			}
		} else {
			invisible += subtermino + "-";
		}
		// String.format("%03d", parcela.getCodtermino())
		invisible += StringUtils.nullToString(String.format("%03d", parcela.getCodcultivo().intValue())) + "-" +

				StringUtils.nullToString(String.format("%03d", parcela.getCodvariedad().intValue())) + "-"
				+ ModelTableDecoratorParcelasModificadas.getSigPacFormateado(parcela) + "-";
		// T.Capital
		if (!capitales.isEmpty()) {
			for (CapitalAsegurado capital : capitales) {
				codTCapital = capital.getTipoCapital().getCodtipocapital().toString();
				break;
			}
		}
		invisible += codTCapital + DIV;

		return invisible;
	}

	/**
	 * Método para obtener la identificación sigpac formateada para poderla ordenar
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
	
	
	/** SONAR Q ** MODIF TAM(03.11.2021) **/
	/** Nuevos métodos para descargar de ifs/for otros métodos **/
	private String obtenerAcciones(CapitalAsegurado capAsegurado, String transparente, 
								   String deshacer, String altaInstalacion, String  editar, 
								   String visualizar, String tipoParc, boolean edito, String eliminar,
								   Long estadoCupon) {
		
		String acciones = "";
		if (estadoCupon != null) {
			if (estadoCupon.equals(Constants.AM_CUPON_ESTADO_CADUCADO))
				edito = false;
		}
	
		if (edito) {
			
			if ("B".equals("" + capAsegurado.getParcela().getTipomodificacion())) {
				// BAJA
				acciones = transparente + transparente + transparente + deshacer;
			}else if ("M".equals("" + capAsegurado.getParcela().getTipomodificacion())) {
			// MODIFICACION
				if ("P".equals(tipoParc)) {
					acciones = editar + transparente + altaInstalacion + deshacer;
				} else {
					acciones = editar + transparente + transparente + deshacer;
				}
			} else if ("A".equals("" + capAsegurado.getParcela().getTipomodificacion())) {
				// ALTA
				if ("P".equals(tipoParc)) {
					acciones = editar + transparente + altaInstalacion + deshacer;
				} else {
					acciones = editar + transparente + transparente + deshacer;
				}
			} else {
				// null - sin estado
				if ("P".equals(tipoParc)) {
					acciones = editar + eliminar + altaInstalacion + transparente;
				} else {
					acciones = editar + eliminar + transparente + transparente;
				}
			}
		} else {
			acciones = visualizar + transparente + transparente + transparente;
		}
		return acciones;
	}
	
	private String  obtenerMetros(CapitalAsegurado capAsegurado) {
		
		String metros = "";
	
		// metros lineales --> cod.concepto:766
		for (CapitalDTSVariable datovar : capAsegurado.getCapitalDTSVariables()) {
			if (datovar.getCodconcepto()
					.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_METROS_LINEALES))) {
				metros = datovar.getValor();
				return metros;
			}
		}
		return metros;
	}
			

/*****/

}
