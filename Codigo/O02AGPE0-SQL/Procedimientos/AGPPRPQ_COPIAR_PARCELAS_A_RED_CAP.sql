create or replace package o02agpe0.PQ_COPIAR_PARCELAS_A_RED_CAP is
/******************************************************************************
   NAME:
   PURPOSE:    Copia las parcelas y capitales asegurados de la copy a parcelas y capitales de reducción de capital (si no tiene parcelas)
               si  idcopy distinto de null o '', si el idcopy es null copiara las
               parcelas y capitales de la poliza a la copy.

   REVISIONS:
   Ver        Date        Author           Description
   ----------  ----------  ---------------  ------------------------------------
   1.0        26/01/2011  T-SYSTEMS

   NOTES:

******************************************************************************/
 -- MAIN (punto de entrada al pl/sql)
   PROCEDURE copiarParcelasEnRedCap(
                                 P_IDREDCAP             IN NUMBER);
-- Procedimientos COPY: si hay idcopy en la reducción de capital, copiará las parcelas de la copy a dicha reducción.

-- main copy
   PROCEDURE copiarParcelasFromCopy(
                                 P_IDCOPY                         IN NUMBER,
                                 P_IDREDCAP             IN NUMBER);
   -- Paso 1
   PROCEDURE setParcelaFromCopy(
                                 p_idparcela                      IN NUMBER,
                                 P_IDREDCAP             IN NUMBER);
   -- Paso 2
   PROCEDURE setCapitalAseguradoFromCopy(
                                 p_idcapaseg                      IN NUMBER,
                                 p_idparcela_redcap     IN NUMBER);



    -- Procedimientos POLIZA: si no hay idcopy habra que copiar las parcelas de la poliza al anexo.

    -- main poliza
    PROCEDURE copiarParcelasFromPoliza(
                                 P_IDPOLIZA                       IN NUMBER,
                                 P_IDREDCAP             IN NUMBER);

    -- Paso 1
    PROCEDURE setParcelaFromPoliza(
                                 p_idparcela                      IN NUMBER,
                                 P_IDREDCAP             IN NUMBER);

    -- Paso 2
    PROCEDURE setCapitalAseguradoFromPoliza(
                                 p_idcapaseg                      IN NUMBER,
                                 p_idparcela_redcap     IN NUMBER);


    -- UTILS
    FUNCTION getIdPolizaFromRedCap ( P_IDREDCAP IN NUMBER) RETURN NUMBER;

    FUNCTION getIdCopyPoliza(P_IDREDCAP IN NUMBER) RETURN NUMBER;


end PQ_COPIAR_PARCELAS_A_RED_CAP;
/
create or replace package body o02agpe0.PQ_COPIAR_PARCELAS_A_RED_CAP is

/*
  * ---------------------------------------------------------------------------------------
  *                            MAIN - copiarParcelasToReduccionCapital
  * ----------------------------------------------------------------------------------------
  */

 PROCEDURE copiarParcelasEnRedCap(P_IDREDCAP IN NUMBER) IS

     v_count NUMBER(10) := 0;
     v_idPoliza NUMBER(10);
     v_idCopy NUMBER(10);

 BEGIN
     BEGIN -- Exception

           -- Comprobamos que no hay parcelas dadas de alta en la reducción de capital
           select count(*) into v_count
           from tb_anexo_red_parcelas t
           where t.idanexo = P_IDREDCAP;

           v_idPoliza := getIdPolizaFromRedCap(P_IDREDCAP);
           v_idCopy := getIdCopyPoliza(P_IDREDCAP);

        IF v_count = 0 THEN

             IF v_idCopy = 0 OR v_idCopy is null THEN  -- no hay copy

                 copiarParcelasFromPoliza(v_idPoliza,P_IDREDCAP);

             ELSE                -- si hay copy

                 copiarParcelasFromCopy(v_idCopy,P_IDREDCAP);
                 update tb_anexo_red t set t.idcopy = v_idCopy where t.idpoliza=v_idPoliza;

             END IF;

       END IF;

      EXCEPTION

            WHEN NO_DATA_FOUND THEN
               ROLLBACK;

      END;-- Exception

    COMMIT;

  END copiarParcelasEnRedCap;


 /*
  * ---------------------------------------------------------------------------------------
  *                          Main Copy - copiarParcelasFromCopy
  * ---------------------------------------------------------------------------------------
  */
  PROCEDURE copiarParcelasFromCopy(P_IDCOPY  IN NUMBER,
                                   P_IDREDCAP   IN NUMBER)  IS


      TYPE TpCursor IS REF CURSOR;
      l_tp_cursor     TpCursor;
      l_sql           VARCHAR2(60);
      l_idparcela     NUMBER;


  BEGIN
      BEGIN -- Exception

      -- Copio las parcelas con idcopy = P_IDCOPY
      IF (P_IDCOPY IS NOT NULL) THEN
          l_sql := 'select id from tb_copy_parcelas where idcopy = ' || P_IDCOPY;

          OPEN l_tp_cursor FOR l_sql;
          LOOP FETCH l_tp_cursor INTO l_idparcela;
              EXIT WHEN l_tp_cursor%NOTFOUND;
              setParcelaFromCopy(l_idparcela, P_IDREDCAP); -- Copio la parcela
          END LOOP;
      END IF;



      EXCEPTION
            WHEN NO_DATA_FOUND THEN
               ROLLBACK;
      END;-- Exception

      COMMIT;

  END copiarParcelasFromCopy;


 /*
  * ---------------------------------------------------------------------------------------
  *                          1.COPY - setParcelaFromCopy
  * ---------------------------------------------------------------------------------------
  */
  PROCEDURE setParcelaFromCopy(p_idparcela  IN NUMBER,
                               P_IDREDCAP    IN NUMBER) IS



    TYPE TpCursor IS REF CURSOR;
    l_tp_cursor           TpCursor;
    l_sql                 VARCHAR2(120);
    l_idcapaseg           NUMBER;
    l_newidparcela_anexo  NUMBER;


  BEGIN

    -- Copiamos la parcela de la copy
    INSERT INTO tb_anexo_red_parcelas
      (id,
       idanexo,
       altaenanexo,
       idparcela,
       idparcelacopy,
       codprovincia,
       codcomarca,
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
       hoja,
       numero,
       codcultivo,
       codvariedad)

      SELECT
             SQ_TB_ANEXO_RED_PARCELAS.Nextval as id,
             P_IDREDCAP,
             'N',
             null,
             id,
             codprovincia,
             codcomarca,
             codtermino,
             NVL(subtermino,' '),
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
             hoja,
             numero,
             codcultivo,
             codvariedad

       FROM tb_copy_parcelas
       WHERE id = p_idparcela;





    -- Recupero el id de la parcela creada
    execute immediate 'select SQ_TB_ANEXO_RED_PARCELAS.CURRVAL from DUAL'
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
  PROCEDURE setCapitalAseguradoFromCopy(p_idcapaseg                  IN NUMBER,
                                        p_idparcela_RedCap IN NUMBER) IS

  BEGIN

    -- Copiamos el Capital Asegurado de la poliza al Anexo
    INSERT INTO tb_anexo_red_cap_aseg
      (id,
       idparcelaanexo,
       codtipocapital,
       superficie,
       altaenanexo,
       prod,
       prodred,
       precio)

      SELECT
          SQ_CAPITALES_ASEGURADOS.Nextval as idcapitalasegurado,
          p_idparcela_RedCap,
          codtipocapital,
          superficie,
          'N',
          produccion,
          null,
          precio

        FROM tb_copy_capitales_aseg
       WHERE id = p_idcapaseg;

  END setCapitalAseguradoFromCopy;


 /*
  * ---------------------------------------------------------------------------------------
  *                        Main Poliza - copiarParcelasFromPoliza
  * ---------------------------------------------------------------------------------------
  */
  PROCEDURE copiarParcelasFromPoliza(P_IDPOLIZA  IN NUMBER,
                                     P_IDREDCAP   IN NUMBER) IS


    TYPE TpCursor IS REF CURSOR;
    l_tp_cursor     TpCursor;
    l_sql           VARCHAR2(80);
    l_idparcela     NUMBER;



  BEGIN
    BEGIN -- Exception


    -- Copio las parcelas con idPoliza = P_IDPOLIZA
    IF (P_IDPOLIZA IS NOT NULL) THEN
        l_sql := 'select idparcela from tb_parcelas where idpoliza = ' || P_IDPOLIZA;

        OPEN l_tp_cursor FOR l_sql;
        LOOP FETCH l_tp_cursor INTO l_idparcela;
            EXIT WHEN l_tp_cursor%NOTFOUND;
            setParcelaFromPoliza(l_idparcela, P_IDREDCAP); -- Copio la parcela
        END LOOP;
    END IF;



    EXCEPTION
      WHEN NO_DATA_FOUND THEN
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
                                 P_IDREDCAP    IN NUMBER) IS



    TYPE TpCursor IS REF CURSOR;
    l_tp_cursor           TpCursor;
    l_sql                 VARCHAR2(120);
    l_idcapaseg           NUMBER;
    l_newidparcela_anexo  NUMBER;


  BEGIN
      BEGIN
          -- Copiamos la parcela de la poliza
          INSERT INTO tb_anexo_red_parcelas
            (id,
             idanexo,
             altaenanexo,
             idparcela,
             idparcelacopy,
             codprovincia,
             codcomarca,
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
             hoja,
             numero,
             codcultivo,
             codvariedad)

            SELECT
                   SQ_TB_ANEXO_RED_PARCELAS.Nextval as id,
                   P_IDREDCAP,
                   'N',
                   idparcela,
                   null,
                   codprovincia,
                   codcomarca,
                   codtermino,
                   NVL(subtermino,' '),
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
                   hoja,
                   numero,
                   codcultivo,
                   codvariedad

             FROM tb_parcelas
             WHERE idparcela = p_idparcela;



          -- Recupero el id de la parcela creada
          execute immediate 'select SQ_TB_ANEXO_RED_PARCELAS.CURRVAL from DUAL'
            into l_newidparcela_anexo;

          -- Copiamos los Capitales Asegurados
          l_sql := 'select idcapitalasegurado from tb_capitales_asegurados where idparcela = ' || p_idparcela;
          OPEN l_tp_cursor FOR l_sql;
          LOOP FETCH l_tp_cursor INTO l_idcapaseg;
               EXIT WHEN l_tp_cursor%NOTFOUND;
              setCapitalAseguradoFromPoliza(l_idcapaseg, l_newidparcela_anexo); -- Copia el capital Asegurado
          END LOOP;

      EXCEPTION
          WHEN NO_DATA_FOUND THEN
               ROLLBACK;
      END;-- Exception

      COMMIT;

  END setParcelaFromPoliza;

  /*
  * ---------------------------------------------------------------------------------------
  *                          2.POLIZA - setCapitalAseguradoFromPoliza
  * ---------------------------------------------------------------------------------------
  */
  PROCEDURE setCapitalAseguradoFromPoliza(p_idcapaseg       IN NUMBER,
                                          p_idparcela_RedCap IN NUMBER) IS

  BEGIN

    -- Copiamos el Capital Asegurado de la poliza
     INSERT INTO tb_anexo_red_cap_aseg
      (id,
       idparcelaanexo,
       codtipocapital,
       superficie,
       altaenanexo,
       prod,
       prodred,
       precio)

      SELECT
          SQ_CAPITALES_ASEGURADOS.Nextval as idcapitalasegurado,
          p_idparcela_RedCap,
          ca.codtipocapital,
          ca.superficie,
          'N',
          carm.produccion,
          null,
          carm.precio

        FROM tb_capitales_asegurados ca, tb_cap_aseg_rel_modulo carm
       WHERE ca.idcapitalasegurado = carm.idcapitalasegurado
         AND ca.idcapitalasegurado = p_idcapaseg;

  END setCapitalAseguradoFromPoliza;


 /* ------------------------------------------------------------------------------
  *                              UTILS
  * ------------------------------------------------------------------------------
  */

  -- getIdPolizaFromAnexo

  FUNCTION getIdPolizaFromRedCap (P_IDREDCAP IN NUMBER) RETURN NUMBER
  IS
      v_idPoliza NUMBER :=0;

  BEGIN

      SELECT idPoliza into v_idPoliza FROM tb_anexo_red WHERE id=P_IDREDCAP;

      RETURN v_idPoliza;

  END getIdPolizaFromRedCap;

  --GETIDCOPY RECUPERA EL IDCOPY DE UNA POLIZA
   FUNCTION getIdCopyPoliza(P_IDREDCAP IN NUMBER) RETURN NUMBER
  IS
    v_idCopy NUMBER := 0;
  BEGIN
       BEGIN

            SELECT idcopy INTO v_idCopy
            FROM tb_anexo_red
            WHERE  id = P_IDREDCAP;

       EXCEPTION
                WHEN NO_DATA_FOUND THEN
                     v_idCopy := 0;
       END;

        RETURN v_idCopy;

  END getIdCopyPoliza;

end PQ_COPIAR_PARCELAS_A_RED_CAP;
/
