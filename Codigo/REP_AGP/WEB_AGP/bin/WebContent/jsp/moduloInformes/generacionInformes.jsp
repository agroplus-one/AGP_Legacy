<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Mantenimiento de informes</title>
		
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
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/moduloInformes/generacionInformes.js" ></script>
		<script type="text/javascript" src="jsp/moduloInformes/mtoInformesAjax.js" ></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
	<script type="text/javascript">
		function cargarFiltro(){
			<c:forEach items="${sessionScope.generacionInformes_LIMIT.filterSet.filters}" var="filtro">
			alert(${filtro.property});
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
				}else if ('${filtro.property}' == 'usuario.codusuario'){
					var inputText = document.getElementById('codusuario');
					inputText.value = '${filtro.value}';
				}
			</c:forEach>
		}
</script>						
	</head>
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="javascript:SwitchMenu('sub11');cargarFiltro();">
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
						<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a> 
						<a class="bot" id="btnLimpiar"href="javascript:limpiar()">Limpiar</a>						
					</td>
				</tr>
			</table>
		</div>
		
		<!-- Contenido de la pï¿½gina -->
		<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Generador de informes</p>
		<form:form id="main" name="main" action="generacionInforme.run" method="post" commandName="informeBean">					
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>	
			<input type="hidden" name="usuarioSession" id="usuarioSession" value="${usuarioSession}"/>
			<input type="hidden" name="filtrarUsuSession" id="filtrarUsuSession" value="${filtrarUsuSession}"/>
			<input type="hidden" name="idInforme" id="idInforme"/>
			<input type="hidden" name="method" id="method"/>
			<input type="hidden" name="origenLlamada" id="origenLlamada"/>
			<input type="hidden" name="sqlInforme" id="sqlInforme" value="${sqlInforme}"/>
			<input type="hidden" name="condicionesOk" id="condicionesOk" />
			<input type="hidden" id="perfil" name="perfil" value="${perfil}"/>
			<input type="hidden" id="grupoOficinas" name="grupoOficinas" value="${grupoOficinas}"/>
			
			
			<c:choose>
				<c:when test="${perfil != 0}">
					<input type="hidden" value="${sessionScope.usuario.oficina.id.codentidad}" id="entidad" />
				</c:when>
				<c:otherwise>
					<input type="hidden" value=""  id="entidad" />
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${perfil == 3 || perfil==2}">
					<input type="hidden" value="${sessionScope.usuario.oficina.id.codoficina}"  id="oficina" />
				</c:when>
				<c:otherwise>
					<input type="hidden" value=""  id="oficina" />
				</c:otherwise>
			</c:choose>
			<input type="hidden" value="${grupoEntidades}"  id="grupoEntidades" />
		
			<input type="hidden" id="cadenaUsuarios" name="cadenaUsuarios" size="50" maxlength="50" value="${cadenaUsuarios}"/>
			<fieldset> 
			<div class="panel2 isrt">
				<table width="100%">	
					<tr>
						<td class="literal"  style="width:60px">Nombre</td>
						<td colspan="3">
							<form:input id="nombre" path="nombre"  cssClass="dato" tabindex="1"   size="102" maxlength="100" onchange="this.value=quitaAcentos(this.value);this.value=this.value.toUpperCase();"/>
						</td>
						
						<c:if test="${perfil != 4}">
						<td class="literal">Usuario</td>
							<td class="literal">
								<form:input path="usuario.codusuario" id="codusuario" size="20" maxlength="19" cssClass="dato" tabindex="3" onchange="this.value=this.value.toUpperCase();"/>
								<img  border="1" id="iconoLupa" src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Usuario','principio', '', '');" alt="Buscar Usuario"/>
							</td>
						</c:if>
						
					</tr>
					<tr>
						<td class="literal"  style="width:45x">T&iacute;tulo 1</td>
						<td>
							<form:input path="titulo1" id="titulo1"  cssClass="dato" tabindex="2"   size="30" maxlength="30" onchange="this.value=quitaAcentos(this.value);this.value=this.value.toUpperCase();"/>
						</td>
						<td class="literal"  style="width:40x">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;T&iacute;tulo 2</td>
						<td>
							<form:input path="titulo2" id="titulo2"  cssClass="dato" tabindex="3"   size="30" maxlength="30" onchange="this.value=quitaAcentos(this.value);this.value=this.value.toUpperCase();"/>
						</td>
						<td class="literal">T&iacute;tulo&nbsp;3</td>
						<td>
							<form:input path="titulo3" id="titulo3"  cssClass="dato" tabindex="4" size="30" maxlength="30" onchange="this.value=quitaAcentos(this.value);this.value=this.value.toUpperCase();"/>
						</td>
					</tr>
				</table>
				</fieldset>
			
			
		</form:form>
		
		<!-- Grid Jmesa -->
		<div id="grid">
	  		${generacionInformes}		  							               
		</div> 	
			
		</div>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>	
	<%@ include file="/jsp/moduloInformes/popupFormatoInforme.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaUsuario.jsp"%>
				
	</body>
</html>