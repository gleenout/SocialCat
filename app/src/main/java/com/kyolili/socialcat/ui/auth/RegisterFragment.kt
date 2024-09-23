package com.kyolili.socialcat.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import androidx.fragment.app.Fragment
import com.kyolili.socialcat.HomeActivity
import com.kyolili.socialcat.R

class RegisterFragment : Fragment(R.layout.fragment_register) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameEditText = view.findViewById<EditText>(R.id.nameEditText)
        val emailEditText = view.findViewById<EditText>(R.id.registerEmailEditText)
        val passwordEditText = view.findViewById<EditText>(R.id.registerPasswordEditText)

        val nameInputLayout = view.findViewById<TextInputLayout>(R.id.nameInputLayout)
        val emailInputLayout = view.findViewById<TextInputLayout>(R.id.registerEmailInputLayout)
        val passwordInputLayout = view.findViewById<TextInputLayout>(R.id.registerPasswordInputLayout)

        val registerButton = view.findViewById<Button>(R.id.registerButton)
        val loginText = view.findViewById<TextView>(R.id.loginText) // Novo texto "Já tem conta? Entre"

        registerButton?.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            var isValid = true

            if (name.isEmpty()) {
                nameInputLayout.error = "O nome é obrigatório"
                isValid = false
            } else {
                nameInputLayout.error = null
            }

            if (email.isEmpty()) {
                emailInputLayout.error = "O email é obrigatório"
                isValid = false
            } else {
                emailInputLayout.error = null
            }

            if (password.isEmpty()) {
                passwordInputLayout.error = "A senha é obrigatória"
                isValid = false
            } else {
                passwordInputLayout.error = null
            }

            // Se todos os campos forem válidos, navegue para a HomeActivity
            if (isValid) {
                val intent = Intent(activity, HomeActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }

        // Navegar para o LoginFragment quando o texto "Já tem conta? Entre" for clicado
        loginText?.setOnClickListener {
            (activity as? AuthActivity)?.showLoginFragment()
        }
    }
}
