<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
<head>
	<title>Agroplus - Detalle Emitidos Aplicación</title>
		
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
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
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
			    $.get('${pageContext.request.contextPath}/ficheroEmitidosApli.run?ajax=true&idEmitidos='+$("#idEmitidos").val() +'&idFichero='+$("#idFichero").val()+'&' + parameterString, function(data) {
			        $("#grid").html(data)
		  			});
			}
		    
		   
		    function volver(){
		    	var frm = document.getElementById('main1');
		    	frm.method.value="doVolver";
		    	$('#main1').submit();
		    }
		    
		    function detalle(idEmitidos,idEmitidosApli){
		    	var frm = document.getElementById('main1');
		    	frm.method.value="doDetalle";
		    	frm.idEmitidos.value = idEmitidos;
		    	frm.idEmitidosApli.value = idEmitidosApli;
		    	$('#main1').submit();
		    }
		    function guardaIdEmitidoApli(idEmitidosApli){
		    	var frm = document.getElementById('main1');
		    	frm.idEmitidosApli.value = idEmitidosApli;
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
			<p class="titulopag" align="left">Detalle Fichero Recibos Emitidos Aplicación</p>			
			
			
			<form:form name="main1" id="main1" action="ficheroEmitidosApli.run" method="post"  commandName="emitidosApliBean">
				<input type="hidden" name="method" id="method" />	
				<input type="hidden" name="idFichero" id ="idFichero" value="${idFichero}"/>
				<input type="hidden" name="nombreFichero" id ="nombreFichero" value="${nombreFichero}"/>
				<input type="hidden" name="estado" id ="estado" value="${estado}"/>
				<input type="hidden" name="fase" id ="fase" value="${fase}"/>
				<input type="hidden" name="idEmitidos" id ="idEmitidos" value="${idEmitidos}"/>
				<input type="hidden" name="idEmitidosApli" id ="idEmitidosApli" value="${idEmitidosApli}"/>
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				
				<div class="panel2 isrt" style="width: 95%;" id="divDatosComunes">
						<fieldset><legend class="literal">Datos Recibo</legend>
							<table>
								<c:choose>
								<c:when test="${emitidosApliBean.reciboEmitido.colectivoref != null}">
									<tr>
										<td class="literal">Referencia colectivo:</td><td width="80px" class="detalI">${emitidosApliBean.reciboEmitido.colectivoref}</td>
										<td class="literal">Dígito control:</td><td width="50px" class="detalI">${emitidosApliBean.reciboEmitido.colectivodc}</td>
									</tr>
								</c:when>
								<c:otherwise>
								<tr>
									<td class="literal">Pol. referencia :</td><td width="80px" class="detalI">${emitidosApliBean.reciboEmitido.individualreferencia}</td>
									<td class="literal">Pol. DC:</td><td width="50px" class="detalI">${emitidosApliBean.reciboEmitido.individualdc}</td>
								</tr>
								</c:otherwise>
								</c:choose>
							</table>
							<c:if test="${tipoPlan eq '2014-'}">
								<table>
									<tr>
										<td class="literal" width="100px">Prima comercial:</td><td width="50px" class="detalI">${emitidosApliBean.reciboEmitido.primacomercial}</td>
										<td class="literal" width="100px">Bonif. sist. protección:</td><td width="70px" class="detalI">${emitidosApliBean.reciboEmitido.bonsistproteccion}</td>
										<td class="literal">Bonificación:</td><td width="50px" class="detalI">${emitidosApliBean.reciboEmitido.bonificacion}</td> 
										<td class="literal">Recargo:</td><td width="50px" class="detalI">${emitidosApliBean.reciboEmitido.recargo}</td>
										<td class="literal">Dto. colectivo:</td><td width="50px" class="detalI">${emitidosApliBean.reciboEmitido.dtoColectivo}</td>
									</tr>
									<tr>
										<td class="literal">Dto. ventanilla:</td><td width="50px" class="detalI">${emitidosApliBean.reciboEmitido.dtoVentanilla}</td>
										<td class="literal">Prima neta:</td><td width="30px" class="detalI">${emitidosApliBean.reciboEmitido.primaneta}</td>
										<td class="literal">Consorcio:</td><td width="50px" class="detalI">${emitidosApliBean.reciboEmitido.consorcio}</td>
										<td class="literal">Clea:</td><td width="45px" class="detalI">${emitidosApliBean.reciboEmitido.clea}</td>
										<td class="literal">Coste neto:</td><td width="50px" class="detalI">${emitidosApliBean.reciboEmitido.costeneto}</td>
									</tr>
									<tr>
										<td class="literal">Subv. enesa:</td><td width="50px" class="detalI">${emitidosApliBean.reciboEmitido.subvenesa}</td>
										<td class="literal">Coste tomador:</td><td width="45px" class="detalI">${emitidosApliBean.reciboEmitido.costetomador}</td>
										<td class="literal">Pagos:</td><td width="25px" class="detalI">${emitidosApliBean.reciboEmitido.pagos}</td>
										<td class="literal">Comp. saldo tomador:</td><td width="25px" class="detalI">${emitidosApliBean.reciboEmitido.compsaldotomador}</td>
										<td class="literal">Comp. recibos impagados:</td><td width="25px" class="detalI">${emitidosApliBean.reciboEmitido.comprecibosimpagados}</td>
										<td class="literal">Líquido:</td><td width="25px" class="detalI">${emitidosApliBean.reciboEmitido.liquido}</td>
									</tr>
								</table>
								<table width="97%" cellspacing="0" cellpadding="0" border="0">
									<tbody>
										<tr>
											<td align="right">
												<a class="bot" id="btnSubvCCAA" href="javascript:lupas.muestraTabla('EmitidosSubvCCAA','principio', '', '');">Subvenciones CCAA</a>
												<a class="bot" id="btnSubvCCAA" href="javascript:lupas.muestraTabla('EmitidosDetComp','principio', '', '');">Detalle Compensación</a>
											</td>
										</tr>
									</tbody>
								</table>
							</c:if>
							<fieldset><legend class="literal">Datos Aplicacion</legend>
							<table>
								<td class="literal">Referencia:</td><td width="80px" class="detalI">${emitidosApliBean.referencia}</td>
								<td class="literal">Dígito control:</td><td width="50px" class="detalI">${emitidosApliBean.digitocontrol}</td>
								<td class="literal">NIF/CIF :</td><td width="50px" class="detalI">${emitidosApliBean.nifcif}</td>
							</table>
							
					<c:choose>
					<c:when test="${tipoPlan eq '2014-'}">
							<table>
								<tr>
									<td class="literal">Saldo póliza:</td><td width="50px" class="detalI">${emitidosApliBean.saldopoliza}</td>
									<td class="literal">Prima comercial:</td><td width="50px" class="detalI">${emitidosApliBean.primacomercial}</td>
									<td class="literal">Bonificación sist. protección:</td><td width="50px" class="detalI">${emitidosApliBean.bonsistproteccion}</td> 
									<td class="literal">Bonificación:</td><td width="50px" class="detalI">${emitidosApliBean.bonificacion}</td>
									<td class="literal">Recargo:</td><td width="50px" class="detalI">${emitidosApliBean.recargo}</td>
								</tr>
								<tr>
									<td class="literal">Dto. colectivo:</td><td width="50px" class="detalI">${emitidosApliBean.dtocolectivo}</td>
									<td class="literal">Dto. ventanilla:</td><td width="50px" class="detalI">${emitidosApliBean.dtoventanilla}</td>
									<td class="literal">Prima neta:</td><td width="50px" class="detalI">${emitidosApliBean.primaneta}</td>
									<td class="literal">Consorcio:</td><td width="45px" class="detalI">${emitidosApliBean.consorcio}</td>
									<td class="literal">Clea:</td><td width="50px" class="detalI">${emitidosApliBean.clea}</td>
								</tr>
								<tr>
									
									<td class="literal">Coste neto:</td><td width="50px" class="detalI">${emitidosApliBean.costeneto}</td>
									<td class="literal">Subv. enesa:</td><td width="45px" class="detalI">${emitidosApliBean.subenesa}</td>
									<td class="literal">Coste tomador:</td><td width="25px" class="detalI">${emitidosApliBean.costetomador}</td>
									<td class="literal">Pagos:</td><td width="25px" class="detalI">${emitidosApliBean.pagos}</td>
								</tr> 
							</table>
					</c:when>
					
					<c:otherwise>
						<!-- Datos sólo 2015+ (INICIO) -->
							<table width="100%">
								<tr>
									<td class="literal">Coste Tomador:</td><td width="50px" class="detalI">${emitidosApliBean.de1CosteTomador}</td>
									<td class="literal">Diferencia:</td><td width="50px" class="detalI">${emitidosApliBean.de1Diferencia}</td>
									<td class="literal">Pagos:</td><td width="50px" class="detalI">${emitidosApliBean.de1Pagos}</td> 
									<td class="literal">Prima Comercial:</td><td width="50px" class="detalI">${emitidosApliBean.de1PrimaComercial}</td>
								</tr>
								<tr>
									<td class="literal">Prima Comercial Neta:</td><td width="50px" class="detalI">${emitidosApliBean.de1PrimaComercialNeta}</td>
									<td class="literal">Imp. Recargo Aval:</td><td width="50px" class="detalI">${emitidosApliBean.de1RecargoAval}</td>
									<td class="literal">Recargo Consorcio:</td><td width="50px" class="detalI">${emitidosApliBean.de1RecargoConsorcio}</td>
									<td class="literal">Imp. Recargo Fraccionamiento:</td><td width="50px" class="detalI">${emitidosApliBean.de1RecargoFracc}</td>

								</tr>
								<tr>
									<td class="literal">Recibo de Prima:</td><td width="45px" class="detalI">${emitidosApliBean.de1ReciboPrima}</td>
									<td class="literal">Subvención ENESA:</td><td width="50px" class="detalI">${emitidosApliBean.de1SubvEnesa}</td>									
									<td class="literal">Total Coste Tomador:</td><td width="50px" class="detalI">${emitidosApliBean.de1TotalCosteTomador}</td>
									<td colspan="2" align="right">
										<a class="bot" id="btnBonifRecargos" href="javascript:lupas.muestraTabla('EmitidosApliBonifRecargos','principio', '', '');">Bonificaciones y recargos</a>
									</td>
								</tr> 
							</table>
						<!-- Datos sólo 2015+ (FIN) -->
					</c:otherwise>
					</c:choose>

					</fieldset>
					</fieldset>
				</div>
				
			</form:form>
			
			<div id="grid" style="width: 95%">
			  	${listadoEmitidosApli}		  							               
			</div> 	
		
		
		</div> 
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEmitidosSubvCCAA.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEmitidosDetComp.jsp"%>	
	<%@ include file="/jsp/common/lupas/lupaEmitidosApliSubvCCAA.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEmitidosApliBonifRecargos.jsp"%>

</body>
</html>