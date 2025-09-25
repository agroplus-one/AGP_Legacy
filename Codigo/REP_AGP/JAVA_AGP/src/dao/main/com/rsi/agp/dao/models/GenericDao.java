package com.rsi.agp.dao.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.commons.Usuario;

@SuppressWarnings("rawtypes")
public interface GenericDao<T> {

	public Transaction beginTransaction();

	/**
	 * Metodo generico para salvar o actualizar un objeto
	 * 
	 * @param entity
	 *            Objeto a salvar
	 * @return Objeto resultante
	 */
	public Object saveOrUpdate(Object entity) throws DAOException;

	/**
	 * Metodo que guarda o actualiza todas las entidades contenidas en el vector de
	 * entrada. La operación es atómica
	 * 
	 * @param listaEntidades
	 *            Lista que contiene los elementos que serán guardados o
	 *            actualizados
	 * @return
	 * @throws DAOException
	 */
	public void saveOrUpdateList(List listaEntidades) throws DAOException;

	/**
	 * Metodo generico para obtener un objeto
	 * 
	 * @param clazz
	 *            clase del objecto
	 * @param id
	 *            clave primaria
	 * @return objecto de la base de datos
	 * @throws DAOException
	 */
	public Object get(Class clazz, Serializable id) throws DAOException;

	/**
	 * Metodo generico para borrar un objeto basado en una clase y un id.
	 * 
	 * @param id
	 *            Identificador (primary key) de la clase
	 */
	public void removeObject(Class clazz, Serializable id);

	/**
	 * 
	 * @param clazz
	 * @param id
	 * @throws DAOException
	 */
	public void delete(Class clazz, Serializable id) throws DAOException;

	/**
	 * Metodo generico para obtener un objeto en base a un identificador.
	 * 
	 * @param id
	 *            Identificador (primary key) de la clase
	 * @return Objeto buscado
	 */
	public void delete(Object entidad) throws DAOException;

	public void deleteAll(Collection lista) throws DAOException;

	public Object getObject(Class clazz, Serializable id);

	public Object getObject(Class clazz, String property, Object value);

	/**
	 * Metodo generico para obtener todos los objetos de un tipo indicado.
	 * 
	 * @return List Listado de los objetos buscados
	 */
	// public List<T> getObjects();

	/**
	 * Metodo generico para obtener todos los objetos de un tipo indicado y que
	 * cumplan el filtro indicado.
	 * 
	 * @param filter
	 *            Filtro indicado por el usuario
	 * @return List Listado de los objetos buscados
	 */
	public List<T> getObjects(Filter filter);

	/**
	 * Metodo para obtener un listado de todos los objetos que cumplen que la
	 * propiedad 'property' tiene el valor 'value'
	 * 
	 * @param property
	 *            Propiedad del objeto de base de datos
	 * @param value
	 *            Valor del objeto
	 * @return Listado de objetos
	 */
	public List<T> getObjects(Class clazz, String property, Object value);

	/**
	 * Metodo creado para obtener el numero de objetos que hay en una tabla
	 * 
	 * @param clazz
	 *            de la tabla que queremos consultar
	 * @return numero total de objetos en la tabla
	 */
	public Integer getNumObjects(Class clazz);

	/**
	 * Metodo creado para obtener el numero de objetos que hay en una tabla y
	 * cumplen unos requisitos
	 * 
	 * @param filter
	 *            indicando los requisitos
	 * @return numero de objetos de la ta bla que cumplen los requisitos
	 */
	public Integer getNumObjects(Filter filter);

	/**
	 * Elimina el objeto de la sesión de hibernate para recargarlo
	 */
	public void evict(Object entity);

	/**
	 * Metodo generico para salvar o actualizar un objeto y facturar
	 * 
	 * @param usuario
	 *            Objeto a salvar
	 * 
	 */
	public Object saveOrUpdateFacturacion(Object entity, Usuario usuario) throws DAOException;

	/**
	 * llama al pl de facturacion
	 * 
	 * @param usuario
	 * @param tipo
	 *            A-> actualizacion I-> Impresion
	 * @throws DAOException
	 */
	public void callFacturacion(Usuario usuario, String tipo) throws DAOException;

	/**
	 * Metodo generico para borrar un objecto y para facturar
	 * 
	 * @param entidad
	 * @param usuario
	 * @throws DAOException
	 */
	public void deleteFacturacion(Object entidad, Usuario usuario) throws DAOException;

	/**
	 * Metodo generico para boorar un objecto y facturar
	 * 
	 * @param clazz
	 * @param id
	 * @param usuario
	 */
	public void removeObjectFacturacion(Class clazz, Serializable id, Usuario usuario);

	/**
	 * Metodo para crear la restriccion
	 * 
	 * @param criteria
	 * @param campoFiltro
	 *            Nombre del campo a filtrar
	 * @param restriccion
	 *            nombre de la restriccion. Igual que el nombre del metodo de Class
	 *            Restrictions
	 * @param valor
	 *            Valor a asignar a la restriccion
	 */
	public void creaRestrictionsCriteria(Criteria criteria, String campoFiltro, String restriccion, Object valor);

	public void creaRestrictionsCriteriaIN(Criteria criteria, String campoFiltro, String restriccion, Object[] valor);

	public BigDecimal getSecuencia(String nombreSecuencia) throws DAOException;
}
