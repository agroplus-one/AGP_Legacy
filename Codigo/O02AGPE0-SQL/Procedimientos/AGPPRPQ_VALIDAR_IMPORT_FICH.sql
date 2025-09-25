SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_VALIDAR_IMPORT_FICH is

-- Author  : U029769
-- Created : 24/08/2012 14:19:47
-- Purpose : 
PROCEDURE doValidarFicherosComisiones(IDFICHERO IN NUMBER);

PROCEDURE validaDatosPolizas(v_existeColectivo IN NUMBER,v_isValidColectivo IN NUMBER,v_iscolectivoRepetido IN NUMBER,
                               v_colref IN VARCHAR2,v_refpoliza IN VARCHAR2, v_dc IN NUMBER , v_tipoRef IN VARCHAR2,
                               codEntidadCol IN NUMBER,entMediadoraCol IN NUMBER, subEntMediadoraCol IN NUMBER, codPlan IN NUMBER,
                               v_linea IN NUMBER,idFichero IN NUMBER,v_idreciboComision IN NUMBER,v_planFichero IN NUMBER,
                               v_codEntidad IN VARCHAR2,v_dtGastosPagados IN NUMBER,v_oficina IN VARCHAR2,v_subEntMedFichero IN NUMBER,
                               v_coincideRefCol IN boolean,v_entMed IN VARCHAR2);

PROCEDURE checkIncidencias(v_existeColectivo IN NUMBER,v_isValidColectivo IN NUMBER,v_iscolectivoRepetido IN NUMBER,
                             v_isValidMediadorGGE IN NUMBER,v_isValidMediadorMtoCultivos IN NUMBER,v_isValidMediadorReglamento IN NUMBER,
                             v_isValidReferenciaPoliza IN NUMBER,idFichero IN NUMBER,v_lineaseguroId IN NUMBER,v_colref IN VARCHAR2,
                             entMediadoraCol IN NUMBER, subEntMediadoraCol IN NUMBER,v_refpoliza IN VARCHAR2,
                             v_idreciboComision IN NUMBER,v_isValidPctGlobalEntidad IN NUMBER,v_isvalidESMed IN NUMBER,
                             entidad IN NUMBER,v_oficina IN VARCHAR2,v_subEntMedFichero IN NUMBER,v_coincideRefCol IN boolean,v_entMed IN VARCHAR2,
                             v_codPlan IN NUMBER);

end PQ_VALIDAR_IMPORT_FICH;
/
create or replace package body o02agpe0.PQ_VALIDAR_IMPORT_FICH is

PROCEDURE doValidarFicherosComisiones (IDFICHERO IN NUMBER) IS

 TYPE TpCursor                         IS REF CURSOR;
 l_tp_cursor                           TpCursor;
 l_sql                                 VARCHAR2(2000);
 l_tp_cursor2                          TpCursor;
 l_sql2                                VARCHAR2(2000);

 v_existeColectivo                     NUMBER := 0;
 v_isValidColectivo                    NUMBER := 1;
 v_iscolectivoRepetido                 NUMBER := 1;

 v_planFichero                         NUMBER := 0;
 v_colref                              VARCHAR2(7);
 v_idreciboComision                    NUMBER := 0;
 v_linea                               NUMBER := 0;
 v_refpoliza                           varchar2(7);
 v_dc                                  NUMBER := 0;
 v_tipoRef                             VARCHAR2(1);
 v_codEntidad                          varchar2(4);
 v_entMed                              NUMBER := 0;
 v_subEntMedFichero                    NUMBER := 0;
 v_oficina                             varchar2(4);
 v_dtGastosPagados                     NUMBER := 0;
 referenciaCol                         VARCHAR2(7);
 v_fechaEmision                        DATE;

 codEntidadCol                         NUMBER := 0;
 entMediadoraCol                       NUMBER := 0;
 subEntMediadoraCol                    NUMBER := 0;
 codPlan                               NUMBER := 0;
 v_coincideRefCol                      boolean;
BEGIN

      --- Obtenemos el plan del fichero
      v_planFichero := PQ_UTIL_VALIDACIONES.getPlanFichero(IDFICHERO);
      -- Obtenemos la fecha de emision del recibo
      v_fechaEmision := PQ_UTIL_VALIDACIONES.getFechaEmision (IDFICHERO);

      l_sql :='select rc.id,rc.colectivoreferencia,rc.linea,SUBSTR(rc.colectivocodinterno,2,3) as codentidad,
                      SUBSTR(rc.colectivocodinterno,1,4)as entMed,
                      nvl(trim(SUBSTR(rc.colectivocodinterno,5)), 0) as subEntMed
               from tb_coms_comisiones rc
               where idfichero='||IDFICHERO ;

      OPEN l_tp_cursor FOR l_sql;
           LOOP FETCH l_tp_cursor INTO v_idreciboComision, v_colref,v_linea,v_codEntidad,v_entMed,v_subEntMedFichero;
                EXIT WHEN l_tp_cursor%NOTFOUND;

           -- validamos el colectivo
            v_existeColectivo := PQ_UTIL_VALIDACIONES.checkExisteColectivo(v_colref);

           l_sql2 := 'select ap.referencia,SUBSTR(ap.codinterno,5,4) as oficina, ap.DC,
                             ap.tiporeferencia,ap.DT_GASTOSPAGADOS
                      from o02agpe0.tb_coms_comis_aplicaciones ap
                      where ap.idcomisiones ='||v_idreciboComision;

           OPEN l_tp_cursor2 FOR l_sql2;
                LOOP FETCH l_tp_cursor2 INTO v_refpoliza, v_oficina,v_dc, v_tipoRef,v_dtGastosPagados;
                     EXIT WHEN l_tp_cursor2%NOTFOUND;

                   --inicializamos variables
                   codEntidadCol:=0;
                   entMediadoraCol:=0;
                   subEntMediadoraCol:=0;
                   codPlan:=0;

                    BEGIN
                     select referencia, codentidad,entmediadora, subentmediadora, codplan
                       into referenciaCol, codEntidadCol,entMediadoraCol, subEntMediadoraCol, codPlan
                     from(
                           select c.referencia as referencia, c.codentidad as codentidad,c.entmediadora as entmediadora,
                                  c.subentmediadora as subentmediadora, l.codplan as codplan
                            from tb_historico_colectivos c, tb_lineas l
                            where c.idcolectivo in
                                 (select idcolectivo from tb_polizas p where p.referencia= v_refpoliza)
                                 and l.lineaseguroid = c.lineaseguroid
                                 and c.fechaefecto<= v_fechaEmision
                                 --and (c.fechabaja is null or c.fechabaja >= v_fechaEmision)
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
                                         v_idreciboComision,v_planFichero,v_codEntidad,
                                         v_dtGastosPagados,v_oficina,v_subEntMedFichero,v_coincideRefCol,v_entMed);
                   --reiniciamos variables
                   v_isValidColectivo := 1;
                   v_iscolectivoRepetido := 1;
                   v_oficina := null;
                 END LOOP;
              END LOOP;
END doValidarFicherosComisiones;



PROCEDURE validaDatosPolizas (v_existeColectivo IN NUMBER,v_isValidColectivo IN NUMBER,v_iscolectivoRepetido IN NUMBER,
                               v_colref IN VARCHAR2,v_refpoliza IN VARCHAR2, v_dc IN NUMBER , v_tipoRef IN VARCHAR2,
                               codEntidadCol IN NUMBER,entMediadoraCol IN NUMBER, subEntMediadoraCol IN NUMBER, codPlan IN NUMBER,
                               v_linea IN NUMBER,idFichero IN NUMBER,v_idreciboComision IN NUMBER,v_planFichero IN NUMBER,
                               v_codEntidad IN VARCHAR2,v_dtGastosPagados IN NUMBER, v_oficina IN VARCHAR2,v_subEntMedFichero IN NUMBER,
                               v_coincideRefCol IN boolean,v_entMed IN VARCHAR2) IS

 v_lineaseguroId                       NUMBER := 0;
 v_isValidMediadorGGE                  NUMBER := 1;--Por defecto a 1 para no dar falsos positivos
 v_isValidMediadorMtoCultivos          NUMBER := 1;--Por defecto a 1 para no dar falsos positivos
 v_isValidMediadorReglamento           NUMBER := 1;--Por defecto a 1 para no dar falsos positivos
 v_isValidReferenciaPoliza             NUMBER := 0;
 v_isvalidESMed                        NUMBER := 1;--Por defecto a 1 para no dar falsos positivos
 v_isValidPctGlobalEntidad             NUMBER := 1;--Por defecto a 1 para no dar falsos positivos
 v_isColectivoxx0                      NUMBER := 0;
 entidad                               NUMBER := 0;
 lineaseguroIdGenerico                 NUMBER := 0;
BEGIN

     -- obtenemos la lineaseguroid

     v_lineaseguroId := PQ_UTIL_VALIDACIONES.getLineaSeguroId(v_linea,v_planFichero);
     IF v_lineaseguroId = -1 THEN
       v_lineaseguroId := PQ_UTIL_VALIDACIONES.getLineaSeguroIdGenerica(v_planFichero);
     END IF;

     entidad := '3' || v_codEntidad;

     IF  v_isValidColectivo = 1 AND v_existeColectivo > 0 THEN --correcto

          BEGIN
              execute immediate 'select lin.lineaseguroid from tb_lineas lin  where lin.codplan = ' || codPlan || ' and lin.codlinea = 999' into lineaseguroIdGenerico;
              EXCEPTION
                       WHEN NO_DATA_FOUND THEN
                            lineaseguroIdGenerico := 0;

          END;
          IF entidad BETWEEN 3000 AND 3999 AND subEntMediadoraCol = 0  THEN
             v_isValidMediadorGGE:=1;

      			 IF codPlan<2015 THEN
      			    v_isValidMediadorMtoCultivos:=1;
      			 ELSE
      			     v_isValidMediadorMtoCultivos:= PQ_UTIL_VALIDACIONES.checkMediadorMtoCultivos2015(entMediadoraCol, subEntMediadoraCol, codPlan, v_linea);
      			 END IF;

             v_isValidMediadorReglamento:=1;

          ELSE
             v_isValidMediadorGGE := PQ_UTIL_VALIDACIONES.checkMediadorGGE(entMediadoraCol, subEntMediadoraCol, codPlan);

			 IF codPlan<2015 THEN
				v_isValidMediadorMtoCultivos := PQ_UTIL_VALIDACIONES.checkMediadorMtoCultivos(entMediadoraCol, subEntMediadoraCol, v_lineaseguroId,lineaseguroIdGenerico);
			 ELSE
				v_isValidMediadorMtoCultivos:= PQ_UTIL_VALIDACIONES.checkMediadorMtoCultivos2015(entMediadoraCol, subEntMediadoraCol, codPlan, v_linea);
			 END IF;

             v_isValidMediadorReglamento := PQ_UTIL_VALIDACIONES.checkMediadorReglamento(codEntidadCol);
          END IF;

           v_isvalidESMed := PQ_UTIL_VALIDACIONES.checkESMediadora(entMediadoraCol,subEntMediadoraCol);
     ELSE
            v_isValidMediadorGGE:=1;

			 IF codPlan<2015 THEN
				v_isValidMediadorMtoCultivos:=1;
			 ELSE
				v_isValidMediadorMtoCultivos:= PQ_UTIL_VALIDACIONES.checkMediadorMtoCultivos2015(entMediadoraCol, subEntMediadoraCol, codPlan, v_linea);
			 END IF;

            v_isValidMediadorReglamento:=1;
     END IF;


     v_isValidReferenciaPoliza :=  PQ_UTIL_VALIDACIONES.checkRefPoliza(v_refpoliza, v_dc, v_tipoRef);


     IF v_isValidReferenciaPoliza != 0 THEN

       v_isColectivoxx0 := PQ_UTIL_VALIDACIONES.checkSubentidadByColectivo(v_colref);
	   -- MOD: Se elimina la validación sobre el porcentaje general de entidades del GGE a partir del plan 2015, pero se mantiene para planes anteriores
       IF (codPlan<2015 AND v_isColectivoxx0 = 0) THEN
          v_isValidPctGlobalEntidad :=  PQ_UTIL_VALIDACIONES.checkPctGlobalEntidad(v_dtGastosPagados, idFichero);
       ELSE
          v_isValidPctGlobalEntidad := 1;
       END IF;

    END IF;


     -- Chequeamos las validaciones
     checkIncidencias (v_existeColectivo,v_isValidColectivo,v_iscolectivoRepetido,v_isValidMediadorGGE,v_isValidMediadorMtoCultivos,
                       v_isValidMediadorReglamento,v_isValidReferenciaPoliza,idFichero,v_lineaseguroId,v_colref,
                       entMediadoraCol,subEntMediadoraCol,v_refpoliza,v_idreciboComision,v_isValidPctGlobalEntidad,
                       v_isvalidESMed,entidad,v_oficina,v_subEntMedFichero, v_coincideRefCol,v_entMed, codPlan);

END   validaDatosPolizas;

PROCEDURE  checkIncidencias (v_existeColectivo IN NUMBER,v_isValidColectivo IN NUMBER,v_iscolectivoRepetido IN NUMBER,
                             v_isValidMediadorGGE IN NUMBER,v_isValidMediadorMtoCultivos IN NUMBER,v_isValidMediadorReglamento IN NUMBER,
                             v_isValidReferenciaPoliza IN NUMBER,idFichero IN NUMBER,v_lineaseguroId IN NUMBER,v_colref IN VARCHAR2,
                             entMediadoraCol IN NUMBER, subEntMediadoraCol IN NUMBER,v_refpoliza IN VARCHAR2,
                             v_idreciboComision IN NUMBER,v_isValidPctGlobalEntidad IN NUMBER,v_isvalidESMed IN NUMBER,
                             entidad IN NUMBER,v_oficina IN VARCHAR2,v_subEntMedFichero IN NUMBER, v_coincideRefCol IN boolean,v_entMed IN VARCHAR2, v_codPlan IN NUMBER) IS

 v_mjsError                            VARCHAR2(2000) := '';
 v_estado                              VARCHAR2(20);
 v_tipoIncidencia                      NUMBER;
 entidadSubentidad                     varchar2(8);
 entSubColectivo                       VARCHAR2(10);
 v_validaES                            boolean;
BEGIN
      -- E-S Fichero
     entidadSubentidad :=  v_entMed ||'-'||v_subEntMedFichero;
     -- E-S  Colectivo
     entSubColectivo := entMediadoraCol ||'-'|| subEntMediadoraCol;

     IF entidadSubentidad != entSubColectivo THEN
        v_validaES := FALSE;
     ELSE
        v_validaES := TRUE;
     END IF;

      IF v_isValidColectivo = 0 OR  v_isValidMediadorMtoCultivos = 0 OR v_isValidMediadorGGE = 0
      OR v_isValidMediadorReglamento = 0 OR v_isValidReferenciaPoliza = 0 OR v_isValidPctGlobalEntidad = 0
      OR v_iscolectivoRepetido = 0 OR v_isvalidESMed = 0 OR  v_existeColectivo = 0 OR v_validaES = FALSE
      OR  v_coincideRefCol = FALSE OR v_oficina is null THEN

          IF v_existeColectivo = 0 THEN
             v_mjsError := v_mjsError || 'El colectivo no existe en BBDD.|';

          ELSIF v_isValidColectivo = 0 THEN
             v_mjsError := v_mjsError || 'No corresponden las E-S Med. mostradas con las del colectivo.|';

          ELSIF v_iscolectivoRepetido = 0 THEN
             v_mjsError := v_mjsError || 'El Colectivo esta duplicado en la BBDD.|';

          END IF;


          IF v_isValidReferenciaPoliza = 0 THEN -- si es erroneo
              v_mjsError :=v_mjsError || 'Error, No existe la referencia de la Poliza.|';
          END IF;

          IF v_oficina is null THEN
      		  v_mjsError :=v_mjsError || 'Error, La póliza no tiene oficina.|';
      		END IF;

          IF v_isValidPctGlobalEntidad = 0 THEN
                 v_mjsError := v_mjsError || 'El porcentaje General Entidades de GGE no debe ser mayor que el porcentaje GGE de la póliza.|';
          END IF;

          IF v_isValidMediadorGGE = 0 THEN
               v_mjsError := v_mjsError || 'Cada Póliza tiene que tener asignado su mediador GGE.|';
          END IF;

          IF v_isValidMediadorMtoCultivos = 0 THEN
      			IF v_codPlan <2015 THEN
                     v_mjsError := v_mjsError || 'Cada Póliza tiene que tener asignado su mediador Mto Cultivos.|';
      			ELSE
      			   v_mjsError := v_mjsError || 'El mediador no existe o está dado de baja.|';
      			END IF;
          END IF;

          IF v_isValidMediadorReglamento = 0 THEN
               v_mjsError := v_mjsError || 'Cada Póliza tiene que tener asignado su mediador Reglamento.|';
          END IF;

          IF v_isvalidESMed = 0 THEN
               v_mjsError := v_mjsError || 'La E-S Mediadora no existe.|';
          END IF;

          IF v_validaES = FALSE THEN
               v_mjsError := v_mjsError || 'No coincide la E-S Mediadora entre fichero y colectivo.|';
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





     -- Insertamos la incidencia
     PQ_UTIL_VALIDACIONES.insertIncidencia (idFichero,v_lineaseguroId,v_colref,entidadSubentidad,v_refpoliza,v_oficina,
                       v_estado,v_mjsError,v_idreciboComision,v_tipoIncidencia,entSubColectivo);

END    checkIncidencias;

--Funcion que valida si la E-S existe
FUNCTION checkESMediadora(v_entMed IN NUMBER,v_subEntMed IN NUMBER) RETURN NUMBER
IS

  v_isValid               NUMBER := 0;
 BEGIN
     BEGIN
          IF v_entMed is not null AND v_subEntMed is not null THEN
               BEGIN
                 select count(*) into v_isValid
                 from tb_subentidades_mediadoras s
                 where s.codentidad = v_entMed
                 and s.codsubentidad= v_subEntMed;

                  EXCEPTION
                       WHEN NO_DATA_FOUND THEN
                            v_isValid := 0;
                END;
            END IF;
      END;
      RETURN v_isValid;
END checkESMediadora;

end PQ_VALIDAR_IMPORT_FICH;
/
SHOW ERRORS;