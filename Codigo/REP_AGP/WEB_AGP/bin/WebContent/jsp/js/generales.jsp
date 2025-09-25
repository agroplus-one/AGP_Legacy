<fmt:setBundle basename="displaytag" />
<c:set var="num"><fmt:message key="numElementsPag"/></c:set>
<c:if test="${num == ''}">
	<c:set var="num" value="10"/>
</c:if>

<script>

var generales = {
	camposRellenos:function(campos) {		
		var primerErrorCampoObligatorio = false;
		var primerErrorRazonSocial = false;
		var primerErrorNombre = false;
		var primerErrorPrimerApellido = false;
		var primerErrorSegundoApellido = false;
		//Array que va almacenando todos los mensajes de error
		var mensajes = new Array();
		var posicionMensaje = 0;
		var tipoIdenti = null;
	    var error  = "<label name='error' style='color:red;margin-left:5px;font-size:10px'>*</label>";
	    
	    if (document.getElementById('tipoIdentificacion'))
	    {
	    	tipoIdenti = document.getElementById('tipoIdentificacion').value;
	    }
	    
	     if(document.getElementById('pctprimerpago') != null){
	      
	         if(document.getElementById('pctprimerpago').value > 100){
		        document.getElementById('pctprimerpago').value = 100;			   	   	   			   
		      }		      
		
		     if((document.getElementById('pctprimerpago').value!= '') && (document.getElementById('pctprimerpago').value > 0)){
		       document.getElementById('pctsegundopago').value = 100 - document.getElementById('pctprimerpago').value;
		     }
		     else{
		       document.getElementById('pctsegundopago').value = '';
		     }	
		}
		
		
		//comprueba la primera fecha con la del dia
		if((document.getElementById('fechaIni') != null) && (document.getElementById('fechaIni').value != '')){
		  if (UTIL.fechaMayorOIgualQueFechaActual(document.getElementById('fechaIni').value) == false){
		  {
			  mensajes[posicionMensaje++] = 'La fecha del primer pago debe ser superior a la actual';
			  document.getElementById('fechaIni').value='';
		  }  
		 }	
		}  
		
	   //comprueba la segunda fecha con la primera fecha
	   if((document.getElementById('fechaIni') != null) && (document.getElementById('fechaFin') != null)){	
		if((document.getElementById('fechaIni').value!='') && (document.getElementById('fechaFin').value!='')){
		   if (UTIL.fechaMayorOIgualQue(document.getElementById('fechaIni'),document.getElementById('fechaFin')) == true){
		      mensajes[posicionMensaje++] = 'La fecha del segundo pago debe ser superior a la del primer pago';
		      document.getElementById('fechaFin').value='';
		    }
		}
	   }
		 
		  for(var i = 0; i < campos.length; i++)
     	  {
		
			var camposi = campos[i];
			var campo = document.getElementById(camposi);
			
			if(campo == null){
				campo = '';
			}				 
			
			if(campo.value == '') 
			{
				if ((camposi == 'tipoIdentificacion') && (campo.value == '')){			         
				    mensajes[posicionMensaje++] = 'Seleccione un tipo de identificación';	
				 }			    
			    if (primerErrorCampoObligatorio == false) {
				   	mensajes[posicionMensaje++] = '* Campos Obligatorios';
					primerErrorCampoObligatorio = true; 			    					
			    } 
			    
			//Va poniendo * a todos los campos vacios
			if (mensajes[posicionMensaje] != ''){
			  if (camposi == 'fechaIni'){
			     document.getElementById('errorFechaPrimerPago').innerHTML = document.getElementById('errorFechaPrimerPago').innerHTML + error;
			  } else if (camposi == 'fechaFin'){
			      document.getElementById('errorFechaSegundoPago').innerHTML = document.getElementById('errorFechaSegundoPago').innerHTML + error;
				}  
				      else{
				         campo.parentNode.innerHTML = campo.parentNode.innerHTML + error;
				      }
     		 }
     	  }
     	  else{
		  //si el campo no esta vacio se hacen las validaciones de cada uno
		  	if (camposi == 'cp'){
		      var expRegular=/0{3}$/;					       
			  if(expRegular.test(campo.value)){
			   campo.parentNode.innerHTML = campo.parentNode.innerHTML + error;
			   mensajes[posicionMensaje++] = 'El Código Postal no tiene el formato correcto';
		      }
			}
			
		   if ((camposi == 'pctprimerpago') && (parseInt(document.getElementById('pctprimerpago').value) <= 0)) {
				   	mensajes[posicionMensaje++] = 'El campo primer pago debe tener un valor mayor que 0';				   					
				
				
		   }else if ((camposi == 'pctsegundopago') && (document.getElementById('pctsegundopago').value < 0)) {
				   	mensajes[posicionMensaje++] = 'El campo segundo pago debe tener un valor mayor que 0';									
				  
				
		   }else if((camposi == 'pctsegundopago') && (document.getElementById('pctsegundopago').value >= 100)){
		        mensajes[posicionMensaje++] = 'El campo segundo pago debe tener un valor menor que 100'	
		   		        	      
		      
		   } else if (camposi == 'ccc'){		   		   
		   		var ibanccc = document.getElementById('iban').value+document.getElementById('cuenta1').value+
		   			document.getElementById('cuenta2').value+document.getElementById('cuenta3').value+
		   			document.getElementById('cuenta4').value+document.getElementById('cuenta5').value;
		   		// La funcion validar esta en el iban.js
		   		var f = validarIBAN(ibanccc);
		   		if (f == false){
			 		mensajes[posicionMensaje++] = 'IBAN pago prima incorrecto';
			 	}
		   } else if (camposi == 'ccc2'){		   		   
	   			var ibanccc = document.getElementById('iban2').value+document.getElementById('cuenta6').value+
		   			document.getElementById('cuenta7').value+document.getElementById('cuenta8').value+
		   			document.getElementById('cuenta9').value+document.getElementById('cuenta10').value;
		   		// La funcion validar esta en el iban.js
		   		var f = validarIBAN(ibanccc);
		   		if (f == false){
			 		mensajes[posicionMensaje++] = 'IBAN Cobro siniestros incorrecto';
			 	} 	
			// aquí comparamos el campo[i] con todos los nombres CIF/NIF de un formulario
		   }else if ((camposi == 'ciftomador' || camposi == 'tomador') && !generales.validaCifNif('CIF', campo.value)
					&& !generales.validaCifNif('NIF', campo.value)){
						mensajes[posicionMensaje++] = 'El formato NIF/CIF es incorrecto';
	 	   }else if (camposi == 'nifcif' && !generales.validaCifNif(document.getElementById('tipoIdentificacion').value, campo.value)){
				mensajes[posicionMensaje++] = 'El formato NIF/CIF es incorrecto';
		   }
		}
		}
		
		if ((tipoIdenti == 'CIF') && (primerErrorRazonSocial == false)){
			if (document.getElementById('razonsocial').value == ''){				
				mensajes[posicionMensaje++] = 'Una persona jurídica debe cumplimentar el campo Razón Social';
				primerErrorRazonSocial = true;
   		    }			
		} 
		else if (tipoIdenti == 'NIF'){			
			if ((document.getElementById('nombre').value == '') || (document.getElementById('apellido1').value == '') || (document.getElementById('apellido2').value == '')){
				
				if((document.getElementById('nombre').value == '') && (primerErrorNombre == false)){				  
				   mensajes[posicionMensaje++] = 'Una entidad física debe cumplimentar el campo Nombre';
			       primerErrorNombre = true;										
				} 
				
				if((document.getElementById('apellido1').value == '') && (primerErrorPrimerApellido == false)){
				  mensajes[posicionMensaje++] = 'Una entidad física debe cumplimentar el campo Primer Apellido';
				  primerErrorPrimerApellido = true;		
			    }
			    
			    if((document.getElementById('apellido2').value == '') && (primerErrorSegundoApellido == false)){
				  mensajes[posicionMensaje++] = 'Una entidad física debe cumplimentar el campo Segundo Apellido';
				  primerErrorSegundoApellido = true;		
			    }
			}
		}
		
		if(($('#regimensegsocial').val() != null) && ($('#numsegsocial').val() != null)){
			if ((document.getElementById('regimensegsocial').value == '') && (document.getElementById('numsegsocial').value != '')){				
					mensajes[posicionMensaje++] = 'el campo Régimen no puede estar vacío';
					$("#campoObligatorio_regimensegsocial").show();
					primerErrorCampoObligatorio = true;
	   		    }
	   	}
		return mensajes;
	},
	enviar:function(operacion) {
	$("#campoObligatorio_regimensegsocial").hide();
		if (arguments.length == 1) {
			document.forms.main.target="";
			document.forms.main.operacion.value = operacion;
			document.forms.main.submit();
		} else {
			UTIL.cleanErrors("panelErrores");
		    var frm    = document.getElementById("main");
			var campos = new Array();
			for (var i=1, j=0; i < arguments.length; i++, j++) 
			{
				campos[j] = arguments[i];
			}
			var errores = generales.camposRellenos(campos);
		    if(errores == '') {
		        frm.operacion.value = operacion;
		    	frm.submit();
		    } else {
		    	if (errores.length != 0) {
		    		document.getElementById("panelErrores").innerHTML = '';
	    		}
		    	for (var i = 0; i < errores.length; i++) {
		    	   	document.getElementById("panelErrores").innerHTML = document.getElementById("panelErrores").innerHTML
		    	   			+ errores[i] + '<br/>';
		    	}
		        document.getElementById("panelErrores").style.display = "block";
	        }
		}
	},
	enviarForm:function(formulario) {
		for (var i=1; i < arguments.length; i++) {
			document.getElementById(arguments[i++]).value = arguments[i];
		}		
		document.getElementById(formulario).submit();
	},	
	lpadNIF:function(texto){
		if (texto.length != 0){		
			while (texto.length < 9) {
				texto = "0" + texto;
			}
		}	
		return texto;
	},	
	validaCifNif:function(tipo,texto){
		var resultado = false;
		var lockup = 'TRWAGMYFPDXBNJZSQVHLCKE';
		if ("NIF" == tipo) {
		 if (/^[KLM]{1}/.test(texto)){	  // Nif Especial  K L M Se calcula igual que CIF
		 
		 	var pares = 0;
	        var impares = 0;
	        var suma;
	        var ultima;
	        var unumero;
	        var uletra = new Array("J", "A", "B", "C", "D", "E", "F", "G", "H", "I");
	        var xxx;
	        
		 	texto = texto.toUpperCase();
	        var regular = new RegExp(/^[KLM]\d\d\d\d\d\d\d[0-9,A-J]$/g);
	        if (!regular.exec(texto)) resultado = false;

	        ultima = texto.substr(8,1);
	        
	        for (var cont = 1 ; cont < 7 ; cont ++){
	            xxx = (2 * parseInt(texto.substr(cont++,1))).toString() + "0";
	            impares += parseInt(xxx.substr(0,1)) + parseInt(xxx.substr(1,1));
	            pares += parseInt(texto.substr(cont,1));
	        }
	        xxx = (2 * parseInt(texto.substr(cont,1))).toString() + "0";
	        impares += parseInt(xxx.substr(0,1)) + parseInt(xxx.substr(1,1));

	        suma = (pares + impares).toString();
	        unumero = parseInt(suma.substr(suma.length - 1, 1));
	        unumero = (10 - unumero).toString();
	        if(unumero == 10) unumero = 0;

	        if ((ultima == unumero) || (ultima == uletra[unumero]))
	            resultado = true;
	        else
	            resultado = false; 
		 }else{
		    while (texto.length < 9) {
		    	texto = "0" + texto;
		    }
		    var codigo = texto.substr(0,8);
		
	        var letra = lockup.charAt(codigo % 23);
	        if (letra == texto.substr(texto.length - 1)) {
	            resultado = true;
	        }
	      }  
        } else if ("CIF" == tipo) {
        	var pares = 0;
	        var impares = 0;
	        var suma;
	        var ultima;
	        var unumero;
	        var uletra = new Array("J", "A", "B", "C", "D", "E", "F", "G", "H", "I");
	        var xxx;

	        texto = texto.toUpperCase();
	        var regular = new RegExp(/^[ABCDEFGHKLMNPQS]\d\d\d\d\d\d\d[0-9,A-J]$/g);
	        if (!regular.exec(texto)) resultado = false;

	        ultima = texto.substr(8,1);

	        for (var cont = 1 ; cont < 7 ; cont ++){
	            xxx = (2 * parseInt(texto.substr(cont++,1))).toString() + "0";
	            impares += parseInt(xxx.substr(0,1)) + parseInt(xxx.substr(1,1));
	            pares += parseInt(texto.substr(cont,1));
	        }
	        xxx = (2 * parseInt(texto.substr(cont,1))).toString() + "0";
	        impares += parseInt(xxx.substr(0,1)) + parseInt(xxx.substr(1,1));

	        suma = (pares + impares).toString();
	        unumero = parseInt(suma.substr(suma.length - 1, 1));
	        unumero = (10 - unumero).toString();
	        if(unumero == 10) unumero = 0;

	        if ((ultima == unumero) || (ultima == uletra[unumero]))
	            resultado = true;
	        else
	            resultado = false; 
        }else if("NIE" == tipo){   
        	var temp=texto.toUpperCase();    
			if (/^[T]{1}/.test(texto)){
	                  if (temp[8] == /^[T]{1}[A-Z0-9]{8}$/.test(temp)){
	                         resultado = true;
	                  }else{
	                         resultado = false; 
	                  }
	         }
			//XYZ
	         if (/^[XYZ]{1}/.test(texto)){	         			
	         		  if(texto.substring(0,1) == 'X'){	         				
	         				temp = temp.replace('X','0');
	         		  }else if(texto.substring(0,1) == 'Y'){
	         				temp = temp.replace('Y','1');
	         		  }else if(texto.substring(0,1) == 'Z'){
	         				temp = temp.replace('Z','2');
	         		  }
	         		  var pos = temp.substring(0, 8) % 23; 	     
	                  if (temp.substring(8,9) == lockup.substring(pos, pos + 1)){
		                      resultado = true;
		               }else{
		                      resultado = false; 
		               }
	         }
      	} 
        return resultado;
	},
	
	//funcion para habilitar/deshabilitar campos segun se seleccione NIF/CIF
	cifnifSeleccionado:function() 	
	{
		if (document.getElementById('tipoIdentificacion').value == 'NIF' || document.getElementById('tipoIdentificacion').value == 'NIE') 
		{ 
			var varActivas = new Array('nombre','apellido1','apellido2','numsegsocial','jovenagricultor','regimensegsocial','atp');
			for (var i = 0; i < varActivas.length; i++) 
			{
				document.getElementById(varActivas[i]).disabled = false;								
			}
			var varInactivas = new Array('razonsocial');
			for (var i = 0; i < varInactivas.length; i++) 
			{
				document.getElementById(varInactivas[i]).value = '';
				document.getElementById(varInactivas[i]).disabled = true;
			}
		} 
		else 
		{
			if (document.getElementById('tipoIdentificacion').value == 'CIF')
			{
				var varActivas = new Array('razonsocial');
				for (var i = 0; i < varActivas.length; i++) 
				{
					document.getElementById(varActivas[i]).disabled = false;
				}
				
				var varInactivas = new Array('nombre','apellido1','apellido2','numsegsocial','jovenagricultor','regimensegsocial','atp');
				
				for (var i = 0; i < varInactivas.length; i++) 
				{
					document.getElementById(varInactivas[i]).value = '';
					document.getElementById(varInactivas[i]).disabled = true;
				}
			}
		}
		if((document.getElementById('comeFrom')!= null) && (document.getElementById('tipoIdentificacion')!= null)){
		  if (document.getElementById('comeFrom').value == '1'
			  && document.getElementById('tipoIdentificacion').value == 'NIF')
			  generales.botonesModificacion();
		}	  
	},
	uneCuenta:function() 
	{
		var cuenta1 = document.getElementById("cuenta1").value;
		var cuenta2 = document.getElementById("cuenta2").value;
		var cuenta3 = document.getElementById("cuenta3").value;
		var cuenta4 = document.getElementById("cuenta4").value;
		var cuenta5 = document.getElementById("cuenta5").value;
		document.getElementById("ccc").value = cuenta1 + cuenta2 + cuenta3 + cuenta4 +cuenta5;
		var cuenta6 = document.getElementById("cuenta6").value;
		var cuenta7 = document.getElementById("cuenta7").value;
		var cuenta8 = document.getElementById("cuenta8").value;
		var cuenta9 = document.getElementById("cuenta9").value;
		var cuenta10 = document.getElementById("cuenta10").value;
		document.getElementById("ccc2").value = cuenta6 + cuenta7 + cuenta8 + cuenta9 +cuenta10;
	},
	uneCuentaIBAN:function() 
	{
		var cuenta1 = document.getElementById("cuenta1").value;
		var cuenta2 = document.getElementById("cuenta2").value;
		var cuenta3 = document.getElementById("cuenta3").value;
		var cuenta4 = document.getElementById("cuenta4").value;
		var cuenta5 = document.getElementById("cuenta5").value;
		var cuenta6 = document.getElementById("cuenta6").value;
		document.getElementById("iban").value = cuenta1 + cuenta2 + cuenta3 + cuenta4 +cuenta5+cuenta6;
	},
	separaCuenta:function(cuenta, cuenta2) 
	{
		if ('' != cuenta) {
			document.getElementById("cuenta1").value = cuenta.toString().substring(0,4);
			document.getElementById("cuenta2").value = cuenta.toString().substring(4,8);
			document.getElementById("cuenta3").value = cuenta.toString().substring(8,12);
			document.getElementById("cuenta4").value = cuenta.toString().substring(12,16)
			document.getElementById("cuenta5").value = cuenta.toString().substring(16,20)
		}
		if (cuenta2 != null && '' != cuenta2) {
			document.getElementById("cuenta6").value = cuenta2.toString().substring(0,4);
			document.getElementById("cuenta7").value = cuenta2.toString().substring(4,8);
			document.getElementById("cuenta8").value = cuenta2.toString().substring(8,12);
			document.getElementById("cuenta9").value = cuenta2.toString().substring(12,16)
			document.getElementById("cuenta10").value = cuenta2.toString().substring(16,20)
		}
	},
	separaCuentaIBAN:function(cuenta) 
	{
		if ('' != cuenta) {
			document.getElementById("cuenta1").value = cuenta.toString().substring(0,4);
			document.getElementById("cuenta2").value = cuenta.toString().substring(4,8);
			document.getElementById("cuenta3").value = cuenta.toString().substring(8,12);
			document.getElementById("cuenta4").value = cuenta.toString().substring(12,16);
			document.getElementById("cuenta5").value = cuenta.toString().substring(16,20);
			document.getElementById("cuenta6").value = cuenta.toString().substring(20,24);
		}
	},
	botonesModificacion:function()
	{
		var alta = document.getElementById('btnAlta');
		alta.style.display = "none";
		var cons = document.getElementById('btnConsultar');
		cons.style.display = "none";
		var modif = document.getElementById('btnModificar');
		modif.style.display = "";
	},
	//funcion que nos fija un fila seleccionada en la tabla del displayTag
	fijarFila:function() 
	{
	    var esInstalacion = false;
		var tbodies = document.getElementsByTagName("tbody");
		
		for (var j=0; j<tbodies.length; j++) 
		{
            // solo para las tablas que se indican en isIdTableInListTables
            var idTable = $(tbodies[j]).parent().attr('id');

            if(generales.isIdTableInListTables(idTable))
			{
				var rows = tbodies[j].getElementsByTagName("tr");	
				// ROWS 					
				for (var i=0; i<rows.length; i++) 
				{		
					rows[i].oldClassName = rows[i].className;			
					rows[i].onclick = function() { // añado el evento click de la fila				
					var tag = this.className;	
								
					//Por cada click limpio todos los selected, Comprobamos si hemos hecho clic en la tabla displayTag 
					if((tag.indexOf("odd")!=-1 )|| (tag.indexOf("even") !=-1) || 
					    tag.indexOf("filaInstalacionImpar") || tag.indexOf("filaInstalacionPar"))
					{
						rows = this.parentNode.rows;
						// RECORRO LAS ROWS
						for(var h=0;h<this.parentNode.rows.length; h++)
						{
						    esInstalacion = false;
							if(h%2 == 0)
							{
							            // recorro todas las celdas de la fila
									    cells = rows[h].getElementsByTagName('td');
								      	if(cells.length > 0)
								      	{ 
									       if (cells[0].innerHTML.indexOf("E@E") != -1){
									           esInstalacion = true;
								           }else{
								               esInstalacion = false;
								           }
								        }
	
									    // si es instalacion
									    if(esInstalacion == true){
									        rows[h].className = "filaInstalacionPar";
									    }else{
									        rows[h].className = "odd";
									    }
							}
							else
							{
							            // recorro todas las celdas de la fila
									    cells = rows[h].getElementsByTagName('td');
								      	if(cells.length > 0)
								      	{ 
									       if (cells[0].innerHTML.indexOf("E@E") != -1){
									           esInstalacion = true;
								           }else{
								               esInstalacion = false;
								           }
								       }
								       // si es instalacion
									   if(esInstalacion == true){
									        rows[h].className = "filaInstalacionImpar";
									    }else{
									        rows[h].className = "even";
									    }
							}//if							
						}//for
					}//if

				
                    // ONLY ROW CLICK Asginamos el css de seleccion a la fila que hizo saltar el evento	
					if (this.className.indexOf("selected") != -1) {
						this.className = this.oldClassName;
					} else {							
						this.className = this.className + " selected";
					}//if
				}//for
				
              }//if

			}//if solo las tablas indicadas
		}//for tbody
	},
	isIdTableInListTables:function(idTable){
	    var result = false;
	    
	    var listTables  = ["listaParcelas_cm","listaColectivos","listaAsegurados","listaColectivos",
	                       "listaHistoricoColectivos","listaSubentidades","listaTomadores","asegurado",
	                       "clase","colectivo","listCamposMascara","grupoAsegurado","listModulosCompatibles",
	                       "listPantallasConfig","listaRelCampos"];
	                     
	     for(var i = 0; i < listTables.length;i++){
	         if(listTables[i] == idTable)
	             result = true;
	     }
	     
	     return result;
	}
	

}// end object

function editarPlzDefMulti (idpoliza) {
	$('#idpolizaEditarPlzDefMulti').val (idpoliza);
	$('#editarPlzDefMulti').submit();
} 

</script>


<body>
	<!-- Formulario para editar una poliza desde la pantalla de grabación definitiva masico -->
	<form name="editarPlzDefMulti" id="editarPlzDefMulti" action="editaPolizaUtilidades.html" method="post">
		<input type="hidden" name="method" id="methodEditarPlzDefMulti" value="doEditaPoliza"/>
		<input type="hidden" name="idpoliza" id="idpolizaEditarPlzDefMulti"/>
		<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidadesEditarPlzDefMulti" value="true"/>
	</form>
</body>

