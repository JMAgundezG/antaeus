package io.pleo.antaeus.core.exceptions

class ChargeFailedException(invoiceId: Int) :
    Exception("Currency of invoice '$invoiceId' does not exist")