package com.rsi.agp.batch.comisiones.main;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import com.rsi.agp.batch.comisiones.util.ConfigBuzonInfovia;


public class BorradoFicherosAntiguos {
	private static final Logger logger = Logger.getLogger(BorradoFicherosAntiguos.class);
	public static void doBorradoFicherosAntiguos(Map<String, String[]> mapEtiquetas) {
		String rutaFtp = ConfigBuzonInfovia.getProperty("directorioLocal");
		List<String> lstRutas = new ArrayList<String>();
		Iterator it = mapEtiquetas.keySet().iterator();
		while(it.hasNext()){
		  String key = (String) it.next();
		  String[] strIdRuta = (String[]) mapEtiquetas.get(key);	
		  logger.info("Clave: " + key + " -> Valor: " +strIdRuta[1]);
		  if (!lstRutas.contains(strIdRuta[1])){
			  lstRutas.add(strIdRuta[1]);
		  }
		}
		for (String ruta:lstRutas)
			borrarDirectoriosFtp(ruta);
	}
	
	public static void borrarDirectoriosFtp(String carpeta) {
		GregorianCalendar cal = new GregorianCalendar();
		Date fechaActual = new Date();
		cal.setTime(fechaActual);
		cal.add(Calendar.YEAR, -2);
		SimpleDateFormat s = new SimpleDateFormat("yyyy/MM/dd");
		String dirDelete =carpeta+"/"+s.format(cal.getTime());
		File dir = new File(dirDelete);
		if (dir.exists())
		{
			try {
				FileUtils.cleanDirectory(dir);
				//FileUtils.deleteDirectory(dir);
			} catch (IOException e) {
				logger.info("Error en el borrado del directorio: "+ dir,e);
			}
			
			/*
			File[] ficherosDir = dir.listFiles();
			for (int i= 0 ; i < ficherosDir.length ; i++) {
				ficherosDir[i].delete();
			}
			*/
			//borramos la carpeta del dia 
			dir.delete();
			// si es 1 de marzo y existe la carpeta del 29 de febrero tambien la borramos
			if (cal.get(Calendar.DAY_OF_MONTH) ==  1 && cal.get(Calendar.MONTH)+1 == 3){
				File file29Febrero = new File(carpeta+cal.get(Calendar.YEAR)+"/02/29");
				File file29FebreroMes = new File(carpeta+cal.get(Calendar.YEAR)+"/02");
				if (file29Febrero.exists()){
					File[] ficheros29FebreroDir = file29Febrero.listFiles();
					// borramos el contenido del directorio
					for (int j= 0 ; j < ficheros29FebreroDir.length ; j++)
						ficheros29FebreroDir[j].delete();
					// borramos el directorio del dia
					file29Febrero.delete();
					//borramos el directorio del mes de febrero
					file29FebreroMes.delete();
				}
			}
			if (cal.get(Calendar.DAY_OF_MONTH) == cal.getActualMaximum(Calendar.DAY_OF_MONTH)){
				SimpleDateFormat simple = new SimpleDateFormat("yyyy/MM");
				String dirMonthDelete =carpeta+simple.format(cal.getTime());
				File fileMonth = new File(dirMonthDelete) ;
				fileMonth.delete();	
			}
			// comprobamos que es el último día del anio y si lo es borramos la carpeta del año 
			if (cal.get(Calendar.DAY_OF_MONTH) ==  31 && cal.get(Calendar.MONTH)+1 == 12){
				String dirAnioDelete =carpeta+cal.get(Calendar.YEAR);
				File fileAnyo = new File(dirAnioDelete) ;
				fileAnyo.delete();	
			}
		}
	}
}
