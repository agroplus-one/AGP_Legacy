/*
**************************************************************************************************
*
*  CReACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034              Miguel Granadino  Dao con las funciones comunes a todo el proyecto.
*                           
*
*
**************************************************************************************************
*/


package com.rsi.agp.dao.models.commons;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.filters.commons.LineasFiltro;
import com.rsi.agp.dao.filters.commons.PlanFiltro;
import com.rsi.agp.dao.filters.commons.PlanLineaFiltro;
import com.rsi.agp.dao.filters.log.TipoImportacionFilter;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.Parcela;

@SuppressWarnings("rawtypes")
public class CommonDao  extends BaseDaoHibernate implements ICommonDao {

	/**
	 * Para obtener todos los planes sin repeticiones.
	 * 
	 */	
	public List getPlanes(){
		PlanFiltro filtro = new PlanFiltro();
		List result = this.getObjects(filtro);
		return result;
	}
	
	/**
	 * Para obtener las lineas de un determinado plan
	 */
	public List getLineas(BigDecimal codPlan){
	      LineasFiltro filtro = new LineasFiltro();
	      filtro.setCodPlan(codPlan);
		  return this.getObjects(filtro);
	}
	
	public List getLineasNoPlan(){
	      
		  return this.getObjectsBySQLQuery("select distinct codlinea,nomlinea from tb_lineas");
	}
	
	public List getLineaseguroid(BigDecimal codPlan, BigDecimal codLinea){
		LineasFiltro filtro = new LineasFiltro(codPlan, codLinea);
		return this.getObjects(filtro);
	}
	
	/**
	 * Para obtener el plan y linea sabiendo el lineasegurid.
	 */
	public Linea getPlanLinea(Long lineaSeguroId){
		PlanLineaFiltro planLineaFiltro = new PlanLineaFiltro();
		planLineaFiltro.setLineaSeguroId(lineaSeguroId);
		
		List lineas = this.getObjects(planLineaFiltro);
		Linea linea = null;
		if(lineas.size() > 0)
		    linea = (Linea)lineas.get(0);
		return linea;
	}
	
	public List getTiposImportacion () {
		TipoImportacionFilter tipImpFiltro = new TipoImportacionFilter();
		List result = this.getObjects(tipImpFiltro);
		return result;
	}
	
	public int getNumInstalaciones(Long idParcela){
		logger.debug("init - [DatosParcelaFLDao] getNumInstalaciones");
		int num = 0;
		
		Session session = obtenerSession();

		try{
			Criteria c = session.createCriteria(Parcela.class);
			Criterion crit = Restrictions.eq("idparcelaestructura",new Long(idParcela));
			c.add(crit);
			num = c.list().size();
		}
		catch(Exception e){
			logger.fatal("[DAOException sin throw][DatosParcelaFLDao][getNumInstalaciones]Error lectura BD", e);
		}
		
		logger.debug("end - [DatosParcelaFLDao] getNumInstalaciones");
		return num;
	}
	
	public int getMaxIdZonificacionSIGPAC() throws DAOException{
		Session session = obtenerSession();
		List list;
		try {
			String sql = "select max(id) from TB_SC_C_ZONIF_SIGPAC";
			list = session.createSQLQuery(sql).list();
			return ( (BigDecimal)list.get(0) ).intValue();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
}

