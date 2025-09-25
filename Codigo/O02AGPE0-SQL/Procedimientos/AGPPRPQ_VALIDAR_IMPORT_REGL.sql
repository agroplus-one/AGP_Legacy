SET DEFINE OFF;
SET SERVEROUTPUT ON;
create or replace package o02agpe0.PQ_VALIDAR_IMPORT_REGL is

-- Author  : U029769
-- Created : 25/08/2012 8:09:41
-- Purpose : 
PROCEDURE doValidarFicherosReglamentos(IDFICHERO IN NUMBER);

PROCEDURE validaDatosPolizas(v_existeColectivo IN NUMBER,v_isValidColectivo IN NUMBER,v_iscolectivoRepetido IN NUMBER,
                               v_colref IN VARCHAR2,v_refpoliza IN VARCHAR2, v_dc IN NUMBER , v_tipoRef IN VARCHAR2,
                               codEntidadCol IN NUMBER,entMediadoraCol IN NUMBER, subEntMediadoraCol IN NUMBER, codPlan IN NUMBER,
                               v_linea IN NUMBER,idFichero IN NUMBER,v_idreciboReglamento IN NUMBER,v_planFichero IN NUMBER,
                               v_codEntidad IN VARCHAR2, v_oficina IN VARCHAR2,v_coincideRefCol IN boolean);

PROCEDURE  checkIncidencias(v_existeColectivo IN NUMBER,v_isValidColectivo IN NUMBER,v_iscolectivoRepetido IN NUMBER,
                             v_isValidMediadorGGE IN NUMBER,v_isValidMediadorMtoCultivos IN NUMBER,v_isValidMediadorReglamento IN NUMBER,
                             v_isValidReferenciaPoliza IN NUMBER,idFichero IN NUMBER,v_lineaseguroId IN NUMBER,v_colref IN VARCHAR2,
                             entMediadoraCol IN NUMBER, subEntMediadoraCol IN NUMBER,v_refpoliza IN VARCHAR2,
                             v_idreciboReglamento IN NUMBER,entidad IN NUMBER,v_oficina IN VARCHAR2,v_coincideRefCol IN boolean);

-- ¿Eliminar y referenciar a PQ_UTIL_VALIDACIONES?
FUNCTION checkMediadorMtoCultivos(entMediadoraCol IN NUMBER, subEntMediadoraCol IN NUMBER, v_lineaseguroId IN NUMBER,lineaseguroIdGenerico IN NUMBER) RETURN NUMBER;

end PQ_VALIDAR_IMPORT_REGL;
/
create or replace package body o02agpe0.PQ_VALIDAR_IMPORT_REGL is

PROCEDURE doValidarFicherosReglamentos (IDFICHERO IN NUMBER) IS

TYPE TpCursor                         IS REF CURSOR;
 l_tp_cursor                           TpCursor;
 l_sql                                 VARCHAR2(2000);


 v_existeColectivo                     NUMBER := 0;
 v_isValidColectivo                    NUMBER := 1;
 v_iscolectivoRepetido                 NUMBER := 1;

 v_planFichero                         NUMBER := 0;
 v_colref                              VARCHAR2(7);
 v_idreciboReglamento                    NUMBER := 0;
 v_linea                               NUMBER := 0;
 v_refpoliza                           varchar2(7);
 v_dc                                  NUMBER := 0;
 v_tipoRef                             VARCHAR2(1);
 v_codEntidad                          varchar2(4);
 v_oficina                             varchar2(4);
 referenciaCol                         VARCHAR2(7);

 codEntidadCol                         NUMBER := 0;
 entMediadoraCol                       NUMBER := 0;
 subEntMediadoraCol                    NUMBER := 0;
 codPlan                               NUMBER := 0;
 v_fechaEmision                        DATE;
 v_coincideRefCol                      boolean;
BEGIN

      -- Obtenemos el plan del fichero
      v_planFichero := PQ_UTIL_VALIDACIONES.getPlanFichero(IDFICHERO);
       -- Obtenemos la fecha de emision del recibo
      v_fechaEmision := PQ_UTIL_VALIDACIONES.getFechaEmision (IDFICHERO);

      l_sql :='select rc.id,rc.col_referencia,rc.linea,SUBSTR(rc.codigointerno,2,3) as codentidad ,
                      rc.referencia,SUBSTR(rc.codigointerno,5,4) as oficina,rc.dc,rc.tiporeferencia
               from o02agpe0.TB_COMS_REGLAMENTO_PROD_EMIT rc
               where rc.idfichero='||IDFICHERO ;

      OPEN l_tp_cursor FOR l_sql;
           LOOP FETCH l_tp_cursor INTO v_idreciboReglamento,v_colref,v_linea,v_codEntidad, v_refpoliza ,v_oficina,v_dc,v_tipoRef;
                EXIT WHEN l_tp_cursor%NOTFOUND;

           -- validamos el colectivo
            v_existeColectivo := PQ_UTIL_VALIDACIONES.checkExisteColectivo(v_colref);

            --inicializamos variables
           codEntidadCol:=0;
           entMediadoraCol:=0;
           subEntMediadoraCol:=0;
           codPlan:=0;

           BEGIN
             select referencia, codentidad,entmediadora, subentmediadora, codplan
             into referenciaCol, codEntidadCol,entMediadoraCol, subEntMediadoraCol, codPlan
             from (
                    select c.referencia as referencia, c.codentidad as codentidad,c.entmediadora as entmediadora,
                                  c.subentmediadora as subentmediadora, l.codplan as codplan
                    from tb_historico_colectivos c, tb_lineas l
                    where c.idcolectivo  in
                            (select idcolectivo from tb_polizas p where p.referencia= v_refpoliza)
                    and l.lineaseguroid = c.lineaseguroid
                    and c.fechaefecto<= v_fechaEmision
                    ---and (c.fechabaja is null or c.fechabaja >= v_fechaEmision)
                    order by c.fechacambio desc)
              where  rownum=1;

            EXCEPTION

                  WHEN NO_DATA_FOUND THEN
                     v_isValidColectivo := 0;
                     subEntMediadoraCol:=0;

                  WHEN too_many_rows THEN
                     v_iscolectivoRepetido := 0;

            END;
            -- Comprobamos que las referencias de los colectivos son iguales
                   IF referenciaCol !=  v_colref THEN
                      v_coincideRefCol := false;
                   ElSE
                      v_coincideRefCol := true;
                   END IF;

             -- Por cada poliza hacemos las validaciones e insertamos incidencias
             validaDatosPolizas (v_existeColectivo,v_isValidColectivo,v_iscolectivoRepetido,
                                   v_colref,v_refpoliza,v_dc,v_tipoRef,codEntidadCol,
                                   entMediadoraCol,subEntMediadoraCol,codPlan,v_linea,IDFICHERO,
                                   v_idreciboReglamento,v_planFichero,v_codEntidad,
                                   v_oficina,v_coincideRefCol);
             --reiniciamos variables
             v_isValidColectivo := 1;
             v_iscolectivoRepetido := 1;

         END LOOP;

END  doValidarFicherosReglamentos;

PROCEDURE validaDatosPolizas (v_existeColectivo IN NUMBER,v_isValidColectivo IN NUMBER,v_iscolectivoRepetido IN NUMBER,
                               v_colref IN VARCHAR2,v_refpoliza IN VARCHAR2, v_dc IN NUMBER , v_tipoRef IN VARCHAR2,
                               codEntidadCol IN NUMBER,entMediadoraCol IN NUMBER, subEntMediadoraCol IN NUMBER, codPlan IN NUMBER,
                               v_linea IN NUMBER,idFichero IN NUMBER,v_idreciboReglamento IN NUMBER,v_planFichero IN NUMBER,
                               v_codEntidad IN VARCHAR2, v_oficina IN VARCHAR2,v_coincideRefCol IN boolean) IS

 v_lineaseguroId                       NUMBER := 0;
 v_isValidMediadorGGE                  NUMBER := 0;
 v_isValidMediadorMtoCultivos          NUMBER := 0;
 v_isValidMediadorReglamento           NUMBER := 0;
 v_isValidReferenciaPoliza             NUMBER := 0;
 lineaseguroIdGenerico                 NUMBER := 0;
 entidad                               NUMBER := 0;

BEGIN

     -- obtenemos la lineaseguroid

     v_lineaseguroId := PQ_UTIL_VALIDACIONES.getLineaSeguroId(v_linea,v_planFichero);
     IF v_lineaseguroId = -1 THEN
       v_lineaseguroId := PQ_UTIL_VALIDACIONES.getLineaSeguroIdGenerica(v_planFichero);
     END IF;

     entidad := '3' || v_codEntidad;

     IF  v_isValidColectivo = 1 AND v_existeColectivo > 0  THEN --correcto
         BEGIN
              execute immediate 'select lin.lineaseguroid from tb_lineas lin  where lin.codplan = ' || codPlan || ' and lin.codlinea = 999' into lineaseguroIdGenerico;
              EXCEPTION
                       WHEN NO_DATA_FOUND THEN
                            lineaseguroIdGenerico := 0;

          END;
         IF entidad BETWEEN 3000 AND 3999 AND subEntMediadoraCol = 0  THEN
           v_isValidMediadorGGE:=1;
           v_isValidMediadorMtoCultivos:=1;
           v_isValidMediadorReglamento:=1;

         ELSE
           v_isValidMediadorGGE := PQ_UTIL_VALIDACIONES.checkMediadorGGE(entMediadoraCol, subEntMediadoraCol, codPlan);
           v_isValidMediadorMtoCultivos := checkMediadorMtoCultivos(entMediadoraCol, subEntMediadoraCol, v_lineaseguroId,lineaseguroIdGenerico);
           v_isValidMediadorReglamento := PQ_UTIL_VALIDACIONES.checkMediadorReglamento(codEntidadCol);

         END IF;
      ELSE
          v_isValidMediadorGGE:=1;
          v_isValidMediadorMtoCultivos:=1;
          v_isValidMediadorReglamento:=1;
      END IF;

     v_isValidReferenciaPoliza :=  PQ_UTIL_VALIDACIONES.checkRefPoliza(v_refpoliza, v_dc, v_tipoRef);

     -- Chequeamos las validaciones
     checkIncidencias (v_existeColectivo,v_isValidColectivo,v_iscolectivoRepetido,v_isValidMediadorGGE,v_isValidMediadorMtoCultivos,
                       v_isValidMediadorReglamento,v_isValidReferenciaPoliza,idFichero,v_lineaseguroId,v_colref,
                       entMediadoraCol,subEntMediadoraCol,v_refpoliza,v_idreciboReglamento,entidad,v_oficina,v_coincideRefCol);

END   validaDatosPolizas;

PROCEDURE  checkIncidencias (v_existeColectivo IN NUMBER,v_isValidColectivo IN NUMBER,v_iscolectivoRepetido IN NUMBER,
                             v_isValidMediadorGGE IN NUMBER,v_isValidMediadorMtoCultivos IN NUMBER,v_isValidMediadorReglamento IN NUMBER,
                             v_isValidReferenciaPoliza IN NUMBER,idFichero IN NUMBER,v_lineaseguroId IN NUMBER,v_colref IN VARCHAR2,
                             entMediadoraCol IN NUMBER, subEntMediadoraCol IN NUMBER,v_refpoliza IN VARCHAR2,
                             v_idreciboReglamento IN NUMBER,entidad IN NUMBER,v_oficina IN VARCHAR2,v_coincideRefCol IN boolean) IS


v_mjsError                            VARCHAR2(2000) := '';
v_estado                              VARCHAR2(20);
v_tipoIncidencia                      NUMBER;
entidadSubentidad                     varchar2(8);
entSubColectivo                       VARCHAR2(10);
 BEGIN

      IF v_isValidColectivo = 0 OR  v_isValidMediadorMtoCultivos = 0 OR v_isValidMediadorGGE = 0
      OR v_isValidMediadorReglamento = 0 OR v_isValidReferenciaPoliza = 0  OR v_iscolectivoRepetido = 0
      OR v_existeColectivo = 0 OR  v_coincideRefCol = FALSE THEN

          IF v_existeColectivo = 0 THEN
             v_mjsError := v_mjsError || 'El colectivo no existe en BBDD.|';

          ELSIF v_isValidColectivo = 0 THEN
             v_mjsError := v_mjsError || 'No corresponden las E-S Med. mostradas con las del colectivo.|';

          ELSIF v_iscolectivoRepetido = 0 THEN
             v_mjsError := v_mjsError || 'El Colectivo esta duplicado en la BBDD.|';

          END IF;


          IF v_isValidReferenciaPoliza = 0 THEN
              v_mjsError :=v_mjsError || 'Error, No existe la referencia de la Poliza.|';
          END IF;

          IF v_isValidMediadorGGE = 0 THEN
               v_mjsError := v_mjsError || 'Cada Póliza tiene que tener asignado su mediador GGE.|';
          END IF;

          IF v_isValidMediadorMtoCultivos = 0 THEN
               v_mjsError := v_mjsError || 'Cada Póliza tiene que tener asignado su mediador Mto Cultivos.|';
          END IF;

          IF v_isValidMediadorReglamento = 0 THEN
               v_mjsError := v_mjsError || 'Cada Póliza tiene que tener asignado su mediador Reglamento.|';
          END IF;
          IF v_coincideRefCol = FALSE THEN
               v_mjsError := v_mjsError || 'El colectivo del fichero no coincide con el colectivo de la poliza';
          END IF;

        IF v_isValidReferenciaPoliza = 0 OR v_iscolectivoRepetido = 0 THEN
            v_estado := 'Erroneo';
            v_tipoIncidencia := 1;
        ELSE
            v_estado := 'Aviso';
            v_tipoIncidencia := 2;
        END IF;

     ELSE
         v_estado := 'Correcto';
         v_tipoIncidencia := 0;

     END IF;

     entidadSubentidad :=  entidad ||'-'||subEntMediadoraCol;
     entSubColectivo := entMediadoraCol ||'-'|| subEntMediadoraCol;


     -- Insertamos la incidencia
     PQ_UTIL_VALIDACIONES.insertIncidencia (idFichero,v_lineaseguroId,v_colref,entidadSubentidad,v_refpoliza,v_oficina,
                       v_estado,v_mjsError,v_idreciboReglamento,v_tipoIncidencia,entSubColectivo);

END    checkIncidencias;

---------------------checkMediadorMtoCultivos-------------------
--METODO QUE COMPRUEBA SI EXISTE LA ENTIDAD Y SUBENTIDAD MEDIADORAS
--EN LA TABLA TB_COMS_CULTIVOS_SUBENTIDADES
FUNCTION checkMediadorMtoCultivos(entMediadoraCol IN NUMBER, subEntMediadoraCol IN NUMBER, v_lineaseguroId IN NUMBER,lineaseguroIdGenerico IN NUMBER) RETURN NUMBER
IS
  v_isValid               NUMBER := 0;

BEGIN

      BEGIN
       select count(*)  into v_isValid
       from tb_coms_cultivos_subentidades
       where codentidad  = entMediadoraCol
       and codsubentidad = subEntMediadoraCol
       and lineaseguroid = v_lineaseguroId;

       EXCEPTION
             WHEN NO_DATA_FOUND THEN
                     v_isValid := 0;
      END;
       IF v_isValid = 0 THEN
               BEGIN
                 select count(*)  into v_isValid
                 from tb_coms_cultivos_subentidades
                 where codentidad  = entMediadoraCol
                 and codsubentidad = subEntMediadoraCol
                 and lineaseguroid = lineaseguroIdGenerico;

              EXCEPTION
                   WHEN NO_DATA_FOUND THEN
                           v_isValid := 0;
              END;

            END IF;

     RETURN v_isValid;

END checkMediadorMtoCultivos;

end PQ_VALIDAR_IMPORT_REGL;
/
SHOW ERRORS;