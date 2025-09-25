<!--                                                     -->		
<!-- popupInformacionRega.jsp (show in listadoExplotaciones.jsp) -->
<!--                                                     -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />

<style>

#datosRega {
	width: 250px;
}

#listaDatos li {
	padding-top: 2px;
	padding-bottom: 2px;
}
.panelInformacionRega {
	display: none;
	color:#333333;
	-moz-border-radius:4px 4px 4px 4px;
	padding:0.2em;
    width: 980px;
    top: 50px;
    left: 50%;
    position: absolute;
   	transform: translateX(-50%);
    z-index: 1006;
    border: 1px solid #A4A4A4;
    background-color: #FFFFFF !important;
}
</style>
<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>

<script>
function cerrarPopUpInformacionRega(){
    $('#panelInformacionRega').fadeOut('normal');
    $('#overlay').hide();
    limpiaInfoRega();
}
</script>

		<div id="panelInformacionRega" class="panelInformacionRega">
		  <!--  header popup -->
			 <div id="header-popupMU" style="padding:0.4em 0.4em; position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:1px 1px 1px 1px;background:#525583;height:15px">
				        <div  style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
				            Información REGA
				        </div>
				        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.1em;top:50%;width:25px;
				                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
				            <span onclick="cerrarPopUpInformacionRega()">x</span>
				        </a>
			 </div>
			 
		 <!--  body popup -->
	
			<div class="panelInformacion_content" style="display:flex;width:100%;padding:0;justify-content:center;">
				<div id="datosRega" style="display:flex;flex-direction:column;padding-top: 20px;padding-bottom: 20px;width: 50%;">
					<div>
						<ul id="listaDatos" style="font-size:11px;list-style-type: none;text-align:left;padding-left:0px;padding-bottom: 15px;">
							<li><strong style="margin-right: 3px;">Explotación registrada:</strong><label id="explotacionRegistrada"/></li>
							<li><strong style="margin-right: 3px;">Fecha efecto:</strong><label id="fechaEfecto"/></li>
							<li><strong style="margin-right: 3px;">Fecha version censo:</strong><label id="fechaVersionCenso"/></li>
						</ul>
					<div>
					<div id="panelCensoLineas">
					</div>
					<div style="margin:10px auto" id="div_bot" >
						<a class="bot" href="#" onclick="cerrarPopUpInformacionRega();">Cerrar</a>
					</div>					
				</div>
			</div>
		</div>
	</div>