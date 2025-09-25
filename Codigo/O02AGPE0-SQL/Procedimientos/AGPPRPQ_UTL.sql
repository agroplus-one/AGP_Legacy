SET DEFINE OFF;
SET SERVEROUTPUT ON;

CREATE OR REPLACE PACKAGE o02agpe0.PQ_UTL IS
/******************************************************************************
   NAME:       UTL
   PURPOSE:    Package de utilidades

   REVISIONS:
   Ver        Date        Author           Description
   ---------  ----------  ---------------  ------------------------------------
   1.1        02/06/2010  T-SYSTEMS        1. Created this package.

   NOTES:

******************************************************************************/

-------------------------------------------------------------------------------

FUNCTION getcfg
-- Recoge de la tabla TB_CONFIG_AGP el campo valor a traves del NEMO.
(
  p_nemo   in TB_CONFIG_AGP.AGP_nemo%type
)
RETURN
  TB_CONFIG_AGP.AGP_valor%type;
  
  
-------------------------------------------------------------------------------
PROCEDURE setcfg (p_nemo tb_config_agp.agp_nemo%TYPE,
                  p_valor tb_config_agp.agp_valor%TYPE);
-------------------------------------------------------------------------------
FUNCTION getcfgAndDelete(p_nemo IN tb_config_agp.agp_nemo%TYPE) RETURN tb_config_agp.agp_valor%TYPE;
-------------------------------------------------------------------------------

PROCEDURE log
(
  p_text   in varchar2,
  p_nivel  in number   default 2  -- niveles 1,2
);

-------------------------------------------------------------------------------

PROCEDURE log
(
  p_lc     in varchar2,
  p_text   in varchar2,
  p_nivel  in number   default 2  -- niveles 1,2
);

END PQ_UTL; 

 
/
CREATE OR REPLACE PACKAGE BODY o02agpe0.PQ_UTL AS

-------------------------------------------------------------------------------

FUNCTION getcfg
(
  p_nemo   IN TB_CONFIG_AGP.AGP_nemo%TYPE
)
RETURN
  TB_CONFIG_AGP.AGP_valor%TYPE
IS

v_valor TB_CONFIG_AGP.AGP_valor%TYPE := NULL;

BEGIN

  SELECT AGP_valor
    INTO v_valor
	   FROM TB_CONFIG_AGP
   WHERE AGP_nemo = p_nemo;

   RETURN v_valor;

EXCEPTION
  WHEN NO_DATA_FOUND THEN
    RETURN v_valor;
  WHEN OTHERS THEN
    PQ_Err.raiser('utl.getcfg');

END getcfg;

-------------------------------------------------------------------------------


PROCEDURE setcfg (p_nemo tb_config_agp.agp_nemo%TYPE,
                  p_valor tb_config_agp.agp_valor%TYPE)  IS

v_count NUMBER;

BEGIN


	log('utl.getcfg', 'NEMO RECIBIDO:' || p_nemo);
	log('utl.getcfg', 'VALOR RECIBIDO:' || p_valor);


SELECT count(*) into v_count FROM o02agpe0.tb_config_agp where AGP_NEMO = p_nemo;

IF v_count > 0 THEN
    -- Actualiza el valor en la tabla de configuracion
    UPDATE o02agpe0.tb_config_agp SET AGP_VALOR = p_valor WHERE AGP_NEMO = p_nemo;
ELSE

    INSERT INTO o02agpe0.tb_config_agp (AGP_NEMO, AGP_VALOR, AGP_DESCRIPCION) values(p_nemo, p_valor, p_nemo);

END IF;

COMMIT;

EXCEPTION
   WHEN OTHERS THEN
        PQ_Err.raiser('utl.getcfg');

END setcfg;

-------------------------------------------------------------------------------

FUNCTION getcfgAndDelete(p_nemo IN tb_config_agp.agp_nemo%TYPE) RETURN tb_config_agp.agp_valor%TYPE IS

v_valor tb_config_agp.agp_valor%TYPE;

BEGIN

    SELECT AGP_VALOR into v_valor FROM o02agpe0.tb_config_agp where AGP_NEMO = p_nemo;

    IF v_valor IS NOT NULL THEN
        DELETE FROM o02agpe0.tb_config_agp where AGP_NEMO = p_nemo;
    END IF;

    RETURN v_valor;
EXCEPTION
  WHEN NO_DATA_FOUND THEN
    RETURN v_valor;
  WHEN OTHERS THEN
    PQ_Err.raiser('utl.getcfg');
END;


-------------------------------------------------------------------------------

PROCEDURE LOG
(
  p_text    IN VARCHAR2,
  p_nivel   IN NUMBER     DEFAULT 2
)
IS

BEGIN

PQ_Utl.LOG
(
  p_lc      => PQ_Typ.gb_procedure,
  p_text    => p_text,
  p_nivel   => p_nivel
);

END;

-------------------------------------------------------------------------------

PROCEDURE LOG
-- Crea el fichero de log si no existe.
-- Aunque la variable de entrada p_test admite un texto de hasta 32000 caracteres,
-- el procedimiento recorta los 994 primeros caracteres y aC1ade un indicador de que
-- se ha recortado la linea.
-- El vi no admite lineas que son mas largas de 1050 caracters.
(
  p_lc      IN VARCHAR2,
  p_text    IN VARCHAR2,
  p_nivel   IN NUMBER     DEFAULT 2
)
IS

fo_log        UTL_FILE.FILE_TYPE;
v_linea       VARCHAR2(32000);
l_nivel       PLS_INTEGER;
l_dirlog      VARCHAR2(2000);

ex_noescribir EXCEPTION;

BEGIN
--
--NULL;
--
  -- Actualizamos el localizador siempre, aunque no se imprima la traza.
  -- ES UN DATO MUY IMPORTANTE EL PROCEDIMIENTO QUE SOLICITA LA TRAZA.
  -- PERMITE UN SEGUIMIENTO,
  -- SI NO SE ACTUALIZA PUEDE CONFUNDIR AL MOSTRAR OTRO NOMBRE DE PROCEDIMIENTO.
  PQ_Typ.putpr(p_lc);
  --
  -- Comprobar que hay que escribir algo. (no se permiten lineas en blanco)
  IF p_text IS NULL THEN
    RAISE ex_noescribir;
  END IF;
  --
  -- Comprobar el nivel de trazas.
  IF p_nivel NOT IN (1,2) THEN
    RAISE ex_noescribir;
  END IF;
  --
  -- Comprobar que se ha de escribir la traza por el nivel de log.
  l_nivel := NVL(PQ_Utl.getcfg(PQ_Typ.nivellog),0);
  IF p_nivel > l_nivel THEN
    RAISE ex_noescribir;
  END IF;
  --
  -- recortamos el mensaje de entrada
  v_linea := SUBSTR(p_text,1,1000);
  -- Aqadimos el nombre del procedimiento que lo invoca.
  v_linea := PQ_Typ.getpr||' '||v_linea;
  -- Indentamos la linea en funcion del n: de orden del procedimiento ejecutado.
  v_linea := LPAD(v_linea,LENGTH(v_linea)+((PQ_Typ.getnumpr-1)*2),' ');
  --
  -- formateamos el mensaje de entrada
  --v_linea := to_char(f_linea, 'dd-mm-yy hh24:mi:ss')||'-'||v_linea;
  v_linea := TO_CHAR(SYSTIMESTAMP, 'dd-mm-yy hh24:mi:ss.ff')||'-'||v_linea;
  -- verificamos el tamaqo de la linea para que nunca supere el maximo
  -- que permite el editor vi en UNIX.
  IF LENGTH(v_linea) > 1000 THEN
     v_linea := SUBSTR(v_linea,1,994)||' -> ++';
  END IF;
  --
  l_dirlog := PQ_Utl.getcfg(PQ_Typ.dirlog);
  IF l_dirlog IS NULL THEN
    RAISE ex_noescribir;
  END IF;
  --
  -- Siempre intentamos aqadir una linea
  IF (SUBSTR(PQ_Typ.gb_nomlog,1,6) = TO_CHAR(SYSDATE,'yymmdd')) THEN
    fo_log := UTL_FILE.FOPEN (l_dirlog, PQ_Typ.gb_nomlog, 'a', PQ_Typ.MAX_LINEFILESIZEWRITE);
  ELSE
    PQ_Typ.gb_nomlog := '%DATE_%ID_trazasBD.log';
    PQ_Typ.gb_nomlog := REPLACE(PQ_Typ.gb_nomlog,'%ID',00);
    PQ_Typ.gb_nomlog := REPLACE(PQ_Typ.gb_nomlog,' ');
    PQ_Typ.gb_nomlog := REPLACE(PQ_Typ.gb_nomlog,'%DATE',TO_CHAR(SYSDATE,'yymmdd'));
    fo_log := UTL_FILE.FOPEN (l_dirlog, PQ_Typ.gb_nomlog, 'a', PQ_Typ.MAX_LINEFILESIZEWRITE);
    UTL_FILE.PUT_LINE (fo_log, RPAD('=',80,'='));
    UTL_FILE.PUT_LINE (fo_log, 'INICIO DEL LOG DEL DIA');
    UTL_FILE.PUT_LINE (fo_log, RPAD('=',80,'='));
  END IF;

  -- imprimimos la linea
  UTL_FILE.PUT_LINE (fo_log, v_linea);
  --DBMS_OUTPUT.PUT_LINE(v_linea);

  -- cerramos el fichero.
  UTL_FILE.FCLOSE (fo_log);

EXCEPTION
  WHEN ex_noescribir THEN
    NULL;

  WHEN OTHERS THEN
    PQ_Err.raiser(SQLCODE,'ERROR en utl.log',FALSE);

END LOG;

END PQ_UTL; 
/
SHOW ERRORS;