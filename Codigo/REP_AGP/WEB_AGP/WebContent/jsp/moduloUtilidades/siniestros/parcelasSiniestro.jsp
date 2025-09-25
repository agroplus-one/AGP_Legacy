<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<fmt:bundle basename="displaytag" >
	<c:set var="numReg">
		<fmt:message key="numElementsPag"/>
	</c:set>
</fmt:bundle>

<html>
<head>
	<title>Datos Complementarios</title>
	
	<%@ include file="/jsp/common/static/metas.jsp"%>
	
	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
	
	<!-- JavaScript,jQery & AJAX -->
	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
	<script type="text/javascript" src="jsp/js/util.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
	<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
	<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
	<script type="text/javascript" src="jsp/js/calendar.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
	<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
	<script type="text/javascript" src="jsp/js/terminos.js"></script>
	<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
	<script type="text/javascript" src="jsp/js/lupaGenericaIN.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	<%@ include file="/jsp/js/generales.jsp"%>
	<script type="text/javascript" src="jsp/js/displayTagAjaxSelectedRow.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
	<script type="text/javascript" src="jsp/moduloUtilidades/siniestros/parcelasSiniestro.js" ></script>
	<%@ include file="/jsp/js/draggable.jsp"%>
	
	<script type="text/javascript">
	
		$(document).ready(function(){
			
			<c:if test="${empty capAsegSiniestrados}">
				$('#botonAlta').hide();
				$('#botonPasarDefinitiva').hide();
			</c:if>
			<c:if test="${modoLectura}">
				$('#botonAlta').hide();
				$('#botonPasarDefinitiva').hide();
				$('#botonCambioMasivoFecha').hide();
				$('#btn_pasar').hide();
				$('#divAplicarMasivo').hide();
			</c:if>

		});
	</script>
	
</head>

<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4');">
	<%@ include file="../../common/static/cabecera.jsp"%>
	<%@ include file="../../common/static/menuGeneral.jsp"%>
	<%@ include file="../../common/static/datosCabeceraTaller.jsp" %>	
	
	
<!-- Botones para alta, imprimir y volver -->
	<div id="buttons">
		<table width="98%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">
					<a class="bot" id="botonVolver" href="javascript:volver();">Volver</a>
					<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>		
					<a class="bot" href="javascript:limpiar();">Limpiar</a>
					<c:if test="${modoLectura != 'true'}">
						<a class="bot" id="botonPasarDefinitiva" href="javascript:PasarDefinitiva();">Enviar</a>		
						<a class="bot" id="botonAlta" href="javascript:alta();">Guardar</a>
					</c:if>
				</td>
			</tr>
		</table>
	</div>
<!-- Contenido de la página -->
	<div class="conten" style="padding: 3px; width: 99%">
		<p class="titulopag" align="left">Alta de Parcelas Siniestradas</p>	
		<!-- Datos de la póliza -->
		<fieldset style="width:99%;">
		<legend class="literal">Datos de la p&oacute;liza</legend>
			<table width="100%" align="center" cellspacing="2">
				<tr>
					<td class="literal" width="75px">Colectivo:</td>
 					<td width="300px" class="detalI">${capitalAsegSiniestradoDV.capAsegSiniestro.parcelaSiniestro.parcela.poliza.colectivo.idcolectivo } - ${capitalAsegSiniestradoDV.capAsegSiniestro.parcelaSiniestro.parcela.poliza.colectivo.nomcolectivo }</td> 
					<td class="literal" width="40px">Plan:</td>
					<td width="40px" class="detalI">${capitalAsegSiniestradoDV.capAsegSiniestro.parcelaSiniestro.parcela.poliza.linea.codplan}</td>
					<td class="literal" width="50px">Línea:</td>
					<td width="300px" class="detalI">${capitalAsegSiniestradoDV.capAsegSiniestro.parcelaSiniestro.parcela.poliza.linea.codlinea } - ${capitalAsegSiniestradoDV.capAsegSiniestro.parcelaSiniestro.parcela.poliza.linea.nomlinea}</td>
  					
				</tr>
				<tr>
					<td class="literal" width="75px">Asegurado:</td>
 					<td width="200px" class="detalI">${capitalAsegSiniestradoDV.capAsegSiniestro.parcelaSiniestro.parcela.poliza.asegurado.nombreCompleto }</td> 
					<td class="literal" width="40px">Póliza:</td>
					<td width="40px" class="detalI">${capitalAsegSiniestradoDV.capAsegSiniestro.parcelaSiniestro.parcela.poliza.referencia }</td>
					<td class="literal" width="50px">Módulo:</td>
					<td width="300px" class="detalI">${capitalAsegSiniestradoDV.capAsegSiniestro.parcelaSiniestro.parcela.poliza.codmodulo}</td>		
				</tr>
			</table>								
		</fieldset>
		
		<!-- MPM 05/07/2012 - Formulario para la acción volver -->
		<form name="formVolver" id="formVolver" action="siniestros.html" method="post">
			<input type="hidden" name="method" id="methodVolver" value="doEdita"/>
			<input type="hidden" name="idSiniestro" id="idSiniestroVolver"/>
			<input type="hidden" name="idPoliza" id="idPolizaVolver"/>
			<input type="hidden" name="modoLectura" id="modoLecturaVolver" value="${modoLectura}"/>
			<input type="hidden" name="fromUtilidades" id="fromUtilidadesVolver" value="${fromUtilidades}"/>
			
		</form>
		
		<form:form name="main3" id="main3" action="parcelasSiniestradas.html" method="post" commandName="capitalAsegSiniestradoDV">			
			<input type="hidden" id="method" name="method" /> 
			<form:hidden path="capAsegSiniestro.parcelaSiniestro.parcela.poliza.linea.lineaseguroid" id="lineaseguroid" />
			<!--  poner como campo oculto el id de la poliza -->			
			<form:hidden path="capAsegSiniestro.parcelaSiniestro.parcela.poliza.idpoliza" id="idPoliza"/>
			<form:hidden path="capAsegSiniestro.parcelaSiniestro.siniestro.id" id="idSiniestro"/>
			<form:hidden path="capAsegSiniestro.parcelaSiniestro.siniestro.estadoSiniestro.descestado" id="estadodSiniestro"/>
			
			<input type="hidden" name="modoLectura" id="modoLectura" value="${modoLectura}"/>
			<input type="hidden" name="fromUtilidades" id="fromUtilidades" value="${fromUtilidades}"/>
			<input type="hidden" name="getParcelasBBDD" id="getParcelasBBDD" value="true"/>
			
			<!-- DAA 25/06/2012 -->
			<!-- Hidden para todos los ids de capitales asegurados -->
			<input type="hidden" id="listaIdsCap" name="listaIdsCap" value="${listaIdsCap}"/>
			<!-- Hidden para los ids de los capitales asegurados marcados -->
			<input type="hidden" id="idsRowsChecked" name="idsRowsChecked" value=""/>
			<!-- Hidden para saber si he marcado el check "marcar todos" -->
			<input type="hidden" id="marcaTodo" name="marcaTodo" value="${marcaTodo}"/>
			
			<input type="hidden" id="operacion" name="operacion" value=""/>
			<input type="hidden" name="idSin" id="idSin"/>
			<input type="hidden" name="idPol" id="idPol"/>
			<input type="hidden" name="pasarADefinitiva" id="pasarADefinitiva"/>
			<input type="hidden" name="altaWs" id="altaWs" value="${altaWs}">
			
			<!-- para la lupa del tipo de capital. Valores "depende" -->
			<input type="hidden" value=${capitalAsegSiniestradoDV.capAsegSiniestro.parcelaSiniestro.parcela.poliza.linea.codlinea}  id="codlinea" />
			<input type="hidden" value=${capitalAsegSiniestradoDV.capAsegSiniestro.parcelaSiniestro.parcela.poliza.linea.codplan}   id="codplan" />	
			<input type="hidden" id="codconcepto" name="codconcepto" value="126"/>
			
			<input type="hidden" value=${capitalAsegSiniestradoDV.capAsegSiniestro.parcelaSiniestro.parcela.poliza.linea.fechaInicioContratacion} id="fechaInicioContratacion" />
						
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			
			<div style="panel2 isrt">
			<fieldset style="width:99%;">
				<legend class="literal">Filtro</legend>				
					<table align="center">
						<tr>
							<td class="literal">Provincia</td>
							<td class="literal">
								<form:input  id="provincia" path="capAsegSiniestro.parcelaSiniestro.codprovincia" cssClass="dato" size="2" maxlength="2" onchange="javascript:lupas.limpiarCampos('desc_provincia','comarca','desc_comarca','termino','desc_termino','subtermino');"/>
								<input class="dato"	id="desc_provincia" name="desc_provincia" size="20" readonly="readonly"  value="${desc_provincia}"/>
								<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Provincia','principio', '', '');"	alt="Buscar Provincia" title="Buscar Provincia" />	
							</td>
							<td class="literal">Comarca</td>
							<td class="literal">
								<form:input  id="comarca" path="capAsegSiniestro.parcelaSiniestro.codcomarca" cssClass="dato" size="2" maxlength="2" onchange="javascript:lupas.limpiarCampos('desc_comarca','termino','desc_termino','subtermino');"/>
								<input class="dato"	id="desc_comarca" name="desc_comarca" size="30" readonly="readonly" value="${desc_comarca}"/>
								<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Comarca','principio', '', '');"	alt="Buscar Comarca" title="Buscar Comarca" />
							</td>
							<td class="literal">Término</td>
							<td class="literal">
								<form:input  id="termino" path="capAsegSiniestro.parcelaSiniestro.codtermino" cssClass="dato" size="3" maxlength="3" onchange="javascript:lupas.limpiarCampos('desc_termino','subtermino');"/>
								<input class="dato"	id="desc_termino" name="desc_termino" size="30" readonly="readonly" value="${desc_termino}"/>
							</td>
							<td class="literal">Subtérmino</td>
							<td class="literal">
								<form:input  id="subtermino" path="capAsegSiniestro.parcelaSiniestro.subtermino" cssClass="dato" size="1" maxlength="1" onchange="this.value=this.value.toUpperCase();"/>
								<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Termino','principio', '', '');"	alt="Buscar Término" title="Buscar Término" />
							</td>
						</tr>
					</table>
				<fieldset style="width: 25%;float:left">
					<legend  class="literal">Identificación Catastral</legend>
					<table align="center">												
						<tr>
							<td class="literal">Polígono</td>
							<td class="literal">
								<form:input  id="poligono" path="capAsegSiniestro.parcelaSiniestro.poligono" cssClass="dato" size="3" maxlength="3"/>
							</td>									
							<td class="literal">Parcela</td>
							<td class="literal">
								<form:input  id="parcela" path="capAsegSiniestro.parcelaSiniestro.parcela_1" cssClass="dato" size="5" maxlength="5"/>
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
								<form:input  id="provSig" path="capAsegSiniestro.parcelaSiniestro.codprovsigpac" cssClass="dato" size="2" maxlength="2"/>
							</td>
							<td class="literal">Term</td>
							<td class="literal">
								<form:input  id="TermSig" path="capAsegSiniestro.parcelaSiniestro.codtermsigpac" cssClass="dato" size="3" maxlength="3"/>
							</td>
							<td class="literal">Agr</td>
							<td class="literal">
								<form:input  id="agrSig" path="capAsegSiniestro.parcelaSiniestro.agrsigpac" cssClass="dato" size="3" maxlength="3"/>
							</td>
							<td class="literal">Zona</td>
							<td class="literal">
								<form:input  id="zonaSig" path="capAsegSiniestro.parcelaSiniestro.zonasigpac" cssClass="dato" size="2" maxlength="2"/>
							</td>
							<td class="literal">Pol</td>
							<td class="literal">
								<form:input  id="polSig" path="capAsegSiniestro.parcelaSiniestro.poligonosigpac" cssClass="dato" size="3" maxlength="3"/>
							</td>									
							<td class="literal">Parc</td>
							<td class="literal">
								<form:input  id="parcSig" path="capAsegSiniestro.parcelaSiniestro.parcelasigpac" cssClass="dato" size="5" maxlength="5"/>
							</td>
							<td class="literal">Rec</td>
							<td class="literal">
								<form:input  id="recSig" path="capAsegSiniestro.parcelaSiniestro.recintosigpac" cssClass="dato" size="5" maxlength="5"/>
							</td>
						</tr>
					</table>
				</fieldset>
				<table align="center" width="100%">
					<tr>
						<td class="literal">Paraje</td>
						<td class="literal">
							<form:input path="capAsegSiniestro.parcelaSiniestro.nomparcela" id="nombre"  size="30" maxlength="40" cssClass="dato" onchange="this.value=this.value.toUpperCase();"/>
						</td>
						<td class="literal">Cultivo</td>
						<td class="literal">
							<form:input  id="cultivo" path="capAsegSiniestro.parcelaSiniestro.codcultivo" cssClass="dato" size="2" maxlength="3" onchange="javascript:lupas.limpiarCampos('desc_cultivo','variedad','desc_variedad');"/>
							<input class="dato"	id="desc_cultivo" name="desc_cultivo" size="22" readonly="readonly" value="${desc_cultivo}"/>
							<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Cultivo','principio', '', '');"	alt="Buscar Cultivo" title="Buscar Cultivo" />
						</td>
						<td class="literal">Variedad</td>
						<td class="literal">
							<form:input  id="variedad" path="capAsegSiniestro.parcelaSiniestro.codvariedad" cssClass="dato" size="2" maxlength="3" onchange="javascript:lupas.limpiarCampos('desc_variedad');"/>
							<input class="dato"	id="desc_variedad" name="desc_variedad" size="22" readonly="readonly" value="${desc_variedad}"/>
							<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Variedad','principio', '', '');"	alt="Buscar Variedad" title="Buscar Variedad" />		
						</td>
						<td class="literal">Tipo capital</td>
						<td class="literal">
							<form:input id="capital" path="capAsegSiniestro.codtipocapital" size="2" maxlength="3" cssClass="dato" onchange="javascript:lupas.limpiarCampos('desc_capital');" />
							<%-- <input type="text" id="capital" name="capital" size="3" maxlength="3" class="dato" onchange="javascript:lupas.limpiarCampos('desc_capital');" tabindex="1" value="${datos.capital }"/> --%>
							<input type="text" class="dato"	id="desc_capital" name="desc_capital" size="22" readonly="true" value="${desc_capital}"/>
							<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('FactoresTipoCapitalIN','principio', '', '');"	alt="Buscar Tipo Capital" title="Buscar Tipo Capital" />							
						</td>
					</tr>
				</table>
			</fieldset>
		</div>
		<div class="literal" style="text-align:right;margin:6px 26px;height:25px">
			<div id="divAplicarMasivo" class="panelInformacion" align="right">
				<table border="0" cellpadding="2" cellspacing="2" align="right">
			        <tr>
			        	<td class="literal">Frutos</td>
			        	<td>
			        		<input type="checkbox" id="frutosCM" name="frutosCM" value="S" class="dato"/>
			        		<input type="hidden" name="valorFrutosCM" id="valorFrutosCM" value="">
			        	</td>
			            <td class="literal">Fecha Recolección:</td>
			            <td>
			            	<input type="hidden" name="inputFechaRec.day" value="">
							<input type="hidden" name="inputFechaRec.month" value="">
							<input type="hidden" name="inputFechaRec.year" value="">
			            	<input type="text" id="inputFechaRec" name="inputFechaRec" size="11" maxlength="10" class="dateITA dato" value="" onblur="if (!ComprobarFecha(this, document.main3, 'Fecha Recolecci&oacute;n')) this.value='';validarElemento(this);">
			            	<input type="button" id="btn_FechaRec" name="btn_FechaRec" class="miniCalendario" style="cursor: pointer;"/>
			            	<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_FechaRec"> *</label>
			            	<label style="display:none" class="campoObligatorio" title="errorFechaRec" id="errorFechaRec"> Formato incorrecto (dd/mm/yyy)</label>
			            </td>
			            <td><a class="bot" href="javascript:aplicarCambios()">Aplicar</a></td>
			        </tr>
	   			</table>
			</div>
		</div>
	<div id="grid">		
	        <display:table requestURI="parcelasSiniestradas.html" id="listParcelasSiniestradas" class="LISTA" summary="ParcelasSiniestradas" 
	        name="${capAsegSiniestrados}" sort="list" pagesize="${numReg }" 
	        decorator="com.rsi.agp.core.decorators.ModelTableDecoratorParcelasSiniestradas" style="width:100%;border-collapse:collapse;" 
	        excludedParams="method listaIdsCap idsRowsChecked" >
	        	<display:column class="literal" headerClass="cblistaImg" title="N&#176;" property="columnaN" style="width:40px;text-align:center" sortable="true" comparator = "com.rsi.agp.core.comparators.TableComparatorHojaNumero"/>
	            <display:column class="literal" headerClass="cblistaImg" title="PRV" property="columnaPRV" style="width:40px;text-align:center" sortable="true" comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico"/>
	            <display:column class="literal" headerClass="cblistaImg" title="CMC" property="columnaCMC" style="width:40px;text-align:center" sortable="true" comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico"/>
	            <display:column class="literal" headerClass="cblistaImg" title="TRM" property="columnaTRM" style="width:40px;text-align:center" sortable="true" comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico"/>
	            <display:column class="literal" headerClass="cblistaImg" title="SBT" property="columnaSBT" style="width:40px;text-align:center" sortable="true" comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico"/>
	           
	            <display:column class="literal" headerClass="cblistaImg" title="CUL" property="columnaCUL" style="width:40px;text-align:center" sortable="true" comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico"/>
	            <display:column class="literal" headerClass="cblistaImg" title="VAR" property="columnaVAR" style="width:40px;text-align:center" sortable="true" comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico"/>
	            <display:column class="literal" headerClass="cblistaImg" title="Id. Cat/SIGPAC" property="idCat" style="width:175px;text-align:center" sortable="true" comparator = "com.rsi.agp.core.comparators.TableComparatorSigPacs"/>
	            <display:column class="literal" headerClass="cblistaImg" title="Paraje" property="columnaParaje" style="width:200px;text-align:center" sortable="true" />
	            <display:column class="literal" headerClass="cblistaImg" title="Superficie" property="columnaSuperf" style="width:40px;text-align:center" sortable="true" comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico"/>
	            <display:column class="literal" headerClass="cblistaImg" title="Tipo Capital" property="columnaCapital" style="width:100px;text-align:center" sortable="true" comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico"/> 	           
	            
	            <display:column class="literal" headerClass="cblistaImg" title="Siniestrada" property="columnaAlta" style="text-align:center;" sortable="true" /> 
	            <display:column class="literal" headerClass="cblistaImg" title="Frutos" property="columnaFrutos" style="width:40px;text-align:center;color:red;" sortable="true"/>	           
	       		<display:column class="literal" headerClass="cblistaImg" title="Fec. Recol." property="columnaFechaRecoleccion" style="width:110px;text-align:center;color:red;" sortable="true" sortProperty="fechaRecoleccion"/>
	        	
	        	<display:column class="literal" headerClass="cblistaImg" title="X" property="checkCambioMasivo" style="width:30px;text-align:center"/>
	        	
	        	<display:setProperty name="paging.banner.some_items_found" value='' />
				<display:setProperty name="paging.banner.one_item_found" value='' />
				<display:setProperty name="paging.banner.all_items_found" value='' />
				    
	        	<display:footer>
	        		<tr style="background-color:#e5e5e5">
	        			<td class="literal" colspan="3">N&#176; Total Parcelas: </td>
	        			<td class="literal" align="left" style="color:green"><%=((java.util.List)request.getAttribute("capAsegSiniestrados")).size()%></td>
	        			<td class="literal" colspan="3">Seleccionados:</td>
	        			<td class="literal" style="color:green"><label id="sel"/></td>
	        			<td class="literal" colspan="6" align="right">Marcar Todos:</td>
	        			<td class="literal"style="width:30px;text-align:center">
	        				<input type="checkbox" id="checkTodos" name="checkTodos" class="dato" onclick="clickCheckTodos(this.checked);"/>
	        			</td>
	        		</tr>
	        	</display:footer>
	        </display:table>		
		</div>
</form:form>
</div>
<%@ include file="/jsp/common/static/piePagina.jsp"%>
<%@ include file="/jsp/common/static/overlay.jsp"%>

<!--                             -->		
<!-- PANEL AVISOS (REUTILIZABLE) -->
<!--                             -->
<div id="popUpAvisos" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
     <!--  header popup -->
	 <div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
	        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
	            Aviso
	        </div>
	        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
	                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
	            <span onclick="hidePopUpPanelAvisos()">x</span>
	        </a>
	 </div>
	 <!--  body popup -->
	 <div class="panelInformacion_content">
			<div id="panelInformacion" class="panelInformacion">
				<div id="txt_mensaje_aviso">sin mensaje.</div>
			</div>
			<div style="margin-top:15px">
			    <a class="bot" href="javascript:hidePopUpPanelAvisos()" title="Cancelar">Aceptar</a>
			</div>
	 </div>
</div>

<%@ include file="/jsp/common/lupas/lupaCultivo.jsp"%>
<%@ include file="/jsp/common/lupas/lupaVariedad.jsp"%>
<%@ include file="/jsp/common/lupas/lupaProvincia.jsp"%>
<%@ include file="/jsp/common/lupas/lupaComarca.jsp"%>
<%@ include file="/jsp/common/lupas/lupaTermino.jsp"%>
<%@ include file="/jsp/common/lupas/lupaTipoCapitalIN.jsp"%>
</body>
</html>