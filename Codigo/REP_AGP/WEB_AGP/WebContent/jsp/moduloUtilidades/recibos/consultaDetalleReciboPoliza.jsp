<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>


<%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", 0);
%>


<html>
	<head>
		<title>Consulta de un Recibo</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<!-- Estilos -->
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		<!-- JavaScript,jQery & AJAX -->
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>	
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/util.js" ></script>		
		
		<script language="javascript">		
		
			function imprimir(){				
				var frm = document.getElementById('main3');
				frm.method.value = 'doVerPDFPoliza';
				frm.target="_blank";
				frm.submit();						
			}
			function volver(){
				$(window.location).attr('href', 'recibosPoliza.html?refPoliza='+$('#refpoliza').val()+ "&idPoliza=" + $('#idPoliza').val()); 		
			}
		</script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4');">
		<%@ include file="../../common/static/cabecera.jsp"%>
		<%@ include file="../../common/static/menuGeneral.jsp"%>
		<%@ include file="../../common/static/datosCabeceraTaller.jsp" %>		
		
<!-- Botones para alta, imprimir y volver -->
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="right">
						<!-- <a class="bot" href="javascript:imprimir();">Copy</a>-->
						<!-- <a class="bot" href="#">Limpiar</a> -->
						<a class="bot" href="javascript:volver();">Volver</a>					
					</td>
				</tr>
			</table>			
		</div>
		
<!-- Contenido de la pagina -->
		<div class="conten" style="padding: 3px; width: 97%">
			<p class="titulopag" align="left">Consulta de un Recibo</p>
			<form:form name="main3" id="main3" action="recibosPoliza.html" method="post" commandName="reciboPolizaBean" >
				<input type="hidden" id="method" name="method" />
				<input type="hidden" name="idPoliza" id="idPoliza"  value="${idPoliza}"/>
				<input type="hidden" name="tipoReciboAux" id="tipoReciboAux"  value="${tipoReciboAux}"/>
				<input type="hidden" name="tipoPolizaAux" id="tipoPolizaAux"  value="${tipoPolizaAux}"/>
				<input type="hidden" name="fechaEmisionAux" id="fechaEmisionAux"  value="${fechaEmisionAux}"/>
				<input type="hidden" name="pintarPlan2015" id="pintarPlan2015"  value="${pintarPlan2015}"/>
				
				<form:hidden path="id" id="id"/>
				<form:hidden path="refpoliza" id="refpoliza"/>
					
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>	
				<div class="panel2 isrt" style="width:100%">
					<fieldset>
					<legend class="literal">Datos del recibo</legend>			
				
						<table cellspacing="5" align="center" width="100%">
							<tr>	
								<td class="literal">Plan: </td>
								<td class="detalI">
									${reciboPolizaBean.recibo.codplan}
								</td>
								<td class="literal" colspan="2">L&iacute;nea: </td>
								<td class="detalI">
									${reciboPolizaBean.recibo.codlinea}
								</td>
								<td class="literal" colspan="2">Fase: </td>
								<td class="detalI">
									${reciboPolizaBean.recibo.codfase}
								</td>
								<td class="literal">Fecha Emisi&oacute;n: </td>
								<td class="detalI">
									${fechaEmisionAux}
								</td>
								<td class="literal">Nº Recibo: </td>
								<td class="detalI">
									${reciboPolizaBean.recibo.codrecibo}
								</td>
								<td class="literal">Tipificación Recibo: </td>
								<td class="detalI">
									${reciboPolizaBean.recibo.tipificacionRecibos.tipificacionRecibo}-${reciboPolizaBean.recibo.tipificacionRecibos.descripcion}
								</td>
							</tr>
							<tr>
								<td class="literal">P&oacute;liza: </td>
								<td class="detalI">
									${reciboPolizaBean.refpoliza}
								</td>
								<td class="literal" colspan="2">Tipo P&oacute;liza: </td>
								<td class="detalI">
									${tipoPolizaAux}
								</td>
								<td class="literal" colspan="2">Tipo Recibo: </td>
								<td class="detalI">
									${tipoReciboAux}
								</td>
								<td class="literal">CIF/NIF Asegurado: </td>
								<td class="detalI">
									${reciboPolizaBean.nifaseg}
								</td>
							</tr>
						</table>
					</fieldset>
					</div>
				</div>
				</form:form>
				<div style="padding:20px">
					<table class="tableborde" style="width:85%;border-collapse: collapse" align="center" cellspacing="0" >
						<thead class="literalbordeCabecera" >
							<c:choose>
									<c:when test="${pintarPlan2015 eq false}">	
										<tr>
											<th colspan="2">Importes</th>
											<th colspan="2">Bonificaciones/Descuentos</th>
											<th colspan="2">Subvenciones</th>								
										</tr>
									</c:when>
									<c:otherwise>
										<tr>
											<th colspan="2">Importes</th>
											<th colspan="2">Bonificaciones/Recargos</th>
											<th colspan="2">Subvenciones</th>								
										</tr>
									</c:otherwise>
							</c:choose>
						</thead>
						<c:choose>
							<c:when test="${pintarPlan2015 eq false}">		
								<tbody class="literalborde"> 
									<tr>
										<td align="left" style="padding:5px;">Prima Comercial:</td>
										<td align="right" style="padding:5px;color:blue">${reciboPolizaBean.primacomercial} &euro;</td>
										<td align="left" style="padding:5px;font-weight:normal">Bonif. Medidas Preventivas:</td>
										<td align="right" style="padding:5px;color:grey">${reciboPolizaBean.bonifsistproteccion } &euro;</td>
										<td align="left" style="padding:5px;font-weight:normal">Total ENESA</td>
										<td align="right" style="padding:5px;color:grey">${reciboPolizaBean.subvenesa } &euro;</td>							
									</tr>
									<tr>
										<td align="left" style="padding:5px">Prima Neta:</td>
										<td align="right" style="padding:5px;color:blue">${reciboPolizaBean.primaneta} &euro;</td>
										<td align="left" style="padding:5px;font-weight:normal">Bonif. Asegurado:</td>
										<td align="right" style="padding:5px;color:grey">${reciboPolizaBean.bonificacion} &euro;</td>
										<td align="left" style="padding:5px"></td>
										<td align="right" style="padding:5px;color:grey"></td>								
									</tr>
									<tr>
										<td align="left" style="padding:5px;font-weight:normal">Reaseguro Consorcio:</td>
										<td align="right" style="padding:5px;color:grey">${reciboPolizaBean.consorcio} &euro;</td>
										<td align="left" style="padding:5px;font-weight:normal">Recargo Asegurado:</td>
										<td align="right" style="padding:5px;color:grey">${reciboPolizaBean.recargo} &euro;</td>
										
									</tr>							
									<tr>
										<td align="left" style="padding:5px;font-weight:normal">Recargo Consorcio:</td>
										<td align="right" style="padding:5px;color:grey">${reciboPolizaBean.clea} &euro;</td>
										<td align="left" style="padding:5px;font-weight:normal">Descuento C. Colectiva:</td>
										<td align="right" style="padding:5px;color:grey">${reciboPolizaBean.dctocolectivo} &euro;</td>
									</tr>
										<c:forEach items="${reciboPolizaBean.reciboPolizaSubvs }" var="subcca">
											<tr>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td align="left" style="padding:5px;font-weight:normal">Total  <c:out value="${subcca.organismo.desorganismo}"/></td>
												<td align="right" style="padding:5px;color:grey"><c:out value="${subcca.subvccaa }"/>&euro;</td>
											</tr>
										</c:forEach>	
									<tr>
										<td align="left" style="padding:5px">Coste Neto:</td>
										<td align="right" style="padding:5px;color:blue">${reciboPolizaBean.costeneto} &euro;</td>
										<td align="left" style="padding:5px;">Total Bonif/Descuento:</td>
										<td align="right" style="padding:5px;color:blue">${reciboPolizaBean.bonifsistproteccion  + reciboPolizaBean.bonificacion + reciboPolizaBean.dctocolectivo - reciboPolizaBean.clea} &euro;</td>
										
									</tr>
									<tr>
										<td align="left" style="padding:5px">Coste Tomador:</td>
										<td align="right" style="padding:5px;color:blue">${reciboPolizaBean.costetomador} &euro;</td>
										<td></td>
										<td></td>
										<td align="left" style="padding:5px"></td>
										<td align="right" style="padding:5px;color:grey"></td>								
									</tr>
									<tr>
										<td align="left" style="padding:5px;font-weight:normal">Importe Abonado:</td>
										<td align="right" style="padding:5px;color:grey">${reciboPolizaBean.pagos} &euro;</td>
										<td></td>
										<td></td>
										<td align="left" style="padding:5px"></td>
										<td align="right" style="padding:5px;color:grey"></td>								
									</tr>
									<tr>
										<td align="left" style="padding:5px;font-weight:normal">Saldo Póliza:</td>
										<td align="right" style="padding:5px;color:grey">${reciboPolizaBean.saldopoliza} &euro;</td>
										<td></td>
										<td></td>
										<td align="left" style="padding:5px"></td>
										<td align="right" style="padding:5px;color:grey"></td>
									</tr>
								</tbody>
							</c:when>
						<c:otherwise>
							<tbody class="literalborde"> 
									
										<c:forEach items="${reciboPolizaBean.reciboPolizaSubvs }" var="subcca">
											<c:set var="auxTotCCAA"  value="${auxTotCCAA + subcca.subvccaa}"/>
										</c:forEach>
										<c:forEach items="${reciboPolizaBean.reciboBonificacionRecargos }" var="boniReca">
											<c:set var="auxTotBonRe"  value="${auxTotBonRe + boniReca.importe}"/>
										</c:forEach>
									
									<tr>
										<td align="left" style="padding:5px;">Prima Comercial:</td>
										<td align="right" style="padding:5px;color:blue">${reciboPolizaBean.primacomercial} &euro;</td>
										<td></td>										
										<td></td>
										<td align="left" style="padding:5px;font-weight:normal">Total ENESA:</td>
										<td align="right" style="padding:5px;color:grey">${reciboPolizaBean.subvenesa } &euro;</td>	
											
									</tr>
										<c:forEach items="${reciboPolizaBean.reciboBonificacionRecargos }" var="br">
												<tr>
													<td></td>
													<td></td>
													<td align="left" style="padding:5px;font-weight:normal"><c:out value="${br.bonificacionRecargo.descripcion}"/></td>
													<td align="right" style="padding:5px;color:grey"><c:out value="${br.importe }"/>&euro;</td>
												</tr>
										</c:forEach>		
									<tr>
										<td align="left" style="padding:5px">Prima Comercial Neta:</td>
										<td align="right" style="padding:5px;color:blue">${reciboPolizaBean.primaneta} &euro;</td>
										<td align="left" style="padding:5px;">Total Bonif/Recargos:</td>
										<td align="right" style="padding:5px;color:blue">${auxTotBonRe} &euro;</td>
										<td align="left" style="padding:5px;font-weight:normal">Total CCAA:</td>
										<td align="right" style="padding:5px;color:grey">${auxTotCCAA} &euro;</td>
									</tr>
									<tr>
										<td align="left" style="padding:5px;font-weight:normal">Recargo Consorcio:</td>
										<td align="right" style="padding:5px;color:grey">${reciboPolizaBean.recargo} &euro;</td>
									</tr>							
									<tr>
										<td align="left" style="padding:5px;font-weight:normal">Recibo de Prima:</td>
										<td align="right" style="padding:5px;color:grey">${reciboPolizaBean.reciboPrima} &euro;</td>
									</tr>
									<tr>
										<td align="left" style="padding:5px">Coste Tomador:</td>
										<td align="right" style="padding:5px;color:blue">${reciboPolizaBean.costetomador} &euro;</td>
									</tr>
									<tr>
										<td align="left" style="padding:5px">Total Coste Tomador:</td>
										<td align="right" style="padding:5px;color:blue">${reciboPolizaBean.totalCosteTomador} &euro;</td>
									</tr>
									<tr>
										<td align="left" style="padding:5px;font-weight:normal">Importe abonado:</td>
										<td align="right" style="padding:5px;color:grey">${reciboPolizaBean.diferencia} &euro;</td>
									</tr>
									<tr>
										<td align="left" style="padding:5px;font-weight:normal">Saldo Poliza:</td>
										<td align="right" style="padding:5px;color:grey">${reciboPolizaBean.saldopoliza} &euro;</td>
									</tr>
									<tr>
										<td align="left" style="padding:5px;font-weight:normal">Importe Recargo Aval:</td>
										<td align="right" style="padding:5px;color:grey">${reciboPolizaBean.recargoAval} &euro;</td>
									</tr>
									<tr>
										<td align="left" style="padding:5px;font-weight:normal">Importe Recargo Fraccionamiento:</td>
										<td align="right" style="padding:5px;color:grey">${reciboPolizaBean.recargoFracc} &euro;</td>
									</tr>
								</tbody>
							
						
						
						</c:otherwise>
					</c:choose>									
					</table>
				</div>			
		</div>	
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
	</body>
</html>