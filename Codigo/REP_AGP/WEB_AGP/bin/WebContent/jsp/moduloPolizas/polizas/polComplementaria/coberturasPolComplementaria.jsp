<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
<head>
	<title>Datos Complementario</title>
	
	<%@ include file="/jsp/common/static/metas.jsp"%>
	
	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
	
	<!-- JavaScript,jQery & AJAX -->
	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
	<script type="text/javascript" src="jsp/js/util.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
	<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
	<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
	<script type="text/javascript" src="jsp/js/calendar.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
	
	<script language="javascript">
	
		$(document).ready(function(){
	            var URL = UTIL.antiCacheRand(document.getElementById("main").action);
		        document.getElementById("main").action = URL;    
	    });
	    
		function volver(){
			$('#method').val('doConsulta');
			$('#deCoberturas').val('deCoberturas');
			$('#main').submit();	
		}
		
		function showdata(id) {
			ID1 = document.getElementById(id+".1");
			ID2 = document.getElementById(id+".2");
			ID3 = document.getElementById(id);
			
			if(ID1.style.display == '') {
				ID1.style.display = 'none';
			}
			else {
				ID1.style.display = '';
			}
			if(ID2.style.display == '') {
				ID2.style.display = 'none';
			}
			else {
				ID2.style.display = '';
			}
			if(ID3.style.display == '') {
				ID3.style.display = 'none';
			}
			else {
				ID3.style.display = '';
			}
		}
		function getModulos(idDiv,idpoliza,idTabla){
			if(!document.getElementById(idTabla)){
				modulos(idDiv,idpoliza);
			}
		}
		function modulos(idDiv,idpoliza){
			$.ajax({
			    url: "coberturasController.html" ,
				data: "method=doDetalleModulo&idpoliza="+idpoliza+"&modoLectura"+$('#modoLecturaCpl').val(),
				async:true,
			    dataType: "json",
			    success: function(datos){		
			            $('#'+idDiv).html(datos.tabla);   		
			            $('#descPpal').html(datos.descripcion);   		
			            $('#descCpl').html(datos.descripcionCPL);   		
			    },
			    beforeSend: function(){
		           		$("#ajaxLoading_"+idDiv).show();
           		},
           		complete: function(){
           				$("#ajaxLoading_"+idDiv).hide();            					
           		},				           
			    type: "POST"
			});
		}
		
		
	</script>
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub3');">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">			
					<a class="bot" id="btnVolver"  href="javascript:volver();">Volver</a>				
				</td>
			</tr>
		</table>
	</div>
	<form name="main" action="polizaComplementaria.html" method="post" id="main">
		<input type="hidden" name="method" id="method" value=""/>	
		<input type="hidden" name="idpolizaPpal" id="idpolizaPpal" value="${polizaPpal.idpoliza }" />
		<input type="hidden" name="idpolizaCpl" id="idpolizaCpl" value="${polizaCpl.idpoliza }" />
		<input type="hidden" name="deCoberturas" id="deCoberturas" value="" />
		<input type="hidden" name="modoLecturaCpl" id="modoLecturaCpl" value="${modoLectura}"/>
		<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${vieneDeUtilidades}"/>
	</form>
	<form method="post" name="frmAux" id="frmAux">  
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Datos Complementario</p>
		<!-- Datos de la póliza -->
		<fieldset style="width:95%">
		<legend class="literal">Datos de la p&oacute;liza</legend>
			<table width="90%" align="center" cellspacing="2">
				<tr>
					<td class="literal" width="75px">Colectivo:</td>
 					<td width="300px" class="detalI" colspan="2">${polizaPpal.colectivo.idcolectivo } - ${polizaPpal.colectivo.nomcolectivo }</td> 
					<td class="literal" width="40px">Plan:</td>
					<td width="40px" class="detalI">${polizaPpal.linea.codplan}</td>
					<td class="literal" width="50px">Línea:</td>
					<td width="300px" class="detalI" colspan="2">${polizaPpal.linea.codlinea } - ${polizaPpal.linea.nomlinea}</td>
  					
				</tr>
				<tr>
					<td class="literal" width="75px">Asegurado:</td>
 					<td width="300px" class="detalI">${polizaPpal.asegurado.nombreCompleto }</td> 
 					<td class="literal" width="75px">CIF/NIF:</td>
 					<td width="80px" class="detalI">${polizaPpal.asegurado.nifcif }</td> 
					<td class="literal" width="40px">Póliza:</td>
					<td width="40px" class="detalI">${polizaPpal.referencia }</td>
					<td class="literal" width="100px">Módulo Ppal:</td>
					<td width="300px" class="detalI">${polizaPpal.codmodulo}</td>		
				</tr>
			</table>								
		</fieldset>
		<fieldset  style="width:95%">
				<legend class="literal">COBERTURAS PÓLIZA PRINCIPAL&nbsp;&nbsp;&nbsp;&nbsp; Módulo <span id="descPpal">${polizaPpal.codmodulo}</span></legend>
				<table width="100%" id="dataPpal.1">
					<tr>
						<td>
							<a href="#" onclick="javascript:showdata('dataPpal');getModulos('dataPpal',$('#idpolizaPpal').val(),'tablaPpal');" title="Mostrar condiciones de coberturas">
									<img src="jsp/img/folderclose.gif" alt="Desglose" title="Desglose" />&nbsp;&nbsp;Mostrar condiciones de coberturas
							</a> 
						</td>
					</tr>
				</table>
				<table width="100%" style="display:none" id="dataPpal.2">
					<tr>
						<td>
							<a href="#" onclick="javascript:showdata('dataPpal');" title="Ocultar condiciones de coberturas">
								<img src="jsp/img/folderopen.gif" alt="Desglose" title="Desglose" />&nbsp;&nbsp;Ocultar condiciones de coberturas
							</a> 
						</td>
					</tr>
				</table>				
				<div id="dataPpal"  style="display:none">
					<img id="ajaxLoading_dataPpal" src="jsp/img/cargando.gif" width="70%" style="cursor:hand;cursor:pointer;display:none" />	
				</div>
		</fieldset>
		<fieldset  style="width:95%">
				<legend class="literal">COBERTURAS PÓLIZA COMPLEMENTARIA &nbsp;&nbsp;&nbsp;&nbsp; Módulo <span id="descCpl">${polizaCpl.codmodulo}</span></legend>
				<table width="100%" id="dataCpl.1">
					<tr>
						<td><a href="#" onclick="javascript:showdata('dataCpl');getModulos('dataCpl',$('#idpolizaCpl').val(),'tablaCpl');" title="Mostrar condiciones de coberturas">
								<img src="jsp/img/folderclose.gif" alt="Desglose" title="Desglose" />&nbsp;&nbsp;Mostrar condiciones de coberturas
							</a> 
						</td>
					</tr>
				</table>
				<table width="100%" style="display:none" id="dataCpl.2">
					<tr>
						<td>
							<a href="#" onclick="javascript:showdata('dataCpl');" title="Ocultar condiciones de coberturas">
								<img src="jsp/img/folderopen.gif" alt="Desglose" title="Desglose" />&nbsp;&nbsp;Ocultar condiciones de coberturas
							</a> 
						</td>
					</tr>
				</table>
				<div id="dataCpl" style="display:none">
					<img id="ajaxLoading_dataCpl" src="jsp/img/cargando.gif" width="70%" style="cursor:hand;cursor:pointer;display:none" />	
				</div>
		</fieldset>
	</div>
	</form>
</body>
</html>