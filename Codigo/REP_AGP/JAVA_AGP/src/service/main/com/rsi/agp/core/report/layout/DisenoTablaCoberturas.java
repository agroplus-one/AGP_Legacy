package com.rsi.agp.core.report.layout;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;


public class DisenoTablaCoberturas {
	
	private static final Log logger = LogFactory.getLog(DisenoTablaCoberturas.class); 

	//Tamanho de las columnas que va a haber en la tabla de coberturas del pdf.
	private static final int TOTAL_WIDTH = 360;
	private static final int DETAIL_SIZE = 6;
	private static final int HEAD_SIZE = 5;

	/**
	 * Metodo que se encarga de montar los estilos para la tabla de "Coberturas y Garantias".
	 * @return La plantilla con los estilos y fuentes para la tabla de "Coberturas y Garantias"
	 */
	private static final DynamicReportBuilder getDisenoTablaCoberturas(){		
		
		DynamicReportBuilder drb = new DynamicReportBuilder();     
		
		Font headerFont = new Font();
		headerFont.setBold(true);
		headerFont.setFontName("SansSerif");
		headerFont.setFontSize(HEAD_SIZE);
		
		Font detailFont = new Font();
		detailFont.setFontName("SansSerif");
		detailFont.setFontSize(DETAIL_SIZE);
		
		Style headerStyle = new Style();
		headerStyle.setFont(headerFont);
		headerStyle.setBorder(Border.THIN());
		headerStyle.setBackgroundColor(Color.WHITE);
		headerStyle.setTransparency(Transparency.TRANSPARENT);
		headerStyle.setTextColor(Color.BLACK);
		headerStyle.setHorizontalAlign(HorizontalAlign.CENTER);
		headerStyle.setVerticalAlign(VerticalAlign.MIDDLE);
		
		Style detailStyle = new Style();
		detailStyle.setFont(detailFont);
		detailStyle.setHorizontalAlign(HorizontalAlign.CENTER);	
		detailStyle.setBorder(Border.THIN());
		detailStyle.setVerticalAlign(VerticalAlign.MIDDLE);
		detailStyle.setTransparency(Transparency.TRANSPARENT);
		
		drb.setDetailHeight(13)  	//defines the height for each record of the report
           .setMargins(0, 10, 0, 0)  //define the margin space for each side (top, bottom, left and right)
           .setDefaultStyles(null, null, headerStyle, detailStyle)  //Titulo y subtitulo no poseen disenho porque esta tabla no posee esos datos.
           .setColumnsPerPage(1);  	//defines columns per page (like in the telephone guide)
		
		return drb;
	}
	
	/**
	 * Calcula y disenha las distintas columnas que forman parte de la tabla de "Resumen de Coberturas y Garantias"
	 * @param cabeceraCoberturasGarantias Bean que indica que columnas van a ser pintadas.
	 * @return Layout para la tabla de "Resumen de Coberturas y Garantias" (faltaria introducir sus datos)
	 * @throws ColumnBuilderException
	 * @throws JRException
	 */
	public void getLayoutTablaCoberturas(String pathInforme, List<String> cabeceras) throws ColumnBuilderException, JRException{
	
		//Las columnas de "Garantia" y "Riesgos Cubiertos" siempre aparecen en el informe.
		AbstractColumn columnaGarantia = ColumnBuilder.getNew()             //creates a new instance of a ColumnBuilder
                .setColumnProperty("garantia", String.class.getName()) 		//defines the field of the data source that this column will show, also its type
                .setTitle("GARANTIA")                                       //the title for the column
                .setWidth(60)                                           //the width of the column
                .build();
                
		AbstractColumn columnaRiesgosCubiertos = ColumnBuilder.getNew()
                .setColumnProperty("riesgosCubiertos", String.class.getName())
                .setTitle("RIESGOS CUBIERTOS").setWidth(90)
                .build();
		
		//A partir del bean que se pasa al metodo, calcularemos que columnas van a ser mostradas.
        DynamicReportBuilder drb = getDisenoTablaCoberturas();  //Se recoge el disenho general para la tabla. 
        drb.addColumn(columnaGarantia);
        drb.addColumn(columnaRiesgosCubiertos);
        
        int width = cabeceras.size() == 0 ? TOTAL_WIDTH : (TOTAL_WIDTH / cabeceras.size());
        
        int i = 0;
		for (String cabecera : cabeceras){
			AbstractColumn columnaInforme = ColumnBuilder.getNew()
            .setColumnProperty("celdas["+i+"]", String.class)
            .setTitle(cabecera).setWidth(width)
            .build();
			drb.addColumn(columnaInforme);
			i++;
		}
		
        drb.setUseFullPageWidth(false); 
        drb.setWhenNoDataAllSectionNoDetail();
        drb.setAllowDetailSplit(false);
        
        DynamicReport dr = drb.build();	//Construimos la plantilla
        OutputStream output = null;
		try {
			output = new FileOutputStream(pathInforme);
			DynamicJasperHelper.generateJRXML(dr, new ClassicLayoutManager(), null, "UTF-8", output);
	        output.close();
		} catch (FileNotFoundException e) {			
			logger.error("Error al generar el jrxml", e);
		} catch (IOException e) {
			logger.error("Error al generar el jrxml", e);
		}
	}	
	
	public void getLayoutTablaCoberturasComplementaria(String pathInforme) throws ColumnBuilderException, JRException{
		
		//Las columnas de "Garantia" y "Riesgos Cubiertos" siempre aparecen en el informe.
		AbstractColumn columnaGarantia = ColumnBuilder.getNew()             //creates a new instance of a ColumnBuilder
                .setColumnProperty("garantia", String.class.getName()) 		//defines the field of the data source that this column will show, also its type
                .setTitle("GARANTIA")                                       //the title for the column
                .setWidth(258) //70                                         //the width of the column                 
                .build(),
                
                columnaRiesgosCubiertos = ColumnBuilder.getNew()
                .setColumnProperty("riesgosCubiertos", String.class.getName())
                .setTitle("RIESGOS CUBIERTOS").setWidth(298)   //110             
                .build();  	       
		
		//A partir del bean que se pasa al metodo, calcularemos que columnas van a ser mostradas.
        DynamicReportBuilder drb = getDisenoTablaCoberturas();  //Se recoge el disenho general para la tabla. 
        drb.addColumn(columnaGarantia);
        drb.addColumn(columnaRiesgosCubiertos);       
        
        drb.setUseFullPageWidth(false); 
        drb.setWhenNoDataAllSectionNoDetail();
        drb.setAllowDetailSplit(false);
        
        DynamicReport dr = drb.build();	//Construimos la plantilla
        OutputStream output = null;
		try {
			output = new FileOutputStream(pathInforme);
			DynamicJasperHelper.generateJRXML(dr, new ClassicLayoutManager(), null, "UTF-8", output);
	        output.close();
		} catch (FileNotFoundException e) {			
			logger.error("Excepcion : DisenoTablaCoberturas - getLayoutTablaCoberturasComplementaria", e);
		} catch (IOException e) {
			logger.error("Excepcion : DisenoTablaCoberturas - getLayoutTablaCoberturasComplementaria", e);
		}
	}	
	
}
