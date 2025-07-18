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
package com.datastax.oss.driver.internal.querybuilder.select;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.querybuilder.select.BM25OrderingClause;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import edu.umd.cs.findbugs.annotations.NonNull;

public class BM25Select extends DefaultSelect {

  public BM25Select(DefaultSelect copy) {
    super(
        copy.getKeyspace(),
        copy.getTable(),
        copy.isJson(),
        copy.isDistinct(),
        copy.getSelectors(),
        copy.getRelations(),
        copy.getGroupByClauses(),
        copy.getOrderingClause(),
        copy.getLimit(),
        copy.getPerPartitionLimit(),
        copy.allowsFiltering());
  }

  public static BM25Select create(DefaultSelect copy) {
    return new BM25Select(copy);
  }

  @NonNull
  public Select orderByBM25Of(@NonNull String columnName, @NonNull String stringToMatch) {
    return withOrderingClause(
        BM25OrderingClause.create(CqlIdentifier.fromCql(columnName), stringToMatch));
  }

  @NonNull
  public Select orderByBM25Of(@NonNull CqlIdentifier columnId, @NonNull String stringToMatch) {
    return withOrderingClause(BM25OrderingClause.create(columnId, stringToMatch));
  }
}
