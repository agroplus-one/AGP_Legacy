<script type="text/javascript">
	function mostrarCombos(){
		document.getElementById("sl_planes").style.display = "";
		document.getElementById("sl_lineas").style.display = "";
		document.getElementById("sl_pantallas").style.display = "";
	}
		$(document).ready(function(){        		
       		$('#sl_planesOrigen').change(function() {				
					$("#sl_lineasOrigen").empty();						
					cargarSelecLineaOrigen();					
			}); 
			$('#sl_planesDestino').change(function() {				
					$("#sl_lineasDestino").empty();						
					cargarSelecLineaDestino();					
			}); 	    		
    	});    	   
    
    	function cargarSelecLineaOrigen(idLinea){
    		var idPlan = $("#sl_planesOrigen").val();

				if(idPlan != ''){
					var cadenaData = "operacion=ajax_getLineas&idPlan="+idPlan;
					if(typeof idLinea !== "undefined"){
						cadenaData = cadenaData + "&codLinea="+idLinea;
					}

					$.ajax({
				            url: "pantallasConfigurables.html",
				            data: cadenaData,
				            dataType: "json",
				            success: function(datos){
								$("#sl_lineasOrigen").removeOption(/./);
								$("#sl_lineasOrigen").addOption("","-- Seleccione una opción --");
								var indice = "";
								$.each(datos, function(index, value) { 
				 					$("#sl_lineasOrigen").addOption(value.value, value.nodeText);
				 					if(value.indiceSeleccionado!==undefined && value.indiceSeleccionado!=""){
				 						indice = value.indiceSeleccionado;
				 					}
								});

								$("#sl_lineasOrigen").selectOptions(String(indice));
								
				            },
				            beforeSend: function(){
			            		$("#ajaxLoading_lineasOrigen").show();
            				},
            				complete: function(){
            					$("#ajaxLoading_lineasOrigen").hide();            					
            				},
				            type: "POST"
						});
			
				}
    	}
    	function cargarSelecLineaDestino(idLinea){
    		var idPlan = $("#sl_planesDestino").val();
				
				if(idPlan != ''){
					$.ajax({
				            url: "pantallasConfigurables.html",
				            data: "operacion=ajax_getLineas&idPlan="+idPlan,
				            dataType: "json",
				            success: function(datos){
								$("#sl_lineasDestino").removeOption(/./);
								$("#sl_lineasDestino").addOption("","-- Seleccione una opción --");
								$.each(datos, function(index, value) { 
				 					$("#sl_lineasDestino").addOption(value.value, value.nodeText);
								});
								
								$("#sl_lineasDestino").selectOptions(idLinea == undefined ? "" : String(idLinea));
								
				            },
				            beforeSend: function(){
			            		$("#ajaxLoading_lineasDestino").show();
            				},
            				complete: function(){
            					$("#ajaxLoading_lineasDestino").hide();            					
            				},
				            type: "POST"
						});
			
				}
    	}
	
</script>			
<div id="window" class="window"  style="cursor:pointer;cursor:hand;display:none;height:200px;
    width:750px;top:150px;left:150px;background-color:white;border:1px solid black;border-color:black;
    position:absolute;z-index:1006">
    
    <div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="left">
						<table cellspacing="2" cellpadding="0" border="0">
							<tbody>
								<tr>
								    <td><a class="bot" href="javascript:replicar.replicar_onClick();">Replicar</a></td>
								    <td><a class="bot" href="javascript:replicar.clean();">Limpiar</a></td>
									<td><a class="bot" href="javascript:mostrarCombos();UTIL.closeModalWindow();">Cerrar</a></td>
									<td>
									    <div id="comprobando" name="comprobandoPlanLinea" style="display:none;margin-left:70px;margin-top:3px;background-color:#F79494;font-size:11px;width:276px;padding:2px;color:black">
			                                Comprobando plan/linea, espere por favor. <img src="jsp/img/ajax_loading2.gif" />
		                                </div>
		                            </td>
								</tr>
							</tbody>
						</table>
					</td>
				</tr>
		</table>
	</div>
	
	
	
	<div class="conten" style="padding:3px;width:97%;text-align:center;">
	        <p class="titulopag">Replicar Plan/L&iacutenea</p>
		    <form id="main2" name="form_replicar" method="POST">
				<input type="hidden" name="operacion" id="operacion">
				<input type="hidden" name="ROW" id="ROW" value="">
				<input type="hidden" name="hidden_idLinea" id="hidden_idLinea">
                <input type="hidden" name="PAGINA" id="PAGINA" value="pantallasConfigurables.jsp">
 
                 <%@ include file="/jsp/common/static/panelErroresModalWindow.jsp"%>
		        
		        <!-- PANEL 1 -->
		        <div class="panel1 isrt" style="padding:0px;border:0px">
		            <fieldset style="border:1px solid #e5e5e5;padding:12px">
		            <legend><b>Origen</b></legend>
				    <fieldset>
				        <span class="literal">Plan</span>
                        <select id="sl_planesOrigen" name="sl_planesOrigen" class="dato" >
				                   <option value="">-- Seleccione una opción --</option>
					        		<c:forEach items="${listPlanes }" var="plan">
					        			<option value="${plan}">${plan }</option>
					        		</c:forEach>
				        </select>
				    </fieldset>
				    
					<fieldset>
					    <span class="literal">L&iacutenea</span>
					    <select id="sl_lineasOrigen" name="sl_lineasOrigen" style="width:350px" class="dato">
					        <option value=""></option>
					    </select>
					    <img id="ajaxLoading_lineasOrigen" src="jsp/img/ajax-loading.gif" width="16px" style="display:none" height="11px"/>  
					</fieldset>
					</fieldset>
				</div>
				<!-- PANEL 2 -->
				<div class="panel1 isrt" style="padding:0px;border:0px">
				    <fieldset style="border:1px solid #e5e5e5;padding:12px">
		            <legend><b>Destino</b></legend>
				    <fieldset>
				        <span class="literal">Plan</span>
				        <select id="sl_planesDestino" name="sl_planesDestino" class="dato">
				           <option value="">-- Seleccione una opción --</option>
					        		<c:forEach items="${listPlanes }" var="plan">
					        			<option value="${plan}">${plan }</option>
					        		</c:forEach>
				        </select>
				    </fieldset>
				    
					<fieldset>
					    <span class="literal">L&iacutenea</span>
					    <select id="sl_lineasDestino" name="sl_lineasDestino" style="width:350px" class="dato">
					        <option value=""></option>
					    </select>
					    <img id="ajaxLoading_lineasDestino" src="jsp/img/ajax-loading.gif" width="16px" style="cursor:hand;cursor:pointer;display:none" height="11px"/>   
					</fieldset>
					</fieldset>
				</div>
		    </form>
     </div>
</div>