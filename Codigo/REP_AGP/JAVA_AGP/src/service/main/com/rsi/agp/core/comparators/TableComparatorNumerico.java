package com.rsi.agp.core.comparators;

import java.math.BigDecimal;
import java.util.Comparator;

import com.rsi.agp.core.webapp.util.StringUtils;

/**DAA 07/12/2012 Comparator numerico para ordenacion de tablas del listado parcelas
 * 
 * @author U028911
 *
 */

@SuppressWarnings("rawtypes")
public class TableComparatorNumerico implements Comparator{

	@Override
	public int compare(Object o1, Object o2) {

		if (o1.toString().contains("<div")){
			o1 = o1.toString().substring((((String) o1).lastIndexOf("'>")+2), ((String) o1).indexOf("</div>"));
		}
		if (o2.toString().contains("<div")){
			o2 = o2.toString().substring((((String) o2).lastIndexOf("'>")+2), ((String) o2).indexOf("</div>"));
		}
		if (o1.toString().contains("<br/>")){
			o1 = o1.toString().substring(0, ((String) o1).indexOf("<"));
		}
		if (((String) o2).contains("<br/>")){
			o2 = o2.toString().substring(0, ((String) o2).indexOf("<"));
		}
		
		BigDecimal clase = new BigDecimal(-1);
		if (!StringUtils.nullToString(o1).trim().equals(""))
			clase = new BigDecimal(o1.toString().trim());
		
		BigDecimal clase2 = new BigDecimal(-1);
		if (!StringUtils.nullToString(o2).trim().equals(""))
			clase2 = new BigDecimal(o2.toString().trim());
		
		return clase.compareTo(clase2);
	}

}
