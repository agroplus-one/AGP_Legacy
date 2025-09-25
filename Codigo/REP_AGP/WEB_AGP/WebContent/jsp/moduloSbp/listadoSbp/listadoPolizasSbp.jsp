<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
	<head>
		<meta charset="utf-8">
  	<meta name="viewport" content="width=device-width, initial-scale=1">
		<title>Listado P&oacute;lizas de SobrePrecio</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
	
		<style>
			.detallePoliza { 
				margin: 0 auto;
			}
		</style>
		
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/imprimir.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/moduloSbp/listadoSbp/listadoPolizasSbp.js" ></script>
		<script type="text/javascript" src="jsp/js/ajaxfileupload.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.filestyle.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/cargaDocFirmada.js" ></script>
		
		
		
		<%@ include file="/jsp/js/draggable.jsp"%>
		
		<script type="text/javascript">
			
		// Para evitar el cacheo de peticiones al servidor
		$(document).ready(function(){
	        <c:if test="${perfil == 0 || perfil == 5}">
				document.getElementById("entidad").focus(); 		
			</c:if>
		    <c:if test="${perfil > 0 && perfil < 5}">
		    	
		      
				document.getElementById("codusuario").focus(); 						
			</c:if>   
			<c:if test="${perfil == 1}">
				
				document.getElementById("oficina").focus();
			</c:if> 
			
			<c:if test="${empty listadoPolizasSbp}">
					$('#divImprimir').hide(); 		
			</c:if> 
		});
		 
		 function cargarFiltro(){
		 
			<c:if test="${origenLlamada != 'menuGeneral'}">		
				<c:forEach items="${sessionScope.listadoPolizasSbp_LIMIT.filterSet.filters}" var="filtro">
					if ('${filtro.property}' == 'polizaPpal.colectivo.tomador.id.codentidad'){
						var inputText = document.getElementById('entidad');
						inputText.value = '${filtro.value}';
					}else if ('${filtro.property}' == 'polizaPpal.oficina'){
						var inputText = document.getElementById('oficina');
						inputText.value = '${filtro.value}';
					}else if ('${filtro.property}' == 'polizaPpal.usuario.delegacion'){	
						var inputText = document.getElementById('delegacion');
						inputText.value = '${filtro.value}';
					}else if ('${filtro.property}' == 'polizaPpal.colectivo.subentidadMediadora.id.codentidad'){
						var inputText = document.getElementById('entmediadora');
						inputText.value = '${filtro.value}';
					}else if ('${filtro.property}' == 'polizaPpal.colectivo.subentidadMediadora.id.codsubentidad'){
						var inputText = document.getElementById('subentmediadora');
						inputText.value = '${filtro.value}';
					}else if ('${filtro.property}' == 'polizaPpal.linea.codplan'){
						var inputText = document.getElementById('plan');
						inputText.value = '${filtro.value}';
					}else if ('${filtro.property}' == 'polizaPpal.linea.codlinea'){
						var inputText = document.getElementById('linea');
						inputText.value = '${filtro.value}';
						if ($('#linea').val() != ''){
							$('#desc_linea').val($('#nomLinea').val());
						}
					}else if ('${filtro.property}' == 'polizaPpal.clase'){
						var inputText = document.getElementById('clase');
						inputText.value = '${filtro.value}';
					}else if ('${filtro.property}' == 'polizaPpal.colectivo.idcolectivo'){
						var inputText = document.getElementById('colectivo');
						inputText.value = '${filtro.value}';
					}else if ('${filtro.property}' == 'polizaPpal.colectivo.dccolectivo'){
						var inputText = document.getElementById('dcCol');
						inputText.value = '${filtro.value}';
					}else if ('${filtro.property}' == 'estadoPlzSbp.idestado'){
						var inputText = document.getElementById('estadoSbp');
						inputText.value = '${filtro.value}';
					}else if ('${filtro.property}' == 'polizaPpal.codmodulo'){
						var inputSelect = document.getElementById('modulo');
						inputSelect.value = '${filtro.value}';
					}else if ('${filtro.property}' == 'polizaPpal.estadoPoliza.idestado'){
						var selectOption = document.getElementById('estadoPpal');
						selectOption.value = '${filtro.value}';
					}else if ('${filtro.property}' == 'polizaCpl.estadoPoliza.idestado'){
						var selectOption = document.getElementById('estadoCpl');
						selectOption.value = '${filtro.value}';
					}else if ('${filtro.property}' == 'polizaPpal.usuario.codusuario'){
						var inputText = document.getElementById('codusuario');
						inputText.value = '${filtro.value}';
					}else if ('${filtro.property}' == 'polizaPpal.asegurado.nifcif'){
						var inputText = document.getElementById('nifCif');
						inputText.value = '${filtro.value}';
					}else if ('${filtro.property}' == 'detalle'){
						var selectOption = document.getElementById('detalle');
						selectOption.value = '${filtro.value}';
					}else if ('${filtro.property}' == 'id'){
					}else if ('${filtro.property}' == 'referencia'){
						var inputText = document.getElementById('referencia');
						inputText.value = '${filtro.value}';
					}else if ('${filtro.property}' == 'tipoEnvio.descripcion'){
						var inputText = document.getElementById('tipoEnvio');
						inputText.value = '${filtro.value}';
					}else if ('${filtro.property}' == 'refPlzOmega'){
						var inputText = document.getElementById('refPlzOmega');
						inputText.value = '${filtro.value}';
					}else if ('${filtro.property}' == /*'polizaPpal.idpoliza'*/ 'nSolicitud'){
						var inputText = document.getElementById('nSolicitud');
						inputText.value = '${filtro.value}';
						// P0073325
					}else if ('${filtro.property}' == 'gedDocPolizaSbp.canalFirma.idCanal'){
						var inputText = document.getElementById('canalFirma');
						inputText.value = '${filtro.value}';
					}else if ('${filtro.property}' == 'gedDocPolizaSbp.docFirmada'){
						var inputText = document.getElementById('docFirmada');
						inputText.value = '${filtro.value}';
					}
					
				</c:forEach>
			</c:if>
		}
		
		function volver() {
			if (${origenLlamada == 'consultaPolizasParaSbp'}){
				$("#consultaPolizasParaSbp").submit();
			}else if (${origenLlamada == 'seleccionPoliza'}){
				$("#seleccionForm").submit();
			}else if (${origenLlamada == 'utilidadesPoliza'}){
				$("#utilidadesForm").submit();
		    }else{
		    	$(window.location).attr('href', 'menu.html?OP=ppal');
		    }
		}
		
		function limpiar(){
			var frm = document.getElementById('main3');
			limpiaAlertas();
			
			$('#entidad').val('');
			$('#desc_entidad').val('');
			if (frm.perfil.value != 4){
				$('#entidad').val("${usuario.oficina.entidad.codentidad}");
			}
			//jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','polizaPpal.colectivo.tomador.id.codentidad', $('#entidad').val());
			$('#desc_entidad').val("${usuario.oficina.entidad.nomentidad}");
			if (frm.perfil.value == 3){
				$('#oficina').val("${usuario.oficina.id.codoficina}");
				//jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','polizaPpal.oficina', $('#oficina').val());
			}else{
				$('#oficina').val('');
			}
			$('#plan').val(frm.filtroPlan.value);
			//jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','polizaPpal.linea.codplan', $('#plan').val());
			
			//$('#codusuario').val(frm.filtroUsuario.value);
			//jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','polizaPpal.usuario.codusuario', $('#codusuario').val());
			
			$('#desc_oficina').val('');
			//$('#codusuario').val('');
			$('#linea').val('');
			$('#desc_linea').val('');
			$('#referencia').val('');
			$('#colectivo').val('');
			$('#modulo').val('');
			$('#tipoEnvio').val('');
			$('#fecEnvioId').val('');
			$('#nifCif').val('');
			$('#estadoPpal').selectOptions('');
			$('#estadoCpl').selectOptions('');
			$('#estadoSbp').selectOptions('');
			$('#ref').val('');
			$('#clase').val('');
			$('#incSbpCpl').selectOptions('');
			$('#detalle').selectOptions('');
			$('#refPlzOmega').val('');
			$('#nSolicitud').val('');
			// P0073325
			$('#canalFirma').val('');
			$('#docFirmada').val('');
			$('#main3').attr('target', '');
			//onInvokeAction('listadoPolizasSbp','filter');
			$('#limpiar').submit();
		}
		
		</script>
	</head>
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="javascript:SwitchMenu('sub8');cargarFiltro();">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		<!-- botones de la página -->
		<div id="buttons" >
			<table width="100%" cellspacing="0" cellpadding="0" border="0">
				<tbody>
					<tr>
						<td align="right">
						
						
							<c:if test="${origenLlamada == 'menuGeneral'}">
								<a class="bot" id="btnConsultar" href="javascript:consultarInicial();">Consultar</a>
							</c:if>
							<c:if test="${origenLlamada != 'menuGeneral'}">
								<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
							</c:if>
							
							
							<a class="bot" href="javascript:limpiar();">Limpiar</a>
							<a class="bot" id="btnVolver" href="javascript:volver();">Volver</a>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<form name="limpiar" id="limpiar" action="consultaPolizaSbp.run" method="post">								
			<input type="hidden" name="origenLlamada" id="origenLlamada" value="menuGeneral"/>
		</form>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:103%" >
		<p class="titulopag" align="left">Listado p&oacute;lizas de Sobreprecio</p>
			
			<form name="seleccionForm" id="seleccionForm" action="seleccionPoliza.html" method="post">
				<input type="hidden" name="idPoliza" id="idPoliza" value="${idPolizaSeleccion}"/>
				<input type="hidden" name="operacion" id="operacion" value="" />
			</form>
			
			<form name="utilidadesForm" id="utilidadesForm" action="utilidadesPoliza.html" method="post">
				<input type="hidden" name="idPoliza" id="idPoliza" />
				<input type="hidden" name="operacion" id="operacion" value="" />
				<input type="hidden" name="recogerPolizaSesion" id="recogerPolizaSesion" value="true" />
			</form>
			
			<form name="informesForm" id="informesForm" action="informes.html" method="post">
				<input type="hidden" name="idPolizaSbp" id="idPolizaSbp" value=""/>
				<input type="hidden" name="method" id="method" value="" />
			</form>
			
			<form name="simulacionForm" id="simulacionForm" action="simulacionSbp.html" method="post">
				<input type="hidden" name="method" id="method" />
				<input type="hidden" name="idPolSbp" id="idPolSbp"/>
				<input type="hidden" name="idPolizaPpal" id="idPolizaPpal"/>
				<input type="hidden" name="idPolizaCpl" id="idPolizaCpl"/>
				<input type="hidden" name="recalcularConCpl" id="recalcularConCpl"/>
				<input type="hidden" name="referenciaPol" id="referenciaPol"/>
				<input type="hidden" name="origenLlamada" id="origenLlamada" value="edicionlistadoPolizasSbp"/>
				<input type="hidden" name="origenLlamadaListPolSbp" id="origenLlamadaListPolSbp" value="origenLlamadaListPolSbp"/>
			</form>
			
			<form name="consultaPolizasParaSbp" id="consultaPolizasParaSbp" action="consultaPolSbp.run" method="post">
				<input type="hidden" name="method" id="method"/>
				<input type="hidden" name="idPolizaSbp" id="idPolizaSbp"/>
				<input type="hidden" name="idPoliza" id="idPoliza" value="${idPolizaSeleccion}"/>
				<input type="hidden" name="recogerPolSesion" id="recogerPolSesion" value="true"/>
			</form>
			
			<form name="altaSuplementoSbp" id="altaSuplementoSbp" action="suplementoSbp.html" method="post">	
				<input type="hidden" name="method" id="method"/>
				<input type="hidden" name="idPolizaSbp" id="idPolizaSbp"/>
			</form>
			
			<form name="anularSuplementoSbp" id="anularSuplementoSbp" action="suplementoSbp.html" method="post">	
				<input type="hidden" name="method" id="method"/>
				<input type="hidden" name="refPolizaPrincipalSbp" id="refPolizaPrincipalSbp"/>
			</form>
		
			<form name="main3" id="main3" action="consultaPolizaSbp.run" method="post">
				<input type="hidden" id="idpoliza" value=""/>
				<input type="hidden" name="fecEnvioId.day" value=""/> 
	            <input type="hidden" name="fecEnvioId.month" value=""/> 
	            <input type="hidden" name="fecEnvioId.year" value=""/>
	            <input type="hidden" name="perfil" value="${perfil}"/>
	            <input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
	            <input type="hidden" name="origenLlamada" value="${origenLlamada}"/>
	            <input type="hidden" name="nomLinea" id="nomLinea" value="${nomLinea}"/>
	            <input type="hidden" name="nomEntidad" id="nomEntidad" value="${nomEntidad}"/>
				<input type="hidden" name="listaLineasSbp" id="listaLineasSbp" value="${listaLineasSbp}"/>
				<input type="hidden" name="filtroPlan" id="filtroPlan" value="${filtroPlan}"/>
				<input type="hidden" name="filtroUsuario" id="filtroUsuario" value="${filtroUsuario}"/>
				<input type="hidden" name="codEntidad" id="codEntidad" value="${codEntidad}"/>
				<input type="hidden" name="codOficina" id="codOficina" value="${codOficina}"/>
				<input type="hidden" name="grupoOficinas" id="grupoOficinas" value="${grupoOficinas}"/>
				<input type="hidden" name="nomOficina" id="nomOficina" value="${nomOficina}"/>
				<input type="hidden" id="externo" value="${externo}" />
				<input type="hidden" name="entMediadora" id="entMediadora" value="${entMediadora}"/>
				<input type="hidden" name="subEntmediadora" id="subEntmediadora" value="${subEntmediadora}"/>
				<input type="hidden" name="deleg" id="deleg" value="${deleg}"/>
				<input type="hidden" name="exportToExcel" id="exportToExcel" value="" />
				<input type="hidden" name="totalListSize" id="totalListSize" value="" />
				
				<input type="hidden" name="canalFirma" id="canalFirma" value="" />
				<input type="hidden" name="docFirmada" id="docFirmada" value="" />
				
		        <%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div style="width:97%;margin:0 auto;">
					<fieldset>
					<legend class="literal">Filtro</legend>
						<table style="margin:0 auto;">
							<tr>
								<table>
									<tr>
										<td class="literal">Entidad
											<c:if test="${perfil == 0 || perfil == 5}">
												<input type="text"  size="3" maxlength="4" class="dato" name="entidad" id="entidad" tabindex="1" onchange="javascript:lupas.limpiarCampos('desc_entidad', 'oficina', 'desc_oficina');"/>
												<input type="text"  class="dato"	id="desc_entidad" size="25" readonly="true"/>
												<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');" alt="Buscar Entidad" title="Buscar Entidad" />
											</c:if>
											<c:if test="${perfil > 0 && perfil < 5}">
												<input type="text" size="3" maxlength="4" class="dato" readonly="true" name="entidad" id="entidad" tabindex="1" />
										        <input type="text"  class="dato"	id="desc_entidad" size="25" readonly="true"/>
											</c:if>
											
										</td>	
										<td class="literal">Oficina
											<c:if test="${perfil == 0 || perfil ==1 || perfil == 2 || perfil == 5}">
												<input type="text"  size="3" maxlength="4" class="dato" name="oficina" id="oficina" tabindex="2" onchange="javascript:lupas.limpiarCampos('desc_oficina');"/>
												<input type="text"  class="dato"	id="desc_oficina" size="18" readonly="true"/>
												<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Oficina','principio', '', '');" alt="Buscar Oficina" title="Buscar Oficina" />
											</c:if>
											<c:if test="${perfil > 2 && perfil < 5}">
												<input type="text"  size="3" maxlength="4" class="dato" readonly="true" name="oficina" id="oficina" tabindex="2" />
												<input type="text"  class="dato"	id="desc_oficina" size="18" readonly="true"/>
											</c:if>	
										</td>
										
										<!-- Pet. 63473 ** MODIF TAM (20/12/2021) ** Inicio  -->
										<td class="literal" >E-S Med
											<c:if test="${externo == 0 and perfil !=4 }">
												<!--  es interno -->
												<input type= "text" name="entmediadora" size="3" maxlength="4" class="dato" id="entmediadora" onchange="javascript:lupas.limpiarCampos('subentmediadora');UTIL.subStrEntidad();" value="
													tabindex="3" />
												<input type= "text" name="subentmediadora"  size="3" maxlength="4" 	class="dato" id="subentmediadora" tabindex="4" />
												<img src="jsp/img/magnifier.png" style="cursor: hand;" 	onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');"
													alt="Buscar SubEntidad Mediadora" title="Buscar SubEntidad Mediadora" />
											</c:if> 
											<c:if test="${externo == 1 or (externo==0 and perfil==4)}">
												<!--  es externo -->
												<input name="entmediadora" size="3" maxlength="4" class="dato" id="entmediadora" tabindex="3" readonly="true" />
												<input name="subentmediadora" size="3" maxlength="4" class="dato" id="subentmediadora" tabindex="4" readonly="true" />
											</c:if>
										</td>	
										<td class="literal">&nbspDelegación
											<c:if test="${externo == 1}">
												<!--  es externo -->
												<c:if test="${perfil == 1}">
													<input name="delegacion" size="3" maxlength="4" class="dato" id="delegacion" tabindex="5" />
												</c:if>
												<c:if test="${perfil == 3}">
													<input name="delegacion" size="3" maxlength="4" class="dato" id="delegacion" tabindex="5" readonly="true" />
												</c:if>
											</c:if> 
											<c:if test="${externo == 0}">
												<!--  es interno -->
												<input name="delegacion" size="3" maxlength="4" class="dato" id="delegacion" tabindex="5" />
											</c:if>
										</td>
										<!-- Pet. 63473 ** MODIF TAM (20/12/2021) ** Fin  -->
								
										<td class="literal">Usuario
											<c:if test="${perfil != 4}">
												<input type="text"  id="codusuario" name="codusuario" size="8" maxlength="19" class="dato" tabindex="3" onchange="this.value=this.value.toUpperCase();"/>
											</c:if>
											<c:if test="${perfil == 4}">
												<input type="text"  id="codusuario" name="codusuario" size="8" maxlength="19" class="dato" readonly="true" tabindex="3"/>
											</c:if>
										</td>
								    </tr>
								</table>
							</tr>  
							<tr>
								<table>
									<tr align="left">
										<td class="literal">Plan</td>
										<td class="literal">
											<input type="text" name="plan" size="5" maxlength="4" class="dato" id="plan" tabindex="4" onchange="javascript:lupas.limpiarCampos('linea', 'desc_linea');"/>
										<td class="literal">L&iacute;nea</td>
										<td class="literal" colspan="3">
											<input type="text" size="3" maxlength="3" class="dato" name="linea" id="linea" tabindex="5" onchange="javascript:lupas.limpiarCampos('desc_linea');"/>
											<input type="text" class="dato" id="desc_linea" size="40" readonly="true"/>
											<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('LineaSbp','principio', '', '');" alt="Buscar Línea Sobreprecio" title="Buscar Línea Sobreprecio" />							
										</td>
										<td class="literal">Clase</td>
										<td class="literal">
										  	<input type="text" size="4" maxlength="3" class="dato" name="clase" id="clase" tabindex="6" />
											<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('ClaseSbp','principio', '', '');" alt="Buscar Clase" title="Buscar Clase" />							
										</td>
									</tr>
								</table>
							</tr>		
							<tr>
								<table>
									<tr align="left">
										<td class="literal">P&oacute;liza</td>
										<td class="literal">
											<input type="text" size="10" maxlength="12" class="dato" name="referencia" id="referencia" tabindex="7" onchange="this.value=this.value.toUpperCase();"/>
										</td>
										<td class="literal">Colectivo</td>
										<td class="literal">
											<input type="text" name="colectivo" id="colectivo" size="11" maxlength="9" class="dato" tabindex="8" />
											<!--
											<input type="text" size="3" id="dcCol" maxlength="1" class="dato" tabindex="9" />
											-->
										</td>
										<td class="literal">Estado Ppal</td>
										<td class="literal" width="205px">
											<select name="estadoPpal" id="estadoPpal" class="dato" tabindex="9" style="width:200px" >
												<option value="">Todos</option>
												<c:forEach items="${estadosPpal}" var="estado">
													<option value="${estado.idestado}">${estado.descEstado}</option>
												</c:forEach>
											</select>
										</td>
										<td class="literal">M&oacute;dulo</td>
										<td class="literal">
											<input type="text" name="modulo" id="modulo" size="4" maxlength="3" class="dato" tabindex="10" onchange="this.value=this.value.toUpperCase();"/>
										</td>	
								</tr>
							</table>	
						</tr>
						<tr>
							<table>		
								<tr align="left">
									<td class="literal">CIF/NIF <br>Asegurado</td>
									<td class="literal">
										<input type="text" name="nifCif" id="nifCif" size="25" maxlength="9" class="dato" tabindex="11" onchange="this.value=this.value.toUpperCase();this.value=generales.lpadNIF(this.value);"/>
									</td>
									<td class="literal">Comp. en Sbp.</td>
									<td class="literal" width="150px">
										<select name="incSbpCpl"  class="dato"	style="width:70" id="incSbpCpl" tabindex="12" >
											<option value="" >Todos</option>
											<c:if test="${opcionRC == 'S'}">
												<option value="S" selected>Si</option>
												<option value="N">No</option>
											</c:if>
											<c:if test="${opcionRC == 'N'}">
												<option value="S">Si</option>
												<option value="N" selected>No</option>
											</c:if>
										</select>
									<td class="literal">Estado Cpl</td>
									<td class="literal" width="205px">
										<select id="estadoCpl" name="estadoCpl" class="dato" tabindex="13" style="width:200px" >
											<option value="">Todos</option>
											<c:forEach items="${estadosCpl}" var="estado">
												<option value="${estado.idestado}">${estado.descEstado}</option>
											</c:forEach>
										</select>
									</td>
									<td class="literal">F.Envío</td>
									<td class="literal">
						                    <bind>
						                    	 <input type="text" name="fechaEnvioSbp" id="fecEnvioId" size="11" maxlength="10" class="dato" tabindex="14"
						                    	        onchange="if (!ComprobarFecha(this, document.main3, 'Fecha envio')) this.value='';"
														value="<fmt:formatDate pattern="dd/MM/yyyy" value="${polizaSbp.fechaEnvioSbp}" />" />
						                    </bind>
					                     <input type="button" id="btn_fechaenvio" name="btn_fechaenvio" class="miniCalendario" style="cursor: pointer;" /> 
					                     <label	class="campoObligatorio" id="campoObligatorio_fechaenvio"	title="Campo obligatorio"> *</label>
				                         
									</td>
								</tr>
							</table>
						</tr>
						<tr>
							<table>			
								<tr align="left">
									<td class="literal">Estado Sbp.</td>
									<td class="literal" width="195px">
										<select name="estadoSbp" id="estadoSbp" class="dato" tabindex="15" style="width:185px" >
											<option value="">Todos</option>
											<c:forEach items="${estadosSbp}" var="estado">
												<option value="${estado.idestado}">${estado.descEstado}</option>
											</c:forEach>
										</select>
									</td>
									<td class="literal">Tipo Envío</td>
									<td class="literal">
										<select name="tipoEnvio" id="tipoEnvio" class="dato" tabindex="16" cssStyle="width:180px">
											<option value="">Todos</option>
											<option value="Principal">Principal</option>
											<option value="Suplemento">Suplemento</option>
										</select>
									</td>
									<td class="literal">Detalle</td>
									<td class="literal" width="405px" colspan="3">
										<select name="detalle" id="detalle" class="dato" tabindex="17" style="width:400px" >
											<option value="">Mostrar todos</option>
											<c:forEach items="${detalleErroresSbp}" var="detalle">
												<option value="${detalle.iderror}">${detalle.descError}</option>
											</c:forEach>
										</select>
									</td>
								</tr>
							</table>
						</tr>		
						<tr>
							<table>		
								<tr align="left">
									<td class="literal">Ref. OMEGA</td>
									<td class="literal">
										<input type="text" size="10" maxlength="9" class="dato" name="refPlzOmega" id="refPlzOmega" tabindex="18" onchange="this.value=this.value.toUpperCase();"/>
									</td>
									
									<td class="literal">Nº Sol.</td>
									<td class="literal">
										<input type="text" size="10" maxlength="15" class="dato" name="nSolicitud" id="nSolicitud"   tabindex="19" onchange="this.value=this.value.toUpperCase();"/>
									</td>
								</tr>
							</table>
						</tr>		
						</table>
					</fieldset>
				</div>
			</form>
			
			<!-- Grid Jmesa -->
			<div id="tablaPolizasSbp">
				${listadoPolizasSbp}
			</div>	
			
			
			<div style="width:20%;text-align:center;margin:0 auto;" id="divImprimir">
				<a style="font-family: tahoma, verdana, arial;color: #626262;font-size:11px;">Exportar</a>	
					<a id="btnImprimirExcel" style="text-decoration:none;" href="javascript:exportToExcel()">
						<img src="jsp/img/jmesa/excel.gif"/>
					</a>
			</div>		 
	</div>
			
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
	<!--               -->
	<!-- POPUPS AVISO  -->
	<!--               -->
	
	<!-- *** popUp detalle Errores *** -->
	<div id="divMensajeError" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;left: 30%;">
		
		<div id="header-popup" style="padding:0.4em 1em;position:relative;
		   				              color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;
						              background:#525583;height:15px">
			<div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
			 	Detalle del mensaje
			</div>
			<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
			          font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
				<span onclick="cerrarPopUp()">x</span>
			</a>
		</div>
	<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelInformacion" class="panelInformacion">
				<div id="mensajeError"></div>
	<!-- buttons -->			
				<div style="margin-top:15px;clear: both">
					<a class="bot" href="javascript:cerrarPopUp()">Aceptar</a>				
				</div>
			</div>
		</div>
	</div>
	
	<!-- *** popUp editar Sbp con Cpl*** -->
		<div id="popUpEditarSbpConCpl" class="parcelasRepWindow" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em; top: 165px; z-index:1000000008;">
		     <!--  header popup -->
			 <div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
				        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
				            Editar póliza de Sobreprecio
				        </div>
				        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
				                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
				            <span onclick="hidePopUpEditarSbpConCpl()">x</span>
				        </a>
			 </div>
			 <!--  body popup -->
			 <div class="panelInformacion_content">
						<div id="panelInformacion" class="panelInformacion">
							<div id="txt_mensaje_eleccionCplEnSbp"></div>
						</div>
						<div style="margin-top:15px">
						  <a class="bot" id="btn_aceptarGrabacionDefinitiva" href="javascript:editar_eleccion_SiCpl()" title="Si incluir">SI</a>
						    <a class="bot" href="javascript:editar_eleccion_NoCpl()" title="No incluir">NO</a>
						</div>
			 </div>
		</div>
	
	<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaOficina.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaLineaSbp.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaClaseSbp.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFecha.jsp"%>
	
	<!-- P0073325 - RQ.10, RQ.11 y RQ.12 -->
	<%@ include file="/jsp/moduloPolizas/polizas/cargaDocFirmada.jsp"%>
	
</body>
</html>