# Decisions for the challenge



## Models changed



### InvoiceStatus

The InvoiceStatus values weren't covering all the states necessary to make the new feature so more statuses were created

- **INVALID_CUSTOMER**: It's an error status where the invoice's customer does not exist
- **INVALID_CURRENCY**: It's an error status where the customer's invoice and the invoice's currency do not match
- **READY**: It's the state that the invoice is ready to process





## Services created or modified

- **InvoiceService**: Added a function for updating the status of the invoices and a new fetch that excludes invoices by status
- **InvoiceValidatorService**: This service will be responsible for validating and updating the invoices' statuses. It has a private function for validating and a public function that validates and updates the invoices
- **BillingService**: This new service will manage the process of charging the invoices to the customers
- **NotificationService**: This service handles the communication between Antaeus and the users that need to be modified. For example, if the invoice needs some manual review by the administrator it will be notified using that service.
- **LoggerNotificationService**: The implementation of NotificationService for local debugging purposes



## Job Scheduling

To implement a Job scheduler I've used Quartz after a quick search I saw that is probably the most used library in Kotlin to implement that kind of process. This job schedule is created in a new folder called **pleo-antaeus-scheduler** where all the things related to the scheduler are created inside.



### Jobs 

There's only a Job called ChargeInvoicesJob that will run the task of trying to charge all the non-already paid invoices in the database. It will be executed at 9 AM from Monday to Friday

## Validation of invoices

To make sure that Antaeus can charge an invoice to a customer, the invoices are validated by InvoiceValidatorService to make sure that the invoice is payable. 

## Testing

There are a lot of new tests implemented. I'm not a fan of trusting 100% coverage, I prefer to have more strategies ([mutation testing](https://medium.com/seat-code/mutation-testing-in-kotlin-a8834771e85e), End to end tests, monitorization with alerts of backend, alerts inside database, etc) but I usually prefer to have more tests in the project.

## Branching models

I don't need that project. Usually, I preferred to implement using a good branching model like [GitFlow](http://datasift.github.io/gitflow/IntroducingGitFlow.html) or [Trunk-based development](https://trunkbaseddevelopment.com/) (I don't use any other good branching model) in a bigger project, but in this case, there is only a new feature and there is just a developer. 

