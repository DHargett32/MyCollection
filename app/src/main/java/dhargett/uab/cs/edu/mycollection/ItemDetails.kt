package dhargett.uab.cs.edu.mycollection

import android.app.ActionBar
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.autofill.AutofillValue
import android.widget.ImageView
import android.widget.TextView

class ItemDetails : AppCompatActivity() {
    var DB = DatabaseHelper(this);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_details)


        val collectionName =intent.getStringExtra("collectionItemDetails_collection")
        val collectionItemName =intent.getStringExtra("collectionItemDetails_name")
        val collectionItemDescription =intent.getStringExtra("collectionItemDetails_description")
        val collectionItemImage: ByteArray =intent.getByteArrayExtra("collectionItemDetails_image")

        //set the title text to the name of the collection the item we are currently viewing belongs to
        setTitle(collectionName);

        // set the details of the item we wish to display
        var itemDetailsName = findViewById<TextView>(R.id.tv_collectionItemDetails_name);
        var itemDetailsImage = findViewById<ImageView>(R.id.iv_collectionItemDetailsImage);
        var itemDetailsDescription = findViewById<TextView>(R.id.tv_collectionItemDetails_description);

        itemDetailsName.text = collectionItemName;
        itemDetailsImage.setImageBitmap(BitmapFactory.decodeByteArray(collectionItemImage, 0, collectionItemImage.size));
        itemDetailsDescription.text = collectionItemDescription;

        // click image and display in dialog box
        itemDetailsImage.setOnClickListener{

            //val i = Intent(this, imageDetails::class.java);
            //i.putExtra("imageDetails", collectionItemImage);
            /*var d = AlertDialog.Builder(this);
            var iv = ImageView(this);
            iv.setImageBitmap(BitmapFactory.decodeByteArray(collectionItemImage, 0, collectionItemImage.size));
            d.setView(iv);*/

            var d = Dialog(this)
            d.window.requestFeature(Window.FEATURE_NO_TITLE);


            d.setContentView(layoutInflater.inflate(R.layout.activity_image_details, null));
            d.window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
            var imageDetailsDestination = d.findViewById<ImageView>(R.id.iv_itemImage);
            imageDetailsDestination.setImageBitmap(BitmapFactory.decodeByteArray(collectionItemImage, 0, collectionItemImage.size));


            d.show();
        }

    }
}
