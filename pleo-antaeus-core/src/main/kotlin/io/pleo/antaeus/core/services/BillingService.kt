package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.NotificationService
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import mu.KotlinLogging
import java.lang.Exception
import java.util.concurrent.Semaphore


class BillingService(
    private val paymentProvider: PaymentProvider,
    private val invoiceService: InvoiceService,
    private val invoiceValidatorService: InvoiceValidatorService,
    private val notificationService: NotificationService
) {
    val logger = KotlinLogging.logger("BillingService")
    val semaphore = Semaphore(1)
    private fun chargeInvoice(invoice: Invoice): Boolean {
        logger.info("charging invoice ${invoice.id}")
        val validatedInvoice = invoiceValidatorService.validateAndUpdate(invoice)
        var result: Boolean
        if (invoiceValidatorService.isInvoicePayable(validatedInvoice)) {
            try {
                result = paymentProvider.charge(validatedInvoice)
            } catch (e: Exception) {
                notificationService.notifyPleo(validatedInvoice.id, "ERROR WITH INVOICE PAYMENT ${e.message}")
                return false
            }
            return if (result) {
                notificationService.notifyUserAccount(validatedInvoice.customerId, validatedInvoice.id, "INVOICE PAID")
                invoiceService.update(validatedInvoice.id, InvoiceStatus.PAID)
                true
            } else {
                notificationService.notifyPleo(validatedInvoice.id, "ERROR WITH INVOICE PAYMENT. ERROR IN PAYMENTPROVIDER")
                false
            }
        }
        notificationService.notifyPleo(validatedInvoice.id, "ERROR WITH INVOICE PAYMENT. INVOICE NOT CHARGEABLE ${invoice.status}")
        return false
    }

    fun chargeInvoice(invoiceId: Int): Boolean {
        val invoice = invoiceService.fetch(invoiceId)
        return chargeInvoice(invoice)
    }

    fun chargeAllInvoices() {
        logger.info("Starting charging all invoices process")
        semaphore.acquire()
        invoiceService.fetchAllExcludingStatus(InvoiceStatus.PAID).parallelStream().map {
            chargeInvoice(it)
        }.toArray()
        semaphore.release()
    }



}
