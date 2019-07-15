# Provider Apache Kafka
Since Apache Kafka is no regular message-broker, the usage and outputs have to be interpreted different.

Syntax:
For a single broker (typically used during development):

    kafka://<host>:<port>

When using multiple brokers (the host will be ignored):

    kafka://<ignored>?brokers=broker1:port,broker2:port,broker3:port

Available url-parameters

* `group` or `groupId` - Defines the groupId Kafka should use for the connection (otherwise `plunger-$USER` will be used)
* `key` - key used when sending data into kafka (optional)
* `autoOffsetReset` - Set the `auto.offset.reset` kafka option, defaults to `earliest`
* `timeout` - Option to set the timeout kafka uses for poll in millis/human notation, defaults to `4s`
* `maxPollRecords` - Sets the `max.poll.records` kafka options, defaults to `1`
* `maxPartitionFetchBytes` - Sets the `max.partition.fetch.bytes` option for the consumer (pcat)
* `maxRequestSize` - Sets the `max.request.size` option for the producer (pput)
* `ack` - Set the producer `acks` option for the producer (pput), defaults to `all`
* `schemaRegistry` - Set the URL to the kafka Schema registry. If this is set, it is assumed that the topic contains Avro rather than plain string. If the protocol is omitted, `http://` is taken. If a protocol is defined, it must be URL encoded to eg. `http%3A%2F%2Fexample.com`.

Examples:

    pls kafka://localhost:5672?managementPort=15672
    pcat kafka://localhost:5672/my.queue?managementPort=15672

Usually you want to configure the message broker in the configuration file, eg.:

    Alias k
    Target kafka://localhost:5672?managementPort=15672

And use it this way later (using the examples from above):

    pls k
    pcat k/my-topic

Limitations
* `pls` does not display the amount of consumers
* `pls` does not display the amount of message left to be consumed, instead it shows the total amount of message in that topic (it is a log).
* `pcount` has the same limitations as `pls`
* Authentication is currently not supported
* Access to kafka has to be within the same network
* Kafka does not support user defined properties per message. The properties shown by cat are metadata from the record. Only the `key` property will be reused when the `put` command is used.

