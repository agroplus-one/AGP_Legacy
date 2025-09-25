<script type="text/javascript">

    function aceptar_onClick(){
        UTIL.cleanErrors("panelErroresModalWindow");
        if(isDataOk()){
             $('#method').val('doBaja');
             
             var result = "";
             $(':checkbox:checked').each(
                 function(i){      
                     $('#tipoBajaParcela').val($(this).val()); 
                     
             });
             
             alert($('#tipoBajaParcela').val());
		     $('#main').submit();
        }
        else{
            alert("Debe seleccionar una opción.");
            // document.getElementById("panelErroresModalWindow").style.display = "block";
        }  
    }
    
    function isDataOk(){
        if($("input[@name='tipoBaja']:checked").val())
            return true;
        else 
            return false;
    }
    
</script>

			
<div id="window" class="window"  style="cursor:pointer;cursor:hand;display:none;height:100px;
    width:400px;top:200px;left:500px;background-color:white;border:1px solid black;border-color:black;
    position:absolute;z-index:1006">
    
    
    <div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="left">
						<table cellspacing="2" cellpadding="0" border="0">
							<tbody>
								<tr>
								    <td><a class="bot" href="javascript:aceptar_onClick();">Aceptar</a></td>
									<td><a class="bot" href="javascript:UTIL.closeModalWindow();">Cerrar</a></td>
								</tr>
							</tbody>
						</table>
					</td>
				</tr>
		</table>
	</div>
    
    
	<div class="conten" style="padding:3px;width:97%;text-align:center;">
	        <p class="titulopag">Motivo de la baja</p>
	        <form id="main2" name="form_replicar" method="POST">
            <%@ include file="/jsp/common/static/panelErroresModalWindow.jsp"%>
                 <div class="panel1 isrt" style="padding-left:100px;border:0px;margin-right:auto;">
                     <fieldset>
                         <label style="width:130">Baja por no siembra.</label>
                         <input id="rd_NS" type="radio" name="tipoBaja" value="NS">
                     </fieldset>
                     <fieldset>
                         <label style="width:130">Baja por no nascencia.</label>
                         <input id="rd_NN" type="radio" name="tipoBaja" value="NN">
                     </fieldset>
                 </div> 
             </form>
     </div>
</div>