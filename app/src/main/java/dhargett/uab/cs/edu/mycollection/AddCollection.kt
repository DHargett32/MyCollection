package dhargett.uab.cs.edu.mycollection

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import java.io.ByteArrayOutputStream

class AddCollection : AppCompatActivity() {
    val REQUEST_IMAGE_CAPTURE = 1;
    lateinit var collectionImage: ImageView;
    lateinit var photo: Bitmap;
    var imageSet: Boolean = false;
    var DB = DatabaseHelper(this);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_collection)

        val addCollectionBtn = findViewById<Button>(R.id.b_addCollectionItem);
        val collectionName = findViewById<EditText>(R.id.tb_collectionName);
        collectionImage = findViewById<ImageView>(R.id.imgv_collection)

        photo = BitmapFactory.decodeResource(resources, R.drawable.collection_default_image);

        // add collection button
        addCollectionBtn.setOnClickListener{

            // check and ensure the collection name has been set
            if(collectionName.text.isBlank() || collectionName.text.isEmpty())
            {
                Toast.makeText(this, "Error. Please enter a name for the collection!", Toast.LENGTH_SHORT).show();
            }

            // if the collection name is set, execute the code as normal
            else
            {
                var i = Intent();
                i.putExtra("collectionName", collectionName.text.toString());
                var stream: ByteArrayOutputStream = ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                var byteArray = stream.toByteArray();

                // check whether collection exists
                var myCol = Collection(collectionName.text.toString(), byteArray);

                // Collection exists, prompt the user
                if (!DB.readCollectionData(myCol).isEmpty()) {
                    Toast.makeText(this, "Error. Collection already exists!", Toast.LENGTH_SHORT).show();
                }
                // Collection does not exist. Add the collection to the list
                else {
                    if (imageSet) //@android:drawable/ic_menu_camera
                    {
                        i.putExtra("collectionImage", byteArray);
                    } else {
                        i.putExtra("collectionImage", byteArray);
                    }

                    setResult(Activity.RESULT_OK, i);
                    finish();
                }
            }
        }

        // collection image view
        collectionImage.setOnClickListener{
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if(cameraIntent.resolveActivity(packageManager)!= null)
            {
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
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
            collectionImage.setImageBitmap(photo);

        }
    }
}
