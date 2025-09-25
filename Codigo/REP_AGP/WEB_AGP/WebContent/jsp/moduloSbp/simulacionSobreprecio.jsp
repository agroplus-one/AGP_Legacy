

<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
	<head>
		<title>Poliza de Sobreprecio</title>
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
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/pago/popupListadoAsegurados.js"></script>
		
		<script type="text/javascript">
		
		  $(document).ready(function(){
			    var URL = UTIL.antiCacheRand(document.getElementById('simulacionSbp').action);
			    document.getElementById('simulacionSbp').action = URL;
		  });
		  	
		  function grabProvisionalSbp(){
          	var frm = document.getElementById('simulacionSbp');
	   		frm.method.value ='doGrabacionProvisional';
	  		$('#simulacionSbp').submit();
		  }
		  	
		  function grabDefinitivaSbp(){
		  	if ($("#estadoPpal").val() == 8 || $("#estadoPpal").val() == 5 || $("#estadoPpal").val() == 3){
				if ($("#estadoPpal").val() == 8 &&
				 ($("#estadoCpl").val() == 1 || $("#estadoCpl").val() == 2) && $("#incSbpComp").val() == 'S'){
				 	if ($("#estadoCpl").val() == 1){
				 		var strEstadoCpl = "pendiente de Validación";
				 	}else if ($("#estadoCpl").val() == 2){
				 		var strEstadoCpl = "Grabación Provisional";
				 	}
					if (confirm('Póliza Complementaria con estado '+ strEstadoCpl + '. No es posible pasar a Grabación Definitiva,\n\ ¿desea recalcular el Sobreprecio sin Complementaria?')){
						// recalculo Sbp sin Cpl
						var frm = document.getElementById('simulacionSbp');
			    		frm.method.value ='doGrabacionDefinitivaSbpSinCpl';
						$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	       				$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		    			$('#simulacionSbp').submit();
					}
				} else { // pasar Sbp a definitiva
					var frm = document.getElementById('simulacionSbp');
					frm.method.value = 'doGrabacionDefinitiva';
					$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	       			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		    		$('#simulacionSbp').submit();
				}
		    } else { 	//estadoPpal != 8
		    	limpiaAlertas();
				$('#panelAlertasValidacion').html("No se puede pasar a Definitiva la póliza de Sobreprecio hasta que su principal esté en Definitiva, Enviada pendiente de confirmación o Enviada Correcta");
				$('#panelAlertasValidacion').show();
		    }
		  }
		  
		  function limpiaAlertas() {
			$("#panelInformacion").hide();
			$("#panelAlertasValidacion").hide();	
			$("#panelAlertas").hide();
		  }
		  
		  function salir(){
		  	if (${origenLlamada == 'seleccionPoliza'}){
		  		var frm = document.getElementById('simulacionSbp');
		  		frm.method.value ='doSalir';
	 			$("#simulacionSbp").submit();
	 		}else if (${origenLlamada == 'consultaPolizasParaSbp'}){
	 			$("#consultaPolizasParaSbp").submit();
	        }else if (${origenLlamada == 'utilidadesPoliza'}){
	 			$("#utilidadesSbp").submit();
	        }else{
	        	var frm = document.getElementById('simulacionSbp');
		  		frm.method.value ='doSalir';
			  	frm.idPolizaSbp.value = frm.idPolSbp.value;
	        	$('#simulacionSbp').submit();
	        }
		  }
		  
		  function imprimir(){
		  	var frm = document.getElementById('imprimirSbp');
		  	var frm2 = document.getElementById('simulacionSbp');
		  	frm.idPolizaSbp.value = frm2.idPolSbp.value;
		    frm.method.value ='doInformePolizaSbp';
		   	$('#imprimirSbp').attr('target', '_blank');
		    $('#imprimirSbp').submit();
		  }
		  
		  function cambiarPrecioSbp(){
			var frm = document.getElementById('simulacionSbp');
			frm.method.value ='doCambiarPrecio';	
			$('#simulacionSbp').submit();		  
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
						<a class="bot" id="btnImprimir"  href="javascript:imprimir();">Imprimir</a>
						<c:if test="${btnProvisional}">
							<a class="bot" id="btnGrabProvisional"  href="javascript:grabProvisionalSbp();">Grabación Provisional</a>
						</c:if>
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
		
		<form name="consultaPolizasParaSbp" id="consultaPolizasParaSbp" action="consultaPolSbp.run" method="post">
			<input type="hidden" name="method" id="method"/>
			<input type="hidden" name="recogerPolSesion" id="recogerPolSesion" value="true"/>
			<input type="hidden" name="idPolizaSbp" id="idPolizaSbp"/>
		</form>
		
		<form name="imprimirSbp" id="imprimirSbp" action="informes.html" method="post">
			<input type="hidden" name="method" id="method"/>
			<input type="hidden" name="idPolizaSbp" id="idPolizaSbp"/>
		</form>
		
		<form name="utilidadesSbp" id="utilidadesSbp" action="utilidadesPoliza.html" method="post">
			<input type="hidden" name="idPolizaSbp" id="idPolizaSbp"/>
			<input type="hidden" name="operacion" id="operacion" value="volver"/>
		</form>
		
		<!-- Contenido de la página -->
		<form name="simulacionSbp" id="simulacionSbp" action="simulacionSbp.html" method="post" commandName="polizaSbp">	
			<input type="hidden" name="method" id="method"/>
			<input type="hidden" name="estadoPpal" id="estadoPpal" value="${estadoPpal}"/>
			<input type="hidden" name="estadoCpl" id="estadoCpl" value="${estadoCpl}"/>
			<input type="hidden" name="origenLlamada" id="origenLlamada" value="${origenLlamada}"/>
			<input type="hidden" name="sbpRecalculada" id="sbpRecalculada" value="${sbpRecalculada}"/>
			<input type="hidden" name="idPolizaSbp" id="idPolizaSbp"/>
			<input type="hidden" name="origenLlamadaListPolSbp" id="origenLlamadaListPolSbp" value="${origenLlamadaListPolSbp}"/>
			
			<form:hidden path="polizaSbp.id" id="idPolSbp"/>
			<form:hidden path="polizaSbp.incSbpComp" id="incSbpComp"/>
			<form:hidden path="polizaSbp.polizaPpal.idpoliza" id="idPolizaPpal"/>	
			<form:hidden path="polizaSbp.polizaPpal.estadoPoliza.idestado" id="idEstadoPpal"/>
			
			<c:if test="${empty polizaSbp.polizaCpl}">
				<input type="hidden" id="idPolizaCpl" value=""/>
				<input type="hidden" id="idEstadoCpl" value=""/>
			</c:if>
			<c:if test="${not empty polizaSbp.polizaCpl}">
				<form:hidden path="polizaSbp.polizaCpl.idpoliza" id="idPolizaCpl"/>
				<form:hidden path="polizaSbp.polizaCpl.estadoPoliza.idestado" id="idEstadoCpl"/>
			</c:if>
			
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