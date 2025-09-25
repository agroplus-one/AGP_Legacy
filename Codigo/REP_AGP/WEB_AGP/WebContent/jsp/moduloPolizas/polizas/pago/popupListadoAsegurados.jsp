<style>
    
    .filaAsegurado:hover {
  		background-color: rgb(204, 204, 204);
	}
    
</style>

<div id="listadoAsegurados" class="window ui-draggable" style="width: 60%; top: 20%; left: 20%; background-color: white; border: 1px solid black; position: absolute; z-index: 1006; display: none;">
  <div id="buttons" align="right" style="padding: 3px;background-color:#e5e5e5">
    <a class="bot" style="padding:3px;text-decoration:none;font-weight: bold; font-size: 11px;filter:alpha(opacity=50);-moz-opacity:.50;opacity:.50;" href="javascript:cerrarListadoAsegurados();">X</a>
  </div>
  <div class="conten">
    <p class="titulopag" align="center" style="margin:0px;padding:0px;font-size:12px;">LISTA DE ASEGURADOS</p>
    <div class="panel2 isrt" style="width:99%;">
      <table width="90%" align="center" id="tablaContenedora">
        <tbody>
          <tr align="center">
            <td id="tabla_listadoAsegurados" colspan="3">
              <table id="tabla" width="100%" style="border: 1px solid rgb(204, 204, 204); table-layout: fixed;">
                <thead>
                  <tr>
                    <td class="cblistaImg literal" width="35%" style="background-color: rgb(229, 229, 229);">NIF/CIF</td>
                    <td class="cblistaImg literal" width="70%" style="background-color: rgb(229, 229, 229);">Nombre</td>
                    <td class="cblistaImg literal" width="35%" style="background-color: rgb(229, 229, 229);">Oficina</td>
                    <td class="cblistaImg literal" width="35%" style="background-color: rgb(229, 229, 229);">Fecha Nacimiento</td>
                    <td class="cblistaImg literal" width="35%" style="background-color: rgb(229, 229, 229);">Id IRIS</td>
                    <td class="cblistaImg literal" width="35%" style="background-color: rgb(229, 229, 229);">Ac. Ruralvia</td>
                  </tr>
                </thead>
                <tbody id="registrosAsegurados"></tbody>
              </table>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>