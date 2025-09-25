var detallehistorico = {    
    init:function(){    	    	
    },
    volver:function()
    {
    	document.forms.frmDetalleImp.operacion.value ='volver';    	
    	document.forms.frmDetalleImp.submit();
    },
    consultaTabla:function(numtabla)
    {
    	document.forms.frmDetalleImp.action = "tabledata.run";
    	//document.forms.frmDetalleImp.operacion.value = "detalleTabla";
    	document.forms.frmDetalleImp.tabla.value = numtabla;
		document.forms.frmDetalleImp.submit();
    }
}