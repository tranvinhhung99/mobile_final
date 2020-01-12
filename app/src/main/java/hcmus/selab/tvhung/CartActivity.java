package hcmus.selab.tvhung;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import hcmus.selab.tvhung.adapters.ProductAdapter;
import hcmus.selab.tvhung.models.Product;
import hcmus.selab.tvhung.models.ProductBuilder;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private static String TAG = CartActivity.class.toString();

    private class CartItem extends Product{
        private long numProduct;

        CartItem(Product p, long n){
            super(p);
            numProduct = n;
        }
    }

    private ArrayList<CartItem> mCartItems;
    private CartAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mCartItems = new ArrayList<>();

        // Casting to adapt to ProductAdapter
        List<Product> products = (List<Product>) (List<? extends  Product>) mCartItems;
        mAdapter = new CartAdapter(getBaseContext(), R.layout.item_cart_layout, products);
        ((ListView) findViewById(R.id.items_list_view)).setAdapter(mAdapter);

        // Query data in database to show
        queryData();

    }

    private void queryData(){

        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                .child("carts").child(FirebaseAuth.getInstance().getUid());

        final DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference()
                .child("products");

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            int numItemNeedToQuery = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Map<String, Object> data = (HashMap<String, Object>) dataSnapshot.getValue();

                numItemNeedToQuery += data.size();
                for(Map.Entry<String, Object> entry : data.entrySet()){
                    queryItemData(entry.getKey(), (Long) entry.getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }


            private void queryItemData(final String itemId, final long quantity){
                itemsRef.child(itemId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.i(TAG, itemId);
                        Map data = (HashMap<String, Object>) dataSnapshot.getValue();


                        ProductBuilder builder = new ProductBuilder();
                        builder.setValue(data).setId(itemId);

                        Product p = builder.createProduct();

                        File file = new File(getFilesDir() + "/" + p.getId() + ".jpeg");
                        Log.d(TAG, file.toString());
                        if(file.exists())
                            p.setAvatarPath(file.toString());

                        CartItem cartItem = new CartItem(p, quantity);

                        // Add to global data
                        mCartItems.add(cartItem);


                        numItemNeedToQuery--;

                        // Update UI when numItemNeedToQuery == 0
                        if(numItemNeedToQuery == 0)
                            mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });
            }


        });



    }


    // Adapter to load on ListView
    private class CartAdapter extends ProductAdapter {
        private class CartItemViewHolder extends ProductAdapter.ViewHolder {
            TextView textViewQuantity;

            // For the sake of compiler
            @Override
            protected void copy(ViewHolder other) {
                super.copy(other);
            }
        }

        public CartAdapter(Context context, int resource, List<Product> objects) {
            super(context, resource, objects);
        }


        @Override
        protected CartItemViewHolder getViewHolder(View v) {
            ViewHolder holder = super.getViewHolder(v);
            CartItemViewHolder cartItemViewHolder = new CartItemViewHolder();
            cartItemViewHolder.copy(holder);
            cartItemViewHolder.textViewQuantity = v.findViewById(R.id.text_view_quantity);
            return cartItemViewHolder;
        }

        @Override
        protected void inflateViewHolder(int position, ViewHolder holder) {
            super.inflateViewHolder(position, holder);

            CartItemViewHolder cartViewHolder = (CartItemViewHolder) holder;
            cartViewHolder.textViewQuantity.setText("x" + mCartItems.get(position).numProduct);
        }
    }



}
