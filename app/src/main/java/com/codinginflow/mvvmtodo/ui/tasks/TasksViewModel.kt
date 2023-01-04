package com.codinginflow.mvvmtodo.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.codinginflow.mvvmtodo.data.PreferencesRepository
import com.codinginflow.mvvmtodo.data.SortOrder
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch


class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    val searchQuery = MutableStateFlow("")

    //    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
//    val hideCompleted = MutableStateFlow(false)
    val preferencesFlow = preferencesRepository.preferencesFLow

    private val taskFlow =
        combine(searchQuery, preferencesFlow) { query, filteredPreferences ->
            Pair(query, filteredPreferences)
        }
            .flatMapLatest {
                taskDao.getTasks(it.first, it.second.sortOrder, it.second.hideCompleted)
            }

    val tasks = taskFlow.asLiveData() //taskDao.getTasks("").asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesRepository.updateSortOrder(sortOrder)
    }

    fun onHideCompleted(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesRepository.updateHideCompleted(hideCompleted)
    }


    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = isChecked))
    }

    fun onTaskSelected(task: Task) {

    }

}