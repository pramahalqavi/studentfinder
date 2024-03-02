package model

data class StudentDetail(
  val info: Info,
  val studyHistories: List<StudyHistory>,
  val statusHistories: List<StatusHistory>
) {
  data class Info(
    val name: String,
    val initialSemester: String,
    val studentId: String,
    val gender: String,
    val major: String,
    val educationLevel: String,
    val institution: String,
    val initialStudentStatus: String,
    val currentStudentStatus: String,
    val diplomaNumber: String
  )

  data class StudyHistory(
    val semesterId: String,
    val subjectCode: String,
    val subjectName: String,
    val credits: String
  )

  data class StatusHistory(
    val semesterId: String,
    val status: String,
    val semesterCredits : String
  )
}