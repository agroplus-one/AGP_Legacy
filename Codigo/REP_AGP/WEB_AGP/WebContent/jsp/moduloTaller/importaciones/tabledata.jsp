<%@ include file="/jsp/common/static/taglibs.jsp"%> 


<%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", 0);
%>


<html>
	<head>
		<title>Agroplus - Detalle de las tablas importadas</title>
		
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
		<script type="text/javascript">
            function onInvokeAction(id) {
                $.jmesa.setExportToLimit(id, '');
                $.jmesa.createHiddenInputFieldsForLimitAndSubmit(id);
            }
            
            function volver() {
            <c:if test="${comeFrom == 'historico'}">
            	document.forms.tableDataForm.action = "importacion.html";
            </c:if>
            <c:if test="${comeFrom == 'activacion'}">
            	document.forms.tableDataForm.action = "activacionlineas.html";
            </c:if>
				document.forms.tableDataForm.submit();
            }
        </script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuLateralTaller.jsp"%>
<div id="conten" style="width:90%;">
	<!-- Botonera de debajo de la cabecera -->
	<div id="FR_bot">
		<table width="100%" height="20" cellspacing="0" cellpadding="0" border="0">
			<tbody>
				<tr height="1">
					<td bgcolor="#666666" height="1">
						
					</td>
				</tr>
				<tr bgcolor="#e5e5e5">
					<td align="right">
						<table border="0">
							<tbody>
								<tr>
									<td width="40">
									</td>
									<td>
										<a id="botonVolver"  href="menu.html?OP=ppal">
											 <img width="16" name="salir" alt="Volver" src="jsp/bot/cerrar_a.gif"/>
										</a>
									</td>									
									<td>
										<a href="">
											 <img width="16" name="ayuda" alt="Ayuda" src="jsp/bot/ayuda_a.gif" />
										</a>
									</td>
									<td width="10">
									</td>
								</tr>
							</tbody>
						</table>
					</td>
				</tr>
				<tr height="1">
					<td bgcolor="#666666" height="1">
						
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	<!-- Cabecera de las pantallas -->
		<table style="border:1px solid black" width="100%">
			<tr>
				<td class="literal">Entidad: ${sessionScope.usuario.oficina.entidad.codentidad } ${sessionScope.usuario.oficina.entidad.nomentidad }</td>
				<td class="literal">Usuario: ${sessionScope.usuario.nombreusu }</td>
			</tr>			
		</table>
		<br/>
	<!-- Cuerpo de la ventana -->
	
	<div id="buttons">
	<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">
					<table cellspacing="2" cellpadding="0" border="0">
						<tbody>
							<tr>
								<td>
									<!-- BOTÓN PARA VOLVER AL DETALLE DEL HISTÓRICO -->
									<a class="bot" href="#" onclick="javascript:volver();">Volver</a>
								</td>
							</tr>
						</tbody>
					</table>
					<!-- FIN TABLA BARRA DE BOTONES-->
				</td>
			</tr>
	</table>
	</div>
	<div class="conten" style="padding:3px;">
		<p class="titulopag" align="left">Detalle de tablas importadas</p><br/>
		<div class="centraTabla">
			<form name="tableDataForm" action="tabledata.run">
				<input type="hidden" name="lineaSeguroId" id="lineaSeguroId" value="${lineaSeguroId }"/>
				<input type="hidden" name="codPlan" id="codPlan" value="${codPlan }"/>
				<input type="hidden" name="codLinea" id="codLinea" value="${codLinea }"/>
				<input type="hidden" name="tabla" value="${tabla}">
				<input type="hidden" name="idhistorico" value="${idhistorico}">
				<input type="hidden" name="seleccionado" value="${idhistorico}">
				<input type="hidden" name="ROW" value="${ROW }"/>
				<input type="hidden" name="comeFrom" value="${comeFrom }"/>
				<input type="hidden" name="operacion" value="detalleTablas">
				${tableData}
			</form>
		</div>
	</div>

	</div>
	</body>
</html>