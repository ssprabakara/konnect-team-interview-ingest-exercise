Requirements:-
  a. JDK - Minimum JDK 17.
  b. The docker kafka cluster needs to be started. From the project root directory `docker-compose --compatibility up -d`
     command can be used to start the kafka cluster.

Assumptions:-
  a.

Building the jar and running the program:-
  a. `cd` into project root directory.
     Run `mvn clean install` on the project root directory.
     Make sure, maven runs successfully and builds the jars under the `target` directory.
     Make sure, there is a jar with `jar-with-dependencies` suffix, as we need this jar to run the program.
  b. Running the program on command line. The below command is a sample usage.
     `java -Xmx1G -cp target/konnect-team-interview-ingest-exercise-1.0-SNAPSHOT-jar-with-dependencies.jar konnect.Driver`
     The process will run for ever, even after publishing the events in the jsonl file to opensearch. To end, we just
     need to kill (Ctrl + c)  the java process.

Future Work & Enhancement:-
  a. For Kafka producer, currently the failed or errored docs|events are only written to logs. It can be put to a
     dead-letter-queue(dlq). But, it might also fail, if there are some core kafka issues.
  b.
