insert into o02agpe0.tb_config_agp
values ('NOMB_FIC_COLABS', 'AgroFormacion', 'Nombre del fichero que recibimos de formacion para el cierre de colaboradores');

select * from o02agpe0.tb_config_agp
where agp_nemo like '%NOMB_FIC_COLABS%'


