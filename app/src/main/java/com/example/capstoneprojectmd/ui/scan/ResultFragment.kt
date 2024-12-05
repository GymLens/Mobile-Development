package com.example.capstoneprojectmd.ui.scan

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.FragmentResultBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class ResultFragment : Fragment() {
    private lateinit var binding: FragmentResultBinding
    private var exoPlayer: ExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentResultBinding.inflate(inflater, container, false)

        // Retrieve arguments passed to the fragment
        val result = arguments?.getString("RESULT")
        val imageUri = arguments?.getString("IMAGE_URI")
        val videoUrl = arguments?.getString("VIDEO_URL")

        // Handle result text and display it
        result?.let {
            val parts = it.split("\n")
            if (parts.size >= 2) {
                val nameConfidence = parts[0]
                val description = parts.getOrElse(1) { "No description available." }

                val nameConfidenceParts = nameConfidence.split(",")
                val name = nameConfidenceParts[0].substringAfter("Predicted Class:").trim()
                val confidence = nameConfidenceParts[1].substringAfter("Confidence:").trim()

                binding.resultName.text = "$name (Confidence: $confidence)"
                binding.resultDescription.text = description
            }
        }

        // Handle image URI and load the image with Glide
        imageUri?.let {
            Log.d("ResultFragment", "Received Image URI: $it") // Log the received URI

            val uri = Uri.parse(it)
            Glide.with(requireContext()).load(uri).placeholder(R.drawable.ic_place_holder).into(binding.resultImage)
        }

        // Show video using ExoPlayer
        videoUrl?.let {
            initializeExoPlayer(it)
        }

        // Back button click action (using Fragment navigation)
        binding.fabBack.setOnClickListener {
            requireActivity().onBackPressed() // Use onBackPressed to navigate back
        }

        return binding.root
    }

    private fun initializeExoPlayer(videoUrl: String) {
        // Create an ExoPlayer instance
        exoPlayer = ExoPlayer.Builder(requireContext()).build()

        // Prepare the video URL
        val mediaItem = MediaItem.fromUri(videoUrl)
        exoPlayer?.setMediaItem(mediaItem)

        // Prepare the player asynchronously
        exoPlayer?.prepare()

        // Bind the player to the PlayerView
        binding.videoPlayerView.player = exoPlayer
        binding.videoPlayerView.visibility = View.VISIBLE

        // Set a thumbnail for the video player view
        setVideoThumbnail(videoUrl)
    }

    private fun setVideoThumbnail(videoUrl: String) {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(videoUrl, HashMap()) // Video URL
            val bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC) // First frame
            retriever.release()

            // Set the thumbnail bitmap as the background of the PlayerView
            binding.videoPlayerView.useArtwork = true
            binding.videoPlayerView.defaultArtwork = android.graphics.drawable.BitmapDrawable(resources, bitmap)
        } catch (e: Exception) {
            Log.e("ResultFragment", "Error retrieving video thumbnail: ${e.message}")
        }
    }

    override fun onPause() {
        super.onPause()
        // Release the ExoPlayer when the fragment is paused to free resources
        exoPlayer?.release()
        exoPlayer = null
    }

    companion object {
        fun newInstance(result: String, imageUri: String, videoUrl: String): ResultFragment {
            val fragment = ResultFragment()
            val args = Bundle()
            args.putString("RESULT", result)
            args.putString("IMAGE_URI", imageUri)
            args.putString("VIDEO_URL", videoUrl)
            fragment.arguments = args
            return fragment
        }
    }
}
