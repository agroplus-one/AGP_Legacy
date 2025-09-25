$(document).ready(
			function() {
				//var aaSPol = document.getElementById("consulta");
				//aaSPol.href = "seleccionPoliza.html?rand=" + UTIL.getRand();
				
				if ($('#muestraBotonDescuentos').val() == "true") {
					$('#btnDescuentos').show();
				}
				if ($('#muestraBotonRecargos').val() == "true") {
					$('#btnRecargos').show();
				}
				//$('#btnDescuentos').show();
				//$('#btnRecargos').show();
				
			});