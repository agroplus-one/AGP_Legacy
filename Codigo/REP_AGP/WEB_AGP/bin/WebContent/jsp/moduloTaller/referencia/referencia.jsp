<%@ include file="/jsp/common/static/taglibs.jsp"%>
<fmt:setBundle basename="displaytag"/>
<c:set var="numReg"><fmt:message key="numElementsPag"/></c:set>

<html>
	<head>
		<title>Referencias de P&oacute;liza</title>
		
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
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/moduloTaller/referencia/referencia.js"></script>

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
			<p class="titulopag" align="left">Referencias de P&oacute;liza</p>
		    <form:form name="main" id="main" action="referencia.html" method="post" commandName="referenciaBean">
				<input type="hidden" name="method" id="method" />
				
				<div id="panelErrores" class="errorForm">
    				* Campos obligatorios
				</div>
				<div id="panelAlertasValidacion" class="errorForm"></div>
				<c:if test="${empty requestScope.alerta and not empty requestScope.mensaje }">
				<div id="panelInformacion" style="width:500px;height:20px;color:black;border:1px solid #FFCD00;display:block;
					font-size:12px;font-style:italic;font-weight:bold;line-height:20px;background-color:#FCF6CF">					
						<c:out value="${requestScope.mensaje }"/>					
				</div>
				</c:if>
					
			
				<div class="panel1 isrt" style="width:95%;">
					<div style="float:left;">
					<fieldset class="fieldset_alone" style="width:50px">
						<legend>REF. LIBRES</legend>
						<span class="literal" id="refLibres">${refLibres }</span>
					</fieldset>
					</div>
					<div style="float:left;">
					<fieldset class="fieldset_alone" style="width:110px; margin:auto 3">
						<legend>ÚLTIMA REFERENCIA</legend>
						<span class="literal" id="ultimaRef">${ultimaRef }</span>
					</fieldset>
					</div>
					<div style="float:left;">
					<fieldset class="fieldset_alone">
						<legend>REFERENCIAS S.AGRÍCOLA</legend>
						<table width="100%">
							<thead>
								<tr align="left">
									<th class="literal">INICIO:</th>
									<th class="literal">FIN:</th>
								</tr>
							</thead>
							<tr align="left">
								<td class="literal"><input type="text" name="referenciaIni" id="referenciaIni" class="dato" value="${filtro.referenciaIni}" /></td>
								<td class="literal"><input type="text" name="referenciaFin" id="referenciaFin" class="dato"  value="${filtro.referenciaFin}"/></td>
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
	</body>
</html>