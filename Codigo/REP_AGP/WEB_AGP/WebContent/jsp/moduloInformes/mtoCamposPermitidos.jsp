<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Mantenimiento de campos permitidos</title>
		
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
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
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
		<script type="text/javascript" src="jsp/moduloInformes/mtoCamposPermitidos.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>				
	
	<script type="text/javascript">
	function cargarFiltro(){
		<c:forEach items="${sessionScope.mtoCamposPermitidos_LIMIT.filterSet.filters}" var="filtro">
			
				if ('${filtro.property}' == 'descripcion'){
					var inputText = document.getElementById('descripcion');
					inputText.value = '${filtro.value}';
				}else if ('${filtro.property}' == 'vistaCampo.nombre'){
					var inputText = document.getElementById('campo');
					inputText.value = '${filtro.value}';
				}else if ('${filtro.property}' == 'vistaCampo.vistaCampoTipo.idtipo'){
					var inputText = document.getElementById('tipo');
					inputText.value = '${filtro.value}';
				}
				
		</c:forEach>
		
		if ($('#campo').val() != '' && $('#tipo').val() != ''){
			obj = document.getElementById('tipo');
			obj.disabled = true;
		}
		
	}
	
	</script>				
	</head>
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="javascript:SwitchSubMenu('sub12','sub11');cargarFiltro();">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		
		<!-- botones de la página -->
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td width="5">&nbsp;</td>
					<td align="left">
						&nbsp;
					</td>
					<td align="right">
						<a class="bot" id="btnModificar" style="display:none" href="javascript:modificar()">Modificar</a>
						<a class="bot" id="btnAlta" href="javascript:alta()">Alta</a> 
						<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a> 
						<a class="bot" id="btnLimpiar"href="javascript:limpiar()">Limpiar</a>
					</td>
				</tr>
			</table>
		</div>
		
		<!-- Contenido de la pï¿½gina -->
		<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Mantenimiento de campos permitidos</p>
		
		<form:form id="main" action="mtoCamposPermitidos.run" method="post" commandName="campoPermitidoBean">					
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			
			<input type="hidden" name="idCampoPermitido" id="idCampoPermitido" value="${idCampoPermitido}"/>
			<input type="hidden" name="method" id="method"/>
			<input type="hidden" name="idVista" id="idVista" value="${idVista}"/>
			<input type="hidden" name="idVistaCampo" id="idVistaCampo" value="${idVistaCampo}"/>
			<input type="hidden" name="origenLlamada" id="origenLlamada" value="${origenLlamada}"/>
			<input type="hidden" name="rowStart" id="rowStart" value="${rowStart}"/>
			<input type="hidden" name="rowEnd" id="rowEnd" value="${rowEnd}"/>
			<input type="hidden" name="tOrigen" id="tOrigen"/>
			<input type="hidden" name="tipoNumerico" id="tipoNumerico" value="${tipoNumerico}"/>
			<input type="hidden" name="tipoFecha" id="tipoFecha" value="${tipoFecha}"/>
			<input type="hidden" name="tipoTexto" id="tipoTexto" value="${tipoTexto}"/>
			
			
			<div class="panel2 isrt">
			<table width="100%">		
				<tr>
					<td class="literal">Tabla origen</td>
					<td>
						<form:select id="tablaOrigen" path="vistaCampo.tablaOrigen" cssStyle="width:255px" cssClass="dato" tabindex="3" onchange="javascript:vaciaCampo();">
						  <option value="">Todas</option>
						  <c:forEach items="${lstVistas}" var="vista" varStatus="lst">
						  	<form:option value ="${vista.id}">${vista.nombre}</form:option>
						  </c:forEach>
						</form:select>
					</td>
					<td class="literal">Campo</td>
					<td>
						<form:input id="campo" path="vistaCampo.nombre" cssClass="dato" size="28" maxlength="28" readonly="true" onchange="javascript:actualizaTipo();"/>
						<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('VistaCampo','principio', '', '');" alt="Buscar Campo" title="Buscar Campo" />
					</td>
				</tr>
				<tr>
					<td class="literal">Tipo</td>
					<td>
						<form:select id="tipo" path="vistaCampo.vistaCampoTipo.idtipo" cssClass="dato" cssStyle="width:100px" tabindex="5">
							<option value="">Todos</option>
							<c:forEach items="${listaTiposDato}" var="operador" varStatus="lst">
						  		<option value ="${operador.codigo}">${operador.descripcion}</option>
						  	</c:forEach>
						</form:select>
					</td>
					
					<td class="literal"  >Descripci&oacute;n</td>
					<td>
						<form:input path="descripcion" id="descripcion" cssClass="dato" tabindex="2" size="60" maxlength="60" onchange="this.value=quitaAcentos(this.value);this.value=this.value.toUpperCase();"/>
					</td>
				</tr>
			</table>
			</div>
			
		</form:form>
		
		<!-- Grid Jmesa -->
		<div id="grid">
	  		${mtoCamposPermitidos}		  							               
		</div> 	
			
		</div>
		<br />
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaVistaCampo.jsp"%>
	</body>
</html>