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
package com.datastax.oss.driver.api.querybuilder.select;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import edu.umd.cs.findbugs.annotations.NonNull;

public class BM25OrderingClause extends OrderingClause {

  private final CqlIdentifier identifier;
  private final String stringToMatch;

  BM25OrderingClause(CqlIdentifier identifier, String stringToMatch) {

    this.identifier = identifier;
    this.stringToMatch = stringToMatch;
  }

  public static BM25OrderingClause create(CqlIdentifier identifier, String stringToMatch) {
    return new BM25OrderingClause(identifier, stringToMatch);
  }

  @Override
  public void appendTo(@NonNull StringBuilder builder) {
    builder.append(" ORDER BY ").append(this.identifier.asCql(true)).append(" BM25 OF ");
    QueryBuilder.literal(this.stringToMatch).appendTo(builder);
  }
}
