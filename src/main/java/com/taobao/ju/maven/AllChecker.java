package com.taobao.ju.maven;

import com.taobao.ju.maven.common.JsonUtil;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: duxing
 * Date: 2012-03-29
 *
 * @goal allcheck
 * @requiresDependencyResolution runtime
 *
 * mvn com.taobao.ju.maven:maven-check-plugin:1.0.0-SNAPSHOT:allcheck
 */
public class AllChecker extends AbstractMojo{
    /**
     * The Maven project to analyze.
     *
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;
    /**
     * The greeting to display.
     *
     * @parameter expression="${sayhi.greeting}" default-value="other checker, waiting ..."
     */
    private String greeting;
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


    @SuppressWarnings("unchecked")
    public void execute() throws MojoExecutionException, MojoFailureException {

//        getLog().info("--------------------        ¨r(¨s¨Œ¨t)¨q        ------------------------");
        getLog().info("------------------------------------------------------------------------");
        getLog().info("---------------------     JU CHECKERS RUN     --------------------------");


        //tair check
        TairAreaChecker ck = new TairAreaChecker(project,baseName,appName,areaConstant);
        ck.execute();

        //say hi
        SayHi sh = new SayHi(greeting);
        sh.execute();

    }
}
