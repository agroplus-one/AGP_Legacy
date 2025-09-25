<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%--
#####################################################################################################################
#																													#
#	Queda pendiente la carga del plan estándar, Una vez hecho esto, habrá que hacer una carga de las subopciones	#
#	en los menús select dejando marcada la que corresponda al bean.													#
#																													#
#####################################################################################################################
--%>

<%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", 0);
%>


<html>
	<head>
		<title>Condiciones Especiales - Período Garantías</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jquery_ui.css" />

		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
		<script type="text/javascript" src="jsp/js/calendar.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
		<script type="text/javascript" src="jsp/moduloTaller/condicionesEspeciales/periodoGarantia.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		
		<script type="text/javascript">
			$(document).ready(function() {
		      	Zapatec.Calendar.setup({
			        firstDay          : 1,
			        weekNumbers       : false,
			        showOthers        : true,
			        showsTime         : false,
			        timeFormat        : "24",
			        step              : 2,
			        range             : [1900.01, 2999.12],
			        electric          : false,
			        singleClick       : true,
			        inputField        : "fechaini",
			        button            : "btn_fechaini",
			        ifFormat          : "%d-%m-%Y",
			        daFormat          : "%d-%m-%Y",
			        align             : "Br"
		      	});
		      	Zapatec.Calendar.setup({
			        firstDay          : 1,
			        weekNumbers       : false,
			        showOthers        : true,
			        showsTime         : false,
			        timeFormat        : "24",
			        step              : 2,
			        range             : [1900.01, 2999.12],
			        electric          : false,
			        singleClick       : true,
			        inputField        : "fechafin",
			        button            : "btn_fechafin",
			        ifFormat          : "%d-%m-%Y",
			        daFormat          : "%d-%m-%Y",
			        align             : "Br"
		      	});
	      	});
		</script>
		<script type="text/javascript">
			
			$(document).ready(function(){
				$("#botonModificar").hide();	
			
			
				$('#selectLinea').change(function() {
				
					$("#selectCultivo").empty();
					$("#selectEstadoFenologicoIni").empty();
					$("#selectEstadoFenologicoFin").empty();
					
					cargarSelectCultivo();
					
				});
				
				$('#selectCultivo').change(function() {
				
					$("#selectEstadoFenologicoIni").empty();
					$("#selectEstadoFenologicoFin").empty();
					cargarSelectsEstadosFenologicos();
					
				});
				
				$('#main').validate({
					 errorLabelContainer: "#panelAlertasValidacion",
   					 wrapper: "li",
					rules: { 
						codlinea: "required",
						codcultivo: "required"
					},
					messages: {
						codlinea: "El campo Línea es obligatorio",
						codcultivo: "El campo Cultivo es obligatorio"
					}
				});
				
				
				<c:if test="${not empty periodoGarantiaBean.linea.codlinea}">
					cargarSelectCultivo('${periodoGarantiaBean.codcultivo}','${periodoGarantiaBean.estadofenologicoini}','${periodoGarantiaBean.estadofenologicofin}');
					//cargarSelectsEstadosFenologicos('${periodoGarantiaBean.estadofenologicoini}','${periodoGarantiaBean.estadofenologicofin}');
				</c:if>
			
			});
			
			function cargarSelectCultivo(codCultivo, idEstadoInicio, idEstadoFin){
			
				var codLinea = $("#selectLinea").val();
				
				if(codLinea != ''){
			
					$.ajax({
				            url: "periodoGarantia.html",
				            data: "operacion=getCultivos_ajax&codLinea="+codLinea,
				            dataType: "json",
				            success: function(datos){
								$("#selectCultivo").removeOption(/./);
								$("#selectCultivo").addOption("","-- Seleccione una opción --");
								$.each(datos, function(index, value) { 
				 					$("#selectCultivo").addOption(value.value, value.nodeText);
								});
								
								$("#selectCultivo").selectOptions(codCultivo == undefined ? "" : String(codCultivo));
								
				            },
				            beforeSend: function(){
			            		$("#ajaxLoading_selectCultivo").show();
            				},
            				complete: function(){
            					$("#ajaxLoading_selectCultivo").hide();
            					cargarSelectsEstadosFenologicos(idEstadoInicio, idEstadoFin);
            				},
				            type: "POST"
						});
			
				}
			}

			function cargarSelectsEstadosFenologicos(idEstadoInicio, idEstadoFin){
			
				var codCultivo = $("#selectCultivo").val();
				
				if(codCultivo != ''){
			
					$.ajax({
				            url: "periodoGarantia.html",
				            data: "operacion=getEstadosFenologicos_ajax&codCultivo="+codCultivo,
				            dataType: "json",
				            success: function(datos){
								$("#selectEstadoFenologicoIni").removeOption(/./);
								$("#selectEstadoFenologicoFin").removeOption(/./);
								$("#selectEstadoFenologicoIni").addOption("","-- Seleccione una opción --");
								$("#selectEstadoFenologicoFin").addOption("","-- Seleccione una opción --");
								$.each(datos, function(index, value) { 
				 					$("#selectEstadoFenologicoIni").addOption(value.value, value.nodeText);
				 					$("#selectEstadoFenologicoFin").addOption(value.value, value.nodeText);
								});
								
								$("#selectEstadoFenologicoIni").selectOptions(idEstadoInicio == undefined ? "" : String(idEstadoInicio));
								$("#selectEstadoFenologicoFin").selectOptions(idEstadoFin == undefined ? "" : String(idEstadoFin));
				            },
				            beforeSend: function(){
			            		$("#ajaxLoading_selectEstadoFenologicoIni").show();
			            		$("#ajaxLoading_selectEstadoFenologicoFin").show();
            				},
            				complete: function(){
            					$("#ajaxLoading_selectEstadoFenologicoIni").hide();
            					$("#ajaxLoading_selectEstadoFenologicoFin").hide();
            				},
				            type: "POST"
						});
			
				}
			}

			
			function limpiar(){
			
				$("#selectLinea").selectOptions("");
				$("#selectCultivo").empty();
				$("#selectEstadoFenologicoIni").empty();
				$("#selectEstadoFenologicoFin").empty();
				$("#nummesesini").val("");
				$("#numdiasini").val("");
				$("#fechaini").val("");
				$("#nummesesfin").val("");
				$("#numdiasfin").val("");
				$("#fechafin").val("");
				$("#botonAlta").show();
				$("#botonModificar").hide();
				$("#panelInformacion").hide();
				$("#id").val("");
			
			}
			
			function consultar(){
				
				$("#main").validate().cancelSubmit = true;
				$('#operacion').val("consultar");
				$('#main').submit();
			
			}
			
			function editar(idPeriodoGarantiaCe){

				$('#id').val(idPeriodoGarantiaCe);
				$("#botonAlta").hide();
				$("#botonModificar").show();
				$('#operacion').val("editar_ajax");
				$("#panelInformacion").hide();
				
				$.ajax({
			            url: "periodoGarantia.html",
			            data: "operacion=editar_ajax&idPeriodoGarantiaCe="+idPeriodoGarantiaCe,
			            dataType: "json",
			            success: function(datos){
			            	$('#id').val(datos.id);
			            	$("#selectLinea").selectOptions(String(datos.idLinea));
			            	cargarSelectCultivo(datos.idCultivo,datos.estadofenologicoini,datos.estadofenologicofin);
			            	$("#nummesesini").val(datos.nummesesini);
							$("#numdiasini").val(datos.numdiasini);
							$("#fechaini").val(datos.fechaini);
							$("#nummesesfin").val(datos.nummesesfin);
							$("#numdiasfin").val(datos.numdiasfin);
							$("#fechafin").val(datos.fechafin);
							//cargarSelectsEstadosFenologicos(datos.estadofenologicoini,datos.estadofenologicofin);
			            },
			            type: "POST"
					});
				
			
			}
			
			function modificar(idPeriodoGarantiaCe){

		        $("#panelInformacion").hide();
		        
				$('#operacion').val("modificar");
				$('#main').submit();			    
			}
			
			function eliminar(idPeriodoGarantiaCe){

				if(confirm('¿Está seguro de que desea eliminar el registro seleccionado?')){
					$("#main").validate().cancelSubmit = true;
					$('#id').val(idPeriodoGarantiaCe);
					$('#operacion').val("eliminar");
					$('#main').submit();

				}
			
			}
			
			function alta(){

		        $("#panelInformacion").hide();
		        
				$('#id').val('');
				$('#operacion').val("alta");
				$('#main').submit();			    
				
			}
			
			
			
		</script>
<%-- TODO: exportar método --%>
		<script type="text/javascript">
		
		</script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub1')">

		<%@ include file="../../common/static/cabecera.jsp"%>
		<%@ include file="../../common/static/menuLateralTaller.jsp"%>
		<%@ include file="../../common/static/datosCabeceraTaller.jsp"%>
		
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="right">
						<a class="bot" id="botonAlta" href="javascript:alta();">Alta</a>
						<a class="bot" id="botonModificar" href="javascript:modificar();">Modificar</a>
						<a class="bot" href="javascript:consultar();">Consultar</a>
						<a class="bot" href="javascript:limpiar();">Limpiar</a>
					</td>
				</tr>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%">
			<p class="titulopag" align="left">Periodo de Garantías</p>
			<form:form id="main" name="main" action="periodoGarantia.html" method="post" commandName="periodoGarantiaBean">
			
				<ul id="messageBox"></ul>
				
				<input type="hidden" id="operacion" name="operacion" value="inicio" />
				<form:hidden path="id" id="id"/>
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				
				<div class="panel2 isrt">
					<table width="100%">
						<tr>
							<td>
								<span class="detalD">Línea</span>
							</td>
							<td>
								<form:select id="selectLinea" path="linea.codlinea" cssClass="dato">
									<form:option value="">-- Seleccione una opción --</form:option>
									<c:forEach var="linea" items="${listaLineas}">
										<form:option value="${linea[0]}" label="${linea[0]} - ${linea[1]}" />
									</c:forEach>
								</form:select>
							</td>
						</tr>
						<tr>
							<td>
								<span class="detalD">Cultivo</span>
							</td>
							<td>
								<form:select id="selectCultivo" path="cultivo.id.codcultivo" cssClass="dato">
								</form:select>
								<img id="ajaxLoading_selectCultivo" src="jsp/img/ajax-loading.gif" width="16px" style="cursor:hand;cursor:pointer;display:none" height="11px" />
							</td>
						</tr>
					</table>
				</div>
				<div class="panel2 isrt">
					<fieldset>
						<legend>Inicio Garantía</legend>
						<table class="NORMAL">
							<tr>
								<td class="literal">Fecha Garantía</td>
								<td class="literal">
									<spring:bind path="fechaini">
										<c:set var="fechaFormateada">
											<fmt:formatDate pattern="dd-MM-yyyy" value="${periodoGarantiaBean.fechaini}" />
										</c:set>
										<spring:transform var="fechaini" value="${fechaFormateada }" />
										<input type="text" name="fechaini" id="fechaini" size="11" maxlength="10" class="dato"
												value="${fechaini }" />
									</spring:bind>
									<input type="button" id="btn_fechaini" name="btn_fechaini" class="miniCalendario"
											style="cursor: pointer;" />
									<span id="errorFechaPrimerPago"/>
								</td>
								<td class="literal">Estado Fenológico</td>
								<td class="literal">
									<form:select path="estadofenologicoini" cssClass="dato" id="selectEstadoFenologicoIni">
									</form:select>
									<img id="ajaxLoading_selectEstadoFenologicoIni" src="jsp/img/ajax-loading.gif" width="16px" style="cursor:hand;cursor:pointer;display:none" height="11px" />
								</td>
							</tr>
							<tr>
								<td class="literal">Núm. Meses</td>
								<td class="literal">
									<form:input path="nummesesini" cssClass="dato" size="3" maxlength="3" id="nummesesini"/>
								</td>
								<td class="literal">Núm. Días</td>
								<td class="literal">
									<form:input path="numdiasini" cssClass="dato" size="2" maxlength="2" id="numdiasini"/>
								</td>
							</tr>
						</table>
					</fieldset>
				</div>
				<div class="panel2 isrt">
					<fieldset>
						<legend>Final Garantía</legend>
						<table class="NORMAL">
							<tr>
								<td class="literal">Fecha Garantía</td>
								<td class="literal">
									<spring:bind path="fechafin">
										<c:set var="fechaFormateada">
											<fmt:formatDate pattern="dd-MM-yyyy" value="${periodoGarantiaBean.fechafin}" />
										</c:set>
										<spring:transform var="fechafin" value="${fechaFormateada }" />
										<input type="text" name="fechafin" id="fechafin" size="11" maxlength="10" class="dato"
												value="${fechafin }" />
									</spring:bind>
									<input type="button" id="btn_fechafin" name="btn_fechafin" class="miniCalendario" style="cursor: pointer;" />
									<span id="errorFechaPrimerPago"/>
								</td>
								<td class="literal">
									<span class="literal">Estado Fenológico</span>
								</td>
								<td class="literal">
									<form:select path="estadofenologicofin" cssClass="dato" id="selectEstadoFenologicoFin">
									</form:select>
									<img id="ajaxLoading_selectEstadoFenologicoFin" src="jsp/img/ajax-loading.gif" width="16px" style="cursor:hand;cursor:pointer;display:none" height="11px" />
								</td>
							</tr>
							<tr>
								<td class="literal">Núm. Meses</td>
								<td class="literal">
									<form:input path="nummesesfin" cssClass="dato" size="3" maxlength="3" id="nummesesfin"/>
								</td>
								<td class="literal">Núm. Días</td>
								<td class="literal">
									<form:input path="numdiasfin" cssClass="dato" size="2" maxlength="2" id="numdiasfin"/>
								</td>
							</tr>
						</table>
					</fieldset>
				</div>
			</form:form>
			<br />      
			
			<div class="grid" style="">
				<display:table requestURI="periodoGarantia.html" id="listPeriodosGarantiaCe" class="LISTA" summary="periodoGarantia" name="${listaPeriodoGarantiaCe}" sort="list" pagesize="10" decorator="com.rsi.agp.core.decorators.ModelTableDecoratorPeriodoGarantiaCe" style="width:95%" excludedParams="operacion" defaultsort="1">
		            <display:column class="literal" headerClass="cblistaImg" title="Acciones" property="acciones" style="text-align:center; width:50px"/>
					<display:column class="literal" headerClass="cblistaImg" title="Línea" property="columnaLinea" sortable="true" />
					<display:column class="literal" headerClass="cblistaImg" title="Cultivo" property="columnaCultivo" sortable="true" />
					<display:column class="literal" headerClass="cblistaImg" title="Fec. Ini" property="fechaini" format="{0,date,dd/MM}" sortable="true" />
					<display:column class="literal" headerClass="cblistaImg" title="Est. Fen." property="estadofenologicoini" />
					<display:column class="literal" headerClass="cblistaImg" title="Fec. Fin" property="fechafin" format="{0,date,dd/MM}" sortable="true" />
					<display:column class="literal" headerClass="cblistaImg" title="Est. Fen." property="estadofenologicofin" sortable="true" />
					
		        </display:table>		
			</div>
		</div>
		<br />
		<%@ include file="../../common/static/piePagina.jsp"%>
	</body>
</html>