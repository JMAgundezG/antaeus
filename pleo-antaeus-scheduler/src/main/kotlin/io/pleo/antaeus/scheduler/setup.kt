package io.pleo.antaeus.scheduler

import io.pleo.antaeus.core.external.NotificationService
import io.pleo.antaeus.core.services.BillingService
import io.pleo.antaeus.core.services.InvoiceService

import io.pleo.antaeus.core.services.InvoiceValidatorService
import io.pleo.antaeus.scheduler.jobs.ChargeInvoicesJob
import io.pleo.antaeus.scheduler.jobs.PleoJob
import mu.KotlinLogging
import org.quartz.impl.JobExecutionContextImpl
import org.quartz.impl.StdSchedulerFactory

private val scheduler = StdSchedulerFactory.getDefaultScheduler()
private val logger = KotlinLogging.logger("SchedulerJobs")

// https://www.baeldung.com/quartz


fun startScheduler(
    billingService: BillingService,
    notificationService: NotificationService
) {

    logger.info("Scheduler created")
    // CLEAR BEFORE CREATE
    scheduler.clear()


    val chargeInvoicesJob = ChargeInvoicesJob()
    scheduleNewJob(chargeInvoicesJob)
    scheduler.context["billingService"] = billingService
    logger.info("Scheduler started")
    // START SCHEDULER
    scheduler.start()

}

private fun scheduleNewJob(job: PleoJob) {
    scheduler.scheduleJob(job.buildJobDetail(), job.buildJobTrigger())
}