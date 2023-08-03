# A reproducer for data synchronization issue in a @Clustered @Singleton (Zendesk ticket #2408)

## Build & run

```
mvn install
```

Deploy the application in `target/simple-1.0-SNAPSHOT.war` to Payara Server with context root `SimpleWAR`.

Then access the following URL to trigger a REST endpoint: 

http://localhost:8080/SimpleWAR/rest/request

Check the logs of Payara Server

### Expected behavior:

Each new log entry should contain a new number from the list and the first item removed from the previous list.

### Observed (wrong) behavior:

Sometimes 2 subsequent entries contain the same number and the list doesn't change.


## Observations

When @Clustered annotation is removed, all works as expected. It means that serialization and deserialization to Hazelcast plays some role here. I assume that some parallel executions received the same copy of the singleton bean and don't observe the changes made by another copy of the bean later.

