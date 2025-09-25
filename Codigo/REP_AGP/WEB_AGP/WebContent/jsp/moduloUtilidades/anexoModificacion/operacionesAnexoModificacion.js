	function eliminar(idAnexoMod){
		
		if(confirm('\u00BFEst\u00E1 seguro de que desea eliminar el registro seleccionado?')){
			$("#method").val("doBaja");
			$("#id").val(idAnexoMod);
			if (arguments.length > 1){
				//Viene el idpoliza como argumento de la funcion
				$("#idPoliza").val(arguments[1]);
			}
			$.blockUI.defaults.message = '<h4> Eliminado registro seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
   			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			$("#main").submit();
		}
	} 
	 
	
	
	
	//DAA 13/11/12 
	function editar (idAnexoMod, idPoliza, estado) {
		if(estado!=5){
				doEditar(idAnexoMod, idPoliza);
		}
		else{
			if(confirm('El Anexo de Mod. pasara a estado Provisional, \u00BFDesea Continuar?')){
				doEditar(idAnexoMod, idPoliza);
			}
		}			
	
	}
	
	function doEditar(idAnexoMod,idPoliza){	
		$("#method").val("doEdita");
		$("#id").val(idAnexoMod);
		$("#idPoliza").val(idPoliza);
		$.blockUI.defaults.message = '<h4> Procesando petici\u00F3n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
   		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$("#main").submit();
	}
	//DAA 13/11/12
	function editarCpl(idAnexoMod,estado){
		$("#idAnexoCpl").val(idAnexoMod);
		if (arguments.length > 2){
				//Viene el idpoliza como argumento de la funcion
				$("#idPoliza").val(arguments[2]);
		}
		if(estado!=5){
		doEditarCpl();
		}
		else{
			if(confirm('El Anexo de Mod. pasar\u00E1 a estado Provisional, \u00BFDesea Continuar?')){
				doEditarCpl();
			}
		}	
	}
	
	function doEditarCpl(){
		$("#methodCpl").val("doConsulta");
		$.blockUI.defaults.message = '<h4> Procesando petici\u00F3n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
   		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$("#main4").submit();
		
	}
				
	function imprimir(idAnexo,idPoliza)	{
		var frm = document.getElementById('print');
		frm.idPoliza.value = idPoliza;
		frm.idAnexo.value = idAnexo;
		frm.aleatorio.value= Math.random();
		frm.action = frm.action + '?rand=' + frm.aleatorio.value;
		$('#print').attr('target', '_blank');
		$("#print").submit();
		
	} 
	function imprimirCpl(idAnexo,idPoliza)	{
		var frm = document.getElementById('printCpl');
		frm.idPoliza.value = idPoliza;
		frm.idAnexo.value = idAnexo;
		frm.aleatorio.value= Math.random();
		frm.action = frm.action + '?rand=' + frm.aleatorio.value;
		$('#printCpl').attr('target', '_blank');
		$("#printCpl").submit();	
	}
	
	function imprimirSw(cuponId,idAnexo,referencia)	{
		var frm = document.getElementById('imprimirAnexo');
		frm.refPoliza.value = referencia;
		frm.idCuponImprimir.value = cuponId;
		frm.idImprimir.value = idAnexo;
		frm.methodImprimir.value = "doImprimirAnexoPpal";
		frm.aleatorio.value = Math.random();
		frm.action = frm.action + '?rand=' + frm.aleatorio.value;
		$('#imprimirAnexo').attr('target', '_blank');
		$("#imprimirAnexo").submit();
		
	} 
	function imprimirCplSw(cuponId,idAnexo,referencia)	{
		var frm = document.getElementById('imprimirAnexo');
		frm.refPoliza.value = referencia;
		frm.idCuponImprimir.value = cuponId;
		frm.idImprimir.value = idAnexo;
		frm.methodImprimir.value = "doImprimirAnexoCpl";
		frm.aleatorio.value = Math.random();
		frm.action = frm.action + '?rand=' + frm.aleatorio.value;
		$('#imprimirAnexo').attr('target', '_blank');
		$("#imprimirAnexo").submit();
	}
	
	function pasarDefinitivo(idAnexoMod){
		if(confirm('\u00BFEst\u00E1 seguro de que desea pasar a definitivo el Anexo de Mod. seleccionado?')){
			$.blockUI.defaults.message = '<h4> Pasando a definitivo el Anexo de Mod. seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			if (arguments.length > 1){
				//Viene el idpoliza como argumento de la funcion
				$("#idPoliza").val(arguments[1]);
			}
			var idcupon = null;
			var esFtp = true;
			var esCpl  = false;
			validacionesPreviasEnvioAjax(idcupon,idAnexoMod,esFtp,esCpl);
			
		    //$("#method").val("doPasarDefinitiva");
			//$("#id").val(idAnexoMod);
			//$("#main").submit();
		}
	}
	
	function pasarDefinitivoCpl(idAnexoMod){
		if(confirm('\u00BFEst\u00E1 seguro de que desea pasar a definitivo el Anexo de Mod. seleccionado?')){
			$.blockUI.defaults.message = '<h4> Pasando a definitivo el Anexo de Mod. seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			if (arguments.length > 1){
				//Viene el idpoliza como argumento de la funcion
				$("#idPoliza").val(arguments[1]);
			}
			var idcupon = null;
			var esFtp = true;
			var esCpl  = true;
			validacionesPreviasEnvioAjax(idcupon,idAnexoMod,esFtp,esCpl);
			
		    //$("#methodCpl").val("doPasarDefinitiva");
			//$("#idAnexoCpl").val(idAnexoMod);
			//$("#main4").submit();
		}
	}
	
	function verErrores(idAnexoMod){	
		$("#method").val("doVerRecibo");
		$("#id").val(idAnexoMod);
		$("#main").submit();
	}
	
	function verInformacion(idAnexoMod,idpoliza){
		var frm = document.getElementById('main');
		frm.idPoliza.value = idpoliza;
		frm.id.value = idAnexoMod;	
		frm.method.value = "doVisualiza";
		
		$.blockUI.defaults.message = '<h4> Procesando petici\u00F3n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
   		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$("#main").submit();  
	}
	
	function verInformacionCpl(idAnexoMod,idpoliza){
		$("#methodCpl").val("doVisualiza");
		$("#idAnexoCpl").val(idAnexoMod);
		if (arguments.length > 1){
			//Viene el idpoliza como argumento de la funcion
			$("#idPolizaCpl").val(arguments[1]);
		}
		$.blockUI.defaults.message = '<h4> Procesando petici\u00F3n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
   		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$("#main4").submit();
		
		
	}
	
	/**
	 * Realiza la llama al SW de validacion previo a la confirmacion del cupon
	 * @param idCupon
	 */
	function validarAMCupon (idCupon,idAnexo) {
		muestraCapaEspera ("Validando el A.M");
		$("#idCuponValidar").val(idCupon);
		validacionesPreviasEnvioAjax(idCupon,idAnexo);
	}
    
	function validacionesPreviasEnvioAjax(idCupon,idAnexo,esFtp,esCpl){
		
		$.ajax({
	            url: "validacionesAnexoAjax.html",
	            data: "method=doValidacionesPreviasEnvio&idAnexo="+idAnexo,
	            async:true,
	            cache: false,
	            beforeSend: function(objeto){
	            },
	            complete: function(objeto, exito){
	            },
	            contentType: "application/x-www-form-urlencoded",
	            dataType: "json",
	            error: function(objeto, quepaso, otroobj){
	                alert("Error al comprobar la coherencia de los datos variables de las explotaciones: " + quepaso);
	            },
	            global: true,
	            ifModified: false,
	            processData:true,
	            success: function(datos){
	            	
	            	if(datos.validacionesPreviasEnvio.valueOf() == "true"){
	            		if (esFtp){
	            			if (esCpl){
	            				$("#methodCpl").val("doPasarDefinitiva");
	            				$("#idAnexoCpl").val(idAnexo);
	            				$("#main4").submit();
	            			}else{
	            				$("#method").val("doPasarDefinitiva");
	            				$("#id").val(idAnexo);
	            				$("#main").submit();
	            			}
	            		}else{
	            			$("#idCuponValidar").val(idCupon);
	            			$("#validarAnexo").submit();
	            		}
	            	}else{
	            		quitaCapaEspera ();
	            		$("#idPoliza").val("");
	            		$('#panelInformacion').hide();
	            		$('#panelAlertasValidacion').html(datos.mensaje);
	            		$('#panelAlertasValidacion').show();
	            	}
	            	
	            },
	            type: "POST"
	        });
	}
	
	
	/**
	 * Muestra la capa de espera con el mensaje recibido como parametro
	 * @param msg
	 * @returns
	 */
	function muestraCapaEspera (msg) {
		$.blockUI.defaults.message = '<h4> ' + msg + '.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	}
	
	/**
	 * Realiza la llamada para redirigir a la pantalla de visualizacion de acuse de recibo de confirmacion
	 * @param idAnexo
	 */
	function verAcuseConfirmacion (idAnexo, idPoliza, idCupon) {
		$("#idAnexoAcuse").val(idAnexo);
		$("#idPolizaAcuse").val(idPoliza);
		$("#idCuponAcuse").val(idCupon);
		$("#acuseReciboConfirmacion").submit();
	}
	
	/**
	 * para cupones SW en estado confirmado-aplicado se imprime el pdfIncidencias (llamada al ws)
	 * @param idCupon
	 */
	function imprimirSwPDFIncidencia(idCupon){
		var frm = document.getElementById('impresionIncidenciasMod');
		frm.method.value = "doImprimirPdf";
		frm.idCuponImpresion.value = idCupon;
		$('#impresionIncidenciasMod').attr('target', '_blank');
		$("#impresionIncidenciasMod").submit();
	}
	
	/**
	 * Muestra la capa de espera con el mensaje recibido como parametro
	 * @param msg
	 * @returns
	 */
	function muestraCapaEspera (msg) {
		$.blockUI.defaults.message = '<h4> ' + msg + '.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	}

	/**
	 * Elimina la capa de espera
	 */
	function quitaCapaEspera () {
		$.unblockUI();
	}