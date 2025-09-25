DEFINE OFF;
SET SERVEROUTPUT ON;
create or replace package o02agpe0.PQ_LIBERAR_ASEGURADOS is

  -- Author  : U028982
  -- Created : 21/10/2016 13:21:20
  -- Purpose :

   lc VARCHAR2(25) := 'PQ_LIBERAR_ASEGURADOS.'; -- Variable que almacena el nombre del paquete y de la función

   PROCEDURE liberarAsegurados ;

end PQ_LIBERAR_ASEGURADOS;
/
create or replace package body o02agpe0.PQ_LIBERAR_ASEGURADOS is

 PROCEDURE liberarAsegurados IS

   TYPE TpCursor                         IS REF CURSOR;
   l_tp_cursor                           TpCursor;
   l_sql                                 VARCHAR2(2000);
   v_codUsuario                          o02agpe0.tb_usuarios.codusuario%type;
   num_usu                       NUMBER :=0;
   num_usu_ok                    NUMBER :=0;
   num_usu_ko                    NUMBER :=0;

   BEGIN

   l_sql := 'select u.codusuario from o02agpe0.tb_usuarios u where u.idasegurado is not null';

   OPEN l_tp_cursor FOR l_sql;
      LOOP FETCH l_tp_cursor INTO v_codUsuario;
          EXIT WHEN l_tp_cursor%NOTFOUND;
                BEGIN
                    num_usu := num_usu + 1 ;
                    update o02agpe0.tb_usuarios u set idclase=null,idasegurado=null where u.codusuario = '' || v_codUsuario || '';
                    commit;
                    num_usu_ok := num_usu_ok+ 1;
                    --dbms_output.put_line('USUARIO LIBRE: '||v_codUsuario);
                    --EXIT WHEN num_usu_ok > 10; -- PARA PRUEBAS
                EXCEPTION
                    WHEN OTHERS THEN
                        dbms_output.put_line('ERROR al liberar el usuario: '||v_codUsuario ||' - '|| SQLCODE || ' - ' || SQLERRM);
                        PQ_Utl.LOG (lc, 'ERROR al liberar el usuario: '||v_codUsuario ||' - '|| SQLCODE || ' - ' || SQLERRM || '. ', 2);
                        rollback;
                        num_usu_ko := num_usu_ko + 1;

                END;
    END LOOP;
    dbms_output.put_line('TOTAL USUARIOS A LIBERAR: '||num_usu);
    dbms_output.put_line('TOTAL USUARIOS LIBERADOS: '||num_usu_ok);
    PQ_Utl.LOG (lc, 'TOTAL USUARIOS A LIBERAR: '||num_usu, 2);
    PQ_Utl.LOG (lc, 'TOTAL USUARIOS LIBERADOS: '||num_usu_ok, 2);

  END liberarAsegurados;
end PQ_LIBERAR_ASEGURADOS;
/
SHOW ERRORS;