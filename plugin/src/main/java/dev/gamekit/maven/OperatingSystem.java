package dev.gamekit.maven;

public enum OperatingSystem {
  LINUX("linux"), WINDOWS("windows"), MAC("macos"), UNSUPPORTED("N/a");

  public final String name;

  OperatingSystem(String name) {
    this.name = name;
  }

  public static OperatingSystem getCurrent() {
    String prop = System.getProperty("os.name").toLowerCase();

    if (prop.contains("nix") || prop.contains("nux") || prop.contains("aix")) return LINUX;
    if (prop.contains("win")) return WINDOWS;
    if (prop.contains("mac")) return MAC;
    return UNSUPPORTED;
  }
}
