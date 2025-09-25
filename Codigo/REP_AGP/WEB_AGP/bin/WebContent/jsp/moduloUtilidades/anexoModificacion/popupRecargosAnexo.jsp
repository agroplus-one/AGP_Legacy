<script type="text/javascript">

$(document).ready(function(){
	$("#panelRecargos").draggable();
	/* if ($("#recargoLectura").val() == "true"){
		var camposInput = getCamposInput("recPctComisiones");
		 for(var i=0; i< camposInput.length; i++) {
			 if(camposInput[i].type=="text"){
				camposInput[i].attr("readonly",true);
			 }
		 }		
	}	 */
});


function openPopupRecargos(){	
	if (muestraPopUp("recPctComisiones", "descElegido")){
		$('#panelAlertasValidacion').html("");
		$('#panelAlertasValidacion').hide();
		
		$('#panelAlertasValidacion_r').html("");
		$('#panelAlertasValidacion_r').hide();
		
		$('#panelRecargos').css('width','35%');
		$('#overlay').show();
		$('#panelRecargos').show();
	}else{
		$('#panelAlertasValidacion').html("Ya existen descuentos aplicados para todos los grupos de negocio.");
		$('#panelAlertasValidacion').show();
	} 
}
	

function cerrarPopUpRecargos(){
	// limpiamos alertas
	$('#panelAlertasValidacion_r').html("");
	$('#panelAlertasValidacion_r').hide();
	$('#panelRecargos').hide();
	$('#overlay').hide();
}

function aplicarRecargos(){
		//Estas funciones están en el popup de descuentos
		var camposInput = getCamposInput("recPctComisiones");
		var camposRecElegido = getCamposInputPorNombre(camposInput, "recElegido");

		// se valida que el Recargo esté entre 0 y 999,99
		var resValidacion=true
		 for(var i=0; i< camposRecElegido.length; i++) {
			 var recElegido=camposRecElegido[i].value;
			 if(!isNaN(recElegido)){
					if(recElegido >=0 && recElegido <= 999.99) 
						resValidacion = true;
					else
						resValidacion = false;
			}else
				resValidacion = false;
		 }
		
		if (resValidacion){
			llamadaWebServiceRecargo();
			//$('#frmRecargos').submit();			
			//limpiarAlertas();
		}else{
			$('#panelAlertasValidacion_r').html("El Recargo debe estar ente 0 y 999.99");
			$('#panelAlertasValidacion_r').show();
		}	
}

function llamadaWebServiceRecargo(){
	
	if($('#complementaria').val()==""){
		var camposInput = getCamposInput("recPctComisiones");
		var camposRecElegido = getCamposInputPorNombre(camposInput, "recElegido");
		 var recElegido=camposRecElegido[0].value;
		 limpiarAlertas();
		 muestraCapaEspera("Calculando el A.M");
		comprobarTotalRecargo($('#idAnexo').val(),recElegido);
			
		
	}else{//true
		$("#frmRecargos").attr("action","webservicesCpl.html");
		var frm = document.getElementById("frmRecargos");
		frm.method.value = 'doCalcular';
		$('#frmRecargos').submit();	
	}
	
}
function comprobarTotalRecargo(idAnexo,recargo){
	
$.ajax({
	url:          UTIL.antiCacheRand("calculoModificacion.html"),
	data:         "method=doComprobarTotalRecargo&idAnexo=" + idAnexo + "&recargo=" + recargo,
	async:        true,
	contentType:  "application/x-www-form-urlencoded",
	dataType:     "json",
	global:       false,
	ifModified:   false,
	processData:  true,
	error: function(objeto, quepaso, otroobj){
		alert("Error: " + quepaso);
		$.unblockUI();
	},
	success: function(resultado){
			if(resultado.alerta ==''){
			
			$('#frmRecargos').submit();
			
		}else{
			
			$.unblockUI();
			openPopupRecargos();
			$('#panelAlertasValidacion_r').html(resultado.alerta);
			$('#panelAlertasValidacion_r').show();
			return false;
		}
			
	},
	type: "GET"
});	
}

function limpiarAlertas (){
	$('#panelAlertasValidacion_r').html("");
	$('#panelAlertasValidacion_r').hide();
	$('#panelRecargos').hide();
	$('#overlay').hide();
}
function validaCampoRecargos(){
	
	if ($('#recargo').val() != ''){ 
		var recargosOk = false;
	 	try {		 	
	 		var auxRecargos =  $('#recargo').val();
	 		if(!isNaN(auxRecargos)){
	 			$('#recargo').val(auxRecargos);
				recargosOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validación muestra el mensaje
		if (!recargosOk) {
			$('#panelAlertasValidacion_r').html("Valor para Recargos no válido");
			$('#panelAlertasValidacion_r').show();
			return false;
		}
		
	}else{
		$('#panelAlertasValidacion_r').html("El campo Recargos es obligatorio");
		$('#panelAlertasValidacion_r').show();
		return false;
	}
	return true; 
	
}

</script>

<html>
	<head>
		<form:form id="frmRecargos" name="frmRecargos" action="calculoModificacion.html" method="post" commandName="anexoModificacion">
			<input type="hidden" name="method" id="method" value="doCalculoModificacion"/>
			<input type="hidden" name="operacion" id="operacion" value="calcularRecargo"/>
			<input type="hidden" name="idpoliza" id="idpoliza" value="${idpoliza}"/>
			<input type="hidden" name="idpolizacpl" id="idpolizacpl" value="${idpolizaCpl}"/>
			<input type="hidden" name="validComps" id="validComps" value="${validComps}"/>
			<input type="hidden" name="recargoLectura" id="recargoLectura" value="${recargoLectura}"/>
			<input type="hidden" name="complementaria" id="complementaria" value="${complementaria}"/><!-- variable para conocer de donde procede la llamada (poliza o póliza complementaria y saber a qué webservice llamar -->
			<form:hidden path="id" id="idAnexo"/>
			<input type="hidden" id="redireccion" name="redireccion" value="${redireccion}"/>
			<input type="hidden" id="errorTramite" name="errorTramite" value="${errorTramite}"/>
			<input type="hidden" id="perfil34" name="perfil34" value="${perfil34}"/>
			<input type="hidden" name="vieneDeListadoAnexosMod" id="vieneDeListadoAnexosMod" value="${requestScope.vieneDeListadoAnexosMod}"/>
			<input type="hidden" id="modoLectura" name="modoLectura" value="${modoLectura}"/>
			<div id="panelRecargos" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em;
					width: 20%; display: none; top: 270px; left: 33%; 
                    position: absolute; z-index: 1003;border:1px solid #A4A4A4;background-color:#F2F2F2">
			
				<!--  header popup -->
				<div id="header-popup" class="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; 
						font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px" >
					<div style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Recargo</div>
					
					<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; 
						top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
						<span onclick="cerrarPopUpRecargos()">x</span>
					</a>
				</div>
			
				<!--  body popup -->
				<div class="panelInformacion_content">
					<div id="panelInformacion" class="panelInformacion">
					<div id="panelAlertasValidacion_r" name="panelAlertasValidacion_r" class="errorForm_fp" align="center"></div>
						<table style="width:90%">
							<%-- <tr><input type="text" name="jeje" id="jeje" value="jeje - ${recargoLectura}"  /></tr> --%>
							<c:forEach items="${pctComisiones}" var="pctComRec">
							<c:set var="recCount" value="${recCount + 1}" />
								<tr>
									<td class="literal" align="center">Recargo ${pctComRec.descGrupoNegocio}:
										<!-- OJO Cualquier cambio que implique un input más o menos hay que revisar el método actualizaPolizaPctComisionesRecargo de WebServiceController -->
										<c:if test="${recargoLectura eq true || 
											(pctComRec.pctdescelegido ne '' && pctComRec.pctdescelegido ne '0' && pctComRec.pctdescelegido ne '0.0' && pctComRec.pctdescelegido ne null)}" >
												<input type="text" name="recPctComisiones" id="recElegido-${pctComRec.descGrupoNegocio}" size="6" maxlength="6" class="dato" value="${pctComRec.pctrecarelegido}" readonly="true"/>%
										</c:if>
										<c:if test="${recargoLectura ne true &&
											(pctComRec.pctdescelegido eq '' || pctComRec.pctdescelegido eq '0' || pctComRec.pctdescelegido eq '0.0' || pctComRec.pctdescelegido eq null)}" >
												<input type="text" name="recPctComisiones" id="recElegido-${pctComRec.descGrupoNegocio}" size="6" maxlength="6" class="dato" value="${pctComRec.pctrecarelegido}" onchange="this.value = this.value.replace(',', '.')"/>%
										</c:if>											
										<input type="hidden" name="recPctComisiones" id="recId-${pctComRec.grupoNegocio}" value="${pctComRec.id}"  />
								   <%-- <input type="hidden" name="recPctComisiones" id="recGn-${pctComRec.grupoNegocio}" value="${pctComRec.grupoNegocio}" /> --%>
										<input type="hidden" name="recPctComisiones" id="recGn-${pctComRec.grupoNegocio}" value="2" /> 
										<input type="hidden" name="recPctComisiones" id="descElegido-${pctComRec.pctdescelegido}" value="${pctComRec.pctdescelegido}"/>
									</td> 
								</tr>
							</c:forEach>
							
						<%-- 	<tr>
							    <td class="literal" align="center">Recargo:
									<input type="text" name="recargo" id="recargo" size="6" maxlength="6" class="dato" value="${recargo}" onchange="this.value = this.value.replace(',', '.')"/>%
								</td>
							</tr> --%>
						</table>
					</div>
				</div>
			    <!-- Botones popup --> 
			    <c:if test="${recargoLectura eq true}">
				    <div style="margin-top:15px" align="center" class="cerrarPopUpX" id="cerrarPopUp">
				        <a class="bot" href="javascript:cerrarPopUpRecargos()" title="Cerrar">Cerrar</a>
					</div>
				</c:if>
				<c:if test="${recargoLectura == null}">
				    <div style="margin-top:15px" align="center" class="aplicarPopUp" id="aplicarPopUp">
				        <a class="bot" href="javascript:cerrarPopUpRecargos()" title="Cancelar">Cancelar</a>
						<a class="bot" href="javascript:aplicarRecargos()" title="Aplicar">Aplicar</a>
					</div>
					
					<!-- Pintamos el botón de cerrar no visible para poder visualizarlo despúes de la grabación provisional,
						de una póliza complementaria, como si fuera en modo lectura. El código se encuentra en ImportesCpl.js -->
					<div style="margin-top:15px; display:none;" align="center" class="cerrarPopUp" id="cerrarPopUp">
				        <a class="bot" href="javascript:cerrarPopUpRecargos()" title="Cerrar">Cerrar</a>
					</div>
				</c:if>
			</div>
		</form:form>
	</head>
</html>