package org.fastser.web.utils;

import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;

public class WatchUtils {
	
	private static final ThreadLocal<StopWatch> STOP_WATCH = new ThreadLocal<StopWatch>();
	private static final ThreadLocal<String> SIGN = new ThreadLocal<String>();
	
	public static void start(String bucket, String restName, String option, String version, String tag){
		String tagStr = bucket + "-" + restName + "-" + option + "-" + version;
		STOP_WATCH.set(new Log4JStopWatch(tagStr + "-" + tag));
		SIGN.set(tagStr);
	}
	
	public static void lap(String tag){
		if(null != STOP_WATCH.get()){
			STOP_WATCH.get().lap(SIGN.get() + "-" + tag);
		}
	}
	
	public static void lap(String tag, String message){
		STOP_WATCH.get().lap(SIGN.get() + "-" + tag, message);
	}
	
	public static void stop(String tag){
		STOP_WATCH.get().stop(SIGN.get() + "-" + tag);
		STOP_WATCH.remove();
		SIGN.remove();
	}
	
	public static void stop(String tag, String message){
		STOP_WATCH.get().stop(SIGN.get() + "-" + tag, message);
		STOP_WATCH.remove();
		SIGN.remove();
	}

}
