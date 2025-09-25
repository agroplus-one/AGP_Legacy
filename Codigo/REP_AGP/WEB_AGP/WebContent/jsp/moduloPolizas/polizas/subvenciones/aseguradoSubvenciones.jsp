<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<fmt:bundle basename="displaytag" >
	<c:set var="numReg"><fmt:message key="numElementsPag"/></c:set>
</fmt:bundle>
<fmt:bundle basename="agp" >
	<c:set var="numEle"><fmt:message key="visores.numElements"/></c:set>
</fmt:bundle>

<html>
	<head>
		<title>Subvenciones de Asegurados</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		
		<script>
		    // Para evitar el cacheo de peticiones al servidor
	        $(document).ready(function(){
	            var URL = UTIL.antiCacheRand(document.getElementById("main").action);
		        document.getElementById("main").action = URL;
		        
		        var URL = UTIL.antiCacheRand(document.getElementById("frmcontinuar").action);
		        document.getElementById("frmcontinuar").action = URL;   

		        if(${subvencionesAsegurado.NoData}){
		        	$('#NoData').show();
		        	$('#tablaSubv').hide();
		        }
		        
		        $("input[type=checkbox]").each(function(){ 				 					        
					  if($('#modoLectura').val() == 'modoLectura'){
					       $(this).attr('disabled','disabled');
					  }
				});  
				
				var formCont = document.getElementById("frmcontinuar");
			    var formMain2 = document.getElementById("main"); 
			    
			    formCont.href = "aseguradoSubvencion.html?rand=" + UTIL.getRand();
			    formMain2.href = "seleccionPoliza.html?rand=" + UTIL.getRand();		
		        
	        });
		    
		    function desmarcarSubvencion(element) {
		    	var valor = element.value;
		    	
		    	if (valor == "10/E" || valor == "11/E" || valor == "20/E" || valor == "73/E" || valor == "30/E") {
			    	var answer = window.confirm("¿Desea desmarcar la subvención en la pantalla de 'Administración del Asegurado'?");
			    	if (answer) {
			    		var sSelec = document.getElementsByTagName("input");	
						var frm = document.getElementById('frmcontinuar');				
						
						for (var i = 0; i < sSelec.length; i++){
							if(sSelec.item(i).checked){
								frm.subsSeleccionadas.value += sSelec.item(i).value + ",";
							}					
						}
						frm.operacion.value = 'desmarcarSubvencion';
						frm.idCheck.value = valor;
						frm.submit();
			    	}
		    	}
		    }
		
			function continuar(){
				if($('#modoLectura').val() == 'modoLectura'){				
					if($('#esGanado').val() == 'true'){
						var frm = document.getElementById('frmcontinuar');
						frm.operacion.value = 'continuar';
						frm.submit();
					}else{
						var frm = document.getElementById('consultaDetallePoliza');
						frm.method.value = 'doVerImportes';
						frm.submit();
					}
				}else{			
					var sSelec = document.getElementsByTagName("input");	
					var frm = document.getElementById('frmcontinuar');				
					
					for (var i = 0; i < sSelec.length; i++){
						if(sSelec.item(i).checked){
							frm.subsSeleccionadas.value += sSelec.item(i).value + ",";
						}					
					}
					frm.operacion.value = 'continuar';
					$.blockUI.defaults.message = '<h4> Obteniendo comparativas.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	       			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } }); 
					frm.submit();
				}
			}
			
			function continuarCpl(){
				if($('#modoLectura').val() == 'modoLectura'){
					var frm = document.getElementById('consultaDetallePoliza');
					frm.method.value = 'doVerImportesCpl';
					frm.submit();
				}else{			
					var sSelec = document.getElementsByTagName("input");	
					var frm = document.getElementById('frmcontinuar');				
					
					for (var i = 0; i < sSelec.length; i++){
						if(sSelec.item(i).checked){
							frm.subsSeleccionadas.value += sSelec.item(i).value + ",";
						}					
					}
					frm.operacion.value = 'continuar';
					$.blockUI.defaults.message = '<h4> Validando las Pólizas.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	       			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } }); 
					frm.submit();
				}
			}
			
			function volver(){
				$('#operacion').val('listparcelas');
				$('#main').submit();
			}
			
			function irASocioSuvbencioens(idPoliza){
				$(location).attr('href', 'socioSubvencion.html?rand=' + UTIL.getRand() + '&idpoliza=' + $('#idpoliza').val() + '&operacion='+ '&vieneDeUtilidades=' + $('#vieneDeUtilidades').val() + '&modoLectura=' + $('#modoLectura').val());
			
			}
			function eligeMenu(){
				if($('#vieneDeUtilidades').val() == 'true'){
				 	SwitchMenu('sub4');
				 }else{
				 	SwitchMenu('sub3');
				 }
			 }
		</script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="eligeMenu();">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<c:choose>
			<c:when test="${subvencionesAsegurado.modoLectura == 'modoLectura' && subvencionesAsegurado.vieneDeUtilidades == 'true'}">
				<%@ include file="/jsp/common/static/datosCabeceraConsulta.jsp" %>	
			</c:when>
			<c:otherwise>
				<%@ include file="/jsp/common/static/datosCabecera.jsp"%>
			</c:otherwise>
		</c:choose>
		
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="right">
						<a class="bot" href="javascript:volver();">Volver</a>
						<c:if test="${subvencionesAsegurado.muestraBotonSocios}">
							<a class="bot" href="#" id="btnSubvSocios" onclick="javascript:irASocioSuvbencioens('${idpoliza}');">Subvenciones Socios</a>
						</c:if>
						
						<c:if test="${subvencionesAsegurado.modoLectura != 'true'}">
							<c:if test="${subvencionesAsegurado.esCpl != 'true'}">
						    	<a class="bot" href="javascript:continuar()">Continuar</a>
						    </c:if>
						    <c:if test="${subvencionesAsegurado.esCpl == 'true'}">
						    	<a class="bot" href="javascript:continuarCpl()">Continuar</a>
							</c:if>
						</c:if>
					</td>
				</tr>
			</table>
		</div>		
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%">
			<p class="titulopag" align="left">Subvenciones de los Asegurados</p>
			<form action="seleccionPoliza.html" method="post" name="main" id="main">
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<input type="hidden" name="operacion" id="operacion"/>
				<input type="hidden" name="subsSeleccionadas" />
				<input type="hidden" name="modoLectura" id="modoLectura" value="${subvencionesAsegurado.modoLectura }"/>
				<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${subvencionesAsegurado.vieneDeUtilidades}"/>
				<input type="hidden" name="idpoliza" id="idpoliza" value="${subvencionesAsegurado.idpoliza }"/>
				<input type="hidden" name="tieneCIF" id="tieneCIF" value="${subvencionesAsegurado.tieneCIF}"/>
			</form>
			
			<form action="aseguradoSubvencion.html" method="post" name="frmcontinuar" id="frmcontinuar">
				<input type="hidden" name="operacion" id="operacionVolver"/>
				<input type="hidden" name="idCheck" id="idCheck"/>
				<input type="hidden" name="subsSeleccionadasAnt" value="${subvencionesAsegurado.subsSeleccionadasAnt }"/>
				<input type="hidden" name="subsSeleccionadas" />
				<input type="hidden" name="modoLectura" id="modoLectura" value="${subvencionesAsegurado.modoLectura }"/>
				<input type="hidden" name="idpoliza" id="idpoliza" value="${subvencionesAsegurado.idpoliza}" />
				<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${subvencionesAsegurado.vieneDeUtilidades}"/>
				<input type="hidden" name="esGanado" id="esGanado" value="${subvencionesAsegurado.esGanado}"/>
			</form>
			<form name="consultaDetallePoliza" id="consultaDetallePoliza" action="consultaDetallePoliza.html" method="post">
				<input type="hidden" name="method" id="method"/>
				<input type="hidden" name="idpoliza" id="idpoliza" value="${subvencionesAsegurado.idpoliza}"/>
				<input type="hidden" name="modoLectura" id="modoLectura" value="${subvencionesAsegurado.modoLectura }"/>
				<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${subvencionesAsegurado.vieneDeUtilidades}"/>
			</form>
			
			<div class="panel2 isrt" style="width: 99%;" id="tablaSubv">
				${subvencionesAsegurado.tabla}						
			</div>
			<div id="NoData" style="display:none">
				No existen subvenciones disponibles
			</div>
			<br/>
		</div>
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
	</body>
</html>