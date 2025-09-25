<div id="subveciones-asegurado-popup" class="wrapper_popup" style="width: 50%;left: 30%">
	<div class="header-popup">
		<div class="title_popup">Control de acceso a subvenciones del asegurado</div>
		<a class="close_botton_popup"><span onclick="cerrarInfoSubvs();">x</span></a>
	</div>
	<div class="content_popup" >
		<div id="resultado-subveciones-asegurado">
			
		</div>
		<div style="margin:10px auto" id="div_bot" >
			<a class="bot" href="#" onclick="cerrarInfoSubvs();">Cerrar </a>
		</div>
	</div>
</div>

<script type="text/javascript" src="jsp/moduloPolizas/polizas/importes/controlAccesoSubvsAseg.js"></script>

<%--
Plantilla del html que se genera en el controlador
<div>
	<p>Control de acceso a subvenciones</p>
	<table>
		<thead>
			<tr>
				<th>Organimos</th>
				<th>Info. Adicional</th>
				<th>Fecha efecto</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${organismos}" var=organismo>
				<tr>
					<td>${organimos.descriptivoOrganismo}</td>
					<td>${organimos.infoAdicional}</td>
					<td>${organimos.FechaEfecto}</td>
				</tr>
				<c:if test="${not empty organismo.subvencionArray}">
					<c:set value="${organismo.subvencionArray}" var="subvenciones">
					<tr>
						<table>
							<thead>
								<tr>
									<th>Subvención</th>
									<th>Fecha efecto</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${subvenciones}" var="subvencion">
									<tr>
										<td>${subvencion.descriptivoSubvencion}</td>
										<td>${subvencion.fechaEfecto}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</tr> 
				</c:if>
			</c:forEach>
		</tbody>
	</table>
</div>
<c:if test="${not empty modulacion}">
	<div>
		<p>Modulación de Enesa</p>
		<table>
			<thead>
				<tr>
					<th>Límite de subvención</th>
					<th>% Modulación</th>
					<th>Coeficiente de Modulación</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>${modulacion.limiteSubvencion}</td>
					<td>${modulacion.porcentajeModulacion}</td>
					<td>${modulacion.coeficienteModulacion}</td>
				</tr>
			</tbody>
		</table>
	</div>
</c:if>
<div>
	<p>Saldo de la reducción de ENESA</p>
	<div>
		<span>Aplicado: ${saldoTotal.aplicado}</span>
		<span>Restante: ${saldoTotal.restante}</span>
	</div>
	<c:if test="${not empty saldoAplicadoPolizas}">
		<table>
			<thead>
				<tr>
					<th>Póliza</th>
					<th>Línea</th>
					<th>Importe</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${saldoAplicadoPolizas}" var="saldoAplicadoPoliza">
					<tr>
						<td>${saldoAplicadoPoliza.referencia}</td>
						<td>${saldoAplicadoPoliza.linea}</td>
						<td>${saldoAplicadoPoliza.importe}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
</div>
--%>

