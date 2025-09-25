<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<html>
	<head>
		<title>Mantenimiento de usuarios</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
				
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
        <script type="text/javascript" src="jsp/js/calendar.js"></script>
        <script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
        <script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js" charset="UTF-8"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/moduloTaller/mtoUsuarios/mtoUsuarios.js" charset="UTF-8"></script>
		<script type="text/javascript" src="jsp/moduloTaller/mtoUsuarios/mtoUsuariosCambioMasivo.js" ></script>
		
		<%@ include file="/jsp/js/draggable.jsp"%>
		
		<script type="text/javascript">
			 
			 function cargarFiltro() {
				 
				 	if($('#origenLlamada').val() == 'menuGeneral') {
						$('#fechaLimiteId').val('');
					}
				 
				 <c:if test="${origenLlamada != 'menuGeneral'}">	
					<c:forEach items="${sessionScope.consultaUsuarios_LIMIT.filterSet.filters}" var="filtro">
						
						<c:if test="${filtro.property == 'codusuario'}">
							$('#codusuario').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'nombreusu'}">
							$('#nombreusu').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'tipousuario'}">
							$('#tipousuario').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'oficina.id.codentidad'}">
							$('#entidad').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'oficina.id.codoficina'}">
							$('#oficina').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'subentidadMediadora.id.codentidad'}">
							$('#esmediadora').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'subentidadMediadora.id.codsubentidad'}">
							$('#subentmediadora').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'delegacion'}">
							$('#delegacion').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'cargaPac'}">
							$('#cargaPac').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'email'}">
							$('#email').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'financiar'}">
							$('#financiar').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'impMinFinanciacion'}">
							$('#impMinFinanciacion').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'impMaxFinanciacion'}">
							$('#impMaxFinanciacion').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'fechaLimite'}">
							$('#fechaLimiteId').val('${filtro.value}');
						</c:if>
					</c:forEach>
				</c:if>
			} 
		
		</script>

	</head>
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="cargarFiltro();">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuLateralTaller.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		<!-- botones de la pagina -->
		<div id="buttons" >
			<table width="97%" cellspacing="2" cellpadding="2" border="0">
				<tbody>
					<tr>
						<td>
							<a class="bot" id="btnIncrFechaLimite" href="javascript:incrementarFechaLimite();">Incr. Fecha L&iacute;mite</a>						
							<a class="bot" id="btnCambioMasivo"  href="javascript:cambioMasivo();">Cambio Masivo</a>
						</td>
						<td align="right">
							<c:if test="${showModificar == 'true'}">	
								<a class="bot" id="btnModificar"  href="javascript:modificar();">Modificar</a>
							</c:if>	
							<c:if test="${showModificar != 'true'}">	
								<a class="bot" id="btnModificar" style="display:none"  href="javascript:modificar();">Modificar</a>
							</c:if>
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
		<form name="limpiar" id="limpiar" action="mtoUsuarios.run" method="post">
			<input type="hidden" name="method" id="method" value="doConsulta"/>
			<input type="hidden" name="origenLlamada" id="origenLlamada" value="menuGeneral"/>
		</form>
		<!-- Contenido de la pagina -->
		<div class="conten" style="padding:3px;width:97%" >
		<p class="titulopag" align="left">MANTENIMIENTO USUARIOS</p>
			
			<form:form name="main3" id="main3" action="mtoUsuarios.run" method="post" commandName="usuarioBean">
				
				<input type="hidden" name="method" id="method" />
				<input type="hidden" name="origenLlamada" id="origenLlamada" />	
				<input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
				<input type="hidden" name="grupoOficinas" id="grupoOficinas" value=""/>
				<input type="hidden" name="perfilIni" id="perfilIni"/>
				<input type="hidden" name="externoIni" id="externoIni"/>
				<input type="hidden" name="entidadSubstr" id="entidadSubstr" value="">
				<input type="hidden" name="alerta" id="alerta" value ="${alerta}"/>
				
				<input type="hidden" name="listaIdsMarcados" id="listaIdsMarcados" value=""/>
				<input type="hidden" name="marcaTodos" id="marcaTodos" value="false"/>
				<input type="hidden" name="listaIdsTodos" id="listaIdsTodos" value="${listaIdsTodos}"/>
				<input type="hidden" name="codusuInicial" id="codusuInicial" value="${codusuInicial}"/>
				<input type="hidden" name="fechaCargaFin.year" value="">		
				<input type="hidden" name="fechaLimiteId.day" value="">
				<input type="hidden" name="fechaLimiteId.month" value="">
				<input type="hidden" name="fechaLimiteId.year" value="">
				
				
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div class="panel2 isrt" style="width: 95%;margin:0 auto;">
					<fieldset>
					<legend class="literal">Filtro</legend>
						<table>
							<tr align="left">
							<td class="literal" style="width: 15%" >Usuario</td>
								<td class="literal" rowSpan="1" colspan="3" nowrap>
									<form:input path="codusuario" id="codusuario" size="8" maxlength="8" cssClass="dato" tabindex="1" onchange="this.value=this.value.toUpperCase();lupas.limpiarCampos('nombreusu')"/>
									<form:input id="nombreusu" path="nombreusu"  cssClass="dato" tabindex="-1"   size="50" maxlength="100" onchange="this.value=quitaAcentos(this.value);this.value=this.value.toUpperCase();"/>
									<img  border="1" id="iconoLupa" src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('UsuarioNombre','principio', '', '');" alt="Buscar Usuario"/>
									<label class="campoObligatorio" id="campoObligatorio_codusuario" title="Campo obligatorio"> *</label>
								</td>
								<td class="literal" align="left">Perfil</td>
								<td class="literal">
								<c:if test="${esExterno == 0}">
									<form:select path="tipousuario" tabindex="2	" cssClass="dato"	cssStyle="width:70" id="tipousuario">
										<form:option value="">Todos</form:option>
										<form:option value="0">0</form:option>
										<form:option value="1">1</form:option>
										<form:option value="2">2</form:option>
										<form:option value="3">3</form:option>
										<form:option value="4">4</form:option>
										<form:option value="5">5</form:option>
									</form:select>
								</c:if>
								<c:if test="${esExterno == 1}">
								<form:select path="tipousuario" tabindex="2" cssClass="dato"	cssStyle="width:70" id="tipousuario">
										<form:option value="">Todos</form:option>
										<form:option value="1">1</form:option>
										<form:option value="3">3</form:option>
										
								</form:select>
								</c:if>
								<label class="campoObligatorio" id="campoObligatorio_tipousuario" title="Campo obligatorio"> *</label>
								</td>
							</tr>
							<tr align="left">
								<td class="literal" style="width: 15%">Entidad</td>
								<td class="literal" colspan="3">
									<form:input path="oficina.id.codentidad" size="5" maxlength="4" cssClass="dato" id="entidad" tabindex="3" onchange="javascript:lupas.limpiarCampos('desc_entidad', 'oficina', 'desc_oficina');"/>
									<form:input path="oficina.entidad.nomentidad" tabindex="-1" cssClass="dato"	id="desc_entidad" size="40" readonly="true"/>
									<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');" alt="Buscar Entidad" title="Buscar Entidad" />
									<label class="campoObligatorio" id="campoObligatorio_entidad" title="Campo obligatorio"> *</label>
								</td>
								<td class="literal" align="left">Oficina</td>
								<td class="literal" nowrap align="left">
									<form:input path="oficina.id.codoficina" size="5" maxlength="4" cssClass="dato" id="oficina" tabindex="4" onchange="javascript:lupas.limpiarCampos('desc_oficina');"/>
									<form:input path="oficina.nomoficina" cssClass="dato"	id="desc_oficina" size="20" tabindex="-1"/>
									<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Oficina','principio', '', '');" alt="Buscar Oficina" title="Buscar Oficina" />
									<label class="campoObligatorio" id="campoObligatorio_oficina" title="Campo obligatorio"> *</label>
								</td>
								
							</tr>
							<tr >
								<td class="literal" style="width: 15%">Entidad mediadora</td>
								<td style="width: 10%">
									&nbsp<form:input path="subentidadMediadora.id.codentidad" size="4" maxlength="4"	cssClass="dato" id="entmediadora" onchange="javascript:lupas.limpiarCampos('subentmediadora', 'desc_subentmediadora');UTIL.subStrEntidad();" tabindex="5"/>
									<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('EntidadMediadora','principio', '', '');" alt="Buscar Entidad Mediadora" title="Buscar Entidad Mediadora" />
									<label class="campoObligatorio" id="campoObligatorio_entmediadora" title="Campo obligatorio"> *</label>
								</td>
								
								<td class="literal" colspan="2" nowrap>Subentidad mediadora&nbsp
									&nbsp<form:input	path="subentidadMediadora.id.codsubentidad" size="4" maxlength="4" cssClass="dato" id="subentmediadora" onchange="javascript:lupas.limpiarCampos('desc_subentmediadora');" tabindex="6"/>
									<form:input path="subentidadMediadora.nomsubentidad" tabindex="-1" cssClass="dato"	id="desc_subentmediadora" size="35" readonly="true"/>
									<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');"	alt="Buscar SubEntidad Mediadora" title="Buscar SubEntidad Mediadora" />
									<label class="campoObligatorio" id="campoObligatorio_subentmediadora" title="Campo obligatorio"> *</label>
								
								</td>
								<td class="literal" align="center">Delegaci&oacute;n</td>
								<td>
									&nbsp<form:input	path="delegacion" size="4" maxlength="4" cssClass="dato" id="delegacion" tabindex="7"/>
									<label class="campoObligatorio" id="campoObligatorio_delegacion" title="Campo obligatorio"> *</label>
								</td>
							</tr>
							<tr>
								<td class="literal" style="width: 15%">Tipo&nbsp</td>
								<td class="literal">
									<form:select path="externo" cssClass="dato" id="externo" cssStyle="width:65"  onchange="" tabindex="8">
										<form:option value="">Todos</form:option>
										<form:option value="1">Externo</form:option>
										<form:option value="0">Interno</form:option>
									</form:select>
								</td>
								<td class="literal">Carga PAC&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
									<form:select path="cargaPac" cssClass="dato" id="cargaPac" cssStyle="width:65" onchange="" tabindex="9">
										<form:option value="">Todos</form:option>
										<form:option value="0">No</form:option>
										<form:option value="1">S&iacute;</form:option>
									</form:select>
									<label class="campoObligatorio" id="campoObligatorio_cargaPac" title="Campo obligatorio"> *</label>
								</td>
								<td class="literal" align="center" colspan="2" nowrap>E-mail</td>
								<td>
									<form:input id="email" path="email"  cssClass="dato" tabindex="-1"   size="35" maxlength="100" onchange=""/>
								</td>
							</tr>
							<tr>
								<td class="literal" style="width: 15%">Financiar</td>
											<td class="literal">
												<form:select path="financiar" id="financiar" cssClass="dato" cssStyle="width:65"  onchange="" tabindex="11">
													<form:option value="">Todos</form:option>
													<form:option value="0">No</form:option>
													<form:option value="1">S&iacute;</form:option>
												</form:select>
												<label class="campoObligatorio" id="campoObligatorio_financiar" title="Campo obligatorio"> *</label>
											</td>
											<td colspan="5">
												<table style="border:2px;" align="left" style="width: 103%">
														<tr>
															<td class="literal" style="width: 19%">Importe m&iacute;nimo</td>
															<td class="literal">
																<form:input path="impMinFinanciacion" id="impMinFinanciacion" size="12" maxlength="12" cssClass="dato" tabindex="12" onchange=""/>
															</td>
															<td class="literal">Importe m&aacute;ximo</td>
															<td class="literal">
																<form:input path="impMaxFinanciacion" id="impMaxFinanciacion" size="12" maxlength="12" cssClass="dato" tabindex="13" onchange=""/>
															</td>
															<td class="literal">Fecha l&iacute;mite</td>
															<td class="literal">
												                    <spring:bind path="fechaLimite">
												                    
						
												                    	 	<input type="text" id="fechaLimiteId" onblur="if (!ComprobarFecha(this, document.main3, 'Fecha limite')) this.value='';" name="fechaLimite" size="12" maxlength="11" tabindex="14" class="dato"
												                    	 	       
												                    	           value="<fmt:formatDate pattern="dd/MM/yyyy" value="${usuarioBean.fechaLimite}" />" />
												                    </spring:bind>
											                     		<input type="button" id="btn_fechaLimite" name="btn_fechaLimite" class="miniCalendario" style="cursor: pointer;" />
										                	</td>
										                </tr>
								            	</table>
						          			</td>
							</tr>
						</table>
					</fieldset>
				</div>
			</form:form>
			
			<form:form name="frmBorrar" id="frmBorrar" action="mtoUsuarios.run" method="post" commandName="usuarioBean">
				
				<input type="hidden" name="method" id="methodBorrar" />
				<form:hidden path="codusuario" id="codUsuarioBorrar"/>
			</form:form> 
			
			<!-- Grid Jmesa -->
			<div id="grid" style="width: 80%;margin:0 auto;">
		  		${consultaUsuarios}		  							               
			</div> 	
	</div>
	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<%@ include file="/jsp/common/static/overlayCambioMasivo.jsp"%>
	
	<!-- ************* -->
		<!-- POPUP  AVISO  -->
		<!-- ************* -->
		
		<div id="popUpAvisos" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
		     <!--  header popup -->
			 <div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
		        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">Aviso</div>
		        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
		                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
		            <span onclick="hidePopUpAviso()">x</span>
		        </a>
			 </div>
			 <!--  body popup -->
			 <div class="panelInformacion_content">
				<div id="panelInformacion2" class="panelInformacion">
					<div id="txt_mensaje_aviso"></div>
				</div>
				<div style="margin-top:15px">
				 	    <a class="bot" id="btn_hidePopUpAviso" href="javascript:hidePopUpAviso()" title="Aceptar">Aceptar</a>
				</div>
			 </div>
		</div>
	
	<!-- PANEL CAMBIO MASIVO USUARIOS -->
		<!-- ***************************-->


		<div id="panelCambioMasivoUsuarios" class="panelCambioMasivo" style="left: 6%; width: 90%; color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">

		     <!--  header popup -->
			<div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
			    <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">Cambio masivo</div>
				<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
					  <span onclick="cerrarCambioMasivoUsuarios()">x</span>
				</a>
			</div>
			<!--  body popup -->
			<div class="panelInformacion_content">
				<div id="panelInformacionCM" class="panelInformacion">
					<div id="txt_mensaje_cm" style="color:red;display:block;font-size:12px;font-style:italic;font-weight:bold;line-height:20px;"></div>
						<form:form name="main" id="main" action="mtoUsuarios.run" method="post" commandName="usuarioBean">
						
							<input type="hidden" name="method" id="method" value="doCambioMasivo"/>
							<input type="hidden" name="listaIdsMarcados_cm" id="listaIdsMarcados_cm" value=""/>							
							
							<form:hidden path="oficina.id.codentidad" id="entidad_cm_sel" />
	  						<form:hidden path="tipousuario" id="tipousuario_cm_sel" />
	  						
	  						<form:hidden path="oficina.id.codoficina" id="oficina_cm_sel" />
	  						<form:hidden path="subentidadMediadora.id.codentidad" id="entmed_cm_sel" />
	  						<form:hidden path="subentidadMediadora.id.codsubentidad" id="submed_cm_sel" />
	  						<form:hidden path="delegacion" id="delegacion_cm_sel" />
	  						<form:hidden path="cargaPac" id="cargaPac_cm_sel"/>
	  						
	  						<form:hidden path="email" id="email_cm_sel"/>
	  						
	  						<form:hidden path="financiar" id="financiar_cm_sel"/>
	  						<form:hidden path="impMinFinanciacion" id="impMinFinanciacion_cm_sel"/>
	  						<form:hidden path="impMaxFinanciacion" id="impMaxFinanciacion_cm_sel"/>
	  						<form:hidden path="fechaLimite" id="fechaLimite_cm_sel"/>
	  						
	  						<input type="hidden" name="entidadSubstr_cm" id="entidadSubstr_cm" value="">
	  						
	  						<input type="hidden" name="fechaLimite_cm.day" value="">
							<input type="hidden" name="fechaLimite_cm.month" value="">
							<input type="hidden" name="fechaLimite_cm.year" value="">
	  						
	  							  						
							<div class="panel2 isrt" style="width:95%">
							<fieldset>
							<table align="center" style="width: 101%">
							
								
								 <tr align="left">
								   
									<td class="literal" style="width: 12%">Perfil</td>
									<td class="literal">
									
										<select  class="dato"	style="width:90" id="tipousuario_cm" name="tipousuario_cm" tabindex="1">
												<option value="">Todos</option>
												<option value="1">1</option>
												<option value="3">3</option>
												
										</select>
									
									<label class="campoObligatorio" id="campoObligatorio_tipousuario" title="Campo obligatorio"> *</label>
									</td>
	
									<td class="literal">Entidad</td>
									<td>
										&nbsp<input type="text" size="5" maxlength="4" class="dato" id="entidad_cm" name="entidad_cm" tabindex="2" onchange="javascript:lupas.limpiarCampos('desc_entidad_cm', 'oficina_cm', 'desc_oficina_cm','entmediadora_cm','subentmediadora_cm', 'desc_subentmediadora_cm');"/>
										<input class="dato"	id="desc_entidad_cm" name="desc_entidad_cm" size="40" readonly="true"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:$('#txt_mensaje_cm').hide();lupas.muestraTabla('EntidadCM','principio', '', '');" alt="Buscar Entidad" title="Buscar Entidad" />
										<label class="campoObligatorio" id="campoObligatorio_entidad" title="Campo obligatorio"> *</label>
									</td>
									<td class="literal">Oficina</td>
									<td>
										<input type="text" size="5" maxlength="4" class="dato" id="oficina_cm" name="oficina_cm" tabindex="2" readonly="true" onchange="javascript:lupas.limpiarCampos('desc_oficina_cm');"/>
									</td>
									<td width="15%">
										<input class="dato"	id="desc_oficina_cm" name="desc_oficina_cm" size="20" tabindex="2" readonly="true"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="comprobarEntidad(0);" alt="Buscar Oficina" title="Buscar Oficina" />
										<label class="campoObligatorio" id="campoObligatorio_oficina" title="Campo obligatorio"> *</label>
									</td>	
								</tr>
								<tr>
									<td class="literal" style="width: 12%">Entidad mediadora</td>
									<td class="literal">
										<input size="4" maxlength="4"	class="dato" id="entmediadora_cm" name="entmediadora_cm" readonly="true" onchange="javascript:lupas.limpiarCampos('subentmediadora_cm', 'desc_subentmediadora_cm');UTIL.subStrEntidadCM();" tabindex="2"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:comprobarEntidad(1);" alt="Buscar Entidad Mediadora" title="Buscar Entidad Mediadora" />
										<label class="campoObligatorio" id="campoObligatorio_entmediadora" title="Campo obligatorio"> *</label>
									<td class="literal">Subentidad mediadora</td>
									<td>
										&nbsp<input size="5" maxlength="4" class="dato" id="subentmediadora_cm" name="subentmediadora_cm" readonly="true" onchange="javascript:lupas.limpiarCampos('desc_subentmediadora_cm');" tabindex="2"/>
										<input class="dato"	id="desc_subentmediadora_cm" id="desc_subentmediadora_cm" size="40" readonly="true"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="comprobarEntidadMed();"	alt="Buscar SubEntidad Mediadora" title="Buscar SubEntidad Mediadora" />
										<label class="campoObligatorio" id="campoObligatorio_subentmediadora" title="Campo obligatorio"> *</label>
									
									</td>
									<td class="literal">Delegaci&oacute;n</td>
									<td>
										<input size="5" maxlength="4" class="dato" id="delegacion_cm" name="delegacion_cm" tabindex="2"/>
										<label class="campoObligatorio" id="campoObligatorio_delegacion" title="Campo obligatorio"> *</label>
									</td>
									<td class="literal" align="center">Carga PAC</td>
									<td class="literal">
										<select class="dato" id="cargaPac_cm" name="cargaPac_cm" tabindex="7" style="width: 70px">
											<option value="">Todos</option>
											<option value="0">No</option>
											<option value="1">S&iacute;</option>
										</select>
									</td>
								</tr>
								<tr>
									<table align="right" style="width: 101%">
										<tr>
											<td class="literal" style="width: 12%">Financiar</td>
											<td>
												<select class="dato" id="financiar_cm" name="financiar_cm" tabindex="7" style="width: 63.5">
													<option value="">Todos</option>
													<option value="0">No</option>
													<option value="1">S&iacute;</option>
												</select>
											</td>
											<td class="literal">Importe m&iacute;nimo</td>
											<td>
												<input id="impMinFinanciacion_cm" name="impMinFinanciacion_cm" size="12" maxlength="12" class="dato" tabindex="1" onchange=""/>
											</td>
											<td class="literal">Importe m&aacute;ximo</td>
											<td class="literal">
												<input id="impMaxFinanciacion_cm" name="impMaxFinanciacion_cm" size="12" maxlength="12" class="dato" tabindex="1" onchange=""/>
											</td>
											<td class="literal">Fecha l&iacute;mite</td>
											<td class="literal">
																	
								                    	 <input type="text" id="fechaLimite_cm" onchange="if (!ComprobarFecha(this, document.main, 'Fecha limiteCM')) this.value='';" name="fechaLimite_cm" size="12" maxlength="11" class="dato" tabindex="25"
								                    	 
														 value="<fmt:formatDate pattern="dd/MM/yyyy" value="${usuarioBean.fechaLimite}" />" />
							                     	<input type="button" id="btn_fechaLimite_cm" name="btn_fechaLimite_cm" class="miniCalendario" style="cursor: pointer;" />
						                	</td>
						               	<tr>
						            </table>
								</tr>
							</table>
							</fieldset>
							</div>
						
						</form:form>	
				</div>
				<div style="margin-top:15px">
						    <a class="bot" href="javascript:limpiarCambioMasivoUsuarios()" title="Limpiar">Limpiar</a>
						    <a class="bot" href="javascript:cerrarCambioMasivoUsuarios()" title="Cancelar">Cancelar</a>
						    <a class="bot" href="javascript:aplicarCambioMasivoUsuarios()" title="Aplicar">Aplicar</a>
				</div>
			</div>
		</div>
		
		<form:form name="frmIncrementarFecha" id="frmIncrementarFecha" action="mtoUsuarios.run" method="post" commandName="usuarioBean">
			<input type="hidden" id="method" name="method" value="doIncrementarFecha"/>								
			<input type="hidden" id="listaIdsMarcados_ifecha" name="listaIdsMarcados_ifecha" value=""/>
		</form:form>
	
	<%@ include file="/jsp/common/lupas/lupaEntidadCM.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaOficinaCM.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEntidadMediadoraCM.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFechaCM.jsp"%>	
	<%@ include file="/jsp/common/lupas/lupaOficina.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaUsuarioNombre.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEntidadMediadora.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFecha.jsp"%>
	
	
	
	
</body>
</html>