<%@ include file="/jsp/common/static/setHeader.jsp"%>
<%@ include file="/jsp/common/static/taglibs.jsp" %>

<html>
<head>
<title>Cálculo de la modificación</title>

        <%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />

		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/moduloUtilidades/modificacionesPoliza/calculoModificacion.js"></script>
		<%@ include file="/jsp/js/draggable.jsp"%>				
		<%@ include file="/jsp/moduloUtilidades/modificacionesPoliza/utilEnviosAM.jsp"%>
</head>


<c:if test="${vieneDeRelacionModfInc eq 1}">
	<script>
	function volver () {
		$("#impresionIncidenciasMod").submit();
	}
	</script>
</c:if>

<c:if test="${empty vieneDeRelacionModfInc}">
	<script>
	function volver () {
		// Se indica que el método a instanciar es 'doVolver', ya que este formulario también se utiliza para confirmar
		$('#methodContinuar').val('doVolver');
		$('#continuar').submit(); 
	}
	</script>
</c:if>




<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub3');">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		
		<!-- FORMULARIOS -->
		<!-- Formulario para volver a la consulta de relación de incidencias asociadas a una póliza en Agroseguro -->
		<form:form name="impresionIncidenciasMod" id="impresionIncidenciasMod" action="impresionIncidenciasMod.html" method="post" commandName="anexoModificacion">
			<input type="hidden" id="methodImpIncMod" name="method" value="doImprimirIncidencias" />
			<form:hidden path="poliza.linea.codlinea" id="codlinea"/>	
			<form:hidden path="poliza.linea.codplan" id="codplan"/>							
			<form:hidden path="poliza.linea.nomlinea" id="nomlinea"/>
			<form:hidden path="poliza.referencia" id="refPoliza"/>
			<form:hidden path="poliza.idpoliza" id="idPoliza"/>
			<input type="hidden" id="nombreCompleto" name="nombreCompleto" value="${anexoModificacion.poliza.asegurado.nombreCompleto}"/>
			<form:hidden path="poliza.codmodulo" id="codmodulo"/>
			<input type="hidden" id="fechaEnvio" name="fechaEnvio" value="${anexoModificacion.poliza.fechaenvio}"/>
		</form:form>
		
		<!-- Formulario para confirmar el anexo o volver a las pantallas desde las que se ha solicitado el envío  -->
		<form:form action="confirmacionModificacion.html" name="continuar" id="continuar"  method="post" commandName="anexoModificacion">
			<input type="hidden" id="methodContinuar" name="method" value="doConfirmarAnexo"/>
			<input type="hidden" id="redireccion" name="redireccion" value="${redireccion}"/>
			<input type="hidden" id="errorTramite" name="errorTramite" value="${errorTramite}"/>
			<input type="hidden" id="perfil34" name="perfil34" value="${perfil34}"/>
			<input type="hidden" id="indRevAdm" name="indRevAdm" value="N"/>
			<input type="hidden" id="volver" name="volver" />
			<input type="hidden" name="vieneDeListadoAnexosMod" id="vieneDeListadoAnexosMod" value="${requestScope.vieneDeListadoAnexosMod}"/>
			<form:hidden path="id" id="idAnexo"/>
			<input type="hidden" id="modoLectura" name="modoLectura" value="${modoLectura}"/>
			<form:hidden path="poliza.idpoliza" id="idPoliza"/>
			<form:hidden path="cupon.id" id="idCupon"/>
			<input type="hidden" name="muestraBotonDescuentos" id="muestraBotonDescuentos" value="${muestraBotonDescuentos}" />
			<input type="hidden" name="muestraBotonRecargos" id="muestraBotonRecargos" value="${muestraBotonRecargos}" />
			<input type="hidden" name="validarRango" id="validarRango" value="${validarRango}" />
			<input type="hidden" name="hayCambiosDatosAsegurado" id="hayCambiosDatosAsegurado" value="${hayCambiosDatosAsegurado}" />
		</form:form>

		<!-- Botonera -->
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="center">
						<a class="bot" href="#" id="btnDescuentos" onclick="javascript:openPopupDescuentos();" style="display: none">Descuentos</a>
						<a class="bot" href="#" id="btnRecargos" onclick="javascript:openPopupRecargos();" style="display: none">Recargos</a>
					</td>
					
					<td align="right">
						<c:choose>
							<c:when test="${not empty listaIncidenciasAgro}">
								<a class="bot" href="${listaIncidenciasAgro}" >Volver</a>
							</c:when>
							<c:otherwise>
								<a class="bot" href="#" onClick="javascript:volver();">Volver</a>
							</c:otherwise>
						</c:choose>
						<c:if test="${empty modoLectura}">
							<a class="bot" href="#" onClick="javascript:confirmarAnexo(${errorTramite}, ${perfil34});">Enviar</a>
						</c:if>						
					</td>
				</tr>
			</table>
		</div>
		
		
		
		<!-- Contenido de la página -->
		<div id="calculoModificacion">	
			<div class="conten" style="padding:3px;width:97%">
				<p class="titulopag" align="left">CÁLCULO DE LA MODIFICACIÓN</p>
				
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				
				<!-- Sólo se muestran las distribuciones de coste si el cálculo ha sido correcto -->
				<c:if test="${empty requestScope.alerta}">
				
					<!-- Mensaje informativo -->
					<div id="panelInformacion" style="width:500px;margin: 0 auto;color:black;border:1px solid #FFCD00;display:block;
					font-size:12px;font-style:italic;font-weight:bold;line-height:20px;background-color:#FCF6CF">
						ATENCI&Oacute;N: La distribuci&oacute;n de costes obtenida es provisional. La distribuci&oacute;n definitiva se podr&aacute;
						consultar cuando Agroseguro emita el recibo correspondiente
					</div>
					
					</br>
					
					<!-- Tabla de distribución de costes del cálculo de la modificación -->
					<jsp:include page="/jsp/moduloUtilidades/modificacionesPoliza/tablaDistribucionCostes.jsp">
						<jsp:param value="0" name="tipoDC"/>
					</jsp:include>
					
					</br></br>
					
					<!-- Tabla de distribución de costes de la diferencia con respecto a la póliza -->
					<jsp:include page="/jsp/moduloUtilidades/modificacionesPoliza/tablaDistribucionCostes.jsp">
						<jsp:param value="1" name="tipoDC"/>
					</jsp:include>
				
				</c:if>
				
			</div>	
		</div>	

		<%@ include file="/jsp/common/static/piePagina.jsp"%>	
		<%@ include	file="/jsp/moduloUtilidades/anexoModificacion/popupDescuentosAnexo.jsp"%>
		<%@ include	file="/jsp/moduloUtilidades/anexoModificacion/popupRecargosAnexo.jsp"%>
		<%@ include file="/jsp/moduloUtilidades/anexoModificacion/popupConfirmacion.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		
		
</body>
</html>