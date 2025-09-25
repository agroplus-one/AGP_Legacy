//DAA 30/07/2012
//Funciones para las operaciones del listado de Red. Capital

		function eliminar(idReduccionCapital){
			
			if(confirm('&iquest;Est&aacute; seguro de que desea eliminar el registro seleccionado?')){
				$("#method").val("doBaja");
				$("#id").val(idReduccionCapital);
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
		function editar(idReduccionCapital, idPoliza, estado){
			$("#id").val(idReduccionCapital);
			if (arguments.length > 2){
				//Viene el idpoliza como argumento de la funcion
				$("#idPoliza").val(arguments[1]);
				idPoliza=arguments[1];
			}
			
			if(estado!=5){
			doEditar(idReduccionCapital, idPoliza);
			}
			else{
				if(confirm('El Red.Capital pasar&aacute; a estado Provisional, &iquest;Desea Continuar?')){
					doEditar(idReduccionCapital, idPoliza);
				}
			}	
		}
		
		/**
		 * Realiza la llamada para comprobar si el anexo por cup�n caducado es editable o no
		 * @param idAnexo
		 * @param idPoliza
		 */
		function editarRCCuponCaducado (idAnexo, idPoliza, referencia, plan) {
				
			var frm = document.getElementById('main');
			// Realiza la llamada al SW
			$.ajax({
				url:          "declaracionesReduccionCapital.html",
				data:         "method=isEditableRCCuponCaducado&idAnexo=" + idAnexo + "&idPoliza=" + idPoliza,
				async:        true,
				contentType:  "application/x-www-form-urlencoded",
				dataType:     "json",
				global:       false,
				ifModified:   false,
				processData:  true,
				error: function(objeto, quepaso, otroobj){
					alert("Error: " + quepaso);
				},
				success: function(resultado){
					renovarRCCuponCaducado (resultado.isEditableRCCuponCaducado, idAnexo, idPoliza, referencia, plan);
				},
				type: "GET"
			});	
		}
		
		/**
		 * Procesa la respuesta de llamada de la funci�n 'editarAMCuponCaducado'
		 * @param isEditableRCCuponCaducado
		 */
		function renovarRCCuponCaducado (isEditableRCCuponCaducado, idAnexo, idPoliza, referencia, plan) {
			
			if (isEditableRCCuponCaducado == '0') {
				jConfirm('El cupón asociado al anexo está caducado. Se va a proceder a solicitar un nuevo cupón. ¿Desea continuar?',
						'Dialogo de Confirmación', function(r) {
					if (r==true) {
							solicitarCuponRCSW(idAnexo, idPoliza, referencia, plan);
						}
					}
				);
			}
			else if (isEditableRCCuponCaducado == '1') {
				alert ('No se puede editar debido a que existe otro anexo por cup�n en provisional y con el cup�n en activo para la p�liza asociada');
			}
			else if (isEditableRCCuponCaducado == '2') {
				alert ('No se puede editar debido a que existe otro anexo por ftp enviado correcto en fecha posterior a la del anexo en cuesti�n');
			}
			else if (isEditableRCCuponCaducado == '3') {
				alert ('No se puede editar debido a que existe otro anexo por cup�n enviado correcto en fecha posterior a la del anexo en cuesti�n');
			}
			else {
				alert ('Ha ocurrido un error al comprobar si el anexo es editable');
			}
		}
		
		/**
		 * Realiza la llamada al SW de Solicitud de Modificacion
		 */
		function solicitarCuponRCSW (idAnexo, idPoliza, referencia, plan) {
			
			// Muestra la capa informativa
			muestraCapaEspera ("Solicitando nuevo cup&oacuten");
			
			// Se rellena el hidden que indica que se va a renovar el cup�n de un AM caducado
			$("#idAnexoCaducado").val('S');
			
			// Realiza la llamada al SW
			$.ajax({
				url:          "solicitudReduccionCap.html",
				data:         "method=doSolicitudModificacion&referencia=" + referencia + "&codPlan=" + plan,
				async:        true,
				contentType:  "application/x-www-form-urlencoded",
				dataType:     "json",
				global:       false,
				ifModified:   false,
				processData:  true,
				error: function(objeto, quepaso, otroobj){
					// Se borra el campo del anexo caducado para hacer posible que se de un alta nueva 
					$("#idAnexoCaducado").val('');
					
					quitaCapaEspera ();
					alert("Error en la llamda al SW de Solicitud de Modificacion: " + quepaso);
				},
				success: function(resultado){
					doEditarRCCuponCaducado (resultado, idAnexo, idPoliza);
				},
				type: "GET"
			});	
			
		}
		
		/**
		 * Realiza la llamada para actualizar el antiguo cup�n con el nuevo y editar el anexo
		 * @param idCupon
		 * @param idAnexoMod
		 * @param idPoliza
		 */
		function doEditarRCCuponCaducado (resultado, idAnexoRC,idPoliza){	
			
			// Comprueba si se ha recibido correctamente el id de cup�n o ha ocurrido alg�n error
			if (resultado.error != null && resultado.error != '') {
				quitaCapaEspera ();
				
				// Se borra el campo del anexo caducado para hacer posible que se de un alta nueva 
				$("#idAnexoCaducado").val('');
				
				alert (resultado.error);
			}
			else {
				$("#method").val("doEdita");
				$("#id").val(idAnexoRC);
				$("#idPoliza").val(idPoliza);
				$("#idPoliza2").val(idPoliza);
				$("#idCupon").val(resultado.idCupon);
				$("#main").submit();
			}
			
		}
			 
		function doEditar(idAnexoRC,idPoliza){	
			$("#method").val("doEdita");
			$("#id").val(idAnexoRC);
			$("#idPoliza").val(idPoliza);
			$("#idPoliza2").val(idPoliza);
			$.blockUI.defaults.message = '<h4> Procesando petici&oacuten.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			$("#main").submit();			
		}
		
		// Redirige a la pantalla de visualizaci�n de la reducci�n en modo solo lectura
		function informacion (idReduccionCapital,idPoliza){	
			$("#method").val("doEdita");
			$("#id").val(idReduccionCapital);
			$("#idPoliza").val(idPoliza);
			$("#idPoliza2").val(idPoliza);
			$("#modoLectura").val("true");
			if (arguments.length > 1){
				//Viene el idpoliza como argumento de la funcion
				$("#idPoliza").val(arguments[1]);
			}
			$.blockUI.defaults.message = '<h4> Procesando petici&oacuten.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			$("#main").submit();
		}
					
		function crear(idPoliza, codlinea, codplan, nomlinea, refPoliza){
			
			$.ajax({
		        url: "declaracionesReduccionCapital.html",
		        data: "method=doComprobarAlta&id=&codLinea="+codlinea + "&idPoliza="+idPoliza + "&codplan="+codplan + + "&nomlinea="+nomlinea + "&refPoliza="+refPoliza,
		        async:true,
		        beforeSend: function(objeto){
		        },
		        cache: false,
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
		        	if (datos.objeto == "tieneAnexo") {
		        		$('#panelAlertasValidacion').html("Ya existe un anexo que no est&aacute; confirmado con Agroseguro.");
		        		$('#panelAlertasValidacion').show();
		        	}
		        	else {
		        		alta ();
		        	}
		        },
		        type: "POST"
		    });
				
		} 
		
		function alta() {
			// Muestra la capa informativa
			muestraCapaEspera ("Solicitando alta de R.C");
			
			var frm = document.getElementById('main');
			// Realiza la llamada al SW
			$.ajax({
				url:          UTIL.antiCacheRand("solicitudReduccionCap.html"),
				data:         "method=doSolicitudModificacion&referencia=" + frm.refPoliza.value + "&codPlan=" + $("#codplan").val(),
				async:        true,
				contentType:  "application/x-www-form-urlencoded",
				dataType:     "json",
				global:       false,
				ifModified:   false,
				processData:  true,
				error: function(objeto, quepaso, otroobj){
					
					quitaCapaEspera ();
					alert("Error en la llamda al SW de Solicitud de Modificacion: " + quepaso);
				},
				success: function(resultado){
					quitaCapaEspera ();
					procesarRespuestaAlta (resultado);
				},
				type: "GET"
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
		 * Elimina la capa de espera
		 */
		function quitaCapaEspera () {
			$.unblockUI();
		}
		
		/**
		 * Procesa la respuesta de la solicitud de modificacion
		 * @param resultado
		 */
		function procesarRespuestaAlta (resultado) {
			
			// Si ha habido algun error en la llamada al SW
			if (resultado.error != '') {
				
				alert (resultado.error);
			}
			// Si la llamada ha sido correcta
			else {
				// Inserta los datos necesarios en el formulario
				$("#idCuponNum").val(resultado.id);
				$("#idCupon").val(resultado.idCupon); 
				$("#idCuponPpalPrevio").val(resultado.modifPpalCupon);
				$("#estadoCuponPpalPrevio").val(resultado.modifPpalIdEstado);
				
				// Rellena el mensaje de estado de contratacion con los datos recibidos del SW
				$("#estadoPpal").html(resultado.estadoPpal);
				// Si hay poliza complementaria
				// Si hay modificaciones previas
				if (resultado.modifPpalCupon != '') {
					$("#noModPrev").hide();
					$("#siModPrev").show();
					$("#tablaModPrev").show();
					
					if (resultado.modifPpalCupon != '') {
						$("#trModPrevPpal").show();
						$("#tdModPrevPpalCupon").html (resultado.modifPpalCupon);
						$("#tdModPrevPpalEstado").html (resultado.modifPpalEstado);
					}
				}
				// Si la poliza asociada es una renovable en estado 'Precartera generada', 'Precartera precalculada' o 'Primera comunicacion'
				// el mensaje en el popUp de 'Estado de la contratacion' sera 'Estado de la Renovacion en Agroseguro'. 
				// En cualquier otro estado sera 'Estado de la Poliza en Agroseguro'
				if ($("#idEstadoPlzAsociada").val() == 12 || $("#idEstadoPlzAsociada").val() == 18 || $("#idEstadoPlzAsociada").val() == 19) {
					$("#idMsgPoliza").html("Renovaci&oacute;n");
				}
				else {
					$("#idMsgPoliza").html("P&oacute;liza");
				}
				
				// Muestra el mensaje
				mostrarMsgContratacion();
			}
		}
		
		/**
		 * Muestra el mensaje de estado de la contratacion
		 */
		function mostrarMsgContratacion (){
		    $('#panelEstadoContratacion').show();
		    $('#overlay').show();
		}
		
		/**
		 * Realiza la llamada al SW de Anulacion de Cupon
		 */
		function cancelarCupon () {
			
			// Oculta y limpia el mensaje de estado de contratacion
			ocultarMsgContratacion ();
			limpiarMsgContratacion ();
			
			// Muestra la capa informativa
			muestraCapaEspera ("Cancelando el cupon");
			
			// Realiza la llamada al SW
			$.ajax({
				url:          "solicitudReduccionCap.html?id=" + $("#idCuponNum").val() + "&idCupon=" + $("#idCupon").val(),
				data:         "method=doAnularCupon",
				async:        true,
				contentType:  "application/x-www-form-urlencoded",
				dataType:     "json",
				global:       false,
				ifModified:   false,
				processData:  true,
				error: function(objeto, quepaso, otroobj){
					// Se borra el campo del anexo caducado para hacer posible que se de un alta nueva 
					$("#idAnexoCaducado").val('');
					
					quitaCapaEspera ();
					alert("Error en la llamda al SW de Anulacion de Cupon: " + quepaso);
				},
				success: function(resultado){
					// Se borra el campo del anexo caducado para hacer posible que se de un alta nueva 
					$("#idAnexoCaducado").val('');
					
					quitaCapaEspera ();
					$("#idCuponNum").val('');
					$("#idCupon").val('');
					//procesarRespuestaAnulacion (resultado);
				},
				type: "GET"
			});	
		}
		
		/**
		 * Oculta el mensaje de estado de la contratacion
		 */
		function ocultarMsgContratacion (){
		    $('#panelEstadoContratacion').hide();
		    $('#overlay').hide();
		}   
		
		/**
		 * Limpia y oculta los datos variables del mensaje de estado de la contratacion
		 */
		function limpiarMsgContratacion () {
			
			// Limpiar
			$("#estadoPpal").empty();
			$("#estadoCpl").empty();
			$("#tdModPrevPpalCupon").empty();
			$("#tdModPrevPpalEstado").empty();
			$("#tdModPrevCplCupon").empty();
			$("#tdModPrevCplEstado").empty();
			
			// Ocultar
			$("#tr_estadoCpl").hide();
		    $("#siModPrev").hide();
		    $("#tablaModPrev").hide();
		    $("#trModPrevPpal").hide();
		    $("#trModPrevCpl").hide();
		}
		
		function volver(){
			$(window.location).attr('href', 'utilidadesPoliza.html?rand=' + UTIL.getRand() +'&recogerPolizaSesion=true');		
		}
		
		function imprimir(idReduccionCapital)	{
			$("#idReduccionCapital").val(idReduccionCapital);
			$('#print').attr('target', '_blank');
			$("#print").submit();
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

		function pasarDefinitivo(idReduccionCapital){
			if(confirm('&iquest;Est&aacute; seguro de que desea pasar a definitivo el Red. Capital seleccionado?')){
				$.blockUI.defaults.message = '<h4> Pasando a definitivo el Red. Capital seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
				$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
				if (arguments.length > 2){
					//Viene el idpoliza como argumento de la funcion
					$("#idPoliza").val(arguments[2]);
				}		
			    $("#method").val("doPasarDefinitiva");
				$("#id").val(idReduccionCapital);
				$("#main").submit();
			}
		}
		
		function continuarAltaAM () {
			
			// Oculta y limpia el mensaje de estado de contratacion
			ocultarMsgContratacion ();
			limpiarMsgContratacion ();
			
			// Si este hidden esta relleno significa que se esta renovando un cupon caducado
			// Hay que llamar al metodo de edicion del AM
			if ($('#idAnexoCaducado').val() != '') {
				doEditar($('#idAnexoCaducado').val());
			}
			else {
				altaRC();
			}
		}
	
		function altaRC() {
			$("#method").val("doEdita");
			$("#id").val("");
			$.blockUI.defaults.message = '<h4> Procesando petici&oacute;n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			$("#main").submit();
		}
		
		function verAcuseRecibo(idReduccionCapital){
			$("#method").val("doVerRecibo");
			$("#id").val(idReduccionCapital);
			$("#main").submit();
		}
				
/*		function informacion(idReduccionCapital){
			alert("edita");	
			$("#method").val("doEdita");
			$("#id").val(idReduccionCapital);
			if (arguments.length > 1){
				//Viene el idpoliza como argumento de la funcion
				$("#idPoliza").val(arguments[1]);
			}
			$.blockUI.defaults.message = '<h4> Procesando petici�n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			$("#main").submit();
		}
*/