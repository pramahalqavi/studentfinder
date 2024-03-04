package screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import base.getAppBarColor
import base.getCardElevation
import base.getOnAppBarColor
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
    }, modifier = Modifier.imePadding(),
      content = { innerPadding ->
      Column(
        modifier = Modifier.padding(
          top = innerPadding.calculateTopPadding() + 4.dp
        ).windowInsetsPadding(WindowInsets.navigationBars)
      ) {
        val homeState = screenModel.screenState.collectAsState()
        SearchTextField(screenModel)
        when (homeState.value) {
          is HomeScreenState.Loading -> Loading()
          is HomeScreenState.Loaded -> {
            val loadedState = screenModel.screenState.value as HomeScreenState.Loaded
            if (loadedState.students.isEmpty() && loadedState.isInitialized) {
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
        containerColor = getAppBarColor(),
        titleContentColor = getOnAppBarColor()
      ),
      title = {
        Text("Student Finder")
      }
    )
  }

  @OptIn(ExperimentalComposeUiApi::class)
  @Composable
  private fun SearchTextField(screenModel: HomeScreenModel) {
    val keyboard = LocalSoftwareKeyboardController.current
    Card(
      modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
      elevation = getCardElevation()
    ) {
      TextField(
        value = screenModel.searchTextField.value,
        shape = MaterialTheme.shapes.medium,
        maxLines = 1,
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer
          )
        },
        trailingIcon = {
          if (screenModel.searchTextField.value.text.isNotEmpty()) {
            Icon(imageVector = Icons.Default.Clear,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.clickable {
                screenModel.onClearTextField()
              })
          }
        },
        label = {
          Text("Enter student name or ID")
        },
        onValueChange = {
          if (it.text != screenModel.searchTextField.value.text) {
            screenModel.findStudentWithDebounce(it.text)
          }
          screenModel.searchTextField.value = it
        },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
          focusedIndicatorColor = Color.Transparent,
          unfocusedIndicatorColor = Color.Transparent,
          disabledIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
          keyboard?.hide()
        })
      )
    }
  }

  @Composable
  private fun Loading() {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
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
        StudentItem(it)
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
  private fun StudentItem(student: Student) {
    val navigator = LocalNavigator.current
    Card(
      modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp).fillMaxWidth()
        .clickable {
          navigator?.push(StudentDetailScreen(student.hash))
        },
      shape = MaterialTheme.shapes.medium,
      elevation = getCardElevation()
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
      Text(
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.weight(5f),
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
        modifier = Modifier.weight(14f),
        text = value,
        textAlign = TextAlign.Start
      )
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