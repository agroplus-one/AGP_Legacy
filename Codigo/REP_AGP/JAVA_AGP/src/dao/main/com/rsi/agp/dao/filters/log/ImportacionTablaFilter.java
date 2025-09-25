/*
**************************************************************************************************
*
*  CREACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034  28/06/2010  Ernesto Laura     Filtro hibernate para detalle de importaciones      
*
 *************************************************************************************************/
package com.rsi.agp.dao.filters.log;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cgen.TablaCondicionado;
import com.rsi.agp.dao.tables.log.HistImportaciones;
import com.rsi.agp.dao.tables.log.ImportacionTabla;
import com.rsi.agp.dao.tables.log.ImportacionTablaId;

public class ImportacionTablaFilter implements Filter {
    private ImportacionTablaId id;
    private TablaCondicionado tablaCondicionado;
    private HistImportaciones histImportaciones;
    private String estado;
    private String descestado;
    
    public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(ImportacionTabla.class);
		
		//Se recorren los atributos del objeto filtro y se van añadiendo los "criterion" al criteria		
		if (this.id!= null)
		{
			if (this.id.getIdhistorico()!=null)
			{
				Criterion crit = Restrictions.eq("id.idhistorico", this.id.getIdhistorico());
				criteria.add(crit);
			}
		}
				
		return criteria;
	}

	public ImportacionTablaId getId() {
		return id;
	}

	public void setId(ImportacionTablaId id) {
		this.id = id;
	}

	public TablaCondicionado getTablaCondicionado() {
		return tablaCondicionado;
	}

	public void setTablaCondicionado(TablaCondicionado tablaCondicionado) {
		this.tablaCondicionado = tablaCondicionado;
	}

	public HistImportaciones getHistImportaciones() {
		return histImportaciones;
	}

	public void setHistImportaciones(HistImportaciones histImportaciones) {
		this.histImportaciones = histImportaciones;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getDescestado() {
		return descestado;
	}

	public void setDescestado(String descestado) {
		this.descestado = descestado;
	}

}
