<%@ page buffer="100kb"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<fmt:bundle basename="displaytag">
	<c:set var="numReg">
		<fmt:message key="numElementsPag" />
	</c:set>
</fmt:bundle>

<fmt:bundle basename="agp">
	<c:set var="numRegImpresion">
		<fmt:message key="impresionnumReg" />
	</c:set>
</fmt:bundle>

<html>
<head>
<title>Histórico de Colectivos</title>

<%@ include file="/jsp/common/static/metas.jsp"%>

<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />

<script type="text/javascript" src="jsp/js/util.js"></script>
<script type="text/javascript" src="jsp/js/menuapli.js"></script>
<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js"></script>
<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
<script type="text/javascript" src="jsp/js/calendar.js"></script>
<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
<script type="text/javascript" src="jsp/js/displayTagAjaxSelectedRow.js"></script>
<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
<%@ include file="/jsp/js/draggable.jsp"%>

<script type="text/javascript">

			$(document).ready(function() {
				
				 // Para evitar el cacheo de peticiones al servidor
                 var URL = UTIL.antiCacheRand(document.getElementById("main").action);
			     document.getElementById("main").action = URL;    
			
		      	/*Zapatec.Calendar.setup({
			        firstDay          : 1,
			        weekNumbers       : false,
			        showOthers        : true,
			        showsTime         : false,
			        timeFormat        : "24",
			        step              : 2,
			        range             : [1900.01, 2999.12],
			        electric          : false,
			        singleClick       : true,
			        inputField        : "fechaIni",
			        button            : "btn_fechaIni",
			        ifFormat          : "%d/%m/%Y",
			        daFormat          : "%d/%m/%Y",
			        align             : "Br"			        	        
		      	});
		      	
		      	Zapatec.Calendar.setup({
			        firstDay          : 1,
			        weekNumbers       : false,
			        showOthers        : true,
			        showsTime         : false,
			        timeFormat        : "24",
			        step              : 2,
			        range             : [1900.01, 2999.12],
			        electric          : false,
			        singleClick       : true,
			        inputField        : "fechaFin",
			        button            : "btn_fechaFin",
			        ifFormat          : "%d/%m/%Y",
			        daFormat          : "%d/%m/%Y",
			        align             : "Br"
		      	});
		      	
		      	Zapatec.Calendar.setup({
			        firstDay          : 1,
			        weekNumbers       : false,
			        showOthers        : true,
			        showsTime         : false,
			        timeFormat        : "24",
			        step              : 2,
			        range             : [1900.01, 2999.12],
			        electric          : false,
			        singleClick       : true,
			        inputField        : "fechaCambio",
			        button            : "btn_fechaCambio",
			        ifFormat          : "%d/%m/%Y",
			        daFormat          : "%d/%m/%Y",
			        align             : "Br"
		      	});
		      	
		      	Zapatec.Calendar.setup({
			        firstDay          : 1,
			        weekNumbers       : false,
			        showOthers        : true,
			        showsTime         : false,
			        timeFormat        : "24",
			        step              : 2,
			        range             : [1900.01, 2999.12],
			        electric          : false,
			        singleClick       : true,
			        inputField        : "fechaEfecto",
			        button            : "btn_fechaEfecto",
			        ifFormat          : "%d/%m/%Y",
			        daFormat          : "%d/%m/%Y",
			        align             : "Br"
		      	});*/
	      	});

            
            $(document).ready(function(){
            	
            	$('#main').validate({
					 errorLabelContainer: "#panelAlertasValidacion",
					 wrapper: "li",					 
					 highlight: function(element, errorClass) {
					 	$("#campoObligatorio_" + element.id).show();
	  			     },
	  				 unhighlight: function(element, errorClass) {
						$("#campoObligatorio_" + element.id).hide();
	  				 },
	  				  				  				 
					 rules: {
					 	"tomador.id.codentidad":{digits: true},
					 	"linea.codplan":{digits: true},
					 	"linea.codlinea":{digits: true},
					 	"subentidadMediadora.id.codentidad":{digits: true},
					 	"subentidadMediadora.id.codsubentidad":{digits: true},				 	
					 	"pctprimerpago":{range: [0,100]},				 	
					 	"pctsegundopago":{range: [0,100]},
					 	"pctdescuentocol":{range: [0,100]},	 
					 	"fechaIni":{ dateITA: true},
					 	"fechaFin": {dateITA: true},
					 	"fechaCambio":{dateITA: true},
					 	"fechaEfecto":{dateITA: true}	
					 },
					 
					 messages: {
					 	"tomador.id.codentidad":{digits: "El campo Entidad sólo puede contener dígitos"},
					 	"linea.codplan":{digits: "El campo Plan sólo puede contener dígitos"},
					 	"linea.codlinea":{digits: "El campo Línea debe contener dígitos"},
					 	"subentidadMediadora.id.codentidad":{digits: "El campo Entidad mediadora sólo puede contener dígitos"},
					 	"subentidadMediadora.id.codsubentidad":{digits: "El campo Subentidad mediadora sólo puede contener dígitos"},
					 	"pctprimerpago":{range: "El campo % Primer pago debe contener un número entre 1 y 100"},
					 	"pctsegundopago":{range: "El campo % Segundo pago debe contener un número entre 1 y 99"},
					 	"pctdescuentocol":{range: "El campo % Colectivo Cálculo debe contener un número entre 1 y 99"},
					 	"fechaIni":{dateITA: "El formato del campo Fecha primer pago es dd/mm/YYYY"},
					 	"fechaFin":{dateITA: "El formato del campo Fecha segundo pago es dd/mm/YYYY"},
					 	"fechaCambio":{dateITA: "El formato del campo Fecha de cambio es dd/mm/YYYY"},
					 	"fechaEfecto":{dateITA: "El formato del campo Fecha de efecto es dd/mm/YYYY"}
					 		
					 }
				});
            });    
            
		</script>

<script type="text/javascript">
		
		function enviarForm(operacion,id) {
			
			jConfirm('¿Estás seguro de que desea eliminar el registro seleccionado?', 'Diálogo de Confirmación', function(r) {
				
				if (r){
					var frm = document.getElementById('main');
					frm.id.value = id;
					frm.operacion.value = operacion;
					frm.submit();
				}
			});
							
		}
		
		// MODIF TAM (02.01.2020)  Se da de alta una nueva función que se encarga
		// del borrado del histórico del colectivo //
		function bajaColectivo(id) {
			jConfirm('¿Estás seguro de que desea eliminar el registro seleccionado?', 'Diálogo de Confirmación', function(r) {
				
				if (r){
					var frm = document.getElementById('main');
					frm.id.value = id;
					$('#method').val("doBajaHistorico");
					
					frm.submit();
				}
			});
							
		}
		// FIN //
			
		function consultar(){
			var frm = document.getElementById('main');
			frm.target="";
			$('#method').val("doConsulta");
			$('#main').submit();	
		}
		
		function volver(){
			$('#operacion').val("consultar");
			$('#recogerColectivoSesion').val("true");
			$('#addFiltro').val("true");
			$('#volver').submit();	
		}			
		
		function limpiar(){
			$('#entidad').val('');
			$('#desc_entidad').val('');
			$('#desc_linea').val('');
			$('#desc_tomador').val('');
			$('#desc_entmediadora').val('');
			$('#desc_subentmediadora').val('');
			$('#plan').val('');				
			$('#linea').val('');
			$('#tomador').val('');
			$('#domicilio').val('');
			$('#nomcolectivo').val('');
			$('#entmediadora').val('');
			$('#subentmediadora').val('');
			$('#activo').selectOptions("");
			$('#pctsegundopago').val('');
			$('#pctdescuentocol').val('');
			$('#pctprimerpago').val('');
			$('#fechaIni').val('');
			$('#fechaFin').val('');
			$('#id').val('');
			$('#lineaseguroid').val('');
			$('#fechaCambio').val('');
			$('#fechaEfecto').val('');
			$('#fechaEfecto').val('');
			$('#tipooperacion').val('');
			
			consultar();
		}
	
	
		//funcion que valida un elemento independientemente, si no jquery no lo hace
		function validarElemento(elemento){
			$("#main").validate().element(elemento);  
		}
		
		function imprimir(size)	{
			var frm = document.getElementById('main');
			if(size < ${numRegImpresion }){
				frm.target="_blank";
			}				
			frm.operacion.value = 'imprimir';
			frm.submit();
		}
		
		function subStrEntidad(){
			var entidadMediadora = document.getElementById('entidad').value;
			if(document.getElementById('entidad').value.length == 4){
			  entidadMediadora = document.getElementById('entidad').value.substr(1);
			}
			document.getElementById('entidadSubstr').value = entidadMediadora
		}
		
		
</script>
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0" onload="SwitchMenu('sub2');generales.fijarFila()">

<%@ include file="/jsp/common/static/cabecera.jsp"%>
<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

<div id="buttons">
<table width="97%" cellspacing="0" cellpadding="0" border="0">
	<tr>
		<td align="right">
			<!-- PTC-5729 (03.05.2019) ** MODIF TAM, se ocultan los botones Consultar y Limpiar -->
			<!-- <a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a> 
			<a class="bot" id="btnLimpiar" href="javascript:limpiar()">Limpiar</a> --> 
			<a class="bot" id="btnVolver" href="javascript:volver()">Volver</a></td>
	</tr>
</table>
</div>
<!-- Contenido de la pï¿½gina -->
<div class="conten" style="padding: 3px; width: 97%">
<p class="titulopag" align="left">Histórico de Colectivos</p>
<form:form name="main" id="main" action="historicoColectivo.html"
	method="post" commandName="historicoColectivoBean">
	<form:hidden path="id" id="id" />
	<form:hidden path="linea.lineaseguroid" id="lineaseguroid" />
	<input type="hidden" name="operacion" id="operacion" />
	<input type="hidden" name="idHistorico" id="idHistorico" value="${idHistorico}"/>
	<input type="hidden" name="method" id="method" />
	<input type="hidden" name="fechaIni.day" value="">
	<input type="hidden" name="fechaIni.month" value="">
	<input type="hidden" name="fechaIni.year" value="">
	<input type="hidden" name="fechaFin.day" value="">
	<input type="hidden" name="fechaFin.month" value="">
	<input type="hidden" name="fechaFin.year" value="">
	<input type="hidden" name="entidadSubstr" id="entidadSubstr" value="">
	<input type="hidden" name="grupoEntidades" id="grupoEntidades" value="" />
	

	<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
	<%@ include file="/jsp/common/static/avisoErroresLocal.jsp"%>

	<fieldset class="panel2 isrt">
		<legend class="literal">Datos del colectivo</legend>
		<div>
		
		<table width="100%">
			<tr>
				<td class="literal">Entidad: </td>
				<td class="detalI">${listHistoricoColectivos[0].tomador.entidad.nomentidad}</td>
				<td class="literal">Plan: </td>
				<td class="detalI">${listHistoricoColectivos[0].linea.codplan}</td>
				<td class="literal">Línea: </td>
				<td class="detalI">${listHistoricoColectivos[0].linea.codlinea} - ${listHistoricoColectivos[0].linea.nomlinea}</td>
				<td class="literal">Tomador: </td>
				<td class="detalI">${listHistoricoColectivos[0].tomador.id.ciftomador} - ${listHistoricoColectivos[0].tomador.razonsocial}</td>
			</tr>
			<tr>
				<td class="literal">Colectivo: </td>
				<td class="detalI">${listHistoricoColectivos[0].colectivo.idcolectivo}-${listHistoricoColectivos[0].colectivo.dc}</td>
				<td class="literal">Nombre: </td>
				<td class="detalI">${listHistoricoColectivos[0].colectivo.nomcolectivo}</td>
				<td class="literal">E-S Med.: </td>
				<td class="detalI">${listHistoricoColectivos[0].colectivo.subentidadMediadora.id.codentidad} - ${listHistoricoColectivos[0].colectivo.subentidadMediadora.id.codsubentidad}</td>
				<td class="literal">Activo: </td>
				<c:if test="${listHistoricoColectivos[0].activo == '0'}">
					<td class="detalI">No</td>
				</c:if>
				<c:if test="${listHistoricoColectivos[0].activo == '1'}">
					<td class="detalI">Si</td>
				</c:if>
			</tr>
		</table>
		</div>
	</fieldset>
</form:form> <br />

<form name="volver" id="volver" action="colectivo.html"><input
	type="hidden" name="operacion" id="operacion" /><input
	type="hidden" name="recogerColectivoSesion" id="recogerColectivoSesion" />
	<input type="hidden" name="addFiltro" id="addFiltro" value="" /></form>

<!-- Aqui tiene que ir el grid de datos -->
<div id="grid"><!-- Aqui tiene que ir el grid de datos --> <display:table
	requestURI="" id="listaHistoricoColectivos" class="LISTA"
	summary="historicoColectivos" pagesize="${numReg}" sort="list"
	name="${listHistoricoColectivos}" size="${totalListSize}"
	excludedParams="method" style="width:99%"
	decorator="com.rsi.agp.core.decorators.ModelTableDecoratorHistoricoColectivos">

	<display:column class="literal" headerClass="cblistaImg" title="Acciones" 
		property="histColectivoSelec" sortable="false"	style="width:70px;text-align:center" media="html" />
	<display:column class="literal" headerClass="cblistaImg" title="Ent."
		property="colEntidad" sortProperty="tomador.id.codentidad"
		sortable="true" />
	<display:column class="literal" headerClass="cblistaImg" title="Plan"
		property="colPlan" sortProperty="linea.codplan" sortable="true" />
	<display:column class="literal" headerClass="cblistaImg"
		title="L&iacute;nea" property="colLinea" sortProperty="linea.codlinea"
		sortable="true" />
	<display:column class="literal" headerClass="cblistaImg"
		title="Ref. Col." property="colId" sortProperty="referencia"
		sortable="true" />
	<display:column class="literal" headerClass="cblistaImg"
		title="Nombre Colectivo" property="colNombre" sortable="true" />
	<display:column class="literal" headerClass="cblistaImg"
		title="E-S Mediadora" property="colEntMed" sortable="true" />
	<display:column class="literal" headerClass="cblistaImg"
		title="CIF Tomador" property="colCifTom"
		sortProperty="tomador.id.ciftomador" sortable="true" />
	<display:column class="literal" headerClass="cblistaImg"
		title="Fec.Mod" property="colFechaCambio" sortProperty="fechacambio"
		sortable="true" />
	<display:column class="literal" headerClass="cblistaImg"
		title="Usuario" property="colUsuario" sortable="true" />
	<display:column class="literal" headerClass="cblistaImg"
		title="Fec.Efecto" property="colFechaEfecto"
		sortProperty="fechaefecto" sortable="true" />
	<display:column class="literal" headerClass="cblistaImg" title="Activo"
		property="colActivo" sortable="true" />
	<display:column class="literal" headerClass="cblistaImg"
		title="Tipo.Op" property="colTipo" sortable="true" />
    <display:column class="literal" headerClass="cblistaImg"
		title="Envio IBAN" property="colenvIban" sortable="true" />			
</display:table></div>

</div>
<br />
<%@ include file="/jsp/common/static/piePagina.jsp"%>
<%@ include file="/jsp/common/static/overlay.jsp"%>
<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
<%@ include file="/jsp/common/lupas/lupaTomador.jsp"%>
<%@ include file="/jsp/common/lupas/lupaEntidadMediadora.jsp"%>
<%@ include file="/jsp/common/lupas/lupaSubentidadMediadora.jsp"%>

</body>
</html>