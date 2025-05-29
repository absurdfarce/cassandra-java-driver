/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datastax.oss.driver.api.core.tracker;

import com.datastax.oss.driver.api.core.cql.Statement;
import com.datastax.oss.driver.api.core.session.Request;
import com.datastax.oss.protocol.internal.util.collection.NullAllowingImmutableMap;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public interface RequestIdGenerator {
  /**
   * Generates a unique identifier for the session request. This will be the identifier for the
   * entire `session.execute()` call. This identifier will be added to logs, and propagated to
   * request trackers.
   *
   * @param statement the statement to be executed
   * @return a unique identifier for the session request
   */
  String getSessionRequestId(@NonNull Request statement);

  /**
   * Generates a unique identifier for the node request. This will be the identifier for the CQL
   * request against a particular node. There can be one or more node requests for a single session
   * request, due to retries or speculative executions. This identifier will be added to logs, and
   * propagated to request trackers.
   *
   * @param statement the statement to be executed
   * @param sessionRequestId the session request identifier
   * @param executionCount the number of previous node requests for this session request, due to
   *     retries or speculative executions
   * @return a unique identifier for the node request
   */
  String getNodeRequestId(
      @NonNull Request statement, @NonNull String sessionRequestId, int executionCount);

  default Statement<?> getDecoratedStatement(
      @NonNull Statement<?> statement, @NonNull String nodeRequestId) {
    Map<String, ByteBuffer> customPayload =
        NullAllowingImmutableMap.<String, ByteBuffer>builder()
            .putAll(statement.getCustomPayload())
            .put("request-id", ByteBuffer.wrap(nodeRequestId.getBytes(StandardCharsets.UTF_8)))
            .build();
    return statement.setCustomPayload(customPayload);
  }
}
