package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentSearchResponse(
  @SerialName("mahasiswa")
  val students: List<Student>
) {
  @Serializable
  data class Student(
    @SerialName("text")
    val text: String? = null,
    @SerialName("website-link")
    val websiteLink: String? = null
  )
}