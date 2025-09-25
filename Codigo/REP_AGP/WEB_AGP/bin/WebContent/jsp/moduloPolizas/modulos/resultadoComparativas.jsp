<!-- 
/*
**************************************************************************************************
*
*  CREACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034  18/08/2010  Ernesto Laura		Página de resultados de comparativas modulos    
*
 **************************************************************************************************
*/
-->

<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<jsp:directive.page import="org.displaytag.*" />




<html>
	<head>
		<title>Resultado seleccion comparativa modulos</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script src="jsp/js/jquery-v1.4.2.js" type="text/javascript"></script>
		<script src="jsp/js/util.js" type="text/javascript"></script>		
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">	
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/datosCabecera.jsp"%>
	
	<div id="buttons">
	<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">
					<table cellspacing="2" cellpadding="0" border="0">
						<tbody>
							<tr>
								<td>
									<a class="bot" href="">Volver</a>
								</td>
							</tr>
						</tbody>
					</table>
					<!-- FIN TABLA BARRA DE BOTONES-->
				</td>
			</tr>
	</table>
	</div>	
	<!-- Contenido de la página -->
	<div class="conten">
	<fieldset>
			<legend class="literal">COBERTURAS</legend>
			<table width="100%">
				<c:forEach items="${listaSeleccion.seleccionados}" var="select">
				<tr>		
					<td class="literal">--> ${select}</td>		
				</tr>
				</c:forEach>	
				</table>		
		</fieldset>
	<table width="100%">
		<tr>
			<td class="literal">INFO:</td>
		</tr>		
		<tr>
			<td class="literal">Valores separados por /</td>
		</tr>
		<tr>
			<td class="literal">Primer valor, c&oacute;digo del m&oacute;dulo</td>
		</tr>
		<tr>
			<td class="literal">Segundo valor, c&oacute;digo/s del concepto principal, se separan por comas si hay m&aacute;s de uno (varias coberturas)</td>
		</tr>
		<tr>
			<td class="literal">Tercer valor, c&oacute;digo/s del riesgo cubierto, se separan por comas si hay m&aacute;s de uno (varias coberturas)</td>
		</tr>
		<tr>
			<td class="literal">Cuarto valor, Concepto/s, se separan por comas si hay m&aacute;s de uno (varias coberturas)</td>
		</tr>
		<tr>
			<td class="literal">Quinto valor, Valor cobertura, se separan por comas si hay m&aacute;s de uno (varias coberturas)</td>
		</tr>		
	</table>
	</div>
	</body>
</html>
	