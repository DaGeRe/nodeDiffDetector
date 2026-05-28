package de.dagere.nodeDiffDetector.typeFinding;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.github.javaparser.ast.CompilationUnit;

import de.dagere.nodeDiffDetector.testUtils.TestConstants;
import de.dagere.nodeDiffDetector.typeFinding.FQNDeterminer;
import de.dagere.nodeDiffDetector.utils.JavaParserProvider;

public class TestFQNDeterminer {

   public static final File SOURCE = new File(TestConstants.TEST_RESOURCES, "clazzFinderExample");
   
   @Test
   public void testTypeItself() throws FileNotFoundException {
      File file = new File(SOURCE, "src/main/java/de/dagere/TestMe1.java");
      CompilationUnit unit = JavaParserProvider.parse(file);
      String fqn = FQNDeterminer.getParameterFQN(unit, "TestMe1");
      Assert.assertEquals("de.dagere.TestMe1", fqn);
   }
   
   @Test
   public void testInnerClass() throws FileNotFoundException {
      File file = new File(SOURCE, "src/main/java/de/dagere/TestMe2.java");
      CompilationUnit unit = JavaParserProvider.parse(file);

      String fqnTest = FQNDeterminer.getParameterFQN(unit, "TestMe2");
      Assert.assertEquals("de.dagere.TestMe2", fqnTest);

      String fqnInner = FQNDeterminer.getParameterFQN(unit, "Inner");
      Assert.assertEquals("de.dagere.TestMe2$Inner", fqnInner);

      String fqnInnerInner = FQNDeterminer.getParameterFQN(unit, "InnerInner");
      Assert.assertEquals("de.dagere.TestMe2$Inner$InnerInner", fqnInnerInner);

      String fqnEnum = FQNDeterminer.getParameterFQN(unit, "InnerEnum");
      Assert.assertEquals("de.dagere.TestMe2$InnerEnum", fqnEnum);

      String fqnSecond = FQNDeterminer.getParameterFQN(unit, "Second");
      Assert.assertEquals("de.dagere.Second", fqnSecond);
   }
   
   @Test
   public void testSimpleType() throws FileNotFoundException {
      File file = new File(SOURCE, "src/main/java/de/dagere/TestMe1.java");
      CompilationUnit unit = JavaParserProvider.parse(file);
      String fqn = FQNDeterminer.getParameterFQN(unit, "int");
      Assert.assertEquals("int", fqn);

      String fqn2 = FQNDeterminer.getParameterFQN(unit, "double");
      Assert.assertEquals("double", fqn2);
      
      String simpleTypeFQNArray = FQNDeterminer.getParameterFQN(unit, "double[]");
      Assert.assertEquals("double[]", simpleTypeFQNArray);
   }
   
   @Test
   public void testJavaLangClass() throws FileNotFoundException {
      File file = new File(SOURCE, "src/main/java/de/dagere/TestMe1.java");
      CompilationUnit unit = JavaParserProvider.parse(file);
      String fqn = FQNDeterminer.getParameterFQN(unit, "Object");
      Assert.assertEquals("java.lang.Object", fqn);

      String fqn2 = FQNDeterminer.getParameterFQN(unit, "String");
      Assert.assertEquals("java.lang.String", fqn2);
   }
   
   @Test
   public void testJavaLangGenericClass() throws FileNotFoundException {
      File file = new File(SOURCE, "src/main/java/de/dagere/TestMe1.java");
      CompilationUnit unit = JavaParserProvider.parse(file);
      String fqn = FQNDeterminer.getParameterFQN(unit, "Class");
      Assert.assertEquals("java.lang.Class", fqn);
   }
   
   @Test
   public void testPackageClass() throws FileNotFoundException {
      File file = new File(SOURCE, "src/main/java/de/dagere/TestMe1.java");
      CompilationUnit unit = JavaParserProvider.parse(file);
      String fqn = FQNDeterminer.getParameterFQN(unit, "TestM2");
      Assert.assertEquals("de.dagere.TestM2", fqn);
   }
   
   @Test
   public void testImportedClass() throws FileNotFoundException {
      
      File dependencyReaderFile = new File(SOURCE, "src/main/java/de/dagere/ImportTestExample.java");
      CompilationUnit unit = JavaParserProvider.parse(dependencyReaderFile);
      String fqn = FQNDeterminer.getParameterFQN(unit, "TestCase");
      Assert.assertEquals("junit.framework.TestCase", fqn);

      String fqn2 = FQNDeterminer.getParameterFQN(unit, "CommandLine");
      Assert.assertEquals("picocli.CommandLine", fqn2);
   }
}
