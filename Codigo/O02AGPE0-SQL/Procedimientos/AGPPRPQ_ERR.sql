CREATE OR REPLACE PACKAGE o02agpe0.PQ_ERR IS
/******************************************************************************
   NAME:       ERR
   PURPOSE:    Control generico de errores

   REVISIONS:
   Ver        Date        Author           Description
   ---------  ----------  ---------------  ------------------------------------
   1.0        02/06/2010  T-SYSTEMS        1. Created this package.

   NOTES:

******************************************************************************/

-- excepcion general cuando no hay definida ninguna expecifica.
ex_general EXCEPTION;
en_general CONSTANT INTEGER := -20001;
PRAGMA EXCEPTION_INIT (ex_general, -20001);

-- excepcion a propagar cuando la entrada de la funcion esta incompleta,
-- al faltar un parametro obligatorio.
ex_input_notvalid EXCEPTION;
en_input_notvalid CONSTANT INTEGER := -20100;
PRAGMA EXCEPTION_INIT (ex_input_notvalid, -20100);


ex_duplicado EXCEPTION;
en_duplicado CONSTANT INTEGER := -20600;
PRAGMA EXCEPTION_INIT (ex_duplicado, -20600);

-- excepcion a propagar cuando dentro del proceso se llama o se aplica una funcion
-- y esta no devuelve datos,y sin ese dato no se puede continuar.
ex_data_noexist EXCEPTION;
en_data_noexist CONSTANT INTEGER := -20102;
PRAGMA EXCEPTION_INIT (ex_data_noexist, -20102);

-- excepcion a propagar cuando al hacer un update o delete no afecta a
-- ningun registro (siempre que se requiera).
ex_dml_no_rows EXCEPTION;
en_dml_no_rows CONSTANT INTEGER := -20120;
PRAGMA EXCEPTION_INIT (ex_dml_no_rows, -20120);

-- excepcion a propagar cuando no se pasan los controles de una funcion
ex_no_pass_ctrl EXCEPTION;
en_no_pass_ctrl CONSTANT INTEGER := -20500;
PRAGMA EXCEPTION_INIT (ex_no_pass_ctrl, -20500);

-- excepcion a propagar cuando es una excepcion de oracle
ex_oracle EXCEPTION;
en_oracle CONSTANT INTEGER := -20999;
PRAGMA EXCEPTION_INIT (ex_oracle, -20999);





-------------------------------------------------------------------------------

PROCEDURE raiser
(
  p_msg    IN VARCHAR2  DEFAULT NULL
);

-------------------------------------------------------------------------------

PROCEDURE raiser
(
  p_err    IN  INTEGER  DEFAULT SQLCODE,
  p_msg    IN  VARCHAR2 DEFAULT NULL,
  p_log    IN  BOOLEAN  DEFAULT TRUE,
  p_others IN  BOOLEAN  DEFAULT FALSE
);

-------------------------------------------------------------------------------

PROCEDURE writefile_errlog
-- escribe el fichero de error y el fichero de log
--
(
  p_dirfile  IN VARCHAR2,
  p_fileerr  IN VARCHAR2,
  p_lineerr  IN VARCHAR2,
  p_linelog  IN VARCHAR2 DEFAULT NULL
);

-------------------------------------------------------------------------------

END PQ_ERR;
/
CREATE OR REPLACE PACKAGE BODY o02agpe0.PQ_ERR AS
-------------------------------------------------------------------------------

PROCEDURE raiser
(
  p_msg IN VARCHAR2 DEFAULT NULL
)
IS BEGIN

raiser (p_err => en_general,
        p_msg => p_msg);

END raiser;

-------------------------------------------------------------------------------

PROCEDURE raiser
(
  p_err    IN  INTEGER  DEFAULT SQLCODE,
  p_msg    IN  VARCHAR2 DEFAULT NULL,
  p_log    IN  BOOLEAN  DEFAULT TRUE,
  p_others IN  BOOLEAN  DEFAULT FALSE
)
IS
  l_msg varchar2(2000) :=  'Message no defined';

BEGIN
--
if p_msg is not null then
 l_msg := substr(p_msg,1,2000);
end if;

if p_log and p_err != -20999 then
  if PQ_typ.getpr is null then
    PQ_utl.log('***+ EXCEPTION ',1);
  else
    PQ_utl.log('***+ EXCEPTION in program: '||PQ_typ.getpr,1);
  end if;
  PQ_utl.log('       CODE:           '||P_ERR,1);
  PQ_utl.log('       MESSAGE:        '||l_msg,1);
  if (p_err BETWEEN -19999 AND 0) or p_err = 100 then
    PQ_utl.log('       MESSAGE ORACLE: '||sqlerrm,1);
  end if;
  PQ_utl.log('***- END EXCEPTION' ,1);
elsif p_log and p_err = -20999 then
  PQ_utl.log('*** EXCEPTION CONTROLED AND ALL READY CATCH IT. RAISED AGAIN.' ,1);
end if;

IF p_err BETWEEN -20999 AND -20000 THEN
  -- excepcion de usuario
  RAISE_APPLICATION_ERROR (p_err, l_msg,TRUE);
  --
ELSIF (p_err BETWEEN -19999 AND 0) or p_err = 100 THEN
   l_msg := 'EXCEPCION DE ORACLE'; --No la controlamos
  -- excepcion de oracle
  --RAISE_APPLICATION_ERROR (p_err, l_msg||': '||sqlerrm,TRUE);
  --RAISE;
  --
ELSE
  -- error no controlado
  PQ_utl.log('*** DECLARE '|| ' ex_nocontrolada EXCEPTION;  PRAGMA EXCEPTION_INIT (ex_nocontrolada, ' ||TO_CHAR (p_err)|| ');'||
    'BEGIN  RAISE ex_nocontrolada; END; ****' ,1);
  EXECUTE IMMEDIATE
    'DECLARE '||
    '  ex_nocontrolada EXCEPTION; ' ||
    '  PRAGMA EXCEPTION_INIT (ex_nocontrolada, ' ||TO_CHAR (p_err)|| ');'||
    'BEGIN  RAISE ex_nocontrolada; END;';
END IF;
--
END raiser;

-------------------------------------------------------------------------------

PROCEDURE writefile_errlog
-- escribe el fichero de error y el fichero de log
--
(
  p_dirfile  IN VARCHAR2,
  p_fileerr  IN VARCHAR2,
  p_lineerr  IN VARCHAR2,
  p_linelog  IN VARCHAR2 DEFAULT NULL
)
IS
  fo_err        UTL_FILE.FILE_TYPE;
  fo_log        UTL_FILE.FILE_TYPE;
  l_linea       VARCHAR2(2000);
BEGIN
PQ_Utl.LOG('writefile_errlog', 'Entro en la funcion writefile_errlog', 2);
  --
  IF p_fileerr IS NOT NULL OR
     p_lineerr IS NOT NULL OR
     p_dirfile IS NOT NULL THEN
    --
    fo_log   := UTL_FILE.FOPEN (LOCATION     => p_dirfile,
                                filename     => p_fileerr,
                                open_mode    => 'a',
                                max_linesize => PQ_Typ.MAX_LINEFILESIZEWRITE);
    --
    IF p_lineerr IS NOT NULL THEN
      l_linea := SUBSTR(p_lineerr||'#'||' '||p_linelog,1, 1050);
    ELSE
      l_linea := SUBSTR(p_linelog,1,1050);
    END IF;
    --
    UTL_FILE.PUT_LINE (fo_log, l_linea);
    UTL_FILE.FCLOSE( fo_log );
    --
  END IF;

END;

-------------------------------------------------------------------------------

END PQ_ERR;
/
