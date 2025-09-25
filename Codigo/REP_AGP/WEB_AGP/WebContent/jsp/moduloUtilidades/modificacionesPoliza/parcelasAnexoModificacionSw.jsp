<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<fmt:bundle basename="displaytag" >
	<c:set var="numReg"><fmt:message key="numElementsPag"/></c:set>
</fmt:bundle>

<fmt:bundle basename="agp" >
	<c:set var="numEle"><fmt:message key="visores.numElements"/></c:set>
</fmt:bundle>

<html>
<head>
	<title>Parcelas Anexo Modificación</title>
	
	<%@ include file="/jsp/common/static/metas.jsp"%>
	
	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
	 
	<!-- JavaScript,jQery & AJAX -->
	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
	<script type="text/javascript" src="jsp/js/util.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
	<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
    <script type="text/javascript" src="jsp/js/additional-methods.js"></script>
    <script type="text/javascript" src="jsp/js/terminos.js"></script>	
    <script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
    <script type="text/javascript" src="jsp/js/lupaGenericaIN.js"></script>
    <script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
    <script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
    <script type="text/javascript" src="jsp/js/calendar.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
	<script type="text/javascript" src="jsp/moduloUtilidades/modificacionesPoliza/parcelasAnexoModificacion.js"></script>
	<script type="text/javascript" src="jsp/moduloUtilidades/modificacionesPoliza/parcelasAnexoModificacionSw.js"></script>
	<script type="text/javascript" src="jsp/js/utilesIBAN.js"></script>
	
	<script>
	$(document).ready(function(){
		$('#btnCambiarDatosAsegurado').click(function(e) {
		    e.preventDefault();
		    jConfirm("¿Enviar los datos del asegurado existentes en Agroplus?", "Confirmacion", function(result) {
		        if(result) {
		            // Si el usuario hace clic en "Aceptar"
		            $('#validarAnexo [name=hayCambiosDatosAsegurado]').val('true');
					$('#main3 [name=hayCambiosDatosAsegurado]').val('true');
					$('#frmSubvenciones [name=hayCambiosDatosAsegurado]').val('true');
		    		jAlert("Se van a enviar los datos del asegurado existentes en Agroplus", "Aviso");
		        }
		        
		    	});
			});

	});
	</script>
	
	<%@ include file="/jsp/js/draggable.jsp"%>
	<%@ include file="/jsp/moduloUtilidades/modificacionesPoliza/parcelasAnexoModificacionSwJsDinamico.jspf" %>	
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4');changeColorRow();">
	<%@ include file="../../common/static/cabecera.jsp"%>
	<%@ include file="../../common/static/menuGeneral.jsp"%>
	<%@ include file="../../common/static/datosCabeceraTaller.jsp" %>	
<!-- Botones para alta, imprimir y volver -->
	<div id="buttons">
		<table width="100%" cellspacing="2" cellpadding="2" border="0">
			<tr>
				<td align="left">
					<a class="bot" id="btnVolver" name="btnVolver" href="javascript:volver();" title="Coberturas">Coberturas</a>
					<a class="bot" id="btnSubvenciones" name="btnSubvenciones" href="javascript:subvenciones();" title="Subvenciones">Subvenciones</a>
					<c:if test="${mostrarImportes == true}">
						<a class="bot" id="btnImportes" name="btnImportes" href="javascript:verDC();" title="Ver distribuci&oacute;n de costes">Importes</a>
					</c:if>
					 <c:if test="${tieneRdtoHist=='true'}">
				    	   <a class="bot" id="btnRdtoHistorico"  href="javascript:calculoRdtoHist();" title="Rendimiento histórico">Rdto. Histórico</a>
				    	   <%-- P0078877 ** MODIF TAM (25.10.2021) ** Inicio --%>
				    	   <a class="bot" id="btnRdtoOrientativo"  href="javascript:calcRdtoOrientativo();" title="Rendimiento Orientativo">Rdto. Orientativo</a>
				    	   
					 </c:if>
				</td>
				<td>
					<c:if test="${modoLectura != 'true'}">
					    <a class="bot" id="btnAlta" name="btnAlta" href="javascript:altaParcela();" title="Alta de una nueva parcela">Alta</a>
					    <a class="bot" id="btnDeshacer" name="btnDeshacer" href="javascript:deshacerCambiosParcela('');" title="Deshacer los cambios">Deshacer</a>
					    <a class="bot" id="btnBaja" name="btnBaja" href="javascript:eliminarParcela('','');" title="Eliminar parcelas">Baja</a>
					    <a class="bot" id="btnCambioMasivo" href="javascript:cambioMasivo();" title="Cambio masivo">Cambio masivo</a>
					</c:if>
					<a class="bot" id="btnCambiarIBAN" href="javascript:showPopUpCambioIBAN();">Cambiar IBAN</a>
					
				</td>
				<td>
					<a class="bot" id="btnConsultar" name="btnConsultar" href="javascript:consultarParcelas();" title="Consultar parcelas">Consultar</a>
				    <a class="bot" id="btnLimpiar" name="btnLimpiar" href="javascript:limpiar();" title="Limpiar filtro de consulta">Limpiar</a>
				</td>
				<td align="right">
					<a class="bot" id="btnImprimir" name="btnImprimir" href="javascript:imprimir();" title="Imprimir borrador">Imprimir</a>
					<c:if test="${modoLectura != 'true'}">
						<c:if test="${mostrarBotonCambiarDatosAsegurado}">
							<a class="bot" href="#" id="btnCambiarDatosAsegurado" >Cambiar datos asegurado</a>
						</c:if>			
						<a class="bot" id="btnEnviar" name="btnEnviar" href="javascript:enviar();" title="Enviar a Agroseguro">Enviar</a>
					</c:if>
				    <c:choose>
						<c:when test="${vieneDeListadoAnexosMod != 'true'}">
							<a class="bot" id="btnVolverAnexo"  name="btnVolverAnexo"  href="javascript:volverAnexo();"                  title="Volver a Anexo de Modificacion" tabindex="36">Volver Anexo</a>
						</c:when>
						<c:otherwise>
							<a class="bot" id="btnVolverAnexoListado"  name="btnVolverAnexoListado"  href="javascript:volverAnexoListado();"  title="Volver a Utilidades Anexo " tabindex="36">Volver a Utilidades Anexo</a>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</table>
	</div>

<!-- Contenido de la página -->
<div class="conten" style="padding: 3px; width: 100%;">
	<p class="titulopag" align="left">Modificación de parcelas</p>
	<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
	<!-- Datos de la póliza -->
		<fieldset style="width:97%;">
		<legend class="literal">Datos de la p&oacute;liza</legend>
			<table width="100%" align="center" cellspacing="2">
				<tr>
					<td class="literal" width="75px">Colectivo:</td>
 					<td class="detalI">${poliza.colectivo.idcolectivo } - ${poliza.colectivo.nomcolectivo }</td> 
					<td class="literal" width="40px">Plan:</td>
					<td class="detalI">${poliza.linea.codplan}</td>
					<td class="literal" width="50px">Línea:</td>
					<td class="detalI">${poliza.linea.codlinea } - ${poliza.linea.nomlinea}</td>
  					
				</tr>
				<tr>
					<td class="literal" width="75px">Asegurado:</td>
 					<td class="detalI">${poliza.asegurado.nombreCompleto }</td> 
					<td class="literal" width="40px">Póliza:</td>
					<td class="detalI">${poliza.referencia }</td>
					<td class="literal" width="50px">Módulo:</td>
					<td class="detalI">${poliza.codmodulo}</td>		
				</tr>
			</table>								
		</fieldset>
		
		
	<form name="volverUtilidadesAnexos" id="volverUtilidadesAnexos" action="anexoModificacionUtilidades.run" method="post">
		<input type="hidden" name="method" id="methodUtilidades" value="doConsulta"/>
		<input type="hidden" name="volver" id="volver" value="true"/>
	</form>
	
	<form:form name="imprimirAnexo" id="imprimirAnexo" action="polizaActualizada.html" method="post" commandName="capitalAseguradoModificadaBean">
		<input type="hidden" name="method" id="methodImprimir" value="doImprimirAnexoPpal"/>
		<input type="hidden" name="imprimirAnexoWS"  id="imprimirAnexoWS"  value="true"/>
		<input type="hidden" name="refPoliza" id="refPoliza" value="${poliza.referencia}"/> 
		<form:hidden path="parcela.anexoModificacion.cupon.idcupon" id="idCuponImprimir"/>
		<form:hidden path="parcela.anexoModificacion.id" id="idImprimir"/>
	</form:form>
	
	<form name="frmSubvenciones" id="frmSubvenciones" action="subvencionAseguradoAnexoMod.html" method="post">
		<input type="hidden" name="method" id="methodSubvenciones" value="doConsulta"/>
		<input type="hidden" name="idAnexoModificacion" id="idAnexoModificacionSubvenciones" value="${requestScope.idAnexo}" />
		<input type="hidden" name="modoLectura" id="modoLecturaSubvenciones" value="${modoLectura}"/>
		<input type="hidden" id="vieneDeListadoAnexosModSubvenciones" name="vieneDeListadoAnexosMod" value="${vieneDeListadoAnexosMod}"/>
		<input type="hidden" name="hayCambiosDatosAsegurado" id="hayCambiosDatosAsegurado" value="${hayCambiosDatosAsegurado}" /> 
	</form>
	
	<form name="validarAnexo" id="validarAnexo" action="confirmacionModificacion.html" method="post">
			<input type="hidden" id="methodValidarAnexo" name="method" value="doValidarAnexo" />
			<input type="hidden" id="redireccion" name="redireccion" value="parcelas"/>
			<input type="hidden" id="idCuponValidar" name="idCuponValidar"/>
			<input type="hidden" name="hayCambiosDatosAsegurado" id="hayCambiosDatosAsegurado" value="${hayCambiosDatosAsegurado}" /> 
	</form>
	
	<form:form name="formDistCoste" id="formDistCoste" action="calculoModificacion.html" method="post" commandName="anexoModificacionBean" >
		<input type="hidden" id="methodDistCoste" name="method" value="doConsultaDistCoste" />
		<input type="hidden" id="vieneDeListadoAnexosMod" name="vieneDeListadoAnexosMod" value="${vieneDeListadoAnexosMod}" />
		<input type="hidden" id="redireccion" name="redireccion" value="parcelas" />
		<form:hidden id="idPolizaVerDC" path="id"/>
	</form:form>
	
	<form:form name="main3" id="main3" action="parcelasAnexoModificacion.html" method="post" commandName="capitalAseguradoModificadaBean" >
		<input type="hidden" name="method"              id="method"              value=""/>
		<input type="hidden" name="idAnexoModificacion" id="idAnexoModificacion" value="${requestScope.idAnexo}" />
	    <input type="hidden" name="codPoliza"           id="codPoliza"           value=""/>
		<input type="hidden" name="codParcela"          id="codParcela"          value=""/>
		<input type="hidden" name="tipoBajaParcela"     id="tipoBajaParcela"     value=""/>
		<input type="hidden" name="tipoParcela"         id="tipoParcela"         value=""/>
		<input type="hidden" name="idPoliza"            id="idPoliza"            value="${poliza.idpoliza}"/>
		<input type="hidden" name="lineaseguroid"       id="lineaseguroid"       value="${lineaseguroid}"/>
		<input type="hidden" name="itemCombo"           id="itemCombo"           value="${itemCombo}"/>
		<input type="hidden" name="modoLectura"         id="modoLectura"         value="${modoLectura}"/>
		<input type="hidden" name="idsRowsChecked"      id="idsRowsChecked"      value="${idsRowsChecked}"/>
		<input type="hidden" name="idsCapAsegRowsChecked"      id="idsCapAsegRowsChecked"      value="${idsCapAsegRowsChecked}"/>
		<input type="hidden" name="parcelasString"      id="parcelasString"      value="${parcelasString}"/>
		<input type="hidden" name="capAsegString"      id="capAsegString"      value="${capAsegString}"/>
		<input type="hidden" name="marcarTodosChecks"   id="marcarTodosChecks"   value="${marcarTodosChecks}"/>
		<input type="hidden" name="isClickInListado"    id="isClickInListado" />
		<input type="hidden" id="vieneDeListadoAnexosMod" name="vieneDeListadoAnexosMod" value="${vieneDeListadoAnexosMod}"/>
		
		<input type="hidden"  id="codlinea" name="codlinea"  value="${poliza.linea.codlinea}" />
		<input type="hidden"  id="fechaInicioContratacion" name="fechaInicioContratacion"  value="${poliza.linea.fechaInicioContratacion}" />
		<input type="hidden"  id="codplan" 	name="codplan"  value="${poliza.linea.codplan}" />	
		<input type="hidden" name="nifCif_cm"                id="nifCif_cm"        value="${poliza.asegurado.nifcif}"/>
		<!-- Valor depende de la lupa tipo de capital -->
		<input type="hidden" id="codconcepto" name="codconcepto" value="126"/>
		
		
		<!--  Pet. 70105 - Fase III (REQ.05) - MODIF TAM (24/02/2021) * Inicio -->
		<input type="hidden" name="ibanAsegOriginal" id="ibanAsegOriginal" value="${ibanAsegOriginal}"/>
		<input type="hidden" name="esIbanAsegModificado" id="esIbanAsegModificado" value="${esIbanAsegModificado}"/>
		<input type="hidden" name="ibanAsegModificado" id="ibanAsegModificado" value="${ibanAsegModificado}"/>
		
		<input type="hidden" name="iban2AsegOriginal" id="iban2AsegOriginal" value="${iban2AsegOriginal}"/>
		<input type="hidden" name="esIban2AsegModificado" id="esIban2AsegModificado" value="${esIban2AsegModificado}"/>
		<input type="hidden" name="iban2AsegModificado" id="iban2AsegModificado" value="${iban2AsegModificado}"/>
		<!--  Pet. 70105 - Fase III (REQ.05) - MODIF TAM (24/02/2021) * Fin -->
		
		<!--  Pet. 78691 ** MODIF TAM (22/12/2021) -->
		<input type="hidden" name="sist_cultivo" id="sist_cultivo" value="${sist_cultivo}"/>
		<input type="hidden" name="des_sist_cultivo" id="des_sist_cultivo" value="${des_sist_cultivo}"/>
		
		<input type="hidden" name="hayCambiosDatosAsegurado" id="hayCambiosDatosAsegurado" value="${hayCambiosDatosAsegurado}" />
		
		<!-- Impresión -->
		<input type="hidden" name="formato" value=""/>
		
		<input type="hidden" name="tipoListadoGrid" id="tipoListadoGrid" <c:if test="${tipoListadoGrid !=null}">value='${tipoListadoGrid}'</c:if>/>
		
		<form:hidden path="parcela.anexoModificacion.cupon.id" id="idCupon"/>
		<form:hidden path="parcela.anexoModificacion.cupon.idcupon" id="idCuponStr"/>
		
		<form:hidden path="id" id="id"/>
		
		
		<div style="panel2 isrt">
			<fieldset style="width:97%;">
				<legend class="literal">Filtro</legend>	
					<table align="center">						
						<tr>
							<td class="literal" >Provincia</td>
							<td>
								<form:input path="parcela.codprovincia" id="provincia" cssClass="dato width40"  size="2" maxlength="2" tabindex="1" onchange="javascript:lupas.limpiarCampos('desc_provincia','comarca','desc_comarca','termino','desc_termino','subtermino');"/>	
	                            <form:input path="parcela.parcela.termino.provincia.nomprovincia" cssClass="dato"	id="desc_provincia"  size="20" readonly="true"/> 
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Provincia','principio', '', '');"	alt="Buscar Provincia" title="Buscar Provincia" />
	                            <label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_provincia"> *</label>
							</td>
							<td class="literal" >Comarca</td>
							<td>
								<form:input path="parcela.codcomarca"  id="comarca" cssClass="dato width40"  size="2" maxlength="2" tabindex="2" onchange="javascript:lupas.limpiarCampos('desc_comarca','termino','desc_termino','subtermino');"/>
								<form:input path="parcela.parcela.termino.comarca.nomcomarca" cssClass="dato"	id="desc_comarca"  size="20" readonly="true"/> 
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Comarca','principio', '', '');"	alt="Buscar Comarca" title="Buscar Comarca" />
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_comarca"> *</label>
							</td>
							<td class="literal" >Término</td>
							<td>
								<form:input path="parcela.codtermino"  id="termino" cssClass="dato width40" size="3" maxlength="3" tabindex="3" onchange="javascript:lupas.limpiarCampos('desc_termino','subtermino');"/>
							    <form:input path="parcela.parcela.termino.nomtermino" cssClass="dato" id="desc_termino"  size="30" readonly="true"/> 
							    <label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_termino"> *</label>
							</td>
							<td class="literal" >Subtr.</td>
							<td>
								<form:input path="parcela.subtermino"  id="subtermino" cssClass="dato width40" size="1" maxlength="1" tabindex="4" onchange="this.value=this.value.toUpperCase();"/>		
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Termino','principio', '', '');"	alt="Buscar Termino" title="Buscar Termino" />
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_subtermino"> *</label>
							</td>
						</tr>
					</table>
					
					<table align="center"><tr><td>				
						
					<div style="float:left;">
					<fieldset style="width: 140px;margin-right: 5px;">
						<legend class="literal">Hoja - Nº</legend>
						<table align="center">
							<tr>
								<td class="literal">
									<form:input path="parcela.hoja" id="txt_hoja" cssClass="dato width40" size="5" maxlength="5" tabindex="5"/>
				            		<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_hoja"> *</label>
						            <form:input path="parcela.numero" id="txt_numero" cssClass="dato width40" size="5" maxlength="5" tabindex="6"/>
						            <label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_numero"> *</label>
								</td>
							</tr>
						</table>
					</fieldset>
					</div>
					<div style="float:left;">
					<fieldset style="width: 100%;">
							<legend class="literal">SIGPAC</legend>
							<table align="center">
								<tr>
									<td class="literal">Prov</td>
									<td class="literal">
										 <form:input path="parcela.codprovsigpac" id="txt_provsigpac" cssClass="dato" size="2" maxlength="2" tabindex="9"/>
				                		 <label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_provsigpac"> *</label>
									</td>
									<td class="literal">Term</td>
									<td class="literal">
										<form:input path="parcela.codtermsigpac"  id="txt_termsigpac" cssClass="dato" size="3" maxlength="3" tabindex="10"/>
				                		<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_termsigpac"> *</label>
									</td>
									<td class="literal">Agr</td>
									<td class="literal">
										<form:input path="parcela.agrsigpac"  id="txt_agrsigpac"  cssClass="dato" size="3" maxlength="3" tabindex="11"/>
				                		<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_agrsigpac"> *</label>
									</td>
									<td class="literal">Zona</td>
									<td class="literal">
										<form:input path="parcela.zonasigpac"  id="txt_zonasigpac"cssClass="dato" size="2" maxlength="2" tabindex="12"/>
				                		<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_zonasigpac"> *</label>
									</td>
									<td class="literal">Pol</td>
									<td class="literal">
										<form:input path="parcela.poligonosigpac"  id="txt_polsigpac"  cssClass="dato" size="3" maxlength="3" tabindex="13"/>
				                		<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_polsigpac"> *</label>
									</td>
									<td class="literal">Parc</td>
									<td class="literal">
										<form:input path="parcela.parcelasigpac"  id="txt_parcsigpac" cssClass="dato" size="5" maxlength="5" tabindex="14"/>
				                		<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_parcsigpac"> *</label>
									</td>
									<td class="literal">Rec</td>
									<td class="literal">
										<form:input path="parcela.recintosigpac"  id="txt_recsigpac"  cssClass="dato" size="5" maxlength="5" tabindex="15"/>
				                		<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_recsigpac"> *</label>
									</td>
								</tr>
							</table>
					</fieldset>
					</div>
					
					</td></tr></table>
					
					<br/>
					<table align="center">
						<tr>
							<td class="literal" >Nombre Parcela</td>
							<td>
								<form:input path="parcela.nomparcela" id="txt_nombreParcela" cssClass="dato" cssStyle="width:200px" size="20" maxlength="20" tabindex="16" onchange="this.value=this.value.toUpperCase();"/>
						        <label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_nombreParcela"> *</label>
							</td>
							<td class="literal" >Cultivo</td>
							<td>
								<form:input path="parcela.codcultivo" id="cultivo" cssClass="dato width40" size="3" maxlength="3" tabindex="17" onchange="javascript:lupas.limpiarCampos('desc_cultivo','variedad','desc_variedad');"/>
								<form:input path="parcela.parcela.variedad.cultivo.descultivo" cssClass="dato" id="desc_cultivo"  size="20" readonly="true"/> 
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Cultivo','principio', '', '');"	alt="Buscar Cultivo" title="Buscar Cultivo" />
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_cultivo"> *</label>
							</td>
							<td class="literal" >Variedad</td>
							<td>
								<form:input path="parcela.codvariedad" id="variedad" cssClass="dato width40" size="3" maxlength="3" tabindex="18" onchange="javascript:lupas.limpiarCampos('desc_variedad');"/>
								<form:input path="parcela.parcela.variedad.desvariedad" cssClass="dato" id="desc_variedad"  size="25" readonly="true"/> 
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Variedad','principio', '', '');"	alt="Buscar Variedad" title="Buscar Variedad" />
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_variedad"> *</label>
							</td>
						</tr>
					</table>
					
					<table align="center">
						<tr>
							<td class="literal" >T. Capital</td>
							<td>
								<form:input path="tipoCapital.codtipocapital" id="capital" size="2" maxlength="3" cssClass="dato" onchange="javascript:lupas.limpiarCampos('desc_capital');" tabindex="19"/>
								<input class="dato"	id="desc_capital" size="20" readonly="readonly" />
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('FactoresTipoCapitalIN','principio', '', '');"	alt="Buscar Tipo Capital" title="Buscar Tipo Capital" />	
						    	<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_tipoCapital"> *</label>
							</td>
							<td class="literal" >Superficie</td>
							<td>
								 <form:input path="superficie" id="txt_superficie" cssClass="dato"  size="8" maxlength="11" tabindex="20"/>
					        	<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_superficie"> *</label>
							</td>
							<td class="literal" >Producción</td>
							<td>
								 <form:input path="produccion" id="txt_produccion" cssClass="dato"  size="8" maxlength="11" tabindex="21"/>
					        	<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_produccion"> *</label>
							</td>
							<td class="literal">Tipo Modificación
								<form:select path="parcela.tipomodificacion" id="sl_tipoModificacion" cssClass="dato" tabindex="22" >
									<form:option value="T" label="TODOS" />
	                                <form:option value="A" label="Alta" />
	                                <form:option value="B" label="Baja" />
	                                <form:option value="M" label="Modificación" /> 
						        </form:select>
							</td>
							
							<!--  Pet. 78691 ** MODIF TAM (17.12.2021) ** Inicio  -->
							<td class="literal"> S. Cultivo</td>
							<td>
							    <input type="text" class="dato" size="2" onchange="javascript:lupas.limpiarCampos('dessistemaCultivo');" maxlength="3" id="sistemaCultivo" name="sistemaCultivo" />
								<input type="text" class="dato" size="19" maxlength="19" id="dessistemaCultivo" name="dessistemaCultivo" readonly="true" />
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('SistemaCultivo','principio', '', '');"	alt="Buscar Sistema Cultivo" title="Buscar Sistema Cultivo" />
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_dessistemacultivo"> *</label>
							</td>
							<!--  Pet. 78691 ** MODIF TAM (17.12.2021) ** Fin  -->
						</tr>
					</table>
					
					<div class="literal" style="width:870px;margin:0 auto;text-align: center;">
					    <div id="parcelas" style="float:left;margin-right:100px" class="handOver" 
					         onmouseover="javascript:radioElem_onmouseover('parcelas')"
					         onmouseout="javascript:radioElem_onmouseout('parcelas')"
					         onclick="javascript:tipoListado_onclick('parcelas')">
					         Parcelas
					         <input id="" type="radio" name="listado" value="parcelas" <c:if test="${tipoListadoGrid == 'parcelas' }">checked</c:if>>
					            
					    </div>
						<div id="instalaciones" style="float:left;margin-right:100px" class="handOver"
						     onmouseover="javascript:radioElem_onmouseover('instalaciones')"
					         onmouseout="javascript:radioElem_onmouseout('instalaciones')"
					         onclick="javascript:tipoListado_onclick('instalaciones')">
					         Instalaciones
					         <input id="" type="radio" name="listado" value="instalaciones" <c:if test="${tipoListadoGrid == 'instalaciones' }">checked</c:if>>
					    </div>
						<div id="todas" style="float:left;" class="handOver"
						     onmouseover="javascript:radioElem_onmouseover('todas')"
					         onmouseout="javascript:radioElem_onmouseout('todas')"
					         onclick="javascript:tipoListado_onclick('todas')">
					         Todas
					         <input id="" type="radio" name="listado" value="todas" <c:if test="${tipoListadoGrid == 'todas'}">checked</c:if>>
					     </div>
					     <div>
							Tipo Rendimiento
							 	 <form:select path ="tipoRdto" id="rdtoHist" cssClass="dato" tabindex="9" cssStyle="width:120" >
							 	 <option	value="">Todos</option> 
								    <c:forEach items="${listaTipoRendimientos}"	var="tiposRdto"> 
 									<form:option value="${tiposRdto.idrdto}">${tiposRdto.descripcion}</form:option>
 								</c:forEach> 
							</form:select>
							 
									
						</div>
				    </div>	
					
			</fieldset>
		</div>
		<br/>
	</form:form>
		<div id="grid" >
			
			<display:table requestURI="parcelasAnexoModificacion.html" 
			               id="listaParcelasModificadas" 
						   class="LISTA" 
						   summary="ParcelaModificada" 
						   name="${listaParcelasModificadas}"
						   sort="list"
						   pagesize="${numReg}"
						   defaultsort="16"
						   keepStatus="true"
						   clearStatus="${clearStatus}"
						   decorator="com.rsi.agp.core.decorators.ModelTableDecoratorParcelasModificadas" 
						   excludedParams="method idsRowsChecked codParcela localizacion_cm parcelasString"
						   style="border-collapse:collapse;">
  					
						<display:column class="accionesDisplayTagColumn" headerClass="cblistaImg 626262" title="Acciones" media="html" property="admActions" sortable="false" style="width:115px;text-align:center;color:#626262;" />
						<display:column class="literal" headerClass="cblistaImg" title="N&#176;"        property="hojaNumero"    style="width:80px;"                   sortable="false"/>
						<display:column class="literal" headerClass="cblistaImg" title="PRV"            property="codprovincia"  style="width:80px;"   				   sortable="false"/>
						<display:column class="literal" headerClass="cblistaImg" title="CMC"            property="codcomarca"    style="width:80px;"   				   sortable="false"/>
						<display:column class="literal" headerClass="cblistaImg" title="TRM"            property="codtermino"    style="width:80px;"   				   sortable="false"/>
						<display:column class="literal" headerClass="cblistaImg" title="SBT"            property="codsubtermino" style="width:80px;"   				   sortable="false"/>
						<display:column class="literal" headerClass="cblistaImg" title="CUL"            property="codcultivo"    style="width:80px;"   				   sortable="true" />
						<display:column class="literal" headerClass="cblistaImg" title="VAR"            property="codvariedad"   style="width:80px;"                   sortable="false"/>
						<display:column class="literal" headerClass="cblistaImg" title="Id. Cat/SIGPAC" property="idCat"         style="width:230px;"                  sortable="true" comparator = "com.rsi.agp.core.comparators.TableComparatorSigPacs" />
						

						<c:if test="${tipoListadoGrid == 'instalaciones'}">
						    <display:column class="literal" headerClass="cblistaImg" title="m" property="superf" style="width:85px;"  sortable="true">
						    </display:column>
						</c:if>
						<c:if test="${tipoListadoGrid == 'todas'}">
							<display:column class="literal" headerClass="cblistaImg" title="Super./m" property="superf" style="width:85px;"  sortable="true">
							</display:column>
						</c:if>
						<c:if test="${tipoListadoGrid == 'parcelas'}">
							<display:column class="literal" headerClass="cblistaImg" title="Superficie" property="superf" style="width:85px;"  sortable="true">
							</display:column>
						</c:if>

						<display:column class="literal" headerClass="cblistaImg" title="Prec."          property="precio"        style="width:80px;"                   sortable="false"/>
						
						<c:if test="${tipoListadoGrid == 'todas'}">
					    	<display:column class="literal" headerClass="cblistaImg" title="Prod."          property="produccion"    style="width:55px;" sortable="true"  comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico" />
					    </c:if>
					    <c:if test="${tipoListadoGrid == 'parcelas'}">
					    	<display:column class="literal" headerClass="cblistaImg" title="Prod."          property="produccion"    style="width:55px;" sortable="true"  comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico" />
					    </c:if>
						
						
						<display:column class="literal" headerClass="cblistaImg" title="T.Capital"      property="tcapital"      style="width:250px;"                  sortable="false"/>
						<display:column class="literal" headerClass="cblistaImg" title="Estado"         property="estado"        style="width:100px;"                  sortable="false"/>
						<display:column class="literal" headerClass="cblistaImg" title="X"              property="columnaCheck"  style="width:50px;text-align:center"  sortable="false"/>
						
						<display:column class="literal" title=" " property="ordenacion" style="visibility:hidden;"/>
						
						<display:setProperty name="paging.banner.some_items_found" value='' />
						<display:setProperty name="paging.banner.one_item_found" value='' />
						<display:setProperty name="paging.banner.all_items_found" value='' />
							        	
			        	<display:footer>
			        		<tr style="background-color:#e5e5e5">
			        			<td class="literal" colspan="2">N&#176; Total Parcelas: </td>
			        			<td class="literal" style="color:green">${numParcelasListado}</td>
			        			<td class="literal" colspan="2">Seleccionados:</td>
			        			<td class="literal" style="color:green"><label id="sel"/></td>	
			        			<td class="literal"></td>	
			        			<td class="literal"></td>	
			        			<td class="literal"></td>	
			        			<td class="literal"></td>
			        			<td class="literal"></td>
			        			<c:if test="${tipoListadoGrid == 'todas'}">	
			        			    <td class="literal"></td>
			        			</c:if>	
			        			<c:if test="${tipoListadoGrid == 'parcelas'}">	
			        			    <td class="literal"></td>
			        			</c:if>
			        			<td class="literal" colspan="2"><div style="float:right">Marcar Todos:</div></td>
			        			<td class="literal" style="width:30px;text-align:center">
			        			 	<input type="checkbox" id="selTodos" name="selTodos" class="dato" 
			        			 		onclick="selectAllChecks(this);"/>
			        			</td>
			        		</tr>
			        	</display:footer>
			        	
			        	<c:if test="${tipoListadoGrid == 'todas'}">
							<div style="float:right;color:#626262;font-family:tahoma,verdana,arial;font-size:11px;font-weight:bold;">
							    <span style="width:12px;height:12px;background-color:#A9F5A9"></span>
							    &nbsp;Instalaciones
							</div>
				       </c:if>
			</display:table>
			
	        <div style="width:20%;text-align:center" id="divImprimir">
	        	 <a style="font-family: tahoma, verdana, arial;color: #626262;font-size:11px;">Imprimir</a>	
				 <a id="btnImprimirExcel" style="text-decoration:none;" href="javascript:imprimirListadoParcelas('xls')">
				 	<img src="jsp/img/jmesa/excel.gif"/>
				 </a>
			</div>
			
		</div>
		
		<!-- Pet. 70105 - Fase III (REQ.05) - MODIF TAM (24/02/2021) * Fin -->
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
				        <!-- Pet. 70105 - Fase III (REQ.05) - MODIF TAM (23/02/2021) * Fin  -->
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
		<!-- Pet. 70105 - Fase III (REQ.05) - MODIF TAM (24/02/2021) * Fin -->
	
</div>
<%@ include file="/jsp/common/static/piePagina.jsp"%>
<%@ include file="/jsp/moduloUtilidades/modificacionesPoliza/razonBajaAnexoParcela.jsp"%>
<%@ include file="/jsp/common/static/overlay.jsp"%>
<%@ include file="/jsp/moduloUtilidades/anexoModificacion/anexoModifSWCambioMasivo.jsp"%>
<%@ include file="/jsp/common/static/overlayCambioMasivo.jsp"%>

<%@ include file="/jsp/common/lupas/lupaCultivo.jsp"%>
<%@ include file="/jsp/common/lupas/lupaVariedad.jsp"%>
<%@ include file="/jsp/common/lupas/lupaProvincia.jsp"%>
<%@ include file="/jsp/common/lupas/lupaComarca.jsp"%>
<%@ include file="/jsp/common/lupas/lupaTermino.jsp"%>
<%@ include file="/jsp/common/lupas/lupaTipoCapitalIN.jsp"%>
<%@ include file="/jsp/common/lupas/lupaSistemaCultivo.jsp"%>

</body>
</html>