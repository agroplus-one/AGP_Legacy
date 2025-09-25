//ARRAYS PARA LAS LUPAS
//Objeto asociado a cada lupa
var arrObjetosLupas = new Array();
//Campos por los que se puede filtrar en la lupa
var arrCamposFiltrosLupas = new Array();
//Campos de los beans que se corresponden con los campos de busqueda
var arrCamposBeanFiltros = new Array();
//Campos de los tipo de datos que se corresponden con los campos de busqueda
var arrTipoCamposBeanFiltros = new Array();
//Textos que iran en las cabeceras de las tablas
var arrTxtCabecerasLupas = new Array();
//Ancho de las columnas
var arrColumnWidth = new Array();
//Campos de los beans que corresponden con cada columna
var arrCamposBean = new Array();
//Campos de pantalla de los que depende la lupa
var arrCamposDepende = new Array();
//Campos del bean de los que depende la lupa
var arrCamposBeanDepende = new Array();
//Tipos de los campos del bean de los que depende la lupa
var arrTipoBeanDepende = new Array();
//Campos de pantalla a devolver
var arrCamposDevolver = new Array();
//Campos del bean a devolver
var arrCamposBeanDevolver = new Array();
// Columnas de la tabla que no se mostraran (opcional)
var arrOcultarColumnas = new Array();
//Columnas de la tabla por las que se hara distinct 
var arrColumnasDistinct = new Array();
//Columnas de la tabla por las que se hara en el criteria un is null
var arrColumnasIsNull = new Array();
//Columnas de la tabla por las que se hara left join
var arrCampoLeftJoin = new Array();

//campo para Restrictions.  Operador Restrictions y Valor de comparacion
var arrCampoRestrictions = new Array();
var arrOperadorRestrictions = new Array();
var arrValorRestrictions = new Array();
var arrTipoValorRestrictions = new Array();

// Valores para la busqueda generica
var arrValorDependeGenerico = new Array();
var arrValorDependeOrdenGenerico = new Array();

var arrAcumulaResultados= new Array();
//-----------------------------------------------------------------------



// INICIO Lupa de Lineas del condicionado
arrObjetosLupas['LineaCondicionado'] = "com.rsi.agp.dao.tables.cgen.LineaCondicionado";
arrCamposFiltrosLupas['LineaCondicionado'] = new Array('filtro_codLineaCondicionado', 'filtro_LineaCondicionado');
arrCamposBeanFiltros['LineaCondicionado'] = new Array('codlinea', 'deslinea');
arrTipoCamposBeanFiltros['LineaCondicionado'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['LineaCondicionado'] = new Array('C\u00F3digo', 'Nombre');
arrColumnWidth['LineaCondicionado'] = new Array('30%', '70%');
arrCamposBean['LineaCondicionado'] = new Array('codlinea', 'deslinea');
arrCamposDepende['LineaCondicionado'] =  new Array('lineaCondicionado', 'nomLineaCondicionado');
arrCamposBeanDepende['LineaCondicionado'] =  new Array('codlinea', 'deslinea');
arrTipoBeanDepende['LineaCondicionado'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrCamposDevolver['LineaCondicionado'] = new Array('lineaCondicionado', 'nomLineaCondicionado');
arrCamposBeanDevolver['LineaCondicionado'] = new Array('codlinea', 'deslinea');
arrColumnasDistinct['LineaCondicionado'] = new Array();
arrColumnasIsNull['LineaCondicionado'] = new Array();
arrCampoLeftJoin['LineaCondicionado'] = new Array();
arrCampoRestrictions['LineaCondicionado'] = new Array();
arrValorRestrictions['LineaCondicionado'] = new Array();
arrOperadorRestrictions['LineaCondicionado'] = new Array();
arrTipoValorRestrictions['LineaCondicionado'] = new Array();
arrValorDependeGenerico['LineaCondicionado'] = new Array();
arrValorDependeOrdenGenerico['LineaCondicionado'] = new Array();
// FIN Lupa de Lineas del condicionado

// INICIO Lupa de Lineas
arrObjetosLupas['Linea'] = "com.rsi.agp.dao.tables.poliza.Linea";
arrCamposFiltrosLupas['Linea'] = new Array('filtro_codLinea', 'filtro_Linea');
arrCamposBeanFiltros['Linea'] = new Array('codlinea', 'nomlinea');
arrTipoCamposBeanFiltros['Linea'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['Linea'] = new Array('Plan', 'Cod. L\u00EDnea', 'Nombre');
arrColumnWidth['Linea'] = new Array('20%', '20%', '60%');
arrCamposBean['Linea'] = new Array('codplan', 'codlinea', 'nomlinea');
arrCamposDepende['Linea'] = new Array('plan', 'linea');
arrCamposBeanDepende['Linea'] = new Array('codplan', 'codlinea');
arrTipoBeanDepende['Linea'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['Linea'] = new Array('plan', 'linea', 'desc_linea');
arrCamposBeanDevolver['Linea'] = new Array('codplan', 'codlinea', 'nomlinea');
arrColumnasDistinct['Linea'] = new Array();
arrColumnasIsNull['Linea'] = new Array();
arrCampoLeftJoin['Linea'] = new Array();
arrCampoRestrictions['Linea'] = new Array();
arrValorRestrictions['Linea'] = new Array();
arrOperadorRestrictions['Linea'] = new Array();
arrTipoValorRestrictions['Linea'] = new Array();
arrValorDependeGenerico['Linea'] = new Array();
arrValorDependeOrdenGenerico['Linea'] = new Array();
// FIN Lupa de Lineas

//INICIO Lupa de Lineas Configurables
arrObjetosLupas['LineaConfigurables'] = "com.rsi.agp.dao.tables.poliza.Linea";
arrCamposFiltrosLupas['LineaConfigurables'] = new Array('filtro_codLineaConfigurables', 'filtro_LineaConfigurables');
arrCamposBeanFiltros['LineaConfigurables'] = new Array('codlinea', 'nomlinea');
arrTipoCamposBeanFiltros['LineaConfigurables'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['LineaConfigurables'] = new Array('Plan', 'Cod. L\u00EDnea', 'Nombre');
arrColumnWidth['LineaConfigurables'] = new Array('20%', '20%', '60%');
arrCamposBean['LineaConfigurables'] = new Array('codplan', 'codlinea', 'nomlinea');
arrCamposDepende['LineaConfigurables'] = new Array('sl_planes', 'sl_lineas');
arrCamposBeanDepende['LineaConfigurables'] = new Array('codplan', 'codlinea');
arrTipoBeanDepende['LineaConfigurables'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['LineaConfigurables'] = new Array('sl_planes', 'sl_lineas', 'desc_linea');
arrCamposBeanDevolver['LineaConfigurables'] = new Array('codplan', 'codlinea', 'nomlinea');
arrColumnasDistinct['LineaConfigurables'] = new Array();
arrColumnasIsNull['LineaConfigurables'] = new Array();
arrCampoLeftJoin['LineaConfigurables'] = new Array();
arrCampoRestrictions['LineaConfigurables'] = new Array();
arrValorRestrictions['LineaConfigurables'] = new Array();
arrOperadorRestrictions['LineaConfigurables'] = new Array();
arrTipoValorRestrictions['LineaConfigurables'] = new Array(); 
arrValorDependeGenerico['LineaConfigurables'] = new Array();
arrValorDependeOrdenGenerico['LineaConfigurables'] = new Array();
// FIN Lupa de Lineas Configurables

// INICIO Lupa de LineaReplicaOrigen
arrObjetosLupas['LineaReplicaOrigen'] = "com.rsi.agp.dao.tables.poliza.Linea";
arrCamposFiltrosLupas['LineaReplicaOrigen'] = new Array('filtro_codLineaReplicaOrigen', 'filtro_LineaReplicaOrigen');
arrCamposBeanFiltros['LineaReplicaOrigen'] = new Array('codlinea', 'nomlinea');
arrTipoCamposBeanFiltros['LineaReplicaOrigen'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['LineaReplicaOrigen'] = new Array('Plan', 'Cod. L\u00EDnea', 'Nombre');
arrColumnWidth['LineaReplicaOrigen'] = new Array('20%', '20%', '60%');
arrCamposBean['LineaReplicaOrigen'] = new Array('codplan', 'codlinea', 'nomlinea');
arrCamposDepende['LineaReplicaOrigen'] = new Array('plan_re', 'linea_re');
arrCamposBeanDepende['LineaReplicaOrigen'] = new Array('codplan', 'codlinea');
arrTipoBeanDepende['LineaReplicaOrigen'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['LineaReplicaOrigen'] = new Array('plan_re', 'linea_re', 'desc_linea_re');
arrCamposBeanDevolver['LineaReplicaOrigen'] = new Array('codplan', 'codlinea', 'nomlinea');
arrColumnasDistinct['LineaReplicaOrigen'] = new Array();
arrColumnasIsNull['LineaReplicaOrigen'] = new Array();
arrCampoLeftJoin['LineaReplicaOrigen'] = new Array();
arrCampoRestrictions['LineaReplicaOrigen'] = new Array();
arrValorRestrictions['LineaReplicaOrigen'] = new Array();
arrOperadorRestrictions['LineaReplicaOrigen'] = new Array();
arrTipoValorRestrictions['LineaReplicaOrigen'] = new Array();
arrValorDependeGenerico['LineaReplicaOrigen'] = new Array();
arrValorDependeOrdenGenerico['LineaReplicaOrigen'] = new Array();
// FIN Lupa de LineaReplicaOrigen

// INICIO Lupa de LineaReplicaDestino
arrObjetosLupas['LineaReplica'] = "com.rsi.agp.dao.tables.poliza.Linea";
arrCamposFiltrosLupas['LineaReplica'] = new Array('filtro_codLineaReplica', 'filtro_LineaReplica');
arrCamposBeanFiltros['LineaReplica'] = new Array('codlinea', 'nomlinea');
arrTipoCamposBeanFiltros['LineaReplica'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['LineaReplica'] = new Array('Plan', 'Cod. L\u00EDnea', 'Nombre');
arrColumnWidth['LineaReplica'] = new Array('20%', '20%', '60%');
arrCamposBean['LineaReplica'] = new Array('codplan', 'codlinea', 'nomlinea');
arrCamposDepende['LineaReplica'] = new Array('planreplica', 'lineareplica');
arrCamposBeanDepende['LineaReplica'] = new Array('codplan', 'codlinea');
arrTipoBeanDepende['LineaReplica'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['LineaReplica'] = new Array('planreplica', 'lineareplica', 'desc_lineareplica');
arrCamposBeanDevolver['LineaReplica'] = new Array('codplan', 'codlinea', 'nomlinea');
arrColumnasDistinct['LineaReplica'] = new Array();
arrColumnasIsNull['LineaReplica'] = new Array();
arrCampoLeftJoin['LineaReplica'] = new Array();
arrCampoRestrictions['LineaReplica'] = new Array();
arrValorRestrictions['LineaReplica'] = new Array();
arrOperadorRestrictions['LineaReplica'] = new Array();
arrTipoValorRestrictions['LineaReplica'] = new Array();
arrValorDependeGenerico['LineaReplica'] = new Array();
arrValorDependeOrdenGenerico['LineaReplica'] = new Array();
// FIN Lupa de LineaReplicaDestino


//INICIO Lupa de Alta Renovables
arrObjetosLupas['LineaAltaRen'] = "com.rsi.agp.dao.tables.poliza.Linea";
arrCamposFiltrosLupas['LineaAltaRen'] = new Array('filtro_codLineaAltaRen', 'filtro_LineaAltaRen');
arrCamposBeanFiltros['LineaAltaRen'] = new Array('codlinea', 'nomlinea');
arrTipoCamposBeanFiltros['LineaAltaRen'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['LineaAltaRen'] = new Array('Plan', 'Cod. L\u00EDnea', 'Nombre');
arrColumnWidth['LineaAltaRen'] = new Array('20%', '20%', '60%');
arrCamposBean['LineaAltaRen'] = new Array('codplan', 'codlinea', 'nomlinea');
arrCamposDepende['LineaAltaRen'] = new Array('plan_renov', 'linea_renov');
arrCamposBeanDepende['LineaAltaRen'] = new Array('codplan', 'codlinea');
arrTipoBeanDepende['LineaAltaRen'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['LineaAltaRen'] = new Array('plan_renov', 'linea_renov', 'desc_linea_renov');
arrCamposBeanDevolver['LineaAltaRen'] = new Array('codplan', 'codlinea', 'nomlinea');
arrColumnasDistinct['LineaAltaRen'] = new Array();
arrColumnasIsNull['LineaAltaRen'] = new Array();
arrCampoLeftJoin['LineaAltaRen'] = new Array();
arrCampoRestrictions['LineaAltaRen'] = new Array();
arrValorRestrictions['LineaAltaRen'] = new Array();
arrOperadorRestrictions['LineaAltaRen'] = new Array();
arrTipoValorRestrictions['LineaAltaRen'] = new Array();
arrValorDependeGenerico['LineaAltaRen'] = new Array();
arrValorDependeOrdenGenerico['LineaAltaRen'] = new Array();
// FIN Lupa de LineaReplicaDestino


// INICIO Lupa de Lineas de Sobreprecio
arrObjetosLupas['LineaSbp'] = "com.rsi.agp.dao.tables.poliza.Linea";
arrCamposFiltrosLupas['LineaSbp'] = new Array('filtro_codLineaSbp', 'filtro_LineaSbp');
arrCamposBeanFiltros['LineaSbp'] = new Array('codlinea', 'nomlinea');
arrTipoCamposBeanFiltros['LineaSbp'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['LineaSbp'] = new Array('Plan', 'Cod. L\u00EDnea', 'Nombre');
arrColumnWidth['LineaSbp'] = new Array('20%', '20%', '60%');
arrCamposBean['LineaSbp'] = new Array('codplan', 'codlinea', 'nomlinea');
arrCamposDepende['LineaSbp'] = new Array('plan', 'linea', 'listaLineasSbp');
arrCamposBeanDepende['LineaSbp'] = new Array('codplan', 'codlinea', 'lineaseguroid');
arrTipoBeanDepende['LineaSbp'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.Long');
arrCamposDevolver['LineaSbp'] = new Array('plan', 'linea', 'desc_linea');
arrCamposBeanDevolver['LineaSbp'] = new Array('codplan', 'codlinea', 'nomlinea');
arrColumnasDistinct['LineaSbp'] = new Array();
arrColumnasIsNull['LineaSbp'] = new Array();
arrCampoLeftJoin['LineaSbp'] = new Array();
arrCampoRestrictions['LineaSbp'] = new Array();
arrValorRestrictions['LineaSbp'] = new Array();
arrOperadorRestrictions['LineaSbp'] = new Array();
arrTipoValorRestrictions['LineaSbp'] = new Array(); 
arrValorDependeGenerico['LineaSbp'] = new Array();
arrValorDependeOrdenGenerico['LineaSbp'] = new Array();
// FIN Lupa de Lineas de Sobreprecio

//INICIO Lupa de Lineas >= o < 2015
arrObjetosLupas['LineaOpe'] = "com.rsi.agp.dao.tables.poliza.Linea";
arrCamposFiltrosLupas['LineaOpe'] = new Array('filtro_codLineaOpe', 'filtro_LineaOpe');
arrCamposBeanFiltros['LineaOpe'] = new Array('codlinea', 'nomlinea');
arrTipoCamposBeanFiltros['LineaOpe'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['LineaOpe'] = new Array('Plan', 'Cod. L\u00EDnea', 'Nombre');
arrColumnWidth['LineaOpe'] = new Array('20%', '20%', '60%');
arrCamposBean['LineaOpe'] = new Array('codplan', 'codlinea', 'nomlinea');
arrCamposDepende['LineaOpe'] = new Array('plan', 'linea');
arrCamposBeanDepende['LineaOpe'] = new Array('codplan', 'codlinea');
arrTipoBeanDepende['LineaOpe'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['LineaOpe'] = new Array('plan', 'linea', 'desc_linea');
arrCamposBeanDevolver['LineaOpe'] = new Array('codplan', 'codlinea', 'nomlinea');
arrColumnasDistinct['LineaOpe'] = new Array();
arrColumnasIsNull['LineaOpe'] = new Array();
arrCampoLeftJoin['LineaOpe'] = new Array();
arrCampoRestrictions['LineaOpe'] = new Array('campoRestriccion');
arrValorRestrictions['LineaOpe'] = new Array('valorRestriccion');
arrOperadorRestrictions['LineaOpe'] = new Array('operadorRestriccion');
arrTipoValorRestrictions['LineaOpe'] = new Array('java.math.BigDecimal');
arrValorDependeGenerico['LineaOpe'] = new Array();
arrValorDependeOrdenGenerico['LineaOpe'] = new Array();

// INICIO Lupa de Clases
arrObjetosLupas['Clase'] = "com.rsi.agp.dao.tables.admin.Clase";
arrCamposFiltrosLupas['Clase'] = new Array('filtro_codClase', 'filtro_Clase');
arrCamposBeanFiltros['Clase'] = new Array('clase', 'descripcion');
arrTipoCamposBeanFiltros['Clase'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['Clase'] = new Array('Plan', 'Cod. L\u00EDnea', 'Nom. L\u00EDnea', 'Cod. Clase', 'Nom. Clase');
arrColumnWidth['Clase'] = new Array('10%', '12%', '26%', '12%', '40%');
arrCamposBean['Clase'] = new Array('linea.codplan', 'linea.codlinea', 'linea.nomlinea', 'clase', 'descripcion');
arrCamposDepende['Clase'] = new Array('plan', 'linea', 'clase');
arrCamposBeanDepende['Clase'] = new Array('linea.codplan', 'linea.codlinea', 'clase');
arrTipoBeanDepende['Clase'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['Clase'] = new Array('plan', 'linea', 'desc_linea', 'clase', 'descripcion');
arrCamposBeanDevolver['Clase'] = new Array('linea.codplan', 'linea.codlinea', 'linea.nomlinea', 'clase', 'descripcion');
arrColumnasDistinct['Clase'] = new Array();
arrColumnasIsNull['Clase'] = new Array();
arrCampoLeftJoin['Clase'] = new Array();
arrCampoRestrictions['Clase'] = new Array();
arrValorRestrictions['Clase'] = new Array();
arrOperadorRestrictions['Clase'] = new Array();
arrTipoValorRestrictions['Clase'] = new Array();
arrValorDependeGenerico['Clase'] = new Array();
arrValorDependeOrdenGenerico['Clase'] = new Array();
// FIN Lupa de Clases

// INICIO Lupa de Clases de sobreprecio
arrObjetosLupas['ClaseSbp'] = "com.rsi.agp.dao.tables.admin.Clase";
arrCamposFiltrosLupas['ClaseSbp'] = new Array('filtro_codClaseSbp', 'filtro_ClaseSbp');
arrCamposBeanFiltros['ClaseSbp'] = new Array('clase', 'descripcion');
arrTipoCamposBeanFiltros['ClaseSbp'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['ClaseSbp'] = new Array('Plan', 'Cod. L\u00EDnea', 'Nom. L\u00EDnea', 'Cod. Clase', 'Nom. Clase');
arrColumnWidth['ClaseSbp'] = new Array('10%', '12%', '26%', '12%', '40%');
arrCamposBean['ClaseSbp'] = new Array('linea.codplan', 'linea.codlinea', 'linea.nomlinea', 'clase', 'descripcion');
arrCamposDepende['ClaseSbp'] = new Array('plan', 'linea', 'clase', 'listaLineasSbp');
arrCamposBeanDepende['ClaseSbp'] = new Array('linea.codplan', 'linea.codlinea', 'clase', 'linea.lineaseguroid');
arrTipoBeanDepende['ClaseSbp'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.Long');
arrCamposDevolver['ClaseSbp'] = new Array('plan', 'linea', 'desc_linea', 'clase');
arrCamposBeanDevolver['ClaseSbp'] = new Array('linea.codplan', 'linea.codlinea', 'linea.nomlinea', 'clase');
arrColumnasDistinct['ClaseSbp'] = new Array();
arrColumnasIsNull['ClaseSbp'] = new Array();
arrCampoLeftJoin['ClaseSbp'] = new Array();
arrCampoRestrictions['ClaseSbp'] = new Array();
arrValorRestrictions['ClaseSbp'] = new Array();
arrOperadorRestrictions['ClaseSbp'] = new Array();
arrTipoValorRestrictions['ClaseSbp'] = new Array();
arrValorDependeGenerico['ClaseSbp'] = new Array();
arrValorDependeOrdenGenerico['ClaseSbp'] = new Array();
// FIN Lupa de Clases de sobreprecio

// INICIO Lupa de Entidades
arrObjetosLupas['Entidad'] = "com.rsi.agp.dao.tables.admin.Entidad";
arrCamposFiltrosLupas['Entidad'] = new Array('filtro_codEntidad', 'filtro_Entidad');
arrCamposBeanFiltros['Entidad'] = new Array('codentidad', 'nomentidad');
arrTipoCamposBeanFiltros['Entidad'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['Entidad'] = new Array('C\u00F3digo', 'Nombre');
arrColumnWidth['Entidad'] = new Array('30%', '70%');
arrCamposBean['Entidad'] = new Array('codentidad', 'nomentidad');
arrCamposDepende['Entidad'] = new Array('grupoEntidades', 'entidad');
arrCamposBeanDepende['Entidad'] = new Array('codentidad', 'codentidad');
arrTipoBeanDepende['Entidad'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['Entidad'] = new Array('entidad', 'desc_entidad');
arrCamposBeanDevolver['Entidad'] = new Array('codentidad', 'nomentidad');
arrColumnasDistinct['Entidad'] = new Array();
arrColumnasIsNull['Entidad'] = new Array();
arrCampoLeftJoin['Entidad'] = new Array();
arrCampoRestrictions['Entidad'] = new Array();
arrValorRestrictions['Entidad'] = new Array();
arrOperadorRestrictions['Entidad'] = new Array();
arrTipoValorRestrictions['Entidad'] = new Array(); 
arrValorDependeGenerico['Entidad'] = new Array();
arrValorDependeOrdenGenerico['Entidad'] = new Array();
// FIN Lupa de Entidades

//INICIO Lupa de Entidades
arrObjetosLupas['EntidadCM'] = "com.rsi.agp.dao.tables.admin.Entidad";
arrCamposFiltrosLupas['EntidadCM'] = new Array('filtro_codEntidadCM', 'filtro_EntidadCM');
arrCamposBeanFiltros['EntidadCM'] = new Array('codentidad', 'nomentidad');
arrTipoCamposBeanFiltros['EntidadCM'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['EntidadCM'] = new Array('C\u00F3digo', 'Nombre');
arrColumnWidth['EntidadCM'] = new Array('30%', '70%');
arrCamposBean['EntidadCM'] = new Array('codentidad', 'nomentidad');
arrCamposDepende['EntidadCM'] = new Array('grupoEntidades', 'entidad_cm');
arrCamposBeanDepende['EntidadCM'] = new Array('codentidad', 'codentidad');
arrTipoBeanDepende['EntidadCM'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['EntidadCM'] = new Array('entidad_cm', 'desc_entidad_cm');
arrCamposBeanDevolver['EntidadCM'] = new Array('codentidad', 'nomentidad');
arrColumnasDistinct['EntidadCM'] = new Array();
arrColumnasIsNull['EntidadCM'] = new Array();
arrCampoLeftJoin['EntidadCM'] = new Array();
arrCampoRestrictions['EntidadCM'] = new Array();
arrValorRestrictions['EntidadCM'] = new Array();
arrOperadorRestrictions['EntidadCM'] = new Array();
arrTipoValorRestrictions['EntidadCM'] = new Array();
arrValorDependeGenerico['EntidadCM'] = new Array();
arrValorDependeOrdenGenerico['EntidadCM'] = new Array();
// FIN Lupa de Entidades

// INICIO Lupa de Oficinas
arrObjetosLupas['Oficina'] = "com.rsi.agp.dao.tables.commons.Oficina";
arrCamposFiltrosLupas['Oficina'] = new Array('filtro_codOficina', 'filtro_Oficina');
arrCamposBeanFiltros['Oficina'] = new Array('id.codoficina', 'nomoficina');
arrTipoCamposBeanFiltros['Oficina'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['Oficina'] = new Array('Cod. Entidad', 'Nom. Entidad', 'Cod. Oficina', 'Nom. Oficina');
arrColumnWidth['Oficina'] = new Array('15%', '35%', '15%', '35%');
arrCamposBean['Oficina'] = new Array('id.codentidad', 'entidad.nomentidad', 'id.codoficina', 'nomoficina');
arrCamposDepende['Oficina'] = new Array('grupoEntidades', 'entidad', 'oficina','grupoOficinas');
arrCamposBeanDepende['Oficina'] = new Array('id.codentidad', 'id.codentidad', 'id.codoficina','id.codoficina');
arrTipoBeanDepende['Oficina'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['Oficina'] = new Array('entidad', 'desc_entidad', 'oficina', 'desc_oficina');
arrCamposBeanDevolver['Oficina'] = new Array('id.codentidad', 'entidad.nomentidad', 'id.codoficina', 'nomoficina');
arrColumnasDistinct['Oficina'] = new Array();
arrColumnasIsNull['Oficina'] = new Array();
arrCampoLeftJoin['Oficina'] = new Array();
arrCampoRestrictions['Oficina'] = new Array();
arrValorRestrictions['Oficina'] = new Array();
arrOperadorRestrictions['Oficina'] = new Array();
arrTipoValorRestrictions['Oficina'] = new Array();
arrValorDependeGenerico['Oficina'] = new Array();
arrValorDependeOrdenGenerico['Oficina'] = new Array();
// FIN Lupa de Oficinas

//INICIO Lupa de Oficinas CM
arrObjetosLupas['OficinaCM'] = "com.rsi.agp.dao.tables.commons.Oficina";
arrCamposFiltrosLupas['OficinaCM'] = new Array('filtro_codOficinaCM', 'filtro_OficinaCM');
arrCamposBeanFiltros['OficinaCM'] = new Array('id.codoficina', 'nomoficina');
arrTipoCamposBeanFiltros['OficinaCM'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['OficinaCM'] = new Array('Cod. Entidad', 'Nom. Entidad', 'Cod. Oficina', 'Nom. Oficina');
arrColumnWidth['OficinaCM'] = new Array('15%', '35%', '15%', '35%');
arrCamposBean['OficinaCM'] = new Array('id.codentidad', 'entidad.nomentidad', 'id.codoficina', 'nomoficina');
arrCamposDepende['OficinaCM'] = new Array('grupoEntidades', 'entidad_cm', 'oficina_cm');
arrCamposBeanDepende['OficinaCM'] = new Array('id.codentidad', 'id.codentidad', 'id.codoficina');
arrTipoBeanDepende['OficinaCM'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['OficinaCM'] = new Array('entidad_cm', 'desc_entidad_cm', 'oficina_cm', 'desc_oficina_cm');
arrCamposBeanDevolver['OficinaCM'] = new Array('id.codentidad', 'entidad.nomentidad', 'id.codoficina', 'nomoficina');
arrColumnasDistinct['OficinaCM'] = new Array();
arrColumnasIsNull['OficinaCM'] = new Array();
arrCampoLeftJoin['OficinaCM'] = new Array();
arrCampoRestrictions['OficinaCM'] = new Array();
arrValorRestrictions['OficinaCM'] = new Array();
arrOperadorRestrictions['OficinaCM'] = new Array(); 
arrTipoValorRestrictions['OficinaCM'] = new Array(); 
arrValorDependeGenerico['OficinaCM'] = new Array();
arrValorDependeOrdenGenerico['OficinaCM'] = new Array();
// FIN Lupa de Oficinas CM

// INICIO Lupa de Cambio de Oficinas
arrObjetosLupas['CambiarOficina'] = "com.rsi.agp.dao.tables.commons.Oficina";
arrCamposFiltrosLupas['CambiarOficina'] = new Array('filtro_CambiarOficina');
arrCamposBeanFiltros['CambiarOficina'] = new Array('nomoficina');
arrTxtCabecerasLupas['CambiarOficina'] = new Array('Cod. Entidad', 'Nom. Entidad', 'Cod. Oficina', 'Nom. Oficina');
arrColumnWidth['CambiarOficina'] = new Array('15%', '35%', '15%', '35%');
arrCamposBean['CambiarOficina'] = new Array('id.codentidad', 'entidad.nomentidad', 'id.codoficina', 'nomoficina');
arrCamposDepende['CambiarOficina'] = new Array('entCambioOficina');
arrCamposBeanDepende['CambiarOficina'] = new Array('id.codentidad');
arrTipoBeanDepende['CambiarOficina'] = new Array('java.math.BigDecimal');
arrCamposDevolver['CambiarOficina'] = new Array('codentCO', 'nomentCO', 'codoficinaCO', 'descoficinaCO');
arrCamposBeanDevolver['CambiarOficina'] = new Array('id.codentidad', 'entidad.nomentidad', 'id.codoficina', 'nomoficina');
arrColumnasDistinct['CambiarOficina'] = new Array();
arrColumnasIsNull['CambiarOficina'] = new Array();
arrCampoLeftJoin['CambiarOficina'] = new Array();
arrCampoRestrictions['CambiarOficina'] = new Array();
arrValorRestrictions['CambiarOficina'] = new Array();
arrOperadorRestrictions['CambiarOficina'] = new Array();
arrTipoValorRestrictions['CambiarOficina'] = new Array(); 
arrValorDependeGenerico['CambiarOficina'] = new Array();
arrValorDependeOrdenGenerico['CambiarOficina'] = new Array();
// FIN Lupa de Cambio de Oficinas

// INICIO Lupa de Entidades Mediadoras
arrObjetosLupas['EntidadMediadora'] = "com.rsi.agp.dao.tables.admin.EntidadMediadora";
arrCamposFiltrosLupas['EntidadMediadora'] = new Array('filtro_codEntidadMediadora', 'filtro_EntidadMediadora');
arrCamposBeanFiltros['EntidadMediadora'] = new Array('codentidad', 'nomentidad');
arrTipoCamposBeanFiltros['EntidadMediadora'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['EntidadMediadora'] = new Array('C\u00F3digo', 'Nombre');
arrColumnWidth['EntidadMediadora'] = new Array('30%', '70%');
arrCamposBean['EntidadMediadora'] = new Array('codentidad', 'nomentidad');
arrCamposDepende['EntidadMediadora'] = new Array('entidadSubstr', 'entmediadora');
arrCamposBeanDepende['EntidadMediadora'] = new Array('codentidad', 'codentidad');
arrTipoBeanDepende['EntidadMediadora'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['EntidadMediadora'] = new Array('entmediadora');
arrCamposBeanDevolver['EntidadMediadora'] = new Array('codentidad');
arrColumnasDistinct['EntidadMediadora'] = new Array();
arrColumnasIsNull['EntidadMediadora'] = new Array();
arrCampoLeftJoin['EntidadMediadora'] = new Array();
arrCampoRestrictions['EntidadMediadora'] = new Array();
arrValorRestrictions['EntidadMediadora'] = new Array();
arrOperadorRestrictions['EntidadMediadora'] = new Array();
arrTipoValorRestrictions['EntidadMediadora'] = new Array(); 
arrValorDependeGenerico['EntidadMediadora'] = new Array();
arrValorDependeOrdenGenerico['EntidadMediadora'] = new Array();
// FIN Lupa de Entidades Mediadoras

//INICIO Lupa de Entidades Mediadoras CM
arrObjetosLupas['EntidadMediadoraCM'] = "com.rsi.agp.dao.tables.admin.EntidadMediadora";
arrCamposFiltrosLupas['EntidadMediadoraCM'] = new Array('filtro_codEntidadMediadoraCM', 'filtro_EntidadMediadoraCM');
arrCamposBeanFiltros['EntidadMediadoraCM'] = new Array('codentidad', 'nomentidad');
arrTipoCamposBeanFiltros['EntidadMediadoraCM'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['EntidadMediadoraCM'] = new Array('C\u00F3digo', 'Nombre');
arrColumnWidth['EntidadMediadoraCM'] = new Array('30%', '70%');
arrCamposBean['EntidadMediadoraCM'] = new Array('codentidad', 'nomentidad');
arrCamposDepende['EntidadMediadoraCM'] = new Array('entidadSubstr_cm', 'entmediadora_cm');
arrCamposBeanDepende['EntidadMediadoraCM'] = new Array('codentidad', 'codentidad');
arrTipoBeanDepende['EntidadMediadoraCM'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['EntidadMediadoraCM'] = new Array('entmediadora_cm');
arrCamposBeanDevolver['EntidadMediadoraCM'] = new Array('codentidad');
arrColumnasDistinct['EntidadMediadoraCM'] = new Array();
arrColumnasIsNull['EntidadMediadoraCM'] = new Array();
arrCampoLeftJoin['EntidadMediadoraCM'] = new Array();
arrCampoRestrictions['EntidadMediadoraCM'] = new Array();
arrValorRestrictions['EntidadMediadoraCM'] = new Array();
arrOperadorRestrictions['EntidadMediadoraCM'] = new Array();
arrTipoValorRestrictions['EntidadMediadoraCM'] = new Array(); 
arrValorDependeGenerico['EntidadMediadoraCM'] = new Array();
arrValorDependeOrdenGenerico['EntidadMediadoraCM'] = new Array();
// FIN Lupa de Entidades Mediadoras CM

//INICIO Lupa de Entidades
arrObjetosLupas['EntidadCM'] = "com.rsi.agp.dao.tables.admin.Entidad";
arrCamposFiltrosLupas['EntidadCM'] = new Array('filtro_EntidadCM');
arrCamposBeanFiltros['EntidadCM'] = new Array('nomentidad');
arrTxtCabecerasLupas['EntidadCM'] = new Array('C\u00F3digo', 'Nombre');
arrColumnWidth['EntidadCM'] = new Array('30%', '70%');
arrCamposBean['EntidadCM'] = new Array('codentidad', 'nomentidad');
arrCamposDepende['EntidadCM'] = new Array('grupoEntidades', 'entidad_cm');
arrCamposBeanDepende['EntidadCM'] = new Array('codentidad', 'codentidad');
arrTipoBeanDepende['EntidadCM'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['EntidadCM'] = new Array('entidad_cm', 'desc_entidad_cm');
arrCamposBeanDevolver['EntidadCM'] = new Array('codentidad', 'nomentidad');
arrColumnasDistinct['EntidadCM'] = new Array();
arrColumnasIsNull['EntidadCM'] = new Array();
arrCampoLeftJoin['EntidadCM'] = new Array();
arrCampoRestrictions['EntidadCM'] = new Array();
arrValorRestrictions['EntidadCM'] = new Array();
arrOperadorRestrictions['EntidadCM'] = new Array();
arrTipoValorRestrictions['EntidadCM'] = new Array(); 
arrValorDependeGenerico['EntidadCM'] = new Array();
arrValorDependeOrdenGenerico['EntidadCM'] = new Array();
// FIN Lupa de Entidades


// INICIO Lupa de Subentidades Mediadoras
arrObjetosLupas['SubentidadMediadora'] = "com.rsi.agp.dao.tables.admin.SubentidadMediadora";
arrCamposFiltrosLupas['SubentidadMediadora'] = new Array('filtro_codSubentidadMediadora', 'filtro_SubentidadMediadora');
arrCamposBeanFiltros['SubentidadMediadora'] = new Array('id.codsubentidad', 'nomsubentidad');
arrTipoCamposBeanFiltros['SubentidadMediadora'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['SubentidadMediadora'] = new Array('Cod. Ent. Med.', 'Nom. Ent. Mediadora', 'Cod. Subentidad', 'Nom Subentidad');
arrColumnWidth['SubentidadMediadora'] = new Array('17%', '33%', '17%', '33%');
arrCamposBean['SubentidadMediadora'] = new Array('entidadMediadora.codentidad', 'entidadMediadora.nomentidad', 'id.codsubentidad', 'nomSubentidadCompleto');
arrCamposDepende['SubentidadMediadora'] = new Array('entmediadora', 'entidadSubstr');
arrCamposBeanDepende['SubentidadMediadora'] = new Array('entidadMediadora.codentidad', 'entidadMediadora.codentidad');
arrTipoBeanDepende['SubentidadMediadora'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['SubentidadMediadora'] = new Array('entmediadora', 'subentmediadora', 'desc_subentmediadora');
arrCamposBeanDevolver['SubentidadMediadora'] = new Array('entidadMediadora.codentidad', 'id.codsubentidad', 'nomSubentidadCompleto');
arrColumnasDistinct['SubentidadMediadora'] = new Array();
arrColumnasIsNull['SubentidadMediadora'] = new Array();
arrCampoLeftJoin['SubentidadMediadora'] = new Array();
arrCampoRestrictions['SubentidadMediadora'] = new Array();
arrValorRestrictions['SubentidadMediadora'] = new Array();
arrOperadorRestrictions['SubentidadMediadora'] = new Array();
arrTipoValorRestrictions['SubentidadMediadora'] = new Array(); 
arrValorDependeGenerico['SubentidadMediadora'] = new Array();
arrValorDependeOrdenGenerico['SubentidadMediadora'] = new Array();
// FIN Lupa de Subentidades Mediadoras

//INICIO Lupa de Subentidades Mediadoras con fecha baja is null
/* GDLD-78692 ** MODIF TAM (28.12.2021) * Resoluci�n Defecto N�8 */
arrObjetosLupas['SubentidadMediadoraFiltroFecha'] = "com.rsi.agp.dao.tables.admin.SubentidadMediadora";
arrCamposFiltrosLupas['SubentidadMediadoraFiltroFecha'] = new Array('filtro_codEntidadMediadoraFiltroFecha', 'filtro_codSubentidadMediadoraFiltroFecha', 'filtro_SubentidadMediadoraFiltroFecha');
arrCamposBeanFiltros['SubentidadMediadoraFiltroFecha'] = new Array('id.codentidad', 'id.codsubentidad', 'nomsubentidad');
arrTipoCamposBeanFiltros['SubentidadMediadoraFiltroFecha'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['SubentidadMediadoraFiltroFecha'] = new Array('Cod. Ent. Med.', 'Nom. Ent. Mediadora', 'Cod. Subentidad', 'Nom Subentidad');
arrColumnWidth['SubentidadMediadoraFiltroFecha'] = new Array('17%', '33%', '17%', '33%');
arrCamposBean['SubentidadMediadoraFiltroFecha'] = new Array('entidadMediadora.codentidad', 'entidadMediadora.nomentidad', 'id.codsubentidad', 'nomSubentidadCompleto');
arrCamposDepende['SubentidadMediadoraFiltroFecha'] = new Array('entmediadora', 'entidad', 'subentmediadora');
arrCamposBeanDepende['SubentidadMediadoraFiltroFecha'] = new Array('entidadMediadora.codentidad', 'entidad.codentidad', 'id.codsubentidad');
arrTipoBeanDepende['SubentidadMediadoraFiltroFecha'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['SubentidadMediadoraFiltroFecha'] = new Array('entmediadora', 'subentmediadora', 'desc_subentmediadora');
arrCamposBeanDevolver['SubentidadMediadoraFiltroFecha'] = new Array('entidadMediadora.codentidad', 'id.codsubentidad', 'nomSubentidadCompleto');
arrColumnasDistinct['SubentidadMediadoraFiltroFecha'] = new Array();
arrColumnasIsNull['SubentidadMediadoraFiltroFecha'] = new Array('fechabaja');
arrCampoLeftJoin['SubentidadMediadoraFiltroFecha'] = new Array();
arrCampoRestrictions['SubentidadMediadoraFiltroFecha'] = new Array();
arrValorRestrictions['SubentidadMediadoraFiltroFecha'] = new Array();
arrOperadorRestrictions['SubentidadMediadoraFiltroFecha'] = new Array();
arrTipoValorRestrictions['SubentidadMediadoraFiltroFecha'] = new Array(); 
arrValorDependeGenerico['SubentidadMediadoraFiltroFecha'] = new Array();
arrValorDependeOrdenGenerico['SubentidadMediadoraFiltroFecha'] = new Array();
// FIN Lupa de Subentidades Mediadoras con fecha baja is null

//INICIO Lupa de Subentidades Mediadoras CM con fecha baja is null 
arrObjetosLupas['SubentidadMediadoraFiltroFechaCM'] = "com.rsi.agp.dao.tables.admin.SubentidadMediadora";
arrCamposFiltrosLupas['SubentidadMediadoraFiltroFechaCM'] = new Array('filtro_codSubentidadMediadoraFiltroFechaCM', 'filtro_SubentidadMediadoraFiltroFechaCM');
arrCamposBeanFiltros['SubentidadMediadoraFiltroFechaCM'] = new Array('id.codsubentidad', 'nomsubentidad');
arrTipoCamposBeanFiltros['SubentidadMediadoraFiltroFechaCM'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['SubentidadMediadoraFiltroFechaCM'] = new Array('Cod. Ent. Med.', 'Nom. Ent. Mediadora', 'Cod. Subentidad', 'Nom Subentidad');
arrColumnWidth['SubentidadMediadoraFiltroFechaCM'] = new Array('17%', '33%', '17%', '33%');
arrCamposBean['SubentidadMediadoraFiltroFechaCM'] = new Array('entidadMediadora.codentidad', 'entidadMediadora.nomentidad', 'id.codsubentidad', 'nomSubentidadCompleto');
arrCamposDepende['SubentidadMediadoraFiltroFechaCM'] = new Array('entmediadora_cm', 'entidad_cm', 'subentmediadora_cm');
arrCamposBeanDepende['SubentidadMediadoraFiltroFechaCM'] = new Array('entidadMediadora.codentidad', 'entidad.codentidad', 'id.codsubentidad');
arrTipoBeanDepende['SubentidadMediadoraFiltroFechaCM'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['SubentidadMediadoraFiltroFechaCM'] = new Array('entmediadora_cm', 'subentmediadora_cm', 'desc_subentmediadora_cm');
arrCamposBeanDevolver['SubentidadMediadoraFiltroFechaCM'] = new Array('entidadMediadora.codentidad', 'id.codsubentidad', 'nomSubentidadCompleto');
arrColumnasDistinct['SubentidadMediadoraFiltroFechaCM'] = new Array();
arrColumnasIsNull['SubentidadMediadoraFiltroFechaCM'] = new Array('fechabaja');
arrCampoLeftJoin['SubentidadMediadoraFiltroFechaCM'] = new Array();
arrCampoRestrictions['SubentidadMediadoraFiltroFechaCM'] = new Array();
arrValorRestrictions['SubentidadMediadoraFiltroFechaCM'] = new Array();
arrOperadorRestrictions['SubentidadMediadoraFiltroFechaCM'] = new Array();
arrTipoValorRestrictions['SubentidadMediadoraFiltroFechaCM'] = new Array(); 
arrValorDependeGenerico['SubentidadMediadoraFiltroFechaCM'] = new Array();
arrValorDependeOrdenGenerico['SubentidadMediadoraFiltroFechaCM'] = new Array();
// FIN Lupa de Subentidades Mediadoras CM con fecha baja is null

// INICIO Lupa de Vias
arrObjetosLupas['Via'] = "com.rsi.agp.dao.tables.commons.Via";
arrCamposFiltrosLupas['Via'] = new Array('filtro_Via');
arrCamposBeanFiltros['Via'] = new Array('nombre');
arrTxtCabecerasLupas['Via'] = new Array('Clave', 'Nombre');
arrColumnWidth['Via'] = new Array('30%', '70%');
arrCamposBean['Via'] = new Array('clave', 'nombre');
arrCamposDepende['Via'] = new Array('via');
arrCamposBeanDepende['Via'] = new Array('clave');
arrTipoBeanDepende['Via'] = new Array('java.lang.String');
arrCamposDevolver['Via'] = new Array('via', 'desc_via');
arrCamposBeanDevolver['Via'] = new Array('clave', 'nombre');
arrColumnasDistinct['Via'] = new Array();
arrColumnasIsNull['Via'] = new Array();
arrCampoLeftJoin['Via'] = new Array();
arrCampoRestrictions['Via'] = new Array();
arrValorRestrictions['Via'] = new Array();
arrOperadorRestrictions['Via'] = new Array();
arrTipoValorRestrictions['Via'] = new Array(); 
arrValorDependeGenerico['Via'] = new Array();
arrValorDependeOrdenGenerico['Via'] = new Array();
// FIN Lupa de Vias

// INICIO Lupa de Provincias
arrObjetosLupas['Provincia'] = "com.rsi.agp.dao.tables.commons.Provincia";
arrCamposFiltrosLupas['Provincia'] = new Array('filtro_codProvincia', 'filtro_Provincia');
arrCamposBeanFiltros['Provincia'] = new Array('codprovincia', 'nomprovincia');
arrTipoCamposBeanFiltros['Provincia'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['Provincia'] = new Array('C\u00F3digo', 'Nombre');
arrColumnWidth['Provincia'] = new Array('30%', '70%');
arrCamposBean['Provincia'] = new Array('codprovincia', 'nomprovincia');
arrCamposDepende['Provincia'] = new Array('provincia');
arrCamposBeanDepende['Provincia'] = new Array('codprovincia');
arrTipoBeanDepende['Provincia'] = new Array('java.math.BigDecimal');
arrCamposDevolver['Provincia'] = new Array('provincia', 'desc_provincia');
arrCamposBeanDevolver['Provincia'] = new Array('codprovincia', 'nomprovincia');
arrColumnasDistinct['Provincia'] = new Array();
arrColumnasIsNull['Provincia'] = new Array();
arrCampoLeftJoin['Provincia'] = new Array();
arrCampoRestrictions['Provincia'] = new Array();
arrValorRestrictions['Provincia'] = new Array();
arrOperadorRestrictions['Provincia'] = new Array();
arrTipoValorRestrictions['Provincia'] = new Array();
arrValorDependeGenerico['Provincia'] = new Array();
arrValorDependeOrdenGenerico['Provincia'] = new Array();
// FIN Lupa de Provincias

// INICIO Lupa de Provincias para el cambio masivo
arrObjetosLupas['ProvinciaCM'] = "com.rsi.agp.dao.tables.commons.Provincia";
arrCamposFiltrosLupas['ProvinciaCM'] = new Array('filtro_codProvinciaCM', 'filtro_ProvinciaCM');
arrCamposBeanFiltros['ProvinciaCM'] = new Array('codprovincia', 'nomprovincia');
arrTipoCamposBeanFiltros['ProvinciaCM'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['ProvinciaCM'] = new Array('C\u00F3digo', 'Nombre');
arrColumnWidth['ProvinciaCM'] = new Array('30%', '70%');
arrCamposBean['ProvinciaCM'] = new Array('codprovincia', 'nomprovincia');
arrCamposDepende['ProvinciaCM'] = new Array('provincia_cm');
arrCamposBeanDepende['ProvinciaCM'] = new Array('codprovincia');
arrTipoBeanDepende['ProvinciaCM'] = new Array('java.math.BigDecimal');
arrCamposDevolver['ProvinciaCM'] = new Array('provincia_cm', 'desc_provincia_cm');
arrCamposBeanDevolver['ProvinciaCM'] = new Array('codprovincia', 'nomprovincia');
arrColumnasDistinct['ProvinciaCM'] = new Array();
arrColumnasIsNull['ProvinciaCM'] = new Array();
arrCampoLeftJoin['ProvinciaCM'] = new Array();
arrCampoRestrictions['ProvinciaCM'] = new Array();
arrValorRestrictions['ProvinciaCM'] = new Array();
arrOperadorRestrictions['ProvinciaCM'] = new Array();
arrTipoValorRestrictions['ProvinciaCM'] = new Array(); 
arrValorDependeGenerico['ProvinciaCM'] = new Array();
arrValorDependeOrdenGenerico['ProvinciaCM'] = new Array();
// FIN Lupa de Provincias para el cambio masivo

// INICIO Lupa de Localidades
arrObjetosLupas['Localidad'] = "com.rsi.agp.dao.tables.commons.Localidad";
arrCamposFiltrosLupas['Localidad'] = new Array('filtro_codLocalidad', 'filtro_Localidad');
arrCamposBeanFiltros['Localidad'] = new Array('id.codlocalidad', 'nomlocalidad');
arrTipoCamposBeanFiltros['Localidad'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['Localidad'] = new Array('Cod. Provincia', 'Nom. Provincia', 'Cod. Localidad', 'Nom. Localidad', 'Sublocalidad');
arrColumnWidth['Localidad'] = new Array('17%', '20%', '17%', '30%', '16%');
arrCamposBean['Localidad'] = new Array('id.codprovincia', 'provincia.nomprovincia', 'id.codlocalidad', 'nomlocalidad', 'id.sublocalidad');
arrCamposDepende['Localidad'] = new Array('provincia', 'localidad', 'sublocalidad');
arrCamposBeanDepende['Localidad'] = new Array('id.codprovincia', 'id.codlocalidad', 'id.sublocalidad');
arrTipoBeanDepende['Localidad'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.String');
arrCamposDevolver['Localidad'] = new Array('provincia', 'desc_provincia', 'localidad', 'desc_localidad', 'sublocalidad');
arrCamposBeanDevolver['Localidad'] = new Array('id.codprovincia', 'provincia.nomprovincia', 'id.codlocalidad', 'nomlocalidad', 'id.sublocalidad');
arrColumnasDistinct['Localidad'] = new Array();
arrColumnasIsNull['Localidad'] = new Array();
arrCampoLeftJoin['Localidad'] = new Array();
arrCampoRestrictions['Localidad'] = new Array();
arrValorRestrictions['Localidad'] = new Array();
arrOperadorRestrictions['Localidad'] = new Array();
arrTipoValorRestrictions['Localidad'] = new Array(); 
arrValorDependeGenerico['Localidad'] = new Array();
arrValorDependeOrdenGenerico['Localidad'] = new Array();
// FIN Lupa de Localidades

// INICIO Lupa de Comarcas
arrObjetosLupas['Comarca'] = "com.rsi.agp.dao.tables.commons.Comarca";
arrCamposFiltrosLupas['Comarca'] = new Array('filtro_codComarca', 'filtro_Comarca');
arrCamposBeanFiltros['Comarca'] = new Array('id.codcomarca', 'nomcomarca');
arrTipoCamposBeanFiltros['Comarca'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['Comarca'] = new Array('Cod. Provincia', 'Nom. Provincia', 'Cod. Comarca', 'Nom. Comarca');
arrColumnWidth['Comarca'] = new Array('20%', '30%', '20%', '30%');
arrCamposBean['Comarca'] = new Array('id.codprovincia', 'provincia.nomprovincia', 'id.codcomarca', 'nomcomarca');
arrCamposDepende['Comarca'] = new Array('provincia', 'comarca');
arrCamposBeanDepende['Comarca'] = new Array('id.codprovincia', 'id.codcomarca');
arrTipoBeanDepende['Comarca'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['Comarca'] = new Array('provincia', 'desc_provincia', 'comarca', 'desc_comarca');
arrCamposBeanDevolver['Comarca'] = new Array('id.codprovincia', 'provincia.nomprovincia', 'id.codcomarca', 'nomcomarca');
arrColumnasDistinct['Comarca'] = new Array();
arrColumnasIsNull['Comarca'] = new Array();
arrCampoLeftJoin['Comarca'] = new Array();
arrCampoRestrictions['Comarca'] = new Array();
arrValorRestrictions['Comarca'] = new Array();
arrOperadorRestrictions['Comarca'] = new Array();
arrTipoValorRestrictions['Comarca'] = new Array(); 
arrValorDependeGenerico['Comarca'] = new Array();
arrValorDependeOrdenGenerico['Comarca'] = new Array();
// FIN Lupa de Comarcas

// INICIO Lupa de Comarcas para el cambio masivo
arrObjetosLupas['ComarcaCM'] = "com.rsi.agp.dao.tables.commons.Comarca";
arrCamposFiltrosLupas['ComarcaCM'] = new Array('filtro_codComarcaCM', 'filtro_ComarcaCM');
arrCamposBeanFiltros['ComarcaCM'] = new Array('id.codcomarca', 'nomcomarca');
arrTipoCamposBeanFiltros['ComarcaCM'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['ComarcaCM'] = new Array('Cod. Provincia', 'Nom. Provincia', 'Cod. Comarca', 'Nom. Comarca');
arrColumnWidth['ComarcaCM'] = new Array('20%', '30%', '20%', '30%');
arrCamposBean['ComarcaCM'] = new Array('id.codprovincia', 'provincia.nomprovincia', 'id.codcomarca', 'nomcomarca');
arrCamposDepende['ComarcaCM'] = new Array('provincia_cm', 'comarca_cm');
arrCamposBeanDepende['ComarcaCM'] = new Array('id.codprovincia', 'id.codcomarca');
arrTipoBeanDepende['ComarcaCM'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['ComarcaCM'] = new Array('provincia_cm', 'desc_provincia_cm', 'comarca_cm', 'desc_comarca_cm');
arrCamposBeanDevolver['ComarcaCM'] = new Array('id.codprovincia', 'provincia.nomprovincia', 'id.codcomarca', 'nomcomarca');
arrColumnasDistinct['ComarcaCM'] = new Array();
arrColumnasIsNull['ComarcaCM'] = new Array();
arrCampoLeftJoin['ComarcaCM'] = new Array();
arrCampoRestrictions['ComarcaCM'] = new Array();
arrValorRestrictions['ComarcaCM'] = new Array();
arrOperadorRestrictions['ComarcaCM'] = new Array();
arrTipoValorRestrictions['ComarcaCM'] = new Array(); 
arrValorDependeGenerico['ComarcaCM'] = new Array();
arrValorDependeOrdenGenerico['ComarcaCM'] = new Array();
// FIN Lupa de Comarcas para el cambio masivo

// INICIO Lupa de Terminos

function setArrCampos(arrCamposBeanTermino, arrCamposBeanDevolverTermino, arrCamposBeanFiltros) {
    arrCamposBean['Termino'] = arrCamposBeanTermino;
    arrCamposBeanDevolver['Termino'] = arrCamposBeanDevolverTermino;
    arrCamposBeanFiltros['Termino'] = arrCamposBeanFiltros;
}

arrObjetosLupas['Termino'] = "com.rsi.agp.dao.tables.commons.Termino";
arrCamposFiltrosLupas['Termino'] = new Array('filtro_codTermino', 'filtro_Termino');
//arrCamposBeanFiltros['Termino'] = new Array('id.codtermino', 'nomtermino');
arrTipoCamposBeanFiltros['Termino'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['Termino'] = new Array('Cod. Prov.', 'Nom. Provincia', 'Cod. Com.', 'Nom. Comarca', 'Cod. Term.', 'Nom. T\u00E9rmino', 'Subt\u00E9rmino');
arrColumnWidth['Termino'] = new Array('10%', '20%', '10%', '20%', '10%', '20%', '10%');
//arrCamposBean['Termino'] = new Array('id.codprovincia', 'provincia.nomprovincia', 'id.codcomarca', 'comarca.nomcomarca', 'id.codtermino', 'nomtermino', 'id.subtermino');
arrCamposDepende['Termino'] = new Array('provincia', 'comarca', 'termino', 'subtermino');
arrCamposBeanDepende['Termino'] = new Array('id.codprovincia', 'id.codcomarca', 'id.codtermino', 'id.subtermino');
arrTipoBeanDepende['Termino'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.String');
arrCamposDevolver['Termino'] = new Array('provincia', 'desc_provincia', 'comarca', 'desc_comarca', 'termino', 'desc_termino', 'subtermino');
//arrCamposBeanDevolver['Termino'] = new Array('id.codprovincia', 'provincia.nomprovincia', 'id.codcomarca', 'comarca.nomcomarca', 'id.codtermino', 'nomtermino', 'id.subtermino');
arrColumnasDistinct['Termino'] = new Array();
arrColumnasIsNull['Termino'] = new Array();
arrCampoLeftJoin['Termino'] = new Array();
arrCampoRestrictions['Termino'] = new Array();
arrValorRestrictions['Termino'] = new Array();
arrOperadorRestrictions['Termino'] = new Array();
arrTipoValorRestrictions['Termino'] = new Array(); 
arrValorDependeGenerico['Termino'] = new Array();
arrValorDependeOrdenGenerico['Termino'] = new Array();
// FIN Lupa de Terminos

// INICIO Lupa de Terminos para el cambio masivo
arrObjetosLupas['TerminoCM'] = "com.rsi.agp.dao.tables.commons.Termino";
arrCamposFiltrosLupas['TerminoCM'] = new Array('filtro_codTerminoCM', 'filtro_TerminoCM');
//arrCamposBeanFiltros['TerminoCM'] = new Array('id.codtermino', 'nomtermino');
arrTipoCamposBeanFiltros['TerminoCM'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['TerminoCM'] = new Array('Cod. Prov.', 'Nom. Provincia', 'Cod. Com.', 'Nom. Comarca', 'Cod. Term.', 'Nom. T\u00E9rmino', 'Subt\u00E9rmino');
arrColumnWidth['TerminoCM'] = new Array('10%', '20%', '10%', '20%', '10%', '20%', '10%');
//arrCamposBean['TerminoCM'] = new Array('id.codprovincia', 'provincia.nomprovincia', 'id.codcomarca', 'comarca.nomcomarca', 'id.codtermino', 'nomtermino', 'id.subtermino');
arrCamposDepende['TerminoCM'] = new Array('provincia_cm', 'comarca_cm', 'termino_cm', 'subtermino_cm');
arrCamposBeanDepende['TerminoCM'] = new Array('id.codprovincia', 'id.codcomarca', 'id.codtermino', 'id.subtermino');
arrTipoBeanDepende['TerminoCM'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.String');
arrCamposDevolver['TerminoCM'] = new Array('provincia_cm', 'desc_provincia_cm', 'comarca_cm', 'desc_comarca_cm', 'termino_cm', 'desc_termino_cm', 'subtermino_cm');
//arrCamposBeanDevolver['TerminoCM'] = new Array('id.codprovincia', 'provincia.nomprovincia', 'id.codcomarca', 'comarca.nomcomarca', 'id.codtermino', 'nomtermino', 'id.subtermino');
arrColumnasDistinct['TerminoCM'] = new Array();
arrColumnasIsNull['TerminoCM'] = new Array();
arrCampoLeftJoin['TerminoCM'] = new Array();
arrCampoRestrictions['TerminoCM'] = new Array();
arrValorRestrictions['TerminoCM'] = new Array();
arrOperadorRestrictions['TerminoCM'] = new Array();
arrTipoValorRestrictions['TerminoCM'] = new Array(); 
arrValorDependeGenerico['TerminoCM'] = new Array();
arrValorDependeOrdenGenerico['TerminoCM'] = new Array();
// FIN Lupa de Terminos para el cambio masivo

// INICIO Lupa de Cultivos
arrObjetosLupas['Cultivo'] = "com.rsi.agp.dao.tables.cpl.Cultivo";
arrCamposFiltrosLupas['Cultivo'] = new Array('filtro_codCultivo', 'filtro_Cultivo');
arrCamposBeanFiltros['Cultivo'] = new Array('id.codcultivo', 'descultivo');
arrTipoCamposBeanFiltros['Cultivo'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['Cultivo'] = new Array('Codigo', 'Nombre');
arrColumnWidth['Cultivo'] = new Array('30%', '70%');
arrCamposBean['Cultivo'] = new Array('id.codcultivo', 'descultivo');
arrCamposDepende['Cultivo'] = new Array('lineaseguroid', 'cultivo');
arrCamposBeanDepende['Cultivo'] = new Array('id.lineaseguroid', 'id.codcultivo');
arrTipoBeanDepende['Cultivo'] = new Array('java.lang.Long', 'java.math.BigDecimal');
arrCamposDevolver['Cultivo'] = new Array('cultivo', 'desc_cultivo');
arrCamposBeanDevolver['Cultivo'] = new Array('id.codcultivo', 'descultivo');
arrColumnasDistinct['Cultivo'] = new Array();
arrColumnasIsNull['Cultivo'] = new Array();
arrCampoLeftJoin['Cultivo'] = new Array();
arrCampoRestrictions['Cultivo'] = new Array();
arrValorRestrictions['Cultivo'] = new Array();
arrOperadorRestrictions['Cultivo'] = new Array();
arrTipoValorRestrictions['Cultivo'] = new Array(); 
arrValorDependeGenerico['Cultivo'] = new Array();
arrValorDependeOrdenGenerico['Cultivo'] = new Array();
// FIN Lupa de Cultivos

// INICIO Lupa de Cultivos para sobreprecio
arrObjetosLupas['CultivoSbp'] = "com.rsi.agp.dao.tables.cpl.Cultivo";
arrCamposFiltrosLupas['CultivoSbp'] = new Array('filtro_codCultivoSbp', 'filtro_CultivoSbp');
arrCamposBeanFiltros['CultivoSbp'] = new Array('id.codcultivo', 'descultivo');
arrTipoCamposBeanFiltros['CultivoSbp'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['CultivoSbp'] = new Array('C\u00F3digo', 'Nombre');
arrColumnWidth['CultivoSbp'] = new Array('30%', '70%');
arrCamposBean['CultivoSbp'] = new Array('id.codcultivo', 'descultivo');
arrCamposDepende['CultivoSbp'] = new Array('plan', 'linea', 'cultivo');
arrCamposBeanDepende['CultivoSbp'] = new Array('linea.codplan', 'linea.codlinea', 'id.codcultivo');
arrTipoBeanDepende['CultivoSbp'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['CultivoSbp'] = new Array('cultivo', 'desc_cultivo');
arrCamposBeanDevolver['CultivoSbp'] = new Array('id.codcultivo', 'descultivo');
arrColumnasDistinct['CultivoSbp'] = new Array();
arrColumnasIsNull['CultivoSbp'] = new Array();
arrCampoLeftJoin['CultivoSbp'] = new Array();
arrCampoRestrictions['CultivoSbp'] = new Array();
arrValorRestrictions['CultivoSbp'] = new Array();
arrOperadorRestrictions['CultivoSbp'] = new Array();
arrTipoValorRestrictions['CultivoSbp'] = new Array(); 
arrValorDependeGenerico['CultivoSbp'] = new Array();
arrValorDependeOrdenGenerico['CultivoSbp'] = new Array();
// FIN Lupa de Cultivos para sobreprecio

// INICIO Lupa de Cultivos para el cambio masivo
arrObjetosLupas['CultivoCM'] = "com.rsi.agp.dao.tables.cpl.Cultivo";
arrCamposFiltrosLupas['CultivoCM'] = new Array('filtro_codCultivoCM', 'filtro_CultivoCM');
arrCamposBeanFiltros['CultivoCM'] = new Array('id.codcultivo', 'descultivo');
arrTipoCamposBeanFiltros['CultivoCM'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['CultivoCM'] = new Array('C\u00F3digo', 'Nombre');
arrColumnWidth['CultivoCM'] = new Array('30%', '70%');
arrCamposBean['CultivoCM'] = new Array('id.codcultivo', 'descultivo');
arrCamposDepende['CultivoCM'] = new Array('lineaseguroid', 'cultivo_cm');
arrCamposBeanDepende['CultivoCM'] = new Array('id.lineaseguroid', 'id.codcultivo');
arrTipoBeanDepende['CultivoCM'] = new Array('java.lang.Long', 'java.math.BigDecimal');
arrCamposDevolver['CultivoCM'] = new Array('cultivo_cm', 'desc_cultivo_cm');
arrCamposBeanDevolver['CultivoCM'] = new Array('id.codcultivo', 'descultivo');
arrColumnasDistinct['CultivoCM'] = new Array();
arrColumnasIsNull['CultivoCM'] = new Array();
arrCampoLeftJoin['CultivoCM'] = new Array();
arrCampoRestrictions['CultivoCM'] = new Array();
arrValorRestrictions['CultivoCM'] = new Array();
arrOperadorRestrictions['CultivoCM'] = new Array();
arrTipoValorRestrictions['CultivoCM'] = new Array(); 
arrValorDependeGenerico['CultivoCM'] = new Array();
arrValorDependeOrdenGenerico['CultivoCM'] = new Array();
// FIN Lupa de Cultivos para el cambio masivo

// INICIO Lupa de Variedades
arrObjetosLupas['Variedad'] = "com.rsi.agp.dao.tables.cpl.Variedad";
arrCamposFiltrosLupas['Variedad'] = new Array('filtro_codVariedad', 'filtro_Variedad');
arrCamposBeanFiltros['Variedad'] = new Array('id.codvariedad', 'desvariedad');
arrTipoCamposBeanFiltros['Variedad'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['Variedad'] = new Array('Cod. Cultivo', 'Nom. Cultivo', 'Cod. Variedad', 'Nom. Variedad');
arrColumnWidth['Variedad'] = new Array('20%', '30%', '20%', '30%');
arrCamposBean['Variedad'] = new Array('id.codcultivo', 'cultivo.descultivo', 'id.codvariedad', 'desvariedad');
arrCamposDepende['Variedad'] = new Array('lineaseguroid', 'cultivo', 'variedad');
arrCamposBeanDepende['Variedad'] = new Array('id.lineaseguroid', 'id.codcultivo', 'id.codvariedad');
arrTipoBeanDepende['Variedad'] = new Array('java.lang.Long', 'java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['Variedad'] = new Array('cultivo', 'desc_cultivo', 'variedad', 'desc_variedad');
arrCamposBeanDevolver['Variedad'] = new Array('id.codcultivo', 'cultivo.descultivo', 'id.codvariedad', 'desvariedad');
arrColumnasDistinct['Variedad'] = new Array();
arrColumnasIsNull['Variedad'] = new Array();
arrCampoLeftJoin['Variedad'] = new Array();
arrCampoRestrictions['Variedad'] = new Array();
arrValorRestrictions['Variedad'] = new Array();
arrOperadorRestrictions['Variedad'] = new Array();
arrTipoValorRestrictions['Variedad'] = new Array(); 
arrValorDependeGenerico['Variedad'] = new Array();
arrValorDependeOrdenGenerico['Variedad'] = new Array();
// FIN Lupa de Variedades

// INICIO Lupa de Variedades para el cambio masivo
arrObjetosLupas['VariedadCM'] = "com.rsi.agp.dao.tables.cpl.Variedad";
arrCamposFiltrosLupas['VariedadCM'] = new Array('filtro_codVariedadCM', 'filtro_VariedadCM');
arrCamposBeanFiltros['VariedadCM'] = new Array('id.codvariedad', 'desvariedad');
arrTipoCamposBeanFiltros['VariedadCM'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['VariedadCM'] = new Array('Cod. Cultivo', 'Nom. Cultivo', 'Cod. Variedad', 'Nom. Variedad');
arrColumnWidth['VariedadCM'] = new Array('20%', '30%', '20%', '30%');
arrCamposBean['VariedadCM'] = new Array('id.codcultivo', 'cultivo.descultivo', 'id.codvariedad', 'desvariedad');
arrCamposDepende['VariedadCM'] = new Array('lineaseguroid', 'cultivo_cm', 'variedad_cm');
arrCamposBeanDepende['VariedadCM'] = new Array('id.lineaseguroid', 'id.codcultivo', 'id.codvariedad');
arrTipoBeanDepende['VariedadCM'] = new Array('java.lang.Long', 'java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['VariedadCM'] = new Array('cultivo_cm', 'desc_cultivo_cm', 'variedad_cm', 'desc_variedad_cm');
arrCamposBeanDevolver['VariedadCM'] = new Array('id.codcultivo', 'cultivo.descultivo', 'id.codvariedad', 'desvariedad');
arrColumnasDistinct['VariedadCM'] = new Array();
arrColumnasIsNull['VariedadCM'] = new Array();
arrCampoLeftJoin['VariedadCM'] = new Array();
arrCampoRestrictions['VariedadCM'] = new Array();
arrValorRestrictions['VariedadCM'] = new Array();
arrOperadorRestrictions['VariedadCM'] = new Array();
arrTipoValorRestrictions['VariedadCM'] = new Array();
arrValorDependeGenerico['VariedadCM'] = new Array();
arrValorDependeOrdenGenerico['VariedadCM'] = new Array();
// FIN Lupa de Variedades para el cambio masivo

// INICIO Lupa de Tomadores
arrObjetosLupas['Tomador'] = "com.rsi.agp.dao.tables.admin.Tomador";
arrCamposFiltrosLupas['Tomador'] = new Array('filtro_codTomador', 'filtro_Tomador');
arrCamposBeanFiltros['Tomador'] = new Array('id.ciftomador', 'razonsocial');
arrTipoCamposBeanFiltros['Tomador'] = new Array('java.lang.String', 'java.lang.String');
arrTxtCabecerasLupas['Tomador'] = new Array('Cod. Entidad', 'Nom. Entidad', 'C.I.F.', 'Raz\u00F3n social');
arrColumnWidth['Tomador'] = new Array('13%', '35%', '12%', '40%');
arrCamposBean['Tomador'] = new Array('id.codentidad', 'entidad.nomentidad', 'id.ciftomador', 'razonsocial');
arrCamposDepende['Tomador'] = new Array('entidad', 'tomador');
arrCamposBeanDepende['Tomador'] = new Array('id.codentidad', 'id.ciftomador');
arrTipoBeanDepende['Tomador'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrCamposDevolver['Tomador'] = new Array('entidad', 'desc_entidad', 'tomador', 'desc_tomador');
arrCamposBeanDevolver['Tomador'] = new Array('id.codentidad', 'entidad.nomentidad', 'id.ciftomador', 'razonsocial');
arrColumnasDistinct['Tomador'] = new Array();
arrColumnasIsNull['Tomador'] = new Array();
arrCampoLeftJoin['Tomador'] = new Array();
arrCampoRestrictions['Tomador'] = new Array();
arrValorRestrictions['Tomador'] = new Array();
arrOperadorRestrictions['Tomador'] = new Array();
arrTipoValorRestrictions['Tomador'] = new Array(); 
arrValorDependeGenerico['Tomador'] = new Array();
arrValorDependeOrdenGenerico['Tomador'] = new Array();
// FIN Lupa de Tomadores

// INICIO Lupa de Ciclo de cultivo
arrObjetosLupas['CicloCultivo'] = "com.rsi.agp.dao.tables.cgen.CicloCultivo";
arrCamposFiltrosLupas['CicloCultivo'] = new Array('filtro_CicloCultivo');
arrCamposBeanFiltros['CicloCultivo'] = new Array('desciclocultivo');
arrTxtCabecerasLupas['CicloCultivo'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['CicloCultivo'] = new Array('30%', '70%');
arrCamposBean['CicloCultivo'] = new Array('codciclocultivo', 'desciclocultivo');
arrCamposDepende['CicloCultivo'] = new Array('cicloCultivo');
arrCamposBeanDepende['CicloCultivo'] = new Array('codciclocultivo');
arrTipoBeanDepende['CicloCultivo'] = new Array('java.math.BigDecimal');
arrCamposDevolver['CicloCultivo'] = new Array('cicloCultivo', 'desciclocultivo');
arrCamposBeanDevolver['CicloCultivo'] = new Array('codciclocultivo', 'desciclocultivo');
arrColumnasDistinct['CicloCultivo'] = new Array();
arrColumnasIsNull['CicloCultivo'] = new Array();
arrCampoLeftJoin['CicloCultivo'] = new Array();
arrCampoRestrictions['CicloCultivo'] = new Array();
arrValorRestrictions['CicloCultivo'] = new Array();
arrOperadorRestrictions['CicloCultivo'] = new Array();
arrTipoValorRestrictions['CicloCultivo'] = new Array(); 
arrValorDependeGenerico['CicloCultivo'] = new Array();
arrValorDependeOrdenGenerico['CicloCultivo'] = new Array();
// FIN Lupa de Ciclo de cultivo

// INICIO Lupa de Ciclo de cultivo para el cambio masivo
arrObjetosLupas['CicloCultivoCM'] = "com.rsi.agp.dao.tables.cgen.CicloCultivo";
arrCamposFiltrosLupas['CicloCultivoCM'] = new Array('filtro_CicloCultivoCM');
arrCamposBeanFiltros['CicloCultivoCM'] = new Array('desciclocultivo');
arrTxtCabecerasLupas['CicloCultivoCM'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['CicloCultivoCM'] = new Array('30%', '70%');
arrCamposBean['CicloCultivoCM'] = new Array('codciclocultivo', 'desciclocultivo');
arrCamposDepende['CicloCultivoCM'] = new Array('cicloCultivo_cm');
arrCamposBeanDepende['CicloCultivoCM'] = new Array('codciclocultivo');
arrTipoBeanDepende['CicloCultivoCM'] = new Array('java.math.BigDecimal');
arrCamposDevolver['CicloCultivoCM'] = new Array('cicloCultivo_cm', 'desciclocultivo_cm');
arrCamposBeanDevolver['CicloCultivoCM'] = new Array('codciclocultivo', 'desciclocultivo');
arrColumnasDistinct['CicloCultivoCM'] = new Array();
arrColumnasIsNull['CicloCultivoCM'] = new Array();
arrCampoLeftJoin['CicloCultivoCM'] = new Array();
arrCampoRestrictions['CicloCultivoCM'] = new Array();
arrValorRestrictions['CicloCultivoCM'] = new Array();
arrOperadorRestrictions['CicloCultivoCM'] = new Array();
arrTipoValorRestrictions['CicloCultivoCM'] = new Array(); 
arrValorDependeGenerico['CicloCultivoCM'] = new Array();
arrValorDependeOrdenGenerico['CicloCultivoCM'] = new Array();
// FIN Lupa de Ciclo de cultivo para el cambio masivo

// INICIO Lupa de Sistema de cultivo
arrObjetosLupas['SistemaCultivo'] = "com.rsi.agp.dao.tables.cgen.SistemaCultivo";
arrCamposFiltrosLupas['SistemaCultivo'] = new Array('filtro_SistemaCultivo');
arrCamposBeanFiltros['SistemaCultivo'] = new Array('dessistemacultivo');
arrTxtCabecerasLupas['SistemaCultivo'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['SistemaCultivo'] = new Array('30%', '70%');
arrCamposBean['SistemaCultivo'] = new Array('codsistemacultivo', 'dessistemacultivo');
arrCamposDepende['SistemaCultivo'] = new Array('sistemaCultivo');
arrCamposBeanDepende['SistemaCultivo'] = new Array('codsistemacultivo');
arrTipoBeanDepende['SistemaCultivo'] = new Array('java.math.BigDecimal');
arrCamposDevolver['SistemaCultivo'] = new Array('sistemaCultivo', 'dessistemaCultivo');
arrCamposBeanDevolver['SistemaCultivo'] = new Array('codsistemacultivo', 'dessistemacultivo');
arrColumnasDistinct['SistemaCultivo'] = new Array();
arrColumnasIsNull['SistemaCultivo'] = new Array();
arrCampoLeftJoin['SistemaCultivo'] = new Array();
arrCampoRestrictions['SistemaCultivo'] = new Array();
arrValorRestrictions['SistemaCultivo'] = new Array();
arrOperadorRestrictions['SistemaCultivo'] = new Array();
arrTipoValorRestrictions['SistemaCultivo'] = new Array(); 
arrValorDependeGenerico['SistemaCultivo'] = new Array();
arrValorDependeOrdenGenerico['SistemaCultivo'] = new Array();
// FIN Lupa de Sistema de cultivo

// INICIO Lupa de Sistema de cultivo para el cambio masivo
arrObjetosLupas['SistemaCultivoCM'] = "com.rsi.agp.dao.tables.cgen.SistemaCultivo";
arrCamposFiltrosLupas['SistemaCultivoCM'] = new Array('filtro_SistemaCultivoCM');
arrCamposBeanFiltros['SistemaCultivoCM'] = new Array('dessistemacultivo');
arrTxtCabecerasLupas['SistemaCultivoCM'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['SistemaCultivoCM'] = new Array('30%', '70%');
arrCamposBean['SistemaCultivoCM'] = new Array('codsistemacultivo', 'dessistemacultivo');
arrCamposDepende['SistemaCultivoCM'] = new Array('sistemaCultivo_cm');
arrCamposBeanDepende['SistemaCultivoCM'] = new Array('codsistemacultivo');
arrTipoBeanDepende['SistemaCultivoCM'] = new Array('java.math.BigDecimal');
arrCamposDevolver['SistemaCultivoCM'] = new Array('sistemaCultivo_cm', 'desc_sistemaCultivo_cm');
arrCamposBeanDevolver['SistemaCultivoCM'] = new Array('codsistemacultivo', 'dessistemacultivo');
arrColumnasDistinct['SistemaCultivoCM'] = new Array();
arrColumnasIsNull['SistemaCultivoCM'] = new Array();
arrCampoLeftJoin['SistemaCultivoCM'] = new Array();
arrCampoRestrictions['SistemaCultivoCM'] = new Array();
arrValorRestrictions['SistemaCultivoCM'] = new Array();
arrOperadorRestrictions['SistemaCultivoCM'] = new Array();
arrTipoValorRestrictions['SistemaCultivoCM'] = new Array(); 
arrValorDependeGenerico['SistemaCultivoCM'] = new Array();
arrValorDependeOrdenGenerico['SistemaCultivoCM'] = new Array();
// FIN Lupa de Sistema de cultivo para el cambio masivo

// INICIO Lupa de Tipos de capital para el cambio masivo
arrObjetosLupas['TipoCapitalCM'] = "com.rsi.agp.dao.tables.cgen.TipoCapital";
arrCamposFiltrosLupas['TipoCapitalCM'] = new Array('filtro_TipoCapitalCM');
arrCamposBeanFiltros['TipoCapitalCM'] = new Array('destipocapital');
arrTxtCabecerasLupas['TipoCapitalCM'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['TipoCapitalCM'] = new Array('30%', '70%');
arrCamposBean['TipoCapitalCM'] = new Array('codtipocapital', 'destipocapital');
arrCamposDepende['TipoCapitalCM'] = new Array('capital');
arrCamposBeanDepende['TipoCapitalCM'] = new Array('codtipocapital_cm');
arrTipoBeanDepende['TipoCapitalCM'] = new Array('java.math.BigDecimal');
arrCamposDevolver['TipoCapitalCM'] = new Array('capital_cm', 'desc_capital_cm');
arrCamposBeanDevolver['TipoCapitalCM'] = new Array('codtipocapital', 'destipocapital');
arrColumnasDistinct['TipoCapitalCM'] = new Array();
arrColumnasIsNull['TipoCapitalCM'] = new Array();
arrCampoLeftJoin['TipoCapitalCM'] = new Array();
arrCampoRestrictions['TipoCapitalCM'] = new Array();
arrValorRestrictions['TipoCapitalCM'] = new Array();
arrOperadorRestrictions['TipoCapitalCM'] = new Array();
arrTipoValorRestrictions['TipoCapitalCM'] = new Array(); 
arrValorDependeGenerico['TipoCapitalCM'] = new Array();
arrValorDependeOrdenGenerico['TipoCapitalCM'] = new Array();
// FIN Lupa de Tipos de capital para el cambio masivo

// INICIO Lupa de Tipos de capital
arrObjetosLupas['TipoCapital'] = "com.rsi.agp.dao.tables.cgen.TipoCapital";
arrCamposFiltrosLupas['TipoCapital'] = new Array('filtro_TipoCapital');
arrCamposBeanFiltros['TipoCapital'] = new Array('destipocapital');
arrTxtCabecerasLupas['TipoCapital'] = new Array('Codigo', 'Descripcion');
arrColumnWidth['TipoCapital'] = new Array('30%', '70%');
arrCamposBean['TipoCapital'] = new Array('codtipocapital', 'destipocapital');
arrCamposDepende['TipoCapital'] = new Array('capital');
arrCamposBeanDepende['TipoCapital'] = new Array('codtipocapital');
arrTipoBeanDepende['TipoCapital'] = new Array('java.math.BigDecimal');
arrCamposDevolver['TipoCapital'] = new Array('capital', 'desc_capital');
arrCamposBeanDevolver['TipoCapital'] = new Array('codtipocapital', 'destipocapital');
arrColumnasDistinct['TipoCapital'] = new Array();
arrColumnasIsNull['TipoCapital'] = new Array();
arrCampoLeftJoin['TipoCapital'] = new Array();
arrCampoRestrictions['TipoCapital'] = new Array();
arrValorRestrictions['TipoCapital'] = new Array();
arrOperadorRestrictions['TipoCapital'] = new Array();
arrTipoValorRestrictions['TipoCapital'] = new Array();
arrValorDependeGenerico['TipoCapital'] = new Array();
arrValorDependeOrdenGenerico['TipoCapital'] = new Array();
// FIN Lupa de Tipos de capital



// INICIO Lupa de Destino para cambio masivo
arrObjetosLupas['DestinoCM'] = "com.rsi.agp.dao.tables.cgen.Destino";
arrCamposFiltrosLupas['DestinoCM'] = new Array('filtro_DestinoCM');
arrCamposBeanFiltros['DestinoCM'] = new Array('desdestino');
arrTxtCabecerasLupas['DestinoCM'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['DestinoCM'] = new Array('30%', '70%');
arrCamposBean['DestinoCM'] = new Array('coddestino', 'desdestino');
arrCamposDepende['DestinoCM'] = new Array('destino_cm');
arrCamposBeanDepende['DestinoCM'] = new Array('coddestino');
arrTipoBeanDepende['DestinoCM'] = new Array('java.math.BigDecimal');
arrCamposDevolver['DestinoCM'] = new Array('destino_cm', 'desc_destino_cm');
arrCamposBeanDevolver['DestinoCM'] = new Array('coddestino', 'desdestino');
arrColumnasDistinct['DestinoCM'] = new Array();
arrColumnasIsNull['DestinoCM'] = new Array();
arrCampoLeftJoin['DestinoCM'] = new Array();
arrCampoRestrictions['DestinoCM'] = new Array();
arrValorRestrictions['DestinoCM'] = new Array();
arrOperadorRestrictions['DestinoCM'] = new Array();
arrTipoValorRestrictions['DestinoCM'] = new Array(); 
arrValorDependeGenerico['DestinoCM'] = new Array();
arrValorDependeOrdenGenerico['DestinoCM'] = new Array();
// FIN Lupa de Destino para cambio masivo

// INICIO Lupa de Tipos de capital segun Cultivo
arrObjetosLupas['TipoCapitalCultivo'] = "com.rsi.agp.dao.tables.orgDat.VistaTipoCapital";
arrCamposFiltrosLupas['TipoCapitalCultivo'] = new Array('filtro_TipoCapitalCultivo');
arrCamposBeanFiltros['TipoCapitalCultivo'] = new Array('id.destipocapital');
arrTxtCabecerasLupas['TipoCapitalCultivo'] = new Array('Codigo', 'Descripcion');
arrColumnWidth['TipoCapitalCultivo'] = new Array('20%', '80%');
arrCamposBean['TipoCapitalCultivo'] = new Array('id.codtipocapital', 'id.destipocapital');
arrCamposDepende['TipoCapitalCultivo'] = new Array('plan', 'linea','codmoduloLupaTCCul','cultivoLupaTCCul','capital');
arrCamposBeanDepende['TipoCapitalCultivo'] = new Array('id.codplan', 'id.codlinea', 'id.codmodulo','id.codcultivo','id.codtipocapital');			
arrTipoBeanDepende['TipoCapitalCultivo'] = new Array('java.math.BigDecimal','java.math.BigDecimal','java.lang.String','java.math.BigDecimal','java.math.BigDecimal');
arrCamposDevolver['TipoCapitalCultivo'] = new Array('capital', 'desc_capital');
arrCamposBeanDevolver['TipoCapitalCultivo'] = new Array('id.codtipocapital', 'id.destipocapital');
arrColumnasDistinct['TipoCapitalCultivo'] = new Array('id.codtipocapital', 'id.destipocapital');
arrColumnasIsNull['TipoCapitalCultivo'] = new Array();
arrCampoLeftJoin['TipoCapitalCultivo'] = new Array();
arrCampoRestrictions['TipoCapitalCultivo'] = new Array();
arrValorRestrictions['TipoCapitalCultivo'] = new Array();
arrOperadorRestrictions['TipoCapitalCultivo'] = new Array();
arrTipoValorRestrictions['TipoCapitalCultivo'] = new Array(); 
arrValorDependeGenerico['TipoCapitalCultivo'] = new Array();
arrValorDependeOrdenGenerico['TipoCapitalCultivo'] = new Array();
// FIN Lupa de Tipos de capital segun Cultivo

//INICIO Lupa de Factores segun codconcepto
arrObjetosLupas['Factores'] = "com.rsi.agp.dao.tables.orgDat.VistaPorFactores";
arrCamposFiltrosLupas['Factores'] = new Array('filtro_Factores');
arrCamposBeanFiltros['Factores'] = new Array('id.descripcion');
arrTxtCabecerasLupas['Factores'] = new Array('Codigo', 'Descripcion');
arrColumnWidth['Factores'] = new Array('20%', '80%');
arrCamposBean['Factores'] = new Array('id.codvalor', 'id.descripcion');
arrCamposDepende['Factores'] = new Array('plan','linea','codconcepto','cicloCultivo');
arrCamposBeanDepende['Factores'] = new Array('linea.codplan', 'linea.codlinea', 'id.codconcepto','id.codvalor'); 		
arrTipoBeanDepende['Factores'] = new Array('java.math.BigDecimal','java.math.BigDecimal','java.math.BigDecimal','java.math.BigDecimal');
arrCamposDevolver['Factores'] = new Array('cicloCultivo', 'desc_cicloCultivo');
arrCamposBeanDevolver['Factores'] = new Array('id.codvalor', 'id.descripcion');
arrColumnasDistinct['Factores'] = new Array('id.codvalor', 'id.descripcion');
arrColumnasIsNull['Factores'] = new Array();
arrCampoLeftJoin['Factores'] = new Array();
arrCampoRestrictions['Factores'] = new Array();
arrValorRestrictions['Factores'] = new Array();
arrOperadorRestrictions['Factores'] = new Array();
arrTipoValorRestrictions['Factores'] = new Array(); 
arrValorDependeGenerico['Factores'] = new Array();
arrValorDependeOrdenGenerico['Factores'] = new Array();
// FIN Lupa de Factores segun codconcepto

// INICIO Lupa de Tipo de plantacion
arrObjetosLupas['TipoPlantacion'] = "com.rsi.agp.dao.tables.cgen.TipoPlantacion";
arrCamposFiltrosLupas['TipoPlantacion'] = new Array('filtro_TipoPlantacion');
arrCamposBeanFiltros['TipoPlantacion'] = new Array('destipoplantacion');
arrTxtCabecerasLupas['TipoPlantacion'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['TipoPlantacion'] = new Array('30%', '70%');
arrCamposBean['TipoPlantacion'] = new Array('codtipoplantacion', 'destipoplantacion');
arrCamposDepende['TipoPlantacion'] = new Array('tplantacion');
arrCamposBeanDepende['TipoPlantacion'] = new Array('codtipoplantacion');
arrTipoBeanDepende['TipoPlantacion'] = new Array('java.math.BigDecimal');
arrCamposDevolver['TipoPlantacion'] = new Array('tplantacion', 'desc_tplantacion');
arrCamposBeanDevolver['TipoPlantacion'] = new Array('codtipoplantacion', 'destipoplantacion');
arrColumnasDistinct['TipoPlantacion'] = new Array();
arrColumnasIsNull['TipoPlantacion'] = new Array();
arrCampoLeftJoin['TipoPlantacion'] = new Array();
arrCampoRestrictions['TipoPlantacion'] = new Array();
arrValorRestrictions['TipoPlantacion'] = new Array();
arrOperadorRestrictions['TipoPlantacion'] = new Array();
arrTipoValorRestrictions['TipoPlantacion'] = new Array(); 
arrValorDependeGenerico['TipoPlantacion'] = new Array();
arrValorDependeOrdenGenerico['TipoPlantacion'] = new Array();
// FIN Lupa de Tipo de plantacion

// INICIO Lupa de Tipo de plantacion para cambio masivo
arrObjetosLupas['TipoPlantacionCM'] = "com.rsi.agp.dao.tables.cgen.TipoPlantacion";
arrCamposFiltrosLupas['TipoPlantacionCM'] = new Array('filtro_TipoPlantacionCM');
arrCamposBeanFiltros['TipoPlantacionCM'] = new Array('destipoplantacion');
arrTxtCabecerasLupas['TipoPlantacionCM'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['TipoPlantacionCM'] = new Array('30%', '70%');
arrCamposBean['TipoPlantacionCM'] = new Array('codtipoplantacion', 'destipoplantacion');
arrCamposDepende['TipoPlantacionCM'] = new Array('tplantacion_cm');
arrCamposBeanDepende['TipoPlantacionCM'] = new Array('codtipoplantacion');
arrTipoBeanDepende['TipoPlantacionCM'] = new Array('java.math.BigDecimal');
arrCamposDevolver['TipoPlantacionCM'] = new Array('tplantacion_cm', 'desc_tplantacion_cm');
arrCamposBeanDevolver['TipoPlantacionCM'] = new Array('codtipoplantacion', 'destipoplantacion');
arrColumnasDistinct['TipoPlantacionCM'] = new Array();
arrColumnasIsNull['TipoPlantacionCM'] = new Array();
arrCampoLeftJoin['TipoPlantacionCM'] = new Array();
arrCampoRestrictions['TipoPlantacionCM'] = new Array();
arrValorRestrictions['TipoPlantacionCM'] = new Array();
arrOperadorRestrictions['TipoPlantacionCM'] = new Array();
arrTipoValorRestrictions['TipoPlantacionCM'] = new Array(); 
arrValorDependeGenerico['TipoPlantacionCM'] = new Array();
arrValorDependeOrdenGenerico['TipoPlantacionCM'] = new Array();
// FIN Lupa de Tipo de plantacion para cambio masivo

// INICIO Lupa de Usuarios para devolverlos a un campo tipo select multiple.
arrObjetosLupas['UsuarioMulti'] = "com.rsi.agp.dao.tables.commons.Usuario";
arrCamposFiltrosLupas['UsuarioMulti'] = new Array('filtro_cod_UsuarioMulti', 'filtro_nom_UsuarioMulti');
arrCamposBeanFiltros['UsuarioMulti'] = new Array('codusuario', 'nombreusu');
arrTxtCabecerasLupas['UsuarioMulti'] = new Array('C\u00F3digo', 'Nombre');
arrColumnWidth['UsuarioMulti'] = new Array('30%', '70%');
arrCamposBean['UsuarioMulti'] = new Array('codusuario', 'nombreusu');
arrCamposDepende['UsuarioMulti'] = new Array();
arrCamposBeanDepende['UsuarioMulti'] = new Array();
arrTipoBeanDepende['UsuarioMulti'] = new Array();
arrCamposDevolver['UsuarioMulti'] = new Array('usuarioTemp');
arrCamposBeanDevolver['UsuarioMulti'] = new Array('codusuario');
arrColumnasDistinct['UsuarioMulti'] = new Array();
arrColumnasIsNull['UsuarioMulti'] = new Array();
arrCampoLeftJoin['UsuarioMulti'] = new Array();
arrCampoRestrictions['UsuarioMulti'] = new Array();
arrValorRestrictions['UsuarioMulti'] = new Array();
arrOperadorRestrictions['UsuarioMulti'] = new Array();
arrTipoValorRestrictions['UsuarioMulti'] = new Array(); 
arrValorDependeGenerico['UsuarioMulti'] = new Array();
arrValorDependeOrdenGenerico['UsuarioMulti'] = new Array();
//FIN Lupa de Usuarios para devolverlos a un campo tipo select multiple.

// INICIO Lupa de Usuarios para devolverlos a un campo tipo input.
arrObjetosLupas['Usuario'] = "com.rsi.agp.dao.tables.commons.Usuario";
arrCamposFiltrosLupas['Usuario'] = new Array('filtro_cod_Usuario', 'filtro_nom_Usuario');
arrCamposBeanFiltros['Usuario'] = new Array('codusuario', 'nombreusu');
arrTxtCabecerasLupas['Usuario'] = new Array('C\u00F3digo', 'Nombre');
arrColumnWidth['Usuario'] = new Array('30%', '70%');
arrCamposBean['Usuario'] = new Array('codusuario', 'nombreusu');
arrCamposDepende['Usuario'] = new Array('codusuario', 'entidad', 'oficina', 'grupoEntidades');
arrCamposBeanDepende['Usuario'] = new Array('codusuario', 'oficina.id.codentidad', 'oficina.id.codoficina', 'oficina.id.codentidad');
arrTipoBeanDepende['Usuario'] = new Array('java.lang.String', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['Usuario'] = new Array('codusuario');
arrCamposBeanDevolver['Usuario'] = new Array('codusuario');
arrColumnasDistinct['Usuario'] = new Array();
arrColumnasIsNull['Usuario'] = new Array();
arrCampoLeftJoin['Usuario'] = new Array();
arrCampoRestrictions['Usuario'] = new Array();
arrValorRestrictions['Usuario'] = new Array();
arrOperadorRestrictions['Usuario'] = new Array();
arrTipoValorRestrictions['Usuario'] = new Array(); 
arrValorDependeGenerico['Usuario'] = new Array();
arrValorDependeOrdenGenerico['Usuario'] = new Array();
// FIN Lupa de Usuarios para devolverlos a un campo tipo input.

//INICIO Lupa de Usuarios para devolverlos a un campo tipo input.
arrObjetosLupas['UsuarioNombre'] = "com.rsi.agp.dao.tables.commons.Usuario";
arrCamposFiltrosLupas['UsuarioNombre'] = new Array('filtro_cod_Usuario', 'filtro_nom_Usuario');
arrCamposBeanFiltros['UsuarioNombre'] = new Array('codusuario', 'nombreusu');
arrTxtCabecerasLupas['UsuarioNombre'] = new Array('C\u00F3digo', 'Nombre');
arrColumnWidth['UsuarioNombre'] = new Array('30%', '70%');
arrCamposBean['UsuarioNombre'] = new Array('codusuario', 'nombreusu');
arrCamposDepende['UsuarioNombre'] = new Array('codusuario', 'entidad', 'oficina', 'grupoEntidades');
arrCamposBeanDepende['UsuarioNombre'] = new Array('codusuario', 'oficina.id.codentidad', 'oficina.id.codoficina', 'oficina.id.codentidad');
arrTipoBeanDepende['UsuarioNombre'] = new Array('java.lang.String', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['UsuarioNombre'] = new Array('codusuario','nombreusu');
arrCamposBeanDevolver['UsuarioNombre'] = new Array('codusuario','nombreusu');
arrColumnasDistinct['UsuarioNombre'] = new Array();
arrColumnasIsNull['UsuarioNombre'] = new Array();
arrCampoLeftJoin['UsuarioNombre'] = new Array();
arrCampoRestrictions['UsuarioNombre'] = new Array();
arrValorRestrictions['UsuarioNombre'] = new Array();
arrOperadorRestrictions['UsuarioNombre'] = new Array();
arrTipoValorRestrictions['UsuarioNombre'] = new Array(); 
arrValorDependeGenerico['UsuarioNombre'] = new Array();
arrValorDependeOrdenGenerico['UsuarioNombre'] = new Array();
// FIN Lupa de Usuarios para devolverlos a un campo tipo input.

//INICIO Lupa de Usuarios para devolverlos a un campo tipo input.
arrObjetosLupas['UsuarioEM'] = "com.rsi.agp.dao.tables.commons.Usuario";
arrCamposFiltrosLupas['UsuarioEM'] = new Array('filtro_cod_UsuarioEM', 'filtro_nom_UsuarioEM');
arrCamposBeanFiltros['UsuarioEM'] = new Array('codusuario', 'nombreusu');
arrTxtCabecerasLupas['UsuarioEM'] = new Array('C\u00F3digo', 'E. Med.', 'S. Med.', 'Nombre');
arrColumnWidth['UsuarioEM'] = new Array('18%', '16%', '16%', '50%');
arrCamposBean['UsuarioEM'] = new Array('codusuario', 'subentidadMediadora.id.codentidad', 'subentidadMediadora.id.codsubentidad','nombreusu');
arrCamposDepende['UsuarioEM'] = new Array('codusuario', 'entidad', 'entmediadora', 'subentmediadora');
arrCamposBeanDepende['UsuarioEM'] = new Array('codusuario', 'oficina.id.codentidad', 'subentidadMediadora.id.codentidad', 'subentidadMediadora.id.codsubentidad');
arrTipoBeanDepende['UsuarioEM'] = new Array('java.lang.String', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['UsuarioEM'] = new Array('codusuario', 'entmediadora', 'subentmediadora');
arrCamposBeanDevolver['UsuarioEM'] = new Array('codusuario', 'subentidadMediadora.id.codentidad', 'subentidadMediadora.id.codsubentidad');
arrColumnasDistinct['UsuarioEM'] = new Array();
arrColumnasIsNull['UsuarioEM'] = new Array();
arrCampoLeftJoin['UsuarioEM'] = new Array('oficina','subentidadMediadora');
arrCampoRestrictions['UsuarioEM'] = new Array();
arrValorRestrictions['UsuarioEM'] = new Array();
arrOperadorRestrictions['UsuarioEM'] = new Array();
arrTipoValorRestrictions['UsuarioEM'] = new Array(); 
arrValorDependeGenerico['UsuarioEM'] = new Array();
arrValorDependeOrdenGenerico['UsuarioEM'] = new Array();
// FIN Lupa de Usuarios para devolverlos a un campo tipo input.

//INICIO Lupa de Usuarios que filtra por 'entidad', 'oficina', 'grupoEntidades'y  e-s mediadora  delegacion
arrObjetosLupas['UsuarioFiltros'] = "com.rsi.agp.dao.tables.commons.Usuario";
arrCamposFiltrosLupas['UsuarioFiltros'] = new Array('filtro_cod_UsuarioFiltros', 'filtro_nom_UsuarioFiltros');
arrCamposBeanFiltros['UsuarioFiltros'] = new Array('codusuario', 'nombreusu');
arrTxtCabecerasLupas['UsuarioFiltros'] = new Array('C\u00F3digo', 'Nombre');
arrColumnWidth['UsuarioFiltros'] = new Array('30%', '70%');
arrCamposBean['UsuarioFiltros'] = new Array('codusuario', 'nombreusu');
arrCamposDepende['UsuarioFiltros'] = new Array('codusuario', 'entidad', 'oficina', 'grupoEntidades','entmediadora','subentmediadora','delegacion');
arrCamposBeanDepende['UsuarioFiltros'] = new Array('codusuario', 'oficina.id.codentidad', 'oficina.id.codoficina', 'oficina.id.codentidad','subentidadMediadora.id.codentidad','subentidadMediadora.id.codsubentidad','delegacion');
arrTipoBeanDepende['UsuarioFiltros'] = new Array('java.lang.String', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal','java.math.BigDecimal','java.math.BigDecimal','java.math.BigDecimal');
arrCamposDevolver['UsuarioFiltros'] = new Array('codusuario');
arrCamposBeanDevolver['UsuarioFiltros'] = new Array('codusuario');
arrColumnasDistinct['UsuarioFiltros'] = new Array();
arrColumnasIsNull['UsuarioFiltros'] = new Array();
arrCampoLeftJoin['UsuarioFiltros'] = new Array();
arrCampoRestrictions['UsuarioFiltros'] = new Array();
arrValorRestrictions['UsuarioFiltros'] = new Array();
arrOperadorRestrictions['UsuarioFiltros'] = new Array();
arrTipoValorRestrictions['UsuarioFiltros'] = new Array();
arrValorDependeGenerico['UsuarioFiltros'] = new Array();
arrValorDependeOrdenGenerico['UsuarioFiltros'] = new Array();
// FIN Lupa de Usuarios que filtra por mas campos e-s mediadora y delegacion

// INICIO Lupa de Campos para el mantenimiento de informes
arrObjetosLupas['VistaCampo'] = "com.rsi.agp.dao.tables.mtoinf.VistaCampo";
arrCamposFiltrosLupas['VistaCampo'] = new Array('filtro_VistaCampo');
arrCamposBeanFiltros['VistaCampo'] = new Array('nombre');
arrTxtCabecerasLupas['VistaCampo'] = new Array('Id tabla', 'Nombre tabla', 'Id', 'Nombre', 'Id tipo', 'Tipo');
arrColumnWidth['VistaCampo'] = new Array('10%', '30%', '10%', '25%', '10%', '15%');
arrCamposBean['VistaCampo'] = new Array('vista.id', 'vista.nombre', 'id', 'nombre', 'vistaCampoTipo.idtipo', 'vistaCampoTipo.nombreTipo');
arrCamposDepende['VistaCampo'] = new Array('tablaOrigen', 'filtro_VistaCampoVisibilidad', 'filtro_VistaCampoVisibilidad');
arrCamposBeanDepende['VistaCampo'] = new Array('vista.id', 'vista.visible', 'visible');
arrTipoBeanDepende['VistaCampo'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['VistaCampo'] = new Array('tablaOrigen', 'idVistaCampo', 'campo', 'tipo', 'nombreTipo');
arrCamposBeanDevolver['VistaCampo'] = new Array('vista.id', 'id', 'nombre','vistaCampoTipo.idtipo', 'vistaCampoTipo.nombreTipo');
arrOcultarColumnas['VistaCampo'] = new Array(true, false, true, false, true, false);
arrColumnasDistinct['VistaCampo'] = new Array();
arrColumnasIsNull['VistaCampo'] = new Array();
arrCampoLeftJoin['VistaCampo'] = new Array();
arrCampoRestrictions['VistaCampo'] = new Array();
arrValorRestrictions['VistaCampo'] = new Array();
arrOperadorRestrictions['VistaCampo'] = new Array();
arrTipoValorRestrictions['VistaCampo'] = new Array(); 
arrValorDependeGenerico['VistaCampo'] = new Array();
arrValorDependeOrdenGenerico['VistaCampo'] = new Array();
// FIN Lupa de Campos para el mantenimiento de informes

// INICIO Lupa de Campos Calculados para el mantenimiento de informes
arrObjetosLupas['CamposCalculados'] = "com.rsi.agp.dao.tables.mtoinf.CamposCalculados";
arrCamposFiltrosLupas['CamposCalculados'] = new Array('filtro_CamposCalculados');
arrCamposBeanFiltros['CamposCalculados'] = new Array('nombre');
arrTxtCabecerasLupas['CamposCalculados'] = new Array('Id', 'Nombre');
arrColumnWidth['CamposCalculados'] = new Array('20%', '80%');
arrCamposBean['CamposCalculados'] = new Array('id', 'nombre');
arrCamposDepende['CamposCalculados'] = new Array();
arrCamposBeanDepende['CamposCalculados'] = new Array();
arrTipoBeanDepende['CamposCalculados'] = new Array();
arrCamposDevolver['CamposCalculados'] = new Array('idcampoCalc', 'campo');
arrCamposBeanDevolver['CamposCalculados'] = new Array('id', 'nombre');
arrColumnasDistinct['CamposCalculados'] = new Array();
arrColumnasIsNull['CamposCalculados'] = new Array();
arrCampoLeftJoin['CamposCalculados'] = new Array();
arrCampoRestrictions['CamposCalculados'] = new Array();
arrValorRestrictions['CamposCalculados'] = new Array();
arrOperadorRestrictions['CamposCalculados'] = new Array();
arrTipoValorRestrictions['CamposCalculados'] = new Array(); 
arrValorDependeGenerico['CamposCalculados'] = new Array();
arrValorDependeOrdenGenerico['CamposCalculados'] = new Array();
// FIN Lupa de Campos Calculados para el mantenimiento de informes

// INICIO Lupa de Colectivos.
arrObjetosLupas['Colectivo'] = "com.rsi.agp.dao.tables.admin.Colectivo";
arrCamposFiltrosLupas['Colectivo'] = new Array('filtro_Colectivo');
arrCamposBeanFiltros['Colectivo'] = new Array('nomcolectivo');
arrTxtCabecerasLupas['Colectivo'] = new Array('Entidad', 'Referencia', 'D.C.', 'Nombre');
arrColumnWidth['Colectivo'] = new Array('20%', '20%', '10%', '50%');
arrCamposBean['Colectivo'] = new Array('tomador.id.codentidad', 'idcolectivo', 'dc', 'nomcolectivo');
arrCamposDepende['Colectivo'] = new Array('entidad');
arrCamposBeanDepende['Colectivo'] = new Array('tomador.id.codentidad');
arrTipoBeanDepende['Colectivo'] = new Array('java.math.BigDecimal');
arrCamposDevolver['Colectivo'] = new Array('entidad', 'colectivo', 'dc');
arrCamposBeanDevolver['Colectivo'] = new Array('tomador.id.codentidad', 'idcolectivo', 'dc');
arrColumnasDistinct['Colectivo'] = new Array();
arrColumnasIsNull['Colectivo'] = new Array();
arrCampoLeftJoin['Colectivo'] = new Array();
arrCampoRestrictions['Colectivo'] = new Array();
arrValorRestrictions['Colectivo'] = new Array();
arrOperadorRestrictions['Colectivo'] = new Array();
arrTipoValorRestrictions['Colectivo'] = new Array();
arrValorDependeGenerico['Colectivo'] = new Array();
arrValorDependeOrdenGenerico['Colectivo'] = new Array();
// FIN Lupa de Colectivos.

// INICIO Lupa de Modulos
arrObjetosLupas['Modulo'] = "com.rsi.agp.dao.tables.cpl.Modulo";
arrCamposFiltrosLupas['Modulo'] = new Array('filtro_Modulo');
arrCamposBeanFiltros['Modulo'] = new Array('id.codmodulo');
arrTxtCabecerasLupas['Modulo'] = new Array('Plan', 'Cod. L\u00EDnea', 'Nom. L\u00EDnea', 'M\u00F3dulo');
arrColumnWidth['Modulo'] = new Array('20%', '20%', '40%', '20%');
arrCamposBean['Modulo'] = new Array('linea.codplan', 'linea.codlinea', 'linea.nomlinea', 'id.codmodulo');
arrCamposDepende['Modulo'] = new Array('plan', 'linea', 'codmodulo');
arrCamposBeanDepende['Modulo'] = new Array('linea.codplan', 'linea.codlinea', 'id.codmodulo');
arrTipoBeanDepende['Modulo'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.String');
arrCamposDevolver['Modulo'] = new Array('plan', 'linea', 'desc_linea', 'codmodulo');
arrCamposBeanDevolver['Modulo'] = new Array('linea.codplan', 'linea.codlinea', 'linea.nomlinea', 'id.codmodulo');
arrColumnasDistinct['Modulo'] = new Array();
arrColumnasIsNull['Modulo'] = new Array();
arrCampoLeftJoin['Modulo'] = new Array();
arrCampoRestrictions['Modulo'] = new Array();
arrValorRestrictions['Modulo'] = new Array();
arrOperadorRestrictions['Modulo'] = new Array();
arrTipoValorRestrictions['Modulo'] = new Array(); 
arrValorDependeGenerico['Modulo'] = new Array();
arrValorDependeOrdenGenerico['Modulo'] = new Array();
// FIN Lupa de Modulos

//INICIO Lupa de Recibos Emitidos bonificaciones y recargos (Comisiones)
arrObjetosLupas['EmitidosApliBonifRecargos'] = "com.rsi.agp.dao.tables.comisiones.recibosEmitidos.ReciboEmitidoApliBonRec";
arrCamposFiltrosLupas['EmitidosApliBonifRecargos'] = new Array();
arrCamposBeanFiltros['EmitidosApliBonifRecargos'] = new Array('reciboEmitidoAplicacion.id');
arrTxtCabecerasLupas['EmitidosApliBonifRecargos'] = new Array('Tipo', 'Descripci\u00F3n', 'Importe');
arrColumnWidth['EmitidosApliBonifRecargos'] = new Array('20%', '60%', '20%');
arrCamposBean['EmitidosApliBonifRecargos'] = new Array('bonRecTipo', 'bonRecDescripcion', 'importe');
arrCamposDepende['EmitidosApliBonifRecargos'] = new Array('idEmitidosApli');
arrCamposBeanDepende['EmitidosApliBonifRecargos'] = new Array('reciboEmitidoAplicacion.id');
arrTipoBeanDepende['EmitidosApliBonifRecargos'] = new Array('java.lang.Long');
arrCamposDevolver['EmitidosApliBonifRecargos'] = new Array();
arrCamposBeanDevolver['EmitidosApliBonifRecargos'] = new Array();
arrColumnasDistinct['EmitidosApliBonifRecargos'] = new Array();
arrColumnasIsNull['EmitidosApliBonifRecargos'] = new Array();
arrCampoLeftJoin['EmitidosApliBonifRecargos'] = new Array();
arrCampoRestrictions['EmitidosApliBonifRecargos'] = new Array();
arrValorRestrictions['EmitidosApliBonifRecargos'] = new Array();
arrOperadorRestrictions['EmitidosApliBonifRecargos'] = new Array();
arrTipoValorRestrictions['EmitidosApliBonifRecargos'] = new Array(); 
arrValorDependeGenerico['EmitidosApliBonifRecargos'] = new Array();
arrValorDependeOrdenGenerico['EmitidosApliBonifRecargos'] = new Array();
//FIN Lupa de Lupa de Recibos Emitidos bonificaciones y recargos (Comisiones)

// INICIO Lupa de Recibos Emitidos Subvencion CCAA (Comisiones)
arrObjetosLupas['EmitidosSubvCCAA'] = "com.rsi.agp.dao.tables.comisiones.recibosEmitidos.ReciboEmitidoSubvCCA";
arrCamposFiltrosLupas['EmitidosSubvCCAA'] = new Array();
arrCamposBeanFiltros['EmitidosSubvCCAA'] = new Array('reciboEmitido.id');
arrTxtCabecerasLupas['EmitidosSubvCCAA'] = new Array('C\u00F3digo', 'Subvenciones Comunidades');
arrColumnWidth['EmitidosSubvCCAA'] = new Array('20%', '40%');
arrCamposBean['EmitidosSubvCCAA'] = new Array('codigo', 'subvcomunidades');
arrCamposDepende['EmitidosSubvCCAA'] = new Array('idEmitidos');
arrCamposBeanDepende['EmitidosSubvCCAA'] = new Array('reciboEmitido.id');
arrTipoBeanDepende['EmitidosSubvCCAA'] = new Array('java.lang.Long');
arrCamposDevolver['EmitidosSubvCCAA'] = new Array();
arrCamposBeanDevolver['EmitidosSubvCCAA'] = new Array();
arrColumnasDistinct['EmitidosSubvCCAA'] = new Array();
arrColumnasIsNull['EmitidosSubvCCAA'] = new Array();
arrCampoLeftJoin['EmitidosSubvCCAA'] = new Array();
arrCampoRestrictions['EmitidosSubvCCAA'] = new Array();
arrValorRestrictions['EmitidosSubvCCAA'] = new Array();
arrOperadorRestrictions['EmitidosSubvCCAA'] = new Array();
arrTipoValorRestrictions['EmitidosSubvCCAA'] = new Array();
arrValorDependeGenerico['EmitidosSubvCCAA'] = new Array();
arrValorDependeOrdenGenerico['EmitidosSubvCCAA'] = new Array();
// FIN Lupa de Lupa de Recibos Emitidos Subvencion CCAA (Comisiones)

// INICIO Lupa de Recibos Emitidos Detalle Compensacion (Comisiones)
arrObjetosLupas['EmitidosDetComp'] = "com.rsi.agp.dao.tables.comisiones.recibosEmitidos.ReciboEmitidoDetComp";
arrCamposFiltrosLupas['EmitidosDetComp'] = new Array();
arrCamposBeanFiltros['EmitidosDetComp'] = new Array('reciboEmitido.id');
arrTxtCabecerasLupas['EmitidosDetComp'] = new Array('Plan', 'L\u00EDnea','Recibo','Cobro');
arrColumnWidth['EmitidosDetComp'] = new Array('20%', '20%','30%','30%');
arrCamposBean['EmitidosDetComp'] = new Array('plan', 'linea','recibo','cobro');
arrCamposDepende['EmitidosDetComp'] = new Array('idEmitidos');
arrCamposBeanDepende['EmitidosDetComp'] = new Array('reciboEmitido.id');
arrTipoBeanDepende['EmitidosDetComp'] = new Array('java.lang.Long');
arrCamposDevolver['EmitidosDetComp'] = new Array();
arrCamposBeanDevolver['EmitidosDetComp'] = new Array();
arrColumnasDistinct['EmitidosDetComp'] = new Array();
arrColumnasIsNull['EmitidosDetComp'] = new Array();
arrCampoLeftJoin['EmitidosDetComp'] = new Array();
arrCampoRestrictions['EmitidosDetComp'] = new Array();
arrValorRestrictions['EmitidosDetComp'] = new Array();
arrOperadorRestrictions['EmitidosDetComp'] = new Array();
arrTipoValorRestrictions['EmitidosDetComp'] = new Array(); 
arrValorDependeGenerico['EmitidosDetComp'] = new Array();
arrValorDependeOrdenGenerico['EmitidosDetComp'] = new Array();
// FIN Lupa de Lupa de Recibos Emitidos Detalle Compensacion (Comisiones)

// INICIO Lupa de Recibos Emitidos Subvencion CCAA Aplicacion (Comisiones)
arrObjetosLupas['EmitidosApliSubvCCAA'] = "com.rsi.agp.dao.tables.comisiones.recibosEmitidos.ReciboEmitidoApliSubvCCAA";
arrCamposFiltrosLupas['EmitidosApliSubvCCAA'] = new Array();
arrCamposBeanFiltros['EmitidosApliSubvCCAA'] = new Array('reciboEmitidoAplicacion.id');
arrTxtCabecerasLupas['EmitidosApliSubvCCAA'] = new Array('C\u00F3digo', 'Subvenci\u00F3n Comunidades');
arrColumnWidth['EmitidosApliSubvCCAA'] = new Array('20%', '20%','30%','30%');
arrCamposBean['EmitidosApliSubvCCAA'] = new Array('codigo', 'subvcomunidades');
arrCamposDepende['EmitidosApliSubvCCAA'] = new Array('idEmitidosApli');
arrCamposBeanDepende['EmitidosApliSubvCCAA'] = new Array('reciboEmitidoAplicacion.id');
arrTipoBeanDepende['EmitidosApliSubvCCAA'] = new Array('java.lang.Long');
arrCamposDevolver['EmitidosApliSubvCCAA'] = new Array();
arrCamposBeanDevolver['EmitidosApliSubvCCAA'] = new Array();
arrColumnasDistinct['EmitidosApliSubvCCAA'] = new Array();
arrColumnasIsNull['EmitidosApliSubvCCAA'] = new Array();
arrCampoLeftJoin['EmitidosApliSubvCCAA'] = new Array();
arrCampoRestrictions['EmitidosApliSubvCCAA'] = new Array();
arrValorRestrictions['EmitidosApliSubvCCAA'] = new Array();
arrOperadorRestrictions['EmitidosApliSubvCCAA'] = new Array();
arrTipoValorRestrictions['EmitidosApliSubvCCAA'] = new Array(); 
arrValorDependeGenerico['EmitidosApliSubvCCAA'] = new Array();
arrValorDependeOrdenGenerico['EmitidosApliSubvCCAA'] = new Array();
// FIN Lupa de Lupa de Recibos Emitidos Subvencion CCAA Aplicacion(Comisiones)

//INICIO Lupa de errores ws tipos
// Pet. 63481 ** MODIF TAM (12.05.2021) ** Se a�ade el nuevo campo de catalogo
arrObjetosLupas['ErrorWs'] = "com.rsi.agp.dao.tables.commons.ErrorWs";
arrCamposFiltrosLupas['ErrorWs'] = new Array('filtro_codErrorWs', 'filtro_ErrorWs');
arrCamposBeanFiltros['ErrorWs'] = new Array('id.coderror', 'descripcion');
arrTipoCamposBeanFiltros['ErrorWs'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['ErrorWs'] = new Array('Catalogo', 'Cod. Error', 'Descripcion', 'Tipo');
arrColumnWidth['ErrorWs'] = new Array('20%', '20%', '40%', '20%');
arrCamposBean['ErrorWs'] = new Array('id.catalogo','id.coderror', 'descripcion', 'errorWsTipo.descripcion', 'errorWsTipo.codigo');
arrCamposDepende['ErrorWs'] = new Array('catalogo','coderror','desc_error','codigoTipo');
arrCamposBeanDepende['ErrorWs'] = new Array('id.catalogo', 'id.coderror', 'descripcion', 'errorWsTipo.codigo');
arrTipoBeanDepende['ErrorWs'] = new Array('java.lang.String', 'java.math.BigDecimal', 'java.lang.String', 'java.lang.String');
arrCamposDevolver['ErrorWs'] = new Array('catalogo', 'coderror', 'desc_error', 'codigoTipo');
arrCamposBeanDevolver['ErrorWs'] = new Array('id.catalogo', 'id.coderror', 'descripcion', 'errorWsTipo.codigo');
arrOcultarColumnas['ErrorWs'] = new Array(false, false, false, false, true);
arrColumnasDistinct['ErrorWs'] = new Array();
arrColumnasIsNull['ErrorWs'] = new Array();
arrCampoLeftJoin['ErrorWs'] = new Array();
arrCampoRestrictions['ErrorWs'] = new Array();
arrValorRestrictions['ErrorWs'] = new Array();
arrOperadorRestrictions['ErrorWs'] = new Array();
arrTipoValorRestrictions['ErrorWs'] = new Array(); 
arrValorDependeGenerico['ErrorWs'] = new Array();
arrValorDependeOrdenGenerico['ErrorWs'] = new Array();
// FIN Lupa de errores ws tipos

// INICIO Lupa de CPM
arrObjetosLupas['CPM'] = "com.rsi.agp.dao.tables.orgDat.VistaConceptoPpalModulo";
arrCamposFiltrosLupas['CPM'] = new Array('filtro_CPM');
arrCamposBeanFiltros['CPM'] = new Array('id.desconceptoppalmod');
arrTxtCabecerasLupas['CPM'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['CPM'] = new Array('30%', '70%');
arrCamposBean['CPM'] = new Array('id.codconceptoppalmod', 'id.desconceptoppalmod');
arrCamposDepende['CPM'] = new Array('plan', 'linea','codmoduloLupaCPM','cpm');
arrCamposBeanDepende['CPM'] = new Array('id.codplan', 'id.codlinea', 'id.codmodulo','id.codconceptoppalmod');
arrTipoBeanDepende['CPM'] = new Array('java.math.BigDecimal','java.math.BigDecimal','java.lang.String','java.math.BigDecimal');
arrCamposDevolver['CPM'] = new Array('cpm', 'desc_cpm');
arrCamposBeanDevolver['CPM'] = new Array('id.codconceptoppalmod','id.desconceptoppalmod');
arrColumnasDistinct['CPM'] = new Array('id.codconceptoppalmod','id.desconceptoppalmod');
arrColumnasIsNull['CPM'] = new Array();
arrCampoLeftJoin['CPM'] = new Array();
arrCampoRestrictions['CPM'] = new Array();
arrValorRestrictions['CPM'] = new Array();
arrOperadorRestrictions['CPM'] = new Array();
arrTipoValorRestrictions['CPM'] = new Array(); 
arrValorDependeGenerico['CPM'] = new Array();
arrValorDependeOrdenGenerico['CPM'] = new Array();
// FIN Lupa de Tipos CPM

// INICIO Lupa de ImpuestoSbp
arrObjetosLupas['ImpuestoSbp'] = "com.rsi.agp.dao.tables.sbp.ImpuestoSbp";
arrCamposFiltrosLupas['ImpuestoSbp'] = new Array('filtro_ImpuestoSbp');
arrCamposBeanFiltros['ImpuestoSbp'] = new Array('descripcion');
arrTxtCabecerasLupas['ImpuestoSbp'] = new Array('C\u00F3digo', 'Nombre');
arrColumnWidth['ImpuestoSbp'] = new Array('30%', '70%');
arrCamposBean['ImpuestoSbp'] = new Array('codigo', 'descripcion');
arrCamposDepende['ImpuestoSbp'] = new Array('codimpuesto');
arrCamposBeanDepende['ImpuestoSbp'] = new Array('codigo');
arrTipoBeanDepende['ImpuestoSbp'] = new Array('java.lang.String');
arrCamposDevolver['ImpuestoSbp'] = new Array('codimpuesto', 'nomimpuesto');
arrCamposBeanDevolver['ImpuestoSbp'] = new Array('codigo','descripcion');
arrColumnasDistinct['ImpuestoSbp'] = new Array();
arrColumnasIsNull['ImpuestoSbp'] = new Array();
arrCampoLeftJoin['ImpuestoSbp'] = new Array();
arrCampoRestrictions['ImpuestoSbp'] = new Array();
arrValorRestrictions['ImpuestoSbp'] = new Array();
arrOperadorRestrictions['ImpuestoSbp'] = new Array();
arrTipoValorRestrictions['ImpuestoSbp'] = new Array(); 
arrValorDependeGenerico['ImpuestoSbp'] = new Array();
arrValorDependeOrdenGenerico['ImpuestoSbp'] = new Array();
// FIN Lupa de Tipos ImpuestoSbp

// INICIO Lupa de BaseSbp
arrObjetosLupas['BaseSbp'] = "com.rsi.agp.dao.tables.sbp.BaseSbp";
arrCamposFiltrosLupas['BaseSbp'] = new Array('filtro_BaseSbp');
arrCamposBeanFiltros['BaseSbp'] = new Array('base');
arrTxtCabecerasLupas['BaseSbp'] = new Array('Nombre');
arrColumnWidth['BaseSbp'] = new Array('100%');
arrCamposBean['BaseSbp'] = new Array('base');
arrCamposDepende['BaseSbp'] = new Array('nombase');
arrCamposBeanDepende['BaseSbp'] = new Array('base');
arrTipoBeanDepende['BaseSbp'] = new Array('java.lang.String');
arrCamposDevolver['BaseSbp'] = new Array('nombase');
arrCamposBeanDevolver['BaseSbp'] = new Array('base');
arrColumnasDistinct['BaseSbp'] = new Array();
arrColumnasIsNull['BaseSbp'] = new Array();
arrCampoLeftJoin['BaseSbp'] = new Array();
arrCampoRestrictions['BaseSbp'] = new Array();
arrValorRestrictions['BaseSbp'] = new Array();
arrOperadorRestrictions['BaseSbp'] = new Array();
arrTipoValorRestrictions['BaseSbp'] = new Array(); 
arrValorDependeGenerico['BaseSbp'] = new Array();
arrValorDependeOrdenGenerico['BaseSbp'] = new Array();
// FIN Lupa de Tipos BaseSbp

//INICIO Lupa bancos
arrObjetosLupas['Banco'] = "com.rsi.agp.dao.tables.cgen.Banco";
arrCamposFiltrosLupas['Banco'] = new Array('filtro_codBanco', 'filtro_Banco');
arrCamposBeanFiltros['Banco'] = new Array('id.codbanco', 'nombanco');
arrTipoCamposBeanFiltros['Banco'] = new Array('java.lang.String', 'java.lang.String');
arrTxtCabecerasLupas['Banco'] = new Array('C\u00F3digo','Nombre');
arrColumnWidth['Banco'] = new Array('30%','70%');
arrCamposBean['Banco'] = new Array('id.codbanco','nombanco');
arrCamposDepende['Banco'] = new Array('bancoDestino');
arrCamposBeanDepende['Banco'] = new Array('id.codbanco');
arrTipoBeanDepende['Banco'] = new Array('java.lang.String');
arrCamposDevolver['Banco'] = new Array('bancoDestino');
arrCamposBeanDevolver['Banco'] = new Array('id.codbanco');
arrColumnasDistinct['Banco'] = new Array();
arrColumnasIsNull['Banco'] = new Array();
arrCampoLeftJoin['Banco'] = new Array();
arrCampoRestrictions['Banco'] = new Array();
arrValorRestrictions['Banco'] = new Array();
arrOperadorRestrictions['Banco'] = new Array();
arrTipoValorRestrictions['Banco'] = new Array(); 
arrValorDependeGenerico['Banco'] = new Array();
arrValorDependeOrdenGenerico['Banco'] = new Array();
// FIN Lupa bancos

//INICIO Lupa de Especies
arrObjetosLupas['Especie'] = "com.rsi.agp.dao.tables.cpl.gan.Especie";
arrCamposFiltrosLupas['Especie'] = new Array('filtro_codEspecie', 'filtro_Especie');
arrCamposBeanFiltros['Especie'] = new Array('id.codespecie', 'descripcion');
arrTipoCamposBeanFiltros['Especie'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['Especie'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['Especie'] = new Array('30%', '70%');
arrCamposBean['Especie'] = new Array('id.codespecie', 'descripcion');
arrCamposDepende['Especie'] = new Array('lineaseguroid', 'especie');
arrCamposBeanDepende['Especie'] = new Array('id.lineaseguroid', 'id.codespecie');
arrTipoBeanDepende['Especie'] = new Array('java.lang.Long', 'java.lang.Long');
arrCamposDevolver['Especie'] = new Array('especie', 'desc_especie');
arrCamposBeanDevolver['Especie'] = new Array('id.codespecie', 'descripcion');
arrColumnasDistinct['Especie'] = new Array();
arrColumnasIsNull['Especie'] = new Array();
arrCampoLeftJoin['Especie'] = new Array();
arrCampoRestrictions['Especie'] = new Array();
arrValorRestrictions['Especie'] = new Array();
arrOperadorRestrictions['Especie'] = new Array();
arrTipoValorRestrictions['Especie'] = new Array(); 
arrValorDependeGenerico['Especie'] = new Array();
arrValorDependeOrdenGenerico['Especie'] = new Array();

// FIN Lupa de Especies

//INICIO Lupa de Especies CAMBIO MASIVO
arrObjetosLupas['EspecieCM'] = "com.rsi.agp.dao.tables.cpl.gan.Especie";
arrCamposFiltrosLupas['EspecieCM'] = new Array('filtro_codEspecieCM', 'filtro_Especie_CM');
arrCamposBeanFiltros['EspecieCM'] = new Array('id.codespecie', 'descripcion');
arrTipoCamposBeanFiltros['EspecieCM'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['EspecieCM'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['EspecieCM'] = new Array('30%', '70%');
arrCamposBean['EspecieCM'] = new Array('id.codespecie', 'descripcion');
arrCamposDepende['EspecieCM'] = new Array('lineaseguroid', 'especie_cm');
arrCamposBeanDepende['EspecieCM'] = new Array('id.lineaseguroid', 'id.codespecie');
arrTipoBeanDepende['EspecieCM'] = new Array('java.lang.Long', 'java.lang.Long');
arrCamposDevolver['EspecieCM'] = new Array('especie_cm', 'desc_especie_cm');
arrCamposBeanDevolver['EspecieCM'] = new Array('id.codespecie', 'descripcion');
arrColumnasDistinct['EspecieCM'] = new Array();
arrColumnasIsNull['EspecieCM'] = new Array();
arrCampoLeftJoin['EspecieCM'] = new Array();
arrCampoRestrictions['EspecieCM'] = new Array();
arrValorRestrictions['EspecieCM'] = new Array();
arrOperadorRestrictions['EspecieCM'] = new Array();
arrTipoValorRestrictions['EspecieCM'] = new Array(); 
arrValorDependeGenerico['EspecieCM'] = new Array();
arrValorDependeOrdenGenerico['EspecieCM'] = new Array();
// FIN Lupa de Especies CAMBIO MASIVO

//INICIO Lupa de Regimenes
arrObjetosLupas['Regimen'] = "com.rsi.agp.dao.tables.cpl.gan.RegimenManejo";
arrCamposFiltrosLupas['Regimen'] = new Array('filtro_codRegimen', 'filtro_Regimen');
arrCamposBeanFiltros['Regimen'] = new Array('id.codRegimen', 'descripcion');
arrTipoCamposBeanFiltros['Regimen'] = new Array('java.lang.Long', 'java.lang.String');
arrTxtCabecerasLupas['Regimen'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['Regimen'] = new Array('30%', '70%');
arrCamposBean['Regimen'] = new Array('id.codRegimen', 'descripcion');
arrCamposDepende['Regimen'] = new Array('lineaseguroid', 'regimen');
arrCamposBeanDepende['Regimen'] = new Array('id.lineaseguroid', 'id.codRegimen');
arrTipoBeanDepende['Regimen'] = new Array('java.lang.Long', 'java.lang.Long');
arrCamposDevolver['Regimen'] = new Array('regimen', 'desc_regimen');
arrCamposBeanDevolver['Regimen'] = new Array('id.codRegimen', 'descripcion');
arrColumnasDistinct['Regimen'] = new Array();
arrColumnasIsNull['Regimen'] = new Array();
arrCampoLeftJoin['Regimen'] = new Array();
arrCampoRestrictions['Regimen'] = new Array();
arrValorRestrictions['Regimen'] = new Array();
arrOperadorRestrictions['Regimen'] = new Array();
arrTipoValorRestrictions['Regimen'] = new Array();
arrValorDependeGenerico['Regimen'] = new Array();
arrValorDependeOrdenGenerico['Regimen'] = new Array();
// FIN Lupa de Regimenes 

//INICIO Lupa de Regimenes CAMBIO MASIVO
arrObjetosLupas['RegimenCM'] = "com.rsi.agp.dao.tables.cpl.gan.RegimenManejo";
arrCamposFiltrosLupas['RegimenCM'] = new Array('filtro_codRegimenCM', 'filtro_Regimen_cm');
arrCamposBeanFiltros['RegimenCM'] = new Array('id.codRegimen', 'descripcion');
arrTipoCamposBeanFiltros['RegimenCM'] = new Array('java.lang.Long', 'java.lang.String');
arrTxtCabecerasLupas['RegimenCM'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['RegimenCM'] = new Array('30%', '70%');
arrCamposBean['RegimenCM'] = new Array('id.codRegimen', 'descripcion');
arrCamposDepende['RegimenCM'] = new Array('lineaseguroid', 'regimen_cm');
arrCamposBeanDepende['RegimenCM'] = new Array('id.lineaseguroid', 'id.codRegimen');
arrTipoBeanDepende['RegimenCM'] = new Array('java.lang.Long', 'java.lang.Long');
arrCamposDevolver['RegimenCM'] = new Array('regimen_cm', 'desc_regimen_cm');
arrCamposBeanDevolver['RegimenCM'] = new Array('id.codRegimen', 'descripcion');
arrColumnasDistinct['RegimenCM'] = new Array();
arrColumnasIsNull['RegimenCM'] = new Array();
arrCampoLeftJoin['RegimenCM'] = new Array();
arrCampoRestrictions['RegimenCM'] = new Array();
arrValorRestrictions['RegimenCM'] = new Array();
arrOperadorRestrictions['RegimenCM'] = new Array();
arrTipoValorRestrictions['RegimenCM'] = new Array();
arrValorDependeGenerico['RegimenCM'] = new Array();
arrValorDependeOrdenGenerico['RegimenCM'] = new Array();
// FIN Lupa de Regimenes CAMBIO MASIVO
 
//INICIO Lupa de Grupos de Raza
arrObjetosLupas['GrupoRaza'] = "com.rsi.agp.dao.tables.cpl.gan.GruposRazas";
arrCamposFiltrosLupas['GrupoRaza'] = new Array('filtro_GrupoRaza');
arrCamposBeanFiltros['GrupoRaza'] = new Array('desGrupoRaza');
arrTxtCabecerasLupas['GrupoRaza'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['GrupoRaza'] = new Array('30%', '70%');
arrCamposBean['GrupoRaza'] = new Array('id.codGrupoRaza', 'descripcion');
arrCamposDepende['GrupoRaza'] = new Array('lineaseguroid', 'codgrupoRaza');
arrCamposBeanDepende['GrupoRaza'] = new Array('id.lineaseguroid','id.CodGrupoRaza');
arrTipoBeanDepende['GrupoRaza'] = new Array('java.lang.Long','java.lang.Long');
arrCamposDevolver['GrupoRaza'] = new Array('codgrupoRaza', 'desGrupoRaza');
arrCamposBeanDevolver['GrupoRaza'] = new Array('id.codGrupoRaza', 'descripcion');
arrColumnasDistinct['GrupoRaza'] = new Array();
arrColumnasIsNull['GrupoRaza'] = new Array();
arrCampoLeftJoin['GrupoRaza'] = new Array();
arrCampoRestrictions['GrupoRaza'] = new Array();
arrValorRestrictions['GrupoRaza'] = new Array();
arrOperadorRestrictions['GrupoRaza'] = new Array();
arrTipoValorRestrictions['GrupoRaza'] = new Array();
arrValorDependeGenerico['GrupoRaza'] = new Array();
arrValorDependeOrdenGenerico['GrupoRaza'] = new Array();
// FIN Lupa de Grupos de Raza 

//INICIO Lupa de Grupos de Raza CAMBIO MASIVO
arrObjetosLupas['GrupoRazaCM'] = "com.rsi.agp.dao.tables.cpl.gan.GruposRazas";
arrCamposFiltrosLupas['GrupoRazaCM'] = new Array('filtro_GrupoRaza_cm');
arrCamposBeanFiltros['GrupoRazaCM'] = new Array('desGrupoRaza');
arrTxtCabecerasLupas['GrupoRazaCM'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['GrupoRazaCM'] = new Array('30%', '70%');
arrCamposBean['GrupoRazaCM'] = new Array('id.codGrupoRaza', 'descripcion');
arrCamposDepende['GrupoRazaCM'] = new Array('lineaseguroid', 'codgrupoRaza_cm');
arrCamposBeanDepende['GrupoRazaCM'] = new Array('id.lineaseguroid','id.CodGrupoRaza');
arrTipoBeanDepende['GrupoRazaCM'] = new Array('java.lang.Long','java.lang.Long');
arrCamposDevolver['GrupoRazaCM'] = new Array('codgrupoRaza_cm', 'desGrupoRaza_cm');
arrCamposBeanDevolver['GrupoRazaCM'] = new Array('id.codGrupoRaza', 'descripcion');
arrColumnasDistinct['GrupoRazaCM'] = new Array();
arrColumnasIsNull['GrupoRazaCM'] = new Array();
arrCampoLeftJoin['GrupoRazaCM'] = new Array();
arrCampoRestrictions['GrupoRazaCM'] = new Array();
arrValorRestrictions['GrupoRazaCM'] = new Array();
arrOperadorRestrictions['GrupoRazaCM'] = new Array();
arrTipoValorRestrictions['GrupoRazaCM'] = new Array();
arrValorDependeGenerico['GrupoRazaCM'] = new Array();
arrValorDependeOrdenGenerico['GrupoRazaCM'] = new Array();
// FIN Lupa de Grupos de Raza CAMBIO MASIVO

//INICIO Lupa de Tipos de Capital Con Grupo Negocio
arrObjetosLupas['TipoCapitalGrupoNegocio'] = "com.rsi.agp.dao.tables.cgen.TipoCapitalConGrupoNegocio";
arrCamposFiltrosLupas['TipoCapitalGrupoNegocio'] = new Array('filtro_TipoCapitalGrupoNegocio');
arrCamposBeanFiltros['TipoCapitalGrupoNegocio'] = new Array('desTipoCapital');
arrTxtCabecerasLupas['TipoCapitalGrupoNegocio'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['TipoCapitalGrupoNegocio'] = new Array('30%', '70%');
arrCamposBean['TipoCapitalGrupoNegocio'] = new Array('codtipocapital', 'destipocapital');
arrCamposDepende['TipoCapitalGrupoNegocio'] = new Array('codtipocapital');
arrCamposBeanDepende['TipoCapitalGrupoNegocio'] = new Array('codtipocapital');
arrTipoBeanDepende['TipoCapitalGrupoNegocio'] = new Array('java.lang.Long');
arrCamposDevolver['TipoCapitalGrupoNegocio'] = new Array('codtipocapital', 'desTipoCapital');
arrCamposBeanDevolver['TipoCapitalGrupoNegocio'] = new Array('codtipocapital', 'destipocapital');
arrColumnasDistinct['TipoCapitalGrupoNegocio'] = new Array();
arrColumnasIsNull['TipoCapitalGrupoNegocio'] = new Array();
arrCampoLeftJoin['TipoCapitalGrupoNegocio'] = new Array();
arrCampoRestrictions['TipoCapitalGrupoNegocio'] = new Array();
arrValorRestrictions['TipoCapitalGrupoNegocio'] = new Array();
arrOperadorRestrictions['TipoCapitalGrupoNegocio'] = new Array();
arrTipoValorRestrictions['TipoCapitalGrupoNegocio'] = new Array(); 
arrValorDependeGenerico['TipoCapitalGrupoNegocio'] = new Array();
arrValorDependeOrdenGenerico['TipoCapitalGrupoNegocio'] = new Array();
// FIN Lupa de Tipos de Capital Con Grupo Negocio 

//INICIO Lupa de Tipos de Capital Con Grupo Negocio CAMBIO MASIVO
arrObjetosLupas['TipoCapitalGrupoNegocioCM'] = "com.rsi.agp.dao.tables.cgen.TipoCapitalConGrupoNegocio";
arrCamposFiltrosLupas['TipoCapitalGrupoNegocioCM'] = new Array('filtro_TipoCapitalGrupoNegocio_CM');
arrCamposBeanFiltros['TipoCapitalGrupoNegocioCM'] = new Array('desTipoCapital');
arrTxtCabecerasLupas['TipoCapitalGrupoNegocioCM'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['TipoCapitalGrupoNegocioCM'] = new Array('30%', '70%');
arrCamposBean['TipoCapitalGrupoNegocioCM'] = new Array('codtipocapital', 'destipocapital');
arrCamposDepende['TipoCapitalGrupoNegocioCM'] = new Array('codtipocapital_cm', 'grupoNegocio_cm');
arrCamposBeanDepende['TipoCapitalGrupoNegocioCM'] = new Array('codtipocapital', 'gruposNegocio.grupoNegocio');
arrTipoBeanDepende['TipoCapitalGrupoNegocioCM'] = new Array('java.lang.Long', 'java.lang.String');
arrCamposDevolver['TipoCapitalGrupoNegocioCM'] = new Array('codtipocapital_cm', 'desTipoCapital_cm');
arrCamposBeanDevolver['TipoCapitalGrupoNegocioCM'] = new Array('codtipocapital', 'destipocapital');
arrColumnasDistinct['TipoCapitalGrupoNegocioCM'] = new Array();
arrColumnasIsNull['TipoCapitalGrupoNegocioCM'] = new Array();
arrCampoLeftJoin['TipoCapitalGrupoNegocioCM'] = new Array();
arrCampoRestrictions['TipoCapitalGrupoNegocioCM'] = new Array();
arrValorRestrictions['TipoCapitalGrupoNegocioCM'] = new Array();
arrOperadorRestrictions['TipoCapitalGrupoNegocioCM'] = new Array();
arrTipoValorRestrictions['TipoCapitalGrupoNegocioCM'] = new Array(); 
arrValorDependeGenerico['TipoCapitalGrupoNegocioCM'] = new Array();
arrValorDependeOrdenGenerico['TipoCapitalGrupoNegocioCM'] = new Array();
// FIN Lupa de Tipos de Capital Con Grupo Negocio CAMBIO MASIVO


//INICIO Lupa de Tipos de Animal
arrObjetosLupas['TiposAnimalGanado'] = "com.rsi.agp.dao.tables.cpl.gan.TiposAnimalGanado";
arrCamposFiltrosLupas['TiposAnimalGanado'] = new Array('filtro_TiposAnimalGanado');
arrCamposBeanFiltros['TiposAnimalGanado'] = new Array('desTipoAnimal');
arrTxtCabecerasLupas['TiposAnimalGanado'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['TiposAnimalGanado'] = new Array('30%', '70%');
arrCamposBean['TiposAnimalGanado'] = new Array('id.codTipoAnimal', 'descripcion');
arrCamposDepende['TiposAnimalGanado'] = new Array('lineaseguroid', 'codtipoanimal');
arrCamposBeanDepende['TiposAnimalGanado'] = new Array('id.lineaseguroid', 'id.codTipoAnimal');
arrTipoBeanDepende['TiposAnimalGanado'] = new Array('java.lang.Long', 'java.lang.Long');
arrCamposDevolver['TiposAnimalGanado'] = new Array('codtipoanimal', 'desTipoAnimal');
arrCamposBeanDevolver['TiposAnimalGanado'] = new Array('id.codTipoAnimal', 'descripcion'); 
arrColumnasDistinct['TiposAnimalGanado'] = new Array();
arrColumnasIsNull['TiposAnimalGanado'] = new Array();
arrCampoLeftJoin['TiposAnimalGanado'] = new Array();
arrCampoRestrictions['TiposAnimalGanado'] = new Array();
arrValorRestrictions['TiposAnimalGanado'] = new Array();
arrOperadorRestrictions['TiposAnimalGanado'] = new Array();
arrTipoValorRestrictions['TiposAnimalGanado'] = new Array(); 
arrValorDependeGenerico['TiposAnimalGanado'] = new Array();
arrValorDependeOrdenGenerico['TiposAnimalGanado'] = new Array();
// FIN Lupa de Tipos de Animal

//INICIO Lupa de Tipos de Animal Cambio Masivo
arrObjetosLupas['TiposAnimalGanadoCM'] = "com.rsi.agp.dao.tables.cpl.gan.TiposAnimalGanado";
arrCamposFiltrosLupas['TiposAnimalGanadoCM'] = new Array('filtro_TiposAnimalGanado_CM');
arrCamposBeanFiltros['TiposAnimalGanadoCM'] = new Array('desTipoAnimal');
arrTxtCabecerasLupas['TiposAnimalGanadoCM'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['TiposAnimalGanadoCM'] = new Array('30%', '70%');
arrCamposBean['TiposAnimalGanadoCM'] = new Array('id.codTipoAnimal', 'descripcion');
arrCamposDepende['TiposAnimalGanadoCM'] = new Array('lineaseguroid', 'codtipoanimal_cm');
arrCamposBeanDepende['TiposAnimalGanadoCM'] = new Array('id.lineaseguroid', 'id.codTipoAnimal');
arrTipoBeanDepende['TiposAnimalGanadoCM'] = new Array('java.lang.Long', 'java.lang.Long');
arrCamposDevolver['TiposAnimalGanadoCM'] = new Array('codtipoanimal_cm', 'desTipoAnimal_cm');
arrCamposBeanDevolver['TiposAnimalGanadoCM'] = new Array('id.codTipoAnimal', 'descripcion'); 
arrColumnasDistinct['TiposAnimalGanadoCM'] = new Array();
arrColumnasIsNull['TiposAnimalGanadoCM'] = new Array();
arrCampoLeftJoin['TiposAnimalGanadoCM'] = new Array();
arrCampoRestrictions['TiposAnimalGanadoCM'] = new Array();
arrValorRestrictions['TiposAnimalGanadoCM'] = new Array();
arrOperadorRestrictions['TiposAnimalGanadoCM'] = new Array();
arrTipoValorRestrictions['TiposAnimalGanadoCM'] = new Array(); 
arrValorDependeGenerico['TiposAnimalGanadoCM'] = new Array();
arrValorDependeOrdenGenerico['TiposAnimalGanadoCM'] = new Array();
// FIN Lupa de Tipos de Animal Cambio Masivo


//INICIO Lupa de Cambiar Titular para devolverlos a un campo tipo input.
arrObjetosLupas['CambiarTitular'] = "com.rsi.agp.dao.tables.admin.Asegurado";
arrCamposFiltrosLupas['CambiarTitular'] = new Array('filtro_nifcif_CambiarTitular', 'filtro_fullName_CambiarTitular');
arrCamposBeanFiltros['CambiarTitular'] = new Array('nifcif', 'fullName', 'id');
arrTxtCabecerasLupas['CambiarTitular'] = new Array('NIF/CIF', 'Nombre/Raz\u00F3n Social', 'id');
arrColumnWidth['CambiarTitular'] = new Array('20%', '80%', '0%');
arrCamposBean['CambiarTitular'] = new Array('nifcif', 'fullName','id');
arrCamposDepende['CambiarTitular'] = new Array('codEntidadLupa', 'codEntMedLupa', 'codSubentidadLupa');
arrCamposBeanDepende['CambiarTitular'] = new Array('entidad.codentidad', 'usuario.subentidadMediadora.id.codentidad', 'usuario.subentidadMediadora.id.codsubentidad');
arrTipoBeanDepende['CambiarTitular'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['CambiarTitular'] = new Array('idAseguradoCambioTitular');
arrCamposBeanDevolver['CambiarTitular'] = new Array('id');
arrOcultarColumnas['CambiarTitular'] = new Array(false, false, true);
arrColumnasDistinct['CambiarTitular'] = new Array();
arrColumnasIsNull['CambiarTitular'] = new Array();
arrCampoLeftJoin['CambiarTitular'] = new Array(); 
arrCampoRestrictions['CambiarTitular'] = new Array();
arrValorRestrictions['CambiarTitular'] = new Array();
arrOperadorRestrictions['CambiarTitular'] = new Array();
arrTipoValorRestrictions['CambiarTitular'] = new Array(); 
arrValorDependeGenerico['CambiarTitular'] = new Array();
arrValorDependeOrdenGenerico['CambiarTitular'] = new Array();
//FIN Lupa de Cambiar Titular para devolverlos a un campo tipo input.

//INICIO Lupa de Entidades cargo cuenta
arrObjetosLupas['EntidadCargoCuenta'] = "com.rsi.agp.dao.tables.admin.Entidad";
arrCamposFiltrosLupas['EntidadCargoCuenta'] = new Array('filtro_EntidadCargoCuenta');
arrCamposBeanFiltros['EntidadCargoCuenta'] = new Array('nomentidad');
arrTxtCabecerasLupas['EntidadCargoCuenta'] = new Array('C\u00F3digo', 'Nombre');
arrColumnWidth['EntidadCargoCuenta'] = new Array('30%', '70%');
arrCamposBean['EntidadCargoCuenta'] = new Array('codentidad', 'nomentidad');
arrCamposDepende['EntidadCargoCuenta'] = new Array('grupoEntidades', 'entidadCargoCuenta');
arrCamposBeanDepende['EntidadCargoCuenta'] = new Array('codentidad', 'codentidad');
arrTipoBeanDepende['EntidadCargoCuenta'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['EntidadCargoCuenta'] = new Array('entidadCargoCuenta', 'entidadCargoCuenta_desc');
arrCamposBeanDevolver['EntidadCargoCuenta'] = new Array('codentidad', 'nomentidad');
arrColumnasDistinct['EntidadCargoCuenta'] = new Array();
arrColumnasIsNull['EntidadCargoCuenta'] = new Array();
arrCampoLeftJoin['EntidadCargoCuenta'] = new Array();
arrCampoRestrictions['EntidadCargoCuenta'] = new Array();
arrValorRestrictions['EntidadCargoCuenta'] = new Array();
arrOperadorRestrictions['EntidadCargoCuenta'] = new Array();
arrTipoValorRestrictions['EntidadCargoCuenta'] = new Array(); 
arrValorDependeGenerico['EntidadCargoCuenta'] = new Array();
arrValorDependeOrdenGenerico['EntidadCargoCuenta'] = new Array();
// FIN Lupa de Entidades

//INICIO Lupa de Tipo Marco Plantacion para el cambio masivo
arrObjetosLupas['MarcoPlantacionCM'] = "com.rsi.agp.dao.tables.cgen.MarcoPlantacion";
arrCamposFiltrosLupas['MarcoPlantacionCM'] = new Array('filtro_MarcoPlantacionCM');
arrCamposBeanFiltros['MarcoPlantacionCM'] = new Array('destipomarcoplantac');
arrTxtCabecerasLupas['MarcoPlantacionCM'] = new Array('C\u00F3digo', 'Nombre');
arrColumnWidth['MarcoPlantacionCM'] = new Array('30%', '70%');
arrCamposBean['MarcoPlantacionCM'] = new Array('codtipomarcoplantac', 'destipomarcoplantac');
arrCamposDepende['MarcoPlantacionCM'] = new Array('codtipomarcoplantac_cm');
arrCamposBeanDepende['MarcoPlantacionCM'] = new Array('codtipomarcoplantac');
arrTipoBeanDepende['MarcoPlantacionCM'] = new Array('java.math.BigDecimal');
arrCamposDevolver['MarcoPlantacionCM'] = new Array('codtipomarcoplantac_cm', 'destipomarcoplantac_cm');
arrCamposBeanDevolver['MarcoPlantacionCM'] = new Array('codtipomarcoplantac', 'destipomarcoplantac');
arrColumnasDistinct['MarcoPlantacionCM'] = new Array();
arrColumnasIsNull['MarcoPlantacionCM'] = new Array();
arrCampoLeftJoin['MarcoPlantacionCM'] = new Array();
arrCampoRestrictions['MarcoPlantacionCM'] = new Array();
arrValorRestrictions['MarcoPlantacionCM'] = new Array();
arrOperadorRestrictions['MarcoPlantacionCM'] = new Array();
arrTipoValorRestrictions['MarcoPlantacionCM'] = new Array(); 
arrValorDependeGenerico['MarcoPlantacionCM'] = new Array();
arrValorDependeOrdenGenerico['MarcoPlantacionCM'] = new Array();
// FIN Lupa de Tipo Marco Plantacion para el cambio masivo


//INICIO Lupa de PracticaCultural para el cambio masivo
arrObjetosLupas['PracticaCulturalCM'] = "com.rsi.agp.dao.tables.cgen.PracticaCultural";
arrCamposFiltrosLupas['PracticaCulturalCM'] = new Array('filtro_PracticaCulturalCM');
arrCamposBeanFiltros['PracticaCulturalCM'] = new Array('despracticacultural');
arrTxtCabecerasLupas['PracticaCulturalCM'] = new Array('C\u00F3digo', 'Nombre');
arrColumnWidth['PracticaCulturalCM'] = new Array('30%', '70%');
arrCamposBean['PracticaCulturalCM'] = new Array('codpracticacultural', 'despracticacultural');
arrCamposDepende['PracticaCulturalCM'] = new Array('codpracticacultural_cm');
arrCamposBeanDepende['PracticaCulturalCM'] = new Array('codpracticacultural');
arrTipoBeanDepende['PracticaCulturalCM'] = new Array('java.math.BigDecimal');
arrCamposDevolver['PracticaCulturalCM'] = new Array('codpracticacultural_cm', 'despracticacultural_cm');
arrCamposBeanDevolver['PracticaCulturalCM'] = new Array('codpracticacultural', 'despracticacultural');
arrColumnasDistinct['PracticaCulturalCM'] = new Array();
arrColumnasIsNull['PracticaCulturalCM'] = new Array();
arrCampoLeftJoin['PracticaCulturalCM'] = new Array();
arrCampoRestrictions['PracticaCulturalCM'] = new Array();
arrValorRestrictions['PracticaCulturalCM'] = new Array();
arrOperadorRestrictions['PracticaCulturalCM'] = new Array();
arrTipoValorRestrictions['PracticaCulturalCM'] = new Array(); 
arrValorDependeGenerico['PracticaCulturalCM'] = new Array();
arrValorDependeOrdenGenerico['PracticaCulturalCM'] = new Array();
//FIN Lupa de PracticaCultural para el cambio masivo

//INICIO Lupa Tipo de Hoja de campo
arrObjetosLupas['TipoHoja'] = "com.rsi.agp.dao.tables.poliza.PolizasInfoHojaCampoTipoHoja";
arrCamposFiltrosLupas['TipoHoja'] = new Array('filtro_TipoHoja');
arrCamposBeanFiltros['TipoHoja'] = new Array('descripcion');
arrTxtCabecerasLupas['TipoHoja'] = new Array('Codigo', 'Descripcion');
arrColumnWidth['TipoHoja'] = new Array('30%', '70%');
arrCamposBean['TipoHoja'] = new Array('tipoHoja', 'descripcion');
arrCamposDepende['TipoHoja'] = new Array('tipoHoja');
arrCamposBeanDepende['TipoHoja'] = new Array('tipoHoja');
arrTipoBeanDepende['TipoHoja'] = new Array('java.lang.Integer');
arrCamposDevolver['TipoHoja'] = new Array('tipoHoja', 'tipoHojaDesc');
arrCamposBeanDevolver['TipoHoja'] = new Array('tipoHoja', 'descripcion');
arrColumnasDistinct['TipoHoja'] = new Array();
arrColumnasIsNull['TipoHoja'] = new Array();
arrCampoLeftJoin['TipoHoja'] = new Array();
arrCampoRestrictions['TipoHoja'] = new Array();
arrValorRestrictions['TipoHoja'] = new Array();
arrOperadorRestrictions['TipoHoja'] = new Array();
arrTipoValorRestrictions['TipoHoja'] = new Array();
arrValorDependeGenerico['TipoHoja'] = new Array();
arrValorDependeOrdenGenerico['TipoHoja'] = new Array();
//FIN Lupa Tipo de Hoja de campo

//INICIO Lupa de Situacion de Hojas de Campo
arrObjetosLupas['SituacionHojaCampo'] = "com.rsi.agp.dao.tables.poliza.PolizasInfoHojaCampoVistaHojaSituacion";
arrCamposFiltrosLupas['SituacionHojaCampo'] = new Array('filtro_SituacionHojaCampo');
arrCamposBeanFiltros['SituacionHojaCampo'] = new Array('id.descripcion');
arrTxtCabecerasLupas['SituacionHojaCampo'] = new Array('Tipo Hoja', 'Hoja', 'Tipo Situacion', 'Situacion', 'Descripci\u00F3n');
arrColumnWidth['SituacionHojaCampo'] = new Array('15%', '20%', '20%', '15%', '30%');
arrCamposBean['SituacionHojaCampo'] = new Array('id.tipoHoja', 'id.hoja', 'id.tipoSituacion', 'id.situacion', 'id.descripcion');
arrCamposDepende['SituacionHojaCampo'] = new Array('tipoHoja', 'situacionHoja');
arrCamposBeanDepende['SituacionHojaCampo'] = new Array('id.tipoHoja', 'id.situacion');
arrTipoBeanDepende['SituacionHojaCampo'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal');
arrCamposDevolver['SituacionHojaCampo'] = new Array('tipoHoja', 'tipoHojaDesc', 'situacionHoja', 'situacionHojaDesc');
arrCamposBeanDevolver['SituacionHojaCampo'] = new Array('id.tipoHoja', 'id.hoja', 'id.situacion', 'id.descripcion');
arrColumnasDistinct['SituacionHojaCampo'] = new Array();
arrColumnasIsNull['SituacionHojaCampo'] = new Array();
arrCampoLeftJoin['SituacionHojaCampo'] = new Array();
arrCampoRestrictions['SituacionHojaCampo'] = new Array();
arrValorRestrictions['SituacionHojaCampo'] = new Array();
arrOperadorRestrictions['SituacionHojaCampo'] = new Array();
arrTipoValorRestrictions['SituacionHojaCampo'] = new Array();
arrValorDependeGenerico['SituacionHojaCampo'] = new Array();
arrValorDependeOrdenGenerico['SituacionHojaCampo'] = new Array();
// FIN Lupa de Situacion de Hojas de Campo

//INICIO Lupa de Situacion de Actas de Tasacion
arrObjetosLupas['SituacionActa'] = "com.rsi.agp.dao.tables.poliza.PolizasInfoActasSituacion";
arrCamposFiltrosLupas['SituacionActa'] = new Array('filtro_SituacionActa');
arrCamposBeanFiltros['SituacionActa'] = new Array('descripcion');
arrTxtCabecerasLupas['SituacionActa'] = new Array('Codigo', 'Descripcion');
arrColumnWidth['SituacionActa'] = new Array('30%', '70%');
arrCamposBean['SituacionActa'] = new Array('tipoSituacion', 'descripcion');
arrCamposDepende['SituacionActa'] = new Array('situacionActa');
arrCamposBeanDepende['SituacionActa'] = new Array('tipoSituacion');
arrTipoBeanDepende['SituacionActa'] = new Array('java.lang.Integer');
arrCamposDevolver['SituacionActa'] = new Array('situacionActa', 'situacionActaDes');
arrCamposBeanDevolver['SituacionActa'] = new Array('tipoSituacion', 'descripcion');
arrColumnasDistinct['SituacionActa'] = new Array();
arrColumnasIsNull['SituacionActa'] = new Array();
arrCampoLeftJoin['SituacionActa'] = new Array();
arrCampoRestrictions['SituacionActa'] = new Array();
arrValorRestrictions['SituacionActa'] = new Array();
arrOperadorRestrictions['SituacionActa'] = new Array();
arrTipoValorRestrictions['SituacionActa'] = new Array();
arrValorDependeGenerico['SituacionActa'] = new Array();
arrValorDependeOrdenGenerico['SituacionActa'] = new Array();
//FIN Lupa de Situacion de Actas de Tasacion

//INICIO Lupa de Especies para Responsabilidad Civil
arrObjetosLupas['EspecieRC'] = "com.rsi.agp.dao.tables.cpl.gan.Especie";
arrCamposFiltrosLupas['EspecieRC'] = new Array('filtro_codEspecieRC', 'filtro_EspecieRC');
arrCamposBeanFiltros['EspecieRC'] = new Array('id.codespecie', 'descripcion');
arrTipoCamposBeanFiltros['EspecieRC'] = new Array('java.math.BigDecimal', 'java.lang.String');
arrTxtCabecerasLupas['EspecieRC'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['EspecieRC'] = new Array('30%', '70%');
arrCamposBean['EspecieRC'] = new Array('id.codespecie', 'descripcion');
arrCamposDepende['EspecieRC'] = new Array('codplan', 'codlinea', 'especie');
arrCamposBeanDepende['EspecieRC'] = new Array('linea.codplan', 'linea.codlinea', 'id.codespecie');
arrTipoBeanDepende['EspecieRC'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.Long');
arrCamposDevolver['EspecieRC'] = new Array('especie', 'desc_especie');
arrCamposBeanDevolver['EspecieRC'] = new Array('id.codespecie', 'descripcion');
arrColumnasDistinct['EspecieRC'] = new Array();
arrColumnasIsNull['EspecieRC'] = new Array();
arrCampoLeftJoin['EspecieRC'] = new Array();
arrCampoRestrictions['EspecieRC'] = new Array();
arrValorRestrictions['EspecieRC'] = new Array();
arrOperadorRestrictions['EspecieRC'] = new Array();
arrTipoValorRestrictions['EspecieRC'] = new Array(); 
arrValorDependeGenerico['EspecieRC'] = new Array();
arrValorDependeOrdenGenerico['EspecieRC'] = new Array();
// FIN Lupa de Especies para Responsabilidad Civil

//INICIO Lupa de Regimenes para Responsabilidad Civil
arrObjetosLupas['RegimenRC'] = "com.rsi.agp.dao.tables.cpl.gan.RegimenManejo";
arrCamposFiltrosLupas['RegimenRC'] = new Array('filtro_codRegimenRC', 'filtro_RegimenRC');
arrCamposBeanFiltros['RegimenRC'] = new Array('id.codRegimen', 'descripcion');
arrTipoCamposBeanFiltros['RegimenRC'] = new Array('java.lang.Long', 'java.lang.String');
arrTxtCabecerasLupas['RegimenRC'] = new Array('C\u00F3digo', 'Descripci\u00F3n');
arrColumnWidth['RegimenRC'] = new Array('30%', '70%');
arrCamposBean['RegimenRC'] = new Array('id.codRegimen', 'descripcion');
arrCamposDepende['RegimenRC'] = new Array('codplan', 'codlinea', 'regimen');
arrCamposBeanDepende['RegimenRC'] = new Array('linea.codplan', 'linea.codlinea', 'id.codRegimen');
arrTipoBeanDepende['RegimenRC'] = new Array('java.math.BigDecimal', 'java.math.BigDecimal', 'java.lang.Long');
arrCamposDevolver['RegimenRC'] = new Array('regimen', 'desc_regimen');
arrCamposBeanDevolver['RegimenRC'] = new Array('id.codRegimen', 'descripcion');
arrColumnasDistinct['RegimenRC'] = new Array();
arrColumnasIsNull['RegimenRC'] = new Array();
arrCampoLeftJoin['RegimenRC'] = new Array();
arrCampoRestrictions['RegimenRC'] = new Array();
arrValorRestrictions['RegimenRC'] = new Array();
arrOperadorRestrictions['RegimenRC'] = new Array();
arrTipoValorRestrictions['RegimenRC'] = new Array();
arrValorDependeGenerico['RegimenRC'] = new Array();
arrValorDependeOrdenGenerico['RegimenRC'] = new Array();
// FIN Lupa de Regimenes para Responsabilidad Civil  

//INICIO Lupa de Importacion de Polizas
arrObjetosLupas['UsuarioFiltrosRenovables'] = "com.rsi.agp.dao.tables.commons.Usuario";
arrCamposFiltrosLupas['UsuarioFiltrosRenovables'] = new Array('filtro_cod_UsuarioFiltrosRenovables', 'filtro_nom_UsuarioFiltrosRenovables');
arrCamposBeanFiltros['UsuarioFiltrosRenovables'] = new Array('codusuario', 'nombreusu');
arrTxtCabecerasLupas['UsuarioFiltrosRenovables'] = new Array('C\u00F3digo', 'Nombre');
arrColumnWidth['UsuarioFiltrosRenovables'] = new Array('30%', '70%');
arrCamposBean['UsuarioFiltrosRenovables'] = new Array('codusuario', 'nombreusu');
arrCamposDepende['UsuarioFiltrosRenovables'] = new Array('usuario_ip', 'entidad', 'oficina', 'grupoEntidades','entmediadora','subentmediadora','delegacion');
arrCamposBeanDepende['UsuarioFiltrosRenovables'] = new Array('codusuario', 'oficina.id.codentidad', 'oficina.id.codoficina', 'oficina.id.codentidad','subentidadMediadora.id.codentidad','subentidadMediadora.id.codsubentidad','delegacion');
arrTipoBeanDepende['UsuarioFiltrosRenovables'] = new Array('java.lang.String', 'java.math.BigDecimal', 'java.math.BigDecimal', 'java.math.BigDecimal','java.math.BigDecimal','java.math.BigDecimal','java.math.BigDecimal');
arrCamposDevolver['UsuarioFiltrosRenovables'] = new Array('usuario_ip');
arrCamposBeanDevolver['UsuarioFiltrosRenovables'] = new Array('codusuario');
arrColumnasDistinct['UsuarioFiltrosRenovables'] = new Array();
arrColumnasIsNull['UsuarioFiltrosRenovables'] = new Array();
arrCampoLeftJoin['UsuarioFiltrosRenovables'] = new Array();
arrCampoRestrictions['UsuarioFiltrosRenovables'] = new Array();
arrValorRestrictions['UsuarioFiltrosRenovables'] = new Array();
arrOperadorRestrictions['UsuarioFiltrosRenovables'] = new Array();
arrTipoValorRestrictions['UsuarioFiltrosRenovables'] = new Array();
arrValorDependeGenerico['UsuarioFiltrosRenovables'] = new Array();
arrValorDependeOrdenGenerico['UsuarioFiltrosRenovables'] = new Array();
// FIN Lupa de Importacion de Polizas 

//INICIO Lupa de Familias
arrObjetosLupas['Familia'] = "com.rsi.agp.dao.tables.familias.Familia";
arrCamposFiltrosLupas['Familia'] = new Array('filtro_codFamilia', 'filtro_Familia');
arrCamposBeanFiltros['Familia'] = new Array('codFamilia', 'nomFamilia');
arrTipoCamposBeanFiltros['Familia'] = new Array('java.lang.Long', 'java.lang.String');
arrTxtCabecerasLupas['Familia'] = new Array('Cod. Familia', 'Nom. Familia');
arrColumnWidth['Familia'] = new Array('35%', '65%');
arrCamposBean['Familia'] = new Array('codFamilia', 'nomFamilia');
arrCamposDepende['Familia'] = new Array('codFamilia', 'nomFamilia');
arrCamposBeanDepende['Familia'] = new Array('codFamilia', 'nomFamilia');
arrTipoBeanDepende['Familia'] = new Array('java.lang.Long', 'java.lang.String');
arrCamposDevolver['Familia'] = new Array('codFamilia', 'nomFamilia');
arrCamposBeanDevolver['Familia'] = new Array('codFamilia', 'nomFamilia');
arrColumnasDistinct['Familia'] = new Array();
arrColumnasIsNull['Familia'] = new Array();
arrCampoLeftJoin['Familia'] = new Array();
arrCampoRestrictions['Familia'] = new Array();
arrValorRestrictions['Familia'] = new Array();
arrOperadorRestrictions['Familia'] = new Array();
arrTipoValorRestrictions['Familia'] = new Array(); 
arrValorDependeGenerico['Familia'] = new Array();
arrValorDependeOrdenGenerico['Familia'] = new Array();

var num = 10;

var lupas = {
	//Funcion para mostrar una tabla al pulsar sobre un campo tipo "Lupa"
	muestraTabla:function(objeto, direccion, campoOrden, tipoOrden){
		lupas.incrementaPosicion(objeto, direccion);
		
		lupas.getTabla(objeto, direccion, campoOrden, tipoOrden);
		lupas.openWindow(objeto);
	},
	//Funcion para llamar al servidor y obtener el listado de objetos a mostrar en la "Lupa"
	getTabla:function(objeto, direccion, campoOrden, tipoOrden){
		/*
		 * Montamos una cadena de texto con todos los parametros necesarios para la busqueda:
		 *   - objeto: clase del objeto que queremos buscar
		 *   - objetoActual: objeto indicado como parametro
		 *   - posicion: primer registro que buscamos
		 *   - filtro: aparece tantas veces como campos de filtro hay en la lupa y se refiere al formulario
		 *   - campoFiltro: aparece tantas veces como campos de filtro hay en la lupa y se refiere al bean 
		 *   - campoListado: campos del bean a mostrar en el listado
		 *   - campoDepende: campos del formulario de los que depende el listado
		 *   - campoBeanDepende: campos del bean de los que depende el listado
		 *   - tipoBeanDepende: tipos de los campos del bean de los que depende el listado
		 *   - campoOrden: campo por el que se quiere ordenar el listado
		 *   - tipoOrden: tipo de ordenacion: ascendente o descendente
		 *   - direccion: direccion indicada en la lupa
		 *   - nocache: parametro aleatorio para evitar el cacheo en las llamadas ajax
		 *   - campoOperador: parametro de parejas operador-campo de filtro
		 *   - valorOperador: valor para la pareja operador campo anterior
		 */
		var argumentos = '';
		argumentos += "&objeto=" + arrObjetosLupas[objeto];
		argumentos += "&objetoActual=" + objeto;
		argumentos += "&posicion=" + $('#posicion_'+objeto).val();
		//Filtro
		//alert("1. " + arrCamposFiltrosLupas[objeto].length);
		for (var i = 0; i < arrCamposFiltrosLupas[objeto].length; i++){
			argumentos += "&filtro=" + document.getElementById(arrCamposFiltrosLupas[objeto][i]).value;
		}
		//alert("2. " + arrCamposBeanFiltros[objeto].length);
		for (var i = 0; i < arrCamposBeanFiltros[objeto].length; i++){
			argumentos += "&campoFiltro=" + arrCamposBeanFiltros[objeto][i];
		}
		//alert("2.5. " + arrTipoCamposBeanFiltros[objeto].length);
		if (arrTipoCamposBeanFiltros[objeto]) {
			for (var i = 0; i < arrTipoCamposBeanFiltros[objeto].length; i++){
				argumentos += "&tipoFiltro=" + arrTipoCamposBeanFiltros[objeto][i];
			}
		}
		//Campos del objeto a obtener
		//alert("3. " + arrCamposBean[objeto].length);
		for (var i = 0; i < arrCamposBean[objeto].length; i++){
			argumentos += "&campoListado=" + arrCamposBean[objeto][i];
		}
		//Valores de los Campos de pantalla de los que dependen los valores de la lupa
		//alert("4. " + arrCamposDepende[objeto].length);
		for (var i = 0; i < arrCamposDepende[objeto].length; i++){
			argumentos += "&valorDepende=" + escape($('#'+arrCamposDepende[objeto][i]).length ? $('#'+arrCamposDepende[objeto][i]).val() : '');
		}
		//Tipos de los Campos del bean de los que dependen los valores de la lupa
		//alert("4. " + arrCamposDepende[objeto].length);
		for (var i = 0; i < arrTipoBeanDepende[objeto].length; i++){
			argumentos += "&tipoDepende=" + arrTipoBeanDepende[objeto][i];
		}
		//Campos del bean de los que dependen los valores de la lupa
		//alert("5. " + arrCamposBeanDepende[objeto].length);
		for (var i = 0; i < arrCamposBeanDepende[objeto].length; i++){
			argumentos += "&campoBeanDepende=" + arrCamposBeanDepende[objeto][i];
		}
		//Campos del bean por los que se hara distinct
		for (var i = 0; i < arrColumnasDistinct[objeto].length; i++){
			argumentos += "&campoBeanDistinct=" + arrColumnasDistinct[objeto][i];
		}
		//Campos del bean por los que se hara is null
		for (var i = 0; i < arrColumnasIsNull[objeto].length; i++){
			argumentos += "&campoBeanIsNull=" + arrColumnasIsNull[objeto][i];
		}
		//Campos del bean por los que se hara left join
		for (var i = 0; i < arrCampoLeftJoin[objeto].length; i++){
			argumentos += "&campoLeftJoin=" + arrCampoLeftJoin[objeto][i];
		}
		
		//RESTRICTIONS----------------------------------------------------------------------------------------------
		//Campo para restrictions
		for (var i = 0; i < arrCampoRestrictions[objeto].length; i++){
			//argumentos += "&valorDepende=" + escape($('#'+arrCamposDepende[objeto][i]).val());
			argumentos += "&campoRestriccion=" + $('#'+arrCampoRestrictions[objeto][i]).val();
		}
		//Valor de comparacion del filtro anterior
		for (var i = 0; i < arrValorRestrictions[objeto].length; i++){
			argumentos += "&valorRestriccion=" + $('#'+arrValorRestrictions[objeto][i]).val();
		}
		
		//Operador de comparacion de los filtros anteriore
		for (var i = 0; i < arrOperadorRestrictions[objeto].length; i++){
			argumentos += "&operadorRestriccion=" + $('#'+arrOperadorRestrictions[objeto][i]).val();
		}
		for (var i = 0; i < arrTipoValorRestrictions[objeto].length; i++){
			argumentos += "&tipoValorRestriccion=" + arrTipoValorRestrictions[objeto][i];
		}
		
		// Valores genericos
		for (var i = 0; i < arrValorDependeGenerico[objeto].length; i++){
			argumentos += "&valorDependeGenerico=" + arrValorDependeGenerico[objeto][i];
		}
		for (var i = 0; i < arrValorDependeOrdenGenerico[objeto].length; i++){
			argumentos += "&valorDependeOrdenGenerico=" + arrValorDependeOrdenGenerico[objeto][i];
		}
		
		
		if (arrAcumulaResultados[objeto] == 'S') {
			argumentos+="&acumularResultado="+arrAcumulaResultados[objeto][0];
		}
		
		
		//--------------------------------------------------------------------------------------------------
		
		
		//alert(objeto);
		//alert(argumentos); 
		argumentos += "&campoOrden=" + campoOrden;
		argumentos += "&tipoOrden=" + tipoOrden;
		argumentos += "&direccion=" + direccion;
		argumentos += "&nocache=" + new Date().getTime();
		
		$.ajax({
			url: "ajaxCommon.html",
			data: "operacion=ajax_getTabla" + argumentos,
			async: true,
			contentType: "application/x-www-form-urlencoded",
			dataType: 'json',
			error: function(nomObjeto, quepaso, otroobj){
				alert("Error al obtener los datos de la tabla: " + quepaso);
			},
			global: true,
			ifModified: false,
			processData:true,
			success: function(datos){
				//Recojo los valores enviados desde el servidor
				var objetoJson = eval(datos);
				var array = objetoJson.lista;
				var objetoActual = objetoJson.objetoActual;
				var tipoOrden = objetoJson.tipoOrden;
				var campoOrden = objetoJson.campoOrden;
				var direccion = objetoJson.direccion;
				var numRegTotal = objetoJson.numRegTotal;
				
				//Obtenemos el "TD" donde debe ir ubidada la tabla
				var casa = document.getElementById('tabla_visor_' + objetoActual);				
				if(document.getElementById('tabla')){
					var tablaVieja = document.getElementById('tabla');
					var padre = tablaVieja.parentNode;
					padre.removeChild(tablaVieja);
				}
				
				//Creamos un "TABLE" para mostrar los datos
				var tabla = document.createElement('table');
				tabla.id = 'tabla';
				tabla.width = "100%";
				tabla.style.border = '1px solid #CCC';
				tabla.style.tableLayout = 'fixed';
				
				//CABECERA
				var head = document.createElement('thead');
				var cabecera = document.createElement('tr');
				for (var i = 0; i < arrTxtCabecerasLupas[objetoActual].length; i++){
					
					// Si se indica que la columna debe estar oculta, se va a la siguiente iteracion del bucle
					if (arrOcultarColumnas[objetoActual] != null) {
						 if (arrOcultarColumnas[objetoActual][i] == true) continue;
					} 
					
					var celdaHeader = document.createElement("td");
					var a = document.createElement('a'); 
					var imagen = document.createElement("IMG");
					var urlIMG = "";
					var ordenColumna;
					if (campoOrden == arrCamposBean[objetoActual][i] && tipoOrden == "ASC"){
						urlIMG = "jsp/img/displaytag/arrow_up.png";
						ordenColumna = "DESC";
					}
					else if (campoOrden == arrCamposBean[objetoActual][i] && tipoOrden == "DESC"){
						urlIMG = "jsp/img/displaytag/arrow_down.png";
						ordenColumna = "ASC";
					}
					else {
						urlIMG = "jsp/img/displaytag/arrow_off.png";
						ordenColumna = "ASC";
					}
					imagen.setAttribute("src",urlIMG);
					a.appendChild(imagen);
					var url = "javascript:lupas.muestraTabla('"+ objetoActual + "','" + direccion + "','" + arrCamposBean[objetoActual][i]  + "','" + ordenColumna + "');";
					a.setAttribute('href',url);
					a.appendChild(document.createTextNode(" " +arrTxtCabecerasLupas[objetoActual][i]));
					
					celdaHeader.appendChild(a);
					celdaHeader.className = "cblistaImg literal";
					celdaHeader.style.backgroundColor = "#e5e5e5";
					celdaHeader.width = arrColumnWidth[objetoActual][i];
					cabecera.appendChild(celdaHeader);
				}
				
				head.appendChild(cabecera);
				tabla.appendChild(head);
				//FIN CABECERA
				
				//CUERPO
				var cuerpo = document.createElement('tbody');
				var arrFuncionesDevolver = new Array();
				if(tabla && array){
					for(var i = 0; i < array.length; i++){
						//Creo las columnas y recorremos el array de filas definido al principio para obtener las propiedades
						var filaBody = document.createElement('tr');
						filaBody.id = i;
						filaBody.onmouseover = function(){
							this.style.backgroundColor = '#CCC';
						}	
						filaBody.onmouseout = function(){
							if(parseInt(this.id) % 2 != 0 ){
									this.style.backgroundColor = '#F7F7F7';
							}else{
									this.style.backgroundColor = 'white';
							}
						}
						var color = 'white';
						if(i%2 != 0){
							color = '#F7F7F7';
						}
						filaBody.style.backgroundColor = color;
						
						var funcion = "lupas.devuelveObjeto('" + objetoActual + "'";
						for (var j = 0; j < arrCamposBean[objetoActual].length; j++){
							
							var objeto = array[i];
							
							// Si no se indica que la celda esta oculta se anade a la fila
							if (arrOcultarColumnas[objetoActual] == null || arrOcultarColumnas[objetoActual][j] == false) {
								var celdaBody = document.createElement('td');								
								var txt = eval("objeto['"+arrCamposBean[objetoActual][j]+"']");
								celdaBody.innerHTML = txt;
								celdaBody.className = "literal";
								filaBody.appendChild(celdaBody);
							}
							
							for (var k = 0; k < arrCamposBeanDevolver[objetoActual].length; k++){
								if (arrCamposBean[objetoActual][j] == arrCamposBeanDevolver[objetoActual][k]){
																	
									var str = eval("objeto['"+arrCamposBeanDevolver[objetoActual][k]+"']");
									var res = str.replace(/\'/g,"\\'");
							
									funcion += ",'" + arrCamposDevolver[objetoActual][k] + "','"+ res + "'";
								}
							}
						}
						funcion += ');';
						arrFuncionesDevolver[i] = funcion;
						//Anado el evento que se lanzara en el onclick para que devuelva los valores al formulario
						filaBody.onclick = function() {
							eval(arrFuncionesDevolver[this.id]);
						}
						
						cuerpo.appendChild(filaBody);
					}
					tabla.appendChild(cuerpo);
					casa.appendChild(tabla);
				}
				//FIN CUERPO
				
				//PAGINACION
				var pag_registros = document.getElementById("registros_" + objetoActual);
				//Limpiamos el elemento por si tuviera algo
				while(pag_registros.hasChildNodes()){
					pag_registros.removeChild(pag_registros.lastChild);
				}
				
				var txt = numRegTotal +  ' registro(s) encontrado(s)';
				var t = document.createTextNode(txt);
				pag_registros.appendChild(t);
				$('#maxima_' + objetoActual).val(numRegTotal);
				
				var div_paginas = document.getElementById("paginas_" + objetoActual);
				//Limpiamos el elemento por si tuviera algo de antes
				while(div_paginas.hasChildNodes()){
					div_paginas.removeChild(div_paginas.lastChild);
				}
				
				var imagenInicio=document.createElement("IMG");
				var imagenFin=document.createElement("IMG");
				var imagenSig=document.createElement("IMG");
				var imagenAnt=document.createElement("IMG");
				
				var urlIMG = "jsp/img/displaytag/first.gif";
				imagenInicio.setAttribute("src",urlIMG);
				var url = "javascript:lupas.muestraTabla('"+ objetoActual + "','principio','" + campoOrden  + "','" + tipoOrden + "');";
				var linkInicio = document.createElement('a');
				linkInicio.setAttribute("href",url);
				linkInicio.appendChild(imagenInicio);
				
				var urlIMG = "jsp/img/displaytag/last.gif";
				imagenFin.setAttribute("src",urlIMG);
				var url = "javascript:lupas.muestraTabla('"+ objetoActual + "','final','" + campoOrden  + "','" + tipoOrden + "');";
				var linkFin = document.createElement('a');
				linkFin.setAttribute("href",url);
				linkFin.appendChild(imagenFin);
				
				var urlIMG = "jsp/img/displaytag/next.gif";
				imagenSig.setAttribute("src",urlIMG);
				var url = "javascript:lupas.muestraTabla('"+ objetoActual + "','adelante','" + campoOrden  + "','" + tipoOrden + "');";
				var linSig = document.createElement('a');
				linSig.setAttribute("href",url);
				linSig.appendChild(imagenSig);
				
				var urlIMG = "jsp/img/displaytag/prev.gif";
				imagenAnt.setAttribute("src",urlIMG);
				var url = "javascript:lupas.muestraTabla('"+ objetoActual + "','atras','" + campoOrden  + "','" + tipoOrden + "');";
				var linkAnt = document.createElement('a');
				linkAnt.setAttribute("href",url);
				linkAnt.appendChild(imagenAnt);
				
				div_paginas.appendChild(linkInicio);
				div_paginas.appendChild(linkAnt);
				if (numRegTotal > 0){
					var paginaActual = Math.round(parseInt($('#posicion_'+objetoActual).val()) /10 + 1);
				}else{
					var paginaActual = 0;
				}
				var paginaTotal = Math.ceil(parseInt(numRegTotal) /10);
				var txt = 'Pagina ' + paginaActual + ' de ' + paginaTotal;
				var t = document.createTextNode(txt);
				div_paginas.appendChild(t);
				
				div_paginas.appendChild(linSig);
				div_paginas.appendChild(linkFin);
				//FIN PAGINACION
			},
			type: "GET"
		});
	},
	incrementaPosicion:function(objeto,direccion){
	
		var maximo = parseInt($('#maxima_' + objeto).val());
		maximo = Math.ceil(maximo/10)*10;
		var posicion = parseInt($('#posicion_'+objeto).val());
		var siguientePosicion = 0;
		if (direccion == 'principio') {
			siguientePosicion = 0;
		} else if (direccion == 'atras') {
			if (eval(posicion - num) < 0) {
				siguientePosicion = eval(maximo - num);
			} else {
				siguientePosicion = eval(posicion - num);
			}
		} else if (direccion == 'adelante') {
			if (eval(posicion + num) < maximo) {
				siguientePosicion = eval(posicion + num);
			}
		} else if (direccion == 'final') {
			siguientePosicion = eval(maximo - num);
		}
		$('#posicion_'+objeto).val(siguientePosicion);
	},
	//Funcion para abrir un popup
	openWindow:function(id){
		$("#visor_"+id).fadeIn('normal');
		$("#overlay").show();
		//Ocultar los combos para que no salgan "por delante" de los popup
		var elements = document.getElementsByTagName("select");
		for (var i = 0; i < elements.length; i++) {
			elements.item(i).style.display = "none";
		}
	},	
	//Funcion para abrir un popup
	closeWindow:function(id){
		$("#visor_"+id).fadeOut('normal');
		$("#overlay").hide();
		//Limpiamos los campos de filtro
		var arrAux = arrCamposFiltrosLupas[id];
		if (arrAux != undefined){
			for (i=0; i<arrAux.length;i++){
				$("#"+arrAux[i]).val('');
			}
		}
		//Mostrar los combos
		var elements = document.getElementsByTagName("select");
		for (var i = 0; i < elements.length; i++) {
			elements.item(i).style.display = "";
		}
	},
	//Funcion para devolver los valores al formulario
	devuelveObjeto:function(id){
		//A esta funcion le llegara el identificador de la vista y pares del tipo campo, valor como argumentos
		if (arguments.length > 1){
			for (var i=1; i < arguments.length; i+=2){
				/* Pet. 63481 ** MODIF TAM (14.05.2021) ** Inicio */
				var argum = arguments[i];
				var valor = $('#'+arguments[i]).val(arguments[i+1]);
				
				if (argum == "catalogo"){
					var valor2 = arguments[i+1].substring(0,1);
					$('#'+arguments[i]).val(valor2);
				}else{
					$('#'+arguments[i]).val(arguments[i+1]);	
				}
				/* Pet. 63481 ** MODIF TAM (14.05.2021) ** Inicio */
				
				// MPM - 22-10-2012
				// Fuerza a que se lance el evento 'onChange' del elemento cuyo value se acaba de cambiar
				$('#'+arguments[i]).trigger({
					type:'change',
					eventCaller: 'lupa'
				});
			}
		}
		lupas.closeWindow(id);
	},
	//Funcion para limpiar campos
	limpiarCampos:function(){
		for (var i=0; i < arguments.length; i++){
			$('#'+arguments[i]).val('');
		}
	}
}

