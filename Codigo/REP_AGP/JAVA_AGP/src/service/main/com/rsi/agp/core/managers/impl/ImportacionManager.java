/*
**************************************************************************************************
*
*  CREACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034  21/06/2010  Ernesto Laura     Manager que gestiona todo lo relacionado
* 											con las importaciones y sus historicos      
*
 **************************************************************************************************
*/
package com.rsi.agp.core.managers.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.NumberUtils;
import com.rsi.agp.dao.filters.commons.RelacionTablasXmlFiltro;
import com.rsi.agp.dao.filters.log.HistImportacionesFiltro;
import com.rsi.agp.dao.models.commons.ICommonDao;
import com.rsi.agp.dao.models.commons.IRelacionTablasDao;
import com.rsi.agp.dao.models.log.IHistImportacionesDao;
import com.rsi.agp.dao.tables.cgen.TablaCondicionado;
import com.rsi.agp.dao.tables.commons.RelacionTablaXml;
import com.rsi.agp.dao.tables.log.HistImportaciones;
import com.rsi.agp.dao.tables.log.ImportacionTabla;
import com.rsi.agp.dao.tables.log.TipoImportacion;
import com.rsi.agp.dao.tables.poliza.Linea;


@SuppressWarnings("unchecked")
public class ImportacionManager implements IManager {
	private final Log logger = LogFactory.getLog(getClass());
	
	//Constantes
	public static final int IMPORTACION_COMPLETA = 1;
	public static final int IMPORTACION_ORGANIZADOR = 2;
	public static final int IMPORTACION_COND_GENERAL = 3;
	public static final int IMPORTACION_PLAN_LINEA = 4;
	
	private IRelacionTablasDao relacionTablasXmlDao;
	private IHistImportacionesDao histImportacionesDao;
	private ICommonDao commonDao;
	
	public HashMap<String, Object> cargaDatosBasicosImportaciones() throws Exception
	{
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		ArrayList<TablaCondicionado> organizador = new ArrayList<TablaCondicionado>();
		ArrayList<TablaCondicionado> condGeneral = new ArrayList<TablaCondicionado>();
		ArrayList<TablaCondicionado> condPL =  new ArrayList<TablaCondicionado>();
		
		//Obtenemos los tipos de importacion para disponer de sus rutas
		List <TipoImportacion> tiposImp = commonDao.getTiposImportacion();
		for (TipoImportacion tip:tiposImp)
		{
			switch (tip.getIdtipoimportacion().intValue())			
			{
				case ImportacionManager.IMPORTACION_COMPLETA: parameters.put("rutaImpTodo", tip.getUbicacionraiz());
						break;
				case ImportacionManager.IMPORTACION_ORGANIZADOR:	parameters.put("rutaImpOrganizador", tip.getUbicacionraiz());
						break;
				case ImportacionManager.IMPORTACION_COND_GENERAL:	parameters.put("rutaImpGeneral", tip.getUbicacionraiz());
						break;				
				case ImportacionManager.IMPORTACION_PLAN_LINEA:	parameters.put("rutaImpPL", tip.getUbicacionraiz());
						break;
				default:
						break;
			}
				
		}		
		
		//Leemos los ficheros txt que haya en la carpeta de buzón
		//para cargar las select de la pantalla con los ficheros 
		//que haya disponibles para importar

		String ruta = tiposImp.get(0).getUbicacionraiz();
		logger.debug("Ruta para cargar las tablas en los select multi: " + ruta);		
		
		File dir = new File(ruta);
		String[] ficheros = dir.list();
		StringBuffer cadenaParentesis = new StringBuffer();
		BigDecimal [] tablas;
		String []tablasString;
		String parametroFichTablas = null;
		
		if (ficheros !=null)
		{
			parametroFichTablas = "";
			for (int z=0; z<ficheros.length; z++)
			{
				logger.debug("Fichero en la ruta - " + ficheros[z]);				
				File fich = new File(ficheros[z]);
				if (fich.getName().endsWith(".TXT"))
				{
					cadenaParentesis = new StringBuffer();
					try (BufferedReader bf = new BufferedReader(new FileReader(ruta + ficheros[z]))) {
						String linea = bf.readLine();
						logger.debug("Tablas incluidas en el TXT: " + linea);
						if (linea != null)
							cadenaParentesis.append(linea.substring(linea.indexOf('(') + 1, linea.lastIndexOf(')')));
					}
					//Establecemos la relacion fichero->tablas que trae
					parametroFichTablas = fich.getName().substring(0, fich.getName().lastIndexOf('.'));
					
					//Creamos un array con las tablas para crear el filtro		
					tablasString = cadenaParentesis.toString().split(",");
					tablas = new BigDecimal[tablasString.length];
					for (int i=0; i<tablasString.length;i++)
						try{
							tablas[i] = new BigDecimal(tablasString[i].trim());
						}
						catch (Exception e){
							tablas[i] = null;
						}
					logger.debug("Numero de tablas en el TXT: " + tablas.length);
					//Creamos e informamos el filtro
					RelacionTablasXmlFiltro filtro = new RelacionTablasXmlFiltro();
					filtro.setIdsTablas(tablas);
					List<RelacionTablaXml> result = this.relacionTablasXmlDao.getObjects(filtro);
					logger.debug("Numero de tablas obtenidas de la base de datos a partir del TXT: " + result.size());
					for (RelacionTablaXml relacion : result)
					{
						logger.debug("Tratando la tabla correspondiente al fichero " + relacion.getXml());
						if (relacion.getTiposc().equals("ORG")){
							//organizador.put(relacion.getNumtabla().toString(), relacion.getXml());
							TablaCondicionado tb = new TablaCondicionado();
							tb.setCodtablacondicionado(relacion.getNumtabla());
							tb.setDestablacondicionado(relacion.getXml());
							tb.setFichero(parametroFichTablas);
							organizador.add(tb);
							
						}
						else if (relacion.getTiposc().equals("GEN")){							
							//condGeneral.put(relacion.getNumtabla().toString(), relacion.getXml());
							TablaCondicionado tb = new TablaCondicionado();
							tb.setCodtablacondicionado(relacion.getNumtabla());
							tb.setDestablacondicionado(relacion.getXml());
							tb.setFichero(parametroFichTablas);
							condGeneral.add(tb);
						}
						else if (relacion.getTiposc().equals("CPL")){
							TablaCondicionado tb = new TablaCondicionado();
							if (relacion.getId().intValue() != 9)
							{
								tb.setCodtablacondicionado(relacion.getNumtabla());
								tb.setDestablacondicionado(relacion.getXml());
								tb.setFichero(parametroFichTablas);
								condPL.add(tb);
							}
						}
					}
					logger.debug("Fin de la carga de las posibles tablas a importar");
				}
			}
		}
		
		//Guardamos la relacion fichero->tablas
		parameters.put("lstorganizador", organizador);
		parameters.put("lstcondgeneral", condGeneral);
		parameters.put("lstcondpl", condPL);

		return parameters;
	}
	
	public HashMap<String, Object> cargaDatosBasicosHistorico() throws Exception
	{
		HashMap<String, Object> historico = new HashMap<String, Object>();
		HashMap<String, Object> estados = new HashMap<String, Object>();
		
		//Cargar todos los planes, lineas, tipos y estados
		//PLANES y lineas
		List<?> planes = commonDao.getPlanes();
		historico.put("planes", planes);

		//TIPOS		
		List<?> tiposImp = commonDao.getTiposImportacion();
		historico.put("tipos", tiposImp);
		
		//ESTADOS
		estados.put("Importado", "Importado");
		estados.put("Error", "Error");
		historico.put("estados", estados);
		
		HistImportacionesFiltro filtro = new HistImportacionesFiltro();
		List<?> resultHistorico = histImportacionesDao.getObjects(filtro);
		historico.put("resultado", resultHistorico);		

		return historico;
	}
	
	public HashMap<String, Object> consultaHistorico(HttpServletRequest request, String plan, String lineaSeguro, String tiposc, String estado, String fxDesde, String fxHasta) throws Exception
	{	
		HashMap<String, Object> historico = new HashMap<String, Object>();
		final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		final String cadenaFechaIni = fxDesde;
		final String cadenaFechaFin = fxHasta;
		
		HistImportacionesFiltro filtro = new HistImportacionesFiltro();
		
		if (request.getSession().getAttribute("filtroHistorico") != null)
		{
			filtro = (HistImportacionesFiltro)request.getSession().getAttribute("filtroHistorico");
		} 
		else
		{
			Linea lin = null;
			if (plan != null && lineaSeguro.equalsIgnoreCase("smsAviso"))
			{
				lin = new Linea();
				lin.setCodplan(new BigDecimal(plan));
				filtro.setLinea(lin);
			}
			if (lineaSeguro != null && !lineaSeguro.equalsIgnoreCase("") && !lineaSeguro.equalsIgnoreCase("smsAviso"))
			{		
				lin = new Linea();
				lin.setLineaseguroid(new Long(lineaSeguro));
				filtro.setLinea(lin);
			}
			if (tiposc != null && !tiposc.equalsIgnoreCase(""))
			{
				TipoImportacion ti = new TipoImportacion();
				ti.setIdtipoimportacion(new Long(tiposc));
				filtro.setTipoImportacion(ti);
			}
			if (estado != null && !estado.equalsIgnoreCase(""))
				filtro.setEstado(estado);
			if (fxDesde != null  && !fxDesde.equalsIgnoreCase(""))
			{		
				Date fDesde = sdf.parse(cadenaFechaIni);
				filtro.setFechaDesde(fDesde);
			}
			if (fxHasta != null && !fxHasta.equalsIgnoreCase(""))
			{			
				Date fHasta = sdf.parse(cadenaFechaFin);
				filtro.setFechaHasta(fHasta);
			}
		}
		request.getSession().setAttribute("filtroHistorico", filtro);
		historico = cargaDatosBasicosHistorico();
		List<?> resultHistorico = histImportacionesDao.getObjects(filtro);
		historico.put("resultado", resultHistorico);
		return historico;
	}
	
	public HashMap<String, Object> consultaDetalleHistorico(String idHistorico) throws Exception
	{				
		HashMap<String, Object> detalleHistorico = new HashMap<String, Object>();
		
		//Obtenemos los detalles para la cabecera
		HistImportacionesFiltro filtroHist = new HistImportacionesFiltro();
		filtroHist.setIdhistorico(new Long(idHistorico));
		List<HistImportaciones> resultHistorico = (List<HistImportaciones>) histImportacionesDao.getObjects(filtroHist);
		
		HistImportaciones resultado = resultHistorico.get(0);
		
		if (resultHistorico.size() > 1)
		{
			Set<ImportacionTabla> tablas = resultado.getImportacionTablas();
			for (int k = 1; k < resultHistorico.size(); k++)
			{
				tablas.addAll(resultHistorico.get(k).getImportacionTablas());
			}
			resultado.setImportacionTablas(tablas);
		}

		detalleHistorico.put("listaDetalle", resultado);
		return detalleHistorico;
	}

	/**
	 * Método para comprobar que el plan/línea introducido por el usuario en la pantalla coincide
	 * con el de todos los ficheros que vamos a importar
	 * @param plan 
	 * @param linea
	 * @param ruta Ruta donde se encuentran los ficheros
	 * @param ficheros Nombres de los ficheros
	 * @param tipoImportacion
	 * @return True si todos los ficheros son correctos. False si hay alguno incorrecto
	 */
	public boolean validarPlanLinea(String plan, String linea, String ruta, String[] ficheros, int tipoImportacion) {
		boolean correcto = true;
		
		if (tipoImportacion != ImportacionManager.IMPORTACION_COND_GENERAL){
			String actual = "";
			for (String fichero : ficheros){
				if (!actual.equals(fichero)){
					logger.debug("Validamos el plan/linea del fichero " + fichero + ".TXT");
					actual = fichero;
					File f = new File(ruta + fichero + ".TXT");
					try {
						InputStream is = new FileInputStream(f);
						StringWriter writer = new StringWriter();
						IOUtils.copy(is, writer);
						String text = writer.toString();
						
						if (text.indexOf("plan") >= 0){
							String plan1 = "plan " + plan;
							String plan2 = "plan " + NumberUtils.formatear(new BigDecimal(plan), 0);
							
							if ((!text.contains((CharSequence) plan1)) && (!text.contains((CharSequence) plan2))) {
								correcto = false;
								break;
							}
							if (!text.contains((CharSequence) linea)) {
								correcto = false;
								break;
							}
						}
						else{
							//en caso de importaciones completas, los ficheros del condicionado general no se tratan.
							logger.debug("El fichero " + fichero + ".TXT es del condicionado general");
						}
					} catch (FileNotFoundException e) {
						logger.error("No se encontró el fichero " + fichero, e);
						correcto = false;
						break;
					} catch (IOException e) {
						logger.error("Error al leer el fichero " + fichero, e);
						correcto = false;
						break;
					}
				}
			}
		}
		
		return correcto;
	}

	public void setHistImportacionesDao(IHistImportacionesDao histImportacionesDao) {
		this.histImportacionesDao = histImportacionesDao;
	}

	public void setRelacionTablasXmlDao(IRelacionTablasDao relacionTablasXmlDao) {
		this.relacionTablasXmlDao = relacionTablasXmlDao;
	}

	public void setCommonDao(ICommonDao commonDao) {
		this.commonDao = commonDao;
	}

}
