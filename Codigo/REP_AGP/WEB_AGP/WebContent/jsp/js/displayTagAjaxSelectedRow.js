// script que a�ade al displaytag la capacidad de hacer paginaci�n por ajax

$(function(){
	//$("#grid").displayTagAjax();
				
	}).ajaxSend(function(){
		
	}).ajaxComplete(function(){
		// para todas
	   	generales.fijarFila();
	   		
	    //DAA 03/07/2012 solo para listadoParcelas.jsp
	    if(document.getElementById("listaParcelas_cm") != null){
	    	
		    changeColorRow();
			check_checks($('#idsRowsChecked').val());
			$('#sel').text(numero_check_seleccionados2());
		    if($('#selectedRow').val() == "true"){
				    setSelectedRow($('#idRowSelected').val()); 
			}
			
			if($('#marcarTodosChecks').val()=="si"){
				$('#selTodos').attr('checked',true);
			}
			if($('#marcarTodosChecks').val()=="no"){
				$('#selTodos').attr('checked',false);
			}
        }
        //DAA 17/05/2012 para cambioPolizaDefinitiva.jsp
        if(document.getElementById("listaResultados") != null){	
	    	
	    	check_checks($('#idsRowsChecked').val());
	    	if($('#checkTodo').val()=="true"){
	    		marcar_todos();
	    	}
	    	if($('#checkTodo').val()=="false"){
	    		desmarcar_todos();
	    	}	
	    }

	    //DAA 18/06/2012 para parcelasSiniestro.jsp
		if(document.getElementById("listParcelasSiniestradas") != null){
			check_checks($('#idsRowsChecked').val());
			if($('#marcaTodo').val()=="true"){
				$('#checkTodos').attr('checked', true);
			}
			else{
				$('#checkTodos').attr('checked', false);
			}
		}
		
		 //AMG 26/11/2014 para parametrosComisionesCultivos.jsp
        if(document.getElementById("Pcomisiones") != null){
	    	check_checks($('#idsRowsChecked').val());
	    	if($('#checkTodo').val()=="true"){
	    		marcar_todos();
	    	}
	    	if($('#checkTodo').val()=="false"){
	    		desmarcar_todos();
	    	}	
	    }
        
        if(document.getElementById("listaAsegurados") != null){	
        	
        	check_checks($('#idsRowsChecked').val());
        	
        	if($('#checkTodo').val()=="true"){
        		marcar_todos();
        	}
        	if($('#checkTodo').val()=="false"){
        		desmarcar_todos();
        	}	
        }
        
});


