SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_VALIDAR_IMPAGADOS is

--MAIN
PROCEDURE doValidarFicherosImpagados (IDFICHERO IN NUMBER);

PROCEDURE  checkIncidenciasRefPoliza(v_isValidReferenciaPoliza IN BOOLEAN, v_lineaSeguroId IN NUMBER, v_individualReferencia IN VARCHAR, v_dcPol IN VARCHAR,
                                      v_codInternoPolizaIndividual IN VARCHAR, v_isValidLineaSeguroID IN BOOLEAN, v_idImpagados IN NUMBER, IDFICHERO IN NUMBER,v_oficinaPoliza IN VARCHAR2);
PROCEDURE  checkIncidenciasRefColectivo(v_isValidColectivo IN BOOLEAN, v_isValidEntidadColectivo IN BOOLEAN,v_isValidLineaSeguroID IN BOOLEAN,
                                         v_lineaSeguroId IN NUMBER,v_colectivoReferencia IN VARCHAR, v_colectivoCodInterno IN VARCHAR,
                                         v_idImpagados IN NUMBER, IDFICHERO IN NUMBER,entSubColectivo IN VARCHAR2, v_fechaEmision IN DATE);

end PQ_VALIDAR_IMPAGADOS;
/
create or replace package body o02agpe0.PQ_VALIDAR_IMPAGADOS is

----------------------------------------------------------------
----------------- doValidarFicherosReglamentos-------------------
--METODO PRINCIPAL QUE VALIDA UN FICHERO DE COMISIONES POR-------
--COBRO DE RECIBOS DE IMPAGADOS---------------------------------
----------------------------------------------------------------
PROCEDURE doValidarFicherosImpagados (IDFICHERO IN NUMBER) IS

 TYPE TpCursor                         IS REF CURSOR;
 l_tp_cursor                           TpCursor;
 l_sql                                 VARCHAR2(2000);
 -- campos tabla
 v_idImpagados                         NUMBER := 0;
 v_individualReferencia                VARCHAR2(7);
 v_individualdc                        VARCHAR2(2);
 v_individualcodinterno                VARCHAR2(12);
 v_colectivoReferencia                 VARCHAR2(7);
 v_colectivoDC                         VARCHAR2(2);
 v_colectivoCodInterno                 VARCHAR(12);
 v_linea                               NUMBER;
 v_plan                                NUMBER := 0;
 v_fechaEmision                        DATE;

 --validaciones
 v_isValidReferenciaPoliza             BOOLEAN := TRUE;
 v_isValidColectivo                    BOOLEAN := TRUE;
 v_isValidEntidadColectivo             BOOLEAN := TRUE;
 v_isValidLineaSeguroID                BOOLEAN := TRUE;

 -- otras variables
 v_lineaSeguroId                       NUMBER := 0;
 entSubColectivo                       VARCHAR2(10);
 v_entidadColectivo                    VARCHAR2(4);
 v_oficinaPoliza              	 TB_POLIZAS.OFICINA%TYPE;
BEGIN
     pq_utl.LOG ('INICIO doValidarFicherosImpagados', 2);
     -- Obtenemos la fecha de emision del recibo
      v_fechaEmision := PQ_UTIL_VALIDACIONES.getFechaEmision (IDFICHERO);

     l_sql := 'select ri.id,ri.individualreferencia,ri.individualdc,ri.individualcodinterno,
                      ri.colectivoreferencia,ri.colectivodc,ri.colectivocodinterno,
                      ri.linea,ri.plan
               from tb_coms_recibos_impagados ri where ri.idfichero =' || IDFICHERO;

     OPEN l_tp_cursor FOR l_sql;
     LOOP FETCH l_tp_cursor INTO v_idImpagados, v_individualReferencia,v_individualdc,v_individualcodinterno,
                                 v_colectivoReferencia,v_colectivoDC,v_colectivoCodInterno,
                                 v_linea,v_plan;
          EXIT WHEN l_tp_cursor%NOTFOUND;

               pq_utl.LOG ('Recuperamos lineaSeguroID', 2);
               v_lineaSeguroId := PQ_UTIL_VALIDACIONES.getLineaSeguroId(v_linea,v_plan);

               IF v_lineaSeguroId = -1 THEN
                  v_isValidLineaSeguroID := FALSE;
                  v_lineaSeguroId := PQ_UTIL_VALIDACIONES.getLineaSeguroIdGenerica(v_plan);
               END IF;

               IF v_colectivoReferencia IS NULL THEN
                   pq_utl.LOG ('Fichero con referencia de poliza', 2);

                   pq_utl.LOG ('Inicio Validaciones ', 2);
                   v_isValidReferenciaPoliza :=  PQ_UTIL_VALIDACIONES.existeRefPoliza(v_individualReferencia, v_individualdc);

                   IF v_isValidReferenciaPoliza then
                         pq_utl.LOG ('Referencia valida. Calculamos oficina ', 2);
                         --Calculamos la oficina
                          select oficina into v_oficinaPoliza
                          from tb_polizas p
                          where p.referencia = v_individualReferencia
                          and p.dc = v_individualdc
                          and rownum = 1;
                    END IF;

                   pq_utl.LOG ('Inseramos incidencias', 2);
                   checkIncidenciasRefPoliza (v_isValidReferenciaPoliza,v_lineaSeguroId,
                                              v_individualReferencia, v_individualdc,v_individualcodinterno,
                                              v_isValidLineaSeguroID,v_idImpagados,IDFICHERO,v_oficinaPoliza);
                   v_oficinaPoliza:= null;

               ELSE
                   pq_utl.LOG ('Fichero con referencia de colectivo', 2);

                   pq_utl.LOG ('Inicio Validaciones ', 2);

                   v_isValidColectivo := PQ_UTIL_VALIDACIONES.existeRefColectivo(v_colectivoReferencia, v_colectivoDC, v_fechaEmision);

                   -- Si el idcolectivo existe, comprobamos que la entidad de ese colectivo sea correcta
                   -- Si no existe  el colectivo tampoco sera correcta por lo que la ponemos a false
                   IF v_isValidColectivo = TRUE THEN
                      v_isValidEntidadColectivo := PQ_UTIL_VALIDACIONES.existeEntidadColectivo(v_colectivoReferencia, v_colectivoCodInterno, v_fechaEmision);
                      --recuperamos la entidad-subentidad del colectivo
                      v_entidadColectivo := nvl(to_number(SUBSTR(v_colectivoCodInterno, 0, 4)), 0);
                      entSubColectivo := PQ_UTIL_VALIDACIONES.getESMedColectivo(v_colectivoReferencia, v_lineaSeguroId, v_fechaEmision);
                   ELSE
                      v_isValidEntidadColectivo := FALSE;
                      v_entidadColectivo := 0;
                      entSubColectivo := 0 || '-' || 0;
                   END IF;

                   pq_utl.LOG ('Inseramos incidencias', 2);
                   checkIncidenciasRefColectivo (v_isValidColectivo,v_isValidEntidadColectivo,v_isValidLineaSeguroID,
                                                 v_lineaSeguroId,v_colectivoReferencia, v_colectivoCodInterno,
                                                 v_idImpagados, IDFICHERO,entSubColectivo, v_fechaEmision);
                 --Reiniciamos el valor de la entidad-subentidad del colectivo
                 entSubColectivo := 0 || '-' || 0;

               END IF;

              v_isValidLineaSeguroID := TRUE;

     END LOOP;

     pq_utl.LOG ('FIN doValidarFicherosImpagados', 2);

END doValidarFicherosImpagados;

----------------------------------------------------------------
--------------------checkIncidenciasRefPoliza-----------------
--METODO QUE COMPRUEBA LAS VALIDACIONES REALIZADAS Y REALIZA----
--EL INSERT CORRESPONDIENTE CON UN REGISTRO POR CADA POLIZA-----
----------------------------------------------------------------
PROCEDURE  checkIncidenciasRefPoliza (v_isValidReferenciaPoliza IN BOOLEAN,
                                     v_lineaSeguroId IN NUMBER, v_individualReferencia IN VARCHAR,
                                     v_dcPol IN VARCHAR,
                                     v_codInternoPolizaIndividual IN VARCHAR,
                                     v_isValidLineaSeguroID IN BOOLEAN,
                                     v_idImpagados IN NUMBER, IDFICHERO IN NUMBER, v_oficinaPoliza IN VARCHAR2)
IS

 TYPE TpCursor                         IS REF CURSOR;
 l_tp_cursor                           TpCursor;
 l_sql                                 VARCHAR2(2000);
 v_mjsError                            VARCHAR2(2000) := '';
 v_estado                              VARCHAR2(20);
 v_tipoIncidencia                      NUMBER;
 v_idpoliza                            NUMBER := 0;
 v_entidadPoliza                       NUMBER;

 BEGIN


      IF v_isValidReferenciaPoliza = FALSE OR v_isValidLineaSeguroID = FALSE OR v_oficinaPoliza is null THEN

        --RECUPERAMOS LOS DATOS NECESARIOS PARA EL INSERT

        --v_subent := getSubEntidad(v_codInternoPolizaIndividual);
        v_entidadPoliza := SUBSTR(v_codInternoPolizaIndividual, 0, 4);

        IF v_isValidReferenciaPoliza = FALSE THEN
           v_mjsError := v_mjsError || 'La Póliza no existe en la BBDD.|';
        END IF;

        IF v_isValidLineaSeguroID = FALSE THEN
           v_mjsError := v_mjsError || 'No existe plan/línea.|';
        END IF;
        IF v_oficinaPoliza is null THEN
      		  v_mjsError :=v_mjsError || 'Error, La póliza no tiene oficina.|';
      	END IF;
        IF v_isValidReferenciaPoliza != FALSE THEN
          v_estado := 'Aviso';
          v_tipoIncidencia := 2;
        ELSE
          v_estado := 'Erroneo';
          v_tipoIncidencia := 1;
        END IF;

     ELSE
         v_estado := 'Correcto';
         v_tipoIncidencia := 0;
     END IF;
     -- BUCLE PARA INSERTAR UNA LINEA POR CADA IDPOLIZA
           l_sql := 'select p.idpoliza from tb_polizas p where p.referencia = ''' || v_individualReferencia || ''' and p.dc = ''' || v_dcPol || '''';
         OPEN l_tp_cursor FOR l_sql;
         LOOP FETCH l_tp_cursor INTO v_idpoliza;
              EXIT WHEN l_tp_cursor%NOTFOUND;
              PQ_UTIL_VALIDACIONES.insertIncidencia(IDFICHERO,v_lineaSeguroId,'',v_entidadPoliza,v_idpoliza,'',v_estado,v_mjsError,v_idImpagados,v_tipoIncidencia,'');
         END LOOP;

END checkIncidenciasRefPoliza;

----------------------------------------------------------------
--------------------checkIncidenciasNivelColectivo-----------------
--METODO QUE COMPRUEBA LAS VALIDACIONES REALIZADAS Y REALIZA----
--EL INSERT CORRESPONDIENTE CON UN REGISTRO POR CADA COLECTIVO-----
----------------------------------------------------------------
PROCEDURE  checkIncidenciasRefColectivo (v_isValidColectivo IN BOOLEAN, v_isValidEntidadColectivo IN BOOLEAN,v_isValidLineaSeguroID IN BOOLEAN,
                                         v_lineaSeguroId IN NUMBER,v_colectivoReferencia IN VARCHAR, v_colectivoCodInterno IN VARCHAR,
                                         v_idImpagados IN NUMBER, IDFICHERO IN NUMBER,entSubColectivo IN VARCHAR2, v_fechaEmision IN DATE)
IS

 v_mjsError                            VARCHAR2(2000) := '';
 v_estado                              VARCHAR2(20);
 v_tipoIncidencia                      NUMBER;
 v_idColectivo                         VARCHAR2(7);
 v_subentidad                          NUMBER(4);
 entidadSubentidad                     VARCHAR2(8);
 v_isValidSubentidad                   BOOLEAN := TRUE;
 v_entidadColectivo                    VARCHAR2(4);

 BEGIN
    --RECUPERAMOS LOS DATOS NECESARIOS PARA EL INSERT
    IF v_colectivoReferencia IS NULL THEN
       v_idColectivo := '';
    ELSE
       v_idColectivo := v_colectivoReferencia;
    END IF;

    --obtenemos la entidad
    v_entidadColectivo := SUBSTR(v_colectivoCodInterno, 0, 4);
    --obtenemos la subentidad
    v_subentidad := PQ_UTIL_VALIDACIONES.getSubentidadMedColectivo(v_colectivoReferencia, v_lineaSeguroId, v_fechaEmision);

    IF v_subentidad IS NULL THEN
        entidadSubentidad := v_entidadColectivo;
        v_isValidSubentidad := FALSE;
    ELSE
        entidadSubentidad := v_entidadColectivo || '-' || v_subentidad;
    END IF;

    IF v_isValidColectivo = FALSE OR v_isValidEntidadColectivo = FALSE OR v_isValidLineaSeguroID = FALSE OR v_isValidSubentidad = FALSE THEN

        IF v_isValidColectivo = FALSE THEN
           v_mjsError := v_mjsError || 'El Colectivo no existe en la BBDD.|';
        END IF;

        IF v_isValidEntidadColectivo = FALSE THEN
           v_mjsError := v_mjsError || 'No corresponden las E-S Med. mostradas con las del colectivo.|';
        END IF;

        IF v_isValidLineaSeguroID = FALSE THEN
           v_mjsError := v_mjsError || 'No existe plan/línea.|';
        END IF;

        IF v_isValidSubentidad = FALSE THEN
           v_mjsError := v_mjsError || 'No se puede obtener la subentidad para el colectivo|';
        END IF;


        IF v_isValidColectivo != FALSE THEN
           v_estado := 'Aviso';
           v_tipoIncidencia := 2;
        ELSE
           v_estado := 'Erroneo';
           v_tipoIncidencia := 1;
        END IF;

     ELSE
         v_estado := 'Correcto';
         v_tipoIncidencia := 0;
     END IF;

          PQ_UTIL_VALIDACIONES.insertIncidencia(IDFICHERO,v_lineaSeguroId,v_idColectivo,entidadSubentidad,'','',v_estado,v_mjsError,
                            v_idImpagados,v_tipoIncidencia,entSubColectivo);

END checkIncidenciasRefColectivo;

end PQ_VALIDAR_IMPAGADOS;
/
SHOW ERRORS;
