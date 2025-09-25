SET DEFINE OFF;
SET SERVEROUTPUT ON;
CREATE OR REPLACE PACKAGE O02AGPE1.PQ_TYP AUTHID CURRENT_USER IS
/******************************************************************************
   NAME:       TYP
   PURPOSE:    Tipos generales, constantes, etc
               Procedimientos genericos asociados a constantes, etc

   REVISIONS:
   Ver        Date        Author           Description
   ---------  ----------  ---------------  ------------------------------------
   1.0        02/06/2010  T-SYSTEMS        1. Created this package.

   NOTES:

******************************************************************************/

-------------------------------------------------------------------------------
-- CONSTANTES GENERALES

-- CONSTANTE MAXIMO TAMAQO DE LINEA ESCRITO EN FICHERO
MAX_LINEFILESIZEWRITE    CONSTANT PLS_INTEGER             := 1050;

-- CONSTANTE GENERAL SIN USO ESPECIFICO
TRUE         						CONSTANT NUMBER(1)                := 1;
FALSE        						CONSTANT NUMBER(1) 		  := 0;



-- CONSTANTES UTILIZADAS PARA SABER SI LAS CONDICIONES O CONTROLES
-- SE RELACIONAN POR OR o AND.
Y            						CONSTANT VARCHAR2(3)   		  := 'AND';
O            						CONSTANT VARCHAR2(2)   		  := 'OR';



-- CONSTANTES UTILIZADAS PARA LAS FUNCIONES QUE SON INVOCADAS DE MANERA DINAMICA
-- COMO EN LOS PAQUETES:
--    GETDATA
--    CTRLFUNC , ETC
-- INDICAN QUE LA FUNCION HA IDO BIEN -> OK
-- O HAN IDO MAL -> KO
OK                      CONSTANT PLS_INTEGER              := 0;
KO                      CONSTANT PLS_INTEGER              := 1;
-- VARIABLE AQADIDA PARA SABER SI UNA SELECT DINAMICA RESPONDE UN NODATAFOUND
ND                      CONSTANT PLS_INTEGER              := -1;

-- VALORES DE LA TABLA CGM_CONFIG_CMFG
NIVELLOG     						CONSTANT VARCHAR2(30) := 'NIVELLOG';

-- constantes del directorio del logs del pl
DIRLOG       						CONSTANT VARCHAR2(30)             := 'DIRLOGCOREPL';



-------------------------------------------------------------------------------
-- GLOBALES

-- global que indica el nombre del procedimiento actual.
gb_procedure       	varchar2(61);
gb_numproc              pls_integer := 0;
gb_seqlog               integer;
gb_nomlog               varchar2(255);

-------------------------------------------------------------------------------

-- TIPOS, TABLAS, ARRAYS
type rc_generic 				is ref cursor;  -- ref cursor generico

-- Table con la pila de procedimientos utilizados para identar el fichero
-- de log
type va_proc            is table of pls_integer
			index by varchar2(61);
--
gbva_proc               va_proc; -- pila de procedimientos a utilizar
gbva_proc_null          va_proc; -- pila vacia para inicializar.
--

------------------------------------------------------------------------------

PROCEDURE putpr
-- Registra la funcion que se ejecuta en cada momento
-- Guarda un historico de las funciones de la session.
-- Implementa un sistema de overflow que no permite ser invocado
-- mas de 50 veces.
(
  p_tex in varchar2
);

------------------------------------------------------------------------------

FUNCTION getpr
RETURN VARCHAR2;
-- Recupera la funcion que se ejecuta ahora mismo.

------------------------------------------------------------------------------

FUNCTION getnumpr
RETURN PLS_INTEGER;
-- Recupera la el numero de orden de funcion que se
-- ejecuta ahora mismo.

-------------------------------------------------------------------------------

FUNCTION initx
RETURN DATE;
-- Cambia la variable global para que cuando se escriba una traza de log
-- se marque el principio de una transaccion.
-------------------------------------------------------------------------------

PROCEDURE inipp;
-- Inicio de procedimiento publico.
-- Cambia la variable global para que cuando se escriba una traza de log
-- se marque como principio de procedimiento publico.
-- Es decir se empieza a identar en la columna 0 y se borra el array de procs.
-------------------------------------------------------------------------------

END PQ_TYP;
/
CREATE OR REPLACE PACKAGE BODY O02AGPE1.PQ_TYP AS

-------------------------------------------------------------------------------

PROCEDURE putpr
(
  p_tex in varchar2
)
IS BEGIN

PQ_typ.gb_procedure := substr(p_tex,1,61);

-- si no existe el procedimiento en la pila, lo aqadimos
if PQ_typ.gb_procedure is not null then
  if not PQ_typ.gbva_proc.exists(PQ_typ.gb_procedure) then
    --
    if PQ_typ.gb_numproc = 100 then
      -- si superamos las 100 funciones se inicializa la tabla.
      PQ_typ.gb_numproc := 1;
      PQ_typ.gbva_proc   := PQ_typ.gbva_proc_null;
    else
      PQ_typ.gb_numproc := PQ_typ.gb_numproc + 1;
    end if;
    --
    PQ_typ.gbva_proc(PQ_typ.gb_procedure) := PQ_typ.gb_numproc;
    --
  end if;
end if;

END;

-------------------------------------------------------------------------------

FUNCTION getpr
RETURN varchar2
IS BEGIN

 return(PQ_typ.gb_procedure);

END;

-------------------------------------------------------------------------------

FUNCTION getnumpr
RETURN PLS_INTEGER
IS BEGIN

  if PQ_typ.gbva_proc.exists(PQ_typ.gb_procedure) then
    return(gbva_proc(PQ_typ.gb_procedure));
  end if;

  return (0);

END;

-------------------------------------------------------------------------------

FUNCTION initx
RETURN DATE
IS
BEGIN

  gb_numproc    := 0;
  gbva_proc     := gbva_proc_null;

  return (sysdate);

END;

-------------------------------------------------------------------------------

PROCEDURE inipp
IS
BEGIN

  gb_numproc    := 0;
  gbva_proc     := gbva_proc_null;

END;

END PQ_TYP;
/
SHOW ERRORS;