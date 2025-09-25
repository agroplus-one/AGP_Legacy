<!-- 
/*
**************************************************************************************************
*
*  CReACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034              Miguel Granadino  Pantalla Pantallas Configurables
*
 **************************************************************************************************
*/
 -->

<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<jsp:directive.page import="org.displaytag.*" />
<fmt:setBundle basename="displaytag"/>

<html>
<head>
    <title>Agroplus - Pantallas Configurables</title>
    
    <%@ include file="/jsp/common/static/metas.jsp"%>
		
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css"/>
    <link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
    <link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />

	<script type="text/javascript" src="jsp/moduloTaller/pantallasConfigurables/pantallasConfigurables.js" ></script> 
	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
	<script type="text/javascript" src="jsp/js/util.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
	<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	<script type="text/javascript" src="jsp/moduloTaller/pantallasConfigurables/pantallasConfigurablesAux.js" ></script>
		
</head>
	
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="generales.fijarFila()">	
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuLateralTaller.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

	<!-- Buttons -->
	<div id="buttons">
	<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">
					<table cellspacing="2" cellpadding="0" border="0">
						<tbody>
							<tr>
								<td><a class="bot" id="btnAlta" href="javascript:alta();">Alta</a></td> 
								<td><a class="bot" id="btnModificar" href="javascript:modificar();">Modificar</a></td>
							    <td><a class="bot" href="javascript:consultar();">Consultar</a></td>
								<td><a class="bot" href="javascript:limpiar();">Limpiar</a></td>
								<td><a class="bot" id="btnReplicar" href="javascript:pantallasConfig.replicar();">Replicar</a></td>
							</tr>
						</tbody>
					</table>
					<!-- FIN TABLA BARRA DE BOTONES-->
				</td>
			</tr>
	</table>
	</div>
		
	<!-- Contenido de la página -->
	<div class="conten" style="padding:3px;width:97%">
	        <p class="titulopag" align="left">Configuraci&oacute;n de Pantallas</p>
		    <form:form id="main" name="main" method="POST" action="pantallasConfigurables.html" commandName="PantallaConfigurableBean">
		    	<input type="hidden" name="operacion" id="operacion" value="" />
		    	<form:hidden path="idpantallaconfigurable" id="id"/>	
		    	<input type="hidden" name="ROW" id="ROW">		    		
                <input type="hidden" name="PAGINA" id="PAGINA" value="pantallasConfigurables.jsp">
                <input type="hidden" name="idRowModificar" value="${id}">
                <input type="hidden" name="idRowConfigurar" value="${id}">
                <input type="hidden" name="origenLlamada" id="origenLlamada" />
                <input type="hidden" name="replicarOK" id="replicarOK" />
                <%@ include file="/jsp/common/static/avisoErrores.jsp"%>
		        
		        <!-- PANEL 1 -->
		        <div class="panel1 isrt" style="width:auto;">
		        	<table width="80%" align="center" style="margin:0 auto;">
						<tr align="left">
							<td class="literal">Plan</td>
							<td>
								<form:input path="linea.codplan" size="4" maxlength="4" cssClass="dato consulta-group" id="sl_planes" onchange="javascript:lupas.limpiarCampos('sl_lineas', 'desc_linea');" /> 
								<label class="campoObligatorio" id="campoObligatorio_sl_planes"	title="Campo obligatorio"> *</label>
							</td>
							<td class="literal">Línea</td>
							<td>
								<form:input path="linea.codlinea" size="3"	maxlength="3" cssClass="dato consulta-group" id="sl_lineas" onchange="javascript:lupas.limpiarCampos('desc_linea');" />
								<form:input path="linea.nomlinea" cssClass="dato" id="desc_linea" size="40" readonly="true"/>
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('LineaConfigurables','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />
								<label class="campoObligatorio"	id="campoObligatorio_sl_lineas" title="Campo obligatorio"> *</label>
							</td>
							
							
					   		<td class="literal">Pantalla</td>
					    	<td align="left"> 					    
							    <form:select id="sl_pantallas" path="pantalla.idpantalla" cssClass="dato consulta-group">
							    	<form:option value="">-- Seleccione una opción --</form:option>
							    	<c:forEach items="${listPantallas }" var="pantalla">
							    		<form:option value="${pantalla.idpantalla }">${pantalla.descpantalla }</form:option>
							    	</c:forEach>
							    </form:select>
							    <label class="campoObligatorio" id="campoObligatorio_sl_pantallas" title="Campo obligatorio"> *</label>
						    </td>
						</tr>
					</table>
				</div>					
			</form:form>
			
			<c:if test="${tablaConfigurables != 'vacio'}">
				<div class="grid" style="">
					<display:table requestURI="pantallasConfigurables.html" id="listPantallasConfig" class="LISTA" summary="pantallasConfigurables" 
					sort="list" pagesize="10" name="${listPantallasConfigurables}" decorator="com.rsi.agp.core.decorators.ModelTableDecoratorPantallasConfigurables"  
					excludedParams="operacion" style="width:50%;">
					        <display:column class="literal" headerClass="cblistaImg" title="Acciones" property="admActions" sortable="false" style="width:70px;text-align:center"/>
					        <display:column class="literal" headerClass="cblistaImg" title="Plan" property="admPlanes" sortable="false" style="width:45px;"/>
					        <display:column class="literal" headerClass="cblistaImg" title="L&iacute;nea" property="admLineas" sortable="false" style="width:45px;"/>
		                    <display:column class="literal" headerClass="cblistaImg" title="Pantalla" property="admPantalla" sortable="true" />	
		            </display:table>		    
	    		 </div>
	    	</c:if>
	    		 
     </div>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
 	<%@ include file="/jsp/moduloTaller/pantallasConfigurables/replicarPlanLinea.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaLineaConfigurables.jsp"%>
	
	</body>
	
</html>
