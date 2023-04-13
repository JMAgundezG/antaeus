# What changes I would create to improve/scale the project?



## Database

I would substitute the current DB for PostgreSQL, there are a lot of improvements using an active BD than using a passive one like SQLite. Using that, we can implement new features like triggers, connect with other microservices, connect monitorization tools like Metabase, etc.

## Models

The changes that I would make in the models are in the Invoice class. I feel empty with just that attributes. The first thing that I would implement inside the model is a timestamp to check when the invoice is created, It will help a lot in creating more insights and features if we have the stamp of the creation of the invoice or the stamp of the moment that the invoice is paid, or updated...

## Architecture

This architecture is ok but we cannot scale a lot. Let's suppose that we have a really big amount of users so we have a really big amount of invoices. There could be a moment when we are generating more invoices per day than invoices that we can charge per day. We would have a bottleneck that we cannot afford well. My solution to that is to create a completely new microservice separated to do those charges, it will be connected by a message queue to the producer of the invoices. This producer will send the invoice to charge to the queue, so we can take the consumer that makes the charges and clone it, having more microservices that makes the same tasks, improving the speed of completing the task.

![image-20230413125043494](/home/jmagundezg/.config/Typora/typora-user-images/image-20230413125043494.png)



## Jobs

The jobs can have some concurrency problems, I put a semaphore for resolving a really naive problem of having the two same tasks running concurrently, but there are more problems with that. For example, what happens if we deploy? We are cutting the job?



## Handlers

I don't like how the API is coded, I know that it's a naive project but I started to create a handler for the API actions and responses, I would implement that strategy to all endpoints to have a cleaner code.