package com.rsi.agp.core.report.layout;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.dao.tables.mtoinf.Informe;

import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.CustomExpression;
import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DJValueFormatter;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;

public class DisenoTablaInformeGenerico {
	
	private Log logger = LogFactory.getLog(getClass());
	private final ResourceBundle bundleInf = ResourceBundle.getBundle("agp_inf");
	private int maxColumnas = -1;
	
	// Textos que se imprimiran en el informe junto a los resultados de las sumas y cuentas parciales y totales
	private final String TEXTO_CUENTA_PARCIAL = "Registros";
	private final String TEXTO_CUENTA_TOTAL = "Total registros";
	
	private int columnaCuenta = -1;
	
	private static int HEIGHT_FOOTER_VARIABLES = 50;
	private static final int EXCEL_CELDA_ANCHURA = 100;
	
  /**
 	* Crea el informe Dinamico
 	* @return DynamicReport
 	*/
	@SuppressWarnings("unchecked")
	public DynamicReport buildReport(DynamicTableModel model, Map<String, Object> map) throws Exception {

		FastReportBuilder drb = new FastReportBuilder();
		
		// PARAMETROS NECESARIOS PARA LA GENERACION DEL INFORME
		List<String> tipo = (List<String>)map.get("tipo");
		List<Boolean> totaliza = (List<Boolean>)map.get("totaliza");
		List<Boolean> totalPorGrupo = (List<Boolean>)map.get("totalPorGrupo");
		List<Boolean> ruptura = (List<Boolean>)map.get("ruptura");
		List<BigDecimal> formato = (List<BigDecimal>)map.get("formato");
		List<BigDecimal> decimales = (List<BigDecimal>)map.get("decimales");
		Informe informe = (Informe)map.get("informe");
		
		// Obtiene el numero maximo de columnas a mostrar en el informe
		this.maxColumnas = getMaximoColumnas(map);
	
		// TITULOS
		crearTitulos(drb, informe);
		
		// COLUMNAS DEL INFORME CON SU FORMATO
		crearColumnas(model, drb, tipo, formato, decimales, map);
		
		// TOTALIZA
		crearTotaliza(drb, totaliza);
		
		// CUENTA
		crearCuenta(drb, totaliza, informe);
		
		// RUPTURAS			
		crearRupturas(drb, totalPorGrupo, ruptura, informe);
		
		// OPCIONES COMUNES AL TODOS LOS INFORMES
		crearOpcionesComunes(drb, informe);
		
		// OPCIONES PARTICULARES SEGUN EL FORMATO DEL INFORME
		crearFormatoExportacion(map, drb);
	
		// Generacion del informe
		return drb.build();
		
	}
	
	
	
	@SuppressWarnings("unchecked")
	private void crearFormatoExportacion(Map<String, Object> map, FastReportBuilder drb) {
		int formatoExport = Integer.parseInt((String)map.get("formatoInforme"));
		
		switch (formatoExport) {
			case ConstantsInf.COD_FORMATO_PDF:
				// orientacion PDF
				BigDecimal orientacionInforme = new BigDecimal((String)map.get("orientacionInforme"));
				if (orientacionInforme.compareTo(new BigDecimal(ConstantsInf.COD_ORIENTACION_HORIZONTAL))== 0 ){ 
					drb.setPageSizeAndOrientation(Page.Page_A4_Landscape());
				}else{
					drb.setPageSizeAndOrientation(Page.Page_A4_Portrait());	
				}
				// pagina x/y
				drb.addAutoText(AutoText.AUTOTEXT_PAGE_X_SLASH_Y, AutoText.POSITION_FOOTER, AutoText.ALIGMENT_RIGHT);
				break;	
			case ConstantsInf.COD_FORMATO_XLS:
				Page page = Page.Page_A4_Landscape();
				page.setWidth((((List<String>)map.get("cabeceras")).size()*EXCEL_CELDA_ANCHURA));
				drb.setPageSizeAndOrientation(page);
				drb.setIgnorePagination(true);
				drb.setUseFullPageWidth(true);
				break;
			case ConstantsInf.COD_FORMATO_HTML:
				drb.setIgnorePagination(true);
				drb.setUseFullPageWidth(false);
				break;
			case ConstantsInf.COD_FORMATO_CSV:
				drb.setIgnorePagination(true);
				break;
			default:
				break;
		}
	}

	/**
	 * Anhade las opciones comunes al informe
	 * @param drb
	 * @param informe
	 */
	private void crearOpcionesComunes(FastReportBuilder drb, Informe informe) {
		// Avoid splitting of rows
		drb.setAllowDetailSplit(false);
		
		// SIN DATOS EN EL INFORME
		drb.setWhenNoData(" ", EstilosInformes.headerVariables());
		// para las comas en los miles y el punto en los decimales
		drb.setReportLocale(Locale.ITALY);
		
		// varios
		drb.setShowDetailBand(true);
		drb.setReportName(informe.getNombre());
		drb.setColumnSpace(50);
		drb.setAllowDetailSplit(false);
		drb.setGlobalFooterVariableHeight(HEIGHT_FOOTER_VARIABLES);
		
	}

	/**
	 * Anhade las rupturas al informe
	 * @param drb
	 * @param totalPorGrupo
	 * @param ruptura
	 * @param informe
	 */
	private void crearRupturas(FastReportBuilder drb, List<Boolean> totalPorGrupo, List<Boolean> ruptura, Informe informe) {
		
		DJGroup g1 = new DJGroup();
		DJGroup g2 = new DJGroup();

		for(int k=0;k<ruptura.size();k++){
			
			boolean tengoGrupo1 = false; // Indica si para la columna actual se anhade el grupo para la suma parcial
			boolean tengoGrupo2 = false; // Indica si para la columna actual se anhade el grupo para la cuenta parcial
			
			// Si se ha seleccionado ruptura por la columna k
			if (ruptura.get(k)) { 
				// Si para la misma columna se ha seleccionado total por grupo
				if (totalPorGrupo.get(k)) {
					GroupBuilder gb1 = new GroupBuilder();
					gb1.setCriteriaColumn((PropertyColumn) drb.getColumn(k));
					gb1.setGroupLayout(GroupLayout.VALUE_FOR_EACH);
					gb1.setFooterVariablesHeight(HEIGHT_FOOTER_VARIABLES);
					gb1.addFooterVariable(drb.getColumn(k), DJCalculation.SUM, EstilosInformes.valueGroup(), getSumaFormatter("parcial"));
					
					g1 = gb1.build();
					tengoGrupo1 = true;
					
					// Si se ha elegido que se cuente en el informe y no se ha anhadido en alguna columna anterior, se incluye en esta columna
					if (informe.getCuenta().compareTo(new BigDecimal(ConstantsInf.COD_CUENTA_SI))== 0 ){ 
						GroupBuilder gb2 = new GroupBuilder();
						gb2.setCriteriaColumn((PropertyColumn) drb.getColumn(k));
						gb2.setGroupLayout(GroupLayout.VALUE_FOR_EACH);
						gb2.setFooterVariablesHeight(HEIGHT_FOOTER_VARIABLES);
						gb2.addFooterVariable(drb.getColumn(k), this.getExpresionCuentaParcial(),EstilosInformes.valueGroup());
						
						g2 =gb2.build();
						tengoGrupo2 = true;
					}
					
					// Se anhaden los grupos al informe
					if (tengoGrupo1)
						drb.addGroup(g1);
					if (tengoGrupo2)
						drb.addGroup(g2);
				}
			}
		}
	}

	/**
	 * Anhade la opcion de la cuenta al informe
	 * @param drb
	 * @param totaliza
	 * @param informe
	 */
	private void crearCuenta(FastReportBuilder drb, List<Boolean> totaliza, Informe informe) {
		if (informe.getCuenta().compareTo(new BigDecimal(ConstantsInf.COD_CUENTA_SI))== 0 ){
			// Si hay alguna columna libre para la cuenta
			if (this.columnaCuenta != -1) {
				drb.addGlobalFooterVariable(drb.getColumn(this.columnaCuenta), this.getMiTotalExpresion(), EstilosInformes.valueGrandTotal());
			}
			// Si no la hay, se pinta en la cabecera de la primera columna
			else {
				drb.addGlobalHeaderVariable(drb.getColumn(0), this.getMiTotalExpresion(), EstilosInformes.valueGrandTotal());
			}
		}
	}

	/**
	 * Anhade la opcion del totaliza al informe
	 * @param drb
	 * @param totaliza
	 */
	private void crearTotaliza(FastReportBuilder drb, List<Boolean> totaliza) {
		
		int tope = (this.maxColumnas == -1 || this.maxColumnas > totaliza.size()) ? totaliza.size() : this.maxColumnas;
		
		// Calcula el tope de columnas a mostrar dependiendo de las columnas configuradas en el informe y el valor maximo obtenido del properties
		// en el caso de generacion en PDF
		for(int i=0; i<tope;i++){
			if (totaliza.get(i)) {				
				drb.addGlobalColumnVariable(DJConstants.FOOTER,drb.getColumn(i), DJCalculation.SUM, EstilosInformes.valueGrandTotal(), getSumaFormatter("total"));
			}
			// Si no hay que totalizar por esta columna y se establece como columna para la cuenta si no se ha hecho anteriormente
			else {
				if (this.columnaCuenta == -1) this.columnaCuenta = i;
			}
		}
	}
	
	/**
	 * Formatea la salida para mostrar las sumas parciales o totales
	 * @param tipo
	 * @return
	 */
	private DJValueFormatter getSumaFormatter (String tipo)  {
		
		final String tipoMsg = tipo;
		
		return new DJValueFormatter() {
			
	        @Override
	        public String getClassName() {
	            return String.class.getName();
	        }

			@SuppressWarnings("rawtypes")
			@Override
			public Object evaluate(Object value, Map fields, Map variables, Map parameters) {
				
				Double numero = (Double) value;
				
				return "Suma " + tipoMsg + " : " + ((numero - (numero.intValue()) == 0) ? (numero.intValue()+"") : (numero.toString()));
			}

		};
	}

	/**
	 * Anhade las columnas al informe
	 * @param model
	 * @param drb
	 * @param tipo
	 * @param formato
	 * @param decimales
	 */
	private void crearColumnas(DynamicTableModel model, FastReportBuilder drb, List<String> tipo, List<BigDecimal> formato, 
							   List<BigDecimal> decimales, Map<String, Object> map) {
		
		String[] headings=model.getColumnNames();
		String pattern = "";
		
		Integer tipoNumerico = ConstantsInf.CAMPO_TIPO_NUMERICO;
		
		// Calcula el tope de columnas a mostrar dependiendo de las columnas configuradas en el informe y el valor maximo obtenido del properties
		// en el caso de generacion en PDF
		int tope = (this.maxColumnas == -1 || this.maxColumnas > headings.length) ? headings.length : this.maxColumnas;
		
		for (int i=0; i<tope; i++) {
			String key=headings[i];
			AbstractColumn column = null;
			if (tipo.get(i).equals(tipoNumerico.toString())){ // tipo numérico
				switch (Integer.parseInt(formato.get(i).toString())) {
					case ConstantsInf.COD_FORMATO_NUM_NNNN:
						column = creaColumna(key,tipo.get(i),EstilosInformes.columDetailWhite(),bundleInf.getString("formato.noMiles"));
						break;
					case ConstantsInf.COD_FORMATO_NUM_N_NNN:
						column = creaColumna(key,tipo.get(i),EstilosInformes.columDetailWhite(),bundleInf.getString("formato.miles"));
						break;
					case ConstantsInf.COD_FORMATO_NUM_NNNN_DD:
						pattern = bundleInf.getString("formato.noMiles");
						for (int k=0;k<Integer.parseInt(decimales.get(i).toString());k++){
							if (k == 0)
								pattern += ".0";
							else	
								pattern += "0";
						}
						column = creaColumna(key,tipo.get(i),EstilosInformes.columDetailWhite(),pattern);
						break;
					case ConstantsInf.COD_FORMATO_NUM_N_NNN_DD:
						pattern = bundleInf.getString("formato.miles");
						for (int k=0;k<Integer.parseInt(decimales.get(i).toString());k++){
							if (k == 0)
								pattern += ".0";
							else		
								pattern += "0";
						}
						column = creaColumna(key,tipo.get(i),EstilosInformes.columDetailWhite(),pattern);
						break;
					case ConstantsInf.COD_FORMATO_NUM_N_NNN_DD_RIGHT:
						pattern = bundleInf.getString("formato.miles");
						for (int k=0;k<Integer.parseInt(decimales.get(i).toString());k++){
							if (k == 0)
								pattern += ".0";
							else		
								pattern += "0";
						}
						column = creaColumna(key,tipo.get(i),EstilosInformes.columDetailWhiteRight(),pattern);
						break;
					case ConstantsInf.COD_FORMATO_NUM_NNNN_DD_RIGHT:
						pattern = bundleInf.getString("formato.noMiles");
						for (int k=0;k<Integer.parseInt(decimales.get(i).toString());k++){
							if (k == 0)
								pattern += ".0";
							else	
								pattern += "0";
						}
						column = creaColumna(key,tipo.get(i),EstilosInformes.columDetailWhiteRight(),pattern);
						break;
					default:
						break;
				}
			}else{ // tipo String
				column = creaColumna(key,tipo.get(i),EstilosInformes.columDetailWhite(),pattern);
			}
			drb.addColumn(column);
		}
	}


	/**
	 * Anhade los titulos informe
	 * @param drb
	 * @param informe
	 */
	private void crearTitulos(FastReportBuilder drb, Informe informe) {
		String subtitulo = "";
		if (informe.getTitulo2() != null){
			subtitulo += informe.getTitulo2();
		}
		if (informe.getTitulo3() != null){
			if (informe.getTitulo2() != null)
				subtitulo += ". ";
			subtitulo += informe.getTitulo3();
		}
		
		drb.setSubtitle(subtitulo)
			.setTitleStyle(EstilosInformes.titleStyle()).setTitleHeight(new Integer(30));
		drb.setTitle(informe.getTitulo1())
			.setTitleStyle(EstilosInformes.titleStyle()).setTitleHeight(new Integer(30))
			.setSubtitleHeight(new Integer(20))
			.setDetailHeight(new Integer(15))
			.setPrintBackgroundOnOddRows(true)
			.setOddRowBackgroundStyle(EstilosInformes.oddRowStyle())
			.setColumnsPerPage(new Integer(1))
			.setUseFullPageWidth(true);
		
	}
	
	@SuppressWarnings("deprecation")
	private AbstractColumn creaColumna(String key, String tipo,Style columDetailWhite,String pattern){
		try {
			AbstractColumn column = null;
			Integer tipoNumerico = ConstantsInf.CAMPO_TIPO_NUMERICO;
			if (tipo.equals(tipoNumerico.toString())){ // tipo numérico
				column =ColumnBuilder.getInstance().setColumnProperty(key, Double.class.getName())
				.setTitle(key).setWidth(new Integer(100))
				.setPattern(pattern)
				.setStyle(columDetailWhite).build();
			}else{ // tipo String/fecha
				column =ColumnBuilder.getInstance().setColumnProperty(key, String.class.getName())
				.setTitle(key).setWidth(new Integer(100))
				.setStyle(columDetailWhite).build();
			}
			column.setBlankWhenNull(true);
			return column;
		} catch (ColumnBuilderException e) {
			logger.error("Excepcion : DisenoTablaInformeGenerico - creaColumna", e);
		}
		return null;
	}
	
	
	@SuppressWarnings("serial")
	private CustomExpression getExpresionCuentaParcial() {
		
		return new CustomExpression() {
	    	private Integer count =0;
	    	private Integer numAnterior =0;
	    	@SuppressWarnings({ "rawtypes" })
			public Object evaluate(Map fields, Map variables, Map parameters) {
	    		
	    		int s =0;
	    		
		    		if (count==0){
		    			count ++;
		    			numAnterior = (Integer)variables.get("global_column_0_COUNT");
		    			s = (Integer)variables.get("global_column_0_COUNT"); 
		    		}else{
		    			s = (Integer)variables.get("global_column_0_COUNT") - numAnterior;
		    			numAnterior = (Integer)variables.get("global_column_0_COUNT");
		    		}
		    		
	        	return TEXTO_CUENTA_PARCIAL + " : " + s;
	        }
	        @Override
	        public String getClassName() {
	            return String.class.getName();
	        }

		};
	}
	@SuppressWarnings("serial")
	private CustomExpression getMiTotalExpresion() {
		
		
	    return new CustomExpression() {
	    	
	    	@SuppressWarnings({ "rawtypes" })
			public Object evaluate(Map fields, Map variables, Map parameters) {
	        	return TEXTO_CUENTA_TOTAL + " : " + variables.get("REPORT_COUNT");
	        }
	        @Override
	        public String getClassName() {
	            return String.class.getName();
	        }


			
	    };
	}
	
	/**
	 * Obtiene el numero maximo del columnas permitidas para el informe dependiendo del formato de generacion elegido
	 * @param map
	 * @param formatoExport
	 * @return -1 si no hay maximo de columnas
	 */
	private int getMaximoColumnas(Map<String, Object> map) {
		
		int max = -1;
		
		// Si el formato de generacion del informe es PDF, se obtiene el numero de columnas maximo que se van a pintar
		if (ConstantsInf.COD_FORMATO_PDF == Integer.parseInt((String)map.get("formatoInforme"))) {
				// Orientacion PDF
				BigDecimal orientacionInforme = new BigDecimal((String)map.get("orientacionInforme"));
				
				if (orientacionInforme.compareTo(new BigDecimal(ConstantsInf.COD_ORIENTACION_HORIZONTAL))== 0 ){ 
					max = getMaximoColumnasPDF(TipoPDF.HORIZONTAL);
				}
				else{
					max = getMaximoColumnasPDF(TipoPDF.VERTICAL);	
				}					
		}
		return max;
	}
	
	/**
	 * Devuelve el numero maximo de columnas permitidas en la generacion del informe en formato PDF 
	 * @param tipoMax Indica la propiedad a buscar en el properties dependiendo si el informe es vertical u horizontal
	 * @return -1 si ocurre algun error durante la obtencion del numero maximo
	 */
	private int getMaximoColumnasPDF(TipoPDF tipoMax) {
		
		String aux = null;
		int max = -1;
		
		try {
			aux = bundleInf.getString(tipoMax.getValue()); 
		}
		catch (NullPointerException e1) {
			logger.error("MtoInformeService.getMaximoColumnasPDF - La clave a buscar en el properties es nula.", e1);
		}
		catch (MissingResourceException e2) {
			logger.error("MtoInformeService.getMaximoColumnasPDF - La clave '" + tipoMax.getValue() + "' no existe en el properties.", e2);
		}
		catch (ClassCastException e3) {
			logger.error("MtoInformeService.getMaximoColumnasPDF - La valor de la clave a buscar no es una cadena.", e3);
		}
		
		// Si aux es nulo ha ocurrido algun error al acceder al properties, por lo que se devuelve -1
		if (aux == null) return max;
		
		// Se convierte el valor obtenido del properties a entero
		try {
			max = Integer.parseInt(aux);
		}
		catch (NumberFormatException e4) {
			logger.error("MtoInformeService.getMaximoColumnasPDF - Error de conversion a entero del valor '" + aux + "'", e4);
		}
		catch (Exception e5) {
			logger.error("MtoInformeService.getMaximoColumnasPDF - Ocurrio un error inesperado al pasar a entero el valor '" + aux + "'", e5);
		}
		
		return max;
	}
	
	/**
	 * Enum que encapsula las dos orientaciones posibles para el formato PDF
	 */
	private enum TipoPDF {
		VERTICAL (ConstantsInf.MAX_COLUMNAS_PDF_VERTICAL), HORIZONTAL (ConstantsInf.MAX_COLUMNAS_PDF_HORIZONTAL);
		
		private String valor;
		
		TipoPDF (String valor) {
			this.valor = valor;
		}
		
		public String getValue () {
			return this.valor;
		}
	}
}
class EstilosInformes {
	
	
	private  static String tipoLetra =  "DejaVu Sans";
	
	
	/**
	 * estilo para el count total de registros y la suma total
	 * @return Style
	 */
	public static Style valueGrandTotal2(){
		
		Style style= new Style();
		style.setHorizontalAlign(HorizontalAlign.RIGHT);
		style.setTextColor(Color.BLACK);
		style.setFont(new Font(10,tipoLetra,false,false,false));			
		return style;
	}
public static Style valueGrandTotal(){
		
		Style style= new Style();
		style.setHorizontalAlign(HorizontalAlign.LEFT);
		style.setVerticalAlign(VerticalAlign.TOP);
		style.setTextColor(Color.BLACK);
		style.setBorderTop(Border.THIN());
		style.setFont(new Font(10,tipoLetra,false,false,false));
		return style;
	}



	/**
	 * estilo para el valor de la suma y del count por grupos
	 */
	public static Style valueGroup(){
		Style style = new Style();
		style.setFont(new Font(10,tipoLetra,false,false,false));
		style.setTextColor(Color.BLACK);
		style.setBorderBottom(Border.THIN());
		style.setHorizontalAlign(HorizontalAlign.LEFT);
		style.setVerticalAlign(VerticalAlign.TOP);
		return style;
	}
	/**
	 * estilo para la etiqueta del count total de registros y la suma total
	 * @return
	 */
	public static Style labelGrandTotal(){
		Style style= new Style();
		style.setHorizontalAlign(HorizontalAlign.LEFT);
		style.setTextColor(Color.BLACK);
		style.setFont(new Font(10,tipoLetra,true,false,true));
		
		return style;
		
	}
	/**
	 * estilo para la etiqueta de la suma y del count por grupos
	 * @return
	 */
	public static Style labelGroup(){
		Style style = new Style();
		style.setFont(new Font(10,tipoLetra,true,false,false));
		style.setTextColor(Color.BLACK);
		return style;
	}
	
	public static Style headerVariables(){
	
		Style headerVariables = new Style();
		headerVariables.setFont(new Font(11,tipoLetra,false,false,false));
		headerVariables.setBorderTop(Border.THIN());
		headerVariables.setHorizontalAlign(HorizontalAlign.LEFT);
		headerVariables.setVerticalAlign(VerticalAlign.BOTTOM);
		headerVariables.setStretchWithOverflow(true);
		headerVariables.setPaddingTop(5);
		Color green = new Color(0,139,0);
		headerVariables.setTextColor(green);
		return headerVariables;
	}
	public static Style columDetail(){
		Style columDetail = new Style();
		columDetail.setBorder(Border.THIN());
		return columDetail;
	}
	public static Style columDetailWhite(){
		Style columDetailWhite = new Style();
		columDetailWhite.setBorder(Border.THIN());
		columDetailWhite.setBackgroundColor(Color.WHITE);
		return columDetailWhite;
	}
	
	public static Style columDetailWhiteRight(){
		Style columDetailWhite = new Style();
		columDetailWhite.setBorder(Border.THIN());
		columDetailWhite.setBackgroundColor(Color.WHITE);
		columDetailWhite.setHorizontalAlign(HorizontalAlign.RIGHT);
		return columDetailWhite;
	}
	
	public static Style columDetailWhiteBold(){
		Style columDetailWhiteBold = new Style();
		columDetailWhiteBold.setBorder(Border.THIN());
		columDetailWhiteBold.setBackgroundColor(Color.WHITE);
		return columDetailWhiteBold;
	}
	public static Style titleStyle(){
		
		Style titleStyle = new Style();
		titleStyle.setFont(new Font (14, tipoLetra,true));
		return titleStyle;
	}
	public static Style numberStyle(){
		Style numberStyle = new Style();
		numberStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
		return numberStyle;
	}
	public static Style amountStyle(){
		Style amountStyle = new Style();
		amountStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
		amountStyle.setBackgroundColor(Color.cyan);
		amountStyle.setTransparency(Transparency.OPAQUE);
		return amountStyle;
	}
	public static Style oddRowStyle(){
		Style oddRowStyle = new Style();
		oddRowStyle.setBorder(Border.NO_BORDER());
		oddRowStyle.setBackgroundColor(new Color(230,230,230));
		oddRowStyle.setTransparency(Transparency.OPAQUE);
		return oddRowStyle;
	}
	
	
}