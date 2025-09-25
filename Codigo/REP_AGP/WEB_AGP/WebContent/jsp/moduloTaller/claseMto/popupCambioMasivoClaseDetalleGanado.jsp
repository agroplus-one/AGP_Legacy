<!-- ************* -->
<!-- POPUP  AVISO  -->
<!-- ************* -->
		
		<div id="popUpAvisos" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
		     <!--  header popup -->
			 <div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
		        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">Aviso</div>
		        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
		                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
		            <span onclick="hidePopUpAviso()">x</span>
		        </a>
			 </div>
			 <!--  body popup -->
			 <div class="panelInformacion_content">
				<div id="panelInformacion" class="panelInformacion">
					<div id="txt_mensaje_aviso"></div>
				</div>
				<div style="margin-top:15px">
				 	    <a class="bot" id="btn_hidePopUpAviso" href="javascript:hidePopUpAviso()" title="Aceptar">Aceptar</a>
				</div>
			 </div>
		</div>                    

<!-- ************* -->
<!-- POPUP  CAMBIO MASIVO  -->
<!-- ************* -->

<div id="panelCambioMasivoClaseDetalleGanado" class="panelCambioMasivo" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em; 
	width: 85%; display: none;position: absolute; top: 270px; left: 10%;">
	 <!--  header popup -->
	<div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
	    <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">Cambio masivo</div>
		<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
			  <span onclick="cerrarCambioMasivo();">x</span>
		</a>
	</div>
	<div class="panelInformacion_content">
		<div>
			<div id="panelAlertasValidacionCambio" name="panelAlertasValidacionCambio" class="errorForm_cm" ></div>
		</div>
	</div>
	<!--  body popup -->
	<div class="panelInformacion_content">
		<div id="panelInformacion" class="panelInformacion">
			<form:form name="main" id="main" action="claseDetalleGanado.run" method="post" commandName="claseDetalleGanadoBean">
				<form:hidden path="id" id="id_cm" />
				<form:hidden path="clase.id" id="detalleid_cm"/>
				<form:hidden path="clase.linea.lineaseguroid" id="lineaseguroid"/>
				<form:hidden path="clase.linea.codplan" id="detalleplan_cm"/>
				<form:hidden path="clase.linea.codlinea" id="detallelinea_cm"/>
				<form:hidden path="clase.clase" id="detalleclase_cm"/>
				<input type="hidden" name="grupoNegocio" id="grupoNegocio_cm" value="2"/>				
				
				<input type="hidden" name="method" id="method" value="doCambioMasivo"/>
				<input type="hidden" name="listaIdsMarcados_cm" id="listaIdsMarcados_cm" value=""/>
				<div class="panel2 isrt">
					<fieldset>
						<table style="width:100%">
							<tr align="left">
								<td class="literal">Módulo</td>
									<td>
										<form:input path="codmodulo" size="5" maxlength="5" cssClass="dato" id="modulo_cm" tabindex="2" onchange="this.value=this.value.toUpperCase();"/>
									</td>
							</tr>
								<tr>
									<td class="literal">Provincia</td>
									<td>
										<form:input path="codprovincia" size="3" maxlength="2" cssClass="dato" id="provincia_cm"  onchange="javascript:lupas.limpiarCampos('desc_provincia_cm','comarca_cm','desc_comarca_cm','termino_cm','desc_termino_cm','subtermino_cm');" tabindex="2" /> 
										<form:input path="" cssClass="dato"	id="desc_provincia_cm" size="25" readonly="true"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('ProvinciaCM','principio', '', '');" alt="Buscar Provincia" title="Buscar Provincia" />
									</td>
									<td class="literal">Comarca</td>
									<td>
										<form:input path="codcomarca" size="3" maxlength="2" cssClass="dato" id="comarca_cm"  onchange="javascript:lupas.limpiarCampos('desc_comarca_cm','termino_cm','desc_termino_cm','subtermino_cm');" tabindex="2" /> 
										<form:input path="" cssClass="dato"	id="desc_comarca_cm" size="25" readonly="true"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('ComarcaCM','principio', '', '');" alt="Buscar Comarca" title="Buscar Comarca" />
									</td>
									<td class="literal">Termino</td>
									<td>
										<form:input path="codtermino" size="3" maxlength="3" cssClass="dato" id="termino_cm"  onchange="javascript:lupas.limpiarCampos('desc_termino_cm','subtermino_cm');" tabindex="2" /> 
										<form:input path="" cssClass="dato"	id="desc_termino_cm" size="25" readonly="true"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('TerminoCM','principio', '', '');" alt="Buscar Término" title="Buscar Término"  />
									</td>
									<td class="literal">Subtermino</td>
									<td>
										<form:input path="subtermino" size="1" maxlength="1" cssClass="dato" id="subtermino_cm" tabindex="2" /> 
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('TerminoCM','principio', '', '');" alt="Buscar Termino" title="Buscar Termino" />
									</td>
							</tr>
						</table>
					</fieldset>
				</div>
				<div class="panel2 isrt">
					<fieldset>
						<table style="border:goove" width="100%">
							<tr align="left">
								<td class="literal" >Especie</td>
								<td class="literal" width="25%" >
									<form:input path="codespecie" id="especie_cm" size="3" maxlength="3" cssClass="dato" onchange="javascript:lupas.limpiarCampos('desc_especie_cm');" tabindex="2"/>
									<form:input path="" cssClass="dato" id="desc_especie_cm" size="16" readonly="true"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;"onclick="javascript:lupas.muestraTabla('EspecieCM','principio', '', '');" alt="Buscar Especie" title="Buscar Especie" />
								</td>
								<td class="literal" align="left" width="15%" >		
									&nbsp
								</td>		
								<td>&nbsp</td>							
								<td class="literal" >Régimen</td>
								<td class="literal" width="25%">
									<form:input path="codregimen" id="regimen_cm" size="3" maxlength="3" cssClass="dato" onchange="javascript:lupas.limpiarCampos('desc_regimen');" tabindex="3"/>
									<form:input path="" cssClass="dato" id="desc_regimen_cm" size="16" readonly="true"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('RegimenCM','principio', '', '');" alt="Buscar Régimen" title="Buscar Régimen" />
								</td>
							</tr>
							<tr>
								<td class="literal">Grupo de raza</td>
								<td class="literal" width="25%">
									<form:input path="codgruporaza" size="5" maxlength="4" cssClass="dato" id="codgrupoRaza_cm"  onchange="javascript:lupas.limpiarCampos('desGrupoRaza_cm');"  tabindex="4"/>
									<form:input path="" cssClass="dato" id="desGrupoRaza_cm" size="25" readonly="true"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('GrupoRazaCM','principio', '', '');" alt="Buscar Grupo de Raza" title="Buscar Grupo de Raza" />
								</td>
								<td class="literal" width="15%">	
									&nbsp									
								</td>
								<td>&nbsp</td>	
								<td class="literal">Tipo de Animal</td>
								<td class="literal" width="25%">
									<form:input path="codtipoanimal" size="5" maxlength="4" cssClass="dato" id="codtipoanimal_cm" onchange="javascript:lupas.limpiarCampos('desTipoAnimal_cm');" tabindex="5"/>
									<form:input path="" cssClass="dato" id="desTipoAnimal_cm" size="25" readonly="true"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('TiposAnimalGanadoCM','principio', '', '');" alt="Buscar Tipo de Animal" title="Buscar Tipo de Animal" />
								</td>
							</tr>
							<tr>
								<td class="literal">Tipo de Capital</td>
								<td class="literal" width="25%">								
									<form:input path="codtipocapital" size="5" maxlength="4" cssClass="dato" id="codtipocapital_cm" onchange="javascript:lupas.limpiarCampos('desTipoCapital_cm');" tabindex="6"/>
									<form:input path="" cssClass="dato" id="desTipoCapital_cm" size="25" readonly="true"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('TipoCapitalGrupoNegocioCM','principio', 'codtipocapital', 'ASC');" alt="Buscar Tipo de Capital" title="Buscar Tipo de Capital" />
								</td>
								<td class="literal"  width="15%">	
									<input type="checkbox" id="tipoCapitalCheck" name="tipoCapitalCheck" onclick="deshabilitaCampo(this);" value="">Sin Valor
								</td>
							</tr>
						</table>
					</fieldset>
				</div>
				
			</form:form>
		</div>
		<div style="margin-top:15px">
		    <a class="bot" href="javascript:limpiarCambioMasivo()" title="Limpiar">Limpiar</a>
		    <a class="bot" href="javascript:cerrarCambioMasivo()" title="Cancelar">Cancelar</a>
		    <a class="bot" href="javascript:aplicarCambioMasivo()" title="Aplicar">Aplicar</a>
		</div>
	</div>
</div>