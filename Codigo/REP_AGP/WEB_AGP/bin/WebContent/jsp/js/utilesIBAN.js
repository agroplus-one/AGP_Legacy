// FUNCIONES "PUBLICAS"
function validarCampoIBAN(numero){
	var ibanValido = false;
    if (esLongitudIBAN(numero)){
    	ibanValido = validarIBAN(numero);
    }
    return ibanValido;
}



// FUNCIONES "PRIVADAS"
function esLongitudIBAN(cifras) {
    return cifras.length == 24;
}

function validarIBAN(iban) {
    var pais = iban.substr(0, 2);
    var dc = iban.substr(2, 2);
    var cifras = iban.substr(4, 20) + valorCifras(pais) + dc;
    resto = modulo(cifras, 97);
    return resto == 1;
}

function valorCifras(cifras) {
    var letras = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // A=10, B=11, ... Z=35
    var items = [];
    for (var i=0; i<cifras.length; i++) {
  	var posicion = letras.indexOf(cifras.charAt(i));
      items.push(posicion < 0? "-": posicion);
    }
    return items.join("");
}

function modulo(cifras, divisor) {
/*
  El entero mas grande en Javascript es 9.007.199.254.740.990 (2^53)
  que tiene 16 cifras, de las cuales las 15 ultimas pueden tomar cualquier valor.
  El divisor y el resto tendran 2 cifras. Por lo tanto CUENTA como tope
  puede ser de 13 cifras (15-2) y como minimo de 1 cifra.
*/
	var CUENTA = 10;
	var largo = cifras.length;
	var resto = 0;
	for (var i=0; i<largo; i+=CUENTA) {
		var dividendo = resto + "" + cifras.substr(i, CUENTA);
		resto = dividendo % divisor;
    }
    return resto;
}