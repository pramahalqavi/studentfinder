package repository

import utils.Constants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.encodeURLPathPart
import kotlinx.coroutines.flow.flow
import model.StudentDetailResponse
import model.StudentSearchResponse
import utils.toStudentDetail
import utils.toStudents

class StudentRepository(private val httpClient: HttpClient) {
  companion object {
    private const val STUDENT_SEARCH_PATH = "/hit_mhs/"
    private const val STUDENT_DETAIL_PATH = "/detail_mhs/"
  }

  fun findStudent(searchTerm: String) = flow {
    try {
      val url = "${Constants.BASE_URL}${STUDENT_SEARCH_PATH}${searchTerm.encodeURLPathPart()}"
      println("Hitting ${url}")
      val response = httpClient.get(url).body<StudentSearchResponse>()
      println("success $response")
      emit(Result.success(response.toStudents()))
    } catch (e: Exception) {
      println("error $e")
      emit(Result.failure(e))
    }
  }

  fun getStudentDetail(studentHash: String) = flow {
    try {
      val url = "${Constants.BASE_URL}${STUDENT_DETAIL_PATH}${studentHash}"
      println("Hitting ${url}")
      val response = httpClient.get(url).body<StudentDetailResponse>()
      println("success $response")
      emit(Result.success(response.toStudentDetail()))
    } catch (e: Exception) {
      println("error $e")
      emit(Result.failure(e))
    }
  }
}