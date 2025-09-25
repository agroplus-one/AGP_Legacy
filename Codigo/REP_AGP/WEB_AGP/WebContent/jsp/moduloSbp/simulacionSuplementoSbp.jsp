

<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
	<head>
		<title>Suplemento de Sobreprecio</title>
		<%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />

		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/imprimir.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>	
		
		<script type="text/javascript">
		
		  $(document).ready(function(){
			    var URL = UTIL.antiCacheRand(document.getElementById('simulacionSbp').action);
			    document.getElementById('simulacionSbp').action = URL;
		  });
		  	
		  function grabDefinitivaSbp(){
			  	var frm = document.getElementById('simulacionSuplementoSbp');
				frm.method.value = 'doGrabacionDefinitiva';
				$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
     			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	    		$('#simulacionSuplementoSbp').submit();
		  }
		  
		  function limpiaAlertas() {
			$("#panelInformacion").hide();
			$("#panelAlertasValidacion").hide();	
			$("#panelAlertas").hide();
		  }
		  
		  function cambiarPrecioSbp(){
				var frm = document.getElementById('simulacionSuplementoSbp');
				frm.method.value ='doCambiarPrecio';	
				$('#simulacionSuplementoSbp').submit();		  
			  }
		  
		  function salir(){
	        	var frm = document.getElementById('simulacionSuplementoSbp');
		  		frm.method.value ='doSalir';
	        	$('#simulacionSuplementoSbp').submit();
		  }
		  
	    </script>
	</head>
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" 
		  onload="SwitchMenu('sub8'); ">

		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

		<div id="buttons">
			<table width="93%" cellspacing="0" cellpadding="0" border="0">
				<tr align="left">
					<td align="right">
							<c:if test="${btnDefinitiva}">
								<a class="bot" id="btnGrabDefinitiva"  href="javascript:grabDefinitivaSbp();">Grabación Definitiva</a>
							</c:if>
							<c:if test="${btnCambiarPrecio}">
								<a class="bot" id="btnCambiarPrecio"  href="javascript:cambiarPrecioSbp();">Cambiar Precio</a>
							</c:if>
							<a class="bot" id="btnSalir" href="javascript:salir();">Salir</a>
					</td>
				</tr>
			</table>
		</div>
		
		<!-- Contenido de la página -->
		<form name="simulacionSuplementoSbp" id="simulacionSuplementoSbp" action="suplementoSbp.html" method="post" commandName="polizaSbp">	
			<input type="hidden" name="method" id="method"/>
			<input type="hidden" name="idPolizaSbp" id="idPolizaSbp"/>
			
			<form:hidden path="polizaSbp.id" id="idPolSbp"/>
			<form:hidden path="polizaSbp.incSbpComp" id="incSbpComp"/>
			<form:hidden path="polizaSbp.polizaPpal.idpoliza" id="idPolizaPpal"/>	
			<form:hidden path="polizaSbp.polizaPpal.estadoPoliza.idestado" id="idEstadoPpal"/>
			
			<div class="conten" style="width:94%" align="center">
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				
				<p class="titapli1" align="center" >COTIZACIÓN DE SEGURO DE SOBREPRECIO</p>
				<p class="titulo" align="left">DESCRIPCION DEL RIESGO</p>
				<p align="left" class="literal">${descRiesgo}</p>
				
				<p class="titulo" align="left">SITUACION DEL RIESGO</p>
				<p align="left" class="literal">Parcelas relacionadas en la póliza de Agroseguro que se cita a continuación:<p>
				
				<table  width="60%" align="center">
					<tr align="left">
						<td class="literal">Asegurado....................... : </td>
						<c:choose>
    						<c:when test='${polizaSbp.polizaPpal.asegurado.tipoidentificacion == "NIF"}'>
    							<td class="literal">${polizaSbp.polizaPpal.asegurado.nombre} ${polizaSbp.polizaPpal.asegurado.apellido1} ${polizaSbp.polizaPpal.asegurado.apellido2}</td>
       						</c:when>
							<c:otherwise>
							        <td class="literal">${polizaSbp.polizaPpal.asegurado.razonsocial} </td>
							</c:otherwise>
						</c:choose>   
					</tr>
					<tr align="left">	
						<td class="literal" width="26%">Nif/Cif................................ : </td>
						<td class="literal">${polizaSbp.polizaPpal.asegurado.nifcif} </td>
					</tr>
					<tr align="left">	
						<td class="literal">Línea................................. : </td>
						<td class="literal">${polizaSbp.polizaPpal.linea.codlinea} -  ${polizaSbp.polizaPpal.linea.nomlinea}</td>
					</tr>
					<tr align="left">	
						<td class="literal">Plan................................... : </td>
						<td class="literal">${polizaSbp.polizaPpal.linea.codplan}<td>
					</tr>						
					<tr align="left">	
						<td class="literal">Número de Colectivo.... : </td>
						<td class="literal">${polizaSbp.polizaPpal.colectivo.idcolectivo} - ${polizaSbp.polizaPpal.colectivo.dc}
					</tr>
					<tr align="left">	
						<td class="literal">Número de declaración : </td>
						<c:if test="${polizaSbp.polizaPpal.referencia != null}">
							<td class="literal">${polizaSbp.polizaPpal.referencia} - ${polizaSbp.polizaPpal.dc}</td>
						</c:if>
					</tr>
					
				</table>
				<p class="titulo" align="left">SUMA ASEGURADA</p>
				<p align="left" class="literal"> La suma asegurada se corresponde con la diferencia en precio entre el precio asegurado por Agroseguro y el precio de mercado, con los
					límites detallados a continuación:</p>
				<table width="80%" align="center" border="1" CELLSPACING="0" cellpadding="0">	
					<tr align="center">
						<td class="literal" style="text-decoration:underline">CULTIVO</td>
						<td class="literal" style="text-decoration:underline">PROVINCIA</td>
						<td class="literal" style="text-decoration:underline">PRODUCCIÓN EN KG.</td>
						<td class="literal" style="text-decoration:underline">SOBREPRECIO ASEGURADO POR KG</td>
						<td class="literal" style="text-decoration:underline">SUMA ASEGURADA &nbsp;&#8364;</td>
					</tr>
						<c:set var="totalSumaAsegurada" value="0" /> 
						<c:forEach items="${parcelaSbpsMostrar}" var="parcela">
						<tr align="center">	
							<td class="literal" align="left">${parcela.cultivo.descultivo}</td>
							<td class="literal" align="left">${parcela.comarca.provincia.nomprovincia}</td>
							<td class="literal" align="center">${parcela.totalProduccion}</td>
            				<c:if test="${empty parcela.sobreprecio }">
	            				<td class="literal" align="center" style="color:red">0</td>
	            			</c:if>
	            			<c:if test="${not empty parcela.sobreprecio }">
								<td class="literal" align="center">${parcela.sobreprecio}</td>  
							</c:if>	
							<td class="literal" align="center">${parcela.totalProduccion * parcela.sobreprecio} </td>
						</tr>
						<c:set var="totalSumaAsegurada" value="${totalSumaAsegurada + (parcela.totalProduccion * parcela.sobreprecio)}" />	
						</c:forEach>
						<tr>
					<td colspan="4" class="titapli1" align="right">TOTAL SUMA ASEGURADA:&nbsp;&nbsp;</td>
					
					<td colspan="1" class="literal" align="center">${totalSumaAsegurada}</td>
				</table>
				<p class="titulo" align="left">RIESGOS GARANTIZADOS</p>
					<pre class="literal" style="text-align:left">${riesgoGarantizado}</pre>
					
				<p class="titulo" align="left">PERIODO DE CARENCIA</p>
				<p align="left" class="literal">${periodoCarencia}</p>
				<p class="titulo" align="left">FRANQUICIA</p>
				<p align="left" class="literal">${franquicia}<br><br>
					
					</p>
				<p class="titulo" align="left">PRECIO DEL SEGURO</p>
				<p align="left" class="literal"> ${primaTotal}&nbsp;<span id="imp" style="font-weight:bold;color: #000000;"> <td class="literal" align="center">${polizaSbp.importe} &#8364;</td></span>
				<p align="left" class="literal"> El presente documento no tiene valor contractual alguno.</p>
				<p align="left" class="literal"><span id="imp" style="font-weight:bold;color: #000000;"><u> En caso de conformidad con la presente oferta y emisión de la póliza, el Tomador/Asegurado acepta la emisión automática de suplementos  y regularización de prima, derivados de la modificación de la póliza base de Agroseguro.</p></u></span>
				<p align="left" class="literal"> Fecha:
				<script>
					var meses = new Array ("Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre");
					var f=new Date();
					document.write(f.getDate() + " de " + meses[f.getMonth()] + " de " + f.getFullYear());
				</script>
				</p>
			</div>
		</form>
		
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		
	</body>
</html>