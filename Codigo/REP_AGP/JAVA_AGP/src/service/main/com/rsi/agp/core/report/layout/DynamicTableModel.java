package com.rsi.agp.core.report.layout;

import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.util.ConstantsInf;

public class DynamicTableModel implements TableModel {
	
	private String[] columnNames;
	private Object[][] data;
	
	private Log logger = LogFactory.getLog(getClass());
	/**
	* Instantiates a new dynamic table model.
	*
	* @param dataset the dataset
	*/
	public DynamicTableModel(Object[] dataset,List<String> cabeceras,List<String> tipo ) {
		super();
		initModel(dataset,cabeceras,tipo);
	}
	/**
	* Inits the model.
	*
	* @param dataset the dataset
	*/
	private void initModel(Object[] dataset,List<String> cabeceras,List<String> tipo) {
		columnNames = new String[cabeceras.size()];
		if (dataset.length > 0) {
			data = new Object[dataset.length][cabeceras.size()];
		}
		for (int i = 0; i < cabeceras.size(); i++) {
			columnNames[i] = cabeceras.get(i);
		}
		for (int j = 0; j < dataset.length; j++) {
			Object[] registro = new Object[dataset.length];
			if (cabeceras.size() >1){
				registro = (Object[]) dataset[j];
			}else{ // informe con solo 1 columna
				registro[0] = dataset[j];
			}
			try {
				for (int k = 0; k < columnNames.length; k++) {
					if (registro[k] != null){
						if (tipo.get(k).equals(Integer.toString(ConstantsInf.CAMPO_TIPO_NUMERICO))){
							data[j][k] = Double.parseDouble(((Object)registro[k]).toString());
						}else{
							data[j][k] = ((Object)registro[k]).toString();
						}
					}else{
						if (tipo.get(k).equals(Integer.toString(ConstantsInf.CAMPO_TIPO_NUMERICO))){
							data[j][k] = null;
						}else{
							data[j][k] = " ";
						}
					}
				}
			} catch (Exception e) {
				logger.error("DynamicTableModel - initModel:Error al iniciar el Modelo del Informe: ", e);
			}
		}
	}

	public int getColumnCount() {
	return columnNames.length;
	}
	
	public int getRowCount() {
	return data.length;
	}
	
	public String getColumnName(int col) {
	return columnNames[col];
	}
	
	public Object getValueAt(int row, int col) {
	return data[row][col];
	}
	
	/**
	* Gets the column names.
	*
	* @return the column names
	*/
	public String[] getColumnNames() {
	return columnNames;
	}
	
	/**
	* Sets the column names.
	*
	* @param columnNames the new column names
	*/
	public void setColumnNames(String[] columnNames) {
	this.columnNames = columnNames;
	}
	
	/**
	* Gets the data.
	*
	* @return the data
	*/
	
	public Object[][] getData() {
	return data;
	}
	
	/**
	* Sets the data.
	*
	* @param data the new data
	*/
	public void setData(Object[][] data) {
	this.data = data;
	}
	
	@Override
	public void addTableModelListener(TableModelListener l) {
		//EMPTY METHOD
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return null;
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	@Override
	public void removeTableModelListener(TableModelListener l) {
		//EMPTY METHOD
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		//EMPTY METHOD
	}
}
