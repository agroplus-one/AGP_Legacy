$(function(){
	$("#grid").displayTagAjax();
}).ajaxSend(function(){
	//checkSeleccionados();
}).ajaxComplete(function(){
	//pintarEstadoModif();
	//pintarCheckModif();
	//pintarIncreModif();
});

$(document).ready(function(){
	
	/*  Pet. 78691 ** MODIF TAM (30/12/2021) * Resolución Defecto Nº 3*/
	$('#sistemaCultivo').val($('#sist_cultivo').val());
	$('#dessistemaCultivo').val($('#des_sist_cultivo').val());
	/*  Pet. 78691 ** MODIF TAM (30/12/2021) * Resolución Defecto Nº 3*/

});

function salir(){
	$(window.location).attr('href', 'declaracionesModificacionPoliza.html?idPoliza='+ $('#idPoliza').val() + '&rand='+encodeURI(parseInt(Math.random()*99999999) + "_" + (new Date).getTime())); 		
}

function limpiar(){
	$('#provincia').val('');
	$('#comarca').val('');
	$('#termino').val('');
	$('#subtermino').val('');
	$('#desc_provincia').val('');
	$('#desc_termino').val('');
	$('#desc_comarca').val('');
	$('#desc_capital').val('');
	$('#poligono').val('');
	$('#parcela').val('');
	$('#provSig').val('');
	$('#TermSig').val('');
	$('#agrSig').val('');
	$('#zonaSig').val('');
	$('#polSig').val('');
	$('#parcSig').val('');
	$('#recSig').val('');
	$('#nombre').val('');
	$('#cultivo').val('');
	$('#variedad').val('');
	$('#desc_cultivo').val('');
	$('#desc_variedad').val('');
	$('#capital').val('');
	$('#superficie').val('');
	$('#prodAnt').val('');
	$('#estado').selectOptions('');
	/* GDLD-78691 ** MODIF TAM (30/12/2021) Defecto Nº 2 */
	$('#sistemaCultivo').val('');
	$('#dessistemaCultivo').val('');

	consultar();
}

function coberturas(){
	$('#method').val('doCoberturas');
	$('#main3').submit();
}

function consultar(){
	$('#method').val('doVisualiza');
	$('#main3').submit();
}

function imprimirListadoParcelas(formato){
	var frm = document.getElementById('main3');
    frm.target="_blank";
    frm.formato.value = formato;
    frm.method.value = 'doImprimirInformeListadoParcelasAnexo';
    frm.submit();
    frm.target="";
}

function imprimir(){
	$('#idPolizaPrint').val($('#idPoliza').val());					
	$('#printCpl').attr('target', '_blank');
	$("#printCpl").submit();	
}

function volverAnexoListado(){
	$('#volverUtilidadesAnexos').submit();
}

//Va a la pantalla de cï¿½lculo de modificaciï¿½n en modo lectura
function verDC () {
	$("#formDistCoste").submit();
}