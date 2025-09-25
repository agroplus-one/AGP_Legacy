SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_COPIA_PARCELAS is
-- Procedimiento principal
PROCEDURE copiarParcelasEnPolCpl(P_IDPOLIZA1 IN NUMBER,P_IDPOLIZA2   IN NUMBER);
--Procedimientos que copian parcelas,capitales Aseg y cap Aseg rel mod, de una poliza en otra poliza--
PROCEDURE copiarParcelasFromPoliza(P_IDPOLIZA1  IN NUMBER,P_IDPOLIZA2   IN NUMBER);
PROCEDURE setParcelaFromPoliza(p_idparcela  IN NUMBER,P_IDPOLIZA2    IN NUMBER);
PROCEDURE setCapitalAseguradoFromPoliza(p_idcapaseg       IN NUMBER,p_idparcela_nueva IN NUMBER, p_idparcela IN NUMBER);
PROCEDURE setDatosVariablesFromPoliza(p_iddatvar        IN NUMBER,p_idcapaseg IN NUMBER);
PROCEDURE setRiesgosParcela(p_idparcela IN NUMBER, p_idcapaseg IN NUMBER);
--Procedimientos que copian parcelas,capitales Aseg ,de polizaCopy en otra poliza--
PROCEDURE copiarParcelasFromCopy(P_IDCOPY  IN NUMBER,P_IDPOLIZA2   IN NUMBER);
PROCEDURE setParcelaFromCopy(p_idparcela  IN NUMBER,P_IDPOLIZA2    IN NUMBER);
PROCEDURE setCapitalAseguradoFromCopy(p_idcapaseg       IN NUMBER,p_idparcela IN NUMBER);
PROCEDURE setDatosVariablesFromCopy(p_iddatvar        IN NUMBER,p_idcapaseg IN NUMBER);

PROCEDURE parseConcepto363Copy(p_idcapaseg IN NUMBER);

--Utils
FUNCTION getIdPoliza (p_referencia IN VARCHAR2) RETURN NUMBER;
FUNCTION getIdCopyPoliza(p_idpoliza IN NUMBER) RETURN NUMBER;

end PQ_COPIA_PARCELAS;
/
create or replace package body o02agpe0.PQ_COPIA_PARCELAS is
------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
PROCEDURE copiarParcelasEnPolCpl(P_IDPOLIZA1 IN NUMBER,P_IDPOLIZA2   IN NUMBER) IS
 
     v_idPoliza NUMBER(10);
     v_idCopy NUMBER(10);
      
 BEGIN
     BEGIN -- Exception
        
        v_idCopy := getIdCopyPoliza(P_IDPOLIZA1);
        --v_idPoliza := getIdPoliza(p_referencia);
             v_idPoliza:=  P_IDPOLIZA1;
        IF v_idCopy = 0 OR v_idCopy is null THEN  -- no hay copy                   
                 copiarParcelasFromPoliza(v_idPoliza,P_IDPOLIZA2);               
        ELSE                -- si hay copy             
                 copiarParcelasFromCopy(v_idCopy,P_IDPOLIZA2);                   
        END IF;  
                   
           
      EXCEPTION      
            WHEN NO_DATA_FOUND THEN
               ROLLBACK;               
      END;-- Exception
    
    COMMIT;

  END copiarParcelasEnPolCpl;
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
 PROCEDURE copiarParcelasFromPoliza(P_IDPOLIZA1  IN NUMBER,
                                     P_IDPOLIZA2   IN NUMBER) IS
                                  
                                                            
    TYPE TpCursor IS REF CURSOR;
    l_tp_cursor     TpCursor;
    l_tp_cursor2    TpCursor;
    l_sql           VARCHAR2(100);
    l_sql2          VARCHAR2(100);
    l_idparcela     NUMBER;
    l_idcapiAse     NUMBER;
    l_codtipocapt   NUMBER;
    l_codconcepto   NUMBER;
    l_count         NUMBER;
    
   
  BEGIN
    BEGIN -- Exception
    
       
    -- Copio las parcelas con idPoliza = P_IDPOLIZA
    IF (P_IDPOLIZA1 IS NOT NULL AND P_IDPOLIZA2 IS NOT NULL) THEN
        l_sql := 'select idparcela from tb_parcelas where tipoparcela = ''P'' and idpoliza = ' || P_IDPOLIZA1;
      
        OPEN l_tp_cursor FOR l_sql;
        LOOP FETCH l_tp_cursor INTO l_idparcela;
            EXIT WHEN l_tp_cursor%NOTFOUND;

               -- solo se mete la parcela si tiene algún tipo de capital asegurado con codconcepto = 68
               l_sql2 := 'select idcapitalasegurado from tb_capitales_asegurados where idparcela = ' ||  l_idparcela;
                OPEN l_tp_cursor2 FOR l_sql2;
                LOOP FETCH l_tp_cursor2 INTO l_idcapiAse;
                     EXIT WHEN l_tp_cursor2%NOTFOUND;
                     
                     SELECT codtipocapital INTO l_codtipocapt FROM tb_capitales_asegurados tca WHERE tca.idcapitalasegurado= l_idcapiAse; 
                     SELECT codconcepto INTO l_codconcepto FROM tb_sc_c_tipo_capital tc WHERE tc.codtipocapital = l_codtipocapt; 
                     
                     IF l_codconcepto = 68 THEN
                       l_count := 1;
                     END IF;                  
                     
                END LOOP;
                
                
                IF l_count = 1 THEN
                    setParcelaFromPoliza(l_idparcela, P_IDPOLIZA2); -- Copio la parcela  
                    -- Reseteamos la cuenta
                    l_count := 0;
                END IF;
            
        END LOOP;
    END IF;
  

  
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
         ROLLBACK;
    END;-- Exception
    
    COMMIT;

 END copiarParcelasFromPoliza;

----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
 PROCEDURE setParcelaFromPoliza(p_idparcela  IN NUMBER,
                                 P_IDPOLIZA2    IN NUMBER) IS
                       
                       
                       
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
             hoja,
             numero,
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
                   hoja,
                   numero,
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
                
                
                IF l_codconcepto = 68 THEN
                    setCapitalAseguradoFromPoliza(l_idcapaseg, l_newidparcela,p_idparcela); -- Copia el capital Asegurado
                END IF;
                
                
                
                
                
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
  PROCEDURE setDatosVariablesFromPoliza(p_iddatvar        IN NUMBER,
                                        p_idcapaseg IN NUMBER) IS
  BEGIN  
    pq_utl.log('setDatosVariablesFromPoliza ('||p_iddatvar||', ' || p_idcapaseg || ');****', 2);
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
  PROCEDURE setCapitalAseguradoFromPoliza(p_idcapaseg       IN NUMBER,
                                          p_idparcela_nueva IN NUMBER,
                                          p_idparcela IN NUMBER) IS
  
   TYPE TpCursor IS REF CURSOR;
    l_tp_cursor               TpCursor;   
    l_sql                VARCHAR2(120);
    l_iddatvar           NUMBER;
    l_newidcapaseg NUMBER;
               
  BEGIN  
    -- Copiamos el Capital Asegurado de la poliza 
     INSERT INTO tb_capitales_asegurados
      (idparcela, 
       idcapitalasegurado, 
       codtipocapital, 
       superficie, 
       precio,
       produccion,     
       preciomodif,
       produccionmodif)
       
      SELECT 
           p_idparcela_nueva, 
           Sq_Capitales_Asegurados.Nextval as id, 
           cap.codtipocapital, 
           cap.superficie, 
           carm.precio,
           carm.produccion,     
           null,
           null
       
       FROM tb_cap_aseg_rel_modulo carm,tb_capitales_asegurados cap
       WHERE carm.idcapitalasegurado = p_idcapaseg
       AND  carm.idcapitalasegurado= cap.idcapitalasegurado;
       
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
    
    --Copiamos las coberturas de parcela como un dato variable 
    setRiesgosParcela(p_idparcela,l_newidcapaseg);         
  
  END setCapitalAseguradoFromPoliza;  
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
PROCEDURE copiarParcelasFromCopy(P_IDCOPY  IN NUMBER,
                                   P_IDPOLIZA2   IN NUMBER)  IS                               
      TYPE TpCursor IS REF CURSOR;
      l_tp_cursor     TpCursor;
      l_tp_cursor2    TpCursor;
      l_sql           VARCHAR2(2000);    
      l_sql2          VARCHAR2(2000);  
      l_idparcela     NUMBER;
      l_idcapiAse     NUMBER;
      l_codtipocapt   NUMBER;
      l_codconcepto   NUMBER;
      l_count         NUMBER;
      
  BEGIN
      BEGIN -- Exception
      
      -- Copio las parcelas con idPoliza = P_IDPOLIZA
      IF (P_IDCOPY IS NOT NULL AND P_IDPOLIZA2 IS NOT NULL) THEN
          l_sql := 'select distinct p.id from tb_copy_parcelas p, tb_copy_capitales_aseg c where p.id = c.idparcela and c.codtipocapital <100 and  p.idcopy = ' || P_IDCOPY;
        
          OPEN l_tp_cursor FOR l_sql;
          LOOP FETCH l_tp_cursor INTO l_idparcela;
              EXIT WHEN l_tp_cursor%NOTFOUND;
              
              -- solo se mete la parcela si tiene algún tipo de capital asegurado con codconcepto = 68
              l_sql2 := 'select id from tb_copy_capitales_aseg where idparcela = ' ||  l_idparcela;
              OPEN l_tp_cursor2 FOR l_sql2;
              LOOP FETCH l_tp_cursor2 INTO l_idcapiAse;
                  EXIT WHEN l_tp_cursor2%NOTFOUND;
                  SELECT codtipocapital INTO l_codtipocapt FROM tb_copy_capitales_aseg tca WHERE tca.id= l_idcapiAse; 
                  SELECT codconcepto INTO l_codconcepto FROM tb_sc_c_tipo_capital tc WHERE tc.codtipocapital = l_codtipocapt; 
                     
                  IF l_codconcepto = 68 THEN
                      l_count := 1;
                  END IF;
              END LOOP;
              
              IF l_count = 1 THEN
                  setParcelaFromCopy(l_idparcela, P_IDPOLIZA2); -- Copio la parcela
                  -- Reseteamos la cuenta
                  l_count := 0;
              END IF;
          END LOOP;
      END IF;        

      EXCEPTION
            WHEN NO_DATA_FOUND THEN
               ROLLBACK;
      END;-- Exception
    
      COMMIT;

  END copiarParcelasFromCopy;
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
PROCEDURE setParcelaFromCopy(p_idparcela  IN NUMBER,
                               P_IDPOLIZA2    IN NUMBER) IS   
    TYPE TpCursor IS REF CURSOR;
    l_tp_cursor           TpCursor;
    l_sql                 VARCHAR2(120);
    l_idcapaseg           NUMBER;
    l_newidparcela  NUMBER;
    l_codtipocapt         NUMBER;
    l_codconcepto         NUMBER;
     
  BEGIN  
    -- Copiamos la parcela de la copy 
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
             tipoparcela)

      SELECT 
             Sq_Parcelas.Nextval as id,             
             P_IDPOLIZA2,                           
             codprovincia,
             codtermino,
             NVL( subtermino, ' ' ),                        
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
             null,
             null,
             'P'          
             
       FROM tb_copy_parcelas
       WHERE id = p_idparcela;  
  
    -- Recupero el id de la parcela creada
    execute immediate 'select Sq_Parcelas.CURRVAL from DUAL'
      into l_newidparcela;   
      
    -- Copiamos los Capitales Asegurados
    l_sql := 'select id from tb_copy_capitales_aseg where idparcela = ' || p_idparcela;
    OPEN l_tp_cursor FOR l_sql;
    LOOP FETCH l_tp_cursor INTO l_idcapaseg;
        EXIT WHEN l_tp_cursor%NOTFOUND;
        
        -- Recupero el codtipocapital y luego el codconcepto
        SELECT codtipocapital INTO l_codtipocapt FROM tb_copy_capitales_aseg tca WHERE tca.id = l_idcapaseg; 
        SELECT codconcepto INTO l_codconcepto FROM tb_sc_c_tipo_capital tc WHERE tc.codtipocapital = l_codtipocapt; 
        
        IF l_codconcepto = 68 THEN
            setCapitalAseguradoFromCopy(l_idcapaseg, l_newidparcela); -- Copia el capital Asegurado
        END IF;
    END LOOP;  
   
   END setParcelaFromCopy;
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
PROCEDURE setCapitalAseguradoFromCopy(p_idcapaseg       IN NUMBER,
                                        p_idparcela IN NUMBER) IS  
                                        
   TYPE TpCursor IS REF CURSOR;
    l_tp_cursor          TpCursor;
    l_sql                VARCHAR2(2000);
    l_iddatvar           NUMBER;
    l_newidcapaseg NUMBER; 
                                        
 BEGIN  
    -- Copiamos el Capital Asegurado de la poliza al Anexo
    INSERT INTO tb_capitales_asegurados
              (idparcela, 
               idcapitalasegurado, 
               codtipocapital, 
               superficie, 
               precio,
               produccion,     
               preciomodif,
               produccionmodif)       
      SELECT 
          p_idparcela,
          SQ_CAPITALES_ASEGURADOS.Nextval as idcapitalasegurado,         
          codtipocapital,
          superficie,  
          precio,
          produccion,
          null,
          null
               
        FROM tb_copy_capitales_aseg
       WHERE id = p_idcapaseg;   
       
         -- Recuperamos el id del Capital Asegurado creado
    execute immediate 'select SQ_CAPITALES_ASEGURADOS.CURRVAL from DUAL'
      into l_newidcapaseg;
   
   
    -- Copiamos los Datos Variables del Capital Asegurado
    l_sql := 'select id from tb_copy_datos_var_parc where codconcepto <> -1 and idcapitalasegurado = ' || p_idcapaseg;
    OPEN l_tp_cursor FOR l_sql;
    LOOP FETCH l_tp_cursor INTO l_iddatvar;
        EXIT WHEN l_tp_cursor%NOTFOUND;
        setDatosVariablesFromCopy(l_iddatvar, l_newidcapaseg);
    END LOOP;
    
    --parseamos el valor del concepto 363 (N,S) a -1 o -2
    parseConcepto363Copy(l_newidcapaseg);
       
  END setCapitalAseguradoFromCopy;
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------  
PROCEDURE parseConcepto363Copy(p_idcapaseg IN NUMBER) IS

BEGIN

      update o02agpe0.tb_datos_var_parcela datvar 
      set datvar.valor = '-2' 
      where datvar.idcapitalasegurado = p_idcapaseg 
      and datvar.codconcepto = 363 
      and datvar.valor = 'N';
      
      update o02agpe0.tb_datos_var_parcela datvar 
      set datvar.valor = '-1' 
      where datvar.idcapitalasegurado = p_idcapaseg 
      and datvar.codconcepto = 363 
      and datvar.valor = 'S';

END parseConcepto363Copy;  
  

 ----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------- 
  PROCEDURE setDatosVariablesFromCopy(p_iddatvar        IN NUMBER,
                                      p_idcapaseg IN NUMBER) IS                                
   BEGIN             

      -- Copio el Dato Variable SIN FECHA de la Copy al anexo
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
             cdat.codconcepto,
             cdat.valor,             
             cdat.CODCONCEPTOPPALMOD,
             cdat.CODRIESGOCUBIERTO
             
        FROM tb_copy_datos_var_parc cdat, tb_sc_dd_dic_datos dd
       WHERE id = p_iddatvar
       AND cdat.codconcepto = dd.codconcepto
       AND dd.codtiponaturaleza != 4;
       
       
      BEGIN  
       -- Copio el Dato Variable CON FECHA de la Copy al anexo
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
                 cdat.codconcepto,                 
                 TO_CHAR(TO_DATE(cdat.valor,'yyyy/mm/dd'),'dd/mm/yyyy'),            
                 cdat.CODCONCEPTOPPALMOD,
                 cdat.CODRIESGOCUBIERTO
                 
            FROM tb_copy_datos_var_parc cdat, tb_sc_dd_dic_datos dd
           WHERE id = p_iddatvar
           AND cdat.codconcepto = dd.codconcepto
           AND dd.codtiponaturaleza = 4;
           
       EXCEPTION
          WHEN OTHERS THEN
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
                 cdat.codconcepto,
                 cdat.valor,            
                 cdat.CODCONCEPTOPPALMOD,
                 cdat.CODRIESGOCUBIERTO
                 
            FROM tb_copy_datos_var_parc cdat, tb_sc_dd_dic_datos dd
           WHERE id = p_iddatvar
           AND cdat.codconcepto = dd.codconcepto
           AND dd.codtiponaturaleza = 4;
       END;
   
  -- ELSE
   
   
   
  -- END IF;
                         
   
  
    
  
  END setDatosVariablesFromCopy;
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
FUNCTION getIdPoliza (p_referencia IN VARCHAR2) RETURN NUMBER
  IS
      v_idPoliza NUMBER :=0;
  
  BEGIN
        BEGIN
        
              SELECT idPoliza into v_idPoliza 
              FROM tb_polizas 
              WHERE referencia= p_referencia
              AND tiporef = 'P';
              
       EXCEPTION
                WHEN NO_DATA_FOUND THEN
                     v_idPoliza := 0;
       END;
       
      RETURN v_idPoliza; 
  
  END getIdPoliza;
  
  --GETIDCOPY RECUPERA EL IDCOPY DE UNA POLIZA
  FUNCTION getIdCopyPoliza(p_idpoliza IN NUMBER) RETURN NUMBER
  IS
    v_idCopy NUMBER := 0;
  BEGIN
       BEGIN
       
            SELECT max(id) INTO v_idCopy
            FROM tb_copy_polizas 
            WHERE refpoliza = 
            (select referencia from tb_polizas where idpoliza = p_idpoliza);          
            
       EXCEPTION
                WHEN NO_DATA_FOUND THEN
                     v_idCopy := 0;
       END;
       
        RETURN v_idCopy;
        
  END getIdCopyPoliza;
end PQ_COPIA_PARCELAS;
/
SHOW ERRORS;
