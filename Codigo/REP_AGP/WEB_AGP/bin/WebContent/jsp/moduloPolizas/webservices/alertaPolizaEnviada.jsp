<%@ page import="es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza,
            es.agroseguro.acuseRecibo.AcuseRecibo,
            es.agroseguro.acuseRecibo.Documento,
            es.agroseguro.acuseRecibo.Error,
            com.rsi.agp.core.ws.ResultadoWS,
            com.rsi.agp.core.util.WSUtils,
            com.rsi.agp.dao.tables.poliza.ComparativaPolizaId,
            java.util.* 
    		"%>
<%@ include file="/jsp/common/static/taglibs.jsp" %>
<%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", 0);
%>


<html>
<head>
<title>Póliza ya enviada</title>

        <%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />

		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		
		<script type="text/javascript">
			
			function volverCicloPoliza() {
				document.getElementById('consulta').submit();
			}

			function volverUtilidades () {
				document.getElementById ('volver').submit();
			}


		</script>
		
		
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub3');">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabecera.jsp"%>

		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="right">
						
						<!-- La llamada del 'Volver' depende de si venimos del listado de utilidades o del ciclo de póliza -->
						<c:if test="${cicloPoliza == 'cicloPoliza'}">
							<a class="bot" href="#" onClick="javascript:volverCicloPoliza();">Volver</a>
						</c:if>
						<c:if test="${cicloPoliza eq null or cicloPoliza !='cicloPoliza'}">
							<a class="bot" href="#" onClick="javascript:volverUtilidades();">Volver</a>
						</c:if>
					
					</td>
				</tr>
			</table>
		</div>
		<!-- Contenido de la página -->

		<div class="conten" style="padding:3px;width:97%">
				<p class="titulopag" align="left">Póliza ya enviada</p>
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				</br>		
		</div>	

		<!-- Formulario para el paso a definitiva simple -->
		<form name="pasarADefinitiva" id="pasarADefinitiva" action="pasoADefinitiva.html" method="post" commandName="polizaDefinitiva">
			<input type="hidden" name="method" id="method" value="doPasarADefinitiva"/>
			<input type="hidden" name="idpoliza" id="idpoliza" value="${idpoliza}"/>
			<input type="hidden" name="resultadoValidacion" id="resultadoValidacion"/>
			<input type="hidden" name="grabFueraContratacion" id="grabFueraContratacion"/>
			<input type="hidden" name="actualizarSbp" id="actualizarSbp"/>				
			<input type="hidden" name="esCpl" id="esCpl" value="false"/>
			<input type="hidden" name="cicloPoliza" id="cicloPoliza" value="${cicloPoliza}"/>								
		</form>	

		<!-- Formulario para la vuelta al listado de utilidades -->
		<form name="volver" id="volver" action="utilidadesPoliza.html" method="post">
			<input type="hidden" name="operacion" id="operacion" value="volver" />
		</form>
		
		<!-- Formulario para la vuelta al cicloPoliza -->
		<form name="consulta" id="consulta" action="seleccionPoliza.html" method="post">
			<input type="hidden" name="operacion" value="listParcelas" />
			<input type="hidden" name="idpoliza" id="idpoliza" value="${idpoliza}"/>
		</form>
		
		
</body>
</html>