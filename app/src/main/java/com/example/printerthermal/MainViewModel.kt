package com.example.printerthermal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel() : ViewModel() {
    private val _listPrinter = MutableStateFlow<List<Print>>(mutableListOf())
    val listPrinter: StateFlow<List<Print>>
        get() = _listPrinter

    private val _printerSelected = MutableStateFlow(Print("", ""))
    val printerSelected: StateFlow<Print>
        get() = _printerSelected

    fun updatePrinterSelected(printer: Print) {
        _printerSelected.value = printer
    }

    fun updatePrinterList(listPrint: MutableList<Print>) {
        _listPrinter.value = listPrint
    }
}

class MainViewModelFactory(
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}