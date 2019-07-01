package dhargett.uab.cs.edu.mycollection

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.*
import kotlinx.android.synthetic.main.activity_collection_items.*

class CollectionItems : AppCompatActivity() {
    var DB = DatabaseHelper(this);
    var itemsCount = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_items);
        // collectionItemName
        var collectionName = "";
        collectionName = intent.getStringExtra("collectionName")




        // set the title text to the collection we are currently viewing
        setTitle(collectionName);
        //var ab: ActionBar = this.actionBar;
        //ab.setTitle(collectionName);

        // if items exist for the collection we selected, load them
        var existingItems = DB.readCollectionItemData(collectionName).sortedWith(compareBy({it.name}));
        DB.close();
        if(!existingItems.isEmpty())
        {
            for (i in 0..(existingItems.size-1))
            {
                LoadCollectionItems(existingItems.get(i).collection, existingItems.get(i).name,existingItems.get(i).image,existingItems.get(i).description);
            }
        }

        // get item count
        InitializeCollectionItemCount();


        val b_addCollectionItem: Button = findViewById<Button>(R.id.b_addCollectionItem);
        b_addCollectionItem.setOnClickListener{
            // open up view to add an item to the list of collection items
            OpenAddCollectionItem(collectionName);
        }



    }

    private fun LoadCollectionItems(_collectionName: String, _collectionItemName: String, _collectionItemImage: ByteArray, _collectionItemDescription: String)
    {
        var collectionItemList = findViewById<LinearLayout>(R.id.ll_collectionItems);
        val view = layoutInflater.inflate(R.layout.collection, null);

        // set name of item
        view.findViewById<TextView>(R.id.tv_collectionName).text = _collectionItemName;

        // set the image of the item
        var image: Bitmap = BitmapFactory.decodeByteArray(_collectionItemImage, 0, _collectionItemImage.size);

        view.findViewById<ImageView>(R.id.iv_collectionImage).setImageBitmap(image);

        view.setOnClickListener {
            // print text of collection clicked
            //val toast = Toast.makeText(this, "" + view.findViewById<TextView>(R.id.tv_collectionName).text + " clicked!", Toast.LENGTH_SHORT).show();
            ;//findViewById<TextView>(R.id.tv_collectionName);
            var myCollectionItem = CollectionItem(_collectionName, _collectionItemName, _collectionItemDescription, _collectionItemImage);
            ViewItemDetails(myCollectionItem);
            //val toast = Toast.makeText(this, "" + view.findViewById<TextView>(R.id.tv_collectionName).text + " clicked!", Toast.LENGTH_SHORT).show();
            //ShowItemDetails();
        }

        view.setOnLongClickListener{
            val dialogBuilder = AlertDialog.Builder(this);
            dialogBuilder.setTitle("Delete Item");
            dialogBuilder.setMessage("Are you sure you want to delete the item: '" + view.findViewById<TextView>(R.id.tv_collectionName).text + "' ?");
            dialogBuilder.setPositiveButton("Delete", DialogInterface.OnClickListener { dialog, which ->
                DeleteCollectionItem(view, _collectionName);
            });
            dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->

            });

            //dialogBuilder.setCancelable(true);

            val alertDialog = dialogBuilder.create();
            alertDialog.show();

            //Toast.makeText(this, "Long click detected", Toast.LENGTH_SHORT).show();
            return@setOnLongClickListener true;
        }

        if(itemsCount % 2 != 0)
        {
            view.setBackgroundColor(Color.parseColor("#292925"));
        }

        // add item to list
        collectionItemList.addView(view);
        itemsCount++;
    }

    private fun AddCollectionItemToList(_collectionName: String, _collectionItemName: String, _collectionItemImage: ByteArray, _collectionItemDescription: String)
    {

        var collectionItemList = findViewById<LinearLayout>(R.id.ll_collectionItems);
        val view = layoutInflater.inflate(R.layout.collection, null);

        // set name of item
        view.findViewById<TextView>(R.id.tv_collectionName).text = _collectionItemName;

        // set the image of the item
        var image: Bitmap = BitmapFactory.decodeByteArray(_collectionItemImage, 0, _collectionItemImage.size);

        view.findViewById<ImageView>(R.id.iv_collectionImage).setImageBitmap(image);

        view.setOnClickListener {
            var myCollectionItem = CollectionItem(_collectionName, _collectionItemName, _collectionItemDescription, _collectionItemImage);
            ViewItemDetails(myCollectionItem);
            //val toast = Toast.makeText(this, "" + view.findViewById<TextView>(R.id.tv_collectionName).text + " clicked!", Toast.LENGTH_SHORT).show();
        }

        view.setOnLongClickListener{
            val dialogBuilder = AlertDialog.Builder(this);
            dialogBuilder.setTitle("Delete Item");
            dialogBuilder.setMessage("Are you sure you want to delete the item: '" + view.findViewById<TextView>(R.id.tv_collectionName).text + "' ?");
            dialogBuilder.setPositiveButton("Delete", DialogInterface.OnClickListener { dialog, which ->
                DeleteCollectionItem(view, _collectionName);
            });
            dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->

            });

            //dialogBuilder.setCancelable(true);

            val alertDialog = dialogBuilder.create();
            alertDialog.show();

            //Toast.makeText(this, "Long click detected", Toast.LENGTH_SHORT).show();
            return@setOnLongClickListener true;
        }

        if(itemsCount % 2 != 0)
        {
            view.setBackgroundColor(Color.parseColor("#292925"));
        }

        // add item to list
        collectionItemList.addView(view);
        // add item to Database
        var myCollectionItem = CollectionItem(_collectionName, _collectionItemName, _collectionItemDescription, _collectionItemImage);
        DB.insertCollectionItem(myCollectionItem);

    }

    private fun DeleteCollectionItem(view: View?, _collectionName: String) {
        val table = findViewById<LinearLayout>(R.id.ll_collectionItems);
        if(view != null)
        {
            var collectionItemName = view.findViewById<TextView>(R.id.tv_collectionName).text.toString();

            DB.deleteCollectionItem(_collectionName, collectionItemName);
            table.removeView(view);
            UpdateCollectionItemList();
            Toast.makeText(this, "Item: '" + collectionItemName + "' deleted!", Toast.LENGTH_SHORT).show();
        }
    }

    private fun UpdateCollectionItemList() {
        // update background colors of each item that represents a collection in the linear layout
        val table = findViewById<LinearLayout>(R.id.ll_collectionItems);
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
        UpdateCollectionItemsCount_decrement();
    }

    private fun ViewItemDetails(_collectionItemDetails: CollectionItem) {
        val i = Intent(this, ItemDetails::class.java);
        i.putExtra("collectionItemDetails_collection",_collectionItemDetails.collection);
        i.putExtra("collectionItemDetails_name",_collectionItemDetails.name);
        i.putExtra("collectionItemDetails_description",_collectionItemDetails.description);
        i.putExtra("collectionItemDetails_image",_collectionItemDetails.image);
        startActivity(i);
    }

    private fun OpenAddCollectionItem(_collectionName: String) {
        val i = Intent(this, AddCollectionItem::class.java);
        i.putExtra("collectionName", _collectionName)
        startActivityForResult(i, 999);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent? ) {
        if(requestCode == 999 && resultCode == Activity.RESULT_OK && data != null){
            var collectionName = data.getStringExtra(("collectionName"));
            var collectionItemName = data.getStringExtra("collectionItemName");
            var collectionItemImage = data.getByteArrayExtra("itemImage");
            var collectionItemDescription = data.getStringExtra("collectionItemDescription");
            AddCollectionItemToList(collectionName, collectionItemName, collectionItemImage, collectionItemDescription);

            UpdateCollectionItemsCount_increment();
            val toast = Toast.makeText(this, "'" + collectionItemName + "' item added!", Toast.LENGTH_SHORT).show();
        }
    }

    private fun UpdateCollectionItemsCount_increment()
    {
        //val table = findViewById<LinearLayout>(R.id.ll_collectionItems);
        var itemsCountLabel = findViewById<TextView>(R.id.tv_itemsCount);
        itemsCount = itemsCount + 1;
        itemsCountLabel.text = "Items: " + itemsCount;
    }

    private fun UpdateCollectionItemsCount_increment(count: Int)
    {
        //val table = findViewById<LinearLayout>(R.id.ll_collectionItems);
        var itemsCountLabel = findViewById<TextView>(R.id.tv_itemsCount);
        itemsCount = itemsCount + count;
        itemsCountLabel.text = "Items: " + itemsCount;
    }

    private fun UpdateCollectionItemsCount_decrement()
    {
        //val table = findViewById<LinearLayout>(R.id.ll_collectionItems);
        var itemsCountLabel = findViewById<TextView>(R.id.tv_itemsCount);
        itemsCount = itemsCount - 1;
        itemsCountLabel.text = "Items: " + itemsCount;
    }

    private fun UpdateCollectionItemsCount_decrement(count: Int)
    {
        //val table = findViewById<LinearLayout>(R.id.ll_collectionItems);
        var itemsCountLabel = findViewById<TextView>(R.id.tv_itemsCount);
        itemsCount = itemsCount - count;
        itemsCountLabel.text = "Items: " + itemsCount;
    }

    private fun InitializeCollectionItemCount()
    {
        //val table = findViewById<LinearLayout>(R.id.ll_collectionItems);
        //val loadedCollectionItemsCount = table.childCount;
        var collectionsItemCountLabel = findViewById<TextView>(R.id.tv_itemsCount);
        collectionsItemCountLabel.setText("Items: " + itemsCount);
    }
}
