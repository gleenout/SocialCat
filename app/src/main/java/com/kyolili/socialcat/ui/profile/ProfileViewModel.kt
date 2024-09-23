package com.kyolili.socialcat.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kyolili.socialcat.R

class ProfileViewModel : ViewModel() {

    // Dados fictícios do perfil
    private val _userName = MutableLiveData<String>().apply { value = "Usuário" }
    val userName: LiveData<String> get() = _userName

    private val _userEmail = MutableLiveData<String>().apply { value = "usuario@socialcat.com" }
    val userEmail: LiveData<String> get() = _userEmail

    private val _profilePicture = MutableLiveData<Int>().apply { value = R.drawable.bl_account_circle }
    val profilePicture: LiveData<Int> get() = _profilePicture

    // Métodos para atualizar dados fictícios (implementação futura)
    fun updateUserName(newName: String) {
        _userName.value = newName
    }

    fun updateUserEmail(newEmail: String) {
        _userEmail.value = newEmail
    }
}
