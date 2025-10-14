# Kafka Propagate Context

Simple project to show problem with PropagatedContext in Micronaut in Kafka 
consumers.

## General idea
In KafkaPropagateContextTest there is a test called testPropagateRequestId 
which will do an http call to the endpoint POST /main with a header that is 
x-reqeust-id and set to a random UUID. The idea is that request id should 
then be propagated through all HTTP and Kafka by being read into propagated 
context on http controller or in kafka consumer. Then put on a header when 
being produced a message to kafka.

## What does not work
Setting it in the PropagatedContext in a kafka consumer is what does not 
seem to work. We see that when it is picked up to be produced in 
MainConsumer we get an error:

```
java.lang.IllegalStateException: No active propagation context!
	at io.micronaut.core.propagation.PropagatedContextImpl.get(PropagatedContextImpl.java:82)
	at io.micronaut.core.propagation.PropagatedContext.get(PropagatedContext.java:79)
	at net.discordia.kafka.propagate.context.kafka.MainConsumer.sendSecondaryMessage(MainConsumer.java:41)
	at reactor.core.publisher.MonoFlatMap$FlatMapMain.onNext(MonoFlatMap.java:132)
	at reactor.core.publisher.FluxMap$MapSubscriber.onNext(FluxMap.java:122)
	at reactor.core.publisher.MonoContextWriteRestoringThreadLocals$ContextWriteRestoringThreadLocalsSubscriber.onNext(MonoContextWriteRestoringThreadLocals.java:110)
	at reactor.core.publisher.Operators$ScalarSubscription.request(Operators.java:2570)
	at reactor.core.publisher.MonoContextWriteRestoringThreadLocals$ContextWriteRestoringThreadLocalsSubscriber.request(MonoContextWriteRestoringThreadLocals.java:156)
	at reactor.core.publisher.FluxMap$MapSubscriber.request(FluxMap.java:164)
	at reactor.core.publisher.MonoFlatMap$FlatMapMain.request(MonoFlatMap.java:194)
	at reactor.core.publisher.BlockingSingleSubscriber.onSubscribe(BlockingSingleSubscriber.java:54)
	at reactor.core.publisher.MonoFlatMap$FlatMapMain.onSubscribe(MonoFlatMap.java:117)
	at reactor.core.publisher.FluxMap$MapSubscriber.onSubscribe(FluxMap.java:92)
	at reactor.core.publisher.MonoContextWriteRestoringThreadLocals$ContextWriteRestoringThreadLocalsSubscriber.onSubscribe(MonoContextWriteRestoringThreadLocals.java:95)
	at reactor.core.publisher.MonoJust.subscribe(MonoJust.java:55)
	at reactor.core.publisher.MonoContextWriteRestoringThreadLocals.subscribe(MonoContextWriteRestoringThreadLocals.java:44)
	at reactor.core.publisher.Mono.subscribe(Mono.java:4576)
	at reactor.core.publisher.Mono.block(Mono.java:1778)
	at net.discordia.kafka.propagate.context.kafka.MainConsumer.consume(MainConsumer.java:36)
	at net.discordia.kafka.propagate.context.kafka.$MainConsumer$Definition$Exec.dispatch(Unknown Source)
	at io.micronaut.context.AbstractExecutableMethodsDefinition$DispatchedExecutableMethod.invoke(AbstractExecutableMethodsDefinition.java:456)
	at io.micronaut.inject.DelegatingExecutableMethod.invoke(DelegatingExecutableMethod.java:86)
	at io.micronaut.core.bind.DefaultExecutableBinder$1.invoke(DefaultExecutableBinder.java:108)
	at io.micronaut.configuration.kafka.processor.ConsumerStateSingle.process(ConsumerStateSingle.java:118)
	at io.micronaut.configuration.kafka.processor.ConsumerStateSingle.processRecords(ConsumerStateSingle.java:91)
	at io.micronaut.configuration.kafka.processor.ConsumerState.pollAndProcessRecords(ConsumerState.java:214)
	at io.micronaut.configuration.kafka.processor.ConsumerState.refreshAssignmentsPollAndProcessRecords(ConsumerState.java:164)
	at io.micronaut.configuration.kafka.processor.ConsumerState.threadPollLoop(ConsumerState.java:154)
	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:572)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:317)
	at java.base/java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:304)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
	at java.base/java.lang.Thread.run(Thread.java:1583)
	Suppressed: java.lang.Exception: #block terminated with an error
		at reactor.core.publisher.BlockingSingleSubscriber.blockingGet(BlockingSingleSubscriber.java:104)
		at reactor.core.publisher.Mono.block(Mono.java:1779)
		... 16 common frames omitted

```

