package com.rsi.agp.dao.tables.comisiones;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.rsi.agp.core.util.ConstantsInf;

public class InformeRecibos2015 implements Serializable{
		
		private static final long serialVersionUID = -2323212192240826924L;
		
		//CONSTANTES PARA LOS NOMBRES DE LOS CAMPOS DE LA TABLA "TB_COMS_INFORMES_RECIBOS_2015"
		private static final String CAMPO_FASE = "FASE";
		private static final String CAMPO_ENTIDAD = "CODENTIDAD";
		private static final String CAMPO_OFICINA = "CODOFICINA";
		private static final String CAMPO_ENTIDAD_MED = "CODENTMED";
		private static final String CAMPO_SUBENT = "CODSUBENTMED";
		private static final String CAMPO_PLAN = "CODPLAN";
		private static final String CAMPO_LINEA = "CODLINEA";
		private static final String CAMPO_FECHA_EMISION = "FECHA_EMISION";
		private static final String CAMPO_REF_COLECTIVO = "REF_COLECTIVO";
		private static final String CAMPO_RS_TOMADOR = "RS_TOMADOR";
		private static final String CAMPO_RECIBO = "NUM_RECIBO";
		private static final String CAMPO_REF_POLIZA = "REF_POLIZA";
		private static final String CAMPO_TIPO_RECIBO = "TIPO_RECIBO";
		private static final String CAMPO_NIF_ASEG = "NIF_ASEGURADO";
		private static final String CAMPO_APE1 = "AP1_ASEG";
		private static final String CAMPO_APE2 = "AP2_ASEG";
		private static final String CAMPO_NOMBRE = "NOM_ASEG";
		private static final String CAMPO_RS_ASEG = "RAZONSOCIAL_ASEG";
		private static final String CAMPO_PRIMA_COMERCIAL = "PRIMA_COMERCIAL";
		private static final String CAMPO_PRIMA_NETA = "PRIMA_NETA";
		private static final String CAMPO_RECARGO_CONSORCIO = "RECARGO_CONSORCIO";
		private static final String CAMPO_RECIBO_PRIMA = "RECIBO_PRIMA";
		private static final String CAMPO_SUBV_ENESA = "SUBV_ENESA";
		private static final String CAMPO_COSTE_TOM = "COSTE_TOMADOR";
		private static final String CAMPO_COSTE_TOM_TOTAL = "TOTAL_COSTE_TOMADOR";
		private static final String CAMPO_PAGOS = "PAGOS";
		private static final String CAMPO_DIFERENCIA = "DIFERENCIA";
		private static final String CAMPO_IMP_RECARGO_AVAL = "IMP_RECARGO_AVAL";
		private static final String CAMPO_IMP_RECARGO_FRAC = "IMP_RECARGO_FRACC";
		private static final String CAMPO_BONIF = "BONIFICACIONES";
		private static final String CAMPO_SUBV_CCAA = "SUBV_CCAA";
		private static final String CAMPO_RECARGOS = "RECARGOS";
		
		private static final String CAMPO_PAGO_DOMICILIADO = "DOMICILIADO";
		private static final String CAMPO_DESTINATARIO = "DEST_DOMICILIACION";
		private static final String CAMPO_IMP_DOMICILIAR = "IMP_DOMICILIADO";
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
		private String fechaEmision;             
		private String recibo;                            
		private String refPoliza;              
		private String nifAseg;
		
		//********************************************************************//
		
		//PROPIEDADES PARA LOS SELECT DE CONDICIONES
		private String condiFase;
		private String condiRefColectivo;
		private String condiLinea;
		private String condiPlan; 
		private String condiEntidad;
		private String condiOficina;
		private String condiEntidadMed;        
		private String condiSubent;           
		private String condiFechaEmision;             
		private String condiRecibo;                            
		private String condiRefPoliza;              
		private String condiNifAseg;
		
		//********************************************************************//
		
		//PROPIEDADES PARA LOS CHECKS
		private boolean checkFase;
		private boolean checkEntidad;
		private boolean checkOficina;
		private boolean checkEntidadMed;
		
		private boolean checkSubent;      
		private boolean checkPlan;                 
		private boolean checkLinea;  
		private boolean checkFechaEmision; 
		
		private boolean checkRefColectivo;  
		private boolean checkRsTomador;  
		private boolean checkRecibo;    
		private boolean checkRefPoliza; 
		
		private boolean checkTipoRecibo; 
		private boolean checkNifAseg; 
		private boolean checkApe1; 
		private boolean checkApe2; 
		
		private boolean checkNombre; 
		private boolean checkRsAseg;
		private boolean checkPrimaComercial; 
		private boolean checkPrimaNeta;
		
		private boolean checkRecargoCons;
		private boolean checkReciboPrima;
		private boolean checkSubvEnesa;
		private boolean checkCosteTomador;
		
		private boolean checkCosteTomTotal;
		private boolean checkPagos;
		private boolean checkDiferencia;
		private boolean checkImpRecargoAval;
		
		private boolean checkImpRecargoFrac;
		private boolean checkBonif;
		private boolean checkSubvCcaa;
		private boolean checkRecargos;
		
		private boolean checkPagoDomiciliado;
		private boolean checkDestinatario;
		private boolean checkImpDomiciliar;

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
		public String getRefColectivo() {
			return refColectivo;
		}
		public void setRefColectivo(String refColectivo) {
			this.refColectivo = refColectivo;
			if(!("").equals(refColectivo)){
				rellenaMapaFiltro(CAMPO_REF_COLECTIVO, 1, refColectivo);
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
		public String getPlan() {
			return plan;
		}
		public void setPlan(String plan) {
			this.plan = plan;
			if(!("").equals(plan)){
				rellenaMapaFiltro(CAMPO_PLAN, 1, plan);
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
		public String getFechaEmision() {
			return fechaEmision;
		}
		public void setFechaEmision(String fechaEmision) {
			this.fechaEmision = fechaEmision;
			if(!("").equals(fechaEmision)){
				rellenaMapaFiltro(CAMPO_FECHA_EMISION, 1, fechaEmision);
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
		public String getRefPoliza() {
			return refPoliza;
		}
		public void setRefPoliza(String refPoliza) {
			this.refPoliza = refPoliza;
			if(!("").equals(refPoliza)){
				rellenaMapaFiltro(CAMPO_REF_POLIZA, 1, refPoliza);
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
		public String getCondiRefColectivo() {
			return condiRefColectivo;
		}
		public void setCondiRefColectivo(String condiRefColectivo) {
			this.condiRefColectivo = condiRefColectivo;
			if(!("").equals(condiRefColectivo)){
				rellenaMapaFiltro(CAMPO_REF_COLECTIVO, 0, condiRefColectivo);
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
		public String getCondiPlan() {
			return condiPlan;
		}
		public void setCondiPlan(String condiPlan) {
			this.condiPlan = condiPlan;
			if(!("").equals(condiPlan)){
				rellenaMapaFiltro(CAMPO_PLAN, 0, condiPlan);
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
		public String getCondiFechaEmision() {
			return condiFechaEmision;
		}
		public void setCondiFechaEmision(String condiFechaEmision) {
			this.condiFechaEmision = condiFechaEmision;
			if(!("").equals(condiFechaEmision)){
				rellenaMapaFiltro(CAMPO_FECHA_EMISION, 0, condiFechaEmision);
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
		public String getCondiRefPoliza() {
			return condiRefPoliza;
		}
		public void setCondiRefPoliza(String condiRefPoliza) {
			this.condiRefPoliza = condiRefPoliza;
			if(!("").equals(condiRefPoliza)){
				rellenaMapaFiltro(CAMPO_REF_POLIZA, 0, condiRefPoliza);
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
		public boolean isCheckFechaEmision() {
			return checkFechaEmision;
		}
		public void setCheckFechaEmision(boolean checkFechaEmision) {
			this.checkFechaEmision = checkFechaEmision;
			if(checkFechaEmision){
				camposComunesMostrar.add(CAMPO_FECHA_EMISION);
			}
		}
		public boolean isCheckRefColectivo() {
			return checkRefColectivo;
		}
		public void setCheckRefColectivo(boolean checkRefColectivo) {
			this.checkRefColectivo = checkRefColectivo;
			if(checkRefColectivo){
				camposComunesMostrar.add(CAMPO_REF_COLECTIVO);
			}
		}
		public boolean isCheckRsTomador() {
			return checkRsTomador;
		}
		public void setCheckRsTomador(boolean checkRsTomador) {
			this.checkRsTomador = checkRsTomador;
			if(checkRsTomador){
				camposComunesMostrar.add(CAMPO_RS_TOMADOR);
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
		public boolean isCheckRefPoliza() {
			return checkRefPoliza;
		}
		public void setCheckRefPoliza(boolean checkRefPoliza) {
			this.checkRefPoliza = checkRefPoliza;
			if(checkRefPoliza){
				camposComunesMostrar.add(CAMPO_REF_POLIZA);
			}
		}
		
		public boolean isCheckTipoRecibo() {
			return checkTipoRecibo;
		}
		public void setCheckTipoRecibo(boolean checkTipoRecibo) {
			this.checkTipoRecibo = checkTipoRecibo;
			if(checkTipoRecibo){
				camposComunesMostrar.add(CAMPO_TIPO_RECIBO);
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
		public boolean isCheckApe1() {
			return checkApe1;
		}
		public void setCheckApe1(boolean checkApe1) {
			this.checkApe1 = checkApe1;
			if(checkApe1){
				camposComunesMostrar.add(CAMPO_APE1);
			}
		}
		public boolean isCheckApe2() {
			return checkApe2;
		}
		public void setCheckApe2(boolean checkApe2) {
			this.checkApe2 = checkApe2;
			if(checkApe2){
				camposComunesMostrar.add(CAMPO_APE2);
			}
		}
		public boolean isCheckNombre() {
			return checkNombre;
		}
		public void setCheckNombre(boolean checkNombre) {
			this.checkNombre = checkNombre;
			if(checkNombre){
				camposComunesMostrar.add(CAMPO_NOMBRE);
			}
		}
		public boolean isCheckRsAseg() {
			return checkRsAseg;
		}
		public void setCheckRsAseg(boolean checkRsAseg) {
			this.checkRsAseg = checkRsAseg;
			if(checkRsAseg){
				camposComunesMostrar.add(CAMPO_RS_ASEG);
			}
			
		}
		public boolean isCheckPrimaComercial() {
			return checkPrimaComercial;
		}
		public void setCheckPrimaComercial(boolean checkPrimaComercial) {
			this.checkPrimaComercial = checkPrimaComercial;
			if(checkPrimaComercial){
				camposComunesMostrar.add(CAMPO_PRIMA_COMERCIAL);
			}
		}
		public boolean isCheckPrimaNeta() {
			return checkPrimaNeta;
		}
		public void setCheckPrimaNeta(boolean checkPrimaNeta) {
			this.checkPrimaNeta = checkPrimaNeta;
			if(checkPrimaNeta){
				camposComunesMostrar.add(CAMPO_PRIMA_NETA);
			}
		}
		//21
		public boolean isCheckRecargoCons() {
			return checkRecargoCons;
		}
		public void setCheckRecargoCons(boolean checkRecargoCons) {
			this.checkRecargoCons = checkRecargoCons;
			if(checkRecargoCons){
				camposComunesMostrar.add(CAMPO_RECARGO_CONSORCIO);
			}
		}
		//22
		public boolean isCheckReciboPrima() {
			return checkReciboPrima;
		}
		public void setCheckReciboPrima(boolean checkReciboPrima) {
			this.checkReciboPrima = checkReciboPrima;
			if(checkReciboPrima){
				camposComunesMostrar.add(CAMPO_RECIBO_PRIMA);
			}
		}
		//23
		public boolean isCheckSubvEnesa() {
			return checkSubvEnesa;
		}
		public void setCheckSubvEnesa(boolean checkSubvEnesa) {
			this.checkSubvEnesa = checkSubvEnesa;
			if(checkSubvEnesa){
				camposComunesMostrar.add(CAMPO_SUBV_ENESA);
			}
		}
		//24
		public boolean isCheckCosteTomador() {
			return checkCosteTomador;
		}
		public void setCheckCosteTomador(boolean checkCosteTomador) {
			this.checkCosteTomador = checkCosteTomador;
			if(checkCosteTomador){
				camposComunesMostrar.add(CAMPO_COSTE_TOM);
			}
		}
		//25
		public boolean isCheckCosteTomTotal() {
			return checkCosteTomTotal;
		}
		public void setCheckCosteTomTotal(boolean checkCosteTomTotal) {
			this.checkCosteTomTotal = checkCosteTomTotal;
			if(checkCosteTomTotal){
				camposComunesMostrar.add(CAMPO_COSTE_TOM_TOTAL);
			}
		}
		//26
		public boolean isCheckPagos() {
			return checkPagos;
		}
		public void setCheckPagos(boolean checkPagos) {
			this.checkPagos = checkPagos;
			if(checkPagos){
				camposComunesMostrar.add(CAMPO_PAGOS);
			}
		}
		//27
		public boolean isCheckDiferencia() {
			return checkDiferencia;
		}
		public void setCheckDiferencia(boolean checkDiferencia) {
			this.checkDiferencia = checkDiferencia;
			if(checkDiferencia){
				camposComunesMostrar.add(CAMPO_DIFERENCIA);
			}
		}
		//28
		public boolean isCheckImpRecargoAval() {
			return checkImpRecargoAval;
		}
		public void setCheckImpRecargoAval(boolean checkImpRecargoAval) {
			this.checkImpRecargoAval = checkImpRecargoAval;
			if(checkImpRecargoAval){
				camposComunesMostrar.add(CAMPO_IMP_RECARGO_AVAL);
			}
		}
		//29
		public boolean isCheckImpRecargoFrac() {
			return checkImpRecargoFrac;
		}
		public void setCheckImpRecargoFrac(boolean checkImpRecargoFrac) {
			this.checkImpRecargoFrac = checkImpRecargoFrac;
			if(checkImpRecargoFrac){
				camposComunesMostrar.add(CAMPO_IMP_RECARGO_FRAC);
			}
		}
		//30
		public boolean isCheckBonif() {
			return checkBonif;
		}
		public void setCheckBonif(boolean checkBonif) {
			this.checkBonif = checkBonif;
			if(checkBonif){
				camposComunesMostrar.add(CAMPO_BONIF);
			}
		}
		//31
		public boolean isCheckSubvCcaa() {
			return checkSubvCcaa;
		}
		public void setCheckSubvCcaa(boolean checkSubvCcaa) {
			this.checkSubvCcaa = checkSubvCcaa;
			if(checkSubvCcaa){
				camposComunesMostrar.add(CAMPO_SUBV_CCAA);
			}
		}
		//32
		public boolean isCheckRecargos() {
			return checkRecargos;
		}
		public void setCheckRecargos(boolean checkRecargos) {
			this.checkRecargos = checkRecargos;
			if(checkRecargos){
				camposComunesMostrar.add(CAMPO_RECARGOS);
			}
		}
		//33
		public boolean isCheckPagoDomiciliado() {
			return checkPagoDomiciliado;
		}
		public void setCheckPagoDomiciliado(boolean checkPagoDomiciliado) {
			this.checkPagoDomiciliado = checkPagoDomiciliado;
			if(checkPagoDomiciliado){
				camposComunesMostrar.add(CAMPO_PAGO_DOMICILIADO);
			}
		}
		//34
		public boolean isCheckDestinatario() {
			return checkDestinatario;
		}
		public void setCheckDestinatario(boolean checkDestinatario) {
			this.checkDestinatario = checkDestinatario;
			if(checkDestinatario){
				camposComunesMostrar.add(CAMPO_DESTINATARIO);
			}
		}
		//34
		public boolean isCheckImpDomiciliar() {
			return checkImpDomiciliar;
		}
		public void setCheckImpDomiciliar(boolean checkImpDomiciliar) {
			this.checkImpDomiciliar = checkImpDomiciliar;
			if(checkImpDomiciliar){
				camposComunesMostrar.add(CAMPO_IMP_DOMICILIAR);
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