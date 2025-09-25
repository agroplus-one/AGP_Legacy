<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Mantenimiento de Datos del Informe</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
	
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
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
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/moduloInformes/datosinforme.js"></script>
		<script type="text/javascript" src="jsp/moduloInformes/mtoInformesAjax.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>				
	
	</head>
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="javascript:SwitchSubMenu('sub12','sub11');">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
		<!-- botones de la página -->
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td width="5">&nbsp;</td>
					<td align="left">
						<a class="bot" id="btnCondicion" href="javascript:condicionInformes();">Condiciones</a>						
						<a class="bot" id="btnClasificacionRuptura" href="javascript:clasificacionRuptura();">Clasificación y ruptura</a>
						<a class="bot" id="btnGenerar" href="javascript:ajaxCheckInforme();">Generar</a>
					</td>
					<td align="right">
						<a class="bot" id="btnModificar" style="display:none" href="javascript:modificar()">Modificar</a>
						<a class="bot" id="btnAlta" href="javascript:alta()">Alta</a> 
						<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a> 
						<a class="bot" id="btnLimpiar"href="javascript:limpiar()">Limpiar</a>
						<a class="bot" id="btnVolver"href="javascript:volver()">Volver</a>
					</td>
				</tr>
			</table>
		</div>
		
		<!-- Contenido de la pï¿½gina -->
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Mantenimiento de datos del informe</p>
		<form:form id="main3" name="main3"  action="mtoDatosInforme.run" method="post" commandName="vistaMtoinfDatosInformes">
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<input type="hidden" name="method" id="method" />
				<input type="hidden" name="idInforme" id="idInforme" value="${idInforme}"/>
				<input type="hidden" name="nombre" id="nombre" value="${nombre}"/>
				<input type="hidden" name="redireccion" id="redireccion"/>
				<input type="hidden" name="origenLlamada" id="origenLlamada" value="${origenLlamada}"/>
				<input type="hidden" name="condicionesOk" id="condicionesOk" />
				<input type="hidden" name="tipoNumerico" id="tipoNumerico" value="${tipoNumerico}"/>
				<input type="hidden" name="tipoFecha" id="tipoFecha" value="${tipoFecha}"/>
				<input type="hidden" name="tipoTexto" id="tipoTexto" value="${tipoTexto}"/>
				<input type="hidden" name="codFormatosFec" id="codFormatosFec" value="${codFormatosFec}"/>
				<input type="hidden" name="formatosFec" id="formatosFec" value="${formatosFec}"/>
				<input type="hidden" name="codFormatosNum" id="codFormatosNum" value="${codFormatosNum}"/>
				<input type="hidden" name="formatosNum" id="formatosNum" value="${formatosNum}"/>
				<!-- Tipos de totaliza -->
				<input type="hidden" name="totalizaNo" id="totalizaNo" value="${totalizaNo}"/>
				<input type="hidden" name="totalizaSuma" id="totalizaSuma" value="${totalizaSuma}"/>
				<!-- Datos del campo seleccionado -->							
				<input type="hidden" name="abreviadoSeleccionado" id="abreviadoSeleccionado"/>
				<input type="hidden" name="tipoSeleccionado" id="tipoSeleccionado"/>
				<input type="hidden" name="recogerInformeSesion" id="recogerInformeSesion" value="${recogerInformeSesion}"/>
				
		<fieldset> 
			<legend class="literal">Datos del informe : ${nombre} </legend>
			<div class="panel2 isrt">
				<table width="100%" cellpadding="2" cellspacing="2">		
					<tr>
						<td class="literal"  style="width:30px">Columna</td>
						<td class="literal" width="405px" colspan="3">
							<select name="listaCampos" id="listaCampos" class="dato" style="width:400px" onchange="javascript: seleccionarCampo (this.value);">
								<option value="">Mostrar todos</option>
								<c:forEach items="${listaCampoInforme}" var="campInf">
									<option value="${campInf.permitidoOCalculado}-id:${campInf.id}-abv:${campInf.nombre}-tipo:${campInf.tipo}">${campInf.nombreVista} - ${campInf.nombre} (${campInf.descTipo})</option>
								</c:forEach>
							</select>
						</td>
						<td>&nbsp;</td>
						<td class="literal"  >Abreviado</td>
						<td colspan="2">
							<form:input path="abreviado" id="abreviado" cssClass="dato" size="30" maxlength="20" onchange="this.value=quitaAcentos(this.value);this.value=this.value.toUpperCase();"/>
						</td>
					</tr>	
					<tr>
						<td class="literal"  style="width:20px">Formato</td>
						<td class="literal">
							<form:select id="formato" path="formato" cssClass="dato" cssStyle="width:105px" onchange="javascript:habilitaDecimales()">
								<option value="">Todos</option>
							</form:select>
						</td>
						<td class="literal"  align="right">Decimales</td>
						<td valign="top">
							<form:input path="decimales" id="decimales" cssClass="dato" size="5" maxlength="1"/>
						</td>
						<td>&nbsp;</td>
						<td class="literal">Totaliza</td>
						<td class="literal">
						   <form:select id="totaliza" path="totaliza" cssClass="dato" cssStyle="width:65" onchange="javascript:habilitaTotalPorGrupo();"> 
						   		<option value="">Todos</option>
						   		<option value="0">No</option>
						   		<option value="1">Suma</option>
							</form:select>
						</td>
						<td class="literal" align="right" colspan="2">Total por grupo</td>
						<td class="literal">
							<form:select id="total_por_grupo" path="total_por_grupo" cssClass="dato" cssStyle="width:65">
						   		<option value="">Todos</option>
						   		<option value="1">Si</option>
								<option value="0">No</option>
							</form:select>
						</td>
					</tr>
				</table>
				</fieldset>
				
					<form:hidden path="id.id" id="id" />
					<form:hidden path="idinforme" id="informeid" />
					<form:hidden path="idcampo" id="idcampo" />
					<form:hidden path="id.permitidocalculado" id="permitidocalculado" />
					<form:hidden path="orden" id="orden" />
									
						
				</form:form>
			
				<!-- Grid Jmesa -->
				<div id="grid">
			  		${consultaDatosInforme}		  							               
				</div> 	
				
  			</div>	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<%@ include file="/jsp/moduloInformes/popupFormatoInforme.jsp"%>
	
	</body>
</html>