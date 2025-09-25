$(document).ready(function(){
	
	// Para evitar el cacheo de peticiones al servidor
	var URL = UTIL.antiCacheRand($("#main").attr("action"));
	$("#main").attr("action", URL);
	
	document.getElementById('entidad').focus();
	
	$('#main').validate({
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		highlight: function(element, errorClass) {
			$("#campoObligatorio_" + element.id).show();
		},
		unhighlight: function(element, errorClass) {
			$("#campoObligatorio_" + element.id).hide();
		},
		rules: {
			"tomador.id.codentidad":{grupoEnt: true}
		},
		messages: {
			"tomador.id.codentidad":{grupoEnt: "La Entidad seleccionada no pertenece al grupo de Entidades del usuario"}
		}
	});
		
	jQuery.validator.addMethod("grupoEnt", function(value, element, params) { 
		var codentidad = $('#entidad').val();
		if($('#grupoEntidades').val() == ""){
			return true;
		}else if (codentidad != ""){
			var grupoEntidades = $('#grupoEntidades').val().split(',');
			var encontrado = false;
			for(var i=0;i<grupoEntidades.length;i++){
				if(grupoEntidades[i] == codentidad){
					encontrado = true;
					break;
				}
			}
		}else
			return true;
		return 	encontrado;	
	});	
        
});

function modificar(id, codEntidad, plan, linea, cifTomador, idColectivo, 
                   dcColectivo, nomColectivo, entMediadora, subEntMediadora, 
                   colCalculo, primerPago, segundoPago, fecPrimerPago, 
                   fecSegundoPago,activo,desc_entidad,desc_linea,
                   desc_tomador,desc_entmediadora,desc_subentmediadora){
                   
	/*Limpiamos descripciones cada vez q seleccionamos una fila*/
	$("#desc_entidad").val(desc_entidad);
	$("#desc_linea").val(desc_linea);
	$("#desc_tomador").val(desc_tomador);
	$("#desc_entmediadora").val(desc_entmediadora);
	$("#desc_subentmediadora").val(desc_subentmediadora);
	var frm = document.getElementById('main');
	frm.id.value = id;
	frm.entidad.value = codEntidad;
	frm.entidad.readOnly = 'true';
	frm.plan.value = plan;
	frm.linea.value = linea;			
	frm.tomador.value = cifTomador;			
	frm.colectivo.value = idColectivo;			
	frm.dc.value = dcColectivo;	
	
	if(activo == '1'){
		document.getElementById('activo').innerHTML = 'SI';
	}else{
		document.getElementById('activo').innerHTML = 'NO';
	}
	
	frm.nomcolectivo.value = nomColectivo;			
	frm.entmediadora.value = entMediadora;			
	frm.subentmediadora.value = subEntMediadora;
	frm.pctdescuentocol.value = colCalculo;			
	frm.pctprimerpago.value = primerPago;			
	frm.pctsegundopago.value = segundoPago;			
	frm.fechaIni.value = fecPrimerPago;			
	frm.fechaFin.value = fecSegundoPago;
	//Botones
}		

function cargarColectivo(idColectivo) {				
	generales.enviarForm('cargar','cargar_id',idColectivo);
}

function limpiar(){
	$('#entidad').val('');
	$('#desc_entidad').val('');
	$('#plan').val('');
	$('#linea').val('');
	$('#desc_linea').val('');
	$('#tomador').val('');
	$('#desc_tomador').val('');
	$('#colectivo').val('');
	$('#dc').val('');
	$('#nomcolectivo').val('');
	$('#entmediadora').val('');
	$('#desc_entmediadora').val('');
	$('#subentmediadora').val('');
	$('#desc_subentmediadora').val('');	
	
	$('#pctdescuentocol').val('');	
	$('#pctprimerpago').val('');	
	$('#fechaIni').val('');	
	$('#fechaFin').val('');	
	$('#pctsegundopago').val('');	
	$('#lineaseguroid').val('');			
	$('#operacion').val("");
	$('#origenLlamada').val("cargaColectivos");
	$('#main').submit();
}

function consultar(){
	var frm = document.getElementById('main');
	frm.target="";
	$('#operacion').val("consultar");
	$('#main').submit();	
}