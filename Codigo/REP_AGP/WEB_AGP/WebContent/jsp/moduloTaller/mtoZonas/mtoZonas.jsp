<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<html>
	<head>
	<style>
 		#divImprimir {
        display: none;
    	}
	</style>
		<title>Mantenimiento de Zonas</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
				
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
        <script type="text/javascript" src="jsp/js/calendar.js"></script>
        <script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
        <script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/moduloTaller/mtoZonas/mtoZonas.js" ></script>
		
		<%@ include file="/jsp/js/draggable.jsp"%>
		
		<script type="text/javascript">
			 
		function cargarFiltro() {
			 
		 	if($('#origenLlamada').val() == 'menuGeneral') {
				$('#fechaLimiteId').val('');
			}
		 
		 	<c:if test="${origenLlamada != 'menuGeneral'}">	
				<c:forEach items="${sessionScope.consultaZonas_LIMIT.filterSet.filters}" var="filtro">
				
				<c:if test="${filtro.property == 'id.codentidad'}">
					$('#codEntidad').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'id.codzona'}">
					$('#codzona').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'nomzona'}">
					$('#nomzona').val('${filtro.value}');
				</c:if>
			</c:forEach>
		</c:if>
	} 



		
		</script>

	</head>
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="cargarFiltro();">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuLateralTaller.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		<!-- botones de la página -->
		<div id="buttons" >
			<table width="97%" cellspacing="2" cellpadding="2" border="0">
				<tbody>
					<tr>
						<td>
						</td>
						<td align="right">
								<a class="bot" id="btnModificar"  href="javascript:modificar();">Modificar</a>
							<a class="bot" id="btnAlta" href="javascript:alta();">Alta</a>
							<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
							<a class="bot" href="javascript:limpiar();">Limpiar</a>							
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<form name="limpiar" id="limpiar" action="mtoZonas.run" method="post">
			<input type="hidden" name="method" id="method" value="doConsulta"/>
			<input type="hidden" name="origenLlamada" id="origenLlamada" value="menuGeneral"/>
		</form>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%" >
		<p class="titulopag" align="left">MANTENIMIENTO ZONAS</p>
			
			<form:form name="main3" id="main3" action="mtoZonas.run" method="post" commandName="zonaBean">
				<input type="hidden" name="method" id="method" />
				<input type="hidden" name="origenLlamada" id="origenLlamada" />	
				<input type="hidden" id="perfil" value="${perfil}" />
				<input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
				<input type="hidden" name="externoIni" id="externoIni"/>
				<input type="hidden" name="alerta" id="alerta" value ="${alerta}"/>
				<input type="hidden" name="codentidadInicial" id="codentidadInicial" value="${codentidadInicial}"/>
				<input type="hidden" name="codzonaInicial" id="codzonaInicial" value="${codzonaInicial}"/>
				
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div class="panel2 isrt" style="width: 95%;margin:0 auto;">
					<fieldset>
					<legend class="literal">Filtro</legend>
						<table>
							<tr align="left">
								<td class="literal">Entidad</td>
								<td class="literal" nowrap align="left">
									&nbsp<form:input	path="id.codentidad" size="4" maxlength="4" cssClass="dato" id="entidad" tabindex="7"/>
									<input class="dato"	id="desc_entidad" name="desc_entidad" size="40" readonly="true"/>
									<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');" alt="Buscar Entidad" title="Buscar Entidad"/>
									<label class="campoObligatorio" id="campoObligatorio_codentidad" title="Campo obligatorio"> *</label>
								</td>	
							</tr>
							<tr align="left">
								<td class="literal" align="left">Zona</td>
								
								<td class="literal" nowrap align="left">
									<form:input	path="id.codzona" size="5" maxlength="5" cssClass="dato" id="codzona" tabindex="7" onchange="javascript:limpiarDesc();"/>
									<form:input	path="nomzona" size="40" maxlength="40" cssClass="dato" id="nomzona" tabindex="7"/>
									<label class="campoObligatorio" id="campoObligatorio_nomzona" title="Campo obligatorio"> *</label>
								</td>
							</tr>
						</table>
					</fieldset>
				</div>
			</form:form>
			
			<form:form name="frmBorrar" id="frmBorrar" action="mtoZonas.run" method="post" commandName="zonaBean">
				<input type="hidden" name="method" id="methodBorrar" />
				<form:hidden path="id.codentidad" id="codEntidadBorrar"/>
				<form:hidden path="id.codzona" id="codZonaBorrar"/>
			</form:form> 
			
			<!-- Grid Jmesa -->
			<div id="grid" style="width: 80%;margin:0 auto;">
		  		${consultaZonas}		  							               
			</div>
			
			<!-- Formulario para exportar a excel el listado -->
			<form name="exportToExcel" id="exportToExcel" action="mtoZonas.run" method="post">
    			<input type="hidden" name="method" id="method" value="doExportToExcel"/>
			</form>

			<div style="width:20%;text-align:center;margin:0 auto;" id="divImprimir">
   				<a style="font-family: tahoma, verdana, arial;color: #626262;font-size:11px;">Exportar</a>
   				<a id="btnImprimirExcel" style="text-decoration:none;" href="javascript:exportToExcel()">
        			<img src="jsp/img/jmesa/excel.gif"/>
    			</a>
			</div>
	</div>
	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
	
		<!-- ************* -->
		<!-- POPUP  AVISO  -->
		<!-- ************* -->
		
		<div id="popUpAvisos" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
		     <!--  header popup -->
			 <div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
		        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">Aviso</div>
		        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
		                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
		            <span onclick="hidePopUpAviso()">x</span>
		        </a>
			 </div>
			 <!--  body popup -->
			 <div class="panelInformacion_content">
				<div id="panelInformacion2" class="panelInformacion">
					<div id="txt_mensaje_aviso"></div>
				</div>
				<div style="margin-top:15px">
				 	    <a class="bot" id="btn_hidePopUpAviso" href="javascript:hidePopUpAviso()" title="Aceptar">Aceptar</a>
				</div>
			 </div>
		</div>
	
</body>
</html>