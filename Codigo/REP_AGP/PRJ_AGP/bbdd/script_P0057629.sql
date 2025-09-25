-- Create table
create table O02AGPE0.TB_CVS_CARGAS
(
  ID             NUMBER(15) not null,
  PLAN           NUMBER(4) not null,
  LINEA          NUMBER(3) not null,
  ENTIDAD        NUMBER(4) not null,
  ENT_MED        NUMBER(4) not null,
  SUBENT_MED     NUMBER(4) not null,
  NOMBRE_FICHERO VARCHAR2(255) not null,
  USUARIO        VARCHAR2(8) not null,
  FECHA_CARGA    DATE not null
);
-- Add comments to the table 
comment on table O02AGPE0.TB_CVS_CARGAS
  is 'Almacena la informacion de los ficheros de CVS cargados en el sistema';
-- Add comments to the columns 
comment on column O02AGPE0.TB_CVS_CARGAS.ID
  is 'Identificador de la carga del fichero';
comment on column O02AGPE0.TB_CVS_CARGAS.PLAN
  is 'Plan asociado al fichero';
comment on column O02AGPE0.TB_CVS_CARGAS.LINEA
  is 'Linea asociada al fichero';
comment on column O02AGPE0.TB_CVS_CARGAS.ENTIDAD
  is 'Entidad asociada al fichero';
comment on column O02AGPE0.TB_CVS_CARGAS.ENT_MED
  is 'Codigo de entidad mediadora asociada a la carga';
comment on column O02AGPE0.TB_CVS_CARGAS.SUBENT_MED
  is 'Codigo de subentidad mediadora asociada a la carga';
comment on column O02AGPE0.TB_CVS_CARGAS.NOMBRE_FICHERO
  is 'Nombre del fichero cargado';
comment on column O02AGPE0.TB_CVS_CARGAS.USUARIO
  is 'Usuario que carga el fichero';
comment on column O02AGPE0.TB_CVS_CARGAS.FECHA_CARGA
  is 'Fecha de carga del fichero';
-- Create/Recreate primary, unique and foreign key constraints 
alter table O02AGPE0.TB_CVS_CARGAS
  add constraint PK_CVS_CARGAS primary key (ID)
  using index ;
-- Grant/Revoke object privileges 
grant select on O02AGPE0.TB_CVS_CARGAS to ROLE_O02AGPE0_SEL;
grant select, insert, update, delete on O02AGPE0.TB_CVS_CARGAS to ROLE_O02AGPE0_UPD;

-- Create table
create table O02AGPE0.TB_CVS_CARGAS_FICHERO
(
  ID_CARGA NUMBER(15) not null,
  FICHERO  CLOB not null
);
-- Add comments to the table 
comment on table O02AGPE0.TB_CVS_CARGAS_FICHERO
  is 'Almacena el contenido de los ficheros cargados';
-- Add comments to the columns 
comment on column O02AGPE0.TB_CVS_CARGAS_FICHERO.ID_CARGA
  is 'Identificador de la carga a la que esta asociado el fichero';
comment on column O02AGPE0.TB_CVS_CARGAS_FICHERO.FICHERO
  is 'Contenido del fichero';
-- Create/Recreate primary, unique and foreign key constraints 
alter table O02AGPE0.TB_CVS_CARGAS_FICHERO
  add constraint PK_CVS_CARGAS_FICHERO primary key (ID_CARGA)
  using index ;
alter table O02AGPE0.TB_CVS_CARGAS_FICHERO
  add constraint FK_CVS_CARGAS_FICHERO foreign key (ID_CARGA)
  references O02AGPE0.TB_CVS_CARGAS (ID) on delete cascade;
-- Grant/Revoke object privileges 
grant select on O02AGPE0.TB_CVS_CARGAS_FICHERO to ROLE_O02AGPE0_SEL;
grant select, insert, update, delete on O02AGPE0.TB_CVS_CARGAS_FICHERO to ROLE_O02AGPE0_UPD;

-- Create table
create table O02AGPE0.TB_CVS_ASEGURADOS
(
  ID            NUMBER(15) not null,
  ID_CARGA      NUMBER(15) not null,
  NIF_ASEGURADO VARCHAR2(9) not null,
  DISCRIMINANTE VARCHAR2(1)
);
-- Add comments to the table 
comment on table O02AGPE0.TB_CVS_ASEGURADOS
  is 'Almacena los asegurados asociados a una carga de fichero de CVS';
-- Add comments to the columns 
comment on column O02AGPE0.TB_CVS_ASEGURADOS.ID
  is 'Identificador del asegurado';
comment on column O02AGPE0.TB_CVS_ASEGURADOS.ID_CARGA
  is 'Identificador de la carga a la que esta asociada el asegurado';
comment on column O02AGPE0.TB_CVS_ASEGURADOS.NIF_ASEGURADO
  is 'Nif del asegurado';
comment on column O02AGPE0.TB_CVS_ASEGURADOS.DISCRIMINANTE
  is 'Discriminante del asegurado';
-- Create/Recreate primary, unique and foreign key constraints 
alter table O02AGPE0.TB_CVS_ASEGURADOS
  add constraint PK_CVS_ASEGURADOS primary key (ID)
  using index;
alter table O02AGPE0.TB_CVS_ASEGURADOS
  add constraint FK_CVS_ASEGURADOS foreign key (ID_CARGA)
  references O02AGPE0.TB_CVS_CARGAS (ID) on delete cascade;
-- Create/Recreate indexes 
create index O02AGPE0.IX_CVS_ASEGURADOS on O02AGPE0.TB_CVS_ASEGURADOS (ID_CARGA);
-- Grant/Revoke object privileges 
grant select on O02AGPE0.TB_CVS_ASEGURADOS to ROLE_O02AGPE0_SEL;
grant select, insert, update, delete on O02AGPE0.TB_CVS_ASEGURADOS to ROLE_O02AGPE0_UPD;

-- Create table
create table O02AGPE0.TB_CVS_PARCELAS
(
  ID               NUMBER(15) not null,
  ID_CVS_ASEG      NUMBER(15) not null,
  PROVINCIA        NUMBER(2),
  COMARCA          NUMBER(2),
  TERMINO          NUMBER(3),
  SUBTERMINO       VARCHAR2(1),
  NOMBRE           VARCHAR2(15),
  NUMERO           NUMBER(3),
  CULTIVO          NUMBER(3),
  VARIEDAD         NUMBER(3),
  OPCION           VARCHAR2(1),
  AJUSTE           NUMBER(4),
  CODORG           NUMBER(2),
  PROVINCIA_SIGPAC NUMBER(2),
  TERMINO_SIGPAC   NUMBER(3),
  AGREGADO_SIGPAC  NUMBER(3),
  ZONA_SIGPAC      NUMBER(2),
  POLIGONO_SIGPAC  NUMBER(3),
  PARCELA_SIGPAC   NUMBER(5),
  RECINTO_SIGPAC   NUMBER(5),
  R_CUB_ELEG       VARCHAR2(1)
);
-- Add comments to the table 
comment on table O02AGPE0.TB_CVS_PARCELAS
  is 'Almacena las parcelas asociadas a un registro de CVS de asegurado';
-- Add comments to the columns 
comment on column O02AGPE0.TB_CVS_PARCELAS.ID
  is 'Identificador del registro';
comment on column O02AGPE0.TB_CVS_PARCELAS.ID_CVS_ASEG
  is 'Identificador del registro de CVS de asegurado asociado';
comment on column O02AGPE0.TB_CVS_PARCELAS.PROVINCIA
  is 'Codigo de provincia de la parcela';
comment on column O02AGPE0.TB_CVS_PARCELAS.COMARCA
  is 'Codigo de comarca de la parcela';
comment on column O02AGPE0.TB_CVS_PARCELAS.TERMINO
  is 'Codigo de termino de la parcela';
comment on column O02AGPE0.TB_CVS_PARCELAS.SUBTERMINO
  is 'Codigo de subtermino  de la parcela';
comment on column O02AGPE0.TB_CVS_PARCELAS.NOMBRE
  is 'Nombre de la parcela';
comment on column O02AGPE0.TB_CVS_PARCELAS.NUMERO
  is 'Numero de la parcela';  
comment on column O02AGPE0.TB_CVS_PARCELAS.CULTIVO
  is 'Codigo de cultivo de la parcela';
comment on column O02AGPE0.TB_CVS_PARCELAS.VARIEDAD
  is 'Codigo de variedad de la parcela';
comment on column O02AGPE0.TB_CVS_PARCELAS.OPCION
  is 'OpciÃ³n';
comment on column O02AGPE0.TB_CVS_PARCELAS.AJUSTE
  is 'Codigo de ajuste';
comment on column O02AGPE0.TB_CVS_PARCELAS.CODORG
  is 'CODORG';
comment on column O02AGPE0.TB_CVS_PARCELAS.PROVINCIA_SIGPAC
  is 'Codigo de provincia SIGPAC de la parcela';
comment on column O02AGPE0.TB_CVS_PARCELAS.TERMINO_SIGPAC
  is 'Codigo de termino SIGPAC de la parcela';
comment on column O02AGPE0.TB_CVS_PARCELAS.AGREGADO_SIGPAC
  is 'Codigo de agregado SIGPAC de la parcela';
comment on column O02AGPE0.TB_CVS_PARCELAS.ZONA_SIGPAC
  is 'Codigo de zona SIGPAC de la parcela';
comment on column O02AGPE0.TB_CVS_PARCELAS.POLIGONO_SIGPAC
  is 'Codigo de poligono SIGPAC de la parcela';
comment on column O02AGPE0.TB_CVS_PARCELAS.PARCELA_SIGPAC
  is 'Codigo de parcela SIGPAC de la parcela';
comment on column O02AGPE0.TB_CVS_PARCELAS.RECINTO_SIGPAC
  is 'Codigo de recinto SIGPAC de la parcela';
comment on column O02AGPE0.TB_CVS_PARCELAS.R_CUB_ELEG
  is 'Indicador de riesgo cubierto elegido';
-- Create/Recreate primary, unique and foreign key constraints 
alter table O02AGPE0.TB_CVS_PARCELAS
  add constraint PK_CVS_PARCELAS primary key (ID)
  using index ;
alter table O02AGPE0.TB_CVS_PARCELAS
  add constraint FK_CVS_PARCELAS foreign key (ID_CVS_ASEG)
  references O02AGPE0.TB_CVS_ASEGURADOS (ID) on delete cascade;
-- Create/Recreate indexes 
create index O02AGPE0.IX_CVS_PARCELAS on O02AGPE0.TB_CVS_PARCELAS (ID_CVS_ASEG);
-- Grant/Revoke object privileges 
grant select on O02AGPE0.TB_CVS_PARCELAS to ROLE_O02AGPE0_SEL;
grant select, insert, update, delete on O02AGPE0.TB_CVS_PARCELAS to ROLE_O02AGPE0_UPD;

-- Create table
create table O02AGPE0.TB_CVS_CAPITALES_ASEGURADOS
(
  ID_CVS_PARCELA NUMBER(15) not null,
  ID_CVS_ASEG    NUMBER(15) not null,
  SUPERFICIE     NUMBER(8,2),
  PRODUCCION     NUMBER(10),
  PRECIO         NUMBER(10,4),
  CODTIPOCAPITAL NUMBER(3)
);
-- Add comments to the table 
comment on table O02AGPE0.TB_CVS_CAPITALES_ASEGURADOS
  is 'Almacena los capitales asegurados asociados a un registro de parcela de CVS';
-- Add comments to the columns 
comment on column O02AGPE0.TB_CVS_CAPITALES_ASEGURADOS.ID_CVS_PARCELA
  is 'Identificador de la parcela de la CVS asociada';
comment on column O02AGPE0.TB_CVS_CAPITALES_ASEGURADOS.ID_CVS_ASEG
  is 'Identificador del registro de CVS de asegurado asociado';
comment on column O02AGPE0.TB_CVS_CAPITALES_ASEGURADOS.SUPERFICIE
  is 'Numero de hectareas de la parcela';
comment on column O02AGPE0.TB_CVS_CAPITALES_ASEGURADOS.PRODUCCION
  is 'Produccion del capital asegurado';
comment on column O02AGPE0.TB_CVS_CAPITALES_ASEGURADOS.PRECIO
  is 'Precio del capital asegurado';  
comment on column O02AGPE0.TB_CVS_CAPITALES_ASEGURADOS.CODTIPOCAPITAL
  is 'Tipo Capital del capital asegurado';
-- Create/Recreate primary, unique and foreign key constraints 
alter table O02AGPE0.TB_CVS_CAPITALES_ASEGURADOS
  add constraint PK_CVS_CAPITALES_ASEGURADOS primary key (ID_CVS_PARCELA, ID_CVS_ASEG, CODTIPOCAPITAL)
  using index;
alter table O02AGPE0.TB_CVS_CAPITALES_ASEGURADOS
  add constraint FK_CVS_CAP_ASEG_ASEG foreign key (ID_CVS_ASEG)
  references O02AGPE0.TB_CVS_ASEGURADOS (ID) on delete cascade;
alter table O02AGPE0.TB_CVS_CAPITALES_ASEGURADOS
  add constraint FK_CVS_CAP_ASEG_PARC foreign key (ID_CVS_PARCELA)
  references O02AGPE0.TB_CVS_PARCELAS (ID) on delete cascade;
-- Create/Recreate indexes 
create index O02AGPE0.IX_CVS_CA_ASEG on O02AGPE0.TB_CVS_CAPITALES_ASEGURADOS (ID_CVS_ASEG);
create index O02AGPE0.IX_CVS_CA_PARCELA on O02AGPE0.TB_CVS_CAPITALES_ASEGURADOS (ID_CVS_PARCELA);
-- Grant/Revoke object privileges 
grant select on O02AGPE0.TB_CVS_CAPITALES_ASEGURADOS to ROLE_O02AGPE0_SEL;
grant select, insert, update, delete on O02AGPE0.TB_CVS_CAPITALES_ASEGURADOS to ROLE_O02AGPE0_UPD;

-- Create table
create table O02AGPE0.TB_CVS_DATOS_VARIABLES
(
  ID_CVS_PARCELA NUMBER(15) not null,
  ID_CVS_ASEG    NUMBER(15) not null,
  CODTIPOCAPITAL NUMBER(3) not null,
  CODCONCEPTO    NUMBER(3) not null,
  VALOR          VARCHAR2(30)
);
-- Add comments to the table 
comment on table O02AGPE0.TB_CVS_DATOS_VARIABLES
  is 'Almacena los datos variables asociados a un registro de capital asegurado de CVS';
-- Add comments to the columns 
comment on column O02AGPE0.TB_CVS_DATOS_VARIABLES.ID_CVS_PARCELA
  is 'Identificador del registro de parcela de CVS asociado';
comment on column O02AGPE0.TB_CVS_DATOS_VARIABLES.ID_CVS_ASEG
  is 'Identificador del registro de CVS de asegurado asociado';
comment on column O02AGPE0.TB_CVS_DATOS_VARIABLES.CODCONCEPTO
  is 'Codigo de concepto correspondiente al dato variable';
comment on column O02AGPE0.TB_CVS_DATOS_VARIABLES.VALOR
  is 'Valor del dato variable';
 comment on column O02AGPE0.TB_CVS_DATOS_VARIABLES.CODTIPOCAPITAL
  is 'Tipo Capital del capital asegurado asociado';
-- Create/Recreate primary, unique and foreign key constraints 
alter table O02AGPE0.TB_CVS_DATOS_VARIABLES
  add constraint PK_CVS_DATOS_VARIABLES primary key (ID_CVS_PARCELA, ID_CVS_ASEG, CODTIPOCAPITAL, CODCONCEPTO)
  using index;
alter table O02AGPE0.TB_CVS_DATOS_VARIABLES
  add constraint FK_CVS_DATOS_VAR_CAP_ASEG foreign key (ID_CVS_PARCELA, ID_CVS_ASEG, CODTIPOCAPITAL)
  references O02AGPE0.tb_cvs_capitales_asegurados (ID_CVS_PARCELA, ID_CVS_ASEG, CODTIPOCAPITAL) on delete cascade;  

-- Grant/Revoke object privileges 
grant select on O02AGPE0.TB_CVS_DATOS_VARIABLES to ROLE_O02AGPE0_SEL;
grant select, insert, update, delete on O02AGPE0.TB_CVS_DATOS_VARIABLES to ROLE_O02AGPE0_UPD;

create sequence O02AGPE0.SQ_CVS_CARGAS
minvalue 1
maxvalue 999999999999999
start with 1
increment by 1;

create sequence O02AGPE0.SQ_CVS_PARCELAS
minvalue 1
maxvalue 999999999999999
start with 1
increment by 1;

-- Create sequence 
create sequence O02AGPE0.SQ_CVS_ASEGURADOS
minvalue 1
maxvalue 999999999999999
start with 1
increment by 1;