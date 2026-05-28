package de.dagere.nodeDiffDetector.data;

import java.io.File;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class Type implements Comparable<Type> {
   
   protected final String module;
   protected final String javaClazzName;

   public Type(@JsonProperty("clazz") final String clazz, @JsonProperty("module") final String module) {
      if (clazz.contains(File.separator)) {
         throw new RuntimeException("Class should be full qualified name, not path! " + clazz);
      }
      if (clazz.contains(MethodCall.METHOD_SEPARATOR)) {
         throw new RuntimeException("Class must never contain METHOD_SEPARATOR: " + clazz);
      }

      this.module = module != null ? module : "";
      javaClazzName = clazz;

      if (javaClazzName.startsWith(".")) {
         throw new RuntimeException("Type names are not allowed to start with ., but was " + javaClazzName);
      }

      if (javaClazzName.startsWith(".")) {
         throw new RuntimeException("Java class names are not allowed to start with ., but was " + javaClazzName);
      }
   }
   
   @JsonIgnore
   public String getSimpleClazzName() {
      return javaClazzName.substring(javaClazzName.lastIndexOf('.') + 1);
   }
   
   @JsonIgnore
   public String getJavaClazzName() {
      return javaClazzName;
   }
   
   @JsonIgnore
   public String getSimplestClazzName() {
      if (javaClazzName.contains(MethodCall.CLAZZ_SEPARATOR)) {
         return javaClazzName.substring(javaClazzName.lastIndexOf(MethodCall.CLAZZ_SEPARATOR) + 1);
      }
      final String simpleClazz = javaClazzName.substring(javaClazzName.lastIndexOf('.') + 1);
      return simpleClazz;
   }
   
   @JsonIgnore
   public Type getSourceContainingClazz() {
      if (!javaClazzName.contains(MethodCall.CLAZZ_SEPARATOR)) {
         return new Type(javaClazzName, module);
      } else {
         final String clazzName = javaClazzName.substring(0, javaClazzName.indexOf(MethodCall.CLAZZ_SEPARATOR));
         return new Type(clazzName, module);
      }
   }
   
   @JsonIgnore
   public String getPackage() {
      final String result = javaClazzName.contains(".") ? javaClazzName.substring(0, javaClazzName.lastIndexOf('.')) : "";
      return result;
   }
   
   @JsonIgnore
   public boolean isInnerClassCall() {
      return javaClazzName.contains(MethodCall.CLAZZ_SEPARATOR);
   }

   @JsonIgnore
   public String getOuterClass() {
      return javaClazzName.substring(0, javaClazzName.lastIndexOf(MethodCall.CLAZZ_SEPARATOR));
   }
   
   @JsonInclude(Include.NON_EMPTY)
   public String getModule() {
      return module;
   }
   
   @JsonIgnore
   public Type onlyClazz() {
      return new Type(javaClazzName, module);
   }

   public String getClazz() {
      return javaClazzName;
   }
   
   @Override
   public String toString() {
      String result;
      if (module != null && !module.equals("")) {
         result = module + MethodCall.MODULE_SEPARATOR + javaClazzName;
      } else {
         result = javaClazzName;
      }
      return result;
   }
   
   @Override
   public int hashCode() {
      return javaClazzName.hashCode();
   }
   
   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj instanceof Type) {
         final Type other = (Type) obj;
         if (module != null) {
            return other.module.equals(module) && other.javaClazzName.equals(javaClazzName);
         } else {
            return other.javaClazzName.equals(javaClazzName);
         }
      } else {
         return false;
      }
   }
   

   @Override
   public int compareTo(final Type o) {
      final String own = toString();
      final String other = o.toString();
      return own.compareTo(other);
   }
}
