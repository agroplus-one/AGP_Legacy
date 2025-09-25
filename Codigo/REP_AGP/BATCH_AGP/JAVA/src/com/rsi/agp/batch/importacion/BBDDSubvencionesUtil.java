package com.rsi.agp.batch.importacion;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.tables.cpl.SubvencionCCAA;
import com.rsi.agp.dao.tables.cpl.SubvencionEnesa;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.SubAseguradoCCAA;
import com.rsi.agp.dao.tables.poliza.SubAseguradoENESA;

public final class BBDDSubvencionesUtil {
	
	private static final Logger logger = Logger.getLogger(BBDDSubvencionesUtil.class);

	private BBDDSubvencionesUtil() {
	}

	// Metodo que transforma las subvenciones de la situacion actual
	// en el formato esperado por el modelo de datos de Agroplus y puebla el
	// objeto Hibernate encargado de la importacion
	protected static void populateSubvenciones(
			final Poliza polizaHbm,
			final es.agroseguro.contratacion.declaracionSubvenciones.SubvencionesDeclaradas subvsDeclaradas,
			final Session session) throws Exception {

		Set<SubAseguradoENESA> subAseguradoENESAs;
		Set<SubAseguradoCCAA> subAseguradoCCAAs;
		SubAseguradoENESA subAsegEnesaHbm;
		SubAseguradoCCAA subAsegCCAAHbm;
		SubvencionEnesa subvEnesaHbm;
		SubvencionCCAA subvCCAAHbm;

		if (subvsDeclaradas != null) {

			es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada[] subvsDeclaradasArr = subvsDeclaradas
					.getSubvencionDeclaradaArray();

			subAseguradoENESAs = new HashSet<SubAseguradoENESA>();
			subAseguradoCCAAs = new HashSet<SubAseguradoCCAA>();

			for (es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada subvDeclaradas : subvsDeclaradasArr) {

				subvEnesaHbm = getSubvEnesaBBDD(
						BigDecimal.valueOf(subvDeclaradas.getTipo()), polizaHbm
								.getLinea().getLineaseguroid(),
						polizaHbm.getCodmodulo(), session);
				// Si la subvencion es de ENESA
				if (subvEnesaHbm != null) {
					subAsegEnesaHbm = new SubAseguradoENESA();
					subAsegEnesaHbm.setAsegurado(polizaHbm.getAsegurado());
					subAsegEnesaHbm.setPoliza(polizaHbm);
					subAsegEnesaHbm.setSubvencionEnesa(subvEnesaHbm);
					session.saveOrUpdate(subAsegEnesaHbm);
					subAseguradoENESAs.add(subAsegEnesaHbm);
				}
				// Si la subvencion es de CCAA
				else {										
					subvCCAAHbm = getSubvCCAABBDD(BigDecimal.valueOf(subvDeclaradas
							.getTipo()), polizaHbm.getLinea().getLineaseguroid(),
							polizaHbm.getCodmodulo(), session);
					if (subvCCAAHbm != null) {
						subAsegCCAAHbm = new SubAseguradoCCAA();
						subAsegCCAAHbm.setAsegurado(polizaHbm.getAsegurado());
						subAsegCCAAHbm.setPoliza(polizaHbm);
						subAsegCCAAHbm.setSubvencionCCAA(subvCCAAHbm);
						session.saveOrUpdate(subAsegCCAAHbm);
						subAseguradoCCAAs.add(subAsegCCAAHbm);
					}
				}
			}
			if (!subAseguradoENESAs.isEmpty())
				polizaHbm.setSubAseguradoENESAs(subAseguradoENESAs);
			if (!subAseguradoCCAAs.isEmpty())
				polizaHbm.setSubAseguradoCCAAs(subAseguradoCCAAs);
		}
	}

	protected static SubvencionEnesa getSubvEnesaBBDD(
			final BigDecimal tipoSubv, final Long lineaseguroid,
			final String codModulo, final Session session) {

		try {
			// MPM - Se obtiene el listado de subvenciones y se devuelve la primera para evitar errores al utilizar 'uniqueResult',
			// ya que da igual el registro de TB_SC_C_SUBVS_ENESA que se asocie a la poliza con tal de que sea correcto el 
			// codigo de tipo de subvencion		
			@SuppressWarnings("unchecked")
			List<SubvencionEnesa> subvEnesaHbm = (List<SubvencionEnesa>) session
					.createCriteria(SubvencionEnesa.class)
					.createAlias("tipoSubvencionEnesa", "tipoSubvencionEnesa")
					.createAlias("modulo", "modulo")
					.add(Restrictions.eq(
							"tipoSubvencionEnesa.codtiposubvenesa", tipoSubv))
					.add(Restrictions.eq("id.lineaseguroid", lineaseguroid))
					.add(Restrictions.eq("modulo.id.codmodulo", codModulo))
					.list();
			
			if (subvEnesaHbm != null && !subvEnesaHbm.isEmpty()) return subvEnesaHbm.get(0);
		} catch (Exception e) {
			logger.error("Error al obtener las subvenciones de enesa", e);			
		}
		
		return null;
	}

	protected static SubvencionCCAA getSubvCCAABBDD(final BigDecimal tipoSubv,
			final Long lineaseguroid, final String codModulo,
			final Session session) {
		try {
			@SuppressWarnings("unchecked")
			List<SubvencionCCAA> subvCCAAHbm = (List<SubvencionCCAA>) session
					.createCriteria(SubvencionCCAA.class)
					.createAlias("tipoSubvencionCCAA", "tipoSubvencionCCAA")
					.createAlias("modulo", "modulo")
					.add(Restrictions.eq("tipoSubvencionCCAA.codtiposubvccaa",
							tipoSubv))
					.add(Restrictions.eq("id.lineaseguroid", lineaseguroid))
					.add(Restrictions.eq("modulo.id.codmodulo", codModulo))
					.list();
			
			if (subvCCAAHbm != null && !subvCCAAHbm.isEmpty()) return subvCCAAHbm.get(0);
		} catch (Exception e) {
			logger.error("Error al obtener las subvenciones de ccaa", e);
		}
		
		return null;
	}
}