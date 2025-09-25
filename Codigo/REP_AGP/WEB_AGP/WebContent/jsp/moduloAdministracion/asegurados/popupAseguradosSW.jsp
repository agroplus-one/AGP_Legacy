<!-- TMR 11/12/2014 -->
<!-- popupAseguradosSW.jsp (show in asegurados.jsp) -->
<script type="text/javascript">



function enviaForm (operacion){
	
	var fmain2 = document.getElementById("frmAseguradoSW");
	fmain2.method.value = operacion;
	
	$('#divAseguradoSW').hide();
	$('#overlay').hide();
	
	var fmain = document.getElementById("main");
	$('#idAseg').val(fmain.idAseguradoP.value);
	fmain2.cargaAseg.value = fmain.cargaAseg.value;
	$('#frmAseguradoSW').submit();
	
}

</script>

<form:form name="frmAseguradoSW" action="asegurado.html" method="post" id="frmAseguradoSW" commandName="aseguradoPopup">
	<input type="hidden" name="method" id="method" />		
	<input type="hidden" name="idAseg" id="idAseg" />
	<input type="hidden" name="cargaAseg" id="cargaAseg" value="${cargaAseg}"/>
	
	<!--  popup AseguradoSW-->
	<div id="divAseguradoSW" class="parcelasRepWindow" style="color: #333333; left: 11%; width: 85%; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em; z-index: 1005">
		<!--  header popup -->
		<div id="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px">
			<div style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Datos obtenidos de Agroseguro</div>
			<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
				<span onclick="enviaForm('actualizaFechaRevision')">x</span>
			</a>
		</div>

		<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelInformacion" class="panelInformacion">
				<div id="txt_mensaje_asg" style="color:red;display:block;font-size:12px;font-style:italic;font-weight:bold;line-height:20px;"></div>
				<div id="txt_validacionesAsg" name="txt_validacionesAsg" style="color:red" class="errorFormRes"></div>
			
				<div style="panel2 isrt">
					<fieldset style="width:95%; margin:0 auto;" align="center">
						<table width="95%">
							<tr>
								
								<td class="literal" >NIF/CIF</td>
								<td class="labelAseg" >
									<form:hidden path="nifcif"  id="nifcifP"/>
									<c:out value="${aseguradoPopup.nifcif}"/> 
								</td>
								<c:choose>
									<c:when test="${aseguradoPopup.razonsocial != null}">	
							
										<td class="literal" >Razón Social</td>
										<td class="labelAseg" >
											<form:hidden path="razonsocial"  id="razonSocialP"/>
											<c:out value="${aseguradoPopup.razonsocial}"/> 
										</td>
									</c:when>
									<c:otherwise>
										<td class="literal" >Nombre</td>
										<td class="labelAseg" >
											<form:hidden path="nombre"  id="nombreP"/>
											<c:out value="${aseguradoPopup.nombre}"/> 
										</td>
										<td class="literal" >1º Apellido</td>
										<td class="labelAseg" >
											<form:hidden path="apellido1"  id="apellido1P"/>
											<c:out value="${aseguradoPopup.apellido1}"/> 
										</td>
										<td class="literal" >2º Apellido</td>
										<td class="labelAseg" >
											<form:hidden path="apellido2"  id="apellido2P"/>
											<c:out value="${aseguradoPopup.apellido2}"/> 
										</td>
									</c:otherwise>
								</c:choose>
							</tr>
							<tr>
								<td class="literal" >Vía</td>
								<td class="labelAseg" >
									<form:hidden path="via.clave"  id="viaP"/>
									<c:out value="${aseguradoPopup.via.clave}" /> 
								</td>
								<td class="literal" >Domicilio</td>
								<td class="labelAseg" >
									<form:hidden path="direccion"  id="domP"/>
									<c:out value="${aseguradoPopup.direccion}"/> 
								</td>
								<td class="literal" >Nº</td>
								<td class="labelAseg" >
									<form:hidden path="numvia"  id="numeroP"/>
									<c:out value="${aseguradoPopup.numvia}"/> 
								</td>
								<td class="literal" >Piso</td>
								<td class="labelAseg" >
									<form:hidden path="piso"  id="pisoP"/>
									<c:out value="${aseguradoPopup.piso}"/> 
								</td>
								<td class="literal" >Bloque</td>
								<td class="labelAseg" >
									<form:hidden path="bloque"  id="bloqueP"/>
									<c:out value="${aseguradoPopup.bloque}"/> 
								</td>
								<td class="literal" >Escalera</td>
								<td class="labelAseg" >
									<form:hidden path="escalera"  id="escP"/>
									<c:out value="${aseguradoPopup.escalera}"/> 
								</td>
							</tr>
							<tr>
								<td class="literal" >Provincia</td>
								<td class="labelAseg" >
									<form:hidden path="localidad.id.codprovincia"  id="codprovinciaP"/>
									<form:hidden path="localidad.provincia.nomprovincia"  id="nomprovinciaP"/>
									<c:out value="${aseguradoPopup.localidad.id.codprovincia}"/> - <c:out value="${aseguradoPopup.localidad.provincia.nomprovincia}"/>
								</td>
								<td class="literal" >Localidad</td>
								<td class="labelAseg" >
									<form:hidden path="localidad.nomlocalidad"  id="nomLocalidadP"/>
									<form:hidden path="localidad.id.codlocalidad"  id="codLocalidadP"/>
									<c:out value="${aseguradoPopup.localidad.id.codlocalidad}"/> - <c:out value="${aseguradoPopup.localidad.nomlocalidad}"/> 
								</td>
								
								<form:hidden path="localidad.id.sublocalidad"  id="sublocalidadP"/>
								
								<td class="literal" >Cód. Postal</td>
								<td class="labelAseg" >
									<form:hidden path="codpostal"  id="codpostalP"/>
									<c:out value="${aseguradoPopup.codpostal}"/> 
								</td>
							</tr>
							<tr>
								<td class="literal" >Teléfono Fijo</td>
								<td class="labelAseg" >
									<form:hidden path="telefono"  id="tlfFijoP"/>
									<c:out value="${aseguradoPopup.telefono}"/>
								</td>
								<td class="literal" >Teléfono móvil</td>
								<td class="labelAseg" >
									<form:hidden path="movil"  id="movilP"/>
									<c:out value="${aseguradoPopup.movil}"/>
								</td>
								<td class="literal" >e-mail</td>
								<td class="labelAseg" >
									<form:hidden path="email"  id="emailP"/>
									<c:out value="${aseguradoPopup.email}"/>
								</td>
							</tr>
							
						</table>
					</fieldset>
				</div>	

			</div>
		</div>
		<!-- Botones popup --> 
	    <div style="margin-top:15px;margin-bottom:5px;" align="center">
			<a class="bot" href="javascript:enviaForm('actualizaFechaRevision')">Cancelar</a>
			<a class="bot" href="javascript:enviaForm('actualizaDatosAseguradoWS')">Actualizar</a>
		</div>
		
		
	</div>

</form:form>