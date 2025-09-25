<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
	<head>
		<title>Elección de modulos</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />

		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>	
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/cuadroCoberturas.js"></script>
		<%@ include file="/jsp/js/draggable.jsp"%>		
		
		<script type="text/javascript">
		 $(document).ready(function(){
		 		var URL = UTIL.antiCacheRand(document.getElementById("frmAux").action);
			    document.getElementById("frmAux").action = URL;    
			     
				$("input[type=checkbox]").each(function(){ 				 					        
				     if($('#lectura').val() == 'modoLectura'){
				        	$(this).attr('disabled','disabled');
				     }
				 }); 
				
				$('#frmAux').validate({
					 errorLabelContainer: "#panelAlertasValidacion",
   					 wrapper: "li",
   					 highlight: function(element, errorClass) {
   						if(!checksRellenos()) $(".campoObligatorio").show();   						
   						<!-- Sólo se valida el combo de renovables si la línea es de ganado -->
   						<c:if test="${detCoberturas.esLineaGanado}">
	  						if(!renovablesElegidos()) $(".campoObligatorioRenov").show();
	  					</c:if>
  					 },
  					 unhighlight: function(element, errorClass) {
  						if(checksRellenos()) $(".campoObligatorio").hide();
  						<!-- Sólo se valida el combo de renovables si la línea es de ganado -->
  						<c:if test="${detCoberturas.esLineaGanado}">
   							if(renovablesElegidos()) $(".campoObligatorioRenov").hide();
   						</c:if>
  					 },
   					 rules: {
	   					 "modSelected": {checksRellenos: true}
  					 	 <!-- Sólo se valida el combo de renovables si la línea es de ganado -->
  					 	 <c:if test="${detCoberturas.esLineaGanado}">
	  					 	, "modRenovable": {renovablesElegidos: true}
	  					 </c:if>
   					 }		
  					   					  					
  					 
   				});
   				// set labels periodo contratación
   				
   		 });		
     	
     	jQuery.validator.addMethod("checksRellenos",checksRellenos,"Debe seleccionar al menos una opción");
     	jQuery.validator.addMethod("renovablesElegidos",renovablesElegidos,"Debe indicar si la póliza es renovable o no");
		
		function checksRellenos() 
		{ 
			 var checksRellenos = $("(input:checkbox):checked");
     		 return checksRellenos.length > 0;
     	}    
		
		/*
		Comprueba si para todos los módulos elegidos se ha indicado si es renovable o no
		*/
		function renovablesElegidos () {
			var checksRellenos = $("(input:checkbox):checked");
			
			// Si no hay ningún módulo seleccionado no se valida el combo de renovables
			if (checksRellenos.length == 0) {
				return true;
			}
			
			for (i=0; i<checksRellenos.length; i++) {
				// Obtiene valor del combo asociado al actual check marcado
				var combo = $("select[id='modRenovable_"+checksRellenos[i].value+"']").val();
				
				// Si este valor no es 0 o 1, no se supera la validación
				if (combo != '0' && combo != '1') {
					return false
				}
			}
			
    		return true;
		}
		
		/*** DNF 20/07/2020 PET-63485.DAVID **********/
		/*Desviamos el flujo para que no pase por las comparativas y se vaya directamente a parcelas*/
		function continuarParcelas(){
			
			activados($("(input:checkbox):checked")); 
			
     	    if($('#tieneParcelas').val() == "si" && $('#modoLectura').val() != 'modoLectura'){
     	    	$('#overlay').show();
     	    	$("#popupRecalcular").show();
     	    }else{
     	    	//continuarPar("no");
     	    	continuar("no");
     	    }
     	}
     	
		//function continuarPar(recalcular) {
		function continuar(recalcular) {
            $("#popupRecalcular").hide();
            $('#overlay').hide();
            $("#recalcular").val(recalcular);

			if($("#frmAux").valid())
			{    
				$.blockUI.defaults.message = '<h4> Redirigiendo.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
				$('#action').val("seleccionParc");
				activados($("(input:checkbox):checked"));  
				$('#frmSINeleccomparativa').submit();	
			}					   			 	
     	}
		/*** FIN DNF 20/07/2020 PET-63485.DAVID **********/
		
		function continuarWrapper(tieneCoberturas)
     	{
     	    if(tieneCoberturas == "false" && $('#tieneParcelas').val() == "si" && $('#lectura').val() != 'modoLectura'){
     	    	$.unblockUI();
     	    	$('#overlay').show();
     	    	$("#popupRecalcular").show();
     	    }else{
     	    	continuar("no");
     	    }
     	}
     	    
     	function continuarCAMBIO(recalcular)
     	{
     	    $("#popupRecalcular").hide();
            $('#overlay').hide();
            $("#recalcular").val(recalcular);

			if (recalcular == "si"){
				//Cuando recalcular es si, ponemos de nuevo la capa (se quitó para poder responder a la pregunta)
            	$.blockUI.defaults.message = '<h4> Solicitando datos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	       		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	       	}

	     	if($("#frmAux").valid())
	     	{ 
				$('#action').val("comparativa");
				//$('#frmSINeleccomparativa').submit();
				$('#main').submit();
			}
     	}
     	
     	function cerrarPopUp()
     	{
			    $('#panelInfo').hide();
			    $('#overlay').hide();
		 }
     	
     	function volverModulos(){
				$('#operacionVolver').val('listparcelas');
				$('#frmlistpolizas').submit();
   		}  		
     	
     	
     	
		function activados(checksRellenos)
		 {
			// Borra el contenido del input que almacena los códigos de módulo seleccionados
     		$('#activados').val("");
     		// Borra el contenido del input que almacena las opciones renovable seleccionadas
     		$('#renovElegidas').val("");
     		
     		for(var i=0; i<checksRellenos.length; i++) {
     			
     			var plzRenov = "";
     			
     			<c:if test="${detCoberturas.esLineaGanado}">
     			    // Sólo para líneas de ganado
     				// Comprueba si el check de módulo elegido tiene asociado combo de renovables
					var combo = $("select[id='modRenovable_"+checksRellenos[i].value+"']").val();
     				// Si el valor elegido para renovables es un numérico (0 - No, 1 - Sí) se añade al código del módulo
     				// para guardar la combinaciones elegidas
     				if (isFinite(combo)) {
     					plzRenov = "#" + combo;
     					$('#renovElegidas').val ($('#renovElegidas').val () + checksRellenos[i].value + plzRenov + ",");
     				}
     			</c:if>
     			
     		 	document.getElementById('activados').value += checksRellenos[i].value + ",";     			 	
     		}  	
     		
     	}
		
		function showdata(id) 
		{
			ID1 = document.getElementById(id+".1");
			if(ID1.style.display == '') {
				ID1.style.display = 'none';
			}
			else {
				ID1.style.display = '';
			}
			ID2 = document.getElementById(id+".2");
			if(ID2.style.display == '') {
				ID2.style.display = 'none';
			}
			else {
				ID2.style.display = '';
			}
			ID3 = document.getElementById(id);
			if(ID3.style.display == '') {
				ID3.style.display = 'none';
			}
			else {
				ID3.style.display = '';
			}
		}
		
		function getModulos(idDiv,codmodulo,idpoliza,idTabla){
			if(!document.getElementById(idTabla)){
				modulos(idDiv,codmodulo,idpoliza,idTabla);
			}
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
			<c:when test="${detCoberturas.modoLectura == 'modoLectura' && detCoberturas.vieneDeUtilidades == 'true'}">
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
						<table cellspacing="2" cellpadding="0" border="0">
							<tbody>
								<tr>
							    	<td>
										<a class="bot" id="btnVolver" href="javascript:volverModulos();">Volver</a>
									</td>
									<td>
										<!-- <a class="bot" id="btnContinuar" href="javascript:continuarComparativas();">Continuar</a> -->
										<a class="bot" id="btnContinuar" href="javascript:continuarParcelas();">Continuar</a>
									</td>
								</tr>
							</tbody>
						</table>
						<!-- FIN TABLA BARRA DE BOTONES-->
					</td>
				</tr>
		</table>
	</div>	
	
	<form name="consultaDetallePoliza" id="consultaDetallePoliza" action="consultaDetallePoliza.html" method="post">
				<input type="hidden" name="method" id="method" value="doListaParcelas"/>
				<input type="hidden" name="idpoliza" id="idpoliza" value="${detCoberturas.idpoliza}"/>
				<input type="hidden" name="modoLectura" id="modoLectura" value="${detCoberturas.modoLectura }"/>
		<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${detCoberturas.vieneDeUtilidades }"/>
	</form>
	
	<form action="seleccionPoliza.html" method="post" name="frmlistpolizas" id="frmlistpolizas">
	
		<input type="hidden" name="operacion" id="operacionVolver" value=""/>
		<input type="hidden" name="idpoliza" id="idpoliza" value="${detCoberturas.idpoliza}" />
		<input type="hidden" name="modulo" id="modulo" value="" />
		<input type="hidden" name="modoLectura" id="modoLectura" value="${detCoberturas.modoLectura }"/>
		<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${detCoberturas.vieneDeUtilidades }"/>
		
	</form>
	
	<!-- DNF 20/07/2020 PET-63485.DAVID -->
		<form action="polizaController.html" method="post" name="frmSINeleccomparativa" id="frmSINeleccomparativa">
		<input type="hidden" name="modoLectura" id="modoLectura" value="${detCoberturas.modoLectura }"/>
		<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${detCoberturas.vieneDeUtilidades}"/>
		<input type="hidden" id="action" name="action" value=""/>
		<input type="hidden" id="maxChecks" name="maxChecks" value="${detCoberturas.numeroMaxComparativas}"/>
		<input type="hidden" name="seleccionados" id="seleccionados" value="${detCoberturas.seleccionados}"/>
		<input type="hidden" name="check" value="${check}"/>
		<input type="hidden" name="idpoliza" value="${detCoberturas.idpoliza }"/>
		<input type="hidden" name="parcelasWeb" value="true"/>
		<input type="hidden" id="recalcular" name="recalcular" value=""/>
		<input type="hidden" id="activados" name="activados" value=""/>
		<input type="hidden" id="tieneParcelas" name="tieneParcelas" value="${tieneParcelas}"/>
		
		
	</form>
	<!-- FIN DNF 20/07/2020 PET-63485.DAVID -->
	
	
	<!-- Contenido de la página -->
	<form name="main" action="polizaController.html" method="post" id="main">
	
		<input type="hidden" name="action" id="action" value=""/>	
		<input type="hidden" name="activados" id="activados" value=""/>
		<input type="hidden" name="renovElegidas" id="renovElegidas" value=""/>
		<input type="hidden" name="hayComprativasElegibles" id="hayComprativasElegibles" value=""/>
		<input type="hidden" name="seleccionados" id="seleccionados" value="${detCoberturas.seleccionados }"/>
		<input type="hidden" name="idpoliza" id="idpoliza" value="${detCoberturas.idpoliza}" />	
		<input type="hidden" name="modoLectura" id="modoLectura" value="${detCoberturas.modoLectura }"/>
		<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades"value="${detCoberturas.vieneDeUtilidades }"/>
		
		<!-- [inicio] Miguel 2-2-2012  -->
		<input type="hidden"  name="tieneParcelas" id="tieneParcelas" value="${tieneParcelas}"/>
		<input type="hidden"  name="recalcular" id="recalcular" value=""/>	
		<input type="hidden" name="tieneCoberturas" id="tieneCoberturas"  value="${detCoberturas.tieneCoberturas}"/>
		<!-- [fin] Miguel 2-2-2012  -->
		
		
	</form>
   
   <form method="post" name="frmAux" id="frmAux"> 
    
   		<input type="hidden" name="lectura" id="lectura" value="${detCoberturas.modoLectura }"/>
	  	<div class="conten">
		<p class="titulopag" align="left">Elecci&oacute;n de Módulos</p>
		
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			<c:choose>
				<c:when test="${detCoberturas.listaModulos!=null}">
					<c:forEach items="${detCoberturas.listaModulos}" var="modulo" varStatus="i"><!-- hacemos una varible contador para asignar mas tarde al check un id distinto para cada iteración  -->
						<fieldset>
								<legend class="literal">MODULO ${modulo.id.codmodulo} - ${modulo.desmodulo} <span style="color:red">${detCoberturas.isInPerioContratMods[modulo.id.codmodulo]}<span> </legend>
							<table width="100%" id="data${modulo.id.codmodulo}.1">
							<tr>
								<td> 
									<a href="#" onclick="javascript:showdata('data${modulo.id.codmodulo}');getModulos('data${modulo.id.codmodulo}','${modulo.id.codmodulo}',${detCoberturas.idpoliza},'data${modulo.id.codmodulo}.3');" title="Mostrar condiciones de coberturas">
										<img src="jsp/img/folderclose.gif" alt="Desglose"/>&nbsp;&nbsp;Mostrar condiciones de coberturas
									</a>   
								</td>
							</tr>
							</table>
							<table width="100%" style="display:none" id="data${modulo.id.codmodulo}.2">
								<tr>
									<td>
										<a href="#" onclick="javascript:showdata('data${modulo.id.codmodulo}');" title="Ocultar condiciones de coberturas">
											<img src="jsp/img/folderopen.gif" alt="Desglose"/>&nbsp;&nbsp;Ocultar condiciones de coberturas
										</a> 
									</td>
								</tr>
							</table>
							<div id="data${modulo.id.codmodulo}" style="display:none">							
								<img id="ajaxLoading_data${modulo.id.codmodulo}" src="jsp/img/cargando.gif" width="70%" style="cursor:hand;cursor:pointer;display:none" />	
							</div>
							
							<div id="prueba"></div>								
							
							<table width="100%">
								<tr>					
									<td width="70%" class="literal" align="left0"> 
										<c:if test="${!detCoberturas.esLineaGanado}">
										Admite compl:
											<c:if test="${modulo.totcomplementarios > 0}">
												<label id="totcomplementarios_si" class="literal">Sí</label>
											</c:if>
											<c:if test="${modulo.totcomplementarios <= 0}">
												<label id="totcomplementarios_no" class="literal">No</label>
											</c:if>
										</c:if>							
									</td>		
									<c:set var="check" value="false"/>
									<c:set var="modSelecRenov" value="-1"/>
									<c:forEach items="${detCoberturas.moduloPolizas}" var="valorSelecPol">
										<c:if test="${valorSelecPol.id.codmodulo == modulo.id.codmodulo}" >
											<c:set var="check" value="true"/>
											<c:set var="modSelecRenov" value="${valorSelecPol.renovable}"/>
										</c:if>
									</c:forEach>									
									<c:if test="${detCoberturas.esLineaGanado}">
										<td class="literal" align="right" width="10%">
											Renovable&nbsp;
											<c:if test="${detCoberturas.modoLectura != 'modoLectura'}">
												<select id="modRenovable_${modulo.id.codmodulo}" name="modRenovable" class="dato" >
													<option value="" <c:if test="${modSelecRenov == '-1' }"> selected="selected"</c:if> />
													<option value="0" <c:if test="${modSelecRenov == '0' }"> selected="selected"</c:if>>No</option>
													<option value="1" <c:if test="${modSelecRenov == '1' }"> selected="selected"</c:if>>S&iacute;</option> 
												</select>
											</c:if>
											<c:if test="${detCoberturas.modoLectura == 'modoLectura'}">
												<select id="modRenovable_${modulo.id.codmodulo}" name="modRenovable" class="dato" disabled=true>
													<option value="" <c:if test="${modSelecRenov == '-1' }"> selected="selected"</c:if> />
													<option value="0" <c:if test="${modSelecRenov == '0' }"> selected="selected"</c:if>>No</option>
													<option value="1" <c:if test="${modSelecRenov == '1' }"> selected="selected"</c:if>>S&iacute;</option> 
												</select>
											</c:if>
											<span id="errorRenovableSelected${i.count }"/>
											<label class="campoObligatorioRenov" id="campoObligatorioRenov" title="Campo obligatorio"> *</label>					
										</td>	
									</c:if>				
									<td class="literal" align="right" width="10%">
										<c:if test="${detCoberturas.modoLectura != 'modoLectura'}">
											Elegir Módulo <input type="checkbox" id="modSelected" name="modSelected" value="${modulo.id.codmodulo}" <c:if test="${check == 'true' }">checked</c:if> />
										</c:if>
										<%-- <c:if test="${detCoberturas.modoLectura == 'modoLectura' && detCoberturas.esLineaGanado}"> --%>
										<c:if test="${detCoberturas.modoLectura == 'modoLectura'}">
											Elegir Módulo <input type="checkbox" id="modSelected" name="modSelected" disabled="true" value="${modulo.id.codmodulo}" <c:if test="${check == 'true' }">checked</c:if> />
										</c:if>
										<span id="errorModSelected${i.count }"/>
										<label class="campoObligatorio" id="campoObligatorio" title="Campo obligatorio"> *</label>					
									</td>
								</tr>
							</table>													
						</fieldset>
					</c:forEach>
				</c:when>
				<c:otherwise>
					No hay módulos disponibles.
				</c:otherwise>
			</c:choose>
		</div>
	  </form>
	  
	  
	  <%@ include file="/jsp/common/static/piePagina.jsp"%>
	  <%@ include file="/jsp/common/static/overlay.jsp"%>
	  
	  <div id="panelInfo" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
	        <!--  header popup -->
			<div id="header-popup" style="padding:0.4em 1em;position:relative;
			                                  color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;
			                                  background:#525583;height:15px">
			        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
			            Aviso
			        </div>
			        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
			                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
			            <span onclick="cerrarPopUp()">x</span>
			        </a>
			</div>
			<div class="panelInformacion_content">
							<div id="panelInformacion" class="panelInformacion">
								<div id="comparativasNoElegibles">Atencion! No hay combinación de comparativas elegibles a nivel de módulo</div>
							</div>
							<div style="margin-top:15px">
							    <a class="bot" href="javascript:cerrarPopUp();" title="Cancelar">Cancelar</a>
							    <a class="bot" href="javascript:continuar();" title="Continuar">Continuar</a>
							</div>
			</div>
	</div>
	
	<!--  POPUPS -->
	<%@ include file="/jsp/moduloPolizas/polizas/popupRecalcular.jsp"%>
	
	
</body>
</html>