package com.taobao.ju.maven;

import com.taobao.ju.maven.common.loader.MyClassLoader;
import com.taobao.ju.maven.tair.TairAreaUtil;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * User: duxing
 * Date: 2012-03-29
 *
 * @goal taircheck
 * @requiresDependencyResolution runtime
 *
 *
 * mvn com.taobao.ju.maven:maven-check-plugin:1.0.0-SNAPSHOT:taircheck
 */
public class TairAreaChecker extends AbstractMojo {
    /**
     * The Maven project to analyze.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
    /**
     * tair area base name
     *
     * @parameter expression="${taircheck.baseName}" default-value="BASE"
     */
    private String baseName;
    /**
     * app name
     *
     * @parameter expression="${taircheck.appName}" default-value="jucenter"
     */
    private String appName;
    /**
     * app name
     *
     * @parameter expression="${taircheck.areaConstant}" default-value="com.taobao.ju.maven.tair.TairAreaConstant"
     */
    private String areaConstant;

    public TairAreaChecker(MavenProject project, String baseName, String appName, String areaConstant) {
        this.project = project;
        this.baseName = baseName;
        this.appName = appName;
        this.areaConstant = areaConstant;
    }

    public TairAreaChecker() {

    }


    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("------------------------------------------------------------------------");
        getLog().info("------------------------------------------------------------------------");
        getLog().info("--------------------        ╮(╯▽╰)╭        ------------------------");
        getLog().info("--------------------     TAIR AREA CHECKER     -------------------------");
        getLog().info("------------------------------------------------------------------------");
        getLogImage();
        getLog().info("------------------------------------------------------------------------");
        getLog().info("[TairAreaChecker] START ...");
        if (!"".equals(appName) && !"".equals(areaConstant)) {
            try {
                checkArea(appName, areaConstant, baseName);
            } catch (Exception e) {
                getLog().error(e);
            }
        } else {
            getLog().error("[TairAreaChecker] [" + appName + "] BEAN SETTING ERROR! APPNAME:" + appName + ",CONSTANT CLASS:" + areaConstant + ",CONSTANT BASENAME:" + baseName);
        }
        getLog().info("[TairAreaChecker] DONE ...");
        getLog().info("------------------------------------------------------------------------");
        getLog().info("");
    }

    private void getLogImage() {
        getLog().info("　◢ ██████◣　　　　　　◢████◣  ");
        getLog().info("◢◤　　　　　　◥◣　　　　◢◤　　　　 ◥◣  ");
        getLog().info("◤　　　　　　　　◥◣　　◢◤　　　　　　█");
        getLog().info("▎　　　◢█◣　　　◥◣◢◤　　◢█　　　█  ");
        getLog().info("◣　　◢◤　　◥◣　　　　　　◢◣◥◣　◢◤");
        getLog().info("◥██◤　　◢◤　　　　　　　　　◥◣  ");
        getLog().info("　　　　　　█　●　　　　　　　●　 █  ");
        getLog().info("　　　　　　█　〃　　　▄　　　〃　█  ");
        getLog().info("　　　　　　◥◣　　　╚╩╝　　　◢◤  ");
        getLog().info("　　　　　　　◥█▅▃▃　▃▃▅█◤  ");
        getLog().info("　　　　　　　　　◢◤　　　◥◣　  ");
        getLog().info("  　　　　　　　　█　　　　　█　  ");
        getLog().info("　　　　　　　　◢◤▕　　　▎◥◣  ");
        getLog().info("　　　　　　　▕▃◣◢▅▅▅◣◢▃▕　 ");
    }

    public void init() throws Exception {
        if (!"".equals(appName) && !"".equals(areaConstant)) {
            checkArea(appName, areaConstant, baseName);
        } else {
            getLog().error("[TairAreaChecker] [" + appName + "] BEAN SETTING ERROR! APPNAME:" + appName + ",CONSTANT CLASS:" + areaConstant + ",CONSTANT BASENAME:" + baseName);
        }
    }

    /**
     * tair 分区检查主方法
     *
     * @param appName
     *         appname
     * @param areaConstant
     *         类配置
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     */
    private void checkArea(String appName, String areaConstant, String baseName) throws Exception {
        MyClassLoader clHandler = new MyClassLoader(project);
        Class<?> a = clHandler.loadClass(areaConstant);
        Field[] field = a != null ? a.getDeclaredFields() : null;
        boolean tmpResult;
        boolean finalResult = true;
        Map<String, Integer> failMap = new HashMap<String, Integer>();
        if (field != null) {
            for (Field f : field) {
                //area base 的配置
                if (baseName.equals(f.getName())) {
                    getLog().info("[TairAreaChecker] [" + appName + "] " + baseName + " CONSTANT:" + f.get(a) + "");
                } else {
                    if(!(f.get(a) instanceof Integer))continue;
                    tmpResult = TairAreaUtil.checkArea((Integer) f.get(a), f.getName(), appName);
                    if (!tmpResult) {
                        failMap.put(f.getName(), (Integer) f.get(a));
                        if (finalResult) finalResult = tmpResult;
                    }
                }
            }
            if (!finalResult) {
                getLog().error("[TairAreaChecker] [" + appName + "] !!!!!!!TAIR AREA SETTING ERROR!!!!!!FILE:" + areaConstant);
                getLog().error("[TairAreaChecker] [" + appName + "] ERROR SETTING:" + failMap.toString());
            } else {
                getLog().info("[TairAreaChecker] [" + appName + "] ALL AREA IS OK!");
            }
        } else {
            getLog().error("[TairAreaChecker] [" + appName + "] BEAN SETTING ERROR! APPNAME:" + appName + ",CONSTANT CLASS:" + areaConstant);
        }
        clHandler.restoreClassLoader();
    }
}
