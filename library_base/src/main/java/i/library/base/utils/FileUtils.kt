package i.library.base.utils

import android.content.ContentResolver
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import i.library.base.base.BaseApplication
import i.library.base.expands.logo
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Created by hc. on 2020/4/23
 * Describe:
 */
object FileUtils {


    fun iReadDuration(path: String): Int{
        return try {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(path)
            val i = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            AMathUtils.toConvertInt(i)
        }catch (e: Exception){
            e.printStackTrace()
            0
        }
    }


    /**
     * 写入一个文件
     */
    fun writeFile(name : String,value : String){
        try {
            val path = "${BaseApplication.getInstance().getExternalFilesDir(null)?.absolutePath?:""}/app"
            val files = File(path)
            if (!files.exists()) {
                files.mkdirs()
            }
            val file = File(path, name)
            val createNewFile = file.createNewFile()
            if(createNewFile){
                val outStream = FileOutputStream(file)
                outStream.write(value.toByteArray())
                outStream.close()
            }
        }catch (e : java.lang.Exception){
            "E:$e".logo()
        }
    }

    /**
     * 创建文件
     **/
    fun createFile(name : String,dir: String = "create") : File?{
        try {
            val path = "${BaseApplication.getInstance().getExternalFilesDir(null)?.absolutePath?:""}/$dir"
            val files = File(path)
            if (!files.exists()) {
                files.mkdirs()
            }
            val file = File(path, name)
            if(file.exists()){
                file.delete()
            }
            file.createNewFile()
            return file
        }catch (e : java.lang.Exception){
            "E:$e".logo()
        }
        return null
    }

    fun saveFile(name: String , bitmap: Bitmap) : String{
        try {
            val path = "${BaseApplication.getInstance().getExternalFilesDir(null)?.absolutePath?:""}/app"
            val files = File(path)
            if (!files.exists()) {
                files.mkdirs()
            }
            val file = File(path, name)
            val isExists = if(file.exists()){
                true
            }else{
                file.createNewFile()
            }
            if(isExists){
                val outStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                outStream.close()
            }
            return file.absolutePath
        }catch (e : java.lang.Exception){
            "E:$e".logo()
            return ""
        }
    }

    /** 保存文件 **/
    fun saveFile(name : String,arrayByte :ByteArray) : String{
        try {
            val path = "${BaseApplication.getInstance().getExternalFilesDir(null)?.absolutePath?:""}/app"
            val files = File(path)
            if (!files.exists()) {
                files.mkdirs()
            }
            val file = File(path, name)
            val isExists = if(file.exists()){
                true
            }else{
                file.createNewFile()
            }
            if(isExists){
                val outStream = FileOutputStream(file)
                outStream.write(arrayByte)
                outStream.close()
            }
            return file.absolutePath
        }catch (e : java.lang.Exception){
            "E:$e".logo()
            return ""
        }
    }

    fun iSaveFile(inputStream: InputStream,name: String,dir:String):String?{
        val file = createFile(name, dir)
        file?.also {
            val fos = FileOutputStream(file)
            val bytes = ByteArray(1024)
            var byteCount = inputStream.read(bytes)
            while (byteCount != -1){
                fos.write(bytes,0,byteCount)
                byteCount = inputStream.read(bytes)
            }
            fos.flush()
            inputStream.close()
            fos.close()
        }
        return file?.absolutePath
    }

    fun getRealFilePath(uri: Uri?): String? {
        if (null == uri) {
            return null
        }
        uri.encodedPath
        val scheme = uri.scheme
        var data: String? = null
        if (scheme == null) {
            data = uri.path
        } else if (ContentResolver.SCHEME_FILE == scheme) {
            data = uri.path
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val cursor = BaseApplication.getInstance().contentResolver.query(
                uri,
                arrayOf(MediaStore.Images.ImageColumns.DATA),
                null,
                null,
                null
            )
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) {
                        data = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return data
    }
}