package de.dagere.peass.dependency.analysis.data;

import java.io.IOException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.dagere.nodeDiffDetector.data.MethodCall;
import de.dagere.nodeDiffDetector.data.serialization.MethodCallDeserializer;

public class TestChangedEntity {

   @Test
   public void testAlltogether() {
      MethodCall entity = new MethodCall("de.test.ClazzA#method(int)");
      Assert.assertEquals("de.test.ClazzA", entity.getClazz());
      Assert.assertEquals("method", entity.getMethod());

      MethodCall entityWithModule = new MethodCall("moduleA/submodul§de.test.ClazzA#method(int)");
      Assert.assertEquals("moduleA/submodul", entityWithModule.getModule());
      Assert.assertEquals("de.test.ClazzA", entityWithModule.getClazz());
      Assert.assertEquals("method", entityWithModule.getMethod());
   }

   @Test
   public void testParametersDirectlyClazz() {
      MethodCall entity = new MethodCall("de.ClassA#methodA(de.peass.Test,int,String)", "moduleA");
      System.out.println(entity.getParametersPrintable());
      MatcherAssert.assertThat(entity.getParameters(), Matchers.hasSize(3));
      Assert.assertEquals("methodA", entity.getMethod());
   }

   @Test
   public void testParametersDirectly() {
      MethodCall entity = new MethodCall("de.ClassA", "moduleA", "methodA(de.peass.Test,int,String)");
      System.out.println(entity.getParametersPrintable());
      MatcherAssert.assertThat(entity.getParameters(), Matchers.hasSize(3));
      Assert.assertEquals("methodA", entity.getMethod());
   }

   @Test
   public void testParametersSimple() {
      MethodCall entity = new MethodCall("de.ClassA", "moduleA", "methodA");
      entity.createParameters("de.peass.Test, int, String");
      System.out.println(entity.getParametersPrintable());
      MatcherAssert.assertThat(entity.getParameters(), Matchers.hasSize(3));
   }

   @Test
   public void testParametersGenerics() {
      MethodCall entity = new MethodCall("de.ClassA", "moduleA", "methodA");
      entity.createParameters("Map<String, Map<String, int>>, int, String");
      System.out.println(entity.getParametersPrintable());
      MatcherAssert.assertThat(entity.getParameters(), Matchers.hasSize(3));
   }

   @Test
   public void testParametersDoubleGenerics() {
      MethodCall entity = new MethodCall("de.ClassA", "moduleA", "methodA");
      entity.createParameters("Map<String, Map<String, int>>, Map<String, Map<String, Integer>>, Set<Integer>");
      System.out.println(entity.getParametersPrintable());
      MatcherAssert.assertThat(entity.getParameters(), Matchers.hasSize(3));
   }

   @Test
   public void testParametersTrippleGenerics() {
      MethodCall entity = new MethodCall("de.ClassA", "moduleA", "methodA");
      entity.createParameters("Triple<String, int, String>>, Map<String, Map<String, Integer>>, Set<Integer>");
      System.out.println(entity.getParametersPrintable());
      MatcherAssert.assertThat(entity.getParameters(), Matchers.hasSize(3));
   }

   @Test
   public void testParametersParenthesis() {
      MethodCall entity = new MethodCall("de.ClassA", "moduleA", "methodA");
      entity.createParameters("(Test, int, String)");
      System.out.println(entity.getParametersPrintable());
      MatcherAssert.assertThat(entity.getParameters().get(0), Matchers.not(Matchers.containsString("(")));
      MatcherAssert.assertThat(entity.getParameters().get(2), Matchers.not(Matchers.containsString(")")));
   }

   @Test
   public void testSerialization() throws JsonProcessingException, IOException {
      String entityString = "de.peass.ClassA#methodA";
      MethodCall entity = new MethodCallDeserializer().deserializeKey(entityString, null);
      Assert.assertEquals("de.peass.ClassA", entity.getClazz());
      Assert.assertEquals("methodA", entity.getMethod());
      Assert.assertEquals("", entity.getModule());
      
      String entityStringWithModule = "moduleA§de.peass.ClassA#methodA";
      MethodCall entityWithModule = new MethodCallDeserializer().deserializeKey(entityStringWithModule, null);
      Assert.assertEquals("de.peass.ClassA", entityWithModule.getClazz());
      Assert.assertEquals("methodA", entityWithModule.getMethod());
      Assert.assertEquals("moduleA", entityWithModule.getModule());
   }
}
