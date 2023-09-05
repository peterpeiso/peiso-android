package i.library.base.tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.Glide
import i.library.base.utils.FileUtils
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException

/**
 * Created by hc. on 2020/11/5
 * Describe: 压缩文件
 */
class CompressFileTools(val context : Context) {

    private var isClear = false
    private val maxSize = 300
    private val toSize = 500

    private var mCompressVideoJob : Job ?= null
    private var mCompressImageJob : Job ?= null

    fun clear(){
        isClear = true
        mCompressVideoJob?.cancel()
        mCompressImageJob?.cancel()
    }

    fun toGetCompressFile(path: String,back: (file:File) -> Unit){
        toGetGiveRotationThread(arrayListOf(File(path))){
            if(!it.isNullOrEmpty()){
                back.invoke(it[0])
            }
        }
    }

    /** 图片压缩 **/
    private fun toGetGiveRotationThread(
        list: List<File>,
        back: (list: List<File>?) -> Unit
    ) {
        mCompressImageJob?.cancel()
        mCompressImageJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                toGetGiveRotation(list, back)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                /* 出错了 */
                back.invoke(null)
            }
        }
    }

    private fun toGetGiveRotation(
        list: List<File>,
        back: (list: List<File>) -> Unit
    ) {
        if (list.isEmpty()){
            back.invoke(list)
            return
        }
        var totalSize = 0L
        val array = java.util.ArrayList<File>()
        for (i in list.indices) {
            /* 开始时间 */
            val file = list[i]
            val bitmapDegree = getBitmapDegree(file)
            val fileSize = file.length() / 1024
            val scale = if(fileSize > maxSize) 1f * toSize / fileSize else 1f
            /** 获得新的图片 **/
            val aFile = if (bitmapDegree != 0) {
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.RGB_565
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(file.absolutePath,options)
                /** Bitmap Size **/
                val w = if(bitmapDegree%90 == 0) options.outHeight else options.outWidth
                val h = if(bitmapDegree%90 == 0) options.outWidth else options.outHeight
                val width = (w * scale).toInt()
                val height = (h * scale).toInt()
                /** Glide 压缩图片 **/
                val bitmap = Glide.with(context)
                    .asBitmap()
                    .load(file.absolutePath)
                    .submit(width, height)
                    .get()
                /** Bitmap 旋转 **/
                val matrix = Matrix()
                val rotateBitmap =
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                val path =
                    FileUtils.saveFile("file_" + System.currentTimeMillis() + ".jpg", rotateBitmap)
                val newFile = File(path)
                /** 再压缩一次 **/
                val aFile = if(newFile.length() / 1024 > maxSize){
                    toCompressBitmap(newFile)
                }else{
                    newFile
                }
                aFile
            } else {
                if(scale < 1){
                    val options = BitmapFactory.Options()
                    options.inPreferredConfig = Bitmap.Config.RGB_565
                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeFile(file.absolutePath,options)
                    /** Bitmap Size **/
                    val width = (options.outWidth * scale).toInt()
                    val height = (options.outHeight * scale).toInt()
                    /** Glide 压缩图片 **/
                    val bitmap = Glide.with(context)
                        .asBitmap()
                        .load(file.absolutePath)
                        .submit(width, height)
                        .get()
                    val path =
                        FileUtils.saveFile("file_" + System.currentTimeMillis() + ".jpg", bitmap)
                    File(path)
                }else{
                    file
                }
            }
            array.add(aFile)
            totalSize += (aFile.length()/1024)
        }
        back.invoke(array)
    }

    private fun toCompressBitmap(file : File) : File{
        val fileSize = file.length() / 1024
        val scale = if(fileSize > maxSize) 1f * toSize / fileSize else 1f
        return if(scale < 1.0f){
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.RGB_565
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(file.absolutePath,options)
            /** Bitmap Size **/
            val width = (options.outWidth * scale).toInt()
            val height = (options.outHeight * scale).toInt()
            /** Glide 压缩图片 **/
            val bitmap = Glide.with(context)
                .asBitmap()
                .load(file.absolutePath)
                .submit(width, height)
                .get()
            val path =
                FileUtils.saveFile("file_" + System.currentTimeMillis() + ".jpg", bitmap)
            File(path)
        }else{
            file
        }
    }

    /**
     * 读取图片的旋转的角度
     */
    fun getBitmapDegree(file: File): Int {
        var degree = 0
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            val exifInterface = ExifInterface(file.absolutePath)
            // 获取图片的旋转信息
            val orientation: Int = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return degree
    }
}