<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<fmt:bundle basename="displaytag" >
	<c:set var="numReg"><fmt:message key="numElementsPag"/></c:set>
</fmt:bundle>

<html>
<head>
<title>Parcelas Modificacion del Complementario</title>

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
	<script type="text/javascript" src="jsp/moduloUtilidades/modificacionesPolizaComplementaria/parcelasAnexoPolizaComplementaria.js"></script>
	<script type="text/javascript" src="jsp/moduloUtilidades/modificacionesPolizaComplementaria/parcelasAnexoPolizaComplementariaSw.js"></script>
	<script type="text/javascript" src="jsp/moduloUtilidades/incrementoProduccionParcelaComplementaria.js"></script>
	<script>
	function imprimirListadoParcelas(formato){
		var frm = document.getElementById('main3');
    	frm.target="_blank";
   		frm.formato.value = formato;
    	frm.method.value = 'doImprimirInformeListadoParcelasAnexo';
    	frm.submit();
    	frm.target="";
	}

</script>
	<%@ include file="/jsp/js/draggable.jsp"%>
</head>

<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4');">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	
	<div id="buttons">
		<table width="100%" cellspacing="2" cellpadding="2" border="0">
			<tr>
				<td align="left">
					<a class="bot" id="btnVolver"  href="javascript:coberturas();">Coberturas</a>	
					<c:if test="${modoLectura != 'true'}">			
						<a class="bot" id="btnBaja"  href="javascript:baja();">Baja</a>				
						<a class="bot" id="btnCoberturas"  href="javascript:resetSw();">Deshacer</a>
					</c:if>				
				</td>
				<td align="left">
					<a class="bot" id="btnConsultar"  href="javascript:consultar();">Consultar</a>				
					<a class="bot" id="btnLimpiar"  href="javascript:limpiar();">Limpiar</a>	
				</td>
				<td>
 					<a class="bot" id="btnImprimir"  href="javascript:imprimir()">Imprimir</a> 
 					<c:if test="${modoLectura != 'true'}">
						<a class="bot" id="btnGuardar"  href="javascript:grabar();">Grabar</a>				
						<a class="bot" id="btnGuardar"  href="javascript:grabarAndEnviar();">Grabar y Enviar</a>
					</c:if>				
				</td>
				<td align="right">
					<c:choose>
						<c:when test="${vieneDeListadoAnexosMod != 'true'}">
							<a class="bot" id="btnSalir"  href="javascript:salir();">Salir</a>
						</c:when>
						<c:otherwise>
							<a class="bot" id="btnVolverAnexoListado"  name="btnVolverAnexoListado"  href="javascript:volverAnexoListado();"  title="Volver a Utilidades Anexo " tabindex="36">Volver a Utilidades Anexo</a>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</table>
	</div>
	<div class="conten" style="padding: 3px; width: 100%">
		<p class="titulopag" align="left">Parcelas Modificación del Complementario</p>
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
		<!-- Datos de la póliza -->
		<fieldset style="width:97%;">
			<legend class="literal">Datos de la p&oacute;liza</legend>
			<table width="100%" align="center" cellspacing="2">
				<tr>
					<td class="literal" width="75px">Colectivo:</td>
 					<td class="detalI">${capitalAseguradoBean.parcela.anexoModificacion.poliza.colectivo.idcolectivo } - ${capitalAseguradoBean.parcela.anexoModificacion.poliza.colectivo.nomcolectivo }</td> 
					<td class="literal" width="40px">Plan:</td>
					<td class="detalI">${capitalAseguradoBean.parcela.anexoModificacion.poliza.linea.codplan}</td>
					<td class="literal" width="50px">Línea:</td>
					<td class="detalI">${capitalAseguradoBean.parcela.anexoModificacion.poliza.linea.codlinea } - ${capitalAseguradoBean.parcela.anexoModificacion.poliza.linea.nomlinea}</td>
  					
				</tr>
				<tr>
					<td class="literal" width="75px">Asegurado:</td>
 					<td class="detalI">${capitalAseguradoBean.parcela.anexoModificacion.poliza.asegurado.nombreCompleto }</td> 
					<td class="literal" width="40px">Póliza:</td>
					<td class="detalI">${capitalAseguradoBean.parcela.anexoModificacion.poliza.referencia }</td>
					<td class="literal" width="50px">Módulo:</td>
					<td class="detalI">${capitalAseguradoBean.parcela.anexoModificacion.poliza.codmodulo}</td>		
				</tr>
			</table>								
		</fieldset>
		
		<form name="volverUtilidadesAnexos" id="volverUtilidadesAnexos" action="anexoModificacionUtilidades.run" method="post">
			<input type="hidden" name="method" id="methodUtilidades" value="doConsulta"/>								
			
			<input type="hidden" name="volver" id="volver" value="true"/>
		</form>	
		<form:form name="imprimirAnexoCpl" id="imprimirAnexoCpl" action="polizaActualizada.html" method="post" commandName="capitalAseguradoBean">
			<input type="hidden" name="method" id="methodImprimir" value="doImprimirAnexoCpl"/>
			<input type="hidden" name="imprimirAnexoWS"  id="imprimirAnexoWS"  value="true"/>
			<input type="hidden" name="refPoliza" id="refPoliza" value="${capitalAseguradoBean.parcela.anexoModificacion.poliza.referencia}"/> 
			<form:hidden path="parcela.anexoModificacion.cupon.idcupon" id="idCuponImprimir"/>
			<form:hidden path="parcela.anexoModificacion.id" id="idImprimir"/>
		</form:form>
		
		
		<form:form name="main3" id="main3" action="declaracionesModificacionPolizaComplementaria.html" method="post" commandName="capitalAseguradoBean">
			<form:hidden path="parcela.anexoModificacion.poliza.linea.lineaseguroid" id="lineaseguroid" />
			<form:hidden path="parcela.anexoModificacion.poliza.linea.codlinea" id="codlinea" />
			<input type="hidden" id="fechaInicioContratacion" name="fechaInicioContratacion" value="${fechaInicioContratacion}"/>
			<form:hidden path="parcela.anexoModificacion.poliza.idpoliza" id="idPoliza" />
			<form:hidden path="parcela.anexoModificacion.id" id="idAnexo" />
			<input type="hidden" id="method" name="method"/>
			<input type="hidden" id="idCapitalIncremento" name="idCapitalIncremento" value=""/>
			<input type="hidden" id="idCapitalSup" name="idCapitalSup" value=""/>
			<input type="hidden" id="checksSel" name="checksSel" value="${capitalesAlta}"/>
			<input type="hidden" id="vieneDeListadoAnexosMod" name="vieneDeListadoAnexosMod" value="${vieneDeListadoAnexosMod}"/>
			<input type="hidden" id="incrSel" name="incrSel" value="${incrementosAlta}"/>
			<!--  Incrementos -->
			<input type="hidden" id="tipoInc" name="tipoInc" value=""/>			
			<input type="hidden" id="incrGen" name="incrGen" value=""/> 
			<input type="hidden" id="listaIds" name="listaIds" value="${listaIds}"/>
			<input type="hidden" id="marcarTodos" name="marcarTodos" value="${marcarTodos}"/>
			<input type="hidden" id="listaIdCapAseg" name="listaIdCapAseg" value="${listaIdCapAseg}"/>
			<input type="hidden" id="incrementoOK" name="incrementoOK" value="${incrementoOK}"/>
			<input type="hidden" id="redireccion" name="redireccion" value="pParcelasCpl"/>
			
			<!--  Pet. 78691 ** MODIF TAM (22/12/2021) -->
		    <input type="hidden" name="sist_cultivo" id="sist_cultivo" value="${sist_cultivo}"/>
		    <input type="hidden" name="des_sist_cultivo" id="des_sist_cultivo" value="${des_sist_cultivo}"/>
			
			
			<!-- Impresión -->
			<input type="hidden" name="formato" value=""/>
			<input type="hidden" name="tipoListadoGrid" id="tipoListadoGrid" <c:if test="${tipoListadoGrid !=null}">value='${tipoListadoGrid}'</c:if>/>
			
			<form:hidden path="parcela.anexoModificacion.cupon.id" id="idCupon"/>
			<form:hidden path="parcela.anexoModificacion.cupon.idcupon" id="cuponId"/>
			
			<div style="panel2 isrt">
				<fieldset style="width:97%;">
					<legend class="literal">Filtro</legend>				
							<table align="center">
								<tr>
									<td class="literal">Provincia</td>
									<td class="literal">
										<form:input  id="provincia" path="parcela.codprovincia" cssClass="dato" size="2" maxlength="2" onchange="javascript:lupas.limpiarCampos('desc_provincia','comarca','desc_comarca','termino','desc_termino','subtermino');"/>
										<input class="dato"	id="desc_provincia" name="desc_provincia" size="20" readonly="readonly"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Provincia','principio', '', '');"	alt="Buscar Provincia" title="Buscar Provincia" />	
									</td>
									<td class="literal">Comarca</td>
									<td class="literal">
										<form:input  id="comarca" path="parcela.codcomarca" cssClass="dato" size="2" maxlength="2" onchange="javascript:lupas.limpiarCampos('desc_comarca','termino','desc_termino','subtermino');"/>
										<input class="dato"	id="desc_comarca" name="desc_comarca" size="30" readonly="readonly"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Comarca','principio', '', '');"	alt="Buscar Comarca" title="Buscar Comarca" />
									</td>
									<td class="literal">Término</td>
									<td class="literal">
										<form:input  id="termino" path="parcela.codtermino" cssClass="dato" size="3" maxlength="3" onchange="javascript:lupas.limpiarCampos('desc_termino','subtermino');"/>
										<input class="dato"	id="desc_termino" name="desc_termino" size="30" readonly="readonly"/>
									</td>
									<td class="literal">Subtérmino</td>
									<td class="literal">
										<form:input  id="subtermino" path="parcela.subtermino" cssClass="dato" size="1" maxlength="1" onchange="this.value=this.value.toUpperCase();"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Termino','principio', '', '');"	alt="Buscar Término" title="Buscar Término" />
									</td>
								</tr>
							</table>
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
										<input class="dato"	id="desc_cultivo" name="desc_cultivo" size="25" readonly="readonly"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Cultivo','principio', '', '');"	alt="Buscar Cultivo" title="Buscar Cultivo" />
									</td>
									<td class="literal">Variedad</td>
									<td class="literal">
										<form:input  id="variedad" path="parcela.codvariedad" cssClass="dato" size="3" maxlength="3" onchange="javascript:lupas.limpiarCampos('desc_variedad');"/>
										<input class="dato"	id="desc_variedad" name="desc_variedad" size="25" readonly="readonly"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Variedad','principio', '', '');"	alt="Buscar Variedad" title="Buscar Variedad" />		
									</td>
									
								</tr>
							</table>
							<table>
								<tr>
									<td class="literal">Tipo Capital</td>
									<td class="literal">
										<form:input path="tipoCapital.codtipocapital" id="capital" size="2" maxlength="2" cssClass="dato" onchange="javascript:lupas.limpiarCampos('desc_capital');"/>
										<input class="dato"	id="desc_capital" size="20" readonly="readonly"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('TipoCapital','principio', '', '');"	alt="Buscar Tipo Capital" title="Buscar Tipo Capital" />	
									</td>
									<td class="literal">Superficie</td>
									<td class="literal">
										<form:input path="superficie"   size="8"  maxlength="8" id="superficie" cssClass="dato"/>
									</td>
									<td class="literal">Prod. Ant</td>
									<td class="literal">
										<form:input path="produccion"  size="8"  maxlength="8"  id="prodAnt" cssClass="dato"/>
									</td>
									<td class="literal">Estado</td>
									<td class="literal" size = "9">
										  <form:select path="parcela.tipomodificacion"  id="estado" cssClass="dato">
											<form:option value="">--Seleccione una opción--</form:option>
											<form:option value="A">ALTA</form:option>
											<form:option value="B">BAJA</form:option>
											<form:option value="M">MODIFICACIÓN</form:option>
										</form:select>
									</td>
									<!--  Pet. 78691 ** MODIF TAM (17.12.2021) ** Inicio  -->
									<td class="literal"> S. Cultivo</td>
									<td>
									    <input type="text" class="dato" size="2" onchange="javascript:lupas.limpiarCampos('dessistemaCultivo');" maxlength="3" id="sistemaCultivo" name="sistemaCultivo" />
										<input type="text" class="dato" size="19" maxlength="30" id="dessistemaCultivo" name="dessistemaCultivo" readonly="true" />
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('SistemaCultivo','principio', '', '');"	alt="Buscar Sistema Cultivo" title="Buscar Sistema Cultivo" />
										<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_dessistemacultivo"> *</label>
									</td>
									<!--  Pet. 78691 ** MODIF TAM (17.12.2021) ** Fin  -->
								</tr>
							</table>
				</fieldset>
			</div>
			
			<div class="literal" style="text-align:right;margin:6px 26px;height:41px">
		    <div id="camposIncremento" style="color:#626262;font-size:11px;font-family: verdana;font-weight:bold">

		        <div style="float:right">
		        	<c:if test="${modoLectura != 'true'}">
		        		<a class="bot" id="btnIncrementar"  href="javascript:incrementarGeneral('false');">Incrementar</a>
		        	</c:if>
		        </div>
		                 
		        <div id="incrementoKilosHa" style="float:right; width:240px;">
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
				<div style="float:right;padding:12px 10px 0 0">Incremento General (seleccionados)</div>

			    </div>
			</div>
			<div id="grid">
			 	<display:table requestURI="" class="LISTA" summary="parcelas"
					pagesize="${numReg}" sort="list" name="${listCapAseg}" id="parcelasCpl" export="false" excludedParams="method"
					decorator="com.rsi.agp.core.decorators.ModelTableDecoratorParcelasModificadasComplementaria" 
					style="width:100%;border-collapse:collapse;">
					
					<display:column class="accionesDisplayTagColumn" headerClass="cblistaImg" title="Acciones" media="html" property="admActionsAnexo" sortable="false" style="width:40px;text-align:center" />
					<display:column class="literal" headerClass="cblistaImg" title="Nº"   			property="numero"        style="width:30px;"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="PRV"            property="codprovincia"  style="width:25px;"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="CMC"            property="codcomarca"    style="width:25px;"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="TRM"            property="codtermino"    style="width:25px;"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="SBT"            property="codsubtermino" style="width:25px;"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="CUL"            property="codCultivo"    style="width:25px;"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="VAR"            property="codVariedad"   style="width:25px;"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="SIGPAC" 		property="idCat"         style="width:250px;" sortable="true" comparator = "com.rsi.agp.core.comparators.TableComparatorSigPacs" />
					<display:column class="literal" headerClass="cblistaImg" title="Nombre Parcela" property="nomPar"        style="width:180px;" sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="T.Cap"		    property="capital"       style="width:30px;" sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="Sup"   			property="superf"        style="width:40px;"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="Prod. Ppal."    property="produccion"    style="width:55px;"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="Precio"         property="precio"        style="width:40px;"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="Estado"         property="estado"    	 style="width:40px;color:red"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="Prod. Cpl."     property="incremento"    style="width:60px;color:green;"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="Modif. Prod. Cpl." property="incrModif"  style="width:60px;color:red"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="Kg totales aseg. después de modificación" property="kilosAseg"   	 style="width:60px;"  sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="X"        	    property="checks"    	 style="width:20px;"  sortable="false"/>
					
					<display:setProperty name="paging.banner.some_items_found" value='' />
					<display:setProperty name="paging.banner.one_item_found" value='' />
					<display:setProperty name="paging.banner.all_items_found" value='' />
						        	
		        	 <display:footer>
		        		<tr style="background-color:#e5e5e5">
		        			<td class="literal" colspan="3">Nº Total Parcelas: </td>
		        			<td class="literal" style="color:green"><%=((java.util.List)request.getAttribute("listCapAseg")).size()%></td>
		        			<td class="literal" colspan="3">Seleccionados:</td>
		        			<td class="literal" style="color:green"><label id="sel"/></td>	
		        			<td class="literal"></td>	
		        			<td class="literal"></td>	
		        			<td class="literal"></td>	
		        			<td class="literal"></td>	
		        			<td class="literal"></td>	
		        			<td class="literal"></td>
		        			<td class="literal"></td>
		        			<td class="literal"></td>	
		        			<td class="literal" colspan="2">Marcar Todos:</td>	
		        			<td class="literal"style="width:30px;text-align:left">
		        				<input type="checkbox" id="selTodos" name="selTodos" class="dato" onclick="this.checked  ? marcar_todos() : desmarcar_todos()"/>
		        			</td>
		        			<td class="literal"></td>	
		        		</tr>
		        	</display:footer>
		       
				</display:table>
				
				<div style="width:20%;text-align:center" id="divImprimir">
	        	 <a style="font-family: tahoma, verdana, arial;color: #626262;font-size:11px;">Imprimir</a>	
				 <a id="btnImprimirExcel" style="text-decoration:none;" href="javascript:imprimirListadoParcelas('xls')">
				 	<img src="jsp/img/jmesa/excel.gif"/>
				 </a>
			</div>
			</div>
		</form:form>
		<!-- form name="printCpl" id="printCpl" action="informes.html" method="post">
			<input type="hidden" name="method" id="methodPrintCpl" value="doInformeAnexoModificacionComplementario"/>
			<input type="hidden" name="idAnexo" id="idAnexoCpl" value="${idAnexo}"/>
			<input type="hidden" name="idPoliza" id="idPolizaPrint"/>
		</form -->
		
	</div>	
	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
	<%@ include file="/jsp/common/lupas/lupaCultivo.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaVariedad.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaProvincia.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaComarca.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaTermino.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaTipoCapital.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaSistemaCultivo.jsp"%>
	
	<!--  popup modificarPorcentajes -->
		<div id="visorIncremento" class="wrapper_popup" style="width: 55%; top: 170px; left: 25%;">
		
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