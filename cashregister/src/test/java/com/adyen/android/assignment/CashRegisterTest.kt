package com.adyen.android.assignment

import com.adyen.android.assignment.money.Bill
import com.adyen.android.assignment.money.Change
import com.adyen.android.assignment.money.Coin
import org.junit.Assert.assertEquals
import org.junit.Test

class CashRegisterTest {
    @Test
    fun testSimpleTransaction() {
        val change = Change()
            .add(Coin.TWO_EURO, 4)
            .add(Bill.ONE_HUNDRED_EURO, 1)
            .add(Coin.FIFTY_CENT, 3)
            .add(Coin.TWENTY_CENT, 20)
            .add(Coin.TEN_CENT, 2)
        val cashRegister = CashRegister(change)
        val price = 410L
        val paid = Change().add(Coin.TWO_EURO, 3)
        val ret = cashRegister.performTransaction(price, paid)
        assertEquals(
            Change()
                .add(Coin.TWENTY_CENT, 2)
                .add(Coin.FIFTY_CENT, 3), ret
        )
        assertEquals(
            Change()
                .add(Coin.TWO_EURO, 7)
                .add(Bill.ONE_HUNDRED_EURO, 1)
                .add(Coin.TWENTY_CENT, 18)
                .add(Coin.TEN_CENT, 2), change
        )
    }

    @Test
    fun testErrorTransaction() {
        val change = Change()
            .add(Coin.TWO_EURO, 4)
            .add(Bill.ONE_HUNDRED_EURO, 1)
            .add(Coin.FIFTY_CENT, 3)
            .add(Coin.TWENTY_CENT, 20)
            .add(Coin.TEN_CENT, 2)
        val cashRegister = CashRegister(change)
        val price = 445L
        val paid = Change().add(Coin.TWO_EURO, 3)
        val failed = try {
            cashRegister.performTransaction(price, paid)
        } catch (ex: Exception) {
            true
        }
        assertEquals(true, failed)
    }
}
