<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
<head>
	<title>Listado de Explotaciones</title>
	
	<%@ include file="/jsp/common/static/metas.jsp"%>
	
	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
	
	<!-- JavaScript,jQery & AJAX -->
	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
	<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
	<script type="text/javascript" src="jsp/js/util.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
	<script type="text/javascript" src="jsp/js/terminos.js"></script>
	<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
	<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
	
	<script type="text/javascript" src="jsp/js/utilesIBAN.js"></script>
	<script type="text/javascript" src="jsp/moduloExplotaciones/explotaciones/listadoExplotacionesAnexo.js"></script>
	
	<%@ include file="/jsp/js/draggable.jsp"%>

<script>
<!--//		
// Para evitar el cacheo de peticiones al servidor
$(document).ready(function(){
	var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
	document.getElementById("main3").action = URL;
	document.getElementById("provincia").focus();
	<c:if test="${requestScope.modoLectura=='true' or requestScope.modoLectura=='modoLectura'}">
		$('#btnAlta').hide();
		//$('#btnRecalcular').hide();
		//$('#btnCambiarIBAN').hide();	
		
		$('#btnEnviar').hide();
	</c:if>

	// P0070105 ** MODIF TAM (10.03.2021) ** Inicio - //
	// Se solicita en RGA que se quite dicha validación y se muestre siempre el botón de Cambio de IBAN	
	//Si no lleva el IBAN informado no permitimos cambiarlo
	//if($('#ibanAsegOriginal').val()=='' && $('#iban2AsegOriginal').val()==''){
	//	$('#btnCambiarIBAN').hide();
	//}
	
	$('#btnCambiarDatosAsegurado').click(function(e) {
    e.preventDefault();
    jConfirm("¿Enviar los datos del asegurado existentes en Agroplus?", "Confirmación", function(result) {
        if(result) {
            // Si el usuario hace clic en "Aceptar"
            $('#validarAnexo [name=hayCambiosDatosAsegurado]').val('true');
			$('#datosExplotacionesAnexo [name=hayCambiosDatosAsegurado]').val('true');
			$('#frmSubvenciones [name=hayCambiosDatosAsegurado]').val('true');
			$('#formComparativas [name=hayCambiosDatosAsegurado]').val('true');
    		jAlert("Se van a enviar los datos del asegurado existentes en Agroplus", "Aviso");
        }
        
    	});
	});

	
	$('#buttons').show();
});

function eligeMenu() {
	if($('#vieneDeUtilidades').val() == 'true' && $('#modoLectura').val() == 'modoLectura'){
	 	SwitchMenu('sub4');
	 }else{
	 	SwitchMenu('sub3');
	 }
}

function cargarFiltro(){
	<c:forEach items="${sessionScope.consultaExplotacionesAnexo_LIMIT.filterSet.filters}" var="filtro">
		var inputText = document.getElementById('${filtro.property}');
		if (null!=inputText){
			inputText.value = '${filtro.value}';
		}
	</c:forEach>
}


//Va a la pantalla de cálculo de modificación en modo lectura
function verDC () {
	$("#formDistCoste").submit();
}

function mostrarComparativas() {
	$.blockUI.defaults.message = '<h4> Procesando comparativas del anexo.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	$('#formComparativas').submit();
}

//-->
</script>
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onLoad="cargarFiltro();SwitchMenu('sub4');">

	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

	<div id="buttons" style="padding:3px; width:97%" style="display:none;">
		<table width="100%" cellspacing="0" cellpadding="0" border="0" >
			<tr align="left">
				<td align="left" width="33%">
					<a class="bot" id="btnVolver" href="javascript:volver();" title="Volver">Volver</a>
					<a class="bot" id="btnSubvenciones" name="btnSubvenciones" href="javascript:subvenciones();" title="Subvenciones">Subvenciones</a>
					<c:if test="${mostrarImportes == true}">
						<a class="bot" id="btnImportes" name="btnImportes" href="javascript:verDC();" title="Ver distribuci&oacute;n de costes">Importes</a>
					</c:if>
				</td>
				<td align="center" width="34%">
					<a class="bot" id="btnAlta" href="javascript:altaExplotacionAnexo();" title="Alta de una nueva explotación">Alta</a>
					<a class="bot" id="btnConsultar" href="javascript:consultar();" title="Consultar explotaciones">Consultar</a>
					<a class="bot" id="btnLimpiar" href="javascript:limpiar();" title="Limpiar filtro de consulta">Limpiar</a>
				</td>
				<td align="right" width="33%">
					<c:if test="${mostrarBotonCoberturas}">
						<a class="bot" href="#" id="btnComparativas" onclick="javascript:mostrarComparativas();">Coberturas</a>
					</c:if>
					<c:if test="${mostrarBotonCambiarDatosAsegurado}">
						<a class="bot" href="#" id="btnCambiarDatosAsegurado" >Cambiar datos asegurado</a>
					</c:if>
					<a class="bot" id="btnCambiarIBAN" href="javascript:showPopUpCambioIBAN();">Cambiar IBAN</a>					
					<a class="bot" id="btnEnviar" href="javascript:validacionesPreviasEnvioAjax();" title="Enviar">Enviar</a>
				</td>
			</tr>
		</table>
	</div>

	<!-- Contenido de la página -->
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Listado de explotaciones</p>

	<!-- Datos de la póliza -->
		<fieldset style="width:95%;">
		<legend class="literal">Datos de la p&oacute;liza</legend>
			<table width="100%" align="center" cellspacing="2">
				<tr>
					<td class="literal" width="40px">Plan:</td>
					<td width="40px" class="detalI">${anexoModificacionBean.poliza.linea.codplan}</td>
					<td class="literal" width="50px">Línea:</td>
					<td width="300px" class="detalI">${requestScope.linea}</td>
  					<td class="literal" width="75px">Asegurado:</td>
 					<td width="250px" class="detalI">${requestScope.nombreAsegurado}</td> 
				</tr>
				<tr>
					<td class="literal" width="40px">Póliza:</td>
					<td width="40px" class="detalI">${requestScope.refPoliza}</td>
					<td class="literal" width="50px">Módulo:</td>
					<td width="300px" class="detalI">${requestScope.codModulo}</td>		
					<td class="literal" width="70px">Fec. Envío:</td>
					<td width="100px" class="detalI"><fmt:formatDate pattern="dd/MM/yyyy" value="${requestScope.fechaEnvio}"/></td>	
				</tr>
			</table>								
		</fieldset>
			
			<form name="frmSubvenciones" id="frmSubvenciones" action="subvencionAseguradoAnexoMod.html" method="post">
				<input type="hidden" name="method" id="methodSubvenciones" value="doConsulta"/>
				<input type="hidden" name="idAnexoModificacion" id="idAnexoModificacionSubvenciones" value="${requestScope.idAnexo}" />
				<input type="hidden" name="modoLectura" id="modoLecturaSubvenciones" value="${modoLectura}"/>
				<input type="hidden" id="vieneDeListadoAnexosModSubvenciones" name="vieneDeListadoAnexosMod" value="${vieneDeListadoAnexosMod}"/>
				<input type="hidden" name="hayCambiosDatosAsegurado" id="hayCambiosDatosAsegurado" value="${hayCambiosDatosAsegurado}" />
			</form>
			
			<form name="volverConsultaAnexos" id="volverConsultaAnexos" action="anexoModificacionUtilidades.run" method="post">
				<input type="hidden" id="method" name="method" value="doConsulta"/>
			</form>
			
			<form name="volverDeclaracionesModificacionPoliza" id="volverDeclaracionesModificacionPoliza" action="declaracionesModificacionPoliza.html" method="post">
				<input type="hidden" name="idPoliza" id="idPoliza"/>
			</form>
			
			<form name="validarAnexo" id="validarAnexo" action="confirmacionModificacion.html" method="post">
				<input type="hidden" id="method" name="method" value="doValidarAnexo" />
				<input type="hidden" id="redireccion" name="redireccion" value="explotaciones"/>
				<input type="hidden" id="idCuponValidar" name="idCuponValidar"/>
				<input type="hidden" name="vieneDeListadoAnexosMod" id="vieneDeListadoAnexosMod" value="${requestScope.vieneDeListadoAnexosMod}"/>
				<input type="hidden" name="hayCambiosDatosAsegurado" id="hayCambiosDatosAsegurado" value="${hayCambiosDatosAsegurado}" />
			</form>
			
			<!-- Formulario para la redirección a la pantalla de alta/baja/modificación de explotaciones -->
			<form:form name="datosExplotacionesAnexo" id="datosExplotacionesAnexo" action="datosExplotacionesAnexo.html" method="post" commandName="anexoModificacionBean">
				<input type="hidden" name="method" id="method"/>
				<input type="hidden" name="anexoModificacionId" id="anexoModificacionId"/>
				<input type="hidden" name="explotacionAnexoId" id="explotacionAnexoId"/>
				<input type="hidden" name="operacion" id="operacion"/>
				<input type="hidden" name="modoLectura" id="modoLectura" value="${requestScope.modoLectura }"/>
				<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${requestScope.vieneDeUtilidades}"/>
				<input type="hidden" name="vieneDeListadoAnexosMod" id="vieneDeListadoAnexosMod" value="${requestScope.vieneDeListadoAnexosMod}"/>
				<input type="hidden" name="primerAcceso" id="primerAcceso" value="SI"/>
				<input type="hidden" name="hayCambiosDatosAsegurado" id="hayCambiosDatosAsegurado" value="${hayCambiosDatosAsegurado}" />
				
				<!-- bean -->
				<form:hidden path="poliza.linea.lineaseguroid" id="lineaseguroid"/>
				<form:hidden path="poliza.linea.codlinea" id="codlinea"/>
				<input type="hidden" id="fechaInicioContratacion" name="fechaInicioContratacion" value="${fechaInicioContratacion}" />
				<form:hidden path="poliza.linea.codplan" id="codplan"/>
				<form:hidden path="poliza.idpoliza" id="idPolizaExplotaciones"/>
				<form:hidden path="poliza.clase" id="idclase"/>
			</form:form>

			<form:form name="main3" id="main3" action="listadoExplotacionesAnexo.html" method="post" commandName="anexoModificacionBean">	

			<!-- generales -->
			<input type="hidden" name="explotacionAnexoId" id="explotacionAnexoId"/>
			<input type="hidden" name="operacion" id="operacion"/>
			<input type="hidden" name="limpiar" id="limpiar"/>
			<input type="hidden" name="modoLectura" id="modoLectura" value="${requestScope.modoLectura }"/>
			<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${requestScope.vieneDeUtilidades }"/>
			<input type="hidden" name="vieneDeListadoAnexosMod" id="vieneDeListadoAnexosMod" value="${requestScope.vieneDeListadoAnexosMod}"/>
			<input type="hidden" name="origenLlamada" name="origenLlamada" value="listaExplotacionesAnexo"/>
			<input type="hidden" name="nifCif_cm" id="nifCif_cm" value="${sessionScope.usuario.asegurado.nifcif}"/>
			<form:hidden path="cupon.id" id="idCupon"/>
			<form:hidden path="cupon.idcupon" id="idCuponStr"/>
			
			<!-- bean -->
			<form:hidden path="poliza.linea.lineaseguroid" id="lineaseguroid"/>
			<form:hidden path="poliza.linea.codplan" id="codplan"/>
			<form:hidden path="poliza.idpoliza" id="idpoliza"/>
			<form:hidden path="poliza.clase" id="idclase"/>
			<form:hidden path="poliza.linea.codlinea" id="codlinea"/>
			<input type="hidden" id="fechaInicioContratacion" name="fechaInicioContratacion" value="${fechaInicioContratacion}" />
			<form:hidden path="id" id="anexoModificacionId"/>
			<form:hidden path="ibanAsegOriginal" id="ibanAsegOriginal"/>
			<form:hidden path="esIbanAsegModificado" id="esIbanAsegModificado"/>
			<form:hidden path="ibanAsegModificado" id="ibanAsegModificado"/>
			<!-- Pet. 70105 - Fase III (REQ.05) - MODIF TAM (23/02/2021) * Inicio -->
			<form:hidden path="iban2AsegOriginal" id="iban2AsegOriginal"/>
			<form:hidden path="esIban2AsegModificado" id="esIban2AsegModificado"/>
			<form:hidden path="iban2AsegModificado" id="iban2AsegModificado"/>			

			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			<%@ include file="/jsp/moduloExplotaciones/explotaciones/filtroExplotacionesAnexo.jsp"%>
			</form:form>
			
			<form:form name="formDistCoste" id="formDistCoste" action="calculoModificacion.html" method="post" commandName="anexoModificacionBean" >
				<input type="hidden" id="method" name="method" value="doConsultaDistCoste" />
				<input type="hidden" id="vieneDeListadoAnexosMod" name="vieneDeListadoAnexosMod" value="${requestScope.vieneDeListadoAnexosMod}" />
				<input type="hidden" id="redireccion" name="redireccion" value="explotaciones" />
				<form:hidden id="idPolizaVerDC" path="id"/>
				<input type="hidden" name="hayCambiosDatosAsegurado" id="hayCambiosDatosAsegurado" value="${hayCambiosDatosAsegurado}" /> 
			</form:form>
			
			<!-- Formulario para ir a la pantalla de comparativas -->
			<form:form method="post" name="formComparativas" id="formComparativas" action="seleccionComparativasAnexoSW.html" commandName="anexoModificacionBean">
				<input type="hidden" id="method" name="method" value="doMostrarComparativasAnexo" />
				<input type="hidden" id="modoLecturaComparativas" name="modoLectura" value="${requestScope.modoLectura}" />
				<form:hidden path="id" id="idAnexoComparativas"/>				
				<form:hidden path="poliza.idpoliza" id="idAnexoComparativas"/>
				<input type="hidden" name="vieneDeListadoAnexosMod" id="vieneDeListadoAnexosModComparativas" value="${requestScope.vieneDeListadoAnexosMod}"/>
				<input type="hidden" name="hayCambiosDatosAsegurado" id="hayCambiosDatosAsegurado" value="${hayCambiosDatosAsegurado}" /> 
				
			</form:form>
			
		<br />
	
		<div id="grid" style="width: 98%">
	  		${consultaExplotacionesAnexo}		  							               
		</div> 	

		<br />
	</div>
	
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
	<!-- *** popUp cambio IBAN *** -->
	<div id="divCambioIBAN" class="parcelasRepWindow"  style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;top: 170px;  width:680px">
		<!--  header popup -->
		<div id="header-popup" style="padding:0.4em 1em;position:relative;
		                              color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;
		                              background:#525583;height:15px">
			<div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">Cambiar IBAN</div>
			<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
				      font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
				<span onclick="cerrarPopUpCambioIBAN()">x</span>
			</a>
		</div>
		<div id="cambioIBANPopUpError" class="literal" style="color:red;display:none;text-align:center;background-color:white">
	       &nbsp;
	    </div>
		<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelInformacion_IBAN" class="panelInformacion">
				
				<table>
			        <tr>
			            <td class="literal">Antiguo IBAN:</td>
			            <td  class="detalI" id="ibanAntiguo">&nbsp;
			            </td>
			        </tr>
			        <tr>
			            <td class="literal">Nuevo IBAN:</td>
			            <td>
				            <c:if test="${requestScope.modoLectura!='true' && requestScope.modoLectura!='modoLectura'}">
				            	<input type="hidden" id="ibanCompleto" name="ibanCompleto"/>
								<input type="text" id="iban" name="iban" size="4" maxlength="4" class="dato" 	   	onKeyup="autotab(this, document.getElementById('cuenta1'));" onChange="this.value=this.value.toUpperCase();"/>
								<input type="text" id="cuenta1" name="cuenta1" size="4" maxlength="4" class="dato"  onKeyup="autotab(this, document.getElementById('cuenta2'));"/>
								<input type="text" id="cuenta2" name="cuenta2" size="4" maxlength="4" class="dato"  onKeyup="autotab(this, document.getElementById('cuenta3'));"/>
								<input type="text" id="cuenta3" name="cuenta3" size="4" maxlength="4" class="dato"  onKeyup="autotab(this, document.getElementById('cuenta4'));"/>
								<input type="text" id="cuenta4" name="cuenta4" size="4" maxlength="4" class="dato"  onKeyup="autotab(this, document.getElementById('cuenta5'));"/>
								<input type="text" id="cuenta5" name="cuenta5" size="4" maxlength="4" class="dato"/>
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_ccc"> *</label>
				            </c:if>
							<c:if test="${requestScope.modoLectura=='true' || requestScope.modoLectura=='modoLectura'}">
								<input type="hidden" id="ibanCompleto" name="ibanCompleto"/>
								<input type="text" id="iban" name="iban" size="4" maxlength="4" class="dato" readonly/>
								<input type="text" id="cuenta1" name="cuenta1" size="4" maxlength="4" class="dato" readonly/>
								<input type="text" id="cuenta2" name="cuenta2" size="4" maxlength="4" class="dato" readonly/>
								<input type="text" id="cuenta3" name="cuenta3" size="4" maxlength="4" class="dato" readonly/>
								<input type="text" id="cuenta4" name="cuenta4" size="4" maxlength="4" class="dato" readonly/>
								<input type="text" id="cuenta5" name="cuenta5" size="4" maxlength="4" class="dato" readonly/>
								
							</c:if>
			            </td>
			        </tr>
			       <!-- Pet. 70105 - Fase III (REQ.05) - MODIF TAM (23/02/2021) * Inicio  -->
			       <tr>
			   			<td class="literal">Antiguo IBAN Cobro Siniestro:</td>
			            	<td  class="detalI" id="iban2Antiguo">&nbsp;
			            </td>
				   </tr>
			        <tr>
			            <td class="literal">Nuevo IBAN Cobro Siniestro:</td>
			            <td>
				            <c:if test="${requestScope.modoLectura!='true' && requestScope.modoLectura!='modoLectura'}">
				            	<input type="hidden" id="iban2Completo" name="iban2Completo"/>
								<input type="text" id="iban2" name="iban2" size="4" maxlength="4" class="dato" 	   	onKeyup="autotab(this, document.getElementById('iban2_cuenta1'));" onChange="this.value=this.value.toUpperCase();"/>
								<input type="text" id="iban2_cuenta1" name="iban2_cuenta1" size="4" maxlength="4" class="dato"  onKeyup="autotab(this, document.getElementById('iban2_cuenta2'));"/>
								<input type="text" id="iban2_cuenta2" name="iban2_cuenta2" size="4" maxlength="4" class="dato"  onKeyup="autotab(this, document.getElementById('iban2_cuenta3'));"/>
								<input type="text" id="iban2_cuenta3" name="iban2_cuenta3" size="4" maxlength="4" class="dato"  onKeyup="autotab(this, document.getElementById('iban2_cuenta4'));"/>
								<input type="text" id="iban2_cuenta4" name="iban2_cuenta4" size="4" maxlength="4" class="dato"  onKeyup="autotab(this, document.getElementById('iban2_cuenta5'));"/>
								<input type="text" id="iban2_cuenta5" name="iban2_cuenta5" size="4" maxlength="4" class="dato"/>
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_ccc2"> *</label>
				            </c:if>
							<c:if test="${requestScope.modoLectura=='true' || requestScope.modoLectura=='modoLectura'}">
								<input type="hidden" id="iban2Completo" name="iban2Completo"/>
								<input type="text" id="iban2" name="iban2" size="4" maxlength="4" class="dato" readonly/>
								<input type="text" id="iban2_cuenta1" name="iban2_cuenta1" size="4" maxlength="4" class="dato" readonly/>
								<input type="text" id="iban2_cuenta2" name="iban2_cuenta2" size="4" maxlength="4" class="dato" readonly/>
								<input type="text" id="iban2_cuenta3" name="iban2_cuenta3" size="4" maxlength="4" class="dato" readonly/>
								<input type="text" id="iban2_cuenta4" name="iban2_cuenta4" size="4" maxlength="4" class="dato" readonly/>
								<input type="text" id="iban2_cuenta5" name="iban2_cuenta5" size="4" maxlength="4" class="dato" readonly/>
								
							</c:if>
			            </td>
			        </tr>
	   			</table>
				
				<div style="margin-top:15px;clear: both">
					<c:if test="${requestScope.modoLectura=='true' || requestScope.modoLectura=='modoLectura'}">
						<a class="bot" href="javascript:cerrarPopUpCambioIBAN()">Cerrar</a>
					</c:if>
					<c:if test="${requestScope.modoLectura!='true' && requestScope.modoLectura!='modoLectura'}">
						<a class="bot" href="javascript:cambiarIBAN()">Aceptar</a>
				    	<a class="bot" href="javascript:cerrarPopUpCambioIBAN()">Cancelar</a>
					</c:if>
				</div>
			</div>
		</div>
	</div>
	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>

	<%@ include file="/jsp/common/lupas/lupaProvincia.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaComarca.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaTermino.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEspecie.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaRegimen.jsp"%>
</body>
</html>