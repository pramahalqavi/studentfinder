package screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import base.getAppBarColor
import base.getCardElevation
import base.getOnAppBarColor
import base.getSelectedOnAppBarColor
import base.shimmerBrush
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import model.StudentDetail
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import screenmodel.DetailScreenState
import screenmodel.StudentDetailScreenModel
import studentfinder.composeapp.generated.resources.Res
import studentfinder.composeapp.generated.resources.credits
import studentfinder.composeapp.generated.resources.current_student_status
import studentfinder.composeapp.generated.resources.diploma_number
import studentfinder.composeapp.generated.resources.education_level
import studentfinder.composeapp.generated.resources.female
import studentfinder.composeapp.generated.resources.gender
import studentfinder.composeapp.generated.resources.generic_error_message
import studentfinder.composeapp.generated.resources.initial_semester
import studentfinder.composeapp.generated.resources.initial_student_status
import studentfinder.composeapp.generated.resources.institution
import studentfinder.composeapp.generated.resources.major
import studentfinder.composeapp.generated.resources.male
import studentfinder.composeapp.generated.resources.name
import studentfinder.composeapp.generated.resources.no_data
import studentfinder.composeapp.generated.resources.retry
import studentfinder.composeapp.generated.resources.semester
import studentfinder.composeapp.generated.resources.status
import studentfinder.composeapp.generated.resources.status_history
import studentfinder.composeapp.generated.resources.student_detail
import studentfinder.composeapp.generated.resources.student_id
import studentfinder.composeapp.generated.resources.study_history
import studentfinder.composeapp.generated.resources.subject
import studentfinder.composeapp.generated.resources.subject_code

@OptIn(ExperimentalResourceApi::class)
class StudentDetailScreen(private val studentHash: String) : Screen {

  @Composable
  override fun Content() {
    val screenModel = getScreenModel<StudentDetailScreenModel>()
    val state = screenModel.screenState.collectAsState()
    LaunchedEffect(true) {
      screenModel.getStudentDetail(studentHash)
    }
    Scaffold(
      topBar = { AppBar() }
    ) { innerPadding ->
      Box(
        modifier = Modifier.padding(
          top = innerPadding.calculateTopPadding()
        ).windowInsetsPadding(WindowInsets.navigationBars)
      ) {
        when (state.value) {
          is DetailScreenState.Loading -> Loading()
          is DetailScreenState.Loaded -> {
            (screenModel.screenState.value as DetailScreenState.Loaded).studentDetail?.let {
              LoadedContainer(it)
            }
          }
          is DetailScreenState.Error -> ErrorContainer(screenModel)
        }
      }
    }
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  private fun AppBar() {
    val navigator = LocalNavigator.current
    TopAppBar(
      navigationIcon = { IconButton(onClick = { navigator?.pop() }) {
        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, tint = getOnAppBarColor())
      } },
      colors = TopAppBarDefaults.topAppBarColors(
        containerColor = getAppBarColor(),
        titleContentColor = getOnAppBarColor()
      ),
      title = {
        Text(stringResource(Res.string.student_detail))
      }
    )
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Composable
  private fun LoadedContainer(studentDetail: StudentDetail) {
    val tabIndex = mutableStateOf(0)
    val tabs = listOf(stringResource(Res.string.status_history), stringResource(Res.string.study_history))
    LazyColumn (modifier = Modifier.fillMaxSize()) {
      item {
        Card(
          modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp).fillMaxWidth(),
          shape = MaterialTheme.shapes.medium,
          elevation = getCardElevation()
        ) {
          Column(modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 20.dp).fillMaxWidth()) {
            with(studentDetail.info) {
              InfoRow(stringResource(Res.string.name), name)
              InfoRow(stringResource(Res.string.gender), decodeGenderCode(gender))
              InfoRow(stringResource(Res.string.institution), institution)
              InfoRow(stringResource(Res.string.major), major)
              InfoRow(stringResource(Res.string.education_level), educationLevel)
              InfoRow(stringResource(Res.string.student_id), studentId)
              InfoRow(stringResource(Res.string.initial_semester), initialSemester)
              InfoRow(stringResource(Res.string.initial_student_status), initialStudentStatus)
              InfoRow(stringResource(Res.string.current_student_status), currentStudentStatus)
              InfoRow(stringResource(Res.string.diploma_number), diplomaNumber)
            }
          }
        }
      }
      stickyHeader {
        TabRow(
          selectedTabIndex = tabIndex.value,
          containerColor = getAppBarColor(),
          contentColor = getOnAppBarColor(),
          indicator = { tabPositions ->
            if (tabIndex.value < tabPositions.size) {
              TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[tabIndex.value]),
                color = getSelectedOnAppBarColor()
              )
            }
          }
        ) {
          tabs.forEachIndexed { index, title ->
            Tab(
              text = { Text(title) },
              selected = tabIndex.value == index,
              onClick = { tabIndex.value = index },
              selectedContentColor = getSelectedOnAppBarColor(),
              unselectedContentColor = getOnAppBarColor()
            )
          }
        }
      }
      when (tabIndex.value) {
        0 -> {
          if (studentDetail.statusHistories.isEmpty()) {
            item {
              EmptyContainer()
            }
          } else {
            itemsIndexed(studentDetail.statusHistories) { idx, item ->
              StatusHistoryItem(idx, item)
            }
          }
        }
        1 -> {
          if (studentDetail.studyHistories.isEmpty()) {
            item {
              EmptyContainer()
            }
          } else {
            itemsIndexed(studentDetail.studyHistories) { idx, item ->
              StudyHistoryItem(idx, item)
            }
          }
        }
      }
      item {
        Spacer(Modifier.height(16.dp))
      }
    }
  }

  @Composable
  private fun decodeGenderCode(genderCode: String): String {
    return when (genderCode) {
      "L" -> stringResource(Res.string.male)
      "P" -> stringResource(Res.string.female)
      else -> ""
    }
  }

  @Composable
  private fun InfoRow(label: String, value: String) {
    if (value.isBlank()) return
    Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp, start = 16.dp, end = 16.dp)) {
      Text(
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.weight(6f),
        text = label,
        textAlign = TextAlign.End,
        color = getAppBarColor()
      )
      Text(
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.weight(1f),
        text = ":",
        textAlign = TextAlign.Center,
        color = getAppBarColor()
      )
      Text(
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.weight(13f),
        text = value,
        textAlign = TextAlign.Start
      )
    }
  }

  @Composable
  private fun EmptyContainer() {
    Column(
      modifier = Modifier.fillMaxWidth().padding(vertical = 100.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = stringResource(Res.string.no_data))
    }
  }

  @Composable
  private fun StatusHistoryItem(index: Int, history: StudentDetail.StatusHistory) {
    Card(
      modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
        .fillMaxWidth(), shape = MaterialTheme.shapes.medium,
      elevation = getCardElevation()
    ) {
      Row( modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 20.dp)
        .fillMaxWidth()
      ) {
        Text(
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.weight(1f).padding(vertical = 12.dp),
          text = "${index + 1}",
          textAlign = TextAlign.Center,
          color = getAppBarColor()
        )
        Column(modifier = Modifier.weight(24f)) {
          InfoRow(stringResource(Res.string.semester), history.semesterId)
          InfoRow(stringResource(Res.string.status), history.status)
          InfoRow(stringResource(Res.string.credits), history.semesterCredits)
        }
      }
    }
  }

  @Composable
  private fun StudyHistoryItem(index: Int, history: StudentDetail.StudyHistory) {
    Card(
      modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
        .fillMaxWidth(), shape = MaterialTheme.shapes.medium,
      elevation = getCardElevation()
    ) {
      Row( modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 20.dp)
        .fillMaxWidth()
      ) {
        Text(
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.weight(1f).padding(vertical = 12.dp),
          text = "${index + 1}",
          textAlign = TextAlign.Center,
          color = getAppBarColor()
        )
        Column(modifier = Modifier.weight(24f)) {
          InfoRow(stringResource(Res.string.semester), history.semesterId)
          InfoRow(stringResource(Res.string.subject_code), history.subjectCode)
          InfoRow(stringResource(Res.string.subject), history.subjectName)
          InfoRow(stringResource(Res.string.credits), history.credits)
        }
      }
    }
}

  @Composable
  private fun Loading() {
    Column(modifier = Modifier.padding(top = 16.dp)) {
      SkeletonCard(this, 300.dp)
      SkeletonTab(this)
      SkeletonCard(this, 120.dp)
      SkeletonCard(this, 120.dp)
      SkeletonCard(this, 120.dp)
    }
  }

  @Composable
  private fun SkeletonTab(columnScope: ColumnScope) {
    columnScope.run {
      Box(modifier = Modifier.fillMaxWidth().height(72.dp).padding(bottom = 16.dp).background(shimmerBrush()))
    }
  }

  @Composable
  private fun SkeletonCard(columnScope: ColumnScope, height: Dp) {
    columnScope.run {
      Card(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp).fillMaxWidth().height(height),
        shape = MaterialTheme.shapes.medium,
        elevation = getCardElevation()
      ) {
        Box(modifier = Modifier.fillMaxSize().background(shimmerBrush()))
      }
    }
  }

  @Composable
  private fun ErrorContainer(screenModel: StudentDetailScreenModel) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(stringResource(Res.string.generic_error_message))
      Button(
        modifier = Modifier.padding(top = 16.dp),
        onClick = { screenModel.getStudentDetail(studentHash) }
      ) {
        Text(stringResource(Res.string.retry))
      }
    }
  }
}