package com.rsi.agp.dao.filters.admin.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.admin.Asegurado;


public class Asegurado2Filtro implements Filter {

	private Asegurado asegurado;
	private Integer posicion;
	private String filtro;
	private BigDecimal codEntidad;
	private String nifcif;
	private List<BigDecimal> listaEnt;
	private Asegurado aseguradoBean;
	private String discriminante;
	private String perfil;
	private Map<String, Object>  mapa;	
	
	public Asegurado2Filtro() {
		
	}

	public Asegurado2Filtro(final Asegurado asegurado) {
		this.asegurado = asegurado;
	}

	public Asegurado2Filtro(final String filtro, final BigDecimal codEntidad) {
		this.filtro = filtro;
		this.codEntidad = codEntidad;
	}

	public Asegurado2Filtro(final Integer posicion, final String filtro, final BigDecimal codEntidad) {
		this.posicion = posicion;
		this.filtro = filtro;
		this.codEntidad = codEntidad;
	}

	public Asegurado2Filtro(final BigDecimal codentidad, final String nifcif, final String discriminante) {
		this.codEntidad = codentidad;
		this.nifcif = nifcif;
		this.discriminante = discriminante;
	}

	@Override
	public Criteria getCriteria(final Session sesion) {
		
		final Criteria criteria = sesion.createCriteria(Asegurado.class);

		criteria.createAlias("entidad", "ent");
		
		//Eliminamos estos cuatro campos del mapa y aÃ±adimos restriciones para dichos campos.
		String nombre = "";
		String apellido1 = "";
		String apellido2 = "";
		String razonsocial = "";
		if(aseguradoBean.getNombre()!= null)
			if(!aseguradoBean.getNombre().equals("")){
			   nombre = aseguradoBean.getNombre();
			   mapa.remove("nombre");
			   criteria.add(Restrictions.ilike("nombre", "%".concat(nombre).concat("%")));
		    }
		if(aseguradoBean.getApellido1()!= null)
			if(!aseguradoBean.getApellido1().equals("")){
			   apellido1 = aseguradoBean.getApellido1();
			   mapa.remove("apellido1");
			   criteria.add(Restrictions.ilike("apellido1", "%".concat(apellido1).concat("%")));
		    }
		if(aseguradoBean.getApellido2()!= null)
			if(!aseguradoBean.getApellido2().equals("")){
			   apellido2 = aseguradoBean.getApellido2();
			   mapa.remove("apellido2");
			   criteria.add(Restrictions.ilike("apellido2", "%".concat(apellido2).concat("%")));
		    }
		if(aseguradoBean.getRazonsocial()!= null)
			if(!aseguradoBean.getRazonsocial().equals("")){
			   razonsocial = aseguradoBean.getRazonsocial();
			   mapa.remove("razonsocial");
			   criteria.add(Restrictions.ilike("razonsocial", "%".concat(razonsocial).concat("%")));
			}
		
		criteria.createAlias("usuario", "usu");
		criteria.createAlias("usu.subentidadMediadora", "esMed", CriteriaSpecification.LEFT_JOIN);
		
		if (mapa.get("usuario.subentidadMediadora.id.codentidad") != null) {
			criteria.add(Restrictions.eq("esMed.id.codentidad",
					mapa.get("usuario.subentidadMediadora.id.codentidad")));
			mapa.remove("usuario.subentidadMediadora.id.codentidad");
		}
		if (mapa.get("usuario.subentidadMediadora.id.codsubentidad") != null) {
			criteria.add(Restrictions.eq("esMed.id.codsubentidad",
					mapa.get("usuario.subentidadMediadora.id.codsubentidad")));
			mapa.remove("usuario.subentidadMediadora.id.codsubentidad");
		}
		
		//criteria.addOrder(Order.asc("ent.codentidad"));
		if (FiltroUtils.noEstaVacio(aseguradoBean)) {
			criteria.add(Restrictions.allEq(mapa));
		}
		//Si el perfil pertenece a algun grupo de Entidades
		if(listaEnt.size() > 0){
			if(aseguradoBean.getEntidad().getCodentidad() != null){
				criteria.add(Restrictions.eq("ent.codentidad", aseguradoBean.getEntidad().getCodentidad()));
			}else{
				criteria.add(Restrictions.in("ent.codentidad", listaEnt));
			}
			
		}else{
			final BigDecimal codEntidad = aseguradoBean.getEntidad().getCodentidad();
			if (FiltroUtils.noEstaVacio(codEntidad)) {
				criteria.add(Restrictions.eq("ent.codentidad", codEntidad));
			}
		}
		
		/* Pet. ESC-12906 ** MODIF TAM (19.01.2021) ** Inicio */
		criteria.add(Restrictions.ne("isBloqueado", Integer.valueOf(1)));
		/* Pet. 62719 ** MODIF TAM (19.01.2021) ** Fin */
		
		return criteria;
	}
	
	public String getSqlWhere() {
		String sqlWhere = " WHERE 1 = 1";
		
		if(!StringUtils.nullToString(aseguradoBean.getNombre()).equals(""))
			   sqlWhere += " AND A.NOMBRE LIKE '%" + aseguradoBean.getNombre() + "%'";

		if(!StringUtils.nullToString(aseguradoBean.getApellido1()).equals(""))
				sqlWhere += " AND A.APELLIDO1 LIKE '%" + aseguradoBean.getApellido1() + "%'";

		if(!StringUtils.nullToString(aseguradoBean.getApellido2()).equals(""))
			   sqlWhere += " AND A.APELLIDO2 LIKE '%" + aseguradoBean.getApellido2() + "%'";

		if(!StringUtils.nullToString(aseguradoBean.getRazonsocial()).equals(""))
			   sqlWhere += " AND A.RAZONSOCIAL LIKE '%" + aseguradoBean.getRazonsocial() + "%'";
		
		//Si el perfil pertenece a algun grupo de Entidades
		if(aseguradoBean.getEntidad().getCodentidad() != null){
			sqlWhere += " AND A.CODENTIDAD = " + aseguradoBean.getEntidad().getCodentidad();
		}else if(listaEnt.size() > 0){
			sqlWhere += " AND A.CODENTIDAD IN (";
			for (int i = 0; i < listaEnt.size(); i++){
				sqlWhere += listaEnt.get(i);
				if (i < listaEnt.size() - 1)
					sqlWhere += ",";
			}
			sqlWhere += ")";
		}
		
		for (Map.Entry<String, Object> entry : mapa.entrySet()){
			if (entry.getKey().indexOf(".") < 0){
				sqlWhere += " AND A." + entry.getKey() + " = '" + entry.getValue() + "'";
			}
			else if (entry.getKey().equals("via.clave")){
				sqlWhere += " AND A.CLAVEVIA = '" + entry.getValue() + "'";
			}
			else if (entry.getKey().indexOf("esMed.id.codentidad") >= 0){
				sqlWhere += " AND A.CODUSUARIO IN (SELECT CODUSUARIO FROM TB_USUARIOS WHERE ENTMEDIADORA = " + entry.getValue() + ")";
			}
			else if (entry.getKey().indexOf("esMed.id.codsubentidad") >= 0){
				sqlWhere += " AND A.CODUSUARIO IN (SELECT CODUSUARIO FROM TB_USUARIOS WHERE SUBENTMEDIADORA = " + entry.getValue() + ")";
			}
			else{
				sqlWhere += " AND A." + entry.getKey().substring(entry.getKey().lastIndexOf(".") + 1) + " = '" + entry.getValue() + "'";
			}
		}
		
		/* Pet. 62719 ** MODIF TAM (18.01.2021) ** Inicio */
		/* Incluimos validación para no recuperar aquellos Asegurados que estén bloqueados */
        sqlWhere += " AND (A.id NOT IN (SELECT BLOQA.ID_ASEGURADO FROM o02agpe0.TB_BLOQUEOS_ASEGURADOS BLOQA WHERE BLOQA.IDESTADO_ASEG = 'B'))";
		
		return sqlWhere;
	}
	
	public String getPerfil() {
		return perfil;
	}

	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}

	public Asegurado getAsegurado() {
		return asegurado;
	}

	public void setAsegurado(Asegurado asegurado) {
		this.asegurado = asegurado;
	}

	public Integer getPosicion() {
		return posicion;
	}

	public void setPosicion(Integer posicion) {
		this.posicion = posicion;
	}

	public String getFiltro() {
		return filtro;
	}

	public void setFiltro(String filtro) {
		this.filtro = filtro;
	}

	public BigDecimal getCodEntidad() {
		return codEntidad;
	}

	public void setCodEntidad(BigDecimal codEntidad) {
		this.codEntidad = codEntidad;
	}

	public String getNifcif() {
		return nifcif;
	}

	public void setNifcif(String nifcif) {
		this.nifcif = nifcif;
	}

	public List<BigDecimal> getListaEnt() {
		return listaEnt;
	}

	public void setListaEnt(List<BigDecimal> listaEnt) {
		this.listaEnt = listaEnt;
	}

	public String getDiscriminante() {
		return discriminante;
	}

	public void setDiscriminante(String discriminante) {
		this.discriminante = discriminante;
	}

	public Asegurado getAseguradoBean() {
		return aseguradoBean;
	}

	public void setAseguradoBean(Asegurado aseguradoBean) {
		this.aseguradoBean = aseguradoBean;
	}

	public Map<String, Object> getMapa() {
		return mapa;
	}

	public void setMapa(Map<String, Object> mapa) {
		this.mapa = mapa;
	}
	
}