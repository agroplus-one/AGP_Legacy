
<div id="camposIncremento" style="color:#333333;font-size:11px;font-family: verdana;font-weight:bold">
		
	    <div style="float:right"><a class="bot" id="btnIncrementar"  href="javascript:incrementarDesdePopup();">Incrementar</a></div>

		<div id="incrementoKilosHa" style="float:right; width:240px;">
		     <span>Kilos totales por Ha</span>
		     <input type="text" id="txt_incrKilosHa" size="10" class="dato" readonly="readonly"/>
		     <input type="radio" name="incr" value="kha" onchange="onchange_incr('kha')"  />
		</div>
		
	    <div id="incrementoHa" style="float:right; width:160px;">
	        <span>Kg/Ha</span>
	        <input type="text" id="txt_incrHa" size="10" class="dato"/>
	        <input type="radio" name="incr" value="ha" checked onchange="onchange_incr('ha')"  />
	    </div>
	    
	    <div id="incrementoPa" style="float:right; width:160px;">
	        <span>Kg/Pa</span>
	        <input type="text" id="txt_incrPa" size="10" class="dato" readonly="readonly"/>
	        <input type="radio" name="incr" value="pa" onchange="onchange_incr('pa')"  />
	    </div>
	
</div>


