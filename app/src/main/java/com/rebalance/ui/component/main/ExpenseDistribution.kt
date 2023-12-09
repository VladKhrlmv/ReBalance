package com.rebalance.ui.component.main

import androidx.compose.runtime.Composable
import com.rebalance.backend.dto.BarChartItem
import com.rebalance.backend.localdb.entities.Group

@Composable
fun ExpenseDistribution(
    group: Group?,
    data: List<BarChartItem>
) {
//    val groupCurrency =
//        if (groupId == -1L) "" else BackendService(preferences).getGroupById(groupId).getCurrency()
//    val debtSettlement = DebtSettlement(preferences, groupId)
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .wrapContentHeight()
//            .padding(bottom = 85.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        for (item in debtSettlement.getListTransaction()) {
//            Card(
//                modifier = Modifier
//                    .padding(10.dp)
//                    .fillMaxWidth()
//                    .wrapContentHeight(),
//                shape = MaterialTheme.shapes.medium,
//                elevation = CardDefaults.cardElevation(8.dp),
//                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .fillMaxHeight()
//                        .padding(16.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    // First column
//                    Column(
//                        modifier = Modifier
//                            .weight(1f)
//                            .padding(end = 8.dp)
//                    ) {
//                        Text(
//                            text = "${item.getFrom()}",
//                            fontSize = 18.sp,
//                            modifier = Modifier.padding(bottom = 8.dp)
//                        )
//                        Text(
//                            text = "owes",
//                            modifier = Modifier.padding(bottom = 8.dp)
//                        )
//                        Text(
//                            text = "${item.getTo()}",
//                            fontSize = 18.sp,
//                            modifier = Modifier.padding(bottom = 8.dp)
//                        )
//                    }
//                    // Second column
//                    Column(
//                        modifier = Modifier
//                            .weight(1f)
//                            .padding(start = 8.dp)
//                    ) {
//                        Text(
//                            text = "${String.format("%.2f", item.getAmount())} ${groupCurrency}",
//                            fontSize = 18.sp,
//                            modifier = Modifier
//                                .align(End)
//                        )
//                    }
//                }
//            }
//
//        }
//    }

}

class Person {

    private var user: String = ""
    private var balance: Double = 0.0

    constructor(user: String, balance: Double) {
        this.user = user
        this.balance = balance
    }

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

class Transaction {

    private var from: Person? = null
    private var to: Person? = null
    private var amount: Double = 0.0

    constructor(from: Person?, to: Person?, amount: Double) {
        this.from = from
        this.to = to
        this.amount = amount
    }

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

//class DebtSettlement {
//
//    private var listPerson: MutableList<Person> = mutableListOf()
//    private var listTransaction: MutableList<Transaction> = mutableListOf()
//    private var totalBalance: Double = 0.0
//
//    constructor(preferences: PreferencesData, groupId: Long) {
//        val data = BackendService(preferences).getGroupVisualBarChart(groupId)
//        this.createPersonList(data)
//        this.calculateTotalBalance()
//        this.settleDebts()
//    }
//
//    private fun createPersonList(data: List<BarChartData>) {
//        for (item in data) {
//            this.listPerson.add(Person(item.data.first, item.data.second))
//        }
//    }
//
//    private fun calculateTotalBalance() {
//        this.totalBalance = 0.0
//        for (person in this.listPerson) {
//            totalBalance += kotlin.math.abs(person.getBalance())
//        }
//    }
//
//    private fun settleDebts() {
//        this.listTransaction.clear()
//
//        while(true) {
//            this.listPerson.sortedBy { -it.getBalance() }
//
//            var maxPositive: Person? = null
//            var maxNegative: Person? = null
//
//            for (p in this.listPerson) {
//                if (p.getBalance() > 0 && (maxPositive == null || p.getBalance() > maxPositive.getBalance())) {
//                    maxPositive = p
//                }
//                if (p.getBalance() < 0 && (maxNegative == null || p.getBalance() < maxNegative.getBalance())) {
//                    maxNegative = p
//                }
//            }
//
//            if (maxPositive == null || maxNegative == null) {
//                break
//            }
//
//            val amount = maxPositive.getBalance().coerceAtMost(-maxNegative.getBalance())
//            maxPositive.setBalance(maxPositive.getBalance() - amount)
//            maxNegative.setBalance(maxNegative.getBalance() + amount)
//            this.listTransaction.add(Transaction(maxNegative, maxPositive, amount))
//        }
//    }
//
//    fun getListTransaction(): MutableList<Transaction> {
//        return this.listTransaction
//    }
//}
