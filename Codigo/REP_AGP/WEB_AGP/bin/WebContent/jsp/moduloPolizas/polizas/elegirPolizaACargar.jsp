<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<fmt:bundle basename="displaytag" >
	<c:set var="numReg"><fmt:message key="numElementsPag"/></c:set>
</fmt:bundle>

<html>
	<head>
		<title>Elección de la póliza a cargar</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />

		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
		
		<script type="text/javascript" charset="ISO-8859-1">
		
		   // Para evitar el cacheo de peticiones al servidor
	        $(document).ready(function(){
	        	var URL = UTIL.antiCacheRand($("#main3").attr("action"));
				$("#main3").attr("action", URL);
	        });

		    $(function(){
				$("#grid").displayTagAjax();
			});
			
			function cargarWrapper(){				
				if($('#tieneParcelas').val() == "si"){
					$('#overlay').show();
     	    		$("#popupRecalcular").show();
     	    	}else{
     	    		continuar("no");
     	    	}									
			}
			
			function cargarExplotaciones(){
				var valorRadio = $('input:radio[name=id]:checked').val();
				var frm= document.getElementById("frmCargaExplo");
				//var origenLlamada=frm.origenLlamada.value;
				if (valorRadio != "" && valorRadio != undefined) {
					frm.idPolizaSitActualizada.value=valorRadio;   
					frm.idPolizaAnterior.value=valorRadio;
					frm.idPolizaPlanActual.value=valorRadio;
			        frm.submit();
				}else{
					$("#divAviso").show();
					$('#txt_info').show();
		        	$('#overlay').show();
				}				
			}
			
			function continuar(recalcular){
			    $("#popupRecalcular").hide();
            	$('#overlay').hide();
                $("#recalcular").val(recalcular);

				var valorRadio = $('input:radio[name=id]:checked').val();
		        if (valorRadio != "" && valorRadio != undefined) {
					$("#idPolSeleccionada").val(valorRadio);
					$.blockUI.defaults.message = '<h4> Realizando cálculos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       				$.blockUI({ overlayCSS: { backgroundColor: '#525583' } }); 
					$('#main3').submit();
				}
				else{
					$("#divAviso").show();
					$('#txt_info').show();
		        	$('#overlay').show();
				}
			}
			
			function cerrarPopUp(){
				$('#divAviso').fadeOut('normal');
				$('#txt_info').hide();
				$('#overlay').hide();
			}
			
			function volver(){
				$('#retornarAOrigenDatos').submit();
			}
		</script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub3');">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabecera.jsp"%>

		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr align="left">
					<td align="right">
						<c:if test="${origenLlamada != 'cargaExplotaciones' }">	
							<a class="bot" id="btnVolver" href="javascript:volver()">Volver</a>
						</c:if>
						<c:if test="${tieneParcelas == 'si' }">						
							<a class="bot" id="btnCargar" href="javascript:cargarWrapper()">Cargar</a>
						</c:if>
						<c:if test="${origenLlamada == 'cargaExplotaciones' }">						
							<a class="bot" id="btnCargar" href="javascript:cargarExplotaciones()">Cargar</a>
						</c:if>
					</td>
				</tr>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%">
		<p class="titulopag" align="left">Elecci&oacute;n de la p&oacute;liza a cargar</p>
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			<form:form name="main3" id="main3" action="cargaParcelasController.html" method="post" commandName="polizaBean">
			    <input type="hidden" name="idpoliza" id="idpoliza" value="${idpoliza}"/>
				<input type="hidden" name="accion" value=""/>
				<input type="hidden" name="polizaOperacion" value=""/>				
				<input type="hidden" name="idPolSeleccionada" id="idPolSeleccionada" value=""/>
				<input type="hidden" id="recalcular" name="recalcular" value=""/>
				<input type="hidden" id="tieneParcelas" name="tieneParcelas" value="${tieneParcelas}"/>
				<input type="hidden" name="method" id="method" value="doCargarParcelasDistClase"/>
			</form:form>
		
			<form:form name="frmCargaExplo" id="frmCargaExplo" action="cargaExplotaciones.html" method="post">
			    <input type="hidden" name="idpoliza" id="idpoliza" value="${idpoliza}"/>				
				<input type="hidden" name="method" id="methodCargaExplo" value="${doMethodCargaExplo}"/>
				<input type="hidden" name="origenLlamada" id="origenLlamada" value="${origenLlamada}"/>	
				<input type="hidden" name="idPolizaSitActualizada" id="idPolizaSitActualizada" value="" />
				<input type="hidden" name="idPolizaAnterior" id="idPolizaAnterior" value="" />
				<input type="hidden" name="idPolizaPlanActual" id="idPolizaPlanActual" value="" />
			</form:form>
			
			
		  <div id="grid">
		  <form name="list" id="list">

				<display:table requestURI="cargaParcelasController.html" id="listaResultados" class="LISTA" summary="Poliza" 
		                       pagesize="${numReg}" name="${listaPolizas}" 
		                       decorator="com.rsi.agp.core.decorators.ModelTableDecoratorListaPolizas" style="width:100%;border-collapse:collapse;">
		            <display:column class="literal" headerClass="cblistaImg" title="Acciones" property="polSelec" style="width:80px;text-align:center" />
					<display:column class="literal" headerClass="cblistaImg" title="Ent." property="polEntidad"sortable="true" style="width:40px;"/>
					<display:column class="literal" headerClass="cblistaImg" title="Ofi." property="oficina" sortable="true" style="width:40px;"/>			
					<display:column class="literal" headerClass="cblistaImg" title="Usuario" property="polUsuario" sortable="true" style="width:70px"/>
					<display:column class="literal" headerClass="cblistaImg" title="Plan" property="polPlan" sortable="true" style="width:50px"/>
					<display:column class="literal" headerClass="cblistaImg" title="Linea" property="polLinea" sortable="true" style="width:50px"/>						
					<display:column class="literal" headerClass="cblistaImg" title="Col." property="polColectivo" sortable="true" style="width:70px"/>
					<display:column class="literal" headerClass="cblistaImg" title="Poliza" property="polPoliza" sortable="true" style="width:70px;"/>
					<display:column class="literal" headerClass="cblistaImg" title="Importe" property="importe" sortable="false" style="width:50px; text-align:right" />
					<display:column class="literal" headerClass="cblistaImg" title="Mod." property="polModulo" sortable="true" style="width:50px; text-align:center"/>
					<display:column class="literal" headerClass="cblistaImg" title="CIF/NIF Aseg" property="polCifNif" sortable="true" style="width:100px;"/>
					<display:column class="literal" headerClass="cblistaImg" title="Nombre Asegurado" property="polNombreAseg" sortable="false" style="width:150px;"/>
					<display:column class="literal" headerClass="cblistaImg" title="Estado" property="polEstado" sortable="true" style="width:50px;"/>	
					<display:column class="literal" headerClass="cblistaImg" title="F.Envío" property="polFechaEnvio" sortable="true" sortProperty="fechaenvio" style="width:65px"/>
		        	<display:column class="literal" headerClass="cblistaImg" title="Clase" property="polClase" sortable="false" style="width:15px;text-align:center"/>
		        </display:table>
		        </form>
		</div>   
</div>


			



		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		
		
		
		<!-- panel avisos -->
	    <div id="divAviso" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
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
		    <!--  body popup -->
		    <div class="panelInformacion_content">
				<div id="panelInformacion" class="panelInformacion">
					<div id="txt_info" style="width: 70%" >No hay ninguna Póliza seleccionada.</div>
				</div>
		    </div>
	    </div>
	    
	    <form name="retornarAOrigenDatos" id="retornarAOrigenDatos" action="polizaController.html" method="post">
		    <input type="hidden" name="action" id="action" value="volverAOrigenDatos"/>
		    <input type="hidden" name="idpoliza" id="idpoliza" value="${idpoliza}"/>
	    </form>
	
	    <!--  POPUPS -->
	    <%@ include file="/jsp/moduloPolizas/polizas/popupRecalcular.jsp"%>
	</body>
</html>