<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Mantenimiento de sobreprecio - Sobreprecio</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		
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
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenericaIN.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/imprimir.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/moduloSbp/mantenimiento/sobreprecio.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
		
		<script type="text/javascript">
			 
			function cargarFiltro(){
				<c:if test="${origenLlamada != 'menuGeneral'}">
				
					<c:forEach items="${sessionScope.consultaSobreprecioSbp_LIMIT.filterSet.filters}" var="filtro">
						if ('${filtro.property}' == 'linea.codplan'){
							
							var inputText = document.getElementById('plan');
							inputText.value = '${filtro.value}';
						}else if ('${filtro.property}' == 'linea.codlinea'){
							var inputText = document.getElementById('linea');
							inputText.value = '${filtro.value}';
						}else if ('${filtro.property}' == 'provincia.codprovincia'){
							var inputText = document.getElementById('provincia.codprovincia');
							inputText.value = '${filtro.value}';
						}else if ('${filtro.property}' == 'provincia.nomprovincia'){
							var inputText = document.getElementById('provincia.nomprovincia');
							inputText.value = '${filtro.value}';	
						}else if ('${filtro.property}' == 'cultivo.id.codcultivo'){
							var inputText = document.getElementById('cultivo.id.codcultivo');
							inputText.value = '${filtro.value}';
						}else if ('${filtro.property}' == 'cultivo.descultivo'){
							var inputText = document.getElementById('cultivo.descultivo');
							inputText.value = '${filtro.value}';
						}else if ('${filtro.property}' == 'tipoCapital.codtipocapital'){
							var inputText = document.getElementById('tipoCapital.codtipocapital');
							inputText.value = '${filtro.value}';
						}else if ('${filtro.property}' == 'precioMinimo'){
							var inputText = document.getElementById('precioMinimo');
							inputText.value = '${filtro.value}';
						}else if ('${filtro.property}' == 'precioMaximo'){
							var inputText = document.getElementById('precioMaximo');
							inputText.value = '${filtro.value}';
						}else{
							//var inputText = document.getElementById('${filtro.property}');
							//inputText.value = '${filtro.value}';
						}
					</c:forEach>
					</c:if>
			}

		</script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="javascript:SwitchSubMenu('sub10','sub8');cargarFiltro();">		
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tbody>
					<tr>
						<td align="right">		
							<c:if test="${showModificar == 'true'}">	
								<a class="bot" id="btnModificar"  href="javascript:modificar();">Modificar</a>
							</c:if>	
							<c:if test="${showModificar != 'true'}">	
								<a class="bot" id="btnModificar" style="display:none"  href="javascript:modificar();">Modificar</a>
							</c:if>
							<a class="bot" id="btnReplicar" href="javascript:replicar()">Replicar</a>	
							<a class="bot" id="btnAlta" href="javascript:alta();">Alta</a>				
							<c:if test="${origenLlamada == 'menuGeneral'}">
								<a class="bot" id="btnConsultar" href="javascript:consultarInicial();">Consultar</a>
							</c:if>
							<c:if test="${origenLlamada != 'menuGeneral'}">
								<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
							</c:if>
							<a class="bot" href="javascript:limpiar();">Limpiar</a>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<form name="limpiar" id="limpiar" action="sobreprecioSbp.run" method="post">
			<input type="hidden" name="method" id="method" value="doConsulta"/>								
			<input type="hidden" name="origenLlamada" id="origenLlamada" value="menuGeneral"/>
		</form>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%">
		<p class="titulopag" align="left">Sobreprecio</p>
			
			<form:form name="main3" id="main3" action="sobreprecioSbp.run" method="post" commandName="sobreprecioBean">
				<form:hidden path="id" id="id" />
				<form:hidden path="linea.lineaseguroid" id="lineaseguroid" />
				<input type="hidden" name="origenLlamada" id="origenLlamada" />
				
				<input type="hidden" name="method" id="method" />
				<input type="hidden" name="comarca" id="comarca" />
				<input type="hidden" name="desc_comarca" id="desc_comarca" />
				<input type="hidden" name="termino" id="termino" />
				<input type="hidden" name="desc_termino" id="desc_termino" />
				<input type="hidden" name="subtermino" id="subtermino" />
				<input type="hidden" name="variedad" id="variedad" />
				<input type="hidden" name="desc_variedad" id="desc_variedad" />
				
				<input type="hidden" name="planreplica" id="planreplica" value="${planreplica}"/>
				<input type="hidden" name="lineareplica" id="lineareplica" value="${lineareplica}"/>
				<input type="hidden" name="desc_lineareplica" id="desc_lineareplica" value="${desc_lineareplica}"/>
				
				<!-- Lupa tipo de capital -->
				<input type="hidden" id="codconcepto" name="codconcepto" value="126"/>
				<!-- para filtros en la lupa tipo de capital -->
				<input type="hidden" name="codlinea" id="codlinea" />
				<input type="hidden" name="codplan" id="codplan" /> 
		
				
				
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div class="panel2 isrt" style="width:99%;margin:0 auto">
					<fieldset>
					<legend class="literal">Filtro</legend>
							<table style="width: 100%;">
								<tr align="left">
				
									<td class="literal">Plan</td>
									
									<td class="literal">
										<form:input path="linea.codplan" size="5" maxlength="4" cssClass="dato" id="plan" tabindex="1" onchange="javascript:lupas.limpiarCampos('linea', 'desc_linea');insertaDatosPlanLinea();"/>
									</td>
									
									<td class="literal">L&iacute;nea</td>
									<td class="literal" colspan="2" nowrap>
										<form:input path="linea.codlinea" size="3" maxlength="3" cssClass="dato" id="linea" tabindex="2" onchange="javascript:lupas.limpiarCampos('desc_linea');insertaDatosPlanLinea();"/>
										<form:input path="linea.nomlinea" cssClass="dato" id="desc_linea" size="30" readonly="true"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />										
									</td>
									  
			                        <td class="literal"><nobr>Provincia</td>
									<td class="literal" colspan="2" nowrap>
										
										<form:input  id="provincia" path="provincia.codprovincia" cssClass="dato" size="2" maxlength="2" tabindex="3" onchange="javascript:lupas.limpiarCampos('desc_provincia');"/>
										<nobr><input class="dato"	id="desc_provincia" name="desc_provincia" size="25" readonly="readonly"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Provincia','principio', '', '');"	alt="Buscar Provincia" title="Buscar Provincia" /></nobr>	
				
									</td>
								</tr>
								<tr>
									
									<td class="literal">Cultivo</td>
									<td class="literal" nowrap>
										<form:input  id="cultivo" path="cultivo.id.codcultivo" cssClass="dato" size="3" maxlength="3" tabindex="4" onchange="javascript:lupas.limpiarCampos('desc_cultivo');" />
												<c:if test="${datos.filtro !=null }">
													value='${datos.filtro.codcultivo }'
												</c:if> 
										<input class="dato"	id="desc_cultivo" name="desc_cultivo" size="20" readonly="readonly"	/>
											<c:if test="${datos.filtro !=null }">
												value='${datos.filtro.variedad.cultivo.descultivo }'
											</c:if> 
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:if(validarLinea()){lupas.muestraTabla('CultivoSbp','principio', '', '');}"	alt="Buscar Cultivo" title="Buscar Cultivo" />
									</td>
									
									<td class="literal" nowrap>Tipo capital</td>
									<td class="literal" colspan="1" nowrap>													
											<form:input path="tipoCapital.codtipocapital" id="capital"  size="3" maxlength="3" cssClass="dato" onchange="javascript:lupas.limpiarCampos('desc_capital');" tabindex="5" />
											<input type="text" class="dato"	id="desc_capital" name="desc_capital" size="20" readonly="readonly"/>
											<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('FactoresTipoCapitalIN','principio', '', '');"	alt="Buscar Tipo Capital" title="Buscar Tipo Capital" />																									
									</td>
									
									<td class="literal"><nobr>Precio M&iacute;nimo</nobr></td>
									<td class="literal" >
										<form:input path="precioMinimo" id="precioMinimo" size="8" maxlength="10" cssClass="dato" tabindex="6" />
									</td>
									
									<td class="literal"><nobr>Precio M&aacute;ximo</nobr></td>
									<td class="literal" align="left">
										&nbsp;&nbsp;&nbsp;<form:input path="precioMaximo" id="precioMaximo" size="8" maxlength="10" cssClass="dato" tabindex="7" />
									</td>
								<tr>
									
							</table>
					</fieldset>
				</div>
			</form:form>
			
			<!-- Grid Jmesa -->
			<div id="grid">
				 ${consultaSobreprecioSbp}
			</div>
	</div>
		
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		<!--               -->
		<!-- POPUPS AVISO  -->
		<!--               -->
		
		<!--  *** panel avisos ***  -->
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
					<div id="txt_info_gp" style="width: 70%;display:none" >Existen pólizas elegidas con estado distinto a Grabación Provisional o Pendiente Validación</div>
					<div id="txt_info_none" style="width: 70%;display:none" >No hay pólizas seleccionadas.</div>
				</div>
			</div>
		</div>
			
		<!-- *** popUpAvisos *** -->
		<div id="popUpAvisos" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
		     <!--  header popup -->
			 <div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
		        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
		            Aviso
		        </div>
		        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
		                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
		            <span onclick="hidePopUpAviso('popUpPasarDefinitivaBoton')">x</span>
		        </a>
			 </div>
			 <!--  body popup -->
			 <div class="panelInformacion_content">
				<div id="panelInformacion" class="panelInformacion">
					<div id="txt_mensaje_aviso_1">sin mensaje.</div>
				</div>
				<div style="margin-top:15px">
				    <a class="bot" href="javascript:hidePopUpAviso('popUpPasarDefinitivaBoton')" title="Cancelar">Cancelar</a>
				    <a class="bot" id="btn_aceptarGrabacionDefinitiva" href="javascript:aceptarPopUpPasoDefinitiva('popUpPasarDefinitivaBoton')" title="Cancelar">Aceptar</a>
				</div>
			 </div>
		</div>
		<%@ include file="/jsp/common/lupas/lupaTipoCapitalIN.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaProvincia.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaCultivoSbp.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaLineaReplicar.jsp"%>
		
	</body>
</html>
