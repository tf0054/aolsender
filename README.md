# aolsender

A Clojure library designed to test genn.ai with AOL search query data.

## Usage

### exec

First your tuple schema and query should be made like this.

```
gungnir> CREATE TUPLE queryTuple ( uid STRING, query STRING, time TIMESTAMP('yyyy-MM-dd HH:mm:ss'), rank STRING, url STRING);
OK
gungnir> FROM queryTuple USING kafka_spout() FILTER url LIKE '%www%' EMIT uid, url USING kafka_emit('test_input_urls');
OK
gungnir> submit topology aol_simple;
OK
Starting ... Done
{"id":"54c14b920cf23a5b010d91fe","name":"aol_simple","status":"RUNNING","owner":"gennai","createTime":"2015-01-22T19:12:18.718Z","summary":{"name":"gungnir_54c14b920cf23a5b010d91fe","status":"ACTIVE","uptimeSecs":1,"numWorkers":1,"numExecutors":3,"numTasks":3}}
gungnir> 
gungnir> post queryTuple {"uid":"142","query":"westchester.gov","time":"2006-03-20 03:55:57","rank":"1","url":"http://www.westchestergov.com"};
POST /gungnir/v0.1/54c141230cf23a5b010d91fc/queryTuple/json
OK
gungnir>
```

And then you can execute aolsender like this.

```
cat gaute/user-ct-test-collection-01.txt.head1000.json| lein run -- -t queryTuple -u 544a65950cf28a00f105fb79 -s 192.168.30.10:9191
```

After the execution, your kafka can get the calculated output and it can be checked through kafka-console-consumer.sh

```
[vagrant@localhost ~]$ /opt/kafka/bin/kafka-console-consumer.sh  --topic test_input_urls --zookeeper localhost:2181
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.

{"uid":"217","url":"http://www.gogol.com/"}
{"uid":"142","url":"http://www.westchestergov.com"}
{"uid":"142","url":"http://www.westchestergov.com"}
{"uid":"142","url":"http://www.courts.state.ny.us"}
.
.
```

### output(stdout)
Return status, duration(millsec), sended line, (thread-id)

```
204 5 {"uid":"142","query":"westchester.gov","time":"2006-03-20 03:55:57","rank":"1","url":"http://www.westchestergov.com"} (13)
```

Copyright © 2014 genn.ai core team

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
