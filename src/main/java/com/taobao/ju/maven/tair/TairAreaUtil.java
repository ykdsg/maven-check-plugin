package com.taobao.ju.maven.tair;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

import java.util.HashMap;
import java.util.Map;

/**
 * 规范Tair分区的工具类
 *
 * 主方法: getArea
 *
 * User: duxing
 * Date: 2012-03-01
 * Time: 下午4:14
 */
public class TairAreaUtil {

    public static final Log log = new SystemStreamLog();
    /**
     * 定义各个app的区间
     */
    public final static Map<String, Map<String, Integer>> appMap = new HashMap<String, Map<String, Integer>>() {{
         //应用名                  基础值 最小值 最大值
        put("jucenter",     getMap(0,     1, 100));
        put("jumisc",       getMap(100, 101, 150));
        put("juitemcenter", getMap(150, 151, 200));
        put("snsju",        getMap(200, 201, 300));
        put("juitem",       getMap(300, 301, 400));
        put("myju",         getMap(400, 401, 500));
        put("candidate",    getMap(500, 501, 510));
        put("julottery",    getMap(510, 511, 550));
        put("jufee",        getMap(550, 551, 570));
        put("juseller",     getMap(570, 571, 600));
    }};

    //确认为不可用的分区值
    public static final int ERROR_AREA = 2048;

    /**
     * 主方法 - 检查分区值
     *
     * @param area 当前分区中的区域序列,需要>0 分区为本区的base+areaId
     * @param appName 应用名
     * @return tair分区id
     */
    public static boolean checkArea(Integer area,String areaName, String appName) {
        boolean result;
        if (area==null || area <= 0) {
            result = false;
        } else {
            Map<String, Integer> map = appMap.get(appName);
            if (map != null) {
                Integer min = map.get("min");
                Integer max = map.get("max");
                Integer base = map.get("base");
                if (min != null && max != null && base != null) {
                    //当area超出了范围的时候
                    result = !(area < min || area > max);
                    if(!result){
                        log.error("[TAIR CACHE AREA SETTINNG ERROR] error settting area! areaName:"+areaName+",area:"+area);
                    }
                } else {
                    log.error("[TAIR CACHE AREA SETTINNG ERROR] no min/max/base found with this App:"+appName+"!");
                    result = false;
                }
            } else {
                log.error("[TAIR CACHE AREA SETTINNG ERROR] no app found(App:"+appName+")!");
                result = false;
            }
        }
        return result;
    }

    /**
     * 设定map
     *
     * @param base    基础值
     * @param min     最小值
     * @param max     最大值
     * @return map
     */
    private static Map<String,Integer> getMap(final Integer base, final Integer min, final Integer max) {
        return new HashMap<String, Integer>() {{
            put("base", base);
            put("min", min);
            put("max", max);
        }};
    }

    public static void main(String[] args) throws Exception{

        /*  Class<?> a = Class.forName("com.taobao.ju.center.constant.TairAreaConstant");
        Field[] field = a.getDeclaredFields();
        for (Field f : field) {
            if (!"BASE".equals(f.getName())) {
                TairAreaUtil.checkArea((Integer)f.get(a),f.getName(),"jucenter");
            }
        }*/
//        System.out.print(TairAreaUtil.appMap);
    }


}
