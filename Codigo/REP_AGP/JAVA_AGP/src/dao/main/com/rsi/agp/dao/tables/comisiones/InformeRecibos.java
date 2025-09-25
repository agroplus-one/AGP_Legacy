package com.rsi.agp.dao.tables.comisiones;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.rsi.agp.core.util.ConstantsInf;

public class InformeRecibos implements Serializable{
		
		// CONSTANTES PARA LOS NOMBRES DE LOS CAMPOS DE LA TABLA "TB_COMS_INFORMES_RECIBOS"
		private static final String CAMPO_FASE = "FASE";
		private static final String CAMPO_ENTIDAD = "CODENTIDAD";
		private static final String CAMPO_OFICINA = "CODOFICINA";
		private static final String CAMPO_ENTIDAD_MED = "CODENTIDAD_MED";
		private static final String CAMPO_SUBENT = "CODSUBENT_MED";
		private static final String CAMPO_PLAN = "CODPLAN";
		private static final String CAMPO_LINEA = "CODLINEA";
		private static final String CAMPO_COLECTIVO = "REF_COLECTIVO";
		private static final String CAMPO_FECHA = "FECHA_EMISION";
		private static final String CAMPO_RS_TOM = "RS_TOMADOR";
		private static final String CAMPO_RECIBO = "NUM_RECIBO";
		private static final String CAMPO_SALDO_TOM = "SALDO_TOMADOR";
		private static final String CAMPO_COMP_TOM = "COMPENSACION_TOM";
		private static final String CAMPO_COMP_IMP = "COMPENSACION_IMP";
		private static final String CAMPO_PAGO_RECIBO = "PAGO_RECIBO";
		private static final String CAMPO_LIQUIDO_RECIBO = "LIQUIDO_RECIBO";
		private static final String CAMPO_REFERENCIA = "REF_POLIZA";
		private static final String CAMPO_TIPO_RECIBO = "TIPO_RECIBO";
		private static final String CAMPO_NIF = "NIF_ASEGURADO";
		private static final String CAMPO_APE1 = "AP1_ASEG";
		private static final String CAMPO_APE2 = "AP2_ASEG";
		private static final String CAMPO_NOMBRE = "NOM_ASEG";
		private static final String CAMPO_RAZON_SOCIAL = "RAZONSOCIAL_ASEG";
		private static final String CAMPO_PRIMA_COMERCIAL = "PRIMA_COMERCIAL";
		private static final String CAMPO_PRIMA_NETA = "PRIMA_NETA";
		private static final String CAMPO_COSTE_NETO = "COSTE_NETO";
		private static final String CAMPO_COSTE_TOM = "COSTE_TOMADOR";
		private static final String CAMPO_PAGO_POLIZA = "PAGO_POLIZA";
		private static final String CAMPO_SALDO_POLIZA = "SALDO_POLIZA";
		
		//********************************************************************//
		
		// OTRAS PROPIEDADES
		private Set<String> camposResumenMostrar = new HashSet<String>();
		private Set<String> camposComunesMostrar = new HashSet<String>();
		private Set<String> camposDetalleMostrar = new HashSet<String>();
		private Map<String, String[]> mapaFiltro = new HashMap<String, String[]>();
		private String datosDe;
		private String campoOrdenar;
		private String sentido;
		private String formato = new Integer(ConstantsInf.COD_FORMATO_PDF).toString();
		
		//********************************************************************//
	
		//PROPIEDADES PARA LOS INPUTS DE FILTRO
		private String  fase;
		private String	entidad;
		private String	oficina;
		private String  entidadMed;        
		private String  subent;           
		private String  plan;                 
		private String  linea;                
		private String  colectivo;           
		private String  fecha;             
		private String  recibo;                            
		private String  referencia;              
		private String  nif;
		
		//********************************************************************//
		
		//PROPIEDADES PARA LOS SELECT DE CONDICIONES
		private String  condiFase;
		private String	condiEntidad;
		private String	condiOficina;
		private String  condiEntidadMed;        
		private String  condiSubent;           
		private String  condiPlan;                 
		private String  condiLinea;                
		private String  condiColectivo;           
		private String  condiFecha;             
		private String  condiRecibo;                            
		private String  condiReferencia;              
		private String  condiNif;
		
		//********************************************************************//
		
		//PROPIEDADES PARA LOS CHECKS
		private boolean checkFase;
		private boolean checkEntidad;
		private boolean checkOficina;
		private boolean checkEntidadMed;        
		private boolean checkSubent;      
		private boolean checkPlan;                 
		private boolean checkLinea;  
		private boolean checkFecha;  
		private boolean checkColectivo;  
		private boolean checkTomador;  
		private boolean checkRecibo;              
		private boolean checkSaldoTom;
		private boolean checkCompTom; 
		private boolean checkCompImp; 
		private boolean checkPagoRecibo; 
		private boolean checkLiquidoRecibo; 
		private boolean checkReferencia; 
		private boolean checkTipoRecibo; 
		private boolean checkNif; 
		private boolean checkApe1; 
		private boolean checkApe2; 
		private boolean checkNombre; 
		private boolean checkRazonSocial; 
		private boolean checkPrimaCom; 
		private boolean checkPrimaNeta; 
		private boolean checkCosteNeto; 
		private boolean checkCosteTom; 
		private boolean checkPagoPlz; 
		private boolean checkSaldo;
		
		//********************************************************************//
		
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
		public String getFecha() {
			return fecha;
		}
		public void setFecha(String fecha) {
			this.fecha = fecha;
			if(!("").equals(fecha)){
				rellenaMapaFiltro(CAMPO_FECHA, 1, fecha);
			}
		}
		public String getRecibo() {
			return recibo;
		}
		public void setRecibo(String recibo) {
			this.recibo = recibo;
			if(!("").equals(recibo)){
				rellenaMapaFiltro(CAMPO_RECIBO, 1, recibo);
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

		//********************************************************************//
		
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
				rellenaMapaFiltro(CAMPO_LINEA, 0, condiLinea);
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
		public String getCondiFecha() {
			return condiFecha;
		}
		public void setCondiFecha(String condiFecha) {
			this.condiFecha = condiFecha;
			if(!("").equals(condiFecha)){
				rellenaMapaFiltro(CAMPO_FECHA, 0, condiFecha);
			}
		}
		public String getCondiRecibo() {
			return condiRecibo;
		}
		public void setCondiRecibo(String condiRecibo) {
			this.condiRecibo = condiRecibo;
			if(!("").equals(condiRecibo)){
				rellenaMapaFiltro(CAMPO_RECIBO, 0, condiRecibo);
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
		
		//********************************************************************//
		
		// GETTERS Y SETTERS PARA CHECKS
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
		public boolean isCheckFecha() {
			return checkFecha;
		}
		public void setCheckFecha(boolean checkFecha) {
			this.checkFecha = checkFecha;
			if(checkFecha){
				camposComunesMostrar.add(CAMPO_FECHA);
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
				camposResumenMostrar.add(CAMPO_RS_TOM);
			}
		}
		public boolean isCheckRecibo() {
			return checkRecibo;
		}
		public void setCheckRecibo(boolean checkRecibo) {
			this.checkRecibo = checkRecibo;
			if(checkRecibo){
				camposComunesMostrar.add(CAMPO_RECIBO);
			}
		}
		public boolean isCheckSaldoTom() {
			return checkSaldoTom;
		}
		public void setCheckSaldoTom(boolean checkSaldoTom) {
			this.checkSaldoTom = checkSaldoTom;
			if(checkSaldoTom){
				camposResumenMostrar.add(CAMPO_SALDO_TOM);
			}
		}
		public boolean isCheckCompTom() {
			return checkCompTom;
		}
		public void setCheckCompTom(boolean checkCompTom) {
			this.checkCompTom = checkCompTom;
			if(checkCompTom){
				camposResumenMostrar.add(CAMPO_COMP_TOM);
			}
		}
		public boolean isCheckCompImp() {
			return checkCompImp;
		}
		public void setCheckCompImp(boolean checkCompImp) {
			this.checkCompImp = checkCompImp;
			if(checkCompImp){
				camposResumenMostrar.add(CAMPO_COMP_IMP);
			}
		}
		public boolean isCheckPagoRecibo() {
			return checkPagoRecibo;
		}
		public void setCheckPagoRecibo(boolean checkPagoRecibo) {
			this.checkPagoRecibo = checkPagoRecibo;
			if(checkPagoRecibo){
				camposResumenMostrar.add(CAMPO_PAGO_RECIBO);
			}
		}
		public boolean isCheckLiquidoRecibo() {
			return checkLiquidoRecibo;
		}
		public void setCheckLiquidoRecibo(boolean checkLiquidoRecibo) {
			this.checkLiquidoRecibo = checkLiquidoRecibo;
			if(checkLiquidoRecibo){
				camposResumenMostrar.add(CAMPO_LIQUIDO_RECIBO);
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
		public boolean isCheckTipoRecibo() {
			return checkTipoRecibo;
		}
		public void setCheckTipoRecibo(boolean checkTipoRecibo) {
			this.checkTipoRecibo = checkTipoRecibo;
			if(checkTipoRecibo){
				camposDetalleMostrar.add(CAMPO_TIPO_RECIBO);
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
		public boolean isCheckApe1() {
			return checkApe1;
		}
		public void setCheckApe1(boolean checkApe1) {
			this.checkApe1 = checkApe1;
			if(checkApe1){
				camposDetalleMostrar.add(CAMPO_APE1);
			}
		}
		public boolean isCheckApe2() {
			return checkApe2;
		}
		public void setCheckApe2(boolean checkApe2) {
			this.checkApe2 = checkApe2;
			if(checkApe2){
				camposDetalleMostrar.add(CAMPO_APE2);
			}
		}
		public boolean isCheckNombre() {
			return checkNombre;
		}
		public void setCheckNombre(boolean checkNombre) {
			this.checkNombre = checkNombre;
			if(checkNombre){
				camposDetalleMostrar.add(CAMPO_NOMBRE);
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
		public boolean isCheckPrimaCom() {
			return checkPrimaCom;
		}
		public void setCheckPrimaCom(boolean checkPrimaCom) {
			this.checkPrimaCom = checkPrimaCom;
			if(checkPrimaCom){
				camposDetalleMostrar.add(CAMPO_PRIMA_COMERCIAL);
			}
		}
		public boolean isCheckPrimaNeta() {
			return checkPrimaNeta;
		}
		public void setCheckPrimaNeta(boolean checkPrimaNeta) {
			this.checkPrimaNeta = checkPrimaNeta;
			if(checkPrimaNeta){
				camposDetalleMostrar.add(CAMPO_PRIMA_NETA);
			}
		}
		public boolean isCheckCosteNeto() {
			return checkCosteNeto;
		}
		public void setCheckCosteNeto(boolean checkCosteNeto) {
			this.checkCosteNeto = checkCosteNeto;
			if(checkCosteNeto){
				camposDetalleMostrar.add(CAMPO_COSTE_NETO);
			}
		}
		public boolean isCheckCosteTom() {
			return checkCosteTom;
		}
		public void setCheckCosteTom(boolean checkCosteTom) {
			this.checkCosteTom = checkCosteTom;
			if(checkCosteTom){
				camposDetalleMostrar.add(CAMPO_COSTE_TOM);
			}
		}
		public boolean isCheckPagoPlz() {
			return checkPagoPlz;
		}
		public void setCheckPagoPlz(boolean checkPagoPlz) {
			this.checkPagoPlz = checkPagoPlz;
			if(checkPagoPlz){
				camposDetalleMostrar.add(CAMPO_PAGO_POLIZA);
			}
		}
		public boolean isCheckSaldo() {
			return checkSaldo;
		}
		public void setCheckSaldo(boolean checkSaldo) {
			this.checkSaldo = checkSaldo;
			if(checkSaldo){
				camposDetalleMostrar.add(CAMPO_SALDO_POLIZA);
			}
		}

		//********************************************************************//
		
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