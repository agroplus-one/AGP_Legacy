package com.rsi.agp.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.rsi.agp.batch.EnvioPolizasExternas.InformacionFileTxt;
import com.rsi.agp.batch.bbdd.Conexion;
import com.rsi.agp.core.util.FileUtil;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.admin.SubentidadMediadoraId;
import com.rsi.agp.dao.tables.batch.comunicacionesExternas.ErrorComunicacionExterna;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidadesHistorico;

public class ValidaPolizasExternas {

	private List<ErrorComunicacionExterna>resultadoValidaciones;
	
	private static final Logger logger= Logger.getLogger(EnvioPolizasExternas.class);
	
	private Long contadorPolizas = null;
	
	public ValidaPolizasExternas(){
		resultadoValidaciones=new ArrayList<ErrorComunicacionExterna>();	
		contadorPolizas=new Long(0);
	}
	
	public boolean isValidProceso(String pathFichero, String pathTmpValidacion, 
			com.rsi.agp.dao.tables.batch.comunicacionesExternas.CorreduriaExterna correduria, InformacionFileTxt infTxt) throws Exception{
		logger.info("Comienza el proceso de validacion de ficheros XML de la correduria " + correduria.getNombre());
		boolean res=false;
		
		
		String[] ficherosZip = {infTxt.getNombreFichero()+ ".ZIP"};// Se supone que solo hay un ZIP
		File fileTmpVal=null;
		
		try {
			this.resultadoValidaciones.clear();
			fileTmpVal=new File(pathTmpValidacion);
			if(null==ficherosZip || ficherosZip.length==0){
				logger.error("Fichero ZIP vacio");
			}else{
				for (String ficheroZip : ficherosZip) {
					File fileZipOrigen=new File(pathFichero + "/" + ficheroZip);
					File fileZipDestino=new File(pathTmpValidacion + ficheroZip);
					
					deleteFicheros(fileTmpVal);
					//Copia fichero zip en directorio temporal
					logger.info("Copiando fichero ZIP en carpeta temporal de validacion. De "+pathFichero + "/" + ficheroZip + " a " + pathTmpValidacion + ficheroZip);
					com.rsi.agp.core.util.FileUtil.copyFile(fileZipOrigen, fileZipDestino);
					
					// Extrae ficheros xml en directorio temporal
					logger.info("Descomprimiendo fichero ZIP en carpeta temporal de validacion");
					com.rsi.agp.core.util.ZipUtil.uncompressFile(ficheroZip, pathTmpValidacion);
					
					res=isValidFicherosXML(pathTmpValidacion,correduria,infTxt,
							fileZipOrigen.getName().substring(0,fileZipOrigen.getName().lastIndexOf(".")));
					
					fileZipDestino= null;					
				}
			}			
			
		} catch (Exception e) {
			logger.error("Error en el proceso de validacion de las polizas externas.",e);
			throw (e);
		}finally{
			//Borra ficheros de directorio temporal			
			if (null!=fileTmpVal)deleteFicheros(fileTmpVal);			
			logger.info("Borrando todos los ficheros del directorio temporal de validacion.");
		}
		
		return res;		
	}
	
	
	private static  void deleteFicheros(File folder) throws IOException {
		FileUtil.deleteFiles(folder);
	}
	
	private boolean isValidFicherosXML(String pathTmpValidacion, 
			com.rsi.agp.dao.tables.batch.comunicacionesExternas.CorreduriaExterna correduria, InformacionFileTxt infTxt, 
			String nombreFicheroZIP) throws Exception{
		boolean res=true;
		logger.info("Comprobamos los ficheros XML del ZIP");
		File fileXml=null;			
		Date fechaEfecto;
		
		String refPoliza = null; String mediador = null;
		String pathFicheroActual = null;
		
		try {
			
			File dir = new File(pathTmpValidacion);
			
			String[] ficherosXml = dir.list(new XMLFileFilter());
			
			for (String pathFichero : ficherosXml) {
				logger.info("Entramos dentro del for");
				
				// Vacia el contenido de '' y '' por si se produce un error al parsear el xml no se muestren los datos de polizas
				// tratadas anteriormente en el mensaje de error
				refPoliza=null; mediador=null;
				pathFicheroActual = pathFichero;
				
				logger.info("Obtenemos la poliza del XML de tipo " + infTxt.getTipoPolizas() + " del fichero " + pathFichero);
				contadorPolizas+=1;
				String mensajeValidacion=null;				
				
				fileXml=new File(pathTmpValidacion+pathFichero);				
				try (FileInputStream fileInputStream = new FileInputStream(fileXml)) {
					if (infTxt.getTipoPolizas().compareTo("SC") == 0) {// Seguro creciente agricola
						es.agroseguro.seguroAgrario.contratacion.PolizaDocument pol = es.agroseguro.seguroAgrario.contratacion.PolizaDocument.Factory
								.parse(fileInputStream);
						refPoliza = pol.getPoliza().getReferencia();
						mediador = pol.getPoliza().getEntidad().getCodigoInterno();
						fechaEfecto = this.getDateFromCalendar(pol.getPoliza().getPago().getFecha());

						// ESC-5571 ** MODIF TAM (01.04.2019)//
						SubentidadMediadora sm = getESMediadora(pol.getPoliza().getEntidad());
						// Se lo pasamos por parametro a la funcion isValidPoliza //

						if (null != pol.getPoliza().getEntidad().getGastos()) {
							mensajeValidacion = isValidPoliza(
									pol.getPoliza().getEntidad().getGastos().getComisionMediador(),
									pol.getPoliza().getPlan(), pol.getPoliza().getLinea(), fechaEfecto, refPoliza,
									pol.getPoliza().getEntidad().getGastos().getAdministracion(),
									pol.getPoliza().getEntidad().getGastos().getAdquisicion(), sm);
						} else {
							if (pol.getPoliza().getPlan() > 2014)
								mensajeValidacion = "Poliza sin gastos. ";// mensajeValidacion="Poliza sin etiqueta de
																			// gastos. ";
						}
						// ESC-5571 ** MODIF TAM (01.04.2019)* Fin //

					} else if (infTxt.getTipoPolizas().compareTo("SD") == 0) {// Complementaria de Seguro creciente
																				// agricola
						es.agroseguro.seguroAgrario.contratacion.complementario.PolizaDocument pol = es.agroseguro.seguroAgrario.contratacion.complementario.PolizaDocument.Factory
								.parse(fileInputStream);
						refPoliza = pol.getPoliza().getReferencia();
						mediador = pol.getPoliza().getEntidad().getCodigoInterno();
						fechaEfecto = this.getDateFromCalendar(pol.getPoliza().getPago().getFecha());

						// ESC-5571 ** MODIF TAM (01.04.2019)//
						SubentidadMediadora sm = getESMediadora(pol.getPoliza().getEntidad());
						// Se lo pasamos por parametro a la funcion isValidPoliza //

						if (null != pol.getPoliza().getEntidad().getGastos()) {
							mensajeValidacion = isValidPoliza(
									pol.getPoliza().getEntidad().getGastos().getComisionMediador(),
									pol.getPoliza().getPlan(), pol.getPoliza().getLinea(), fechaEfecto, refPoliza,
									pol.getPoliza().getEntidad().getGastos().getAdministracion(),
									pol.getPoliza().getEntidad().getGastos().getAdquisicion(), sm);
						} else {
							mensajeValidacion = "Poliza sin gastos. ";
						}

					} else if (infTxt.getTipoPolizas().compareTo("SG") == 0) {// Seguro creciente de Ganado
						es.agroseguro.contratacion.PolizaDocument pol = es.agroseguro.contratacion.PolizaDocument.Factory
								.parse(fileInputStream);
						refPoliza = pol.getPoliza().getReferencia();
						mediador = pol.getPoliza().getEntidad().getCodigoInterno();
						BigDecimal comisMediador = new BigDecimal("0.00");
						BigDecimal gastosAdmin = new BigDecimal("0.00");
						BigDecimal gastosAdq = new BigDecimal("0.00");

						// ESC-5571 ** MODIF TAM (01.04.2019)//
						SubentidadMediadora sm = getESMediadora(pol.getPoliza().getEntidad());
						// Se lo pasamos por parametro a la funcion isValidPoliza //

						if (pol.getPoliza().getEntidad().getGastosArray().length > 0) {
							for (int i = 0; i < pol.getPoliza().getEntidad().getGastosArray().length; i++) {
								BigDecimal cm = pol.getPoliza().getEntidad().getGastosArray(i).getComisionMediador();
								if (null != cm)
									comisMediador = comisMediador.add(cm);

								BigDecimal gAdmin = pol.getPoliza().getEntidad().getGastosArray(i).getAdministracion();
								if (null != gAdmin)
									gastosAdmin = gastosAdmin.add(gAdmin);

								BigDecimal gAdq = pol.getPoliza().getEntidad().getGastosArray(i).getAdquisicion();
								if (null != gAdq)
									gastosAdq = gastosAdq.add(gAdq);
							}
							fechaEfecto = this.getDateFromCalendar(pol.getPoliza().getPago().getFecha());
							mensajeValidacion = isValidPoliza(comisMediador, pol.getPoliza().getPlan(),
									pol.getPoliza().getLinea(), fechaEfecto, refPoliza, gastosAdmin, gastosAdq, sm);
						} else {
							mensajeValidacion = "Poliza sin gastos. ";
						}

					}
				}
				if(null!=mensajeValidacion && !mensajeValidacion.isEmpty()){
					res=false;
					ErrorComunicacionExterna resVal= new ErrorComunicacionExterna(nombreFicheroZIP, refPoliza,
							mediador,mensajeValidacion);
					this.resultadoValidaciones.add(resVal);
					
				}
				
			}
		
		} catch (Exception e) {
			res=false; 
			logger.error("Error obteniendo la poliza y validandola. ", e);		
			
			ErrorComunicacionExterna resVal= new ErrorComunicacionExterna(nombreFicheroZIP, 
												refPoliza != null ? refPoliza : " ",
												mediador != null ? mediador : " ",
												"Error al procesar el fichero " + (pathFicheroActual != null ? pathFicheroActual : ""));
			
			this.resultadoValidaciones.add(resVal);
		}
		
		return res;
	}
	
	
	private String isValidPoliza(BigDecimal comisionMediador, int plan, int linea, Date fechaEfecto, 
			String referencia, BigDecimal gastosAdmon, BigDecimal gastosAdq,final SubentidadMediadora sm)throws Exception {
		String res="";
		logger.info("Validamos la poliza. " + referencia);
		boolean mayorCero=(comisionMediador!=null && comisionMediador.compareTo(BigDecimal.ZERO)>=0);
		if(!mayorCero){			
			res=" % comisionMediador incorrecto. ";//"La comision del mediador debe ser >= a cero. ";//45
		}
		CultivosEntidadesHistorico comisionHco=getComisionesEnHistorico(plan, linea, fechaEfecto, sm);
		
		if(comisionHco==null){
			res+="Poliza sin gastos. ";//"Comisiones erroneas. No existen en el Hco. ";
		}else{
			if(comisionHco.getPctadministracion().compareTo(gastosAdmon)!=0){
				res+=" % de administracion incorrecto. ";
			}
			if(comisionHco.getPctadquisicion().compareTo(gastosAdq)!=0){
				res+=" % de adquisicion incorrecto.";
			}
		}logger.info(res);
		return res;		
	}
	
	private CultivosEntidadesHistorico getComisionesEnHistorico( int plan, int linea, Date fechaEfecto, final SubentidadMediadora sm
			) throws Exception{
		logger.info("Seleccionamos las comisiones en la tabla de Historico por fecha de efecto. ");		
		CultivosEntidadesHistorico res=null;
		Conexion c = new Conexion();		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String fecEfecto= df.format(fechaEfecto);
		
		//TO_DATE('"+ fechaCarga + "','YYYY-MM-DD')
		// ESC-5571 ** MODIF TAM (01.04.2019) ** Inicio //
		// Se solicita incluir en la validacion los campos de E-S Mediadora para que coja el registro correcto
		// En caso de no encontrar datos, incluimos la E-S Mediadora a nula, y en caso de no encontrar informacion
		// se buscará para el plan y línea genérica.
		
		BigDecimal entMed = sm.getId().getCodentidad();
		BigDecimal subEntMed = sm.getId().getCodsubentidad();
		
		try {
			
			logger.info("CultivosEntidadesHistorico (sql1) EntMed: " +entMed + " - SubEntMed: " +subEntMed + " - Plan: " + plan + " - Linea:" 
					    + linea + " - Fecha Efecto: " + fecEfecto.toString());
			
			String sql1= " Select hco.PCTRGA as pctAdministracion, hco.PCTADQUISICION from O02AGPE0.TB_COMS_CULTIVOS_ENTS_HIST hco " +
					"inner join O02AGPE0.TB_LINEAS l on hco.LINEASEGUROID=l.LINEASEGUROID " + 
					" where l.CODPLAN=" + plan + " and l.CODLINEA=" + linea + 
					" and hco.ENTMEDIADORA=" + entMed + " and hco.SUBENTMEDIADORA=" + subEntMed +
					" and hco.FECHA_EFECTO<= TO_DATE('" + fecEfecto + "', 'YYYY-MM-DD') order by hco.ID desc";
			
			List<Object> resultado1 = c.ejecutaQuery(sql1, 2);
			
			if(resultado1!=null && resultado1.size()>0){
				BigDecimal pctAdmon = (BigDecimal) ((Object[])resultado1.get(0))[0];
				BigDecimal pctAdq = (BigDecimal) ((Object[])resultado1.get(0))[1] ;
				
		  		res = new CultivosEntidadesHistorico();
				res.setPctadministracion(pctAdmon);
				res.setPctadquisicion(pctAdq);
			}else{	
			
			   String sql2 = " Select hco.PCTRGA as pctAdministracion, hco.PCTADQUISICION from O02AGPE0.TB_COMS_CULTIVOS_ENTS_HIST hco " +
				   	"inner join O02AGPE0.TB_LINEAS l on hco.LINEASEGUROID=l.LINEASEGUROID " + 
					" where l.CODPLAN=" + plan + " and l.CODLINEA=" + linea + 
					" and hco.FECHA_EFECTO<= TO_DATE('" + fecEfecto + "', 'YYYY-MM-DD') order by hco.ID desc";
			   
			   logger.info("CultivosEntidadesHistorico (sql2) Plan: " + plan + " - Linea:" + linea + " - Fecha Efecto: " + fecEfecto.toString());
			   
			   List<Object> resultado2 = c.ejecutaQuery(sql2, 2);
			   
			   if(resultado2!=null && resultado2.size()>0){
				   BigDecimal pctAdmon = (BigDecimal) ((Object[])resultado2.get(0))[0];
				   BigDecimal pctAdq = (BigDecimal) ((Object[])resultado2.get(0))[1] ;
				
		  		   res = new CultivosEntidadesHistorico();
				   res.setPctadministracion(pctAdmon);
				   res.setPctadquisicion(pctAdq);
				
			   }else{
				   // Si no hay resultados buscamos por linea generica									
				   String sql3 = " Select hco.PCTRGA as pctAdministracion, hco.PCTADQUISICION from O02AGPE0.TB_COMS_CULTIVOS_ENTS_HIST hco " +
						"inner join O02AGPE0.TB_LINEAS l on hco.LINEASEGUROID=l.LINEASEGUROID " + 
						" where l.CODPLAN=" + plan + " and l.CODLINEA=999" +  
						" and hco.FECHA_EFECTO<= TO_DATE('" + fecEfecto + "', 'YYYY-MM-DD') order by hco.ID desc";
				   
				   logger.info("CultivosEntidadesHistorico (sql3) Plan: " + plan + " - Linea: 999"  + " - Fecha Efecto: " + fecEfecto.toString());
				   List<Object> resultado3 = c.ejecutaQuery(sql3, 2);
				   
				   if(resultado3!=null && resultado3.size()>0){
					  BigDecimal pctAdmon = (BigDecimal) ((Object[])resultado3.get(0))[0];
					  BigDecimal pctAdq = (BigDecimal) ((Object[])resultado3.get(0))[1] ;
					
					  res = new CultivosEntidadesHistorico();
					  res.setPctadministracion(pctAdmon);
					  res.setPctadquisicion(pctAdq);
				   }
			   }
			}
			
				
		} catch (Exception e) {
			logger.error("Error seleccionando las comisiones en la tabla de Historico por fecha de efecto." ,e);
			throw(e);
		}
		return res;
	}
	/* ESC-5571 ** MODIF TAM (01.04.2019) * INICIO */
	/**
	 * @param poliza
	 * @return
	 */
	private static SubentidadMediadora getESMediadora(Object entidad) {

		// Obtener el codigo interno
		String codigoInterno = "";
		if (entidad instanceof es.agroseguro.seguroAgrario.contratacion.Entidad) {
			codigoInterno = ((es.agroseguro.seguroAgrario.contratacion.Entidad) entidad).getCodigoInterno().trim();
		}
		else if (entidad instanceof es.agroseguro.seguroAgrario.contratacion.complementario.Entidad) {
			codigoInterno = ((es.agroseguro.seguroAgrario.contratacion.complementario.Entidad) entidad).getCodigoInterno().trim();
		}
		else {
			return null;
		}
		
		// Entidad mediadora
		BigDecimal entMed = toBigDecimal (codigoInterno.substring(0, 4));
		// Subentidad mediadora
		BigDecimal subEntMed = toBigDecimal (codigoInterno.substring(4));
		
		logger.debug("entMed: " + entMed + " - subEntMed:" + subEntMed);
		
		SubentidadMediadoraId smId = new SubentidadMediadoraId(entMed, subEntMed);
		SubentidadMediadora sm = new SubentidadMediadora();
		sm.setId(smId);
		return sm;
	}
	
	private static BigDecimal toBigDecimal (String numero) {
		try {
			return new BigDecimal (numero.trim());
		} catch (Exception e) {
			return null;
		}
	}	
	/* ESC-5571 ** MODIF TAM (01.04.2019) * FIN */
	
	@SuppressWarnings("deprecation")
	private Date getDateFromCalendar(Calendar cal){
		int year=cal.get(Calendar.YEAR);
		int mes=cal.get(Calendar.MONTH);
		int dia=cal.get(Calendar.DAY_OF_MONTH);
		Date date=new Date(year - 1900,mes,dia);
		return date;
	}
	
	public List<ErrorComunicacionExterna> getResultadoValidaciones() {
		return resultadoValidaciones;
	}

	public void setResultadoValidaciones(
			List<ErrorComunicacionExterna> resultadoValidaciones) {
		this.resultadoValidaciones = resultadoValidaciones;
	}	
	
	private class XMLFileFilter implements FilenameFilter{
		public boolean accept(File dir, String name) {
	        return (name.endsWith(".xml") || name.endsWith(".XML"));
	    }
	}

	public Long getContadorPolizas() {
		return contadorPolizas;
	}

	public void setContadorPolizas(Long contadorPolizas) {
		this.contadorPolizas = contadorPolizas;
	}
}