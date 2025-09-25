package com.rsi.agp.dao.filters.admin.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.admin.Colectivo;

public class Colectivo2Filtro implements Filter {

	private Colectivo            colectivo;
	private BigDecimal           codentidad;
	private String               ciftomador;
	private Long                 lineaseguroid;
	private Colectivo            colectivoBean;
	private List<BigDecimal>     listaEnt;
	private Map<String, Object>  mapa;
	private String				 perfilUsuario;
	private boolean              addFiltroFechaBaja;    
	private List<BigDecimal>     planesFiltroInicial;


	@Override
	public final Criteria getCriteria(final Session sesion) {
		
		Criteria criteria = sesion.createCriteria(Colectivo.class);
		
		criteria.createAlias("linea", "lin");
		criteria.createAlias("subentidadMediadora", "SM");
		criteria.createAlias("tomador", "tom");
		
		//criteria.addOrder(Order.asc("tom.id.codentidad"));
		//criteria.addOrder(Order.asc("SM.entidadMediadora.codentidad"));
		//criteria.addOrder(Order.desc("lin.codplan"));
		//criteria.addOrder(Order.asc("lin.codlinea"));
		//criteria.add(Restrictions.isNull("SM.fechabaja"));
		
		if (FiltroUtils.noEstaVacio(colectivoBean)) {
			criteria.add(Restrictions.allEq(mapa));
			
			final String nomColectivo = colectivoBean.getNomcolectivo();
			if (FiltroUtils.noEstaVacio(nomColectivo)) {
				criteria.add(Restrictions.ilike("nomcolectivo", "%".concat(nomColectivo).concat("%")));
			}
		}
//		Si el perfil pertenece a algun grupo de Entidades
		if(listaEnt != null && listaEnt.size() > 0){
			if(colectivoBean.getTomador().getId().getCodentidad() != null){
				criteria.add(Restrictions.eq("tom.id.codentidad", colectivoBean.getTomador().getId().getCodentidad()));
			}else{
				criteria.add(Restrictions.in("tom.id.codentidad", listaEnt));
			}
			
		}else{
			final BigDecimal codEntidad = colectivoBean.getTomador().getId().getCodentidad();
			if (FiltroUtils.noEstaVacio(codEntidad)) {
				criteria.add(Restrictions.eq("tom.id.codentidad", codEntidad));
			}
		}
		//DAA 22/05/2013
		if(!perfilUsuario.equals(Constants.PERFIL_USUARIO_ADMINISTRADOR)){
			criteria.add(Restrictions.isNull("fechabaja"));
		}	
		
		if (addFiltroFechaBaja){
			criteria.add(Restrictions.isNull("fechabaja"));
		}
		if (planesFiltroInicial.size()>0){
			criteria.add(Restrictions.in("lin.codplan", planesFiltroInicial));
		}
		
		return criteria;
	}



	public Colectivo getColectivo() {
		return colectivo;
	}



	public void setColectivo(Colectivo colectivo) {
		this.colectivo = colectivo;
	}



	public BigDecimal getCodentidad() {
		return codentidad;
	}



	public void setCodentidad(BigDecimal codentidad) {
		this.codentidad = codentidad;
	}



	public String getCiftomador() {
		return ciftomador;
	}



	public void setCiftomador(String ciftomador) {
		this.ciftomador = ciftomador;
	}



	public Long getLineaseguroid() {
		return lineaseguroid;
	}



	public void setLineaseguroid(Long lineaseguroid) {
		this.lineaseguroid = lineaseguroid;
	}



	public Colectivo getColectivoBean() {
		return colectivoBean;
	}



	public void setColectivoBean(Colectivo colectivoBean) {
		this.colectivoBean = colectivoBean;
	}



	public List<BigDecimal> getListaEnt() {
		return listaEnt;
	}



	public void setListaEnt(List<BigDecimal> listaCodEntidadesGrupo) {
		this.listaEnt = listaCodEntidadesGrupo;
	}



	public Map<String, Object> getMapa() {
		return mapa;
	}



	public void setMapa(Map<String, Object> mapa) {
		this.mapa = mapa;
	}



	public String getPerfilUsuario() {
		return perfilUsuario;
	}



	public void setPerfilUsuario(String perfilUsuario) {
		this.perfilUsuario = perfilUsuario;
	}



	public boolean isAddFiltroFechaBaja() {
		return addFiltroFechaBaja;
	}



	public void setAddFiltroFechaBaja(boolean addFiltroFechaBaja) {
		this.addFiltroFechaBaja = addFiltroFechaBaja;
	}



	public List<BigDecimal> getPlanesFiltroInicial() {
		return planesFiltroInicial;
	}



	public void setPlanesFiltroInicial(List<BigDecimal> planesFiltroInicial) {
		this.planesFiltroInicial = planesFiltroInicial;
	}
}
