package de.dagere.nodeDiffDetector.typeFinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.dagere.nodeDiffDetector.config.FolderConfig;
import de.dagere.nodeDiffDetector.data.MethodCall;
import de.dagere.nodeDiffDetector.data.Type;
import de.dagere.nodeDiffDetector.sourceReading.MethodReader;
import de.dagere.nodeDiffDetector.testUtils.TestConstants;
import de.dagere.nodeDiffDetector.typeFinding.TypeFileFinder;

public class TestTypeFileFinder {
   
   private static final File SOURCE = new File("src/test/resources/clazzFinderExample/");
   
   @Test
   public void testClasses() {
      List<String> clazzes = new TypeFileFinder(TestConstants.DEFAULT_FOLDERS).getTypes(SOURCE);
      
      System.out.println(clazzes);
      
      MatcherAssert.assertThat(clazzes, Matchers.hasItem("de.dagere.TestMe1"));
      MatcherAssert.assertThat(clazzes, Matchers.hasItem("de.dagere.TestMe2"));
      MatcherAssert.assertThat(clazzes, Matchers.hasItem("de.dagere.Second"));
      MatcherAssert.assertThat(clazzes, Matchers.hasItem("de.dagere.TestMe2$Inner"));
      MatcherAssert.assertThat(clazzes, Matchers.hasItem("de.dagere.TestMe2$Inner$InnerInner"));
      MatcherAssert.assertThat(clazzes, Matchers.hasItem("de.dagere.LocalClass"));
      
      MatcherAssert.assertThat(clazzes, Matchers.hasItem("de.dagere.TestMe2$InnerEnum"));
   }
   
   @Test
   public void testScalaClasses() {
      FolderConfig config = Mockito.mock(FolderConfig.class);
      Mockito.when(config.getClazzFolders()).thenReturn(Arrays.asList("src/main/scala"));
      
      List<String> clazzes = new TypeFileFinder(config).getTypes(SOURCE);
      
      System.out.println(clazzes);
      
      MatcherAssert.assertThat(clazzes, Matchers.hasItem("scala.de.dagere.Callee"));
      MatcherAssert.assertThat(clazzes, Matchers.hasItem("scala.de.dagere.ExampleClazz"));
   }
   
   @Test
   public void testGetSourceFile() throws FileNotFoundException {
      File sourceFileClass = new TypeFileFinder(TestConstants.DEFAULT_FOLDERS).getSourceFile(SOURCE, new Type("de.dagere.LocalClass", ""));
      Assert.assertNotNull(sourceFileClass);
      
      File sourceFileEnum = new TypeFileFinder(TestConstants.DEFAULT_FOLDERS).getSourceFile(SOURCE, new Type("de.dagere.LocalEnum", ""));
      Assert.assertNotNull(sourceFileEnum);
      
      File sourceFileInterface = new TypeFileFinder(TestConstants.DEFAULT_FOLDERS).getSourceFile(SOURCE, new Type("de.dagere.LocalInterface", ""));
      Assert.assertNotNull(sourceFileInterface);
      
      MethodCall exampleEntity = MethodCall.createMethodCallFromString("de.dagere.LocalClass#myMethod(int)");
      String text = MethodReader.getMethodSource(SOURCE, exampleEntity, TestConstants.DEFAULT_FOLDERS);
      MatcherAssert.assertThat(text, Matchers.containsString("this.i = i;"));
   }
}
