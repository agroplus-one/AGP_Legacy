package com.rsi.agp.dao.filters;

import org.hibernate.Criteria;

/**
 * Creates a command to wrap the Hibernate criteria API.
 * 
 * @author T-Systems
 */
public interface CriteriaCommand {
	public Criteria execute(Criteria criteria);
}
