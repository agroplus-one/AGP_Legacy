<%@page contentType="text/html;" %>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>

<%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", 0);
%>


<html>
	<head> 
		<title>Agroplus - Campos de Máscara</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
				
    	<script type="text/javascript" src="jsp/js/util.js"></script>  
    	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
    	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>		
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		
		<script type="text/javascript">
		
		
		    // Para evitar el cacheo de peticiones al servidor
		    /*
	        $(document).ready(function(){
	             var URL = UTIL.antiCacheRand(document.getElementById("main").action);
				 document.getElementById("main").action = URL;    
	        });
	        */
		
			var listDiccionarioDatosCampoLimite = null;
			
			$(document).ready(function(){
				$('#selectCodtablacondicionado').change(function() {
				
					$("#selectCodconceptomasc").empty();
					$("#divDesconceptomas").empty();
					$("#divDeducible").empty();
					
					inicializarSelectCodconceptomasc("");				
				});
						
				$('#main').validate({	
	 				errorLabelContainer: "#panelAlertasValidacion",
	 				wrapper: "li",
	 				onfocusout: false, 
					 highlight: function(element, errorClass) {
					 	$("#campoObligatorio_"+element.id).show();
  					 },
  					 unhighlight: function(element, errorClass) {
					 	$("#campoObligatorio_"+element.id).hide();
  					 },
					rules: { 
							  "tablaCondicionado.codtablacondicionado": "required",
							  "diccionarioDatosByCodconceptomasc.codconcepto": "required",
							  "diccionarioDatosByCodconceptoasoc.codconcepto": "required"
							},
					messages: {
							   "tablaCondicionado.codtablacondicionado": "El campo Tipo Máscara es obligatorio",
							   "diccionarioDatosByCodconceptomasc.codconcepto": "El campo Campo Máscara es obligatorio",
							   "diccionarioDatosByCodconceptoasoc.codconcepto": "El campo Campo de entrada asociado es obligatorio"
							  }
							  
				});
				
				if($("#id").val()!= ""){
					
						$('#accion').val("modificar");
						$('#botonAlta').hide();
						$('#botonModificar').show(); 				
					} 
				else{
					$("#botonModificar").hide();
				}					
						
			
				$('#selectCodconceptomasc').change(function(event) {
				
					var idCodconceptomasc = $(event.target).val();
					
					$("#divDesconceptomas").empty();
					$("#divDeducible").empty();
					
					$.each(listDiccionarioDatosCampoLimite, function(index, value) { 
	 					
	 					if(value.codconcepto == idCodconceptomasc){
	 					
	 						$("#divDesconceptomas").html(value.desconcepto);
	 						$("#divDeducible").html(value.deducible);
							return;	 						
	 					}
	 					
					});
				
				});
				
				inicializarSelectCodconceptomasc('${campoMascaraBean.diccionarioDatosByCodconceptomasc.codconcepto}');
				
					
				
			});
			
			function inicializarSelectCodconceptomasc(valueInit){
			
				var idCodtablacondicionado = $("#selectCodtablacondicionado").val();
				
				if(idCodtablacondicionado != ''){
					
	  				$.ajax({
			            url: "camposMascara.html",
			            data: "accion=listCamposMascara_ajax&idTablaCondicionado="+idCodtablacondicionado,
			            dataType: "json",
			            success: function(datos){
			                listDiccionarioDatosCampoLimite = eval(datos);
							$("#selectCodconceptomasc").removeOption(/./);
							$("#selectCodconceptomasc").addOption("","-- Seleccione una opción --");
							$.each(listDiccionarioDatosCampoLimite, function(index, value) { 
			 					$("#selectCodconceptomasc").addOption(value.codconcepto, value.codconcepto+" - "+value.nomconcepto);
							});
							$("#selectCodconceptomasc").selectOptions(valueInit);
							
							if(valueInit != '')
								$('#selectCodconceptomasc').change();
			            },
			            beforeSend: function(){
			            	$("#ajaxLoading_selectCodconceptomasc").show();
            			},
            			complete: function(){
            				$("#ajaxLoading_selectCodconceptomasc").hide();
            			},
			            type: "POST"
					});
				}
				
			}

			function limpiar(){
			
				$("#selectCodtablacondicionado").selectOptions("");
				$("#selectCodconceptomasc").empty();
				$("#selectCodconceptoasoc").selectOptions("");
				consultar();
			}
			
			function consultar(){
				
				$('#id').val('');
				$("#main").validate().cancelSubmit = true;
				$('#accion').val("consultar");
				$('#main').submit();			
			}
			
			function editar(idCampoMascara){

				$('#id').val(idCampoMascara);
				$("#botonAlta").hide();
				$("#botonModificar").show();
				$('#accion').val("editarCampoMascara_ajax");
				$("#panelInformacion").hide();
				$("#panelAlertasValidacion").hide();
				
				$.ajax({
			            url: "camposMascara.html",
			            data: "accion=editarCampoMascara_ajax&idCampoMascara="+idCampoMascara,
			            dataType: "json",
			            success: function(datos){
			            	$('#id').val(datos.id);
			            	$("#selectCodtablacondicionado").selectOptions(""+datos.codtablacondicionado);
			            	inicializarSelectCodconceptomasc(""+datos.codconceptomasc);
			            	$("#selectCodconceptoasoc").selectOptions(""+datos.codconceptoasoc);
			            },
			            type: "POST"
					});
				
			
			}
			
			function modificar(idCampoMascara){

		        $("#panelInformacion").hide();
				$('#accion').val("modificar");
				$('#main').submit();			    
			}
			
			function eliminar(idCampoMascara){

				if(confirm('¿Está seguro de que desea eliminar el registro seleccionado?')){
					
					$("#main").validate().cancelSubmit = true;
					$('#id').val(idCampoMascara);
					$('#accion').val("eliminar");
					$('#main').submit();

				}			
			}
			
			function alta(){
			
		        $("#panelInformacion").hide();
		        
				$('#id').val('');
				$('#accion').val("alta");
				$('#main').submit();			
			}		
			
		</script>
	</head>
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="generales.fijarFila()">

		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuLateralTaller.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="right">
						<a class="bot" id="botonAlta" href="javascript:alta();">Alta</a>
						<a class="bot" id="botonModificar" href="javascript:modificar();">Modificar</a>
						<a class="bot" href="javascript:consultar();">Consultar</a>
						<a class="bot" href="javascript:limpiar();">Limpiar</a>
					</td>
				</tr>
			</table>
		</div>
		
		
	
		<div class="conten" style="padding:3px;width:100%">
			<p class="titulopag" align="left">Campos posibles de Máscaras y campos asociados</p>
			<form:form name="main" id="main" action="camposMascara.html" method="post" commandName="campoMascaraBean">
						
			<input type="hidden" id="accion" name="accion" />
			<form:hidden path="id" id="id"/>
			<div id="panelErrores" class="errorForm">* Campos obligatorios</div>
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			<div class="panel2 isrt">
				<table width="80%" align="center" style="margin:0 auto;">
					<tr>
						<td class="literal" width="80px">Tipo Máscara</td>
						<td align="left">
							<form:select path="tablaCondicionado.codtablacondicionado" cssClass="dato" tabindex="12" id="selectCodtablacondicionado" cssStyle="width:320px">
								<form:option value="">-- Seleccione una opción --</form:option>
								<c:forEach items="${listTiposCamposLimites}" var="tipoCampoLimite">
									<form:option value="${tipoCampoLimite.codtablacondicionado}">${tipoCampoLimite.codtablacondicionado} - ${tipoCampoLimite.destablacondicionado}</form:option>
								</c:forEach>
							</form:select><label class="campoObligatorio" id="campoObligatorio_selectCodtablacondicionado" title="Campo obligatorio"> *</label>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<fieldset class="fieldset_alone" style="width: 85%">
								<table width="100%">
									<tr align="left">
										<td class="literal" width="215px">Campo Máscara</td>
										<td class="literal" colspan="2">
											<form:select path="diccionarioDatosByCodconceptomasc.codconcepto" cssClass="dato" tabindex="12" id="selectCodconceptomasc" cssStyle="width:320px">
											</form:select><label class="campoObligatorio" id="campoObligatorio_selectCodconceptomasc" title="Campo obligatorio"> *</label>
											<img id="ajaxLoading_selectCodconceptomasc" src="jsp/img/ajax-loading.gif" width="16px" style="cursor:hand;cursor:pointer;display:none" height="11px" />
										</td>
									</tr>
									<tr>
									<td colspan="4"><hr></td>
									</tr>
									<tr align="left">
										<td class="literal" style="vertical-align: top">Descripcion</td>
										<td class="literal">
											<div id="divDesconceptomas" class="fieldset_alone" style="height: 150px; width:320;padding: 3px"></div>
										</td>
										<td class="literal" style="vertical-align: top">Deducible</td>
										<td class="literal" style="vertical-align: top">
											<div id="divDeducible" class="fieldset_alone" style="height: 15px;width: 15px;text-align: center;padding:1px"></div>
										</td>
										
									</tr>
								</table>
							</fieldset>
						</td>
					</tr>
					
					<tr>
						<td class="literal">Campo de entrada asociado</td>
						<td class="dato">
							<form:select path="diccionarioDatosByCodconceptoasoc.codconcepto" cssClass="dato" tabindex="12" id="selectCodconceptoasoc" cssStyle="width:320px">
								<form:option value="">-- Seleccione una opción --</form:option>
								<c:forEach items="${listDiccionarioDatos}" var="diccionarioDato">
									<form:option value="${diccionarioDato.codconcepto}">${diccionarioDato.codconcepto} - ${diccionarioDato.nomconcepto}</form:option>
								</c:forEach>
							</form:select><label class="campoObligatorio" id="campoObligatorio_selectCodconceptoasoc"  title="Campo obligatorio"> *</label>
						</td>
					</tr>
				</table>				
			</div>	
		</form:form>
		<div class="grid" style="">	
	        <display:table requestURI="camposMascara.html" id="listCamposMascara" class="LISTA" summary="CampoMascara" name="${listCamposMascara}" sort="list" pagesize="10" decorator="com.rsi.agp.core.decorators.ModelTableDecoratorCamposMascara" style="width:85%" excludedParams="accion" defaultsort="1">
	            <display:column class="literal" headerClass="cblistaImg" title="Acciones" property="acciones" style="width:50px;text-align:center"/>
	            <display:column class="literal" headerClass="cblistaImg" title="Desc. Máscara" property="descMascara" sortable="true" />
	            <display:column class="literal" headerClass="cblistaImg" title="Campo" property="campo"  sortable="true" />
	            <display:column class="literal" headerClass="cblistaImg" title="Campo E" property="campoE" sortable="true"/>            
	        </display:table>				
		</div>
	</div>
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
	</body>
</html>