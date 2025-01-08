package com.example.firebasepam.model

data class Mahasiswa(
    val nim: String,
    val nama: String,
    val alamat: String,

//    @SerialName("jenis_kelamin")
    val jenis_kelamin: String,

    val kelas: String,
    val angkatan: String
) {
    constructor() : this("", "", "", "", "", "")
}