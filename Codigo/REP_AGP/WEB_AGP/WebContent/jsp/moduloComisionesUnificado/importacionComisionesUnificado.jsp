<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>


<html>
<head>
<title>IMPORTACIÓN FICHEROS DE COMISIONES 2015+</title>
<%@ include file="/jsp/common/static/metas.jsp"%>
<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />

<script type="text/javascript" src="jsp/js/menuapli.js"></script>
<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
<script type="text/javascript" src="jsp/js/util.js"></script>
<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
<script type="text/javascript" src="jsp/js/calendar.js"></script>
<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>

<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
<script type="text/javascript" src="jsp/js/jquery.progressbar.js"></script>
<script type="text/javascript" src="jsp/js/jquery.progressbar.min.js"></script>

<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
<script type="text/javascript" src="jsp/js/jquery.filestyle.js"></script>
<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
<!--  <script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script> -->
<script type="text/javascript" src="jsp/js/ajaxfileupload.js"></script>
<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js"></script>
<script type="text/javascript" src="jsp/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="jsp/moduloComisionesUnificado/importacionComisionesUnificado.js"></script>
<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
	    <%@ include file="/jsp/js/draggable.jsp"%>
	   
	 <!--  <script type="text/javascript">
		  	function cargarFiltro(){
		  		<c:forEach items="${sessionScope.consultaFicheroUnificado.filterSet.filters}" var="filtro">
					//alert(${filtro.property});
					var inputText = document.getElementById('${filtro.property}');
					//alert(inputText);
					if (null!=inputText){
						inputText.value = '${filtro.value}';
					}
				</c:forEach>
			}
	   </script> -->
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0 "
	marginheight="0" onload="SwitchSubMenu('sub7', 'sub5');">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr align="left">
				<td align="right">
					<a class="bot" id="btnCargaAutomatica" href="javascript:cargaAutomatica();" style="display:none;">Carga automática</a> 
					<a class="bot" id="btnCargar" href="javascript:ajaxFileUpload();">Cargar fichero</a> 
					<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
					<a class="bot" id="btnLimpiar" href="javascript:limpiar();">Limpiar</a>
				</td>
			</tr>
		</table>
	</div>
	<!-- Contenido de la página -->
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left" id="titulo"></p>

		<form:form name="main8" id="main8"
			action="importacionComisionesUnificadas.html" method="post"
			enctype="multipart/form-data" commandName="fichero">
			<input type="hidden" id="method" name="method" />
			<input type="hidden" id="idFichero" name="idFichero" />
			<input type="hidden" id="tipo" name="tipo" value="${tipo}" />
			<input type="hidden" id="estadoF" name="estadoF" />
			<input type="hidden" id="nombreF" name="nombreF" />
			<input type="hidden" id="limpiar" name="limpiar" />
			<input type="hidden" id="origenLlamada" name="origenLlamada" />
			<input type="hidden" id="fechaCarga.day" name="tx_fecha_carga.day" value=""> 
	        <input type="hidden" id="fechaCarga.month" name="tx_fecha_carga.month" value=""> 
	        <input type="hidden" id="fechaCarga.year" name="tx_fecha_carga.year" value="">
	        <input type="hidden" id="fechaAceptacion.day" name="tx_fecha_aceptacion.day" value=""> 
	        <input type="hidden" id="fechaAceptacion.month" name="tx_fecha_aceptacion.month" value=""> 
	        <input type="hidden" id="fechaAceptacion.year" name="tx_fecha_aceptacion.year" value="">
	        <input type="hidden" id="fechaCierre.day" name="tx_fecha_cierre.day" value=""> 
	        <input type="hidden" id="fechaCierre.month" name="tx_fecha_cierre.month" value=""> 
	        <input type="hidden" id="fechaCierre.year" name="tx_fecha_cierre.year" value="">

			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>

			<div style="">
				<fieldset style="width: 95%; margin:0 auto;">
					<legend class="literal">Importación</legend>
					<table align="center">
						<tr>
							<td class="literal">Fichero a importar&nbsp;&nbsp;</td>
							<td width="350px"><input type="file" class="dato" id="file"
								name="file" style="width: 400px" /> <label
								class="campoObligatorio" id="campoObligatorio_file"
								title="Campo obligatorio"> *</label></td>
						</tr>
					</table>
				</fieldset>
				<fieldset style="width: 95%; margin:0 auto;">
					<legend class="literal">Consulta</legend>
					<table align="center">
						<tr>
							<td class="literal">Fichero</td>
							<td colspan="3"><form:input cssClass="dato" id="nombreFichero"
									size="40" path="nombreFichero" /></td>
							<td class="literal">Estado</td>
							<td>
								<!--  <select class="dato" id="estado" name="estado">
									<option value="">Todos</option>
									<option value="Correcto">Correcto</option>
									<option value="Aviso">Aviso</option>
									<option value="Erroneo">Erroneo</option>
									<option value="Cargado">Cargado</option>
								</select>-->
								<form:select path="estado" cssClass="dato" id="estado" cssStyle="width:100px">
									<form:option value="">Todos</form:option>
									<form:option value="C">Correcto</form:option>
									<form:option value="A">Aviso</form:option>
									<form:option value="E">Erroneo</form:option>
									<form:option value="X">Cargado</form:option>
								</form:select>
							</td>
						</tr>
						<tr>
							<td class="literal">Fec. Carga</td>
							<td><spring:bind path="fechaCarga">
									<input type="text" name="fechaCarga" id="fechaCarga"
										size="11" maxlength="10" class="dato"
										onchange="if (!ComprobarFecha(this, document.main8, 'Fec. Carga')) this.value='';"
										value="<fmt:formatDate pattern="dd/MM/yyyy" value="${fichero.fechaCarga}"/>" />
								</spring:bind> <input type="button" id="btn_fecha_carga"
								name="btn_fecha_carga" class="miniCalendario"
								style="cursor: pointer;" /></td>


							<td class="literal">Fec. Aceptación</td>
							<td><spring:bind path="fechaAceptacion">
									<input type="text" name="fechaAceptacion"
										id="fechaAceptacion" size="11" maxlength="10" class="dato"
										onchange="if (!ComprobarFecha(this, document.main8, 'Fec. Aceptación')) this.value='';"
										value="<fmt:formatDate pattern="dd/MM/yyyy" value="${fichero.fechaAceptacion}"/>" />
								</spring:bind> <input type="button" id="btn_fecha_aceptacion"
								name="btn_fecha_aceptacion" class="miniCalendario"
								style="cursor: pointer;" /></td>
							<td class="literal">Fec. Cierre</td>
									<td>
										<spring:bind path="fechaCierre">
											<input type="text" name="fechaCierre"  id="fechaCierre" size="11" maxlength="10" class="dato"
												onchange="if (!ComprobarFecha(this, document.main8, 'Fec. Cierre')) this.value='';"
												value="<fmt:formatDate pattern="dd/MM/yyyy" value="${fichero.fechaCierre}"/>"/>
										</spring:bind>
										<input type="button" id="btn_fecha_cierre" name="btn_fecha_cierre" class="miniCalendario" style="cursor: pointer;"/>
									</td>
						</tr>
					</table>
				</fieldset>

			</div>
		</form:form>
		<!-- Grid Jmesa -->
	<div id="grid" style="width: 95%; margin:0 auto;">
  		${consultaFicheroUnificadoTabla}		  							               
	</div> 	
</div>




<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		
	    <!-- Popup de la barra de progreso -->
	    <div id="progressbar_popup" class="wrapper_popup">
		    <div class="header-popup">
		        <div class="title_popup">Aviso</div>
		        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
		                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer;display:none" id="divbot">
		            <span onclick="cerrarPopUp()">x</span>
		        </a>
		         <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
		                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer;display:none" id="boterr">
		            <span onclick="cerrarPopUpError()">x</span>
		        </a>
		    </div>
		    <div id="panelInformacionImportacion" class="literal" style="color:green;display:none;text-align: center;">
					Importación completada con exito
			</div>
			<div id="panelAlertasImportacion" class="literal" style="color:red;display:none;text-align: center;">
					Se ha producido un error durante la importación
			</div>
			<div class="content_popup">
			    <table>
			        <tr>
			            <td class="literal" id="mensaje">Espere un momento, por favor...</td>
			            <td>
			            	<table>
			            		<tr>
			            			<td>
									<div id="upload_bar"></div>
									</td>
									<td>
										<img src="jsp/img/displaytag/accept.png" style="display: none" id="uploadOK" />
										<img src="jsp/img/displaytag/cancel.png" style="display: none" id="uploadKO" />
									</td>
			            		</tr>
			            	</table>
			            </td>
			        </tr>
			    </table>
			</div>
		</div>
		
		
		<!--  <form name="revisarForm" id="revisarForm" action="incidenciasUnificado.html">
			<input type="hidden" id="revisar_idFichero" name="idFichero" />			
			<input type="hidden" id="revisar_method" name="method"/>			
		</form>-->
		
		<form name="verFich" id="verFich" action="importacionComisionesUnificadas.html" target="_blank" >
			<input type="hidden" id="verFich_idFichero" name="idFichero" />
			<input type="hidden" id="verFich_method" name="method"/>
	    </form>
	    

	<div id="divContenidoFichero" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
			<div id="header-popup" style="padding:0.4em 1em;position:relative;
			   				              color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;
							              background:#525583;height:15px">
				<div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">	
							Contenido del Fichero
				</div>
				<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px; font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
							<span onclick="cerrarPopUpContenido()">x</span>
				</a>
			</div>
			<div class="panelInformacion_content">
				<div id="panelInformacion" class="panelInformacion">
					<div id="contenidoFichero" />			
				</div>
			</div>
	</div>	

</body>
</html>