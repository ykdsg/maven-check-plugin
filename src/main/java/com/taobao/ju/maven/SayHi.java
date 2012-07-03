package com.taobao.ju.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * User: duxing
 * Date: 2012-03-29
 * @goal sayhi
 */
public class SayHi extends AbstractMojo
{
    /**
     * The greeting to display.
     *
     * @parameter expression="${sayhi.greeting}" default-value="Hello World!"
     */
    private String greeting;

    public SayHi(String greeting) {
        this.greeting = greeting;
    }

    public SayHi() {

    }


    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(greeting);
    }
}
