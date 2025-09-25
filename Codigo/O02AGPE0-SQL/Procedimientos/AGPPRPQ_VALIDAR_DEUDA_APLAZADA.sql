SET DEFINE OFF;
SET SERVEROUTPUT ON;
create or replace package o02agpe0.PQ_VALIDAR_DEUDA_APLAZADA is

PROCEDURE doValidarFicheroDeudaAplazada(IDFICHERO IN NUMBER);

  PROCEDURE checkIncidencias(v_isValidLineaSeguroID    IN BOOLEAN,
                             v_lineaSeguroId           IN NUMBER,
                             v_isValidColectivo        IN BOOLEAN,
                             v_refColectivoFichero     IN VARCHAR,
                             v_isValidEntidadColectivo IN BOOLEAN,
                             v_xml_entidadColectivo    IN NUMBER,
                             v_xml_entMedColectivo     IN NUMBER,
                             v_xml_ESMedColectivo      IN VARCHAR,
                             v_sys_codEntidadCol       IN NUMBER,
                             v_sys_codEntMedCol        IN NUMBER,
                             v_sys_codESMedCol         IN NUMBER,
                             v_isColectivoRepetido     IN BOOLEAN,
                             v_isCoincideRefCol        IN BOOLEAN,
                             v_isValidReferenciaPoliza IN BOOLEAN,
                             v_valiPorcentajes         IN NUMBER,
                             v_valiComiAsoc            IN NUMBER,
                             IDFICHERO                 IN NUMBER,
                             v_idReciboDeuda           IN NUMBER,
                             v_refPolizaFichero        IN VARCHAR2,
                             v_oficinaPoliza           IN VARCHAR2,
                             v_faseFichero             IN VARCHAR2,
                             v_polizaSinComisiones     IN NUMBER);

end PQ_VALIDAR_DEUDA_APLAZADA;
/
create or replace package body o02agpe0.PQ_VALIDAR_DEUDA_APLAZADA is

/*
Validaciones mínimas:
-	Existencia de la entidad
-	Existencia de la subentidad
-	Existencia de la línea (v_isValidLineaSeguroID)
-	Existencia del colectivo (v_isValidColectivo)
-	Revisar que la subentidad tiene porcentaje asignado.
-	Revisar que coincide la E-S Mediadora del colectivo con la que viene en el fichero.
-	Existencia  de comisiones asociadas.
*/
PROCEDURE doValidarFicheroDeudaAplazada (IDFICHERO IN NUMBER) IS

TYPE TpCursor               	IS REF CURSOR;
l_tp_cursor                 	TpCursor;
l_sql VARCHAR2(20000);

-- validaciones
v_isValidReferenciaPoliza       BOOLEAN := TRUE;
v_isValidColectivo              BOOLEAN := TRUE;
v_isValidEntidadColectivo       BOOLEAN := TRUE;
v_isValidLineaSeguroID          BOOLEAN := TRUE;
v_isColectivoRepetido           BOOLEAN := FALSE;
v_isCoincideRefCol              BOOLEAN := TRUE;
v_valiPorcentajes               NUMBER  := 0;
v_valiComiAsoc                  NUMBER  := 0;
v_polizaSinComisiones           NUMBER := 0;

---OTROS
v_idReciboDeuda            		TB_COMS_UNIF_RECIBO.ID%TYPE;
v_planFichero               	TB_COMS_UNIF_RECIBO.PLAN%TYPE;
v_lineaFichero              	TB_COMS_UNIF_RECIBO.LINEA%TYPE;
v_refColectivoFichero       	TB_COMS_UNIF_COLECTIVO.REFERENCIA%TYPE;
v_xml_dcrefcol              	TB_COMS_UNIF_COLECTIVO.DC%TYPE;
v_xml_codinternocol         	TB_COMS_UNIF_COLECTIVO.CODIGO_INTERNO%TYPE;

v_sys_codEntidadCol         	NUMBER := 0;
v_sys_codEntMedCol          	NUMBER := 0;
v_sys_codESMedCol           	NUMBER := 0;
v_sys_referenciaCol         	TB_COLECTIVOS.IDCOLECTIVO%TYPE;

v_lineaSeguroId             	NUMBER := 0;
v_xml_entidadColectivo        VARCHAR2(4);
v_xml_entMedColectivo         VARCHAR2(20);
v_xml_ESMedColectivo          VARCHAR2(20);
v_aux_codInterno              VARCHAR2(20);

oficinaPoliza              	 TB_POLIZAS.OFICINA%TYPE;
v_faseFichero                tb_coms_unif_fase.fase%TYPE;
v_fechaEmision               tb_coms_unif_fase.fecha_emision_recibo%TYPE;
v_codEntidadFichero          VARCHAR2(4);
v_refPolizaFichero           tb_coms_unif_aplicacion.referencia%TYPE;
v_dcFichero                  tb_coms_unif_aplicacion.dc%TYPE;
v_tipoRefFichero             tb_coms_unif_aplicacion.tipo_referencia%TYPE;
v_colIdAuxFichero            TB_COMS_UNIF_COLECTIVO.ID%TYPE;
v_estadoFichero              tb_coms_unif_fichero.estado%TYPE;
v_idAplicacion               tb_coms_unif_aplicacion.id%TYPE;

contador number:=0;

BEGIN

v_fechaEmision:= SYSDATE;

l_sql :='SELECT fase.fase fase,
                rec.plan plan,
                rec.linea linea,
                rec.id idrecibo,
                SUBSTR(apli.codigo_interno,2,3) AS codentidad,
                apli.referencia refPoliza,
                apli.dc,
                apli.tipo_referencia,
                col.id,
                col.referencia refColectivo,
                col.codigo_interno,
                col.dc,
                apli.id
FROM TB_COMS_UNIF_FASE fase,
     TB_COMS_UNIF_INDIV_COLECTIVO incol,
     TB_COMS_UNIF_COLECTIVO col,
     TB_COMS_UNIF_INDIVIDUAL ind,
     TB_COMS_UNIF_APLICACION apli,
     TB_COMS_UNIF_RECIBO rec
WHERE fase.idfichero = '||IDFICHERO||'
AND fase.id = incol.idfase
AND incol.idcolectivo = col.id(+)
AND incol.idindividual = ind.id(+)
AND incol.id = apli.idindivcol_da
AND apli.id = rec.idaplicacion_da';

	OPEN l_tp_cursor FOR l_sql;
		LOOP FETCH l_tp_cursor INTO v_faseFichero, v_planFichero, v_lineaFichero, v_idReciboDeuda,
					v_codEntidadFichero, v_refPolizaFichero, v_dcFichero, v_tipoRefFichero,
					v_colIdAuxFichero, v_refColectivoFichero, v_xml_codinternocol, v_xml_dcrefcol, v_idAplicacion;
     EXIT WHEN l_tp_cursor%NOTFOUND;

               contador := contador +1;
               DBMS_OUTPUT.PUT_LINE(contador || ' '  || v_idAplicacion);

               v_polizaSinComisiones := PQ_UTIL_VALIDACIONES.checkPolizaSinComs(v_idAplicacion);

               v_lineaSeguroId := PQ_UTIL_VALIDACIONES.getLineaSeguroId(v_lineaFichero, v_planFichero);

               IF v_lineaSeguroId = -1 THEN
                  v_isValidLineaSeguroID := FALSE;
                  v_lineaSeguroId := PQ_UTIL_VALIDACIONES.getLineaSeguroIdGenerica(v_planFichero);
               END IF;


               -- VALIDACIÓN COLECTIVO (INICIO)------------------------------------------------------------
               IF v_colIdAuxFichero IS NOT null THEN
                  --Se comprueba si existe el colectivo
                   v_isValidColectivo := PQ_UTIL_VALIDACIONES.existeRefColectivo(v_refColectivoFichero, v_xml_dcrefcol, v_fechaEmision);

                   IF v_isValidColectivo = FALSE THEN
                        v_sys_codEntidadCol := 0;
                        v_sys_codEntMedCol := 0;
                        v_sys_codESMedCol := 0;
                   ELSE
                       -- Validamos que el colectivo con esa entidad exista
                       v_isValidEntidadColectivo := PQ_UTIL_VALIDACIONES.existeEntidadColectivo(v_refColectivoFichero, v_xml_codinternocol, v_fechaEmision);

                   END IF;


                  --recuperamos la entidad-subentidad del colectivo
                  /** posibles valores de codigoInterno:
                        codigoInterno=3059, codigoInterno=305900 ó codigoInterno=30590000 se traducen como 3059-0
                        codigoInterno=305902 ó codigoInterno=30590002 se traducen como 3059-2.
                        codigoInterno="3059-08" se traduce como 3058-8 En este caso puede venir un guion
                        codigoInterno="3059 BANTIERRA" se traduce como 3190-Bantierra */

                  v_xml_entMedColectivo := SUBSTR(v_xml_codinternocol,1,4);

                  v_aux_codInterno := nvl(SUBSTR(v_xml_codinternocol,5), 0);
                  IF (PQ_UTIL_VALIDACIONES.isNumeric(v_aux_codInterno)) then
                        v_xml_ESMedColectivo := nvl(to_number(SUBSTR(v_xml_codinternocol,5)), 0);
                  ELSE
                      v_xml_ESMedColectivo := SUBSTR(v_xml_codinternocol,5);
                  END IF;

                  v_xml_entidadColectivo := PQ_UTIL_VALIDACIONES.getEntidadByEntMed(v_xml_entMedColectivo);

                  -- ELSE
                  --    v_isValidEntidadColectivo := FALSE;
                  --    v_xml_entidadColectivo := 0;
                  --    v_xml_entMedColectivo := 0;
                  --    v_xml_ESMedColectivo := 0;
                  -- END IF;

                --   IF v_isValidColectivo = TRUE THEN

                   BEGIN
                   select referencia, codentidad,entmediadora, subentmediadora
                   into v_sys_referenciaCol, v_sys_codEntidadCol, v_sys_codEntMedCol, v_sys_codESMedCol
                   from (
                         select c.referencia as referencia, c.codentidad as codentidad,c.entmediadora as entmediadora,
                                  c.subentmediadora as subentmediadora, l.codplan as codplan
                         from tb_historico_colectivos c, tb_lineas l
                         where c.idcolectivo  in
                               (select idcolectivo from tb_polizas p where p.referencia= v_refPolizaFichero)
                         and l.lineaseguroid = c.lineaseguroid
                         and c.fechaefecto<= v_fechaEmision
                         order by c.fechacambio desc)
                   where  rownum=1;

                   EXCEPTION

                          WHEN NO_DATA_FOUND THEN
                                 v_isValidColectivo := FALSE;

                          WHEN too_many_rows THEN
                                 v_isColectivoRepetido := TRUE;

                   END;

                   -- Comprobamos que las referencias de los colectivos son iguales
                   IF v_sys_referenciaCol !=  v_refColectivoFichero THEN
                        v_isCoincideRefCol := false;
                   ElSE
                        v_isCoincideRefCol := true;
                   END IF;

             --  END IF;

			END IF;
			-- VALIDACIÓN COLECTIVO (FIN)------------------------------------------------------------


			IF PQ_UTIL_VALIDACIONES.obtenerNumeroDePolizas(v_refPolizaFichero, v_dcFichero, v_tipoRefFichero) = 0 THEN
			 v_isValidReferenciaPoliza:= FALSE;
			ELSE
			  v_isValidReferenciaPoliza:= TRUE;
        --Calculamos la oficina
        select oficina into oficinaPoliza
            from tb_polizas p
            where p.referencia = v_refPolizaFichero
            and p.dc = v_dcFichero
            and p.tiporef = v_tipoRefFichero
            and rownum = 1;

			END IF;

            v_valiPorcentajes:=PQ_UTIL_VALIDACIONES.checkPorcentajesSubentidad(v_lineaSeguroId => v_lineaSeguroId,
                                                                                  v_codEntidad    => v_sys_codEntMedCol,
                                                                                  v_codSubentidad => v_sys_codESMedCol);

            v_valiComiAsoc:= PQ_UTIL_VALIDACIONES.checkComisionesAsociadas2015(v_refPolizaFichero, v_tipoRefFichero);

            checkIncidencias(v_isValidLineaSeguroID,
                              v_lineaSeguroId,
                              v_isValidColectivo,
                              v_refColectivoFichero,
                              v_isValidEntidadColectivo,
                              v_xml_entidadColectivo,
                              v_xml_entMedColectivo,
                              v_xml_ESMedColectivo,
                              v_sys_codEntidadCol,
                              v_sys_codEntMedCol,
                              v_sys_codESMedCol,
                              v_isColectivoRepetido,
                              v_isCoincideRefCol,
                              v_isValidReferenciaPoliza,
                              v_valiPorcentajes,
                              v_valiComiAsoc,
                              IDFICHERO,
                              v_idReciboDeuda,
                              v_refPolizaFichero,
                              oficinaPoliza,
                              v_faseFichero, v_polizaSinComisiones);

              --Sección de reseteo de variables
              v_isValidColectivo:= TRUE;
              v_isValidEntidadColectivo:= TRUE;
              v_isValidLineaSeguroID:= TRUE;
              v_isColectivoRepetido:= FALSE;
              v_isCoincideRefCol:= TRUE;
              v_sys_codEntidadCol:=null;
              v_sys_codEntMedCol:=null;
              v_sys_codESMedCol:=null;
              oficinaPoliza:=null;
     END LOOP;

     begin
        v_estadoFichero :=  PQ_UTIL_VALIDACIONES.getEstadoFicheroUnificado(IDFICHERO);
    end;
    begin
      UPDATE TB_COMS_UNIF_FICHERO SET ESTADO = v_estadoFichero WHERE ID = IDFICHERO;
    end;

 END  doValidarFicheroDeudaAplazada;
------------------------------------------------------------------------

--------------------checkIncidenciasMultNivelColectivo-----------------
--METODO QUE COMPRUEBA LAS VALIDACIONES REALIZADAS Y REALIZA----
--EL INSERT CORRESPONDIENTE CON UN REGISTRO POR CADA COLECTIVO-----
----------------------------------------------------------------
PROCEDURE checkIncidencias(v_isValidLineaSeguroID IN BOOLEAN,
                              v_lineaSeguroId IN NUMBER,
                              v_isValidColectivo IN BOOLEAN,
                              v_refColectivoFichero IN VARCHAR,
                              v_isValidEntidadColectivo IN BOOLEAN,
                              v_xml_entidadColectivo IN NUMBER,
                              v_xml_entMedColectivo IN NUMBER,
                              v_xml_ESMedColectivo IN VARCHAR,
                              v_sys_codEntidadCol IN NUMBER,
                              v_sys_codEntMedCol IN NUMBER,
                              v_sys_codESMedCol IN NUMBER,
                              v_isColectivoRepetido IN BOOLEAN,
                              v_isCoincideRefCol IN BOOLEAN,
                              v_isValidReferenciaPoliza IN BOOLEAN,
                              v_valiPorcentajes IN NUMBER,
                              v_valiComiAsoc IN NUMBER,
                              IDFICHERO IN NUMBER,
                              v_idReciboDeuda IN NUMBER,
                              v_refPolizaFichero IN VARCHAR2,
                              v_oficinaPoliza IN VARCHAR2,
                              v_faseFichero IN VARCHAR2,
                              v_polizaSinComisiones IN NUMBER)
IS

v_mjsError              VARCHAR2(2000) := '';
v_estado                TB_COMS_UNIF_FICH_INCIDENCIAS.ESTADO%TYPE;
v_tipoIncidencia        NUMBER;
entidadSubentidadXml    TB_COMS_UNIF_FICH_INCIDENCIAS.SUBENTIDAD%TYPE;
entidadSubentidadSys    TB_COMS_UNIF_FICH_INCIDENCIAS.ES_MED_COLECTIVO%TYPE;
v_isValidEntidad        BOOLEAN := TRUE;
v_isValidSubentidad     BOOLEAN := TRUE;

 BEGIN

    --RECUPERAMOS LOS DATOS NECESARIOS PARA EL INSERT
   /*IF v_xml_entidadColectivo <> v_sys_codEntidadCol THEN
      v_isValidEntidad := FALSE;
   END IF;*/
   IF (PQ_UTIL_VALIDACIONES.isNumeric(v_xml_ESMedColectivo)) THEN
     IF (v_xml_entMedColectivo <> v_sys_codEntMedCol) OR  (v_xml_ESMedColectivo <> v_sys_codESMedCol)THEN
        v_isValidEntidad := FALSE;
     END IF;
   ELSE
        v_isValidEntidad := FALSE;
   END IF;

    IF v_isValidColectivo = FALSE OR v_isValidEntidadColectivo = FALSE OR v_isValidLineaSeguroID = FALSE OR
       v_isValidSubentidad = FALSE OR v_isValidEntidad = FALSE OR v_isValidSubentidad = FALSE OR
       v_isColectivoRepetido = TRUE OR v_isCoincideRefCol = FALSE OR v_isValidReferenciaPoliza = FALSE OR
       v_valiPorcentajes=0 OR v_polizaSinComisiones=1 OR v_oficinaPoliza is null THEN

          IF v_polizaSinComisiones=1 THEN
              v_mjsError := v_mjsError || 'Póliza sin distribución de comisiones.|';
          END IF;

      		IF v_isValidReferenciaPoliza = FALSE THEN
      		  v_mjsError :=v_mjsError || 'Error, No existe la referencia de la Poliza.|';
      		END IF;

      		IF v_isCoincideRefCol = FALSE THEN
      		  v_mjsError := v_mjsError || 'El colectivo del fichero no coincide con el colectivo de la poliza.|';
      		END IF;

          IF v_oficinaPoliza is null THEN
      		  v_mjsError :=v_mjsError || 'Error, La póliza no tiene oficina.|';
      		END IF;

          IF v_isValidColectivo = FALSE THEN
             v_mjsError := v_mjsError || 'El Colectivo no existe en la BBDD.|';
          END IF;

          IF v_isValidEntidad = FALSE THEN
             v_mjsError := v_mjsError || 'No corresponden las E-S Med. mostradas con las del colectivo.|';
          END IF;

          IF v_isValidLineaSeguroID = FALSE THEN
             v_mjsError := v_mjsError || 'No existe plan/línea.|';
          END IF;

          IF v_isValidEntidadColectivo = FALSE THEN
             v_mjsError := v_mjsError || 'No se puede obtener la entidad para el colectivo.|';
          END IF;

          /*IF v_isValidSubentidad = FALSE THEN
             v_mjsError := v_mjsError || 'No se puede obtener la subentidad para el colectivo.|';
          END IF;*/

          IF v_valiPorcentajes = 0 THEN
             v_mjsError := v_mjsError || 'Porcentajes para la ES Med no establecidos en el sistema.|';
          END IF;

          IF v_valiComiAsoc = 0 THEN
             v_mjsError := v_mjsError || 'No existen comisiones asociadas.|';
          END IF;

          IF v_isValidColectivo = FALSE OR v_isColectivoRepetido = TRUE
            OR v_isValidReferenciaPoliza = FALSE OR v_polizaSinComisiones=1 THEN
             v_estado := 'E';
             v_tipoIncidencia := 1;
          ELSE
             v_estado := 'A';
             v_tipoIncidencia := 2;
          END IF;

     ELSE
         v_estado := 'C';
         v_tipoIncidencia := 0;
     END IF;

     entidadSubentidadXml := v_xml_entMedColectivo ||'-'||v_xml_ESMedColectivo;
     entidadSubentidadSys := v_sys_codEntMedCol ||'-'|| v_sys_codESMedCol;

     -- Insertamos la incidencia
     PQ_UTIL_VALIDACIONES.insertIncidenciaUnif(IDFICHERO, v_lineaseguroId, v_refColectivoFichero, entidadSubentidadXml, v_refPolizaFichero, v_oficinaPoliza,
                       v_estado, v_mjsError, v_idReciboDeuda, v_tipoIncidencia, entidadSubentidadSys, v_faseFichero);

END checkIncidencias;


end PQ_VALIDAR_DEUDA_APLAZADA;
/
SHOW ERRORS;