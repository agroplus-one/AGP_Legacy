<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
<head>
	<title>Agroplus - Importaci&oacute;n</title>
		
        <%@ include file="/jsp/common/static/metas.jsp"%>
		
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />

		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.filestyle.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		
		<%@ include file="/jsp/js/draggable.jsp"%>
		<script type="text/javascript">
		
	       $(document).ready(function(){
	       		<c:if test="${modoConsulta eq 'true'}">
	       			$("#btnAnadir").css("display", "none");
	       			$("#btnSalir").css("display", "");
	       			$("#btnSelecTodas").attr("disabled", "disabled");
					$("#btnVolver").css("display", "none");
					optionReadOnly();
	       		</c:if>
	       		
	       		$('#main1').validate({					
				
					errorLabelContainer: "#panelAlertasValidacion",
	   				wrapper: "li",
					 highlight: function(element, errorClass) {
					 	$("#campoObligatorio_" + element.id).show();
	  			     },
	  				 unhighlight: function(element, errorClass) {
						$("#campoObligatorio_" + element.id).hide();
	  				 },  	
					
					rules: {
						"tabla":{required: true}
				 	},
					messages: {
						"tabla":{required: "Debe seleccionar al menos una tabla"}
				 	}
				});
		    });
		    
		   function guardar(){
		   	 guardaTodasTablas();
		   	 var frm = document.getElementById('main1');
		   	 $('#method').val('doGuardar');	 
			 $('#main1').submit();
		   }
		   
		   function guardaTodasTablas(){
		   	var selObj = document.getElementById('tabla');
			var i;
			for (i=0; i<selObj.options.length; i++) {
				if (i==0){
			  		$("#otraListTablas"). val(selObj.options[i].value);
			  	}else{
			  		$("#otraListTablas"). val($("#otraListTablas"). val() + ";" + selObj.options[i].value);
			  	}
			 }
		   }
		   function salir(){
		    	$("#main1").validate().cancelSubmit = true;
		    	var frm = document.getElementById('main1');
		    	frm.method.value="doSalir";
		    	$('#main1').submit();
		   }
		  
		   //DAA 07/05/2013		  
		   function selecTodas(){
		   		$("#tabla option").each(function(){
    				$(this).attr('selected', true);
				});
		   }
		   function optionReadOnly(){
		   		$("#tabla option").each(function(){
		   			$(this).attr('disabled', 'disabled');
		   			if($(this).attr('selected').valueOf() == true){
    					$(this).css('background-color','#CCCCCC');
    				}	
				});
		   }
		   //vuelve a carga de ficheros en modo edicion
		   function volver(){
		    	$("#main1").validate().cancelSubmit = true;
		    	var frm = document.getElementById('main1');
		    	frm.volver.value="true";
		    	frm.method.value="doSalir";
		    	$('#main1').submit();
		   }
			
		</script>
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="../../common/static/menuLateralTaller.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		<!-- botones de la página -->
		<div id="buttons" >
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tbody>
					<tr>
						<td align="right">
						
							<a class="bot" id="btnAnadir" href="javascript:guardar();">Guardar</a>
							<a class="bot" id="btnSalir" href="javascript:salir();" style="display:none">Salir</a>
							<a class="bot" id="btnVolver" href="javascript:volver();">Volver</a>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%" >
			<p class="titulopag" align="left">Cargas de Tablas</p>			
			
			<form:form name="main1" id="main1" action="cargasTablas.html" method="post" commandName="cargasTablasBean">
				<input type="hidden" name="method" id="method" />	
				<input type="hidden" name="volver" id="volver" value="" />	
				<input type="hidden" name="idFichero" id ="idFichero" value="${idFichero}"/>
				<input type="hidden" name="listTablas" id ="listTablas" value="${listTablas}"/>
				<input type="hidden" name="otraListTablas" id ="otraListTablas" value="${otraListTablas}"/>
					
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div class="panel2 isrt" style="width:90%; margin:0 auto;">
					<fieldset >
						<legend class="literal">Tablas</legend>
						<div align="center"> 
							<select name="tabla" id="tabla" class="dato" multiple style="width: 350px;height: 150px">
								<c:forEach items="${listTablas}" var="tablas">  
									<option value="${tablas.numtabla}-${tablas.ficheroxml}" <c:if test="${tablas.alta eq 'S'}">selected </c:if>>
										${nombreFichero}-${tablas.ficheroxml}
									</option>
								</c:forEach>
							</select>	
						</div> 
					</fieldset>
					<div class="literal" align="center" style="margin:0 auto;display:table;text-align:center;"> 
						<a class="bot" id="btnSelecTodas" href="javascript:selecTodas();">Seleccionar Todas</a>
					</div>
				</div>
			</form:form>
			
			
		
		</div> 
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
	<!-- POPUPS AVISO  -->
	<!--               -->
	<!-- *** popUp detalle Errores *** -->
	<div id="divMensajeError" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;left: 30%;">
		
		<div id="header-popup" style="padding:0.4em 1em;position:relative;
		   				              color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;
						              background:#525583;height:15px">
			<div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
			 	Detalle del mensaje
			</div>
			<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
			          font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
				<span onclick="cerrarPopUp()">x</span>
			</a>
		</div>
		<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelInformacion" class="panelInformacion">
				<div id="mensajeError"></div>
		<!-- buttons -->			
				<div style="margin-top:15px;clear: both">
					<a class="bot" href="javascript:cerrarPopUp()">Aceptar</a>				
				</div>
			</div>
		</div>
	</div>
	

</body>
</html>