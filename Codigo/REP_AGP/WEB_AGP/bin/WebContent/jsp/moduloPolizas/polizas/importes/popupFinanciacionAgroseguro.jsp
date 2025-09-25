<html>
<head>
	
<form name="frmFinanciacionAgr" id="frmFinanciacionAgr" method="post">

	<div id="panelFinanciacionAgr" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em;
		width: 45%; display: none; top: 270px; left: 33%; 
       position: absolute; z-index: 1003;border:1px solid #A4A4A4;background-color:#F2F2F2">
		
		<!--  header popup -->
		<div id="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; 
				font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px" >
			<div style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Financiaci&oacute;n de la p&oacute;liza</div>
			
			<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; 
				top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
				<span onclick="cerrarPopUpFinanciacionAgroseguro()">x</span>
			</a>
		</div>
		
		<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelInformacion" class="panelInformacion">
			<fieldset>
				<table border="0" style="width:70%">
					<tr>
						<td width="100%" align="center">
							<label class="literal">Total coste Tomador:</label>
							<label id="totCostTomFraccAgr" class="detalI"/><label class="detalI">&euro;</label>
						</td>
					</tr>
				</table>
			</fieldset>
			
			<fieldset>
				<input type="hidden" id="numCompFinanciacion" name="numCompFinanciacion" value="">
				<table border="0" style="width:70%">
					<tr>
						<td class="detalI" width="100%" align="center">
							Los pagos se har&aacute;n con la siguiente periodicidad:
						</td>
					</tr>
					<tr>
						<td class="detalI" width="100%" align="center">
							<ul>
								<li>1ª Fracci&oacute;n:</li>&nbsp;<label id="primeraFraccAgr" class="dato"></label><label class="detalI">&euro;</label>
								<li>2ª Fracci&oacute;n: A los 91 d&iacute;as</li>
								<li>3ª Fracci&oacute;n: A los 211 d&iacute;as</li>
							</ul>
						</td>
					</tr>
				</table>
				<table border="0" style="width:70%">
					<tr>
						<td width="100%" align="center">
							<label class="literal">Total coste Tomador con recargo:</label>
							<label id="totCostTomFraccRecargoAgr" class="detalI"></label><label class="detalI">&euro;</label>
						</td>
					</tr>
				</table>
				<table border="0" style="width:70%" class="detalI">
					<tr>
						<%-- <td width="100%" align="center">
							<c:if test="${modoLectura != 'modoLectura' && isLineaGanado eq true}}">
								<input type="checkbox" id="checkIBANAgr" checked="true"/> Enviar IBAN a Agroseguro
							</c:if>							
						</td> --%>
						
						<td width="100%" align="center">
							<c:choose>
								<c:when test="${(modoLectura == 'modoLectura'|| grProvisionalOK=='true') && isLineaGanado == 'true'}">
									<input type="checkbox" id="checkIBANAgr" checked="checked" disabled/> Enviar IBAN a Agroseguro
								</c:when>
								<c:otherwise>
									<input type="checkbox" id="checkIBANAgr" /> Enviar IBAN a Agroseguro
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</table>
			</fieldset>
		</div>				
		
		 <div class="botones" style="margin-top:15px" align="center">
			<c:choose>
				<c:when test="${modoLectura == 'modoLectura' || grProvisionalOK=='true'}">
					<!-- <a class="bot" href="javascript:financiarAgroseguro();" title="Financiar" id="btnFinanciacionAgr" disabled>Financiar</a>
					<a class="bot" href="javascript:noFinanciarAgroseguro()" title="No financiar" disabled>No financiar</a> -->
					<a class="bot" href="javascript:cerrarPopUpFinanciacionAgroseguro()" title="Cerrar">Cerrar</a>
				</c:when>
				<c:otherwise>
					<a class="bot" href="javascript:financiarAgroseguro();" title="Financiar" id="btnFinanciacionAgr">Financiar</a>
					<a class="bot" href="javascript:noFinanciarAgroseguro()" title="No financiar">No financiar</a>
				</c:otherwise>
			</c:choose>			
		</div>
		</div>				
	</div>
</form>

</head>
</html>

