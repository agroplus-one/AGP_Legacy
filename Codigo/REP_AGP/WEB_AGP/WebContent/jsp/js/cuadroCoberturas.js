function modulosTaller(idDiv, codmodulo, idtabla, idlinea, ganadoc){
	$.ajax({
		url: "coberturasController.html" ,
		data: "method=doGetModulos&codmodulo="+codmodulo+"&idtabla="+idtabla+"&idlinea="+idlinea+"&ganadoc="+ganadoc,
		async:true,
		dataType: "json",
		success: function(datos){				    					    		
    		$('#'+idDiv).html(datos.modulo);				              				            			            		
		},
		beforeSend: function(){
   			$("#ajaxLoading_"+idDiv).show();
		},
		complete: function(){
			$("#ajaxLoading_"+idDiv).hide();            					
		},				           
		type: "POST"
	});
}

function modulos(idDiv,codmodulo,idpoliza,idtabla){
	$.ajax({
	    url: "coberturasController.html" ,
		data: "method=doGetModulosPoliza&idpoliza="+idpoliza+"&codmodulo="+codmodulo+"&idtabla="+idtabla,
		async:true,
	    dataType: "json",
	    success: function(datos){				    					    		
	        	$('#'+idDiv).html(datos.modulo);				              				            			            		
	    },
	    beforeSend: function(){
           		$("#ajaxLoading_"+idDiv).show();
		},
		complete: function(){
				$("#ajaxLoading_"+idDiv).hide();            					
		},				           
	    type: "POST"
	});
}