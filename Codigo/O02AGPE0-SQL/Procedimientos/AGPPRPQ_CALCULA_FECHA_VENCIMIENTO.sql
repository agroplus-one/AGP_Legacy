SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_CALCULA_FECHA_VENCIMIENTO is

  /* Busca las pólizas Enviadas o Emitidas de planes >= 2020 y que no tenga informada la fecha de vencimiento*/
  FUNCTION calcula_fecha_vencimiento RETURN VARCHAR2;
  
  /* Calcula la fecha de vencimiento y la retorna como parametro */
  FUNCTION obtener_vencimiento (v_fecha_envio in DATE, v_cod_linea in VARCHAR2, v_codmodulo in VARCHAR2) RETURN VARCHAR2;
  
  /* Actualiza el valor de la fecha Vto de la póliza */
  PROCEDURE actualizar_fechavto_poliza (p_idpoliza IN VARCHAR2, v_fecha_vto IN VARCHAR2);
  
end PQ_CALCULA_FECHA_VENCIMIENTO;
/
create or replace package body o02agpe0.PQ_CALCULA_FECHA_VENCIMIENTO is
  -- *************************************************************************************************--
  -- Author  : T-Systems (P0067752)   - Body                                                                --
  -- Created : 26/08/2021 14:01:36                                                                    --
  -- Purpose : Procedimiento que calcula la fecha de vencimiento de las pólizas Enviadas y Emitidas   --
  -- Version : v1.1
  -- *************************************************************************************************--

 ---------------------------------------------------------------------------------------------------------------------------
 -- FUNCTION devolver si hay pólizas en vigor
 -----------------------------------------------------------------------------------------------------------------------------
 FUNCTION calcula_fecha_vencimiento RETURN VARCHAR2 IS
      
      v_id_poliza      VARCHAR2(10);
      v_codplan_ini    VARCHAR2(4);
      l_query_pol      VARCHAR2(2000);

        
      v_fechaEnvio     VARCHAR2(8);
      v_codlinea       VARCHAR2(4);
      v_codplan        VARCHAR2(4);
      v_codmodulo      VARCHAR2(5);
      v_fecha_vto      VARCHAR2(10);
    	l_num_polizas    NUMBER := 0;                     -- Contador para el numero de polizas a tratar.
	    l_num_pol_ok     NUMBER := 0;                     -- Contador para el numero de polizas actualizadas.      
      v_f_envio_aux    DATE;
      
      
      var_plan_vto     VARCHAR2 (30) := 'PLAN_FEC_VTO'; -- Nombre de la variable de ocnfiguración de TB_CONFIG_AGP que contiene el nombre del DIRECTORY
      
      lc               VARCHAR2(60) := 'PQ_CALCULA_FECHA_VENCIMIENTO.calculaFechaVencimiento'; -- Variable que almacena el nombre del paquete y de la función
        
      TYPE cur_typ IS REF CURSOR;
      C_POLIZAS cur_typ;
      
   BEGIN
 		  pq_utl.log(lc, '## INICIO PROCEDIMIENTO PQ_CALCULA_FECHA_VENCIMIENTO ##', 1);
 
      v_codplan_ini := PQ_Utl.getcfg (var_plan_vto);
      
      execute immediate 'select count(*) FROM o02agpe0.tb_polizas PO 
                         inner join o02agpe0.tb_lineas li on li.lineaseguroid = po.lineaseguroid 
                         where po.idestado in (8, 14) 
                           and po.fecha_vto is null
                           and po.fechaenvio is not null 
                           and li.codplan >=  ' || v_codplan_ini || ' ' into l_num_polizas;
                           
      pq_utl.log(lc, ' Valor de l_num_poliza:'|| l_num_polizas, 1);                     
                           

      l_query_pol := ' SELECT PO.IDPOLIZA, TO_CHAR(PO.FECHAENVIO, ''DDMMYYYY''), ' ||
                                        ' PO.CODMODULO, LI.CODLINEA, LI.CODPLAN ' ||
                                   ' FROM o02agpe0.tb_polizas PO ' ||
                                   ' INNER JOIN o02agpe0.tb_lineas li on li.lineaseguroid = po.lineaseguroid ' ||
                                   ' WHERE po.idestado in (8, 14) ' ||
                                     ' AND  po.fecha_vto is null ' ||
                                     ' AND po.fechaenvio is not null ' ||
                                     ' AND li.codplan >=  ' || v_codplan_ini; 
     PQ_UTL.log (lc, 'Valor de l_query_pol: ' || l_query_pol);         

      --LUEGO INSERTAMOS TODOS LOS REGISTROS PARA LOS ASEGURADOS QUE TIENEN PÓLIZA
     OPEN C_POLIZAS FOR l_query_pol;
      LOOP
           FETCH C_POLIZAS INTO
             v_id_poliza,
             v_fechaenvio,
             v_codmodulo,
             v_codlinea,
             v_codplan;
            
	    EXIT WHEN C_POLIZAS%NOTFOUND;
        /* Si estamos bloqueando por cada id de ASegurado tendremos que 
        /* comprobar si tiene pólizas en vigor */
        pq_utl.log(lc, 'Obtenemos Fecha Vencimiento para la póliza: ' || v_id_poliza );
        v_f_envio_aux := TO_DATE (v_fechaenvio, 'DDMMYYYY');
        
        v_fecha_vto := obtener_vencimiento(v_f_envio_aux, v_codlinea, v_codmodulo);

           
       /* Si encontramos al menos una póliza en vigor, salimos con true */
       IF (v_fecha_vto is not null) THEN
          actualizar_fechavto_poliza(v_id_poliza, v_fecha_vto);
          l_num_pol_ok := l_num_pol_ok + 1;
        END IF;

      END LOOP;

     CLOSE C_POLIZAS;
        
         
     PQ_Utl.LOG(lc, '');
	   PQ_Utl.LOG(lc, '*********************************************************************************', 2);
 	   PQ_Utl.LOG(lc, 'ACTUALIZACIÓN DE FECHA DE VENCIMIENTO PÓLIZAS', 2);
	   PQ_Utl.LOG(lc, 'ESTADISTICAS EN FECHA ' || TO_CHAR(SYSDATE,'DD/MM/YY HH24:MI:SS'), 2);
	   PQ_Utl.LOG(lc, '*********************************************************************************', 2);
	   PQ_Utl.LOG(lc, 'Polizas Procesadas                                := ' || l_num_polizas, 2);   
     PQ_Utl.LOG(lc, 'Polizas Actualizadas                              := ' || l_num_pol_ok, 2);   
	   PQ_Utl.LOG(lc, '*********************************************************************************', 2);
	   PQ_Utl.LOG(lc, '', 2);
	   PQ_Utl.LOG(lc, 'El proceso ha finalizado correctamente a las ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
	   PQ_Utl.LOG(lc, '## FIN PROCEDIMIENTO PQ_CALCULA_FECHA_VENCIMIENTO ##', 1);
      
     RETURN 'OK'; 

    EXCEPTION
       WHEN others THEN
       	   PQ_Utl.LOG('ERROR EN Calcula Fecha Vencimiento Polizas' || SQLERRM || '*********');
           ROLLBACK;
            
 END calcula_fecha_vencimiento; 
    
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION obtener_vencimiento
	--  ---------------------------------------------------------------------------------------------------------------------------
	FUNCTION obtener_vencimiento (v_fecha_envio in DATE, v_cod_linea in VARCHAR2, v_codmodulo in VARCHAR2) RETURN VARCHAR2 IS
  
      v_vencimiento          BOOLEAN := false;
      v_meses                NUMBER(5);
      v_fecha_vencida        VARCHAR2(10) := '';
      d_fecha_vencida        DATE;
      lc VARCHAR2(50) := 'pq_envio_polizas_agro_iris.comprobar_vencimiento'; -- Variable que almacena el nombre del paquete y de la funcion
      
   BEGIN
   
      pq_utl.log(lc, 'Entramos a consultar la fecha Vencimiento',2);
     
   
      -- 1º buscamos el valor del mes venicmiento de forma mas especifica (por codlinea y codmodulo)
      BEGIN 
         select ME.NUM_MESES
           into v_meses
           from o02agpe0.tb_meses_venc ME
          where ME.CODLINEA = v_cod_linea
            and ME.CODMODULO = v_codmodulo;
            
      EXCEPTION
         WHEN NO_DATA_FOUND THEN
           v_meses := 0;
         WHEN OTHERS THEN
           DBMS_OUTPUT.PUT_LINE(SQLERRM);
           pq_utl.log(lc,SQLERRM);
			     -- Se escribe en el log el error
			     pq_utl.log(lc, 'Se ha producido un error al recuperar los meses de vencimiento ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
			     pq_err.raiser(SQLCODE, 'Error al buscar los meses de vencimiento(1)' || ' [' || SQLERRM || ']');
           return null;
      END;   
         
      
      IF (v_meses = 0) THEN
         BEGIN 
           -- 2º buscamos el valor del mes venicmiento de forma mas genérica (por codlinea y codmodulo genérico)         
           select ME.NUM_MESES
             into v_meses
             from o02agpe0.tb_meses_venc ME
            where ME.CODLINEA = v_cod_linea
              and ME.CODMODULO = '99999';
         EXCEPTION
            WHEN NO_DATA_FOUND THEN
              v_meses := 0;
            WHEN OTHERS THEN
              DBMS_OUTPUT.PUT_LINE(SQLERRM);
              pq_utl.log(lc,SQLERRM);
			        -- Se escribe en el log el error
			        pq_utl.log(lc, 'Se ha producido un error al recuperar los meses de vencimiento ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
			        pq_err.raiser(SQLCODE, 'Error al buscar los meses de vencimiento(2)' || ' [' || SQLERRM || ']');
              return null;
         END;      
      END IF;     

      -- 3º buscamos el valor del mes venicmiento de forma genérica (por codlinea genérica y codmodulo genérico)            
      IF (v_meses = 0) THEN
         BEGIN 
           -- 2º buscamos el valor del mes venicmiento de forma mas genérica (por codlinea y codmodulo genérico)         
           select ME.NUM_MESES
             into v_meses
             from o02agpe0.tb_meses_venc ME
            where ME.CODLINEA = 999
              and ME.CODMODULO = '99999';
         EXCEPTION
            WHEN NO_DATA_FOUND THEN
              v_meses := 0;
            WHEN OTHERS THEN
              DBMS_OUTPUT.PUT_LINE(SQLERRM);
              pq_utl.log(lc,SQLERRM);
			        -- Se escribe en el log el error
			        pq_utl.log(lc, 'Se ha producido un error al recuperar los meses de vencimiento ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
			        pq_err.raiser(SQLCODE, 'Error al buscar los meses de vencimiento(3)' || ' [' || SQLERRM || ']');
              return null;
         END;            
      END IF;      
      
      -- Una vez obtenido los meses para la línea/codmodulo, calculamos la fehca de vencimiento   
      IF v_meses <> 0 THEN
            v_fecha_vencida := TO_CHAR(add_months(v_fecha_envio, +v_meses), 'DD/MM/YYYY');
             pq_utl.log(lc, 'Valor de v_fecha_vencida:' || v_fecha_vencida,2);
      ELSE 
          return null;      
      END IF;      
      
      /* RETORNAMOS LA FECHA DE VENCIMIENTO CALCULADA */
      RETURN v_fecha_vencida;
      
      
    END;
    
    
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION obtener_vencimiento
	--  ---------------------------------------------------------------------------------------------------------------------------
    PROCEDURE actualizar_fechavto_poliza (p_idpoliza IN VARCHAR2, v_fecha_vto IN VARCHAR2) IS
    
    lc               VARCHAR2(60) := 'PQ_CALCULA_FECHA_VENCIMIENTO.actualizar_fechavto_poliza'; -- Variable que almacena el nombre del paquete y de la función
        
    BEGIN
           UPDATE o02agpe0.tb_polizas pol
              SET pol.fecha_vto = TO_DATE (v_fecha_vto,  'DD/MM/YYYY')
            WHERE pol.idpoliza = p_idpoliza;            
            
        COMMIT;

    EXCEPTION
         when others then
            pq_utl.log('* ERROR EN la Actualización del registro fecha_vto de Póliza:  ' || p_idpoliza || ' con el error:' || SQLERRM || '*');
            rollback;
    END actualizar_fechavto_poliza;



end PQ_CALCULA_FECHA_VENCIMIENTO;
/
SHOW ERRORS;