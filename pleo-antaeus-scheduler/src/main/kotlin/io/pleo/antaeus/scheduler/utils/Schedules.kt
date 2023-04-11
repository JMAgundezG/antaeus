package io.pleo.antaeus.scheduler.utils

import org.quartz.CronExpression

val OncePerDayAt9AM = CronExpression("0 0 9 ? * MON,TUE,WED,THU,FRI *")