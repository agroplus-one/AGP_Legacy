<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
	<head>
		<title>Impuestos para RC de Ganado</title>
		
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
		<script type="text/javascript" src="jsp/js/util.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.alerts.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenericaIN.js"></script>
		
		<!-- JavaScript propio de la pagina -->
		<script type="text/javascript" src="jsp/moduloRC/impuestosRC.js"></script>
		<script type="text/javascript">
			function cargarFiltro() {
			 	<c:if test="${origenLlamada != 'menuGeneral'}">	
					<c:forEach items="${sessionScope.listaImpuestosRC_LIMIT.filterSet.filters}" var="filtro">
						<c:if test="${filtro.property == 'codPlan'}">
							$('#codplan').val('${filtro.value}');
						</c:if>	
						<c:if test="${filtro.property == 'impuestoSbp.codigo'}">
							$('#codimpuesto').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'impuestoSbp.descripcion'}">
							$('#nomimpuesto').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'valor'}">
							$('#valor').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'baseSbp.base'}">
							$('#nombase').val('${filtro.value}');
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
		
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td width="5">&nbsp;</td>
					<td align="left">
						<a class="bot" id="btnReplicar" href="javascript:showPopUpReplicarPlan()">Replicar</a>
					</td>
					<td align="right">
						<a class="bot" id="btnAlta" href="javascript:alta()">Alta</a> 
						<a class="bot" id="btnModificar" href="javascript:modificar()" style="display: none;">Modificar</a> 
						<c:if test="${origenLlamada eq 'menuGeneral'}">
							<a class="bot" id="btnConsultar" href="javascript:consultarInicial()">Consultar</a>
						</c:if>
						<c:if test="${origenLlamada ne 'menuGeneral'}">
							<a class="bot" id="btnConsultar" href="javascript:consultar()">Consultar</a>
						</c:if>
						<a class="bot" id="btnLimpiar" href="javascript:limpiar()">Limpiar</a>
					</td>
				</tr>
			</table>
		</div>
		
		<!-- Formulario principal de la pagina -->
		<div class="conten" style="padding: 3px; width: 97%">
			<p class="titulopag" align="left">IMPUESTOS PARA RC DE GANADO</p>
		
			<form:form id="main3" name="main3" action="impuestosRC.run" method="POST" commandName="impuestosRC">
				
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				
				<input type="hidden" name="method" id="method" />
				<input type="hidden" name="origenLlamada" id="origenLlamada" />
				<input type="hidden" name="planreplica" id="planreplica" />
				<form:hidden path="id" id="id" />
		
				<fieldset class="panel2 isrt" style="width: 95%; margin:0 auto;">
					<legend class="literal">Filtro</legend>
					<div style="width: 95%; float: left;">
						<table cellspacing="10px">
							<tr>
								<td>
									<label for="codplan" class="literal">Plan</label> 
									<form:input path="codPlan" size="4" maxlength="4" cssClass="dato" id="codplan" tabindex="1" />
									<label class="campoObligatorio" id="campoObligatorio_codplan" title="Campo obligatorio"> *</label>
								</td>
								<td>
									<label for="codimpuesto" class="literal">Impuesto</label>
									<form:input path="impuestoSbp.codigo" id="codimpuesto" size="5" maxlength="5" cssClass="dato" tabindex="1" onchange="javascript:this.value=this.value.toUpperCase();lupas.limpiarCampos('nomimpuesto');"/>
									<label class="campoObligatorio"	id="campoObligatorio_impuesto" title="Campo obligatorio"> *</label>
									<form:input path="impuestoSbp.descripcion" id="nomimpuesto" size="30" maxlength="30" cssClass="dato" tabindex="1" onchange="javascript:this.value=this.value.toUpperCase();"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('ImpuestoSbp','principio', '', '');"	alt="Buscar Impuesto" title="Buscar Impuesto" />
									
								</td>
								<td>
									<label for="valor" class="literal">Valor</label>
									<form:input path="valor" size="4" maxlength="4" cssClass="dato" id="valor" tabindex="1" />
									<label class="campoObligatorio" id="campoObligatorio_valor" title="Campo obligatorio"> *</label>
								</td>
								<td>
									<label for="nombase" class="literal">Base</label>
									<form:input path="baseSbp.base" id="nombase" size="30" maxlength="30"cssClass="dato" tabindex="1" onchange="javascript:this.value=this.value.toUpperCase();"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('BaseSbp','principio', '', '');"	alt="Buscar Base" title="Buscar Base" />
									<label class="campoObligatorio" id="campoObligatorio_base" title="Campo obligatorio"> *</label>
								</td>
							</tr>
						</table>
					</div>
				</fieldset>
			</form:form>
		
			<!-- Grid Jmesa -->
			<div id="grid">
				${listaImpuestosRC}
			</div>
		</div>
		
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		
		<%@ include file="/jsp/common/lupas/lupaImpuestoSbp.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaBaseSbp.jsp"%>
		<%@ include file="/jsp/moduloRC/popUpReplicarPlan.jsp" %>
	</body>
</html>