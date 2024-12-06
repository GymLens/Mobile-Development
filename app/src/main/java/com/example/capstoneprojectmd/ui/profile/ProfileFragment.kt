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
import androidx.navigation.fragment.findNavController
import com.example.capstoneprojectmd.R
import com.example.capstoneprojectmd.ui.password.ChangePasswordActivity
import com.example.capstoneprojectmd.ui.profile.ProfileViewModel
import com.example.capstoneprojectmd.ui.welcome.WelcomeActivity
import java.io.File
import java.io.FileOutputStream

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    // Launchers for camera and gallery
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val uri = data?.data
                uri?.let {
                    binding.profilePicture.setImageURI(it)
                    profileViewModel.setProfileImageUri(it) // Save URI to ViewModel
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
                    profileViewModel.setProfileImageUri(uri) // Save URI to ViewModel
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

        // Observe changes in profile image URI from ViewModel
        profileViewModel.profileImageUri.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                binding.profilePicture.setImageURI(it)
            }
        }

        // Observe user data from ViewModel
        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.userEmail.text = user.email ?: "Email not available"
                binding.userName.text = user.displayName ?: "Name not available"
            } else {
                binding.userEmail.text = "No user logged in"
                binding.userName.text = ""
            }
        }

        // Camera icon click listener
        binding.cameraIcon.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                showImagePickerOptions()
            } else {
                // Request camera permission if not granted
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            }
        }

        // Change password button
        binding.changePasswordButton.setOnClickListener {
            navigateToChangePassword()
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }
}

    private fun navigateToChangePassword() {
        // Show a Toast message when the button is clicked
        Toast.makeText(requireContext(), "Change Password Clicked", Toast.LENGTH_SHORT).show()

        // Start the ChangePasswordActivity
        val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        Toast.makeText(requireContext(), "Logging out...", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), WelcomeActivity::class.java)
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
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> openCamera() // Open camera to take a photo
                1 -> openGallery() // Open gallery to choose a photo
                2 -> dialog.dismiss() // Cancel
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
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
