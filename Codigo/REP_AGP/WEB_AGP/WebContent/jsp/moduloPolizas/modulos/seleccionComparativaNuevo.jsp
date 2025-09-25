<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>




<html>
	<head>
		<title>Selección de comparativa</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
				
		<script type="text/javascript" src="jsp/js/util.js"></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
	
	
	    <script type="text/javascript">
		
		
		// Para evitar el cacheo de peticiones al servidor
            $(document).ready(function(){
                 var URL = UTIL.antiCacheRand(document.getElementById("frmAux").action);
			     document.getElementById("frmAux").action = URL;    
            });

		
		 $(document).ready(function(){	
		 		$("input[type=checkbox]").each(function(){ 				 					        
				        if($('#modoLectura').val() == 'modoLectura'){
				        	$(this).attr('disabled','disabled');
				        }
				 });
				 			
				$('#frmAux').validate({
					 errorLabelContainer: "#panelAlertasValidacion",
   					 wrapper: "li",
   					  highlight: function(element, errorClass) {
					 	$(".campoObligatorio").show();
  					 },
  					 unhighlight: function(element, errorClass) {
					 	if(checksRellenos())
					 	$(".campoObligatorio").hide();
  					 },
   					 rules: {
   						 "comparativaCheck": {checksRellenos: true,maxCheck: true}
   					 },
					messages: {
						"comparativaCheck": {checksRellenos: "Debe seleccionar al menos una opción",maxCheck: "Ha superado el número máximo de comparativas elegibles {" + $('#maxChecks').val() +"}" }
					}					  
   				});
   		});		
     	
  		jQuery.validator.addMethod("maxCheck",maxChecks);
     	function maxChecks(){
     		 var check = $("(input:checkbox):checked");
     		return check.length <= $('#maxChecks').val();
     	}
     	
     	jQuery.validator.addMethod("checksRellenos",checksRellenos);
		
		function checksRellenos() { 
			 var checksRellenos = $("(input:checkbox):checked");
     		 return checksRellenos.length > 0;
     	}
     	
     	function continuarWrapper(){
     	    if($('#tieneParcelas').val() == "si" && $('#modoLectura').val() != 'modoLectura'){
     	    	$('#overlay').show();
     	    	$("#popupRecalcular").show();
     	    }else{
     	    	continuar("no");
     	    }
     	}
     	
		function continuar(recalcular) {
            $("#popupRecalcular").hide();
            $('#overlay').hide();
            $("#recalcular").val(recalcular);

			if($("#frmAux").valid())
			{    
				$.blockUI.defaults.message = '<h4> Redirigiendo.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
				$('#action').val("seleccionComp");
				activados($("(input:checkbox):checked"));  
				frmeleccomparativa.submit();	
			}					   			 	
     	}
     	
     	function volverComparativa() {
     		$('#action').val("");
     		$.blockUI.defaults.message = '<h4> Calculando las condiciones de coberturas de los módulos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
       		var frmeleccomparativa = document.getElementById("frmeleccomparativa");
			frmeleccomparativa.href = "polizaController.html?rand=" + UTIL.getRand();
            frmeleccomparativa.submit();
		}		 
		 
		function activados(checksRellenos) {
     	  $('#seleccionados').val("");   		  		   			 	
     		
     	  for(var i=0; i<checksRellenos.length; i++){ 
     		document.getElementById('seleccionados').value += checksRellenos[i].value;     			 	
     	  }  						
     	}	
     	function eligeMenu(){
			if($('#vieneDeUtilidades').val() == 'true'){
			 	SwitchMenu('sub4');
			 }else{
			 	SwitchMenu('sub3');
			 }
		 }	
	</script>
	
	</head>	
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="eligeMenu();">	
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<c:choose>
			<c:when test="${listaModulos.modoLectura == 'modoLectura' && listaModulos.vieneDeUtilidades == 'true'}">
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
				<table cellspacing="2" cellpadding="0" border="0">
					<tbody>
						<tr>
							<td>
								<a class="bot" id="btnVolver" href="javascript:volverComparativa();">Volver</a>
							</td>
							<td>
								<a class="bot" id="btnContinuar" href="javascript:continuarWrapper();">Continuar</a>
							</td>											
						</tr>
					</tbody>
				</table>
				<!-- FIN TABLA BARRA DE BOTONES-->
			</td>
		</tr>
	</table>
	</div>	
	<!-- Contenido de la página -->
	<form action="polizaController.html" method="post" name="frmeleccomparativa" id="frmeleccomparativa">
		<input type="hidden" name="modoLectura" id="modoLectura" value="${listaModulos.modoLectura }"/>
		<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${listaModulos.vieneDeUtilidades}"/>
		<input type="hidden" id="action" name="action" value=""/>
		<input type="hidden" id="maxChecks" name="maxChecks" value="${listaModulos.numeroMaxComparativas}"/>
		<input type="hidden" name="seleccionados" id="seleccionados" value=""/>
		<input type="hidden" name="check" value="${check}"/>
		<input type="hidden" name="idpoliza" value="${listaModulos.idpoliza }"/>
		<input type="hidden" name="parcelasWeb" value="true"/>
		<input type="hidden" id="recalcular" name="recalcular" value=""/>
		<input type="hidden" id="tieneParcelas" name="tieneParcelas" value="${listaModulos.tieneParcelas}"/>
		
		
	</form>
	<form method="post" name="frmAux" id="frmAux">
		<input type="hidden" name="modoLectura" id="modoLectura" value="${detCoberturas.modoLectura }"/>
		<div class="conten">
		<p class="titulopag" align="left">Selección de comparativas</p>
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
		<%int i=0; %>
		<c:choose>
		<c:when test="${not empty listaModulos.comparativa}">
		<c:forEach items="${listaModulos.comparativa}" var="modulo" varStatus="i">
			<fieldset>
				<legend class="literal">MODULO ${modulo.key}</legend>
				<table width="100%">
					<tr>
						<td class="literalbordeCabecera" align="center" width="20%">Garantía</td>
						<td class="literalbordeCabecera" align="center" width="25%">Riesgo Cubierto</td>
						<td class="literalbordeCabecera" align="center" width="20%">Cobertura</td>
						<td class="literalbordeCabecera" align="center" width="20%">Valor</td>
						<td class="literalbordeCabecera" align="right" width="5%">Sel</td>
					</tr>
				
				<c:forEach items="${modulo.value}" var="filaModulo" varStatus="status">
				<tr>
					<td class="literalborde" align="center" colspan="4">
						<c:set var="compara" value=""/>
						<c:set var="comparativaTodosConElementosMenos2" value="true"/>
							<c:forEach items="${filaModulo}" var="fila">
								<c:set var="compara" value="${compara}${fila.id.codmodulo}|${fila.id.filamodulo}|${fila.id.codconceptoppalmod}|${fila.id.codriesgocubierto}|${fila.id.codconcepto}|${fila.id.codvalor}|${status.count}|${fila.id.desvalor};"/>
								<c:set var="comparaCheck" value="${fila.id.codmodulo}|${fila.id.filamodulo}|${fila.id.codconceptoppalmod}|${fila.id.codriesgocubierto}|${fila.id.codconcepto}|${fila.id.codvalor}|${status.count}"/>
								<c:forEach items="${fila.filasVinculadas}" var="filaVinculada">
									<c:set var="compara" value="${compara}${filaVinculada.id.codmodulo}|${filaVinculada.id.filamodulo}|${filaVinculada.id.codconceptoppalmod}|${filaVinculada.id.codriesgocubierto}|${filaVinculada.id.codconcepto}|${filaVinculada.id.codvalor}|${status.count}|${filaVinculada.id.desvalor};"/>
								</c:forEach>

								<c:if test="${fila.id.codvalor != -2}">
									<c:set var="comparativaTodosConElementosMenos2" value="false"/>
									<table width="100%">
									<tr>
										<td class="literalborde" width="20%">${fila.id.desconceptoppalmod}&nbsp;</td>
										<td class="literalborde" width="26%">${fila.id.desriesgocubierto}&nbsp;</td>
										<td class="literalborde" width="20%">${fila.id.nomconcepto}&nbsp;<c:forEach items="${fila.filasVinculadas}" var="filaVinculada"><br/>${filaVinculada.id.nomconcepto}</c:forEach></td>
										<td class="literalborde" width="20%">${fila.id.desvalor}&nbsp;<c:forEach items="${fila.filasVinculadas}" var="filaVinculada"><br/>${filaVinculada.id.desvalor}</c:forEach></td>
									</tr>
									</table>
								</c:if>
							</c:forEach>
							<c:if test="${comparativaTodosConElementosMenos2}">
								<table width="100%">
								<tr>
									<td class="literalborde" colspan="4">Sin riesgos cubiertos elegibles</td>
								</tr>
								</table>
							</c:if>
							
						</td>

						<c:set var="check" value="false"/>
						
						<c:forEach items="${listaModulos.comparativasPoliza}" var="comparativasPoliza">
							<c:set var="compPoliza"	value="${comparativasPoliza.id}"/>
							<c:set var="compPolizaCheck" value="${compPoliza.codmodulo}|${compPoliza.filamodulo}|${compPoliza.codconceptoppalmod}|${compPoliza.codriesgocubierto}|${compPoliza.codconcepto}|${compPoliza.codvalor}|${compPoliza.filacomparativa}"/>
							<c:if test="${comparaCheck == compPolizaCheck}" >
								<c:set var="check" value="true"/>
							</c:if>	
						</c:forEach>
						 					
						<td valign="middle" style="border-width: 1px; border-style: solid; border-color: #004539;" align="center" width="5%"><input type="checkbox" id="comparativaCheck" name="comparativaCheck" align="center" value="${compara}" <c:if test="${check == 'true' }">checked</c:if>/>
							<span id="errorCompSelected<%=i%>"/>
							<label class="campoObligatorio" id="campoObligatorio" title="Campo obligatorio"> *</label>
							<c:set var="compara" value=""/>
						</td>
						
					</tr>
					</c:forEach>
				</table>	
					<c:set var="modu" value="${fn:substring(modulo.key,0,1)}"/>
					<c:forEach var="element" items="${listaModulos.msjModulo}">
						<c:if test="${element.key ==  modu && element.value !='' && element != null}">
							<div style="font-size:10px;color:#004539;text-align:justify;padding:5px">${element.value}</div>
						</c:if>
					</c:forEach>	
			</fieldset>
		</c:forEach>
		</c:when>
		<c:otherwise>
			No hay comparativas disponibles.
		</c:otherwise>
		</c:choose>
		</div>
	</form>
	
	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
	<!--  POPUPS -->
	<%@ include file="/jsp/moduloPolizas/polizas/popupRecalcular.jsp"%>
	
	
	
	
	
</body>
</html>