<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<fmt:bundle basename="displaytag" >
	<c:set var="numReg"><fmt:message key="numElementsPag"/></c:set>
</fmt:bundle>
<html>
	<head>
		<title>Revisión de Subvenciones por Modificación del Asegurado</title>
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<!-- Estilos -->
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		
		<!-- JavaScript,jQery & AJAX -->
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
        <script type="text/javascript" src="jsp/js/calendar.js"></script>
        <script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
        <script type="text/javascript" src="jsp/js/calendar-sp.js"></script>

		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
		
		<script type="text/javascript" charset="ISO-8859-1">
            // Para evitar el cacheo de peticiones al servidor
	        $(document).ready(function(){
	            var URL = UTIL.antiCacheRand(document.getElementById("main").action);
		        document.getElementById("main").action = URL;
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
			        inputField        : "fecEnvioId",
			        button            : "btn_fechaenvio",
			        ifFormat          : "%d/%m/%Y",
			        daFormat          : "%d/%m/%Y",
			        align             : "Br"			        	        
		      	});
		    });  
		        
		      	
		    function consultar(){
				var frm = document.getElementById('main');			
				frm.operacion.value = 'polizasAseg';
				$('#main').submit();
			}  	
		    
	        function volver() {
	       		var frm = document.getElementById('main');
				frm.operacion.value = 'retornarAsegSinModificar';
				$('#main').submit();
			}
			
			function subvenciones() {
				var frm = document.getElementById('main');
				frm.operacion.value = 'irSeleccionSubvenciones';
				$('#main').submit();
			}
			
			function modificar(oficina, usuario, colectivo, dc, plan, linea, desc_linea, modulo, referencia, fecenvio){
				var frm = document.getElementById('main');
				frm.oficina.value = oficina;
				frm.codusuario.value = usuario;
				frm.colectivo.value = colectivo;
				frm.dc.value=dc;
				frm.plan.value = plan;
				frm.linea.value = linea;
				frm.codmodulo.value = modulo;
				frm.referencia.value = referencia;			
				frm.fecEnvioId.value = fecenvio;
				frm.desc_linea.value = desc_linea		
				
			}
			
			function limpiar(){
				$('#oficina').val('');
				$('#codusuario').val('');
				$('#colectivo').val('');
				$('#dc').val('');
				$('#plan').val('');
				$('#linea').val('');
				$('#desc_linea').val('');
				$('#referencia').val('');
				$('#fecEnvioId').val('');
				$('#opTipoPol').selectOptions('');
				$('#codmodulo').val('');
				consultar();
			}
			
		</script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4');">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabecera.jsp"%>
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr align="left">
					<td align="right">
						<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
						<a class="bot" id="btnLimpiar" href="javascript:limpiar();">Limpiar</a>
						<a class="bot" id="btnSubvenciones" href="javascript:subvenciones();">Subvenciones</a>
						<a class="bot" id="btnVolver" href="javascript:volver();">Volver</a>
					</td>
				</tr>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%">
			<p class="titulopag" align="left">Revisión de Subvenciones por Modificación del Asegurado</p>
		<div style="panel2 isrt">
			<fieldset style="width:95%;">
				<table >
					<tr>
						<td width="40px" class="literal">Entidad</td>
						<td width="60px" class="detalI">
							${entidad}
						</td>
						<td width="40px" class="literal">Asegurado</td>
						<td width="170px"" class="detalI">
							${nomAseg}
						</td>
						<td width="40px" class="literal">CIF/NIF</td>
						<td width="60px" class="detalI">
							${nifcifAseg}
						</td>
					</tr>
				</table>
			</fieldset>
		</div>	
			<form:form name="main" id="main" action="utilidadesPoliza.html" method="post" commandName="polizaBean">
				<input type="hidden" name="operacion" id="operacion" value="polizasAseg"/>
				<input type="hidden" name="modoLectura" id="modoLectura" value=""/>
				<form:hidden path="linea.lineaseguroid" id="lineaseguroid" />
				<form:hidden path="idpoliza" id="idpoliza" />
				<form:hidden path="idenvio" id="idenvio"/>
				
				<input type="hidden" name="oficina2" id="sucursal"/>
				<input type="hidden" name="idAseg" id="idAseg" value="${idAseg}"/>
				<input type="hidden" name="joven" id="joven" value="${joven}"/>
				<input type="hidden" name="prof" id="prof" value="${prof}"/>
				<input type="hidden" name="fecEnvioId.day" value=""> 
	            <input type="hidden" name="fecEnvioId.month" value=""> 
	            <input type="hidden" name="fecEnvioId.year" value="">
	            <input type="hidden" name="entidad" id="entidad" value="${entidad }">
	            <input type="hidden" name="grupoEntidades" id="grupoEntidades" value="">
	           	<input type="hidden" name="grupoOficinas" id="grupoOficinas" value=""/>
	            <input type="hidden" name="nomAseg" value="${nomAseg }">
	            <input type="hidden" name="nifcifAseg" value="${nifcifAseg }">
	            <input type="hidden" name="cifTomador" id="cifTomador" value="">
	            <input type="hidden" name="nomColectivo" id="nomColectivo" value="">
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				
				<div class="panel2 isrt">
					<fieldset>
							<table width="100%">
							<tr align="left">
								
								<td class="literal">Oficina</td>
								<td class="literal">
									<c:if test="${perfil == 0 || perfil ==1 || perfil == 5}">
										<form:input path="oficina" size="5" maxlength="4" cssClass="dato" id="oficina" tabindex="1" readonly="false" onchange="javascript:lupas.limpiarCampos('desc_oficina');"/>
										<form:input path="" cssClass="dato"	id="desc_oficina" size="20" readonly="true"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Oficina','principio', '', '');" alt="Buscar Oficina" title="Buscar Oficina" />
									</c:if>
									<c:if test="${perfil > 1 && perfil < 5}">
										<form:input path="oficina" size="4" maxlength="4" cssClass="dato" readonly="true" id="oficina" tabindex="1"/>
									</c:if>	
								</td>
								<td class="literal">Usuario</td>
								<td class="literal">
									<c:if test="${perfil != 4}">
										<form:input path="usuario.codusuario" id="codusuario" size="8" maxlength="8" cssClass="dato" readonly="false" tabindex="2" onchange="this.value=this.value.toUpperCase();"/>
									</c:if>
									<c:if test="${perfil == 4}">
										<form:input path="usuario.codusuario" id="codusuario" size="8" maxlength="8" cssClass="dato" readonly="true" tabindex="2"/>
									</c:if>
								</td>
								<td class="literal">Colectivo</td>
								<td class="literal">
									<form:input path="colectivo.idcolectivo" id="colectivo" size="7" maxlength="7" tabindex="3" cssClass="dato" readonly="false" onchange="javascript:lupas.limpiarCampos('dc');"/>
									<form:input path="dc" cssClass="dato" id="dc" size="2" maxlength="2" readonly="true"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Colectivo','principio', '', '');" alt="Buscar Colectivo" title="Buscar Colectivo" />
								</td>
							</tr>
							<tr align="left">
								<td class="literal">Plan</td>
								<td class="literal">
									<form:input path="linea.codplan" size="5" maxlength="4" cssClass="dato" id="plan" tabindex="4" readonly="false" onchange="javascript:lupas.limpiarCampos('linea', 'desc_linea', 'codmodulo');"/>
								</td>
								<td class="literal">Línea</td>
								<td class="literal">
									<form:input path="linea.codlinea" size="5" maxlength="4" cssClass="dato" id="linea" tabindex="5" onchange="javascript:lupas.limpiarCampos('desc_linea', 'codmodulo');"/>
									<form:input path="linea.nomlinea" cssClass="dato" id="desc_linea" size="40" readonly="true"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />	
								</td>
								<td class="literal">Módulo</td>
								<td class="literal">
									<form:input path="codmodulo" id="codmodulo" size="4" maxlength="4" tabindex="6" cssClass="dato" readonly="false" onchange="this.value=this.value.toUpperCase();"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Modulo','principio', '', '');" alt="Buscar Módulo" title="Buscar Módulo" />
								</td>
								
								<td class="literal">Póliza</td>
								<td class="literal">
									<form:input path="referencia"  id="referencia" size="15" maxlength="15" tabindex="7" cssClass="dato" readonly="false"/>
								</td>
							</tr>
							<tr align="left">
								<td class="literal">F.Envío</td>
								<td class="literal">
					                    <spring:bind path="fechaenvio">
					                    	 <input type="text" name="fechaenvio" id="fecEnvioId" size="11" maxlength="10" tabindex="8" class="dato" 
					                    	        onchange="if (!ComprobarFecha(this, document.main, 'Fecha envio')) this.value='';"
													value="<fmt:formatDate pattern="dd/MM/yyyy" value="${polizaBean.fechaenvio}" />" />
					                    </spring:bind>
				 
				                     <input type="button" id="btn_fechaenvio" name="btn_fechaenvio" class="miniCalendario" style="cursor: pointer;" /> 
				                     <label	class="campoObligatorio" id="campoObligatorio_fechaenvio"	title="Campo obligatorio"> *</label>
			                 	</td>
			                 	<td class="literal">Tipo Póliza</td>
								<td class="literal" >	
									<select name="opTipoPol"  tabindex="9" class="dato"	style="width:70" id="opTipoPol">
										<option value="" >TODAS</option>
										<option value="1"<c:if test="${opTipoPol == 1}">selected</c:if>>P</option>
										<option value="0"<c:if test="${opTipoPol == 0}">selected</c:if>>C</option>
									</select>
								</td>
			                 </tr>
						</table>
					</fieldset>
				</div>
			</form:form>
				<display:table requestURI="utilidadesPoliza.html" class="LISTA" summary="poliza" defaultsort="0" defaultorder="ascending"
						pagesize="${numReg}" sort="list" name="${listaPolizas}" id="poliza" 
						decorator="com.rsi.agp.core.decorators.ModelTableDecoratorPolizasAseg" 
						style="width:100%;border-collapse:collapse">
					<display:column class="literal" headerClass="cblistaImg" title="Acciones" property="polSelec" sortable="false" style="width:60px;text-align:center"/>
					<display:column class="literal" headerClass="cblistaImg" title="Ent." property="polEntidad" sortable="false" style="width:40px;"/>
					<display:column class="literal" headerClass="cblistaImg" title="Ofi." property="oficina" sortProperty="oficina" sortable="true" style="width:40px;"/>			
					<display:column class="literal" headerClass="cblistaImg" title="Póliza" property="polPoliza" />
					<display:column class="literal" headerClass="cblistaImg" title="Plan" property="polPlan" sortProperty="linea.codplan" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="Línea" property="polLinea" sortProperty="linea.codlinea" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="Mód." property="polModulo" />
					<display:column class="literal" headerClass="cblistaImg" title="P/C" property="polTipoRef" />
					<display:column class="literal" headerClass="cblistaImg" title="Estado" property="polEstado" sortProperty="estadoPoliza" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="Fec.Envío" property="polFechaEnvio" sortProperty="fechaenvio" sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="RC" property="polRc"/>
					<display:column class="literal" headerClass="cblistaImg" title="STR" property="polStr"/>
					<display:column class="literal" headerClass="cblistaImg" title="MOD" property="polMOD"/>
					<display:column class="literal" headerClass="cblistaImg" title="Clase" property="polClase" sortable="false" />
				</display:table>
		</div>
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		
		<%@ include file="/jsp/common/lupas/lupaOficina.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaColectivo.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaModulo.jsp"%>
	</body>
</html>