package com.kyolili.socialcat.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kyolili.socialcat.R
import com.kyolili.socialcat.databinding.FragmentProfileBinding
import android.app.AlertDialog
import android.widget.EditText
import android.widget.LinearLayout

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        setupObservers()
        setupClickListeners()

        // Define a imagem estática
        binding.imageProfile.setImageResource(R.drawable.bl_account_circle)
    }

    private fun setupObservers() {
        profileViewModel.userName.observe(viewLifecycleOwner) { userName ->
            binding.textProfileName.text = userName
        }

        profileViewModel.userEmail.observe(viewLifecycleOwner) { userEmail ->
            binding.textProfileEmail.text = userEmail
        }
    }

    private fun setupClickListeners() {
        binding.buttonEditProfile.setOnClickListener {
            showEditProfileDialog()
        }
    }

    private fun showEditProfileDialog() {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 30, 50, 30)
        }

        val nameEdit = EditText(requireContext()).apply {
            hint = "Nome"
            setText(profileViewModel.userName.value)
        }

        val emailEdit = EditText(requireContext()).apply {
            hint = "Email"
            setText(profileViewModel.userEmail.value)
        }

        layout.addView(nameEdit)
        layout.addView(emailEdit)

        AlertDialog.Builder(requireContext())
            .setTitle("Editar Perfil")
            .setView(layout)
            .setPositiveButton("Salvar") { _, _ ->
                val newName = nameEdit.text.toString().trim()
                val newEmail = emailEdit.text.toString().trim()

                if (newName.isBlank() || newEmail.isBlank()) {
                    Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                profileViewModel.updateProfile(newName, newEmail) { success ->
                    val message = if (success) {
                        "Perfil atualizado com sucesso!"
                    } else {
                        "Erro ao atualizar perfil. Tente novamente."
                    }
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}