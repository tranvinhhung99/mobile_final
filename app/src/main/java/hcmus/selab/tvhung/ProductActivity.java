package hcmus.selab.tvhung;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import hcmus.selab.tvhung.models.Product;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
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
        findViewById(R.id.btn_back).setOnClickListener(this);

        // Create fake map

        DatabaseReference detailInformation = FirebaseDatabase.getInstance().getReference()
                .child("products_detail").child(mProduct.getId());

        detailInformation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();

                if(data == null){
                    Log.d(TAG, "Not found product detail information:" + mProduct.getId());
                    return;
                }

                if(data.containsKey("description")){
                    TextView descriptionText = findViewById(R.id.text_view_description);
                    descriptionText.setText(data.get("description").toString());
                }

                if(data.containsKey("detail_information")){
                    Map<String, String> detailInformation = (Map<String, String>) data.get("detail_information");
                    generateGrid(detailInformation);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Not found product detail information:" + mProduct.getId());
            }
        });


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
        if(id == R.id.btn_back)
        {
            finish();
        }

    }



    private final int EVEN_CELL_COLOR = Color.parseColor("#EAEBEC");
    private final int ODD_CELL_COLOR = Color.parseColor("#FFFFFF");

    private void addEntryToGrid(Map.Entry<String, String> entry, int position, GridLayout gridLayout){
        int color = (position % 2 == 0) ? EVEN_CELL_COLOR : ODD_CELL_COLOR;

        // For Key Cell
        ContextThemeWrapper keyWrapper = new ContextThemeWrapper(this, R.style.GridCellKey);
        TextView keyTextView = new TextView(keyWrapper);
        keyTextView.setBackgroundColor(color);
        keyTextView.setText(entry.getKey());

        GridLayout.LayoutParams keyParam = new GridLayout.LayoutParams(keyWrapper, null);

        gridLayout.addView(keyTextView, keyParam);


        // For Value Cell
        ContextThemeWrapper valueWrapper = new ContextThemeWrapper(this, R.style.GridCellValue);
        TextView valueTextView = new TextView(valueWrapper);
        valueTextView.setBackgroundColor(color);
        valueTextView.setText(entry.getValue());

        GridLayout.LayoutParams valueParam = new GridLayout.LayoutParams(valueWrapper, null);

        gridLayout.addView(valueTextView, valueParam);

    }

    private void generateGrid(Map<String, String> map){
        GridLayout layout = findViewById(R.id.detail_information_grid);
        int numRows = map.size();
        layout.setRowCount(numRows);
        int count = 0;

        for(Map.Entry<String, String> entry : map.entrySet()){
            addEntryToGrid(entry, count, layout);
            count++;
        }

    }


}