<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<fmt:bundle basename="displaytag">
	<c:set var="numReg">
		<fmt:message key="numElementsPag" />
	</c:set>
</fmt:bundle>
<fmt:bundle basename="agp">
	<c:set var="numEle">
		<fmt:message key="visores.numElements" />
	</c:set>
	<c:set var="codIndicadorRegimen">
		<fmt:message key="codIndicadorRegimen" />
	</c:set>
	<c:set var="listaCodRegimen"
		value='${fn:split(codIndicadorRegimen,",") }' />
	<c:set var="descIndicadorRegimen">
		<fmt:message key="descIndicadorRegimen" />
	</c:set>
	<c:set var="listaDesRegimen"
		value='${fn:split(descIndicadorRegimen,",") }' />
</fmt:bundle>
<fmt:bundle basename="agp">
	<c:set var="numRegImpresion">
		<fmt:message key="impresionnumRegAseg"/>
	</c:set>
</fmt:bundle> 
<html>
	<head>
		<c:if test="${cargaAseg eq ''}">			
			<title>Mantenimiento de Asegurados</title>
		</c:if>
		<c:if test="${cargaAseg eq 'cargaAseg'}">			
			<title>Carga de Asegurados</title>
		</c:if>
		<%@ include file="/jsp/common/static/metas.jsp"%>
			
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
		
		<script type="text/javascript" src="jsp/js/util.js"></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js" charset="UTF-8"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>	
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/displayTagAjaxSelectedRow.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
		<script type="text/javascript" src="jsp/moduloAdministracion/asegurados/asegurados.js" charset="UTF-8"></script>
		<script type="text/javascript" src="jsp/js/ajaxfileupload.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.filestyle.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
		<script type="text/javascript" src="jsp/js/calendar.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.13.1/xlsx.full.min.js"></script> 
		<%@ include file="/jsp/js/draggable.jsp"%>
		
		
		<script type="text/javascript" charset="UTF-8">
		
			$(document).ready(function(){
				<c:if test="${empty listaAsegurados}">
					$('#btnImprimir').hide();
				</c:if>
				// muestra el popup con los datos actualizados por el Ws de un asegurado
				if ($('#showPopupAsegurados').val() == "true"){
					$.unblockUI();
					$('#overlay').show();
					$('#divAseguradoSW').show();
				} 
				if ($('#showPopupPolAsegurados').val() == "true") {
		      	  $('#overlay').show();
		      	  $('#divAseguradoPol').show();
		        }
				if($('#cargaAseg').val() == "cargaAseg"){
					$("#btnSocios").hide();
					$("#btnDatosAdic").hide();
				}
				$("input[type=file]").filestyle({
					image: "jsp/img/boton_examinar.png",
					imageheight : 22,
					imagewidth : 82,
					width : 250
				});
				
				<c:if test="${cargaAseg eq 'cargaAseg' and alertaSubvencionable}">
					jAlert('Asegurado no subvencionable','Aviso');
				</c:if>
			});
			
			function setUsuarioSesion(){
				$('#codusuario').val("${sessionScope.usuario.codusuario}");
			}			
		</script>
	</head>
<c:if test="${cargaAseg eq ''}">			
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0"
		onload="SwitchMenu('sub2');generales.cifnifSeleccionado();generales.fijarFila();">
</c:if>
<c:if test="${cargaAseg eq 'cargaAseg'}">			
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" 
	onload="SwitchMenu('sub3');generales.fijarFila()">
</c:if>

<%@ include file="/jsp/common/static/cabecera.jsp"%>
<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<c:if test="${perfil == 0 && cargaAseg eq ''}">	
						<td align="left">
							&nbsp;<a class="bot" id="btnImportar" href="javascript:showPopUpImportarCsv()">Importar</a>
						</td>
					</c:if>
					
					<td align="center">
						<c:if test="${cargaAseg eq ''}">	
							<a class="bot" id="btnSubvenciones" href="javascript:abrirPopupSeleccionLinea()">Subvenciones</a>
						</c:if>
					</td>
					
					<td align="right">			
						<a class="bot" id="btnActualizaAseguradoWS" hidden="true" href="javascript:mostrarFormularioAsegurados()">Actualizar datos Asegurados WS</a>
						<a class="bot" id="btnAlta" href="javascript:alta()">Alta</a> 	
						<a class="bot" id="btnModificar" style="display:none" href="javascript:guardarModificaciones()">Modificar</a>
						<a class="bot" id="btnConsultar" href="javascript:consultar()">Consultar</a> 
						<a class="bot" id="btnLimpiar" href="javascript:limpiar()">Limpiar</a>
					</td>
				</tr>
			</table>
		</div>
		<!-- Contenido de la pagina -->
		<div class="conten" style="padding:3px; width: 97%">
		<c:if test="${cargaAseg eq ''}">			
			<p class="titulopag" align="left">Administraci&oacute;n de Asegurados</p>
		</c:if>
		<c:if test="${cargaAseg eq 'cargaAseg'}">			
			<p class="titulopag" align="left">Carga de Asegurados</p>
		</c:if>
		
			<form:form name="main" id="main" action="asegurado.html" method="post" commandName="aseguradoBean">
				<form:hidden path="id" id="idAsegurado" />
				<input type="hidden" name="method" id="method" value="doConsulta"/>				
				<input type="hidden" name="origenLlamada" id="origenLlamada" value="${origenLlamada}"/>			
				<input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
				<input type="hidden" name="showPopupPolAsegurados" id="showPopupPolAsegurados" value="${showPopupPolAsegurados}"/>
				<input type="hidden" name="perfil" id="perfil" value="${perfil}" />
				<input type="hidden" name="polizasAsegurado" id="polizasAsegurado" value="${polizasAsegurado}" />
				<input type="hidden" name="listIdPolizas" id="listIdPolizas" value="${listIdPolizas}" />
				<input type="hidden" name="subv20" id="subv20" value="${subv20}" />
				<input type="hidden" name="subv10" id="subv10" value="${subv10}" />
				<input type="hidden" name="usuarioAsegurado" id="usuarioAsegurado" value=""/>
				<input type="hidden" name="idAseguradoP" id="idAseguradoP" value="${idAseguradoP}"/>
				<input type="hidden" name="nifcifBusqueda" id="nifcifBusqueda" value="${nifcifP}"/>
				<input type="hidden" name="entidadSubstr" id="entidadSubstr" value="">
				<input type="hidden" name="showPopupAsegurados" id="showPopupAsegurados" value="${showPopupAsegurados}">
				<input type="hidden" name="fechaRevisionN" id="fechaRevisionN" value="${fechaRevisionN}"/>
				<input type="hidden" name="formato" id="formato" value="${formato}"/>
				<input type="hidden" name="cargaAseg" id="cargaAseg" value="${cargaAseg}"/>
				<input type="hidden" name="formulario" id="formulario" value="asegurados"/>
				<input type="hidden" id="comeFrom" value="${comeFrom }" />
				<input type="hidden" name="impresionnumRegAseg" id="impresionnumRegAseg" value="${impresionnumRegAseg}" />
				<input type="hidden" name="listMsgError" id="listMsgError" value="${listMsgError}" />
				<input type="hidden" name="fechaRevision" id="fechaRevision" value="${fechaRevision}"/>
				<input type="hidden" name="error" id="error" value="${error}"/>
				
				<input type="hidden" name="idsRowsChecked" id="idsRowsChecked" value="${idsRowsChecked}"/>
				<input type="hidden" name="checkTodo" id="checkTodo" value="${checkTodo}"/>
				<input type="hidden" name="aseguradosString" id="aseguradosString" value="${aseguradosString}"/>
				<input type="hidden" name="numElem" id="numElem" value="${numElem}"/>
			<div id="avisoErrores">		
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			</div>
			<div class="panel2 isrt">
				<table width="100%" border="2">
						<tr align="left">
							
							<input type="hidden" id="oficina" />
							
							<!-- Si es administracion de asegurados -->
							<c:if test="${cargaAseg eq ''}">	
								<td class="literal">Entidad</td>
								<td class="literal" >
								
									<c:if test="${perfil == 0 || perfil == 5}">			
										<form:input path="entidad.codentidad" size="2" maxlength="4"	cssClass="dato" id="entidad" tabindex="1" onchange="javascript:lupas.limpiarCampos('desc_entidad');"   />
										<form:input path="entidad.nomentidad" cssClass="dato"	id="desc_entidad" size="35" readonly="true"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');" alt="Buscar Entidad" title="Buscar Entidad"/>
										
									</c:if>
									<c:if test="${perfil > 0 && perfil < 5}">
										<form:input path="entidad.codentidad" size="2" maxlength="4" cssClass="dato" id="entidad" tabindex="1" readonly="true"/>
										<form:input path="entidad.nomentidad" cssClass="dato"	id="desc_entidad" size="35" readonly="true"/>
										
									</c:if>
									<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_entidad"> *</label>
								</td>
							</c:if>
							
							<!-- Si es carga de asegurados -->
							<c:if test="${cargaAseg eq 'cargaAseg'}">			
								<td class="literal">Entidad</td>
								<td class="literal" >
										<form:input path="entidad.codentidad" size="2" maxlength="4" cssClass="dato" id="entidad" tabindex="1" readonly="true"/>
										<form:input path="entidad.nomentidad" cssClass="dato"	id="desc_entidad" size="35" readonly="true"/>
										<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_entidad"> *</label>
								</td>
							</c:if>
							
							
							<!-- Si es administracion de asegurados -->
							<c:if test="${cargaAseg eq ''}">	
								<td class="literal" >E-S Med.  </td>
								<td class="literal" >		
								    <c:if test="${((perfil == 1 || perfil == 3) && externo == 1) || perfil == 4}">
								    	<form:input path="usuario.subentidadMediadora.id.codentidad" size="4" maxlength="4" cssClass="dato" id="entmediadora" tabindex="1" readonly="true" />
								    	<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_entmediadora">*</label>
										<form:input path="usuario.subentidadMediadora.id.codsubentidad" size="4" maxlength="4" cssClass="dato" id="subentmediadora" tabindex="1" readonly="true" />
										<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_subentmediadora">*</label>
									</c:if>	
								    <c:if test="${((perfil != 1 && perfil != 3) || externo == 0) && perfil != 4}">
									    <form:input path="usuario.subentidadMediadora.id.codentidad" size="4" maxlength="4" cssClass="dato" id="entmediadora" tabindex="1" />
									    <label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_entmediadora">*</label>
										<form:input path="usuario.subentidadMediadora.id.codsubentidad" size="4" maxlength="4" cssClass="dato" id="subentmediadora" tabindex="1" />
										<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_subentmediadora">*</label>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');" alt="Buscar E-S Med." title="Buscar E-S Med." />
								    </c:if>
								</td>
							</c:if>
							<!-- Si es carga de asegurados -->
							<c:if test="${cargaAseg eq 'cargaAseg'}">	
								<td class="literal" >E-S Med.  </td>
								<td class="literal" >		
								    	<form:input path="usuario.subentidadMediadora.id.codentidad" size="4" maxlength="4" cssClass="dato" id="entmediadora" tabindex="1" readonly="true" />
								    	<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_entmediadora">*</label>
										<form:input path="usuario.subentidadMediadora.id.codsubentidad" size="4" maxlength="4" cssClass="dato" id="subentmediadora" tabindex="1" readonly="true" />
										<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_subentmediadora">*</label>
								</td>
							</c:if>
							
							
							<td class="literal">Tipo ident.</td>
							<td class="literal">
								<form:select path="tipoidentificacion" cssClass="dato" id="tipoIdentificacion" cssStyle="width:70" onchange="javascript:generales.cifnifSeleccionado();" tabindex="2">
									<form:option value="">Todos</form:option>
									<form:option value="CIF">CIF</form:option>
									<form:option value="NIF">NIF</form:option>
									<form:option value="NIE">NIE</form:option>
								</form:select>
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_tipoIdentifiacion"> *</label>
							</td>
							<td class="literal" >NIF/CIF/NIE</td>
							<td class="literal" >
								<form:input path="nifcif" size="10" maxlength="9" cssClass="dato" id="nifcif" tabindex="3" onchange="this.value=this.value.toUpperCase();this.value=generales.lpadNIF(this.value);"/>
								<c:if test="${cargaAseg eq '' && perfil == 0}">
									<a href="javascript:getDatosAseguradoWService()"><img src="jsp/img/jmesa/clear.gif" alt="Actualizar" title="Actualizar"/></a>
								</c:if>
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_nifcif"> *</label>
							</td>
						</tr>
					
					
						<tr align="left">
							<td colspan="1" class="literal">Nombre</td>
							<td class="literal">
								<form:input path="nombre" size="20"	maxlength="20" cssClass="dato" id="nombre" tabindex="5" onchange="this.value=this.value.toUpperCase();"/>
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_nombre"> *</label>
							</td>
							
							<td class="literal" >1<sup style="vertical-align:'super';">er</sup> Apellido</td>
							<td class="literal" colspan="3" >
								<form:input path="apellido1" size="40" maxlength="40" cssClass="dato" id="apellido1" tabindex="6" onchange="this.value=this.value.toUpperCase();"/>
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_apellido1"> *</label>
							</td>
							
							<td class="literal" >2&deg; Apellido</td>
							<td class="literal">
								<form:input path="apellido2" size="25" maxlength="40" cssClass="dato" id="apellido2" tabindex="7" onchange="this.value=this.value.toUpperCase();"/>
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_apellido2"> *</label>
							</td>
						</tr>
					
				
						<tr align="left">
							<td class="literal">Raz&oacute;n social</td>
							<td  class="literal" >
								<form:input path="razonsocial" size="45" maxlength="50" cssClass="dato" id="razonsocial" tabindex="8" onchange="this.value=this.value.toUpperCase();"/>
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_razonsocial"> *</label>
							</td>
							
							
							<td class="literal">Usuario</td>
							<td class="literal">	
						    	<c:if test="${perfil == 4 }">
									<form:input path="usuario.codusuario" size="8" maxlength="8" cssClass="dato" id="codusuario" tabindex="9" readonly="true" onchange="this.value=this.value.toUpperCase();"/>
								</c:if>
								<c:if test="${perfil != 4}">
									<form:input path="usuario.codusuario" size="8" maxlength="8" cssClass="dato" id="codusuario" tabindex="9" onchange="this.value=this.value.toUpperCase();"/>
									<img  border="1" id="iconoLupa" src="jsp/img/magnifier.png" style="cursor: hand;" onclick="document.getElementById('codusuario').value='';lupas.muestraTabla('UsuarioEM','principio', '', '');" alt="Buscar Usuario" />
								</c:if>
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_codusuario"> *</label>
							</td>
						</tr>
					</table>
			</div>
			<div class="panel2 isrt">
				<table width="95%">
					<tr align="left">
						<td class="literal">V&iacute;a</td>
						<td class="literal">
							<form:input path="via.clave" size="2" maxlength="2" cssClass="dato" id="via" tabindex="10" onchange="javascript:this.value=this.value.toUpperCase();lupas.limpiarCampos('desc_via');"/>
							<form:input path="via.nombre" cssClass="dato"	id="desc_via" size="15" readonly="true"/>
							<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Via','principio', '', '');" alt="Buscar Via" title="Buscar Via" />
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_via"> *</label>
						</td>
						<td class="literal">Domicilio</td>
						<td colspan="3" class="literal">
							<form:input path="direccion" size="30" maxlength="22" cssClass="dato" id="direccion" tabindex="11" onchange="this.value=this.value.toUpperCase();" />
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_direccion"> *</label>
						</td>
					</tr>
					<tr>
						<td class="literal" align="left">N&deg;</td>
						<td class="literal">
							<form:input path="numvia" size="5" maxlength="5" cssClass="dato" id="numvia" tabindex="12"/>
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_numvia"> *</label>
						</td>
						<td class="literal">Piso</td>
						<td class="literal">
							<form:input path="piso" size="5" maxlength="5" cssClass="dato" id="piso" tabindex="13"/>
						</td>
						<td class="literal">Bloque</td>
						<td class="literal">
							<form:input path="bloque" size="3"	maxlength="3" cssClass="dato" id="bloque" tabindex="14"/>
						</td>
					
						<td class="literal" >Escalera</td>
						<td class="literal">
							<form:input path="escalera" size="3" maxlength="3" cssClass="dato" id="esc" tabindex="15"/>
						
						</td>
					</tr>
					<tr align="left">
						<td class="literal">Provincia</td>
						<td class="literal">
							<form:input path="localidad.id.codprovincia" size="2" maxlength="2" onchange="javascript:lupas.limpiarCampos('desc_provincia','localidad','desc_localidad','sublocalidad');" cssClass="dato" id="provincia" tabindex="16"/>
							<form:input path="localidad.provincia.nomprovincia" cssClass="dato"	id="desc_provincia" size="20" readonly="true"/>
							<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Provincia','principio', '', '');" alt="Buscar Provincia" title="Buscar Provincia" />
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_provincia"> *</label>
						</td>
						<td class="literal">Localidad</td>
						<td class="literal">
							<form:input path="localidad.id.codlocalidad" size="3" maxlength="3" cssClass="dato" id="localidad" tabindex="17" onchange="javascript:lupas.limpiarCampos('desc_localidad','sublocalidad');"/>
							<form:input path="localidad.nomlocalidad" cssClass="dato"	id="desc_localidad" size="30" readonly="true"/>
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_localidad"> *</label>
						</td>
						<td class="literal" >Sublocalidad</td>
						<td class="literal">
							<form:input path="localidad.id.sublocalidad" size="4" maxlength="4" cssClass="dato" id="sublocalidad" tabindex="18"/>
							<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Localidad','principio', '', '');" alt="Buscar Localidad" title="Buscar Localidad" />
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_sublocalidad"> *</label>
						</td>
				
						<td class="literal">Cod. postal</td>
						<td class="literal">
							<form:input path="codpostalstr" size="5" maxlength="5" cssClass="dato" id="cp" tabindex="19" onchange="while(this.value.length<5){this.value='0'+this.value;}"/>
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_cp"> *</label>
						</td>
					</tr>
				</table>
			</div>
			<div class="panel2 isrt">
				<table width="95%">
					<tr align="left">
						<td class="literal">Tel&eacute;fono</td>
						<td class="literal">
							<form:input path="telefono" size="10" maxlength="9" cssClass="dato" id="telefono" tabindex="20"/>
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_telefono"> *</label>
						</td>
						<td class="literal">M&oacute;vil</td>
						<td class="literal">
							<form:input path="movil" size="10" maxlength="9" cssClass="dato" id="movil" tabindex="21"/>
						</td>
						<td class="literal">e-mail</td>
						<td colspan="3" class="literal">
							<form:input path="email" size="48" maxlength="50" cssClass="dato" id="mail" tabindex="22"/>
							<label class="campoObligatorio" title="" id="campoObligatorio_mail"> *</label>
						</td>
					</tr>
					<tr align="left">
						<td class="literal">N&deg; S.S.</td>
						<td class="literal">
							<form:input path="numsegsocial" size="14" maxlength="12" cssClass="dato" id="numsegsocial" tabindex="23"/>
							<input type="hidden" name="numsegsocialOLD" id="numsegsocialOLD" />
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_numsegsocial"> *</label>
						</td>
						<td class="literal">Ind. r&eacute;gimen</td>
						<td class="literal">
							<form:select path="regimensegsocial" cssClass="dato" id="regimensegsocial" cssStyle="width:120" tabindex="24">
								<form:option value="">Todos</form:option>
								<c:forEach var="i" begin="0" end="${fn:length(listaCodRegimen) - 1 }">
									<form:option value="${listaCodRegimen[i] }">${listaDesRegimen[i] }</form:option>
								</c:forEach>
							</form:select>
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_regimensegsocial"> *</label>
						</td>
						<td class="literal">ATP</td>
						<td class="literal">
							<form:select path="atp" cssClass="dato" id="atp" cssStyle="width:70" tabindex="25">
								<form:option value="">Todos</form:option>
								<form:option value="S">SI</form:option>
								<form:option value="N">NO</form:option>
							</form:select>
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_atp"> *</label>
							<input type="hidden" name="atpOLD" id="atpOLD" value="${atpOLD}"/>
						</td>
						<td class="literal" align="right">J. Agricultor/a</td>
						<td class="literal">
							<form:select path="jovenagricultor" cssClass="dato" id="jovenagricultor" cssStyle="width:70" tabindex="26">
								<form:option value="">Todos</form:option>
								<form:option value="S">SI</form:option>
								<form:option value="N">NO</form:option>
							</form:select>
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_jovenagricultor"> *</label>
							<input type="hidden" name="jovenagricultorOLD" id="jovenagricultorOLD" value="${jovenagricultorOLD}"/>
						</td>
					</tr>
				</table>
			</div>
			</form:form>  	
			<div id="buttons2">
				<table width="97%" cellspacing="0" cellpadding="0" border="0">
					<tr>
						<td align="right">
							<c:if test="${comeFrom eq 1}">
								<a class="bot" id="btnSocios" style="display:none" href="javascript:document.getElementById('socioForm').submit()" onClick="javascript:cargaDatos('socioForm')">Socios</a> 
								<a class="bot" id="btnDatosAdic" href="javascript:document.getElementById('datoAdicionalForm').submit()" onClick="javascript:cargaDatos('datoAdicionalForm')">Datos Adic.</a>
							</c:if>
							<c:if test="${comeFrom eq 2}">
								<a class="bot" id="btnSocios" href="javascript:document.getElementById('socioForm').submit()" onClick="javascript:cargaDatos('socioForm')">Socios</a> 
								<a class="bot" id="btnDatosAdic" href="javascript:document.getElementById('datoAdicionalForm').submit()" onClick="javascript:cargaDatos('datoAdicionalForm')">Datos Adic.</a>
							</c:if>
							<c:if test="${(comeFrom ne 1) and (comeFrom ne 2)}">
								<a class="bot" id="btnSocios"  style="display:none" href="javascript:document.getElementById('socioForm').submit()" onClick="javascript:cargaDatos('socioForm')">Socios</a> 
								<a class="bot" id="btnDatosAdic"   style="display:none" href="javascript:document.getElementById('datoAdicionalForm').submit()" onClick="javascript:cargaDatos('datoAdicionalForm')">Datos Adic.</a>
							</c:if>
						</td>
					</tr>
				</table>	
			</div>
		<!-- Aqui tiene que ir el grid de datos -->
		<div id="grid">
			<display:table requestURI="" id="listaAsegurados" class="LISTA" summary="asegurado" 
			               pagesize="${numReg}" size="${totalListSize}" name="${listaAsegurados}" 
			               style="width:95%; margin:0 auto;"  partialList="true" excludedParams="method"
			               decorator="com.rsi.agp.core.decorators.ModelTableDecoratorAsegurados">
			
				<display:setProperty name="pagination.sort.param" value="sort"/>
				<display:setProperty name="pagination.sortdirection.param" value="dir"/>
				
				<c:if test="${cargaAseg eq ''}">			
					<c:if test="${perfil == 0}">
						<display:column class="literal" headerClass="cblistaImg" title="Acciones" property="aseguradoSelecPerfil0" sortable="false" style="width:80px;text-align:center" media="html"/>
					</c:if>
					<c:if test="${perfil != 0}">
						<display:column class="literal" headerClass="cblistaImg" title="Acciones" property="aseguradoSelec" sortable="false" style="width:80px;text-align:center" media="html"/>
					</c:if>
			   </c:if>
			   <c:if test="${cargaAseg eq 'cargaAseg'}">			
					<display:column class="literal" headerClass="cblistaImg" title="Acciones" property="cargaAseguradoSelec" sortable="false" style="width:90px;text-align:center"/>
			   </c:if>
				<display:column class="literal" headerClass="cblistaImg" title="Entidad" property="entidad.codentidad"  sortable="true" style="width:65px;text-align:center"/>
				<display:column class="literal" headerClass="cblistaImg" title="E-S Med" property="entSubEnt" sortProperty="esMed.id.codentidad" sortable="true" style="width:80px;text-align:center"/>
				<display:column class="literal" headerClass="cblistaImg" title="NIF/CIF/NIE" property="nifcif" sortable="true" />		
				<display:column class="literal" headerClass="cblistaImg" title="Nombre/Raz&oacute;n Social" property="nombreCompleto" sortProperty="nombre" sortable="true"/>         		   
				<display:column class="literal" headerClass="cblistaImg" title="Prov." property="localidad.id.codprovincia" sortable="true"/>		
				<display:column class="literal" headerClass="cblistaImg" title="Localidad" property="localidad.id.codlocalidad" sortable="true" />				            
				<display:column class="literal" headerClass="cblistaImg" title="Cod. postal" property="codpostalstr" sortable="true" sortProperty="codpostal"/>				            
				<display:column class="literal" headerClass="cblistaImg" title="Tel&eacute;fono" property="telefono" sortable="false"/>
				<display:column class="literal" headerClass="cblistaImg" title="Fecha Revisi&oacute;n" property="fechaRevision" sortable="true" format="{0,date,dd/MM/yyyy}"  style="text-align:center"/>
			
				<display:footer>
					<c:if test="${cargaAseg eq ''}">	
					   <form:form name="frmcheck" id="frmcheck">
					   		<tr style="background-color:#e5e5e5">
		        			<td class="literal"style="text-align:center">
		        			 	<input type="checkbox" id="selTodos" name="selTodos" class="dato" onclick="this.checked  ? marcar_todos() : desmarcar_todos() "/>
		        			</td>
		        			<td class="literal" colspan="9"><div style="float:left">Marcar Todos</div></td>	        			
		        		</tr>
		        		</form:form>
		        	</c:if>
			    </display:footer>
			</display:table>	
			
				
			 <div class="imprimirDisplayTag" id="divImprimir">
		       	 <a style="font-family: tahoma, verdana, arial;color: #626262;font-size:11px;">Imprimir</a>	
				  <a id="btnImprimirExcel" style="text-decoration:none;" href="javascript:imprimir(${totalListSize}, 'xls')">
				 	<img src="jsp/img/jmesa/excel.gif"/>
				 </a>
			</div>
		</div>
		
		<form name="socioForm" id="socioForm" action="socio.html" >
			<input type="hidden" name="method" id="method" value="doConsulta"/>	
			<input type="hidden" id="idAsegurado" name="idAsegurado" />
		</form>
		<form name="datoAdicionalForm" id="datoAdicionalForm" action="datoAsegurado.html">
			<input type="hidden" name="method" id="method" value="doConsulta"/>	
			<input type="hidden" id="idAsegurado" name="idAsegurado" />
		</form>
		<form name="cargaColectivoForm" id="cargaColectivoForm" action="cargaColectivo.html">
			<input type="hidden" name="operacion" id="operacion" value=""/>	
			<input type="hidden" id="origenLlamada" name="origenLlamada" value="cargaColectivos"/>
		</form>
		
		</div>
		
	
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		
		<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaVia.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaProvincia.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaLocalidad.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaUsuarioEM.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFecha.jsp"%>
		
		<%@ include file="/jsp/moduloAdministracion/asegurados/popupAseguradosSW.jsp"%>
		<%@ include file="/jsp/moduloAdministracion/asegurados/popupPolizasAsegurados.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		
		<!-- POPUP Importar Asegurados  -->
		<form:form name="frmImportarCsv" id="frmImportarCsv" method="post" enctype="multipart/form-data" action="asegurado.html" commandName="aseguradoBean">
			<input type="hidden" name="method" id="methodImportarCsv" value ="doImportarCsv"/>
			
			
			<div id="divImportarCsv" class="wrapper_popup" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;top: 170px">
				<div id="headerPopupImportarCsv" style="padding:0.4em 1em;position:relative; color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px; background:#525583;height:15px">
						<div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px" id="divTituloImportraCsv" name="divTituloImportraCsv">
							Importar asegurados desde fichero CSV
						</div>
						<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px; font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
							<span onclick="cleanPopUpImportarCsv()">x</span>
						</a>
				</div>
				<div class="panelInformacion_content">
						<div style="height:30px">
							<div id="panelAlertasValidacionImportarCsv" class="errorForm_cm" ></div>
						</div>
						<div id="tablaInformacion" class="panelInformacion" style="text-align:center">
							<input type="file" class="dato"  id="file" name="file" size="40" onchange="javascript:limpiaPanelAlertasImportarCsv();"/>
						</div>
						<div style="margin:10px auto;clear: both">
							    <a class="bot" href="javascript:ajaxFileUpload()">Importar</a>
							    <a class="bot" href="javascript:cleanPopUpImportarCsv()">Cancelar</a>					    
						</div>
				</div>
			</div>
		</form:form>
		<!-- FIN POPUP Importar Asegurados -->
		<!-- POP UP Resultado importacion csv -->
		<div id="resultadoImportacionCSV_popup" class="wrapper_popup" style="width: 50%;left: 30%">
			<div class="header-popup">
				<div class="title_popup">Resultado de la importaci&oacute;n de Asegurados</div>
				<a class="close_botton_popup"><span onclick="cerrarPopUpResultadoImportarCsv()">x</span></a>
			</div>
			<div class="content_popup" >
				<div id="resultadoImportacionCSV" class="literal" >	</div>
				<div style="margin:10px auto" id="div_bot" >		
					<a class="bot" href="javascript:cerrarPopUpResultadoImportarCsv();">Cerrar </a>
				</div>
			</div>
		</div>
		
		<!-- FIN POP UP Resultado importacion csv -->
		
		
		
		<!-- INICIO POPUP FECHA DE ESTUDIO-->
		<form name="fecha-estudio-form" id="fecha-estudio-form" action="asegurado.html" method="GET">
			<input type="hidden" id="nif-fecha-estudio" name="nif-fecha-estudio" value=""> 
			<input type="hidden" id="fecha-fecha-estudio.day" name="fecha-fecha-estudio.day" value=""> 
	        <input type="hidden" id="fecha-fecha-estudio.month" name="fecha-fecha-estudio.month" value=""> 
	        <input type="hidden" id="fecha-fecha-estudio.year" name="fecha-fecha-estudio.year" value="">
			<div id="fecha-estudio-popup" class="wrapper_popup" style="left: 30%">
				<div class="header-popup">
					<div class="title_popup">Consultar detalle de asegurado</div>
					<a class="close_botton_popup"><span onclick="cerrarPopUpFechaEstudio();">x</span></a>
				</div>
				<div class="content_popup">
					<label class="literal">Fecha de estudio</label>
					<input type="text" id="fecha-fecha-estudio" 
							onchange="if (!ComprobarFecha(this, document.forms['fecha-estudio-form'], 'Fecha de estudio')) this.value='';" 
							name="fecha-fecha-estudio" value="" class="dato" size="10" maxlength="10" />
					<input type="button" id="btn-fecha-estudio" name="btn-fecha-estudio" class="miniCalendario" style="cursor: pointer;" />
					<div style="margin:10px auto;clear: both">
					     <a class="bot" href="#" onclick="cerrarPopUpFechaEstudio();">Cancelar</a>	
					    <a class="bot" href="#" onclick="abrirPopupSubvenciones();">Aceptar</a>
					</div>
				</div>
			</div>
		</form>
		<!-- FINAL POPUP FECHA DE ESTUDIO-->
		
		<!-- INICIO POPUP SUBVENCIONES ASEGURADO -->
		<div id="subveciones-asegurado-popup" class="wrapper_popup" style="width: 50%;left: 30%">
			<div class="header-popup">
				<div class="title_popup">Control de acceso a subvenciones del asegurado</div>
				<a class="close_botton_popup"><span onclick="cerrarPopupSubvenciones();">x</span></a>
			</div>
			<div class="content_popup" >
				<div id="resultado-subvecionesAsegurado"></div>
				<div style="margin:10px auto" id="div_bot" >
					<a class="bot" href="#" onclick="cerrarPopupSubvenciones();">Cerrar</a>
				</div>
			</div>
		</div>
		<!-- FIN POPUP SUBVENCIONES ASEGURADO -->
		
		<!-- INICIO POPUP SELECCION LINEA -->
		<div id="seleccion-linea-popup" class="wrapper_popup" style="width: 50%;left: 30%">
			<div class="header-popup">
				<div class="title_popup">Selecciï¿½n de l&iacute;nea</div>
				<a class="close_botton_popup"><span onclick="cerrarPopupSeleccionLinea();">x</span></a>
			</div>
			<div class="content_popup" >
				<label class="literal">Plan</label>
					<input type="text" id="plan" name="plan" value="" class="dato" size="4" maxlength="4" onchange="javascript:lupas.limpiarCampos('linea', 'desc_linea');" />
				<label class="literal">L&iacute;nea</label>
				<input type="text" id="linea" name="linea" value="" class="dato" size="3" maxlength="3"  onchange="javascript:lupas.limpiarCampos('desc_linea');" />
				<input type="text" class="dato" id="desc_linea" size="40" readonly="readonly" />
				<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar L&iacute;nea" title="Buscar L&iacute;nea" />
				<label class="campoObligatorio"	id="campoObligatorio_linea" title="Campo obligatorio"> *</label>
				<div style="margin:10px auto">
					<a class="bot" href="#" onclick="javascript:lupas.limpiarCampos('plan', 'linea', 'desc_linea');">Limpiar</a>
					<a class="bot" href="#" onclick="cerrarPopupSeleccionLinea();">Cancelar</a>
					<a class="bot" href="#" onclick="doControlSubvenciones($('#plan').val(), $('#linea').val());">Aplicar</a>
				</div>
			</div>
		</div>
		<!-- FIN POPUP SELECCION LINEA -->
		
		<!-- INICIO POPUP CONTROL SUBVS -->
		<div id="control-subvs-popup" class="wrapper_popup" style="width: 50%;left: 30%">
			<div class="header-popup">
				<div class="title_popup">Control de acceso a subvenciones</div>
				<a class="close_botton_popup"><span onclick="cerrarPopupControlSubvs();">x</span></a>
			</div>
			<div class="content_popup">
				<div id="content_popup_table" style="height:300px;overflow-x:hidden;overflow-y:auto;">
					<table width="90%" align="center" class="tabla-popup" id="control-subvs-table"></table>
				</div>
				<div style="margin:10px auto">
						<label class="literal">Exportar listado</label>
						<a id="btnImprimirExcel" style="text-decoration:none;" href="#" onclick="doImprimirControlSubvenciones();"><img src="jsp/img/jmesa/excel.gif"/></a>
					</div>
				<div style="margin:10px auto">
					<a class="bot" href="#" onclick="cerrarPopupControlSubvs();">Cerrar</a>					
				</div>
			</div>
		</div>
		<!-- FIN POPUP CONTROL SUBVS -->
		
		<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
	</body>
</html>