package com.rsi.agp.dao.tables.mtoinf;

import java.math.BigDecimal;

public class DatoInformesItem implements java.io.Serializable {

	

    private Long id;
    private Informe informe;
    private CamposCalculados camposCalculados;
    private CamposPermitidos camposPermitidos;
    private BigDecimal orden;


 
	
   public DatoInformesItem(Long id, Informe informe, BigDecimal orden) {
       this.id = id;
       this.informe = informe;
       this.orden = orden;
   }
   public DatoInformesItem(Long id, Informe informe, CamposCalculados camposCalculados, CamposPermitidos camposPermitidos, BigDecimal orden) {
      this.id = id;
      this.informe = informe;
      this.camposCalculados = camposCalculados;
      this.camposPermitidos = camposPermitidos;
      this.orden = orden;
   }
	
  
   public DatoInformesItem(DatoInformes datoInformes) {
	      this.id = datoInformes.getId();
	      this.informe = datoInformes.getInforme();
	      
	      if (datoInformes.getCamposCalculados() == null)
	    	  camposCalculados = new CamposCalculados();
	      else
	   	   this.camposCalculados = datoInformes.getCamposCalculados();
	      
	      if (datoInformes.getCamposPermitidos() == null)
	    	  camposPermitidos = new CamposPermitidos();
	      else
	   	   this.camposPermitidos = datoInformes.getCamposPermitidos();
	      
	     
	      this.orden = datoInformes.getOrden();
	   }
   
   public Long getId() {
       return this.id;
   }
   
   public void setId(Long id) {
       this.id = id;
   }
   public Informe getInforme() {
       return this.informe;
   }
   
   public void setInforme(Informe informe) {
       this.informe = informe;
   }
   public CamposCalculados getCamposCalculados() {
       return this.camposCalculados;
   }
   
   public void setCamposCalculados(CamposCalculados camposCalculados) {
       this.camposCalculados = camposCalculados;
   }
   public CamposPermitidos getCamposPermitidos() {
       return this.camposPermitidos;
   }
   
   public void setCamposPermitidos(CamposPermitidos camposPermitidos) {
       this.camposPermitidos = camposPermitidos;
   }
   public BigDecimal getOrden() {
       return this.orden;
   }
   
   public void setOrden(BigDecimal orden) {
       this.orden = orden;
   }


	
	
	
}
