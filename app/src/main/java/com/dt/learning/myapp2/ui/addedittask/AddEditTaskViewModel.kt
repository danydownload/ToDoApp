package com.dt.learning.myapp2.ui.addedittask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dt.learning.myapp2.data.Task
import com.dt.learning.myapp2.data.TaskDao
import com.dt.learning.myapp2.ui.ADD_TASK_RESULT_OK
import com.dt.learning.myapp2.ui.EDIT_TASK_RESULT_OK
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* C'e' un secondo tipo di distruzione che puo' avvenire oltre a quella di girare lo schermo
* ed e' process death. Mettiamo l'app in background per fare altro ed android puo' distruggere
* tutto del nostro programma, anche i viewmodel, per ottimizzare la memoria, quindi dobbiamo gestire
* questa cosa. In SavedInstanceState possiamo mettere piccole quantita' di informazioni che sopravviveranno
*/
@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val state: SavedStateHandle // dagger lo injecta automaticamente
    /*
     * With the new @HiltViewModel you can have the SavedStateHandle as a dependency without having
     * to annotate it with @Assisted. The SavedStateHandle is now a binding from the new ViewModelComponent,
     * so it doesn't need to be assist-injected anymore.
     */
    // @Assisted private val state: SavedStateHandle // dagger lo injecta automaticamente

) : ViewModel() {

    val task = state.get<Task>("task") // e' lo stesso mes o nel nav fragment

    // vogliamo splittare task. Vogliamo creare variabili diverse per titolo e per l'importanza
    var taskName = state.get<String>("taskName") ?: task?.name ?: "" // ?: prende valore alla sua destra se null
        set(value) {
            field = value
            state.set("taskName", value)
        }

    var taskImportance = state.get<Boolean>("taskImportance") ?: task?.important ?: false
        set(value) {
            field = value
            state.set("taskImportance", value)
        }

    private val addEditTaskEventChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEvent = addEditTaskEventChannel.receiveAsFlow()

    fun onSaveClick() {
        if (taskName.isBlank()) {
            // show invalid input message
            showInvalidInputMessage("Name cannot be empty")
            return
        }

        if (task != null) {
            val updatedTask = task.copy(name = taskName, important = taskImportance)
            updateTask(updatedTask)
        } else {
            val newTask = Task(name = taskName, important = taskImportance)
            createTask(newTask)
        }
    }

    private fun createTask(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
        // navigate back
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun updateTask(task: Task) = viewModelScope.launch {
        taskDao.update(task)
        // navigate back
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        addEditTaskEventChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(text))
    }

    sealed class AddEditTaskEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditTaskEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditTaskEvent()
    }


}