<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Carga de pólizas a través de la copy de Agroseguro</title>
		<%@ include file="/jsp/common/static/metas.jsp"%>


		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		
		<script type="text/javascript" src="jsp/js/util.js"></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/listadoParcelas.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
		<script type="text/javascript" src="jsp/js/calendar.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/displayTagAjaxSelectedRow.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
		
		<script type="text/javascript">
		function doCargar(){
			$("#main").submit();
		}
		</script>
		
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabecera.jsp"%>
		
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr align="left">
					<td align="right">
						<a class="bot" id="btnCargar" href="javascript:doCargar();">Cargar</a>
					</td>
				</tr>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%">
			<form:form name="main" id="main" action="cargaPolizaFromCopyController.html" method="post" commandName="polizaBean">
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<input type="hidden" name="method" id="method" value="doCargar"/>
				<table>
					<tr>
						<td class="literal">Referencia:</td>
						<td>
							<form:input path="referencia" id="referencia" cssClass="dato" size="9" maxlength="7" onchange="this.value=this.value.toUpperCase();" />
						</td>
					</tr>
					<tr>
						<td class="literal">Tipo póliza:</td>
						<td>
							<form:select id="tipoReferencia" path="tipoReferencia" cssClass="dato" cssStyle="width:100px">
								<form:option value=""></form:option>
								<form:option value="P">Principal</form:option>
								<form:option value="C">Complementaria</form:option>
							</form:select>
						</td>
					</tr>
					<tr>
						<td class="literal">Plan:</td>
						<td>
							<form:input path="linea.codplan" id="codplan" cssClass="dato" size="4" maxlength="4" />
						</td>
					</tr>
					<tr>
						<td class="literal">Clase:</td>
						<td>
							<form:input path="clase" id="clase" cssClass="dato" size="3" maxlength="3" />
						</td>
					</tr>
				</table>
			</form:form>
		</div>

		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
	</body>
</html>