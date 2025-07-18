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

import static com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder.ASC;
import static com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder.DESC;
import static com.datastax.oss.driver.api.querybuilder.Assertions.assertThat;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.selectFrom;

import com.datastax.oss.driver.api.core.data.CqlVector;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.internal.querybuilder.select.BM25Select;
import com.datastax.oss.driver.internal.querybuilder.select.DefaultSelect;
import org.junit.Test;

public class BM25SelectOrderingTest {

  @Test
  public void should_generate_bm25_ordering_clauses() {
    DefaultSelect base =
        (DefaultSelect) selectFrom("foo").all().where(Relation.column("k").isEqualTo(literal(1)));
    assertThat(BM25Select.create(base).orderByBM25Of("c1", "foo"))
        .hasCql("SELECT * FROM foo WHERE k=1 ORDER BY c1 BM25 OF 'foo'");
  }

  @Test
  public void should_replace_columns_ordering_with_bm25() {
    DefaultSelect base =
        (DefaultSelect)
            selectFrom("foo")
                .all()
                .where(Relation.column("k").isEqualTo(literal(1)))
                .orderBy("c1", ASC)
                .orderBy("c2", DESC);
    assertThat(BM25Select.create(base).orderByBM25Of("c1", "foo"))
        .hasCql("SELECT * FROM foo WHERE k=1 ORDER BY c1 BM25 OF 'foo'");
  }

  @Test
  public void should_replace_ann_ordering_with_bm25() {
    DefaultSelect base =
        (DefaultSelect)
            selectFrom("foo")
                .all()
                .where(Relation.column("k").isEqualTo(literal(1)))
                .orderByAnnOf("c1", CqlVector.newInstance(0.1, 0.2, 0.3));
    assertThat(BM25Select.create(base).orderByBM25Of("c1", "foo"))
        .hasCql("SELECT * FROM foo WHERE k=1 ORDER BY c1 BM25 OF 'foo'");
  }

  @Test
  public void should_replace_bm25_ordering_with_columns() {
    DefaultSelect base =
        (DefaultSelect) selectFrom("foo").all().where(Relation.column("k").isEqualTo(literal(1)));
    Select bm25Select = BM25Select.create(base).orderByBM25Of("c1", "foo");
    Select finalSelect = bm25Select.orderBy("c1", ASC).orderBy("c2", DESC);
    assertThat(finalSelect).hasCql("SELECT * FROM foo WHERE k=1 ORDER BY c1 ASC,c2 DESC");
  }

  @Test
  public void should_replace_bm25_ordering_with_ann() {
    DefaultSelect base =
        (DefaultSelect) selectFrom("foo").all().where(Relation.column("k").isEqualTo(literal(1)));
    Select bm25Select = BM25Select.create(base).orderByBM25Of("c1", "foo");
    Select finalSelect = bm25Select.orderByAnnOf("c1", CqlVector.newInstance(0.1, 0.2, 0.3));
    assertThat(finalSelect)
        .hasCql("SELECT * FROM foo WHERE k=1 ORDER BY c1 ANN OF [0.1, 0.2, 0.3]");
  }
}
