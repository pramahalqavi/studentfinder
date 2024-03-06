package screenmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import model.Student
import repository.StudentRepository
import utils.SchedulerProvider

class HomeScreenModel(
  private val studentRepository: StudentRepository,
  private val schedulerProvider: SchedulerProvider
) : ScreenModel {

  private val _screenState: MutableStateFlow<HomeScreenState> = MutableStateFlow(HomeScreenState.Loaded())
  val screenState: StateFlow<HomeScreenState> = _screenState.asStateFlow()

  val searchTextField: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue(""))

  private var findStudentJob: Job? = null

  fun findStudentWithDebounce(searchTerm: String) {
    if (findStudentJob?.isActive == true) findStudentJob?.cancel()
    if (searchTerm.length <= 1) {
      _screenState.value = HomeScreenState.Loaded()
      return
    }
    findStudentJob = screenModelScope.launch {
      delay(1000L)
      findStudentApiCall(searchTerm)
    }
  }

  private suspend fun findStudentApiCall(searchTerm: String) {
    _screenState.value = HomeScreenState.Loading
    studentRepository.findStudent(searchTerm).flowOn(schedulerProvider.io()).collect { result ->
      if (result.isSuccess) {
        _screenState.value = HomeScreenState.Loaded(true, searchTerm, result.getOrDefault(emptyList()))
      } else {
        _screenState.value = HomeScreenState.Error(result.exceptionOrNull())
      }
    }
  }

  fun findStudent() {
    if (screenState.value is HomeScreenState.Loading ||
      (screenState.value is HomeScreenState.Loaded &&
          (screenState.value as HomeScreenState.Loaded).query == searchTextField.value.text)) return
    if (findStudentJob?.isActive == true) findStudentJob?.cancel()
    findStudentJob = screenModelScope.launch {
      findStudentApiCall(searchTextField.value.text)
    }
  }

  fun onClearTextField() {
    findStudentJob?.cancel()
    searchTextField.value = TextFieldValue()
    _screenState.value = HomeScreenState.Loaded()
  }
}

sealed class HomeScreenState {
  data object Loading : HomeScreenState()
  class Loaded(
    val isInitialized: Boolean = false,
    val query: String = "",
    val students: List<Student> = emptyList()
  ) : HomeScreenState()
  class Error(val throwable: Throwable?) : HomeScreenState()
}