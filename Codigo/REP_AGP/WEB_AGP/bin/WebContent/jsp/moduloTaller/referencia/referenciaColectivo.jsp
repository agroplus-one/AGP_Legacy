<%@ include file="/jsp/common/static/taglibs.jsp"%>
<fmt:setBundle basename="displaytag"/>
<c:set var="numReg"><fmt:message key="numElementsPag"/></c:set>

<html>
	<head>
		<title>Referencias de Colectivo</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		
		<script type="text/javascript" src="jsp/js/util.js"></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript">
		
		    // Para evitar el cacheo de peticiones al servidor
	        $(document).ready(function(){
				$("#main").attr("action", UTIL.antiCacheRand($("#main").attr("action")));
	        });
		
			function generar(method) {
				$('#method').val('doGenerar');
				$.blockUI.defaults.message = '<h4>Generando referencias.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
				$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
				$('#main').submit();
			}
		</script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub2');">

		<%@ include file="../../common/static/cabecera.jsp"%>
		<%@ include file="../../common/static/menuLateralTaller.jsp"%>
		<%@ include file="../../common/static/datosCabeceraTaller.jsp"%>

		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="right">
						<a class="bot" id='btnGenerar' href="javascript:generar()">Generar</a>
					</td>
				</tr>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%">
			<p class="titulopag" align="left">Referencias de Colectivo</p>
		    <form:form name="main" id="main" action="referenciaColectivo.html" method="post" commandName="colectivoReferenciaBean">
				<input type="hidden" name="method" id="method" />
	
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				
				
				
				<div class="panel1 isrt" style="width:95%;">
					<div style="float:left;">
					<fieldset class="fieldset_alone" style="width:50px">
						<legend>REF. LIBRES</legend>
						<span class="literal">${refLibres }</span>
					</fieldset>
					</div>
					<div style="float:left;">
					<fieldset class="fieldset_alone" style="width:110px; margin:auto 3;">
						<legend>ÚLTIMA REFERENCIA</legend>
						<span class="literal">${ultimaRef }</span>
					</fieldset>
					</div>
					<div style="float:left;">
					<fieldset class="fieldset_alone">
						<legend>REFERENCIAS COLECTIVO</legend>
						<table width="100%">
							<thead>
								<tr align="left">
									<th class="literal">INICIO:</th>
									<th class="literal">FIN:</th>
								</tr>
							</thead>
							<tr align="left">
								<td class="literal"><input type="text" name="referenciaIni" class="dato" value="${filtro.referenciaIni}" /></td>
								<td class="literal"><input type="text" name="referenciaFin" class="dato"  value="${filtro.referenciaFin}"/></td>
							</tr>
						</table>
					</fieldset>
					</div>
				</div>
				
				
				
				
				
			</form:form>
			<br />
		</div>
		<br />
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
	</body>
</html>