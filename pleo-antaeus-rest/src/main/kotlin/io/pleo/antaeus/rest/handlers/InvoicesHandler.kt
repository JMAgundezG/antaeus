package io.pleo.antaeus.rest.handlers
import io.pleo.antaeus.core.services.BillingService



class InvoicesHandler(private val billingService: BillingService) {

    fun chargeInvoice(id: Int): Boolean{
        return billingService.chargeInvoice(id)
    }

    fun chargeAllInvoices(){
        billingService.chargeAllInvoices()
    }
}