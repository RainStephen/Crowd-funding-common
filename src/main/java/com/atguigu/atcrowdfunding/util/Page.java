package com.atguigu.atcrowdfunding.util;

import java.util.List;

public class Page<T> {
	
	private int pageNo;
	private int pageSize;
	private int totalRecord;
	private int totalPages;
	private List<T> list;
	public Page() {
		super();
	}
	
	
	
	public Page(int pageNo, int pageSize) {
		
		if(pageNo < 1)
			pageNo = 1;
		else
			this.pageNo = pageNo;
		if(pageSize < 1)
			pageSize = 5;
		else
			this.pageSize = pageSize;
	}



	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getTotalRecord() {
		return totalRecord;
	}
	//设置总记录数的同时设置总页面数
	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
		
		this.totalPages = (totalRecord % pageSize)==0?(totalRecord / pageSize):(totalRecord / pageSize +1);
	}
	public int getTotalPages() {
		return totalPages;
	}
	
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
	}
	
	//返回记录在数据库中的索引
	public int getStartIndex(){
		return (pageNo -1 ) * pageSize;
	}

	
	
	
	
}
