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
	<title>Histórico de Descuentos por Oficina/E-S Mediadora</title>
	
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
		<p class="titulopag" align="left">Histórico de Descuentos por Oficina/E-S Mediadora</p>
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
		
		<form:form id="main3" name="main3" method="post" action="mtoDescuentos.run" commandName="descuentosBean">
			<input type="hidden" name="method" id="method" value="doConsultarHistorico"/>
			<input type="hidden" name="operacion" id="operacion"/>
			<form:hidden path="id" id="id"/>
			<input type="hidden" name="limpiarFiltro" id="limpiarFiltro"/>
			<div style="panel2 isrt">
				<fieldset>
					<table style="width:100%;">
						<tr align="center">
							<td class="literal" align="right">Entidad</td>
							<td class="detalI" id="celdaEntMed" colspan="3">
								${entidadH} - ${nomEntidadH}
							</td>
							<td class="literal" align="right">Oficina</td>
							<td width="150px" class="detalI" id="celdalSubMed">
								<c:if test="${oficinaH != '00-1'}">
									${oficinaH} - ${nomOficinaH} 
								</c:if>
								<c:if test="${oficinaH == '00-1'}">
									&nbsp
								</c:if> 			
							</td>
							<td class="literal" align="right">E-S Med.</td>
							<td width="40px" class="detalI" id="celdalSubMed">
								${entmediadoraH} - ${subentmediadoraH} 
							</td>
							<td class="literal" align="right">Delegación</td>
							<td width="15px" class="detalI" id="celdaPlan">
								${delegacionH}
							</td>							
						</tr>
						<tr align="center">
							<td class="literal" align="right">Plan</td>
							<td width="15px" class="detalI" id="celdaCodPlan">
								${planH}
							</td>
							<td class="literal">Línea</td>
							<td width="135px" class="detalI" id="celdaCodLinea">
								${lineaH} - ${desc_lineaH}
							</td>
							<td class="literal" align="right">% Descuento Máximo</td>
							<td width="150px" class="detalI" id="celdarga">
								<fmt:formatNumber type="number" pattern="0.00" maxFractionDigits="2" value="${pctDescMaxH}" />%							
							</td>
							<td class="literal" align="right">¿Permitir recargos?</td>
							<td width="20px" class="detalI" id="celdaPermitirRecargos">
								${permitirRecargoTxt}
							</td>
							<td class="literal" align="right">¿Ver comisiones?</td>
							<td width="70px" align="left" class="detalI" id="celdaverComisiones">
								${verComisionesTxt}
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
		</form:form>
		<div id="grid">
			<display:table requestURI="" class="LISTA" summary="listHistorico" 
					pagesize="${numReg}" name="${listadoHistorico}" id="listHistorico" 
					defaultsort="9" defaultorder="ascending"
					decorator="com.rsi.agp.core.decorators.ModelTableDecoratorMtoDescuentosHistorico" 	sort="list"
					style="width:95%;border-collapse:collapse;">
					
					<display:column class="literal" headerClass="cblistaImg" title="Entidad"   	     property="entidad"      style="text-align:center;width:60px" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="Nombre Entidad"  property="nomEntidad"   style="text-align:center;width:120px" sortable="true"><c:out value="${nomEntidadH}"/></display:column>
					<display:column class="literal" headerClass="cblistaImg" title="Oficina"   	     property="oficina"		 style="text-align:center;width:60px" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="Nombre Oficina"  property="nomOficina"   style="text-align:center;width:120px" sortable="true"> <c:out value="${nomOficinaH}"/></display:column>
					<display:column class="literal" headerClass="cblistaImg" title="E-S Med."   	 property="esMed"        style="text-align:center;width:65px" sortable="true" comparator="com.rsi.agp.core.comparators.TableComparatorHojaNumero"/>
					<display:column class="literal" headerClass="cblistaImg" title="Delegación"      property="delegacion"   style="text-align:center;width:80px" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="Plan"   	     property="plan"		 style="text-align:center;width:50px" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="Línea"   	     property="linea"		 style="text-align:center;width:50px" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="% Dto. Máx."     property="pctMaximo"    style="text-align:center;width:90px" sortable="true" comparator="com.rsi.agp.core.comparators.TableComparatorPorcentajes"/>
					<display:column class="literal" headerClass="cblistaImg" title="Recargo"   	     property="permitirRecargo"		 style="text-align:center;width:65px" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="Ver comis."   	 property="verComisiones"		 style="text-align:center;width:60px" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="Tipo mov."       property="tipoMov" 	 style="text-align:center" sortable="true" />
					<display:column class="literal" headerClass="cblistaImg" title="Fecha mov."      property="fechaMov" 	 style="text-align:center" sortable="true" format="{0,date,dd/MM/yyyy HH:mm:ss}"/>
					<display:column class="literal" headerClass="cblistaImg" title="Usuario"      	 property="usuario"      style="text-align:center;width:60px" sortable="true" />
			</display:table>
		</div>
	</div>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
</body>
</html>