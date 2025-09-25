SET DEFINE OFF; 
SET SERVEROUTPUT ON; 
 
-- Create table 
create table o02agpe0.TB_SW_MODULOS_COB_PARC_ANEXO 
( 
  ID               NUMBER(15) not null, 
  IDANEXO          NUMBER(15) not null, 
  ID_PARCELA_ANEXO NUMBER(15), 
  CODMODULO        VARCHAR2(5) not null, 
  ENVIO            CLOB not null, 
  RESPUESTA        CLOB not null, 
  USUARIO          VARCHAR2(8) not null, 
  FECHA            DATE not null 
) 
tablespace AGPTS001 
  pctfree 10 
  initrans 1 
  maxtrans 255 
  storage 
  ( 
    initial 64K 
    next 1M 
    minextents 1 
    maxextents unlimited 
  ); 
-- Add comments to the table  
comment on table o02agpe0.TB_SW_MODULOS_COB_PARC_ANEXO 
  is 'Almacena la comunicacion con el metodo modulosCoberturas del SW ContratacionAyudas'; 
-- Add comments to the columns  
comment on column o02agpe0.TB_SW_MODULOS_COB_PARC_ANEXO.ID 
  is 'Identificador del registro'; 
comment on column o02agpe0.TB_SW_MODULOS_COB_PARC_ANEXO.IDANEXO 
  is 'Identificador del anexo asociado'; 
comment on column o02agpe0.TB_SW_MODULOS_COB_PARC_ANEXO.ID_PARCELA_ANEXO 
  is 'Identificador de la parcela asociada a la llamada'; 
comment on column o02agpe0.TB_SW_MODULOS_COB_PARC_ANEXO.CODMODULO 
  is 'M¢dulo asociado a la llamada'; 
comment on column o02agpe0.TB_SW_MODULOS_COB_PARC_ANEXO.ENVIO 
  is 'XML de llamada al servicio'; 
comment on column o02agpe0.TB_SW_MODULOS_COB_PARC_ANEXO.RESPUESTA 
  is 'XML de respuesta del servicio'; 
comment on column o02agpe0.TB_SW_MODULOS_COB_PARC_ANEXO.USUARIO 
  is 'Usuario que ejecuta la llamada al servicio'; 
comment on column o02agpe0.TB_SW_MODULOS_COB_PARC_ANEXO.FECHA 
  is 'Fecha en la que se ejecuta la llamada al servicio'; 
-- Create/Recreate primary, unique and foreign key constraints  
alter table o02agpe0.TB_SW_MODULOS_COB_PARC_ANEXO 
  add constraint PK_SW_MODULOS_COB_PARC_ANEXO primary key (ID) 
  using index  
  tablespace AGPIX001 
  pctfree 10 
  initrans 2 
  maxtrans 255 
  storage 
  ( 
    initial 64K 
    next 1M 
    minextents 1 
    maxextents unlimited 
  ); 
alter table o02agpe0.TB_SW_MODULOS_COB_PARC_ANEXO 
  add constraint FK_TB_SW_M_COB_PAR_IDANE foreign key (IDANEXO) 
  references TB_ANEXO_MOD (ID) on delete cascade; 
alter table o02agpe0.TB_SW_MODULOS_COB_PARC_ANEXO 
  add constraint FK_TB_SW_M_COB_PARC_IDPARC_ANE foreign key (ID_PARCELA_ANEXO) 
  references TB_ANEXO_MOD_PARCELAS (ID) on delete cascade; 
-- Grant/Revoke object privileges  
grant select on o02agpe0.TB_SW_MODULOS_COB_PARC_ANEXO to ROLE_O02AGPE0_SEL; 
grant select, insert, update, delete on o02agpe0.TB_SW_MODULOS_COB_PARC_ANEXO to ROLE_O02AGPE0_UPD; 
 
/  
show errors;