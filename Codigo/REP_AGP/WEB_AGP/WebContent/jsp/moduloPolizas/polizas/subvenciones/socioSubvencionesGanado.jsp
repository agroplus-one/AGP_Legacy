<%@ page import="com.rsi.agp.dao.tables.cgen.TipoSubvencionEnesa, com.rsi.agp.dao.tables.cgen.TipoSubvencionEnesaView, 
com.rsi.agp.dao.tables.poliza.SubvencionSocio, com.rsi.agp.dao.tables.admin.Socio, com.rsi.agp.core.managers.impl.SocioSubvencionManager, 
java.util.*, com.rsi.agp.core.webapp.util.StringUtils"%>	
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
		<title>Subvenciones de Socios</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />

		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>

		<script type="text/javascript">
			 $(document).ready(function(){
			      	 var URL = UTIL.antiCacheRand(document.getElementById("main").action);
		       		 document.getElementById("main").action = URL;		
		       		         
			        $("input[type=checkbox]").each(function(){ 				 					        
						        if($('#modoLectura').val() == 'modoLectura'){
						        	$(this).attr('disabled','disabled');
						        	$('#btnAlta').hide();
						        	$('#btnModificar').hide();
						        }
					 });  	
		        
	    	  });
		
			function modificar (idAsegurado, nifcif, nombre, esmodif)
			{
		        $('#tablaSubv').show();
				var frm = document.getElementById('main');
				frm.idAseg.value = idAsegurado;
				frm.cifsocio.value = nifcif;
				frm.nomobresocio.value = nombre;
				frm.operacion.value = 'cargaSocio';
				$.blockUI.defaults.message = '<h4> Cargando las subvenciones del socio.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } }); 
				frm.submit();
			}
			
			function botones()
			{
				var frm = document.getElementById('main');				
				if (frm.cifsocio.value != '')
				{
					if (document.getElementById('numSubsSocio').value >0)
					{
						var modif = document.getElementById('btnModificar');
						if ($('#modoLectura').val() != 'modoLectura'){
							modif.style.display = "";
						}
					}
					else
					{
						var alta = document.getElementById('btnAlta');
						if ($('#modoLectura').val() != 'modoLectura'){
							alta.style.display = "";
						}
						
					}												
				}
			}
			
			function limpia()
			{
				var frm = document.getElementById('main');
				frm.idAseg.value = '';
				frm.cifsocio.value = '';
				frm.nomobresocio.value = '';
				document.getElementById('tablaSubv').style.display ="none";
				var alta = document.getElementById('btnAlta');
				alta.style.display = "none";
				var modif = document.getElementById('btnModificar');
				modif.style.display = "none";
				if (frm.panelAlertas != null)
					frm.panelAlertas.style.display ="none";
			}
			
			function modificarRegs()
			{
				var sSelec = document.getElementsByTagName("input");	
				var frm = document.getElementById('main');				
				
				for (var i = 0; i < sSelec.length; i++) 
				{
					if(sSelec.item(i).checked)
					{
						frm.subsSeleccionadas.value += sSelec.item(i).value + ",";
					}					
				}
				frm.operacion.value = 'modificar';				
				frm.submit();
			}
			
			function alta()
			{				
				var sSelec = document.getElementsByTagName("input");	
				var frm = document.getElementById('main');				
				
				for (var i = 0; i < sSelec.length; i++) 
				{
					if(sSelec.item(i).checked)
					{
						frm.subsSeleccionadas.value += sSelec.item(i).value + ",";
					}					
				}
				frm.operacion.value = 'alta';				
				frm.submit();
			}
			function continuar(){
	         	$(window.location).attr('href', 'aseguradoSubvencion.html?rand=' + UTIL.getRand() + '&idpoliza='+$('#idpoliza').val()+'&modoLectura='+$('#modoLectura').val()+'&vieneDeUtilidades='+$('#vieneDeUtilidades').val()+'&operacion=');
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
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="eligeMenu();botones();">

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
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="right">
						<a class="bot" id="btnLimpiar" href="javascript:limpia()">Limpiar</a>
						<a class="bot" id="btnAlta" style="display:none" href="javascript:alta()">Alta</a>
						<a class="bot" id="btnModificar" style="display:none" href="javascript:modificarRegs()">Modificar</a>
						<a class="bot" id="btnContinuar" href="javascript:continuar()">Volver</a>
					</td>
				</tr>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%">
			<p class="titulopag" align="left">Subvenciones de los Socios</p>
			<form:form name="main" id="main" action="socioSubvencion.html" method="post" commandName="socioBean">
				<input type="hidden" name="operacion" id="operacion" />
				<input type="hidden" name="subvsJS" value="${subsJS }"/>
				<input type="hidden" name="subsSeleccionadas" />
				<input type="hidden" name="idpoliza" id="idpoliza"  value="${poliza.idpoliza}"/>
				<input type="hidden" name="modoLectura" id="modoLectura" value="${modoLectura}"/>
				<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${vieneDeUtilidades}"/>
				<form:hidden path="id.idasegurado" id="idAseg" />
				<form:hidden path="tipoidentificacion" />
			
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div class="panel2 isrt" style="padding: 3px; width: 97%;">
					<table width="97%">
						<colgroup>
							<col width="20%" />
							<col width="20%" />
							<col width="20%" />
							<col width="*" />
						</colgroup>
						<tr align="left">
							<td class="literal" align="right">Cif/Nif socio</td>
							<td class="literal">
								<form:input path="id.nif" size="15" id="cifsocio" maxlength="39" cssClass="dato" readonly="true"/>
							</td>
							<td class="literal" align="right">Nombre</td>
							<td class="literal">
								<%
								String nombre = "";
								Socio socio = (Socio)request.getAttribute("socio_subv");
								if (socio != null){
								if (socio.getTipoidentificacion() != null && socio.getTipoidentificacion().equals("NIF") && socio.getNombre() != null)
									nombre = socio.getNombre() + " " + StringUtils.nullToString(socio.getApellido1()) + " " + StringUtils.nullToString(socio.getApellido2());
								else
									nombre = StringUtils.nullToString(socio.getRazonsocial());
								}
								%>
								<input type="text" size="100" id="nomobresocio" maxlength="100" class="dato" value="<%=nombre%>" readonly="true"/>
							</td>
						</tr>
					</table>
				</div>
			</form:form>
			<input type="hidden" name="numSubsSocio" id="numSubsSocio" value="${numSubvencionesSocio}" />
			<div class="panel2 isrt" style="width: 80%;" id="tablaSubv">
					
							${tabla}
					
			</div>					
			<br/> 
			<div id="grid">
				<!-- Aqui tiene que ir el grid de datos -->
					<display:table requestURI="" id="listaSocios" class="LISTA" summary="socio" sort="list" defaultsort="2" defaultorder="ascending"
					pagesize="${numReg}" name="${socioBean.listaSocios}" export="false" decorator="com.rsi.agp.core.decorators.ganado.ModelTableDecoratorSociosGanado" style="width:95%">
						<display:column class="literal" headerClass="cblistaImg" title="Acciones" property="socioSoloSelec" sortable="false" style="width:50px;text-align:center"/>
						<display:column class="literal" headerClass="cblistaImg" title="Nombre" property="socioNombre" sortable="true" />
						<display:column class="literal" headerClass="cblistaImg" title="CIF/NIF" property="socioCif" sortable="true" />		
						<display:column class="literal" headerClass="cblistaImg" title="SS" property="socioSS" sortable="true" />         		   
						<display:column class="literal" headerClass="cblistaImg" title="Régimen" property="socioRegimen" sortable="true"/>		
						<display:column class="literal" headerClass="cblistaImg" title="ATP" property="socioATP" sortable="true" />				            
						<display:column class="literal" headerClass="cblistaImg" title="J.Agricultor/a" property="socioJAgr" sortable="true"/>
						<display:column class="literal" headerClass="cblistaImg" title="Sub. Declaradas" property="subvDeclaradas" sortable="true"/>						
					</display:table>
			</div>
		</div>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
	</body>
</html>