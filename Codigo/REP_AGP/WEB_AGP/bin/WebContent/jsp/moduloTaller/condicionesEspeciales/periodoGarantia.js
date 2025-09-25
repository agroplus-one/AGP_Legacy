/*
**************************************************************************************************
*  CReACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* 			              Marcos Del Rey  	js para la pantalla Periodo Garantia .   
*
 **************************************************************************************************
*/

var periodoGarantia = {
	
	cargaCombo:function(direccion, operacion, selectOrigen, selectDestino){
		var valorDatos ="";
		if("" != selectOrigen) {
			valorDatos = "&" + selectOrigen + "=" + UTIL.getValueOfSelect(document.getElementById(selectOrigen));
		}
		for(var j = 5; j < arguments.length; j++) {
			valorDatos += "&" + arguments[j] + "=" + UTIL.getValueOfSelect(document.getElementById(arguments[j]));
		}
		UTIL.cleanSelect(document.getElementById(selectDestino));
		$.ajax({
			url: direccion,
			data: "operacion=" + operacion + valorDatos,
			async:true,
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
		var sl = document.getElementById(selectDestino);
		var opt = document.createElement('OPTION');
			opt.innerHTML = "Todos";
			opt.value = "0";
			sl.appendChild(opt);
			if(sl && list){
				for(var i = 0; i < list.length; i++){ 
					opt = document.createElement('OPTION');
					opt.innerHTML = list[i].value+" - "+list[i].nodeText;
					opt.value = list[i].value;
					sl.appendChild(opt);
				}
			}
		},
		type: "GET"
	});
	}

	
	
	
	
}