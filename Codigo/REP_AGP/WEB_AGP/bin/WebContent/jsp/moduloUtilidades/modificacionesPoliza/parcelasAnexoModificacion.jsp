<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<fmt:bundle basename="displaytag" >
	<c:set var="numReg"><fmt:message key="numElementsPag"/></c:set>
</fmt:bundle>

<fmt:bundle basename="agp" >
	<c:set var="numEle"><fmt:message key="visores.numElements"/></c:set>
</fmt:bundle>

<html>
<head>
	<title>Parcelas Anexo Modificaci&oacute;n</title>
	
	<%@ include file="/jsp/common/static/metas.jsp"%>
	
	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
	
	<!-- JavaScript,jQery & AJAX -->
	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
	<script type="text/javascript" src="jsp/js/util.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
	<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
    <script type="text/javascript" src="jsp/js/additional-methods.js"></script>
    <script type="text/javascript" src="jsp/js/terminos.js"></script>	
    <script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
    <script type="text/javascript" src="jsp/js/lupaGenericaIN.js"></script>
    <script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	<script type="text/javascript" src="jsp/moduloUtilidades/modificacionesPoliza/parcelasAnexoModificacion.js"></script>
	
    <script>
    
    $(document).ready(function(){
   
    	var item="${itemCombo}";
    	
    	if(item == "T") $("#parcela.tipomodificacion").val("T");
    	if(item == "A") $("#parcela.tipomodificacion").val("A");
    	if(item == "M") $("#parcela.tipomodificacion").val("M");
    	if(item == "B") $("#parcela.tipomodificacion").val("B");
    	
    	// --------------------------------------------------
    	//                 control de checks
    	// --------------------------------------------------
    	check_checks($('#idsRowsChecked').val());
    	$('#sel').text(numero_check_seleccionados());
    	checkTodos();
    	
		//Para ocultar el icono de exportar si la lista de parcelas es vacía
		<c:if test="${empty listaParcelasModificadas}">					
			$('#divImprimir').hide(); 									
		</c:if>
    });
    
    $(function(){
    	<c:forEach items="${listaParcelasModificadas}" var = "capitalAsegurado" varStatus="status">
    		estadosCapitalesAsegurados[${status.index}]    = new Array(3);
    		estadosCapitalesAsegurados[${status.index}][0] = "${capitalAsegurado.parcela.id}";
			estadosCapitalesAsegurados[${status.index}][1] = "${capitalAsegurado.parcela.tipomodificacion}";
			estadosCapitalesAsegurados[${status.index}][2] = "checkParcela_" + "${capitalAsegurado.parcela.id}";
		</c:forEach>
		
		$("input:text:visible:first").focus();
	});
    
    function imprimirListadoParcelas(formato) {
    	
    	var frm = document.getElementById('main3');
   		frm.target="_blank";
   		frm.formato.value = formato;
   		frm.method.value = 'doImprimirInformeListadoParcelasAnexo';
   		frm.submit();
   		frm.target="";
   		frm.method.value = "";
    }
    function calculoRdtoHist(){
     	   var itemsChecked = $('#idsRowsChecked').val();
     		if(itemsChecked.length > 0){
    			$('#method').val('doCalculoRdtoHist');
    			$('#main3').submit();
     		}else{
     			showPopUpAviso("Debe seleccionar como mínimo una parcela.");
     		}
    	}
	</script>
	
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4');changeColorRow();">
	<%@ include file="../../common/static/cabecera.jsp"%>
	<%@ include file="../../common/static/menuGeneral.jsp"%>
	<%@ include file="../../common/static/datosCabeceraTaller.jsp" %>	
<!-- Botones para alta, imprimir y volver -->
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">
					<a class="bot" id="btnConsultar"    name="btnConsultar"    href="javascript:consultarParcelas();"            title="Consultar parcelas"             tabindex="30">Consultar</a>
				    <a class="bot" id="btnLimpiar"      name="btnLimpiar"      href="javascript:limpiar();"                      title="Limpiar filtro de consulta"     tabindex="31">Limpiar</a>
				    <c:if test="${modoLectura != 'true'}">
				    <c:if test="${tieneRdtoHist=='true'}">
				    	<a class="bot" id="btnRdtoHistorico"  href="javascript:calculoRdtoHist();" title="Rendimiento hist&oacute;rico">Rdto. Hist&oacute;rico</a>
					</c:if>
					    <a class="bot" id="btnAlta"         name="btnAlta"         href="javascript:altaParcela();"                  title="Alta de una nueva parcela"      tabindex="32">Alta</a>
					    <a class="bot" id="btnDeshacer"     name="btnDeshacer"     href="javascript:deshacerCambiosParcela('','');"  title="Deshacer los cambios"           tabindex="33">Deshacer</a>
					    <a class="bot" id="btnBaja"         name="btnBaja"         href="javascript:eliminarParcela('','');"         title="Eliminar parcelas"              tabindex="34">Baja</a>
					</c:if>
					<a class="bot" id="btnSubvenciones" name="btnSubvenciones" href="javascript:subvenciones();"                 title="Subvenciones"                   tabindex="35">Subvenciones</a>
					<a class="bot" id="btnCambiarIBAN" href="javascript:showPopUpCambioIBAN();">Cambiar IBAN</a>
					<c:choose>
						<c:when test="${vieneDeListadoAnexosMod != 'true'}">
							<a class="bot" id="btnVolverAnexo"  name="btnVolverAnexo"  href="javascript:volverAnexo();"                  title="Volver a Anexo de Modificacion" tabindex="36">Volver Anexo</a>
						</c:when>
						<c:otherwise>
							<a class="bot" id="btnVolverAnexoListado"  name="btnVolverAnexoListado"  href="javascript:volverAnexoListado();"  title="Volver a Utilidades Anexo " tabindex="36">Volver a Utilidades Anexo</a>
						</c:otherwise>
					</c:choose>
					<a class="bot" id="btnVolver"       name="btnVolver"       href="javascript:volver();"                       title="Volver al listado de p&oacute;lizas"   tabindex="36">Volver</a>
				</td>
			</tr>
		</table>
	</div>

<!-- Contenido de la página -->
<div class="conten" style="padding: 3px; width: 97%;">
	<p class="titulopag" align="left">Modificaci&oacute;n de parcelas</p>
	<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
	<!-- Datos de la poliza -->
		<fieldset style="width:95%;">
		<legend class="literal">Datos de la p&oacute;liza</legend>
			<table width="100%" align="center" cellspacing="2">
				<tr>
					<td class="literal" width="75px">Colectivo:</td>
 					<td class="detalI">${poliza.colectivo.idcolectivo } - ${poliza.colectivo.nomcolectivo }</td> 
					<td class="literal" width="40px">Plan:</td>
					<td class="detalI">${poliza.linea.codplan}</td>
					<td class="literal" width="50px">Línea:</td>
					<td class="detalI">${poliza.linea.codlinea } - ${poliza.linea.nomlinea}</td>
  					
				</tr>
				<tr>
					<td class="literal" width="75px">Asegurado:</td>
 					<td class="detalI">${poliza.asegurado.nombreCompleto }</td> 
					<td class="literal" width="40px">P&oacute;liza:</td>
					<td class="detalI">${poliza.referencia }</td>
					<td class="literal" width="50px">M&oacute;dulo:</td>
					<td class="detalI">${poliza.codmodulo}</td>		
				</tr>
			</table>								
		</fieldset>	
	<form name="volverUtilidadesAnexos" id="volverUtilidadesAnexos" action="anexoModificacionUtilidades.run" method="post">
		<input type="hidden" name="method" id="methodUtilidades" value="doConsulta"/>
		<input type="hidden" name="volver" id="volver" value="true"/>
	</form>
	
	<form name="frmSubvenciones" id="frmSubvenciones" action="subvencionAseguradoAnexoMod.html" method="post">
		<input type="hidden" name="method" id="methodSubvenciones" value="doConsulta"/>
		<input type="hidden" name="idAnexoModificacion" id="idAnexoModificacionSubvenciones" value="${requestScope.idAnexo}" />
		<input type="hidden" name="modoLectura" id="modoLecturaSubvenciones" value="${modoLectura}"/>
		<input type="hidden" id="vieneDeListadoAnexosModSubvenciones" name="vieneDeListadoAnexosMod" value="${vieneDeListadoAnexosMod}"/>
	</form>
	
	<form:form name="main3" id="main3" action="parcelasAnexoModificacion.html" method="post" commandName="capitalAseguradoModificadaBean" >
		<input type="hidden" name="method"              id="method"              value=""/>
		<input type="hidden" name="idAnexoModificacion" id="idAnexoModificacion" value="${requestScope.idAnexo}" />
	    <input type="hidden" name="codPoliza"           id="codPoliza"           value=""/>
		<input type="hidden" name="codParcela"          id="codParcela"          value=""/>
		<input type="hidden" name="tipoBajaParcela"     id="tipoBajaParcela"     value=""/>
		<input type="hidden" name="idPoliza"            id="idPoliza"            value="${poliza.idpoliza}"/>
		<input type="hidden" name="lineaseguroid"       id="lineaseguroid"       value="${lineaseguroid}"/>
		<input type="hidden" name="itemCombo"           id="itemCombo"           value="${itemCombo}"/>
		<input type="hidden" name="modoLectura"         id="modoLectura"         value="${modoLectura}"/>
		<input type="hidden" name="idsRowsChecked"      id="idsRowsChecked"      value="${idsRowsChecked}"/>
		<input type="hidden" name="parcelasString"      id="parcelasString"      value="${parcelasString}"/>
		<input type="hidden" name="marcarTodosChecks"   id="marcarTodosChecks"   value="${marcarTodosChecks}"/>
		<input type="hidden" name="isClickInListado"    id="isClickInListado" />
		<input type="hidden" id="vieneDeListadoAnexosMod" name="vieneDeListadoAnexosMod" value="${vieneDeListadoAnexosMod}"/>
		
		<input type="hidden"  id="codlinea" name="codlinea"  value="${poliza.linea.codlinea}" />
		<input type="hidden"  id="fechaInicioContratacion" name="fechaInicioContratacion"  value="${poliza.linea.fechaInicioContratacion}" />
		<input type="hidden"  id="codplan" 	name="codplan"  value="${poliza.linea.codplan}" />	
		<input type="hidden" name="nifCif_cm"                id="nifCif_cm"        value="${poliza.asegurado.nifcif}"/>
		<!-- Valor depende de la lupa tipo de capital -->
		<input type="hidden" id="codconcepto" name="codconcepto" value="126"/>
		
		
		<!-- Impresion -->
		<input type="hidden" name="formato" value=""/>
		
		<input type="hidden" name="tipoListadoGrid" id="tipoListadoGrid" <c:if test="${tipoListadoGrid !=null}">value='${tipoListadoGrid}'</c:if>/>
		
		<form:hidden path="id" id="id"/>
		
		<div style="panel2 isrt">
			<fieldset style="width:95%;">
				<legend class="literal">Filtro</legend>	
					<table align="center">						
						<tr>
							<td class="literal" >Provincia</td>
							<td>
								<form:input path="parcela.codprovincia" id="provincia" cssClass="dato width40"  size="2" maxlength="2" tabindex="1" onchange="javascript:lupas.limpiarCampos('desc_provincia','comarca','desc_comarca','termino','desc_termino','subtermino');"/>	
	                            <form:input path="parcela.parcela.termino.provincia.nomprovincia" cssClass="dato"	id="desc_provincia"  size="20" readonly="true"/> 
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Provincia','principio', '', '');"	alt="Buscar Provincia" title="Buscar Provincia" />
	                            <label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_provincia"> *</label>
							</td>
							<td class="literal" >Comarca</td>
							<td>
								<form:input path="parcela.codcomarca"  id="comarca" cssClass="dato width40"  size="2" maxlength="2" tabindex="2" onchange="javascript:lupas.limpiarCampos('desc_comarca','termino','desc_termino','subtermino');"/>
								<form:input path="parcela.parcela.termino.comarca.nomcomarca" cssClass="dato"	id="desc_comarca"  size="20" readonly="true"/> 
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Comarca','principio', '', '');"	alt="Buscar Comarca" title="Buscar Comarca" />
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_comarca"> *</label>
							</td>
							<td class="literal" >Término</td>
							<td>
								<form:input path="parcela.codtermino"  id="termino" cssClass="dato width40" size="3" maxlength="3" tabindex="3" onchange="javascript:lupas.limpiarCampos('desc_termino','subtermino');"/>
							    <form:input path="parcela.parcela.termino.nomtermino" cssClass="dato" id="desc_termino"  size="30" readonly="true"/> 
							    <label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_termino"> *</label>
							</td>
							<td class="literal" >Subtr.</td>
							<td>
								<form:input path="parcela.subtermino"  id="subtermino" cssClass="dato width40" size="1" maxlength="1" tabindex="4" onchange="this.value=this.value.toUpperCase();"/>		
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Termino','principio', '', '');"	alt="Buscar Termino" title="Buscar Termino" />
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_subtermino"> *</label>
							</td>
						</tr>
					</table>
					  <fieldset style="width: 15%;float:left">
						<legend class="literal">Hoja - Nº</legend>
						<table align="center">
							<tr>
								<td class="literal"></td>
								<td class="literal">
									<form:input path="parcela.hoja" id="txt_hoja" cssClass="dato width40" size="5" maxlength="5" tabindex="5"/>
				            		<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_hoja"> *</label>
						            <form:input path="parcela.numero" id="txt_numero" cssClass="dato width40" size="5" maxlength="5" tabindex="6"/>
						            <label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_numero"> *</label>
								</td>
							</tr>
						</table>
					</fieldset>
					
					<fieldset style="width: 20%;">
						<legend class="literal">Identificaci&oacute;n Catastral</legend>
						<table align="center">
							<tr>
								<td class="literal" >Pol</td>
								<td class="literal">
									<form:input path="parcela.poligono" id="txt_poligono" cssClass="dato" size="3" maxlength="3" tabindex="7"/>
				                    <label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_poligono"> *</label>
								</td>
								<td class="literal" >Par</td>
								<td class="literal">
									<form:input path="parcela.parcela_1" id="txt_parcela" cssClass="dato" size="5" maxlength="5" tabindex="8" />
				                    <label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_parcela"> *</label>
								</td>
							</tr>
						</table>
					</fieldset>
					<fieldset style="width: 60%;">
							<legend class="literal">SIGPAC</legend>
							<table align="center">
								<tr>
									<td class="literal">Prov</td>
									<td class="literal">
										 <form:input path="parcela.codprovsigpac" id="txt_provsigpac" cssClass="dato" size="2" maxlength="2" tabindex="9"/>
				                		 <label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_provsigpac"> *</label>
									</td>
									<td class="literal">Term</td>
									<td class="literal">
										<form:input path="parcela.codtermsigpac"  id="txt_termsigpac" cssClass="dato" size="3" maxlength="3" tabindex="10"/>
				                		<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_termsigpac"> *</label>
									</td>
									<td class="literal">Agr</td>
									<td class="literal">
										<form:input path="parcela.agrsigpac"  id="txt_agrsigpac"  cssClass="dato" size="3" maxlength="3" tabindex="11"/>
				                		<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_agrsigpac"> *</label>
									</td>
									<td class="literal">Zona</td>
									<td class="literal">
										<form:input path="parcela.zonasigpac"  id="txt_zonasigpac"cssClass="dato" size="2" maxlength="2" tabindex="12"/>
				                		<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_zonasigpac"> *</label>
									</td>
									<td class="literal">Pol</td>
									<td class="literal">
										<form:input path="parcela.poligonosigpac"  id="txt_polsigpac"  cssClass="dato" size="3" maxlength="3" tabindex="13"/>
				                		<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_polsigpac"> *</label>
									</td>
									<td class="literal">Parc</td>
									<td class="literal">
										<form:input path="parcela.parcelasigpac"  id="txt_parcsigpac" cssClass="dato" size="5" maxlength="5" tabindex="14"/>
				                		<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_parcsigpac"> *</label>
									</td>
									<td class="literal">Rec</td>
									<td class="literal">
										<form:input path="parcela.recintosigpac"  id="txt_recsigpac"  cssClass="dato" size="5" maxlength="5" tabindex="15"/>
				                		<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_recsigpac"> *</label>
									</td>
								</tr>
							</table>
					</fieldset>
					
					<table align="center">
						<tr>
							<td class="literal" >Nombre Parcela</td>
							<td>
								<form:input path="parcela.nomparcela" id="txt_nombreParcela" cssClass="dato" cssStyle="width:200px" size="20" maxlength="20" tabindex="16" onchange="this.value=this.value.toUpperCase();"/>
						        <label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_nombreParcela"> *</label>
							</td>
							<td class="literal" >Cultivo</td>
							<td>
								<form:input path="parcela.codcultivo" id="cultivo" cssClass="dato width40" size="3" maxlength="3" tabindex="17" onchange="javascript:lupas.limpiarCampos('desc_cultivo','variedad','desc_variedad');"/>
								<form:input path="parcela.parcela.variedad.cultivo.descultivo" cssClass="dato" id="desc_cultivo"  size="20" readonly="true"/> 
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Cultivo','principio', '', '');"	alt="Buscar Cultivo" title="Buscar Cultivo" />
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_cultivo"> *</label>
							</td>
							<td class="literal" >Variedad</td>
							<td>
								<form:input path="parcela.codvariedad" id="variedad" cssClass="dato width40" size="3" maxlength="3" tabindex="18" onchange="javascript:lupas.limpiarCampos('desc_variedad');"/>
								<form:input path="parcela.parcela.variedad.desvariedad" cssClass="dato" id="desc_variedad"  size="25" readonly="true"/> 
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Variedad','principio', '', '');"	alt="Buscar Variedad" title="Buscar Variedad" />
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_variedad"> *</label>
							</td>
						</tr>
					</table>
					
					<table align="center">
						<tr>
							<td class="literal" >T. Capital</td>
							<td>
								<form:input path="tipoCapital.codtipocapital" id="capital" size="3" maxlength="3" cssClass="dato" onchange="javascript:lupas.limpiarCampos('desc_capital');" tabindex="19"/>
								<input class="dato"	id="desc_capital" size="25" readonly="readonly" />
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('FactoresTipoCapitalIN','principio', '', '');"	alt="Buscar Tipo Capital" title="Buscar Tipo Capital" />	
						    	<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_tipoCapital"> *</label>
							</td>
							<td class="literal" >Superficie</td>
							<td>
								 <form:input path="superficie" id="txt_superficie" cssClass="dato" cssStyle="width:85px" size="11" maxlength="11" tabindex="20"/>
					        	<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_superficie"> *</label>
							</td>
							<td class="literal" >Producci&oacute;n</td>
							<td>
								 <form:input path="produccion" id="txt_produccion" cssClass="dato" cssStyle="width:85px" size="11" maxlength="11" tabindex="21"/>
					        	<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_produccion"> *</label>
							</td>
							<td class="literal">Tipo Modificaci&oacute;n</td>
							<td>
								<form:select path="parcela.tipomodificacion" id="sl_tipoModificacion" cssClass="dato" tabindex="22">
									<form:option value="T" label="TODOS" />
	                                <form:option value="A" label="Alta" />
	                                <form:option value="B" label="Baja" />
	                                <form:option value="M" label="Modificaci&oacute;n" /> 
						        </form:select>
							</td>
						</tr>
					</table>
					
					<div class="literal" style="width:870px;margin:0px 5 0 5;text-align: center;">
					    <div id="parcelas" style="float:left;margin-right:100px" class="handOver" 
					         onmouseover="javascript:radioElem_onmouseover('parcelas')"
					         onmouseout="javascript:radioElem_onmouseout('parcelas')"
					         onclick="javascript:tipoListado_onclick('parcelas')">
					         Parcelas
					         <input id="" type="radio" name="listado" value="parcelas" <c:if test="${tipoListadoGrid == 'parcelas' }">checked</c:if>>
					            
					    </div>
						<div id="instalaciones" style="float:left;margin-right:100px" class="handOver"
						     onmouseover="javascript:radioElem_onmouseover('instalaciones')"
					         onmouseout="javascript:radioElem_onmouseout('instalaciones')"
					         onclick="javascript:tipoListado_onclick('instalaciones')">
					         Instalaciones
					         <input id="" type="radio" name="listado" value="instalaciones" <c:if test="${tipoListadoGrid == 'instalaciones' }">checked</c:if>>
					    </div>
						<div id="todas" style="float:left;" class="handOver"
						     onmouseover="javascript:radioElem_onmouseover('todas')"
					         onmouseout="javascript:radioElem_onmouseout('todas')"
					         onclick="javascript:tipoListado_onclick('todas')">
					         Todas
					         <input id="" type="radio" name="listado" value="todas" <c:if test="${tipoListadoGrid == 'todas'}">checked</c:if>>
					     </div>
					     <div>
							Tipo Rendimiento
							 	 <form:select path ="tipoRdto" id="rdtoHist" cssClass="dato" tabindex="9" cssStyle="width:170" >
							 	 <option value="">Todos</option> 
								    <c:forEach items="${listaTipoRendimientos}"	var="tiposRdto"> 
 									<form:option value="${tiposRdto.idrdto}">${tiposRdto.descripcion}</form:option>
 								</c:forEach> 
							</form:select>
							 
									
						</div>
				    </div>	
					
			</fieldset>
		</div>
		<br/>
		<div id="grid" >

			<display:table requestURI="parcelasAnexoModificacion.html" 
			               id="listaParcelasModificadas" 
						   class="LISTA" 
						   summary="ParcelaModificada" 
						   name="${listaParcelasModificadas}"
						   sort="list"
						   pagesize="${numReg}"
						   keepStatus="true"
						   clearStatus="${clearStatus}"
						   defaultsort="16"
						   decorator="com.rsi.agp.core.decorators.ModelTableDecoratorParcelasModificadas" 
						   excludedParams="method idsRowsChecked codParcela localizacion_cm parcelasString capAsegString"
						   style="border-collapse:collapse;">
  					
						<display:column class="accionesDisplayTagColumn" headerClass="cblistaImg 626262" title="Acciones" media="html" property="admActions" sortable="false" style="width:115px;text-align:center;color:#626262;" />
						<display:column class="literal" headerClass="cblistaImg" title="N&#176;"        property="hojaNumero"    style="width:80px;"                   sortable="false"/>
						<display:column class="literal" headerClass="cblistaImg" title="PRV"            property="codprovincia"  style="width:80px;"   				   sortable="false"/>
						<display:column class="literal" headerClass="cblistaImg" title="CMC"            property="codcomarca"    style="width:80px;"   				   sortable="false"/>
						<display:column class="literal" headerClass="cblistaImg" title="TRM"            property="codtermino"    style="width:80px;"   				   sortable="false"/>
						<display:column class="literal" headerClass="cblistaImg" title="SBT"            property="codsubtermino" style="width:80px;"   				   sortable="false"/>
						<display:column class="literal" headerClass="cblistaImg" title="CUL"            property="codcultivo"    style="width:80px;"   				   sortable="false"/>
						<display:column class="literal" headerClass="cblistaImg" title="VAR"            property="codvariedad"   style="width:80px;"                   sortable="false"/>
						<display:column class="literal" headerClass="cblistaImg" title="Id. Cat/SIGPAC" property="idCat"         style="width:230px;"                  sortable="true" comparator = "com.rsi.agp.core.comparators.TableComparatorSigPacs" />
						

						<c:if test="${tipoListadoGrid == 'instalaciones'}">
						    <display:column class="literal" headerClass="cblistaImg" title="m" property="superf" style="width:85px;"  sortable="true">
						    </display:column>
						</c:if>
						<c:if test="${tipoListadoGrid == 'todas'}">
							<display:column class="literal" headerClass="cblistaImg" title="Super./m" property="superf" style="width:85px;"  sortable="true" comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico">
							</display:column>
						</c:if>
						<c:if test="${tipoListadoGrid == 'parcelas'}">
							<display:column class="literal" headerClass="cblistaImg" title="Superficie" property="superf" style="width:85px;"  sortable="true" comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico">
							</display:column>
						</c:if>

						<display:column class="literal" headerClass="cblistaImg" title="Prec."          property="precio"        style="width:80px;"                   sortable="false"/>
						
						<c:if test="${tipoListadoGrid == 'todas'}">
					    	<display:column class="literal" headerClass="cblistaImg" title="Prod."          property="produccion"    style="width:55px;"  sortable="true" comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico" />
					    </c:if>
					    <c:if test="${tipoListadoGrid == 'parcelas'}">
					    	<display:column class="literal" headerClass="cblistaImg" title="Prod."          property="produccion"    style="width:55px;"  sortable="true" comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico" />
					    </c:if>
						
						
						<display:column class="literal" headerClass="cblistaImg" title="T.Capital"      property="tcapital"      style="width:250px;"                  sortable="false"/>
						<display:column class="literal" headerClass="cblistaImg" title="Estado"         property="estado"        style="width:100px;"                  sortable="false"/>
						<display:column class="literal" headerClass="cblistaImg" title="X"              property="columnaCheck"  style="width:50px;text-align:center"  sortable="false"/>
						
						<display:column class="literal" title=" " property="ordenacion" style="visibility:hidden;"/>
						
						<display:setProperty name="paging.banner.some_items_found" value='' />
						<display:setProperty name="paging.banner.one_item_found" value='' />
						<display:setProperty name="paging.banner.all_items_found" value='' />
							        	
			        	<display:footer>
			        		<tr style="background-color:#e5e5e5">
			        			<td class="literal" colspan="2">N&#176; Total Parcelas: </td>
			        			<td class="literal" style="color:green">${numParcelasListado}</td>
			        			<td class="literal" colspan="2">Seleccionados:</td>
			        			<td class="literal" style="color:green"><label id="sel"/></td>	
			        			<td class="literal"></td>	
			        			<td class="literal"></td>	
			        			<td class="literal"></td>	
			        			<td class="literal"></td>
			        			<td class="literal"></td>
			        			<c:if test="${tipoListadoGrid == 'todas'}">	
			        			    <td class="literal"></td>
			        			</c:if>	
			        			<c:if test="${tipoListadoGrid == 'parcelas'}">	
			        			    <td class="literal"></td>
			        			</c:if>
			        			<td class="literal" colspan="2"><div style="float:right">Marcar Todos:</div></td>
			        			<td class="literal" style="width:30px;text-align:center">
			        			 	<input type="checkbox" id="selTodos" name="selTodos" class="dato" onclick="this.checked  ? seleccionar_checks() : '' "/>
			        			</td>
			        		</tr>
			        	</display:footer>
			        	
			        	<c:if test="${tipoListadoGrid == 'todas'}">
							<div style="float:right;color:#626262;font-family:tahoma,verdana,arial;font-size:11px;font-weight:bold;">
							    <span style="width:12px;height:12px;background-color:#A9F5A9"></span>
							    &nbsp;Instalaciones
							</div>
				       </c:if>
			</display:table>
			
	        <div style="width:20%;text-align:center" id="divImprimir">
	        	 <a style="font-family: tahoma, verdana, arial;color: #626262;font-size:11px;">Imprimir</a>	
				 <a id="btnImprimirExcel" style="text-decoration:none;" href="javascript:imprimirListadoParcelas('xls')">
				 	<img src="jsp/img/jmesa/excel.gif"/>
				 </a>
			</div>

		</div>
	</form:form>
</div>
<%@ include file="/jsp/common/static/piePagina.jsp"%>
<%@ include file="/jsp/moduloUtilidades/modificacionesPoliza/razonBajaAnexoParcela.jsp"%>
<%@ include file="/jsp/common/static/overlay.jsp"%>

<%@ include file="/jsp/common/lupas/lupaCultivo.jsp"%>
<%@ include file="/jsp/common/lupas/lupaVariedad.jsp"%>
<%@ include file="/jsp/common/lupas/lupaProvincia.jsp"%>
<%@ include file="/jsp/common/lupas/lupaComarca.jsp"%>
<%@ include file="/jsp/common/lupas/lupaTermino.jsp"%>
<%@ include file="/jsp/common/lupas/lupaTipoCapitalIN.jsp"%>

</body>
</html>