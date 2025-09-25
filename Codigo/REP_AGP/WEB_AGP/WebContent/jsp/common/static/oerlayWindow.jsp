<!--
/*
**************************************************************************************************
*
*  CReACION: --> en desarrollo!!
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034              Miguel Granadino  Muestra un overlay
*
* Usage: por claridad se aconseja situarlo justo antes del cierre de la etiqueta body 
 **************************************************************************************************
*/
-->
<div id="overlay" class="overlay" style="width:100%;height:100%;top:0;left:0;
    position:absolute; background-color:#CCC;display:none;z-index:1003;
    filter:alpha(opacity=50);-moz-opacity:.50;opacity:.50;">
</div>



<div id="window" class="window"  style="cursor:pointer;cursor:hand;display:none;height:200px;
    width:350px;top:150px;left:350px;background-color:white;border:1px solid #CCC;
    position:absolute;z-index:1006">
    <div onclick="relCamp.closeModalWindow();">[Cerrar]</div>
</div>