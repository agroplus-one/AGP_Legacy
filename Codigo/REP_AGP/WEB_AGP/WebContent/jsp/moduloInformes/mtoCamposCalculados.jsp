<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
<head>
<title>Mantenimiento de campos calculados</title>

<%@ include file="/jsp/common/static/metas.jsp"%>

		
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
	
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
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/moduloPolizas/polizas/imprimir.js"></script>
	<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
	
	<script type="text/javascript" src="jsp/moduloInformes/camposcalculados.js"></script>
	<%@ include file="/jsp/js/draggable.jsp"%>

</head>

<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="javascript:SwitchSubMenu('sub12','sub11');onLoadCalculado()">
<%@ include file="/jsp/common/static/cabecera.jsp"%>
<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

<!-- botones de la página -->
<div id="buttons">
<table width="97%" cellspacing="0" cellpadding="0" border="0">
	<tr>
		<td width="5">&nbsp;</td>
		<td align="left">&nbsp;</td>
		<td align="right">
			<a class="bot" id="btnModificar" style="display:none" href="javascript:modificar()">Modificar</a>
			<a class="bot" id="btnAlta" href="javascript:alta()">Alta</a>
			<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
			<a class="bot" id="btnLimpiar" href="javascript:limpiar()">Limpiar</a>
		</td>
	</tr>
</table>
</div>

<!-- Contenido de la pagina -->
<div class="conten" style="padding: 3px; width: 97%">
<p class="titulopag" align="left">Mantenimiento de campos calculados</p>
	<form:form name="main" id="main" action="mtoCamposCalculados.run" method="post" commandName="camposCalculados">								
	<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
	<input type="hidden" name="method" id="method" />
	<input type="hidden" name="origenLlamada" id="origenLlamada" />
				
<div class="panel2 isrt">
 <fieldset>
				
<table width="100%" cellpadding="5" cellspacing="5">
	<tr>
		<td class="literal">Nombre</td>
		<td colspan="2">
			<form:input path="nombre" id="nombre" cssClass="dato" tabindex="1" size="70" maxlength="50" onchange="this.value=quitaAcentos(this.value);this.value=this.value.toUpperCase();" />
		</td>
	</tr>
	<tr>
		<td class="literal">Operando 1</td>
		<td>
			<form:select id="camposPermitidosByIdoperando1" path="camposPermitidosByIdoperando1.id" tabindex="2" cssClass="dato" cssStyle="width:320px">
				<form:option value="">Todos</form:option>
				<c:forEach items="${listaCampoOperando}" var="cpo">
					<form:option value="${cpo.id}">${cpo.vistaCampo.vista.nombre} - ${cpo.vistaCampo.nombre}</form:option>
				</c:forEach>
			</form:select>
		</td>
		<td class="literal">Operador</td>
		<td>
			<form:select id="idoperador" path="idoperador" tabindex="3" cssClass="dato" cssStyle="width:70px">
				<option value="">Todos</option>
				<form:option value="0">+</form:option>
				<form:option value="1">-</form:option>
				<form:option value="2">*</form:option>
				<form:option value="3">/</form:option>
			</form:select>
		</td>
		<td class="literal">Operando 2</td>
		<td>
			<form:select id="camposPermitidosByIdoperando2" path="camposPermitidosByIdoperando2.id" tabindex="4" cssClass="dato" cssStyle="width:320px">
				<form:option value="">Todos</form:option>
				<c:forEach items="${listaCampoOperando}" var="cpo">
					<form:option value="${cpo.id}">${cpo.vistaCampo.vista.nombre} - ${cpo.vistaCampo.nombre}</form:option>
				</c:forEach>
			</form:select>
		</td>
	</tr>
	<tr>
		
	</tr>
</table>
</div>

<form:hidden path="id" id="id" />	

</form:form> 

<!-- Grid Jmesa -->
<div id="grid">${consultaCamposCalculados}</div>


</div> 			
	
		<br />
				
	</body>
</html>