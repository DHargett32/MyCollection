package dhargett.uab.cs.edu.mycollection

class Collection{
    var name: String = "";
    lateinit var image: ByteArray; // are we able to resize the array???

    constructor()
    {

    }
    constructor(_name: String, _image: ByteArray)
    {
        this.name = _name;
        this.image = _image;
    }
}