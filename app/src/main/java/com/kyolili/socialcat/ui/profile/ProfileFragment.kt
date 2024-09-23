package com.kyolili.socialcat.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kyolili.socialcat.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializando o ProfileViewModel
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        // Observa as mudanças no nome do usuário e outros dados fictícios
        profileViewModel.userName.observe(viewLifecycleOwner) { userName ->
            binding.textProfileName.text = userName
        }

        profileViewModel.userEmail.observe(viewLifecycleOwner) { userEmail ->
            binding.textProfileEmail.text = userEmail
        }

        profileViewModel.profilePicture.observe(viewLifecycleOwner) { profilePicture ->
            binding.imageProfile.setImageResource(profilePicture)
        }

        // Lógica para editar o perfil (a ser implementada posteriormente)
        binding.buttonEditProfile.setOnClickListener {
            // Função de edição de perfil futura
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
