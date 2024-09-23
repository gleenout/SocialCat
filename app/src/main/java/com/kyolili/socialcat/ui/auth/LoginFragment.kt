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

class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailEditText = view.findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = view.findViewById<EditText>(R.id.passwordEditText)
        val emailInputLayout = view.findViewById<TextInputLayout>(R.id.emailInputLayout)
        val passwordInputLayout = view.findViewById<TextInputLayout>(R.id.passwordInputLayout)
        val loginButton = view.findViewById<Button>(R.id.loginButton)
        val registerText = view.findViewById<TextView>(R.id.registerText)

        loginButton?.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Validação de entrada
            var isValid = true

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
                val activity = activity ?: return@setOnClickListener
                val intent = Intent(activity, HomeActivity::class.java)
                startActivity(intent)
                activity.finish()
            }
        }

        registerText?.setOnClickListener {
            (activity as? AuthActivity)?.showRegisterFragment()
        }
    }
}
