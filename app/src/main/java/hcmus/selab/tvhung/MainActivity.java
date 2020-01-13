package hcmus.selab.tvhung;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import hcmus.selab.tvhung.adapters.ProductAdapter;
import hcmus.selab.tvhung.models.Product;
import hcmus.selab.tvhung.models.ProductBuilder;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.toString();
    private DatabaseReference mProductReference;
    private ProductAdapter mProductAdapter;
    private FirebaseStorage mStorage;

    private ValueEventListener mProductListener;

    // Search feature
    private SearchView searchView;
    private ImageView btn_shopping_cart;
    private ArrayList<Product> mProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init Grid View
        mProducts = new ArrayList<Product>();
        mProductAdapter = new ProductAdapter(getBaseContext(), R.layout.product_item_layout, mProducts);
        GridView gridView = findViewById(R.id.grid_view_items);
        gridView.setAdapter(mProductAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = mProductAdapter.getItem(i);
                Intent intent = new Intent(getBaseContext(), ProductActivity.class);

                intent.putExtra("product", product);
                startActivity(intent);

            }
        });

        // Init Search view
        searchView = findViewById(R.id.searchView);

        // Init shopping cart button
        btn_shopping_cart = findViewById(R.id.shopping_cart);
        btn_shopping_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent cart = new Intent(MainActivity.this, CartActivity.class);
                    startActivity(cart);
                }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(getBaseContext(), query, Toast.LENGTH_LONG).show();

                // After user submit a query
                Intent search = new Intent(MainActivity.this, SearchActivity.class);
                search.putExtra("products", mProducts);
                startActivity(search);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                Toast.makeText(getBaseContext(), newText, Toast.LENGTH_LONG).show();
                return false;
            }
        });

        // Init database
        mProductReference = FirebaseDatabase.getInstance().getReference().child("products");
        mStorage = FirebaseStorage.getInstance();


        
    }


    @Override
    protected void onStart() {
        super.onStart();

        // Add listener to db
        if(mProductListener == null){
            mProductListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    collectProductList((Map<String, Object>) dataSnapshot.getValue());
                    mProductAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };


            mProductReference.addValueEventListener(mProductListener);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mProductListener != null)
            mProductReference.removeEventListener(mProductListener);

    }


    private void collectProductList(Map<String, Object> data){
        Log.i(TAG, "add new data");
        for(Map.Entry<String, Object> entry : data.entrySet()){

            Map singleProduct = (Map) entry.getValue();
            ProductBuilder builder = new ProductBuilder();

            builder = builder.setId(entry.getKey())
                    .setValue(singleProduct);


            if(singleProduct.containsKey("avatar_url")){
                String avatarUrl = (String)singleProduct.get("avatar_url");
                String downloadedPath = downloadAvatar(avatarUrl, entry.getKey(), mProductAdapter.getCount());

                if(downloadedPath != null){
                    builder.setAvatarPath(downloadedPath);
                }
            }

            mProductAdapter.add(builder.createProduct());


        }

    }

    // Return file path if file already exists
    private String downloadAvatar(String url, final String id, final int position){
        StorageReference reference = mStorage.getReferenceFromUrl(url);

        final File file = new File(getFilesDir() + "/" + id + ".jpeg");

        // Avatar already downloaded
        if(file.exists()){
            return file.toString();
        }

        reference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                mProductAdapter.getItem(position).setAvatarPath(file.toString());
                mProductAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Cannot download " + file.toString());
                e.printStackTrace();
            }
        });
        return null;
    }

}
