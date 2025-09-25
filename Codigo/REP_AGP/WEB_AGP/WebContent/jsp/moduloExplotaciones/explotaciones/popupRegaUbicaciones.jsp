<!--                                                     -->		
<!-- popupRegaUbicaciones.jsp (show in datosExplotaciones.jsp) -->
<!--                                                     -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />

<style>

#datosRega {
	width: 250px;
}

#listaDatos li {
	padding-top: 2px;
	padding-bottom: 2px;
}
.panelInformacionRega {
	display: none;
	color:#333333;
	-moz-border-radius:4px 4px 4px 4px;
	padding:0.2em;
    width: 980px;
    top: 50px;
    left: 50%;
    position: absolute;
   	transform: translateX(-50%);
    z-index: 1006;
    border: 1px solid #A4A4A4;
    background-color: #FFFFFF !important;
}

.ubicacion-fila:hover .literal {
	background-color: silver;
	cursor: default;
}


.tdhead {
 background-color: rgb(229, 229, 229);

  font-family: tahoma, verdana, arial;
  font-size: 11px;
  color: #626262;
  text-align: left;
  vertical-align: middle;
  padding-left: 5px;
  padding-right: 10px;
  padding-bottom: 2px;
  border-bottom: 1px solid black;
}

.thead_code {
	width:80px
}

.thead_desc {
	width:80px
}
</style>

<div id="popupRegaUbicaciones" class="panelInformacionRega">
	  <!--  header popup -->
	<div id="header-popupMU" style="padding:0.4em 0.4em; position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:1px 1px 1px 1px;background:#525583;height:15px">
        <div  style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
            Ubicaciones REGA
        </div>
        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.1em;top:50%;width:25px;
                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
            <span onclick="cerrarPopUpInformacionRega()">x</span>
        </a>
	</div>
		 
	<!--  body popup -->
	
	<div class="panelInformacion_content" style="display:flex;width:100%;padding:0;justify-content:center;">
		<div id="datosRega" style="display:flex;flex-direction:column;padding-top: 20px;padding-bottom: 20px;width: 75%;">
			<div>
				
				<table id="tabla-ubicaciones"
					style="border: 1px solid rgb(204, 204, 204); table-layout: fixed;"
					width="100%">
					<thead>
						<tr>
							<td class="tdhead literal thead_code">Cod. Provincia</td>
							<td class="tdhead literal thead_desc">Nom. Provincia</td>
							<td class="tdhead literal thead_code">Cod. Comarca</td>
							<td class="tdhead literal thead_desc">Nom. Comarca</td>
							<td class="tdhead literal thead_code">Cod. Término</td>
							<td class="tdhead literal thead_desc">Nom. Término</td>
							<td class="tdhead literal thead_code">Subtérmino</td>
						</tr>
					</thead>
					<tbody id="table-body"></tbody>
				</table>

				<div>
					<div id="panelCensoLineas"></div>
					<div style="margin:10px auto" id="div_bot" >
						<a class="bot" href="#" onclick="cerrarPopUpInformacionRega();">Cerrar</a>
					</div>					
				</div>
			</div>
		</div>
	</div>
</div>




<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>

<script>
function cerrarPopUpInformacionRega(){
    $('#popupRegaUbicaciones').fadeOut('normal');
    $('#overlay').hide();
}


function doInfoRega() {


	clearFormFields();
	limpiarAlertas();
	$.ajax({
        url: "listadoExplotaciones.html",
		data: "method=doInfoRega&codigoRega="+$('#rega').val()+"&plan="+$("meta[name=codigo_plan]").attr("content")+"&linea="+$("meta[name=codigo_linea]").attr("content"),
        async:true,
        cache: false,
        beforeSend: function() {
        	$.blockUI.defaults.message = '<h4>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
    		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
        	limpiaInfoRega();
        },
        complete: function() {
        	$.unblockUI();
        },
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        error : function(jqXHR, exception) {
			if (jqXHR.status === 0) {
	            msg = 'Verifique la conexi\u00F3n.';
	        } else if (jqXHR.status == 404) {
	            msg = 'P\u00E1gina no encontrada [404].';
	        } else if (jqXHR.status == 500) {
	            msg = 'Error interno del servidor [500].';
	        } else if (exception === 'parsererror') {
	            msg = 'Fallo en el tratamiento del JSON esperado.';
	        } else if (exception === 'timeout') {
	            msg = 'Tiempo de espera agotado.';
	        } else if (exception === 'abort') {
	            msg = 'Petici\u00F3n Ajax cancelada.';
	        } else {
	            msg = 'Error no esperado: ' + jqXHR.responseText;
	        }
			$('#panelAlertasValidacion').html(msg);
			$('#panelAlertasValidacion').show();
		},
        global: true,
        ifModified: false,
        processData:true,
        success : function(datos) {
        	
			console.warn(datos);

			
        	if (datos.errorMsgs.length > 0) {
				var errorMsg = '';
				for (i = 0; i < datos.errorMsgs.length; i++) {
					errorMsg += datos.errorMsgs[i] + '<br/>';
				}
				$('#panelAlertasValidacion').html(errorMsg);
				$('#panelAlertasValidacion').show();
			} else {

				
				if (datos.informacionRega.ambitoAgroseguro == null) {
					console.warn('No se han encontrado ubicaciones');
					return;
				}
				
				var ambitosCount = datos.informacionRega.ambitoAgroseguro.length;
				
				if (ambitosCount > 1) {
					// show modal					
					$('#popupRegaUbicaciones').show();
		        	$('#overlay').show();
		        	generateTable(datos.informacionRega.ambitoAgroseguro)
		        	

				} else if(ambitosCount == 1) {
					
					fillAmbitoValues(datos.informacionRega.ambitoAgroseguro[0])
				}

			
			}
        },
        type: "POST"
    });	
}

function limpiaInfoRega() {
	$('#table-body').html('');
}

function limpiarAlertas() {
	$('#panelAlertasValidacion').html('');
	$('#panelAlertasValidacion').hide();
	$('label[id*="campoObligatorio_"]').each(function() {
		$(this).hide();
	});
	$('#panelMensajeValidacion').html('');
	$('#panelMensajeValidacion').hide();
}

function fillAmbitoValues(ambitoAgroseguro) {
	$('#provincia').val(ambitoAgroseguro.provincia.codigo);
	$('#desc_provincia').val(ambitoAgroseguro.provincia.descriptivo);

	$('#comarca').val(ambitoAgroseguro.comarca.codigo);
	$('#desc_comarca').val(ambitoAgroseguro.comarca.descriptivo);
	$('#termino').val(ambitoAgroseguro.termino.codigo);
	$('#desc_termino').val(ambitoAgroseguro.termino.descriptivo);
	
	if (ambitoAgroseguro.subtermino != null && ambitoAgroseguro.subtermino.codigo != "null")
		$('#subtermino').val(ambitoAgroseguro.subtermino.codigo);
}

function clearFormFields() {
	
	$('#provincia').val('');
	$('#desc_provincia').val('');
	$('#comarca').val('');
	$('#desc_comarca').val('');
	$('#termino').val('');
	$('#desc_termino').val('');
	$('#subtermino').val('');
	
}


function generateTable(arr) {
	
	var rows = '';
	
	for (var i = 0; i < arr.length; i++) {
		
		var color = i % 2 == 0? 'white': 'rgb(247, 247, 247)'
		
		var object = arr[i];
		
		var subtermino = '';
	    if (object.subtermino != "null") {
	        subtermino = object.subtermino.codigo;
	    }
		
		var tdStr = '<tr style="background-color: '+color+'" class="ubicacion-fila" data-index="' + i +'">' +
			'<td class="literal">' + object.provincia.codigo + '</td>' +
			'<td class="literal">' + object.provincia.descriptivo + '</td>' +

			'<td class="literal">' + object.comarca.codigo + '</td>' +
			'<td class="literal">' + object.comarca.descriptivo + '</td>' +

			'<td class="literal">' + object.termino.codigo + '</td>' +
			'<td class="literal">' + object.termino.descriptivo + '</td>' +

			'<td class="literal">' + subtermino + '</td>' +
		'</tr>';
		
	
		rows += tdStr;
	}
	
	$('#table-body').html(rows);
	
	$('.ubicacion-fila').click(function() {
		fillAmbitoValues(arr[$(this).attr('data-index')]);
		cerrarPopUpInformacionRega();
	});
	
}

</script>
	