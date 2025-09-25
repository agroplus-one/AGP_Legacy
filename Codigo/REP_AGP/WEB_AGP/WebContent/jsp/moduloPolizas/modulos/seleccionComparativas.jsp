<!-- 
/*
**************************************************************************************************
*
*  CREACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034  04/08/2010  Ernesto Laura		Página para la eleccion de modulos    
*
 **************************************************************************************************
*/
-->
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
	
	    <script type="text/javascript">
	

		
		 $(document).ready(function(){				
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
     	
		function continuar() {		
			if($("#frmAux").valid()){    
				$('#action').val("seleccionComp");
				activados($("(input:checkbox):checked"));  
				frmeleccomparativa.submit();	
			}					   			 	
     	}
     	
     	function volverComparativa() {
     		$('#action').val("");
			frmeleccomparativa.submit();	
		}		 
		 
		function activados(checksRellenos) {
     	  $('#seleccionados').val("");   		  		   			 	
     		
     	  for(var i=0; i<checksRellenos.length; i++){ 
     		document.getElementById('seleccionados').value += checksRellenos[i].value;     			 	
     	  }  						
     	}		
	</script>
	
	</head>	
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub3')">	
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabecera.jsp"%>
	
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
								<a class="bot" id="btnContinuar" href="javascript:continuar();">Continuar</a>
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
		<input type="hidden" id="action" name="action" value=""/>
		<input type="hidden" id="maxChecks" name="maxChecks" value="${listaModulos.numeroMaxComparativas}"/>
		<input type="hidden" name="seleccionados" id="seleccionados" value=""/>
		<input type="hidden" name="check" value="${check}"/>
		<input type="hidden" name="idpoliza" value="${listaModulos.idpoliza }"/>
	</form>
	<form method="post" name="frmAux" id="frmAux">
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
				<c:forEach items="${modulo.value}" var="filaModulo">
				<tr>
					<td class="literalborde" align="center" colspan="4">
						<c:set var="compara" value=""/>
							<c:forEach items="${filaModulo.filasCompMod}" var="fila">
							<% i++;%>
							<table width="100%">
							<tr>
								<c:set var="compara" value="${compara}${fila.codmodulo}|${fila.filamodulo}|${fila.codconceptoppalmod}|${fila.codriesgocubierto}|${fila.codconcepto}|${fila.codvalor}|${filaModulo.filaComparativa}|${fila.desvalor}"/>
								<c:set var="comparaCheck" value="${fila.codmodulo}|${fila.filamodulo}|${fila.codconceptoppalmod}|${fila.codriesgocubierto}|${fila.codconcepto}|${fila.codvalor}|${filaModulo.filaComparativa}"/>
								<c:if test="${fila.elegible == 'S'}" >
									<c:set var="compara" value="${compara}|${fila.filaelegible.codmodulo}|${fila.filaelegible.filamodulo}|${fila.filaelegible.codconceptoppalmod}|${fila.filaelegible.codriesgocubierto}|${fila.filaelegible.codconcepto}|${fila.filaelegible.codvalor}|${fila.filaelegible.desvalor}"/>
								</c:if>
								<c:set var="compara" value="${compara}|${fila.filavinculada.codmodulo}|${fila.filavinculada.filamodulo}|${fila.filavinculada.codconceptoppalmod}|${fila.filavinculada.codriesgocubierto}|${fila.filavinculada.codconcepto}|${fila.filavinculada.codvalor}|${fila.filavinculada.desvalor};"/>
								<td class="literalborde" width="20%">${fila.desconceptoppalmod}&nbsp;</td>
								<td class="literalborde" width="26%">${fila.desriesgocubierto}&nbsp;</td>
								<td class="literalborde" width="20%">${fila.nomconcepto}&nbsp;<br/>${fila.filavinculada.nomconcepto}</td>
								<td class="literalborde" width="20%">${fila.desvalor}&nbsp;</td>
							</tr>
							</table>
							</c:forEach>
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
			</fieldset>
		</c:forEach>
		</c:when>
		<c:otherwise>
			No hay m&oacute;dulos disponibles.
		</c:otherwise>
		</c:choose>
		</div>
	</form>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
</body>
</html>