<!--                                                     -->
<!-- popupFormaPago.jsp (show in importes.jsp) -->
<!--                                                     -->
<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>

<script type="text/javascript">
<!--
$(document).ready(function(){
	$("#panelFormaPago").draggable();
	
	if ($('#datosCuentaId')[0] != null)
		if ($('#datosCuentaId')[0].checked) muestraDatosCuenta(); else $('#panelDatosCuenta').hide();
	if ($('#pagoManualId')[0] != null)
		if ($('#pagoManualId')[0].checked) muestraDatosPagoManual(); else $('#panelDatosPagoManual').hide();
	if ($('#envIBANAgr').val() == "true") {
		$('#envioIBANAgr').attr('checked', true);	
	}
	$('#panelAlertasValidacion_fp').hide();
	
	<c:if test="${modoLectura != 'modoLectura'}">
		// Calendario de Fecha pago
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
		        inputField        : "tx_fechaPago",
		        button            : "btn_fechaPago",
		        ifFormat          : "%d/%m/%Y",
		        daFormat          : "%d/%m/%Y",
		        align             : "Br"			        	        
		  	});
	</c:if>
	
	$('#frmFormapago').validate({
		errorLabelContainer: "#panelAlertasValidacion_fp",
		wrapper: "li",
		rules: { 
			"bancoDestino":{required: true,digits:true},
			"importe":{required:true, number:true},
			"fechaPago" : {required:true,dateITA: true,validaFechaPago:['fechaPago']}
			
			
		},
		messages: {
			"bancoDestino":{required: "El campo Banco Destino es obligatorio",digits: "El campo Banco Destino es sólo puede contener dígitos"},
			"importe":{required: "El campo Importe es obligatorio", number:"El campo Importe es de tipo numérico decimal"},
			"fechaPago" : {required:"El campo Fecha Pago es Obligatorio.", dateITA: "El formato del campo Fecha Pago pago es dd/mm/YYYY",validaFechaPago:"La Fecha Pago no puede ser posterior a la fecha actual"}
			
		}
	});
	jQuery.validator.addMethod("validaFechaPago", function(value, element) {
		return (this.optional(element) || UTIL.fechaMenorOIgualQueFechaActual(element.value));
	});
});
function cerrarPopUpFormaPago(){
	     
	// limpiamos alertas
	$('#panelAlertasValidacion_fp').html("");
	$('#panelAlertasValidacion_fp').hide();
	//cerramos div "inferiores"
	$('#panelDatosPagoManual').hide();
	$('#panelDatosCuenta').hide();
    $('#panelFormaPago').hide();
    <c:if test="${modoLectura != 'modoLectura'}">
    	($('#btnFormaPago')).show();
    </c:if>
    $('#overlay').hide();
}
function muestraDatosCuenta(){
	$('#panelAlertasValidacion_fp').html("");
	$('#panelAlertasValidacion_fp').hide();
	$('#panelDatosPagoManual').hide();
	
	
	if ($.trim($('#numeroCuenta').val())==""){
		$('#lbl_valor_IBAN').html("<span> El asegurado no tiene cuenta asignada</span>");
		
	}else{
		$('#lbl_valor_IBAN').html("<span>IBAN asegurado:    " + $('#numeroCuenta').val() + "</span>");	
	}
	$('#panelFormaPago').css('width','40%');
	
	$('#panelDatosCuenta').show();
	
}

function ajustar(tam, num) {
	if (num.toString().length <= tam) return ajustar(tam, "0" + num)
	else return num;
}

function muestraDatosPagoManual(){
	$('#panelDatosCuenta').hide();
	$('#panelAlertasValidacion_fp').html("");
	$('#panelAlertasValidacion_fp').hide();
	$('#panelFormaPago').css('width','45%');
	
	if ($('#modoLectura').val() != 'modoLectura'){
		$('#ibanPop').val( $('#numeroCuenta').val().substring(0,4));
		$('#cuenta1Pop').val( $('#numeroCuenta').val().substring(5,9));
		$('#cuenta2Pop').val( $('#numeroCuenta').val().substring(10,14));
		$('#cuenta3Pop').val( $('#numeroCuenta').val().substring(15,19));
		$('#cuenta4Pop').val( $('#numeroCuenta').val().substring(20,24));
		$('#cuenta5Pop').val( $('#numeroCuenta').val().substring(25,29));
		
		$('#bancoDestino').val( $('#banDestino').val());
		
		if ($('#fecPago').val() != null && $('#fecPago').val() != undefined){
			$('#tx_fechaPago').val( $('#fecPago').val());
		}
			
		if ($('#import').val() != null && $('#import').val() != undefined){
			$('#importe').val( $('#import').val());
		}

		if ($('#importeSaeca').val() != null && $('#importeSaeca').val() != ""){
			$('#importe').val($('#importeSaeca').val());
		}
		
	}
	if ($('#modoLectura').val() == 'modoLectura'){
		var banDestino = $('#bancoDestino').val();	
		banDestino = ajustar(3, banDestino);	
		$('#bancoDestino').val(banDestino);
	}
	$('#panelDatosPagoManual').show();
}

function aplicarFormaPago(){
	
	var frm = document.getElementById('frmFormapago');
	var validaciones = true;
	if (frm.pagoManualId.checked == true){
		$('#netoTomadorFinanciadoAgr').val($('#importe').val());
		if ($("#frmFormapago").valid()){
			var ibanccc = frm.iban.value + frm.cuenta1.value + frm.cuenta2.value + 
						  frm.cuenta3.value + frm.cuenta4.value + frm.cuenta5.value;
				 
			var f = validarIBAN(ibanccc);
			var existeBanco = existeBancoDestinoAjax();
			
			if (f == false || existeBanco == false){
				if (f == false){
					$('#panelAlertasValidacion_fp').html("El Iban tiene un formato incorrecto");
				}
				if (existeBanco == false){
					$('#panelAlertasValidacion_fp').html("El Banco Destino no existe");
				}
			 	$('#panelAlertasValidacion_fp').show();
			 	validaciones = false;
			}
			
			
			if (validaciones == true){
				if ($('#importe1').val() != frm.importe.value){
					jConfirm('El importe introducido no coincide con el importe de la póliza ¿Desea continuar?', 'Diálogo de Confirmación', function(r) {
					    if (r == true){
					    	uneCuenta();
					    	guardaDatosManualAjax();
					    }
					});
				}else{
					uneCuenta();
					guardaDatosManualAjax();
				}
			}
		}
	}else if (frm.datosCuentaId.checked == true){
		if ($.trim($('#numeroCuenta').val())==""){
			$('#panelAlertasValidacion_fp').html("Debe seleccionar pago manual");
		 	$('#panelAlertasValidacion_fp').show();
		}else{
			guardaDatosCuentaAjax();
			cerrarPopUpFormaPago();
		}
	}else{
		$('#panelAlertasValidacion_fp').html("Debe seleccionar una opción");
	 	$('#panelAlertasValidacion_fp').show();
	}
	
}
function existeBancoDestinoAjax(){
	
		var frm = document.getElementById('frmFormapago');
		var bancoDestino = frm.bancoDestino.value;
		var existe;
		$.ajax({
        url:          "pagoPoliza.html",
        data:         "operacion=doValidaBancoDestinoAjax&bancoDestino="+bancoDestino,
        async:        false,
        contentType:  "application/x-www-form-urlencoded",
        dataType:     "text",
        global:       false,
        ifModified:   false,
        processData:  true,
        error: function(objeto, quepaso, otroobj){
            alert("Error al solicitar la hora del servidor: " + quepaso);
        },
        success: function(datos){
        	if (datos == 'true'){ //banco correcto
        		existe = true;
					            	
        	}else{ // el banco no existe
        		existe = false;
        	}
        },
        type: "GET"
    });
		
		return existe;
}
function guardaDatosManualAjax(){
	
	var frm = document.getElementById('frmFormapago');
	var importe = frm.importe.value;
	var fechaPago = frm.fechaPago.value;
	var iban = frm.iban.value;
	var cccbanco = frm.cccbanco.value;
	var banco = frm.bancoDestino.value;
	var envioIBANAgr = null;
	if (frm.envioIBANAgr){
		envioIBANAgr = frm.envioIBANAgr.checked;
	}
	var polCpl = "false";
	
	var frm2 = document.getElementById('importes'); //estamos en polizas ppl
	if (frm2 == null){ 
		frm2 = document.getElementById('pasarADefinitiva');// estamos en polizas complementarias
		polCpl = "true";
	}
	var idpoliza = frm2.idpoliza.value;
	
	$('#panelAlertasValidacion').html("");
	$('#panelAlertasValidacion').hide();
	
	$.ajax({
    url:          "pagoPoliza.html",
    data:         "operacion=guardaDatosManualAjax&envioIBANAgr="+envioIBANAgr+"&importe="+importe+"&fechaPago="+fechaPago+"&iban="+iban+"&cccbanco="+cccbanco+"&idpoliza="+idpoliza+"&banco="+banco,
    async:        false,
    cache: false,
    contentType:  "application/x-www-form-urlencoded",
    dataType:     "json",
    global:       false,
    ifModified:   false,
    processData:  true,
    error: function(objeto, quepaso, otroobj){
        alert("Error al guardar los datos: " + quepaso);
    },
    success: function(datos){
    	$('#panelMensajeValidacion').html("Los datos de pago han sido guardados correctamente");
	 	$('#panelMensajeValidacion').show();
    	grabacionProvisionalAjax(frm, importe,fechaPago,iban,cccbanco, idpoliza,banco)
    	/* $('#overlay').hide();
    	cerrarPopUpFormaPago();
    	
    	if (polCpl == "false"){ // estamos en polizas principales
    		var nuevaCuenta = separaCuenta (datos.cccbanco);
    		document.getElementById('numeroCuentaId').innerHTML=datos.iban +" "+ nuevaCuenta;
    		$('#numeroCuenta').val(datos.iban +" "+ nuevaCuenta);
    		$('#tipoPagoGuardado').val(datos.tipoPagoGuardado);
    	}
    	
    	$('#panelMensajeValidacion').html("Los datos de pago han sido guardados correctamente");
	 	$('#panelMensajeValidacion').show(); */
    	
    },
    type: "GET"
});
}

function grabacionProvisionalAjax(frm, importe,fechaPago,iban,cccbanco, idpoliza,banco ){
	$.ajax({
	    url:          "pagoPoliza.html",
	    data:         "operacion=grProvisional&&pagoManual=SI&importe="+importe+"&fechaPago="+fechaPago+"&iban="+iban+"&cccbanco="+cccbanco+"&idpoliza="+idpoliza+"&banco="+banco+"&grabacionProvisional=true",
	    async:        false,
	    contentType:  "application/x-www-form-urlencoded",
	    dataType:     "text",
	    global:       false,
	    ifModified:   false,
	    processData:  true,
	    error: function(objeto, quepaso, otroobj){
	        alert("Error al guardar los datos: " + quepaso);
	        $('#overlay').hide();
	    },
	    success: function(datos){
	    	$('#overlay').hide();
	    	cerrarPopUpFormaPago();
	    	
	    	if (polCpl == "false"){ // estamos en polizas principales
	    		var nuevaCuenta = separaCuenta (datos.cccbanco);
	    		document.getElementById('numeroCuentaId').innerHTML=datos.iban +" "+ nuevaCuenta;
	    		$('#numeroCuenta').val(datos.iban +" "+ nuevaCuenta);
	    		$('#tipoPagoGuardado').val(datos.tipoPagoGuardado);
	    	}
	    	
	    	
	    	
	    },
	    type: "GET"
	});
}

function guardaDatosCuentaAjax(){
	var frm = document.getElementById('frmFormapago');
	var frm2 = document.getElementById('importes');//estamos en polizas ppl
	if (frm2 == null){
		frm2 = document.getElementById('pasarADefinitiva');// estamos en polizas complementarias
	}
	var idpoliza = frm2.idpoliza.value;
	var envioIBANAgr = null;
	if (frm.envioIBANAgr){
		var envioIBANAgr = frm.envioIBANAgr.checked;
	}
	$.ajax({
    url:          "pagoPoliza.html",
    data:         "operacion=guardaDatosCuentaAjax&envioIBANAgr="+envioIBANAgr+"&idpoliza="+idpoliza,
    async:        false,
    cache: false,
    contentType:  "application/x-www-form-urlencoded",
    dataType:     "text",
    global:       false,
    ifModified:   false,
    processData:  true,
    error: function(objeto, quepaso, otroobj){
        alert("Error al guardar los datos: " + quepaso);
    },
    success: function(datos){
    	$('#tipoPagoGuardado').val(datos.tipoPagoGuardado);
    	cerrarPopUpFormaPago();
    	$('#panelMensajeValidacion').html("Los datos de pago han sido guardados correctamente");
	 	$('#panelMensajeValidacion').show();
    },
    type: "GET"
});
}
function uneCuenta(){
	$('#cccbanco').val($('#cuenta1Pop').val()+$('#cuenta2Pop').val()+$('#cuenta3Pop').val()+$('#cuenta4Pop').val()+$('#cuenta5Pop').val());
	
}
function separaCuenta(cccBanco){
	var nuevaCuenta = cccBanco.substring(0,4)+" "+cccBanco.substring(4,8)+" "+cccBanco.substring(8,12)+" "+cccBanco.substring(12,16)+" "+cccBanco.substring(16,20);
	return nuevaCuenta;
	
}
//-->
</script>
<html>
	<head>
		<form id="frmFormapago" name="frmFormapago" action="pagoPoliza.html" method="post">
		
			<input type="hidden" name="tx_fechaPago.day" value="">
			<input type="hidden" name="tx_fechaPago.month" value="">
			<input type="hidden" name="tx_fechaPago.year" value="">
			<input type="hidden" name="cccbanco" id="cccbanco" >
			<input type="hidden" name="envIBANAgr" id="envIBANAgr" value="${envioIBANAgr}">
			<input type="hidden" name="operacion" id="operacion" value="popupFormaPago"/>
			<input type="hidden" name="idpoliza" id="idpoliza" value="${idpoliza}"/>
			<div id="panelFormaPago" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em;
					width: 40%; display: none; width: 40%; top: 270px; left: 33%; 
                    position: absolute; z-index: 1003;border:1px solid #A4A4A4;background-color:#F2F2F2">
			
				<!--  header popup -->
				<div id="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; 
						font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px" >
					<div style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Elección de forma de pago</div>
					
					<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; 
						top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
						<span onclick="cerrarPopUpFormaPago()">x</span>
					</a>
				</div>
			
				<!--  body popup -->
				<div class="panelInformacion_content">
					<div id="panelInformacion" class="panelInformacion">
					<div id="panelAlertasValidacion_fp" name="panelAlertasValidacion_fp" class="errorForm_fp" align="center"></div>
						<table style="width:90%">
							<tr>
								<c:choose>
								    <c:when test="${modoLectura == 'modoLectura'}">
								    <c:if test="${mpPagoC == true}">
										<td class="literal" align="center">
											<input type="radio" name="opcion" id="datosCuentaId" onclick="muestraDatosCuenta();" checked disabled="disabled">Cargo en cuenta
										</td>
								    </c:if>
								    <c:if test="${mpPagoC == false}">
								    	<td class="literal" align="center">
											<input type="radio" name="opcion" id="datosCuentaId" onclick="muestraDatosCuenta();" disabled="disabled">Cargo en cuenta
										</td>
								    </c:if>
								    <c:if test="${mpPagoM == true}">
										<td class="literal" align="center"> 
											<input type="radio" name="opcion" id="pagoManualId" onclick="muestraDatosPagoManual();" checked disabled="disabled">Pago Manual
										</td>
									</c:if>
									<c:if test="${mpPagoM == false}">
										<td class="literal" align="center"> 
											<input type="radio" name="opcion" id="pagoManualId" onclick="muestraDatosPagoManual();"  disabled="disabled">Pago Manual
										</td>
									</c:if>
									</c:when>
									<c:otherwise>
										<c:if test="${mpPagoC == true}">
											<td class="literal" align="center">
												<input type="radio" name="opcion" id="datosCuentaId" onclick="muestraDatosCuenta();" checked>Cargo en cuenta
											</td>
											<td class="literal" align="center"> 
												<input type="radio" name="opcion" id="pagoManualId" onclick="muestraDatosPagoManual();" >Pago Manual
											</td>
										</c:if>
										<c:if test="${mpPagoC == false && mpPagoM == true}">
											<td class="literal" align="center">
												<input type="radio" name="opcion" id="datosCuentaId" onclick="muestraDatosCuenta();" disabled="disabled">Cargo en cuenta
											</td>
											<td class="literal" align="center"> 
												<input type="radio" name="opcion" id="pagoManualId" onclick="muestraDatosPagoManual();" checked>Pago Manual
											</td>
										</c:if>
										<c:if test="${mpPagoC == false && mpPagoM == false}">
											<td class="literal" align="center">
												<input type="radio" name="opcion" id="datosCuentaId" onclick="muestraDatosCuenta();" disabled="disabled">Cargo en cuenta
											</td>
											<td class="literal" align="center"> 
												<input type="radio" name="opcion" id="pagoManualId" onclick="muestraDatosPagoManual();" disabled="disabled">Pago Manual
											</td>
										</c:if>	
									</c:otherwise>
								</c:choose>
							</tr>
						</table>
					</div>
					<div id="panelDatosCuenta" class="panelDatosCuenta">
						<table width="90%">
							<tr></tr>
							<tr>
								<td class="literal" align="left">
									<label id="lbl_valor_IBAN" name="lbl_valor_IBAN"  class="detalI"></label>
								</td>
							</tr>
						</table>
					</div>
					<div id="panelDatosPagoManual" class="panelDatosPagoManual">
						<table>
							<tr>
								<td class="literal" align="left">Banco Destino</td>
								<td class="literal">
								    <c:choose>
									    <c:when test="${modoLectura == 'modoLectura'}">
											<input type="text" name="bancoDestino" id="bancoDestino"  class="dato" size="4" maxlength="4" value="${bancoDestino}" readonly="readonly"/>	
										</c:when>
										<c:otherwise>
											<input type="text" name="bancoDestino" id="bancoDestino"  tabindex="1" class="dato" size="4" maxlength="4" value="${bancoDestino}"/>
											<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Banco','principio', '', '');"  alt="Buscar Banco" title="Buscar Banco" />
										</c:otherwise>
									</c:choose>									
								</td>
							</tr>
							<tr>
								<td class="literal" align="left">Importe</td>
								<td class="literal">
								    <c:choose>
									    <c:when test="${modoLectura == 'modoLectura'}">
											<input type="text" name="importe" id="importe"  class="dato" value="${importe}" readonly="readonly"/>	
										</c:when>
										<c:otherwise>
											<input type="text" name="importe" id="importe" tabindex="2"  class="dato" value="${importe}"/>
										</c:otherwise>
									</c:choose>										
								 </td>
								<td class="literal">Fecha de pago</td>
								<td class="literal">
								    <c:choose>
									    <c:when test="${modoLectura == 'modoLectura'}">
											<input type="text" name="fechaPago" id="tx_fechaPago" size="11" maxlength="10" class="dato"
											value="<fmt:formatDate pattern="dd/MM/yyyy" value="${fechaPago}" />" readonly="readonly"/>	
										</c:when>
										<c:otherwise>
											<input type="text" name="fechaPago" tabindex="3"  id="tx_fechaPago" size="11" maxlength="10" class="dato"
											value="<fmt:formatDate pattern="dd/MM/yyyy" value="${fechaPago}" />" />
											
											<input type="button" id="btn_fechaPago" name="btn_fechaPago"
												class="miniCalendario" style="cursor: pointer;" />
											<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_tx_fechaPago"> *</label>
										</c:otherwise>
									</c:choose>									
								</td>
							</tr>
							<tr>
								<td class="literal">IBAN</td>
								<td class="literal" colspan="2">
								    <c:choose>
									    <c:when test="${modoLectura == 'modoLectura'}">
											<input type="text" id="ibanPop"    name="iban"    size="4" maxlength="4" class="dato" tabindex="4" readonly="readonly" value="${iban}"/>
											<input type="text" id="cuenta1Pop" name="cuenta1" size="4" maxlength="4" class="dato" tabindex="5" readonly="readonly" value="${cuenta1}"/>
											<input type="text" id="cuenta2Pop" name="cuenta2" size="4" maxlength="4" class="dato" tabindex="6" readonly="readonly" value="${cuenta2}"/>
											<input type="text" id="cuenta3Pop" name="cuenta3" size="4" maxlength="4" class="dato" tabindex="7" readonly="readonly" value="${cuenta3}"/>
											<input type="text" id="cuenta4Pop" name="cuenta4" size="4" maxlength="4" class="dato" tabindex="8" readonly="readonly" value="${cuenta4}"/>
											<input type="text" id="cuenta5Pop" name="cuenta5" size="4" maxlength="4" class="dato" tabindex="9" readonly="readonly" value="${cuenta5}"/>
										</c:when>
										<c:otherwise>
											<input type="text" id="ibanPop"    name="iban"    size="4" maxlength="4" class="dato" tabindex="4" onkeyup="autotab(this, document.frmFormapago.cuenta1Pop);" onchange="this.value=this.value.toUpperCase()";/>
											<input type="text" id="cuenta1Pop" name="cuenta1" size="4" maxlength="4" class="dato" tabindex="5" onKeyup="autotab(this, document.frmFormapago.cuenta2Pop);"/>
											<input type="text" id="cuenta2Pop" name="cuenta2" size="4" maxlength="4" class="dato" tabindex="6" onKeyup="autotab(this, document.frmFormapago.cuenta3Pop);"/>
											<input type="text" id="cuenta3Pop" name="cuenta3" size="4" maxlength="4" class="dato" tabindex="7" onKeyup="autotab(this, document.frmFormapago.cuenta4Pop);"/>
											<input type="text" id="cuenta4Pop" name="cuenta4" size="4" maxlength="4" class="dato" tabindex="8" onKeyup="autotab(this, document.frmFormapago.cuenta5Pop);"/>
											<input type="text" id="cuenta5Pop" name="cuenta5" size="4" maxlength="4" class="dato" tabindex="9" value="${cuenta5}"/>
										</c:otherwise>
									</c:choose>	
								</td>
							</tr>
							
						</table>
					</div>
					<!-- div envioIBANAgr común -->
					<div id="eIBANAgr" class="literal">
					<c:if test="${isLineaGanado eq true}">
						<tr>
							<td class="literal" colspan="4" align="center">
								<c:choose>
									<c:when test="${modoLectura == 'modoLectura'}">
										<input valign="center" name="envioIBANAgr" id="envioIBANAgr" type="checkbox" tabindex="10" disabled="disabled"> Enviar IBAN a Agroseguro
									</c:when>
									<c:otherwise>
										<input valign="center" name="envioIBANAgr" id="envioIBANAgr" type="checkbox" tabindex="10"> Enviar IBAN a Agroseguro
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:if>
				</div>
					
				</div>
			    <!-- Botones popup --> 
			    <div style="margin-top:15px" align="center">
			        <c:choose>
					    <c:when test="${modoLectura == 'modoLectura'}">
							<a class="bot" href="javascript:cerrarPopUpFormaPago()" title="Cancelar">Cerrar</a>
						</c:when>
						<c:otherwise>
							<a class="bot" href="javascript:cerrarPopUpFormaPago()" title="Cancelar">Cancelar</a>
							<a class="bot" href="javascript:aplicarFormaPago()" title="Aplicar" id="btnAplicar">Aplicar</a>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</form>
		
	</head>
</html>