package dhargett.uab.cs.edu.mycollection

import android.content.ContentValues
import android.content.Context;
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast
import java.util.ArrayList

val DATABASE_NAME = "MyDB";
val TABLE_NAME_CollectionItem = "CollectionItem";
val TABLE_NAME_Collection = "Collection";
val COL_COLLECTION = "collection";
val COL_ITEMNAME = "itemname";
val COL_IMAGE = "image";
val COL_DESCRIPTION = "description";

class DatabaseHelper(var context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, 1){


    override fun onCreate(db: SQLiteDatabase?) {

        val createTable_Collection = "CREATE TABLE " + TABLE_NAME_Collection + "(" +
                COL_COLLECTION + " VARCHAR(256)PRIMARY KEY, " +
                COL_IMAGE + " VARCHAR(256)" +
                ")";

        val createTable_CollectionItem = "CREATE TABLE " + TABLE_NAME_CollectionItem + "(" +
                COL_COLLECTION + " VARCHAR(256), " +
                COL_ITEMNAME + " VARCHAR(256), " +
                COL_IMAGE + " VARCHAR(256), " +
                COL_DESCRIPTION + " VARCHAR(256), " +
                "PRIMARY KEY (" + COL_COLLECTION + ", " + COL_ITEMNAME + "), " +
                "FOREIGN KEY (" + COL_COLLECTION +") REFERENCES " + TABLE_NAME_Collection +" ("+ COL_COLLECTION + ")" +
                ")";

        db?.execSQL(createTable_Collection);
        db?.execSQL(createTable_CollectionItem);
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /////////////////////////////////
    // Collection Queries
    ////////////////////////////////
    // Insert
    ///////////

    fun insertCollection(collection: Collection)
    {
        val db = this.writableDatabase;
        var cv = ContentValues();
        cv.put(COL_COLLECTION, prepareQueryString(collection.name));
        cv.put(COL_IMAGE, collection.image);
        var result = db.insert(TABLE_NAME_Collection, null,cv);
        if(result == -1.toLong()) {
            //Toast.makeText(context, "Insert collection failed", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //Toast.makeText(context, "Insert collection success", Toast.LENGTH_SHORT).show();
        }
    }

    ///////////
    // Read
    ///////////

    fun readCollectionData() : MutableList<Collection>
    {
        var list : MutableList<Collection> = ArrayList();

        val db = this.readableDatabase;
        val query = "SELECT * FROM " + TABLE_NAME_Collection;
        val result = db.rawQuery(query, null);
        if(result.moveToFirst())
        {
            do {
                var collection = Collection();
                collection.name = result.getString(result.getColumnIndex(COL_COLLECTION));
                collection.image = result.getBlob(result.getColumnIndex(COL_IMAGE)) as ByteArray;

                // add the object to the list
                list.add(collection);
            }while(result.moveToNext());
        }

        result.close();
        db.close();
        return list;
    }

    fun readCollectionData(collection: Collection) : MutableList<Collection>
    {
        var list : MutableList<Collection> = ArrayList();

        val db = this.readableDatabase;
        val query = "SELECT * FROM " + TABLE_NAME_Collection +
                    " WHERE " + COL_COLLECTION + " = '" + prepareQueryString(collection.name) + "'";
        val result = db.rawQuery(query, null);
        if(result.moveToFirst())
        {
            do {
                var collection = Collection();
                collection.name = result.getString(result.getColumnIndex(COL_COLLECTION));
                collection.image = result.getBlob(result.getColumnIndex(COL_IMAGE)) as ByteArray;

                // add the object to the list
                list.add(collection);
            }while(result.moveToNext());
        }

        result.close();
        db.close();
        return list;
    }

    ///////////
    // Delete
    ///////////
    fun deleteCollection(_collectionName: String )
    {
        // remove the collection record from the 'Collection' table
        val db = this.writableDatabase;
        db.delete(TABLE_NAME_Collection, COL_COLLECTION + "= ?", arrayOf(prepareQueryString(_collectionName.toString())));
        //db.close();
        // remove the collection items related to the collection that was deleted
        db.delete(TABLE_NAME_CollectionItem, COL_COLLECTION + "= ?", arrayOf(prepareQueryString(_collectionName.toString())));
        db.close();
    }


    /////////////////////////////////
    // Collection Item Queries
    ////////////////////////////////
    // Insert
    ///////////

    fun insertCollectionItem(_collectionItem: CollectionItem)
    {
        val db = this.writableDatabase;
        var cv = ContentValues();
        cv.put(COL_COLLECTION, prepareQueryString(_collectionItem.collection));
        cv.put(COL_ITEMNAME, prepareQueryString(_collectionItem.name));
        cv.put(COL_IMAGE, _collectionItem.image);
        cv.put(COL_DESCRIPTION, prepareQueryString(_collectionItem.description));
        var result = db.insert(TABLE_NAME_CollectionItem, null,cv);
        if(result == -1.toLong())
        {
            //Toast.makeText(context, "Insert collection failed", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //Toast.makeText(context, "Insert collection success", Toast.LENGTH_SHORT).show();
        }
    }

    ///////////
    // Read
    ///////////

    // return all items for a particular collection
    fun readCollectionItemData(_collectionName: String) : MutableList<CollectionItem>
    {
        var list : MutableList<CollectionItem> = ArrayList();

        val db = this.readableDatabase;
        val query = "SELECT * FROM " + TABLE_NAME_CollectionItem +
                    " WHERE " + COL_COLLECTION + " = " + DatabaseUtils.sqlEscapeString(_collectionName) + "";
        val result = db.rawQuery(query, null);
        if(result.moveToFirst())
        {
            do {
                var collectionItem = CollectionItem();
                collectionItem.collection = result.getString(result.getColumnIndex(COL_COLLECTION));
                collectionItem.name = result.getString(result.getColumnIndex(COL_ITEMNAME));
                collectionItem.image = result.getBlob(result.getColumnIndex(COL_IMAGE)) as ByteArray;
                collectionItem.description = result.getString(result.getColumnIndex(COL_DESCRIPTION));

                // add the object to the list
                list.add(collectionItem);
            }while(result.moveToNext());
        }

        result.close();
        db.close();
        return list;
    }

    // return the details for an item in a collection
    fun readCollectionItemDetailsData(_collection: String, _collectionItemName: String) : MutableList<CollectionItem>
    {
        var list : MutableList<CollectionItem> = ArrayList();

        val db = this.readableDatabase;
        val query = "SELECT * FROM " + TABLE_NAME_CollectionItem +
                " WHERE " + COL_COLLECTION + " = " + DatabaseUtils.sqlEscapeString(_collection) + " " +
                "AND " + COL_ITEMNAME + " = " + DatabaseUtils.sqlEscapeString(_collectionItemName) + "";
        val result = db.rawQuery(query, null);
        if(result.moveToFirst())
        {
            do {
                var collectionItem = CollectionItem();
                collectionItem.collection = result.getString(result.getColumnIndex(COL_COLLECTION));
                collectionItem.name = result.getString(result.getColumnIndex(COL_ITEMNAME));
                collectionItem.image = result.getBlob(result.getColumnIndex(COL_IMAGE)) as ByteArray;
                collectionItem.description = result.getString(result.getColumnIndex(COL_DESCRIPTION));

                // add the object to the list
                list.add(collectionItem);
            }while(result.moveToNext());
        }

        result.close();
        db.close();
        return list;
    }

    ///////////
    // Delete
    ///////////
    fun deleteCollectionItem(_collectionName: String, _collectionItemName: String)
    {
        val db = this.writableDatabase;

        /*val query = "DELETE FROM " + TABLE_NAME_CollectionItem +
                    " WHERE " + COL_COLLECTION + " = '" + _collectionName +
                    "' AND " + COL_ITEMNAME + " = '" + _collectionItemName + "'";
        db.execSQL(query);*/

        db.delete(TABLE_NAME_CollectionItem, COL_ITEMNAME + "= ? AND " + COL_COLLECTION + " = ?", arrayOf(_collectionItemName.toString(), _collectionName.toString()));
        db.close();
        //Toast.makeText(context, "Deleted THIS: " + _collectionItemName, Toast.LENGTH_SHORT).show();
    }

    fun deleteAllCollectionItems(_collectionName: String )
    {
        val db = this.writableDatabase;
        db.delete(TABLE_NAME_CollectionItem, COL_COLLECTION + "= ?", arrayOf(_collectionName.toString()));
        db.close();
        //Toast.makeText(context, "Deleted Collection and Items for: " + _collectionName, Toast.LENGTH_SHORT).show();
    }


    ////////////
    //  MISC.
    ///////////

    private fun prepareQueryString(_query: String): String
    {
        var newString = "";

        for(i in (0.._query.length-1))
        {
            if(_query[i].toString() == "'")
                newString += "''";
            else if(_query[i].toString() == "\\")
                newString += "\\\\";
            else
                newString += _query[i].toString();
        }

        return newString;
    }

}