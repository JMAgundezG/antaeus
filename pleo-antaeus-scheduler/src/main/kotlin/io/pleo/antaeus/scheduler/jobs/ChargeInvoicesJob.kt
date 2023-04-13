package io.pleo.antaeus.scheduler.jobs

import io.pleo.antaeus.core.services.BillingService
import io.pleo.antaeus.scheduler.utils.OncePerDayAt9AM
import mu.KotlinLogging
import org.quartz.*
import java.lang.Exception


// https://www.baeldung.com/quartz
val logger = KotlinLogging.logger("ChargeInvoicesJob")

class ChargeInvoicesJob() : PleoJob {

    override fun execute(context: JobExecutionContext?) {
        logger.info("EXECUTION STARTED")
        var schedulerContext: SchedulerContext? = null
        try {
            if (context != null) {
                schedulerContext = context.scheduler.context
                (schedulerContext["billingService"] as BillingService?)?.chargeAllInvoices()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun buildJobTrigger(): Trigger {
        return TriggerBuilder
            .newTrigger()
            .withIdentity(TriggerKey.triggerKey("ChargeInvoices"))
            .withSchedule(
                CronScheduleBuilder
                    .cronSchedule(OncePerDayAt9AM)
            )
            .build()
    }

    override fun buildJobDetail(): JobDetail {
        return JobBuilder.newJob(ChargeInvoicesJob::class.java).withIdentity("ChargeInvoicesJob")
            .build()
    }

}