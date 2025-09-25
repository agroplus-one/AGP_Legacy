SET DEFINE OFF;
SET SERVEROUTPUT ON;
create or replace package o02agpe1.PQ_IMPORTACION_CSV AUTHID CURRENT_USER is

  -- Author  : U028783
  -- Created : 26/10/2012
  -- Purpose : Cargar los datos enviados por Agroseguro en el modelo de la aplicación Agroplus.

  --Procedimiento para crear el backup de las tablas a importar.
  PROCEDURE PR_CREATE_BACKUP (TABLAS IN VARCHAR2);

end PQ_IMPORTACION_CSV;
/
CREATE OR REPLACE PACKAGE BODY O02AGPE1.PQ_IMPORTACION_CSV IS

   -- Procedimiento para copiar los datos de las tablas indicadas como parametro a las tablas donde se guarda
   -- la copia de seguridad para poder recuperar los datos en caso de error.
   PROCEDURE PR_CREATE_BACKUP (TABLAS IN VARCHAR2) IS
      consulta VARCHAR2 (2000) := 'SELECT NOMBRE, TABLA_BAK FROM o02agpe0.tb_tablas_xmls t WHERE t.NUMTABLA IN (' || tablas || ')';

      TYPE cur_typ IS REF CURSOR;
      c_tablas cur_typ;

      sql_backup varchar2(2000);
      tabla_ori  VARCHAR2(30);
      tabla_bak  VARCHAR2(30);
   BEGIN
      --o02agpe0.pq_utl.LOG ('******** CREANDO BACKUP.... ********', 2);
      OPEN c_tablas FOR consulta;
      LOOP
         FETCH c_tablas INTO tabla_ori, tabla_bak;
         EXIT WHEN c_tablas%NOTFOUND;

         dbms_output.put_line('PR_CREATE_BACKUP: fetch  ');
         --EXECUTE IMMEDIATE 'TRUNCATE TABLE '||tabla_bak;
         truncate_table(tabla_bak);
         dbms_output.put_line('PR_CREATE_BACKUP: truncate  ');
         sql_backup := 'INSERT INTO o02agpe1.' || tabla_bak || ' (SELECT * FROM o02agpe0.' || tabla_ori || ')';
         dbms_output.put_line('PR_CREATE_BACKUP: ' || sql_backup || '  ');
         execute immediate sql_backup;
         dbms_output.put_line('OK');
      END LOOP;
      close c_tablas;
      commit;
   EXCEPTION
       WHEN OTHERS THEN
           rollback;
           dbms_output.put_line('Error al crear el backup: ' || SQLCODE || ' - ' || SQLERRM || '. ');
   END PR_CREATE_BACKUP;

END PQ_IMPORTACION_CSV;
/
SHOW ERRORS;