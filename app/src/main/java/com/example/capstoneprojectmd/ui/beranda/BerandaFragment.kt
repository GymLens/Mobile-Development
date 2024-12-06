package com.example.capstoneprojectmd.ui.beranda

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.capstoneprojectmd.R
import com.example.capstoneprojectmd.databinding.FragmentBerandaBinding

class BerandaFragment : Fragment() {

    private var _binding: FragmentBerandaBinding? = null
    private val binding get() = _binding!!

    private var userName: String? = null

    companion object {
        fun newInstance(userName: String): BerandaFragment {
            val fragment = BerandaFragment()
            val args = Bundle()
            args.putString("USER_NAME", userName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBerandaBinding.inflate(inflater, container, false)
        // Retrieve the fullName from the arguments passed via the Bundle
        val fullName = arguments?.getString("USER_NAME") ?: "Guest"

        // Display fullName in a TextView (or wherever you need it)
        binding.userNameTextView.text = "Hi, $fullName!"

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
