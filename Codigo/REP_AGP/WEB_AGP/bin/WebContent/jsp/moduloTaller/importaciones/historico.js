// historico Object
//


var historico = {
    indexRowSelect:"-1",
    
    init:function(){    	
    },
    
    cleanGrid:function(){
		var combos = ["sl_planes", "sl_linea", "sl_tipo", "sl_estado"];
        UTIL.cleanCombos(combos);
        var fields = ["fechaDesde", "fechaHasta"];
        UTIL.cleanTextboxs(fields);
    },
      
       
    consultaDetalleHistorico:function(idHistorico)
    {
    	document.forms.main.operacion.value ='detalleTablas';
    	document.forms.main.seleccionado.value = idHistorico;
    	document.forms.main.submit();
    },
    consultaHist:function()
	{
		var continuar = true;
		
		if((document.getElementById('fechaIni').value!='') && (document.getElementById('fechaFin').value!='')){
			if (UTIL.fechaMayorOIgualQue(document.getElementById('fechaFin'), document.getElementById('fechaIni')) == false ){
			  continuar = false;
			} 
				
		}
		
		if(continuar == true){
		   document.forms.main.operacion.value = "consulta";		
		   //Parámetro para indicar si se debe limpiar el filtro
		   document.forms.main.nuevo.value='1';
		   document.forms.main.submit();
		}else{
			  alert("'Fecha desde' debe ser mayor o igual que 'Fecha hasta'");
		    }		
		
	}

}