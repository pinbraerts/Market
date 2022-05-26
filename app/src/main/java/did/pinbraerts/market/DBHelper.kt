package did.pinbraerts.market

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(val context: Context):
    SQLiteOpenHelper(context, context.getString(R.string.db_name), null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(context.getString(
            R.string.cmd_create_preferences,
            context.getString(R.string.tb_preferences)
        ))
        db.execSQL(context.getString(
            R.string.cmd_create_snapshot,
            context.getString(R.string.tb_snapshot)
        ))
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(context.getString(
            R.string.cmd_drop_table,
            context.getString(R.string.tb_preferences)
        ))
        db.execSQL(context.getString(
            R.string.cmd_drop_table,
            context.getString(R.string.tb_snapshot)
        ))
        onCreate(db)
    }

    fun clear() {
        readableDatabase.delete(
            context.getString(R.string.tb_preferences), null, null
        )
        readableDatabase.delete(
            context.getString(R.string.tb_snapshot), null, null
        )
    }

    fun readPreferences(f: (Pair<String, Int>) -> Unit) =
        readableDatabase.rawQuery(context.getString(
            R.string.cmd_select_all,
            context.getString(R.string.tb_preferences)
        ), null).use { c ->
            if (c.moveToFirst())
                do f(
                    c.getString(0) to // c.getString(c.getColumnIndex("name")) to
                    c.getInt(1) // c.getInt(c.getColumnIndex("color"))
                ) while (c.moveToNext())
        }

    fun writePreferences(preferences: HashMap<String, Int>) =
        preferences.forEach { (name, color) ->
            val cv = ContentValues()
            cv.put("name", name)
            cv.put("color", color)
            writableDatabase.insertWithOnConflict(
                context.getString(R.string.tb_preferences),
                null, cv, SQLiteDatabase.CONFLICT_REPLACE
            )
        }

    fun readSnapshot(f: (MarketItem) -> Unit) =
        readableDatabase.rawQuery(context.getString(
            R.string.cmd_select_all,
            context.getString(R.string.tb_preferences)
        ), null).use { c ->
            if (c.moveToFirst())
                do f(
                    MarketItem(
                        c.getString(0), // c.getColumnIndex("name")),
                        c.getString(1), // c.getColumnIndex("amount")),
                        c.getFloat(2), // c.getColumnIndex("weight")),
                        c.getFloat(3), // c.getColumnIndex("price")),
                        c.getFloat(4), // c.getColumnIndex("cost")),
                        c.getInt(5) // c.getColumnIndex("color"))
                )) while (c.moveToNext())
        }

    fun writeSnapshot(items: MarketItems) =
        items.forEach {
            val cv = ContentValues()
            cv.put("name", it.name)
            cv.put("amount", it.amount)
            cv.put("weight", it.weight)
            cv.put("price", it.price)
            cv.put("cost", it.cost)
            cv.put("color", it.color)
            writableDatabase.insertWithOnConflict(
                context.getString(R.string.tb_snapshot),
                null, cv, SQLiteDatabase.CONFLICT_REPLACE
            )
        }
}