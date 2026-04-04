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
import org.zeroturnaround.zip.ZipUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributeView;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

/**
 * {@link BuildMojo} is a mojo for building a gamekit application into an executable for the current platform.
 * <p>
 * It first creates a fat/uber JAR file of the project files, then uses Java's jpackage to build a platform-dependent
 * executable.
 */
@Mojo(name = "build", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class BuildMojo extends AbstractMojo {
  /** The fully qualified path of the class contain the {@code main()} method */
  @Parameter(name = "mainClass", required = true)
  private String mainClass;

  /** The display name of the built executable */
  @Parameter(name = "jarOnly", defaultValue = "false")
  private boolean jarOnly;

  /** The vendor name */
  @Parameter(name = "vendor")
  private String vendor;

  /** The application description */
  @Parameter(name = "description")
  private String description;

  /**
   * Path to the resources directory for packaging. Refer to the docs for
   * <a href="https://docs.oracle.com/en/java/javase/17/jpackage/override-jpackage-resources.html">
   * overriding jpackage resources</a> for more information
   */
  @Parameter(name = "resourceDir")
  private String resourceDir;

  @Component
  private MavenProject mavenProject;

  @Component
  private MavenSession mavenSession;

  @Component
  private BuildPluginManager pluginManager;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    OS os = OS.getCurrent();

    if (os == OS.UNSUPPORTED)
      throw new MojoExecutionException("Unsupported operating system");

    getLog().info("Generating uber JAR with all dependencies");

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
              element(name("Multi-Release"), "true")
            )
          )
        ),
        element(name("createDependencyReducedPom"), "false")
      ),
      executionEnvironment(
        mavenProject,
        mavenSession,
        pluginManager
      )
    );

    getLog().info("Creating output directories");

    String name = mavenProject.getArtifactId();
    String jarFileName = String.format("%s-%s.jar", name, mavenProject.getVersion());
    Path jarFilePath = Paths.get(mavenProject.getBuild().getDirectory(), jarFileName);
    Path jarFileDestPath = Paths.get(mavenProject.getBuild().getDirectory(), "outputs", "jar", jarFileName);
    Path platformExecutablePath = Paths.get(mavenProject.getBuild().getDirectory(), "outputs", os.name);

    try {
      Files.createDirectories(jarFileDestPath.getParent());
      Files.createDirectories(platformExecutablePath);
      Files.copy(jarFilePath, jarFileDestPath);
    } catch (IOException e) {
      getLog().error("Unable to create output directories", e);
      throw new MojoExecutionException(e);
    }

    if (jarOnly) {
      getLog().info("JAR output generated at " + Paths.get(mavenProject.getBuild().getDirectory(), "outputs", "jar"));
      return;
    }

    getLog().info("Building platform executable: " + os.name);

    executeMojo(
      plugin(
        groupId("com.github.akman"),
        artifactId("jpackage-maven-plugin"),
        version("0.1.5")
      ),
      goal("jpackage"),
      configuration(
        element(name("type"), "IMAGE"),
        element(name("name"), name),
        element(name("vendor"), vendor),
        element(name("description"), description),
        element(name("appversion"), mavenProject.getVersion()),
        element(name("input"), jarFileDestPath.getParent().toString()),
        element(name("dest"), platformExecutablePath.toString()),
        element(name("mainjar"), jarFileName),
        element(name("mainclass"), mainClass),
        element(name("resourcedir"), resourceDir)
      ),
      executionEnvironment(
        mavenProject,
        mavenSession,
        pluginManager
      )
    );

    getLog().info("Generating README");

    Path platformOutputRootPath = Paths.get(platformExecutablePath.toString(), name);
    Path readmeFilePath = Paths.get(platformExecutablePath.toString(), name, "README.txt");
    StringBuilder instructionsBuilder = new StringBuilder(name + "\n");

    switch (os) {
      case LINUX -> {
        instructionsBuilder.append("\n").append("Navigate to the bin/ directory");
        instructionsBuilder.append("\n").append("Open the bin/ directory in a terminal");
        instructionsBuilder.append("\n").append("Type 'chmod u+x ")
          .append(name).append("' to make the file " + "executable");
        instructionsBuilder.append("\n").append("Close the terminal and open the file to launch the game");
      }
      case WINDOWS -> instructionsBuilder.append("\n").append("Launch the ").append(name).append(".exe file");
    }

    try {
      Files.writeString(readmeFilePath, instructionsBuilder.toString());
    } catch (IOException e) {
      getLog().error("Unable to generate README.txt file", e);
      throw new MojoExecutionException(e);
    }

    getLog().info("Zipping files");
    Path zippedOutputPath = Paths.get(platformExecutablePath.toString(), name + ".zip");
    ZipUtil.pack(readmeFilePath.getParent().toFile(), zippedOutputPath.toFile());

    getLog().info("Cleaning up");

    try {
      try (Stream<Path> entries = Files.walk(platformOutputRootPath)) {
        List<Path> reversed = new java.util.ArrayList<>(entries.toList());
        Collections.reverse(reversed);

        for (Path entry : reversed) {
          if (os == OS.WINDOWS) {
            var attrs = Files.getFileAttributeView(entry, DosFileAttributeView.class);
            if (attrs != null) attrs.setReadOnly(false);
          }

          Files.delete(entry);
        }
      }
    } catch (IOException e) {
      getLog().error("Unable to delete platform output directory", e);
    }

    getLog().info("Outputs generated at " + Paths.get(mavenProject.getBuild().getDirectory(), "outputs"));
  }
}
