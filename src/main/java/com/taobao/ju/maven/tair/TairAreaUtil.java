package com.taobao.ju.maven.tair;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

import java.util.HashMap;
import java.util.Map;

/**
 * �淶Tair�����Ĺ�����
 *
 * ������: getArea
 *
 * User: duxing
 * Date: 2012-03-01
 * Time: ����4:14
 */
public class TairAreaUtil {

    public static final Log log = new SystemStreamLog();
    /**
     * �������app������
     */
    public final static Map<String, Map<String, Integer>> appMap = new HashMap<String, Map<String, Integer>>() {{
         //Ӧ����                  ����ֵ ��Сֵ ���ֵ
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

    //ȷ��Ϊ�����õķ���ֵ
    public static final int ERROR_AREA = 2048;

    /**
     * ������ - ������ֵ
     *
     * @param area ��ǰ�����е���������,��Ҫ>0 ����Ϊ������base+areaId
     * @param appName Ӧ����
     * @return tair����id
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
                    //��area�����˷�Χ��ʱ��
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
     * �趨map
     *
     * @param base    ����ֵ
     * @param min     ��Сֵ
     * @param max     ���ֵ
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
