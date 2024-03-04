package screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import base.getAppBarColor
import base.getCardElevation
import base.getOnAppBarColor
import base.getSelectedOnAppBarColor
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import model.StudentDetail
import screenmodel.DetailScreenState
import screenmodel.StudentDetailScreenModel

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
      Column(
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
        Text("Student Detail")
      }
    )
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Composable
  private fun LoadedContainer(studentDetail: StudentDetail) {
    val tabIndex = mutableStateOf(0)
    val tabs = listOf("Status History", "Study History")
    LazyColumn (modifier = Modifier.fillMaxSize()) {
      item {
        Card(
          modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp).fillMaxWidth(),
          shape = MaterialTheme.shapes.medium,
          elevation = getCardElevation()
        ) {
          Column(modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 20.dp).fillMaxWidth()) {
            with(studentDetail.info) {
              InfoRow("Name", name)
              InfoRow("Gender", gender)
              InfoRow("Institution", institution)
              InfoRow("Major", major)
              InfoRow("Education level", educationLevel)
              InfoRow("Student ID", studentId)
              InfoRow("Initial semester", initialSemester)
              InfoRow("Initial student status", initialStudentStatus)
              InfoRow("Current student status", currentStudentStatus)
              InfoRow("Diploma number", diplomaNumber)
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
              TabRowDefaults.Indicator(
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
      Text(text = "No data")
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
          InfoRow("Semester", history.semesterId)
          InfoRow("Status", history.status)
          InfoRow("Credits", history.semesterCredits)
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
          InfoRow("Semester", history.semesterId)
          InfoRow("Subject code", history.subjectCode)
          InfoRow("Subject", history.subjectName)
          InfoRow("Credits", history.credits)
        }
      }
    }
}

  @Composable
  private fun Loading() {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      CircularProgressIndicator()
    }
  }

  @Composable
  private fun ErrorContainer(screenModel: StudentDetailScreenModel) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text("An error occured")
      Button(
        modifier = Modifier.padding(top = 16.dp),
        onClick = { screenModel.getStudentDetail(studentHash) }
      ) {
        Text("Retry")
      }
    }
  }
}