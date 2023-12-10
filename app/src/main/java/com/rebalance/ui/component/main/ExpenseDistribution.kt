package com.rebalance.ui.component.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.rebalance.backend.dto.BarChartItem
import com.rebalance.backend.localdb.entities.Group
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExpenseDistribution(
    group: Group?,
    data: List<BarChartItem>
) {
    val groupCurrency = group?.currency ?: ""
    val debtSettlement = DebtSettlement(data)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(bottom = 85.dp)
    ) {
        for (item in debtSettlement.getListTransaction()) {
            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(CenterHorizontally),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // First column
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = "${item.getFrom()}",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "owes",
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "${item.getTo()}",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    // Second column
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        Text(
                            text = "${String.format("%.2f", item.getAmount())} $groupCurrency",
                            fontSize = 18.sp,
                            modifier = Modifier
                                .align(End)
                        )
                    }
                }
            }

        }
    }

}

class Person(private var user: String, private var balance: Double) {

    fun getBalance(): Double {
        return this.balance
    }

    fun setBalance(balance: Double) {
        this.balance = balance
    }

    override fun toString(): String {
        return user
    }

}

class Transaction(private var from: Person?, private var to: Person?, private var amount: Double) {

    fun getTo(): Person? {
        return this.to
    }

    fun getFrom(): Person? {
        return this.from
    }

    fun getAmount(): Double {
        return this.amount
    }

    override fun toString(): String {
        return "$from owes $to $amount"
    }

}

class DebtSettlement(data: List<BarChartItem>) {

    private var listPerson: MutableList<Person> = mutableListOf()
    private var listTransaction: MutableList<Transaction> = mutableListOf()
    private var totalBalance: Double = 0.0

    init {
        this.createPersonList(data)
        this.calculateTotalBalance()
        this.settleDebts()
    }

    private fun createPersonList(data: List<BarChartItem>) {
        for (item in data) {
            this.listPerson.add(Person(item.username, item.balance.setScale(2).toDouble()))
        }
    }

    private fun calculateTotalBalance() {
        this.totalBalance = 0.0
        for (person in this.listPerson) {
            totalBalance += kotlin.math.abs(person.getBalance())
        }
    }

    private fun settleDebts() {
        this.listTransaction.clear()

        while(true) {
            this.listPerson.sortedBy { -it.getBalance() }

            var maxPositive: Person? = null
            var maxNegative: Person? = null

            for (p in this.listPerson) {
                if (p.getBalance() > 0 && (maxPositive == null || p.getBalance() > maxPositive.getBalance())) {
                    maxPositive = p
                }
                if (p.getBalance() < 0 && (maxNegative == null || p.getBalance() < maxNegative.getBalance())) {
                    maxNegative = p
                }
            }

            if (maxPositive == null || maxNegative == null) {
                break
            }

            val amount = maxPositive.getBalance().coerceAtMost(-maxNegative.getBalance())
            maxPositive.setBalance(maxPositive.getBalance() - amount)
            maxNegative.setBalance(maxNegative.getBalance() + amount)
            this.listTransaction.add(Transaction(maxNegative, maxPositive, amount))
        }
    }

    fun getListTransaction(): MutableList<Transaction> {
        return this.listTransaction
    }
}
