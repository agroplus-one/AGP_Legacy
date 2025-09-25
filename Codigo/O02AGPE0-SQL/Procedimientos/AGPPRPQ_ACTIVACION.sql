SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_ACTIVACION is
  -- Author  : Ernesto Laura de la Fuente
  -- Created : 22/7/2010 10:03:12
  -- Purpose : 
  
  -- Public type declarations
  
  -- Public constant declarations

  -- Public variable declarations

  -- Public function and procedure declarations
   FUNCTION FN_COMPRUEBAREGISTROSPL (idLineaSeguro IN NUMBER, forzarActivar IN VARCHAR2) return NUMBER;
   
   -- Funcion para comprobar que las tablas mínimas de la importacion tienen al menos un registro.
   FUNCTION FN_PERMITE_ACTIVACION (idLineaSeguro IN NUMBER) return NUMBER;
   
   --DAA 08/08/2013
   FUNCTION FN_MOSTRAR_COBERTURAS (idLineaSeguro IN NUMBER) return NUMBER;
   
   FUNCTION FN_ESLINEA_GANADO (idLineaSeguro IN NUMBER) return NUMBER;
   
end PQ_ACTIVACION;
/
create or replace package body o02agpe0.PQ_ACTIVACION is

--Valores devolucion:
--         1 : Todo correcto
--        -1 : Si alguna de las tablas importadas no tiene estado 'Importado'
--        -2 : Si existe alguna tabla del condicionado para este P/L que no tiene registros
--        -3 : Si no estan configuradas las pantallas configurables
--        -4 : Si alguna de las pantallas configurables no tiene datos variables configurados
  FUNCTION FN_COMPRUEBAREGISTROSPL (idLineaSeguro IN NUMBER, forzarActivar IN VARCHAR2) return NUMBER
  IS
	TYPE cur_typ     IS REF CURSOR;
    cursorTablas     CUR_TYP;
    sentenciaTablas  VARCHAR2(500);
	
    registros        NUMBER := -1;
    sentenciaCount   VARCHAR2(1000);
    
    idPantalla       NUMBER(15);
    esLineaGanado   NUMBER(1);
    pantallaConfig   VARCHAR(20);
  BEGIN
    BEGIN
    -- PASOS PARA ACTIVAR UN PLAN/LINEA:
    --     1. Todas las tablas del detalle de la importacion deben tener estado 'Importado'.
    --     2. Todos los registros de TB_TABLAS_XMLS que tienen ACTIVACION=SI deben tener 
    --        al menos un registro.
    --     3. La línea debe tener configuradas las pantallas de Polizas y Siniestros.
    
    IF(forzarActivar != 'true')THEN
            
            -- INICIO DE LA PRIMERA COMPROBACION
            IF (FN_PERMITE_ACTIVACION(idLineaSeguro) = -2) THEN
               RETURN -2;
            END IF;
            -- FIN DE LA PRIMERA COMPROBACION
                       
            -- INICIO DE LA SEGUNDA COMPROBACION: Comprobamos las pantallas configurables
            esLineaGanado:=FN_ESLINEA_GANADO(idLineaSeguro);
             IF  esLineaGanado = 0 THEN
                pantallaConfig:='POLIZA';
             ELSE 
                pantallaConfig:='EXPLOTACIONES';
             END IF;
            sentenciaCount := 'SELECT COUNT(*) FROM TB_PANTALLAS_CONFIGURABLES PC
                               WHERE PC.LINEASEGUROID = ' || idLineaSeguro
                               ||' AND PC.IDPANTALLA IN (
                               SELECT P.IDPANTALLA FROM TB_PANTALLAS P WHERE P.OBJETOPANTALLA IN ('''|| pantallaConfig || '''))'; 
            EXECUTE IMMEDIATE sentenciaCount INTO registros;
            --La consulta debe devolver 1 registros. En caso contrario no se permitirá activar
            IF (registros < 1) THEN
               RETURN -3;
            END IF;
          
            
             -- ESC-18606
            -- Comprobar si tiene configuraciones que hacer
            select count(*) into registros from tb_sc_oi_org_info where lineaseguroid = idLineaSeguro AND coduso = '31' AND codubicacion IN ('31', '33', '29', '27', '16');
            IF (registros > 0) THEN

	            --Comprobamos que TODAS las pantallas configurables para este P/L esten configuraedas
	            sentenciaTablas := 'select idpantallaconfigurable from tb_pantallas_configurables where obligatoria = 1 and lineaseguroid = '||idLineaSeguro;
	            OPEN cursorTablas FOR sentenciaTablas;
	            LOOP
	                FETCH cursorTablas INTO idPantalla;
	                EXIT WHEN cursorTablas%NOTFOUND;
	                sentenciaCount := 'SELECT COUNT(*) FROM TB_CONFIGURACION_CAMPOS CC WHERE CC.IDPANTALLACONFIGURABLE = ' || idPantalla;
	                EXECUTE IMMEDIATE sentenciaCount INTO registros;
	                IF (registros = 0) THEN
	                   CLOSE cursorTablas;
	                   RETURN -4;
	                END IF;
	            END LOOP;
	            CLOSE cursorTablas;
	        END IF;
            -- FIN DE LA SEGUNDA COMPROBACION
    END IF;
	-- Actualizamos el estado de la línea
    UPDATE TB_LINEAS SET ACTIVO = 'SI', FECHAACTIVACION = SYSDATE, MAXPOLIZASPPAL = 1 WHERE LINEASEGUROID = idLineaSeguro;
    COMMIT;
    
    RETURN 1;
    
    EXCEPTION 
    WHEN OTHERS THEN
         RETURN -1;
    END;
  END FN_COMPRUEBAREGISTROSPL;
  
  -- Funcion para comprobar que las tablas mínimas de la importacion para la activacion tienen al menos un registro.
  FUNCTION FN_PERMITE_ACTIVACION (idLineaSeguro IN NUMBER) return NUMBER IS
    TYPE cur_typ     IS REF CURSOR;
    cursorTablas     CUR_TYP;
    sentenciaTablas  VARCHAR2(500);
    
    nombreTabla      tb_tablas_xmls.nombre%TYPE;
    tipoSc           tb_tablas_xmls.tiposc%TYPE;
    tipoLinea        TB_SC_C_LINEAS.CODGRUPOSEGURO%TYPE;
    sentenciaCount   VARCHAR2(1000);
    registros        NUMBER := -1;
  BEGIN
  
  		--Calcular si la línea es de agro o(A01) de ganado (G01)
    select CODGRUPOSEGURO
      into tipoLinea
      from TB_SC_C_LINEAS sclin
     where sclin.codlinea =
           (select codlinea
              from tb_lineas
             where lineaseguroid = idLineaSeguro);
  
       IF tipoLinea = 'A01' THEN
          sentenciaTablas := 'SELECT NOMBRE, TIPOSC FROM TB_TABLAS_XMLS WHERE ACTIVACION IN(''SC'',''SA'')';
       ELSIF tipoLinea = 'G01' THEN
          sentenciaTablas := 'SELECT NOMBRE, TIPOSC FROM TB_TABLAS_XMLS WHERE ACTIVACION IN(''SC'',''SG'')';
       END IF;
          
        OPEN cursorTablas FOR sentenciaTablas;
        LOOP
            FETCH cursorTablas INTO nombreTabla, tipoSc;
            EXIT WHEN cursorTablas%NOTFOUND;
            IF (tipoSc = 'CPL') THEN
               --Comprobamos que hay al menos un registro en la tabla para el plan/línea indicado
               sentenciaCount := 'SELECT COUNT(*) FROM '|| nombreTabla || ' WHERE LINEASEGUROID = ' || idLineaSeguro;
            ELSE
               --Comprobamos que hay al menos un registro en la tabla ('ORG' o 'GEN')
               sentenciaCount := 'SELECT COUNT(*) FROM '|| nombreTabla;
            END IF;

            EXECUTE IMMEDIATE sentenciaCount INTO registros;
            IF (registros = 0) THEN
               CLOSE cursorTablas;
               RETURN -2;
            END IF;
        END LOOP;
        CLOSE cursorTablas;
        RETURN 1;
        
    EXCEPTION 
    WHEN OTHERS THEN
         RETURN -7;
         
  END FN_PERMITE_ACTIVACION;
  
  -- DAA 08/08/2013
  -- Funcion para comprobar las tablas mínimas de la importacion para mostar el cuadro de coberturas.
  --------------------------------------------------------------------------------------------
  FUNCTION FN_MOSTRAR_COBERTURAS (idLineaSeguro IN NUMBER) return NUMBER IS
    TYPE cur_typ     IS REF CURSOR;
    cursorTablas     CUR_TYP;
    sentenciaTablas  VARCHAR2(500);
    
    nombreTabla      tb_tablas_xmls.nombre%TYPE;
    tipoSc           tb_tablas_xmls.tiposc%TYPE;
    sentenciaCount   VARCHAR2(1000);
    sentenciaGanado  VARCHAR2(1000);
    ganado           VARCHAR2(4);
    registros        NUMBER := -1;
  BEGIN		 
	RETURN 1;        
  END FN_MOSTRAR_COBERTURAS;
  --------------------------------------------------------------------------------------------

 FUNCTION FN_ESLINEA_GANADO (idLineaSeguro IN NUMBER) return NUMBER IS
   tipoLinea        TB_SC_C_LINEAS.CODGRUPOSEGURO%TYPE;
   BEGIN
  
  		--Calcular si la línea es de agro o(A01) de ganado (G01)
    select CODGRUPOSEGURO
      into tipoLinea
      from TB_SC_C_LINEAS sclin
     where sclin.codlinea =
           (select codlinea
              from tb_lineas
             where lineaseguroid = idLineaSeguro);
  
       IF tipoLinea = 'A01' THEN
          RETURN 0;
       ELSIF tipoLinea = 'G01' THEN
          RETURN 1;
       END IF;
END FN_ESLINEA_GANADO;

end PQ_ACTIVACION;
/

SHOW ERRORS;