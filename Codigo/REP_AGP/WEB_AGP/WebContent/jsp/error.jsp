<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>


<html>
<script language=javascript type="text/javascript">

	function showdata(id){
	     ID = document.getElementById(id);
		    
	     if(ID.style.display == '') {
	          ID.style.display = 'none';
	     }
	     else {
	          ID.style.display = '';
	     }
	}
	
	function showdata(){
		ID = document.getElementById('textoError');
		var btnDetalle = document.getElementById('btnDetalle');
		if(ID.style.display == '') {
	          ID.style.display = 'none';
	          btnDetalle.text = "Ver detalle error";
	    }
	    else {
	          ID.style.display = '';
	          btnDetalle.text = "Ocultar detalle error";
	    }
	}

</script>
	<head>
		<title>Agroplus - Página de error</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	</head>
	<body bgcolor=#FFFFFF leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >
	<%@ include file="common/static/cabecera.jsp"%>
	<fmt:requestEncoding value="UTF-8"/>
	<form id="theForm" name="theForm" method="post" action="login.html">
		<input type="hidden" name="OP" value="ppal"/>
		<br/>
		<br/>
		<table width="75%" align="center">
			<tr>
				<td class="literal">
					<strong>Se ha producido un error controlado de la aplicaci&oacute;n:</strong> 
					<span class="detalnumNI"><c:out value="${requestScope.result.component}"/></span>
					<br/><br/>
					<c:if test="${requestScope.result.code != null}">
						<strong>Código: </strong> <span class="detalnumNI"><c:out value="${requestScope.result.code}" /></span>
					</c:if>
					<c:if test="${requestScope.result.message != null}">
						<strong>Mensaje: </strong> <span class="detalnumNI"><c:out value="${requestScope.result.message}" /></span>
					</c:if>
					<c:if test="${result.textoMensaje != null}">
						<br/>
						<div align="left" style="padding-left:7px;">
							<a class="bot" href="#" id="btnDetalle"  onclick="javascript:showdata(this);" >Ver detalle error</a>
						</div>
						<div id="textoError" style="display: none">
							<br/>
							<strong>Mensaje: </strong> <span class="detalnumNI"><c:out value="${result.textoMensaje}" /></span>
						</div>
					</c:if>
					<br/>
					<br/>
					<jsp:useBean id="now" class="java.util.Date" />
						Póngase en contacto con el administrador indicando el mensaje de error y la fecha y hora en la que se produjo (<fmt:formatDate value="${now}" pattern="EEEE, dd MMMM yyyy, HH:mm" />).
					<c:if test="${result.textoMensaje == null}">
						<br><br>Pulse <a href="#" onclick="theForm.submit();">aqu&iacute;</a> para volver.
					</c:if>
					<c:if test="${result.textoMensaje != null}">
						<br><br>Pulse <a href="#" onclick="top.window.close();">aqu&iacute;</a> para volver a iniciar sesi&oacute;n.
					</c:if>
				</td>
			</tr>
		</table>
		
	</form>

	</body>
</html>