import java.util.ArrayList;
import java.util.List;

public class ScratchPad {
  public static void main(String[] args) {
    Test a = new Test();
    Test b = new Test();
    Test c = new Test();

    List<Test> tests = new ArrayList<>();
    tests.add(a);
    tests.add(b);

    System.out.println(tests.indexOf(a));
    System.out.println(tests.indexOf(b));
    tests.set(0, c);
    System.out.println(tests.indexOf(a));
    System.out.println(tests.indexOf(b));
    System.out.println(tests.indexOf(c));
  }

  static class Test {}
}
