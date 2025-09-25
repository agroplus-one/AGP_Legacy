
--- PASO 1 -- HACER EL UPDATE DE LA TABLA TB_DOC_AGROSEGURO... incluyendo los nuevos campos
--- =======================================================================================

-- Add/modify columns 
alter table O02AGPE0.TB_DOC_AGROSEGURO modify NOMBRE VARCHAR2(256);
alter table O02AGPE0.TB_DOC_AGROSEGURO modify DESCRIPCION VARCHAR2(255);
alter table O02AGPE0.TB_DOC_AGROSEGURO add CODPLAN NUMBER(4) default 9999 not null;
alter table O02AGPE0.TB_DOC_AGROSEGURO add CODLINEA NUMBER(3);
alter table O02AGPE0.TB_DOC_AGROSEGURO add CODENTIDAD NUMBER(4);
alter table O02AGPE0.TB_DOC_AGROSEGURO add FECHA_VALIDEZ date;
alter table O02AGPE0.TB_DOC_AGROSEGURO add CODUSUARIO VARCHAR2(8);
alter table O02AGPE0.TB_DOC_AGROSEGURO add FECHA_ACT date;
-- Add comments to the columns 
comment on column O02AGPE0.TB_DOC_AGROSEGURO.CODPLAN
  is 'Numero de plan (Se permite un plan Genérico)';
comment on column O02AGPE0.TB_DOC_AGROSEGURO.CODLINEA
  is 'Codigo de Línea (Se permite una línea genérica e incluso blanco)';
comment on column O02AGPE0.TB_DOC_AGROSEGURO.CODENTIDAD
  is 'Entidad asociada al documento (Se permite vacía y se considerará todas)';
comment on column O02AGPE0.TB_DOC_AGROSEGURO.FECHA_ACT
  is 'Fecha en la que se realiza la modificación/alta del registro';
comment on column O02AGPE0.TB_DOC_AGROSEGURO.CODUSUARIO
  is 'Usuario que ha realizado la modificación/alta del registro';
comment on column O02AGPE0.TB_DOC_AGROSEGURO.FECHA_VALIDEZ
  is 'Fecha Validez del documento';

 
--- PASO 2 -- HACER EL UPDATE DE LOS REGISTROS DE LA TABLA PARA DAR VALOR AL PLAN Y LA LÍNEA
--- =======================================================================================
UPDATE (select doc.lineaseguroid,
               doc.codplan plan_doc, 
               doc.codlinea linea_doc, 
               lin.codlinea linea_lin, 
               lin.codplan plan_lin
               FROM o02agpe0.tb_doc_agroseguro doc, 
                    o02agpe0.tb_lineas lin 
               WHERE doc.lineaseguroid = lin.lineaseguroid) 
SET plan_doc = plan_lin,
    linea_doc = linea_lin
    
   
CREATE INDEX O02AGPE0.IX_DOC_AGRO_LINEASEGUROID ON O02AGPE0.TB_DOC_AGROSEGURO (CODPLAN, CODLINEA) TABLESPACE 
AGPIX001;
