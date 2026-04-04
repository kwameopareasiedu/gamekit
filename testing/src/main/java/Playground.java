public class Playground {
  public static void main(String[] args) {
    for (Object key : System.getProperties().keySet()) {
      System.out.println(key);
    }
    System.out.println(System.getProperty("os.arch"));
  }
}
