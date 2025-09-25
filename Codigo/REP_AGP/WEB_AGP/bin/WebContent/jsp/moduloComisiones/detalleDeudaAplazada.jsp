<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
<head>
	<title>Agroplus - Detalle Deuda Aplazada</title>
		
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
		
	        function onInvokeAction(id) {
				var to=document.getElementById("adviceFilter");
				to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
			    $.jmesa.setExportToLimit(id, '');
			    var parameterString = $.jmesa.createParameterStringForLimit(id);
			    var frm = document.getElementById('main1');
			    $.get('${pageContext.request.contextPath}/ficheroDeudaAplazada.run?ajax=true&idFichero='+$("#idFichero").val() +'&' + parameterString, function(data) {
			        $("#grid").html(data)
		  			});
			}
		    
		   
		    function volver(){
		    	var frm = document.getElementById('main1');
		    	frm.method.value="doVolver";
		    	$('#main1').submit();
		    }
		    function consultar (idDeudaAplazada){
		    	var frm = document.getElementById('main2');
		    	frm.method.value="doConsulta";
		    	frm.idDeudaAplazada.value= idDeudaAplazada;
		    	$('#main2').submit();
		    
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
			<p class="titulopag" align="left">Detalle Fichero Deuda Aplazada</p>			
			
			<form name="main2" id="main2" action="ficheroDeudaAplazadaApli.run" method="post" >
				<input type="hidden" name="method" id="method"/>
				<input type="hidden" name="origenLlamada" id="origenLlamada" value="detalleComisiones"/>
				<input type="hidden" name="idDeudaAplazada" id="idDeudaAplazada" />
				<input type="hidden" name="idFichero" id ="idFichero" value="${idFichero}"/>
				<input type="hidden" name="nombreFichero" id ="nombreFichero" value="${deudaAplazadaBean.fichero.nombrefichero}"/>
				<input type="hidden" name="estado" id ="estado" value="${estado}"/>
				<input type="hidden" name="fase" id ="fase" value="${deudaAplazadaBean.fichero.fase.fase}"/>
			</form>
			<form:form name="main1" id="main1" action="ficheroDeudaAplazada.run" method="post"  commandName="deudaAplazadaBean">
				<input type="hidden" name="method" id="method" />	
				<input type="hidden" name="idFichero" id ="idFichero" value="${idFichero}"/>
				
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				
				<div class="panel2 isrt" style="width: 90%;" id="divDatosComunes">
						<fieldset><legend class="literal">Datos Fichero</legend>
							<table align="center">
				
								<td class="literal">Fichero:</td>
									<td width="350px" class="detalI">${deudaAplazadaBean.fichero.nombrefichero}</td>
								<td class="literal">Estado:</td>
									<td width="350px" class="detalI">${estado}</td>
								<td class="literal">Fase:</td>
									<td width="350px" class="detalI">${deudaAplazadaBean.fichero.fase.fase}</td>
				
							</table>
						</fieldset>
					</div>
			</form:form>
			
			<div id="grid" style="width: 90%">
			  	${listadoDeudaAplazada}		  							               
			</div> 	
		
		
		</div> 
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
		
		

</body>
</html>