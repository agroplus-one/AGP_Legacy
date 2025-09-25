SET DEFINE OFF; 
 
SET SERVEROUTPUT ON; 
 
-- Create table 
create table o02agpe0.TB_SW_MODULOS_COBERTURAS_PARC 
( 
  ID            NUMBER(15) not null, 
  IDPOLIZA      NUMBER(15) not null, 
  IDPARCELA     NUMBER(15), 
  CODMODULO     VARCHAR2(5) not null, 
  ENVIO         CLOB not null, 
  RESPUESTA     CLOB not null, 
  USUARIO       VARCHAR2(8) not null, 
  FECHA         DATE not null 
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
comment on table o02agpe0.TB_SW_MODULOS_COBERTURAS_PARC 
  is 'Almacena la comunicacion con el m?todo modulosCoberturas del SW ContratacionAyudas'; 
-- Add comments to the columns  
comment on column o02agpe0.TB_SW_MODULOS_COBERTURAS_PARC.ID 
  is 'Identificador del registro'; 
comment on column o02agpe0.TB_SW_MODULOS_COBERTURAS_PARC.IDPOLIZA 
  is 'Identificador de la p?liza asociada'; 
comment on column o02agpe0.TB_SW_MODULOS_COBERTURAS_PARC.IDPARCELA 
  is 'Identificador de la parcela asociada a la llamada'; 
comment on column o02agpe0.TB_SW_MODULOS_COBERTURAS_PARC.CODMODULO 
  is 'M?dulo asociado a la llamamada'; 
comment on column o02agpe0.TB_SW_MODULOS_COBERTURAS_PARC.ENVIO 
  is 'XML de llamada al servicio'; 
comment on column o02agpe0.TB_SW_MODULOS_COBERTURAS_PARC.RESPUESTA 
  is 'XML de respuesta del servicio'; 
comment on column o02agpe0.TB_SW_MODULOS_COBERTURAS_PARC.USUARIO 
  is 'Usuario que ejecuta la llamada al servicio'; 
comment on column o02agpe0.TB_SW_MODULOS_COBERTURAS_PARC.FECHA 
  is 'Fecha en la que se ejecuta la llamada al servicio'; 
-- Create/Recreate primary, unique and foreign key constraints  
alter table o02agpe0.TB_SW_MODULOS_COBERTURAS_PARC 
  add constraint PK_SW_MODULOS_COBERTURAS_PARC primary key (ID) 
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
alter table o02agpe0.TB_SW_MODULOS_COBERTURAS_PARC 
  add constraint FK_TB_SW_MOD_COB_PARC_IDPARC foreign key (IDPARCELA) 
  references o02agpe0.TB_PARCELAS (IDPARCELA) on delete cascade; 
alter table o02agpe0.TB_SW_MODULOS_COBERTURAS_PARC 
  add constraint FK_TB_SW_MOD_COB_PARC_IDPLZ foreign key (IDPOLIZA) 
  references o02agpe0.TB_POLIZAS (IDPOLIZA) on delete cascade; 
-- Grant/Revoke object privileges  
grant select on o02agpe0.TB_SW_MODULOS_COBERTURAS_PARC to ROLE_O02AGPE0_SEL; 
grant select, insert, update, delete on o02agpe0.TB_SW_MODULOS_COBERTURAS_PARC to ROLE_O02AGPE0_UPD; 
 
/  
show errors;