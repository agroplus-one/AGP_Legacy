	
	function datosInformes(){
	
		var frm = document.getElementById('main3');
		frm.method.value = 'doConsulta';	
		$("#redireccion").val('datoInformes');
		$("#origenLlamada").val('clasificacionRuptura');
		$('#main3').validate().cancelSubmit = true;
		$('#main3').submit(); 
		
	}
	
	function condiciones(){
	
		var frm = document.getElementById('main3');
		frm.method.value = 'doConsulta';	
		$("#redireccion").val('condiciones');
		$("#origenLlamada").val('clasificacionRuptura');
		$('#main3').validate().cancelSubmit = true;
		$('#main3').submit(); 
			
	}
	
	function volver(){
		
		var frm = document.getElementById('main3');
		frm.method.value = 'doConsulta';
		$("#redireccion").val('informes');
		$("#origenLlamada").val('');
		$('#recogerInformeSesion').val("true");
		$('#main3').validate().cancelSubmit = true;
		$('#main3').submit(); 
	}
	
	

	function generar(){
	
		
		var frm = document.getElementById('main3');
		var frmGen = document.getElementById('generarInformeForm');
		frmGen.idInforme.value = frm.idInforme.value;
		frmGen.method.value = 'doGenerar';
		$('#generarInformeForm').attr('target', '_blank');
		$('#generarInformeForm').submit();
	}

	
	
	function borrar(idClasifRupt,iddatoInforme,permitidoOcalculado){
	
		$('#main3').validate().cancelSubmit = true;
		if(confirm('¿Está seguro de que desea eliminar esta Clasificación ?')){
			var frm = document.getElementById('main3');
			frm.method.value = 'doBaja';	
			
			$('#permitidOCalculado').val(permitidoOcalculado);
	   		$('#id').val(idClasifRupt);
	   		$('#origenLlamada').val("borrar");
			$("#main3").submit();	
		}	
	}
	
	function alta(){
	
		var frm = document.getElementById('main3');
	    frm.method.value = 'doAlta';
	    listaCampoVal =  $('#listaCampo').val();
	    var strDatoInforme;
	    if(listaCampoVal.indexOf("1") == 0){
	    	strDatoInforme	= listaCampoVal.substr(listaCampoVal.indexOf("1")+2,listaCampoVal.length);
		   	$("#permitidOCalculado").val(1);
				}
		else if(listaCampoVal.indexOf("2") == 0){
			strDatoInforme	= listaCampoVal.substr(listaCampoVal.indexOf("2")+2,listaCampoVal.length);	
		     $("#permitidOCalculado").val(2);
		    }
	    $("#idDatoInforme").val(strDatoInforme);
	    $("#id").val('');
	    $('#main3').submit();
	}
	
	function editar(idClasifRupt,sentido,ruptura,iddatoInforme,permitidoOcalculado){
	
		limpiaAlertas();
		$('#idDatoInforme').val(iddatoInforme);
		$('#permitidOCalculado').val(permitidoOcalculado);
		$('#listaCampo').val($('#permitidOCalculado').val()+"-"+$('#idDatoInforme').val());
	    $('#sentido').val(sentido);
	    $('#ruptura').val(ruptura);
	    $('#id').val(idClasifRupt);
	    if(permitidoOcalculado == 1){
	    	$("#modificarValidCalculado").val('true');
		}else if(permitidoOcalculado ==2){
			$("#modificarValidCalculado").val('false');
		}
	    $('#btnModificar').show();
	  	}
	
	function visualizar(idClasifRupt,sentido,ruptura,iddatoInforme,permitidoOcalculado){
	
		limpiaAlertas();
		$('#idDatoInforme').val(iddatoInforme);
		$('#permitidOCalculado').val(permitidoOcalculado);
		$('#listaCampo').val($('#permitidOCalculado').val()+"-"+$('#idDatoInforme').val());
	    $('#sentido').val(sentido);
	    $('#ruptura').val(ruptura);
	    $('#id').val(idClasifRupt);
		if(permitidoOcalculado == 1){
			$("#modificarValidCalculado").val('true');
		}
		$('#btnModificar').hide();
	}
	
	
	function modificar(){
		
		limpiaAlertas();
		var frm = document.getElementById('main3');
	    frm.method.value = 'doModificacion';
	    listaCampoVal =  $('#listaCampo').val();
	    
	    if(listaCampoVal.indexOf("1") == 0){
	    	strDatoInforme	= listaCampoVal.substr(listaCampoVal.indexOf("1")+2,listaCampoVal.length);
		    $("#permitidOCalculado").val(1);
		}
		else if(listaCampoVal.indexOf("2") == 0){
			strDatoInforme	= listaCampoVal.substr(listaCampoVal.indexOf("2")+2,listaCampoVal.length);	
		    $("#permitidOCalculado").val(2);
		}
	    $("#idDatoInforme").val(strDatoInforme);
	    if(($("#modificarValidCalculado").val() == "true" & $("#permitidOCalculado").val() =="1") || ($("#modificarValidCalculado").val() == "false" & $("#permitidOCalculado").val() =="2")){
	    	$('#main3').submit();
	    }else{
	    	$('#panelAlertasValidacion').html("No se puede cambiar un campo de tipo permitido a calculado o viceversa.");	
		    $('#panelAlertasValidacion').show();			    
	    }
	    
	}
	
	function limpiar(){
		
		limpiaAlertas();
		$('#sentido').selectOptions('');
		$('#ruptura').selectOptions('');
		$('#listaCampo').selectOptions('');
		$('#btnModificar').hide();
		jQuery.jmesa.removeAllFiltersFromLimit('mtoConsultaClasificacionRuptura');
		onInvokeAction('mtoConsultaClasificacionRuptura','clear');			
	}
	
	
	function onInvokeAction(id) {
		
		var to=document.getElementById("adviceFilter");
		to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
	    $.jmesa.setExportToLimit(id, '');
	    var parameterString = $.jmesa.createParameterStringForLimit(id);
	    var frm = document.getElementById('main3');
	    $.get('mtoClasificacionRuptura.run?ajax=true&idInforme='+$("#idInforme").val() +'&'+ parameterString, function(data) {
	    $("#grid").html(data)
		});
	}
	
	function comprobarCampos(){
		
		jQuery.jmesa.removeAllFiltersFromLimit('mtoConsultaClasificacionRuptura');
		var resultado = false;
		
		if ( $("#idDatoInforme").val() != ''){
			jQuery.jmesa.addFilterToLimit('mtoConsultaClasificacionRuptura','id.iddatoInforme',  $("#idDatoInforme").val());
			resultado = true;
		}       	
		if ($('#sentido').val() != ''){
			jQuery.jmesa.addFilterToLimit('mtoConsultaClasificacionRuptura','sentido', $('#sentido').val());
			resultado = true;
		}
		if ($('#ruptura').val() != ''){
			jQuery.jmesa.addFilterToLimit('mtoConsultaClasificacionRuptura','ruptura', $('#ruptura').val());
		 	resultado = true;
		 } 
		 
		return resultado;
	}
	
	function consultar(){
		
		limpiaAlertas();
		var strDatoInforme;
		listaCampoVal =  $('#listaCampo').val();
		
		if(listaCampoVal.indexOf("1") == 0){
			strDatoInforme	= listaCampoVal.substr(listaCampoVal.indexOf("1")+2,listaCampoVal.length);
		}
		else if(listaCampoVal.indexOf("2") == 0){
		 	strDatoInforme	= listaCampoVal.substr(listaCampoVal.indexOf("2")+2,listaCampoVal.length);	
		}
		else {
			strDatoInforme = '';
		}
		
	    $("#idDatoInforme").val(strDatoInforme);
	    
		comprobarCampos();
		$("#btnModificar").hide();
		onInvokeAction('mtoConsultaClasificacionRuptura','filter');
	}
	function onLoad(){
		
		if($("#id").val() != ""){			
   			$("#btnModificar").show();
   			
  		}else{
  			$("#btnModificar").hide();
  		
  		}
  		if($("#permitidOCalculado").val() != "" && $("#idDatoInforme").val() != ""){			
   			$('#listaCampo').val($("#permitidOCalculado").val()+"-"+ $("#idDatoInforme").val());
   			
  	}
	}
	
	 function limpiaAlertas(){
	 	
	 	$("#panelInformacion").val('');
		$("#panelInformacion").hide();
		$("#panelAlertasValidacion").val('');
		$("#panelAlertasValidacion").hide();	
		$("#panelAlertas").val('');
		$("#panelAlertas").hide();
	 	
	 }
	 
	 function listaCampoChange(){
	 	listaCampoVal =  $('#listaCampo').val();
	    
	    if(listaCampoVal.indexOf("1") == 0){
	        $("#permitidOCalculado").val(1);
	        $("#modificarValidCalculado").val() == "true";
		}
		else if(listaCampoVal.indexOf("2") == 0){
			$("#permitidOCalculado").val(2);
			$("#modificarValidCalculado").val() == "false";
		}
	 	
	 }
	 