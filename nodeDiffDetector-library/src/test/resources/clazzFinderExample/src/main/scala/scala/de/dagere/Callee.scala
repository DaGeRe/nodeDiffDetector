package scala.de.dagere.peass

class Callee {
  def method1() {
    innerMethod();
  }

  private def innerMethod() {
    Thread.sleep(20);
  }

  protected def method2() {
    // This change should not be detected by PeASS since it is not covered by a test
    println("This is a test");
  }
}