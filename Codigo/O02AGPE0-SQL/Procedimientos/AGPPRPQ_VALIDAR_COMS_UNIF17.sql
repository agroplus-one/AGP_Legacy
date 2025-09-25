SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package PQ_VALIDAR_COMS_UNIF17 is

  PROCEDURE doValidarFichGastosEntidad(V_IDFICHERO IN NUMBER);

  PROCEDURE validaDatosPolizas(v_refColectivoFichero IN VARCHAR2,
                               v_refPolizaFichero    IN VARCHAR2,
                               v_dcFichero           IN NUMBER,
                               v_tipoRefFichero      IN VARCHAR2,
                               codEntidadCol         IN NUMBER,
                               entMediadoraCol       IN NUMBER,
                               subEntMediadoraCol    IN NUMBER,
                               codPlan               IN NUMBER,
                               v_lineaFichero        IN NUMBER,
                               v_idFichero           IN NUMBER,
                               v_idReciboComision    IN NUMBER,
                               v_codEntidadFichero   IN VARCHAR2,
                               v_oficinaPoliza       IN VARCHAR2,
                               v_subEntMedFichero    IN VARCHAR2,
                               v_entMedFichero       IN VARCHAR2,
                               v_faseFichero         IN VARCHAR2,
                               v_existeColectivo     IN NUMBER,
                               v_isValidColectivo    IN NUMBER,
                               v_isColectivoRepetido IN NUMBER,
                               v_coincideRefCol      IN boolean,
                               v_lineaseguroId       IN NUMBER); -- RQ.01);

  PROCEDURE checkIncidencias(v_existeColectivo            IN NUMBER,
                             v_isValidColectivo           IN NUMBER,
                             v_isColectivoRepetido        IN NUMBER,
                             v_isValidMediadorGGE         IN NUMBER,
                             v_isValidMediadorMtoCultivos IN NUMBER,
                             v_isValidMediadorReglamento  IN NUMBER,
                             v_isValidReferenciaPoliza    IN NUMBER,
                             v_isValidPctGlobalEntidad    IN NUMBER,
                             v_isValidESMed               IN NUMBER,
                             v_coincideRefCol             IN boolean,
                             v_idFichero                  IN NUMBER,
                             v_lineaseguroId              IN NUMBER,
                             v_refColectivoFichero        IN VARCHAR2,
                             entMediadoraCol              IN NUMBER,
                             subEntMediadoraCol           IN NUMBER,
                             v_refPolizaFichero           IN VARCHAR2,
                             v_idreciboComision           IN NUMBER,
                             v_oficinaPoliza              IN VARCHAR2,
                             v_subEntMedFichero           IN VARCHAR2,
                             v_entMedFichero              IN VARCHAR2,
                             v_faseFichero                IN VARCHAR2,
                             v_polizaSinComisiones        IN NUMBER,
                             v_areComsOk                  IN BOOLEAN, -- TODO req.01 - P78497
                             v_isComsConValor             IN BOOLEAN); -- TODO req.01 - P78497

end PQ_VALIDAR_COMS_UNIF17;
/
create or replace package body PQ_VALIDAR_COMS_UNIF17 is

  PROCEDURE doValidarFichGastosEntidad(V_IDFICHERO IN NUMBER) IS
  
    TYPE TpCursor IS REF CURSOR;
    l_tp_cursor TpCursor;
    l_sql       VARCHAR2(2000);
  
    -- variables principales importacion
    v_faseFichero         tb_coms_unif_fase.fase%TYPE;
    v_planFichero         tb_coms_unif_fase.plan%TYPE;
    v_lineaFichero        tb_coms_unif17_recibo.linea%TYPE;
    v_fechaEmision        tb_coms_unif_fase.fecha_emision_recibo%TYPE;
    v_idreciboComision    tb_coms_unif17_recibo.id%TYPE;
    v_refPolizaFichero    tb_coms_unif17_poliza.referencia%TYPE;
    v_refColectivoFichero tb_coms_unif_colectivo.referencia%TYPE;
    v_colIdAuxFichero     tb_coms_unif_colectivo.id%TYPE;
    v_dcFichero           tb_coms_unif_colectivo.dc%TYPE;
    v_tipoRefFichero      tb_coms_unif_fichero.tipo_fichero%TYPE;
    v_codEntidadFichero   VARCHAR2(20);
    v_entMedFichero       VARCHAR2(20);
    v_subEntMedFichero    VARCHAR2(20);
    v_estadoFichero       tb_coms_unif_fichero.estado%TYPE;
  
    -- variables validaciones
    v_existeColectivo     NUMBER := 0;
    v_isValidColectivo    NUMBER := 1;
    v_isColectivoRepetido NUMBER := 1;
    v_coincideRefCol      boolean;
  
    -- datos de poliza sacados del sistema
    refColectivo       TB_HISTORICO_COLECTIVOS.REFERENCIA%TYPE;
    codEntidadCol      TB_HISTORICO_COLECTIVOS.CODENTIDAD%TYPE;
    entMediadoraCol    TB_HISTORICO_COLECTIVOS.ENTMEDIADORA%TYPE;
    subEntMediadoraCol TB_HISTORICO_COLECTIVOS.SUBENTMEDIADORA%TYPE;
    oficinaPoliza      TB_POLIZAS.OFICINA%TYPE;
  
    contador number := 0;
	
    -- TODO req.01 - P78497
    v_lineaseguroId o02agpe0.tb_lineas.lineaseguroid%TYPE;
	TYPE t_lineaseguroId_arr is TABLE OF o02agpe0.tb_lineas.lineaseguroid%TYPE;
    v_lineaseguroId_arr t_lineaseguroId_arr;
    v_idAux         o02agpe0.tb_lineas.lineaseguroid%TYPE := 0;
	
	timestart NUMBER;
  BEGIN
  
	o02agpe0.PQ_UTL.LOG('[BEGIN] doValidarFichGastosEntidad ' || V_IDFICHERO || '            ', 2);
    timestart := dbms_utility.get_time();
	
    --- ASF 27/07/2017 cambio para utilizar la fecha de envio de la poliza como fecha de emision para las busquedas en historicos
    l_sql := 'select fase.fase fase, ' || 'fase.plan plan, ' ||
             'rec.linea linea, ' ||
             'nvl(p.fechaenvio, fase.fecha_emision_recibo) fechaEmision, ' ||
             'pol.idrecibo, ' ||
             'SUBSTR(col.codigo_interno, 2, 3) as codentidad, ' ||
             'SUBSTR(col.codigo_interno, 1, 4) as entMed, ' ||
             'nvl(trim(SUBSTR(col.codigo_interno, 5)), 0), ' ||
             'pol.referencia refPoliza, ' || 'pol.dc, ' ||
             'pol.tipo_referencia, ' || 'col.id, ' ||
             'col.referencia refColectivo, ' || 'l.lineaseguroid ' ||
             'from o02agpe0.TB_COMS_UNIF_FASE fase ' ||
             'inner join o02agpe0.TB_COMS_UNIF17_RECIBO rec on fase.id = rec.idfase ' ||
             'inner join o02agpe0.TB_COMS_UNIF17_POLIZA pol on rec.id = pol.idrecibo ' ||
             'left outer join o02agpe0.TB_COMS_UNIF_COLECTIVO col on pol.idcolectivo = col.id ' ||
             'inner join o02agpe0.tb_lineas l on l.codplan = fase.plan and l.codlinea = rec.linea ' ||
             'left outer join o02agpe0.tb_polizas p on p.idestado >= 8 ' ||
             'and p.referencia = pol.referencia ' ||
             'and p.tiporef = pol.tipo_referencia ' ||
             'and p.lineaseguroid = l.lineaseguroid ' ||
             'where fase.idfichero = ' || V_IDFICHERO || ' ' ||
             'order by fase.fase, fase.plan, rec.linea'; 
  
    OPEN l_tp_cursor FOR l_sql;
    LOOP
      FETCH l_tp_cursor
        INTO v_faseFichero, v_planFichero, v_lineaFichero, v_fechaEmision, v_idreciboComision, v_codEntidadFichero, v_entMedFichero, v_subEntMedFichero, v_refPolizaFichero, v_dcFichero, v_tipoRefFichero, v_colIdAuxFichero, v_refColectivoFichero, v_lineaseguroId;
      EXIT WHEN l_tp_cursor%NOTFOUND;
    
      --Por si es individual, que no salten
      v_existeColectivo     := 1;
      v_isValidColectivo    := 1;
      v_isColectivoRepetido := 1;
      v_coincideRefCol      := true;
      subEntMediadoraCol    := 0;
      codEntidadCol         := 0;
      entMediadoraCol       := 0;
      oficinaPoliza         := null;
    
      contador := contador + 1;
    
      -- si es distinto de null, es que hay colectivo y lo validamos
      IF v_colIdAuxFichero IS NOT null THEN
        v_existeColectivo := PQ_UTIL_VALIDACIONES.checkExisteColectivo17(v_refColectivoFichero,
                                                                         v_planFichero);
      
        BEGIN
          select referencia, codentidad, entmediadora, subentmediadora
            into refColectivo,
                 codEntidadCol,
                 entMediadoraCol,
                 subEntMediadoraCol
            from (select c.referencia      as referencia,
                         c.codentidad      as codentidad,
                         c.entmediadora    as entmediadora,
                         c.subentmediadora as subentmediadora,
                         l.codplan         as codplan
                    from tb_historico_colectivos c, tb_lineas l
                   where c.idcolectivo in
                         (select idcolectivo
                            from tb_polizas p, tb_lineas l2
                           where p.referencia = v_refPolizaFichero
                             and p.tiporef = v_tipoRefFichero
                             and p.idestado >= 8
                             and p.lineaseguroid = l2.lineaseguroid
                             and l2.codplan = v_planFichero)
                     and l.lineaseguroid = c.lineaseguroid
                     and c.fechaefecto <= v_fechaEmision
                   order by c.fechacambio desc)
           where rownum = 1;
        
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_isValidColectivo := 0;
            subEntMediadoraCol := 0;
          WHEN too_many_rows THEN
            v_isColectivoRepetido := 0;
        END;
      
        -- Comprobamos que las referencias de los colectivos son iguales
        IF refColectivo != v_refColectivoFichero THEN
          v_coincideRefCol := false;
        END IF;
      END IF;
	
      
      BEGIN
        --Calculamos la oficina
        select oficina
          into oficinaPoliza
          from o02agpe0.tb_polizas p
		  inner join o02agpe0.tb_lineas l on l.codplan = v_planFichero 
				and l.lineaseguroid = p.lineaseguroid
         where p.referencia = v_refPolizaFichero
           and p.dc = v_dcFichero
           and p.tiporef = v_tipoRefFichero
           and p.idestado <> 0 
           and p.idestado <> 1
           and p.idestado <> 2;
      EXCEPTION
          WHEN NO_DATA_FOUND THEN
            oficinaPoliza := NULL;
	  END;
	
      -- Por cada poliza hacemos las validaciones e insertamos incidencias
      validaDatosPolizas(v_refColectivoFichero,
                         v_refPolizaFichero,
                         v_dcFichero,
                         v_tipoRefFichero,
                         codEntidadCol,
                         entMediadoraCol,
                         subEntMediadoraCol,
                         v_planFichero,
                         v_lineaFichero,
                         V_IDFICHERO,
                         v_idReciboComision,
                         v_codEntidadFichero,
                         oficinaPoliza,
                         v_subEntMedFichero,
                         v_entMedFichero,
                         v_faseFichero,
                         v_existeColectivo,
                         v_isValidColectivo,
                         v_isColectivoRepetido,
                         v_coincideRefCol,
                         v_lineaseguroId); -- RQ.01
    
      oficinaPoliza := null;
    
      IF v_lineaseguroId not member of v_lineaseguroId_arr THEN
		v_lineaseguroId_arr.extend;
		v_lineaseguroId_arr(v_lineaseguroId_arr.count) := v_lineaseguroId;
        IF PQ_UTIL_VALIDACIONES.checkImpPolizasFichero(V_IDFICHERO, v_lineaseguroId) = FALSE THEN
		    pq_util_validaciones.insertincidenciaunif(V_IDFICHERO,
                                                      v_lineaseguroId,
                                                      NULL,
                                                      NULL,
                                                      NULL,
                                                      NULL,
                                                      'A',
                                                      'El importe de las polizas del fichero no coincide con el de Agroplus',
                                                      NULL,
                                                      2,
                                                      NULL,
                                                      NULL);			
        END IF;
      END IF;
    END LOOP;
  
    begin
      v_estadoFichero := PQ_UTIL_VALIDACIONES.getEstadoFicheroUnificado(V_IDFICHERO);
    end;
    begin
      UPDATE TB_COMS_UNIF_FICHERO
         SET ESTADO = v_estadoFichero
       WHERE ID = V_IDFICHERO;
    
    end;
	
	o02agpe0.PQ_UTL.LOG('[RUNTIME] doValidarFichGastosEntidad: ' || (dbms_utility.get_time() - timestart) || '            ', 2);
	o02agpe0.PQ_UTL.LOG('[END] doValidarFichGastosEntidad ' || V_IDFICHERO || '            ', 2);  
  END doValidarFichGastosEntidad;

  PROCEDURE validaDatosPolizas(v_refColectivoFichero IN VARCHAR2,
                               v_refPolizaFichero    IN VARCHAR2,
                               v_dcFichero           IN NUMBER,
                               v_tipoRefFichero      IN VARCHAR2,
                               codEntidadCol         IN NUMBER,
                               entMediadoraCol       IN NUMBER,
                               subEntMediadoraCol    IN NUMBER,
                               codPlan               IN NUMBER,
                               v_lineaFichero        IN NUMBER,
                               v_idFichero           IN NUMBER,
                               v_idReciboComision    IN NUMBER,
                               v_codEntidadFichero   IN VARCHAR2,
                               v_oficinaPoliza       IN VARCHAR2,
                               v_subEntMedFichero    IN VARCHAR2,
                               v_entMedFichero       IN VARCHAR2,
                               v_faseFichero         IN VARCHAR2,
                               v_existeColectivo     IN NUMBER,
                               v_isValidColectivo    IN NUMBER,
                               v_isColectivoRepetido IN NUMBER,
                               v_coincideRefCol      IN boolean,
                               v_lineaseguroId       IN NUMBER -- RQ.01 - P78497
                               ) IS
  
    -- v_lineaseguroId              NUMBER := 0; -- RQ.01 - P78497
    v_isValidMediadorGGE         NUMBER := 1; --Por defecto a 1 para no dar falsos positivos
    v_isValidMediadorMtoCultivos NUMBER := 1; --Por defecto a 1 para no dar falsos positivos
    v_isValidMediadorReglamento  NUMBER := 1; --Por defecto a 1 para no dar falsos positivos
    v_isValidReferenciaPoliza    NUMBER := 0;
    v_isvalidESMed               NUMBER := 1; --Por defecto a 1 para no dar falsos positivos
    v_isValidPctGlobalEntidad    NUMBER := 1; --Por defecto a 1 para no dar falsos positivos
    v_polizaSinComisiones        NUMBER := 0; --
    entidad                      NUMBER := 0;
    -- RQ.01 - P78497
    v_areComsOk BOOLEAN := TRUE;
    -- RQ.01 - P78497
    v_isComsConValor BOOLEAN := FALSE;
  BEGIN
    o02agpe0.PQ_UTL.LOG('[BEGIN] validaDatosPolizas' || '            ', 2);
	-- obtenemos la lineaseguroid
    v_isValidReferenciaPoliza := PQ_UTIL_VALIDACIONES.checkRefPoliza17(v_refPolizaFichero,
                                                                       v_dcFichero,
                                                                       v_tipoRefFichero,
                                                                       v_lineaseguroId);
  
    -- RQ.01 - P78497
    IF v_isvalidreferenciapoliza != 0 THEN
    
      IF v_isValidColectivo = 1 AND v_existeColectivo > 0 THEN
        --correcto
      
        entidad := '3' || v_codEntidadFichero;
      
        IF entidad BETWEEN 3000 AND 3999 AND subEntMediadoraCol = 0 THEN
          v_isValidMediadorGGE        := 1;
          v_isValidMediadorReglamento := 1;
        ELSE
          v_isValidMediadorGGE         := PQ_UTIL_VALIDACIONES.checkMediadorGGE(entMediadoraCol,
                                                                                subEntMediadoraCol,
                                                                                codPlan);
          v_isValidMediadorMtoCultivos := PQ_UTIL_VALIDACIONES.checkMediadorMtoCultivos2015(entMediadoraCol,
                                                                                            subEntMediadoraCol,
                                                                                            codPlan,
                                                                                            v_lineaFichero);
          v_isValidMediadorReglamento  := PQ_UTIL_VALIDACIONES.checkMediadorReglamento(codEntidadCol);
        END IF;
      
        v_isvalidESMed := PQ_UTIL_VALIDACIONES.checkESMediadora(entMediadoraCol,
                                                                subEntMediadoraCol);
      ELSE
        v_isValidMediadorGGE         := 1;
        v_isValidMediadorMtoCultivos := PQ_UTIL_VALIDACIONES.checkMediadorMtoCultivos2015(entMediadoraCol,
                                                                                          subEntMediadoraCol,
                                                                                          codPlan,
                                                                                          v_lineaFichero);
        v_isValidMediadorReglamento  := 1;
      END IF;
    
      v_areComsOk := PQ_UTIL_VALIDACIONES.checkComsPolizaFichero(v_idFichero,
                                                                 v_refPolizaFichero);
    
      IF (entMediadoraCol BETWEEN 3000 AND 3999 OR entMediadoraCol BETWEEN 8000 AND 8999)
      AND subEntMediadoraCol = 0 THEN
        v_isComsConValor := PQ_UTIL_VALIDACIONES.tienecomsesmed(v_refPolizaFichero,
																codPlan,
                                                                v_idFichero);
      END IF;
    END IF;
    -- Chequeamos las validaciones
    checkIncidencias(v_existeColectivo,
                     v_isValidColectivo,
                     v_isColectivoRepetido,
                     v_isValidMediadorGGE,
                     v_isValidMediadorMtoCultivos,
                     v_isValidMediadorReglamento,
                     v_isValidReferenciaPoliza,
                     v_isValidPctGlobalEntidad,
                     v_isValidESMed,
                     v_coincideRefCol,
                     v_idFichero,
                     v_lineaseguroId,
                     v_refColectivoFichero,
                     entMediadoraCol,
                     subEntMediadoraCol,
                     v_refPolizaFichero,
                     v_idreciboComision,
                     v_oficinaPoliza,
                     v_subEntMedFichero,
                     v_entMedFichero,
                     v_faseFichero,
                     v_polizaSinComisiones,
                     v_areComsOk,
                     v_isComsConValor); -- TODO req.01 - P78497
    o02agpe0.PQ_UTL.LOG('[END] validaDatosPolizas' || '            ', 2);					 
  END validaDatosPolizas;

  PROCEDURE checkIncidencias(v_existeColectivo            IN NUMBER,
                             v_isValidColectivo           IN NUMBER,
                             v_isColectivoRepetido        IN NUMBER,
                             v_isValidMediadorGGE         IN NUMBER,
                             v_isValidMediadorMtoCultivos IN NUMBER,
                             v_isValidMediadorReglamento  IN NUMBER,
                             v_isValidReferenciaPoliza    IN NUMBER,
                             v_isValidPctGlobalEntidad    IN NUMBER,
                             v_isValidESMed               IN NUMBER,
                             v_coincideRefCol             IN boolean,
                             v_idFichero                  IN NUMBER,
                             v_lineaseguroId              IN NUMBER,
                             v_refColectivoFichero        IN VARCHAR2,
                             entMediadoraCol              IN NUMBER,
                             subEntMediadoraCol           IN NUMBER,
                             v_refPolizaFichero           IN VARCHAR2,
                             v_idreciboComision           IN NUMBER,
                             v_oficinaPoliza              IN VARCHAR2,
                             v_subEntMedFichero           IN VARCHAR2,
                             v_entMedFichero              IN VARCHAR2,
                             v_faseFichero                IN VARCHAR2,
                             v_polizaSinComisiones        IN NUMBER,
                             v_areComsOk                  IN BOOLEAN,
                             v_isComsConValor             IN BOOLEAN) IS
  
    v_mjsError        VARCHAR2(2000) := '';
    v_estado          VARCHAR2(1);
    v_tipoIncidencia  NUMBER;
    entidadSubentidad varchar2(20);
    entSubColectivo   VARCHAR2(10);
    v_validaES        boolean;
    subEntFichero     VARCHAR2(20);
    v_idIncidR        NUMBER(15);
    v_insertaIncid    boolean := TRUE;
	timestart NUMBER;
  BEGIN
	timestart := dbms_utility.get_time();
    IF (PQ_UTIL_VALIDACIONES.isNumeric(v_subEntMedFichero)) then
      subEntFichero := abs(nvl(to_number(v_subEntMedFichero), 0));
    ELSE
      subEntFichero := v_subEntMedFichero;
    END IF;
    -- FIN
  
    -- E-S Fichero
    entidadSubentidad := v_entMedFichero || '-' || subEntFichero;
    -- E-S Colectivo
    entSubColectivo := entMediadoraCol || '-' || subEntMediadoraCol;
  
    IF entidadSubentidad != entSubColectivo THEN
      v_validaES := FALSE;
    ELSE
      v_validaES := TRUE;
    END IF;
  
    IF v_isValidReferenciaPoliza = 0 THEN
      -- si es erroneo
      v_mjsError       := 'Error, No existe la referencia de la Poliza.';
      v_estado         := 'E';
      v_tipoIncidencia := 1;
    ELSE
    
      IF v_isValidColectivo = 0 OR v_isValidMediadorMtoCultivos = 0 OR
         v_isValidMediadorGGE = 0 OR v_isValidMediadorReglamento = 0 OR
         v_isValidPctGlobalEntidad = 0 OR v_isColectivoRepetido = 0 OR
         v_isValidESMed = 0 OR v_existeColectivo = 0 OR v_validaES = FALSE OR
         v_coincideRefCol = FALSE OR v_polizaSinComisiones = 1 OR
         v_oficinaPoliza is null OR v_areComsOk = FALSE OR
         v_isComsConValor = TRUE THEN
      
        IF v_polizaSinComisiones = 1 THEN
          v_mjsError := v_mjsError ||
                        'Poliza sin distribucion de comisiones.|';
        END IF;
      
        IF v_existeColectivo = 0 THEN
          v_mjsError := v_mjsError || 'El colectivo no existe en BBDD.|';
        
        ELSIF v_isValidColectivo = 0 THEN
          v_mjsError := v_mjsError ||
                        'No corresponden las E-S Med. mostradas con las del colectivo.|';
        
        ELSIF v_isColectivoRepetido = 0 THEN
          v_mjsError := v_mjsError ||
                        'El Colectivo esta duplicado en la BBDD.|';
        
        END IF;
      
        IF v_oficinaPoliza is null THEN
          v_mjsError := v_mjsError || 'Error, La poliza no tiene oficina.|';
        END IF;
      
        IF v_isValidPctGlobalEntidad = 0 THEN
          v_mjsError := v_mjsError ||
                        'El porcentaje General Entidades de GGE no debe ser mayor que el porcentaje GGE de la poliza.|';
        END IF;
      
        IF v_isValidMediadorGGE = 0 THEN
          v_mjsError := v_mjsError ||
                        'Cada Poliza tiene que tener asignado su mediador GGE.|';
        END IF;
      
        IF v_isValidMediadorMtoCultivos = 0 THEN
          v_mjsError := v_mjsError ||
                        'El mediador no existe o esta dado de baja.|';
        END IF;
      
        IF v_isValidMediadorReglamento = 0 THEN
          v_mjsError := v_mjsError ||
                        'Cada Poliza tiene que tener asignado su mediador Reglamento.|';
        END IF;
      
        IF v_isValidESMed = 0 THEN
          v_mjsError := v_mjsError || 'La E-S Mediadora no existe.|';
        END IF;
      
        IF v_validaES = FALSE THEN
          v_mjsError := v_mjsError ||
                        'No coincide la E-S Mediadora entre fichero y colectivo.|';
        END IF;
      
        IF v_coincideRefCol = FALSE THEN
          v_mjsError := v_mjsError ||
                        'El colectivo del fichero no coincide con el colectivo de la poliza.|';
        END IF;
      
        
              
        -- TODO req.01 - P78497
        IF v_isComsConValor = TRUE THEN
          v_mjsError := v_mjsError ||
                        'La comision de la E-S Mediadora no debe tener valor.|';
        END IF;
        
        
        -- TODO req.01 - P78497
        IF v_areComsOk = FALSE THEN
          v_mjsError := v_mjsError ||
                        'El importe de comisiones la poliza calculado no corresponde con el fichero.';
        END IF;

      
        IF v_isColectivoRepetido = 0 OR v_existeColectivo = 0 or
           v_polizaSinComisiones = 1 THEN
          v_estado         := 'E';
          v_tipoIncidencia := 1;
        ELSE
          v_estado         := 'A';
          v_tipoIncidencia := 2;
        END IF;
      
      ELSE
        v_estado         := 'C';
        v_tipoIncidencia := 0;
      END IF;
    
    END IF;
  
    IF v_estado = 'E' OR v_estado = 'A' THEN
    
      --VERIFICAR SI EXISTE REGISTRO EN TB_COMS_UNIF_FICH_INCIDENCIAS
      BEGIN
        SELECT ID
          INTO v_idIncidR
          FROM TB_COMS_UNIF_FICH_INCIDENCIAS
         WHERE ESTADO = 'R'
           AND ID_FICHERO_UNIF = v_idFichero
           AND LINEASEGUROID = v_lineaseguroId
           AND FASE = v_faseFichero
           AND TIPO_INCIDENCIA = v_tipoIncidencia
           AND ROWNUM = 1;
        v_insertaIncid := FALSE;
      
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          v_insertaIncid := TRUE;
      END;
    
    END IF;
  
    IF v_insertaIncid = TRUE THEN
      -- Insertamos la incidencia
      PQ_UTIL_VALIDACIONES.insertIncidenciaUnif(v_idFichero,
                                                v_lineaseguroId,
                                                v_refColectivoFichero,
                                                entidadSubentidad,
                                                v_refPolizaFichero,
                                                v_oficinaPoliza,
                                                v_estado,
                                                v_mjsError,
                                                v_idReciboComision,
                                                v_tipoIncidencia,
                                                entSubColectivo,
                                                v_faseFichero);
    END IF;
  END checkIncidencias;

end PQ_VALIDAR_COMS_UNIF17;
/
SHOW ERRORS;
