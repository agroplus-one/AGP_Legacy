<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Consultas de Recibos 2015+</title>

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
		<script type="text/javascript" src="jsp/moduloComisiones/informesRecibos2015.js" ></script>
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
		<p class="titulopag" align="left">Consultas de Recibos 2015+</p>

			<form:form name="main3" id="main3" action="informesRecibos2015.run" method="post" commandName="informeRecibosBean">

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
									<td class="literal">Fecha emisi&oacute;n:</td>
									<td><form:select path="condiFechaEmision" cssClass="dato" id="condiFechaEmision" cssStyle="width:100" >
											<form:option value="" ></form:option>
											<form:option value="5">Igual</form:option>
											<form:option value="4">Entre</form:option>
										</form:select></td>
									<td><form:input path="fechaEmision" id="fechaEmision" cssClass="dato" size="30"/></td>
									<td class="literal">N&uacute;mero recibo:</td>
									<td><form:select path="condiRecibo" cssClass="dato" id="condiRecibo" cssStyle="width:100" >
											<form:option value="" ></form:option>
											<form:option value="5">Igual</form:option>
											<form:option value="3">Contenido en</form:option>
										</form:select></td>
									<td><form:input path="recibo" id="recibo" cssClass="dato" size="30"/></td>
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
							</table>
					 </fieldset>

					 <fieldset style="width:85%;margin: 0 auto;">
						<legend class="literal">Campos a mostrar en el Informe</legend>

							<table width="90%" align="center" border="0" >
								<tr>
									<td class="literal">Formato</td>
									<td class="literal">
										<form:radiobutton cssClass="literal" id="formato" path="formato" value="3" />EXCEL
										<form:radiobutton  cssClass="literal" id="formato" path="formato" value="1" />HTML
										<form:radiobutton cssClass="literal" id="formatoPDF" path="formato" value="2" />PDF
										<form:radiobutton cssClass="literal" id="formato" path="formato" value="4" />TXT
									</td>
									<td class="literal" align="right">Marcar todo&nbsp;<input type="checkbox" name="checkTodo" id="checkTodo"  class="dato" onclick="this.checked  ? marcar_todos() : desmarcar_todos() "/></td>
								</tr>
							</table>
							<table width="90%" align="center" border="1" >
								<tr align="left">
									<td class="literal">1-N&uacute;mero fase</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkFase" id="checkFase" onclick="desmarcaCheckTodo(this);" tabindex="1"/></td>
									<td rowspan="100%">&nbsp;</td>
									<td class="literal">2-Entidad</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkEntidad" id="checkEntidad" onclick="desmarcaCheckTodo(this);" tabindex="2"/></td>
									<td rowspan="100%">&nbsp;</td>
									<td class="literal">3-Oficina</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkOficina" id="checkOficina" onclick="desmarcaCheckTodo(this);" tabindex="3"/></td>
									<td rowspan="100%">&nbsp;</td>
									<td class="literal">4-Entidad med.</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkEntidadMed" id="checkEntidadMed" onclick="desmarcaCheckTodo(this);" tabindex="4"/></td>
								</tr>
								<tr align="left">
									<td class="literal">5-Subentidad med.</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkSubent" id="checkSubent" onclick="desmarcaCheckTodo(this);" tabindex="5"/></td>
									<td class="literal">6-Plan</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkPlan" id="checkPlan" onclick="desmarcaCheckTodo(this);" tabindex="6"/></td>
									<td class="literal">7-L&iacute;nea</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkLinea" id="checkLinea" onclick="desmarcaCheckTodo(this);" tabindex="7"/></td>
									<td class="literal">8-Fecha emisi&oacute;n</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkFechaEmision" id="checkFechaEmision" onclick="desmarcaCheckTodo(this);" tabindex="8"/></td>
								</tr>
								<tr align="left">
									<td class="literal">9-Colectivo</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkRefColectivo" id="checkRefColectivo" onclick="desmarcaCheckTodo(this);"tabindex="9"/></td>
									<td class="literal">10-Tomador</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkRsTomador" id="checkRsTomador" onclick="desmarcaCheckTodo(this);" tabindex="10"/></td>
									<td class="literal">11-N&uacute;mero recibo</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkRecibo" id="checkRecibo" onclick="desmarcaCheckTodo(this);" tabindex="11"/></td>
									<td class="literal">12-Ref. p&oacute;liza</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkRefPoliza" id="checkRefPoliza" onclick="desmarcaCheckTodo(this);" tabindex="12"/></td>
								</tr>
								<tr align="left">
									<td class="literal" >13-Tipo recibo</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkTipoRecibo" id="checkTipoRecibo" onclick="desmarcaCheckTodo(this);"  tabindex="13"/></td>
									<td class="literal" >14- NIF asegurado</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkNifAseg" id="checkNifAseg" onclick="desmarcaCheckTodo(this);" tabindex="14"/></td>
									<td class="literal" >15-Apellido 1</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkApe1" id="checkApe1" onclick="desmarcaCheckTodo(this);" tabindex="15"/></td>
									<td class="literal" >16-Apellido 2</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkApe2" id="checkApe2" onclick="desmarcaCheckTodo(this);" tabindex="16"/></td>
								</tr>
								<tr align="left">
									<td class="literal" >17-Nombre asegurado</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkNombre" id="checkNombre" onclick="desmarcaCheckTodo(this);" tabindex="17"/></td>
									<td class="literal" >18-Raz&oacute;n social</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkRsAseg" id="checkRsAseg" onclick="desmarcaCheckTodo(this);" tabindex="18"/></td>
									<td class="literal" >19-Prima Comercial</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkPrimaComercial" id="checkPrimaComercial" onclick="desmarcaCheckTodo(this);" tabindex="19"/></td>
									<td class="literal" >20-Prima Comercial Neta</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkPrimaNeta" id="checkPrimaNeta"  onclick="desmarcaCheckTodo(this);" tabindex="20"/></td>
								</tr>
								<tr align="left">
									<td class="literal" >21-Recargo Consorcio</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkRecargoCons" id="checkRecargoCons" onclick="desmarcaCheckTodo(this);" tabindex="21"/></td>
									<td class="literal" >22-Recibo de Prima</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkReciboPrima" id="checkReciboPrima" onclick="desmarcaCheckTodo(this);" tabindex="22"/></td>
									<td class="literal" >23-Subvenci&oacute;n ENESA</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkSubvEnesa" id="checkSubvEnesa" onclick="desmarcaCheckTodo(this);"  tabindex="23"/></td>
									<td class="literal" >24-Coste Tomador</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkCosteTomador" id="checkCosteTomador" onclick="desmarcaCheckTodo(this);"  tabindex="24"/></td>
								</tr>
								<tr align="left">
									<td class="literal" >25-Total Coste Tomador</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkCosteTomTotal" id="checkCosteTomTotal" onclick="desmarcaCheckTodo(this);" tabindex="25"/></td>
									<td class="literal" >26-Pagos</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkPagos" id="checkPagos" onclick="desmarcaCheckTodo(this);" tabindex="26"/></td>
									<td class="literal" >27-Diferencia</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkDiferencia" id="checkDiferencia" onclick="desmarcaCheckTodo(this);" tabindex="27"/></td>
									<td class="literal" >28-Imp. Recargo Aval</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkImpRecargoAval" id="checkImpRecargoAval" onclick="desmarcaCheckTodo(this);" tabindex="28"/></td>
								</tr>
								<tr align="left">
									<td class="literal" >29-Imp. Recargo Fraccionamiento</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkImpRecargoFrac" id="checkImpRecargoFrac" onclick="desmarcaCheckTodo(this);" tabindex="29"/></td>
									<td class="literal" >30-Bonificaciones</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkBonif" id="checkBonif" onclick="desmarcaCheckTodo(this);" tabindex="30"/></td>
									<td class="literal" >31-Subvenciones CCAA</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkSubvCcaa" id="checkSubvCcaa" onclick="desmarcaCheckTodo(this);" tabindex="31"/></td>
									<td class="literal" >32-Recargos</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkRecargos" id="checkRecargos" onclick="desmarcaCheckTodo(this);" tabindex="32"/></td>
								</tr>
								<tr align="left">
									<td class="literal" >33-Pago domiciliado</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkPagoDomiciliado" id="checkPagoDomiciliado" onclick="desmarcaCheckTodo(this);" tabindex="33"/></td>
									<td class="literal" >34-Destinatario</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkDestinatario" id="checkDestinatario" onclick="desmarcaCheckTodo(this);" tabindex="34"/></td>
									<td class="literal" >35-Imp. a domiciliar</td>
									<td style="background-color:#e5e5e5"><form:checkbox path="checkImpDomiciliar" id="checkImpDomiciliar" onclick="desmarcaCheckTodo(this);" tabindex="35"/></td>
									<td class="literal" ></td>
									<td style="background-color:#e5e5e5"></td>
								</tr>
							</table>
					 </fieldset>

			</form:form>

		</div>

	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>

</body>
</html>