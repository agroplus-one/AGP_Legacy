
/*
**************************************************************************************************
*
*  CReACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* 			              Marcos Del Rey  	js para la pantalla Grupo Asegurado.
* 
* Usage:
*     UTIL.cleanFields(parametro/s);
*
 **************************************************************************************************
*/



var grupoAsegurado = {
	 
	init:function(){    	
    },	
	consulta:function(){
		document.forms.main.operacion.value= 'consulta';
		document.forms.main.submit();
	},	
	edit:function(idRow){	     	
     	var frm = document.getElementById("main");
	    frm.row.value = idRow;
	    frm.operacion.value = "editar";
	    frm.submit();
    },  	
	remove:function(idRow){
	    var frm = document.getElementById("main");
	    frm.operacion.value = "baja";
	    frm.row.value = idRow;
	    frm.submit();
    },     
    save:function () {
    	UTIL.cleanErrors("panelErrores");
	    var frm = document.getElementById("main");
	    var error = "<label name='error' style='color:red;margin-left:5px;font-size:10px'>*</label>";
	    
	    if(frm.operacion.value = "alta")
	        //frm.operacion.value = "alta";
	    
	  var obligatorios = ["codgrupoaseg", "bonifrecprimas", "bonifrecrdtomax"];

	    if(this.isDataOk(obligatorios,error)){
	    	frm.submit();
	    }
	    else{
	        document.getElementById("panelErrores").style.display = "block";
	    }
    },        
    isDataOk:function(obligatorios,error){
		var result = true;  
		var aux = 0;
		     
		for(var i = 0; i < obligatorios.length;i++){
			var pCalculo = document.getElementById("txt_procesoCalculo");
	     	var sl = document.getElementById(obligatorios[i])
	     	if(sl.selectedIndex == 0 || sl.options.length == 0){
	    	    sl.parentNode.innerHTML = sl.parentNode.innerHTML + error;
	            result = false;           
	     	}
	     	else{
	     		if((UTIL.getValueOfSelect(sl)== "C")&& pCalculo.value == ""){
                    pCalculo.parentNode.innerHTML = pCalculo.parentNode.innerHTML + error;
	                result = false;
	     	     }
	     	}	     
	    }
	    return result;
	},
	modificar:function(){    	
	    var error = "<label name='error' style='color:red;margin-left:5px;font-size:10px'>*</label>";
    	var frm = document.getElementById("main");
    	var obligatorios = ["codgrupoaseg", "bonifrecprimas", "bonifrecrdtomax"];
    	var existeError = false;
    	
    	for(i = 0;i < obligatorios.lenght; i++){
    		var objeto = obligatorios[i];
    			if (objeto.value == ""){
    				objeto.parentNode.innerHTML = objeto.parentNode.innerHTML + error;
    				existeError = true;	
    		}
    	}
    	if (existeError==false){
    		frm.submit();
    	}    	  	
    }
}

