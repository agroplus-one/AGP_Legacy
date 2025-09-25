/*
**************************************************************************************************
*
*  CReACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034              Miguel Granadino  Dao especifico para la pantalla pantallasConfigurables.jsp
*
 **************************************************************************************************
*/
package com.rsi.agp.dao.models.config;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Session;

import com.rsi.agp.dao.filters.commons.IdLineaFiltro;
import com.rsi.agp.dao.filters.config.PantallaConfigurableConsultaFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.config.Pantalla;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;

public class PantallasConfigurablesDao  extends BaseDaoHibernate implements IPantallasConfigurablesDao{
	public List getPantallas(){
		return this.getObjects(Pantalla.class, null, null);
	}
	
	public List getPantallasConfigurables(){
        return this.getObjects(PantallaConfigurable.class, null, null);
	}
	
	public List<PantallaConfigurable> getPantallasConfigurablesConsulta(PantallaConfigurable pantallaConfigurable){
		PantallaConfigurableConsultaFiltro filtro = new PantallaConfigurableConsultaFiltro(pantallaConfigurable);
		return this.getObjects(filtro);
	}
	
	public List getPantallasConfigurables(Long idLinea){
		IdLineaFiltro filtro = new IdLineaFiltro();
		filtro.setIdLinea(idLinea);
		return this.getObjects(filtro);
	}
	
	public Pantalla getPantalla(Long idPantalla){
		return (Pantalla)this.getObject(Pantalla.class, idPantalla);
	}
	
	public boolean existePantalla(Long lineaseguroid, Long idpantalla, Long idpantallaconfigurable){
		Session session = obtenerSession();
		
		String sql= "select count(*) from tb_pantallas_configurables pc where pc.lineaseguroid = " + 
			lineaseguroid + " and idpantalla = " + idpantalla;
		
		if (idpantallaconfigurable != null){
			//si nos pasan como parametro un id, es para verificar que no hay otra con el mismo idpantalla, lineaseguroid y distinto id 
			sql += " and idpantallaconfigurable != " + idpantallaconfigurable;
		}
		
		List list = session.createSQLQuery(sql).list();
		
		return ( (BigDecimal)list.get(0) ).intValue() > 0;

	}
	public String obtieneGrupoSeguro(BigDecimal codLinea){
		Session session =obtenerSession();
		String sql="select CODGRUPOSEGURO from TB_SC_C_LINEAS where codlinea="+codLinea;
		List list = session.createSQLQuery(sql).list();
		if (list != null && !list.isEmpty())
			return  (String)list.get(0);
		else
			return "";
	}
	
}


