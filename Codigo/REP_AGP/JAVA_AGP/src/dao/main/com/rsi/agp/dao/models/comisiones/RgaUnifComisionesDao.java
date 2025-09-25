package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.impl.CierreComisionesManager;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.comisiones.Retencion;
import com.rsi.agp.dao.tables.comisiones.unificado.AplicacionUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.FaseUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.GrupoNegocioUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.ReciboUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.RgaUnifMediadores;

public class RgaUnifComisionesDao extends BaseDaoHibernate implements
		IRgaUnifComisionesDao {

	private static final Log LOGGER = LogFactory
			.getLog(RgaUnifComisionesDao.class);

	@SuppressWarnings("rawtypes")
	public void generaDatosMediadores2015(
			List<FicheroUnificado> listFasesUnifCierre)
			throws Exception {
		RgaUnifMediadores rgaMed = null;
		BigDecimal real = new BigDecimal(0);
		Integer entidad =0;
		Integer subentidad = 0;
		Calendar cal = Calendar.getInstance();
		HashMap<String, BigDecimal> entidadReal = new HashMap<String, BigDecimal>();
		BigDecimal retencion = new BigDecimal(0);
		String clave = "";
	
		try {

			for (FicheroUnificado fich : listFasesUnifCierre) {
				logger.debug("FICHERO : " + fich.getNombreFichero());
				for (FaseUnificado fase : fich.getFases()) {
					logger.debug("FASE : " + fase.getFase());
					for (ReciboUnificado recibo : fase.getReciboUnificados()) {

						if (recibo.getAplicacion() != null) {

							AplicacionUnificado apl = recibo.getAplicacion();
							
							if (recibo.getColectivo()!= null && recibo.getColectivo().getReferencia()!=null){
								Colectivo col=(Colectivo) this.getObject(Colectivo.class, "idcolectivo", recibo.getColectivo().getReferencia());
								if(null!=col && null!=col.getSubentidadMediadora() && null!=col.getSubentidadMediadora().getId() 
										&& null!=col.getSubentidadMediadora().getId().getCodentidad()){
									entidad = col.getSubentidadMediadora().getId().getCodentidad().intValue();
								}
								if(null!=col && null!=col.getSubentidadMediadora() && null!=col.getSubentidadMediadora().getId() 
										&& null!=col.getSubentidadMediadora().getId().getCodsubentidad()){
									subentidad = col.getSubentidadMediadora().getId().getCodsubentidad().intValue();
								}								
							}
							clave = entidad + "-" + subentidad;

							// Para ficheros de deuda aplazada
							if (fich.getTipoFichero().equals(
									CierreComisionesManager.FICHERO_DEUDA)) {
								for (ReciboUnificado rec : apl.getRecibos()) {
									for (GrupoNegocioUnificado gn : rec
											.getGrupoNegocios()) {

										real = real
												.add(gn.getGaCommedEsmed() != null ? gn
														.getGaCommedEsmed()
														: new BigDecimal(0));
									}
								}

							} else {

								if (!apl.getGrupoNegocios().isEmpty()) {
									for (GrupoNegocioUnificado gn : apl
											.getGrupoNegocios()) {

										real = real
												.add(gn.getGaCommedEsmed() != null ? gn
														.getGaCommedEsmed()
														: new BigDecimal(0));
									}
								}
							}
							// guardamos las entidades con el real
							if (entidadReal.containsKey(clave)) {
								BigDecimal saldoaux = (BigDecimal) entidadReal
										.get(clave);
								saldoaux = saldoaux.add(real);
								entidadReal.put(clave, saldoaux);
							} else {
								entidadReal.put(clave, real);
							}
							real = new BigDecimal(0);
						}
					}// for recibos
				}// for fases
			}// for fichero

			// obten retencion de IRPF
			BigDecimal irpf = this.getIRPF(cal.get(Calendar.YEAR));

			// recorremos el hashmap y guardamos cada entidad-subentidad en
			// nuevo regitro de la tabla
			Iterator it = entidadReal.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry e = (Map.Entry) it.next();

				rgaMed = new RgaUnifMediadores();
				rgaMed.setAnyo(cal.get(Calendar.YEAR));
				rgaMed.setMes(cal.get(Calendar.MONTH));
				rgaMed.setEntidad(Integer.valueOf(e.getKey().toString()
						.substring(0, 4)));
				rgaMed.setSubentidad(Integer.valueOf(e.getKey().toString()
						.substring(5)));
				rgaMed.setReal((BigDecimal) e.getValue());

				retencion = rgaMed.getReal().multiply(irpf)
						.divide(new BigDecimal(100));

				rgaMed.setRetencion(retencion);
				rgaMed.setLiquido(rgaMed.getReal().subtract(retencion));
				this.saveOrUpdate(rgaMed);
			}

		} catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD  (generaDatosMediadores2015): "
					+ ex.getMessage());
			throw new DAOException(
					"Se ha producido un error en generaDatosMediadores2015", ex);
		}
	}

	/**
	 * Busca en la tabla tb_coms_retenciones la retencion dado un anyo
	 * 
	 * @param i
	 * @return retencion
	 * @throws DAOException
	 */
	private BigDecimal getIRPF(int anyo) throws DAOException {

		logger.debug("init - getIRPF");
		Session session = obtenerSession();

		try {

			Criteria criteria = session.createCriteria(Retencion.class);
			criteria.add(Restrictions.eq("anyo", anyo));
			Retencion irpf = (Retencion) criteria.uniqueResult();
			if (irpf != null)
				return irpf.getRetencion();
			else
				return new BigDecimal(0);

		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:"
					+ e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}

	}

}
