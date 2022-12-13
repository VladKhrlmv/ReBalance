package com.rebalance.ui.components.screens

import androidx.lifecycle.ViewModel
import com.rebalance.DummyBackend
import com.rebalance.DummyItem
import com.rebalance.DummyScale

class PersonalViewModel : ViewModel () {
    private val _tabItems: MutableList<DummyItem> = mutableListOf()
    val tabItems: List<DummyItem> = _tabItems

    init {
        _tabItems.addAll(DummyBackend().getValues(DummyScale.Day)) // TODO: get from arguments (or init in initializer)
    }

    fun updateTabItems(scaleType: DummyScale) {
        _tabItems.clear()
        _tabItems.addAll(DummyBackend().getValues(scaleType))
    }
}

// TODO: implement ViewModelFactory to create PersonalViewModel with arguments
//class PersonalViewModelFactory(val scaleType: DummyScale) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T =
//        modelClass.getConstructor(String::class.java)
//            .newInstance(scaleType)
//}
//
//private val model by viewModels<PersonalViewModel> {
//    PersonalViewModelFactory(DummyScale.Day)
//}
