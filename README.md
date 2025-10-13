# Kafka Propagate Context

Simple project to show problem with PropagatedContext in Micronaut in Kafka 
consumers.

## General idea
In KafkaPropagateContextTest there is a test called testPropagateRequestId 
which will do an http call to the endpoint POST /main with a header that is 
x-reqeust-id and set to a random UUID. The idea is that request id shoudl 
then be propagated through all http and Kafka by being read into propagated 
context on http controller or in kafka consumer. Then put on a header when 
being produced a message to kafka.

## What does not work
Setting it in the PropagatedContext in a kafka consumer is what does not 
seem to work.

