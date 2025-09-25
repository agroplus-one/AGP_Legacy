<%@ page language="java" contentType="text/html;"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>

<html>
	<head>
		<title>Agroplus - Hist&oacute;rico importaciones</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		
		<script src="jsp/moduloTaller/importaciones/historico.js" type="text/javascript"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script src="jsp/js/util.js" type="text/javascript"></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
		<script type="text/javascript" src="jsp/js/calendar.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		
		
		<script type="text/javascript">
		
	    $(document).ready(function(){
	        var URL = UTIL.antiCacheRand(document.getElementById("main").action);
		    document.getElementById("main").action = URL;    
	    });
	    
		
		$(document).ready(function() {
		   	Zapatec.Calendar.setup({
		        firstDay          : 1,
		        weekNumbers       : false,
		        showOthers        : true,
		        showsTime         : false,
		        timeFormat        : "24",
		        step              : 2,
		        range             : [1900.01, 2999.12],
		        electric          : false,
		        singleClick       : true,
		        inputField        : "fechaIni",
		        button            : "btn_fechaIni",
		        ifFormat          : "%d-%m-%Y",
		        daFormat          : "%d-%m-%Y",
		        align             : "Br"
		   	});
		   	Zapatec.Calendar.setup({
		        firstDay          : 1,
		        weekNumbers       : false,
		        showOthers        : true,
		        showsTime         : false,
		        timeFormat        : "24",
		        step              : 2,
		        range             : [1900.01, 2999.12],
		        electric          : false,
		        singleClick       : true,
		        inputField        : "fechaFin",
		        button            : "btn_fechaFin",
		        ifFormat          : "%d-%m-%Y",
		        daFormat          : "%d-%m-%Y",
		        align             : "Br"
		   	});
	    });
	</script>		

	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">	
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuLateralTaller.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	<div id="buttons">
	<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">
					<table cellspacing="2" cellpadding="0" border="0">
						<tbody>
							<tr>
								<td><a class="bot" href="javascript:historico.consultaHist();">Consultar</a></td>
								<td><a class="bot" href="javascript:generales.enviar('limpiar');">Limpiar</a></td>
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
	<p class="titulopag" align="left">Histórico de importaciones</p>
	<form id="main" name="main" method="POST" action="importacion.html">
	<!-- <form action="importacion.html" method="post" id="frmHistorico"> -->
	<input type="hidden" name="operacion" id="operacion" value="" />
	<input type="hidden" name="seleccionado" value="" />
	<input type="hidden" name="nuevo" value="" />
	<input type="hidden" name="fechaIni.day" value=""> 
	<input type="hidden" name="fechaIni.month" value=""> 
	<input type="hidden" name="fechaIni.year" value=""> 
	<input type="hidden" name="fechaFin.day" value=""> 
	<input type="hidden" name="fechaFin.month" value=""> 
	<input type="hidden" name="fechaFin.year" value="">
	<input type="hidden" name="valorPlan" value=""> 
	<input type="hidden" name="valorLinea" value=""> 		
	
	
	<!-- PANEL 1 -->
		        <div class="panel1 isrt" style="padding:3px;width:97%">
				    <fieldset>
				        <span class="literal">Plan</span>
				        <select id="sl_planes" name="sl_planes" onchange="common.selectPlan_onchange('sl_planes', 'sl_lineas');" class="dato" style="width:60px">
							<option value="">Todos</option>
		                	<c:forEach items="${historico.planes}" var="planes">
									<option value="${planes}"
										<c:if test="${historico.filtro.plan != null && historico.filtro.plan == planes}">selected</c:if>>
							        	${planes}										
									</option>						
							</c:forEach>
						</select>
				    </fieldset>
				    		
					<fieldset>
						<span class="literal">L&iacute;nea</span>
					    <select id="sl_lineas" name="sl_lineas" class="dato">
					     <c:if test="${historico.filtro.plan != null && historico.filtro.linea != null}">
					    <c:forEach items="${sessionScope.listalineas}" var="lineas">
								<option value="${lineas.lineaseguroid}"
					     			<c:if test="${historico.filtro.linea != null && historico.filtro.linea == lineas.lineaseguroid}">selected</c:if>>
					    			${lineas.codlinea} - ${lineas.nomlinea}		
					    		</option>	
					    </c:forEach>
					    </c:if>
					    </select>
					    <img id="ajaxLoading_lineas" src="jsp/img/ajax-loading.gif" width="16px" 
					         style="cursor:hand;cursor:pointer;display:none" height="11px" />					         
					</fieldset>
					
					<fieldset>
					    <span class="literal">Fecha</span>
					    <table>
					    	<tr>
					    		<td class="literal">desde</td>
					    		<td><p class="txt">
					    			<input type="text" id="fechaIni" onchange="if (!ComprobarFecha(this, document.main, 'Fecha desde')) this.value='';" name="fechaIni" class="dato" value="${historico.filtro.fechaD}" size="10" maxlength="10"/>
					    			<a id="btn_fechaIni" name="btn_fechaIni"><img src="jsp/img/calendar.gif"/></a></p>
								</td>
								<td class="literal">&nbsp;&nbsp;hasta</td>
					    		<td><p class="txt">
					    			<input type="text" id="fechaFin" onchange="if (!ComprobarFecha(this, document.main, 'Fecha fin')) this.value='';" name="fechaFin" class="dato" value="${historico.filtro.fechaH}" size="10" maxlength="10"/>
					    			<a id="btn_fechaFin" name="btn_fechaFin"><img src="jsp/img/calendar.gif"/></a></p>
								</td>
					    	</tr>
					    </table>
					</fieldset>
					
					<fieldset>
					    <span class="literal">Tipo</span>
					    <select id="sl_tipo" name="sl_tipo" class="dato" style="width:150px">
					    	<option value="">Todos</option>
							<c:forEach items="${historico.tipos}" var="tipo">
								<option value="${tipo.idtipoimportacion}" <c:if test="${historico.filtro.tiposc != null && historico.filtro.tiposc == tipo.idtipoimportacion}">selected</c:if>>${tipo.descripcion}</option>
							</c:forEach>
						</select>
						<span class="literal">&nbsp;&nbsp;&nbsp;&nbsp;Estado</span>
						<select id="sl_estado" name="sl_estado" class="dato" style="width:100px">
							<option value="">Todos</option>
							<c:forEach items="${historico.estados}" var="estado">
								<option value="${estado.key}" <c:if test="${historico.filtro.estado != null && historico.filtro.estado == estado.key }">selected</c:if>>${estado.value}</option>
							</c:forEach>
						</select>
					    
					</fieldset>
				</div>
			</form>
		<div class="centraTabla">
		<!-- Aqui tiene que ir el grid de datos -->
        <display:table requestURI="importacion.html" id="listaResultados" class="LISTA" summary="HistImportaciones" sort="list" pagesize="10" name="${historico.resultado}" decorator="com.rsi.agp.core.decorators.ModelTableDecoratorHistorico" style="width:85%">
			<display:column class="accionesDisplayTagColumn" headerClass="cblistaImg" title="Acciones" property="histSelec" sortable="false" style="width:58px;text-align:center"/>
            <display:column class="literalDisplayTagFecha" headerClass="cblistaImg" title="Fec. Import" property="histFecImport" sortable="true" format="{0,date,dd/MM/yyyy  - HH:mm}" style="width:100px;text-align:center"/>
            <display:column class="literalDisplayTagPlanLinea" headerClass="cblistaImg" title="Plan" property="histPlan" sortable="false" style="width:45px;"/>
            <display:column class="literalDisplayTagPlanLinea" headerClass="cblistaImg" title="L&iacute;nea" property="histLinea" sortable="false" style="width:45px;"/>            
            <display:column class="literalDisplayTagDescLargo" headerClass="cblistaImg" title="Tipo Importaci&oacute;n" property="histTipoImport" sortable="true"/>
            <display:column class="literalDisplayTagDescCorto" headerClass="cblistaImg" title="Estado" property="histEstado" sortable="true" style="width:60px;"/>
            <display:column class="literalDisplayTagFecha" headerClass="cblistaImg" title="Fec. Activaci&oacute;n" property="histFechaAct" format="{0,date,dd/MM/yyyy  - HH:mm}" sortable="true" style="width:100px;text-align:center"/>
        </display:table>
        </div>		
	</div> 
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	</body>
</html>

