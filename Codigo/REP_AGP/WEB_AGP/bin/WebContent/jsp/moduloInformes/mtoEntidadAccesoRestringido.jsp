<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Mantenimiento de Entidades con Acceso Restringido</title>
		
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
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/moduloInformes/mtoEntidadAccesoRestringido.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>

	</head>
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub11');">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		<!-- botones de la página -->
		<div id="buttons" >
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tbody>
					<tr>
						<td align="right">
							<a class="bot" id="btnModificar" style="display:none" href="javascript:modificar();">Modificar</a>
							<a class="bot" id="btnAlta" href="javascript:alta();">Alta</a>
							<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
							<a class="bot" href="javascript:limpiar();">Limpiar</a>							
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%" >
		<p class="titulopag" align="left">Entidades con acceso restringido</p>					
			
			<form:form name="main3" id="main3" action="mtoEntidadesAccesoRestringido.run" method="post" commandName="entidadAccesoRestringido">
		        
		        <input type="hidden" name=codDenegado id="codDenegado" value="${codDenegado}" />
		        <input type="hidden" name=codPermitido id="codPermitido" value="${codPermitido}" />
		        <input type="hidden" name=codPermitidoConcreto id="codPermitidoConcreto" value="${codPermitidoConcreto}" />
		        <input type="hidden" name="method" id="method"/>
		        <input type="hidden" name="grupoEntidades" id="grupoEntidades" value=""/>
		        <form:hidden path="id" id="id"/>
		        
		        <%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div style="width:97%;margin:0 auto;">
					<fieldset>
						<table style="margin:0 auto;">
							<tr align="left">
								<td valign="middle">
									<div class="literal">Entidad</div>
								</td>
								
								<td>&nbsp;</td>
								
								<td colspan="3" valign="middle">
									<form:input path="entidad.codentidad" size="5" maxlength="4" cssClass="dato" id="entidad" tabindex="1" onchange="javascript:lupas.limpiarCampos('desc_entidad');"/>
									<form:input path="entidad.nomentidad" cssClass="dato" id="desc_entidad" size="40" readonly="true"/>
									<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');" alt="Buscar Entidad" title="Buscar Entidad" />
								</td>
								
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								
								<td colspan="3">
									<fieldset class="literal">
										<legend class="literal">Acceso al diseñador</legend>
										&nbsp;&nbsp;
										<form:select path="accesoDisenador" id="accesoDisenador" cssClass="dato" tabindex="17" onchange="javascript:comprobarValor(this.value);">
											<form:option value="">Todos</form:option>	
											<form:option value="${codDenegado}">Denegado</form:option>
											<form:option value="${codPermitido}">Permitido</form:option>
										</form:select>
										
									</fieldset>
								</td>
								
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								
								<td colspan="3">
									<fieldset class="literal">
										<legend class="literal">Acceso al generador</legend>
										
										<form:select path="accesoGenerador" id="accesoGenerador" cssClass="dato" tabindex="17">
											<form:option value="">Todos</form:option>	
											<form:option value="${codDenegado}">Denegado</form:option>
											<form:option value="${codPermitido}">Permitido</form:option>
											<form:option value="${codPermitidoConcreto}">Permitido a Usuarios y Perfiles concretos</form:option>
										</form:select>
										
									</fieldset>
								</td>
								
							</tr>
						</table>
					</fieldset>
				</div>
			</form:form>
			
			<!-- Grid Jmesa -->
			<div id="grid">
		  		${entidadAccesoRestringidoListado}		  							               
			</div> 	
	</div>
				
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
	
</body>
</html>