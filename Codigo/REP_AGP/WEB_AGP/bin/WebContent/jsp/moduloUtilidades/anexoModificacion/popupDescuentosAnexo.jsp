<script type="text/javascript">

$(document).ready(function(){
	$("#panelDescuentos").draggable();
	/* if ($("#descuentoLectura").val() == "true"){
		$("#descuento").attr("readonly",true); 
	}*/
});
	
function openPopupDescuentos(){
	//alert($('#recargo').val());
	if (muestraPopUp("dctoPctComisiones", "recElegido")){
		$('#panelAlertasValidacion').html("");
		$('#panelAlertasValidacion').hide();
		
		$('#panelAlertasValidacion_d').html("");
		$('#panelAlertasValidacion_d').hide();
		
		$('#panelDescuentos').css('width','35%');
		$('#overlay').show();
		$('#panelDescuentos').show();
	}else{
		$('#panelAlertasValidacion').html("Ya existe recargos aplicados para todos los grupos de negocio");
		$('#panelAlertasValidacion').show();
	}
	
}
function cerrarPopUpDescuentos(){
	// limpiamos alertas
	$('#panelAlertasValidacion_d').html("");
	$('#panelAlertasValidacion_d').hide();
	$('#panelDescuentos').hide();
	$('#overlay').hide();
}

/*FUNCIONES COMUNES A LOS POPUPS DE DESCUENTOS Y RECARGOS*/
function getCamposInput(nombreElemento){
	//alert("nombreElemento: "+nombreElemento);
	/* frm = document.getElementById("frmDescuentos");	 */
	var camposInput = document.getElementsByName(nombreElemento);						  
	return camposInput;
}

function getCamposInputPorNombre(camposInput, contiene){
	//alert("contiene: "+contiene);
	var campos=[];
	for(var i=0; i< camposInput.length; i++) {
		var name=camposInput[i].id;
		if(name.indexOf(contiene)>-1){
			campos.push(camposInput[i]);
		}			 
	}
	return campos;
}
/* ************************************************************** */
function muestraPopUp(nameCamposInput, idCampo){
	var camposInput = getCamposInput(nameCamposInput);
	var camposDescElegido = getCamposInputPorNombre(camposInput, idCampo);
	var res=false;
	for(var i=0; i< camposDescElegido.length; i++) {
		 var descElegido=camposDescElegido[i].value;
		 if(descElegido=="" || descElegido=="0" || descElegido=="0.0" || descElegido==null){
			 res=true;
			 break;
		 }
	}
	return res;
}

function aplicarDescuento(){
	limpiarAlertas ();
	var camposInput = getCamposInput("dctoPctComisiones");
	var camposGn =  getCamposInputPorNombre(camposInput, "dctoGn");
	var camposIdPctComis = getCamposInputPorNombre(camposInput,"dctoId");
	var camposDctoMax = getCamposInputPorNombre(camposInput, "dctoMax");
	var camposDctoElegido = getCamposInputPorNombre(camposInput, "dctoElegido");
	
	if (validaCampos(camposDctoElegido)){//Valida que al menos uno de los campos tenga valor			
		 var dctoGn=[];  var dctoiId=[];  var dctoMax=[]; var dctoElegido=[];		 
		 for(var i=0; i< camposGn.length; i++) {
			 dctoGn.push(camposGn[i].value);
			 dctoiId.push(camposIdPctComis[i].value);
			 dctoMax.push(camposDctoMax[i].value);
			 dctoElegido.push(camposDctoElegido[i].value);
			
			 if ($('#validarRango').val()== "true"){
				 if(dctoElegido[i]!=""){//Solo valida el campo con valor
					if (validaRangoDesc(dctoElegido[i],dctoMax[i])){
						//$('#frmDescuentos').submit();
						llamadaWebServiceDescuento();
						limpiarAlertas();
					}
				 }
			}else{
				// se valida que el descuento este entre 0 y 100
				 if(dctoElegido[i]!=""){//Solo valida el campo con valor
					if (parseFloat(dctoElegido[i])>= 0 && parseFloat(dctoElegido[i]) <= 100){
						//$('#frmDescuentos').submit();
						llamadaWebServiceDescuento();
						
						limpiarAlertas();
					}else{
						$('#panelAlertasValidacion_d').html("El descuento debe estar ente 0 y 100");
						$('#panelAlertasValidacion_d').show();
						break;
					}			
				 }
			}
		}
		
	}		 	 
}

/* function getpctDescMax(indice){
	var res;
	for(var i=0; i< $('#pctComisiones').length; i++) { 
		if(i==indice){
			
		}
	}
} */

function llamadaWebServiceDescuento(){
	cerrarPopUpDescuentos();
	muestraCapaEspera("Calculando el A.M");
	if($('#complementaria').val()==""){
		$('#frmDescuentos').submit();
	}else{//true		
		$("#frmDescuentos").attr("action","webservicesCpl.html");
		var frm = document.getElementById("frmDescuentos");
		frm.method.value = 'doCalcular';
		$('#frmDescuentos').submit();
	}
	
}


function limpiarAlertas (){
	$('#panelAlertasValidacion_d').html("");
	$('#panelAlertasValidacion_d').hide();
	$('#panelDescuentos').hide();
	$('#overlay').hide();
}
function validaCampos(campos){
	var value;
	var camposVacios=true;
	var descuentoOk=false;
	for(var i=0; i< campos.length; i++) {
		value = campos[i].value;
		if (value != ''){ 
			camposVacios=false;
			break;
		}
	}
	
	if(camposVacios){
		$('#panelAlertasValidacion_d').html("El campo Descuento es obligatorio");
		$('#panelAlertasValidacion_d').show();
		return false;
	}
	
	for(var i=0; i< campos.length; i++) {
		value = campos[i].value;
		try {		 	
	 		var auxdescuento =  value;
	 		if(!isNaN(auxdescuento)){	 			
				descuentoOk = true;
			}
		}
		catch (ex) {
			descuentoOk=false;
			break;
		}
	}	

	// Si ha habido error en la validación muestra el mensaje
		if (!descuentoOk) {
			$('#panelAlertasValidacion_d').html("Valor para Descuento no válido");
			$('#panelAlertasValidacion_d').show();
			return false;
		}

	
	return true;
	
}
function validaRangoDesc(value, pctdescmax){
	
	if (parseFloat(value)>= 0 && parseFloat(value) <= parseFloat(pctdescmax)){
		return true;
		
		
	}else{
		$('#panelAlertasValidacion_d').html("El descuento debe estár dentro del rango");
		$('#panelAlertasValidacion_d').show();
		return false;	
	}
	
}
</script>

<html>
<head>
<form:form id="frmDescuentos" name="frmDescuentos" action="calculoModificacion.html"	method="post" commandName="anexoModificacion">
	<input type="hidden" name="method" id="method" value="doCalculoModificacion"/>	
	<input type="hidden" name="operacion" id="operacion" value="calcular" />
	<input type="hidden" name="idpoliza" id="idpoliza" value="${idpoliza}" />
	<form:hidden path="id" id="idAnexo"/>
	<input type="hidden" name="idpolizacpl" id="idpolizacpl"
		value="${idpolizaCpl}" /> <input type="hidden" name="validComps"
		id="validComps" value="${validComps}" /> <input type="hidden"
		name="descuentoLectura" id="descuentoLectura"
		value="${descuentoLectura}" /> <input type="hidden"
		name="complementaria" id="complementaria" value="${complementaria}" />
	<!-- variable para conocer de donde procede la llamada (poliza o póliza complementaria y saber a qué webservice llamar -->
	<input type="hidden" name="method" id="method" />
	
	
	<input type="hidden" id="redireccion" name="redireccion" value="${redireccion}"/>
	<input type="hidden" id="errorTramite" name="errorTramite" value="${errorTramite}"/>
	<input type="hidden" id="perfil34" name="perfil34" value="${perfil34}"/>
	<input type="hidden" name="vieneDeListadoAnexosMod" id="vieneDeListadoAnexosMod" value="${requestScope.vieneDeListadoAnexosMod}"/>
	<input type="hidden" id="modoLectura" name="modoLectura" value="${modoLectura}"/>
	
	
	<div id="panelDescuentos"
		style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em; width: 60%; display: none; top: 270px; left: 33%; position: absolute; z-index: 1003; border: 1px solid #A4A4A4; background-color: #F2F2F2">

		<!--  header popup -->
		<div id="header-popup" class="header-popup"
			style="padding: 0.4em 1em; position: relative; color: #FFFFFF; font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px">
			<div
				style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Descuento</div>

			<a
				style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
				<span onclick="cerrarPopUpDescuentos()">x</span>
			</a>
		</div>

		<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelInformacion" class="panelInformacion">
				<div id="panelAlertasValidacion_d" name="panelAlertasValidacion_d" class="errorForm_fp" align="center"></div>
				<table style="width: 90%">
					<c:forEach items="${pctComisiones}" var="pctComDesc">
						<c:set var="descCount" value="${descCount + 1}" />
						<tr>
							<!-- OJO Cualquier cambio que implique un input más o menos hay que revisar el método actualizaPolizaPctComisionesDescuento de WebServiceController -->
							<td class="literal" align="center">% Descuento ${pctComDesc.descGrupoNegocio}: 
								<c:if test="${descuentoLectura eq true || 
									(pctComDesc.pctrecarelegido ne '' && pctComDesc.pctrecarelegido ne '0' && pctComDesc.pctrecarelegido ne '0.0' && pctComDesc.pctrecarelegido ne null)}">
									
									<input type="text" name="dctoPctComisiones" id="dctoElegido-${pctComDesc.grupoNegocio}" size="6"
										maxlength="6" class="dato" value="${pctComDesc.pctdescelegido}" readonly="true" />%
								</c:if> 
								<c:if test="${descuentoLectura ne true &&
										(pctComDesc.pctrecarelegido eq '' || pctComDesc.pctrecarelegido eq '0' || pctComDesc.pctrecarelegido eq '0.0' || pctComDesc.pctrecarelegido eq null)}">
									
									<input type="text" name="dctoPctComisiones" id="dctoElegido-${pctComDesc.grupoNegocio}" size="6"
										maxlength="6" class="dato" value="${pctComDesc.pctdescelegido}"
										onchange="this.value = this.value.replace(',', '.')" />%
									</c:if> 
									<input type="hidden" name="dctoPctComisiones" id="dctoId-${pctComDesc.grupoNegocio}" value="${pctComDesc.id}" />
									<input type="hidden" name="dctoPctComisiones" id="dctoMax-${pctComDesc.grupoNegocio}" value="${pctComDesc.pctdescmax}" /> 
									<%-- <input type="hidden" name="dctoPctComisiones" id="dctoGn-${pctComDesc.grupoNegocio}" value="${pctComDesc.grupoNegocio}" /> --%>
									<input type="hidden" name="dctoPctComisiones" id="dctoGn-${pctComDesc.grupoNegocio}" value="2" /> 
									<input type="hidden" name="dctoPctComisiones" id="recElegido-${pctComDesc.pctrecarelegido}" value="${pctComDesc.pctrecarelegido}" /> 
									<c:if test="${descuentoLectura == null}">
										<c:if test="${pctComDesc.pctdescmax == null}">
											<td class="literal" align="left">(0% - 0%)</td>
										</c:if>
										<c:if test="${pctComDesc.pctdescmax != null}">
											<td class="literal" align="left">(0% - ${pctComDesc.pctdescmax}%)</td>
										</c:if>
									</c:if>
							</td>
						</tr>
					</c:forEach>
				</table>
			</div>
		</div>
		<!-- Botones popup -->
		<c:if test="${descuentoLectura eq true}">
			<div style="margin-top: 15px" align="center" class="cerrarPopUpX" id="cerrarPopUp">
				<a class="bot" href="javascript:cerrarPopUpDescuentos()"
					title="Cerrar">Cerrar</a>
			</div>
		</c:if>
		<c:if test="${descuentoLectura == null}">
			<div style="margin-top: 15px" align="center" class="aplicarPopUp" id="aplicarPopUp">
				<a class="bot" href="javascript:cerrarPopUpDescuentos()"
					title="Cancelar">Cancelar</a> <a class="bot"
					href="javascript:aplicarDescuento()" title="Aplicar">Aplicar</a>
			</div>
			
			<!-- Pintamos el botón de cerrar no visible para poder visualizarlo despúes de la grabación provisional,
			de una póliza complementaria, como si fuera en modo lectura. El código se encuentra en ImportesCpl.js --> 
			<div style="margin-top: 15px; display:none;" align="center" class="cerrarPopUp" id="cerrarPopUp">
				<a class="bot" href="javascript:cerrarPopUpDescuentos()"
					title="Cerrar">Cerrar</a>
			</div>
		</c:if>
	</div>
</form:form>
</head>
</html>