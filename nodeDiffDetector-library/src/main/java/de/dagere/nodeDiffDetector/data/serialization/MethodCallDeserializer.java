package de.dagere.nodeDiffDetector.data.serialization;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import de.dagere.nodeDiffDetector.data.MethodCall;
import de.dagere.nodeDiffDetector.data.Type;

public class MethodCallDeserializer extends KeyDeserializer {

   public MethodCallDeserializer() {
   }

   @Override
   public Type deserializeKey(final String key, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
      String value = key;
      final Type entity = MethodCall.createFromString(value);
      return entity;
   }
}
