package com.example.capstoneprojectmd.data.response

import com.example.capstoneprojectmd.data.model.Chat
import com.google.gson.annotations.SerializedName

data class ChatContentResponse(

    @field:SerializedName("candidates")
    val candidates: List<CandidatesItemResponse?>? = null,

    )
data class ContentResponse(

    @field:SerializedName("parts")
    val parts: List<PartsItemResponse>? = null
)
data class CandidatesItemResponse(

    @field:SerializedName("content")
    val content: ContentResponse? = null
)

data class PartsItemResponse(

    @field:SerializedName("text")
    val text: String? = null
)


fun ChatContentResponse.toData()=
    Chat(
        this.candidates?.first()?.content?.parts?.first()?.text?:"", true
    )



