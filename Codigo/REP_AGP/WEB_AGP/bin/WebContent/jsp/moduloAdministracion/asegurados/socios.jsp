<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>


<fmt:bundle basename="displaytag" >
	<c:set var="numReg"><fmt:message key="numElementsPag"/></c:set>
</fmt:bundle>

<fmt:bundle basename="agp" >
	<c:set var="numEle"><fmt:message key="visores.numElements"/></c:set>
	<c:set var="codIndicadorRegimen"><fmt:message key="codIndicadorRegimen"/></c:set>
	<c:set var="listaCodRegimen" value='${fn:split(codIndicadorRegimen,",") }' />
	<c:set var="descIndicadorRegimen"><fmt:message key="descIndicadorRegimen"/></c:set>
	<c:set var="listaDesRegimen" value='${fn:split(descIndicadorRegimen,",") }' />
</fmt:bundle>


<html>
	<head>
		<title>Datos Socios</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />


		<script type="text/javascript" src="jsp/js/util.js"></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>	
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/displayTagAjaxSelectedRow.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
		<script type="text/javascript" src="jsp/moduloAdministracion/asegurados/socios.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
		
	<script type="text/javascript">
		</script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0"
			onload="SwitchMenu('sub2')">

		<%@ include file="/jsp/common/static/cabecera.jsp"%>		
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>		
		
		
		<c:if test="${not empty asegurado }">
			<table style="border:1px solid black" width="100%"><TR>
				<c:if test="${asegurado.tipoidentificacion eq 'CIF' }">
					<td class="literal">Asegurado: ${asegurado.razonsocial }</td>
				    <td class="literal">CIF: ${asegurado.nifcif }</td>	
				</c:if>
				<c:if test="${asegurado.tipoidentificacion eq 'NIF' }">
					<td class="literal">Asegurado: ${asegurado.nombre} ${asegurado.apellido1} ${asegurado.apellido2 }</td>
				    <td class="literal">NIF: ${asegurado.nifcif }</td>	
				</c:if>	
				<c:if test="${asegurado.tipoidentificacion eq 'NIE' }">
					<td class="literal">Asegurado: ${asegurado.nombre} ${asegurado.apellido1} ${asegurado.apellido2 }</td>
				    <td class="literal">NIE: ${asegurado.nifcif }</td>	
				</c:if>				
				</TR>			
			</table>
		</c:if>	
		
		<div id="buttons">
			<table width="98%" cellspacing="0" cellpadding="2">
				<tr>
					<td align="left" width="33%">
						<a class="bot" id="btnVolver" href="javascript:document.getElementById('aseguradoForm').submit()" 
						onclick="javascript:document.aseguradoForm.idAsegurado.value =document.main.idAsegurado.value; ">Volver</a>
					</td>
					<td align="center" width="33%">
						<a class="bot" id="btnImprimir" href="javascript:imprimir()">Imprimir</a>
					</td>
					<td align="right" width="33%">
						<a class="bot" id="btnAlta" href="javascript:doAlta()">Alta</a>
						<a class="bot" id="btnModificar" style="display:none" href="javascript:doModificar()">Modificar</a>
						<a class="bot" id="btnConsultar" href="javascript:doConsulta()">Consultar</a>
						<a class="bot" id="btnLimpiar" href="javascript:doLimpiar()">Limpiar</a>
					</td>
				</tr>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%">
			<p class="titulopag" align="left">Datos de Socios</p>		
			
			
			<form:form name="main" id="main" action="socio.html" method="post" commandName="socioBean" >
				<input type="hidden" name="method" id="method" />	
				<form:hidden path="id.idasegurado" id="idAsegurado" />		
				
				<input type="hidden" name="idAseguradoBaja" id="idAseguradoBaja"/>
				<input type="hidden" name="nifcifBaja" id="nifcifBaja"/>
				<input type="hidden" name="displayPopUpPolizas" id="displayPopUpPolizas" value="${popUpPolizas}" />
				<input type="hidden" name="origenLlamada" id="origenLlamada" value="${origenLlamada}" />
				<input type="hidden" name="idSocio" id="idSocio" value="${idSocio}" />
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div class="panel2 isrt">
					<table width="100%" >
						<tr>
							<td class="literal" style="width: 85px;">Tipo ident.</td>
							<td class="literal" >
								<form:select path="tipoidentificacion" cssClass="dato" id="tipoIdentificacion" cssStyle="width:70"
										onchange="generales.cifnifSeleccionado()" tabindex="1">
									<form:option value="">Todos</form:option>
									<form:option value="CIF">CIF</form:option>
									<form:option value="NIF">NIF</form:option>
									<form:option value="NIE">NIE</form:option>
								</form:select>
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_tipoidentificacion"> *</label>
							</td>
							<td class="literal" style="width:110px;" >NIF/CIF/NIE Socio</td>
							<td>
								<form:input path="id.nif" size="10" maxlength="9" cssClass="dato" id="nifcif" tabindex="2" onchange="this.value=this.value.toUpperCase();this.value=generales.lpadNIF(this.value);"/>
							</td>
						</tr>
						<tr>
							<td class="literal" >Nombre</td>
							<td class="literal">
								<form:input path="nombre" size="21" maxlength="20" cssClass="dato" id="nombre" tabindex="3" onchange="this.value=this.value.toUpperCase();"/>
								<label class="campoObligatorio" id="campoObligatorio_nombre" title="Campo obligatorio"> *</label>
							</td>
							<td class="literal">1<sup style="vertical-align:'super';">er</sup> Apellido</td>
							<td>
								<form:input path="apellido1" size="41" maxlength="40" cssClass="dato" id="apellido1" tabindex="4" onchange="this.value=this.value.toUpperCase();"/>
								<label class="campoObligatorio" id="campoObligatorio_apellido1" title="Campo obligatorio"> *</label>
							</td>
							<td class="literal">2º Apellido</td>
							<td align="left">
								<form:input path="apellido2" size="41" maxlength="40" cssClass="dato" id="apellido2" tabindex="5" onchange="this.value=this.value.toUpperCase();"/>
								<label class="campoObligatorio" id="campoObligatorio_apellido2" title="Campo obligatorio"> *</label>
							</td>
						</tr>
						
						
					</table>
					<table width="100%" border="2">
						<tr>
							<td class="literal" style="width:85px;">Raz&oacute;n social</td>
							<td class="literal">
								<form:input path="razonsocial" size="51" maxlength="50" cssClass="dato" id="razonsocial" tabindex="6" onchange="this.value=this.value.toUpperCase();"/>
								<label class="campoObligatorio" id="campoObligatorio_razonsocial" title="Campo obligatorio"> *</label>
							</td>
						</tr>
					</table>
				</div>
				<div class="panel2 isrt">
					<table width="100%">
						<tr align="left">
							<td class="literal">Nº Seguridad S.S.</td>
							<td>
								<form:input path="numsegsocial" size="13" maxlength="12" cssClass="dato" id="numsegsocial" tabindex="7"/>
							</td>
							<td class="literal">Ind. R&eacute;gimen</td>
							<td class="literal">
								<form:select path="regimensegsocial" cssClass="dato" id="regimensegsocial" cssStyle="width:120" tabindex="8">
									<form:option value="">Todos</form:option>
									<c:forEach var="i" begin="0" end="${fn:length(listaCodRegimen) - 1 }">
										<form:option value="${listaCodRegimen[i] }">${listaDesRegimen[i] }</form:option>
									</c:forEach>
								</form:select>
								<label class="campoObligatorio" id="campoObligatorio_regimensegsocial" title="Campo obligatorio"> *</label>
							</td>
							
							<td class="literal">ATP
								
								<form:select path="atp" cssClass="dato" id="atp" tabindex="9" cssStyle="width:70">
									<form:option value="">Todos</form:option>
									<form:option value="S">SI</form:option>
									<form:option value="N">NO</form:option>
								</form:select>
								<label class="campoObligatorio" id="campoObligatorio_atp" title="Campo obligatorio"> *</label>
							</td>
							<td class="literal">J. Agricultor/A
								<form:select path="jovenagricultor" cssClass="dato" id="jovenagricultor" cssStyle="width:70" tabindex="10">
									<form:option value="">Todos</form:option>
									<form:option value="S">SI</form:option>
									<form:option value="N">NO</form:option>
								</form:select>
								<label class="campoObligatorio" id="campoObligatorio_jovenagricultor" title="Campo obligatorio"> *</label>
							</td>
						</tr>
					</table>
				</div>	
				</form:form>		
			<br />
			<div class="grid" style="">
				<!-- <input type="hidden" name="idAsegurado" value="${idAsegurado }" /> -->
				<!-- Aqui tiene que ir el grid de datos -->
					<display:table requestURI="" id="listaSocios" class="LISTA" summary="socio" sort="list" defaultsort="0" defaultorder="ascending"
					pagesize="${numReg}" name="${listaSocios}" export="false" decorator="com.rsi.agp.core.decorators.ModelTableDecoratorSocios" style="width:95%">
						<display:column class="literal" headerClass="cblistaImg" title="Acciones" property="socioSelec" sortable="false" style="width:50px;text-align:center"/>
						<display:column class="literal" headerClass="cblistaImg" title="Nombre" property="socioNombre" sortable="true" style="width:250px;text-align:left"/>
						<display:column class="literal" headerClass="cblistaImg" title="NIF/CIF/NIE" property="socioCif" sortable="true" style="width:80px;text-align:left"/>		
						<display:column class="literal" headerClass="cblistaImg" title="Nº S.S." property="socioSS" sortable="true" style="width:100px;text-align:left"/>         		   
						<display:column class="literal" headerClass="cblistaImg" title="Ind. régimen" property="socioRegimen" sortable="true" style="width:120px;text-align:left"/>		
						<display:column class="literal" headerClass="cblistaImg" title="ATP" property="socioATP" sortable="true" style="width:40px;text-align:left"/>				            
						<display:column class="literal" headerClass="cblistaImg" title="J.Agricultor/a" property="socioJAgr" sortable="true" style="width:90px;text-align:left"/>				            
						
						<display:column class="literal" headerClass="cblistaImg" title="Baja" property="baja" sortable="true" style="width:40px;text-align:left"/>/>						
						<display:setProperty name="export.pdf" value="true"/>
						<display:setProperty name="export.pdf.filename" value="exportar.pdf" />
					</display:table>
					
			</div>			
			<form name="aseguradoForm" id="aseguradoForm" action="asegurado.html">
				<input type="hidden" id="method" name="method" value="doConsulta" />
				<input type="hidden" id="idAsegurado" name="idAsegurado" value="${idAsegurado}" />
			</form>
			
			<br />	
		</div>
		
		<%@ include file="/jsp/common/static/piePagina.jsp"%>		
		<%@ include file="/jsp/common/static/overlay.jsp"%>	
				
		<!-- *** popUp ámbito contratación *** -->
		<div id="popUpPolizasSinGrabar" class="parcelasRepWindow" style="color:#333333;width:700px;max-height:400px; 
            height:expression(this.scrollHeight > 400? '400px' : 'auto' );overflow:auto;left:300px;padding:3px">
            
	            <!-- header -->
	            <div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;background:#525583;height:15px">
					        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
					            Aviso
					        </div>
					        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
					                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
					            <span onclick="closePopUpPolizasSinGrabar()">x</span>
					        </a>
				 </div>
				 <br />
				 
				<!-- body -->
				<div style="">
			       ${tableInfoPolizasSinGrabar}
	            </div>
	            
	            <!-- buttons -->
	            <div style="margin-top:10px;text-align:center;">
	    			<a href="javascript:closePopUpPolizasSinGrabar();" class="bot">Cerrar</a>
	    			<c:if test="${origenLlamada == 'doAlta'}">
	    				<a href="javascript:doAlta()" class="bot">Alta</a>
	    			</c:if>
	    			<c:if test="${origenLlamada == 'doBaja'}">
	    				<a href="javascript:bajaSociosConPolizas(${idAseguradoBaja}, '${nifcifBaja}')" class="bot">Eliminar</a>
	    			</c:if>
	    			<c:if test="${origenLlamada == 'deshacerSocio'}">
	    				<a href="javascript:deshacerSocio(${idAseguradoBaja}, '${nifcifBaja}')" class="bot">Recuperar</a>
	    			</c:if>	    				    			
	           </div>   
          </div>          
             		
	</body>	
	
</html>