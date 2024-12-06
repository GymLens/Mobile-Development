package com.example.capstoneprojectmd.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.capstoneprojectmd.databinding.FragmentProfileBinding
import android.content.pm.PackageManager
import com.example.capstoneprojectmd.ui.password.ChangePasswordActivity
import com.example.capstoneprojectmd.ui.signin.SignInActivity
import java.io.File
import java.io.FileOutputStream

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val uri = data?.data
                uri?.let {
                    binding.profilePicture.setImageURI(it)
                    profileViewModel.setProfileImageUri(it)
                }
            }
        }

    private val takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val photo: Bitmap? = result.data?.extras?.get("data") as Bitmap
                photo?.let {
                    val uri = saveImageToStorage(it)
                    binding.profilePicture.setImageBitmap(it)
                    profileViewModel.setProfileImageUri(uri)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.profileImageUri.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                binding.profilePicture.setImageURI(it)
            }
        }

        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.userEmail.text = user.email ?: "Email tidak tersedia"
                binding.userName.text = user.displayName ?: "Nama tidak tersedia"
            } else {
                binding.userEmail.text = "Tidak ada pengguna yang login"
                binding.userName.text = ""
            }
        }

        binding.cameraIcon.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                showImagePickerOptions()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            }
        }

        binding.changePasswordButton.setOnClickListener {
            navigateToChangePassword()
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun navigateToChangePassword() {
        Toast.makeText(requireContext(), "Ganti Kata Sandi Diklik", Toast.LENGTH_SHORT).show()

        val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        Toast.makeText(requireContext(), "Keluar...", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePhotoLauncher.launch(intent)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        takePictureLauncher.launch(intent)
    }

    private fun showImagePickerOptions() {
        val options = arrayOf("Ambil Foto", "Pilih dari Galeri", "Batal")
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
                2 -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun saveImageToStorage(bitmap: Bitmap): Uri {
        val filename = "profile_picture_${System.currentTimeMillis()}.jpg"
        val file = File(requireContext().filesDir, filename)

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }

        return Uri.fromFile(file)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImagePickerOptions()
            } else {
                Toast.makeText(requireContext(), "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
