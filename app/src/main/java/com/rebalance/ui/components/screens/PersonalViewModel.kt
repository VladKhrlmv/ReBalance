package com.rebalance.ui.components.screens

import androidx.lifecycle.ViewModel
import com.rebalance.DummyBackend
import com.rebalance.DummyItem
import com.rebalance.DummyScale

class PersonalViewModel : ViewModel () {
    private val _tabItems = init()
    val tabItems: List<DummyItem> = _tabItems

    fun updateTabItems(scaleType: DummyScale) {
        _tabItems.clear()
        _tabItems.addAll(DummyBackend().getValues(scaleType))
    }

    fun init(): MutableList<DummyItem> {
        val list = mutableListOf<DummyItem>()
        list.addAll(DummyBackend().getValues(DummyScale.Day)) // TODO: get from arguments (or init in initializer)
        return list
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
