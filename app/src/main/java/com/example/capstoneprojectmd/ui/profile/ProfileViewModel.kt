package com.example.capstoneprojectmd.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import android.util.Log

class ProfileViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _profileImageUri = MutableLiveData<Uri?>()
    val profileImageUri: LiveData<Uri?> = _profileImageUri

    init {
        fetchCurrentUser()
    }

    fun fetchCurrentUser() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            _user.value = currentUser
            fetchProfileImage()
        } else {
            _errorMessage.value = "User not logged in"
        }
    }

    fun fetchProfileImage() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    val profilePicture = document.getString("profilePicture")
                    if (!profilePicture.isNullOrEmpty()) {
                        _profileImageUri.value = Uri.parse(profilePicture)
                        Log.d("ProfileViewModel", "Profile image URI fetched: $profilePicture")
                    } else {
                        _profileImageUri.value = null
                        Log.d("ProfileViewModel", "No profile image found")
                    }
                }
                .addOnFailureListener { exception ->
                    _errorMessage.value = "Failed to fetch profile image: ${exception.message}"
                    Log.e("ProfileViewModel", "Error fetching profile image", exception)
                }
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
        _user.value = null
    }

    fun setProfileImageUri(uri: Uri) {
        _profileImageUri.value = uri
        uploadProfilePicture(uri)
    }

    private fun uploadProfilePicture(uri: Uri) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val storageRef = firebaseStorage.reference.child("profile_pictures/${currentUser.uid}.jpg")

            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        updateProfilePicture(downloadUri.toString())
                    }.addOnFailureListener { exception ->
                        _errorMessage.value = "Failed to get download URL: ${exception.message}"
                        Log.e("ProfileViewModel", "Error getting download URL", exception)
                    }
                }
                .addOnFailureListener { exception ->
                    _errorMessage.value = "Failed to upload picture: ${exception.message}"
                    Log.e("ProfileViewModel", "Error uploading picture", exception)
                }
        }
    }

    private fun updateProfilePicture(imageUrl: String) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userRef = firestore.collection("users").document(currentUser.uid)
            userRef.update("profilePicture", imageUrl, "lastUpdated", FieldValue.serverTimestamp())
                .addOnSuccessListener {
                    _profileImageUri.postValue(Uri.parse(imageUrl))
                    Log.d("ProfileViewModel", "Profile picture updated successfully: $imageUrl")
                }
                .addOnFailureListener { exception ->
                    _errorMessage.value = "Failed to update profile picture: ${exception.message}"
                    Log.e("ProfileViewModel", "Error updating profile picture", exception)
                }
        }
    }
}
