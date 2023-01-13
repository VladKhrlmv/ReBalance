package com.rebalance

import android.content.Context
import android.os.Parcelable
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize
import java.io.FileInputStream
import java.io.FileOutputStream

class Preferences(
    private val context: Context
) {
    private val gson = Gson()
    private val fileName = "preferences.txt"
    private val serverIp = "192.168.56.1:8080"

    fun read(): PreferencesData {
        return try {
            val fin: FileInputStream = context.openFileInput(fileName)
            var a: Int
            val temp = StringBuilder()
            while (fin.read().also { a = it } != -1) {
                temp.append(a.toChar())
            }
            fin.close()

            gson.fromJson(temp.toString(), PreferencesData::class.java)
        } catch (e: Exception) {
            PreferencesData(this.serverIp, "","")
        }
    }

    fun write(data: PreferencesData): Boolean {
        return try {
            data.serverIp = this.serverIp
            val dataFormatted = gson.toJson(data)

            val fos: FileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            fos.write(dataFormatted.toByteArray())
            fos.flush()
            fos.close()

            true
        } catch (e: Exception) {
            false
        }
    }
}

@Parcelize
data class PreferencesData(
    var serverIp: String,
    val userId: String,
    var groupId: String
) : Parcelable {
    fun exists(): Boolean {
        return userId != null
    }
}
