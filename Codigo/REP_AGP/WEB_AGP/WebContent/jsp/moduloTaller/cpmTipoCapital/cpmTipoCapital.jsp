<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Mantenimiento de CPM - Tipo Capital</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>

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
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/moduloTaller/cpmTipoCapital/cpmTipoCapital.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
		
		<script type="text/javascript">
			 
			function cargarFiltro(){
					
				<c:forEach items="${sessionScope.consultaCPMTipoCapital_LIMIT.filterSet.filters}" var="filtro">
					<c:if test="${filtro.property == 'cultivo.linea.codplan'}">
						$('#plan').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'cultivo.linea.codlinea'}">
						$('#linea').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'modulo'}">
						$('#codmodulo').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'tipoCapital.codtipocapital'}">
						$('#capital').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'conceptoPpalModulo.codconceptoppalmod'}">
						$('#cpm').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'cultivo.id.codcultivo'}">
						$('#cultivo').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'sistemaCultivo.codsistemacultivo'}">
						$('#sistemaCultivo').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'fechafingarantia'}">
						$('#fechaFinG').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'cicloCultivo.codciclocultivo'}">
						$('#cicloCultivo').val('${filtro.value}');
					</c:if>
				</c:forEach>
			}
		
		</script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="cargarFiltro();">		
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuLateralTaller.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tbody>
					<tr>
						<td align="right">		
							<a class="bot" id="btnModificar" style="display:none" href="javascript:modificar();">Modificar</a>	
							<a class="bot" id="btnReplicar" href="javascript:replicar()">Replicar</a>
							<a class="bot" id="btnAlta" href="javascript:alta();">Alta</a>				
							<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
							<a class="bot" href="javascript:limpiar();">Limpiar</a>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:101%">
		<p class="titulopag" align="left">Mantenimiento de CPM - Tipo Capital</p>
			
			<form:form name="main3" id="main3" action="cpmTipoCapital.run" method="post" commandName="cpmTipoCapitalBean">
				
				<form:hidden path="id" id="id" />
				<form:hidden path="cultivo.linea.lineaseguroid" id="lineaseguroid" />
				
				<input type="hidden" name="planreplica" id="planreplica" value="${planreplica}"/>
				<input type="hidden" name="lineareplica" id="lineareplica" value="${lineareplica}"/>
				<input type="hidden" name="desc_lineareplica" id="desc_lineareplica" value="${desc_lineareplica}"/>
				
				<input type="hidden" name="method" id="method" />
				
				<input type="hidden" name="fechaFinG.day" value="">
				<input type="hidden" name="fechaFinG.month" value="">
				<input type="hidden" name="fechaFinG.year" value="">
								
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div class="panel2 isrt"> 
					<fieldset>
					<legend class="literal">Filtro</legend>
							<table >
								<tr>
									<td class="literal">Plan</td>
									<td class="literal">
										<form:input path="cultivo.linea.codplan" id="plan" size="4" maxlength="4" cssClass="dato"  tabindex="1" />
									</td>
									<td class="literal">L&iacute;nea</td>
									<td class="literal">
										<form:input path="cultivo.linea.codlinea" id="linea" size="2" maxlength="3" cssClass="dato" onchange="javascript:lupas.limpiarCampos('desc_linea');" tabindex="1"/>
										<form:input path="cultivo.linea.nomlinea" id="desc_linea" cssClass="dato"  size="25" readonly="true"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />										
									</td>
										
									<td class="literal">Módulo</td>
									<td class="literal">
										<form:input path="modulo" id="codmodulo" size="4" maxlength="5" cssClass="dato" readonly="false" onchange="this.value=this.value.toUpperCase();" tabindex="1"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:if(validarLinea()){lupas.muestraTabla('Modulo','principio', '', '');}" alt="Buscar Módulo" title="Buscar Módulo"  />
									</td>								
								</tr>
								<tr>
									<td class="literal">Cultivo</td>
									<td class="literal">
										<form:input  id="cultivo" path="cultivo.id.codcultivo" cssClass="dato" size="2" maxlength="3" onchange="javascript:lupas.limpiarCampos('desc_cultivo');" tabindex="1"/>
										<form:input cssClass="dato" path="" id="desc_cultivo" size="25" readonly="true"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:if(validarLinea()){lupas.muestraTabla('CultivoSbp','principio', '', '');}" alt="Buscar Cultivo" title="Buscar Cultivo" />
									</td>
									<td class="literal">Sist. Cultivo</td>
									<td class="literal">
										<form:input path="sistemaCultivo.codsistemacultivo" size="2" onchange="javascript:lupas.limpiarCampos('dessistemaCultivo');" maxlength="3" cssClass="dato" id="sistemaCultivo" tabindex="1"/>
										<form:input path="sistemaCultivo.dessistemacultivo" size="25" maxlength="30" cssClass="dato" id="dessistemaCultivo" readonly="true"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('SistemaCultivo','principio', '', '');"	alt="Buscar Sistema Cultivo" title="Buscar Sistema Cultivo" />
									</td>
									<td class="literal">Fin de Garantía</td>
			                         <td class="literal">
					                    <spring:bind path="fechafingarantia">
					                    	 <input type="text" name="fechafingarantia" id="fechaFinG" size="11" maxlength="10" class="dato" tabindex="1"
					                    	 		onblur=" if (!ComprobarFecha(this, document.main3, 'Fecha Fin Garantia')) this.value=''"
													value="<fmt:formatDate pattern="dd/MM/yyyy" value="${cpmTipoCapitalBean.fechafingarantia}" />" />
					                    </spring:bind>
				                     <input type="button" id="btn_fechafingarantia" name="btn_fechafingarantia" class="miniCalendario" style="cursor: pointer;" /> 
			                         </td>
								</tr>
								<tr>
									<td class="literal">Tipo Capital</td>
									<td class="literal">
										<form:input path="tipoCapital.codtipocapital" id="capital" size="2" maxlength="3" cssClass="dato" onchange="javascript:lupas.limpiarCampos('desc_capital');" tabindex="1"/>
										<input class="dato"	id="desc_capital" size="25" readonly="readonly"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:if(validarLinea()){muestraLupaTipoCapitalCultivo();}" alt="Buscar Tipo Capital" title="Buscar Tipo Capital" />	
									</td>
									<td class="literal">CPM</td>
									<td class="literal">
										<form:input path="conceptoPpalModulo.codconceptoppalmod" id="cpm" size="2" maxlength="3" cssClass="dato" tabindex="1"/>
										<form:input cssClass="dato" path="conceptoPpalModulo.desconceptoppalmod" id="desc_cpm" size="25" readonly="true"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:if(validarLinea()){ muestraLupaCPM();}" alt="Buscar CPM" title="Buscar CPM" />	
									</td>	
									<td class="literal">Ciclo Cultivo</td>
									<td class="literal">
										<form:input path="cicloCultivo.codciclocultivo" id="cicloCultivo" size="2" maxlength="3" cssClass="dato"  onchange="javascript:lupas.limpiarCampos('desc_cicloCultivo');" tabindex="1"/>
										<form:input cssClass="dato" path="cicloCultivo.desciclocultivo" id="desc_cicloCultivo" size="25" readonly="true"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:if (validarLinea()){muestraLupaFactores(618);}"	alt="Buscar Ciclo Cultivo" title="Buscar Ciclo Cultivo" />
									</td>				
								</tr>
							</table>
					</fieldset>
				</div>
			</form:form>
			
			<form:form name="frmBorrar" id="frmBorrar" action="cpmTipoCapital.run" method="post" commandName="cpmTipoCapitalBean">
				<form:hidden path="id" id="idBorrar" />
				<input type="hidden" name="method" id="methodBorrar" />
			</form:form>
			
			<!-- Grid Jmesa -->
			<div id="grid" style="width: 85%; margin:0 auto;">
				 ${consultaCPMTipoCapital}
			</div>
		</div>
		
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>		
		<%@ include file="/jsp/common/lupas/lupaLineaReplicar.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaModulo.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaTipoCapitalCultivo.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaCultivoSbp.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaSistemaCultivo.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaCPM.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaFactores.jsp"%>

	</body>
</html>
