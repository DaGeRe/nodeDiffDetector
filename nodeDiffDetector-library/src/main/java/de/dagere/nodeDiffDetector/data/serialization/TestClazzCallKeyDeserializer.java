package de.dagere.nodeDiffDetector.data.serialization;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import de.dagere.nodeDiffDetector.data.MethodCall;
import de.dagere.nodeDiffDetector.data.TestClazzCall;

public class TestClazzCallKeyDeserializer extends KeyDeserializer {

   @Override
   public Object deserializeKey(final String key, final DeserializationContext ctxt) throws IOException {
      if (key.contains(MethodCall.MODULE_SEPARATOR)) {
         String module = key.substring(0, key.indexOf(MethodCall.MODULE_SEPARATOR));
         String clazz = key.substring(key.indexOf(MethodCall.MODULE_SEPARATOR) + 1, key.length());
         return new TestClazzCall(clazz, module);
      } else {
         return new TestClazzCall(key);
      }

   }

}
