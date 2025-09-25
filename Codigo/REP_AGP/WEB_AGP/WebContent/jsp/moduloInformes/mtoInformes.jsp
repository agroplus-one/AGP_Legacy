<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Mantenimiento de Informes</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
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
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/imprimir.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/moduloInformes/mtoInformes.js" ></script>
		<script type="text/javascript" src="jsp/moduloInformes/mtoInformesAjax.js" ></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<%@ include file="/jsp/js/draggable.jsp"%>				
	<script type="text/javascript">
		function cargarFiltro(){
			<c:forEach items="${sessionScope.mtoInforme_LIMIT.filterSet.filters}" var="filtro">
				if ('${filtro.property}' == 'nombre'){
					var inputText = document.getElementById('nombre');
					inputText.value = '${filtro.value}';
				}else if ('${filtro.property}' == 'titulo1'){
					var inputText = document.getElementById('titulo1');
					inputText.value = '${filtro.value}';
				}else if ('${filtro.property}' == 'titulo2'){
					var inputText = document.getElementById('titulo2');
					inputText.value = '${filtro.value}';
				}else if ('${filtro.property}' == 'titulo3'){
					var inputText = document.getElementById('titulo3');
					inputText.value = '${filtro.value}';
				}else if ('${filtro.property}' == 'visibilidad'){
					$('#visibilidad').val('${filtro.value}');
					document.main.visibilidad['${filtro.value}'].checked="true";
				}else if ('${filtro.property}' == 'perfil'){
					$('#perfilRadio').val('${filtro.value}');
				}else if ('${filtro.property}' == 'cuenta'){
					$('#cuenta').val('${filtro.value}');
				}else if ('${filtro.property}' == 'usuario.codusuario'){
					var inputText = document.getElementById('codusuario');
					inputText.value = '${filtro.value}';
				}
			</c:forEach>
		}			
		
	</script>				
	</head>
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" 
			onload="javascript:SwitchSubMenu('sub12','sub11');cargarFiltro();checksOnload();">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		
		<!-- botones de la página -->
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td width="5">&nbsp;</td>
					<td align="left">
						<a class="bot" id="btnDatosInforme" href="javascript:redirigir('datosInformes')">Datos informe</a>
						<a class="bot" id="btnCondiciones" href="javascript:redirigir('condiciones')">Condiciones</a>
						<a class="bot" id="btnClasif_Ruptura" href="javascript:redirigir('clasifYRuptura')">Clasificaci&oacute;n y ruptura</a>
						<a class="bot" id="btnGenerar" href="javascript:ajaxCheckInforme()">Generar</a>
					</td>
					<td align="right">
						<a class="bot" id="btnModificar" style="display:none" href="javascript:modificar()">Modificar</a>
						<a class="bot" id="btnAlta" href="javascript:alta()">Alta</a> 
						<a class="bot" id="btnConsultar" href="javascript:consultar(false);">Consultar</a> 
						<a class="bot" id="btnLimpiar"href="javascript:limpiar()">Limpiar</a>
					</td>
				</tr>
			</table>
		</div>
		
		<!-- Contenido de la página -->
		<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Mantenimiento de informes</p>
		
		<form name="datosInformesForm" id="datosInformesForm" action="mtoDatosInforme.run" method="post" commandName="datoInformesBean">
			<input type="hidden" name="method" id="method"/>
		</form>
		
		<!-- Form principal -->
		<form:form id="main" name="main" action="mtoInformes.run" method="post" commandName="informeBean" >
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>					
			<input type="hidden" name="usuarioSession" id="usuarioSession" value="${usuarioSession}"/>
			<input type="hidden" name="filtrarUsuSession" id="filtrarUsuSession" value="${filtrarUsuSession}"/>
			<input type="hidden" name="idInforme" id="idInforme" value="${idInforme}"/>
			<input type="hidden" name="method" id="method"/>
			<input type="hidden" name="redireccion" id="redireccion"/>
			<input type="hidden" name="origenLlamada" id="origenLlamada"/>
			<input type="hidden" name="condicionesOk" id="condicionesOk" />
			<input type="hidden" name="cadenaCodigosLupas" id="cadenaCodigosLupas" value="${cadenaUsuarios}"/>
			<input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
			<input type="hidden" name="entidad" id="entidad" value="${entidad}"/>
			<input type="hidden" name="entidadAux" id="entidadAux" value="${entidadAux}"/>
			<input type="hidden" name="tituloInfoDuplicado" id="tituloInfoDuplicado" value="${tituloInfoDuplicado}"/>
			<input type="hidden" name="cuentaAux" id="cuentaAux" value="${cuenta}"/>
			
			<input type="hidden" name="oficina" id="oficina" value=""/>
			<input type="hidden" name="usuarioTemp" id="usuarioTemp"/>
			<input type="hidden" id="perfil" value="${perfil}" />
			
			<input type="hidden" disabled="disabled" name="codCuentaNo" id="codCuentaNo" value="${codCuentaNo}"/>
			<input type="hidden" disabled="disabled" name="codCuentaSi" id="codCuentaSi" value="${codCuentaSi}"/>
			<input type="hidden" disabled="disabled" name="vsbTodos" id="vsbTodos" value="${vsbTodos}"/>
			<input type="hidden" disabled="disabled" name="vsbPerfil" id="vsbPerfil" value="${vsbPerfil}"/>
			<input type="hidden" disabled="disabled" name="vsbUsuarios" id="vsbUsuarios" value="${vsbUsuarios}"/>
			<input type="hidden" disabled="disabled" name="vsbEntidades" id="vsbEntidades" value="${vsbEntidades}"/>
			
			<input type="hidden" id="entidadesBox" name="entidadesBox" value="${entidadesBox}"/>
			<input type="hidden" id="usuariosBox" name="usuariosBox" value ="${usuariosBox}"/>
							
			<div class="panel2 isrt">
			
			<table width="100%">
				
				<tr>
					<td class="literal"  style="width:60px">Nombre</td>
					<td colspan="3">
						<form:input id="nombre" path="nombre"  cssClass="dato" size="95" maxlength="100" onchange="this.value=quitaAcentos(this.value);this.value=this.value.toUpperCase();"/>
					</td>
					<td class="literal">Usuario</td>
					<td class="literal">
						<form:input path="usuario.codusuario" id="codusuario" size="20" maxlength="19" cssClass="dato" tabindex="3" onchange="this.value=this.value.toUpperCase();"/>
						<img  border="1" id="iconoLupaUsu" src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Usuario','principio', '', '');" alt="Buscar Usuario"/>
					
					</td>
					
				</tr>
				<tr></tr>
				<tr>
				
					<td class="literal"  style="width:45x">T&iacute;tulo 1</td>
					<td>
						<form:input path="titulo1" id="titulo1"  cssClass="dato" size="30" maxlength="100" onchange="this.value=quitaAcentos(this.value);this.value=this.value.toUpperCase();"/>
					</td>
					<td class="literal"  style="width:40x">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;T&iacute;tulo 2</td>
					<td>
						<form:input path="titulo2" id="titulo2"  cssClass="dato" size="30" maxlength="100" onchange="this.value=quitaAcentos(this.value);this.value=this.value.toUpperCase();"/>
					</td>
					<td class="literal">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;T&iacute;tulo&nbsp;3</td>
					<td>
						<form:input path="titulo3" id="titulo3"  cssClass="dato" size="30" maxlength="100" onchange="this.value=quitaAcentos(this.value);this.value=this.value.toUpperCase();"/>
					</td>
					<td class="literal">Cuenta</td>
					<td class="literal">
					   <form:select id="cuenta" path="cuenta" cssClass="dato" cssStyle="width:65"> 
					   		<form:option value="">Todos</form:option>
					   		<form:option value="${codCuentaNo}">No</form:option>
					   		<form:option value="${codCuentaSi}">Si</form:option>
						</form:select>
					</td>
				</tr>
				
				<tr><td></td></tr>
				<tr><td></td></tr>
				
				<tr>
					<td class="literal">Visibilidad</td>
					<td colspan="4">
						<table>
							<td class="detalI" >
								<!-- Todos -->
								<form:checkbox path="visibilidad" id="checkTodos" value="${vsbTodos}"/> Todos&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								
								<!-- Perfil -->
								<input type="checkbox"  name="visibilidad" id="checkPerfil" value="${vsbPerfil}">  Perfil&nbsp;
								<form:select id="perfilRadio" path="perfil" cssClass="dato" cssStyle="width:70px;" disabled="true" >
									<option value="0">0</option>
									<option value="1">1</option>
									<option value="2">2</option>
									<option value="3">3</option>
									<option value="4">4</option>
									<option value="5">5</option>
									<option value="6">1 Ext.</option>
									<option value="7">3 Ext.</option>
								</form:select>
								<!-- Usuarios -->
								<input type="checkbox" name="visibilidad" id="checkUsuarios" value="${vsbUsuarios}"> Usuario(s)&nbsp;
								<select multiple size="2" id="multUsers" name="multipleUsuarios" class="dato" style="width:80px;" disabled="disabled">
								</select>
							</td>
							<td align="left" style="width:4px">
									<img  border="1" id="iconoBorrar" src="jsp/img/displaytag/delete.png" align="left" style="cursor: hand;display:none" onclick="javascript:deleteValue('multUsers');" alt="Borrar Usuario"/>
									<img  border="1" id="iconoLupa" src="jsp/img/magnifier.png" align="left" style="cursor: hand;display:none" onclick="javascript:lupas.muestraTabla('UsuarioMulti','principio', '', '');" alt="Buscar Usuario"/>	
							</td>
							<td class="detalI" >
								<!-- Entidades -->
								<input type="checkbox" name="visibilidadEnt" id="checkEntidades" value="${vsbEntidades}"> Entidad(es)&nbsp;
								<select multiple size="2" id="multEnts" name="multipleEntidades" class="dato" style="width:80px;" disabled="disabled">
								</select>
							</td>
							
							<form:hidden path="fechaAlta" id="fechaAlta" />
							
							<td align="left" style="width:4px">
								<img  border="1" id="iconoBorrarEnt" src="jsp/img/displaytag/delete.png" align="left" style="cursor: hand;display:none" onclick="javascript:deleteValue('multEnts');" alt="Borrar Entidad"/>
								<img  border="1" id="iconoLupaEnt" src="jsp/img/magnifier.png" align="left" style="cursor: hand;display:none" onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');" alt="Buscar Entidad"/>	
							</td>
						</table>
					</td>
				</tr>
				
			</table>
			
			</div>
			
		</form:form>
			<!-- Grid Jmesa -->
			<div id="grid">
		  		${mtoInforme}		  							               
			</div> 	
			
	</div>
	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<%@ include file="/jsp/moduloInformes/popupFormatoInforme.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaUsuario.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaUsuarioMulti.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>	
	<%@ include file="/jsp/moduloInformes/popupDuplicarInforme.jsp"%>	
	
	</body>
</html>