<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
	<head>
		<title>Selección de Precios</title>
		<%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />

		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/imprimir.js"></script>
		
	</head>
	
	<script type="text/javascript">
		
		  $(document).ready(function(){
			    var URL = UTIL.antiCacheRand(document.getElementById('seleccionPrecios').action);
			    document.getElementById('seleccionPrecios').action = URL;
			    
				$('#seleccionPrecios').validate({
				
					 errorLabelContainer: "#panelAlertasValidacion",
   					 wrapper: "li",
					 onfocusout: false, 
					 rules: {
					 	
					 },
					 messages: {
					 	
					 }
				}); 
				jQuery.validator.addMethod("betweenPrecio", function(value, element, params) { 
 					alert(value);
 					alert(element);
 					alert(params);
 					//return (this.optional(element) || (parseFloat($('#'+params[0]).val()) <= parseFloat(element.value) && parseFloat(element.value) <= parseFloat($('#'+params[1]).val())));
				});
		  });
		  
				
		  function continuar (){
		  
		  	var cancelSubmit = false;
		  	// valida que el sbp este entre el minimo y el maximo
			$("input[type=text]").each(function() {
				if($(this).attr('id').indexOf('sbp_') == 0){		        	
		        	var sbp = $(this).attr('id');
		        	var value = document.getElementById(sbp).value;
		        	sbp = sbp.split('_');
		        	idPrecio = sbp[1]+"_"+sbp[2];
		        	var limite = document.getElementById("precio_"+idPrecio).innerHTML;
			        limite = limite.split ('-');			        			       
		 			var precioMin = limite[0].substring(1,limite[0].length);
		 			var precioMax = limite[1].substring(0,limite[1].length-2);		 					 			 
					//value = value.replace(",",".");
	 				//precioMin = precioMin.replace(",",".");
	 				//precioMax = precioMax.replace(",",".");
		 			if (parseFloat(value) > parseFloat(precioMax) || parseFloat(value) < parseFloat (precioMin)){
						$('#panelAlertasValidacion').html("El sobreprecio debe estar comprendido entre el precio mínimo y el máximo");
						$('#panelAlertasValidacion').show();
						cancelSubmit = true;
					}	
		        }    
			});
			// valida que no haya 2 cultivos con sbp diferente
			var arrayCult = new Array();
			$("input[type=hidden]").each(function() {
				if($(this).attr('id').indexOf('cultivo_') == 0){
					var culProv = $(this).attr('id');
					culProv = culProv.split ('_');
					var cultivo = culProv[2];
					var provincia = culProv[1];
					var idPrecio = provincia+"_"+cultivo;
		        	var precio = document.getElementById("sbp_"+idPrecio).value;
		        	//alert("cultivo" + cultivo);
		        	//	alert("precio" + precio);
		        	if (arrayCult.length == 0){
		        		
						arrayCult[cultivo] = precio;
					}else{
						for (var k in arrayCult) {
							//alert("arrayCult[cultivo] : " +arrayCult[cultivo]+ " cultivo: "+ cultivo +"precio  : " +precio);
							if (arrayCult.hasOwnProperty(cultivo)) {
						       if (arrayCult[cultivo] != precio){
						      	//alert("error");
						      	 $('#panelAlertasValidacion').html("El sobreprecio debe ser igual para el mismo cultivo");
							   	 $('#panelAlertasValidacion').show();
							   	 cancelSubmit = true;
							   	 return false
							   }else{
							   	  arrayCult[cultivo] = precio;
							   }
						    }
						}
					}
				}	
			});
			if (cancelSubmit == false){
		    	$('#method').val('doContinuar');
		  		$('#seleccionPrecios').submit();
		  	}
		  }
		  
		
		  
	</script>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" 
		  onload="SwitchMenu('sub8'); ">

		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
		<div id="buttons">
			<table width="93%" cellspacing="0" cellpadding="0" border="0">
				<tr align="left">
					<td align="right">
						<a class="bot" id="btnAceptar" href="javascript:continuar();">Continuar</a>
					</td>
				</tr>
			</table>
		</div>	
		<div align="center">
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
		<div class="conten" style="padding:3px;width:97%">
		
			<p class="titulopag" align="left">Selección de Precios</p>
			<c:if test="${altaSuplemento}"  >
				<form name="seleccionPrecios" id="seleccionPrecios" action="suplementoSbp.html" method="post" commandName="polizaSbp">
			</c:if>
			<c:if test="${not altaSuplemento}"  >
				<form name="seleccionPrecios" id="seleccionPrecios" action="simulacionSbp.html" method="post" commandName="polizaSbp">
			</c:if>
				<input type="hidden" name="method" id="method"/>
				<form:hidden path="polizaSbp.id" id="idPolSbp"/>
				<form:hidden path="polizaSbp.polizaPpal.idpoliza" id="idPolizaPpal"/>	
				<form:hidden path="polizaSbp.polizaCpl.idpoliza" id="idPolizaCpl"/>
				<form:hidden path="polizaSbp.polizaPpal.estadoPoliza.idestado" id="idEstadoPpal"/>	
				<form:hidden path="polizaSbp.polizaCpl.estadoPoliza.idestado" id="idEstadoCpl"/>
				<form:hidden path="polizaSbp.incSbpComp" id="incSbpComp"/>
				<input type="hidden" name="origenLlamada" id="origenLlamada" value="${origenLlamada}"/>
				<input type="hidden" name="lstCodComarcaStr" id="lstCodComarcaStr" value="${lstCodComarcaStr}" />

				<table width="60%" align="center" border="1" CELLSPACING="0" cellpadding="0">	
						<tr align="center">
							<td class="literal" style="text-decoration:underline">CULTIVO</td>
							<td class="literal" style="text-decoration:underline">PROVINCIA</td>
							<td class="literal" style="text-decoration:underline">SOBREPRECIO ASEGURADO POR KG</td>
							<td class="literal" style="text-decoration:underline">LÍMITES PRECIO</td>
						</tr>
							 
							<c:forEach items="${listaSobreprecios}" var="sbp" varStatus="status" >
								<c:set var="codProv" value="${sbp.provincia.codprovincia}" />
								<c:if test="${empty codProv}"  >
									<c:set var="codProv" value="99" />
								</c:if>
								<c:set var="codCult" value="${sbp.cultivo.id.codcultivo}" />
								<tr align="center">	
									<td class="literal" align="center">${sbp.cultivo.descultivo}</td>
									<td class="literal" align="center">${sbp.provincia.nomprovincia}</td>
									<td class="literal" align="center">
									<c:if test="${empty sbp.sbpAsegurado}">
			            				<input type="text" id ="sbp_${codProv}_${codCult}" name="sbp_${codProv}_${codCult}" class="dato" 
											value="${sbp.precioMaximo}">
										
			            			</c:if>
			            			<c:if test="${not empty sbp.sbpAsegurado}">
			            				<input type="text" id ="sbp_${codProv}_${codCult}" name="sbp_${codProv}_${codCult}" class="dato" 
											value="${sbp.sbpAsegurado}">
										
			            			</c:if>
										
									</td>
									<input type="hidden" name="cultivo__${codProv}_${codCult}" id="cultivo_${codProv}_${codCult}" value="cultivo_${codProv}_${codCult}"/>
		            				<td class="literal" align="center" id="precio_${codProv}_${codCult}" name="precio_${codProv}_${codCult}" >(${sbp.precioMinimo}-${sbp.precioMaximo}) 
		            				</td>
		            				
		            				
								</tr>
							</c:forEach>
					</table>
			</form>
		</div>
		</div>
	</body>
</html>	