package com.rsi.agp.dao.tables.comisiones;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.rsi.agp.core.util.ConstantsInf;

public class InformeComisiones {
	
	
	// CONSTANTES PARA LOS NOMBRES DE LOS CAMPOS DE LA TABLA "TB_COMS_INFORMES_COMISIONES"
	private static final String CAMPO_FASE            = "NUMFAS";
	private static final String CAMPO_ENTIDAD         = "CODENTIDAD";
	private static final String CAMPO_ENTIDAD_MED     = "CODENTMED";
	private static final String CAMPO_SUBENT          = "CODSUBMED";
	private static final String CAMPO_OFICINA         = "CODOFI";
	private static final String CAMPO_PLAN 	          = "CODPLN";
	private static final String CAMPO_LINEA           = "CODLIN";
	private static final String CAMPO_COLECTIVO       = "CODCOL";
	private static final String CAMPO_FECHA_ACEP      = "FECACEP";
	private static final String CAMPO_NOM_TOM         = "NOMTOM";
	private static final String CAMPO_REFERENCIA      = "REFPLZ";
	private static final String CAMPO_NIF             = "NIFASG";
	private static final String CAMPO_NOM_ASEG 	      = "NOMASG";
	private static final String CAMPO_APE_1           = "APE1ASG";
	private static final String CAMPO_APE_2	          = "APE2ASG";
	private static final String CAMPO_RAZON_SOCIAL    = "RAZONSOCIAL";
	
	private static final String CAMPO_PRIN_NUE        = "PRINUE";
	private static final String CAMPO_GASENT_NUE      = "GASENTNUE";
	private static final String CAMPO_GASSUB_NUE      = "GASSUBNUE";  
	private static final String CAMPO_COMMENT_NUE     = "COMMENTNUE";   
	private static final String CAMPO_COM_SUB_NUE     = "COMSUBNUE";    
	private static final String CAMPO_GAS_NUE         = "GASNUE";       
	private static final String CAMPO_GAS_R_GANUE     = "GASRGANUE";    
	private static final String CAMPO_TOT_NUE         = "TOTNUE";       
	
	private static final String CAMPO_PRI_REG         = "PRIREG";
	private static final String CAMPO_GASENT_REG      = "GASENTREG";    
	private static final String CAMPO_GAS_SUB_REG     = "GASSUBREG";    
	private static final String CAMPO_COMENT_REG      = "COMENTREG";
	private static final String CAMPO_COM_SUB_REG     = "COMSUBREG";
	private static final String CAMPO_GAS_REG         = "GASREG";       
	private static final String CAMPO_GAS_RGA_REG     = "GASRGAREG";    
	private static final String CAMPO_TOT_REG         = "TOTREG";       
	  
	  
	private static final String CAMPO_PRIM_SUM        = "PRISUM";       
	private static final String CAMPO_GASENT_SUM      = "GASENTSUM";    
	private static final String CAMPO_GAS_SUB_SUM     = "GASSUBSUM";    
	private static final String CAMPO_COMENT_SUM      = "COMENTSUM";    
	private static final String CAMPO_COM_SUB_SUM     = "COMSUBSUM";    
	private static final String CAMPO_GAS_SUM         = "GASSUM";       
	private static final String CAMPO_GAS_RGA_SUM     = "GASRGASUM";    
	private static final String CAMPO_TOT_SUM         = "TOTSUM";       
	private static final String CAMPO_GAS_PEN         = "GASPEN";       
	private static final String CAMPO_GAS_PAG         = "GASPAG";       
	private static final String CAMPO_TOT_LIQ         = "TOTLIQ";       
	private static final String CAMPO_TOT_ENT         = "TOTENT";       
	private static final String CAMPO_TOT_SUB         = "TOTSUB";       
	private static final String CAMPO_COST_TOT        = "COSTOT"; 
	
	private static final String CAMPO_COD_METRA       = "CODMETRA";     
	private static final String CAMPO_TIP_MD_TRA      = "TIPMDTRA";     
	private static final String CAMPO_IMP_TRA         = "IMPTRA";       
	private static final String CAMPO_IMP_SIN_RED_TRA = "IMPSINREDTRA"; 
	private static final String CAMPO_COD_MED_CAL     = "CODMEDCAL";    
	private static final String CAMPO_TIP_MED_CAL     = "TIPMEDCAL";    
	private static final String CAMPO_IMP_CAL         = "IMPCAL";       
	private static final String CAMPO_IMP_SIN_RED_CAL = "IMPSINREDCAL"; 
	private static final String CAMPO_TOT_TRAM_CAL    ="TOTTRAMCAL";   
	   
	
	//PROPIEDADES PARA LOS INPUTS DE FILTRO
	private String  fase;
	private String  entidad;
	private String  oficina;
	private String  entidadMed;        
	private String  subent;           
	private String  plan;                 
	private String  linea;                
	private String  colectivo;           
	private String  fechaAcep;             
	private String  referencia;              
	private String  nif;
	
	
	//PROPIEDADES PARA LOS SELECT DE CONDICIONES
	private String  condiFase;
	private String  condiEntidad;
	private String  condiOficina;
	private String  condiEntidadMed;        
	private String  condiSubent;           
	private String  condiPlan;                 
	private String  condiLinea;                
	private String  condiColectivo;           
	private String  condiFechaAcep;             
	private String  condiReferencia;              
	private String  condiNif;  
	
	//PROPIEDADES PARA LOS CHECKS
	
	// Datos generales
	private boolean checkFase;
	private boolean checkEntidad;
	private boolean checkEntidadMed;
	private boolean checkSubent; 
	private boolean checkOficina; 
	private boolean checkPlan;                 
	private boolean checkLinea;  
	private boolean checkFechaAcep;  
	private boolean checkColectivo;  
	private boolean checkTomador;  
	private boolean checkReferencia; 
	private boolean checkNif; 
	private boolean checkNombre;
	private boolean checkApe1; 
	private boolean checkApe2; 
	private boolean checkRazonSocial; 
	
	//Nuevo
	private boolean checkPrimaBasN;
	private boolean checkGGEntN;
	private boolean checkGGSubEntN;
	private boolean checkComEntN;
	private boolean checkComSubEntN;
	private boolean checkGGAgroN;
	private boolean checkGGARgaN;
	private boolean checkTotAgroN;
	
	//Regularizacion
	private boolean checkPrimaBasR;
	private boolean checkGGEntR;
	private boolean checkGGSubEntR;
	private boolean checkComEntR;
	private boolean checkComSubEntR;
	private boolean checkGGAgroR;
	private boolean checkGGARgaR;
	private boolean checkTotAgroR;
	
	
	//Total (N + R)
	private boolean checkPrimaBasT;
	private boolean checkGGEntT;
	private boolean checkGGSubEntT;
	private boolean checkComEntT;
	private boolean checkComSubEntT;
	private boolean checkGGAgroT;
	private boolean checkGGARgaT;
	private boolean checkTotAgroT;
	private boolean checkGasPenT;
	private boolean checkGasPagT;
	private boolean checkTotLiquiT;
	private boolean checkTotEntT;
	private boolean checkTotSubentT;
	private boolean checkCosteTotalT;
	
	//Reglamento
	private boolean checkCodTram;
	private boolean checkTipoTram;
	private boolean checkImpApliTram;
	private boolean checkImpSinReduccTram;
	private boolean checkcodCalidad;
	private boolean checkTipoCalidad;
	private boolean checkImpApliCal;
	private boolean checkImpSinReduccCal;
	private boolean checkTotTramCal;
	 
	
	// OTRAS PROPIEDADES
	private Set<String> camposResumenMostrar = new HashSet<String>();
	private Set<String> camposComunesMostrar = new HashSet<String>();
	private Set<String> camposDetalleMostrar = new HashSet<String>();
	private Map<String, String[]> mapaFiltro = new HashMap<String, String[]>();
	private String datosDe;
	private String campoOrdenar;
	private String sentido;
	private String formato = new Integer(ConstantsInf.COD_FORMATO_PDF).toString();
	
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
			rellenaMapaFiltro(CAMPO_ENTIDAD_MED, 1, entidadMed);
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
	public String getColectivo() {
		return colectivo;
	}
	public void setColectivo(String colectivo) {
		this.colectivo = colectivo;
		if(!("").equals(colectivo)){
			rellenaMapaFiltro(CAMPO_COLECTIVO, 1, colectivo);
		}
	}
	public String getFechaAcep() {
		return fechaAcep;
	}
	public void setFechaAcep(String fechaAcep) {
		this.fechaAcep = fechaAcep;
		if(!("").equals(fechaAcep)){
			rellenaMapaFiltro(CAMPO_FECHA_ACEP, 1, fechaAcep);
		}
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
		if(!("").equals(referencia)){
			rellenaMapaFiltro(CAMPO_REFERENCIA, 1, referencia);
		}
	}
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
			this.nif = nif;
			if(!("").equals(nif)){
				rellenaMapaFiltro(CAMPO_NIF, 1, nif);
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
			rellenaMapaFiltro(CAMPO_ENTIDAD_MED, 0, condiEntidadMed);
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
	public String getCondiColectivo() {
		return condiColectivo;
	}
	public void setCondiColectivo(String condiColectivo) {
		this.condiColectivo = condiColectivo;
		if(!("").equals(condiColectivo)){
			rellenaMapaFiltro(CAMPO_COLECTIVO, 0, condiColectivo);
		}
	}
	public String getCondiFechaAcep() {
		return condiFechaAcep;
	}
	public void setCondiFechaAcep(String condiFechaAcep) {
		this.condiFechaAcep = condiFechaAcep;
		if(!("").equals(condiFechaAcep)){
			rellenaMapaFiltro(CAMPO_FECHA_ACEP, 0, condiFechaAcep);
		}
	}
	public String getCondiReferencia() {
		return condiReferencia;
	}
	public void setCondiReferencia(String condiReferencia) {
		this.condiReferencia = condiReferencia;
		if(!("").equals(condiReferencia)){
			rellenaMapaFiltro(CAMPO_REFERENCIA, 0, condiReferencia);
		}
	}
	public String getCondiNif() {
		return condiNif;
	}
	public void setCondiNif(String condiNif) {
		this.condiNif = condiNif;
		if(!("").equals(condiNif)){
			rellenaMapaFiltro(CAMPO_NIF, 0, condiNif);
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
	public boolean isCheckEntidad() {
		return checkEntidad;
	}
	public void setCheckEntidad(boolean checkEntidad) {
		this.checkEntidad = checkEntidad;
		if(checkEntidad){
			camposDetalleMostrar.add(CAMPO_ENTIDAD);
		}
	}
	public boolean isCheckOficina() {
		return checkOficina;
	}
	public void setCheckOficina(boolean checkOficina) {
		this.checkOficina = checkOficina;
		if(checkOficina){
			camposDetalleMostrar.add(CAMPO_OFICINA);
		}
	}
	public boolean isCheckEntidadMed() {
		return checkEntidadMed;
	}
	public void setCheckEntidadMed(boolean checkEntidadMed) {
		this.checkEntidadMed = checkEntidadMed;
		if(checkEntidadMed){
			camposComunesMostrar.add(CAMPO_ENTIDAD_MED);
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
	public boolean isCheckFechaAcep() {
		return checkFechaAcep;
	}
	public void setCheckFechaAcep(boolean checkFechaAcep) {
		this.checkFechaAcep = checkFechaAcep;
		if(checkFechaAcep){
			camposComunesMostrar.add(CAMPO_FECHA_ACEP);
		}
	}
	public boolean isCheckColectivo() {
		return checkColectivo;
	}
	public void setCheckColectivo(boolean checkColectivo) {
		this.checkColectivo = checkColectivo;
		if(checkColectivo){
			camposComunesMostrar.add(CAMPO_COLECTIVO);
		}
	}
	public boolean isCheckTomador() {
		return checkTomador;
	}
	public void setCheckTomador(boolean checkTomador) {
		this.checkTomador = checkTomador;
		if(checkTomador){
			camposResumenMostrar.add(CAMPO_NOM_TOM);
		}
	}
	public boolean isCheckReferencia() {
		return checkReferencia;
	}
	public void setCheckReferencia(boolean checkReferencia) {
		this.checkReferencia = checkReferencia;
		if(checkReferencia){
			camposDetalleMostrar.add(CAMPO_REFERENCIA);
		}
	}
	public boolean isCheckNif() {
		return checkNif;
	}
	public void setCheckNif(boolean checkNif) {
		this.checkNif = checkNif;
		if(checkNif){
			camposDetalleMostrar.add(CAMPO_NIF);
		}
	}
	public boolean isCheckNombre() {
		return checkNombre;
	}
	public void setCheckNombre(boolean checkNombre) {
		this.checkNombre = checkNombre;
		if(checkNombre){
			camposDetalleMostrar.add(CAMPO_NOM_ASEG);
		}
	}
	public boolean isCheckApe1() {
		return checkApe1;
	}
	public void setCheckApe1(boolean checkApe1) {
		this.checkApe1 = checkApe1;
		if(checkApe1){
			camposDetalleMostrar.add(CAMPO_APE_1);
		}
	}
	public boolean isCheckApe2() {
		return checkApe2;
	}
	public void setCheckApe2(boolean checkApe2) {
		this.checkApe2 = checkApe2;
		if(checkApe2){
			camposDetalleMostrar.add(CAMPO_APE_2);
		}
	}
	public boolean isCheckRazonSocial() {
		return checkRazonSocial;
	}
	public void setCheckRazonSocial(boolean checkRazonSocial) {
		this.checkRazonSocial = checkRazonSocial;
		if(checkRazonSocial){
			camposDetalleMostrar.add(CAMPO_RAZON_SOCIAL);
		}
	}
	public boolean isCheckPrimaBasN() {
		return checkPrimaBasN;
	}
	public void setCheckPrimaBasN(boolean checkPrimaBasN) {
		this.checkPrimaBasN = checkPrimaBasN;
		if(checkPrimaBasN){
			camposComunesMostrar.add(CAMPO_PRIN_NUE);
		}
	}
	public boolean isCheckGGEntN() {
		return checkGGEntN;
	}
	public void setCheckGGEntN(boolean checkGGEntN) {
		this.checkGGEntN = checkGGEntN;
		if(checkGGEntN){
			camposComunesMostrar.add(CAMPO_GASENT_NUE);
		}
	}
	public boolean isCheckGGSubEntN() {
		return checkGGSubEntN;
	}
	public void setCheckGGSubEntN(boolean checkGGSubEntN) {
		this.checkGGSubEntN = checkGGSubEntN;
		if(checkGGSubEntN){
			camposComunesMostrar.add(CAMPO_GASSUB_NUE);
		}
	}
	public boolean isCheckComEntN() {
		return checkComEntN;
	}
	public void setCheckComEntN(boolean checkComEntN) {
		this.checkComEntN = checkComEntN;
		if(checkComEntN){
			camposComunesMostrar.add(CAMPO_COMMENT_NUE);
		}
	}
	public boolean isCheckComSubEntN() {
		return checkComSubEntN;
	}
	public void setCheckComSubEntN(boolean checkComSubEntN) {
		this.checkComSubEntN = checkComSubEntN;
		if(checkComSubEntN){
			camposComunesMostrar.add(CAMPO_COM_SUB_NUE);
		}
	}
	public boolean isCheckGGAgroN() {
		return checkGGAgroN;
	}
	public void setCheckGGAgroN(boolean checkGGAgroN) {
		this.checkGGAgroN = checkGGAgroN;
		if(checkGGAgroN){
			camposComunesMostrar.add(CAMPO_GAS_NUE);
		}
	}
	public boolean isCheckGGARgaN() {
		return checkGGARgaN;
	}
	public void setCheckGGARgaN(boolean checkGGARgaN) {
		this.checkGGARgaN = checkGGARgaN;
		if(checkGGARgaN){
			camposComunesMostrar.add(CAMPO_GAS_R_GANUE);
		}
	}
	public boolean isCheckTotAgroN() {
		return checkTotAgroN;
	}
	public void setCheckTotAgroN(boolean checkTotAgroN) {
		this.checkTotAgroN = checkTotAgroN;
		if(checkTotAgroN){
			camposComunesMostrar.add(CAMPO_TOT_NUE);
		}
	}
	public boolean isCheckPrimaBasR() {
		return checkPrimaBasR;
	}
	public void setCheckPrimaBasR(boolean checkPrimaBasR) {
		this.checkPrimaBasR = checkPrimaBasR;
		if(checkPrimaBasR){
			camposComunesMostrar.add(CAMPO_PRI_REG);
		}
	}
	public boolean isCheckGGEntR() {
		return checkGGEntR;
	}
	public void setCheckGGEntR(boolean checkGGEntR) {
		this.checkGGEntR = checkGGEntR;
		if(checkGGEntR){
			camposComunesMostrar.add(CAMPO_GASENT_REG);
		}
	}
	public boolean isCheckGGSubEntR() {
		return checkGGSubEntR;
	}
	public void setCheckGGSubEntR(boolean checkGGSubEntR) {
		this.checkGGSubEntR = checkGGSubEntR;
		if(checkGGSubEntR){
			camposComunesMostrar.add(CAMPO_GAS_SUB_REG);
		}
	}
	public boolean isCheckComEntR() {
		return checkComEntR;
	}
	public void setCheckComEntR(boolean checkComEntR) {
		this.checkComEntR = checkComEntR;
		if(checkComEntR){
			camposComunesMostrar.add(CAMPO_COMENT_REG);
		}
	}
	public boolean isCheckComSubEntR() {
		return checkComSubEntR;
	}
	public void setCheckComSubEntR(boolean checkComSubEntR) {
		this.checkComSubEntR = checkComSubEntR;
		if(checkComSubEntR){
			camposComunesMostrar.add(CAMPO_COM_SUB_REG);
		}
	}
	public boolean isCheckGGAgroR() {
		return checkGGAgroR;
	}
	public void setCheckGGAgroR(boolean checkGGAgroR) {
		this.checkGGAgroR = checkGGAgroR;
		if(checkGGAgroR){
			camposComunesMostrar.add(CAMPO_GAS_REG);
		}
	}
	public boolean isCheckGGARgaR() {
		return checkGGARgaR;
	}
	public void setCheckGGARgaR(boolean checkGGARgaR) {
		this.checkGGARgaR = checkGGARgaR;
		if(checkGGARgaR){
			camposComunesMostrar.add(CAMPO_GAS_RGA_REG);
		}
	}
	public boolean isCheckTotAgroR() {
		return checkTotAgroR;
	}
	public void setCheckTotAgroR(boolean checkTotAgroR) {
		this.checkTotAgroR = checkTotAgroR;
		if(checkTotAgroR){
			camposComunesMostrar.add(CAMPO_TOT_REG);
		}
	}
	public boolean isCheckPrimaBasT() {
		return checkPrimaBasT;
	}
	public void setCheckPrimaBasT(boolean checkPrimaBasT) {
		this.checkPrimaBasT = checkPrimaBasT;
		if(checkPrimaBasT){
			camposComunesMostrar.add(CAMPO_PRIM_SUM);
		}
	}
	public boolean isCheckGGEntT() {
		return checkGGEntT;
	}
	public void setCheckGGEntT(boolean checkGGEntT) {
		this.checkGGEntT = checkGGEntT;
		if(checkGGEntT){
			camposComunesMostrar.add(CAMPO_GASENT_SUM);
		}
	}
	public boolean isCheckGGSubEntT() {
		return checkGGSubEntT;
	}
	public void setCheckGGSubEntT(boolean checkGGSubEntT) {
		this.checkGGSubEntT = checkGGSubEntT;
		if(checkGGSubEntT){
			camposComunesMostrar.add(CAMPO_GAS_SUB_SUM);
		}
	}
	public boolean isCheckComEntT() {
		return checkComEntT;
	}
	public void setCheckComEntT(boolean checkComEntT) {
		this.checkComEntT = checkComEntT;
		if(checkComEntT){
			camposComunesMostrar.add(CAMPO_COMENT_SUM);
		}
	}
	public boolean isCheckComSubEntT() {
		return checkComSubEntT;
	}
	public void setCheckComSubEntT(boolean checkComSubEntT) {
		this.checkComSubEntT = checkComSubEntT;
		if(checkComSubEntT){
			camposComunesMostrar.add(CAMPO_COM_SUB_SUM);
		}
	}
	public boolean isCheckGGAgroT() {
		return checkGGAgroT;
	}
	public void setCheckGGAgroT(boolean checkGGAgroT) {
		this.checkGGAgroT = checkGGAgroT;
		if(checkGGAgroT){
			camposComunesMostrar.add(CAMPO_GAS_SUM);
		}
	}
	public boolean isCheckGGARgaT() {
		return checkGGARgaT;
	}
	public void setCheckGGARgaT(boolean checkGGARgaT) {
		this.checkGGARgaT = checkGGARgaT;
		if(checkGGARgaT){
			camposComunesMostrar.add(CAMPO_GAS_RGA_SUM);
		}
	}
	public boolean isCheckTotAgroT() {
		return checkTotAgroT;
	}
	public void setCheckTotAgroT(boolean checkTotAgroT) {
		this.checkTotAgroT = checkTotAgroT;
		if(checkTotAgroT){
			camposComunesMostrar.add(CAMPO_TOT_SUM);
		}
	}
	public boolean isCheckGasPenT() {
		return checkGasPenT;
	}
	public void setCheckGasPenT(boolean checkGasPenT) {
		this.checkGasPenT = checkGasPenT;
		if(checkGasPenT){
			camposResumenMostrar.add(CAMPO_GAS_PEN);
		}
	}
	public boolean isCheckGasPagT() {
		return checkGasPagT;
	}
	public void setCheckGasPagT(boolean checkGasPagT) {
		this.checkGasPagT = checkGasPagT;
		if(checkGasPagT){
			camposResumenMostrar.add(CAMPO_GAS_PAG);
		}
	}
	public boolean isCheckTotLiquiT() {
		return checkTotLiquiT;
	}
	public void setCheckTotLiquiT(boolean checkTotLiquiT) {
		this.checkTotLiquiT = checkTotLiquiT;
		if(checkTotLiquiT){
			camposResumenMostrar.add(CAMPO_TOT_LIQ);
		}
	}
	public boolean isCheckTotEntT() {
		return checkTotEntT;
	}
	public void setCheckTotEntT(boolean checkTotEntT) {
		this.checkTotEntT = checkTotEntT;
		if(checkTotEntT){
			camposResumenMostrar.add(CAMPO_TOT_ENT);
		}
	}
	public boolean isCheckTotSubentT() {
		return checkTotSubentT;
	}
	public void setCheckTotSubentT(boolean checkTotSubentT) {
		this.checkTotSubentT = checkTotSubentT;
		if(checkTotSubentT){
			camposResumenMostrar.add(CAMPO_TOT_SUB);
		}
	}
	public boolean isCheckCosteTotalT() {
		return checkCosteTotalT;
	}
	public void setCheckCosteTotalT(boolean checkCosteTotalT) {
		this.checkCosteTotalT = checkCosteTotalT;
		if(checkCosteTotalT){
			camposComunesMostrar.add(CAMPO_COST_TOT);
		}
	}
	public boolean isCheckCodTram() {
		return checkCodTram;
	}
	public void setCheckCodTram(boolean checkCodTram) {
		this.checkCodTram = checkCodTram;
		if(checkCodTram){
			camposDetalleMostrar.add(CAMPO_COD_METRA);
		}
	}
	public boolean isCheckTipoTram() {
		return checkTipoTram;
	}
	public void setCheckTipoTram(boolean checkTipoTram) {
		this.checkTipoTram = checkTipoTram;
		if(checkTipoTram){
			camposDetalleMostrar.add(CAMPO_TIP_MD_TRA);
		}
	}
	public boolean isCheckImpApliTram() {
		return checkImpApliTram;
	}
	public void setCheckImpApliTram(boolean checkImpApliTram) {
		this.checkImpApliTram = checkImpApliTram;
		if(checkImpApliTram){
			camposComunesMostrar.add(CAMPO_IMP_TRA);
		}
	}
	public boolean isCheckImpSinReduccTram() {
		return checkImpSinReduccTram;
	}
	public void setCheckImpSinReduccTram(boolean checkImpSinReduccTram) {
		this.checkImpSinReduccTram = checkImpSinReduccTram;
		if(checkImpSinReduccTram){
			camposComunesMostrar.add(CAMPO_IMP_SIN_RED_TRA);
		}
	}
	public boolean isCheckcodCalidad() {
		return checkcodCalidad;
	}
	public void setCheckcodCalidad(boolean checkcodCalidad) {
		this.checkcodCalidad = checkcodCalidad;
		if(checkcodCalidad){
			camposDetalleMostrar.add(CAMPO_COD_MED_CAL);
		}
	}
	public boolean isCheckTipoCalidad() {
		return checkTipoCalidad;
	}
	public void setCheckTipoCalidad(boolean checkTipoCalidad) {
		this.checkTipoCalidad = checkTipoCalidad;
		if(checkTipoCalidad){
			camposDetalleMostrar.add(CAMPO_TIP_MED_CAL);
		}
	}
	public boolean isCheckImpApliCal() {
		return checkImpApliCal;
	}
	public void setCheckImpApliCal(boolean checkImpApliCal) {
		this.checkImpApliCal = checkImpApliCal;
		if(checkImpApliCal){
			camposComunesMostrar.add(CAMPO_IMP_CAL);
		}
	}
	public boolean isCheckImpSinReduccCal() {
		return checkImpSinReduccCal;
	}
	public void setCheckImpSinReduccCal(boolean checkImpSinReduccCal) {
		this.checkImpSinReduccCal = checkImpSinReduccCal;
		if(checkImpSinReduccCal){
			camposComunesMostrar.add(CAMPO_IMP_SIN_RED_CAL);
		}
	}
	public boolean isCheckTotTramCal() {
		return checkTotTramCal;
	}
	public void setCheckTotTramCal(boolean checkTotTramCal) {
		this.checkTotTramCal = checkTotTramCal;
		if(checkTotTramCal){
			camposComunesMostrar.add(CAMPO_TOT_TRAM_CAL);
		}
	}
	
		
	// OTRAS PROPIEDADES
	
	public Set<String> getCamposResumenMostrar() {
		return camposResumenMostrar;
	}
	
	public Set<String> getCamposComunesMostrar() {
		return camposComunesMostrar;
	}
	
	public Set<String> getCamposDetalleMostrar() {
		return camposDetalleMostrar;
	}
	
	public Map<String, String[]> getMapaFiltro() {
		return mapaFiltro;
	}
	public void setMapaFiltro(Map<String, String[]> mapaFiltro) {
		this.mapaFiltro = mapaFiltro;
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
	
	
	/**
	 *  Metodo para rellenar el mapa que contendra el filtro del informe con sus condiciones
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
	