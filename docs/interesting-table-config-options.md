# Interesting Table Config options
* __table.exec.sink.not-null-enforcer__: The NOT NULL column constraint on a table enforces that null values can't be inserted into the table.  
Flink supports 'error' (default) and 'drop' enforcement behavior.  
By default, Flink will check values and throw runtime exception when null values writing into NOT NULL columns.  
Users can change the behavior to 'drop' to silently drop such records without throwing exception.
* __table.exec.state.ttl__: Specifies a minimum time interval for how long idle state (i.e. state which was not updated), will be retained.  
Default value is 0, which means that it will never clean up state.
* __table.exec.source.idle-timeout__: When a source do not receive any elements for the timeout time, it will be marked as temporarily idle.  
Default value is 0, which means detecting source idleness is not enabled.