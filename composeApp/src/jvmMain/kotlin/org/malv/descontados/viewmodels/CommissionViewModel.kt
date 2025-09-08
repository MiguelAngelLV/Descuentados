package org.malv.descontados.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.malv.descontados.models.Commission
import org.malv.descontados.services.CommissionService

class CommissionViewModel(
    private val commissionService: CommissionService
) : ViewModel() {

    private val _selected = MutableStateFlow("Seleccionar fichero")
    val selected: StateFlow<String> = _selected.asStateFlow()

    private val _commissions = MutableStateFlow(emptyList<Commission>())
    val commissions: StateFlow<List<Commission>> = _commissions.asStateFlow()

    fun onFileSelected(file: String) {
        _commissions.value = commissionService.getCommissions(file)
        _selected.value = file.substringAfterLast("/")
    }
}
