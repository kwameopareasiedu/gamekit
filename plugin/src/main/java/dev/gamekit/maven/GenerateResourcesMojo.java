package dev.gamekit.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** {@link GenerateResourcesMojo} is a mojo for including additional resources to the target project */
@Mojo(name = "generateResources", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class GenerateResourcesMojo extends AbstractMojo {
  @Component
  private MavenProject mavenProject;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      getLog().info("Generating Log4j config file");
      Path configFilePath = Paths.get(mavenProject.getBuild().getDirectory(), "classes", "log4j2.xml");
      String configContent = """
        <?xml version="1.0" encoding="UTF-8"?>
        <Configuration status="INFO">
          <Loggers>
            <Root level="WARN">
              <AppenderRef ref="console"/>
            </Root>
          </Loggers>
        
          <Appenders>
            <Console name="console" target="SYSTEM_OUT">
              <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] [%-5level] (%logger{36}) - %msg%n"/>
            </Console>
          </Appenders>
        </Configuration>
        """;

      Files.writeString(configFilePath, configContent);
      getLog().info("Generated Log4j config file");
    } catch (IOException e) {
      getLog().error("Unable to generate Log4j config", e);
      throw new MojoExecutionException(e);
    }
  }
}
