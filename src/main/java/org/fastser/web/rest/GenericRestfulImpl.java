package org.fastser.web.rest;

import static org.fastser.web.constant.RestConstant.OBJECT_ID;
import static org.fastser.web.constant.RestConstant.RESULT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.fastser.dal.core.BaseDAL;
import org.fastser.dal.criteria.Model;
import org.fastser.dal.criteria.QueryCriteria;
import org.fastser.dal.criteria.QueryCriteria.Criteria;
import org.fastser.dal.descriptor.QueryResult;
import org.fastser.web.constant.RestConstant;
import org.fastser.web.context.Page;
import org.fastser.web.context.RestRequest;
import org.fastser.web.context.RestResponse;
import org.fastser.web.utils.WatchUtils;

public class GenericRestfulImpl implements GenericRestful {
	
    private BaseDAL baseDAL;
    
	public void setBaseDAL(BaseDAL baseDAL) {
		this.baseDAL = baseDAL;
	}

	@Override
	public void _list(RestRequest request, RestResponse response) {
		QueryResult queryResult = null;
		 if(request.hasPage()){
			 queryResult = selectPageByCriteria(null, request.getParameters(), request.getTable(), request.getCacheTimeOut());
		 }else{
			 queryResult = selectByCriteria(null, request.getParameters(), request.getTable(), request.getCacheTimeOut());
		 }
		 if(queryResult.getList() != null){
			 response.put(RestResponse.SYSTEM_HANDLE_RESULT_KEY, queryResult.getList());
		 }
		 if(queryResult.getPage() != null){
			 response.putPage(Page.valueOf(queryResult.getPage()));
		 }
	}

	@Override
	public void _get(RestRequest request, RestResponse response) {
		 QueryResult queryResult = selectByPrimaryKey(null, request.getParameterInt(OBJECT_ID), request.getTable(), request.getCacheTimeOut());
		 if(queryResult.get() != null){
			 response.put(RestResponse.SYSTEM_HANDLE_RESULT_KEY, queryResult.get());
		 }
	}

	@Override
	public void _insert(RestRequest request, RestResponse response) {
		int result = insert(request.getParameters(), request.getTable());
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put(RESULT, result);
		response.put(RestResponse.SYSTEM_HANDLE_RESULT_KEY, rtMap);
	}

	@Override
	public void _update(RestRequest request, RestResponse response) {
		int result = updateByPrimaryKey(request.getParameterInt(OBJECT_ID), request.getParameters(), request.getTable());
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put(RESULT, result);
		response.put(RestResponse.SYSTEM_HANDLE_RESULT_KEY, rtMap);
	}

	@Override
	public void _delete(RestRequest request, RestResponse response) {
		int result = deleteByPrimaryKey(request.getParameterInt(OBJECT_ID), request.getTable());
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put(RESULT, result);
		response.put(RestResponse.SYSTEM_HANDLE_RESULT_KEY, rtMap);
	}
	
	protected QueryResult selectPageByCriteria(List<String> fields, Map<String, Object> params, String table, int seconds) {
        WatchUtils.lap("generic-service-selectByCriteria-start");
        // query
        QueryCriteria queryCriteria = new QueryCriteria();
        queryCriteria.setTable(table);
        Criteria criteria = queryCriteria.createCriteria();
        int pageIndex = 0;
        int pageSize = 0;
        if (null != params) {
            Iterator<String> iter = params.keySet().iterator();
            while (iter.hasNext()) {
                String name = iter.next();
                Object value = params.get(name);
                if (StringUtils.isNotEmpty(name)) {
                    if ("pageIndex".equals(name)) {
                        pageIndex = Integer.valueOf((String) value);
                    } else if ("pageSize".equals(name)) {
                        pageSize = Integer.valueOf((String) value);
                    } else if ("order".equals(name)) {
                        queryCriteria.setOrderByClause(String.valueOf(value));
                    } else if ("selectOne".equals(name)) {
                        queryCriteria.setSelectOne(Boolean.valueOf(String.valueOf(value)));
                    } else {
                    	if(value instanceof HashMap){
                    		Map<?,?> valueMap = (HashMap<?,?>)value;
                    		Iterator<?> ite = valueMap.keySet().iterator();
                    		while(ite.hasNext()){
                    			String key = String.valueOf(ite.next());
                    			buildCriteria(criteria, name, valueMap, key);
                    		}
                    	}else{
                    		criteria.andColumnEqualTo(name, value);
                    	}
                    }
                }
            }
        }
        if(pageIndex >= 0){
        	if(pageIndex == 0){
        		pageSize = RestConstant.PAGE_INDEX;
        	}
        	if(pageSize <= 0){
            	pageSize = RestConstant.PAGE_SIZE;
            }
        	queryCriteria.setPageIndex(pageIndex);
            queryCriteria.setPageSize(pageSize);
        }
        WatchUtils.lap("base-dal-selectByCriteria-start");
        QueryResult qresult = baseDAL.selectPageByCriteria(fields, queryCriteria, dalCache(seconds));
        WatchUtils.lap("base-dal-selectByCriteria-end");
        WatchUtils.lap("generic-service-selectByCriteria-end");
        return qresult;
    }
	
	protected QueryResult selectByCriteria(List<String> fields, Map<String, Object> params, String table, int seconds) {
        WatchUtils.lap("generic-service-selectByCriteria-start");
        
        //List<String> failFields = checkTableAcl(table, Resource.TABLE_OPTION_READ);
        // query
        QueryCriteria queryCriteria = new QueryCriteria();
        queryCriteria.setTable(table);
        Criteria criteria = queryCriteria.createCriteria();
        int pageIndex = 0;
        int pageSize = 0;
        if (null != params) {
            Iterator<String> iter = params.keySet().iterator();
            while (iter.hasNext()) {
                String name = iter.next();
                Object value = params.get(name);
                if (StringUtils.isNotEmpty(name)) {
                    if ("pageIndex".equals(name)) {
                        pageIndex = Integer.valueOf((String) value);
                    } else if ("pageSize".equals(name)) {
                        pageSize = Integer.valueOf((String) value);
                    } else if ("order".equals(name)) {
                        queryCriteria.setOrderByClause(String.valueOf(value));
                    } else if ("selectOne".equals(name)) {
                        queryCriteria.setSelectOne(Boolean.valueOf(String.valueOf(value)));
                    } else {
                    	if(value instanceof HashMap){
                    		Map<?,?> valueMap = (HashMap<?,?>)value;
                    		Iterator<?> ite = valueMap.keySet().iterator();
                    		while(ite.hasNext()){
                    			String key = String.valueOf(ite.next());
                    			buildCriteria(criteria, name, valueMap, key);
                    		}
                    	}else{
                    		criteria.andColumnEqualTo(name, value);
                    	}
                    }
                }
            }
        }
        if(pageIndex >= 0){
        	if(pageIndex == 0){
        		pageSize = RestConstant.PAGE_INDEX;
        	}
        	if(pageSize <= 0){
            	pageSize = RestConstant.PAGE_SIZE;
            }
        	queryCriteria.setPageIndex(pageIndex);
            queryCriteria.setPageSize(pageSize);
        }
        WatchUtils.lap("base-dal-selectByCriteria-start");
        QueryResult qresult = baseDAL.selectByCriteria(fields, queryCriteria, dalCache(seconds));
        WatchUtils.lap("base-dal-selectByCriteria-end");
        WatchUtils.lap("generic-service-selectByCriteria-end");
        /*if(failFields != null && failFields.size() > 0){
        	qresult.setResultList(qresult.getList(failFields));
        }*/
        return qresult;
    }


	protected QueryResult selectByPrimaryKey(List<String> fields, Integer id, String table, int seconds) {
    	//List<String> failFields = checkTableAcl(table, Resource.TABLE_OPTION_READ);
    	Model mod = new Model(table);
        mod.setSinglePrimaryKey(id);
        QueryResult qresult = baseDAL.selectByPrimaryKey(fields, mod, dalCache(seconds));
        /*if(failFields != null && failFields.size() > 0){
        	qresult.setResultMap(qresult.get(failFields));
        }*/
        return qresult;
    }

	protected QueryResult selectByIds(List<String> fields, Map<String, Object[]> params, String table, int seconds,
            String database) {
    	//List<String> failFields = checkTableAcl(table, Resource.TABLE_OPTION_READ);
        // query
        QueryCriteria queryCriteria = new QueryCriteria();
        queryCriteria.setDatabase(database);
        queryCriteria.setTable(table);
        Criteria criteria = queryCriteria.createCriteria();
        if (null != params) {
            Iterator<String> iter = params.keySet().iterator();
            while (iter.hasNext()) {
                String name = iter.next();
                Object[] values = params.get(name);
                if (StringUtils.isNotEmpty(name)) {
                    criteria.andColumnIn(name, Arrays.asList(values));
                }
            }
        }
        QueryResult qresult = baseDAL.selectByCriteria(fields, queryCriteria, dalCache(seconds));
        /*if(failFields != null && failFields.size() > 0){
        	qresult.setResultList(qresult.getList(failFields));
        }*/
        return qresult;
    }

	protected int deleteByPrimaryKey(Integer id, String table) {
    	//checkTableAcl(table, Resource.TABLE_OPTION_REMOVE);
        Model mod = new Model(table);
        mod.setSinglePrimaryKey(id);
        int result = baseDAL.deleteByPrimaryKey(mod);
        return result;
    }
    
	protected int deleteByCriteria(Map<String, Object> params, String table, String database) {
    	//checkTableAcl(table, Resource.TABLE_OPTION_REMOVE);
        QueryCriteria queryCriteria = new QueryCriteria();
        queryCriteria.setDatabase(database);
        queryCriteria.setTable(table);
        Criteria criteria = queryCriteria.createCriteria();
        if (null != params) {
            Iterator<String> iter = params.keySet().iterator();
            while (iter.hasNext()) {
                String name = iter.next();
                Object value = params.get(name);
                if (StringUtils.isNotEmpty(name)) {
                	if(value instanceof HashMap){
                		Map<?,?> valueMap = (HashMap<?,?>)value;
                		Iterator<?> ite = valueMap.keySet().iterator();
                		while(ite.hasNext()){
                			String key = String.valueOf(ite.next());
                			buildCriteria(criteria, name, valueMap, key);
                		}
                	}else{
                		criteria.andColumnEqualTo(name, value);
                	}
                }
            }
        }
        int result = baseDAL.deleteByCriteria(queryCriteria);
        return result;
    }

	protected int updateByPrimaryKey(Integer id, Map<String, Object> params, String table) {
    	List<String> failFields = null;//checkTableAcl(table, Resource.TABLE_OPTION_UPDATE);
    	params = RestDataUtils.hiddenRequestField(params, failFields);
        Model mod = new Model(table);
        mod.setSinglePrimaryKey(id);
        mod.addContent(params);
        int result = baseDAL.updateByPrimaryKey(mod);
        return result;
    }

	protected int insert(Map<String, Object> params, String table) {
    	List<String> failFields = null;//checkTableAcl(table, Resource.TABLE_OPTION_INSERT);
    	params = RestDataUtils.hiddenRequestField(params, failFields);
        Model mod = new Model(table);
        mod.addContent(params);
        int result = baseDAL.insert(mod);
        return result;
    }

	protected int countByCriteria(Map<String, Object> params, String table, int seconds) {
    	//checkTableAcl(table, Resource.TABLE_OPTION_READ);
        QueryCriteria queryCriteria = new QueryCriteria();
        queryCriteria.setTable(table);
        Criteria criteria = queryCriteria.createCriteria();
        if (null != params) {
            Iterator<String> iter = params.keySet().iterator();
            while (iter.hasNext()) {
                String name = iter.next();
                Object value = params.get(name);
                if (StringUtils.isNotEmpty(name)) {
                	if(value instanceof HashMap){
                		Map<?,?> valueMap = (HashMap<?,?>)value;
                		Iterator<?> ite = valueMap.keySet().iterator();
                		while(ite.hasNext()){
                			String key = String.valueOf(ite.next());
                			buildCriteria(criteria, name, valueMap, key);
                		}
                	}else{
                		criteria.andColumnEqualTo(name, value);
                	}
                }
            }
        }
        // paging query
        int total = baseDAL.countByCriteria(queryCriteria, dalCache(seconds));
        return total;
    }

	protected void clearDALCache(String table, String database) {
        if (StringUtils.isNotEmpty(table)) {
            baseDAL.clearCache(database, table);
        }
    }
    
    /**
     * check table acl
     * @param table
     * @param option
     * @return
     */
    private List<String> checkTableAcl(String table, String option) {
		//String bucket = RestContextManager.getContext().getBucket();
        List<String> failFields = null;
        /*if(StringUtils.isNotEmpty(bucket)){
        	RestTable restTable = SystemCache.getRestTable(bucket, table);
        	failFields = AccessTokenUtils.checkPermissions(restTable, option);
        }*/
		return failFields;
	}

    private int dalCache(int seconds) {
        return seconds == BaseDAL.NO_CACHE ? BaseDAL.NO_CACHE : 0;
    }
    
    private Object convertValue(Object value){
    	String vstr = String.valueOf(value);
    	if(vstr.endsWith(".0")){
    		vstr = vstr.replace(".0", "");
    		return Integer.valueOf(vstr);
    	}
    	return value;
    }
    
    /**
     * 
     * @param criteria
     * @param name
     * @param valueMap
     * @param key
     */
    private void buildCriteria(Criteria criteria, String name, Map<?, ?> valueMap, String key) {
		if(Condition.IS_NULL.equals(key)){
			criteria.andColumnIsNull(name);
		}else if(Condition.IS_NOT_NULL.equals(key)){
			criteria.andColumnIsNotNull(name);
		}else if(Condition.EQUAL.equals(key)){
			criteria.andColumnEqualTo(name, convertValue(valueMap.get(key)));
		}else if(Condition.NOT_EQUAL.equals(key)){
			criteria.andColumnNotEqualTo(name, convertValue(valueMap.get(key)));
		}else if(Condition.GREATER_THAN.equals(key)){
			criteria.andColumnGreaterThan(name, convertValue(valueMap.get(key)));
		}else if(Condition.GREATER_THAN_OR_EQUAL.equals(key)){
			criteria.andColumnGreaterThanOrEqualTo(name, convertValue(valueMap.get(key)));
		}else if(Condition.LESS_THAN.equals(key)){
			criteria.andColumnLessThan(name, convertValue(valueMap.get(key)));
		}else if(Condition.LESS_THAN_OR_EQUAL.equals(key)){
			criteria.andColumnGreaterThanOrEqualTo(name, convertValue(valueMap.get(key)));
		}else if(Condition.LIKE.equals(key)){
			criteria.andColumnLike(name, convertValue(valueMap.get(key)));
		}else if(Condition.NOT_LIKE.equals(key)){
			criteria.andColumnNotLike(name, convertValue(valueMap.get(key)));
		}else if(Condition.IN.equals(key)){
			String vlist = String.valueOf(valueMap.get(key));
			List<Object> array = new ArrayList<Object>();
			array.addAll(Arrays.asList(vlist.split(",")));
			criteria.andColumnIn(name, array);
		}else if(Condition.NOT_IN.equals(key)){
			String vlist = String.valueOf(valueMap.get(key));
			List<Object> array = new ArrayList<Object>();
			array.addAll(Arrays.asList(vlist.split(",")));
			criteria.andColumnNotIn(name, array);
		}else if(Condition.BETWEEN.equals(key)){
			String valbt = String.valueOf(valueMap.get(key));
			String[] val = valbt.split(",");
			criteria.andColumnBetween(name, val[0], val[1]);
		}else if(Condition.NOT_BETWEEN.equals(key)){
			String valbt = String.valueOf(valueMap.get(key));
			String[] val = valbt.split(",");
			criteria.andColumnBetween(name, val[0], val[1]);
		}
	}
    
    
    public static class Condition{
    	public static final String TAG= "$";
        public static final String IS_NULL = "$nu";
        public static final String IS_NOT_NULL = "$nnu";
        public static final String EQUAL = "$eq";
        public static final String NOT_EQUAL = "$neq";
        public static final String GREATER_THAN = "$gt";
        public static final String GREATER_THAN_OR_EQUAL = "$gte";
        public static final String LESS_THAN = "$lt";
        public static final String LESS_THAN_OR_EQUAL = "$lte";
        public static final String LIKE = "$lk";
        public static final String NOT_LIKE = "$nlk";
        public static final String IN = "$in";
        public static final String NOT_IN = "$nin";
        public static final String BETWEEN = "$bt";
        public static final String NOT_BETWEEN = "$nbt";
       
    }

}
