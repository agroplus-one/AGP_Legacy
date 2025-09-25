<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
<head>
	<title>Proceso de Cierre Mensual</title>
	
	<%@ include file="/jsp/common/static/metas.jsp"%>
	 
	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
	
	<!-- JavaScript,jQery & AJAX -->
	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
	<script type="text/javascript" src="jsp/js/util.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
	<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>	
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
	<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
	<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
	<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
	<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
		
	<script type="text/javascript" charset="ISO-8859-1">
			
			function cerrarPopUp(){
			     $('#errores_cierre_popup').fadeOut('normal');
			     $('#overlay').hide();
			 }
			 
			function validarCerrarPeriodo(){
				$('#div_bot').hide();
				$('#ok').hide();
				$('#errores').html('');
				$.ajax({
					url: 'cierre.html',
					data: 'method=doValidarCerrarPeriodo&fechaCierre=' + $('#fechaCierre').val(),
					dataType: 'json',
					success: function(datos){
						if(datos.ok){
							$('#div_bot').show();
							$('#ok').show();
							$('#fases').show();
							if (datos.todasfasesAceptadas != null){
								$('#fases').html(datos.todasfasesAceptadas);
							}
							
						}
						if(datos.errores.length > 0){
							var str = "";
							for(var i=0;i<datos.errores.length;i++){
								str +=datos.errores[i];
								$('#errores').html(str);
							}
						}
					},
					beforeSend: function(){
						$('#errores_cierre_popup').fadeIn('normal');
						$('#overlay').show();
			           	$('#gif').show();
            		},
            		complete: function(){
            			$('#gif').hide();        					
            		},
					type: 'post'
				});
			}
			
			function cierre(){
				
				$.ajax({
					url: 'cierre.html',
					data: 'method=doCerrarPeriodo&fechaCierre=' + $('#fechaCierre').val(),
					type: "POST",
					cache: false,
					dataType: "json",
					success: function(msg){
						if(msg.resul == 'OK') {
							$.unblockUI();
							$('#isPeriodoCerradoOK').val("true");
							$('#method').val('doConsulta');
							$('#main3').submit();
							
						}else if (msg.resul =='KO'){
							$.unblockUI();
							$('#panelAlertaPeriodo').html("Error al cerrar el periodo. Error al generar los informes");
							document.getElementById("panelAlertaPeriodo").style.display='';
							document.getElementById("btnCerrar").style.display=''; 
						
						}else if (msg.resul =='ERRORINFORMES'){
							$.unblockUI();
							$('#panelInformacionPeriodo').html("El periodo se ha cerrado correctamente");
							$('#panelAlertaPeriodo').html("Error al generar los informes");
							document.getElementById("panelInformacionPeriodo").style.display='block';
							document.getElementById("panelAlertaPeriodo").style.display='block';
							document.getElementById("btnCerrar").style.display='none';
						}else if (msg.resul =='KO_DATOS_CONSULTA_COMS.OK'){
							$.unblockUI();
							$('#panelInformacionPeriodo').html("El periodo se ha cerrado correctamente");
							$('#panelAlertaPeriodo').html("Error al generar los datos para la consulta de comisiones");
							document.getElementById("panelInformacionPeriodo").style.display='block';
							document.getElementById("panelAlertaPeriodo").style.display='block';
							document.getElementById("btnCerrar").style.display='none';
						}else if (msg.resul =='KO_DATOS_CONSULTA_COMS.ERRORINFORMES'){
							$.unblockUI();
							$('#panelInformacionPeriodo').html("El periodo se ha cerrado correctamente");
							$('#panelAlertaPeriodo').html("Error al generar los informes. Error al generar los datos para la consulta de comisiones");
							document.getElementById("panelInformacionPeriodo").style.display='block';
							document.getElementById("panelAlertaPeriodo").style.display='block';
							document.getElementById("btnCerrar").style.display='none';
						}
					},
					error: function(msg){
						$.unblockUI();
						$('#panelAlertaPeriodo').html("Error en la llamada");
						document.getElementById("panelAlertaPeriodo").style.display='';
						document.getElementById("btnCerrar").style.display='';
						
            		},
            		beforeSend: function(){
            			
            			$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
        				$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
        			    $('#errores_cierre_popup').fadeOut('normal');
        			    $('#overlay').hide(); 
            		}
				 
				}); 
			}
			
			function borrarCierre(idCierre){
				jConfirm('¿Está seguro de que desea eliminar el registro seleccionado?', 'Diálogo de Confirmación', function(r) {
					if (r){
						$('#idCierre').val(idCierre);
						$('#method').val('doBorrarCierrePorId');
						$('#main3').submit();	
					}
				});
			}
			
			function verDetalle(){
				$('#method').val('doDetalleFasesSinCierre');
				$('#main3').submit();			
			}
			
			function generarInforme(){
				$('#method').val('doGenerarInforme');
				$('#main3').submit();			
			}
			
			function abrirInformes(){
				$('#method').val('doAbrirInformes');
				$('#main3').submit();			
			}
			function abrirInformebyIdCierre(idCierre){
				$('#idCierre').val(idCierre);
				$('#method').val('doAbrirInformebyIdCierre');
				$('#main3').submit();		
			}
			function onInvokeAction(id) {
				var to=document.getElementById("adviceFilter");
				to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
			    $.jmesa.setExportToLimit(id, '');
			    var parameterString = $.jmesa.createParameterStringForLimit(id);
			    var frm = document.getElementById('main3');
			    $.get('${pageContext.request.contextPath}/cierre.html?ajax=true&idFichero='+$("#idFichero").val() +'&' + parameterString, function(data) {
			        $("#grid").html(data)
		  			});
			}
			function detalle(idCierre){
				var posicion_x; 
				var posicion_y; 
				posicion_x=(screen.width/2)-(800/2); 
				posicion_y=(screen.height/2)-(300/2); 
		    	var opciones="toolbar=no, location=no, directories=no, status=no,menubar=no, scrollbars=no, resizable=yes, width=800, height=300, left="+posicion_x+",top="+posicion_y+"";
		    	var url = 'fasesCierreComisiones.run?method=doDetalle&idCierre='+idCierre+'&origenLlamada=detalleCierre';
				window.open(url,"",opciones);
		    }
			
	</script>
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchSubMenu('sub7', 'sub5');">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Proceso de Cierre Mensual</p>
	
		<form:form action="cierre.html" method="post" name="main3" id="main3" commandName="cierreBean">
			<input type="hidden" name="method" id="method" />
			<input type="hidden" name="idCierre" id="idCierre" value ="${idCierre}"/>
			<input type="hidden" name="isPeriodoCerradoOK" id="isPeriodoCerradoOK"/>
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			
			<div id="panelInformacionPeriodo" class="literal" style="width:500px;height:20px;color:black;border:1px solid #FFCD00;display:none;
					font-size:12px;font-style:italic;font-weight:bold;line-height:20px;background-color:#FCF6CF">
				
			</div>
			<div id="panelAlertaPeriodo" class="literal" style="width:500px;height:20px;color:black;border:1px solid #DD3C10;display:none;
			font-size:12px;font-style:italic;font-weight:bold;line-height:20px;background-color:#FFEBE8">
				
			</div>
			
			<br/>
			<div style="panel2 isrt">
				<fieldset style="width:95%;margin:0 auto;">
					<table style="margin:0 auto;">
						<tr>
							<td width="100px" class="literal">Fecha de Cierre</td>
							<td width="100px" class="detalI">
								<spring:bind path="fechacierre">	
									<input type="hidden" name="fechacierre" id="fechaCierre" 
										value="<fmt:formatDate pattern="dd/MM/yyyy" value="${cierreBean.fechacierre }"/>"/>
								</spring:bind>
								<fmt:formatDate pattern="dd/MM/yyyy" value="${cierreBean.fechacierre }"/>
							</td>
							<td>
								<c:if test="${periodoCerrado != true}">
									<a class="bot" id="btnCerrar"  href="javascript:validarCerrarPeriodo();">Cerrar Periodo</a>			
								</c:if>
								
								<a class="bot" id="btnInformes"  href="javascript:generarInforme();">Volver a Generar Informes</a>	
								
								<a id="btnAbrir" href="javascript:abrirInformes();"><img src="jsp/img/folderopen.gif" alt="Abrir Informes" title="Abrir Informes" /></a>	
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
			
			<div style="panel2 isrt">
				<fieldset style="width:95%;margin:0 auto;">
					<legend class="literal">Detalle del Estado actual</legend>
					<table style="margin:0 auto;">
						<tr>
								<td class="literal">Fases Cargadas sin Cierre</td>
								<td width="50px" class="detalI">
									${fasesSinCierre }
								</td>
								
								<td>
								<c:if test="${fasesSinCierre > 0}">
									<a class="bot" id="btnAlta"  href="javascript:verDetalle()">Ver Detalle</a>			
								</c:if>
									
								</td>
						</tr>
					</table>
				</fieldset>
			</div>
			
		</form:form>
		<div id="grid" style="width: 90%;margin:0 auto;">
			  	${listadoCierres}		  							               
			</div> 	
	</div>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
		<!--  popup modificarPorcentajes -->
		<div id="errores_cierre_popup" class="wrapper_popup">
		    <div class="header-popup">
		        <div class="title_popup">Aviso</div>
		        <a class="close_botton_popup"><span onclick="cerrarPopUp()">x</span></a>
		    </div>
			<div class="content_popup">
				<img src="jsp/img/loading.gif" id="gif" style="display:none"/>
			    <div id="errores" class="literal" style="color:red">
			    </div>
			    <div id="ok" class="literal" style="display:none">
			    	Hay fases Aceptadas y listas para cerrar.<br>
			    	Pulse Continuar para cerrarlas.<br>
			    	<div id="fases" class="literal" style="color:red;display:none;">
			    	</div>
			    </div>
			    
				<div style="margin-top:15px" id="div_bot" style="display:none">
				    <a class="bot" href="javascript:cerrarPopUp();">Cancelar</a>
				    <a class="bot" href="javascript:cierre();">Continuar </a>
				</div>
			</div>
		</div>
</body>
</html>