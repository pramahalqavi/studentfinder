package model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class StudentDetailResponse(
    @SerialName("datastatuskuliah")
    val statusHistories: List<StatusHistory>?,
    @SerialName("datastudi")
    val studyHistories: List<StudyHistory>?,
    @SerialName("dataumum")
    val studentInfo: Info?
) {
    @Serializable
    data class StatusHistory(
        @SerialName("id_smt")
        val idSmt: String?,
        @SerialName("nm_stat_mhs")
        val nmStatMhs: String?,
        @SerialName("sks_smt")
        val sksSmt: Int?
    )

    @Serializable
    data class StudyHistory(
        @SerialName("id_smt")
        val idSmt: String?,
        @SerialName("kode_mk")
        val kodeMk: String?,
        @SerialName("nm_mk")
        val nmMk: String?,
        @SerialName("sks_mk")
        val sksMk: Int?
    )

    @Serializable
    data class Info(
        @SerialName("jk")
        val jk: String?,
        @SerialName("ket_keluar")
        val ketKeluar: String?,
        @SerialName("link_prodi")
        val linkProdi: String?,
        @SerialName("link_pt")
        val linkPt: String?,
        @SerialName("mulai_smt")
        val mulaiSmt: String?,
        @SerialName("namajenjang")
        val namajenjang: String?,
        @SerialName("namaprodi")
        val namaprodi: String?,
        @SerialName("namapt")
        val namapt: String?,
        @SerialName("nipd")
        val nipd: String?,
        @SerialName("nm_jns_daftar")
        val nmJnsDaftar: String?,
        @SerialName("nm_pd")
        val nmPd: String?,
        @SerialName("no_seri_ijazah")
        val noSeriIjazah: String?,
        @SerialName("reg_pd")
        val regPd: String?,
    )
}