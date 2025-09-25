SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_COPIAR_PARCELAS_CPL_A_ANEXO is

 -- MAIN (punto de entrada al pl/sql)
   PROCEDURE copiarParcelasEnAnexo(
                                 P_IDANEXO       IN NUMBER);
                                
                               
   --- Procedimientos COPY: si hay idcopy en el anexo, copiara las parcelas de la copy al anexo.  
   
   
   --- main copy
   PROCEDURE copiarParcelasFromCopy(
                                 P_IDCOPY      IN NUMBER,
                                 P_IDANEXO       IN NUMBER);
   -- Paso 1
   PROCEDURE setParcelaFromCopy(
                                 p_idparcela     IN NUMBER,
                                 p_idanexo       IN NUMBER);
   -- Paso 2
   PROCEDURE setCapitalAseguradoFromCopy(
                                 p_idcapaseg       IN NUMBER,
                                 p_idparcela_anexo IN NUMBER);                         
   -- Paso 3                             
   PROCEDURE setDatosVariablesFromCopy(
                                 p_iddatvar        IN NUMBER,
                                 p_idcapaseg_anexo IN NUMBER);
                                      
                                      
   
    -- Procedimientos POLIZA: si no hay idcopy habra que copiar las parcelas de la poliza al anexo.

    -- main poliza
    PROCEDURE copiarParcelasFromPoliza(
                                 P_IDPOLIZA      IN NUMBER,
                                 P_IDANEXO       IN NUMBER);
  
    -- Paso 1
    PROCEDURE setParcelaFromPoliza(
                                 p_idparcela     IN NUMBER,
                                 p_idanexo       IN NUMBER);
  
    -- Paso 2
    PROCEDURE setCapitalAseguradoFromPoliza(
                                 p_idcapaseg       IN NUMBER,
                                 p_idparcela_anexo IN NUMBER);
  
    -- Paso 3
    PROCEDURE setDatosVariablesFromPoliza(
                                 p_iddatvar        IN NUMBER,
                                 p_idcapaseg_anexo IN NUMBER);
                                 
    -- UTILS
    FUNCTION getIdPolizaFromAnexo (P_IDANEXO IN NUMBER) RETURN NUMBER;    
    FUNCTION getIdCopyPoliza(P_IDANEXO IN NUMBER) RETURN NUMBER;
    FUNCTION algunaParcelaConIncremento (IDANEXO IN NUMBER) RETURN BOOLEAN;
    PROCEDURE copiarIncrementosDesdePoliza (IDANEXO IN NUMBER, IDPOLIZA IN NUMBER);

end PQ_COPIAR_PARCELAS_CPL_A_ANEXO;
/
create or replace package body o02agpe0.PQ_COPIAR_PARCELAS_CPL_A_ANEXO is

/*
  * ---------------------------------------------------------------------------------------
  *                            MAIN - copiarParcelasToAnexo
  * ----------------------------------------------------------------------------------------
  */

 PROCEDURE copiarParcelasEnAnexo(P_IDANEXO IN NUMBER) IS
 
     lc VARCHAR2(60) := 'PQ_COPIAR_PARCELAS_CPL_A_ANEXO.copiarParcelasEnAnexo';
     v_idPoliza NUMBER(10);
     v_idCopy   NUMBER(10);
     v_count    NUMBER(10);
     v_isCopy   BOOLEAN := FALSE;  
        
 BEGIN
     BEGIN -- Exception
     
           
           v_idPoliza := getIdPolizaFromAnexo(P_IDANEXO);
           v_idCopy := getIdCopyPoliza(P_IDANEXO);
     
           SELECT count(*)   
           INTO v_count       
           FROM tb_anexo_mod_parcelas
           WHERE idanexo = P_IDANEXO;
               
         IF v_count = 0 THEN                    
           IF v_idCopy = 0 OR v_idCopy is null THEN -- si no hay copy
               pq_utl.log(lc, 'Se copian las parcelas de la póliza al anexo');
               copiarParcelasFromPoliza(v_idPoliza,P_IDANEXO);
           ELSE  -- si hay copy
               pq_utl.log(lc, 'Se copian las parcelas de la copy al anexo');
               copiarParcelasFromCopy(v_idCopy,P_IDANEXO);               
               v_isCopy := TRUE;
           END IF;
         END IF;                
      
       COMMIT; 
       
       -- Si después de copiar las parcelas de la copy se detecta que ninguna de ellas tiene incremento
       -- es porque todavía no hay copy de la complementaria contratada
       -- En ese caso se copian los incrementos de las parcelas de la complementaria en las del anexo
       IF (v_isCopy AND algunaParcelaConIncremento (P_IDANEXO) = false) THEN
          pq_utl.log(lc, 'Se copian los incrementos de la póliza con id ' || v_idPoliza || ' al anexo con id ' || P_IDANEXO);
          copiarIncrementosDesdePoliza (P_IDANEXO, v_idPoliza);
          COMMIT;
       END IF;
       
           
      EXCEPTION
            WHEN OTHERS THEN
               pq_utl.log(lc, 'Ocurrió un error al copiar las parcelas en el anexo. ERROR: ' || SQLERRM);
               ROLLBACK;
      END;-- Exception
    
    COMMIT;

  END copiarParcelasEnAnexo;
  
 
 /*
  * ---------------------------------------------------------------------------------------
  *                          Main Copy - copiarParcelasFromCopy
  * ---------------------------------------------------------------------------------------
  */
  PROCEDURE copiarParcelasFromCopy(P_IDCOPY  IN NUMBER,
                                   P_IDANEXO   IN NUMBER)  IS
                                   
                                   
      TYPE TpCursor IS REF CURSOR;
      l_tp_cursor     TpCursor;
      l_sql           VARCHAR2(2000);
     
    
      l_idparcela     NUMBER;
      
  
  BEGIN
      BEGIN -- Exception
      
      -- Copio las parcelas con idPoliza = P_IDPOLIZA
      IF (P_IDCOPY IS NOT NULL) THEN
          l_sql := 'select p.id from tb_copy_parcelas p, tb_copy_capitales_aseg ca, tb_sc_c_tipo_capital tc ' || 
                   'where p.id = ca.idparcela and ca.codtipocapital = tc.codtipocapital ' || 
                   'and tc.codconcepto = 68 and p.idcopy = ' || P_IDCOPY;
        
          OPEN l_tp_cursor FOR l_sql;
          LOOP FETCH l_tp_cursor INTO l_idparcela;
              EXIT WHEN l_tp_cursor%NOTFOUND;
              setParcelaFromCopy(l_idparcela, P_IDANEXO); -- Copio la parcela
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
                               p_idanexo    IN NUMBER) IS
                                 
   
   
    TYPE TpCursor IS REF CURSOR;
    l_tp_cursor           TpCursor;
    l_sql                 VARCHAR2(120);
    l_idcapaseg           NUMBER;
    l_newidparcela_anexo  NUMBER;
    
    
  BEGIN
  
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
       recintosigpac)

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
             recintosigpac
             
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
    l_sql                VARCHAR2(2000);
    l_iddatvar           NUMBER;
    l_newidcapaseg_anexo NUMBER;
    
    
  BEGIN
  
    -- Copiamos el Capital Asegurado de la poliza al Anexo
    INSERT INTO tb_anexo_mod_capitales_aseg
      (id, idparcelaanexo, codtipocapital, superficie, precio, produccion, incrementoanterior)
      SELECT 
          SQ_CAPITALES_ASEGURADOS.Nextval as idcapitalasegurado,
          p_idparcela_anexo, codtipocapital, superficie, precio,
          produccion, INCREMENTOPRODUCCION
       FROM tb_copy_capitales_aseg
       WHERE id = p_idcapaseg;
  
  
  
  
    -- Recuperamos el id del Capital Asegurado creado
    execute immediate 'select SQ_CAPITALES_ASEGURADOS.CURRVAL from DUAL'
      into l_newidcapaseg_anexo;
  
  
  
  
    -- Copiamos los Datos Variables del Capital Asegurado
    l_sql := 'select id from tb_copy_datos_var_parc where idcapitalasegurado = ' || p_idcapaseg;
    OPEN l_tp_cursor FOR l_sql;
    LOOP FETCH l_tp_cursor INTO l_iddatvar;
        EXIT WHEN l_tp_cursor%NOTFOUND;
        setDatosVariablesFromCopy(l_iddatvar, l_newidcapaseg_anexo);
    END LOOP;
  
   
  
  END setCapitalAseguradoFromCopy;
  
  
  /*
  * ---------------------------------------------------------------------------------------
  *                          3.COPY - setDatosVariablesFromCopy 
  * ---------------------------------------------------------------------------------------
  */
  PROCEDURE setDatosVariablesFromCopy(p_iddatvar        IN NUMBER,
                                      p_idcapaseg_anexo IN NUMBER) IS
   BEGIN                                   
   -- Copio el Dato Variable de la Copy al anexo
    INSERT INTO tb_anexo_mod_capitales_dts_vbl
        (id,
         idcapitalasegurado, 
         codconcepto, 
         valor,
         CODCONCEPTOPPALMOD,
         CODRIESGOCUBIERTO)
         
         SELECT 
             SQ_ANEXO_MOD_CAPITALES_DTS_VBL.Nextval as iddatovariable,
             p_idcapaseg_anexo,
             codconcepto,
             valor,
             CODCONCEPTOPPALMOD,
             CODRIESGOCUBIERTO
             
        FROM tb_copy_datos_var_parc
       WHERE id = p_iddatvar;
  
    
  
  END setDatosVariablesFromCopy;
  
   

 /*
  * ---------------------------------------------------------------------------------------
  *                        Main Poliza - copiarParcelasFromPoliza
  * ---------------------------------------------------------------------------------------
  */
  PROCEDURE copiarParcelasFromPoliza(P_IDPOLIZA  IN NUMBER,
                                     P_IDANEXO   IN NUMBER) IS
                              
                              
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
            setParcelaFromPoliza(l_idparcela, P_IDANEXO); -- Copio la parcela
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
                                 p_idanexo    IN NUMBER) IS
                       
                       
                       
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
             altaencomplementario)
      
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
                   altaencomplementario
                   
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
                                          p_idparcela_anexo IN NUMBER) IS
                                
                                
    TYPE TpCursor IS REF CURSOR;
    l_tp_cursor               TpCursor;     
    l_sql                VARCHAR2(120);
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
       produccion,
       INCREMENTOANTERIOR)
       
      SELECT 
          SQ_ANEXO_MOD_CAPITALES_ASEG.Nextval as idcapitalasegurado,
          p_idparcela_anexo,
          codtipocapital,
          superficie,
          precio,
          produccion,
          incrementoproduccion
        
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
  END setCapitalAseguradoFromPoliza;

 
  /*
  * ---------------------------------------------------------------------------------------
  *                          3.POLIZA - setDatosVariablesFromPoliza 
  * ---------------------------------------------------------------------------------------
  */
  PROCEDURE setDatosVariablesFromPoliza(p_iddatvar        IN NUMBER,
                                        p_idcapaseg_anexo IN NUMBER) IS
  BEGIN
  
    -- Arrastramos el Dato Variable sin fecha de la Copy a la Poliza
    INSERT INTO tb_anexo_mod_capitales_dts_vbl
        (id,
         idcapitalasegurado, 
         codconcepto, 
         valor,
         CODCONCEPTOPPALMOD,
         CODRIESGOCUBIERTO)
         
         SELECT 
             SQ_ANEXO_MOD_CAPITALES_DTS_VBL.Nextval as iddatovariable,
             p_idcapaseg_anexo,
             cdat.codconcepto,
             cdat.valor,
             cdat.CODCONCEPTOPPALMOD,
             cdat.CODRIESGOCUBIERTO
             
        FROM tb_datos_var_parcela cdat, tb_sc_dd_dic_datos dd
       WHERE iddatovariable = p_iddatvar
       and cdat.codconcepto= dd.codconcepto
       and dd.codtiponaturaleza != 4;
       
    -- Arrastramos el Dato Variable con fecha de la Copy a la Poliza
     BEGIN   
        INSERT INTO tb_anexo_mod_capitales_dts_vbl
            (id,
             idcapitalasegurado, 
             codconcepto, 
             valor,
             CODCONCEPTOPPALMOD,
             CODRIESGOCUBIERTO)
             
             SELECT 
                 SQ_ANEXO_MOD_CAPITALES_DTS_VBL.Nextval as iddatovariable,
                 p_idcapaseg_anexo,
                 cdat.codconcepto,
                 TO_CHAR(TO_DATE(cdat.valor, 'YYYY/MM/DD'),'DD/MM/YYYY'),
                 cdat.CODCONCEPTOPPALMOD,
                 cdat.CODRIESGOCUBIERTO
                 
             FROM tb_datos_var_parcela cdat, tb_sc_dd_dic_datos dd
           WHERE iddatovariable = p_iddatvar
           and cdat.codconcepto= dd.codconcepto
           and dd.codtiponaturaleza = 4;
      EXCEPTION
        WHEN OTHERS THEN
             INSERT INTO tb_anexo_mod_capitales_dts_vbl
            (id,
             idcapitalasegurado, 
             codconcepto, 
             valor,
             CODCONCEPTOPPALMOD,
             CODRIESGOCUBIERTO)
             
             SELECT 
                 SQ_ANEXO_MOD_CAPITALES_DTS_VBL.Nextval as iddatovariable,
                 p_idcapaseg_anexo,
                 cdat.codconcepto,
                 cdat.valor,
                 cdat.CODCONCEPTOPPALMOD,
                 cdat.CODRIESGOCUBIERTO
                 
             FROM tb_datos_var_parcela cdat, tb_sc_dd_dic_datos dd
           WHERE iddatovariable = p_iddatvar
           and cdat.codconcepto= dd.codconcepto
           and dd.codtiponaturaleza = 4;
     END;
  END setDatosVariablesFromPoliza;


 /* ------------------------------------------------------------------------------
  *                              UTILS
  * ------------------------------------------------------------------------------
  */

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
                WHEN NO_DATA_FOUND THEN
                     v_idCopy := 0;
       END;
       
        RETURN v_idCopy;
        
  END getIdCopyPoliza;
  
  -- Devuelve un booleano indicando si alguna parcela del anexo correspondiente al id pasado como parámetro
  -- tiene incremento
  FUNCTION algunaParcelaConIncremento (IDANEXO IN NUMBER) RETURN BOOLEAN
  IS
    v_countInc NUMBER := 0;
  BEGIN
        execute immediate ('select count(*) 
        from o02agpe0.tb_anexo_mod_capitales_aseg amca, o02agpe0.tb_anexo_mod_parcelas amp
        where amca.incrementoproduccion is not null and amca.incrementoproduccion > 0
        and amca.idparcelaanexo= amp.id
        and amp.idanexo= ' || IDANEXO) into v_countInc;
        
        IF (v_countInc > 0) THEN RETURN TRUE;
        ELSE RETURN FALSE;        
        END IF;
        
  END algunaParcelaConIncremento;
  
  -- Copia los incrementos de las parcelas de la póliza indicada por el parámetro 'idpoliza' en
  -- las parcelas correspondientes del anexo indicado por el parámetro 'idanexo'
  PROCEDURE copiarIncrementosDesdePoliza (IDANEXO IN NUMBER, IDPOLIZA IN NUMBER) IS
  
  TYPE TpCursor IS REF CURSOR;
  l_tp_cursor     TpCursor;
  l_sql           VARCHAR2(2000);
  
  -- Variables para volcar los datos al recorrer las parcelas
  v_hoja          NUMBER(3);
  v_numero        NUMBER(3);
  v_incremento    NUMBER(11,2);
  
  BEGIN
       
       l_sql := 'select par.hoja, par.numero, ca.incrementoproduccion
                 from o02agpe0.tb_capitales_asegurados ca, o02agpe0.tb_parcelas par
                 where ca.idparcela = par.idparcela
                 and par.idpoliza = ' || IDPOLIZA || 
                 ' and ca.altaencomplementario = ''S''
                 and ca.incrementoproduccion is not null';
                 
       OPEN l_tp_cursor FOR l_sql;
       LOOP FETCH l_tp_cursor INTO v_hoja, v_numero, v_incremento;
            EXIT WHEN l_tp_cursor%NOTFOUND;
            -- Actualiza el incremento de producción del anexo
            UPDATE o02agpe0.tb_anexo_mod_capitales_aseg ca set ca.altaencomplementario='S', ca.incrementoanterior=v_incremento
            WHERE CA.IDPARCELAANEXO IN (
                  SELECT amp.id FROM o02agpe0.tb_anexo_mod_parcelas amp 
                  WHERE amp.idanexo = IDANEXO and amp.hoja = v_hoja and amp.numero = v_numero
            ) AND CA.ID in (SELECT amca.id FROM o02agpe0.tb_anexo_mod_parcelas amp1, o02agpe0.tb_anexo_mod_capitales_aseg amca 
                  WHERE amp1.id = amca.idparcelaanexo and amp1.idanexo = IDANEXO and amp1.hoja = v_hoja and amp1.numero = v_numero);
       END LOOP;
  END;

end PQ_COPIAR_PARCELAS_CPL_A_ANEXO;
/
SHOW ERRORS;
