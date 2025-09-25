<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>



<html>
	<head>
		<title>Revision de Incidencias</title>

		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
		<!--  <link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />-->
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/util.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
		<script type="text/javascript" src="jsp/js/calendar.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<!--<script type="text/javascript" src="jsp/js/jquery.progressbar.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.progressbar.min.js"></script>		
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.filestyle.js"></script>
		<script type="text/javascript" src="jsp/js/ajaxfileupload.js"></script>
		-->
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>		
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
		<script type="text/javascript" src="jsp/moduloComisionesUnificado/incidenciasUnificado.js"></script>
		
		<script type="text/javascript">
		  	function cargarFiltro(){
				<c:forEach items="${sessionScope.consulta_LIMIT.filterSet.filters}" var="filtro">
					//alert(${filtro.property});
					var inputText = document.getElementById('${filtro.property}');
					//alert(inputText);
					if (null!=inputText){
						inputText.value = '${filtro.value}';
					}
				</c:forEach>
			}
	   </script>
	</head>
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="cargarFiltro();SwitchSubMenu('sub7', 'sub5');">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	
	<form:form name="main3" id="main3" action="incidenciasUnificado.run" method="post" commandName="ficheroIncidenciasUnificadoBean">
		
		<c:set var="ficheroTipo" value="${ficheroIncidenciasUnificadoBean.ficheroUnificado.tipoFichero}"/>
		
		<form:hidden path="ficheroUnificado.id" id="idFichero" />
		<form:hidden path="ficheroUnificado.tipoFichero" id="tipofichero" />
		
		<input type="hidden" id="idFicheroUnificado" name ="idFicheroUnificado" value="${idFicheroUnificado}"/>		
		<input type="hidden" id="pagina" name="pagina" />
		<input type="hidden" id="method" name="method" />	
		<input type="hidden" id="estadoFichero" name ="estadoFichero" value="${estadoFichero}"/>
		<input type="hidden" id="origenLlamada" name="origenLlamada" />
		<input type="hidden" id="idIncidencia" name="idIncidencia" />
		
		<input type="hidden" name="listaIdsMarcados" id="listaIdsMarcados" value=""/>
		<input type="hidden" name="listaIdsTodos" id="listaIdsTodos" value="${listaIdsTodos}"/>
		<input type="hidden" name="marcaTodos" id="marcaTodos" value="false"/>
		
		<input type="hidden" name="idEstadoRevision" id="idEstadoRevision"/>
				
		<div id="buttons"  style="padding-left:20px;">
			<table width="95%" cellspacing="0" cellpadding="0" border="0">
				<tr align="left">
					<td align="left">
						<a class="bot" id="btnVerificar" href="javascript:verificar();">Verificar Todos</a>
						
						<!--  <a class="bot" id="btnImprimir" href="javascript:imprimir(${totalListSize})">Imprimir</a>-->
						<a class="bot" id="btnImprimir" href="javascript:imprimir()">Imprimir</a> 
						<c:if test="${ficheroTipo!='G'}" >
							<a class="bot" id="btnCargar" href="javascript:openPopupAceptarDatos();">Aceptar Datos</a>
						</c:if>
						<a class="bot" id="btnRevisar" href="javascript:openPopupRevisar();">Revisar</a>
						<a class="bot" id="btnRecargarFichero" href="javascript:recargarFichero();">Recargar fichero</a>
						
					</td>
					<td align="center">		 
							<a class="bot" id="btnESMed" href="javascript:redirigir(4);">E-S Med.</a>
							<a class="bot" id="btnColectivo" href="javascript:redirigir(5);">Colectivos</a>
							<a class="bot" id="btnGGE" href="javascript:redirigir(6);">Param Grales.</a> 
							<a class="bot" id="btnCC"  href="javascript:redirigir(7);">Com E-S Med</a> 
							<a class="bot" id="btnReg" href="javascript:redirigir(8);">Descuentos</a>
					</td>
					<td align="right">
						<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a> 
						<a class="bot" id="btnLimpiar" href="javascript:limpiar();">Limpiar</a>
						<a class="bot" id="btnVolver" href="javascript:volver();">Volver</a>
					</td>
				</tr>
			</table>
		</div>
		
		<!-- Contenido de la página -->
		<div class="conten" style="padding: 3px; width: 97%">
			<p class="titulopag" align="left" id="titulo"></p>
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			<div class="panel2 isrt" style="width: 95%;margin:0 auto;">
				<fieldset><legend class="literal">Fichero</legend>
				<table align="center">				
					<tr>
						<td class="literal">Fichero</td>
						<td width="175px" class="detalI" id="fichero">${ficheroIncidenciasUnificadoBean.ficheroUnificado.nombreFichero}</td>
													
						<td class="literal">Fec. Carga</td>
						<td width="175px" class="detalI" id="fechacarga">
							<fmt:formatDate pattern="dd/MM/yyyy" value="${ficheroIncidenciasUnificadoBean.ficheroUnificado.fechaCarga}"/>
						</td> 								
						
						<td class="literal">Estado</td>
						<td width="100px" class="detalI" id="estadoFichero">${estadoFichero}</td>
					</tr>
				</table>
				</fieldset>
			</div>
			
			<div class="panel2 isrt" style="width: 95%;margin:0 auto;">
			<fieldset><legend class="literal">Filtro</legend>
			<table align="center">
				<tr>
					<td class="literal">Colectivo</td>
					<td width="100px">
						<form:input path="idcolectivo" id="colectivo" size="10" maxlength="7" cssClass="dato" />
					</td>
					<td class="literal">Línea</td>
					<td width="100px">
						<form:input path="linea.codlinea" id="linea" size="5" maxlength="3" cssClass="dato" />
						<form:hidden path="linea.codplan" id="plan" />
					</td>
					<td class="literal">E-S Med Fich.</td>
					<td width="100px">
						<form:input path="subentidad" id="subentidad" size="8" maxlength="8" cssClass="dato" />
						<form:hidden path="esMedColectivo" id="esMed_colectivo" />
					</td>
					<td class="literal">Oficina</td>
					<td width="100px">
						<form:input path="oficina" id="oficina"	size="5" maxlength="4" cssClass="dato" />
					</td>
					<td class="literal">Fase</td>
					<td width="100px">
						<form:input path="fase" id="fase"	size="5" maxlength="4" cssClass="dato" />
					</td>
				</tr>
				<tr>
				<%-- <c:if test="${ficheroTipo!='I'}" > --%>
					<td class="literal">Póliza</td>
					<td width="100px">
						<form:input path="refpoliza" id="refpoliza" size="8" maxlength="7" cssClass="dato" onchange="this.value=this.value.toUpperCase();"/>
					</td>
				<%-- </c:if> --%>
				<%-- <c:if test="${ficheroTipo=='I'}" >
					<form:hidden path="refpoliza" id="refpoliza" />
				</c:if> --%>
					<td class="literal">Estado</td>
					<td width="100px">
						<form:select path="estado" id="estado" cssClass="dato">
							<form:option value="">-- Seleccione una opción --</form:option>				
							<form:option value="E">Erróneo</form:option>
							<form:option value="A">Aviso</form:option>
							<form:option value="R">Revisado</form:option>
							<form:option value="C">Correcto</form:option>
						</form:select>
					</td>
					<td class="literal">Mensaje</td>
					<td width="250px" colspan="5">
						<form:input path="mensaje" id="mensaje" size="50" maxlength="40" cssClass="dato" />
					</td>
				</tr>
			</table>
			</fieldset>
		</div>
		
		<%@ include file="/jsp/moduloComisiones/popupAceptarDatos.jsp"%>
		<%@ include file="/jsp/moduloComisionesUnificado/popupRevisar.jsp"%>
		
	</form:form>
	
	<div id="grid" style="width: 95%;margin:0 auto;">
  		${consultaIncidenciasUnificado}
	</div>


<%@ include file="/jsp/common/static/piePagina.jsp"%>
<%@ include file="/jsp/common/static/overlay.jsp"%>

<div id="divMensajeError" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
	
	<div id="header-popup" style="padding:0.4em 1em;position:relative;
	   				              color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;
					              background:#525583;height:15px">
		<div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
		 	Errores
		</div>
		<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
		          font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
			<span onclick="cerrarPopUp()">x</span>
		</a>
	</div>
	
	<div class="panelInformacion_content">
		<div id="panelInformacion" class="panelInformacion">
			<div id="mensajeError"></div>			
			<div style="margin-top:15px;clear: both">
				<a class="bot" href="javascript:cerrarPopUp()">Aceptar</a>				
			</div>
		</div>
	</div>
</div>

</body>
</html>