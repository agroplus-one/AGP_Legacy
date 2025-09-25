INSERT INTO o02agpe0.TB_ESTADOS_AGROSEGURO VALUES (1, 'Correcta', '');
INSERT INTO o02agpe0.TB_ESTADOS_AGROSEGURO VALUES (2, 'Con errores', '');
INSERT INTO o02agpe0.TB_ESTADOS_AGROSEGURO VALUES (3, 'Emitida', '');
INSERT INTO o02agpe0.TB_ESTADOS_AGROSEGURO VALUES (4, 'Refundida', '');
INSERT INTO o02agpe0.TB_ESTADOS_AGROSEGURO VALUES (5, 'Precartera generada', '');
INSERT INTO o02agpe0.TB_ESTADOS_AGROSEGURO VALUES (6, 'Precartera precalculada', '');
INSERT INTO o02agpe0.TB_ESTADOS_AGROSEGURO VALUES (7, 'Primera comunicación', '');
INSERT INTO o02agpe0.TB_ESTADOS_AGROSEGURO VALUES (8, 'Comunicación definitiva', '');
INSERT INTO o02agpe0.TB_ESTADOS_AGROSEGURO VALUES (9, 'Anulada', '');
INSERT INTO o02agpe0.TB_ESTADOS_AGROSEGURO VALUES (10, 'Rescindida', '');
INSERT INTO o02agpe0.TB_ESTADOS_AGROSEGURO VALUES (11, 'Con gastos sin renovación', '');

INSERT INTO o02agpe0.TB_RELACION_ESTADOS VALUES (3, 14, 4);
INSERT INTO o02agpe0.TB_RELACION_ESTADOS VALUES (5, 19, 9);
INSERT INTO o02agpe0.TB_RELACION_ESTADOS VALUES (6, 18, 8);
INSERT INTO o02agpe0.TB_RELACION_ESTADOS VALUES (7, 12, 2);
INSERT INTO o02agpe0.TB_RELACION_ESTADOS VALUES (8, 13, 3);
INSERT INTO o02agpe0.TB_RELACION_ESTADOS VALUES (9, 16, 6);
INSERT INTO o02agpe0.TB_RELACION_ESTADOS VALUES (10, 15, 5);
INSERT INTO o02agpe0.TB_RELACION_ESTADOS VALUES (11, 17, 7);

UPDATE o02agpe0.TB_POLIZAS SET IDESTADO = 16 WHERE IDESTADO = 4;

DELETE FROM o02agpe0.TB_ESTADOS_POLIZA WHERE IDESTADO = 4;

-- INSERT VALORES EN TB_MTOINF_VISTAS_CAMPOS (Tabla 146:TB_INF_ANEXOS_MODIFICACION)
------------------------------------------------------------------------------------ 
 insert into o02agpe0.TB_MTOINF_VISTAS_CAMPOS
   values ((select max(id)+1 from o02agpe0.tb_mtoinf_vistas_campos),
           '146',
           'Estado_Agroseguro_AM',
           'IDESTADO_AGRO',
           'TB_INF_ANEXOS_MODIFICACION',
           '0',
           '1',
           '6');
           
 
 insert into o02agpe0.TB_MTOINF_VISTAS_CAMPOS
   values ((select max(id)+1 from o02agpe0.tb_mtoinf_vistas_campos),
           '146',
           'Fecha_Actualizacion_AM',
           'FECHA_SEGUIMIENTO',
           'TB_INF_ANEXOS_MODIFICACION',
           '1',
           '1',
           '0');
 
-- INSERT VALORES EN TB_MTOINF_VISTAS_CAMPOS (Tabla 139:TB_INF_POLIZAS)
------------------------------------------------------------------------------------ 
 insert into o02agpe0.TB_MTOINF_VISTAS_CAMPOS
   values ((select max(id)+1 from o02agpe0.tb_mtoinf_vistas_campos),
           '139',
           'Estado_Agroseguro',
           'IDESTADO_AGRO',
           'TB_INF_POLIZAS',
           '2',
           '1',
           '5');
           
 
 insert into o02agpe0.TB_MTOINF_VISTAS_CAMPOS
   values ((select max(id)+1 from o02agpe0.tb_mtoinf_vistas_campos),
           '139',
           'Fecha_Actualizacion',
           'FECHA_SEGUIMIENTO',
           'TB_INF_POLIZAS',
           '1',
           '1',
           '0');

INSERT INTO O02AGPE0.TB_CONFIG_AGP VALUES ('FECHA_CAM_EST_DESDE', '      ', 'Fecha Desde para la llamada al SW de seguimiento');	
INSERT INTO O02AGPE0.TB_CONFIG_AGP VALUES ('EST_POL_MAIL_SEG_TOM', '9,10', 'Estados póliza para el envío de correos de seguimiento a tomadores');
INSERT INTO O02AGPE0.TB_CONFIG_AGP VALUES ('EST_INC_MAIL_SEG_TOM', '''O'',''R'',''D''', 'Estados incidencia para el envío de correos de seguimiento a tomadores');

UPDATE O02AGPE0.TB_PARAMETROS SET ESTADO_PLZ_RENOV_PAGO = '3,';	   

COMMIT;
