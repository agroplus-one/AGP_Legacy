
		
		
		//* Mantener checks *//
		function onClickInCheck2(idCheck){
			if(idCheck){
				var __aux = idCheck.split("_");
				var __ids = $('#idsRowsChecked').val();
				if (document.getElementById(idCheck).checked == true){
					addCheck2(__ids, __aux[1]);
				}else{
					subtractCheck2(__ids, __aux[1]);
				}
			}
		}

		function addCheck2(ids, check){
			if(ids != null){
				if (ids.substring(ids.length-1, ids.length) == ';'){
					ids = ids + check;
				}else{
					ids = ids + ";" + check;
				}
			}
			
			var frm = document.getElementById('main3');
			frm.idsRowsChecked.value = ids;
			//var frm2 = document.getElementById('pasarADefinitivaMultiple');
			//frm2.idsRowsChecked.value = ids;
		}

		function subtractCheck2(ids, check){
			var newList = "";
			var frm = document.getElementById('main3');
			
			// DAA 14/05/2012
			if(frm.checkTodo.value=="true"){
				frm.checkTodo.value="";
				var frmcheck = document.getElementById('frmcheck');
				frmcheck.checkTodo.checked=false;
				ids=frm.idsRowsChecked.value;
			}
			
			if(ids != null){
				var array_ids = ids.split(';');
				for(var i = 0; i < array_ids.length; i++){
					if(array_ids[i] != check && array_ids[i] != "" && array_ids[i] != null && array_ids[i] != undefined){
						newList = newList + array_ids[i] + ";";
					}
				}
			}
			
			var frm = document.getElementById('main3');
			frm.idsRowsChecked.value = newList;
			// var frm2 = document.getElementById('pasarADefinitivaMultiple');
			// frm2.idsRowsChecked.value = newList;
		}
		
		// para mantener los checks al paginar (llamado desde displaytagAjaxSelectedRow.js)
		function check_checks(ids){
			if(ids != null){
				var array_ids = ids.split(';');
				for(var i = 0; i < array_ids.length; i++){
					var idCheck = "checkParcela_" + array_ids[i];
					$('#' + idCheck).attr('checked',true);
				}
			}
		}
		
		function cambioMasivo() {		
			// Lista de id de póliza separados por ';'
			var ids = $('#idsRowsChecked').val();
			// Si se ha marcado alguna póliza
			if (ids !=null && ids.length>0) {
				//limpiarCambioMasivoUsuarios();
				//$('#listaIdsMarcados_cm').val(listaIdsMarcados);
				inicializarFechas();
				$('#overlayCambioMasivo').show();
				$('#panelCambioMasivoUsuarios').show();
			}else { // Si no se ha marcado ninguna comisión se muestra el aviso
				$('#txt_info').hide();
				$('#divAviso').show();
				$('#txt_info_none').show();
				$('#overlay').show();
			}
		}
		
		
		function cerrarPopUp(){
			$('#divAviso').fadeOut('normal');
			$('#txt_info_gp').hide();
			$('#txt_info').hide();		
			$('#txt_info_none').hide();
			$('#txt_info_DistintaEnt').hide();
			$('#txt_info_check_multiple').hide();
			$('#overlay').hide();
		}
		
		
		function marcar_todos(){			
			var frm = document.getElementById('main3');						
			$("input[type=checkbox]").each(function() { 				        		    
		    	$(this).attr('checked',true);
			});
			frm.checkTodo.value="true";
			frm.idsRowsChecked.value=frm.polizasString.value;		
		}
				
		function desmarcar_todos() {			
			var frm = document.getElementById('main3');
			if(frm.checkTodo.value =="true"){
				$("input[type=checkbox]").each(function() { 				        		    
		    		$(this).attr('checked',false);
		  		});		  		
			    frm.checkTodo.value="false";
			    frm.idsRowsChecked.value="";	
			}
		}
		
		function limpiarPaneles(){
			$('#txt_mensaje_cm').html("");
			$('#txt_mensaje_cm').hide();
		}
		
		function limpiarCambioMasivoUsuarios(){
			$('#pctgeneralentidadCM').val('');
			$('#pctadquisicionCM').val('');
			$('#pctadministracionCM').val('');
			$('#fechaEfectoCM').val('');
			$('#txt_mensaje_cm').hide();
			inicializarFechas();
			$('#panelAlertasValidacion_cm').val('');		
			$("#panelAlertasValidacion_cm").hide();
			$("#campoObligatorio_pctgeneralentidadCM").hide();
			$("#campoObligatorio_pctadministracionCM").hide();
			$("#campoObligatorio_pctadquisicionCM").hide();
			$("#campoObligatorio_tx_fechaEfectoCM").hide();
			$("#main").validate().cancelSubmit = false;
			if ($('#main').valid()){
				$("#main").validate().cancelSubmit = true;
			}
		}
		
		//popupCambioMasivo
		function cerrarCambioMasivoUsuarios(){
			limpiarCambioMasivoUsuarios();
			$('#panelCambioMasivoUsuarios').hide();
			$('#overlayCambioMasivo').hide();
		}
		
		// PANEL BAJA		
		function cerrarBaja(){			
			$('#panelBajaComisiones').hide();
			$('#overlayBaja').hide();
		}
		
		function aplicarBaja(){
			$('#mainBaja').submit();
		}
		
		 function aplicarCambioMasivoUsuarios(){
			 $('#txt_mensaje_cm').html("");
				$('#txt_mensaje_cm').show();
			 if ($('#main').valid()){
					if($('#pctgeneralentidadCM').val() == '' && $('#pctadministracionCM').val() =='' && $('#pctadquisicionCM').val() ==''){
						$('#txt_mensaje_cm').html("Debe seleccionar al menos un porcentaje");
						$('#txt_mensaje_cm').show();
					}else{
						$('#txt_mensaje_cm').html("");
						$('#txt_mensaje_cm').hide();
						jConfirm('¿Desea realizar el cambio masivo de los registros seleccionados??', 'Diálogo de Confirmación', function(r) {
							if (r){
								$('#pctgeneralentidadCM_sel').val($('#pctgeneralentidadCM').val());
								$('#pctadquisicionCM_sel').val($('#pctadquisicionCM').val());
								$('#pctadministracionCM_sel').val($('#pctadministracionCM').val());	
								$('#fechaEfectoCM_sel').val($('#tx_fechaEfectoCM').val());
								$('#planFiltro').val($('#plan').val());
								$('#lineaFiltro').val($('#linea').val());
								$('#desc_lineaFiltro').val($('#desc_linea').val());
								$('#pctComFiltro').val($('#pctgeneralentidad').val());
								$('#pctAdqFiltro').val($('#pctadquisicion').val());
								$('#pctAdmFiltro').val($('#pctadministracion').val());					
								$('#fecEfectoFiltro').val($('#tx_fechaEfecto').val());
								$('#entmediadoraFiltro').val($('#entmediadora').val());
								$('#subentmediadoraFiltro').val($('#subentmediadora').val());
								$('#grupoNegocioFiltro').val($('#grupoNegocio').val());
								
								var ids = $('#idsRowsChecked').val();
								$('#listaIdsMarcados_cm').val(ids);
								
								$('#main').submit();
								//$.blockUI.defaults.message = '<h4> Solicitando datos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
							      //$.blockUI({ 
							        //  overlayCSS: { backgroundColor: '#525583'},
							          //baseZ: 2000
							      //});
								cerrarCambioMasivoUsuarios();
							}
						});
					}
			 	}
			}