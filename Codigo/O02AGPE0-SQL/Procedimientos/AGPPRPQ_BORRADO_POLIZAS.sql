SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_BORRADO_POLIZAS is

  --- Author  : U029769
  --- Created : 28/09/2016 14:25:25
  -- Purpose :

   lc VARCHAR2(25) := 'PQ_BORRADO_POLIZAS.'; -- Variable que almacena el nombre del paquete y de la función

   PROCEDURE borradoFisico ;


end PQ_BORRADO_POLIZAS;
/

create or replace package body o02agpe0.PQ_BORRADO_POLIZAS is


   PROCEDURE borradoFisico IS
   
   TYPE TpCursor                         IS REF CURSOR;
   l_tp_cursor                           TpCursor;
   l_sql                                 VARCHAR2(2000);
   v_idpoliza                            o02agpe0.tb_polizas.idpoliza%type;
   v_tiporef                             o02agpe0.tb_polizas.tiporef%type;
   v_hasSBP                              VARCHAR2(2);
   v_hasRC                               VARCHAR2(2);
   v_hasSBP_C                            VARCHAR2(2);
   num_plz_estado_baja                   NUMBER :=0;
   num_plz_borradas_ok                   NUMBER :=0;
   num_plz_borradas_ko                   NUMBER :=0;
   ids_polizas_ko                        clob;
   /* ESC-7662 */
   v_par_estructura                      NUMBER :=0;
   num_plz_estructura                    NUMBER :=0;
   ids_plz_estructura                    clob;
   
   BEGIN
   
   l_sql := 'select p.idpoliza, p.tiporef from o02agpe0.tb_polizas p where p.idestado = 0' ;

   OPEN l_tp_cursor FOR l_sql;
      LOOP FETCH l_tp_cursor INTO v_idpoliza, v_tiporef;
          EXIT WHEN l_tp_cursor%NOTFOUND;
                
                BEGIN
                    num_plz_estado_baja := num_plz_estado_baja + 1 ;
					 
					-- MIRAMOS SI LA POLIZA TIENE SBP O RC 
					select decode(sbp.id, null, 'NO', 'SI'),
							decode(rc.id, null, 'NO', 'SI')
					into v_hasSBP, v_hasRC
					from o02agpe0.tb_polizas p
					left outer join o02agpe0.tb_sbp_polizas sbp on sbp.idestado <> 0
					and ((p.tiporef = 'P' AND
					sbp.idpoliza = p.idpoliza and
					sbp.tipoenvio = 1) OR
					(p.tiporef = 'C' AND
					sbp.idpolizacom = p.idpoliza and
					sbp.tipoenvio = 2))
					left outer join o02agpe0.tb_rc_polizas rc on rc.idestado <> 0
					and rc.idpoliza = p.idpoliza
					where p.idpoliza = v_idpoliza;
            
          /* ESC-7662 ** MODIF TAM (30/12/2019) INICIO */
          /* MIRAMOS SI LA POLIZA TIENE PARCELA DE ESTRUCTURA */
          	select count(*) 
            into v_par_estructura
						from o02agpe0.tb_parcelas par
             where par.idpoliza = v_idpoliza
               and par.idparcelaestructura is not null
               and par.tipoparcela ='E';
               
          /* Si tiene parcelas de tipo Estructura hay que borrarlas antes de borrar la póliza. */
          IF v_par_estructura > 0 THEN
 						PQ_Utl.LOG (lc, 'Póliza con Parcelas de tipo Estructura: '||v_idpoliza || '. ', 2);
            
						num_plz_estructura := num_plz_estructura + 1;
            ids_plz_estructura := ids_plz_estructura ||' '||  v_idpoliza;
            
            DELETE FROM o02agpe0.tb_parcelas par 
              WHERE par.idpoliza = v_idpoliza 
                AND par.idparcelaestructura is not null
                AND par.tipoparcela ='E';
            COMMIT;

          END IF;
          
          /* ESC-7662 ** MODIF TAM (30/12/2019) FIN */            

					IF v_hasSBP = 'SI' OR v_hasRC = 'SI' THEN
						-- LA POLIZA NO SE PUEDE BORRAR POR TENER SBP O RC ASOCIADOS
						PQ_Utl.LOG (lc, 'Póliza con SBP o RC asociadas: '||v_idpoliza || '. ', 2);
						num_plz_borradas_ko := num_plz_borradas_ko + 1;
                        ids_polizas_ko := ids_polizas_ko ||' '||  v_idpoliza;
						
					ELSE
						-- BORRAMOS DIRECTAMENTE LOS SUPUESTOS REGISTROS SBP O RC EN ESTADO BORRADOR/SIMULACION
						DELETE FROM o02agpe0.tb_sbp_polizas sbp WHERE sbp.idpoliza = v_idpoliza AND sbp.idestado = 0;
						DELETE FROM o02agpe0.tb_rc_polizas rc WHERE rc.idpoliza = v_idpoliza AND rc.idestado = 0;
						
						-- PARA COMPLEMENTARIAS MIRAMOS SI LA PRINCIPAL TIENE SBP
						IF v_tiporef = 'C' THEN
							select decode(sbp.id, null, 'NO', 'SI')
								into v_hasSBP_C
								from o02agpe0.tb_polizas p
								left outer join o02agpe0.tb_sbp_polizas sbp on sbp.idpoliza = p.idpoliza_ppal
								and sbp.tipoenvio = 1
								and sbp.idpolizacom = p.idpoliza
								where p.idpoliza = v_idpoliza;
								
							IF v_hasSBP_C = 'SI' THEN
								-- ELIMINAMOS LA RELACION SBP/POLIZA COMPLEMENTARIA PARA PODER BORRAR ESTA ULTIMA
								UPDATE o02agpe0.tb_sbp_polizas sbp SET sbp.idpolizacom = NULL
									WHERE sbp.idpoliza = (SELECT p.idpoliza_ppal FROM o02agpe0.tb_polizas P
															WHERE p.idpoliza = v_idpoliza);
							END IF;
						END IF;
                    
						DELETE o02agpe0.TB_POLIZAS WHERE IDPOLIZA = v_idpoliza;

						COMMIT;
						num_plz_borradas_ok := num_plz_borradas_ok+ 1;
					END IF;
          
                
                EXCEPTION
                        WHEN OTHERS THEN
                            PQ_Utl.LOG (lc, 'ERROR al borrar la póliza: '||v_idpoliza ||' - '|| SQLCODE || ' - ' || SQLERRM || '. ', 2);
                            ROLLBACK;
                            num_plz_borradas_ko := num_plz_borradas_ko + 1;
                            ids_polizas_ko := ids_polizas_ko ||' '||  v_idpoliza;
                END;    
   
    END LOOP;
    -- Enviamos el correo
    Pq_Envio_Correos.generaCorreoBorradoPoliza(num_plz_estado_baja,num_plz_borradas_ok,num_plz_borradas_ko,ids_polizas_ko);
   
END borradoFisico;   


end PQ_BORRADO_POLIZAS;
/
SHOW ERRORS;
