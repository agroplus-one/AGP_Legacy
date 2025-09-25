SET DEFINE OFF;
SET SERVEROUTPUT ON;
create or replace package o02agpe0.PQ_HISTORICO_ESTADOS is

  -- Author  : U029769
  -- Created : 26/06/2013 14:42:45
  PROCEDURE pr_insertar_estado (v_tabla IN VARCHAR2, v_secuencia IN VARCHAR2, v_usuario IN VARCHAR2,
                                       id_objeto IN NUMBER, estado IN NUMBER) ;
  PROCEDURE pr_insertar_estado_anexo (v_tabla IN VARCHAR2, v_secuencia IN VARCHAR2, v_usuario IN VARCHAR2,
                                       id_objeto IN NUMBER, estado IN NUMBER, estadoAgro IN VARCHAR2) ;
  PROCEDURE pr_insertar_estado_poliza (v_tabla IN VARCHAR2, v_secuencia IN VARCHAR2, v_usuario IN VARCHAR2,
                                       id_objeto IN NUMBER, estado IN NUMBER, tipo_pago IN NUMBER, fecha_primer_pago IN VARCHAR2,
                                       pct_primer_pago IN NUMBER, fecha_segundo_pago IN VARCHAR2, pct_segundo_pago IN NUMBER);
  PROCEDURE pr_insertar_estados_recepcion (P_IDENVIO IN VARCHAR2, P_TIPOFICHERO IN VARCHAR2);

  PROCEDURE pr_insertar_estado_datos_aseg (v_tabla IN VARCHAR2, v_secuencia IN VARCHAR2, v_usuario IN VARCHAR2,
                                       id_objeto IN NUMBER, estado IN VARCHAR2);

    PROCEDURE pr_insertar_pago_poliza (v_idPoliza IN NUMBER, v_idagp IN NUMBER, v_idpago IN NUMBER,v_usuario IN VARCHAR2,
                                          v_fecha IN VARCHAR2);
end PQ_HISTORICO_ESTADOS;
/
create or replace package body o02agpe0.PQ_HISTORICO_ESTADOS is

  PROCEDURE pr_insertar_estado (v_tabla IN VARCHAR2, v_secuencia IN VARCHAR2, v_usuario IN VARCHAR2,
                                       id_objeto IN NUMBER, estado IN NUMBER)  is

  BEGIN

     execute immediate 'insert into ' || v_tabla || ' values (' || v_secuencia ||', '||id_objeto||',
                                   '''|| v_usuario ||''', sysdate ,' || estado||')';
     commit;
	 
  END pr_insertar_estado;

  PROCEDURE pr_insertar_estado_anexo (v_tabla IN VARCHAR2, v_secuencia IN VARCHAR2, v_usuario IN VARCHAR2,
                                       id_objeto IN NUMBER, estado IN NUMBER, estadoAgro IN VARCHAR2)  is

  BEGIN

     execute immediate 'insert into ' || v_tabla || ' values (' || v_secuencia ||', '||id_objeto||',
                                   '''|| v_usuario ||''', sysdate ,' || estado||', '''|| estadoAgro || ''')';
     commit;
	 
  END pr_insertar_estado_anexo;

  PROCEDURE pr_insertar_estado_poliza (v_tabla IN VARCHAR2, v_secuencia IN VARCHAR2, v_usuario IN VARCHAR2,
                                       id_objeto IN NUMBER, estado IN NUMBER, tipo_pago IN NUMBER, fecha_primer_pago IN VARCHAR2,
                                       pct_primer_pago IN NUMBER, fecha_segundo_pago IN VARCHAR2, pct_segundo_pago IN NUMBER)  is
  tipo_pagoF varchar2(4);
  pct_primer_pagoF VARCHAR2(6);
  pct_segundo_pagoF varchar2(6);
  fecha_primer_pagoF varchar2(12);
  fecha_segundo_pagoF varchar2(12);
  BEGIN
     if (tipo_pago is not null) then
        tipo_pagoF := to_char(tipo_pago);
     else
        tipo_pagoF := 'null';
     end if;
     if (fecha_primer_pago is not null or fecha_primer_pago !='')then
        fecha_primer_pagoF := fecha_primer_pago;
        --fecha_primer_pagoF := ''''||to_char(fecha_primer_pago,'dd/mm/YYYY')||'''';
     else
         fecha_primer_pagoF := 'null';
     end if;
     if (pct_primer_pago is not null) then
        pct_primer_pagoF := to_char(pct_primer_pago, 'FM999.99');
     else
        pct_primer_pagoF := 'null';
     end if;

     if (pct_segundo_pago is not null) then
        pct_segundo_pagoF := to_char(pct_segundo_pago, 'FM999.99');
     else
        pct_segundo_pagoF := 'null';
     end if;
     if (fecha_segundo_pago is not null or fecha_segundo_pago !='')then
        fecha_segundo_pagoF := fecha_segundo_pago;
        --fecha_segundo_pagoF := ''''||to_char(fecha_segundo_pago,'dd/mm/YYYY')||'''';
     else
        fecha_segundo_pagoF := 'null';
     end if;


     dbms_output.put_line('insert into ' || v_tabla || ' values (' || v_secuencia ||', '||id_objeto||',
                                   '''|| v_usuario ||''', sysdate ,' || estado||',
                                   '''||fecha_primer_pagoF||''',' || pct_primer_pagoF||','''|| fecha_segundo_pagoF||''',' ||pct_segundo_pagoF||',' ||tipo_pagoF||')');

     IF (fecha_primer_pagoF = 'null' AND fecha_segundo_pagoF = 'null') THEN
        execute immediate 'insert into ' || v_tabla || ' values (' || v_secuencia ||', '||id_objeto||',
                                   '''|| v_usuario ||''', sysdate ,' || estado||',
                                   '||fecha_primer_pagoF||',' || pct_primer_pagoF||','|| fecha_segundo_pagoF||',' ||pct_segundo_pagoF||',' ||tipo_pagoF||')';
     ELSIF fecha_segundo_pagoF = 'null' THEN
        execute immediate 'insert into ' || v_tabla || ' values (' || v_secuencia ||', '||id_objeto||',
                                   '''|| v_usuario ||''', sysdate ,' || estado||',
                                   TO_DATE('''||fecha_primer_pagoF||''', ''DD/MM/YY''),' || pct_primer_pagoF||','|| fecha_segundo_pagoF||',' ||pct_segundo_pagoF||',' ||tipo_pagoF||')';
     ELSE
         execute immediate 'insert into ' || v_tabla || ' values (' || v_secuencia ||', '||id_objeto||',
                                   '''|| v_usuario ||''', sysdate ,' || estado||',
                                   TO_DATE('''||fecha_primer_pagoF||''', ''DD/MM/YY''),' || pct_primer_pagoF||',TO_DATE('''|| fecha_segundo_pagoF||''', ''DD/MM/YY''),' ||pct_segundo_pagoF||',' ||tipo_pagoF||')';
     END IF;
     commit;
      --dbms_output.put_line('insert into ' || v_tabla || ' values (' || v_secuencia ||', '||id_objeto||',
        --                            '''|| v_usuario ||''', '''||to_date(to_char(sysdate, 'DD/MM/YYYY HH:MI:SS'))||''' ,' || estado||')');

  END pr_insertar_estado_poliza;

  PROCEDURE pr_insertar_estados_recepcion (P_IDENVIO IN VARCHAR2, P_TIPOFICHERO IN VARCHAR2) IS

     v_tabla_origen varchar2(30);
     v_tabla_destino varchar2(30);
     v_campo_id varchar2(30);
     v_campo_estado varchar2(30);
     v_secuencia varchar2(40);

     TYPE cur_typ IS REF CURSOR;
     c_datos cur_typ;

     v_idobjeto number;
     v_estado number;

  BEGIN
     --0. En función del tipo de fichero elijo la tabla donde insertar y el campo identificador a utilizar
     IF (P_TIPOFICHERO = 'P') THEN
        v_tabla_origen := 'TB_POLIZAS';
        v_tabla_destino := 'TB_POLIZAS_HISTORICO_ESTADOS';
        v_campo_id := 'idpoliza';
        v_campo_estado := 'idestado';
        v_secuencia := 'SQ_POLIZAS_HISTORICO_ESTADOS.nextval';
     ELSIF (P_TIPOFICHERO = 'R') THEN
        v_tabla_origen := 'TB_ANEXO_RED';
        v_tabla_destino := 'TB_ANEXO_RED_HISTORICO_ESTADOS';
        v_campo_id := 'id';
        v_campo_estado := 'idestado';
        v_secuencia := 'SQ_ANEXO_RED_HISTORICO_ESTADOS.nextval';
     ELSIF (P_TIPOFICHERO = 'M') THEN
        v_tabla_origen := 'TB_ANEXO_MOD';
        v_tabla_destino := 'TB_ANEXO_MOD_HISTORICO_ESTADOS';
        v_campo_id := 'id';
        v_campo_estado := 'estado';
        v_secuencia := 'SQ_ANEXO_MOD_HISTORICO_ESTADOS.nextval';
     ELSIF (P_TIPOFICHERO = 'S') THEN
        v_tabla_origen := 'TB_SINIESTROS';
        v_tabla_destino := 'TB_SINIESTRO_HISTORICO_ESTADOS';
        v_campo_id := 'id';
        v_campo_estado := 'estado';
        v_secuencia := 'SQ_SINIESTRO_HISTORICO_ESTADOS.nextval';
     END IF;

     --1. Buscar los identificadores de los objetos a partir del idenvio y el tipofichero
     OPEN c_datos FOR 'SELECT ' || v_campo_id || ', ' || v_campo_estado || ' FROM ' || v_tabla_origen || ' WHERE IDENVIO = ' || P_IDENVIO;
      LOOP
         FETCH c_datos INTO v_idobjeto, v_estado;
         EXIT WHEN c_datos%NOTFOUND;
            --2. Recorrer los objetos e ir insertando el estado para cada uno de ellos
            -- MPM --
            -- Diferenciación entre histórico de pólizas y demás registros
            IF (P_TIPOFICHERO = 'P') THEN
               pr_insertar_estado_poliza(v_tabla_destino, v_secuencia, null, v_idobjeto, v_estado, NULL, NULL, NULL, NULL, NULL);               
            ELSE
               pr_insertar_estado(v_tabla_destino, v_secuencia, null, v_idobjeto, v_estado);
            END IF;
            -- MPM --
      END LOOP;
      close c_datos;

  END pr_insertar_estados_recepcion;

  PROCEDURE pr_insertar_estado_datos_aseg (v_tabla IN VARCHAR2, v_secuencia IN VARCHAR2, v_usuario IN VARCHAR2,
                                       id_objeto IN NUMBER, estado IN VARCHAR2)  is

  BEGIN

     execute immediate 'insert into ' || v_tabla || ' values (' || v_secuencia ||', '||id_objeto||',
                                   '''|| v_usuario ||''', sysdate,''' || estado||''')';
     commit;


  END pr_insertar_estado_datos_aseg;

  PROCEDURE pr_insertar_pago_poliza (v_idPoliza IN NUMBER, v_idagp IN NUMBER, v_idpago IN NUMBER,v_usuario IN VARCHAR2,
                                          v_fecha IN VARCHAR2)  is

    BEGIN

       execute immediate 'insert into TB_PAGO_HISTORICO_PLZ values (SQ_PAGO_HISTORICO_PLZ.nextval, '||v_idPoliza||',
                                     '||v_idagp ||','|| v_idpago ||', '''|| v_usuario ||''', to_date('''||v_fecha||''', ''DD/MM/YYYY HH24:MI:SS''))';

       commit;


  END pr_insertar_pago_poliza;


end PQ_HISTORICO_ESTADOS;
/
SHOW ERRORS;