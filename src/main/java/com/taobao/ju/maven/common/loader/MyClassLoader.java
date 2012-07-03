package com.taobao.ju.maven.common.loader;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: duxing
 * Date: 2012-06-19
 * Time: ÏÂÎç5:50
 */
public class MyClassLoader{
    private Log logger;
    private MavenProject project;
    private static ClassLoader cl = Thread.currentThread().getContextClassLoader();

    public MyClassLoader(MavenProject project) {
        this.project = project;
        if(this.logger==null){
            this.logger = new SystemStreamLog();
        }
        addAllArtifactsToClassLoader();
    }

    public void addAllArtifactsToClassLoader() {
//        logger.debug("adding all artifacts to classLoader");
        Collection<URL> urls = new ArrayList<URL>();
        for (Object artifact : project.getArtifacts()) {
            try {
                urls.add(((Artifact)artifact).getFile().toURI().toURL());
            } catch (MalformedURLException e) {
                logger.error(e);
            }
        }
        try {
            urls.add(new File(project.getBuild().getOutputDirectory()).toURI().toURL());
        } catch (MalformedURLException e) {
            logger.error(e);
        }
//        logger.debug("urls = \n" + urls.toString().replace(",","\n")) ;
        Thread.currentThread().setContextClassLoader(new URLClassLoader(urls.toArray(new URL[urls.size()]),cl));
    }

    public Class<?> loadClass(String className)throws Exception{
        Class<?> cls = null;
        try{
            cls = Class.forName(className);
        }catch (Exception e){
            //ignore errors
        }

        if(cls == null){
            try{
                cls =Thread.currentThread().getContextClassLoader().loadClass(className);
            }catch (Exception e){
                logger.error("[TairChecker] project need compile to it's build path.");
            }
        }
        return cls;
    }
    
    public void restoreClassLoader() {
        Thread.currentThread().setContextClassLoader(cl);
    }
}
