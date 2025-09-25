/*
**************************************************************************************************
*
*  CReACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034              Miguel Granadino  Dao especifico para la pantalla relación campos
*
 **************************************************************************************************
*/
package com.rsi.agp.dao.models.config;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.rsi.agp.dao.filters.commons.CampoSCFiltro;
import com.rsi.agp.dao.filters.commons.LineasFiltro;
import com.rsi.agp.dao.filters.commons.PlanFiltro;
import com.rsi.agp.dao.filters.config.RelacionCamposConsultaFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.config.GrupoFactores;
import com.rsi.agp.dao.tables.config.RelacionCampo;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;
import com.rsi.agp.dao.tables.org.Uso;

public class RelCamposDao extends BaseDaoHibernate implements IRelCamposDao {

	
	public List getUsos(){
		return this.getObjects(Uso.class, null, null);
	}
	
	public List getPlanes(){
		return this.getObjects(new PlanFiltro());
	}
	
	public List getLineas(BigDecimal codPlan){
	      LineasFiltro filtro = new LineasFiltro();
	      filtro.setCodPlan(codPlan);
		  return this.getObjects(filtro);
	}
	
	public List getRelacionesCampos(){
		return this.getObjects(RelacionCampo.class, null, null);
	}
	
	public List getRelacionCamposConsulta(RelacionCampo rc){
		RelacionCamposConsultaFiltro filtro = new RelacionCamposConsultaFiltro(rc);
		return this.getObjects(filtro);
	}
	
	public List getCampoSC(Long linea, BigDecimal uso,BigDecimal ubicacion) {
		return this.getObjects(new CampoSCFiltro(linea, uso, ubicacion));	
	}
	
	/**
	 * Método para obtener el grupo de factores al que está asociado un campo
	 * @param camposc Campo sobre el que comprobar si tiene grupo de factores
	 * @return Campo del diccionario de datos cuyo identificador coincide con camposc
	 */
	public Object getGruposFactores(BigDecimal camposc){
		return this.getObject(DiccionarioDatos.class, camposc);
	}
	
	/**
	 * Método para obtener los factores asociados a un grupo. En el objeto GrupoFactores, vendrá un Set de factores (conceptos)
	 * @param codgrupo Código del grupo de factores
	 * @return Objeto GrupoFactores cuyo identificador coincida con codgrupo 
	 */
	public Object getFactoresPorGrupo(BigDecimal codgrupo) {
		return this.getObject(GrupoFactores.class, codgrupo);
	}
		
	
}