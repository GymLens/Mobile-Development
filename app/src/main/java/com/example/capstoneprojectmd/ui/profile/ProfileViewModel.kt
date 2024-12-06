package com.example.capstoneprojectmd.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

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

    private fun fetchCurrentUser() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            _user.value = currentUser
        } else {
            _errorMessage.value = "User not logged in"
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
        _user.value = null
    }

    fun setProfileImageUri(uri: Uri) {
        _profileImageUri.value = uri
    }

    fun uploadProfilePicture(uri: Uri, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val storageRef: StorageReference = firebaseStorage.reference.child("profile_pictures/${currentUser.uid}.jpg")

            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        updateProfilePicture(downloadUri.toString(), onSuccess)
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure("Failed to upload picture: ${exception.message}")
                }
        } else {
            onFailure("User not logged in")
        }
    }

    private fun updateProfilePicture(imageUrl: String, onSuccess: () -> Unit) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userRef = firestore.collection("users").document(currentUser.uid)
            userRef.update("profilePicture", imageUrl, "lastUpdated", FieldValue.serverTimestamp())
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    _errorMessage.value = "Failed to update profile picture: ${exception.message}"
                }
        }
    }
}
