<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
<head>
	<title>Líneas para RC de Ganado</title>
	
	<%@ include file="/jsp/common/static/metas.jsp"%>
	
	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
	
	<!-- JavaScript,jQery & AJAX -->
	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
	<script type="text/javascript" src="jsp/js/util.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
	<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>	
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
	<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
	<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
	<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
	<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
	<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
	<script type="text/javascript" src="jsp/js/lupaGenericaIN.js"></script>
	<script type="text/javascript" src="jsp/js/lineas.js"></script>
	<script type="text/javascript" src="jsp/js/ajaxfileupload.js"></script>	
	<script type="text/javascript" src="jsp/js/jquery.filestyle.js"></script>
	
	<script type="text/javascript" src="jsp/moduloRC/lineasRC.js" ></script>
	
	<script type="text/javascript">
		function cargarFiltro() {			 
		 	<c:if test="${origenLlamada != 'menuGeneral'}">	
				<c:forEach items="${sessionScope.listaLineasRC_LIMIT.filterSet.filters}" var="filtro">				
					<c:if test="${filtro.property == 'linea.codplan'}">
						$('#plan').val('${filtro.value}');
					</c:if>	
					<c:if test="${filtro.property == 'linea.codlinea'}">
						$('#linea').val('${filtro.value}');
					</c:if>	
					<c:if test="${filtro.property == 'codespecie'}">
						$('#especie').val('${filtro.value}');
					</c:if>	
					<c:if test="${filtro.property == 'codregimen'}">
						$('#regimen').val('${filtro.value}');
					</c:if>	
					<c:if test="${filtro.property == 'codtipocapital'}">
						$('#capital').val('${filtro.value}');
					</c:if>	
					<c:if test="${filtro.property == 'especiesRC.codespecie'}">
						$('#especiesRC').val('${filtro.value}');
					</c:if>	
				</c:forEach>
			</c:if>
		}
	</script>	
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchSubMenu('sub15','sub14'); cargarFiltro();">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	<!-- botones de la página -->
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td width="5">&nbsp;</td>
				<td align="left">
					<a class="bot" id="btnReplicar" href="javascript:replicar();">Replicar</a>
				</td>
				<td align="right"> 
					<a class="bot" id="btnAlta" href="javascript:alta();">Alta</a>
					<a class="bot" id="btnModificar" href="javascript:modificar();" style="display:none;">Modificar</a>
					<c:if test="${origenLlamada == 'menuGeneral'}">
						<a class="bot" id="btnConsultar" href="javascript:consultarInicial();">Consultar</a>
					</c:if>
					<c:if test="${origenLlamada != 'menuGeneral'}">
						<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a> 
					</c:if>	
					<a class="bot" id="btnLimpiar"href="javascript:limpiar()">Limpiar</a>
				</td>
			</tr>
		</table>
	</div>
	
	<div class="conten" style="padding: 3px; width: 100%">
		<p class="titulopag" align="left">LÍNEAS PARA RC DE GANADO</p>
	
		<!-- Form principal -->
		<form:form id="formLineasRC" name="formLineasRC" action="lineasRC.run" method="post" commandName="lineasRC">
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>	
			
			<input type="hidden" name="usuarioSession" id="usuarioSession" value="${usuarioSession}"/>		
			<input type="hidden" name="method" id="method"/>
			<input type="hidden" name="origenLlamada" id="origenLlamada"/>
			<form:hidden path="id" id="id"/>
			
			<input type="hidden" name="listaIdsMarcados" id="listaIdsMarcados" value=""/>
			<input type="hidden" name="listaIdsTodos" id="listaIdsTodos" value="${listaIdsTodos}"/>
			<input type="hidden" name="marcaTodos" id="marcaTodos" value="false"/>
			
			<form:hidden path="linea.lineaseguroid" id="lineaseguroid"/>
			<input type="hidden" id="codconcepto" name="codconcepto" value="126"/>
			<input type="hidden" id="codplan" name="codplan" value=""/>
			<input type="hidden" id="codlinea" name="codlinea" value=""/>
			
			<input type="hidden" name="planreplica" id="planreplica" value="${planreplica}"/>
			<input type="hidden" name="lineareplica" id="lineareplica" value="${lineareplica}"/>
			
			<fieldset class="panel2 isrt" style="width:95%; margin:0 auto;">
				<legend class="literal">Filtro</legend>
				<div style="width: 100%; float: left;">
					<table cellspacing="10px">
						<tr>
							<td>
								<label for="plan" class="literal">Plan</label>
								<form:input path="linea.codplan" size="4" maxlength="4"
									cssClass="dato" id="plan" tabindex="1"
									onchange="javascript:lupas.limpiarCampos('linea', 'desc_linea');" />
								<label class="campoObligatorio" id="campoObligatorio_plan"
									title="Campo obligatorio"> *</label> 
							</td>
							<td>
								<label for="linea" class="literal">Línea</label>
								<form:input path="linea.codlinea" size="3" maxlength="3"
									cssClass="dato" id="linea" tabindex="2"
									onchange="javascript:lupas.limpiarCampos('desc_linea');" />
								<form:input path="linea.nomlinea" cssClass="dato" id="desc_linea"
									size="30" readonly="true" />
								<img src="jsp/img/magnifier.png" style="cursor: hand;"
									onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');"
									alt="Buscar Línea" title="Buscar Línea" /> 
								<label class="campoObligatorio"
									id="campoObligatorio_linea" title="Campo obligatorio"> *</label>
							</td>
							<td>
								<label for="especie" class="literal">Especie</label>
								<form:input path="codespecie" size="3" maxlength="3"
									cssClass="dato" id="especie" tabindex="3"
									onchange="javascript:lupas.limpiarCampos('desc_especie');" />
								<form:input path="descespecie" cssClass="dato" id="desc_especie"
									size="16" readonly="true" />
								<img src="jsp/img/magnifier.png" style="cursor: hand;"
									onclick="javascript:if(isLineaSeleccionada())lupas.muestraTabla('EspecieRC','principio', '', '');"
									alt="Buscar especie" title="Buscar especie" /> 
								<label class="campoObligatorio"
									id="campoObligatorio_especie" title="Campo obligatorio"> *</label>
							</td>
							<td>
								<label for="regimen" class="literal">Régimen</label>
								<form:input path="codregimen" size="3" maxlength="3"
									cssClass="dato" id="regimen" tabindex="4"
									onchange="javascript:lupas.limpiarCampos('desc_regimen');" />
								<form:input path="descregimen" cssClass="dato" id="desc_regimen"
									size="16" readonly="true" />
								<img src="jsp/img/magnifier.png" style="cursor: hand;"
									onclick="javascript:if(isLineaSeleccionada())lupas.muestraTabla('RegimenRC','principio', '', '');"
									alt="Buscar régimen" title="Buscar régimen" /> 
								<label class="campoObligatorio"
									id="campoObligatorio_regimen" title="Campo obligatorio"> *</label>
							</td>
						</tr>
					</table>
					<table cellspacing="10px">
						<tr>
							<td>
								<label for="capital" class="literal">Tipo de Capital</label>
								<form:input path="codtipocapital" size="3" maxlength="3"
									cssClass="dato" id="capital" tabindex="5"
									onchange="javascript:lupas.limpiarCampos('desc_capital');" />
								<form:input path="desctipocapital" cssClass="dato" id="desc_capital"
									size="16" readonly="true" />
								<img src="jsp/img/magnifier.png" style="cursor: hand;"
									onclick="javascript:if(isLineaSeleccionada())lupas.muestraTabla('FactoresTipoCapitalIN','principio', '', '');"
									alt="Buscar Tipo de Capital" title="Buscar Tipo de Capital" />
								<label class="campoObligatorio"
									id="campoObligatorio_capital" title="Campo obligatorio"> *</label>
							</td>
							<td>
								<label for="especiesRC" class="literal">Especie para RC</label>
								<form:select path="especiesRC.codespecie" cssClass="dato" tabindex="6"
									cssStyle="width:220px" id="especiesRC">
									<form:option value="">Todos</form:option>
									<c:forEach var="i" begin="0" end="${fn:length(listaEspeciesRC) - 1 }">
										<form:option value="${listaEspeciesRC[i].codespecie}">${listaEspeciesRC[i].descripcion}</form:option>
									</c:forEach>
								</form:select>
								<label class="campoObligatorio"
									id="campoObligatorio_especiesRC" title="Campo obligatorio"> *</label>
							</td>
						</tr>
					</table>
				</div>
			</fieldset>
		</form:form>
	
		<!-- Grid Jmesa -->
		<div id="grid">
	  		${listaLineasRC}		  							               
		</div> 	
		
	</div>
		
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
	<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEspecieRC.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaRegimenRC.jsp"%>	
	<%@ include file="/jsp/common/lupas/lupaTipoCapitalIN.jsp"%>	
	<%@ include file="/jsp/common/lupas/lupaLineaReplicar.jsp"%>
</body>
</html>