var elecmodulos = 
{ 
	volver:function()
	{
		var frm = document.getElementById("frmlistpolizas");
		//frm.idpoliza.value=document.getElementById("idpoliza").value;
		frm.operacion.value="volver";
		frm.submit();
	},
	
	continuar:function()
	{
		UTIL.cleanErrors("panelErrores");
		var error = "<label name='error' style='color:red;margin-left:5px;font-size:10px'>*</label>";
		var checks = document.getElementsByTagName('input');
		var cadenaActivados="";
		var frm = document.main;
		
		for (var i = 0; i < checks.length; i++)
		{
     		var node = checks[i];
     		if (node.getAttribute('type') == 'checkbox')
     		{
     			if (node.checked)
     			{
     				cadenaActivados+=node.value+",";
     			}
     		}
	 	}
	 	
	 	if (cadenaActivados != ""){
	 		cadenaActivados = cadenaActivados.substr(0, cadenaActivados.lastIndexOf(','));
		 	frm.action.value = "comparativa";
		 	frm.activados.value = cadenaActivados;
		 	frm.submit();
	 	}
	 	else{
	 		document.getElementById("panelErrores").style.display = "block";
	 		document.getElementById("panelErrores").parentNode.innerHTML = document.getElementById("panelErrores").parentNode.innerHTML
		   for (var i = 1; i <= checks.length; i++)
		   document.getElementById('errorModSelected'+i).innerHTML = document.getElementById('errorModSelected'+i).innerHTML + error;
	 	}
	},
	
	volverComparativa:function()
	{
		var frm = document.getElementById("frmeleccomparativa");
		frm.action.value="";
		frm.submit();
	},
	
	continuarComparativa:function()
	{
		UTIL.cleanErrors("panelErrores");
 		var error = "<label name='error' style='color:red;margin-left:5px;font-size:10px'>*</label>";
		var checks = document.getElementsByTagName('input');
		var cadenaActivados="";
		var frm = document.getElementById("frmeleccomparativa");
		var checkeado = false;
  		
		for (var i = 0; i < checks.length; i++)
		{
     		var node = checks[i];
     		if (node.getAttribute('type') == 'checkbox')
     		{
     			if (node.checked)
     			{
     				checkeado =true;
     				cadenaActivados+=node.value;
     			}
     		}
	 	}

	 	document.getElementById("action").value="seleccionComp";
	 	frm.seleccionados.value = cadenaActivados;
	 	
	 	if (checkeado){
	 		frm.submit();	//solo hacemos submit en caso de que los checks no esten vacios		
	 	}else{
	 		document.getElementById("panelErrores").style.display = "block";
	 		document.getElementById("panelErrores").parentNode.innerHTML = document.getElementById("panelErrores").parentNode.innerHTML;
			for (var i = 1; i <= checks.length; i++){
				document.getElementById('errorCompSelected'+i).innerHTML = document.getElementById('errorCompSelected'+i).innerHTML + error;	
			}
	 	}
	 }

}