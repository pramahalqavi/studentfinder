package screenmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import model.StudentDetail
import repository.StudentRepository
import utils.SchedulerProvider

class StudentDetailScreenModel(
  private val studentRepository: StudentRepository,
  private val schedulerProvider: SchedulerProvider
) : ScreenModel {

  private val _screenState: MutableStateFlow<DetailScreenState> = MutableStateFlow(DetailScreenState.Loaded())
  val screenState = _screenState.asStateFlow()

  fun getStudentDetail(studentHash: String) {
    _screenState.value = DetailScreenState.Loading
    studentRepository.getStudentDetail(studentHash)
      .flowOn(schedulerProvider.io()).onEach { result ->
        if (result.isSuccess) {
          _screenState.value = DetailScreenState.Loaded(result.getOrNull())
        } else {
          _screenState.value = DetailScreenState.Error(result.exceptionOrNull())
        }
    }.launchIn(screenModelScope)
  }
}

sealed class DetailScreenState {
  data object Loading : DetailScreenState()
  class Loaded(val studentDetail: StudentDetail? = null) : DetailScreenState()
  class Error(val throwable: Throwable?) : DetailScreenState()
}