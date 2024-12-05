package com.example.capstoneprojectmd.helper

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.ByteOrder

class ImageClassifierHelper(private val context: Context) {

    private lateinit var interpreter: Interpreter
    private lateinit var labels: List<String>
    private val inputSize = 224
    private val inputPixelSize = 3

    private val videoUrls = mapOf(
        "Bench Press" to "https://github.com/GymLens/Exercise/raw/refs/heads/main/Bench%20Press/Bench%20Press.mp4",
        "Dip Bar" to "https://github.com/GymLens/Exercise/raw/refs/heads/main/Dip%20Bar/Chest%20Dip.mp4",
        "Dumbells" to "https://github.com/GymLens/Exercise/raw/refs/heads/main/Dumbbell.mp4",
        "Elliptical Machine" to "https://github.com/GymLens/Exercise/raw/refs/heads/main/Elliptical%20Machine.mp4",
        "KettleBell" to "https://github.com/GymLens/Exercise/raw/refs/heads/main/Kettlebell/Kettlebell_Deep%20Push%20Up.mp4",
        "Lat Pulldown" to "https://github.com/GymLens/Exercise/raw/refs/heads/main/Lateral%20Pulldown/Lat%20Pulldown_Cable%20Pulldown.mp4",
        "Leg Press Machine" to "https://github.com/GymLens/Exercise/raw/refs/heads/main/Leg%20Press%20Machine/Leg%20Press.mp4",
        "Pullbar" to "https://github.com/GymLens/Exercise/raw/refs/heads/main/Pull%20Bar/Pull%20Up.mp4",
        "Recumbent Bike" to "https://github.com/GymLens/Exercise/raw/refs/heads/main/Recumbent%20Bike.mp4",
        "Stair Climber" to "https://github.com/GymLens/Exercise/raw/refs/heads/main/Stair%20Climber.mp4",
        "Swiss Ball" to "https://github.com/GymLens/Exercise/raw/refs/heads/main/Swiss%20Ball/Swiss_Balls_Lying_Hip_Lift.mp4",
        "Treadmill" to "https://github.com/GymLens/Exercise/raw/refs/heads/main/Treadmill.mp4"
    )

    fun getVideoUrl(predictedLabel: String): String? {
        return videoUrls[predictedLabel]
    }

    private val labelDescriptions = mapOf(
        "Bench Press" to "Bench Press adalah latihan yang sangat efektif untuk menguatkan otot dada, bahu, dan triceps. Dengan menggunakan alat ini, Anda dapat meningkatkan kekuatan tubuh bagian atas dan membentuk tubuh yang lebih berotot. Latihan ini sangat penting dalam program kekuatan dan merupakan pilihan utama bagi mereka yang ingin meningkatkan performa di olahraga angkat beban.",
        "Dip Bar" to "Dip Bar adalah alat yang dirancang untuk melatih otot triceps, dada, dan bahu. Dengan melakukan gerakan menurunkan dan mengangkat tubuh, alat ini dapat membantu Anda mengembangkan kekuatan dan daya tahan otot bagian atas tubuh secara maksimal. Dip Bar sangat ideal untuk pemula maupun atlet berpengalaman yang ingin menambah variasi dalam rutinitas latihan.",
        "Dumbells" to "Dumbells adalah alat angkat beban yang sangat serbaguna dan dapat digunakan untuk berbagai latihan kekuatan dan daya tahan. Dengan dumbells, Anda dapat melatih hampir semua otot tubuh, baik untuk latihan isolasi maupun kombinasi. Alat ini cocok digunakan untuk meningkatkan kekuatan, fleksibilitas, serta memperbaiki postur tubuh secara keseluruhan.",
        "Elliptical Machine" to "Elliptical Machine adalah peralatan kardio yang menawarkan latihan penuh tubuh tanpa memberi dampak besar pada sendi. Cocok untuk sesi latihan dengan intensitas rendah hingga sedang, alat ini membantu meningkatkan stamina dan kebugaran secara keseluruhan. Dengan menggunakan mesin ini, Anda bisa merasakan latihan efektif untuk pembakaran kalori serta penguatan otot kaki, tangan, dan inti tubuh.",
        "KettleBell" to "KettleBell adalah alat latihan berbentuk bola dengan pegangan yang dirancang untuk latihan kekuatan dan daya tahan. Dengan kettlebell, Anda dapat melakukan berbagai latihan dinamis yang melibatkan banyak otot sekaligus. Alat ini sangat efektif dalam membangun otot, meningkatkan keseimbangan, serta melatih daya tahan tubuh. Kettlebell cocok digunakan oleh siapa saja, dari pemula hingga atlet berpengalaman.",
        "Lat Pulldown" to "Lat Pulldown adalah mesin yang sangat efektif untuk melatih otot punggung bagian atas, termasuk otot latissimus dorsi, biceps, dan bahu. Dengan menggunakan mesin ini, Anda dapat meningkatkan kekuatan punggung dan postur tubuh secara keseluruhan. Lat Pulldown membantu memperkuat otot punggung, sehingga mendukung kegiatan fisik sehari-hari dan olahraga yang membutuhkan gerakan tarik.",
        "Leg Press Machine" to "Leg Press Machine adalah alat yang dirancang untuk melatih otot kaki, terutama paha depan (quadriceps), paha belakang (hamstrings), dan glutes (otot bokong). Dengan mesin ini, Anda dapat melakukan latihan yang sangat efektif untuk memperkuat otot-otot kaki, meningkatkan mobilitas, serta memperbaiki keseimbangan tubuh. Alat ini sangat cocok untuk pemula maupun atlet yang ingin meningkatkan kekuatan kaki mereka.",
        "Pullbar" to "Pullbar adalah alat yang digunakan untuk latihan tarik tubuh ke atas, yang menargetkan otot punggung, lengan, dan bahu. Dengan menggunakan pullbar, Anda dapat melatih otot tubuh bagian atas secara efektif dan membangun kekuatan tubuh bagian atas. Latihan dengan pullbar sangat penting untuk meningkatkan kekuatan genggaman, postur tubuh, dan daya tahan.",
        "Recumbent Bike" to "Recumbent Bike adalah sepeda statis dengan posisi duduk yang nyaman dan sandaran punggung, membuatnya ideal untuk latihan kardio ringan hingga sedang. Alat ini dapat meningkatkan kebugaran jantung dan paru-paru tanpa memberi tekanan berlebih pada sendi kaki. Cocok untuk pemula, orang yang ingin melatih kebugaran kardiovaskular dengan nyaman, atau mereka yang ingin mengurangi risiko cedera.",
        "Stair Climber" to "Stair Climber mensimulasikan gerakan menaiki tangga, memberikan latihan yang sangat baik untuk memperkuat otot kaki, glutes, dan jantung. Latihan dengan Stair Climber dapat membantu membakar kalori secara efektif, meningkatkan stamina, serta menguatkan tubuh bagian bawah. Mesin ini cocok digunakan untuk latihan intensitas tinggi dengan manfaat yang sangat baik bagi kebugaran secara keseluruhan.",
        "Swiss Ball" to "Swiss Ball adalah bola besar yang digunakan untuk meningkatkan keseimbangan dan memperkuat otot inti tubuh, seperti otot perut dan punggung bawah. Dengan latihan menggunakan Swiss Ball, Anda dapat meningkatkan stabilitas tubuh, memperbaiki postur, dan mengurangi risiko cedera. Alat ini sangat efektif dalam meningkatkan fleksibilitas dan kekuatan tubuh bagian tengah.",
        "Treadmill" to "Treadmill adalah alat yang digunakan untuk latihan berjalan atau berlari di tempat, membantu meningkatkan kebugaran kardiovaskular dengan cara yang mudah dan efisien. Mesin ini memungkinkan Anda untuk berolahraga di dalam ruangan, sambil membakar kalori dan meningkatkan stamina. Treadmill sangat cocok untuk latihan pemanasan, latihan intensitas tinggi, maupun latihan kardio untuk penurunan berat badan."
    )

    init {
        try {
            setupImageClassifier()
            loadLabels()
        } catch (e: Exception) {
            throw RuntimeException("Initialization Error: ${e.message}")
        }
    }

    private fun setupImageClassifier() {
        interpreter = Interpreter(loadModelFile("gym_model.tflite"))
    }

    private fun loadModelFile(modelFile: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelFile)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
    }

    private fun loadLabels() {
        labels = try {
            FileUtil.loadLabels(context, "labels.txt")
        } catch (e: Exception) {
            throw RuntimeException("Error loading labels: ${e.message}")
        }
    }

    private fun bitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * inputPixelSize)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(inputSize * inputSize)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (pixelValue in intValues) {
            byteBuffer.putFloat(((pixelValue shr 16 and 0xFF) / 255.0f) * 2 - 1)
            byteBuffer.putFloat(((pixelValue shr 8 and 0xFF) / 255.0f) * 2 - 1)
            byteBuffer.putFloat(((pixelValue and 0xFF) / 255.0f) * 2 - 1)
        }
        return byteBuffer
    }

    fun classifyStaticImage(imageUri: Uri): String {
        return try {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                .copy(Bitmap.Config.ARGB_8888, true)

            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
            val inputBuffer = bitmapToByteBuffer(resizedBitmap)

            val outputBuffer = Array(1) { FloatArray(labels.size) }
            interpreter.run(inputBuffer, outputBuffer)

            val result = outputBuffer[0]
            val maxIndex = result.indices.maxByOrNull { result[it] } ?: -1
            val confidence = result[maxIndex] * 100

            if (maxIndex in labels.indices) {
                val predictedLabel = labels[maxIndex]
                val description = labelDescriptions[predictedLabel] ?: "Description not available"

                Log.d("ImageClassifierHelper", "Predicted label: $predictedLabel, Description: $description")

                "Predicted Class: $predictedLabel, Confidence: ${"%.2f".format(confidence)}%\n$description"
            } else {
                "Error: Predicted index out of range. Check labels or model output."
            }
        } catch (e: Exception) {
            "Error during classification: ${e.message}"
        }
    }
}