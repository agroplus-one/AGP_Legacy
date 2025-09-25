/**DEFINICIӓN DE INCIO DE LUPAS CON restricctions.in */
/**Convención: - llevan el sufijo IN
 ** 			  - hay que rellenar los arrays arrCampoRestrictions, arrValorRestrictions,
 ** 			    arrOperadorRestrictions y  arrTipoValorRestrictions
 ** PARA UTILIZAR ESTE JS HAY QUE IMPORTAR PREVIAMENTE EL 'lupaGenerica.js'	
 ** Las lupas*.jsp debe de contener los siguientes hidden:
 ** 				<input type="hidden" id="campoRestriccion_ObjetoIN" value="campoBean" />
 **				<input type="hidden" id="valorRestriccion_ObjetoIN" value="${listaDeValoresParaConsultaIN}" />
 **				<input type="hidden" id="operadorRestriccion_ObjetoIN" value="in" />
**/

//INICIO Lupa de Subentidades Mediadoras CM con fecha baja is null 
arrObjetosLupas['SubentidadMediadoraEntidadIN'] = "com.rsi.agp.dao.tables.admin.SubentidadMediadora";
arrCamposFiltrosLupas['SubentidadMediadoraEntidadIN'] = new Array('filtro_codSubentidadMediadoraEntidadIN', 'filtro_SubentidadMediadoraEntidadIN');
arrCamposBeanFiltros['SubentidadMediadoraEntidadIN'] = new Array('id.codsubentidad', 'nomsubentidad');
arrTipoCamposBeanFiltros['SubentidadMediadoraEntidadIN'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['SubentidadMediadoraEntidadIN'] = new Array('C\u00F3d. Ent. Med.', 'Nom. Ent. Mediadora', 'C\u00F3d. Subentidad', 'Nom Subentidad');
arrColumnWidth['SubentidadMediadoraEntidadIN'] = new Array('17%', '33%', '17%', '33%');
arrCamposBean['SubentidadMediadoraEntidadIN'] = new Array('entidadMediadora.codentidad', 'entidadMediadora.nomentidad', 'id.codsubentidad', 'nomSubentidadCompleto');
arrCamposDepende['SubentidadMediadoraEntidadIN'] = new Array('entmediadora_cm', 'entidad_cm', 'subentmediadora_cm');
arrCamposBeanDepende['SubentidadMediadoraEntidadIN'] = new Array('entidadMediadora.codentidad', 'entidad.codentidad', 'id.codsubentidad');
arrTipoBeanDepende['SubentidadMediadoraEntidadIN'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['SubentidadMediadoraEntidadIN'] = new Array('entmediadora_cm', 'subentmediadora_cm', 'desc_subentmediadora_cm');
arrCamposBeanDevolver['SubentidadMediadoraEntidadIN'] = new Array('entidadMediadora.codentidad', 'id.codsubentidad', 'nomSubentidadCompleto');
arrColumnasDistinct['SubentidadMediadoraEntidadIN'] = new Array();
arrColumnasIsNull['SubentidadMediadoraEntidadIN']= new Array('fechabaja');
arrCampoLeftJoin['SubentidadMediadoraEntidadIN']= new Array();
arrCampoRestrictions['SubentidadMediadoraEntidadIN']=new Array('campoRestriccion_EntidadIN');
arrValorRestrictions['SubentidadMediadoraEntidadIN']=new Array('valorRestriccion_EntidadIN');
arrOperadorRestrictions['SubentidadMediadoraEntidadIN']=new Array('operadorRestriccion_EntidadIN');
arrTipoValorRestrictions['SubentidadMediadoraEntidadIN']=new Array('java.math.BigDecimal'); 
arrValorDependeGenerico['SubentidadMediadoraEntidadIN']=new Array();
arrValorDependeOrdenGenerico['SubentidadMediadoraEntidadIN']=new Array();

//INICIO Lupa de Especies
arrObjetosLupas['EspecieIN'] = "com.rsi.agp.dao.tables.cpl.gan.Especie";
arrCamposFiltrosLupas['EspecieIN'] = new Array('filtro_codEspecieIN', 'filtro_EspecieIN');
arrCamposBeanFiltros['EspecieIN'] = new Array('id.codespecie', 'descripcion');
arrTipoCamposBeanFiltros['EspecieIN'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['EspecieIN'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['EspecieIN'] = new Array('30%', '70%');
arrCamposBean['EspecieIN'] = new Array('id.codespecie', 'descripcion');
arrCamposDepende['EspecieIN'] = new Array('lineaseguroid', 'especie');
arrCamposBeanDepende['EspecieIN'] = new Array('id.lineaseguroid', 'id.codespecie');
arrTipoBeanDepende['EspecieIN'] = new Array('java.lang.Long', 'java.lang.Long');
arrCamposDevolver['EspecieIN'] = new Array('especie', 'desc_especie');
arrCamposBeanDevolver['EspecieIN'] = new Array('id.codespecie', 'descripcion');
arrColumnasDistinct['EspecieIN'] = new Array();
arrColumnasIsNull['EspecieIN']= new Array();
arrCampoLeftJoin['EspecieIN']= new Array();
arrCampoRestrictions['EspecieIN']=new Array('campoRestriccion_EspecieIN','campoRestriccion_EspecieNE');
arrValorRestrictions['EspecieIN']=new Array('valorRestriccion_EspecieIN', 'valorRestriccion_EspecieNE');
arrOperadorRestrictions['EspecieIN']=new Array('operadorRestriccion_EspecieIN','operadorRestriccion_EspecieNE');
arrTipoValorRestrictions['EspecieIN']=new Array('java.lang.Long','java.lang.Long'); 
arrValorDependeGenerico['EspecieIN']=new Array();
arrValorDependeOrdenGenerico['EspecieIN']=new Array();
//FIN Lupa de Especies



//INICIO Lupa de Regómenes
arrObjetosLupas['RegimenIN'] = "com.rsi.agp.dao.tables.cpl.gan.RegimenManejo";
arrCamposFiltrosLupas['RegimenIN'] = new Array('filtro_codRegimenIN', 'filtro_RegimenIN');
arrCamposBeanFiltros['RegimenIN'] = new Array('id.codRegimen', 'descripcion');
arrTipoCamposBeanFiltros['RegimenIN'] = new Array('java.lang.Long', 'java.lang.String');
arrTxtCabecerasLupas['RegimenIN'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['RegimenIN'] = new Array('30%', '70%');
arrCamposBean['RegimenIN'] = new Array('id.codRegimen', 'descripcion');
arrCamposDepende['RegimenIN'] = new Array('lineaseguroid', 'regimen');
arrCamposBeanDepende['RegimenIN'] = new Array('id.lineaseguroid', 'id.codRegimen');
arrTipoBeanDepende['RegimenIN'] = new Array('java.lang.Long', 'java.lang.Long');
arrCamposDevolver['RegimenIN'] = new Array('regimen', 'desc_regimen');
arrCamposBeanDevolver['RegimenIN'] = new Array('id.codRegimen', 'descripcion');
arrColumnasDistinct['RegimenIN'] = new Array();
arrColumnasIsNull['RegimenIN']= new Array();
arrCampoLeftJoin['RegimenIN']= new Array();
arrCampoRestrictions['RegimenIN']=new Array('campoRestriccion_RegimenIN', 'campoRestriccion_RegimenNE');
arrValorRestrictions['RegimenIN']=new Array('valorRestriccion_RegimenIN', 'valorRestriccion_RegimenNE');
arrOperadorRestrictions['RegimenIN']=new Array('operadorRestriccion_RegimenIN', 'operadorRestriccion_RegimenNE');
arrTipoValorRestrictions['RegimenIN']=new Array('java.lang.Long', 'java.lang.Long');
arrValorDependeGenerico['RegimenIN']=new Array();
arrValorDependeOrdenGenerico['RegimenIN']=new Array();
// FIN Lupa de Regómenes 

//INICIO Lupa de Grupos de Raza
arrObjetosLupas['GrupoRazaIN'] = "com.rsi.agp.dao.tables.cpl.gan.GruposRazas";
arrCamposFiltrosLupas['GrupoRazaIN'] = new Array('filtro_GrupoRazaIN');
arrCamposBeanFiltros['GrupoRazaIN'] = new Array('desGrupoRaza');
arrTxtCabecerasLupas['GrupoRazaIN'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['GrupoRazaIN'] = new Array('30%', '70%');
arrCamposBean['GrupoRazaIN'] = new Array('id.codGrupoRaza', 'descripcion');
arrCamposDepende['GrupoRazaIN'] = new Array('lineaseguroid', 'codgrupoRaza');
arrCamposBeanDepende['GrupoRazaIN'] = new Array('id.lineaseguroid','id.CodGrupoRaza');
arrTipoBeanDepende['GrupoRazaIN'] = new Array('java.lang.Long','java.lang.Long');
arrCamposDevolver['GrupoRazaIN'] = new Array('codgrupoRaza', 'desGrupoRaza');
arrCamposBeanDevolver['GrupoRazaIN'] = new Array('id.codGrupoRaza', 'descripcion');
arrColumnasDistinct['GrupoRazaIN'] = new Array();
arrColumnasIsNull['GrupoRazaIN']= new Array();
arrCampoLeftJoin['GrupoRazaIN']= new Array();
arrCampoRestrictions['GrupoRazaIN']=new Array('campoRestriccion_GrupoRazaIN', 'campoRestriccion_GrupoRazaNE');
arrValorRestrictions['GrupoRazaIN']=new Array('valorRestriccion_GrupoRazaIN', 'valorRestriccion_GrupoRazaNE');
arrOperadorRestrictions['GrupoRazaIN']=new Array('operadorRestriccion_GrupoRazaIN', 'operadorRestriccion_GrupoRazaNE');
arrTipoValorRestrictions['GrupoRazaIN']=new Array('java.lang.Long', 'java.lang.Long');
arrValorDependeGenerico['GrupoRazaIN']=new Array();
arrValorDependeOrdenGenerico['GrupoRazaIN']=new Array();
// FIN Lupa de Grupos de Raza 

//INICIO Lupa de Tipos de Capital Con Grupo Negocio
arrObjetosLupas['TipoCapitalGrupoNegocioIN'] = "com.rsi.agp.dao.tables.cgen.TipoCapitalConGrupoNegocio";
arrCamposFiltrosLupas['TipoCapitalGrupoNegocioIN'] = new Array('filtro_TipoCapitalGrupoNegocioIN');
arrCamposBeanFiltros['TipoCapitalGrupoNegocioIN'] = new Array('desTipoCapital');
arrTxtCabecerasLupas['TipoCapitalGrupoNegocioIN'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['TipoCapitalGrupoNegocioIN'] = new Array('30%', '70%');
arrCamposBean['TipoCapitalGrupoNegocioIN'] = new Array('codtipocapital', 'destipocapital');
arrCamposDepende['TipoCapitalGrupoNegocioIN'] = new Array('codtipocapital', 'grupoNegocio');
arrCamposBeanDepende['TipoCapitalGrupoNegocioIN'] = new Array('codtipocapital', 'gruposNegocio.grupoNegocio');
arrTipoBeanDepende['TipoCapitalGrupoNegocioIN'] = new Array('java.lang.Long', 'java.lang.String');
arrCamposDevolver['TipoCapitalGrupoNegocioIN'] = new Array('codtipocapital', 'desTipoCapital');
arrCamposBeanDevolver['TipoCapitalGrupoNegocioIN'] = new Array('codtipocapital', 'destipocapital');
arrColumnasDistinct['TipoCapitalGrupoNegocioIN'] = new Array();
arrColumnasIsNull['TipoCapitalGrupoNegocioIN']= new Array();
arrCampoLeftJoin['TipoCapitalGrupoNegocioIN']= new Array();
arrCampoRestrictions['TipoCapitalGrupoNegocioIN']=new Array('campoRestriccion_TipoCapitalGrupoNegocioIN', 'campoRestriccion_TipoCapitalGrupoNegocioNE');
arrValorRestrictions['TipoCapitalGrupoNegocioIN']=new Array('valorRestriccion_TipoCapitalGrupoNegocioIN', 'valorRestriccion_TipoCapitalGrupoNegocioNE');
arrOperadorRestrictions['TipoCapitalGrupoNegocioIN']=new Array('operadorRestriccion_TipoCapitalGrupoNegocioIN', 'operadorRestriccion_TipoCapitalGrupoNegocioNE');
arrTipoValorRestrictions['TipoCapitalGrupoNegocioIN']=new Array('java.lang.Long','java.lang.Long'); 
arrValorDependeGenerico['TipoCapitalGrupoNegocioIN']=new Array();
arrValorDependeOrdenGenerico['TipoCapitalGrupoNegocioIN']=new Array();
// FIN Lupa de Tipos de Capital Con Grupo Negocio 

//INICIO Lupa de Tipos de Animal 
arrObjetosLupas['TiposAnimalGanadoIN'] = "com.rsi.agp.dao.tables.cpl.gan.TiposAnimalGanado";
arrCamposFiltrosLupas['TiposAnimalGanadoIN'] = new Array('filtro_TiposAnimalGanadoIN');
arrCamposBeanFiltros['TiposAnimalGanadoIN'] = new Array('desTipoAnimal');
arrTxtCabecerasLupas['TiposAnimalGanadoIN'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['TiposAnimalGanadoIN'] = new Array('30%', '70%');
arrCamposBean['TiposAnimalGanadoIN'] = new Array('id.codTipoAnimal', 'descripcion');
arrCamposDepende['TiposAnimalGanadoIN'] = new Array('lineaseguroid', 'codtipoanimal');
arrCamposBeanDepende['TiposAnimalGanadoIN'] = new Array('id.lineaseguroid', 'id.codTipoAnimal');
arrTipoBeanDepende['TiposAnimalGanadoIN'] = new Array('java.lang.Long', 'java.lang.Long');
arrCamposDevolver['TiposAnimalGanadoIN'] = new Array('codtipoanimal', 'desTipoAnimal');
arrCamposBeanDevolver['TiposAnimalGanadoIN'] = new Array('id.codTipoAnimal', 'descripcion'); 
arrColumnasDistinct['TiposAnimalGanadoIN'] = new Array();
arrColumnasIsNull['TiposAnimalGanadoIN']= new Array();
arrCampoLeftJoin['TiposAnimalGanadoIN']= new Array();
arrCampoRestrictions['TiposAnimalGanadoIN']=new Array('campoRestriccion_TiposAnimalGanadoIN','campoRestriccion_TiposAnimalGanadoNE');
arrValorRestrictions['TiposAnimalGanadoIN']=new Array('valorRestriccion_TiposAnimalGanadoIN', 'valorRestriccion_TiposAnimalGanadoNE');
arrOperadorRestrictions['TiposAnimalGanadoIN']=new Array('operadorRestriccion_TiposAnimalGanadoIN','operadorRestriccion_TiposAnimalGanadoNE');
arrTipoValorRestrictions['TiposAnimalGanadoIN']=new Array('java.lang.Long', 'java.lang.Long'); 
arrValorDependeGenerico['TiposAnimalGanadoIN']=new Array();
arrValorDependeOrdenGenerico['TiposAnimalGanadoIN']=new Array();
// FIN Lupa de Tipos de Animal


//INICIO Lupa de Provincias
arrObjetosLupas['ProvinciaIN'] = "com.rsi.agp.dao.tables.commons.Provincia";
arrCamposFiltrosLupas['ProvinciaIN'] = new Array('filtro_codProvinciaIN', 'filtro_ProvinciaIN');
arrCamposBeanFiltros['ProvinciaIN'] = new Array('codprovincia', 'nomprovincia');
arrTipoCamposBeanFiltros['ProvinciaIN'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['ProvinciaIN'] = new Array('C\u00F3digo', 'Nombre');
arrColumnWidth['ProvinciaIN'] = new Array('30%', '70%');
arrCamposBean['ProvinciaIN'] = new Array('codprovincia', 'nomprovincia');
arrCamposDepende['ProvinciaIN'] = new Array('provincia');
arrCamposBeanDepende['ProvinciaIN'] = new Array('codprovincia');
arrTipoBeanDepende['ProvinciaIN'] = new Array('java.math.BigDecimal');
arrCamposDevolver['ProvinciaIN'] = new Array('provincia', 'desc_provincia');
arrCamposBeanDevolver['ProvinciaIN'] = new Array('codprovincia', 'nomprovincia');
arrColumnasDistinct['ProvinciaIN'] = new Array();
arrColumnasIsNull['ProvinciaIN']= new Array();
arrCampoLeftJoin['ProvinciaIN']= new Array();
arrCampoRestrictions['ProvinciaIN']=new Array('campoRestriccion_ProvinciaIN', 'campoRestriccion_ProvinciaNE');
arrValorRestrictions['ProvinciaIN']=new Array('valorRestriccion_ProvinciaIN', 'valorRestriccion_ProvinciaNE');
arrOperadorRestrictions['ProvinciaIN']=new Array('operadorRestriccion_ProvinciaIN', 'operadorRestriccion_ProvinciaNE');
arrTipoValorRestrictions['ProvinciaIN']=new Array('java.math.BigDecimal', 'java.math.BigDecimal');
arrValorDependeGenerico['ProvinciaIN']=new Array();
arrValorDependeOrdenGenerico['ProvinciaIN']=new Array();
// FIN Lupa de Provincias



//INICIO Lupa de Comarcas
arrObjetosLupas['ComarcaIN'] = "com.rsi.agp.dao.tables.commons.Comarca";
arrCamposFiltrosLupas['ComarcaIN'] = new Array('filtro_codComarcaIN', 'filtro_ComarcaIN');
arrCamposBeanFiltros['ComarcaIN'] = new Array('id.codcomarca', 'nomcomarca');
arrTipoCamposBeanFiltros['ComarcaIN'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['ComarcaIN'] = new Array('C\u00F3d. Provincia', 'Nom. Provincia', 'C\u00F3d. Comarca', 'Nom. Comarca');
arrColumnWidth['ComarcaIN'] = new Array('20%', '30%', '20%', '30%');
arrCamposBean['ComarcaIN'] = new Array('id.codprovincia', 'provincia.nomprovincia', 'id.codcomarca', 'nomcomarca');
arrCamposDepende['ComarcaIN'] = new Array('provincia', 'comarca');
arrCamposBeanDepende['ComarcaIN'] = new Array('id.codprovincia', 'id.codcomarca');
arrTipoBeanDepende['ComarcaIN'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['ComarcaIN'] = new Array('provincia', 'desc_provincia', 'comarca', 'desc_comarca');
arrCamposBeanDevolver['ComarcaIN'] = new Array('id.codprovincia', 'provincia.nomprovincia', 'id.codcomarca', 'nomcomarca');
arrColumnasDistinct['ComarcaIN'] = new Array();
arrColumnasIsNull['ComarcaIN']= new Array();
arrCampoLeftJoin['ComarcaIN']= new Array();
arrCampoRestrictions['ComarcaIN']=new Array('campoRestriccion_ComarcaIN', 'campoRestriccion_ComarcaINProvincia', 'campoRestriccion_ComarcaNE');
arrValorRestrictions['ComarcaIN']=new Array('valorRestriccion_ComarcaIN', 'valorRestriccion_ProvinciaIN','valorRestriccion_ComarcaNE');
arrOperadorRestrictions['ComarcaIN']=new Array('operadorRestriccion_ComarcaIN', 'operadorRestriccion_ComarcaIN', 'operadorRestriccion_ComarcaNE');
arrTipoValorRestrictions['ComarcaIN']=new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal'); 
arrValorDependeGenerico['ComarcaIN']=new Array();
arrValorDependeOrdenGenerico['ComarcaIN']=new Array();
// FIN Lupa de Comarcas


//INICIO Lupa de Terminos
arrObjetosLupas['TerminoIN'] = "com.rsi.agp.dao.tables.commons.Termino";
arrCamposFiltrosLupas['TerminoIN'] = new Array('filtro_codTerminoIN', 'filtro_TerminoIN');
//arrCamposBeanFiltros['TerminoIN'] = new Array('id.codtermino', 'nomtermino');
arrTipoCamposBeanFiltros['TerminoIN'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['TerminoIN'] = new Array('C\u00F3d. Prov.', 'Nom. Provincia', 'C\u00F3d. Com.', 'Nom. Comarca', 'C\u00F3d. T\u00E9rm.', 'Nom. T\u00E9rmino', 'Subt\u00E9rmino');
arrColumnWidth['TerminoIN'] = new Array('10%', '20%', '10%', '20%', '10%', '20%', '10%');
//arrCamposBean['TerminoIN'] = new Array('id.codprovincia', 'provincia.nomprovincia', 'id.codcomarca', 'comarca.nomcomarca', 'id.codtermino', 'nomtermino', 'id.subtermino');
arrCamposDepende['TerminoIN'] = new Array('provincia', 'comarca', 'termino', 'subtermino');
arrCamposBeanDepende['TerminoIN'] = new Array('id.codprovincia', 'id.codcomarca', 'id.codtermino', 'id.subtermino');
arrTipoBeanDepende['TerminoIN'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.String');
arrCamposDevolver['TerminoIN'] = new Array('provincia', 'desc_provincia', 'comarca', 'desc_comarca', 'termino', 'desc_termino', 'subtermino');
//arrCamposBeanDevolver['TerminoIN'] = new Array('id.codprovincia', 'provincia.nomprovincia', 'id.codcomarca', 'comarca.nomcomarca', 'id.codtermino', 'nomtermino', 'id.subtermino');
arrColumnasDistinct['TerminoIN'] = new Array();
arrColumnasIsNull['TerminoIN']= new Array();
arrCampoLeftJoin['TerminoIN']= new Array();
arrCampoRestrictions['TerminoIN']=new Array('campoRestriccion_TerminoIN', 'campoRestriccion_ComarcaIN', 'campoRestriccion_ComarcaINProvincia', 'campoRestriccion_TerminoNE');
arrValorRestrictions['TerminoIN']=new Array('valorRestriccion_TerminoIN', 'valorRestriccion_ComarcaIN', 'valorRestriccion_ProvinciaIN', 'valorRestriccion_TerminoNE');
arrOperadorRestrictions['TerminoIN']=new Array('operadorRestriccion_TerminoIN', 'operadorRestriccion_TerminoIN', 'operadorRestriccion_TerminoIN','operadorRestriccion_TerminoNE');
arrTipoValorRestrictions['TerminoIN']=new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal'); 
arrValorDependeGenerico['TerminoIN']=new Array();
arrValorDependeOrdenGenerico['TerminoIN']=new Array();
// FIN Lupa de Terminos


//Orógen de datos - Vista Especie (para datos variables asociados a la especie)
arrObjetosLupas['VistaEspecieIN'] = "com.rsi.agp.dao.tables.poliza.explotaciones.oddv.VistaEspecie";
arrCamposFiltrosLupas['VistaEspecieIN'] = new Array('filtro_VistaEspecieIN');
arrCamposBeanFiltros['VistaEspecieIN'] = new Array('id.descripcion');
arrTxtCabecerasLupas['VistaEspecieIN'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaEspecieIN'] = new Array('30%', '70%');
arrCamposBean['VistaEspecieIN'] = new Array('id.codespecie', 'id.descripcion');
arrCamposDepende['VistaEspecieIN'] = new Array('especie', 'lineaseguroid', 'codmodulo', 'provincia', 'comarca', 'termino', 'subtermino');
arrCamposBeanDepende['VistaEspecieIN'] = new Array('id.codespecie', 'id.lineaseguroid', 'id.codmodulo', 'id.codprovincia','id.codcomarca','id.codtermino','id.subtermino');
arrTipoBeanDepende['VistaEspecieIN'] = new Array('java.lang.Integer','java.lang.Long','java.lang.String', 'java.lang.Integer','java.lang.Integer','java.lang.Integer','java.lang.String');
arrCamposDevolver['VistaEspecieIN'] = new Array('especie', 'desc_especie');
arrCamposBeanDevolver['VistaEspecieIN'] = new Array('id.codespecie', 'id.descripcion'); 
arrColumnasDistinct['VistaEspecieIN'] = new Array('id.codespecie', 'id.descripcion');
arrColumnasIsNull['VistaEspecieIN']= new Array();
arrCampoLeftJoin['VistaEspecieIN']= new Array();
arrCampoRestrictions['VistaEspecieIN']=new Array('campoRestriccion_VistaEspecieIN','campoRestriccion_VistaEspecieNE');
arrValorRestrictions['VistaEspecieIN']=new Array('valorRestriccion_VistaEspecieIN', 'valorRestriccion_VistaEspecieNE');
arrOperadorRestrictions['VistaEspecieIN']=new Array('operadorRestriccion_VistaEspecieIN','operadorRestriccion_VistaEspecieNE');
arrTipoValorRestrictions['VistaEspecieIN']=new Array('java.lang.Integer', 'java.lang.Integer'); 
arrValorDependeGenerico['VistaEspecieIN']=new Array('0','0', '0', '99', '99', '999', '9');
arrValorDependeOrdenGenerico['VistaEspecieIN']=new Array('0','0', '0', '4', '3', '2', '1');
arrAcumulaResultados['VistaEspecieIN']=new Array('S');
// FIN DE Lupa VistaEspecie


//Orógen de datos - Vista Rógimen Manjeo (para datos variables asociados al rógimen)
arrObjetosLupas['VistaRegimenIN'] = "com.rsi.agp.dao.tables.poliza.explotaciones.oddv.VistaRegimenManejo";
arrCamposFiltrosLupas['VistaRegimenIN'] = new Array('filtro_VistaRegimenIN');
arrCamposBeanFiltros['VistaRegimenIN'] = new Array('id.descripcion');
arrTxtCabecerasLupas['VistaRegimenIN'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaRegimenIN'] = new Array('30%', '70%');
arrCamposBean['VistaRegimenIN'] = new Array('id.codregimen', 'id.descripcion');
arrCamposDepende['VistaRegimenIN'] = new Array('regimen', 'especie', 'lineaseguroid', 'codmodulo', 'provincia', 'comarca', 'termino', 'subtermino');
arrCamposBeanDepende['VistaRegimenIN'] = new Array('id.codregimen', 'id.codespecie', 'id.lineaseguroid', 'id.codmodulo', 'id.codprovincia','id.codcomarca','id.codtermino','id.subtermino');
arrTipoBeanDepende['VistaRegimenIN'] = new Array('java.lang.Integer','java.lang.Integer', 'java.lang.Long','java.lang.String', 'java.lang.Integer','java.lang.Integer','java.lang.Integer','java.lang.String');
arrCamposDevolver['VistaRegimenIN'] = new Array('regimen', 'desc_regimen');
arrCamposBeanDevolver['VistaRegimenIN'] = new Array('id.codregimen', 'id.descripcion'); 
arrColumnasDistinct['VistaRegimenIN'] = new Array('id.codregimen', 'id.descripcion');
arrColumnasIsNull['VistaRegimenIN']= new Array();
arrCampoLeftJoin['VistaRegimenIN']= new Array();
arrCampoRestrictions['VistaRegimenIN']=new Array('campoRestriccion_VistaRegimenIN','campoRestriccion_VistaRegimenNE');
arrValorRestrictions['VistaRegimenIN']=new Array('valorRestriccion_VistaRegimenIN', 'valorRestriccion_VistaRegimenNE');
arrOperadorRestrictions['VistaRegimenIN']=new Array('operadorRestriccion_VistaRegimenIN','operadorRestriccion_VistaRegimenNE');
arrTipoValorRestrictions['VistaRegimenIN']=new Array('java.lang.Integer', 'java.lang.Integer'); 
arrValorDependeGenerico['VistaRegimenIN']=new Array('0','0', '0','0', '99', '99', '999', '9');
arrValorDependeOrdenGenerico['VistaRegimenIN']=new Array('0','0','0', '0', '4', '3', '2', '1');
arrAcumulaResultados['VistaRegimenIN']=new Array('S');
// FIN DE Lupa VistaRegimen

//Orógen de datos - Vista Grupos Razas (para datos variables asociados al grupo de raza)
arrObjetosLupas['VistaGruposRazasIN'] = "com.rsi.agp.dao.tables.poliza.explotaciones.oddv.VistaGruposRazas";
arrCamposFiltrosLupas['VistaGruposRazasIN'] = new Array('filtro_VistaGruposRazasIN');
arrCamposBeanFiltros['VistaGruposRazasIN'] = new Array('id.descripcion');
arrTxtCabecerasLupas['VistaGruposRazasIN'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaGruposRazasIN'] = new Array('30%', '70%');
arrCamposBean['VistaGruposRazasIN'] = new Array('id.codgruporaza', 'id.descripcion');
arrCamposDepende['VistaGruposRazasIN'] = new Array('codgrupoRaza', 'regimen', 'especie', 'lineaseguroid', 'codmodulo', 'provincia', 'comarca', 'termino', 'subtermino');
arrCamposBeanDepende['VistaGruposRazasIN'] = new Array('id.codgruporaza','id.codregimen', 'id.codespecie', 'id.lineaseguroid', 'id.codmodulo', 'id.codprovincia','id.codcomarca','id.codtermino','id.subtermino');
arrTipoBeanDepende['VistaGruposRazasIN'] = new Array('java.lang.Integer', 'java.lang.Integer','java.lang.Integer', 'java.lang.Long','java.lang.String', 'java.lang.Integer','java.lang.Integer','java.lang.Integer','java.lang.String');
arrCamposDevolver['VistaGruposRazasIN'] = new Array('codgrupoRaza', 'desGrupoRaza');
arrCamposBeanDevolver['VistaGruposRazasIN'] = new Array('id.codgruporaza', 'id.descripcion'); 
arrColumnasDistinct['VistaGruposRazasIN'] = new Array('id.codgruporaza', 'id.descripcion');
arrColumnasIsNull['VistaGruposRazasIN']= new Array();
arrCampoLeftJoin['VistaGruposRazasIN']= new Array();
arrCampoRestrictions['VistaGruposRazasIN']=new Array('campoRestriccion_VistaGruposRazasIN','campoRestriccion_VistaGruposRazasNE');
arrValorRestrictions['VistaGruposRazasIN']=new Array('valorRestriccion_VistaGruposRazasIN', 'valorRestriccion_VistaGruposRazasNE');
arrOperadorRestrictions['VistaGruposRazasIN']=new Array('operadorRestriccion_VistaGruposRazasIN','operadorRestriccion_VistaGruposRazasNE');
arrTipoValorRestrictions['VistaGruposRazasIN']=new Array('java.lang.Integer', 'java.lang.Integer'); 
arrValorDependeGenerico['VistaGruposRazasIN']=new Array('0','0','0', '0','0', '99', '99', '999', '9');
arrValorDependeOrdenGenerico['VistaGruposRazasIN']=new Array('0','0','0','0', '0', '4', '3', '2', '1');
arrAcumulaResultados['VistaGruposRazasIN']=new Array('S');
// FIN DE Lupa VistaGruposRazas

//Orógen de datos - Vista Tipo de Capital por Grupo de Negocio (para datos variables asociados al grupo de raza)
arrObjetosLupas['VistaTipoCapGrupoNegocioIN'] = "com.rsi.agp.dao.tables.poliza.explotaciones.oddv.VistaTipoCapGrupoNegocio";												
arrCamposFiltrosLupas['VistaTipoCapGrupoNegocioIN'] = new Array('filtro_VistaTipoCapGrupoNegocioIN');
arrCamposBeanFiltros['VistaTipoCapGrupoNegocioIN'] = new Array('id.destipocapital');
arrTxtCabecerasLupas['VistaTipoCapGrupoNegocioIN'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaTipoCapGrupoNegocioIN'] = new Array('30%', '70%');
arrCamposBean['VistaTipoCapGrupoNegocioIN'] = new Array('id.codtipocapital', 'id.destipocapital');
arrCamposDepende['VistaTipoCapGrupoNegocioIN'] = new Array('codtipocapital', 'codgrupoRaza', 'regimen', 'especie', 'lineaseguroid', 'codmodulo', 'provincia', 'comarca', 'termino', 'subtermino');
arrCamposBeanDepende['VistaTipoCapGrupoNegocioIN'] = new Array('id.codtipocapital','id.codgruporaza','id.codregimen', 'id.codespecie', 'id.lineaseguroid', 'id.codmodulo', 'id.codprovincia','id.codcomarca','id.codtermino','id.subtermino');
arrTipoBeanDepende['VistaTipoCapGrupoNegocioIN'] = new Array('java.lang.Integer','java.lang.Integer', 'java.lang.Integer','java.lang.Integer', 'java.lang.Long','java.lang.String', 'java.lang.Integer','java.lang.Integer','java.lang.Integer','java.lang.String');
arrCamposDevolver['VistaTipoCapGrupoNegocioIN'] = new Array('codtipocapital', 'desTipoCapital');
arrCamposBeanDevolver['VistaTipoCapGrupoNegocioIN'] = new Array('id.codtipocapital', 'id.destipocapital'); 
arrColumnasDistinct['VistaTipoCapGrupoNegocioIN'] = new Array('id.codtipocapital', 'id.destipocapital');
arrColumnasIsNull['VistaTipoCapGrupoNegocioIN']= new Array();
arrCampoLeftJoin['VistaTipoCapGrupoNegocioIN']= new Array();
arrCampoRestrictions['VistaTipoCapGrupoNegocioIN']=new Array('campoRestriccion_VistaTipoCapGrupoNegocioIN','campoRestriccion_VistaTipoCapGrupoNegocioNE');
arrValorRestrictions['VistaTipoCapGrupoNegocioIN']=new Array('valorRestriccion_VistaTipoCapGrupoNegocioIN', 'valorRestriccion_VistaTipoCapGrupoNegocioNE');
arrOperadorRestrictions['VistaTipoCapGrupoNegocioIN']=new Array('operadorRestriccion_VistaTipoCapGrupoNegocioIN','operadorRestriccion_VistaTipoCapGrupoNegocioNE');
arrTipoValorRestrictions['VistaTipoCapGrupoNegocioIN']=new Array('java.lang.Integer', 'java.lang.Integer'); 
arrValorDependeGenerico['VistaTipoCapGrupoNegocioIN']=new Array('0','0','0','0', '0','0', '99', '99', '999', '9');
arrValorDependeOrdenGenerico['VistaTipoCapGrupoNegocioIN']=new Array('0','0','0','0','0', '0', '4', '3', '2', '1');
arrAcumulaResultados['VistaTipoCapGrupoNegocioIN']=new Array('S');
// FIN DE Lupa Vista Tipo de Capital por Grupo de Negocio


//Orógen de datos - Vista Tipo de Animal (para datos variables asociados al tipo de animal)
arrObjetosLupas['VistaTiposAnimalIN'] = "com.rsi.agp.dao.tables.poliza.explotaciones.oddv.VistaTiposAnimal";												
arrCamposFiltrosLupas['VistaTiposAnimalIN'] = new Array('filtro_VistaTiposAnimalIN');
arrCamposBeanFiltros['VistaTiposAnimalIN'] = new Array('id.descripcion');
arrTxtCabecerasLupas['VistaTiposAnimalIN'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaTiposAnimalIN'] = new Array('30%', '70%');
arrCamposBean['VistaTiposAnimalIN'] = new Array('id.codtipoanimal', 'id.descripcion');
arrCamposDepende['VistaTiposAnimalIN'] = new Array('codtipoanimal','codtipocapital', 'codgrupoRaza', 'regimen', 'especie', 'lineaseguroid', 'codmodulo', 'provincia', 'comarca', 'termino', 'subtermino');
arrCamposBeanDepende['VistaTiposAnimalIN'] = new Array('id.codtipoanimal', 'id.codtipocapital','id.codgruporaza','id.codregimen', 'id.codespecie', 'id.lineaseguroid', 'id.codmodulo', 'id.codprovincia','id.codcomarca','id.codtermino','id.subtermino');
arrTipoBeanDepende['VistaTiposAnimalIN'] = new Array('java.lang.Integer','java.lang.Integer','java.lang.Integer', 'java.lang.Integer','java.lang.Integer', 'java.lang.Long','java.lang.String', 'java.lang.Integer','java.lang.Integer','java.lang.Integer','java.lang.String');
arrCamposDevolver['VistaTiposAnimalIN'] = new Array('codtipoanimal', 'desTipoAnimal');
arrCamposBeanDevolver['VistaTiposAnimalIN'] = new Array('id.codtipoanimal', 'id.descripcion'); 
arrColumnasDistinct['VistaTiposAnimalIN'] = new Array('id.codtipoanimal', 'id.descripcion');
arrColumnasIsNull['VistaTiposAnimalIN']= new Array();
arrCampoLeftJoin['VistaTiposAnimalIN']= new Array();
arrCampoRestrictions['VistaTiposAnimalIN']=new Array('campoRestriccion_VistaTiposAnimalIN','campoRestriccion_VistaTiposAnimalNE');
arrValorRestrictions['VistaTiposAnimalIN']=new Array('valorRestriccion_VistaTiposAnimalIN', 'valorRestriccion_VistaTiposAnimalNE');
arrOperadorRestrictions['VistaTiposAnimalIN']=new Array('operadorRestriccion_VistaTiposAnimalIN','operadorRestriccion_VistaTiposAnimalNE');
arrTipoValorRestrictions['VistaTiposAnimalIN']=new Array('java.lang.Integer', 'java.lang.Integer'); 
arrValorDependeGenerico['VistaTiposAnimalIN']=new Array('0','0','0','0','0', '0','0', '99', '99', '999', '9');
arrValorDependeOrdenGenerico['VistaTiposAnimalIN']=new Array('0','0','0','0','0','0', '0', '4', '3', '2', '1');
arrAcumulaResultados['VistaTiposAnimalIN']=new Array('S');
// FIN DE Lupa Vista Tipo de Animal

//INICIO Lupa de Tipo de Capital de Factores segun codconcepto 126
arrObjetosLupas['FactoresTipoCapitalIN'] = "com.rsi.agp.dao.tables.orgDat.VistaPorFactores";
arrCamposFiltrosLupas['FactoresTipoCapitalIN'] = new Array('filtro_FactoresTipoCapitalIN');
arrCamposBeanFiltros['FactoresTipoCapitalIN'] = new Array('id.descripcion');
arrTxtCabecerasLupas['FactoresTipoCapitalIN'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['FactoresTipoCapitalIN'] = new Array('20%', '80%');
arrCamposBean['FactoresTipoCapitalIN'] = new Array('id.codvalor', 'id.descripcion');
arrCamposDepende['FactoresTipoCapitalIN'] = new Array('capital', 'codplan', 'codlinea', 'codconcepto');
arrCamposBeanDepende['FactoresTipoCapitalIN'] = new Array('id.codvalor','linea.codplan', 'linea.codlinea', 'id.codconcepto'); 		
arrTipoBeanDepende['FactoresTipoCapitalIN'] = new Array('java.math.BigDecimal','java.math.BigDecimal','java.math.BigDecimal','java.math.BigDecimal');
arrCamposDevolver['FactoresTipoCapitalIN'] = new Array('capital', 'desc_capital');
arrCamposBeanDevolver['FactoresTipoCapitalIN'] = new Array('id.codvalor', 'id.descripcion');
arrColumnasDistinct['FactoresTipoCapitalIN'] = new Array('id.codvalor', 'id.descripcion');
arrColumnasIsNull['FactoresTipoCapitalIN'] = new Array();
arrCampoLeftJoin['FactoresTipoCapitalIN'] = new Array();
arrCampoRestrictions['FactoresTipoCapitalIN'] = new Array('campoRestriccion_FactoresTipoCapitalIN', 'campoRestriccion_FactoresTipoCapitalIN2', 'campoRestriccion_FactoresTipoCapitalLT', 'campoRestriccion_FactoresTipoCapitalGE');
arrValorRestrictions['FactoresTipoCapitalIN'] = new Array('valorRestriccion_FactoresTipoCapitalIN', 'valorRestriccion_FactoresTipoCapitalIN2', 'valorRestriccion_FactoresTipoCapitalLT', 'valorRestriccion_FactoresTipoCapitalGE');
arrOperadorRestrictions['FactoresTipoCapitalIN'] = new Array('operadorRestriccion_FactoresTipoCapitalIN', 'operadorRestriccion_FactoresTipoCapitalIN2', 'operadorRestriccion_FactoresTipoCapitalLT', 'operadorRestriccion_FactoresTipoCapitalGE');
arrTipoValorRestrictions['FactoresTipoCapitalIN'] = new Array('java.lang.String', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal'); 
arrValorDependeGenerico['FactoresTipoCapitalIN'] = new Array();
arrValorDependeOrdenGenerico['FactoresTipoCapitalIN'] = new Array();

// FIN Lupa de Factores segun codconcepto

//INICIO Lupa de Cultivos
arrObjetosLupas['CultivoIN'] = "com.rsi.agp.dao.tables.cpl.Cultivo";
arrCamposFiltrosLupas['CultivoIN'] = new Array('filtro_codCultivoIN', 'filtro_CultivoIN');
arrCamposBeanFiltros['CultivoIN'] = new Array('id.codcultivo', 'descultivo');
arrTipoCamposBeanFiltros['CultivoIN'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['CultivoIN'] = new Array('C\u00F3digo', 'Nombre');
arrColumnWidth['CultivoIN'] = new Array('30%', '70%');
arrCamposBean['CultivoIN'] = new Array('id.codcultivo', 'descultivo');
arrCamposDepende['CultivoIN'] = new Array('lineaseguroid', 'cultivo');
arrCamposBeanDepende['CultivoIN'] = new Array('id.lineaseguroid', 'id.codcultivo');
arrTipoBeanDepende['CultivoIN'] = new Array('java.lang.Long', 'java.math.BigDecimal');
arrCamposDevolver['CultivoIN'] = new Array('cultivo', 'desc_cultivo');
arrCamposBeanDevolver['CultivoIN'] = new Array('id.codcultivo', 'descultivo');
arrColumnasDistinct['CultivoIN'] = new Array();
arrColumnasIsNull['CultivoIN']= new Array();
arrCampoLeftJoin['CultivoIN']= new Array();
arrCampoRestrictions['CultivoIN']=new Array('campoRestriccion_CultivoIN', 'campoRestriccion_CultivoNE');
arrValorRestrictions['CultivoIN']=new Array('valorRestriccion_CultivoIN', 'valorRestriccion_CultivoNE');
arrOperadorRestrictions['CultivoIN']=new Array('operadorRestriccion_CultivoIN', 'operadorRestriccion_CultivoNE');
arrTipoValorRestrictions['CultivoIN']=new Array('java.math.BigDecimal', 'java.math.BigDecimal');
arrValorDependeGenerico['CultivoIN']=new Array();
arrValorDependeOrdenGenerico['CultivoIN']=new Array();
// FIN Lupa de Cultivos

//INICIO Lupa de Variedades
arrObjetosLupas['VariedadIN'] = "com.rsi.agp.dao.tables.cpl.Variedad";
arrCamposFiltrosLupas['VariedadIN'] = new Array('filtro_codVariedadIN', 'filtro_VariedadIN');
arrCamposBeanFiltros['VariedadIN'] = new Array('id.codvariedad', 'desvariedad');
arrTipoCamposBeanFiltros['VariedadIN'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['VariedadIN'] = new Array('C\u00F3d. Cultivo', 'Nom. Cultivo', 'C\u00F3d. Variedad', 'Nom. Variedad');
arrColumnWidth['VariedadIN'] = new Array('20%', '30%', '20%', '30%');
arrCamposBean['VariedadIN'] = new Array('id.codcultivo', 'cultivo.descultivo', 'id.codvariedad', 'desvariedad');
arrCamposDepende['VariedadIN'] = new Array('lineaseguroid', 'cultivo', 'variedad');
arrCamposBeanDepende['VariedadIN'] = new Array('id.lineaseguroid', 'id.codcultivo', 'id.codvariedad');
arrTipoBeanDepende['VariedadIN'] = new Array('java.lang.Long', 'java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['VariedadIN'] = new Array('cultivo', 'desc_cultivo', 'variedad', 'desc_variedad');
arrCamposBeanDevolver['VariedadIN'] = new Array('id.codcultivo', 'cultivo.descultivo', 'id.codvariedad', 'desvariedad');
arrColumnasDistinct['VariedadIN'] = new Array();
arrColumnasIsNull['VariedadIN']= new Array();
arrCampoLeftJoin['VariedadIN']= new Array();
arrCampoRestrictions['VariedadIN']=new Array('campoRestriccion_VariedadIN', 'campoRestriccion_VariedadNE');
arrValorRestrictions['VariedadIN']=new Array('valorRestriccion_VariedadIN', 'valorRestriccion_VariedadNE');
arrOperadorRestrictions['VariedadIN']=new Array('operadorRestriccion_VariedadIN', 'operadorRestriccion_VariedadNE');
arrTipoValorRestrictions['VariedadIN']=new Array('java.math.BigDecimal', 'java.math.BigDecimal');
arrValorDependeGenerico['VariedadIN']=new Array();
arrValorDependeOrdenGenerico['VariedadIN']=new Array();
// FIN Lupa de Variedades

////Origen de datos - Sistema de produccion Cambio Masivo de parcelas
arrObjetosLupas['VistaSistemaProduccionCM_IN'] = "com.rsi.agp.dao.tables.orgDat.VistaSistemaProduccion";
arrCamposFiltrosLupas['VistaSistemaProduccionCM_IN'] = new Array('filtro_VistaSistemaProduccionCM_IN');
arrCamposBeanFiltros['VistaSistemaProduccionCM_IN'] = new Array('id.dessistemaproduccion');
arrTxtCabecerasLupas['VistaSistemaProduccionCM_IN'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaSistemaProduccionCM_IN'] = new Array('30%', '70%');
arrCamposBean['VistaSistemaProduccionCM_IN'] = new Array('id.codsistemaproduccion', 'id.dessistemaproduccion');
arrCamposDepende['VistaSistemaProduccionCM_IN'] = new Array('lineaseguroid', 'cultivo_cm', 'variedad_cm', 'provincia_cm', 'comarca_cm', 'termino_cm', 'subtermino_cm');
arrCamposBeanDepende['VistaSistemaProduccionCM_IN'] = new Array('id.lineaseguroid','id.codcultivo', 'id.codvariedad', 'codprovincia', 'codcomarca', 'codtermino', 'subtermino' );
arrTipoBeanDepende['VistaSistemaProduccionCM_IN'] = new Array('java.lang.Long', 'java.math.BigDecimal' , 'java.math.BigDecimal','java.math.BigDecimal','java.math.BigDecimal','java.math.BigDecimal','java.lang.String');
arrCamposDevolver['VistaSistemaProduccionCM_IN'] = new Array('sistemaProduccion_cm', 'desc_sistemaProduccion_cm');
arrCamposBeanDevolver['VistaSistemaProduccionCM_IN'] = new Array('id.codsistemaproduccion', 'id.dessistemaproduccion'); 
arrColumnasDistinct['VistaSistemaProduccionCM_IN'] = new Array('id.codsistemaproduccion', 'id.dessistemaproduccion');
arrColumnasIsNull['VistaSistemaProduccionCM_IN'] = new Array();
arrCampoLeftJoin['VistaSistemaProduccionCM_IN'] = new Array();
arrCampoRestrictions['VistaSistemaProduccionCM_IN'] = new Array('campoRestriccion_VistaSistemaProduccionCM_IN');
arrValorRestrictions['VistaSistemaProduccionCM_IN'] = new Array('valorRestriccion_VistaSistemaProduccionCM_IN');
arrOperadorRestrictions['VistaSistemaProduccionCM_IN'] = new Array('operadorRestriccion_VistaSistemaProduccionCM_IN');
arrTipoValorRestrictions['VistaSistemaProduccionCM_IN'] = new Array('java.lang.String'); 
arrValorDependeGenerico['VistaSistemaProduccionCM_IN'] = new Array();
arrValorDependeOrdenGenerico['VistaSistemaProduccionCM_IN'] = new Array();
arrValorDependeGenerico['VistaSistemaProduccionCM_IN'] = new Array();
arrValorDependeOrdenGenerico['VistaSistemaProduccionCM_IN'] = new Array();