package de.dagere.peass;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import de.dagere.peass.dependency.analysis.data.ChangedEntity;
import de.dagere.peass.dependency.changesreading.JavaParserProvider;
import de.dagere.peass.dependency.changesreading.SourceReadUtils;

public class TestSourceReading {

   private static final File baseFolder = new File(TestConstants.TEST_RESOURCES, "methodFinding");

   @Test
   public void testGenericClass() throws FileNotFoundException {
      final CompilationUnit cu = JavaParserProvider.parse(new File(baseFolder, "GenericClassExample.java"));

      final ChangedEntity exampleTrace = new ChangedEntity("GenericClassExample", null, "test1");
      final Node exampleMethod = SourceReadUtils.getMethod(exampleTrace, cu);

      Assert.assertNotNull(exampleMethod);

      final ChangedEntity genericMethod = new ChangedEntity("GenericClassExample", null, "myMethod");
      genericMethod.addParameters("Comparable");
      final Node genericMethodNode = SourceReadUtils.getMethod(genericMethod, cu);

      Assert.assertNotNull(genericMethodNode);
   }

   @Test
   public void testGenericClass2() throws FileNotFoundException {
      final CompilationUnit cu = JavaParserProvider.parse(new File(baseFolder, "GenericsExample.java"));

      final ChangedEntity exampleTrace = new ChangedEntity("GenericsExample", null, "test1");
      exampleTrace.addParameters("Map<ClassA, ClassB>", "TimeRange");
      final Node exampleMethod = SourceReadUtils.getMethod(exampleTrace, cu);

      Assert.assertNotNull(exampleMethod);

   }

   @Test
   public void testAnonymousClass() throws FileNotFoundException {
      final CompilationUnit cu = JavaParserProvider.parse(new File(baseFolder, "AnonymousClassExample.java"));

      final ChangedEntity anonymousTrace = new ChangedEntity("AnonymousClassExample$1", null, "run");
      final Node anonymousMethod = SourceReadUtils.getMethod(anonymousTrace, cu);

      Assert.assertNotNull(anonymousMethod);

      final ChangedEntity elementConstuctor = new ChangedEntity("AnonymousClassExample$MyPrivateClass", null, "<init>");
      final Node methodConstructor = SourceReadUtils.getMethod(elementConstuctor, cu);

      Assert.assertNotNull(methodConstructor);

      final ChangedEntity elementInnerMethod = new ChangedEntity("AnonymousClassExample$MyPrivateClass", null, "doSomething");
      final Node innerMethod = SourceReadUtils.getMethod(elementInnerMethod, cu);

      Assert.assertNotNull(innerMethod);
   }

   @Test
   public void testInnerConstructor() throws FileNotFoundException {
      final CompilationUnit cu = JavaParserProvider.parse(new File(baseFolder, "AnonymousClassExample.java"));

      final ChangedEntity anonymousTrace = new ChangedEntity("AnonymousClassExample$MyPrivateClass", null, "<init>");
      anonymousTrace.addParameters("int");
      final Node anonymousMethod = SourceReadUtils.getMethod(anonymousTrace, cu);

      System.out.println(anonymousMethod);

      Assert.assertNotNull(anonymousMethod);
   }

   @Test
   public void testParameters() throws FileNotFoundException {
      final CompilationUnit cu = JavaParserProvider.parse(new File(baseFolder, "/AnonymousClassExample.java"));

      final ChangedEntity te = new ChangedEntity("AnonymousClassExample", null, "parameterMethod");
      te.addParameters("int");
      final Node anonymousMethod = SourceReadUtils.getMethod(te, cu);
      Assert.assertNotNull(anonymousMethod);

      final ChangedEntity te2 = new ChangedEntity("AnonymousClassExample", null, "parameterMethod");
      te2.addParameters("String");
      final Node anonymousMethod2 = SourceReadUtils.getMethod(te2, cu);
      Assert.assertNotNull(anonymousMethod2);

      final ChangedEntity te3 = new ChangedEntity("AnonymousClassExample", null, "parameterMethod");
      te3.addParameters("Long");
      final Node anonymousMethod3 = SourceReadUtils.getMethod(te3, cu);
      Assert.assertNull(anonymousMethod3);

      final ChangedEntity teSmall = new ChangedEntity("AnonymousClassExample", null, "parameterMethod");
      final Node anonymousMethodSmall = SourceReadUtils.getMethod(teSmall, cu);
      Assert.assertNull(anonymousMethodSmall);
   }

   @Test
   public void testVarArgs() throws FileNotFoundException {
      final CompilationUnit cu = JavaParserProvider.parse(new File(baseFolder, "AnonymousClassExample.java"));

      final ChangedEntity teVarArg = new ChangedEntity("AnonymousClassExample", null, "parameterMethod");
      teVarArg.addParameters("Object", "String");
      final Node anonymousMethodVarArg = SourceReadUtils.getMethod(teVarArg, cu);
      Assert.assertNotNull(anonymousMethodVarArg);

      final ChangedEntity teVarArg2 = new ChangedEntity("AnonymousClassExample", null, "parameterMethod");
      teVarArg2.addParameters("Object", "String", "String");
      final Node anonymousMethodVarArg2 = SourceReadUtils.getMethod(teVarArg2, cu);
      Assert.assertNotNull(anonymousMethodVarArg2);

      final ChangedEntity teVarArg3 = new ChangedEntity("AnonymousClassExample", null, "parameterMethod");
      teVarArg3.addParameters("Object");
      final Node anonymousMethodVarArg3 = SourceReadUtils.getMethod(teVarArg3, cu);
      Assert.assertNotNull(anonymousMethodVarArg3);

      final ChangedEntity teVarArg4 = new ChangedEntity("AnonymousClassExample", null, "parameterMethod");
      teVarArg4.addParameters("Object", "String", "String", "String");
      final Node anonymousMethodVarArg4 = SourceReadUtils.getMethod(teVarArg4, cu);
      Assert.assertNotNull(anonymousMethodVarArg4);

      final ChangedEntity teVarArgWrong = new ChangedEntity("AnonymousClassExample", null, "parameterMethod");
      teVarArgWrong.addParameters("Object", "Long");
      final Node anonymousMethodVarArgWrong = SourceReadUtils.getMethod(teVarArgWrong, cu);
      Assert.assertNull(anonymousMethodVarArgWrong);

      final ChangedEntity teVarArgWrong2 = new ChangedEntity("AnonymousClassExample", null, "parameterMethod");
      teVarArgWrong2.addParameters("Object", "String", "Long");
      final Node anonymousMethodVarArgWrong2 = SourceReadUtils.getMethod(teVarArgWrong2, cu);
      Assert.assertNull(anonymousMethodVarArgWrong2);
   }
}
