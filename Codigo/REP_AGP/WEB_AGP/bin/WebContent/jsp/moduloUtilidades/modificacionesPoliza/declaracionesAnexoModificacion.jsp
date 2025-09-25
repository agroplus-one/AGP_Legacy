<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
<head>
	<title>Modificaci&oacute;n de p&oacute;lizas</title>
	
	<%@ include file="/jsp/common/static/metas.jsp"%>
	
	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
	<!-- JavaScript,jQery & AJAX -->
	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
	<script type="text/javascript" src="jsp/js/util.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	<script type="text/javascript" src="jsp/moduloUtilidades/modificacionesPoliza/declaracionesAnexoModificacion.js" ></script> 
	<script type="text/javascript" src="jsp/moduloUtilidades/anexoModificacion/renovacionCupon.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
	<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
	<%@ include file="/jsp/js/draggable.jsp"%>
	 
	<script type="text/javascript">
	$(document).ready(function(){
	
		if('${poliza.tipoReferencia}' == 'C'){
			$('#btnAlta').hide();
			$('#btnAltaCpl').show();
		}
	});
	</script>
	
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4')">	
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="left" width="30%">&nbsp;
					<!-- Ya no se utiliza -->
					<!--<c:if test="${not poliza.linea.lineaGanado}">
						<a class="bot" id="btnAlta" href="javascript:comprobarAltaAnexo(false, false);">Alta FTP</a>
					</c:if>-->
					<!--  <a class="bot" id="btnAltaCpl" href="javascript:comprobarAltaAnexo(true, false);" style="display: none">Alta FTP</a>-->
				</td>
				<td align="center" width="30%">
					<a class="bot" id="btnVerPolizaActualizada" href="javascript:verPolizaActualizada();">Ver Póliza Actualizada</a>
					<!-- Pet. 50775 * MODIF TAM (08.06.2018) -->
					<!-- <a class="bot" id="btnVerRelacionModificaciones" href="javascript:verRelacionModificaciones();">Ver Relación de Modificaciones</a> -->
					<a class="bot" id="btnVerRelacionModificaciones" href="javascript:verRelacionModificaciones();">Ver Relación de Incidencias</a>
				</td>
				<td align="right">
					<a class="bot" id="btnAltaCupon" href="javascript:comprobarAltaAnexo(null, true, ${idPoliza}, ${anexoModificacion.poliza.estadoPoliza.idestado});">Alta</a>
					<a class="bot" id="btnVolver" href="javascript:volver();">Volver</a>
				</td>
			</tr>
		</table>
	</div>
	
	<!-- Contenido de la página -->
	<div class="conten" style="padding: 3px; width: 97%">
	
		<p class="titulopag" align="left">Declaraciones de Modificaci&oacute;n</p>
		
		<input type="hidden" name="idEstadoPlzAsociada" id="idEstadoPlzAsociada" value="${anexoModificacion.poliza.estadoPoliza.idestado}"/>
		
		<form:form name="main3" id="main3" action="declaracionesModificacionPolizaComplementaria.html" method="post" commandName="anexoModificacion">
			<input type="hidden" name="method" id="methodCpl"/>
			<input type="hidden" name="idAnexo" id="idAnexoCpl"/>
			<input type="hidden" name="idPoliza" id="idPolizaCpl" value="${idPoliza }"/>
			
			<form:hidden path="cupon.id" id="idCuponNumCpl"/>
			<form:hidden path="cupon.idcupon" id="idCuponCpl"/>
			<form:hidden path="cupon.cuponPrevio.cuponPpalPrevio" id="idCuponPpalPrevioCpl"/>
			<form:hidden path="cupon.cuponPrevio.estadoCuponPpalPrevio.id" id="estadoCuponPpalPrevioCpl"/>
			<form:hidden path="cupon.cuponPrevio.cuponCplPrevio" id="idCuponCplPrevioCpl"/>
			<form:hidden path="cupon.cuponPrevio.estadoCuponCplPrevio.id" id="estadoCuponCplPrevioCpl"/>
			
			<form:hidden path="cupon.estadoPlzPpalAgroseguro.id" id="idEstadoPlzPpalAgroseguroCpl"/>
			<form:hidden path="cupon.estadoPlzPpalAgroseguro.estado" id="estadoPlzPpalAgroseguroCpl"/>
		</form:form>
		<form name="imprimirAnexo" id="imprimirAnexo" action="polizaActualizada.html" method="post" >
			<input type="hidden" name="method" id="methodImprimir" value="doImprimirAnexoPpal"/>
			<input type="hidden" name="imprimirAnexoWS"  id="imprimirAnexoWS"  value="true"/>
			<input type="hidden" name="refPoliza" id="refPoliza" /> 
			<input type="hidden" name="idCuponImprimir" id="idCuponImprimir"/>
			<input type="hidden" name="idImprimir" id="idImprimir"/>
		</form>
		
		<form name="print" id="print" action="informes.html" method="post">
			<input type="hidden" name="method" id="methodPrint" value="doInformeAnexoModificacion"/>
			<input type="hidden" name="idAnexo" id="idAnexo"/>
			<input type="hidden" id="idPoliza" name="idPoliza" value="${idPoliza}"/>
		</form>
		
		<form name="printCpl" id="printCpl" action="informes.html" method="post">
			<input type="hidden" name="method" id="methodPrintCpl" value="doInformeAnexoModificacionComplementario"/>
			<input type="hidden" name="idAnexoCompl" id="idAnexoCompl"/>
			<input type="hidden" id="idPoliza" name="idPoliza" value="${idPoliza}"/>
		</form>
		
		<!-- Formulario para ver el borrador con el estado actual de la poliza en Agroseguro -->
		<form:form name="polizaActualizada" id="polizaActualizada" action="polizaActualizada.html" method="post" commandName="anexoModificacion" target="_blank" >
			<input type="hidden" id="methodPlzAct" name="method" value="doVerPolizaActualizada" />
			<form:hidden path="poliza.referencia" id="refPoliza"/>
			<form:hidden path="poliza.linea.codplan" id="codplan"/>
			<form:hidden path="poliza.idpoliza" id="idPoliza"/>
			<!--  Pet. 57626 ** MODIF TAM (23.06.2020) ** Inicio  -->
			<form:hidden path="poliza.tipoReferencia" id="tipoReferencia"/>
		</form:form>
		<!-- Formulario para vconsultar la relacion de incidencias asociadas a una poliza en agroseguro -->
		<form:form name="impresionIncidenciasMod" id="impresionIncidenciasMod" action="impresionIncidenciasMod.html" method="post" commandName="anexoModificacion">
			<input type="hidden" id="methodImpIncMod" name="method" value="doImprimirIncidencias" />
			<form:hidden path="poliza.linea.codlinea" id="codlinea"/>	
			<form:hidden path="poliza.linea.codplan" id="codplan"/>							
			<form:hidden path="poliza.linea.nomlinea" id="nomlinea"/>
			<form:hidden path="poliza.referencia" id="refPoliza"/>
			<form:hidden path="poliza.idpoliza" id="idPoliza"/>
			<input type="hidden" id="nombreCompleto" name="nombreCompleto" value="${poliza.asegurado.nombreCompleto}"/>
			<form:hidden path="poliza.codmodulo" id="codmodulo"/>
			<input type="hidden" id="fechaEnvio" name="fechaEnvio" value="${poliza.fechaenvio}"/>
			<input type="hidden" id="idCuponImpresion" name="idCuponImpresion"/>
		</form:form>
		
		
		<form:form name="main" id="main" action="declaracionesModificacionPoliza.html" method="post" commandName="anexoModificacion" >
			<input type="hidden" id="method" name="method" />
			<input type="hidden" id="idPoliza" name="idPoliza" value="${idPoliza}"/>
			<input type="hidden" id="tipoModo" name="tipoModo" value="${tipoModo}"/>
			<input type="hidden" id="modoLectura" name="modoLectura" value="${modoLectura}"/>
			<input type="hidden" id="idAnexoCaducado" name="idAnexoCaducado"/> 
			
			<form:hidden path="id" id="id"/>
			<form:hidden path="poliza.idenvio" id="idenvio"/>
			<form:hidden path="poliza.linea.codlinea" id="codlinea"/>	
			<form:hidden path="poliza.linea.codplan" id="codplan"/>							
			<form:hidden path="poliza.linea.nomlinea" id="nomlinea"/>
			<form:hidden path="poliza.referencia" id="refPoliza"/>
			<form:hidden path="poliza.idpoliza" id="idPoliza"/>
			<form:hidden path="poliza.tipoReferencia" id="tipoReferencia"/>
			
			<form:hidden path="cupon.id" id="idCuponNum"/>
			<form:hidden path="cupon.idcupon" id="idCupon"/>
			<form:hidden path="cupon.cuponPrevio.cuponPpalPrevio" id="idCuponPpalPrevio"/>
			<form:hidden path="cupon.cuponPrevio.estadoCuponPpalPrevio.id" id="estadoCuponPpalPrevio"/>
			<form:hidden path="cupon.cuponPrevio.cuponCplPrevio" id="idCuponCplPrevio"/>
			<form:hidden path="cupon.cuponPrevio.estadoCuponCplPrevio.id" id="estadoCuponCplPrevio"/>
			
			<form:hidden path="cupon.estadoPlzPpalAgroseguro.id" id="idEstadoPlzPpalAgroseguro"/>
			<form:hidden path="cupon.estadoPlzPpalAgroseguro.estado" id="estadoPlzPpalAgroseguro"/>
		</form:form>
		
		<form name="validarAnexo" id="validarAnexo" action="confirmacionModificacion.html" method="post">
			<input type="hidden" id="methodValidarAnexo" name="method" value="doValidarAnexo" />
			<input type="hidden" id="redireccion" name="redireccion" value="declaracionesAnexos"/>
			<input type="hidden" id="idCuponValidar" name="idCuponValidar"/>
		</form>
		
		<form name="acuseReciboConfirmacion" id="acuseReciboConfirmacion" action="confirmacionModificacion.html" method="post">
			<input type="hidden" name="method" id="methodAcuseRecConf" value="doVerAcuseConfirmacion"/>								
			<input type="hidden" name="idAnexoAcuse" id="idAnexoAcuse"/>
			<input type="hidden" name="idPolizaAcuse" id="idPolizaAcuse"/>
			<input type="hidden" name="idCuponAcuse" id="idCuponAcuse"/>
			<input type="hidden" name="redireccion" id="redireccion" value="declaracionesAnexos"/>
		</form>
		
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
		
		<!-- Datos de la póliza -->
		<fieldset style="width:95%">
		<legend class="literal">Datos de la p&oacute;liza</legend>
			<table width="100%" align="center" cellspacing="2">
				<tr>
					<td class="literal" width="40px">Plan:</td>
					<td width="40px" class="detalI">${poliza.linea.codplan}</td>
					<td class="literal" width="50px">Línea:</td>
					<td width="300px" class="detalI">${poliza.linea.codlinea } - ${poliza.linea.nomlinea}</td>
  					<td class="literal" width="75px">Asegurado:</td>
 					<td width="250px" class="detalI">${poliza.asegurado.nombreCompleto }</td> 
				</tr>
				<tr>
					<td class="literal" width="40px">Póliza:</td>
					<td width="40px" class="detalI">${poliza.referencia }</td>
					<td class="literal" width="50px">Módulo:</td>
					<td width="300px" class="detalI">${poliza.codmodulo}</td>		
					<td class="literal" width="70px">Fec. Envío:</td>
					<td width="100px" class="detalI"><fmt:formatDate pattern="dd/MM/yyyy" value="${poliza.fechaenvio}"/></td>	
				</tr>
			</table>								
		</fieldset>
		<br /><br />
		
		<div id="grid">
	        <display:table requestURI="declaracionesModificacionPoliza.html" id="listAnexosModificacion" class="LISTA" summary="Anexos" 
	        		name="${listAnexosModificacion}" sort="list" pagesize="10" excludedParams="method" defaultsort="5" defaultorder="ascending"
	        		decorator="com.rsi.agp.core.decorators.ModelTableDecoratorModificacionesPoliza" style="width:100%;border-collapse:collapse;">
	           	<display:column class="literal" headerClass="cblistaImg" title="Acciones" property="acciones" sortable="false" style="width:120px;text-align:center"/>
				<display:column class="literal" headerClass="cblistaImg" title="Modificaciones" property="anexModifPoliza"/>
				<display:column class="literal" headerClass="cblistaImg" title="Tipo" property="tipoEnvio" style="width:100px;"/>
				<display:column class="literal" headerClass="cblistaImg" title="Estado" property="estadoAnexModifPoliza" style="width:110px;"/>				
				<display:column class="literal" headerClass="cblistaImg" title="Fec. Alta" property="fechaAltaAM" sortable="true" sortProperty="fechaAlta" style="width:90px;"/>
				<display:column class="literal" headerClass="cblistaImg" title="Fec. Envío" property="fecEnvioAnexModifPoliza" style="width:90px;"/>
				<display:column class="literal" headerClass="cblistaImg" title="Imp. plz. final" property="importePlzFinal" style="width:90px;text-align:right"/>
				<display:column class="literal" headerClass="cblistaImg" title="Imp. modif." property="importeModificacion" style="width:80px;text-align:right"/>
				<display:column class="literal" headerClass="cblistaImg" title="Estado Cupón" property="cupon.estadoCupon.estado" style="width:150px;"/>
				<display:column class="literal" headerClass="cblistaImg" title="Estado Agroseguro" property="estRenovacionAgroseguro" style="width:150px;"/>
				<!--property="poliza.estadoPagoAgp.descripcion" property="estRenovacionAgroseguro"-->
	        </display:table>				
		</div>
	</div>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<%@ include file="/jsp/moduloUtilidades/anexoModificacion/popupEstadoContratacion.jsp"%>
	
</body>
</html>