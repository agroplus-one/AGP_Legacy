package com.rsi.agp.dao.tables.comisiones;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.rsi.agp.core.util.ConstantsInf;

public class InformeComisiones2015 {
	
	
	// CONSTANTES PARA LOS NOMBRES DE LOS CAMPOS DE LA TABLA "TB_COMS_INFORMES_COMS_2015"
	private static final String CAMPO_FASE = "FASE";
	private static final String CAMPO_ENTMED = "CODENTMED";
	private static final String CAMPO_SUBENT = "CODSUBENTMED";
	private static final String CAMPO_ENTIDAD = "CODENTIDAD";
	private static final String CAMPO_OFICINA = "CODOFICINA";
	private static final String CAMPO_PLAN = "CODPLAN";
	private static final String CAMPO_LINEA = "CODLINEA";
	private static final String CAMPO_REF_COL = "COLECTIVO";
	private static final String CAMPO_FEC_ACEPT = "FECHA_ACEPTACION";
	private static final String CAMPO_NOM_TOM = "TOMADOR";
	private static final String CAMPO_REF_POL = "REF_POLIZA";
	private static final String CAMPO_NIF_ASEG = "NIF_ASEGURADO";
	private static final String CAMPO_NOM_ASEG = "NOM_ASEG";
	private static final String CAMPO_AP1_ASEG = "AP1_ASEG";
	private static final String CAMPO_AP2_ASEG = "AP2_ASEG";
	private static final String CAMPO_RS_ASEG = "RAZONSOCIAL_ASEG";

	private static final String CAMPO_N_PRIMA_COM_NETA = "PRIMA_COMERCIAL_NETA_DN1";
	private static final String CAMPO_N_GASTOS_ADMIN = "GASTOS_ADM_DN1";
	private static final String CAMPO_N_GASTOS_ADQ = "GASTOS_ADQ_DN1";
	private static final String CAMPO_N_COMS_ENTD = "COMISION_ENTIDAD_DN1";
	private static final String CAMPO_N_COMS_SUBENT = "COMISION_ES_MEDIADORA_DN1";
	private static final String CAMPO_N_TOTAL_GASTOS = "TOTAL_GASTOS_DN1";

	private static final String CAMPO_R_PRIMA_COM_NETA = "PRIMA_COMERCIAL_NETA_DR1";
	private static final String CAMPO_R_GASTOS_ADMIN = "GASTOS_ADM_DR1";
	private static final String CAMPO_R_GASTOS_ADQ = "GASTOS_ADQ_DR1";
	private static final String CAMPO_R_COMS_ENTD = "COMISION_ENTIDAD_DR1";
	private static final String CAMPO_R_COMS_SUBENT = "COMISION_ES_MEDIADORA_DR1";
	private static final String CAMPO_R_TOTAL_GASTOS = "TOTAL_GASTOS_DR1";
	
	private static final String CAMPO_T_PRIMA_COM_NETA = "PRIMA_COMERCIAL_NETA_DT1";
	private static final String CAMPO_T_GASTOS_ADMIN = "GASTOS_ADM_DT1";
	private static final String CAMPO_T_GASTOS_ADQ = "GASTOS_ADQ_DT1";
	private static final String CAMPO_T_COMS_ENTD = "COMISION_ENTIDAD_DT1";
	private static final String CAMPO_T_COMS_SUBENT = "COMISION_ES_MEDIADORA_DT1";
	private static final String CAMPO_T_TOTAL_GASTOS = "TOTAL_GASTOS_DT1";

	private static final String CAMPO_T_GASTOS_ADMIN_A = "GASTOS_ADM_ABON_DT1";
	private static final String CAMPO_T_GASTOS_ADQ_A = "GASTOS_ADQ_ABON_DT1";
	private static final String CAMPO_T_COMS_ENTD_A = "COMISION_ENT_ABON_DT1";
	private static final String CAMPO_T_COMS_SUBENT_A = "COMISION_ESMED_ABON_DT1";
	
	private static final String CAMPO_T_GASTOS_ADMIN_P = "GASTOS_ADM_PDTE_DT1";
	private static final String CAMPO_T_GASTOS_ADQ_P = "GASTOS_ADQ_PDTE_DT1";
	private static final String CAMPO_T_COMS_ENTD_P = "COMISION_ENT_PDTE_DT1";
	private static final String CAMPO_T_COMS_SUBENT_P = "COMISION_ESMED_PDTE_DT1";

	private static final String CAMPO_TOTAL_LIQ = "TOTAL_LIQUIDACION";
	private static final String CAMPO_TOTAL_LIQ_RGA = "TOTAL_LIQUIDACION_RGA";
	
	private static final String CAMPO_COD_TRAM = "COD_TRAM";
	private static final String CAMPO_TIPO_TRAM = "TIPO_TRAM";
	private static final String CAMPO_IMP_APLI_TRAM = "IMPORTE_APLICADO_TRAM";
	private static final String CAMPO_IMP_SIN_REDUCC_TRAM = "IMPORTE_SIN_REDUCC_TRAM";
	private static final String CAMPO_COD_CALIDAD = "COD_CALIDAD";
	private static final String CAMPO_TIPO_CALIDAD = "TIPO_CALIDAD";
	private static final String CAMPO_IMP_APLI_CALIDAD = "IMPORTE_APLICADO_CALIDAD";
	private static final String CAMPO_IMP_SIN_REDUCC_CALIDAD = "IMPORTE_SIN_REDUCC_CALIDAD";
	private static final String CAMPO_TOTAL_TRAM_CALIDAD = "TOTAL_TRAM_CALIDAD";
	//********************************************************************//
	
	// OTRAS PROPIEDADES
	private Set<String> camposComunesMostrar = new HashSet<String>();
	private Map<String, String[]> mapaFiltro = new HashMap<String, String[]>();
	private String datosDe;
	private String campoOrdenar;
	private String sentido;
	private String formato = new Integer(ConstantsInf.COD_FORMATO_PDF).toString();
	
	//********************************************************************//
	
	//PROPIEDADES PARA LOS INPUTS DE FILTRO
	private String fase;
	private String refColectivo;
	private String linea;
	private String plan;
	private String entidad;
	private String oficina;
	private String entidadMed;        
	private String subent;           
	private String refPoliza;              
	private String nifAseg;
	private String fechaAcep;
	
	
	//PROPIEDADES PARA LOS SELECT DE CONDICIONES
	private String condiFase;
	private String condiRefColectivo;
	private String condiLinea;
	private String condiPlan;    
	private String condiEntidad;
	private String condiOficina;
	private String condiEntidadMed;        
	private String condiSubent;           
	private String condiRefPoliza;  
	private String condiNifAseg;
	private String condiFechaAcep;             

	
	//PROPIEDADES PARA LOS CHECKS
	
	// Datos generales
	private boolean checkFase;
	private boolean checkEntidadMed;
	private boolean checkSubent;
	private boolean checkEntidad;
	private boolean checkOficina; 
	private boolean checkPlan;                 
	private boolean checkLinea;
	private boolean checkRefColectivo;
	private boolean checkFechaAcep;  
	private boolean checkTomador;  
	private boolean checkRefPoliza; 
	private boolean checkNifAseg; 
	private boolean checkNombreAseg;
	private boolean checkApe1Aseg; 
	private boolean checkApe2Aseg; 
	private boolean checkRazonSocialAseg; 
	
	//Nuevo
	private boolean checkNuevoPrimaComNeta;
	private boolean checkNuevoGastosAdmin;
	private boolean checkNuevoGastosAdq;
	private boolean checkNuevoComisionEnt;
	private boolean checkNuevoComisionSubent;
	private boolean checkNuevoTotalGastos;
	
	//Regularizacion
	private boolean checkReguPrimaComNeta;
	private boolean checkReguGastosAdmin;
	private boolean checkReguGastosAdq;
	private boolean checkReguComisionEnt;
	private boolean checkReguComisionSubent;
	private boolean checkReguTotalGastos;
	
	//Total (N + R)
	private boolean checkTotPrimaComNeta;
	private boolean checkTotGastosAdmin;
	private boolean checkTotGastosAdq;
	private boolean checkTotComisionEnt;
	private boolean checkTotComisionSubent;
	private boolean checkTotTotalGastos;
	private boolean checkTotGastosAdminAbon;
	private boolean checkTotGastosAdqAbon;
	private boolean checkTotComisionEntAbon;
	private boolean checkTotComisionSubentAbon;
	private boolean checkTotGastosAdminPdte;
	private boolean checkTotGastosAdqPdte;
	private boolean checkTotComisionEntPdte;
	private boolean checkTotComisionSubentPdte;
	private boolean checkTotTotalLiquidacion;
	private boolean checkTotTotalLiquidacionRga;
	
	//Reglamento
	private boolean checkReglaCodTram;
	private boolean checkReglaTipoTram;
	private boolean checkReglaImpApliTram;
	private boolean checkReglaImpSinReduccTram;
	private boolean checkReglaCodCalidad;
	private boolean checkReglaTipoCalidad;
	private boolean checkReglaImpApliCal;
	private boolean checkReglaImpSinReduccCal;
	private boolean checkReglaTotalTramCal;

	
	// GETTERS Y SETTERS PARA INPUTS DEL FILTRO
	public String getFase() {
		return fase;
	}
	public void setFase(String fase) {
		this.fase = fase;
		if(!("").equals(fase)){
			rellenaMapaFiltro(CAMPO_FASE, 1, fase);
		}
	}
	public String getRefColectivo() {
		return refColectivo;
	}
	public void setRefColectivo(String refColectivo) {
		this.refColectivo = refColectivo;
		if(!("").equals(refColectivo)){
			rellenaMapaFiltro(CAMPO_REF_COL, 1, refColectivo);
		}
	}
	public String getEntidad() {
		return entidad;
	}
	public void setEntidad(String entidad) {
		this.entidad = entidad;
		if(!("").equals(entidad)){
			rellenaMapaFiltro(CAMPO_ENTIDAD, 1, entidad);
		}
	}
	public String getOficina() {
		return oficina;
	}
	public void setOficina(String oficina) {
		this.oficina = oficina;
		if(!("").equals(oficina)){
			rellenaMapaFiltro(CAMPO_OFICINA, 1, oficina);
		}
	}
	public String getEntidadMed() {
		return entidadMed;
	}
	public void setEntidadMed(String entidadMed) {
		this.entidadMed = entidadMed;
		if(!("").equals(entidadMed)){
			rellenaMapaFiltro(CAMPO_ENTMED, 1, entidadMed);
		}
	}
	public String getSubent() {
		return subent;
	}
	public void setSubent(String subent) {
		this.subent = subent;
		if(!("").equals(subent)){
			rellenaMapaFiltro(CAMPO_SUBENT, 1, subent);
		}
	}
	public String getPlan() {
		return plan;
	}
	public void setPlan(String plan) {
		this.plan = plan;
		if(!("").equals(plan)){
			rellenaMapaFiltro(CAMPO_PLAN, 1, plan);
		}
	}
	public String getLinea() {
		return linea;
	}
	public void setLinea(String linea) {
		this.linea = linea;
		if(!("").equals(linea)){
			rellenaMapaFiltro(CAMPO_LINEA, 1, linea);
		}
	}

	public String getFechaAcep() {
		return fechaAcep;
	}
	public void setFechaAcep(String fechaAcep) {
		this.fechaAcep = fechaAcep;
		if(!("").equals(fechaAcep)){
			rellenaMapaFiltro(CAMPO_FEC_ACEPT, 1, fechaAcep);
		}
	}
	public String getRefPoliza() {
		return refPoliza;
	}
	public void setRefPoliza(String refPoliza) {
		this.refPoliza = refPoliza;
		if(!("").equals(refPoliza)){
			rellenaMapaFiltro(CAMPO_REF_POL, 1, refPoliza);
		}
	}
	public String getNifAseg() {
		return nifAseg;
	}
	public void setNifAseg(String nifAseg) {
		this.nifAseg = nifAseg;
		if(!("").equals(nifAseg)){
			rellenaMapaFiltro(CAMPO_NIF_ASEG, 1, nifAseg);
		}
	}
	
	
	// GETTERS Y SETTERS PARA LOS SELECT DE CONDICIONES
	public String getCondiFase() {
		return condiFase;
	}
	public void setCondiFase(String condiFase) {
		this.condiFase = condiFase;
		if(!("").equals(condiFase)){
			rellenaMapaFiltro(CAMPO_FASE, 0, condiFase);
		}
	}
	public String getCondiEntidad() {
		return condiEntidad;
	}
	public void setCondiEntidad(String condiEntidad) {
		this.condiEntidad = condiEntidad;
		if(!("").equals(condiEntidad)){
			rellenaMapaFiltro(CAMPO_ENTIDAD, 0, condiEntidad);
		}
	}
	public String getCondiOficina() {
		return condiOficina;
	}
	public void setCondiOficina(String condiOficina) {
		this.condiOficina = condiOficina;
		if(!("").equals(condiOficina)){
			rellenaMapaFiltro(CAMPO_OFICINA, 0, condiOficina);
		}
	}
	public String getCondiEntidadMed() {
		return condiEntidadMed;
	}
	public void setCondiEntidadMed(String condiEntidadMed) {
		this.condiEntidadMed = condiEntidadMed;
		if(!("").equals(condiEntidadMed)){
			rellenaMapaFiltro(CAMPO_ENTMED, 0, condiEntidadMed);
		}
	}
	public String getCondiSubent() {
		return condiSubent;
	}
	public void setCondiSubent(String condiSubent) {
		this.condiSubent = condiSubent;
		if(!("").equals(condiSubent)){
			rellenaMapaFiltro(CAMPO_SUBENT, 0, condiSubent);
		}
	}
	public String getCondiPlan() {
		return condiPlan;
	}
	public void setCondiPlan(String condiPlan) {
		this.condiPlan = condiPlan;
		if(!("").equals(condiPlan)){
			rellenaMapaFiltro(CAMPO_PLAN, 0, condiPlan);
		}
	}
	public String getCondiLinea() {
		return condiLinea;
	}
	public void setCondiLinea(String condiLinea) {
		this.condiLinea = condiLinea;
		if(!("").equals(condiLinea)){
			rellenaMapaFiltro(CAMPO_LINEA,0, condiLinea);
		}
	}
	public String getCondiRefColectivo() {
		return condiRefColectivo;
	}
	public void setCondiRefColectivo(String condiRefColectivo) {
		this.condiRefColectivo = condiRefColectivo;
		if(!("").equals(condiRefColectivo)){
			rellenaMapaFiltro(CAMPO_REF_COL, 0, condiRefColectivo);
		}
	}
	public String getCondiFechaAcep() {
		return condiFechaAcep;
	}
	public void setCondiFechaAcep(String condiFechaAcep) {
		this.condiFechaAcep = condiFechaAcep;
		if(!("").equals(condiFechaAcep)){
			rellenaMapaFiltro(CAMPO_FEC_ACEPT, 0, condiFechaAcep);
		}
	}
	public String getCondiRefPoliza() {
		return condiRefPoliza;
	}
	public void setCondiRefPoliza(String condiRefPoliza) {
		this.condiRefPoliza = condiRefPoliza;
		if(!("").equals(condiRefPoliza)){
			rellenaMapaFiltro(CAMPO_REF_POL, 0, condiRefPoliza);
		}
	}
	public String getCondiNifAseg() {
		return condiNifAseg;
	}
	public void setCondiNifAseg(String condiNifAseg) {
		this.condiNifAseg = condiNifAseg;
		if(!("").equals(condiNifAseg)){
			rellenaMapaFiltro(CAMPO_NIF_ASEG, 0, condiNifAseg);
		}
	}
	
	
	// GETTERS Y SETTERS PARA LOS CHECKS
	public boolean isCheckFase() {
		return checkFase;
	}
	public void setCheckFase(boolean checkFase) {
		this.checkFase = checkFase;
		if(checkFase){
			camposComunesMostrar.add(CAMPO_FASE);
		}
	}
	public boolean isCheckEntidadMed() {
		return checkEntidadMed;
	}
	public void setCheckEntidadMed(boolean checkEntidadMed) {
		this.checkEntidadMed = checkEntidadMed;
		if(checkEntidadMed){
			camposComunesMostrar.add(CAMPO_ENTMED);
		}
	}
	public boolean isCheckSubent() {
		return checkSubent;
	}
	public void setCheckSubent(boolean checkSubent) {
		this.checkSubent = checkSubent;
		if(checkSubent){
			camposComunesMostrar.add(CAMPO_SUBENT);
		}
	}
	public boolean isCheckEntidad() {
		return checkEntidad;
	}
	public void setCheckEntidad(boolean checkEntidad) {
		this.checkEntidad = checkEntidad;
		if(checkEntidad){
			camposComunesMostrar.add(CAMPO_ENTIDAD);
		}
	}
	public boolean isCheckOficina() {
		return checkOficina;
	}
	public void setCheckOficina(boolean checkOficina) {
		this.checkOficina = checkOficina;
		if(checkOficina){
			camposComunesMostrar.add(CAMPO_OFICINA);
		}
	}
	public boolean isCheckPlan() {
		return checkPlan;
	}
	public void setCheckPlan(boolean checkPlan) {
		this.checkPlan = checkPlan;
		if(checkPlan){
			camposComunesMostrar.add(CAMPO_PLAN);
		}
	}
	public boolean isCheckLinea() {
		return checkLinea;
	}
	public void setCheckLinea(boolean checkLinea) {
		this.checkLinea = checkLinea;
		if(checkLinea){
			camposComunesMostrar.add(CAMPO_LINEA);
		}
	}
	public boolean isCheckRefColectivo() {
		return checkRefColectivo;
	}
	public void setCheckRefColectivo(boolean checkRefColectivo) {
		this.checkRefColectivo = checkRefColectivo;
		if(checkRefColectivo){
			camposComunesMostrar.add(CAMPO_REF_COL);
		}
	}
	public boolean isCheckFechaAcep() {
		return checkFechaAcep;
	}
	public void setCheckFechaAcep(boolean checkFechaAcep) {
		this.checkFechaAcep = checkFechaAcep;
		if(checkFechaAcep){
			camposComunesMostrar.add(CAMPO_FEC_ACEPT);
		}
	}
	public boolean isCheckTomador() {
		return checkTomador;
	}
	public void setCheckTomador(boolean checkTomador) {
		this.checkTomador = checkTomador;
		if(checkTomador){
			camposComunesMostrar.add(CAMPO_NOM_TOM);
		}
	}
	public boolean isCheckRefPoliza() {
		return checkRefPoliza;
	}
	public void setCheckRefPoliza(boolean checkRefPoliza) {
		this.checkRefPoliza = checkRefPoliza;
		if(checkRefPoliza){
			camposComunesMostrar.add(CAMPO_REF_POL);
		}
	}
	public boolean isCheckNifAseg() {
		return checkNifAseg;
	}
	public void setCheckNifAseg(boolean checkNifAseg) {
		this.checkNifAseg = checkNifAseg;
		if(checkNifAseg){
			camposComunesMostrar.add(CAMPO_NIF_ASEG);
		}
	}
	public boolean isCheckNombreAseg() {
		return checkNombreAseg;
	}
	public void setCheckNombreAseg(boolean checkNombreAseg) {
		this.checkNombreAseg = checkNombreAseg;
		if(checkNombreAseg){
			camposComunesMostrar.add(CAMPO_NOM_ASEG);
		}
	}
	public boolean isCheckApe1Aseg() {
		return checkApe1Aseg;
	}
	public void setCheckApe1Aseg(boolean checkApe1Aseg) {
		this.checkApe1Aseg = checkApe1Aseg;
		if(checkApe1Aseg){
			camposComunesMostrar.add(CAMPO_AP1_ASEG);
		}
	}
	public boolean isCheckApe2Aseg() {
		return checkApe2Aseg;
	}
	public void setCheckApe2Aseg(boolean checkApe2Aseg) {
		this.checkApe2Aseg = checkApe2Aseg;
		if(checkApe2Aseg){
			camposComunesMostrar.add(CAMPO_AP2_ASEG);
		}
	}
	public boolean isCheckRazonSocialAseg() {
		return checkRazonSocialAseg;
	}
	public void setCheckRazonSocialAseg(boolean checkRazonSocialAseg) {
		this.checkRazonSocialAseg = checkRazonSocialAseg;
		if(checkRazonSocialAseg){
			camposComunesMostrar.add(CAMPO_RS_ASEG);
		}
	}
	public boolean isCheckNuevoPrimaComNeta() {
		return checkNuevoPrimaComNeta;
	}
	public void setCheckNuevoPrimaComNeta(boolean checkNuevoPrimaComNeta) {
		this.checkNuevoPrimaComNeta = checkNuevoPrimaComNeta;
		if(checkNuevoPrimaComNeta){
			camposComunesMostrar.add(CAMPO_N_PRIMA_COM_NETA);
		}
	}
	public boolean isCheckNuevoGastosAdmin() {
		return checkNuevoGastosAdmin;
	}
	public void setCheckNuevoGastosAdmin(boolean checkNuevoGastosAdmin) {
		this.checkNuevoGastosAdmin = checkNuevoGastosAdmin;
		if(checkNuevoGastosAdmin){
			camposComunesMostrar.add(CAMPO_N_GASTOS_ADMIN);
		}
	}
	public boolean isCheckNuevoGastosAdq() {
		return checkNuevoGastosAdq;
	}
	public void setCheckNuevoGastosAdq(boolean checkNuevoGastosAdq) {
		this.checkNuevoGastosAdq = checkNuevoGastosAdq;
		if(checkNuevoGastosAdq){
			camposComunesMostrar.add(CAMPO_N_GASTOS_ADQ);
		}
	}
	public boolean isCheckNuevoComisionEnt() {
		return checkNuevoComisionEnt;
	}
	public void setCheckNuevoComisionEnt(boolean checkNuevoComisionEnt) {
		this.checkNuevoComisionEnt = checkNuevoComisionEnt;
		if(checkNuevoComisionEnt){
			camposComunesMostrar.add(CAMPO_N_COMS_ENTD);
		}
	}
	public boolean isCheckNuevoComisionSubent() {
		return checkNuevoComisionSubent;
	}
	public void setCheckNuevoComisionSubent(boolean checkNuevoComisionSubent) {
		this.checkNuevoComisionSubent = checkNuevoComisionSubent;
		if(checkNuevoComisionSubent){
			camposComunesMostrar.add(CAMPO_N_COMS_SUBENT);
		}
	}
	public boolean isCheckNuevoTotalGastos() {
		return checkNuevoTotalGastos;
	}
	public void setCheckNuevoTotalGastos(boolean checkNuevoTotalGastos) {
		this.checkNuevoTotalGastos = checkNuevoTotalGastos;
		if(checkNuevoTotalGastos){
			camposComunesMostrar.add(CAMPO_N_TOTAL_GASTOS);
		}
	}
	public boolean isCheckReguPrimaComNeta() {
		return checkReguPrimaComNeta;
	}
	public void setCheckReguPrimaComNeta(boolean checkReguPrimaComNeta) {
		this.checkReguPrimaComNeta = checkReguPrimaComNeta;
		if(checkReguPrimaComNeta){
			camposComunesMostrar.add(CAMPO_R_PRIMA_COM_NETA);
		}
	}
	public boolean isCheckReguGastosAdmin() {
		return checkReguGastosAdmin;
	}
	public void setCheckReguGastosAdmin(boolean checkReguGastosAdmin) {
		this.checkReguGastosAdmin = checkReguGastosAdmin;
		if(checkReguGastosAdmin){
			camposComunesMostrar.add(CAMPO_R_GASTOS_ADMIN);
		}
	}
	public boolean isCheckReguGastosAdq() {
		return checkReguGastosAdq;
	}
	public void setCheckReguGastosAdq(boolean checkReguGastosAdq) {
		this.checkReguGastosAdq = checkReguGastosAdq;
		if(checkReguGastosAdq){
			camposComunesMostrar.add(CAMPO_R_GASTOS_ADQ);
		}
	}
	public boolean isCheckReguComisionEnt() {
		return checkReguComisionEnt;
	}
	public void setCheckReguComisionEnt(boolean checkReguComisionEnt) {
		this.checkReguComisionEnt = checkReguComisionEnt;
		if(checkReguComisionEnt){
			camposComunesMostrar.add(CAMPO_R_COMS_ENTD);
		}
	}
	public boolean isCheckReguComisionSubent() {
		return checkReguComisionSubent;
	}
	public void setCheckReguComisionSubent(boolean checkReguComisionSubent) {
		this.checkReguComisionSubent = checkReguComisionSubent;
		if(checkReguComisionSubent){
			camposComunesMostrar.add(CAMPO_R_COMS_SUBENT);
		}
	}
	public boolean isCheckReguTotalGastos() {
		return checkReguTotalGastos;
	}
	public void setCheckReguTotalGastos(boolean checkReguTotalGastos) {
		this.checkReguTotalGastos = checkReguTotalGastos;
		if(checkReguTotalGastos){
			camposComunesMostrar.add(CAMPO_R_TOTAL_GASTOS);
		}
	}
	public boolean isCheckTotPrimaComNeta() {
		return checkTotPrimaComNeta;
	}
	public void setCheckTotPrimaComNeta(boolean checkTotPrimaComNeta) {
		this.checkTotPrimaComNeta = checkTotPrimaComNeta;
		if(checkTotPrimaComNeta){
			camposComunesMostrar.add(CAMPO_T_PRIMA_COM_NETA);
		}
	}
	public boolean isCheckTotGastosAdmin() {
		return checkTotGastosAdmin;
	}
	public void setCheckTotGastosAdmin(boolean checkTotGastosAdmin) {
		this.checkTotGastosAdmin = checkTotGastosAdmin;
		if(checkTotGastosAdmin){
			camposComunesMostrar.add(CAMPO_T_GASTOS_ADMIN);
		}
	}
	public boolean isCheckTotGastosAdq() {
		return checkTotGastosAdq;
	}
	public void setCheckTotGastosAdq(boolean checkTotGastosAdq) {
		this.checkTotGastosAdq = checkTotGastosAdq;
		if(checkTotGastosAdq){
			camposComunesMostrar.add(CAMPO_T_GASTOS_ADQ);
		}
	}
	public boolean isCheckTotComisionEnt() {
		return checkTotComisionEnt;
	}
	public void setCheckTotComisionEnt(boolean checkTotComisionEnt) {
		this.checkTotComisionEnt = checkTotComisionEnt;
		if(checkTotComisionEnt){
			camposComunesMostrar.add(CAMPO_T_COMS_ENTD);
		}
	}
	public boolean isCheckTotComisionSubent() {
		return checkTotComisionSubent;
	}
	public void setCheckTotComisionSubent(boolean checkTotComisionSubent) {
		this.checkTotComisionSubent = checkTotComisionSubent;
		if(checkTotComisionEnt){
			camposComunesMostrar.add(CAMPO_T_COMS_SUBENT);
		}
	}
	public boolean isCheckTotTotalGastos() {
		return checkTotTotalGastos;
	}
	public void setCheckTotTotalGastos(boolean checkTotTotalGastos) {
		this.checkTotTotalGastos = checkTotTotalGastos;
		if(checkTotTotalGastos){
			camposComunesMostrar.add(CAMPO_T_TOTAL_GASTOS);
		}
	}
	public boolean isCheckTotGastosAdminAbon() {
		return checkTotGastosAdminAbon;
	}
	public void setCheckTotGastosAdminAbon(boolean checkTotGastosAdminAbon) {
		this.checkTotGastosAdminAbon = checkTotGastosAdminAbon;
		if(checkTotGastosAdminAbon){
			camposComunesMostrar.add(CAMPO_T_GASTOS_ADMIN_A);
		}
	}
	public boolean isCheckTotGastosAdqAbon() {
		return checkTotGastosAdqAbon;
	}
	public void setCheckTotGastosAdqAbon(boolean checkTotGastosAdqAbon) {
		this.checkTotGastosAdqAbon = checkTotGastosAdqAbon;
		if(checkTotGastosAdqAbon){
			camposComunesMostrar.add(CAMPO_T_GASTOS_ADQ_A);
		}
	}
	public boolean isCheckTotComisionEntAbon() {
		return checkTotComisionEntAbon;
	}
	public void setCheckTotComisionEntAbon(boolean checkTotComisionEntAbon) {
		this.checkTotComisionEntAbon = checkTotComisionEntAbon;
		if(checkTotComisionEntAbon){
			camposComunesMostrar.add(CAMPO_T_COMS_ENTD_A);
		}
	}
	public boolean isCheckTotComisionSubentAbon() {
		return checkTotComisionSubentAbon;
	}
	public void setCheckTotComisionSubentAbon(boolean checkTotComisionSubentAbon) {
		this.checkTotComisionSubentAbon = checkTotComisionSubentAbon;
		if(checkTotComisionSubentAbon){
			camposComunesMostrar.add(CAMPO_T_COMS_SUBENT_A);
		}
	}
	public boolean isCheckTotGastosAdminPdte() {
		return checkTotGastosAdminPdte;
	}
	public void setCheckTotGastosAdminPdte(boolean checkTotGastosAdminPdte) {
		this.checkTotGastosAdminPdte = checkTotGastosAdminPdte;
		if(checkTotGastosAdminPdte){
			camposComunesMostrar.add(CAMPO_T_GASTOS_ADMIN_P);
		}
	}
	public boolean isCheckTotGastosAdqPdte() {
		return checkTotGastosAdqPdte;
	}
	public void setCheckTotGastosAdqPdte(boolean checkTotGastosAdqPdte) {
		this.checkTotGastosAdqPdte = checkTotGastosAdqPdte;
		if(checkTotGastosAdqPdte){
			camposComunesMostrar.add(CAMPO_T_GASTOS_ADQ_P);
		}
	}
	public boolean isCheckTotComisionEntPdte() {
		return checkTotComisionEntPdte;
	}
	public void setCheckTotComisionEntPdte(boolean checkTotComisionEntPdte) {
		this.checkTotComisionEntPdte = checkTotComisionEntPdte;
		if(checkTotComisionEntPdte){
			camposComunesMostrar.add(CAMPO_T_COMS_ENTD_P);
		}
	}
	public boolean isCheckTotComisionSubentPdte() {
		return checkTotComisionSubentPdte;
	}
	public void setCheckTotComisionSubentPdte(boolean checkTotComisionSubentPdte) {
		this.checkTotComisionSubentPdte = checkTotComisionSubentPdte;
		if(checkTotComisionSubentPdte){
			camposComunesMostrar.add(CAMPO_T_COMS_SUBENT_P);
		}
	}
	public boolean isCheckTotTotalLiquidacion() {
		return checkTotTotalLiquidacion;
	}
	public void setCheckTotTotalLiquidacion(boolean checkTotTotalLiquidacion) {
		this.checkTotTotalLiquidacion = checkTotTotalLiquidacion;
		if(checkTotTotalLiquidacion){
			camposComunesMostrar.add(CAMPO_TOTAL_LIQ);
		}
	}
	public boolean isCheckTotTotalLiquidacionRga() {
		return checkTotTotalLiquidacionRga;
	}
	public void setCheckTotTotalLiquidacionRga(boolean checkTotTotalLiquidacionRga) {
		this.checkTotTotalLiquidacionRga = checkTotTotalLiquidacionRga;
		if(checkTotTotalLiquidacionRga){
			camposComunesMostrar.add(CAMPO_TOTAL_LIQ_RGA);
		}
	}
	public boolean isCheckReglaCodTram() {
		return checkReglaCodTram;
	}
	public void setCheckReglaCodTram(boolean checkReglaCodTram) {
		this.checkReglaCodTram = checkReglaCodTram;
		if(checkReglaCodTram){
			camposComunesMostrar.add(CAMPO_COD_TRAM);
		}
	}
	public boolean isCheckReglaTipoTram() {
		return checkReglaTipoTram;
	}
	public void setCheckReglaTipoTram(boolean checkReglaTipoTram) {
		this.checkReglaTipoTram = checkReglaTipoTram;
		if(checkReglaTipoTram){
			camposComunesMostrar.add(CAMPO_TIPO_TRAM);
		}
	}
	public boolean isCheckReglaImpApliTram() {
		return checkReglaImpApliTram;
	}
	public void setCheckReglaImpApliTram(boolean checkReglaImpApliTram) {
		this.checkReglaImpApliTram = checkReglaImpApliTram;
		if(checkReglaImpApliTram){
			camposComunesMostrar.add(CAMPO_IMP_APLI_TRAM);
		}
	}
	public boolean isCheckReglaImpSinReduccTram() {
		return checkReglaImpSinReduccTram;
	}
	public void setCheckReglaImpSinReduccTram(boolean checkReglaImpSinReduccTram) {
		this.checkReglaImpSinReduccTram = checkReglaImpSinReduccTram;
		if(checkReglaImpSinReduccTram){
			camposComunesMostrar.add(CAMPO_IMP_SIN_REDUCC_TRAM);
		}
	}
	public boolean isCheckReglaCodCalidad() {
		return checkReglaCodCalidad;
	}
	public void setCheckReglaCodCalidad(boolean checkReglaCodCalidad) {
		this.checkReglaCodCalidad = checkReglaCodCalidad;
		if(checkReglaCodCalidad){
			camposComunesMostrar.add(CAMPO_COD_CALIDAD);
		}
	}
	public boolean isCheckReglaTipoCalidad() {
		return checkReglaTipoCalidad;
	}
	public void setCheckReglaTipoCalidad(boolean checkReglaTipoCalidad) {
		this.checkReglaTipoCalidad = checkReglaTipoCalidad;
		if(checkReglaTipoCalidad){
			camposComunesMostrar.add(CAMPO_TIPO_CALIDAD);
		}
	}
	public boolean isCheckReglaImpApliCal() {
		return checkReglaImpApliCal;
	}
	public void setCheckReglaImpApliCal(boolean checkReglaImpApliCal) {
		this.checkReglaImpApliCal = checkReglaImpApliCal;
		if(checkReglaImpApliCal){
			camposComunesMostrar.add(CAMPO_IMP_APLI_CALIDAD);
		}
	}
	public boolean isCheckReglaImpSinReduccCal() {
		return checkReglaImpSinReduccCal;
	}
	public void setCheckReglaImpSinReduccCal(boolean checkReglaImpSinReduccCal) {
		this.checkReglaImpSinReduccCal = checkReglaImpSinReduccCal;
		if(checkReglaImpSinReduccCal){
			camposComunesMostrar.add(CAMPO_IMP_SIN_REDUCC_CALIDAD);
		}
	}
	public boolean isCheckReglaTotalTramCal() {
		return checkReglaTotalTramCal;
	}
	public void setCheckReglaTotalTramCal(boolean checkReglaTotalTramCal) {
		this.checkReglaTotalTramCal = checkReglaTotalTramCal;
		if(checkReglaTotalTramCal){
			camposComunesMostrar.add(CAMPO_TOTAL_TRAM_CALIDAD);
		}
	}
	//********************************************************************//
	
	// OTRAS PROPIEDADES
	public Set<String> getCamposComunesMostrar() {
		return camposComunesMostrar;
	}
	public Map<String, String[]> getMapaFiltro() {
		return mapaFiltro;
	}
	public String getDatosDe() {
		return datosDe;
	}
	public void setDatosDe(String datosDe) {
		this.datosDe = datosDe;
	}
	public String getCampoOrdenar() {
		return campoOrdenar;
	}
	public void setCampoOrdenar(String campoOrdenar) {
		this.campoOrdenar = campoOrdenar;
	}
	public String getSentido() {
		return sentido;
	}
	public void setSentido(String sentido) {
		this.sentido = sentido;
	}
	public String getFormato() {
		return formato;
	}
	public void setFormato(String formato) {
		this.formato = formato;
	}	


	
	//********************************************************************//
	
	/** DAA 03/10/2013
	 *  Metodo para rellenar el mapa que contendr� el filtro del informe con sus condiciones
	 * @param 
	 */
	private void rellenaMapaFiltro(String key, int posicion, String valor) {
		if(mapaFiltro.containsKey(key)){
			String[] aux = mapaFiltro.get(key);
			aux[posicion]= valor;
			mapaFiltro.put(key, aux);
		}	
		else{
			String[] lstOperadorYValor = new String[2];
			lstOperadorYValor[posicion]= valor;
			mapaFiltro.put(key, lstOperadorYValor);	
		}
	}
}	