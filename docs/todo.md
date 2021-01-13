# TODO
* [Emit](https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/table/common.html#emit-a-table) the SQL query result (writting the Table to a TableSink). 3 options:
    * AppendStreamTableSink
    * RetractStreamTableSink
    * UpsertStreamTableSink
 * Use kafka-cat to send events to kafka: https://github.com/edenhill/kafkacat.  
   Produce a tombstone (a "delete" for compacted topics) for key "abc" by providing an empty message value which -Z interpretes as NULL:  
   `$ echo "abc:" | kafkacat -b mybroker -t mytopic -Z -K:`