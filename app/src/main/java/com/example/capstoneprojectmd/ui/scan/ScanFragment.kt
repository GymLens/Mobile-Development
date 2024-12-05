package com.example.capstoneprojectmd.ui.scan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.capstoneprojectmd.databinding.FragmentScanBinding
import com.example.capstoneprojectmd.helper.ImageClassifierHelper
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ScanFragment : Fragment() {
    private lateinit var binding: FragmentScanBinding
    private lateinit var imageClassifier: ImageClassifierHelper
    private val imageViewModel: ImageViewModel by viewModels()
    private lateinit var currentPhotoUri: Uri

    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "Camera permission granted.")
            startCamera()
        } else {
            Log.w(TAG, "Camera permission denied.")
            showToast("Camera permission denied.")
        }
    }

    private val cameraResultLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            Log.d(TAG, "Camera capture success. URI: $currentPhotoUri")
            startCropActivity(currentPhotoUri)
        } else {
            Log.e(TAG, "Camera capture failed.")
            showToast("Image capture failed.")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentScanBinding.inflate(inflater, container, false)

        try {
            imageClassifier = ImageClassifierHelper(requireContext())
            Log.d(TAG, "ImageClassifierHelper initialized successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing ImageClassifierHelper: ${e.message}")
            showToast("Error initializing classifier.")
            return binding.root
        }

        imageViewModel.imageUri.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                Log.d(TAG, "Image URI updated: $uri")
                showImage(it)
            }
        }

        binding.galleryButton.setOnClickListener {
            Log.d(TAG, "Gallery button clicked.")
            startGallery()
        }

        binding.cameraButton.setOnClickListener {
            Log.d(TAG, "Camera button clicked.")
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Log.d(TAG, "Requesting camera permission.")
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        binding.analyzeButton.setOnClickListener {
            imageViewModel.imageUri.value?.let {
                Log.d(TAG, "Analyze button clicked. URI: $it")
                analyzeImage(it)
            } ?: run {
                Log.w(TAG, "No image selected for analysis.")
                showToast("Please select an image first.")
            }
        }

        return binding.root
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    private fun startCamera() {
        val photoFile = File(requireContext().externalCacheDir, "photo_${System.currentTimeMillis()}.jpg")
        currentPhotoUri = FileProvider.getUriForFile(requireContext(), "$requireActivity().packageName.fileprovider", photoFile)
        cameraResultLauncher.launch(currentPhotoUri)
    }

    private fun startCropActivity(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_image_${System.currentTimeMillis()}.jpg"))
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(800, 800)
            .withOptions(UCrop.Options().apply { setFreeStyleCropEnabled(true) })
            .start(requireContext(), this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_GALLERY -> if (resultCode == RESULT_OK) {
                val uri = data?.data
                uri?.let { startCropActivity(it) }
            }
            UCrop.REQUEST_CROP -> if (resultCode == RESULT_OK) {
                val resultUri = UCrop.getOutput(data ?: return)
                resultUri?.let {
                    imageViewModel.setImageUri(it)
                    showImage(it)
                }
            }
        }
    }

    private fun showImage(uri: Uri) {
        binding.previewImageView.setImageURI(uri)
    }

    private fun analyzeImage(uri: Uri) {
        showProgressBar(true)
        Log.d(TAG, "Starting image analysis...")

        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    imageClassifier.classifyStaticImage(uri)
                }

                val predictedLabel = result.split(",")[0].substringAfter("Predicted Class: ").trim()
                val videoUrl = imageClassifier.getVideoUrl(predictedLabel)

                if (videoUrl != null) {
                    Log.d(TAG, "Analysis successful. Predicted Label: $predictedLabel")

                    // Pindahkan ke ResultFragment
                    val resultFragment = ResultFragment.newInstance(result, uri.toString(), videoUrl)
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, resultFragment)
                        .addToBackStack(null)
                        .commit()

                } else {
                    Log.w(TAG, "No video URL found for predicted label.")
                    showToast("Video for the predicted class not found.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during image analysis: ${e.message}")
                showToast("Error during analysis.")
            } finally {
                showProgressBar(false)
                Log.d(TAG, "Image analysis completed.")
            }
        }
    }


    private fun showProgressBar(isVisible: Boolean) {
        binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "ScanFragment"
        private const val REQUEST_GALLERY = 100
    }
}
