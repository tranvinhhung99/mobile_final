package hcmus.selab.tvhung.models;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.lang.annotation.Target;
import java.util.Map;

import hcmus.selab.tvhung.R;

public class ProductBuilder {
    private static final String TAG = ProductBuilder.class.toString();

    private String mName;
    private int    mPrice;
    private float  mRating    = 0;
    private int    mNumRating = 0;
    private String mCategory  = "phone";
    private String mId;
    private String mAvatarPath = null;

    public ProductBuilder setCategory(String category){
        this.mCategory = category;
        return this;
    }

    public ProductBuilder setName(String name) {
        this.mName = name;
        return this;
    }

    public ProductBuilder setPrice(int price) {
        this.mPrice = price;
        return this;
    }

    public ProductBuilder setRating(int rating) {
        this.mRating = rating;
        return this;
    }

    public ProductBuilder setRating(float rating) {
        this.mRating = rating;
        return this;
    }

    public ProductBuilder setNumRating(int numRating) {
        this.mNumRating = numRating;
        return this;
    }

    public ProductBuilder setId(String id){
        this.mId = id;
        return this;
    }

    public Product createProduct() {
        return new Product(mName, mPrice, mRating, mNumRating, mCategory, mId, mAvatarPath);
    }

    public ProductBuilder setValue(Map map){
        Log.d(TAG, map.toString());
        Log.d(TAG, (String) map.get("name"));
        if(map.containsKey("category"))
            this.setCategory((String) map.get("category"));

        if(map.containsKey("name"))
            this.setName((String) map.get("name"));

        if(map.containsKey("num_rating"))
            this.setNumRating(convertObjectToInt(map.get("num_rating")));

        if(map.containsKey("average_rating"))
            this.setRating(convertObjectToFloat(map.get("average_rating")));

        if(map.containsKey("price"))
            this.setPrice(convertObjectToInt(map.get("price")));


        return this;
    }

    public ProductBuilder setAvatarPath(String avatarPath){
        this.mAvatarPath = avatarPath;
        return this;
    }


    private int convertObjectToInt(Object object){
        return ((Long) object).intValue();
    }

    private float convertObjectToFloat(Object object){
        return ((Double) object).floatValue();
    }

}