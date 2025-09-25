<%@ page buffer="100kb"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<fmt:bundle basename="displaytag">
	<c:set var="numReg">
		<fmt:message key="numElementsPag" />
	</c:set>
</fmt:bundle>

<fmt:bundle basename="agp">
	<c:set var="numRegImpresion">
		<fmt:message key="impresionnumReg" />
	</c:set>
</fmt:bundle>

<html>
<head>
<title>Histórico de Parámetros Generales de Comisiones</title>

<%@ include file="/jsp/common/static/metas.jsp"%>

<!-- Estilos -->
<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />

<!-- JavaScript,jQery & AJAX -->
<script type="text/javascript" src="jsp/js/util.js"></script>
<script type="text/javascript" src="jsp/js/menuapli.js"></script>
<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js"></script>
<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
<script type="text/javascript" src="jsp/js/calendar.js"></script>
<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
<script type="text/javascript" src="jsp/js/displayTagAjaxSelectedRow.js"></script>
<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
<%@ include file="/jsp/js/draggable.jsp"%>

<script type="text/javascript">

			$(document).ready(function() {
			
				 // Para evitar el cacheo de peticiones al servidor
                 var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
			     document.getElementById("main3").action = URL;
		      	
	      	});
  
		</script>

<script type="text/javascript">
		
		function volver(){
			$('#volver').submit();	
		}			

</script>
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0" onload="SwitchSubMenu('sub6', 'sub5');"generales.fijarFila()">

<%@ include file="/jsp/common/static/cabecera.jsp"%>
<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

<div id="buttons">
<table width="97%" cellspacing="0" cellpadding="0" border="0">
	<tr>
		<td align="right">
			<a class="bot" id="btnVolver" href="javascript:volver()">Volver</a>
		</td>
	</tr>
</table>
</div>
<!-- Contenido de la pagina -->

<div class="conten" style="padding: 3px; width: 97%">
<p class="titulopag" align="left">Histórico de Parámetros generales de comisiones</p>
<form:form name="main3" id="main3" action="historicoColectivo.html"
	method="post" commandName="cultEntHistoricoBean">
	<form:hidden path="id" id="id" />
	<input type="hidden" name="lineaseguroid" />
	<input type="hidden" name="method" id="method" />
	<input type="hidden" name="fechaIni.day" value="">
	<input type="hidden" name="fechaIni.month" value="">
	<input type="hidden" name="fechaIni.year" value="">
	<input type="hidden" name="fechaFin.day" value="">
	<input type="hidden" name="fechaFin.month" value="">
	<input type="hidden" name="fechaFin.year" value="">
	<input type="hidden" name="entidadSubstr" id="entidadSubstr" value="">
	<input type="hidden" name="grupoEntidades" id="grupoEntidades" value="" />
	<input type="hidden" name="comMaxP" value="">
	

	<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
	       <div style="panel2 isrt">
				<fieldset style="width:95%;margin:0 auto;" align="center">
					<legend class="literal">Datos del parámetro</legend>
					<table  align="center" border=0>
						<tr>
							<td class="literal">Plan:</td>
								<td width="35px" class="detalI">${plan}</td>
							<td class="literal">Línea:</td>
								<td class="detalI">${linea} - ${desc_linea}</td>	
								
							<td class="literal">E-S Med.:</td>
								<td class="detalI">${esMed}</td>
						</tr>						
					</table>
					
					<table  align="center" border=0>
						<tr>
							<td class="literal">Grupo Negocio:</td>
								<td width="35px" class="detalI">${grupoNegDesc}</td>	
								
							<td class="literal">% Comisión máximo:</td>
							  
								<td width="35px" class="detalI"><fmt:formatNumber type="number" pattern="0.00" maxFractionDigits="2" value="${comMaximo}" /><c:if test="${not empty comMaximo }">%</c:if>
								</td>		
						
							<td class="literal" width="20px"> </td>
							<td class="literal">% Administración:</td>				
							
								<td width="35px" class="detalI"><fmt:formatNumber type="number" pattern="0.00" maxFractionDigits="2" value="${pctAdministracion}" /><c:if test="${not empty pctAdministracion }">%</c:if>
								</td>							
							</td>
							<td class="literal" width="20px"> </td>
							<td class="literal">% Adquisición:</td>				
								<td width="35px" class="detalI"><fmt:formatNumber type="number" pattern="0.00" maxFractionDigits="2" value="${pctadquisicion}" /><c:if test="${not empty pctadquisicion }">%</c:if>
								</td>							
							</td>
							<td class="literal" width="20px"> </td>
							<td class="literal">Fecha efecto:</td>
							<td>
								<td width="35px" class="detalI">${fecEfecto}</td>			
							</td>
			
				</tr>
					</table>
				</fieldset>
			</div>	
		
		</form>
	
	
	
</form:form> <br />

<form name="volver" id="volver" action="comisionesCultivos.html"><input
	type="hidden" name="method" id="method" value="doConsultaParam"/><input
	type="hidden" name="procedencia" id="procedencia" value="historicoComisiones"/>
</form>

<!-- Aqui tiene que ir el grid de datos -->
	<div id="grid">
		<display:table requestURI="" class="LISTA" summary="comisiones" defaultsort="8" defaultorder="ascending"
				pagesize="${numReg}" name="${listHisCe}" id="comisiones" sort="list"
				decorator="com.rsi.agp.core.decorators.ModelTableDecoratorHisComisionesCultivosEntidades" 
				style="width:97%;border-collapse:collapse;">

				<display:column class="literal" headerClass="cblistaImg" title="Plan"   				property="plan"              	style="text-align:center" sortable="true"/>
				<display:column class="literal" headerClass="cblistaImg" title="Línea"            		property="linea"  		     	style="text-align:center" sortable="true"/>
				<display:column class="literal" headerClass="cblistaImg" title="E-S Med."            	property="entSubEntMed"  		style="text-align:center" sortProperty="subentidadMediadora.id.codentidad" sortable="true" />
					<display:column class="literal" headerClass="cblistaImg" title="G.N."            	property="grupoNegocio"  		sortable="true" style="width:40px;text-align:center"/>
				<display:column class="literal" headerClass="cblistaImg" title="% Comisión máximo"  	property="pctgeneralentidad" 	style="text-align:right" sortable="true" comparator="com.rsi.agp.core.comparators.TableComparatorPorcentajes"/>
				<display:column class="literal" headerClass="cblistaImg" title="% Administracion"       property="pctadministracion"	style="text-align:right" sortable="true"/>
				<display:column class="literal" headerClass="cblistaImg" title="% Adquisicion"          property="pctadquisicion"    	style="text-align:right" sortable="true"/>
				<display:column class="literal" headerClass="cblistaImg" title="Fec. Efecto"            property="fechaEfecto" 		 	style=";text-align:center" sortable="true" format="{0,date,dd/MM/yyyy}"/>
				
				<display:column class="literal" headerClass="cblistaImg" title="Tipo mov."              property="accion" 		     	style="text-align:center" sortable="true"/>
				<display:column class="literal" headerClass="cblistaImg" title="Fec. mov."              property="fechamodificacion" 	style="text-align:center" sortable="true" format="{0,date,dd/MM/yyyy HH:mm:ss}"/>
				<display:column class="literal" headerClass="cblistaImg" title="Usuario"                property="usuario.codusuario" 	style="text-align:center" sortable="true"/>
		
		</display:table>
	</div>
</div>

<%@ include file="/jsp/common/static/piePagina.jsp"%>
<%@ include file="/jsp/common/static/overlay.jsp"%>

</body>
</html>