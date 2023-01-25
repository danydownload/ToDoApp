package com.dt.learning.myapp2.ui.deleteallcompleted

import androidx.lifecycle.ViewModel
import com.dt.learning.myapp2.data.TaskDao
import com.dt.learning.myapp2.di.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/* Ci serve applicationScope e non semplicemente viewModel scope
 * perche' nel momento in cui clicchiamo si sul dialogo il fragment
 * viene cancellato e quindi la coroutine sarebbe cancellata pure.
 * Ci serve quindi uno scope piu' grande
 */

@HiltViewModel
class DeleteAllCompletedViewModel @Inject constructor(
    private val taskDao: TaskDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    fun onConfirmClick() = applicationScope.launch {
        taskDao.deleteCompletedTasks()
    }
}