<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Agroplus - Importaci&oacute;n</title>
		
        <%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/imprimir.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		
		<script type="text/javascript">
		
	       $(document).ready(function(){
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
			        inputField        : "fechaC",
			        button            : "btn_fechaCreacion",
			        ifFormat          : "%d/%m/%Y",
			        daFormat          : "%d/%m/%Y",
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
			        inputField        : "fechaG",
			        button            : "btn_fechaCarga",
			        ifFormat          : "%d/%m/%Y",
			        daFormat          : "%d/%m/%Y",
			        align             : "Br"			        	        
		      	});
		      	
		    });
		    
		    function bloqueaInputs(){
				var frm = document.getElementById('frmListadoCargas');
			}			
	    	
	    	function onInvokeAction(id) {
				var to=document.getElementById("adviceFilter");
				to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
			    $.jmesa.setExportToLimit(id, '');
			    var parameterString = $.jmesa.createParameterStringForLimit(id);
			    var frm = document.getElementById('frmListadoCargas');
			    $.get('${pageContext.request.contextPath}/cargasCondicionado.run?ajax=true&' + parameterString, function(data) {
			        $("#grid").html(data)
		  			});
			}
			function cargarFiltro(){
				
				<c:forEach items="${sessionScope.listadoCargasCondicionado_LIMIT.filterSet.filters}" var="filtro">
					
					
					if ('${filtro.property}' == 'fechaCreacion'){
						var inputText = document.getElementById('fechaCreacion');
						inputText.value = '${filtro.value}';
					}else if ('${filtro.property}' == 'estado'){
						var inputText = document.getElementById('estado');
						inputText.value = '${filtro.value}';
					}else if ('${filtro.property}' == 'fechaCarga'){
						var inputText = document.getElementById('fechaCarga');
						inputText.value = '${filtro.value}';
					}else{
						//var inputText = document.getElementById('${filtro.property}');
						//inputText.value = '${filtro.value}';
					}
				</c:forEach>
			}
			 function comprobarCampos(){
				jQuery.jmesa.removeAllFiltersFromLimit('listadoCargasCondicionado');
	         	var resultado = false;
				
	         	if ($('#fechaC').val() != ''){
	         		jQuery.jmesa.addFilterToLimit('listadoCargasCondicionado','fechaCreacion', $('#fechaC').val());
	       			resultado = true;
	         	}       	
	         	if ($('#estado').val() != ''){
	         		jQuery.jmesa.addFilterToLimit('listadoCargasCondicionado','estado', $('#estado').val());
	       			resultado = true;
	         	}
	         	if ($('#fechaG').val() != ''){
	         		jQuery.jmesa.addFilterToLimit('listadoCargasCondicionado','fechaCarga', $('#fechaG').val());
	         		resultado = true;
	         	} 

	         	return resultado;
	         }
			function consultarFiltro(){
			 	//$("#panelInformacion").hide();
				//$("#panelAlertasValidacion").hide();	
				//$("#panelAlertas").hide();
				
				//if (validarCamposConsulta()) {
				comprobarCampos();
				onInvokeAction('listadoCargasCondicionado','filter');
				//}					
		 	}
		 	
		 	function limpiar(){
			
				//$("#panelInformacion").hide();
				//$("#panelAlertasValidacion").hide();	
				//$("#panelAlertas").hide();
				
				$('#fechaC').val('');
				$('#estado').val('');
				$('#fechaG').val('');
				
				
				$('#frmListadoCargas').attr('target', '');
				
				jQuery.jmesa.removeAllFiltersFromLimit('listadoCargasCondicionado');
				onInvokeAction('listadoCargasCondicionado','clear');								
							
			 }
			 function alta(){
			 	var frm = document.getElementById("cargasFichero");
			 	frm.method.value="doConsulta";
			 	$('#cargasFichero').submit();
			 	
			 }
			 function borrarCarga(id){
			 	$('#frmListadoCargas').validate().cancelSubmit = true;
				if(confirm('¿Está seguro de que desea eliminar la carga de condicionado seleccionada?')){
		    		var frm = document.getElementById('frmListadoCargas');
	    			frm.idCondicionado.value=id;
	    			frm.method.value='doBorrarCondicionado';
	    			$.blockUI.defaults.message = '<h4> Eliminando registro seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			       	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		    		$('#frmListadoCargas').submit();
		    	}
			 }
			 function editarCarga(id){
			 	var frm = document.getElementById('frmListadoCargas');
    			frm.idCondicionado.value=id;
    			frm.method.value='doEditarCondicionado';
    			$('#frmListadoCargas').submit();
	    	}
	    	function cerrarCarga(id){
	    		var frm = document.getElementById('frmListadoCargas');
    			frm.idCondicionado.value=id;
    			frm.method.value='doCerrarCarga';
    			$('#frmListadoCargas').submit();
	    	
	    	}
	    	function consultar(id){
	    		var frm = document.getElementById('frmListadoCargas');
    			frm.idCondicionado.value=id;
    			frm.method.value='doConsultarCondicionado';
    			$('#frmListadoCargas').submit();
	    	}
			
	    </script>
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="bloqueaInputs();javascript:cargarFiltro();">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="../../common/static/menuLateralTaller.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		<!-- botones de la página -->
		<div id="buttons" >
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tbody>
					<tr>
						<td align="right">
						
							<a class="bot" id="btnAlta" href="javascript:alta()">Alta</a>
							<a class="bot" id="btnConsultar" href="javascript:consultarFiltro();">Consultar</a>
							<a class="bot" href="javascript:limpiar();">Limpiar</a>							
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%" >
			<p class="titulopag" align="left">Cargas de Condicionado</p>			
			<form name="cargasFichero" id="cargasFichero" action="cargasFicheros.run" method="post">
				<input type="hidden" name="method" id="method"/>
				<input type="hidden" name="origenLlamada" id="origenLlamada" value=""/>
			</form>
			<form:form name="frmListadoCargas" id="frmListadoCargas" action="cargasCondicionado.run" method="post" commandName="cargasCondicionadoBean">
				<input type="hidden" name="method" id="method" />	
				<input type="hidden" name="origenLlamada" id="origenLlamada" value=""/>	
				<input type="hidden" name="fechaC.day" value="">
				<input type="hidden" name="fechaC.month" value="">
				<input type="hidden" name="fechaC.year" value="">
				
				<input type="hidden" name="fechaG.day" value="">
				<input type="hidden" name="fechaG.month" value="">
				<input type="hidden" name="fechaG.year" value="">
				<input type="hidden" name="idCondicionado" id ="idCondicionado"/>
				
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div>
					<fieldset style="width:60%;margin:0 auto;">
					<legend class="literal">Filtro</legend>
							<table style="margin:0 auto">
								<tr align="left">
				
									<td class="literal">Fecha de Creación</td>
									<td class="literal">
										<spring:bind path="fechaCreacion">
					                    	 <input type="text" name="fechaC" id="fechaC" size="11" maxlength="10" class="dato" tabindex="1"
					                    	 		onchange="if (!ComprobarFecha(this, document.frmListadoCargas, 'Inicio de Creación')) this.value=''"
													value="<fmt:formatDate pattern="dd/MM/yyyy" value="${cargasCondicionadoBean.fechaCreacion}" />" />
					                    </spring:bind>
				 						<input type="button" id="btn_fechaCreacion" name="btn_fechaCreacion" class="miniCalendario" style="cursor: pointer;" /> 
			                         </td>
			                         
			                         <td class="literal">Estado</td>
										<td colspan="4">
											<select  class="dato" id="estado" name="estado" tabindex="2">
												<option value="">Todos</option>
												<option value="1">Cargado</option>
												<option value="2">Abierto</option>
												<option value="3">Cerrado</option>	
												<option value="4">Error</option>
											</select>
										</td>
			                         
			                         <td class="literal">Fecha de Carga</td>
			                         <td class="literal">
					                    <spring:bind path="fechaCarga">
					                    	 <input type="text" name="fechaG" id="fechaG" size="11" maxlength="10" class="dato" tabindex="3"
					                    	 		onchange="if (!ComprobarFecha(this, document.frmListadoCargas, 'Fecha de carga')) this.value=''"
													value="<fmt:formatDate pattern="dd/MM/yyyy" value="${cargasCondicionadoBean.fechaCarga}" />" />
					                    </spring:bind>
					                  
				 
				                     <input type="button" id="btn_fechaCarga" name="btn_fechaCarga" class="miniCalendario" style="cursor: pointer;" /> 
			                         </td>
								</tr>	
							</table>
					</fieldset>
				</div>	
			</form:form>
			<div id="grid" style="width: 70%;margin:0 auto;">
			  	${listadoCargasCondicionado}		  							               
			</div> 	
		
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