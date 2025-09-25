// ComprobarFechas.js

function ComprobarFecha (campoOrigen, formulario, descripcion)
{
	//alert("camporigen="+campoOrigen+" formulario="+formulario+" descripcion="+descripcion);
	var ok = true;
	var fecha = new Object();
	fecha.dia, fecha.mes, fecha.anio = 0;
	// analisis sintactico
	ok = InterpretarFecha(fecha,campoOrigen,descripcion);
   
	if (ok)
	{
		ok = ValidarFecha(fecha, campoOrigen,descripcion);
		if (ok)
		{
			var nomDia = campoOrigen.id + ".day";
			var nomMes = campoOrigen.id + ".month";
			var nomAnio = campoOrigen.id + ".year";
			/*
			var nomDia = campoOrigen.name + ".day";
			var nomMes = campoOrigen.name + ".month";
			var nomAnio = campoOrigen.name + ".year";
			*/
			formulario.elements[nomDia].value = fecha.dia;
			formulario.elements[nomMes].value = fecha.mes;
			formulario.elements[nomAnio].value = fecha.anio;

			// las fechas en blanco las convertimos a ceros
			if ( (fecha.dia != 0) && (fecha.mes != 0) && (fecha.anio != 0) )
			{
				campoOrigen.value = fecha.dia+'/'+fecha.mes+'/'+fecha.anio;
			}
			else
			{
            campoOrigen.value = "";
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	else
	{
		return false;
	}
}


// Esta versión de ComprobarFecha2, se utiliza sólo desde la función
// ComprobarMultiFecha y ComprobarMultiFechaHora

function ComprobarFecha2 (campoOrigen, formulario, descripcion, iteracion)
{
	var ok = true;
	var fecha = new Object();
	fecha.dia, fecha.mes, fecha.anio = 0;
	// analisis sintactico
	ok = InterpretarFecha(fecha,campoOrigen,descripcion);

	if (ok)
	{
		ok = ValidarFecha(fecha, campoOrigen,descripcion);

		if (ok)
		{
			var nomDia = campoOrigen.name + ".day";
			var nomMes = campoOrigen.name + ".month";
			var nomAnio = campoOrigen.name + ".year";

            formulario.elements[nomDia][iteracion].value = fecha.dia;
            formulario.elements[nomMes][iteracion].value = fecha.mes;
            formulario.elements[nomAnio][iteracion].value = fecha.anio;
		
			// las fechas en blanco las convertimos a ceros
			if ( (fecha.dia != 0) && (fecha.mes != 0) && (fecha.anio != 0) )
			{
            campoOrigen.value = fecha.dia+'/'+fecha.mes+'/'+fecha.anio;
			}
			else
			{
				campoOrigen.value = "";
	            formulario.elements[nomDia][iteracion].value = 0;
    	        formulario.elements[nomMes][iteracion].value = 0;
        	    formulario.elements[nomAnio][iteracion].value = 0;
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	else
	{
		return false;
	}
}


// El parámetro "multi" es opcional, y sólo se indica si se está llamando a la función
// desde ComprobarMultiFechaHora, para que se llame a ComprobarFecha2 en vez de
// a ComprobarFecha

function ComprobarFechaHora (campoOrigen, formulario, descripcion, iteracion, multi)
{
// funcion para comprobar fecha y hora de un campo
	var resultado = true;
	var valorOriginal = campoOrigen.value;
	var hayEspacios = true;
	var posicion = campoOrigen.length;
	
	// quitar espacios al final de la cadena (despues de la hora y minuto)
	for (var ii = campoOrigen.value.length-1;(hayEspacios);ii--)
	{
		var aux = valorOriginal.charAt(ii);
		if ( aux != " ")
		{
			hayEspacios = false;
			posicion = ii;
		}
	}
	
	var parteFecha;
	var parteHora;
	
	// obtenemos el valor sin espacios al final
	var valorSinEspacios = valorOriginal.slice (0,posicion+1);
	// obtenemos el lugar donde empieza la hora
	var posicionHora = valorSinEspacios.lastIndexOf(" ",posicion+1); 

	// si el valor sin espacios al final es vacío
	if (valorSinEspacios == "")
	{
		// parteFecha y parteHora los pondremos a vacío
		parteFecha = "";
		parteHora = "";	
	}
	else if (posicionHora == -1)
	{
		// si no hay un espacio separando fecha de hora, el dato no es válido
		alert ("El campo '"+descripcion+"' no contiene una fecha-hora válida");
		campoOrigen.focus();
		return false;
	}
	else
	{
		// obtenemos la parte de la fecha dentro de la variable "valorSinEspacio" cortando la cadena
		// desde el principio hasta donde empiece la hora indicado por la variale "posicionHora" 
		// por la variable "posicionHora"
		parteFecha = valorSinEspacios.slice(0,posicionHora+1);
		// obtenemos la parte de la hora cortando la cadena desde donde empieza la hora 
		// hasta el final
		parteHora = valorSinEspacios.slice(posicionHora+1,valorSinEspacios.length);
	}

	campoOrigen.value = parteFecha; // dejo solo la fecha en el valor del campo

	// Si estamos llamando directamente a esta función desde nuestra página
	if (!multi) {
		resultado = ComprobarFecha (campoOrigen, formulario, descripcion, iteracion);
	}
	else  {
	// Si llamamos a esta función desde ComprobarMultiFechaHora
		resultado = ComprobarFecha2 (campoOrigen, formulario, descripcion, iteracion-1);
	}


	// si la fecha es valida..
	if (resultado)
	{
		// recojo la fecha válida y formateada
		parteFecha = campoOrigen.value;
		// dejo la hora en el valor del campo
		campoOrigen.value = parteHora;
		// comprobamos que se trate de una hora válida
		resultado = ComprobarHora (campoOrigen, formulario, descripcion, iteracion);
		// si la hora es válida
		if (resultado)
		{
			// guardo en parteHora la hora válida y formateada
			parteHora = campoOrigen.value;
			// si la fecha y hora son distintos de vacío
			if ( (parteFecha != "") && (parteHora != "") )
			{
				// el valor de campo origen es la fecha y hora separadas por un espacio
				campoOrigen.value = parteFecha +" "+parteHora;
			}
			else
			{
				campoOrigen.value = "";
			}
		}
	}
	// si la fecha no es válida..
	if (resultado == false)
	{
		// restauro el valor original
		campoOrigen.value = valorOriginal;
	}
	return resultado;
}

function ComprobarHora(campoOrigen,formulario,descripcion,iteracion)
{
	var valorHoraOriginal = campoOrigen.value;
	var hora;
	var minu;
	// if hora esta en blanco, ni interpreto ni valido
	if (valorHoraOriginal == "")
	{
		hora = 0;
		minu = 0;
	}
	else
	{		
		// localizo la posicion de ":"
		var dosPuntos = campoOrigen.value.indexOf(":",0);
		// si no hay ":" en la hora..
		if (dosPuntos == -1)
		{
			// la hora y minutos estará formada por dos dígitos
			hora = campoOrigen.value.slice(0,2);
			minu = campoOrigen.value.slice(2,campoOrigen.value.length);
			// si no esta formada por dos dígitos..
			if ( (hora.length != 2) || (minu.length != 2) )
			{
				// el formato de la hora introducida no es válida
				alert ("La hora introducida en el campo "+descripcion+ "no es vlida");
				campoOrigen.focus();
				return false;
			}
		}
		else
		{
			hora = campoOrigen.value.slice(0,dosPuntos);
			minu = campoOrigen.value.slice(dosPuntos+1,campoOrigen.value.length);
		}
		
		// comprobar que la hora tiene digitos y no otros caracteres
		for (var jj = 0; jj < hora.length; jj++)
		{
			var carHora = hora.charAt(jj);
			if ( (carHora <'0') || (carHora > '9') )
			{
				alert ("La hora introducida en el campo '"+descripcion+"' no es vlida");
				campoOrigen.focus();
				return false;
			}
		}
		// comprobar que los minutos tiene digitos y no otros caracteres
		for (var zz = 0; zz < minu.length; zz++)
		{
			var carMinu = minu.charAt(zz);
			if ( (carMinu <'0') || (carMinu > '9') )
			{
				alert ("La hora introducida en el campo '"+descripcion+"' no es vlida");
				campoOrigen.focus();
				return false;
			}
		}

		if ( (hora < 0) || (hora > 23) )
		{
			alert ("La hora introducida en el campo '"+descripcion+ "' no es vlida");
			campoOrigen.focus();
			return false;
		}
		if ( (minu < 0) || (minu > 59) )
		{
			alert ("La hora introducida en el campo '"+descripcion+ "' no es vlida");
			campoOrigen.focus();
			return false;
		}
		if ( hora.length == 1 )
		{
			// añadimos el cero ("0") delante de la hora para darle nuestro formato
			hora = "0"+hora;
		}
		if ( minu.length == 1 )
		{
			// añadimos el cero ("0") delante de los minutos para darle nuestro formato
			minu = "0"+minu;
		}
		// el valor del campo tendrá nuestra hora formateada
		campoOrigen.value = hora +":"+minu;
	}
		
	var nomHora = campoOrigen.name + ".hour";
	var nomMinuto = campoOrigen.name + ".minute";
	
	var encontradoHora = false;
	var encontradoMinuto = false;

	var indice;
	// variables que contendran la iteracion, es decir; de todos los
	// campos hidden con el mismo nombre que hay en cual de ellos
	// deberemos almacenar el valor
	var iteracionHora = 1;
	var iteracionMinuto = 1;
						
	for (indice=0; indice<formulario.length ; indice++)
	{
		if (formulario.elements[indice].name == nomHora)
		{
			// comparamos la posicion dentro del formulario donde
			// se encuentra el campo, con la posicion donde 
			// corresponde introducir el valor 
			if (iteracion == iteracionHora)
			{
				formulario.elements[indice].value = hora;
				encontradoHora = true;
			}
			iteracionHora++;
		}
		if (formulario.elements[indice].name == nomMinuto)
		{
			if (iteracion == iteracionMinuto)
			{
				formulario.elements[indice].value = minu;
				encontradoMinuto = true;
			}
			iteracionMinuto++;
		}
	}

	if ( (encontradoHora) && (encontradoMinuto) )
	{
		// las horas en blanco no se muestran
		if (valorHoraOriginal != "")
		{
			campoOrigen.value = hora+':'+minu;
		}
		else
		{
			campoOrigen.value = "";
		}
		return true;
	}
	else
	{
		alert ("Faltan los campos hidden referentes al campo Fecha-Hora: " +campoOrigen.name);
		return false;
	}
	
	return true;
}


function ComprobarMultiFecha (miForm,NombreCampo,descripcion)
{
	// funcion para comprobar los campos multivalores de forma que
	// sean valores 'fecha' validos
	var resultado = true;

	// recorro todos los elementos del formulario
	for (var contador = 0 ; ( (contador < miForm.elements[NombreCampo].length) && (resultado) ); contador++)
	{
		resultado = ComprobarFecha2 (miForm.elements[NombreCampo][contador],miForm, descripcion, contador);
	}
	if (resultado)
	{
		return true;
	}
	else
	{
		return false;
	}
}

function ComprobarMultiFechaHora (miForm,NombreCampo,descripcion)
{
	// funcion para comprobar los campos multivalores de forma que
	// sean valores 'fecha-hora' validos
	var resultado = true;
	var iteracion = 1;
	// recorro todos los elementos del formulario
	for (var contador = 0 ; ( (contador < miForm.length) && (resultado) ); contador++)
	{
		if ( miForm.elements[contador].name == NombreCampo )
		{
		resultado = ComprobarFechaHora (miForm.elements[contador],miForm, descripcion, iteracion, true);
   		iteracion++;
		}
	}
	if (resultado)
	{
		return true;
	}
	else
	{
		return false;
	}
}


function InterpretarFecha (fecha,campoOrigen,descripcion)
{
	// si el campo esta vacio para que no nos de ningun error
	if (campoOrigen.value =="")
	{
		fecha.dia = 0;
		fecha.mes = 0;
		fecha.anio = 0;
		return true;
	}

	if (campoOrigen.value.indexOf ('/') == -1)
	{
		return InterpretarFechaSinBarras(fecha, campoOrigen, descripcion);
	}
	else
	{
		return InterpretarFechaConBarras(fecha, campoOrigen, descripcion);
	}
}
	
function InterpretarFechaSinBarras(fecha, campoOrigen, descripcion)
{
	// declaramos la variable cadena y la inicializamos a vacio
	var cadena = "";
	// recorrremos uno a uno los caracteres del campo
	// comprobando que son caracteres validos para almecenarlos
	for (var i = 0; i < campoOrigen.value.length ; i++)
	{
		var car = campoOrigen.value.charAt(i);
		if ((car=="0") || (car=="1") || (car=="2") || (car=="3") || (car=="4") ||
			(car=="5") || (car=="6") || (car=="7") || (car=="8") || (car=="9")) 
		{
			// almacenamos en la variable cadena los caracteres validos  
			cadena = cadena + car;
		}
		else if ( (car !=' ') && (car!='\n') && (car=='\t') ) 
		{
			alert ("El campo '" +descripcion+ "' no contiene una fecha válida.");
			campoOrigen.focus();
			return false;
		} 
	}
	
	// comprobamos la longitud de la cadena para validar la fecha
	if ((cadena.length != 6) && (cadena.length != 8))
	{
		alert ("El campo '" +descripcion+ "' no contiene una fecha válida.");
		campoOrigen.focus();
		return false;
	}
		
	// obtengo de la variable cadena los caracteres para almacenarlos
	// en dia, mes, anio		
	var dia = (cadena.slice(0,2));
	var mes = (cadena.slice(2,4));
	if (cadena.length == 6)
	{
		var anio = (cadena.slice(4,6));
		// comparamos si el anio es menor que 50, para hacer que sea
		// de la forma 20xx
		if (anio <= 50) 
		{
			anio = '20'+anio;
		}
		// en caso contrario el anio sera 19xx
		else 
		{
			anio = '19'+anio;
		}
	}
	else
	{
		anio = (cadena.slice(4,8));
	}

	// esta sentencia convierte un string en un numero		
	/*
	dia = dia - 0;
	mes = mes - 0;
	anio = anio - 0; 
	*/
	// asignamos los valores de dia , mes y anio a 
	// fecha.dia, fecha.mes y fecha.anio respectivamente
	fecha.dia = dia;
	fecha.mes = mes;
	fecha.anio = anio;

	return true;
}		


function InterpretarFechaConBarras(fecha, campoOrigen,descripcion)
{
	var primera = campoOrigen.value.indexOf ('/');
	var dia = campoOrigen.value.slice (0,primera);
	var segunda  = campoOrigen.value.indexOf ('/',primera+1);
	var mes = campoOrigen.value.slice (primera+1,segunda);
	var anio = campoOrigen.value.slice (segunda+1,campoOrigen.value.length);
	// comprobar que anio no incluye barras
	if ( (anio.indexOf ('/') != -1) )
	{
		alert ("El campo '" +descripcion+ "' no contiene una fecha válida.");
		campoOrigen.focus();
		return false;
	}
	
	var cadenaDia = "";
	for (var i = 0; i < dia.length ; i++)
	{
		var carDia = dia.charAt(i);
		if ((carDia=="0") || (carDia=="1") || (carDia=="2") || (carDia=="3") || (carDia=="4") ||
			(carDia=="5") || (carDia=="6") || (carDia=="7") || (carDia=="8") || (carDia=="9")) 
		{
			// almacenamos en la variable cadena los caracteres validos  
			cadenaDia = cadenaDia + carDia;
		}
		else if ( (carDia !=' ') && (carDia!='\n') && (carDia=='\t') ) 
		{
			alert ("El campo '" +descripcion+ "' no contiene una fecha válida.");
			campoOrigen.focus();
			return false;
		} 
	}
			
	var cadenaMes = "";
	for (var j = 0; j < mes.length ; j++)
	{
		var carMes = mes.charAt(j);
		if ((carMes=="0") || (carMes=="1") || (carMes=="2") || (carMes=="3") || (carMes=="4") ||
			(carMes=="5") || (carMes=="6") || (carMes=="7") || (carMes=="8") || (carMes=="9")) 
		{
			// almacenamos en la variable cadena los caracteres validos  
			cadenaMes = cadenaMes + carMes;
		}
		else if ( (carMes !=' ') && (carMes!='\n') && (carMes=='\t') ) 
		{
			alert ("El campo '" +descripcion+ "' no contiene una fecha válida.");
			campoOrigen.focus();
			return false;
		} 
	}
		
	var cadenaAnio = "";
	for (var k = 0; k < anio.length ; k++)
	{
		var carAnio = anio.charAt(k);
		if ((carAnio=="0") || (carAnio=="1") || (carAnio=="2") || (carAnio=="3") || (carAnio=="4") ||
			(carAnio=="5") || (carAnio=="6") || (carAnio=="7") || (carAnio=="8") || (carAnio=="9")) 
		{
			// almacenamos en la variable cadena los caracteres validos  
			cadenaAnio = cadenaAnio + carAnio;
		}
		else if ( (carAnio !=' ') && (carAnio!='\n') && (carAnio=='\t') ) 
		{
			alert ("El campo '" +descripcion+ "' no contiene una fecha válida.");
			campoOrigen.focus();
			return false;
		} 
	}

	if ( (cadenaAnio.length != 2) && (cadenaAnio.length != 4) )
	{
		alert ("El campo '" +descripcion+ "' no contiene una fecha válida.");
		campoOrigen.focus();
		return false;
	}
	else if (cadenaAnio.length == 2)
	{
		if (cadenaAnio <= 50) 
		{
			cadenaAnio = '20'+cadenaAnio;
		}
		// en caso contrario el anio sera 19xx
		else 
		{
			cadenaAnio = '19'+cadenaAnio;
		}
	}
		
	// convertir string a numerico
	/*
	cadenaDia = cadenaDia - 0;
	cadenaMes = cadenaMes - 0;
	cadenaAnio = cadenaAnio - 0;
	*/
	fecha.dia = cadenaDia;
	fecha.mes = cadenaMes;
	fecha.anio = cadenaAnio;

	return true;
}
	
	
function ValidarFecha (fecha, campoOrigen,descripcion)
{
	if ( (fecha.dia == 0) && (fecha.mes == 0) && (fecha.anio == 0) )
	{
		return true;
	}
	
	// comprobamos que es un mes correcto, es decir
	// un mes comprendido entre 1 y 12
	if ((fecha.mes == 0) || (fecha.mes > 12) || (fecha.dia > 31) || (fecha.dia == 0) || (fecha.anio == 0))
	{
		alert ("El campo '" +descripcion+ "' no contiene una fecha válida.");
		campoOrigen.focus();
		return false;
	}
		
	// comprobamos que enero no tenga mas de 31 dias
	if ((fecha.mes == 1) && (fecha.dia > 31))
	{
		alert ("El campo '" +descripcion+ "' no contiene una fecha válida: enero sólo tiene 31 días.");
		campoOrigen.focus();
		return false;
	}
		
	// comprobamos que marzo no tenga mas de 31 dias
	if ((fecha.mes == 3) && (fecha.dia > 31))
	{
		alert ("El campo '" +descripcion+ "' no contiene una fecha válida: marzo sólo tiene 31 días.");
		campoOrigen.focus();
		return false;
	}
		
	// comprobamos que abril no tiene mas de 30 dias
	if ((fecha.mes == 4) &&  (fecha.dia > 30))
	{
		alert("El campo '" +descripcion+ "' no contiene una fecha válida: abril sólo tiene 30 días.");
		campoOrigen.focus();
		return false;
	}
		
	// comprobamos que mayo no tiene mas de 31 dias
	if ((fecha.mes == 5) && (fecha.dia > 31))
	{
		alert ("El campo '" +descripcion+ "' no contiene una fecha válida: mayo sólo tiene 31 días.");
		campoOrigen.focus();
		return false;
	}
		
	// comprobamos que junio no tiene mas de 30 dias
	if ((fecha.mes == 6) &&  (fecha.dia > 30))
	{
		alert("El campo '" +descripcion+ "' no contiene una fecha válida: junio sólo tiene 30 días.");
		campoOrigen.focus();
		return false;
	}
	
	// comprobamos que julio no tenga mas de 31 dias
	if ((fecha.mes == 7) && (fecha.dia > 31))
	{
		alert ("El campo '" +descripcion+ "' no contiene una fecha válida: julio sólo tiene 31 días.");
		campoOrigen.focus();
		return false;
	}
		
	// comprobamos que agosto no tenga mas de 31 dias
	if ((fecha.mes == 8)  && (fecha.dia > 31))
	{
		alert ("El campo '" +descripcion+ "' no contiene una fecha válida: agosto sólo tiene 31 días.");
		campoOrigen.focus();
		return false;
	}
	
	// comprobamos que septiembre no tenga mas de 30 dias
	if ((fecha.mes == 9) &&  (fecha.dia > 30))
	{
		alert("El campo '" +descripcion+ "' no contiene una fecha válida: septiembre sólo tiene 30 días.");
		campoOrigen.focus();
		return false;
	}
		
	// comprobamos que octubre no tenga mas de 31 dias
	if ((fecha.mes == 10) && (fecha.dia > 31))
	{
		alert ("El campo '" +descripcion+ "' no contiene una fecha válida: octubre sólo tiene 31 días.");
		campoOrigen.focus();
		return false;
	}
	
	// comprobamos que noviembre no tenga mas de 30 dias
	if ((fecha.mes == 11) &&  (fecha.dia > 30))
	{
		alert("El campo '" +descripcion+ "' no contiene una fecha válida: noviembre sólo tiene 30 días.");
		campoOrigen.focus();
		return false;
	}
		
	// comprobamos que diciembre no tenga mas de 31 dias
	if ((fecha.mes == 12) && (fecha.dia > 31))
	{
		alert ("El campo '" +descripcion+ "' no contiene una fecha válida: diciembre sólo tiene 31 días.");
		campoOrigen.focus();
		return false;
	}
		
	var bisiesto = false;
	if ( ((fecha.anio % 4) == 0) && ((fecha.anio % 100) != 0) )
	{
		bisiesto = true;
	}
	else
	{
		if (((fecha.anio % 4) == 0) && ((fecha.anio % 100) == 0) && ((fecha.anio % 400) == 0))
		{
			bisiesto = true;
		}
	}

	if ((fecha.mes == 2) && (fecha.dia > 29) && (bisiesto == true))
	{
		alert("El campo '" +descripcion+ "' no contiene una fecha válida: febrero sólo tiene 29 días en año bisiesto.");
		campoOrigen.focus();
		return false;
	}

	if ((fecha.mes == 2) && (fecha.dia > 28) && (bisiesto==false))
	{
		alert("El campo '" +descripcion+ "' no contiene una fecha válida: febrero sólo tiene 28 días.");
		campoOrigen.focus();
	   return false;
	}

	return true;
}

function CargarFechaActual (SobreEscribir, campo)
{
// esta funcion cargará la fecha actual en un campo
// recibirá dos variables,"SobreEscribir" que si su valor es true
// sobreescribiremos el valor del campo, si es false dejaremos
// el campo como está
	var fecha = new Date();
	var dia = fecha.getDate();
	var mes = fecha.getMonth();
	var mes = mes + 1;
	var anio = fecha.getFullYear();
	//DAA 02/07/2013
	if (dia < 10){
		dia = "0"+dia;
	}
	if (mes < 10){
		mes = "0"+mes;
	}
	var FechaActual = dia+"/"+mes+"/"+anio; 
	if ((campo.value == "") || (SobreEscribir)){
		campo.value = FechaActual;
	}
}

function CargarFechaHoraActual (SobreEscribir, campo)
{
// esta funcion cargará la fecha y hora actual en un campo
// recibirá dos variables,"SobreEscribir" que si su valor es true
// sobreescribiremos el valor del campo, si es false dejaremos
// el campo como está
	var fecha = new Date();
	var dia = fecha.getDate();
	var mes = fecha.getMonth();
	var mes = mes + 1;
	var anio = fecha.getFullYear();
   var hora = fecha.getHours();
   if (hora < 10)
   {
      hora = "0" + hora;
   }
   var minuto = fecha.getMinutes();
   if (minuto < 10)
   {
      minuto = "0" + minuto;
   }

	var FechaHoraActual = dia + "/" + mes + "/" + anio + " " + hora + ":" + minuto; 
	if ((campo.value == "") || (SobreEscribir))
	{
		campo.value = FechaHoraActual;
	}
}


// Esta función compara dos fechas, según el operador que se le facilite.
// Admite todos los operadores de javascript: "==", "<", ">", "!="
// Devuelve "true" o "false".
// Las fechas deben pasarse como cadenas de texto, pero FORMATEADAS (dd/mm/aaaa hh:mm), 
// por lo que deberemos hacer pasar el campo de fecha por la función "ComprobarFecha"
// antes de llamar a esta función.
// La indicación de la HORA es OPCIONAL.
// "fecha1" es la parte izquierda de la expresión y "fecha2" la parte derecha.
// Si alguna de las dos fechas es la cadena vacía, se considera siempre menor que
// cualquier otra fecha.
// obligatorio : indica si es obligatorio que se cumpla la comparación o si sólo se avisa.
// mensaje : mensaje que se muestra si la comparación no es satisfactoria.
function CompararFechas (fecha1, operador, fecha2, obligatorio, mensaje)
{
   var fechaA,fechaB;
   var resultado;
   var noesvacioA = true;
   var noesvacioB = true;
   var auxarray;

   // Si las cadenas de texto no son vacías, las transformamos en fechas
   // Si no, modificamos la variable correspondiente que indica que es NaN
   // OJO : SUPONEMOS QUE LA FECHA VACÍA ES LA FECHA MÁS PEQUEÑA. DE ESA FORMA, CUALQUIER FECHA
   // ES MAYOR QUE LA VACÍA, Y AL SER, POR DEFINICIÓ€œN, TRUE > FALSE, APROVECHAMOS ESTO PARA
   // DECIR QUE UNA FECHA NO VACÍA ES TRUE Y UNA VACÍA FALSE, ASÍ LAS SIGUIENTES COMPARACIONES CON
   // FECHAS VACÍAS DARÍAN TRUE:
   // FECHA VACIA < FECHA NO VACÍA
   // FECHA NO VACIA > FECHA VACÍA.
   // FECHA VACÍA = FECHA VACÍA
   //
   // EL RESTO DE COMPARACIONES EN LAS QUE APARECE LA FECHA VACÍA DEVUELVE FALSE.
   if (fecha1.value != "")
   {
      auxarray = fecha1.value.split("/");
      fechaA = Date.parse (auxarray[1] + "/" + auxarray[0] + "/" + auxarray[2]);
   }
   else
      noesvacioA = false;
      
   if (fecha2.value != "")
   {
      auxarray = fecha2.value.split("/");
      fechaB = Date.parse (auxarray[1] + "/" + auxarray[0] + "/" + auxarray[2]);
   }
   else
      noesvacioB = false;

   // Dependiendo de si hay fecha que son NaN o no, realizamos la comparación como fechas
   // o como valores lógicos.
   if (noesvacioA == false || noesvacioB== false)
      resultado = eval ("noesvacioA" + operador + "noesvacioB");
   else
      resultado = eval ("fechaA" + operador + "fechaB");
   

   // Aquí miramos si era obligatorio, aviso y realizamos las acciones oportunas.
   if (resultado == false)
   {
      if (obligatorio == true)
      {
	 alert (mensaje);
         resultado = false;
      }
      else
      {
         resultado = confirm ( mensaje + "\n¿ Desea usted continuar ?");
      }
   }

   // Devolvemos el resultado
   return resultado
}
