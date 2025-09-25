package com.rsi.agp.core.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.webapp.action.CambioMasivoController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.poliza.PolizaPctComisiones;
import com.rsi.agp.dao.tables.poliza.SubvencionSocio;

import es.agroseguro.seguroAgrario.calculoSeguroAgrario.SeguridadSocial;
import es.agroseguro.seguroAgrario.calculoSeguroAgrario.SubvencionDeclarada;
import es.agroseguro.seguroAgrario.calculoSeguroAgrario.SubvencionesDeclaradas;

/**
 * Clase para transformar una poliza de base de datos en una poliza para enviar
 * a Agroseguro
 * 
 * @author U028783
 *
 */
public class PolizaTransformer {

	private static final Log logger = LogFactory.getLog(CambioMasivoController.class);
	/* Pet. 22208 ** MODIF TAM (27.02.2018) ** Inicio */

	public static SubvencionesDeclaradas rellenaSubvencionesDeclaradas(com.rsi.agp.dao.tables.poliza.PolizaSocio ps) {
		SubvencionesDeclaradas sds = SubvencionesDeclaradas.Factory.newInstance();
		boolean tieneATP = false;
		boolean NumSegSocial = false;
		ArrayList<SubvencionDeclarada> lstSubvSoc = new ArrayList<SubvencionDeclarada>();
		Map<String, String> mapSocios = new HashMap<String, String>();
		if (!StringUtils.nullToString(ps.getSocio().getNumsegsocial()).equals(""))
			NumSegSocial = true;
		for (SubvencionSocio sSociG : ps.getSocio().getSubvencionSocios()) {

			SubvencionDeclarada subd = SubvencionDeclarada.Factory.newInstance();
			subd.setTipo(sSociG.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa().intValue());
			if (sSociG.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa()
					.compareTo(Constants.SUBVENCION20) == 0)
				tieneATP = true;
			if (!StringUtils.nullToString(ps.getSocio().getNumsegsocial()).equals("") && tieneATP) {
				SeguridadSocial segSocial = SeguridadSocial.Factory.newInstance();
				segSocial.setProvincia(Integer.parseInt(ps.getSocio().getNumsegsocial().substring(0, 2)));
				segSocial.setNumero(Integer.parseInt(ps.getSocio().getNumsegsocial().substring(2, 10)));
				segSocial.setCodigo(ps.getSocio().getNumsegsocial().substring(10));
				if (!StringUtils.nullToString(ps.getSocio().getRegimensegsocial()).equals("")) {
					segSocial.setRegimen(Short.parseShort(ps.getSocio().getRegimensegsocial() + ""));
				}
				sds.setSeguridadSocial(segSocial);
				tieneATP = false;
			}

			if (sSociG.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa()
					.compareTo(Constants.SUBVENCION20) == 0) {
				if (NumSegSocial) {
					if (!mapSocios.containsKey(sSociG.getSocio().getId().getNif() + "-"
							+ sSociG.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa())) {
						lstSubvSoc.add(subd);
						mapSocios.put(
								sSociG.getSocio().getId().getNif() + "-"
										+ sSociG.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa(),
								"OK");
					}
				}
			} else {
				if (!mapSocios.containsKey(sSociG.getSocio().getId().getNif() + "-"
						+ sSociG.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa())) {
					lstSubvSoc.add(subd);
					mapSocios.put(sSociG.getSocio().getId().getNif() + "-"
							+ sSociG.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa(), "OK");
				}
			}

		}
		if (lstSubvSoc.size() > 0) {
			sds.setSubvencionDeclaradaArray(lstSubvSoc.toArray(new SubvencionDeclarada[lstSubvSoc.size()]));
			return sds;
		} else {
			return null;
		}

	}

	/**
	 * Calcula la comision del mediador a partir de los porcentajes de comisiones de
	 * la poliza
	 * 
	 * @param pctCom
	 *            pctComisiones de la poliza
	 * @return
	 */
	public static BigDecimal obtenerComisionMediador(PolizaPctComisiones ppc) {

		/*
		 * con descuento pctComision = (%comision mediador* (1 - (%dtoelegido / 100)) +
		 * %comision entidad comisionMediador = (%comision maxima * pctComision) / 100
		 */

		/*
		 * con recargo %Comision mediador = %Mediador (80%) * (1 + %elegido (40%) / 100)
		 * = 112% %comision %Comision mediador (48%) + %comision Entidad (112%)
		 * comisionMediador (XML) = %comision maxima (10%) * %comision 160%) / 100 = 16%
		 * <Gastos administracion= adquisicion= comisionMediador= 6.00 >
		 */
		BigDecimal comMediador = new BigDecimal(0);
		if (ppc.getPctcommax() != null && ppc.getPctentidad() != null && ppc.getPctesmediadora() != null) {
			BigDecimal comisionMax = ppc.getPctcommax();
			BigDecimal comisionEntidad = ppc.getPctentidad();
			BigDecimal comisionE_S = ppc.getPctesmediadora();
			BigDecimal dtoElegido = null;
			BigDecimal recElegido = null;
			BigDecimal zero = new BigDecimal(0);
			BigDecimal cien = new BigDecimal(100);
			if (ppc.getPctdescelegido() != null)
				dtoElegido = ppc.getPctdescelegido();
			if (ppc.getPctrecarelegido() != null)
				recElegido = ppc.getPctrecarelegido();
			BigDecimal descuento = zero;
			BigDecimal recargo = zero;
			BigDecimal pctComision = zero;

			logger.debug("DATOS PARA COMISION MEDIADOR: comisionMax: " + comisionMax + " comisionEnt: "
					+ comisionEntidad + " comisionE_ S: " + comisionE_S + " dtoElegido: " + dtoElegido + " recElegido: "
					+ recElegido);

			if (dtoElegido != null && dtoElegido.compareTo(zero) != 0) {
				descuento = dtoElegido.divide(cien);
				pctComision = (comisionE_S.multiply(new BigDecimal(1).subtract(descuento))).add(comisionEntidad);
			}
			if (recElegido != null && recElegido.compareTo(zero) != 0) {
				recargo = recElegido.divide(cien);
				pctComision = (comisionE_S.multiply(new BigDecimal(1).add(recargo))).add(comisionEntidad);
			}
			if ((dtoElegido == null || dtoElegido.compareTo(zero) == 0)
					&& (recElegido == null || recElegido.compareTo(zero) == 0)) {
				pctComision = (comisionE_S.multiply(new BigDecimal(1))).add(comisionEntidad);
			}
			if (comisionMax != null && comisionMax.compareTo(zero) != 0)
				comMediador = (comisionMax.multiply(pctComision)).divide(cien);
		}
		logger.debug("COM MEDIADOR CALCULADO: " + comMediador.setScale(2, BigDecimal.ROUND_DOWN));
		return comMediador.setScale(2, BigDecimal.ROUND_DOWN);
	}

	/**
	 * Devuelve el codigo interno asociado a la poliza. Si la mediadora es del tipo
	 * 3xxx-0 o 8xxx-0, se obtendra a partir del codigo de entidad y el cogigo de
	 * oficina, cada uno de ellos formateados a 4 caracteres rellenando por la
	 * izquierda con ceros y, a su vez, todo formateado a 20 caracteres. Si la
	 * mediadora es de cualquier otro tipo, se obtendra a partir del codigo de
	 * entidad mediadora y del cogigo de subentidad mediadora, cada uno de ellos
	 * formateados a 4 caracteres rellenando por la izquierda con ceros y, a su vez,
	 * todo formateado a 20 caracteres.
	 * 
	 * @param poliza
	 * @return
	 */
	public static String getCodigoInterno(final com.rsi.agp.dao.tables.poliza.Poliza poliza) {

		try {
			int entMed = poliza.getColectivo().getSubentidadMediadora().getId().getCodentidad().intValue();
			int subEntMed = poliza.getColectivo().getSubentidadMediadora().getId().getCodsubentidad().intValue();

			// Si la subentidad mediadora es 0 y la entidad mediadora es 3xxx o 8xxx
			if (subEntMed == 0 && ((entMed >= 3000 && entMed < 4000) || (entMed >= 8000 && entMed < 9000))) {
				int entidad = poliza.getColectivo().getSubentidadMediadora().getEntidad().getCodentidad().intValue();
				int oficina = poliza.getOficina() == null ? 0 : (Integer.parseInt(poliza.getOficina().trim()));

				return String.format("%20s", String.format("%04d", entidad) + String.format("%04d", oficina));
			}
			// En cualquier otro caso de mediadora
			else {
				return String.format("%20s", String.format("%04d", entMed) + String.format("%04d", subEntMed));
			}
		} catch (Exception e) {
			logger.error("Error al obtener el codigo interno", e);
			return String.format("%20s", "");
		}

	}
}