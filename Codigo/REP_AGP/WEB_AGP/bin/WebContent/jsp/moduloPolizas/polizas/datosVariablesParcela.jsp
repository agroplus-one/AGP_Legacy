<c:set var="tabIndex">${tabIndex+1}</c:set>
<c:set var="valor"></c:set>
<c:forEach items="${parcelaBean.capitalAsegurado.datosVariablesParcela}" var="dvVO">
	<c:if test="${dv.codConcepto eq dvVO.codconcepto}">
		<c:set var="valor">${dvVO.valor}</c:set>
	</c:if>
</c:forEach>
<div style="position: absolute; top : ${dv.y}px; left : ${dv.x}px;">
	<table border="0" width="100%">
	<tr>
		<td class="detalI" nowrap="nowrap">
			<label for="cod_cpto_${dv.codConcepto}">${dv.etiqueta}</label>
			<c:choose>
				<c:when test="${dv.idtipo == 1}"> 
					<!-- TIPO TEXTO -->
					<input type="text" style="width: ${dv.ancho}px; height: ${dv.alto}px;" id="cod_cpto_${dv.codConcepto}" name="cod_cpto_${dv.codConcepto}" ${dv.deshabilitado  == 'S' && dv.mostrar  == 'N' ? 'disabled' : ''} tabindex="${tabIndex}" maxlength="${dv.tamanio}" tipoDV="${dv.idtipo}" value="${valor}" onchange="isAlreadySaved = false;" />
				</c:when>
				<c:when test="${dv.idtipo == 3}">
					<!-- TIPO FECHA -->
					<input type="text" style="width: ${dv.ancho}px; height: ${dv.alto}px;" id="cod_cpto_${dv.codConcepto}" name="cod_cpto_${dv.codConcepto}" ${dv.deshabilitado  == 'S' && dv.mostrar  == 'N' ? 'disabled' : ''} tabindex="${tabIndex}" maxlength="10" tipoDV="${dv.idtipo}" value="${valor}" onchange="isAlreadySaved = false;" />
					<c:if test="${modoLectura != 'modoLectura'}">
						<input type="button" id="btn_cod_cpto_${dv.codConcepto}" name="btn_cod_cpto_${dv.codConcepto}" class="miniCalendario" style="cursor: pointer;" />
					</c:if> 
				</c:when>
				<c:when test="${dv.idtipo == 4}">
					<!-- TIPO TEXTO MULTILINEA -->
					<textarea style="width: ${dv.ancho}px; height: ${dv.alto}px; resize: none;" id="cod_cpto_${dv.codConcepto}" name="cod_cpto_${dv.codConcepto}" ${dv.deshabilitado  == 'S' && dv.mostrar  == 'N' ? 'disabled' : ''} tabindex="${tabIndex}" wrap="hard"  tipoDV="${dv.idtipo}" onchange="isAlreadySaved = false;">${valor}</textarea>
				</c:when>
				<c:when test="${dv.idtipo == 5}">
					<!-- TIPO SELECCION MULTIPLE -->
					<select size="${dv.tamanio}" multiple="multiple" id="cod_cpto_${dv.codConcepto}" style="width: ${dv.ancho}px; height: ${dv.alto}px;" name="cod_cpto_${dv.codConcepto}" ${dv.deshabilitado  == 'S' && dv.mostrar  == 'N' ? 'disabled' : ''} tabindex="${tabIndex}" tipoDV="${dv.idtipo}" onchange="isAlreadySaved = false;">
						<c:forEach items="${dv.valores}" var="lvBean" varStatus="loop">
							<option value="${lvBean.codigo}" ${lvBean.codigo  == valor ? 'selected' : ''}>${lvBean.descripcion}</option>
						</c:forEach>
					</select>
				</c:when>
				<c:when test="${dv.idtipo == 6}">
					<!-- TIPO LUPA -->
					<c:if test="${dv.codConcepto == 134}">
						<!-- TRATAMIENTO ESPECIFICO FECHA FIN GARANTIAS [134] -->
						<input type="text" id="cod_cpto_${dv.codConcepto}" style="width: 100px; height: ${dv.alto}px;" name="cod_cpto_${dv.codConcepto}" ${dv.deshabilitado  == 'S' && dv.mostrar  == 'N' ? 'disabled' : ''} maxlength="${dv.tamanio}" tipoDV="${dv.idtipo}" value="${valor}" onchange="isAlreadySaved = false;" />
					</c:if>
					<c:if test="${dv.codConcepto != 134}">
						<input type="text" id="cod_cpto_${dv.codConcepto}" style="width: 50px; height: ${dv.alto}px;" name="cod_cpto_${dv.codConcepto}" ${dv.deshabilitado  == 'S' && dv.mostrar  == 'N' ? 'disabled' : ''} onchange="javascript:lupas.limpiarCampos('des_cpto_${dv.codConcepto}'); getDatosConcepto('${dv.codConcepto}', this.value, event); isAlreadySaved = false;" tabindex="${tabIndex}"  maxlength="${dv.tamanio}" tipoDV="${dv.idtipo}" value="${valor}" />
						<input type="text" id="des_cpto_${dv.codConcepto}" style="width: ${dv.ancho}px; height: ${dv.alto}px;" name="des_cpto_${dv.codConcepto}" ${dv.deshabilitado  == 'S' && dv.mostrar  == 'N' ? 'disabled' : 'readonly'} />
					</c:if>
					<c:if test="${modoLectura!= 'modoLectura'}">
						<img id="lupa_cpto_${dv.codConcepto}" src="jsp/img/magnifier.png" style="cursor: hand; ${dv.deshabilitado  == 'S' && dv.mostrar  == 'N' ? 'display: none' : ''}" onclick="javascript:actualizaCptoFactores(${dv.codConcepto}); lupas.muestraTabla('${dv.tabla_asociada}','principio', '', '');" />
					</c:if>
				</c:when>
				<c:when test="${dv.idtipo == 7}">
					<!-- TIPO CHECKBOX MULTIPLE -->
					<input type="hidden" id="cod_cpto_${dv.codConcepto}" name="cod_cpto_${dv.codConcepto}" tipoDV="${dv.idtipo}" value="${valor}" onchange="isAlreadySaved = false;" /> 
					<div style="width: ${dv.ancho}px; height: ${dv.alto}px; overflow-x: hidden; overflow-y: auto; border: 1px solid #767676" >
						<c:forEach items="${dv.valores}" var="lvBean" varStatus="loop">
							<c:set var="tabIndex">${tabIndex+1}</c:set>
							<input id="cod_cpto_${dv.codConcepto}_${loop.index}" type="checkbox" value="${lvBean.codigo}" onchange="actualizaHdnTipo7(${dv.codConcepto});" tabindex="${tabIndex}" ${dv.deshabilitado  == 'S' && dv.mostrar  == 'N' ? 'disabled' : ''}/>
							<label for="cod_cpto_${dv.codConcepto}_${loop.index}">${lvBean.codigo}-${lvBean.descripcion}</label>
							<br/>
						</c:forEach>						
					</div>
				</c:when>
			</c:choose>	
			<c:if test="${modoLectura!= 'modoLectura'}">
				<label class="campoObligatorio" id="campoObligatorio_cod_cpto_${dv.codConcepto}" title="Campo obligatorio">*</label>	
			</c:if>
		</td>
	</tr>
	</table>														
</div>