/*
**************************************************************************************************
*
*  CREACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034  14/07/2010  Antonio Serrano   js para las llamadas comunes por ajax.
*
**************************************************************************************************
*/
var common = {
    selectPlan_onchange:function(idPlanes, idLineas){
    	//var idSelect = document.getElementById("sl_lineas");
    	var idSelect = document.getElementById(idLineas);
    	UTIL.cleanSelect(idSelect);
    	this.loadLineas_ajax(idPlanes, idLineas);
    },

    loadLineas_ajax:function(idPlanes, idLineas){
    	//var plan = UTIL.getValueOfSelect(document.getElementById("sl_planes"));
    	var plan = UTIL.getValueOfSelect(document.getElementById(idPlanes));
        $.ajax({
            url: "ajaxCommon.html",
            data: "operacion=ajax_getLineas&Plan=" + plan,
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
                var sl = document.getElementById(idLineas);
                
                if(sl && list){
                	if(list.length > 0){
                		var opt = document.createElement('OPTION');
                        opt.innerHTML = " -- Seleccione un opci&oacute;n -- ";
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
    },
    //DAA 24/10/2013 Montar el detalle para el informe Recibos formato HTML  
    mostrarDetalleInformeRecibos_ajax:function(){
        $.ajax({
            url: "informesRecibos.run",
            data: "method="+$("#method").val()+"&stringRegistro="+$("#stringRegistro").val(),
            async:true,
            beforeSend: function(objeto){
            },
            complete: function(objeto, exito){
            },
            contentType: "application/x-www-form-urlencoded",
            dataType: "json",
            error: function(objeto, quepaso, otroobj){
                alert("Error: " + quepaso);
            },
            global: true,
            ifModified: false,
            processData:true,
            success: function(datos){
          	
            	// montamos la cabecera
            	var cabecera = eval(datos.cabecera);
            	var newRow = document.createElement("tr");
            	for(var i = 0; i < cabecera.length; i++){
            		var newCol = document.createElement("td");
            		var newTxt = document.createTextNode(cabecera[i]);
            		newCol.appendChild(newTxt);
            		newCol.className = "literalBordeCabecera";
            		newRow.appendChild(newCol);
            	}
            	document.getElementById('tablaDetalle').getElementsByTagName("tbody")[0].appendChild(newRow);
            	
            	
            	//montamos los registros
            	var registros = eval(datos.registros);
            	for(var j = 0; j < registros.length; j++){
            		var newRow = document.createElement("tr");
            		var datos = registros[j];
            		for(var k = 0; k < datos.length; k++){
            			var newCol = document.createElement("td");
            			var newTxt = document.createTextNode(datos[k]);
            			if(newTxt.nodeValue == "null"){
            				newTxt.nodeValue = " ";
            			}
            			newCol.appendChild(newTxt);
            			newCol.className = "detalI";
            			if(j == registros.length-1){
            				newRow.style.backgroundColor="#e5e5e5"; 
            			}
            			newRow.appendChild(newCol);
            		}
            		document.getElementById('tablaDetalle').getElementsByTagName("tbody")[0].appendChild(newRow);	
            	}
            	showPopupDetalleRecibos();
            },
            type: "POST"
        });
    },
  // Montar el detalle para el informe Comisiones formato HTML  
    mostrarDetalleInformeComisiones_ajax:function(){
        $.ajax({
            url: "informesComisiones.run",
            data: "method="+$("#method").val()+"&stringRegistro="+$("#stringRegistro").val(),
            async:true,
            beforeSend: function(objeto){
            },
            complete: function(objeto, exito){
            },
            contentType: "application/x-www-form-urlencoded",
            dataType: "json",
            error: function(objeto, quepaso, otroobj){
                alert("Error: " + quepaso);
            },
            global: true,
            ifModified: false,
            processData:true,
            success: function(datos){
          	
            	// montamos la cabecera
            	var cabecera = eval(datos.cabecera);
            	var newRow = document.createElement("tr");
            	for(var i = 0; i < cabecera.length; i++){
            		var newCol = document.createElement("td");
            		var newTxt = document.createTextNode(cabecera[i]);
            		newCol.appendChild(newTxt);
            		newCol.className = "literalBordeCabecera";
            		newRow.appendChild(newCol);
            	}
            	document.getElementById('tablaDetalle').getElementsByTagName("tbody")[0].appendChild(newRow);
            	
            	
            	//montamos los registros
            	var formatos = eval(datos.formatoCom);
            	var registros = eval(datos.registros);
            	for(var j = 0; j < registros.length; j++){
            		var newRow = document.createElement("tr");
            		var datos = registros[j];
            		for(var k = 0; k < datos.length; k++){
            			var newCol = document.createElement("td");
            			if(formatos[k] == "6" || formatos[k] == "7"){
            				newCol.style.textAlign="right";
            			}
            			var newTxt = document.createTextNode(datos[k]);
            			if(newTxt.nodeValue == "null"){
            				newTxt.nodeValue = " ";
            			}
            			newCol.appendChild(newTxt);
            			newCol.className = "detalI";
            			if(j == registros.length-1){
            				newRow.style.backgroundColor="#e5e5e5"; 
            			}
            			newRow.appendChild(newCol);
            		}
            		document.getElementById('tablaDetalle').getElementsByTagName("tbody")[0].appendChild(newRow);	
            	}
            	showPopupDetalleComisiones();
            },
            type: "POST"
        });
    }
    

}
