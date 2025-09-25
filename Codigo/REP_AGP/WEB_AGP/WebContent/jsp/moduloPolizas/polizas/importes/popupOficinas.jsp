
<html>
	<head>
	
	<c:set var="permisoEscritura" value="${((perfil == 0 || perfil == 1 || perfil == 2 || perfil == 3 || perfil == 5) && externo == 0)}"/>
	
		<form id="frmOficinas" name="frmOficinas" action="consultaDetallePoliza.html" method="post">
			<input type="hidden" name="method" id="method" value=""/>
			<input type="hidden" name="idpoliza" id="idpoliza" value="${idpoliza}"/>
			<input type="hidden" name="entidad" id="entidad" value="${entidad}"/>
			
			<!-- Mantener el value de id="oficina" siempre vacio (para no filtrar por ella al usar la lupa) -->
			<input type="hidden" name="oficina" id="oficina" value="" onchange="javascript:cambioValorOficina();"/>
			<input type="hidden" name="nomboficina" id="nomboficina" value="${nomboficina}"/>
			
			<!-- Almacenamos el valor de la oficina y su nombre que estan en la base de datos, por si necesitamos recuperarlos 
			(si cambian el valor de la oficina y cierran la ventana) -->
			<input type="hidden" name="oficinaOriginal" id="oficinaOriginal" value="${oficinaActual}"/>
			<input type="hidden" name="nombreOficinaOriginal" id="nombreOficinaOriginal" value="${nomboficina}"/>
			
			<input type="hidden" name="grupoEntidades" id="grupoEntidades" value="">
			<input type="hidden" name="grupoOficinas" id="grupoOficinas" value="">
			<input type="hidden" name="perfil" id="perfil" value="${perfil}">
			<input type="hidden" name="perm" id="perm" value="${permisoEscritura}">
			<input type="hidden" name="modoLectura" id="modoLectura" value="${modoLectura}">
			<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${vieneDeUtilidades}">
			<input type="hidden" name="validComps" id="validComps" value="${validComps}">
			
			<input type="hidden" name="idpolizacpl" id="idpolizacpl" value="${idpolizaCpl}"/>
			
			<div id="panelOficinas" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em;
					width: 20%; display: none; top: 270px; left: 33%; 
                    position: absolute; z-index: 1003;border:1px solid #A4A4A4;background-color:#F2F2F2">
			
				<!--  header popup -->
				<div id="header-popup" class="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; 
						font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px" >
					<div style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Oficina Propietaria</div>
					
					<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; 
						top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
						<span onclick="cerrarPopUpOficinas()">x</span>
					</a>
				</div>
			
				<!--  body popup -->
				<div class="panelInformacion_content">
					<div id="panelInformacion" class="panelInformacion">
					<div id="panelAlertasValidacion_ofi" name="panelAlertasValidacion_ofi" class="errorForm_fp" align="center"></div>
						<table style="width:90%">
							<td class="literal">Oficina
								<c:if test="${permisoEscritura eq true}">
									<input type="text" size="5" maxlength="4" cssClass="dato" id="oficinaActual" name="oficinaActual" value="${oficinaActual}" tabindex="2" onchange="javascript:lupas.limpiarCampos('desc_oficina');"/>
									<input cssClass="dato"	id="desc_oficina" size="20" readonly="true" value="${nomboficina}"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:obtenerDatosOficina();" alt="Buscar Oficina" title="Buscar Oficina" />
								</c:if>
								<c:if test="${permisoEscritura eq false}">
									<input size="5" maxlength="4" cssClass="dato" readonly="true" id="oficinaActual" name="oficinaActual" value="${oficinaActual}" tabindex="2"/>
									<input cssClass="dato"	id="desc_oficina" size="20" readonly="true" value="${nomboficina}"/>
								</c:if>	
							</td>
						</table>
					</div>
				</div>
			    <!-- Botones popup --> 
			    <c:if test="${permisoEscritura eq false}">
				    <div style="margin-top:15px" align="center" class="cerrarPopUpX" id="cerrarPopUp">
				        <a class="bot" href="javascript:cerrarPopUpOficinas()" title="Cerrar">Cerrar</a>
					</div>
				</c:if>
				<c:if test="${permisoEscritura eq true}">
				    <div style="margin-top:15px" align="center" class="aplicarPopUp" id="aplicarPopUp">
				        <a class="bot" href="javascript:cerrarPopUpOficinas()" title="Cancelar">Cancelar</a>
						<a class="bot" href="javascript:aplicarOficina('${mensaje}')" title="Aplicar">Aceptar</a>
					</div>
				</c:if>
			</div>
		</form>
	</head>
</html>
