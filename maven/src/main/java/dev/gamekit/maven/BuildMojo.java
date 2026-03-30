package dev.gamekit.maven;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

/** {@link BuildMojo} is a mojo for building a gamekit application into an executable for the current platform */
@Mojo(name = "build", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class BuildMojo extends AbstractMojo {
  /** The fully qualified path of the class contain the {@code main()} method */
  @Parameter(name = "mainClass", required = true)
  private String mainClass;

  @Component
  private MavenProject mavenProject;

  @Component
  private MavenSession mavenSession;

  @Component
  private BuildPluginManager pluginManager;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info(String.format("Main clazz: %s", mainClass));

    executeMojo(
      plugin(
        groupId("org.apache.maven.plugins"),
        artifactId("maven-shade-plugin"),
        version("3.6.0")
      ),
      goal("shade"),
      configuration(
        element(
          name("transformers"),
          element(
            name("transformer"),
            attribute("implementation", "org.apache.maven.plugins.shade.resource.ManifestResourceTransformer"),
            element(
              name("mainClass"),
              mainClass
            ),
            // Required for Log4j to work properly since it's a multi-release JAR
            element(
              name("manifestEntries"),
              element(
                name("Multi-Release"),
                "true"
              )
            )
          )
        ),
        element(
          name("createDependencyReducedPom"),
          "false"
        )
      ),
      executionEnvironment(
        mavenProject,
        mavenSession,
        pluginManager
      )
    );
  }
}
