<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
<head>
	<title>Datos Complementarios</title>
	
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
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
	<script type="text/javascript" src="jsp/js/calendar.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
	<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
	<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>	
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
	<script type="text/javascript" src="jsp/moduloUtilidades/siniestros/datosSiniestro.js" ></script>
	<%@ include file="/jsp/js/draggable.jsp"%>

	
	
	<script language="javascript">						 
		$(document).ready(function() {	
			<c:if test="${modoLectura}">			
				$('#botonLimpiar').hide();					
				$("input[type=text]").each(function(){	
						$(this).attr('readonly',"readonly");
				});	
				$('#tx_observaciones').attr('readonly',"readonly");		
				$('#sl_riesgo_siniestros').attr('disabled',true);
				$('#lupavia').hide();
				$('#lupapr').hide();
				$('#lupaloc').hide();
			
				$("#btn_fecha_ocurrencia").hide();
			</c:if>
		});
	</script>	
	
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4');">
	<%@ include file="../../common/static/cabecera.jsp"%>
	<%@ include file="../../common/static/menuGeneral.jsp"%>
	<%@ include file="../../common/static/datosCabeceraTaller.jsp" %>		
	

<!-- Botones para alta, imprimir y volver -->
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">	
					<a class="bot" href="javascript:volver();">Volver</a>	
										
					<c:if test="${modoLectura != true}">			
						<a class="bot" id="botonLimpiar" href="javascript:limpiar()">Limpiar</a>	
						<a class="bot" id="botonAlta" href="javascript:continuar();">Continuar</a>		
					</c:if>	
					
					<c:if test="${modoLectura == true}">			
						<a class="bot" id="botonAlta" href="javascript:continuarSoloLectura();">Continuar</a>		
					</c:if>	
						
					
				</td>
			</tr>
		</table>
	</div>
	
<!-- Contenido de la página -->
<div class="conten" style="padding: 3px; width: 97%">
	<p class="titulopag" align="left">Datos Complementarios del Siniestro</p>
	
	<form name="listadoParcelas" id="listadoParcelas" action="parcelasSiniestradas.html" method="post">			
			<input type="hidden" name="idPoliza" value="${idPoliza}">
			<input type="hidden" name="idSiniestro" value="${idSiniestro}">
			<input type="hidden" name="modoLectura" value="${modoLectura}">
			<input type="hidden" name="fromUtilidades" value="${fromUtilidades}">
	</form>
	
	<form name="volverListado" id="volverListado" action="utilidadesSiniestros.run" method="post">
			<input type="hidden" name="method" id="methodListado" value="doConsulta"/>
			<input type="hidden" name="volver" id="volver" value="volver"/>
	</form>
	
	<form:form name="main" id="main" action="siniestros.html" method="post" commandName="siniestroBean">
		<input type="hidden" name="tx_fechaocurrencia.day" value="">
		<input type="hidden" name="tx_fechaocurrencia.month" value="">
		<input type="hidden" name="tx_fechaocurrencia.year" value="">
		<input type="hidden" id="method" name="method" />
		<input type="hidden" name="fromUtilidades" id="fromUtilidades" value="${fromUtilidades}"/>
		<input type="hidden" name="soloLectura" id="soloLectura" value="${soloLectura}"/>
		<input type="hidden" name="getParcelasBBDD" id="getParcelasBBDD" value="${getParcelasBBDD}"/>
		
		<input type="hidden" name="idPoliza" value="${idPoliza}">		
		<input type="hidden" name="idSiniestro" value="${idSiniestro}">
		<input type="hidden" name="altaWs" id="altaWs" value="${altaWs}">
		<form:hidden path="id" id="id"/>
		<form:hidden path="poliza.idpoliza" id="idPoliza"/>
		<form:hidden path="estadoSiniestro.idestado" id="estado"/>
		<form:hidden path="usuarioAlta" id="usuarioAlta"/>
		<form:hidden path="numerosiniestro" id="numerosiniestro"/>
		<spring:bind path="fechaAlta">
			<input type="hidden" name="fechaAlta" id="fechaAlta" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${siniestroBean.fechaAlta}" />" />
		</spring:bind>
		<input type="hidden" id="tipoIdent" name="tipoIdent" value="${nifcif }"/>
		
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
		
		
		<div class="panel2 isrt">
			<table width="60%" align="center" cellspacing="5" style="margin:0 auto;">
				<tr>
					<td class="literal">Póliza</td>
					<td>
						<form:input id="referencia" path="poliza.referencia" cssClass="literal"  />
					</td>
					<td class="literal">Nº Siniestro</td>
					<td>
						<form:input id="lb_n_siniestro" path="numsiniestro" cssClass="literal" />
					</td>
				</tr>
				<tr>
					<td class="literal">Riesgo Siniestro</td>					
					<td>
					
						<form:select id="sl_riesgo_siniestros" path="codriesgo" cssClass="dato" cssStyle="width:180px">
							<form:option value="">-- Seleccione una opción --</form:option>
							<c:forEach items="${listaRiesgos}" var="riesgo">
								<fmt:parseNumber var="aux" value="${riesgo.id.codriesgo}"/>
								<form:option value="${aux }">${riesgo.id.codriesgo} - ${riesgo.desriesgo }</form:option>
							</c:forEach>
						</form:select>
					
						<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_sl_riesgo_siniestros"> *</label>
					</td>
					<td class="literal">Fecha Ocurrencia</td>
					<td>						
						<spring:bind path="fechaocurrencia">
							<!-- DAA 12/05/2012  -->
							<input type="text" name="fechaocurrencia"  id="tx_fechaocurrencia" size="11" maxlength="10" class="dato"  
										onblur="if(!ComprobarFecha(this, document.main, 'Fecha Ocurrencia')) this.value='';	(this);" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${siniestroBean.fechaocurrencia}"/>"/>
						</spring:bind>
						<input type="button" id="btn_fecha_ocurrencia" name="btn_fecha_ocurrencia" class="miniCalendario" 
											style="cursor: pointer;"/>
						<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_tx_fechaocurrencia"> *</label>
					</td>
				</tr>
				<tr>
					<td class="literal">Observaciones<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_tx_observaciones"> *</label></td>				
				</tr>
				<tr>
					<td colspan="4">
						<form:textarea id="tx_observaciones" path="observaciones" cols="120" rows="4" cssClass="dato"  onchange="this.value=this.value.toUpperCase();"/>
					</td>
				</tr>
			</table>
		</div>
		<div class="panel2 isrt">
		<fieldset>
			<legend>Persona de Contacto</legend>
			<br/>		
			<table cellpadding="5">
				<tr>
					<td class="literal">Nombre</td>
					<td>
						<form:input id="tx_nombre" path="nombre" cssClass="dato" size="40" maxlength="20" onchange="this.value=this.value.toUpperCase();"/>
						<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_tx_nombre"> *</label>
					</td>
				</tr>
				<tr>
					<td class="literal">1º Apellido</td>
					<td>
						<form:input id="tx_apellido1" path="apellido1" cssClass="dato" size="40" maxlength="40" onchange="this.value=this.value.toUpperCase();"/>
						<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_tx_apellido1"> *</label>
					</td>
					<td class="literal">2º Apellido</td>
					<td>
						<form:input id="tx_apellido2" path="apellido2" cssClass="dato" size="40" maxlength="40" onchange="this.value=this.value.toUpperCase();"/>
					</td>
				</tr>
				<tr>
					<td class="literal">Razón Social</td>
					<td>
						<form:input id="tx_razonsocial" path="razonsocial" cssClass="dato" size="45" maxlength="20" onchange="this.value=this.value.toUpperCase();"/>
						<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_tx_razonsocial"> *</label>
					</td>
					<td colspan="2">&nbsp;</td>
				</tr>
			</table>
			<hr/>
			<table cellpadding="5">
				<tr>
					<td class="literal" width="80px">Vía</td>
					<td>
						<form:input id="via" path="clavevia" cssClass="dato" size="4" maxlength="4" onchange="this.value=this.value.toUpperCase();lupas.limpiarCampos('desc_via');"/>
						<input id="desc_via"type="text" class="dato" value="<c:out value="${desc_via}"/>" readonly="true"/>						
						<img id="lupavia" src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Via','principio', '', '');" alt="Buscar Via"/>
						<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_via"> *</label>
					</td>
					<td class="literal" width="80px">Domicilio</td>
					<td colspan="3">
						<form:input id="tx_direccion" path="direccion" cssClass="dato" size="25" maxlength="30" onchange="this.value=this.value.toUpperCase();"/>
						<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_tx_direccion"> *</label>
					</td>
				</tr>
				<tr>
					<td class="literal" width="40px">Nº</td>
					<td>
						<form:input id="tx_numero" path="numvia" cssClass="dato" size="5" maxlength="5" />
						<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_tx_numero"> *</label>
					</td>
					<td class="literal" width="40px">Piso</td>
					<td>
						<form:input id="tx_piso" path="piso" cssClass="dato" size="5" maxlength="5" />
					</td>
					<td class="literal" width="40px">Bloque</td>
					<td>
						<form:input id="tx_bloque" path="bloque" cssClass="dato" size="3" maxlength="3" />
					</td>
					<td class="literal" width="40px">Esc</td>
					<td>
						<form:input id="tx_escalera" path="escalera" cssClass="dato" size="3" maxlength="3" />
					</td>
				</tr>
				<tr>
					<td class="literal" width="80px">Provincia</td>
					<td>
						<form:input id="provincia" path="codprovincia" cssClass="dato" size="3" maxlength="3" onchange="javascript:lupas.limpiarCampos('desc_provincia','localidad','desc_localidad','sublocalidad');"/>
						<input id="desc_provincia" type="text" class="dato" size="20" value="<c:out value="${desc_provincia}"/>" readonly="true" />		
						<img id="lupapr" src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Provincia','principio', '', '');" alt="Buscar Provincia" />
						<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_provincia"> *</label>
					</td>									
					<td class="literal" width="80px">Localidad</td>
					<td >
						<form:input id="localidad" path="codlocalidad" cssClass="dato" size="3" maxlength="3" onchange="javascript:lupas.limpiarCampos('desc_localidad','sublocalidad');"/>
						<input id="desc_localidad" type="text" class="dato" size="30" value="<c:out value="${desc_localidad}"/>" readonly="true"/>		
						<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_localidad"> *</label>
					</td>		
					<td class="literal" width="80px">Sublocalidad</td>								
					<td>
						<form:input id="sublocalidad" path="sublocalidad" cssClass="dato" size="4" maxlength="4" />
						<img id="lupaloc" src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Localidad','principio', '', '');" alt="Buscar Localidad" />
					</td>
					<td class="literal" width="100px">Cod. Postal</td>
					<td>
						<form:input id="cp" path="codpostalstr" cssClass="dato" size="5" maxlength="5" onblur="validarElemento(this)"/>
						<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_cp"> *</label>
					</td>
				</tr>
			</table>
			<hr/>
			<table cellpadding="5">
				<tr>
					<td class="literal" width="80px">Teléfono 1</td>
					<td>
						<form:input id="tx_telefono1" path="telefono1" cssClass="dato" size="9" maxlength="9" />
						<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_tx_telefono1"> *</label>
					</td>
					<td class="literal" width="90px"><div id="div_telefono2" style="margin-left:15px; margin-right:15px;">Teléfono 2</div></td>
					<td>
						<form:input id="tx_telefono2" path="telefono2" cssClass="dato" size="9" maxlength="9" />
						<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_tx_telefono2"> *</label>
					</td>
					<td class="literal" width="90px"><div id="div_telefono3" style="margin-left:15px; margin-right:15px;">Teléfono 3</div></td>
					<td>
						<form:input id="tx_telefono3" path="telefono3" cssClass="dato" size="9" maxlength="9" />
						<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_tx_telefono3"> *</label>
					</td>
				</tr>
			</table>
			<hr/>
			<table cellpadding="5">
				<tr>
					<td class="literal" width="160px">Teléfono Fijo Asegurado</td>
					<td>
						<form:input id="tx_telefono_fijo_asegurado" path="telefonoFijoAsegurado" cssClass="dato" size="9" maxlength="9" />
						<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_tx_telefono_fijo_asegurado"> *</label>
					</td>
					<td class="literal" width="190px"><div id="div_movil_asegurado" style="margin-left:15px">Teléfono Móvil Asegurado</div></td>
					<td>
						<form:input id="tx_telefono_movil_asegurado" path="telefonoMovilAsegurado" cssClass="dato" size="9" maxlength="9" />
						<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_tx_telefono_movil_asegurado"> *</label>
					</td>
					<td class="literal" width="130px"><div id="div_email_asegurado" style="margin-left:15px">e-mail Asegurado</div></td>
					<td>
						
						<form:input path="emailAsegurado" size="50" maxlength="50" cssClass="dato" id="emailAsegurado" onchange="this.value=this.value.toUpperCase();"/>
						
						<!-- 
						<form:input id="tx_email_asegurado" path="emailAsegurado" cssClass="dato" size="50" maxlength="50"/>
						<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_tx_email_asegurado"> *</label>
						-->
					</td>
				</tr>
			</table>
		</fieldset>
		</div>
	</form:form>
</div>	
<%@ include file="/jsp/common/static/piePagina.jsp"%>
<%@ include file="/jsp/common/static/overlay.jsp"%>
<%@ include file="/jsp/common/lupas/lupaVia.jsp"%>
<%@ include file="/jsp/common/lupas/lupaProvincia.jsp"%>
<%@ include file="/jsp/common/lupas/lupaLocalidad.jsp"%>

</body>
</html>