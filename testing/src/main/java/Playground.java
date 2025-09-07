import dev.gamekit.core.IO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class Playground {
  private static final Logger LOGGER = LogManager.getLogger();

  public static void main(String[] args) {
    IO.writeFile("testy/write-test.txt", "My name is GameKits!", true);

    try (BufferedReader bf = new BufferedReader(
      new InputStreamReader(IO.getFileStream("./README.md"))
    )) {
      LOGGER.debug("File content: {}", bf.lines().collect(Collectors.joining()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
