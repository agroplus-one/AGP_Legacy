package com.rsi.agp.core.managers.impl;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.models.commons.ICheckStatusDao;

public class CheckStatusManager implements IManager {

	private ICheckStatusDao checkStatusDao;

	public boolean checkStatusBBDD() {
		boolean dbIsUp = true;
		try {
			checkStatusDao.checkStatusBBDD();
		} catch (DAOException e) {
			dbIsUp = false;
		}
		return dbIsUp;
	}

	public void setCheckStatusDao(ICheckStatusDao checkStatusDao) {
		this.checkStatusDao = checkStatusDao;
	}


}
