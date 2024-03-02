package screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import model.Student
import screenmodel.HomeScreenModel
import screenmodel.HomeScreenState

class HomeScreen : Screen {
  @Composable
  override fun Content() {
    val screenModel = getScreenModel<HomeScreenModel>()
    Scaffold(topBar = {
      AppBar()
    }, content = { innerPadding ->
      Column(
        modifier = Modifier.padding(
          top = innerPadding.calculateTopPadding() + 4.dp
        )
      ) {
        val homeState = screenModel.screenState.collectAsState()
        SearchTextField(screenModel)
        when (homeState.value) {
          is HomeScreenState.Loading -> Loading()

          is HomeScreenState.Loaded -> {
            val loadedState = screenModel.screenState.value as HomeScreenState.Loaded
            if (loadedState.students.isEmpty() && screenModel.searchTextField.value.text.length > 1) {
              EmptySearchResult()
            } else {
              SuccessSearchResult(loadedState, screenModel)
            }
          }

          is HomeScreenState.Error -> ErrorSearchResult(screenModel)
        }
      }
    })
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  private fun AppBar() {
    TopAppBar(
      colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary,
        titleContentColor = MaterialTheme.colorScheme.onPrimary
      ),
      title = {
        Text("Student Finder")
      }
    )
  }

  @Composable
  private fun SearchTextField(screenModel: HomeScreenModel) {
    TextField(
      value = screenModel.searchTextField.value,
      shape = MaterialTheme.shapes.medium,
      maxLines = 1,
      leadingIcon = {
        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
      },
      label = {
        Text("Enter student name or ID")
      },
      onValueChange = {
        screenModel.searchTextField.value = it
        screenModel.findStudentWithDebounce(it.text)
      },
      modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 16.dp),
      colors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent
      )
    )
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
  private fun SuccessSearchResult(successState: HomeScreenState.Loaded, screenModel: HomeScreenModel) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(top = 8.dp)
    ) {
      items(successState.students) {
        StudentItem(it, screenModel)
      }
    }
  }

  @Composable
  private fun EmptySearchResult() {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text("No result")
    }
  }

  @Composable
  private fun StudentItem(student: Student, screenModel: HomeScreenModel) {
    val navigator = LocalNavigator.current
    Card(
      modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp).fillMaxWidth()
        .clickable {
          navigator?.push(StudentDetailScreen(student.hash))
        },
      shape = MaterialTheme.shapes.medium
    ) {
      Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        InfoRow("Name", student.name)
        InfoRow("Student ID", student.studentNumber)
        InfoRow("Institution", student.institution)
        InfoRow("Major", student.major)
      }
    }
  }

  @Composable
  private fun InfoRow(label: String, value: String) {
    if (value.isBlank()) return
    Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp, start = 16.dp, end = 16.dp)) {
      Text(style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(5f), text = label, textAlign = TextAlign.End)
      Text(style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), text = ":", textAlign = TextAlign.Center)
      Text(style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(14f), text = value, textAlign = TextAlign.Start)
    }
  }

  @Composable
  private fun ErrorSearchResult(screenModel: HomeScreenModel) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text("An error occured")
      Button(
        modifier = Modifier.padding(top = 16.dp),
        onClick = { screenModel.retrySearch() }
      ) {
        Text("Retry")
      }
    }
  }
}