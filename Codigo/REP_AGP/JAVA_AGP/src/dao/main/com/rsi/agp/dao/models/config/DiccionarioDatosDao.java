package com.rsi.agp.dao.models.config;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.report.anexoMod.RelacionEtiquetaTabla;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.OrganizadorInfoConstants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;

@SuppressWarnings("unchecked")
public class DiccionarioDatosDao extends BaseDaoHibernate implements
		IDiccionarioDatosDao {

	/**
	 * Metodo que obtiene un mapa cuya clave es el codigo de concepto de los
	 * datos variables de parcelas y el valor es un objeto que contiene la
	 * etiqueta y la tabla asociadas al concepto.
	 * 
	 * @param lineaseguroid
	 *            Identificador de plan/línea
	 * @return Mapa con la informacion asociada a cada codigo de concepto de los
	 *         datos variables de parcelas
	 */
	public Map<BigDecimal, RelacionEtiquetaTabla> getCodConceptoEtiquetaTablaParcelas(
			final Long lineaseguroid) {
		String sql = "select distinct o.codconcepto, dd.nomconcepto, dd.etiquetaxml, dd.numtabla "
				+ "from o02agpe0.tb_sc_oi_org_info o, o02agpe0.tb_sc_dd_dic_datos dd "
				+ "where o.codconcepto = dd.codconcepto and "
				+ "o.codubicacion = "
				+ OrganizadorInfoConstants.UBICACION_PARCELA_DV
				+ " and o.coduso = "
				+ Constants.USO_POLIZA
				+ "  and o.lineaseguroid = " + lineaseguroid;
		List<Object> busqueda = (List<Object>) this.getObjectsBySQLQuery(sql);
		// Recorro la lista y voy rellenando el mapa
		Map<BigDecimal, RelacionEtiquetaTabla> resultado = new HashMap<BigDecimal, RelacionEtiquetaTabla>();
		for (Object elem : busqueda) {
			Object[] elemento = (Object[]) elem;
			RelacionEtiquetaTabla ret = new RelacionEtiquetaTabla(
					StringUtils.nullToString(elemento[2]),
					StringUtils.nullToString(elemento[3]),
					StringUtils.nullToString(elemento[1]));
			resultado.put(new BigDecimal(elemento[0] + ""), ret);
		}
		return resultado;
	}

	/**
	 * Metodo que obtiene un mapa cuya clave es el codigo de concepto de los
	 * datos variables de explotaciones y el valor es un objeto que contiene la
	 * etiqueta y la tabla asociadas al concepto.
	 * 
	 * @param lineaseguroid
	 *            Identificador de plan/línea
	 * @return Mapa con la informacion asociada a cada codigo de concepto de los
	 *         datos variables de explotaciones
	 */
	public Map<BigDecimal, RelacionEtiquetaTabla> getCodConceptoEtiquetaTablaExplotaciones(
			final Long lineaseguroid) {
		String sql = "select distinct o.codconcepto, dd.nomconcepto, dd.etiquetaxml, dd.numtabla "
				+ "from o02agpe0.tb_sc_oi_org_info o, o02agpe0.tb_sc_dd_dic_datos dd "
				+ "where o.codconcepto = dd.codconcepto and "
				+ "o.codubicacion in ("
				+ OrganizadorInfoConstants.UBICACION_EXPLOTACION
				+ ", "
				+ OrganizadorInfoConstants.UBICACION_GRUPO_RAZA
				+ ", "
				+ OrganizadorInfoConstants.UBICACION_CAP_ASEG
				+ ", "
				+ OrganizadorInfoConstants.UBICACION_ANIMALES
				+ ") and o.coduso = "
				+ OrganizadorInfoConstants.USO_POLIZA
				+ "  and o.lineaseguroid = " + lineaseguroid;
		List<Object> busqueda = (List<Object>) this.getObjectsBySQLQuery(sql);
		// Recorro la lista y voy rellenando el mapa
		Map<BigDecimal, RelacionEtiquetaTabla> resultado = new HashMap<BigDecimal, RelacionEtiquetaTabla>();
		for (Object elem : busqueda) {
			Object[] elemento = (Object[]) elem;
			RelacionEtiquetaTabla ret = new RelacionEtiquetaTabla(
					StringUtils.nullToString(elemento[2]),
					StringUtils.nullToString(elemento[3]),
					StringUtils.nullToString(elemento[1]));
			resultado.put(new BigDecimal(elemento[0] + ""), ret);
		}
		return resultado;
	}

	/**
	 * Metodo que obtiene un mapa cuya clave es el codigo de concepto de los
	 * datos variables de coberturas y el valor es un objeto que contiene la
	 * etiqueta y la tabla asociadas al concepto.
	 * 
	 * @param lineaseguroid
	 *            Identificador de plan/línea
	 * @return Mapa con la informacion asociada a cada codigo de concepto de los
	 *         datos variables de coberturas
	 */
	public Map<BigDecimal, RelacionEtiquetaTabla> getCodConceptoEtiquetaTablaCoberturas(
			final Long lineaseguroid) {
		String sql = "select distinct o.codconcepto, dd.nomconcepto, dd.etiquetaxml, dd.numtabla "
				+ "from o02agpe0.tb_sc_oi_org_info o, o02agpe0.tb_sc_dd_dic_datos dd "
				+ "where o.codconcepto = dd.codconcepto and "
				+ "o.codubicacion = "
				+ OrganizadorInfoConstants.UBICACION_PARCELA_DV
				+ " and o.coduso = "
				+ Constants.USO_POLIZA
				+ "  and o.lineaseguroid = " + lineaseguroid;
		
		List<Object> busqueda = (List<Object>) this.getObjectsBySQLQuery(sql);
		// Recorro la lista y voy rellenando el mapa
		Map<BigDecimal, RelacionEtiquetaTabla> resultado = new HashMap<BigDecimal, RelacionEtiquetaTabla>();
		for (Object elem : busqueda) {
			Object[] elemento = (Object[]) elem;
			RelacionEtiquetaTabla ret = new RelacionEtiquetaTabla(
					StringUtils.nullToString(elemento[2]),
					StringUtils.nullToString(elemento[3]),
					StringUtils.nullToString(elemento[1]));
			resultado.put(new BigDecimal(elemento[0] + ""), ret);
		}
		return resultado;
	}

	@Override
	public RiesgoCubierto getRiesgosElegidos(final Long lineaseguroid,
			final String modulo, final int codriesgoCubierto) {
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(RiesgoCubierto.class);
		criteria.add(Restrictions.eq("id.codmodulo", modulo.trim()));
		criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
		criteria.add(Restrictions.eq("id.codriesgocubierto",
				BigDecimal.valueOf(codriesgoCubierto)));
		RiesgoCubierto rc = (RiesgoCubierto) criteria.uniqueResult();
		return rc;
	}
	
	public String getTipoAsegurado(final BigDecimal idComparativa) {
		String sql = "select b.descripcion from o02agpe0.TB_MODULOS_POLIZA mp "
				+ "inner join o02agpe0.TB_SC_C_DATOS_BUZON_GENERAL b on b.codcpto = "
				+ ConstantsConceptos.CODCPTO_TIPO_ASEG_GAN + " and b.valor_cpto = mp.tipo_aseg_ganado "
				+ "where mp.idmodulo = " + idComparativa.toString();
		List<Object> busqueda = (List<Object>) this.getObjectsBySQLQuery(sql);
		return busqueda != null && !busqueda.isEmpty() ? busqueda.get(0).toString() : "";
	}
}