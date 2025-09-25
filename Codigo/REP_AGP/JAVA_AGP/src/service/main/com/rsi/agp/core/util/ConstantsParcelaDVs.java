package com.rsi.agp.core.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConstantsParcelaDVs {

	public static final String FIELD_CLASS = "fieldClass";
	public static final String FIELDS_TO_SHOW = "fieldsToShow";
	public static final String FIELD_TO_ORDER = "fieldToOrder";
	public static final String FIELDS_TO_FILTER = "fieldsToFilter";
	public static final String FILTER_TYPES = "filterTypes";
	public static final String FIELDS_TO_FILTER_IN = "fieldsToFilterIn";
	public static final String FILTER_TYPES_IN = "filterTypesIn";
	public static final String FIELDS_TO_DISTINCT = "fieldsToDistinct";
	
	// MEDIDA PREVENTIVA
	public static final Map<String, Object> MAP_MED_PREV = Collections
	.unmodifiableMap(new HashMap<String, Object>() {
		private static final long serialVersionUID = 1758629596551667500L;
		{
			put(FIELD_CLASS, "com.rsi.agp.dao.tables.orgDat.VistaMedidaPreventiva");
			put(FIELDS_TO_SHOW, new String[] { "id.codmedidapreventiva", "id.desmedidapreventiva" });
			put(FIELD_TO_ORDER, "id.desmedidapreventiva");
			put(FIELDS_TO_FILTER, new String[] { "id.lineaseguroid" });
			put(FILTER_TYPES, new String[] { "java.lang.Long" });
			put(FIELDS_TO_FILTER_IN, new String[] { "id.codmodulo" });
			put(FILTER_TYPES_IN, new String[] { "java.lang.String" });
			put(FIELDS_TO_DISTINCT, new String[] { "id.codmedidapreventiva", "id.desmedidapreventiva" });			
		}
	});

	public static final Map<Integer, Map<String, Object>> MAP_DV_TIPO_5_7 = Collections
			.unmodifiableMap(new HashMap<Integer, Map<String, Object>>() {
				private static final long serialVersionUID = 1758629596551667500L;
				{
					put(ConstantsConceptos.CODCPTO_MEDIDA_PREVENTIVA, MAP_MED_PREV);
				}
			});
	
	public static String[] getfilterValues(final Object obj, final String[] properties) {
		 String[] values = new String[properties.length];
		 
		 return values;
	}
}
