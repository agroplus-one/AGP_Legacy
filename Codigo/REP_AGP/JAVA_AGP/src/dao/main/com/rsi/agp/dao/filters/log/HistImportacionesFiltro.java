package com.rsi.agp.dao.filters.log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.log.HistImportaciones;
import com.rsi.agp.dao.tables.log.ImportacionTabla;
import com.rsi.agp.dao.tables.log.TipoImportacion;
import com.rsi.agp.dao.tables.poliza.Linea;
/*
**************************************************************************************************
*
*  CREACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034  28/06/2010  Ernesto Laura     Filtro hibernate para importaciones de la tabla de historico de importaciones      
*
 **************************************************************************************************
*/
public class HistImportacionesFiltro implements Filter, Serializable {
    private Long idhistorico;
    private ArrayList<Long> variosIds;
    private TipoImportacion tipoImportacion;
    private String ubicacionficheros;
    private Date fechaimport;
    private String estado;
    private Linea linea;
    //private BigDecimal lineaseguroid;
    private String activo;
    private Date fechaactivacion;
    private Set<ImportacionTabla> importacionTablas = new HashSet<ImportacionTabla>(0);
    private Date fechaDesde;
    private Date fechaHasta;
	
	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(HistImportaciones.class);
		
		//Se recorren los atributos del objeto filtro y se van añadiendo los "criterion" al criteria
		if (this.getIdhistorico()!=null)
		{
			Criterion crit = Restrictions.eq("idhistorico", this.getIdhistorico());
			criteria.add(crit);
		}
		
		if (this.linea!=null)
		{
			if (this.linea.getCodplan()!=null)
			{
				criteria.createAlias("linea", "l");
				Criterion crit = Restrictions.eq("l.codplan", this.linea.getCodplan());
				criteria.add(crit);
			}
			if (this.linea.getLineaseguroid()!=null)
			{
				Criterion crit = Restrictions.eq("linea.lineaseguroid", this.linea.getLineaseguroid());
				criteria.add(crit);
			}
		}
		
		if (this.activo!=null)
		{
			Criterion crit = Restrictions.eq("activo", this.activo);
			criteria.add(crit);			
		}
		
		if (this.fechaDesde != null && this.fechaHasta != null){
			Criterion crit = Restrictions.between("fechaimport", this.fechaDesde, this.fechaHasta);
			criteria.add(crit);
		}
		else {
			if (this.fechaDesde!=null){
				Criterion crit = Restrictions.ge("fechaimport", this.fechaDesde);
				criteria.add(crit);
			}
			
			if (this.fechaHasta!=null){
				Criterion crit = Restrictions.le("fechaimport", this.fechaHasta);
				criteria.add(crit);
			}
		}
		
		if (this.fechaactivacion!=null)
		{
			Criterion crit = Restrictions.eq("fechaactivacion", this.fechaactivacion);
			criteria.add(crit);	
		}
		
		if (this.tipoImportacion != null)
		{
			if (this.tipoImportacion.getIdtipoimportacion()!=null){
				Criterion crit = Restrictions.eq("tipoImportacion.idtipoimportacion", this.getTipoImportacion().getIdtipoimportacion());
				criteria.add(crit);
			}
		}
				
		if (this.estado!= null && !this.estado.equalsIgnoreCase(""))
		{
			Criterion crit = Restrictions.eq("estado", this.estado);
			criteria.add(crit);			
		}
		
		if (this.variosIds !=null && this.variosIds.size() > 0)
		{
			Criterion crit = Restrictions.in("idhistorico", this.variosIds);
			criteria.add(crit);
		}
		
		//Mostramos siempre los datos ordenados por fecha de importacion
		Order or = Order.desc("fechaimport");
		criteria.addOrder(or);
		
		return criteria;
	}
		
	public Long getIdhistorico() {
		return idhistorico;
	}

	public void setIdhistorico(Long idhistorico) {
		this.idhistorico = idhistorico;
	}

	public TipoImportacion getTipoImportacion() {
		return tipoImportacion;
	}

	public void setTipoImportacion(TipoImportacion tipoImportacion) {
		this.tipoImportacion = tipoImportacion;
	}

	public String getUbicacionficheros() {
		return ubicacionficheros;
	}

	public void setUbicacionficheros(String ubicacionficheros) {
		this.ubicacionficheros = ubicacionficheros;
	}

	public Date getFechaimport() {
		return fechaimport;
	}

	public void setFechaimport(Date fechaimport) {
		this.fechaimport = fechaimport;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	/*public BigDecimal getLineaseguroid() {
		return lineaseguroid;
	}

	public void setLineaseguroid(BigDecimal lineaseguroid) {
		this.lineaseguroid = lineaseguroid;
	}*/

	public String getActivo() {
		return activo;
	}

	public void setActivo(String activo) {
		this.activo = activo;
	}

	public Date getFechaactivacion() {
		return fechaactivacion;
	}

	public void setFechaactivacion(Date fechaactivacion) {
		this.fechaactivacion = fechaactivacion;
	}

	public Set<ImportacionTabla> getImportacionTablas() {
		return importacionTablas;
	}

	public void setImportacionTablas(Set<ImportacionTabla> importacionTablas) {
		this.importacionTablas = importacionTablas;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public Linea getLinea() {
		return linea;
	}

	public void setLinea(Linea linea) {
		this.linea = linea;
	}

	public ArrayList<Long> getVariosIds() {
		return variosIds;
	}

	public void setVariosIds(ArrayList<Long> variosIds) {
		this.variosIds = variosIds;
	}
}
