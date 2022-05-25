package did.pinbraerts.market

import android.content.Context
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.text.DecimalFormat
import java.util.*

object MarketData {
    const val ITEMS_FILE_NAME: String = "items.txt"
    private const val PREFERENCES_FILE_NAME: String = "preferences.txt"

    var palette: IntArray = IntArray(0)
    private val colorPreferences: HashMap<String, Int> = HashMap()
    private val format = DecimalFormat()

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

    fun savePreferences(context: Context, data: ArrayList<MarketItem>) =
        savePreferences(OutputStreamWriter(context.openFileOutput(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)), data)

    fun savePreferences(output: OutputStreamWriter, data: ArrayList<MarketItem>) {
        data.forEach {
            colorPreferences[it.name.toLowerCase(Locale.getDefault())] = it.color
        }
        colorPreferences.forEach { (name, color) ->
            output.write("$name:$color\n")
        }
        output.close()
    }

    fun preferenceOrItself(item: MarketItem) =
        colorPreferences.getOrElse(item.name.toLowerCase(Locale.getDefault())) { item.color }

    fun preferenceOrNull(item: MarketItem) =
        colorPreferences.getOrElse(item.name.toLowerCase(Locale.getDefault())) { null }

    fun format(number: Float): String =
        format.format(number)

    fun parse(string: String): Float =
        string.toFloatOrZero()
}
