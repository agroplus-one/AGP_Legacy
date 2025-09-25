<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>


<fmt:bundle basename="displaytag">
	<c:set var="numReg">
		<fmt:message key="numElementsPag" />
	</c:set>
</fmt:bundle>





<html>
<head>
<title>Revision Precios y Produccion</title>


<%@ include file="/jsp/common/static/metas.jsp"%>


<%@ include file="/jsp/common/static/metas.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>


<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />


<script type="text/javascript" src="jsp/js/util.js" ></script>
<script type="text/javascript" src="jsp/js/menuapli.js"></script>
<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
<script type="text/javascript" src="jsp/moduloPolizas/polizas/revision/revProduccionPrecios.js"></script>
<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>


<script type="text/javascript">

			/*LLamada al metodo que utiliza AJAX para paginar el displaytag*/
			/*añadimos el evento .ajaxComplete, para pintar los valores modificados despues de cada llamada a AJAX(cada vez que paginamos)*/		
			$(function(){
				   $("#grid").displayTagAjax();
				}
			).ajaxComplete(function(){
					pintarModif();
			});
			


	        $(document).ready(function(){
	            var URL = UTIL.antiCacheRand(document.getElementById("main").action);
		        document.getElementById("main").action = URL; 
		        
		        
		        var URL = UTIL.antiCacheRand(document.getElementById("maintable").action);
		        document.getElementById("maintable").action = URL; 
    
	        });
			
			
			$(document).ready(function(){
				
				$('#main').validate({
					 errorLabelContainer: "#panelAlertasValidacion",
   					 wrapper: "li",
					 onfocusout: false, 
					 rules: {
					 	"produccion": {number: true, betweenProduccion: ['produccionMin','produccionMax']},
					 	"precio": {number: true, betweenPrecio: ['precioMin','precioMax']}
					 },
					 messages: {
					 	"produccion": {number: "El valor introducido en el campo Producción no es válido", betweenProduccion: "La producción no se encuentra entre los límites aceptados"},
					 	"precio": {number: "El valor introducido en el campo Precio no es válido", betweenPrecio: "El precio no se encuentra entre los límites aceptados"}
					 }
				});
				
				
				jQuery.validator.addMethod("betweenPrecio", function(value, element, params) { 
 					return (this.optional(element) || (parseFloat($('#'+params[0]).val()) <= parseFloat(element.value) && parseFloat(element.value) <= parseFloat($('#'+params[1]).val())));
				});
				
				jQuery.validator.addMethod("betweenProduccion", function(value, element, params) {
					if($('#'+params[1]).val() != "")				
 						return (this.optional(element) || (parseFloat($('#'+params[0]).val()) <= parseFloat(element.value) && parseFloat(element.value) <= parseFloat($('#'+params[1]).val())));
					return (this.optional(element) || (parseFloat($('#'+params[0]).val()) <= parseFloat(element.value)));
				});
				
			});
		
			function enviarTabla(){
				document.forms.maintable.operacion.value = 'Cambiar';
				$.blockUI.defaults.message = '<h4> Grabando los datos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } }); 
				document.forms.maintable.submit();
			}
			
			
			function modificar(codtipocapital, sigpac, superficie){
			
				var frm = document.getElementById('main');
				frm.codtipocapital.value = codtipocapital;
				frm.sigpac.value = sigpac;
				frm.superficie.value = superficie;
				$("#panelAlertasValidacion").hide();
			}
			
			function validar(){
			
				if ($('#main').valid()){
					ocultarAviso();
					modificarTabla();
				} 
				
			}
			
			function validarProduccion(){
				
				var produccionMod = document.getElementById('produccion').value;
				
				if(isNaN(produccionMod) || isNaN(parseFloat(produccionMod))){
					mostrarAviso('El valor introducido en el campo Producción no es válido');
					return false;	
				}
				
				produccionMod = Math.round(parseFloat(produccionMod)*100)/100;
				
				if(document.getElementById('produccionMax').value < produccionMod || document.getElementById('produccionMin').value > produccionMod){
					mostrarAviso('La producción no se encuentra entre los límites aceptados');
					return false;	
				}
				
				document.getElementById('produccion').value = produccionMod;
				
				return true;

			}
			
			function validarPrecio(){
			
				var precioMod = document.getElementById('precio').value;
				
				if(isNaN(precioMod) || isNaN(parseFloat(precioMod))){
					mostrarAviso('El valor introducido en el campo Precio no es válido');
					return false;	
				}
				
				var precioMod = Math.round(parseFloat(precioMod)*100)/100;
				var precioFijo =  parseFloat(document.getElementById('precioFijo').value);
				
				if( isNaN(precioFijo) && (document.getElementById('precioMax').value < precioMod || document.getElementById('precioMin').value > precioMod)){
					mostrarAviso('El precio no se encuentra entre los límites aceptados');
					return false;
				}
				
				document.getElementById('precio').value = precioMod;
				
				return true;

			}
			
			function modificarTabla(){
				var table = document.getElementById('capital');
				if(table){
					var rowCount = table.rows.length;
					var string = $('#capModif').val();
					for(var i=1; i<rowCount; i++) {
						var cell = table.rows[i].cells[0];
						 if (cell.childNodes[1].value == document.getElementById('idCapitalAseguradoSeleccionado').value){
		            	    
		            	    table.rows[i].cells[7].innerHTML = document.getElementById('produccion').value;
		            	    table.rows[i].cells[9].innerHTML = document.getElementById('precio').value;
		           	   		
		           	   		var string1 = "listaProduccionMod_" + document.getElementById('idCapitalAseguradoSeleccionado').value;
		           	   		var string2 = "listaPrecioMod_" + document.getElementById('idCapitalAseguradoSeleccionado').value;	           	   		
		           	   			           	   		
		           	   		$('#'+ string1).val(document.getElementById('produccion').value);
		           	   		$('#'+ string2).val(document.getElementById('precio').value);
		           	   		
		           	   		 string += document.getElementById('idCapitalAseguradoSeleccionado').value + "|" + document.getElementById('produccion').value + "|" + document.getElementById('precio').value + ";";
		           	    }
		            }
		            $('#capModif').val(string);
				}
	         }
	         
	         function volver(){
	         	 
	         	//$(window.location).attr('href', 'webservices.html?rand=' + UTIL.getRand() + '&idpoliza='+$('#idpoliza').val()+'&operacion=validar'); 
	         	
	         	
	         	$.blockUI.defaults.message = '<h4> Realizando cálculos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } }); 
	         	//$(window.location).attr('href', 'webservices.html?rand=' + UTIL.getRand() + '&idpoliza='+$('#idpoliza').val()+'&operacion=validar');
	         	$(window.location).attr('href', 'seleccionPoliza.html?rand=' + UTIL.getRand() + '&idpoliza='+$('#idpoliza').val()+ '&idEnvio='+$('#idEnvio').val()+'&operacion=importes');
	         	
	         			
	         }
	         
	         function pintarModif(){
	         	var table = document.getElementById('capital');
	         	if(table){
	         		var rowCount = table.rows.length;
		         	var string = "";	         	
		         	if($('#capModif').val() != null){
		         		string = $('#capModif').val().substring(0,$('#capModif').val().length-1);
		         		string = string.split(";");
		         		for(var i=1; i<rowCount; i++){
		         			for(var j=0; j<string.length; j++){
		         				var datos = string[j].split("|");
			         			var cell = table.rows[i].cells[0];
							 	if (cell.childNodes[1].value == datos[0]){
								 	table.rows[i].cells[7].innerHTML = datos[1];
				            	    table.rows[i].cells[9].innerHTML = datos[2];
							 	}
		         			}
		         		}
		         	}
	         	}
	         }
	        
</script>
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub3')">

<%@ include file="/jsp/common/static/cabecera.jsp"%>
<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
<%@ include file="/jsp/common/static/datosCabecera.jsp"%>

<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="right">
						<a class="bot" href="javascript:volver()">Volver</a>
						<a class="bot" href="javascript:validar();">Modificar</a>
						<a class="bot" href="javascript:enviarTabla()">Aceptar Cambios</a>
					</td>
				</tr>
			</table>
</div>
<!-- Contenido de la página -->
<div class="conten" style="padding: 3px; width: 97%">
	<p class="titulopag" align="left">Revisión Precios y Producción</p>
	<form:form name="main" id="main" action="revProduccionPrecio.html" method="post" commandName="capitalAseguradoBean">
				<input type="hidden" name="idpoliza" value="${idpoliza}"/>
				<input type="hidden" id="codPoliza" value="${idpoliza}"/>
				<input type="hidden" id="operacion" name="operacion" />
				<input type="hidden" id="precioMin" />
				<input type="hidden" id="precioMax" />
				<input type="hidden" id="produccionMin" />
				<input type="hidden" id="produccionMax" />
				<input type="hidden" id="precioFijo" />
				<input type="hidden" id="idCapitalAseguradoSeleccionado" />
				<input type="hidden" name="idEnvio" id="idEnvio" value="${idEnvio}"/>
				<form:hidden path="idcapitalasegurado" />
				
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<%@ include file="/jsp/common/static/avisoErroresLocal.jsp"%>
				
				<div class="panel2 isrt">
					<table width="80%" align="center">
						<tr>
							<td class="literal">SIGPAC
								<input type="text" id="sigpac" class="dato" size="38" disabled="true"/>
							</td>
							<td class="literal">Tipo Capital
								<form:input path="" id="codtipocapital" cssClass="dato" size="30" maxlength="30" disabled="true" />
							</td>
							<td class="literal">Superficie
								<form:input path="" id="superficie" cssClass="dato" size="8" maxlength="8" disabled="true" />
							</td>
						</tr>
					</table>
					<fieldset class="fieldset_alone" align="center">
						<legend class="literal">Datos modificables</legend>
						<table width="100%">
							<colgroup>
								<col width="35%" align="left" />
								<col width="30%" align="left" />
								<col width="*" align="center" />
							</colgroup>
							<thead>
								<tr><th /><th /><th class="literal">Límites</th></tr>
							</thead>
							<tr>
								<td class="literal">Producción</td>
								<td class="literal"><input type="text" class="dato" id="produccion" name="produccion" value="" size="8" maxlength="8" readonly="true"/></td>
								<td class="literal"><input class="literal" id="limitesProduccion" value="" /></td>
								<img id="ajaxLoading_lineas" src="jsp/img/ajax-loading.gif" width="16px" style="cursor:hand;cursor:pointer;display:none" height="11px" />
							</tr>
							<tr>
								<td class="literal">Precio</td>
								<td class="literal"><input type="text" class="dato" id="precio" name="precio"  value=""  size="8" maxlength="8" readonly="true"/></td>
								<td class="literal"><input class="literal" id="limitesPrecio" value="" /></td>
								<img id="ajaxLoading_lineas" src="jsp/img/ajax-loading.gif" width="16px" style="cursor:hand;cursor:pointer;display:none" height="11px" />
							</tr>
						</table>
					</fieldset>
				</div>
			</form:form>
			
	<form:form name="maintable" id="maintable" action="revProduccionPrecio.html" method="post" commandName="capitalAseguradoBean">
		<input type="hidden" id="operacion" name="operacion" />
		<input type="hidden" id="idpoliza" name="idpoliza" value="${idpoliza}" />
		<input type="hidden" id="capModif" name="capModif"/>
		<input type="hidden" name="idEnvio" id="idEnvio" value="${idEnvio}"/>
	<!-- Aqui tiene que ir el grid de datos -->
					<div id="grid">
						<display:table requestURI="" class="LISTA" summary="listaCapitalAsegurado"
						pagesize="${numReg}"  size="${totalListSize}" name="${listaCapitalAsegurado}" id="capital"
						decorator="com.rsi.agp.core.decorators.ModelTableDecoratorRevProduccionPrecios" sort="list">
						
						
						
							<display:column class="literal" headerClass="cblistaImg" title="Acciones" property="capitalesAseguradosSelec" sortable="false" style="width:50px;text-align:center" media="html"/>
							<display:column class="literal" headerClass="cblistaImg" title="N&ordm;" property="parcelaHoja"  style="width:50px;"  sortable="true"  comparator = "com.rsi.agp.core.comparators.TableComparatorHojaNumero"/>
							<display:column class="literal" headerClass="cblistaImg" title="Id. Cat." sortable="true" property="codProvSigpac"/>
							<display:column class="literal" headerClass="cblistaImg" title="SIGPAC." sortable="true" property="sigPac" style="text-align:center;" comparator = "com.rsi.agp.core.comparators.TableComparatorSigPacs"/>
							<display:column class="literal" headerClass="cblistaImg" title="Tipo Capital" sortable="true" property="desTipoCapital" />
							<display:column class="literal" headerClass="cblistaImg" title="Superficie&nbsp;&nbsp;" sortable="true" property="superficie" style="text-align: right;" />
							<display:column class="literal" headerClass="cblistaImg" title="Prod. Intr.&nbsp;&nbsp;" sortable="true" property="produccionInt" style="text-align: right; "/>
							<display:column class="literal" headerClass="cblistaImg" title="Prod. Modif." property="produccionMod" style="text-align: right; color:red;" />
							<display:column class="literal" headerClass="cblistaImg" title="Prec. Intr.&nbsp;&nbsp;" sortable="true" property="precioInt" style="text-align: right;" />
							<display:column class="literal" headerClass="cblistaImg" title="Prec. Modif." property="precioMod" style="text-align: right; color:red;" />
						</display:table>
					</div>
		</form:form>
</div>
<%@ include file="/jsp/common/static/piePagina.jsp"%>
<%@ include file="/jsp/common/static/overlay.jsp"%>
</body>
</html>