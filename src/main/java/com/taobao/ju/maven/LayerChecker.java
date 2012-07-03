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
 * @goal layercheck
 * @phase validate
 * @threadSafe
 * @requiresDependecyResolutiong validate
 *
 * mvn com.taobao.ju.maven:maven-check-plugin:1.0.0-SNAPSHOT:layercheck
 */
public class LayerChecker extends AbstractMojo{
    /**
     * The Maven project to analyze.
     *
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;
    /**
     * Maven Artifact Factory component.
     *
     * @component
     */
    private ArtifactFactory artifactFactory;
    /**
     * Maven Project Builder component.
     *
     * @component
     */
    private MavenProjectBuilder mavenProjectBuilder;
    /**
     * @parameter expression="${dependcheck.ctrlProject}"
     * @required
     */
    private MavenProject ctrlProject;
    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;
    /**
     * Remote repositories used for the project.
     *
     * @parameter default-value="${project.remoteArtifactRepositories}"
     * @readonly
     */
    private List<ArtifactRepository> remoteRepositories;

    /**
     * @parameter expression="${ctrlProjectGroupId}"  default-value="com.taobao.ju"
     * @required
     * @readonly
     */
    private String ctrlProjectGroupId;
    /**
     * @parameter expression="${ctrlProjectArtifactId}" default-value="ju-version-ctrl"
     * @required
     * @readonly
     */
    private String ctrlProjectArtifactId;
    /**
     * @parameter expression="${ctrlProjectVersion}" default-value="1.0.0-SNAPSHOT"
     * @required
     * @readonly
     */
    private String ctrlProjectVersion;


    private ArtifactMetadataSource artifactMetadataSource;
    private ArtifactResolutionResult result;
    private ArtifactCollector artifactCollector;
    private List list;

    @SuppressWarnings("unchecked")
    public void execute() throws MojoExecutionException, MojoFailureException {
        List<Dependency> deps = project.getDependencies();

        Artifact ar = getArtifact(ctrlProjectGroupId, ctrlProjectArtifactId, ctrlProjectVersion);

        try {

            Map managedVersions = project.getManagedVersionMap();

            Set dependencyArtifacts = project.getDependencyArtifacts();

            if ( dependencyArtifacts == null )
            {
                dependencyArtifacts = project.createArtifacts( artifactFactory, null, null );
            }

            getLog().info(JsonUtil.objectToJSonStr( project.getArtifact()));
            getLog().info(JsonUtil.objectToJSonStr(managedVersions));
            getLog().info(JsonUtil.objectToJSonStr(dependencyArtifacts));


            MavenProject mp =getMavenProject(ar);
            getLog().info("1");
            //Dependencies
            getLog().info(JsonUtil.objectToJSonStr(mp.getDependencies()));
            getLog().info("2");
            //DependencyManagement
            getLog().info(JsonUtil.objectToJSonStr(mp.getDependencyManagement()));
            getLog().info("3");
            //父项目的对象
            getLog().info(JsonUtil.objectToJSonStr(mp.getParentArtifact()));

        } catch (Exception e) {
            e.printStackTrace();

        }

        try {
            getLog().info(JsonUtil.objectToJSonStr(project.getBasedir()+ "/pom.xml"));
            getLog().info(localRepository.toString());
            getLog().info(remoteRepositories.toString());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        if(deps.size()==0)getLog().info("0 size!!!");
        try{
            Map<String,String> depMap=new HashMap<String, String>();
            List<String> depReduplicateList=new ArrayList<String>();
            String key;
            for(Dependency dep : deps){
                key = dep.getGroupId()+":"+dep.getArtifactId();
                if(depMap.get(key) == null){
                    depMap.put(key, dep.getVersion());
                }else{
                    depReduplicateList.add(key);
                }
            }
            getLog().info(JsonUtil.objectToJSonStr(depMap));
            getLog().info(JsonUtil.objectToJSonStr(depReduplicateList));
        }catch (Exception e2){
            getLog().info(e2);
        }


    }
    private ArtifactFilter createResolvingArtifactFilter(String scope)
    {
        ArtifactFilter filter;

        // filter scope
        if ( scope != null )
        {
            getLog().debug( "+ Resolving dependency tree for scope '" + scope + "'" );

            filter = new ScopeArtifactFilter( scope );
        }
        else
        {
            filter = null;
        }

        return filter;
    }

    private Artifact getArtifact(String ctrlProjectGroupId, String ctrlProjectArtifactId,String ctrlProjectVersion) {
        return artifactFactory.createArtifact(ctrlProjectGroupId, ctrlProjectArtifactId, ctrlProjectVersion, Artifact.SCOPE_COMPILE, "jar" );
    }

    private MavenProject getMavenProject( Artifact artifactObj )
            throws MojoExecutionException, ProjectBuildingException
    {
        if ( Artifact.SCOPE_SYSTEM.equals( artifactObj.getScope() ) )
        {
            throw new MojoExecutionException( "System artifact is not be handled." );
        }

        Artifact copyArtifact = ArtifactUtils.copyArtifact( artifactObj );
        if ( !"pom".equals( copyArtifact.getType() ) )
        {
            copyArtifact =
                    artifactFactory.createProjectArtifact( copyArtifact.getGroupId(), copyArtifact.getArtifactId(),
                            copyArtifact.getVersion(), copyArtifact.getScope() );
        }

        return mavenProjectBuilder.buildFromRepository( copyArtifact, remoteRepositories, localRepository );
    }

}
