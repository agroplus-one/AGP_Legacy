<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" 
	import="es.agroseguro.acuseRecibo.AcuseRecibo,
            es.agroseguro.acuseRecibo.Documento,
            java.util.*"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
<head>
	<title>Resultado múltiple grabación definitiva pólizas</title>
	
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
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	
	
	<script language="javascript">
		
		// Hace submit del formulario de pasar a definitiva múltiple
		function validarDeNuevo(){				
			var frm = document.getElementById ('pasarADefinitivaMultiple');
			frm.grabFueraContratacion.value="";
			frm.submit();
		}
		
		// Hace submit del formulario de volver
		function volver () {
			var frm = document.getElementById ('volver');
			frm.submit();
		}
		
				
		function corregir(idPoliza,tipoRef) {
			window.opener.editarPlzDefMulti(idPoliza);
			window.opener.focus();
			
		/* if (tipoRef == 'P'){
				$("#operacion").val("listParcelas");
				$("#idpoliza").val(idPoliza);
				$('#consulta').attr('target', '_blank');
				$("#consulta").submit();
			}else if (tipoRef == 'C'){
				$("#method").val('doConsulta');
				$("#idpolizaCpl").val(idPoliza);
				$('#consultaCpl').attr('target', '_blank');
				$("#consultaCpl").submit();
			}
		 */	
		}
		
		function updateParcela(codPoliza, numhoja, numparcela){
			$("#operacion").val("modificarParcelaError");
			$("#idpoliza").val(codPoliza);
			$("#numhoja").val(numhoja);
			$("#numparcela").val(numparcela);
			$("#idpoliza").val(codPoliza);
			$('#consulta').attr('target', '_blank');
			$("#consulta").submit();	        
		}
		function marcatodos(){
			$('input[type=checkbox]').each( function() { 			        		    
		    	$(this).attr('checked',true);
			});
		}
		function desmarcatodos(){
			$('input[type=checkbox]').each( function() { 	        		    
		    	$(this).attr('checked',false);
			});
		}
		function forzarPasoDefinitiva(){
			var frm = document.getElementById ('forzarPasoADefinitivaMultiple');
			$("input[type=checkbox]").each(function() { 
				if ($(this).is(':checked')){
		    		if ($(this).val() != "on"){
		    			frm.idsRowsChecked.value =frm.idsRowsChecked.value +";"+ $(this).val();
		    		}
		    	}
			});
			frm.submit();
		}
		
		function showdata (idPoliza) {
			$("#data3_" + idPoliza).show();
			$("#data2_" + idPoliza).show();
			$("#data1_" + idPoliza).hide();
		}
		
		function hidedata (idPoliza) {
			$("#data3_" + idPoliza).hide();
			$("#data2_" + idPoliza).hide();
			$("#data1_" + idPoliza).show();
		}
		
		
	</script>
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4');">
	
	
	<form name="consulta" id="consulta" target="_blanco" action="seleccionPoliza.html" method="post">
		<input type="hidden" name="operacion" id="operacion" value="listParcelas" />
		<input type="hidden" name="idpoliza" id="idpoliza" value="${idpoliza}"/>
		<input type="hidden" name="numhoja" id="numhoja" value=""/>
		<input type="hidden" name="numparcela" id="numparcela" value=""/>
	</form>
	<form name="consultaCpl" id="consultaCpl" target="_blanco" action="polizaComplementaria.html" method="post">
		<input type="hidden" name="operacion" id="operacion" value="listParcelas" />
		<input type="hidden" name="idpoliza" id="idpoliza" value="${idpoliza}"/>
		<input type="hidden" name="idpolizaCpl" id="idpolizaCpl" />
		<input type="hidden" name="numhoja" id="numhoja" value=""/>
		<input type="hidden" name="numparcela" id="numparcela" value=""/>
		<input type="hidden" name="method" id="method"/>
	</form>
	
	<!-- MPM - 07-05-12 -->
	<!-- Formulario para el paso a definitiva múltiple -->
	<form name="pasarADefinitivaMultiple" id="pasarADefinitivaMultiple" action="pasoADefinitiva.html" method="post">
		<input type="hidden" name="method" id="method" value="doPasarADefinitivaMultiple"/>				
		<input type="hidden" name="resultadoValidacion" id="resultadoValidacion" value="true"/>
		<input type="hidden" name="grabFueraContratacion" id="grabFueraContratacion" value="grabFueraContratacion"/>						
		<input type="hidden" name="idsRowsChecked" id="idsRowsChecked" value="${idsPolizaKO}"/>
		
	</form>
	<form name="forzarPasoADefinitivaMultiple" id="forzarPasoADefinitivaMultiple" action="pasoADefinitiva.html" method="post">
		<input type="hidden" name="method" id="method" value="doPasarADefinitivaMultiple"/>				
		<input type="hidden" name="resultadoValidacion" id="resultadoValidacion" value="true"/>
		<input type="hidden" name="grabFueraContratacion" id="grabFueraContratacion" value="true"/>						
		<input type="hidden" name="idsRowsChecked" id="idsRowsChecked" value="${idsRowsChecked}"/>
		
	</form>
	<!-- Formulario para la vuelta al listado de utilidades -->
	<form name="volver" id="volver" action="utilidadesPoliza.html" method="post">
		<input type="hidden" name="operacion" id="operacion" value="volver" />
	</form>
	
	<%-- <div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">
				    <c:if test="${algunError eq true}">
						<a class="bot" id="btnValidarTodo" href="javascript:validarDeNuevo()">Enviar de nuevo</a>
					</c:if>
					<a class="bot" id="btnVolver"  href="javascript:volver()">Volver</a>
				</td>
			</tr>
		</table>
	</div>	 --%>
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Resultado grabación definitiva pólizas</p>
	
	<%@ include file="/jsp/common/static/avisoErrores.jsp"%>	
	
	<br/>
	
	<c:choose>
		<c:when test="${listIdPolizas!=null}">
			<c:forEach items="${mapaPolizas}" var="entry">
			<div id ="panelInfo" class="panel2nuevo" style=".isrt	{ background-color: #FFFFFF; font-family: tahoma,arial,verdana; font-size: 11px; color: #666666; text-align: left; vertical-align: top; padding-left: 5px; padding-top: 2px;width=100%; padding:3;}">
				<input type="hidden" name="pol_estado" id="pol_estado${entry.key}" value="2"/>
				<fieldset class="" >					
					<table width="100%" id="tablaDatos">						
						<tr align="left" width ="100%">							
							<td class="literal" vlign="left">
								<fieldset style="width:100%">
									<legend class="literal">Datos de la p&oacute;liza</legend>
									<table width="100%" align="center" cellspacing="2">
										<tr>
											<td class="literal" width="40px">Plan:</td>
											<td width="40px" class="detalI">${entry.value.linea.codplan}</td>
											<td class="literal" width="50px">Línea:</td>
											<td width="300px" class="detalI">${entry.value.linea.codlinea } - ${entry.value.linea.nomlinea}</td>
							  				<td class="literal" width="75px">Asegurado:</td>
							 				<td width="300px" class="detalI">${entry.value.asegurado.nombreCompleto }</td> 
							 				<td class="literal" align="left">CIF/NIF:</td>
											<td width="250px" class="detalI" align="left">${entry.value.asegurado.nifcif}</td>
											<td class="literal" align="center">Referencia:</td>
											<td width="300px" class="detalI">${entry.value.referencia}</td>
											<td class="literal" align="right">Módulo:</td>
											<td width="250px" class="detalI">${entry.value.codmodulo}</td>
										</tr>										
									</table>								
								</fieldset>		
							</td>				
																														
						
						<tr align="left">	
							<td class="literal" align="right">
								<c:if test="${entry.value.errorEnPasoADefinitiva eq true}">
							     <div id="btnCorregir${entry.key}">
								 	<a class="bot" id="btnCorregir" href="#" onClick="javascript:corregir(${entry.key},'${entry.value.tipoReferencia}');">Corregir</a>
								 </div>
								</c:if>
							</td>
						</tr>
						<tr align="left">	
							<td class="literal">
								     <div id="data${entry.key}">
								     	${entry.value.mensaje}
									 </div>
							</td>
							<c:if test="${entry.value.forzarPasoADef}">
								<c:set var="mostrarBoton" value="true"/>
								<td class="literal"  align="left">
								
									 <input type="checkbox" id="check${entry.key}" name="check${entry.key}" 
									 	class="dato" value="${entry.key}"/>
								</td>
							</c:if>
						</tr>
					</table> 
						
					<!-- Sólo se pinta si hay errores en el acuse de recibo -->	
					<c:if test="${not empty entry.value.listErroresAcuse}">
						<table width="100%"  id="data1_${entry.key}">
							<tr>
								<td align="right">
									<a href="#" onclick="javascript:showdata(${entry.key})" title="Mostrar errores">
										<img src="jsp/img/folderclose.gif" alt="Desglose" title="Desglose" />&nbsp;&nbsp;Mostrar errores
									</a> 
								</td>
							</tr>
						</table>
						<table width="100%" style="display:none" id="data2_${entry.key}">
							<tr>
								<td align="right">
									<a href="#" onclick="javascript:hidedata(${entry.key});" title="Ocultar errores">
										<img src="jsp/img/folderopen.gif" alt="Desglose" title="Desglose" />&nbsp;&nbsp;Ocultar errores
									</a> 
								</td>
							</tr>
						</table>
						<div id="data3_${entry.key}" style="display:none" align="center">
							
							<table id="error" class="LISTA" width="80%" style="width: 80%;" >
								<thead>
									<tr>
										<th class="cblistaImg sortable">C&oacute;digo del Error</th>
										<th class="cblistaImg">Tipo de Error</th>
										<th class="cblistaImg">Descripción del Error</th>
										<th class="cblistaImg">
											<c:if test="${entry.value.linea.esLineaGanadoCount eq 1}">
												Explotaci&oacute;n
											</c:if>
											<c:if test="${entry.value.linea.esLineaGanadoCount eq 0}">
												N&uacute;mero de Parcela
											</c:if>
										</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${entry.value.listErroresAcuse}" var="errorAcuse">
										<tr class="odd">								
											<td class=literal style="TEXT-ALIGN: left">${errorAcuse.codigo}</td>
											<td class=literal style="TEXT-ALIGN: left">
												<c:if test="${errorAcuse.tipo eq 1}">
													<img src="jsp/img/displaytag/cancel.png" alt="Rechazado" title="Rechazado"/>
												</c:if>
												<c:if test="${errorAcuse.tipo eq 2}">
													<img src="jsp/img/displaytag/warning.gif" alt="Con Errores" title="Con Errores"/>
												</c:if>
												<c:if test="${errorAcuse.tipo eq 3}">
													<img src="jsp/img/displaytag/accept.png" alt="Correcto" title="Correcto"/>
												</c:if>
											</td>
											<TD class=literal style="TEXT-ALIGN: left">${errorAcuse.descripcion}</TD>										
											<TD class=literal style="TEXT-ALIGN: left">
												<c:if test="${errorAcuse.numero ne -1}">
													<c:if test="${entry.value.linea.esLineaGanadoCount eq 1}">
														${errorAcuse.numero}
													</c:if>
													<c:if test="${entry.value.linea.esLineaGanadoCount eq 0}">
														${errorAcuse.hoja} - ${errorAcuse.numero}
													</c:if>
												</c:if>
											</TD>
											
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</c:if>
					
			</fieldset>
		</div>
		</c:forEach>
		<c:if test="${mostrarBoton}">
			<div id="botonDefinitiva" align="right">
				<table width="100%" >
					<tr>
						<td align="right">
							<a href="javascript:forzarPasoDefinitiva();" class="bot" >Forzar paso a definitiva</a>
						</td>
						<td align="right" class="literal" width="13%">
							Marcar Todos <input type="checkbox" id="marcaTodosDefMul" name="marcaTodosDefMul" 
				    						onclick="javascript:this.checked? marcatodos(): desmarcatodos()" />
						</td>
					</tr>
				</table>
			</div>
		</c:if>
		</c:when>
		<c:otherwise>
			No hay polizas para mostrar.
		</c:otherwise>
	</c:choose>			
	</div>			
	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
</body>
</html>