<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Acciones sobre Anexos de Modificacion</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
		
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
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/imprimir.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
		<script type="text/javascript" src="jsp/moduloUtilidades/anexoModificacion/listadoAnexoModificacion.js" ></script>
		<script type="text/javascript" src="jsp/moduloUtilidades/anexoModificacion/operacionesAnexoModificacion.js" ></script>
		<script type="text/javascript" src="jsp/moduloUtilidades/anexoModificacion/renovacionCupon.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
		
		<script type="text/javascript">
		function cargarFiltro(){			
		
			<c:forEach items="${sessionScope.listadoAnexoModificacion_LIMIT.filterSet.filters}" var="filtro">
				<c:if test="${origenLlamada != 'menuGeneral'}">
					<c:if test="${filtro.property == 'poliza.colectivo.tomador.id.codentidad'}">
						$('#entidad').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'poliza.oficina'}">
						$('#oficina').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'poliza.linea.codplan'}">
						$('#plan').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'poliza.linea.codlinea'}">
						$('#linea').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'poliza.referencia'}">
						$('#poliza').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'poliza.tipoReferencia'}">
						$('#tipo').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'poliza.asegurado.nifcif'}">
						$('#nifcif').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'poliza.asegurado.fullName'}">
						$('#fullName').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'estado.idestado'}">
						$('#estado').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'fechaEnvioAnexo'}">
						$('#fechaEnvioId').val('${filtro.value}');
					</c:if>		
					<c:if test="${filtro.property == 'tipoEnvio'}">
						<c:if test="${filtro.value == 'FTP'}">
							$('#tipoEnvioId').val('${filtro.value}');
						</c:if>
					</c:if>
					<c:if test="${filtro.property == 'cupon.idcupon'}">
						$('#tipoEnvioId').val('${filtro.value}');
					</c:if>	
					<c:if test="${filtro.property == 'cupon.estadoCupon.id'}">
						$('#estadoCuponId').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'poliza.usuario.delegacion'}">
						$('#delegacion').val('${filtro.value}');
					</c:if>
						<c:if test="${filtro.property == 'poliza.colectivo.subentidadMediadora.id.codsubentidad'}">
						$('#subentmediadora').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'poliza.colectivo.subentidadMediadora.id.codentidad'}">
						$('#entmediadora').val('${filtro.value}');
					</c:if>
				</c:if>
			</c:forEach>
		}
		</script>
		<script type="text/javascript">	
			$(document).ready(function(){
				<c:if test="${empty listadoAnexoModificacion}">					
					$('#divImprimir').hide(); 									
				</c:if> 				
			});
			
			function imprimirExcel(size) {
				
				var frm = document.getElementById('main3');
				
				// MPM - 05/09/12
				// Si el numero de registros a imprimir es menor al permitido, se lanza el informe en ventana nueva
			//	if(size < $("#numRegImpresion").val()){
					frm.target="_blank";
				//	frm.formato.value = formato;
					frm.operacion.value = 'imprimirInforme';
					frm.submit();
			//	}
			}
		</script>
	</head>
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="javascript:SwitchMenu('sub4');cargarFiltro();">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		<!-- botones de la página -->
		<div id="buttons" >
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
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
						</td>
					</tr> 
				</tbody>
			</table>
		</div>
		
		
		<form:form name="main" id="main" action="declaracionesModificacionPoliza.html" method="post" commandName="anexoModificacionBean">
			<input type="hidden" id="method" name="method" />
			<form:hidden path="id" id="id"/>
			<form:hidden path="poliza.idenvio" id="idenvio"/>
			<form:hidden path="poliza.linea.codlinea" id="codlinea"/>	
			<form:hidden path="poliza.linea.codplan" id="codplan"/>							
			<form:hidden path="poliza.linea.nomlinea" id="nomlinea"/>
			<form:hidden path="poliza.referencia" id="refPoliza"/>
			<form:hidden path="cupon.idcupon" id="idCupon"/>
			<input type="hidden" name="idPoliza" id="idPoliza"/>
			<input type="hidden" name="vieneDeListadoAnexosMod" id="vieneDeListadoAnexosMod" value="true"/>
			<input type="hidden" name="idAnexoCaducado" id="idAnexoCaducado"/>
		</form:form>
		
		<form name="print" id="print" action="informes.html" method="post">
			<input type="hidden" name="method" id="methodPrint" value="doInformeAnexoModificacion"/>
			<input type="hidden" name="idAnexo" id="idAnexo"/>			
			<input type="hidden" id="idPoliza" name="idPoliza"/>
			<input type="hidden" name="aleatorio" id="aleatorio"/> <!-- para evitar problemas con la cache -->
		</form>
		<form name="printCpl" id="printCpl" action="informes.html" method="post">
			<input type="hidden" name="method" id="methodPrintCpl" value="doInformeAnexoModificacionComplementario"/>
			<input type="hidden" name="idAnexo" id="idAnexo"/>
			<input type="hidden" id="idPoliza" name="idPoliza"/>
			<input type="hidden" name="aleatorio" id="aleatorio"/> <!-- para evitar problemas con la cache -->
		</form>
		
		<form name="validarAnexo" id="validarAnexo" action="confirmacionModificacion.html" method="post">
			<input type="hidden" id="method" name="method" value="doValidarAnexo" />
			<input type="hidden" id="redireccion" name="redireccion" value="listadoAnexos"/>
			<input type="hidden" id="idCuponValidar" name="idCuponValidar"/>
		</form>
		
		<form name="main4" id="main4" action="declaracionesModificacionPolizaComplementaria.html" method="post">
			<input type="hidden" name="method" id="methodCpl"/>
			<input type="hidden" name="idAnexo" id="idAnexoCpl"/>
			<input type="hidden" name="idPoliza" id="idPolizaCpl"/>
			<input type="hidden" name="vieneDeListadoAnexosMod" id="vieneDeListadoAnexosMod" value="true"/>
		</form>
		<form name="limpiar" id="limpiar" action="anexoModificacionUtilidades.run" method="post">
			<input type="hidden" name="method" id="method" value="doConsulta"/>								
			<input type="hidden" name="origenLlamada" id="origenLlamada" value="menuGeneral"/>
		</form>
		<form:form name="polizaActualizada" id="polizaActualizada" action="polizaActualizada.html" method="post" commandName="anexoModificacionBean" target="_blank" >
			<input type="hidden" id="method" name="method" value="doVerPolizaActualizada" />
			<form:hidden path="poliza.referencia" id="refPolizaPlzAct"/>
			<form:hidden path="poliza.linea.codplan" id="codPlanPlzAct"/>
			<form:hidden path="poliza.tipoReferencia" id="tipoRefPlzAct"/>
			<form:hidden path="id" id="idAnexoPolActualizada"/>
			<input type="hidden" name="aleatorio" id="aleatorio"/> <!-- para evitar problemas con la cache -->			
		</form:form>
		<form:form name="imprimirAnexo" id="imprimirAnexo" action="polizaActualizada.html" method="post" commandName="capitalAseguradoBean">
			<input type="hidden" name="method" id="methodImprimir" value="doImprimirAnexoPpal"/>
			<input type="hidden" name="imprimirAnexoWS"  id="imprimirAnexoWS"  value="true"/>
			<input type="hidden" name="refPoliza" id="refPoliza" /> 
			<form:hidden path="parcela.anexoModificacion.cupon.idcupon" id="idCuponImprimir"/>
			<form:hidden path="parcela.anexoModificacion.id" id="idImprimir"/>
			<input type="hidden" name="aleatorio" id="aleatorio"/> <!-- para evitar problemas con la cache -->
		</form:form>
		<form name="acuseReciboConfirmacion" id="acuseReciboConfirmacion" action="confirmacionModificacion.html" method="post">
			<input type="hidden" name="method" id="method" value="doVerAcuseConfirmacion"/>								
			<input type="hidden" name="idAnexoAcuse" id="idAnexoAcuse"/>
			<input type="hidden" name="idPolizaAcuse" id="idPolizaAcuse"/>
			<input type="hidden" name="idCuponAcuse" id="idCuponAcuse"/>
			<input type="hidden" name="redireccion" id="redireccion" value="listadoAnexos"/>
		</form>
		<!-- Formulario para vconsultar la relacion de incidencias asociadas a una poliza en agroseguro -->
		<form:form name="impresionIncidenciasMod" id="impresionIncidenciasMod" action="impresionIncidenciasMod.html" method="post" commandName="anexoModificacionBean">
			<input type="hidden" id="method" name="method" value="doImprimirIncidencias" />
			<form:hidden path="poliza.linea.codlinea" id="codlinea"/>	
			<form:hidden path="poliza.linea.codplan" id="codplan"/>							
			<form:hidden path="poliza.linea.nomlinea" id="nomlinea"/>
			<form:hidden path="poliza.referencia" id="refPoliza"/>
			<form:hidden path="poliza.idpoliza" id="idPoliza"/>
			<input type="hidden" id="nombreCompleto" name="nombreCompleto" value="${poliza.asegurado.nombreCompleto}"/>
			<form:hidden path="poliza.codmodulo" id="codmodulo"/>
			<input type="hidden" id="fechaEnvio" name="fechaEnvio" value="${fechaEnvioAnexo}"/>
			<input type="hidden" id="idCuponImpresion" name="idCuponImpresion"/>
		</form:form>
		
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%" >
		<p class="titulopag" align="left">Acciones sobre Anexo de Modificación</p>			
			 
			<form:form name="main3" id="main3" action="anexoModificacionUtilidades.run" method="post" commandName="anexoModificacionBean">
									            
	            <input type="hidden" name="method" id="method" value="doConsulta" />	            									            
	            <input type="hidden" name="primeraBusqueda" id="primeraBusqueda" />
	            <input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
	            <input type="hidden" name="grupoOficinas" id="grupoOficinas" value="${grupoOficinas}"/>
	           	<input type="hidden" name="grupoOficinas" id="grupoOficinas" value=""/>
				<input type="hidden" name="propiedadGrupo" id="propiedadGrupo" value=""/>
				<input type="hidden" name="vieneDeListadoAnexosMod" id="vieneDeListadoAnexosMod" value="${vieneDeListadoAnexosMod}"/>				
				<input type="hidden" name="fechaEnvioId.day" value="">
				<input type="hidden" name="fechaEnvioId.month" value="">
				<input type="hidden" name="fechaEnvioId.year" value="">							
				<input type="hidden" name="entidadSubstr" id="entidadSubstr" value="">
				<input type="hidden" name="operacion" id="operacion" value="">			
		        <input type="hidden" id="externo" value="${externo}" />
		        <%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div class="panel2 isrt">
					<fieldset>
					<legend class="literal">Filtro</legend>
						<table style="width:100%">
							<!-- Primera fila -->
							<tr align="left">
								<td class="literal">Entidad</td>
								<td class="literal" colspan="3">
									<c:if test="${perfil == 0 || perfil == 5}">
										<form:input path="poliza.colectivo.tomador.id.codentidad" size="3" maxlength="4" cssClass="dato" id="entidad"  onchange="javascript:lupas.limpiarCampos('desc_entidad', 'oficina', 'desc_oficina');"/>
										<form:input path="poliza.colectivo.tomador.entidad.nomentidad" cssClass="dato	"id="desc_entidad" size="30" readonly="true"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');" alt="Buscar Entidad" title="Buscar Entidad" />
									</c:if>
									<c:if test="${perfil > 0 && perfil < 5}">
										<form:input path="poliza.colectivo.tomador.id.codentidad" size="3" maxlength="4" cssClass="dato" disabled="disabled" readonly="true" id="entidad" />
										<form:input path="poliza.colectivo.tomador.entidad.nomentidad" cssClass="dato	"id="desc_entidad" size="30" readonly="true"/>
									</c:if>
									Oficina
									<c:if test="${perfil == 0 || perfil ==1 || perfil == 5}">
										<form:input path="poliza.oficina" size="3" maxlength="4" cssClass="dato" id="oficina"  onchange="javascript:lupas.limpiarCampos('desc_oficina');"/>
										<form:input path="" cssClass="dato"	id="desc_oficina" size="20" readonly="true"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Oficina','principio', '', '');" alt="Buscar Oficina" title="Buscar Oficina" />
									</c:if>
									<c:if test="${perfil > 2  && perfil < 5}">
										<form:input path="poliza.oficina" size="3" maxlength="4" cssClass="dato" readonly="true" id="oficina" />
										<form:input path="poliza.nombreOfi" cssClass="dato"	id="desc_oficina" size="20" readonly="true"/>
									</c:if>
									<c:if test="${perfil == 2}">
												<form:input path="poliza.oficina" size="3" maxlength="4" cssClass="dato" id="oficina" tabindex="2" onchange="javascript:lupas.limpiarCampos('desc_oficina');"/>
												<form:input path="poliza.nombreOfi" cssClass="dato"	id="desc_oficina" size="20" readonly="true"/>
												<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Oficina','principio', '', '');" alt="Buscar Oficina" title="Buscar Oficina" />
									</c:if>		
									
								</td>
								<td class="literal" colspan="1">E-S Mediadora </td>	
									<td class="literal">
									<c:if test="${externo == 0 and perfil !=4 }"> <!--  es interno -->
										<form:input	path="poliza.colectivo.subentidadMediadora.id.codentidad" size="3" maxlength="4"	cssClass="dato" id="entmediadora" onchange="javascript:lupas.limpiarCampos('subentmediadora');UTIL.subStrEntidad();" />
										<form:input	path="poliza.colectivo.subentidadMediadora.id.codsubentidad" size="3" maxlength="4" cssClass="dato" id="subentmediadora"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');"	alt="Buscar SubEntidad Mediadora" title="Buscar SubEntidad Mediadora" />
									</c:if>
									<c:if test="${externo == 1 or (externo==0 and perfil==4)}"> <!--  es externo -->
										<form:input	path="poliza.colectivo.subentidadMediadora.id.codentidad" size="3" maxlength="4"	cssClass="dato" id="entmediadora" tabindex="3"  readonly="true"/>
										<form:input	path="poliza.colectivo.subentidadMediadora.id.codsubentidad" size="3" maxlength="4" cssClass="dato" id="subentmediadora"  readonly="true"/>
										
									</c:if>
									
									Delegación
									
									<c:if test="${externo == 1}"><!--  es externo -->
										<c:if test="${perfil == 1}">
											<form:input	path="poliza.usuario.delegacion" size="3" maxlength="4" cssClass="dato" id="delegacion"  />
										</c:if>
										<c:if test="${perfil == 3}">
											<form:input	path="poliza.usuario.delegacion" size="3" maxlength="4" cssClass="dato" id="delegacion" readonly="true"/>
										</c:if>
									</c:if>
									<c:if test="${externo == 0}"> <!--  es interno -->
										<form:input	path="poliza.usuario.delegacion" size="3" maxlength="4" cssClass="dato" id="delegacion" />
									</c:if>
									</td>
								</td>
								<td class="literal">
									<form:hidden path="poliza.usuario.codusuario" id="codusuario" />
								</td>									
							</tr>	
							<!-- Segunda fila -->
							<tr align="left">
									<td class="literal">Plan</td>
									<td class="literal" colspan="3">
										<form:input path="poliza.linea.codplan" size="5" maxlength="4" cssClass="dato" id="plan"  onchange="javascript:lupas.limpiarCampos('linea', 'desc_linea');"/>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;L&iacute;nea &nbsp;
										
										<form:input path="poliza.linea.codlinea" size="3" maxlength="3" cssClass="dato" id="linea"  onchange="javascript:lupas.limpiarCampos('desc_linea');"/>
										<form:input path="poliza.linea.nomlinea" cssClass="dato" id="desc_linea" size="40" readonly="true"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />							
									</td>
									<td class="literal">Estado AM&nbsp;&nbsp;</td>
									<td class="literal" >
										<form:select path="estado.idestado" id="estado" cssClass="dato"  cssStyle="width:200px">								
											<form:option value="">Todos</form:option>
											<c:forEach items="${estados}" var="estado">
												<form:option value="${estado.idestado}">${estado.descestado}</form:option>
											</c:forEach>
										</form:select>
									</td>
							</tr>	
							<!-- Tercera fila -->
							<tr>
									<td class="literal">P&oacute;liza</td>
									<td class="literal">
										<form:input path="poliza.referencia" id="poliza" size="20" maxlength="15" cssClass="dato"  onchange="this.value=this.value.toUpperCase();"/>
									</td>
									
									<td class="literal">Asegurado</td>
									<td class="literal" colspan="1">
										<form:input path="poliza.asegurado.fullName" id="fullName" size="35" maxlength="39" cssClass="dato"  onchange="this.value=this.value.toUpperCase();"/>&nbsp;&nbsp;
									</td>
									
									<td class="literal">Estado Cup&oacute;n&nbsp;&nbsp;</td>
									<td class="literal" >
										<form:select path="cupon.estadoCupon.id" id="estadoCuponId" cssClass="dato" cssStyle="width:200px">								
											<form:option value="">Todos</form:option>
											<c:forEach items="${estadosCupon}" var="estadoCupon">
												<form:option value="${estadoCupon.id}">${estadoCupon.estado}</form:option>
											</c:forEach>
										</form:select>
									</td>
							</tr>
							<!-- Cuarta fila -->
							<tr align="left">		
								<td class="literal">CIF/NIF Aseg</td>
									<td class="literal">
										<form:input path="poliza.asegurado.nifcif" id="nifcif" size="25" maxlength="9" cssClass="dato"  onchange="this.value=this.value.toUpperCase();this.value=generales.lpadNIF(this.value);"/>
									</td>
									
								<td class="literal" colspan="2" valign="top">
									<table>
										<tr>
										<td class="literal">Fec. Env&iacute;o</td>
										<td class="literal">
											<bind>
					                    	 <input type="text" name="fechaEnvioAnexo" id="fechaEnvioId" size="8"  maxlength="10" class="dato" 
					                    	 		onblur="limpiaAlertas(); if (!ComprobarFecha(this, document.main3, 'Fecha de envio')) this.value=''"
													value="<fmt:formatDate pattern="dd/MM/yyyy" value="${fechaEnvioAnexo}" />" />
						                    </bind>
					                     	<input type="button" id="btn_fechaEnvio" name="btn_fechaEnvio" class="miniCalendario" style="cursor: pointer;" />
										</td>	

										
										
										<td class="literal">Tipo Pol.</td>
										<td class="literal">
											<form:select path="poliza.tipoReferencia" id="tipo" cssClass="dato"  cssStyle="width:75px" >								
												<form:option value="">Todos</form:option>
												<form:option value="P">Principal</form:option>
												<form:option value="C">Complementaria</form:option>
											</form:select>
										</td>
													</tr></table>
													
													
								</td>
								<td class="literal">Tipo A.M</td>
								<td class="literal">
									<form:input path="tipoEnvio" id="tipoEnvioId" size="15" maxlength="15" cssClass="dato"  onchange="this.value=this.value.toUpperCase();"/>
									&nbsp;(ftp, nº de cupón)									
								</td>
							</tr>	
										
						</table>
					</fieldset>
				</div>
			</form:form>
			
			<!-- Grid Jmesa -->
			<div id="grid">
		  		${listadoAnexoModificacion}		  							               
			</div> 	
			<div style="width:20%;text-align:center;margin:0 auto;" id="divImprimir">
	        	 <a style="font-family: tahoma, verdana, arial;color: #626262;font-size:11px;">Imprimir</a>	
				 <a id="btnImprimirExcel" style="text-decoration:none;" href="javascript:imprimirExcel(0)">
				 	<img src="jsp/img/jmesa/excel.gif"/>
				 </a>
			</div>
	</div>
			
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
	<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaOficina.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFecha.jsp"%>
</body>
</html>