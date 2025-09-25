<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
<head>
	<title>Datos para RC de Ganado</title>
	
	<%@ include file="/jsp/common/static/metas.jsp"%>
	
	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
	
	<!-- JavaScript,jQery & AJAX -->
	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
	<script type="text/javascript" src="jsp/js/util.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
	<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>	
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
	<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
	<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
	<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
	<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
	<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
	<script type="text/javascript" src="jsp/js/lupaGenericaIN.js"></script>
	<script type="text/javascript" src="jsp/js/lineas.js"></script>
	<script type="text/javascript" src="jsp/js/ajaxfileupload.js"></script>	
	<script type="text/javascript" src="jsp/js/jquery.filestyle.js"></script>
	
	<script type="text/javascript" src="jsp/moduloRC/datosRC.js" ></script>
	<script type="text/javascript" src="jsp/moduloRC/datosRCCambioMasivo.js" ></script>
	
	<script type="text/javascript">
		function cargarFiltro() {			 
		 	<c:if test="${origenLlamada != 'menuGeneral'}">	
				<c:forEach items="${sessionScope.listaDatosRC_LIMIT.filterSet.filters}" var="filtro">				
					<c:if test="${filtro.property == 'linea.codplan'}">
						$('#plan').val('${filtro.value}');
					</c:if>	
					<c:if test="${filtro.property == 'linea.codlinea'}">
						$('#linea').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'subentidadMediadora.id.codentidad'}">
						$('#entmediadora').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'subentidadMediadora.id.codsubentidad'}">
						$('#subentmediadora').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'especiesRC.codespecie'}">
						$('#especiesRC').val('${filtro.value}');
					</c:if>	
					<c:if test="${filtro.property == 'regimenRC.codregimen'}">
						$('#regimenesRC').val('${filtro.value}');
					</c:if>	
					<c:if test="${filtro.property == 'sumaAseguradaRC.codsuma'}">
						$('#sumaAseguradaRC').val('${filtro.value}');
					</c:if>	
					<c:if test="${filtro.property == 'tasa'}">
						$('#tasa').val('${filtro.value}');
					</c:if>	
					<c:if test="${filtro.property == 'franquicia'}">
						$('#franquicia').val('${filtro.value}');
					</c:if>	
					<c:if test="${filtro.property == 'primaMinima'}">
						$('#primaMinima').val('${filtro.value}');
					</c:if>	
				</c:forEach>
			</c:if>
		}
	</script>	
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchSubMenu('sub15','sub14'); cargarFiltro();">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	<!-- botones de la página -->
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td width="5">&nbsp;</td>
				<td align="left">
					<a class="bot" id="btnCambioMasivo" href="javascript:cambioMasivo();">Cambio masivo</a>
					<a class="bot" id="btnReplicar" href="javascript:replicar();">Replicar</a>
				</td>
				<td align="right"> 
					<a class="bot" id="btnAlta" href="javascript:alta();">Alta</a>
					<a class="bot" id="btnModificar" href="javascript:modificar();" style="display:none;">Modificar</a>
					<c:if test="${origenLlamada == 'menuGeneral'}">
						<a class="bot" id="btnConsultar" href="javascript:consultarInicial();">Consultar</a>
					</c:if>
					<c:if test="${origenLlamada != 'menuGeneral'}">
						<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a> 
					</c:if>	
					<a class="bot" id="btnLimpiar"href="javascript:limpiar()">Limpiar</a>
				</td>
			</tr>
		</table>
	</div>
	
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">DATOS PARA RC DE GANADO</p>
	
		<!-- Form principal -->
		<form:form id="formDatosRC" name="formDatosRC" action="datosRC.run" method="post" commandName="datosRC">
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>	
			
			<input type="hidden" name="usuarioSession" id="usuarioSession" value="${usuarioSession}"/>		
			<input type="hidden" name="method" id="method"/>
			<input type="hidden" name="origenLlamada" id="origenLlamada"/>
			<form:hidden path="id" id="id"/>
			<input type="hidden" name="entidad" id="entidad"/>
			
			<input type="hidden" name="listaIdsMarcados" id="listaIdsMarcados" value=""/>
			<input type="hidden" name="listaIdsTodos" id="listaIdsTodos" value="${listaIdsTodos}"/>
			<input type="hidden" name="marcaTodos" id="marcaTodos" value="false"/>
			
			<form:hidden path="linea.lineaseguroid" id="lineaseguroid"/>
			<input type="hidden" id="codplan" name="codplan" value=""/>
			<input type="hidden" id="codlinea" name="codlinea" value=""/>
			
			<input type="hidden" name="planreplica" id="planreplica" value="${planreplica}"/>
			<input type="hidden" name="lineareplica" id="lineareplica" value="${lineareplica}"/>
			
			<fieldset class="panel2 isrt" style="width:98%;margin:0 auto;">
				<legend class="literal">Filtro</legend>
				<div style="width: 98%; float: left;">
					<table cellspacing="10px">
						<tr>
							<td>
								<label for="plan" class="literal">Plan</label>
								<form:input path="linea.codplan" size="4" maxlength="4"
									cssClass="dato" id="plan" tabindex="1"
									onchange="javascript:lupas.limpiarCampos('linea', 'desc_linea');" />
								<label class="campoObligatorio" id="campoObligatorio_plan"
									title="Campo obligatorio"> *</label> 
							</td>
							<td>
								<label for="linea" class="literal">Línea</label>
								<form:input path="linea.codlinea" size="3" maxlength="3"
									cssClass="dato" id="linea" tabindex="2"
									onchange="javascript:lupas.limpiarCampos('desc_linea');" />
								<form:input path="linea.nomlinea" cssClass="dato" id="desc_linea"
									size="30" readonly="true" />
								<img src="jsp/img/magnifier.png" style="cursor: hand;"
									onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');"
									alt="Buscar Línea" title="Buscar Línea" /> 
								<label class="campoObligatorio"
									id="campoObligatorio_linea" title="Campo obligatorio"> *</label>
							</td>
							<td>
								<label for="entmediadora" class="literal">E-S Med.</label>
								<form:input path="subentidadMediadora.id.codentidad" size="4" maxlength="4" cssClass="dato" id="entmediadora" tabindex="3" />
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_entmediadora"> *</label>
							    <form:input path="subentidadMediadora.id.codsubentidad" size="4" maxlength="4" cssClass="dato" id="subentmediadora" tabindex="4" />
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_subentmediadora"> *</label>
								<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');" alt="Buscar E-S Med." title="Buscar E-S Med." />
							</td>
							<td>
								<label for="especiesRC" class="literal">Especie para RC</label>
								<form:select path="especiesRC.codespecie" cssClass="dato" tabindex="5"
									cssStyle="width:220px" id="especiesRC">
									<form:option value="">Todos</form:option>
									<c:forEach var="i" begin="0" end="${fn:length(listaEspeciesRC) - 1 }">
										<form:option value="${listaEspeciesRC[i].codespecie}">${listaEspeciesRC[i].descripcion}</form:option>
									</c:forEach>
								</form:select>
								<label class="campoObligatorio"
									id="campoObligatorio_especiesRC" title="Campo obligatorio"> *</label>
							</td>
						</tr>
					</table>
					<table cellspacing="10px">
						<tr>
							<td>
								<label for="regimenesRC" class="literal">Régimen para RC</label>
								<form:select path="regimenRC.codregimen" cssClass="dato" tabindex="6"
									cssStyle="width:180px" id="regimenesRC">
									<form:option value="">Todos</form:option>
									<c:forEach var="i" begin="0" end="${fn:length(listaRegimenesRC) - 1 }">
										<form:option value="${listaRegimenesRC[i].codregimen}">${listaRegimenesRC[i].descripcion}</form:option>
									</c:forEach>
								</form:select>
								<label class="campoObligatorio"
									id="campoObligatorio_regimenesRC" title="Campo obligatorio"> *</label>
							</td>
							<td>
								<label for="sumaAseguradaRC" class="literal">Suma Asegurada</label>
								<form:select path="sumaAseguradaRC.codsuma" cssClass="dato" tabindex="7"
									cssStyle="width:110px" id="sumaAseguradaRC">
									<form:option value="">Todos</form:option>
									<c:forEach var="i" begin="0" end="${fn:length(listaSumasAseguradasRC) - 1 }">
										<form:option value="${listaSumasAseguradasRC[i].codsuma}">${listaSumasAseguradasRC[i].descripccion}</form:option>
									</c:forEach>
								</form:select>
								<label class="campoObligatorio"
									id="campoObligatorio_sumaAseguradaRC" title="Campo obligatorio"> *</label>
							</td>
							<td>
								<label for="tasa" class="literal">Tasa</label>
								<form:input path="tasa" size="9" maxlength="9"
									cssClass="dato" id="tasa" tabindex="8" />
								<label class="campoObligatorio" id="campoObligatorio_tasa"
									title="Campo obligatorio"> *</label> 
							</td>
							<td>
								<label for="franquicia" class="literal">Franquicia</label>
								<form:input path="franquicia" size="9" maxlength="9"
									cssClass="dato" id="franquicia" tabindex="9" />
								<label class="campoObligatorio" id="campoObligatorio_franquicia"
									title="Campo obligatorio"> *</label> 
							</td>
							<td>
								<label for="primaMinima" class="literal">Prima mínima</label>
								<form:input path="primaMinima" size="6" maxlength="6"
									cssClass="dato" id="primaMinima" tabindex="10" />
								<label class="campoObligatorio" id="campoObligatorio_primaMinima"
									title="Campo obligatorio"> *</label> 
							</td>
						</tr>
					</table>
				</div>
			</fieldset>
		</form:form>
	
		<!-- Grid Jmesa -->
		<div id="grid">
	  		${listaDatosRC}		  							               
		</div> 	
		
	</div>
		
	<%-- Si se ponen al final no funcionan correctamente --%>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>	
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<%@ include file="/jsp/common/static/overlayCambioMasivo.jsp"%>
	
	<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>	
	<%@ include file="/jsp/common/lupas/lupaLineaReplicar.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFecha.jsp"%>
		
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
			<div id="panelInformacion2" class="panelInformacion">
				<div id="txt_mensaje_aviso"></div>
			</div>
			<div style="margin-top:15px">
			 	<a class="bot" id="btn_hidePopUpAviso" href="javascript:hidePopUpAviso()" title="Aceptar">Aceptar</a>
			</div>
		 </div>
	</div>	
	
	<!-- PANEL CAMBIO MASIVO -->
	<!-- ***************************-->
	<div id="panelCambioMasivoDatosRC" class="panelCambioMasivo"
		style="left: 30%; top: 20%; width: 40%; color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em;">

		<!--  header popup -->
		<div id="header-popup"
			style="padding: 0.4em 1em; position: relative; color: #FFFFFF; font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px">
			<div
				style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Cambio
				masivo</div>
			<a
				style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
				<span onclick="cerrarCambioMasivoDatosRC()">x</span>
			</a>
		</div>
		<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelInformacionCM" class="panelInformacion">
				<div id="txt_mensaje_cm"
					style="color: red; display: block; font-size: 12px; font-style: italic; font-weight: bold; line-height: 20px;"></div>
				<form:form name="formDatosRCCM" id="formDatosRCCM" action="datosRC.run" method="post"
					commandName="datosRC">

					<input type="hidden" name="method" id="method"
						value="doCambioMasivo" />
					<input type="hidden" name="listaIdsMarcados_cm"
						id="listaIdsMarcados_cm" value="" />

					<div class="panel2 isrt" style="width: 100%">
						<fieldset class="panel2 isrt" style="width: 100%">
							<table cellspacing="10px" style="width: 100%">
								<tr align="left">
									<td><label for="tasaCM" class="literal">Tasa</label> <input
										type="text" name="tasaCM" size="9" maxlength="9"
										class="dato" id="tasaCM" tabindex="11" /> <label
										class="campoObligatorio" id="campoObligatorio_tasaCM"
										title="Campo obligatorio"> *</label></td>
									<td><label for="franquiciaCM" class="literal">Franquicia</label>
										<input type="text" name="franquiciaCM" size="9" maxlength="9"
										class="dato" id="franquiciaCM" tabindex="12" /> <label
										class="campoObligatorio" id="campoObligatorio_franquiciaCM"
										title="Campo obligatorio"> *</label></td>
									<td><label for="primaMinimaCM" class="literal">Prima
											mínima</label> <input type="text" name="primaMinimaCM" size="6"
										maxlength="6" class="dato" id="primaMinimaCM" tabindex="13" />
										<label class="campoObligatorio"
										id="campoObligatorio_primaMinimaCM" title="Campo obligatorio">
											*</label></td>
								</tr>
							</table>
						</fieldset>
					</div>
				</form:form>
			</div>
			<div style="margin-top: 15px">
				<a class="bot" href="javascript:limpiarCambioMasivoDatosRC()"
					title="Limpiar">Limpiar</a> <a class="bot"
					href="javascript:cerrarCambioMasivoDatosRC()" title="Cancelar">Cancelar</a>
				<a class="bot" href="javascript:aplicarCambioMasivoDatosRC()"
					title="Aplicar">Aplicar</a>
			</div>
		</div>
	</div>
</body>
</html>