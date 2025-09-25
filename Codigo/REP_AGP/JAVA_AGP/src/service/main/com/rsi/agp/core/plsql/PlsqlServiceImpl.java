package com.rsi.agp.core.plsql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.UnzipException;
import com.rsi.agp.core.managers.impl.ImportacionManager;
import com.rsi.agp.core.managers.impl.PantallasConfigurablesManager;
import com.rsi.agp.core.util.ZipUtil;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.config.PantallaConfigurableConsultaFiltro;
import com.rsi.agp.dao.models.IPlsqlDao;
import com.rsi.agp.dao.models.commons.ICommonDao;
import com.rsi.agp.dao.models.log.IHistImportacionesDao;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;
import com.rsi.agp.dao.tables.poliza.Linea;

public class PlsqlServiceImpl implements IPlsqlService {
	
	private final Log logger = LogFactory.getLog(getClass());
	
	private IPlsqlDao plsqlDao;
	private ICommonDao commonDao;
	private IHistImportacionesDao histImportacionesDao;
	
	private PantallasConfigurablesManager pantallasConfigurablesManager;
	
	private int tipoImportacion;
	private String plan;
	private String linea;
	private String tablas;
	private String fichTablas;
	
	private String nombreFichero;
	private String classpath;
	
	private final String SEPARADOR_STR_PRP = "importacion.separador";
	private final String IMPORTACION_STR_PRP = "importacion.";
	
	public PlsqlServiceImpl() {
		super();
	}

	@Override
	/**
	 * Metodo para lanzar la carga de los ficheros xml en la base de datos
	 */
	public void doTask() throws Exception {
		ResourceBundle rb = ResourceBundle.getBundle("agp_importacion");
		logger.debug("Parametro importacion.moverFicherosZip = " + rb.getString("importacion.moverFicherosZip"));
		boolean mover = Boolean.parseBoolean(rb.getString("importacion.moverFicherosZip"));
		String ruta = rb.getString("ruta_carga");
		Long lineaseguroid = null;
		// Simulate long running task
		// Aqui va la llamada al PLSQL
		try {
			logger.info("Iniciando la ejecucion de la insercion de los ficheros de Agroseguro...");
			
			logger.info("Obteniendo el identificador de plan/linea");
			if (this.tipoImportacion != ImportacionManager.IMPORTACION_COND_GENERAL){
				//Obtener de BBDD el valor existente o insertar uno nuevo
				lineaseguroid = this.getLineaseguroid();
			}
			
			logger.info("Recorremos las tablas a importar y transformamos los ficheros del condicionado");
			
			//llegará por cada tabla el nombre del fichero de donde hay que importarla
			String[] ficherosZIP = fichTablas.substring(0, this.fichTablas.lastIndexOf(
					rb.getString(SEPARADOR_STR_PRP))).split(rb.getString(SEPARADOR_STR_PRP));
			
			String[] tablasImportar = this.tablas.substring(0, this.tablas.lastIndexOf(
					rb.getString(SEPARADOR_STR_PRP))).split(rb.getString(SEPARADOR_STR_PRP));

			String dateFormat = rb.getString("importacion.dateFormat");
			
			StringBuilder tablasStr = new StringBuilder();
			StringBuilder tablasErrorStr = new StringBuilder();
			StringBuilder ficherosStr = new StringBuilder();
			Map<String, String> tablasError = new HashMap<String, String>();
			
			for (int i = 0; i < tablasImportar.length; i++){
				//Descomprimimos el fichero xml del zip correspondiente
				this.descomprimirZip(ruta, ficherosZIP[i] + ".ZIP", rb.getString(IMPORTACION_STR_PRP + tablasImportar[i] + ".xml"));
				
				ProcessBuilder proc = null;
				//Para la tabla de zonificacion SIGPAC necesitamos consultar el ultimo id insertado
				if (tablasImportar[i].equals("42")){
					logger.debug("Carga de zonificaciones. Primero consultaremos el máximo id en la tabla de zonificacion");
					int maxId;
					try {
						maxId = this.commonDao.getMaxIdZonificacionSIGPAC();
						logger.debug("Max Id de zonificacion: " + maxId);
					} catch (DAOException e) {
						logger.error("Error al calcular el máximo de zonificacion SIGPAC", e);
						maxId = 0;
					} catch (Exception e) {
						logger.error("Error inesperado al calcular el máximo de zonificacion SIGPAC", e);
						maxId = 0;
					}
					
					proc = generarProcessBuilder(rb, ruta, tablasImportar[i], dateFormat, Long.valueOf(maxId));
				}
				else{
					proc = generarProcessBuilder(rb, ruta, tablasImportar[i], dateFormat, lineaseguroid);
				}
				
				try{
					Process p = proc.start();
					
					int errCode = p.waitFor();
					
					logger.debug("Echo command executed, any errors? " + (errCode == 0 ? "No" : "Yes"));
					logger.debug("Echo Output:\n" + output(p.getInputStream()));   
					
					logger.debug("Fichero  " + rb.getString(IMPORTACION_STR_PRP + tablasImportar[i] + ".xml") + " canonizado ("+p.exitValue()+").");
		            if (p.exitValue() != 0){
		            	//Se ha producido algun error durante la canonizacion. 
		            	//Eliminamos de la tabla esta importacion e insertamos un registro con error en el detalle de la importacion.
		            	if (!tablasImportar[i].equals("8R") && !tablasImportar[i].equals("406R")){
		            		tablasErrorStr.append(tablasImportar[i] + rb.getString(SEPARADOR_STR_PRP));
		            		tablasError.put(tablasImportar[i], StringUtils.convertStreamToString(p.getErrorStream()));
		            	}
		            	else if (tablasImportar[i].equals("406R")){
		            		tablasErrorStr.append("406" + rb.getString(SEPARADOR_STR_PRP));
		            		tablasError.put("406", tablasError.get("406") + "\n" + StringUtils.convertStreamToString(p.getErrorStream()));
		            	}else{
		            		tablasErrorStr.append("8" + rb.getString(SEPARADOR_STR_PRP));
		            		tablasError.put("8", tablasError.get("8") + "\n" + StringUtils.convertStreamToString(p.getErrorStream()));
		            	}
		            	//Obtenemos el outputstream del proceso y lo pintamos en cosola.
		            	logger.error("Error al transformar: " + StringUtils.convertStreamToString(p.getErrorStream()));
		            }
		            else{
		            	//Añadimos la tabla a la cadena que se le pasa al pl para realizar las inserciones
		            	if (!tablasImportar[i].equals("8R") && !tablasImportar[i].equals("406R")){
		            		//La tabla de Caracteristicas grupo tasas riesgo no la añadimos porque se trata de forma especial
			            	tablasStr.append(tablasImportar[i] + rb.getString(SEPARADOR_STR_PRP));
			            	ficherosStr.append(ficherosZIP[i] + rb.getString(SEPARADOR_STR_PRP));
		            	}
		            }
				}catch(IOException e){
					logger.error("Error durante la transformacion del fichero " + rb.getString(IMPORTACION_STR_PRP + tablasImportar[i] + ".xml"), e);
				}
			}
			
			logger.info("Fin de la transformacion e inicio de la insercion - " + new Date());
			//4.1 Llamar al procedimiento que se encarga de lanzar los procesos de carga.
			if (!StringUtils.isNullOrEmpty(tablasStr.toString())){
				String tablasErrorTransformacion = "";
				if (!StringUtils.isNullOrEmpty(tablasErrorStr.toString())){
					tablasErrorTransformacion = tablasErrorStr.toString().substring(0, tablasErrorStr.toString().lastIndexOf(rb.getString(SEPARADOR_STR_PRP)));
				}
				//Si no ha habido errores al canonizar los ficheros, creamos una copia de seguridad de las tablas
				//a importar por si se produce algun error.
				plsqlDao.ejecutaPLInsercionXML(tablasStr.toString().substring(0, tablasStr.toString().lastIndexOf(rb.getString(SEPARADOR_STR_PRP))), 
						tipoImportacion, rb.getString("importacion.directoryXml"), plan, linea, 
						ficherosStr.toString().substring(0, ficherosStr.toString().lastIndexOf(rb.getString(SEPARADOR_STR_PRP))),
						tablasErrorTransformacion);
				logger.info("Fin de la insercion - " + new Date());
			}
			
			//4.2 Para las tablas que han dado error, los pintamos en el fichero de log de la aplicacion
			if (!tablasError.isEmpty()) {				
				logger.info("Insertamos los errores para las tablas que han dado error");
				for (Map.Entry<String,String> entry : tablasError.entrySet()){
					logger.error("Error al transformar el fichero de la tabla " + entry.getKey() + ": " + entry.getValue());
				}
			}
			
			//5. Borrar los ficheros con extension xml de la ruta para poder continuar con el siguiente.
			borrarXMLs(ruta);
			
			//6. Movemos los ficheros ZIP y TXT a la carpeta copias
			logger.debug("Mover ficheros zip y txt a copias: " + mover);
			if (mover){
				String ficheroAnterior = "";
				for (String ficheroActual : ficherosZIP){
					//Comprobamos si no lo hemos movido ya y, en ese caso, lo movemos
					if (!ficheroActual.equals(ficheroAnterior)){
						logger.debug("Eliminamos el fichero " + ficheroActual);
						moverZIP(ruta, ficheroActual);
					}
					ficheroAnterior = ficheroActual;
				}
			}
			
			//7. Creacion/replicacion de pantallas configurables
			//En caso de no ser una importacion del condicionado general, activamos la linea.
			//Solo vamos a crear la pantalla de confeccion de poliza de manera automatica 
			//si el pl que comprueba si se permite la activacion devuelve "-3" que significa que no hay pantalla configurada.
			if (lineaseguroid != null){
				int resultadoActivacion = this.histImportacionesDao.compruebaRegistrosPL(lineaseguroid.intValue(), Boolean.FALSE);
				if (resultadoActivacion == -3){
					logger.info("Creamos pantallas configurables para lineaseguroid = " + lineaseguroid);
					creaPantallasConfigurables(lineaseguroid);
					
					//Volvemos a llamar al pl de activacion para intentar activar la linea
					resultadoActivacion = this.histImportacionesDao.compruebaRegistrosPL(lineaseguroid.intValue(), Boolean.FALSE);
					logger.info("Resultado de la activacion de la linea " + lineaseguroid + ": " + resultadoActivacion);
				}
				else{
					logger.info("No se pueden crear las pantallas configurables para lineaseguroid = " + lineaseguroid);
					if (resultadoActivacion == 1){
						logger.info("La linea ha sido activada");
					}
					else{
						logger.error("No se pudo activar la linea. Resultado = " + resultadoActivacion);
					}
				}
			}
		}
		catch (InterruptedException ie) {
			logger.error("Error durante la ejecucion de la hebra: " ,ie);
			throw ie;
		} 
		catch (IOException e) {
			logger.error("Error de lectura/escritura de fichero: " ,e);
			throw e;
		} 
		catch (BusinessException e) {
			logger.error(e);
			throw e;
		} 
		catch (UnzipException e) {
			//Insertamos en la tabla del historico un registro indicando que se ha producido un error.
			logger.error(e);
			try {
				if (lineaseguroid != null){
					plsqlDao.executeStatementErrorImportacion(tipoImportacion, StringUtils.eliminaRetornos(e.getMessage()), plan, linea);
				}
				else{
					logger.error("No se pudo obtener el identificador de plan/linea.");
				}
				throw e;
			} catch (BusinessException e1) {
				logger.error("Error durante la insercion del error en la tabla del historico: " , e1);
				throw e1;
			} catch (Exception e1) {
				logger.error("Error indefinido durante la insercion del error en la tabla del historico: " ,e1);
				throw e1;
			}
		}
		catch (Exception e) {
			logger.error("Error indefinido durante la ejecucion de la importacion", e);
			throw e;
		} 
	}

	/**
	 * Metodo para generar el processBuilder que ejecuta la transformacion del fichero xml en el csv
	 * @param rb Para acceder al fichero de propiedades
	 * @param tablaImportar Numero de la tabla a importar
	 * @param dateFormat Formato de la fecha
	 * @param lineaseguroid_maxId Lineaseguroid o máximo id a insertar en el fichero csv resultado (el maxId se usa para zonificacion)
	 * @return
	 */
	private ProcessBuilder generarProcessBuilder(ResourceBundle rb, String ruta,
			String tablaImportar, String dateFormat, Long lineaseguroidMaxId) {
		ProcessBuilder proc;
		
		if (lineaseguroidMaxId == null){
			lineaseguroidMaxId = 0L;
		}
		
		proc = new ProcessBuilder(rb.getString("java_path") + "java", rb.getString("importacion.xms"), rb.getString("importacion.xmx"), 
				"-classpath", this.classpath, rb.getString(IMPORTACION_STR_PRP + tablaImportar + ".class"), 
				ruta + rb.getString(IMPORTACION_STR_PRP + tablaImportar + ".xml"), 
				ruta + rb.getString(IMPORTACION_STR_PRP + tablaImportar + ".csv"), lineaseguroidMaxId + "", dateFormat);
		
		
		
		logger.debug("Llamada al ProcessBuilder: " + rb.getString("java_path") + "java " + rb.getString("importacion.xms") + " " + 
				rb.getString("importacion.xmx") + " -classpath " + this.classpath + " " + 
				rb.getString(IMPORTACION_STR_PRP + tablaImportar + ".class") + " " + 
				ruta + rb.getString(IMPORTACION_STR_PRP + tablaImportar + ".xml") + " " + 
				ruta + rb.getString(IMPORTACION_STR_PRP + tablaImportar + ".csv") + " " + lineaseguroidMaxId + " " + dateFormat);
		return proc;
	}
	
	/**
	 * Metodo para obtener un identificador de plan/linea o bien insertarlo en caso de que no exista
	 * @return LineaSeguroId de la importacion actual.
	 */
	@SuppressWarnings("rawtypes")
	private Long getLineaseguroid() {
		Transaction tx = null;
		Long lineaseguroid = null;
		try {
			tx = this.commonDao.beginTransaction();
			List ids = commonDao.getLineaseguroid(new BigDecimal(plan), new BigDecimal(linea));
			if (ids == null || ids.isEmpty()){
				logger.debug("No se encontraron coincidencias para el plan " + plan + " y la linea " + linea);
				//Insertar un registro en TB_LINEAS y obtenerlo de nuevo
				Linea lineaCond = new Linea();
				lineaCond.setActivo("NO");
				lineaCond.setCodplan(new BigDecimal(plan));
				lineaCond.setCodlinea(new BigDecimal(linea));
				lineaCond.setNomlinea(linea);
				lineaCond.setDiccionarioDatos(null);
				
				logger.debug("Insertamos la nueva linea en la base de datos");
				commonDao.saveOrUpdate(lineaCond);
				lineaseguroid = lineaCond.getLineaseguroid();
				logger.debug("Linea creada en base de datos. Identificador = " + lineaseguroid);
			} else{
				lineaseguroid = ((Linea) ids.get(0)).getLineaseguroid();
			}
			if (!tx.wasCommitted())
				tx.commit();
		} catch (DAOException e) {
			logger.error("Error al crear la linea", e);
			if (tx != null && tx.isActive())
				tx.rollback();
		} catch (Exception e) {
			logger.error("Error indefinido al crear la linea", e);
			if (tx != null && tx.isActive())
				tx.rollback();
		}
		
		return lineaseguroid;
	}
	
	/**
	 * Metodo para mover los ficheros ZIP y TXT a la carpeta copias
	 * @throws BusinessException
	 */
	private void moverZIP(String ruta, String fichero) throws IOException {			
		ArrayList <String> ficherosMover = new ArrayList<String>();
		ArrayList <File> ficherosBorrar = new ArrayList<File>();
		ficherosMover.add(fichero + ".TXT");
		ficherosMover.add(fichero + ".ZIP");
		for (String f : ficherosMover) {
			logger.debug("Moviendo el fichero " + f + " a COPIAS.");
			File fich = new File(ruta + f);
			File dest = new File(ruta + "COPIAS/" + fich.getName());
			dest.delete();
			try (InputStream in = new FileInputStream(fich); OutputStream out = new FileOutputStream(dest)) {
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				ficherosBorrar.add(fich);
				logger.debug("Fichero " + f + " movido a COPIAS correctamente.");
			}
		}
		//Borramos los fichero de origen
		for (File f : ficherosBorrar){
			f.delete();
			logger.debug("Fichero " + ruta + f + " eliminado.");
		}
	}
	
	/**
	 * Metodo para borrar los ficheros XML
	 * @throws IOException
	 */
	private void borrarXMLs(String ruta){
		File dir = new File(ruta);
		String[] ficheros = dir.list(new XMLFileFilter());
		logger.info("Borrado de los ficheros XML - " + (ficheros != null ? ficheros.length : 0) + " ficheros a eliminar.");
		for (int z=0; ficheros != null && z<ficheros.length; z++){
			logger.debug("Borrando el fichero " + ruta + ficheros[z]);
			File fich = new File(ruta + ficheros[z]);
			boolean borrado = fich.delete();
			if (borrado)
				logger.debug(ruta + ficheros[z] + " BORRADO!!");
			else
				logger.debug("NO SE PUDO BORRAR EL FICHERO " + ruta + ficheros[z]);
		}
		
	}
	
	/**
	 * Metodo para la creacion/replicacion de las pantallas configurables
	 * @param lineaseguroid Identificador del plan/linea destino
	 * @throws Exception
	 */	
	@SuppressWarnings("unchecked")
	private void creaPantallasConfigurables(Long lineaseguroid) throws BusinessException
	{
		Transaction tx = null;
		try {
			tx = commonDao.beginTransaction();
			Linea linea;
			logger.debug("Creacion de la pantalla configurable para la linea " + this.getPlan() + ", " + this.getLinea());
			//Comprobamos si tiene configurada una pantalla configurable para el uso "Poliza"
			PantallaConfigurable datosConsulta = new PantallaConfigurable();
			datosConsulta.getLinea().setLineaseguroid(lineaseguroid);
			
			//Consultamos para este plan/linea
			PantallaConfigurableConsultaFiltro filtroConsultaPantalla = new PantallaConfigurableConsultaFiltro(datosConsulta);
			Integer registrosPantalla = commonDao.getNumObjects(filtroConsultaPantalla);
			
			logger.debug("Numero de pantallas configurables para la linea " + this.getPlan() + ", " + this.getLinea() + ": "+ registrosPantalla);

			if (registrosPantalla.intValue() == 0){
				//Si no existe la pantalla deberiamos crearla, a menos que ya exista para la misma linea en un plan anterior
				//en cuyo caso la replicariamos
				
				//Ahora busco la misma pantalla para otro año
				linea = new Linea();
				linea.setCodlinea(new BigDecimal(this.getLinea()));
				datosConsulta = new PantallaConfigurable();
				datosConsulta.setLinea(linea);
				
				//Consultamos para otras lineas
				filtroConsultaPantalla = new PantallaConfigurableConsultaFiltro(datosConsulta);
				List<PantallaConfigurable> pantallasConfiguradas = (List<PantallaConfigurable>) commonDao.getObjects(filtroConsultaPantalla);
				
				logger.debug("Obtenemos las posibles pantallas configurables de otros planes");
				
				//Ahora si que si, si tenemos un registro lo replicamos para el P/L actual
				if (pantallasConfiguradas.size() > 0) {
					logger.debug("HAY pantallas configurables de otros planes");
					Long idLineaOrigen;
					//Antes de nada , necesito obtener el plan de la ultima pantalla configurada
					//Como el listado de pantallas viene ordenado por plan descendente => me quedo con la primera.
					idLineaOrigen = pantallasConfiguradas.get(0).getLinea().getLineaseguroid();
					
					//Llamada al metodo replicar del amigo Miguel Granadino
					logger.debug("Llamada a replicar la configuracion de pantalla con los parametros '" + 
							idLineaOrigen + "', '" + lineaseguroid + "'");
					pantallasConfigurablesManager.replicar(idLineaOrigen, lineaseguroid);
					logger.debug("Replica de pantallas finalizada");
				}
				else {
					logger.debug("NO HAY pantallas configurables de otros planes");
					//La liamos parda para automatizar la creacion de la pantalla
					//Llamada a un PL pasandole el plan y linea para que se cree la pantalla configurable
					plsqlDao.ejecutaConfiguradorPantallasAuto(lineaseguroid.intValue());
					logger.debug("Pantallas configuradas automaticamente");
				}
			} //Ya esta configurada, salimos
			if (!tx.wasCommitted())
				tx.commit();
		} catch (HibernateException e) {
			logger.error("Error durante la creacion de pantallas configurables", e);
			if (tx != null && tx.isActive())
				tx.rollback();
		} catch (Exception e) {
			logger.error("Error indefinido durante la creacion de pantallas configurables", e);
			if (tx != null && tx.isActive())
				tx.rollback();
		}
	}
	
	private void descomprimirZip(String ruta, String fichero, String xml) throws Exception {
		String ficheroZIP = fichero;
		if (fichero.indexOf(".") > -1)
			ficheroZIP = fichero.substring(0, fichero.lastIndexOf('.'));
		//Llamamos al PL que descomprime el fichero ZIP 
		logger.debug("Descomprimiendo " + xml + " del fichero " + ficheroZIP + " en la ruta " + ruta);
		ZipUtil.uncompressFile(ficheroZIP + ".ZIP", ruta, xml);
	}
	
	public static void main(String[] args){
		PlsqlServiceImpl pl = new PlsqlServiceImpl();
		try {
			pl.doTask();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String output(InputStream inputStream) throws IOException {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + System.getProperty("line.separator"));
			}
		}
		return sb.toString();
	}
	
	public int getTipoImportacion() {
		return tipoImportacion;
	}

	public void setTipoImportacion(int tipoImportacion) {
		this.tipoImportacion = tipoImportacion;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public String getLinea() {
		return linea;
	}

	public void setLinea(String linea) {
		this.linea = linea;
	}

	public String getTablas() {
		return tablas;
	}

	public void setTablas(String tablas) {
		this.tablas = tablas;
	}

	public String getNombreFichero() {
		return nombreFichero;
	}

	public void setNombreFichero(String nombreFichero) {
		this.nombreFichero = nombreFichero;
	}

	public void setPlsqlDao(IPlsqlDao plsqlDao) {
		this.plsqlDao = plsqlDao;
	}
	
	public void setCommonDao(ICommonDao commonDao) {
		this.commonDao = commonDao;
	}

	public String getFichTablas() {
		return fichTablas;
	}

	public void setFichTablas(String fichTablas) {
		this.fichTablas = fichTablas;
	}
	
	private class XMLFileFilter implements FilenameFilter{
		public boolean accept(File dir, String name) {
	        return (name.endsWith(".xml"));
	    }
	}

	public String getClasspath() {
		return classpath;
	}

	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}

	public void setPantallasConfigurablesManager(
			PantallasConfigurablesManager pantallasConfigurablesManager) {
		this.pantallasConfigurablesManager = pantallasConfigurablesManager;
	}

	public void setHistImportacionesDao(IHistImportacionesDao histImportacionesDao) {
		this.histImportacionesDao = histImportacionesDao;
	}
}
