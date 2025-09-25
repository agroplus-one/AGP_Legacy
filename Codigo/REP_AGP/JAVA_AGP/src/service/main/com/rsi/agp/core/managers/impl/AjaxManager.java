package com.rsi.agp.core.managers.impl;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.models.commons.IAjaxDao;
import com.rsi.agp.dao.models.commons.ICommonDao;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.Poliza;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class AjaxManager implements IManager {
	
	private ICommonDao commonDao;
	private IAjaxDao ajaxDao;
	
	private final Log logger = LogFactory.getLog(getClass());

	public List getPlanes() {
		List listPlanes = null;
		try {
			listPlanes = commonDao.getPlanes();
		} catch (Exception exception) {
			logger.error(exception);
		}
		return listPlanes;
	}

	public List getLineas(BigDecimal codPlan) {
		List listLineas = null;
		try {
			listLineas = commonDao.getLineas(codPlan);
		} catch (Exception exception) {
			logger.error(exception);
		}
		return listLineas;
	}

	public Linea getPlanLinea(Long lineaseguroid) {
		Linea linea = null;
		try {
			linea = commonDao.getPlanLinea(lineaseguroid);
		} catch (Exception exception) {
			logger.error(exception);
		}
		return linea;
	}

	public int getNumInstalaciones(Long idParcela) throws ParseException, BusinessException {
		int num = 0;

		try {
			num = commonDao.getNumInstalaciones(idParcela);
		} catch (Exception exception) {
			logger.error("Se ha producido un error al obtener el numero de instalaciones de la parcela", exception);
			throw new BusinessException("Se ha producido un error al obtener el numero de instalaciones de la parcela",
					exception);
		}

		return num;
	}

	public void setCommonDao(ICommonDao commonDao) {
		this.commonDao = commonDao;
	}

	public void setAjaxDao(IAjaxDao ajaxDao) {
		this.ajaxDao = ajaxDao;
	}

	public Poliza getPoliza(Long id) {
		logger.debug("init - [metodo] getPoliza");

		Poliza poliza = (Poliza) ajaxDao.getObject(Poliza.class, id);

		logger.debug("end - [metodo] getPoliza");
		return poliza;
	}

	/**
	 * Método para obtener un listado genérico cualquier lupa mediante ajax.
	 * 
	 * @param objeto
	 *            Objeto del que queremos el listado
	 * @param posicion
	 *            Pagina a mostrar en el listado.
	 * @param filtro
	 *            Valores para los filtros
	 * @param campoFiltro
	 *            Campos para los filtros
	 * @param campoListado
	 *            Campos a mostrar en el listado
	 * @param campoDepende
	 *            Valores de los que depende el listado
	 * @param campoBeanDepende
	 *            Campos de los que depende en el listado
	 * @param tipoBeanDepende
	 * @param campoDevolver
	 *            Campos a devolver
	 * @param campoBDDevolver
	 *            Campos del bean a devolver
	 * @param objetoActual
	 *            Objeto javascript con el que estamos trabajando
	 * @param campoOrden
	 *            Campo por el que se ordena el listado
	 * @param tipoOrden
	 *            Tipo de ordenacion del listado
	 * @param direccion
	 *            Parametro para la paginacion.
	 * @return Objeto con el listado solicitado y toda la informacion necesaria para
	 *         mostrar el popup
	 */
	public JSONObject getData(String objeto, Integer posicion, String[] filtro, String[] campoFiltro,
			String[] tipoFiltro, String[] campoListado, String[] valorDepende, String[] campoBeanDepende,
			String[] tipoBeanDepende, String objetoActual, String campoOrden, String tipoOrden, String direccion,
			String[] campoBeanDistinct, String[] campoBeanIsNull, String[] campoLeftJoin, String[] campoRestrictions,
			String[] valorRestrictions, String[] operadorRestrictions, String[] tipoValorRestrictions,
			String[] valorDependeGenerico, String[] valorDependeOrdenGenerico, String acumularResultados) {

		List lista = null;

		if ("S".equals(acumularResultados)) {
			// preparamos los valorDepende. a�adimos los nuevos valores al string con comas
			// para que los interpete como IN en la query.
			for (int x = 0; x < valorDependeGenerico.length; x++) {
				if (!"".equals(valorDependeGenerico[x]) && !"0".equals(valorDependeGenerico[x])) {
					valorDepende[x] = valorDepende[x].concat(valorDepende[x].length() != 0 ? "," : "")
							.concat(valorDependeGenerico[x]);
				}
			}

			lista = this.ajaxDao.getDataAcumularResultados(objeto, posicion, filtro, campoFiltro, tipoFiltro,
					valorDepende, campoBeanDepende, tipoBeanDepende, campoOrden, tipoOrden, campoListado,
					campoBeanDistinct, campoBeanIsNull, campoLeftJoin, campoRestrictions, valorRestrictions,
					operadorRestrictions, tipoValorRestrictions, valorDependeGenerico);

		} else {

			lista = this.ajaxDao.getData(objeto, posicion, filtro, campoFiltro, tipoFiltro, valorDepende,
					campoBeanDepende, tipoBeanDepende, campoOrden, tipoOrden, campoListado, campoBeanDistinct,
					campoBeanIsNull, campoLeftJoin, campoRestrictions, valorRestrictions, operadorRestrictions,
					tipoValorRestrictions);
			// MPM - B�squedas gen�ricas
			// Si la b�squeda con valores exactos no devuelve registros y se han configurado
			// valores de b�squeda gen�ricos
			if ((lista == null || lista.isEmpty()) && valorDependeGenerico != null && valorDependeGenerico.length > 0) {

				// Iteraci�n
				int ordenCampoABuscar = 1;
				int posBuscarCampoGen = -1;

				while ((lista == null || lista.isEmpty()) && ordenCampoABuscar <= valorDependeGenerico.length) {

					// Buscamos en el array de �rdenes gen�ricos la posici�n del siguiente campo a
					// buscar (posBuscarCampoGen)
					for (int i = 0; i < valorDependeOrdenGenerico.length; i++) {
						if (new Integer(valorDependeOrdenGenerico[i]).intValue() == ordenCampoABuscar) {
							posBuscarCampoGen = i;
							break;
						}
					}

					if (posBuscarCampoGen == -1)
						break;

					// Se actualiza el array de valores y se busca de nuevo
					valorDepende[posBuscarCampoGen] = valorDependeGenerico[posBuscarCampoGen];

					lista = this.ajaxDao.getData(objeto, posicion, filtro, campoFiltro, tipoFiltro, valorDepende,
							campoBeanDepende, tipoBeanDepende, campoOrden, tipoOrden, campoListado, campoBeanDistinct,
							campoBeanIsNull, campoLeftJoin, campoRestrictions, valorRestrictions, operadorRestrictions,
							tipoValorRestrictions);

					ordenCampoABuscar++;
				}
			}
		}
		// Relleno el JSONArray
		JSONArray array = new JSONArray();
		JSONObject object = null;
		JSONObject resultado = new JSONObject();

		try {
			// Añado al resultado los parámetros que ya conocemos
			resultado.put("objetoActual", objetoActual);
			resultado.put("tipoOrden", tipoOrden);
			resultado.put("campoOrden", campoOrden);
			resultado.put("direccion", direccion);
			resultado.put("numRegTotal",
					this.ajaxDao.getCountData(objeto, filtro, campoFiltro, tipoFiltro, valorDepende, campoBeanDepende,
							tipoBeanDepende, campoListado, campoBeanDistinct, campoBeanIsNull, campoLeftJoin,
							campoRestrictions, valorRestrictions, operadorRestrictions, tipoValorRestrictions));

			Class clase = Class.forName(objeto);
			for (Object elemento : lista) {
				object = new JSONObject();
				Object elementoAux = clase.cast(elemento);
				for (int i = 0; i < campoListado.length; i++) {
					if (campoListado[i].indexOf(".") < 0) {
						String metodo = "get" + campoListado[i].substring(0, 1).toUpperCase()
								+ campoListado[i].substring(1);
						Method method = clase.getMethod(metodo);
						Object valor = method.invoke(elementoAux);
						object.put(campoListado[i], valorToString(valor));
						
					} else {
						String[] propiedades = campoListado[i].split("\\.");
						String metodo = "get" + propiedades[0].substring(0, 1).toUpperCase()
								+ propiedades[0].substring(1);
						Method method = clase.getMethod(metodo);
						Object valor = method.invoke(elementoAux);
						if (valor != null) {
							for (int j = 1; j < propiedades.length; j++) {
								metodo = "get" + propiedades[j].substring(0, 1).toUpperCase()
										+ propiedades[j].substring(1);
								method = valor.getClass().getMethod(metodo);
								
								/* Pet. 63481 ** MODIF TAM (14.05.2021) ** Inicio */
								if (propiedades[j].equals("catalogo")) {
									Object valorAux = method.invoke(valor);
									
									if (valorAux.toString().equals("P")) {
										valor = "Poliza";
									}else {
										valor = "Siniestro";
									}
								}else {
									valor = method.invoke(valor);
								}
									
								/* Pet. 63481 ** MODIF TAM (14.05.2021) ** Fin */
								
								
							}
						}
						object.put(campoListado[i], valor != null ? valorToString(valor) : "");
					}
				}
				array.put(object);
			}

			// Añado el array a la lista
			resultado.put("lista", array);
		} catch (Exception e) {
			logger.error("Fallo al crear la lista de " + objeto, e);
		}
		return resultado;
	}
	
	private String valorToString(Object valor) {
		if (valor instanceof Date) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			return sdf.format(valor);
		} else {
			return valor.toString();
		}
	}

	/**
	 * DAA 29/01/2013 Obtiene la lista de tablas que faltan para completar el
	 * proceso de Activacion de la linea
	 * 
	 * @param lineaSeguroId
	 * @return List<TablaCondicionado>
	 */
	public List<String> getTablasPendientes(BigDecimal lineaSeguroId) {
		List<String> listTablas = null;
		try {
			listTablas = ajaxDao.getTablasPendientes(lineaSeguroId);

		} catch (DAOException e) {
			logger.error(
					"Error al recuperar las tablas que faltan para completar el proceso de Activacion",
					e);
		}
		return listTablas;
	}

}