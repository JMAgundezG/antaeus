package io.pleo.antaeus.core.services
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.pleo.antaeus.core.external.LoggerNotificationService
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class BillingServiceTest {
    private val mockMoney = Money(BigDecimal.valueOf(1), Currency.EUR)
    private val correctInvoice = Invoice(1, 1, mockMoney, InvoiceStatus.READY)
    private val wrongInvoice = Invoice(404, 1, mockMoney, InvoiceStatus.READY)
    private val notPayableInvoice = Invoice(500, 1, mockMoney, InvoiceStatus.INVALID_CURRENCY)

    private val correctCustomer = Customer(1, Currency.EUR)
    private val dal = mockk<AntaeusDal> {
        every { fetchCustomer(404) } returns null

        every { fetchCustomer(1) } returns correctCustomer
        every { fetchInvoice(1)} returns correctInvoice
        every { updateInvoice(1, InvoiceStatus.READY)} returns correctInvoice
        every { updateInvoice(1, InvoiceStatus.PAID)} returns correctInvoice
        every { updateInvoice(404, InvoiceStatus.READY)} returns wrongInvoice
        every { fetchInvoice(404) } returns wrongInvoice

        every { updateInvoice(500, InvoiceStatus.INVALID_CURRENCY)} returns notPayableInvoice
        every { updateInvoice(500, InvoiceStatus.READY)} returns notPayableInvoice
        every { fetchInvoice(500) } returns notPayableInvoice
    }
    private val notificationService = mockk<LoggerNotificationService> {
        every { notifyPleo(any(), any()) } just runs
        every { notifyUserAccount(any(), any(), any()) } just runs
    }
    private val paymentProvider = mockk<PaymentProvider> {
        every {charge(Invoice(404, 1, mockMoney, InvoiceStatus.READY))} returns false
        every {charge(Invoice(1, 1, mockMoney, InvoiceStatus.READY))} returns true
    }
    private val customerService = CustomerService(dal = dal)

    private val invoiceService = InvoiceService(dal = dal)

    private val invoiceValidatorService = InvoiceValidatorService(invoiceService, customerService, notificationService)

    private val billingService = BillingService(paymentProvider, invoiceService, invoiceValidatorService, notificationService)

    @Test
    fun `payInvoice will return true if everything works fine`() {
        assert(billingService.chargeInvoice(1))
    }

    @Test
    fun `payInvoice will return false paymentProvider fails`() {
        assert(!billingService.chargeInvoice(404))
    }

}
