package com.rsi.agp.dao.models.config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.filters.commons.LineasFiltro;
import com.rsi.agp.dao.filters.config.OrganizacionInformacionFiltro;
import com.rsi.agp.dao.filters.config.UsosFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.config.ConfiguracionCampo;
import com.rsi.agp.dao.tables.config.OrigenDatos;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;
import com.rsi.agp.dao.tables.config.Seccion;
import com.rsi.agp.dao.tables.config.TipoCampo;
import com.rsi.agp.dao.tables.org.OrganizadorInformacion;
import com.rsi.agp.dao.tables.org.OrganizadorInformacionId;
import com.rsi.agp.dao.tables.org.Uso;
import com.rsi.agp.vo.ComboDataVO;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class TallerConfiguracionPantallasDao extends BaseDaoHibernate implements ITallerConfiguracionPantallasDao {

	final String MENSAJE_SAVE_OK = "Alta realizada";
	final String MENSAJE_SAVE_ERROR = "El alta no se pudo realizar";

	public List<ComboDataVO> getUsos(final Long idLinea) {
		// Recogemos los codUsos por idLinea
		OrganizacionInformacionFiltro filter = new OrganizacionInformacionFiltro(idLinea);
		List listaCodUsos = this.getObjects(filter);
		// Con los codusos reogemos de la tabla Usos, coduso + descripcion
		UsosFiltro filtroUsos = new UsosFiltro(listaCodUsos);
		List<Uso> listaUsos = this.getObjects(filtroUsos);
		// Montamos una lista equivalente a las listas de un ComboBox
		List<ComboDataVO> lista = new ArrayList<ComboDataVO>(listaUsos.size());
		for (Uso uso : listaUsos) {
			ComboDataVO item = new ComboDataVO(uso.getCoduso().toString(), uso.getDesuso());
			lista.add(item);
		}
		return lista;
	}

	// -------------------------------------------
	// GET ESTRUCTURA CAMPOS
	// -------------------------------------------
	public List getEstructuraCampos(final Long idLinea, final BigDecimal codUso) {
		Criteria criteria = this.getSession().createCriteria(OrganizadorInformacion.class);
		// eq (where)
		Criterion critUso = Restrictions.eq("id.coduso", codUso);
		criteria.add(critUso);
		Criterion critLineaSeguroId = Restrictions.eq("id.lineaseguroid", idLinea);
		criteria.add(critLineaSeguroId);
		criteria.createAlias("uso", "u");
		criteria.createAlias("ubicacion", "ub");
		criteria.createAlias("diccionarioDatos", "dd");
		// order(order by)
		criteria.addOrder(Order.asc("u.desuso"));
		criteria.addOrder(Order.asc("ub.codubicacion"));
		criteria.addOrder(Order.asc("dd.desconcepto"));
		return criteria.list();
	}

	public List getLineas(BigDecimal codPlan) {
		LineasFiltro filtro = new LineasFiltro();
		filtro.setCodPlan(codPlan);
		return this.getObjects(filtro);
	}

	private void deleteCamposConfiguradosPantalla(final BigDecimal idPantallaConfigurada) throws BusinessException {
		Session session = obtenerSession();
		try {
			String hql = "delete from TB_CONFIGURACION_CAMPOS where idpantallaconfigurable = :id";
			SQLQuery query = session.createSQLQuery(hql);
			query.setParameter("id", idPantallaConfigurada).executeUpdate();
		} catch (Exception excepcion) {
			logger.error(excepcion);
			throw new BusinessException("Se ha producido un error en el borrado de Campos Configurados ", excepcion);
		}
	}

	public PantallaConfigurable getPantallaConfigurada(final Long idPantallaConfigurable) {
		PantallaConfigurable pantallaConfigurable = null;
		pantallaConfigurable = (PantallaConfigurable) this.getObject(PantallaConfigurable.class,
				idPantallaConfigurable);
		return pantallaConfigurable;
	}

	public void saveCamposPantalla(final Long idPantallaConfigurable, final List<ConfiguracionCampo> campos)
			throws BusinessException {
		PantallaConfigurable pantallaConfigurable = this.getPantallaConfigurada(idPantallaConfigurable);
		if (pantallaConfigurable == null) {
			throw new BusinessException(
					"No se encuentra la pantalla para el identificador recibido: " + idPantallaConfigurable);
		} else {
			deleteCamposConfiguradosPantalla(BigDecimal.valueOf(idPantallaConfigurable));
			try {
				for (ConfiguracionCampo campo : campos) {
					campo.setPantallaConfigurable(pantallaConfigurable);
					Long idOrigenDatos = campo.getOrigenDatos().getIdorigendatos();
					if (Long.valueOf(-1).equals(idOrigenDatos)) {
						campo.setOrigenDatos(null);
					} else {
						campo.setOrigenDatos((OrigenDatos) this.get(OrigenDatos.class, idOrigenDatos));
					}
					Long idTipoCampo = campo.getTipoCampo().getIdtipo();
					if (Long.valueOf(-1).equals(idTipoCampo)) {
						campo.setTipoCampo(null);
					} else {
						campo.setTipoCampo((TipoCampo) this.get(TipoCampo.class, idTipoCampo));
					}
					campo.setSeccion(
							(Seccion) this.get(Seccion.class, Long.valueOf(campo.getId().getIdseccion().intValue())));
					OrganizadorInformacionId oiId = new OrganizadorInformacionId();
					oiId.setLineaseguroid(campo.getId().getLineaseguroid());
					oiId.setCodconcepto(campo.getId().getCodconcepto());
					oiId.setCodubicacion(campo.getId().getCodubicacion());
					oiId.setCoduso(campo.getId().getCoduso());
					campo.setOrganizadorInformacion(
							(OrganizadorInformacion) this.get(OrganizadorInformacion.class, oiId));
					this.saveOrUpdate(campo);
				}
			} catch (Exception ex) {
				throw new BusinessException("Se ha producido un error en la transaccion", ex);
			}
		}
	}

	@Override
	public OrganizadorInformacion getOrgInformacion(final Long lineaseguroid, final BigDecimal codConcepto,
			final BigDecimal codUbicacion, final BigDecimal codUso) throws BusinessException {
		try {
			OrganizadorInformacionId id = new OrganizadorInformacionId();
			id.setLineaseguroid(lineaseguroid);
			id.setCodconcepto(codConcepto);
			id.setCodubicacion(codUbicacion);
			id.setCoduso(codUso);
			return (OrganizadorInformacion) this.get(OrganizadorInformacion.class, id);
		} catch (Exception ex) {
			throw new BusinessException("Se ha producido un error en la transaccion", ex);
		}
	}
}