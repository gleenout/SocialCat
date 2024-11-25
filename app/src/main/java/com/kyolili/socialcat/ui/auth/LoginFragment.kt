package com.kyolili.socialcat.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.kyolili.socialcat.HomeActivity
import com.kyolili.socialcat.R
import com.kyolili.socialcat.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty()) {
                binding.emailEditText.error = "Digite seu email"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.passwordEditText.error = "Digite sua senha"
                return@setOnClickListener
            }

            binding.loginButton.isEnabled = false

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Verificar dados do usuário no Firestore
                        val currentUser = auth.currentUser
                        currentUser?.let { user ->
                            db.collection("users").document(user.uid)
                                .get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        Toast.makeText(requireContext(), "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(activity, HomeActivity::class.java))
                                        activity?.finish()
                                    } else {
                                        // Caso o documento do usuário não exista no Firestore
                                        Toast.makeText(requireContext(), "Erro: Dados do usuário não encontrados", Toast.LENGTH_SHORT).show()
                                        binding.loginButton.isEnabled = true
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(requireContext(), "Erro ao carregar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                                    binding.loginButton.isEnabled = true
                                }
                        }
                    } else {
                        binding.loginButton.isEnabled = true
                        when (task.exception) {
                            is FirebaseAuthInvalidUserException -> {
                                Toast.makeText(requireContext(),
                                    "Email não cadastrado. Por favor, verifique o email ou registre-se.",
                                    Toast.LENGTH_LONG).show()
                                binding.emailEditText.error = "Email não encontrado"
                            }
                            is FirebaseAuthInvalidCredentialsException -> {
                                Toast.makeText(requireContext(),
                                    "Senha incorreta. Por favor, tente novamente.",
                                    Toast.LENGTH_LONG).show()
                                binding.passwordEditText.error = "Senha incorreta"
                            }
                            else -> {
                                Toast.makeText(requireContext(),
                                    "Erro ao fazer login: ${task.exception?.message}",
                                    Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
        }

        binding.registerText.setOnClickListener {
            (activity as? AuthActivity)?.showRegisterFragment()
        }
    }
}