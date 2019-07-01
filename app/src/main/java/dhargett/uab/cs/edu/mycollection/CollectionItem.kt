package dhargett.uab.cs.edu.mycollection

class CollectionItem{
    var collection: String = "";
    var name: String = "";
    var description: String = "";
    lateinit var image: ByteArray;

    constructor()
    {

    }

    constructor(_collection: String, _name: String, _description: String, _image: ByteArray)
    {
        this.collection = _collection;
        this.name = _name;
        this.description = _description;
        //this.image = ByteArray(_image.size);
        this.image = _image;//_image;
    }
}