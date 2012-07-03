package com.taobao.ju.maven.common;

/**
 * User: duxing
 * Date: 2012-04-26
 * Time: ÉÏÎç11:08
 */

import com.alibaba.fastjson.JSON;

import java.io.IOException;

public class JsonUtil {

    public static String objectToJSonStr(Object object) throws IOException {
        return formatJson(JSON.toJSONString(object));
    }

    public static String formatJson(String strJson) {
        StringBuilder strNew = new StringBuilder();
        String strNewLine = "\r\n";
        String tabStr = "  ";
        int tabNum = 0;
        String strConfig;
        for (int i = 0; i < strJson.length(); i++) {
            if(i>0)strNewLine = "\r\n";
            strConfig=strJson.charAt(i)+"";
            char[] c=strJson.toCharArray();
            if ("{".equals(strConfig)) {
                strNew.append(strNewLine);
                strNew.append(repeatStr(tabStr,tabNum));
                strNew.append(strConfig);
                tabNum++;
                strNewLine = "\r\n";
                strNew.append(strNewLine);
                strNew.append(repeatStr(tabStr,tabNum));
            } else if ("}".equals(strConfig)) {
                tabNum--;
                strNew.append(strNewLine);
                strNew.append(repeatStr(tabStr,tabNum));
                strNew.append(strConfig);
            } else if (",".equals(strConfig)) {
                strNew.append(strConfig);
                strNew.append(strNewLine);
                strNew.append(repeatStr(tabStr,tabNum));
            } else {
                strNew.append(strConfig);
            }
        }
        return strNew.toString();
    }

    private static String repeatStr(String str , int num){
        if(num<=0)return "";
        StringBuilder r=new StringBuilder();
        for (int i=0;i<num;i++){
            r.append(str);
        }
        return r.toString();
    }
}

