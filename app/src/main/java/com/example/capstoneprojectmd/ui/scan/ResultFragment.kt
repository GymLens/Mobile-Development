package com.example.capstoneprojectmd.ui.scan

import android.graphics.drawable.BitmapDrawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.capstoneprojectmd.R
import com.example.capstoneprojectmd.databinding.FragmentResultBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class ResultFragment : Fragment() {
    private lateinit var binding: FragmentResultBinding
    private var exoPlayer: ExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResultBinding.inflate(inflater, container, false)

        val result = arguments?.getString("RESULT")
        val imageUri = arguments?.getString("IMAGE_URI")
        val videoUrl = arguments?.getString("VIDEO_URL")

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

        imageUri?.let {
            Log.d("ResultFragment", "Received Image URI: $it")

            val uri = Uri.parse(it)
            Glide.with(requireContext()).load(uri).placeholder(R.drawable.ic_place_holder).into(binding.resultImage)
        }

        videoUrl?.let {
            initializeExoPlayer(it)
        }

        binding.fabBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return binding.root
    }

    private fun initializeExoPlayer(videoUrl: String) {
        exoPlayer = ExoPlayer.Builder(requireContext()).build()

        val mediaItem = MediaItem.fromUri(videoUrl)
        exoPlayer?.setMediaItem(mediaItem)

        exoPlayer?.prepare()

        binding.videoPlayerView.player = exoPlayer
        binding.videoPlayerView.visibility = View.VISIBLE

        setVideoThumbnail(videoUrl)
    }

    private fun setVideoThumbnail(videoUrl: String) {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(videoUrl, HashMap())
            val bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            retriever.release()

            binding.videoPlayerView.useArtwork = true
            binding.videoPlayerView.defaultArtwork = BitmapDrawable(resources, bitmap)
        } catch (e: Exception) {
            Log.e("ResultFragment", "Error retrieving video thumbnail: ${e.message}")
        }
    }

    override fun onPause() {
        super.onPause()
        exoPlayer?.release()
        exoPlayer = null
    }

    companion object {
        fun newInstance(result: String, imageUri: String, videoUrl: String): ResultFragment {
            return ResultFragment().apply {
                arguments = Bundle().apply {
                    putString("RESULT", result)
                    putString("IMAGE_URI", imageUri)
                    putString("VIDEO_URL", videoUrl)
                }
            }
        }
    }
}