<%@ include file="/jsp/common/static/taglibs.jsp" %>

<table class="float-izquierda">
	<tr>
		<th valign="top">
			<p class="literal">Calcular RC de Ganado:&nbsp;
				<c:if test="${consultaRC}">
					<select id="calcularRC" name="calcularRC" class="dato">
						<c:choose>
							<c:when test="${not empty idPolizaRC}">
								<option selected="selected" disabled="disabled" value="true">S�</option>
							</c:when>
							<c:otherwise>
								<option selected="selected" disabled="disabled" value="false">No</option>																
							</c:otherwise>
						</c:choose>
					</select>
				</c:if>
				<c:if test="${not consultaRC}">
					<select id="calcularRC" name="calcularRC" class="dato">
						<c:choose>
							<c:when test="${not puedeCalcular or not esEspecieUnica or not explDatosRC}">
								<option selected="selected" disabled="disabled" value="false">No</option>
							</c:when>
							<c:when test="${not empty idPolizaRC}">
								<option selected="selected" value="true">S�</option>
								<option value="false">No</option>
							</c:when>
							<c:otherwise>
								<option selected="selected" value=""></option>
								<option value="false">No</option>
								<option value="true">S�</option>
							</c:otherwise>
						</c:choose>
					</select>
				</c:if>
			</p>
		</th>	
	</tr>
</table>

<c:if test="${puedeCalcular}">
	<c:choose>
		<c:when test="${not explDatosRC and not consultaRC}">
			<table class="float-izquierda">
				<tr>
					<th style="font-size: 12px;color: #F63707;" width="35%">Los datos de las explotaciones no permiten el c�lculo. La RC de Ganado deber� contratarse por el canal habitual</th>
				</tr>
			</table>		
		</c:when>
		<c:when test="${not esEspecieUnica and not consultaRC}">
			<table class="float-izquierda">
				<tr>
					<th style="font-size: 12px;color: #F63707;" width="35%">P�liza con m�s de una especie asegurable. La RC de Ganado deber� contratarse por el canal habitual</th>
				</tr>
			</table>			
		</c:when>
		<c:when test="${consultaRC}">
			<table class="float-izquierda">
				<tr>
					<th class="literalbordeCabecera" valign="top" width="7.25%">Especie para RC</th>
					<th class="literalbordeCabecera" valign="top" width="20%">R�gimen para RC</th>
					<th class="literalbordeCabecera" valign="top" width="7.25%">N� Animales</th>
				</tr>
				<tr>
					<input type="hidden" name="idPolizaRC" id="idPolizaRC" value="${idPolizaRC}"/>
					<td class="literalborde" align="center">
						${especieRC.descripcion}
						<input type="hidden" name="codEspecieRC" id="codEspecieRC" value="${especieRC.codespecie}"/>
					</td>
					<td class="literalborde" align="center">
						<c:choose>
							<c:when test="${not empty regimenRCSeleccionado}">
								<select  class="dato" cssStyle="width:220px" id="codRegimenRC" name="codRegimenRC">
									<option value="${regimenRCSeleccionado.codregimen}">${regimenRCSeleccionado.descripcion}</option>
									<c:forEach items="${regimenesRC}" var="regimen">
										<option value="${regimen.codregimen}">${regimen.descripcion}</option>
									</c:forEach>
								</select>
							</c:when>
							<c:when test="${consultaRC}">
								<select  class="dato" cssStyle="width:220px" id="codRegimenRC" name="codRegimenRC" >
									<option selected="selected" value="">${regimenRCSeleccionado.descripcion}</option>
								</select>
							</c:when>
							<c:otherwise>				
								<select  class="dato" cssStyle="width:220px" id="codRegimenRC" name="codRegimenRC">
									<option value=""></option>
									<c:forEach items="${regimenesRC}" var="regimen">
										<option value="${regimen.codregimen}">${regimen.descripcion}</option>
									</c:forEach>
								</select>
							</c:otherwise>
						</c:choose>
					</td>
					<td class="literalborde" align="center">
						${numAnimales}
						<input type="hidden" name="numAnimalesRC" id="numAnimalesRC" value ="${numAnimales}"/>
					</td>
				</tr>
				<tr>
					<td colspan="3" class="literal">
						(1) Estabulaci�n Permanente: Los animales est�n permanentemente encerrados dentro de un establo y sus instalaciones anexas de recreo o alimentaci�n.
						<br/>
						(2) Semiestabulaci�n Regular: Para su alimentaci�n, los animales acceden a los pastos de la propia explotaci�n y el resto del d�a permanecen encerrados dentro de un establo y sus instalaciones anexas.
						<br/>
						(3) Semiestabulaci�n Estacional y/o Extensivo: Para su alimentaci�n, los animales permanecen en pastos naturales, ya sea un periodo del a�o o durante todo el periodo de explotaci�n.
						<br/>
						(4) Excluidas Reses Bravas y Caballos de Monta o Paseo												
					</td>
				</tr>				
			</table>	
		</c:when>
		<c:otherwise>
			<table id="tablaContratarRC" class="ganado-rc-oculto">
				<tr>
					<th class="literalbordeCabecera" valign="top" width="7.25%">Especie para RC</th>
					<th class="literalbordeCabecera" valign="top" width="20%">R�gimen para RC</th>
					<th class="literalbordeCabecera" valign="top" width="7.25%">N� Animales</th>
				</tr>
				<tr>
					<input type="hidden" name="idPolizaRC" id="idPolizaRC" value="${idPolizaRC}"/>
					<td class="literalborde" align="center">
						${especieRC.descripcion}
						<input type="hidden" name="codEspecieRC" id="codEspecieRC" value="${especieRC.codespecie}"/>
					</td>
					<td class="literalborde" align="center">
						<c:choose>
							<c:when test="${not empty regimenRCSeleccionado}">
								<select  class="dato" cssStyle="width:220px" id="codRegimenRC" name="codRegimenRC">
									<option value="${regimenRCSeleccionado.codregimen}">${regimenRCSeleccionado.descripcion}</option>
									<c:forEach items="${regimenesRC}" var="regimen">
										<option value="${regimen.codregimen}">${regimen.descripcion}</option>
									</c:forEach>
								</select>
							</c:when>
							<c:when test="${consultaRC}">
								<select  class="dato" cssStyle="width:220px" id="codRegimenRC" name="codRegimenRC" >
									<option selected="selected" value="">${regimenRCSeleccionado.descripcion}</option>
								</select>
							</c:when>
							<c:otherwise>				
								<select  class="dato" cssStyle="width:220px" id="codRegimenRC" name="codRegimenRC">
									<option value=""></option>
									<c:forEach items="${regimenesRC}" var="regimen">
										<option value="${regimen.codregimen}">${regimen.descripcion}</option>
									</c:forEach>
								</select>
							</c:otherwise>
						</c:choose>
					</td>
					<td class="literalborde" align="center">
						${numAnimales}
						<input type="hidden" name="numAnimalesRC" id="numAnimalesRC" value ="${numAnimales}"/>
					</td>
				</tr>
				<tr>
					<td colspan="3" class="literal">
						(1) Estabulaci�n Permanente: Los animales est�n permanentemente encerrados dentro de un establo y sus instalaciones anexas de recreo o alimentaci�n.
						<br/>
						(2) Semiestabulaci�n Regular: Para su alimentaci�n, los animales acceden a los pastos de la propia explotaci�n y el resto del d�a permanecen encerrados dentro de un establo y sus instalaciones anexas.
						<br/>
						(3) Semiestabulaci�n Estacional y/o Extensivo: Para su alimentaci�n, los animales permanecen en pastos naturales, ya sea un periodo del a�o o durante todo el periodo de explotaci�n.
						<br/>
						(4) Excluidas Reses Bravas y Caballos de Monta o Paseo												
					</td>
				</tr>			
			</table>	
		</c:otherwise>
	</c:choose>
	<br/>
	<br/>
</c:if>

<script>
	<c:if test="${not empty idPolizaRC}">
		$('#tablaContratarRC').show();
	</c:if>
	$('#calcularRC').change(function(){
		if(this.value == 'true'){
			$('#tablaContratarRC').show();
		} else {
			$('#tablaContratarRC').hide();
		}
	});
</script>
