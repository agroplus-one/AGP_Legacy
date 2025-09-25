SET DEFINE OFF;
SET SERVEROUTPUT ON;
CREATE OR REPLACE PACKAGE O02AGPE0.PQ_COPIAR_PARCELAS_A_ANEXO IS
/*******************************************************************************
   NAME:
   PURPOSE:    Copia las parcelas de la copy al anexo (si el anexo no tiene parcelas) si el
               anexo tiene idcopy distinto de null o '', si el idcopy es null copiara las
               parcelas de la poliza a la copy.

   REVISIONS:
   Ver        Date        Author           Description
   ---------  ----------  ----------------  ------------------------------------
   1.0        02/01/2011  T-SYSTEMS

   NOTES:

*******************************************************************************/


   -- MAIN (punto de entrada al pl/sql)
   PROCEDURE copiarParcelasEnAnexo(
                                 P_IDANEXO       IN NUMBER);


   -- Procedimientos COPY: si hay idcopy en el anexo, copiara las parcelas de la copy al anexo.


   -- main copy
   PROCEDURE copiarParcelasFromCopy(
                                 P_IDCOPY      IN NUMBER,
                                 P_IDANEXO     IN NUMBER,
                                 REFPARCELA    IN NUMBER);
   -- Paso 1
   PROCEDURE setParcelaFromCopy(
                                 p_idparcela     IN NUMBER,
                                 p_idanexo       IN NUMBER,
                                 refparcela      IN NUMBER);
   -- Paso 2
   PROCEDURE setCapitalAseguradoFromCopy(
                                 p_idcapaseg       IN NUMBER,
                                 p_idparcela_anexo IN NUMBER);
   -- Paso 3
   PROCEDURE setDatosVariablesFromCopy(
                                 p_idcapaseg        IN NUMBER,
                                 p_idcapaseg_anexo IN NUMBER);




    -- Procedimientos POLIZA: si no hay idcopy habra que copiar las parcelas de la poliza al anexo.

    -- main poliza
    PROCEDURE copiarParcelasFromPoliza(
                                 P_IDPOLIZA      IN NUMBER,
                                 P_IDANEXO       IN NUMBER,
                                 REFPARCELA      IN NUMBER);

    -- Paso 1
    PROCEDURE setParcelaFromPoliza(
                                 p_idparcela     IN NUMBER,
                                 p_idanexo       IN NUMBER,
                                 refparcela      IN NUMBER);

    -- Paso 2
    PROCEDURE setCapitalAseguradoFromPoliza(
                                 p_idcapaseg       IN NUMBER,
                                 p_idparcela_anexo IN NUMBER,
                                 id_parcela_poliza IN NUMBER);

    -- Paso 3
    PROCEDURE setDatosVariablesFromPoliza(
                                 p_iddatvar        IN NUMBER,
                                 p_idcapaseg_anexo IN NUMBER);

    -- UTILS
    FUNCTION getIdPolizaFromAnexo (P_IDANEXO IN NUMBER) RETURN NUMBER;

    FUNCTION getIdCopyPoliza(P_IDANEXO IN NUMBER) RETURN NUMBER;

    FUNCTION getIdParcelaEstrutura(l_idEstructura IN NUMBER, l_idAnexo IN NUMBER) RETURN NUMBER;

    FUNCTION  isParcela(p_idparcela IN NUMBER) RETURN BOOLEAN;


    -- MAIN (punto de entrada al pl/sql)
    -- NOTA:copia una parcela de poliza a anexo. Recibe como parametro el código a copiar.

    PROCEDURE copyParcelaFromPolizaToAnexo(COD_PARCELA_ANEXO  IN NUMBER,
                                           COD_PARCELA_POLIZA IN NUMBER,
                                           ID_ANEXO IN NUMBER,
                                           REFPARCELA IN NUMBER);


     -- MAIN (punto de entrada al pl/sql)
     -- NOTA:copia una parcela de copy a anexo. Recibe como parametro el código a copiar.

     PROCEDURE copyParcelaFromCopyToAnexo(COD_PARCELA_ANEXO IN NUMBER,
                                          COD_PARCELA_COPY IN NUMBER,
                                          ID_ANEXO IN NUMBER,
                                          REFPARCELA IN NUMBER);


     PROCEDURE setSubvencionCCAAToAnexo(idsubv_ccaa          IN NUMBER,
                                        idparcela_anexo      IN NUMBER,
                                        id_capital_asegurado IN NUMBER);


     PROCEDURE setSubvencionEnesaToAnexo(idsubv_enesa         IN NUMBER,
                                         idparcela_anexo      IN NUMBER,
                                         id_capital_asegurado IN NUMBER);


     PROCEDURE insertCoberturaToAnexo(idconertura             IN NUMBER,
                                      idparcela_anexo         IN NUMBER,
                                      id_capital_asegurado    IN NUMBER);


     -- borra una parcela de la tabla de parcelas del anexo
     PROCEDURE deleteParcelaInAnexo(COD_PARCELA IN NUMBER);


END PQ_COPIAR_PARCELAS_A_ANEXO;
/
CREATE OR REPLACE PACKAGE BODY O02AGPE0.PQ_COPIAR_PARCELAS_A_ANEXO IS

 /*
  * ---------------------------------------------------------------------------------------
  *                            MAIN - copiarParcelasToAnexo
  * ----------------------------------------------------------------------------------------
  */

 PROCEDURE copiarParcelasEnAnexo(P_IDANEXO IN NUMBER) IS

     v_idPoliza NUMBER(10);
     v_idCopy NUMBER(10);
 BEGIN
     BEGIN -- Exception


           v_idPoliza := getIdPolizaFromAnexo(P_IDANEXO);
           v_idCopy := getIdCopyPoliza(P_IDANEXO);

           --SELECT count(*)
           --INTO v_count
           --FROM tb_anexo_mod
           --WHERE id = P_IDANEXO AND (idcopy is not null);

           IF v_idCopy = 0 OR v_idCopy is null THEN -- si copy
               copiarParcelasFromPoliza(v_idPoliza,P_IDANEXO,null);
           ELSE                -- no copy
               copiarParcelasFromCopy(v_idCopy,P_IDANEXO,null);
           END IF;

      EXCEPTION
            WHEN OTHERS THEN
               ROLLBACK;
      END;-- Exception

    COMMIT;

  END copiarParcelasEnAnexo;


 /*
  * ---------------------------------------------------------------------------------------
  *                          Main Copy - copiarParcelasFromCopy
  * ---------------------------------------------------------------------------------------
  */
  PROCEDURE copiarParcelasFromCopy(P_IDCOPY    IN NUMBER,
                                   P_IDANEXO   IN NUMBER,
                                   REFPARCELA  IN NUMBER)  IS

      TYPE TpCursor IS REF CURSOR;
      l_tp_cursor      TpCursor;
      l_tp_cursor2     TpCursor;
      l_sql            VARCHAR2(60);
      l_sql2           VARCHAR2(1000);
      l_idparcela      NUMBER;
      idParcelaDeCopy  NUMBER;
      esParcela        BOOLEAN; -- 1 parcela, 2 instalacion --

  BEGIN
      BEGIN -- Exception --

          esParcela := false;

          -- Copio las parcelas con idPoliza = P_IDPOLIZA --
          IF (P_IDCOPY IS NOT NULL) THEN
              l_sql := 'select id from tb_copy_parcelas where idcopy = ' || P_IDCOPY;

              OPEN l_tp_cursor FOR l_sql;
              LOOP FETCH l_tp_cursor INTO l_idparcela;
                  EXIT WHEN l_tp_cursor%NOTFOUND;
                  setParcelaFromCopy(l_idparcela, P_IDANEXO, REFPARCELA); -- Copio la parcela --
              END LOOP;
          END IF;

          COMMIT;

          -- introduzco en las estructuras el IDPARCELAANXESTRUCTURA--
          IF (P_IDCOPY IS NOT NULL) THEN
              l_sql2 := 'select id from tb_anexo_mod_parcelas where idanexo = ' || P_IDANEXO;

              OPEN l_tp_cursor2 FOR l_sql2;
              LOOP FETCH l_tp_cursor2 INTO l_idparcela;
                  EXIT WHEN l_tp_cursor2%NOTFOUND;

                  esParcela := isParcela(l_idparcela);
                  -- solo si es estructura recupero el id de su parcela buscando en la tabla de parcelas copy --
                  IF NOT esParcela THEN
                      idParcelaDeCopy:= getIdParcelaEstrutura(l_idparcela,P_IDANEXO);
                      UPDATE tb_anexo_mod_parcelas SET IDPARCELAANXESTRUCTURA=idParcelaDeCopy WHERE id=l_idparcela;
                  END IF;

              END LOOP;
          END IF;

      EXCEPTION
            WHEN OTHERS THEN
               ROLLBACK;
      END;-- Exception

      COMMIT;

  END copiarParcelasFromCopy;


  /*
  * ---------------------------------------------------------------------------------------
  *                          MAIN - copyParcelaFromPolizaToAnexo
  * ---------------------------------------------------------------------------------------
  */

  PROCEDURE copyParcelaFromPolizaToAnexo(COD_PARCELA_ANEXO  IN NUMBER,
                                         COD_PARCELA_POLIZA IN NUMBER,
                                         ID_ANEXO IN NUMBER,
                                         REFPARCELA IN NUMBER)  IS


   BEGIN
      BEGIN -- Exception

          deleteParcelaInAnexo(COD_PARCELA_ANEXO);
          setParcelaFromPoliza(COD_PARCELA_POLIZA, ID_ANEXO,REFPARCELA); -- Copio la parcela

      EXCEPTION
            WHEN OTHERS THEN
               PQ_UTL.log('copyParcelaFromPolizaToAnexo - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
               ROLLBACK;
      END; -- Exception

      COMMIT;

   END copyParcelaFromPolizaToAnexo;


   /*
  * ---------------------------------------------------------------------------------------
  *                          MAIN - deleteParcelaInAnexo
  * ---------------------------------------------------------------------------------------
  */
    PROCEDURE deleteParcelaInAnexo(COD_PARCELA  IN NUMBER)  IS

    l_sql_p VARCHAR2(200);

    BEGIN
        -- Delete parcela
        l_sql_p := 'delete from tb_anexo_mod_parcelas where id = ' || COD_PARCELA;
        execute immediate l_sql_p;
    END deleteParcelaInAnexo;

  /*
  * ---------------------------------------------------------------------------------------
  *                          MAIN - copyParcelaFromCopyToAnexo
  * ---------------------------------------------------------------------------------------
  */

   PROCEDURE copyParcelaFromCopyToAnexo(COD_PARCELA_ANEXO IN NUMBER,
                                        COD_PARCELA_COPY IN NUMBER,
                                        ID_ANEXO IN NUMBER,
                                        REFPARCELA IN NUMBER)  IS


   BEGIN
      BEGIN -- Exception
          PQ_UTL.log('copyParcelaFromCopyToAnexo - INICIO ****', 2);
          deleteParcelaInAnexo(COD_PARCELA_ANEXO);
          setParcelaFromCopy(COD_PARCELA_COPY, ID_ANEXO,REFPARCELA); -- Copio la parcela
          PQ_UTL.log('copyParcelaFromCopyToAnexo - FIN ****', 2);
          COMMIT;
      EXCEPTION
            WHEN OTHERS THEN
               PQ_UTL.log('copyParcelaFromCopyToAnexo - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
               ROLLBACK;
      END; -- Exception

   END copyParcelaFromCopyToAnexo;

 /*
  * ---------------------------------------------------------------------------------------
  *                          1.COPY - setParcelaFromCopy
  * ---------------------------------------------------------------------------------------
  */
  PROCEDURE setParcelaFromCopy(p_idparcela  IN NUMBER,
                               p_idanexo    IN NUMBER,
                               refparcela   IN NUMBER) IS

    TYPE TpCursor IS REF CURSOR;
    l_tp_cursor           TpCursor;
    l_sql                 VARCHAR2(120);
    l_idcapaseg           NUMBER;
    l_newidparcela_anexo  NUMBER;
    l_codtipocapital      NUMBER;
    l_tipoParcela         VARCHAR2(1) := 'P';
    l_tp_cursorTp         TpCursor;
    l_sqlTp               VARCHAR2(120);



  BEGIN
    -- Calculamos el tipo parcela recorriendo los capitales asegurados y viendo si alguno es >= 100
    l_sqlTp := 'select codtipocapital from tb_copy_capitales_aseg where idparcela = ' || p_idparcela;
    OPEN l_tp_cursorTp FOR l_sqlTp;
    LOOP FETCH l_tp_cursorTp INTO l_codtipocapital;
        EXIT WHEN l_tp_cursorTp%NOTFOUND;

            IF l_codtipocapital >= 100 THEN -- es Instalacion
               l_tipoParcela := 'E';
            ELSE                            -- es Parcela
               l_tipoParcela := 'P';
            END IF;
    END LOOP;

    -- Copiamos la parcela de la copy al anexo
    INSERT INTO tb_anexo_mod_parcelas
      (id,
       idanexo,
       idparcela,
       idcopyparcela,
       tipomodificacion,
       hoja,
       numero,
       nomparcela,
       codprovincia,
       codcomarca,
       codtermino,
       subtermino,
       codcultivo,
       codvariedad,
       poligono,
       parcela,
       codprovsigpac,
       codtermsigpac,
       agrsigpac,
       zonasigpac,
       poligonosigpac,
       parcelasigpac,
       recintosigpac,
       tipoparcela,
       idparcelaanxestructura)

      SELECT
             SQ_TB_ANEXO_MOD_PARCELAS.Nextval as id,
             p_idanexo,
             null,
             p_idparcela,
             null,
             hoja,
             numero,
             nomparcela,
             codprovincia,
             codcomarca,
             codtermino,
             NVL(subtermino,' '),
             codcultivo,
             codvariedad,
             poligono,
             parcela,
             codprovsigpac,
             codtermsigpac,
             agrsigpac,
             zonasigpac,
             poligonosigpac,
             parcelasigpac,
             recintosigpac,
             l_tipoParcela,
             refparcela


       FROM tb_copy_parcelas
       WHERE id = p_idparcela;

    -- Recupero el id de la parcela creada
    execute immediate 'select SQ_TB_ANEXO_MOD_PARCELAS.CURRVAL from DUAL'
      into l_newidparcela_anexo;

    -- Copiamos los Capitales Asegurados
    l_sql := 'select id from tb_copy_capitales_aseg where idparcela = ' || p_idparcela;
    OPEN l_tp_cursor FOR l_sql;
    LOOP FETCH l_tp_cursor INTO l_idcapaseg;
        EXIT WHEN l_tp_cursor%NOTFOUND;
        setCapitalAseguradoFromCopy(l_idcapaseg, l_newidparcela_anexo); -- Copia el capital Asegurado
    END LOOP;

   END setParcelaFromCopy;


  /*
  * ---------------------------------------------------------------------------------------
  *                          2.COPY - setCapitalAseguradoFromCopy
  * ---------------------------------------------------------------------------------------
  */
  PROCEDURE setCapitalAseguradoFromCopy(p_idcapaseg       IN NUMBER,
                                        p_idparcela_anexo IN NUMBER) IS

  TYPE TpCursor IS REF CURSOR;
    l_tp_cursor          TpCursor;
    l_sql                VARCHAR2(32000);
    l_iddatvar           NUMBER;
    l_newidcapaseg_anexo NUMBER;

  BEGIN

    -- Copiamos el Capital Asegurado de la poliza al Anexo
    INSERT INTO tb_anexo_mod_capitales_aseg
      (id,
       idparcelaanexo,
       codtipocapital,
       superficie,
       precio,
       produccion)

      SELECT
          SQ_CAPITALES_ASEGURADOS.Nextval as idcapitalasegurado,
          p_idparcela_anexo,
          codtipocapital,
          superficie,
          precio,
          produccion

        FROM tb_copy_capitales_aseg
       WHERE id = p_idcapaseg;

    -- Recuperamos el id del Capital Asegurado creado
    execute immediate 'select SQ_CAPITALES_ASEGURADOS.CURRVAL from DUAL'
      into l_newidcapaseg_anexo;

    -- Copiamos los Datos Variables Sin fecha del Capital Asegurado
    /*l_sql := 'select id from tb_copy_datos_var_parc where idcapitalasegurado = ' || p_idcapaseg;
    OPEN l_tp_cursor FOR l_sql;
    LOOP FETCH l_tp_cursor INTO l_iddatvar;
        EXIT WHEN l_tp_cursor%NOTFOUND;
        setDatosVariablesFromCopy(l_iddatvar, l_newidcapaseg_anexo);
    END LOOP;*/

    -- Copia todos los datos variables del capital asegurado sin
    setDatosVariablesFromCopy(p_idcapaseg, l_newidcapaseg_anexo);

  END setCapitalAseguradoFromCopy;


  /*
  * ---------------------------------------------------------------------------------------
  *                          3.COPY - setDatosVariablesFromCopy
  * ---------------------------------------------------------------------------------------
  */
  PROCEDURE setDatosVariablesFromCopy(p_idcapaseg        IN NUMBER,
                                      p_idcapaseg_anexo IN NUMBER) IS
   BEGIN
   -- Copio el Dato Variable Sin fiche de la Copy al anexo
    INSERT INTO tb_anexo_mod_capitales_dts_vbl
        (id,
         idcapitalasegurado,
         codconcepto,
         valor, codconceptoppalmod, codriesgocubierto)

         SELECT SQ_ANEXO_MOD_CAPITALES_DTS_VBL.Nextval, p_idcapaseg_anexo, codconcepto, valor, codconceptoppalmod, codriesgocubierto FROM
           (SELECT
                  cdat.codconcepto,
                  DECODE (cdat.valor, 'S','-1','N','-2',cdat.valor) valor,
                  cdat.codconceptoppalmod,
                  cdat.codriesgocubierto

             FROM tb_copy_datos_var_parc cdat, tb_sc_dd_dic_datos dd
            WHERE cdat.idcapitalasegurado = p_idcapaseg
              and cdat.codconcepto = dd.codconcepto
              and dd.codtiponaturaleza != 4
            GROUP BY cdat.codconcepto, cdat.valor, cdat.codconceptoppalmod, cdat.codriesgocubierto);

  -- Copio el Dato Variable Con fecha de la Copy al anexo
    INSERT INTO tb_anexo_mod_capitales_dts_vbl
        (id,
         idcapitalasegurado,
         codconcepto,
         valor)

          SELECT SQ_ANEXO_MOD_CAPITALES_DTS_VBL.Nextval, p_idcapaseg_anexo, codconcepto, fecha FROM
             (SELECT
                    cdat.codconcepto,
                    TO_CHAR(TO_DATE(cdat.valor, 'YYYY/MM/DD'), 'DD/MM/YYYY') fecha

               FROM tb_copy_datos_var_parc cdat, tb_sc_dd_dic_datos dd
              WHERE cdat.idcapitalasegurado = p_idcapaseg
                and cdat.codconcepto = dd.codconcepto
                and dd.codtiponaturaleza = 4
              GROUP BY cdat.codconcepto, TO_CHAR(TO_DATE(cdat.valor, 'YYYY/MM/DD'), 'DD/MM/YYYY'));

  EXCEPTION
      WHEN OTHERS THEN

       INSERT INTO tb_anexo_mod_capitales_dts_vbl
        (id,
         idcapitalasegurado,
         codconcepto,
         valor)

         SELECT
             SQ_ANEXO_MOD_CAPITALES_DTS_VBL.Nextval as iddatovariable,
             p_idcapaseg_anexo,
             cdat.codconcepto,
              cdat.valor

        FROM tb_copy_datos_var_parc cdat , tb_sc_dd_dic_datos dd
       WHERE cdat.idcapitalasegurado = p_idcapaseg
       and cdat.codconcepto = dd.codconcepto
       and dd.codtiponaturaleza = 4;



  END setDatosVariablesFromCopy;


 /*
  * ---------------------------------------------------------------------------------------
  *                        Main Poliza - copiarParcelasFromPoliza
  * ---------------------------------------------------------------------------------------
  */
  PROCEDURE copiarParcelasFromPoliza(P_IDPOLIZA  IN NUMBER,
                                     P_IDANEXO   IN NUMBER,
                                     REFPARCELA  IN NUMBER) IS


    TYPE TpCursor IS REF CURSOR;
    l_tp_cursor     TpCursor;
    l_tp_cursor2     TpCursor;
    l_sql           VARCHAR2(80);
    l_idparcela     NUMBER;
    l_sql2           VARCHAR2(1000);
    idParcelaDeCopy  NUMBER;
    esParcela        BOOLEAN; -- 1 parcela, 2 instalacion --



  BEGIN
    BEGIN -- Exception


    -- Copio las parcelas con idPoliza = P_IDPOLIZA
    IF (P_IDPOLIZA IS NOT NULL) THEN
        l_sql := 'select idparcela from tb_parcelas where idpoliza = ' || P_IDPOLIZA;

        OPEN l_tp_cursor FOR l_sql;
        LOOP FETCH l_tp_cursor INTO l_idparcela;
            EXIT WHEN l_tp_cursor%NOTFOUND;
            setParcelaFromPoliza(l_idparcela, P_IDANEXO,REFPARCELA); -- Copio la parcela
        END LOOP;
    END IF;


    COMMIT;

    -- introduzco en las estructuras el IDPARCELAANXESTRUCTURA--
    IF (P_IDPOLIZA IS NOT NULL) THEN
              l_sql2 := 'select id from tb_anexo_mod_parcelas where idanexo = ' || P_IDANEXO;

              OPEN l_tp_cursor2 FOR l_sql2;
              LOOP FETCH l_tp_cursor2 INTO l_idparcela;
                  EXIT WHEN l_tp_cursor2%NOTFOUND;

                  esParcela := isParcela(l_idparcela);
                  -- solo si es estructura recupero el id de su parcela  --
                  IF NOT esParcela THEN
                      idParcelaDeCopy:= getIdParcelaEstrutura(l_idparcela,P_IDANEXO);
                      UPDATE tb_anexo_mod_parcelas SET IDPARCELAANXESTRUCTURA=idParcelaDeCopy WHERE id=l_idparcela;
                  END IF;

              END LOOP;
      END IF;





    EXCEPTION
      WHEN OTHERS THEN
         ROLLBACK;
    END;-- Exception

    COMMIT;

  END copiarParcelasFromPoliza;

  /*
  * ---------------------------------------------------------------------------------------
  *                          1.POLIZA - setParcelaFromPoliza
  * ---------------------------------------------------------------------------------------
  */
  PROCEDURE setParcelaFromPoliza(p_idparcela  IN NUMBER,
                                 p_idanexo    IN NUMBER,
                                 refparcela   IN NUMBER) IS



    TYPE TpCursor IS REF CURSOR;
    l_tp_cursor           TpCursor;
    l_sql                 VARCHAR2(120);
    l_idcapaseg           NUMBER;
    l_newidparcela_anexo  NUMBER;


  BEGIN
      BEGIN
          -- Copiamos la parcela de la poliza al anexo
          INSERT INTO tb_anexo_mod_parcelas
            (id,
             idanexo,
             idparcela,
             idcopyparcela,
             tipomodificacion,
             hoja,
             numero,
             nomparcela,
             codprovincia,
             codcomarca,
             codtermino,
             subtermino,
             codcultivo,
             codvariedad,
             poligono,
             parcela,
             codprovsigpac,
             codtermsigpac,
             agrsigpac,
             zonasigpac,
             poligonosigpac,
             parcelasigpac,
             recintosigpac,
             tipoparcela,
             idparcelaanxestructura)

            SELECT
                   SQ_TB_ANEXO_MOD_PARCELAS.Nextval as id,
                   p_idanexo,
                   p_idparcela,
                   null,
                   null,
                   hoja,
                   numero,
                   nomparcela,
                   codprovincia,
                   codcomarca,
                   codtermino,
                   subtermino,
                   codcultivo,
                   codvariedad,
                   poligono,
                   parcela,
                   codprovsigpac,
                   codtermsigpac,
                   agrsigpac,
                   zonasigpac,
                   poligonosigpac,
                   parcelasigpac,
                   recintosigpac,
                   tipoparcela,
                   refparcela

             FROM tb_parcelas
             WHERE idparcela = p_idparcela;



          -- Recupero el id de la parcela creada
          execute immediate 'select SQ_TB_ANEXO_MOD_PARCELAS.CURRVAL from DUAL'
            into l_newidparcela_anexo;



          -- Copiamos los CAPITALES ASEGURADOS
          l_sql := 'select idcapitalasegurado from tb_capitales_asegurados where idparcela = ' || p_idparcela;
          OPEN l_tp_cursor FOR l_sql;
          LOOP FETCH l_tp_cursor INTO l_idcapaseg;
               EXIT WHEN l_tp_cursor%NOTFOUND;
              setCapitalAseguradoFromPoliza(l_idcapaseg, l_newidparcela_anexo,p_idparcela); -- Copia el capital Asegurado
          END LOOP;

      EXCEPTION
          WHEN OTHERS THEN
               ROLLBACK;
               RAISE;
      END;-- Exception

      COMMIT;

  END setParcelaFromPoliza;

  /*
  * ---------------------------------------------------------------------------------------
  *                          setSubvencionCCAAToAnexo
  * ---------------------------------------------------------------------------------------
  */
  PROCEDURE setSubvencionCCAAToAnexo(idsubv_ccaa          IN NUMBER,
                                     idparcela_anexo      IN NUMBER,
                                     id_capital_asegurado IN NUMBER) IS
  BEGIN

    -- Arrastramos el Dato Variable de la Copy a la Poliza
    INSERT INTO tb_anexo_mod_capitales_dts_vbl
        (id,
         idcapitalasegurado,
         codconcepto,
         valor)

         SELECT
             SQ_ANEXO_MOD_CAPITALES_DTS_VBL.Nextval as iddatovariable,
             id_capital_asegurado,
             132,
             s.codtiposubvccaa

        FROM tb_subv_parcela_ccaa sp, tb_sc_c_subvs_ccaa s
        where sp.lineaseguroid = s.lineaseguroid and
        sp.idsubvccaa = s.id and sp.idsubvencion = idsubv_ccaa;

  END setSubvencionCCAAToAnexo;
  /*
  * ---------------------------------------------------------------------------------------
  *                         setSubvencionEnesaToAnexo
  * ---------------------------------------------------------------------------------------
  */

  PROCEDURE setSubvencionEnesaToAnexo(idsubv_enesa    IN NUMBER,
                                      idparcela_anexo IN NUMBER,
                                      id_capital_asegurado IN NUMBER) IS
  BEGIN

    -- Arrastramos el Dato Variable de la Copy a la Poliza
    INSERT INTO tb_anexo_mod_capitales_dts_vbl
        (id,
         idcapitalasegurado,
         codconcepto,
         valor)

         SELECT
             SQ_ANEXO_MOD_CAPITALES_DTS_VBL.Nextval as iddatovariable,
             id_capital_asegurado,
             132,
             codtiposubvenesa

        FROM tb_subv_parcela_enesa sp, tb_sc_c_subvs_enesa s
        where sp.lineaseguroid = s.lineaseguroid and
        sp.idsubvenesa = s.id and sp.idsubvencion = idsubv_enesa;

  END setSubvencionEnesaToAnexo;

  /*
  * ---------------------------------------------------------------------------------------
  *                          2.POLIZA - setCapitalAseguradoFromPoliza
  * ---------------------------------------------------------------------------------------
  */
  PROCEDURE setCapitalAseguradoFromPoliza(p_idcapaseg       IN NUMBER,
                                          p_idparcela_anexo IN NUMBER,
                                          id_parcela_poliza IN NUMBER) IS


    TYPE TpCursor IS REF CURSOR;
    l_tp_cursor               TpCursor;
    l_tp_cursor_ccaa          TpCursor;
    l_tp_cursor_enesa         TpCursor;
    l_tp_cursor_coberturas    TpCursor;

    l_idsubv_ccaa        NUMBER;
    l_idsubv_enesa       NUMBER;
    l_sql                VARCHAR2(120);
    l_iddatvar           NUMBER;
    l_newidcapaseg_anexo NUMBER;
    l_precio             NUMBER;
    l_produccion         NUMBER;
    l_idconertura        NUMBER;


  BEGIN

    -- Obtengo precio y produccion de tb_cap_aseg_rel_modulo
    -- precio
    SELECT precio into l_precio
    FROM tb_cap_aseg_rel_modulo
    WHERE idcapitalasegurado=p_idcapaseg;

    -- produccion
    SELECT produccion into l_produccion
    FROM tb_cap_aseg_rel_modulo
    WHERE idcapitalasegurado=p_idcapaseg;


    -- Copiamos el Capital Asegurado de la poliza al Anexo
    INSERT INTO tb_anexo_mod_capitales_aseg
      (id,
       idparcelaanexo,
       codtipocapital,
       superficie,
       precio,
       produccion)

      SELECT
          SQ_ANEXO_MOD_CAPITALES_ASEG.Nextval as idcapitalasegurado,
          p_idparcela_anexo,
          codtipocapital,
          superficie,
          l_precio,
          l_produccion

        FROM tb_capitales_asegurados
       WHERE idcapitalasegurado = p_idcapaseg;

    -- Recuperamos el id del Capital Asegurado creado
    execute immediate 'select SQ_ANEXO_MOD_CAPITALES_ASEG.CURRVAL from DUAL'
      into l_newidcapaseg_anexo;

    -- Copiamos los Datos Variables del Capital Asegurado
    l_sql := 'select iddatovariable from tb_datos_var_parcela where idcapitalasegurado = ' || p_idcapaseg;

    OPEN l_tp_cursor FOR l_sql;
    LOOP FETCH l_tp_cursor INTO l_iddatvar;
                EXIT WHEN l_tp_cursor%NOTFOUND;
                setDatosVariablesFromPoliza(l_iddatvar, l_newidcapaseg_anexo);
    END LOOP;


     -- Copiamos las SUBVENCIONES
          --ccaa
          l_sql := 'select idsubvencion from tb_subv_parcela_ccaa where idparcela = ' || id_parcela_poliza;
          OPEN l_tp_cursor_ccaa FOR l_sql;
          LOOP FETCH l_tp_cursor_ccaa INTO l_idsubv_ccaa;
               EXIT WHEN l_tp_cursor_ccaa%NOTFOUND;
              setSubvencionCCAAToAnexo(l_idsubv_ccaa,p_idparcela_anexo,l_newidcapaseg_anexo);
          END LOOP;

          -- enesa
          l_sql := 'select idsubvencion from tb_subv_parcela_enesa where idparcela = ' || id_parcela_poliza;
          OPEN l_tp_cursor_enesa FOR l_sql;
          LOOP FETCH l_tp_cursor_enesa INTO l_idsubv_enesa;
               EXIT WHEN l_tp_cursor_enesa%NOTFOUND;
              setSubvencionEnesaToAnexo(l_idsubv_enesa,p_idparcela_anexo,l_newidcapaseg_anexo);
          END LOOP;

      -- Copiamos las COBERTURAS (tb_parcelas_coberturas)
          l_sql := 'select id from tb_parcelas_coberturas where idparcela = ' || id_parcela_poliza;
          OPEN l_tp_cursor_coberturas FOR l_sql;
          LOOP FETCH l_tp_cursor_coberturas INTO l_idconertura;
              EXIT WHEN l_tp_cursor_coberturas%NOTFOUND;
              insertCoberturaToAnexo(l_idconertura,p_idparcela_anexo,l_newidcapaseg_anexo);
          END LOOP;


  END setCapitalAseguradoFromPoliza;

 /*
  * ---------------------------------------------------------------------------------------
  *                         insertCoberturaToAnexo
  * ---------------------------------------------------------------------------------------
  */

  PROCEDURE insertCoberturaToAnexo(idconertura IN NUMBER,
                                   idparcela_anexo IN NUMBER,
                                   id_capital_asegurado IN NUMBER) IS
  BEGIN

    -- Arrastramos el Dato Variable de la Copy a la Poliza
    INSERT INTO tb_anexo_mod_capitales_dts_vbl
        (id,
         idcapitalasegurado,
         codconcepto,
         valor,
         codconceptoppalmod,
         codriesgocubierto)

         SELECT
             SQ_ANEXO_MOD_CAPITALES_DTS_VBL.Nextval as iddatovariable,
             id_capital_asegurado,
             363,
             -1,
             codconceptoppalmod,
             codriesgocubierto

        FROM tb_parcelas_coberturas
       WHERE id = idconertura;

  END insertCoberturaToAnexo;

  /*
  * ---------------------------------------------------------------------------------------
  *                          3.POLIZA - setDatosVariablesFromPoliza
  * ---------------------------------------------------------------------------------------
  */
  PROCEDURE setDatosVariablesFromPoliza(p_iddatvar        IN NUMBER,
                                        p_idcapaseg_anexo IN NUMBER) IS
  BEGIN

    -- Arrastramos el Dato Variable de la Copy a la Poliza
    INSERT INTO tb_anexo_mod_capitales_dts_vbl
        (id,
         idcapitalasegurado,
         codconcepto,
         valor)

         SELECT
             SQ_ANEXO_MOD_CAPITALES_DTS_VBL.Nextval as iddatovariable,
             p_idcapaseg_anexo,
             codconcepto,
             valor

        FROM tb_datos_var_parcela
       WHERE iddatovariable = p_iddatvar;

  END setDatosVariablesFromPoliza;


 /* ------------------------------------------------------------------------------
  *                              UTILS
  * ------------------------------------------------------------------------------
  */

  -- isParcela --
  FUNCTION  isParcela(p_idparcela IN NUMBER) RETURN BOOLEAN
  IS
      resultado             BOOLEAN;
      TYPE TpCursor IS REF CURSOR;
      l_codtipocapital      NUMBER;
      l_tp_cursorTp         TpCursor;
      l_sqlTp               VARCHAR2(120);

  BEGIN



    -- Calculamos el tipo parcela usando la copy recorriendo los capitales asegurados y viendo si alguno es >= 100 --
    l_sqlTp := 'select codtipocapital from tb_anexo_mod_capitales_aseg where idparcelaanexo = ' || p_idparcela;
    OPEN l_tp_cursorTp FOR l_sqlTp;
    LOOP FETCH l_tp_cursorTp INTO l_codtipocapital;
        EXIT WHEN l_tp_cursorTp%NOTFOUND;
            IF l_codtipocapital >= 100 THEN -- es Instalacion
               resultado := false;
            ELSE                            -- es Parcela
               resultado := true;
            END IF;
    END LOOP;


      RETURN resultado;

  END isParcela;


  -- --
  FUNCTION getIdParcelaEstrutura (l_idEstructura IN NUMBER, l_idAnexo IN NUMBER) RETURN NUMBER
  IS
      v_idParcelaEstructura NUMBER := 0;
      TYPE TpCursor IS REF CURSOR;
      l_tp_cursorTp         TpCursor;
      l_sqlTp               VARCHAR2(1000);

      -- --
      codprovincia1         NUMBER;
      codcomarca1           NUMBER;
      codtermino1           NUMBER;
      subtermino1           VARCHAR(1);
      codcultivo1           NUMBER;
      codvariedad1          NUMBER;
      codprovsigpac1        NUMBER;
      codtermsigpac1        NUMBER;
      agrsigpac1            NUMBER;
      zonasigpac1           NUMBER;
      poligonosigpac1       NUMBER;
      parcelasigpac1        NUMBER;
      recintosigpac1        NUMBER;

      -- --
      id2                   NUMBER;
      tipoparcela2          VARCHAR(1);
      codprovincia12        NUMBER;
      codcomarca12          NUMBER;
      codtermino12          NUMBER;
      subtermino12          VARCHAR(1);
      codcultivo12          NUMBER;
      codvariedad12         NUMBER;
      codprovsigpac12       NUMBER;
      codtermsigpac12       NUMBER;
      agrsigpac12           NUMBER;
      zonasigpac12          NUMBER;
      poligonosigpac12      NUMBER;
      parcelasigpac12       NUMBER;
      recintosigpac12       NUMBER;
  BEGIN


     SELECT codprovincia,codcomarca,codtermino,subtermino,codcultivo,codvariedad,codprovsigpac,codtermsigpac,agrsigpac,zonasigpac,
             poligonosigpac,parcelasigpac,recintosigpac INTO codprovincia1,codcomarca1,codtermino1,subtermino1,codcultivo1,codvariedad1,
             codprovsigpac1,codtermsigpac1,agrsigpac1,zonasigpac1,poligonosigpac1,parcelasigpac1,recintosigpac1
      FROM tb_anexo_mod_parcelas
      WHERE id = l_idEstructura;

      l_sqlTp := 'select id,tipoparcela,codprovincia,codcomarca,codtermino,subtermino,codcultivo,codvariedad,codprovsigpac,codtermsigpac,agrsigpac,zonasigpac,
                poligonosigpac,parcelasigpac,recintosigpac from tb_anexo_mod_parcelas
                where idanexo = ' || l_idAnexo ;

      OPEN l_tp_cursorTp FOR l_sqlTp;
      LOOP FETCH l_tp_cursorTp INTO id2,tipoparcela2,codprovincia12,codcomarca12,codtermino12,subtermino12,codcultivo12,codvariedad12,
             codprovsigpac12,codtermsigpac12,agrsigpac12,zonasigpac12,poligonosigpac12,parcelasigpac12,recintosigpac12;
        EXIT WHEN l_tp_cursorTp%NOTFOUND;

          IF  codprovincia1 = codprovincia12 and
              codcomarca1 = codcomarca12 and
              codtermino1 = codtermino12 and
              subtermino1 = subtermino12 and
              codcultivo1 = codcultivo12 and
              codvariedad1 = codvariedad12 and
              codprovsigpac1 = codprovsigpac12 and
              codtermsigpac1 = codtermsigpac12 and
              agrsigpac1 = agrsigpac12 and
              zonasigpac1 = zonasigpac12 and
              poligonosigpac1 = poligonosigpac12 and
              parcelasigpac1 = parcelasigpac12 and
              recintosigpac1 = recintosigpac12
         THEN
             IF l_idEstructura != id2 and tipoparcela2 != 'E'
             THEN
                  v_idParcelaEstructura := id2;
             END IF;
          END IF;

    END LOOP;

     RETURN v_idParcelaEstructura;


      RETURN  v_idParcelaEstructura;

  END getIdParcelaEstrutura;


  -- getIdPolizaFromAnexo
  FUNCTION getIdPolizaFromAnexo (P_IDANEXO IN NUMBER) RETURN NUMBER
  IS
      v_idPoliza NUMBER :=0;

  BEGIN

      SELECT idPoliza into v_idPoliza FROM tb_anexo_mod WHERE id=P_IDANEXO;

      RETURN v_idPoliza;

  END getIdPolizaFromAnexo;


  --GETIDCOPY RECUPERA EL IDCOPY DE UNA POLIZA
  FUNCTION getIdCopyPoliza(P_IDANEXO IN NUMBER) RETURN NUMBER
  IS
    v_idCopy NUMBER := 0;
  BEGIN
       BEGIN

            SELECT idcopy INTO v_idCopy
            FROM tb_anexo_mod
            WHERE id = P_IDANEXO;

       EXCEPTION
                WHEN OTHERS THEN
                     v_idCopy := 0;
       END;

        RETURN v_idCopy;

  END getIdCopyPoliza;

END PQ_COPIAR_PARCELAS_A_ANEXO;
/
SHOW ERRORS;