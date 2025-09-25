<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Mantenimiento de operadores permitidos por campo</title>
		
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
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/imprimir.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/moduloInformes/mtoOperadores.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>				
	
	</head>
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="javascript:SwitchSubMenu('sub12','sub11');">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		
		<!-- botones de la página -->
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td width="5">&nbsp;</td>
					<td align="left">						
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
		<p class="titulopag" align="left">Mantenimiento de operadores permitidos por campo</p>
		
		<form:form name="opCamposPermitidosForm" id="opCamposPermitidosForm" action="mtoOperadoresCamposPermitidos.run" method="post" commandName="operadorCamposPermitidosBean">	
				<input type="hidden" name="method" id="method"/>
				<input type="hidden" name="idOpCamposPermitido" id=idOpCamposPermitido value="${idOpCamposPermitido}"/>
				<input type="hidden" name="idVistaC" id="idVistaC" value="${idVistaC}"/>
				<input type="hidden" name="nombreVista" id=nombreVista/>
				<input type="hidden" name="idOperador" id=idOperador/>
				<input type="hidden" name="origenLlamada" id="origenLlamada" value=""/>
				<input type="hidden" name="tablaOrigenFiltroP" id="tablaOrigenFiltroP" />
				<input type="hidden" name="campoFiltroP" id="campoFiltroP" />
				<input type="hidden" name="operadorFiltroP" id="operadorFiltroP" />
		</form:form>
		
		<form:form name="opCamposCalculadosForm" id="opCamposCalculadosForm" action="mtoOperadoresCamposCalculados.run" method="post" commandName="operadorCamposCalculadosBean">	
				<input type="hidden" name="method" id="method"/>
				<input type="hidden" name="idOpCalculado" id="idOpCalculado" value="${idOpCalculado}"/>
				<input type="hidden" name="origenLlamada" id="origenLlamada" value=""/>
				<input type="hidden" name="idOperador" id=idOperador/>
				<input type="hidden" name="idcampoCalc" id=idcampoCalc value="${idcampoCalc}"/>
				<input type="hidden" name="tablaOrigenFiltroC" id="tablaOrigenFiltroC" />
				<input type="hidden" name="campoFiltroC" id="campoFiltroC" />
				<input type="hidden" name="operadorFiltroC" id="operadorFiltroC" />
		</form:form>
		
		<form:form id="main" action="mtoOperadoresCampos.run" method="post" commandName="operadorCamposGenerico">					
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			<input type="hidden" name="idOperadorGenerico" id="idOperadorGenerico"/>
			<input type="hidden" name="method" id="method"/>
			<input type="hidden" name="idVista" id="idVista" value="${idVista}"/>
			<input type="hidden" name="idVistaCampo" id="idVistaCampo"/>
			<input type="hidden" name="idOperador" id="idOperador"/>
			<input type="hidden" name="origenLlamada" id="origenLlamada" value="${origenLlamada}"/>
			<input type="hidden" name="isCalcOPermMain3" id=isCalcOPermMain3 value="${isCalcOPermMain3}"/>
			<input type="hidden" name="tablaOrigenStr" id="tablaOrigenStr" value="${tablaOrigenStr}"/>
			<input type="hidden" name="campoStr" id="campoStr" value="${campoStr}"/>
			<input type="hidden" name="operadorStr" id="operadorStr" value="${operadorStr}"/>
			
			<div class="panel2 isrt">
				<table width="100%">		
					<tr>
						<td class="literal">Tabla origen</td>
						<td>
							<select id="tablaOrigen" name="tablaOrigen" style="width:260px" class="dato" tabindex="3" onchange="javascript:vaciaCampo();habilitaOperadores();">
							  <option value="">Todas</option>
							  <c:forEach items="${lstVistas}" var="vista" varStatus="lst">
							  	<option value ="${vista.id}">${vista.nombre}</option>
							  </c:forEach>
							  <option value ="0">Campos Calculados</option>
							<select>
						</td>
						<td class="literal">Campo</td>
						<td>
							<input id="campo" name="campo" class="dato" size="28" maxlength="28" readonly="true" onchange=""/>
							<img id="iconoLupa" src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:displayFieldList();" alt="Buscar Campo" />
						</td>
						<td class="literal">Operador</td>
						<td>
							<select id="operador" name="operador" class="dato" style="width:180px">
								<option value="">Todos</option>
								<c:forEach items="${listaOperadores}" var="operador" varStatus="lst">
							  		<option value ="${operador.codigo}">${operador.descripcion}</option>
							  		<script>
							  			guardarOperador (${operador.codigo}, '${operador.descripcion}', ${operador.tipoNumerico});
							  		</script>
							  	</c:forEach>
							</select>
						</td>
					</tr>
				</table>
			</div>
			
		</form:form> 
		
		<!-- Grid Jmesa -->
		<div id="grid" style="width: 80%; margin:0 auto;">
	  		${opGenerico}		  							               
		</div> 	
			
		</div>
		<br />
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaVistaCampo.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaCamposCalculados.jsp"%>
	</body>
</html>