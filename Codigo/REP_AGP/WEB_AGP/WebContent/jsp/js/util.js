
/*
 * Objeto con funciones de utilidad
 */
var UTIL = {
	isDataOk:function(combos,error){
         var result = true;               
		 for(var i = 0; i < combos.length;i++){
	         var sl = document.getElementById(combos[i])
	     	 if(sl.selectedIndex == 0 || sl.options.length == 0){
	    	     sl.parentNode.innerHTML = sl.parentNode.innerHTML + error;
	             result = false;           
	     	 }
	     }
	     return result
	     
     },
     isDataOkForJQuery:function(combos){
         var result = true;               
		 for(var i = 0; i < combos.length;i++){
	         var sl = document.getElementById(combos[i])
	     	 if(sl.selectedIndex == 0 || sl.options.length == 0){
	    	     var labelError = document.createElement("label");
	    	     labelError.name='error';
	    	     labelError.style.color='red';
	    	     labelError.style.marginLeft='5px';
	    	     labelError.style.fontSize='10px';
	    	     labelError.innerHTML='*';
	    	     sl.parentNode.appendChild(labelError);
	             result = false;           
	     	 }
	     }
	     return result
	     
     },     
	cleanErrors:function(panelErrores){
    	document.getElementById(panelErrores).style.display="none";
    	var listLabel = document.getElementsByTagName("LABEL");
    	var listError = [];
    	for(var i=0; i< listLabel.length; i++){
    	    if(listLabel[i].name == "error")
    	    	listError.push(listLabel[i]);
    	}
    	
    	for(var i=0; i< listError.length; i++){
    		padre = listError[i].parentNode;
    		padre.removeChild(listError[i]);
    	}
    },
    openModalWindow:function(){
        var overlay = document.getElementById('overlay');
        var popup   = document.getElementById('window');

        if(overlay && popup){
                overlay.style.display = 'block';
                popup.style.display  = 'block';
	    }
    },
    
    closeModalWindow:function(){
        var overlay = document.getElementById('overlay');
        var popup   = document.getElementById('window');
        
        if(overlay && popup){
                overlay.style.display = 'none';
                popup.style.display  = 'none';
	    }
    },
	/**
 	* No Usar: funcion que limpia los campos cuyos id's estan en el parametro. 
	*/
	cleanFields:function(fields){
	    var el = document.getElementById("sl_planes");
	    if(el){
	        if (el.type=="text")
	            el.text = "";
	    }
	},
	/**
 	* Funcion que limpia los combos cuyos id's estan en el array parametro. 
	*/
	cleanCombos:function(combos){
	    if(combos){
	        for(var i = 0; i < combos.length;i++){
	              var el = document.getElementById(combos[i]);
	              if(el){
	                  if (el.type=="select-one" || el.type=="select-multiple")
	                      el.options.selectedIndex = 0;
	              }
	        }  
	     }
    },
    /**
 	* Funcion que limpia todos los combos del form pasado como parametro. 
	*/
    cleanAllCombos:function(form){
       var combos = new Array();
       var frm = document.main; 
       var count = 0;
    
	   if (frm){
           for(i=0; i<frm.elements.length; i++){
	           if(frm.elements[i].type == "select-one" || frm.elements[i].type == "select-multiple"){
			       combos[count]= frm.elements[i].id;
			       count++;
			   }
	       }
	   }
    },
    /**
 	* Funcion que limpia los textbox cuyos id's estan en el parametro.
	*/
    cleanTextboxs:function(textboxs){
       if(textboxs){
	        for(var i = 0; i < textboxs.length;i++){
	              var el = document.getElementById(textboxs[i]);
	              if(el){
	                  if (el.type=="text"){
	                      el.value = "";
	                   }
	              }
	        }  
	   }
    },
    /**
 	* Funcion que muestra un panel emergente. 
	*/
    viewPanel:function(position){
       // TODO
    },
    /**
 	* No usar: Funcion que muestra los datos de una fila en las elementos HTML. 
	*/
    viewDataRow:function(table,row,htmlTags){
        alert("utils");
        var index = row.parentNode.parentNode.rowIndex;
        var strTable = table.toString();
        var idTable = document.getElementById(strTable);
        
        if(idTable){
            var numCells = idTable.rows.length;
            var r = idTable.rows[index];
            
            for(var i = 0;i < numCells;i++){
                var el = document.getElementById(htmlTags[i]);
                var cellValue = r.cells[i].firstChild.nodeValue;

                if (el.type=="text"){
                    el.value = cellValue;
                }
                if(el.type == "select-one" || el.type == "select-multiple"){
                    for(var e = 0;e < el.length;e++){
                        if(el.options[e].text.toString() == cellValue){
                            el.selectedIndex = e;
                        }
                    }
                }
            }//for   
        }
    },  
    /**
 	* Funcion que elimina una fila de una tabla. 
	*/
    deleteRow:function(table,row){
       var strTable = table.toString();
       var idTable = document.getElementById(strTable);
       if(idTable){
            idTable.deleteRow(row);
       }
    },
    /**
 	* Funcion que elimina todos los options de un select. y le anhade uno en blanco
	*/
    cleanSelect:function(idSelect){
    	if(idSelect){
            idSelect.options.length= 0; 
    	}	
    },
    cleanSelectAddOneOptions:function(idSelect){
    	if(idSelect){
            idSelect.options.length= 0; 
    	    var opt = document.createElement('OPTION');
            opt.innerHTML = "";
            opt.value = "";
            idSelect.appendChild(opt);
    	}	
    },
    cleanSelectAndAddEmptyOption:function(idSelect){
    	if(idSelect){
            idSelect.options.length= 0; 
    	    var opt = document.createElement('OPTION');
            opt.innerHTML = "";
            opt.value = "";
            idSelect.appendChild(opt);
    	}	
    },
    getValueOfSelect:function(idSelect){
    	value = "none";
    	if(idSelect){
    		value = idSelect.options[idSelect.selectedIndex ].value; 
    	}
    	return value;
    },
	fechaMayorOIgualQue:function (fec0, fec1){ 
	    var bRes = false;
	    var sDia0 = fec0.value.substr(0, 2); 
	    var sMes0 = fec0.value.substr(3, 2); 
	    var sAno0 = fec0.value.substr(6, 4); 
	    var sDia1 = fec1.value.substr(0, 2); 
	    var sMes1 = fec1.value.substr(3, 2); 
	    var sAno1 = fec1.value.substr(6, 4); 
	    
	    //si el anho de la primera fecha es mayor que el de la segunda
	    if (sAno0 > sAno1){
	    	 bRes = true;
	    }	  
	    else {
		   if (sAno0 == sAno1){ 
              //si son del mismo anho y el mes de la primera es mayor que el de la segunda
              if (sMes0 > sMes1){
              	 bRes = true; 
              }
		      else { 
		   	   if (sMes0 == sMes1){ 
		          //si el mes es el mismo y el dia de la primera es mayor que el de la segunda
		          if (sDia0 >= sDia1){
		        	bRes = true;
		          }
		   	   } 	 
		     } 
		   } 
	    } 
	    return bRes; 
    },
    fechaMayorQue:function (fec0, fec1){ 
	    var bRes = false;
	    var sDia0 = fec0.value.substr(0, 2); 
	    var sMes0 = fec0.value.substr(3, 2); 
	    var sAno0 = fec0.value.substr(6, 4); 
	    var sDia1 = fec1.value.substr(0, 2); 
	    var sMes1 = fec1.value.substr(3, 2); 
	    var sAno1 = fec1.value.substr(6, 4); 
	    
	    //si el anho de la primera fecha es mayor que el de la segunda
	    if (sAno0 > sAno1){
	    	 bRes = true;
	    }	  
	    else {
		   if (sAno0 == sAno1){ 
              //si son del mismo anho y el mes de la primera es mayor que el de la segunda
              if (sMes0 > sMes1){
              	 bRes = true; 
              }
		      else { 
		   	   if (sMes0 == sMes1){ 
		          //si el mes es el mismo y el dia de la primera es mayor que el de la segunda
		          if (sDia0 > sDia1){
		        	bRes = true;
		          }
		   	   } 	 
		     } 
		   } 
	    } 
	    return bRes; 
    },
   
    //funcion que compara una fecha recibida en formato dd/mm/yyyy con la fecha actual
    fechaMayorOIgualQueFechaActual:function (fecha){
        var fechaActual = new Date();
        //se crea un objeto fecha a partir del string recibido (meses 0-11)
        var mes =  fecha.substr(3,2);
        if(mes.substr(0,1)=='0'){
        	mes = mes.substr(1,1);
        }
        var fechaRecibida = new Date(fecha.substr(6,4),(parseInt(mes)-1),fecha.substr(0,2));
        //var result = false;     
        //si el dia del anho (para que no cuente las hh/min/seg)
        /*if(fechaRecibida.getDayOfYear() < fechaActual.getDayOfYear()){
      	      result = false;
          }
          else{
      	      result = true;
         }*/
        if(fechaRecibida.getYear()<fechaActual.getYear())
      	    return false;
        if(fechaRecibida.getYear()>fechaActual.getYear())
      	    return true;
        if(fechaRecibida.getMonth()<fechaActual.getMonth())
      	    return false;		
        if(fechaRecibida.getMonth()>fechaActual.getMonth())
      	    return true;		
        if(fechaRecibida.getDate()<fechaActual.getDate())
      	    return false;		
        return true;		
    },
    //TMR 23/05/2012
    //funcion que comprueba que una fecha recibida en formato dd/mm/yyyy sea menor o igual a la fecha actual
    fechaMenorOIgualQueFechaActual:function (fecha){
    	var fechaActual = new Date();
        //se crea un objeto fecha a partir del string recibido (meses 0-11)
        var mes =  fecha.substr(3,2);
        if(mes.substr(0,1)=='0'){
        	mes = mes.substr(1,1);
        }
        var fechaRecibida = new Date(fecha.substr(6,4),(parseInt(mes)-1),fecha.substr(0,2));
        
        if(fechaRecibida.getYear()<fechaActual.getYear())
      	    return true;
        else if(fechaRecibida.getYear()>fechaActual.getYear())
        	return false;
        else
        	if(fechaRecibida.getMonth()<fechaActual.getMonth())
        		 return true;
        	else if(fechaRecibida.getMonth()>fechaActual.getMonth())
        		 return false;
        	else
        		 if(fechaRecibida.getMonth()<fechaActual.getMonth())
      	    		return true;		
        		 else if(fechaRecibida.getDate()>fechaActual.getDate())
      	    		return false;
      	    	 else
      	    		return true;

    },
    //TMR 23/05/2012
    //funcion que comprueba que una fecha no sea anterior a 6 meses.
    //Para calcular la diferencia en meses, calculamos el numero de meses desde el momento 0.
    //Para ello multiplicamos la fecha actual por 12 y le sumamos el mes en el que estamos. 
	//Esta operacion da dos cifras resultantes. Si sustraemos la segunda de la primera, 
	//obtenemos el numero de meses que hay entre las dos.
	//Hay que tener en cuenta que el dia del mes de la segunda fecha sea menor que el dia del mes de la primera. 
	//Eso querria decir que aun no se ha cumplido el mes, y por tanto hay que restar 1.
    fechaAnterior6Meses:function(fecha){
    	
    	var fechaActual = new Date();
    	
    	var mes =  fecha.substr(3,2);
    	
        if(mes.substr(0,1)=='0'){
        	mes = mes.substr(1,1);
        }
    	
    	var numMeses = (parseInt(fechaActual.getYear()*12) +  parseInt(fechaActual.getMonth())) -
					   (parseInt(fecha.substr(6,4))*12 +(parseInt(mes)-1)); 
    	
    	if (fechaActual.getDate()<= fecha.substr(0,2)){
    		numMeses = numMeses-1;
    	}
    	
    	if (numMeses>5)
    		return false;
    	else
    		return true;
    		
  },
    marcaCombo:function (combo, valor){
		for (var i=0; i<combo.options.length; i++){
			if (combo.options[i].value == valor){
				combo.options[i].selected = true;
				break;
			}					
		}
	},
	/**
	 * Funcion que anhade a la URL un parametro con un numero aleatorio
	 * para evitar el cacheo de la app por parte del navegador.
	 */
    antiCacheRand:function(aurl){
    	var newUrl = "";
    	
		if(aurl.indexOf("?")>=0)
		    newUrl = aurl + "&" + "rand=" + encodeURI(parseInt(Math.random()*99999999) + "_" + (new Date).getTime());
		else
			newUrl = aurl + "?" + "rand=" + encodeURI(parseInt(Math.random()*99999999) + "_" + (new Date).getTime());

		return newUrl;
    },
    /**
	 * Funcion que redirige a una url pasada como parametro
	 */
	go_to:function(url){
		location.href = url;
	},
	/*
	 * Genera un cadena aleatoria con un formato especifico
	 */
	getRand:function(){
		return encodeURI(parseInt(Math.random()*99999999) + "_" + (new Date).getTime());
	},
	/*
	 * redirect to url + random string --> no usada
	 */
	randomRedirect:function(url){
		var newUrl = this.antiCacheRand(url);
		window.location.replace(newUrl);
	},
	subStrEntidad:function(){
		var entidadMediadora = $('#entidad').val();
		// caso especial para entidades externas
		if (entidadMediadora.substr(1,3) == '000'){	
			
			$('#entidadSubstr').val(entidadMediadora);
		}else if(entidadMediadora.length == 4){
			entidadMediadora = '%' + $('#entidad').val().substr(1);
			$('#entidadSubstr').val(entidadMediadora);
			
		}
		else{
			$('#entidadSubstr').val('');
		}
		
	},
	subStrEntidadCM:function(){
		var entidadMediadora = $('#entidad_cm').val();
		if(entidadMediadora.length == 4){
			entidadMediadora = '%' + $('#entidad_cm').val().substr(1);
			$('#entidadSubstr_cm').val(entidadMediadora);
		}
		else{
			$('#entidadSubstr_cm').val('');
		}
	},
	cambiarPctMediador:function(){
		if($('#operacion').val() != null){
			if($('#operacion').val() == "cambioPctMediador"){				
				var entMediadora = $('#entmediadora').val();
				$('#txt_porcentajeMediador').val('');
				$('#txt_porcentajeMediador').attr("readonly", false);
				if (entMediadora.length == 4){
					if (entMediadora.charAt(0) == '8'){
						$('#txt_porcentajeMediador').val('100');
						$('#txt_porcentajeMediador').attr("readonly", true);
					} 
				}
			}
		}
	}  
}



/*
 * Objeto ajax
 */
var AJAX_RSI = {
    getLineas:function(url,sl_planes,sl_lineas,loading_lineas){
    	var plan = UTIL.getValueOfSelect(document.getElementById(sl_planes));
        $.ajax({
            url: url,
            data: "operacion=ajax_getLineas&Plan=" + plan,
            async:true,
            beforeSend: function(objeto){
            	var img = document.getElementById(loading_lineas)
            	if(img)
                    document.getElementById(loading_lineas).style.display = 'block';
            },
            complete: function(objeto, exito){
                var img = document.getElementById(loading_lineas)
            	if(img)
                    document.getElementById(loading_lineas).style.display = 'none';
            },
            contentType: "application/x-www-form-urlencoded",
            dataType: "text",
            error: function(objeto, quepaso, otroobj){
                alert("Error: " + quepaso);
            },
            global: true,
            ifModified: false,
            processData:true,
            success: function(datos){
                var list = eval(datos);
                var sl = document.getElementById(sl_lineas);
                
                if(sl && list){
                    for(var i = 0; i < list.length; i++){
                        var opt = document.createElement('OPTION');
                        opt.innerHTML = list[i].nodeText;
                        opt.value = list[i].value;
                        sl.appendChild(opt);
                    }
                }
            },
            type: "GET"
        });
    }
}

function quitaAcentos(t){
	return t.normalize("NFD").replace(/[\u0300-\u036f]/g, "")
}

function autotab(current,to){
	var teclaASCII=event.keyCode; 
	if ((event.keyCode >47 && event.keyCode <58) || (event.keyCode >95 && event.keyCode <106)){
		if (current.getAttribute && current.value.length==current.getAttribute("maxlength")) {
			to.focus();
		}
	}
}

function limita(maximoCaracteres) {
	
	var elemento = document.getElementById("tx_observaciones");
	if(elemento.value.length >= maximoCaracteres ) {
	    return false;
	}else {
		return true;
	}
}

function soloNumeroEntero(event){
	var numero=String.fromCharCode(event.keyCode);
	if (!/^([0-9])*$/.test(numero)){
		return event.preventDefault();
	}else{
		return true;
	}
}

function validaFechaddMMYYYY(fecha) {
	var dtCh= "/";
	var minYear=1900;
	var maxYear=2100;
	function isInteger(s){
		var i;
		for (i = 0; i < s.length; i++){
			var c = s.charAt(i);
			if (((c < "0") || (c > "9"))) return false;
		}
		return true;
	}
	function stripCharsInBag(s, bag){
		var i;
		var returnString = "";
		for (i = 0; i < s.length; i++){
			var c = s.charAt(i);
			if (bag.indexOf(c) == -1) returnString += c;
		}
		return returnString;
	}
	function daysInFebruary (year){
		return (((year % 4 == 0) && ( (!(year % 100 == 0)) || (year % 400 == 0))) ? 29 : 28 );
	}
	function DaysArray(n) {
		for (var i = 1; i <= n; i++) {
			this[i] = 31
			if (i==4 || i==6 || i==9 || i==11) {this[i] = 30}
			if (i==2) {this[i] = 29}
		}
		return this
	}
	function isDate(dtStr){
		var daysInMonth = DaysArray(12)
		var pos1=dtStr.indexOf(dtCh)
		var pos2=dtStr.indexOf(dtCh,pos1+1)
		var strDay=dtStr.substring(0,pos1)
		var strMonth=dtStr.substring(pos1+1,pos2)
		var strYear=dtStr.substring(pos2+1)
		strYr=strYear
		if (strDay.charAt(0)=="0" && strDay.length>1) strDay=strDay.substring(1)
		if (strMonth.charAt(0)=="0" && strMonth.length>1) strMonth=strMonth.substring(1)
		for (var i = 1; i <= 3; i++) {
			if (strYr.charAt(0)=="0" && strYr.length>1) strYr=strYr.substring(1)
		}
		month=parseInt(strMonth)
		day=parseInt(strDay)
		year=parseInt(strYr)
		if (pos1==-1 || pos2==-1){
			return false
		}
		if (strMonth.length<1 || month<1 || month>12){
			return false
		}
		if (strDay.length<1 || day<1 || day>31 || (month==2 && day>daysInFebruary(year)) || day > daysInMonth[month]){
			return false
		}
		if (strYear.length != 4 || year==0 || year<minYear || year>maxYear){
			return false
		}
		if (dtStr.indexOf(dtCh,pos2+1)!=-1 || isInteger(stripCharsInBag(dtStr, dtCh))==false){
			return false
		}
		return true
	}
	if(isDate(fecha)){
		return true;
	}else{
		return false;
	}
}
