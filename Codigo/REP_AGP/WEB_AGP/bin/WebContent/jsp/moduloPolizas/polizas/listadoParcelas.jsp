<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<fmt:bundle basename="displaytag" >
	<c:set var="numReg"><fmt:message key="numElementsPag"/></c:set>
</fmt:bundle>
<fmt:bundle basename="agp" >
	<c:set var="numEle"><fmt:message key="visores.numElements"/></c:set>
</fmt:bundle>

<html>
	<head>
		<title>Listado de parcelas</title>
		
        <%@ include file="/jsp/common/static/metas.jsp"%>


		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		
		<script type="text/javascript" src="jsp/js/util.js"></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/terminos.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenericaIN.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/listadoParcelas.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
		<script type="text/javascript" src="jsp/js/calendar.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/displayTagAjaxSelectedRow.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
		
		<script><!--
		
		     // Para evitar el cacheo de peticiones al servidor
	         $(document).ready(function(){
	        	
	            var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
		        document.getElementById("main3").action = URL;
		        document.getElementById("provincia").focus();
		        
	         });
		
		
			 $(document).ready(function() {
			      $('#rdtoHist').val("${datos.rdtoHist}");
			     // si edita volver a seleccionar la que estaba editando
			     if($('#selectedRow').val() == "true"){
			         setSelectedRow($('#idRowSelected').val());
			     }
			     
			     $("#grid").displayTagAjax();

			     // --------------------------------------------------
                 //                 control de checks
                 // --------------------------------------------------
                
				check_checks($('#idsRowsChecked').val());
				$('#sel').text(numero_check_seleccionados2());
				//checkTodos();
			    
			 
				<c:if test="${parcelasRepetidas || polizaSinInstalacion}">
				    $('#overlay').show();
				    $('#parcelasRep').show();
				</c:if>
				
				
				<c:if test="${polizaSinInstalacion}">
					document.getElementById("txt_noHayInstalaciones").style.display='block';
				</c:if>
				
				
				<c:if test="${parcelasRepetidas}">
					document.getElementById("txt_parcelasRepetidas").style.display='block';
				</c:if>			
				
				
				 if($('#modoLectura').val() != 'modoLectura'){
					   $('#btnAlta').show();
					   $('#btnCambioMasivo').show();
					   $('#btnBorradoMasivo').show();
					   $('#btnDuplicar').show();
					   $('#btnPrecioMasivo').show();
					     
				 }else{
					 disabledChecks(true); 
				 }
				 changeColorRow();
				 
				//Para ocultar el icono de exportar si la lista de parcelas es vac&#237a
				<c:if test="${empty datos.listadoParcelas}">					
					$('#divImprimir').hide(); 									
				</c:if> 	
			 });
			 
			/////////////////////////////////////////////////////////////////////////////////////////// 
			 
			function setSelectedRow(idRow){
				var rows = $('#listaParcelas_cm').find('tr').get();
				for (var i = 0; i < rows.length; ++i)
				{
					cells = rows[i].getElementsByTagName('td');
					if(cells.length > 0){
						if (cells[0].innerHTML.indexOf(idRow) != -1){
							rows[i].className =  " selected";
						}
					}
				}
			}
			function eligeMenu(){
			
				if($('#vieneDeUtilidades').val() == 'true' && $('#modoLectura').val() == 'modoLectura'){
				 	SwitchMenu('sub4');
				 }else{
				 	SwitchMenu('sub3');
				 }
			 }

		    function imprimirListadoParcelas(formato) {
		    	
		    	var frm = document.getElementById('main3');
	    		frm.target="_blank";
	    		frm.formato.value = formato;
	    		frm.operacion.value = 'imprimirInformeListadoParcelas';
	    		frm.submit();
	    		frm.target="";
		    }
		--></script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="javascript:eligeMenu();generales.fijarFila();javascript:abrePopupDanhosFauna()">
		
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		 <c:choose>
			<c:when test="${modoLectura == 'modoLectura' && vieneDeUtilidades == 'true'}">
				<%@ include file="/jsp/common/static/datosCabeceraConsulta.jsp" %>	
			</c:when>
			<c:otherwise>
				<%@ include file="/jsp/common/static/datosCabecera.jsp"%>
			</c:otherwise>
		</c:choose>
		<div id="buttons">
			<table width="97%" cellspacing="2" cellpadding="2" border="0">
				<tr align="left">
					<td align="left">
						<a class="bot" id="btnComparativas"  href="javascript:comparativas();"  title="Ir a Coberturas">Coberturas</a>
					</td>
					<td>
						<c:if test="${numParcelasListado > 0}">
							<a class="bot" id="btnDuplicar"  style="display: none"	 href="javascript:duplicar();"  	title="Duplicar Parcelas">Duplicar</a>
						</c:if>
						<a class="bot" id="btnCambioMasivo"  style="display: none" href="javascript:cambioMasivo();"  title="Cambio masivo">Cambio masivo</a>
						<a class="bot" id="btnBorradoMasivo" style="display: none" href="javascript:borradoMasivo();" title="Borrado masivo">Borrado masivo</a>
						<!-- Pet. 643485 - FASE III DNF 23/12/2020 -->
						<a class="bot" id="btnPrecioMasivo" style="display: none" href="javascript:calcularPrecioMasivo();"  title="Precio masivo">Precio masivo</a>
						<!-- fin Pet. 643485 - FASE III DNF 23/12/2020 --> 
						<!-- Pet. 63668 - FASE III DNF 05/08/2021 -->
						<c:if test="${mostrarDanhosFauna == true }">
						<a class="bot" id="btnDanhosFauna"  href="javascript:danhosFauna();"  title="Da&#241o Fauna">Da&#241o fauna</a>
						<!-- fin Pet. 63668 - FASE III DNF 05/08/2021 --> 
						</c:if>
						
						<c:if test="${tieneRdtoHist=='true'}">
							<a class="bot" id="btnRdtoHistorico" href="javascript:calculoRdtoHist();" title="Rendimiento hist&#243rico">Rdto. Hist&#243rico</a>
							<!-- P0078877 ** MODIF TAM (25.10.2021) ** Inicio -->
							<a class="bot" id="btnRdtoOrientativo" href="javascript:calcRdtoOrientativo();" title="Rendimiento Orientativo">Rdto. Orientativo</a>
						</c:if>
					</td>
					<td>
						<a class="bot" id="btnAlta"          style="display: none" href="javascript:alta();"          title="Alta de una nueva parcela">Alta</a>
						<a class="bot" id="btnConsultar"     href="javascript:consultar();"     title="Consultar parcelas">Consultar</a>
						<a class="bot" id="btnLimpiar"       href="javascript:limpiar();"       title="Limpiar filtro de consulta">Limpiar</a>
					</td>
					<td align="right">
						<a class="bot" id="btnVolver"        href="javascript:volver()"         title="Volver al listado de p&#243lizas">Volver</a>
						<a class="bot" id="btnSiguiente"     href="javascript:siguiente();"     title="Continuar con la tramitaci&oacute;n de la p&oacute;liza">Continuar</a>
					</td>
				</tr>
			</table>
		</div>
		<!-- Contenido de la p&#225gina -->
		<div class="conten" style="padding:3px;width:97%">
		
			<p class="titulopag" align="left">Listado de parcelas</p>
			
			<form name="frmComparativas" action="polizaController.html" method="post" id="frmComparativas">
				<input type="hidden" name="action" id="action" value="comparativa"/>
				<input type="hidden" name="origenllamada" id="origenllamada" value="listparcelas"/>
				<input type="hidden" name="activados" id="activados" value=""/>
				<input type="hidden" name="idpoliza" id="idpoliza" value="${datos.idpoliza}" />
				<input type="hidden" name="parcelasWeb" value="true"/>
				<input type="hidden" name="modoLectura" id="modoLectura" value="${datos.modoLectura}"/>
				<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${datos.vieneDeUtilidades}"/>
				<input type="hidden" name="tieneParcelas" id="tieneParcelas" <c:if test="${not empty datos.listadoParcelas}"> value='si'</c:if>/>
				
			</form>
			<form name="consultaDetallePoliza" id="consultaDetallePoliza" action="consultaDetallePoliza.html" method="post">
				<input type="hidden" name="method" id="method"/>
				<input type="hidden" name="idpoliza" id="idpoliza" value="${datos.idpoliza}"/>
				<input type="hidden" name="modoLectura" id="modoLectura" value="${datos.modoLectura}"/>
				<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${datos.vieneDeUtilidades}"/>
			</form>
			
			<form:form name="datosParcela" id="datosParcela" action="datosParcela.html" method="post" commandName="polizaBean">
				<input type="hidden" name="method" id="methodDP"/>
				<input type="hidden" name="codParcela" id="codParcelaDP"/>
				<input type="hidden" name="operacion" id="operacionDP"/>
				<input type="hidden" name="tipoParcela" id="tipoParcelaDP"/>
				<input type="hidden" name="modoLectura"                id="modoLectura"                value="${datos.modoLectura }"/>
				<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${datos.vieneDeUtilidades}"/>
				<input type="hidden" name="listaIdsStr" id="listaIdsStr"/>
				<!-- bean -->
				<form:hidden path="linea.lineaseguroid"                id="lineaseguroid" />
				<form:hidden path="linea.codlinea"                     id="codlinea" />
				<form:hidden path="linea.codplan"                      id="codplan" />
				<form:hidden path="idpoliza"                           id="idpoliza"/>
				<form:hidden  path="clase"                              id="idclase"/>
				<form:hidden  path="asegurado.nifcif" />
			</form:form>
			<!-- Pet. 643485 - FASE III DNF 23/12/2020 -->
			<form name="precioMasivoForm" id="precioMasivoForm" action="cambioMasivo.html" method="post" commandName="polizaBean">
				<input type="hidden" name="method" id="method" value="doPrecioMasivo"/>
				<input type="hidden" name="lineaseguroid" id="lineaseguroid"/>
				<input type="hidden" name="listCodModulos" id="listCodModulos"/>
				<input type="hidden" name="idsRowsCheckedCM" id="idsRowsCheckedCM"/>
				<input type="hidden" name="idPolizaCM" id="idPolizaCM"/>
				<input type="hidden" name="method" id="method"/>	
			</form>
			<!-- fin Pet. 643485 - FASE III DNF 23/12/2020 -->
			
			<!-- Pet. 643485 - FASE III DNF 23/12/2020 -->
			<form name="danhosFaunaForm" id="danhosFaunaForm" action="danhosFauna.html" method="post" commandName="polizaBean">
				<input type="hidden" name="method" id="method" value="doConsulta"/>
				<input type="hidden" name="idsRowsCheckedDF" id="idsRowsCheckedDF"/>
				<input type="hidden" name="idPolizaDF" id="idPolizaDF"/>
				<input type="hidden" name="codplanDF" id="codplanDF" value="${sessionScope.usuario.colectivo.linea.codplan}"/>
				<input type="hidden" name="codlineaDF" id="codlineaDF" value="${sessionScope.usuario.colectivo.linea.codlinea}"/>
				<input type="hidden" name="method" id="method"/>	
				<input type="hidden" name="danhoFaunaMaxParcelas" id="danhoFaunaMaxParcelas" value="${danhoFaunaMaxParcelas}"/>
				
				<!-- Bean -->
				<form:hidden path="idpoliza"                           id="idpoliza"/>
				
			</form>
			<!-- fin Pet. 643485 - FASE III DNF 23/12/2020 -->
			
			<form:form name="main3" id="main3" action="seleccionPoliza.html" method="post" commandName="polizaBean">
			
			    <!-- generales -->
				<input type="hidden" name="operacion"                  id="operacion"/>
				<input type="hidden" name="limpiar"                    id="limpiar"/>
				<input type="hidden" name="parcelasRepOK"              id="parcelasRepOK"/>
				<input type="hidden" name="sinInstalacionOK"           id="sinInstalacionOK"/>
				<input type="hidden" name="modoLectura"                id="modoLectura"                value="${datos.modoLectura }"/>
				<input type="hidden" name="vieneDeUtilidades"          id="vieneDeUtilidades"          value="${datos.vieneDeUtilidades }"/>
				<input type="hidden" name="origenllamada"              name="origenllamada"            value="${datos.origenllamada}"/>
				<input type="hidden" name="nifCif_cm"                  id="nifCif_cm"                  value="${sessionScope.usuario.asegurado.nifcif}"/>
				
				<!-- mantener checks en paginacion -->
				<input type="hidden" name="idsRowsChecked"             id="idsRowsChecked"             value="${idsRowsChecked}"/>
		        <input type="hidden" name="parcelasString"             id="parcelasString"             value="${parcelasString}"/>
		        <input type="hidden" name="marcarTodosChecks"          id="marcarTodosChecks"          value="${marcarTodosChecks}"/>
		        <input type="hidden" name="isClickInListado"           id="isClickInListado" />
		        
		        <!-- cambio pasivo  -->
			  	<input type="hidden" name="variedad_form_cm"           id="variedad_form_cm"           value=""/>
			  	<input type="hidden" name="cultivo_form_cm"            id="cultivo_form_cm"            value=""/>
			  	<input type="hidden" name="produccion_form_cm"         id="produccion_form_cm"         value=""/>
			  	<input type="hidden" name="incremento_form_ha_cm"      id="incremento_form_ha_cm"      value=""/>
			  	<input type="hidden" name="incremento_form_parcela_cm" id="incremento_form_parcela_cm" value=""/>
			  	<input type="hidden" name="checked_form_parcela_cm"    id="checked_form_parcela_cm"    value=""/>
			  	<input type="hidden" name="superficie_form_cm"         id="superficie_form_cm"         value=""/>
			  	<input type="hidden" name="fechaSiembra_form_cm"       id="fechaSiembra_form_cm"       value=""/>
			  	
				<!-- bean -->
				<form:hidden path="linea.lineaseguroid"                id="lineaseguroid" />
				<form:hidden path="linea.codlinea"                     id="codlinea" />
				<form:hidden path="linea.codplan"                      id="codplan" />
				<form:hidden path="idpoliza"                           id="idpoliza"/>
				<input type="hidden" id="fechaInicioContratacion" value="${polizaBean.linea.fechaInicioContratacion}"/>
				
                <!-- otros -->
                <input type="hidden" name="codPoliza"                                                  value=""/>
				<input type="hidden" name="codParcela"                                                 value=""/>
				<input type="hidden" name="isParcela"                  id="isParcela"                  value=""/>
				
				<input type="hidden" name="idpantalla"                 id="idpantalla"                 value="${datos.pantalla.pantalla.idpantalla }"/>
				<input type="hidden" name="despantalla"                id="despantalla"                value="${datos.pantalla.pantalla.descpantalla }"/>
				<input type="hidden" name="tipoListadoGrid"            id="tipoListadoGrid"  		<c:if test="${datos.tipoListadoGrid !=null}">value='${datos.tipoListadoGrid}'</c:if>/>
				
				<input type="hidden" name="listCodModulos"             id="listCodModulos"             value="${datos.listCodModulos }"/>
				<%-- <input type="hidden" name="listCodModulos_cm"          id="listCodModulos_cm"          value="${datos.listCodModulos_cm }"/> --%>
				
				<input type="hidden" name="lstParcelasPopUp"           id="lstParcelasPopUp"           value=""/>
							
				<input type="hidden" name="selectedRow" id="selectedRow" value="${datos.selectedRow}"/>
				<input type="hidden" name="idRowSelected" id="idRowSelected"  value="${datos.idRow}"/>
				
				<!-- Impresi&#243n -->
				<input type="hidden" name="formato" value=""/>
				<!-- Lupa tipo de capital -->
				<input type="hidden" id="codconcepto" name="codconcepto" value="126"/>
		
				<!-- ESC-30568 / GD-18421 -->
				<input type="hidden" id="rdtoOrientativoPulsado" name="rdtoOrientativoPulsado" value="${rdtoOrientativoPulsado}"/>
				<input type="hidden" id="rdtoHistPulsado" name="rdtoHistPulsado" value="${rdtoHistPulsado}"/>
				<!-- ESC-30568 / GD-18421 -->
		
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>

		<div class="panel2 isrt">
			<fieldset>
			<legend class="literal">Filtro</legend>		
				<fieldset>				
						<legend class="literal">Ubicaci&#243n</legend>				
						<table align="center">
								<tr>
									<td class="literal">Provincia</td>
									<td class="literal">
										<input type="text" id="provincia" name="provincia" size="2" maxlength="2" class="dato" onchange="javascript:lupas.limpiarCampos('desc_provincia','comarca','desc_comarca','termino','desc_termino','subtermino');" tabindex="1"
										<c:if test="${datos.filtro !=null }">
											value='${datos.filtro.termino.id.codprovincia }'
										</c:if> />	
										<input class="dato"	id="desc_provincia" name="desc_provincia" size="18" readonly="readonly"
										<c:if test="${datos.filtro !=null }">
											value='${datos.filtro.termino.provincia.nomprovincia }'
										</c:if> /> 
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Provincia','principio', '', '');"	alt="Buscar Provincia" title="Buscar Provincia" />	
									</td>
									<td class="literal">Comarca</td>
									<td class="literal">
										<input type="text"  id="comarca" name="comarca" size="2" maxlength="2" class="dato" onchange="javascript:lupas.limpiarCampos('desc_comarca','termino','desc_termino','subtermino');" tabindex="2"
										<c:if test="${datos.filtro !=null }">
											value='${datos.filtro.termino.id.codcomarca }'
										</c:if>/>	
										<input class="dato"	id="desc_comarca" name="desc_comarca" size="18" readonly="readonly"
										<c:if test="${datos.filtro !=null }">
											value='${datos.filtro.termino.comarca.nomcomarca }'
										</c:if> /> 
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Comarca','principio', '', '');"	alt="Buscar Comarca" title="Buscar Comarca" />
									</td>
									<td class="literal">T&#233rmino</td>
									<td class="literal">
										<input type="text"  id="termino" name="termino" size="2" maxlength="3" class="dato" onchange="javascript:lupas.limpiarCampos('desc_termino','subtermino');" tabindex="3"
										<c:if test="${datos.filtro !=null }">
											value='${datos.filtro.termino.id.codtermino }'
										</c:if>/>	
										<input class="dato"	id="desc_termino" name="desc_termino" size="24" readonly="readonly"
										<c:if test="${datos.filtro !=null }">
											value='${datos.filtro.termino.nomtermino }'
										</c:if> />												
									</td>
									<td class="literal">Subt&#233rmino</td>
									<td class="literal"><input type="text"  id="subtermino"  name="subtermino" size="1" maxlength="1" class="dato" onchange="this.value=this.value.toUpperCase();" tabindex="4"
										<c:if test="${datos.filtro !=null }">
											value='${datos.filtro.termino.id.subtermino }'
										</c:if> />	
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Termino','principio', '', '');"	alt="Buscar T&#233rmino" title="Buscar T&#233rmino" />
									</td>
								</tr>
						</table>
					</fieldset>
					<fieldset style="width: 21%;float:left">
						<legend class="literal">Id. Catastral</legend>
						<table align="center">												
									<tr>
										<td class="literal">Pol&#237gono</td>
										<td class="literal"><input type="text"  name="poligono" id="poligono" size="4" maxlength="4" class="dato" tabindex="5" 
											<c:if test="${datos.filtro !=null }">
												value='${datos.filtro.poligono }'
											</c:if>/></td>									
										<td class="literal">Parcela</td>
										<td class="literal"><input type="text"  name="parcela" id="parcela" size="4" maxlength="4" class="dato" tabindex="6"
											<c:if test="${datos.filtro !=null }">
												value='${datos.filtro.parcela }'
											</c:if> /></td>
									</tr>
						</table>
					</fieldset>
					<fieldset>
						<legend class="literal">SIGPAC</legend>
						<table align="center">
									<tr>
										<td class="literal">Prov</td>
										<td class="literal"><input type="text"  name="provSig" id="provSig" size="2" maxlength="2" class="dato" tabindex="7"
											<c:if test="${datos.filtro !=null }">
												value='${datos.filtro.codprovsigpac }'
											</c:if> /></td>
										<td class="literal">Term</td>
										<td class="literal"><input type="text"  name="TermSig"  id="TermSig" size="3" maxlength="3" class="dato" tabindex="8"
											<c:if test="${datos.filtro !=null }">
												value='${datos.filtro.codtermsigpac }'
											</c:if> /></td>
										<td class="literal">Agr</td>
										<td class="literal"><input type="text"  name="agrSig"  id="agrSig" size="3" maxlength="3" class="dato" tabindex="9" 
											<c:if test="${datos.filtro !=null }">
												value='${datos.filtro.agrsigpac }'
											</c:if>/></td>
										<td class="literal">Zona</td>
										<td class="literal"><input type="text"  name="zonaSig"  id="zonaSig" size="2" maxlength="2" class="dato" tabindex="10" 
											<c:if test="${datos.filtro !=null }">
												value='${datos.filtro.zonasigpac }'
											</c:if>/></td>
										<td class="literal">Pol</td>
										<td class="literal"><input type="text"  name="polSig"  id="polSig" size="3" maxlength="3" class="dato" tabindex="11"
											<c:if test="${datos.filtro !=null }">
												value='${datos.filtro.poligonosigpac }'
											</c:if> /></td>									
										<td class="literal">Parc</td>
										<td class="literal"><input type="text"  name="parcSig"  id="parcSig" size="5" maxlength="5" class="dato" tabindex="12"
											<c:if test="${datos.filtro !=null }">
												value='${datos.filtro.parcelasigpac }'
											</c:if> /></td>
										<td class="literal">Rec</td>
										<td class="literal"><input type="text"  name="recSig"  id="recSig" size="5" maxlength="5" class="dato" tabindex="13"
											<c:if test="${datos.filtro !=null }">
												value='${datos.filtro.recintosigpac }'
											</c:if>/></td>
									</tr>
							</table>
					</fieldset>
					<table align="center" width="101%" >
							<tr>
								<td class="literal" rowspan="2" style="padding: 30px 0px 30px 0px;" >Nombre</td>
								<td class="literal" rowspan="2" style="padding: 30px 0px 30px 0px;">
									<input type="text" name="nombre"  size="19" id="nombre" class="dato" tabindex="14"
									<c:if test="${datos.filtro !=null }">
												value='${datos.filtro.nomparcela }'
											</c:if>/>
								</td>
								<td class="literal" rowspan="2" style="padding: 30px 0px 30px 0px;">Cultivo</td>
								<td class="literal" rowspan="2" style="padding: 30px 0px 30px 0px;">
									<input type="text" id="cultivo" name="cultivo" size="2" maxlength="3" class="dato" onchange="javascript:lupas.limpiarCampos('desc_cultivo','variedad','desc_variedad');" tabindex="15"
									<c:if test="${datos.filtro !=null }">
												value='${datos.filtro.codcultivo }'
											</c:if>/>
									<input class="dato"	id="desc_cultivo" name="desc_cultivo" size="17" readonly="readonly"
									<c:if test="${datos.filtro !=null }">
											value='${datos.filtro.variedad.cultivo.descultivo }'
									</c:if> /> 
									<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Cultivo','principio', '', '');"	alt="Buscar Cultivo" title="Buscar Cultivo" />
								</td>
								<td class="literal" rowspan="2" style="padding: 30px 0px 30px 0px;">Variedad</td>
								<td class="literal" rowspan="2" style="padding: 30px 0px 30px 0px;"><input type="text"  id="variedad" name="variedad" size="2" maxlength="3" class="dato" onchange="javascript:lupas.limpiarCampos('desc_variedad');" tabindex="16"
									<c:if test="${datos.filtro !=null }">
												value='${datos.filtro.codvariedad }'
											</c:if>/>	
									<input class="dato"	id="desc_variedad" name="desc_variedad" size="17" readonly="readonly"
									<c:if test="${datos.filtro !=null }">
											value='${datos.filtro.variedad.desvariedad }'
										</c:if> /> 
									<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Variedad','principio', '', '');"	alt="Buscar Variedad" title="Buscar Variedad" />		
								</td>
								<td class="literal">
									<table>
										<tr> 
											<td class="literal" style="padding: 7px 10px 0px 0px;">Tipo capital</td>
											<td class="literal" style="padding: 7px 0px 0px 0px;">													
													<input type="text" id="capital" name="capital" size="2" maxlength="3" class="dato" onchange="javascript:lupas.limpiarCampos('desc_capital');" tabindex="1" value="${datos.capital }"/>
													<input type="text" class="dato"	id="desc_capital" name="desc_capital" size="19" readonly="true" value="${datos.desc_capital}"/>
													<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('FactoresTipoCapitalIN','principio', '', '');"	alt="Buscar Tipo Capital" title="Buscar Tipo Capital" />
																									
											</td>
										</tr>
										<tr>
											<td class="literal" style="padding: 1px 0px 0px 0px;" >S. Cultivo</td>
											<td class="literal" style="padding: 1px 0px 0px 0px;">
												<input type="text" class="dato" size="2" onchange="javascript:lupas.limpiarCampos('dessistemaCultivo');" maxlength="3" id="sistemaCultivo" name="sistemaCultivo" value="${datos.sistemaCultivo }"/>
												<input type="text" class="dato" size="19" maxlength="30" id="dessistemaCultivo" name="dessistemaCultivo" readonly="true" value="${datos.dessistemaCultivo }"/>
												<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('SistemaCultivo','principio', '', '');"	alt="Buscar Sistema Cultivo" title="Buscar Sistema Cultivo" />
												<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_dessistemacultivo"> *</label>
											</td>
 										</tr>
 									</table> 
								</td> 

							</tr>
					</table>
					
				    <div class="literal" style="width:870px;margin:0px 5 0 5;">
					    <div id="parcelas" style="float:left;margin-right:100px" class="handOver" 
					         onmouseover="javascript:radioElem_onmouseover('parcelas')"
					         onmouseout="javascript:radioElem_onmouseout('parcelas')"
					         onclick="javascript:tipoListado_onclick2('parcelas')">
					         Parcelas
					         <input id="" type="radio" name="listado" value="parcelas" <c:if test="${datos.tipoListadoGrid == 'parcelas' }">checked</c:if>>
					            
					    </div>
						<div id="instalaciones" style="float:left;margin-right:100px" class="handOver"
						     onmouseover="javascript:radioElem_onmouseover('instalaciones')"
					         onmouseout="javascript:radioElem_onmouseout('instalaciones')"
					         onclick="javascript:tipoListado_onclick2('instalaciones')">
					         Instalaciones
					         <input id="" type="radio" name="listado" value="instalaciones" <c:if test="${datos.tipoListadoGrid == 'instalaciones' }">checked</c:if>>
					    </div>
						<div id="todas" style="float:left;margin-right:100px" class="handOver"
						     onmouseover="javascript:radioElem_onmouseover('todas')"
					         onmouseout="javascript:radioElem_onmouseout('todas')"
					         onclick="javascript:tipoListado_onclick2('todas')">
					         Todas
					         <input id="" type="radio" name="listado" value="todas" <c:if test="${datos.tipoListadoGrid == 'todas'}">checked</c:if>>
					     </div>
					     <div>

							Tipo Rendimiento
							 <select name="rdtoHist" id="rdtoHist" class="dato" tabindex="9" style="width:200px">
                   				<option	value="">Todos</option> 
								<c:forEach items="${datos.listaTipoRendimientos}" var="tiposRdto">
										<option value="${tiposRdto.idrdto}">${tiposRdto.descripcion}</option>
								</c:forEach>
							</select>
									
						</div>
				    </div>	
			</fieldset>
		</div>
		
</form:form>	
                <div id="grid" >
					<display:table excludedParams="parcelasString"
					        requestURI="seleccionPoliza.html" id="listaParcelas_cm" class="LISTA" summary="" name="${datos.listadoParcelas}" 
					        decorator="${modelTableDecorator}" pagesize="${numReg}" sort="list" defaultsort="17"
							clearStatus="${clearStatus}" style="width:100%;border-collapse:collapse;border-bottom:none">
                        <c:choose>
							<c:when test="${modoLectura == 'modoLectura'}">
								 <display:column class="accionesDisplayTagColumn" headerClass="cblistaImg" title="Acciones" media="html" property="admActionsConsulta" sortable="false" style="width:100px;text-align:center" />
							</c:when>
							<c:otherwise>
								 <display:column class="accionesDisplayTagColumn" headerClass="cblistaImg" title="Acciones" media="html" property="admActions" sortable="false" style="width:100px;text-align:center" />
							</c:otherwise>
						</c:choose>
                       	<display:column class="literal" headerClass="cblistaImg" title="PRV"            property="codprovincia"  style="width:50px;"  sortable="true" sortProperty="codprovincia" sortName="codprovincia" comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico"/>
						<display:column class="literal" headerClass="cblistaImg" title="CMC"            property="codcomarca"    style="width:50px;"  sortable="true" sortProperty="codcomarca" comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico"/>
						<display:column class="literal" headerClass="cblistaImg" title="TRM"            property="codtermino"    style="width:50px;"  sortable="true" sortProperty="codtermino" sortName="codtermino" comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico"/>
						<display:column class="literal" headerClass="cblistaImg" title="SBT"            property="codsubtermino" style="width:50px;"  sortable="true" sortProperty="codsubtermino" sortName="codsubtermino" comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico"/>
						<display:column class="literal" headerClass="cblistaImg" title="CUL"            property="codcultivo"    style="width:50px;"  sortable="true" sortProperty="codcultivo" sortName="codcultivo" comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico"/>
						<display:column class="literal" headerClass="cblistaImg" title="VAR"            property="codvariedad"   style="width:50px;"  sortable="true" sortProperty="codvariedad" sortName="codvariedad" comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico"/>
						
						<display:column class="literal" headerClass="cblistaImg" title="N&ordm;" property="hojaNum"  style="width:50px;"  sortable="true"  comparator = "com.rsi.agp.core.comparators.TableComparatorHojaNumero"/>
						
						<c:if test="${datos.codEstadoPolizaMayor3 == 'true'}">
						    <display:column class="literal" headerClass="cblistaImg" title="N&ordm;"         property="numero"   style="width:50px;"  sortable="true"  comparator="com.rsi.agp.core.comparators.TableComparatorHojaNumero" />
						</c:if>
						
						<display:column class="literal" headerClass="cblistaImg" title="Id. Cat/SIGPAC" property="idCat"         style="width:150px;" sortable="true" comparator="com.rsi.agp.core.comparators.TableComparatorSigPacs"/>
						<display:column class="literal" headerClass="cblistaImg" title="Nombre"         property="nomPar"        style="width:150px;" sortable="true" sortProperty="nomPar" />
						
						<!--  Superficie  metros cuadrados -->
						<c:if test="${datos.tipoListadoGrid == 'instalaciones'}">
							<display:column class="literal" headerClass="cblistaImg" title="m"           property="superf" style="width:85px;"  sortable="true" sortProperty="superf" comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico"></display:column>
						</c:if>
						<c:if test="${datos.tipoListadoGrid == 'todas'}">
							<display:column class="literal" headerClass="cblistaImg" title="Super./m" property="superf"  style="width:85px;"  sortable="true" sortProperty="superf" comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico"></display:column>
						</c:if>
						<c:if test="${datos.tipoListadoGrid == 'parcelas'}">
							<display:column class="literal" headerClass="cblistaImg" title="Superficie"  property="superf" style="width:85px;"  sortable="true" sortProperty="superf" comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico"></display:column>
						</c:if>
						<!-- producci&#243n: si es instalaci&#243n no debe salir -->
						<c:if test="${datos.tipoListadoGrid == 'todas' || datos.tipoListadoGrid == 'parcelas'}">
						    <display:column class="literal" headerClass="cblistaImg" title="Prod."       property="produccion"    style="width:55px;"  sortable="true" sortProperty="produccion" comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico"/>
						 </c:if>
						 
						<display:column class="literal" headerClass="cblistaImg" title="Precio"          property="precio"        style="width:60px;"  sortable="true"  sortProperty="precio" comparator = "com.rsi.agp.core.comparators.TableComparatorNumerico"/>
						<display:column class="literal" headerClass="cblistaImg" title="T.Capital"       property="tcapital"      style="width:150px;" sortable="true" sortProperty="tcapital"/>
						
						<!-- Fecha Fin Garantias: si es instalaci&#243n no debe salir -->
						<c:if test="${datos.tipoListadoGrid == 'todas' || datos.tipoListadoGrid == 'parcelas'}">
							<display:column class="literal" headerClass="cblistaImg" title="F.Garantia"   property="fechaFin"    style="width:55px;"  sortable="false"/>
						</c:if>
						
						<display:column class="literal" headerClass="cblistaImg" title=" X" property="checkCambioMasivo" style="width:30px;text-align:center"/> 
						<display:column class="literal" title=" " property="ordenacion" style="visibility:hidden;"/>
						
						<display:setProperty name="paging.banner.some_items_found" value='' />
						<display:setProperty name="paging.banner.one_item_found" value='' />
						<display:setProperty name="paging.banner.all_items_found" value='' />${requestParameters['d-7577386-p']}
						
						<display:footer>
			        		<tr style="background-color:#e5e5e5">
			        			<td class="literal" colspan="2">N&#176; Total Parcelas: </td>
			        			<td class="literal" style="color:green">${numParcelasListado}</td>
			        			<td class="literal" colspan="2">Seleccionados:</td>
			        			<td class="literal" style="color:green"><label id="sel"/></td>
			        			<td class="literal" colspan="5"><a class="bot" id="btnTotalProd"     href="javascript:DameLstTotalProduccion('${idpoliza}');"     title="Total Produccion">Total Prod./Sup.</a></td>
			        			<!-- <td class="literal" style="color:green"></td> -->
			        			<!-- <td class="literal" colspan="2">Total superficie: <span style="color:green">${superficieTotal}</span></td> -->
			        			<td class="literal"></td>
			        			<c:if test="${datos.tipoListadoGrid == 'todas'}">	
			        			    <td class="literal"></td>
			        			</c:if>	
			        			<c:if test="${datos.tipoListadoGrid == 'parcelas'}">	
			        			    <td class="literal"></td>
			        			</c:if>
			        			<td class="literal" colspan="2"><div style="float:right">Marcar Todos:</div></td>
			        			<td class="literal"style="width:30px;text-align:center">
			        			 	<input type="checkbox" id="selTodos" name="selTodos" class="dato" onclick="selectAllChecks(this)"/>
			        			</td>
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
				
			    <div style="color:#626262;font-family:tahoma,verdana,arial;font-size:11px;font-weight:bold;text-align: right;">
			        <!--  
	        		<span style="padding-right:20px">
	        		    Marcar Todos:
	        		    <input type="checkbox" id="selTodos" name="selTodos" onclick="selCheckTodos()" style="color:#111111" />
	        		</span>
	        		-->
					<c:if test="${datos.tipoListadoGrid == 'todas'}">
						<span>
						    Instalaciones&nbsp;
						    
						</span>
						<span style="width:12px;height:12px;background-color:#A9F5A9"></span>
					</c:if>
				</div>
				
		</div>

		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		<%@ include file="/jsp/common/static/overlayCambioMasivo.jsp"%>
		<%@ include file="/jsp/common/static/overlayDanhosFauna.jsp"%>
		
		<!--              -->		
		<!-- PANEL AVISOS -->
		<!--              -->
		<div id="parcelasRep" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
            <!--  header popup -->
		    <div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
		        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
		            Aviso
		        </div>
		        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
		            <span onclick="cerrarPopUp()">x</span>
		        </a>
		    </div>
			<!--  body popup -->
			<div class="panelInformacion_content">
				<div id="panelInformacion" class="panelInformacion">
					<div id="txt_parcelasRepetidas" style="display:none">Hay parcelas repetidas.</div>
					<div id="txt_noHayInstalaciones" style="display:none">No hay ninguna instalaci&#243n dada de alta.</div>
				</div>
				<div style="margin-top:15px">
				    <a class="bot" href="javascript:cerrarPopUp()" title="Cancelar">Cancelar</a>
				    <a class="bot" href="javascript:continuar()" title="Continuar">Continuar</a>
				</div>
			</div>
		</div>
		
		
		<%@ include file="/jsp/moduloPolizas/polizas/popupCambioMasivo.jsp"%>
		<%@ include file="/jsp/moduloPolizas/polizas/popupTotalProduccion.jsp"%>
		<%@ include file="/jsp/moduloPolizas/polizas/popupDuplicarMasivo.jsp"%>
		<%@ include file="/jsp/moduloPolizas/polizas/popupDanhosFauna.jsp"%>
		
		<%@ include file="/jsp/common/lupas/lupaCultivo.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaVariedad.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaProvincia.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaComarca.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaTermino.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaSistemaCultivo.jsp"%>
   <%-- <%@ include file="/jsp/common/lupas/lupaTipoCapital.jsp"%> --%> 
		<%@ include file="/jsp/common/lupas/lupaTipoCapitalIN.jsp"%>
							

	</body>
</html>