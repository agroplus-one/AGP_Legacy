<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Detalle de Clases</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
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
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/imprimir.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/moduloTaller/claseMto/claseDetalle.js" ></script>
		<script type="text/javascript" src="jsp/moduloTaller/claseMto/popupCambioMasivoClaseDetalle.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
		
		<script type="text/javascript">
			 
			function cargarFiltro(){
					
				<c:forEach items="${sessionScope.consultaClaseDetalle_LIMIT.filterSet.filters}" var="filtro">
					
					<c:if test="${filtro.property == 'codmodulo'}">
						$('#modulo').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'cicloCultivo.codciclocultivo'}">
						$('#cicloCultivo').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'sistemaCultivo.codsistemacultivo'}">
						$('#sistemaCultivo').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'cultivo.id.codcultivo'}">
						$('#cultivo').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'variedad.id.codvariedad'}">
						$('#variedad').val('${filtro.value}');
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
					<c:if test="${filtro.property == 'tipoCapital.codtipocapital'}">
						$('#capital').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'tipoPlantacion.codtipoplantacion'}">
						$('#tplantacion').val('${filtro.value}');
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
		<div class="conten" style="padding: 3px; width: 101%">
		<p class="titulopag" align="left">Mantenimiento de Detalle de Clases</p>
			
			<form:form name="main3" id="main3" action="claseDetalle.run" method="post" commandName="claseDetalleBean">
				<form:hidden path="id" id="id" />
				<form:hidden path="clase.id" id="detalleid"/>
				<form:hidden path="clase.linea.lineaseguroid" id="detallelineaseguroid"/>
				<input type="hidden" id="codlinea" name="codlinea" value="${codlinea}" />
				<input type="hidden" name="fechaInicioContratacion" id="fechaInicioContratacion" value="${fechaInicioContratacion}"/>
				
				<input type="hidden" name="method" id="method"/>
				<input type="hidden" name="origenLlamada" id="origenLlamada" value="${origenLlamada}"/>
				<input type="hidden" name="listaIdsMarcados" id="listaIdsMarcados" value=""/>
				<input type="hidden" name="marcaTodos" id="marcaTodos" value="false"/>
				<input type="hidden" name="listaIdsTodos" id="listaIdsTodos" value="${listaIdsTodos}"/>
				<input type="hidden" name="vieneDeConsultar" id="vieneDeConsultar" value=""/>
							
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div class="panel2 isrt">
				<fieldset>
				<table>
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
				  
				<div class="panel2 isrt">
				<fieldset><legend class="literal">Filtro</legend>
				<table>
					<tr align="left">
						<td class="literal">Módulo</td>
						<td colspan="1" class="literal">
							<form:input path="codmodulo" size="3" maxlength="5" cssClass="dato" id="modulo" tabindex="1" onchange="this.value=this.value.toUpperCase();"/>
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_modulo"> *</label>
						</td>
						<td class="literal">Ciclo Cult.</td>
						<td colspan="1" class="literal">
							<form:input path="cicloCultivo.codciclocultivo" size="2" onchange="javascript:lupas.limpiarCampos('desciclocultivo');" maxlength="3" cssClass="dato" id="cicloCultivo" tabindex="1"/>
							<form:input path="cicloCultivo.desciclocultivo" size="20" maxlength="30" cssClass="dato" id="desciclocultivo" readonly="true"/>
							<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('CicloCultivo','principio', '', '');"	alt="Buscar Ciclo Cultivo" title="Buscar Ciclo Cultivo" />
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_ciclocultivo"> *</label>
						</td>
						<td class="literal">Sist. Cultivo</td>
						<td colspan="1" class="literal">
							<form:input path="sistemaCultivo.codsistemacultivo" size="2" onchange="javascript:lupas.limpiarCampos('dessistemaCultivo');" maxlength="3" cssClass="dato" id="sistemaCultivo" tabindex="1"/>
							<form:input path="sistemaCultivo.dessistemacultivo" size="20" maxlength="30" cssClass="dato" id="dessistemaCultivo" readonly="true"/>
							<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('SistemaCultivo','principio', '', '');"	alt="Buscar Sistema Cultivo" title="Buscar Sistema Cultivo" />
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_dessistemacultivo"> *</label>
						</td>
				    </tr>
				    <tr align="left">
						<td class="literal">Cultivo</td>
						<td colspan="1" class="literal">
							<form:input  id="cultivo" path="cultivo.id.codcultivo" cssClass="dato" size="2" maxlength="3" onchange="javascript:lupas.limpiarCampos('desc_cultivo','variedad','desc_variedad');" tabindex="1"/>
							<form:input cssClass="dato" path="cultivo.descultivo" id="desc_cultivo" size="20" readonly="true"/>
							<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Cultivo','principio', '', '');"	alt="Buscar Cultivo" title="Buscar Cultivo" />
						    <label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_cultivo"> *</label>
						</td>
						<td class="literal">Variedad</td>
						<td colspan="1" class="literal">
							<form:input  id="variedad" path="variedad.id.codvariedad" cssClass="dato" size="2" maxlength="3" onchange="javascript:lupas.limpiarCampos('desc_variedad');" tabindex="1"/>
							<form:input cssClass="dato" path="variedad.desvariedad" id="desc_variedad" size="20" readonly="true"/>
							<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Variedad','principio', '', '');"	alt="Buscar Variedad" title="Buscar Variedad" />		
						    <label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_variedad"> *</label>
						</td>
						<td class="literal">Tipo capital</td>
						<td colspan="1" class="literal">
							<form:input path="tipoCapital.codtipocapital" id="capital" size="2" maxlength="3" cssClass="dato" onchange="javascript:lupas.limpiarCampos('desc_capital');" tabindex="1"/>
								<form:input cssClass="dato"	path="tipoCapital.destipocapital"id="desc_capital" size="20" readonly="true" />
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('TipoCapital','principio', '', '');"	alt="Buscar Tipo Capital" title="Buscar Tipo Capital" />	
						</td>
					 </tr>
					 <tr align="left">
							<td class="literal">Provincia</td>
							<td class="literal">
								<form:input path="codprovincia" size="2" maxlength="2" cssClass="dato" id="provincia"  onchange="javascript:lupas.limpiarCampos('desc_provincia','comarca','desc_comarca','termino','desc_termino','subtermino');" tabindex="1" /> 
								<form:input path="" cssClass="dato"	id="desc_provincia" size="20" readonly="true"/>
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Provincia','principio', '', '');" alt="Buscar Provincia" title="Buscar Provincia" />
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_provincia"> *</label>
							</td>
							<td class="literal">Comarca</td>
							<td class="literal">
								<form:input path="codcomarca" size="2" maxlength="2" cssClass="dato" id="comarca"  onchange="javascript:lupas.limpiarCampos('desc_comarca','termino','desc_termino','subtermino');" tabindex="1" /> 
								<form:input path="" cssClass="dato"	id="desc_comarca" size="20" readonly="true"/>
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Comarca','principio', '', '');" alt="Buscar Comarca" title="Buscar Comarca" />
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_comarca"> *</label>
							</td>
							<td class="literal"  align="right">Termino</td>
							<td class="literal">
								<form:input path="codtermino" size="2" maxlength="3" cssClass="dato" id="termino"  onchange="javascript:lupas.limpiarCampos('desc_termino','subtermino');" tabindex="1" /> 
								<form:input path="" cssClass="dato"	id="desc_termino" size="20" readonly="true"/>
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_termino"> *</label>
							</td>
							<td class="literal">Subtermino</td>
							<td class="literal">
								<form:input path="subtermino" size="1" maxlength="1" cssClass="dato" id="subtermino" tabindex="1" /> 
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Termino','principio', '', '');" alt="Buscar Termino" title="Buscar Termino" />
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_subtermino"> *</label>
							</td>
					</tr>
					<tr align="left">
						<td class="literal">Tipo Plantación</td>
						<td colspan="1" class="literal">
							<form:input path="tipoPlantacion.codtipoplantacion" id="tplantacion" size="2" maxlength="3" cssClass="dato" onchange="javascript:lupas.limpiarCampos('desc_tplantacion');" tabindex="1"/>
							<form:input cssClass="dato"	path="tipoPlantacion.destipoplantacion" id="desc_tplantacion" size="20" readonly="true" />
							<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('TipoPlantacion','principio', '', '');"	alt="Buscar Tipo Plantación" title="Buscar Tipo Plantación" />	
						</td>
				 	</tr>
				</table>
				</fieldset>
				</div>
			
			</form:form>
			
			
			<!-- Grid Jmesa -->
			<div id="grid" style="width: 85%;margin:0 auto;">
				 ${consultaClaseDetalle}
			</div>
	</div>
		
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		<%@ include file="/jsp/common/static/overlayCambioMasivo.jsp"%>
		
		<!-- ************* -->
		<!-- POPUP  AVISO  -->
		<!-- ************* -->
		
		<div id="popUpAvisos" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
		     <!--  header popup -->
			 <div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
		        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">Aviso</div>
		        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
		                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
		            <span onclick="hidePopUpAviso()">x</span>
		        </a>
			 </div>
			 <!--  body popup -->
			 <div class="panelInformacion_content">
				<div id="panelInformacion" class="panelInformacion">
					<div id="txt_mensaje_aviso"></div>
				</div>
				<div style="margin-top:15px">
				 	    <a class="bot" id="btn_hidePopUpAviso" href="javascript:hidePopUpAviso()" title="Aceptar">Aceptar</a>
				</div>
			 </div>
		</div>
		
		
		<!-- DAA 08/02/2013 ************-->
		<!-- PANEL CAMBIO MASIVO CLASES -->
		<!-- ***************************-->
		
		<div id="panelCambioMasivoClaseDetalle" class="panelCambioMasivo" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">

		     <!--  header popup -->
			<div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
			    <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">Cambio masivo</div>
				<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
					  <span onclick="cerrarCambioMasivoClaseDetalle()">x</span>
				</a>
			</div>
			<!--  body popup -->
			<div class="panelInformacion_content">
				<div id="panelInformacion" class="panelInformacion">
						<form:form name="main" id="main" action="claseDetalle.run" method="post" commandName="claseDetalleBean">
						
							<form:hidden path="id" id="id_cm" />
							<form:hidden path="clase.id" id="detalleid_cm"/>
							<form:hidden path="clase.linea.lineaseguroid" id="lineaseguroid"/>
							<form:hidden path="clase.linea.codplan" id="detalleplan_cm"/>
							<form:hidden path="clase.linea.codlinea" id="detallelinea_cm"/>
							<form:hidden path="clase.clase" id="detalleclase_cm"/>
							
							<input type="hidden" name="method" id="method" value="doCambioMasivo"/>
							<input type="hidden" name="listaIdsMarcados_cm" id="listaIdsMarcados_cm" value=""/>
							
							<div class="panel2 isrt" style="margin:10 auto;">
							<fieldset>
							<table style="width:101%;">
								<tr align="left">
									<td class="literal">Módulo</td>
									<td colspan="1" class="literal">
										<form:input path="codmodulo" size="5" maxlength="5" cssClass="dato" id="modulo_cm" tabindex="2" onchange="this.value=this.value.toUpperCase();"/>
									</td>
									<td class="literal">Cultivo</td>
									<td colspan="1" class="literal">
										<form:input  id="cultivo_cm" path="cultivo.id.codcultivo" cssClass="dato" size="3" maxlength="3" onchange="javascript:lupas.limpiarCampos('desc_cultivo_cm','variedad_cm','desc_variedad_cm');" tabindex="2"/>
										<form:input cssClass="dato" path="cultivo.descultivo" id="desc_cultivo_cm" size="25" readonly="true"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('CultivoCM','principio', '', '');"	alt="Buscar Cultivo" title="Buscar Cultivo" />
									</td>
									<td class="literal">Variedad</td>
									<td colspan="1" class="literal">
										<form:input  id="variedad_cm" path="variedad.id.codvariedad" cssClass="dato" size="3" maxlength="3" onchange="javascript:lupas.limpiarCampos('desc_variedad_cm');" tabindex="2"/>
										<form:input cssClass="dato" path="variedad.desvariedad" id="desc_variedad_cm" size="25" readonly="true"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('VariedadCM','principio', '', '');"	alt="Buscar Variedad" title="Buscar Variedad" />		
									</td>
							    </tr>
							    <tr align="left">
										<td class="literal">Provincia</td>
										<td class="literal">
											<form:input path="codprovincia" size="3" maxlength="2" cssClass="dato" id="provincia_cm"  onchange="javascript:lupas.limpiarCampos('desc_provincia_cm','comarca_cm','desc_comarca_cm','termino_cm','desc_termino_cm','subtermino_cm');" tabindex="2" /> 
											<form:input path="" cssClass="dato"	id="desc_provincia_cm" size="25" readonly="true"/>
											<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('ProvinciaCM','principio', '', '');" alt="Buscar Provincia" title="Buscar Provincia" />
										</td>
										<td class="literal">Comarca</td>
										<td class="literal">
											<form:input path="codcomarca" size="3" maxlength="2" cssClass="dato" id="comarca_cm"  onchange="javascript:lupas.limpiarCampos('desc_comarca_cm','termino_cm','desc_termino_cm','subtermino_cm');" tabindex="2" /> 
											<form:input path="" cssClass="dato"	id="desc_comarca_cm" size="25" readonly="true"/>
											<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('ComarcaCM','principio', '', '');" alt="Buscar Comarca" title="Buscar Comarca" />
										</td>
										<td class="literal"  align="right">Termino</td>
										<td class="literal">
											<form:input path="codtermino" size="3" maxlength="3" cssClass="dato" id="termino_cm"  onchange="javascript:lupas.limpiarCampos('desc_termino_cm','subtermino_cm');" tabindex="2" /> 
											<form:input path="" cssClass="dato"	id="desc_termino_cm" size="25" readonly="true"/>
										</td>
										
								</tr>
								<tr align="left">
									<td class="literal">Subtermino</td>
										<td class="literal">
											<form:input path="subtermino" size="1" maxlength="1" cssClass="dato" id="subtermino_cm" tabindex="2" /> 
											<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('TerminoCM','principio', '', '');" alt="Buscar Termino" title="Buscar Termino"  />
										</td>
									
							 	</tr>
							</table>
							</fieldset>
							</div>
							
							
							
							<div class="panel2 isrt" style="margin:10 auto;">
							<fieldset>
								<table style="width:100%; border:goove">
								<tr align="left">
									<td class="literal">Ciclo Cult.</td>
									<td colspan="1" class="literal">
										<form:input path="cicloCultivo.codciclocultivo" size="3" onchange="javascript:lupas.limpiarCampos('desciclocultivo_cm');" maxlength="3" cssClass="dato" id="cicloCultivo_cm" tabindex="2"/>
										<form:input path="cicloCultivo.desciclocultivo" size="25" maxlength="30" cssClass="dato" id="desciclocultivo_cm" readonly="true"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('CicloCultivoCM','principio', '', '');"	alt="Buscar Ciclo Cultivo" title="Buscar Ciclo Cultivo" id="lupaCicloCultivo"/>
									</td>
									<td class="literal" width="15%">
										<input type="checkbox" id ="cicloCultivoCheck" name="cicloCultivoCheck" onclick="deshabilitaCampo(this);" value="">Sin Valor
									</td>
									<td class="literal" >Sist. Cultivo</td>
									<td colspan="1" class="literal">
										<form:input path="sistemaCultivo.codsistemacultivo" size="3" onchange="javascript:lupas.limpiarCampos('desc_sistemaCultivo_cm');" maxlength="3" cssClass="dato" id="sistemaCultivo_cm" tabindex="2"/>
										<form:input path="sistemaCultivo.dessistemacultivo" size="25" maxlength="30" cssClass="dato" id="desc_sistemaCultivo_cm" readonly="true"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('SistemaCultivoCM','principio', '', '');"	alt="Buscar Sistema Cultivo" title="Buscar Sistema Cultivo" id="lupaSistemaCultivo"/>
									</td>
									<td class="literal">
										<input type="checkbox" id="sistemaCultivoCheck" name="sistemaCultivoCheck" onclick="deshabilitaCampo(this);" value="">Sin Valor
									</td>
								</tr>
								<tr  >
									<td class="literal" >Tipo capital</td>
									<td colspan="1" class="literal">
										<form:input path="tipoCapital.codtipocapital" id="capital_cm" size="3" maxlength="3" cssClass="dato" onchange="javascript:lupas.limpiarCampos('desc_capital_cm');" tabindex="2"/>
											<form:input cssClass="dato"	path="tipoCapital.destipocapital"id="desc_capital_cm" size="25" readonly="true" />
											<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('TipoCapitalCM','principio', '', '');"	alt="Buscar Tipo Capital" title="Buscar Tipo Capital" id="lupaTipoCapital"/>	
									</td>
									<td class="literal">
										<input type="checkbox" id="tipoCapitalCheck" name="tipoCapitalCheck" onclick="deshabilitaCampo(this);" value="">Sin Valor
									</td>
									<td class="literal" >Tipo Plantación</td>
									<td colspan="1" class="literal">
										<form:input path="tipoPlantacion.codtipoplantacion" id="tplantacion_cm" size="3" maxlength="3" cssClass="dato" onchange="javascript:lupas.limpiarCampos('desc_tplantacion_cm');" tabindex="2"/>
										<form:input cssClass="dato"	path="tipoPlantacion.destipoplantacion" id="desc_tplantacion_cm" size="25" readonly="true" />
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('TipoPlantacionCM','principio', '', '');"	alt="Buscar Tipo Plantación" title="Buscar Tipo Plantación" id="lupaTipoPlantacion"/>	
									</td>
									<td class="literal">
										<input type="checkbox" id="tipoPlantacionCheck" name="tipoPlantacionCheck" onclick="deshabilitaCampo(this);" value="">Sin Valor
									</td>
								</tr>	
								</table>
							</fieldset>
							</div>
						</form:form>	
				</div>
				<div style="margin-top:15px">
						    <a class="bot" href="javascript:limpiarCambioMasivoClaseDetalle()" title="Limpiar">Limpiar</a>
						    <a class="bot" href="javascript:cerrarCambioMasivoClaseDetalle()" title="Cancelar">Cancelar</a>
						    <a class="bot" href="javascript:aplicarCambioMasivoClaseDetalle()" title="Aplicar">Aplicar</a>
				</div>
			</div>
		</div>

		<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaClase.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaCicloCultivo.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaCicloCultivoCM.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaSistemaCultivo.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaSistemaCultivoCM.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaCultivo.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaCultivoCM.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaVariedad.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaVariedadCM.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaProvincia.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaProvinciaCM.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaComarca.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaComarcaCM.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaTermino.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaTerminoCM.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaTipoCapital.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaTipoCapitalCM.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaTipoPlantacion.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaTipoPlantacionCM.jsp"%>
	</body>
</html>
