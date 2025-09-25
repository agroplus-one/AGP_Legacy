package com.rsi.agp.batch.importacion;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.tables.admin.Socio;
import com.rsi.agp.dao.tables.admin.SocioId;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizaSocio;
import com.rsi.agp.dao.tables.poliza.SubvencionSocio;

public final class BBDDSociosUtil {
	
	private static final Logger logger = Logger
			.getLogger(ImportacionPolizasExt.class);

	private BBDDSociosUtil() {
	}

	// Metodo que transforma los socios de la situación actual
	// en el formato esperado por el modelo de datos de Agroplus y puebla el
	// objeto Hibernate encargado de la importacion
	protected static void populateSocios(
			final Poliza polizaHbm,
			final es.agroseguro.contratacion.declaracionSubvenciones.RelacionSocios relacionSocios,
			final Session session) throws Exception {
		
		logger.info("**@@**Dentro de populateSocios");

		if (relacionSocios != null) {
			/* ESC-6024 ** Inicio ** MODIF TAM (27.05.2019) */
			//Set<PolizaSocio> polizaSocios;*/
			/*Set<SubvencionSocio> subvencionSocios;*/
			/* ESC-6024 ** Inicio ** MODIF TAM (27.05.2019) */
			PolizaSocio socioPolizaHbm;
			Socio socioHbm;
			SubvencionSocio subSocioHbm;

			/* ESC-6024 ** Inicio ** MODIF TAM (27.05.2019) */
			/* polizaSocios = new HashSet<PolizaSocio>();*/
			/* subvencionSocios = new HashSet<SubvencionSocio>();*/

			/* ESC-10831 ** Inicio ** MODIF TAM (28.09.2020) */
			
			Long idAsegurado = polizaHbm.getAsegurado().getId();
			logger.info("**@@** Valor de idAsegurado"+idAsegurado);			
			
			/* Incluimos como filtro el id del asegurado */
			es.agroseguro.contratacion.declaracionSubvenciones.Socio[] socios = relacionSocios
					.getSocioArray();
			for (es.agroseguro.contratacion.declaracionSubvenciones.Socio socio : socios) {
				logger.info("**@@** Valor de nif-socio: "+socio.getNif());
				// El socio debe de estar en la tabla de socios
				// Si no esta lo creamos
				/*socioHbm = (Socio) session.createCriteria(Socio.class)
						.add(Restrictions.eq("id.nif", socio.getNif()))
						.uniqueResult();*/
				socioHbm = (Socio) session.createCriteria(Socio.class)
				.add(Restrictions.eq("id.nif", socio.getNif()))
				.add(Restrictions.eq("asegurado.id", idAsegurado))
				.uniqueResult();


				if (socioHbm == null) {
					socioHbm = new Socio();

					socioHbm.setAsegurado(polizaHbm.getAsegurado());

					socioHbm.setId(new SocioId(socio.getNif(), polizaHbm
							.getAsegurado().getId()));

					if (socio.getRazonSocial() != null) {
						socioHbm.setRazonsocial(socio.getRazonSocial()
								.getRazonSocial());
						socioHbm.setTipoidentificacion("CIF");
					} else {
						socioHbm.setNombre(socio.getNombreApellidos()
								.getNombre());
						socioHbm.setApellido1(socio.getNombreApellidos()
								.getApellido1());
						socioHbm.setApellido2(socio.getNombreApellidos()
								.getApellido2());
						socioHbm.setTipoidentificacion("NIF");
					}
					if (socio.getSubvencionesDeclaradas() != null
							&& socio.getSubvencionesDeclaradas()
									.getSeguridadSocial() != null) {
						socioHbm.setAtp("SI");
						socioHbm.setNumsegsocial(socio
								.getSubvencionesDeclaradas()
								.getSeguridadSocial().getProvincia()
								+ ""
								+ socio.getSubvencionesDeclaradas()
										.getSeguridadSocial().getNumero()
								+ socio.getSubvencionesDeclaradas()
										.getSeguridadSocial().getCodigo());
						socioHbm.setRegimensegsocial(BigDecimal.valueOf(socio
								.getSubvencionesDeclaradas()
								.getSeguridadSocial().getRegimen()));
					} else {
						socioHbm.setAtp("NO");
					}
					session.saveOrUpdate(socioHbm);
				}

				// Asociamos el socio a la poliza
				socioPolizaHbm = new PolizaSocio();
				socioPolizaHbm.setSocio(socioHbm);
				socioPolizaHbm.setOrden(BigDecimal.valueOf(socio.getNumero()));
				socioPolizaHbm.setPoliza(polizaHbm);
				session.saveOrUpdate(socioPolizaHbm);
				if (socio.getSubvencionesDeclaradas() != null) {
					es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada[] subvsDeclaradaSocio = socio
							.getSubvencionesDeclaradas().getSubvencionDeclaradaArray();
					for (es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada subvDeclaradaSocio : subvsDeclaradaSocio) {
						subSocioHbm = new SubvencionSocio();
						subSocioHbm.setSocio(socioHbm);
						subSocioHbm
								.setSubvencionEnesa(BBDDSubvencionesUtil
										.getSubvEnesaBBDD(BigDecimal
												.valueOf(subvDeclaradaSocio
														.getTipo()), polizaHbm
												.getLinea().getLineaseguroid(),
												polizaHbm.getCodmodulo(),
												session));

						if (Constants.SUBVENCION_JOVEN_HOMBRE.equals(BigDecimal
								.valueOf(subvDeclaradaSocio.getTipo()))) {
							socioHbm.setJovenagricultor('H');
						} else if (Constants.SUBVENCION_JOVEN_MUJER
								.equals(BigDecimal.valueOf(subvDeclaradaSocio
										.getTipo()))) {
							socioHbm.setJovenagricultor('M');
						} else if (Constants.SUBVENCION20.equals(BigDecimal
								.valueOf(subvDeclaradaSocio.getTipo()))) {
							socioHbm.setAtp("SI");
							socioHbm.setNumsegsocial(socio
									.getSubvencionesDeclaradas()
									.getSeguridadSocial().getProvincia()
									+ ""
									+ socio.getSubvencionesDeclaradas()
											.getSeguridadSocial().getNumero()
									+ socio.getSubvencionesDeclaradas()
											.getSeguridadSocial().getCodigo());
							socioHbm.setRegimensegsocial(BigDecimal
									.valueOf(socio.getSubvencionesDeclaradas()
											.getSeguridadSocial().getRegimen()));
						}

						subSocioHbm.setPoliza(polizaHbm);
						session.saveOrUpdate(subSocioHbm);
						polizaHbm.getSubvencionSocios().add(subSocioHbm);
						/*subvencionSocios.add(subSocioHbm);*/
						/*polizaHbm.getSubvencionSocios().add(e)*/
					}
				}
				/*polizaSocios.add(socioPolizaHbm);*/
				polizaHbm.getPolizaSocios().add(socioPolizaHbm);
			}
			
			/*polizaHbm.setPolizaSocios(polizaSocios);
			polizaHbm.setSubvencionSocios(subvencionSocios);*/
		}

	}
}
