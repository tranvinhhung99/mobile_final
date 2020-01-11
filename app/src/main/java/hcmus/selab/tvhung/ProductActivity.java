package hcmus.selab.tvhung;

import androidx.appcompat.app.AppCompatActivity;
import hcmus.selab.tvhung.models.Product;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class ProductActivity extends AppCompatActivity {

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




    }
}
