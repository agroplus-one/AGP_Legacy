<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<fmt:bundle basename="displaytag" >
	<c:set var="numReg">
		<fmt:message key="numElementsPag"/>
	</c:set>
</fmt:bundle>

<html>
<head>
	<title>Reduccion de Capital Capitales Asegurados</title>
	
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
	<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
	<script type="text/javascript" src="jsp/js/calendar.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
	<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
	<script type="text/javascript" src="jsp/js/terminos.js"></script>
	<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>	
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	<script type="text/javascript" src="jsp/moduloUtilidades/reduccionCapital/parcelasReduccionCapital.js"></script>
	
	<script language="javascript"> 	
	
	
	
	$(document).ready(function(){

		<c:if test="${empty listCapitalesAsegurados}">
			$('#botonAlta').hide();				
		</c:if>
		<c:if test="${modoLectura}">			
			$('#botonAlta').hide();
			$('#btn_pasar').hide();	
			$("input[type=checkbox]").each(function(){	       			
					$(this).attr('disabled',true);
			});	
			$("input[type=text]").each(function(){		
						
				if($(this).attr('id').substr(0,9)== "prodPost_"){				
					$(this).attr('disabled',true);
				}
			});				
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
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">	
					<a class="bot" id="botonVolver" href="javascript:volver();">Volver</a>
					<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>		
					<a class="bot" href="javascript:limpiar();">Limpiar</a>
					<c:if test="${modoLectura != 'true'}">			
						<!--P0079361 -->
						<a class="bot" id="btnEnviar" name="btnEnviar" href="javascript:enviar();" title="Enviar a Agroseguro">Enviar</a>
						<!--  P0079361 -->    
						<!-- <a class="bot" id="botonAlta" href="javascript:alta();">Guardar</a> -->
						<!--  <a class="bot" id="btn_pasar" name="btn_pasar" href="javascript:pasarDefinitivo();">Pasar Definitivo</a> -->
					</c:if>									
				</td>
			</tr>
		</table>
	</div>
<!-- Contenido de la página -->
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Reducción de Capital Capitales Asegurados</p>	
		<!-- Datos de la Póliza -->
		<fieldset style="width:95%;">
			<legend class="literal">Datos de la p&oacute;liza</legend>
			<table width="100%" align="center" cellspacing="2">
				<tr>
					<td class="literal" width="75px">Colectivo:</td>
 					<td width="300px" class="detalI">${poliza.colectivo.nomcolectivo }</td> 
					<td class="literal" width="40px">Plan:</td>
					<td width="40px" class="detalI">${poliza.linea.codplan }</td>
					<td class="literal" width="50px">Línea:</td>
					<td width="300px" class="detalI">${poliza.linea.codlinea } - ${poliza.linea.nomlinea }</td>
  					
				</tr>
				<tr>
					<td class="literal" width="75px">Asegurado:</td>
 					<td width="200px" class="detalI">${poliza.asegurado.nombreCompleto }</td> 
					<td class="literal" width="40px">Póliza:</td>
					<td width="40px" class="detalI">${poliza.referencia }</td>
					<td class="literal" width="50px">Módulo:</td>
					<td width="300px" class="detalI">${poliza.codmodulo }</td>		
				</tr>
			</table>								
		</fieldset>		
		
		<!-- MPM 05/07/2012 - Formulario para la acción volver -->
		<form name="formVolver" id="formVolver" action="declaracionesReduccionCapital.html" method="post">
			<input type="hidden" name="method" id="methodVolver" value="doEdita"/>
			<input type="hidden" name="origen" id="origenVolver"/>
			<input type="hidden" name="idReduccionCapital" id="idReduccionCapitalVolver"/>
			<input type="hidden" name="idPoliza" id="idPolizaVolver"/>
			<input type="hidden" name="id" id="id"/>
			<input type="hidden" name="vieneDeListadoRedCap" id="vieneDeListadoRedCap" value="${vieneDeListadoRedCap}"/>
			<input type="hidden" name="modoLectura" id="modoLectura" value="${modoLectura}"/>
		</form>
		
		
		<form:form name="main3" id="main3" action="parcelasReduccionCapital.html" method="post" commandName="capitalAseguradoBean">	
			<input type="hidden" id="method" name="method" /> 
			<input type="hidden" name="vieneDeListadoRedCap" id="vieneDeListadoRedCap" value="${vieneDeListadoRedCap}"/>
			<input type="hidden" name="modoLectura" id="modoLectura" value="${modoLectura}"/>
			<input type="hidden" id="redireccion" name="redireccion" value="parcelas"/>

			
			<!--  poner como campo oculto el id de la poliza -->
			<form:hidden path="parcela.reduccionCapital.poliza.idpoliza" id="idPoliza"/>
			<form:hidden path="parcela.reduccionCapital.id" id="idReduccionCapital"/>
			<form:hidden path="parcela.parcela.poliza.linea.lineaseguroid" id="lineaseguroid" />
			<form:hidden path="parcela.parcela.poliza.linea.codlinea" id="codlinea" />
			<input type="hidden" id="fechaInicioContratacion" name="fechaInicioContratacion" value="${fechaInicioContratacion}" />
			<input type="hidden" id="altaSel" name="altaSel" value="${capitalesAlta }"/>			
			<input type="hidden" id="prodSel" name="prodSel" value="${capitalesProdPost}"/>
			<input type="hidden" id="operacion" name="operacion" value=""/>
			<input type="hidden" name="hayCambiosDatosAsegurado" id="hayCambiosDatosAsegurado" value="true" />
						
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>	
			
			<div style="panel2 isrt">
			<fieldset style="width:95%;">
				<legend class="literal">Filtro</legend>	
					<table align="center">						
						<tr>
							<td class="literal" >Provincia</td>
							<td>
								<form:input path="parcela.codprovincia" id="provincia" cssClass="dato width40"  size="2" maxlength="2" tabindex="11" onchange="javascript:lupas.limpiarCampos('desc_provincia','comarca','desc_comarca','termino','desc_termino','subtermino');"/>	
	                            <form:input path="parcela.parcela.termino.provincia.nomprovincia" cssClass="dato"	id="desc_provincia"  size="20" readonly="true"/> 
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Provincia','principio', '', '');"	alt="Buscar Provincia" title="Buscar Provincia" />
							</td>
							<td class="literal" >Comarca</td>
							<td>
								<form:input path="parcela.codcomarca"  id="comarca" cssClass="dato width40"  size="2" maxlength="2" tabindex="12" onchange="javascript:lupas.limpiarCampos('desc_comarca','termino','desc_termino','subtermino');"/>
								<form:input path="parcela.parcela.termino.comarca.nomcomarca" cssClass="dato"	id="desc_comarca"  size="20" readonly="true"/> 
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Comarca','principio', '', '');"	alt="Buscar Comarca" title="Buscar Comarca" />
							</td>
							<td class="literal" >Término</td>
							<td>
								<form:input path="parcela.codtermino"  id="termino" cssClass="dato width40" size="3" maxlength="3" tabindex="13" onchange="javascript:lupas.limpiarCampos('desc_termino','subtermino');"/>
							    <form:input path="parcela.parcela.termino.nomtermino" cssClass="dato" id="desc_termino"  size="30" readonly="true"/> 
							</td>
							<td class="literal" >Subtr.</td>
							<td>
								<form:input path="parcela.subtermino"  id="subtermino" cssClass="dato width40" size="1" maxlength="1" tabindex="14" onchange="this.value=this.value.toUpperCase();"/>		
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Termino','principio', '', '');"	alt="Buscar Termino" title="Buscar Termino" />
							</td>
						</tr>
					</table>
					<fieldset style="width: 20%;float:left">
						<legend class="literal">Identificación Catastral</legend>
						<table align="center">
							<tr>
								<td class="literal" >Pol</td>
								<td class="literal">
									<form:input path="parcela.poligono" id="txt_poligono" cssClass="dato" size="3" maxlength="3" tabindex="9"/>
								</td>
								<td class="literal" >Par</td>
								<td class="literal">
									<form:input path="parcela.parcela_1" id="txt_parcela" cssClass="dato" size="5" maxlength="5" tabindex="10" />
								</td>
							</tr>
						</table>
					</fieldset>
					<fieldset>
							<legend class="literal">SIGPAC</legend>
							<table align="center">
								<tr>
									<td class="literal">Prov</td>
									<td class="literal">
										 <form:input path="parcela.codprovsigpac" id="txt_provsigpac" cssClass="dato" size="2" maxlength="2" tabindex="16"/>
									</td>
									<td class="literal">Term</td>
									<td class="literal">
										<form:input path="parcela.codtermsigpac"  id="txt_termsigpac" cssClass="dato" size="3" maxlength="3" tabindex="17"/>
									</td>
									<td class="literal">Agr</td>
									<td class="literal">
										<form:input path="parcela.agrsigpac"  id="txt_agrsigpac"  cssClass="dato" size="3" maxlength="3" tabindex="18"/>
									</td>
									<td class="literal">Zona</td>
									<td class="literal">
										<form:input path="parcela.zonasigpac"  id="txt_zonasigpac"cssClass="dato" size="2" maxlength="2" tabindex="19"/>
									</td>
									<td class="literal">Pol</td>
									<td class="literal">
										<form:input path="parcela.poligonosigpac"  id="txt_polsigpac"  cssClass="dato" size="3" maxlength="3" tabindex="20"/>
									</td>
									<td class="literal">Parc</td>
									<td class="literal">
										<form:input path="parcela.parcelasigpac"  id="txt_parcsigpac" cssClass="dato" size="5" maxlength="5" tabindex="21"/>
									</td>
									<td class="literal">Rec</td>
									<td class="literal">
										<form:input path="parcela.recintosigpac"  id="txt_recsigpac"  cssClass="dato" size="5" maxlength="5" tabindex="22"/>
									</td>
								</tr>
							</table>
					</fieldset>
					
					<table align="center">
						<tr>
							<td class="literal" >Nombre Parcela</td>
							<td>
								<form:input path="parcela.nomparcela" id="txt_nombreParcela" cssClass="dato" cssStyle="width:200px" size="20" maxlength="20" tabindex="15" onchange="this.value=this.value.toUpperCase();"/>
							</td>
							<td class="literal" >Cultivo</td>
							<td>
								<form:input path="parcela.codcultivo" id="cultivo" cssClass="dato width40" size="3" maxlength="3" tabindex="24" onchange="javascript:lupas.limpiarCampos('desc_cultivo','variedad','desc_variedad');"/>
								<form:input path="parcela.parcela.variedad.cultivo.descultivo" cssClass="dato" id="desc_cultivo"  size="20" readonly="true"/> 
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Cultivo','principio', '', '');"	alt="Buscar Cultivo" title="Buscar Cultivo" />
							</td>
							<td class="literal" >Variedad</td>
							<td>
								<form:input path="parcela.codvariedad" id="variedad" cssClass="dato width40" size="3" maxlength="3" tabindex="25" onchange="javascript:lupas.limpiarCampos('desc_variedad');"/>
								<form:input path="parcela.parcela.variedad.desvariedad" cssClass="dato" id="desc_variedad"  size="25" readonly="true"/> 
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Variedad','principio', '', '');"	alt="Buscar Variedad" title="Buscar Variedad" />
							</td>
						</tr>
					</table>
					<table align="center">
						<tr>
							<td class="literal" >T. Capital</td>
							<td>
								<form:input  id="capital" path="codtipocapital" cssClass="dato" size="3" maxlength="3" onchange="javascript:lupas.limpiarCampos('desc_capital');"/>
								<form:input path="" id="desc_capital" cssClass="dato" size="20" maxlength="25" readonly="true"/>
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('TipoCapital','principio', '', '');"	alt="Buscar Tipo Capital" title="Buscar Tipo Capital" />
							</td>
							<td class="literal" >Superficie</td>
							<td>
								 <form:input path="superficie" id="txt_superficie" cssClass="dato" cssStyle="width:85px" size="11" maxlength="11" tabindex="26"/>
							</td>
							<td class="literal" >Prod. Antes Daños</td>
							<td>
								<form:input  id="prod" path="prod" cssClass="dato" size="8" maxlength="8"/>
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
			<br/>
			<div id="grid">		
		        <display:table requestURI="parcelasReduccionCapital.html" id="listCapitalesAsegurados" class="LISTA" 
		        summary="CapitalesAsegurados" name="${listCapitalesAsegurados}" sort="list" pagesize="${numReg }"
		        decorator="com.rsi.agp.core.decorators.ModelTableDecoratorParcelasReduccionCapital" style="width:100%;border-collapse:collapse;" 
		        excludedParams="method">     
		        	<display:column class="literal" headerClass="cblistaImg" title="Nº" property="columnaN" style="width:40px;text-align:center"/>
		            <display:column class="literal" headerClass="cblistaImg" title="PRV" property="columnaPRV" style="width:40px;text-align:center"/>
		            <display:column class="literal" headerClass="cblistaImg" title="CMC" property="columnaCMC" style="width:40px;text-align:center"/>
		            <display:column class="literal" headerClass="cblistaImg" title="TRM" property="columnaTRM" style="width:40px;text-align:center"/>
		            <display:column class="literal" headerClass="cblistaImg" title="SBT" property="columnaSBT" style="width:40px;text-align:center"/>
		           
		            <display:column class="literal" headerClass="cblistaImg" title="CUL" property="columnaCUL" style="width:40px;text-align:center"/>
		            <display:column class="literal" headerClass="cblistaImg" title="VAR" property="columnaVAR" style="width:40px;text-align:center"/>
		            <display:column class="literal" headerClass="cblistaImg" title="Id. Cat/SIGPAC" property="idCat" style="width:175px;text-align:center" sortable="true" comparator="com.rsi.agp.core.comparators.TableComparatorSigPacs"/>          
		            <display:column class="literal" headerClass="cblistaImg" title="Tipo Capital" property="columnaTCap" style="width:100px;text-align:center"/> 	           
		            <display:column class="literal" headerClass="cblistaImg" title="Superficie" property="columnaSuperf" style="width:40px;text-align:center"/>
		            <display:column class="literal" headerClass="cblistaImg" title="Precio" property="columnaPrecio" style="width:40px;text-align:center"/>
		          	<display:column class="literal" headerClass="cblistaImg" title="Prod." property="columnaProd" style="width:40px;text-align:center"/>	            	          	
		            
		            <display:column class="literal" headerClass="cblistaImg" title="Alta" property="columnaAlta" style="width:30px;text-align:center"/> 	          	       		
		           	<display:column class="literal" headerClass="cblistaImg" title="Prod. Post." property="columnaProdPost" style="width:110px;text-align:center"/>
		        	
		        	<display:setProperty name="paging.banner.some_items_found" value='' />
					<display:setProperty name="paging.banner.one_item_found" value='' />
					<display:setProperty name="paging.banner.all_items_found" value='' />
						        	
		        	<display:footer>
		        		<tr style="background-color:#e5e5e5">
		        			<td class="literal" colspan="3">Nº Total Parcelas: </td>
		        			<td class="literal" style="color:green">
		    					<%=((java.util.List)request.getAttribute("listCapitalesAsegurados")).size()%>	
		        			</td>
		        			<td class="literal" colspan="2">Seleccionados:</td>
		        			<td class="literal" style="color:green"><label id="sel"/></td>	
		        			<td class="literal"></td>	
		        			<td class="literal"></td>	        			
		        			<td class="literal"></td>	        			
		        			<td class="literal" colspan="2">Marcar Todos:</td>
		        			<td class="literal" style="width:30px;text-align:center;">
		        				<input type="checkbox" id="selTodos" name="selTodos" class="dato" onclick="this.checked  ? seleccionar_checks() : '' "/>
		        			</td>
		        			<td></td>
		        		</tr>
		        	</display:footer>
		        </display:table>		
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

</body>
</html>