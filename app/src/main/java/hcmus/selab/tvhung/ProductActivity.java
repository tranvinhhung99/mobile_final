package hcmus.selab.tvhung;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import hcmus.selab.tvhung.models.Product;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProductActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ProductActivity.class.toString();

    private Product mProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        mProduct = (Product) this.getIntent().getSerializableExtra("product");

        // Show UI
        Resources resources = Resources.getSystem();
        ((ImageView)findViewById(R.id.item_avatar)).setImageBitmap(mProduct.getAvatar());
        ((TextView)findViewById(R.id.text_view_item_name)).setText(mProduct.getName());
        ((TextView)findViewById(R.id.text_view_num_rating)).setText(getString(R.string.format_num_rating, mProduct.getNumRating()));
        ((TextView)findViewById(R.id.text_view_item_price)).setText(getString(R.string.format_price, mProduct.getPrice()));
        ((RatingBar)findViewById(R.id.rating_bar)).setRating(mProduct.getRating());

        // Set OnClickListener
        findViewById(R.id.btn_add_to_cart).setOnClickListener(this);


    }


    private void addToCart(){
        String uid = FirebaseAuth.getInstance().getUid();
        Log.i(TAG, uid + " add item to cart");
        final DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference().child("carts")
                .child(uid);
        final DatabaseReference numItemReference = cartReference.child(mProduct.getId());


        cartReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                HashMap<String, Object> data = (HashMap<String, Object>) dataSnapshot.getValue();
                long numItem = 0;

                if(data != null)
                    if(data.containsKey(mProduct.getId()))
                        numItem = (Long) data.get(mProduct.getId());

                numItem += 1;
                Map<String, Object> updateChildrens = new HashMap<>();
                updateChildrens.put("/" + mProduct.getId(), numItem);



                cartReference.updateChildren(updateChildrens);
                Toast.makeText(getBaseContext(), "add item success", Toast.LENGTH_LONG).show();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getBaseContext(), "Cannot add to cart", Toast.LENGTH_LONG).show();
                databaseError.toException().printStackTrace();
            }
        });






    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.btn_add_to_cart)
            addToCart();
    }
}
