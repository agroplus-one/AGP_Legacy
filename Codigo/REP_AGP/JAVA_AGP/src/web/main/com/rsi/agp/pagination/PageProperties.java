package com.rsi.agp.pagination;

public class PageProperties {
	
	private int fullListSize;
	private int pageSize;
	private int pageNumber;
	private int indexRowMin;
	private int indexRowMax;
	private String sort;
	private String dir;
	
	
	public int getFullListSize() {
		return fullListSize;
	}
	
	public void setFullListSize(int fullListSize) {
		this.fullListSize = fullListSize;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public int getPageNumber() {
		return pageNumber;
	}
	
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	
	public int getIndexRowMin() {
		return indexRowMin;
	}
	
	public void setIndexRowMin(int indexRowMin) {
		this.indexRowMin = indexRowMin;
	}
	
	public int getIndexRowMax() {
		return indexRowMax;
	}
	
	public void setIndexRowMax(int indexRowMax) {
		this.indexRowMax = indexRowMax;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}
	
	
}
