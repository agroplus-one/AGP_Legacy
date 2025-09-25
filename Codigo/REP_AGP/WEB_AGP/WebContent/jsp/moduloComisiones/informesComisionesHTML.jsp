<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<html>
	<head>
		<title>Consultas de Comisiones</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.progressbar.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.progressbar.min.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
		<script type="text/javascript" src="jsp/js/calendar.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.filestyle.js"></script>	
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
		<script type="text/javascript" src="jsp/js/ajaxfileupload.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
		<script type="text/javascript" src="jsp/moduloComisiones/informesComisionesHTML.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
		
	</head>
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
	
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:100%" >
		<p class="titulopag" align="center">Consultas de Comisiones</p>	
		
 			<form:form name="main3" id="main3" action="informesComisiones.run" method="post" commandName="informeComisionesBean"> 
				<form:hidden path="campoOrdenar" id="campoOrdenar" />
				<form:hidden path="sentido" id="sentido" />
				<form:hidden path="formato" id="formato" />
				<form:hidden path="datosDe" id="datosDe" />
				
				<input type="hidden" name="method" id="method" value="" />
				<input type="hidden" name="stringCabeceras" id="stringCabeceras" value=""/>
				<input type="hidden" name="stringRegistro" id="stringRegistro" value=""/>

				<table border="1">
					<tr>
						<c:forEach items="${listadoCabecera}" var="cabecera" varStatus="loopCount" >
							<script type="text/javascript">rellenaStringCabeceras('${listadoCabeceraNombre[loopCount.count-1]}');</script>	
							<td class="literalBordeCabecera" onclick="ordenacion('${cabecera}', '${sentido}');" onmouseover="this.style.cursor='pointer';">
								${cabecera}&nbsp;&nbsp;								
								<c:if test="${cabecera eq campoOrdenar and sentido eq 'ASCENDENTE'}" >
									<img id="imgOrden" src="jsp/img/displaytag/arrow_up.png" />
								</c:if>
								<c:if test="${cabecera eq campoOrdenar && sentido eq 'DESCENDENTE'}" >
									<img id="imgOrden" src="jsp/img/displaytag/arrow_down.png" />
								</c:if>
								<c:if test="${(cabecera != campoOrdenar) || (cabecera eq campoOrdenar && sentido eq '')}" >
									<img id="imgOrden" src="jsp/img/displaytag/arrow_off.png" />
								</c:if>
								
							</td>
						</c:forEach>
					</tr>
					<c:forEach items="${listadoInforme}" var="registro" varStatus="loopCount">
							<c:choose>
								<c:when test="${loopCount.count eq fn:length(listadoInforme)}">
									<tr style="background-color:#e5e5e5;">
								</c:when>	
								<c:otherwise>	
									<tr>
								</c:otherwise>	
							</c:choose>	
										<c:forEach items="${registro}" var="datos" varStatus="i">
											<c:choose>
												<c:when test="${empty datos}">
													<td class="detalI">&nbsp;</td>
												</c:when>	
												<c:otherwise>
													<c:choose>
														<c:when test="${(informeComisionesBean.datosDe eq 'resumen') and (loopCount.count != fn:length(listadoInforme))}">
															<c:choose>
																<c:when test="${formatoCom[i.count-1] eq '6'}" >
																	<td class="detalI" id="${listadoCabeceraNombre[i.count -1]}_${loopCount.count}" style="text-decoration: underline;cursor:pointer;text-align:right;" onclick="obtenerRegistro('${loopCount.count}')">${datos}</td>
																</c:when>
																<c:when test="${formatoCom[i.count-1] eq '7'}" >
																	<td class="detalI" id="${listadoCabeceraNombre[i.count -1]}_${loopCount.count}" style="text-decoration: underline;cursor:pointer;text-align:right;" onclick="obtenerRegistro('${loopCount.count}')">${datos}</td>
																</c:when>
																<c:otherwise>
																	<td class="detalI" id="${listadoCabeceraNombre[i.count -1]}_${loopCount.count}" style="text-decoration: underline;cursor:pointer" onclick="obtenerRegistro('${loopCount.count}')">${datos}</td>
																</c:otherwise>
															</c:choose>	
														</c:when>	
														<c:otherwise>
															<c:choose>
																<c:when test="${formatoCom[i.count-1] eq '6'}" >
																    <td class="detalI" style="text-align:right;">${datos}</td>					
																</c:when>
																<c:when test="${formatoCom[i.count-1] eq '7'}" >
																    <td class="detalI" style="text-align:right;">${datos}</td>					
																</c:when>	
																<c:otherwise>
																	<td class="detalI">${datos}</td>
																</c:otherwise>
															</c:choose>	
														</c:otherwise>
													</c:choose>	
												</c:otherwise>	
											</c:choose>	
										</c:forEach>
									</tr>		
					</c:forEach>
				</table>
				
			 </form:form>  		 

		</div>
	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/moduloComisiones/popupDetalleComisiones.jsp"%>
	
</body>
</html>