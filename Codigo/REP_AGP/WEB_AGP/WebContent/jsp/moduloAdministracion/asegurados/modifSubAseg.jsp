<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
<head>
	<title>Revisión de Subvenciones por modificación del Asegurado</title>
	
	<%@ include file="/jsp/common/static/metas.jsp"%>
	
	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
	
	<!-- JavaScript,jQery & AJAX -->
	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
	<script type="text/javascript" src="jsp/js/util.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
	<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
	<script type="text/javascript" src="jsp/js/calendar.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
	<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
	
	<script language="javascript">
		$(document).ready(function(){		
			<c:choose>
				<c:when test="${prof == 'alta'}">
					$('#profesional').attr('checked',true);	
				</c:when>
				<c:when test="${prof == 'baja'}">
					$('#profesional').attr('checked',false);	
				</c:when>
				<c:when test="${prof == null}">		
					$('#filaAgr').hide();
					$('#profesional').attr('disabled',true);		
					$('#profesional').hide();	
				</c:when>
			</c:choose>
			<c:choose>
				<c:when test="${joven == null || joven == ''}">
					$('#filaH').hide();
					$('#filaM').hide();
					$('#hombre').attr('disabled',true);
					$('#mujer').attr('disabled',true);	
					$('#hombre').hide();
					$('#mujer').hide();	
				</c:when>
				<c:when test="${joven == 'H'}">					
					$('#hombre').attr('checked',false);
					$('#filaM').hide();
					$('#mujer').attr('disabled',true);	
					$('#mujer').hide();			
				</c:when>
				<c:when test="${joven == 'M'}">
					$('#filaH').hide();
					$('#hombre').attr('disabled',true);
					$('#hombre').hide();
					$('#mujer').attr('checked',false);		
				</c:when>
			</c:choose>
		});
		
		function modificar(){
			$('#operacion').val('modifSubv');
			$('#profesional').attr('disabled',false);
			activados($("input[type=checkbox]"));  
			$('#main3').submit();
		}
		function activados(checks) {
     	  $('#seleccionados').val("");   		  		   			 	
     	  $('#noseleccionados').val("");  
     	  for(var i=0; i<checks.length; i++){ 
     	  	if(checks[i].checked)
			{
				document.getElementById('seleccionados').value += checks[i].value +",";  						
			}else{
				if(!checks[i].disabled){
					document.getElementById('noseleccionados').value += checks[i].value +",";  	
				}							
			}   	  	
     	  }  						
     	}		
	</script>
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
	<%@ include file="../../common/static/cabecera.jsp"%>
	<%@ include file="../../common/static/menuGeneral.jsp"%>
	<%@ include file="../../common/static/datosCabeceraUtil.jsp" %>		
	

<!-- Botones para alta, imprimir y volver -->
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">					
					<a class="bot" id="botonModif" href="javascript:modificar();">Modificar Subvenciones</a>						
				</td>
			</tr>
		</table>
	</div>
	
<!-- Contenido de la página -->
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Revisión de Subvenciones por modificación del Asegurado</p>
		<form name="main3" id="main3" action="asegurado.html" method="post">
			<input type="hidden" name="operacion" id="operacion"/>
			<input type="hidden" name="seleccionados" id="seleccionados" value=""/>
			<input type="hidden" name="noseleccionados" id="noseleccionados" value=""/>
			<input type="hidden" name="idAseg" id="idAseg" value="${aseguradoBean.id }"/>
			<div class="panel2Siniestro">
				<fieldset style="width:50%;">
					<legend>Subvenciones ENESA Y CCAA</legend>
					<table align="center" width="60%">
						<tr id="filaAgr">
							<td  class="literal">Agricultor/Ganadero Profesional (14%)</td>
							<td>
								<input type="checkbox" name="profesional" id="profesional"  value="20" class="dato" disabled="disabled" />
							</td>
						</tr>
						<tr id="filaH">
							<td class="literal">Joven Agricultor/Ganadero Hombre (14%)</td>
							<td>
								<input type="checkbox" name="hombre" id="hombre"  class="dato" value="10" />
							</td>
						</tr>
						<tr id="filaM">
							<td class="literal">Joven Agricultora/Ganadera Mujer (16%)</td>
							<td>
								<input type="checkbox" name="mujer" id="mujer"  class="dato" value="11" />
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
		</form>
	</div>
<%@ include file="/jsp/common/static/piePagina.jsp"%>
</body>
</html>