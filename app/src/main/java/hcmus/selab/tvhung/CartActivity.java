package hcmus.selab.tvhung;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import hcmus.selab.tvhung.adapters.ProductAdapter;
import hcmus.selab.tvhung.models.Product;
import hcmus.selab.tvhung.models.ProductBuilder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CartActivity.class.toString();
    private static final int USER_INFORMATION_REQUEST = 1000;
    private static final int SUCCESS_INFORMATION_SHOW = 1001;


    private class CartItem extends Product{
        private long numProduct;

        CartItem(Product p, long n){
            super(p);
            numProduct = n;
        }
    }

    private ArrayList<CartItem> mCartItems;
    private CartAdapter mAdapter;
    private DatabaseReference mCartRef;

    // Buttons handle

    private ImageButton btnBack_cart;
    private ImageView img_empty_cart;
    private LinearLayout layout_price_cart;
    private TextView total_cart;
    private Button btnPay_cart, btnBuy_cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mCartItems = new ArrayList<>();

        // Find buttons
        btnBack_cart = findViewById(R.id.btnBack_cart);
        img_empty_cart = findViewById(R.id.img_empty_cart);
        layout_price_cart = findViewById(R.id.layout_price_cart);
        total_cart = findViewById(R.id.text_view_total_price);
        btnBuy_cart = findViewById(R.id.btnBuy_cart);
        btnPay_cart = findViewById(R.id.btnPay_cart);

        // Casting to adapt to ProductAdapter
        List<Product> products = (List<Product>) (List<? extends  Product>) mCartItems;
        mAdapter = new CartAdapter(getBaseContext(), R.layout.item_cart_layout, products);
        ((ListView) findViewById(R.id.items_list_view)).setAdapter(mAdapter);

        // Query data in database to show
        queryData();

        // Add Listener to btn
        btnBack_cart.setOnClickListener(this);
        btnBuy_cart.setOnClickListener(this);
        btnPay_cart.setOnClickListener(this);
        img_empty_cart.setOnClickListener(this);
        layout_price_cart.setOnClickListener(this);
        total_cart.setOnClickListener(this);

    }

    private void queryData(){

        mCartRef = FirebaseDatabase.getInstance().getReference()
                .child("carts").child(FirebaseAuth.getInstance().getUid());

        final DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference()
                .child("products");

        mCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            int numItemNeedToQuery = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Map<String, Object> data = (HashMap<String, Object>) dataSnapshot.getValue();

                if(data == null){
                    Log.d(TAG, "This user doesn't have anything in cart yet");
//                    Toast.makeText(getBaseContext(), "There are no items", Toast.LENGTH_SHORT).show();

//                    img_empty_cart.setVisibility(View.VISIBLE);
//                    btnBuy_cart.setVisibility(View.VISIBLE);
//                    btnPay_cart.setVisibility(View.INVISIBLE);
//                    layout_price_cart.setVisibility(View.VISIBLE);
                    return;
                }

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
                        {
                            mAdapter.notifyDataSetChanged();
                        }
                        if (mCartItems.size() <= 0)
                        {
                            img_empty_cart.setVisibility(View.VISIBLE);
                            btnBuy_cart.setVisibility(View.VISIBLE);
                            btnPay_cart.setVisibility(View.INVISIBLE);
                            layout_price_cart.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            img_empty_cart.setVisibility(View.INVISIBLE);
                            btnBuy_cart.setVisibility(View.INVISIBLE);
                            btnPay_cart.setVisibility(View.VISIBLE);
                            layout_price_cart.setVisibility(View.VISIBLE);
                        }
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


        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            total_cart.setText(decimalFormat.format(getTotalPrice()) + "đ");
        }
    }


    private long getTotalPrice(){
        long result = 0;
        for(CartItem item : mCartItems){
            result += item.getPrice() * item.numProduct;
        }
        return result;
    }

    private void buyRequest(Map<String, Object> userInformation){
        DatabaseReference orderReference = FirebaseDatabase.getInstance().getReference()
                .child("orders/current");
        
        // Get data for request
        String orderId = orderReference.push().getKey();

        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        String formattedString = dateFormat.format(today);

        Map<String, Object> items = new HashMap<>();
        for(CartItem item : mCartItems){
            items.put(item.getId(), item.numProduct);
        }

        String uid = FirebaseAuth.getInstance().getUid();
        userInformation.put("uid", uid);

        // Generate output map
        Map<String, Object> outputData = new HashMap<>();
        outputData.put("date", formattedString);
        outputData.put("user", userInformation);
        outputData.put("items", items);

        // Send data to Firebase
        Map<String, Object> updateChild = new HashMap<>();
        updateChild.put("/" + orderId, outputData);

        orderReference.updateChildren(updateChild).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Buy success");

                removeCart();

                Intent successIntent = new Intent(getBaseContext(), SuccessOrderActivity.class);
                successIntent.putExtra("total_price", getTotalPrice());
                startActivityForResult(successIntent, SUCCESS_INFORMATION_SHOW);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == USER_INFORMATION_REQUEST && resultCode == RESULT_OK){

            // Convert Intent data to map
            Map<String, Object> userInformation = new HashMap<>();
            userInformation.put("name", data.getStringExtra("name"));
            userInformation.put("address", data.getStringExtra("address"));
            userInformation.put("phone", data.getStringExtra("phone"));

            buyRequest(userInformation);

        }
        else if(requestCode == SUCCESS_INFORMATION_SHOW){
            finish();
        }

    }

    private void removeCart(){
        Log.d(TAG, "remove cart");
        mCartRef.removeValue().isSuccessful();



    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.btnPay_cart)
            startActivityForResult(
                    new Intent(getBaseContext(), PaymentActivity.class),
                    USER_INFORMATION_REQUEST
            );
        if(id == R.id.btnBack_cart || id == R.id.btnBuy_cart)
        {
            finish();
        }

    }


}
