package io.pleo.antaeus.core.external;

import mu.KotlinLogging;

interface NotificationService {

    fun notifyUserAccount(accountId: Int, invoiceId: Int, message: String)

    fun notifyPleo(invoiceId: Int, message: String)

}
