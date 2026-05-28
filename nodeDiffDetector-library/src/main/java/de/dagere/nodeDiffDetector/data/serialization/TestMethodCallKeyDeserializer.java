package de.dagere.nodeDiffDetector.data.serialization;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import de.dagere.nodeDiffDetector.data.TestMethodCall;

public class TestMethodCallKeyDeserializer extends KeyDeserializer {

   @Override
   public Object deserializeKey(final String key, final DeserializationContext ctxt) throws IOException {
      return TestMethodCall.createFromString(key);
   }

}
