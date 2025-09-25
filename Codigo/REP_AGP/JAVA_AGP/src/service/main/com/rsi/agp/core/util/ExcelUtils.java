package com.rsi.agp.core.util;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;


/**
 * 
 * Clase con utilidades basicas para trabajar con hojas en excel.
 * 
 **/

public class ExcelUtils {
	
	public static HSSFCellStyle getEstiloCabecera(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		HSSFFont font = libro.createFont();
		
		style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor((short)8);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor((short)8);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor((short)8);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor((short)8);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
		
		return style;
	}
	
	public static HSSFCellStyle getEstiloCabGris(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		HSSFFont font = libro.createFont();
		
		style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
		
		return style;
	}
	
	public static HSSFCellStyle getEstiloCabVerdeClaro(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		HSSFFont font = libro.createFont();
		
		style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor((short)8);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor((short)8);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor((short)8);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor((short)8);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
		
		return style;
	}
	
	public static HSSFCellStyle getEstiloCabAzul(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		HSSFFont font = libro.createFont();
		
		style.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor((short)8);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor((short)8);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor((short)8);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor((short)8);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
		
		return style;
	}
	
	public static HSSFCellStyle getEstiloCabAmarillo(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		HSSFFont font = libro.createFont();
		
		style.setFillForegroundColor(HSSFColor.YELLOW.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor((short)8);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor((short)8);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor((short)8);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor((short)8);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);	
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
		
		return style;
	}
	
	public static HSSFCellStyle getEstiloCabAmarilloClaro(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		HSSFFont font = libro.createFont();
		
		style.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);	
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
		
		return style;
	}
	
	public static HSSFCellStyle getEstiloCabAmarilloRojo(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		HSSFFont font = libro.createFont();
		
		style.setFillForegroundColor(HSSFColor.YELLOW.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor((short)8);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor((short)8);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor((short)8);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor((short)8);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.RED.index);
		style.setFont(font);
		
		return style;
	}
	
	public static HSSFCellStyle getEstiloCabVerde(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		HSSFFont font = libro.createFont();
		
		style.setFillForegroundColor(HSSFColor.SEA_GREEN.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor((short)8);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor((short)8);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor((short)8);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor((short)8);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
		
		return style;
	}
	
	public static HSSFCellStyle getEstiloCabVerdeMorado(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		HSSFFont font = libro.createFont();
		
		style.setFillForegroundColor(HSSFColor.SEA_GREEN.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor((short)8);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor((short)8);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor((short)8);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor((short)8);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.ORCHID.index);
		style.setFont(font);
		
		return style;
	}
	
	public static HSSFCellStyle getEstiloCabAzulClaro(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		HSSFFont font = libro.createFont();
		
		style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor((short)8);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor((short)8);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor((short)8);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor((short)8);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
		
		return style;
	}
	
	public static HSSFCellStyle getEstiloCabRojo(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		HSSFFont font = libro.createFont();
		
		style.setFillForegroundColor(HSSFColor.RED.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor((short)8);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor((short)8);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor((short)8);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor((short)8);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
		
		return style;
	}
	
	public static HSSFCellStyle getEstiloFilaTotales(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		HSSFFont font = libro.createFont();
		
		style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor((short)8);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor((short)8);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor((short)8);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor((short)8);
		style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
		
		return style;
	}
	
	public static HSSFCellStyle getEstiloFilaTotalesNumero(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		HSSFFont font = libro.createFont();
		HSSFDataFormat df = libro.createDataFormat();
		
		style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor((short)8);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor((short)8);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor((short)8);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor((short)8);
		style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
		
		style.setDataFormat(df.getFormat("#,##0.00"));
		
		return style;
	}
	
	public static HSSFCellStyle getEstiloFila(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBottomBorderColor((short)8);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setLeftBorderColor((short)8);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setRightBorderColor((short)8);
				
		return style;
	}
	
	public static HSSFCellStyle getEstiloFilaNumero(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		HSSFDataFormat df = libro.createDataFormat();
		
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBottomBorderColor((short)8);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setLeftBorderColor((short)8);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setRightBorderColor((short)8);
		
		style.setDataFormat(df.getFormat("#,##0.00"));
				
		return style;
	}
	
	public static HSSFCellStyle getEstiloFilaNumeroSinSeparadorDeMiles(HSSFWorkbook libro) {
		
		HSSFCellStyle style = getEstiloFilaNumero(libro);
		HSSFDataFormat df = libro.createDataFormat();

		style.setDataFormat(df.getFormat("###0.00"));
		
		return style;
	}
	
	public static HSSFCellStyle getEstiloFilaNumeroEntero(HSSFWorkbook libro) {
		
		HSSFCellStyle style = getEstiloFilaNumero(libro);
		HSSFDataFormat df = libro.createDataFormat();

		style.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		
		return style;
	}
	
	public static HSSFCellStyle getEstiloFilaPorcentaje(HSSFWorkbook libro) {
		
		HSSFCellStyle style = getEstiloFilaNumero(libro);
		HSSFDataFormat df = libro.createDataFormat();

		style.setDataFormat(df.getFormat("0,00"));
		
		return style;
	}
	
	public static HSSFCellStyle getEstiloFilaFecha(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		HSSFDataFormat df = libro.createDataFormat();
		
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBottomBorderColor((short)8);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setLeftBorderColor((short)8);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setRightBorderColor((short)8);
		
		style.setDataFormat(df.getFormat("dd-mmm-yy"));
				
		return style;
	}
	
	public static HSSFCellStyle getEstiloFilaNumeroColor(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		HSSFDataFormat df = libro.createDataFormat();
		
		style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBottomBorderColor((short)8);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setLeftBorderColor((short)8);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setRightBorderColor((short)8);
		
		style.setDataFormat(df.getFormat("#,##0.00"));
				
		return style;
	}
	
	public static HSSFCellStyle getEstiloTitulo(HSSFWorkbook libro) {
		HSSFCellStyle estiloTitulo = libro.createCellStyle();
		HSSFFont font = libro.createFont();
		
		font.setFontHeightInPoints((short)16);
		estiloTitulo.setFont(font);
		estiloTitulo.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		estiloTitulo.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);
		
		return estiloTitulo;
	}
		
	public static HSSFCellStyle getEstiloTextoRojo(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		HSSFFont font = libro.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.RED.index);
		style.setFont(font);	
		return style;
	}
	
	public static HSSFCellStyle getEstiloTextoGris(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		HSSFFont font = libro.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFont(font);	
		return style;
	}
	
	public static HSSFCellStyle getEstiloTextoMorado(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		HSSFFont font = libro.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.VIOLET.index);
		style.setFont(font);	
		return style;
	}
	
	public static HSSFCellStyle getEstiloTextoVerde(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		HSSFFont font = libro.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.SEA_GREEN.index);
		style.setFont(font);	
		return style;
	}
	
	public static HSSFCellStyle getEstiloTextoAzulClaro(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		HSSFFont font = libro.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.SKY_BLUE.index);
		style.setFont(font);	
		return style;
	}
	
	public static HSSFCellStyle getEstiloTextoNegro(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		HSSFFont font = libro.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.BLACK.index);
		style.setFont(font);	
		return style;
	}
	
	public static HSSFCellStyle getEstiloRosa(HSSFWorkbook libro) {
		HSSFCellStyle style = libro.createCellStyle();
		HSSFFont font = libro.createFont();
		style.setFillForegroundColor(HSSFColor.TAN.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.BLACK.index);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFont(font);	
		return style;
	}
	
	public static void autoajustarColumnas(HSSFSheet sheet, int numcolumnas) {
		for (short s=0; s<numcolumnas; s++){
			sheet.autoSizeColumn(s, true);
		}		
	}
}