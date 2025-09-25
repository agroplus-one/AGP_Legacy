<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<%@ include file="/jsp/moduloPolizas/polizas/popupRecalcular.jsp"%>


<fmt:bundle basename="displaytag" >
	<c:set var="numReg"><fmt:message key="numElementsPag"/></c:set>
</fmt:bundle>


<html>
	<head>
		<title>Selección de Pólizas</title>
		<%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />

		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/imprimir.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>	
		
		<script type="text/javascript">
            // Para evitar el cacheo de peticiones al servidor
	        $(document).ready(function(){
	            var URL = UTIL.antiCacheRand(document.getElementById("main").action);
		        document.getElementById("main").action = URL;   

		        var URL = UTIL.antiCacheRand(document.getElementById("print").action);
		        document.getElementById("print").action = URL;
		        
		        var URL = UTIL.antiCacheRand(document.getElementById("polizaCompl").action);
		        document.getElementById("polizaCompl").action = URL; 
		        
		        var URL = UTIL.antiCacheRand(document.getElementById("altaPolizaSbp").action);
		        document.getElementById("altaPolizaSbp").action = URL; 
		        
		        document.getElementById("oficina").focus();   
		        
		        <c:if test="${PolEnvidasCorrectas}">
		        	$('#btnComplem').show();
		        	$('#btnComplem').attr('disabled','disabled');
		        </c:if>
		        
		        <c:if test="${addBotonSbp}">
		        	$('#btnSbp').show();
		        </c:if>
	        });
	        
	        
		
			function modificar(idpoliza, codentidad, oficina, codusuario, codplan, codlinea, idcolectivo,
				codmodulo, nifcif, referencia, nombre, estado,tipoRef){
				
				var frm = document.getElementById('main');
				frm.idpoliza.value = idpoliza;
				frm.entidad.value = codentidad;
				frm.oficina.value = oficina;
				frm.codusuario.value = codusuario;
				frm.plan.value = codplan;			
				frm.linea.value = codlinea;			
				frm.idcolectivo.value = idcolectivo;			
				frm.nifcif.value = nifcif;			
				frm.referencia.value = referencia;			
				frm.nombre.value = nombre;
				UTIL.marcaCombo(frm.estadoPoliza, estado);
				//Botones
				if (tipoRef == 'P'){
					if((estado == 8) || (estado == 2)){
						$('#btnComplem').attr('disabled','');
					}else{
						$('#btnComplem').attr('disabled','disabled');
					}
				}
				// Se actualiza el id de la póliza en el formulario de alta de complementaria
				var frmCpl = document.getElementById('polizaCompl');
				frmCpl.idPol.value = idpoliza;
			}			
			
			function loadBtns(){
				var listaPolizas = document.getElementById('poliza');
				if (listaPolizas == null){
					soloBtnAlta();
				}
			}					
			
			function soloBtnAlta(){
				document.getElementById('btnAlta').style.display = "";
			}
			
			function borrar(idpoliza){
				if(confirm('¿Está seguro de que desea eliminar el registro seleccionado?')){
					$("#operacion").val("baja");
					$("#idpoliza").val(idpoliza);
					$.blockUI.defaults.message = '<h4>Eliminando la póliza.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
					$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
					$("#main").submit();
				}
			}
			
			function editar(idpoliza, estado, tipo){
				
				if (estado == 3){
					if(confirm('La póliza va a pasar a estado Pendiente de Validación. No se enviará a Agroseguro hasta que se guarde como Definitiva. ¿Desea Continuar?')){
					// *** LLAMADA POR AJAX PARA SABER SI TIENE SBP
					$.ajax({
					            url:          "simulacionSbp.html",
					            data:         "method=ajax_buscarPolAsocYValidar&idPoliza="+idpoliza+"&tipoPoliza="+tipo+"&validarSbp=false",
					            dataType:     "json",
					            async:        true,
					            error: function(objeto, quepaso, otroobj){
					                alert("Error al validar la poliza para Sobreprecio: " + quepaso);
					            },
					            success: function(datos){
					            	if (datos.idPolizaSbp != "" && datos.estadoPolSbp == 2){ // tiene poliza Sbp y su estado es grab definitiva
							  	 			if(confirm('La póliza de Sobreprecio asociada en estado Definitiva se borrará, ¿desea continuar?')){
										        $.blockUI.defaults.message = '<h4> Redirigiendo.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
							       			    $.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
												$("#borrarPolizaSbp").val("true");		
												$("#operacion").val("editar");
												$("#idpoliza").val(idpoliza);
												$("#main").submit();
							  	 			}
							  	 	}else{
								        $.blockUI.defaults.message = '<h4> Redirigiendo.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
					       			    $.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
										$("#operacion").val("editar");
										$("#idpoliza").val(idpoliza);
										$("#main").submit();
							  	 	}
							  	},
					            type: "POST"
					    });
					  }
				}else{ // estado != 3
			        $.blockUI.defaults.message = '<h4> Redirigiendo.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       			    $.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
					$("#operacion").val("editar");
					$("#idpoliza").val(idpoliza);
					$("#main").submit();
				}
				
			}
			
			function confirmarBloqueo(){
				<c:if test="${not empty requestScope.confirmarBloqueo and requestScope.confirmarBloqueo == 'SI' }">
					if(confirm('La póliza está bloqueada. ¿Desea desbloquearla?')){
						$("#operacion").val("desbloquear");
						$("#main").submit();
					}
				</c:if>
			}
			
			function altaCpl(){ 
				$('#btnComplem').attr('disabled','disabled');
				$('#method').val('doAlta');
				$('#refPol').val($('#referencia').val());
				$('#lineaseguroidCpl').val($('#lineaseguroid').val());
				$('#polizaCompl').submit();
			}
			
			function editarPolCpl(idpoliza,estado){
				if (estado == 3){
					if(confirm('La póliza pasará a estado pendiente de validación, ¿Desea Continuar?')){
						$("#method").val("doConsulta");						
						$("#idpolizaCpl").val(idpoliza);
						$('#polizaCompl').submit();
					}
				}else{
					$("#method").val("doConsulta");
					$("#idpolizaCpl").val(idpoliza);
					$('#polizaCompl').submit();	
				}
			}
			
			function ver(idpoliza){
				$("#operacion").val("editar");
				$("#modoLectura").val("modoLectura");
				$("#idpoliza").val(idpoliza);
				$("#main").submit();
			}
			
			function verCpl(idpoliza){
				$("#method").val("doConsulta");
				$("#modoLecturaCpl").val("modoLectura");
				$("#idpolizaCpl").val(idpoliza);
				$('#polizaCompl').submit();
			}
			
			function verAcuseRecibo(idpoliza){	
				$("#operacion").val("verAcuseRecibo");
				$("#idpoliza").val(idpoliza);
				$("#main").submit();
			}
			
			/*
			 *  show popupImprimir
			 */
		  	 function imprimir(idpoliza,tiporef){
		  	    var frm = document.getElementById('print');
				$("#idPolizaPrint").val(idpoliza);
				if (tiporef == 'C'){
					frm.method.value = 'doInformePolizaComplementaria';
					$('#print').attr('target', '_blank');
					$("#print").submit();
				} else {
					frm.method.value = 'doInformePoliza';
					$('#overlay').show();			
	  	     		$('#popUpImprimir').show();
				}
	        		        	
		  	 }
		  	 function altaSbp(){
		  	 	
		  	 	var frm = document.getElementById('altaPolizaSbp');
		  	 	frm.method.value ='doAlta';
		  	 	var idpolizaPpl = frm.idPolizaPpal.value;
		  	 	var idpolizaCpl = frm.idPolizaCpl.value;
		  	 	var idEstadoPpal = frm.idEstadoPpal.value;
		  	 	var idEstadoCpl = frm.idEstadoCpl.value;
		  	 	var frm2 = document.getElementById('main');
		  	 	
		  	 	if (frm2.existePolizaSbp.value =='false'){
			  	 	if (idpolizaCpl != ""){ //tenemos poliza Cpl
			  	 		if (idEstadoCpl != 4){
			  	 			// Si la principal está contratada y la complementaria está en estado 'Definitiva', 'Enviada pendiente de confirmación'
			  	 			// o 'Enviada correcta' no se pregunta y se incluye la complementaria en el sobreprecio
			  	 			if((idEstadoCpl == 8 || idEstadoCpl == 5) && idEstadoPpal == 8){
	       						alta_eleccion_Si();
			  	 			}else{			  	 			
			  	 				// popUpAltaSbp
			  	 				var msj = "¿Desea incluir los datos de la póliza complementaria en el Sobreprecio?";
			  	 				//ACTIVAR POPUP incluir Cpl en Sbp
			  	 				$('#txt_mensaje_eleccionCplEnSbp').html(msj);
			  	 				$('#panelInformacion').show();
			  	 				$('#popUpAltaSbp').show();
	 	     					$('#overlay').show();
	 	     				}	
			  	 		}else{ //estadoCpl == 4
			  	 			$.blockUI.defaults.message = '<h4> <BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	       					$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	       					$('#altaPolizaSbp').submit();
			  	 		}
			  	 	}else{ // no hay Cpl
			  	 		$.blockUI.defaults.message = '<h4> <BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	       				$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	       				$('#altaPolizaSbp').submit();
	       			}
			  	}else{ // no existe Sbp
			  		$.blockUI.defaults.message = '<h4> <BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	       			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	       			$('#altaPolizaSbp').submit();
	       		}
		  	 	
		  	}
		  	
		  	function alta_eleccion_Si(){
				$('#popUpAltaSbp').hide();
				$('#overlay').hide();
				var frm = document.getElementById('altaPolizaSbp');
				frm.incSbpComp.value = "S";
				$.blockUI.defaults.message = '<h4> Calculando Póliza de Sobreprecio.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	       			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
				$('#altaPolizaSbp').submit();
			}
		
			function alta_eleccion_No(){
				$('#popUpAltaSbp').hide();
				$('#overlay').hide();
				var frm = document.getElementById('altaPolizaSbp');
				frm.incSbpComp.value = "N";
				$.blockUI.defaults.message = '<h4> Calculando Póliza de Sobreprecio.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	       			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
				$('#altaPolizaSbp').submit();
			}
			
			function hidePopUpAltaSbp(){
				$('#popUpAltaSbp').hide();
				$('#overlay').hide();
			}

			// DAA 11/07/2013
			function alertaCargaPac(){
				$('#panelAlertasValidacion').html("No se pueden realizar acciones sobre la póliza. Cargando parcelas de la PAC"); 
				$('#panelAlertasValidacion').show(); 
			}
	
		  	 
		</script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" 
		  onload="SwitchMenu('sub3'); loadBtns(); confirmarBloqueo();">

		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabecera.jsp"%>

		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr align="left">
					<td align="right">
						<a class="bot" id="btnSbp" style="display: none" href="javascript:altaSbp();">Sobreprecio</a>
						<a class="bot" id="btnComplem" style="display: none" href="javascript:altaCpl();">Alta Compl.</a>
						<a class="bot" id="btnAlta" href="javascript:generales.enviar('alta')">Alta</a>
					</td>
				</tr>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%">
			<p class="titulopag" align="left">Selección de Póliza</p>
			<form name="polizaCompl" id="polizaCompl" action="polizaComplementaria.html" method="post">	
				<input type="hidden" name="refPol" id="refPol" />	
				<input type="hidden" name="lineaseguroidCpl" id="lineaseguroidCpl" />
				<input type="hidden" name="idpolizaCpl" id="idpolizaCpl" />		
				<input type="hidden" name="method" id="method"/>
				<input type="hidden" name="idPol" id="idPol" value="${idPolPr}"/>
				<input type="hidden" name="modoLecturaCpl" id="modoLecturaCpl" value=""/>
			</form>
			<form name="consulta" id="consulta" action="polizaController.html" method="post">
				<input type="hidden" name="operacion" value="editar" />
				<input type="hidden" name="idpoliza" id="consulta_idpoliza" />
			</form>
			
			<form name="altaPolizaSbp" id="altaPolizaSbp" action="simulacionSbp.html" method="post" commandName="polizaSbp">	
				<input type="hidden" name="method" id="method"/>
				<form:hidden path="polizaSbp.polizaPpal.idpoliza" id="idPolizaPpal"/>	
				<form:hidden path="polizaSbp.polizaCpl.idpoliza" id="idPolizaCpl"/>
				<form:hidden path="polizaSbp.polizaPpal.estadoPoliza.idestado" id="idEstadoPpal"/>	
				<form:hidden path="polizaSbp.polizaCpl.estadoPoliza.idestado" id="idEstadoCpl"/>
				<form:hidden path="polizaSbp.incSbpComp" id="incSbpComp"/>
				<input type="hidden" name="origenLlamada" id="origenLlamada" value="seleccionPoliza"/>
				
			</form>
			<form:form name="main" id="main" action="seleccionPoliza.html" method="post" commandName="polizaBean">
				<input type="hidden" name="operacion" id="operacion"/>
				<input type="hidden" name="modoLectura" id="modoLectura" value=""/>
				<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${vieneDeUtilidades}"/>
				<input type="hidden" name="borrarPolizaSbp" id="borrarPolizaSbp" value=""/>
				<input type="hidden" name="estado" id="estado" value=""/>
				<input type="hidden" name="tipo" id="tipo" value=""/>
				<input type="hidden" name="tieneParcelas" id="tieneParcelas" value=""/>
				<form:hidden path="linea.lineaseguroid" id="lineaseguroid" />
				<form:hidden path="idpoliza" id="idpoliza" />
				<form:hidden path="linea.codlinea" id="codlinea"/>	
				<form:hidden path="linea.codplan" id="codplan"/>
				<form:hidden path="idenvio" id="idenvio"/>
				<input type="hidden" name="existePolizaSbp" id="existePolizaSbp" value="${existePolizaSbp}"/>
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				
				<div class="panel2 isrt">
					<fieldset>
							<table width="100%">
							<tr align="left">
								<td class="literal">Entidad</td>
								<td class="literal">
									<form:input path="colectivo.tomador.id.codentidad" id="entidad" size="5" maxlength="4" cssClass="dato" readonly="true"/>
								</td>
								<td class="literal">Oficina</td>
								<td class="literal">
									<form:input path="oficina" size="4" maxlength="4" cssClass="dato" id="oficina" readonly="true"/>
								</td>
								<td class="literal">Usuario</td>
								<td class="literal">
									<form:input path="usuario.codusuario" id="codusuario" size="8" maxlength="8" cssClass="dato" readonly="true"/>
								</td>
							</tr>
							<tr align="left">
								<td class="literal">Plan</td>
								<td class="literal">
									<form:input path="colectivo.linea.codplan" size="5" maxlength="4" cssClass="dato" id="plan" readonly="true"/>
								</td>
								<td class="literal">Línea</td>
								<td class="literal">
									<form:input path="colectivo.linea.codlinea" size="4" maxlength="3" cssClass="dato" id="linea" readonly="true"/>
								</td>
								<td class="literal">Colectivo</td>
								<td class="literal">
									<form:input path="colectivo.idcolectivo" id="idcolectivo" size="15" maxlength="15" cssClass="dato" readonly="true"/>
								</td>
							</tr>
							<tr align="left">
								<td class="literal">CIF/NIF Asegurado</td>
								<td class="literal">
									<form:input path="asegurado.nifcif" id="nifcif" size="9" maxlength="9" cssClass="dato" readonly="true"/>
								</td>
								<td class="literal">Póliza</td>
								<td class="literal">
									<form:input path="referencia"  id="referencia" size="15" maxlength="15" cssClass="dato" readonly="true"/>
								</td>
								<td class="literal">Estado</td>
								<td class="literal">
									<form:select path="estadoPoliza.idestado" cssClass="dato" id="estadoPoliza" cssStyle="width:150px">
										<form:option value="">Todos</form:option>
										<c:forEach items="${requestScope.listaEstados}" var="estado">
											<form:option value="${estado.idestado}">${estado.descEstado }</form:option>
										</c:forEach>
									</form:select>
								</td>
							</tr>
							<tr align="left">
								<td class="literal">Nombre asegurado</td>
								<td class="literal" colspan="5">
									<form:input path="asegurado.nombre" id="nombre" size="60" maxlength="60" cssClass="dato" readonly="true"/>
								</td>
							</tr>
						</table>
					</fieldset>
				</div>
			</form:form>
				<display:table requestURI="seleccionPoliza.html" class="LISTA" summary="poliza" defaultsort="0" defaultorder="ascending"
						pagesize="${numReg}" sort="list" name="${listaPolizas}" id="poliza" excludedParams="*"
						decorator="com.rsi.agp.core.decorators.ModelTableDecoratorPolizas" 
						style="width:100%;border-collapse:collapse">
					<display:column class="literal" headerClass="cblistaImg" title="Acciones" property="cargaPolSelec" sortable="false" style="width:60px;text-align:center"/>
					<display:column class="literal" headerClass="cblistaImg" title="Ent." property="polEntidad" />
					<display:column class="literal" headerClass="cblistaImg" title="Ofi." property="polOficina" />
					<display:column class="literal" headerClass="cblistaImg" title="Usuario" property="polUsuario" />
					<display:column class="literal" headerClass="cblistaImg" title="Plan" property="polPlan" />
					<display:column class="literal" headerClass="cblistaImg" title="Línea" property="polLinea" />
					<display:column class="literal" headerClass="cblistaImg" title="Colectivo" property="polColectivo" />
					<display:column class="literal" headerClass="cblistaImg" title="Póliza" property="polPoliza" />
					<display:column class="literal" headerClass="cblistaImg" title="CIF/NIF Aseg." property="polCifNif" />
					<display:column class="literal" headerClass="cblistaImg" title="Nombre Asegurado" property="polNombreAseg"/>
					<display:column class="literal" headerClass="cblistaImg" title="Estado" property="polEstado"/>
					<display:column class="literal" headerClass="cblistaImg" title="Tipo Referencia" property="polTipoRef"/>
					<display:column class="literal" headerClass="cblistaImg" title="Clase" property="polClase" sortProperty="clase" sortable="true" />
				</display:table>
		</div>
		
		
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		
		<!--               -->
		<!-- POPUPS AVISO  -->
		<!--               -->
		
		<!-- *** popUp Alta Sbp *** -->
		<div id="popUpAltaSbp" class="parcelasRepWindow" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em; top: 165px; z-index:1000000008;">
		     <!--  header popup -->
			 <div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
				        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
				            Alta de Sobreprecio
				        </div>
				        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
				                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
				            <span onclick="hidePopUpAltaSbp()">x</span>
				        </a>
			 </div>
			 <!--  body popup -->
			 <div class="panelInformacion_content">
						<div id="panelInformacion" class="panelInformacion">
							<div id="txt_mensaje_eleccionCplEnSbp"></div>
						</div>
						<div style="margin-top:15px">
						  <a class="bot" id="btn_aceptarGrabacionDefinitiva" href="javascript:alta_eleccion_Si()" title="Si incluir">SI</a>
						    <a class="bot" href="javascript:alta_eleccion_No()" title="No incluir">NO</a>
						</div>
			 </div>
		</div>
	<%@ include file="/jsp/moduloPolizas/polizas/popupEleccionImpresion.jsp"%>	
	</body>
</html>