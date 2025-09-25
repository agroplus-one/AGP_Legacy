-- Create table
create table o02agpe0.TB_ESTADOS_AGROSEGURO
(
  IDESTADO    NUMBER(3) not null,
  DESC_ESTADO VARCHAR2(30),
  ABREVIATURA VARCHAR2(4)
);
-- Add comments to the table 
comment on table o02agpe0.TB_ESTADOS_AGROSEGURO
  is 'Tabla que almacena los posibles estados de una póliza en Agroseguro';
-- Add comments to the columns 
comment on column o02agpe0.TB_ESTADOS_AGROSEGURO.IDESTADO
  is 'Identificador del Estado';
comment on column o02agpe0.TB_ESTADOS_AGROSEGURO.DESC_ESTADO
  is 'Descripción del estado';
comment on column o02agpe0.TB_ESTADOS_AGROSEGURO.ABREVIATURA
  is 'Abreviatura del estado';
-- Create/Recreate primary, unique and foreign key constraints 
alter table o02agpe0.TB_ESTADOS_AGROSEGURO
  add constraint PK_ESTADOS_AGROSEGURO primary key (IDESTADO)
  using index;
  
-- Create table
create table o02agpe0.TB_RELACION_ESTADOS
(
  IDESTADO_AGRO    NUMBER(3) not null,
  IDESTADO_POL	   NUMBER(15) not null,
  IDESTADO_RENOV   NUMBER(3) not null
); 
-- Create/Recreate primary, unique and foreign key constraints 
alter table o02agpe0.TB_RELACION_ESTADOS
  add constraint PK_RELACION_ESTADOS primary key (IDESTADO_AGRO, IDESTADO_POL, IDESTADO_RENOV);
alter table o02agpe0.TB_RELACION_ESTADOS
  add constraint FK_REL_ESTADOS_AGRO foreign key (IDESTADO_AGRO)
  references o02agpe0.tb_estados_agroseguro (IDESTADO);
alter table o02agpe0.TB_RELACION_ESTADOS
  add constraint FK_REL_ESTADOS_RENOV foreign key (IDESTADO_RENOV)
  references o02agpe0.tb_estado_renovacion_agroseg (CODIGO);
alter table o02agpe0.TB_RELACION_ESTADOS
  add constraint FK_REL_ESTADOS_POLIZA foreign key (IDESTADO_POL)
  references o02agpe0.TB_ESTADOS_POLIZA (IDESTADO);  
  
-- Add/modify columns 
alter table o02agpe0.TB_POLIZAS add IDESTADO_AGRO number(3);
alter table o02agpe0.TB_POLIZAS add FECHA_SEGUIMIENTO date;
alter table o02agpe0.TB_POLIZAS add COSTE_TOMADOR_AGRO number(11,2);
-- Add comments to the columns 
comment on column o02agpe0.TB_POLIZAS.IDESTADO_AGRO
  is 'Estado de la póliza en Agroseguro';
comment on column o02agpe0.TB_POLIZAS.FECHA_SEGUIMIENTO
  is 'Fecha en la que se modifica el estado Agroseguro';
comment on column o02agpe0.TB_POLIZAS.COSTE_TOMADOR_AGRO
  is 'Coste tomador de la póliza en Agroseguro';

-- Create/Recreate primary, unique and foreign key constraints 
alter table o02agpe0.TB_POLIZAS
  add constraint FK_ESTADOS_AGRO foreign key (IDESTADO_AGRO)
  references o02agpe0.tb_estados_agroseguro (IDESTADO);  
  
-- Add/modify columns 
alter table o02agpe0.tb_inc_incidencias add FECHA_SEGUIMIENTO date;  
  
-- Add/modify columns 
alter table o02agpe0.tb_anexo_mod add IDESTADO_AGRO varchar2(1);
alter table o02agpe0.tb_anexo_mod add FECHA_SEGUIMIENTO date;  

-- Create/Recreate primary, unique and foreign key constraints 
alter table o02agpe0.tb_anexo_mod
  add constraint FK_AM_ESTADOS_AGRO foreign key (IDESTADO_AGRO)
  references o02agpe0.TB_INC_ESTADOS (CODESTADO);   
  
alter table o02agpe0.TB_ANEXO_MOD_HISTORICO_ESTADOS add ESTADO_AGRO varchar2(1);  
  
-- Create table
create table o02agpe0.TB_TMP_BATCH_SEGUIMIENTO
(
  TIPO           number(1) not null,
  CIF_TOMADOR    varchar2(10),
  PLAN           number(4),
  LINEA          number(3),
  ASEGURADO      varchar2(10),
  ENTIDAD        number(4),
  OFICINA        varchar2(4),
  DETALLE        varchar2(255),
  REFERENCIA     varchar2(7),
  TIPOREFERENCIA varchar2(1),
  ESTADO         varchar2(3)
);

-- Add comments to the columns 
comment on column o02agpe0.TB_TMP_BATCH_SEGUIMIENTO.TIPO
  is '1-poliza, 2-anexo/incidencia, 3-póliza no encontrada, 4-póliza suspensión garantías';
comment on column o02agpe0.TB_TMP_BATCH_SEGUIMIENTO.ID_TOMADOR
  is 'Identificador del tomador';
comment on column o02agpe0.TB_TMP_BATCH_SEGUIMIENTO.PLAN
  is 'Código del plan';
comment on column o02agpe0.TB_TMP_BATCH_SEGUIMIENTO.LINEA
  is 'Código de línea';
comment on column o02agpe0.TB_TMP_BATCH_SEGUIMIENTO.ASEGURADO
  is 'NIF/CIF/NIE del asegurado';
comment on column o02agpe0.TB_TMP_BATCH_SEGUIMIENTO.ENTIDAD
  is 'Código de la entidad';
comment on column o02agpe0.TB_TMP_BATCH_SEGUIMIENTO.OFICINA
  is 'Código de oficina';  
comment on column o02agpe0.TB_TMP_BATCH_SEGUIMIENTO.REFERENCIA
  is 'Referencia de la póliza';    
comment on column o02agpe0.TB_TMP_BATCH_SEGUIMIENTO.TIPOREFERENCIA
  is 'Tipo Referencia de la póliza';      
comment on column o02agpe0.TB_TMP_BATCH_SEGUIMIENTO.ESTADO
  is 'Estado del elemento';
  
-- Create table
create table o02agpe0.TB_CORREOS_TOMADORES
(
  CIF    varchar2(10) not null,
  FECHA  date not null,
  CUERPO varchar2(4000) not null
);

-- Create sequence 
create sequence o02agpe0.SQ_SW_SEGUIMIENTO_CONTR
minvalue 1
maxvalue 999999999999999
start with 1
increment by 1;

-- Create table
create table o02agpe0.TB_SW_SEGUIMIENTO_CONTR
(
  ID          number(15) not null,
  REFERENCIA  varchar2(7),
  PLAN        number(4),
  FECHA_DESDE date not null,
  FECHA_HASTA date not null,
  USUARIO     varchar2(8) not null,
  XML         clob not null,
  FECHA_PET   date not null
)
;
-- Create/Recreate primary, unique and foreign key constraints 
alter table o02agpe0.TB_SW_SEGUIMIENTO_CONTR
  add constraint o02agpe0.PK_SW_SEGUIMIENTO_CONTR primary key (ID);
  
  
create or replace view o02agpe0.vw_inc_incidencias_agro as
select inc.idincidencia,
       inc.anhoincidencia as anho,
       inc.numincidencia as numero,
       ias.codasunto,
       ias.descripcion as asunto,
       inc.codestado,
       decode(inc.codestado, '0', 'Enviada Errónea',
                             '1', 'Enviada Correcta',
                             '9', 'Borrador') estadodes,
       inc.fechaestado as fecha,
       idoc.coddocafectado,
       idoc.descripcion as docafectado,
       inc.tiporef,
       inc.idenvio,
       q.codentidad,
       q.oficina,
       q.entmediadora,
       q.subentmediadora,
       q.delegacion,
       q.codusuario,
       inc.codplan,
       inc.codlinea,
       inc.codestadoagro,
       inc.nifaseg as nifcif,
       e.descripcion as estadoagrodes,
       inc.fechaestadoagro,
       inc.fecha_seguimiento,
       decode(inc.tipoalta, 'c', inc.idenvio,
                            'a', '',
                            'p', '',
                            'i', '') idcupon,
       q.referencia
  from o02agpe0.tb_inc_incidencias inc
 inner join o02agpe0.tb_inc_asuntos ias on ias.codasunto = inc.codasunto
 inner join o02agpe0.tb_inc_docs_afectados idoc on inc.coddocafectado =
                                                   idoc.coddocafectado
 inner join o02agpe0.tb_inc_estados e on e.codestado = inc.codestadoagro
  left outer join (select l.codlinea,
                          l.codplan,
                          p.referencia,
                          p.tiporef,
                          p.dc,
                          c.codentidad,
                          c.entmediadora,
                          c.subentmediadora,
                          p.oficina,
                          a.nifcif,
                          u.delegacion,
                          p.codusuario,
                          p.idpoliza
                     from o02agpe0.tb_polizas    p,
                          o02agpe0.tb_lineas     l,
                          o02agpe0.tb_asegurados a,
                          o02agpe0.tb_colectivos c,
                          o02agpe0.tb_usuarios   u
                    where p.lineaseguroid = l.lineaseguroid
                      and p.idasegurado = a.id
                      and c.id = p.idcolectivo
                      and l.lineaseguroid = c.lineaseguroid
                      and u.codusuario = p.codusuario) q on q.codlinea =
                                                            inc.codlinea
                                                        and q.codplan =
                                                            inc.codplan
                                                        and q.referencia =
                                                            inc.referencia
                                                        and q.tiporef =
                                                            inc.tiporef
                                                        and q.dc = inc.dc;