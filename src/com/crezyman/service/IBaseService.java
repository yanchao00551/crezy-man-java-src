package com.crezyman.service;

import java.util.List;

import com.crezyman.utils.Params;

public interface IBaseService<T> {
	
public void delete(String id) ;
	
	public T findById(String id);
	
	public int save(Object entity) ;
	
	public void update(Params params,Object entity) ;
	
	public List<Object> queryOrdersList(Params params) ;
	
	public Integer queryCount(Params param) ;

}
