SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_ARREGLOS is
	
	procedure ARREGLA_HISTORICO_ESTADOS;
	
	procedure ARREGLA_FECHA_VENCIMIENTO;
	
end PQ_ARREGLOS;
/
create or replace package body o02agpe0.PQ_ARREGLOS is

	procedure ARREGLA_HISTORICO_ESTADOS IS
	
		q_insert varchar2(176) := 'insert into o02agpe0.tb_polizas_historico_estados values (o02agpe0.sq_polizas_historico_estados.nextval, :idpoliza, ''@ARREGLO'', :fecha, :estado, null, null, null, null, null)';
		cursor c_polizas is select p.idpoliza, p.idestado, p.fechaenvio, p.fecha_vigor, mp.renovable,
								   case p.idestado
									 when 8 then
									  p.fechaenvio
									 when 14 then
									  p.fechaenvio
									 when 16 then
									  case
									   when p.fecha_vto is not NULL then
										 case
										   when p.fecha_vto > sysdate then
											 nvl(p.fecha_seguimiento, nvl(p.fecha_env_canc_iris, p.fecha_modificacion))
										   when p.fecha_vto <= sysdate then
											 p.fecha_vto
										 end
									   when p.fecha_vto is NULL then
										 nvl(p.fecha_seguimiento, nvl(p.fecha_env_canc_iris, p.fecha_modificacion))
									   end
								   end as fecha
							  from o02agpe0.tb_polizas p
							 inner join o02agpe0.tb_modulos_poliza mp on mp.idpoliza = p.idpoliza
							 inner join o02agpe0.tb_lineas l on l.lineaseguroid = p.lineaseguroid
							  left join o02agpe0.tb_polizas_historico_estados he on he.idpoliza =
																					p.idpoliza
																				and he.estado =
																					p.idestado
							 where he.id is null
							   and p.idestado in (8, 14, 16);
		
	BEGIN
	
		o02agpe0.PQ_UTL.LOG('[BEGIN] ARREGLA_HISTORICO_ESTADOS', 2);
	
		FOR poliza IN c_polizas LOOP
    
			o02agpe0.PQ_UTL.LOG('PROCESANDO POLIZA ' || poliza.idpoliza || ' ', 2);
    
		    IF poliza.idestado = 16 THEN			     
				
				IF poliza.renovable  = 1 THEN
					
					o02agpe0.PQ_UTL.LOG('POLIZA RENOVABLE ANULADA... INSERTANDO REGISTRO DE HISTORICO DE EMITIDA CON FECHA ' || nvl(poliza.fechaenvio, poliza.fecha_vigor) || ' ', 2);
					EXECUTE IMMEDIATE q_insert USING IN poliza.idpoliza, nvl(poliza.fechaenvio, poliza.fecha_vigor), 14;
				
				ELSE
				
					o02agpe0.PQ_UTL.LOG('POLIZA ANULADA... INSERTANDO REGISTRO DE HISTORICO DE ENVIADA CORRECTA CON FECHA ' || nvl(poliza.fechaenvio, poliza.fecha_vigor) || ' ', 2);
					EXECUTE IMMEDIATE q_insert USING IN poliza.idpoliza, nvl(poliza.fechaenvio, poliza.fecha_vigor), 8;
				
				END IF;
           
		  	END IF;			
			
			o02agpe0.PQ_UTL.LOG('INSERTANDO REGISTRO DE HISTORICO DE ESTADO ' || poliza.idestado || ' CON FECHA ' || poliza.fecha || ' ', 2);
			EXECUTE IMMEDIATE q_insert USING IN poliza.idpoliza, poliza.fecha, poliza.idestado;
      
			COMMIT;
      
		END LOOP;
		
		o02agpe0.PQ_UTL.LOG('[END] ARREGLA_HISTORICO_ESTADOS', 2);
	
	EXCEPTION	
		WHEN OTHERS THEN
			o02agpe0.PQ_UTL.LOG('[ERROR] ARREGLA_HISTORICO_ESTADOS ' || SQLERRM || ' ', 2);
	END;
	
	procedure ARREGLA_FECHA_VENCIMIENTO IS
	
		cursor c_polizas is select p.idpoliza, p.referencia, l.codplan, l.codlinea, p.codmodulo, mp.renovable, p.fechaenvio, p.fecha_env_canc_iris
							from o02agpe0.tb_polizas p
							inner join o02agpe0.tb_lineas l on l.lineaseguroid = p.lineaseguroid
							inner join o02agpe0.tb_modulos_poliza mp on mp.idpoliza = p.idpoliza
							where p.fecha_vto is null and p.idestado in (8, 14, 16);
							
		v_fecha VARCHAR2(10);
		v_fecha_base DATE;
		
	BEGIN
	
		o02agpe0.PQ_UTL.LOG('[BEGIN] ARREGLA_FECHA_VENCIMIENTO', 2);
	
		FOR poliza IN c_polizas LOOP
		
			v_fecha := NULL;
			v_fecha_base := NULL;
    
			o02agpe0.PQ_UTL.LOG('PROCESANDO POLIZA ' || poliza.idpoliza || ' ', 2);
			
			IF poliza.fecha_env_canc_iris IS NULL THEN
			
				IF poliza.fechaenvio IS NULL THEN
				
					IF poliza.renovable = 1 THEN
					
						BEGIN
						
							select pr.fecha_renovacion INTO v_fecha_base 
								from o02agpe0.tb_polizas_renovables pr 
								where pr.referencia = poliza.referencia and pr.plan = poliza.codplan;
						
						EXCEPTION	
							WHEN NO_DATA_FOUND THEN
								v_fecha_base := NULL;
						END;
	
					END IF;
					
				ELSE
				
					v_fecha_base := poliza.fechaenvio;
				
				END IF;
				
				IF v_fecha_base IS NOT NULL THEN
				
					v_fecha := o02agpe0.PQ_CALCULA_FECHA_VENCIMIENTO.obtener_vencimiento(v_fecha_base, poliza.codlinea, poliza.codmodulo);
				
				END IF;
				
			ELSE
			
				v_fecha := TO_CHAR(poliza.fecha_env_canc_iris, 'DD/MM/YYYY');
			
			END IF;			
	
		    
			
			IF v_fecha IS NULL THEN
			
				o02agpe0.PQ_UTL.LOG('NO SE ACTUALIZA AL NO HABER OBTENIDO FECHA DE VENCIMIENTO', 2);
			
			ELSE
			
				o02agpe0.PQ_UTL.LOG('ACTUALIZANDO FECHA DE VENCIMIENTO ' || v_fecha || ' ', 2);	
				o02agpe0.PQ_CALCULA_FECHA_VENCIMIENTO.actualizar_fechavto_poliza(poliza.idpoliza, v_fecha);
				
			END IF;
			
			COMMIT;
      
		END LOOP;
		
		o02agpe0.PQ_UTL.LOG('[END] ARREGLA_FECHA_VENCIMIENTO', 2);
	
	EXCEPTION	
		WHEN OTHERS THEN
			o02agpe0.PQ_UTL.LOG('[ERROR] ARREGLA_FECHA_VENCIMIENTO ' || SQLERRM || ' ', 2);
	END;

end PQ_ARREGLOS;
/
SHOW ERRORS;
