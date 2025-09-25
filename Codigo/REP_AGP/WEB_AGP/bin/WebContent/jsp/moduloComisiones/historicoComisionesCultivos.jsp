<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<fmt:bundle basename="displaytag" >
	<c:set var="numReg">
		<fmt:message key="numElementsPag"/>
	</c:set>
</fmt:bundle>
<html>
<head>
	<title>Histórico de Comisiones por E-S Mediadora</title>
	
	 <%@ include file="/jsp/common/static/metas.jsp"%>
	 
	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
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
	<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
    <script type="text/javascript" src="jsp/js/calendar.js"></script>
    <script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
    <script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
	<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
		
	<script type="text/javascript">
	function volver (){
		$('#method').val ("doConsulta");
		$('#limpiarFiltro').val ("false");
		$('#main3').submit();
	}
	
	</script>
	
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchSubMenu('sub6', 'sub5');">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr align="center">
				<td align="right">			
					<a class="bot" id="btnVovler" href="javascript:volver();">Volver</a>				
				</td>
			</tr>
		</table>
	</div>
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Histórico de comisiones por E-S Mediadora</p>
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
		
		<form:form id="main3" name="main3" method="post" action="comisionesCultivos.html" commandName="cultivosSubentidadesBean">
			<input type="hidden" name="method" id="method" value="doConsultarHistorico"/>
			<input type="hidden" name="operacion" id="operacion"/>
			<input type="hidden" name="idCultivosEntidades" id="idCultivosEntidades" value="${cultivosEntidades.id }"/>
			<form:hidden path="id" id="id"/>
			<form:hidden path="usuario.codusuario" id="usuario"/>
			<input type="hidden" name="limpiarFiltro" id="limpiarFiltro"/>
			<div style="panel2 isrt">
				<fieldset style="width:95%;margin:0 auto;">
					<legend class="literal">Distribución del Mediador</legend>
					<table style="width:95%;">
						<tr align="center">
							<td class="literal">Entidad Mediadora</td>
							<td width="10px" class="detalI" id="celdaEntMed">
								${entmediadoraH}
							</td>
							<td class="literal">Subentidad Mediadora</td>
							<td width="230px" class="detalI" id="celdalSubMed">
								${subentmediadoraH} - ${desc_subentmediadoraH} 
							</td>
							<td class="literal">Plan</td>
							<td width="20px" class="detalI" id="celdaPlan">
								${planH}
							</td>
							<td class="literal">Línea</td>
							<td width="20px" class="detalI" id="celdarga">
								${lineaH}
							</td>
							<td class="literal">% E-S Mediadora</td>
							<td width="20px" class="detalI" id="celdapctMed">
								${txt_porcentajeMediadorH}
							</td>
						</tr>
					</table>
						
						
				</fieldset>
			</div>
		</form:form>
		<div id="grid">
			<display:table requestURI="" class="LISTA" summary="listHistorico" 
					pagesize="${numReg}" name="${listadoHistorico}" id="listHistorico" 
					decorator="com.rsi.agp.core.decorators.ModelTableDecoratorComisionesCultivoMediadoresHistorico" 	sort="list"
					defaultsort="9" defaultorder="ascending"
					style="width:90%;border-collapse:collapse;">
					
					<display:column class="literal" headerClass="cblistaImg" title="Entidad Med."   	property="entidad"       style="text-align:center" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="Subentidad Med."   	property="subentidad"    style="text-align:center" sortable="true" comparator="com.rsi.agp.core.comparators.TableComparatorSubEntidadMediadora"/>
					<display:column class="literal" headerClass="cblistaImg" title="Plan"   			property="plan"          style="text-align:center" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="Línea"              property="linea"         style="text-align:center" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="% Entidades"  		property="pctent"    	 style="text-align:center" sortable="true" comparator="com.rsi.agp.core.comparators.TableComparatorPorcentajes"/>
					<display:column class="literal" headerClass="cblistaImg" title="% E-S Mediadora"    property="pctmed"    	 style="text-align:center" sortable="true" comparator="com.rsi.agp.core.comparators.TableComparatorPorcentajes"/>
					<display:column class="literal" headerClass="cblistaImg" title="Fec. Efecto"      	property="fechaEfecto" 	 style="text-align:center" sortable="true" format="{0,date,dd/MM/yyyy}"/>
					<display:column class="literal" headerClass="cblistaImg" title="Tipo mov."      	property="tipoMov" 		 style="text-align:center" sortable="true" />
					<display:column class="literal" headerClass="cblistaImg" title="Fecha mov."      	property="fechaMov" 	 style="text-align:center" sortable="true" format="{0,date,dd/MM/yyyy HH:mm:ss}"/>
					<display:column class="literal" headerClass="cblistaImg" title="Usuario"      		property="usuario"       style="text-align:center" sortable="true" />
			</display:table>
		</div>
	</div>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
	<!-- panel avisos -->
	<div id="divAviso" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
       <!--  header popup -->
		<div id="header-popup" style="padding:0.4em 1em;position:relative;
		                                  color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;
		                                  background:#525583;height:15px">
		        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
		            Aviso
		        </div>
		        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
		                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
		            <span onclick="cerrarPopUp()">x</span>
		        </a>
		</div>
		<!--  body popup -->
		<div class="panelInformacion_content">
				<div id="panelInformacion" class="panelInformacion">
					<div id="txt_info" style="width: 70%" >Por cada plan,debe introducir una distribución para la línea genérica 999.</div>
				</div>
		</div>
	</div>
	
</body>
</html>