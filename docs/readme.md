# JWorkflow

JWorkflow is a light weight workflow library for Java.  It supports pluggable persistence and concurrency providers to allow for multi-node clusters.

## Installing

### Using Maven

Add `jworkflow` to your POM file as a dependency.

```xml
<dependencies>
    <dependency>
        <groupId>net.jworkflow</groupId>
        <artifactId>jworkflow</artifactId>
        <version>0.5-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### Using Gradle

```Gradle
dependencies { 
    compile 'net.jworkflow:jworkflow:0.5-SNAPSHOT'
}
```

## Tutorial

 * [Basic Concepts](01-basic-concepts.md)
 * [Passing Data](02-data.md)
 * [Reponding to external events](03-events.md)
 * [Error handling](04-error-handling.md)
 * [Control structures](05-control-structures.md)
 * [Saga transactions](06-sagas.md)
 * [Schedule and recur](07-schedule.md)
 * [Defining workflows in JSON](08-json.md)


## Samples

 * [Hello World](https://github.com/danielgerlag/jworkflow/tree/master/samples/sample01)
 * [Passing Data](https://github.com/danielgerlag/jworkflow/tree/master/samples/sample02)
 * [If condition](https://github.com/danielgerlag/jworkflow/tree/master/samples/sample06)
 * [Responding to external events](https://github.com/danielgerlag/jworkflow/tree/master/samples/sample03)
 * [Parallel ForEach](https://github.com/danielgerlag/jworkflow/tree/master/samples/sample04)
 * [While loop](https://github.com/danielgerlag/jworkflow/tree/master/samples/sample05)
 * [Saga Transactions](https://github.com/danielgerlag/jworkflow/tree/master/samples/sample07)
 * [JSON Workflows](https://github.com/danielgerlag/jworkflow/tree/master/samples/sample08)
