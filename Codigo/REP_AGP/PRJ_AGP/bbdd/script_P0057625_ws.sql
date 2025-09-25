-- Add/modify columns 
alter table o02agpe0.TB_CORREDURIAS_EXTERNAS add COD_INTERNO varchar2(12);
alter table o02agpe0.TB_CORREDURIAS_EXTERNAS add constraint UQ_CODIGO_INTERNO unique (COD_INTERNO);
  
-- Create table
create table o02agpe0.TB_AUDIT_CONFIRM_EXT 
( 
  id             number(15) not null,
  CODIGO_INTERNO varchar2(12) not null, 
  HORA_LLAMADA   date not null, 
  ENTRADA        clob not null, 
  SALIDA         clob, 
  RESULTADO      number(1) not null, 
  MENSAJE        varchar2(1000), 
  CONSTRAINT PK_TB_AUDIT_CONFIRM_EXT PRIMARY KEY (ID)
);  
create sequence o02agpe0.SQ_AUDIT_CONFIRM_EXT INCREMENT BY 1;