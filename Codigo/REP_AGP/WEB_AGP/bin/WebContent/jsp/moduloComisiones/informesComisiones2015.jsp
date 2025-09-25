<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Consultas de Comisiones 2015+</title>
		
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
		<script type="text/javascript" src="jsp/moduloComisiones/informesComisiones2015.js" ></script>
		
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
		<div class="conten" style="padding:3px;width:100%" >
		<p class="titulopag" align="left">Consultas de Comisiones 2015+</p>	
		
			<form:form name="main3" id="main3" action="informesComisiones2015.run" method="post" commandName="informeComisionesBean">
				
				<input type="hidden" name="idInforme" id="idInforme" value="${idInforme}"/>
				<input type="hidden" id="method" name="method" value="" />
				<input type="hidden" id="lstCodEntidades" name="lstCodEntidades" value="${lstCodEntidades}" />
				<input type="hidden" id="perfil" value="${perfil}" />
				<input type="hidden" id="entMed" value="${entMed}" />
				<input type="hidden" id="subEntMed" value="${subEntMed}" />
				<input type="hidden" id="esExterno" value="${esExterno}" />
				
				<form:hidden path="datosDe" id="datosDe"/>

								
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>	

					 <fieldset style="width:85%;">
						<legend class="literal">Condiciones en el Informe</legend>
							<table align="center">
								<tr>
									<td class="literal">N&uacute;mero fase:</td>
									<td><form:select path="condiFase" cssClass="dato" id="condiFase" cssStyle="width:100" >
											<form:option value=""></form:option>
											<form:option value="5">Igual</form:option>
											<form:option value="3">Contenido en</form:option>
										</form:select></td>
									<td><form:input path="fase" id="fase" cssClass="dato" size="30"/></td>
									<td class="literal">Colectivo:</td>
									<td><form:select path="condiRefColectivo" cssClass="dato" id="condiRefColectivo" cssStyle="width:100" >
											<form:option value="" ></form:option>
											<form:option value="5">Igual</form:option>
											<form:option value="3">Contenido en</form:option>
										</form:select></td>
									<td><form:input path="refColectivo" id="refColectivo" cssClass="dato" size="30"/></td>
								</tr>
								<tr>
									<td class="literal">L&iacute;nea:</td>
									<td><form:select path="condiLinea" cssClass="dato" id="condiLinea" cssStyle="width:100" >
											<form:option value="" ></form:option>
											<form:option value="5">Igual</form:option>
											<form:option value="3">Contenido en</form:option>
										</form:select></td>
									<td><form:input path="linea" id="linea" cssClass="dato" size="30"/></td>
									<td class="literal">Plan:</td>
									<td><form:select path="condiPlan" cssClass="dato" id="condiPlan" cssStyle="width:100" >
											<form:option value="" ></form:option>
											<form:option value="5">Igual</form:option>
											<form:option value="3">Contenido en</form:option>
										</form:select></td>
									<td><form:input path="plan" id="plan" cssClass="dato" size="30"/></td>
								</tr>

								<tr>
									<td class="literal">Entidad:</td>
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

									<td class="literal">Oficina:</td>
									<td><form:select path="condiOficina" cssClass="dato" id="condiOficina" cssStyle="width:100" >
											<form:option value="" ></form:option>
											<form:option value="5">Igual</form:option>
											<form:option value="3">Contenido en</form:option>
										</form:select></td>
									<td><form:input path="oficina" id="oficina" cssClass="dato" size="30"/></td>
								</tr>

								<tr>
									<td class="literal">Entidad med.:</td>
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
									<td class="literal">Subentidad med.:</td>
									<td>
										<form:select path="condiSubent" cssClass="dato" id="condiSubent" cssStyle="width:100" >
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
										</form:select>
									</td>
									<td><form:input path="subent" id="subent" cssClass="dato" size="30"/></td>
								</tr>
								<tr>
									<td class="literal" >Ref. P&oacute;liza:</td>
									<td><form:select path="condiRefPoliza" cssClass="dato" id="condiRefPoliza" cssStyle="width:100">
											<form:option value="" ></form:option>
											<form:option value="5">Igual</form:option>
											<form:option value="3">Contenido en</form:option>
										</form:select></td>
									<td><form:input path="refPoliza" id="refPoliza" cssClass="dato" size="30"/></td>
									<td class="literal">Nif. aseg.:</td>
									<td><form:select path="condiNifAseg" cssClass="dato" id="condiNifAseg" cssStyle="width:100">
											<form:option value="" ></form:option>
											<form:option value="5">Igual</form:option>
											<form:option value="3">Contenido en</form:option>
										</form:select></td>
									<td><form:input path="nifAseg" id="nifAseg" cssClass="dato" size="30"/></td>
								</tr>
								<tr>
									<td class="literal">Fecha aceptaci&oacute;n: </td>
									<td><form:select path="condiFechaAcep" cssClass="dato" id="condiFechaAcep" cssStyle="width:100" >
											<form:option value="" ></form:option>
											<form:option value="5">Igual</form:option>
											<form:option value="4">Entre</form:option>											
										</form:select></td>
									<td><form:input path="fechaAcep" id="fechaAcep" cssClass="dato" size="30"/></td>
									<td colspan="3">&nbsp;</td>
								</tr>
							</table>
					 </fieldset>


					 <fieldset style="width:95%;">
						<legend class="literal">Campos a mostrar en el Informe</legend>

							<table width="95%" align="center" border="0" >
								<tr>
									<td class="literal">Formato</td>
									<td class="literal">
										<form:radiobutton cssClass="literal" id="formato" 		path="formato" value="3" />EXCEL
										<form:radiobutton cssClass="literal" id="formato" 		path="formato" value="1" />HTML
										<form:radiobutton cssClass="literal" id="formatoPDF" 	path="formato" value="2" />PDF
										<form:radiobutton cssClass="literal" id="formato" 		path="formato" value="4" />TXT
									</td>
									<td class="literal" align="right">Marcar todo&nbsp;<input type="checkbox" name="checkTodo" id="checkTodo"  class="dato" onclick="this.checked  ? marcar_todos() : desmarcar_todos() "/></td>
								</tr>
							</table>

							<table width="95%" align="center" border="1" >
								<tr>
									<td class="literal" colspan="2" align="center">Datos generales</td>
									<td class="literal" colspan="2" align="center">Nuevo</td>
									<td class="literal" colspan="2" align="center">Regularización</td>
									<td class="literal" colspan="2" align="center">Total (N + R)</td>
									<td class="literal" colspan="2" align="center">Reglamento</td>
								</tr>
								
<c:choose>
	<c:when test="${perfil eq '0'}">
	
<tr align="left">
	<td class="literal">1-N&uacute;mero fase </td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkFase" id="checkFase" onclick="desmarcaCheckTodo(this);" tabindex="1"/></td>
	<td class="literal">17-Prima Comercial Neta </td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkNuevoPrimaComNeta" id="checkNuevoPrimaComNeta" onclick="desmarcaCheckTodo(this);" tabindex="17"/></td>
	<td class="literal">23-Prima Comercial Neta</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReguPrimaComNeta" id="checkReguPrimaComNeta" onclick="desmarcaCheckTodo(this);" tabindex="23"/></td>
	<td class="literal">29-Prima Comercial Neta </td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkTotPrimaComNeta" id="checkTotPrimaComNeta" onclick="desmarcaCheckTodo(this);" tabindex="29"/></td>
	<td class="literal">45-Cód. Tram.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaCodTram" id="checkReglaCodTram" onclick="desmarcaCheckTodo(this);" tabindex="45"/></td>
</tr>
<tr align="left">	
	<td class="literal">2-Entidad med.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkEntidadMed" id="checkEntidadMed" onclick="desmarcaCheckTodo(this);" tabindex="2"/></td>
	<td class="literal">18-Gastos Administraci&oacute;n</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkNuevoGastosAdmin" id="checkNuevoGastosAdmin" onclick="desmarcaCheckTodo(this);" tabindex="18"/></td>
	<td class="literal">24-Gastos administraci&oacute;n</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReguGastosAdmin" id="checkReguGastosAdmin" onclick="desmarcaCheckTodo(this);" tabindex="24"/></td>
	<td class="literal">30-Gastos administraci&oacute;n</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkTotGastosAdmin" id="checkTotGastosAdmin" onclick="desmarcaCheckTodo(this);" tabindex="30"/></td>
	<td class="literal">46-Tipo Tram.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaTipoTram" id="checkReglaTipoTram" onclick="desmarcaCheckTodo(this);" tabindex="46"/></td>
</tr>
<tr align="left">	
	<td class="literal">3-Subentidad med.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkSubent" id="checkSubent" onclick="desmarcaCheckTodo(this);" tabindex="3"/></td>
	<td class="literal">19-Gastos Adquisici&oacute;n</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkNuevoGastosAdq" id="checkNuevoGastosAdq" onclick="desmarcaCheckTodo(this);" tabindex="19"/></td>
	<td class="literal">25-Gastos Adquisici&oacute;n</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReguGastosAdq" id="checkReguGastosAdq" onclick="desmarcaCheckTodo(this);" tabindex="25"/></td>
	<td class="literal">31-Gastos Adquisici&oacute;n</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkTotGastosAdq" id="checkTotGastosAdq" onclick="desmarcaCheckTodo(this);" tabindex="31"/></td>
	<td class="literal">47-Importe aplicado Tram.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaImpApliTram" id="checkReglaImpApliTram" onclick="desmarcaCheckTodo(this);" tabindex="47"/></td>
</tr>
<tr align="left">	
	<td class="literal">4-Entidad</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkEntidad" id="checkEntidad" onclick="desmarcaCheckTodo(this);" tabindex="4"/></td>
	<td class="literal">20-Comisi&oacute;n Ent. </td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkNuevoComisionEnt" id="checkNuevoComisionEnt" onclick="desmarcaCheckTodo(this);" tabindex="20"/></td>
	<td class="literal">26-Comisi&oacute;n Ent. </td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReguComisionEnt" id="checkReguComisionEnt" onclick="desmarcaCheckTodo(this);" tabindex="26"/></td>
	<td class="literal">32-Comisi&oacute;n Ent. </td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkTotComisionEnt" id="checkTotComisionEnt" onclick="desmarcaCheckTodo(this);" tabindex="32"/></td>
	<td class="literal">48-Importe sin reducc. Tram.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaImpSinReduccTram" id="checkReglaImpSinReduccTram" onclick="desmarcaCheckTodo(this);" tabindex="48"/></td>
</tr>
<tr align="left">
	<td class="literal">5-Oficina</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkOficina" id="checkOficina" onclick="desmarcaCheckTodo(this);" tabindex="5"/></td>	
	<td class="literal">21-Comision E-S Med.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkNuevoComisionSubent" id="checkNuevoComisionSubent" onclick="desmarcaCheckTodo(this);" tabindex="21"/></td>
	<td class="literal">27-Comision E-S Med.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReguComisionSubent" id="checkReguComisionSubent" onclick="desmarcaCheckTodo(this);" tabindex="27"/></td>
	<td class="literal" >33-Comision E-S Med.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkTotComisionSubent" id="checkTotComisionSubent" onclick="desmarcaCheckTodo(this);" tabindex="33"/></td>
	<td class="literal">49-Cód. Calidad </td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaCodCalidad" id="checkReglaCodCalidad" onclick="desmarcaCheckTodo(this);" tabindex="49"/></td>
</tr>
<tr align="left">
	<td class="literal">6-Plan</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkPlan" id="checkPlan" onclick="desmarcaCheckTodo(this);" tabindex="6"/></td>	
	<td class="literal">22-Total gastos</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkNuevoTotalGastos" id="checkNuevoTotalGastos" onclick="desmarcaCheckTodo(this);" tabindex="22"/></td>
	<td class="literal">28-Total gastos</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReguTotalGastos" id="checkReguTotalGastos" onclick="desmarcaCheckTodo(this);" tabindex="28"/></td>
	<td class="literal">34-Total gastos</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkTotTotalGastos" id="checkTotTotalGastos" onclick="desmarcaCheckTodo(this);" tabindex="34"/></td>
	<td class="literal">50-Tipo Calidad </td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaTipoCalidad" id="checkReglaTipoCalidad" onclick="desmarcaCheckTodo(this);" tabindex="50"/></td>
</tr>
<tr align="left">
	<td class="literal" >7-L&iacute;nea</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkLinea" id="checkLinea" onclick="desmarcaCheckTodo(this);" tabindex="7"/></td>

	<td class="literal" colspan="4" rowspan="10">&nbsp;</td>
	
	<td class="literal" >35-Gastos Administración Abon.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkTotGastosAdminAbon" id="checkTotGastosAdminAbon" onclick="desmarcaCheckTodo(this);" tabindex="35"/></td>
	<td class="literal" >51-Importe aplicado Calidad</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaImpApliCal" id="checkReglaImpApliCal" onclick="desmarcaCheckTodo(this);" tabindex="51"/></td>
</tr>
<tr align="left">
	<td class="literal" >8-Colectivo</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkRefColectivo" id="checkRefColectivo" onclick="desmarcaCheckTodo(this);"tabindex="8"/></td>	

	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->

	<td class="literal" >36-Gastos Adquisición Abon.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkTotGastosAdqAbon" id="checkTotGastosAdqAbon" onclick="desmarcaCheckTodo(this);" tabindex="36"/></td>
	<td class="literal" >52-Importe sin reducc. Calidad </td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaImpSinReduccCal" id="checkReglaImpSinReduccCal" onclick="desmarcaCheckTodo(this);" tabindex="52"/></td>
</tr>
<tr align="left">
	<td class="literal">9-Fecha aceptaci&oacute;n</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkFechaAcep" id="checkFechaAcep" onclick="desmarcaCheckTodo(this);" tabindex="9"/></td>	
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<td class="literal">37-Comisión Ent. Abon.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkTotComisionEntAbon" id="checkTotComisionEntAbon" onclick="desmarcaCheckTodo(this);" tabindex="37"/></td>
	<td class="literal">53-Total Tram+Calid</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaTotalTramCal" id="checkReglaTotalTramCal" onclick="desmarcaCheckTodo(this);" tabindex="53"/></td>
	
</tr>
<tr align="left">
	<td class="literal">10-Tomador</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkTomador" id="checkTomador" onclick="desmarcaCheckTodo(this);" tabindex="10"/></td>	
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<td class="literal">38-Comisión E-S Med. Abon.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkTotComisionSubentAbon" id="checkTotComisionSubentAbon" onclick="desmarcaCheckTodo(this);" tabindex="38"/></td>
	
	<!-- Columna: Reglamento -->									
	
</tr>
<tr align="left">
	<td class="literal">11-Ref. p&oacute;liza</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkRefPoliza" id="checkRefPoliza" onclick="desmarcaCheckTodo(this);" tabindex="11"/></td>    
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<td class="literal">39-Gastos Administración Pdte.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkTotGastosAdminPdte" id="checkTotGastosAdminPdte" onclick="desmarcaCheckTodo(this);" tabindex="39"/></td>
	
	<!-- Columna: Reglamento -->
	
</tr>
<tr align="left">
    <td class="literal">12- NIF asegurado</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkNifAseg" id="checkNifAseg" onclick="desmarcaCheckTodo(this);" tabindex="12"/></td>
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<td class="literal">40-Gastos Adquisición Pdte.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkTotGastosAdqPdte" id="checkTotGastosAdqPdte" onclick="desmarcaCheckTodo(this);" tabindex="40"/></td>
	
	<!-- Columna: Reglamento -->
	
</tr>
<tr align="left">
	<td class="literal">13-Nombre asegurado</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkNombreAseg" id="checkNombreAseg" onclick="desmarcaCheckTodo(this);" tabindex="13"/></td>	
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<td class="literal">41-Comisión Ent. Pdte.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkTotComisionEntPdte" id="checkTotComisionEntPdte" onclick="desmarcaCheckTodo(this);" tabindex="41"/></td>
	
	<!-- Columna: Reglamento -->
	
</tr>
<tr align="left">
	<td class="literal">14-Apellido 1</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkApe1Aseg" id="checkApe1Aseg" onclick="desmarcaCheckTodo(this);" tabindex="14"/></td>	
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<td class="literal" >42-Comisión E-S Med. Pdte.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkTotComisionSubentPdte" id="checkTotComisionSubentPdte" onclick="desmarcaCheckTodo(this);" tabindex="42"/></td>
	
	<!-- Columna: Reglamento -->
</tr>
<tr align="left">
	<td class="literal">15-Apellido 2</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkApe2Aseg" id="checkApe2Aseg" onclick="desmarcaCheckTodo(this);" tabindex="15"/></td>

	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->

	<td class="literal">43-Total liquidación</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkTotTotalLiquidacion" id="checkTotTotalLiquidacion" onclick="desmarcaCheckTodo(this);" tabindex="43"/></td>
	
	<!-- Columna: Reglamento -->

</tr>
<tr align="left">												
	<td class="literal">16-Raz&oacute;n social</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkRazonSocialAseg" id="checkRazonSocialAseg" onclick="desmarcaCheckTodo(this);" tabindex="16"/></td>
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<td class="literal">44-Total liquidación RGA</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkTotTotalLiquidacionRga" id="checkTotTotalLiquidacionRga" onclick="desmarcaCheckTodo(this);" tabindex="44"/></td>
	
	<!-- Columna: Reglamento -->
</tr>			
	
	</c:when>
	<c:when test="${(perfil eq '1' && esExterno == 0) || perfil eq '5'}">
	
<tr align="left">
	<td class="literal">1-N&uacute;mero fase </td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkFase" id="checkFase" onclick="desmarcaCheckTodo(this);" tabindex="1"/></td>
	<td class="literal">17-Prima Comercial Neta </td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkNuevoPrimaComNeta" id="checkNuevoPrimaComNeta" onclick="desmarcaCheckTodo(this);" tabindex="17"/></td>
	<td class="literal">20-Prima Comercial Neta</td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkReguPrimaComNeta" id="checkReguPrimaComNeta" onclick="desmarcaCheckTodo(this);" tabindex="20"/></td>
	<td class="literal">23-Prima Comercial Neta </td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkTotPrimaComNeta" id="checkTotPrimaComNeta" onclick="desmarcaCheckTodo(this);" tabindex="23"/></td>
	<td class="literal">30-Cód. Tram.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaCodTram" id="checkReglaCodTram" onclick="desmarcaCheckTodo(this);" tabindex="30"/></td>
</tr>
<tr align="left">	
	<td class="literal">2-Entidad med.</td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkEntidadMed" id="checkEntidadMed" onclick="desmarcaCheckTodo(this);" tabindex="2"/></td>
	<td class="literal">18-Comisi&oacute;n Ent. </td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkNuevoComisionEnt" id="checkNuevoComisionEnt" onclick="desmarcaCheckTodo(this);" tabindex="18"/></td>
	<td class="literal">21-Comisi&oacute;n Ent. </td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkReguComisionEnt" id="checkReguComisionEnt" onclick="desmarcaCheckTodo(this);" tabindex="21"/></td>
	<td class="literal">24-Comisi&oacute;n Ent. </td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkTotComisionEnt" id="checkTotComisionEnt" onclick="desmarcaCheckTodo(this);" tabindex="24"/></td>
	<td class="literal">31-Tipo Tram.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaTipoTram" id="checkReglaTipoTram" onclick="desmarcaCheckTodo(this);" tabindex="31"/></td>
</tr>
<tr align="left">	
	<td class="literal">3-Subentidad med.</td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkSubent" id="checkSubent" onclick="desmarcaCheckTodo(this);" tabindex="3"/></td>
	<td class="literal">19-Comision E-S Med.</td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkNuevoComisionSubent" id="checkNuevoComisionSubent" onclick="desmarcaCheckTodo(this);" tabindex="19"/></td>
	<td class="literal">22-Comision E-S Med.</td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkReguComisionSubent" id="checkReguComisionSubent" onclick="desmarcaCheckTodo(this);" tabindex="22"/></td>
	<td class="literal">25-Comision E-S Med.</td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkTotComisionSubent" id="checkTotComisionSubent" onclick="desmarcaCheckTodo(this);" tabindex="25"/></td>
	<td class="literal">32-Importe aplicado Tram.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaImpApliTram" id="checkReglaImpApliTram" onclick="desmarcaCheckTodo(this);" tabindex="32"/></td>
</tr>
<tr align="left">	
	<td class="literal">4-Entidad</td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkEntidad" id="checkEntidad" onclick="desmarcaCheckTodo(this);" tabindex="4"/></td>
	
	<td class="literal" colspan="2" rowspan="14">&nbsp;</td>
	
	<td class="literal" colspan="2" rowspan="14">&nbsp;</td>
	
	<td class="literal">26-Comisión Ent. Abon.</td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkTotComisionEntAbon" id="checkTotComisionEntAbon" onclick="desmarcaCheckTodo(this);" tabindex="26"/></td>
	<td class="literal">33-Importe sin reducc. Tram.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaImpSinReduccTram" id="checkReglaImpSinReduccTram" onclick="desmarcaCheckTodo(this);" tabindex="33"/></td>
</tr>
<tr align="left">	
	<td class="literal">5-Oficina</td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkOficina" id="checkOficina" onclick="desmarcaCheckTodo(this);" tabindex="5"/></td>	
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<td class="literal">27-Comisión E-S Med. Abon.</td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkTotComisionSubentAbon" id="checkTotComisionSubentAbon" onclick="desmarcaCheckTodo(this);" tabindex="27"/></td>
	<td class="literal">34-Cód. Calidad </td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaCodCalidad" id="checkReglaCodCalidad" onclick="desmarcaCheckTodo(this);" tabindex="34"/></td>
</tr>
<tr align="left">	
	<td class="literal">6-Plan</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkPlan" id="checkPlan" onclick="desmarcaCheckTodo(this);" tabindex="6"/></td>	
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<td class="literal">28-Comisión Ent. Pdte.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkTotComisionEntPdte" id="checkTotComisionEntPdte" onclick="desmarcaCheckTodo(this);" tabindex="28"/></td>
	<td class="literal">35-Tipo Calidad </td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaTipoCalidad" id="checkReglaTipoCalidad" onclick="desmarcaCheckTodo(this);" tabindex="35"/></td>
</tr>
<tr align="left">
	<td class="literal" >7-L&iacute;nea</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkLinea" id="checkLinea" onclick="desmarcaCheckTodo(this);" tabindex="7"/></td>
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<td class="literal" >29-Comisión E-S Med. Pdte.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkTotComisionSubentPdte" id="checkTotComisionSubentPdte" onclick="desmarcaCheckTodo(this);" tabindex="29"/></td>
	<td class="literal" >36-Importe aplicado Calidad</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaImpApliCal" id="checkReglaImpApliCal" onclick="desmarcaCheckTodo(this);" tabindex="36"/></td>
</tr>
<tr align="left">
	<td class="literal" >8-Colectivo</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkRefColectivo" id="checkRefColectivo" onclick="desmarcaCheckTodo(this);"tabindex="8"/></td>	
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<td class="literal" colspan="2" rowspan="10">&nbsp;</td>
		
	<td class="literal" >37-Importe sin reducc. Calidad </td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaImpSinReduccCal" id="checkReglaImpSinReduccCal" onclick="desmarcaCheckTodo(this);" tabindex="37"/></td>
</tr>
<tr align="left">
	<td class="literal">9-Fecha aceptaci&oacute;n</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkFechaAcep" id="checkFechaAcep" onclick="desmarcaCheckTodo(this);" tabindex="9"/></td>	
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<!-- Columna: Total -->
	<td class="literal">38-Total Tram+Calid</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaTotalTramCal" id="checkReglaTotalTramCal" onclick="desmarcaCheckTodo(this);" tabindex="38"/></td>
</tr>
<tr align="left">
	<td class="literal">10-Tomador</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkTomador" id="checkTomador" onclick="desmarcaCheckTodo(this);" tabindex="10"/></td>
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<td class="literal" colspan="2" rowspan="8">&nbsp;</td>
	
	<!-- Columna: Reglamento -->
</tr>
<tr align="left">

	<!-- Columna: Datos generales -->
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<!-- Columna: Total -->
	
	<!-- Columna: Reglamento -->
</tr>
<tr align="left">
	<td class="literal">11-Ref. p&oacute;liza</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkRefPoliza" id="checkRefPoliza" onclick="desmarcaCheckTodo(this);" tabindex="11"/></td>    
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<!-- Columna: Total -->
	
	<!-- Columna: Reglamento -->
</tr>
<tr align="left">
    <td class="literal">12- NIF asegurado</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkNifAseg" id="checkNifAseg" onclick="desmarcaCheckTodo(this);" tabindex="12"/></td>
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<!-- Columna: Total -->
	
	<!-- Columna: Reglamento -->
</tr>
<tr align="left">
	<td class="literal">13-Nombre asegurado</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkNombreAseg" id="checkNombreAseg" onclick="desmarcaCheckTodo(this);" tabindex="13"/></td>	
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<!-- Columna: Total -->
	
	<!-- Columna: Reglamento -->
</tr>
<tr align="left">
	<td class="literal">14-Apellido 1</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkApe1Aseg" id="checkApe1Aseg" onclick="desmarcaCheckTodo(this);" tabindex="14"/></td>
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<!-- Columna: Total -->
	
	<!-- Columna: Reglamento -->
</tr>

<tr align="left">
	<td class="literal">15-Apellido 2</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkApe2Aseg" id="checkApe2Aseg" onclick="desmarcaCheckTodo(this);" tabindex="15"/></td>
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<!-- Columna: Total -->
	
	<!-- Columna: Reglamento -->
</tr>

<tr align="left">
	<td class="literal">16-Raz&oacute;n social</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkRazonSocialAseg" id="checkRazonSocialAseg" onclick="desmarcaCheckTodo(this);" tabindex="16"/></td>
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<!-- Columna: Total -->
	
	<!-- Columna: Reglamento -->
</tr>
	
	</c:when>
	<c:when test="${perfil eq '1' && esExterno ==1}">
	
<tr align="left">
	<td class="literal">1-N&uacute;mero fase </td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkFase" id="checkFase" onclick="desmarcaCheckTodo(this);" tabindex="1"/></td>
	<td class="literal">17-Prima Comercial Neta </td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkNuevoPrimaComNeta" id="checkNuevoPrimaComNeta" onclick="desmarcaCheckTodo(this);" tabindex="17"/></td>
	<td class="literal">19-Prima Comercial Neta</td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkReguPrimaComNeta" id="checkReguPrimaComNeta" onclick="desmarcaCheckTodo(this);" tabindex="19"/></td>
	<td class="literal">21-Prima Comercial Neta </td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkTotPrimaComNeta" id="checkTotPrimaComNeta" onclick="desmarcaCheckTodo(this);" tabindex="21"/></td>
	<td class="literal">25-Cód. Tram.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaCodTram" id="checkReglaCodTram" onclick="desmarcaCheckTodo(this);" tabindex="25"/></td>
</tr>
<tr align="left">	
	<td class="literal">2-Entidad med.</td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkEntidadMed" id="checkEntidadMed" onclick="desmarcaCheckTodo(this);" tabindex="2"/></td>
	<td class="literal">18-Comisi&oacute;n Ent. </td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkNuevoComisionEnt" id="checkNuevoComisionEnt" onclick="desmarcaCheckTodo(this);" tabindex="18"/></td>
	<td class="literal">20-Comisi&oacute;n Ent. </td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkReguComisionEnt" id="checkReguComisionEnt" onclick="desmarcaCheckTodo(this);" tabindex="20"/></td>
	<td class="literal">22-Comisi&oacute;n Ent. </td>
	<td  style="background-color:#e5e5e5"><form:checkbox path="checkTotComisionEnt" id="checkTotComisionEnt" onclick="desmarcaCheckTodo(this);" tabindex="22"/></td>
	<td class="literal">26-Tipo Tram.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaTipoTram" id="checkReglaTipoTram" onclick="desmarcaCheckTodo(this);" tabindex="26"/></td>
</tr>
<tr align="left">
	<td class="literal">3-Subentidad med.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkSubent" id="checkSubent" onclick="desmarcaCheckTodo(this);" tabindex="3"/></td>
	
	<td class="literal" colspan="2" rowspan="14">&nbsp;</td>
	
	<td class="literal" colspan="2" rowspan="14">&nbsp;</td>
	
	<td class="literal">23-Comisión E-S Med. Abon.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkTotComisionSubentAbon" id="checkTotComisionSubentAbon" onclick="desmarcaCheckTodo(this);" tabindex="23"/></td>
	<td class="literal">27-Importe aplicado Tram.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaImpApliTram" id="checkReglaImpApliTram" onclick="desmarcaCheckTodo(this);" tabindex="27"/></td>
</tr>
<tr align="left">
	<td class="literal">4-Entidad</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkEntidad" id="checkEntidad" onclick="desmarcaCheckTodo(this);" tabindex="4"/></td>
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<td class="literal" >24-Comisión E-S Med. Pdte.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkTotComisionSubentPdte" id="checkTotComisionSubentPdte" onclick="desmarcaCheckTodo(this);" tabindex="24"/></td>
	<td class="literal">28-Importe sin reducc. Tram.</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaImpSinReduccTram" id="checkReglaImpSinReduccTram" onclick="desmarcaCheckTodo(this);" tabindex="28"/></td>
</tr>
<tr align="left">
	<td class="literal">5-Oficina</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkOficina" id="checkOficina" onclick="desmarcaCheckTodo(this);" tabindex="5"/></td>
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<td class="literal" colspan="2" rowspan="12">&nbsp;</td>
	
	<td class="literal">29-Cód. Calidad </td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaCodCalidad" id="checkReglaCodCalidad" onclick="desmarcaCheckTodo(this);" tabindex="29"/></td>
</tr>
<tr align="left">
	<td class="literal">6-Plan</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkPlan" id="checkPlan" onclick="desmarcaCheckTodo(this);" tabindex="6"/></td>
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<!-- Columna: Total -->
	
	<td class="literal">30-Tipo Calidad </td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaTipoCalidad" id="checkReglaTipoCalidad" onclick="desmarcaCheckTodo(this);" tabindex="30"/></td>
</tr>
<tr align="left">
	<td class="literal" >7-L&iacute;nea</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkLinea" id="checkLinea" onclick="desmarcaCheckTodo(this);" tabindex="7"/></td>
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<!-- Columna: Total -->
	
	<td class="literal" >31-Importe aplicado Calidad</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaImpApliCal" id="checkReglaImpApliCal" onclick="desmarcaCheckTodo(this);" tabindex="31"/></td>
</tr>
<tr align="left">
	<td class="literal" >8-Colectivo</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkRefColectivo" id="checkRefColectivo" onclick="desmarcaCheckTodo(this);"tabindex="8"/></td>	
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<!-- Columna: Total -->
	
	<td class="literal" >32-Importe sin reducc. Calidad </td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaImpSinReduccCal" id="checkReglaImpSinReduccCal" onclick="desmarcaCheckTodo(this);" tabindex="32"/></td>
</tr>
<tr align="left">
	<td class="literal">9-Fecha aceptaci&oacute;n</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkFechaAcep" id="checkFechaAcep" onclick="desmarcaCheckTodo(this);" tabindex="9"/></td>
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<!-- Columna: Total -->
	
	<td class="literal">33-Total Tram+Calid</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkReglaTotalTramCal" id="checkReglaTotalTramCal" onclick="desmarcaCheckTodo(this);" tabindex="33"/></td>
</tr>
<tr align="left">
	<td class="literal">10-Tomador</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkTomador" id="checkTomador" onclick="desmarcaCheckTodo(this);" tabindex="10"/></td>	
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<!-- Columna: Total -->
	
	<td class="literal" colspan="2" rowspan="7">&nbsp;</td>
</tr>
<tr align="left">
	<td class="literal">11-Ref. p&oacute;liza</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkRefPoliza" id="checkRefPoliza" onclick="desmarcaCheckTodo(this);" tabindex="11"/></td>    
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<!-- Columna: Total -->
	
	<!-- Columna: Reglamento -->
	
</tr>
<tr align="left">
    <td class="literal">12- NIF asegurado</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkNifAseg" id="checkNifAseg" onclick="desmarcaCheckTodo(this);" tabindex="12"/></td>
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<!-- Columna: Total -->
	
	<!-- Columna: Reglamento -->
	
</tr>
<tr align="left">
	<td class="literal">13-Nombre asegurado</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkNombreAseg" id="checkNombreAseg" onclick="desmarcaCheckTodo(this);" tabindex="13"/></td>	
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<!-- Columna: Total -->
	
	<!-- Columna: Reglamento -->
	
</tr>
<tr align="left">
	<td class="literal">14-Apellido 1</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkApe1Aseg" id="checkApe1Aseg" onclick="desmarcaCheckTodo(this);" tabindex="14"/></td>	
	
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<!-- Columna: Total -->
	
	<!-- Columna: Reglamento -->
</tr>
<tr align="left">
	<td class="literal">15-Apellido 2</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkApe2Aseg" id="checkApe2Aseg" onclick="desmarcaCheckTodo(this);" tabindex="15"/></td>

	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->

	<!-- Columna: Total -->
	
	<!-- Columna: Reglamento -->

</tr>
<tr align="left">												
	<td class="literal">16-Raz&oacute;n social</td>
	<td style="background-color:#e5e5e5"><form:checkbox path="checkRazonSocialAseg" id="checkRazonSocialAseg" onclick="desmarcaCheckTodo(this);" tabindex="16"/></td>
	<!-- Columna: Nuevo -->
	
	<!-- Columna: Regularización -->
	
	<!-- Columna: Total -->
	
	<!-- Columna: Reglamento -->
</tr>			
	
	</c:when>
	<c:otherwise>
		
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