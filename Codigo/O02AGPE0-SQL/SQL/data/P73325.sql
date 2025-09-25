delete from o02agpe0.tb_ged_doc_poliza b where b.idpoliza not in (select a.idpoliza from o02agpe0.tb_polizas a);

declare
  v_seq varchar(9);
begin
  for r_pol in (select a.idpoliza
                  from o02agpe0.tb_polizas a
                 where a.idpoliza not in
                       (select idpoliza
                          from o02agpe0.tb_ged_doc_poliza)) loop
    select 'A23' || (TO_NUMBER(SUBSTR(agp_valor, 4)) + 1)
      into v_seq
      from o02agpe0.tb_config_agp
     where agp_nemo = 'SEQ_BARCODE_A';
    dbms_output.put_line(v_seq);
    insert into o02agpe0.tb_ged_doc_poliza b
      (IDPOLIZA,
       IDDOCUMENTUM,
       COD_CANAL_FIRMA,
       IND_DOC_FIRMADA,
       FECHA_FIRMA,
       CODUSUARIO,
       COD_BARRAS)
    values
      (r_pol.idpoliza, 'N/A', 1, 'N', sysdate, 'N/A', v_seq);
    update o02agpe0.tb_config_agp
       set agp_valor = v_seq
     where agp_nemo = 'SEQ_BARCODE_A';
  end loop;
  commit;
end;

declare
  v_seq varchar(9);
begin
  for r_pol in (select a.id
                  from o02agpe0.tb_sbp_polizas a
                 where a.id not in
                       (select idpoliza_sbp
                          from o02agpe0.tb_ged_doc_poliza_sbp)) loop
    select 'D23' || (TO_NUMBER(SUBSTR(agp_valor, 4)) + 1)
      into v_seq
      from o02agpe0.tb_config_agp
     where agp_nemo = 'SEQ_BARCODE_D';
    dbms_output.put_line(v_seq);
    insert into o02agpe0.tb_ged_doc_poliza_sbp b
      (IDPOLIZA_SBP,
       IDDOCUMENTUM,
       COD_CANAL_FIRMA,
       IND_DOC_FIRMADA,
       FECHA_FIRMA,
       CODUSUARIO,
       COD_BARRAS)
    values
      (r_pol.id, 'N/A', 1, 'N', sysdate, 'N/A', v_seq);
    update o02agpe0.tb_config_agp
       set agp_valor = v_seq
     where agp_nemo = 'SEQ_BARCODE_D';
  end loop;
  commit;
end;

update o02agpe0.TB_CONFIG_AGP set AGP_VALOR=
 'select count(*) from TB_POLIZAS P
inner join TB_COLECTIVOS C on P.IDCOLECTIVO = C.ID
inner join TB_LINEAS L on P.LINEASEGUROID = L.LINEASEGUROID
inner join Tb_ASEGURADOS A on P.IDASEGURADO = A.ID
inner join TB_ESTADOS_POLIZA E on P.IDESTADO = E.IDESTADO
inner join TB_USUARIOS U on P.Codusuario = U.CODUSUARIO
inner join TB_GED_DOC_POLIZA D on P.IDPOLIZA= D.IDPOLIZA'
WHERE AGP_NEMO= 'SQL_COUNT_POLIZAS_UTILIDADES';

INSERT INTO o02agpe0.tb_config_agp VALUES('IRIS_API_KEY', '599682cc-c5ee-11ec-9d64-0242ac120002', 'API-KEY de acceso a los SW de IRIS');
INSERT INTO o02agpe0.tb_config_agp VALUES('GED_API_KEY', '599682cc-c5ee-11ec-9d64-0242ac120002', 'API-KEY de acceso a los SW de GED');

INSERT INTO o02agpe0.TB_MTOINF_VISTAS_CAMPOS VALUES (o02agpe0.sq_mtoinf_vistas_campos.nextval, (select ID from o02agpe0.TB_MTOINF_VISTAS WHERE NOMBRE_REAL = 'TB_INF_POLIZAS'), 'COD_CANAL_FIRMA', 'COD_CANAL_FIRMA', 'TB_INF_POLIZAS', 2, 1, 0);
INSERT INTO o02agpe0.TB_MTOINF_VISTAS_CAMPOS VALUES (o02agpe0.sq_mtoinf_vistas_campos.nextval, (select ID from o02agpe0.TB_MTOINF_VISTAS WHERE NOMBRE_REAL = 'TB_INF_POLIZAS'), 'IND_DOC_FIRMADA', 'IND_DOC_FIRMADA', 'TB_INF_POLIZAS', 2, 1, 0);
INSERT INTO o02agpe0.TB_MTOINF_VISTAS_CAMPOS VALUES (o02agpe0.sq_mtoinf_vistas_campos.nextval, (select ID from o02agpe0.TB_MTOINF_VISTAS WHERE NOMBRE_REAL = 'TB_INF_POLIZAS'), 'FECHA_FIRMA', 'FECHA_FIRMA', 'TB_INF_POLIZAS', 2, 1, 0);
INSERT INTO o02agpe0.TB_MTOINF_VISTAS_CAMPOS VALUES (o02agpe0.sq_mtoinf_vistas_campos.nextval, (select ID from o02agpe0.TB_MTOINF_VISTAS WHERE NOMBRE_REAL = 'TB_INF_POLIZAS'), 'COD_BARRAS', 'COD_BARRAS', 'TB_INF_POLIZAS', 2, 1, 0);

DECLARE
  type v_cursor is ref cursor;
  c1         v_cursor;
  v_idpoliza o02agpe0.tb_ged_doc_poliza.idpoliza%TYPE;
  v_plan     VARCHAR2(2);
  v_codebar  o02agpe0.tb_ged_doc_poliza.cod_barras%TYPE;
  TYPE index_type IS TABLE OF NUMBER INDEX BY VARCHAR2(2);
  v_index index_type;
  v_query VARCHAR2(1000);
BEGIN
v_query := 'SELECT g.idpoliza, SUBSTR(l.codplan, 3, 2)
      FROM o02agpe0.tb_ged_doc_poliza g
     INNER JOIN o02agpe0.tb_polizas p on p.idestado <> 0
                                     AND p.idpoliza = g.idpoliza
     INNER JOIN o02agpe0.tb_lineas l on l.lineaseguroid = p.lineaseguroid
                                    AND l.codplan BETWEEN 2011 AND 2023
     WHERE g.cod_barras = ''N/A''';
  v_index('11') := 1;
  v_index('12') := 1;
  v_index('13') := 1;
  v_index('14') := 1;
  v_index('15') := 1;
  v_index('16') := 1;
  v_index('17') := 1;
  v_index('18') := 1;
  v_index('19') := 1;
  v_index('20') := 1;
  v_index('21') := 1;
  v_index('22') := 1;
  v_index('23') := 1;
  OPEN c1 FOR v_query;
  LOOP
    FETCH c1
      INTO v_idpoliza, v_plan;
    EXIT WHEN c1%NOTFOUND;
    v_codebar := 'A' || v_plan || LPAD(v_index(v_plan), 6, 0);
    UPDATE o02agpe0.tb_ged_doc_poliza
      set cod_barras = v_codebar
    WHERE idpoliza = v_idpoliza;
    v_index(v_plan) := v_index(v_plan) + 1;
  END LOOP;
  CLOSE c1;
  COMMIT;
  update o02agpe0.tb_config_agp
   set agp_valor = (select max(SUBSTR(g.cod_barras, 4, 9))
                      from o02agpe0.tb_ged_doc_poliza g
                     INNER JOIN o02agpe0.tb_polizas p on p.idpoliza =
                                                         g.idpoliza
                     INNER JOIN o02agpe0.tb_lineas l on l.lineaseguroid =
                                                        p.lineaseguroid
                                                    AND l.codplan in
                                                        (2022, 2023))
 where agp_nemo = 'SEQ_BARCODE_A';
END;

DECLARE
  type v_cursor is ref cursor;
  c1         v_cursor;
  v_idpoliza o02agpe0.tb_ged_doc_poliza.idpoliza%TYPE;
  v_plan     VARCHAR2(2);
  v_codebar  o02agpe0.tb_ged_doc_poliza.cod_barras%TYPE;
  TYPE index_type IS TABLE OF NUMBER INDEX BY VARCHAR2(2);
  v_index index_type;
  v_query VARCHAR2(1000);
BEGIN
v_query := 'SELECT g.idpoliza_sbp, SUBSTR(l.codplan, 3, 2)
      FROM o02agpe0.tb_ged_doc_poliza_sbp g
     INNER JOIN o02agpe0.tb_sbp_polizas sbp on sbp.idestado = 5 and sbp.id = g.idpoliza_sbp
     INNER JOIN o02agpe0.tb_polizas p on p.idestado <> 0 AND p.idpoliza = sbp.idpoliza
     INNER JOIN o02agpe0.tb_lineas l on l.lineaseguroid = p.lineaseguroid
                                    AND l.codplan BETWEEN 2011 AND 2023
     WHERE g.cod_barras = ''N/A''';
  v_index('11') := 170000;
  v_index('12') := 170000;
  v_index('13') := 170000;
  v_index('14') := 170000;
  v_index('15') := 170000;
  v_index('16') := 170000;
  v_index('17') := 170000;
  v_index('18') := 170000;
  v_index('19') := 170000;
  v_index('20') := 170000;
  v_index('21') := 170000;
  v_index('22') := 170000;
  v_index('23') := 170000;
  OPEN c1 FOR v_query;
  LOOP
    FETCH c1
      INTO v_idpoliza, v_plan;
    EXIT WHEN c1%NOTFOUND;
    v_codebar := 'D' || v_plan || LPAD(v_index(v_plan), 6, 0);
    UPDATE o02agpe0.tb_ged_doc_poliza_sbp
      set cod_barras = v_codebar
    WHERE idpoliza_sbp = v_idpoliza;
    v_index(v_plan) := v_index(v_plan) + 1;
  END LOOP;
  CLOSE c1;
  COMMIT;
  update o02agpe0.tb_config_agp
   set agp_valor = (select max(SUBSTR(g.cod_barras, 4, 9))
                      from o02agpe0.tb_ged_doc_poliza_sbp g
                     INNER JOIN o02agpe0.tb_sbp_polizas sbp on sbp.id = g.idpoliza_sbp
                     INNER JOIN o02agpe0.tb_polizas p on p.idpoliza =
                                                         sbp.idpoliza
                     INNER JOIN o02agpe0.tb_lineas l on l.lineaseguroid =
                                                        p.lineaseguroid
                                                    AND l.codplan in
                                                        (2022, 2023))
 where agp_nemo = 'SEQ_BARCODE_D';
END;