<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Consultas de Comisiones</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/moduloComisiones/informesComisiones.js" ></script>
		
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

	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="javascript:SwitchSubMenu('sub9', 'sub5');cargarFiltro();">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		<%@ include file="/jsp/js/generales.jsp"%>
		
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr align="left">
					<td align="right">
						<a class="bot" id="btnGenerar" href="javascript:generar()">Generar</a>
						<a class="bot" id="btnLimpiar" href="javascript:limpiar()">Limpiar</a>
					</td>
				</tr>
			</table>
		</div>
		
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%" >
		<p class="titulopag" align="left">Consultas de Comisiones</p>	
		
			<form:form name="main3" id="main3" action="informesComisiones.run" method="post" commandName="informeComisionesBean">
				
				<input type="hidden" name="idInforme" id="idInforme" value="${idInforme}"/>
				<input type="hidden" id="method" name="method" value="" />
				<input type="hidden" id="lstCodEntidades" name="lstCodEntidades" value="${lstCodEntidades}" />
				<input type="hidden" id="perfil" value="${perfil}" />
				<input type="hidden" id="entMed" value="${entMed}" />
				<input type="hidden" id="subEntMed" value="${subEntMed}" />
				<input type="hidden" id="esExterno" value="${esExterno}" />
				
				<form:hidden path="datosDe" id="datosDe"/>

								
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>	
				
					<fieldset style="width:90%;margin: 0 auto;">
						<legend class="literal">Formato</legend>
							<table align="center">
								<tr>
									<td  class="literal">
										<form:radiobutton cssClass="literal" id="formato" path="formato" value="3" />EXCEL
										<form:radiobutton  cssClass="literal" id="formato" path="formato" value="1" />HTML
										<form:radiobutton cssClass="literal" id="formatoPDF" path="formato" value="2" />PDF
										<form:radiobutton cssClass="literal" id="formato" path="formato" value="4" />TXT
									</td>
									<td class="literal" width="200px" align="right">Mostrar datos de: &nbsp&nbsp</td>
										 
									<td class="literal" width="100px" style="background-color:#91FFA2">
										<input type="radio" class="literal" id="mostrarDatosResumen" name="mostrarDatos" value="resumen" checked="true" onclick="deshabilitaRefYNif()">Resumen
									</td>	
									<td class="literal" width="100px" style="background-color:#e5e5e5">	
										<input type="radio" class="literal" id="mostrarDatos" name="mostrarDatos" value="detalle" onclick="habilitaRefYNif()">Detalle
									</td>
								</tr>
							</table>		
					 </fieldset>
					  <fieldset style="width:90%;margin: 0 auto;">
						<legend class="literal">Condiciones en el Informe</legend>
							<table align="center">
								<tr>
									<td class="literal">N&uacute;mero fase: </td>
									<td><form:select path="condiFase" cssClass="dato" id="condiFase" cssStyle="width:100" >
											<form:option value=""></form:option>
											<form:option value="5">Igual</form:option>
											<form:option value="3">Contenido en</form:option>
											<form:option value="4">Entre</form:option>											
										</form:select></td>
									<td><form:input path="fase" id="fase" cssClass="dato" size="30"/></td>
									<td class="literal">Colectivo: </td>
									<td><form:select path="condiColectivo" cssClass="dato" id="condiColectivo" cssStyle="width:100" >
											<form:option value="" ></form:option>
											<form:option value="5">Igual</form:option>
											<form:option value="3">Contenido en</form:option>										
										</form:select></td>
									<td><form:input path="colectivo" id="colectivo" cssClass="dato" size="30"/></td>
								</tr>
								<tr>	
									<td class="literal">L&iacute;nea: </td>
									<td><form:select path="condiLinea" cssClass="dato" id="condiLinea" cssStyle="width:100" >
											<form:option value="" ></form:option>
											<form:option value="5">Igual</form:option>
											<form:option value="3">Contenido en</form:option>									
										</form:select></td>
									<td><form:input path="linea" id="linea" cssClass="dato" size="30"/></td>
									<td class="literal">Plan: </td>
									<td><form:select path="condiPlan" cssClass="dato" id="condiPlan" cssStyle="width:100" >
											<form:option value="" ></form:option>
											<form:option value="5">Igual</form:option>
											<form:option value="3">Contenido en</form:option>
											<form:option value="4">Entre</form:option>											
										</form:select></td>
									<td><form:input path="plan" id="plan" cssClass="dato" size="30"/></td>			
								</tr>
								
								<tr>	
									<td class="literal">Entidad: </td>
									<td><form:select path="condiEntidad" cssClass="dato" id="condiEntidad" cssStyle="width:100" >
											<c:choose>
												<c:when test="${perfil eq '0'}">
													<form:option value="" ></form:option>
													<form:option value="5">Igual</form:option>
													<form:option value="3">Contenido en</form:option>
												</c:when>
												<c:when test="${perfil eq '1'}">
													<form:option value="5">Igual</form:option>
												</c:when>
												<c:when test="${perfil eq '5'}">
													<form:option value="5">Igual</form:option>
													<form:option value="3">Contenido en</form:option>
												</c:when>
											</c:choose>
										</form:select></td>
									<td><form:input path="entidad" id="entidad" cssClass="dato" size="30"/></td>
									
									<td class="literal">Oficina: </td>
									<td><form:select path="condiOficina" cssClass="dato" id="condiOficina" cssStyle="width:100" >
											<form:option value="" ></form:option>
											<form:option value="5">Igual</form:option>
											<form:option value="3">Contenido en</form:option>
										</form:select></td>
									<td><form:input path="oficina" id="oficina" cssClass="dato" size="30"/></td>			
								</tr>
								
								<tr>	
									<td class="literal">Entidad med.: </td>
									<td>
										<form:select path="condiEntidadMed" cssClass="dato" id="condiEntidadMed" cssStyle="width:100" >
											<c:choose>
											  <c:when test="${perfil eq '0'}">
											  	<form:option value="" ></form:option>
												<form:option value="5">Igual</form:option>
												<form:option value="3">Contenido en</form:option>	
											  </c:when>
											  <c:when test="${perfil eq '1' && esExterno == 0}">
											  	<form:option value="" ></form:option>
												<form:option value="5">Igual</form:option>
												<form:option value="3">Contenido en</form:option>	
											  </c:when>
											  <c:when test="${perfil eq '1' && esExterno == 1}">
												<form:option value="5">Igual</form:option>
											  </c:when>
											  <c:when test="${perfil eq '5'}">
											  	<form:option value="" ></form:option>
												<form:option value="5">Igual</form:option>
												<form:option value="3">Contenido en</form:option>	
											  </c:when>
											</c:choose>
										</form:select>
									</td>
									<td><form:input path="entidadMed" id="entidadMed" cssClass="dato" size="30"/></td>
									<td class="literal">Subentidad med.: </td>
									<td><form:select path="condiSubent" cssClass="dato" id="condiSubent" cssStyle="width:100" >
											<c:choose>
												<c:when test="${esExterno eq '1'}">
													<form:option value="5">Igual</form:option>
												</c:when>
												<c:otherwise>
													<form:option value="" ></form:option>
													<form:option value="5">Igual</form:option>
													<form:option value="3">Contenido en</form:option>
											 	</c:otherwise>
											 </c:choose>								
										</form:select></td>
									<td><form:input path="subent" id="subent" cssClass="dato" size="30"/></td>			
								</tr>
								<tr style="background-color:#e5e5e5">	
									<td class="literal" >Ref. P&oacute;liza: </td>
									<td><form:select path="condiReferencia" cssClass="dato" id="condiReferencia" cssStyle="width:100" disabled="true">
											<form:option value="" ></form:option>
											<form:option value="5">Igual</form:option>
											<form:option value="3">Contenido en</form:option>										
										</form:select></td>
									<td><form:input path="referencia" id="referencia" cssClass="dato" size="30" disabled="true"/></td>
									<td class="literal">Nif. aseg.: </td>
									<td><form:select path="condiNif" cssClass="dato" id="condiNif" cssStyle="width:100" disabled="true">
											<form:option value="" ></form:option>
											<form:option value="5">Igual</form:option>
											<form:option value="3">Contenido en</form:option>										
										</form:select></td>
									<td><form:input path="nif" id="nif" cssClass="dato" size="30" disabled="true"/></td>		
								</tr>
								<tr>	
									<td class="literal">Fecha aceptaci&oacute;n: </td>
									<td><form:select path="condiFechaAcep" cssClass="dato" id="condiFechaAcep" cssStyle="width:100" >
											<form:option value="" ></form:option>
											<form:option value="5">Igual</form:option>
											<form:option value="4">Entre</form:option>											
										</form:select></td>
									<td><form:input path="fechaAcep" id="fechaAcep" cssClass="dato" size="30"/></td>
								</tr>
							</table>		
					 </fieldset>
					 <fieldset style="width:90%;margin: 0 auto;">
						<legend class="literal">Campos a mostrar en el Informe</legend>
							<table width="100%" align="center" border="1" >
								<tr align="right">
									<td class="literal" colspan="9">Marcar todo&nbsp;</td>
									<td align="left" style="background-color:#e5e5e5"><input type="checkbox" name="checkTodo" id="checkTodo"  class="dato" onclick="this.checked  ? marcar_todos() : desmarcar_todos() "/></td>
								</tr>
								<tr align="left">
									<td class="literal" colspan="2">Datos generales</td>
									
									<td class="literal" colspan="2">Nuevo</td>
									
									<td class="literal" colspan="2">Regularización</td>
									
									<td class="literal" colspan="2">Total (N + R)</td>
									
									<td class="literal" colspan="2">Reglamento</td>
								</tr>
								<c:choose>
									<c:when test="${esExterno eq '1'}"> <!--***********************  USUARIOS PERFIL 1 EXTERNO ****************************-->
										<tr align="left">
											<td class="literal" >1-N&uacute;mero fase </td>
											<td  style="background-color:#e5e5e5"><form:checkbox path="checkFase" id="checkFase" onclick="desmarcaCheckTodo(this);" tabindex="1"/></td>
											<td class="literal" >17-Prima base </td>
											<td  style="background-color:#e5e5e5"><form:checkbox path="checkPrimaBasN" id="checkPrimaBasN" onclick="desmarcaCheckTodo(this);" tabindex="17"/></td>
											<td class="literal" >20-Prima base</td>
											<td  style="background-color:#e5e5e5"><form:checkbox path="checkPrimaBasR" id="checkPrimaBasR" onclick="desmarcaCheckTodo(this);" tabindex="20"/></td>
											<td class="literal" >23-Prima base</td>
											<td  style="background-color:#e5e5e5"><form:checkbox path="checkPrimaBasT" id="checkPrimaBasT" onclick="desmarcaCheckTodo(this);" tabindex="23"/></td>
											<td class="literal" style="background-color:#e5e5e5">29-Cód. Tram.</td>
											<td style="background-color:#e5e5e5"><form:checkbox path="checkCodTram" id="checkCodTram" onclick="desmarcaCheckTodo(this);" tabindex="29"/></td>
										</tr>
										<tr align="left">	
											<td class="literal" >2-Entidad med.</td>
											<td  style="background-color:#e5e5e5"><form:checkbox path="checkEntidadMed" id="checkEntidadMed" onclick="desmarcaCheckTodo(this);" tabindex="2"/></td>
											<td class="literal" >18-G.G Subent.</td>
											<td  style="background-color:#e5e5e5"><form:checkbox path="checkGGSubEntN" id="checkGGSubEntN" onclick="desmarcaCheckTodo(this);" tabindex="18"/></td>
											<td class="literal" >21-G.G Subent.</td>
											<td  style="background-color:#e5e5e5"><form:checkbox path="checkGGSubEntR" id="checkGGSubEntR" onclick="desmarcaCheckTodo(this);" tabindex="21"/></td>
											<td class="literal" >24-G.G Subent.</td>
											<td  style="background-color:#e5e5e5"><form:checkbox path="checkGGSubEntT" id="checkGGSubEntT" onclick="desmarcaCheckTodo(this);" tabindex="24"/></td>
											<td class="literal" style="background-color:#e5e5e5">30-Tipo Tram.</td>
											<td style="background-color:#e5e5e5"><form:checkbox path="checkTipoTram" id="checkTipoTram" onclick="desmarcaCheckTodo(this);" tabindex="30"/></td>
										</tr>
										<tr align="left">	
											<td class="literal" >3-Subentidad med.</td>
											<td  style="background-color:#e5e5e5"><form:checkbox path="checkSubent" id="checkSubent" onclick="desmarcaCheckTodo(this);" tabindex="3"/></td>
											<td class="literal" >19-Comision Subent.</td>
											<td  style="background-color:#e5e5e5"><form:checkbox path="checkComSubEntN" id="checkComSubEntN" onclick="desmarcaCheckTodo(this);" tabindex="19"/></td>
											<td class="literal" >22-Comision Subent.</td>
											<td  style="background-color:#e5e5e5"><form:checkbox path="checkComSubEntR" id="checkComSubEntR" onclick="desmarcaCheckTodo(this);" tabindex="22"/></td>
											<td class="literal" >25-Comision Subent.</td>
											<td  style="background-color:#e5e5e5"><form:checkbox path="checkComSubEntT" id="checkComSubEntT" onclick="desmarcaCheckTodo(this);" tabindex="25"/></td>
											<td class="literal" >31-Importe aplicado Tram.</td>
											<td style="background-color:#e5e5e5"><form:checkbox path="checkImpApliTram" id="checkImpApliTram" onclick="desmarcaCheckTodo(this);" tabindex="31"/></td>
										</tr>
										<tr align="left">	
											<td class="literal" style="background-color:#e5e5e5">4-Entidad</td>
											<td  style="background-color:#e5e5e5"><form:checkbox path="checkEntidad" id="checkEntidad" onclick="desmarcaCheckTodo(this);" tabindex="5"/></td>
											<td></td><td></td>
											<td></td><td></td>
											<td class="literal" style="background-color:#91FFA2">26-Gasto Pendiente</td>
											<td  style="background-color:#e5e5e5"><form:checkbox path="checkGasPenT" id="checkGasPenT" onclick="desmarcaCheckTodo(this);" tabindex="26"/></td>
											<td class="literal" >32-Importe sin reducc. Tram.</td>
											<td style="background-color:#e5e5e5"><form:checkbox path="checkImpSinReduccTram" id="checkImpSinReduccTram" onclick="desmarcaCheckTodo(this);" tabindex="32"/></td>
										</tr>
										<tr align="left">
											<td class="literal" style="background-color:#e5e5e5">5-Oficina</td>
											<td  style="background-color:#e5e5e5"><form:checkbox path="checkOficina" id="checkOficina" onclick="desmarcaCheckTodo(this);" tabindex="5"/></td>
											<td></td><td></td>
											<td></td><td></td>
											<td class="literal" style="background-color:#91FFA2">27-Total Subentidad</td>
											<td  style="background-color:#e5e5e5"><form:checkbox path="checkTotSubentT" id="checkTotSubentT" onclick="desmarcaCheckTodo(this);" tabindex="27"/></td>
											<td class="literal" style="background-color:#e5e5e5">33-Cód. Calidad </td>
											<td style="background-color:#e5e5e5"><form:checkbox path="checkcodCalidad" id="checkcodCalidad" onclick="desmarcaCheckTodo(this);" tabindex="33"/></td>
										</tr>
										<tr align="left">
											<td class="literal" >6-Plan</td>
											<td  style="background-color:#e5e5e5"><form:checkbox path="checkPlan" id="checkPlan" onclick="desmarcaCheckTodo(this);" tabindex="6"/></td>
											<td></td><td></td>
											<td></td><td></td>
											<td class="literal" >28-Coste total</td>
											<td  style="background-color:#e5e5e5"><form:checkbox path="checkCosteTotalT" id="checkCosteTotalT" onclick="desmarcaCheckTodo(this);" tabindex="28"/></td>
											<td class="literal" style="background-color:#e5e5e5">34-Tipo Calidad </td>
											<td style="background-color:#e5e5e5"><form:checkbox path="checkTipoCalidad" id="checkTipoCalidad" onclick="desmarcaCheckTodo(this);" tabindex="34"/></td>
										</tr>
										<tr align="left">
											<td class="literal" >7-L&iacute;nea</td>
											<td  style="background-color:#e5e5e5"><form:checkbox path="checkLinea" id="checkLinea" onclick="desmarcaCheckTodo(this);" tabindex="7"/></td>
											<td></td><td></td>
											<td></td><td></td>
											<td></td><td></td>
											<td class="literal" >35-Importe aplicado Calidad </td>
											<td style="background-color:#e5e5e5"><form:checkbox path="checkImpApliCal" id="checkImpApliCal" onclick="desmarcaCheckTodo(this);" tabindex="35"/></td>
										</tr>
										<tr align="left">
											<td class="literal" >8-Colectivo</td>
											<td  style="background-color:#e5e5e5"><form:checkbox path="checkColectivo" id="checkColectivo" onclick="desmarcaCheckTodo(this);"tabindex="8"/></td>	
											<td></td><td></td>
											<td></td><td></td>
											<td></td><td></td>
											<td class="literal" >36-Importe sin reducc. Calidad </td>
											<td style="background-color:#e5e5e5"><form:checkbox path="checkImpSinReduccCal" id="checkImpSinReduccCal" onclick="desmarcaCheckTodo(this);" tabindex="36"/></td>
										</tr>
										<tr align="left">
											<td class="literal" >9-Fecha aceptaci&oacute;n</td>
											<td  style="background-color:#e5e5e5"><form:checkbox path="checkFechaAcep" id="checkFechaAcep" onclick="desmarcaCheckTodo(this);" tabindex="9"/></td>	
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											<td class="literal" >37-Total Tram + Calidad </td>
											<td style="background-color:#e5e5e5"><form:checkbox path="checkTotTramCal" id="checkTotTramCal" onclick="desmarcaCheckTodo(this);" tabindex="37"/></td>
											
										</tr>
										<tr align="left">
											<td class="literal" style="background-color:#91FFA2">10-Tomador</td>
											<td  style="background-color:#e5e5e5"><form:checkbox path="checkTomador" id="checkTomador" onclick="desmarcaCheckTodo(this);" tabindex="10"/></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td><td></td>									
											
										</tr>
										<tr align="left">
											<td class="literal" style="background-color:#e5e5e5">11-Ref. p&oacute;liza</td>
											<td style="background-color:#e5e5e5"><form:checkbox path="checkReferencia" id="checkReferencia" onclick="desmarcaCheckTodo(this);" tabindex="11"/></td>    
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td><td></td>
											
										</tr>
										<tr align="left">
										    <td class="literal" style="background-color:#e5e5e5">12- NIF asegurado</td>
											<td style="background-color:#e5e5e5"><form:checkbox path="checkNif" id="checkNif" onclick="desmarcaCheckTodo(this);" tabindex="12"/></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td><td></td>
											
										</tr>
										<tr align="left">
											<td class="literal" style="background-color:#e5e5e5">13-Nombre asegurado</td>
											<td style="background-color:#e5e5e5"><form:checkbox path="checkNombre" id="checkNombre" onclick="desmarcaCheckTodo(this);" tabindex="13"/></td>	
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											
										</tr>
										<tr align="left">
											<td class="literal" style="background-color:#e5e5e5">14-Apellido 1</td>
											<td style="background-color:#e5e5e5"><form:checkbox path="checkApe1" id="checkApe1" onclick="desmarcaCheckTodo(this);" tabindex="14"/></td>	
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td>
										</tr>
										<tr align="left">
											<td class="literal" style="background-color:#e5e5e5">15-Apellido 2</td>
											<td style="background-color:#e5e5e5"><form:checkbox path="checkApe2" id="checkApe2" onclick="desmarcaCheckTodo(this);" tabindex="15"/></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td>	
										</tr>
										<tr align="left">										
											<td class="literal" style="background-color:#e5e5e5">16-Raz&oacute;n social</td>
											<td style="background-color:#e5e5e5"><form:checkbox path="checkRazonSocial" id="checkRazonSocial" onclick="desmarcaCheckTodo(this);" tabindex="16"/></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td>
										</tr>
									</c:when>
									<c:otherwise>  <!-- *********************  USUARIOS INTERNOS  *************************-->
										<c:choose>
											
											<c:when test="${perfil eq '1' or perfil eq '5'}">  <!--  ******************* USUARIOS PERFIL 1 y 5 INTERNO ******************** -->
												<tr align="left">
													<td class="literal" >1-N&uacute;mero fase </td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkFase" id="checkFase" onclick="desmarcaCheckTodo(this);" tabindex="1"/></td>
													<td class="literal" >17-Prima base </td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkPrimaBasN" id="checkPrimaBasN" onclick="desmarcaCheckTodo(this);" tabindex="17"/></td>
													<td class="literal" >22-Prima base</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkPrimaBasR" id="checkPrimaBasR" onclick="desmarcaCheckTodo(this);" tabindex="22"/></td>
													<td class="literal" >27-Prima base </td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkPrimaBasT" id="checkPrimaBasT" onclick="desmarcaCheckTodo(this);" tabindex="27"/></td>
													<td class="literal" style="background-color:#e5e5e5">37-Cód. Tram.</td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkCodTram" id="checkCodTram" onclick="desmarcaCheckTodo(this);" tabindex="37"/></td>
												</tr>
												<tr align="left">	
													<td class="literal" >2-Entidad med.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkEntidadMed" id="checkEntidadMed" onclick="desmarcaCheckTodo(this);" tabindex="2"/></td>
													<td class="literal" >18-G.G Ent.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkGGEntN" id="checkGGEntN" onclick="desmarcaCheckTodo(this);" tabindex="18"/></td>
													<td class="literal" >23-G.G Ent.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkGGEntR" id="checkGGEntR" onclick="desmarcaCheckTodo(this);" tabindex="23"/></td>
													<td class="literal" >28-G.G Ent.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkGGEntT" id="checkGGEntT" onclick="desmarcaCheckTodo(this);" tabindex="28"/></td>
													<td class="literal" style="background-color:#e5e5e5">38-Tipo Tram.</td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkTipoTram" id="checkTipoTram" onclick="desmarcaCheckTodo(this);" tabindex="38"/></td>
												</tr>
												<tr align="left">	
													<td class="literal" >3-Subentidad med.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkSubent" id="checkSubent" onclick="desmarcaCheckTodo(this);" tabindex="3"/></td>
													<td class="literal" >19-G.G Subent.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkGGSubEntN" id="checkGGSubEntN" onclick="desmarcaCheckTodo(this);" tabindex="19"/></td>
													<td class="literal" >24-G.G Subent.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkGGSubEntR" id="checkGGSubEntR" onclick="desmarcaCheckTodo(this);" tabindex="24"/></td>
													<td class="literal" >29-G.G Subent.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkGGSubEntT" id="checkGGSubEntT" onclick="desmarcaCheckTodo(this);" tabindex="29"/></td>
													<td class="literal" >39-Importe aplicado Tram.</td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkImpApliTram" id="checkImpApliTram" onclick="desmarcaCheckTodo(this);" tabindex="39"/></td>
												</tr>
												<tr align="left">	
													<td class="literal" style="background-color:#e5e5e5">4-Entidad</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkEntidad" id="checkEntidad" onclick="desmarcaCheckTodo(this);" tabindex="5"/></td>
													<td class="literal" >20-Comision Ent. </td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkComEntN" id="checkComEntN" onclick="desmarcaCheckTodo(this);" tabindex="20"/></td>
													<td class="literal" >25-Comision Ent. </td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkComEntR" id="checkComEntR" onclick="desmarcaCheckTodo(this);" tabindex="25"/></td>
													<td class="literal" >30-Comision Ent. </td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkComEntT" id="checkComEntT" onclick="desmarcaCheckTodo(this);" tabindex="30"/></td>
													<td class="literal" >40-Importe sin reducc. Tram.</td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkImpSinReduccTram" id="checkImpSinReduccTram" onclick="desmarcaCheckTodo(this);" tabindex="40"/></td>
												</tr>
												<tr align="left">	
													<td class="literal" style="background-color:#e5e5e5">5-Oficina</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkOficina" id="checkOficina" onclick="desmarcaCheckTodo(this);" tabindex="5"/></td>
													<td class="literal" >21-Comision Subent.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkComSubEntN" id="checkComSubEntN" onclick="desmarcaCheckTodo(this);" tabindex="21"/></td>
													<td class="literal" >26-Comision Subent.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkComSubEntR" id="checkComSubEntR" onclick="desmarcaCheckTodo(this);" tabindex="26"/></td>
													<td class="literal" >31-Comision Subent.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkComSubEntT" id="checkComSubEntT" onclick="desmarcaCheckTodo(this);" tabindex="31"/></td>
													<td class="literal" style="background-color:#e5e5e5">41-Cód. Calidad </td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkcodCalidad" id="checkcodCalidad" onclick="desmarcaCheckTodo(this);" tabindex="41"/></td>
												</tr>
												<tr align="left">
													<td class="literal" >6-Plan</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkPlan" id="checkPlan" onclick="desmarcaCheckTodo(this);" tabindex="6"/></td>	
													<td></td><td></td>
													<td></td><td></td>
													<td class="literal" style="background-color:#91FFA2">32-Gasto Pendiente</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkGasPenT" id="checkGasPenT" onclick="desmarcaCheckTodo(this);" tabindex="32"/></td>
													<td class="literal" style="background-color:#e5e5e5">42-Tipo Calidad </td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkTipoCalidad" id="checkTipoCalidad" onclick="desmarcaCheckTodo(this);" tabindex="42"/></td>
												</tr>
												<tr align="left">
													<td class="literal" >7-L&iacute;nea</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkLinea" id="checkLinea" onclick="desmarcaCheckTodo(this);" tabindex="7"/></td>
													<td></td><td></td>
													<td></td><td></td>
													<td class="literal" style="background-color:#91FFA2">33-Total Liquidación</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkTotLiquiT" id="checkTotLiquiT" onclick="desmarcaCheckTodo(this);" tabindex="33"/></td>
													<td class="literal" >43-Importe aplicado Calidad </td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkImpApliCal" id="checkImpApliCal" onclick="desmarcaCheckTodo(this);" tabindex="43"/></td>
												</tr>
												<tr align="left">
													<td class="literal" >8-Colectivo</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkColectivo" id="checkColectivo" onclick="desmarcaCheckTodo(this);"tabindex="8"/></td>	
													<td></td><td></td>
													<td></td><td></td>
													<td class="literal" style="background-color:#91FFA2">34-Total Entidad</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkTotEntT" id="checkTotEntT" onclick="desmarcaCheckTodo(this);" tabindex="34"/></td>
													<td class="literal" >44-Importe sin reducc. Calidad </td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkImpSinReduccCal" id="checkImpSinReduccCal" onclick="desmarcaCheckTodo(this);" tabindex="44"/></td>
												</tr>
												<tr align="left">
													<td class="literal" >9-Fecha aceptaci&oacute;n</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkFechaAcep" id="checkFechaAcep" onclick="desmarcaCheckTodo(this);" tabindex="9"/></td>	
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td class="literal" style="background-color:#91FFA2">35-Total Subentidad</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkTotSubentT" id="checkTotSubentT" onclick="desmarcaCheckTodo(this);" tabindex="35"/></td>
													<td class="literal" >45-Total Tram + Calidad </td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkTotTramCal" id="checkTotTramCal" onclick="desmarcaCheckTodo(this);" tabindex="45"/></td>
													
												</tr>
												<tr align="left">
													<td class="literal" style="background-color:#91FFA2">10-Tomador</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkTomador" id="checkTomador" onclick="desmarcaCheckTodo(this);" tabindex="10"/></td>	
													<td></td>
													<td></td>
													<td></td>
													<td></td>	
													<td class="literal" >36-Coste total</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkCosteTotalT" id="checkCosteTotalT" onclick="desmarcaCheckTodo(this);" tabindex="36"/></td>
												</tr>
												<tr align="left">
													<td class="literal" style="background-color:#e5e5e5">11-Ref. p&oacute;liza</td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkReferencia" id="checkReferencia" onclick="desmarcaCheckTodo(this);" tabindex="11"/></td>    
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
												</tr>
												<tr align="left">
												    <td class="literal" style="background-color:#e5e5e5">12- NIF asegurado</td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkNif" id="checkNif" onclick="desmarcaCheckTodo(this);" tabindex="12"/></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td><td></td>
												</tr>
												<tr align="left">
													<td class="literal" style="background-color:#e5e5e5">13-Nombre asegurado</td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkNombre" id="checkNombre" onclick="desmarcaCheckTodo(this);" tabindex="13"/></td>	
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td><td></td>
												</tr>
												<tr align="left">
													<td class="literal" style="background-color:#e5e5e5">14-Apellido 1</td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkApe1" id="checkApe1" onclick="desmarcaCheckTodo(this);" tabindex="14"/></td>	
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
												</tr>
												<tr align="left">
													<td class="literal" style="background-color:#e5e5e5">15-Apellido 2</td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkApe2" id="checkApe2" onclick="desmarcaCheckTodo(this);" tabindex="15"/></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>	
												</tr>
												<tr align="left">
													<td class="literal" style="background-color:#e5e5e5">16-Raz&oacute;n social</td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkRazonSocial" id="checkRazonSocial" onclick="desmarcaCheckTodo(this);" tabindex="16"/></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>	
												</tr>
											</c:when>
											<c:otherwise>  <!--  USUARIOS PERFIL 0 INTERNO -->
												<tr align="left">
													<td class="literal" >1-N&uacute;mero fase </td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkFase" id="checkFase" onclick="desmarcaCheckTodo(this);" tabindex="1"/></td>
													<td class="literal" >17-Prima base </td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkPrimaBasN" id="checkPrimaBasN" onclick="desmarcaCheckTodo(this);" tabindex="17"/></td>
													<td class="literal" >25-Prima base</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkPrimaBasR" id="checkPrimaBasR" onclick="desmarcaCheckTodo(this);" tabindex="25"/></td>
													<td class="literal" >33-Prima base </td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkPrimaBasT" id="checkPrimaBasT" onclick="desmarcaCheckTodo(this);" tabindex="33"/></td>
													<td class="literal" style="background-color:#e5e5e5">47-Cód. Tram.</td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkCodTram" id="checkCodTram" onclick="desmarcaCheckTodo(this);" tabindex="47"/></td>
												</tr>
												<tr align="left">	
													<td class="literal" >2-Entidad med.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkEntidadMed" id="checkEntidadMed" onclick="desmarcaCheckTodo(this);" tabindex="2"/></td>
													<td class="literal" >18-G.G Ent.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkGGEntN" id="checkGGEntN" onclick="desmarcaCheckTodo(this);" tabindex="18"/></td>
													<td class="literal" >26-G.G Ent.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkGGEntR" id="checkGGEntR" onclick="desmarcaCheckTodo(this);" tabindex="26"/></td>
													<td class="literal" >34-G.G Ent.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkGGEntT" id="checkGGEntT" onclick="desmarcaCheckTodo(this);" tabindex="34"/></td>
													<td class="literal" style="background-color:#e5e5e5">48-Tipo Tram.</td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkTipoTram" id="checkTipoTram" onclick="desmarcaCheckTodo(this);" tabindex="48"/></td>
												</tr>
												<tr align="left">	
													<td class="literal" >3-Subentidad med.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkSubent" id="checkSubent" onclick="desmarcaCheckTodo(this);" tabindex="3"/></td>
													<td class="literal" >19-G.G Subent.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkGGSubEntN" id="checkGGSubEntN" onclick="desmarcaCheckTodo(this);" tabindex="19"/></td>
													<td class="literal" >27-G.G Subent.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkGGSubEntR" id="checkGGSubEntR" onclick="desmarcaCheckTodo(this);" tabindex="27"/></td>
													<td class="literal" >35-G.G Subent.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkGGSubEntT" id="checkGGSubEntT" onclick="desmarcaCheckTodo(this);" tabindex="35"/></td>
													<td class="literal" >49-Importe aplicado Tram.</td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkImpApliTram" id="checkImpApliTram" onclick="desmarcaCheckTodo(this);" tabindex="49"/></td>
												</tr>
												<tr align="left">	
													<td class="literal" style="background-color:#e5e5e5">4-Entidad</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkEntidad" id="checkEntidad" onclick="desmarcaCheckTodo(this);" tabindex="5"/></td>
													<td class="literal" >20-Comision Ent. </td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkComEntN" id="checkComEntN" onclick="desmarcaCheckTodo(this);" tabindex="20"/></td>
													<td class="literal" >28-Comision Ent. </td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkComEntR" id="checkComEntR" onclick="desmarcaCheckTodo(this);" tabindex="28"/></td>
													<td class="literal" >36-Comision Ent. </td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkComEntT" id="checkComEntT" onclick="desmarcaCheckTodo(this);" tabindex="36"/></td>
													<td class="literal" >50-Importe sin reducc. Tram.</td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkImpSinReduccTram" id="checkImpSinReduccTram" onclick="desmarcaCheckTodo(this);" tabindex="50"/></td>
												</tr>
												<tr align="left">
													<td class="literal" style="background-color:#e5e5e5">5-Oficina</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkOficina" id="checkOficina" onclick="desmarcaCheckTodo(this);" tabindex="5"/></td>	
													<td class="literal" >21-Comision Subent.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkComSubEntN" id="checkComSubEntN" onclick="desmarcaCheckTodo(this);" tabindex="21"/></td>
													<td class="literal" >29-Comision Subent.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkComSubEntR" id="checkComSubEntR" onclick="desmarcaCheckTodo(this);" tabindex="29"/></td>
													<td class="literal" >37-Comision Subent.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkComSubEntT" id="checkComSubEntT" onclick="desmarcaCheckTodo(this);" tabindex="37"/></td>
													<td class="literal" style="background-color:#e5e5e5">51-Cód. Calidad </td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkcodCalidad" id="checkcodCalidad" onclick="desmarcaCheckTodo(this);" tabindex="51"/></td>
												</tr>
												<tr align="left">
													<td class="literal" >6-Plan</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkPlan" id="checkPlan" onclick="desmarcaCheckTodo(this);" tabindex="6"/></td>	
													<td class="literal" >22-G.G. Agro.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkGGAgroN" id="checkGGAgroN" onclick="desmarcaCheckTodo(this);" tabindex="22"/></td>
													<td class="literal" >30-G.G. Agro.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkGGAgroR" id="checkGGAgroR" onclick="desmarcaCheckTodo(this);" tabindex="30"/></td>
													<td class="literal" >38-G.G. Agro.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkGGAgroT" id="checkGGAgroT" onclick="desmarcaCheckTodo(this);" tabindex="38"/></td>
													<td class="literal" style="background-color:#e5e5e5">52-Tipo Calidad </td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkTipoCalidad" id="checkTipoCalidad" onclick="desmarcaCheckTodo(this);" tabindex="52"/></td>
												</tr>
												<tr align="left">
													<td class="literal" >7-L&iacute;nea</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkLinea" id="checkLinea" onclick="desmarcaCheckTodo(this);" tabindex="7"/></td>
													<td class="literal" >23-G.G. RGA.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkGGARgaN" id="checkGGARgaN" onclick="desmarcaCheckTodo(this);" tabindex="23"/></td>
													<td class="literal" >31-G.G. RGA.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkGGARgaR" id="checkGGARgaR" onclick="desmarcaCheckTodo(this);" tabindex="31"/></td>
													<td class="literal" >39-G.G. RGA.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkGGARgaT" id="checkGGARgaT" onclick="desmarcaCheckTodo(this);" tabindex="39"/></td>
													<td class="literal" >53-Importe aplicado Calidad </td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkImpApliCal" id="checkImpApliCal" onclick="desmarcaCheckTodo(this);" tabindex="53"/></td>
												</tr>
												<tr align="left">
													<td class="literal" >8-Colectivo</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkColectivo" id="checkColectivo" onclick="desmarcaCheckTodo(this);"tabindex="8"/></td>	
													<td class="literal" >24-Total Agro.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkTotAgroN" id="checkTotAgroN" onclick="desmarcaCheckTodo(this);" tabindex="24"/></td>
													<td class="literal" >32-Total Agro.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkTotAgroR" id="checkTotAgroR" onclick="desmarcaCheckTodo(this);" tabindex="32"/></td>
													<td class="literal" >40-Total Agro.</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkTotAgroT" id="checkTotAgroT" onclick="desmarcaCheckTodo(this);" tabindex="40"/></td>
													<td class="literal" >54-Importe sin reducc. Calidad </td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkImpSinReduccCal" id="checkImpSinReduccCal" onclick="desmarcaCheckTodo(this);" tabindex="54"/></td>
												</tr>
												<tr align="left">
													<td class="literal" >9-Fecha aceptaci&oacute;n</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkFechaAcep" id="checkFechaAcep" onclick="desmarcaCheckTodo(this);" tabindex="9"/></td>	
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td class="literal" style="background-color:#91FFA2">41-Gasto Pendiente</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkGasPenT" id="checkGasPenT" onclick="desmarcaCheckTodo(this);" tabindex="41"/></td>
													<td class="literal" >55-Total Tram + Calidad </td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkTotTramCal" id="checkTotTramCal" onclick="desmarcaCheckTodo(this);" tabindex="55"/></td>
													
												</tr>
												<tr align="left">
													<td class="literal" style="background-color:#91FFA2">10-Tomador</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkTomador" id="checkTomador" onclick="desmarcaCheckTodo(this);" tabindex="10"/></td>	
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td class="literal" style="background-color:#91FFA2">42-Gasto pagado de Agro</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkGasPagT" id="checkGasPagT" onclick="desmarcaCheckTodo(this);" tabindex="42"/></td>									
													
												</tr>
												<tr align="left">
													<td class="literal" style="background-color:#e5e5e5">11-Ref. p&oacute;liza</td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkReferencia" id="checkReferencia" onclick="desmarcaCheckTodo(this);" tabindex="11"/></td>    
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td class="literal" style="background-color:#91FFA2">43-Total Liquidación</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkTotLiquiT" id="checkTotLiquiT" onclick="desmarcaCheckTodo(this);" tabindex="43"/></td>
													
												</tr>
												<tr align="left">
												    <td class="literal" style="background-color:#e5e5e5">12- NIF asegurado</td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkNif" id="checkNif" onclick="desmarcaCheckTodo(this);" tabindex="12"/></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td class="literal" style="background-color:#91FFA2">44-Total Entidad</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkTotEntT" id="checkTotEntT" onclick="desmarcaCheckTodo(this);" tabindex="44"/></td>
													
												</tr>
												<tr align="left">
													<td class="literal" style="background-color:#e5e5e5">13-Nombre asegurado</td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkNombre" id="checkNombre" onclick="desmarcaCheckTodo(this);" tabindex="13"/></td>	
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td class="literal" style="background-color:#91FFA2">45-Total Subentidad</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkTotSubentT" id="checkTotSubentT" onclick="desmarcaCheckTodo(this);" tabindex="45"/></td>
													
												</tr>
												<tr align="left">
													<td class="literal" style="background-color:#e5e5e5">14-Apellido 1</td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkApe1" id="checkApe1" onclick="desmarcaCheckTodo(this);" tabindex="14"/></td>	
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td class="literal" >46-Coste total</td>
													<td  style="background-color:#e5e5e5"><form:checkbox path="checkCosteTotalT" id="checkCosteTotalT" onclick="desmarcaCheckTodo(this);" tabindex="46"/></td>
												</tr>
												<tr align="left">
													<td class="literal" style="background-color:#e5e5e5">15-Apellido 2</td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkApe2" id="checkApe2" onclick="desmarcaCheckTodo(this);" tabindex="15"/></td>
	
												</tr>
												<tr align="left">												
													<td class="literal" style="background-color:#e5e5e5">16-Raz&oacute;n social</td>
													<td style="background-color:#e5e5e5"><form:checkbox path="checkRazonSocial" id="checkRazonSocial" onclick="desmarcaCheckTodo(this);" tabindex="16"/></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
												</tr>													
											</c:otherwise>
										</c:choose>	
									</c:otherwise>
								</c:choose>			
								
							</table>			
					 </fieldset>
					 
					 	
			</form:form>  			

		</div>
	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
</body>
</html>