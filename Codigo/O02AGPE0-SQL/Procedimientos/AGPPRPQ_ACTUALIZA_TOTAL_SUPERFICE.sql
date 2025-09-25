SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_ACTUALIZA_TOTAL_SUPERFICE is

  -- Author  : U028982
  -- Created : 26/1/2012 13:51:50
  --- Modify  : 18/07/2018 (decompilado)
  --- Purpose : 
  procedure actualizarTotalSuperficie;
  

end PQ_ACTUALIZA_TOTAL_SUPERFICE;
/
create or replace package body o02agpe0.PQ_ACTUALIZA_TOTAL_SUPERFICE is

procedure actualizarTotalSuperficie is
      
      TYPE  TpCursor	IS REF CURSOR;
      consulta_plz    varchar2(2000) := 'select pol.idPoliza from tb_polizas pol where pol.idestado > 1 and pol.totalsuperficie is null';
      v_idPoliza      TB_POLIZAS.IDPOLIZA%TYPE;
      aux_cur_plz	    TpCursor;
      v_totalSup      number(15);
      
      err_num             NUMBER;
      err_msg             VARCHAR2 (2000);
      
    BEGIN
      --Seleccinamos el idPoliza con idEstado>1     
      OPEN aux_cur_plz FOR consulta_plz;
    	LOOP FETCH aux_cur_plz INTO v_idPoliza;
    	
          EXIT WHEN aux_cur_plz%NOTFOUND;
          -- Calculamos el total superficie de la poliza
          select sum(superficie) into v_totalSup from tb_capitales_asegurados ca
          where ca.idparcela in (select idparcela from tb_parcelas where idpoliza = v_idPoliza);
        
          --DBMS_OUTPUT.put_line('Poliza: '||v_idPoliza||', total superficie: '|| v_totalSup);
          --Actualizamos v_totalSup a la poliza
          IF v_totalSup is not null THEN
             update tb_polizas po
             set totalsuperficie = v_totalSup
             where idpoliza = v_idPoliza;
             commit;
          END IF;
      
      END LOOP;
      CLOSE aux_cur_plz;
      
      EXCEPTION
      when others then  
          err_num := SQLCODE;
          err_msg := SQLERRM;       
          DBMS_OUTPUT.put_line('Mensaje: '||err_msg||', codigo: '|| err_num);
          rollback;
  
end actualizarTotalSuperficie;
 
END PQ_ACTUALIZA_TOTAL_SUPERFICE;
/
SHOW ERRORS;