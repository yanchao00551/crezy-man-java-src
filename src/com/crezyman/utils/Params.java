package com.crezyman.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Params implements Serializable {

	private static final long serialVersionUID = 1L;

	private String tableName;

	private Map<String, Object> selectParams;
	private Map<String, Object> likeParams;
	private Map<String, Object> updateParams;
	private Map<String, Object> sortParams;
	private Map<String, Object> addParams;
	private Map<String, List<Integer>> betweenParams;
	private Map<String, List<Integer>> inParams;

	private int startIndex;

	private int pageSize;

	private boolean isOpenPager = false;

	public Params() {
		selectParams = new HashMap<String, Object>();
		sortParams = new HashMap<String, Object>();
		updateParams = new HashMap<String, Object>();
		addParams = new HashMap<String, Object>();
		likeParams = new HashMap<String, Object>();
		betweenParams = new HashMap<String, List<Integer>>();
		inParams = new HashMap<String,List<Integer>>();

	}

	public Params(String tableName) {
		this.tableName = tableName;
		selectParams = new HashMap<String, Object>();
		sortParams = new HashMap<String, Object>();
		updateParams = new HashMap<String, Object>();
		addParams = new HashMap<String, Object>();
		likeParams = new HashMap<String, Object>();
		betweenParams = new HashMap<String, List<Integer>>();
		inParams = new HashMap<String,List<Integer>>();
	}

	public Params addBetween(String column, List<Integer> value) {
		betweenParams.put(column, value);
		return this;
	}

	public Params addEqual(String column, Object value) {
		selectParams.put(column, value);
		return this;
	}

	public Params addLike(String column, Object value) {
		likeParams.put(column, value);
		return this;
	}

	public Params addSet(String column, Object value) {
		updateParams.put(column, value);
		return this;
	}

	public Params orderDesc(String cloumn) {
		sortParams.put(cloumn, "desc");
		return this;
	}

	public Params orderAsc(String cloumn) {
		sortParams.put(cloumn, "asc");
		return this;
	}

	public Params openPager(int startIndex, int pageSize) {
		this.isOpenPager = true;
		this.startIndex = startIndex;
		this.pageSize = pageSize;
		return this;
	}

	public Params addSaveParam(String column, Object value) {
		addParams.put(column, value);
		return this;
	}

	public Map<String, Object> getLikeParams() {
		return likeParams;
	}

	public void setLikeParams(Map<String, Object> likeParams) {
		this.likeParams = likeParams;
	}

	public boolean isOpenPager() {
		return isOpenPager;
	}

	public void setOpenPager(boolean isOpenPager) {
		this.isOpenPager = isOpenPager;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Map<String, Object> getSelectParams() {
		return selectParams;
	}

	public void setSelectParams(Map<String, Object> selectParams) {
		this.selectParams = selectParams;
	}

	public Map<String, Object> getUpdateParams() {
		return updateParams;
	}

	public void setUpdateParams(Map<String, Object> updateParams) {
		this.updateParams = updateParams;
	}

	public Map<String, Object> getSortParams() {
		return sortParams;
	}

	public void setSortParams(Map<String, Object> sortParams) {
		this.sortParams = sortParams;
	}

	public Map<String, Object> getAddParams() {
		return addParams;
	}

	public void setAddParams(Map<String, Object> addParams) {
		this.addParams = addParams;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public Map<String, List<Integer>> getBetweenParams() {
		return betweenParams;
	}

	public void setBetweenParams(Map<String, List<Integer>> betweenParams) {
		this.betweenParams = betweenParams;
	}

	public Map<String, List<Integer>> getInParams() {
		return inParams;
	}

	public void setInParams(Map<String, List<Integer>> inParams) {
		this.inParams = inParams;
	}

	
}
