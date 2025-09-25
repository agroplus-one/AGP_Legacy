<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
<head>
	<title>Agroplus - Detalle Impagados</title>
		
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
			    $.get('${pageContext.request.contextPath}/ficheroImpagados.run?ajax=true&idFichero='+$("#idFichero").val() +'&' + parameterString, function(data) {
			        $("#grid").html(data)
		  			});
			}
		   	function salir(){
		    	var frm = document.getElementById('main1');
		    	frm.method.value="doVolver";
		    	$('#main1').submit();
		    }
		    function detalle(idImpagado){
		    	var frm = document.getElementById('main1');
		    	frm.method.value="doDetalle";
		    	frm.idImpagado.value=idImpagado;
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
							<a class="bot" id="btnSalir" href="javascript:salir();">Salir</a>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%" >
			<p class="titulopag" align="left">Detalle Fichero Impagados</p>			
			
			<form:form name="main1" id="main1" action="ficheroImpagados.run" method="post"  commandName="reciboImpagadoBean">
				<input type="hidden" name="method" id="method" />	
				<input type="hidden" name="idFichero" id ="idFichero" value="${idFichero}"/>
				<input type="hidden" name="idImpagado" id ="idImpagado" value="${idImpagado}"/>
				<input type="hidden" name="idFichero" id ="idFichero" value="${idFichero}"/>
				<input type="hidden" name="nombreFichero" id ="nombreFichero" value="${reciboImpagadoBean.fichero.nombrefichero}"/>
				<input type="hidden" name="estado" id ="estado" value="${estado}"/>
				<input type="hidden" name="fase" id ="fase" value="${reciboImpagadoBean.fichero.fase.fase}"/>
				
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				
				<div class="panel2 isrt" style="width: 90%;margin:0 auto;" id="divDatos">
					<fieldset><legend class="literal">Datos Fichero</legend>
						<table align="center">
			
							<td class="literal">Fichero:</td>
								<td width="350px" class="detalI">${reciboImpagadoBean.fichero.nombrefichero}</td>
							<td class="literal">Estado:</td>
								<td width="350px" class="detalI">${estado}</td>
							<td class="literal">Fase:</td>
								<td width="350px" class="detalI">${reciboImpagadoBean.fichero.fase.fase}</td>
			
						</table>
					</fieldset>
				</div>
				<div class="panel2 isrt" style="width: 95%;margin:0 auto;" id="divDatosComunes">
						<fieldset><legend class="literal">Detalle Impagados</legend>
							<table>
								<c:choose>
									<c:when test="${reciboImpagadoBean.colectivoreferencia != null}">
										<tr align="center">
											<td class="literal">Referencia colectivo:</td><td width="50px" class="detalI">${reciboImpagadoBean.colectivoreferencia}</td>
											<td class="literal">Dígito control:</td><td width="30px" class="detalI">${reciboImpagadoBean.colectivodc}</td>
											<td class="literal">Código interno Colectivo:</td><td width="50px" class="detalI">${reciboImpagadoBean.colectivocodinterno}</td>
											<td class="literal">Localidad:</td><td width="70px" class="detalI">${reciboImpagadoBean.colectivolocalidad}</td>
										</tr>
									</c:when>
								<c:otherwise>
									<tr>
										<td class="literal">Pol. referencia :</td><td width="50px" class="detalI">${reciboImpagadoBean.individualreferencia}</td>
										<td class="literal">Pol. DC:</td><td width="25px" class="detalI">${reciboImpagadoBean.individualdc}</td>
										<td class="literal">Pol. localidad</td><td width="25px" class="detalI">${reciboImpagadoBean.individuallocalidad}</td>
										<td class="literal">Pol. código interno:</td><td width="50px" class="detalI">${reciboImpagadoBean.individualcodinterno}</td>
									</tr>
								</c:otherwise>
								</c:choose>
							</table>
							
					<c:choose>
					<c:when test="${tipoPlan eq '2014-'}">
							<table>
								<tr>
									<td class="literal"> PDTE. ABONO RECIBO IMP.:</td>
									<td class="literal">Gastos entidad:</td><td width="50px" class="detalI">${reciboImpagadoBean.pariGastosentidad}</td>
									<td class="literal">Gastos comisiones:</td><td width="50px" class="detalI">${reciboImpagadoBean.pariGastoscomisiones}</td>
									<td class="literal">Total gastos:</td><td width="50px" class="detalI">${reciboImpagadoBean.pariTotalgastos}</td> 
									<td class="literal">Importe pdte.:</td><td width="50px" class="detalI">${reciboImpagadoBean.pariImportepdte}</td>
									
								</tr>
								<tr>
									<td class="literal"> COBRO ACTUAL:  </td>
									<td class="literal">Gastos entidad:</td><td width="50px" class="detalI">${reciboImpagadoBean.caGastosentidad}</td>
									<td class="literal">Gastos comisiones:</td><td width="50px" class="detalI">${reciboImpagadoBean.caGastoscomisiones}</td>
									<td class="literal">Total gastos:</td><td width="50px" class="detalI">${reciboImpagadoBean.caTotalgastos}</td>
									<td class="literal">Imp. recibo:</td><td width="50px" class="detalI">${reciboImpagadoBean.caImporterecibo}</td>
								</tr>
								<tr>
									<td class="literal"> PDTE. ABONO:</td>
									<td class="literal">Gastos entidad:</td><td width="45px" class="detalI">${reciboImpagadoBean.paGastosentidad}</td>
									<td class="literal">Gastos comisiones:</td><td width="50px" class="detalI">${reciboImpagadoBean.paGastoscomisiones}</td>
									<td class="literal">Total gastos:</td><td width="50px" class="detalI">${reciboImpagadoBean.paTotalgastos}</td>
								</tr>
							</table>
					</c:when>
					
					<c:otherwise>
						<!-- Datos sólo 2015+ (INICIO) -->
						<table border="1">
							<tr>
								<td >&nbsp;</td>
								<td class="literal">Pendiente Abono Recibo Impagado</td>
								<td class="literal">Cobro Actual</td>
								<td class="literal">Pendiente Abono</td>
							</tr>
							<tr>
								<td class="literal">Comisión Ent.:</td>
								<td class="detalI">${reciboImpagadoBean.pari1ComisMediadorEnt}</td>
								<td class="detalI">${reciboImpagadoBean.ca1ComisMediadorEnt}</td>
								<td class="detalI">${reciboImpagadoBean.pa1ComisMediadorEnt}</td>	
							</tr>
							<tr>
								<td class="literal">Comision E-S Med.:</td>
								<td class="detalI">${reciboImpagadoBean.pari1ComisMediadorEsmed}</td>
								<td class="detalI">${reciboImpagadoBean.ca1ComisMediadorEsmed}</td>
								<td class="detalI">${reciboImpagadoBean.pa1ComisMediadorEsmed}</td>
							</tr>
							<tr>
								<td class="literal">Gastos Administración:</td>
								<td class="detalI">${reciboImpagadoBean.pari1GastosAdminEntidad}</td>
								<td class="detalI">${reciboImpagadoBean.ca1GastosAdminEntidad}</td>
								<td class="detalI">${reciboImpagadoBean.pa1GastosAdminEntidad}</td>
							</tr>
							<tr>
								<td class="literal">Gastos Adquisición:</td>
								<td class="detalI">${reciboImpagadoBean.pari1GastosAdqEntidad}</td>
								<td class="detalI">${reciboImpagadoBean.ca1GastosAdqEntidad}</td>
								<td class="detalI">${reciboImpagadoBean.pa1GastosAdqEntidad}</td>
							</tr>

							<tr>
								<td class="literal">Total gastos:</td>
								<td class="detalI">${reciboImpagadoBean.pari1TotalGastos}</td>
								<td class="detalI">${reciboImpagadoBean.ca1TotalGastos}</td>
								<td class="detalI">${reciboImpagadoBean.pa1TotalGastos}</td>
							</tr>
							<tr>
								<td class="literal">Importe cobro recibido:</td>
								<td class="detalI">N/A</td>
								<td class="detalI">${reciboImpagadoBean.ca1ImporteCobroRecibido}</td>
								<td class="detalI">N/A</td>
							</tr>
							<tr>
								<td class="literal">Importe saldo pendiente:</td>
								<td class="detalI">N/A</td>
								<td class="detalI">${reciboImpagadoBean.pari1ImporteSaldoPendiente}</td>
								<td class="detalI">N/A</td>
							</tr>
						</table>
						<!-- Datos sólo 2015+ (FIN) -->
					</c:otherwise>
					</c:choose>
					
					</fieldset>
				</div>
			</form:form>
			<div id="grid" style="width: 90%;margin:0 auto;">
			  	${listadoFicheroImpagados}		  							               
			</div> 	
		</div> 
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
		
</body>
</html>