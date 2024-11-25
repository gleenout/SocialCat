package com.kyolili.socialcat.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail

    private val _photoUrl = MutableLiveData<String>()
    val photoUrl: LiveData<String> = _photoUrl

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val user = auth.currentUser
        _userName.value = user?.displayName ?: "Usuário"
        _userEmail.value = user?.email ?: ""
        _photoUrl.value = user?.photoUrl?.toString()
    }

    fun updateProfile(newName: String, newEmail: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser

                // Atualiza email se foi alterado
                if (newEmail != user?.email) {
                    user?.updateEmail(newEmail)?.await()
                }

                // Atualiza nome no Auth
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build()
                user?.updateProfile(profileUpdates)?.await()

                // Atualiza dados no Firestore
                firestore.collection("users")
                    .document(user?.uid ?: "")
                    .update(mapOf(
                        "name" to newName,
                        "email" to newEmail
                    )).await()

                // Atualiza LiveData
                _userName.value = newName
                _userEmail.value = newEmail

                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    fun updateProfilePhoto(photoUrl: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser

                // Atualiza foto no Auth
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(photoUrl))
                    .build()
                user?.updateProfile(profileUpdates)?.await()

                // Atualiza foto no Firestore
                firestore.collection("users")
                    .document(user?.uid ?: "")
                    .update("photoUrl", photoUrl)
                    .await()

                // Atualiza LiveData
                _photoUrl.value = photoUrl

                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }
}