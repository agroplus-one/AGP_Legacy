package com.rsi.agp.dao.models.commons;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.webapp.util.ListUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.poliza.Linea;

/**
 * Clase para manejar las operaciones de base de datos de los tomadores.
 * 
 * Las operaciones basicas se realizan en BaseDaoHibernate. Aqui deberemos
 * definir las operaciones especificas para los tomadores.
 * 
 * @author U028783
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class AjaxDao extends BaseDaoHibernate implements IAjaxDao {

	/**
	 * Metodo para obtener un listado generico
	 */
	public List getData(String objeto, Integer posicion, String[] filtro, String[] campoFiltro, String[] tipoFiltro,
			String[] valorDepende, String[] campoBeanDepende, String[] tipoBeanDepende, String campoOrden,
			String tipoOrden, String[] campoListado, String[] campoBeanDistinct, String[] campoBeanIsNull,
			String[] campoLeftJoin, String[] campoRestrictions, String[] valorRestrictions,
			String[] operadorRestrictions, String[] tipoValorRestrictions) {

		Session sesion = this.obtenerSession();
		
		// Mapa para guardar los alias creados
		Map<String, Boolean> mapAlias = new HashMap<String, Boolean>();

		try {

			Criteria criteria = sesion.createCriteria(objeto);
			
			createAlias(objeto, criteria, campoRestrictions, mapAlias);

			if (FiltroUtils.noEstaVacio(posicion)) {
				criteria.setFirstResult(posicion);
				criteria.setMaxResults(FiltroUtils.getMaxVisoresResults());
			}

			// Distinct
			if (campoBeanDistinct != null && campoBeanDistinct.length > 0) {
				ProjectionList projList = Projections.projectionList();

				for (int i = 0; i < campoBeanDistinct.length; i++) {
					projList.add(Projections.property(campoBeanDistinct[i]));
				}

				criteria.setProjection(Projections.distinct(projList));
			}
			// Campos is null
			if (campoBeanIsNull != null && campoBeanIsNull.length > 0) {
				for (int i = 0; i < campoBeanIsNull.length; i++) {
					criteria.add(Restrictions.isNull(campoBeanIsNull[i]));
				}
			}

			// Filtros
			this.creaFiltros(criteria, filtro, campoFiltro, tipoFiltro, campoRestrictions, valorRestrictions,
					operadorRestrictions, tipoValorRestrictions);

			// Campos dependientes
			this.createCriteriaLupaGenerica(valorDepende, campoBeanDepende, tipoBeanDepende, objeto, criteria,
					campoListado, campoLeftJoin, mapAlias);

			// Ordenacion
			if (!StringUtils.nullToString(campoOrden).equals("") && !StringUtils.nullToString(tipoOrden).equals("")) {
				if (tipoOrden.equals("ASC"))
					criteria.addOrder(Order.asc(campoOrden));
				else if (tipoOrden.equals("DESC"))
					criteria.addOrder(Order.desc(campoOrden));
			}

			List lista = criteria.list();

			// Si hay que hacer distinct por algun campo
			if (campoBeanDistinct != null && campoBeanDistinct.length > 0) {
				return parsearArrayObjects(objeto, lista);
			}

			return lista;
		} catch (Exception e) {
			logger.error("Error inesperado al obtener el listado de " + objeto, e);
		}

		return null;
	}

	public List getDataAcumularResultados(String objeto, Integer posicion, String[] filtro, String[] campoFiltro,
			String[] tipoFiltro, String[] valorDepende, String[] campoBeanDepende, String[] tipoBeanDepende,
			String campoOrden, String tipoOrden, String[] campoListado, String[] campoBeanDistinct,
			String[] campoBeanIsNull, String[] campoLeftJoin, String[] campoRestrictions, String[] valorRestrictions,
			String[] operadorRestrictions, String[] tipoValorRestrictions, String[] valorDependeGenerico) {

		Session sesion = this.obtenerSession();
		
		// Mapa para guardar los alias creados
		Map<String, Boolean> mapAlias = new HashMap<String, Boolean>();

		try {

			Criteria criteria = sesion.createCriteria(objeto);
			
			createAlias(objeto, criteria, campoRestrictions, mapAlias);

			if (FiltroUtils.noEstaVacio(posicion)) {
				criteria.setFirstResult(posicion);
				criteria.setMaxResults(FiltroUtils.getMaxVisoresResults());
			}

			// Distinct
			if (campoBeanDistinct != null && campoBeanDistinct.length > 0) {
				ProjectionList projList = Projections.projectionList();

				for (int i = 0; i < campoBeanDistinct.length; i++) {
					projList.add(Projections.property(campoBeanDistinct[i]));
				}

				criteria.setProjection(Projections.distinct(projList));
			}
			// Campos is null
			if (campoBeanIsNull != null && campoBeanIsNull.length > 0) {
				for (int i = 0; i < campoBeanIsNull.length; i++) {
					criteria.add(Restrictions.isNull(campoBeanIsNull[i]));
				}
			}

			// Filtros
			this.creaFiltros(criteria, filtro, campoFiltro, tipoFiltro, campoRestrictions, valorRestrictions,
					operadorRestrictions, tipoValorRestrictions);

			// Campos dependientes
			this.createCriteriaLupaGenerica(valorDepende, campoBeanDepende, tipoBeanDepende, objeto, criteria,
					campoListado, campoLeftJoin, mapAlias);

			// Ordenacion
			if (!StringUtils.nullToString(campoOrden).equals("") && !StringUtils.nullToString(tipoOrden).equals("")) {
				if (tipoOrden.equals("ASC"))
					criteria.addOrder(Order.asc(campoOrden));
				else if (tipoOrden.equals("DESC"))
					criteria.addOrder(Order.desc(campoOrden));
			}

			/*
			 * criteria.createAlias("usuario", "usu");
			 * criteria.createAlias("usu.subentidadMediadora", "esMed");
			 * criteria.add(Restrictions.eq("esMed.id.codsubentidad", new BigDecimal(0)));
			 */

			List lista = criteria.list();

			// Si hay que hacer distinct por algun campo
			if (campoBeanDistinct != null && campoBeanDistinct.length > 0) {
				return parsearArrayObjects(objeto, lista);
			}

			return lista;
		} catch (Exception e) {
			logger.error("Error inesperado al obtener el listado de " + objeto, e);
		}

		return null;
	}

	private void creaFiltros(Criteria criteria, String[] filtro, String[] campoFiltro, String[] tipoFiltro,
			String[] campoRestrictions, String[] valorRestrictions, String[] operadorRestrictions,
			String[] tipoValorRestrictions) throws ClassNotFoundException, SecurityException, NoSuchMethodException,
			IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {

		if (null != campoRestrictions && null != valorRestrictions && null != operadorRestrictions) {
			String operador = null;
			String campo = null;
			Object valor = null;

			for (int i = 0; campoRestrictions != null && i < campoRestrictions.length; i++) {
				if (!StringUtils.nullToString(campoRestrictions[i]).equals("")
						&& !StringUtils.nullToString(valorRestrictions[i]).equals("")) {

					Class clase = Class.forName(tipoValorRestrictions[i]);
					Constructor cons = clase.getConstructor(String.class);
					operador = operadorRestrictions[i];
					campo = campoRestrictions[i];

					if (operador.compareTo("in") == 0) {
						Object[] valores = ListUtils.getListaParaIn(valorRestrictions[i], clase);
						this.creaRestrictionsCriteriaIN(criteria, campo, operador, valores);
					} else {
						valor = cons.newInstance(valorRestrictions[i]);
						this.creaRestrictionsCriteria(criteria, campo, operador, valor);
					}
					operador = null;
					campo = null;
					valor = null;
				}
			}
		}

		for (int i = 0; filtro != null && i < filtro.length; i++) {
			if (!StringUtils.nullToString(filtro[i]).equals("")) {
				Class<?> clase;
				Object valor;
				if (tipoFiltro != null && tipoFiltro.length > i) {
					clase = Class.forName(tipoFiltro[i]);
				} else {
					clase = String.class;
				}
				if (String.class.equals(clase)) {
					criteria.add(Restrictions.ilike(campoFiltro[i], "%" + filtro[i] + "%"));
				} else {
					Constructor<?> cons = clase.getConstructor(String.class);
					valor = cons.newInstance(filtro[i]);
					criteria.add(Restrictions.eq(campoFiltro[i], valor));
				}
			}
		}

	}

	/**
	 * Metodo para contar los elementos de un listado generico
	 */
	public int getCountData(String objeto, String[] filtro, String[] campoFiltro, String[] tipoFiltro,
			String[] valorDepende, String[] campoBeanDepende, String[] tipoBeanDepende, String[] campoListado,
			String[] campoBeanDistinct, String[] campoBeanIsNull, String[] campoLeftJoin, String[] campoRestrictions,
			String[] valorRestrictions, String[] operadorRestrictions, String[] tipoValorRestrictions) {

		Session sesion = this.obtenerSession();
		
		// Mapa para guardar los alias creados
		Map<String, Boolean> mapAlias = new HashMap<String, Boolean>();

		try {

			Criteria criteria = sesion.createCriteria(objeto);
			
			createAlias(objeto, criteria, campoRestrictions, mapAlias);
			
			// Filtros
			this.creaFiltros(criteria, filtro, campoFiltro, tipoFiltro, campoRestrictions, valorRestrictions,
					operadorRestrictions, tipoValorRestrictions);

			// Campos dependientes
			this.createCriteriaLupaGenerica(valorDepende, campoBeanDepende, tipoBeanDepende, objeto, criteria,
					campoListado, campoLeftJoin, mapAlias);

			// Campos is null
			if (campoBeanIsNull != null && campoBeanIsNull.length > 0) {
				for (int i = 0; i < campoBeanIsNull.length; i++) {
					criteria.add(Restrictions.isNull(campoBeanIsNull[i]));
				}
			}
			// Distinct
			if (campoBeanDistinct != null && campoBeanDistinct.length > 0) {

				criteria.setProjection(Projections.countDistinct(campoBeanDistinct[0]));

				return Integer.parseInt(criteria.uniqueResult().toString());
			}

			return Integer.parseInt(criteria.setProjection(Projections.rowCount()).uniqueResult().toString());
		} catch (Exception e) {
			logger.error("Error inesperado al obtener el numero de elementos del listado de " + objeto, e);
		}

		return 0;
	}

	private void createCriteriaLupaGenerica(String[] valorDepende, String[] campoBeanDepende, String[] tipoBeanDepende,
			String objeto, Criteria criteria, String[] campoListado, String[] campoLeftJoin,
			Map<String, Boolean> mapAlias) throws ClassNotFoundException, NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException {

		// Left joins
		for (int j = 0; campoLeftJoin != null && j < campoLeftJoin.length; j++) {
			criteria.createAlias(campoLeftJoin[j], campoLeftJoin[j], Criteria.LEFT_JOIN);
			mapAlias.put(campoLeftJoin[j], true);
		}

		// MPM - 08/08/12
		// Alias para los campos a mostrar
		for (int j = 0; campoListado != null && j < campoListado.length; j++) {
			// Se crean los alias correspondientes para todos los campos que
			// tengan '.' en su nombre y no sean 'id'
			createAlias(objeto, criteria, campoListado[j], mapAlias);
		}
		// Alias para los campos de busqueda
		for (int k = 0; campoBeanDepende != null && k < campoBeanDepende.length; k++) {
			// Se crean los alias correspondientes para todos los campos que
			// tengan '.' en su nombre y no sean 'id'
			createAlias(objeto, criteria, campoBeanDepende[k], mapAlias);
		}

		for (int i = 0; valorDepende != null && i < valorDepende.length; i++) {

			Class clase = Class.forName(tipoBeanDepende[i]);
			Constructor cons = clase.getConstructor(String.class);

			if (!StringUtils.nullToString(valorDepende[i]).equals("") && valorDepende[i].indexOf("%") >= 0) {
				// En este caso vamos estamos buscando entidades mediadoras por
				// lo que cambiamos el % por
				// valores del 3 al 8 para buscar con el operador IN
				List listaValores = new ArrayList();
				for (int k = 3; k <= 8; k++) {
					Object valorAux = cons
							.newInstance((k + "" + valorDepende[i].substring(valorDepende[i].indexOf("%") + 1)));
					listaValores.add(valorAux);
				}
				criteria.add(Restrictions.in(campoBeanDepende[i], listaValores));
			} else if (!StringUtils.nullToString(valorDepende[i]).equals("") && valorDepende[i].indexOf(",") >= 0) {
				// En este caso es un filtro con el operador IN para cada uno de
				// los valores separados por comas
				List listaValores = new ArrayList();
				for (String cadena : valorDepende[i].split(",")) {
					Object valorAux = cons.newInstance(cadena);
					listaValores.add(valorAux);
				}
				criteria.add(Restrictions.in(campoBeanDepende[i], listaValores));
			} else if (!StringUtils.nullToString(valorDepende[i]).equals("") && valorDepende[i].indexOf(",") < 0) {
				// El valor es un valor simple => se utilizara el operador IGUAL
				Object valorAux = cons.newInstance(valorDepende[i]);
				if (clase.equals(String.class)) {
					criteria.add(Restrictions.ilike(campoBeanDepende[i], "%" + valorAux + "%"));
				} else {
					if (campoBeanDepende[i].equals("usuario.subentidadMediadora.id.codsubentidad")) {
						criteria.add(Restrictions.eq("esMed.id.codsubentidad", valorAux));
					} else if (campoBeanDepende[i].equals("usuario.subentidadMediadora.id.codentidad")) {
						criteria.add(Restrictions.eq("esMed.id.codentidad", valorAux));
					} else {
						criteria.add(Restrictions.eq(campoBeanDepende[i], valorAux));
					}

				}
			}

		}
	}

	private void createAlias(String objeto, Criteria criteria, String[] campos, Map<String, Boolean> mapAlias) {
		if (campos != null) {
			for (String campo : campos) {
				createAlias(objeto, criteria, campo, mapAlias);
			}
		}
	}
	
	/**
	 * Metodo para crear dinamicamente los alias necearios para obtener un listado.
	 */
	private void createAlias(String objeto, Criteria criteria, String campo, Map<String, Boolean> mapAlias) {
		if (campo != null && campo.indexOf(".") >= 0) {
			// Se parte la cadena por el '.' y se comprueba que no sea id y que
			// no se haya creado antes el alias
			String[] aux = campo.split("\\.");
			if (objeto.equals("com.rsi.agp.dao.tables.admin.Asegurado")) {
				if (!"id".equals(aux[0]) && !mapAlias.containsKey(aux[0]) && !mapAlias.containsKey("usu." + aux[1])) {
					mapAlias.put(aux[0], true);
					if ("usuario".equals(aux[0])) {
						mapAlias.put("usu." + aux[1], true);
						criteria.createAlias(aux[0], "usu");
						criteria.createAlias("usu." + aux[1], "esMed");
					} else {
						criteria.createAlias(aux[0], aux[0]);
					}
				}
			} else {
				if (!"id".equals(aux[0]) && !mapAlias.containsKey(aux[0])) {
					// Se crea el alias y se guarda en el mapa para no repetirlos
					mapAlias.put(aux[0], true);
					criteria.createAlias(aux[0], aux[0]);
				}
			}
		}
	}

	/**
	 * DAA 29/01/13 Obtiene la lista de tablas que faltan para completar el proceso
	 * de Activacion de la linea
	 * 
	 */

	public List getTablasPendientes(BigDecimal lineaSeguroId) throws DAOException {
		Session session = obtenerSession();
		try {
			Linea lin = (Linea) this.getObject(Linea.class, lineaSeguroId.longValue());
			String activacion = "t.activacion IN ('SC'";
			if (null != lin) {
				if (lin.isLineaGanado()) {
					activacion += ", 'SG')";
				} else {
					activacion += ", 'SA')";
				}
			} else {
				activacion += ")";
			}

			String sql = "select distinct c.destablacondicionado from o02agpe0.tb_tablas_xmls t, o02agpe0.tb_sc_c_tablas_condicionado c "
					+ "where t.numtabla = c.codtablacondicionado and t.numtabla not in ("
					+ "select i.codtablacondicionado from o02agpe0.tb_hist_importaciones h, o02agpe0.tb_importacion_tablas i "
					+ "where h.idhistorico = i.idhistorico and h.lineaseguroid = " + lineaSeguroId
					+ ") and t.tiposc = 'CPL' and " + activacion;
			List list = session.createSQLQuery(sql).list();

			return list;
		} catch (Exception excepcion) {
			logger.error("Error al recuperar las tablas que faltan para completar el proceso de Activacion", excepcion);
			throw new DAOException("Error al recuperar las tablas que faltan para completar el proceso de Activacion",
					excepcion);
		}
	}

	/**
	 * Genera una lista de objetos de la clse 'objeto' a partir de la lista indicada
	 */
	private List parsearArrayObjects(String objeto, List lista) {
		List listaNueva = new ArrayList();

		try {
			// Se obtiene el contructor que recibe como parametros un array de
			// Objects
			Constructor constructor = Class.forName(objeto).getConstructor(new Class[] { Object[].class });
			for (Object object : lista) {
				Object nuevoObj = Class.forName(objeto);
				// Crea el objeto correspondiente al array de Objects y lo
				// pone en la lista
				nuevoObj = constructor.newInstance(object instanceof Object[] ? object : new Object[] {object});
				listaNueva.add(nuevoObj);
			}
		} catch (Exception e) {
			logger.debug("Ocurrio un error al parsear los elementos del array de objetos", e);
		}

		return listaNueva;
	}

}
