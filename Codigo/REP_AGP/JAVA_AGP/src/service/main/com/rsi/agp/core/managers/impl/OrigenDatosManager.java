package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.commons.LineasFiltro;
import com.rsi.agp.dao.filters.orgDat.VistaCertificadoEstructuraFiltro;
import com.rsi.agp.dao.filters.orgDat.VistaCicloCultivoFiltro;
import com.rsi.agp.dao.filters.orgDat.VistaCodigoReduccionRendimientoFiltro;
import com.rsi.agp.dao.filters.orgDat.VistaDenomOrigenFiltro;
import com.rsi.agp.dao.filters.orgDat.VistaDestinoFiltro;
import com.rsi.agp.dao.filters.orgDat.VistaFechaFinGarantiasEspecificoYGenericoFiltro;
import com.rsi.agp.dao.filters.orgDat.VistaFechaFinGarantiasEspecificoYGenericoFiltro308;
import com.rsi.agp.dao.filters.orgDat.VistaFechaFinGarantiasFiltro;
import com.rsi.agp.dao.filters.orgDat.VistaFechaRecoleccionFiltro;
import com.rsi.agp.dao.filters.orgDat.VistaIGP307Filtro;
import com.rsi.agp.dao.filters.orgDat.VistaIGPFiltro;
import com.rsi.agp.dao.filters.orgDat.VistaMarcoPlantacionFiltro;
import com.rsi.agp.dao.filters.orgDat.VistaMaterialCubiertaFiltro;
import com.rsi.agp.dao.filters.orgDat.VistaMaterialCubiertaInstFiltro;
import com.rsi.agp.dao.filters.orgDat.VistaMaterialEstructuraFiltro;
import com.rsi.agp.dao.filters.orgDat.VistaMaterialEstructuraInstFiltro;
import com.rsi.agp.dao.filters.orgDat.VistaMedidaPreventivaFiltro;
import com.rsi.agp.dao.filters.orgDat.VistaPracticaCultural305Filtro;
import com.rsi.agp.dao.filters.orgDat.VistaPracticaCulturalFiltro;
import com.rsi.agp.dao.filters.orgDat.VistaRotacionFiltro;
import com.rsi.agp.dao.filters.orgDat.VistaSistemaCultivo310Filtro;
import com.rsi.agp.dao.filters.orgDat.VistaSistemaCultivoFiltro;
import com.rsi.agp.dao.filters.orgDat.VistaSistemaProduccion307Filtro;
import com.rsi.agp.dao.filters.orgDat.VistaSistemaProduccionFiltro;
import com.rsi.agp.dao.filters.orgDat.VistaSistemaProteccionFiltro;
import com.rsi.agp.dao.filters.orgDat.VistaTipoInstalacionFiltro;
import com.rsi.agp.dao.filters.orgDat.VistaTipoPlantacion309Filtro;
import com.rsi.agp.dao.filters.orgDat.VistaTipoPlantacionFiltro;
import com.rsi.agp.dao.models.poliza.IOrigenDatosDao;
import com.rsi.agp.dao.tables.cgen.IGP;
import com.rsi.agp.dao.tables.cgen.MaterialCubierta;
import com.rsi.agp.dao.tables.cgen.MaterialEstructura;
import com.rsi.agp.dao.tables.cgen.NumAniosDesdePoda;
import com.rsi.agp.dao.tables.cgen.PracticaCultural;
import com.rsi.agp.dao.tables.cgen.SistemaConduccion;
import com.rsi.agp.dao.tables.cgen.SistemaCultivo;
import com.rsi.agp.dao.tables.cgen.SistemaProduccion;
import com.rsi.agp.dao.tables.config.OrigenDatos;
import com.rsi.agp.dao.tables.cpl.FechaFinGarantia;
import com.rsi.agp.dao.tables.cpl.FechaRecoleccion;
import com.rsi.agp.dao.tables.orgDat.VistaCertificadoEstructura;
import com.rsi.agp.dao.tables.orgDat.VistaCicloCultivo;
import com.rsi.agp.dao.tables.orgDat.VistaCodigoReduccionRendimiento;
import com.rsi.agp.dao.tables.orgDat.VistaDenomOrigen;
import com.rsi.agp.dao.tables.orgDat.VistaDestino;
import com.rsi.agp.dao.tables.orgDat.VistaIGP;
import com.rsi.agp.dao.tables.orgDat.VistaIGPFactorAmbito;
import com.rsi.agp.dao.tables.orgDat.VistaMarcoPlantacion;
import com.rsi.agp.dao.tables.orgDat.VistaMaterialCubierta;
import com.rsi.agp.dao.tables.orgDat.VistaMaterialEstructura;
import com.rsi.agp.dao.tables.orgDat.VistaMedidaPreventiva;
import com.rsi.agp.dao.tables.orgDat.VistaPorFactores;
import com.rsi.agp.dao.tables.orgDat.VistaPracticaCultural;
import com.rsi.agp.dao.tables.orgDat.VistaPracticaCultural305;
import com.rsi.agp.dao.tables.orgDat.VistaRotacion;
import com.rsi.agp.dao.tables.orgDat.VistaSistemaCultivo;
import com.rsi.agp.dao.tables.orgDat.VistaSistemaCultivo312;
import com.rsi.agp.dao.tables.orgDat.VistaSistemaProduccion;
import com.rsi.agp.dao.tables.orgDat.VistaSistemaProteccion;
import com.rsi.agp.dao.tables.orgDat.VistaTipoInstalacion;
import com.rsi.agp.dao.tables.orgDat.VistaTipoPlantacion;
import com.rsi.agp.dao.tables.orgDat.VistaTipoPlantacion309;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.vo.DatagridPopupVO;
import com.rsi.agp.vo.ItemVistaVO;
import com.rsi.agp.vo.ListOrgDatosVO;

@SuppressWarnings("unchecked")
public class OrigenDatosManager implements IManager{

	private static final String SE_HA_PRODUCIDO_UN_ERROR_AL_PARSEAR_LOS_DATOS_DE_VISTA_TIPO_PLANTACION = "Se ha producido un error al parsear los datos de VistaTipoPlantacion";
	private static final String DD_MM_YYYY = "dd/MM/yyyy";
	private static final String CLASE_DETALLE_SUBTERMINO = "claseDetalle.subtermino";
	private static final String CLASE_DETALLE_VARIEDAD_ID_CODVARIEDAD = "claseDetalle.variedad.id.codvariedad";
	private static final String CLASE_DETALLE_CULTIVO_ID_CODCULTIVO = "claseDetalle.cultivo.id.codcultivo";
	private static final String CLASE_DETALLE_CODTERMINO = "claseDetalle.codtermino";
	private static final String CLASE_DETALLE_CODCOMARCA = "claseDetalle.codcomarca";
	private static final String CLASE_DETALLE_CODPROVINCIA = "claseDetalle.codprovincia";
	private static final String DESCRIPCION2 = "descripcion";
	private static final String CODIGO2 = "codigo";
	private static final String DESCRIPCION = "Descripcion";
	private static final String CODIGO = "Codigo";
	private IOrigenDatosDao origenDatosDao;
	
	private static final Log logger = LogFactory.getLog(OrigenDatosManager.class); 
	
	public ListOrgDatosVO dameLista (DatagridPopupVO dataGridPopUpData)
	{
		
		OrigenDatos identificadorVista = (OrigenDatos)origenDatosDao.getObject(OrigenDatos.class, new Long(dataGridPopUpData.getCodOrigenDeDatos()));
	    ListOrgDatosVO datos           = null;
	    LineasFiltro filtroLinea       = new LineasFiltro(new BigDecimal(dataGridPopUpData.getCodPlan()), new BigDecimal(dataGridPopUpData.getCodLinea()));	
		List<Linea> lineas             = origenDatosDao.getObjects(filtroLinea);
		
		dataGridPopUpData.setCodLinea(lineas.get(0).getLineaseguroid().toString());
		
		if (identificadorVista != null)
		{
			logger.debug("OrigenDatosManager.dameLista " + identificadorVista);
			switch (identificadorVista.getIdorigendatos().intValue())
			{
				case 1:  datos = getDenomOrigen(dataGridPopUpData);break;
				case 2:  datos = getDestinos(dataGridPopUpData);break;
				case 3:  datos = getFecFinGarantias(dataGridPopUpData);break;
				case 4:  datos = getMedidaPreventiva(dataGridPopUpData);break;
				case 5:  datos = getPracticaCultural(dataGridPopUpData);break;
				case 6:  datos = getSistemaProd(dataGridPopUpData);break;
				case 7:  datos = getMarcoPlantacion(dataGridPopUpData);break;
				case 8:  datos = getFechaRecoleccion(dataGridPopUpData);break;
				case 9:  datos = getTipoPlantacion(dataGridPopUpData);break;
				case 10: datos = getIGP(dataGridPopUpData);break;
				case 11: datos = getSistemaProteccion(dataGridPopUpData);break;
				case 12: datos = getCicloCultivo(dataGridPopUpData);break;
				case 13: datos = getMaterialCubierta(dataGridPopUpData);break;
				case 14: datos = getMaterialEstructura(dataGridPopUpData); break;
				case 15: datos = getTipoInstalacion(dataGridPopUpData);break;
				case 16: datos = getCertificadoInstalacion(dataGridPopUpData);break;
				case 17: datos = getPracticaCultural305(dataGridPopUpData);break; 
				case 18: datos = getRotacion(dataGridPopUpData);break;       	
				case 19: datos = getMaterialCubirtaInstalaciones(dataGridPopUpData);break;
				case 20: datos = getMaterialEstructuraInstalaciones(dataGridPopUpData);break;	
				case 21: datos = getIGP307(dataGridPopUpData);break;	
				case 22: datos = getSistemaProd307(dataGridPopUpData);break;	
				case 23: datos = getSistemaCultivo(dataGridPopUpData);break;	
				case 24: datos = getCodRedRendimiento(dataGridPopUpData);break;	
				case 25: datos = getTipoPlantacion309(dataGridPopUpData);break;
				case 26: datos = getSistemaCultivo310(dataGridPopUpData);break;
				case 27: datos = getSistemaConduccion(dataGridPopUpData);break;
				case 28: datos = getPracticaCultural314(dataGridPopUpData);break;
				case 29: datos = getSistemaCultivo312(dataGridPopUpData);break;
				case 30: datos = getIGP307FacAmb(dataGridPopUpData);break;
				case 31: datos = getVistaPorFactores(dataGridPopUpData);break;
				case 32: datos = getFecFinGarantiasEspecificoYGenerico(dataGridPopUpData);break;
				case 33: datos = getNumAniosDesdePoda(dataGridPopUpData);break;
				case 56: datos = getFecFinGarantiasEspecificoYGenerico308(dataGridPopUpData);break;
				default: break;
			}  
		}
		
		return datos;
	}
	
	public ListOrgDatosVO dameLista(String codConcepto){
		ListOrgDatosVO datos = null;

		DatagridPopupVO dataGridPopUpData = new DatagridPopupVO();
		dataGridPopUpData.setBbconcepto(codConcepto);
				
		datos = getVistaPorFactores(dataGridPopUpData);
		return datos;
	}
	
	private ListOrgDatosVO getDenomOrigen(DatagridPopupVO dataGridPopUpData)
	{
		ListOrgDatosVO denomOrigen = new ListOrgDatosVO();
		
		try{
			// Create and set filter
			VistaDenomOrigenFiltro filtro = new VistaDenomOrigenFiltro();
			filtro.setLineaSeguroId(new Long(dataGridPopUpData.getCodLinea()));
			filtro.setCodcomarca(new BigDecimal(dataGridPopUpData.getCodComarca().trim()));
			filtro.setCodcultivo(new BigDecimal(dataGridPopUpData.getCultivo().trim()));
			filtro.setCodmodulo(dataGridPopUpData.getCodModulo().trim());
			filtro.setCodprovincia(new BigDecimal(dataGridPopUpData.getCodProvincia().trim()));
			filtro.setCodvariedad(new BigDecimal(dataGridPopUpData.getVariedad().trim()));
			filtro.setCodtermino(new BigDecimal(dataGridPopUpData.getCodTermino()));
			filtro.setSubtermino(new Character(dataGridPopUpData.getCodSubTermino().charAt(0)));
			filtro.setCodtipocapital(new BigDecimal(dataGridPopUpData.getCodTipoCapital().trim()));
			
			
			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);
			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);
			denomOrigen.setCabeceras(cabeceras);
			denomOrigen.setNombreAtributo(atributos);
			denomOrigen.setNombreDescriptivo("Denominaciones de origen");
			denomOrigen.setDatosVO(parseaDO(origenDatosDao.getObjects(filtro)));
			denomOrigen.setNombreVO("VistaDenomOrigenVO");
		}
		catch(Exception exception){
			logger.error("Se ha producido un erro en la VistaDenomOrigen",exception);
		}
		return denomOrigen;
		
	}
	
	private ListOrgDatosVO getDestinos(DatagridPopupVO dataGridPopUpData)
	{
		ListOrgDatosVO destinos = new ListOrgDatosVO();
		
		try{
			// Create and set filter
			VistaDestinoFiltro filtro = new VistaDestinoFiltro();
	        filtro.setLineaSeguroId(new Long(dataGridPopUpData.getCodLinea()));
			filtro.setCodcomarca(new BigDecimal(dataGridPopUpData.getCodComarca()));
			filtro.setCodcultivo(new BigDecimal(dataGridPopUpData.getCultivo()));
			filtro.setCodmodulo(dataGridPopUpData.getCodModulo().trim());
			filtro.setCodprovincia(new BigDecimal(dataGridPopUpData.getCodProvincia()));
			filtro.setCodvariedad(new BigDecimal(dataGridPopUpData.getVariedad()));
			filtro.setCodtipocapital(new BigDecimal(dataGridPopUpData.getCodTipoCapital().trim()));
			filtro.setSubtermino(new Character(dataGridPopUpData.getCodSubTermino().charAt(0)));
			
			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);
			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);
			destinos.setCabeceras(cabeceras);
			destinos.setNombreAtributo(atributos);
			destinos.setNombreDescriptivo("Destinos");
			destinos.setDatosVO(parseaDEST(origenDatosDao.getObjects(filtro)));
			destinos.setNombreVO("VistaDestinosVO");
		
		}
		catch(Exception exception){
			logger.error("Se ha producido un error en la VistaDestinos",exception);
		}
		return destinos;
	}
	
	private ListOrgDatosVO getFecFinGarantias(DatagridPopupVO dataGridPopUpData)
	{
		ListOrgDatosVO garantias = new ListOrgDatosVO();
		
		try{

			// Create and set filter
			VistaFechaFinGarantiasFiltro filtro = new VistaFechaFinGarantiasFiltro();
            filtro.setLineaSeguroId(new Long(dataGridPopUpData.getCodLinea()));
			filtro.setCodcultivo(new BigDecimal(dataGridPopUpData.getCultivo()));
			filtro.setCodvariedad(new BigDecimal(dataGridPopUpData.getVariedad()));
			filtro.setCodmodulo(new String(dataGridPopUpData.getCodModulo()));
			filtro.setCodprovincia(new BigDecimal(dataGridPopUpData.getCodProvincia()));
			filtro.setCodcomarca(new BigDecimal(dataGridPopUpData.getCodComarca()));
			filtro.setCodtermino(new BigDecimal(dataGridPopUpData.getCodTermino()));
			filtro.setSubtermino(new Character(dataGridPopUpData.getCodSubTermino().charAt(0)));
			filtro.setCodtipocapital(new BigDecimal(dataGridPopUpData.getCodTipoCapital()));
//			Si se ha seleccionado una practica cultural, la incluimos en el filtro
			if(!dataGridPopUpData.getCodPracticaCultural().equals("")){
				filtro.setCodpracticacultural(new BigDecimal(dataGridPopUpData.getCodPracticaCultural().trim()));
			}
			
			logger.debug("OrigenesDatosManager.getFecFinGarantias - Practica cultural: " + filtro.getCodpracticacultural());
			
			//Realizamos la consulta de fechas de fin de garantía con los datos introducidos por el usuario
			//y si no obtenemos resultados para dichos datos, los vamos cambiando por los 'genéricos'.
			List<FechaFinGarantia> listaFechasGarantia = origenDatosDao.getObjects(filtro);
			if (listaFechasGarantia.isEmpty()){
				filtro.setSubtermino(new Character('9'));
				listaFechasGarantia = origenDatosDao.getObjects(filtro);
				if (listaFechasGarantia.isEmpty()){
					filtro.setCodtermino(new BigDecimal("999"));
					listaFechasGarantia = origenDatosDao.getObjects(filtro);
					if (listaFechasGarantia.isEmpty()){
						filtro.setCodcomarca(new BigDecimal("99"));
						listaFechasGarantia = origenDatosDao.getObjects(filtro);
						if (listaFechasGarantia.isEmpty()){
							filtro.setCodprovincia(new BigDecimal("99"));
							listaFechasGarantia = origenDatosDao.getObjects(filtro);
							if (listaFechasGarantia.isEmpty()){
								filtro.setCodvariedad(new BigDecimal("999"));
								listaFechasGarantia = origenDatosDao.getObjects(filtro);
								if (listaFechasGarantia.isEmpty()){
									filtro.setCodcultivo(new BigDecimal("999"));
									listaFechasGarantia = origenDatosDao.getObjects(filtro);
									if (listaFechasGarantia.isEmpty()){
										//Por último añadimos el módulo genérico al filtro
										filtro.setCodmodulo(filtro.getCodmodulo() + ";99999");
										listaFechasGarantia = origenDatosDao.getObjects(filtro);
										
									}
								}
							}
						}
					}
				}
			}
			
			List<String> cabeceras =  new ArrayList<String>();
			cabeceras.add("Fecha fin");
			List<String> atributos = new ArrayList<String>();
			atributos.add("fecha");
			garantias.setCabeceras(cabeceras);
			garantias.setNombreAtributo(atributos);
			garantias.setNombreDescriptivo("Fecha fin garantia");
			garantias.setDatosVO(parseaFecFinGarant(listaFechasGarantia));
			garantias.setNombreVO("VistaFecFinGarantiaVO");
		
		}
		catch(Exception exception){
			logger.error("Se ha producido un error en la VistaFechaFinGarantias",exception);
		}
		return garantias;
	}
	
	/**
	 * Método para obtener un listado de fechas de fin de garantía buscando por los valores de los datos identificativos de 
	 * la parcela y por los valores genéricos
	 * @param dataGridPopUpData
	 * @return
	 */
	private ListOrgDatosVO getFecFinGarantiasEspecificoYGenerico(DatagridPopupVO dataGridPopUpData)
	{
		ListOrgDatosVO garantias = new ListOrgDatosVO();
		
		try{

			// Create and set filter
			VistaFechaFinGarantiasEspecificoYGenericoFiltro filtro = new VistaFechaFinGarantiasEspecificoYGenericoFiltro();
            filtro.setLineaSeguroId(new Long(dataGridPopUpData.getCodLinea()));
			filtro.setCodcultivo(new BigDecimal(dataGridPopUpData.getCultivo()));
			filtro.setCodvariedad(new BigDecimal(dataGridPopUpData.getVariedad()));
			filtro.setCodmodulo(new String(dataGridPopUpData.getCodModulo()));
			filtro.setCodprovincia(new BigDecimal(dataGridPopUpData.getCodProvincia()));
			filtro.setCodcomarca(new BigDecimal(dataGridPopUpData.getCodComarca()));
			filtro.setCodtermino(new BigDecimal(dataGridPopUpData.getCodTermino()));
			filtro.setSubtermino(new Character(dataGridPopUpData.getCodSubTermino().charAt(0)));
			filtro.setCodtipocapital(new BigDecimal(dataGridPopUpData.getCodTipoCapital()));
			//Si se ha seleccionado una practica cultural, la incluimos en el filtro
			if(!dataGridPopUpData.getCodPracticaCultural().equals("")){
				filtro.setCodpracticacultural(new BigDecimal(dataGridPopUpData.getCodPracticaCultural().trim()));
			}
			
			logger.debug("OrigenesDatosManager.getFecFinGarantiasEspecificoYGenerico - Practica cultural: " + filtro.getCodpracticacultural());
			
			//Realizamos la consulta de fechas de fin de garantía con los datos introducidos por el usuario
			//y si no obtenemos resultados para dichos datos, los vamos cambiando por los 'genéricos'.
			List<FechaFinGarantia> listaFechasGarantia = origenDatosDao.getObjects(filtro);
			
			List<String> cabeceras =  new ArrayList<String>();
			cabeceras.add("Fecha fin");
			List<String> atributos = new ArrayList<String>();
			atributos.add("fecha");
			garantias.setCabeceras(cabeceras);
			garantias.setNombreAtributo(atributos);
			garantias.setNombreDescriptivo("Fecha fin garantia");
			garantias.setDatosVO(parseaFecFinGarant(listaFechasGarantia));
			garantias.setNombreVO("VistaFecFinGarantiaVO");
		
		}
		catch(Exception exception){
			logger.error("Se ha producido un error en la VistaFechaFinGarantias",exception);
		}
		return garantias;
	}
	
	/**
	 * Método para obtener un listado de fechas de fin de garantía buscando por los valores de los datos identificativos de 
	 * la parcela y por los valores genéricos
	 * @param dataGridPopUpData
	 * @return
	 */
	private ListOrgDatosVO getFecFinGarantiasEspecificoYGenerico308(DatagridPopupVO dataGridPopUpData)
	{
		ListOrgDatosVO garantias = new ListOrgDatosVO();
		
		try{

			// Create and set filter
			VistaFechaFinGarantiasEspecificoYGenericoFiltro308 filtro = new VistaFechaFinGarantiasEspecificoYGenericoFiltro308();
            filtro.setLineaSeguroId(new Long(dataGridPopUpData.getCodLinea()));
			filtro.setCodcultivo(new BigDecimal(dataGridPopUpData.getCultivo()));
			filtro.setCodvariedad(new BigDecimal(dataGridPopUpData.getVariedad()));
			filtro.setCodmodulo(new String(dataGridPopUpData.getCodModulo()));
			filtro.setCodprovincia(new BigDecimal(dataGridPopUpData.getCodProvincia()));
			filtro.setCodcomarca(new BigDecimal(dataGridPopUpData.getCodComarca()));
			filtro.setCodtermino(new BigDecimal(dataGridPopUpData.getCodTermino()));
			filtro.setSubtermino(new Character(dataGridPopUpData.getCodSubTermino().charAt(0)));
			filtro.setCodtipocapital(new BigDecimal(dataGridPopUpData.getCodTipoCapital()));
			//Si se ha seleccionado una practica cultural, la incluimos en el filtro
			if(!dataGridPopUpData.getCodPracticaCultural().equals("")){
				filtro.setCodpracticacultural(new BigDecimal(dataGridPopUpData.getCodPracticaCultural().trim()));
			}
			
			logger.debug("OrigenesDatosManager.getFecFinGarantiasEspecificoYGenerico308 - Practica cultural: " + filtro.getCodpracticacultural());
			
			//Realizamos la consulta de fechas de fin de garantía con los datos introducidos por el usuario
			//y si no obtenemos resultados para dichos datos, los vamos cambiando por los 'genéricos'.
			List<FechaFinGarantia> listaFechasGarantia = origenDatosDao.getObjects(filtro);
			
			List<String> cabeceras =  new ArrayList<String>();
			cabeceras.add("Fecha fin");
			List<String> atributos = new ArrayList<String>();
			atributos.add("fecha");
			garantias.setCabeceras(cabeceras);
			garantias.setNombreAtributo(atributos);
			garantias.setNombreDescriptivo("Fecha fin garantia");
			garantias.setDatosVO(parseaFecFinGarant(listaFechasGarantia));
			garantias.setNombreVO("VistaFecFinGarantiaVO");
		
		}
		catch(Exception exception){
			logger.error("Se ha producido un error en la VistaFechaFinGarantias",exception);
		}
		return garantias;
	}
	
	private ListOrgDatosVO getFechaRecoleccion(DatagridPopupVO dataGridPopUpData)
	{
		ListOrgDatosVO fechasRecoleccion = new ListOrgDatosVO();
		List<BigDecimal>  provincias  = new ArrayList<BigDecimal>();
		List<BigDecimal>  comarcas    = new ArrayList<BigDecimal>();
		List<BigDecimal>  terminos    = new ArrayList<BigDecimal>();
		List<BigDecimal>  cultivo     = new ArrayList<BigDecimal>(); 
		List<BigDecimal>  variedad    = new ArrayList<BigDecimal>();
		List<Character>   subterminos = new ArrayList<Character>();
		
		try{

			BigDecimal idClase = new BigDecimal(dataGridPopUpData.getClaseId());
			
			provincias  = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CODPROVINCIA, idClase);
			comarcas    = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CODCOMARCA, idClase);
			terminos    = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CODTERMINO, idClase);
			cultivo     = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CULTIVO_ID_CODCULTIVO, idClase);
			variedad    = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_VARIEDAD_ID_CODVARIEDAD, idClase);
			subterminos = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_SUBTERMINO, idClase);
			
			// Create and set filter
			VistaFechaRecoleccionFiltro filtro = new VistaFechaRecoleccionFiltro();
			filtro.setListas(provincias, comarcas, terminos, cultivo, variedad, subterminos);
            filtro.setLineaSeguroId(new Long(dataGridPopUpData.getCodLinea()));
			filtro.setCodcultivo(new BigDecimal(dataGridPopUpData.getCultivo()));
			filtro.setCodvariedad(new BigDecimal(dataGridPopUpData.getVariedad()));
			filtro.setCodmodulo(new String(dataGridPopUpData.getCodModulo()));
			filtro.setCodprovincia(new BigDecimal(dataGridPopUpData.getCodProvincia()));
			filtro.setCodcomarca(new BigDecimal(dataGridPopUpData.getCodComarca()));
			filtro.setCodtermino(new BigDecimal(dataGridPopUpData.getCodTermino()));
			filtro.setSubtermino(new Character(dataGridPopUpData.getCodSubTermino().charAt(0)));
			filtro.setCodtipocapital(new BigDecimal(dataGridPopUpData.getCodTipoCapital()));
			
			//Realizamos la consulta de fechas de fin de garantía con los datos introducidos por el usuario
			//y si no obtenemos resultados para dichos datos, los vamos cambiando por los 'genéricos'.
			List<FechaRecoleccion> listaFechasRecol = origenDatosDao.getObjects(filtro);
			if (listaFechasRecol.isEmpty()){
				filtro.setSubtermino(new Character('9'));
				listaFechasRecol = origenDatosDao.getObjects(filtro);
				if(listaFechasRecol.isEmpty()){
					filtro.setCodtermino(new BigDecimal("999"));
					listaFechasRecol = origenDatosDao.getObjects(filtro);
					if (listaFechasRecol.isEmpty()){
						filtro.setCodcomarca(new BigDecimal("99"));
						listaFechasRecol = origenDatosDao.getObjects(filtro);
						if (listaFechasRecol.isEmpty()){
							filtro.setCodprovincia(new BigDecimal("99"));
							listaFechasRecol = origenDatosDao.getObjects(filtro);
							if (listaFechasRecol.isEmpty()){
								filtro.setCodvariedad(new BigDecimal("999"));
								listaFechasRecol = origenDatosDao.getObjects(filtro);
								if (listaFechasRecol.isEmpty()){
									filtro.setCodcultivo(new BigDecimal("999"));
									listaFechasRecol = origenDatosDao.getObjects(filtro);
									if (listaFechasRecol.isEmpty()){
										//Por último añadimos el módulo genérico al filtro
										filtro.setCodmodulo(filtro.getCodmodulo() + ";99999");
										listaFechasRecol = origenDatosDao.getObjects(filtro);
									}
								}
							}
						}
					}
				}
			}
			
			List<String> cabeceras =  new ArrayList<String>();
			cabeceras.add("Fecha desde");
			cabeceras.add("Fecha hasta");
			List<String> atributos = new ArrayList<String>();
			atributos.add("fecha");
			atributos.add(OrigenDatosManager.DESCRIPCION2);
			fechasRecoleccion.setCabeceras(cabeceras);
			fechasRecoleccion.setNombreAtributo(atributos);
			fechasRecoleccion.setNombreDescriptivo("Fecha recoleccion");
			fechasRecoleccion.setDatosVO(parseaFecRecoleccion(listaFechasRecol));
			fechasRecoleccion.setNombreVO("VistaFecRecoleccionVO");
		
		}
		catch(Exception exception){
			logger.error("Se ha producido un error en la VistaFecRecoleccion",exception);
		}
		return fechasRecoleccion;
	}
	
	private ListOrgDatosVO getMedidaPreventiva(DatagridPopupVO dataGridPopUpData)
	{
		ListOrgDatosVO medPrev = new ListOrgDatosVO();
		
		try{
		
			// Create and set filter
			VistaMedidaPreventivaFiltro filtro = new VistaMedidaPreventivaFiltro();
			filtro.setLineaSeguroId(new Long(dataGridPopUpData.getCodLinea()));
			//le pasamos el módulo. Si hay más de uno seleccionado, vendrán separados por ';'
			filtro.setCodmodulo(dataGridPopUpData.getCodModulo());
			filtro.setElegible('N');
			//filtro.setCodriesgocubierto();			
//			Listado con todas las medidas preventivas que no son elegibles.
			List<VistaMedidaPreventiva> MPreventivasN = origenDatosDao.getObjects(filtro);
//			Listado con todas las medidas preventivas que no elegibles.
			filtro.setElegible('S');
			List<VistaMedidaPreventiva> MPreventivasS = origenDatosDao.getObjects(filtro);
//			riesgos elegidos en nuestra poliza
			List<ComparativaPoliza> riesgosElegidos = origenDatosDao.getRiesgosCubiertos(dataGridPopUpData.getCodPoliza(),new BigDecimal("363"),"-1");
//			Lista auxiliar con las MP seleccionadas
			List<VistaMedidaPreventiva> aux = new ArrayList<VistaMedidaPreventiva>();
			if(!riesgosElegidos.isEmpty()){
				for(VistaMedidaPreventiva mp: MPreventivasS){
					for(ComparativaPoliza cp:riesgosElegidos){
						if(mp.getId().getCodriesgocubierto().equals(cp.getId().getCodriesgocubierto())){
							aux.add(mp);
						}
					}
				}
//				Union de MP no elegibles y las elegidas en nuestra poliza
				MPreventivasN.addAll(aux);
			}

			
			List<String> cabeceras= new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);
			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);
			medPrev.setCabeceras(cabeceras);
			medPrev.setNombreAtributo(atributos);
			medPrev.setNombreDescriptivo("Medidas preventivas");
			medPrev.setDatosVO(parseaMedPrev(MPreventivasN));
			medPrev.setNombreVO("VistaMedPreventivasVO");
		
		}
		catch(Exception exception){
			logger.error("Se ha producido un error en la VistaMedPreventivas",exception);
		}
		return medPrev;
	}
	
	private ListOrgDatosVO getPracticaCultural(DatagridPopupVO dataGridPopUpData)
	{
		ListOrgDatosVO practCult = new ListOrgDatosVO();
		SimpleDateFormat sdf = new SimpleDateFormat(OrigenDatosManager.DD_MM_YYYY);
		try{ 
			// Create and set filter
			VistaPracticaCulturalFiltro filtro = new VistaPracticaCulturalFiltro();
			filtro.setLineaSeguroId(new Long(dataGridPopUpData.getLineaSeguroId()));
			filtro.setCodcomarca(new BigDecimal(dataGridPopUpData.getCodComarca()));
			filtro.setCodcultivo(new BigDecimal(dataGridPopUpData.getCultivo().trim()));
			filtro.setCodprovincia(new BigDecimal(dataGridPopUpData.getCodProvincia()));
			filtro.setCodtermino(new BigDecimal(dataGridPopUpData.getCodTermino()));
			filtro.setCodvariedad(new BigDecimal(dataGridPopUpData.getVariedad()));			
			filtro.setSubtermino(new Character(dataGridPopUpData.getCodSubTermino().charAt(0)));
			filtro.setCodtipocapital(new BigDecimal(dataGridPopUpData.getCodTipoCapital().trim()));
			filtro.setCodmodulo(dataGridPopUpData.getCodModulo());
// 			si hay fecha fin de garantias seleccionada, la incluimos en el filtro
			if(!dataGridPopUpData.getFechaFinGarantias().trim().equals("")){
				filtro.setFGaranthasta(sdf.parse(dataGridPopUpData.getFechaFinGarantias().trim()));
			}
			
			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);
			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);
			practCult.setCabeceras(cabeceras);
			practCult.setNombreAtributo(atributos);
			practCult.setNombreDescriptivo("Practica cultural");
			practCult.setDatosVO(parseaPractCult(origenDatosDao.getObjects(filtro)));
			practCult.setNombreVO("VistaPracticaCulturalVO");
		
		}
		catch(Exception exception){
			logger.error("Se ha producido un error en la VistaPracticaCultural",exception);
		}

		return practCult;
	}
	
	private ListOrgDatosVO getPracticaCultural305(DatagridPopupVO dataGridPopUpData)
	{
		ListOrgDatosVO practCult305 = new ListOrgDatosVO();
		
		try{
		
			// Create and set filter
			VistaPracticaCultural305Filtro filtro = new VistaPracticaCultural305Filtro();
			filtro.setCodmodulo(dataGridPopUpData.getCodModulo().trim());
			filtro.setCodcomarca(new BigDecimal(dataGridPopUpData.getCodComarca().trim()));
			filtro.setCodprovincia(new BigDecimal(dataGridPopUpData.getCodProvincia().trim()));
			filtro.setCodtermino(new BigDecimal(dataGridPopUpData.getCodTermino()));
			filtro.setSubtermino(new Character(dataGridPopUpData.getCodSubTermino().charAt(0)));
			filtro.setCodcultivo(new BigDecimal(dataGridPopUpData.getCultivo().trim()));
			filtro.setCodvariedad(new BigDecimal(dataGridPopUpData.getVariedad().trim()));
			filtro.setLineaSeguroId(new Long(dataGridPopUpData.getCodLinea()));
			filtro.setCodtipocapital(new BigDecimal(dataGridPopUpData.getCodTipoCapital().trim()));
			
			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);
			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);
			practCult305.setCabeceras(cabeceras);
			practCult305.setNombreAtributo(atributos);
			practCult305.setNombreDescriptivo("Practica Cultural Precios");
			practCult305.setDatosVO(parseaPractCult305(origenDatosDao.getObjects(filtro)));
			practCult305.setNombreVO("VistaPracticaCultural305VO");
		
		}
		catch(Exception exception){
			logger.error("Se ha producido un error en la VistaPracticaCultural305",exception);
		}

	    return practCult305;	
	}
	
	private ListOrgDatosVO getRotacion(DatagridPopupVO dataGridPopUpData)
	{
		ListOrgDatosVO practRotacion = new ListOrgDatosVO();
		List<BigDecimal>  cultivo     = new ArrayList<BigDecimal>(); 
		List<BigDecimal>  variedad    = new ArrayList<BigDecimal>();
		
		try{
			    BigDecimal idClase = new BigDecimal(dataGridPopUpData.getClaseId());
			
				cultivo     = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CULTIVO_ID_CODCULTIVO, idClase);
				variedad    = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_VARIEDAD_ID_CODVARIEDAD, idClase);
				
				// Create and set filter
				VistaRotacionFiltro filtro = new VistaRotacionFiltro();
				filtro.setListas(cultivo, variedad);
				filtro.setCodmodulo(dataGridPopUpData.getCodModulo().trim());
				filtro.setCodcomarca(new BigDecimal(dataGridPopUpData.getCodComarca().trim()));
				filtro.setCodprovincia(new BigDecimal(dataGridPopUpData.getCodProvincia().trim()));
				filtro.setCodtermino(new BigDecimal(dataGridPopUpData.getCodTermino()));
				filtro.setSubtermino(new Character(dataGridPopUpData.getCodSubTermino().charAt(0)));
				filtro.setCodcultivo(new BigDecimal(dataGridPopUpData.getCultivo().trim()));
				filtro.setCodvariedad(new BigDecimal(dataGridPopUpData.getVariedad().trim()));
				filtro.setLineaSeguroId(new Long(dataGridPopUpData.getCodLinea()));
				filtro.setCodtipocapital(new BigDecimal(dataGridPopUpData.getCodTipoCapital().trim()));
				
				List<String> cabeceras = new ArrayList<String>();
				cabeceras.add(OrigenDatosManager.CODIGO);
				cabeceras.add(OrigenDatosManager.DESCRIPCION);
				List<String> atributos = new ArrayList<String>();
				atributos.add(OrigenDatosManager.CODIGO2);
				atributos.add(OrigenDatosManager.DESCRIPCION2);
				practRotacion.setCabeceras(cabeceras);
				practRotacion.setNombreAtributo(atributos);
				practRotacion.setNombreDescriptivo("Rotacion");
				practRotacion.setDatosVO(parseaRotacion(origenDatosDao.getObjects(filtro)));
				practRotacion.setNombreVO("VistaRotacionVO");
		
		}
		catch(Exception exception){
			logger.error("Se ha producido un error en la VistaRotacion",exception);
		}

	    return practRotacion;	
	}
	
	private ListOrgDatosVO getSistemaProd(DatagridPopupVO dataGridPopUpData)
	{
		ListOrgDatosVO    sistProd    = new ListOrgDatosVO();
		List<BigDecimal>  provincias  = new ArrayList<BigDecimal>();
		List<BigDecimal>  comarcas    = new ArrayList<BigDecimal>();
		List<BigDecimal>  terminos    = new ArrayList<BigDecimal>();
		List<BigDecimal>  cultivo     = new ArrayList<BigDecimal>(); 
		List<BigDecimal>  variedad    = new ArrayList<BigDecimal>();
		List<Character>   subterminos = new ArrayList<Character>();
		
		try{

			BigDecimal idClase = new BigDecimal(dataGridPopUpData.getClaseId());
			
			provincias  = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CODPROVINCIA, idClase);
			comarcas    = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CODCOMARCA, idClase);
			terminos    = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CODTERMINO, idClase);
			cultivo     = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CULTIVO_ID_CODCULTIVO, idClase);
			variedad    = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_VARIEDAD_ID_CODVARIEDAD, idClase);
			subterminos = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_SUBTERMINO, idClase);
			
			// Create and set filter
			VistaSistemaProduccionFiltro filtro = new VistaSistemaProduccionFiltro();
			filtro.setListas(provincias, comarcas, terminos, cultivo, variedad, subterminos);
			filtro.setCodmodulo(dataGridPopUpData.getCodModulo().trim());
			filtro.setCodcomarca(new BigDecimal(dataGridPopUpData.getCodComarca().trim()));
			filtro.setCodcultivo(new BigDecimal(dataGridPopUpData.getCultivo().trim()));
			filtro.setCodprovincia(new BigDecimal(dataGridPopUpData.getCodProvincia().trim()));
			filtro.setCodvariedad(new BigDecimal(dataGridPopUpData.getVariedad().trim()));
			filtro.setLineaSeguroId(new Long(dataGridPopUpData.getCodLinea()));
			filtro.setCodtipocapital(new BigDecimal(dataGridPopUpData.getCodTipoCapital().trim()));

			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);
			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);
			sistProd.setCabeceras(cabeceras);
			sistProd.setNombreAtributo(atributos);
			sistProd.setNombreDescriptivo("Sistema produccion");
			sistProd.setDatosVO(parseaSistProd(origenDatosDao.getObjects(filtro)));
			sistProd.setNombreVO("VistaSistemaProduccionVO");
		
		}
		catch(Exception exception){
			logger.error("Se ha producido un error en la VistaSistemaProduccion",exception);
		}

	    return sistProd;	
	}
	
	private ListOrgDatosVO getMarcoPlantacion(DatagridPopupVO dataGridPopUpData)
	{
		ListOrgDatosVO marcoPlantacion = new ListOrgDatosVO();
		
		try{
			// Create and set filter
			VistaMarcoPlantacionFiltro filtro = new VistaMarcoPlantacionFiltro();
			//filtro.setListas(provincias, comarcas, terminos, cultivo, variedad, subterminos);
			filtro.setLineaSeguroId(new Long(dataGridPopUpData.getCodLinea()));
			filtro.setCodcomarca(new BigDecimal(dataGridPopUpData.getCodComarca()));
			filtro.setCodcultivo(new BigDecimal(dataGridPopUpData.getCultivo()));
			filtro.setCodmodulo(dataGridPopUpData.getCodModulo());
			filtro.setCodprovincia(new BigDecimal(dataGridPopUpData.getCodProvincia()));
			filtro.setCodvariedad(new BigDecimal(dataGridPopUpData.getVariedad()));
			filtro.setSubtermino(new Character(dataGridPopUpData.getCodSubTermino().charAt(0)));
			
			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);
			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);
			marcoPlantacion. setCabeceras(cabeceras);
			marcoPlantacion.setNombreAtributo(atributos);
			marcoPlantacion.setNombreDescriptivo("Marco plantacion");
			marcoPlantacion.setDatosVO(parseaMarcoPlantacion(origenDatosDao.getObjects(filtro)));
			marcoPlantacion.setNombreVO("VistaMarcoPlantacionVO");
		
		}
		catch(Exception exception){
			logger.error("Se ha producido un error en la VistaMarcoPlantacion",exception);
		}
		
		return marcoPlantacion;
	}
	
	
	
	private ListOrgDatosVO getTipoPlantacion (DatagridPopupVO dataGridPopUpData){
		
		ListOrgDatosVO tipoPlantacion = new ListOrgDatosVO();
		List<BigDecimal>  provincias  = new ArrayList<BigDecimal>();
		List<BigDecimal>  comarcas    = new ArrayList<BigDecimal>();
		List<BigDecimal>  terminos    = new ArrayList<BigDecimal>();
		List<BigDecimal>  cultivo     = new ArrayList<BigDecimal>(); 
		List<BigDecimal>  variedad    = new ArrayList<BigDecimal>();
		List<Character>   subterminos = new ArrayList<Character>();
		List<BigDecimal> tiposPlantacion = new ArrayList<BigDecimal>();
		
		try {
			
			
			BigDecimal idClase = new BigDecimal(dataGridPopUpData.getClaseId());
			
			provincias  = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CODPROVINCIA, idClase);
			comarcas    = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CODCOMARCA, idClase);
			terminos    = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CODTERMINO, idClase);
			cultivo     = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CULTIVO_ID_CODCULTIVO, idClase);
			variedad    = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_VARIEDAD_ID_CODVARIEDAD, idClase);
			subterminos = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_SUBTERMINO, idClase);
			tiposPlantacion = origenDatosDao.getCampoClaseDetalle("claseDetalle.tipoPlantacion.codtipoplantacion", idClase);
			
			
			// Create and set filter
			VistaTipoPlantacionFiltro filtro = new VistaTipoPlantacionFiltro();	
			filtro.setListas(provincias, comarcas, terminos, cultivo, variedad, subterminos, tiposPlantacion);
			filtro.setLineaSeguroId(new Long(dataGridPopUpData.getCodLinea()));
			filtro.setCodmodulo(dataGridPopUpData.getCodModulo());
			filtro.setCodcultivo(new BigDecimal(dataGridPopUpData.getCultivo()));
			filtro.setCodvariedad(new BigDecimal(dataGridPopUpData.getVariedad()));
			filtro.setCodprovincia(new BigDecimal(dataGridPopUpData.getCodProvincia()));
			filtro.setCodcomarca(new BigDecimal(dataGridPopUpData.getCodComarca()));
			filtro.setCodtermino(new BigDecimal(dataGridPopUpData.getCodTermino()));
			filtro.setSubtermino(new Character(dataGridPopUpData.getCodSubTermino().charAt(0)));
			filtro.setCodtipocapital(new BigDecimal(dataGridPopUpData.getCodTipoCapital().trim()));
			
			
			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);
			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);
			tipoPlantacion. setCabeceras(cabeceras);
			tipoPlantacion.setNombreAtributo(atributos);
			tipoPlantacion.setNombreDescriptivo("Tipo plantacion");
			tipoPlantacion.setDatosVO(parseaTipoPlantacion(origenDatosDao.getObjects(filtro)));
			tipoPlantacion.setNombreVO("VistaTipoPlantacionVO");
			
		} catch (Exception e) {
			logger.error("Se ha producido un error en la VistaTipoPlantacion",e);
		}
		return tipoPlantacion;
	}
	
	private ListOrgDatosVO getIGP (DatagridPopupVO dataGridPopUpData){
		
		ListOrgDatosVO igp = new ListOrgDatosVO();
		
		try {			
			// Create and set filter
			VistaIGPFiltro filtro = new VistaIGPFiltro();
			filtro.setLineaSeguroId(new Long(dataGridPopUpData.getCodLinea()));
			filtro.setCodmodulo(dataGridPopUpData.getCodModulo());
			filtro.setCodcultivo(new BigDecimal(dataGridPopUpData.getCultivo()));
			filtro.setCodvariedad(new BigDecimal(dataGridPopUpData.getVariedad()));
			filtro.setCodprovincia(new BigDecimal(dataGridPopUpData.getCodProvincia()));

			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);
			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);
			igp. setCabeceras(cabeceras);
			igp.setNombreAtributo(atributos);
			igp.setNombreDescriptivo("IGP");
			igp.setDatosVO(parseaIGP(origenDatosDao.getObjects(filtro)));
			igp.setNombreVO("VistaIGPVO");			
		} catch (Exception e) {
			logger.error("Se ha producido un error en la VistaIGP",e);
		}
		return igp;
	}
	
	private ArrayList<ItemVistaVO> parseaMaterialCubiertaInst(List<MaterialCubierta> listaMatCu) {
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (MaterialCubierta objetoMatCu : listaMatCu)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoMatCu != null ){
					if (objetoMatCu.getCodmaterialcubierta() != null)
					{
						nuevo.setCodigo(objetoMatCu.getCodmaterialcubierta().intValue());
					} 
					else {
						nuevo.setCodigo(-1);
					}
					nuevo.setDescripcion(objetoMatCu.getDescripcion());
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
				
			}
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de VistaMaterialCUbiertaInst",exception);
		}
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaMaterialEstructuraInst(List<MaterialEstructura> listaME) {
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (MaterialEstructura objetoME : listaME)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoME != null){
					if (objetoME.getCodmaterialestructura() != null){ 
						nuevo.setCodigo(objetoME.getCodmaterialestructura().intValue());
					} else { 
						nuevo.setCodigo(-1);
					}					
					nuevo.setDescripcion(objetoME.getDescripcion());					
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
			}
		
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de VistaMaterialEstructuraInst",exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaIGP307(List<IGP> listaIGP) {
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (IGP objetoIGP : listaIGP)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoIGP != null){
					if (objetoIGP.getCodigp() != null){ 
						nuevo.setCodigo(objetoIGP.getCodigp().intValue());
					} else { 
						nuevo.setCodigo(-1);
					}					
					nuevo.setDescripcion(objetoIGP.getDescripcion());					
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
			}
		
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de IGP307",exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaVistPorFactores(List<VistaPorFactores> listaPorFactores) {
		
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (VistaPorFactores vPorFactores : listaPorFactores)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(vPorFactores != null){
					if (vPorFactores.getId().getCodvalor() != null) {
						nuevo.setCodigo(vPorFactores.getId().getCodvalor().intValue());
					} else {
						nuevo.setCodigo(-1);
					}
					nuevo.setDescripcion(vPorFactores.getId().getDescripcion());
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
				
			}
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de VistaPorFactores",exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaIGP307FacAmb(List<VistaIGPFactorAmbito> listaIGP) {
	
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (VistaIGPFactorAmbito objetoIGP : listaIGP)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoIGP != null){
					if (objetoIGP.getId().getCodigp() != null) {
						nuevo.setCodigo(objetoIGP.getId().getCodigp().intValue());
					} else {
						nuevo.setCodigo(-1);
					}
					nuevo.setDescripcion(objetoIGP.getId().getDescripcion());
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
				
			}
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de VistaIgp",exception);
		}
		
		return resultado;
	}
	//fin borrar
	
	
	
	
	
	
	private ArrayList<ItemVistaVO> parseaSistProd307(List<SistemaProduccion> listaSistemaProduccion) {
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (SistemaProduccion objetoSistProd : listaSistemaProduccion)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoSistProd != null){
					if (objetoSistProd.getCodsistemaproduccion() != null){ 
						nuevo.setCodigo(objetoSistProd.getCodsistemaproduccion().intValue());
					} else { 
						nuevo.setCodigo(-1);
					}					
					nuevo.setDescripcion(objetoSistProd.getDessistemaproduccion());					
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
			}
		
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de SistemaProduccion307",exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaDO (List<VistaDenomOrigen> listaDO)
	{
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (VistaDenomOrigen objetoDO : listaDO)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoDO != null ){
					if (objetoDO.getId().getCoddenomorigenigp() != null)
					{
						nuevo.setCodigo(objetoDO.getId().getCoddenomorigenigp().intValue());
					} 
					else {
						nuevo.setCodigo(-1);
					}
					nuevo.setDescripcion(objetoDO.getId().getDesdenomorigenigp());
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
				
			}
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de VistaDenomOrigen",exception);
		}
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaDEST (List<VistaDestino> listaDEST)
	{
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (VistaDestino objetoDEST : listaDEST)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoDEST != null){
					if (objetoDEST.getId().getCoddestino() != null) {
						nuevo.setCodigo(objetoDEST.getId().getCoddestino().intValue());
					} 
					else {
						nuevo.setCodigo(-1);
					}
					nuevo.setDescripcion(objetoDEST.getId().getDesdestino());
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
				
			}
		
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de VistaDestino",exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaFecFinGarant (List<FechaFinGarantia> listaFG)
	{
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (FechaFinGarantia objetoFG: listaFG)
			{
				if (objetoFG != null){
					ItemVistaVO nuevo = new ItemVistaVO();
					SimpleDateFormat sdf = new SimpleDateFormat(OrigenDatosManager.DD_MM_YYYY);					
					nuevo.setFecha(sdf.format(objetoFG.getFgaranthasta()));
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
			}
		
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de VistaFechaFinGarantias",exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaFecRecoleccion (List<FechaRecoleccion> listaFG)
	{
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (FechaRecoleccion objetoFG: listaFG)
			{
				if (objetoFG != null){
					ItemVistaVO nuevo = new ItemVistaVO();
					SimpleDateFormat sdf = new SimpleDateFormat(OrigenDatosManager.DD_MM_YYYY);					
					nuevo.setFecha(sdf.format(objetoFG.getFrecoldesde()));
					nuevo.setDescripcion(sdf.format(objetoFG.getFrecolhasta()));
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
			}
		
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de VistaFechaRecoleccion",exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaMedPrev (List<VistaMedidaPreventiva> listaMP)
	{
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (VistaMedidaPreventiva objetoMP: listaMP)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoMP != null){
					if (objetoMP.getId().getCodmedidapreventiva() != null) {
						nuevo.setCodigo(objetoMP.getId().getCodmedidapreventiva().intValue());
					} else {
						nuevo.setCodigo(-1);
					}
					nuevo.setDescripcion(objetoMP.getId().getCodmedidapreventiva().intValue() + " - " + objetoMP.getId().getDesmedidapreventiva());
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
				
				
			}
		
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de VistaMedidadPreventiva",exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaPractCult (List<VistaPracticaCultural> listaPC)
	{
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (VistaPracticaCultural objetoPC : listaPC)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				
				if(objetoPC != null){
					if (objetoPC != null && objetoPC.getId().getCodpracticacultural() != null) {
						nuevo.setCodigo(objetoPC.getId().getCodpracticacultural().intValue());
					}
					else {
						nuevo.setCodigo(-1);
					}				
					nuevo.setDescripcion(objetoPC.getId().getDespracticacultural());
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
			}
		
			
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de VistaPracticaCultural",exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaPractCult305 (List<VistaPracticaCultural305> listaSP)
	{
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (VistaPracticaCultural305 objetoPC : listaSP)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoPC != null){
					if (objetoPC.getId().getCodpracticacultural() != null) {
						nuevo.setCodigo(objetoPC.getId().getCodpracticacultural().intValue());
					} else {
						nuevo.setCodigo(-1);
					}
					nuevo.setDescripcion(objetoPC.getId().getDespracticacultural());
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
				
			}
		
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de VistaPracticaCultural305",exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaRotacion (List<VistaRotacion> listaSP)
	{
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (VistaRotacion objetoPC : listaSP)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoPC != null){
					if (objetoPC.getId().getCodrotacion() != null) {
						nuevo.setCodigo(objetoPC.getId().getCodrotacion().intValue());
					} else {
						nuevo.setCodigo(-1);
					}
					nuevo.setDescripcion(objetoPC.getId().getDescripcion());
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
				
			}
		
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de VistaPracticaCultural305",exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaSistProd (List<VistaSistemaProduccion> listaSP)
	{
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (VistaSistemaProduccion objetoPC : listaSP)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoPC != null){
					if (objetoPC.getId().getCodsistemaproduccion() != null) {
						nuevo.setCodigo(objetoPC.getId().getCodsistemaproduccion().intValue());
					} else {
						nuevo.setCodigo(-1);
					}
					nuevo.setDescripcion(objetoPC.getId().getDessistemaproduccion());
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
				
			}
		
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de VistaSistemaProduccion",exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaMarcoPlantacion (List<VistaMarcoPlantacion> listaMPA)
	{
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (VistaMarcoPlantacion objetoMPA : listaMPA)
			{
				if (objetoMPA != null){
					ItemVistaVO nuevo = new ItemVistaVO();
					if (objetoMPA != null && objetoMPA.getId().getCodtipomarcoplantac() != null) {
						nuevo.setCodigo(objetoMPA.getId().getCodtipomarcoplantac().intValue());
					} else {
						nuevo.setCodigo(-1);
					}
					nuevo.setDescripcion(objetoMPA.getId().getDestipomarcoplantac());
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
			}
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de VistaMarcoPlantacion",exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaTipoPlantacion (List<VistaTipoPlantacion> listaTP)
	{
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (VistaTipoPlantacion objetoTP : listaTP)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoTP != null){
					if (objetoTP.getId().getCodtipoplantacion() != null) {
						nuevo.setCodigo(objetoTP.getId().getCodtipoplantacion().intValue());
					} else {
						nuevo.setCodigo(-1);
					}
					nuevo.setDescripcion(objetoTP.getId().getDestipoplantacion());
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
				
			}
		
		}catch(Exception exception){
			logger.error(OrigenDatosManager.SE_HA_PRODUCIDO_UN_ERROR_AL_PARSEAR_LOS_DATOS_DE_VISTA_TIPO_PLANTACION,exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaTipoPlantacion309 (List<VistaTipoPlantacion309> listaTP)
	{
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (VistaTipoPlantacion309 objetoTP : listaTP)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoTP != null){
					if (objetoTP.getId().getCodtipoplantacion() != null) {
						nuevo.setCodigo(objetoTP.getId().getCodtipoplantacion().intValue());
					} else {
						nuevo.setCodigo(-1);
					}
					nuevo.setDescripcion(objetoTP.getId().getDestipoplantacion());
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
				
			}
		
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de VistaTipoPlantacion309",exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaIGP (List<VistaIGP> listaIGP)
	{
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
//			INCIDENCIA IGP, ADD UN CAMPO CON VALOR CERO PERO QUE NO SE ENVIARA A AGROSEGURO
			if (listaIGP.size() > 0){
				ItemVistaVO valorNoValido = new ItemVistaVO();
				valorNoValido.setCodigo(0);
				valorNoValido.setDescripcion("SIN VALOR");
				resultado.add(valorNoValido);
			}
			for (VistaIGP objetoIGP : listaIGP)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoIGP != null){
					if (objetoIGP.getId().getCodigp() != null) {
						nuevo.setCodigo(objetoIGP.getId().getCodigp().intValue());
					} else {
						nuevo.setCodigo(-1);
					}
					nuevo.setDescripcion(objetoIGP.getId().getDescripcion());
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
				
			}

			
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de VistaIgp",exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaSistProt (List<VistaSistemaProteccion> listaSP)
	{
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (VistaSistemaProteccion objetoPC : listaSP)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoPC != null){
					if (objetoPC.getId().getCodsistemaproteccion() != null) {
						nuevo.setCodigo(objetoPC.getId().getCodsistemaproteccion().intValue());
					} else {
						nuevo.setCodigo(-1);
					}
					nuevo.setDescripcion(objetoPC.getId().getDessistemaproteccion());
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
			}
		
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de VistaSistemaProteccion",exception);
		}
		
		return resultado;
	}

	private ArrayList<ItemVistaVO> parseaCicloCultivo(List<VistaCicloCultivo> listaCC) {
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (VistaCicloCultivo objetoCC : listaCC)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoCC != null){
					if (objetoCC.getId().getCodciclocultivo() != null) {
						nuevo.setCodigo(objetoCC.getId().getCodciclocultivo().intValue());
					} else {
						nuevo.setCodigo(-1);
					}
					nuevo.setDescripcion(objetoCC.getId().getDesciclocultivo());
					SimpleDateFormat sdf = new SimpleDateFormat(OrigenDatosManager.DD_MM_YYYY);					
					nuevo.setFecha1(sdf.format(objetoCC.getId().getFeciniciocontrata()));
					nuevo.setFecha2(sdf.format(objetoCC.getId().getFecfincontrata()));
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
			}
		
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de VistaCicloCultivo",exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaMaterialEstructura(List<VistaMaterialEstructura> listaME) {
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (VistaMaterialEstructura objetoME : listaME)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoME != null) {
					
					if (objetoME.getId().getCodmaterialestructura() != null) {
						nuevo.setCodigo(objetoME.getId().getCodmaterialestructura().intValue());
					} else { 
						nuevo.setCodigo(-1);
					}
					
					nuevo.setDescripcion(objetoME.getId().getDescripcion());
					
					if (!resultado.contains(nuevo)) {
						resultado.add(nuevo);
					}
				}
			}
		
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de VistaMaterialEstructura",exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaMaterialCubierta (List<VistaMaterialCubierta> listaMC) {
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (VistaMaterialCubierta objetoMC : listaMC)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoMC != null){
					
					if (objetoMC.getId().getCodmaterialcubierta() != null) {
						nuevo.setCodigo(objetoMC.getId().getCodmaterialcubierta().intValue());
					} else { 
						nuevo.setCodigo(-1);
					}
					
					nuevo.setDescripcion(objetoMC.getId().getDescripcion());
					
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
			}
		
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de VistaMaterialCubierta",exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaTipoInstalacion (List<VistaTipoInstalacion> listaTI) {
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (VistaTipoInstalacion objetoTI : listaTI)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoTI != null){
					
					if (objetoTI.getId().getCodtipoinstalacion() != null) {
						nuevo.setCodigo(objetoTI.getId().getCodtipoinstalacion().intValue());
					} else { 
						nuevo.setCodigo(-1);
					}
					
					nuevo.setDescripcion(objetoTI.getId().getDescripcion());
					
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
			}
		
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de VistaTipoInstalacion",exception);
		}
		
		return resultado;
	}
	
	
	private ArrayList<ItemVistaVO> parseaCodigoCertificacion (List<VistaCertificadoEstructura> listaMC) {
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (VistaCertificadoEstructura objetoMC : listaMC)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoMC != null){
					
					if (objetoMC.getId().getCodcertificadoinstal() != null) {
						nuevo.setCodigo(objetoMC.getId().getCodcertificadoinstal().intValue());
					} else { 
						nuevo.setCodigo(-1);
					}
					
					nuevo.setDescripcion(objetoMC.getId().getDescripcion());
					
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
			}
		
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de VistaMaterialCubierta",exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaSistCult (List<VistaSistemaCultivo> listaSC)
	{
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (VistaSistemaCultivo objetoSC : listaSC)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoSC != null){
					if (objetoSC.getId().getCodsistemacultivo() != null) {
						nuevo.setCodigo(objetoSC.getId().getCodsistemacultivo().intValue());
					} else {
						nuevo.setCodigo(-1);
					}
					nuevo.setDescripcion(objetoSC.getId().getDessistemacultivo());
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
				
			}
		
		}catch(Exception exception){
			logger.error(OrigenDatosManager.SE_HA_PRODUCIDO_UN_ERROR_AL_PARSEAR_LOS_DATOS_DE_VISTA_TIPO_PLANTACION,exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaSistCult312 (List<VistaSistemaCultivo312> listaSC)
	{
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (VistaSistemaCultivo312 objetoSC : listaSC)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoSC != null){
					if (objetoSC.getId().getCodsistemacultivo() != null) {
						nuevo.setCodigo(objetoSC.getId().getCodsistemacultivo().intValue());
					} else {
						nuevo.setCodigo(-1);
					}
					nuevo.setDescripcion(objetoSC.getId().getDessistemacultivo());
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
				
			}
		
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de VistaSisCultivo312",exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaSistCult310 (List<SistemaCultivo> listaSC)
	{
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (SistemaCultivo objetoSC : listaSC)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoSC != null){
					if (objetoSC.getCodsistemacultivo() != null) {
						nuevo.setCodigo(objetoSC.getCodsistemacultivo().intValue());
					} else {
						nuevo.setCodigo(-1);
					}
					nuevo.setDescripcion(objetoSC.getDessistemacultivo());
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
				
			}
		
		}catch(Exception exception){
			logger.error(OrigenDatosManager.SE_HA_PRODUCIDO_UN_ERROR_AL_PARSEAR_LOS_DATOS_DE_VISTA_TIPO_PLANTACION,exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaNumAniosPoda(List<NumAniosDesdePoda> lstNumAniosPoda){
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for(int i = 0; i < lstNumAniosPoda.size(); i++)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				NumAniosDesdePoda objetoCRR = lstNumAniosPoda.get(i);

				if(objetoCRR != null){
					if (objetoCRR.getCodnumaniospoda() != null){
						nuevo.setCodigo(objetoCRR.getCodnumaniospoda().intValue());
					}else{
						nuevo.setCodigo(-1);
					}
					nuevo.setDescripcion(objetoCRR.getDesnumaniospoda());
					if (!resultado.contains(nuevo)){
						resultado.add(nuevo);
					}
				}
			}
		
		}catch(Exception exception){
			logger.error(OrigenDatosManager.SE_HA_PRODUCIDO_UN_ERROR_AL_PARSEAR_LOS_DATOS_DE_VISTA_TIPO_PLANTACION,exception);
		}
		
		return resultado;
	}
	
	private ArrayList<ItemVistaVO> parseaSistConduccion(List<SistemaConduccion> lstSistemaConduccion){
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for(int i = 0; i < lstSistemaConduccion.size(); i++)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				SistemaConduccion objetoCRR = lstSistemaConduccion.get(i);

				if(objetoCRR != null){
					if (objetoCRR.getCodsistemaconduccion() != null){
						nuevo.setCodigo(objetoCRR.getCodsistemaconduccion().intValue());
					}else{
						nuevo.setCodigo(-1);
					}
					nuevo.setDescripcion(objetoCRR.getDessistemaconduccion());
					if (!resultado.contains(nuevo)){
						resultado.add(nuevo);
					}
				}
			}
		
		}catch(Exception exception){
			logger.error(OrigenDatosManager.SE_HA_PRODUCIDO_UN_ERROR_AL_PARSEAR_LOS_DATOS_DE_VISTA_TIPO_PLANTACION,exception);
		}
		
		return resultado;
		
		
		
	}
	
	private ArrayList<ItemVistaVO> parseaPracticaCultural314(List<PracticaCultural> lstPracticaCultural314){
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for(int i = 0; i < lstPracticaCultural314.size(); i++)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				PracticaCultural objetoCRR = lstPracticaCultural314.get(i);

				if(objetoCRR != null){
					if (objetoCRR.getCodpracticacultural() != null){
						nuevo.setCodigo(objetoCRR.getCodpracticacultural().intValue());
					}else{
						nuevo.setCodigo(-1);
					}
					nuevo.setDescripcion(objetoCRR.getDespracticacultural());
					if (!resultado.contains(nuevo)){
						resultado.add(nuevo);
					}
				}
			}
		
		}catch(Exception exception){
			logger.error("Se ha producido un error al parsear los datos de PracticaCultural314",exception);
		}
		
		return resultado;
		
		
		
	}
	
	private ArrayList<ItemVistaVO> parseaCodRedRend (List<VistaCodigoReduccionRendimiento> listaCRR)
	{
		ArrayList<ItemVistaVO> resultado = new ArrayList<ItemVistaVO>();
		
		try{
			for (VistaCodigoReduccionRendimiento objetoCRR : listaCRR)
			{
				ItemVistaVO nuevo = new ItemVistaVO();
				if(objetoCRR != null){
					if (objetoCRR.getId().getCodreducrdto() != null) {
						nuevo.setCodigo(objetoCRR.getId().getCodreducrdto().intValue());
					} else {
						nuevo.setCodigo(-1);
					}
					nuevo.setDescripcion(objetoCRR.getId().getDesreducrdto());
					if (!resultado.contains(nuevo))
						resultado.add(nuevo);
				}
				
			}
		
		}catch(Exception exception){
			logger.error(OrigenDatosManager.SE_HA_PRODUCIDO_UN_ERROR_AL_PARSEAR_LOS_DATOS_DE_VISTA_TIPO_PLANTACION,exception);
		}
		
		return resultado;
	}

	private ListOrgDatosVO getMaterialCubirtaInstalaciones(	DatagridPopupVO dataGridPopUpData) {
		ListOrgDatosVO materialCubIns = new ListOrgDatosVO();
			try{
				VistaMaterialCubiertaInstFiltro filtro = new VistaMaterialCubiertaInstFiltro();
				
				List<String> cabeceras = new ArrayList<String>();
				cabeceras.add(OrigenDatosManager.CODIGO);
				cabeceras.add(OrigenDatosManager.DESCRIPCION);
				List<String> atributos = new ArrayList<String>();
				atributos.add(OrigenDatosManager.CODIGO2);
				atributos.add(OrigenDatosManager.DESCRIPCION2);
				materialCubIns.setCabeceras(cabeceras);
				materialCubIns.setNombreAtributo(atributos);
				materialCubIns.setNombreDescriptivo("Material Cubierta Instalaciones");
				materialCubIns.setDatosVO(parseaMaterialCubiertaInst(origenDatosDao.getObjects(filtro)));
				materialCubIns.setNombreVO("VistaMaterialCubiertaInstVO");
			
			}
			catch(Exception exception){
				logger.error("Se ha producido un error en la VistaMaterialCubiertaInstVO",exception);
			}

	    return materialCubIns;	
	}

	private ListOrgDatosVO getMaterialEstructuraInstalaciones(DatagridPopupVO dataGridPopUpData) {
		ListOrgDatosVO materialEstIns = new ListOrgDatosVO();
			try{
				
				BigDecimal lineaSeguroId = null;
				BigDecimal codConcepto = new BigDecimal(875);
				
				if(dataGridPopUpData.getLineaSeguroId()!= null){
					lineaSeguroId = new BigDecimal(dataGridPopUpData.getLineaSeguroId());
				}
				
				VistaMaterialEstructuraInstFiltro filtro = new VistaMaterialEstructuraInstFiltro();
				
				
				List<BigDecimal> lstValoresConceptoFactor = origenDatosDao.dameListaTotalValoresConceptoFactor(lineaSeguroId, codConcepto);
				filtro.setLstValoresConceptoFactor(lstValoresConceptoFactor);

				
				List<String> cabeceras = new ArrayList<String>();
				cabeceras.add(OrigenDatosManager.CODIGO);
				cabeceras.add(OrigenDatosManager.DESCRIPCION);
				List<String> atributos = new ArrayList<String>();
				atributos.add(OrigenDatosManager.CODIGO2);
				atributos.add(OrigenDatosManager.DESCRIPCION2);
				materialEstIns.setCabeceras(cabeceras);
				materialEstIns.setNombreAtributo(atributos);
				materialEstIns.setNombreDescriptivo("Material Estructura Instalaciones");
				materialEstIns.setDatosVO(parseaMaterialEstructuraInst(origenDatosDao.getObjects(filtro)));
				materialEstIns.setNombreVO("VistaMaterialEstructuraInstVO");
			
			}
			catch(Exception exception){
				logger.error("Se ha producido un error en la VistaMaterialCubiertaInstVO",exception);
			}

	    return materialEstIns;	
	}
	
	private ListOrgDatosVO getIGP307(DatagridPopupVO dataGridPopUpData) {
		ListOrgDatosVO IGP307 = new ListOrgDatosVO();
			try{
				VistaIGP307Filtro filtro = new VistaIGP307Filtro();
				BigDecimal codConcepto=new BigDecimal(765);
				List<BigDecimal> lstValoresConcepto = origenDatosDao.dameListaValoresConceptoFactor(new BigDecimal(dataGridPopUpData.getCodLinea()),dataGridPopUpData.getCodModulo(), codConcepto);
				// aÃ±adimos el "cero" a la lista
				lstValoresConcepto.add(new BigDecimal(0));
				
				filtro.setLstValoresConcepto(lstValoresConcepto);
				List<String> cabeceras = new ArrayList<String>();
				cabeceras.add(OrigenDatosManager.CODIGO);
				cabeceras.add(OrigenDatosManager.DESCRIPCION);
				List<String> atributos = new ArrayList<String>();
				atributos.add(OrigenDatosManager.CODIGO2);
				atributos.add(OrigenDatosManager.DESCRIPCION2);
				IGP307.setCabeceras(cabeceras);
				IGP307.setNombreAtributo(atributos);
				IGP307.setNombreDescriptivo("IGP");
				IGP307.setDatosVO(parseaIGP307(origenDatosDao.getObjects(filtro)));
				IGP307.setNombreVO("VistaIGP307VO");			
			}
			catch(Exception exception){
				logger.error("Se ha producido un error en la VistaIGP307VO",exception);
			}

	    return IGP307;	
	}
	
	private ListOrgDatosVO getIGP307FacAmb(DatagridPopupVO dataGridPopUpData) {
		ListOrgDatosVO igp = new ListOrgDatosVO();
		
		try {

			BigDecimal lineaSeguroId =(new BigDecimal(dataGridPopUpData.getCodLinea()));
			String codmodulo =(dataGridPopUpData.getCodModulo());
			BigDecimal codcultivo =(new BigDecimal(dataGridPopUpData.getCultivo()));
			BigDecimal codvariedad =(new BigDecimal(dataGridPopUpData.getVariedad()));
			BigDecimal codprovincia =(new BigDecimal(dataGridPopUpData.getCodProvincia()));
			BigDecimal codcomarca = (new BigDecimal(dataGridPopUpData.getCodComarca()));
			BigDecimal codtermino = (new BigDecimal(dataGridPopUpData.getCodTermino()));
			Character subtermino = (new Character(dataGridPopUpData.getCodSubTermino().charAt(0)));
			
			List<VistaIGPFactorAmbito> lstIGPFacAmb = origenDatosDao.getVistaIGP307FacAmb(lineaSeguroId,codmodulo,
					codcultivo,codvariedad,codprovincia,codcomarca, codtermino,subtermino);
			
			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);
			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);
			igp. setCabeceras(cabeceras);
			igp.setNombreAtributo(atributos);
			igp.setNombreDescriptivo("IGP");
			igp.setDatosVO(parseaIGP307FacAmb(lstIGPFacAmb));
			igp.setNombreVO("VistaIGPVO");
			
		} catch (Exception e) {
			logger.error("Se ha producido un error en la VistaIGP",e);
		}
		return igp;
	}
	
	private ListOrgDatosVO getVistaPorFactores(DatagridPopupVO dataGridPopUpData) {
		
		BigDecimal lineaSeguroId = null;
		String codmodulo = null;
		BigDecimal codConcepto = null;
		String label = null;
		ListOrgDatosVO xFactores = new ListOrgDatosVO();
		BigDecimal idClase = null;
		
		try {

			if(!StringUtils.isNullOrEmpty(dataGridPopUpData.getCodLinea())){
				lineaSeguroId =(new BigDecimal(dataGridPopUpData.getCodLinea()));
			}
			if(!StringUtils.isNullOrEmpty(dataGridPopUpData.getCodModulo())){
				codmodulo =(dataGridPopUpData.getCodModulo());
			}
			if(!StringUtils.isNullOrEmpty(dataGridPopUpData.getBbconcepto())){
				codConcepto =(new BigDecimal(dataGridPopUpData.getBbconcepto()));
			}
			if(!StringUtils.isNullOrEmpty(dataGridPopUpData.getBbconcepto())){
				label = (StringUtils.nullToString(dataGridPopUpData.getTextLabel()));
			}
			// MPM 26/09 - Control de nulos y vacíos para la clase 			
			if(!StringUtils.isNullOrEmpty(dataGridPopUpData.getClaseId())){
				idClase = new BigDecimal(dataGridPopUpData.getClaseId());
			}
			List<VistaPorFactores> lstVistaPorFactores = origenDatosDao.getVistaPorFactores(lineaSeguroId,codmodulo,codConcepto, idClase);
			
			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);
			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);
			
			xFactores. setCabeceras(cabeceras);
			xFactores.setNombreAtributo(atributos);
			xFactores.setNombreDescriptivo(label);
			xFactores.setDatosVO(parseaVistPorFactores(lstVistaPorFactores));
			xFactores.setNombreVO("VistaPorFactores");
			
		} catch (Exception e) {
			logger.error("[getVistaPorFactores]Se ha producido un error en la VistaPorFactores",e);
		}
		return xFactores;
	}
	
	
	private ListOrgDatosVO getSistemaProd307(DatagridPopupVO dataGridPopUpData) {
		ListOrgDatosVO sistProd307 = new ListOrgDatosVO();
			try{
				VistaSistemaProduccion307Filtro filtro = new VistaSistemaProduccion307Filtro();
				BigDecimal codConcepto=new BigDecimal(616);
				List<BigDecimal> lstValoresConceptoFactor = origenDatosDao.dameListaValoresConceptoFactor(new BigDecimal(dataGridPopUpData.getCodLinea()),dataGridPopUpData.getCodModulo(), codConcepto);
				filtro.setLstValoresConceptoFactor(lstValoresConceptoFactor);
				
				List<String> cabeceras = new ArrayList<String>();
				cabeceras.add(OrigenDatosManager.CODIGO);
				cabeceras.add(OrigenDatosManager.DESCRIPCION);
				List<String> atributos = new ArrayList<String>();
				atributos.add(OrigenDatosManager.CODIGO2);
				atributos.add(OrigenDatosManager.DESCRIPCION2);
				sistProd307.setCabeceras(cabeceras);
				sistProd307.setNombreAtributo(atributos);
				sistProd307.setNombreDescriptivo("Sistema Produccion");
				sistProd307.setDatosVO(parseaSistProd307(origenDatosDao.getObjects(filtro)));
				sistProd307.setNombreVO("VistaSistemaProduccion307VO");			
			}
			catch(Exception exception){
				logger.error("Se ha producido un error en la VistaSistemaProduccion307VO",exception);
			}

	    return sistProd307;	
	}
	
	
	private ListOrgDatosVO getSistemaCultivo (DatagridPopupVO dataGridPopUpData){
		
		ListOrgDatosVO    sistCult    = new ListOrgDatosVO();
		List<BigDecimal>  provincias  = new ArrayList<BigDecimal>();
		List<BigDecimal>  comarcas    = new ArrayList<BigDecimal>();
		List<BigDecimal>  terminos    = new ArrayList<BigDecimal>();
		List<BigDecimal>  cultivo     = new ArrayList<BigDecimal>(); 
		List<BigDecimal>  variedad    = new ArrayList<BigDecimal>();
		List<Character>   subterminos = new ArrayList<Character>();
		List<BigDecimal>  sistemaCultivos = new ArrayList<BigDecimal>();
		
		
		try {
			
			
			BigDecimal idClase = new BigDecimal(dataGridPopUpData.getClaseId());
			
			provincias  = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CODPROVINCIA, idClase);
			comarcas    = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CODCOMARCA, idClase);
			terminos    = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CODTERMINO, idClase);
			cultivo     = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CULTIVO_ID_CODCULTIVO, idClase);
			variedad    = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_VARIEDAD_ID_CODVARIEDAD, idClase);
			subterminos = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_SUBTERMINO, idClase);
			sistemaCultivos = origenDatosDao.getCampoClaseDetalle("claseDetalle.sistemaCultivo.codsistemacultivo", idClase);
			
			
			// Create and set filter
			VistaSistemaCultivoFiltro filtro = new VistaSistemaCultivoFiltro();	
			filtro.setListas(provincias, comarcas, terminos, cultivo, variedad, subterminos, sistemaCultivos);
			filtro.setLineaSeguroId(new Long(dataGridPopUpData.getCodLinea()));
			filtro.setCodmodulo(dataGridPopUpData.getCodModulo().trim());
			filtro.setCodcultivo(new BigDecimal(dataGridPopUpData.getCultivo()));
			filtro.setCodvariedad(new BigDecimal(dataGridPopUpData.getVariedad()));
			filtro.setCodprovincia(new BigDecimal(dataGridPopUpData.getCodProvincia()));
			filtro.setCodcomarca(new BigDecimal(dataGridPopUpData.getCodComarca()));
			filtro.setCodtermino(new BigDecimal(dataGridPopUpData.getCodTermino()));
			filtro.setSubtermino(new Character(dataGridPopUpData.getCodSubTermino().charAt(0)));
			filtro.setCodtipocapital(new BigDecimal(dataGridPopUpData.getCodTipoCapital().trim()));
			
			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);
			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);
			sistCult. setCabeceras(cabeceras);
			sistCult.setNombreAtributo(atributos);
			sistCult.setNombreDescriptivo("Sistema Cultivo");
			sistCult.setDatosVO(parseaSistCult(origenDatosDao.getObjects(filtro)));
			sistCult.setNombreVO("VistaSistemaCultivoVO");
			
		} catch (Exception e) {
			logger.error("Se ha producido un error en la VistaSistemaCultivo",e);
		}
		return sistCult;
	}
	
private ListOrgDatosVO getSistemaCultivo312 (DatagridPopupVO dataGridPopUpData){
		
		ListOrgDatosVO    sistCult    = new ListOrgDatosVO();
		List<BigDecimal>  provincias  = new ArrayList<BigDecimal>();
		List<BigDecimal>  comarcas    = new ArrayList<BigDecimal>();
		List<BigDecimal>  terminos    = new ArrayList<BigDecimal>();
		List<BigDecimal>  cultivo     = new ArrayList<BigDecimal>(); 
		List<BigDecimal>  variedad    = new ArrayList<BigDecimal>();
		List<Character>   subterminos = new ArrayList<Character>();
		List<BigDecimal>  sistemaCultivos = new ArrayList<BigDecimal>();
		
		
		try {
			
			
			BigDecimal idClase = new BigDecimal(dataGridPopUpData.getClaseId());
			
			provincias  = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CODPROVINCIA, idClase);
			comarcas    = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CODCOMARCA, idClase);
			terminos    = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CODTERMINO, idClase);
			cultivo     = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CULTIVO_ID_CODCULTIVO, idClase);
			variedad    = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_VARIEDAD_ID_CODVARIEDAD, idClase);
			subterminos = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_SUBTERMINO, idClase);
			sistemaCultivos = origenDatosDao.getCampoClaseDetalle("claseDetalle.sistemaCultivo.codsistemacultivo", idClase);
			
			
			// Create and set filter
			//VistaSistemaCultivoFiltro filtro = new VistaSistemaCultivoFiltro();	
			//filtro.setListas(provincias, comarcas, terminos, cultivo, variedad, subterminos, sistemaCultivos);
			Long lineaSeguroId =(new Long(dataGridPopUpData.getCodLinea()));
			String codmodulo = (dataGridPopUpData.getCodModulo().trim());
			BigDecimal codcultivo = (new BigDecimal(dataGridPopUpData.getCultivo()));
			BigDecimal codvariedad = (new BigDecimal(dataGridPopUpData.getVariedad()));
			BigDecimal codprovincia = (new BigDecimal(dataGridPopUpData.getCodProvincia()));
			BigDecimal codcomarca = (new BigDecimal(dataGridPopUpData.getCodComarca()));
			BigDecimal codtermino = (new BigDecimal(dataGridPopUpData.getCodTermino()));
			Character subtermino = (new Character(dataGridPopUpData.getCodSubTermino().charAt(0)));
			BigDecimal codtipocapital = (new BigDecimal(dataGridPopUpData.getCodTipoCapital().trim()));
			
			List<VistaSistemaCultivo312> lstSisCult312 = origenDatosDao.getVistaSistemaCultivo312(provincias, comarcas, terminos,
					cultivo, variedad, subterminos, sistemaCultivos,lineaSeguroId,codmodulo,codcultivo,codvariedad,codprovincia,codcomarca,
					codtermino,subtermino,codtipocapital);
			
			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);
			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);
			sistCult. setCabeceras(cabeceras);
			sistCult.setNombreAtributo(atributos);
			sistCult.setNombreDescriptivo("Sistema Cultivo 312");
			sistCult.setDatosVO(parseaSistCult312(lstSisCult312));
			sistCult.setNombreVO("VistaSistemaCultivoVO");
			
		} catch (Exception e) {
			logger.error("Se ha producido un error en la VistaSistemaCultivo 312",e);
		}
		return sistCult;
	}
	
	private ListOrgDatosVO getSistemaCultivo310 (DatagridPopupVO dataGridPopUpData){
		
		ListOrgDatosVO    sistCult    = new ListOrgDatosVO();
		List<BigDecimal>  sistemaCultivos = new ArrayList<BigDecimal>();
		
		try {
			BigDecimal idClase = new BigDecimal(dataGridPopUpData.getClaseId());
			
			sistemaCultivos = origenDatosDao.getCampoClaseDetalle("claseDetalle.sistemaCultivo.codsistemacultivo", idClase);
			
			// Create and set filter
			VistaSistemaCultivo310Filtro filtro = new VistaSistemaCultivo310Filtro();
			filtro.setLstCodSistemaCultivoClase(sistemaCultivos);
			
			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);
			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);
			sistCult. setCabeceras(cabeceras);
			sistCult.setNombreAtributo(atributos);
			sistCult.setNombreDescriptivo("Sistema Cultivo");
			sistCult.setDatosVO(parseaSistCult310(origenDatosDao.getObjects(filtro)));
			sistCult.setNombreVO("VistaSistemaCultivoVO");
			
		} catch (Exception e) {
			logger.error("Se ha producido un error en la VistaSistemaCultivo",e);
		}
		return sistCult;
	}
	
	private ListOrgDatosVO getSistemaConduccion(DatagridPopupVO dataGridPopUpData){
		
		ListOrgDatosVO sistCond = new ListOrgDatosVO();
		List<SistemaConduccion> lstSistemaConduccion = new ArrayList<SistemaConduccion>();
		BigDecimal lineaSeguroId = null;
		BigDecimal codConcepto = new BigDecimal(131);
		
		
		
		if(dataGridPopUpData.getLineaSeguroId()!= null){
			lineaSeguroId = new BigDecimal(dataGridPopUpData.getLineaSeguroId());
		}

		try {
			
			lstSistemaConduccion = origenDatosDao.getListSistemaConduccion(lineaSeguroId, codConcepto);
			
            
			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);
			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);
			
			sistCond.setCabeceras(cabeceras);
			sistCond.setNombreAtributo(atributos);
			sistCond.setNombreDescriptivo("Sistema Conduccion");
			sistCond.setDatosVO(parseaSistConduccion(lstSistemaConduccion));
			sistCond.setNombreVO("SistemaConduccion");
			
		} catch (Exception e) {
			logger.error("Se ha producido un error al obtener los datos del sistema de conducción",e);
		}
		return sistCond;	
	}

	private ListOrgDatosVO getNumAniosDesdePoda(DatagridPopupVO dataGridPopUpData){
		
		ListOrgDatosVO aniosPoda = new ListOrgDatosVO();
		List<NumAniosDesdePoda> lstNumAniosPoda = new ArrayList<NumAniosDesdePoda>();

		try {
			lstNumAniosPoda = origenDatosDao.getListNumAniosPoda();
            
			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);
			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);
			
			aniosPoda.setCabeceras(cabeceras);
			aniosPoda.setNombreAtributo(atributos);
			aniosPoda.setNombreDescriptivo("Numero anios desde Poda");
			aniosPoda.setDatosVO(parseaNumAniosPoda(lstNumAniosPoda));
			aniosPoda.setNombreVO("NumAniosPoda");
			
		} catch (Exception e) {
			logger.error("Se ha producido un error al obtener los datos del Num anios Poda",e);
		}
		return aniosPoda;	
	}	
	
	private ListOrgDatosVO getPracticaCultural314(DatagridPopupVO dataGridPopUpData){
		
		ListOrgDatosVO sistCond = new ListOrgDatosVO();
		List<PracticaCultural> lstPracticaCultural314 = new ArrayList<PracticaCultural>();
		BigDecimal lineaSeguroId = null;
		BigDecimal codConcepto = new BigDecimal(133);
		
		if(dataGridPopUpData.getLineaSeguroId()!= null){
			lineaSeguroId = new BigDecimal(dataGridPopUpData.getLineaSeguroId());
		}

		try {
			
			lstPracticaCultural314 = origenDatosDao.getListPracticaCultural314(lineaSeguroId, codConcepto);
		    
			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);
			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);
			
			sistCond.setCabeceras(cabeceras);
			sistCond.setNombreAtributo(atributos);
			sistCond.setNombreDescriptivo("Practica Cultural 314");
			sistCond.setDatosVO(parseaPracticaCultural314(lstPracticaCultural314));
			sistCond.setNombreVO("PracticaCultural314");
			
		} catch (Exception e) {
			logger.error("Se ha producido un error al obtener los datos de la práctica cultural",e);
		}
		return sistCond;	
	}
	
	private ListOrgDatosVO getCodRedRendimiento (DatagridPopupVO dataGridPopUpData){
		
		ListOrgDatosVO    codRedRend    = new ListOrgDatosVO();
		List<BigDecimal>  provincias  = new ArrayList<BigDecimal>();
		List<BigDecimal>  comarcas    = new ArrayList<BigDecimal>();
		List<BigDecimal>  terminos    = new ArrayList<BigDecimal>();
		List<Character>   subterminos = new ArrayList<Character>();
		
		try {
			
			BigDecimal idClase = new BigDecimal(dataGridPopUpData.getClaseId());
			
			provincias  = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CODPROVINCIA, idClase);
			comarcas    = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CODCOMARCA, idClase);
			terminos    = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CODTERMINO, idClase);
			subterminos = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_SUBTERMINO, idClase);
			
			// Create and set filter
			VistaCodigoReduccionRendimientoFiltro filtro = new VistaCodigoReduccionRendimientoFiltro();	
			filtro.setListas(provincias, comarcas, terminos, subterminos);
			filtro.setLineaSeguroId(new Long(dataGridPopUpData.getCodLinea()));
			filtro.setCodmodulo(dataGridPopUpData.getCodModulo().trim());
			filtro.setCodprovincia(new BigDecimal(dataGridPopUpData.getCodProvincia()));
			filtro.setCodcomarca(new BigDecimal(dataGridPopUpData.getCodComarca()));
			filtro.setCodtermino(new BigDecimal(dataGridPopUpData.getCodTermino()));
			filtro.setSubtermino(new Character(dataGridPopUpData.getCodSubTermino().charAt(0)));
			
			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);
			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);
			codRedRend. setCabeceras(cabeceras);
			codRedRend.setNombreAtributo(atributos);
			codRedRend.setNombreDescriptivo("Codigo Reduccion Rendimiento");
			codRedRend.setDatosVO(parseaCodRedRend(origenDatosDao.getObjects(filtro)));
			codRedRend.setNombreVO("VistaCodigoReduccionRendimientoVO");
			
		} catch (Exception e) {
			logger.error("Se ha producido un error en la VistaCodigoReduccionRendimiento",e);
		}
		return codRedRend;
	}
	
	private ListOrgDatosVO getTipoPlantacion309 (DatagridPopupVO dataGridPopUpData){
		
		ListOrgDatosVO tipoPlantacion    = new ListOrgDatosVO();
		List<BigDecimal>  cultivo        = new ArrayList<BigDecimal>(); 
		List<BigDecimal>  variedad       = new ArrayList<BigDecimal>();
		
		try {
			
			BigDecimal idClase = new BigDecimal(dataGridPopUpData.getClaseId());
			
			cultivo        = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CULTIVO_ID_CODCULTIVO, idClase);
			variedad       = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_VARIEDAD_ID_CODVARIEDAD, idClase);
			
			// Create and set filter
			VistaTipoPlantacion309Filtro filtro = new VistaTipoPlantacion309Filtro();	
			filtro.setListas(cultivo, variedad );
			filtro.setLineaSeguroId(new Long(dataGridPopUpData.getCodLinea()));
			filtro.setCodmodulo(dataGridPopUpData.getCodModulo());
			filtro.setCodcultivo(new BigDecimal(dataGridPopUpData.getCultivo()));
			filtro.setCodvariedad(new BigDecimal(dataGridPopUpData.getVariedad()));
			
			if(dataGridPopUpData.getSistemaCultivo()!=null && !"".equals(dataGridPopUpData.getSistemaCultivo())){
				filtro.setSistemaCultivo(new BigDecimal(dataGridPopUpData.getSistemaCultivo()));
		    }
			
			filtro.setCodtipocapital(new BigDecimal(dataGridPopUpData.getCodTipoCapital().trim()));
			
			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);
			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);
			tipoPlantacion. setCabeceras(cabeceras);
			tipoPlantacion.setNombreAtributo(atributos);
			tipoPlantacion.setNombreDescriptivo("Tipo plantacion 309");
			tipoPlantacion.setDatosVO(parseaTipoPlantacion309(origenDatosDao.getObjects(filtro)));
			tipoPlantacion.setNombreVO("VistaTipoPlantacion309VO");
			
		} catch (Exception e) {
			logger.error("Se ha producido un error en la VistaTipoPlantacion309",e);
		}
		return tipoPlantacion;
	}
	
	private ListOrgDatosVO getSistemaProteccion(DatagridPopupVO dataGridPopUpData) {
		ListOrgDatosVO sistProt = new ListOrgDatosVO();
		List<BigDecimal>  provincias  = new ArrayList<BigDecimal>();
		List<BigDecimal>  comarcas    = new ArrayList<BigDecimal>();
		List<BigDecimal>  terminos    = new ArrayList<BigDecimal>();
		List<BigDecimal>  cultivo     = new ArrayList<BigDecimal>(); 
		List<BigDecimal>  variedad    = new ArrayList<BigDecimal>();
		List<Character>   subterminos = new ArrayList<Character>();
		
		try{
			BigDecimal idClase = new BigDecimal(dataGridPopUpData.getClaseId());
			
			provincias  = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CODPROVINCIA, idClase);
			comarcas    = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CODCOMARCA, idClase);
			terminos    = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CODTERMINO, idClase);
			cultivo     = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CULTIVO_ID_CODCULTIVO, idClase);
			variedad    = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_VARIEDAD_ID_CODVARIEDAD, idClase);
			subterminos = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_SUBTERMINO, idClase);
			
			VistaSistemaProteccionFiltro filtro = new VistaSistemaProteccionFiltro();
			filtro.setListas(provincias, comarcas, terminos, cultivo, variedad, subterminos);
			filtro.setCodcultivo(new BigDecimal(dataGridPopUpData.getCultivo().trim()));
			filtro.setCodvariedad(new BigDecimal(dataGridPopUpData.getVariedad().trim()));
			filtro.setLineaSeguroId(new Long(dataGridPopUpData.getCodLinea()));
			filtro.setCodmodulo(dataGridPopUpData.getCodModulo().trim());

			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);
			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);
			sistProt.setCabeceras(cabeceras);
			sistProt.setNombreAtributo(atributos);
			sistProt.setNombreDescriptivo("Sistema proteccion");
			sistProt.setDatosVO(parseaSistProt(origenDatosDao.getObjects(filtro)));
			sistProt.setNombreVO("VistaSistemaProteccionVO");
		
		}
		catch(Exception exception){
			logger.error("Se ha producido un error en la VistaSistemaProteccion",exception);
		}

	    return sistProt;	
	}
	
	

	private ListOrgDatosVO getCicloCultivo(DatagridPopupVO dataGridPopUpData) {
		ListOrgDatosVO cicloCult = new ListOrgDatosVO();
		List<BigDecimal> cultivo       = new ArrayList<BigDecimal>(); 
		List<BigDecimal> variedad      = new ArrayList<BigDecimal>();
		List<BigDecimal> ciclosCultivo = new ArrayList<BigDecimal>();
		
		try{
			
			BigDecimal idClase = new BigDecimal(dataGridPopUpData.getClaseId());
			
			cultivo     = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CULTIVO_ID_CODCULTIVO, idClase);
			variedad    = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_VARIEDAD_ID_CODVARIEDAD, idClase);
			ciclosCultivo = origenDatosDao.getCampoClaseDetalle("claseDetalle.cicloCultivo.codciclocultivo", idClase);
		
			// Create and set filter
			VistaCicloCultivoFiltro filtro = new VistaCicloCultivoFiltro();
			filtro.setListas(cultivo, variedad, ciclosCultivo);
			filtro.setCodcomarca(new BigDecimal(dataGridPopUpData.getCodComarca().trim()));
			filtro.setCodtermino(new BigDecimal(dataGridPopUpData.getCodTermino().trim()));
			filtro.setCodprovincia(new BigDecimal(dataGridPopUpData.getCodProvincia().trim()));
			filtro.setCodcultivo(new BigDecimal(dataGridPopUpData.getCultivo().trim()));
			filtro.setCodvariedad(new BigDecimal(dataGridPopUpData.getVariedad().trim()));
			filtro.setLineaSeguroId(new Long(dataGridPopUpData.getCodLinea()));
			filtro.setCodmodulo(dataGridPopUpData.getCodModulo().trim());
			
			Date fecha = new Date();
			filtro.setFecha(fecha);
			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);
			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);
			cicloCult.setCabeceras(cabeceras);
			cicloCult.setNombreAtributo(atributos);
			cicloCult.setNombreDescriptivo("Ciclo cultivo");
			cicloCult.setDatosVO(parseaCicloCultivo(origenDatosDao.getObjects(filtro)));
			cicloCult.setNombreVO("VistaCicloCultivoVO");
		
		}
		catch(Exception exception){
			logger.error("Se ha producido un error en la VistaCicloCultivo",exception);
		}

	    return cicloCult;	
	}

	
    private ListOrgDatosVO getMaterialCubierta(DatagridPopupVO dataGridPopUpData) {
        ListOrgDatosVO materialCubierta = new ListOrgDatosVO();
		List<BigDecimal>  cultivo     = new ArrayList<BigDecimal>(); 
		List<BigDecimal>  variedad    = new ArrayList<BigDecimal>();
		
		try{

			BigDecimal idClase = new BigDecimal(dataGridPopUpData.getClaseId());
			
			cultivo     = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_CULTIVO_ID_CODCULTIVO, idClase);
			variedad    = origenDatosDao.getCampoClaseDetalle(OrigenDatosManager.CLASE_DETALLE_VARIEDAD_ID_CODVARIEDAD, idClase);
			
			VistaMaterialCubiertaFiltro filtro = new VistaMaterialCubiertaFiltro();
			filtro.setListas(cultivo, variedad);
			filtro.setCodcomarca(new BigDecimal(dataGridPopUpData.getCodComarca().trim()));
			filtro.setCodtermino(new BigDecimal(dataGridPopUpData.getCodTermino().trim()));
			filtro.setSubtermino(new Character(dataGridPopUpData.getCodSubTermino().charAt(0)));
			filtro.setCodprovincia(new BigDecimal(dataGridPopUpData.getCodProvincia().trim()));
			filtro.setCodcultivo(new BigDecimal(dataGridPopUpData.getCultivo().trim()));
			filtro.setCodvariedad(new BigDecimal(dataGridPopUpData.getVariedad().trim()));
			filtro.setLineaSeguroId(new Long(dataGridPopUpData.getCodLinea()));
			filtro.setCodmodulo(dataGridPopUpData.getCodModulo().trim());
			filtro.setCodtipocapital(new BigDecimal(dataGridPopUpData.getCodTipoCapital()));
//			Si se ha seleccionado una practica cultural, la incluimos en el filtro
			if(!dataGridPopUpData.getCodsistemaproteccion().equals("")){
				filtro.setCodsistemaproteccion(new BigDecimal(dataGridPopUpData.getCodsistemaproteccion().trim()));
			}

			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);

			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);

			materialCubierta.setCabeceras(cabeceras);
			materialCubierta.setNombreAtributo(atributos);
			materialCubierta.setNombreDescriptivo("Material cubierta");
			materialCubierta.setDatosVO(parseaMaterialCubierta(origenDatosDao.getObjects(filtro)));
			materialCubierta.setNombreVO("VistaMaterialCubiertaVO");	
	    }
		catch(Exception exception){
			logger.error("Se ha producido un error en la VistaMaterialCubierta",exception);
		}

        return materialCubierta;
    	
    }
    
    private ListOrgDatosVO getMaterialEstructura(DatagridPopupVO dataGridPopUpData) {
        ListOrgDatosVO materialEstructura = new ListOrgDatosVO();
		
		try{
			VistaMaterialEstructuraFiltro filtro = new VistaMaterialEstructuraFiltro();
			filtro.setCodcomarca(new BigDecimal(dataGridPopUpData.getCodComarca().trim()));
			filtro.setCodtermino(new BigDecimal(dataGridPopUpData.getCodTermino().trim()));
			filtro.setSubtermino(new Character(dataGridPopUpData.getCodSubTermino().charAt(0)));
			filtro.setCodprovincia(new BigDecimal(dataGridPopUpData.getCodProvincia().trim()));
			filtro.setCodcultivo(new BigDecimal(dataGridPopUpData.getCultivo().trim()));
			filtro.setCodvariedad(new BigDecimal(dataGridPopUpData.getVariedad().trim()));
			filtro.setLineaSeguroId(new Long(dataGridPopUpData.getCodLinea()));
			filtro.setCodmodulo(dataGridPopUpData.getCodModulo().trim());
			filtro.setCodtipocapital(new BigDecimal(dataGridPopUpData.getCodTipoCapital()));

			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);

			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);

			materialEstructura.setCabeceras(cabeceras);
			materialEstructura.setNombreAtributo(atributos);
			materialEstructura.setNombreDescriptivo("Material estructura");
			materialEstructura.setDatosVO(parseaMaterialEstructura(origenDatosDao.getObjects(filtro)));
			materialEstructura.setNombreVO("VistaMaterialEstructuraVO");	
	    }
		catch(Exception exception){
			logger.error("Se ha producido un error en la VistaMaterialEstructura",exception);
		}

        return materialEstructura;
    	
    }

    private ListOrgDatosVO getTipoInstalacion(DatagridPopupVO dataGridPopUpData) {
        ListOrgDatosVO tipoInstalacion = new ListOrgDatosVO();
		
		try{
			
			VistaTipoInstalacionFiltro filtro = new VistaTipoInstalacionFiltro();
			filtro.setCodcomarca(new BigDecimal(dataGridPopUpData.getCodComarca().trim()));
			filtro.setCodtermino(new BigDecimal(dataGridPopUpData.getCodTermino().trim()));
			filtro.setSubtermino(new Character(dataGridPopUpData.getCodSubTermino().charAt(0)));
			filtro.setCodprovincia(new BigDecimal(dataGridPopUpData.getCodProvincia().trim()));
			filtro.setCodcultivo(new BigDecimal(dataGridPopUpData.getCultivo().trim()));
			filtro.setCodvariedad(new BigDecimal(dataGridPopUpData.getVariedad().trim()));
			filtro.setLineaSeguroId(new Long(dataGridPopUpData.getCodLinea()));
			filtro.setCodmodulo(dataGridPopUpData.getCodModulo().trim());
			filtro.setCodtipocapital(new BigDecimal(dataGridPopUpData.getCodTipoCapital()));
			
			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);
			cabeceras.add(OrigenDatosManager.DESCRIPCION);

			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);
			atributos.add(OrigenDatosManager.DESCRIPCION2);


			tipoInstalacion.setCabeceras(cabeceras);
			tipoInstalacion.setNombreAtributo(atributos);
			tipoInstalacion.setNombreDescriptivo("Tipo instalacion");
			tipoInstalacion.setDatosVO(parseaTipoInstalacion(origenDatosDao.getObjects(filtro)));
			tipoInstalacion.setNombreVO("VistaTipoInstalacionVO");	
	
			
		}
		catch(Exception exception){
			logger.error("Se ha producido un error en la VistaTipoInstalacion",exception);
		}

	    return tipoInstalacion;
    }
    
    private ListOrgDatosVO getCertificadoInstalacion(DatagridPopupVO dataGridPopUpData) {
        ListOrgDatosVO codigoCertificado = new ListOrgDatosVO();
		
		try{
			
			VistaCertificadoEstructuraFiltro filtro = new VistaCertificadoEstructuraFiltro();
			filtro.setCodcultivo(new BigDecimal(dataGridPopUpData.getCultivo().trim()));
			filtro.setCodvariedad(new BigDecimal(dataGridPopUpData.getVariedad().trim()));
			filtro.setLineaseguroid(new Long(dataGridPopUpData.getCodLinea()));
			filtro.setCodmodulo(dataGridPopUpData.getCodModulo().trim());
			filtro.setCodtipocapital(new BigDecimal(dataGridPopUpData.getCodTipoCapital()));
			//filtro.setCodcertificadoinstal(new BigDecimal(dataGridPopUpData.getCodTipoCapital()));

			List<String> cabeceras = new ArrayList<String>();
			cabeceras.add(OrigenDatosManager.CODIGO);cabeceras.add(OrigenDatosManager.DESCRIPCION);
			List<String> atributos = new ArrayList<String>();
			atributos.add(OrigenDatosManager.CODIGO2);atributos.add(OrigenDatosManager.DESCRIPCION2);

			codigoCertificado.setCabeceras(cabeceras);
			codigoCertificado.setNombreAtributo(atributos);
			codigoCertificado.setNombreDescriptivo("Codigo certificado");
			codigoCertificado.setDatosVO(parseaCodigoCertificacion(origenDatosDao.getObjects(filtro)));
			codigoCertificado.setNombreVO("VistaTipoInstalacionVO");	
	
			
		}
		catch(Exception exception){
			logger.error("Se ha producido un error en la VistaTipoInstalacion",exception);
		}

	    return codigoCertificado;
    }

	public void setOrigenDatosDao(IOrigenDatosDao origenDatosDao) {
		this.origenDatosDao = origenDatosDao;
	}
	
	/**
	 *    FIN ORIGENES DE DATOS
	 */
}