package com.kyolili.socialcat.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.kyolili.socialcat.HomeActivity
import com.kyolili.socialcat.R
import com.kyolili.socialcat.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.registerButton.setOnClickListener {
            val email = binding.registerEmailEditText.text.toString().trim()
            val password = binding.registerPasswordEditText.text.toString().trim()
            val username = binding.registerUsernameEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                Toast.makeText(requireContext(), "Todos os campos são obrigatórios.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Desabilitar botão durante o registro
            binding.registerButton.isEnabled = false

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUser = auth.currentUser

                        // Atualizar o perfil do usuário com o nome
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build()

                        currentUser?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { profileTask ->
                                if (profileTask.isSuccessful) {
                                    // Criar objeto do usuário
                                    val user = User(
                                        uid = currentUser.uid,
                                        username = username,
                                        email = email
                                    )

                                    // Salvar no Firestore
                                    db.collection("users")
                                        .document(currentUser.uid)
                                        .set(user)
                                        .addOnSuccessListener {
                                            Toast.makeText(requireContext(), "Registro bem-sucedido!", Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(activity, HomeActivity::class.java))
                                            activity?.finish()
                                        }
                                        .addOnFailureListener { e ->
                                            binding.registerButton.isEnabled = true
                                            Toast.makeText(requireContext(), "Erro ao salvar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    binding.registerButton.isEnabled = true
                                    Toast.makeText(requireContext(), "Erro ao atualizar perfil: ${profileTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        binding.registerButton.isEnabled = true
                        Toast.makeText(requireContext(), "Erro no registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.loginText.setOnClickListener {
            (activity as? AuthActivity)?.showLoginFragment()
        }
    }
}