package com.gomusic.app.helpers.app;

import java.util.Hashtable;

import com.gomusic.app.helpers.constants.Constants;

public class BeanHelper {

    private static BeanHelper _instance;
    private Hashtable<String, Object> _hash;

    private BeanHelper() {
        _hash = new Hashtable<String, Object>();
    }

    private static BeanHelper getInstance() {
        if(_instance==null) {
            _instance = new BeanHelper();
        }
        return _instance;
    }

    public static void addObjectForKey(Object object, String key) {
        getInstance()._hash.put(key, object);
    }
    
    /**
     * 
     * @param key
     * @param doRetain
     * @return
     */
    public static Object getObjectForKey(String key,boolean doRetain) {
        BeanHelper helper = getInstance();
        Object data = helper._hash.get(key);
        if(!doRetain){
        	helper._hash.remove(key);
        }
        helper = null;
        return data;
    }
    
    public static String getString(String key) {
        return getString(key,false);
    }
    
    public static String getString(String key,boolean doRetain) {
        BeanHelper helper = getInstance();
        Object data = helper._hash.get(key);
        if(!doRetain){
        	helper._hash.remove(key);
        }
        helper = null;
        return data == null ? Constants.EMPTY_STRING : data.toString();
    }
    
    public static Object getObjectForKey(String key) {
        return getObjectForKey(key,false);
    }
    
    public static void destroy() {
    	_instance = null;
    }
}