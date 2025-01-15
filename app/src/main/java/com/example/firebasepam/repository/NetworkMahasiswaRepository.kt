package com.example.firebasepam.repository

import com.example.firebasepam.model.Mahasiswa
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NetworkMahasiswaRepository(
    private val firestore: FirebaseFirestore
) : MahasiswaRepository {
    override suspend fun getMahasiswa(): Flow<List<Mahasiswa>> = callbackFlow {
        val subscription = firestore.collection("Mahasiswa")
            .orderBy("nama", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    try {
                        val mhsList = snapshot.documents.mapNotNull {
                            it.toObject(Mahasiswa::class.java)
                        }
                        trySend(mhsList)
                    } catch (e: Exception) {
                        close(e)
                    }
                }
            }
        awaitClose {
            subscription.remove()
        }
    }

    override suspend fun insertMahasiswa(mahasiswa: Mahasiswa) {
        try {
            firestore.collection("Mahasiswa")
                .document(mahasiswa.nim) // Gunakan NIM sebagai document ID
                .set(mahasiswa)
                .await()
        } catch (e: Exception) {
            throw Exception("Gagal menambah data mahasiswa: ${e.message}")
        }
    }

    override suspend fun updateMahasiswa(mahasiswa: Mahasiswa) {
        try {
            val docRef = firestore.collection("Mahasiswa")
                .document(mahasiswa.nim)

            val doc = docRef.get().await()
            if (!doc.exists()) {
                throw Exception("Data mahasiswa tidak ditemukan")
            }

            docRef.set(mahasiswa).await()
        } catch (e: Exception) {
            throw Exception("Gagal mengupdate data mahasiswa: ${e.message}")
        }
    }

    override suspend fun deleteMahasiswa(mahasiswa: String) {
        try {
            firestore.collection("Mahasiswa")
                .document(mahasiswa)
                .delete()
                .await()
        } catch (e: Exception) {
            throw Exception("Gagal menghapus data mahasiswa: ${e.message}")
        }
    }

    override suspend fun getMahasiswaById(nim: String): Flow<Mahasiswa> = callbackFlow {
        val subscription = firestore.collection("Mahasiswa")
            .document(nim)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    try {
                        val mhs = snapshot.toObject(Mahasiswa::class.java)
                        if (mhs != null) {
                            trySend(mhs)
                        } else {
                            close(Exception("Gagal mengkonversi data mahasiswa"))
                        }
                    } catch (e: Exception) {
                        close(e)
                    }
                } else {
                    close(Exception("Data mahasiswa tidak ditemukan"))
                }
            }
        awaitClose {
            subscription.remove()
        }
    }
}