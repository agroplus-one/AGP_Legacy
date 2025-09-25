<%@ include file="common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%> 



<html>
	<head>
		<title>Agroplus - Menú principal</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		
		<script type="text/javascript">
		function es_NTF(){
			var retorno=false;
			if (window.name == "NTF") {
				retorno=true;
			}        
			return retorno;
		}
		function salir(){
			if (es_NTF()) 
				alert("Acción bloqueada desde el Terminal Financiero"); 
			else 
				top.close();
		}
		</script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
	<%@ include file="common/static/cabecera.jsp"%>
	<%@ include file="common/static/menuGeneral.jsp"%>
	<%@ include file="common/static/datosCabecera.jsp"%>
	
	<!-- Mostrar al subir la petición de navegadores -->
	<!--  
	<table cellspacing="2" cellpadding="0" border="0" width="95%" style="display:none;">
		<tbody>
			<tr>
				<td class="literal" align="center" style="color:#5FB404">
					Ésta aplicacion está optimizada para una resolución de 1280x960 y para los siguientes navegadores:
				</td>
			
			</tr>
			<tr>
				<td class="literal" align="center" style="color:#5FB404" >
					<lu>
						<li>Internet explorer 11.0</li>
						<li>Mozilla Firefox 45.0</li>
						<li>Google Chrome 53.0</li>
					</lu>
				</td>
			</tr>
		</tbody>
	</table>
	-->
	<table cellspacing="2" cellpadding="0" border="0" width="95%">
		<tbody>
			<tr>
				<td class="literal" align="center" style="color:#5FB404;text-align:center">
					Esta aplicacion está optimizada para Google Chrome y una resolución de 1280x960<br/><br/>
					<c:if test="${not empty sessionScope.codTerminal}">Acceso desde Terminal Financiero: ${sessionScope.codTerminal}</c:if>
				</td>			
			</tr>
		</tbody>
	</table>
	
	
	<div style="display: none;">
		<p>Versi&oacute;n de compilaci&oacute;n: 26/07/2017 13:30</p>
	</div>
	
	
<%@ include file="common/static/piePagina.jsp"%>
	</body>
</html>