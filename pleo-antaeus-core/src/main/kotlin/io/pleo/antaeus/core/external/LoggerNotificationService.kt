package io.pleo.antaeus.core.external

import mu.KotlinLogging

class LoggerNotificationService: NotificationService {

    val logger = KotlinLogging.logger("LoggerNotificationService")

    override fun notifyUserAccount(accountId: Int, invoiceId: Int, message: String) {
        logger.info("[NOTIFICATIONSERVICE] [ACCOUNT=$accountId] [INVOICE=$invoiceId] $message")
    }

    override fun notifyPleo(invoiceId: Int, message: String) {
        logger.info("[NOTIFICATIONSERVICE] [PLEO/ADMIN] [INVOICE=$invoiceId] $message")
    }

}