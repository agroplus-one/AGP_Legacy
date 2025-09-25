<div id="conten" style="width: 85%">
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
									<td width="10">
									</td>
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
	<table style="border:1px solid black" width="100%">
		<tr>
			<td class="literal">Entidad: ${sessionScope.usuario.oficina.entidad.codentidad } ${sessionScope.usuario.oficina.entidad.nomentidad }</td>
			<td class="literal">E-S Med: ${sessionScope.usuario.subentidadMediadora.id.codentidad} - ${sessionScope.usuario.subentidadMediadora.id.codsubentidad}</td>
			<td class="literal">Usuario: ${sessionScope.usuario.nombreusu }</td>
			<c:if test="${not empty sessionScope.medida }">		
					<c:set var="esGanado" value="${sessionScope.medida.esGanado}" />
					<c:choose>
						<c:when test="${esGanado eq '0' }">
							<td class="literal">
								<c:set var="tipoMedida" value="${sessionScope.medida.tipomedidaclub }" />
								<c:set var="porcentage" value="${sessionScope.medida.pctbonifrecargo }" />
								<c:choose>
									<c:when test="${tipoMedida eq '1' }">
										<c:out value="Bon:  ${porcentage }%  Bonus" />
									</c:when>
									<c:when test="${tipoMedida eq '2' }">
										<c:out value="Rec:  ${porcentage }%  General" />
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
										<c:out value="Bon: +${porcentage }%" />
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
		<c:if test="${not empty sessionScope.usuario.asegurado }">
			<tr>
				<td class="literal" colspan="2">Asegurado:
					<c:choose>
						<c:when test="${sessionScope.usuario.asegurado.tipoidentificacion == 'NIF' }">
							 ${sessionScope.usuario.asegurado.nombre } ${sessionScope.usuario.asegurado.apellido1 } ${sessionScope.usuario.asegurado.apellido2 }
						</c:when>
						<c:otherwise>
							 ${sessionScope.usuario.asegurado.razonsocial }
						</c:otherwise>
					</c:choose>
					
				</td>
				<td class="literal">CIF/NIF: ${sessionScope.usuario.asegurado.nifcif }</td>
			</tr>
		</c:if>
		<c:if test="${not empty sessionScope.usuario.colectivo }">
			<tr>
				<td class="literal" colspan="2">Colectivo: ${sessionScope.usuario.colectivo.nomcolectivo }</td>
				<td class="literal">Plan / Línea: ${sessionScope.usuario.colectivo.linea.codplan } /
						${sessionScope.usuario.colectivo.linea.codlinea}</td>				
			</tr>
		</c:if>
	</table>
	<br/>
	<!-- Cuerpo de la ventana -->
	<div id="FR_apli">