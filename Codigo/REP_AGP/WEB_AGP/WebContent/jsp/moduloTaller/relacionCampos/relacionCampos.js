/*
**************************************************************************************************
*
*  CReACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034              Miguel Granadino  js para la pantalla relacion campos.
*
 **************************************************************************************************
*/
var relCamp = {
    dataSelect:function(){
	    document.forms.frmImportaciones.submit();
    },
    cleanFields:function(){
    	UTIL.cleanErrors("panelErrores");
	    var combos = ["sl_planes", "sl_usos", "sl_ubicaciones", "sl_tipoCampo", "sl_factores"];
        UTIL.cleanCombos(combos);
        var fields = ["txt_procesoCalculo"];
        UTIL.cleanTextboxs(fields);
        UTIL.cleanSelectAndAddEmptyOption(document.getElementById("sl_lineas"));
        UTIL.cleanSelectAndAddEmptyOption(document.getElementById("sl_camposSC"));
        UTIL.cleanSelect(document.getElementById("sl_grupoFactores"));
    },
    consultar:function(){
         UTIL.cleanErrors("panelErrores");
         var frm = document.getElementById("main");
         frm.operacion.value = "consulta";
         frm.submit();
     },
     edit:function(idRow){
     	UTIL.cleanErrors("panelErrores");
     	var frm = document.getElementById("main");
	    frm.ROW.value = idRow;
	    frm.operacion.value = "editar";
	    frm.submit();
     },
     edit2:function (idRow, codUso, codUbicacion, codConcepto, nomConcepto, factor, tipoCampo , procesoCalculo){
     	UTIL.cleanErrors("panelErrores");
        var frm = document.getElementById("main");
	    var sl = document.getElementById("sl_camposSC");
	    
	    if(sl){
	    	UTIL.cleanSelect(sl);
	        var opt = document.createElement('OPTION');
            opt.innerHTML = nomConcepto;
            opt.value = codConcepto;
            sl.appendChild(opt);
	    }
        
        frm.operacion.value          = "modificacion";
        frm.ROW.value                = idRow;
	    frm.sl_usos.value            = codUso;
	    frm.sl_ubicaciones.value     = codUbicacion;                
	    frm.sl_factores.value        = factor;
	    frm.sl_tipoCampo.value       = tipoCampo;
	    frm.txt_procesoCalculo.value = procesoCalculo;
    },
    modificar:function(){
    	
	    var error = "<label name='error' style='color:red;margin-left:5px;font-size:10px'>*</label>";
    	var frm = document.getElementById("main");
    	
    	if(frm.idRowRelacionCampos.value != ""){
    	    UTIL.cleanErrors("panelErrores");
            var idRowModificar = frm.idRowRelacionCampos.value;
            var error  = "<label name='error' style='color:red;margin-left:5px;font-size:10px'>*</label>";
	        var combos = ["sl_planes", "sl_usos", "sl_ubicaciones","sl_tipoCampo"];
	    
            if(this.isDataOk(combos,error)){
        	    frm.operacion.value = "modificacion";
                frm.submit();
            }
            else {
	            document.getElementById("panelErrores").style.display = "block";
            }
    	}
        else {
	            document.getElementById("panelErrores").style.display = "block";
        	 
    	} 
    },
    remove:function (idRow) {
		if (confirm("¿Está seguro de que desea eliminar el registro seleccionado?"))
		{    	
		    var frm = document.getElementById("main");
		    frm.ROW.value = idRow;
		    frm.operacion.value = "baja";
		    frm.submit();
		}
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
	    
	    var combos = ["sl_planes", "sl_lineas", "sl_usos", "sl_ubicaciones","sl_camposSC"];

	    if(this.isDataOk(combos,error)){
	    	frm.submit();
	    }
	    else{
	        document.getElementById("panelErrores").style.display = "block";
	    }
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
	},
    
    deleteRow:function(row){
        var index = row.parentNode.parentNode.rowIndex;
        var t = 'gridView';
        UTIL.deleteRow(t,index);
    },
    
    viewDataRow:function(div){
         var htmlTags = ["sl_usos","sl_ubicaciones","sl_camposSC","sl_factores",
                        "sl_tipoCampo","txt_procesoCalculo"];
         UTIL.viewDataRow('gridView',div,htmlTags);
    },
    
    selectUbicaciones_onchange:function(){
    	var idSelect = document.getElementById("sl_camposSC");
    	UTIL.cleanSelect(idSelect);
    	this.loadCamposSC_ajax();
    },
    
    /*selectCampoSC_onchange:function(){
    	this.loadGruposFactores_ajax();
    },*/
    
    selectFactores_onchange:function(){
    	this.loadFactoresPorGrupo_ajax();
    },
    selectTipoCampo:function(){
    	var idTipoCampo = UTIL.getValueOfSelect(document.getElementById("sl_tipoCampo"));
    	
    	if (idTipoCampo == 'NC')
    	{
    		document.getElementById("txt_procesoCalculo").value = '';
    		document.getElementById("txt_procesoCalculo").disabled = true;    		
    	}
    	else if(idTipoCampo == 'C')
    	{
    		document.getElementById("txt_procesoCalculo").disabled = false;
    	}     	
    },
    loadCamposSC_ajax:function(){
    	var idlinea   = UTIL.getValueOfSelect(document.getElementById("sl_lineas"));
        var uso       = UTIL.getValueOfSelect(document.getElementById("sl_usos"));
        var ubicacion = UTIL.getValueOfSelect(document.getElementById("sl_ubicaciones"));
        if (idlinea != "" && uso != "" && ubicacion != ""){
        $.ajax({
            url: "relacionCampos.html",
            data: "operacion=ajax_getCamposSC&idLinea=" + idlinea + "&Uso=" + uso + "&Ubicacion=" + ubicacion,
            beforeSend: function(objeto){document.getElementById("ajaxLoading_camposSC").style.display = 'block';},
            complete: function(objeto, exito){document.getElementById("ajaxLoading_camposSC").style.display = 'none';},
            contentType: "application/x-www-form-urlencoded",
            dataType: "text",
            error: function(objeto, quepaso, otroobj){alert("Error: " + quepaso);},
            success: function(datos){
            	var list = eval(datos);
                var sl = document.getElementById("sl_camposSC");
               // alert(list.length);
                if(sl && list){
                    if(list.length > 0){
                		var opt = document.createElement('OPTION');
                        opt.innerHTML = " -- Seleccione una opción -- ";
                        opt.value = "smsAviso";
                        sl.appendChild(opt);
                        for(var i = 0; i < list.length; i++){
                            var opt = document.createElement('OPTION');
                            opt.innerHTML = list[i].nodeText;
                            opt.value = list[i].value;
                            sl.appendChild(opt);
                        }
                	}
                    else{
                    	 var opt = document.createElement('OPTION');
                    	 opt.innerHTML = " -- Sin opción seleccionable -- ";
                         opt.value = "smsAviso";
                         sl.appendChild(opt);
                    }
                }
            },
            type: "POST"
        });
        }
    },
    
    /*loadGruposFactores_ajax:function(){
    	var camposc = UTIL.getValueOfSelect(document.getElementById("sl_camposSC"));
        $.ajax({
            url: "relacionCampos.html",
            data: "operacion=ajax_getGrupoFactores&campo=" + camposc,
            beforeSend: function(objeto){document.getElementById("ajaxLoading_grupoFactores").style.display = 'block';},
            complete: function(objeto, exito){document.getElementById("ajaxLoading_grupoFactores").style.display = 'none';},
            dataType: "text",
            error: function(objeto, quepaso, otroobj){alert("Error: " + quepaso);},
            success: function(datos){
            	UTIL.cleanSelect(document.getElementById("sl_factores"));
            	var list = eval(datos);
                var sl = document.getElementById("sl_factores");
                if(sl && list){
                    if(list.length > 0){
                    	var opt = document.createElement('OPTION');
                        opt.innerHTML = "";
                        opt.value = "";
                        sl.appendChild(opt);
                        for(var i = 0; i < list.length; i++){
                            var opt = document.createElement('OPTION');
                            opt.innerHTML = list[i].nodeText;
                            opt.value = list[i].value;
                            sl.appendChild(opt);
                        }
                	}
                }
            },
            type: "POST"
        });
    	
    },*/
    
    loadFactoresPorGrupo_ajax:function(){
    	var factor = UTIL.getValueOfSelect(document.getElementById("sl_factores"));
        $.ajax({
            url: "relacionCampos.html",
            data: "operacion=ajax_getFactoresGrupo&Factor=" + factor,
            beforeSend: function(objeto){document.getElementById("ajaxLoading_factor").style.display = 'block';},
            complete: function(objeto, exito){document.getElementById("ajaxLoading_factor").style.display = 'none';},
            dataType: "text",
            error: function(objeto, quepaso, otroobj){alert("Error: " + quepaso);},
            success: function(datos){
            	UTIL.cleanSelect(document.getElementById("sl_grupoFactores"));
            	var list = eval(datos);
                var sl = document.getElementById("sl_grupoFactores");
                if(sl && list){
                    if(list.length > 0){
                        for(var i = 0; i < list.length; i++){
                            var opt = document.createElement('OPTION');
                            opt.innerHTML = list[i].nodeText;
                            opt.value = list[i].value;
                            sl.appendChild(opt);
                        }
                	}
                }
            },
            type: "POST"
        });
    	
    },
    
		valorCombo:function()
	{
		var operacion = document.forms.main.valorCombo.value;
		var linea = document.forms.main.valorLinea.value;
		//alert(operacion);
		
	}
}