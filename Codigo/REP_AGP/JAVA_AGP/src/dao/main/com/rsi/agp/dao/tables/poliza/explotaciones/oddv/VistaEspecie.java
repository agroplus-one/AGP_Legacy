package com.rsi.agp.dao.tables.poliza.explotaciones.oddv;




public class VistaEspecie  implements java.io.Serializable {


     private VistaEspecieId id;

    public VistaEspecie() {
    	this.id=new VistaEspecieId();
    }
    
    public VistaEspecie(Object[] o2) {
    	VistaEspecieId v2=new VistaEspecieId(new Integer (o2[0].toString()),  o2[1].toString(), 
        		null, null, null,null, null, null);
    	setId(v2);
    }
    

    public VistaEspecie(VistaEspecieId id) {
       this.id = id;
    }
   
    public VistaEspecieId getId() {
        return this.id;
    }
    
    public void setId(VistaEspecieId id) {
        this.id = id;
    }




}


