

var modComp = {
    dataSelect:function(){
	    document.forms.frmImportaciones.submit();
    },
    //TODO:terminar incluir las acciones de botones en JS
    
    cleanFields:function(){
    	UTIL.cleanErrors("panelErrores");
	    var combos = ["combo_planes", "combo_lineas", "combo_principal", "combo_riesgo", "combo_compl"];
        UTIL.cleanCombos(combos);
        var fields = ["txt_procesoCalculo"];
        UTIL.cleanTextboxs(fields);
        UTIL.cleanSelectAndAddEmptyOption(document.getElementById("combo_lineas"));
        UTIL.cleanSelectAndAddEmptyOption(document.getElementById("combo_principal"));
        UTIL.cleanSelect(document.getElementById("combo_compl"));    
    },
    
    guardaHidden:function (){
    	var plan = document.getElementById("combo_planes");
    	var lineas = document.getElementById("combo_lineas");
    	var principal = document.getElementById("combo_principal");
    	var riesgo = document.getElementById("combo_riesgo");
    	var compl = document.getElementById("combo_compl");
    	
    },
    
	consultar:function(){
        UTIL.cleanErrors("panelErrores");
        var frm = document.getElementById("main");
        frm.operacion.value = "consulta";
        frm.submit();
     },    
 //combos
 	cleanFields:function(){
    	UTIL.cleanErrors("panelErrores");
    	var combos = ["combo_planes","combo_lineas","combo_principal","combo_riesgo","combo_compl"];
        UTIL.cleanCombos(combos);
        UTIL.cleanSelectAddOneOptions(document.getElementById("sl_lineas"));
        
        var panelInformacionId = document.getElementById("panelInformacion");
        if(panelInformacionId){
        	panelInformacionId.style.display="none";
        }
        
    },   
    
    remove:function (id_hidden_linea,id_hidden_principal,id_hidden_riesgo,id_hidden_compl,id_hidden_gruposeguro) {
	    var frm = document.getElementById("main");
	    frm.hidden_linea.value = id_hidden_linea;
	    frm.hidden_principal.value = id_hidden_principal;
	    frm.hidden_riesgo.value = id_hidden_riesgo;
	    frm.hidden_compl.value = id_hidden_compl;
	    frm.hidden_gruposeguro.value = id_hidden_gruposeguro;
	    
	    frm.operacion.value = "baja";
	    frm.submit();
     },
     
     select:function(){
     	var frm = document.getElementById("main");
	    frm.operacion.value = "consulta";
	    frm.submit();
     	
     },    
     
    save:function () {
    	UTIL.cleanErrors("panelErrores");
	    var frm = document.getElementById("main");
	    var error = "<label name='error' style='color:red;margin-left:5px;font-size:10px'>*</label>";
	    
	    if(frm.operacion.value != "modificacion")
	        frm.operacion.value = "alta";
	    
	   // var combos = ["sl_planes", "sl_lineas", "sl_usos", "sl_ubicaciones","sl_camposSC"];

	    if(this.isDataOk(combos,error)){
	    	frm.submit();
	    }
	    else{
	        document.getElementById("panelErrores").style.display = "block";
	    }
    },
    
    selectLineas_onchange:function(){
    	var idSelect = document.getElementById("combo_principal");
    	var idSelect2 = document.getElementById("combo_compl");
    	UTIL.cleanSelect(idSelect);
    	UTIL.cleanSelect(idSelect2);
    	this.loadModulos_ajax();
    },    
    
    loadModulos_ajax:function(){
    	var idlinea   = UTIL.getValueOfSelect(document.getElementById("combo_lineas"));
       // var ubicacion = UTIL.getValueOfSelect(document.getElementById("sl_ubicaciones"));
        if (idlinea != "smsAviso")
        {
        $.ajax({
            url: "moduloCompatible.html",
            data: "operacion=ajax_getModulos&idLinea=" + idlinea,
            beforeSend: function(objeto){document.getElementById("ajaxLoading_modulos").style.display = 'block';},
            complete: function(objeto, exito){document.getElementById("ajaxLoading_modulos").style.display = 'none';},
            contentType: "application/x-www-form-urlencoded",
            dataType: "text",
            error: function(objeto, quepaso, otroobj){alert("Error: " + quepaso);},
            success: function(datos){
            var list = eval(datos);
              var sl = document.getElementById("combo_principal");
                var sl2 = document.getElementById("combo_compl");
                if(sl && list){
                	alert(list.length);
                    if(list.length > 0){
                		var opt = document.createElement('OPTION');
                        opt.innerHTML = " -- Seleccione un opción -- ";
                        opt.value = "smsAviso";
                        sl.appendChild(opt);
                        var opt2 = document.createElement('OPTION');
                        opt2.innerHTML = " -- Seleccione un opción -- ";
                        opt2.value = "smsAviso";
                        sl2.appendChild(opt2);
                        for(var i = 0; i < list.length; i++){
                            var opt = document.createElement('OPTION');
                            opt.innerHTML = list[i].nodeText;
                            opt.value = list[i].value;
                            if (list[i].ppalComp=='P') //combo principal
                            	sl.appendChild(opt);
                            else if (list[i].ppalComp=='C') //combo compatible
                       			sl2.appendChild(opt);	
                        }
                	}
                    else{
                    	 var opt = document.createElement('OPTION');
                    	 opt.innerHTML = " -- Sin opción seleccionable -- ";
                         opt.value = "smsAviso";
                         sl.appendChild(opt);
                    }
                }//if
            },
            type: "POST"
        });
        
               	
        }//fin if 
    },
    
    selectModulo_onchange:function(){
		var idModulo = document.getElementById("combo_riesgo");
		UTIL.cleanSelect(idModulo);
		this.loadRiesgo_ajax();
	 },  
    
    
    loadRiesgo_ajax:function()
    {
    	alert('riesgo');
    	var codModulo   = UTIL.getValueOfSelect(document.getElementById("combo_principal"));  
    	var lineaSeguroId   = UTIL.getValueOfSelect(document.getElementById("combo_lineas"));    	
    	//  if (idRiesgo != "smsAviso"){
    	  	$.ajax({
	            url: "moduloCompatible.html",
	            data: "operacion=ajax_getRiesgos&codModulo=" + codModulo+"&lineaSeguroId="+lineaSeguroId,
    	  		beforeSend: function(objeto){document.getElementById("ajaxLoading_riesgos").style.display = 'block';},
		        complete: function(objeto, exito){document.getElementById("ajaxLoading_riesgos").style.display = 'none';},
		        contentType: "application/x-www-form-urlencoded",
		        dataType: "text",
		        error: function(objeto, quepaso, otroobj){alert("Error: " + quepaso);},
		        success: function(datos){
		        	alert(datos);
    	  			var list = eval(datos);
    	
                var rs = document.getElementById("combo_riesgo");
                if(rs && list){
               	alert(list.length);
                    if(list.length > 0){
                		var opt = document.createElement('OPTION');
                        opt.innerHTML = " -- Seleccione un opción -- ";
                        opt.value = "smsAviso";
                        rs.appendChild(opt);   
                        for(var i = 0; i < list.length; i++){
	                            var opt = document.createElement('OPTION');
	                            opt.innerHTML = list[i].nodeText;
	                            opt.value = list[i].value;
	                            rs.appendChild(opt);
	                        	}
                        }
                        else{
	                    	 var opt = document.createElement('OPTION');
	                    	 opt.innerHTML = " -- Sin opción seleccionable -- ";
	                         opt.value = "smsAviso";
	                         rs.appendChild(opt);                                  
    	  				}
    	  			 }//if
    	  			 
		        },
    			type: "POST"
    	  	});
    	  	
    	//    }//fin if
    },
    
    
    isDataOk:function(combos,error){
		var result = true;  
		var aux = 0;
		     
		for(var i = 0; i < combos.length;i++){
			var pCalculo = document.getElementById("txt_procesoCalculo");
	     	var sl = document.getElementById(combos[i])
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
	    return result
	}   
	
}