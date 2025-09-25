$(function(){
	$("#grid").displayTagAjax();
	check_checks($('#idsRowsChecked').val());
});
		
$(document).ready(function(){
		 
		 	var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
			document.getElementById("main3").action = URL;  
			
			if ($('#activarModoModificar').val() == 'true'){
				$("#btnAlta").hide();
				$("#btnModif").show();
			}
			
			if ($('#limpiarReplicar').val() == 'true'){
				limpiarReplicar();
			}
			
//			<c:if test="${activarModoModificar == 'true'}">	
//			//$('#method').val("doEdita");
//			$("#btnAlta").hide();
//			$("#btnModif").show();
//			//$('#txt_porcentajeMediador').attr("readonly", true);
//			</c:if>	
			
			inicializarFechas();

			//Inicializar los calendarios de los campos fecha
			Zapatec.Calendar.setup({
			    firstDay          : 1,
			    weekNumbers       : false,
			    showOthers        : true,
			    showsTime         : false,
			    timeFormat        : "24",
			    step              : 2,
			    range             : [1900.01, 2999.12],
			    electric          : false,
			    singleClick       : true,
			    inputField        : "tx_fechaEfecto",
			    button            : "btn_fechaEfecto",
			    ifFormat          : "%d/%m/%Y",
			    daFormat          : "%d/%m/%Y",
			    align             : "Br"			        	        
			});
			
			//Inicializar los calendarios de los campos fecha
			Zapatec.Calendar.setup({
			    firstDay          : 1,
			    weekNumbers       : false,
			    showOthers        : true,
			    showsTime         : false,
			    timeFormat        : "24",
			    step              : 2,
			    range             : [1900.01, 2999.12],
			    electric          : false,
			    singleClick       : true,
			    inputField        : "tx_fechaEfectoBaja",
			    button            : "btn_fechaEfectoBaja",
			    ifFormat          : "%d/%m/%Y",
			    daFormat          : "%d/%m/%Y",
			    align             : "Br"			        	        
			});
			
			Zapatec.Calendar.setup({
			    firstDay          : 1,
			    weekNumbers       : false,
			    showOthers        : true,
			    showsTime         : false,
			    timeFormat        : "24",
			    step              : 2,
			    range             : [1900.01, 2999.12],
			    electric          : false,
			    singleClick       : true,
			    inputField        : "tx_fechaEfectoCM",
			    button            : "btn_fechaEfectoCM",
			    ifFormat          : "%d/%m/%Y",
			    daFormat          : "%d/%m/%Y",
			    align             : "Br"			        	        
});
			
			
		
		 	$('#main3').validate({
		 		 errorLabelContainer: "#panelAlertasValidacion",
		 		 onfocusout: function(element) {
		   			if(($('#method').val() == "doGuardarParametrosComisiones") && !this.checkable(element) && (element.name in this.submitted || !this.optional(element)) ) {
						this.element(element);
					}
	   			 },
   				 wrapper: "li",
   				 highlight: function(element, errorClass) {
				 	$("#campoObligatorio_" + element.id).show();
  			     },
  				 unhighlight: function(element, errorClass) {
					$("#campoObligatorio_" + element.id).hide();
  				 },
   				 rules: {	
   				 	"linea.codplan"  : 	{required: true, digits: true,minlength: 4},		 	
   				 	"linea.codlinea" : 	{required: true, digits: true,linea_noGen: true},
   				 	"pctgeneralentidad" : 	{required: true, pctgeneralent: true, pctrangogen: true, digitspctgen: true}, //digits: true,
   				    "pctadquisicion" :  {requiredAdq: true,pctrangoAdq: true},
   				 	"pctadministracion" :  {required: true,pctrangoAdm: true},
   				 	"fechaEfecto" 	 :	{requiredFecEfecto:true,dateITA: true, comprobarFechaE: true},
   				 	"grupoNegocio.grupoNegocio"    : {required: true},
   				 	"subentidadMediadora.id.codentidad":{validaSubentidadMediadora:true}
   				 
				 },
				 messages: {
				 	"linea.codplan" 	: {required: "El campo Plan es obligatorio.",digits: "El campo Plan solo admite dï¿½gitos.",minlength: "El campo Plan debe contener 4 dï¿½gitos"},		 	
   				 	"linea.codlinea" 	: {required: "El campo Lï¿½nea es obligatorio.",digits: "El campo Lï¿½nea solo admite dï¿½gitos.",linea_noGen: "El campo lï¿½nea no puede ser la genï¿½rica"},		 	
   				 	"pctgeneralentidad" : {required: "El campo % Comisiï¿½n mï¿½ximo es obligatorio.",pctrangogen: "El campo % Comisiï¿½n mï¿½ximo debe contener un nï¿½mero entre 0 y 100",digitspctgen: "El campo comisiï¿½n mï¿½ximo solo admite dï¿½gitos.",pctgeneralent: "El campo % Comisiï¿½n mï¿½ximo debe contener un nï¿½mero entre 0 y 999.99",pctsuma:"Los campos porcentaje entidad y porcentaje Administraciï¿½n deben sumar 100"}, // digitspctgen: "El campo comisiï¿½n mï¿½ximo solo admite dï¿½gitos.",
   				 	"pctadquisicion"    : {requiredAdq: "El campo % Adquisiciï¿½n es obligatorio.",pctrangoAdq: "El campo % Adquisiciï¿½n debe contener un nï¿½mero entre 0 y 999.99"},
   				 	"pctadministracion" : {required: "El campo % Administraciï¿½n es obligatorio.",pctrangoAdm: "El campo % Administraciï¿½n debe contener un nï¿½mero entre 0 y 999.99"},
   				 	"fechaEfecto"		: {requiredFecEfecto: "El campo Fecha efecto es obligatorio.",dateITA: "El formato del campo Fecha efecto debe ser dd/mm/YYYY",comprobarFechaE:"La Fecha Efecto debe ser superior o igual a la fecha actual"},
   				 	"grupoNegocio.grupoNegocio"		: {required: "El campo grupo de negocio es obligatorio."},
   				 	"subentidadMediadora.id.codentidad": {validaSubentidadMediadora: "El campo subentidadMediadora estï¿½ incompleto."}
				 }
		 	});
		 	
		 	$('#main').validate({ // formulario cambio masivo
		 		 errorLabelContainer: "#panelAlertasValidacion_cm",
		 		 onfocusout: function(element) {
		   			if(($('#method').val() == "doGuardarParametrosComisiones") && !this.checkable(element) && (element.name in this.submitted || !this.optional(element)) ) {
						this.element(element);
					}
	   			 },
  				 wrapper: "li",
  				 highlight: function(element, errorClass) {
				 	$("#campoObligatorio_" + element.id).show();
 			     },
 				 unhighlight: function(element, errorClass) {
					$("#campoObligatorio_" + element.id).hide();
 				 },
  				 rules: {
  					"pctgeneralentidadCM" : {pctrangogenCM: true, digitspctgenCM: true},
  					"pctadquisicionCM" : {pctrangoAdqCM: true},
  					"pctadministracionCM" : {pctrangoAdmCM: true},
  				 	"fechaEfectoCM" :	{required: true,dateITA: true, comprobarFechaE: true}
  				 
				 },
				 messages: {
					"pctgeneralentidadCM" : {pctrangogenCM: "El campo % Comisiï¿½n mï¿½ximo debe contener un nï¿½mero entre 0 y 999.99",digitspctgenCM: "El campo comisiï¿½n mï¿½ximo solo admite dï¿½gitos."},
					"pctadquisicionCM" 	  : {pctrangoAdqCM: "El campo % Adquisiciï¿½n debe contener un nï¿½mero entre 0 y 999.99"},
					"pctadministracionCM" : {pctrangoAdmCM: "El campo % Administraciï¿½n debe contener un nï¿½mero entre 0 y 999.99"},
  				 	"fechaEfectoCM" : 	    {required: "El campo Fecha efecto es obligatorio.", dateITA: "El formato del campo Fecha efecto debe ser dd/mm/YYYY",comprobarFechaE:"La Fecha Efecto debe ser superior o igual a la fecha actual"}
				 }
		 	});
		 	
		 	$('#mainBaja').validate({ // formulario baja
		 		 errorLabelContainer: "#panelAlertasValidacion_BJ",
		 		 onfocusout: function(element) {
		   			if(($('#method').val() == "doBorrarParametrosComisiones") && !this.checkable(element) && (element.name in this.submitted || !this.optional(element)) ) {
						this.element(element);
					}
	   			 },
 				 wrapper: "li",
 				 highlight: function(element, errorClass) {
				 	$("#campoObligatorio_BJ" + element.id).show();
			     },
				 unhighlight: function(element, errorClass) {
					$("#campoObligatorio_BJ" + element.id).hide();
				 },
				 rules: {					 	
 				 	"fechaEfectoBaja" :	{required: true,dateITA: true, comprobarFechaE: true}
 				 
				 },
				 messages: {
 				 	"fechaEfectoBaja" : {required: "El campo Fecha efecto es obligatorio.", dateITA: "El formato del campo Fecha efecto debe ser dd/mm/YYYY",comprobarFechaE:"La Fecha Efecto debe ser superior o igual a la fecha actual"}
				 }
		 	});
		 	
		 	$('#replicarCultivos').validate({ // formulario cambio masivo
		 		 errorLabelContainer: "#panelAlertasValidacion_replica",
		 		 onfocusout: function(element) {
		   			this.element(element);
	   			 },
				 wrapper: "li",
				 highlight: function(element, errorClass) {
				 	$("#campoObligatorio_" + element.id).show();
			     },
				 unhighlight: function(element, errorClass) {
					$("#campoObligatorio_" + element.id).hide();
				 },
				 rules: {
					"plan_origen" : {required: true},
					"linea_origen" : {required: true},
					"plan_destino" : {required: true},
					"linea_destino" : {required: true}
				 },
				 messages: {
					"plan_origen" : {required: "El campo plan_origen es obligatorio"},
					"linea_origen" : {required: "El campo linea_origen es obligatorio"},
					"plan_destino" : {required: "El campo plan_destino es obligatorio"},
				 	"linea_destino" : {required: "El campo linea_destino es obligatorio"}
				 }
		 	});
		 	
		 	//comprueba la fecha inicial con la del sistema
		 	//element--> la fechaIni
		 	jQuery.validator.addMethod("comprobarFechaE", function(value, element) {					
		 		return (this.optional(element) || UTIL.fechaMayorOIgualQueFechaActual(element.value));
		 	});
		 	
		 	jQuery.validator.addMethod("pctrango", function(value, element, params) {
				var isvalid = false;
			
				value = value.replace(",",".");
				if(!isNaN(value)){
					if(value >=0 && value <= 999.99)
						isvalid = true;
					else
						isvalid = false;
				}else
					isvalid = false;
				return (this.optional(element) || isvalid);	
			});
			
	
 	jQuery.validator.addMethod("digitspctgen", function(value, element, params) {
		var isvalid = false;	
		if ($('#plan').val() < 2015){			
			value = value.replace(",",".");
			if(!isNaN(value)){	
				isvalid = true;		
			}else
				isvalid = false;
			
		}else{
			isvalid = true;
		}
		return isvalid;
	});
 	
 	jQuery.validator.addMethod("digitspctgenCM", function(value, element, params) {
		var isvalid = false;				
		value = value.replace(",",".");
		if(!isNaN(value)){	
			isvalid = true;		
		}else
			isvalid = false;
		return isvalid;
	});
 	
 	
 	jQuery.validator.addMethod("pctrangogen", function(value, element, params) {
		var isvalid = false;
		
		if ($('#plan').val() < 2015){			
			value = value.replace(",",".");
			if(!isNaN(value)){
				if(value >=0 && value <= 100)
					isvalid = true;
				else
					isvalid = false;
			}else
				isvalid = false;
			
		}else{
			isvalid = true;
		}
		return isvalid;
	});	 	
 	
 	jQuery.validator.addMethod("pctrangogenCM", function(value, element, params) {
		var isvalid = false;
		value = value.replace(",",".");
		if(!isNaN(value)){
			if(value >=0 && value <= 100)
				isvalid = true;
			else
				isvalid = false;
		}else
			isvalid = false;
		return isvalid;
	});	 
 	
 	jQuery.validator.addMethod("pctgeneralent", function(value, element, params) {
		var isvalid = false;
		
		if ($('#plan').val() >= 2015){			
			value = value.replace(",",".");
			if(!isNaN(value)){
				if(value >=0 && value <= 999.99)
					isvalid = true;
				else
					isvalid = false;
			}else
				isvalid = false;
			
		}else{
			isvalid = true;
		}
		return isvalid;
	});	 	
	
		 	
	jQuery.validator.addMethod("pctrangoAdq", function(value, element, params) {
		var isvalid = false;
		
		if ($('#plan').val() >= 2015){			
			value = value.replace(",",".");
			if(!isNaN(value)){
				if(value >=0 && value <= 999.99)
					isvalid = true;
				else
					isvalid = false;
			}else
				isvalid = false;
			
		}else{
			isvalid = true;
		}
		return isvalid;
	});
	
	jQuery.validator.addMethod("pctrangoAdqCM", function(value, element, params) {
		var isvalid = false;	
		value = value.replace(",",".");
		if(!isNaN(value)){
			if(value >=0 && value <= 999.99)
				isvalid = true;
			else
				isvalid = false;
		}else
			isvalid = false;
		return isvalid;
	});
			
	jQuery.validator.addMethod("pctrangoAdm", function(value, element, params) {
		var isvalid = false;
		
		if ($('#plan').val() >= 2015){			
			value = value.replace(",",".");
			if(!isNaN(value)){
				if(value >=0 && value <= 999.99)
					isvalid = true;
				else
					isvalid = false;
			}else
				isvalid = false;
			
		}else{
			isvalid = true;
		}
		return isvalid;
	});
	
	jQuery.validator.addMethod("pctrangoAdmCM", function(value, element, params) {
		var isvalid = false;		
		value = value.replace(",",".");
		if(!isNaN(value)){
			if(value >=0 && value <= 999.99)
				isvalid = true;
			else
				isvalid = false;
		}else
			isvalid = false;
		return isvalid;
	});
			
	jQuery.validator.addMethod("requiredAdq", function(value, element, params) {
		var isvalid = false;		
		if ($('#plan').val() >= 2015){	
			if($('#pctadquisicion').val() != null && $('#pctadquisicion').val() != ""){					
					isvalid = true;	
			}else{
				isvalid = false;
			}
		}else{
			isvalid = true;
		}
		return isvalid;
	});
	
	jQuery.validator.addMethod("requiredAdm", function(value, element, params) {
		var isvalid = false;		
		if ($('#plan').val() >= 2015){					
			if($('#pctadministracion').val() != null && $('#pctadministracion').val() != ""){					
					isvalid = true;	
			}else{
				isvalid = false;
			}
		}else{
			isvalid = true;
		}
		return isvalid;
	});
		
	jQuery.validator.addMethod("linea_noGen", function(value, element, params) {
		var isvalid = false;		
		if ($('#plan').val() >= 2015){	
			if ($('#linea').val() == 999){	
				isvalid = false;
			}else{
				isvalid = true;
			}
		}else{
			isvalid = true;
		}
		return isvalid;
	});

	jQuery.validator.addMethod("requiredFecEfecto", function(value, element, params) {
		var isvalid = false;		
		if ($('#plan').val() >= 2015){	
			if($('#tx_fechaEfecto').val() != null && $('#tx_fechaEfecto').val() != ""){					
					isvalid = true;	
			}else{
				isvalid = false;
			}
		}else{
			isvalid = true;
		}
		return isvalid;
	});
	
	jQuery.validator.addMethod("validaSubentidadMediadora", function(value, element, params) {
		var isvalid = true;
		var form = document.getElementById("main3");		 
		var codEnt =form.entmediadora.value;
		var codSubEnt=form.subentmediadora.value;
		 if((codEnt!='' || codSubEnt!='') &&(codEnt=='' || codSubEnt=='') ){
			isvalid=false;
		 }
		return isvalid;
	});
	
	
});
	


//	if(${altaLinea999}){
	if ($('#altaLinea999').val() == 'true'){
		$('#divAviso').fadeIn('normal');
		$('#overlay').show();
		$('#plan').attr('readonly',true);
		$('#linea').attr('readonly',true);
		$('#btnConsultar').hide();
		$('#btnLimpiar').hide();
	}  
		function cerrarPopUp(){
			$('#divAviso').hide();
			$('#overlay').hide();
		}
		
		// Javascript al restar numeros decimales presenta en ocasiones uno o 2 fallos uno es que coloca o muchos 9999 detras o 00000
		// ha tenido un error de precision restando  0,1 menos por ello tambien esta contemplada esta posibilidad .  
		// en ambos casos de redondea a dos decimales 
		function calcularPctAdm(){
			if ($('#plan').val() < 2015 || $('#plan').val() == ''){					
				 var pctEntidad = parseFloat($('#pctgeneralentidad').val().replace(",","."));
				 if(!isNaN(pctEntidad)){
				 	$('#pctgeneralentidad').val(pctEntidad);
				 	if(pctEntidad <= 100)
				 	{
					 	 pctEntidad = parseFloat(pctEntidad);
					 	 var pctAdm = parseFloat(100) - parseFloat(pctEntidad);					 	
					 	var resultado = Math.round(pctAdm*100)/100;				
					 	 var spctAdm = new String(resultado);
					 	 if (spctAdm.length > 4  ) 
					 	 {
					 	 	var quintoDecimal = "";
					 	 	if (pctEntidad > 10)
					 	 		var quintoDecimal = spctAdm.substring(5,6);
					 	 	if (quintoDecimal != '0' && quintoDecimal != '')
					 	 	{
					 	 		pctAdm = parseFloat(pctAdm);
					 	 		pctAdm = pctAdm + 0.01;
					 	 		spctAdm =  new String(pctAdm);
					 	 		pctAdm = spctAdm.substring(0,4);
					 	 	}
					 		else{
					 			pctAdm = spctAdm.substring(0,5);
					 			pctAdm = parseFloat(pctAdm);
					 		}
					 	 }
						 $('#pctadministracion').val(pctAdm);
						 $('#pctadquisicion').val('');
						 $('#tx_fechaEfecto').val('');
						 
					}
				}else{
				 		$('#pctadministracion').val('');
				}
			}
			 
		}
		
		function modificar(id,plan,linea,lineaseguroid,pctent,pctrga,usuario,
				pctadquisicion,pctadministracion,fecEfecto,descLinea, grupoNeg, entMed, subEntMed){
			$('#id').val(id);
			$('#plan').val(plan);
			$('#linea').val(linea);
			$('#lineaseguroid').val(lineaseguroid);
			$('#pctgeneralentidad').val(pctent);
			$('#pctrga').val(pctrga);
			$('#usuario').val(usuario);
			$('#pctadquisicion').val(pctadquisicion);
			$('#pctadministracion').val(pctadministracion);
			$('#tx_fechaEfecto').val(fecEfecto);
			$('#desc_linea').val(descLinea);
			$('#grupoNegocio').val(grupoNeg);
			$('#entmediadora').val(entMed);
			$('#subentmediadora').val(subEntMed);
			
			//BOTONES
			$('#btnAlta').hide();
			$('#btnModif').show();			
		}
		
		function limpiar(){
			$('#id').val('');
			$('#plan').val('');
			$('#linea').val('');
			$('#desc_linea').val('');			
			$('#pctgeneralentidad').val('');		
			$('#usuario').val('');
			$('#pctadquisicion').val('');
			$('#pctadministracion').val('');
			$('#tx_fechaEfecto').val('');
			$('#grupoNegocio').val('');
			$('#entmediadora').val('');
			$('#subentmediadora').val('');
			$('#procedencia').val('');
			consultar();
		}
		
		function consultar(){
			
			$("#main3").validate().cancelSubmit = true;
			if (validarCamposConsulta()){
				$('#consultando').val("consultando");
				$('#id').val('');
				$('#method').val('doConsultaParam');
				$('#main3').submit();
			}
		}
		
		function volver(){
			 $("#main3").validate().cancelSubmit = true;
			$('#method').val('doConsulta');
			
			$('#id').val('');
			$('#plan').val('');
			$('#linea').val('');
			$('#pctgeneralentidad').val('');
			
			$('#usuario').val('');
			$('#limpiarFiltro').val('false');
			$('#main3').submit();	
			
			
		}
		
		function returnBack()
		{
			
			
			if ($('#procedencia').val() == ''){
				
				limpiar();
			}else{
			
//				<c:if test="${procedencia eq 'incidenciasComisionesUnificadas'}" >
//				
//				 	$(window.location).attr('href', 'incidenciasUnificado.run?rand=' + UTIL.getRand() + 
//							'&origenLlamada=parametrosComisiones'+'&idFicheroUnificado='+$('#idFichero').val()+	
//							'&method=doConsulta');	
//				</c:if>	
//				
//				<c:if test="${procedencia ne 'incidenciasComisionesUnificadas'}" >
//				
//					$(window.location).attr('href', 'incidencias.html?rand=' + UTIL.getRand() + 
//							'&idFichero='+$('#idFicheroComisiones').val()+
//							'&tipo='+$('#tipoFicheroComisiones').val()+
//							'&codplan='+$('#plan').val()+
//							'&method=doConsulta');	
//				</c:if>	
				
				
				if($('#procedencia').val()=='incidenciasComisionesUnificadas'){
					$(window.location).attr('href', 'incidenciasUnificado.run?rand=' + UTIL.getRand() + 
							'&origenLlamada=parametrosComisiones'+'&idFicheroUnificado='+$('#idFichero').val()+	
							'&method=doConsulta');
				}else{
					$(window.location).attr('href', 'incidencias.html?rand=' + UTIL.getRand() + 
							'&idFichero='+$('#idFicheroComisiones').val()+
							'&tipo='+$('#tipoFicheroComisiones').val()+
							'&codplan='+$('#plan').val()+
							'&method=doConsulta');
				}
				
			}
			
		}
		
		function alta(){
			
			if ($('#plan').val() < 2015 || $('#plan').val() == ''){
				calcularPctAdm();	
			}
			if (validarCamposConsulta()){
				validarAdquisicionFecEfecto();
								
				$('#method').val('doGuardarParametrosComisiones');
				$('#main3').submit();
			}
		}
		
		function editar(){
			limpiaAlertas();
			if ($("#main3").valid()){
				if (validarCamposConsulta()){
					validarAdquisicionFecEfecto();
					$('#gnDesc').val($( "#grupoNegocio option:selected" ).text());
					$('#method').val('doGuardarParametrosComisiones');
					$('#main3').submit();
				}
			}
			
		}
		
		function borrar(id, plan){
			inicializarFechas();		
			$("#panelAlertasValidacion_BJ").hide();
			$("#main3").validate().cancelSubmit = true;
			$('#idBaja').val(id);
			$('#id').val(id);
			if (plan < 2015){
				jConfirm('¿Está seguro que desea eliminar el registro seleccionado?', 'Diálogo de Confirmación', function(r) {
				    if (r == true){
						$('#mainBaja').submit();
					}
				});
			}else{
				$('#overlayBaja').show();				
				$('#panelBajaComisiones').show();
			}
		}
		
		function inicializarFechas(){
			// inizializamos fechaefecto del popupbaja
			var fecha = new Date();
			var dia = fecha.getDate();
			var mes = fecha.getMonth();
			var mes = mes + 1;
			var anio = fecha.getFullYear();
			if (dia < 10){
				dia = "0"+ dia;
			}
			if (mes <10){
				mes = "0" + mes;
			}
			var fecFormateada = dia + "/" + mes + "/" + anio;
			$('#tx_fechaEfectoCM').val(fecFormateada);
			$('#tx_fechaEfectoBaja').val(fecFormateada);
		}
		
		function aplicarBajaUnica(){			
			$('#fFec').val($('#tx_fechaEfectoBaja').val());
			$('#mainBaja').submit();
		}
		
		function verHistorico(id,plan,linea,lineaseguroid,pctent,pctrga,usuario,
				pctadquisicion,pctadministracion,fecEfecto, grupoNeg, entMed, subEntMed){
			$('#idHistorico').val(id);
			$('#planF').val($('#plan').val());
			$('#lineaF').val($('#linea').val());		
			$('#desc_lineaF').val($('#desc_linea').val());
			$('#pctgeneralentidadF').val($('#pctgeneralentidad').val());
			$('#pctadministracionF').val($('#pctadministracion').val());
			$('#pctadquisicionF').val($('#pctadquisicion').val());
			$('#fechaEfectoF').val($('#tx_fechaEfecto').val());
			$('#grupoNegHis').val(grupoNeg);
			$('#entmediadoraHis').val(entMed);
			$('#subentmediadoraHis').val(subEntMed);
			
			$('#historicoForm').submit();
		}
		
		function validarCamposConsulta () {
			$('#panelAlertasValidacion').html("");
			$('#panelAlertasValidacion').hide();
			
			// pctadquisicion
			if ($('#pctadquisicion').val() != ''){ 
			 	var pctadqOk = false;
			 	try {		 	
			 		var auxpctadq =  parseFloat($('#pctadquisicion').val());
			 		if(!isNaN(auxpctadq)){
						$('#pctadquisicion').val(auxpctadq);
						pctadqOk = true;
					}
				}
				catch (ex) {}
				
				// Si ha habido error en la validaciï¿½n muestra el mensaje
				if (!pctadqOk) {
					$('#panelAlertasValidacion').html("Valor para el % adquisiciï¿½n no vï¿½lido");
					$('#panelAlertasValidacion').show();
					return false;
				}
			}
			// pctadministracion
			if ($('#pctadministracion').val() != ''){ 
			 	var pctadmOk = false;
			 	try {		 	
			 		var auxpctadm =  parseFloat($('#pctadministracion').val());
			 		if(!isNaN(auxpctadm)){
						$('#pctadministracion').val(auxpctadm);
						pctadmOk = true;
					}
				}
				catch (ex) {}
				
				// Si ha habido error en la validaciï¿½n muestra el mensaje
				if (!pctadmOk) {
					$('#panelAlertasValidacion').html("Valor para el % administracion no vï¿½lido");
					$('#panelAlertasValidacion').show();
					return false;
				}
			}
			// pctgeneralentidad
			if ($('#pctgeneralentidad').val() != ''){ 
			 	var pctgenOk = false;
			 	try {		 	
			 		var auxpctgen =  parseFloat($('#pctgeneralentidad').val());
			 		if(!isNaN(auxpctgen)){
						$('#pctgeneralentidad').val(auxpctgen);
						pctgenOk = true;
					}
				}
				catch (ex) {}
				
				// Si ha habido error en la validaciï¿½n muestra el mensaje
				if (!pctgenOk) {
					$('#panelAlertasValidacion').html("Valor para el % Comisiï¿½n mï¿½ximo no vï¿½lido");
					$('#panelAlertasValidacion').show();
					return false;
				}
			}
			
			// validamos que para el pan <2015 %comision maximo y %administracion tienen que sumar 100
			if ($('#plan').val() < 2015 || $('#plan').val() == ''){
				if ($('#pctgeneralentidad').val() != '' && $('#pctadministracion').val() != ''){
					var pctEntidad = parseFloat($('#pctgeneralentidad').val().replace(",","."));
					var pctadmin = parseFloat($('#pctadministracion').val().replace(",","."));
					//alert(pctEntidad + pctadmin);
					if (pctEntidad + pctadmin != parseFloat(100)){
						$('#panelAlertasValidacion').html("la suma de % comisiï¿½n mï¿½ximo y % administraciï¿½n tiene que ser 100");
						$('#panelAlertasValidacion').show();
						return false;
					}	
				}
			}
			return true;
		}
		
		function validarAdquisicionFecEfecto () {
			if ($('#plan').val() < 2015){
				$('#pctadquisicion').val('');
				$('#tx_fechaEfecto').val('');
			}
		}
		
		function borrarNombreLinea(){
			$('#desc_linea').val('');
		}
		
		function limpiaAlertas() {
			$('#alerta').val("");
			$("#panelInformacion").hide();
			$("#panelAlertasValidacion").hide();
			$("#panelAlertas").hide();
			$("#panelInformacion").html('');
			$("#panelAlertasValidacion").html('');
			$("#panelAlertas").html('');
			$('#mensaje').val("");
			$("#panelMensajeValidacion").hide();
		    $("#panelMensajeValidacion").html('');
		}
		
		
		
		
		function showReplicarCultivos() {
			// TODO muestra una tabla con codigo plan y linea origen a destino para replicar cultivos
			$('#panelAlertasValidacion_replica').html('');
			$('#panelAlertasValidacion2').hide();
			$('#lineaorigen').val($('#lineaorigenH').val());
			$('#lineanuevo').val($('#lineanuevoH').val());
			$('#divReplicar').fadeIn('normal');
			$('#overlay').show();
		}
		
		
		function replicarCultivos(){
			
			if ($('#replicarCultivos').valid()) {
				
				let plan_re = $('#plan_re').val();
				let linea_re = $('#linea_re').val();
				let planreplica = $('#planreplica').val();
				let lineareplica = $('#lineareplica').val();

				if (!replicaPlanLineaDiferentes(plan_re,planreplica,linea_re, lineareplica)) {
					alert('No se pueden replicar plan/lineas iguales');
					return;
				}
				
				 jConfirm('\u00BFDesea replicar todas las Comisiones para este Plan y L\u00EDnea?', 'Dialogo de Confirmacion', function(r){
						if(r){
							$("#replicarCultivos").submit();
						}
					});
			}

		}
		
		function replicaPlanLineaDiferentes (planReplica, plan, lineaReplica, linea) {	
			if (planReplica == plan && lineaReplica == linea){ 
				return false
			} else { 
				return true; 
			}
		}
		
		function limpiarReplicar() {
			$('#plan_re').val('');
			$('#linea_re').val('');
			$('#desc_linea_re').val('');
			$('#planreplica').val('');
			$('#lineareplica').val('');
			$('#desc_lineareplica').val('');
			
			$('#txt_mensaje_re').html("");
			$('#panelAlertasValidacion_replica').html('');
		}
		
		function cerrarPopUpReplica(){
			limpiarReplicar();
			$('#divAviso').fadeOut('normal');
			$('#divReplicar').fadeOut('normal');
			$('#overlay').hide();
			
		}
		
		function exportarExcel(){
			$("#main3").validate().cancelSubmit = true;
			$('#method').val('doExportarExcel');
			$('#main3').submit();			
		}
		