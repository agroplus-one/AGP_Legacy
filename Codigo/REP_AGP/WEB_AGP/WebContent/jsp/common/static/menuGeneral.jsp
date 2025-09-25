 <%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>


<div id="FR_menu">
<table width="144" cellspacing="0" cellpadding="0" border="0">
	<tbody>
		<tr style="height: 1px; margin: 0px; padding: 0px;">
			<td colspan="3" height="1" class="espiga"></td>
		</tr>
		<tr style="height: 21px; padding: 0px; margin: 0px;">
			<td colspan="2" class="espiga"><img height="21" width="144"
				src="jsp/img/espi.gif" alt="" /></td>
			<td width="1" class="borde"></td>
		</tr>
		<tr style="height: 1px; margin: 0px; padding: 0px;">
			<td colspan="3" height="1" class="espiga"></td>
		</tr>
	</tbody>
</table>
<table width="145" style="height: 60px;" cellspacing="0" cellpadding="0"
	border="0">
	<tbody>
		<tr style="height: 60px;" bgcolor="#e5e5e5">
			<td width="19"></td>
			<td width="128"><!--NOMBRE APLICACION-->
			<table style="width: 128px;" cellspacing="0" cellpadding="0"
				border="0">
				<tbody>
					<tr>
						<td class="titapli1">AGROPLUS</td>
					</tr>
					<tr>
						<td class="titapli2">Seguro Creciente</td>
					</tr>
					<tr>
						<td class="titapli3"></td>
					</tr>
				</tbody>
			</table>
			</td>
			<td width="1" bgcolor="#666666"><img src="jsp/img/pix.gif"
				alt="" /></td>
		</tr>
	</tbody>
</table>
<table width="144" cellspacing="0" cellpadding="0" border="0">
	<tbody>
		<tr style="height: 1px">
			<td colspan="3" class="borde"></td>
		</tr>
		<tr>
			<td valign="top" colspan="2" style="height: 100%; background-image: url('jsp/img/fondo_blank.jpg')" 	class="norepetir"><!-- MENU LATERAL -->
			<div id="masterdiv">
				<table class="raya" cellpadding="0" cellspacing="0" border="0">
					<tr>
						<td valign="top">
						<sec:authorize ifAnyGranted="ROLE_0">
							<div class="opc">
								<table cellpadding="0" cellspacing="0" border="0">
									<tr>
										<td><img src="jsp/img/cuad_menu1.gif"></td>
										<td class="titopc"><a href="menu.html?OP=taller" id="a_menu_moduloTaller">M&oacute;dulo de taller</a></td>
									</tr>
								</table>
							</div>
						</sec:authorize>
						<div class="opc" onclick="SwitchMenu('sub2')">
							<table cellpadding="0" cellspacing="0" border="0">
								<tr>
									<td><img src="jsp/img/cuad_menu2.gif"></td>
									<td class="titopc">Administraci&oacute;n</td>
								</tr>
							</table>
						</div>
						<span class="submenu" id="sub2"> 
						<sec:authorize ifAnyGranted="ROLE_0,ROLE_1,ROLE_5">
							<sec:authorize ifAnyGranted="INTERNO">
								<a class="subm" href="tomador.html" id="a_menu_tomador">Tomadores</a>
								<br>
							</sec:authorize>
							<sec:authorize ifAnyGranted="EXTERNO">
								<sec:authorize ifAnyGranted="ROLE_1">
									<a class="subm" href="colectivo.html" id="a_menu_colectivo">Colectivos</a><br>
								</sec:authorize>
							</sec:authorize>
							<sec:authorize ifAnyGranted="INTERNO">
								<a class="subm" href="colectivo.html" id="a_menu_colectivo">Colectivos</a><br>
							</sec:authorize>
						</sec:authorize> 
						
							<a class="subm" href="asegurado.html" id="a_menu_asegurado">Asegurados</a><br>
							
							<sec:authorize ifAnyGranted="ROLE_PAC"> 
									<a class="subm" href="cargaPAC.html" id="a_menuTaller_cargaPAC">Carga de la PAC</a><br>
							</sec:authorize>
							
							<sec:authorize ifAnyGranted="ROLE_PAC"> 
									<a class="subm" href="cargaCSV.html" id="a_menuTaller_cargaCSV">Carga CSV</a><br>
							</sec:authorize>
						
						</span>
	
						<div class="opc" onclick="SwitchMenu('sub3')">
							<table cellpadding="0" cellspacing="0" border="0">
								<tr>
									<td><img src="jsp/img/cuad_menu3.gif"></td>
									<td class="titopc">P&oacute;lizas</td>
								</tr>
							</table>
						</div>
						<span class="submenu" id="sub3">
							 
							<a class="subm" href="cargaColectivo.html" id="a_menu_cargaColectivo">Carga	colectivo</a>
							<br>
							
							<c:if test="${not empty sessionScope.usuario.colectivo.tomador.id.codentidad }">
								<a class="subm" href="cargaAsegurado.html" id="a_menu_cargaAsegurado">Carga asegurado</a>
								<br>
							</c:if> 
							<c:if test="${not empty sessionScope.usuario.asegurado and not empty sessionScope.usuario.colectivo}">
								<a class="subm" href="cargaClase.run" id="a_menu_cargaClase">Carga clase</a>  
								<br>
							</c:if> 
							<c:if test="${not empty sessionScope.usuario.asegurado and not empty sessionScope.usuario.colectivo and not empty sessionScope.usuario.clase}">
								<a class="subm" href="seleccionPoliza.html" id="a_menu_seleccionPoliza">Selecci&oacute;n de p&oacute;liza</a>
								<br>
							</c:if> 
						</span>
	
						<div class="opc" onclick="SwitchMenu('sub4')">
							<table cellpadding="0" cellspacing="0" border="0">
								<tr>
									<td><img src="jsp/img/cuad_menu4.gif"></td>
									<td class="titopc">Utilidades</td>
								</tr>
							</table>
						</div>
						<span class="submenu" id="sub4"> 
							<a class="subm" href="utilidadesPoliza.html" id="a_menu_utilidadesPoliza">P&oacute;lizas</a>
							<br>
							<a class="subm" href="utilidadesSiniestros.run" id="a_menu_utilidadesSiniestros">Consulta Siniestros</a>  
							<br>
							<a class="subm" href="utilidadesReduccionCapital.run" id="a_menu_utilidadesReduccionCapital">Consulta R.C</a>  
							<br>
							<a class="subm" href="anexoModificacionUtilidades.run" id="a_menu_anexoModificacionUtilidades">Consulta A. Modif.</a>  
							<br>
							<a class="subm" href="utilidadesIncidencias.run" id="a_menu_utilidadesIncidencias">Incidencias</a>
							<br>
							<sec:authorize ifAnyGranted="ROLE_0"> 
								<c:if test="${not empty sessionScope.usuario and (sessionScope.usuario.codusuario == 'U020875' or sessionScope.usuario.codusuario == 'U990440' or sessionScope.usuario.codusuario == 'U028982')}">
									<a class="subm" href="utilidadesXML.run" id="a_menu_utilidadesXML">XMLs</a>
									<br>
								</c:if>
							</sec:authorize>
							<!-- P73326 -->
							<sec:authorize ifAnyGranted="ROLE_0,ROLE_1"> 
								<a class="subm" href="#" id="a_menu_portalMedAgroseguro" onclick="irPortalMediador();return false;">Portal Med. Agroseguro</a>
								<br>
							</sec:authorize>
							<sec:authorize ifAnyGranted="ROLE_5"> 
								<sec:authorize ifAnyGranted="INTERNO">
									<a class="subm" href="#" id="a_menu_portalMedAgroseguro" onclick="irPortalMediador();return false;">Portal Med. Agroseguro</a>
									<br>
								</sec:authorize>
							</sec:authorize>
						</span>
						
						<!-- Apartado de COMISIONES -->
						<sec:authorize ifAnyGranted="ROLE_0,ROLE_1,ROLE_5">
							<div class="opc" onclick="SwitchMenu('sub5')">
										<table cellpadding="0" cellspacing="0" border="0">
											<tr>
												<td><img src="jsp/img/cuad_menu5.gif"></td>
												<td class="titopc">Comisiones</td>
											</tr>
										</table>
							</div>
							<span class="submenu" id="sub5" style="padding-left: 21px;">
							<sec:authorize ifAnyGranted="INTERNO">
								<sec:authorize ifAnyGranted="ROLE_0,ROLE_1,ROLE_5">								
									<a class="subm" href="#" onclick="SwitchSubMenu('sub6', 'sub5')">Mantenimiento</a><br>							
									<span class="submenu" id="sub6" style="padding-bottom: 0px; padding-left: 19px;">
										<sec:authorize ifAnyGranted="ROLE_0"> 
											<a class="subm"	href="gge.html" id="a_menu_mantenimientoGEE">GGE</a><br>
											<a class="subm" href="comisionesCultivos.html?method=doConsultaParam"	id="a_menu_mantenimientoParametrosGenerales">Param. Generales</a><br>
											<a class="subm" href="comisionesCultivos.html"	id="a_menu_mantenimientoComisionesCultivo">Comisi&oacute;n E-S Med</a><br>
											<a class="subm" href="reglamento.html"	id="a_menu_mantenimientoReglamentos">Reglamentos</a><br>
										</sec:authorize> 
										
										<a class="subm" href="descuentos.html"	id="a_menu_mantenimientoDescuentos">Descuentos</a><br>
										<a class="subm" href="retenciones.html"	id="a_menu_mantenimientoRetenciones">Retenciones</a><br>
										
									</span>
								</sec:authorize>
								<sec:authorize ifAnyGranted="ROLE_0"> 
									<a class="subm" href="#" onclick="SwitchSubMenu('sub7', 'sub5')">Importaciones</a><br>								
									<span class="submenu" id="sub7" style="padding-bottom: 0px; padding-left: 19px;">
									    <a class="subm" href="importacionComisiones.html?tipo=G&limpiar=limpiar" id="a_menu_mantenimientoImportacionRecibos">Rec. Emitidos</a><br>
										<a class="subm" href="importacionComisiones.html?tipo=C&limpiar=limpiar" id="a_menu_mantenimientoImportacionComisiones">Comisiones</a><br>
										<a class="subm" href="importacionComisionesUnificadas.html?tipo=C&limpiar=limpiar&origenLlamada=menuGeneral" id="a_menu_mantenimientoImportacionComisionesUnificadas">Comisiones 2015+</a><br>										                     
										<a class="subm" href="importacionComisiones.html?tipo=R&limpiar=limpiar" id="a_menu_mantenimientoImportacionReglamento">Reglamento</a><br>										
										<a class="subm" href="importacionComisiones.html?tipo=I&limpiar=limpiar" id="a_menu_mantenimientoImportacionImpagados">Impagados</a><br>
										<a class="subm" href="importacionComisionesUnificadas.html?tipo=I&limpiar=limpiar&origenLlamada=menuGeneral" id="a_menu_mantenimientoImportacionImpagadosUnificadas">Impagados 2015+</a><br>
										<a class="subm" href="importacionComisionesUnificadas.html?tipo=D&limpiar=limpiar&origenLlamada=menuGeneral" id="a_menu_mantenimientoImportacionDeudaAplazada">Deuda Aplazada</a><br>
										<a class="subm" href="cierre.html" id="a_menu_mantenimientoCierreComisiones">Cierre</a><br>
									</span>
								</sec:authorize>
							</sec:authorize>


							<!-- Nuevo: todas las consultas se agrupan -->
							<a class="subm" href="#" onclick="SwitchSubMenu('sub9', 'sub5')">Consultas</a><br>								
							<span class="submenu" id="sub9" style="padding-bottom: 0px; padding-left: 19px;">
							    <a class="subm" href="informesRecibos.run" id="a_menu_InformesRecibos">Recibos</a><br>
							    <a class="subm" href="informesRecibos2015.run" id="a_menu_InformesRecibos2015">Recibos 2015+</a><br>
								<a class="subm" href="informesComisiones.run" id="a_menu_InformesComisiones">Comisiones</a><br>
							    <a class="subm" href="informesComisiones2015.run" id="a_menu_InformesComisiones2015">Comisiones 2015+</a><br>
								<a class="subm" href="informesImpagados.run" id="a_menu_InformesImpagados">Impagados</a><br>	
								<a class="subm" href="informesImpagadosUnificado.run" id="a_menu_InformesImpagados2015">Impagados 2015+</a><br>
								<a class="subm" href="informesDeudaAplazada.run" id="a_menu_InformesDeudaAplazada">Deuda aplazada</a><br>

							</span>

							</span>
						</sec:authorize>
						
						<!-- Apartado de SOBREPRECIO -->
						<sec:authorize ifAnyGranted="INTERNO">
							<sec:authorize ifAnyGranted="ROLE_0,ROLE_1,ROLE_2,ROLE_3,ROLE_4,ROLE_5">
								  <div class="opc" onclick="SwitchMenu('sub8')">
											<table cellpadding="0" cellspacing="0" border="0">
												<tr>
													<td><img src="jsp/img/cuad_menu1.gif"></td>
													<td class="titopc">Sobreprecio</td>
												</tr>
											</table>
									</div>
								<span class="submenu" id="sub8" style="padding-left: 21px;">
									<sec:authorize ifAnyGranted="ROLE_0"> 
										<a class="subm" href="#" onclick="SwitchSubMenu('sub10','sub8')">Mantenimiento</a><br>
										<span class="submenu" id="sub10" style="padding-bottom: 0px; padding-left: 19px;">
											<a class="subm"	href="tasasSbp.run" id="a_menu_mantenimiento_sbp_tasas">Tasas</a><br>
											<a class="subm"	href="sobreprecioSbp.run" id="a_menu_mantenimiento_sbp_sobreprecio">Sobreprecio</a><br>
											<a class="subm"	href="primaMinimaSbp.run" id="a_menu_mantenimiento_sbp_prima_minima">Prima M&iacute;nima</a><br>
											<a class="subm"	href="periodoContSbp.run" id="a_menu_mantenimiento_sbp_periodo_cont">Periodos Contrataci&oacute;n</a><br>
											<a class="subm"	href="mtoImpuestoSbp.run" id="a_menu_mantenimiento_sbp_impuestos">Impuestos</a><br>
										</span>
									</sec:authorize>
									<a class="subm" href="consultaPolSbp.run" id="a_menu_Confeccion_Poliza_Sopreprecio" >Confecci&oacute;n de p&oacute;l. Sobreprecio</a><br>
									 
									 
									<a class="subm" href="consultaPolizaSbp.run" id="a_menu_Consulta_Sopreprecio" >Listado de p&oacute;l. Sobreprecio</a><br>							
								</span>
							</sec:authorize>
						</sec:authorize>
						
						<!--  Pet. 63473 ** MODIF TAM (25.11.2021) ** Inicio  -->
						<!--  Apartado de SOBREPRECIO  - Permitimos acceder a Sobprecio a los Perfiles 1 y 3 Externos --> 
						<sec:authorize ifAnyGranted="EXTERNO">
							<sec:authorize ifAnyGranted="ROLE_1,ROLE_3">
								  <div class="opc" onclick="SwitchMenu('sub8')">
											<table cellpadding="0" cellspacing="0" border="0">
												<tr>
													<td><img src="jsp/img/cuad_menu1.gif"></td>
													<td class="titopc">Sobreprecio</td>
												</tr>
											</table>
									</div>
								<span class="submenu" id="sub8" style="padding-left: 21px;">
									<a class="subm" href="consultaPolSbp.run" id="a_menu_Confeccion_Poliza_Sopreprecio" >Confecci&oacute;n de p&oacute;l. Sobreprecio</a><br>
									<a class="subm" href="consultaPolizaSbp.run" id="a_menu_Consulta_Sopreprecio" >Listado de p&oacute;l. Sobreprecio</a><br>							
								</span>
							</sec:authorize>
						</sec:authorize>
						<!--  Pet. 63473 ** MODIF TAM (25.11.2021) ** Inicio  -->
						
						
					<!-- INFORMES -->
					<sec:authorize ifAnyGranted="ROLE_0">
						<div class="opc" onclick="SwitchMenu('sub11')">
								<table cellpadding="0" cellspacing="0" border="0">
									<tr>
										<td><img src="jsp/img/cuad_menu2.gif"></td>
										<td class="titopc">Informes</td>
									</tr>
								</table>
						</div>
					</sec:authorize>
					
					<sec:authorize ifAnyGranted="ROLE_1,ROLE_2,ROLE_3,ROLE_4,ROLE_5">
						<c:catch var ="catchException">	
							<c:if test="${sessionScope.usuario.permisosInformes.accesoGenerador || sessionScope.usuario.permisosInformes.accesoDisenador}">
							    <div class="opc" onclick="SwitchMenu('sub11')">
										<table cellpadding="0" cellspacing="0" border="0">
											<tr>
												<td><img src="jsp/img/cuad_menu5.gif"></td>
												<td class="titopc">Informes</td>
											</tr>
										</table>
								</div>
							</c:if>
						</c:catch>
					</sec:authorize>
					
					<span class="submenu" id="sub11" style="padding-left: 21px;">
							<sec:authorize ifAnyGranted="INTERNO">
							<c:catch var ="catchException">							
								<sec:authorize ifAnyGranted="ROLE_0">
									<a class="subm" href="mtoEntidadesAccesoRestringido.run" id="a_menu_mantenimiento_ent_acceso_restringido">Ent. Acceso Restring</a>
									<a class="subm" href="#" onclick="SwitchSubMenu('sub12','sub11')">Mantenimiento</a><br>
									<span class="submenu" id="sub12" style="padding-bottom: 0px; padding-left: 19px;">
											<a class="subm"	href="mtoCamposPermitidos.run" id="a_menu_mantenimiento_campos_permitidos">Campos perm.</a><br>
											<a class="subm"	href="mtoCamposCalculados.run" id="a_menu_mantenimiento_campos_calculados">Campos calc.</a><br>
											<a class="subm"	href="mtoOperadoresCampos.run" id="a_menu_mantenimiento_operadores">Operadores</a><br>
										<a class="subm"	href="mtoInformes.run" id="a_menu_mantenimiento_informes">Informes</a><br>
									</span>
								</sec:authorize>
								
								<sec:authorize ifAnyGranted="ROLE_1,ROLE_5">
									<c:if test="${sessionScope.usuario.permisosInformes.accesoDisenador}">
										<a class="subm" href="#" onclick="SwitchSubMenu('sub12','sub11')">Mantenimiento</a><br>
										<span class="submenu" id="sub12" style="padding-bottom: 0px; padding-left: 19px;">
											<a class="subm"	href="mtoInformes.run" id="a_menu_mantenimiento_informes">Informes</a><br>
										</span>
									</c:if>
								</sec:authorize>
							</c:catch>
							</sec:authorize>
						<sec:authorize ifAnyGranted="ROLE_0">
							<a class="subm" href="generacionInforme.run" id="a_menu_generacion_informes" >Generaci&oacute;n</a><br>
						</sec:authorize>
						
						<sec:authorize ifAnyGranted="ROLE_1,ROLE_2,ROLE_3,ROLE_4,ROLE_5">	
							<c:catch var ="catchException">												 
								<c:if test="${sessionScope.usuario.permisosInformes.accesoGenerador}">
									<a class="subm" href="generacionInforme.run" id="a_menu_generacion_informes" >Generaci&oacute;n</a><br>
								</c:if>
							</c:catch>
						</sec:authorize>	
									
					</span>
					
						<sec:authorize ifAnyGranted="ROLE_0,ROLE_1,ROLE_5">
						<div class="opc" onclick="SwitchMenu('sub13')">
								<table cellpadding="0" cellspacing="0" border="0">
									<tr>
										<td><img src="jsp/img/cuad_menu3.gif"></td>
										<td class="titopc">Utilidades Ganado</td>
									</tr>
								</table>
						</div>
							<span class="submenu" id="sub13"> 
								<a class="subm" href="polizasRenovables.run" id="a_menu_polizas_renovables">Renovables</a>
								<br>
								
							</span>
						</sec:authorize>
						<sec:authorize ifAnyGranted="ROLE_0,INTERNO">
							<div class="opc" onclick="SwitchMenu('sub14')">
								<table cellpadding="0" cellspacing="0" border="0">
									<tr>
										<td><img src="jsp/img/cuad_menu4.gif"></td>
										<td class="titopc">RC Ganado</td>
									</tr>
								</table>
							</div>
							<span class="submenu" id="sub14"> 
								<sec:authorize ifAnyGranted="ROLE_0">
									<a class="subm" href="#" onclick="SwitchSubMenu('sub15','sub14')" id="a_menu_mant_rc">Mantenimiento</a>
									<span class="submenu" id="sub15" style="padding-bottom: 0px; padding-left: 19px;">
										<a class="subm"	href="lineasRC.run?origenLlamada=menuGeneral" id="a_menu_mant_rc_lineas">L&iacute;neas</a><br>
										<a class="subm"	href="datosRC.run?origenLlamada=menuGeneral" id="a_menu_mant_rc_ganado">RC Ganado</a><br>
										<a class="subm"	href="impuestosRC.run?origenLlamada=menuGeneral" id="a_menu_mant_rc_ganado">Impuestos</a><br>
									</span>
								</sec:authorize>
								<sec:authorize ifAnyGranted="INTERNO">
									<a class="subm" href="listadoRCGanado.run?origenLlamada=menuGeneral" id="a_menu_list_rc_ganado">Listado RC Ganado</a>
								</sec:authorize>
							</span>
						</sec:authorize>
						<sec:authorize ifAnyGranted="ROLE_0,ROLE_1,ROLE_2,ROLE_3,ROLE_4,ROLE_5">
							<div class="opc">
								<table cellpadding="0" cellspacing="0" border="0">
									<tr>
										<td><img src="jsp/img/cuad_menu5.gif"></td>
										<td class="titopc"><a href="documentacionAgroseguro.run?origenLlamada=menuGeneral" id="a_menu_docAgroseguro">Doc. Agroseguro</a></td>
									</tr>
								</table>
							</div>
						</sec:authorize>
						</td>	
					</tr>
				</table>
			</div>
			</td>
			<td class="borde" width="1"><img src="jsp/img/pix.gif"></td>
		</tr>
	</tbody>
</table>
</div>