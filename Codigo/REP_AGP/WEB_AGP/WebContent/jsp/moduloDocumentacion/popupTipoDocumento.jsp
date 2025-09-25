<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
	<head>
		<form  name="frmAltaModifTipoDoc" action="documentacionAgroseguro.run" method="post" id="frmAltaTipoDoc">
			<input type="hidden" name="idTipoDoc" id="idTipoDoc" value="${idTipoDoc}" >
			
		    <div id="divAltaTipoDoc"
				style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em; width: 35%; display: none; top: 270px; left: 33%; position: absolute; 
				z-index: 1003; border: 1px solid #A4A4A4; background-color: #F2F2F2">             
				<!--  header popup -->
				<div id="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; 
						font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px" >
					<div id="AltaModTipoDocTitulo" style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Alta/Modificacion Tipo Documento</div>
					
					<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; 
						top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
						<span onclick="cerrarPopUpTipoDocumMo()">x</span>
					</a>
				</div>
				
				<!--  body popup -->
				<div class="panelInformacion_content" >
					<div id="panelInformacionTipoDocA" class="panelInformacion" style="width:60%">
					<div id="panelAlertasValidacion_tipoDocA" name="panelAlertasValidacion_tipoDocA" class="errorForm_fp" align="center" style="width:98%; height:25%" text-align="center"></div>
						<table style="width:90%">
							<tr>
							   <table width="90%" height="80%">
								   	<!--  -->
								   	<tr>
										<td class="literal" style="width:60px"  align="left">Descripción</td>
										<td class="literal" style="width:250"  align="rigth">
											<input cssClass="dato" name="descTipoDoc" style=" width:400px" maxlength="255" id="descTipoDoc" tabindex="1" value="${descTipoDoc}" />
										</td>
									</tr>
								</table>
							</tr>
						</table>
					</div>
					<a class="bot" id="btnAplicarAlta" href="javascript:doAltaTipoDocu()">Alta</a>
					<a class="bot" id="btnCancelar" href="javascript:cerrarPopUpTipoDocumAl()">Cancelar</a>
				</div>				
			</div>
		    <div id="divModifTipoDoc"
				style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em; width: 35%; display: none; top: 270px; left: 33%; position: absolute; z-index: 1003; border: 1px solid #A4A4A4; background-color: #F2F2F2">             
				<!--  header popup -->
				<div id="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; 
						font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px" >
					<div id="AltaModTipoDocTitulo" style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Alta/Modificacion Tipo Documento</div>
					
					<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; 
						top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
						<span onclick="cerrarPopUpTipoDocumAl()">x</span>
					</a>
				</div>
				
				<!--  body popup -->
				<div class="panelInformacion_content" >
					<div id="panelInformacionTipoDocM" class="panelInformacion" style="width:60%">
					<div id="panelAlertasValidacion_tipoDocM" name="panelAlertasValidacion_tipoDocM" class="errorForm_fp" align="center" style="width:98%; height:25%" text-align="center"></div>
						<table style="width:90%">
							<tr>
							   <table width="90%" height="80%">
								   	<!--  -->
								   	<tr>
										<td class="literal" style="width:60px"  align="left">Descripción</td>
										<td class="literal" style="width:250"  align="rigth">
											<input cssClass="dato" name="descTipoDocM" style="width:400px" maxlength="50" id="descTipoDocM" tabindex="1" value="${descTipoDocM}" />
										</td>
									</tr>
								</table>
							</tr>
						</table>
					</div>
					<a class="bot" id="btnAplicarModif" href="javascript:doModifTipoDocu()">Modificar</a>
					<a class="bot" id="btnCancelar" href="javascript:cerrarPopUpTipoDocumMo()">Cancelar</a>
				</div>				
			</div>
		</form>
	</head>
</html>	

