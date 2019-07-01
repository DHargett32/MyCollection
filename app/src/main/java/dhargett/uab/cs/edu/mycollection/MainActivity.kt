package dhargett.uab.cs.edu.mycollection

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    var DB = DatabaseHelper(this);
    var collectionsCount = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

 val addCollectionBtn = findViewById<Button>(R.id.b_addCollectionItem);


 // If collections exist, load them
 var existingCollections = DB.readCollectionData();
 if(!existingCollections.isEmpty())
 {
     for (i in 0..(existingCollections.size-1))
     {
         LoadCollections(existingCollections.get(i).name.toString(), existingCollections.get(i).image);
     }
 }

 // get collection count
 InializeCollectionCount();


 addCollectionBtn.setOnClickListener{
     OpenAddCollection();
 }
}

private fun LoadCollections(_collectionName: String, _collectionImage: ByteArray) {
 val table = findViewById<LinearLayout>(R.id.ll_collections);
 val view = layoutInflater.inflate(R.layout.collection, null);
 var myCol = Collection(_collectionName,_collectionImage);
 // set the name of the collection
 view.findViewById<TextView>(R.id.tv_collectionName).text = _collectionName;

 // set the image of the collection
 var image: Bitmap = BitmapFactory.decodeByteArray(_collectionImage,0,_collectionImage.size);
 view.findViewById<ImageView>(R.id.iv_collectionImage).setImageBitmap(image);


 view.setOnClickListener{
     ShowItems(_collectionName);
 }


 view.setOnLongClickListener{
     val dialogBuilder = AlertDialog.Builder(this);
     dialogBuilder.setTitle("Delete Collection");
     dialogBuilder.setMessage("Are you sure you want to delete the collection: '" + view.findViewById<TextView>(R.id.tv_collectionName).text + "' ?");
     dialogBuilder.setPositiveButton("Delete", DialogInterface.OnClickListener { dialog, which ->
         DeleteCollection(view);
     });
     dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->

     });

     //dialogBuilder.setCancelable(true);

     val alertDialog = dialogBuilder.create();
     alertDialog.show();

     //Toast.makeText(this, "Long click detected", Toast.LENGTH_SHORT).show();
     return@setOnLongClickListener true;
 }
 /**/


 // change background color if collection count % 2 != 0

 if(collectionsCount % 2 != 0)
 {
     view.setBackgroundColor(Color.parseColor("#292925"));
 }

 // Add collection to list
 table.addView(view);
 collectionsCount++;
 //val toast = Toast.makeText(this, "Collections loaded!", Toast.LENGTH_SHORT).show();

}


private fun AddCollections(_collectionName: String, _collectionImage: ByteArray) {
 val table = findViewById<LinearLayout>(R.id.ll_collections);
 val view = layoutInflater.inflate(R.layout.collection, null);
 var myCol = Collection(_collectionName,_collectionImage);
 // set the name of the collection
 view.findViewById<TextView>(R.id.tv_collectionName).text = _collectionName;

 // set the image of the collection
 var image: Bitmap = BitmapFactory.decodeByteArray(_collectionImage,0,_collectionImage.size);
 view.findViewById<ImageView>(R.id.iv_collectionImage).setImageBitmap(image);


 view.setOnClickListener{
     ShowItems(_collectionName);
 }


 view.setOnLongClickListener{
     val dialogBuilder = AlertDialog.Builder(this);
     dialogBuilder.setTitle("Delete Collection");
     dialogBuilder.setMessage("Are you sure you want to delete the collection: '" + view.findViewById<TextView>(R.id.tv_collectionName).text + "' ?");
     dialogBuilder.setPositiveButton("Delete", DialogInterface.OnClickListener { dialog, which ->
         DeleteCollection(view);
     });
     dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->

     });

     //dialogBuilder.setCancelable(true);

     val alertDialog = dialogBuilder.create();
     alertDialog.show();

     //Toast.makeText(this, "Long click detected", Toast.LENGTH_SHORT).show();
     return@setOnLongClickListener true;
 }
     /**/


 // change background color if collection count % 2 != 0

 if(collectionsCount % 2 != 0)
 {
     view.setBackgroundColor(Color.parseColor("#292925"));
 }

 // Add collection to list
 table.addView(view);
 // Add collection to Database
 DB.insertCollection(myCol);
 val toast = Toast.makeText(this, "'" + _collectionName + "' collection added!", Toast.LENGTH_SHORT).show();

}

private fun DeleteCollection(view: View?) {
 val table = findViewById<LinearLayout>(R.id.ll_collections);
 if(view != null)
 {
     var collectionName = view.findViewById<TextView>(R.id.tv_collectionName).text.toString();

     DB.deleteCollection(collectionName);
     table.removeView(view);
     UpdateCollectionList();
     Toast.makeText(this, "'" + collectionName + "' collection deleted!", Toast.LENGTH_SHORT).show();
 }
}

private fun UpdateCollectionList() {
 // update background colors of each item that represents a collection in the linear layout
 val table = findViewById<LinearLayout>(R.id.ll_collections);
 var collectionViewCount = 0;

 for(i in 0..table.childCount)
 {
     if(table.getChildAt(i) is View)
     {
         if(collectionViewCount % 2 == 0)
             table.getChildAt(i).setBackgroundColor(Color.parseColor("#222220"));
         else
             table.getChildAt(i).setBackgroundColor(Color.parseColor("#292925"));
         collectionViewCount++;
     }
 }

 // update the collections count
 UpdateCollectionsCount_decrement();
}


private fun ShowItems(_collectionName: String) {
 // open new activity that will display the items within the selected collection
 val i = Intent(this, CollectionItems::class.java);
 i.putExtra("collectionName", _collectionName)
 startActivity(i);
}

private fun OpenAddCollection() {
 val i = Intent(this, AddCollection::class.java);
 startActivityForResult(i, 999);
}

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent? ) {
 if(requestCode == 999 && resultCode == Activity.RESULT_OK && data != null){
     var collectionName = data.getStringExtra("collectionName");
     var collectionImage = data.getByteArrayExtra("collectionImage");
     AddCollections(collectionName, collectionImage);
     // update collections count
     UpdateCollectionsCount_increment();
 }
}

private fun UpdateCollectionsCount_increment()
{
 val table = findViewById<LinearLayout>(R.id.ll_collections);
 var collectionsCountLabel = findViewById<TextView>(R.id.tv_collectionsCount);
 collectionsCount = collectionsCount + 1;
 collectionsCountLabel.text = "Collections: " + collectionsCount;
}

private fun UpdateCollectionsCount_increment(count: Int)
{
 val table = findViewById<LinearLayout>(R.id.ll_collections);
 var collectionsCountLabel = findViewById<TextView>(R.id.tv_collectionsCount);
 collectionsCount = collectionsCount + count;
 collectionsCountLabel.text = "Collections: " + collectionsCount;
}

// Decrement
private fun UpdateCollectionsCount_decrement()
{
 val table = findViewById<LinearLayout>(R.id.ll_collections);
 var collectionsCountLabel = findViewById<TextView>(R.id.tv_collectionsCount);
 collectionsCount = collectionsCount - 1;
 collectionsCountLabel.text = "Collections: " + collectionsCount;
}

private fun UpdateCollectionsCount_decrement(count: Int)
{
 val table = findViewById<LinearLayout>(R.id.ll_collections);
 var collectionsCountLabel = findViewById<TextView>(R.id.tv_collectionsCount);
 collectionsCount = collectionsCount - count;
 collectionsCountLabel.text = "Collections: " + collectionsCount;
}

private fun InializeCollectionCount()
{
 val table = findViewById<LinearLayout>(R.id.ll_collections);
 val loadedCollectionsCount = table.childCount;
 var collectionsCountLabel = findViewById<TextView>(R.id.tv_collectionsCount);
 collectionsCountLabel.text = "Collections: " + collectionsCount;
}


}

