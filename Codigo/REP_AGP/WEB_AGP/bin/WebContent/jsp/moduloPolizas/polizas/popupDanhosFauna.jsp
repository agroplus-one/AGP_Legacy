<!--                                                     -->		
<!-- popupDanhosFauna.jsp (show in listadoparcelas.jsp) -->
<!--                                                     -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>

<script>
function cerrarPopDanhosFauna(){
    $('#panelDanhosFauna').fadeOut('normal');
    $('#overlayDanhosFauna').hide();
}
</script>
<form id="frmDanhosfauna" name="frmDanhosFauna" action="danhosFauna.html" method="post" >

<!-- mantener checks en paginacion -->
	<input type="hidden" name="idsRowsCheckedDF"           id="idsRowsChecked"             />
    <input type="hidden" name="marcarTodosChecks"          id="marcarTodosChecks"          value="${marcarTodosChecks}"/>
    <input type="hidden" name="isClickInListado"           id="isClickInListado" />
    <input type="hidden" name="tipoListadoGridDF"          id="tipoListadoGridDF" />
    <input type="hidden" name="stringprueba"          		id="stringprueba" />
    <input type="hidden" name="method"           		   id="method" />
    
	<div id="panelDanhosFauna" class="panelCambioMasivo" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
	  <!--  header popup -->
		 <div id="header-popupMU" style="padding:0.4em 0.4em; position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:1px 1px 1px 1px;background:#525583;height:15px">
			        <div  style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
			            Daños por Fauna
			        </div>
			        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.1em;top:50%;width:25px;
			                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
			            <span onclick="cerrarPopDanhosFauna()">x</span>
			        </a>
		 </div>
		 
	 <!--  body popup -->

		<div class="panelInformacion_content" style="width: 100%; padding: 0;">
			<div id="botones_abajo" width="100%" style="margin-top: 10px">
				<c:choose>
					<c:when test="${not empty mapDanhoFauna}">
						<table width="100%">
							<thead>
								<tr>
									<th class="literalbordeCabecera" width="15%">SIGPAC</th>
									<th class="literalbordeCabecera" width="07%">Fecha Vigor</th>
									<th class="literalbordeCabecera" width="05%">Activo</th>
									<th class="literalbordeCabecera" width="05%">Reduccion</th>
									<th class="literalbordeCabecera" width="55%">Descripcion</th>
								</tr>
							</thead>

							<tbody id="tblDanhoFauna">
								<c:forEach items="${mapDanhoFauna}" var="entry">
									<tr>
										<td class=literalborde style="TEXT-ALIGN: left">${entry.key}</td>
										<td class=literalborde style="TEXT-ALIGN: left">${entry.value.fechaVigor}</td>
										<td class=literalborde style="TEXT-ALIGN: left">${entry.value.enVigor}</td>
										<td class=literalborde style="TEXT-ALIGN: left">${entry.value.reduccionProducion}</td>
										<td class=literalborde style="TEXT-ALIGN: left">${entry.value.descripcion}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</c:when>
					<c:otherwise>
						<table width="100%">
							<tbody id="tblDanhoFauna">
								<tr>
									<td class=literal style="text-align:center;vertical-align:middle;">No se han encontrado datos de ninguna
										parcela con información de reducción de la producción por
										riesgo de fauna para los parámetros enviados</td>
								</tr>
							</tbody>
						</table>
					</c:otherwise>
				</c:choose>

				<table width="100%">
					<tr>
						<td class="literal" align="center"
							style="margin: 0 auto; display: table; text-align: center;">
							<a class="bot" href="javascript:cerrarPopDanhosFauna()">Cancelar</a>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</div>
</div>
</form>