package com.rsi.agp.core.comparators;

import java.util.Comparator;

public class TableComparatorComisionesEntSubMed implements Comparator<Object> {

	public int compare(Object o1, Object o2) {

		boolean iso1Empty = (o1 == null || o1.toString().isEmpty());
		boolean iso2Empty = (o2 == null || o2.toString().isEmpty());

		if (iso1Empty && iso2Empty) {
			return 0;
		}
		if (iso2Empty) {
			return 1;
		}
		if (iso1Empty) {
			return -1;
		}
		int result = 0;
		final String[] c1 = ((String) o1.toString()).split("-");
		final String[] c2 = ((String) o2.toString()).split("-");
		final Integer ent1 = Integer.valueOf(c1[0].trim());
		final Integer ent2 = Integer.valueOf(c2[0].trim());
		result = ent1.compareTo(ent2);
		if (result == 0) {
			final Integer subent1 = Integer.valueOf(c1[1].trim());
			final Integer subent2 = Integer.valueOf(c2[1].trim());
			result = subent1.compareTo(subent2);
		}
		return result;
	}
}
