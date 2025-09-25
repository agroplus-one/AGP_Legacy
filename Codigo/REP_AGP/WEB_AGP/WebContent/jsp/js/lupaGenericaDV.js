/**
 * 
 *  DEFINICION DE INICIO DE LUPAS PARA DATOS VARIABLES
 * 
 **/

//INICIO Lupa de Denominacion Origen 
arrObjetosLupas['VistaDenomOrigen'] = "com.rsi.agp.dao.tables.orgDat.VistaDenomOrigen";
arrCamposFiltrosLupas['VistaDenomOrigen'] = new Array('filtro_VistaDenomOrigen');
arrCamposBeanFiltros['VistaDenomOrigen'] = new Array('id.desdenomorigenigp');
arrTxtCabecerasLupas['VistaDenomOrigen'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaDenomOrigen'] = new Array('30%', '70%');
arrCamposBean['VistaDenomOrigen'] = new Array('id.coddenomorigenigp', 'id.desdenomorigenigp');
arrCamposDepende['VistaDenomOrigen'] = new Array('cod_cpto_107', 'lineaseguroid', 'cultivo', 'variedad', 'capital', 'provincia', 'comarca', 'termino', 'subtermino');
arrCamposBeanDepende['VistaDenomOrigen'] = new Array('id.coddenomorigenigp', 'id.lineaseguroid', 'id.codcultivo', 'id.codvariedad', 'codtipocapital', 'codprovincia', 'codcomarca', 'codtermino', 'subtermino');
arrTipoBeanDepende['VistaDenomOrigen'] = new Array('java.math.BigDecimal', 'java.lang.Long', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.String');
arrCamposDevolver['VistaDenomOrigen'] = new Array('cod_cpto_107', 'des_cpto_107');
arrCamposBeanDevolver['VistaDenomOrigen'] = new Array('id.coddenomorigenigp', 'id.desdenomorigenigp');
arrColumnasDistinct['VistaDenomOrigen'] = new Array('id.coddenomorigenigp', 'id.desdenomorigenigp');
arrColumnasIsNull['VistaDenomOrigen'] = new Array();
arrCampoLeftJoin['VistaDenomOrigen'] = new Array();
arrCampoRestrictions['VistaDenomOrigen'] = new Array('campoRestriccion_VistaDenomOrigen');
arrValorRestrictions['VistaDenomOrigen'] = new Array('valorRestriccion_VistaDenomOrigen');
arrOperadorRestrictions['VistaDenomOrigen'] = new Array('operadorRestriccion_VistaDenomOrigen');
arrTipoValorRestrictions['VistaDenomOrigen'] = new Array('java.lang.String'); 
arrValorDependeGenerico['VistaDenomOrigen'] = new Array('0', '0', '0', '0', '0', '99', '99', '999', '9');
arrValorDependeOrdenGenerico['VistaDenomOrigen'] = new Array('0', '0', '0', '0', '0', '4', '3', '2', '1');


//INICIO Lupa de Sistema Conduccion
arrObjetosLupas['SistemaConduccion'] = "com.rsi.agp.dao.tables.cgen.SistemaConduccion";
arrCamposFiltrosLupas['SistemaConduccion'] = new Array('filtro_SistemaConduccion');
arrCamposBeanFiltros['SistemaConduccion'] = new Array('dessistemaconduccion');
arrTxtCabecerasLupas['SistemaConduccion'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['SistemaConduccion'] = new Array('30%', '70%');
arrCamposBean['SistemaConduccion'] = new Array('codsistemaconduccion', 'dessistemaconduccion');
arrCamposDepende['SistemaConduccion'] = new Array('cod_cpto_131');
arrCamposBeanDepende['SistemaConduccion'] = new Array('codsistemaconduccion');
arrTipoBeanDepende['SistemaConduccion'] = new Array('java.math.BigDecimal');
arrCamposDevolver['SistemaConduccion'] = new Array('cod_cpto_131', 'des_cpto_131');
arrCamposBeanDevolver['SistemaConduccion'] = new Array('codsistemaconduccion', 'dessistemaconduccion'); 
arrColumnasDistinct['SistemaConduccion'] = new Array();
arrColumnasIsNull['SistemaConduccion'] = new Array();
arrCampoLeftJoin['SistemaConduccion'] = new Array();
arrCampoRestrictions['SistemaConduccion'] = new Array();
arrValorRestrictions['SistemaConduccion'] = new Array();
arrOperadorRestrictions['SistemaConduccion'] = new Array();
arrTipoValorRestrictions['SistemaConduccion'] = new Array(); 
arrValorDependeGenerico['SistemaConduccion'] = new Array();
arrValorDependeOrdenGenerico['SistemaConduccion'] = new Array();


//INICIO Lupa de Practica Cultural
arrObjetosLupas['VistaPracticaCultural'] = "com.rsi.agp.dao.tables.orgDat.VistaPracticaCultural";
arrCamposFiltrosLupas['VistaPracticaCultural'] = new Array('filtro_VistaPracticaCultural');
arrCamposBeanFiltros['VistaPracticaCultural'] = new Array('id.despracticacultural');
arrTxtCabecerasLupas['VistaPracticaCultural'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaPracticaCultural'] = new Array('30%', '70%');
arrCamposBean['VistaPracticaCultural'] = new Array('id.codpracticacultural', 'id.despracticacultural');
arrCamposDepende['VistaPracticaCultural'] = new Array('cod_cpto_133', 'lineaseguroid', 'cultivo', 'variedad', 'capital', 'provincia', 'comarca', 'termino', 'subtermino');
arrCamposBeanDepende['VistaPracticaCultural'] = new Array('id.codpracticacultural', 'id.lineaseguroid', 'id.codcultivo', 'id.codvariedad', 'codtipocapital', 'codprovincia', 'codcomarca', 'codtermino', 'subtermino');
arrTipoBeanDepende['VistaPracticaCultural'] = new Array('java.math.BigDecimal', 'java.lang.Long', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.String');
arrCamposDevolver['VistaPracticaCultural'] = new Array('cod_cpto_133', 'des_cpto_133');
arrCamposBeanDevolver['VistaPracticaCultural'] = new Array('id.codpracticacultural', 'id.despracticacultural');
arrColumnasDistinct['VistaPracticaCultural'] = new Array('id.codpracticacultural', 'id.despracticacultural');
arrColumnasIsNull['VistaPracticaCultural'] = new Array();
arrCampoLeftJoin['VistaPracticaCultural'] = new Array();
arrCampoRestrictions['VistaPracticaCultural'] = new Array('campoRestriccion_VistaPracticaCultural');
arrValorRestrictions['VistaPracticaCultural'] = new Array('valorRestriccion_VistaPracticaCultural');
arrOperadorRestrictions['VistaPracticaCultural'] = new Array('operadorRestriccion_VistaPracticaCultural');
arrTipoValorRestrictions['VistaPracticaCultural'] = new Array('java.lang.String'); 
arrValorDependeGenerico['VistaPracticaCultural'] = new Array('0', '0', '0', '0', '0', '99', '99', '999', '9');
arrValorDependeOrdenGenerico['VistaPracticaCultural'] = new Array('0', '0', '0', '0', '0', '4', '3', '2', '1');


//INICIO Lupa de Practica Cultural 305
arrObjetosLupas['VistaPracticaCultural305'] = "com.rsi.agp.dao.tables.orgDat.VistaPracticaCultural305";
arrCamposFiltrosLupas['VistaPracticaCultural305'] = new Array('filtro_VistaPracticaCultural305');
arrCamposBeanFiltros['VistaPracticaCultural305'] = new Array('id.despracticacultural');
arrTxtCabecerasLupas['VistaPracticaCultural305'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaPracticaCultural305'] = new Array('30%', '70%');
arrCamposBean['VistaPracticaCultural305'] = new Array('id.codpracticacultural', 'id.despracticacultural');
arrCamposDepende['VistaPracticaCultural305'] = new Array('cod_cpto_133', 'lineaseguroid', 'cultivo', 'variedad', 'capital', 'provincia', 'comarca', 'termino', 'subtermino');
arrCamposBeanDepende['VistaPracticaCultural305'] = new Array('id.codpracticacultural', 'id.lineaseguroid', 'id.codcultivo', 'id.codvariedad', 'codtipocapital', 'codprovincia', 'codcomarca', 'codtermino', 'subtermino');
arrTipoBeanDepende['VistaPracticaCultural305'] = new Array('java.math.BigDecimal', 'java.lang.Long', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.String');
arrCamposDevolver['VistaPracticaCultural305'] = new Array('cod_cpto_133', 'des_cpto_133');
arrCamposBeanDevolver['VistaPracticaCultural305'] = new Array('id.codpracticacultural', 'id.despracticacultural');
arrColumnasDistinct['VistaPracticaCultural305'] = new Array('id.codpracticacultural', 'id.despracticacultural');
arrColumnasIsNull['VistaPracticaCultural305'] = new Array();
arrCampoLeftJoin['VistaPracticaCultural305'] = new Array();
arrCampoRestrictions['VistaPracticaCultural305'] = new Array('campoRestriccion_VistaPracticaCultural305');
arrValorRestrictions['VistaPracticaCultural305'] = new Array('valorRestriccion_VistaPracticaCultural305');
arrOperadorRestrictions['VistaPracticaCultural305'] = new Array('operadorRestriccion_VistaPracticaCultural305');
arrTipoValorRestrictions['VistaPracticaCultural305'] = new Array('java.lang.String'); 
arrValorDependeGenerico['VistaPracticaCultural305'] = new Array('0', '0', '0', '0', '0', '99', '99', '999', '9');
arrValorDependeOrdenGenerico['VistaPracticaCultural305'] = new Array('0', '0', '0', '0', '0', '4', '3', '2', '1');


//INICIO Lupa de Vista por Factores
arrObjetosLupas['VistaPorFactores'] = "com.rsi.agp.dao.tables.orgDat.VistaPorFactores";
arrCamposFiltrosLupas['VistaPorFactores'] = new Array('filtro_VistaPorFactores');
arrCamposBeanFiltros['VistaPorFactores'] = new Array('id.descripcion');
arrTxtCabecerasLupas['VistaPorFactores'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaPorFactores'] = new Array('30%', '70%');
arrCamposBean['VistaPorFactores'] = new Array('id.codvalor', 'id.descripcion');
arrCamposDepende['VistaPorFactores'] = new Array('cod_cpto_lupa_factores', 'valor_lupa_factores', 'lineaseguroid', 'codModuloPrecio');
arrCamposBeanDepende['VistaPorFactores'] = new Array('id.codconcepto', 'id.codvalor', 'id.lineaseguroid', 'id.codmodulo');
arrTipoBeanDepende['VistaPorFactores'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.String');
arrCamposDevolver['VistaPorFactores'] = new Array('valor_lupa_factores', 'desc_lupa_factores');
arrCamposBeanDevolver['VistaPorFactores'] = new Array('id.codvalor', 'id.descripcion'); 
arrColumnasDistinct['VistaPorFactores'] = new Array('id.codvalor', 'id.descripcion');
arrColumnasIsNull['VistaPorFactores'] = new Array();
arrCampoLeftJoin['VistaPorFactores'] = new Array();
arrCampoRestrictions['VistaPorFactores'] = new Array();
arrValorRestrictions['VistaPorFactores'] = new Array();
arrOperadorRestrictions['VistaPorFactores'] = new Array();
arrTipoValorRestrictions['VistaPorFactores'] = new Array();
arrValorDependeGenerico['VistaPorFactores'] = new Array();
arrValorDependeOrdenGenerico['VistaPorFactores'] = new Array();


//INICIO Lupa de Numero Años Desde Poda
arrObjetosLupas['NumAniosPoda'] = "com.rsi.agp.dao.tables.cgen.NumAniosDesdePoda";
arrCamposFiltrosLupas['NumAniosPoda'] = new Array('filtro_NumAniosPoda');
arrCamposBeanFiltros['NumAniosPoda'] = new Array('desnumaniospoda');
arrTxtCabecerasLupas['NumAniosPoda'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['NumAniosPoda'] = new Array('30%', '70%');
arrCamposBean['NumAniosPoda'] = new Array('codnumaniospoda', 'desnumaniospoda');
arrCamposDepende['NumAniosPoda'] = new Array('cod_cpto_617');
arrCamposBeanDepende['NumAniosPoda'] = new Array('codnumaniospoda');
arrTipoBeanDepende['NumAniosPoda'] = new Array('java.math.BigDecimal');
arrCamposDevolver['NumAniosPoda'] = new Array('cod_cpto_617', 'des_cpto_617');
arrCamposBeanDevolver['NumAniosPoda'] = new Array('codnumaniospoda', 'desnumaniospoda'); 
arrColumnasDistinct['NumAniosPoda'] = new Array();
arrColumnasIsNull['NumAniosPoda'] = new Array();
arrCampoLeftJoin['NumAniosPoda'] = new Array();
arrCampoRestrictions['NumAniosPoda'] = new Array();
arrValorRestrictions['NumAniosPoda'] = new Array();
arrOperadorRestrictions['NumAniosPoda'] = new Array();
arrTipoValorRestrictions['NumAniosPoda'] = new Array(); 
arrValorDependeGenerico['NumAniosPoda'] = new Array();
arrValorDependeOrdenGenerico['NumAniosPoda'] = new Array();


//INICIO Lupa de Ciclo Cultivo
arrObjetosLupas['VistaCicloCultivo'] = "com.rsi.agp.dao.tables.orgDat.VistaCicloCultivo";
arrCamposFiltrosLupas['VistaCicloCultivo'] = new Array('filtro_VistaCicloCultivo');
arrCamposBeanFiltros['VistaCicloCultivo'] = new Array('id.desciclocultivo');
arrTxtCabecerasLupas['VistaCicloCultivo'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaCicloCultivo'] = new Array('30%', '70%');
arrCamposBean['VistaCicloCultivo'] = new Array('id.codciclocultivo', 'id.desciclocultivo');
arrCamposDepende['VistaCicloCultivo'] = new Array('cod_cpto_618', 'lineaseguroid', 'cultivo', 'variedad', 'provincia', 'comarca', 'termino');
arrCamposBeanDepende['VistaCicloCultivo'] = new Array('id.codciclocultivo', 'id.lineaseguroid', 'id.codcultivo', 'id.codvariedad', 'codprovincia', 'codcomarca', 'codtermino');
arrTipoBeanDepende['VistaCicloCultivo'] = new Array('java.math.BigDecimal', 'java.lang.Long', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['VistaCicloCultivo'] = new Array('cod_cpto_618', 'des_cpto_618');
arrCamposBeanDevolver['VistaCicloCultivo'] = new Array('id.codciclocultivo', 'id.desciclocultivo');
arrColumnasDistinct['VistaCicloCultivo'] = new Array('id.codciclocultivo', 'id.desciclocultivo');
arrColumnasIsNull['VistaCicloCultivo'] = new Array();
arrCampoLeftJoin['VistaCicloCultivo'] = new Array();
arrCampoRestrictions['VistaCicloCultivo'] = new Array('campoRestriccion_VistaCicloCultivo');
arrValorRestrictions['VistaCicloCultivo'] = new Array('valorRestriccion_VistaCicloCultivo');
arrOperadorRestrictions['VistaCicloCultivo'] = new Array('operadorRestriccion_VistaCicloCultivo');
arrTipoValorRestrictions['VistaCicloCultivo'] = new Array('java.lang.String'); 
arrValorDependeGenerico['VistaCicloCultivo'] = new Array('0', '0', '0', '0', '99', '99', '999');
arrValorDependeOrdenGenerico['VistaCicloCultivo'] = new Array('0', '0', '0', '0', '3', '2', '1',);


//INICIO Lupa de Destino
arrObjetosLupas['VistaDestino'] = "com.rsi.agp.dao.tables.orgDat.VistaDestino";
arrCamposFiltrosLupas['VistaDestino'] = new Array('filtro_VistaDestino');
arrCamposBeanFiltros['VistaDestino'] = new Array('id.desdestino');
arrTxtCabecerasLupas['VistaDestino'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaDestino'] = new Array('30%', '70%');
arrCamposBean['VistaDestino'] = new Array('id.coddestino', 'id.desdestino');
arrCamposDepende['VistaDestino'] = new Array('cod_cpto_110', 'lineaseguroid', 'cultivo', 'variedad', 'capital', 'provincia', 'comarca', 'termino', 'subtermino');
arrCamposBeanDepende['VistaDestino'] = new Array('id.coddestino', 'id.lineaseguroid', 'id.codcultivo', 'id.codvariedad', 'codtipocapital', 'codprovincia', 'codcomarca', 'codtermino', 'subtermino');
arrTipoBeanDepende['VistaDestino'] = new Array('java.math.BigDecimal', 'java.lang.Long', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.String');
arrCamposDevolver['VistaDestino'] = new Array('cod_cpto_110', 'des_cpto_110');
arrCamposBeanDevolver['VistaDestino'] = new Array('id.coddestino', 'id.desdestino');
arrColumnasDistinct['VistaDestino'] = new Array('id.coddestino', 'id.desdestino');
arrColumnasIsNull['VistaDestino'] = new Array();
arrCampoLeftJoin['VistaDestino'] = new Array();
arrCampoRestrictions['VistaDestino'] = new Array('campoRestriccion_VistaDestino');
arrValorRestrictions['VistaDestino'] = new Array('valorRestriccion_VistaDestino');
arrOperadorRestrictions['VistaDestino'] = new Array('operadorRestriccion_VistaDestino');
arrTipoValorRestrictions['VistaDestino'] = new Array('java.lang.String'); 
arrValorDependeGenerico['VistaDestino'] = new Array('0', '0', '0', '0', '0', '99', '99', '999', '9');
arrValorDependeOrdenGenerico['VistaDestino'] = new Array('0', '0', '0', '0', '0', '4', '3', '2', '1');


//INICIO Lupa de Fecha Fin Garantias

arrCamposFiltrosLupas['VistaFechaFinGarantias'] = new Array();
arrCamposBeanFiltros['VistaFechaFinGarantias'] = new Array();
arrTxtCabecerasLupas['VistaFechaFinGarantias'] = new Array('Fecha');
arrColumnWidth['VistaFechaFinGarantias'] = new Array('100%');
arrCamposBean['VistaFechaFinGarantias'] = new Array('fgaranthasta');
arrCamposDepende['VistaFechaFinGarantias'] = new Array('lineaseguroid', 'cultivo', 'variedad', 'capital', 'provincia', 'comarca', 'termino', 'subtermino', 'cod_cpto_133');
arrCamposBeanDepende['VistaFechaFinGarantias'] = new Array('id.lineaseguroid', 'variedad.id.codcultivo', 'variedad.id.codvariedad', 'tipoCapital.codtipocapital', 'codprovincia', 'codcomarca', 'codtermino', 'subtermino', 'practicaCultural.codpracticacultural');
arrTipoBeanDepende['VistaFechaFinGarantias'] = new Array('java.lang.Long', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.String', 'java.math.BigDecimal');
arrCamposDevolver['VistaFechaFinGarantias'] = new Array('cod_cpto_134');
arrCamposBeanDevolver['VistaFechaFinGarantias'] = new Array('fgaranthasta');
arrColumnasDistinct['VistaFechaFinGarantias'] = new Array('fgaranthasta');
arrColumnasIsNull['VistaFechaFinGarantias'] = new Array();
arrCampoLeftJoin['VistaFechaFinGarantias'] = new Array('practicaCultural');
arrCampoRestrictions['VistaFechaFinGarantias'] = new Array('campoRestriccion_VistaFechaFinGarantias1', 'campoRestriccion_VistaFechaFinGarantias2', 'campoRestriccion_VistaFechaFinGarantias3');
arrValorRestrictions['VistaFechaFinGarantias'] = new Array('valorRestriccion_VistaFechaFinGarantias1', 'valorRestriccion_VistaFechaFinGarantias2', 'valorRestriccion_VistaFechaFinGarantias3');
arrOperadorRestrictions['VistaFechaFinGarantias'] = new Array('operadorRestriccion_VistaFechaFinGarantias1', 'operadorRestriccion_VistaFechaFinGarantias2', 'operadorRestriccion_VistaFechaFinGarantias3');
arrTipoValorRestrictions['VistaFechaFinGarantias'] = new Array('java.lang.String', 'java.math.BigDecimal', 'java.math.BigDecimal'); 
arrValorDependeGenerico['VistaFechaFinGarantias'] = new Array('0', '0', '999', '999', '99', '99', '999', '9', '0');
arrValorDependeOrdenGenerico['VistaFechaFinGarantias'] = new Array('0', '0', '6', '5', '4', '3', '2', '1', '0');


//INICIO Lupa de IGP
arrObjetosLupas['VistaIGP'] = "com.rsi.agp.dao.tables.orgDat.VistaIGP";
arrCamposFiltrosLupas['VistaIGP'] = new Array('filtro_VistaIGP');
arrCamposBeanFiltros['VistaIGP'] = new Array('id.descripcion');
arrTxtCabecerasLupas['VistaIGP'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaIGP'] = new Array('30%', '70%');
arrCamposBean['VistaIGP'] = new Array('id.codigp', 'id.descripcion');
arrCamposDepende['VistaIGP'] = new Array('cod_cpto_765', 'lineaseguroid', 'cultivo', 'variedad', 'provincia');
arrCamposBeanDepende['VistaIGP'] = new Array('id.codigp', 'id.lineaseguroid', 'id.codcultivo', 'id.codvariedad', 'id.codprovincia');
arrTipoBeanDepende['VistaIGP'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['VistaIGP'] = new Array('cod_cpto_765', 'des_cpto_765');
arrCamposBeanDevolver['VistaIGP'] = new Array('id.codigp', 'id.descripcion');
arrColumnasDistinct['VistaIGP'] = new Array('id.codigp', 'id.descripcion');
arrColumnasIsNull['VistaIGP'] = new Array();
arrCampoLeftJoin['VistaIGP'] = new Array();
arrCampoRestrictions['VistaIGP'] = new Array('campoRestriccion_VistaIGP');
arrValorRestrictions['VistaIGP'] = new Array('valorRestriccion_VistaIGP');
arrOperadorRestrictions['VistaIGP'] = new Array('operadorRestriccion_VistaIGP');
arrTipoValorRestrictions['VistaIGP'] = new Array('java.lang.String'); 
arrValorDependeGenerico['VistaIGP'] = new Array('0', '0', '999', '999', '99');
arrValorDependeOrdenGenerico['VistaIGP'] = new Array('0', '0', '2', '1', '3');


//INICIO Lupa de IGP Factores/Ambito
arrObjetosLupas['VistaIGPFactorAmbito'] = "com.rsi.agp.dao.tables.orgDat.VistaIGPFactorAmbito";
arrCamposFiltrosLupas['VistaIGPFactorAmbito'] = new Array('filtro_VistaIGPFactorAmbito');
arrCamposBeanFiltros['VistaIGPFactorAmbito'] = new Array('id.descripcion');
arrTxtCabecerasLupas['VistaIGPFactorAmbito'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaIGPFactorAmbito'] = new Array('30%', '70%');
arrCamposBean['VistaIGPFactorAmbito'] = new Array('id.codigp', 'id.descripcion');
arrCamposDepende['VistaIGPFactorAmbito'] = new Array('cod_cpto_107', 'lineaseguroid', 'cultivo', 'variedad', 'provincia', 'comarca', 'termino', 'subtermino');
arrCamposBeanDepende['VistaIGPFactorAmbito'] = new Array('id.codigp', 'id.lineaseguroid', 'id.codcultivo', 'id.codvariedad', 'id.codprovincia', 'id.codcomarca', 'id.codtermino', 'id.codsubtermino');
arrTipoBeanDepende['VistaIGPFactorAmbito'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.String');
arrCamposDevolver['VistaIGPFactorAmbito'] = new Array('cod_cpto_107', 'des_cpto_107');
arrCamposBeanDevolver['VistaIGPFactorAmbito'] = new Array('id.codigp', 'id.descripcion');
arrColumnasDistinct['VistaIGPFactorAmbito'] = new Array('id.codigp', 'id.descripcion');
arrColumnasIsNull['VistaIGPFactorAmbito'] = new Array();
arrCampoLeftJoin['VistaIGPFactorAmbito'] = new Array();
arrCampoRestrictions['VistaIGPFactorAmbito'] = new Array('campoRestriccion_VistaIGPFactorAmbito');
arrValorRestrictions['VistaIGPFactorAmbito'] = new Array('valorRestriccion_VistaIGPFactorAmbito');
arrOperadorRestrictions['VistaIGPFactorAmbito'] = new Array('operadorRestriccion_VistaIGPFactorAmbito');
arrTipoValorRestrictions['VistaIGPFactorAmbito'] = new Array('java.lang.String'); 
arrValorDependeGenerico['VistaIGPFactorAmbito'] = new Array('0', '0', '0', '0', '99', '99', '999', '9');
arrValorDependeOrdenGenerico['VistaIGPFactorAmbito'] = new Array('0', '0', '0', '0', '4', '3', '2', '1');


//INICIO Lupa de Marco Plantacion
arrObjetosLupas['VistaMarcoPlantacion'] = "com.rsi.agp.dao.tables.orgDat.VistaMarcoPlantacion";
arrCamposFiltrosLupas['VistaMarcoPlantacion'] = new Array('filtro_VistaMarcoPlantacion');
arrCamposBeanFiltros['VistaMarcoPlantacion'] = new Array('id.destipomarcoplantac');
arrTxtCabecerasLupas['VistaMarcoPlantacion'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaMarcoPlantacion'] = new Array('30%', '70%');
arrCamposBean['VistaMarcoPlantacion'] = new Array('id.codtipomarcoplantac', 'id.destipomarcoplantac');
arrCamposDepende['VistaMarcoPlantacion'] = new Array('cod_cpto_116', 'lineaseguroid', 'cultivo', 'variedad', 'provincia', 'comarca', 'termino', 'subtermino');
arrCamposBeanDepende['VistaMarcoPlantacion'] = new Array('id.codtipomarcoplantac', 'id.lineaseguroid', 'id.codcultivo', 'id.codvariedad', 'codprovincia', 'codcomarca', 'codtermino', 'subtermino');
arrTipoBeanDepende['VistaMarcoPlantacion'] = new Array('java.math.BigDecimal', 'java.lang.Long', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.String');
arrCamposDevolver['VistaMarcoPlantacion'] = new Array('cod_cpto_116', 'des_cpto_116');
arrCamposBeanDevolver['VistaMarcoPlantacion'] = new Array('id.codtipomarcoplantac', 'id.destipomarcoplantac');
arrColumnasDistinct['VistaMarcoPlantacion'] = new Array('id.codtipomarcoplantac', 'id.destipomarcoplantac');
arrColumnasIsNull['VistaMarcoPlantacion'] = new Array();
arrCampoLeftJoin['VistaMarcoPlantacion'] = new Array();
arrCampoRestrictions['VistaMarcoPlantacion'] = new Array('campoRestriccion_VistaMarcoPlantacion');
arrValorRestrictions['VistaMarcoPlantacion'] = new Array('valorRestriccion_VistaMarcoPlantacion');
arrOperadorRestrictions['VistaMarcoPlantacion'] = new Array('operadorRestriccion_VistaMarcoPlantacion');
arrTipoValorRestrictions['VistaMarcoPlantacion'] = new Array('java.lang.String'); 
arrValorDependeGenerico['VistaMarcoPlantacion'] = new Array('0', '0', '0', '0', '99', '99', '999', '9');
arrValorDependeOrdenGenerico['VistaMarcoPlantacion'] = new Array('0', '0', '0', '0', '4', '3', '2', '1');


//INICIO Lupa de Material Cubierta
arrObjetosLupas['VistaMaterialCubierta'] = "com.rsi.agp.dao.tables.orgDat.VistaMaterialCubierta";
arrCamposFiltrosLupas['VistaMaterialCubierta'] = new Array('filtro_VistaMaterialCubierta');
arrCamposBeanFiltros['VistaMaterialCubierta'] = new Array('id.descripcion');
arrTxtCabecerasLupas['VistaMaterialCubierta'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaMaterialCubierta'] = new Array('30%', '70%');
arrCamposBean['VistaMaterialCubierta'] = new Array('id.codmaterialcubierta', 'id.descripcion');
arrCamposDepende['VistaMaterialCubierta'] = new Array('cod_cpto_873', 'lineaseguroid', 'cultivo', 'variedad', 'capital', 'cod_cpto_621');
arrCamposBeanDepende['VistaMaterialCubierta'] = new Array('id.codmaterialcubierta', 'id.lineaseguroid', 'id.codcultivo', 'id.codvariedad', 'codtipocapital', 'id.codsistemaproteccion');
arrTipoBeanDepende['VistaMaterialCubierta'] = new Array('java.math.BigDecimal', 'java.lang.Long', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['VistaMaterialCubierta'] = new Array('cod_cpto_873', 'des_cpto_873');
arrCamposBeanDevolver['VistaMaterialCubierta'] = new Array('id.codmaterialcubierta', 'id.descripcion');
arrColumnasDistinct['VistaMaterialCubierta'] = new Array('id.codmaterialcubierta', 'id.descripcion');
arrColumnasIsNull['VistaMaterialCubierta'] = new Array();
arrCampoLeftJoin['VistaMaterialCubierta'] = new Array();
arrCampoRestrictions['VistaMaterialCubierta'] = new Array('campoRestriccion_VistaMaterialCubierta');
arrValorRestrictions['VistaMaterialCubierta'] = new Array('valorRestriccion_VistaMaterialCubierta');
arrOperadorRestrictions['VistaMaterialCubierta'] = new Array('operadorRestriccion_VistaMaterialCubierta');
arrTipoValorRestrictions['VistaMaterialCubierta'] = new Array('java.lang.String'); 
arrValorDependeGenerico['VistaMaterialCubierta'] = new Array();
arrValorDependeOrdenGenerico['VistaMaterialCubierta'] = new Array();

//INICIO Lupa de Rotacion
arrObjetosLupas['VistaRotacion'] = "com.rsi.agp.dao.tables.orgDat.VistaRotacion";
arrCamposFiltrosLupas['VistaRotacion'] = new Array('filtro_VistaRotacion');
arrCamposBeanFiltros['VistaRotacion'] = new Array('id.descripcion');
arrTxtCabecerasLupas['VistaRotacion'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaRotacion'] = new Array('30%', '70%');
arrCamposBean['VistaRotacion'] = new Array('id.codrotacion', 'id.descripcion');
arrCamposDepende['VistaRotacion'] = new Array('cod_cpto_144', 'lineaseguroid', 'cultivo', 'variedad', 'capital');
arrCamposBeanDepende['VistaRotacion'] = new Array('id.codrotacion', 'id.lineaseguroid', 'id.codcultivo', 'id.codvariedad', 'codtipocapital');
arrTipoBeanDepende['VistaRotacion'] = new Array('java.math.BigDecimal', 'java.lang.Long', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['VistaRotacion'] = new Array('cod_cpto_144', 'des_cpto_144');
arrCamposBeanDevolver['VistaRotacion'] = new Array('id.codrotacion', 'id.descripcion');
arrColumnasDistinct['VistaRotacion'] = new Array('id.codrotacion', 'id.descripcion');
arrColumnasIsNull['VistaRotacion'] = new Array();
arrCampoLeftJoin['VistaRotacion'] = new Array();
arrCampoRestrictions['VistaRotacion'] = new Array('campoRestriccion_VistaRotacion');
arrValorRestrictions['VistaRotacion'] = new Array('valorRestriccion_VistaRotacion');
arrOperadorRestrictions['VistaRotacion'] = new Array('operadorRestriccion_VistaRotacion');
arrTipoValorRestrictions['VistaRotacion'] = new Array('java.lang.String'); 
arrValorDependeGenerico['VistaRotacion'] = new Array();
arrValorDependeOrdenGenerico['VistaRotacion'] = new Array();


//INICIO Lupa de Sistema Cultivo
arrObjetosLupas['VistaSistemaCultivo'] = "com.rsi.agp.dao.tables.orgDat.VistaSistemaCultivo";
arrCamposFiltrosLupas['VistaSistemaCultivo'] = new Array('filtro_VistaSistemaCultivo');
arrCamposBeanFiltros['VistaSistemaCultivo'] = new Array('id.dessistemacultivo');
arrTxtCabecerasLupas['VistaSistemaCultivo'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaSistemaCultivo'] = new Array('30%', '70%');
arrCamposBean['VistaSistemaCultivo'] = new Array('id.codsistemacultivo', 'id.dessistemacultivo');
arrCamposDepende['VistaSistemaCultivo'] = new Array('cod_cpto_123', 'lineaseguroid', 'cultivo', 'variedad', 'capital', 'provincia', 'comarca', 'termino', 'subtermino');
arrCamposBeanDepende['VistaSistemaCultivo'] = new Array('id.codsistemacultivo', 'id.lineaseguroid', 'id.codcultivo', 'id.codvariedad', 'codtipocapital', 'codprovincia', 'codcomarca', 'codtermino', 'subtermino');
arrTipoBeanDepende['VistaSistemaCultivo'] = new Array('java.math.BigDecimal', 'java.lang.Long', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.String');
arrCamposDevolver['VistaSistemaCultivo'] = new Array('cod_cpto_123', 'des_cpto_123');
arrCamposBeanDevolver['VistaSistemaCultivo'] = new Array('id.codsistemacultivo', 'id.dessistemacultivo');
arrColumnasDistinct['VistaSistemaCultivo'] = new Array('id.codsistemacultivo', 'id.dessistemacultivo');
arrColumnasIsNull['VistaSistemaCultivo'] = new Array();
arrCampoLeftJoin['VistaSistemaCultivo'] = new Array();
arrCampoRestrictions['VistaSistemaCultivo'] = new Array('campoRestriccion_VistaSistemaCultivo');
arrValorRestrictions['VistaSistemaCultivo'] = new Array('valorRestriccion_VistaSistemaCultivo');
arrOperadorRestrictions['VistaSistemaCultivo'] = new Array('operadorRestriccion_VistaSistemaCultivo');
arrTipoValorRestrictions['VistaSistemaCultivo'] = new Array('java.lang.String'); 
arrValorDependeGenerico['VistaSistemaCultivo'] = new Array('0', '0', '0', '0', '0', '99', '99', '999', '9');
arrValorDependeOrdenGenerico['VistaSistemaCultivo'] = new Array('0', '0', '0', '0', '0', '4', '3', '2', '1');


//INICIO Lupa de Sistema Cultivo 310
arrObjetosLupas['VistaSistemaCultivo310'] = "com.rsi.agp.dao.tables.admin.ClaseDetalle";
arrCamposFiltrosLupas['VistaSistemaCultivo310'] = new Array('filtro_VistaSistemaCultivo310');
arrCamposBeanFiltros['VistaSistemaCultivo310'] = new Array('sistemaCultivo.codsistemacultivo');
arrTxtCabecerasLupas['VistaSistemaCultivo310'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaSistemaCultivo310'] = new Array('30%', '70%');
arrCamposBean['VistaSistemaCultivo310'] = new Array('sistemaCultivo.codsistemacultivo', 'sistemaCultivo.dessistemacultivo');
arrCamposDepende['VistaSistemaCultivo310'] = new Array('cod_cpto_123', 'claseId', 'cultivo', 'variedad', 'provincia', 'comarca', 'termino', 'subtermino');
arrCamposBeanDepende['VistaSistemaCultivo310'] = new Array('sistemaCultivo.codsistemacultivo', 'clase.id', 'variedad.id.codcultivo', 'variedad.id.codvariedad', 'codprovincia', 'codcomarca', 'codtermino', 'subtermino');
arrTipoBeanDepende['VistaSistemaCultivo310'] = new Array('java.math.BigDecimal', 'java.lang.Long', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.String');
arrCamposDevolver['VistaSistemaCultivo310'] = new Array('cod_cpto_123', 'des_cpto_123');
arrCamposBeanDevolver['VistaSistemaCultivo310'] = new Array('sistemaCultivo.codsistemacultivo', 'sistemaCultivo.dessistemacultivo');
arrColumnasDistinct['VistaSistemaCultivo310'] = new Array('sistemaCultivo.codsistemacultivo', 'sistemaCultivo.dessistemacultivo');
arrColumnasIsNull['VistaSistemaCultivo310'] = new Array();
arrCampoLeftJoin['VistaSistemaCultivo310'] = new Array();
arrCampoRestrictions['VistaSistemaCultivo310'] = new Array('campoRestriccion_VistaSistemaCultivo310');
arrValorRestrictions['VistaSistemaCultivo310'] = new Array('valorRestriccion_VistaSistemaCultivo310');
arrOperadorRestrictions['VistaSistemaCultivo310'] = new Array('operadorRestriccion_VistaSistemaCultivo310');
arrTipoValorRestrictions['VistaSistemaCultivo310'] = new Array('java.lang.String'); 
arrValorDependeGenerico['VistaSistemaCultivo310'] = new Array('0', '0', '999', '999', '99', '99', '999', '9');
arrValorDependeOrdenGenerico['VistaSistemaCultivo310'] = new Array('0', '0', '6', '5', '4', '3', '2', '1');


//INICIO Lupa de Sistema Cultivo 312
arrObjetosLupas['VistaSistemaCultivo312'] = "com.rsi.agp.dao.tables.orgDat.VistaSistemaCultivo312";
arrCamposFiltrosLupas['VistaSistemaCultivo312'] = new Array('filtro_VistaSistemaCultivo312');
arrCamposBeanFiltros['VistaSistemaCultivo312'] = new Array('id.dessistemacultivo');
arrTxtCabecerasLupas['VistaSistemaCultivo312'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaSistemaCultivo312'] = new Array('30%', '70%');
arrCamposBean['VistaSistemaCultivo312'] = new Array('id.codsistemacultivo', 'id.dessistemacultivo');
arrCamposDepende['VistaSistemaCultivo312'] = new Array('cod_cpto_123', 'lineaseguroid', 'cultivo', 'variedad', 'capital', 'provincia', 'comarca', 'termino', 'subtermino');
arrCamposBeanDepende['VistaSistemaCultivo312'] = new Array('id.codsistemacultivo', 'id.lineaseguroid', 'id.codcultivo', 'id.codvariedad', 'id.codtipocapital', 'id.codprovincia', 'id.codcomarca', 'id.codtermino', 'id.subtermino');
arrTipoBeanDepende['VistaSistemaCultivo312'] = new Array('java.math.BigDecimal', 'java.lang.Long', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.String');
arrCamposDevolver['VistaSistemaCultivo312'] = new Array('cod_cpto_123', 'des_cpto_123');
arrCamposBeanDevolver['VistaSistemaCultivo312'] = new Array('id.codsistemacultivo', 'id.dessistemacultivo');
arrColumnasDistinct['VistaSistemaCultivo312'] = new Array('id.codsistemacultivo', 'id.dessistemacultivo');
arrColumnasIsNull['VistaSistemaCultivo312'] = new Array();
arrCampoLeftJoin['VistaSistemaCultivo312'] = new Array();
arrCampoRestrictions['VistaSistemaCultivo312'] = new Array('campoRestriccion_VistaSistemaCultivo312');
arrValorRestrictions['VistaSistemaCultivo312'] = new Array('valorRestriccion_VistaSistemaCultivo312');
arrOperadorRestrictions['VistaSistemaCultivo312'] = new Array('operadorRestriccion_VistaSistemaCultivo312');
arrTipoValorRestrictions['VistaSistemaCultivo312'] = new Array('java.lang.String'); 
arrValorDependeGenerico['VistaSistemaCultivo312'] = new Array('0', '0', '0', '0', '0', '99', '99', '999', '9');
arrValorDependeOrdenGenerico['VistaSistemaCultivo312'] = new Array('0', '0', '0', '0', '0', '4', '3', '2', '1');


//INICIO Lupa de Sistema Produccion
arrObjetosLupas['VistaSistemaProduccion'] = "com.rsi.agp.dao.tables.orgDat.VistaSistemaProduccion";
arrCamposFiltrosLupas['VistaSistemaProduccion'] = new Array('filtro_VistaSistemaProduccion');
arrCamposBeanFiltros['VistaSistemaProduccion'] = new Array('id.dessistemaproduccion');
arrTxtCabecerasLupas['VistaSistemaProduccion'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaSistemaProduccion'] = new Array('30%', '70%');
arrCamposBean['VistaSistemaProduccion'] = new Array('id.codsistemaproduccion', 'id.dessistemaproduccion');
arrCamposDepende['VistaSistemaProduccion'] = new Array('cod_cpto_616', 'lineaseguroid', 'cultivo', 'variedad', 'capital', 'provincia', 'comarca', 'termino', 'subtermino');
arrCamposBeanDepende['VistaSistemaProduccion'] = new Array('id.codsistemaproduccion', 'id.lineaseguroid', 'id.codcultivo', 'id.codvariedad', 'codtipocapital', 'codprovincia', 'codcomarca', 'codtermino', 'subtermino');
arrTipoBeanDepende['VistaSistemaProduccion'] = new Array('java.math.BigDecimal', 'java.lang.Long', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.String');
arrCamposDevolver['VistaSistemaProduccion'] = new Array('cod_cpto_616', 'des_cpto_616');
arrCamposBeanDevolver['VistaSistemaProduccion'] = new Array('id.codsistemaproduccion', 'id.dessistemaproduccion');
arrColumnasDistinct['VistaSistemaProduccion'] = new Array('id.codsistemaproduccion', 'id.dessistemaproduccion');
arrColumnasIsNull['VistaSistemaProduccion'] = new Array();
arrCampoLeftJoin['VistaSistemaProduccion'] = new Array();
arrCampoRestrictions['VistaSistemaProduccion'] = new Array('campoRestriccion_VistaSistemaProduccion');
arrValorRestrictions['VistaSistemaProduccion'] = new Array('valorRestriccion_VistaSistemaProduccion');
arrOperadorRestrictions['VistaSistemaProduccion'] = new Array('operadorRestriccion_VistaSistemaProduccion');
arrTipoValorRestrictions['VistaSistemaProduccion'] = new Array('java.lang.String'); 
arrValorDependeGenerico['VistaSistemaProduccion'] = new Array('0', '0', '0', '0', '0', '99', '99', '999', '9');
arrValorDependeOrdenGenerico['VistaSistemaProduccion'] = new Array('0', '0', '0', '0', '0', '4', '3', '2', '1');


//INICIO Lupa de Sistema Proteccion
arrObjetosLupas['VistaSistemaProteccion'] = "com.rsi.agp.dao.tables.orgDat.VistaSistemaProteccion";
arrCamposFiltrosLupas['VistaSistemaProteccion'] = new Array('filtro_VistaSistemaProteccion');
arrCamposBeanFiltros['VistaSistemaProteccion'] = new Array('id.dessistemaproteccion');
arrTxtCabecerasLupas['VistaSistemaProteccion'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaSistemaProteccion'] = new Array('30%', '70%');
arrCamposBean['VistaSistemaProteccion'] = new Array('id.codsistemaproteccion', 'id.dessistemaproteccion');
arrCamposDepende['VistaSistemaProteccion'] = new Array('cod_cpto_621', 'lineaseguroid', 'cultivo', 'variedad');
arrCamposBeanDepende['VistaSistemaProteccion'] = new Array('id.codsistemaproteccion', 'id.lineaseguroid', 'id.codcultivo', 'id.codvariedad');
arrTipoBeanDepende['VistaSistemaProteccion'] = new Array('java.math.BigDecimal', 'java.lang.Long', 'java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['VistaSistemaProteccion'] = new Array('cod_cpto_621', 'des_cpto_621');
arrCamposBeanDevolver['VistaSistemaProteccion'] = new Array('id.codsistemaproteccion', 'id.dessistemaproteccion');
arrColumnasDistinct['VistaSistemaProteccion'] = new Array('id.codsistemaproteccion', 'id.dessistemaproteccion');
arrColumnasIsNull['VistaSistemaProteccion'] = new Array();
arrCampoLeftJoin['VistaSistemaProteccion'] = new Array();
arrCampoRestrictions['VistaSistemaProteccion'] = new Array('campoRestriccion_VistaSistemaProteccion');
arrValorRestrictions['VistaSistemaProteccion'] = new Array('valorRestriccion_VistaSistemaProteccion');
arrOperadorRestrictions['VistaSistemaProteccion'] = new Array('operadorRestriccion_VistaSistemaProteccion');
arrTipoValorRestrictions['VistaSistemaProteccion'] = new Array('java.lang.String'); 
arrValorDependeGenerico['VistaSistemaProteccion'] = new Array();
arrValorDependeOrdenGenerico['VistaSistemaProteccion'] = new Array();


//INICIO Lupa de Tipo Plantacion
arrObjetosLupas['VistaTipoPlantacion'] = "com.rsi.agp.dao.tables.orgDat.VistaTipoPlantacion";
arrCamposFiltrosLupas['VistaTipoPlantacion'] = new Array('filtro_VistaTipoPlantacion');
arrCamposBeanFiltros['VistaTipoPlantacion'] = new Array('id.destipoplantacion');
arrTxtCabecerasLupas['VistaTipoPlantacion'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaTipoPlantacion'] = new Array('30%', '70%');
arrCamposBean['VistaTipoPlantacion'] = new Array('id.codtipoplantacion', 'id.destipoplantacion');
arrCamposDepende['VistaTipoPlantacion'] = new Array('cod_cpto_173', 'lineaseguroid', 'cultivo', 'variedad', 'capital', 'provincia', 'comarca', 'termino', 'subtermino');
arrCamposBeanDepende['VistaTipoPlantacion'] = new Array('id.codtipoplantacion', 'id.lineaseguroid', 'id.codcultivo', 'id.codvariedad', 'codtipocapital', 'codprovincia', 'codcomarca', 'codtermino', 'subtermino');
arrTipoBeanDepende['VistaTipoPlantacion'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.String');
arrCamposDevolver['VistaTipoPlantacion'] = new Array('cod_cpto_173', 'des_cpto_173');
arrCamposBeanDevolver['VistaTipoPlantacion'] = new Array('id.codtipoplantacion', 'id.destipoplantacion');
arrColumnasDistinct['VistaTipoPlantacion'] = new Array('id.codtipoplantacion', 'id.destipoplantacion');
arrColumnasIsNull['VistaTipoPlantacion'] = new Array();
arrCampoLeftJoin['VistaTipoPlantacion'] = new Array();
arrCampoRestrictions['VistaTipoPlantacion'] = new Array('campoRestriccion_VistaSistemaProteccion');
arrValorRestrictions['VistaTipoPlantacion'] = new Array('valorRestriccion_VistaSistemaProteccion');
arrOperadorRestrictions['VistaTipoPlantacion'] = new Array('operadorRestriccion_VistaSistemaProteccion');
arrTipoValorRestrictions['VistaTipoPlantacion'] = new Array('java.lang.String'); 
arrValorDependeGenerico['VistaTipoPlantacion'] = new Array('0', '0', '0', '0', '0', '99', '99', '999', '9');
arrValorDependeOrdenGenerico['VistaTipoPlantacion'] = new Array('0', '0', '0', '0', '0', '4', '3', '2', '1');


//INICIO Lupa de Tipo Instalacion
arrObjetosLupas['VistaTipoInstalacion'] = "com.rsi.agp.dao.tables.orgDat.VistaTipoInstalacion";
arrCamposFiltrosLupas['VistaTipoInstalacion'] = new Array('filtro_VistaTipoInstalacion');
arrCamposBeanFiltros['VistaTipoInstalacion'] = new Array('id.descripcion');
arrTxtCabecerasLupas['VistaTipoInstalacion'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaTipoInstalacion'] = new Array('30%', '70%');
arrCamposBean['VistaTipoInstalacion'] = new Array('id.codtipoinstalacion', 'id.descripcion');
arrCamposDepende['VistaTipoInstalacion'] = new Array('cod_cpto_778', 'lineaseguroid', 'cultivo', 'variedad', 'capital', 'provincia', 'comarca', 'termino', 'subtermino');
arrCamposBeanDepende['VistaTipoInstalacion'] = new Array('id.codtipoinstalacion', 'id.lineaseguroid', 'id.codcultivo', 'id.codvariedad', 'id.codtipocapital', 'id.codprovincia', 'id.codcomarca', 'id.codtermino', 'id.subtermino');
arrTipoBeanDepende['VistaTipoInstalacion'] = new Array('java.math.BigDecimal', 'java.lang.Long', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.String');
arrCamposDevolver['VistaTipoInstalacion'] = new Array('cod_cpto_778', 'des_cpto_778');
arrCamposBeanDevolver['VistaTipoInstalacion'] = new Array('id.codtipoinstalacion', 'id.descripcion');
arrColumnasDistinct['VistaTipoInstalacion'] = new Array('id.codtipoinstalacion', 'id.descripcion');
arrColumnasIsNull['VistaTipoInstalacion'] = new Array();
arrCampoLeftJoin['VistaTipoInstalacion'] = new Array();
arrCampoRestrictions['VistaTipoInstalacion'] = new Array('campoRestriccion_VistaTipoInstalacion');
arrValorRestrictions['VistaTipoInstalacion'] = new Array('valorRestriccion_VistaTipoInstalacion');
arrOperadorRestrictions['VistaTipoInstalacion'] = new Array('operadorRestriccion_VistaTipoInstalacion');
arrTipoValorRestrictions['VistaTipoInstalacion'] = new Array('java.lang.String'); 
arrValorDependeGenerico['VistaTipoInstalacion'] = new Array('0', '0', '0', '0', '0', '99', '99', '999', '9');
arrValorDependeOrdenGenerico['VistaTipoInstalacion'] = new Array('0', '0', '0', '0', '0', '4', '3', '2', '1');


//INICIO Lupa de Material Estructura
arrObjetosLupas['VistaMaterialEstructura'] = "com.rsi.agp.dao.tables.orgDat.VistaMaterialEstructura";
arrCamposFiltrosLupas['VistaMaterialEstructura'] = new Array('filtro_VistaMaterialEstructura');
arrCamposBeanFiltros['VistaMaterialEstructura'] = new Array('id.descripcion');
arrTxtCabecerasLupas['VistaMaterialEstructura'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaMaterialEstructura'] = new Array('30%', '70%');
arrCamposBean['VistaMaterialEstructura'] = new Array('id.codmaterialestructura', 'id.descripcion');
arrCamposDepende['VistaMaterialEstructura'] = new Array('cod_cpto_875', 'lineaseguroid', 'cultivo', 'variedad', 'capital', 'provincia', 'comarca', 'termino', 'subtermino');
arrCamposBeanDepende['VistaMaterialEstructura'] = new Array('id.codmaterialestructura', 'id.lineaseguroid', 'id.codcultivo', 'id.codvariedad', 'id.codtipocapital', 'id.codprovincia', 'id.codcomarca', 'id.codtermino', 'id.subtermino');
arrTipoBeanDepende['VistaMaterialEstructura'] = new Array('java.math.BigDecimal', 'java.lang.Long', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.String');
arrCamposDevolver['VistaMaterialEstructura'] = new Array('cod_cpto_875', 'des_cpto_875');
arrCamposBeanDevolver['VistaMaterialEstructura'] = new Array('id.codmaterialestructura', 'id.descripcion');
arrColumnasDistinct['VistaMaterialEstructura'] = new Array('id.codmaterialestructura', 'id.descripcion');
arrColumnasIsNull['VistaMaterialEstructura'] = new Array();
arrCampoLeftJoin['VistaMaterialEstructura'] = new Array();
arrCampoRestrictions['VistaMaterialEstructura'] = new Array('campoRestriccion_VistaMaterialEstructura');
arrValorRestrictions['VistaMaterialEstructura'] = new Array('valorRestriccion_VistaMaterialEstructura');
arrOperadorRestrictions['VistaMaterialEstructura'] = new Array('operadorRestriccion_VistaMaterialEstructura');
arrTipoValorRestrictions['VistaMaterialEstructura'] = new Array('java.lang.String'); 
arrValorDependeGenerico['VistaMaterialEstructura'] = new Array('0', '0', '0', '0', '0', '99', '99', '999', '9');
arrValorDependeOrdenGenerico['VistaMaterialEstructura'] = new Array('0', '0', '0', '0', '0', '4', '3', '2', '1');


//INICIO Lupa de Material Estructura Instalaciones
arrObjetosLupas['VistaMaterialEstructuraInst'] = "com.rsi.agp.dao.tables.cgen.MaterialEstructura";
arrCamposFiltrosLupas['VistaMaterialEstructuraInst'] = new Array('filtro_VistaMaterialEstructuraInst');
arrCamposBeanFiltros['VistaMaterialEstructuraInst'] = new Array('descripcion');
arrTxtCabecerasLupas['VistaMaterialEstructuraInst'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaMaterialEstructuraInst'] = new Array('30%', '70%');
arrCamposBean['VistaMaterialEstructuraInst'] = new Array('codmaterialestructura', 'descripcion');
arrCamposDepende['VistaMaterialEstructuraInst'] = new Array('cod_cpto_875');
arrCamposBeanDepende['VistaMaterialEstructuraInst'] = new Array('codmaterialestructura');
arrTipoBeanDepende['VistaMaterialEstructuraInst'] = new Array('java.math.BigDecimal');
arrCamposDevolver['VistaMaterialEstructuraInst'] = new Array('cod_cpto_875', 'des_cpto_875');
arrCamposBeanDevolver['VistaMaterialEstructuraInst'] = new Array('codmaterialestructura', 'descripcion');
arrColumnasDistinct['VistaMaterialEstructuraInst'] = new Array();
arrColumnasIsNull['VistaMaterialEstructuraInst'] = new Array();
arrCampoLeftJoin['VistaMaterialEstructuraInst'] = new Array();
arrCampoRestrictions['VistaMaterialEstructuraInst'] = new Array();
arrValorRestrictions['VistaMaterialEstructuraInst'] = new Array();
arrOperadorRestrictions['VistaMaterialEstructuraInst'] = new Array();
arrTipoValorRestrictions['VistaMaterialEstructuraInst'] = new Array(); 
arrValorDependeGenerico['VistaMaterialEstructuraInst'] = new Array();
arrValorDependeOrdenGenerico['VistaMaterialEstructuraInst'] = new Array();


//INICIO Lupa de Material Cubierta Instalaciones
arrObjetosLupas['VistaMaterialCubiertaInst'] = "com.rsi.agp.dao.tables.orgDat.VistaMaterialCubierta";
arrCamposFiltrosLupas['VistaMaterialCubiertaInst'] = new Array('filtro_VistaMaterialCubiertaInst');
arrCamposBeanFiltros['VistaMaterialCubiertaInst'] = new Array('id.descripcion');
arrTxtCabecerasLupas['VistaMaterialCubiertaInst'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['VistaMaterialCubiertaInst'] = new Array('30%', '70%');
arrCamposBean['VistaMaterialCubiertaInst'] = new Array('id.codmaterialcubierta', 'id.descripcion');
arrCamposDepende['VistaMaterialCubiertaInst'] = new Array('cod_cpto_873', 'lineaseguroid');
arrCamposBeanDepende['VistaMaterialCubiertaInst'] = new Array('id.codmaterialcubierta', 'id.lineaseguroid');
arrTipoBeanDepende['VistaMaterialCubiertaInst'] = new Array('java.math.BigDecimal', 'java.lang.Long');
arrCamposDevolver['VistaMaterialCubiertaInst'] = new Array('cod_cpto_873', 'des_cpto_873');
arrCamposBeanDevolver['VistaMaterialCubiertaInst'] = new Array('id.codmaterialcubierta', 'id.descripcion');
arrColumnasDistinct['VistaMaterialCubiertaInst'] = new Array('id.codmaterialcubierta', 'id.descripcion');
arrColumnasIsNull['VistaMaterialCubiertaInst'] = new Array();
arrCampoLeftJoin['VistaMaterialCubiertaInst'] = new Array();
arrCampoRestrictions['VistaMaterialCubiertaInst'] = new Array('campoRestriccion_VistaMaterialCubiertaInst');
arrValorRestrictions['VistaMaterialCubiertaInst'] = new Array('valorRestriccion_VistaMaterialCubiertaInst');
arrOperadorRestrictions['VistaMaterialCubiertaInst'] = new Array('operadorRestriccion_VistaMaterialCubiertaInst');
arrTipoValorRestrictions['VistaMaterialCubiertaInst'] = new Array('java.lang.String'); 
arrValorDependeGenerico['VistaMaterialCubiertaInst'] = new Array();
arrValorDependeOrdenGenerico['VistaMaterialCubiertaInst'] = new Array();