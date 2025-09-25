SET DEFINE OFF;
SET SERVEROUTPUT ON;

CREATE OR REPLACE PACKAGE o02agpe0.PQ_SITUACION_ACTUALIZADA IS

  PROCEDURE arrastre_parcelas_copy(P_LINEASEGUROID IN NUMBER,
                                   P_IDCOPY        IN NUMBER,
                                   P_IDPOLIZA_DEST IN NUMBER,
                                   P_CLASE         IN NUMBER);

  PROCEDURE setParcelaCopy(p_lineaseguroid IN NUMBER,
                           p_idpoliza      IN NUMBER,
                           p_idcopyparcela IN NUMBER);

  PROCEDURE setCapitalAseguradoCopy(p_idcopycapaseg IN NUMBER,
                                    p_idparcela     IN NUMBER,
                                    p_idpoliza      IN NUMBER,
                                    p_lineaseguroid IN NUMBER);

  PROCEDURE setDatosVariablesCopy(p_idcopydatvar      IN NUMBER,
                                  p_idcapaseg         IN NUMBER,
                                  P_IDPARCELA_DESTINO IN NUMBER,
                                  P_IDPOLIZA_DESTINO  IN NUMBER,
                                  p_lineaseguroid     IN NUMBER);

  PROCEDURE setCapAsegRelModulo(p_idcapitalasegurado IN NUMBER,
                                p_idpoliza           IN NUMBER,
                                p_precio             IN NUMBER,
                                p_produccion         IN NUMBER);

  PROCEDURE setInstalacionesCopy(P_IDPOLIZA_DEST     IN NUMBER);

  PROCEDURE arrastre_parcelas(P_LINEASEGUROID IN NUMBER,
                              P_IDPOLIZA_ORIG IN NUMBER,
                              P_IDPOLIZA_DEST IN NUMBER,
                              P_CLASE         IN NUMBER);

  PROCEDURE setParcela(p_lineaseguroid IN NUMBER,
                       p_idpoliza      IN NUMBER,
                       p_idparcela     IN NUMBER);

  PROCEDURE setCapitalAsegurado(p_lineaseguroid IN NUMBER,
                                p_idcapaseg IN NUMBER,
                                p_idparcela IN NUMBER,
                                p_idpoliza  IN NUMBER);

  PROCEDURE setDatosVariables(p_lineaseguroid IN NUMBER,
                              p_iddatvar IN NUMBER,
                              p_idcapaseg    IN NUMBER);

  PROCEDURE generarPolizaFromCopy(P_REFERENCIA IN VARCHAR2,
                                  P_TIPOREFERENCIA IN VARCHAR2,
                                  P_CLASE IN NUMBER,
                                  P_IDCOPY IN NUMBER,
                                  P_IDPOLIZA OUT NUMBER);

  PROCEDURE setIncrementosComplementario(P_IDPOLIZA IN NUMBER,
                                         P_IDCOPY IN NUMBER);


END PQ_SITUACION_ACTUALIZADA;
/
CREATE OR REPLACE PACKAGE BODY o02agpe0.PQ_SITUACION_ACTUALIZADA IS

  /*
   * arrastre_parcelas_copy.
  * P_LINEASEGUROID : lineaSeguroId de la poliza que estamos creando
  * P_IDPOLIZA : ID de la poliza COPY.
  * P_NIFCIF : nifcif de la poliza que estamos creando
  * P_CLASE : clase  de la poliza que estamos creando
  * P_CODLINEA: Linea de la poliza copy
  * P_PLAN: plan de la poliza que estamos creando MENOS 1 (plan del año anterior)
  */
  PROCEDURE arrastre_parcelas_copy(P_LINEASEGUROID IN NUMBER,
                                   P_IDCOPY        IN NUMBER,
                                   P_IDPOLIZA_DEST IN NUMBER,
                                   P_CLASE         IN NUMBER) IS
    TYPE TpCursor IS REF CURSOR;
    l_tp_cursor     TpCursor;
    l_clase_cursor  TpCursor;
    l_sql           VARCHAR2(2000);
    l_sql_clase     VARCHAR2(2000);
    l_idcopyparcela NUMBER;
    -- datos del filtro clase detalle --
    l_codprovincia NUMBER;
    l_codtermino NUMBER;
    l_codcomarca NUMBER;
    l_subtermino CHARACTER;
    l_codcultivo NUMBER;
    l_codvariedad NUMBER;
    --  datos de la copy --
    l_codvariedad_copy NUMBER;
    l_codcultivo_copy NUMBER;
    l_codprovincia_copy NUMBER;
    l_codtermino_copy NUMBER;
    l_codcomarca_copy NUMBER;
    l_subtermino_copy CHARACTER;
    -- resultado aplicar filtro
    is_provincia_ok BOOLEAN := FALSE;
    is_comarca_ok BOOLEAN := FALSE;
    is_termino_ok BOOLEAN := FALSE;
    is_subtermino_ok BOOLEAN := FALSE;
    is_cultivo_ok BOOLEAN := FALSE;
    is_variedad_ok BOOLEAN := FALSE;



  BEGIN
    BEGIN
    
    -- Arrastro las Parcelas de la Copy
    IF (P_IDCOPY IS NOT NULL) THEN
      l_sql := 'select id from tb_copy_parcelas where idcopy = ' || P_IDCOPY;

      l_sql_clase := 'select codprovincia, codtermino, codcomarca, subtermino, codcultivo, codvariedad
      from TB_CLASE c, TB_CLASE_DETALLE d where c.id = d.idclase and c.clase =' || P_CLASE || ' AND c.lineaseguroid = ' || P_LINEASEGUROID;



     -- un bucle por parcela --
      OPEN l_tp_cursor FOR l_sql;
      LOOP FETCH l_tp_cursor INTO l_idcopyparcela;
           EXIT WHEN l_tp_cursor%NOTFOUND;
           
                -- obtengo los datos de la parcela que se va a arrastrar

                SELECT codprovincia, codtermino, codcomarca, nvl(subtermino, ' '), codcultivo, codvariedad
                INTO l_codprovincia_copy, l_codtermino_copy, l_codcomarca_copy, l_subtermino_copy, l_codcultivo_copy, l_codvariedad_copy
                FROM tb_copy_parcelas WHERE id = l_idcopyparcela;

               -- un bucle por clase detalle --
               OPEN l_clase_cursor FOR l_sql_clase;
               LOOP FETCH l_clase_cursor INTO l_codprovincia, l_codtermino, l_codcomarca,l_subtermino, l_codcultivo, l_codvariedad;
               EXIT WHEN l_clase_cursor%NOTFOUND;
                   -- provincia --
                   IF (l_codprovincia = l_codprovincia_copy OR l_codprovincia = 99 OR l_codprovincia_copy = 99) THEN
                       is_provincia_ok := true;
                   END IF;
                   -- comarca --
                   IF (l_codcomarca = l_codcomarca_copy OR l_codcomarca = 99 OR l_codcomarca_copy = 99) THEN
                       is_comarca_ok := true;
                   END IF;
                   -- termino --
                   IF (l_codtermino = l_codtermino_copy OR l_codtermino = 999 OR l_codtermino_copy = 999) THEN
                       is_termino_ok := true;
                   END IF;
                   -- subtermino --
                   IF (l_subtermino = l_subtermino_copy OR l_subtermino = '9' OR l_subtermino_copy = '9') THEN
                       is_subtermino_ok := true;
                   END IF;
                   -- cultivo --
                   IF (l_codcultivo = l_codcultivo_copy OR l_codcultivo = 999 OR l_codcultivo_copy = 999) THEN
                       is_cultivo_ok := true;
                   END IF;
                   -- variedad --
                   IF (l_codvariedad = l_codvariedad_copy OR l_codvariedad = 999 OR l_codvariedad_copy = 999) THEN
                       is_variedad_ok := true;
                   END IF;

               END LOOP; -- fin bucle clase
               
               IF is_provincia_ok AND
                  is_comarca_ok AND
                  is_termino_ok AND
                  is_subtermino_ok AND
                  is_cultivo_ok AND
                  is_variedad_ok
               THEN
                   setParcelaCopy(P_LINEASEGUROID, P_IDPOLIZA_DEST, l_idcopyparcela); -- Guardo la Parcela
               END IF;
               is_provincia_ok := false;
               is_comarca_ok := false;
               is_termino_ok := false;
               is_subtermino_ok := false;
               is_cultivo_ok := false;
               is_variedad_ok := false;

      END LOOP; -- fin bucle por parcela

      --Actualizamos las instalaciones que pueda haber
      setInstalacionesCopy(P_IDPOLIZA_DEST);

      COMMIT;

    END IF;



    EXCEPTION

      WHEN ACCESS_INTO_NULL THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN CASE_NOT_FOUND THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN COLLECTION_IS_NULL THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN CURSOR_ALREADY_OPEN THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN DUP_VAL_ON_INDEX THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN INVALID_CURSOR THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN INVALID_NUMBER THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN LOGIN_DENIED THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN NO_DATA_FOUND THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN NOT_LOGGED_ON THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN PROGRAM_ERROR THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN ROWTYPE_MISMATCH THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN SELF_IS_NULL THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN STORAGE_ERROR THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN SUBSCRIPT_BEYOND_COUNT THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN SUBSCRIPT_OUTSIDE_LIMIT THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN SYS_INVALID_ROWID THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN TIMEOUT_ON_RESOURCE THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN TOO_MANY_ROWS THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN VALUE_ERROR THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN ZERO_DIVIDE THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;

    END;-- FIN Exception --
    
  END arrastre_parcelas_copy;

  /*
  *
  * setParcela
  *
  */
  PROCEDURE setParcelaCopy(p_lineaseguroid IN NUMBER,
                           p_idpoliza      IN NUMBER,
                           p_idcopyparcela IN NUMBER) IS
    TYPE TpCursor IS REF CURSOR;
    l_tp_cursor     TpCursor;
    l_sql           VARCHAR2(2000);
    l_idcopycapaseg NUMBER;
    l_newidparcela  NUMBER;

    l_CODCONCEPTOPPALMOD tb_datos_var_parcela.codconceptoppalmod%TYPE;
    l_CODRIESGOCUBIERTO  tb_datos_var_parcela.codriesgocubierto%TYPE;
    l_LINEASEGUROID      TB_PARCELAS_COBERTURAS.LINEASEGUROID%TYPE;
    l_CODMODULO          TB_PARCELAS_COBERTURAS.CODMODULO%TYPE;

    l_cnt_cobertura      number := 0;

  BEGIN

    -- Arrastramos la Parcela de la Copy a la poliza
    INSERT INTO tb_parcelas
      (idparcela,
       idpoliza,
       codprovincia,
       codtermino,
       subtermino,
       poligono,
       parcela,
       codprovsigpac,
       codtermsigpac,
       agrsigpac,
       zonasigpac,
       poligonosigpac,
       parcelasigpac,
       recintosigpac,
       nomparcela,
       codcultivo,
       codvariedad,
       hoja,
       numero,
       codcomarca,
       lineaseguroid)
      SELECT SQ_PARCELAS.Nextval as idparcela,
             p_idpoliza,
             codprovincia,
             codtermino,
             nvl(subtermino, ' '),
             poligono,
             parcela,
             codprovsigpac,
             codtermsigpac,
             agrsigpac,
             zonasigpac,
             poligonosigpac,
             parcelasigpac,
             recintosigpac,
             nomparcela,
             codcultivo,
             codvariedad,
             hoja,
             numero,
             codcomarca,
             p_lineaseguroid
        FROM tb_copy_parcelas
       WHERE id = p_idcopyparcela;

    -- Recupero el id de la parcela creada
    execute immediate 'select SQ_PARCELAS.CURRVAL from DUAL'
      into l_newidparcela;

    -- Arrastramos el Capital Asegurado
    l_sql := 'select id from tb_copy_capitales_aseg where idparcela = ' ||
             p_idcopyparcela;

    OPEN l_tp_cursor FOR l_sql;
    LOOP FETCH l_tp_cursor INTO l_idcopycapaseg;
         EXIT WHEN l_tp_cursor%NOTFOUND;
      -- Guardo el capital Asegurado
      setCapitalAseguradoCopy(l_idcopycapaseg, l_newidparcela,p_idpoliza, p_lineaseguroid);
    END LOOP;

    /** SIGPE 6419 */
    /*-- Recorremos los módulos seleccionados para si hay alguno que lleve coberturas
    -- a nivel de parcela y para estos, insertamos el registro con el riesgo elegido
    -- si no lo tiene ya.
    PQ_UTL.log('setParcelaCopy - Comprobamos si hay que insertar el riesgo cubierto elegido ---', 2);
    OPEN l_tp_cursor FOR SELECT distinct mp.LINEASEGUROID, mp.CODMODULO, rcm.codconceptoppalmod, rcm.codriesgocubierto
                         FROM tb_modulos_poliza mp, TB_SC_C_RIESGO_CBRTO_MOD RCM
                         WHERE MP.LINEASEGUROID = RCM.LINEASEGUROID AND MP.CODMODULO = RCM.CODMODULO
                               AND RCM.NIVELECCION = 'D' AND mp.idpoliza = p_idpoliza;
    LOOP FETCH l_tp_cursor INTO l_LINEASEGUROID, l_codmodulo, l_CODCONCEPTOPPALMOD, l_CODRIESGOCUBIERTO;
        EXIT WHEN l_tp_cursor%NOTFOUND;

        --Comprobamos que no tengamos ya valor para esta combinación
        SELECT COUNT(*) INTO l_cnt_cobertura FROM TB_PARCELAS_COBERTURAS WHERE
            IDPARCELA = l_newidparcela;

        IF (l_cnt_cobertura = 0) THEN
            PQ_UTL.log('setParcelaCopy - Insertamos el riesgo cubierto elegido ---', 2);
            INSERT INTO TB_PARCELAS_COBERTURAS (ID, IDPARCELA, LINEASEGUROID, CODMODULO,
                CODCONCEPTOPPALMOD, CODRIESGOCUBIERTO, CODCONCEPTO, CODVALOR) VALUES
                (SQ_PARCELA_COBERTURA.NEXTVAL, l_newidparcela, l_LINEASEGUROID, l_CODMODULO,
                l_CODCONCEPTOPPALMOD, l_CODRIESGOCUBIERTO, 363, -1);

            END IF;

        END LOOP;*/

  EXCEPTION
    WHEN OTHERS THEN
       PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.setParcelaCopy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
       RAISE;

  END setParcelaCopy;

  /*
  *
  * setCapitalAsegurado
  *
  */
  PROCEDURE setCapitalAseguradoCopy(p_idcopycapaseg IN NUMBER,
                                    p_idparcela     IN NUMBER,
                                    p_idpoliza      IN NUMBER,
                                    p_lineaseguroid IN NUMBER) IS
    TYPE TpCursor IS REF CURSOR;
    l_tp_cursor    TpCursor;
    l_sql          VARCHAR2(2000);
    l_idcopydatvar NUMBER;
    l_newidcapaseg NUMBER;
    l_precio       NUMBER;
    l_produccion   NUMBER;
  BEGIN

    -- Arrastramos el Capital Asegurado de la Copy a la Poliza
    INSERT INTO tb_capitales_asegurados
      (idparcela, idcapitalasegurado, codtipocapital, superficie)
      SELECT p_idparcela,
             SQ_CAPITALES_ASEGURADOS.Nextval as idcapitalasegurado,
             codtipocapital,
             superficie
        FROM tb_copy_capitales_aseg
       WHERE id = p_idcopycapaseg;

    -- Recuperamos el id del Capital Asegurado creado
    execute immediate 'select SQ_CAPITALES_ASEGURADOS.CURRVAL from DUAL'
      into l_newidcapaseg;

    -- Arrastramos los Datos Variables del Capital Asegurado
    l_sql := 'select id from tb_copy_datos_var_parc where idcapitalasegurado = ' ||
             p_idcopycapaseg;


    OPEN l_tp_cursor FOR l_sql;
    LOOP FETCH l_tp_cursor INTO l_idcopydatvar;
         EXIT WHEN l_tp_cursor%NOTFOUND;

      -- Guardo los Datos Variables
      setDatosVariablesCopy(l_idcopydatvar, l_newidcapaseg, p_idparcela, P_IDPOLIZA, p_lineaseguroid);
    END LOOP;

    --Por cada capital asegurado guardo en tb_cap_aseg_rel_modulo
    SELECT precio,produccion
       INTO l_precio,l_produccion
       FROM tb_copy_capitales_aseg c
       WHERE c.id = p_idcopycapaseg;

       IF p_idpoliza IS NOT  NULL THEN
          setCapAsegRelModulo(l_newidcapaseg,p_idpoliza,l_precio,l_produccion);
       END IF;

  EXCEPTION
    WHEN OTHERS THEN
       PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.setCapitalAseguradoCopy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
       RAISE;

  END setCapitalAseguradoCopy;

  /*
  *
  * setDatosVariables
  *
  */
  PROCEDURE setDatosVariablesCopy(p_idcopydatvar      IN NUMBER,
                                  p_idcapaseg         IN NUMBER,
                                  P_IDPARCELA_DESTINO IN NUMBER,
                                  P_IDPOLIZA_DESTINO  IN NUMBER,
                                  p_lineaseguroid     IN NUMBER) IS

     l_existe             boolean := false;
     l_count              NUMBER;
     l_fecha_USA_ffg      number;
     l_fecha_USA          number;
     l_fecha_EUR_ffg      number;
     l_fecha_EUR          number;

     l_codconcepto        tb_datos_var_parcela.Codconcepto%TYPE;
     l_valor              tb_datos_var_parcela.valor%TYPE;
     l_CODCONCEPTOPPALMOD tb_datos_var_parcela.codconceptoppalmod%TYPE;
     l_CODRIESGOCUBIERTO  tb_datos_var_parcela.codriesgocubierto%TYPE;

     l_codvalor           tb_parcelas_coberturas.codvalor%TYPE;
     l_CODMODULO          TB_PARCELAS_COBERTURAS.CODMODULO%TYPE;
     l_LINEASEGUROID      TB_PARCELAS_COBERTURAS.LINEASEGUROID%TYPE;

     TYPE TpCursor        IS REF CURSOR;
     l_tp_cursor          TpCursor;

     l_cnt_cobertura      number := 0;

  BEGIN

    --Comprobamos que no tenemos ya ese codigo de concepto para el capital asegurado
    select count(*) into l_count from tb_datos_var_parcela
       where IDCAPITALASEGURADO = p_idcapaseg and CODCONCEPTO in (SELECT codconcepto
           FROM tb_copy_datos_var_parc dv
           WHERE id = p_idcopydatvar
       );

    if (l_count > 0) then
        l_existe := true;
    end if;

    if (l_existe = false) then
        -- Comprobamos el formato por si es fecha fin de garantia y viene en americano
        select count(*) into l_fecha_USA_ffg from tb_copy_datos_var_parc
           where id = p_idcopydatvar and codconcepto = 134 and
           REGEXP_LIKE(valor, '^[0-9]{4}/[0-9]{2}/[0-9]{2}$');

        -- Comprobamos el formato por si NO es fecha fin de garantia y viene en americano
        select count(*) into l_fecha_USA from tb_copy_datos_var_parc
           where id = p_idcopydatvar and codconcepto <> 134 and
           REGEXP_LIKE(valor, '^[0-9]{4}/[0-9]{2}/[0-9]{2}$');

        -- Comprobamos el formato por si es fecha fin de garantia y viene en europeo
        select count(*) into l_fecha_EUR_ffg from tb_copy_datos_var_parc
           where id = p_idcopydatvar and codconcepto = 134 and
           REGEXP_LIKE(valor, '^[0-9]{2}/[0-9]{2}/[0-9]{4}$');

        -- Comprobamos el formato por si NO es fecha fin de garantia y viene en europeo
        select count(*) into l_fecha_EUR from tb_copy_datos_var_parc
           where id = p_idcopydatvar and codconcepto <> 134 and
           REGEXP_LIKE(valor, '^[0-9]{2}/[0-9]{2}/[0-9]{4}$');

        if (l_fecha_USA > 0) then
            -- Es fecha en formato americano pero no sumamos nada
            SELECT codconcepto, to_char(to_date(valor, 'YYYY/MM/DD'), 'DD/MM/YYYY'), CODCONCEPTOPPALMOD, CODRIESGOCUBIERTO
            into l_codconcepto, l_valor, l_CODCONCEPTOPPALMOD, l_CODRIESGOCUBIERTO
            FROM tb_copy_datos_var_parc
            WHERE id = p_idcopydatvar and codconcepto in (
               select codconcepto from o02agpe0.tb_sc_oi_org_info oi
                   where oi.lineaseguroid = p_lineaseguroid and oi.codubicacion = 16 and oi.coduso = 31
           );
        elsif (l_fecha_USA_ffg > 0) then
            -- Es fecha de fin de garantias en formato americano => sumamos 12 meses
            SELECT codconcepto, to_char(add_months(to_date(valor, 'YYYY/MM/DD'), 12), 'DD/MM/YYYY'), CODCONCEPTOPPALMOD, CODRIESGOCUBIERTO
            into l_codconcepto, l_valor, l_CODCONCEPTOPPALMOD, l_CODRIESGOCUBIERTO
            FROM tb_copy_datos_var_parc
            WHERE id = p_idcopydatvar and codconcepto in (
               select codconcepto from o02agpe0.tb_sc_oi_org_info oi
                   where oi.lineaseguroid = p_lineaseguroid and oi.codubicacion = 16 and oi.coduso = 31
           );
        elsif (l_fecha_EUR > 0) then
           -- Es fecha en formato europeo pero no sumamos nada
           SELECT codconcepto, to_char(to_date(valor, 'DD/MM/YYYY'), 'DD/MM/YYYY'), CODCONCEPTOPPALMOD, CODRIESGOCUBIERTO
           into l_codconcepto, l_valor, l_CODCONCEPTOPPALMOD, l_CODRIESGOCUBIERTO
           FROM tb_copy_datos_var_parc
           WHERE id = p_idcopydatvar and codconcepto in (
               select codconcepto from o02agpe0.tb_sc_oi_org_info oi
                   where oi.lineaseguroid = p_lineaseguroid and oi.codubicacion = 16 and oi.coduso = 31
           );
        elsif (l_fecha_EUR_ffg > 0) then
           -- Es fecha de fin de garantias en formato europeo => sumamos 12 meses
            SELECT codconcepto, to_char(add_months(to_date(valor, 'DD/MM/YYYY'), 12), 'DD/MM/YYYY'), CODCONCEPTOPPALMOD, CODRIESGOCUBIERTO
            into l_codconcepto, l_valor, l_CODCONCEPTOPPALMOD, l_CODRIESGOCUBIERTO
            FROM tb_copy_datos_var_parc
            WHERE id = p_idcopydatvar and codconcepto in (
               select codconcepto from o02agpe0.tb_sc_oi_org_info oi
                   where oi.lineaseguroid = p_lineaseguroid and oi.codubicacion = 16 and oi.coduso = 31
           );
        else
            -- No es fecha
            SELECT codconcepto, valor, CODCONCEPTOPPALMOD, CODRIESGOCUBIERTO
            into l_codconcepto, l_valor, l_CODCONCEPTOPPALMOD, l_CODRIESGOCUBIERTO
            FROM tb_copy_datos_var_parc
            WHERE id = p_idcopydatvar and codconcepto in (
               select codconcepto from o02agpe0.tb_sc_oi_org_info oi
                   where oi.lineaseguroid = p_lineaseguroid and oi.codubicacion = 16 and oi.coduso = 31
           );
        end if;

        IF (l_codconcepto is not null) THEN
           IF (l_codconcepto = 111) THEN
               --Si es "edad", le sumamos 1 al valor
               select to_char(to_number(l_valor)+1) into l_valor from dual;
           ELSIF (l_codconcepto = 113) THEN
               --Si es "Fecha Siembra/transplante", le sumamos 1 año al valor
               SELECT to_char(add_months(to_date(l_valor, 'DD/MM/YYYY'), 12), 'DD/MM/YYYY') into l_valor from dual;
           END IF;

           IF (l_codconcepto <> 363) THEN
               INSERT INTO tb_datos_var_parcela (idcapitalasegurado, iddatovariable, codconcepto, valor, CODCONCEPTOPPALMOD, CODRIESGOCUBIERTO)
                   values (p_idcapaseg, SQ_DATOS_VAR_PARCELA.Nextval, l_codconcepto, l_valor, l_CODCONCEPTOPPALMOD, l_CODRIESGOCUBIERTO);
          /** SIGPE 6419 */
           /**ELSE
               --Riesgo cubierto elegido: insertamos en TB_PARCELAS_COBERTURAS

               --Según si se ha elegido o no, ponemos el valor correspondiente para la cobertura de parcelas
               IF (l_valor = 'S') THEN
                   l_codvalor := -1;
               ELSE
                   l_codvalor := -2;
               END IF;

               --Recorremos los módulos seleccionados para ver cual lleva coberturas a nivel de parcela y
               --para estos, insertamos el registro.
               OPEN l_tp_cursor FOR SELECT distinct mp.LINEASEGUROID, mp.CODMODULO
                                 FROM tb_modulos_poliza mp, TB_SC_C_RIESGO_CBRTO_MOD RCM
                                 WHERE MP.LINEASEGUROID = RCM.LINEASEGUROID AND MP.CODMODULO = RCM.CODMODULO
                                       AND RCM.NIVELECCION = 'D' AND mp.idpoliza = P_IDPOLIZA_DESTINO;
               LOOP FETCH l_tp_cursor INTO l_LINEASEGUROID, l_codmodulo;
                   EXIT WHEN l_tp_cursor%NOTFOUND;

                   --Puesto que los datos variables van a nivel de capital asegurado y las coberturas
                   --van a nivel de parcela, comprobamos que no tengamos ya valor para esta combinación
                   SELECT COUNT(*) INTO l_cnt_cobertura FROM TB_PARCELAS_COBERTURAS WHERE
                       IDPARCELA = P_IDPARCELA_DESTINO;

                   IF (l_cnt_cobertura = 0) THEN

                       INSERT INTO TB_PARCELAS_COBERTURAS (ID, IDPARCELA, LINEASEGUROID, CODMODULO,
                           CODCONCEPTOPPALMOD, CODRIESGOCUBIERTO, CODCONCEPTO, CODVALOR) VALUES
                           (SQ_PARCELA_COBERTURA.NEXTVAL, P_IDPARCELA_DESTINO, l_LINEASEGUROID, l_CODMODULO,
                           l_CODCONCEPTOPPALMOD, l_CODRIESGOCUBIERTO, l_codconcepto, l_codvalor);

                   END IF;

               END LOOP;*/
           END IF;
        END IF;

    end if;

  EXCEPTION
    WHEN NO_DATA_FOUND THEN
       PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.setDatosVariablesCopy - El dato variable no aplica para esta linea ****', 2);
    WHEN OTHERS THEN
       PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.setDatosVariablesCopy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
       RAISE;

  END setDatosVariablesCopy;

  /*
  *
  * setCapAsegRelModulo
  *
  */
  PROCEDURE setCapAsegRelModulo(p_idcapitalasegurado IN NUMBER,
                                p_idpoliza           IN NUMBER,
                                p_precio             IN NUMBER,
                                p_produccion         IN NUMBER) IS

     TYPE TpCursor   IS REF CURSOR;
     l_tp_cursor     TpCursor;
     l_codmodulo     TB_MODULOS_POLIZA.CODMODULO%TYPE;

  BEGIN

     --PQ_UTL.log('setCapAsegRelModulo: ' || l_newidcapaseg || ', ' || p_idpoliza || ', ' || l_precio || ', ' || l_produccion || ' ****', 2);

     -- Obtenemos los módulos de la póliza y hacemos un insert por cada uno de ellos
     --consultamos el modulo de la poliza
     OPEN l_tp_cursor FOR SELECT codmodulo FROM tb_modulos_poliza mp WHERE mp.idpoliza = P_idpoliza;
     LOOP FETCH l_tp_cursor INTO l_codmodulo;
        EXIT WHEN l_tp_cursor%NOTFOUND;
        -- Arrastramos el Dato Variable de la Copy a la Poliza
        INSERT INTO tb_cap_aseg_rel_modulo (id,idcapitalasegurado, codmodulo, precio, produccion)
           VALUES (SQ_TB_CAP_ASEG_REL_MODULO.Nextval,p_idcapitalasegurado,l_codmodulo,p_precio,p_produccion);

     END LOOP;

  END setCapAsegRelModulo;

  -----------------------------------------------------------------------------
  -- Procedimiento para actualizar los datos de las instalaciones de la copy --
  -----------------------------------------------------------------------------
  PROCEDURE setInstalacionesCopy(P_IDPOLIZA_DEST     IN NUMBER) IS

      TYPE TpCursor     IS REF CURSOR;
      l_tp_cursor       TpCursor;
      --Datos de la instalación
      l_idparcela_inst  TB_PARCELAS.IDPARCELA%TYPE;
      l_provsigpac_inst TB_PARCELAS.CODPROVSIGPAC%TYPE;
      l_termsigpac_inst TB_PARCELAS.CODTERMSIGPAC%TYPE;
      l_agrsigpac_inst  TB_PARCELAS.AGRSIGPAC%TYPE;
      l_zonasigpac_inst TB_PARCELAS.ZONASIGPAC%TYPE;
      l_polsigpac_inst  TB_PARCELAS.POLIGONOSIGPAC%TYPE;
      l_parcsigpac_inst TB_PARCELAS.PARCELASIGPAC%TYPE;
      l_recsigpac_inst  TB_PARCELAS.RECINTOSIGPAC%TYPE;
      l_cultivo_inst    TB_PARCELAS.CODCULTIVO%TYPE;
      l_variedad_inst   TB_PARCELAS.CODVARIEDAD%TYPE;
      l_lineaseguroid_inst   TB_PARCELAS.LINEASEGUROID%TYPE;
      --Datos de la parcela asociada a la instalación
      l_idparcela       TB_PARCELAS.IDPARCELA%TYPE;
      l_provsigpac      TB_PARCELAS.CODPROVSIGPAC%TYPE;
      l_termsigpac      TB_PARCELAS.CODTERMSIGPAC%TYPE;
      l_agrsigpac       TB_PARCELAS.AGRSIGPAC%TYPE;
      l_zonasigpac      TB_PARCELAS.ZONASIGPAC%TYPE;
      l_polsigpac       TB_PARCELAS.POLIGONOSIGPAC%TYPE;
      l_parcsigpac      TB_PARCELAS.PARCELASIGPAC%TYPE;
      l_recsigpac       TB_PARCELAS.RECINTOSIGPAC%TYPE;
      l_cultivo         TB_PARCELAS.CODCULTIVO%TYPE;
      l_variedad        TB_PARCELAS.CODVARIEDAD%TYPE;

  BEGIN
      -- Busco las parcelas que tienen instalaciones
      OPEN l_tp_cursor FOR SELECT PAR.idparcela, PAR.CODPROVSIGPAC, PAR.CODTERMSIGPAC,
                                  PAR.AGRSIGPAC, PAR.ZONASIGPAC, PAR.POLIGONOSIGPAC,
                                  PAR.PARCELASIGPAC, PAR.RECINTOSIGPAC, PAR.CODCULTIVO, PAR.CODVARIEDAD, PAR.LINEASEGUROID
                           FROM TB_PARCELAS PAR, TB_CAPITALES_ASEGURADOS CA
                           WHERE PAR.IDPARCELA = CA.IDPARCELA AND CA.CODTIPOCAPITAL >= 100
                                 AND PAR.IDPOLIZA = P_IDPOLIZA_DEST;
      LOOP FETCH l_tp_cursor INTO l_idparcela_inst, l_provsigpac_inst, l_termsigpac_inst,
                                  l_agrsigpac_inst, l_zonasigpac_inst, l_polsigpac_inst,
                                  l_parcsigpac_inst, l_recsigpac_inst, l_cultivo_inst, l_variedad_inst,l_lineaseguroid_inst;
          EXIT WHEN l_tp_cursor%NOTFOUND;
          --Busco la parcela que coincide con esos datos y cuyo TC < 100
          SELECT PAR.idparcela, PAR.CODPROVSIGPAC, PAR.CODTERMSIGPAC, PAR.AGRSIGPAC, PAR.ZONASIGPAC,
                 PAR.POLIGONOSIGPAC, PAR.PARCELASIGPAC, PAR.RECINTOSIGPAC, PAR.CODCULTIVO, PAR.CODVARIEDAD
              INTO l_idparcela, l_provsigpac, l_termsigpac, l_agrsigpac, l_zonasigpac, l_polsigpac, l_parcsigpac,
                   l_recsigpac, l_cultivo, l_variedad
              FROM TB_PARCELAS PAR, TB_CAPITALES_ASEGURADOS CA
              WHERE PAR.IDPARCELA = CA.IDPARCELA AND CA.CODTIPOCAPITAL < 100 AND PAR.CODPROVSIGPAC = l_provsigpac_inst
                    AND PAR.CODTERMSIGPAC = l_termsigpac_inst AND PAR.AGRSIGPAC = l_agrsigpac_inst AND
                    PAR.ZONASIGPAC = l_zonasigpac_inst AND PAR.POLIGONOSIGPAC = l_polsigpac_inst AND
                    PAR.PARCELASIGPAC = l_parcsigpac_inst AND PAR.RECINTOSIGPAC = l_recsigpac_inst AND
                    PAR.CODCULTIVO = l_cultivo_inst AND PAR.CODVARIEDAD = l_variedad_inst
                    AND PAR.IDPOLIZA = P_IDPOLIZA_DEST AND ROWNUM = 1;
          --Si la encuentro, actualizo los datos de la instalación
          IF (l_idparcela IS NOT NULL) THEN
              UPDATE TB_PARCELAS PAR SET PAR.TIPOPARCELA = 'E', PAR.IDPARCELAESTRUCTURA = l_idparcela WHERE PAR.IDPARCELA = l_idparcela_inst;
          END IF;

      END LOOP;

  END setInstalacionesCopy;



  /*
  *
  * arrastre_parcelas
  * P_LINEASEGUROID lineaSeguroId de la poliza que estamos creando
  * P_IDPOLIZA      idPoliza de la poliza que estamos creando
  * P_NIFCIF        nifcif de la poliza que estamos creando
  * P_CLASE         clase de la poliza que estamos creando
  * P_CODLINEA      codLinea de la poliza que estamos creando
  * P_PLAN          plan de la poliza que estamos creando MENOS 1 (plan del año anterior)
  *
  */
  PROCEDURE arrastre_parcelas(P_LINEASEGUROID IN NUMBER,
                              P_IDPOLIZA_ORIG IN NUMBER,
                              P_IDPOLIZA_DEST IN NUMBER,
                              P_CLASE         IN NUMBER) IS
    TYPE TpCursor IS REF CURSOR;
    l_tp_cursor     TpCursor;
    l_clase_cursor  TpCursor;
    l_sql           VARCHAR2(2000);
    l_sql_clase     VARCHAR2(2000);
    l_idparcela NUMBER;
    -- datos del filtro clase detalle --
    l_codprovinciac NUMBER;
    l_codterminoc NUMBER;
    l_codcomarcac NUMBER;
    l_subterminoc CHARACTER;
    l_codcultivoc NUMBER;
    l_codvariedadc NUMBER;
    --  datos de la poliza --
    l_codvariedad NUMBER;
    l_codcultivo NUMBER;
    l_codprovincia NUMBER;
    l_codtermino NUMBER;
    l_codcomarca NUMBER;
    l_subtermino CHARACTER;
    -- resultado aplicar filtro
    is_provincia_ok BOOLEAN := FALSE;
    is_comarca_ok BOOLEAN := FALSE;
    is_termino_ok BOOLEAN := FALSE;
    is_subtermino_ok BOOLEAN := FALSE;
    is_cultivo_ok BOOLEAN := FALSE;
    is_variedad_ok BOOLEAN := FALSE;



  BEGIN
    BEGIN

     -- Arrastro las Parcelas
     IF (P_IDPOLIZA_ORIG IS NOT NULL) THEN
        -- Obtenemos los identificadores de las parcelas a copiar
        l_sql := 'select idparcela from tb_parcelas where idpoliza = ' || P_IDPOLIZA_ORIG;

        l_sql_clase := 'select codprovincia, codtermino, codcomarca, subtermino, codcultivo, codvariedad
                     from TB_CLASE c, TB_CLASE_DETALLE d where c.id = d.idclase and c.clase =' ||
                     P_CLASE || ' AND c.lineaseguroid = ' || P_LINEASEGUROID;

     -- un bucle por parcela --
      OPEN l_tp_cursor FOR l_sql;
      LOOP FETCH l_tp_cursor INTO l_idparcela;
           EXIT WHEN l_tp_cursor%NOTFOUND;

               -- obtengo los datos de la parcela que se va a arrastrar
                SELECT codprovincia, codtermino, codcomarca, subtermino, codcultivo, codvariedad
                INTO l_codprovincia, l_codtermino, l_codcomarca, l_subtermino, l_codcultivo, l_codvariedad
                FROM tb_parcelas WHERE idparcela = l_idparcela;

               -- un bucle por clase detalle --
               OPEN l_clase_cursor FOR l_sql_clase;
               LOOP FETCH l_clase_cursor INTO l_codprovinciac, l_codterminoc, l_codcomarcac,l_subterminoc, l_codcultivoc, l_codvariedadc;
               EXIT WHEN l_clase_cursor%NOTFOUND;
                   -- provincia --
                   IF (l_codprovinciac = l_codprovincia OR l_codprovinciac = 99 OR l_codprovincia = 99) THEN
                       is_provincia_ok := true;
                   END IF;
                   -- comarca --
                   IF (l_codcomarcac = l_codcomarca OR l_codcomarcac = 99 OR l_codcomarca = 99) THEN
                       is_comarca_ok := true;
                   END IF;
                   -- termino --
                   IF (l_codterminoc = l_codtermino OR l_codterminoc = 999 OR l_codtermino = 999) THEN
                       is_termino_ok := true;
                   END IF;
                   -- subtermino --
                   IF (l_subterminoc = l_subtermino OR l_subterminoc = '9' OR l_subtermino = '9') THEN
                       is_subtermino_ok := true;
                   END IF;
                   -- cultivo --
                   IF (l_codcultivoc = l_codcultivo OR l_codcultivoc = 999 OR l_codcultivo = 999) THEN
                       is_cultivo_ok := true;
                   END IF;
                   -- variedad --
                   IF (l_codvariedadc = l_codvariedad OR l_codvariedadc= 999 OR l_codvariedad = 999) THEN
                       is_variedad_ok := true;
                   END IF;

               END LOOP; -- fin bucle clase


               IF is_provincia_ok AND
                  is_comarca_ok AND
                  is_termino_ok AND
                  is_subtermino_ok AND
                  is_cultivo_ok AND
                  is_variedad_ok
               THEN
                   setParcela(P_LINEASEGUROID, P_IDPOLIZA_DEST, l_idparcela); -- Guardo la Parcela
               END IF;
               is_provincia_ok := false;
               is_comarca_ok := false;
               is_termino_ok := false;
               is_subtermino_ok := false;
               is_cultivo_ok := false;
               is_variedad_ok := false;

      END LOOP; -- fin bucle por parcela
    END IF;



    EXCEPTION

      WHEN ACCESS_INTO_NULL THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN CASE_NOT_FOUND THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN COLLECTION_IS_NULL THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN CURSOR_ALREADY_OPEN THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN DUP_VAL_ON_INDEX THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN INVALID_CURSOR THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN INVALID_NUMBER THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN LOGIN_DENIED THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN NO_DATA_FOUND THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN NOT_LOGGED_ON THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN PROGRAM_ERROR THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN ROWTYPE_MISMATCH THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN SELF_IS_NULL THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN STORAGE_ERROR THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN SUBSCRIPT_BEYOND_COUNT THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN SUBSCRIPT_OUTSIDE_LIMIT THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN SYS_INVALID_ROWID THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN TIMEOUT_ON_RESOURCE THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN TOO_MANY_ROWS THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN VALUE_ERROR THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;
      WHEN ZERO_DIVIDE THEN
           PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.arrastre_parcelas - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
           ROLLBACK;


    END;-- FIN Exception --


    COMMIT;


  END arrastre_parcelas;

  /*
  *
  * setParcela
  *
  */
  PROCEDURE setParcela(p_lineaseguroid IN NUMBER,
                       p_idpoliza      IN NUMBER,
                       p_idparcela IN NUMBER) IS
    TYPE TpCursor IS REF CURSOR;
    l_tp_cursor     TpCursor;
    l_sql           VARCHAR2(170);
    l_idcapaseg NUMBER;
    l_newidparcela  NUMBER;
  BEGIN

    -- Arrastramos la Parcela a la poliza
    INSERT INTO tb_parcelas
      (idparcela,
       idpoliza,
       codprovincia,
       codtermino,
       subtermino,
       poligono,
       parcela,
       codprovsigpac,
       codtermsigpac,
       agrsigpac,
       zonasigpac,
       poligonosigpac,
       parcelasigpac,
       recintosigpac,
       nomparcela,
       codcultivo,
       codvariedad,
       hoja,
       numero,
       codcomarca,
       lineaseguroid,
       idparcelaestructura,
       tipoparcela,
       altaencomplementario,
       ind_recalculo_hoja_numero,
       idparcela_pac)
      SELECT SQ_PARCELAS.Nextval as idparcela,
             p_idpoliza,
             codprovincia,
             codtermino,
             subtermino,
             poligono,
             parcela,
             codprovsigpac,
             codtermsigpac,
             agrsigpac,
             zonasigpac,
             poligonosigpac,
             parcelasigpac,
             recintosigpac,
             nomparcela,
             codcultivo,
             codvariedad,
             hoja,
             numero,
             codcomarca,
             p_lineaseguroid,
             idparcelaestructura,
             tipoparcela,
             altaencomplementario,
             ind_recalculo_hoja_numero,
             idparcela_pac
        FROM tb_parcelas
       WHERE idparcela = p_idparcela;

    -- Recupero el id de la parcela creada
    execute immediate 'select SQ_PARCELAS.CURRVAL from DUAL'
      into l_newidparcela;

    -- Arrastramos el Capital Asegurado
    l_sql := 'select idcapitalasegurado from tb_capitales_asegurados where idparcela = ' ||
             p_idparcela;

    OPEN l_tp_cursor FOR l_sql;
    LOOP FETCH l_tp_cursor INTO l_idcapaseg;
         EXIT WHEN l_tp_cursor%NOTFOUND;
      -- Guardo el capital Asegurado
      setCapitalAsegurado(p_lineaseguroid, l_idcapaseg, l_newidparcela, p_idpoliza);
    END LOOP;

  EXCEPTION
    WHEN OTHERS THEN
       PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.setParcela - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
       RAISE;

  END setParcela;

  /*
  *
  * setCapitalAsegurado
  *
  */
  PROCEDURE setCapitalAsegurado(p_lineaseguroid IN NUMBER,
                                p_idcapaseg IN NUMBER,
                                p_idparcela IN NUMBER,
                                p_idpoliza  IN NUMBER) IS
    TYPE TpCursor IS REF CURSOR;
    l_tp_cursor    TpCursor;
    l_sql          VARCHAR2(170);
    l_iddatvar NUMBER;
    l_newidcapaseg NUMBER;
    l_precio NUMBER;
    l_produccion NUMBER;
  BEGIN

    -- Arrastramos el Capital Asegurado a la Poliza
    INSERT INTO tb_capitales_asegurados
      (idparcela, idcapitalasegurado, codtipocapital, superficie)
      SELECT p_idparcela,
             SQ_CAPITALES_ASEGURADOS.Nextval as idcapitalasegurado,
             codtipocapital,
             superficie
        FROM tb_capitales_asegurados
       WHERE idcapitalasegurado = p_idcapaseg;

    -- Recuperamos el id del Capital Asegurado creado
    execute immediate 'select SQ_CAPITALES_ASEGURADOS.CURRVAL from DUAL'
      into l_newidcapaseg;

    -- Arrastramos los Datos Variables del Capital Asegurado
    l_sql := 'select iddatovariable from tb_datos_var_parcela where idcapitalasegurado = ' ||
             p_idcapaseg;

    OPEN l_tp_cursor FOR l_sql;
    LOOP FETCH l_tp_cursor INTO l_iddatvar;
         EXIT WHEN l_tp_cursor%NOTFOUND;
      -- Guardo los Datos Variables
      setDatosVariables(p_lineaseguroid, l_iddatvar, l_newidcapaseg);
    END LOOP;
    --Por cada capital asegurado guardo en tb_cap_aseg_rel_modulo
    SELECT precio,produccion
       INTO l_precio,l_produccion
       FROM o02agpe0.tb_cap_aseg_rel_modulo
       WHERE idcapitalasegurado = p_idcapaseg;

       IF p_idpoliza IS NOT  NULL THEN
          setCapAsegRelModulo(l_newidcapaseg,p_idpoliza,l_precio,l_produccion);
       END IF;

  EXCEPTION
    WHEN OTHERS THEN
       PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.setCapitalAsegurado - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
       RAISE;

  END setCapitalAsegurado;

  /*
  *
  * setDatosVariables
  *
  */
  PROCEDURE setDatosVariables(p_lineaseguroid IN NUMBER,
                              p_iddatvar IN NUMBER,
                              p_idcapaseg    IN NUMBER) IS

     l_existe      boolean := false;
     l_count       NUMBER;

     l_codconcepto number;
     l_valor       varchar2(2000);

  BEGIN

    --Comprobamos que no tenemos ya ese codigo de concepto para el capital asegurado
    select count(*) into l_count from tb_datos_var_parcela
       where IDCAPITALASEGURADO = p_idcapaseg and CODCONCEPTO = (SELECT codconcepto
           FROM tb_datos_var_parcela
           WHERE iddatovariable = p_iddatvar
       );

    if (l_count > 0) then
        l_existe := true;
    end if;

    if (l_existe = false) then
        SELECT codconcepto, valor
        into l_codconcepto, l_valor
        FROM tb_datos_var_parcela
        WHERE iddatovariable = p_iddatvar and codconcepto in (
            select codconcepto from o02agpe0.tb_sc_oi_org_info oi
            where oi.lineaseguroid = p_lineaseguroid and oi.codubicacion = 16 and oi.coduso = 31);

        if (l_codconcepto is not null) then
           --Si es "edad", le sumamos 1 al valor
           IF (l_codconcepto = 111) THEN
               select to_char(to_number(l_valor)+1) into l_valor from dual;
           END IF;

           --Si es "Fecha fin garantías", le sumamos 1 al año
           IF (l_codconcepto = 134) THEN
               select to_char(add_months(to_date(l_valor, 'DD/MM/YYYY'), 12), 'DD/MM/YYYY') into l_valor from dual;
           END IF;

           INSERT INTO tb_datos_var_parcela (idcapitalasegurado, iddatovariable, codconcepto, valor)
           values (p_idcapaseg, SQ_DATOS_VAR_PARCELA.Nextval, l_codconcepto, l_valor);
       end if;
    end if;

  EXCEPTION
    WHEN NO_DATA_FOUND THEN
       PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.setDatosVariables - El dato variable no aplica para esta linea ***', 2);
    WHEN OTHERS THEN
       PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.setDatosVariables - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
       RAISE;

  END setDatosVariables;

  PROCEDURE generarPolizaFromCopy(P_REFERENCIA IN VARCHAR2,
                                  P_TIPOREFERENCIA IN VARCHAR2,
                                  P_CLASE IN NUMBER,
                                  P_IDCOPY IN NUMBER,
                                  P_IDPOLIZA OUT NUMBER) IS

     v_idpoliza number;
     v_idpoliza_origen_cpl number;
     v_lineaseguroid number;
     v_idasegurado o02agpe0.Tb_Asegurados.ID%TYPE;
     v_idcolectivo o02agpe0.Tb_Colectivos.ID%TYPE;
     v_fechafirma date;
     v_total_superficie number;
     v_idpoliza_ppal number;

  BEGIN
     -- Insertar los datos de la póliza a partir de la copy
     PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.generarPolizaFromCopy - INICIO ****', 2);

     select o02agpe0.sq_polizas.nextval into v_idpoliza from dual;

     -- Busco el asegurado. Debe existir y contener todos los datos necesarios
     BEGIN
        select A.ID into v_idasegurado
          from o02agpe0.tb_asegurados a, o02agpe0.tb_copy_polizas cp, o02agpe0.tb_copy_asegurados ca
          where cp.id = ca.idcopy and a.nifcif = ca.nifcif
             and a.codentidad = to_number(substr(cp.codinternoentidad, 1, 4)) and ca.idcopy = P_IDCOPY;
     EXCEPTION
        WHEN OTHERS THEN
           v_idasegurado := '';
     END;
     -- Busco el colectivo. Debe existir y contener todos los datos necesarios
     BEGIN
        select C.ID into v_idcolectivo
          from o02agpe0.tb_colectivos c, o02agpe0.tb_copy_polizas cp, o02agpe0.tb_copy_colectivos cc
          where cp.id = cc.idcopy and c.idcolectivo = cc.refcolectivo
             and c.codentidad = to_number(substr(cp.codinternoentidad, 1, 4)) and cc.idcopy = P_IDCOPY;
     EXCEPTION
        WHEN OTHERS THEN
           v_idcolectivo := '';
     END;

     select cp.fechafirma into v_fechafirma from o02agpe0.tb_copy_polizas cp where cp.id = P_IDCOPY;

     if (v_idasegurado is not null AND v_idcolectivo is not null) then
        -- Inserto los datos básicos de póliza
       insert into o02agpe0.tb_polizas(
           select v_idpoliza, l.lineaseguroid, v_idasegurado, v_idcolectivo, null,
                  to_number(substr(cp.codinternoentidad, 5)), trim(cp.codmodulo), P_REFERENCIA, cp.costetomador,
                  8, null, cp.dcpoliza, null, P_TIPOREFERENCIA, 1, null, null, P_CLASE, 'N', 'N', 'N',
                  null, null, pa.fectransferencia, 'N', 'U028783', cp.fechafirma, 0, -1,null,null,null, null,
         				  null, null, null, null, null, null, null, null, null, null, null, null
           from o02agpe0.tb_copy_polizas cp, o02agpe0.tb_lineas l, o02agpe0.tb_copy_pago pa
           where l.codplan = cp.codplan and l.codlinea = cp.codlinea and cp.id = pa.idcopy and cp.id = P_IDCOPY
        );

        -- Insertar los valores en el historico de cambios de estado
        insert into o02agpe0.tb_polizas_historico_estados values (
           o02agpe0.sq_polizas_historico_estados.nextval, v_idpoliza, null, v_fechafirma, 1, null, null, null, null, null
        );
        insert into o02agpe0.tb_polizas_historico_estados values (
           o02agpe0.sq_polizas_historico_estados.nextval, v_idpoliza, null, v_fechafirma, 3, null, null, null, null, null
        );
        insert into o02agpe0.tb_polizas_historico_estados values (
           o02agpe0.sq_polizas_historico_estados.nextval, v_idpoliza, null, v_fechafirma, 5, null, null, null, null, null
        );
        insert into o02agpe0.tb_polizas_historico_estados values (
           o02agpe0.sq_polizas_historico_estados.nextval, v_idpoliza, null, v_fechafirma, 8, null, null, null, null, null
        );

        -- Inserto el módulo en TB_MODULOS_POLIZA
        insert into o02agpe0.tb_modulos_poliza (
           select v_idpoliza, lineaseguroid, trim(codmodulo), null,'N', null,o02agpe0.sq_modulos_poliza.nextval from o02agpe0.tb_polizas p where p.idpoliza = v_idpoliza
        );

        -- Inserto las comparativas
        -- Esto tenemos que hacerlo desde Java porque no puedo calcular todos los campos desde aquí

        -- Inserto las subvenciones??
        -- En principio parece que las subvenciones que vienen en la copy son de la distribución
        -- de costes, pero no tienen por qué haberse marcado en la pantalla.
        -- De momento NO las inserto

        -- Inserto los socios y sus subvenciones??
        -- De momento NO los inserto porque no se como van a venir

        -- Inserto la distribución de costes
        insert into o02agpe0.tb_distribucion_costes d (
          select o02agpe0.sq_distribucion_costes.nextval, v_idpoliza,
          cp.primacomercial, cp.primaneta, cp.costeneto, cp.costetomador, cp.bonifsistproteccion,
          cp.bonificacion, NULL, cp.dctoventanilla, cp.dctocolectivo, cp.clea, cp.recargo, null, null, null, null
          from o02agpe0.tb_copy_polizas cp where cp.id = P_IDCOPY
        );
        -- Los datos de la ditribución de costes de parcelas y subvenciones no se puede obtener del copy.

        -- Inserto los datos del pago??
        -- No se puede obtener de la copy, porque faltan datos

        commit;

        select lineaseguroid into v_lineaseguroid from o02agpe0.tb_polizas p where p.idpoliza = v_idpoliza;

        -- Inserto las parcelas
        if (P_TIPOREFERENCIA = 'P') then
           PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy(v_lineaseguroid, P_IDCOPY, v_idpoliza, P_CLASE);
        else
           --Obtengo el idpoliza origen y llamo al pl para copiar las parcleas de complementario
           select idpoliza into v_idpoliza_origen_cpl from o02agpe0.tb_polizas p where p.referencia = P_REFERENCIA and p.tiporef = 'P';
           PQ_COPIA_PARCELAS.copiarParcelasEnPolCpl(v_idpoliza_origen_cpl, v_idpoliza);
           --Asigno los incrementos a las parcelas marco las parcelas dadas de alta en el complementario
           setIncrementosComplementario(v_idpoliza, P_IDCOPY);
        end if;

        -- Actualizar el TOTALSUPERFICIE
        select sum(ca.superficie) into v_total_superficie
           from o02agpe0.tb_parcelas par, o02agpe0.tb_capitales_asegurados ca
           where par.idparcela = ca.idparcela and par.idpoliza = v_idpoliza;

        update o02agpe0.tb_polizas p set p.totalsuperficie = v_total_superficie where p.idpoliza = v_idpoliza;

        -- Actualizar IDPOLIZA_PPAL (si es complementaria)
        if (P_TIPOREFERENCIA = 'C') then
           -- Busco el idpoliza principal
           select idpoliza into v_idpoliza_ppal from o02agpe0.tb_polizas p
             where p.referencia = P_REFERENCIA and p.tiporef = 'P';
           update o02agpe0.tb_polizas p set p.idpoliza_ppal = v_idpoliza_ppal where p.idpoliza = v_idpoliza;
        end if;

        commit;

        P_IDPOLIZA := v_idpoliza;
     else
        -- El colectivo o el asegurado no existen
        PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.generarPolizaFromCopy - No existe el colectivo o el asegurado ****', 2);
     end if;

     PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.generarPolizaFromCopy - FIN ****', 2);
  EXCEPTION
    WHEN OTHERS THEN
       PQ_UTL.log('PQ_SITUACION_ACTUALIZADA.generarPolizaFromCopy - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
       rollback;
       RAISE;
  END generarPolizaFromCopy;

  PROCEDURE setIncrementosComplementario(P_IDPOLIZA IN NUMBER,
                                         P_IDCOPY IN NUMBER) IS
      TYPE TpCursor IS REF CURSOR;
      l_tp_cursor     TpCursor;
      l_sql           VARCHAR2(2000) := 'select ca.idcapitalasegurado, CCA.INCREMENTOPRODUCCION '||
                            'from o02agpe0.tb_parcelas pp, o02agpe0.tb_capitales_asegurados ca, ' ||
                            'o02agpe0.tb_copy_parcelas cp, o02agpe0.tb_copy_capitales_aseg cca '||
                            'where pp.idparcela = ca.idparcela and cp.id = cca.idparcela and '||
                            'pp.hoja = cp.hoja and pp.numero = cp.numero AND '||
                            'PP.ALTAENCOMPLEMENTARIO = ''S'' AND pp.idpoliza = ' || P_IDPOLIZA ||
                            'and cp.idcopy = ' || P_IDCOPY;

      v_idcapaseg    number;
      v_incremento   number;

  BEGIN
      --Actualizamos las parcelas con incremento
      update o02agpe0.tb_parcelas par set par.altaencomplementario = 'S' where par.idparcela in (
         select PP.idparcela from o02agpe0.tb_parcelas pp, o02agpe0.tb_copy_parcelas cp,
         o02agpe0.tb_copy_capitales_aseg cca where cp.id = cca.idparcela and
         pp.hoja = cp.hoja and pp.numero = cp.numero and CCA.INCREMENTOPRODUCCION IS NOT NULL AND
         CCA.INCREMENTOPRODUCCION > 0 AND
         pp.idpoliza = P_IDPOLIZA and cp.idcopy = P_IDCOPY
      );
      COMMIT;

      --Asignamos los incrementos
      OPEN l_tp_cursor FOR l_sql;
      LOOP FETCH l_tp_cursor INTO v_idcapaseg, v_incremento;
         EXIT WHEN l_tp_cursor%NOTFOUND;
         UPDATE o02agpe0.Tb_Capitales_Asegurados CA
            SET CA.INCREMENTOPRODUCCION = v_incremento, ca.altaencomplementario = 'S'
            WHERE ca.idcapitalasegurado = v_idcapaseg;
      END LOOP;
      COMMIT;
  END setIncrementosComplementario;

END PQ_SITUACION_ACTUALIZADA;
/
SHOW ERRORS;