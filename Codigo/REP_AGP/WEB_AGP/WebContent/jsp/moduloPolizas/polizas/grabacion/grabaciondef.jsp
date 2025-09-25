<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
	<head>
		<title>Grabacion definitiva poliza</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		
		<script type="text/javascript" src="jsp/js/util.js"></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
		<script type="text/javascript" src="jsp/js/calendar.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>		
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>	
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/iban.js" ></script>	
		<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/importes/datosAval.js"></script>
		
		<%@ include file="/jsp/js/draggable.jsp"%>
		<%@ include file="/jsp/js/generales.jsp"%>
		
		<script type="text/javascript">
		
		
		     $(document).ready(function(){
		    	<c:if test="${polizaComplementaria}">
					$('#btnImprimir').show();
				</c:if>
				
				// Popup Forma pago
				/* if ($('#showPopupFormaPago').val() == "true"){
					$('#overlay').show();
					($('#panelFormaPago')).show();
			    } */
				// boton Forma Pago
				/* if ($('#showBotonFormaPago').val() == "true"){
					($('#btnFormaPago')).show();
				} */
			    
				if ($('#mpPagoC').val() == "true"){
				    $('#btnPagos').show();
			    }
			  
        	 });
		
			function salir(){
				var frm = document.getElementById('main');				
				frm.operacion.value = 'salir';
				frm.target="_self";
				frm.submit();
			}
			
			function grabarDef(){				
				
       			// Comprueba si hay que mostrar el pop-up de datos de aval
       			ajax_muestraDatosAval($('#idpoliza').val());
       			
			}
			
			function continuarPDCpl() {
				// Mensaje de aviso de paso a definitiva
				$.blockUI.defaults.message = '<h4>Pasando la póliza a definitiva.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
       			
				// Hace submit en el formulario de paso a definitiva
       			var frm = document.getElementById('pasarADefinitiva');				
				frm.target="_self";       			 
				frm.submit();
			}
			
			function datosPago(){
				$.blockUI.defaults.message = '<h4>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } }); 
				$('#datosPago').submit();
			}
			
			function getModuleAndSave(checkRevision, formulario, campo, idpoliza, importeFin) {
				if(importeFin != null){
					$('#netoTomadorFinanciadoAgr').val(importeFin);
				}
				if ($('#esAgr').val() == 'false'){
					$('#esAgrSend').val($('#esAgr').val());
					$('#esSaecaVal').val(importeFin);
				}
				
				/* var hiddens = document.importes.idEnvioComp;
				var radios = document.importes.modElegido;
				var seleccionado = false;
				for ( var i = 0; i < radios.length; i++) {
					if (radios[i].checked == true) {
						document.grabar.modSeleccionado.value = radios[i].value;
						document.grabar.idEnvio.value = document.importes.elements['idEnv' + (i)].value;
						document.grabar.importeSeleccionado.value = document.importes.elements['importeC' + (i)].value;

						document.irAPagos.modSeleccionado.value = radios[i].value;
						document.irAPagos.idEnvio.value = document.importes.elements['idEnv'+ (i)].value;
						document.irAPagos.importeSeleccionado.value = document.importes.elements['importeC'	+ (i)].value;

						document.grProvisional.modSeleccionado.value = radios[i].value;
						document.grProvisional.idEnvio.value = document.importes.elements['idEnv'+ (i)].value;
						
						document.grProvisional.importeSeleccionado.value = document.importes.elements['importeC' + (i)].value;
						
						seleccionado = true;
						break;
					}
				} 
				
				// Si no se ha seleccionado ningún módulo
				if (!seleccionado) {
					alert("Debe seleccionar un módulo para continuar...");
				} else {
					$.blockUI.defaults.message = '<h4> Grabando los importes.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
					$.blockUI({
						overlayCSS : {
							backgroundColor : '#525583'
						}
					});
					if (checkRevision == 'noRevision') {
						document.grabar.noRevPrecioProduccion.value = 'true';
					}
					
					if (formulario == 'grProvisional' || formulario == 'grabar') {
					// Se lanza la llamada ajax para guardar la distribución de costes
					ajaxGuardarDistCoste(formulario);
					}
					// Cualquier otra acción
					else {
						generales.enviarForm(formulario, campo, idpoliza);
					}
				}*/
				generales.enviarForm(formulario, campo, idpoliza);
			}
			
			function imprimir(){
				var frm = document.getElementById('main');
				frm.operacion.value = 'imprimirPoliza';
				frm.target="_blank";
				frm.submit();
			} 
								

			 /*
			 *  show popup
			 */
		  	 function showPopUpAviso(mensaje){
		  	     $('#txt_mensaje_aviso_1').html(mensaje);
		  	     $('#overlay').show();
		  	     $('#popUpAvisos').show();  
		  	 }
		  	 
		  	 /**
		  	 * hide popup
		  	 */
		  	 function hidePopUpAviso(){
		  	     $('#popUpAvisos').hide();
		  	     $('#overlay').hide();
		  	 } 
		  	function showPopupFormaPago(){
		  		$('#overlay').show();
				 var frm = document.getElementById('frmFormapago');
				 if ($('#mpPagoC').val() == 'true') {
				     frm.datosCuentaId.checked = true;
				     muestraDatosCuenta();
				     frm.pagoManualId.checked = false;
				 } else {
					 frm.datosCuentaId.checked = false;
				     frm.pagoManualId.checked = true;
				     muestraDatosPagoManual();
				 }
				 ($('#panelFormaPago')).show();
			 }

		</script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub3');">
	    <%@ include file="/jsp/common/static/overlay.jsp"%>
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabecera.jsp"%>
		
		<!-- MPM 03/05/12 -->
		<form name="pasarADefinitiva" id="pasarADefinitiva" action="pasoADefinitiva.html" method="post" commandName="polizaDefinitiva">
			<input type="hidden" name="method" id="method" value="doPasarADefinitiva"/>		
			<form:hidden path="polizaDefinitiva.idpoliza" id="idpoliza"/>
			<input type="hidden" name="resultadoValidacion" id="resultadoValidacion"/>
			<input type="hidden" name="grabFueraContratacion" id="grabFueraContratacion"/>
			<input type="hidden" name="actualizarSbp" id="actualizarSbp"/>				
			<input type="hidden" name="cicloPoliza" id="cicloPoliza" value="cicloPoliza"/>
			<input type="hidden" name="numeroAval" id="numeroAval" value="" /> 
			<input type="hidden" name="importeAval" id="importeAval" value="" />
			<input type="hidden" name="pdComplementaria" id="pdComplementaria" value="" />
		</form>
		
		<form:form name="datosPago" method="post" id="datosPago" action="pagoPoliza.html">	
			<input type="hidden" id="idpoliza" name="idpoliza" value="${idpoliza}"/>
			<input type="hidden" id="modoLectura" name="modoLectura" value="${modoLectura}" />
			<input type="hidden" id="vieneDeUtilidades" name="vieneDeUtilidades" value="grabacionProv" />
		</form:form>
		
		<form name="irAPagos" action="webservices.html" method="post" id="irAPagos">
			<input type="hidden" name="operacion" id="operacion" value="grabar" />
			<input type="hidden" name="idpoliza" id="consulta_idpoliza"
			value="${idpoliza}" /> <input type="hidden" name="modSeleccionado"
			id="modSeleccionado" value="" /> <input type="hidden"
			name="noRevPrecioProduccion" id="noRevPrecioProduccion" value="" /> <input
			type="hidden" name="importeSeleccionado" id="importeSeleccionado"
			value="" /> <input type="hidden" name="revPagos" id="revPagos"
			value="true" /> <input type="hidden" name="idEnvio" id="idEnvio"
			value="${idEnvio}" /> <input type="hidden" name="modoLectura"
			id="modoLectura" value="${modoLectura}" /> <input type="hidden"
			name="origenllamada" id="origenllamada" value="pago" /> <input
			type="hidden" name="grProvisionalOK" id="grProvisionalOK"
			value="${grProvisionalOK}" />
			<input type="hidden" id="vieneDeUtilidades" name="vieneDeUtilidades" value="grabacionProv" />
			<input type="hidden" name="esAgrSend" id="esAgrSend" value="" />
			<input type="hidden" name="esSaecaVal" id="esSaecaVal" value="" />
		</form>
		
		<form action="grabacionPoliza.html" method="post" name="main" id="main">
			<input type="hidden" id="operacion" name="operacion"/>
			<!-- <input type="hidden" id="idpoliza" name="idpoliza" value="${grabDef.idPolizaDefinitiva}"/> -->
			<form:hidden path="polizaDefinitiva.idpoliza" id="idpoliza"/>
		</form>
		<%-- <input type="hidden" name="showPopupFormaPago" id="showPopupFormaPago" value="${showPopupFormaPago}"/> --%>
		<%-- <input type="hidden" name="showBotonFormaPago" id="showBotonFormaPago" value="${showBotonFormaPago}"/> --%>
		<input type="hidden" name="mpPagoM" id="mpPagoM" value="${mpPagoM}"/>
		<input type="hidden" name="mpPagoC" id="mpPagoC" value="${mpPagoC}"/>
		<input type="hidden" name="numeroCuenta" id="numeroCuenta" value="${numeroCuenta}"/>
		<input type="hidden" name="importe1" id="importe1" value="${importe1}"/>
		<input type="hidden" name="banDestino" id="banDestino" value="${banDestino}"/>
		<input type="hidden" name="import" id="import" value="${import}"/>
		<input type="hidden" name="fecPago" id="fecPago" value="${fecPago}"/>
		<input type="hidden" name="externo" id="externo" value="${externo}"/>
		<input type="hidden" name="importeSaeca" id="importeSaeca" value="${importeSaeca}"/>
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr align="left">
					<td align="center">
						<!-- <a class="bot" href="#" id="btnFormaPago" onclick="javascript:showPopupFormaPago()" style="display:none">Forma Pago</a> -->
						<a class="bot" id="btnPagosLectura" href="javascript:datosPago();"  style="display:none">C/c-Oficina</a>
						
						<c:choose>
							<c:when test="${importeSaeca != null && importeSaeca!=''}">
								<a class="bot" href="#" id="btnPagos" onclick="javascript:getModuleAndSave('noRevision','irAPagos','consulta_idpoliza','${idpoliza}', ${importeSaeca});" style="display: none">C/c-Oficina</a>
							</c:when>
							<c:otherwise>
								<a class="bot" id="btnPagos" href="javascript:datosPago();" style="display:none">C/c-Oficina</a>
							</c:otherwise>
						</c:choose>
					</td>
					
					<td align="center">
						<a class="bot" id="btnImprimir" href="javascript:imprimir()">Imprimir</a>
					</td>
					 
					<td align="right">
						<c:if test="${grabDef.mostrarBtnDef ne 'NO'}">
							<a class="bot" id="btnDefinitiva" href="javascript:grabarDef()">Grabar definitiva</a>
						</c:if>
						<a class="bot" id="btnSalir" href="javascript:salir()">Salir</a>										
					</td>
				</tr>
			</table>
		</div>
		
		

		
		<div class="conten" style="padding:3px;width:97%">
			<p class="titulopag" align="left">${grabDef.titulo}</p>
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			<br>
			<fieldset style="width:100%">
				<table width="97%">
					<tr>
						<td width="97%" class="literal" align="center">
							${grabDef.mensajeCentral}					
						</td>
					</tr>
					<tr>
						<td width="97%" class="literal" align="center">
						${grabDef.mensajeGrabacion}
						</td>
					</tr>
					<tr>
						<td width="97%" class="literal" align="center">
						${grabDef.mensajeSuplemento}
						</td>
					</tr>
			</table>
			</fieldset>
			
		</div>

		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		<!--               -->
		<!-- POPUPS AVISO  -->
		<!--               -->
		
		<!-- *** popUp pasar definitiva icon row *** -->
		<div id="popUpAvisos" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;z-index:1005">
		     <!--  header popup -->
			 <div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
				        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
				            Aviso
				        </div>
				        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
				                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
				            <span onclick="hidePopUpAviso()">x</span>
				        </a>
			 </div>
			 <!--  body popup -->
			 <div class="panelInformacion_content">
						<div id="panelInformacion" class="panelInformacion">
							<div id="txt_mensaje_aviso_1">sin mensaje.</div>
						</div>
						<div style="margin-top:15px">
						    <a class="bot" href="javascript:hidePopUpAviso()" title="Cancelar">Cancelar</a>
						    <a class="bot" id="btn_aceptarGrabacionDefinitiva" href="javascript:aceptarPopUpPasoDefinitiva()" title="Cancelar">Aceptar</a>
						</div>
			 </div>
		</div>
		<%-- <%@ include file="/jsp/moduloPolizas/polizas/importes/popupFormaPago.jsp"%> --%>
		<%@ include file="/jsp/common/lupas/lupaBanco.jsp"%>
		<%@ include	file="/jsp/moduloPolizas/polizas/importes/popupDatosAval.jsp"%>
	</body>
	
</html>
