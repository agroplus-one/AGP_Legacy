

<html>
<head>



	
<form:form name="frmCalculoFinanciacion" id="frmCalculoFinanciacion" action="webservicesCpl.html" method="post" commandName="polizaBean">
	<input type="hidden" name="costeTomador_cf" id="costeTomador_cf" value=""/>
	<input type="hidden" name="origenllamada" id = "origenllamada_cf" value="financiacion" />
	<input type="hidden" name="esFinanciacionCpl" id = "esFinanciacionCpl" value="${esFinanciacionCpl}" />
	<input type="hidden" name="operacion" id = "operacion_cf" value="calcular" />
	<input type="hidden" name="financiacionSeleccionada" id="financiacionSeleccionada_cf" value=""/> 
	<input type="hidden" name="idpoliza" id="idPoliza_cf" value="${idpoliza}"/>		
	<input type="hidden" name="method" id="method_cf" />
	<input type="hidden" name="validComps" id="validComps" value="${validComps}"/>
	<input type="hidden" name="totalCosteTomadorAFinanciar" id="totalCosteTomadorAFinanciar_cf" value=""/>
	<input type="hidden" name="idpolizaComp" id="idPolizaComp" value="${idpolizaCpl}"/>
	<input type="hidden" name="pctMinFinanc_cf" id="pctMinFinanc_cf" value=""/>
	<div id="panelCalculofinanciacion" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em;
		width: 45%; display: none; top: 270px; left: 33%; 
       position: absolute; z-index: 1003;border:1px solid #A4A4A4;background-color:#F2F2F2">
		
		<!--  header popup -->
		<div id="header-popup" class="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; 
				font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px" >
			<div style="float:left; margin: 0 0 0 0; font-size:11px; line-height:15px">
				<c:choose>
					<c:when test="${modoLectura == 'modoLectura' || grProvisionalOK=='true'}">
						Datos de financiación de la póliza
					</c:when>
					<c:otherwise>
						Cálculo de la financiación de pólizas
					</c:otherwise>
				</c:choose>
			</div>
			
			<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; 
				top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
				<span onclick="cerrarPopUpCalculoFinanciacion()">x</span>
			</a>
		</div>
		<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelInformacion" class="panelInformacion">
			<fieldset style="border:0px">
				<div id="panelAlertasValidacion_cf" name="panelAlertasValidacion_cf" class="errorForm_fp" align="center"></div>
			</fieldset>
			<table width="80%">
				<tr>
					<td class="literal">Total coste tomador:</td>
					<td class="detalI" align="right">
						<label id="costeTomador_lb" class="dato"></label>&euro;</br>
					</td>
					<td width="50px">&nbsp;</td>
					<td class="literal" rowspan="2">
						<nobr>Periodo:
						<select name="condicionesFraccionamiento" id="condicionesFraccionamiento" class="dato" value="${periodo_cf}" style="width:50px">
							
							<c:forEach items="${condicionesFraccionamiento}" var="cf">								
								<option value="${cf.id.periodoFracc}">${cf.id.periodoFracc}</option>								
							</c:forEach>
						</select>
						</nobr>
					</td>
				</tr>
				<tr>
					<td class="detalI" colspan="3">
						(con subvención por financiación con SAECA)
					</td>
				</tr>
			</table>
			<table>
				<tr>
					<td class="literal" align="left">
						<input type="radio" name="opcion_cf" id="porcentajeCheck" value="0" onclick="seleccionaImporte();">% Sobre coste tomador
					</td>
					<td class="literal" align="left">
						<input type="text" name="porcentajeCosteTomador_txt" id="porcentajeCosteTomador_txt"  class="dato" size="5" maxlength="6" value="${porCosTom_cf}"/>
					</td>
				</tr>
				<tr>
					<td class="literal" align="left">
						<input type="radio" name="opcion_cf" id="importeCheck"  value="1" onclick="seleccionaImporte();">Importe
					</td>
					<td class="literal" align="left">
						<input type="text" name="importeFinanciar_txt" id="importeFinanciar_txt"  class="dato" size="10" maxlength="12" value="${impFinan_cf}"/>						
					</td>
				</tr>
				<tr>
					<td class="literal" align="left">
						<input type="radio" name="opcion_cf" id="importeAvalCheck" value="2" onclick="seleccionaImporte();">Importe Aval
					</td>
					<td class="literal" align="left">
						<input type="text" name="importeAval_txt" id="importeAval_txt"  class="dato" size="10" maxlength="12" value="${impAval_cf}"/>
					</td>
				</tr>
			</table>
			<c:if test="${idestado!=null && idestado >= 3}">
				<div style="width: 80%">
				<fieldset>
					<legend class="literal">Datos del aval</legend>
					<table>
						<tr>
							<td class="literal">Número:</td>
							<td class="detalI">${numaval}</td>
							<td width="20px">&nbsp;</td>
							<td class="literal" align="right">Importe:</td>
							<td class="detalI" width="*">
								<fmt:setLocale value="es_ES" />
									<fmt:formatNumber value="${importeaval}" type="currency" currencySymbol="&euro;"/> 
							</td>
						</tr>
					</table>
				</fieldset>
				</div>
			</c:if>
		</div>				
		
		 <div class="botones" style="margin-top:15px" align="center">			      
			<a class="bot" href="javascript:calcularFinanciacion();" title="Calcular" id="btnCalcular_da">Aplicar</a>
			<a class="bot" href="javascript:cerrarPopUpCalculoFinanciacion()" title="Cancelar">Cancelar</a>					
		</div>
		</div>				
	</div>
</form:form>
</head>
</html>

