package did.pinbraerts.market

import android.content.Context
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object MarketData {
    private const val ITEMS_FILE_NAME: String = "items.txt"
    private const val PREFERENCES_FILE_NAME: String = "preferences.txt"

    val data: ArrayList<MarketItem> = arrayListOf()
    var palette: IntArray = IntArray(0)
    private val colorPreferences: HashMap<String, Int> = HashMap()

    fun loadPreferences(context: Context) {
        if (colorPreferences.isNotEmpty())
            return
        if (!context.getFileStreamPath(PREFERENCES_FILE_NAME).exists())
            return
        loadPreferences(InputStreamReader(context.openFileInput(PREFERENCES_FILE_NAME)))
    }

    fun loadPreferences(input: InputStreamReader) {
        input.useLines {
            it.forEach { line ->
                val a = line.split(':')
                if(a.size < 2)
                    return
                a[1].toIntOrNull()?.let { color ->
                    colorPreferences[a[0]] = color
                }
            }
        }
        input.close()
    }

    fun loadPalette(context: Context) {
        if(palette.isNotEmpty())
            return
        with(context.resources.obtainTypedArray(R.array.user_palette)) {
            try {
                palette = IntArray(length()) { getColor(it, 0) }
            }
            finally {
                recycle()
            }
        }
    }

    fun load(context: Context) {
        if(data.isNotEmpty())
            return
        if(!context.getFileStreamPath(ITEMS_FILE_NAME).exists())
            return
        load(InputStreamReader(context.openFileInput(ITEMS_FILE_NAME)))
    }

    fun load(reader: InputStreamReader) {
        reader.useLines {
            it.filter(String::isNotBlank).map(MarketItem::deserialize).forEach(data::add)
        }
        reader.close()
    }

    fun save(context: Context) =
        save(OutputStreamWriter(context.openFileOutput(ITEMS_FILE_NAME, Context.MODE_PRIVATE)))

    fun save(writer: OutputStreamWriter) {
        data.filter(MarketItem::isValid).forEach {
            writer.write(it.serialize() + '\n')
        }
        writer.close()
    }

    fun savePreferences(context: Context) =
        savePreferences(OutputStreamWriter(context.openFileOutput(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)))

    fun savePreferences(output: OutputStreamWriter) {
        data.forEach {
            colorPreferences[it.name] = it.color
        }
        colorPreferences.forEach { (name, color) ->
            output.write("$name:$color\n")
        }
        output.close()
    }

    fun preference(item: MarketItem) =
        colorPreferences.getOrElse(item.name.toLowerCase(Locale.getDefault())) { item.color }
}