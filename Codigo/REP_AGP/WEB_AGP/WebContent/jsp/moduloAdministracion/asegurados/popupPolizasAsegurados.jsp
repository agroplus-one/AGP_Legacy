<!-- TMR 11/12/2014 -->
<!-- popupAseguradosSW.jsp (show in datosAsegurados.jsp) -->
<script type="text/javascript">
$(document).ready(function(){
	$("#divAseguradoPol").draggable();
	
});


function cerrarPopUp(){
	
	$('#divAseguradoPol').hide();
	$('#overlay').hide();
}
function actualizaPolizas(){
	
	$('#divAseguradoPol').hide();
	$('#overlay').hide();
	
	if ($('#formulario').val()== "asegurados"){
		$('#method').val("doActualizarSubvsAseg");
	}else{
		$('#method').val("actualizaIbanPolizasAseg");
	}
	// le quitamos el disabled para que envie el valor del combo
	$("#tipoIdentificacion").attr('disabled', false);
	$('#main').submit();
}

</script>


	<!--  popup AseguradoSW-->
	<div id="divAseguradoPol" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em;
					width: 60%; display: none; top: 270px; left: 20%; height:10%;
                    position: absolute; z-index: 1003;border:1px solid #A4A4A4;background-color:#F2F2F2">
		<!--  header popup -->
		<div id="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px">
			<div style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">${tituloListadoPolizasAseg}</div>
			<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
				<span onclick="cerrarPopUp()">x</span>
			</a>
		</div>

		<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelInformacion" class="panelInformacion">
				
				<div >
					<display:table requestURI="datoAsegurado.html" 
								   id="poliza" class="LISTA" summary="" sort="list" 
						           pagesize="${numReg}" name="${polizasAsegurado}" export="false" 
						           decorator="com.rsi.agp.core.decorators.ModelTableDecoratorPolizasAsegurados" 
						           style="width:95%" >
						
						<display:column class="literal" headerClass="cblistaImg" title="Nif Aseg." property="nifAseg" sortable="false"/>
						<display:column class="literal" headerClass="cblistaImg" title="Colectivo" property="colectivo" sortable="false" />
						<display:column class="literal" headerClass="cblistaImg" title="Plan" property="plan" sortable="false" />
						<display:column class="literal" headerClass="cblistaImg" title="Linea" property="linea" sortable="fasle" />		
												
						
					</display:table>		
			</div>

			</div>
		</div>
		<!-- Botones popup --> 
	    <div style="margin-top:15px;margin-bottom: 5px;" align="center">
			<a class="bot" href="javascript:cerrarPopUp()">Cancelar</a>
			<a class="bot" href="javascript:actualizaPolizas()">Actualizar</a>
		</div>
		
		
	</div>

