<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
	<title>Datos complementarios para Reducciones de Capital</title>
	
	<%@ include file="/jsp/common/static/metas.jsp"%>
	
	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
	
	<!-- JavaScript,jQery & AJAX -->
	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
	<script type="text/javascript" src="jsp/js/util.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
	<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
	<script type="text/javascript" src="jsp/js/calendar.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>	
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	
	<script language="javascript">	
	
		$(document).ready(function() {
		
			 var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
		     document.getElementById("main3").action = URL;  	
			
			<c:if test="${modoLectura}">			
				$("input[type=text]").each(function(){	
						$(this).attr('readonly',"readonly");
				});	
				$('#tx_descripcion').attr('readonly',"readonly");		
				$('#sl_motivo').attr('disabled',true);				
				$("#btn_fechadanios").hide();
			</c:if>
			
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
		        inputField        : "tx_fechadanios",
		        button            : "btn_fechadanios",
		        ifFormat          : "%d/%m/%Y",
		        daFormat          : "%d/%m/%Y",
		        align             : "Br"			        	        
	      	});		      	
	      	
	      	$('#main3').validate({
	      		errorLabelContainer: "#panelAlertasValidacion",
  				wrapper: "li",
				onfocusout: function(element) {
					if ( ($('#method').val() == "doGuarda") && !this.checkable(element) && (element.name in this.submitted || !this.optional(element)) ) {
						this.element(element);
					}
				},
  				highlight: function(element, errorClass) {
				 	$("#campoObligatorio_"+element.id).show();
 				},
 				unhighlight: function(element, errorClass) {
				 	$("#campoObligatorio_"+element.id).hide();
 				},
 				
			 	rules: {
			 					 	
			 		"fechadanios" : {required: true, dateITA: true},			 		
			 		"codmotivoriesgo" : {alMenosUno: true}
			 	},
			 	messages: {
			 		
			 		"fechadanios" : {required: "El campo Fec. Daños es obligatorio", dateITA: "Formato de fecha incorrecto"},
			 		"codmotivoriesgo" : {alMenosUno: "El campo Motivo es obligatorio"}
			 	}
	      	});	
	      	
	    });
	    
	    jQuery.validator.addMethod("alMenosUno",function(value, element) {
				var codmotivo = $('#sl_motivo').val();
				var descripcion = $('#tx_motivo').val();
				if( codmotivo == ""){
					return false
				} else {
					return true
				}
			}, 
			"Se debe informar el Motivo y/o la Descripción"
		);
		
		function continuar(){
			$("#method").val("doGuarda");
			if($("#main3").valid()){
				$.blockUI.defaults.message = '<h4> Grabando los datos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
       			$("#main3").submit();
			}
		} 
		
		// MPM - 04/09/2012
		function continuarSoloLectura(){
			// Copia el idPoliza y el idReduccion del formulario principal al de listado de parcelas
			formPrin = document.getElementById ("main3");
			formSL = document.getElementById ("listadoParcelas");
			
			formSL.idPoliza.value = formPrin.lb_poliza.value;
			formSL.idReduccionCapital.value = formPrin.id.value;
						 
			$("#panelInformacion").hide();
			$("#listadoParcelas").submit();
		}
		
		function limpiar(){
				$("#panelErrores").hide();
				$("#panelAlertasValidacion").hide();
		
				$("#sl_motivo").selectOptions("");			
				$("#tx_fechadanios").val('');	
				$("#tx_motivo").val('');					
		}
		
		function volver(){
			$('#tx_fechadanios').attr('disabled',true);		
			$('#sl_motivo').attr('disabled',true);
			$('#tx_motivo').attr('disabled',true);			
			$("#method").val("doVolver");
			$("#main3").submit();
		}
		
		//funcion que valida un elemento independientemente, si no jquery no lo hace
		function validarElemento(elemento){
			$("#main3").validate().element(elemento);  
		}	
		
	</script>

</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4');"> 
	<%@ include file="../../common/static/cabecera.jsp"%>
	<%@ include file="../../common/static/menuGeneral.jsp"%>
	<%@ include file="../../common/static/datosCabeceraTaller.jsp" %>
	
	<!-- Botones para alta y volver -->
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">					
					
					<c:if test="${modoLectura != true}">
						<a class="bot" id="botonContinuar" href="javascript:continuar();">Continuar</a>
						<a class="bot" id="botonLimpiar" href="javascript:limpiar();">Limpiar</a>
					</c:if>
					
					<c:if test="${modoLectura == true}">			
						<a class="bot" id="botonAlta" href="javascript:continuarSoloLectura();">Continuar</a>		
					</c:if>	
										
					<a class="bot" href="javascript:volver()">Volver</a>					
				</td>
			</tr>
		</table>
	</div>		

	<!-- Contenido de la página -->
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Datos complementarios para Reducciones de Capital</p>
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>		
			
			<!-- Datos de la póliza -->
		<fieldset style="width:95%;margin:0 auto;">
		<legend class="literal">Datos de la p&oacute;liza</legend>
			<table width="90%" align="center" cellspacing="1">
				<tr>
					<td class="literal" width="40px">Plan:</td>
					<td width="80px" class="detalI">${poliza.linea.codplan}</td>
					<td class="literal" width="50px">Línea:</td>
					<td width="500px" class="detalI">${poliza.linea.codlinea } - ${poliza.linea.nomlinea }</td>
  					<td class="literal" width="60px">Asegurado:</td>
 					<td width="300px" class="detalI" colspan="3">${poliza.asegurado.nombreCompleto}</td> 
				</tr>
				<tr>
					<td class="literal" width="40px">Póliza:</td>
					<td width="80px" class="detalI">${poliza.referencia}</td>
					<td class="literal" width="50px">Módulo:</td>
					<td width="500px" class="detalI">${poliza.codmodulo}</td>		
					<td class="literal" width="70px" align="left">Fec. Envío:</td>
					<td width="300px" align="left" class="detalI"><fmt:formatDate pattern="dd/MM/yyyy" value="${poliza.fechaenvio}"/></td>
					<td class="literal" width="50px">Nº R.C.:</td>
					<td width="50px" style="position:relative; left:-20px" class="detalI">${numAnexo}</td>					
				</tr>
			</table>
		</fieldset>
		<br/><br/>
		
		<form name="listadoParcelas" id="listadoParcelas" action="parcelasReduccionCapital.html" method="post">			
			<input type="hidden" name="idPoliza" value="">
			<input type="hidden" name="idReduccionCapital" value="">
			<input type="hidden" name="modoLectura" value="${modoLectura}">
			<input type="hidden" name="vieneDeListadoRedCap" id="vieneDeListadoRedCap" value="${vieneDeListadoRedCap}">
		</form>
		
		
		<form:form name="main3" id="main3" action="declaracionesReduccionCapital.html" method="post" commandName="reduccionCapitalBean">
			<input type="hidden" id="method" name="method" />
			<form:hidden path="id" id="id"/>
			<form:hidden path="estado.idestado" id="estado"/>
			<form:hidden path="poliza.idpoliza" id="lb_poliza" />
			<form:hidden path="poliza.linea.codlinea" id="codlinea"/>
			<form:hidden path="poliza.linea.nomlinea" id="nomlinea"/>
			<form:hidden path="codModulo" id="codModulo"/>
			
			<input type="hidden" name="vieneDeListadoRedCap" id="vieneDeListadoRedCap" value="${vieneDeListadoRedCap}"/>
			
			<!-- Datos complementarios -->
			<div>
				<fieldset style="width:95%;margin:0 auto;">
					<table width="100%" align="center" cellspacing="2">
						<tr>
							<td class="literal">Fec. Daños:</td>
							<td class="literal">						
								<spring:bind path="fechadanios">								
									<input type="text" name="fechadanios"  id="tx_fechadanios" size="11" maxlength="10" class="dato"  
												value="<fmt:formatDate pattern="dd/MM/yyyy" value="${reduccionCapitalBean.fechadanios}"/>"/>
								</spring:bind>
								<input type="button" id="btn_fechadanios" name="btn_fechadanios" class="miniCalendario" 
													style="cursor: pointer;position:relative; top:-2px;"/>
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_tx_fechadanios"> *</label>
							</td>
							
							<td class="literal">Motivo:</td>					
							<td class="literal">
								<%-- <form:input path="codmotivoriesgo" id="jejecodmotivoriesgo"/> --%>
								<form:select id="sl_motivo" path="codmotivoriesgo" cssClass="dato" cssStyle="width:180px" >
									<form:option value="">-- Seleccione una opción --</form:option>
									<c:forEach items="${listaRiesgos}" var="riesgo">
										<%-- <fmt:parseNumber var="aux" value="${riesgo[0]}"/>
										<form:option value="${aux }">${riesgo[0]} - ${riesgo[1] }</form:option> --%>
										
										<form:option value="${riesgo[0] }">${riesgo[0]} - ${riesgo[1] }</form:option>
									</c:forEach>
								</form:select>
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_sl_motivo"> *</label>
							</td>
						</tr>
						<tr>						
							<td class="literal">Descripción:</td>
							<td class="literal">
								<form:input id="tx_motivo" path="motivo" cssClass="dato" size="45" maxlength="20"   
									onchange="this.value=this.value.toUpperCase();"/>
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_tx_motivo" > *</label>
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
		</form:form>
	</div>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
</body>
</html>