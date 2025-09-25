<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
<title>Detalle de Clases</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
    	<!--  <link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />-->
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/terminos.js"></script>	
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGeneriaODGanado.js"></script>
		<!-- <script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar-sp.js"></script> 
	    <script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>  --> 
	    <script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/imprimir.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/moduloTaller/claseMto/claseDetalleGanado.js" ></script>
		<script type="text/javascript" src="jsp/moduloTaller/claseMto/popupCambioMasivoClaseDetalleGanado.js" ></script>
		                                    
		<%@ include file="/jsp/js/draggable.jsp"%>
		<script type="text/javascript">
		function cargarFiltro(){
			 <c:forEach items="${sessionScope.consultaClaseDetalleGanado_LIMIT.filterSet.filters}" var="filtro">
			
				//Id
				 <c:if test="${filtro.property == 'clase.id'}">
						$('#detalleid').val('${filtro.value}');
				</c:if>
				
				//lineaSeguroId
				<c:if test="${filtro.property == 'lineaseguroid'}">
					$('#lineaseguroid').val('${filtro.value}');
				</c:if>
			
				//Modulo
				<c:if test="${filtro.property == 'codmodulo'}">
					$('#modulo').val('${filtro.value}');
				</c:if>
				
				<c:if test="${filtro.property == 'codprovincia'}">
					$('#provincia').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'codcomarca'}">
					$('#comarca').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'codtermino'}">
					$('#termino').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'subtermino'}">
					$('#subtermino').val('${filtro.value}');
				</c:if>

				//CodEspecie
				<c:if test="${filtro.property == 'codespecie'}">
					$('#especie').val('${filtro.value}');
				</c:if>
				//CodRegimenManejo
				<c:if test="${filtro.property == 'codregimen'}">
					$('#regimen').val('${filtro.value}');
				</c:if>
				//CodGrupoRaza
				<c:if test="${filtro.property == 'codgruporaza'}">
					$('#codgrupoRaza').val('${filtro.value}');
				</c:if>
				//codTipoAnimal
				<c:if test="${filtro.property == 'codtipoanimal'}">
					$('#codtipoanimal').val('${filtro.value}');
				</c:if>
				//codTipoCapital
				<c:if test="${filtro.property == 'codtipocapital'}">
					$('#codtipocapital').val('${filtro.value}');
				</c:if>
							
			</c:forEach>
		}

		function cargaMenu(){
			<c:if test="${vieneDeCargaClases eq true}">
				SwitchMenu('sub3');
			</c:if>
		}


		</script>
	</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="cargarFiltro();cargaMenu();">		
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<c:choose>
			<c:when test="${vieneDeCargaClases eq true}">
			    <%@ include file="/jsp/common/static/menuGeneral.jsp"%>
				<%@ include file="/jsp/common/static/datosCabecera.jsp"%>
			</c:when>
			<c:otherwise>
			    <%@ include file="/jsp/common/static/menuLateralTaller.jsp"%>
				<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
			</c:otherwise>
		</c:choose>
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tbody>
					<tr>
						<td align="right">		
							<c:choose>
								<c:when test="${vieneDeCargaClases eq true}">
									<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
									<a class="bot" href="javascript:limpiar();">Limpiar</a>
									<a class="bot" id="btnVolver" href="cargaClase.run?origenLlamada=cicloPoliza">Volver</a>
								</c:when>
								<c:otherwise>	
									<a class="bot" id="btnImportar" href="javascript:importar()">Importar</a>
									<a class="bot" id="btnModificar" style="display:none" href="javascript:modificar();">Modificar</a>	
									<a class="bot" id="btnAlta" href="javascript:alta();">Alta</a>		
									<a class="bot" id="btnCambioMasivo" href="javascript:cambioMasivo();" title="Cambio masivo">Cambio masivo</a>		
									<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
									<a class="bot" href="javascript:limpiar();">Limpiar</a>
									<a class="bot" href="claseMto.run">Volver</a>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding: 3px; width: 100%">
			<p class="titulopag" align="left">Mantenimiento de Detalle de Clases</p>
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			
			<form:form name="main3" id="main3" action="claseDetalleGanado.run" method="post" commandName="claseDetalleGanadoBean">
				<form:hidden path="id" id="id" />
				<form:hidden path="clase.id" id="detalleid" />
				<form:hidden path="clase.linea.lineaseguroid" id="lineaseguroid"/>
				<input type="hidden" id="codlinea" name="codlinea" value="${codlinea}" />
				<input type="hidden" name="fechaInicioContratacion" id="fechaInicioContratacion" value="${fechaInicioContratacion}"/>
				
				<input type="hidden" name="method" id="method"/>
				<input type="hidden" name="origenLlamada" id="origenLlamada" value="${origenLlamada}"/>
				<input type="hidden" name="vieneDeCargaClases" id="vieneDeCargaClases" value="${vieneDeCargaClases}"/>
				<input type="hidden" name="listaIdsMarcados" id="listaIdsMarcados" value=""/>
				<input type="hidden" name="marcaTodos" id="marcaTodos" value="false"/>
				<input type="hidden" name="listaIdsTodos" id="listaIdsTodos" value="${listaIdsTodos}"/>
				<input type="hidden" name="vieneDeConsultar" id="vieneDeConsultar" value=""/>
				<!-- JANV 13/04/2016   Mantenimiento de clases para ganado. También en lupagenerica.js -->
				<!--<input type="hidden" name="grupoNegocio" id="grupoNegocio" value="2"/>-->					
				
				<div class="panel2 isrt" style="width:100%;">
					<fieldset style="width:100%;">
						<table align="center">
							<tr align="left">
								<td class="literal">Plan</td>
								<td colspan="8" class="literal">
									<form:input path="clase.linea.codplan" id="detalleplan" cssClass="dato" size="3" readonly="true"/>
								</td>
								<td class="literal">Linea</td>
								<td class="literal">
									<form:input path="clase.linea.codlinea" id="detallelinea" cssClass="dato" size="4" readonly="true"/>
								</td>
								<td class="literal">Clase</td>
								<td colspan="8" class="literal">
									<form:input path="clase.clase" id="detalleclase" cssClass="dato" size="3" readonly="true"/>
								</td>
								<td class="literal">
									<form:input path="clase.descripcion" id="detalledesc" cssClass="dato" size="100" readonly="true"/>
								</td> 
								
							</tr>
						</table>
					</fieldset>
				</div>
				<div class="panel2 isrt" style="width:100%;">
					<fieldset style="width:100%;"><legend class="literal">Filtro</legend>
						<table  align="center" >
							<tr>
								<td class="literal">Módulo</td>
								<td>
									<form:input path="codmodulo" size="5" maxlength="5" cssClass="dato" id="modulo" tabindex="1" onchange="this.value=this.value.toUpperCase();"/>
									<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_modulo"> *</label>
								</td>
								
								<td class="literal">Especie</td>
								<td>
									<form:input path="codespecie" id="especie" size="3" maxlength="3" cssClass="dato" onchange="javascript:lupas.limpiarCampos('desc_especie');" tabindex="2"/>
									<form:input path="descespecie" cssClass="dato" id="desc_especie" size="16" readonly="true"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;"onclick="javascript:lupas.muestraTabla('Especie','principio', '', '');"alt="Buscar Especie" title="Buscar Especie" />
									<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_especie"> *</label>								
								</td>
								<td class="literal">Régimen</td>
								<td colspan="3">
									<form:input path="codregimen" id="regimen" size="3" maxlength="3" cssClass="dato" onchange="javascript:lupas.limpiarCampos('desc_regimen');" tabindex="3"/>
									<form:input path="descregimen" cssClass="dato" id="desc_regimen" size="16" readonly="true"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Regimen','principio', '', '');" alt="Buscar Régimen" title="Buscar Régimen" />
									<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_regimen"> *</label>	
								</td>
							</tr>
							<tr>
								<td class="literal">Grupo de raza</td>
								<td>
									<form:input path="codgruporaza" size="3" maxlength="3" cssClass="dato" id="codgrupoRaza"  onchange="javascript:lupas.limpiarCampos('desGrupoRaza');"  tabindex="4"/>
									<form:input path="descgruporaza" cssClass="dato" id="desGrupoRaza" size="25" readonly="true"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('GrupoRaza','principio', '', '');" alt="Buscar Grupo de Raza" title="Buscar Grupo de Raza" />
									<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_codgrupoRaza"> *</label>										
								</td>
								
								<td class="literal">Tipo de Animal</td>
								<td>
									<form:input path="codtipoanimal" size="3" maxlength="3" cssClass="dato" id="codtipoanimal" onchange="javascript:lupas.limpiarCampos('desTipoAnimal');" tabindex="5"/>
									<form:input path="desctipoanimal" cssClass="dato" id="desTipoAnimal" size="25" readonly="true"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('TiposAnimalGanado','principio', '', '');" alt="Buscar Tipo de Animal" title="Buscar Tipo de Animal" />
									<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_codtipoanimal"> *</label>																			
								</td>
								<td class="literal">Tipo de Capital</td>
								<td colspan="3">								
									<form:input path="codtipocapital" size="3" maxlength="3" cssClass="dato" id="codtipocapital" onchange="javascript:lupas.limpiarCampos('desTipoCapital');" tabindex="6"/>
									<form:input path="desctipocapital" cssClass="dato" id="desTipoCapital" size="25" readonly="true"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('TipoCapitalGrupoNegocio','principio', 'codtipocapital', 'ASC');" alt="Buscar Tipo de Capital"  title="Buscar Tipo de Capital" />
									<label class="campoObligatorio" id="campoObligatorio_codtipocapital" title="Campo obligatorio">*</label>
								</td>												
							</tr>
							<tr>
								<td class="literal">Provincia</td>
								<td>
									<form:input path="codprovincia" size="3" maxlength="2" cssClass="dato" id="provincia"  onchange="javascript:lupas.limpiarCampos('desc_provincia','comarca','desc_comarca','termino','desc_termino','subtermino');" tabindex="7" /> 
									<form:input path="descprovincia" cssClass="dato"	id="desc_provincia" size="25" readonly="true"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Provincia','principio', '', '');" alt="Buscar Provincia" title="Buscar Provincia" />
									<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_provincia"> *</label>
								</td>
								
								<td class="literal">Comarca</td>
								<td>
									<form:input path="codcomarca" id="comarca" size="2" maxlength="2" cssClass="dato" onchange="javascript:lupas.limpiarCampos('desc_comarca','termino','desc_termino','subtermino');" tabindex="8"/>
									<form:input path="desccomarca" cssClass="dato" id="desc_comarca" size="16" readonly="true"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Comarca','principio', '', '');" alt="Buscar Comarca" title="Buscar Comarca" />
									<label class="campoObligatorio" id="campoObligatorio_comarca" title="Campo obligatorio">*</label>
								</td>
								<td class="literal">Término</td>
								<td>
									<form:input path="codtermino" id="termino" size="3" maxlength="3" cssClass="dato" onchange="javascript:lupas.limpiarCampos('desc_termino','subtermino');" tabindex="9"/>
									<form:input path="desctermino" cssClass="dato" id="desc_termino" size="26" readonly="true"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Termino','principio', '', '');" alt="Buscar Término" title="Buscar Término" />
									<label class="campoObligatorio" id="campoObligatorio_termino" title="Campo obligatorio">*</label>
								</td>
								<td class="literal">Subt.</td>
								<td>
									<form:input path="subtermino" id="subtermino" size="1" maxlength="1" cssClass="dato" onchange="this.value=this.value.toUpperCase();" tabindex="10"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Termino','principio', '', '');" alt="Buscar Término" title="Buscar Término" />
									<label class="campoObligatorio" id="campoObligatorio_subtermino" title="Campo obligatorio">*</label>
								</td>
							</tr>
						</table>
					</fieldset>
				</div>			
			</form:form>
			<!-- Grid Jmesa -->
			<div id="grid" style="width: 90%" align="center" >
				 ${consultaClaseDetalleGanado}
			</div>
		</div>
		
			
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		<%@ include file="/jsp/moduloTaller/claseMto/popupCambioMasivoClaseDetalleGanado.jsp"%>
		
		<%@ include file="/jsp/moduloTaller/claseMto/popupImportarClaseDetalleGanado.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaGrupoRaza.jsp"%>	 
		<%@ include file="/jsp/common/lupas/lupaTipoCapitalGrupoExplotacion.jsp"%>	 
		<%@ include file="/jsp/common/lupas/lupaTipoAnimalGanado.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaEspecie.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaRegimen.jsp"%>		
		<%@ include file="/jsp/common/lupas/lupaProvincia.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaComarca.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaTermino.jsp"%>		
		<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>	
		<%@ include file="/jsp/common/lupas/lupaProvinciaCM.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaComarcaCM.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaTerminoCM.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaGrupoRazaCM.jsp"%>	 
		<%@ include file="/jsp/common/lupas/lupaTipoCapitalGrupoExplotacionCM.jsp"%>	 
		<%@ include file="/jsp/common/lupas/lupaTipoAnimalGanadoCM.jsp"%>						  
		<%@ include file="/jsp/common/lupas/lupaEspecieCM.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaRegimenCM.jsp"%>	 
			
		
</body>
</html>