/*
**************************************************************************************************
*
*  CReACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034              Miguel Granadino  js para la pantalla pantallas Configurables.
* 
* Usage:
*     UTIL.cleanFields(parametro/s);
*
 **************************************************************************************************
*/

var responseExistLinea = "";

var replicar = {
	   init:function(){
    	    this.clean();
    	    var valorIndiceCombo = ""+document.getElementById("sl_planes").value;
    	    //document.querySelector('#sl_planesOrigen [value="' + valorIndiceCombo + '"]').selected = true;
    	    $('#sl_planesOrigen').val(valorIndiceCombo);
    	    cargarSelecLineaOrigen(document.getElementById("sl_lineas").value);
        },
        clean:function(){
        	UTIL.cleanErrors("panelErroresModalWindow");
        	document.getElementById("panelErroresModalWindow").style.display = "none";
    	    var combos = ["sl_planesOrigen","sl_planesDestino"];
            UTIL.cleanCombos(combos);
            UTIL.cleanSelectAndAddEmptyOption(document.getElementById("sl_lineasOrigen"));
    	    UTIL.cleanSelectAndAddEmptyOption(document.getElementById("sl_lineasDestino"));
        },
        selectPlanOrigen_onchange:function(){
    	    UTIL.cleanSelectAndAddEmptyOption(document.getElementById("sl_lineasOrigen"));
    	    AJAX_RSI.getLineas("pantallasConfigurables.html","sl_planesOrigen","sl_lineasOrigen","ajaxLoading_lineasDestino");
        },
        
        selectPlanDestino_onchange:function(){
    	    UTIL.cleanSelectAndAddEmptyOption(document.getElementById("sl_lineasDestino"));
    	    AJAX_RSI.getLineas("pantallasConfigurables.html","sl_planesDestino","sl_lineasDestino","ajaxLoading_lineasDestino");
        },
        isDataOk:function(combos,error){
		    var result = true;               
		    for(var i = 0; i < combos.length;i++){
	     	    var sl = document.getElementById(combos[i])
	     	    if(sl.selectedIndex == 0 || sl.options.length == 0){
	    	        sl.parentNode.innerHTML = sl.parentNode.innerHTML + error;
	                result = false;           
	     	    }
	         }
	         return result
	    },
        replicar_onClick:function(){
        document.getElementById("sl_planes").style.display = "none";
        document.getElementById("sl_lineas").style.display = "none";
        document.getElementById("sl_pantallas").style.display = "none";
	    
	    var frm    = document.getElementById("main2");
	    var error  = "<label name='error' style='color:red;margin-left:5px;font-size:10px'>*</label>";
        var combos = ["sl_planesDestino","sl_lineasDestino","sl_planesOrigen","sl_lineasOrigen"];
        
        UTIL.cleanErrors("panelErroresModalWindow");
        
	    if(UTIL.isDataOk(combos,error)){
	    	this.existLinea("pantallasConfigurables.html","sl_planesDestino","sl_planesDestino","comprobandoPlanLinea");
	    	
	    	if(responseExistLinea == "true"){
	    		if(confirm('El plan/linea ya esta configurado, ¿Desea sobreescribirlo?')){
                    this.replicarLinea();
                    UTIL.closeModalWindow();
	    		}
	    	}
	    	else if(responseExistLinea == "noConfiguracion"){
	    		alert("El plan/linea seleccionado no tiene configuración, replicación imposible.");
	    	}
	    	else{
	    	    this.replicarLinea();
	    	    UTIL.closeModalWindow();   
	    	}
	    }
	    else {
	        document.getElementById("panelErroresModalWindow").style.display = "block";
	    }
     },
     existLinea:function(url,plan,linea,loadingImg){
    	var planDestino   = UTIL.getValueOfSelect(document.getElementById("sl_planesDestino"));
     	var lineaDestino  = UTIL.getValueOfSelect(document.getElementById("sl_lineasDestino"));
     	var planOrigen    = UTIL.getValueOfSelect(document.getElementById("sl_planesOrigen"));
     	var lineaOrigen   = UTIL.getValueOfSelect(document.getElementById("sl_lineasOrigen")); 
     	
       $.ajax({
          url: "pantallasConfigurables.html",
          global: false,
          type: "POST",
          data: "operacion=ajax_existPlanLinea&PlanOrigen=" + planOrigen + "&LineaOrigen=" + lineaOrigen + "&PlanDestino=" + planDestino + "&LineaDestino=" + lineaDestino,
          dataType: "html",
          async:false,
          success: function(jsondata){
              aux = eval(jsondata);
          	  responseExistLinea = aux[0].result;
          }
       }).responseText;
    },
    replicarLinea:function(){
    	var planOrigen     = UTIL.getValueOfSelect(document.getElementById("sl_planesOrigen"));
    	var idLineaOrigen  = UTIL.getValueOfSelect(document.getElementById("sl_lineasOrigen"));
    	var planDestino    = UTIL.getValueOfSelect(document.getElementById("sl_planesDestino"));
    	var idLineaDestino = UTIL.getValueOfSelect(document.getElementById("sl_lineasDestino"));
    	var loadingImg     = "comprobandoPlanLinea";
    
    	$.ajax({
            url: "pantallasConfigurables.html",
            data: "operacion=ajax_replicar&PlanOrigen=" + planOrigen  + "&idLineaOrigen=" + idLineaOrigen + "&PlanDestino=" + planDestino + "&idLineaDestino=" + idLineaDestino,
            async:true,
            beforeSend: function(objeto){
            	var img = document.getElementById(loadingImg)
            	if(img)
                    document.getElementById(loadingImg).style.display = 'block';
            },
            complete: function(objeto, exito){
                var img = document.getElementById(loadingImg)
            	if(img)
                    document.getElementById(loadingImg).style.display = 'none';
            },
            contentType: "application/x-www-form-urlencoded",
            dataType: "text",
            error: function(objeto, quepaso, otroobj){
                alert("Error: por favor vuelva a intentarlo.");
            },
            global: true,
            ifModified: false,
            processData:true,
            success: function(datos){
            	// Submit de pantallasConfigurables.jsp para que se actualice el grid
            	
            	document.getElementById("operacion").value='consulta';
            	document.getElementById("replicarOK").value='true';
            	document.getElementById("main").submit();
            },
            type: "GET"
        });
    }
}

/*--------------------------------
 *  
 *--------------------------------*/
var pantallasConfig = {
    init:function(){

    },
	cleanFields:function(){
    	UTIL.cleanErrors("panelErrores");
    	var combos = ["sl_planes","sl_pantallas"];
        UTIL.cleanCombos(combos);
        UTIL.cleanSelectAddOneOptions(document.getElementById("sl_lineas"));
        
        var panelInformacionId = document.getElementById("panelInformacion");
        if(panelInformacionId){
        	panelInformacionId.style.display="none";
        }
        
    },
    remove:function(idRow){
    		if (confirm('¿Seguro que desea borrar este registro?')) {
			    var frm = document.getElementById("main");
			    frm.operacion.value = "baja";
			    frm.ROW.value = idRow;
			    frm.submit();
    		}
    },  
    save:function(){
    	
    	UTIL.cleanErrors("panelErrores");
	    var frm    = document.getElementById("main");
	    var error  = "<label name='error' style='color:red;margin-left:5px;font-size:10px'>*</label>";
	    var combos = ["sl_planes","sl_lineas","sl_pantallas"];
        
	    if(UTIL.isDataOk(combos,error)){
	        frm.operacion.value = "alta";
	    	frm.submit();
	    }
	    else
	        document.getElementById("panelErrores").style.display = "block";
    },  
    edit:function(idRow){
     	UTIL.cleanErrors("panelErrores");
     	var frm = document.getElementById("main");
	    frm.ROW.value = idRow;
	    frm.operacion.value = "editar";
	    frm.submit();
    },  
    modificar:function(){
    	var frm = document.getElementById("main");
    	
    	if(frm.idRowModificar.value != ""){
    	    UTIL.cleanErrors("panelErrores");
            var idRowModificar = frm.idRowModificar.value;
            var error  = "<label name='error' style='color:red;margin-left:5px;font-size:10px'>*</label>";
	        var combos = ["sl_planes","sl_lineas","sl_pantallas"];
	    
            if(UTIL.isDataOk(combos,error)){
        	    frm.operacion.value = "modificacion";
                frm.submit();
            }
            else {
	            document.getElementById("panelErrores").style.display = "block";
            }
    	}
        else {
        	alert("Debe seleccionar el registro a modificar");
        	 
    	} 
    },
    replicar:function(codLinea, codPlan){
        replicar.init();       
        document.getElementById("sl_planes").style.display = "none";
		document.getElementById("sl_lineas").style.display = "none";
		document.getElementById("sl_pantallas").style.display = "none";
    	UTIL.openModalWindow();
    },
	tallerConfiguracion:function(idRow){
		 UTIL.cleanErrors("panelErrores");
		 var frm = document.getElementById("main");
		 frm.ROW.value = idRow;
		 frm.operacion.value = "redirect_tallerPantallasConfigurables";
		 frm.submit();
	},
	consultar:function(){
        UTIL.cleanErrors("panelErrores");
        var frm = document.getElementById("main");
        frm.operacion.value = "consulta";
        frm.submit();
     },
     selectPlan_onchange:function(){
     	 UTIL.cleanSelectAndAddEmptyOption(document.getElementById("sl_lineas"));
         AJAX_RSI.getLineas("pantallasConfigurables.html","sl_planes","sl_lineas","ajaxLoading_lineas");
     }
}
