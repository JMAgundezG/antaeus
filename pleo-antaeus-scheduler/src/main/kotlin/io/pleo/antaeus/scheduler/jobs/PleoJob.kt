package io.pleo.antaeus.scheduler.jobs

import org.quartz.Job
import org.quartz.JobDetail
import org.quartz.JobExecutionContext
import org.quartz.Trigger

interface PleoJob : Job {
    override fun execute(context: JobExecutionContext?)

    fun buildJobTrigger(): Trigger
    fun buildJobDetail(): JobDetail
}