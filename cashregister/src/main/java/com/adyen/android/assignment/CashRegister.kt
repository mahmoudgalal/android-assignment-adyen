package com.adyen.android.assignment

import com.adyen.android.assignment.money.Change
import kotlin.math.min

/**
 * The CashRegister class holds the logic for performing transactions.
 *
 * @param change The change that the CashRegister is holding.
 */
class CashRegister(private val change: Change) {
    /**
     * Performs a transaction for a product/products with a certain price and a given amount.
     *
     * @param price The price of the product(s).
     * @param amountPaid The amount paid by the shopper.
     *
     * @return The change for the transaction.
     *
     * @throws TransactionException If the transaction cannot be performed.
     */
    fun performTransaction(price: Long, amountPaid: Change): Change {
        val residue = amountPaid.total - price
        return if (residue < 0)
            throw TransactionException("No Enough Funds")
        else if (residue > 0) {
            val ret = Change.none()
            change.add(amountPaid)
            change.constructOptimalChange(residue, ret)
            ret
        } else
            Change.none()
    }

    @Throws
    tailrec fun Change.constructOptimalChange(initial: Long, returned: Change) {
        if (initial == 0L)
            return
        var initVal = initial
        val maxItemAvailable = getElements().findLast {
            it.minorValue <= initVal
        } ?: throw TransactionException("No Enough Funds")
        val maxItemCount = getCount(maxItemAvailable)
        val allowedCount = min(initVal / maxItemAvailable.minorValue, maxItemCount.toLong())
        remove(maxItemAvailable, allowedCount.toInt())
        returned.add(maxItemAvailable, allowedCount.toInt())
        initVal -= maxItemAvailable.minorValue * allowedCount
        constructOptimalChange(initVal, returned)
    }

    class TransactionException(message: String, cause: Throwable? = null) :
        Exception(message, cause)
}
