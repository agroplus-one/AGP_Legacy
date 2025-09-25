-- Add/modify columns 
alter table TB_COMS_RENOV_ESMED add CODMODULO varchar2(5) default 1 not null;
-- Add comments to the columns 
comment on column TB_COMS_RENOV_ESMED.CODMODULO
  is 'Codigo de modulo';
