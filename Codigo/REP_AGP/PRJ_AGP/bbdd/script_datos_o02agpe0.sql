insert into o02agpe0.tb_config_agp
values ('CANCEL_IRIS_AGRO', '0', 'Número de Cancelaciones de Agroseguro enviadas para procesar por IRIS');

insert into o02agpe0.tb_config_agp
values ('CANCEL_IRIS_AGRO_RECHZ', '0', 'Número de Cancelaciones de Agroseguro Rechazadas por IRIS');

insert into o02agpe0.tb_config_agp
values ('CANCEL_IRIS_AGRO_OK', '0', 'Número de Cancelaciones de Agroseguro Aceptadas por IRIS');

select * from o02agpe0.tb_config_agp
where agp_nemo like 'CANCEL_IRIS%'