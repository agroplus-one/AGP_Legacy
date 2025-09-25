<script type="text/javascript">
	function mostrarAviso(aviso){
		document.getElementById('panelAlertasLocal').style.display="";
		document.getElementById('panelAlertasLocal').className = "alerta";
		document.getElementById('validacion').value = aviso; 
	}
	
	function ocultarAviso(){
		document.getElementById('panelAlertasLocal').style.display="none";
		document.getElementById('validacion').value = ""; 
	}
				
</script>

<style type="text/css">
#main div.alerta {
	width:1000px;
	height:20px;
	color:black;
	border:1px solid #DD3C10;
	font-size:12px;
	font-style:italic;
	font-weight:bold;
	line-height:20px;
	background-color:#FFEBE8;
	}
</style>

<div id="panelAlertasLocal" style="display: none;text-align:left;">
	<input type="text" style="width: 500px" id="validacion" />
</div>