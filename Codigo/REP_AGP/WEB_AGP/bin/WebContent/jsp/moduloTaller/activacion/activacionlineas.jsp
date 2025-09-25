<!-- 
/*
**************************************************************************************************
*
*  CREACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034  17/07/2010  Ernesto Laura		Página para la consulta y activación de planes    
*
 **************************************************************************************************
*/
-->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>

<jsp:directive.page import="org.displaytag.*" />


<%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", 0);
%>


<html>
	<head>
		<title>Agroplus - Activaci&oacute;n de L&iacute;neas</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" /> 
		
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script src="jsp/js/jquery-v1.4.2.js" type="text/javascript"></script>
		<script src="jsp/js/util.js" type="text/javascript"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
		<script src="jsp/moduloTaller/activacion/activacionlineas.js" type="text/javascript"></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
		<script type="text/javascript" src="jsp/js/calendar.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>	
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<%@ include file="/jsp/js/draggable.jsp"%>	
		<script type="text/javascript">
		
		// Para evitar el cacheo de peticiones al servidor
		/*
	    $(document).ready(function(){
	        var URL = UTIL.antiCacheRand(document.getElementById("main").action);
		    document.getElementById("main").action = URL;    
	    });
	    */
		
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
		        inputField        : "fechaActiv",
		        button            : "btn_fechaActiv",
		        ifFormat          : "%d-%m-%Y",
		        daFormat          : "%d-%m-%Y",
		        align             : "Br"
		   	});	
		});
		function limpiar(){				
				$('#sl_planes').val('');
				$('#sl_lineas').val('smsAviso');
				$('#sl_estado').val('');
				$('#sl_activado').val('');
				var frm = document.getElementById('main');
				frm.target="";
				$('#operacion').val("consultar");
				$('#main').submit();				
			}</script>

	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">	
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuLateralTaller.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	<div id="buttons">
	<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">
					<table cellspacing="2" cellpadding="0" border="0">
						<tbody>
							<tr>
								<td>
									<a class="bot" href="javascript:activacion.detalleTablas('');">Tablas Generales</a>
								</td>
								<td>
									<a class="bot" href="javascript:activacion.consultar();">Consultar</a>
								</td>
								<td>
									<a class="bot" href="javascript:limpiar();">Limpiar</a>
								</td>
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
	<p class="titulopag" align="left">Activaci&oacute;n del Condicionado</p>
	<form action="activacionlineas.html" method="post" name="main" id="main">
	<input type="hidden" id="operacion" name="operacion" value=""/>
	<input type="hidden" name="ROW" id="ROW" value="${activacion.RowHistoricoImp.idhistorico }">
	<input type="hidden" name="lineaSeguroSelect" id="lineaSeguroSelect" value="">
	<input type="hidden" name="esGanado" id="esGanado" value="">	
	<input type="hidden" name="forzarActivar" id="forzarActivar" value="">
	<input type="hidden" name="fechaActiv.day" id="fechaActiv.day" value=""> 
	<input type="hidden" name="fechaActiv.month" id="fechaActiv.month" value=""> 
	<input type="hidden" name="fechaActiv.year" id="fechaActiv.year" value="">
	 
	<%@ include file="/jsp/common/static/avisoErroresActiv.jsp"%>
	
		<div class="panel1 isrt" style="padding:3px;width:97%">
		<fieldset>
		<table style="padding:3px;width:97%">
		  <tr>
		    <td width="10%">			
				<span class="literal">Plan</span>
			</td>
	        <td align="left">
					<select id="sl_planes" name="sl_planes" onchange="common.selectPlan_onchange('sl_planes', 'sl_lineas');" class="dato" style="width:60px">
						<option value="">Todos</option>
		                <c:forEach items="${activacion.planes}" var="planes">
							<option value="${planes}"
								<c:if test="${activacion.filtro !=null && activacion.filtro.linea.codplan == planes}">selected</c:if>>
								${planes}
							</option>
						</c:forEach>
					</select>
			</td>
			</tr>
			<tr>
			<td width="10%">						
			    <span class="literal">L&iacute;nea</span>
			</td>
	        <td align="left">
				    <select id="sl_lineas" name="sl_lineas" class="dato">
				    	<c:if test="${activacion.filtro.linea != null}">
				    	<option value="smsAviso"> -- Seleccione un opci&oacute;n -- </option>
					    <c:forEach items="${sessionScope.listalineas}" var="lineas">
								<option value="${lineas.lineaseguroid}"
					     			<c:if test="${activacion.filtro.linea != null && activacion.filtro.linea.lineaseguroid == lineas.lineaseguroid}">selected</c:if>>
					    			${lineas.codlinea} - ${lineas.nomlinea}	
					    		</option>	
					    </c:forEach>
					    </c:if>
				    </select>
				    <img id="ajaxLoading_lineas" src="jsp/img/ajax-loading.gif" width="16px" 
			         style="cursor:hand;cursor:pointer;display:none" height="11px" />
			</td>
			</tr>
			<tr>
			<td width="10%">		         					         
				<span class="literal">Estado</span>
		    </td>
		    <td align="left">
				<select id="sl_estado" name="sl_estado" class="dato" style="width:100px">
					<option value="">Todos</option>
					<c:forEach items="${activacion.estados}" var="estado">
						<option value="${estado.key}"
							<c:if test="${activacion.filtro !=null && activacion.filtro.estado == estado.key}">selected</c:if>>
							${estado.value}
						</option>
					</c:forEach>
				</select>
			</td>
			</tr>
			<tr>
			<td width="10%">
				<span class="literal">Activado</span>
			</td>
		    <td align="left">
				<select id="sl_activado" name="sl_activado" class="dato" style="width:90px">
				    <option value="">Todos</option>
					<option value="SI" <c:if test="${activacion.filtro !=null && activacion.filtro.activo=='SI'}">selected</c:if>>SI</option>
					<option value="NO" <c:if test="${activacion.filtro !=null && activacion.filtro.activo=='NO'}">selected</c:if>>NO</option>
					<option value="BL" <c:if test="${activacion.filtro !=null && activacion.filtro.activo=='BL'}">selected</c:if>>BLOQUEADO</option>
				</select>
			</td>
			</tr>
			<tr>
			<td width="10%">			
				<span class="literal">Fecha Act.</span>
			</td>
			<td align="left">		    	
		    	<input type="text" onchange="if (!ComprobarFecha(this, document.main, 'Fecha Act.')) this.value='';" id="fechaActiv" size="10" maxlength="10" name="fechaActiv" class="dato" value="<c:if test="${activacion.filtro.fechaactivacion !=null}"><fmt:formatDate pattern="dd/MM/yyyy" value="${activacion.filtro.fechaactivacion}"/></c:if>"/>
		    	<a id="btn_fechaActiv" name="btn_fechaActiv"><img src="jsp/img/calendar.gif"/></a>
		   </td>
		   </tr>
		   </table>
			</fieldset>
		</div>
	</form>	
		<!-- Aqui tiene que ir el grid de datos -->
		
	<div class="centraTabla">	
        <display:table requestURI="activacionlineas.html" excludedParams="operacion" id="listaResultados" class="LISTA" summary="ActivacionLineas" sort="list" pagesize="10" name="${activacion.resultados}" decorator="com.rsi.agp.core.decorators.ModelTableDecoratorActvLineas" style="width:60%">
			<display:column class="accionesDisplayTagColumn" headerClass="cblistaImg" title="Acciones" property="lineaSelec" sortable="false" style="width:130px;text-align:center"/>
            <display:column class="literal" headerClass="cblistaImg" title="Plan" property="lineaPlan" sortable="false" style="width:45px; text-align: center;"/>
            <display:column class="literal" headerClass="cblistaImg" title="L&iacute;nea" property="lineaLinea" sortable="true" style="width:45px; text-align: center;"/>            
            <display:column class="literal" headerClass="cblistaImg" title="Estado importaci&oacute;n" property="lineaImport" sortable="true" style="width:170px;"/>
            <display:column class="literal" headerClass="cblistaImg" title="Activado" property="lineaActivado" sortable="false" style="width:45px; text-align: center;"/>
            <display:column class="literal" headerClass="cblistaImg" title="Fec. Activaci&oacute;n" property="lineaFechaAct"  sortable="true" format="{0,date,dd/MM/yyyy  - HH:mm}" style="width:150px;text-align:center"/>
            <display:column class="literal" headerClass="cblistaImg" title="Bloqueado" property="lineaBloqueado" sortable="false" style="width:45px; text-align: center;"/>            
        </display:table>				
	</div>
	
	</div> 
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	
		<%@ include file="/jsp/common/static/overlay.jsp"%>
	
	<!-- DAA 29/01/13 *** popUp Forzar Activar *** -->
	<div id="divForzarActivar" class="wrapper_popup" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;top: 170px">

			<div id="header-popup" style="padding:0.4em 1em;position:relative; color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px; background:#525583;height:15px">
					<div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
						Listado de Tablas Pendientes de Importar
					</div>
					<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
						      font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
						<span onclick="activacion.cerrarPopUpForzarActivar()">x</span>
					</a>
			</div>
			<div class="panelInformacion_content">
					<div id="tablaInformacion" class="panelInformacion" style="text-align:left">
						<img id="ajaxLoading_tablas" src="jsp/img/ajax-loading.gif" width="16px" style="cursor:hand;cursor:pointer;display:none" height="11px" />
					<input type="hidden" id="idLinSeg" name="idLinSeg" value=""/>	
					</div>	
					<div style="margin-top:15px;clear: both">
						    <a class="bot" href="javascript:activacion.cerrarPopUpForzarActivar()">Cancelar</a>
						    <a class="bot" href="javascript:activacion.forzarActivar()">Forzar Activaci&oacuten</a>
					</div>
			</div>
	</div>
	
	</body>
</html>

