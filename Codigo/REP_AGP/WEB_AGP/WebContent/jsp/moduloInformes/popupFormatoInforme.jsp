<!--                                                       						 -->
<!-- popupFormatoInforme.jsp (show in mtoInformes.jsp,mtoDatosInforme.jsp,mtoCondicionCampos.jsp,mtoClasificacionRuptura.jsp y generacioniInformes.jsp) -->
<!--                                                       						 -->

<script>

            
function limpiarAlertasFormatoInf(){
	$('#campoObligatorio_numDuplicar').hide();
	$('#duplicar_popup_error').hide();
	$('#duplicarMax_popup_error').hide();
}

function cerrarPopUpFormatoInf(){
     $('#formatoInf_popup').hide();
     $('#overlay').hide();
}

function elegirFormato(id){
	$('#overlay').show();
    $('#formatoInf_popup').fadeIn('normal');
    
    var frmGen = document.getElementById('formatoInformeForm');
	frmGen.idInforme.value = id;
}

function habilitaOrientacion(){
	if ($('#formato').val() == $('#codFormatoPDF').val()){
		obj = document.getElementById('divOrientacion');
		for (i=0; ele = obj.getElementsByTagName('select')[i]; i++){
			ele.disabled = false;
		}
		var inputText = document.getElementById('orientacion');
	}else if ($('#formato').val() != $('#codFormatoPDF').val()){
		obj = document.getElementById('divOrientacion');
		$('#orientacion').val($('#codOrientacionV').val());
		for (i=0; ele = obj.getElementsByTagName('select')[i]; i++){
			ele.disabled = true;
		}
	} else{
		obj = document.getElementById('divOrientacion');
		$('#orientacion').val('');
		obj = document.getElementById('divOrientacion');
		for (i=0; ele = obj.getElementsByTagName('select')[i]; i++){
			ele.disabled = true;
		}
	}
}

//DAA 20/02/2013
function doGenerar(){
	$('#formatoInf_popup').fadeOut('normal');
    $('#overlay').hide();
	limpiaAlertas();
    var frmGen = document.getElementById('formatoInformeForm');
    $('#formatoInformeForm').attr('target', '_blank');
 	$('#formatoInformeForm').submit();
}

//DAA 20/02/2013
function generar(){
	
	var frmGen = document.getElementById('formatoInformeForm');
	var id = frmGen.idInforme.value;
	var formato = frmGen.formato.value;
	
	$.ajax({
		url:          "generacionInforme.run",
		data:         "method=verificarNumRegistros&idInforme=" + id + "&formato=" + formato,
		async:        true,
		beforeSend: function(objeto){
                document.getElementById("ajaxLoading_formato").style.display = 'block';
                $('#btnGenerar').attr('disabled','disabled');
                $('#btnCancell').attr('disabled','disabled');
        },
        complete: function(objeto, exito){
            	document.getElementById("ajaxLoading_formato").style.display = 'none';
            	$('#btnGenerar').attr('disabled','');
            	$('#btnCancell').attr('disabled','');

        },
		contentType:  "application/x-www-form-urlencoded",
		dataType:     "json",
		global:       false,
		ifModified:   false,
		processData:  true,
		error: function(objeto, quepaso, otroobj){
			alert("Error al comprobar los datos del informe: " + quepaso);
		},
		success: function(resultado){
			$('#consultaYaGenerada').val('true');
			switch(parseInt(resultado.datos)){
				case 0:
					doGenerar();
					break;
				case 1: 
				  	if(confirm(resultado.mensaje)){
				  		doGenerar();
				  	}
				 	break;
				case 2: 
					alert(resultado.mensaje);
				  	break;
				default:
				  	break;
			}
		},
		type: "GET"
	});
}

</script>

<!--  popup Formato Informe -->

<div id="formatoInf_popup" class="wrapper_popup">
    <div class="header-popup">
        <div class="title_popup">Elección del Formato y Orientación del Informe</div>
        <a class="close_botton_popup"><span onclick="cerrarPopUpFormatoInf()">x</span></a>
    </div>
	<div class="content_popup">
	    <form id="formatoInformeForm" name="formatoInformeForm" action="generacionInforme.run" method="post">
	    	<input type="hidden" name="consultaYaGenerada" id="consultaYaGenerada"/>
	    	<input type="hidden" name="idInforme" id="idInforme"/>
			<input type="hidden" name="method" id="method" value ="doGenerar" />
			<input type="hidden" name="lstFormatosInforme" id="lstFormatosInforme" value="${lstFormatosInforme}"/>
			<input type="hidden" name="lstOrientacionesInforme" id="lstOrientacionesInforme" value="${lstOrientacionesInforme}"/>
			<input type="hidden" name="codFormatoPDF" id="codFormatoPDF" value="${codFormatoPDF}"/>
			<input type="hidden" name="codOrientacionV" id="codOrientacionV" value="${codOrientacionV}"/>
		    <table width="30%" style="margin:0 auto;">
		    	<tr>
					<td class="literal">Formato</td>
					<td class="literal">
						<select id="formato" name="formato" tabindex="5" class="dato" style="width:60px" onchange="javascript:habilitaOrientacion()">
						  <c:forEach items="${lstFormatosInforme}" var="formato" varStatus="lst">
						  		<option value ="${formato.idFormato}">${formato.nombreFormato}</option>
						  </c:forEach>
						</select>
					</td>
					<td>&nbsp;&nbsp;</td> 
					<td class="literal">Orientaci&oacute;n</td>
					<td class="literal"> 
						<div id="divOrientacion">
							<select id="orientacion" name="orientacion" tabindex="6" class="dato" style="width:105px">
							  	<c:forEach items="${lstOrientacionesInforme}" var="orientacion" varStatus="lst">
					  				<option value ="${orientacion.idFormato}">${orientacion.nombreFormato}</option>
					  			</c:forEach>
							</select>
						</div>
					</td>
				</tr>
		    </table>
	    </form>
		<div style="margin-top:10px;">
			<table style="margin:0 auto;">
				<tr>
					<td><a class="bot" id="btnCancell" href="javascript:cerrarPopUpFormatoInf()">Cancelar</a></td>
					<td><a class="bot" id="btnGenerar" href="javascript:generar()" >Generar Informe</a></td>
					<td><img id="ajaxLoading_formato" src="jsp/img/ajax-loading.gif" width="16px" style="cursor:hand;cursor:pointer;display:none" height="11px" /></td>
				</tr>
			</table>
		</div>
		
	</div>
</div>