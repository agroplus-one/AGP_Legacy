<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", 0);
%>

<html>
<head>
	<title>Lista de recibos</title>
	
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
	<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
	<script type="text/javascript" src="jsp/js/calendar.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
	<script type="text/javascript" src="jsp/moduloUtilidades/recibos/consultaRecibosPoliza.js" ></script>
	<%@ include file="/jsp/js/draggable.jsp"%>
	
</head>

<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4');">
	<%@ include file="../../common/static/cabecera.jsp"%>
	<%@ include file="../../common/static/menuGeneral.jsp"%>
	<%@ include file="../../common/static/datosCabeceraTaller.jsp" %>		
	
	
	<!-- Botones para alta, imprimir y volver -->
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right" width="100%">
					<a class="bot" id="btnBorrador" href="javascript:imprimirBorradorReducido(false);">Ver borrador</a>
					<a class="bot" id="btnBorradorReducido" href="javascript:imprimirBorradorReducido(true);">Ver borrador Reducido</a>
					<a class="bot" id="btnPolizaOrigen" href="javascript:imprimirPolizaOrigen();">Ver p&oacute;liza origen</a>
					<a class="bot" href="javascript:consultar();">Consultar</a>
					<a class="bot" href="javascript:limpiar();">Limpiar</a>
					<a class="bot" href="javascript:volver()">Volver</a>
				</td>
			</tr>
		</table>
	</div>

	
	
	<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%" >
		<p class="titulopag" align="left">Lista de Recibos</p>			
		<form name="print" id="print" action="informes.html" method="post">
			<input type="hidden" name="method" id="method"/>
			<input type="hidden" name="idReciboPoliza" id="idReciboPoliza"/>
			<input type="hidden" name="StrImprimirReducida" id="StrImprimirReducida"/>
			<input type="hidden" name="idPoliza" id="idPolizaImprimir"  value="${idPoliza}"/>
		</form>	
			
		<form:form name="main3" id="main3" action="recibosPoliza.html" method="post" commandName="reciboPolizaBean" >
		
			<input type="hidden" id="method" name="method" />
			<form:hidden path="id" id="id"/>
			<input type="hidden" name="idPoliza" id="idPoliza"  value="${idPoliza}"/>
			<input type="hidden" name="tipoRef" id="tipoRef"  value="${tipoRef}"/>
			
			    <%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div class="panel2 isrt" style="width:85%">
					<fieldset>
					<legend class="literal">Datos de la póliza</legend>
						<table  style="width:80%" align="center">
							<tr align="left" >
								<td class="literal" >Plan: </td>
								<td class="detalI">
									${reciboPolizaBean.recibo.codplan}
								</td>
								<td class="literal" >L&iacute;nea: </td>
								<td class="detalI">
									${reciboPolizaBean.recibo.codlinea} 
								</td>
								<td class="literal">Colectivo: </td>
								<td class="detalI">
									${reciboPolizaBean.recibo.refcolectivo}-${reciboPolizaBean.recibo.dccolectivo} 
								</td>
								<td class="literal" >Póliza: </td>
								<td class="detalI">
									${reciboPolizaBean.refpoliza} 
								</td>
							</tr>
							<tr align="left">	
								<td class="literal">CIF/NIF: </td>
								<td class="detalI">
									${reciboPolizaBean.nifaseg} 
								</td>			
								<td class="literal">Nombre Asegurado: </td>
								<td class="detalI">
									${reciboPolizaBean.nombreaseg} 
								</td>
							</tr>
						</table>
					</fieldset>
					<fieldset>
					<legend class="literal">Filtro</legend>
						<table style="width:100%" align="center">
							<tr>						
								<td class="literal">Fase</td>
								<td>
									<form:input id="fase" path="recibo.codfase" size="4" maxlength="4" cssClass="dato"/>
								</td>
								<td class="literal">Fecha Emisión</td>
								<td>							
									<spring:bind path="recibo.fecemisionrecibo">							
											<input type="text" name="recibo.fecemisionrecibo"  id="fechaemision" size="11" maxlength="10" class="dato" 
													value="<fmt:formatDate pattern="dd/MM/yyyy" value="${reciboPolizaBean.recibo.fecemisionrecibo}"/>"/>
									</spring:bind>
									<input type="button" id="btn_fecha_emision" name="btn_fecha_emision" class="miniCalendario" style="cursor: pointer;"/>							
								</td>
								<td class="literal">Nº Recibo</td>
								<td>
									<form:input id="nrecibo" path="recibo.codrecibo" size="7" maxlength="7" cssClass="dato"/>
								</td>
								<td>
									<td class="literal">Tipificación de Recibo</td>
									<td>
									<form:select path="recibo.tipificacionRecibos.tipificacionRecibo" id="codTipificacionRecibo" cssClass="dato"  cssStyle="width:235px">								
										<form:option value=""></form:option>	
										<c:forEach items="${listTipRecibos}" var="tipRe">
											<form:option value="${tipRe.tipificacionRecibo}">${tipRe.tipificacionRecibo} - ${tipRe.descripcion}</form:option>
										</c:forEach>								
									</form:select>
									</td>
								</td>
							</tr>
								
						</table>
					</fieldset>
					
				</div>
			</form:form>
	
	
	<div id="grid" style="width: 98%;">
		 <form name="list" id="list">
	         <display:table requestURI="recibosPoliza.html" id="listaRecibosPoliza"
	        	class="LISTA"
	        	summary="Recibos" 
	        	name="${listaRecibosPoliza}"
	        	sort="list"
	        	pagesize="10"
	        	decorator="com.rsi.agp.core.decorators.ModelTableDecoratorRecibosPoliza"
	        	style="width:100%;border-collapse:collapse;" 
	        	excludedParams="method" >
	        
	            <display:column class="literal" headerClass="cblistaImg" title="Acciones" property="acciones" style="width:40px;text-align:center"/>
	       		<display:column class="literal" headerClass="cblistaImg" title="Fase" property="fase" style="width:5px;text-align:center"/>
	       		<display:column class="literal" headerClass="cblistaImg" title="Fecha Emsión" format="{0,date,dd/MM/yyyy}"	 property="fechaEmsion" style="width:40px;text-align:center"/>
	       		<display:column class="literal" headerClass="cblistaImg" title="Nº Recibo" property="recibo" style="width:50px;text-align:center"/>
	       		<display:column class="literal" headerClass="cblistaImg" title="Tipificación Recibo" property="tipificacionRecibo" style="width:100px;text-align:center"/>
	       		<display:column class="literal" headerClass="cblistaImg" title="Plan" property="plan" style="width:5px;text-align:center"/>
	       		<display:column class="literal" headerClass="cblistaImg" title="Línea" property="linea" style="width:5px;text-align:center"/>
	       		<display:column class="literal" headerClass="cblistaImg" title="Colectivo" property="colectivo" style="width:65px;text-align:center"/>
	       		<display:column class="literal" headerClass="cblistaImg" title="Póliza" property="poliza" style="width:20px;text-align:center"/>
	       		<display:column class="literal" headerClass="cblistaImg" title="CIF/NIF Aseg." property="CIFNIF" style="width:70px;text-align:center"/>
	       		<display:column class="literal" headerClass="cblistaImg" title="Nombre Aseg." property="nombre" style="width:160px;text-align:center"/>
	       		
	        </display:table>
        </form>				
	</div>	
		
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
</body>
</html>