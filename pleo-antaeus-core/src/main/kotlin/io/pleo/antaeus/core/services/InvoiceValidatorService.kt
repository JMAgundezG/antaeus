package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.external.NotificationService
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import mu.KotlinLogging

val logger = KotlinLogging.logger("InvoiceValidatorService")


class InvoiceValidatorService(
    private val invoiceService: InvoiceService,
    private val customerService: CustomerService,
    private val notificationService: NotificationService
) {

    fun validateAndUpdate(invoice: Invoice): Invoice {
        logger.info("Validating invoice ${invoice.id}")
        val newInvoiceStatus = validate(invoice)
        if (newInvoiceStatus == InvoiceStatus.INVALID_CUSTOMER || newInvoiceStatus == InvoiceStatus.INVALID_CURRENCY) {
            notificationService.notifyPleo(invoice.id, "INVALID INVOICE")
            logger.info("Not valid invoice ${invoice.id}")

        }
        logger.info("Valid invoice ${invoice.id}")
        return invoiceService.update(invoice.id, newInvoiceStatus)
    }

    private fun validate(invoice: Invoice): InvoiceStatus {

        if (invoice.status == InvoiceStatus.PAID) {
            return InvoiceStatus.PAID
        }
        try {
            val customer = customerService.fetch(invoice.customerId)
            if (invoice.amount.currency != customer.currency) {
                return InvoiceStatus.INVALID_CURRENCY
            }
        } catch (e: CustomerNotFoundException) {
            return InvoiceStatus.INVALID_CUSTOMER
        }
        return InvoiceStatus.READY
    }

    fun isInvoicePayable(invoice: Invoice): Boolean {
        return invoice.status == InvoiceStatus.READY
    }
}