package com.rsi.agp.pagination;


import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;
import java.util.List;


public class PaginatedListImpl<T> implements PaginatedList{
	
	private int            fullListSize;
	private int            objectsPerPage;
	private int            pageNumber;
	private String         searchId;
	private String         sortCriterion;
	private SortOrderEnum  sortDirection;
	private List<T>        list;
	  
    
   /**
    * Devuelve el tamaño total de la lista.
    * @return El tamaño total de toda la lista.
    */
	public int getFullListSize() {
		return fullListSize;
	}
	
   
	public void setFullListSize(int fullListSize) {
		this.fullListSize = fullListSize;
	}
	
	public int getObjectsPerPage() {
		return objectsPerPage;
	}
	
	public void setObjectsPerPage(int objectsPerPage) {
		this.objectsPerPage = objectsPerPage;
	}
	
   /**
    * Devuelve el número de página actual.
    * @return El número de página actual.
    */
	public int getPageNumber() {
		return pageNumber;
	}
	
   /**
    * Establece el tamaño de página actual.
    * @param pageNumber El tamaño de página a mostrar.
    */
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	
   /**
    * Devuelve el ID para localizar la lista en caso de que sea Cacheada en memoria.
    * @return El ID con el que obtener la lista de memoria o null si no se cachea.
    */
	public String getSearchId() {
		return searchId;
	}
	
   /**
    * Establece el ID para localizar la lista en caso de que sea Cacheada en memoria.
    * @return El ID con el que obtener la lista de memoria o null si no se cachea.
    */
	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}
	
   /**
	* Devuelve el criterio para odenar externamente la lista.
	* @return Devuelve el criterio para odenar externamente toda la lista.
	*/
	public String getSortCriterion() {
		return sortCriterion;
	}
	
   /**
    * Devuelve el SortOrderEnum con el que se indica la dirección de orden de la lista.
    * @return El objeto SortOrderEnum para ordenar la lista.
    */
	public void setSortCriterion(String sortCriterion) {
		this.sortCriterion = sortCriterion;
	}
	
	public SortOrderEnum getSortDirection() {
		return sortDirection;
	}
	
	public void setSortDirection(SortOrderEnum sortDirection) {
		this.sortDirection = sortDirection;
	}
	
   /**
    * Devuelve la lista parcial actual.
    * @return La lista parcial.
    */
	public List<T> getList() {
		return list;
	}
	
   /**
    * Establece la lista parcial a mostrar.
    * @param list La lista a establecer para este momento.
    */
	public void setList(List<T> list) {
		this.list = list;
	}
	
   /**
    * Devuelve el numero total de pï¿½ginas.
    * @return El número total de páginas.
    */
    public int getTotalPages()
    {
        double aux = Math.ceil(new Double(this.fullListSize).doubleValue() / new Double(this.objectsPerPage).doubleValue());

        return new Double(aux).intValue();
    }
}

