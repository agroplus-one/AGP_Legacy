<%@ include file="/jsp/common/static/taglibs.jsp"%>
<fmt:bundle basename="displaytag" >
	<c:set var="numReg"><fmt:message key="numElementsPag"/></c:set>
</fmt:bundle>
<%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", 0);
%>
<html>
	<head>
	<c:choose>
			<c:when test="${datosVarCopy eq true}">
				<title>Ver datos variables sit. act. agroseguro</title>						
			</c:when>
			<c:otherwise>
				<title>Ver datos variables CSV</title>
			</c:otherwise>
		</c:choose>
		
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
		<script type="text/javascript" src="jsp/js/calendar.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/lupaOrigenDatosPACPopUp.js"></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
			
	<script language="javascript">
		$(document).ready(function(){
			var URL = UTIL.antiCacheRand($("#main3").attr("action"));
			$("#main3").attr("action", URL);
		});
		
		//DAA 23/07/2012  cargarWrapper y continuar
		function cargarWrapper(){
			$('#overlay').show();
			//$("#popupRecalcular").show();
			var recalcular = $('#recalcular').val();
			continuar(recalcular);
		}
				
		function continuar(recalcular) {
			$("#popupRecalcular").hide();
			$('#overlay').hide();
			$("#recalcular").val(recalcular);
			$.blockUI.defaults.message = '<h4> Realizando cálculos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
       		
       		if(${datosVarCopy} == true){
       			$("#method").val('doGrabarDatosVariablesCopy');
       		}else{
       			$("#method").val('doGrabarDatosVariablesCsv');
       		}
       		
			$("#main3").submit();
            
		}
		//DAA 09/09/2013 validacion dinamica
		
		<c:set var="tipoFecha" value="3"/>
		<c:set var="tipoLupa" value="6"/>
		
		
		$(document).ready(function() {

			$('#main3').validate({
				
				errorLabelContainer: "#panelAlertasValidacion",
   				wrapper: "li",
   				rules: {
   					<c:forEach items="${lstDatosVar}" var="datVar" varStatus="lst">
   						<c:choose>
	   						<c:when test="${(datVar.tipoCampo.idtipo eq tipoFecha) }">
	   							"codConcepto_${datVar.id.codconcepto}":{dateITA: true,required:false}
	   						</c:when>
	   						<c:otherwise>
	   							"codConcepto_${datVar.id.codconcepto}":{required:false}
	   						</c:otherwise>
	   					</c:choose>
	   					<c:if test="${ lst.count < fn:length(lstDatosVar)}">,</c:if>
   					</c:forEach>	
				 },
				 messages: {
					 <c:forEach items="${lstDatosVar}" var="datVar" varStatus="lst">
						<c:choose>
	   						<c:when test="${(datVar.tipoCampo.idtipo eq tipoFecha) }">
	   							"codConcepto_${datVar.id.codconcepto}":{dateITA: "El formato del campo ${datVar.etiqueta} es dd/mm/YYYY",required:"El campo ${datVar.etiqueta} es obligatorio"}
	   						</c:when>
	   						<c:otherwise>
	   							"codConcepto_${datVar.id.codconcepto}":{required:" "}
	   						</c:otherwise>
	   					</c:choose>
	   					<c:if test="${ lst.count < fn:length(lstDatosVar)}">,</c:if>
					</c:forEach>	
				 }
			});
		});
	</script>

	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub3');">
	
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabecera.jsp"%>

		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr align="left">
					<td align="right">
						<a class="bot" id="btnCargar" href="javascript:cargarWrapper()">Continuar</a>
					</td>
				</tr>
			</table>
		</div>
		
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:100%">
		<c:choose>
			<c:when test="${datosVarCopy eq true}">
				<p class="titulopag" align="left">VISUALIZACIÓN DE LOS DATOS VARIABLES SIT. ACT. AGROSEGURO</p>						
			</c:when>
			<c:otherwise>
				<p class="titulopag" align="left">VISUALIZACIÓN DE LOS DATOS VARIABLES CSV</p>
			</c:otherwise>
		</c:choose>
			
			<form:form name="main3" id="main3" action="cargaParcelasController.html" method="post" commandName="polizaBean">
				
				<input type="hidden" id="recalcular" name="recalcular" value="${recalcular}"/>
				<input type="hidden" name="method" id="method" value="" />
				<input type="hidden" id="datosVarCopy" name="datosVarCopy" value="${datosVarCopy}"/>
				<input type="hidden" id="alertaOrigen" name="alertaOrigen" value="${alertaOrigen}"/>
				<input type="hidden" id="mensajeOrigen" name="mensajeOrigen" value="${mensajeOrigen}"/>
				
				<input type="hidden" name="listaIdAseguradoCsv" id="listaIdAseguradoCsv" value="${listaIdAseguradoCsv}" />
				
				<form:hidden path="idpoliza" id="idpoliza" />
				<form:hidden path="linea.codplan" id="codplan" />
				<form:hidden path="linea.codlinea" id="codlinea" />
				<form:hidden path="linea.lineaseguroid" id="lineaseguroid" />
				
				<form:hidden path="asegurado.id" id="idasegurado" />
				<form:hidden path="asegurado.nifcif" id="nifasegurado" />
				<form:hidden path="asegurado.entidad.codentidad" id="codentidad" />
				
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div class="isrt" align="center" style="vertical-align: middle;width: 97%" >
					<fieldset class="fieldset_alone" style="width: 100%; height: 100px" >
					   <legend  class="literal">Datos variables</legend>
					   <div class="">
						<table width="100%" cellpadding="2" cellspacing="2">
							<tr>
							
							<!--  FOR  DATOS VARIABLES -->
							<c:forEach items="${lstDatosVar}" var="datVar" varStatus="lst">
								<td class="literal" valign="middle" width="8%">
								    ${datVar.etiqueta }
								</td>
									<c:choose>
		     								<c:when test="${datVar.tipoCampo.idtipo eq tipoFecha}">
		     								 	    <fmt:parseDate value="${datVar.valorcargapac}" var="nuevaFecha" pattern="dd/MM/yyyy"/>
		     								 	    <td class="literal" nowrap="nowrap" valign="middle">
		     								 	        <input type="text" name="codConcepto_${datVar.id.codconcepto}" id="codConcepto_${datVar.id.codconcepto}" size="10" maxlength="10" class="dato" 
		     								 				value="<fmt:formatDate pattern="dd/MM/yyyy" value="${nuevaFecha}" />" />
												        <input type="button" id="btn_fecha_${datVar.id.codconcepto}" name="btn_fecha_${datVar.id.codconcepto}" class="miniCalendario" style="cursor: pointer;" />
												        <script type="text/javascript">
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
														        inputField        : "codConcepto_${datVar.id.codconcepto}",
														        button            : "btn_fecha_${datVar.id.codconcepto}",
														        ifFormat          : "%d/%m/%Y",
														        daFormat          : "%d/%m/%Y",
														        align             : "Br"			        	        
													      });
												        </script>
		     								</c:when>
		     								
		     								<c:when test="${datVar.tipoCampo.idtipo eq tipoLupa}">
		     								 	   <td class="literal" nowrap="nowrap" valign="middle">
		     								 	       <input id="${datVar.id.codconcepto}" name="codConcepto_${datVar.id.codconcepto}" class="dato" type="text" value="${datVar.valorcargapac}" size="3" maxlength="3"/>
		     								 	       <input id="${datVar.id.codconcepto}_des" class="dato" size="25" style="" readonly="true" value=""/>
												       <img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="lupaDatoVariableCargaPac('${datVar.id.codconcepto}','${datVar.etiqueta}');" alt="" /> 
		     								</c:when>
	
										    <c:otherwise>
										     	  <td class="literal" nowrap="nowrap" valign="middle">
										     	      <input id="${datVar.id.codconcepto}" name="codConcepto_${datVar.id.codconcepto}" class="dato" type="text" value="${datVar.valorcargapac}" size="10" maxlength="3"/>
										     </c:otherwise>
										     
					   				 </c:choose>
					   				 <c:if test="${lst.count % 3 == 0}">
					   				 	</tr><tr>
					   				 </c:if>
							</c:forEach>

							</tr>
						</table>
						</div>
					</fieldset>
				</div>	
			</form:form>
			<br/>
		</div>
		<br/>
		<!--  POPUPS -->
		<%@ include file="/jsp/moduloPolizas/polizas/popupPacOrigenDatosVarLupa.jsp"%>
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
	    <%@ include file="/jsp/moduloPolizas/polizas/popupRecalcular.jsp"%>
	</body>
</html>