package com.rsi.agp.core.executor;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.XMLConstants;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;

public abstract class GenericXMLParser {
	
	private String tagPrincipal = "";
	public static final String FORMATO_ORIGEN = "yyyy-MM-dd";
	
	//Tags principales
	public static final String TAG_CARACTERISTICA_GRUPO_TASA = "CaracteristicaGrupoTasa";
	public static final String TAG_RG = "RG";
	
	//Constantes para evaluar los tags a tratar en los ficheros xml
	//VALORES QUE NO DEPENDEN DEL RIESGO CUBIERTO NI EL CONCEPTO PRINCIPAL DEL MODULO
	public static final String TAG_CONDICIONADO_PLAN = "CondicionadoPlan";
	public static final String TAG_TIPO_CAPITAL = "TipCptal";
	public static final String TAG_TIPO_PLANTACION = "TipPlant";
	public static final String TAG_SISTEMA_CULTIVO = "SisCult";
	public static final String TAG_SISTEMA_PRODUCCION = "SisProd";
	public static final String TAG_SISTEMA_PROTECCION = "SisProt";
	public static final String TAG_SISTEMA_CONDUCCION = "SisCond";
	public static final String TAG_GASTOS_SALVAMENTO = "IndGastSalv";
	public static final String TAG_PROTECCION_CULTIVO = "IndProtCult";
	public static final String TAG_ROTACION = "Rot"; //ALTERNATIVA
	public static final String TAG_PRACTICA_CULTURAL = "PraCult";
	public static final String TAG_CARACTERISTICA_EXPLOTACION = "CarExpl";
	public static final String TAG_HISTORIAL_ASEGURADO = "HistAseg";
	public static final String TAG_DESTINO = "Dest";
	public static final String TAG_DENOMINACION_ORIGEN = "IndDO";
	public static final String TAG_NIVEL_RIESGO = "NivRies";
	public static final String TAG_CICLO_CULTIVO = "CiCul";
	public static final String TAG_TIPO_INSTALACION = "TipInst";
	public static final String TAG_CERTIFICADO_INSTALACION = "CodCert";
	public static final String TAG_MATERIAL_CUBIERTA = "MatCubi";
	public static final String TAG_EDAD_ESTRUCTURA_DESDE = "EdadDEstr";
	public static final String TAG_EDAD_ESTRUCTURA_HASTA = "EdadHEstr";
	public static final String TAG_FECHA_RECOLECCION_DESDE = "FecRecolD";
	public static final String TAG_FECHA_RECOLECCION_HASTA = "FecRecolH";
	public static final String TAG_TIPO_TERRENO = "TipTer";
	public static final String TAG_TIPO_MASA = "TipMas";
	public static final String TAG_NUM_ANIOS_DESDE_DESCORCHE = "Nadd";
	// GANADO
	public static final String TAG_CALIFICACION_SANEAMIENTO = "CalSanea";
	public static final String TAG_CALIFICACION_SANEAM_DEDUCIBLE = "CalSaneaD";
	public static final String TAG_CALIFICACION_SANITARIA = "CalSanit";
	public static final String TAG_CALIFICACION_SANIT_DEDUCIBLE = "CalSanitD";
	public static final String TAG_SISTEMA_ALMACENAMIENTO = "SisAlm";
	public static final String TAG_AUTORIZACION_ESPECIAL = "AutEsp";
	public static final String TAG_EXCEPCION_CONTRAT_EXPLOTACION = "ExcepConExc";
	public static final String TAG_EXCEPCION_CONTRAT_POLIZA = "ExcepConPol";
	public static final String TAG_DURACION_PERIODO_PRODUCTIVO = "DurPerPro";
	public static final String TAG_TIPO_ASEGURADO = "TAseGan";
	public static final String TAG_TIPO_GANADERIA = "TGanad";
	
	//VALORES QUE SI DEPENDEN DEL RIESGO CUBIERTO NI EL CONCEPTO PRINCIPAL DEL MODULO
	public static final String TAG_TIPO_FRANQUICIA = "TipFranq";
	public static final String TAG_FRANQUICIA = "Franq";
	public static final String TAG_MINIMO_INDEMNIZABLE = "MinIndem";
	public static final String TAG_DANIO_CUBIERTO = "DnCbtos";
	public static final String TAG_CALCULO_INDEMNIZACION = "CalcIndem";
	public static final String TAG_GARANTIZADO = "Garant";
	public static final String TAG_CAPITAL_ASEGURADO = "CapAseg";
	public static final String TAG_DURACION_MAX_GARANTIA_DIAS = "DurMaxGarantDias";
	public static final String TAG_DIAS_INICIO_GARANTIAS = "DIGarant";
	public static final String TAG_ESTADO_FENOLOGICO_FIN_GARANTIAS = "EFFGarant";
	public static final String TAG_ESTADO_FENOLOGICO_INICIO_GARANTIAS = "EFIGarant";
	public static final String TAG_FECHA_FIN_GARANTIAS = "FecFGarant";
	public static final String TAG_FECHA_INICIO_GARANTIAS = "FecIGarant";
	public static final String TAG_DURACION_MAX_GARANTIAS_MESES = "DurMaxGarantMeses";
	public static final String TAG_MES_INICIO_GARANTIAS = "MIGarant";
	public static final String TAG_RIESGO_CUBIERTO_ELEGIDO = "RiesgCbtoEleg";
	
	public static final String TAG_DATOS_INCOMPATIBILIDAD = "DatosIncompatibilidad";
	public static final String TAG_DATOS_TRADICIONALES = "DatosTradicionales";
	public static final String TAG_DATOS_ELEGIBLES = "DatsEleg";
	public static final String TAG_DATOS_VINCULADOS = "DatsVinc";
	// GANADO
	public static final String TAG_LIMITE_INDEMNIZACION = "LimIndem";
	public static final String TAG_PERIODO_GARANTIAS = "PerGarant";
	public static final String TAG_ADAPTACION_RIESGO_EXPLOT = "AdapRiesExp";
	public static final String TAG_DATOS_SEGURO_TRADICIONAL = "DatosSeguroTradicional";
	
	// Caracter separador de campos en fichero .csv
	public final String SEPARADOR = ";";
	// Caracter por el que se sustituye el separador si lo contiene algún campo
	public final String SUSTITUTO_SEPARADOR = ".";
	
	
	/**
	 * Metodo para procesar los ficheros del condicionado enviados por Agroseguro.
	 * @param ficheroOrigen Fichero a tratar.
	 * @param ficheroDestino Fichero de inserts a generar.
	 * @param lineaseguroid Identificador de plan/linea.
	 * @param dateFormat formato para las fechas
	 * @throws SAXException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws FactoryConfigurationError 
	 * @throws XMLStreamException 
	 * @throws ParseException Error al parsear la fecha
	 */
	public void procesarFichero(String ficheroOrigen, String ficheroDestino, Long lineaseguroid, String dateFormat) throws SAXException, FileNotFoundException, IOException, XMLStreamException, FactoryConfigurationError, ParseException{
		//Formato para logs
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		int eventCode;
		String tag;
		
		System.out.println("INICIO DEL PARSEO " + ficheroOrigen + ": " + sdf1.format(new Date()));
		
		int id = 1;
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(ficheroDestino))) {
			
			XMLInputFactory factory = XMLInputFactory.newInstance();
				
			try {
				factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
				factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
				factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
				factory.setProperty(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
				factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
			}
			
			XMLStreamReader parser = factory.createXMLStreamReader(new FileInputStream(ficheroOrigen));
			
			Object actual = null;
			while (parser.hasNext()) {
				eventCode = parser.next();
				if (eventCode == XMLStreamConstants.START_ELEMENT) {
					tag = parser.getLocalName();
					actual = generaRegistro(actual, tag, parser, id, lineaseguroid);
				} else if (eventCode == XMLStreamConstants.END_ELEMENT) {
					tag = parser.getLocalName();
					// anhado el elemento anterior al fichero e incrementamos el identificador
					if (tagPrincipal.equals(tag) && actual != null) {
						String sql = generaInsert(actual, dateFormat);
						if (!StringUtils.nullToString(sql).equals("")) {
							bw.write(sql + "\n");
						}
						// reseteo el objeto
						actual = null;
						id++;
					}
				}
			}			
		}
		System.out.println("Numero de elementos generados: " + (id-1));
		System.out.println("FIN DEL PARSEO " + ficheroOrigen + ": " + sdf1.format(new Date()));
	}
	
	/**
	 * Devuelve la cadena recibida como parámetro despues de haber sustituido el caracter separador por el caracter sustituto
	 * @param valor
	 * @return
	 */
	protected String reemplazarSeparador (String valor) {
		try {
			return valor.replace(SEPARADOR, SUSTITUTO_SEPARADOR);
		} catch (Exception e) {
			return valor;
		}
	}
	
	/**
	 * Metodo para rellenar el objeto que estamos tratando
	 * @param parser "Registro" que estamos leyendo del fichero xml origen 
	 * @param id Siguiente identificador interno a utilizar
	 * @param lineaseguroid Identificador de plan/linea
	 * @param dateFormat Formato para los campos fecha
	 * @return Objeto del condicionado a insertar en base de datos
	 */
	protected abstract Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid);
	
	/**
	 * Metodo para generar la sentencia a volcar en el fichero.
	 * @param reg Registro a generar
	 * @param dateFormat Formato para los campos fecha
	 * @return Cadena de texto con los valores separados por ";" para que funcione con las tablas externas
	 */
	protected abstract String generaInsert(Object reg, String dateFormat);

	public String getTagPrincipal() {
		return tagPrincipal;
	}

	public void setTagPrincipal(String tagPrincipal) {
		this.tagPrincipal = tagPrincipal;
	}

}
