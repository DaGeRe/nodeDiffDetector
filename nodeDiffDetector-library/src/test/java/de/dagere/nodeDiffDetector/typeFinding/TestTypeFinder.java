package de.dagere.nodeDiffDetector.typeFinding;

import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.TypeDeclaration;

import de.dagere.nodeDiffDetector.data.Type;

public class TestTypeFinder {
   
   @Test
   public void testFindClazz() {
      String test = "class A{ }";
      JavaParser parser = new JavaParser();
      List<Node> parsed = parser.parse(test).getResult().get().getChildNodes();
      TypeDeclaration<?> clazz = TypeFinder.findClazz(new Type("A", ""), parsed);
      Assert.assertNotNull(clazz);
   }
   
   @Test
   public void testFindInnerClazz() {
      String test = "class A{ class B{ } }";
      JavaParser parser = new JavaParser();
      List<Node> parsed = parser.parse(test).getResult().get().getChildNodes();
      TypeDeclaration<?> clazz = TypeFinder.findClazz(new Type("A$B", ""), parsed);
      Assert.assertNotNull(clazz);
   }
   
   @Test
   public void testFindInnerInnerClazz() {
      String test = "class A{ class B{ class C{ } } }";
      JavaParser parser = new JavaParser();
      List<Node> parsed = parser.parse(test).getResult().get().getChildNodes();
      TypeDeclaration<?> clazz = TypeFinder.findClazz(new Type("A$B$C", ""), parsed);
      Assert.assertNotNull(clazz);
   }
}
