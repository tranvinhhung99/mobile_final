package hcmus.selab.tvhung;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.Format;

public class SuccessOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_order);


        Intent inputIntent = getIntent();
        TextView textView = findViewById(R.id.text_view_total_price);
        textView.setText(
                String.format(
                        textView.getText().toString(),
                        inputIntent.getLongExtra("total_price", 0)
                )
        );


        findViewById(R.id.btn_continue_shopping).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SuccessOrderActivity.this.finish();
            }
        });

    }
}
