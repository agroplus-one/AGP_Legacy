/**
 * T-Systems 2012
 * Componente  js
 * 
 * Popup-ajax de datos variables de pac, desde los origenes de datos.
 * Permite paginación y ordenación por columnas.
 * Usado en: verDatosVariablesPac.jsp
 */


/** ---------------------------------------- VARIABLES ---------------------------------------- */
var rowColor;
var itemsPacPopUp;
var title;
var numPages;
var numRecords;
var page = 1; // las páginas empiezan en 1, no en 0
var concepto;
var plLinCon;
var ascCodImg = 'none';
var desCodImg = 'inline';
var ascDesImg = 'none';
var desDesImg = 'inline';
/** ---------------------------------------- CONSTANTS ---------------------------------------- */
var MAX_NUM_PAGE = 10;
/** ---------------------------------------- MÉTODOS ----------------------------------------- */

/**
 * llamada ajax que obtiene los datos. 
 */ 
function lupaDatoVariableCargaPac(codConcepto,descripcion){
	plLinCon = codConcepto;
	concepto = codConcepto;
	var data = "method=doDatosVariablesLupa&codConcepto=" + concepto;
	$.ajax({
			url: "lupa.html",
			data: data,
			async:true,
			dataType: "json",
			success: function(datos){	
			    itemsPacPopUp = eval(datos);
			     createLupa(descripcion);	
			},				           
			type: "POST"
	});
}

/**
 * crea la popup y la muestra 
 */ 
function createLupa(descripcion){

	numRecords = itemsPacPopUp.length;
	// calculo del numero de paginas
	numPages =  Math.floor(numRecords / MAX_NUM_PAGE);
	if(numRecords % MAX_NUM_PAGE != 0){
		numPages++;
	}
	
	
	// ------------- set data -------------
    $('#titulo').html(descripcion);
    $('#infoPage').html('Pagina ' + page + 'de ' + numPages);
    $('#registrosInfo').html(numRecords + ' registros encontrados.');
	
	
	// ------- BODY LUPA (registros) -------
	var table = "<table id='tabla' style='border-bottom: #ccc 1px solid; border-left: #ccc 1px solid; width: 100%; table-layout: fixed; border-top: #ccc 1px solid; border-right: #ccc 1px solid;'>";
    table = table + "<thead id='thead'>" +
    		                "<tr class='cblistaImg literal' style='background-color: #e5e5e5;'>" +
    		                     "<td class='cblistaImg literal' style='background-color: #e5e5e5;'>" +
    		                                 "<a>" +
    		                                 "<img id='codigo-asc' src='jsp/img/displaytag/arrow_up.png' complete='complete' style='display:" + ascCodImg + ";cursor:hand;cursor:pointer' onclick=\u0022ordenarPopUpLupaPac(&#39;codigo&#39;,&#39;asc&#39;)\u0022/>" +
    		                                 "<img id='codigo-des' src='jsp/img/displaytag/arrow_down.png' complete='complete' style='display:" + desCodImg + ";cursor:hand;cursor:pointer' onclick=\u0022ordenarPopUpLupaPac(&#39;codigo&#39;,&#39;des&#39;)\u0022/>" +
	    		                             " &nbsp;Código" +
	    		                             "</a>" + 
    		                     "</td>" +
    		                     "<td class='cblistaImg literal' style='background-color: #e5e5e5;'>" +
    		                                 "<a>" +
	    		                             "<img id='descri-asc' src='jsp/img/displaytag/arrow_up.png' complete='complete' style='display:" + ascDesImg + ";cursor:hand;cursor:pointer' onclick=\u0022ordenarPopUpLupaPac(&#39;descripcion&#39;,&#39;asc&#39;)\u0022/>" +
	    		                             "<img id='descri-des' src='jsp/img/displaytag/arrow_down.png' complete='complete' style='display:" + desDesImg + ";cursor:hand;cursor:pointer' onclick=\u0022ordenarPopUpLupaPac(&#39;descripcion&#39;,&#39;des&#39;)\u0022/>" +
	    		                             " &nbsp;Descripcion" +
	    		                             "</a>" + 
    		                     "</td>" +
    		                "</tr>" +
    		         "</thead>";
    
    var count = 0;
    var firstRecord = (page-1)*10;		         
	for(var i = firstRecord;i < itemsPacPopUp.length;i++){
		var backcolor = (i%2 != 0) ? "#F7F7F7" : "#FFF";
		table  = table + "<tr style='background-color:"+ backcolor +";cursor:pointer' onclick=\u0022clickRowLupa(&#39;" + itemsPacPopUp[i].codigo + "&#39;,&#39;" + itemsPacPopUp[i].descripcion + "&#39;)\u0022 onmouseover='overRowLupa(this)' onmouseout='outRowLupa(this)'>" +
								"<td class='literal'>" + itemsPacPopUp[i].codigo + "</td>" +
								"<td class='literal'>" + itemsPacPopUp[i].descripcion + "</td>" +
						 "</tr>";
						 
		count++;
		if(count == 10){
			break;
		}
	}
	
	table = table + "</table>";	
	
	// ---------------- view ------------------
	
	$('#table-lupaGenericaOrigDatos').html(table);
	$('#lupaGenericaOrigDatos').fadeIn('normal');
}
/**
 * cierra la popup-lupa
 */
function cerrarPopupDatosVarPac(){
	$("#lupaGenericaOrigDatos").fadeOut('normal');
}
/**
 * pagina
 */
function paginarLupaPac(accion){
	// paginar
	
	switch(accion){
		case "principio":
		    page = 1;
		    createLupa();
		    break;
		case "final":
		    page = numPages;
		    createLupa();
		    break;
		case "adelante":
		   if((page + 1) <= numPages){
			  page = page + 1;
			  createLupa();
		   }
		   break;
		case "atras":
		   if((page - 1) >= 1){
			   page = page - 1;
			   createLupa();
		   }
		   break;
	}
	$('#infoPage').html('Pagina ' + page + 'de ' + numPages);	
}

/** ---------------------------------------- EVENT HANDLER ----------------------------------------- */

/**
 * handler for onclick event in rows
 */
function clickRowLupa(codigo,descripcion){
	document.getElementById("" + plLinCon).value = codigo;
	document.getElementById(plLinCon+"_des").value = descripcion;
	$("#lupaGenericaOrigDatos").fadeOut('normal');
}

/**
 * handler for onmouseout event in rows
 */
function overRowLupa(elem){
	rowColor = elem.style.backgroundColor;
	elem.style.backgroundColor = '#CCCCCC';
}

/**
 * hander for onmouseout event in rows
 */
function outRowLupa(elem){
	elem.style.backgroundColor = rowColor;	
}

/**
 * Busqueda por descripción, actualizar info.
 * Si no mete nada y le da a buscar recarga los registros.
 */
function buscarPopUpLupaPac(){
	var valueToFind = $("#filtroTabla").val().toUpperCase();
 	var newitemsPacPopUp=[];

	if(valueToFind != ""){
	 	for(var i = 0; i < numRecords; i++){
	        if(itemsPacPopUp[i].descripcion == valueToFind)	{
	        	newitemsPacPopUp.push(itemsPacPopUp[i]);
	        }    	
	 	}
	 	itemsPacPopUp = newitemsPacPopUp;
	 	createLupa()
	}else{
		lupaDatoVariableCargaPac(plLinCon);
	}
 }


function setImgAscDes(){
	ascCodImg = $('#descri-asc').css('display');
	desCodImg = $('#descri-des').css('display');
	ascDesImg = $('#codigo-asc').css('display');
	desDesImg = $('#codigo-des').css('display');	
} 
/**
 * Ordenar datos columna
 */
function ordenarPopUpLupaPac(columna, modo){
	if(columna == "descripcion"){
			if(modo == 'asc'){
                $('#descri-asc').hide();
                $('#descri-des').show();
                itemsPacPopUp.sort(sortByDescripcionAsc);
			}else if(modo == 'des'){
                $('#descri-asc').show();
                $('#descri-des').hide();
                itemsPacPopUp.sort(sortByDescripcionDes);
			}
    }else if(columna == "codigo"){
    	    if(modo == 'asc'){
                $('#codigo-asc').hide();
                $('#codigo-des').show();
                itemsPacPopUp.sort(sortByCodigoAsc);
			}else if(modo == 'des'){
                $('#codigo-asc').show();
                $('#codigo-des').hide();
                itemsPacPopUp.sort(sortByCodigoDes);
               
			}
    }
    
    setImgAscDes();
    createLupa();
}
/** ---------------------------------------- ORDENACION ----------------------------------------- */

/**
 * sin orden ------> 
 * ascendiente ----> de menor a mayor, el triangulo apunta hacia arriba
 * descendiente ---> de mayor a menor, el triangulo apunta hacia abajo
 */

/**
 * ordena por descripcion descendiente 
 */
function sortByDescripcionDes(a,b){
	 var aDes = a.descripcion.toLowerCase();   
	 var bDes = b.descripcion.toLowerCase();    
	 return ((aDes < bDes) ? -1 : ((aDes > bDes) ? 1 : 0)); 
}
/**
 * ordena por descripcion ascendentemente 
 */
function sortByDescripcionAsc(a,b){
	 var aDes = a.descripcion.toLowerCase();   
	 var bDes = b.descripcion.toLowerCase();    
	 return ((aDes > bDes) ? -1 : ((aDes < bDes) ? 1 : 0)); 
}
/**
 * ordena numérica por código descendiente 
 */
 
function sortByCodigoDes(aa,bb){
     var a, b;

	 if(aa.codigo != null){
	 	a = parseInt(aa.codigo);
	 }
	 if(bb.codigo != null){
	 	b = parseInt(bb.codigo);
	 }

	 if(!isNaN(a) && !isNaN(b)){
		 if (a < b)return 1;
	     if (a > b)return -1;
	     if (a == b)return 0;
	 }else{
	 	return 0;
	 }
	 
}
/**
 * ordena numérica por código ascendentemente
 */
function sortByCodigoAsc(aa,bb){
	 var a, b;

	 if(aa.codigo != null){
	 	a = parseInt(aa.codigo);
	 }
	 if(bb.codigo != null){
	 	b = parseInt(bb.codigo);
	 }

	 if(!isNaN(a) && !isNaN(b)){
		 if (a < b)return -1;
	     if (a > b)return 1;
	     if (a == b)return 0;
	 }else{
	 	return 0;
	 }
}


