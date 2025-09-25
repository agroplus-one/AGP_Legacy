var activacion = {
	getLimitesProduccion_ajax:function(idcapitalasegurado){
    	//var plan = UTIL.getValueOfSelect(document.getElementById("sl_planes"));
    	document.getElementById('idCapitalAseguradoSeleccionado').value = idcapitalasegurado;
    	document.getElementById('panelAlertasLocal').style.display = "none";
    	$.ajax({
            url: "revProduccionPrecio.html",
            data: "operacion=getLimitesProduccion_ajax&idcapitalasegurado="+idcapitalasegurado,
            success: function(datos){  
            	var list = eval(datos);
            	if (list != null && list[0] == '') {
            		 $.unblockUI();
            		 alert('Se ha producido un error al calcular la producción y el precio. Consulte con el Administrador.');
            		 return;
            	}
            	var produccion = list[0].produccion;
            	document.getElementById('produccion').value = produccion;
      			var precio = list[0].precio;
      			document.getElementById('precio').value = precio;
            	
            	var limitesProduccion = '(' +list[1].produccionMin+' - '+list[1].produccionMax+')';
            	document.getElementById('main').produccion.value = produccion;
            	document.getElementById('main').produccion.readOnly = false;
            	document.getElementById('main').limitesProduccion.value = limitesProduccion;
            	document.getElementById('main').produccionMin.value = list[1].produccionMin;
            	document.getElementById('main').produccionMax.value = list[1].produccionMax;
            	
            	var limitesPrecio = '('+list[2].precioMin+' - '+list[2].precioMax+')';
            	document.getElementById('main').limitesPrecio.value = limitesPrecio;

                if (list[2].precioMin != "Precio Fijo"){
                	document.getElementById('main').precio.value = precio;
                	document.getElementById('main').precio.readOnly = false;
                	document.getElementById('main').precioMin.value = list[2].precioMin;
					document.getElementById('main').precioMax.value = list[2].precioMax;
                }else{
					document.getElementById('main').precio.value = precio;
					document.getElementById('main').precio.readOnly = true;
					document.getElementById('main').precioMin.value = list[2].precioMax;
					document.getElementById('main').precioMax.value = list[2].precioMax;
					document.getElementById('main').precioFijo.value = list[2].precioMax;
                }
            },
            beforeSend: function(){
            	$.blockUI.defaults.message = '<h4> Calculando límites de producción y precios.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } }); 
       		},
			complete: function(){
				$.unblockUI();			
			},
            type: "POST"
        });
    }
}