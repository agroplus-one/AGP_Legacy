SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_BORRAR_PARCELAS is

  -- Author  :  U028783
  -- Created : 5/12/2011 13:17:06
  --- Purpose : Paquete para realizar el borrado de parcelas

  --Procedimiento para borrar los datos de una poliza
  PROCEDURE PR_BORRAR_POLIZA(P_IDPOLIZA IN VARCHAR2, p_result out varchar2);
  
  ---Procedimiento para borrar los datos de todas las parcelas de una poliza
  PROCEDURE PR_BORRAR_PARCELAS_POLIZA(P_IDPOLIZA IN VARCHAR2, p_result out varchar2);

  --Procedimiento para borrar los datos de una parcela
  PROCEDURE PR_BORRAR_PARCELA(P_IDPARCELA IN VARCHAR2, p_result out varchar2);
  
  --Procedimiento para borrar una lista de parcelas. Recibirá los ids de parcela separados por ';'
  PROCEDURE PR_BORRADO_MASIVO(P_IDSPARCELA IN VARCHAR2, p_result out varchar2);

  function fn_borrado(p_idparcela varchar2) return varchar2;

  --Procedimiento para borrar los anexos de modificacion de una poliza
  PROCEDURE PR_BORRAR_ANEXOS_MODIFICACION(P_IDPOLIZA IN VARCHAR2, p_result out varchar2);
  PROCEDURE PR_BORRAR_PARCELA_ANEXO_MOD(P_IDPARCELA IN VARCHAR2, p_result out varchar2);
  FUNCTION FN_BORRADO_ANEXO_MOD(p_idparcela varchar2) return varchar2;

  ---Procedimiento para borrar los anexos de reduccion de una poliza
  PROCEDURE PR_BORRAR_ANEXOS_REDUCCION(P_IDPOLIZA IN VARCHAR2, p_result out varchar2);
  FUNCTION FN_BORRADO_ANEXO_RED(p_idparcela varchar2) return varchar2;

  --Procedimiento para borrar los siniestros de una poliza
  PROCEDURE PR_BORRAR_SINIESTROS(P_IDPOLIZA IN VARCHAR2, p_result out varchar2);
  FUNCTION FN_BORRADO_SINIESTRO(p_idparcela varchar2) return varchar2;
  
  --Procedimiento para borrar los asegurados repetidos
  PROCEDURE PR_BORRAR_ASEGURADOS_REPETIDOS(P_CODENTIDAD IN VARCHAR2, P_CODUSUARIO IN VARCHAR2);

end PQ_BORRAR_PARCELAS;
/
create or replace package body o02agpe0.PQ_BORRAR_PARCELAS is

  --Procedimiento para borrar los datos de una póliza
  PROCEDURE PR_BORRAR_POLIZA(P_IDPOLIZA IN VARCHAR2, p_result out varchar2) is
      v_result varchar2(10) := null;
  BEGIN
      --BORRAMOS LOS MODULOS
      begin
        delete TB_MODULOS_POLIZA
         where IDPOLIZA = P_IDPOLIZA;
         commit;
      exception
        when others then
          pq_utl.log('En TB_MODULOS_POLIZA ' || P_IDPOLIZA ||
                     '*********');
      end;
      --BORRAMOS LAS COBERTURAS
      begin
        delete TB_COBERTURAS_POLIZA
         where IDPOLIZA = P_IDPOLIZA;
         commit;
      exception
        when others then
          pq_utl.log('En TB_COBERTURAS_POLIZA ' || P_IDPOLIZA ||
                     '*********');
      end;
      --BORRAMOS LAS COMPARATIVAS
      begin
        delete TB_COMPARATIVAS_POLIZA
         where IDPOLIZA = P_IDPOLIZA;
         commit;
      exception
        when others then
          pq_utl.log('En TB_COMPARATIVAS_POLIZA ' || P_IDPOLIZA ||
                     '*********');
      end;
      --BORRAMOS LAS SUBVENCIONES CCAA DEL ASEGURADO
      begin
        delete TB_SUBVS_ASEG_CCAA
         where IDPOLIZA = P_IDPOLIZA;
         commit;
      exception
        when others then
          pq_utl.log('En TB_SUBVS_ASEG_CCAA ' || P_IDPOLIZA ||
                     '*********');
      end;
      --BORRAMOS LAS SUBVENCIONES ENESA DEL ASEGURADO
      begin
        delete TB_SUBVS_ASEG_ENESA
         where IDPOLIZA = P_IDPOLIZA;
         commit;
      exception
        when others then
          pq_utl.log('En TB_SUBVS_ASEG_ENESA ' || P_IDPOLIZA ||
                     '*********');
      end;
      --BORRAMOS LAS SUBVENCIONES DE SOCIOS
      begin
        delete TB_SUBV_ENESA_SOCIOS
         where IDPOLIZA = P_IDPOLIZA;
         commit;
      exception
        when others then
          pq_utl.log('En TB_SUBV_ENESA_SOCIOS ' || P_IDPOLIZA ||
                     '*********');
      end;
      --BORRAMOS LA DISTRIBUCION DE COSTES
      begin
        delete TB_DISTRIBUCION_COSTES
         where IDPOLIZA = P_IDPOLIZA;
         commit;
      exception
        when others then
          pq_utl.log('En TB_DISTRIBUCION_COSTES ' || P_IDPOLIZA ||
                     '*********');
      end;
      --BORRAMOS LOS PAGOS
      begin
        delete TB_PAGOS_POLIZA
         where IDPOLIZA = P_IDPOLIZA;
         commit;
      exception
        when others then
          pq_utl.log('En TB_PAGOS_POLIZA ' || P_IDPOLIZA ||
                     '*********');
      end;
      --BORRAMOS LOS ENVIOS A AGROSEGURO
      begin
        delete TB_ENVIOS_AGROSEGURO
         where IDPOLIZA = P_IDPOLIZA;
         commit;
      exception
        when others then
          pq_utl.log('En TB_ENVIOS_AGROSEGURO ' || P_IDPOLIZA ||
                     '*********');
      end;
      --BORRAMOS LAS PARCELAS
      PR_BORRAR_PARCELAS_POLIZA(P_IDPOLIZA, v_result);
      --BORRAMOS LOS ANEXOS DE MODIFICACION
      PR_BORRAR_ANEXOS_MODIFICACION(P_IDPOLIZA, v_result);
      --BORRAMOS LOS ANEXOS DE REDUCCIÓN DE CAPITAL
      --PR_BORRAR_ANEXOS_REDUCCION(P_IDPOLIZA, v_result);
      --BORRAMOS LOS SINIESTROS
      --PR_BORRAR_SINIESTROS(P_IDPOLIZA, v_result);
      
      --BORRAMOS LA PÓLIZA
      begin
        delete TB_POLIZAS
         where IDPOLIZA = P_IDPOLIZA;
         commit;
      exception
        when others then
          pq_utl.log('En TB_POLIZAS ' || P_IDPOLIZA ||
                     '*********');
      end;
      
      p_result := v_result;

  EXCEPTION
      when others then
          pq_utl.log('ERRO EN PR_BORRAR_POLIZA ' || SQLERRM || '*********');
          rollback;
  END;
  
  --Procedimiento para borrar todas las parcelas de una póliza
  
  PROCEDURE PR_BORRAR_PARCELAS_POLIZA(P_IDPOLIZA IN VARCHAR2, p_result out varchar2) is
  
    cursor c_parcelas is
      select idparcela
        from tb_parcelas p
       where p.idpoliza = P_IDPOLIZA;
    v_result varchar2(10) := null;
  BEGIN
    for indice in c_parcelas loop
      PQ_BORRAR_PARCELAS.PR_BORRAR_PARCELA(indice.idparcela, v_result);
      if v_result = 'N' then
        exit;
      end if;
    end loop;
    
    p_result := v_result;
  
  EXCEPTION
    when others then
      p_result := 'N';
      pq_utl.log('ERRO EN PR_BORRAR_PARCELAS_POLIZA ' || SQLERRM || '*********');
      rollback;
  
  END;

  --Procedimiento para borrar los datos de una parcela

  PROCEDURE PR_BORRAR_PARCELA(P_IDPARCELA IN VARCHAR2,
                              p_result    out varchar2) IS
  
    cursor c_estructuras is
      select idparcela
        from tb_parcelas p
       where p.idparcelaestructura = P_idparcela;
    v_result varchar2(10) := null;
  BEGIN
  
    v_result := 'S';
    for indice in c_estructuras loop
      v_result := PQ_BORRAR_PARCELAS.fn_borrado(indice.idparcela);
      if v_result = 'N' then
        exit;
      end if;
    end loop;
  
    if v_result = 'S' then
      v_result := PQ_BORRAR_PARCELAS.fn_borrado(P_idparcela);
    end if;
  
    p_result := v_result;
  
  EXCEPTION
    when others then
      pq_utl.log('ERROR EN PR_BORRAR_PARCELA ' || SQLERRM || '*********');
      rollback;
    
  END;

  --Procedimiento para borrar una lista de parcelas. Recibirá los ids de parcela separados por ';'
  PROCEDURE PR_BORRADO_MASIVO(P_IDSPARCELA IN VARCHAR2,
                              p_result     out varchar2) is
    v_ind     number := 1;
    v_parcela varchar2(32000) := null;
    v_result  varchar2(10) := null;
--    v_name varchar2(32000) := null;
--    v_name1234 varchar2(32000) := null;    
--    v_file utl_file.file_type;
  begin
--    v_name := dbms_random.random;
--    v_file := utl_file.fopen(location => 'AGP_INTERFACES_LOGS',filename => v_name,open_mode => 'w');
--    utl_file.put_line(v_file, P_IDSPARCELA);    
    WHILE pq_calcula_precio.fn_extraer_campo(P_IDSPARCELA, v_ind, ';') IS NOT NULL LOOP
    
      v_parcela := pq_calcula_precio.fn_extraer_campo(P_IDSPARCELA,
                                                      v_ind,
                                                      ';');
--      utl_file.put_line(v_file, 'inicio '||v_parcela);                                                          
      pq_utl.log('Borro la parcela ' || v_parcela || '*********');
      PQ_BORRAR_PARCELAS.PR_BORRAR_PARCELA(P_IDPARCELA => v_parcela,
                                           p_result    => v_result);
--      utl_file.put_line(v_file, 'fin '||v_parcela);                                                          
      if v_result = 'N' then
        exit;
      end if;
      v_ind := v_ind + 1;
    END LOOP;
    p_result := v_result;
--    utl_file.fclose(v_file);
  
  exception
    when others then
      pq_utl.log('ERRO EN PR_BORRADO_MASIVO ' || SQLERRM || '*********');
      rollback;
  end;

  function fn_borrado(p_idparcela varchar2) return varchar2 is
    cursor c_capitales_asegurados is
      select * from tb_capitales_asegurados ca where ca.idparcela = p_idparcela;
  
  begin
    pq_utl.log('En fn_borrado inicio el borrado ' || p_idparcela || '*********');
    for ind in c_capitales_asegurados loop
      begin
        delete tb_datos_var_parcela where IDCAPITALASEGURADO = ind.idcapitalasegurado;
        --commit;
      exception
        when others then
          pq_utl.log('ERROR BORRADO tb_datos_var_parcela ' || p_idparcela || ' ' || SQLCODE || '-' || SQLERRM || '*********');
      end;
    
      begin
        delete tb_cap_aseg_rel_modulo where IDCAPITALASEGURADO = ind.idcapitalasegurado;
        --commit;         
      exception
        when others then
          pq_utl.log('ERROR BORRADO tb_cap_aseg_rel_modulo ' || p_idparcela || ' ' || SQLCODE || '-' || SQLERRM || '*********');
      end;
    end loop;

    begin
      delete tb_subv_parcela_enesa where idparcela = p_idparcela;
      --commit;      
    exception
      when others then
        pq_utl.log('ERROR BORRADO tb_subv_parcela_enesa ' || p_idparcela || ' ' || SQLCODE || '-' || SQLERRM || '*********');
    end;

    begin
      delete tb_subv_parcela_ccaa where idparcela = p_idparcela;
      --commit;
    exception
      when others then
        pq_utl.log('ERROR BORRADO tb_subv_parcela_ccaa ' || p_idparcela || ' ' || SQLCODE || '-' || SQLERRM || '*********');
    end;

    begin
      delete tb_parcelas_coberturas where idparcela = p_idparcela;
      --commit;      
    exception
      when others then
        pq_utl.log('ERROR BORRADO tb_parcelas_coberturas ' || p_idparcela || ' ' || SQLCODE || '-' || SQLERRM || '*********');
    end;

    begin
      delete tb_capitales_asegurados where idparcela = p_idparcela;
      --commit;      
    exception
      when others then
        pq_utl.log('ERROR BORRADO tb_capitales_asegurados ' || p_idparcela || ' ' || SQLCODE || '-' || SQLERRM || '*********');
    end;

    begin
      delete tb_parcelas where idparcela = p_idparcela;
    exception
      when others then
        pq_utl.log('ERROR BORRADO tb_parcelas ' || p_idparcela || ' ' || SQLCODE || '-' || SQLERRM || '*********');
    end;
    
    commit;
    pq_utl.log('En fn_borrado FIN el borrado ' || p_idparcela || '*********');
    return 'S';
  exception
    when others then
      pq_utl.log('ERROR EN fn_borrado: ' || SQLERRM || '*********');
      rollback;
      return 'N';
  end fn_borrado;
  
  PROCEDURE PR_BORRAR_ANEXOS_MODIFICACION(P_IDPOLIZA IN VARCHAR2, p_result out varchar2) is

    cursor c_anexos is
      select ID
        from tb_anexo_mod m
       where m.idpoliza = P_IDPOLIZA;
    v_result varchar2(10) := null;
  BEGIN
  
    for indice in c_anexos loop
      --BORRAMOS LAS COBERTURAS
      begin
        delete TB_ANEXO_MOD_COBERTURAS
         where IDANEXO = indice.ID;
         commit;
      exception
        when others then
          pq_utl.log('En TB_ANEXO_MOD_COBERTURAS ' || P_IDPOLIZA ||
                     '*********');
      end;
      --BORRAMOS LAS SUBVENCIONES
      begin
        delete TB_ANEXO_MOD_SUBV_DECL
         where IDANEXO = indice.ID;
         commit;
      exception
        when others then
          pq_utl.log('En TB_ANEXO_MOD_SUBV_DECL ' || P_IDPOLIZA ||
                     '*********');
      end;
      --BORRAMOS LAS PARCELAS
      begin
        PR_BORRAR_PARCELA_ANEXO_MOD(indice.ID, v_result);
      exception
        when others then
          pq_utl.log('En PR_BORRAR_PARCELA_ANEXO_MOD ' || P_IDPOLIZA ||
                     '*********');
      end;
      --BORRAMOS LOS ANEXOS
      begin
        delete TB_ANEXO_MOD
         where ID = indice.ID;
         commit;
      exception
        when others then
          pq_utl.log('En TB_ANEXO_MOD ' || P_IDPOLIZA ||
                     '*********');
      end;
      
    end loop;
    
    p_result := v_result;
  
  EXCEPTION
    when others then
      pq_utl.log('ERROR EN PR_BORRAR_ANEXOS_MODIFICACION ' || SQLERRM || '*********');
      rollback;
      p_result := 'N';
  
  END PR_BORRAR_ANEXOS_MODIFICACION;

  --Procedimiento para borrar los datos de una parcela de un anexo de modificación:
  --Primero se borran las instalaciones y luego las parcelas
  PROCEDURE PR_BORRAR_PARCELA_ANEXO_MOD(P_IDPARCELA IN VARCHAR2,
                              p_result    out varchar2) IS
    --cursor para borrar las instalaciones de una parcela
    cursor c_estructuras is
      select idparcela
        from tb_anexo_mod_parcelas p
       where p.idparcelaanxestructura = P_idparcela;
    v_result varchar2(10) := null;
  BEGIN
    --Primero borramos todas las instalaciones de la parcela
    v_result := 'S';
    for indice in c_estructuras loop
      v_result := PQ_BORRAR_PARCELAS.FN_BORRADO_ANEXO_MOD(indice.idparcela);
      if v_result = 'N' then
        exit;
      end if;
    end loop;
  
    if v_result = 'S' then
      v_result := PQ_BORRAR_PARCELAS.FN_BORRADO_ANEXO_MOD(P_idparcela);
    end if;
  
    p_result := v_result;
  
  EXCEPTION
    when others then
      pq_utl.log('ERRO EN PR_BORRAR_PARCELA ' || SQLERRM || '*********');
      rollback;
    
  END PR_BORRAR_PARCELA_ANEXO_MOD;

  --Función para realizar el borrado de una parcela de anexo de modificacion
  FUNCTION FN_BORRADO_ANEXO_MOD(p_idparcela varchar2) return varchar2 is
    cursor c_capitales_asegurados is
      select *
        from TB_ANEXO_MOD_CAPITALES_ASEG ca
       where ca.idparcelaanexo = p_idparcela;
  
  begin
    pq_utl.log('En FN_BORRADO_ANEXO_MOD inicio el borrado ' || p_idparcela ||
               '*********');
    for ind in c_capitales_asegurados loop
      begin
        delete TB_ANEXO_MOD_CAPITALES_DTS_VBL
         where IDCAPITALASEGURADO = ind.id;
         commit;
      exception
        when others then
          pq_utl.log('En TB_ANEXO_MOD_CAPITALES_DTS_VBL ' || p_idparcela ||
                     '*********');
      end;
    end loop;
    begin
      delete TB_ANEXO_MOD_CAPITALES_ASEG where idparcelaanexo = p_idparcela;
         commit;      
    exception
      when others then
        pq_utl.log('En TB_ANEXO_MOD_CAPITALES_ASEG ' || p_idparcela ||
                   '*********');
    end;
    begin
      delete TB_ANEXO_MOD_PARCELAS p where p.Id = p_idparcela;
    exception
      when others then
        pq_utl.log('En TB_ANEXO_MOD_PARCELAS ' || p_idparcela || '*********');
    end;
    commit;
    return 'S';
    pq_utl.log('En FN_BORRADO_ANEXO_MOD FIN el borrado ' || p_idparcela ||
               '*********');
  exception
    when others then
      pq_utl.log('ERROR EN FN_BORRADO_ANEXO_MOD ' || SQLERRM || '*********');
      rollback;
      return 'N';
  end FN_BORRADO_ANEXO_MOD;

  --Procedimiento para borrar todos los anexos de reducción de capital de una póliza
  PROCEDURE PR_BORRAR_ANEXOS_REDUCCION(P_IDPOLIZA IN VARCHAR2, p_result out varchar2) is

    cursor c_anexos is
      select ID
        from tb_anexo_red r
       where r.idpoliza = P_IDPOLIZA;
    v_result varchar2(10) := null;
  BEGIN
  
    for indice in c_anexos loop
      --BORRAMOS LAS PARCELAS
      begin
        v_result := PQ_BORRAR_PARCELAS.FN_BORRADO_ANEXO_RED(indice.ID);
      exception
        when others then
          pq_utl.log('En PR_BORRAR_PARCELA_ANEXO_RED ' || P_IDPOLIZA ||
                     '*********');
      end;
      --BORRAMOS LOS ANEXOS
      begin
        delete TB_ANEXO_RED
         where ID = indice.ID;
         commit;
      exception
        when others then
          pq_utl.log('En TB_ANEXO_RED ' || P_IDPOLIZA ||
                     '*********');
      end;
      
    end loop;
    
    p_result := v_result;
  
  EXCEPTION
    when others then
      pq_utl.log('ERROR EN PR_BORRAR_ANEXOS_MODIFICACION ' || SQLERRM || '*********');
      rollback;
      p_result := 'N';
  
  END PR_BORRAR_ANEXOS_REDUCCION;
  
  --Función para borrar una parcela de un anexo de reducción de capital
  FUNCTION FN_BORRADO_ANEXO_RED(p_idparcela varchar2) return varchar2 is

  begin
    pq_utl.log('En FN_BORRADO_ANEXO_RED inicio el borrado ' || p_idparcela ||
               '*********');
    begin
      delete TB_ANEXO_RED_CAP_ASEG where idparcelaanexo = p_idparcela;
         commit;      
    exception
      when others then
        pq_utl.log('En TB_ANEXO_RED_CAP_ASEG ' || p_idparcela ||
                   '*********');
    end;
    begin
      delete TB_ANEXO_RED_PARCELAS p where p.Id = p_idparcela;
    exception
      when others then
        pq_utl.log('En TB_ANEXO_RED_PARCELAS ' || p_idparcela || '*********');
    end;
    commit;
    return 'S';
    pq_utl.log('En FN_BORRADO_ANEXO_RED FIN el borrado ' || p_idparcela ||
               '*********');
  exception
    when others then
      pq_utl.log('ERROR EN FN_BORRADO_ANEXO_RED ' || SQLERRM || '*********');
      rollback;
      return 'N';
  end FN_BORRADO_ANEXO_RED;
  
  --Procedimiento para borrar todos los siniestros de una póliza
  PROCEDURE PR_BORRAR_SINIESTROS(P_IDPOLIZA IN VARCHAR2, p_result out varchar2) is

    cursor c_siniestros is
      select ID
        from tb_siniestros s
       where s.idpoliza = P_IDPOLIZA;
    v_result varchar2(10) := null;
  BEGIN
  
    for indice in c_siniestros loop
      --BORRAMOS LAS PARCELAS
      begin
        v_result := PQ_BORRAR_PARCELAS.FN_BORRADO_SINIESTRO(indice.ID);
      exception
        when others then
          pq_utl.log('En PR_BORRAR_PARCELA_SINIESTRO ' || P_IDPOLIZA ||
                     '*********');
      end;
      --BORRAMOS LOS SINIESTROS
      begin
        delete TB_SINIESTROS
         where ID = indice.ID;
         commit;
      exception
        when others then
          pq_utl.log('En TB_SINIESTROS ' || P_IDPOLIZA ||
                     '*********');
      end;
      
    end loop;
    
    p_result := v_result;
  
  EXCEPTION
    when others then
      pq_utl.log('ERROR EN PR_BORRAR_SINIESTROS ' || SQLERRM || '*********');
      rollback;
      p_result := 'N';
  
  END PR_BORRAR_SINIESTROS;

  --Función para realizar el borrado de una parcela de anexo de modificacion
  FUNCTION FN_BORRADO_SINIESTRO(p_idparcela varchar2) return varchar2 is
    cursor c_capitales_asegurados is
      select *
        from TB_SINIESTRO_CAP_ASEG ca
       where ca.Idparcelasiniestro = p_idparcela;
  
  begin
    pq_utl.log('En FN_BORRADO_SINIESTRO inicio el borrado ' || p_idparcela ||
               '*********');
    for ind in c_capitales_asegurados loop
      begin
        delete TB_SINIESTRO_CAP_ASEG_DV
         where IDSINIESTROCAPASEG = ind.id;
         commit;
      exception
        when others then
          pq_utl.log('En TB_SINIESTRO_CAP_ASEG_DV ' || p_idparcela ||
                     '*********');
      end;
    end loop;
    begin
      delete TB_SINIESTRO_CAP_ASEG where IDPARCELASINIESTRO = p_idparcela;
         commit;      
    exception
      when others then
        pq_utl.log('En TB_SINIESTRO_CAP_ASEG ' || p_idparcela ||
                   '*********');
    end;
    begin
      delete TB_SINIESTRO_PARCELAS p where p.ID = p_idparcela;
    exception
      when others then
        pq_utl.log('En TB_SINIESTRO_PARCELAS ' || p_idparcela || '*********');
    end;
    commit;
    return 'S';
    pq_utl.log('En FN_BORRADO_SINIESTRO FIN el borrado ' || p_idparcela ||
               '*********');
  exception
    when others then
      pq_utl.log('ERROR EN FN_BORRADO_SINIESTRO ' || SQLERRM || '*********');
      rollback;
      return 'N';
  end FN_BORRADO_SINIESTRO;
  
  
  PROCEDURE PR_BORRAR_ASEGURADOS_REPETIDOS(P_CODENTIDAD IN VARCHAR2, P_CODUSUARIO IN VARCHAR2) IS
      TYPE cur_typ IS REF CURSOR;
	    v_cursor     cur_typ;
      v_num        number;
      v_nif        o02agpe0.tb_asegurados.nifcif%TYPE;
      v_id         number;
  BEGIN
  
      dbms_output.put_line('comienzo del borrado ');
      OPEN v_cursor FOR 'select count(*) as num, nifcif from o02agpe0.tb_asegurados a where a.codentidad = ' 
                        || P_CODENTIDAD || ' and a.codusuario = ''' || P_CODUSUARIO || 
                        ''' and a.revisado = ''N'' group by a.nifcif having count(*) > 1';
		  LOOP
			FETCH v_cursor INTO v_num, v_nif;
			    EXIT WHEN v_cursor%NOTFOUND;
			    SELECT min(id) into v_id FROM O02AGPE0.TB_ASEGURADOS A WHERE A.NIFCIF = v_nif;
          if (v_id IS NOT null) then
              delete o02agpe0.tb_datos_asegurados da where da.idasegurado = v_id;
              delete o02agpe0.tb_asegurados a where a.ID = v_id;
          end if;
		  END LOOP;
		  CLOSE v_cursor;

      commit;
  exception
      when others then
       dbms_output.put_line('Error');
       dbms_output.put_line('Error en el borrado ' || SQLCODE || ' - ' || SQLERRM);
       rollback;
  
  END PR_BORRAR_ASEGURADOS_REPETIDOS;

end PQ_BORRAR_PARCELAS;
/
SHOW ERRORS;