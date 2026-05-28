package de.dagere.nodeDiffDetector.sourceReading;

import java.io.ByteArrayInputStream;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;

import de.dagere.nodeDiffDetector.data.MethodCall;
import de.dagere.nodeDiffDetector.sourceReading.ParameterComparator;

public class TestParameterComparator {

   @Test
   public void testNoParameterComparison() {
      String methodSource = "class Clazz{ class MyInner{ public void myMethod(){} } }";
      CompilationUnit declaration = new JavaParser().parse(new ByteArrayInputStream(methodSource.getBytes())).getResult().get();

      ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) declaration.getChildNodes().get(0);
      ClassOrInterfaceDeclaration myInner = clazz.findAll(ClassOrInterfaceDeclaration.class).get(1);
      CallableDeclaration<?> method = myInner.findAll(CallableDeclaration.class).get(0);

      MethodCall traceElementMethod = new MethodCall("Clazz$MyInner", "", "myMethod");
      boolean isEqualMethod = new ParameterComparator(clazz).parametersEqual(traceElementMethod, method);
      Assert.assertTrue(isEqualMethod);
   }

   @Test
   public void testSimpleConstructor() {
      String methodSource = "class Clazz{ class MyInner{ public MyInner(){} } }";
      CompilationUnit declaration = new JavaParser().parse(new ByteArrayInputStream(methodSource.getBytes())).getResult().get();

      ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) declaration.getChildNodes().get(0);
      ClassOrInterfaceDeclaration myInner = clazz.findAll(ClassOrInterfaceDeclaration.class).get(1);
      CallableDeclaration<?> method = myInner.findAll(CallableDeclaration.class).get(0);

      MethodCall traceElementConstructorWrong = new MethodCall("Clazz$MyInner", "", "<init>");
      traceElementConstructorWrong.getParameters().add("Clazz");
      boolean isEqualConstructorWrong = new ParameterComparator(clazz).parametersEqual(traceElementConstructorWrong, method);
      Assert.assertTrue(isEqualConstructorWrong);
   }

   @Test
   public void testStaticInnerConstructor() {
      String methodSource = "class Clazz{ static class MyInner{ public MyInner(int a){} } }";
      CompilationUnit declaration = new JavaParser().parse(new ByteArrayInputStream(methodSource.getBytes())).getResult().get();

      ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) declaration.getChildNodes().get(0);
      ClassOrInterfaceDeclaration myInner = clazz.findAll(ClassOrInterfaceDeclaration.class).get(1);
      CallableDeclaration<?> method = myInner.findAll(CallableDeclaration.class).get(0);

      MethodCall traceElementConstructorWrong = new MethodCall("Clazz$MyInner", "", "<init>");
      boolean isEqualConstructor = new ParameterComparator(clazz).parametersEqual(traceElementConstructorWrong, method);
      Assert.assertFalse(isEqualConstructor);
   }

   @Test
   public void testStaticComparison() {
      String methodSource = "class Clazz{ class MyInner{ public static void myMethod(int i){} } }";
      CompilationUnit declaration = new JavaParser().parse(new ByteArrayInputStream(methodSource.getBytes())).getResult().get();

      ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) declaration.getChildNodes().get(0);
      ClassOrInterfaceDeclaration innerClass = clazz.findAll(ClassOrInterfaceDeclaration.class).get(1);
      CallableDeclaration<?> method = innerClass.findAll(CallableDeclaration.class).get(0);

      MethodCall wrongTraceElement = new MethodCall("Clazz$MyInner", "", "myMethod");
      boolean isEqualWrong = new ParameterComparator(clazz).parametersEqual(wrongTraceElement, method);
      Assert.assertFalse(isEqualWrong);

      MethodCall correctTraceElement = new MethodCall("Clazz$MyInner", "", "myMethod");
      correctTraceElement.getParameters().add("int");
      boolean isEqualCorrect = new ParameterComparator(clazz).parametersEqual(correctTraceElement, method);
      Assert.assertTrue(isEqualCorrect);
   }

   @Test
   public void testDoubleInnerComparison() {
      MethodCall traceElement = new MethodCall("Clazz$MyInner$SecondInner", "", "<init>");
      traceElement.getParameters().add("Clazz$MyInner");
      traceElement.getParameters().add("int");
      String methodSource = "class Clazz{ class MyInner{ class SecondInner { public SecondInner(int i){} } } } ";
      CompilationUnit declaration = new JavaParser().parse(new ByteArrayInputStream(methodSource.getBytes())).getResult().get();

      ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) declaration.getChildNodes().get(0);
      ClassOrInterfaceDeclaration myInner = clazz.findAll(ClassOrInterfaceDeclaration.class).get(1);
      ClassOrInterfaceDeclaration secondInnerClass = myInner.findAll(ClassOrInterfaceDeclaration.class).get(1);
      CallableDeclaration<?> method = secondInnerClass.findAll(CallableDeclaration.class).get(0);

      boolean isEqual1 = new ParameterComparator(clazz).parametersEqual(traceElement, method);
      Assert.assertTrue(isEqual1);
   }

   @Test
   public void testWrongConstructorComparison() {
      String methodSource = "class Clazz{ class MyInner{ public MyInner(){ } } }";
      CompilationUnit declaration = new JavaParser().parse(new ByteArrayInputStream(methodSource.getBytes())).getResult().get();

      ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) declaration.getChildNodes().get(0);
      ClassOrInterfaceDeclaration myInner = clazz.findAll(ClassOrInterfaceDeclaration.class).get(1);
      CallableDeclaration<?> method = myInner.findAll(CallableDeclaration.class).get(0);

      MethodCall traceElementConstructorWrong = new MethodCall("Clazz$MyInner", "", "<init>");
      traceElementConstructorWrong.getParameters().add("Clazz");
      traceElementConstructorWrong.getParameters().add("int");
      boolean isEqualConstructorWrong = new ParameterComparator(clazz).parametersEqual(traceElementConstructorWrong, method);
      Assert.assertFalse(isEqualConstructorWrong);
   }

   @Test
   public void testWrongMethodComparison() {
      String methodSource = "class Clazz{ class MyInner{ public void doStuff(){ } } }";
      CompilationUnit declaration = new JavaParser().parse(new ByteArrayInputStream(methodSource.getBytes())).getResult().get();

      ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) declaration.getChildNodes().get(0);
      ClassOrInterfaceDeclaration myInner = clazz.findAll(ClassOrInterfaceDeclaration.class).get(1);
      CallableDeclaration<?> method = myInner.findAll(CallableDeclaration.class).get(0);

      MethodCall traceElementMethodWrong = new MethodCall("Clazz$MyInner", "", "doStuff");
      traceElementMethodWrong.getParameters().add("Clazz");
      traceElementMethodWrong.getParameters().add("int");
      boolean isEqualMethodWrong = new ParameterComparator(clazz).parametersEqual(traceElementMethodWrong, method);
      Assert.assertFalse(isEqualMethodWrong);
   }

   @Test
   public void testEnumMethodComparison() {
      String methodSource = "class Clazz{ enum MyInner{ A, B; public void doStuff(int a){ } } }";
      CompilationUnit declaration = new JavaParser().parse(new ByteArrayInputStream(methodSource.getBytes())).getResult().get();

      System.out.println(declaration);

      ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) declaration.getChildNodes().get(0);
      EnumDeclaration myInner = clazz.findAll(EnumDeclaration.class).get(0);
      CallableDeclaration<?> method = myInner.findAll(CallableDeclaration.class).get(0);

      MethodCall traceElementMethodWrong = new MethodCall("Clazz$MyInner", "", "doStuff");
      traceElementMethodWrong.getParameters().add("int");
      boolean isEqualMethodWrong = new ParameterComparator(clazz).parametersEqual(traceElementMethodWrong, method);
      Assert.assertTrue(isEqualMethodWrong);
   }
}
