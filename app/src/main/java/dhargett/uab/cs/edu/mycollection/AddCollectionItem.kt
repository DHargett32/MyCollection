package dhargett.uab.cs.edu.mycollection

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import java.io.ByteArrayOutputStream
import java.net.URI

class AddCollectionItem : AppCompatActivity() {
    val REQUEST_SELECT_IMAGE_IN_GALLERY = 0;
    val REQUEST_IMAGE_CAPTURE = 1;
    lateinit var collectionItemImage: ImageView;
    //var resources = context.getResources();
    lateinit var photo: Bitmap;
    var imageSet: Boolean = false;
    var DB = DatabaseHelper(this);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_collection_item)

        var addCollectionItemBtn = findViewById<Button>(R.id.b_addCollectionItem);
        var collectionItemName = findViewById<EditText>(R.id.tb_collectionItemName);
        var collectionItemDetails = findViewById<EditText>(R.id.tv_collectionItemDetails);
        collectionItemImage = findViewById<ImageView>(R.id.imgv_collectionItem);
        val collectionName = intent.getStringExtra("collectionName");

        photo = BitmapFactory.decodeResource(resources, R.drawable.item_default_image2);

        // add item button
        addCollectionItemBtn.setOnClickListener{

            // check and ensure the item name has been set
            if(collectionItemName.text.isBlank() || collectionItemName.text.isEmpty())
            {
                Toast.makeText(this, "Error. Please enter a name for the item!", Toast.LENGTH_SHORT).show();
            }

            // if the item name is set, execute the code as normal
            else
            {
                var i = Intent();
                val collectionItemName = collectionItemName.text.toString();
                i.putExtra("collectionItemName", collectionItemName);

                var collectionItemDescription = collectionItemDetails.text.toString();
                i.putExtra("collectionItemDescription", collectionItemDescription);

                var stream: ByteArrayOutputStream = ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                var byteArray = stream.toByteArray();

                // check whether the collection item exists
                if (!DB.readCollectionItemDetailsData(collectionName, collectionItemName).isEmpty()) {
                    DB.close();
                    Toast.makeText(this, "Error. Item already exists in this collection!", Toast.LENGTH_SHORT).show();
                } else {
                    DB.close();
                    if (imageSet) //@android:drawable/ic_menu_camera
                    {
                        i.putExtra("collectionName", collectionName);
                        i.putExtra("itemImage", byteArray);
                    } else {
                        i.putExtra("collectionName", collectionName);
                        i.putExtra("itemImage", byteArray);71
                    }
                    setResult(Activity.RESULT_OK, i);
                    finish();
                }
            }
        }

        // item image view
        collectionItemImage.setOnClickListener{


            val dialogBuilder = AlertDialog.Builder(this);
            dialogBuilder.setTitle("Set Image");
            dialogBuilder.setCancelable(true);
            dialogBuilder.setMessage("How do you want to set the image?");

            dialogBuilder.setNeutralButton("Cancel", DialogInterface.OnClickListener { dialog, which ->

            });

            dialogBuilder.setPositiveButton("Take Photo", DialogInterface.OnClickListener { dialog, which ->
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(cameraIntent.resolveActivity(packageManager)!= null)
                {
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                }
            });

            dialogBuilder.setNegativeButton("Gallery", DialogInterface.OnClickListener { dialog, which ->
                val galleryIntent = Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.type = "image/*";
                if(galleryIntent.resolveActivity(packageManager) != null)
                {
                    startActivityForResult(galleryIntent, REQUEST_SELECT_IMAGE_IN_GALLERY);
                }
            });


            val imageOptions = dialogBuilder.create();
            imageOptions.show();
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null)
        {
            // collectionItemImage = findViewById<ImageView>(R.id.imgv_collectionItem);

            // get the photo
            imageSet = true;
            var extras = data.extras; // getExtra
            photo = extras.get("data") as Bitmap;
            collectionItemImage.setImageBitmap(photo);

        }

        else if(requestCode == REQUEST_SELECT_IMAGE_IN_GALLERY && resultCode == Activity.RESULT_OK && data != null)
        {
            // get the photo
            imageSet = true;
            var d = data.data;

            //var extras = data.extras; // getExtra
            photo = MediaStore.Images.Media.getBitmap(this.contentResolver, d);
            collectionItemImage.setImageBitmap(photo);
        }

    }
}
