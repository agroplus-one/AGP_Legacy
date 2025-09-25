<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
	
		<%@ include file="/jsp/common/static/metas.jsp"%>
		<title>Mantenimiento de Condiciones del Informe</title>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />

		
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/moduloInformes/condicion.js"></script>
		<script type="text/javascript" src="jsp/moduloInformes/mtoInformesAjax.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
	

		
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="javascript:SwitchSubMenu('sub12','sub11');onLoadCampoValue()">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		
		<!-- botones de la página -->
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td width="5">&nbsp;</td>
					<td align="left">
						<a class="bot" id="datoinforme" href="javascript:datosInformes();">Datos informe</a>						
						<a class="bot" id="clasificacionRuptura" href="javascript:clasificacionRuptura();">Clasificación y ruptura</a>
						<a class="bot" id="btnGenerar" href="javascript:ajaxCheckInforme();">Generar</a>
					</td>
					<td align="right">
						<a class="bot" id="btnModificar" href="javascript:modificar()">Modificar</a>
						<a class="bot" id="btnAlta" href="javascript:alta()">Alta</a> 
						<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a> 
						<a class="bot" id="btnLimpiar"href="javascript:limpiar()">Limpiar</a>
						<a class="bot" id="btnVolver"href="javascript:volver()">Volver</a>
					</td>
				</tr>
			</table>
		</div>
		
		<!-- Contenido de la pagina -->
		<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Mantenimiento de Condiciones del Informe</p>

		<form:form name="main3" id="main3" action="mtoCondicionCampos.run" method="post" commandName="vistaMtoinfCondiciones">								
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<input type="hidden" name="idInforme" id="idInforme" value="${idInforme}"/>
				<input type="hidden" name="nombre" id="nombre" value="${nombre}"/>
				<input type="hidden" name="redireccion" id="redireccion"/>
				<input type="hidden" name="mensaje" id="mensaje" value="${requestScope.mensaje}"/>
				<input type="hidden" name="alerta" id="alerta" value="${requestScope.alerta}"/>
				<input type="hidden" name="modificarValidCalculado" id="modificarValidCalculado" value="${modificarValidCalculado}"/>
				<input type="hidden" name="origenLlamada" id="origenLlamada" />
				<input type="hidden" name="method" id="method" />
				<input type="hidden" name="condicionesOk" id="condicionesOk" />
				<input type="hidden" name="tipoNumerico" id="tipoNumerico" value="${tipoNumerico}"/>
				<input type="hidden" name="tipoFecha" id="tipoFecha" value="${tipoFecha}"/>
				<input type="hidden" name="condiciontxt.day" value="">
				<input type="hidden" name="condiciontxt.month" value="">
				<input type="hidden" name="condiciontxt.year" value="">
				<input type="hidden" name="odValorLibre" id="odValorLibre" value="${odValorLibre}"/>
				<input type="hidden" name="recogerInformeSesion" id="recogerInformeSesion" value="${recogerInformeSesion}"/>
			
			<div class="panel2 isrt">
				<fieldset>
				<legend class="literal">Condiciones del Informe  : ${nombre} </legend>
				
				<table align="left" width="100%">	
					<tr>
						<td></td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td class="literal" style="width:60px" valign="bottom">Columna&nbsp;</td>
						<td colspan="3" valign="bottom">
							<select class="dato" id ="listaCampo"  style="width:400px" tabindex="1" onchange="javascript:checkOrigenDatos(this.value);">
							<option value="">Mostrar todos</option>
							 <c:forEach items="${listaCampoInforme}" var="cp">
								<option value="${cp.permitidoOCalculado}-${cp.datoInformeId}-${cp.tipo}-${cp.origen_datos}">${cp.nombreVista} - ${cp.nombre}</option>
							</c:forEach>
							</select>
						</td>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td></td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td class="literal" valign="top" >Operador&nbsp;</td>
						<td valign="top" nowrap="nowrap">
							<select name="listaOperador" id="listaOperador" class="dato" tabindex="2" style="width:190px" onchange="javascript:actualizaIdoperador(this.value)" >
								<c:if test="${listaOperadores != null}">
								 	<c:forEach items="${listaOperadores}" var="listaOper">
										<option value="${listaOper.value}-${listaOper.idOperador}">${listaOper.property}</option>	
						    		</c:forEach>
					      		</c:if>
					    	</select>
					    	<img id="ajaxLoading" src="jsp/img/ajax-loading.gif" width="16px" style="display:none" height="11px" />
					    </td>
					    <td valign="top" class="literal">Valor&nbsp;</td>
						<td valign="top">
							<input type="text" style="display: block;" class="dato" id="condiciontxt"  tabindex="3" size="10" maxlength="20" onfocus="javascript:limpiaAlertas();" onblur="limpiaAlertas();validarFecha(this,'Valor');"/>
							<select style="display: none;" style="width:190px" class="dato" id="condiciontxtcombo"  tabindex="3"/>
						</td>
						<td  valign="top" style="width:60px">
							<input type="button" id="btn_fecha" name="btn_fecha" class="miniCalendario" style="cursor: pointer;display:none " />
						</td>
						<td  valign="top" style="width:60px">
							<img  border="1" id="iconoAnadir" src="jsp/img/flecha1.gif" style="cursor: hand" onclick="javascript:anadirCondicion();" alt="Añadir Condici&oacute;n" />	
						</td>
						<td valign="top" nowrap="nowrap">
							<select multiple size="3" id="multCondicion" name="multCondicion[]" class="dato" style="width:110px;" tabindex="4" >
							</select>
							<img  border="1" id="iconoBorrar" src="jsp/img/displaytag/delete.png" style="cursor: hand" onclick="javascript:borrarCondicion();" alt="Borrar Condici&oacute;n"/>
						</td>
						<td>&nbsp;</td>
					</tr>
				</table>			
			</div>		
			
			<form:hidden path="id.condid" id="id" />	
			<form:hidden path="datoinformeid" id="datoInformesId" />
			<form:hidden path="idcampo" id="campoId" />
			<form:hidden path="condicion" id="condicion" />
			<form:hidden path="tipo" id="tipo" />
			<form:hidden path="idinforme" id="informeid" />
			<form:hidden path="id.permitidocalculado" id="permitidocalculado" />
			<form:hidden path="idtablaoperadores" id="idOperadorCondicion" />
			<form:hidden path="idoperador" id="idOperador" />
			
		</form:form> 
		
		<!-- Grid Jmesa -->
		<div id="grid">
	  		${mtoCondicionesCampos}		  							               
		</div> 	
		</div>
		<br />
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<%@ include file="/jsp/moduloInformes/popupFormatoInforme.jsp"%>
				
	</body>
</html>