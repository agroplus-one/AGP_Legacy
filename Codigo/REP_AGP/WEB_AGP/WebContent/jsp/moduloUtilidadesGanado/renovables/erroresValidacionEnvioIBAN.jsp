<%@ page language="java" import="java.util.*, com.rsi.agp.core.webapp.util.*"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Errores validación envío IBAN</title>
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		
		<script type="text/javascript" src="jsp/js/util.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>		
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
	   	<script language=javascript type="text/javascript">
	    </script>
	</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<%@ include file="/jsp/common/static/cabecera.jsp"%>
	

	<div class="divAcuse">
		<p class="titulopag" align="left">ERRORES DE VALIDACIÓN PARA EL ENVÍO DEL IBAN A AGROSEGURO</p>
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
		<form:form name="erroresValIBAN" method="post" id="erroresValIBAN" action="polizasRenovables.run">
		<input type="hidden" name="lstErroresEnvioIBAN" id="lstErroresEnvioIBAN" value="${lstErroresEnvioIBAN}" />					
				<table id="oculto" width="100%" style="border: 0px;" align="left" border=0>
					<tbody>
						<tr>
							<td valign="top" align="left">
								<c:if test="${mostrarErrNoAsegurado eq true}">
									<table border=0>
										<tr class="literalborde">
											<td class="literalborde" align="left" style="border: 0px;color: red;">Pólizas erróneas por asegurado inexistente:</td>
										</tr>
									</table>
									<table border=0>
										<tr class="literalborde"  >						
											<td width="15%" class="literalborde" align="right"> 
											<td class="literalborde" align="center" style="border:none;border-bottom: #CCCC00 1px solid;">Referencia</td>
											<td class="literalborde" align="center" style="border:none;border-bottom: #CCCC00 1px solid;"><nobr>E-S Mediadora</nobr></td> 
											<td class="literalborde" align="center" style="border:none;border-bottom: #CCCC00 1px solid;">NIF/CIF/NIE</td>							 
										</tr>
										 									
										<c:forEach var="error" items="${mapaListas.lstErroresNoAsegurado}">																		
											<tr class="literalborde">
												<td width="15%" class="literalborde" align=""right"">
												<td align="center" class="literalborde" align="center" style="border: 0px;font-weight: normal;color: grey;">${error.id.referencia}</td>
												<td class="literalborde" align="center" style="border: 0px;font-weight: normal;color: grey;">${error.entidadMediadora}-${error.subentidadMediadora}</td> 
												<td class="literalborde" align="center" style="border: 0px;font-weight: normal;color: grey;">${error.nifAsegurado}</td>
											</tr>
										</c:forEach>
									</table>
								</c:if>
								<c:if test="${mostrarErrNoCuenta eq true}">
									<table border=0>
										<tr class="literalborde">
											<td class="literalborde" align="left" style="border: 0px;color: red;">Pólizas erróneas por asegurado sin cuenta:</td>
										</tr>
									</table>
									<table border=0>
										<tr class="literalborde"  >						
											<td width="15%" class="literalborde" align=""right"">
											<td class="literalborde" align="center" style="border:none;border-bottom: #CCCC00 1px solid;">Referencia</td>
											<td class="literalborde" align="center" style="border:none;border-bottom: #CCCC00 1px solid;"><nobr>E-S Mediadora</nobr></td> 
											<td class="literalborde" align="center" style="border:none;border-bottom: #CCCC00 1px solid;">NIF/CIF/NIE</td>							 
										</tr>									
										<c:forEach var="error" items="${mapaListas.lstErroresNoCuenta}">																		
											<tr class="literalborde">
												<td width="15%" class="literalborde" align=""right"">
												<td align="center" class="literalborde" align="center" style="border: 0px;font-weight: normal;color: grey;">${error.id.referencia}</td>
												<td class="literalborde" align="center" style="border: 0px;font-weight: normal;color: grey;">${error.entidadMediadora}-${error.subentidadMediadora}</td> 
												<td class="literalborde" align="center" style="border: 0px;font-weight: normal;color: grey;">${error.nifAsegurado}</td>
											</tr>
										</c:forEach>
									</table>
								</c:if>
							</td>
						</tr>
					</tbody>
				</table>	
		</form:form>
	</div>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	

</body>
</html>