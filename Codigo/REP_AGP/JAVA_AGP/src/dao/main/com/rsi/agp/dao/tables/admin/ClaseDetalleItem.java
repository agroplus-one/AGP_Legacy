package com.rsi.agp.dao.tables.admin;

import java.math.BigDecimal;

import com.rsi.agp.dao.tables.cgen.CicloCultivo;
import com.rsi.agp.dao.tables.cgen.SistemaCultivo;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.cgen.TipoPlantacion;
import com.rsi.agp.dao.tables.cpl.Cultivo;
import com.rsi.agp.dao.tables.cpl.Variedad;

public class ClaseDetalleItem implements java.io.Serializable{

    private Long id;
    private Clase clase;
    private TipoPlantacion tipoPlantacion;
    private SistemaCultivo sistemaCultivo;
    private Cultivo cultivo;
    private Variedad variedad;
    private TipoCapital tipoCapital;
    private CicloCultivo cicloCultivo;
    private BigDecimal codprovincia;
    private BigDecimal codcomarca;
    private BigDecimal codtermino;
    private Character subtermino;
    private String codmodulo;

   public ClaseDetalleItem() {
   	clase = new Clase();
   	cultivo = new Cultivo();
   	sistemaCultivo = new SistemaCultivo();
   	cicloCultivo = new CicloCultivo();
   	variedad = new Variedad();
   	tipoCapital = new TipoCapital();
   	tipoPlantacion = new TipoPlantacion();
   }

   public ClaseDetalleItem(ClaseDetalle cl) {
      this.id = cl.getId();
      this.clase = cl.getClase();
      if (cl.getTipoPlantacion() == null)
    	  this.tipoPlantacion = new TipoPlantacion();
      else
    	  this.tipoPlantacion = cl.getTipoPlantacion();
      if (cl.getSistemaCultivo() == null)
    	  this.sistemaCultivo = new SistemaCultivo();
      else
    	  this.sistemaCultivo = cl.getSistemaCultivo();
      if (cl.getCicloCultivo() == null)
    	  this.cicloCultivo = new CicloCultivo();
      else
    	  this.cicloCultivo = cl.getCicloCultivo();
      this.cultivo = cl.getCultivo();
      this.variedad = cl.getVariedad();
      if (cl.getTipoCapital() == null)
    	  this.tipoCapital = new TipoCapital();
      else
    	  this.tipoCapital = cl.getTipoCapital();
      this.codprovincia = cl.getCodprovincia();
      this.codcomarca = cl.getCodcomarca();
      this.codtermino = cl.getCodtermino();
      this.subtermino = cl.getSubtermino();
      this.codmodulo = cl.getCodmodulo();
   }
  
   public Long getId() {
       return this.id;
   }
   
   public void setId(Long id) {
       this.id = id;
   }
   public Clase getClase() {
       return this.clase;
   }
   
   public void setClase(Clase clase) {
       this.clase = clase;
   }
   public TipoPlantacion getTipoPlantacion() {
       return this.tipoPlantacion;
   }
   
   public void setTipoPlantacion(TipoPlantacion tipoPlantacion) {
       this.tipoPlantacion = tipoPlantacion;
   }
   public SistemaCultivo getSistemaCultivo() {
       return this.sistemaCultivo;
   }
   
   public void setSistemaCultivo(SistemaCultivo sistemaCultivo) {
       this.sistemaCultivo = sistemaCultivo;
   }
   public Cultivo getCultivo() {
       return this.cultivo;
   }
   
   public void setCultivo(Cultivo cultivo) {
       this.cultivo = cultivo;
   }
   public Variedad getVariedad() {
       return this.variedad;
   }
   
   public void setVariedad(Variedad variedad) {
       this.variedad = variedad;
   }
   public TipoCapital getTipoCapital() {
       return this.tipoCapital;
   }
   
   public void setTipoCapital(TipoCapital tipoCapital) {
       this.tipoCapital = tipoCapital;
   }
   public CicloCultivo getCicloCultivo() {
       return this.cicloCultivo;
   }
   
   public void setCicloCultivo(CicloCultivo cicloCultivo) {
       this.cicloCultivo = cicloCultivo;
   }
   public BigDecimal getCodprovincia() {
       return this.codprovincia;
   }
   
   public void setCodprovincia(BigDecimal codprovincia) {
       this.codprovincia = codprovincia;
   }
   public BigDecimal getCodcomarca() {
       return this.codcomarca;
   }
   
   public void setCodcomarca(BigDecimal codcomarca) {
       this.codcomarca = codcomarca;
   }
   public BigDecimal getCodtermino() {
       return this.codtermino;
   }
   
   public void setCodtermino(BigDecimal codtermino) {
       this.codtermino = codtermino;
   }
   public Character getSubtermino() {
       return this.subtermino;
   }
   
   public void setSubtermino(Character subtermino) {
       this.subtermino = subtermino;
   }
   public String getCodmodulo() {
       return this.codmodulo;
   }
   
   public void setCodmodulo(String codmodulo) {
       this.codmodulo = codmodulo;
   }
	
}
