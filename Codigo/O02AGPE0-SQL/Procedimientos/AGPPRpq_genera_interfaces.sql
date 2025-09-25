CREATE OR REPLACE PACKAGE o02agpe0.pq_genera_interfaces IS

	PROCEDURE pr_envio_ci(p_fecha_ejecucion VARCHAR2 DEFAULT to_char(SYSDATE, 'YYYYMM'));

END pq_genera_interfaces;
/
CREATE OR REPLACE PACKAGE BODY o02agpe0.pq_genera_interfaces IS

	PROCEDURE pr_envio_ci(p_fecha_ejecucion VARCHAR2 DEFAULT to_char(SYSDATE, 'YYYYMM')) IS
	
	
		v_file         utl_file.file_type;
		v_file_log     utl_file.file_type;
		v_linea        VARCHAR2(32000) := NULL;
		v_linea_log    VARCHAR2(32000) := NULL;
		v_f_nombre     VARCHAR2(150) := NULL;
		v_f_nombre_log VARCHAR2(150) := NULL;
		v_contador     NUMBER := 0;
		v_porfra       VARCHAR2(2) := NULL;
		v_segfec       VARCHAR2(10) := NULL;
		v_segpor       VARCHAR2(15) := NULL;
	
		v_fecha_envio VARCHAR2(10) := NULL;
		v_fecha_carga VARCHAR2(6) := to_char(SYSDATE, 'YYYYMM');
	
	
	
		CURSOR c_polizas IS
			SELECT c.codentidad, p.referencia, p.dc, nvl(p.oficina, 0) oficina, a.nifcif, a.discriminante, ' ' opccol, l.codlinea, to_char(pp.fecha, 'dd.mm.yyyy') feccob, to_char(p.fechaenvio, 'dd.mm.yyyy') fecenv, to_char(pp.fechasegundopago, 'dd.mm.yyyy') segfeci, dc.primacomercial +
							dc.reaseguro +
							dc.recargo costetotal, dc.cargotomador, pp.pctprimerpago, pp.pctsegundopago, to_char((nvl(SUM(rga.gasentsum + rga.gassubsum + rga.gassum +
															 rga.imptra), '0') * 100), 'S00000000000009') comision
				FROM tb_polizas p, tb_coms_comis_aplicaciones com, tb_colectivos c, tb_asegurados a, tb_lineas l, tb_pagos_poliza pp, tb_distribucion_costes dc, tb_rga_comisiones rga, tb_coms_ficheros cf, tb_coms_comisiones rc
			 WHERE com.referencia = p.referencia
				 AND com.dc = p.dc
				 AND com.tiporeferencia = p.tiporef
				 AND p.idcolectivo = c.id
				 AND p.idasegurado = a.id
				 AND p.lineaseguroid = l.lineaseguroid
				 AND p.idpoliza = pp.idpoliza
				 AND p.idpoliza = dc.idpoliza
				 AND rga.refplz = p.referencia
				 AND rga.tippol = p.tiporef
				 AND cf.id = rc.idfichero
				 AND c.codentidad != 9998
				 AND p.idestado = 8
			--         AND to_char(cf.fechaaceptacion, 'YYYYMM') = v_fecha_carga
			 GROUP BY c.codentidad, p.referencia, p.dc, p.oficina, a.nifcif, a.discriminante, ' ', l.codlinea, to_char(pp.fecha, 'dd.mm.yyyy'), to_char(p.fechaenvio, 'dd.mm.yyyy'), to_char(pp.fechasegundopago, 'dd.mm.yyyy'), dc.primacomercial +
								 dc.reaseguro +
								 dc.recargo, dc.cargotomador, pp.pctprimerpago, pp.pctsegundopago;
	
	
	BEGIN
	
		v_f_nombre_log := 'LOG_CI.log';
		v_f_nombre     := 'Fichero_CI.txt';
	
		v_fecha_carga := p_fecha_ejecucion;
	
		dbms_output.put_line(' Fecha de datos => ' || p_fecha_ejecucion);
	
	
	
		v_file_log := utl_file.fopen(location => 'AGP_BATCH', filename => v_f_nombre_log, open_mode => 'w');
	
		v_linea_log := 'Inicio proceso => ' || to_char(SYSDATE, 'dd/mm/yyyy hh24:mi:ss');
	
		utl_file.put_line(file => v_file_log, buffer => v_linea_log);
	
		v_linea_log := 'Fecha de Datos => ' || v_fecha_carga;
	
		utl_file.put_line(file => v_file_log, buffer => v_linea_log);
	
	
		v_file := utl_file.fopen(location => 'AGP_BATCH', filename => v_f_nombre, open_mode => 'w');
	
	
	
		FOR a IN c_polizas LOOP
		
		
			IF a.fecenv IS NULL THEN
				v_fecha_envio := a.feccob;
			ELSE
				v_fecha_envio := a.fecenv;
			END IF;
		
		
		
			IF (a.pctprimerpago * 100) = 10000 THEN
				v_porfra := 1;
				v_segfec := '11.11.1111';
				v_segpor := '000000000000000';
			ELSE
				v_porfra := 2;
				v_segfec := nvl(to_char(to_date(a.segfeci, 'YYYYMMDD'), 'DD.MM.YYYY'), '11.11.1111');
				v_segpor := a.pctsegundopago * 100;
			END IF;
		
		
			v_linea := lpad(a.codentidad, 4, '0') || a.referencia || a.dc || lpad(a.oficina, 4, '0') || '00' || lpad(a.nifcif, 9, '0') ||
								 rpad(a.discriminante, 3, ' ') || ' ' || lpad(a.codlinea, 3, '0') || 'SS41ZZ' || lpad(a.feccob, 10, '0') || lpad(v_porfra, 2, '0') ||
								 lpad(v_fecha_envio, 10, '0') || lpad(v_segfec, 10, '0') || '000000000000000' || '000000000000000' || '000000000000000' ||
								 '000000000000000' || '000000000000000' || lpad((a.costetotal * 100), 15, '0') || lpad((a.cargotomador * 100), 15, '0') ||
								 lpad((a.pctprimerpago * 100), 15, '0') || lpad(v_segpor, 15, '0') || '000000000000000' || lpad(a.comision, 15, '0') ||
								 '000000000000000' || '000000000000000' || '000000000000000' || '00000';
		
		
		
			utl_file.put_line(file => v_file, buffer => v_linea);
		
			v_linea_log := 'Añado la póliza => ' || a.referencia || '-' || a.dc || ' de la Entidad ' || a.codentidad;
		
			utl_file.put_line(file => v_file_log, buffer => v_linea_log);
		
		
		
			v_contador := v_contador + 1;
		END LOOP;
	
	
		utl_file.fclose(v_file);
	
		v_linea_log := 'Lineas procesadas => ' || v_contador;
	
		utl_file.put_line(file => v_file_log, buffer => v_linea_log);
	
		v_linea_log := 'Fin proceso => ' || to_char(SYSDATE, 'dd/mm/yyyy hh24:mi:ss');
	
		utl_file.put_line(file => v_file_log, buffer => v_linea_log);
		utl_file.fclose(v_file_log);
	
		--AGP_INTERFACES_COPIAS
	
		utl_file.fcopy(src_location => 'AGP_BATCH', src_filename => v_f_nombre, dest_location => 'AGP_INTERFACES_COPIAS', dest_filename => 'Fichero_CI_' ||
																		 to_char(SYSDATE, 'yyyymmddhh24miss') ||
																		 '.txt');
	
		utl_file.fcopy(src_location => 'AGP_BATCH', src_filename => v_f_nombre_log, dest_location => 'AGP_INTERFACES_COPIAS', dest_filename => 'Log_CI_' ||
																		 to_char(SYSDATE, 'yyyymmddhh24miss') ||
																		 '.log');
	
	
	
	EXCEPTION
		WHEN OTHERS THEN
			dbms_output.put_line(SQLERRM);
	END pr_envio_ci;


END pq_genera_interfaces;
/
