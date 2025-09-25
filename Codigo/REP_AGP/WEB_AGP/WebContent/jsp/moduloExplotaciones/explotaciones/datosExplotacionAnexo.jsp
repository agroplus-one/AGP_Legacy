<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
	<head>
		<title>Alta/modificación de explotaciones</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
    	
<script type="text/javascript" charset="ISO-8859-1">
	function comprobarSiBorrarFormularioGrupoRaza(){
		<c:if test="${requestScope.borradoFormularioGrupoRaza == 'true'}">	
			$('input[id^="cod_cpto_"]').val('');
			$('input[id^="des_cpto_"]').val('');
			$('#codgrupoRaza').val('');
			$('#desGrupoRaza').val('');
			$('#codtipocapital').val('');
			$('#desTipoCapital').val('');
			$('#codtipoanimal').val('');
			$('#desTipoAnimal').val('');
			$('#numanimales').val('');
			$('#precio').val('');
			$('#gruporazaid').val('');
			
			var primerAcceso = $('#primerAcceso').val();
			
			if(primerAcceso!="SI"){
				irPanelGrupoRaza();
			}
		</c:if>	
	}
	
	function comprobarSiForzarEdicionGrupoRaza(){
		var gruporazaid = $('#gruporazaid').val();
		if(gruporazaid!=''){
			$('#iconoEditar_1').click();
		}
	}
	
	function vueltaGuardarYReplicarGR(){
		var idNuevoGr= $('#idGuardarReplicar').val();
		if(idNuevoGr!=null && idNuevoGr!=""){
			irPanelGrupoRaza();
			$('#duplicar_'+idNuevoGr).click();
			/* var img = document.getElementById("duplicar_"+idNuevoGr);
			img.click();	 */		
		}
	}
	
</script>
    	
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/terminos.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenericaIN.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGeneriaODGanado.js"></script>
		<script type="text/javascript" src="jsp/moduloExplotaciones/explotaciones/datosExplotacionComun.js"></script>
		<script type="text/javascript" src="jsp/moduloExplotaciones/explotaciones/datosExplotacionAnexo.js"></script>
	<!-- 	<script type="text/javascript" src="jsp/moduloExplotaciones/explotaciones/datosExplotacionesCobAnexo.js"></script> -->
		
		<%@ include file="/jsp/js/draggable.jsp"%>
		
				 <meta name="codigo_plan" content="${codPlan}" />
		 <meta name="codigo_linea" content="${codLinea}" />
		 
		<script language="javascript"> 	
		
		
		
		$(document).ready(function(){
			
			<c:if test="${requestScope.modoLectura=='true' or requestScope.modoLectura=='modoLectura'}">
			
				$("input[type=checkbox]").each(function(){	       			
					$(this).attr('disabled',true);
				});	
				$("input[type=text]").each(function(){		
					$(this).attr('disabled',true);
				});
				
				$("img[src='jsp/img/magnifier.png']").each(function(){
					$(this).hide();
				});
				
				$('#btnGuardarSalir').hide();
				$('#btnGuardarNuevo').hide();
				$('#btnGuardarReplicar').hide();
				$('#btnCalcular').hide();
				$('#btnLimpiar').hide();
				$('#guardarGrupoRaza').hide();
				$('#guardarGrupoRazaReplicar').hide();
				
			</c:if>
			$('#buttons').show();
			$('#buttons2').show();
			$('#buttons3').show();
		});		
		</script> 
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onLoad="SwitchMenu('sub4');">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

		<form:form name="listadoExplotacionesAnexo" id="listadoExplotacionesAnexo" action="listadoExplotacionesAnexo.html" method="post" commandName="anexoModificacion">
			<input type="hidden" id="methodVolver" name="method" value="doPantallaListaExplotacionesAnexo">
			<input type="hidden" name="vieneDeListadoAnexosMod" id="vieneDeListadoAnexosMod" value="${requestScope.vieneDeListadoAnexosMod}"/>
			<input type="hidden" name="anexoModificacionId" id="anexoModificacionId"/>
			<input type="hidden" name="origenLlamada" id="origenLlamada" value="datosExplotacionAnexo"/>
			<input type="hidden" name="hayCambiosDatosAsegurado" id="hayCambiosDatosAsegurado" value="${hayCambiosDatosAsegurado}" />
			
			<c:if test="${requestScope.modoLectura=='true' or requestScope.modoLectura=='modoLectura'}">
				<input type="hidden" name="modoLectura" id="modoLectura" value="true"/>
			</c:if>
			<input type="hidden" name="primerAcceso" id="primerAcceso" value="${requestScope.primerAcceso}"/>
		</form:form>

		<form:form name="datosExplotacionesAnexo" id="datosExplotacionesAnexo" action="datosExplotacionesAnexo.html" method="post" commandName="explotacionAnexoBean">
			<input type="hidden" id="methodDE" name="method" size="5" value="">
			<input type="hidden" id="accion" name="accion" value="">
			<input type="hidden" name="vieneDeListadoAnexosMod" id="vieneDeListadoAnexosMod" value="${requestScope.vieneDeListadoAnexosMod}"/>

			<input type="hidden" id="gruporazaid" name="gruporazaid" value="${gruporazaid}">

			<input type="hidden" id="listaCodProvincias" name="codProvinciasid" value="${listaCodProvincias}">
			<input type="hidden" id="listaCodComarcas" name="codComarcasid" value="${listaCodComarcas}">
			<input type="hidden" id="listaCodTerminos" name="codTerminosid" value="${listaCodTerminos}">
			<input type="hidden" id="listaCodEspecies" name="codEspeciesid" value="${listaCodEspecies}">
			<input type="hidden" id="listaCodRegimenes" name="codRegimenesid" value="${listaCodRegimenes}">
			<input type="hidden" id="listaCodGruposRazas" name="codGruposRazasid" value="${listaCodGruposRazas}">
			<input type="hidden" id="listaCodTiposCapital" name="codTiposCapitalid" value="${listaCodTiposCapital}">
			<input type="hidden" id="listaTCapNoDepNumAni" name="listaTCapNoDepNumAni" value="${listaTCapNoDepNumAni}">
			<input type="hidden" id="validaNumAni" name="validaNumAni" value="true">			
			
			<input type="hidden" id="listaCodTiposAnimal" name="codTiposAnimalid" value="${listaCodTiposAnimal}">
			<input type="hidden" id=isCoberturas name="isCoberturas" value="${isCoberturas}">
			<input type="hidden" id="coberturas" name="coberturas" value="${coberturas}">
			<input type="hidden" id="tieneCoberturas" name="tieneCoberturas" value="${tieneCoberturas}">
			<input type="hidden" id="botonGuardarGR" name="botonGuardarGR" value="${botonGuardarGR}">
			
			<!-- Se corresponde con el id del último grupo de raza guaradado una vez pulsado el botón de guardar y replicar
			para saber qué datos debemos cargar en los controles de la página -->
			<input type="hidden" id="idGuardarReplicar" name="idGuardarReplicar" value="${idGuardarReplicar}">
			
			<form:hidden path="anexoModificacion.id" id="anexoModificacionId"/>
			<form:hidden path="id" id="idExplotacion"/>
			<form:hidden path="numero" id="numero"/>
			<form:hidden path="tipoModificacion" id="tipoModificacion"/>
			<form:hidden path="grupoRazaAnexos[0].id" id="idGrupoRaza" />
			<form:hidden path="grupoRazaAnexos[0].precioAnimalesModuloAnexos[0].id" id="idPrecioAnimalesModulo"/>
			
			<input type="hidden" id="codlinea" name="codlinea" value="${codLinea}">
			<input type="hidden" id="fechaInicioContratacion" name="fechaInicioContratacion" value="${fechaInicioContratacion}" />
			<input type="hidden" name="hayCambiosDatosAsegurado" id="hayCambiosDatosAsegurado" value="${hayCambiosDatosAsegurado}" />
			
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:100%">
			<p class="titulopag" align="left">Datos de la explotaci&oacute;n</p>
			
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			
			<!-- Plan/línea de la póliza para el filtro de las lupas -->
			<form:hidden path="anexoModificacion.poliza.linea.lineaseguroid" id="lineaseguroid"/>
			<form:hidden path="anexoModificacion.poliza.idpoliza" id="idPoliza"/>
<%-- 			<form:hidden path="anexoModificacion.codmodulo" id="codModulo"/> --%>
 			<form:hidden path="anexoModificacion.codmodulo" id="codmodulo"/> <!-- para las lupas in -->
			
			<form:hidden path="anexoModificacion.poliza.clase" id="clase"/>
			<!-- Grupo de negocio para el filtro de la lupa de tipo de capital con grupo de negocio -->
			<!-- <input type="hidden" id="grupoNegocio" size="5" value="2"> -->
			
			<!-- Panel de Datos identificativos -->
			<div id="datosIdentificativos" class="panel2 isrt">
				<fieldset>
				
					<legend class="literal">Datos Identificativos</legend>
					
					<fieldset style="float:left;">
						<legend class="literal">REGA</legend>
						<table border="0">
							<tr>
								<td class="literal" style="padding-left: 10px;">REGA</td>
								<td class="literal">
									<form:input path="rega" id="rega"
										maxlength="14" size="14" cssClass="dato"
										onchange="this.value=this.value.toUpperCase();" tabindex="7"/>
									<label class="campoObligatorio" id="campoObligatorio_rega" title="Campo obligatorio">*</label>
								</td>
								<c:if test="${requestScope.modoLectura != 'modoLectura'}">
								<td class="literal">	
									<a class="bot" id="sigpacBtn" href="#" onclick="doInfoRega()" tabindex="11">R</a>
									<img id="ajaxLoading_sigpac" src="jsp/img/ajax-loading.gif" style="cursor:hand; cursor:pointer; display:none">
								</td>
								</c:if>
								<td class="literal" style="padding-left: 10px;">Sigla</td>
								<td class="literal">
									<form:input path="sigla" id="sigla" size="4"
										maxlength="3" cssClass="dato"
										onchange="this.value=this.value.toUpperCase();" tabindex="8"/>
									<label class="campoObligatorio" id="campoObligatorio_sigla" title="Campo obligatorio">*</label>
								</td>
							</tr>
						</table>
					</fieldset>
					
					
					
					<fieldset>
						<legend class="literal">Ubicación</legend>
						<table>
							<tr>
								<td class="literal"><abbr title="Provincia">Prov.</abbr></td>
								<td class="literal">
								
								<form:input path="termino.id.codprovincia" id="provincia" readonly="${perfilUsuario != 0}"
									size="2" maxlength="2" cssClass="dato"
									onchange="javascript:lupas.limpiarCampos('desc_provincia','comarca','desc_comarca','termino','desc_termino','subtermino');"
									tabindex="1"/>
									
								<form:input path="termino.provincia.nomprovincia" id="desc_provincia" size="14" readonly="true" cssClass="dato"/>
									<c:if test="${requestScope.modoLectura!= 'modoLectura' && perfilUsuario == 0}">
										<img src="jsp/img/magnifier.png" style="cursor: hand;"
										onclick="javascript:lupas.muestraTabla('ProvinciaIN','principio', '', '');"
										alt="Buscar Provincia" title="Buscar Provincia" />
										<label class="campoObligatorio" id="campoObligatorio_provincia" title="Campo obligatorio">*</label>
									</c:if>
									
								</td>
								<td class="literal"><abbr title="Comarca">Com.</abbr></td>
								<td class="literal">
								
								<form:input path="termino.id.codcomarca" id="comarca" readonly="${perfilUsuario != 0}"
									size="2" maxlength="2" cssClass="dato"
									onchange="javascript:lupas.limpiarCampos('desc_comarca','termino','desc_termino','subtermino');"
									tabindex="2"/>

									<form:input path="termino.comarca.nomcomarca" id="desc_comarca" size="14" readonly="true" cssClass="dato"/>									
									<c:if test="${requestScope.modoLectura!= 'modoLectura' && perfilUsuario == 0}" >
										<img src="jsp/img/magnifier.png" style="cursor: hand;"
										onclick="javascript:lupas.muestraTabla('ComarcaIN','principio', '', '');"
										alt="Buscar Comarca" title="Buscar Comarca" />
										<label class="campoObligatorio" id="campoObligatorio_comarca" title="Campo obligatorio">*</label>
									</c:if>
								</td>
								<td class="literal"><abbr title="Término">Térm.</abbr></td>
								<td class="literal">
								
								<form:input path="termino.id.codtermino" id="termino" readonly="${perfilUsuario != 0}"
									size="3" maxlength="3" cssClass="dato"
									onchange="javascript:lupas.limpiarCampos('desc_termino','subtermino');"
									tabindex="3"/>
									
									<c:choose>
															<c:when test="${not empty isFechaMenor}">														        
															<c:choose>
														            <c:when test="${isFechaMenor}">
														                <form:input path="termino.nomtermino" id="desc_termino" size="16" readonly="true" cssClass="dato" />
														            </c:when>
														            <c:otherwise>
														                <form:input path="termino.nombreREGA" id="desc_termino" size="16" readonly="true" cssClass="dato" />
														            </c:otherwise>
														    </c:choose>
														    </c:when>
														    <c:otherwise>
														        <form:input path="termino.nomtermino" id="desc_termino" size="16" readonly="true" cssClass="dato" />
														    </c:otherwise>
														</c:choose>
									<c:if test="${requestScope.modoLectura!= 'modoLectura' && perfilUsuario == 0}">
										<!-- <img src="jsp/img/magnifier.png" style="cursor: hand;"
										onclick="javascript:lupas.muestraTabla('TerminoIN','principio', '', '');"
										alt="Buscar Término" /> -->
										<label class="campoObligatorio" id="campoObligatorio_termino" title="Campo obligatorio">*</label>
									</c:if>
								</td>
								
								<td class="literal"><abbr title="Subtérmino">Subt.</abbr></td>
								<td class="literal">
								<form:input path="termino.id.subtermino" id="subtermino" readonly="${perfilUsuario != 0}"
									size="1" maxlength="1" cssClass="dato"
									onchange="this.value=this.value.toUpperCase();" tabindex="4"/>
									<c:if test="${requestScope.modoLectura!= 'modoLectura' && perfilUsuario == 0}">
										<img src="jsp/img/magnifier.png" style="cursor: hand;"
										onclick="javascript:lupas.muestraTabla('TerminoIN','principio', '', '');"
										alt="Buscar Término" title="Buscar Término" />
										<label class="campoObligatorio" id="campoObligatorio_subtermino" title="Campo obligatorio">*</label>
									</c:if>
								</td>
							</tr>
						</table>
					</fieldset>
					
					
					<fieldset  style="float: left;margin-bottom:10px;margin-left:100px">
						<legend class="literal">Coordenadas</legend>
						<table align="center">
							<tr>
								<td class="literal"><abbr title="Latitud">Lat.</abbr></td>
								<td class="literal">
								
								
								<form:input path="latitud" id="latitud"
									size="6" maxlength="6" cssClass="dato"
									onchange="this.value=this.value.toUpperCase();" tabindex="5"/>
								
								<label class="campoObligatorio" id="campoObligatorio_latitud" title="Campo obligatorio">*</label>	
								</td>
								
								<td class="literal"><abbr title="Longitud">Lon.</abbr></td>
								<td class="literal">
								
								<form:input path="longitud" id="longitud"
									size="6" maxlength="6" cssClass="dato"
									onchange="this.value=this.value.toUpperCase();" tabindex="6"/>
								<label class="campoObligatorio" id="campoObligatorio_longitud" title="Campo obligatorio">*</label>	
								</td>
							</tr>
						</table>
					</fieldset>
					
					
	
					<table border="0" style="margin-top: 30px">
						<tr>
							<td class="literal" style="padding-left: 10px;">Subexplotación</td>
							<td class="literal">
								<form:input path="subexplotacion" id="subexplotacion"
									size="3" maxlength="3" cssClass="dato"
									onchange="this.value=this.value.toUpperCase();" tabindex="9"/>
								<label class="campoObligatorio" id="campoObligatorio_subexplotacion" title="Campo obligatorio">*</label>
							</td>
							<td class="literal" style="padding-left: 10px;">Especie</td>
							<td class="literal">
								<form:input path="especie" id="especie"
									size="3" maxlength="3" cssClass="dato"
									onchange="javascript:lupas.limpiarCampos('desc_especie');"
									tabindex="10"/>
									
								<form:input cssClass="dato" id="desc_especie" path="nomespecie" size="14"
								readonly="true"/>
								<c:if test="${modoLectura!= 'modoLectura'}">
									<img src="jsp/img/magnifier.png" style="cursor: hand;"
									onclick="javascript:lupas.muestraTabla('VistaEspecieIN','principio', '', '');"
									alt="Buscar Especie" title="Buscar Especie" />
									<label class="campoObligatorio" id="campoObligatorio_especie" title="Campo obligatorio">*</label>
								</c:if>
							</td>
							<td class="literal" style="padding-left: 10px;">Régimen</td>
							<td class="literal">
								<form:input path="regimen" id="regimen"
									size="3" maxlength="3" cssClass="dato"
									onchange="javascript:lupas.limpiarCampos('desc_regimen');"
									tabindex="11"/>
									
								<form:input cssClass="dato" id="desc_regimen" path="nomregimen" size="14"
								readonly="true"/>
								<c:if test="${requestScope.modoLectura!= 'modoLectura'}">
									<img src="jsp/img/magnifier.png" style="cursor: hand;"
									onclick="javascript:lupas.muestraTabla('VistaRegimenIN','principio', '', '');"
									alt="Buscar Régimen" title="Buscar Régimen" />
									<label class="campoObligatorio" id="campoObligatorio_regimen" title="Campo obligatorio">*</label>
								</c:if>
							</td>
							<!-- Botón de paso de panel -->
							<td class="literal">
								<a class="bot" id="btnPanelGruposRaza" href="javascript:irPanelGrupoRaza()" tabindex="12">>></a>
							</td>
						</tr>
					</table>
					
					
				</fieldset>
			</div> 
			<!-- Fin de Datos Identificativos -->
			
			
			<!-- Panel de Datos identificativos -->
			<!-- 
			<div id="datosIdentificativos" class="panel2 isrt" style="width: 100%;">
				<fieldset style="width: 100%;">
					<legend class="literal">Datos Identificativos</legend>
					<fieldset style="width: 79%; float: left">
						<legend class="literal">Ubicación</legend>
						<table align="center">
							<tr>
								<td class="literal"><abbr title="Provincia">Prov.</abbr></td>
								<td class="literal">
								
									<form:input path="termino.id.codprovincia" id="provincia"
										size="2" maxlength="2" cssClass="dato"
										onchange="javascript:lupas.limpiarCampos('desc_provincia','comarca','desc_comarca','termino','desc_termino','subtermino');"
										tabindex="1"/>
										
								<form:input path="termino.provincia.nomprovincia" id="desc_provincia" size="16" readonly="true" cssClass="dato"/>
									<c:if test="${requestScope.modoLectura!='true' && requestScope.modoLectura!='modoLectura'}">
										<img src="jsp/img/magnifier.png" style="cursor: hand;"
										onclick="javascript:lupas.muestraTabla('ProvinciaIN','principio', '', '');"
										alt="Buscar Provincia" title="Buscar Provincia" />
										<label class="campoObligatorio" id="campoObligatorio_provincia" title="Campo obligatorio">*</label>
									</c:if>
								</td>
								<td class="literal"><abbr title="Comarca">Com.</abbr></td>
								<td class="literal">
								
								<form:input path="termino.id.codcomarca" id="comarca"
									size="2" maxlength="2" cssClass="dato"
									onchange="javascript:lupas.limpiarCampos('desc_comarca','termino','desc_termino','subtermino');"
									tabindex="2"/>
									
									<form:input path="termino.comarca.nomcomarca" id="desc_comarca" size="16" readonly="true" cssClass="dato"/>									
									<c:if test="${requestScope.modoLectura!='true' && requestScope.modoLectura!='modoLectura'}">
										<img src="jsp/img/magnifier.png" style="cursor: hand;"
										onclick="javascript:lupas.muestraTabla('ComarcaIN','principio', '', '');"
										alt="Buscar Comarca" title="Buscar Comarca" />
										<label class="campoObligatorio" id="campoObligatorio_comarca" title="Campo obligatorio">*</label>
									</c:if>
								</td>
								<td class="literal"><abbr title="Término">Térm.</abbr></td>
								<td class="literal">
								
									<form:input path="termino.id.codtermino" id="termino"
										size="3" maxlength="3" cssClass="dato"
										onchange="javascript:lupas.limpiarCampos('desc_termino','subtermino');"
										tabindex="3"/>
									
									<form:input path="termino.nomtermino" id="desc_termino" size="26" readonly="true" cssClass="dato"/>
									<c:if test="${requestScope.modoLectura!='true' && requestScope.modoLectura!='modoLectura'}">
										<img src="jsp/img/magnifier.png" style="cursor: hand;"
										onclick="javascript:lupas.muestraTabla('TerminoIN','principio', '', '');"
										alt="Buscar Término" title="Buscar Término" />
										<label class="campoObligatorio" id="campoObligatorio_termino" title="Campo obligatorio">*</label>
									</c:if>
								</td>
								
								<td class="literal"><abbr title="Subtérmino">Subt.</abbr></td>
								<td class="literal">
								<form:input path="termino.id.subtermino" id="subtermino"
									size="1" maxlength="1" cssClass="dato"
									onchange="this.value=this.value.toUpperCase();" tabindex="4"/>
									<c:if test="${requestScope.modoLectura!='true' && requestScope.modoLectura!='modoLectura'}">
										<img src="jsp/img/magnifier.png" style="cursor: hand;"
										onclick="javascript:lupas.muestraTabla('TerminoIN','principio', '', '');"
										alt="Buscar Término" title="Buscar Término" />
										<label class="campoObligatorio" id="campoObligatorio_subtermino" title="Campo obligatorio">*</label>
									</c:if>
								</td>
							</tr>
						</table>
					</fieldset>
					<fieldset style="float: right">
						<legend class="literal">Coordenadas</legend>
						<table align="center">
							<tr>
								<td class="literal"><abbr title="Latitud">Lat.</abbr></td>
								<td class="literal">
								
								
								<form:input path="latitud" id="latitud"
									size="6" maxlength="6" cssClass="dato"
									onchange="this.value=this.value.toUpperCase();" tabindex="5"/>
								
								<label class="campoObligatorio" id="campoObligatorio_latitud" title="Campo obligatorio">*</label>	
								</td>
								
								<td class="literal"><abbr title="Longitud">Lon.</abbr></td>
								<td class="literal">
								
								<form:input path="longitud" id="longitud"
									size="6" maxlength="6" cssClass="dato"
									onchange="this.value=this.value.toUpperCase();" tabindex="6"/>
								<label class="campoObligatorio" id="campoObligatorio_longitud" title="Campo obligatorio">*</label>	
								</td>
							</tr>
						</table>
					</fieldset>
					<table style="margin: 10px;" border="0" width="100%">
						<tr>
							<td class="literal" style="padding-left: 15px;">REGA</td>
							<td class="literal">
								<form:input path="rega" id="rega"
									maxlength="14" cssClass="dato"
									onchange="this.value=this.value.toUpperCase();" tabindex="7"/>
								<label class="campoObligatorio" id="campoObligatorio_rega" title="Campo obligatorio">*</label>
							</td>
							<td class="literal" style="padding-left: 15px;">Sigla</td>
							<td class="literal">
								<form:input path="sigla" id="sigla"
									maxlength="3" cssClass="dato"
									onchange="this.value=this.value.toUpperCase();" tabindex="8"/>
								<label class="campoObligatorio" id="campoObligatorio_sigla" title="Campo obligatorio">*</label>
							</td>
							<td class="literal" style="padding-left: 15px;">Subexplotación</td>
							<td class="literal">
								<form:input path="subexplotacion" id="subexplotacion"
									size="3" maxlength="3" cssClass="dato"
									onchange="this.value=this.value.toUpperCase();" tabindex="9"/>
								<label class="campoObligatorio" id="campoObligatorio_subexplotacion" title="Campo obligatorio">*</label>
							</td>
							<td class="literal" style="padding-left: 15px;">Especie</td>
							<td class="literal">
								<form:input path="especie" id="especie"
									size="3" maxlength="3" cssClass="dato"
									onchange="javascript:lupas.limpiarCampos('desc_especie');"
									tabindex="10"/>
									
								<form:input cssClass="dato" id="desc_especie" path="nomespecie" size="16"
								readonly="true"/>
								<c:if test="${requestScope.modoLectura!='true' && requestScope.modoLectura!='modoLectura'}">
									<img src="jsp/img/magnifier.png" style="cursor: hand;"
									onclick="javascript:lupas.muestraTabla('VistaEspecieIN','principio', '', '');"
									alt="Buscar Especie" title="Buscar Especie" />
									<label class="campoObligatorio" id="campoObligatorio_especie" title="Campo obligatorio">*</label>
								</c:if>
							</td>
							<td class="literal" style="padding-left: 15px;">Régimen</td>
							<td class="literal">
								<form:input path="regimen" id="regimen"
									size="3" maxlength="3" cssClass="dato"
									onchange="javascript:lupas.limpiarCampos('desc_regimen');"
									tabindex="11"/>
									
								<form:input cssClass="dato" id="desc_regimen" path="nomregimen" size="16"
								readonly="true"/>
								<c:if test="${requestScope.modoLectura!='true' && requestScope.modoLectura!='modoLectura'}">
									<img src="jsp/img/magnifier.png" style="cursor: hand;"
									onclick="javascript:lupas.muestraTabla('VistaRegimenIN','principio', '', '');"
									alt="Buscar Régimen" title="Buscar Régimen" />
									<label class="campoObligatorio" id="campoObligatorio_regimen" title="Campo obligatorio">*</label>
								</c:if>
							</td>
							
							<td class="literal">
								<a class="bot" id="btnPanelGruposRaza" href="javascript:irPanelGrupoRaza()">&gt;&gt;</a>
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
			 -->
			 <!-- Fin de Datos Identificativos -->
			
			<!-- Panel de Grupos de Raza -->
			<div id="divGrupoRaza" name="divGrupoRaza" style="width:100%">			
				<fieldset style="width:100%;">
					<legend class="literal">Grupos de Raza</legend>
					<!-- Lupas -->
					<table align="center" width="100%"> 
						<!-- Botón de paso de panel -->
						<tr>
							<td colspan="9" align="right"><a class="bot" id="btnPanelGruposRaza" href="javascript:irPanelDatosIdentificativos()">&lt;&lt;</a></td>
						</tr>
						<tr>
							<td class="literal" width="10%">Grupo de Raza</td>
							<td width="23%">
								<form:input path="grupoRazaAnexos[0].codgruporaza" size="5" maxlength="4" cssClass="dato" id="codgrupoRaza"
									onchange="javascript:lupas.limpiarCampos('desGrupoRaza');" tabindex="12"/>
								<form:input path="grupoRazaAnexos[0].nomgruporaza" cssClass="dato" id="desGrupoRaza" size="25" readonly="true"/>
								<c:if test="${requestScope.modoLectura!='true' && requestScope.modoLectura!='modoLectura'}">
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('VistaGruposRazasIN','principio', '', '');" alt="Buscar Grupo de Raza" title="Buscar Grupo de Raza" />
									<label class="campoObligatorio" id="campoObligatorio_codgrupoRaza" title="Campo obligatorio">*</label>
								</c:if>
							</td>
							<td class="literal" width="10%">Tipo de Capital</td>
							<td width="23%">
								<form:input path="grupoRazaAnexos[0].codtipocapital" size="5" maxlength="4" cssClass="dato" id="codtipocapital"
									onchange="javascript:lupas.limpiarCampos('desTipoCapital');javascript:comprobarInputNumAnimales(this.value,'datosExplotacionesAnexo')" tabindex="13"/>
								<form:input path="grupoRazaAnexos[0].nomtipocapital" cssClass="dato" id="desTipoCapital" size="25" readonly="true"/>
								<c:if test="${requestScope.modoLectura!='true' && requestScope.modoLectura!='modoLectura'}">
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('VistaTipoCapGrupoNegocioIN','principio', 'id.codtipocapital', 'ASC');" alt="Buscar Tipo de Capital" title="Buscar Tipo de Capital" />
									<label class="campoObligatorio" id="campoObligatorio_codtipocapital" title="Campo obligatorio">*</label>
								</c:if>
							</td>
							<td class="literal" width="10%">Tipo de Animal</td>
							<td width="23%">
								<form:input path="grupoRazaAnexos[0].codtipoanimal" size="5" maxlength="4" cssClass="dato" id="codtipoanimal"
									onchange="javascript:lupas.limpiarCampos('desTipoAnimal');" tabindex="14"/>
								<form:input path="grupoRazaAnexos[0].nomtipoanimal" cssClass="dato" id="desTipoAnimal" size="25" readonly="true"/>
								<c:if test="${requestScope.modoLectura!='true' && requestScope.modoLectura!='modoLectura'}">
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('VistaTiposAnimalIN','principio', '', '');" alt="Buscar Tipo de Animal" title="Buscar Tipo de Animal" />
									<label class="campoObligatorio" id="campoObligatorio_codtipoanimal" title="Campo obligatorio">*</label>
								</c:if>
							</td>				
						</tr>
					</table>
					
					<!-- INI - Panel de Datos Variables -->
					<input type="hidden" class="dato" id="cod_cpto_lupa_factores"/>
					<input type="hidden" class="dato" id="valor_lupa_factores" onchange="javascript:copiaMarcadoFactoresValor();"/>
					<input type="hidden" class="dato" id="desc_lupa_factores" onchange="javascript:copiaMarcadoFactoresDesc();"/>
					
					<fieldset style="width:100%;">
						<legend class="literal">Datos Variables</legend>
						<div id="contenedorDV" width="100%" style="height: 205px; position: relative;" align="left">
							<c:set var="numTabIndex">15</c:set>
							<c:forEach items="${listaDV}" var="dv">
								<c:choose>
  									<c:when test="${dv.tipoCampo.idtipo == 1}"> 
										<div style="position: absolute; top : ${dv.y}px; left : ${dv.x}px; width: ${dv.ancho}px; height: ${dv.alto}px;">
											<table border="0" width="100%">
												<tr>
												<td class="detalI" nowrap="nowrap"><c:out value="${dv.etiqueta}"/>&nbsp;</td>
												<td><input type="text" class="dato" style="width: ${dv.ancho}px; height: ${dv.alto}px;" id="cod_cpto_${dv.id.codconcepto}" name="dvCpto_${dv.id.codconcepto}" ${dv.disabled  == 'S' ? 'disabled' : ''} tabindex="${numTabIndex}"/></td>
												</tr>
											</table>
										</div> 
									</c:when>									
  									<c:when test="${dv.tipoCampo.idtipo == 6}">
  										<div style="position: absolute; top : ${dv.y}px; left : ${dv.x}px; width: ${dv.ancho}px; height: ${dv.alto}px;">
											<table border="0" width="100%">
												<tr>
												<td class="detalI" nowrap="nowrap"><c:out value="${dv.etiqueta}"/>&nbsp;</td>
												<td><input type="text" class="dato" id="cod_cpto_${dv.id.codconcepto}" style="width: 50px; height: ${dv.alto}px;" name="dvCpto_${dv.id.codconcepto}" ${dv.disabled  == 'S' ? 'disabled' : ''} onchange="javascript:lupas.limpiarCampos('des_cpto_${dv.id.codconcepto}');" tabindex="${numTabIndex}"/></td>
												<td><input type="text" class="dato" id="des_cpto_${dv.id.codconcepto}" style="width: ${dv.ancho}px; height: ${dv.alto}px;" name="${dv.etiqueta}" readonly="readonly"/></td>
												<c:if test="${requestScope.modoLectura!='true' && requestScope.modoLectura!='modoLectura'}">
													<td><img src="jsp/img/magnifier.png" style="cursor: hand; ${dv.disabled  == 'S' ? 'display: none' : ''}" onclick="javascript:actualizaCptoFactores(${dv.id.codconcepto});lupas.muestraTabla('${dv.origenDatos.sql}','principio', '', '');"/></td>
												</c:if>
												</tr>
											</table>
										</div>
  									</c:when>
  								</c:choose>
  								<c:set var="numTabIndex">${numTabIndex}+1</c:set>
							</c:forEach>
						</div>
						
					</fieldset>
					<br/>
					
					<!-- Pinta los valores de la lista de datos variables -->
					<c:forEach items="${explotacionAnexoBean.grupoRazaAnexos}" var="grExpDv">
						<c:forEach items="${grExpDv.datosVarExplotacionAnexos}" var="dvExp">
							<script>pintarValorDV(${dvExp.codconcepto}, ${dvExp.valor}, '${dvExp.desValor}', ${grExpDv.codgruporaza}, ${grExpDv.codtipocapital}, ${grExpDv.codtipoanimal});</script>
							<input type="hidden" id="gr_${grExpDv.id}" name="gr_${grExpDv.id}" value="${dvExp.codconcepto}_${dvExp.valor}_${dvExp.desValor}">
						</c:forEach>
					</c:forEach>
					
					<!-- FIN - Panel de Datos Variables -->
					
					<!-- Número de animales, precio y tabla de grupos de raza -->
					<table align="center" width="100%" border="0">  
						<tr>
							<!-- Número de animales, precio y calcular -->
							<td width="50%" valign="top">
								<table align="center" width="100%" border="0"> 
									<tr>
										<td class="literal" width="10%">N&uacute;mero</td>
										<td width="10%">
											<form:input path="grupoRazaAnexos[0].numanimales" size="9" maxlength="9" cssClass="dato" id="numanimales" tabindex="${numTabIndex}+1"/>
											<label class="campoObligatorio" id="campoObligatorio_numanimales" title="Campo obligatorio">*</label>
										</td>
										<td class="literal" width="10%">Precio</td>
										<td width="15%">
											<form:hidden path="grupoRazaAnexos[0].precioAnimalesModuloAnexos[0].codmodulo" id="codModuloPrecio"/>
										   	<form:hidden path="" id="pMin"/>
											<form:input path="grupoRazaAnexos[0].precioAnimalesModuloAnexos[0].precio" size="10" maxlength="12" cssClass="dato" id="precio"  tabindex="${numTabIndex}+1"/>
											<label class="campoObligatorio" id="campoObligatorio_precio" title="Campo obligatorio">*</label>
										</td>
										<td width="3%" align="center">
											<c:if test="${requestScope.modoLectura!='true' && requestScope.modoLectura!='modoLectura'}">
												<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:mostrarPopUpPreciosPorModulo();" alt="Detalle de precios" title="Detalle de precios" />
											</c:if>
										</td>
										<td width="10%" align="center">
											<a class="bot" id="btnCalcular" href="javascript:calcularPrecio()">Calcular</a>
										</td>
										<td width="45%" align="center">
											<div id="buttons2" style="display:none;">
												<a class="bot" id="guardarGrupoRaza" href="javascript:guardarGrupoRaza(0)">Guardar Grupo de Raza</a>
											</div>
										</td>
									</tr>
									<tr>
										<td colspan="6"></td>
										<td align="center">
											<div id="buttons3" style="display:none;">
												<a class="bot" id="guardarGrupoRazaReplicar" href="javascript:guardarGrupoRaza(1)">Guardar G.R. y Replicar</a>
											</div>
										</td>
									</tr> 
								</table>
							</td>
							
							
							<!-- Tabla de grupos de raza -->
							<td rowspan="2" width="50%" valign="center">
								<table align="center" width="95%" border="0" id="gr">
									<thead>
										<tr>
											<th class="literalbordeCabecera" width="15%">Acciones</th>
											<th class="literalbordeCabecera" width="17%">G. Raza</th>
											<th class="literalbordeCabecera" width="17%">T. Capital</th>
											<th class="literalbordeCabecera" width="17%">T. Animal</th>
											<th class="literalbordeCabecera" width="17%">Número</th>
											<th class="literalbordeCabecera" width="17%">Precio</th>
										</tr>
									</thead>
									<tbody> 
										<c:set var="filaGrupoRaza">1</c:set>
										<c:forEach items="${explotacionAnexoBean.grupoRazaAnexos}" var="grupoRazaExplotacion">
											<c:if test="${not empty grupoRazaExplotacion.id }">
												<tr>
													<td class="literalborde" align="center">
														<c:choose>
															<c:when test="${requestScope.modoLectura=='true' or requestScope.modoLectura=='modoLectura'}">
																<a href="javascript:editarGrupoRazaExplotacion(false,${grupoRazaExplotacion.id}, ${grupoRazaExplotacion.codgruporaza}, '${grupoRazaExplotacion.nomgruporaza}', ${grupoRazaExplotacion.codtipocapital}, '${grupoRazaExplotacion.nomtipocapital}', ${grupoRazaExplotacion.codtipoanimal}, '${grupoRazaExplotacion.nomtipoanimal}', ${grupoRazaExplotacion.numanimales}, 'precioExpl${grupoRazaExplotacion.codgruporaza}${grupoRazaExplotacion.codtipocapital}${grupoRazaExplotacion.codtipoanimal}');"><img src="jsp/img/displaytag/information.png" alt="Consultar" title="Consultar" /></a>
															</c:when>
															<c:otherwise>
																<a href="javascript:editarGrupoRazaExplotacion(false, ${grupoRazaExplotacion.id}, ${grupoRazaExplotacion.codgruporaza}, '${grupoRazaExplotacion.nomgruporaza}', ${grupoRazaExplotacion.codtipocapital}, '${grupoRazaExplotacion.nomtipocapital}', ${grupoRazaExplotacion.codtipoanimal}, '${grupoRazaExplotacion.nomtipoanimal}', ${grupoRazaExplotacion.numanimales}, 'precioExpl${grupoRazaExplotacion.codgruporaza}${grupoRazaExplotacion.codtipocapital}${grupoRazaExplotacion.codtipoanimal}'); javascript:comprobarInputNumAnimales('${grupoRazaExplotacion.codtipocapital}','datosExplotacionesAnexo');"><img src="jsp/img/displaytag/edit.png" alt="Editar" title="Editar" id="iconoEditar_${filaGrupoRaza}"/></a>
																<a href="javascript:editarGrupoRazaExplotacion(true, ${grupoRazaExplotacion.id}, ${grupoRazaExplotacion.codgruporaza}, '${grupoRazaExplotacion.nomgruporaza}', ${grupoRazaExplotacion.codtipocapital}, '${grupoRazaExplotacion.nomtipocapital}', ${grupoRazaExplotacion.codtipoanimal}, '${grupoRazaExplotacion.nomtipoanimal}', ${grupoRazaExplotacion.numanimales}, 'precioExpl${grupoRazaExplotacion.codgruporaza}${grupoRazaExplotacion.codtipocapital}${grupoRazaExplotacion.codtipoanimal}'); javascript:comprobarInputNumAnimales('${grupoRazaExplotacion.codtipocapital}', 'datosExplotacionesAnexo');"><img src="jsp/img/displaytag/duplicar.png" alt="Duplicar" title="Duplicar" id="duplicar_${grupoRazaExplotacion.id}"/></a>
																<a href="javascript:eliminarGrupoRazaExplotacion(${grupoRazaExplotacion.id}, ${grupoRazaExplotacion.codgruporaza}, '${grupoRazaExplotacion.nomgruporaza}', ${grupoRazaExplotacion.codtipocapital}, '${grupoRazaExplotacion.nomtipocapital}', ${grupoRazaExplotacion.codtipoanimal}, '${grupoRazaExplotacion.nomtipoanimal}', ${grupoRazaExplotacion.numanimales}, 'precioExpl${grupoRazaExplotacion.codgruporaza}${grupoRazaExplotacion.codtipocapital}${grupoRazaExplotacion.codtipoanimal}');"><img src="jsp/img/displaytag/delete.png" alt="Eliminar" title="Eliminar" /></a>
															</c:otherwise>
														</c:choose>
														
													</td>
													<td class="literalborde" align="center">${grupoRazaExplotacion.codgruporaza}</td>
													<td class="literalborde" align="center">${grupoRazaExplotacion.codtipocapital}</td>
													<td class="literalborde" align="center">${grupoRazaExplotacion.codtipoanimal}</td>
													<td class="literalborde" align="center">${grupoRazaExplotacion.numanimales}</td>
													<td class="literalborde" align="center">
														<c:forEach items="${grupoRazaExplotacion.precioAnimalesModuloAnexos}" var="preciosExpl">
															${preciosExpl.precio}
															<input type="hidden" id="precioExpl${grupoRazaExplotacion.codgruporaza}${grupoRazaExplotacion.codtipocapital}${grupoRazaExplotacion.codtipoanimal}" value="${preciosExpl.precio}" />
														</c:forEach>
													</td>
												</tr>
												<c:set var="filaGrupoRaza">${filaGrupoRaza}+1</c:set>
											</c:if>
										</c:forEach>
									</tbody>
								</table>
							</td>
						</tr>
				</div>		
					<!-- Tabla de COBERTURAS DE LA EXPLOTACION -->		
				<%@ include file="/jsp/moduloExplotaciones/explotaciones/coberturasExplotacionUnificado.jsp"%>
		
				</fieldset>
			</div>
			
		</div>
		</br>
		<div id="buttons" style="display:none;">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr align="center">
					<td align="center" width="25%">
						&nbsp;
					</td>
					<td align="center" width="50%">
						<a class="bot" id="btnGuardarSalir" href="javascript:guardarSalir()">Guardar y salir</a>
						<a class="bot" id="btnGuardarNuevo" href="javascript:guardarNuevo()">Guardar y nuevo</a>
						<a class="bot" id="btnGuardarReplicar" href="javascript:guardarReplicar()">Guardar y replicar</a>
					</td>
					<td align="right" width="25%">
						<a class="bot" id="btnLimpiar" href="javascript:limpiar()">Limpiar</a>
						<a class="bot" id="btnVolver" href="javascript:volver()">Volver</a>
					</td>
				</tr>
			</table>
		</div>
		</form:form>		
		<br/>
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaProvinciaIN.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaComarcaIN.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaTerminoIN.jsp"%>
		
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaEspecieIN.jsp"%>
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaRegimenIN.jsp"%>
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaGruposRazasIN.jsp"%>						   
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaTipoCapGrupoNegocioIN.jsp"%>
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaTiposAnimalIN.jsp"%>
		
		
		<%@ include file="/jsp/moduloExplotaciones/explotaciones/popupPreciosModulo.jsp"%>
		<%@ include file="/jsp/moduloExplotaciones/explotaciones/popupRegaUbicaciones.jsp"%>
				
		<!-- Lupas para los orígenes de datos de los datos variables -->
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaAdapRiesgoEX.jsp"%>
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaAdapRiesgoGR.jsp"%>
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaAdapRiesgoTC.jsp"%>
		<!--Autorización especial -->
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaAutorizacionEspecial.jsp"%>
		<!--Sistema almacenamiento -->
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaSistemaAlmacenamiento.jsp"%>
		<!--Condiciones particulares -->
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaCondicionesParticulares.jsp"%>
		<!--Empresa gestora -->
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaEmpresaGestora.jsp"%>
		<!--Alojamiento -->
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaAlojamiento.jsp"%> 
		<!--Calidad de la producción -->
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaCalidadProduccion.jsp"%>
		<!--Calificación del saneamiento -->
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaCalificacionSaneamiento.jsp"%>
		<!--Calificación sanitaria -->
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaCalificacionSanitaria.jsp"%>
		<!--Control Oficial lechero -->
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaControlOficialLechero.jsp"%>
		<!--Cuenca Hidrográfica -->
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaCuencaHidrografica.jsp"%>
		<!--Duración período productivo -->
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaDuracionPeriodoProductivo.jsp"%>
		<!--Excepción contratación - Explotación -->
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaExcepContratacionExplot.jsp"%>
		<!--Excepción contratación - Póliza-->
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaExcepContratacionPoliza.jsp"%>
		<!--IGP/DAO Ganado-->
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaIgpDoGanado.jsp"%>
		<!--Pureza-->
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaPureza.jsp"%>
		<!--Tipo asegurado ganado-->
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaTipoAseguradoGanado.jsp"%>
		<!--Tipo ganadería-->
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaTipoGanaderia.jsp"%>		
		<!--Sistema de producción de ganado-->
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaSistemaProdGanado.jsp"%>
		<!--Destino de ganado-->
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaDestinoGanado.jsp"%>
		<!--Vista por factores-->
		<%@ include file="/jsp/common/lupas/lupasODGanado/lupaVistaPorFactores.jsp"%>
		
	</body>
</html>