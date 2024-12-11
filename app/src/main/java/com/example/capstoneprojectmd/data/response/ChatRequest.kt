package com.example.capstoneprojectmd.data.response

import com.google.gson.annotations.SerializedName

data class ChatRequest(

	@field:SerializedName("safety_settings")
	val safetySettings: SafetySettings? = null,

	@field:SerializedName("contents")
	val contents: Contents? = null,

	@field:SerializedName("system_instruction")
	val systemInstruction: SystemInstruction? = null,

	@field:SerializedName("generation_config")
	val generationConfig: GenerationConfig? = null
)

data class Contents(

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("parts")
	val parts: Parts? = null
)

data class Parts(

	@field:SerializedName("text")
	val text: String? = null
)

data class GenerationConfig(

	@field:SerializedName("topK")
	val topK: Int? = null,

	@field:SerializedName("stopSequences")
	val stopSequences: List<Any?>? = null,

	@field:SerializedName("temperature")
	val temperature: Any? = null,

	@field:SerializedName("topP")
	val topP: Any? = null,

	@field:SerializedName("maxOutputTokens")
	val maxOutputTokens: Int? = null,

	@field:SerializedName("candidateCount")
	val candidateCount: Int? = null
)

data class SystemInstruction(

	@field:SerializedName("parts")
	val parts: List<PartsItem?>? = null
)

data class SafetySettings(

	@field:SerializedName("threshold")
	val threshold: String? = null,

	@field:SerializedName("category")
	val category: String? = null
)

data class PartsItem(

	@field:SerializedName("text")
	val text: String? = null
)
