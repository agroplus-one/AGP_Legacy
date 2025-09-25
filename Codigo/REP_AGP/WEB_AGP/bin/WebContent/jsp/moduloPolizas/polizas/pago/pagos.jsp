<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
	<head>
		<title>Pagos</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		
		<script type="text/javascript" src="jsp/js/util.js"></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
		<script type="text/javascript" src="jsp/js/calendar.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>		
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
		
		<script type="text/javascript">
		
		//Para evitar el cacheo de peticiones al servidor
        $(document).ready(function(){
                 var URL = UTIL.antiCacheRand(document.getElementById("main").action);
			     document.getElementById("main").action = URL;    

				$('#btnGrabar').show();
				
				$('#iban').attr('readonly','readonly');
				$('#cuenta1').attr('readonly','readonly');
				$('#cuenta2').attr('readonly','readonly');
				$('#cuenta3').attr('readonly','readonly');
				$('#cuenta4').attr('readonly','readonly');
				$('#cuenta5').attr('readonly','readonly');
				$('#btnVolverCpl').hide();
							
				<c:if test="${AltaCuenta}">
					$('#btnAltaCCC').show();
				</c:if>
				
				<c:if test="${polizaComplementaria}">
					$('#btnVolverCpl').show();
					$('#btnVolver').hide();
				</c:if>
				
				if($('#modoLectura').val() == 'modoLectura'){
					//Deshabilitamos todos los campos
					$(".dato").attr("readonly","true");
					$(".miniCalendario").attr("disabled","disabled");
				}				
				$('#main').validate({
					errorLabelContainer: "#panelAlertasValidacion",
					wrapper: "li",
					onfocusout: function(element) {
						if ( ($('#operacion').val() == "Grabar") && !this.checkable(element) && (element.name in this.submitted || !this.optional(element)) ) {
							this.element(element);
						}
					 },
   					
					rules: { 
						"pctprimerpago":{required: true,digits:true, range: [1,100]},
						"pctsegundopago":{required: function(element) {return parseInt($("#pctprimerpagocliente").val()) < 100;},digits:true, range: [1,99], comprobarPagos: ['pctprimerpagocliente','pctsegundopagocliente']},
						"banco" : {required:true,digits:true,minlength: 4},
						"sucursal" : {required:true,digits:true,minlength: 4},
						"dc" : {required:true,digits:true,minlength: 2},
						"numeroCuenta" : {required:true,digits:true,minlength: 10},
						"oficina":{required:true,digits:true},
						"fecha1":{fechaRellena: ['pctprimerpagocliente','fecha1'],dateITA: true},
						"fechasegundopago1":{fechaRellena: ['pctsegundopagocliente','fechasegundopago1'],dateITA: true,validaFechaPago:['fecha1','fechasegundopago1']}
					},
					messages: {
						"pctprimerpago":{required: "El campo % Primer pago es obligatorio",digits: "El campo % Primer pago sólo puede contener dígitos", range: "El campo % Primer pago debe contener un número entre 1 y 100"},
						"pctsegundopago":{required: "El campo % Segundo pago es obligatorio",digits: "El campo % Segundo pago sólo puede contener dígitos",  range: "El campo % Segundo pago debe contener un número entre 1 y 99", comprobarPagos: "La suma de los pagos debe ser igual a 100"},
						"banco" : {required:"El campo Banco de NºCuenta es Obligatorio.", digits: "El campo Banco sólo puede contener dígitos", minlength: "El campo Banco debe contener 4 dígitos"},
						"sucursal" : {required:"El campo Surcursal de NºCuenta es Obligatorio.", digits: "El campo Surcursal sólo puede contener dígitos", minlength: "El campo Surcursal debe contener 4 dígitos"},
						"dc" : {required:"El campo DC de NºCuenta es Obligatorio.", digits: "El campo DC sólo puede contener dígitos", minlength: "El campo DC debe contener 4 dígitos"},
						"numeroCuenta" : {required:"El campo Numero Cuenta de NºCuenta es Obligatorio.", digits: "El campo Numero Cuenta sólo puede contener dígitos", minlength: "El campo Numero Cuenta debe contener 4 dígitos"},
						"oficina" : {required:"El campo Oficina Propietaria es Obligatorio.", digits: "El campo Oficina Propietaria sólo puede contener dígitos"},
						"fecha1": {fechaRellena:"El campo Fecha Primer Pago es Obligatorio",dateITA: "El formato del campo Fecha Primer Pago es dd/mm/YYYY"},
						"fechasegundopago1":{fechaRellena:"El campo Fecha Segundo Pago es Obligatorio",dateITA: "El formato del campo Fecha Primer Pago es dd/mm/YYYY",validaFechaPago:"La Fecha Segundo Pago no puede ser anterior a la Fecha Primer Pago"}
					}
				});
				//comprueba que la suma del porcentaje de los pagos es 100
				//params--> los pagos
				jQuery.validator.addMethod("comprobarPagos", function(value, element, params) {	
				  return (this.optional(element) || parseInt($('#'+params[0]).val())+parseInt($('#'+params[1]).val()) == 100);
				});
				
				// comprueba que la fecha segundo pago sea posterior a la primera
				jQuery.validator.addMethod("validaFechaPago", function(value, element, params) {
					if (document.getElementById(params[1]).value != null && document.getElementById(params[1]).value != ""){
						//alert (document.getElementById(params[1]).value);
						return (!UTIL.fechaMayorOIgualQue(document.getElementById(params[0]), document.getElementById(params[1])));
					}else{
						return true;
					}
				});
				
				
				// comprueba que si se ha metido valos en el % del primer pago, la fecha se rellene
				jQuery.validator.addMethod("fechaRellena", function(value, element, params) {
					
					if ($('#'+params[0]).val() != null && $('#'+params[0]).val() !=""){ // si el % esta relleno
						  if ($('#'+params[1]).val() != null && $('#'+params[1]).val() !=""){ //si la fecha esta rellena
							  return true ;
						  }else{ // si la fecha esta vacia
							  return  false ;
						  }
					  }
					  return  true ;
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
			        inputField        : "fecha1",
			        button            : "btn_fechaprimerpagomodificado",
			        ifFormat          : "%d-%m-%Y",
			        daFormat          : "%d-%m-%Y",
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
			        inputField        : "fechasegundopago1",
			        button            : "btn_fechasegundopagomodificado",
			        ifFormat          : "%d-%m-%Y",
			        daFormat          : "%d-%m-%Y",
			        align             : "Br"
		      	});
		      	
		      	
		      	// MPM - 15/06/12
		      	// En modo lectura sólo hay que mostrar el botón de 'Volver',			      	
		      	if($('#modoLectura').val() == 'modoLectura'){		      				  			   			 					   		  								   	
			   		// Ocultar botones			   				
					$('#btnAltaCCC').hide();
					$('#btnGrabar').hide();					
				}
		      	
		      	calculaImportePrimerPago();
		      	calculaImporteSegundoPago();		      	
	      	});
	
			/* function calculaImportePrimerPago(){
				var pct = document.getElementById('pctprimerpago').value;
				var importe = document.getElementById('importeTotal').value;
				if (pct != '' && importe != '') {
					pct =  document.getElementById('pctprimerpago').value;
					if(isNaN(eval(pct*importe/100))){
						document.getElementById('importePrimerPagoCliente').value = '0';
					}else{
						document.getElementById('importePrimerPagoCliente').value = eval(trunc(pct*importe/100,3));
					} 
				}
			} */
			function calculaImportePrimerPago(){				
				var pct = document.getElementById('pctprimerpagocliente').value;
				var importe = document.getElementById('importeTotal').value;
				if (pct != '' && importe != '') {
					pct =  document.getElementById('pctprimerpagocliente').value;
					if(isNaN(eval(pct*importe/100))){
						document.getElementById('importePrimerPagoCliente').value = '0';
					}else{
						document.getElementById('importePrimerPagoCliente').value = eval(trunc(pct*importe/100,3));
					} 
				}
			}
			
			/* function calculaImporteSegundoPago(){
				var pct = document.getElementById('pctsegundopago').value;
				var importe = document.getElementById('importeTotal').value;
				if (pct != '' && importe != '') {
					pct =  document.getElementById('pctsegundopago').value;
					if(isNaN(eval(pct*importe/100))){
						document.getElementById('importeSegundoPagoCliente').value = '0';
					}else{
						document.getElementById('importeSegundoPagoCliente').value = eval(trunc(pct*importe/100,3));
					}
				}			
			} */
			function calculaImporteSegundoPago(){
				var pct = document.getElementById('pctsegundopagocliente').value;
				var importe = document.getElementById('importeTotal').value;
				if (pct != '' && importe != '') {
					pct =  document.getElementById('pctsegundopagocliente').value;
					if(isNaN(eval(pct*importe/100))){
						document.getElementById('importeSegundoPagoCliente').value = '0';
					}else{
						document.getElementById('importeSegundoPagoCliente').value = eval(trunc(pct*importe/100,3));
					}
				}
			}
			
			function trunc(num, ndec) { 
			  var fact = Math.pow(10, ndec); // 10 elevado a ndec 
			
			  /* Se desplaza el punto decimal ndec posiciones, 
			    se trunca el número y se vuelve a colocar 
			    el punto decimal en su sitio. */ 
			  return parseInt(num * fact) / fact; 
			} 
			
			function grabarPagoPoliza(){
				/* alert($('#importePrimerPagoCliente').val()); */
				
				//$('#importePrimerPagoCliente').val($('#importeTotal').val());
				$('#operacion').val('grabar');
				if($('#main').valid()){
					$.blockUI.defaults.message = '<h4> Grabando los datos del pago.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	       			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } }); 
					$('#main').submit();
					
				}
			}
			
			function volver(){	
				//alert($('#cicloPoliza').val());
				if ($('#modoLectura').val() == 'modoLectura'){
						history.back(-1);
				}else if($('#origenllamada').val() == 'pago'){
					/* $.blockUI.defaults.message = '<h4> Realizando cálculos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       				$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });  */
	         		$(window.location).attr('href', 'pagoPoliza.html?rand=' + UTIL.getRand() + 
	         				'&idpoliza='+$('#idpoliza').val()+ '&idEnvio='+$('#idEnvio').val()+
	         				'&operacion=volverDePagos' + '&cicloPoliza=' + $('#cicloPoliza').val() + 
	         				'&modoLectura=' + $('#modoLectura').val() + '&volverPagos=true' + 
	         				'&vieneDeUtilidades='+$('#vieneDeUtilidades').val());
				}else{					
					if($('#vieneDeUtilidades').val() == 'true'){
						$(window.location).attr('href', 'consultaDetallePoliza.html?rand=' + UTIL.getRand() + '&method=doVerImportes' + '&idpoliza='+$('#idpoliza').val()+ '&idEnvio='+$('#idEnvio').val()+ '&modoLectura=' + $('#modoLectura').val()+ '&vieneDeUtilidades='+$('#vieneDeUtilidades').val());
	    			
					}else{						
						$.blockUI.defaults.message = '<h4> Realizando cálculos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	       				$.blockUI({ overlayCSS: { backgroundColor: '#525583' } }); 
		         		$(window.location).attr('href', 'pagoPoliza.html?rand=' + UTIL.getRand() + '&idpoliza='+$('#idpoliza').val()+ '&idEnvio='+$('#idEnvio').val()+'&operacion=volverDePagos' + '&cicloPoliza=' + $('#cicloPoliza').val() + '&modoLectura=' + $('#modoLectura').val() + '&volverPagos=true');
		         	}
				}
	         }
			
			// DAA 02/07/2013
			function volverCpl(){
				$("#main").validate().cancelSubmit = true
				if ($('#modoLectura').val() == 'modoLectura'){
					history.back(-1);
				}else if($('#vieneDeUtilidades').val() == 'true'){
					$(window.location).attr('href', 'consultaDetallePoliza.html?rand=' + UTIL.getRand() + '&method=doVerImportesCpl' + '&idpoliza='+$('#idpoliza').val()+ '&idEnvio='+$('#idEnvio').val()+ '&modoLectura=' + $('#modoLectura').val()+ '&vieneDeUtilidades='+$('#vieneDeUtilidades').val());
    			
				}else if($('#vieneDeUtilidades').val() == 'grabacionProv'){
					$('#operacion').val('volverDePagos');
					$('#main').submit();
				}else{
					//$(window.location).attr('href', 'webservicesCpl.html?method=doValidar&idpoliza='+ $('#idpoliza').val() +  '&rand=' +encodeURI(parseInt(Math.random()*99999999) + "_" + (new Date).getTime()));
					$(window.location).attr('href', 'pagoPoliza.html?rand=' + UTIL.getRand() + '&idpoliza='+$('#idpoliza').val()+ '&idEnvio='+$('#idEnvio').val()+'&operacion=volverDePagos' + '&modoLectura=' + $('#modoLectura').val() + '&volverPagos=true');
				}
			}
			
			function altaCCC(){
				$("#main").validate().cancelSubmit = true;
				$('#operacion').val('altaCCC');
				$('#main').submit();
			}
			function eligeMenu(){
				if($('#vieneDeUtilidades').val() == 'true'){
				 	SwitchMenu('sub4');
				 }else{
				 	SwitchMenu('sub3');
				 }
			 }
			function rellenaPctSegundoPago(pctPrimerPago){
				if (pctPrimerPago.value <= 100){
					$('#pctsegundopagocliente').val(100 - pctPrimerPago.value);
					calculaImporteSegundoPago();
				}
				
			}
			
			function rellenaPctPrimerPago(pctSegundoPago){
				if (pctSegundoPago.value <= 100){
					$('#pctprimerpagocliente').val(100 - pctSegundoPago.value);
					calculaImportePrimerPago();
				}
			}
			
		</script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0"
			onload="eligeMenu();">

		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<c:choose>
			<c:when test="${modoLectura == 'modoLectura' && vieneDeUtilidades == 'true'}">
				<%@ include file="/jsp/common/static/datosCabeceraConsulta.jsp" %>	
			</c:when>
			<c:otherwise>
				<%@ include file="/jsp/common/static/datosCabecera.jsp"%>
			</c:otherwise>
		</c:choose>
		
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="right">
						<a class="bot" href="#" id="btnVolver" onclick="javascript:volver();">Volver</a>
						<a class="bot" href="#" id="btnVolverCpl" onclick="javascript:volverCpl();">Volver</a>
						<a class="bot" id="btnAltaCCC" href="javascript:altaCCC()" style="display:none">Alta CCC</a>
						<a class="bot" id="btnGrabar" href="#" onclick="javascript:grabarPagoPoliza();">Grabar</a>
					</td>
				</tr>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Pago</p>
		<form:form name="main" id="main" action="pagoPoliza.html" method="post"	commandName="pagoPolizaBean">
			<input type="hidden" name="idpoliza" id="idpoliza" value="${pagoPolizaBean.poliza.idpoliza}"/>
			<input type="hidden" name="operacion" id="operacion" />
			<input type="hidden" name="fecha1.day" value="" /> 
			<input type="hidden" name="fecha1.month" value="" /> 
			<input type="hidden" name="fecha1.year" value="" /> 
			<input type="hidden" name="fechasegundopago1.day" value="" /> 
			<input type="hidden" name="fechasegundopago1.month" value="" /> 
			<input type="hidden" name="fechasegundopago1.year" value="" />
			<input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
			<input type="hidden" name="idEnvio" id="idEnvio" value="${idEnvio}"/>
			<input type="hidden" name="modoLectura" id="modoLectura" value="${modoLectura}"/>
			<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${vieneDeUtilidades}"/>
			<input type="hidden" name="volverPagos" id="volverPagos" value="true"/>
			<input type="hidden" name="primPago" id="primPago" value=""/>
			<input type="hidden" name="importePoliza" id="importePoliza" value="${importePoliza}"/>
			<input type="hidden" name="cicloPoliza" id="cicloPoliza" value="${cicloPoliza}" />
			<input type="hidden" name="origenllamada" id="origenllamada" value="${origenllamada}" />
			<form:hidden path="tipoPago" id="tipoPago"/>
			<form:hidden path="id" />
			<form:hidden path="cccbanco" id="cccbanco"/>
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			<%@ include file="/jsp/common/static/avisoErroresLocal.jsp"%>
			<div class="panel1 isrt">
			<table width="80%">
				<tr align="left">
					<td class="literal">IBAN</td>
					<td class="literal">
					<form:input path="iban" size="4" maxlength="4" cssClass="dato" tabindex="2" onkeyup="autotab(this, document.main.cuenta1);"/>
					<input type="text" id="cuenta1" name="cuenta1" size="4" maxlength="4" class="dato" tabindex="3" onKeyup="autotab(this, document.main.cuenta2);" value="${cuenta1}"/>
					<input type="text" id="cuenta2" name="cuenta2" size="4" maxlength="4" class="dato" tabindex="4" onKeyup="autotab(this, document.main.cuenta3);" value="${cuenta2}"/>
					<input type="text" id="cuenta3" name="cuenta3" size="4" maxlength="4" class="dato" tabindex="5" onKeyup="autotab(this, document.main.cuenta4);" value="${cuenta3}"/>
					<input type="text" id="cuenta4" name="cuenta4" size="4" maxlength="4" class="dato" tabindex="6" onKeyup="autotab(this, document.main.cuenta5);" value="${cuenta4}"/>
					<input type="text" id="cuenta5" name="cuenta5" size="4" maxlength="4" class="dato" tabindex="7" value="${cuenta5}"/>
					
					</td>
					<c:if test="${perfil == 0 || perfil == 1 || perfil == 3 || perfil == 5}">
						<td class="literal">Oficina Propietaria</td>
						<td>
							<input type="hidden" id="entidad" value="${pagoPolizaBean.poliza.colectivo.tomador.id.codentidad}"/>
							<input name="oficina" id="oficina" size="4" maxlength="4" class="dato" value="${pagoPolizaBean.poliza.oficina}" onchange="javascript:lupas.limpiarCampos('desc_oficina');"/>
							<!-- el frupoOficinas solo se llena para perfiles 2 y este campo no lo ve el perfil 2-->
							<input type="hidden" id="grupoOficinas" value=""/>
							<form:input path="${pagoPolizaBean.poliza.nombreOfi}" cssClass="dato"	id="desc_oficina" size="20" readonly="true"/>
							<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Oficina','principio', '', '');"  alt="Buscar Oficina" title="Buscar Oficina" />
						</td>
					</c:if>
					<c:if test="${perfil == 4}">
						<input type="hidden" id="oficina" name="oficina" value="${pagoPolizaBean.poliza.oficina}" />
					</c:if>
				</tr>
				<tr align="left">
					<td class="literal">Importe</td>
					<td class="literal">
						<c:if test="${importePoliza != ''}">
						<%-- <form:input path="importe" id="importeTotal" cssClass="dato" readonly="true" cssStyle="text-align: right;"/> --%>
							<input type="text" name="importeTotal" id="importeTotal" value="${importePoliza}" class="dato" readonly="readonly" style="text-align: right;"/>&#8364;
						</c:if>
						<c:if test="${importePoliza == ''}">
							<form:input path="importe" id="importeTotal" cssClass="dato" readonly="true" cssStyle="text-align: right;"/>
						</c:if>
					</td>
					<td></td>
					<td></td>
				</tr>
			</table>
			</div>
			<div class="panel1 isrt">
				<fieldset>
					<table width="100%">
						<colgroup>
							<col width="15%" />
							<col width="15%" />
							<col width="15%" />
							<col width="15%" />
							<col width="15%" />
							<col width="15%" />
						</colgroup>
						<tr>
							<td class="literal" rowspan="2">Forma de pago colectivo</td>
				
							<td class="literal">% Primer pago colectivo</td>
							<td class="literal">
								<input type="text" name="pctprimerpagocolectivo" id="pctprimerpagocolectivo" value="${pagoPolizaBean.poliza.colectivo.pctprimerpago}" size="5" maxlength="5" class="dato" style="text-align: left;" readonly="readonly"/>
							</td>
							<td class="literal">Fecha primer pago</td>
							<td class="literal">
									<input type="text" name="fechaprimerpagocolectivo" id="fechaprimerpagocolectivo" size="11" maxlength="10" class="dato"
											value="<fmt:formatDate pattern="dd/MM/yyyy" value="${pagoPolizaBean.poliza.colectivo.fechaprimerpago}" />" readonly="readonly" />
							
							</td>
						</tr>
						<tr>
							<td class="literal">% Segundo pago colectivo</td>
							<td class="literal">
								<input type="text" name="pctsegundopagocolectivo" id="pctsegundopagocolectivo" value="${pagoPolizaBean.poliza.colectivo.pctsegundopago}" class="dato" readonly="readonly" size="5" maxlength="5" style="text-align: left;" readonly="readonly"/>
							</td>
							<td class="literal">Fecha segundo pago</td>
							<td class="literal">
									<input type="text" name="fechasegundopagocolectivo" id="fechasegundopagocolectivo" size="11" maxlength="10" class="dato"
											value="<fmt:formatDate pattern="dd/MM/yyyy" value="${pagoPolizaBean.poliza.colectivo.fechasegundopago}" />" readonly="readonly" />
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
			<div class="panel1 isrt">
				<fieldset>
					<table width="100%">
						<colgroup>
							<col width="15%" />
							<col width="15%" />
							<col width="15%" />
							<col width="15%" />
							<col width="*" />
						</colgroup>
						<tr>
							<td class="literal" rowspan="2">Forma de pago cliente</td>
							<td class="literal">% Primer pago cliente</td>
							<td class="literal">
								<form:input path="pctprimerpago" id="pctprimerpagocliente" size="5" maxlength="5" cssClass="dato" cssStyle="text-align: left;" onchange="calculaImportePrimerPago();rellenaPctSegundoPago(this)"/>
							</td>
							<td class="literal">Fecha primer pago</td>
							<td class="literal"><p class="txt">
								<spring:bind path="fecha">
									<input type="text" name="fecha1" id="fecha1" size="11" maxlength="10" class="dato" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${pagoPolizaBean.fecha}" />"  onchange="if (!ComprobarFecha(this, document.main, 'Fecha primer pago')) this.value='';"/>
								</spring:bind>
								<input type="button" id="btn_fechaprimerpagomodificado" name="btn_fechaprimerpagomodificado"
										class="miniCalendario" style="cursor: pointer;" />
								<span id="errorFechaSegundoPagoModificado" /></p>
							</td>
							<td class="literal">Imp.:</td>
							<td class="literal" style="white-space: no-wrap">
								<input type="text" name="importePrimerPagoCliente" id="importePrimerPagoCliente" value="${importePrimerPagoCliente}" class="dato" readonly="readonly" style="text-align: right;"/>&#8364;
							</td>
						</tr>
						<tr>
							<td class="literal">% Segundo pago cliente</td>
							<td class="literal">
								<form:input path="pctsegundopago" id="pctsegundopagocliente" size="5" maxlength="5" cssClass="dato" cssStyle="text-align: left;" onchange="calculaImporteSegundoPago();rellenaPctPrimerPago(this)"/>
							</td>
							<td class="literal">Fecha segundo pago</td>
							<td class="literal"><p class="txt">
								<spring:bind path="fechasegundopago">
									<input type="text" name="fechasegundopago1" id="fechasegundopago1" size="11" maxlength="10" class="dato" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${pagoPolizaBean.fechasegundopago}" />"  onchange="if (!ComprobarFecha(this, document.main, 'Fecha segundo pago')) this.value='';"/>
								</spring:bind>
								<input type="button" id="btn_fechasegundopagomodificado" name="btn_fechasegundopagomodificado" class="miniCalendario" style="cursor: pointer;" />
								<span id="errorFechaSegundoPago" /></p>
							</td>
							<td class="literal">Imp.:</td>
							<td class="literal" style="white-space: no-wrap">
								<input type="text" name="importeSegundoPagoCliente" id="importeSegundoPagoCliente" value="${importeSegundoPagoCliente}" class="dato" readonly="readonly" style="text-align: right;"/>&#8364;
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
		</form:form>
		<form name="altaCuentaForm" id="altaCuentaForm" action="datoAsegurado.html" method="post">
			<input type="hidden" name="operacion" value="altaCuentaPago"/>
<!--			<input type="hidden" name="ccc" id="ccc" value="${pagoPolizaBean.cccbanco }"/>-->
			<input type="hidden" name="codLinea" id="codLinea" value="${pagoPolizaBean.poliza.linea.codlinea }" />
			<input type="hidden" name="idAsegurado" id="idAsegurado" value="${pagoPolizaBean.poliza.asegurado.id }"/>
		</form>
		</div>
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaOficina.jsp"%>
	</body>
</html>