create or replace package o02agpe0.PQ_SOCIOS is

  -- Asigna el orden de los socios correspondientes al idpoliza indicado
  -- Si no se indica el idpoliza se actualiza todos los socios de la tabla
  procedure actualizarOrden (idPolizaParam IN NUMBER);

end PQ_SOCIOS;
/
create or replace package body o02agpe0.PQ_SOCIOS is

   -- Asigna el orden de los socios correspondientes al idpoliza indicado
  -- Si no se indica el idpoliza se actualiza todos los socios de la tabla
  procedure actualizarOrden (idPolizaParam IN NUMBER) is

    TYPE tpcursor IS REF CURSOR;
    l_tp_cursor       tpcursor;
    orden o02agpe0.tb_poliza_socios.orden%TYPE;
    idsocio o02agpe0.tb_poliza_socios.id%TYPE;
    idpoliza o02agpe0.tb_poliza_socios.idpoliza%TYPE;
    idpoliza_ant o02agpe0.tb_poliza_socios.idpoliza%TYPE;
    query VARCHAR2(1000);

    begin

    query := 'select ps.id, ps.idpoliza from o02agpe0.tb_poliza_socios ps, o02agpe0.tb_socios so
    where so.idasegurado = ps.idasegurado  and so.nif = ps.nif_socio
    and (so.baja is null or so.baja = ''N'')';

    -- Si se ha indicado un idpoliza como parámetro, se añade a la consulta
    IF (idPolizaParam IS NOT NULL) THEN
       query := query || ' and ps.idpoliza = ' || idPolizaParam;
    -- Si no se ha indicado el parámetro, se ordena la consulta por idpoliza
    ELSE
       query := query || ' order by ps.idpoliza';
    END IF;



    OPEN l_tp_cursor FOR query;

    LOOP

    FETCH l_tp_cursor	INTO idsocio, idpoliza;

      -- Sale del bucle si se han procesado todos los registros
      IF (l_tp_cursor%NOTFOUND) THEN
      		EXIT;
      END IF;

      -- Si el idpoliza actual es diferente al anterior se reinicia el orden
      IF (idpoliza_ant is null or idpoliza <> idpoliza_ant) THEN
         orden := 1;
      -- Si no, se incrementa
      ELSE
         orden := orden + 1;
      END IF;

      -- Actualiza el orden del registro
      DBMS_OUTPUT.put_line ('UPDATE o02agpe0.tb_poliza_socios ps set ps.orden=' || orden || ' where ps.id=' || idsocio);
      EXECUTE IMMEDIATE ('UPDATE o02agpe0.tb_poliza_socios ps set ps.orden=' || orden || ' where ps.id=' || idsocio);

      -- Actualiza el id de póliza tratada
      idpoliza_ant := idpoliza;

    END LOOP;

    commit;

    CLOSE l_tp_cursor;

    end actualizarOrden;

end PQ_SOCIOS;
/
