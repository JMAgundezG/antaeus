package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.pleo.antaeus.core.external.LoggerNotificationService
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class InvoiceValidatorServiceTest {


    private val mockMoney = Money(BigDecimal.valueOf(10), Currency.EUR)
    private val alreadyPaidInvoice = Invoice(1, 1, mockMoney, InvoiceStatus.PAID)
    private val pendingInvoice = Invoice(2, 1, mockMoney, InvoiceStatus.PENDING)
    private val readyInvoice = Invoice(2, 1, mockMoney, InvoiceStatus.READY)
    private val pendingInvalidCustomerInvoice = Invoice(3, 404, mockMoney, InvoiceStatus.PENDING)
    private val invalidCustomerInvoice = Invoice(3, 404, mockMoney, InvoiceStatus.INVALID_CUSTOMER)
    private val pendingInvalidCurrencyInvoice = Invoice(4, 1, Money(BigDecimal.valueOf(10), Currency.DKK), InvoiceStatus.PENDING)
    private val invalidCurrencyInvoice = Invoice(4, 1, Money(BigDecimal.valueOf(10), Currency.DKK), InvoiceStatus.INVALID_CURRENCY)


    private val dal = mockk<AntaeusDal> {
        every {fetchCustomer(1)} returns Customer(1, Currency.EUR)
        every {fetchCustomer(404)} returns null

        every { fetchInvoice(404) } returns null
        every { fetchInvoice(1) } returns alreadyPaidInvoice
        every { fetchInvoice(2) } returns pendingInvoice
        every { updateInvoice(1, InvoiceStatus.PAID) } returns alreadyPaidInvoice
        every { updateInvoice(2, InvoiceStatus.READY) } returns readyInvoice
        every { updateInvoice(3, InvoiceStatus.INVALID_CUSTOMER) } returns invalidCustomerInvoice
        every { updateInvoice(4, InvoiceStatus.INVALID_CURRENCY) } returns invalidCurrencyInvoice

    }

    private val invoiceService = InvoiceService(dal = dal)
    private val customerService = CustomerService(dal = dal)
    private val notificationService = mockk<LoggerNotificationService> {
        every { notifyPleo(any(), any()) } just runs
    }
    private val invoiceValidatorService = InvoiceValidatorService(
        invoiceService = invoiceService,
        customerService = customerService,
        notificationService = notificationService
    )

    @Test
    fun `validateAndUpdate returns status PAID when the invoice is already paid`() {
        invoiceValidatorService.validateAndUpdate(alreadyPaidInvoice).status
    }

    @Test
    fun `validateAndUpdate returns status READY when the invoice is already valid but not paid`() {
        assert(invoiceValidatorService.validateAndUpdate(pendingInvoice).status == InvoiceStatus.READY)
    }

    @Test
    fun `validateAndUpdate returns status INVALID_CUSTOMER when the invoice customer does not match with any customer`() {
        assert(invoiceValidatorService.validateAndUpdate(pendingInvalidCustomerInvoice).status == InvoiceStatus.INVALID_CUSTOMER)
    }

    @Test
    fun `validateAndUpdate returns status INVALID_CURRENCY when the invoice customer does not match with any customer`() {
        assert(invoiceValidatorService.validateAndUpdate(pendingInvalidCurrencyInvoice).status == InvoiceStatus.INVALID_CURRENCY)
    }
}

