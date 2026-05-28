package de.dagere.nodeDiffDetector.data;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MethodCall extends Type {

   private static final Logger LOG = LogManager.getLogger(MethodCall.class);

   public static final String MODULE_SEPARATOR = "§";
   public static final String METHOD_SEPARATOR = "#";
   public static final String CLAZZ_SEPARATOR = "$";

   private final String method;
   private final List<String> parameters = new LinkedList<String>();

   @JsonCreator
   public MethodCall(@JsonProperty("clazz") final String clazz, @JsonProperty("module") final String module, @JsonProperty("method") final String testMethodName) {
      super(clazz, module);

      if (testMethodName != null && (testMethodName.contains("(") && testMethodName.contains(")"))) {
         String parameterString = testMethodName.substring(testMethodName.indexOf("(") + 1, testMethodName.length() - 1).replaceAll(" ", "");
         createParameters(parameterString);
         method = testMethodName.substring(0, testMethodName.indexOf("("));
      } else {
         method = testMethodName;
      }
      
      if (method == null) {
         throw new RuntimeException("Method is not allowed to be null!");
      }
   }

   @JsonIgnore
   public String getSimpleFullName() {
      return javaClazzName.substring(javaClazzName.lastIndexOf('.') + 1) + METHOD_SEPARATOR + method;
   }

   public String getMethod() {
      return method;
   }

   @JsonIgnore
   public String getParameterString() {
      return MethodCallHelper.getParameterString(parameters.toArray(new String[0]));
   }

   @Override
   public String toString() {
      String result;
      if (module != null && !module.equals("")) {
         result = module + MODULE_SEPARATOR + javaClazzName;
      } else {
         result = javaClazzName;
      }
      if (method != null && !"".equals(method)) {
         result += METHOD_SEPARATOR + method;
      }
      if (parameters.size() > 0) {
         result += MethodCallHelper.getParameterString(parameters.toArray(new String[0]));
      }
      return result;
   }

   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj instanceof MethodCall) {
         final MethodCall other = (MethodCall) obj;
         if (method != null) {
            if (other.method == null) {
               return false;
            }
            if (!other.method.equals(method)) {
               return false;
            }
         } else {
            if (other.method != null) {
               return false;
            }
         }
         if (module != null) {
            return other.module.equals(module) && other.javaClazzName.equals(javaClazzName);
         } else {
            return other.javaClazzName.equals(javaClazzName);
         }
      } else {
         return false;
      }
   }

   public MethodCall copy() {
      final MethodCall copy = new MethodCall(javaClazzName, module, method);
      copy.createParameters(getParameterString());
      return copy;
   }

   public List<String> getParameters() {
      return parameters;
   }

   @JsonIgnore
   public String getParametersPrintable() {
      String result = "";
      for (String parameter : parameters) {
         result += parameter + "_";
      }
      return result.length() > 0 ? result.substring(0, result.length() - 1) : "";
   }

   public void createParameters(final String parameterString) {
      LOG.trace("Creating parameters: {}", parameterString); // TODO trace
      String cleanParameters = parameterString.replaceAll(" ", "").replaceAll("\\(", "").replaceAll("\\)", "");
      if (parameterString.contains("<")) {
         addParameterWithGenerics(cleanParameters);
      } else {
         addGenericFreePart(cleanParameters);
      }
      LOG.trace("Parameters parsed: {}", parameters); // TODO trace
   }

   private void addParameterWithGenerics(final String parameterString) {
      final String[] genericSplitted = parameterString.split(">");
      for (String genericPart : genericSplitted) {
         if (genericPart.length() > 0) {
            if (genericPart.startsWith(",")) {
               genericPart = genericPart.substring(1);
            }
            if (genericPart.contains("<")) {
               final String beforeGeneric = genericPart.substring(0, genericPart.indexOf('<'));
               final String[] beforeGenericEnding = beforeGeneric.split(",");
               if (beforeGenericEnding.length > 1) {
                  for (int i = 0; i < beforeGenericEnding.length - 1; i++) {
                     this.parameters.add(beforeGenericEnding[i]);
                  }
               }
               String genericParameter = beforeGenericEnding[beforeGenericEnding.length - 1] + genericPart.substring(genericPart.indexOf('<')) + '>';
               this.parameters.add(genericParameter);
            } else {
               addGenericFreePart(genericPart);
            }
         } else {
            String lastParameter = this.parameters.get(this.parameters.size() - 1);
            lastParameter += ">";
            this.parameters.set(this.parameters.size() - 1, lastParameter);
         }
      }
   }

   private void addGenericFreePart(final String parameterString) {
      if (parameterString.length() == 0) {
         this.parameters.clear();
      } else {
         final String[] parameters = parameterString.split(",");
         for (final String parameter : parameters) {
            // int dotIndex = parameter.lastIndexOf('.');
            // if (dotIndex != -1) {
            // this.parameters.add(parameter.substring(dotIndex + 1));
            // } else {
            this.parameters.add(parameter);
            // }
         }
      }
   }

   @JsonIgnore
   public String[] getParameterTypes() {
      return parameters.toArray(new String[0]);
   }

   @JsonIgnore
   public void addParameters(String... parameters) {
      for (String parameter : parameters) {
         this.parameters.add(parameter);
      }
   }

   public static MethodCall createMethodCallFromString(String fullName) {
      final int methodIndex = fullName.lastIndexOf(MethodCall.METHOD_SEPARATOR);
      if (methodIndex == -1) {
         throw new RuntimeException("Expecting String to contain METHOD_SEPARATOR, but was " + fullName);
      }

      String javaClazzName = fullName.substring(0, methodIndex);
      String method = fullName.substring(methodIndex + 1);

      String paramString, module;
      if (fullName.contains("(")) {
         method = fullName.substring(methodIndex + 1, fullName.indexOf("("));
         paramString = fullName.substring(fullName.indexOf("(") + 1, fullName.length() - 1);
      } else {
         method = fullName.substring(methodIndex + 1);
         paramString = null;
      }

      int moduleIndex = fullName.indexOf(MethodCall.MODULE_SEPARATOR);
      if (moduleIndex == -1) {
         module = "";
      } else {
         module = fullName.substring(0, moduleIndex);
         javaClazzName = fullName.substring(moduleIndex + 1, methodIndex);
      }

      if (javaClazzName.contains(File.separator)) {
         throw new RuntimeException("Class should be full qualified name, not path! " + fullName);
      }
      
      MethodCall methodCall = new MethodCall(javaClazzName, module, method);
      if (paramString != null) {
         methodCall.createParameters(paramString);
      }
      return methodCall;
   }

   public static Type createFromString(final String fullName) {
      String module, javaClazzName, method = null, paramString = null;

      int moduleIndex = fullName.indexOf(MethodCall.MODULE_SEPARATOR);
      if (moduleIndex == -1) {
         module = "";
         if (fullName.contains(File.separator)) {
            throw new RuntimeException("Class should be full qualified name, not path! " + fullName);
         }
         final int methodIndex = fullName.lastIndexOf(MethodCall.METHOD_SEPARATOR);
         if (methodIndex == -1) {
            javaClazzName = fullName;
            method = null;
         } else {
            javaClazzName = fullName.substring(0, methodIndex);
            method = fullName.substring(methodIndex + 1);

            if (fullName.contains("(")) {
               method = fullName.substring(methodIndex + 1, fullName.indexOf("("));
               paramString = fullName.substring(fullName.indexOf("(") + 1, fullName.length() - 1);
            } else {
               method = fullName.substring(methodIndex + 1);
            }
         }
      } else {
         module = fullName.substring(0, moduleIndex);
         String end = fullName.substring(moduleIndex + 1);
         final int methodIndex = end.lastIndexOf(MethodCall.METHOD_SEPARATOR);
         if (methodIndex == -1) {
            javaClazzName = end;
            method = null;
         } else {
            javaClazzName = end.substring(0, methodIndex);
            method = end.substring(methodIndex + 1);

            if (end.contains("(")) {
               method = end.substring(methodIndex + 1, end.indexOf("("));
               paramString = end.substring(end.indexOf("(") + 1, end.length() - 1);
            } else {
               method = end.substring(methodIndex + 1);
            }
         }
      }

      if (javaClazzName.startsWith(".")) {
         throw new RuntimeException("Java class names are not allowed to start with ., but was " + javaClazzName);
      }

      if (method != null) {
         MethodCall call = new MethodCall(javaClazzName, module, method);
         if (paramString != null) {
            call.createParameters(paramString);
         }
         return call;
      } else {
         Type type = new Type(javaClazzName, module);
         return type;
      }
   }

}