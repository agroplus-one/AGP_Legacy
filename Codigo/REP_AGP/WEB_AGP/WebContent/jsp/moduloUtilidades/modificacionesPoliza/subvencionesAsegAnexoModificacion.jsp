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
		<title>Anexo de modificación - Subvenciones de Asegurados</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
        
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		
		<script type="text/javascript">
		  $(document).ready(function(){
		        if($('#modoLectura').val() == 'true'){
				    $('#btn_continuar').hide();
				    $('#btn_pasar').hide();
				}
		  
	            var URL = UTIL.antiCacheRand(document.getElementById("frmcontinuar").action);
		        document.getElementById("frmcontinuar").action = URL;   

		        if(${subvencionesAsegurado.NoData}){
		        	$('#NoData').show();
		        	$('#tablaSubv').hide();
		        }
		        
		        $("input[type=checkbox]").each(function(){ 			 					        
					  if($('#modoLectura').val() == 'true'){
					       $(this).attr('disabled','disabled');
					  }
				});
	      });
		
		function continuar(){				
			var sSelec = document.getElementsByTagName("input");	
			var frm = document.getElementById('frmcontinuar');				
			
			for (var i = 0; i < sSelec.length; i++) 
			{
				if(sSelec.item(i).checked)
				{
					frm.subvsSeleccionadas.value += sSelec.item(i).value + ",";
				}					
			}
			$("#method").val("doContinua");
			$.blockUI.defaults.message = '<h4>Actualizando datos de subvenciones - Anexo Modificación.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } }); 
			$("#frmcontinuar").submit();
		}
		
		function pasarDefinitivo(){
		    document.getElementById('operacion').value = "pasarDedinitivo";				
			continuar();
		}
		
		function volver(){
			$("#method").val("doVolver");
			$("#frmcontinuar").submit();
		}
		
		function volver2() {
			$("#formVolver").submit();
		}
		</script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4');">

		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="right">
						<c:if test="${subvencionesAsegurado.isGanado=='true'}">
							<a class="bot" href="javascript:volver2();">Volver</a>
						</c:if>
						<c:if test="${subvencionesAsegurado.isGanado=='false'}">
							<a class="bot" href="javascript:volver();">Volver</a>
						</c:if>
						    
                            <a class="bot" id="btn_continuar" name="btn_continuar" href="javascript:continuar();">Continuar</a>
                            <!-- <a class="bot" id="btn_pasar"     name="btn_pasar"     href="javascript:pasarDefinitivo();">Pasar Definitivo</a> -->
					</td>
				</tr>
			</table>
		</div>		
		
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%">
			<p class="titulopag" align="left">Subvenciones de los Asegurados</p>
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			<form action="subvencionAseguradoAnexoMod.html" method="post" name="frmcontinuar" id="frmcontinuar">
				<input type="hidden" id="method" name="method"/>
				<input type="hidden" name="subvsSeleccionadas" value="">
				<input type="hidden" name="idAnexoModificacion" value="${subvencionesAsegurado.idAnexoModificacion}">
				<input type="hidden" id="modoLectura" name="modoLectura" value="${subvencionesAsegurado.modoLectura}"/>
				<input type="hidden" id="operacion" name="operacion" value=""/>
				<input type="hidden" id="vieneDeListadoAnexosMod" name="vieneDeListadoAnexosMod" value="${subvencionesAsegurado.vieneDeListadoAnexosMod}"/>
				<input type="hidden" id="hayCambiosDatosAsegurado" name="hayCambiosDatosAsegurado" value="${subvencionesAsegurado.hayCambiosDatosAsegurado}" />
			</form>
			
			<form:form name="formVolver" id="formVolver" action="listadoExplotacionesAnexo.html" method="post" commandName="anexo">
				<input type="hidden" id="methodVolver" name="method" value="doPantallaListaExplotacionesAnexo">
				<input type="hidden" name="vieneDeListadoAnexosMod" id="vieneDeListadoAnexosMod" value="${subvencionesAsegurado.vieneDeListadoAnexosMod}"/>
				<input type="hidden" name="anexoModificacionId" id="anexoModificacionId" value="${subvencionesAsegurado.idAnexoModificacion}"/>
				<input type="hidden" name="origenLlamada" id="origenLlamada" value="datosExplotacionAnexo"/>
				<input type="hidden" name="hayCambiosDatosAsegurado" id="hayCambiosDatosAsegurado" value="${subvencionesAsegurado.hayCambiosDatosAsegurado}" />
				
				<c:if test="${subvencionesAsegurado.modoLectura=='true'}">
					<input type="hidden" name="modoLectura" id="modoLectura" value="true"/>
				</c:if>
				<input type="hidden" name="primerAcceso" id="primerAcceso" value=""/>
		</form:form>
		
		
			
				<div class="panel2 isrt" style="width: 80%;" id="tablaSubv">
						<fieldset>
								<legend class="literal">ENESA Y CCAA</legend>	
								${subvencionesAsegurado.tabla}
						</fieldset>						
					</div>
					<div id="NoData" style="display:none">
						No existen subvenciones disponibles
					</div>
				</div>
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
	</body>
</html>