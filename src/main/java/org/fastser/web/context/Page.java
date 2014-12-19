package org.fastser.web.context;

import static org.fastser.web.constant.RestConstant.DEFAULT_PAGE_COUNT;
import static org.fastser.web.constant.RestConstant.DEFAULT_PAGE_INDEX;
import static org.fastser.web.constant.RestConstant.DEFAULT_PAGE_SIZE;

import java.io.Serializable;
import java.util.Map;

import org.fastser.dal.core.BaseDAL;
import org.fastser.dal.descriptor.QueryResult;

public class Page implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4236322096856633537L;
	
	// 当前页面索引 默认为第1页
    protected int pageIndex = DEFAULT_PAGE_INDEX;
    // 每页显示的条数 默认为每页10条数据
    protected int pageSize = DEFAULT_PAGE_SIZE;
    // 总页数
    protected int pageCount = DEFAULT_PAGE_COUNT;
    // 总记录条数 默认总记录数为0
    protected int recordTotal = 0;

    public Page(int pageIndex, int pageSize) {
    	setPageIndex(pageIndex);
    	setPageSize(pageSize);
    }
    
    public void setPageIndex(int pageIndex) {
    	if(pageIndex > 0){
    		this.pageIndex = pageIndex;
    	}
    }

    public void setPageSize(int pageSize) {
    	if(pageSize > 0){
    		this.pageSize = pageSize;
    	}
    }
    
    public void setRecordTotal(int recordTotal) {
    	if(recordTotal > 0){
    		this.recordTotal = recordTotal;
    		this.pageCount = this.recordTotal / this.pageSize;
            if (this.recordTotal % this.pageSize != 0) {
                this.pageCount++;
            }
    	}
	}

    public int getRecordTotal() {
		return recordTotal;
	}
	

	public int getPageSize() {
        return pageSize;
    }

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getPageIndex() {
		return pageIndex;
	}
    
    public static Page valueOf(Map<String, Object> map){
    	if(null != map){
    		int index = getPageValue(map, BaseDAL.PAGE_INDEX_KEY);
    		int size = getPageValue(map, BaseDAL.PAGE_SIZE_KEY);
    		int count = getPageValue(map, BaseDAL.PAGE_COUNT_KEY);
    		int total = getPageValue(map, BaseDAL.RECORD_TOTAL_KEY);
    		Page page = new Page(index, size);
    		page.setPageCount(count);
    		page.setRecordTotal(total);
    		return page;
    	}
    	return null;
    }
    
    private static int getPageValue(Map<String, Object> map, String key){
    	if(map != null){
    		Object value = map.get(key);
    		if(null != value){
    			if(value instanceof String){
        			return Integer.valueOf((String)value);
        		}else{
        			return (int)value;
        		}
    		}
    	}
    	return 0;
    }


}
