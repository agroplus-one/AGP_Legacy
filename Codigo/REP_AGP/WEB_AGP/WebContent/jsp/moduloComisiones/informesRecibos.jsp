<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Consultas de Recibos</title>
		
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
		<script type="text/javascript" src="jsp/moduloComisiones/informesRecibos.js" ></script>
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
		<p class="titulopag" align="left">Consultas de Recibos</p>	
		
			<form:form name="main3" id="main3" action="informesRecibos.run" method="post" commandName="informeRecibosBean">
				
				<input type="hidden" name="idInforme" id="idInforme" value="${idInforme}"/>
				<input type="hidden" id="method" name="method" value="" />
				<input type="hidden" id="lstCodEntidades" name="lstCodEntidades" value="${lstCodEntidades}" />
				<input type="hidden" id="perfil" value="${perfil}" />
				<input type="hidden" id="entMed" value="${entMed}" />
				<input type="hidden" id="subEntMed" value="${subEntMed}" />
				<input type="hidden" id="esExterno" value="${esExterno}" />
				
				<form:hidden path="datosDe" id="datosDe"/>

								
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>	
				
				
					<fieldset style="width:85%;margin: 0 auto;">
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
					 
					 <fieldset style="width:85%;margin: 0 auto;">
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
									<td class="literal">Fecha emisi&oacute;n: </td>
									<td><form:select path="condiFecha" cssClass="dato" id="condiFecha" cssStyle="width:100" >
											<form:option value="" ></form:option>
											<form:option value="5">Igual</form:option>
											<form:option value="4">Entre</form:option>											
										</form:select></td>
									<td><form:input path="fecha" id="fecha" cssClass="dato" size="30"/></td>
									<td class="literal">N&uacute;mero recibo: </td>
									<td><form:select path="condiRecibo" cssClass="dato" id="condiRecibo" cssStyle="width:100" >
											<form:option value="" ></form:option>
											<form:option value="5">Igual</form:option>
											<form:option value="3">Contenido en</form:option>									
										</form:select></td>
									<td><form:input path="recibo" id="recibo" cssClass="dato" size="30"/></td>		
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
							</table>		
					 </fieldset>
					 
					 <fieldset style="width:85%;margin: 0 auto;">
						<legend class="literal">Campos a mostrar en el Informe</legend>
							<table width="80%" align="center" border="1" >
								<tr align="right">
									<td class="literal" colspan="7">Marcar todo&nbsp;</td>
									<td align="left" style="background-color:#e5e5e5"><input type="checkbox" name="checkTodo" id="checkTodo"  class="dato" onclick="this.checked  ? marcar_todos() : desmarcar_todos() "/></td>
								</tr>
								<tr align="left">
									<td class="literal" >1-N&uacute;mero fase:</td>
									<td  style="background-color:#e5e5e5"><form:checkbox path="checkFase" id="checkFase" onclick="desmarcaCheckTodo(this);" tabindex="1"/></td>
									<td class="literal" >2-Entidad:</td>
									<td  style="background-color:#e5e5e5"><form:checkbox path="checkEntidad" id="checkEntidad" onclick="desmarcaCheckTodo(this);" tabindex="2"/></td>									
									<td class="literal" >3-Oficina:</td>
									<td  style="background-color:#e5e5e5"><form:checkbox path="checkOficina" id="checkOficina" onclick="desmarcaCheckTodo(this);" tabindex="3"/></td>
									<td class="literal" >4-Entidad med.:</td>
									<td  style="background-color:#e5e5e5"><form:checkbox path="checkEntidadMed" id="checkEntidadMed" onclick="desmarcaCheckTodo(this);" tabindex="4"/></td>
								</tr>
								<tr align="left">
									<td class="literal" >5-Subentidad med.:</td>
									<td  style="background-color:#e5e5e5"><form:checkbox path="checkSubent" id="checkSubent" onclick="desmarcaCheckTodo(this);" tabindex="5"/></td>
									<td class="literal" >6-Plan:</td>
									<td  style="background-color:#e5e5e5"><form:checkbox path="checkPlan" id="checkPlan" onclick="desmarcaCheckTodo(this);" tabindex="6"/></td>
									<td class="literal" >7-L&iacute;nea:</td>
									<td  style="background-color:#e5e5e5"><form:checkbox path="checkLinea" id="checkLinea" onclick="desmarcaCheckTodo(this);" tabindex="7"/></td>
									<td class="literal" >8-Fecha emisi&oacute;n:</td>
									<td  style="background-color:#e5e5e5"><form:checkbox path="checkFecha" id="checkFecha" onclick="desmarcaCheckTodo(this);" tabindex="8"/></td>
								</tr>
								<tr align="left">
									<td class="literal" >9-Colectivo:</td>
									<td  style="background-color:#e5e5e5"><form:checkbox path="checkColectivo" id="checkColectivo" onclick="desmarcaCheckTodo(this);"tabindex="9"/></td>
									<td class="literal" style="background-color:#91FFA2">10-Tomador:</td>
									<td  style="background-color:#e5e5e5"><form:checkbox path="checkTomador" id="checkTomador" onclick="desmarcaCheckTodo(this);" tabindex="10"/></td>
									<td class="literal" style="background-color:transparent" >11-N&uacute;mero recibo:</td>
									<td  style="background-color:#e5e5e5"><form:checkbox path="checkRecibo" id="checkRecibo" onclick="desmarcaCheckTodo(this);" tabindex="11"/></td>
									<td class="literal" style="background-color:#91FFA2">12-Saldo tomador:</td>
									<td  style="background-color:#e5e5e5"><form:checkbox path="checkSaldoTom" id="checkSaldoTom" onclick="desmarcaCheckTodo(this);" tabindex="12"/></td>
								</tr>
								<tr align="left" style="background-color:#e5e5e5">
									<td class="literal" style="background-color:#91FFA2">13-Comp. tomador:</td>
									<td  style="background-color:#e5e5e5"><form:checkbox path="checkCompTom" id="checkCompTom" onclick="desmarcaCheckTodo(this);" tabindex="13"/></td>
									<td class="literal" style="background-color:#91FFA2">14-Comp. impagado:</td>
									<td  style="background-color:#e5e5e5"><form:checkbox path="checkCompImp" id="checkCompImp" onclick="desmarcaCheckTodo(this);" tabindex="14"/></td>
									<td class="literal" style="background-color:#91FFA2">15-Pago recibo:</td>
									<td><form:checkbox path="checkPagoRecibo" id="checkPagoRecibo" onclick="desmarcaCheckTodo(this);" tabindex="15" /></td>
									<td class="literal" style="background-color:#91FFA2">16-L&iacute;quido recibo:</td>
									<td><form:checkbox path="checkLiquidoRecibo" id="checkLiquidoRecibo" onclick="desmarcaCheckTodo(this);" tabindex="16"/></td>
								</tr>
								<tr align="left" style="background-color:#e5e5e5">
									<td class="literal" >17-Ref. p&oacute;liza</td>
									<td><form:checkbox path="checkReferencia" id="checkReferencia" onclick="desmarcaCheckTodo(this);" tabindex="17"/></td>
									<td class="literal" >18-Tipo recibo:</td>
									<td><form:checkbox path="checkTipoRecibo" id="checkTipoRecibo" onclick="desmarcaCheckTodo(this);"  tabindex="18"/></td>
									<td class="literal" >19- NIF asegurado</td>
									<td><form:checkbox path="checkNif" id="checkNif" onclick="desmarcaCheckTodo(this);" tabindex="19"/></td>
									<td class="literal" >20-Apellido 1:</td>
									<td><form:checkbox path="checkApe1" id="checkApe1" onclick="desmarcaCheckTodo(this);" tabindex="20"/></td>
								</tr>
								<tr align="left" style="background-color:#e5e5e5">
									<td class="literal" >21-Apellido 2:</td>
									<td><form:checkbox path="checkApe2" id="checkApe2" onclick="desmarcaCheckTodo(this);" tabindex="21"/></td>
									<td class="literal" >22-Nombre asegurado</td>
									<td><form:checkbox path="checkNombre" id="checkNombre" onclick="desmarcaCheckTodo(this);" tabindex="22"/></td>
									<td class="literal" >23-Raz&oacute;n social:</td>
									<td><form:checkbox path="checkRazonSocial" id="checkRazonSocial" onclick="desmarcaCheckTodo(this);" tabindex="23"/></td>
									<td class="literal" >24-Prima comercial:</td>
									<td><form:checkbox path="checkPrimaCom" id="checkPrimaCom" onclick="desmarcaCheckTodo(this);" tabindex="24"/></td>
								</tr>
								<tr align="left" style="background-color:#e5e5e5">
									<td class="literal" >25-Prima neta:</td>
									<td><form:checkbox path="checkPrimaNeta" id="checkPrimaNeta"  onclick="desmarcaCheckTodo(this);" tabindex="25"/></td>
									<td class="literal" >26-Coste neto:</td>
									<td><form:checkbox path="checkCosteNeto" id="checkCosteNeto" onclick="desmarcaCheckTodo(this);" tabindex="26"/></td>
									<td class="literal" >27-Coste tomador:</td>
									<td><form:checkbox path="checkCosteTom" id="checkCosteTom" onclick="desmarcaCheckTodo(this);" tabindex="27"/></td>
									<td class="literal" >28-Pago p&oacute;liza:</td>
									<td><form:checkbox path="checkPagoPlz" id="checkPagoPlz" onclick="desmarcaCheckTodo(this);"  tabindex="28"/></td>
								</tr>
								<tr align="left" style="background-color:#e5e5e5">
									<td class="literal" >29-Saldo p&oacute;liza:</td>
									<td><form:checkbox path="checkSaldo" id="checkSaldo" onclick="desmarcaCheckTodo(this);" tabindex="29"/></td>
									<td colspan="6" style="background-color:#FFFFFF">&nbsp;</td>
								</tr>
							</table>			
					 </fieldset>
					 	
			</form:form>  			

		</div>
	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
</body>
</html>