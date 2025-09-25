<c:choose>

<c:when test="${operacion == 'altaParcela' || 
                operacion == 'guardarParcela' || 
                operacion == 'modificarParcela' || 
                operacion == 'replicarParcela' || 
                operacion == 'altaEstructuraParcela' || 
                operacion == 'modificarInstalacionParcela' ||
                operacion == 'modiicarEstructuraParcela' ||
                operacion == 'visualizarParcela' ||
                tallerPantalla}">	



<div id="conten" style="width: 99%;left:0">	
</c:when>	
<c:otherwise>
<div id="conten" style="width: 85%;">
</c:otherwise>
</c:choose>
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
										<a id="botonVolver"  href="menu.html?OP=ppal">
											 <img width="16" name="salir" alt="Volver" src="jsp/bot/cerrar_a.gif"/>
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
	<!-- Cabecera de las pantallas -->
		<table style="border:1px solid black" width="100%">
			<tr>
				<td class="literal">Entidad: ${sessionScope.usuario.oficina.entidad.codentidad } ${sessionScope.usuario.oficina.entidad.nomentidad }</td>
				<td class="literal">E-S Med: ${sessionScope.usuario.subentidadMediadora.id.codentidad} - ${sessionScope.usuario.subentidadMediadora.id.codsubentidad}</td>
				<td class="literal">Usuario: ${sessionScope.usuario.nombreusu }</td>
			</tr>			
		</table>
		<br/>
	<!-- Cuerpo de la ventana -->
	<div id="FR_apli">
		