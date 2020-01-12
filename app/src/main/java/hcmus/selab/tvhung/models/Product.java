package hcmus.selab.tvhung.models;

import android.app.Application;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;

import com.google.api.Context;
import com.google.firebase.database.Exclude;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import hcmus.selab.tvhung.R;

public class Product implements Serializable {

    private static final int DEFAULT_ITEM_AVATAR_ID =  R.drawable.ic_launcher_background;

    private String mName;
    private int    mPrice;
    private float  mRating;
    private int    mNumRating;
    private String mCategory;
    private String mId;
    private String mAvatarPath = null;

    public Product(String mName, int mPrice, float mRating, int mNumRating,
                   String mCategory, String mId, String avatarPath) {
        this.mName = mName;
        this.mPrice = mPrice;
        this.mRating = mRating;
        this.mNumRating = mNumRating;
        this.mCategory = mCategory;
        this.mId = mId;
        this.mAvatarPath = avatarPath;
    }

    // Default constructor needed for Firebase
    public Product(){}

    public Bitmap getAvatar() {

        if(mAvatarPath == null)
            return getDefaultBitmap();

        try {
            FileInputStream inputStream = new FileInputStream(mAvatarPath);
            return BitmapFactory.decodeFileDescriptor(inputStream.getFD());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getDefaultBitmap();
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int mPrice) {
        this.mPrice = mPrice;
    }

    public float getRating() {
        return mRating;
    }

    public void setRating(float mRating) {
        this.mRating = mRating;
    }

    public int getNumRating() {
        return mNumRating;
    }

    public void setNumRating(int mNumRating) {
        this.mNumRating = mNumRating;
    }

    public String getCategory(){
        return mCategory;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("category", getCategory());
        result.put("name", getName());
        result.put("num_rating", getNumRating());
        result.put("average_rating", getRating());
        result.put("price", getPrice());

        return result;
    }

    public String getId() {
        return mId;
    }

    public void setAvatarPath(String path){
        mAvatarPath = path;
    }

    private Bitmap getDefaultBitmap(){
        return BitmapFactory.decodeResource(
                Resources.getSystem(), DEFAULT_ITEM_AVATAR_ID
        );
    }

}
