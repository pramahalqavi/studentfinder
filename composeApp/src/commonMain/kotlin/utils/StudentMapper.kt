package utils

import model.Student
import model.StudentDetail
import model.StudentDetailResponse
import model.StudentSearchResponse

fun StudentSearchResponse.toStudents(): List<Student> {
  return this.students.mapNotNull { studentResp ->
    if (studentResp.text.isNullOrBlank()) return@mapNotNull null
    val regex = Regex("""([\w\s]*)\((\d+)\s*\),\s+PT : ([\w\s]*),\s+Prodi:\s+([\w\s]*)""")
    val matchResult = regex.find(studentResp.text)
    matchResult?.let { match ->
      val (name, studentNumber, institution, major) = match.destructured
      return@mapNotNull Student(
        name,
        studentNumber,
        institution,
        major,
        studentResp.websiteLink.orEmpty()
      )
    } ?: run {
      return@mapNotNull null
    }
  }
}

fun StudentDetailResponse.toStudentDetail(): StudentDetail {
  return StudentDetail(
    info = StudentDetail.Info(
      name = studentInfo?.nmPd.orEmpty(),
      initialSemester = semesterIdToSemesterYear(studentInfo?.mulaiSmt.orEmpty()),
      studentId = studentInfo?.nipd.orEmpty(),
      gender = if (studentInfo?.jk == "L") "Male" else if (studentInfo?.jk == "P") "Female" else "",
      major = studentInfo?.namaprodi.orEmpty(),
      educationLevel = studentInfo?.namajenjang.orEmpty(),
      institution = studentInfo?.namapt.orEmpty(),
      initialStudentStatus = studentInfo?.nmJnsDaftar.orEmpty(),
      currentStudentStatus = studentInfo?.ketKeluar.orEmpty(),
      diplomaNumber = studentInfo?.noSeriIjazah.orEmpty()
    ),
    studyHistories = studyHistories?.map { history ->
      return@map StudentDetail.StudyHistory(
        semesterId = semesterIdToSemesterYear(history.idSmt.orEmpty()),
        subjectCode = history.kodeMk.orEmpty(),
        subjectName = history.nmMk.orEmpty(),
        credits = history.sksMk?.let { "$it" } ?: ""
      )
    }.orEmpty(),
    statusHistories = statusHistories?.map { history ->
      return@map StudentDetail.StatusHistory(
        semesterId = semesterIdToSemesterYear(history.idSmt.orEmpty()),
        status = history.nmStatMhs.orEmpty(),
        semesterCredits = history.sksSmt?.let { "$it" } ?: ""
      )
    }.orEmpty()
  )
}

fun semesterIdToSemesterYear(semesterId: String): String {
  if (semesterId.length <= 4) return semesterId
  return "${semesterId[semesterId.length - 1]} - ${semesterId.substring(0, semesterId.length - 1)}"
}