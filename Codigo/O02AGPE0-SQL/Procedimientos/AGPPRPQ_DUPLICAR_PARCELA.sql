create or replace package o02agpe0.PQ_DUPLICAR_PARCELA is

--Procedimiento para duplicar una parcela n veces
PROCEDURE PR_DUPLICAR_PARCELA(P_IDPARCELA IN VARCHAR2, P_IDPOLIZA IN VARCHAR2,P_veces IN VARCHAR2,p_result out varchar2);

 --- Procedimientos para copiar la parcela
PROCEDURE setParcelaFromPoliza(p_idparcela  IN NUMBER,P_IDPOLIZA2    IN NUMBER);
PROCEDURE setCapitalAseguradoFromPoliza(p_idcapaseg       IN NUMBER,p_idparcela_nueva IN NUMBER, p_idparcela IN NUMBER);
PROCEDURE setDatosVariablesFromPoliza(p_iddatvar        IN NUMBER,p_idcapaseg IN NUMBER);
PROCEDURE setRelModFromPoliza(p_idrelmod        IN NUMBER,p_idcapaseg IN NUMBER);
PROCEDURE setRiesgosParcela(p_idparcela IN NUMBER, p_idcapaseg IN NUMBER);

end PQ_DUPLICAR_PARCELA;
/
create or replace package body o02agpe0.PQ_DUPLICAR_PARCELA is

--Procedimiento para duplicar una parcela n veces
  PROCEDURE PR_DUPLICAR_PARCELA(P_IDPARCELA IN VARCHAR2, P_IDPOLIZA IN VARCHAR2,P_VECES IN VARCHAR2,p_result out varchar2) is

  i     NUMBER := 0;
   v_result varchar2(10) := null;

  BEGIN
    BEGIN -- Exception

      -- Copio la parcelas con idPoliza = P_IDPOLIZA n veces
       i := P_VECES;
       WHILE i >0 LOOP
           setParcelaFromPoliza(P_IDPARCELA, P_IDPOLIZA); -- Copio la parcela
           i := i -1;
       END LOOP;
       p_result := v_result;

   EXCEPTION
      WHEN NO_DATA_FOUND THEN
           --pq_utl.log('ERRO EN PR_DUPLICAR_POLIZA ' || SQLERRM || '*********');
           ROLLBACK;
   END;-- Exception

  COMMIT;


END PR_DUPLICAR_PARCELA;
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
 PROCEDURE setParcelaFromPoliza(p_idparcela  IN NUMBER,P_IDPOLIZA2    IN NUMBER) IS

    TYPE TpCursor IS REF CURSOR;
    l_tp_cursor           TpCursor;
    l_sql                 VARCHAR2(120);
    l_idcapaseg           NUMBER;
    l_newidparcela        NUMBER;
    l_codtipocapt         NUMBER;
    l_codconcepto         NUMBER;

  BEGIN
      BEGIN
          -- Copiamos la parcela de la poliza
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
             --hoja,
             --numero,
             codcomarca,
             lineaseguroid,
             idparcelaestructura,
             tipoparcela)

            SELECT
                   SQ_PARCELAS.Nextval as id,
                   P_IDPOLIZA2,
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
                   --hoja,
                   --numero,
                   codcomarca,
                   lineaseguroid,
                   idparcelaestructura,
                   tipoparcela

             FROM tb_parcelas
             WHERE idparcela = p_idparcela;


          -- Recupero el id de la parcela creada
          execute immediate 'select SQ_PARCELAS.CURRVAL from DUAL'
            into l_newidparcela;

          -- Copiamos los Capitales Asegurados
          l_sql := 'select idcapitalasegurado from tb_capitales_asegurados where idparcela = ' || p_idparcela;
          OPEN l_tp_cursor FOR l_sql;
          LOOP FETCH l_tp_cursor INTO l_idcapaseg;
                EXIT WHEN l_tp_cursor%NOTFOUND;


                -- Recupero el codtipocapital y luego el codconcepto
                SELECT codtipocapital INTO l_codtipocapt FROM tb_capitales_asegurados tca WHERE tca.idcapitalasegurado= l_idcapaseg;
                SELECT codconcepto INTO l_codconcepto FROM tb_sc_c_tipo_capital tc WHERE tc.codtipocapital = l_codtipocapt;


                --IF l_codconcepto = 68 THEN
                    setCapitalAseguradoFromPoliza(l_idcapaseg, l_newidparcela,p_idparcela); -- Copia el capital Asegurado
                -- END IF;

          END LOOP;

      EXCEPTION
          WHEN NO_DATA_FOUND THEN
               ROLLBACK;
      END;-- Exception

      COMMIT;

  END setParcelaFromPoliza;

----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
  PROCEDURE setCapitalAseguradoFromPoliza(p_idcapaseg       IN NUMBER,
                                          p_idparcela_nueva IN NUMBER,
                                          p_idparcela IN NUMBER) IS

   TYPE TpCursor IS REF CURSOR;
    l_tp_cursor               TpCursor;
    l_tp_cursor2              TpCursor;
    l_sql                VARCHAR2(120);
    l_sql2               VARCHAR2(120);
    l_iddatvar           NUMBER;
    l_idrelmod           NUMBER;
    l_newidcapaseg NUMBER;

  BEGIN
    -- Copiamos el Capital Asegurado de la poliza
     INSERT INTO tb_capitales_asegurados
      (idparcela,
       idcapitalasegurado,
       codtipocapital,
       superficie)

      SELECT
           p_idparcela_nueva,
           Sq_Capitales_Asegurados.Nextval as id,
           cap.codtipocapital,
           cap.superficie

       FROM tb_capitales_asegurados cap
       WHERE cap.idcapitalasegurado = p_idcapaseg;

         -- Recuperamos el id del Capital Asegurado creado
    execute immediate 'select Sq_Capitales_Asegurados.CURRVAL from DUAL'
      into l_newidcapaseg;

     -- Copiamos los Datos Variables del Capital Asegurado
    l_sql := 'select iddatovariable from tb_datos_var_parcela where idcapitalasegurado = ' || p_idcapaseg;

    OPEN l_tp_cursor FOR l_sql;
    LOOP FETCH l_tp_cursor INTO l_iddatvar;
                EXIT WHEN l_tp_cursor%NOTFOUND;
                setDatosVariablesFromPoliza(l_iddatvar, l_newidcapaseg);
    END LOOP;

     -- Copiamos precio y produccion en tb_cap_aseg_rel_modulo
    l_sql2 := 'select id from tb_cap_aseg_rel_modulo where idcapitalasegurado = ' || p_idcapaseg;

    OPEN l_tp_cursor2 FOR l_sql2;
    LOOP FETCH l_tp_cursor2 INTO l_idrelmod;
                EXIT WHEN l_tp_cursor2%NOTFOUND;
                setRelModFromPoliza(l_idrelmod, l_newidcapaseg);
    END LOOP;

    --Copiamos las coberturas de parcela como un dato variable
    setRiesgosParcela(p_idparcela,l_newidcapaseg);

  END setCapitalAseguradoFromPoliza;
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
  PROCEDURE setDatosVariablesFromPoliza(p_iddatvar        IN NUMBER,
                                        p_idcapaseg IN NUMBER) IS
  BEGIN
    --pq_utl.log('setDatosVariablesFromPoliza ('||p_iddatvar||', ' || p_idcapaseg || ');****', 2);
    INSERT INTO tb_datos_var_parcela
        (iddatovariable,
         idcapitalasegurado,
         codconcepto,
         valor,
         CODCONCEPTOPPALMOD,
         CODRIESGOCUBIERTO)

         SELECT
             SQ_DATOS_VAR_PARCELA.Nextval as iddatovariable,
             p_idcapaseg,
             codconcepto,
             valor,
             CODCONCEPTOPPALMOD,
             CODRIESGOCUBIERTO

        FROM tb_datos_var_parcela
        WHERE iddatovariable = p_iddatvar;



  END setDatosVariablesFromPoliza;
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
  PROCEDURE setRelModFromPoliza(p_idrelmod        IN NUMBER,p_idcapaseg IN NUMBER) IS
  BEGIN
    --pq_utl.log('setDatosVariablesFromPoliza ('||p_iddatvar||', ' || p_idcapaseg || ');****', 2);
    INSERT INTO tb_cap_aseg_rel_modulo
        (id,
         idcapitalasegurado,
         codmodulo,
         precio,
         produccion,
         preciomodif,
         produccionmodif)

         SELECT
             SQ_TB_CAP_ASEG_REL_MODULO.Nextval as id,
             p_idcapaseg,
             codmodulo,
             precio,
             produccion,
             preciomodif,
             produccionmodif

        FROM tb_cap_aseg_rel_modulo
        WHERE id = p_idrelmod;

  END setRelModFromPoliza;
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
PROCEDURE setRiesgosParcela(p_idparcela IN NUMBER, p_idcapaseg IN NUMBER) IS

BEGIN

      INSERT INTO tb_datos_var_parcela
        (iddatovariable,
         idcapitalasegurado,
         codconcepto,
         valor,
         CODCONCEPTOPPALMOD,
         CODRIESGOCUBIERTO)

         SELECT
             SQ_DATOS_VAR_PARCELA.Nextval as iddatovariable,
             p_idcapaseg,
             p.codconcepto,
             TO_CHAR(p.codvalor),
             p.codconceptoppalmod,
             p.codriesgocubierto

        FROM tb_parcelas_coberturas p
        WHERE p.idparcela = p_idparcela;



END setRiesgosParcela;

----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
END PQ_DUPLICAR_PARCELA;
/
