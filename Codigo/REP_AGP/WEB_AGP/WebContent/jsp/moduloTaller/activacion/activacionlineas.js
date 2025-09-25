var activacion = {
	consultar:function()
    {
    	document.forms.main.operacion.value = "consultar";    
    	document.forms.main.submit();
    },
	loadLineas_ajax:function(){
    	var plan = UTIL.getValueOfSelect(document.getElementById("sl_planes"));
    	
        $.ajax({
            url: "activacionlineas.html",
            data: "action=ajax_getLineas&Plan="+plan,

            async:true,
            beforeSend: function(objeto){
                document.getElementById("ajaxLoading_lineas").style.display = 'block';
            },
            complete: function(objeto, exito){
            	document.getElementById("ajaxLoading_lineas").style.display = 'none';
            },
            contentType: "application/x-www-form-urlencoded",
            dataType: "text",
            error: function(objeto, quepaso, otroobj){
                alert("Error: " + quepaso);
            },
            global: true,
            ifModified: false,
            processData:true,
            success: function(datos){               
                var list = eval(datos);
                var sl = document.getElementById("sl_linea");
                
                if(sl && list){
                    for(var i = 0; i < list.length; i++){
                        var opt = document.createElement('OPTION');
                        opt.innerHTML = list[i].nodeText;
                        opt.value = list[i].value;
                        sl.appendChild(opt);
                    }
                }                  
            },
            type: "GET"
        });
    },
	selectPlan_onchange:function(){
    	var idSelect = document.getElementById("sl_linea");
    	UTIL.cleanSelect(idSelect);
    	this.loadLineas_ajax();
    },
    cleanGrid:function(){
		var combos = ["sl_planes", "sl_linea", "sl_estado", "sl_activado"];
        UTIL.cleanCombos(combos);
        var fields = ["fechaActiv"];
        UTIL.cleanTextboxs(fields);
    },
    detalle:function(idRow){
    	var frm = document.getElementById("main");
	    frm.ROW.value = idRow;
	    frm.operacion.value = "selecccionar";
	    frm.submit();
    },
    activar:function(lineaSeg){
       	var frm = document.getElementById("main");
    	frm.lineaSeguroSelect.value = lineaSeg;
    	frm.operacion.value = "activar";
    	frm.submit();
    },
    //DAA 29/01/13 forzarActivar,showPopUpForzarActivar,cerrarPopUpForzarActivar,getTablasPendientes_ajax
    forzarActivar:function(){
		activacion.cerrarPopUpForzarActivar();
		$('#forzarActivar').val('true');
		activacion.activar($('#idLinSeg').val());
    },
    cerrarPopUpForzarActivar:function(){
		$('#divForzarActivar').fadeOut('normal');
		$('#overlay').hide();
		$('#listado').remove();
	},  
    showPopUpForzarActivar:function(lineaSeg){
    	$('#idLinSeg').val(lineaSeg);
		
		activacion.getTablasPendientes_ajax(lineaSeg);
    },
    getTablasPendientes_ajax:function(lineaSeg){
    	//DAA 29/01/13
        $.ajax({
            url: "ajaxCommon.html",
            data: "operacion=ajax_getTablasPendientes&lineaseguroId="+lineaSeg,
            async:true,
            
            complete: function(objeto, exito){
            	document.getElementById("ajaxLoading_tablas").style.display = 'none';
            },
            contentType: "application/x-www-form-urlencoded",
            dataType: "text",
            error: function(objeto, quepaso, otroobj){
                alert("Error: " + quepaso);
            },
            global: true,
            ifModified: false,
            processData:true,
            success: function(datos){
            	var list = eval(datos);
            	
            	//Controlar que cuando la tabla venga sin registros no muestre ningun pop-up
            	if (list.length > 0){
            		
            		$('#divForzarActivar').fadeIn('normal');
            		$('#overlay').show();
            		
            		document.getElementById("ajaxLoading_tablas").style.display = 'block';
            		
            		var div = document.getElementById("tablaInformacion");
                	if(div && list){
                		
                		var ul = document.createElement('ul');
                		ul.setAttribute("id", "listado");
                		for(var i = 0; i < list.length; i++){
                			var li = document.createElement('li');
                			li.innerHTML = list[i];
                			
                			ul.appendChild(li);
                			
                		}
                		div.appendChild(ul);
                	}
            		
            	}else{
            		
            		$('#overlay').hide();
            		activacion.forzarActivar();
            	}  
            },
            type: "GET"
        });
    },  
    bloquear:function(idRow){
    	var frm = document.getElementById("main");
    	frm.ROW.value = idRow;
    	frm.operacion.value = "bloquear";
    	frm.submit();
	},
    detalleTablas:function(idRow){
    	var frm = document.getElementById("main");
    	frm.ROW.value = idRow;
    	frm.operacion.value = "detalleTablas";
    	frm.submit();
    },
    coberturas:function(lineaSeg, esGanado){
    	var frm = document.getElementById("main");
    	frm.operacion.value = "coberturas";
    	frm.lineaSeguroSelect.value = lineaSeg;
    	frm.esGanado.value = esGanado;
    	frm.submit();
    }    
}