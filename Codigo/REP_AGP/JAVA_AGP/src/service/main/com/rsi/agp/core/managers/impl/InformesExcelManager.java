package com.rsi.agp.core.managers.impl;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.xmlbeans.XmlException;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Node;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.DateUtil;
import com.rsi.agp.core.util.ExcelUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.admin.IColectivoDao;
import com.rsi.agp.dao.models.admin.ISubentidadMediadoraDao;
import com.rsi.agp.dao.models.comisiones.IInformesExcelDao;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.EntidadMediadora;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.comisiones.Cierre;
import com.rsi.agp.dao.tables.comisiones.EntidadesOperadoresInforme;
import com.rsi.agp.dao.tables.comisiones.InformeMediadores;
import com.rsi.agp.dao.tables.comisiones.InformeMediadoresMeses;
import com.rsi.agp.dao.tables.comisiones.InformeMediadoresSaldos;
import com.rsi.agp.dao.tables.comisiones.RgaComisiones;
import com.rsi.agp.dao.tables.comisiones.impagados.ReciboImpagado;
import com.rsi.agp.dao.tables.comisiones.unificado.RgaUnifMediadores;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeColaboradores2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeComsFacturacion;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeComsFamLinEnt;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeComsImpagados2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeComsRGA2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeCorredores2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeDetMediador2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeEntidades2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeEntidadesOperadores2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeTotMediador2015;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.commons.TerminoId;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.BonificacionRecargo;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.cpl.VariedadId;
import com.rsi.agp.dao.tables.poliza.CapAsegRelModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteParcela2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteParcela2015BonifRec;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteParcela2015Subvencion;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;

import es.agroseguro.contratacion.ObjetosAsegurados;


@SuppressWarnings("deprecation")
public class InformesExcelManager implements IManager {

	private static final Log logger = LogFactory.getLog(InformesExcelManager.class);
	
	private static final String ERROR_LIST_COM_PEN = "Se ha producido un error al generar la lista de comisiones pendientes";
	private static final String ERROR_LIST_COM_ENT = "Se ha producido un error al generar la lista de comisiones a entidades";
	private static final String RUTA_TEMP_INF_COM = "ruta.temporal.informes.comisiones";
	private static final String LINEA = "LINEA";
	private static final String COLECTIVO = "COLECTIVO";
	private static final String TOTAL = "TOTAL";
	private static final String PERMISO_CARPETA_TEMP = "No tiene permiso para crear la carpeta temporal:";
	private static final String LOGGER_ERROR_GUARDAR_EXCEL = "Se ha producido un error al guardar el fichero excel generado:";
	private static final String ERROR_GUARDAR_EXCEL = "Se ha producido un error al guardar el fichero excel generado";
	private static final String ERROR_GUARDAR_EXCEL2 = "Se ha producido un error al guardar el fichero Excel generado";
	private static final String ENT_MED = "ENT MED";
	private static final String SUBENT_MED = "SUBENT MED";
	private static final String NOMBRE_MEDIADORA = "NOMBRE E-S MEDIADORA";
	
	
	// TITULOS CABECERAS COLUMNAS
	private static final String TITULO_COL_ENTIDAD_MEDIADORA = "Entidad mediadora";
	private static final String TITULO_COL_MES = "Mes";
	private static final String TITULO_COL_IMPORTE_A_LIQUIDAR = "Importe a liquidar";
	private static final String TITULO_COL_RETENCION_IRPF = "Retencion IRPF";
	private static final String TITULO_COL_IMPORTE = "Importe";
	private static final String TITULO_COL_PCN = "PCN";
	private static final String TITULO_COL_EMAIL2 = "Email2";
	private static final String TITULO_COL_EMAIL = "Email";
	private static final String TITULO_COL_NOMBRE_DESTINATARIO = "Nombre destinatario";
	private static final String TITULO_COL_IBAN = "IBAN";
	private static final String TITULO_COL_ABON_ADQ = "Abon/Adq";

	
	private IInformesExcelDao informesExcelDao;
	private ISubentidadMediadoraDao subentidadMediadoraDao;
	private ILineaDao lineaDao;
	private IColectivoDao colectivoDao;
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	public List<RgaComisiones> getListaTotalesMediador(final Long idCierre) throws BusinessException {
		logger.debug("init - getListaComisionesPendientes");
		List<RgaComisiones> comisiones = null;		
		try {
			
			comisiones = informesExcelDao.listTotalesMediador(idCierre);	
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al generar la lista de comisiones pendientes: " + dao.getMessage());
			throw new BusinessException (ERROR_LIST_COM_PEN , dao);
		}
		logger.debug("end - getListaComisionesPendientes");
		return comisiones;
	}
	
	public List<InformeTotMediador2015> getListaTotalesMediador2015() throws BusinessException {
		logger.debug("init - getListaTotalesMediador2015");
		List<InformeTotMediador2015> comisiones = null;		
		try {
			
			comisiones = informesExcelDao.listTotalesMediador2015();	
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al generar la lista de TotalesMediador2015:" + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al generar la lista de TotalesMediador2015" , dao);
		}
		logger.debug("end - getListaTotalesMediador2015");
		return comisiones;
	}
	
	public List<InformeMediadores> getListaMediadores(final Date fechaCierre) throws BusinessException {
		logger.debug("init - getListaInformeMediadores");
		List<InformeMediadores> informeMediadores = null;		
		try {
			
			informeMediadores = informesExcelDao.listMediadores(fechaCierre);	
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al generar la lista de comisiones  pendientes:" + dao.getMessage());
			throw new BusinessException (ERROR_LIST_COM_PEN , dao);
		}
		logger.debug("end - getListaInformeMediadores");
		return informeMediadores;
	}
	
	
	/*
	 * Recogemos la lista de Mediadores 2015 
	 */
	public List<RgaUnifMediadores> getListaMediadores2015(final Date fechaCierre, boolean segGen) throws BusinessException {
		logger.debug("init - getListaMediadores2015");
		List<RgaUnifMediadores> informeMediadores2015 = null;		
		try {
			
			informeMediadores2015 = informesExcelDao.listMediadores2015(fechaCierre , segGen);	
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al generar la lista de MediadoresSegGenerales2015:" + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al generar la lista de MediadoresSegGenerales2015" , dao);
		}
		logger.debug("end - getListaMediadores2015");
		return informeMediadores2015;
	}
	
	
	/**
	 * 
	 * @param cierre
	 * @return
	 */
	public List<RgaComisiones> getListaComisionesEntidades(final Long idCierre) throws BusinessException {
		logger.debug("init - getListaComisionesEntidades");
		List<RgaComisiones> comisionesEntidades = null;		
		try {
			comisionesEntidades = informesExcelDao.listComisionesEntidades(idCierre);
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al generar la lista de comisiones a entidades: " + dao.getMessage());
			throw new BusinessException (ERROR_LIST_COM_ENT , dao);
		}
		logger.debug("end - getListaComisionesEntidades ");
		return comisionesEntidades;
		
	}
	
	/**
	 * 
	 * @param cierre 2015
	 * @return
	 */
	public List<InformeEntidades2015> getListaComisionesEntidades2015() throws BusinessException {
		logger.debug("init - getListaComisionesEntidades2015");
		List<InformeEntidades2015> comisionesEntidades2015 = null;		
		try {
			comisionesEntidades2015 = informesExcelDao.listComisionesEntidades2015();
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al generar la lista de comisiones a entidades2015:" + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al generar la lista de comisiones a entidades2015" , dao);
		}
		logger.debug("end - getListaComisionesEntidades2015");
		return comisionesEntidades2015;
		
	}
	
	public HashMap <String, String> getSubentidadesMediadoras() throws BusinessException{
		logger.debug("init - getListSubentidadMediadora");
		List<SubentidadMediadora> subentidadMediadora = null;	
		HashMap<String, String> codDesc = new HashMap<String, String>();
		try {
			subentidadMediadora = subentidadMediadoraDao.getAll();
			String desc ="";
			for (int i = 0; i < subentidadMediadora.size(); i++) {
				String cod = subentidadMediadora.get(i).getId().getCodentidad()+"-" +subentidadMediadora.get(i).getId().getCodsubentidad();
				desc = StringUtils.nullToString(subentidadMediadora.get(i).getNomSubentidadCompleto());
				codDesc.put(cod, desc);
			}
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al generar la lista de comisiones a entidades :" + dao.getMessage());
			throw new BusinessException (ERROR_LIST_COM_ENT , dao);
		}
		logger.debug("end -  getListaComisionesEntidades");	
		return codDesc;
	}
	
	
	public HashMap <String, SubentidadMediadora> getSubentidadesMediadorasObject() throws BusinessException{
		logger.debug("init - getSubentidadesMediadorasObject");
		List<SubentidadMediadora> subentidadMediadora = null;	
		HashMap<String, SubentidadMediadora> mapSubEnt = new HashMap<String, SubentidadMediadora>();
		try {
			subentidadMediadora = subentidadMediadoraDao.getAll();
			
			for (int i = 0; i < subentidadMediadora.size(); i++) {
				String cod = subentidadMediadora.get(i).getId().getCodentidad()+"-" +subentidadMediadora.get(i).getId().getCodsubentidad();
				mapSubEnt.put(cod, subentidadMediadora.get(i));
			}
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al generar la lista de SubentidadesMediadoras:" + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al generar la lista de SubentidadesMediadoras" , dao);
		}
		logger.debug("end - getSubentidadesMediadorasObject");	
		return mapSubEnt;
	}
	
	public HashMap <String, String> getEntidadesMediadoras() throws BusinessException{
		logger.debug("init - getListSubentidadMediadora");
		List<EntidadMediadora> entidadMediadora = null;	
		HashMap<String, String> codDesc = new HashMap<String, String>();
		try {
			entidadMediadora = subentidadMediadoraDao.getAllEntMediadoras();
			
			for (int i = 0; i < entidadMediadora.size(); i++) {
				String cod = entidadMediadora.get(i).getCodentidad().toString();
				String desc = entidadMediadora.get(i).getNomentidad();
				codDesc.put(cod, desc);
			}
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al generar la lista de comisiones a  entidades:" + dao.getMessage());
			throw new BusinessException (ERROR_LIST_COM_ENT , dao);
		}
		logger.debug("end  - getListaComisionesEntidades");	
		return codDesc;
	}
	
	public HashMap<String,String> getLineas() throws BusinessException{
		logger.debug("init - getLineas");
		List<Linea> lineas = null;	
		HashMap<String, String> codDescLineas = new HashMap<String, String>();
		try {
			lineas = lineaDao.getAll();
			
			for (int i = 0; i < lineas.size(); i++) {
				String cod = lineas.get(i).getCodlinea().toString();
				String desc = lineas.get(i).getNomlinea();
				codDescLineas.put(cod, desc);
			}
			logger.debug("end - getLineas");	
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al generar la lista de comisiones  a entidades:" + dao.getMessage());
			throw new BusinessException (ERROR_LIST_COM_ENT , dao);
		}
		logger.debug("end - getListaComisionesEntidades");	
		return codDescLineas;
	}
	
	public List<RgaComisiones> getListaInformesMediador(final Long idCierre) throws BusinessException {
		logger.debug("init - getListaInformesMediador");
		List<RgaComisiones> listDetalleMediador = null;		
		try {
			
			listDetalleMediador = informesExcelDao.listDetalleMediador(idCierre);			
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al generar la lista de  comisiones pendientes:" + dao.getMessage());
			throw new BusinessException (ERROR_LIST_COM_PEN , dao);
		}
		logger.debug("end - getListaInformesMediador");
		return listDetalleMediador;
	}
	
	public List<InformeDetMediador2015> getListaInformesMediador2015() throws BusinessException {
		logger.debug("init - getListaInformesMediador2015");
		List<InformeDetMediador2015> listDetalleMediador2015 = null;		
		try {
			
			listDetalleMediador2015 = informesExcelDao.listDetalleMediador2015();			
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al generar la lista de DetalleMediador2015:" + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al generar la lista de DetalleMediador2015" , dao);
		}
		logger.debug("end - getListaInformesMediador2015");
		return listDetalleMediador2015;
	}
	
	
	public List<ReciboImpagado> getListaComisionesImpagados(final Cierre cierre) throws BusinessException {
		logger.debug("init - getListaComisionesImpagados");
		List<ReciboImpagado> listComisionesImpagados = null;		
		try {
			
			listComisionesImpagados = informesExcelDao.listImpagados(cierre);			
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al generar la lista  de comisiones pendientes:" + dao.getMessage());
			throw new BusinessException (ERROR_LIST_COM_PEN , dao);
		}
		logger.debug("end - getListaComisionesImpagados");
		return listComisionesImpagados;
	}
	
	public List<InformeComsImpagados2015> getListaComisionesImpagados2015(final Long idCierre) throws BusinessException {
		logger.debug("init - getListaComisionesImpagados2015");
		List<InformeComsImpagados2015> listComisionesImpagados2015 = null;		
		try {
			
			listComisionesImpagados2015 = informesExcelDao.listImpagados2015(idCierre);			
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al generar la lista de ComisionesImpagados2015:" + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al generar la lista de ComisionesImpagados2015" , dao);
		}
		logger.debug("end - getListaComisionesImpagados2015");
		return listComisionesImpagados2015;
	}
	
	public List<InformeComsRGA2015> getListaComsRGA2015() throws BusinessException {
		logger.debug("init - getListaComsRGA2015");
		List<InformeComsRGA2015> listComsRGA2015 = null;		
		try {
			
			listComsRGA2015 = informesExcelDao.listComsRGA2015();			
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al generar la lista de ComsRGA2015:" + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al generar la lista de ComsRGA2015" , dao);
		}
		logger.debug("end - getListaComsRGA2015");
		return listComsRGA2015;
	}
	
	
	
	public List<Colectivo> getColectivos() throws BusinessException{
		logger.debug("init - getColectivos");
		List<Colectivo> listcolectivos = null;		
		try {
			
			listcolectivos = colectivoDao.getAll();		
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al generar la  lista de comisiones pendientes:" + dao.getMessage());
			throw new BusinessException (ERROR_LIST_COM_PEN , dao);
		}
		logger.debug("end - getListaInformesMediador");
		return listcolectivos;
	}

	public List<InformeEntidadesOperadores2015> getListaEntidadesOperadores2015() throws BusinessException {
		logger.debug("init - getListaEntidadesOperadores2015");
		List<InformeEntidadesOperadores2015> listEntidadesOperadores2015= null;		
		try {
			
			listEntidadesOperadores2015 = informesExcelDao.listEntidadesOperadores2015();			
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al generar la lista de EntidadesOperadores2015:" + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al generar la lista de EntidadesOperadores2015" , dao);
		}
		logger.debug("end - getListaEntidadesOperadores2015");
		return listEntidadesOperadores2015;		
	}
	
	private String getMesByCierre(final Date fechaCierre){
		logger.debug("init - getMesByCierre");
		GregorianCalendar fecha = new GregorianCalendar();
		fecha.setTime(fechaCierre);
		int mes = fecha.get(Calendar.MONTH);
		int year = fecha.get(Calendar.YEAR);
		String resultado = "";
		
		switch (mes+1){
			case 1:
				resultado = "Enero";
				break;
			case 2:
				resultado = "Febrero";
				break;
			case 3:
				resultado = "Marzo";
				break;
			case 4:
				resultado = "Abril";
				break;
			case 5:
				resultado = "Mayo";
				break;
			case 6:
				resultado = "Junio";
				break;
			case 7:
				resultado = "Julio";
				break;
			case 8:
				resultado = "Agosto";
				break;
			case 9:
				resultado = "Septiembre";
				break;
			case 10:
				resultado = "Octubre";
				break;
			case 11:
				resultado = "Noviembre";
				break;
			case 12:
				resultado = "Diciembre";
				break;	
			default:
				break;
		}
				
		resultado = resultado.toUpperCase() + " " + year;		
		
		logger.debug("end - getMesByCierre");
		return resultado;
	}
	
	public void generarInformeComisionesEntidades(List<RgaComisiones> listInformeEntidades, final Long idCierre, final Date fechaCierre,
			HashMap<String,String> subEntMed, HashMap<String,String> lineas,Usuario usuario) throws BusinessException {
		logger.debug("init - generarInformeComisionesEntidades");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaCierre);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		StringBuilder nombreFichero = new StringBuilder("InformeEntidades_");
		nombreFichero.append(year);
		nombreFichero.append(month < 10 ? "0" + month : month);
		nombreFichero.append(".xls");
		
		BigDecimal sumaTTCaja = new BigDecimal(0);
		BigDecimal sumaTramCal = new BigDecimal(0);
		BigDecimal sumaAgente = new BigDecimal(0);
		BigDecimal sumaPendiente = new BigDecimal(0);
		try {		
			
			HSSFWorkbook wb          = new HSSFWorkbook();
			// ruta temporal
			
			FileOutputStream fileOut = new FileOutputStream(
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString());
		
			HSSFSheet sheet = wb.createSheet();
			sheet = wb.getSheetAt(0);

			HSSFCell cell;	
			
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));
			
			HSSFCellStyle estiloTitulo = ExcelUtils.getEstiloTitulo(wb);
			HSSFRow titulo = sheet.createRow(0);		
			
			cell = titulo.createCell(0);		
			
			cell.setCellStyle(estiloTitulo);
			cell.setCellValue(new HSSFRichTextString(getMesByCierre(fechaCierre)));				
			
			//Se insertan las cabeceras
			HSSFRow cabecera = sheet.createRow(1);
			HSSFCellStyle estiloCabecera = ExcelUtils.getEstiloCabecera(wb);	
		
			cell = cabecera.createCell(0);
			cell.setCellValue(new HSSFRichTextString("ENTIDAD MEDIADORA - SUBENTIDAD MEDIADORA"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(1);
			cell.setCellValue(new HSSFRichTextString("FASE"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(2);
			cell.setCellValue(new HSSFRichTextString("PLAN"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(3);
			cell.setCellValue(new HSSFRichTextString(LINEA));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(4);
			cell.setCellValue(new HSSFRichTextString(COLECTIVO));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(5);
			cell.setCellValue(new HSSFRichTextString("PRIMA NETA BONIF./RECARGO"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(6);
			cell.setCellValue(new HSSFRichTextString("G.G.ENTIDAD"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(7);
			cell.setCellValue(new HSSFRichTextString("G.G SUBENTIDAD"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(8);
			cell.setCellValue(new HSSFRichTextString("COMISION CULTIVO ENT."));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(9);
			cell.setCellValue(new HSSFRichTextString("COMISION CULTIVO SUBENT."));
			cell.setCellStyle(estiloCabecera);	
			
			cell = cabecera.createCell(10);
			cell.setCellValue(new HSSFRichTextString("PENDIENTE"));
			cell.setCellStyle(estiloCabecera);	
			
			cell = cabecera.createCell(11);
			cell.setCellValue(new HSSFRichTextString("LIQUIDACION"));
			cell.setCellStyle(estiloCabecera);	
			
			cell = cabecera.createCell(12);
			cell.setCellValue(new HSSFRichTextString("TT. CAJA"));
			cell.setCellStyle(estiloCabecera);	
			
			cell = cabecera.createCell(13);
			cell.setCellValue(new HSSFRichTextString("TT. AGENTE"));
			cell.setCellStyle(estiloCabecera);	
			
			cell = cabecera.createCell(14);
			cell.setCellValue(new HSSFRichTextString("TRAMITACION & CALIDAD"));
			cell.setCellStyle(estiloCabecera);	
			
			
			HSSFCellStyle estiloFila = ExcelUtils.getEstiloFila(wb);
			HSSFCellStyle estiloFilaNumero = ExcelUtils.getEstiloFilaNumero(wb);
			
			int i=0;
			for ( i=0; i<listInformeEntidades.size(); i++){
				HSSFRow row     = sheet.createRow(i+2); 
				
								
				cell = row.createCell(0);
				cell.setCellStyle(estiloFila);
				//C贸digo entidad mediadiora- codigo subentidad mediadora
				if ((listInformeEntidades.get(i).getCodentmed() != null) && (listInformeEntidades.get(i).getCodsubmed() != null)){					
					String cod = listInformeEntidades.get(i).getCodentmed().intValue()
									+"-"+listInformeEntidades.get(i).getCodsubmed().intValue();
					String desc = subEntMed.get(cod);
					desc = (desc != null) ? desc.trim(): "";
					cell.setCellValue(new HSSFRichTextString(cod+" "+desc));					
				}
				
				//fase
				cell = row.createCell(1);
				cell.setCellStyle(estiloFila);
				if ((listInformeEntidades.get(i).getNumfas() != null)){					
					cell.setCellValue(new HSSFRichTextString(listInformeEntidades.get(i).getNumfas()));					
				}
				
				//C贸digo plan
				cell = row.createCell(2);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (listInformeEntidades.get(i).getCodpln() != null){								
					cell.setCellValue(listInformeEntidades.get(i).getCodpln().doubleValue());					
				}
				
				//L铆nea
				cell = row.createCell(3);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (listInformeEntidades.get(i).getCodlin() != null){					
					String desc = lineas.get(listInformeEntidades.get(i).getCodlin().toString());
					desc = (desc != null) ? desc.trim(): "";
					cell.setCellValue(listInformeEntidades.get(i).getCodlin().intValue()+" "+desc);					
				}
				
				//Referencia colectivo
				cell = row.createCell(4);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (listInformeEntidades.get(i).getCodcol() != null){					
					cell.setCellValue(Integer.parseInt(listInformeEntidades.get(i).getCodcol()));										
				}
				
				//primsum
				cell = row.createCell(5);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				if (listInformeEntidades.get(i).getPrisum() != null){									
					cell.setCellValue(listInformeEntidades.get(i).getPrisum().doubleValue());				
				}
				
				//g.g entidad
				cell = row.createCell(6);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFilaNumero);
				if (listInformeEntidades.get(i).getGasentsum()!= null){								
					cell.setCellValue(listInformeEntidades.get(i).getGasentsum().doubleValue());					
				}
				
				//g.g subentidad
				cell = row.createCell(7);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				if (listInformeEntidades.get(i).getGassubsum() != null){									
					cell.setCellValue(listInformeEntidades.get(i).getGassubsum().doubleValue());					
				}
				
				//comision cultivo entidad
				cell = row.createCell(8);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				if (listInformeEntidades.get(i).getComentsum() != null){									
					cell.setCellValue(listInformeEntidades.get(i).getComentsum().doubleValue());					
				}
				
				//comision cultivo subentidad
				cell = row.createCell(9);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				if (listInformeEntidades.get(i).getComsubsum() != null){									
					cell.setCellValue(listInformeEntidades.get(i).getComsubsum().doubleValue());					
				}	
				//Pendiente
				cell = row.createCell(10);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				if (listInformeEntidades.get(i).getGaspen()!= null){
					
					cell.setCellValue(listInformeEntidades.get(i).getGaspen().doubleValue());	
					sumaPendiente =sumaPendiente.add(new BigDecimal(listInformeEntidades.get(i).getGaspen().doubleValue()));
				}
				
				BigDecimal p1 = StringUtils.nullToZero(listInformeEntidades.get(i).getGasentsum());
				BigDecimal p2 = StringUtils.nullToZero(listInformeEntidades.get(i).getGassubsum());
				BigDecimal p3 = StringUtils.nullToZero(listInformeEntidades.get(i).getComentsum());
				BigDecimal p4 = StringUtils.nullToZero(listInformeEntidades.get(i).getComsubsum());
				BigDecimal p5 = StringUtils.nullToZero(listInformeEntidades.get(i).getGaspen());
				
				//Liquidacion
				cell = row.createCell(11);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				
				BigDecimal totLiquidacion = p1.add(p2).add(p3).add(p4).subtract(p5);
				cell.setCellValue(totLiquidacion.doubleValue());					
				
				//tt. caja
				cell = row.createCell(12);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				
				//si la subentidad es 0 se resta el pendiente al tt. caja
				if (listInformeEntidades.get(i).getCodsubmed().compareTo(new BigDecimal(0))==0){ 
					
					BigDecimal totCaja = p1.add(p3).subtract(p5);
					cell.setCellValue(totCaja.doubleValue());
					sumaTTCaja = sumaTTCaja.add(totCaja);
				}else{
					BigDecimal totCaja = p1.add(p3);
					cell.setCellValue(totCaja.doubleValue());
					sumaTTCaja = sumaTTCaja.add(totCaja);
				}
				
				//tt. agente
				cell = row.createCell(13);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				//Si la subentidad es distinto de 0 se resta el pendiente al agente
				if (listInformeEntidades.get(i).getCodsubmed().compareTo(new BigDecimal(0))==0){ 
					
					BigDecimal totAgente = p2.add(p4);
					cell.setCellValue(totAgente.doubleValue());
					sumaAgente = sumaAgente.add(totAgente);
				}else{
					BigDecimal totAgente = p2.add(p4).subtract(p5);
					cell.setCellValue(totAgente.doubleValue());
					sumaAgente = sumaAgente.add(totAgente);
				}
				
				
				
				//tramitacion & calidad
				cell = row.createCell(14);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				BigDecimal rgTramitacion = StringUtils.nullToZero(listInformeEntidades.get(i).getImptra());
				BigDecimal rgCalidad =StringUtils.nullToZero(listInformeEntidades.get(i).getImpcal());
				//cell.setCellValue(rgTramitacion.add(rgCalidad).doubleValue());
				BigDecimal restaCalTram = rgTramitacion.subtract(rgCalidad);
				cell.setCellValue(restaCalTram.doubleValue());
				sumaTramCal = sumaTramCal.add(restaCalTram);
				
				
				
			}
			if (i > 0){
				HSSFRow row = sheet.createRow(i+2);
				HSSFCellStyle estiloTotales = ExcelUtils.getEstiloFilaTotales(wb);
				HSSFCellStyle estiloFilaTotalesNumero = ExcelUtils.getEstiloFilaTotalesNumero(wb);
				
				cell = row.createCell(9);
				cell.setCellValue(new HSSFRichTextString(TOTAL));
				cell.setCellStyle(estiloTotales);
				
				cell = row.createCell(10);
				cell.setCellValue(sumaPendiente.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
				cell = row.createCell(11);
				cell.setCellStyle(estiloTotales);
				
				cell = row.createCell(12);
				cell.setCellValue(sumaTTCaja.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
				cell = row.createCell(13);
				cell.setCellValue(sumaAgente.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
				cell = row.createCell(14);
				cell.setCellValue(sumaTramCal.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
			}			
			
			ExcelUtils.autoajustarColumnas(sheet, cell.getColumnIndex() + 1);
			
			wb.write(fileOut);
			
			informesExcelDao.saveFicheroExcelCierre(nombreFichero.toString(),
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString(), idCierre, usuario);	
			
			fileOut.close();
		} catch (FileNotFoundException e) {
			logger.error(PERMISO_CARPETA_TEMP + e.getMessage());
			throw new BusinessException (PERMISO_CARPETA_TEMP , e);
		} catch (IOException e) {
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + e.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , e);
		} catch (DAOException dao){
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + dao.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , dao);
		}			
		
	}	
	
	/*
	 *  Generaci髇 del informe de Comisiones de Entidades 2015+
	 */
	public void generarInformeComisionesEntidades2015(List<InformeEntidades2015> lstInfEnt2015, final Long idCierre, final Date fechaCierre,
			HashMap<String,String> lineas,Usuario usuario) throws BusinessException {
		logger.debug("init - generarInformeEntidades2015");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaCierre);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		StringBuilder nombreFichero = new StringBuilder("InformeEntidades2015+_");
		nombreFichero.append(year);
		nombreFichero.append(month < 10 ? "0" + month : month);
		nombreFichero.append(".xls");
		
		try {		
			BigDecimal sumaPrima   = new BigDecimal(0);
			BigDecimal sumaComEnt  = new BigDecimal(0);
			BigDecimal sumaComE_S  = new BigDecimal(0);
			BigDecimal sumaPendEnt = new BigDecimal(0);
			BigDecimal sumaPendE_S = new BigDecimal(0);
			BigDecimal sumaTT_E    = new BigDecimal(0);
			BigDecimal sumaTT_E_S  = new BigDecimal(0);
			BigDecimal sumaLiquid  = new BigDecimal(0);
			HSSFWorkbook wb        = new HSSFWorkbook();
			// ruta temporal
			
			FileOutputStream fileOut = new FileOutputStream(
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString());
		
			HSSFSheet sheet = wb.createSheet();
			sheet = wb.getSheetAt(0);

			HSSFCell cell;	
			
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 15));
			
			HSSFCellStyle estiloTitulo = ExcelUtils.getEstiloTitulo(wb);
			HSSFRow titulo = sheet.createRow(0);		
			
			cell = titulo.createCell(0);		
			
			cell.setCellStyle(estiloTitulo);
			cell.setCellValue(new HSSFRichTextString(getMesByCierre(fechaCierre)));				
			
			//Se insertan las cabeceras
			HSSFRow cabecera = sheet.createRow(1);
			HSSFCellStyle estiloCabecera = ExcelUtils.getEstiloCabecera(wb);	
		
			cell = cabecera.createCell(0);
			cell.setCellValue(new HSSFRichTextString(ENT_MED));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(1);
			cell.setCellValue(new HSSFRichTextString(SUBENT_MED));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(2);
			cell.setCellValue(new HSSFRichTextString(NOMBRE_MEDIADORA));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(3);
			cell.setCellValue(new HSSFRichTextString("FASE"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(4);
			cell.setCellValue(new HSSFRichTextString("PLAN"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(5);
			cell.setCellValue(new HSSFRichTextString("COD. LINEA"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(6);
			cell.setCellValue(new HSSFRichTextString(LINEA));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(7);
			cell.setCellValue(new HSSFRichTextString(COLECTIVO));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(8);
			cell.setCellValue(new HSSFRichTextString("PRIMA COMERCIAL NETA"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(9);
			cell.setCellValue(new HSSFRichTextString("COMISION ENT."));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(10);
			cell.setCellValue(new HSSFRichTextString("COMISION E-S MED."));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(11);
			cell.setCellValue(new HSSFRichTextString("PDTE. ENT."));
			cell.setCellStyle(estiloCabecera);	
			
			cell = cabecera.createCell(12);
			cell.setCellValue(new HSSFRichTextString("PDTE E-S MED."));
			cell.setCellStyle(estiloCabecera);	
			
			cell = cabecera.createCell(13);
			cell.setCellValue(new HSSFRichTextString("TT. ENT."));
			cell.setCellStyle(estiloCabecera);	
			
			cell = cabecera.createCell(14);
			cell.setCellValue(new HSSFRichTextString("TT. E-S MED."));
			cell.setCellStyle(estiloCabecera);	
			
			cell = cabecera.createCell(15);
			cell.setCellValue(new HSSFRichTextString("LIQUIDACION"));
			cell.setCellStyle(estiloCabecera);	
			

			
			HSSFCellStyle estiloFila = ExcelUtils.getEstiloFila(wb);
			HSSFCellStyle estiloFilaNumero = ExcelUtils.getEstiloFilaNumero(wb);
			
			int i=0;
			for ( i=0; i<lstInfEnt2015.size(); i++){
				HSSFRow row     = sheet.createRow(i+2); 		
								
				cell = row.createCell(0);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				//Codigo entidad mediadiora
				if ((lstInfEnt2015.get(i).getEntidad() != null)){					
					cell.setCellValue(lstInfEnt2015.get(i).getEntidad().doubleValue());					
				}
								
				cell = row.createCell(1);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				//Codigo Subentidad mediadiora
				if ((lstInfEnt2015.get(i).getSubentidad() != null)){					
					cell.setCellValue(lstInfEnt2015.get(i).getSubentidad().doubleValue());						
				}			
				
				//Nombre E-S Mediadora
				cell = row.createCell(2);
				cell.setCellStyle(estiloFila);
				if ((lstInfEnt2015.get(i).getNomentidad() != null)){					
					cell.setCellValue(new HSSFRichTextString(lstInfEnt2015.get(i).getNomentidad()));					
				}
							
				//fase
				cell = row.createCell(3);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if ((lstInfEnt2015.get(i).getFase() != null)){					
					cell.setCellValue(lstInfEnt2015.get(i).getFase().doubleValue());					
				}
				
				//plan
				cell = row.createCell(4);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (lstInfEnt2015.get(i).getPlan() != null){								
					cell.setCellValue(lstInfEnt2015.get(i).getPlan().doubleValue());					
				}

				//codLinea
				cell = row.createCell(5);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (lstInfEnt2015.get(i).getLinea() != null){						
					cell.setCellValue(lstInfEnt2015.get(i).getLinea().doubleValue());					
				}
				
				//NombreLinea
				cell = row.createCell(6);
				cell.setCellStyle(estiloFila);
				if (lstInfEnt2015.get(i).getNomlinea() != null){					
					String descLinea = lineas.get(lstInfEnt2015.get(i).getLinea().toString());
					descLinea = (descLinea != null) ? descLinea.trim(): "";
					cell.setCellValue(descLinea);					
				}
				
				//Referencia colectivo
				cell = row.createCell(7);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (lstInfEnt2015.get(i).getReferencia() != null){					
					cell.setCellValue(Integer.parseInt(lstInfEnt2015.get(i).getReferencia()));										
				}
				
				//prima comercial neta
				cell = row.createCell(8);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				if (lstInfEnt2015.get(i).getPrima() != null){									
					cell.setCellValue(lstInfEnt2015.get(i).getPrima().doubleValue());
					sumaPrima =sumaPrima.add(new BigDecimal(lstInfEnt2015.get(i).getPrima().doubleValue()));
				}
				
				//comision entidad
				cell = row.createCell(9);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFilaNumero);
				if (lstInfEnt2015.get(i).getComisionEnt()!= null){								
					cell.setCellValue(lstInfEnt2015.get(i).getComisionEnt().doubleValue());
					sumaComEnt =sumaComEnt.add(new BigDecimal(lstInfEnt2015.get(i).getComisionEnt().doubleValue()));
				}
				
				//comision E-S Med
				cell = row.createCell(10);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				if (lstInfEnt2015.get(i).getComisionEsMed() != null){									
					cell.setCellValue(lstInfEnt2015.get(i).getComisionEsMed().doubleValue());
					sumaComE_S =sumaComE_S.add(new BigDecimal(lstInfEnt2015.get(i).getComisionEsMed().doubleValue()));
				}
				
				//Pendiente Entidad
				cell = row.createCell(11);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				if (lstInfEnt2015.get(i).getPdteEnt() != null){									
					cell.setCellValue(lstInfEnt2015.get(i).getPdteEnt().doubleValue());
					sumaPendEnt =sumaPendEnt.add(new BigDecimal(lstInfEnt2015.get(i).getPdteEnt().doubleValue()));
				}
				
				//Pendiente E-S Med
				cell = row.createCell(12);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				if (lstInfEnt2015.get(i).getPdtMediador() != null){									
					cell.setCellValue(lstInfEnt2015.get(i).getPdtMediador().doubleValue());
					sumaPendE_S =sumaPendE_S.add(new BigDecimal(lstInfEnt2015.get(i).getPdtMediador().doubleValue()));
				}	
				//TT.ENT.
				cell = row.createCell(13);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				if (lstInfEnt2015.get(i).getTtEnt()!= null){				
					cell.setCellValue(lstInfEnt2015.get(i).getTtEnt().doubleValue());	
					sumaTT_E =sumaTT_E.add(new BigDecimal(lstInfEnt2015.get(i).getTtEnt().doubleValue()));
				}
				
				//TT.E-S Med
				cell = row.createCell(14);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				if (lstInfEnt2015.get(i).getTtEsMed()!= null){				
					cell.setCellValue(lstInfEnt2015.get(i).getTtEsMed().doubleValue());	
					sumaTT_E_S =sumaTT_E_S.add(new BigDecimal(lstInfEnt2015.get(i).getTtEsMed().doubleValue()));
				}
				
				//Liquidacion
				cell = row.createCell(15);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				if (lstInfEnt2015.get(i).getLiquidacion()!= null){				
					cell.setCellValue(lstInfEnt2015.get(i).getLiquidacion().doubleValue());	
					sumaLiquid =sumaLiquid.add(new BigDecimal(lstInfEnt2015.get(i).getLiquidacion().doubleValue()));
				}
			}
			if (i > 0){
				HSSFRow row = sheet.createRow(i+2);
				HSSFCellStyle estiloTotales = ExcelUtils.getEstiloFilaTotales(wb);
				HSSFCellStyle estiloFilaTotalesNumero = ExcelUtils.getEstiloFilaTotalesNumero(wb);
				
				cell = row.createCell(7);
				cell.setCellValue(new HSSFRichTextString(TOTAL));
				cell.setCellStyle(estiloTotales);
				
				cell = row.createCell(8);
				cell.setCellValue(sumaPrima.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
				cell = row.createCell(9);
				cell.setCellValue(sumaComEnt.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
				cell = row.createCell(10);
				cell.setCellValue(sumaComE_S.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
				cell = row.createCell(11);
				cell.setCellValue(sumaPendEnt.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
				cell = row.createCell(12);
				cell.setCellValue(sumaPendE_S.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
				cell = row.createCell(13);
				cell.setCellValue(sumaTT_E.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
				cell = row.createCell(14);
				cell.setCellValue(sumaTT_E_S.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
				cell = row.createCell(15);
				cell.setCellValue(sumaLiquid.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
			}			
			
			ExcelUtils.autoajustarColumnas(sheet, cell.getColumnIndex() + 1);
			
			wb.write(fileOut);
			
			informesExcelDao.saveFicheroExcelCierre(nombreFichero.toString(),
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString(), idCierre, usuario);	
			
			fileOut.close();
		} catch (FileNotFoundException e) {
			logger.error(PERMISO_CARPETA_TEMP + e.getMessage());
			throw new BusinessException (PERMISO_CARPETA_TEMP , e);
		} catch (IOException e) {
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + e.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , e);
		} catch (DAOException dao){
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + dao.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , dao);
		}			
		logger.debug("fin - generarInformeEntidades2015");
	}	
	
	public void generarInformeDetalleMediador(List<RgaComisiones> listComisiones, final Long idCierre, final Date fechaCierre, HashMap<String,String> subEntMed, 
			HashMap<String,String> lineas,Usuario usuario) throws BusinessException{
		logger.debug("init - generarInformeDetalleMediador");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaCierre);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		StringBuilder nombreFichero = new StringBuilder("InformeDetalleMediador_");
		nombreFichero.append(year);
		nombreFichero.append(month < 10 ? "0" + month : month);
		nombreFichero.append(".xls");
		
		int cont = 0;
		BigDecimal sumaComision = new BigDecimal(0);
		BigDecimal sumaGastoPendiente = new BigDecimal(0);
		
		try {		
		
			HSSFWorkbook wb          = new HSSFWorkbook();
			// ruta temporal
				
			FileOutputStream fileOut = new FileOutputStream(
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString());
		
			HSSFSheet sheet = wb.createSheet();
			sheet = wb.getSheetAt(0);
			
			HSSFCell cell;
					
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
			
			HSSFCellStyle estiloTitulo = ExcelUtils.getEstiloTitulo(wb);
			HSSFRow titulo = sheet.createRow(0);		
			
			cell = titulo.createCell(0);		
			
			cell.setCellStyle(estiloTitulo);
			cell.setCellValue(new HSSFRichTextString(getMesByCierre(fechaCierre)));				
						
			//Se insertan las cabeceras
			HSSFRow cabecera = sheet.createRow(1);
			HSSFCellStyle estiloCabecera = ExcelUtils.getEstiloCabecera(wb);	
				
			cell = cabecera.createCell(0);
			cell.setCellValue(new HSSFRichTextString("ENTIDAD MEDIADORA - SUBENTIDAD MEDIADORA"));
			cell.setCellStyle(estiloCabecera);						
			
			cell = cabecera.createCell(1);
			cell.setCellValue(new HSSFRichTextString("FASE"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(2);
			cell.setCellValue(new HSSFRichTextString("PLAN"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(3);
			cell.setCellValue(new HSSFRichTextString(LINEA));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(4);
			cell.setCellValue(new HSSFRichTextString("REFERENCIA COLECTIVO"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(5);
			cell.setCellValue(new HSSFRichTextString("COMISION"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(6);
			cell.setCellValue(new HSSFRichTextString("GASTO PENDIENTE"));
			cell.setCellStyle(estiloCabecera);
						
			HSSFCellStyle estiloFila = ExcelUtils.getEstiloFila(wb);
			HSSFCellStyle estiloFilaNumero = ExcelUtils.getEstiloFilaNumero(wb);
			
			for (cont=0; cont<listComisiones.size(); cont++){
				HSSFRow row     = sheet.createRow(cont+2);
				
								
				//C贸digo entidad-subentidad
				cell = row.createCell(0);
				cell.setCellStyle(estiloFila);
				if ((listComisiones.get(cont).getCodentmed() != null) && (listComisiones.get(cont).getCodsubmed()!= null)){
					String cod = listComisiones.get(cont).getCodentmed().intValue()
								 +"-"+listComisiones.get(cont).getCodsubmed().intValue();
					String descSubentidad = subEntMed.get(cod);					
					descSubentidad = (descSubentidad != null) ? descSubentidad.trim(): "";					
					cell.setCellValue(new HSSFRichTextString(listComisiones.get(cont).getCodentmed() + 
							"-" + listComisiones.get(cont).getCodsubmed() + " " + descSubentidad));					
				}		
				
				//Fase
				cell = row.createCell(1);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (listComisiones.get(cont).getNumfas() != null){								
					cell.setCellValue(Integer.parseInt(listComisiones.get(cont).getNumfas())); 					
				}
				
				//Plan
				cell = row.createCell(2);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (listComisiones.get(cont).getCodpln() != null){									
					cell.setCellValue(listComisiones.get(cont).getCodpln().intValue()); 					
				}
				
				//Linea
				cell = row.createCell(3);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (listComisiones.get(cont).getCodlin()!= null){					
					String descLinea = lineas.get(listComisiones.get(cont).getCodlin().toString());				
					descLinea = (descLinea != null) ? descLinea.trim(): "";
					cell.setCellValue(listComisiones.get(cont).getCodlin() + "-" + descLinea);					
				}	
								
				//Referencia colectivo
				cell = row.createCell(4);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (listComisiones.get(cont).getCodcol() != null){					
					cell.setCellValue(Integer.parseInt(listComisiones.get(cont).getCodcol()));					
				}
				
				
				//Comision 
				
				cell = row.createCell(5);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				
				BigDecimal ccSubentidad = StringUtils.nullToZero(listComisiones.get(cont).getComsubsum());
				BigDecimal ggSubentidad = StringUtils.nullToZero(listComisiones.get(cont).getGassubsum());
				BigDecimal gastoPendiente = StringUtils.nullToZero(listComisiones.get(cont).getGaspen());
				BigDecimal comision = ccSubentidad.add(ggSubentidad).subtract(gastoPendiente);
				
				cell.setCellValue(comision.doubleValue()); 		
				//suma comision
				sumaComision = sumaComision.add(comision);					
				
				
				// Gasto pendiente
				cell = row.createCell(6);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				cell.setCellValue(gastoPendiente.doubleValue()); 		
				
				
				//Suma de total de gasto Pendiente
				/*if (listComisiones.get(cont).getCostot() != null){*/
				sumaGastoPendiente = sumaGastoPendiente.add(gastoPendiente);
				//}
				
			}
			
			if (cont > 0){
				HSSFRow row = sheet.createRow(cont+2);
				HSSFCellStyle estiloTotales = ExcelUtils.getEstiloFilaTotales(wb);
				HSSFCellStyle estiloFilaTotalesNumero = ExcelUtils.getEstiloFilaTotalesNumero(wb);
				
				cell = row.createCell(4);	
				cell.setCellValue(new HSSFRichTextString(TOTAL));
				cell.setCellStyle(estiloTotales);
				
				cell = row.createCell(5);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(sumaComision.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
				cell = row.createCell(6);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(sumaGastoPendiente.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
			}			
			
			ExcelUtils.autoajustarColumnas(sheet, cell.getColumnIndex() + 1);		
			
			wb.write(fileOut);
			
			informesExcelDao.saveFicheroExcelCierre(nombreFichero.toString(),
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString(), idCierre, usuario);	
						
			fileOut.close();
		} catch (FileNotFoundException e) {
			logger.error(PERMISO_CARPETA_TEMP + e.getMessage());
			throw new BusinessException (PERMISO_CARPETA_TEMP , e);
		} catch (IOException e) {
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL +e );
			throw new BusinessException (ERROR_GUARDAR_EXCEL , e);
		} catch (DAOException dao){
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + dao.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , dao);	
		}			
	}
	
	public void generarInformeDetalleMediador2015(List<InformeDetMediador2015> listDetMed2015, final Long idCierre, final Date fechaCierre, 
			HashMap<String,String> lineas,Usuario usuario) throws BusinessException{
		logger.debug("init - generarInformeDetalleMediador2015");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaCierre);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		StringBuilder nombreFichero = new StringBuilder("InformeDetalleMediador2015_");
		nombreFichero.append(year);
		nombreFichero.append(month < 10 ? "0" + month : month);
		nombreFichero.append(".xls");
		
		int cont = 0;
		BigDecimal sumaComE_S_Med = new BigDecimal(0);
		BigDecimal sumaGastoPdteE_S_Med = new BigDecimal(0);
		try {		
		
			HSSFWorkbook wb          = new HSSFWorkbook();
			// ruta temporal
				
			FileOutputStream fileOut = new FileOutputStream(
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString());
		
			HSSFSheet sheet = wb.createSheet();
			sheet = wb.getSheetAt(0);
			
			HSSFCell cell;
					
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));
			
			HSSFCellStyle estiloTitulo = ExcelUtils.getEstiloTitulo(wb);
			HSSFRow titulo = sheet.createRow(0);		
			
			cell = titulo.createCell(0);		
			
			cell.setCellStyle(estiloTitulo);
			cell.setCellValue(new HSSFRichTextString(getMesByCierre(fechaCierre)));				
						
			//Se insertan las cabeceras
			HSSFRow cabecera = sheet.createRow(1);
			HSSFCellStyle estiloCabecera = ExcelUtils.getEstiloCabecera(wb);	
				
			cell = cabecera.createCell(0);
			cell.setCellValue(new HSSFRichTextString(ENT_MED));
			cell.setCellStyle(estiloCabecera);						
			
			cell = cabecera.createCell(1);
			cell.setCellValue(new HSSFRichTextString(SUBENT_MED));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(2);
			cell.setCellValue(new HSSFRichTextString(NOMBRE_MEDIADORA));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(3);
			cell.setCellValue(new HSSFRichTextString("FASE"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(4);
			cell.setCellValue(new HSSFRichTextString("PLAN"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(5);
			cell.setCellValue(new HSSFRichTextString("COD. LINEA"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(6);
			cell.setCellValue(new HSSFRichTextString(LINEA));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(7);
			cell.setCellValue(new HSSFRichTextString(COLECTIVO));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(8);
			cell.setCellValue(new HSSFRichTextString("COMISION E-S MED"));
			cell.setCellStyle(estiloCabecera);
						
			cell = cabecera.createCell(9);
			cell.setCellValue(new HSSFRichTextString("GASTO PDTE.E-S MED"));
			cell.setCellStyle(estiloCabecera);
			
			HSSFCellStyle estiloFila = ExcelUtils.getEstiloFila(wb);
			HSSFCellStyle estiloFilaNumero = ExcelUtils.getEstiloFilaNumero(wb);
			
			for (cont=0; cont<listDetMed2015.size(); cont++){
				HSSFRow row     = sheet.createRow(cont+2);
				
				
				cell = row.createCell(0);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				//Codigo entidad mediadiora
				if ((listDetMed2015.get(cont).getEntidad() != null)){					
					cell.setCellValue(listDetMed2015.get(cont).getEntidad().doubleValue());					
				}
								
				cell = row.createCell(1);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				//Codigo Subentidad mediadiora
				if ((listDetMed2015.get(cont).getSubentidad() != null)){					
					cell.setCellValue(listDetMed2015.get(cont).getSubentidad().doubleValue());						
				}			
				
				//Nombre E-S Mediadora
				cell = row.createCell(2);
				cell.setCellStyle(estiloFila);
				if ((listDetMed2015.get(cont).getNomentidad() != null)){					
					cell.setCellValue(new HSSFRichTextString(listDetMed2015.get(cont).getNomentidad()));					
				}
								
				//Fase
				cell = row.createCell(3);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (listDetMed2015.get(cont).getFase() != null){								
					cell.setCellValue(listDetMed2015.get(cont).getFase()); 					
				}
				
				//Plan
				cell = row.createCell(4);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (listDetMed2015.get(cont).getPlan() != null){									
					cell.setCellValue(listDetMed2015.get(cont).getPlan().intValue()); 					
				}
				
				//Linea
				cell = row.createCell(5);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (listDetMed2015.get(cont).getLinea()!= null){					
					cell.setCellValue(listDetMed2015.get(cont).getLinea().doubleValue());					
				}	
				
				//NombreLinea
				cell = row.createCell(6);
				cell.setCellStyle(estiloFila);
				if (listDetMed2015.get(cont).getNomlinea() != null){					
					String descLinea = lineas.get(listDetMed2015.get(cont).getLinea().toString());
					descLinea = (descLinea != null) ? descLinea.trim(): "";
					cell.setCellValue(descLinea);					
				}				
				
				//Referencia colectivo
				cell = row.createCell(7);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (listDetMed2015.get(cont).getReferencia() != null){					
					cell.setCellValue(Integer.parseInt(listDetMed2015.get(cont).getReferencia()));					
				}
				
				//Comision E-S Med
				cell = row.createCell(8);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				if (listDetMed2015.get(cont).getComisionEsMed() != null){					
					cell.setCellValue(listDetMed2015.get(cont).getComisionEsMed().doubleValue());
					sumaComE_S_Med = sumaComE_S_Med.add(listDetMed2015.get(cont).getComisionEsMed());
				}	
							
				// Gasto pendiente E-S Med
				cell = row.createCell(9);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				if (listDetMed2015.get(cont).getPdteEsMed() != null){					
					cell.setCellValue(listDetMed2015.get(cont).getPdteEsMed().doubleValue());
					sumaGastoPdteE_S_Med = sumaGastoPdteE_S_Med.add(listDetMed2015.get(cont).getPdteEsMed());
				}	
				
				
			}
			
			if (cont > 0){
				HSSFRow row = sheet.createRow(cont+2);
				HSSFCellStyle estiloTotales = ExcelUtils.getEstiloFilaTotales(wb);
				HSSFCellStyle estiloFilaTotalesNumero = ExcelUtils.getEstiloFilaTotalesNumero(wb);
				
				cell = row.createCell(7);	
				cell.setCellValue(new HSSFRichTextString(TOTAL));
				cell.setCellStyle(estiloTotales);
								
				cell = row.createCell(8);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(sumaComE_S_Med.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
								
				cell = row.createCell(9);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(sumaGastoPdteE_S_Med.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
			}			
			
			ExcelUtils.autoajustarColumnas(sheet, cell.getColumnIndex() + 1);		
			
			wb.write(fileOut);
			
			informesExcelDao.saveFicheroExcelCierre(nombreFichero.toString(),
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString(), idCierre, usuario);	
						
			fileOut.close();
		} catch (FileNotFoundException e) {
			logger.error(PERMISO_CARPETA_TEMP + e.getMessage());
			throw new BusinessException (PERMISO_CARPETA_TEMP , e);
		} catch (IOException e) {
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL +e );
			throw new BusinessException (ERROR_GUARDAR_EXCEL , e);
		} catch (DAOException dao){
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + dao.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , dao);	
		}			
	}
	
	public void generarInformeImpagados(List<ReciboImpagado> listInformeImpagados, final Long idCierre, final Date fechaCierre, 
			HashMap<String,String> entMed,List<Colectivo> listcolectivos,Usuario usuario) throws BusinessException {
		logger.debug("init - generarInformeImpagados");
		//String nombreFichero = System.getProperty("java.io.tmpdir")+"informeImpagados.xls" ;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaCierre);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		StringBuilder nombreFichero = new StringBuilder("InformeImpagados_");
		nombreFichero.append(year);
		nombreFichero.append(month < 10 ? "0" + month : month);
		nombreFichero.append(".xls");
		
		int i = 0;
		BigDecimal totComImp = new BigDecimal(0);
		try {		
		
			
			HSSFWorkbook wb          = new HSSFWorkbook();
			// ruta temporal
			
			FileOutputStream fileOut = new FileOutputStream(
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString());
		
			HSSFSheet sheet = wb.createSheet();
			sheet = wb.getSheetAt(0);			
					
			HSSFCell cell;	
			
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
			
			HSSFCellStyle estiloTitulo = ExcelUtils.getEstiloTitulo(wb);
			HSSFRow titulo = sheet.createRow(0);		
			
			cell = titulo.createCell(0);		
			
			cell.setCellStyle(estiloTitulo);
			cell.setCellValue(new HSSFRichTextString(getMesByCierre(fechaCierre)));	
			
			//Se insertan las cabeceras
			HSSFCellStyle estiloCabecera = ExcelUtils.getEstiloCabecera(wb);
			HSSFRow cabecera = sheet.createRow(1);
			
			cell = cabecera.createCell(0);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("AGENTE"));
			
			cell = cabecera.createCell(1);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("NOMBRE DE LA ENTIDAD"));
			
			cell = cabecera.createCell(2);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("REFERENCIA COLECTIVO"));
			
			cell = cabecera.createCell(3);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("RAZON SOCIAL DEL TOMADOR"));
			
			cell = cabecera.createCell(4);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("NUMERO DE RECIBO"));
			
			cell = cabecera.createCell(5);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("TOTAL COMISIONES IMPAGADOS"));			
			
			
			HSSFCellStyle estiloFila = ExcelUtils.getEstiloFila(wb);
			HSSFCellStyle estiloFilaNumero = ExcelUtils.getEstiloFilaNumero(wb);
			
			for (i=0; i<listInformeImpagados.size(); i++){
				HSSFRow row     = sheet.createRow(i+2); 
								
				
				//AGENTE
				cell = row.createCell(0);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				
				//colectivocodinterno en este caso es realmente el codigo de la entidad
				//como  el codigo de la entidad no esta en el objeto reciboimpagados 
				//se le pasa a traves de esta propiedad
				if (listInformeImpagados.get(i).getColectivocodinterno() != null){					
					cell.setCellValue(listInformeImpagados.get(i).getColectivocodinterno());					
				}
				
				//NOMBRE DE LA ENTIDAD
				cell = row.createCell(1);
				cell.setCellStyle(estiloFila);
				if (listInformeImpagados.get(i).getColectivocodinterno() != null){					
					String cod = listInformeImpagados.get(i).getColectivocodinterno();									
					String desc = entMed.get(cod);
					desc = (desc != null) ? desc.trim(): "";
					cell.setCellValue(new HSSFRichTextString(desc));								
				}
				
				//REFERENCIA COLECTIVO
				cell = row.createCell(2);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (listInformeImpagados.get(i).getColectivoreferencia() != null){					
					cell.setCellValue(Integer.parseInt(listInformeImpagados.get(i).getColectivoreferencia()));				
				}
				
				//RAZON SOCIAL DEL TOMADOR
				cell = row.createCell(3);
				cell.setCellStyle(estiloFila);
				if (listInformeImpagados.get(i).getRazonsocial() != null){					
					cell.setCellValue(new HSSFRichTextString(listInformeImpagados.get(i).getRazonsocial()));					
				}
				
				//NUMERO DE RECIBO
				cell = row.createCell(4);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (listInformeImpagados.get(i).getRecibo() != null){						
					cell.setCellValue(listInformeImpagados.get(i).getRecibo().doubleValue()); 					
				}
				
				//TOTAL COMISIONES IMPAGADOS
				cell = row.createCell(5);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFilaNumero);
				if (listInformeImpagados.get(i).getPaGastoscomisiones() != null){
					cell.setCellValue(listInformeImpagados.get(i).getPaGastoscomisiones().doubleValue());
					totComImp = totComImp.add(listInformeImpagados.get(i).getPaGastoscomisiones());
				}
				
			}
			if (i > 0){
				HSSFRow row = sheet.createRow(i+2);				
				HSSFCellStyle estiloTotales = ExcelUtils.getEstiloFilaTotales(wb);	
				HSSFCellStyle estiloFilaTotalesNumero = ExcelUtils.getEstiloFilaTotalesNumero(wb);
				
				cell = row.createCell(4);
				cell.setCellValue(new HSSFRichTextString(TOTAL));				
				cell.setCellStyle(estiloTotales);
				
				cell = row.createCell(5);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(totComImp.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
			}			
			
			ExcelUtils.autoajustarColumnas(sheet, cell.getColumnIndex() + 1);		
			
			wb.write(fileOut);
			
			informesExcelDao.saveFicheroExcelCierre(nombreFichero.toString(),
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString(), idCierre, usuario);	
			
			fileOut.close();
		} catch (FileNotFoundException e) {
			logger.error(PERMISO_CARPETA_TEMP + e.getMessage());
			throw new BusinessException (PERMISO_CARPETA_TEMP , e);
		} catch (IOException e) {
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + e.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , e);
		} catch (DAOException dao){
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + dao.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , dao);
		}			
		
	}
	
	public void generarInformeImpagados2015(List<InformeComsImpagados2015> lstInfImpagados2015, final Long idCierre, final Date fechaCierre,Usuario usuario) throws BusinessException {
		logger.debug("init - generarInformeImpagados2015");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaCierre);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		StringBuilder nombreFichero = new StringBuilder("InformeImpagados2015+_");
		nombreFichero.append(year);
		nombreFichero.append(month < 10 ? "0" + month : month);
		nombreFichero.append(".xls");
		
		int i = 0;
		BigDecimal totComImp = new BigDecimal(0);
		try {		
		
			
			HSSFWorkbook wb          = new HSSFWorkbook();
			// ruta temporal
			
			FileOutputStream fileOut = new FileOutputStream(
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString());
		
			HSSFSheet sheet = wb.createSheet();
			sheet = wb.getSheetAt(0);			
					
			HSSFCell cell;	
			
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
			
			HSSFCellStyle estiloTitulo = ExcelUtils.getEstiloTitulo(wb);
			HSSFRow titulo = sheet.createRow(0);		
			
			cell = titulo.createCell(0);		
			
			cell.setCellStyle(estiloTitulo);
			cell.setCellValue(new HSSFRichTextString(getMesByCierre(fechaCierre)));	
			
			//Se insertan las cabeceras
			HSSFCellStyle estiloCabecera = ExcelUtils.getEstiloCabecera(wb);
			HSSFRow cabecera = sheet.createRow(1);
			
			cell = cabecera.createCell(0);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString(ENT_MED));
			
			cell = cabecera.createCell(1);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString(SUBENT_MED));
			
			cell = cabecera.createCell(2);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString(NOMBRE_MEDIADORA));
			
			cell = cabecera.createCell(3);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString(COLECTIVO));
			
			cell = cabecera.createCell(4);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("RAZON SOCIAL DEL TOMADOR"));
			
			cell = cabecera.createCell(5);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("P覮IZA"));
			
			cell = cabecera.createCell(6);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("RECIBO"));
			
			cell = cabecera.createCell(7);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("TOTAL COMISIONES IMPAGADOS"));			
			
			
			HSSFCellStyle estiloFila = ExcelUtils.getEstiloFila(wb);
			HSSFCellStyle estiloFilaNumero = ExcelUtils.getEstiloFilaNumero(wb);
			
			for (i=0; i<lstInfImpagados2015.size(); i++){
				HSSFRow row     = sheet.createRow(i+2); 
								
				
				cell = row.createCell(0);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				//Codigo entidad mediadiora
				if ((lstInfImpagados2015.get(i).getEntidad() != null)){					
					cell.setCellValue(lstInfImpagados2015.get(i).getEntidad().doubleValue());					
				}
								
				cell = row.createCell(1);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				//Codigo Subentidad mediadiora
				if ((lstInfImpagados2015.get(i).getSubentidad() != null)){					
					cell.setCellValue(lstInfImpagados2015.get(i).getSubentidad().doubleValue());						
				}			
				
				//Nombre E-S Mediadora
				cell = row.createCell(2);
				cell.setCellStyle(estiloFila);
				if ((lstInfImpagados2015.get(i).getNomentidad() != null)){					
					cell.setCellValue(new HSSFRichTextString(lstInfImpagados2015.get(i).getNomentidad()));					
				}
								
				// COLECTIVO
				cell = row.createCell(3);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (lstInfImpagados2015.get(i).getReferencia() != null){					
					cell.setCellValue(Integer.parseInt(lstInfImpagados2015.get(i).getReferencia()));				
				}
				
				//RAZON SOCIAL DEL TOMADOR
				cell = row.createCell(4);
				cell.setCellStyle(estiloFila);
				if (lstInfImpagados2015.get(i).getTomador() != null){					
					cell.setCellValue(new HSSFRichTextString(lstInfImpagados2015.get(i).getTomador()));					
				}
				
				//Poliza
				cell = row.createCell(5);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (lstInfImpagados2015.get(i).getRefPoliza() != null){						
					cell.setCellValue(lstInfImpagados2015.get(i).getRefPoliza()); 					
				}
				
				//NUMERO DE RECIBO
				cell = row.createCell(6);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (lstInfImpagados2015.get(i).getRecibo() != null){						
					cell.setCellValue(lstInfImpagados2015.get(i).getRecibo().doubleValue()); 					
				}
				
				//TOTAL COMISIONES IMPAGADOS
				cell = row.createCell(7);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFilaNumero);
				if (lstInfImpagados2015.get(i).getComisionImp() != null){
					cell.setCellValue(lstInfImpagados2015.get(i).getComisionImp().doubleValue());
					totComImp = totComImp.add(lstInfImpagados2015.get(i).getComisionImp());
				}
				
			}
			if (i > 0){
				HSSFRow row = sheet.createRow(i+2);				
				HSSFCellStyle estiloTotales = ExcelUtils.getEstiloFilaTotales(wb);	
				HSSFCellStyle estiloFilaTotalesNumero = ExcelUtils.getEstiloFilaTotalesNumero(wb);
				
				cell = row.createCell(6);
				cell.setCellValue(new HSSFRichTextString(TOTAL));				
				cell.setCellStyle(estiloTotales);
				
				cell = row.createCell(7);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(totComImp.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
			}			
			
			ExcelUtils.autoajustarColumnas(sheet, cell.getColumnIndex() + 1);		
			
			wb.write(fileOut);
			
			informesExcelDao.saveFicheroExcelCierre(nombreFichero.toString(),
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString(), idCierre, usuario);	
			
			fileOut.close();
		} catch (FileNotFoundException e) {
			logger.error(PERMISO_CARPETA_TEMP + e.getMessage());
			throw new BusinessException (PERMISO_CARPETA_TEMP , e);
		} catch (IOException e) {
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + e.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , e);
		} catch (DAOException dao){
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + dao.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , dao);
		}			
		
	}
	
	public void generarInformeComsRGA2015(List<InformeComsRGA2015> lstComsRGA2015, final Long idCierre, final Date fechaCierre, 
			HashMap<String,String> lineas,Usuario usuario) throws BusinessException {
		logger.debug("init - generarInformeComsRGA2015");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaCierre);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		StringBuilder nombreFichero = new StringBuilder("InformeComsRGA2015+_");
		nombreFichero.append(year);
		nombreFichero.append(month < 10 ? "0" + month : month);
		nombreFichero.append(".xls");
		
		int i = 0;
		BigDecimal totAdq = new BigDecimal(0);
		BigDecimal totAdm = new BigDecimal(0);
		BigDecimal totPendAdq = new BigDecimal(0);
		BigDecimal totPendAdm = new BigDecimal(0);
		try {		

			HSSFWorkbook wb          = new HSSFWorkbook();
			// ruta temporal
			
			FileOutputStream fileOut = new FileOutputStream(
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString());
		
			HSSFSheet sheet = wb.createSheet();
			sheet = wb.getSheetAt(0);			
					
			HSSFCell cell;	
			
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 11));
			
			HSSFCellStyle estiloTitulo = ExcelUtils.getEstiloTitulo(wb);
			HSSFRow titulo = sheet.createRow(0);		
			
			cell = titulo.createCell(0);		
			
			cell.setCellStyle(estiloTitulo);
			cell.setCellValue(new HSSFRichTextString(getMesByCierre(fechaCierre)));	
			
			//Se insertan las cabeceras
			HSSFCellStyle estiloCabecera = ExcelUtils.getEstiloCabecera(wb);
			HSSFRow cabecera = sheet.createRow(1);

			cell = cabecera.createCell(0);
			cell.setCellValue(new HSSFRichTextString("PLAN"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(1);
			cell.setCellValue(new HSSFRichTextString("COD. LINEA"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(2);
			cell.setCellValue(new HSSFRichTextString(LINEA));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(3);
			cell.setCellValue(new HSSFRichTextString("FASE"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(4);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("P覮IZA"));
			
			cell = cabecera.createCell(5);
			cell.setCellValue(new HSSFRichTextString(ENT_MED));
			cell.setCellStyle(estiloCabecera);						
			
			cell = cabecera.createCell(6);
			cell.setCellValue(new HSSFRichTextString(SUBENT_MED));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(7);
			cell.setCellValue(new HSSFRichTextString(NOMBRE_MEDIADORA));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(8);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("ADM"));
			
			cell = cabecera.createCell(9);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("ADQ"));
			
			cell = cabecera.createCell(10);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("PENDIENTE ADM"));
			
			cell = cabecera.createCell(11);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("PENDIENTE ADQ"));

			HSSFCellStyle estiloFila = ExcelUtils.getEstiloFila(wb);
			HSSFCellStyle estiloFilaNumero = ExcelUtils.getEstiloFilaNumero(wb);
			
			for (i=0; i<lstComsRGA2015.size(); i++){
				HSSFRow row     = sheet.createRow(i+2); 

				//Plan
				cell = row.createCell(0);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (lstComsRGA2015.get(i).getPlan() != null){									
					cell.setCellValue(lstComsRGA2015.get(i).getPlan().intValue()); 					
				}
				
				//Linea
				cell = row.createCell(1);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (lstComsRGA2015.get(i).getLinea()!= null){					
					cell.setCellValue(lstComsRGA2015.get(i).getLinea().doubleValue());					
				}	
				
				//NombreLinea
				cell = row.createCell(2);
				cell.setCellStyle(estiloFila);
				if (lstComsRGA2015.get(i).getNomlinea() != null){					
					String descLinea = lineas.get(lstComsRGA2015.get(i).getLinea().toString());
					descLinea = (descLinea != null) ? descLinea.trim(): "";
					cell.setCellValue(descLinea);					
				}	
				
				//Fase
				cell = row.createCell(3);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (lstComsRGA2015.get(i).getFase() != null){								
					cell.setCellValue(lstComsRGA2015.get(i).getFase()); 					
				}
				
				//Poliza
				cell = row.createCell(4);
				cell.setCellStyle(estiloFila);
				if (lstComsRGA2015.get(i).getRefpoliza() != null){						
					cell.setCellValue(lstComsRGA2015.get(i).getRefpoliza()); 					
				}
				
				//Codigo entidad mediadiora
				cell = row.createCell(5);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if ((lstComsRGA2015.get(i).getEntidad() != null)){					
					cell.setCellValue(lstComsRGA2015.get(i).getEntidad().doubleValue());					
				}
								
				cell = row.createCell(6);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				//Codigo Subentidad mediadiora
				if ((lstComsRGA2015.get(i).getSubentidad() != null)){					
					cell.setCellValue(lstComsRGA2015.get(i).getSubentidad().doubleValue());						
				}			
				
				//Nombre E-S Mediadora
				cell = row.createCell(7);
				cell.setCellStyle(estiloFila);
				if ((lstComsRGA2015.get(i).getNomentidad() != null)){					
					cell.setCellValue(new HSSFRichTextString(lstComsRGA2015.get(i).getNomentidad()));					
				}
				
				//Adm
				cell = row.createCell(8);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFilaNumero);
				if (lstComsRGA2015.get(i).getAdmin() != null){
					cell.setCellValue(lstComsRGA2015.get(i).getAdmin().doubleValue());
					totAdm = totAdm.add(lstComsRGA2015.get(i).getAdmin());
				}
				
				
				//Adq
				cell = row.createCell(9);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFilaNumero);
				if (lstComsRGA2015.get(i).getAdq() != null){
					cell.setCellValue(lstComsRGA2015.get(i).getAdq().doubleValue());
					totAdq = totAdq.add(lstComsRGA2015.get(i).getAdq());
				}
				
				//Pendiente Adm
				cell = row.createCell(10);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFilaNumero);
				if (lstComsRGA2015.get(i).getPdteAdmin() != null){
					cell.setCellValue(lstComsRGA2015.get(i).getPdteAdmin().doubleValue());
					totPendAdm = totPendAdm.add(lstComsRGA2015.get(i).getPdteAdmin());
				}
				
				//Pendiente Adq
				cell = row.createCell(11);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFilaNumero);
				if (lstComsRGA2015.get(i).getPdteAdq() != null){
					cell.setCellValue(lstComsRGA2015.get(i).getPdteAdq().doubleValue());
					totPendAdq = totPendAdq.add(lstComsRGA2015.get(i).getPdteAdq());
				}

			}
			if (i > 0){
				HSSFRow row = sheet.createRow(i+2);				
				HSSFCellStyle estiloTotales = ExcelUtils.getEstiloFilaTotales(wb);	
				HSSFCellStyle estiloFilaTotalesNumero = ExcelUtils.getEstiloFilaTotalesNumero(wb);
				
				cell = row.createCell(7);
				cell.setCellValue(new HSSFRichTextString(TOTAL));				
				cell.setCellStyle(estiloTotales);
				
				cell = row.createCell(8);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(totAdm.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
				cell = row.createCell(9);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(totAdq.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
				cell = row.createCell(10);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(totPendAdm.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
				cell = row.createCell(11);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(totPendAdq.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
			}			
			
			ExcelUtils.autoajustarColumnas(sheet, cell.getColumnIndex() + 1);		
			
			wb.write(fileOut);
			
			informesExcelDao.saveFicheroExcelCierre(nombreFichero.toString(),
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString(), idCierre, usuario);	
			
			fileOut.close();
		} catch (FileNotFoundException e) {
			logger.error(PERMISO_CARPETA_TEMP + e.getMessage());
			throw new BusinessException (PERMISO_CARPETA_TEMP , e);
		} catch (IOException e) {
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + e.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , e);
		} catch (DAOException dao){
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + dao.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , dao);
		}			
		
	}
	
	public List<EntidadesOperadoresInforme> getListaEntidadesOperadoras(final Long idCierre) throws BusinessException {
		logger.debug("init - getListaCooperativas");
		List<EntidadesOperadoresInforme> listEntidadesOperadoras= null;		
		try {
			
			listEntidadesOperadoras = informesExcelDao.listEntidadesOperadores(idCierre);			
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al generar  la lista de comisiones pendientes:" + dao.getMessage());
			throw new BusinessException (ERROR_LIST_COM_PEN , dao);
		}
		logger.debug("end - getListaCooperativas");
		return listEntidadesOperadoras;
		
	}
	
	public void generarEntidadesOperadores2015(List<InformeEntidadesOperadores2015> lstEntOpe2015, final Long idCierre, final Date fechaCierre, Usuario usuario) throws BusinessException {
		logger.debug("init - generarEntidadesOperadores2015");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaCierre);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		StringBuilder nombreFichero = new StringBuilder("InformeEntidadesOperadores2015+_");
		nombreFichero.append(year);
		nombreFichero.append(month < 10 ? "0" + month : month);
		nombreFichero.append(".xls");
		
		int i = 0;
		BigDecimal totFases = new BigDecimal(0);
		BigDecimal totCoop  = new BigDecimal(0);
		BigDecimal totImp   = new BigDecimal(0);
		BigDecimal totDeuda = new BigDecimal(0);
		BigDecimal totTotal = new BigDecimal(0);
		try {		

			HSSFWorkbook wb          = new HSSFWorkbook();
			// ruta temporal
			
			FileOutputStream fileOut = new FileOutputStream(
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString());		
			HSSFSheet sheet = wb.createSheet();
			sheet = wb.getSheetAt(0);								
			HSSFCell cell;				
			
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
			
			HSSFCellStyle estiloTitulo = ExcelUtils.getEstiloTitulo(wb);
			HSSFRow titulo = sheet.createRow(0);		
			
			cell = titulo.createCell(0);		
			
			cell.setCellStyle(estiloTitulo);
			cell.setCellValue(new HSSFRichTextString(getMesByCierre(fechaCierre)));	
			
			//Se insertan las cabeceras
			HSSFCellStyle estiloCabecera = ExcelUtils.getEstiloCabecera(wb);
			HSSFRow cabecera = sheet.createRow(1);
			
			
			cell = cabecera.createCell(0);
			cell.setCellValue(new HSSFRichTextString("CSB"));
			cell.setCellStyle(estiloCabecera);						

			cell = cabecera.createCell(1);
			cell.setCellValue(new HSSFRichTextString("Caja Rural"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(2);
			cell.setCellValue(new HSSFRichTextString("FASES"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(3);
			cell.setCellValue(new HSSFRichTextString("COOP."));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(4);
			cell.setCellValue(new HSSFRichTextString("IMPAGADOS"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(5);
			cell.setCellValue(new HSSFRichTextString("D.APLAZADA"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(6);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString(TOTAL));
			
			cell = cabecera.createCell(7);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString(TITULO_COL_IBAN));

			

			HSSFCellStyle estiloFila = ExcelUtils.getEstiloFila(wb);
			HSSFCellStyle estiloFilaNumero = ExcelUtils.getEstiloFilaNumero(wb);
			
			for (i=0; i<lstEntOpe2015.size(); i++){
				HSSFRow row     = sheet.createRow(i+2); 

				//Codigo entidad mediadiora
				cell = row.createCell(0);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if ((lstEntOpe2015.get(i).getEntidad() != null)){			
					cell.setCellValue(lstEntOpe2015.get(i).getEntidad().doubleValue());					
				}
				
				//Nombre E-S Mediadora
				cell = row.createCell(1);
				cell.setCellStyle(estiloFila);
				if ((lstEntOpe2015.get(i).getNomentidad() != null)){	
					cell.setCellValue(new HSSFRichTextString(lstEntOpe2015.get(i).getNomentidad()));					
				}
					
				//FASES
				cell = row.createCell(2);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFilaNumero);
				if (lstEntOpe2015.get(i).getFases() != null){
					cell.setCellValue(lstEntOpe2015.get(i).getFases().doubleValue());
					totFases = totFases.add(lstEntOpe2015.get(i).getFases());
				}
			
				//COOP.
				cell = row.createCell(3);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFilaNumero);
				if (lstEntOpe2015.get(i).getCoop() != null){
					cell.setCellValue(lstEntOpe2015.get(i).getCoop().doubleValue());
					totCoop = totCoop.add(lstEntOpe2015.get(i).getCoop());
				}
				
				//IMPAGADOS
				cell = row.createCell(4);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFilaNumero);
				if (lstEntOpe2015.get(i).getImpagados() != null){
					cell.setCellValue(lstEntOpe2015.get(i).getImpagados().doubleValue());
					totImp = totImp.add(lstEntOpe2015.get(i).getImpagados());
				}
				
				//DEUDA APLAZADA
				cell = row.createCell(5);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFilaNumero);
				if (lstEntOpe2015.get(i).getDeuda() != null){
					cell.setCellValue(lstEntOpe2015.get(i).getDeuda().doubleValue());
					totDeuda = totDeuda.add(lstEntOpe2015.get(i).getDeuda());
				}
				
				//TOTAL
				cell = row.createCell(6);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFilaNumero);
				if (lstEntOpe2015.get(i).getTotal() != null){
					cell.setCellValue(lstEntOpe2015.get(i).getTotal().doubleValue());
					totTotal = totTotal.add(lstEntOpe2015.get(i).getTotal());
				}
				
				//IBAN
				cell = row.createCell(7);
				cell.setCellStyle(estiloFila);
				if ((lstEntOpe2015.get(i).getIban() != null)){					
					cell.setCellValue(new HSSFRichTextString(lstEntOpe2015.get(i).getIban()));					
				}
			}
			if (i > 0){
				HSSFRow row = sheet.createRow(i+2);				
				HSSFCellStyle estiloTotales = ExcelUtils.getEstiloFilaTotales(wb);	
				HSSFCellStyle estiloFilaTotalesNumero = ExcelUtils.getEstiloFilaTotalesNumero(wb);
				
				cell = row.createCell(1);
				cell.setCellValue(new HSSFRichTextString(TOTAL));				
				cell.setCellStyle(estiloTotales);
				
				cell = row.createCell(2);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(totFases.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
				cell = row.createCell(3);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(totCoop.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
				cell = row.createCell(4);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(totImp.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
				cell = row.createCell(5);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(totDeuda.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
				cell = row.createCell(6);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(totTotal.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
			}			
			
			ExcelUtils.autoajustarColumnas(sheet, cell.getColumnIndex() + 1);		
			
			wb.write(fileOut);
			
			informesExcelDao.saveFicheroExcelCierre(nombreFichero.toString(),
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString(), idCierre, usuario);	
			
			fileOut.close();
		} catch (FileNotFoundException e) {
			logger.error(PERMISO_CARPETA_TEMP + e.getMessage());
			throw new BusinessException (PERMISO_CARPETA_TEMP , e);
		} catch (IOException e) {
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + e.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , e);
		} catch (DAOException dao){
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + dao.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , dao);
		}			
		
	}
	
	public void generarEntidadesOperadores(List<EntidadesOperadoresInforme> listEntidadesOperadores,
			HashMap<String,String> entMed, final Long idCierre, final Date fechaCierre, Usuario usuario) throws BusinessException {
		logger.debug("init - generarEntOperadores");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaCierre);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		StringBuilder nombreFichero = new StringBuilder("InformeEntidadesOperadores_");
		nombreFichero.append(year);
		nombreFichero.append(month < 10 ? "0" + month : month);
		nombreFichero.append(".xls");
		
		int cont = 0;
		
		BigDecimal totalFase = new BigDecimal(0);
		BigDecimal totalCoop = new BigDecimal(0);
		BigDecimal totalImp = new BigDecimal(0);
		BigDecimal totalTotal = new BigDecimal(0);
		
		
		BigDecimal totalFila = new BigDecimal(0);
		try {		
		
			HSSFWorkbook wb          = new HSSFWorkbook();
			// ruta temporal
			
			
			FileOutputStream fileOut = new FileOutputStream(
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString());
		
			HSSFSheet sheet = wb.createSheet();
			sheet = wb.getSheetAt(0);
			
			HSSFCell cell;						
			
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
			
			HSSFCellStyle estiloTitulo = ExcelUtils.getEstiloTitulo(wb);
			HSSFRow titulo = sheet.createRow(0);		
			
			cell = titulo.createCell(0);		
			
			cell.setCellStyle(estiloTitulo);
			cell.setCellValue(new HSSFRichTextString(getMesByCierre(fechaCierre)));	
						
			//Se insertan las cabeceras
			HSSFRow cabecera = sheet.createRow(1); //new HSSFRichTextString("CODENT")
			HSSFCellStyle estiloCabecera = ExcelUtils.getEstiloCabecera(wb);
			
			cell = cabecera.createCell(0);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("CSB"));
			
			cell = cabecera.createCell(1);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("CAJA RURAL"));
			
			cell = cabecera.createCell(2);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("FASES"));
			
			cell = cabecera.createCell(3);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("COOP."));
			
			cell = cabecera.createCell(4);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("IMPAGADOS"));
			
			cell = cabecera.createCell(5);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString(TOTAL));
			
			HSSFCellStyle estiloFila = ExcelUtils.getEstiloFila(wb);
			HSSFCellStyle estiloFilaNumero = ExcelUtils.getEstiloFilaNumero(wb);
			
			for (cont=0; cont<listEntidadesOperadores.size(); cont++){
				HSSFRow row     = sheet.createRow(cont+2); 
					
								
				//C贸digo entidad-subentidad
				cell = row.createCell(0);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if ((listEntidadesOperadores.get(cont).getCodEntMed()!= null && listEntidadesOperadores.get(cont).getCodSubMed()!= null)){			
					cell.setCellValue(listEntidadesOperadores.get(cont).getCodEntMed()+"-"+
							listEntidadesOperadores.get(cont).getCodSubMed());					
				}		
				
				//CAJA RURAL
				cell = row.createCell(1);
				cell.setCellStyle(estiloFila);
				if (listEntidadesOperadores.get(cont).getCodEntMed() != null){					
					String cod = listEntidadesOperadores.get(cont).getCodEntMed().toString();									
					String desc = entMed.get(cod);
					desc = (desc != null) ? desc.trim(): "";
					cell.setCellValue(new HSSFRichTextString(desc));					
				}
				
				//FASES
				cell = row.createCell(2);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				if (listEntidadesOperadores.get(cont).getFases()!= null){
					cell.setCellValue(listEntidadesOperadores.get(cont).getFases().doubleValue());					
					// Suma de todas las Fases
					totalFase = totalFase.add(listEntidadesOperadores.get(cont).getFases());
				}
				
				//COOP.
				cell = row.createCell(3);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				//Si el pago directo es distinto de 1, significa que la ent-subent no tiene el campo "pagoDirecto" marcado
				if (!StringUtils.nullToString(listEntidadesOperadores.get(cont).getPagoDirecto()).equals("1")){
				
					cell.setCellValue(listEntidadesOperadores.get(cont).getCooperativas().doubleValue());
					totalCoop = totalCoop.add(listEntidadesOperadores.get(cont).getCooperativas());
				}else{
					cell.setCellValue(new BigDecimal(0).doubleValue());
					totalCoop = totalCoop.add(new BigDecimal(0));
				}
				//suma de de todas las coop
				
				
				//IMPAGADOS
				cell = row.createCell(4);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero); 
				if (listEntidadesOperadores.get(cont).getImpagados()!= null){
					cell.setCellValue(listEntidadesOperadores.get(cont).getImpagados().doubleValue());					
					//suma de todos los impagados
					totalImp = totalImp.add(listEntidadesOperadores.get(cont).getImpagados());
				}
				
				//TOTAL FILA
				cell = row.createCell(5);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				totalFila = new BigDecimal(listEntidadesOperadores.get(cont).getFases().doubleValue()+ 
										   listEntidadesOperadores.get(cont).getCooperativas().doubleValue() +
										   listEntidadesOperadores.get(cont).getImpagados().doubleValue());
				
				cell.setCellValue(totalFila.doubleValue());
				cell.setCellStyle(estiloFilaNumero);
				
			}
			
			if (cont > 0){
				HSSFRow row = sheet.createRow(cont+2);
				HSSFCellStyle estiloTotales = ExcelUtils.getEstiloFilaTotales(wb);
				HSSFCellStyle estiloFilaTotalesNumero = ExcelUtils.getEstiloFilaTotalesNumero(wb);
				
				cell = row.createCell(1);
				cell.setCellValue(new HSSFRichTextString(TOTAL));
				cell.setCellStyle(estiloTotales);
				
				cell = row.createCell(2);
				cell.setCellValue(totalFase.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
				cell = row.createCell(3);
				cell.setCellValue(totalCoop.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
				cell = row.createCell(4);
				cell.setCellValue(totalImp.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
				cell = row.createCell(5);
				totalTotal = totalTotal.add(totalFase).add(totalCoop).add(totalImp);
				cell.setCellValue(totalTotal.doubleValue());	
				cell.setCellStyle(estiloFilaTotalesNumero);
			}			
			
			ExcelUtils.autoajustarColumnas(sheet, cell.getColumnIndex() + 1);
			
			wb.write(fileOut);
			
			informesExcelDao.saveFicheroExcelCierre(nombreFichero.toString(),
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString(), idCierre, usuario);
			
			fileOut.close();
		} catch (FileNotFoundException e) {
			logger.error(PERMISO_CARPETA_TEMP + e.getMessage());
			throw new BusinessException (PERMISO_CARPETA_TEMP , e);
		} catch (IOException e) {
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + e.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , e);
		} catch (DAOException dao){
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + dao.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , dao);
		}			
	}
	
	@SuppressWarnings("rawtypes")
	public void generarInformeMediadores(List<InformeMediadores> listInformesMediadores, final Long idCierre, final Date fechaCierre, Usuario usuario) throws BusinessException {
		logger.debug("init - generarInformeEntidadesMeidadoras");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaCierre);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		StringBuilder nombreFichero = new StringBuilder("InformeEntidadesMediadoras_");
		nombreFichero.append(year);
		nombreFichero.append(month < 10 ? "0" + month : month);
		nombreFichero.append(".xls");
		
		BigDecimal codEntidad = null;
		BigDecimal codSubentidad = null;
		BigDecimal saldo = null;
		String descripcion = null;
		InformeMediadoresSaldos informeMediadorSaldo = null;
		InformeMediadoresMeses informeMediadorMes = null;
		InformeMediadores informeMediador = null;
		Set<InformeMediadoresMeses> mediadoresMeses = null;		
		double totalAnoLinea = 0.0;
		double totalAPagarLinea = 0.0;
		double totalRetencionLinea = 0.0;
		double acumuladoSaldos = 0.0;
		double acumuladoDevengo = 0.0;
		double acumuladoPago = 0.0;
		double acumuladoRetencion = 0.0;
		double acumuladoTotalAno = 0.0;
		double acumuladoTotalPagar = 0.0;
		double acumuladoTotalRetencion = 0.0;
		Iterator it = null;		
		
		try {
			//Creamos el fichero Excel		
			HSSFCell cell = null;
			HSSFRow row = null;
			HSSFWorkbook book = new HSSFWorkbook();
			HSSFSheet sheet = book.createSheet();
			sheet =  book.getSheetAt(0);
			CellRangeAddress region = null;
			HSSFCellStyle styleTotal = ExcelUtils.getEstiloFilaTotalesNumero(book);
			BigDecimal nm = informesExcelDao.getNumeroMaximoMes(fechaCierre);
			int numMeses = nm.intValue();
			int rowIndex = 0;				
			
			
			//Insertamos la cabecera addMergedRegion(int rowFrom, short colFrom, int rowTo, short colTo)
			row = sheet.createRow(rowIndex);
			HSSFCellStyle estiloCabecera = ExcelUtils.getEstiloCabecera(book);
			estiloCabecera.setWrapText(true);
			
			cell = row.createCell(0);
			cell.setCellValue(new HSSFRichTextString("SEGUROS GENERALES RURAL S.A. CTA. 410000002"));
			cell.setCellStyle(estiloCabecera);
			cell = row.createCell(cell.getColumnIndex() + 1);
			cell.setCellStyle(estiloCabecera);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			nm = new BigDecimal(sdf.format(fechaCierre));
			
			cell = row.createCell(2);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString((nm.subtract(new BigDecimal(1)).toString())));			
			cell = row.createCell(cell.getColumnIndex()+1);
			
			//For con los meses que saquemos de los periodos para poner devengo y pago
			for (int i = 1; i < numMeses+1; i++) {
				
				cell.setCellValue(new HSSFRichTextString(DateUtil.getNombreMes(i)));
				cell.setCellStyle(estiloCabecera);
				region = new CellRangeAddress(0, 0, cell.getColumnIndex(), cell.getColumnIndex()+1);
				sheet.addMergedRegion(region);
				cell = row.createCell(cell.getColumnIndex()+2);				
			}

			//Tenemos que realizar el for de nuevo para poner las retenciones
			for(int i=1; i < numMeses+1; i++) {
				cell.setCellValue(new HSSFRichTextString(DateUtil.getNombreMes(i)));
				cell.setCellStyle(estiloCabecera);
				cell = row.createCell(cell.getColumnIndex() + 1);				
			}
			
			//Insertamos las columnas de totales
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("TOTAL " + nm.toString() + " NUESTRO" ));
			cell.setCellStyle(estiloCabecera);			
			cell = row.createCell(cell.getColumnIndex() + 1);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("TOTAL A PAGAR CONTABILIDAD"));
			cell.setCellStyle(estiloCabecera);			
			cell = row.createCell(cell.getColumnIndex() + 1);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("TOTAL RETENCION"));
			cell.setCellStyle(estiloCabecera);	
			
			rowIndex++;
			
			//Insertamos la segunda parte de la cabecera:			
			row = sheet.createRow(rowIndex);
			region = new CellRangeAddress(0, 1, 0, 1);
			sheet.addMergedRegion(region);
			cell = row.createCell(0);
			cell.setCellStyle(estiloCabecera);
			cell = row.createCell(cell.getColumnIndex() + 1);
			cell.setCellStyle(estiloCabecera);
			cell = row.createCell(2);
			cell.setCellValue(new HSSFRichTextString("SALDO"));
			cell.setCellStyle(estiloCabecera);
			for(int i=0; i < (numMeses); i++) {
				cell = row.createCell(cell.getColumnIndex() + 1);
				cell.setCellValue(new HSSFRichTextString("DEVENGO"));
				cell.setCellStyle(estiloCabecera);
				cell = row.createCell(cell.getColumnIndex() + 1);
				cell.setCellValue(new HSSFRichTextString("PAGO"));
				cell.setCellStyle(estiloCabecera);
			}
			for(int i=0; i < numMeses; i++) {
				cell = row.createCell(cell.getColumnIndex() + 1);
				cell.setCellValue(new HSSFRichTextString("RETENCION"));
				cell.setCellStyle(estiloCabecera);
			}
			
			//A帽adimos las columnas de los totales y unimos las celdas
			cell = row.createCell(cell.getColumnIndex() + 1);
			cell.setCellStyle(estiloCabecera);
			region = new CellRangeAddress(0, 1, cell.getColumnIndex(), cell.getColumnIndex());
			sheet.addMergedRegion(region);
			cell = row.createCell(cell.getColumnIndex() + 1);
			cell.setCellStyle(estiloCabecera);
			region = new CellRangeAddress(0, 1, cell.getColumnIndex(), cell.getColumnIndex());
			sheet.addMergedRegion(region);
			cell = row.createCell(cell.getColumnIndex() + 1);
			cell.setCellStyle(estiloCabecera);
			region = new CellRangeAddress(0, 1, cell.getColumnIndex(), cell.getColumnIndex());
			
			sheet.addMergedRegion(region);
			
			//Metemos los datos de la lista uno por uno en el Excel
			if (listInformesMediadores != null) {
				for(int i = 0; i < listInformesMediadores.size(); i++) {
					rowIndex++;
					row = sheet.createRow(rowIndex);
					boolean anadir = false;
					Set<InformeMediadoresMeses> s = listInformesMediadores.get(i).getInformeMediadoresMeseses();
					Iterator r = s.iterator();
					while (r.hasNext()) {
						InformeMediadoresMeses mes = (InformeMediadoresMeses) r.next();
						mes.getAnyo().equals(nm);
						anadir = true;
						break;
						
					}
					if (anadir) {
						informeMediador = listInformesMediadores.get(i);
						codEntidad = informeMediador.getSubentidadMediadora().getId().getCodentidad();
						codSubentidad = informeMediador.getSubentidadMediadora().getId().getCodsubentidad();
						descripcion = StringUtils.nullToString(informeMediador.getSubentidadMediadora().getNomSubentidadCompleto());
						cell = row.createCell(0);
						cell.setCellValue(new HSSFRichTextString(codEntidad + " - " + codSubentidad));
						cell = row.createCell(cell.getColumnIndex() +1 );
						cell.setCellValue(new HSSFRichTextString(descripcion));				
						cell = row.createCell(cell.getColumnIndex() +1 );				
						
						Set<InformeMediadoresSaldos> sInformeMediadoresSaldos = informeMediador.getInformeMediadoresSaldoses();
						it = sInformeMediadoresSaldos.iterator();
						while(it.hasNext()) {
							informeMediadorSaldo = (InformeMediadoresSaldos)it.next();
							if (informeMediadorSaldo.getAnyo().equals(nm)) {
								saldo = informeMediadorSaldo.getSaldo();
								
								if(informeMediador.getNuevo() == '0') { //Se pone el saldo
									cell.setCellValue(saldo.doubleValue());					
									acumuladoSaldos += saldo.doubleValue();
								} else if (informeMediador.getNuevo() == '1') { // NUEVO
									cell.setCellValue(new HSSFRichTextString("NUEVO"));
								} else { //BAJA
									if(saldo.equals(new BigDecimal(0))) {
										cell.setCellValue(new HSSFRichTextString("BAJA"));
									} else {
										cell.setCellValue(new HSSFRichTextString("BAJA (" + saldo + ")"));
										acumuladoSaldos += saldo.doubleValue();
									}						
								}
							}
						}			
						
						//Obtengo la informacion de los meses del mediador
						mediadoresMeses = informeMediador.getInformeMediadoresMeseses();
						it = mediadoresMeses.iterator();
						totalAnoLinea = 0.0;				
						Map<BigDecimal, Object> mapaMeses = new HashMap<BigDecimal, Object>();
						
						while(it.hasNext()) {
							informeMediadorMes = (InformeMediadoresMeses) it.next();
							if (informeMediadorMes.getAnyo().equals(nm)) {
								mapaMeses.put(informeMediadorMes.getMes(), informeMediadorMes);
							}
						}
						
						totalAnoLinea = 0.0;
						totalAPagarLinea = 0.0;
						totalRetencionLinea = 0.0;
						for(int j = 1; j < numMeses+1; j++) {					
							if(mapaMeses.get(new BigDecimal(j))!= null) {
								informeMediadorMes = (InformeMediadoresMeses) mapaMeses.get(new BigDecimal(j));
								if(informeMediadorMes.getDevengo() != null) {
									cell = row.createCell(cell.getColumnIndex() + 1);
									cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									cell.setCellValue(informeMediadorMes.getDevengo().doubleValue());
									totalAnoLinea += informeMediadorMes.getDevengo().doubleValue();
									acumuladoTotalAno += informeMediadorMes.getDevengo().doubleValue();
								}
								if(informeMediadorMes.getPago() != null) {
									cell = row.createCell(cell.getColumnIndex() + 1);
									cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									cell.setCellValue(informeMediadorMes.getPago().doubleValue());
									totalAPagarLinea += informeMediadorMes.getPago().doubleValue();
									acumuladoTotalPagar += informeMediadorMes.getPago().doubleValue();
								}
							} else {
								cell = row.createCell(cell.getColumnIndex() + 2);
							}
						}
		
						//Colocamos las retenciones				
						for(int j = 1; j < numMeses+1; j++) { 
							cell = row.createCell(cell.getColumnIndex() + 1);
							if(mapaMeses.get(new BigDecimal(j))!= null) {
								informeMediadorMes = (InformeMediadoresMeses) mapaMeses.get(new BigDecimal(j));
								if(informeMediadorMes.getRetencion() != null) {							
									cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									cell.setCellValue(informeMediadorMes.getRetencion().doubleValue());
									totalRetencionLinea += informeMediadorMes.getRetencion().doubleValue() ;
									acumuladoTotalRetencion += informeMediadorMes.getRetencion().doubleValue();
								}						
							} 
						}				
										
						//Colocamos los totales
						cell = row.createCell(cell.getColumnIndex() + 1);
						cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						cell.setCellValue(totalAnoLinea);
						cell = row.createCell(cell.getColumnIndex() + 1);
						cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						cell.setCellValue(totalAPagarLinea);
						cell = row.createCell(cell.getColumnIndex() + 1);
						cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						cell.setCellValue(totalRetencionLinea);
					}
				}
			}
						
			//Pintamos la columna de saldos acumulados
			rowIndex++;
			row = sheet.createRow(rowIndex);
			cell = row.createCell(2);			
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(styleTotal);
			cell.setCellValue(acumuladoSaldos);			
			
			//Pintar la columna de devengo y pago acumulado
			for(int j = 1; j < numMeses+1; j++) { 
				acumuladoDevengo = 0.0;
				acumuladoPago = 0.0;
				for(int i = 0; i < listInformesMediadores.size(); i++) {
					informeMediador = listInformesMediadores.get(i);
					it = mediadoresMeses.iterator();	
					mediadoresMeses = informeMediador.getInformeMediadoresMeseses();
					Map<BigDecimal, Object> mapaMeses = new HashMap<BigDecimal, Object>();
					
					while(it.hasNext()) {
						informeMediadorMes = (InformeMediadoresMeses) it.next();
						mapaMeses.put(informeMediadorMes.getMes(), informeMediadorMes);
					}					
					
					if(mapaMeses.get(new BigDecimal(j)) != null) {
						informeMediadorMes = (InformeMediadoresMeses) mapaMeses.get(new BigDecimal(j));
						if(informeMediadorMes.getDevengo() != null) {
							acumuladoDevengo += informeMediadorMes.getDevengo().doubleValue();
						}
						if(informeMediadorMes.getPago() != null) {
							acumuladoPago += informeMediadorMes.getPago().doubleValue();
						}						
					}
				}		
				cell = row.createCell(cell.getColumnIndex() + 1);
				cell.setCellStyle(styleTotal);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(acumuladoDevengo);
				cell = row.createCell(cell.getColumnIndex() + 1);
				cell.setCellStyle(styleTotal);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(acumuladoPago);
			}			
			
			//Pintar la columna de retenciones acumuladas
			for(int j = 1; j < numMeses+1; j++) { 
				acumuladoRetencion = 0.0;
				for(int i = 0; i < listInformesMediadores.size(); i++) {
					informeMediador = listInformesMediadores.get(i);
					mediadoresMeses = informeMediador.getInformeMediadoresMeseses();
					it = mediadoresMeses.iterator();
					Map<BigDecimal, Object> mapaMeses = new HashMap<BigDecimal, Object>();					
					while(it.hasNext()) {
						informeMediadorMes = (InformeMediadoresMeses) it.next();
						mapaMeses.put(informeMediadorMes.getMes(), informeMediadorMes);
					}
					if(mapaMeses.get(new BigDecimal(j)) != null) {						
						informeMediadorMes = (InformeMediadoresMeses) mapaMeses.get(new BigDecimal(j));
						if(informeMediadorMes.getRetencion() != null) {
							acumuladoRetencion += informeMediadorMes.getRetencion().doubleValue();
						}						
					}
				}
				cell = row.createCell(cell.getColumnIndex() + 1);
				cell.setCellStyle(styleTotal);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(acumuladoRetencion);
			}
			
			//Pintar la columna de totales acumulados
			cell = row.createCell(cell.getColumnIndex() + 1);
			cell.setCellStyle(styleTotal);
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			cell.setCellValue(acumuladoTotalAno);
			cell = row.createCell(cell.getColumnIndex() + 1);
			cell.setCellStyle(styleTotal);
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			cell.setCellValue(acumuladoTotalPagar);
			cell = row.createCell(cell.getColumnIndex() + 1);
			cell.setCellStyle(styleTotal);
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			cell.setCellValue(acumuladoTotalRetencion);
			
			ExcelUtils.autoajustarColumnas(sheet, cell.getColumnIndex() + 1);
			
			//Escribimos el fichero Excel
			FileOutputStream fileOut = new FileOutputStream(
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString());
			book.write(fileOut);
			informesExcelDao.saveFicheroExcelCierre(nombreFichero.toString(),
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString(), idCierre, usuario);
			fileOut.close();

		} catch (FileNotFoundException e) {
			logger.error(PERMISO_CARPETA_TEMP + e.getMessage());
			throw new BusinessException (PERMISO_CARPETA_TEMP , e);
		} catch (IOException e) {
			logger.error("Se ha producido un error al guardar el fichero Excel generado: " + e.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL2, e);
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al guardar el fichero Excel generado :" + dao.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL2, dao);
		}
	
	}
	
	public void generarInformeMediadores2015(List<RgaUnifMediadores> listInformesMediadores2015, final Long idCierre, final Date fechaCierre, Usuario usuario, 
			HashMap<String,String> subEntMed, HashMap<String, SubentidadMediadora> mapSubEnt, boolean segGen) throws BusinessException {
		String nombreFichero = "";
		if(segGen) {
			logger.debug("init - generarInformeMediadores2015 # SegurosGenereales");
			nombreFichero = bundle.getString(RUTA_TEMP_INF_COM)+"/MediadoresSegurosGenerales.xls";
		}else {
			logger.debug("init - generarInformeMediadores2015 # MediadoresRGA");
			nombreFichero = bundle.getString(RUTA_TEMP_INF_COM)+"/MediadoresRGA.xls";
		}
		
		BigDecimal codEntidad = null;
		BigDecimal codSubentidad = null;
		String anno = "";
		String descSubentidad = "";
		RgaUnifMediadores unifMedia = null;	
		double totalReal = 0.0;
		double totalRetencion = 0.0;
		double totalLiquid = 0.0;
		double acumuladoSaldos = 0.0;		
		double acumuladoReal = 0.0;
		double acumuladoRet = 0.0;
		double acumuladoLiquid = 0.0;	
		double acumuladoTotalReal = 0.0;
		double acumuladoTotalRet = 0.0;
		double acumuladoTotalLiquid = 0.0;

		try {
			//Creamos el fichero Excel		
			HSSFCell cell = null;
			HSSFRow row = null;
			HSSFWorkbook book = new HSSFWorkbook();
			HSSFSheet sheet = book.createSheet();
			sheet =  book.getSheetAt(0);
			CellRangeAddress region = null;
			HSSFCellStyle styleTotal = ExcelUtils.getEstiloFilaTotalesNumero(book);
			BigDecimal nm = informesExcelDao.getNumeroMaximoMes2015(fechaCierre);
			Map<String, List<RgaUnifMediadores>> mapMesesEnt = new HashMap<String, List<RgaUnifMediadores>>();
			int numMeses = nm.intValue();
			int rowIndex = 0;
			HSSFCellStyle estiloCabecera    	 = ExcelUtils.getEstiloCabecera(book);			
			HSSFCellStyle estiloCabAmarillo      = ExcelUtils.getEstiloCabAmarillo(book);
			HSSFCellStyle estiloCabAmarilloClaro = ExcelUtils.getEstiloCabAmarilloClaro(book);
			HSSFCellStyle estiloCabAmarilloRojo  = ExcelUtils.getEstiloCabAmarilloRojo(book);
			HSSFCellStyle estiloCabAzul 		 = ExcelUtils.getEstiloCabAzul(book);			
			HSSFCellStyle estiloCabAzulClaro 	 = ExcelUtils.getEstiloCabAzulClaro(book);			
			HSSFCellStyle estiloCabVerde 		 = ExcelUtils.getEstiloCabVerde(book);
			HSSFCellStyle estiloCabVerdeMorado   = ExcelUtils.getEstiloCabVerdeMorado(book);
			HSSFCellStyle estiloCabVerdeClaro    = ExcelUtils.getEstiloCabVerdeClaro(book);			
			HSSFCellStyle estiloCabRojo 		 = ExcelUtils.getEstiloCabRojo(book);
			HSSFCellStyle estiloCabGris 		 = ExcelUtils.getEstiloCabGris(book);
			HSSFCellStyle estiloTextoRojo 		 = ExcelUtils.getEstiloTextoRojo(book);
			HSSFCellStyle estiloTextoGris 	     = ExcelUtils.getEstiloTextoGris(book);
			HSSFCellStyle estiloTextoMorado 	 = ExcelUtils.getEstiloTextoMorado(book);
			HSSFCellStyle estiloTextoVerde 	     = ExcelUtils.getEstiloTextoVerde(book);
			HSSFCellStyle estiloTextoAzulClaro   = ExcelUtils.getEstiloTextoAzulClaro(book);
			HSSFCellStyle estiloTextoNegro 		 = ExcelUtils.getEstiloTextoNegro(book);
			HSSFCellStyle estiloRosa 			 = ExcelUtils.getEstiloRosa(book);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			nm = new BigDecimal(sdf.format(fechaCierre));
			anno = nm.subtract(new BigDecimal(1)).toString();
			
			// TITULO						
			row = sheet.createRow(rowIndex);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, (6+numMeses*3)));
			HSSFRow titulo = sheet.createRow(0);					
			cell = titulo.createCell(0);
			cell.setCellStyle(estiloRosa);
			cell.setCellValue(new HSSFRichTextString("COMISIONES "+nm));
			rowIndex++;

			//Insertamos la cabecera FILA 1
			row = sheet.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellValue(new HSSFRichTextString("SEGUROS GENERALES RURAL S.A. CTA. 410000002"));  
			cell.setCellStyle(estiloCabVerdeClaro);
			cell = row.createCell(cell.getColumnIndex() + 1); // ch +2
			cell.setCellStyle(estiloCabVerdeClaro);//#COLOR#

			cell = row.createCell(2); // ch +3
			cell.setCellStyle(estiloCabRojo); //#COLOR#
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);		
			cell.setCellValue(new BigDecimal(anno).doubleValue()); // ch INSERTAMOS EL A袿 EN EL SALDO		
			cell = row.createCell(cell.getColumnIndex()+1);
						
			//For con los meses que saquemos de los periodos para poner real, retencion y liquido
			for (int i = 1; i < numMeses+1; i++) {
		
				cell.setCellValue(new HSSFRichTextString(DateUtil.getNombreMes(i).toUpperCase()));		
				region = new CellRangeAddress(1, 1, cell.getColumnIndex(), cell.getColumnIndex()+2); // ch +1
				sheet.addMergedRegion(region);
				cell.setCellStyle(estiloCabVerdeClaro);
				cell = row.createCell(cell.getColumnIndex()+1);
				cell.setCellStyle(estiloCabVerdeClaro);
				cell = row.createCell(cell.getColumnIndex()+1);
				cell.setCellStyle(estiloCabVerdeClaro);
				cell = row.createCell(cell.getColumnIndex()+1);
			}

			//Insertamos las columnas de Totales
			cell.setCellStyle(estiloCabAzul); //#COLOR#
			cell.setCellValue(new HSSFRichTextString("TOTAL " + nm.toString() + " REAL" ));
			cell.setCellStyle(estiloCabAzul);	//#COLOR#		
			cell = row.createCell(cell.getColumnIndex() + 1);
			
			cell.setCellStyle(estiloCabVerde); //#COLOR#
			cell.setCellValue(new HSSFRichTextString("TOTAL A PAGAR LIQUIDA"));
			cell.setCellStyle(estiloCabVerde);	 //#COLOR#		
			cell = row.createCell(cell.getColumnIndex() + 1);
			
			cell.setCellStyle(estiloCabAmarillo); //#COLOR#
			cell.setCellValue(new HSSFRichTextString("TOTAL RETENCI覰"));
			cell.setCellStyle(estiloCabAmarillo);//#COLOR#
			cell = row.createCell(cell.getColumnIndex() + 1);
			
			//Insertamos IBAN
			cell.setCellStyle(estiloCabAzulClaro);  //#COLOR#
			cell.setCellValue(new HSSFRichTextString("CTA.CORRIENTE"));
			cell.setCellStyle(estiloCabAzulClaro);	 //#COLOR#
			cell = row.createCell(cell.getColumnIndex() + 1);

			rowIndex++;
			//Insertamos la cabecera FILA 2
			row = sheet.createRow(rowIndex);
			
			region = new CellRangeAddress(1, 2, 0, 1);
			sheet.addMergedRegion(region);
			
			cell = row.createCell(0);	
			cell.setCellStyle(estiloCabVerdeClaro);
			
			cell = row.createCell(cell.getColumnIndex() + 1);
			cell.setCellStyle(estiloCabVerdeClaro);

			cell = row.createCell(2);			
			cell.setCellValue(new HSSFRichTextString("SALDO"));
			cell.setCellStyle(estiloCabRojo);	 //#COLOR#
			
			for(int i=0; i < (numMeses); i++) {
				cell = row.createCell(cell.getColumnIndex() + 1);
				cell.setCellValue(new HSSFRichTextString("REAL"));
				cell.setCellStyle(estiloCabAzul); //#COLOR#
				cell = row.createCell(cell.getColumnIndex() + 1);
				cell.setCellValue(new HSSFRichTextString("RETENCI覰"));
				cell.setCellStyle(estiloCabAmarilloRojo); //#COLOR#
				cell = row.createCell(cell.getColumnIndex() + 1);
				cell.setCellValue(new HSSFRichTextString("LIQUIDA"));
				cell.setCellStyle(estiloCabVerdeMorado); //#COLOR#
			}
			
			//Anadimos las columnas de los totales y unimos las celdas
			cell = row.createCell(cell.getColumnIndex() + 1);
			cell.setCellStyle(estiloCabecera);
			region = new CellRangeAddress(1, 2, cell.getColumnIndex(), cell.getColumnIndex());
			sheet.addMergedRegion(region);
			cell = row.createCell(cell.getColumnIndex() + 1);
			cell.setCellStyle(estiloCabecera);
			region = new CellRangeAddress(1, 2, cell.getColumnIndex(), cell.getColumnIndex());
			sheet.addMergedRegion(region);
			cell = row.createCell(cell.getColumnIndex() + 1);
			cell.setCellStyle(estiloCabecera);
			region = new CellRangeAddress(1, 2, cell.getColumnIndex(), cell.getColumnIndex());
			sheet.addMergedRegion(region);
			
			// PARA EL IBAN unimos las celdas tb
			cell = row.createCell(cell.getColumnIndex() + 1);
			cell.setCellStyle(estiloCabecera);
			region = new CellRangeAddress(1, 2, cell.getColumnIndex(), cell.getColumnIndex());
			sheet.addMergedRegion(region);
			
			
			//Metemos los datos de la lista uno por uno en el Excel
			if (listInformesMediadores2015 != null) {				
				Map<BigDecimal, Object> mapaMeses = new HashMap<BigDecimal, Object>();			
				// recogemos un mapa de key: ent-subent, lista de infMed
				String E_S_anterior ="";
				String E_S = "";
				boolean nuevo = true;
				List<RgaUnifMediadores> lstM = new ArrayList<RgaUnifMediadores>();
				for(int x = 0; x < listInformesMediadores2015.size(); x++) {					
					E_S = listInformesMediadores2015.get(x).getEntidad().toString()+"-"+listInformesMediadores2015.get(x).getSubentidad().toString();
					if (nuevo) {
						mapMesesEnt.put(E_S, null);
						lstM.add(listInformesMediadores2015.get(x));
						nuevo = false;
					}else {	
						if (!mapMesesEnt.containsKey(E_S)) {
							mapMesesEnt.put(E_S_anterior, lstM);
							lstM = new ArrayList<RgaUnifMediadores>();
							mapMesesEnt.put(E_S, null);
							lstM.add(listInformesMediadores2015.get(x));
						}else {
							lstM.add(listInformesMediadores2015.get(x));
						}
					}
					E_S_anterior = E_S;
				}
				
				E_S_anterior ="";
				nuevo = true;
				for(int i = 0; i < listInformesMediadores2015.size(); i++) {					
					RgaUnifMediadores infMed = listInformesMediadores2015.get(i);
					codEntidad    = new BigDecimal(infMed.getEntidad().toString());
					codSubentidad = new BigDecimal(infMed.getSubentidad());					
					String key    = codEntidad+"-"+codSubentidad;
					if (nuevo || !E_S_anterior.equalsIgnoreCase(key)){
				    	rowIndex++;
				    	totalReal = 0.0;
						totalRetencion = 0.0;
						totalLiquid = 0.0;	
				    	nuevo = false;    
				    }
					E_S_anterior = key;
					row = sheet.createRow(rowIndex);
					boolean antiguo = false;
					codEntidad = new BigDecimal(infMed.getEntidad().toString());
					codSubentidad = new BigDecimal(infMed.getSubentidad());								
					descSubentidad = subEntMed.get(key);					
					descSubentidad = (descSubentidad != null) ? descSubentidad.trim(): "";
					
					cell = row.createCell(0);
					cell.setCellStyle(estiloTextoNegro); //#COLOR#
					cell.setCellValue(new HSSFRichTextString(codEntidad + " - " + codSubentidad));
					
					cell = row.createCell(cell.getColumnIndex() +1 );
					cell.setCellStyle(estiloTextoGris); //#COLOR#
					cell.setCellValue(new HSSFRichTextString(descSubentidad));				
					
					// pintamos el fondo de la celda Saldo en gris
					cell = row.createCell(cell.getColumnIndex() +1 );				
					cell.setCellStyle(estiloCabGris); //#COLOR#
				
					for(int j = 1; j < numMeses+1; j++) {					
						if (infMed.getMes().equals(j)) {
							if(infMed.getReal() != null) {								
								cell = row.createCell(cell.getColumnIndex() + 1);
								cell.setCellStyle(estiloTextoGris);
								cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
								cell.setCellValue(infMed.getReal().doubleValue());
								totalReal += infMed.getReal().doubleValue();
								acumuladoTotalReal += infMed.getReal().doubleValue(); // acumulado real
							}
							if(infMed.getRetencion() != null) {
								cell = row.createCell(cell.getColumnIndex() + 1);
								cell.setCellStyle(estiloTextoRojo);
								cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
								cell.setCellValue(infMed.getRetencion().doubleValue());
								totalRetencion += infMed.getRetencion().doubleValue();
								acumuladoTotalRet += infMed.getRetencion().doubleValue(); // acum retencion
							}
							if(infMed.getLiquido() != null) {
								cell = row.createCell(cell.getColumnIndex() + 1);
								cell.setCellStyle(estiloTextoMorado);
								cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
								cell.setCellValue(infMed.getLiquido().doubleValue());
								totalLiquid += infMed.getLiquido().doubleValue();
								acumuladoTotalLiquid += infMed.getLiquido().doubleValue(); // acum liquido
							}
						} else {
							// buscamos los valores de otros meses de la misma ent-subent y los pintamos para que no se machaquen
							lstM =  mapMesesEnt.get(key);
							// recorremos lista
							antiguo = false;
							if (lstM != null) {
								for(RgaUnifMediadores uni : lstM){	
									if (uni.getMes().equals(j)){
										if(uni.getReal() != null) {								
											cell = row.createCell(cell.getColumnIndex() + 1);
											cell.setCellStyle(estiloTextoGris);
											cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
											cell.setCellValue(uni.getReal().doubleValue());
										}
										if(uni.getRetencion() != null) {
											cell = row.createCell(cell.getColumnIndex() + 1);
											cell.setCellStyle(estiloTextoRojo);
											cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
											cell.setCellValue(uni.getRetencion().doubleValue());
										}
										if(uni.getLiquido() != null) {
											cell = row.createCell(cell.getColumnIndex() + 1);
											cell.setCellStyle(estiloTextoMorado);
											cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
											cell.setCellValue(uni.getLiquido().doubleValue());
										}
										antiguo = true;
									}
								}
							}
							if (!antiguo) {
								cell = row.createCell(cell.getColumnIndex() + 3); // ch +2
							}						
						}
					}
							
					//Colocamos los totales
					cell = row.createCell(cell.getColumnIndex() + 1);
					cell.setCellStyle(estiloTextoVerde);
					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					cell.setCellValue(totalReal);
					cell = row.createCell(cell.getColumnIndex() + 1);
					cell.setCellStyle(estiloTextoAzulClaro);
					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					cell.setCellValue(totalRetencion);
					cell = row.createCell(cell.getColumnIndex() + 1);
					cell.setCellStyle(estiloTextoMorado);
					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					cell.setCellValue(totalLiquid);
									
					// PINTAMOS IBAN
					cell = row.createCell(cell.getColumnIndex() + 1);
					SubentidadMediadora subEnt = (SubentidadMediadora) mapSubEnt.get(key);
					if (subEnt != null && subEnt.getIban() !=null)
						cell.setCellValue(new HSSFRichTextString(subEnt.getIban()));
					cell.setCellStyle(estiloCabAmarilloClaro); //#COLOR#
					cell = row.createCell(cell.getColumnIndex() + 1);
			} // FIN (listInformesMediadores2015 != null)

			//Pintamos la columna de saldos acumulados GRISES
			rowIndex++;
			row = sheet.createRow(rowIndex);
			
			// TEXTO TOTAL
			cell = row.createCell(1);
			cell.setCellValue(new HSSFRichTextString(TOTAL));
			cell.setCellStyle(styleTotal);
			
			cell = row.createCell(2);			
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(styleTotal);
			cell.setCellValue(acumuladoSaldos);			
			
			//Pintar la columna de real, retencion y liquido acumulado
			for(int j = 1; j < numMeses+1; j++) { 
				acumuladoReal = 0.0;
				acumuladoRet = 0.0;
				acumuladoLiquid = 0.0;

				for(int i = 0; i < listInformesMediadores2015.size(); i++) {
					unifMedia = listInformesMediadores2015.get(i);
					mapaMeses = new HashMap<BigDecimal, Object>();
					mapaMeses.put(new BigDecimal(unifMedia.getMes().toString()), unifMedia);
							
					
					if(mapaMeses.get(new BigDecimal(j)) != null) {
						if(unifMedia.getReal() != null) {
							acumuladoReal += unifMedia.getReal().doubleValue();
						}
						if(unifMedia.getRetencion() != null) {
							acumuladoRet += unifMedia.getRetencion().doubleValue();
						}
						if(unifMedia.getLiquido() != null) {
							acumuladoLiquid += unifMedia.getLiquido().doubleValue();
						}
					}
				}		
				cell = row.createCell(cell.getColumnIndex() + 1);
				cell.setCellStyle(styleTotal);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(acumuladoReal);
				cell = row.createCell(cell.getColumnIndex() + 1);
				cell.setCellStyle(styleTotal);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(acumuladoRet);
				cell = row.createCell(cell.getColumnIndex() + 1);
				cell.setCellStyle(styleTotal);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(acumuladoLiquid);
			}			

			//Pintar la columna de totales acumulados
			cell = row.createCell(cell.getColumnIndex() + 1); // ch quitada
			cell.setCellStyle(styleTotal);
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			cell.setCellValue(acumuladoTotalReal);
			cell = row.createCell(cell.getColumnIndex() + 1);
			cell.setCellStyle(styleTotal);
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			cell.setCellValue(acumuladoTotalRet);
			cell = row.createCell(cell.getColumnIndex() + 1);
			cell.setCellStyle(styleTotal);
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			cell.setCellValue(acumuladoTotalLiquid);
			cell = row.createCell(cell.getColumnIndex() + 1);
						
			ExcelUtils.autoajustarColumnas(sheet, cell.getColumnIndex() + 1);
			
			//Escribimos el fichero Excel
			FileOutputStream fileOut = new FileOutputStream(nombreFichero);
			book.write(fileOut);
					
			if(segGen) {
				informesExcelDao.saveFicheroExcelCierre("MediadoresSegurosGenerales.xls", nombreFichero, idCierre, usuario);
				logger.debug("end - generarInformeMediadores2015 # SegurosGenereales");
			}else {
				informesExcelDao.saveFicheroExcelCierre("MediadoresRGA.xls", nombreFichero, idCierre, usuario);
				logger.debug("init - generarInformeMediadores2015 # MediadoresRGA");
			}
	
			fileOut.close();
			}		
		} catch (FileNotFoundException e) {
			logger.error(PERMISO_CARPETA_TEMP + e.getMessage());
			throw new BusinessException (PERMISO_CARPETA_TEMP , e);
		} catch (IOException e) {
			logger.error("Se ha producido un error al guardar el fichero Excel  generado:" + e.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL2, e);
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al guardar el fichero  Excel generado:" + dao.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL2, dao);
		}
	
	}
		
	public void generarTotalesMediador(List<RgaComisiones> listTotalesMediador,HashMap<String,String> lineas,
			HashMap<String,String> subEntMed, final Long idCierre, final Date fechaCierre, Usuario usuario) throws BusinessException {
		logger.debug("init - generarTotalesMediador");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaCierre);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		StringBuilder nombreFichero = new StringBuilder("InformeTotalesMediador_");
		nombreFichero.append(year);
		nombreFichero.append(month < 10 ? "0" + month : month);
		nombreFichero.append(".xls");
		
		int cont = 0;
		
		BigDecimal totalPrimSum = new BigDecimal(0);
		BigDecimal totalComAgen = new BigDecimal(0);
		
		try {		
		
			HSSFWorkbook wb          = new HSSFWorkbook();
			// ruta temporal
			FileOutputStream fileOut = new FileOutputStream(
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString());
		
			HSSFSheet sheet = wb.createSheet();
			sheet = wb.getSheetAt(0);
			
			HSSFCell cell;		
			
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));
			
			HSSFCellStyle estiloTitulo = ExcelUtils.getEstiloTitulo(wb);
			HSSFRow titulo = sheet.createRow(0);		
			
			cell = titulo.createCell(0);		
			
			cell.setCellStyle(estiloTitulo);
			cell.setCellValue(new HSSFRichTextString(getMesByCierre(fechaCierre)));	
						
			//Se insertan las cabeceras
			HSSFRow cabecera = sheet.createRow(1); //new HSSFRichTextString("CODENT")
			HSSFCellStyle estiloCabecera = ExcelUtils.getEstiloCabecera(wb);
			cell = cabecera.createCell(0);
			cell.setCellValue(new HSSFRichTextString("MEDIADOR"));
			cell.setCellStyle(estiloCabecera);			
			
			cell = cabecera.createCell(1);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("FASE"));
			
			cell = cabecera.createCell(2);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("PLAN"));
			
			cell = cabecera.createCell(3);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString(LINEA));
			
			cell = cabecera.createCell(4);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString(COLECTIVO));
			
			cell = cabecera.createCell(5);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("REF. POLIZA"));
			
			cell = cabecera.createCell(6);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("NIF ASG."));
			
			cell = cabecera.createCell(7);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("ASEGURADO"));
			
			cell = cabecera.createCell(8);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("PRIMA BASE"));
			
			cell = cabecera.createCell(9);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("COMISION AGENTE"));
			
			HSSFCellStyle estiloFila = ExcelUtils.getEstiloFila(wb);
			HSSFCellStyle estiloFilaNumero = ExcelUtils.getEstiloFilaNumero(wb);
			
			for (cont=0; cont<listTotalesMediador.size(); cont++){
				HSSFRow row     = sheet.createRow(cont+2); 
					
				
				
				//C贸digo entidad mediadiora- codigo subentidad mediadora
				cell = row.createCell(0);
				cell.setCellStyle(estiloFila);
				if ((listTotalesMediador.get(cont).getCodentmed() != null) && (listTotalesMediador.get(cont).getCodsubmed() != null)){					
					String cod = listTotalesMediador.get(cont).getCodentmed().intValue()
									+"-"+listTotalesMediador.get(cont).getCodsubmed().intValue();
					String desc = subEntMed.get(cod);
					desc = (desc != null) ? desc.trim(): "";
					cell.setCellValue(new HSSFRichTextString(cod+" "+desc));					
				}
				
				//FASE
				cell = row.createCell(1);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if ((listTotalesMediador.get(cont).getNumfas()!= null)){
					cell.setCellValue(Double.parseDouble(listTotalesMediador.get(cont).getNumfas()));					
				}		
				//PLAN
				cell = row.createCell(2);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (listTotalesMediador.get(cont).getCodpln() != null){	
					cell.setCellValue(listTotalesMediador.get(cont).getCodpln().doubleValue());					
				}
				//LINEA
				cell = row.createCell(3);
				cell.setCellStyle(estiloFila);
				if (listTotalesMediador.get(cont).getCodlin() != null){
					String desc = lineas.get(listTotalesMediador.get(cont).getCodlin().toString());					
					cell.setCellValue(new HSSFRichTextString(listTotalesMediador.get(cont).getCodlin() + "-" + desc));					
				}
				
				//COLECTIVO
				cell = row.createCell(4);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (listTotalesMediador.get(cont).getCodcol() != null){
					cell.setCellValue(Integer.parseInt(listTotalesMediador.get(cont).getCodcol()));					
				}
				
				//REF.POLIZA
				cell = row.createCell(5);
				cell.setCellStyle(estiloFila); 
				if ((listTotalesMediador.get(cont).getRefplz()!= null)){
					cell.setCellValue(new HSSFRichTextString(listTotalesMediador.get(cont).getRefplz()));					
				}
				//NIF. ASEG.
				cell = row.createCell(6);
				cell.setCellStyle(estiloFila); 
				if ((listTotalesMediador.get(cont).getNifasg()!= null)){
					cell.setCellValue(new HSSFRichTextString(listTotalesMediador.get(cont).getNifasg()));					
				}
				//ASEGURADO
				cell = row.createCell(7);
				cell.setCellStyle(estiloFila); 
				if ((listTotalesMediador.get(cont).getNomasg()!= null)){
					cell.setCellValue(new HSSFRichTextString(listTotalesMediador.get(cont).getNomasg()));					
				}
				//PRIMA BASE
				cell = row.createCell(8);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero); 
				if ((listTotalesMediador.get(cont).getPrisum()!= null)){
					cell.setCellValue(listTotalesMediador.get(cont).getPrisum().doubleValue());	
					totalPrimSum = totalPrimSum.add(listTotalesMediador.get(cont).getPrisum());
				}
				//COMISION AGENTE
				cell = row.createCell(9);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero); 
				BigDecimal ggeMed = StringUtils.nullToZero(listTotalesMediador.get(cont).getGassubsum());
				BigDecimal ccMed = StringUtils.nullToZero(listTotalesMediador.get(cont).getComsubsum());
				BigDecimal comisionAgente = ggeMed.add(ccMed);
				cell.setCellValue(comisionAgente.doubleValue());		
				totalComAgen = totalComAgen.add(comisionAgente);
				
			}
			
			if (cont > 0){
				HSSFRow row = sheet.createRow(cont+2);
				HSSFCellStyle estiloTotales = ExcelUtils.getEstiloFilaTotales(wb);
				HSSFCellStyle estiloFilaTotalesNumero = ExcelUtils.getEstiloFilaTotalesNumero(wb);
				
				cell = row.createCell(7);
				cell.setCellValue(new HSSFRichTextString(TOTAL));
				cell.setCellStyle(estiloTotales);
				
				cell = row.createCell(8);
				cell.setCellValue(totalPrimSum.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
				cell = row.createCell(9);
				cell.setCellValue(totalComAgen.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
			}			
			
			ExcelUtils.autoajustarColumnas(sheet, cell.getColumnIndex() + 1);
			
			wb.write(fileOut);
			
			informesExcelDao.saveFicheroExcelCierre(nombreFichero.toString(),
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString(), idCierre, usuario);
			
			fileOut.close();
		} catch (FileNotFoundException e) {
			logger.error(PERMISO_CARPETA_TEMP + e.getMessage());
			throw new BusinessException (PERMISO_CARPETA_TEMP , e);
		} catch (IOException e) {
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + e.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , e);
		} catch (DAOException dao){
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + dao.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , dao);
		}			
	}
	
	public IInformesExcelDao getInformesExcelDao() {
		return informesExcelDao;
	}

	public void setInformesExcelDao(IInformesExcelDao informesExcelDao) {
		this.informesExcelDao = informesExcelDao;
	}
	public void setSubentidadMediadoraDao(ISubentidadMediadoraDao subentidadMediadoraDao) {
		this.subentidadMediadoraDao = subentidadMediadoraDao;
	}
	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}
	public void setColectivoDao(IColectivoDao colectivoDao) {
		this.colectivoDao = colectivoDao;
	}
	
	public void generarTotalesMediador2015(List<InformeTotMediador2015> lstTotMed2015,HashMap<String,String> lineas,
			final Long idCierre, final Date fechaCierre, Usuario usuario) throws BusinessException {
		logger.debug("init - generarTotalesMediador2015");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaCierre);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		StringBuilder nombreFichero = new StringBuilder("InformeTotalesMediador2015+_");
		nombreFichero.append(year);
		nombreFichero.append(month < 10 ? "0" + month : month);
		nombreFichero.append(".xls");
		
		int cont = 0;
		
		BigDecimal sumaPCN = new BigDecimal(0);
		BigDecimal sumaComAgente = new BigDecimal(0);
		
		try {		
		
			HSSFWorkbook wb          = new HSSFWorkbook();
			// ruta temporal
			FileOutputStream fileOut = new FileOutputStream(
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString());
		
			HSSFSheet sheet = wb.createSheet();
			sheet = wb.getSheetAt(0);
			
			HSSFCell cell;		
			
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 11));
			
			HSSFCellStyle estiloTitulo = ExcelUtils.getEstiloTitulo(wb);
			HSSFRow titulo = sheet.createRow(0);		
			
			cell = titulo.createCell(0);		
			
			cell.setCellStyle(estiloTitulo);
			cell.setCellValue(new HSSFRichTextString(getMesByCierre(fechaCierre)));	
						
			//Se insertan las cabeceras
			HSSFRow cabecera = sheet.createRow(1); //new HSSFRichTextString("CODENT")
			HSSFCellStyle estiloCabecera = ExcelUtils.getEstiloCabecera(wb);
			
			
			cell = cabecera.createCell(0);
			cell.setCellValue(new HSSFRichTextString(ENT_MED));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(1);
			cell.setCellValue(new HSSFRichTextString(SUBENT_MED));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(2);
			cell.setCellValue(new HSSFRichTextString(NOMBRE_MEDIADORA));
			cell.setCellStyle(estiloCabecera);

			cell = cabecera.createCell(3);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("FASE"));
			
			cell = cabecera.createCell(4);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("PLAN"));
			
			cell = cabecera.createCell(5);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("COD.LINEA"));
			
			cell = cabecera.createCell(6);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString(LINEA));
			
			cell = cabecera.createCell(7);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString(COLECTIVO));
			
			cell = cabecera.createCell(8);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("REF. POLIZA"));
			
			cell = cabecera.createCell(9);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("NIF ASG."));
			
			cell = cabecera.createCell(10);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString(TITULO_COL_PCN));

			cell = cabecera.createCell(11);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("COMISION AGENTE"));
			
			HSSFCellStyle estiloFila = ExcelUtils.getEstiloFila(wb);
			HSSFCellStyle estiloFilaNumero = ExcelUtils.getEstiloFilaNumero(wb);
			
			for (cont=0; cont<lstTotMed2015.size(); cont++){
				HSSFRow row     = sheet.createRow(cont+2); 
				
				
				cell = row.createCell(0);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				//Codigo entidad mediadiora
				if ((lstTotMed2015.get(cont).getEntidad() != null)){					
					cell.setCellValue(lstTotMed2015.get(cont).getEntidad().doubleValue());					
				}
								
				cell = row.createCell(1);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);			
				//Codigo Subentidad mediadiora
				if ((lstTotMed2015.get(cont).getSubentidad() != null)){					
					cell.setCellValue(lstTotMed2015.get(cont).getSubentidad().doubleValue());						
				}			
				
				//Nombre E-S Mediadora
				cell = row.createCell(2);
				cell.setCellStyle(estiloFila);
				if ((lstTotMed2015.get(cont).getNomentidad() != null)){					
					cell.setCellValue(new HSSFRichTextString(lstTotMed2015.get(cont).getNomentidad()));					
				}
						
						
				//FASE
				cell = row.createCell(3);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if ((lstTotMed2015.get(cont).getFase()!= null)){
					cell.setCellValue(lstTotMed2015.get(cont).getFase().doubleValue());					
				}		
				//PLAN
				cell = row.createCell(4);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (lstTotMed2015.get(cont).getPlan() != null){	
					cell.setCellValue(lstTotMed2015.get(cont).getPlan().doubleValue());					
				}
				//LINEA
				cell = row.createCell(5);
				cell.setCellStyle(estiloFila);
				if (lstTotMed2015.get(cont).getLinea() != null){				
					cell.setCellValue(lstTotMed2015.get(cont).getLinea().doubleValue());					
				}
				
				//NombreLinea
				cell = row.createCell(6);
				cell.setCellStyle(estiloFila);
				if (lstTotMed2015.get(cont).getNomlinea() != null){					
					String descLinea = lineas.get(lstTotMed2015.get(cont).getLinea().toString());
					descLinea = (descLinea != null) ? descLinea.trim(): "";
					cell.setCellValue(descLinea);					
				}
				
				//COLECTIVO
				cell = row.createCell(7);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (lstTotMed2015.get(cont).getReferencia() != null){
					cell.setCellValue(Integer.parseInt(lstTotMed2015.get(cont).getReferencia()));					
				}
				
				//REF.POLIZA
				cell = row.createCell(8);
				cell.setCellStyle(estiloFila); 
				if ((lstTotMed2015.get(cont).getRefpoliza()!= null)){
					cell.setCellValue(new HSSFRichTextString(lstTotMed2015.get(cont).getRefpoliza()));					
				}
				//NIF. ASEG.
				cell = row.createCell(9);
				cell.setCellStyle(estiloFila); 
				if ((lstTotMed2015.get(cont).getNifaseg()!= null)){
					cell.setCellValue(new HSSFRichTextString(lstTotMed2015.get(cont).getNifaseg()));					
				}
				//PCN
				cell = row.createCell(10);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero); 
				if ((lstTotMed2015.get(cont).getPrima()!= null)){
					cell.setCellValue(lstTotMed2015.get(cont).getPrima().doubleValue());
					sumaPCN = sumaPCN.add(lstTotMed2015.get(cont).getPrima());
				}

				//COMISION AGENTE
				cell = row.createCell(11);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero); 
				if ((lstTotMed2015.get(cont).getComisionAgente()!= null)){
					cell.setCellValue(lstTotMed2015.get(cont).getComisionAgente().doubleValue());
					sumaComAgente = sumaComAgente.add(lstTotMed2015.get(cont).getComisionAgente());
				};
				
			}
			
			if (cont > 0){
				HSSFRow row = sheet.createRow(cont+2);
				HSSFCellStyle estiloTotales = ExcelUtils.getEstiloFilaTotales(wb);
				HSSFCellStyle estiloFilaTotalesNumero = ExcelUtils.getEstiloFilaTotalesNumero(wb);
				
				cell = row.createCell(9);
				cell.setCellValue(new HSSFRichTextString(TOTAL));
				cell.setCellStyle(estiloTotales);
				
				cell = row.createCell(10);
				cell.setCellValue(sumaPCN.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
				
				cell = row.createCell(11);
				cell.setCellValue(sumaComAgente.doubleValue());
				cell.setCellStyle(estiloFilaTotalesNumero);
			}			
			
			ExcelUtils.autoajustarColumnas(sheet, cell.getColumnIndex() + 1);
			
			wb.write(fileOut);
			
			informesExcelDao.saveFicheroExcelCierre(nombreFichero.toString(),
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString(), idCierre, usuario);
			
			fileOut.close();
		} catch (FileNotFoundException e) {
			logger.error(PERMISO_CARPETA_TEMP + e.getMessage());
			throw new BusinessException (PERMISO_CARPETA_TEMP , e);
		} catch (IOException e) {
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + e.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , e);
		} catch (DAOException dao){
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + dao.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , dao);
		}			
	}
	
	
	/**
	 * 
	 * @param idCierre
	 * @return
	 * @throws BusinessException 
	 */
	public List<InformeColaboradores2015> getListaColaboradores2015() throws BusinessException {
		
		logger.debug("InformesExcelManager - getListaColaboradores2015 - init");
		
		List<InformeColaboradores2015> listColaboradores2015 = null;		
		try {
			
			listColaboradores2015 = informesExcelDao.listColaboradores2015();			
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al generar la lista de ComisionesImpagados2015:" + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al generar la lista de ComisionesImpagados2015" , dao);
		}
		
		logger.debug("InformesExcelManager - getListaColaboradores2015 - end");
		
		return listColaboradores2015;
	}
	
	/**
	 * 
	 * @param idCierre
	 * @return
	 * @throws BusinessException
	 */
	public List<InformeCorredores2015> getListaCorredores2015() throws BusinessException {
		
		logger.debug("InformesExcelManager - getListaCorredores2015 - init");
		
		List<InformeCorredores2015> listCorredores2015 = null;		
		try {
			
			listCorredores2015 = informesExcelDao.listCorredores2015();			
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al generar la lista de ComisionesCorredores:" + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al generar la lista de ComisionesCorredores" , dao);
		}
		
		logger.debug("InformesExcelManager - getListaCorredores2015 - end");
		return listCorredores2015;
	}
	
	/**
	 * 
	 * @param idCierre
	 * @return
	 * @throws BusinessException 
	 */
	public List<InformeComsFamLinEnt> getListaComsFamLinEnt() throws BusinessException {
		
		logger.debug("InformesExcelManager - getListaComsFamLinEnt - init");
		
		List<InformeComsFamLinEnt> listComsFamLinEnt = null;		
		try {

			listComsFamLinEnt = informesExcelDao.listComsFamLinEnt();			

		} catch (DAOException dao) {
			logger.error("Se ha producido un error al generar la lista de ComsFamLinEnt:" + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al generar la lista de ComsFamLinEnt" , dao);
		}
		
		logger.debug("InformesExcelManager - getListaComsFamLinEnt - end");
		
		return listComsFamLinEnt;
	}
	
	/**
	 * 
	 * @param idCierre
	 * @return
	 * @throws BusinessException 
	 */
	public List<InformeComsFacturacion> getListaComsFacturacion() throws BusinessException {
		
		logger.debug("InformesExcelManager - getListaComsFacturacion - init");
		
		List<InformeComsFacturacion> listComsFacturacion = null;		
		try {
			
			listComsFacturacion = informesExcelDao.listComsFacturacion();			
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al generar la lista de ComsFacturacion:" + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al generar la lista de ComsFacturacion" , dao);
		}
		
		logger.debug("InformesExcelManager - getListaComsFacturacion - end");
		
		return listComsFacturacion;
	}
	
	
	/**
	 * 
	 */
	public void generarInformeColaboradores2015(List<InformeColaboradores2015> lstColaboradores2015, final Long idCierre, final Date fechaCierre, Usuario usuario) throws BusinessException {
		logger.debug("InformesExcelManager - generarInformeColaboradores2015 - init");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaCierre);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		StringBuilder nombreFichero = new StringBuilder("InformeColaboradores2015+_");
		nombreFichero.append(year);
		nombreFichero.append(month < 10 ? "0" + month : month);
		nombreFichero.append(".xls");
		int i = 0;
				
		try {		

			HSSFWorkbook wb = new HSSFWorkbook();
			// ruta temporal
			
			FileOutputStream fileOut = new FileOutputStream(
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString());		
			HSSFSheet sheet = wb.createSheet();
			sheet = wb.getSheetAt(0);								
			HSSFCell cell;				
						
			//Se insertan las cabeceras
			HSSFCellStyle estiloCabecera = ExcelUtils.getEstiloCabecera(wb);
			HSSFRow cabecera = sheet.createRow(0);
			
			cell = cabecera.createCell(0);
			cell.setCellValue(new HSSFRichTextString(TITULO_COL_ENTIDAD_MEDIADORA));
			cell.setCellStyle(estiloCabecera);						

			cell = cabecera.createCell(1);
			cell.setCellValue(new HSSFRichTextString(TITULO_COL_NOMBRE_DESTINATARIO));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(2);
			cell.setCellValue(new HSSFRichTextString(TITULO_COL_EMAIL));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(3);
			cell.setCellValue(new HSSFRichTextString(TITULO_COL_EMAIL2));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(4);
			cell.setCellValue(new HSSFRichTextString(TITULO_COL_PCN));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(5);
			cell.setCellValue(new HSSFRichTextString(TITULO_COL_IMPORTE));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(6);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString(TITULO_COL_RETENCION_IRPF));
			
			cell = cabecera.createCell(7);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString(TITULO_COL_IMPORTE_A_LIQUIDAR));

			cell = cabecera.createCell(8);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString(TITULO_COL_MES));

			cell = cabecera.createCell(9);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString(TITULO_COL_IBAN));


			HSSFCellStyle estiloFila = ExcelUtils.getEstiloFila(wb);
			HSSFCellStyle estiloFilaNumero = ExcelUtils.getEstiloFilaNumeroSinSeparadorDeMiles(wb);
			
			for (i=0; i<lstColaboradores2015.size(); i++){
				HSSFRow row     = sheet.createRow(i+1); 

				//Codigo entidad mediadora
				cell = row.createCell(0);
				cell.setCellStyle(estiloFila);
				if ((lstColaboradores2015.get(i).getCSB() != null)){			
					cell.setCellValue(lstColaboradores2015.get(i).getCSB());					
				}
				
				//Nombre E-S Mediadora
				cell = row.createCell(1);
				cell.setCellStyle(estiloFila);
				if ((lstColaboradores2015.get(i).getNomSubentidad() != null)){	
					cell.setCellValue(new HSSFRichTextString(lstColaboradores2015.get(i).getNomSubentidad()));					
				}
					
				//Email
				cell = row.createCell(2);
				cell.setCellStyle(estiloFila);
				if (lstColaboradores2015.get(i).getEmail() != null){
					cell.setCellValue(lstColaboradores2015.get(i).getEmail());
				}
				
				//Email2
				cell = row.createCell(3);
				cell.setCellStyle(estiloFila);
				if (lstColaboradores2015.get(i).getEmail2() != null){
					cell.setCellValue(lstColaboradores2015.get(i).getEmail2());
				}
			
				//PCN
				cell = row.createCell(4);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFilaNumero);
				if (lstColaboradores2015.get(i).getPCN() != null){
					cell.setCellValue(lstColaboradores2015.get(i).getPCN().doubleValue());
				}
				
				//Importe
				cell = row.createCell(5);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFilaNumero);
				if (lstColaboradores2015.get(i).getImporte() != null){
					cell.setCellValue(lstColaboradores2015.get(i).getImporte().doubleValue());
				}
				
				//Retenci髇 IRPF
				cell = row.createCell(6);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFilaNumero);
				if (lstColaboradores2015.get(i).getRetencion() != null){
					cell.setCellValue(lstColaboradores2015.get(i).getRetencion().doubleValue());
				}
				
				//Importe a liquidar
				cell = row.createCell(7);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFilaNumero);
				if (lstColaboradores2015.get(i).getLiquidar() != null){
					cell.setCellValue(lstColaboradores2015.get(i).getLiquidar().doubleValue());
				}
				
				//Mes
				cell = row.createCell(8);
				cell.setCellStyle(estiloFila);
				if (lstColaboradores2015.get(i).getMes() != null){
					cell.setCellValue(new HSSFRichTextString(lstColaboradores2015.get(i).getMes().toString()));	
				}
				
				//IBAN
				cell = row.createCell(9);
				cell.setCellStyle(estiloFila);
				if ((lstColaboradores2015.get(i).getIBAN() != null)){					
					cell.setCellValue(new HSSFRichTextString(lstColaboradores2015.get(i).getIBAN()));					
				}
			}
			
			
			ExcelUtils.autoajustarColumnas(sheet, cell.getColumnIndex() + 1);		
						
			wb.write(fileOut);
			
			informesExcelDao.saveFicheroExcelCierre(nombreFichero.toString(),
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString(), idCierre, usuario);	
			
			fileOut.close();
			
			logger.debug("InformesExcelManager - generarInformeColaboradores2015 - end");

		} catch (FileNotFoundException e) {
			logger.error(PERMISO_CARPETA_TEMP + e.getMessage());
			throw new BusinessException (PERMISO_CARPETA_TEMP , e);
		} catch (IOException e) {
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + e.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , e);
		} catch (DAOException dao){
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + dao.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , dao);
		}			
		
	}

	/**
	 * 
	 */
	public void generarInformeCorredores2015(List<InformeCorredores2015> lstCorredores2015, final Long idCierre, final Date fechaCierre, Usuario usuario) throws BusinessException {
		logger.debug("InformesExcelManager - generarInformeCorredores2015 - init");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaCierre);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		StringBuilder nombreFichero = new StringBuilder("InformeCorredores2015+_");
		nombreFichero.append(year);
		nombreFichero.append(month < 10 ? "0" + month : month);
		nombreFichero.append(".xls");
		
		int i = 0;
		
		try {		

			HSSFWorkbook wb = new HSSFWorkbook();
			// ruta temporal
			
			FileOutputStream fileOut = new FileOutputStream(
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString());		
			HSSFSheet sheet = wb.createSheet();
			sheet = wb.getSheetAt(0);								
			HSSFCell cell;				
						
			//Se insertan las cabeceras
			HSSFCellStyle estiloCabecera = ExcelUtils.getEstiloCabecera(wb);
			HSSFRow cabecera = sheet.createRow(0);
			
			
			cell = cabecera.createCell(0);
			cell.setCellValue(new HSSFRichTextString(TITULO_COL_ENTIDAD_MEDIADORA));
			cell.setCellStyle(estiloCabecera);						

			cell = cabecera.createCell(1);
			cell.setCellValue(new HSSFRichTextString(TITULO_COL_NOMBRE_DESTINATARIO));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(2);
			cell.setCellValue(new HSSFRichTextString(TITULO_COL_EMAIL));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(3);
			cell.setCellValue(new HSSFRichTextString(TITULO_COL_EMAIL2));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(4);
			cell.setCellValue(new HSSFRichTextString(TITULO_COL_PCN));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(5);
			cell.setCellValue(new HSSFRichTextString(TITULO_COL_IMPORTE));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(6);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString(TITULO_COL_RETENCION_IRPF));
			
			cell = cabecera.createCell(7);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString(TITULO_COL_IMPORTE_A_LIQUIDAR));

			cell = cabecera.createCell(8);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString(TITULO_COL_ABON_ADQ));

			cell = cabecera.createCell(9);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString(TITULO_COL_IBAN));


			HSSFCellStyle estiloFila = ExcelUtils.getEstiloFila(wb);
			HSSFCellStyle estiloFilaNumero = ExcelUtils.getEstiloFilaNumeroSinSeparadorDeMiles(wb);
			
			for (i=0; i<lstCorredores2015.size(); i++){
				HSSFRow row     = sheet.createRow(i+1); 

				//Codigo entidad mediadiora
				cell = row.createCell(0);
				cell.setCellStyle(estiloFila);
				if ((lstCorredores2015.get(i).getCSB() != null)){			
					cell.setCellValue(lstCorredores2015.get(i).getCSB());					
				}
				
				//Nombre E-S Mediadora
				cell = row.createCell(1);
				cell.setCellStyle(estiloFila);
				if ((lstCorredores2015.get(i).getNomSubentidad() != null)){	
					cell.setCellValue(new HSSFRichTextString(lstCorredores2015.get(i).getNomSubentidad()));					
				}
					
				//Email
				cell = row.createCell(2);
				cell.setCellStyle(estiloFila);
				if (lstCorredores2015.get(i).getEmail() != null){
					cell.setCellValue(lstCorredores2015.get(i).getEmail());
				}
				
				//Email2
				cell = row.createCell(3);
				cell.setCellStyle(estiloFila);
				if (lstCorredores2015.get(i).getEmail2() != null){
					cell.setCellValue(lstCorredores2015.get(i).getEmail2());
				}
			
				//PCN
				cell = row.createCell(4);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFilaNumero);
				if (lstCorredores2015.get(i).getPCN() != null){
					cell.setCellValue(lstCorredores2015.get(i).getPCN().doubleValue());
				}
				
				//Importe
				cell = row.createCell(5);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFilaNumero);
				if (lstCorredores2015.get(i).getImporte() != null){
					cell.setCellValue(lstCorredores2015.get(i).getImporte().doubleValue());
				}
				
				//Retenci髇 IRPF
				cell = row.createCell(6);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFilaNumero);
				if (lstCorredores2015.get(i).getRetencion() != null){
					cell.setCellValue(lstCorredores2015.get(i).getRetencion().doubleValue());
				}
				
				//Importe a liquidar
				cell = row.createCell(7);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFilaNumero);
				if (lstCorredores2015.get(i).getLiquidar() != null){
					cell.setCellValue(lstCorredores2015.get(i).getLiquidar().doubleValue());
				}
				
				//Abon/Adq
				cell = row.createCell(8);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFilaNumero);
				if (lstCorredores2015.get(i).getAbonAdq() != null){
					cell.setCellValue(lstCorredores2015.get(i).getAbonAdq().doubleValue());	
				}
				
				//IBAN
				cell = row.createCell(9);
				cell.setCellStyle(estiloFila);
				if ((lstCorredores2015.get(i).getIBAN() != null)){					
					cell.setCellValue(new HSSFRichTextString(lstCorredores2015.get(i).getIBAN()));					
				}
			}
			
			
			ExcelUtils.autoajustarColumnas(sheet, cell.getColumnIndex() + 1);		
			
			wb.write(fileOut);
			
			informesExcelDao.saveFicheroExcelCierre(nombreFichero.toString(),
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString(), idCierre, usuario);	
			
			fileOut.close();
			
			logger.debug("InformesExcelManager - generarInformeCorredores2015 - end");

		} catch (FileNotFoundException e) {
			logger.error(PERMISO_CARPETA_TEMP + e.getMessage());
			throw new BusinessException (PERMISO_CARPETA_TEMP , e);
		} catch (IOException e) {
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + e.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , e);
		} catch (DAOException dao){
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + dao.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , dao);
		}			
		
	}
	
	/**
	 * 
	 */
	public void generarInformeComsFamLinEnt(List<InformeComsFamLinEnt> lstComsFamLinEnt, final Long idCierre, final Date fechaCierre, Usuario usuario) throws BusinessException {
		logger.debug("InformesExcelManager - generarInformeComsFamLinEnt - init");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaCierre);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		StringBuilder nombreFichero = new StringBuilder("AgroPlus_comisiones_");
		nombreFichero.append(year);
		nombreFichero.append(month < 10 ? "0" + month : month);
		
		String nombreCSV_or = nombreFichero.toString() + ".csv";
		
		nombreFichero.append(".xls");
		
		int i = 0;
		
		try {		

			HSSFWorkbook wb = new HSSFWorkbook();
			// ruta temporal
			
			FileOutputStream fileOut = new FileOutputStream(
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString());		
			HSSFSheet sheet = wb.createSheet("Mis_comisiones_2011");
			sheet = wb.getSheetAt(0);								
			HSSFCell cell;				
						
			//Se insertan las cabeceras
			HSSFCellStyle estiloCabecera = ExcelUtils.getEstiloCabecera(wb);
			HSSFRow cabecera = sheet.createRow(0);
			
			cell = cabecera.createCell(0);
			cell.setCellValue(new HSSFRichTextString("fechaEmisionRecibo"));
			cell.setCellStyle(estiloCabecera);						

			cell = cabecera.createCell(1);
			cell.setCellValue(new HSSFRichTextString("Grupo de lineas.Tx"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(2);
			cell.setCellValue(new HSSFRichTextString("Familias.Tx"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(3);
			cell.setCellValue(new HSSFRichTextString("linea"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(4);
			cell.setCellValue(new HSSFRichTextString("Grupo de negocio"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(5);
			cell.setCellValue(new HSSFRichTextString("plan"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(6);
			cell.setCellValue(new HSSFRichTextString("CSB_Origen"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(7);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("Ent_n"));
			
			cell = cabecera.createCell(8);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("Sub_n"));

			cell = cabecera.createCell(9);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("Total_Entidad"));

			cell = cabecera.createCell(10);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("SumaDeReglamento"));

			cell = cabecera.createCell(11);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("Tt_Comision"));
			
			HSSFCellStyle estiloFila = ExcelUtils.getEstiloFila(wb);
			HSSFCellStyle estiloFilaNumero = ExcelUtils.getEstiloFilaNumero(wb);
			HSSFCellStyle estiloFecha = ExcelUtils.getEstiloFilaFecha(wb);
			
			String nombreCSV = bundle.getString(RUTA_TEMP_INF_COM) + "/"  + nombreCSV_or;
			
			FileWriter fileWriter = new FileWriter(nombreCSV);
			
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			
			String header = "fechaEmisionRecibo; Grupo de lineas.Tx; Familias.Tx; linea; Grupo de negocio; plan; CSB_Origen; Ent_n; Sub_n; Total_Entidad; SumaDeReglamento; Tt_Comision";
			
			bufferedWriter.write(header);

			for (i=0; i<lstComsFamLinEnt.size(); i++){
				HSSFRow row     = sheet.createRow(i+1); 

				//Fecha emisi髇 recibo
				cell = row.createCell(0);
				cell.setCellStyle(estiloFecha);
				if ((lstComsFamLinEnt.get(i).getFechaEmisionRecibo() != null)){
					cell.setCellValue(lstComsFamLinEnt.get(i).getFechaEmisionRecibo());
				}
				
				//Grupo
				cell = row.createCell(1);
				cell.setCellStyle(estiloFila);
				if ((lstComsFamLinEnt.get(i).getGrupo() != null)){	
					cell.setCellValue(new HSSFRichTextString(lstComsFamLinEnt.get(i).getGrupo()));					
				}
					
				//Familia
				cell = row.createCell(2);
				cell.setCellStyle(estiloFila);
				if (lstComsFamLinEnt.get(i).getFamilia() != null){
					cell.setCellValue(lstComsFamLinEnt.get(i).getFamilia());
				}
				
				//Linea
				cell = row.createCell(3);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFila);
				if (lstComsFamLinEnt.get(i).getLinea() != null){
					cell.setCellValue(lstComsFamLinEnt.get(i).getLinea());
				}
				
				//Codgruponegocio
				cell = row.createCell(4);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (lstComsFamLinEnt.get(i).getCodGrupoNegocio() != null){
					cell.setCellValue(Integer.valueOf(lstComsFamLinEnt.get(i).getCodGrupoNegocio()));
				}				
			
				//Plan
				cell = row.createCell(5);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFila);
				if (lstComsFamLinEnt.get(i).getPlan() != null){
					cell.setCellValue(lstComsFamLinEnt.get(i).getPlan());
				}
				
				//CSB
				cell = row.createCell(6);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFila);
				if (lstComsFamLinEnt.get(i).getCSB() != null){
					cell.setCellValue(lstComsFamLinEnt.get(i).getCSB());
				}
				
				//Entidad
				cell = row.createCell(7);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFila);
				if (lstComsFamLinEnt.get(i).getEntidad() != null){
					cell.setCellValue(lstComsFamLinEnt.get(i).getEntidad());
				}
				
				//Subentidad
				cell = row.createCell(8);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFila);
				if (lstComsFamLinEnt.get(i).getSubentidad() != null){
					cell.setCellValue(lstComsFamLinEnt.get(i).getSubentidad());
				}
				
				//Total entidad
				cell = row.createCell(9);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				if (lstComsFamLinEnt.get(i).getTotal() != null){
					cell.setCellValue(lstComsFamLinEnt.get(i).getTotal().doubleValue());
				}
				
				//Suma reglamento
				cell = row.createCell(10);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				if (lstComsFamLinEnt.get(i).getReglamento() != null){
					cell.setCellValue(lstComsFamLinEnt.get(i).getReglamento().doubleValue());
				}
				
				//Comisi髇
				cell = row.createCell(11);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				if (lstComsFamLinEnt.get(i).getComision() != null){
					cell.setCellValue(lstComsFamLinEnt.get(i).getComision().doubleValue());
				}
				
				logger.debug("InformesExcelManager - generarInformeComsFamLinEntCSV - init");
				
				String filaCSV = "";
            	
            	bufferedWriter.newLine(); // Move to the next line
            	
            	if ((lstComsFamLinEnt.get(i).getFechaEmisionRecibo() != null)){		
            		filaCSV+=(lstComsFamLinEnt.get(i).getFechaEmisionRecibo().toString() + ";");
				}
            	else {
            		filaCSV+=";";
            	}
            	
            	if ((lstComsFamLinEnt.get(i).getGrupo() != null)){	
            		filaCSV+=(new HSSFRichTextString(lstComsFamLinEnt.get(i).getGrupo()).toString()+ ";");					
				}
            	else {
            		filaCSV+=";";
            	}
            	
            	if (lstComsFamLinEnt.get(i).getFamilia() != null){
            		filaCSV+=(lstComsFamLinEnt.get(i).getFamilia()+ ";");
				}
            	else {
            		filaCSV+=";";
            	}
            	
            	//Linea
				if (lstComsFamLinEnt.get(i).getLinea() != null){
					filaCSV+=(lstComsFamLinEnt.get(i).getLinea().toString()+ ";");
				}
				else {
            		filaCSV+=";";
            	}
            	
            	//Cod grupo negocio
				cell.setCellStyle(estiloFila);
				if (lstComsFamLinEnt.get(i).getCodGrupoNegocio() != null){
					filaCSV+=(lstComsFamLinEnt.get(i).getCodGrupoNegocio()+ ";");
				}
				else {
            		filaCSV+=";";
            	}
			
				//Plan
				if (lstComsFamLinEnt.get(i).getPlan() != null){
					filaCSV+=(lstComsFamLinEnt.get(i).getPlan().toString()+ ";");
				}
				else {
            		filaCSV+=";";
            	}
				
				//CSB
				if (lstComsFamLinEnt.get(i).getCSB() != null){
					filaCSV+=(lstComsFamLinEnt.get(i).getCSB().toString() + ";");
				}
				else {
            		filaCSV+=";";
            	}
				
				//Entidad
				if (lstComsFamLinEnt.get(i).getEntidad() != null){
					filaCSV+=(lstComsFamLinEnt.get(i).getEntidad().toString()+ ";");
				}
				else {
            		filaCSV+=";";
            	}
				
				//Subentidad
				if (lstComsFamLinEnt.get(i).getSubentidad() != null){
					filaCSV+=(lstComsFamLinEnt.get(i).getSubentidad().toString()+ ";");
				}
				else {
            		filaCSV+=";";
            	}
				
				// Total entidad
				if (lstComsFamLinEnt.get(i).getTotal() != null){
					filaCSV+=(lstComsFamLinEnt.get(i).getTotal().toString()+ ";");
				}
				else {
            		filaCSV+=";";
            	}
				
				// //Suma reglamento
				if (lstComsFamLinEnt.get(i).getReglamento() != null){
					filaCSV+=(lstComsFamLinEnt.get(i).getReglamento().toString()+ ";");
				}
				else {
            		filaCSV+=";";
            	}
				
				// Comisi髇
				if (lstComsFamLinEnt.get(i).getComision() != null){
					filaCSV+=(lstComsFamLinEnt.get(i).getComision().toString()+ ";");
				}
				else {
            		filaCSV+=";";
            	}
				
            	
				bufferedWriter.write(filaCSV);
				
			}
			
			
			ExcelUtils.autoajustarColumnas(sheet, cell.getColumnIndex() + 1);		
			
			wb.write(fileOut);
			
			informesExcelDao.saveFicheroExcelCierre(nombreFichero.toString(),
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString(), idCierre, usuario);	
			
			fileOut.close();
			
			logger.debug("InformesExcelManager - generarInformeComsFamLinEnt - end");
			
			bufferedWriter.close();
            
			informesExcelDao.saveFicheroExcelCierre(nombreCSV_or,
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreCSV_or, idCierre, usuario);
			
			logger.debug("InformesExcelManager - generarInformeComsFamLinEntCSV - end");

		} catch (FileNotFoundException e) {
			logger.error(PERMISO_CARPETA_TEMP + e.getMessage());
			throw new BusinessException (PERMISO_CARPETA_TEMP , e);
		} catch (IOException e) {
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + e.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , e);
		} catch (DAOException dao){
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + dao.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , dao);
		}			
		
	}

	/**
	 * 
	 * @param listInformeCierreComisiones
	 * @param idCierre
	 * @param fechaCierre
	 * @param usuario
	 * @throws BusinessException 
	 */
	public void generarInformeComsFacturacion(List<InformeComsFacturacion> lstComsFacturacion,
			Long idCierre, Date fechaCierre, Usuario usuario) throws BusinessException {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaCierre);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		StringBuilder nombreFichero = new StringBuilder("Agroplus_Facturacion_");
		nombreFichero.append(year);
		nombreFichero.append(month < 10 ? "0" + month : month);
		
		String nombreCSV_or = nombreFichero.toString() + ".csv";
		
		nombreFichero.append(".xls");
		
		int i = 0;
		
		try {		

			HSSFWorkbook wb = new HSSFWorkbook();
			// ruta temporal
			
			FileOutputStream fileOut = new FileOutputStream(
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString());		
			HSSFSheet sheet = wb.createSheet("Hoja1");
			sheet = wb.getSheetAt(0);								
			HSSFCell cell;				
						
			//Se insertan las cabeceras
			HSSFCellStyle estiloCabecera = ExcelUtils.getEstiloCabecera(wb);
			HSSFRow cabecera = sheet.createRow(0);
			
			cell = cabecera.createCell(0);
			cell.setCellValue(new HSSFRichTextString("fechaEmisionRecibo"));
			cell.setCellStyle(estiloCabecera);						

			cell = cabecera.createCell(1);
			cell.setCellValue(new HSSFRichTextString("Grupo de lineas.Tx"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(2);
			cell.setCellValue(new HSSFRichTextString("Familias.Tx"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(3);
			cell.setCellValue(new HSSFRichTextString("Grupo de negocio"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(4);
			cell.setCellValue(new HSSFRichTextString("linea"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(5);
			cell.setCellValue(new HSSFRichTextString("plan"));
			cell.setCellStyle(estiloCabecera);
			
			cell = cabecera.createCell(6);
			cell.setCellValue(new HSSFRichTextString("CSB_Origen"));
			cell.setCellStyle(estiloCabecera);

			cell = cabecera.createCell(7);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString("Coste de Seguro"));

			
			HSSFCellStyle estiloFila = ExcelUtils.getEstiloFila(wb);
			HSSFCellStyle estiloFilaNumero = ExcelUtils.getEstiloFilaNumero(wb);
			HSSFCellStyle estiloFecha = ExcelUtils.getEstiloFilaFecha(wb);
			
			logger.debug("InformesExcelManager - generarInformeComsFacturacionCSV - start");
			
			String nombreCSV = bundle.getString(RUTA_TEMP_INF_COM) + "/"  + nombreCSV_or;
			
			logger.debug("Nombre de archivo: " + nombreCSV);
			
			FileWriter fileWriter = new FileWriter(nombreCSV);
			
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			
			String header = "fechaEmisionRecib; Grupo de lineas.Tx; Familias.Tx; Grupo de negocio; linea; plan; CSB Origen; Coste de Seguro";
			
			bufferedWriter.write(header);
			for (i=0; i<lstComsFacturacion.size(); i++){
				HSSFRow row = sheet.createRow(i+1); 

				//Fecha emisi髇 recibo
				cell = row.createCell(0);
				cell.setCellStyle(estiloFecha);
				if ((lstComsFacturacion.get(i).getFechaEmisionRecibo() != null)){		
					cell.setCellValue(lstComsFacturacion.get(i).getFechaEmisionRecibo());
				}
				
				//Grupo
				cell = row.createCell(1);
				cell.setCellStyle(estiloFila);
				if ((lstComsFacturacion.get(i).getGrupo() != null)){	
					cell.setCellValue(new HSSFRichTextString(lstComsFacturacion.get(i).getGrupo()));					
				}
					
				//Familia
				cell = row.createCell(2);
				cell.setCellStyle(estiloFila);
				if (lstComsFacturacion.get(i).getFamilia() != null){
					cell.setCellValue(lstComsFacturacion.get(i).getFamilia());
				}
				
				//Cod grupo negocio
				cell = row.createCell(3);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFila);
				if (lstComsFacturacion.get(i).getCodGrupoNegocio() != null){
					cell.setCellValue(Integer.valueOf(lstComsFacturacion.get(i).getCodGrupoNegocio()));
				}
				
				//Linea
				cell = row.createCell(4);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFila);
				if (lstComsFacturacion.get(i).getLinea() != null){
					cell.setCellValue(lstComsFacturacion.get(i).getLinea());
				}
			
				//Plan
				cell = row.createCell(5);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFila);
				if (lstComsFacturacion.get(i).getPlan() != null){
					cell.setCellValue(lstComsFacturacion.get(i).getPlan());
				}
				
				//CSB
				cell = row.createCell(6);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellStyle(estiloFila);
				if (lstComsFacturacion.get(i).getCSB() != null){
					cell.setCellValue(lstComsFacturacion.get(i).getCSB());
				}
				
				//Coste de seguro
				cell = row.createCell(7);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(estiloFilaNumero);
				if (lstComsFacturacion.get(i).getCoste() != null){
					cell.setCellValue(lstComsFacturacion.get(i).getCoste().doubleValue());
				}
				
				
				// Fichero CSV
				
				String filaCSV = "";
            	
            	bufferedWriter.newLine(); // Move to the next line
            	
            	if ((lstComsFacturacion.get(i).getFechaEmisionRecibo() != null)){		
            		filaCSV+=(lstComsFacturacion.get(i).getFechaEmisionRecibo().toString() + ";");
				}
            	else {
            		filaCSV+=";";
            	}
            	
            	if ((lstComsFacturacion.get(i).getGrupo() != null)){	
            		filaCSV+=(new HSSFRichTextString(lstComsFacturacion.get(i).getGrupo()).toString()+ ";");					
				}
            	else {
            		filaCSV+=";";
            	}
            	
            	if (lstComsFacturacion.get(i).getFamilia() != null){
            		filaCSV+=(lstComsFacturacion.get(i).getFamilia()+ ";");
				}
            	else {
            		filaCSV+=";";
            	}
            	
            	//Cod grupo negocio
				cell.setCellStyle(estiloFila);
				if (lstComsFacturacion.get(i).getCodGrupoNegocio() != null){
					filaCSV+=(lstComsFacturacion.get(i).getCodGrupoNegocio()+ ";");
				}
				else {
            		filaCSV+=";";
            	}
				
				//Linea
				if (lstComsFacturacion.get(i).getLinea() != null){
					filaCSV+=(lstComsFacturacion.get(i).getLinea().toString()+ ";");
				}
				else {
            		filaCSV+=";";
            	}
			
				//Plan
				if (lstComsFacturacion.get(i).getPlan() != null){
					filaCSV+=(lstComsFacturacion.get(i).getPlan().toString()+ ";");
				}
				else {
            		filaCSV+=";";
            	}
				
				//CSB
				if (lstComsFacturacion.get(i).getCSB() != null){
					filaCSV+=(lstComsFacturacion.get(i).getCSB().toString() + ";");
				}
				else {
            		filaCSV+=";";
            	}
				
				//Coste de seguro
				if (lstComsFacturacion.get(i).getCoste() != null){
					filaCSV+=(lstComsFacturacion.get(i).getCoste().toString()+ ";");
				}
				else {
            		filaCSV+=";";
            	}
				
            	
				bufferedWriter.write(filaCSV);
				
				
			}
			
			ExcelUtils.autoajustarColumnas(sheet, cell.getColumnIndex() + 1);		
			
			wb.write(fileOut);
			
			informesExcelDao.saveFicheroExcelCierre(nombreFichero.toString(),
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreFichero.toString(), idCierre, usuario);	
			
			fileOut.close();
			
			logger.debug("InformesExcelManager - generarInformeComsFacturacion - end");
		
            bufferedWriter.close();
            
			informesExcelDao.saveFicheroExcelCierre(nombreCSV_or,
					bundle.getString(RUTA_TEMP_INF_COM) + "/" + nombreCSV_or, idCierre, usuario);
			
			logger.debug("InformesExcelManager - generarInformeComsFacturacionCSV - end");
			

		} catch (FileNotFoundException e) {
			logger.error(PERMISO_CARPETA_TEMP + e.getMessage());
			throw new BusinessException (PERMISO_CARPETA_TEMP , e);
		} catch (IOException e) {
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + e.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , e);
		} catch (DAOException dao){
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + dao.getMessage());
			throw new BusinessException (ERROR_GUARDAR_EXCEL , dao);
		}			
		
	}
	
	public HSSFWorkbook generarDetallePolizaParcelas(Poliza poliza, List<Parcela> parcelas) throws BusinessException {

		logger.debug("InformesExcelManager - generarDetallePolizaParcelas # INIT");

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();

		try {

			HSSFCell cell;

			///////// CABECERAS (FILA 2)
			HSSFRow cabecera = sheet.createRow(0);
			HSSFCellStyle estiloCabecera = ExcelUtils.getEstiloCabecera(wb);

			String[] headers = new String[] { "MODULO", "HOJA", "PARCELA", "CULTIVO", "NOM_CULTIVO", "VARIEDAD",
					"NOM_VARIEDAD", "PROVINCIA", "COMARCA", "TERMINO", "NOM_TERMINO", "SUBTERMINO", "NOM_PARCELA",
					"SUPERFICIE", "PRECIO", "PRODUCCION", "TIPO_CAPITAL", "PRIMA_COMERCIAL", "BONIFICACIONES",
					"BONIF_MEDIDAS", "PRIMA_NETA", "REC_CONSORCIO", "RECIBO_PRIMA", "SUB_ENESA", "SUB_CCAA",
					"COSTE_TOMADOR" };

			for (int i = 0; i < headers.length; i++) {
				cell = cabecera.createCell(i);
				cell.setCellValue(new HSSFRichTextString(headers[i]));
				cell.setCellStyle(estiloCabecera);
			}

			logger.debug("Parcelas recibidas: " + parcelas.size());
			
			//////// CONTENIDO
			for (int i = 0; i < parcelas.size(); i++) {

				for (CapitalAsegurado cap : parcelas.get(i).getCapitalAsegurados()) {

					HSSFRow row = sheet.createRow(i + 1);

					Parcela parcela = parcelas.get(i);

					////////////////////
					// MODULO
					auxSetCell(row.createCell(0), wb, poliza.getCodmodulo());
					// HOJA
					auxSetCell(row.createCell(1), wb, parcela.getHoja());
					// PARCELA
					auxSetCell(row.createCell(2), wb, parcela.getNumero());

					VariedadId variedadId = new VariedadId(poliza.getLinea().getLineaseguroid(),
							parcela.getCodcultivo(), parcela.getCodvariedad());
					Variedad variedad = (Variedad) this.informesExcelDao.get(Variedad.class, variedadId);
					// CULTIVO
					auxSetCell(row.createCell(3), wb, variedad.getId().getCodcultivo().doubleValue());
					// NOM_CULTIVO
					auxSetCell(row.createCell(4), wb, variedad.getCultivo().getDescultivo());
					// VARIEDAD
					auxSetCell(row.createCell(5), wb, variedad.getId().getCodvariedad().doubleValue());
					// NOM_VARIEDAD
					auxSetCell(row.createCell(6), wb, variedad.getDesvariedad());

					// PROVINCIA
					auxSetCell(row.createCell(7), wb, parcela.getTermino().getId().getCodprovincia().intValue());
					// COMARCA
					auxSetCell(row.createCell(8), wb, parcela.getTermino().getId().getCodcomarca().intValue());
					// TERMINO
					auxSetCell(row.createCell(9), wb, parcela.getTermino().getId().getCodtermino().intValue());
					// NOM_TERMINO
					
					Date fechaInicioContratacion = parcela.getPoliza().getLinea().getFechaInicioContratacion();
					auxSetCell(row.createCell(10), wb, parcela.getTermino().getNomTerminoByFecha(fechaInicioContratacion, false));
					
					// SUBTERMINO
					auxSetCell(row.createCell(11), wb, parcela.getTermino().getId().getSubtermino().toString());
					// NOM_PARCELA
					auxSetCell(row.createCell(12), wb, parcela.getNomparcela());

					// SUPERFICIE
					if (cap.getSuperficie() != null)
						auxSetCell(row.createCell(13), wb, cap.getSuperficie().doubleValue());

					BigDecimal precio = null;
					BigDecimal produccion = null;
					if (!CollectionUtils.isEmpty(cap.getCapAsegRelModulos())) {
						Iterator<CapAsegRelModulo> itCaRm = cap.getCapAsegRelModulos().iterator();
						while (itCaRm.hasNext()) {
							CapAsegRelModulo caRm = itCaRm.next();
							if (poliza.getCodmodulo().equals(caRm.getCodmodulo())) {
								precio = caRm.getPrecio();
								produccion = caRm.getProduccion();
								break;
							}
						}
					}
					// PRECIO
					if (precio == null) {
						if (cap.getPrecio() != null) {
							auxSetCell(row.createCell(14), wb, cap.getPrecio().doubleValue());
						}
					} else {
						auxSetCell(row.createCell(14), wb, precio.doubleValue());
					}

					// PRODUCCION
					if (produccion == null) {
						if (cap.getProduccion() != null) {
							auxSetCell(row.createCell(15), wb, cap.getProduccion().doubleValue());
						}
					} else {
						auxSetCell(row.createCell(15), wb, produccion.doubleValue());
					}

					// TIPO_CAPITAL
					if (cap.getTipoCapital() != null && cap.getTipoCapital().getCodtipocapital() != null)
						auxSetCell(row.createCell(16), wb, cap.getTipoCapital().getCodtipocapital().intValue());
					
					///////////DISTRIBUCION DE COSTES
					if (poliza.getDistribucionCoste2015s().size()!=0) {
						
						DistribucionCoste2015 dc2015 = poliza.getDistribucionCoste2015s().iterator().next();
						
							if (dc2015.getDistCosteParcela2015s().size()!=0) {
								
								Iterator<DistCosteParcela2015> itDcp2015 = dc2015.getDistCosteParcela2015s().iterator();
								
								while (itDcp2015.hasNext()) {
												
									DistCosteParcela2015 dcp2015 = itDcp2015.next();
									
									if (dcp2015.getTipo().doubleValue() == cap.getTipoCapital().getCodtipocapital().doubleValue()) {
										
										if (dcp2015.getHoja().doubleValue() == cap.getParcela().getHoja()) {
										
											if (dcp2015.getNumero().doubleValue() == cap.getParcela().getNumero().doubleValue()) {
												
												auxSetCell(row.createCell(17), wb, 0);
												auxSetCell(row.createCell(18), wb, 0);
												auxSetCell(row.createCell(19), wb, 0);
												auxSetCell(row.createCell(20), wb, 0);
												auxSetCell(row.createCell(21), wb, 0);
												auxSetCell(row.createCell(22), wb, 0);
												auxSetCell(row.createCell(23), wb, 0);
												auxSetCell(row.createCell(24), wb, 0);
												auxSetCell(row.createCell(25), wb, 0);

												if (dcp2015.getPrimacomercial() != null) {
													auxSetCell(row.createCell(17), wb, dcp2015.getPrimacomercial().doubleValue());
												}
												
												if (dcp2015.getDistCosteBonifRecs().size()!=0) {
													Iterator<DistCosteParcela2015BonifRec> itDcpBR = dcp2015.getDistCosteBonifRecs()
															.iterator();
													
													double totalBonif = 0;
													double totalBonifMedidas = 0;
													
													while (itDcpBR.hasNext()) {
														DistCosteParcela2015BonifRec dcpBR = itDcpBR.next();
														
														if ("B".equals(dcpBR.getTipoBonif().toString())) {
															if (dcpBR.getImporte() != null) {
																totalBonif+=dcpBR.getImporte().doubleValue();
															}
														} else if ("R".equals(dcpBR.getTipoBonif().toString())) {
															if (dcpBR.getImporte() != null) {
																totalBonif-=dcpBR.getImporte().doubleValue();
															}
														}
													}
													
													auxSetCell(row.createCell(18), wb, totalBonif);
													auxSetCell(row.createCell(19), wb, totalBonifMedidas);
													
												}
												
												if (dcp2015.getPrimacomercialneta() != null) {
													auxSetCell(row.createCell(20), wb, dcp2015.getPrimacomercialneta().doubleValue());
												}
												
												if (dcp2015.getRecargoconsorcio() != null) {
													auxSetCell(row.createCell(21), wb, dcp2015.getRecargoconsorcio().doubleValue());
												}
												
												if (dcp2015.getReciboprima() != null) {
													auxSetCell(row.createCell(22), wb, dcp2015.getReciboprima().doubleValue());
												}
												
												if (dcp2015.getDistCosteSubvs().size()!=0) {
													
													Iterator<DistCosteParcela2015Subvencion> itDcpSubv = dcp2015.getDistCosteSubvs().iterator();
													
													double subvEnesa = 0;
													double subvCCAA = 0;
													
													while (itDcpSubv.hasNext()) {
														
														DistCosteParcela2015Subvencion dcpSubv = itDcpSubv.next();
														
														if ("E".equals(dcpSubv.getCodTipo().toString())) {
															if (dcpSubv.getImporte() != null) {
																subvEnesa+=dcpSubv.getImporte().doubleValue();
															}
														} else if ("C".equals(dcpSubv.getCodTipo().toString())) {
															if (dcpSubv.getImporte() != null) {
																subvCCAA+=dcpSubv.getImporte().doubleValue();
															}
														}
													}
													
													auxSetCell(row.createCell(23), wb, subvEnesa);
													auxSetCell(row.createCell(24), wb, subvCCAA);
												}
												
												if (dcp2015.getCostetomador() != null) {
													auxSetCell(row.createCell(25), wb, dcp2015.getCostetomador().doubleValue());
												}
												
												break;
												
												
											}
										}
										
									}

								}
								
							}
						}
						
					}

				}

			ExcelUtils.autoajustarColumnas(sheet, 26);

			logger.debug("InformesExcelManager - generarDetallePolizaParcelas # END");

			return wb;

		} catch (Exception e) {
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + e.getMessage());
			throw new BusinessException(ERROR_GUARDAR_EXCEL, e);
		}
	}

	public HSSFWorkbook generarDetallePolizaParcelasSitAct(
			es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza polizaSW, Poliza poliza, es.agroseguro.contratacion.Poliza polizaSWAct)
			throws BusinessException {
		logger.debug("InformesExcelManager - generarDetallePolizaParcelasSitAct # INIT");

		HSSFWorkbook wb = new HSSFWorkbook();

		HSSFSheet sheet = wb.createSheet();
		Map<String, es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela> parcelasActualizadas = new HashMap<String, es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela>();
		

		try {

			HSSFCell cell;

			HSSFRow cabecera = sheet.createRow(0);
			HSSFCellStyle estiloCabecera = ExcelUtils.getEstiloCabecera(wb);

			String[] headers = new String[] { "MODULO", "HOJA", "PARCELA", "CULTIVO", "NOM_CULTIVO", "VARIEDAD",
					"NOM_VARIEDAD", "PROVINCIA", "COMARCA", "TERMINO", "NOM_TERMINO", "SUBTERMINO", "NOM_PARCELA",
					"SUPERFICIE", "PRECIO", "PRODUCCION", "TIPO_CAPITAL", "PRIMA_COMERCIAL", "BONIFICACIONES",
					"BONIF_MEDIDAS", "PRIMA_NETA", "REC_CONSORCIO", "RECIBO_PRIMA", "SUB_ENESA", "SUB_CCAA",
					"COSTE_TOMADOR" };

			for (int i = 0; i < headers.length; i++) {
				cell = cabecera.createCell(i);
				cell.setCellValue(new HSSFRichTextString(headers[i]));
				cell.setCellStyle(estiloCabecera);
			}

			if (polizaSW != null) {
				
				if (!ArrayUtils.isEmpty(polizaSW.getObjetoAseguradoArray())) {

					logger.debug("XML de poliza con " + polizaSW.getObjetoAseguradoArray().length + " parcelas");
					
					int i = 1;
					ObjetosAsegurados objetos = polizaSWAct.getObjetosAsegurados();
					Node node2 = objetos.getDomNode().getFirstChild();
					
					while (node2 != null) {
						
						
						if (node2.getNodeType() == Node.ELEMENT_NODE) {
							es.agroseguro.contratacion.parcela.ParcelaDocument parcelaDocumentAct = null;
							try { 
								parcelaDocumentAct = es.agroseguro.contratacion.parcela.ParcelaDocument.Factory.parse(node2);
				            } catch (XmlException e) { 
				                logger.error("Error al parsear una parcela.", e); 
				                
				            }

							es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela parcelaAct = parcelaDocumentAct.getParcela();
							String key = parcelaAct.getHoja() + "-" + parcelaAct.getNumero();
							
							parcelasActualizadas.put(key, parcelaAct);
						}
						node2 = node2.getNextSibling();
					}
					
					for (es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.ObjetoAsegurado objetoAsegurado : polizaSW
							.getObjetoAseguradoArray()) {

						if (objetoAsegurado != null) {
							
							logger.debug("Procesando objeto asegurado numero " + i);
							Node node = objetoAsegurado.getDomNode().getFirstChild();
							
							
							while (node != null) {
								
								if (node.getNodeType() == Node.ELEMENT_NODE) {

									es.agroseguro.seguroAgrario.distribucionCoste.parcela.ParcelaDocument parcelaDocument = null;
									try {
										logger.debug("Transformando XML de parcela");
										parcelaDocument = es.agroseguro.seguroAgrario.distribucionCoste.parcela.ParcelaDocument.Factory
												.parse(node);
									} catch (XmlException e) {
										logger.error("Error al parsear una parcela.", e);
									}

									if (parcelaDocument != null) {
										
										logger.debug("Obteniendo parcela de poliza.");
										es.agroseguro.seguroAgrario.distribucionCoste.parcela.ParcelaDocument.Parcela parcelaSW = parcelaDocument
												.getParcela();
									


										es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela parcelaAct = parcelasActualizadas.get(parcelaSW.getHoja()+"-"+parcelaSW.getNumero());
										
									
										//Parcela parcela = getPolizaParcelaByHojaNumero(poliza.getParcelas(),
												//parcelaSW.getHoja(), parcelaSW.getNumero());

										if (parcelaAct != null) {

											logger.debug("Creando fila numero " + i);
											HSSFRow row = sheet.createRow(i++);
											
											// MODULO
											//auxSetCell(row.createCell(0), wb, poliza.getCodmodulo());
											auxSetCell(row.createCell(0), wb, polizaSWAct.getCobertura().getModulo());
											// HOJA
											auxSetCell(row.createCell(1), wb, parcelaSW.getHoja());
											// PARCELA
											auxSetCell(row.createCell(2), wb, parcelaSW.getNumero());

											VariedadId variedadId = new VariedadId(poliza.getLinea().getLineaseguroid(),
													BigDecimal.valueOf(parcelaAct.getCosecha().getCultivo()), BigDecimal.valueOf(parcelaAct.getCosecha().getVariedad()));
											Variedad variedad = (Variedad) this.informesExcelDao.get(Variedad.class,
													variedadId);
											// CULTIVO
											auxSetCell(row.createCell(3), wb,
													variedad.getId().getCodcultivo().doubleValue());
											// NOM_CULTIVO
											auxSetCell(row.createCell(4), wb, variedad.getCultivo().getDescultivo());
											// VARIEDAD
											auxSetCell(row.createCell(5), wb,
													variedad.getId().getCodvariedad().doubleValue());
											// NOM_VARIEDAD
											auxSetCell(row.createCell(6), wb, variedad.getDesvariedad());


											Character subtermino = parcelaAct.getUbicacion().getSubtermino().isEmpty() ? ' ' : parcelaAct.getUbicacion().getSubtermino().charAt(0);
											
											//Termino termino = parcela.getTermino();
											TerminoId terminoid = new TerminoId(new BigDecimal (parcelaAct.getUbicacion().getProvincia()),
													new BigDecimal(parcelaAct.getUbicacion().getTermino()), subtermino,
													new BigDecimal(parcelaAct.getUbicacion().getComarca()));

											Termino terminoaux = (Termino) this.informesExcelDao.getObject(Termino.class,
													terminoid);
													
													// PROVINCIA
											auxSetCell(row.createCell(7), wb,
													terminoaux.getId().getCodprovincia().doubleValue());
											// COMARCA
											auxSetCell(row.createCell(8), wb,
													terminoaux.getId().getCodcomarca().doubleValue());
											// TERMINO
											auxSetCell(row.createCell(9), wb,
													terminoaux.getId().getCodtermino().doubleValue());
											
											Date fechaInicioContratacion = poliza.getLinea().getFechaInicioContratacion();
											
											// NOM_TERMINO
											auxSetCell(row.createCell(10), wb, terminoaux.getNomTerminoByFecha(fechaInicioContratacion, false));
											// SUBTERMINO
											auxSetCell(row.createCell(11), wb,
													String.valueOf(terminoaux.getId().getSubtermino()));
											// NOM_PARCELA
											auxSetCell(row.createCell(12), wb, parcelaAct.getNombre());
											
											

											es.agroseguro.contratacion.parcela.CapitalAsegurado cap = null;
											for (int j=0; j< parcelaAct.getCosecha().getCapitalesAsegurados().getCapitalAseguradoArray().length; j++ ) {
												es.agroseguro.contratacion.parcela.CapitalAsegurado capAux = parcelaAct.getCosecha().getCapitalesAsegurados().getCapitalAseguradoArray()[j];
												
												int parcelatipo = parcelaSW.getTipo();
												int captipo = capAux.getTipo();
												if (parcelatipo == captipo) {
													cap = capAux;
													break;
												}
											}
											
											if (cap != null) {

												// SUPERFICIE
												auxSetCell(row.createCell(13), wb, cap.getSuperficie().doubleValue());
												BigDecimal precio = null;
												BigDecimal produccion = null;
												/*if (!CollectionUtils.isEmpty(cap.getCapAsegRelModulos())) {
													Iterator<CapAsegRelModulo> itCaRm = cap.getCapAsegRelModulos()
															.iterator();
													while (itCaRm.hasNext()) {
														CapAsegRelModulo caRm = itCaRm.next();
														if (poliza.getCodmodulo().equals(caRm.getCodmodulo())) {
															precio = caRm.getPrecio();
															produccion = caRm.getProduccion();
															break;
														}
													}
												}*/
												
												precio = cap.getPrecio();
												produccion = new BigDecimal (cap.getProduccion());
												
												if (precio != null)
													auxSetCell(row.createCell(14), wb, precio.doubleValue());
												
												if (produccion != null)
													auxSetCell(row.createCell(15), wb, produccion.doubleValue());
												
												// PRECIO
												/*if (precio == null) {
													if (cap.getPrecio() != null) {
														auxSetCell(row.createCell(14), wb,
																cap.getPrecio().doubleValue());
													}
												} else {
													auxSetCell(row.createCell(14), wb, precio.doubleValue());
												}
												// PRODUCCION
												if (produccion == null) {
													if (cap.getProduccion() != null) {
														auxSetCell(row.createCell(15), wb,
																cap.getProduccion().doubleValue());
													}
												} else {
													auxSetCell(row.createCell(15), wb, produccion.doubleValue());
												}*/
											}

											// TIPO_CAPITAL
											auxSetCell(row.createCell(16), wb, parcelaSW.getTipo());

											double totalBonif = 0;
											double totalBonifMedidas = 0;
											double subvEnesa = 0;
											double subvCCAA = 0;

											es.agroseguro.seguroAgrario.distribucionCoste.DistribucionCoste dcp = parcelaSW
													.getDistribucionCoste1();

											if (!ArrayUtils.isEmpty(dcp.getSubvencionEnesaArray())) {

												for (es.agroseguro.seguroAgrario.distribucionCoste.SubvencionEnesa subv : dcp
														.getSubvencionEnesaArray()) {

													subvEnesa += subv.getImporte() == null ? 0
															: subv.getImporte().doubleValue();
												}
											}
											if (!ArrayUtils.isEmpty(parcelaSW.getSubvencionParcelaCCAAArray())) {

												for (es.agroseguro.seguroAgrario.distribucionCoste.parcela.SubvencionParcelaCCAA subv : parcelaSW.getSubvencionParcelaCCAAArray()) {

													subvCCAA += subv.getSubvencionCA() == null ? 0
															: subv.getSubvencionCA().doubleValue();
												}
											}
											if (!ArrayUtils.isEmpty(dcp.getBonificacionRecargoArray())) {

												for (es.agroseguro.seguroAgrario.distribucionCoste.BonificacionRecargo br : dcp
														.getBonificacionRecargoArray()) {

													BonificacionRecargo brAux = (BonificacionRecargo) this.informesExcelDao
															.get(BonificacionRecargo.class, new Long(br.getCodigo()));
													Character tipoBR = brAux.getTipBonRec();
													if (new Character('B').equals(tipoBR)) {
														totalBonif += br.getImporte() == null ? 0
																: br.getImporte().doubleValue();
													} else if (new Character('R').equals(tipoBR)) {
														totalBonif -= br.getImporte() == null ? 0
																: br.getImporte().doubleValue();
													}
												}
											}

											if (dcp.getPrimaComercial() != null)
												auxSetCell(row.createCell(17), wb,
														dcp.getPrimaComercial().doubleValue());
											auxSetCell(row.createCell(18), wb, totalBonif);
											auxSetCell(row.createCell(19), wb, totalBonifMedidas);
											if (dcp.getPrimaComercialNeta() != null)
												auxSetCell(row.createCell(20), wb,
														dcp.getPrimaComercialNeta().doubleValue());
											if (dcp.getRecargoConsorcio() != null)
												auxSetCell(row.createCell(21), wb,
														dcp.getRecargoConsorcio().doubleValue());
											if (dcp.getReciboPrima() != null)
												auxSetCell(row.createCell(22), wb, dcp.getReciboPrima().doubleValue());
											auxSetCell(row.createCell(23), wb, subvEnesa);
											auxSetCell(row.createCell(24), wb, subvCCAA);
											if (dcp.getCosteTomador() != null)
												auxSetCell(row.createCell(25), wb, dcp.getCosteTomador().doubleValue());
										}
									}
								}
								
								node = node.getNextSibling();
								
							}
						}
					}
				}
			}

			ExcelUtils.autoajustarColumnas(sheet, 26);

			logger.debug("InformesExcelManager - generarDetallePolizaParcelasSitAct # END");

			return wb;

		} catch (Exception e) {
			logger.error(LOGGER_ERROR_GUARDAR_EXCEL + e.getMessage());
			throw new BusinessException(ERROR_GUARDAR_EXCEL, e);
		}
	}

	private Parcela getPolizaParcelaByHojaNumero(Set<Parcela> parcelas, Integer hoja, Integer numero) {
		
		Parcela parcela = null;
		
		for (Parcela pAux : parcelas) {
			
			if (pAux.getHoja().equals(hoja) && pAux.getNumero().equals(numero)) {
				
				parcela = pAux;
				break;
			}
		}
		return parcela;
	}

	private void auxSetCell(HSSFCell cell, HSSFWorkbook wb, Double value) {
		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
		cell.setCellValue(value);
	}

	private void auxSetCell(HSSFCell cell, HSSFWorkbook wb, Integer value) {
		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
		cell.setCellValue(value);
	}

	private void auxSetCell(HSSFCell cell, HSSFWorkbook wb, String value) {
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(value);
	}		
}
