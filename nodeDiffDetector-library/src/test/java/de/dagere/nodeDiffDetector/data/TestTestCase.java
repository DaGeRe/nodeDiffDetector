package de.dagere.nodeDiffDetector.data;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class TestTestCase {
   @Test
   public void testPackageGetting() {
      String packageName = new TestMethodCall("my.package.Clazz", "method").getPackage();
      Assert.assertEquals("my.package", packageName);
   }
}
