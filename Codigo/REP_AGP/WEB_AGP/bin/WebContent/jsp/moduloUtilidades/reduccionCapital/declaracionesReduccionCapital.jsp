<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<fmt:bundle basename="displaytag">
	<c:set var="numReg">
		<fmt:message key="numElementsPag" />
	</c:set>
</fmt:bundle>

<html>
<head>
	<title>Declaraciones de Reducciones de Capital</title>
	
	<%@ include file="/jsp/common/static/metas.jsp"%>
	
	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
	
	<!-- JavaScript,jQery & AJAX -->
	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
	<script type="text/javascript" src="jsp/js/util.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
	<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
	<script type="text/javascript" src="jsp/js/calendar.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>	
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
	<script type="text/javascript" src="jsp/moduloUtilidades/reduccionCapital/operacionesReduccionCapital.js" ></script>
	
	<script language="javascript">
	
		$(document).ready(function(){
			var URL = UTIL.antiCacheRand(document.getElementById("main").action);
			document.getElementById("main").action = URL;
			//P0079361 No se esconde nunca, la comprobación se hará al pulsar el botón de Alta
			$('#botonAlta').show();
			//if ($('#altaKO').val() == "true"){
				//$('#botonAlta').attr('disabled','disabled');
			//}else{
				//$('#botonAlta').attr('disabled','');
			//}   
			//P0079361
		});

		function pintarDescRiesgo(){
				var table = document.getElementById('listaReduccionCapital');
				if(table){
					var rowCount = table.rows.length;
					var jsonArray = ${listaRiesgosDesc};
					
					for(var j=1; j<rowCount; j++) {
						for(var json in jsonArray){
						    for(var i in jsonArray[json]){
						    	if (table.rows[j].cells[3].innerHTML == i){
						    		table.rows[j].cells[4].innerHTML = jsonArray[json][i];
						    	}
							}
						}
		            }
				}
		}
		
	</script>

</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4');pintarDescRiesgo();"> 
	<%@ include file="../../common/static/cabecera.jsp"%>
	<%@ include file="../../common/static/menuGeneral.jsp"%>
	<%@ include file="../../common/static/datosCabeceraTaller.jsp" %>		
	
	<!-- Botones para alta y volver -->
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">
				<!-- P0079361 Dudas -->
					<a class="bot" id="botonAlta" href="javascript:crear(${idPoliza}, ${poliza.linea.codlinea}, ${poliza.linea.codplan}, 
					'${poliza.linea.nomlinea}', '${poliza.referencia }');">Alta</a>
				<!-- P0079361 Dudas -->
					<a class="bot" href="javascript:volver()">Volver</a>					
				</td>
			</tr>
		</table>
	</div>		

	<!-- Contenido de la página -->
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Declaraciones de Reducciones de Capital</p>
		<form name="print" id="print" action="informes.html" method="post">
			<input type="hidden" name="method" id="methodPrint" value="doInformeReduccionCapital"/>
			<input type="hidden" name="idReduccionCapital" id="idReduccionCapital"/>
		</form>
		<form:form name="main" id="main" action="declaracionesReduccionCapital.html" method="post" commandName="reduccionCapitalBean">
			<input type="hidden" id="modoLectura" name="modoLectura" />
			<input type="hidden" id="method" name="method" />
			
			<input type="hidden" id="idAnexoCaducado" name="idAnexoCaducado"/> 
			<input type="hidden" id="idPoliza" name="idPoliza" value="${idPoliza}"/>
			<input type="hidden" id="idPoliza2" name="idPoliza2" value="${idPoliza}"/>
			<input type="hidden" id="idCupon" name="idCupon" />
			
			<form:hidden path="cupon.id" id="idCuponNum"/>
			<form:hidden path="cupon.idcupon" id="idCupon"/>
			<form:hidden path="cupon.cuponPrevio.cuponPpalPrevio" id="idCuponPpalPrevio"/>
			<form:hidden path="cupon.cuponPrevio.estadoCuponPpalPrevio.id" id="estadoCuponPpalPrevio"/>
			
			<form:hidden path="id" id="id"/>
			<form:hidden path="poliza.idenvio" id="idenvio"/>
			<form:hidden path="poliza.idpoliza" id="idPoliza"/>				
			<form:hidden path="poliza.linea.codlinea" id="codlinea"/>	
			<form:hidden path="poliza.linea.codplan" id="codplan"/>							
			<form:hidden path="poliza.linea.nomlinea" id="nomlinea"/>
			<form:hidden path="poliza.referencia" id="refPoliza"/>
			
			<input type="hidden" id="altaKO" name="altaKO" value="${altaKO}"/>
			<input type="hidden" name="fromUtilidades" value="true">
			
			
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
		</form:form>
		
		<form:form name="impresionIncidenciasMod" id="impresionIncidenciasMod" action="impresionIncidenciasMod.html" method="post" commandName="reduccionCapitalBean">
			<input type="hidden" id="method" name="method" value="doImprimirIncidencias" />
			<form:hidden path="poliza.linea.codlinea" id="codlinea"/>	
			<form:hidden path="poliza.linea.codplan" id="codplan"/>							
			<form:hidden path="poliza.linea.nomlinea" id="nomlinea"/>
			<form:hidden path="poliza.referencia" id="refPoliza"/>
			<form:hidden path="poliza.idpoliza" id="idPoliza"/>
			<input type="hidden" id="nombreCompleto" name="nombreCompleto" value="${poliza.asegurado.nombreCompleto}"/>
			<form:hidden path="poliza.codmodulo" id="codmodulo"/>
			<input type="hidden" id="fechaEnvio" name="fechaEnvio" value="${fechaEnvioAnexo}"/>
			<input type="hidden" id="idCuponImpresion" name="idCuponImpresion"/>
		</form:form>
		
		<!-- Datos de la póliza -->
		<fieldset style="width:95%">
		<legend class="literal">Datos de la p&oacute;liza</legend>
			<table width="90%" align="center" cellspacing="1">
				<tr>
					<td class="literal" width="40px">Plan:</td>
					<td width="80px" class="detalI">${poliza.linea.codplan}</td>
					<td class="literal" width="50px">Línea:</td>
					<td width="500px" class="detalI">${poliza.linea.codlinea } - ${poliza.linea.nomlinea }</td>
  					<td class="literal" width="60px">Asegurado:</td>
 					<td width="300px" class="detalI">${poliza.asegurado.nombreCompleto}</td> 
				</tr>
				<tr>
					<td class="literal" width="40px">Póliza:</td>
					<td width="80px" class="detalI">${poliza.referencia}</td>
					<td class="literal" width="50px">Módulo:</td>
					<td width="500px" class="detalI">${poliza.codmodulo}</td>		
					<td class="literal" width="70px" align="left">Fec. Envío:</td>
					<td width="300px" align="left" class="detalI"><fmt:formatDate pattern="dd/MM/yyyy" value="${poliza.fechaenvio}"/></td>					
				</tr>
			</table>
		</fieldset>
		<!-- Para el listado de las diferentes reducciones de capital -->
		<br/><br/>
		<div>
	        <display:table requestURI="declaracionesReduccionCapital.html" id="listaReduccionCapital" class="LISTA" summary="ReduccionCapital" 
	        name="${listaReduccionCapital}" sort="list" pagesize="${numReg}"
	        decorator="com.rsi.agp.core.decorators.ModelTableDecoratorReduccionCapital" 
	        style="width:100%;border-collapse:collapse;" excludedParams="method" defaultsort="2">
	            <display:column class="literal" headerClass="cblistaImg" title="Acciones" property="acciones" style="width:100px;text-align:center"/>
	            <display:column class="literal" headerClass="cblistaImg" title="Orden" property="columnaNumAnexo" style="width:60px;text-align:left" sortable="true"/>
	          	<display:column class="literal" headerClass="cblistaImg" title="Fec. Ocurrencia" format="{0,date,dd/MM/yyyy}" property="columnaOcurrencia" style="width:60px;text-align:left" sortable="true"/>
	        	<display:column class="literal" headerClass="cblistaImg" title="Riesgo" property="columnaRiesgo" style="width:60px;text-align:left"/>
	        	<display:column class="literal" headerClass="cblistaImg" title="Descripción" property="columnaDescripcion" style="width:60px;text-align:left"/>
	        	<!-- P0079361 -->
	        	<display:column class="literal" headerClass="cblistaImg" title="Tipo RC" property="columnaNumero" style="width:70px;text-align:left"/>
	        	<display:column class="literal" headerClass="cblistaImg" title="Estado RC" property="columnaEstado" style="width:70px;text-align:left"/>
	         	<display:column class="literal" headerClass="cblistaImg" title="Estado Cupón" property="columnaEstadoCupon" style="width:70px;text-align:left"/>
	        	<display:column class="literal" headerClass="cblistaImg" title="Fec. Envío" format="{0,date,dd/MM/yyyy}" property="columnaEnvio" style="width:60px;text-align:left"/>
	        	<!-- P0079361 -->
	        </display:table>				
		</div>

	</div>
	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/moduloUtilidades/reduccionCapital/popupEstadoContratacion.jsp"%>
</body>
</html>