<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

## Request Id

### Quick overview

Users can inject an identifier for each individual CQL request, and such ID can be written in to the [custom payload](https://github.com/apache/cassandra/blob/trunk/doc/native_protocol_v5.spec) to 
correlate a request across the driver and the Apache Cassandra server.

A request ID generator needs to generate both:
- Session request ID: an identifier for an entire session.execute() call
- Node request ID: an identifier for the execution of a CQL statement against a particular node. There can be one or more node requests for a single session request, due to retries or speculative executions.

Usage:
* Inject ID generator: set the desired `RequestIdGenerator` in `advanced.request-id.generator.class`. 
  The default implementation generates the session request ID as `{session_name}|{hash_code}`, and node request ID as `{session_name}|{hash_code}|{execution_count}`, 
  where "hash_code" is the hash code of the `CqlRequestHandler` object, and "execution_count" is the zero-based index of the node request in the session request.
  For example, if there is a retry or speculative execution right after the first node request, the second node request will have the ID `{session_name}|{hash_code}|1`.
* Add ID to custom payload: disabled by default. Set the desired key in `advanced.request-id.custom-payload-with-key`, 
  then the driver will add the generated ID to the custom payload with the specified key.

### Request Id Generator Configuration

Request ID generator can be declared in the [configuration](../configuration/) as follows:

```
datastax-java-driver.advanced.request-id.generator {
  class = com.example.app.MyGenerator
}
```

To register your own request ID generator, specify the name of the class
that implements `RequestIdGenerator`.

By default, the build-in implementation `DefaultRequestIdGenerator` is used. It generates the ID as
`{session_name}|{hash_code}|{execution_count}`. Note that this ID is not guaranteed to be unique.
Other built-in implementations include `UUIDRequestIdGenerator` and `W3CContextRequestIdGenerator`.

The generated ID will be added to the log message of `CqlRequestHandler`, and propagated to the request trackers.

### Custom Payload Configuration

Users can opt in to add the generated node request ID to the custom payload to achieve request end-to-end tracing.
Custom payload is a map of string to `ByteBuffer` pairs, and the driver will add the generated ID to the custom payload with the specified key.

```
datastax-java-driver.advanced.request-id{
  custom-payload-with-key = my-request-id
}
```

Users can then run Apache Cassandra with customized query handler to extract the request ID from the custom payload, to achieve end-to-end tracing.

When this key is set to an empty string (default), the driver will not add the ID to the custom payload.
