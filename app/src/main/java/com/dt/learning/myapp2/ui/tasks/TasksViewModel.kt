package com.dt.learning.myapp2.ui.tasks

import android.util.Log
import androidx.lifecycle.*
import com.dt.learning.myapp2.data.PreferencesManager
import com.dt.learning.myapp2.data.SortOrder
import com.dt.learning.myapp2.data.Task
import com.dt.learning.myapp2.data.TaskDao
import com.dt.learning.myapp2.ui.ADD_TASK_RESULT_OK
import com.dt.learning.myapp2.ui.EDIT_TASK_RESULT_OK
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao : TaskDao,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle
) : ViewModel() {
    // state with liveData ci permette di non dover gestire il set essendo gestito automaticamente
    // ci basta leggere il dato.
    val searchQuery = state.getLiveData("searchQuery", "")
    val preferencesFlow = preferencesManager.preferencesFlow

    // questo e' meglio non esporlo fuori dalla classe
    private val tasksEventChannel = Channel<TasksEvent>()
    // lo trasformiamo in flow cosi' dal fragment possiamo prenderci l'ultimo valore.
    val tasksEvent = tasksEventChannel.receiveAsFlow()

    private val taskFlow = combine(
        searchQuery.asFlow(),
        preferencesFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        taskDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

    val tasks = taskFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) =
        viewModelScope.launch {
            preferencesManager.updateSortOrder(sortOrder = sortOrder)
        }

    fun onHideCompletedClick(hideCompleted : Boolean) =
        viewModelScope.launch {
            preferencesManager.updateHideCompleted(hideCompleted = hideCompleted)
        }

    fun onTaskSelected(task: Task) {
        viewModelScope.launch {
            tasksEventChannel.send(TasksEvent.NavigateToEditTaskScreen(task))
        }
    }

    fun onTaskCheckedChanged(task: Task, isChecked : Boolean) {
        viewModelScope.launch {
            taskDao.update(task.copy(completed = isChecked))
        }
    }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        tasksEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    // eseguiamo coroutine perche' vogliamo lanciare un evento nel channel
    // e possiamo farlo solo in una coroutine
    fun onAddNewTaskClick() = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToAddTaskScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task updated")
        }
    }

    private fun showTaskSavedConfirmationMessage(text : String) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.ShowTaskSavedConfirmationMessage(text))
    }


    // rappresenta i tipi differenti di eventi che vogliamo mandare al fragment.
    // Ora sopra dobbiamo creare un channel a cui mandare questi eventi
    sealed class TasksEvent {
        // il renderlo object migliora l'efficenza. Fa si' che venga creato una sola volta.
        object NavigateToAddTaskScreen : TasksEvent()
        data class NavigateToEditTaskScreen(val task: Task) : TasksEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task) : TasksEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String) : TasksEvent()

    }
}



