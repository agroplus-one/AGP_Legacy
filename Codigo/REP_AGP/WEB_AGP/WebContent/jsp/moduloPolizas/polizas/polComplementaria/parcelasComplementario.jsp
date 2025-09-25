<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<fmt:bundle basename="displaytag" >
	<c:set var="numReg">
		<fmt:message key="numElementsPag"/>
	</c:set>
</fmt:bundle>

<html>
<head>
	<title>Parcelas Complementario</title>
	
	 <%@ include file="/jsp/common/static/metas.jsp"%>
	 
	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
	
	<!-- JavaScript,jQery & AJAX -->
	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
	<script type="text/javascript" src="jsp/js/util.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
	<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
	<script type="text/javascript" src="jsp/js/terminos.js"></script>	
	<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
	<script type="text/javascript" src="jsp/moduloUtilidades/incrementoProduccionParcelaComplementaria.js"></script>
	<%@ include file="/jsp/js/draggable.jsp"%>
	<script type="text/javascript" charset="ISO-8859-1">
	
		$(function(){
			$("#grid").displayTagAjax();
			}).ajaxSend(function(){
			}).ajaxComplete(function(){
					
		    if(document.getElementById("parcelasCpl") != null){
		     	
		    	var frm = document.getElementById('main3');
		     	
		     	frm.selTodos.checked=false;
		     	
		     	if(frm.marcarTodos.value=="true"){
		     		marcar_todos();
		    	}
		    	else {
		    		if ($('#listaIds').val() != ''){
		    			check_checks($('#listaIds').val());
		    		}
		    		else{
		    			desmarcar_todos();
		    		}
		    	}
		    	
		    	if (frm.incrementoOK.value=="true"){
		     		desmarcar_todos();
		     	}
		    }	
				
		});
		
		$(document).ready(function(){
			var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
		    document.getElementById("main3").action = URL;  
		
			//POLIZA MODO LECTURA
			/// mejora 112 Angel 01/02/2012 añadida la opción de ver la póliza sin opción a editarla también con estado grabación definitiva
			//var modLec =  $('#modoLectura').val();
			
			//if(${capitalAseguradoBean.parcela.poliza.estadoPoliza.idestado} == 8 || ${capitalAseguradoBean.parcela.poliza.estadoPoliza.idestado} == 3)
			if ($('#modoLectura').val() == 'modoLectura'){
				$("input[type=checkbox]").each(function() { 
					$(this).attr('disabled',true);
				});
				
				$('#incrGen').attr('disabled',true);
				$('#btnIncrementar').hide();
				//$('#btnGrabar').html('Continuar');
				$('#btnGrabar').hide();
				$('#btnContinuar').show();
			}else{
				$('#btnGrabar').show();
				$('#btnIncrementar').show();
				//$('#btnContinuar').show();
			}
		});

		function trunc(num, ndec) { 
			  var fact = Math.pow(10, ndec); // 10 elevado a ndec 
			
			  /* Se desplaza el punto decimal ndec posiciones, se trunca el número y se vuelve a colocar 
			    el punto decimal en su sitio. */ 
			  return (parseInt(num * fact) / fact).toFixed(2); 
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
	    	$('#methodPrint').val('');
	    	$('#method').val('');
	    	consultar();
		}
		function  consultar(){
			var frm = document.getElementById('main3');
			frm.method.value='doConsulta';
			frm.marcarTodos.value="";
			frm.listaIds.value= "";
			
			//Para evitar problemas cuando el listado no tiene registros, compruebo que exista el checkbox
			if (frm.selTodos != undefined)
				frm.selTodos.checked=false;
			
			$('#main3').attr('target', '');
			$('#main3').submit();
		}
		function volver(){
			if($('#vieneDeUtilidades').val() == "true"){
				$(window.location).attr('href', 'utilidadesPoliza.html?operacion=volver&rand='+encodeURI(parseInt(Math.random()*99999999) + "_" + (new Date).getTime()));
			}else{
				$(window.location).attr('href', 'seleccionPoliza.html?rand='+encodeURI(parseInt(Math.random()*99999999) + "_" + (new Date).getTime()));
			}	
    	}
		function guardar(){
			var frm = document.getElementById('main3');
			frm.method.value='doGuardar';
			$('#panelAlertas').hide();
	    	$.blockUI.defaults.message = '<h4>Validando los datos de la Póliza.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	    	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } }); 
			$('#main3').attr('target', '');
			$('#main3').submit();
		}
		function coberturas(){
			var frm = document.getElementById('main3');
			frm.method.value='doCoberturas';
			$('#main3').attr('target', '');
			$('#main3').submit();
		}
		function imprimir(){
			var frm = document.getElementById('main3');
			frm.method.value='doImprimir';			
			$('#main3').attr('target', '_blank');
			$('#main3').submit();
		}
		
		function imprimirListadoParcelas(formato) {
	    	
			var frm = document.getElementById('main3');
			frm.method.value='doInformeListadoParcelas';
			frm.marcarTodos.value="";
			frm.listaIds.value= "";
			frm.formato.value=formato;
			
			//Para evitar problemas cuando el listado no tiene registros, compruebo que exista el checkbox
			if (frm.selTodos != undefined)
				frm.selTodos.checked=false;
			
			$('#main3').attr('target', '');
			$('#main3').submit();
			
	    }

		function continuar (){
			$('#consultaDetallePoliza').submit();
		
		}
		function eligeMenu(){
			
			if($('#vieneDeUtilidades').val() == 'true' && $('#modoLectura').val() == 'modoLectura'){
			 	SwitchMenu('sub4');
			 }else{
			 	SwitchMenu('sub3');
			 }
		 }
		
	</script>
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="eligeMenu();">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0" align="center">
			<tr>
				<td align="left">
					<a class="bot" id="btnConsultar"  href="javascript:consultar();">Consultar</a>				
					<a class="bot" id="btnLimpiar"  href="javascript:limpiar();">Limpiar</a>	
				</td>
				<td align="right">			
					<a class="bot" id="btnImprimir"  href="javascript:imprimir();">Imprimir</a>				
					<a class="bot" id="btnCoberturas"  href="javascript:coberturas();">Coberturas</a>				
					<a class="bot" id="btnVolver"  href="javascript:volver();">Volver</a>				
					<a class="bot" id="btnGrabar"  href="javascript:guardar();" style="display:none">Grabar</a>	
					<a class="bot" id="btnContinuar" style="display:none" href="javascript:continuar();">Continuar</a>				
				</td>
			</tr>
		</table>
	</div>
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Parcelas Complementario</p>
		<!-- Datos de la póliza -->
		<fieldset style="width:95%">
		<legend class="literal">Datos de la p&oacute;liza</legend>
			<table width="90%" align="center" cellspacing="2">
				<tr>
					<td class="literal" width="75px">Colectivo:</td>
 					<td width="300px" class="detalI">${capitalAseguradoBean.parcela.poliza.colectivo.idcolectivo } - ${capitalAseguradoBean.parcela.poliza.colectivo.nomcolectivo }</td> 
					<td class="literal" width="40px">Plan:</td>
					<td width="40px" class="detalI">${capitalAseguradoBean.parcela.poliza.linea.codplan}</td>
					<td class="literal" width="50px">Línea:</td>
					<td width="300px" class="detalI">${capitalAseguradoBean.parcela.poliza.linea.codlinea } - ${capitalAseguradoBean.parcela.poliza.linea.nomlinea}</td>
  					
				</tr>
				<tr>
					<td class="literal" width="75px">Asegurado:</td>
 					<td width="300px" class="detalI">${capitalAseguradoBean.parcela.poliza.asegurado.nombreCompleto }</td> 
					<td class="literal" width="40px">Póliza:</td>
					<td width="40px" class="detalI">${capitalAseguradoBean.parcela.poliza.referencia }</td>
					<td class="literal" width="50px">Módulo:</td>
					<td width="300px" class="detalI">${capitalAseguradoBean.parcela.poliza.codmodulo}</td>		
				</tr>
			</table>								
		</fieldset>				
		<form name="consultaDetallePoliza" id="consultaDetallePoliza" action="consultaDetallePoliza.html" method="post">
				<input type="hidden" name="method" id="method_consulta" value="doVerImportesCpl"/>
				<input type="hidden" name="idpoliza" id="idpoliza_consulta" value="${idPoliza}"/>
				<input type="hidden" name="modoLectura" id="modoLectura_consulta" value="${modoLectura}"/>
				<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades_consulta" value="${vieneDeUtilidades}"/>
				<input type="hidden" id="modoLectura" name="modoLectura" value="${modoLectura}"/>
			</form>
		<form:form name="main3" id="main3" action="polizaComplementaria.html" method="post" commandName="capitalAseguradoBean" >
			<form:hidden path="parcela.poliza.linea.lineaseguroid" id="lineaseguroid" />
			<form:hidden path="parcela.poliza.idpoliza" id="idpoliza" />
			<form:hidden path="parcela.poliza.referencia" id="refPol" />
			<form:hidden path="parcela.poliza.linea.codlinea" id="codlinea" />
			<input type="hidden" id="fechaInicioContratacion" name="fechaInicioContratacion" value="${fechaInicioContratacion}" />
			<input type="hidden" id="method" name="method"/>
			
			<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${vieneDeUtilidades}"/>			
			<input type="hidden" id="modoLectura" name="modoLectura" value="${modoLectura}"/>
			<!-- MPM - 09-05-12 -->
			<input type="hidden" id="listaIdCapAseg" name="listaIdCapAseg" value="${listaIdCapAseg}"/>
			<input type="hidden" id="listaCapAseg" name="listaCapAseg" value="${listCapAseg}"/>
			<input type="hidden" id="tipoInc" name="tipoInc" value=""/>			
			<input type="hidden" id="incrGen" name="incrGen" value=""/> 
			<input type="hidden" id="listaIds" name="listaIds" value="${listaIds}"/>	
			<input type="hidden" id="marcarTodos" name="marcarTodos" value="${marcarTodos}"/> 
			<input type="hidden" id="guardoIds" name="guardoIds" value="${guardoIds}"/> 
			<input type="hidden" id="incrementoOK" name="incrementoOK" value="${incrementoOK}"/>
			<input type="hidden" name="modoLecturaCpl" id="modoLecturaCpl" value="${modoLectura}"/>
			<!-- Impresión -->
			<input type="hidden" name="formato" value=""/>
			
 			<input type="hidden" name="idsRowsChecked" id="idsRowsChecked"/> 
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			
		<div style="panel2 isrt">
			<fieldset style="width:95%;">
				<legend class="literal">Filtro</legend>				
						<table align="center">
								<tr>
									<td class="literal">Provincia</td>
									<td class="literal">
										<form:input  id="provincia" path="parcela.termino.id.codprovincia" cssClass="dato" size="2" maxlength="2" onchange="javascript:lupas.limpiarCampos('desc_provincia','comarca','desc_comarca','termino','desc_termino','subtermino');"/>
										<input class="dato"	id="desc_provincia" name="desc_provincia" size="18" readonly="readonly"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Provincia','principio', '', '');"	alt="Buscar Provincia" title="Buscar Provincia" />	
									</td>
									<td class="literal">Comarca</td>
									<td class="literal">
										<form:input  id="comarca" path="parcela.termino.id.codcomarca" cssClass="dato" size="2" maxlength="2" onchange="javascript:lupas.limpiarCampos('desc_comarca','termino','desc_termino','subtermino');"/>
										<input class="dato"	id="desc_comarca" name="desc_comarca" size="18" readonly="readonly"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Comarca','principio', '', '');"	alt="Buscar Comarca" title="Buscar Comarca" />
									</td>
									<td class="literal">Término</td>
									<td class="literal">
										<form:input  id="termino" path="parcela.termino.id.codtermino" cssClass="dato" size="3" maxlength="3" onchange="javascript:lupas.limpiarCampos('desc_termino','subtermino');"/>
										<input class="dato"	id="desc_termino" name="desc_termino" size="24" readonly="readonly"/>
									</td>
									<td class="literal">Subtérmino</td>
									<td class="literal">
										<form:input  id="subtermino" path="parcela.termino.id.subtermino" cssClass="dato" size="1" maxlength="1" onchange="this.value=this.value.toUpperCase();"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Termino','principio', '', '');"	alt="Buscar Término" title="Buscar Término"  />
									</td>
								</tr>
						</table>
						<fieldset style="width: 20%;float:left">
							<legend  class="literal">Identificación Catastral</legend>
							<table align="center">												
									<tr>
										<td class="literal">Polígono</td>
										<td class="literal">
											<form:input  id="poligono" path="parcela.poligono" cssClass="dato" size="3" maxlength="3"/>
										</td>									
										<td class="literal">Parcela</td>
										<td class="literal">
											<form:input  id="parcela" path="parcela.parcela" cssClass="dato" size="5" maxlength="5"/>
										</td>
									</tr>
							</table>
						</fieldset>
						<fieldset>
							<legend  class="literal">SIGPAC</legend>
							<table align="center">
										<tr>
											<td class="literal">Prov</td>
											<td class="literal">
												<form:input  id="provSig" path="parcela.codprovsigpac" cssClass="dato" size="2" maxlength="2"/>
											</td>
											<td class="literal">Term</td>
											<td class="literal">
												<form:input  id="TermSig" path="parcela.codtermsigpac" cssClass="dato" size="3" maxlength="3"/>
											</td>
											<td class="literal">Agr</td>
											<td class="literal">
												<form:input  id="agrSig" path="parcela.agrsigpac" cssClass="dato" size="3" maxlength="3"/>
											</td>
											<td class="literal">Zona</td>
											<td class="literal">
												<form:input  id="zonaSig" path="parcela.zonasigpac" cssClass="dato" size="2" maxlength="2"/>
											</td>
											<td class="literal">Pol</td>
											<td class="literal">
												<form:input  id="polSig" path="parcela.poligonosigpac" cssClass="dato" size="3" maxlength="3"/>
											</td>									
											<td class="literal">Parc</td>
											<td class="literal">
												<form:input  id="parcSig" path="parcela.parcelasigpac" cssClass="dato" size="5" maxlength="5"/>
											</td>
											<td class="literal">Rec</td>
											<td class="literal">
												<form:input  id="recSig" path="parcela.recintosigpac" cssClass="dato" size="5" maxlength="5"/>
											</td>
										</tr>
							</table>
						</fieldset>
						<table align="center">
							<tr>
								<td class="literal">Nombre Parcela</td>
								<td class="literal">
									<form:input path="parcela.nomparcela" id="nombre"  size="40" maxlength="40" cssClass="dato" onchange="this.value=this.value.toUpperCase();"/>
								</td>
								<td class="literal">Cultivo</td>
								<td class="literal">
									<form:input  id="cultivo" path="parcela.codcultivo" cssClass="dato" size="3" maxlength="3" onchange="javascript:lupas.limpiarCampos('desc_cultivo','variedad','desc_variedad');"/>
									<input class="dato"	id="desc_cultivo" name="desc_cultivo" size="23" readonly="readonly"/>
									<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Cultivo','principio', '', '');"	alt="Buscar Cultivo" title="Buscar Cultivo" />
								</td>
								<td class="literal">Variedad</td>
								<td class="literal">
									<form:input  id="variedad" path="parcela.codvariedad" cssClass="dato" size="3" maxlength="3" onchange="javascript:lupas.limpiarCampos('desc_variedad');"/>
									<input class="dato"	id="desc_variedad" name="desc_variedad" size="23" readonly="readonly"/>
									<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Variedad','principio', '', '');"	alt="Buscar Variedad" title="Buscar Variedad" />		
								</td>
							</tr>
						</table>
						<table>
							<tr>
								<td class="literal">Tipo Capital</td>
								<td class="literal">
									<form:input path="tipoCapital.codtipocapital" id="capital" size="2" maxlength="2" cssClass="dato" onchange="javascript:lupas.limpiarCampos('desc_capital');"/>
									<input class="dato"	id="desc_capital" size="25" readonly="readonly"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('TipoCapital','principio', '', '');"	alt="Buscar Tipo Capital" title="Buscar Tipo Capital" />	
								</td>
								<td class="literal">Superficie</td>
								<td class="literal">
									<form:input path="superficie"   size="10"  maxlength="8" id="superficie" cssClass="dato"/>
								</td>
								<td class="literal">Prod. Ant</td>
								<td class="literal">
									<form:input path="produccion"  size="10"  maxlength="8"  id="prodAnt" cssClass="dato"/>
								</td>
								<td class="literal">Estado</td>
								<td class="literal">
									<form:select path="altaencomplementario"  id="estado" cssClass="dato">
										<form:option value="">--Seleccione una opción--</form:option>
										<form:option value="S">ALTA</form:option>
									</form:select>
								</td>
							</tr>
						</table>
			</fieldset>
		</div>
		
		
		
		<div class="literal" style="text-align:right;margin:6px 26px;height:32px">
		        <!-- [inicio]Miguel 31-1-2012  -->

		    	<div id="camposIncremento" style="color:#626262;font-size:11px;font-family: verdana;font-weight:bold">

                   <div style="float:right"><a class="bot" id="btnIncrementar"  href="javascript:incrementarGeneral('false');" style="display:none">Incrementar</a></div>
                    
                    <div id="incrementoKilosHa" style="float:right; width:250px;">
		        		<span>Kilos totales por Ha</span>
		        		<input type="text" id="txt_incrKilosHaGe" size="10" class="dato" readonly="readonly"/>
		        		<input type="radio" name="incr2" value="kha" onchange="onchange_incrGe('kha')"  />
		    		</div>
		    		
		    		<div id="incrementoKg" style="float:right; width:160px;">
		        		<span>Kg/Ha</span>
		        		<input type="text" id="txt_incrHaGe" size="10" class="dato"/>
		        		<input type="radio" name="incr2" value="ha" checked onchange="onchange_incrGe('ha')"  />
		    		</div>
		    
		    		<div id="incrementoHa" style="float:right; width:160px;">
		        		<span>Kg/Pa</span>
		        		<input type="text" id="txt_incrPaGe" size="10" class="dato" readonly="readonly"/>
		        		<input type="radio" name="incr2" value="pa" onchange="onchange_incrGe('pa')"  />
		    		</div>
					
	    			<div style="float:right;padding:8px 10px 0 0">Incremento General (seleccionados)</div>
	
			    </div>
			
			

			<!-- [inicio]Miguel 31-1-2012  -->
		</div>
		
		
		
		<div id="grid">
			<display:table requestURI="" class="LISTA" summary="parcelasCpl" 
					pagesize="${numReg}" sort="list" name="${listCapAseg}" id="parcelasCpl" excludedParams="method"
					decorator="com.rsi.agp.core.decorators.ModelTableDecoratorParcelasComplementario" 
					style="width:100%;border-collapse:collapse;">
					
					<c:if test="${modoLectura != 'modoLectura'}">
						<display:column class="accionesDisplayTagColumn" headerClass="cblistaImg" title="" property="admActions" sortable="false" style="width:30px;text-align:center" />
					</c:if>
					<display:column class="literal" headerClass="cblistaImg" title="N&#176;"   		property="numero"        style="width:50px;"  sortable="true" comparator="com.rsi.agp.core.comparators.TableComparatorHojaNumero"/>
					<display:column class="literal" headerClass="cblistaImg" title="PRV"            property="codprovincia"  style="width:50px;"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="CMC"            property="codcomarca"    style="width:50px;"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="TRM"            property="codtermino"    style="width:50px;"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="SBT"            property="codsubtermino" style="width:50px;"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="CUL"            property="codCultivo"    style="width:50px;"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="VAR"            property="codVariedad"   style="width:50px;"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="Id. Cat/SIGPAC" property="idCat"         style="width:150px;" sortable="true" comparator="com.rsi.agp.core.comparators.TableComparatorSigPacs"/>
					<display:column class="literal" headerClass="cblistaImg" title="Nombre Parcela" property="nomPar"        style="width:180px;" sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="T.Capital"      property="tcapital"      style="width:150px;" sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="Superficie"     property="superf"        style="width:85px;"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="Prod."          property="produccion"    style="width:55px;"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="Precio"         property="precio"        style="width:60px;"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="Sel."           property="alta"          style="width:20px;"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="Increm."     property="incremento"    style="width:100px;color:red;"  sortable="false"/>
					
					<display:setProperty name="paging.banner.some_items_found" value='' />
					<display:setProperty name="paging.banner.one_item_found" value='' />
					<display:setProperty name="paging.banner.all_items_found" value='' />
						        	
		        	<display:footer>
		        		<tr style="background-color:#e5e5e5">
		        			<td class="literal" colspan="3">Nº Total Parcelas: </td>
		        			<td class="literal" style="color:green"><%=((java.util.List)request.getAttribute("listCapAseg")).size()%></td>
		        			<td class="literal" colspan="3">Seleccionados:</td>
		        			<td class="literal" style="color:green"><label id="sel"/></td>   
		        			<td class="literal">Total superficie: </td>	
		        			<td class="literal" style="color:green">${superficieTotalComp}</td>	
		        			<td class="literal"></td>	
		        			<td class="literal"></td>	
		        			<td class="literal" colspan="2">Marcar Todos:</td>	
		        			<td class="literal"style="width:30px;text-align:left">
		        				<input type="checkbox" id="selTodos" name="selTodos" class="dato" onclick="this.checked  ? marcar_todos() : desmarcar_todos() "/>
		        			</td>
		        			<td></td>
		        		</tr>
		        	</display:footer>
			</display:table>
			<div style="width:20%;text-align:center;margin: 0 auto;" id="divImprimir">
	        	 <a style="font-family: tahoma, verdana, arial;color: #626262;font-size:11px;">Imprimir</a>	
				 <a id="btnImprimirExcel" style="text-decoration:none;" href="javascript:imprimirListadoParcelas('xls')">
				 	<img src="jsp/img/jmesa/excel.gif"/>
			 	</a>
			</div>
		</div>
		</form:form>
	</div>
		
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
	<%@ include file="/jsp/common/lupas/lupaCultivo.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaVariedad.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaProvincia.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaComarca.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaTermino.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaTipoCapital.jsp"%>
	
	<!--  popup modificarPorcentajes -->
		<div id="visorIncremento" class="wrapper_popup" style="width: 55%; top: 150px; left: 25%;">
		
		    <div class="header-popup">
		        <div class="title_popup">Incremento Producción</div>
		        <a class="close_botton_popup"><span onclick="cerrarIncremento()">x</span></a>
		    </div>
		    
		     <div id="modificarPorcentajes_popup_error" class="literal" style="color:red;display:none;text-align:center">
		     	 Incremento de la produción demasiado grande
		    </div>
		    
			<div class="content_popup">
			
			    <div id="sms_error_incr" name="sms_error_incr" class="errorForm_cm" style="margin-bottom:10px"></div>
			    <%@ include file="/jsp/moduloPolizas/polizas/polComplementaria/camposIncremento.jsp"%>

			</div>
		</div>
	
</body>
</html>	