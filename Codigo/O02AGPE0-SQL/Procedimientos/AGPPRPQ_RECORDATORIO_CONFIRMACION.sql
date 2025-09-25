SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace PACKAGE o02agpe0.PQ_RECORDATORIO_CONFIRMACION 
AS

  TYPE tpcursor IS REF CURSOR; -- Tipo cursor
  TYPE polizas_usuario_map IS TABLE OF VARCHAR2(32767) INDEX BY VARCHAR2(8);

  TYPE fec_agr_map_type IS TABLE OF o02agpe0.tb_sc_c_fec_contrat_agr%ROWTYPE;
  TYPE fec_gan_map_type IS TABLE OF o02agpe0.tb_sc_c_fec_contrat_g%ROWTYPE;

  FUNCTION get_polizas(str_query varchar2, fechaEjecucion varchar2)
    RETURN polizas_usuario_map;

  FUNCTION enviar_record_polizas(fechaEjecucion varchar2) RETURN VARCHAR2;

  PROCEDURE procesar_usuarios(pol_map        polizas_usuario_map,
                              fechaEjecucion varchar2);

  FUNCTION enviar_correo(mensaje        varchar2,
                         email          varchar2,
                         fechaEjecucion varchar2) RETURN NUMBER;

  FUNCTION ult_dia_pago_agr(id_poliza number, cod_modulo varchar2)
    RETURN VARCHAR2;

  FUNCTION localiza_ult_dia_pago_agr(fec_agr_map    fec_agr_map_type,
                                     cod_cultivo    number,
                                     cod_variedad   number,
                                     cod_provincia  number,
                                     cod_comarca    number,
                                     cod_termino    number,
                                     cod_subtermino varchar2) RETURN DATE;

  FUNCTION ult_dia_pago_gan(id_poliza number, cod_modulo varchar2)
    RETURN VARCHAR2;

  FUNCTION localiza_ult_dia_pago_gan(fec_gan_map     fec_gan_map_type,
                                     cod_especie     number,
                                     cod_regimen     number,
                                     cod_grupo_raza  number,
                                     cod_tipo_animal number,
                                     cod_provincia   number,
                                     cod_comarca     number,
                                     cod_termino     number,
                                     cod_subtermino  varchar2) RETURN DATE;

END PQ_RECORDATORIO_CONFIRMACION;
/
create or replace PACKAGE BODY o02agpe0.PQ_RECORDATORIO_CONFIRMACION IS

  FUNCTION get_polizas(str_query varchar2, fechaEjecucion varchar2)
    RETURN polizas_usuario_map AS
    l_tp_cursor TPCURSOR; -- Cursor para la consulta de polizas
  
    l_pol_map polizas_usuario_map;
  
    codusuario    VARCHAR2(8);
    idpoliza      NUMBER(15);
    codplan       NUMBER(4);
    codlinea      NUMBER(3);
    codmodulo     VARCHAR2(5);
    clase         NUMBER(3);
    idcolectivo   VARCHAR2(7);
    entmediadora  NUMBER(4);
    subentmed     NUMBER(4);
    nifcif        VARCHAR2(9);
    nomaseg       VARCHAR2(100);
    descestado    VARCHAR2(30);
    codgruposeg   VARCHAR2(3);
    ultimodiapago VARCHAR2(10);
  
    lc VARCHAR2(200) := 'PQ_RECORDATORIO_CONFIRMACION.get_polizas'; -- Variable que almacena el nombre del paquete y de la funcion
  
    str_polizas     varchar2(32767);
    v_codusuarioAnt VARCHAR2(8);
  
  BEGIN
  
    pq_utl.Log(lc,
               '## INICIO FUNCION get_polizas PQ_RECORDATORIO_CONFIRMACION  ##',
               1);
    pq_utl.Log(lc, str_query, 1);
  
    v_codusuarioAnt := ' ';
  
    OPEN l_tp_cursor FOR str_query;
    LOOP
      FETCH l_tp_cursor
        INTO codusuario, idpoliza, codplan, codlinea, codmodulo, clase, idcolectivo, entmediadora, subentmed, nifcif, nomaseg, descestado, codgruposeg;
      EXIT WHEN l_tp_cursor%NOTFOUND;
      
      IF codgruposeg = 'A01' THEN
         ultimodiapago := o02agpe0.PQ_RECORDATORIO_CONFIRMACION.ult_dia_pago_agr(idpoliza, codmodulo);
      ELSE
          ultimodiapago := o02agpe0.PQ_RECORDATORIO_CONFIRMACION.ult_dia_pago_gan(idpoliza, codmodulo);
      END IF;
    
      pq_utl.Log(lc, '## poliza del usuario: ' || codusuario, 1);
      pq_utl.Log(lc,
                 codplan || '/' || codlinea || ' - Modulo: ' || codmodulo ||
                 ' - Clase: ' || clase || ' - Colectivo: ' || idcolectivo ||
                 ' - Entidad: ' || entmediadora || '-' || subentmed ||
                 ' - NIF: ' || nifcif || ' - ' || nomaseg ||
                 ' - Poliza en estado: ' || descestado ||
                 ' - Fec. limite: ' || ultimodiapago,
                 1);
    
      IF TO_DATE(ultimodiapago, 'DD/MM/YYYY') >=
         TO_DATE(fechaEjecucion, 'YYYYMMDD') THEN
      
        IF codusuario = v_codusuarioAnt THEN
          str_polizas := l_pol_map(codusuario);
          IF LENGTH(str_polizas) < 30000 THEN
            str_polizas := str_polizas || codplan || '/' || codlinea ||
                           ' - Modulo: ' || codmodulo || ' - Clase: ' ||
                           clase || ' - Colectivo: ' || idcolectivo ||
                           ' - Entidad: ' || entmediadora || '-' ||
                           subentmed || ' - NIF: ' || nifcif || ' - ' ||
                           nomaseg || ' - Poliza en estado: ' || descestado ||
                           ' - Fec. limite: ' || ultimodiapago ||
                           o02agpe0.PQ_ENVIO_CORREOS.v_retorno;
          ELSE
            str_polizas := str_polizas || '[...]' ||
                           o02agpe0.PQ_ENVIO_CORREOS.v_retorno;
          END IF;
        ELSE
          str_polizas     := codplan || '/' || codlinea || ' - Modulo: ' ||
                             codmodulo || ' - Clase: ' || clase ||
                             ' - Colectivo: ' || idcolectivo ||
                             ' - Entidad: ' || entmediadora || '-' ||
                             subentmed || ' - NIF: ' || nifcif || ' - ' ||
                             nomaseg || ' - Poliza en estado: ' ||
                             descestado || ' - Fec. limite: ' ||
                             ultimodiapago ||
                             o02agpe0.PQ_ENVIO_CORREOS.v_retorno;
          v_codusuarioAnt := codusuario;
        END IF;
      
        pq_utl.Log(lc, '## se incluye en el mapa', 1);
        l_pol_map(codusuario) := str_polizas;
      END IF;
    
    END LOOP;
  
    CLOSE l_tp_cursor;
  
    RETURN l_pol_map;
  
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      pq_utl.Log(lc,
                 '## ERROR en get_polizas PQ_RECORDATORIO_CONFIRMACION - No se han encontrado registros  ##',
                 1);
    
  END;

  FUNCTION enviar_record_polizas(fechaEjecucion varchar2) RETURN VARCHAR2 IS
    lc VARCHAR2(200) := 'PQ_RECORDATORIO_CONFIRMACION.enviar_record_polizas';
  
    l_query_pol varchar2(20000) := 'select
codusuario,
idpoliza,
codplan,
codlinea,
codmodulo,
clase,
idcolectivo,
entmediadora,
subentmediadora,
nifcif,
nomaseg,
desc_estado,
codgruposeguro
from (
(select distinct p.codusuario, p.idpoliza, l.codplan, l.codlinea, mp.codmodulo, p.clase, c.idcolectivo, c.entmediadora, c.subentmediadora, a.nifcif,
	decode(a.tipoidentificacion, ''CIF'', a.razonsocial, (a.nombre || '' '' || a.apellido1 || '' '' || a.apellido2)) as nomaseg,
	e.desc_estado, l1.codgruposeguro
from o02agpe0.tb_polizas p
inner join o02agpe0.tb_colectivos c on p.idcolectivo = c.id
inner join o02agpe0.tb_asegurados a on p.idasegurado = a.id
inner join o02agpe0.tb_estados_poliza e on p.idestado = e.idestado
inner join o02agpe0.tb_modulos_poliza mp on mp.idpoliza = p.idpoliza
inner join o02agpe0.tb_lineas l on l.lineaseguroid = p.lineaseguroid
inner join o02agpe0.tb_sc_c_lineas l1 on l1.codgruposeguro = ''G01'' and l1.codlinea = l.codlinea
inner join o02agpe0.tb_explotaciones ex on p.idpoliza = ex.idpoliza
inner join o02agpe0.tb_grupo_raza_explotacion gr on gr.idexplotacion = ex.id
inner join o02agpe0.tb_sc_c_fec_contrat_g fg on fg.lineaseguroid = l.lineaseguroid and fg.lineaseguroid = p.lineaseguroid
and to_char(fg.fec_contrat_fin, ''YYYYMMDD'') = ''' ||
                                   fechaEjecucion || '''
and fg.codmodulo = mp.codmodulo
and (fg.cod_riesgo_cbto_ele_cpm is null or fg.cod_riesgo_cbto_ele_cpm = mp.codriesgocubierto)
and (fg.codespecie = ex.especie or fg.codespecie = 999)
and (fg.codregimen = ex.regimen or fg.codregimen = 999)
and (fg.codgruporaza = gr.codgruporaza or fg.codgruporaza = 999)
and (fg.codtipoanimal = gr.codtipoanimal or fg.codtipoanimal = 999)
and (fg.codtipocapital = gr.codtipocapital or fg.codtipocapital = 999)
and (fg.codprovincia = ex.codprovincia or fg.codprovincia = 99)
and (fg.codcomarca = ex.codcomarca or fg.codcomarca = 99)
and (fg.codtermino = ex.codtermino or fg.codtermino = 999)
and (fg.subtermino = ex.subtermino or fg.subtermino = ''9'')
where p.idestado in (1, 2))
UNION
(select distinct p.codusuario, p.idpoliza, l.codplan, l.codlinea, mp.codmodulo, p.clase, c.idcolectivo, c.entmediadora, c.subentmediadora, a.nifcif,
	decode(a.tipoidentificacion, ''CIF'', a.razonsocial, (a.nombre || '' '' || a.apellido1 || '' '' || a.apellido2)) as nomaseg,
	e.desc_estado, l1.codgruposeguro
from o02agpe0.tb_polizas p
inner join o02agpe0.tb_colectivos c on p.idcolectivo = c.id
inner join o02agpe0.tb_asegurados a on p.idasegurado = a.id
inner join o02agpe0.tb_estados_poliza e on p.idestado = e.idestado
inner join o02agpe0.tb_modulos_poliza mp on mp.idpoliza = p.idpoliza
left outer join o02agpe0.tb_comparativas_poliza cp on cp.idpoliza = p.idpoliza and cp.idmodulo = mp.idmodulo and cp.codconcepto = 363
inner join o02agpe0.tb_lineas l on l.lineaseguroid = p.lineaseguroid
inner join o02agpe0.tb_sc_c_lineas l1 on l1.codgruposeguro = ''A01'' and l1.codlinea = l.codlinea
inner join o02agpe0.tb_parcelas pa on p.idpoliza = pa.idpoliza
inner join o02agpe0.tb_capitales_asegurados ca on ca.idparcela = pa.idparcela
left outer join o02agpe0.tb_datos_var_parcela dv_621 on dv_621.idcapitalasegurado = ca.idcapitalasegurado and dv_621.codconcepto = 621
left outer join o02agpe0.tb_datos_var_parcela dv_618 on dv_618.idcapitalasegurado = ca.idcapitalasegurado and dv_618.codconcepto = 618
left outer join o02agpe0.tb_datos_var_parcela dv_173 on dv_173.idcapitalasegurado = ca.idcapitalasegurado and dv_173.codconcepto = 173
left outer join o02agpe0.tb_datos_var_parcela dv_123 on dv_123.idcapitalasegurado = ca.idcapitalasegurado and dv_123.codconcepto = 123
inner join o02agpe0.tb_sc_c_fec_contrat_agr fa on fa.lineaseguroid = l.lineaseguroid and fa.lineaseguroid = p.lineaseguroid
and to_char(fa.fecfincontrata, ''YYYYMMDD'') = ''' ||
                                   fechaEjecucion || '''
and fa.codmodulo = mp.codmodulo
and ((cp.idpoliza is null and fa.codriesgocubierto is null) or (cp.idpoliza is not null and (fa.codriesgocubierto is null or (fa.codriesgocubierto = cp.codriesgocubierto and fa.riesgocubiertoelegible = decode(cp.codvalor, -1, ''S'', ''N'')))))
and (fa.codcultivo = pa.codcultivo or fa.codcultivo = 999)
and (fa.codvariedad = pa.codvariedad or fa.codvariedad = 999)
and (fa.codprovincia = pa.codprovincia or fa.codprovincia = 99)
and (fa.codcomarca = pa.codcomarca or fa.codcomarca = 99)
and (fa.codtermino = pa.codtermino or fa.codtermino = 999)
and (fa.subtermino = pa.subtermino or fa.subtermino = ''9'')
and (fa.codtipocapital is null or fa.codtipocapital = ca.codtipocapital or fa.codtipocapital = 999)
and (fa.codsistemaproteccion is null or fa.codsistemaproteccion = 0 or fa.codsistemaproteccion = dv_621.valor)
and (fa.codciclocultivo is null or fa.codciclocultivo = dv_618.valor)
and (fa.codtipoplantacion is null or fa.codtipoplantacion = 0 or fa.codtipoplantacion = dv_173.valor)
and (fa.codsistemacultivo is null or fa.codsistemacultivo = dv_123.valor)
where p.idestado in (1, 2)))
order by codusuario asc';
  
    l_pol_map polizas_usuario_map;
  
  BEGIN
    pq_utl.Log(lc,
               '########################################################################',
               1);
    pq_utl.Log(lc,
               '##                                                                    ##',
               1);
    pq_utl.Log(lc,
               '## INICIO FUNCION enviar_record_polizas PQ_RECORDATORIO_CONFIRMACION  ##',
               1);
    pq_utl.Log(lc,
               '##                                                                    ##',
               1);
    pq_utl.Log(lc,
               '########################################################################',
               1);
    pq_utl.Log(lc,
               '## FECHA RECIBIDA POR PARAMETRO:  ##' || fechaEjecucion,
               1);
  
    l_pol_map := pq_recordatorio_confirmacion.get_polizas(l_query_pol,
                                                          fechaEjecucion); -- recuperamos las polizas afectadas
    
    pq_recordatorio_confirmacion.procesar_usuarios(l_pol_map,
                                                   fechaEjecucion); -- validamos si existe email y enviamos
  
    pq_utl.Log(lc,
               '#####################################################################',
               1);
    pq_utl.Log(lc,
               '##                                                                 ##',
               1);
    pq_utl.Log(lc,
               '## FIN FUNCION enviar_record_polizas PQ_RECORDATORIO_CONFIRMACION  ##',
               1);
    pq_utl.Log(lc,
               '##                                                                 ##',
               1);
    pq_utl.Log(lc,
               '#####################################################################',
               1);
  
    RETURN 'OK';
  
  EXCEPTION
    WHEN others THEN
      PQ_Utl.LOG('ERROR EN enviar_record_polizas ' || SQLERRM ||
                 '*********',
                 1);
      ROLLBACK;
    
  END;

  PROCEDURE procesar_usuarios(pol_map        polizas_usuario_map,
                              fechaEjecucion varchar2) IS
    lc VARCHAR2(200) := 'PQ_RECORDATORIO_CONFIRMACION.procesar_usuarios';
  
    v_codusr VARCHAR2(24);
    v_email  varchar2(100);
    n_elem   number(2);
  
    v_res number(1);
  
  BEGIN
  
    pq_utl.Log(lc,
               '## INICIO PROCEDIMIENTO procesar_usuarios PQ_RECORDATORIO_CONFIRMACION  ##',
               1);
  
    v_codusr := pol_map.first;
    while v_codusr is not null loop
    
      pq_utl.Log(lc, '## POLIZAS DEL USUARIO: ' || v_codusr, 1);
      pq_utl.Log(lc, pol_map(v_codusr), 1);
    
      SELECT count(*)
        into n_elem
        from tb_usuarios
       where codusuario = v_codusr
         and email is not null;
    
      if n_elem <> 0 then
        -- si el usuario tiene direccion de correo asignado
      
        SELECT distinct email
          into v_email
          from tb_usuarios
         where codusuario = v_codusr;
      
        pq_utl.Log(lc,
                   '## EL USUARIO ' || v_codusr || ' tiene email: ' ||
                   v_email || '##',
                   1);
      
        v_res := pq_recordatorio_confirmacion.enviar_correo(pol_map(v_codusr),
                                                            v_email,
                                                            fechaEjecucion);
      
        if v_res > -1 then
          INSERT INTO TB_CORREO_RECORD_CONFIRM
          VALUES
            (O02AGPE0.SQ_TB_CORREO_RECORD_CONFIRM.NEXTVAL,
             TO_DATE(fechaEjecucion, 'YYYYMMDD'),
             v_codusr,
             'CORREO RECORDATORIO CONFIRMACION DE POLIZAS ENVIADO',
             TO_CLOB(pol_map(v_codusr)));
          pq_utl.Log(lc,
                     '## BASE DE DATOS TB_CORREO_RECORD_CONFIRM actualizada ##',
                     1);
        else
          INSERT INTO TB_CORREO_RECORD_CONFIRM
          VALUES
            (O02AGPE0.SQ_TB_CORREO_RECORD_CONFIRM.NEXTVAL,
             TO_DATE(fechaEjecucion, 'YYYYMMDD'),
             v_codusr,
             'ERROR EN ENVIO DE CORREO RECORDATORIO CONFIRMACION DE POLIZAS',
             NULL);
          pq_utl.Log(lc, '## No se ha enviado el email ##', 1);
        end if;
      else
        pq_utl.Log(lc,
                   '## EL USUARIO ' || v_codusr || ' NO tiene email ##',
                   1);
      end if;
    
      v_codusr := pol_map.next(v_codusr);
    end loop;
  
  END;

  FUNCTION enviar_correo(mensaje        varchar2,
                         email          varchar2,
                         fechaEjecucion varchar2) RETURN NUMBER IS
  
    lc VARCHAR2(200) := 'PQ_RECORDATORIO_CONFIRMACION.enviar_correo';
  
    contenido varchar2(32767) := pq_envio_correos.v_retorno ||
                                 pq_envio_correos.v_retorno ||
                                 'Buenos dias,' ||
                                 pq_envio_correos.v_retorno ||
                                 pq_envio_correos.v_retorno ||
                                 'Le informamos que se aproxima el ultimo dia de pago de las siguientes polizas que aun tienes en estado pendiente de validacion o grabacion provisional con el objeto de que puedas revisar las mismas, por si alguna deberia confirmarse' ||
                                 pq_envio_correos.v_salto;
    asunto    varchar2(2000) := 'Polizas pendientes de confirmacion a fecha ' ||
                                TO_CHAR(TO_DATE(fechaEjecucion, 'YYYYMMDD'),
                                        'DD/MM/YYYY');
  
  BEGIN
  
    pq_utl.Log(lc,
               '## INICIO FUNCION enviar_correo PQ_RECORDATORIO_CONFIRMACION  ##',
               1);
  
    contenido := contenido || mensaje || ' ';
  
    pq_utl.Log(lc, contenido, 1);
    pq_envio_correos.enviarCorreoUnico(email, asunto, contenido); --se llama al paquete de envio de correos
    return 1;
  
  EXCEPTION
    WHEN OTHERS THEN
      pq_utl.log(lc,
                 'Se ha producido un error en el envio del correo ' ||
                 to_char(SYSDATE, 'HH24:MI:SS'),
                 2);
      return - 1;
    
  END;

  FUNCTION ult_dia_pago_agr(id_poliza number, cod_modulo varchar2)
    RETURN VARCHAR2 IS
  
    lc VARCHAR2(200) := 'PQ_RECORDATORIO_CONFIRMACION.ult_dia_pago_agr';
  
    fec_agr_map fec_agr_map_type := fec_agr_map_type();
    v_row       NUMBER := 1;
  
    ultimodiapago o02agpe0.tb_sc_c_fec_contrat_agr.ultimodiapago%TYPE := NULL;
    udp_aux       o02agpe0.tb_sc_c_fec_contrat_agr.ultimodiapago%TYPE := NULL;
  
    -- OBTIENE TODAS LAS COMBINACIONES DE REGISTROS
    -- SEGUN LAS CARACTERISTICAS DE LA POLIZA
    fec_agr_record o02agpe0.tb_sc_c_fec_contrat_agr%ROWTYPE;
    CURSOR fec_agr_cursor IS
      select fa.*
        from o02agpe0.tb_sc_c_fec_contrat_agr fa
       inner join o02agpe0.tb_lineas l on fa.lineaseguroid =
                                          l.lineaseguroid
       inner join o02agpe0.tb_polizas p on p.idpoliza = id_poliza
                                       and fa.lineaseguroid =
                                           p.lineaseguroid
       inner join o02agpe0.tb_modulos_poliza mp on mp.codmodulo =
                                                   cod_modulo
                                               and mp.idpoliza = p.idpoliza
        left outer join o02agpe0.tb_comparativas_poliza cp on cp.idpoliza =
                                                              p.idpoliza
                                                          and cp.idmodulo =
                                                              mp.idmodulo
                                                          and cp.codconcepto = 363
       inner join o02agpe0.tb_parcelas pa on p.idpoliza = pa.idpoliza
       inner join o02agpe0.tb_capitales_asegurados ca on ca.idparcela =
                                                         pa.idparcela
        left outer join o02agpe0.tb_datos_var_parcela dv_621 on dv_621.idcapitalasegurado =
                                                                ca.idcapitalasegurado
                                                            and dv_621.codconcepto = 621
        left outer join o02agpe0.tb_datos_var_parcela dv_618 on dv_618.idcapitalasegurado =
                                                                ca.idcapitalasegurado
                                                            and dv_618.codconcepto = 618
        left outer join o02agpe0.tb_datos_var_parcela dv_173 on dv_173.idcapitalasegurado =
                                                                ca.idcapitalasegurado
                                                            and dv_173.codconcepto = 173
        left outer join o02agpe0.tb_datos_var_parcela dv_123 on dv_123.idcapitalasegurado =
                                                                ca.idcapitalasegurado
                                                            and dv_123.codconcepto = 123
       where fa.codmodulo = cod_modulo
         and (fa.codsistemaproteccion is null or
			 fa.codsistemaproteccion = 0 or
             fa.codsistemaproteccion = dv_621.valor)
         and (fa.codciclocultivo is null or
             fa.codciclocultivo = dv_618.valor)
         and (fa.codtipoplantacion is null or
			 fa.codtipoplantacion = 0 or
             fa.codtipoplantacion = dv_173.valor)
         and (fa.codsistemacultivo is null or
             fa.codsistemacultivo = dv_123.valor)
         and (fa.codcultivo = pa.codcultivo or fa.codcultivo = 999)
         and (fa.codvariedad = pa.codvariedad or fa.codvariedad = 999)
         and (fa.codprovincia = pa.codprovincia or fa.codprovincia = 99)
         and (fa.codcomarca = pa.codcomarca or fa.codcomarca = 99)
         and (fa.codtermino = pa.codtermino or fa.codtermino = 999)
         and (fa.subtermino = pa.subtermino or fa.subtermino = '9')
         and (fa.codtipocapital is null or
             fa.codtipocapital = ca.codtipocapital or
             fa.codtipocapital = 999);
  
  BEGIN
  
    pq_utl.log(lc,
               'Poblamos todas las combinatorias de fechas segun las caracteristicas de la poliza para una fecha final de contracion especifica.',
               2);
  
    OPEN fec_agr_cursor;
    LOOP
      FETCH fec_agr_cursor
        INTO fec_agr_record;
      EXIT WHEN fec_agr_cursor%NOTFOUND;
      fec_agr_map.extend;
      fec_agr_map(v_row) := fec_agr_record;
      v_row := v_row + 1;
    END LOOP;
    CLOSE fec_agr_cursor;
  
    FOR parc_record IN (SELECT *
                          from o02agpe0.tb_parcelas
                         WHERE idpoliza = id_poliza) LOOP
      udp_aux := localiza_ult_dia_pago_agr(fec_agr_map,
                                           parc_record.codcultivo,
                                           parc_record.codvariedad,
                                           parc_record.codprovincia,
                                           parc_record.codcomarca,
                                           parc_record.codtermino,
                                           parc_record.subtermino);
    
      IF ultimodiapago IS NULL OR udp_aux < ultimodiapago THEN
        ultimodiapago := udp_aux;
      END IF;
    END LOOP;
  
    return TO_CHAR(ultimodiapago, 'DD/MM/YYYY');
  EXCEPTION
    WHEN OTHERS THEN
      pq_utl.log(lc,
                 'Se ha producido un error [' || sqlcode ||
                 '] en la obtencion de la fecha de pago de agricola: ' ||
                 sqlerrm,
                 2);
      return 'N/A';
  END;

  FUNCTION localiza_ult_dia_pago_agr(fec_agr_map    fec_agr_map_type,
                                     cod_cultivo    number,
                                     cod_variedad   number,
                                     cod_provincia  number,
                                     cod_comarca    number,
                                     cod_termino    number,
                                     cod_subtermino varchar2) RETURN DATE IS
  
    lc     VARCHAR2(200) := 'PQ_RECORDATORIO_CONFIRMACION.localiza_ult_dia_pago_agr';
    result o02agpe0.tb_sc_c_fec_contrat_agr.ultimodiapago%TYPE := NULL;
  BEGIN
  
    FOR i IN fec_agr_map.FIRST .. fec_agr_map.LAST LOOP
      IF (cod_cultivo = 999 OR cod_cultivo = fec_agr_map(i).codcultivo) AND
         (cod_variedad = 999 OR cod_variedad = fec_agr_map(i).codvariedad) AND
         (cod_provincia = 99 OR cod_provincia = fec_agr_map(i).codprovincia) AND
         (cod_comarca = 99 OR cod_comarca = fec_agr_map(i).codcomarca) AND
         (cod_termino = 999 OR cod_termino = fec_agr_map(i).codtermino) AND
         (cod_subtermino = '9' OR cod_subtermino = fec_agr_map(i).subtermino) THEN
      
        result := fec_agr_map(i).ultimodiapago;
        EXIT;
      END IF;
    
    END LOOP;
  
    IF result IS NULL THEN
    
      IF cod_cultivo <> 999 THEN
      
        result := localiza_ult_dia_pago_agr(fec_agr_map,
                                            999,
                                            cod_variedad,
                                            cod_provincia,
                                            cod_comarca,
                                            cod_termino,
                                            cod_subtermino);
      
      ELSIF cod_cultivo = 999 AND cod_variedad <> 999 THEN
      
        result := localiza_ult_dia_pago_agr(fec_agr_map,
                                            999,
                                            999,
                                            cod_provincia,
                                            cod_comarca,
                                            cod_termino,
                                            cod_subtermino);
      
      ELSIF cod_cultivo = 999 AND cod_variedad = 999 AND
            cod_provincia <> 99 THEN
      
        result := localiza_ult_dia_pago_agr(fec_agr_map,
                                            999,
                                            999,
                                            99,
                                            cod_comarca,
                                            cod_termino,
                                            cod_subtermino);
      
      ELSIF cod_cultivo = 999 AND cod_variedad = 999 AND cod_provincia = 99 AND
            cod_comarca <> 99 THEN
      
        result := localiza_ult_dia_pago_agr(fec_agr_map,
                                            999,
                                            999,
                                            99,
                                            99,
                                            cod_termino,
                                            cod_subtermino);
      
      ELSIF cod_cultivo = 999 AND cod_variedad = 999 AND cod_provincia = 99 AND
            cod_comarca = 99 AND cod_termino <> 999 THEN
      
        result := localiza_ult_dia_pago_agr(fec_agr_map,
                                            999,
                                            999,
                                            99,
                                            99,
                                            999,
                                            cod_subtermino);
      
      ELSIF cod_cultivo = 999 AND cod_variedad = 999 AND cod_provincia = 99 AND
            cod_comarca = 99 AND cod_termino = 999 AND
            cod_subtermino <> '9' THEN
      
        result := localiza_ult_dia_pago_agr(fec_agr_map,
                                            999,
                                            999,
                                            99,
                                            99,
                                            999,
                                            '9');
      END IF;
    END IF;
  
    return result;
  EXCEPTION
    WHEN OTHERS THEN
      pq_utl.log(lc,
                 'Se ha producido un error [' || sqlcode ||
                 '] en la obtencion de la fecha de pago de agricola: ' ||
                 sqlerrm,
                 2);
      return 'N/A';
  END;

  FUNCTION ult_dia_pago_gan(id_poliza number, cod_modulo varchar2)
    RETURN VARCHAR2 IS
  
    lc VARCHAR2(200) := 'PQ_RECORDATORIO_CONFIRMACION.ult_dia_pago_gan';
  
    ultimodiapago o02agpe0.tb_sc_c_fec_contrat_g.fec_ultimo_dia_pago%TYPE := NULL;
    udp_aux       o02agpe0.tb_sc_c_fec_contrat_g.fec_ultimo_dia_pago%TYPE := NULL;
  
    fec_gan_map fec_gan_map_type := fec_gan_map_type();
    v_row       NUMBER := 1;
  
    -- OBTIENE TODAS LAS COMBINACIONES DE REGISTROS
    -- SEGUN LAS CARACTERISTICAS DE LA POLIZA
    fec_gan_record o02agpe0.tb_sc_c_fec_contrat_g%ROWTYPE;
    CURSOR fec_gan_cursor IS
      select fg.*
        from o02agpe0.tb_sc_c_fec_contrat_g fg
       inner join o02agpe0.tb_lineas l on fg.lineaseguroid =
                                          l.lineaseguroid
       inner join o02agpe0.tb_polizas p on p.idpoliza = id_poliza
                                       and fg.lineaseguroid =
                                           p.lineaseguroid
       inner join o02agpe0.tb_modulos_poliza mp on mp.codmodulo =
                                                   cod_modulo
                                               and mp.idpoliza = p.idpoliza
        left outer join o02agpe0.tb_comparativas_poliza cp on cp.idpoliza =
                                                              p.idpoliza
                                                          and cp.idmodulo =
                                                              mp.idmodulo
                                                          and cp.codconcepto = 363
       inner join o02agpe0.tb_explotaciones ex on p.idpoliza = ex.idpoliza
       inner join o02agpe0.tb_grupo_raza_explotacion gr on gr.idexplotacion =
                                                           ex.id
       where fg.codmodulo = mp.codmodulo
         and (fg.cod_riesgo_cbto_ele_cpm is null or
             fg.cod_riesgo_cbto_ele_cpm = mp.codriesgocubierto)
         and (fg.codespecie = ex.especie or fg.codespecie = 999)
         and (fg.codregimen = ex.regimen or fg.codregimen = 999)
         and (fg.codgruporaza = gr.codgruporaza or fg.codgruporaza = 999)
         and (fg.codtipoanimal = gr.codtipoanimal or fg.codtipoanimal = 999)
         and (fg.codtipocapital = gr.codtipocapital or
             fg.codtipocapital = 999)
         and (fg.codprovincia = ex.codprovincia or fg.codprovincia = 99)
         and (fg.codcomarca = ex.codcomarca or fg.codcomarca = 99)
         and (fg.codtermino = ex.codtermino or fg.codtermino = 999)
         and (fg.subtermino = ex.subtermino or fg.subtermino = '9');
  
  BEGIN
  
    pq_utl.log(lc,
               'Poblamos todas las combinatorias de fechas segun las caracteristicas de la poliza para una fecha final de contracion especifica.',
               2);
  
    OPEN fec_gan_cursor;
    LOOP
      FETCH fec_gan_cursor
        INTO fec_gan_record;
      EXIT WHEN fec_gan_cursor%NOTFOUND;
      fec_gan_map.extend;
      fec_gan_map(v_row) := fec_gan_record;
      v_row := v_row + 1;
    END LOOP;
    CLOSE fec_gan_cursor;
  
    FOR expl_record IN (SELECT t2.especie,
                               t2.regimen,
                               t1.codgruporaza,
                               t1.codtipoanimal,
                               t2.codprovincia,
                               t2.codcomarca,
                               t2.codtermino,
                               t2.subtermino
                          from o02agpe0.tb_grupo_raza_explotacion t1
                         inner join o02agpe0.tb_explotaciones t2 on t1.idexplotacion =
                                                                    t2.id
                         WHERE t2.idpoliza = id_poliza) LOOP
      udp_aux := localiza_ult_dia_pago_gan(fec_gan_map,
                                           expl_record.especie,
                                           expl_record.regimen,
                                           expl_record.codgruporaza,
                                           expl_record.codtipoanimal,
                                           expl_record.codprovincia,
                                           expl_record.codcomarca,
                                           expl_record.codtermino,
                                           expl_record.subtermino);
    
      IF ultimodiapago IS NULL OR udp_aux < ultimodiapago THEN
        ultimodiapago := udp_aux;
      END IF;
    END LOOP;
  
    return TO_CHAR(ultimodiapago, 'DD/MM/YYYY');
  EXCEPTION
    WHEN OTHERS THEN
      pq_utl.log(lc,
                 'Se ha producido un error [' || sqlcode ||
                 '] en la obtencion de la fecha de pago de ganado: ' ||
                 sqlerrm,
                 2);
      return 'N/A';
  END;

  FUNCTION localiza_ult_dia_pago_gan(fec_gan_map     fec_gan_map_type,
                                     cod_especie     number,
                                     cod_regimen     number,
                                     cod_grupo_raza  number,
                                     cod_tipo_animal number,
                                     cod_provincia   number,
                                     cod_comarca     number,
                                     cod_termino     number,
                                     cod_subtermino  varchar2) RETURN DATE IS
  
    lc     VARCHAR2(200) := 'PQ_RECORDATORIO_CONFIRMACION.localiza_ult_dia_pago_gan';
    result o02agpe0.tb_sc_c_fec_contrat_g.fec_ultimo_dia_pago%TYPE := NULL;
  BEGIN
  
    FOR i IN fec_gan_map.FIRST .. fec_gan_map.LAST LOOP
      IF (cod_especie = 999 OR cod_especie = fec_gan_map(i).codespecie) AND
         (cod_regimen = 999 OR cod_regimen = fec_gan_map(i).codregimen) AND
         (cod_grupo_raza = 999 OR cod_grupo_raza = fec_gan_map(i)
         .codgruporaza) AND (cod_tipo_animal = 999 OR
         cod_tipo_animal = fec_gan_map(i).codtipoanimal) AND
         (cod_provincia = 99 OR cod_provincia = fec_gan_map(i).codprovincia) AND
         (cod_comarca = 99 OR cod_comarca = fec_gan_map(i).codcomarca) AND
         (cod_termino = 999 OR cod_termino = fec_gan_map(i).codtermino) AND
         (cod_subtermino = '9' OR cod_subtermino = fec_gan_map(i).subtermino) THEN
      
        result := fec_gan_map(i).fec_ultimo_dia_pago;
        EXIT;
      END IF;
    
    END LOOP;
  
    IF result IS NULL THEN
    
      IF cod_especie <> 999 THEN
      
        result := localiza_ult_dia_pago_gan(fec_gan_map,
                                            999,
                                            cod_regimen,
                                            cod_grupo_raza,
                                            cod_tipo_animal,
                                            cod_provincia,
                                            cod_comarca,
                                            cod_termino,
                                            cod_subtermino);
      
      ELSIF cod_especie = 999 AND cod_regimen <> 999 THEN
      
        result := localiza_ult_dia_pago_gan(fec_gan_map,
                                            999,
                                            999,
                                            cod_grupo_raza,
                                            cod_tipo_animal,
                                            cod_provincia,
                                            cod_comarca,
                                            cod_termino,
                                            cod_subtermino);
      
      ELSIF cod_especie = 999 AND cod_regimen = 999 AND
            cod_grupo_raza <> 999 THEN
      
        result := localiza_ult_dia_pago_gan(fec_gan_map,
                                            999,
                                            999,
                                            999,
                                            cod_tipo_animal,
                                            cod_provincia,
                                            cod_comarca,
                                            cod_termino,
                                            cod_subtermino);
      
      ELSIF cod_especie = 999 AND cod_regimen = 999 AND
            cod_grupo_raza = 999 AND cod_tipo_animal <> 999 THEN
      
        result := localiza_ult_dia_pago_gan(fec_gan_map,
                                            999,
                                            999,
                                            999,
                                            999,
                                            cod_provincia,
                                            cod_comarca,
                                            cod_termino,
                                            cod_subtermino);
      
      ELSIF cod_especie = 999 AND cod_regimen = 999 AND
            cod_grupo_raza = 999 AND cod_tipo_animal = 999 AND
            cod_provincia <> 99 THEN
      
        result := localiza_ult_dia_pago_gan(fec_gan_map,
                                            999,
                                            999,
                                            999,
                                            999,
                                            99,
                                            cod_comarca,
                                            cod_termino,
                                            cod_subtermino);
      
      ELSIF cod_especie = 999 AND cod_regimen = 999 AND
            cod_grupo_raza = 999 AND cod_tipo_animal = 999 AND
            cod_provincia = 99 AND cod_comarca <> 99 THEN
      
        result := localiza_ult_dia_pago_gan(fec_gan_map,
                                            999,
                                            999,
                                            999,
                                            999,
                                            99,
                                            99,
                                            cod_termino,
                                            cod_subtermino);
      
      ELSIF cod_especie = 999 AND cod_regimen = 999 AND
            cod_grupo_raza = 999 AND cod_tipo_animal = 999 AND
            cod_provincia = 99 AND cod_comarca = 99 AND cod_termino <> 999 THEN
      
        result := localiza_ult_dia_pago_gan(fec_gan_map,
                                            999,
                                            999,
                                            999,
                                            999,
                                            99,
                                            99,
                                            999,
                                            cod_subtermino);
      
      ELSIF cod_especie = 999 AND cod_regimen = 999 AND
            cod_grupo_raza = 999 AND cod_tipo_animal = 999 AND
            cod_provincia = 99 AND cod_comarca = 99 AND cod_termino = 999 AND
            cod_subtermino <> '9' THEN
      
        result := localiza_ult_dia_pago_gan(fec_gan_map,
                                            999,
                                            999,
                                            999,
                                            999,
                                            99,
                                            99,
                                            999,
                                            '9');
      END IF;
    END IF;
  
    return result;
  EXCEPTION
    WHEN OTHERS THEN
      pq_utl.log(lc,
                 'Se ha producido un error [' || sqlcode ||
                 '] en la obtencion de la fecha de pago de ganado: ' ||
                 sqlerrm,
                 2);
      return 'N/A';
  END;
END pq_recordatorio_confirmacion;
/
SHOW ERRORS;