package com.rsi.agp.dao.tables.poliza;

import java.util.ArrayList;
import java.util.List;

/**
 * Se encarga del control de la tabla de lista de capitales asegurados para indicar que precios
 * y producciones han sido modificadas (revProduccionPrecioController). De esta forma recogemos 
 * los valores a modificar de dicha tabla.
 */
public class ListaCapitalAsegurado extends CapitalAsegurado{

	private static final long serialVersionUID = -9102059645143402342L;
	
	List<String> idCapitalAseguradoFila = new ArrayList<String>();
	List<String> listaProduccionMod = new ArrayList<String>();
	List<String> listaPrecioMod = new ArrayList<String>();
	
    public ListaCapitalAsegurado() {
    	super();
    }
    
    public List<String> getIdCapitalAseguradoFila(){
    	return this.idCapitalAseguradoFila;
    }
	
    public void setIdCapitalAseguradoFila(List<String> idCapitalAseguradoFila){
    	this.idCapitalAseguradoFila = idCapitalAseguradoFila;
    }
    
    public List<String> getListaProduccionMod(){
    	return this.listaProduccionMod;
    }
	
    public void setListaProduccionMod(List<String> listaProduccionMod){
    	this.listaProduccionMod = listaProduccionMod;
    }
    
    public List<String> getListaPrecioMod(){
    	return this.listaPrecioMod;
    }
	
    public void setListaPrecioMod(List<String> listaPrecioMod){
    	this.listaPrecioMod = listaPrecioMod;
    }    
    
}
