
package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.CriteriaUtils;
import com.rsi.agp.core.util.DateUtil;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;



public class PolizaFiltro implements Filter { 
	
	private final Log logger = LogFactory.getLog(getClass());
	
	// Constantes
	private static final String WHERE_E_ENVIO_IBAN_AGRO_S = " where e.envio_iban_agro = 'S' ";
	private static final String AND_E_IDPOLIZA_THIS_IDPOLIZA = " and e.idpoliza = this_.IDPOLIZA) ";
	private static final String RENOV_POLIZA_IDPOLIZA = "renovPoliza.idpoliza";
	private static final String ESTADO_PAGO_AGP_ID = "estadoPagoAgp.id";
	private static final String COL_TOMADOR_ID_CODENTIDAD = "col.tomador.id.codentidad";
	private static final String ESTADO_POLIZA_IDESTADO = "estadoPoliza.idestado";
	
	Poliza               polizaBean;
	BigDecimal[]         estados;
	String               perfil;
	BigDecimal[]         listaEnt;
	Character			 tipoReferencia;
	Map<String, Object>  mapa;
	List<BigDecimal>     lstIdPolizaFechaEnvio;
	BigDecimal           tipoPago;
	List<BigDecimal>     listaOfi = new ArrayList<BigDecimal>();

	public Character getTipoReferencia() {
		return tipoReferencia;
	}


	public void setTipoReferencia(Character tipoReferencia) {
		this.tipoReferencia = tipoReferencia;
	}

	public PolizaFiltro() {
		// Constructor por defecto
	}
	
	
	public Criteria getCriteria(final Session sesion) {
		
		// CRITERIA
		Criteria criteria = sesion.createCriteria(Poliza.class,"pol");		

		criteria.createAlias("colectivo", "col");
		criteria.createAlias("col.subentidadMediadora", "subent");
		
		criteria.createAlias("asegurado", "ase");
		criteria.createAlias("linea", "lin");
		criteria.createAlias("estadoPoliza", "estadoPoliza");
		criteria.createAlias("usuario", "u");
        /* P73325 - RQ.04, RQ.05 y RQ.06  */
		criteria.createAlias("gedDocPoliza", "ged");
		
		if (FiltroUtils.noEstaVacio(polizaBean)) {
			criteria.add(Restrictions.allEq(mapa));
		}
		//filtramos por clase --- Ya vienen en el mapa
//		if (FiltroUtils.noEstaVacio(polizaBean.getClase())) {
//			criteria.add(Restrictions.eq("clase", polizaBean.getClase()));
//		}	
//		
//		if (FiltroUtils.noEstaVacio(polizaBean.getLinea().getCodplan())) {
//			criteria.add(Restrictions.eq("lin.codplan", polizaBean.getLinea().getCodplan()));
//		}
//		if (FiltroUtils.noEstaVacio(polizaBean.getLinea().getCodlinea())) {
//			criteria.add(Restrictions.eq("lin.codlinea", polizaBean.getLinea().getCodlinea()));
//		}
//		if (FiltroUtils.noEstaVacio(polizaBean.getTienesiniestros())) {
//			criteria.add(Restrictions.eq("tienesiniestros",polizaBean.getTienesiniestros()));
//		}
//		if (FiltroUtils.noEstaVacio(polizaBean.getTieneanexomp())) {
//			criteria.add(Restrictions.eq("tieneanexomp", polizaBean.getTieneanexomp()));
//		}
//		if (FiltroUtils.noEstaVacio(polizaBean.getTieneanexorc())) {
//			criteria.add(Restrictions.eq("tieneanexorc",polizaBean.getTieneanexorc()));
//		}
				
		if (polizaBean.getTipoReferencia()!=null){			
			if (!polizaBean.getTipoReferencia().equals('T')){
				criteria.add(Restrictions.eq("tipoReferencia", polizaBean.getTipoReferencia()));
			}
		}
		/*
		if (this.polizaBean.getFechaenvio()!= null){
			Date fechaMas24 = new Date();
			GregorianCalendar fechaEnvioGrMas24 = new GregorianCalendar();
			fechaEnvioGrMas24.setTime(this.polizaBean.getFechaenvio());
			fechaEnvioGrMas24.add(GregorianCalendar.HOUR,24);
			fechaMas24 = fechaEnvioGrMas24.getTime();
			//criteria.add(Restrictions.between("fechaenvio", this.fechaEnvio, fechaMas24));
			criteria.add(Restrictions.ge("fechaenvio", this.polizaBean.getFechaenvio()));
			criteria.add(Restrictions.lt("fechaenvio", fechaMas24));
		}	
		*/
		final String nombre = polizaBean.getAsegurado().getNombre();
		if (FiltroUtils.noEstaVacio(nombre)) {
			criteria.add(
					Restrictions.disjunction()
					.add(Restrictions.like("ase.fullName",nombre,MatchMode.ANYWHERE))
					.add(Restrictions.like("ase.razonsocial",nombre,MatchMode.ANYWHERE))
			);
		}
		
		if (FiltroUtils.noEstaVacio(polizaBean.getDc())) {
			criteria.add(Restrictions.eq("dc", polizaBean.getDc()));
		}	
		
		if(mapa.get(ESTADO_POLIZA_IDESTADO) == null){
			if(estados != null && estados.length > 0){
				criteria.add(Restrictions.not(Restrictions.in(ESTADO_POLIZA_IDESTADO, estados)));
			}
		}
		
		if(listaEnt.length > 0){
			if(polizaBean.getColectivo().getTomador().getId().getCodentidad() != null){
				criteria.add(Restrictions.eq(COL_TOMADOR_ID_CODENTIDAD, polizaBean.getColectivo().getTomador().getId().getCodentidad()));
			}else{
				criteria.add(Restrictions.in(COL_TOMADOR_ID_CODENTIDAD, listaEnt));
			}
		}else{			
			final BigDecimal codEntidad = polizaBean.getColectivo().getTomador().getId().getCodentidad();
			if (FiltroUtils.noEstaVacio(codEntidad)) {
				criteria.add(Restrictions.eq(COL_TOMADOR_ID_CODENTIDAD, codEntidad));
			}
		}
		
		if (polizaBean.getEstadoPagoAgp() != null && !StringUtils.nullToString(polizaBean.getEstadoPagoAgp().getId()).equals("")){
			if (polizaBean.getEstadoPagoAgp().getId().equals(Constants.POLIZA_NO_PAGADA)){
				criteria.add(
					Restrictions.disjunction()
						.add(Restrictions.isNull(ESTADO_PAGO_AGP_ID))
						.add(Restrictions.eq(ESTADO_PAGO_AGP_ID, polizaBean.getEstadoPagoAgp().getId()))
				);
			}
			else{
				criteria.add(Restrictions.eq(ESTADO_PAGO_AGP_ID, polizaBean.getEstadoPagoAgp().getId()));
			}
		}
		if (polizaBean.getColectivo().getSubentidadMediadora().getId().getCodentidad() != null) {
			criteria.add(Restrictions.eq("subent.id.codentidad", polizaBean.getColectivo().getSubentidadMediadora().getId().getCodentidad()));
		}
		if (polizaBean.getColectivo().getSubentidadMediadora().getId().getCodsubentidad() != null) {
			criteria.add(Restrictions.eq("subent.id.codsubentidad", polizaBean.getColectivo().getSubentidadMediadora().getId().getCodsubentidad()));
		}
		if (polizaBean.getUsuario().getDelegacion()!= null) {
			criteria.add(Restrictions.eq("u.delegacion",polizaBean.getUsuario().getDelegacion()));
		}
		if (FiltroUtils.noEstaVacio(polizaBean.getOficina())){
			criteria.add(Restrictions.in("oficina",CriteriaUtils.getCodigosOficina(polizaBean.getOficina())));
		}else{
			if(listaOfi.size() > 0){
				criteria.add(Restrictions.in("oficina",CriteriaUtils.getCodigosListaOficina(listaOfi)));
			}
		}
		if (!StringUtils.nullToString(tipoPago).equals("")) {
			DetachedCriteria pagoPolizaCriteria = DetachedCriteria.forClass(PagoPoliza.class, "pagoPoliza");
			 
			pagoPolizaCriteria.add(Restrictions.eq("pagoPoliza.tipoPago", tipoPago));
			pagoPolizaCriteria.createAlias("pagoPoliza.poliza", "pagoP");
			pagoPolizaCriteria.add(Restrictions.eqProperty("pagoP.idpoliza", "pol.idpoliza"));
			pagoPolizaCriteria.setProjection(Projections.property("pagoP.idpoliza"));
			criteria.add(Subqueries.exists(pagoPolizaCriteria));
		}

		
		if(polizaBean.getRenovableSn()!=null ){
			DetachedCriteria sizeCriteria = DetachedCriteria.forClass(ModuloPoliza.class, "modulosPol");
			sizeCriteria.createAlias("modulosPol.poliza", "renovPoliza");
			sizeCriteria.add(Restrictions.and(Restrictions.eq("modulosPol.renovable", new Integer(1)),Restrictions.eqProperty(RENOV_POLIZA_IDPOLIZA, "pol.idpoliza"))   );
			if(polizaBean.getRenovableSn().equals('S')){
			
			criteria.add(Subqueries.exists(sizeCriteria.setProjection(Projections.property(RENOV_POLIZA_IDPOLIZA))));	
			}
			else if(polizaBean.getRenovableSn().equals('N')){
			
			criteria.add(Subqueries.notExists(sizeCriteria.setProjection(Projections.property(RENOV_POLIZA_IDPOLIZA))));		
			}
		}
		
		if (polizaBean.getEsRyD()!=null) {	
			if(polizaBean.getEsRyD().equals('S')){
				criteria.add(Restrictions.sqlRestriction(" 1=1 AND this_.IDPOLIZA IN (select e.IDPOLIZA from  tb_explotaciones e " + 
											                 " inner join tb_grupo_raza_explotacion g on g.idexplotacion = e.id " +
											                 " where g.codtipocapital = 15 " +
											                 AND_E_IDPOLIZA_THIS_IDPOLIZA));
			}else if(polizaBean.getEsRyD().equals('N')){			
				criteria.add(Restrictions.sqlRestriction(" 1=1 AND this_.IDPOLIZA not IN (select e.IDPOLIZA from  tb_explotaciones e " + 
														 " inner join tb_grupo_raza_explotacion g on g.idexplotacion = e.id " +
														 " where g.codtipocapital = 15 " +
		                 								 AND_E_IDPOLIZA_THIS_IDPOLIZA));
			}
		}
		
		if (polizaBean.getEsFinanciada()!=null) {
			String sqlWhereFinanciada = " 1=1 AND (select ((select count(*) " +
									    " from o02agpe0.tb_polizas po " +
									    " inner join o02agpe0.TB_LINEAS L on po.LINEASEGUROID = L.LINEASEGUROID " +
									    " inner join o02agpe0.TB_POLIZAS_RENOVABLES pr on pr.referencia = po.referencia and pr.plan = l.codplan " +
									    " where pr.coste_total_tomador != pr.importe_domiciliar and po.IDPOLIZA = this_.IDPOLIZA) " +
									    " + " +
									    " (select count(*) " +
									    " from o02agpe0.tb_distribucion_costes_2015 dc " +
									    " where (dc.importe_pago_fracc is not null or " +
									    " dc.importe_pago_fracc_agr is not null) " +
									    " and dc.idpoliza = this_.IDPOLIZA)) " +
									    " from dual) ";
			
			if(polizaBean.getEsFinanciada().equals('S')){
				criteria.add(Restrictions.sqlRestriction(sqlWhereFinanciada + " > 0 "));
			}else if(polizaBean.getEsFinanciada().equals('N')){			
				criteria.add(Restrictions.sqlRestriction(sqlWhereFinanciada + " = 0 "));	
			}
		}		
		
		if(polizaBean.getTieneIBAN()!=null){
			/* Filtramos por envio IBAN, si tiene 'S' solo sacamos los registros con ese valor,
			 en caso contrario consideramos que no tiene el envio ('N' o nulo) */
			
			if(polizaBean.getTieneIBAN().equals('S')){
				criteria.add(Restrictions.sqlRestriction(" 1=1 AND this_.IDPOLIZA IN (select e.IDPOLIZA from  tb_pagos_poliza e " + 

											                 WHERE_E_ENVIO_IBAN_AGRO_S +
											                 AND_E_IDPOLIZA_THIS_IDPOLIZA));
			}else if(polizaBean.getTieneIBAN().equals('N')){			
				criteria.add(Restrictions.sqlRestriction(" 1=1 AND this_.IDPOLIZA not IN (select e.IDPOLIZA from  tb_pagos_poliza e " + 

											                 WHERE_E_ENVIO_IBAN_AGRO_S +
											                 AND_E_IDPOLIZA_THIS_IDPOLIZA));
			}
		}
		
		
		/*
		if (polizaBean.getFechavigor()!=null) {			
				Date fechaMas24 = new Date();
				GregorianCalendar fechaVigorGrMas24 = new GregorianCalendar();
				fechaVigorGrMas24.setTime(this.polizaBean.getFechavigor());
				fechaVigorGrMas24.add(GregorianCalendar.HOUR,24);
				fechaMas24 = fechaVigorGrMas24.getTime();
				criteria.add(Restrictions.ge("fechavigor", this.polizaBean.getFechavigor()));
				criteria.add(Restrictions.lt("fechavigor", fechaMas24));				
		}
		*/
		if (polizaBean.getFechaEnvioDesde()!=null) {
			criteria.add(Restrictions.ge("fechaenvio", this.polizaBean.getFechaEnvioDesde()));			
		}
		
		if (polizaBean.getFechaEnvioHasta()!=null) {
			Date fechaMas24 = new Date();
			GregorianCalendar fechaEnvioGrMas24 = new GregorianCalendar();
			fechaEnvioGrMas24.setTime(this.polizaBean.getFechaEnvioHasta());
			fechaEnvioGrMas24.add(Calendar.HOUR,24);
			fechaMas24 = fechaEnvioGrMas24.getTime();
			criteria.add(Restrictions.lt("fechaenvio", fechaMas24));
			//criteria.add(Restrictions.le("fechaenvio", this.polizaBean.getFechaEnvioHasta()));
		}
		
		if (polizaBean.getFechaVigorDesde()!=null) {
			criteria.add(Restrictions.ge("fechavigor", this.polizaBean.getFechaVigorDesde()));			
		}
		
		if (polizaBean.getFechaVigorHasta()!=null) {
			Date fechaMas24 = new Date();
			GregorianCalendar fechaVigorGrMas24 = new GregorianCalendar();
			fechaVigorGrMas24.setTime(this.polizaBean.getFechaVigorHasta());
			fechaVigorGrMas24.add(Calendar.HOUR,24);
			fechaMas24 = fechaVigorGrMas24.getTime();
			criteria.add(Restrictions.lt("fechavigor", fechaMas24));
			//criteria.add(Restrictions.le("fechavigor", this.polizaBean.getFechaVigorHasta()));
		}
		
		if (polizaBean.getFechaPagoDesde()!=null || polizaBean.getFechaPagoHasta()!=null) {
			criteria.add(Restrictions.eq(ESTADO_PAGO_AGP_ID, (Constants.POLIZA_PAGADA)));
		}
		
		if (polizaBean.getFechaPagoDesde()!=null) {
			criteria.add(Restrictions.ge("fechaPago", this.polizaBean.getFechaPagoDesde()));			
		}
		
		if (polizaBean.getFechaPagoHasta()!=null) {
			Date fechaMas24 = new Date();
			GregorianCalendar fechaPagoGrMas24 = new GregorianCalendar();
			fechaPagoGrMas24.setTime(this.polizaBean.getFechaPagoHasta());
			fechaPagoGrMas24.add(Calendar.HOUR,24);
			fechaMas24 = fechaPagoGrMas24.getTime();
			criteria.add(Restrictions.lt("fechaPago", fechaMas24));
		}
		
		/* Pet. ESC-12906 ** MODIF TAM (19.01.2021) ** Inicio */
		criteria.add(Restrictions.ne("ase.isBloqueado", Integer.valueOf(1)));
		/* Pet. ESC-12906 ** MODIF TAM (19.01.2021) ** Inicio */
		
		/* P73325 - RQ.04, RQ.05 y RQ.06  */
		if (polizaBean.getGedDocPoliza() !=null) {
			if ((polizaBean.getGedDocPoliza().getCanalFirma() != null) && (polizaBean.getGedDocPoliza().getCanalFirma().getIdCanal() != null)) {	
				criteria.add(Restrictions.eq("ged.canalFirma.idCanal", this.polizaBean.getGedDocPoliza().getCanalFirma().getIdCanal()));		
			}
			
			if ((polizaBean.getGedDocPoliza()!=null) && !StringUtils.nullToString(polizaBean.getGedDocPoliza().getDocFirmada()).equals("")){		
				criteria.add(Restrictions.eq("ged.docFirmada", this.polizaBean.getGedDocPoliza().getDocFirmada()));			
			}
		}
		
		return criteria;
	}


	public Poliza getPolizaBean() {
		return polizaBean;
	}


	public void setPolizaBean(Poliza polizaBean) {
		this.polizaBean = polizaBean;
	}


	public String getPerfil() {
		return perfil;
	}


	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}


	public Map<String, Object> getMapaPoliza() {
		return mapa;
	}


	public void setMapaPoliza(Map<String, Object> mapaPoliza) {
		this.mapa = mapaPoliza;
	}


	public BigDecimal[] getEstados() {
		return estados;
	}

	public void setEstados(BigDecimal[] estados) {
		this.estados = estados;
	}


	public BigDecimal[] getListaEnt() {
		return listaEnt;
	}


	public void setListaEnt(BigDecimal[] listaEnt) {
		this.listaEnt = listaEnt;
	}


	public List<BigDecimal> getListaOfi() {
		return listaOfi;
	}


	public void setListaOfi(List<BigDecimal> listaOfi) {
		this.listaOfi = listaOfi;
	}


	public List<BigDecimal> getLstIdPolizaFechaEnvio() {
		return lstIdPolizaFechaEnvio;
	}


	public void setLstIdPolizaFechaEnvio(List<BigDecimal> lstIdPolizaFechaEnvio) {
		this.lstIdPolizaFechaEnvio = lstIdPolizaFechaEnvio;
	}

	public String getSqlWhere(){
		String sqlWhere = " WHERE";
		
		for (Map.Entry<String, Object> entry : mapa.entrySet()){
			if (entry.getKey().indexOf(".") < 0 && (!entry.getKey().equals("usuario.codusuario")) ){
				sqlWhere += " AND P." + entry.getKey() + " = '" + entry.getValue() + "'";
			}
			else if (entry.getKey().equals("usuario.codusuario")){
				sqlWhere += " AND P.CODUSUARIO = '" + entry.getValue() + "'";
			}
			else if (entry.getKey().equals("col.tomador.id.ciftomador")){
				sqlWhere += " AND C.CIFTOMADOR = '" + entry.getValue() + "'";
			}
			else if (entry.getKey().equals(COL_TOMADOR_ID_CODENTIDAD)){
				sqlWhere += " AND C.CODENTIDAD = " + entry.getValue() + "";
			}
			else if (entry.getKey().equals("subent.id.codentidad")){
				sqlWhere += " AND C.ENTMEDIADORA = '" + entry.getValue() + "'";
			}
			else if (entry.getKey().equals("subent.id.codsubentidad")){
				sqlWhere += " AND C.SUBENTMEDIADORA = '" + entry.getValue() + "'";
			}
			else if (entry.getKey().equals("lin.codplan")){
				sqlWhere += " AND L.CODPLAN = " + entry.getValue() + "";
			}
			else if (entry.getKey().equals("lin.codlinea")){
				sqlWhere += " AND L.CODLINEA = " + entry.getValue() + "";
			}
			else if (entry.getKey().equals("col.linea.lineaseguroid")){
				sqlWhere += " AND C.LINEASEGUROID = " + entry.getValue() + "";
			}
			else if (entry.getKey().equals("col.id")){
				sqlWhere += " AND C.ID = '" + entry.getValue() + "'";
			}
			else if (entry.getKey().equals("col.idcolectivo")){
				sqlWhere += " AND C.IDCOLECTIVO = '" + entry.getValue() + "'";
			}
			else if (entry.getKey().equals("col.dc")){
				sqlWhere += " AND C.DC = '" + entry.getValue() + "'";
			}
			else if (entry.getKey().equals("ase.id")){
				sqlWhere += " AND A.ID = '" + entry.getValue() + "'";
			}
			else if (entry.getKey().equals("ase.nifcif")){
				sqlWhere += " AND A.NIFCIF = '" + entry.getValue() + "'";
			}
			else if (entry.getKey().equals("ase.entidad.codentidad")){
				sqlWhere += " AND A.CODENTIDAD = '" + entry.getValue() + "'";
			}
			else if (entry.getKey().equals("ase.discriminante")){
				sqlWhere += " AND A.DISCRIMINANTE = '" + entry.getValue() + "'";
			}
			else if (entry.getKey().equals(ESTADO_POLIZA_IDESTADO)){
				sqlWhere += " AND P.IDESTADO = " + entry.getValue() + "";
			}
			else if (entry.getKey().equals("estadoPoliza.descEstado")){
				sqlWhere += " AND E.DESC_ESTADO = '" + entry.getValue() + "'";
			}
			else if (entry.getKey().equals("u.delegacion")){
				sqlWhere += " AND U.DELEGACION = '" + entry.getValue() + "'";
			}
			else{
				sqlWhere += " AND P." + entry.getKey().substring(entry.getKey().lastIndexOf(".") + 1) + " = '" + entry.getValue() + "'";				
			}			
		}
		
		/* P73325 - RQ.04, RQ.05 y RQ.06  Inicio */
		if (polizaBean.getGedDocPoliza() != null && !StringUtils.nullToString(polizaBean.getGedDocPoliza().getCanalFirma().getIdCanal()).equals("")){

			sqlWhere += " AND D.COD_CANAL_FIRMA = '" + polizaBean.getGedDocPoliza().getCanalFirma().getIdCanal() + "'";

		}
		
		if (polizaBean.getGedDocPoliza() != null && !StringUtils.nullToString(polizaBean.getGedDocPoliza().getDocFirmada()).equals("")){

			sqlWhere += " AND D.IND_DOC_FIRMADA = '" + polizaBean.getGedDocPoliza().getDocFirmada() + "'";

		}
		/* P73325 - RQ.04, RQ.05 y RQ.06  Fin */
		
		if (polizaBean.getTipoReferencia()!=null){
			if (!polizaBean.getTipoReferencia().equals('T')){
				sqlWhere += " AND P.TIPOREFERENCIA = " + polizaBean.getTipoReferencia();
			}
		}
		
		final String nombre = polizaBean.getAsegurado().getNombre();
		if (FiltroUtils.noEstaVacio(nombre)) {
			sqlWhere += " AND ( upper(A.nombre ||' '|| A.Apellido1 ||' '||A.Apellido2) like upper('%"+nombre+"%')";
			sqlWhere += " OR UPPER(A.RAZONSOCIAL) LIKE UPPER('%"+nombre+"%'))";
		}
		
		if (FiltroUtils.noEstaVacio(polizaBean.getDc())) {
			sqlWhere += " AND P.DC = " + polizaBean.getDc();
		}
		
		if(mapa.get(ESTADO_POLIZA_IDESTADO) == null){
			if(estados != null && estados.length > 0){
				sqlWhere += " AND P.IDESTADO NOT IN (";
				for (int i = 0; i < estados.length; i++)
				{
					sqlWhere += estados[i];
					if (i < estados.length-1){
						sqlWhere += ",";
					}
				}
				sqlWhere += ")";
			}
		}	
		
		if(listaEnt.length > 0){
			if(polizaBean.getColectivo().getTomador().getId().getCodentidad() != null){
				sqlWhere += " AND C.CODENTIDAD = " + polizaBean.getColectivo().getTomador().getId().getCodentidad();
			}else{
				sqlWhere += " AND C.CODENTIDAD IN (";
				for (int i = 0; i < listaEnt.length; i++){
					sqlWhere += listaEnt[i];
					if (i < listaEnt.length-1)
						sqlWhere += ",";
				}
				sqlWhere += ")";
			}
		}else{
			final BigDecimal codEntidad = polizaBean.getColectivo().getTomador().getId().getCodentidad();
			if (FiltroUtils.noEstaVacio(codEntidad)) {
				sqlWhere += " AND C.CODENTIDAD = " + codEntidad;
			}
		}
		
		if (polizaBean.getEstadoPagoAgp() != null && !StringUtils.nullToString(polizaBean.getEstadoPagoAgp().getId()).equals("")){
			if (polizaBean.getEstadoPagoAgp().getId().equals(Constants.POLIZA_NO_PAGADA)){
				sqlWhere += " AND (P.PAGADA IS NULL OR P.PAGADA = " + polizaBean.getEstadoPagoAgp().getId() + ")";
			}
			else{
				sqlWhere += " AND P.PAGADA = " + polizaBean.getEstadoPagoAgp().getId();
			}
		}
		if (FiltroUtils.noEstaVacio(polizaBean.getOficina())){
			sqlWhere += " AND P.OFICINA in " + StringUtils.toValoresSeparadosXComas(
					CriteriaUtils.getCodigosOficina(polizaBean.getOficina()), false, true);
		}else{
			if(listaOfi.size()> 0){
				sqlWhere += " AND P.OFICINA in " + StringUtils.toValoresSeparadosXComas(
						 CriteriaUtils.getCodigosListaOficina(listaOfi), false, true);;
			}
		
		}
		if (!StringUtils.nullToString(tipoPago).equals("")) {
			
				sqlWhere += " and exists (select PO.IDPOLIZA from TB_PAGOS_POLIZA pagoPoliza " + 
						"           inner join TB_POLIZAS PO on pagoPoliza.IDPOLIZA = PO.IDPOLIZA " + 
						"           where pagoPoliza.TIPO_PAGO = " + tipoPago + 
						"             and pagoPoliza.IDPOLIZA =P.IDPOLIZA) ";
			
		}
		
		if (polizaBean.getRenovableSn()!=null) {
			if(polizaBean.getRenovableSn().equals('S')){
				sqlWhere += " AND exists(select 1 from tb_modulos_poliza pm where pm.renovable=1 and pm.idpoliza = P.idpoliza) ";		
			}else if(polizaBean.getRenovableSn().equals('N')){
				sqlWhere += " AND not exists(select 1 from tb_modulos_poliza pm where pm.renovable=1 and pm.idpoliza = P.idpoliza) ";
			}
		}
		
		if(polizaBean.getEsRyD()!=null){
			if(polizaBean.getEsRyD().equals('S')){
				sqlWhere += " AND P.IDPOLIZA IN (select e.IDPOLIZA from  o02agpe0.tb_explotaciones e inner join o02agpe0.tb_grupo_raza_explotacion g " +
    					  	" on g.idexplotacion = e.id"+
    					  	" where g.codtipocapital = 15 and e.idpoliza =p.idpoliza)  ";
        	}else{
        		sqlWhere += " AND P.IDPOLIZA not IN  (select e.IDPOLIZA from  o02agpe0.tb_explotaciones e inner join o02agpe0.tb_grupo_raza_explotacion g " +
        					" on g.idexplotacion = e.id"+
        					" where g.codtipocapital = 15 and e.idpoliza =p.idpoliza)  ";
			}
		}
		
		if(polizaBean.getEsFinanciada()!=null){
			
			String sqlWhereFinanciada = " AND (select ((select count(*) " +
				    " from o02agpe0.tb_polizas po " +
				    " inner join o02agpe0.TB_LINEAS L on po.LINEASEGUROID = L.LINEASEGUROID " +
				    " inner join o02agpe0.TB_POLIZAS_RENOVABLES pr on pr.referencia = po.referencia and pr.plan = l.codplan " +
				    " where pr.coste_total_tomador != pr.importe_domiciliar and po.IDPOLIZA = P.IDPOLIZA) " +
				    " + " +
				    " (select count(*) " +
				    " from o02agpe0.tb_distribucion_costes_2015 dc " +
				    " where (dc.importe_pago_fracc is not null or " +
				    " dc.importe_pago_fracc_agr is not null) " +
				    " and dc.idpoliza = P.IDPOLIZA)) " +
				    " from dual) ";
			
			if (polizaBean.getEsFinanciada().equals('S')) {
				sqlWhere += sqlWhereFinanciada + " > 0 ";
			} else {
				sqlWhere += sqlWhereFinanciada + " = 0 ";
			}
			
		}
		
		if(polizaBean.getTieneIBAN()!=null){
			
			/* Filtramos por envio IBAN, si tiene 'S' solo sacamos los registros con ese valor,
			 en caso contrario consideramos que no tiene el envio ('N' o nulo) */
			

			if(polizaBean.getTieneIBAN().equals('S')){
				sqlWhere += " AND p.IDPOLIZA IN (select e.IDPOLIZA from  tb_pagos_poliza e " + 
							WHERE_E_ENVIO_IBAN_AGRO_S +
							" and e.idpoliza = p.IDPOLIZA)  ";
        	}else{
        		sqlWhere += " AND p.IDPOLIZA not IN (select e.IDPOLIZA from  tb_pagos_poliza e " + 
							WHERE_E_ENVIO_IBAN_AGRO_S +
							" and e.idpoliza = p.IDPOLIZA)  ";
			}
			
		}
		
//		if (FiltroUtils.noEstaVacio(polizaBean.getFechavigor())) {
//			sqlWhere += " AND TO_CHAR(P.FECHA_VIGOR,'DD/MM/YYYY') = '" + 
//				DateUtil.date2String(polizaBean.getFechavigor(), DateUtil.FORMAT_DATE_SQL_DEFAULT) + "'";
//		}
		
		if (FiltroUtils.noEstaVacio(polizaBean.getFechaEnvioDesde())) {
			sqlWhere += " AND P.FECHAENVIO >= TO_DATE('" +DateUtil.date2String(polizaBean.getFechaEnvioDesde(), DateUtil.FORMAT_DATE_SQL_DEFAULT)+" 00:00:00 ','DD/MM/YYYY HH24:mi:ss')";
		}
		
		if (FiltroUtils.noEstaVacio(polizaBean.getFechaEnvioHasta())) {
			sqlWhere += " AND P.FECHAENVIO <= TO_DATE('" +DateUtil.date2String(polizaBean.getFechaEnvioHasta(), DateUtil.FORMAT_DATE_SQL_DEFAULT)+" 23:59:59 ','DD/MM/YYYY HH24:mi:ss')";
		}
		
		if (FiltroUtils.noEstaVacio(polizaBean.getFechaVigorDesde())) {
			sqlWhere += " AND P.FECHA_VIGOR >= TO_DATE('" +DateUtil.date2String(polizaBean.getFechaVigorDesde(), DateUtil.FORMAT_DATE_SQL_DEFAULT)+" 00:00:00 ','DD/MM/YYYY HH24:mi:ss')";
		}
		
		if (FiltroUtils.noEstaVacio(polizaBean.getFechaVigorHasta())) {
			sqlWhere += " AND P.FECHA_VIGOR <= TO_DATE('" +DateUtil.date2String(polizaBean.getFechaVigorHasta(), DateUtil.FORMAT_DATE_SQL_DEFAULT)+" 23:59:59 ','DD/MM/YYYY HH24:mi:ss')";
		}
		
		if (FiltroUtils.noEstaVacio(polizaBean.getFechaPagoDesde()) || FiltroUtils.noEstaVacio(polizaBean.getFechaPagoHasta())) {		
			sqlWhere += " AND P.PAGADA = " + Constants.POLIZA_PAGADA;
		}
		
		if (FiltroUtils.noEstaVacio(polizaBean.getFechaPagoDesde())) {
			sqlWhere += " AND P.FECHA_PAGO >= TO_DATE('" +DateUtil.date2String(polizaBean.getFechaPagoDesde(), DateUtil.FORMAT_DATE_SQL_DEFAULT)+" 00:00:00 ','DD/MM/YYYY HH24:mi:ss')";
		}
		
		if (FiltroUtils.noEstaVacio(polizaBean.getFechaPagoHasta())) {
			sqlWhere += " AND P.FECHA_PAGO <= TO_DATE('" +DateUtil.date2String(polizaBean.getFechaPagoHasta(), DateUtil.FORMAT_DATE_SQL_DEFAULT)+" 23:59:59 ','DD/MM/YYYY HH24:mi:ss')";
		}
		
		
		/* Pet. 62719 ** MODIF TAM (21.01.2021) ** Inicio */
		/* Incluimos validaci�n para no recuperar aquellos Asegurados que est�n bloqueados */
        sqlWhere += " AND (A.id NOT IN (SELECT BLOQA.ID_ASEGURADO FROM o02agpe0.TB_BLOQUEOS_ASEGURADOS BLOQA WHERE BLOQA.IDESTADO_ASEG = 'B'))";
        /* Pet. 62719 ** MODIF TAM (21.01.2021) ** Fin */
		
		String sql=sqlWhere.replaceFirst("WHERE AND", "WHERE ");
		
		return sql;
	}


	public BigDecimal getTipoPago() {
		return tipoPago;
	}


	public void setTipoPago(BigDecimal tipoPago) {
		this.tipoPago = tipoPago;
	}
	
	
	
	
	
	
	
	
	
	
}
