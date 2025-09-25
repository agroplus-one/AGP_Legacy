<!--/*
**************************************************************************************************
*
*  CReACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034              Miguel Granadino  Pantalla relacion campos
*
 **************************************************************************************************
*/
-->

<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<jsp:directive.page import="org.displaytag.*" />
<fmt:setBundle basename="displaytag"/>
<c:set var="numReg"><fmt:message key="numElementsPag"/></c:set>


<%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", 0);
%>


<html>
<head>
    <title>Agroplus - Relacion campos</title>
    
    <%@ include file="/jsp/common/static/metas.jsp"%>
		
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css"/>
    <link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
    <link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />


<script type="text/javascript" src="jsp/moduloTaller/relacionCampos/relacionCampos.js" ></script>
<script type="text/javascript" src="jsp/js/menuapli.js"></script>
<script type="text/javascript" src="jsp/js/util.js" ></script>
<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
        
    <script>
    
         // Para evitar el cacheo de peticiones al servidor
         /*
         $(document).ready(function(){
             var URL = UTIL.antiCacheRand(document.getElementById("main").action);
			 document.getElementById("main").action = URL;    
         });
         */
    
   		 $(document).ready(function(){
   		 
   		 		$('#sl_planes').change(function(){
   		 			$("#sl_lineas").empty();						
					cargarSelecLinea();
   		 		});
   		 		$('#sl_lineas').change(function(){
   		 			$('#sl_usos').empty();
   		 			cargarSelectUso();   		 		
   		 		});
   		 		$('#sl_usos').change(function(){
   		 			$('#sl_ubicaciones').empty();
   		 			cargarSelectUbicaciones();   		 		
   		 		});
   		 		$('#sl_ubicaciones').change(function(){
   		 			$('#sl_camposSC').empty();
   		 			cargarSelectSC();   		 		
   		 		});
   		 		$('#sl_tipoCampo').change(function(){
   		 			if($('#sl_tipoCampo').val() == 'NC'){
							$('#txt_procesoCalculo').attr('disabled',true);
					}else{
							$('#txt_procesoCalculo').attr('disabled',false);
						}
   		 		});
   		 	/**	$('#sl_camposSC').change(function(){
   		 			$('#sl_factores').empty();
   		 			cargarSelectFactores();   		 		
   		 		}); */
   		 		$('#sl_factores').change(function(){
   		 			$('#sl_grupoFactores').empty();
   		 			cargarSelectGrupoFactores();   		 		
   		 		});
   		 		
   		 		<c:if test="${not empty RelacionCampoBean.linea.codplan}">
					cargarSelecLinea('${RelacionCampoBean.linea.lineaseguroid}','${RelacionCampoBean.uso.coduso}',
								'${RelacionCampoBean.ubicacion.codubicacion}','${RelacionCampoBean.diccionarioDatos.codconcepto}');
				</c:if>
				
				
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
					 	"linea.codplan": "required",
					 	"linea.lineaseguroid": "required",
					 	"uso.coduso": "required",
					 	"ubicacion.codubicacion": "required",
					 	"diccionarioDatos.codconcepto": "required",
					 	"tipocampo": "required"
					 	
					 },
					 messages: {
					 	"linea.codplan": "El campo Plan es obligatorio",
					 	"linea.lineaseguroid": "El campo Línea es obligatorio",
					 	"uso.coduso": "El campo Uso es obligatorio",
					 	"ubicacion.codubicacion": "El campo Ubicación es obligatorio",
					 	"diccionarioDatos.codconcepto": "El campo Campo SC es obligatorio",
					 	"tipocampo": "El campo Tipo Campo es obligatorio"
					 }
					 
				});	
				
				if($("#id").val() != ""){
					$('#operacion').val("modificar");
					$("#btnAlta").hide();
					$("#btnModif").show();
				} 		
			});
			
			
			
			
			
		function cargarSelecLinea(idLinea,idUso,idUbi,idSc){
    		var idPlan = $("#sl_planes").val();
				
				if(idPlan != ''){
			
					$.ajax({
				            url: "relacionCampos.html",
				            data: "operacion=ajax_getLineas&idPlan="+idPlan,
				            dataType: "json",
				            success: function(datos){
								$("#sl_lineas").removeOption(/./);
								$("#sl_lineas").addOption("","-- Seleccione una opción --");
								$.each(datos, function(index, value) { 
				 					$("#sl_lineas").addOption(value.value, value.nodeText);
								});
								
								$("#sl_lineas").selectOptions(idLinea == undefined ? "" : String(idLinea));
								
				            },
				            beforeSend: function(){
			            		$("#ajaxLoading_lineas").show();
            				},
            				complete: function(){
            					$("#ajaxLoading_lineas").hide();   
            					cargarSelectUso(idUso,idUbi,idSc);         					
            				},
				            type: "POST"
						});
				}
    	}
    	function cargarSelectUso(idUso,idUbi,idSc){
    		var plan = $("#sl_planes").val();
    		var linea= $('#sl_lineas').val();
				
				if((plan != '') && (linea != '')){
			
					$.ajax({
				            url: "relacionCampos.html",
				            data: "operacion=ajax_getUsos&idLinea="+linea,
				            dataType: "json",
				            success: function(datos){
								$("#sl_usos").removeOption(/./);
								$("#sl_usos").addOption("","-- Seleccione una opción --");
								$.each(datos, function(index, value) { 
				 					$("#sl_usos").addOption(value.value, value.nodeText);
								});
								
								$("#sl_usos").selectOptions(idUso == undefined ? "" : String(idUso));
								
				            },
				            beforeSend: function(){
			            		$("#ajaxLoading_usos").show();
            				},
            				complete: function(){
            					$("#ajaxLoading_usos").hide();   
            					cargarSelectUbicaciones(idUbi,idSc)         					
            				},
				            type: "POST"
						});
				}
    	}
    	function cargarSelectUbicaciones(idUbica,idSc){
    		var linea = $('#sl_lineas').val();
    		var uso = $('#sl_usos').val();    		
    		
    		if((linea!='') && (uso!='')){
    			
    			$.ajax({
		    				url: "relacionCampos.html",
		    				data: "operacion=ajax_getUbicaciones&idLinea="+linea+"&idUso="+uso,
		    				dataType: "json",
		    				
		    				success: function(datos){
		    					$('#sl_ubicaciones').removeOption(/./);
		    					$('#sl_ubicaciones').addOption("","-- Seleccione una opción --");
		    					$.each(datos,function(index,value){
		    						$('#sl_ubicaciones').addOption(value.value,value.nodeText);
		    					});
		    					$("#sl_ubicaciones").selectOptions(idUbica == undefined ? "" : String(idUbica));
		    				},
		    				
		    				beforeSend:function(){
		    					$('#ajaxLoading_ubicaciones').show();
		    				},
		    				
		    				complete:function(){
		    					$('#ajaxLoading_ubicaciones').hide();
		    					cargarSelectSC(idSc);
		    				},
		    				
		    				type:'POST'				
    			});
    		}
    	}
    	function cargarSelectSC(idSC){
    		var linea = $('#sl_lineas').val();
    		var uso = $('#sl_usos').val();   
    		var ubicacion = $('#sl_ubicaciones').val();
    		
    		if((linea!= '') && (uso != '') && (ubicacion!='')){
    			$.ajax({
	    				url: "relacionCampos.html",
	    				data: "operacion=ajax_getSC&idLinea="+linea+"&idUso="+uso+"&idUbi="+ubicacion,
	    				dataType: "json",
	    				success: function(datos){
	    					$('#sl_camposSC').removeOption(/./);
	    					$('#sl_camposSC').addOption("","-- Seleccione una opción --");
	    					$.each(datos,function(index,value){
	    						$('#sl_camposSC').addOption(value.value,value.nodeText);
	    					});
	    					$('#sl_camposSC').selectOptions(idSC == undefined ? "" : String(idSC));
	    				},
	    				beforeSend: function(){
	    					$('#ajaxLoading_sc').show();
	    				},
	    				complete: function(){
	    					$('#ajaxLoading_sc').hide();
	    				},	    				
	    				type:'POST'
    			});
    		}
    	}
    	/*function cargarSelectFactores(idFacto){
    		var sc = $('#sl_camposSC').val();    		
    		
    		if(sc != ''){
    			$.ajax({
	    				url: "relacionCampos.html",
	    				data: "operacion=ajax_getGrupoFactores&idCampo="+sc,
	    				dataType: "json",
	    				success: function(datos){
	    					$('#sl_factores').removeOption(/./);
	    					$('#sl_factores').addOption("","-- Seleccione una opción --");
	    					$.each(datos,function(index,value){
	    						$('#sl_factores').addOption(value.value,value.nodeText);
	    					});
	    					$('#sl_factores').selectOptions(idFacto == undefined ? "" : String(idFacto));
	    				},
	    				beforeSend: function(){
	    					$('#ajaxLoading_grupoFactores').show();
	    				},
	    				complete: function(){
	    					$('#ajaxLoading_grupoFactores').hide();
	    				},	    				
	    				type:'POST'
    			});
    		}
    	}*/
    	function cargarSelectGrupoFactores(){
    		var gf = $('#sl_factores').val();
    		
    		if(gf != ''){
    			$.ajax({
    					url:"relacionCampos.html",
    					data:"operacion=ajax_getFactoresGrupo&idFactor="+gf,
    					dataType: "json",
    					success: function(datos){
    						$('#sl_grupoFactores').removeOption(/./);    						
    						$.each(datos,function(index,value){
    							$('#sl_grupoFactores').addOption(value.value,value.nodeText);    							
    						});
    						/*Aparecen todos los elementos seleccionados, asiq, los deseleccionamos*/
    						elementos = $('#sl_grupoFactores option:selected').attr("selected",true);
    						elementos.each(function(){
    							$(this).attr("selected",false);
    						});    						
    					
    					},
    					beforeSend:function(){
    						$('#ajaxLoading_factor').show();
    					},
    					complete:function(){
    						$('#ajaxLoading_factor').hide();
    					},
    					type:'POST'
    			});
    		}
    	}
    	
    	function limpiar(){
			
				$("#sl_planes").selectOptions("");
				$("#sl_lineas").empty();
				$("#sl_usos").empty();		
				$("#sl_ubicaciones").empty();
				$("#sl_camposSC").empty();	
				$("#sl_tipoCampo").selectOptions("");	
				$("#txt_procesoCalculo").val("");
				$("#sl_factores").selectOptions("");	
				$("#sl_grupoFactores").empty();
				$('#operacion').val("");
				$("#main").validate().cancelSubmit = true;
				$('#main').submit();			
		}
		function consultar(){

				$('#operacion').val("consulta");
				$("#main").validate().cancelSubmit = true;
				$('#main').submit();		
		}
		function eliminar(idPantallaConfigurable){

				if(confirm('¿Está seguro de que desea eliminar el registro seleccionado?')){
					
					$("#main").validate().cancelSubmit = true;
					$('#id').val(idPantallaConfigurable);
					$('#operacion').val("baja");
					//limpiamos
					$("#sl_planes").selectOptions("");
					$("#sl_lineas").empty();
					$("#sl_usos").empty();		
					$("#sl_ubicaciones").empty();
					$("#sl_camposSC").empty();	
					$("#sl_tipoCampo").selectOptions("");	
					$("#txt_procesoCalculo").val("");
					$("#sl_factores").selectOptions("");	
					$("#sl_grupoFactores").empty();
					
					$('#main').submit();
				}			
		}
		function alta(){
		
		  $("#panelInformacion").hide();
		  $('#id').val('');
		  $('#operacion').val("alta");
		  $('#main').submit();
		}
		function editar(idRelacionCampo){
		
				$('#operacion').val("editar");
				$('#id').val(idRelacionCampo);
				$('#btnAlta').hide();
				$('#btnModif').show();				
				
				$.ajax({
					 	url: "relacionCampos.html",
			            data: "operacion=editar&idRelacionCampo="+idRelacionCampo,
			            dataType: "json",
			            success: function(datos){
			            	$('#id').val(datos.idrelacioncampo);
			            	$("#sl_planes").selectOptions(""+datos.plan);			            	
			            	$("#sl_tipoCampo").selectOptions(""+datos.tipo);
			            	$('#txt_procesoCalculo').val(datos.calculo);
			            	if($('#sl_tipoCampo').val() == 'NC'){
									$('#txt_procesoCalculo').attr('disabled',true);
							}else{
									$('#txt_procesoCalculo').attr('disabled',false);
								}
			            	$('#sl_factores').selectOptions(""+datos.factor);			            				            	
			            	cargarSelecLinea(datos.linea,datos.uso,datos.ubicacion,datos.sc);			            		            	
			            	/*falta grupo factores*/
			            	cargarSelectGrupoFactores();
			            },
			            type: "POST"
				});
		}
		function modificar(){

		        $("#panelInformacion").hide();		        
				$('#operacion').val("modificacion");
				$('#main').submit();			    

		}
    	/*function alta2() 
    	{
    		var frm = document.getElementById('main');
    		var valortipocampo = frm.sl_tipoCampo.value;
    		
    			//$('#id').val('');
				$('#operacion').val("alta");
				$('#main').submit();			
    		if(true) return;
    		
    		if (valortipocampo == 'C')
    		{
				//generales.enviar('alta', 'sl_planes', 'sl_lineas', 'sl_usos', 'sl_ubicaciones', 'sl_camposSC', 'sl_tipoCampo', 'txt_procesoCalculo');
				
				generales.enviar('alta');
			}
			else
			{
				//generales.enviar('alta', 'sl_planes', 'sl_lineas', 'sl_usos', 'sl_ubicaciones', 'sl_camposSC', 'sl_tipoCampo');
				generales.enviar('alta');
			}
		}*/
		
		
    </script>
  
	
	<style type="text/css"> 
 .scrollable{ 
   overflow: auto; width: 250px; height: 40px; border: 1px silver solid; } 
 .scrollable select{ border: none; } 
</style> 
	
		
</head>
	
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="generales.fijarFila()">	
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuLateralTaller.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	<!-- Buttons -->
	<div id="buttons">
	<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">
					<table cellspacing="2" cellpadding="0" border="0">
						<tbody>
							<tr>
								<td><a class="bot" id="btnAlta" href="javascript:alta();">Alta</a></td>
								<td><a class="bot" id="btnModif" style="display:none" href="javascript:modificar();">Modificar</a></td>
							    <td><a class="bot" id="btnConsulta" href="javascript:consultar();">Consultar</a></td>
								<td><a class="bot" href="javascript:limpiar();">Limpiar</a></td>
							</tr>
						</tbody>
					</table>
					<!-- FIN TABLA BARRA DE BOTONES-->
				</td>
			</tr>
	</table>
	</div>
		
	<!-- Contenido de la página -->
	<div class="conten" style="padding:3px;width:97%">
	        <p class="titulopag" align="left">Relacion campos</p>
		    <form:form id="main" name="main" method="POST" action="relacionCampos.html" commandName="RelacionCampoBean">
		    	<form:hidden id="id" path="idrelacion"/>
				<input type="hidden" name="operacion" id="operacion">
				<input type="hidden" name="ROW" id="ROW" value="">
                <input type="hidden" name="PAGINA" id="PAGINA" value="relacionCampos.jsp">
                <input type="hidden" name="idRowRelacionCampos" value="${relacionCampos.RowRelacionCampo.idrelacion}">
                <input type="hidden" name="modificar" id="modificar" value="${relacionCampos.Modif }">
                                

		        <%@ include file="/jsp/common/static/avisoErrores.jsp"%>
		        <!-- PANEL 1 -->
		        <div class="panel1 isrt" style="width:auto;">
		        	<table width="50%" align="center" cellspacing="4" style="margin:0 auto;">
		        		<tr>
		        			<td class="literal" width="80px">Plan</td>
		        			<td>
		        				<form:select id="sl_planes" path="linea.codplan" cssClass="dato" >
		        					<form:option value="">-- Seleccione una opción --</form:option>
		        					<c:forEach items="${ listPlanes}" var="plan">
		        						<form:option value="${plan }">${plan }</form:option>
		        					</c:forEach>
		        				</form:select>
		        				<label class="campoObligatorio" id="campoObligatorio_sl_planes" title="Campo obligatorio"> *</label>
		        			</td>		        			
		        		</tr>
		        		<tr>
		        			<td class="literal" width="80px">L&iacute;nea</td>
		        			<td>
		        				<form:select id="sl_lineas" path="linea.lineaseguroid" cssClass="dato">
		        					<form:option value=""></form:option>
		        				</form:select>
		        				<label class="campoObligatorio" id="campoObligatorio_sl_lineas" title="Campo obligatorio"> *</label>
		        				<img id="ajaxLoading_lineas" src="jsp/img/ajax-loading.gif" width="16px" style="cursor:hand;cursor:pointer;display:none" height="11px" />	
		        			</td>		        			
		        		</tr>		        		
		        		<tr>
		        			<td class="literal" width="80px">Uso</td>
		        			<td>
		        				<form:select id="sl_usos" path="uso.coduso" cssClass="dato">
									<form:option value=""></form:option>									
		        				</form:select>
		        				<label class="campoObligatorio" id="campoObligatorio_sl_usos" title="Campo obligatorio"> *</label>
		        				<img id="ajaxLoading_usos" src="jsp/img/ajax-loading.gif" width="16px" style="cursor:hand;cursor:pointer;display:none" height="11px" />			        				
		        			</td>		        			
		        		</tr>
		        	</table>
		        </div>
		        <div class="panel2 isrt" style="margin:0 auto;">
		        	<div class="panel2_left" style="width:50%">
			        	<table width="100%" align="center" cellspacing="4">
							<tr>
								<td class="literal" width="80px">Ubicaci&oacute;n</td>
								<td>
									<form:select id="sl_ubicaciones" path="ubicacion.codubicacion" cssClass="dato">
										<form:option value=""></form:option>										
									</form:select>
									<label class="campoObligatorio" id="campoObligatorio_sl_ubicaciones" title="Campo obligatorio"> *</label>
									<img id="ajaxLoading_ubicaciones" src="jsp/img/ajax-loading.gif" width="16px" style="cursor:hand;cursor:pointer;display:none" height="11px" />	
								</td>
							</tr>
							<tr>
								<td class="literal" width="80px">Campos SC</td>
								<td>
									<form:select id="sl_camposSC" path="diccionarioDatos.codconcepto" cssClass="dato">
										<form:option value=""></form:option>										
									</form:select>
									<label class="campoObligatorio" id="campoObligatorio_sl_camposSC" title="Campo obligatorio"> *</label>
									<img id="ajaxLoading_sc" src="jsp/img/ajax-loading.gif" width="16px" style="cursor:hand;cursor:pointer;display:none" height="11px" />	
								</td>
							</tr>
							<tr>
								<td class="literal" width="80px">Tipo Campo</td>
								<td>
									<form:select id="sl_tipoCampo" path="tipocampo" cssClass="dato">
										<form:option value="">-- Seleccione una opción --</form:option>	
										<form:option value="C">CALCULADO</form:option>
										<form:option value="NC">NO CALCULADO</form:option>									
									</form:select>
									<label class="campoObligatorio" id="campoObligatorio_sl_tipoCampo" title="Campo obligatorio"> *</label>
								</td>
							</tr>
							<tr>
								<td class="literal" width="100px">Proceso C&aacute;lculo</td>
								<td>
									<form:input id="txt_procesoCalculo" path="procesocalculo" cssClass="dato"/>
								</td>
							</tr>		        	
			        	</table>
		        	</div>
		        	<div class="panel2_right">
		        		<fieldset style="width:350px;">
						    <legend>Grupo Factores</legend>
			        		<table width="80%" align="center" cellspacing="4">
			        			<tr>
			        				<td class="literal">Factores</td>
			        				<td>
			        					<form:select id="sl_factores" path="grupoFactores.idgrupofactores" cssStyle="width:60px" cssClass="dato" size="1">
			        						<form:option value=""></form:option>
			        						<c:forEach items="${listFactores }" var="factor">
			        							<form:option value="${factor.idgrupofactores }">${factor.descgrupofactores }</form:option>
			        						</c:forEach>			        						
			        					</form:select>
			        					
			        				</td>
			        			</tr>			        			
			        			<tr>
			        				<td></td>
			        				<td>
			        					<form:select id="sl_grupoFactores" path="grupoFactores.diccionarioDatoses_1" cssClass="dato" multiple="true">
			        						
			        					</form:select>
			        					 <img id="ajaxLoading_factor" src="jsp/img/ajax-loading.gif" width="16px" style="cursor:hand;cursor:pointer;display:none" height="11px"/> 
			        				</td>
			        			</tr>
			        		</table>
			        	</fieldset>
					</div>
		        </div>				   
			 </form:form>
			      <display:table requestURI="" class="LISTA" id="listaRelCampos" summary="relacionCampos" sort="list" pagesize="${numReg}" name="${listRelacionCampos}" decorator="com.rsi.agp.core.decorators.ModelTableDecoratorRelacionCampos">
                    <display:column class="literal" headerClass="cblistaImg" title="Acciones" property="admActions" sortable="false" style="width:45px;text-align:center"/>
                    <display:column class="literal" headerClass="cblistaImg" title="Uso" property="uso.desuso"  sortable="true" style="width:60px;"/>
                    <display:column class="literal" headerClass="cblistaImg" title="Ubicacion" property="ubicacion.desubicacion" sortable="true"/>
                    <display:column class="literal" headerClass="cblistaImg" title="Campo S.C." property="admCampoSC"  sortable="true" />
                    <display:column class="literal" headerClass="cblistaImg" title="Grupo Factores" property="admFactor"  sortable="false" style="width:100px;text-align:right"/>
                    <display:column class="literal" headerClass="cblistaImg" title="Tipo" property="admTipocampo" sortable="false" style="width:45px;text-align:center;"/>
                    <display:column class="literal" headerClass="cblistaImg" title="Proceso de cálculo" property="admProcesocalculo" sortable="false" />	
                </display:table>
		    </div>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	</body>
</html>
