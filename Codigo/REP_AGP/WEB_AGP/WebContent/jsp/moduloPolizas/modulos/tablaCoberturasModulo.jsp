<%@ include file="/jsp/common/static/taglibs.jsp" %>

<!-- Guarda el código de módulo para las validaciones JS -->
<script>listaModulos.push ('${modulo.codModulo}');</script>
<div class="conten" id="compDIV_${modulo.codModulo}_${modulo.numComparativa}" border=0>
<div class="panel2 isrt" style="width: 98%;" align="center" >
<fieldset style="border:1px solid #CCC;">

	<!-- Descripción del módulo -->
	<legend class="literal">${msgDescModulo}M&Oacute;DULO ${modulo.codModulo} - ${modulo.descripcionModulo}</legend></legend>
	
	<!-- Tabla de comparativas -->
	<table width="100%">
		<tr>
			<!--  -->
			<!-- Cabecera de la tabla -->
			<!--  -->
			<!-- Obtiene el número de elementos de cabecera para establecer el ancho de las columnas por igual -->
			<c:set var="ancho" value="${fn:length(modulo.listaCabeceras)}"/>
			
			<td class="literalbordeCabecera" align="center" valign="" style="width:17%;">GARANT&Iacute;A</td>
			<td class='literalbordeCabecera' align="center" valign="" style="width:17%;">RIESGOS CUBIERTOS</td>
			<td class="literalbordeCabecera" width="66%">
			
			<c:forEach items="${modulo.listaCabeceras}" var="cabecera" varStatus="status">
				<!-- Comienzo del apartado de condiciones de cobertura -->
				<c:if test="${status.index == 0}">
					<table width="100%">
						<tr>
							<td colspan="${ancho-numCobFijas}" class="literalbordeCabecera" align="center" width="${66 - (numCobFijas*(66/ancho))}%">CONDICIONES COBERTURAS</td>
						</tr>
						<tr>
				</c:if>
				
				<!-- Pinta la cabecera de la columna -->
				<td class="literalbordeCabecera" align="center" valign="" width="${100/ancho}%">${cabecera}</td>
				
				<!-- Fin del apartado de condiciones de cobertura  -->
				<c:if test="${status.index == (ancho-1)}">
						</tr>
					</table>
				</c:if>
			</c:forEach>
			</td>
		</tr>
		
		<!--  -->
		<!-- Cuerpo de la tabla -->
		<!--  -->
		<c:forEach items="${modulo.listaFilas}" var="fila" varStatus="statusFila">
		
			<!-- Si es la primera fila del módulo -->
			<c:if test="${statusFila.index == 0}">
				<input type="hidden" name="cptSinElegibles_${modulo.codModulo}_${modulo.numComparativa}" id="cptSinElegibles_${modulo.codModulo}_${modulo.numComparativa}" value="${modulo.codModulo}_${modulo.numComparativa}_${fila.filamodulo}_${fila.codConceptoPrincipalModulo}_${fila.codRiesgoCubierto}_2_1_-2"/>
			</c:if>
		
			<tr>							
				<!-- "Concepto principal del módulo" --> 
				<td class="literalborde" align="center">${fila.conceptoPrincipalModulo}</td>
				
				<!-- Riesgo cubierto -->
				<td class="literalborde" align="center">
				
					<!-- Si el riesgo cubierto es elegible se muestra el check correspondiente -->
					<c:if test="${fila.rcElegible}">
					
						<FONT color="red">ELEGIBLE</FONT><br/>
						
						<!-- Modo edición -->
						<c:if test="${tabPlz == false}">	
							<!-- En el cuadro del anexo, si la cobertura es elegible y básica se añade a las validaciones-->
							<c:if test="${fila.basica}">
								<script>
									var cbe = {comparativa:${modulo.numComparativa},modulo:'${modulo.codModulo}', fila:${fila.filamodulo}, cpm:${fila.codConceptoPrincipalModulo}, rc:${fila.codRiesgoCubierto}, rce:${fila.codCptoRCE}, filaComp:${fila.filaComparativa}, descFila:'${fila.riesgoCubierto}'};
									listaCoberturasBasicasElegibles.push(cbe);
								</script>
							</c:if>
						
						
							<c:set var="idCheck" value="check_${modulo.codModulo}_${modulo.numComparativa}_${fila.filamodulo}_${fila.codConceptoPrincipalModulo}_${fila.codRiesgoCubierto}_${fila.codCptoRCE}_${fila.filaComparativa}"/>
							
											
							<input type="checkbox" style="background-color: #E5E5E5" name="vincRiesgos" id="${idCheck}" value="" onclick="gestElegiblesAsociados (this,${modulo.numComparativa},'${modulo.codModulo}',${fila.filamodulo});"/>&nbsp;

							<!-- guardamos las de RyD si tiene-->
							<c:if test="${fila.codConceptoPrincipalModulo == 183}">
								<c:if test="${fila.codRiesgoCubierto == 23}">
									<script>
										var cRyD = {comparativa:${modulo.numComparativa}, modulo:'${modulo.codModulo}', fila:${fila.filamodulo}, cpm:${fila.codConceptoPrincipalModulo}, rc:${fila.codRiesgoCubierto}, rce:${fila.codCptoRCE}, filaComp:${fila.filaComparativa}, descFila:'${fila.riesgoCubierto}'};
										listaRyDElegibles.push(cRyD);
									</script>
								</c:if>
							</c:if>

							<!-- Almacena la fila y la descripción del riesgo cubierto elegible para utilizarlo en las validaciones de los vinculados -->
							<script>listaRiesgosCubiertosElegibles.push ({comparativa:${modulo.numComparativa}, codfila:${fila.filamodulo}, desRC:'${fila.riesgoCubierto}'});</script>
						
							<!-- Se comprueba si el riesgo cubierto elegible tiene vinculaciones con otros riesgos -->
							<c:forEach items="${fila.listVinculaciones}" var="vinc">
								<script>
									var v = {comparativa:${modulo.numComparativa}, modulo:'${modulo.codModulo}', fila:${fila.filamodulo}, elegida:${vinc.elegida}, descFila:'${fila.riesgoCubierto}', vincFila:${vinc.vincFila}, vincElegida:${vinc.vincElegida}, grupoVinculacion:${vinc.grupoVinculacion}};
									listaVinculaciones.push(v);
								</script>								
							</c:forEach>
							<!-- Se comprueba si el riesgo cubierto elegible tiene vinculaciones con otras filas al no seleccionarse -->
						</c:if>
						
						<!-- Modo póliza - Marcados y dehabilitados -->
						<c:if test="${tabPlz == true}">	
							<c:set var="idCheck" value="check_${modulo.codModulo}_${modulo.numComparativa}_${fila.filamodulo}_${fila.codConceptoPrincipalModulo}_${fila.codRiesgoCubierto}_${fila.codCptoRCE}_${fila.filaComparativa}"/>					
							<input type="checkbox" style="background-color: #E5E5E5" name="vincRiesgos" checked="checked" disabled="disabled" id="${idCheck}"/>&nbsp;
						</c:if>
						
					</c:if>
					
					${fila.riesgoCubierto}
				</td>
				
				<!-- Lista de condiciones de cobertura -->
				<td class="literalborde">
					<table width="100%">
						<tr>
							<c:forEach items="${fila.celdas}" var="celda" varStatus="statusCelda">
								<td class="literalborde" width="${100/ancho}%" align="center">
								
								<!-- Se comprueba si tiene informado el campo 'Valor' -->
								<c:if test="${not empty celda.valores}">
									<!-- Si no es elegible, se pinta la descripción -->
									<c:if test="${celda.elegible == false}">
										<c:forEach items="${celda.valores}" var="valor">
								    		${valor.descripcion}
								    	</c:forEach>
									</c:if>
									
									<!-- Si es elegible se crea un combo de valor-descripción -->
									<c:if test="${celda.elegible}">
										<FONT color="red">ELEGIBLE</FONT>
										<br/>
										
										<!-- Modo edición -->
										<c:if test="${tabPlz == false}">
											<c:set var="idSelect" value="select_${modulo.codModulo}_${modulo.numComparativa}_${fila.filamodulo}_${fila.codConceptoPrincipalModulo}_${fila.codRiesgoCubierto}_${celda.codconcepto}_${celda.columna}"/>
											<c:set var="idCampoObligatorio" value="campoObligatorioSelect_${modulo.codModulo}_${modulo.numComparativa}_${fila.filamodulo}_${fila.codConceptoPrincipalModulo}_${fila.codRiesgoCubierto}_${celda.codconcepto}_${celda.columna}"/>
											
											<!-- Si el riesgo cubierto asociado a esta cobertura es elegible, se deshabilita el combo
												y sólo se habilitará si se elige el riesgo -->
											<c:if test="${fila.rcElegible}">
												<select class="dato" id="${idSelect}" name="comboElegible" disabled="disabled" onchange="javascript:validarVinculados(this.id, '${idCheck}');">
											</c:if>
											<c:if test="${fila.rcElegible eq false}">
												<select class="dato" id="${idSelect}" name="comboElegible" onchange="javascript:validarVinculados(this.id, '');">
											</c:if>
											
												<option value=""/>
												<c:forEach items="${celda.valores}" var="valor">
													<option value="${valor.codigo}">${valor.descripcion}</option>
												</c:forEach>
											</select>
											<label id="${idCampoObligatorio}" class="campoObligatorio" title="Campo obligatorio"> *</label>
										</c:if>
										
										<!-- Modo póliza - Marcados y dehabilitados -->
										<c:if test="${tabPlz == true}">			
											<c:set var="idSelect" value="plz_select_${modulo.codModulo}_${modulo.numComparativa}_${fila.filamodulo}_${fila.codConceptoPrincipalModulo}_${fila.codRiesgoCubierto}_${celda.codconcepto}_${celda.columna}"/>			
											<select class="dato" name="${idSelect}" disabled="disabled" id="${idSelect}">
												<c:forEach items="${celda.valores}" var="valor">
													<option value="${valor.codigo}">${valor.descripcion}</option>
												</c:forEach>
											</select>
										</c:if>
										
									</c:if>
								</c:if>
								
								<!-- Si no tiene informado el campo 'Valor' se comprueba si tiene informado el campo 'Observaciones'-->
								<c:if test="${empty celda.valores}">
								    <c:if test="${empty celda.observaciones}">
								    	&nbsp;
									</c:if>
									<c:if test="${not empty celda.observaciones}">
								    	${celda.observaciones}
									</c:if>
								</c:if>
							
								</td>
							</c:forEach>
						</tr>
					</table>
				</td>
			</tr>
		</c:forEach>
		
		<c:if test="${empty modulo.listaFilas}">
			<tr><td class="literalborde" colspan="3">Sin riesgos cubiertos elegibles</td></tr>
		</c:if>	
	
		<!-- Combo de elección de módulo renovable y tipología de asegurado (si aplica) -->
		<td class="literal" align="right" colspan="${ancho}" >
			
			<!-- Modo edición -->
			<c:if test="${renovableSoloLectura == false}">
				<c:if test="${modoLectura != 'modoLectura'}">
					<c:if test="${modulo.numComparativa > 1}">
						<a href="javascript:borrarComparativa(${modulo.numComparativa},'${modulo.codModulo}')"><img width="16" alt="Borrar Comparativa" src="jsp/img/displaytag/cancel.png"/>Borrar Comparativa</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					</c:if>
				</c:if>				
				<c:if test="${not empty listaTipoAseguradoGanado}">
					Tipolog&iacute;a del asegurado&nbsp;
					
					<select id="tipologiaAsegurado_${modulo.codModulo}_${modulo.numComparativa}" name="tipologiaAseg" class="dato" >
						<option value=""/>
						
						<c:forEach items="${listaTipoAseguradoGanado}" var="tipologia">
							<c:choose>
								 <c:when test="${modulo.tipoAsegGanado eq tipologia.id.valorCpto}">
								 	<option selected="selected" value="${tipologia.id.valorCpto}">${tipologia.descripcion}</option>
								 </c:when>
								 <c:otherwise>
								 	<option value="${tipologia.id.valorCpto}">${tipologia.descripcion}</option>
								 </c:otherwise>
							</c:choose>							
						</c:forEach>
					</select>
					<label class="campoObligatorioTipologia" id="campoObligatorioTipologia" title="Campo obligatorio"> *</label>
					&nbsp;
				</c:if>
		
				<c:if test="${esPolizaGanado}">
					Renovable&nbsp;
					<select id="modRenovable_${modulo.codModulo}_${modulo.numComparativa}" name="modRenovable" class="dato" >
						<option value=""></option>
						<c:choose>
							 <c:when test="${modulo.renovable!=null && modulo.renovable eq 0}">
							 	<option selected="selected" value="0">No</option>
							 </c:when>
							 <c:otherwise>
							 	<option value="0">No</option>
							 </c:otherwise>
						</c:choose>
						<c:choose>
							 <c:when test="${modulo.renovable!=null && modulo.renovable eq 1}">
							 	<option selected="selected" value="1">S&iacute;</option>
							 </c:when>
							 <c:otherwise>
							 	<option value="1">S&iacute;</option>
							 </c:otherwise>
						</c:choose>				
					</select>
					<label class="campoObligatorioRenov" id="campoObligatorioRenov" title="Campo obligatorio"> *</label>
				</c:if>
				<c:if test="${!esPolizaGanado}">
					<select id="modRenovable_${modulo.codModulo}_${modulo.numComparativa}" name="modRenovable" style="display:none;" >
						<option value="0">No</option>
					</select>
				</c:if>
			</c:if>
			
			<!-- Modo póliza - Marcados y dehabilitados -->
			<c:if test="${renovableSoloLectura == true}">
			
				<c:if test="${tabPlz == false}">	
					<c:if test="${not empty listaTipoAseguradoGanado}">
						Tipolog&iacute;a del asegurado&nbsp;
						
						<select id="tipologiaAseguradoAnex_${modulo.numComparativa}" name="tipologiaAseg" class="dato" >
							<option value=""/>
							
							<c:forEach items="${listaTipoAseguradoGanado}" var="tipologia">
								<c:choose>
									 <c:when test="${modulo.tipoAsegGanado eq tipologia.id.valorCpto}">
									 	<option selected="selected" value="${tipologia.id.valorCpto}">${tipologia.descripcion}</option>
									 </c:when>
									 <c:otherwise>
									 	<option value="${tipologia.id.valorCpto}">${tipologia.descripcion}</option>
									 </c:otherwise>
								</c:choose>							
							</c:forEach>
						</select>
						<label class="campoObligatorioTipologia" id="campoObligatorioTipologia" title="Campo obligatorio"> *</label>
						&nbsp;
					</c:if>
				</c:if>
				
				<c:if test="${tabPlz == true}">	
					<c:if test="${not empty listaTipoAseguradoGanado}">
						Tipolog&iacute;a del asegurado&nbsp;
						
						<select id="tipologiaAsegurado_${modulo.numComparativa}" name="tipologiaAseg" class="dato" disabled>
							<option value=""/>
							
							<c:forEach items="${listaTipoAseguradoGanado}" var="tipologia">
								<c:choose>
									 <c:when test="${modulo.tipoAsegGanado eq tipologia.id.valorCpto}">
									 	<option selected="selected" value="${tipologia.id.valorCpto}">${tipologia.descripcion}</option>
									 </c:when>
									 <c:otherwise>
									 	<option value="${tipologia.id.valorCpto}">${tipologia.descripcion}</option>
									 </c:otherwise>
								</c:choose>							
							</c:forEach>
						</select>
						<label class="campoObligatorioTipologia" id="campoObligatorioTipologia" title="Campo obligatorio"> *</label>
						&nbsp;
					</c:if>
				</c:if>
			
				Renovable:&nbsp; <c:if test="${modulo.renovable eq 0}">No</c:if><c:if test="${modulo.renovable eq 1}">S&iacute;</c:if>
			</c:if>
		</td>
	</table>
	
</fieldset>
</div>
</div>
<c:if test="${renovableSoloLectura == false}">
	<c:if test="${modoLectura != 'modoLectura'}">
		<table width="98%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">
					<div  id= "botonDuplica_${modulo.codModulo}_${modulo.numComparativa}" border=0>
						<c:if test="${perfil == 0 && not esPolizaGanado}">
							<a href="javascript:descargarxml('${modulo.codModulo}')"><img width="16" height="16" alt="Descargar XML" title="Descargar XML" src="jsp/img/jmesa/csv.gif"/></a>
						</c:if>
						<a  href="javascript:duplica('${modulo.codModulo}');" align="right"><img width="16" alt="Añadir Comparativa" src="jsp/img/jmesa/png/addWorksheetRow.png"/>Añadir Comparativa</a>
					</div>
				</td>
			</tr>
		</table>
	</c:if>
</c:if>

