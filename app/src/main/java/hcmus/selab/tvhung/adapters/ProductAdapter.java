package hcmus.selab.tvhung.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import hcmus.selab.tvhung.CartActivity;
import hcmus.selab.tvhung.R;
import hcmus.selab.tvhung.models.Product;

public class ProductAdapter extends ArrayAdapter<Product> {
    private final int           mResourceId;
    private final List<Product> mProducts;

    protected class ViewHolder{
        ImageView avatar;
        TextView  name;
        TextView  price;
        RatingBar ratingBar;
        TextView numRating;

        protected void copy(ViewHolder other){
            avatar = other.avatar;
            name = other.name;
            price = other.price;
            ratingBar = other.ratingBar;
            numRating = other.numRating;
        }
    }

    public ProductAdapter(Context context, int resource, List<Product> objects) {
        super(context, resource, objects);
        mResourceId = resource;
        mProducts = objects;
    }


    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if(v == null){
            v = LayoutInflater.from(getContext()).inflate(mResourceId, null);

            holder = this.getViewHolder(v);

            v.setTag(holder);
        }
        else{
            holder = (ViewHolder) v.getTag();
        }

        this.inflateViewHolder(position, holder);

        return v;
    }

    protected ViewHolder getViewHolder(View v) {
        ViewHolder holder;
        holder = new ViewHolder();
        holder.avatar = v.findViewById(R.id.item_avatar);
        holder.name = v.findViewById(R.id.text_view_item_name);
        holder.price = v.findViewById(R.id.text_view_item_price);
        holder.ratingBar = v.findViewById(R.id.rating_bar);
        holder.numRating = v.findViewById(R.id.text_view_num_rating);
        return holder;
    }

    protected void inflateViewHolder(int position, ViewHolder holder) {
        Product currentItem = mProducts.get(position);
        setAvatar(holder.avatar, currentItem);
        holder.name.setText(currentItem.getName());


//        holder.numRating.setText(Resources.getSystem().getString(R.string.format_num_rating, currentItem.getNumRating()));
        holder.numRating.setText(String.format("(%d)", currentItem.getNumRating()));
//        holder.price.setText(Resources.getSystem().getString(R.string.format_price, currentItem.getPrice()));
        holder.price.setText(String.format("%dd", currentItem.getPrice()));
        holder.ratingBar.setRating(currentItem.getRating());

        //TODO: Add listener for logic of ratingBar

    }


    private void setAvatar(ImageView imageView, Product product){
        //TODO: Edit getting image avatar later.
        imageView.setImageBitmap(product.getAvatar());

    }

    private void addProduct(Product product){
        mProducts.add(product);
    }

}
