<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
<head>
	<title>Agroplus - Detalle Comisión Aplicación</title>
		
        <%@ include file="/jsp/common/static/metas.jsp"%>
		
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
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
		
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.filestyle.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.progressbar.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.progressbar.min.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/ajaxfileupload.js"></script>	
		<%@ include file="/jsp/js/draggable.jsp"%>
		<script type="text/javascript">
			$(document).ready(function(){
	       		$("#divDatosComunes").css("display",'none');
	       		<c:if test="${muestraDiv eq 'true'}">
	       			$("#divDatosComunes").css("display",'');
	       		</c:if>
	       	});
		
	        function onInvokeAction(id) {
				var to=document.getElementById("adviceFilter");
				to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
			    $.jmesa.setExportToLimit(id, '');
			    var parameterString = $.jmesa.createParameterStringForLimit(id);
			    var frm = document.getElementById('main1');
			    $.get('${pageContext.request.contextPath}/ficheroComisionApli.run?ajax=true&idComision='+$("#idComision").val() +'&idFichero='+$("#idFichero").val()+'&' + parameterString, function(data) {
			        $("#grid").html(data)
		  			});
			}
		    
		   
		    function volver(){
		    	var frm = document.getElementById('main1');
		    	frm.method.value="doVolver";
		    	$('#main1').submit();
		    }
		    
		    function detalle(idComision,idComisionApli){
		    	var frm = document.getElementById('main1');
		    	frm.method.value="doDetalle";
		    	frm.idComision.value = idComision;
		    	frm.idComisionApli.value = idComisionApli;
		    	$('#main1').submit();
		    }
		  
		    
		</script>
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0"  onload="SwitchSubMenu('sub7', 'sub5');">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		<!-- botones de la página -->
		<div id="buttons" >
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tbody>
					<tr>
						<td align="right">
							<a class="bot" id="btnSalir" href="javascript:volver();">Volver</a>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%" >
			<p class="titulopag" align="left">Detalle Fichero Comisiones Aplicación</p>			
			
			
			<form:form name="main1" id="main1" action="ficheroComisionApli.run" method="post"  commandName="comisionApliBean">
				<input type="hidden" name="method" id="method" />	
				<input type="hidden" name="idFichero" id ="idFichero" value="${idFichero}"/>
				<input type="hidden" name="nombreFichero" id ="nombreFichero" value="${nombreFichero}"/>
				<input type="hidden" name="estado" id ="estado" value="${estado}"/>
				<input type="hidden" name="fase" id ="fase" value="${fase}"/>
				<input type="hidden" name="idComision" id ="idComision"/>
				<input type="hidden" name="idComisionApli" id ="idComisionApli"/>
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				
				<div class="panel2 isrt" style="width: 95%;" id="divDatosComunes">
							
					<fieldset>
					<legend class="literal">Datos Recibo</legend>
					<table>
						<c:choose>
						<c:when test="${comisionApliBean.comision.colectivoreferencia != null}">
							<tr>
								<td class="literal">Referencia colectivo:</td>		<td width="80px" class="detalI">${comisionApliBean.comision.colectivoreferencia}</td>
								<td class="literal">Dígito control:</td>			<td width="50px" class="detalI">${comisionApliBean.comision.colectivodc}</td>
								<td class="literal">Código interno Colectivo:</td>		<td width="50px" class="detalI">${comisionApliBean.comision.colectivocodinterno}</td>
							</tr>
						</c:when>
						<c:otherwise>
							<tr>
								<td class="literal">Pol. referencia :</td>		<td width="50px" class="detalI">${comisionApliBean.comision.individualreferencia}</td>
								<td class="literal">Pol. DC:</td>				<td width="25px" class="detalI">${comisionApliBean.comision.individualdc}</td>
								<td class="literal">Pol. Tipo referencia</td>	<td width="25px" class="detalI">${comisionApliBean.comision.individualtiporef}</td>
								<td class="literal">Pol. Código interno:</td>	<td width="50px" class="detalI">${comisionApliBean.comision.individualcodinterno}</td>
								<td class="literal">Anulada refundida:</td>		<td width="25px" class="detalI">${comisionApliBean.comision.individualanuladarefundida}</td>
								<td class="literal">% Gastos entidad:</td>		<td width="30px" class="detalI">${comisionApliBean.comision.individualpctogtosent}</td>
								<td class="literal">% Gastos comisiones:</td>	<td width="30px" class="detalI">${comisionApliBean.comision.individualpctogtoscomis}</td>
							</tr>
						</c:otherwise>
						</c:choose>
					</table>
					<hr/>
					<c:if test="${tipoPlan eq '2014-'}">
						<table>
							<tr>
								<td class="literal">DATOS NUEVOS:</td>
								<td class="literal">Prima cálculo:</td>		<td width="50px" class="detalI">${comisionApliBean.comision.dnPrimabasecalculo}</td>
								<td class="literal">Gastos entidad:</td>	<td width="50px" class="detalI">${comisionApliBean.comision.dnGastosextentidad}</td>
								<td class="literal">Comisiones:</td>		<td width="50px" class="detalI">${comisionApliBean.comision.dnComisiones}</td> 
								<td class="literal">Total:</td>				<td width="50px" class="detalI">${comisionApliBean.comision.dnTotal}</td>
								<td colspan="4" rowspan="2">&nbsp;</td>
							</tr>
							<tr>
								<td class="literal">DATOS REGUL.:</td>
								<td class="literal">Prima cálculo:</td>		<td width="50px" class="detalI">${comisionApliBean.comision.drPrimabasecalculo}</td>
								<td class="literal">Gastos entidad:</td>	<td width="50px" class="detalI">${comisionApliBean.comision.drGastosextentidad}</td>
								<td class="literal">Comisiones:</td>		<td width="50px" class="detalI">${comisionApliBean.comision.drComisiones}</td>
								<td class="literal">Total:</td>				<td width="50px" class="detalI">${comisionApliBean.comision.drTotal}</td>
							</tr>
							<tr>
								<td class="literal">DATOS TOTALES:</td>
								<td class="literal">Prima cálculo:</td>		<td width="45px" class="detalI">${comisionApliBean.comision.dtPrimabasecalculo}</td>
								<td class="literal">Gastos entidad:</td>	<td width="50px" class="detalI">${comisionApliBean.comision.dtGastosextentidad}</td>
								<td class="literal">Comisiones:</td>		<td width="50px" class="detalI">${comisionApliBean.comision.dtComisiones}</td>
								<td class="literal">Total:</td>				<td width="45px" class="detalI">${comisionApliBean.comision.dtTotal}</td>
								<td class="literal">Gastos pagados:</td>	<td width="25px" class="detalI">${comisionApliBean.comision.dtGastospagados}</td>
								<td class="literal">Gastos pendientes:</td>	<td width="25px" class="detalI">${comisionApliBean.comision.dtGastospendientes}</td>
							</tr>
						</table>
						
						<table>
							<tr>
								<td class="literal" style="" width="360px">Marcas Condiciones Particulares Comisiones:</td>
								<td width="45px" class="detalI">${marcaCondiComisiones}</td>
							</tr>									
						</table>
					</c:if>			
					<fieldset>
					<legend class="literal">Datos Aplicacion</legend>
					<table>
						<tr>
							<td class="literal">Referencia:</td>		<td width="80px" class="detalI">${comisionApliBean.referencia}</td>
							<td class="literal">Dígito control:</td>	<td width="50px" class="detalI">${comisionApliBean.dc}</td>
							<td class="literal">Código interno :</td>	<td width="50px" class="detalI">${comisionApliBean.codinterno}</td>
						</tr>
					</table>

					<c:choose>
					<c:when test="${tipoPlan eq '2014-'}">
						<table>
							<tr>
								<td class="literal">DATOS NUEVOS:</td>
								<td class="literal">Prima cálculo:</td>		<td width="50px" class="detalI">${comisionApliBean.dnPrimabasecalculo}</td>
								<td class="literal">Gastos entidad:</td>	<td width="50px" class="detalI">${comisionApliBean.dnGastosextentidad}</td>
								<td class="literal">Comisiones:</td>		<td width="50px" class="detalI">${comisionApliBean.dnComisiones}</td> 
								<td class="literal">Total:</td>				<td width="50px" class="detalI">${comisionApliBean.comision.dnTotal}</td>
								<td colspan="4" rowspan="2">&nbsp;</td>
							</tr>
							<tr>
								<td class="literal">DATOS REGUL.:</td>
								<td class="literal">Prima cálculo:</td>		<td width="50px" class="detalI">${comisionApliBean.drPrimabasecalculo}</td>
								<td class="literal">Gastos entidad:</td>	<td width="50px" class="detalI">${comisionApliBean.drGastosextentidad}</td>
								<td class="literal">Comisiones:</td>		<td width="50px" class="detalI">${comisionApliBean.drComisiones}</td>
								<td class="literal">Total:</td>				<td width="50px" class="detalI">${comisionApliBean.drTotal}</td>
							</tr>
							<tr>
								<td class="literal">DATOS TOTALES:</td>
								<td class="literal">Prima cálculo:</td>		<td width="45px" class="detalI">${comisionApliBean.dtPrimabasecalculo}</td>
								<td class="literal">Gastos entidad:</td>	<td width="50px" class="detalI">${comisionApliBean.dtGastosextentidad}</td>
								<td class="literal">Comisiones:</td>		<td width="50px" class="detalI">${comisionApliBean.dtComisiones}</td>
								<td class="literal">Total:</td>				<td width="45px" class="detalI">${comisionApliBean.dtTotal}</td>
								<td class="literal">Gastos pagados:</td>	<td width="25px" class="detalI">${comisionApliBean.dtGastospagados}</td>
								<td class="literal">Gastos pendientes:</td>	<td width="25px" class="detalI">${comisionApliBean.dtGastospendientes}</td>
							</tr> 
						</table>
					</c:when>
					
					<c:otherwise>
						<!-- Datos sólo 2015+ (INICIO) -->
						<table><tr><td valign="top">
						<table border="1">
							<tr>
								<td >&nbsp;</td>
								<td class="literal">DATOS TOTALES</td>
								<td class="literal">DATOS NUEVOS</td>
								<td class="literal">DATOS REGUL.</td>
							</tr>
							<tr>
								<td class="literal">Prima Comercial Neta:</td>
								<td class="detalI">${comisionApliBean.dt1PrimaComercialNeta}</td>
								<td class="detalI">${comisionApliBean.dn1PrimaComercialNeta}</td>
								<td class="detalI">${comisionApliBean.dr1PrimaComercialNeta}</td>
							</tr>
							<tr>
								<td class="literal">Gastos Administración:</td>
								<td class="detalI">${comisionApliBean.dt1GastosAdminEntidad}</td>
								<td class="detalI">${comisionApliBean.dn1GastosAdminEntidad}</td>
								<td class="detalI">${comisionApliBean.dr1GastosAdminEntidad}</td>
							</tr>
							<tr>
								<td class="literal">Gastos Adquisición:</td>
								<td class="detalI">${comisionApliBean.dt1GastosAdqEntidad}</td>
								<td class="detalI">${comisionApliBean.dn1GastosAdqEntidad}</td>
								<td class="detalI">${comisionApliBean.dr1GastosAdqEntidad}</td>
							</tr>
							<tr>
								<td class="literal">Comisión Ent.:</td>
								<td class="detalI">${comisionApliBean.dt1ComisionesMediadorEnt}</td>
								<td class="detalI">${comisionApliBean.dn1ComisionesMediadorEnt}</td>
								<td class="detalI">${comisionApliBean.dr1ComisionesMediadorEnt}</td>	
							</tr>
							<tr>
								<td class="literal">Comision E-S Med.:</td>
								<td class="detalI">${comisionApliBean.dt1ComisionesMediadorEsmed}</td>
								<td class="detalI">${comisionApliBean.dn1ComisionesMediadorEsmed}</td>
								<td class="detalI">${comisionApliBean.dr1ComisionesMediadorEsmed}</td>
							</tr>
							<tr>
								<td class="literal">Total gastos:</td>
								<td class="detalI">N/A</td>
								<td class="detalI">${comisionApliBean.dn1Total}</td>
								<td class="detalI">${comisionApliBean.dr1Total}</td>
							</tr>
						</table>
						</td>
						<td>
						<table border="1">
							<tr>
								<td >&nbsp;</td>
								<td class="literal">DATOS TOTALES</td>
							</tr>
							<tr>
								<td class="literal">Gastos Administración Abon.:</td>
								<td class="detalI">${comisionApliBean.dt1GastosAdminEntAbon}</td>
							</tr>
							<tr>
								<td class="literal">Gastos Adquisición Abon.</td>
								<td class="detalI">${comisionApliBean.dt1GastosAdqEntAbon}</td>
							</tr>
							<tr>
								<td class="literal">Comisión Ent. Abon.</td>
								<td class="detalI">${comisionApliBean.dt1ComisMediadorAbonEnt}</td>
							</tr>
							<tr>
								<td class="literal">Comisión E-S Med. Abon.</td>
								<td class="detalI">${comisionApliBean.dt1ComisMediadorAbonEsmed}</td>
							</tr>
							<tr>
								<td class="literal">Gastos Administración Pdte.</td>
								<td class="detalI">${comisionApliBean.dt1GastosAdminEntPdte}</td>
							</tr>
							<tr>
								<td class="literal">Gastos Adquisición Pdte.</td>
								<td class="detalI">${comisionApliBean.dt1GastosAdqEntPdte}</td>
							</tr>
							<tr>
								<td class="literal">Comisión Ent. Pdte.</td>
								<td class="detalI">${comisionApliBean.dt1ComisMediadorPdteEnt}</td>
							</tr>
							<tr>
								<td class="literal">Comisión E-S Med. Pdte.</td>
								<td class="detalI">${comisionApliBean.dt1ComisMediadorPdteEsmed}</td>
							</tr>
						</table>
						</td></tr></table>
						<!-- Datos sólo 2015+ (FIN) -->
					</c:otherwise>
					</c:choose>
					<c:if test="${tipoPlan eq '2014-'}">
						<table>
							<tr>
								<td class="literal">Marcas Condiciones Particulares Aplicacion:</td>
								<td width="45px" class="detalI">${marcaCondiComisionesApli}</td>
							</tr>
						</table>
					</c:if>
					</fieldset>
					</fieldset>
						
				</div>
				
			</form:form>
			
			<div id="grid" style="width: 95%">
			  	${listadoComisionApli}		  							               
			</div> 	
		
		
		</div> 
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
		
		

</body>
</html>