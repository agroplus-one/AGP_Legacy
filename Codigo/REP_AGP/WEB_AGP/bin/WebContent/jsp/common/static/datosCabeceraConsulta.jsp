<script> 
	function switchCabecera(mostrar) { 
		if (mostrar) { 
			$('#btnOcultarCabecera').show();
			$('#btnMostrarCabecera').hide();
			$('#divDatosCabecera').show();
		} 
		else { 
			$('#btnOcultarCabecera').hide();
			$('#btnMostrarCabecera').show();
			$('#divDatosCabecera').hide();
		} 
	} 
</script>

<div id="conten" style="width: 85%">	
	<c:choose>
		<c:when test="${operacion == 'altaParcela' || 
	                    operacion == 'guardarParcela' || 
	               		operacion == 'modificarParcela' || 
		                operacion == 'replicarParcela' || 
		                operacion == 'altaEstructuraParcela' || 
	    	            operacion == 'modificarInstalacionParcela' ||
	        	        operacion == 'modiicarEstructuraParcela' ||
	            	    operacion == 'visualizarParcela'}">
			<c:set var="mostrarPlegar">true</c:set>
		</c:when>
		<c:otherwise>
			<c:set var="mostrarPlegar">false</c:set>
		</c:otherwise>
	</c:choose>
	<div>
		<!-- Botonera de debajo de la cabecera -->
		<div id="FR_bot">
			<table width="100%" height="20" cellspacing="0" cellpadding="0" border="0">
				<tbody>
					<tr height="1">
						<td bgcolor="#666666" height="1">
							
						</td>
					</tr>
					<tr bgcolor="#e5e5e5">
						<td align="right">
							<table border="0">
								<tbody>
									<tr>
										<td width="40">
										</td>
										<td>
											<a id="botonVolver"  href="menu.html?OP=logout">
												 <img width="16" name="salir" alt="Desconectar" src="jsp/bot/cerrar_a.gif"/>
											</a>
										</td>									
										<td>
											<a href="">
												 <img width="16" name="ayuda" alt="Ayuda" src="jsp/bot/ayuda_a.gif" />
											</a>
										</td>
										<c:if test="${mostrarPlegar eq true}">
											<td id="trOcultarCabecera">
												<img id="btnOcultarCabecera" width="16" style="cursor: pointer;" onclick="javascript:switchCabecera(false);" alt="Ocultar cabecera" src="jsp/bot/plegar_a.gif" />
												<img id="btnMostrarCabecera" width="16" style="cursor: pointer;" onclick="javascript:switchCabecera(true);" alt="Mostrar cabecera" src="jsp/bot/desplegar_a.gif" style="display:none;" />
											</td>
											<td width="10">
											</td>
										</c:if>
									</tr>
								</tbody>
							</table>
						</td>
					</tr>
					<tr height="1">
						<td bgcolor="#666666" height="1">
							
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<!-- Cabecera de la aplicación: mostramos entidad y usuario -->
		<div id="divDatosCabecera">
			<table style="border:1px solid black" width="100%">
				<tr>
					<td class="literal">Entidad : ${sessionScope.datosCabecera.entidad}</td>
					<td class="literal">Usuario: ${sessionScope.datosCabecera.usuario}</td>
					
					
					<c:if test="${not empty sessionScope.medida }">		
						<c:set var="esGanado" value="${sessionScope.medida.esGanado}" />
						<c:choose>
							<c:when test="${esGanado eq '0' }">
								<td class="literal">
									<c:set var="tipoMedida" value="${sessionScope.medida.tipomedidaclub }" />
									<c:set var="porcentage" value="${sessionScope.medida.pctbonifrecargo }" />
									<c:choose>
										<c:when test="${tipoMedida eq '1' }">
											<c:out value="Bon: +${porcentage }%  Bonus" />
										</c:when>
										<c:when test="${tipoMedida eq '2' }">
											<c:out value="Rec: -${porcentage }%  General" />
										</c:when>
										<c:when test="${tipoMedida eq '0' }">
											<c:out value="Bon/Rec:  ${porcentage }%  General" />
										</c:when>
										<c:otherwise>
									        <c:out value="Bon/Rec:   0%" />
									    </c:otherwise>
									</c:choose>
								</td>
							</c:when>
						</c:choose>
						<c:choose>			
							<c:when test="${esGanado eq '1' }">
								<td class="literal">
									<c:set var="tipoMedida" value="${sessionScope.medida.tipomedidaclub }" />
									<c:set var="porcentage" value="${sessionScope.medida.pctbonifrecargo }" />
									<c:choose>
										<c:when test="${tipoMedida eq '1' }">
											<c:out value="Bon:  +${porcentage }%" />
										</c:when>
										<c:when test="${tipoMedida eq '2' }">
											<c:out value="Rec: -${porcentage }%" />
										</c:when>
										<c:when test="${tipoMedida eq '0' }">
											<c:out value="Bon/Rec:  ${porcentage }%" />
										</c:when>
										<c:when test="${tipoMedida eq '9' }">
											<c:set var="pctMin" value="${sessionScope.medida.pctMin }" />
											<c:set var="pctMax" value="${sessionScope.medida.pctMax }" />
											<c:out value="Bon/Rec:  ${pctMin }%/${pctMax }%" />
										</c:when>
										<c:otherwise>
									        <c:out value="Bon/Rec:   0%" />
									    </c:otherwise>
									</c:choose>
								</td>
							</c:when>
						</c:choose>
					</c:if>
				</tr>
				<c:if test="${not empty sessionScope.datosCabecera.asegurado}">
					<tr>
						<td class="literal" >Asegurado:
							${sessionScope.datosCabecera.asegurado}
						</td>
						<td class="literal">CIF/NIF: ${sessionScope.datosCabecera.nifCif }</td>
						
						<c:if test="${not empty sessionScope.datosCabecera.clase}">
							<td class="literal">
								<SPAN title="${sessionScope.datosCabecera.clase }">CLASE: ${sessionScope.datosCabecera.clase }
								</SPAN>
							</td>
						</c:if>
											
					</tr>
				</c:if>
				<c:if test="${not empty sessionScope.datosCabecera.colectivo }">
					<tr>
						<td class="literal">Colectivo: ${sessionScope.datosCabecera.colectivo}</td>
						<td class="literal">Plan / Línea: ${sessionScope.datosCabecera.planLinea}</td>
						<!--  		
						<c:if test="${editar}">
							<td class="literal">Póliza: ${ referencia }</td>
						</c:if>
						-->
						<c:if test="${not empty intervaloCoefReduccionRdto && not empty sessionScope.datosCabecera.asegurado}">
							<td class="literal">Intervalo Coef. Rdto: ${intervaloCoefReduccionRdto}</td>
						</c:if>
						
					</tr>
				</c:if>
				
			</table>
			<br/>
		</div>
	</div>
	
	<!-- Cuerpo de la ventana -->
	<div id="FR_apli">
		