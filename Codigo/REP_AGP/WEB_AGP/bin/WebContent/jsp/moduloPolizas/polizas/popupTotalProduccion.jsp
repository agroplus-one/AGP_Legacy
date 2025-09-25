<!--                                                     -->
<!-- popupTotalProduccion.jsp (show in listadoparcelas.jsp) -->
<!--                                                     -->


<script><!--

$(document).ready(function(){  
    $(".parcelasRepWindow").draggable();
});
		    
function DameLstTotalProduccion(idPoliza){ 
    $.ajax({
        url: 	"ajaxCommon.html",
        data:   "operacion=ajax_getLstTotalProdParcelas&idPoliza="+idPoliza,
        dataType:     "text",
        
        success: function(datos){
	        $('#dataTotalProd').html(datos);
	        totalProd();
        },
        error: function(objeto, quepaso, otroobj){
            alert("Error al calcular la produccion total: " + quepaso);
        },
        type: "POST"
    });
}
		      	
function totalProd(){
	$('#overlay').show();
	$('#panelTotalProd').show();
}
 
function cerrarPopUpTotalProd(){
    $('#panelTotalProd').hide();
    $('#overlay').hide();
}
		  	 
--></script>

<div id="panelTotalProd" class="parcelasRepWindow"	style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em; width: 500px; top: 165px;">
	<!--  header popup -->
	<div id="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; font-weight: bold; -moz-border-radius: 4px 4px 4px 4px;
																		  background: #525583; height: 15px">
		<div style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">
			Totales Producci&oacute;n y Superficie
		</div>
		<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; top: 50%; width: 19px;
					 font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
		<span onclick="cerrarPopUpTotalProd()">x</span> </a></div>
	
	
	<div id="dataTotalProd">
	</div>
	
	
</div>


