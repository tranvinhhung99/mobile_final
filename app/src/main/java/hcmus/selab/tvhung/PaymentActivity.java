package hcmus.selab.tvhung;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class PaymentActivity extends AppCompatActivity {

    private TextInputEditText mName, mAddress, mPhone;
    private CheckBox chk_StandardShipping, chk_ExpressShipping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Find View
        mName = findViewById(R.id.customer_name);
        mAddress = findViewById(R.id.customer_address);
        mPhone = findViewById(R.id.customer_phone);

        chk_StandardShipping = findViewById(R.id.chk_StandardShipping);
        chk_ExpressShipping = findViewById(R.id.chk_ExpressShipping);

        // Set on click listener
        findViewById(R.id.btn_buy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buy();
            }
        });

        chk_StandardShipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox)view).isChecked()){
                    Toast.makeText(PaymentActivity.this,
                            "You have chosen Standard Shipping", Toast.LENGTH_LONG).show();
                }

            }
        });

        chk_ExpressShipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox)view).isChecked()){
                    Toast.makeText(PaymentActivity.this,
                            "You have chosen Express Shipping", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void buy(){
        String name =  mName.getText().toString();
        String address = mAddress.getText().toString();
        String phone = mPhone.getText().toString();

        if(!validateCustomerInformation(name, address, phone))
            return;

        Intent data = new Intent();

        data.putExtra("name", name);
        data.putExtra("address", address);
        data.putExtra("phone", phone);

        setResult(RESULT_OK, data);
        finish();

    }

    private boolean validateCustomerInformation(String name, String address, String phone){

        if(name == null || name.isEmpty()){
            Toast.makeText(getBaseContext(), "Invalid customer's name", Toast.LENGTH_LONG).show();
            return false;
        }


        if(address == null || address.isEmpty()){
            Toast.makeText(getBaseContext(), "Invalid customer's address", Toast.LENGTH_LONG).show();
            return false;
        }


        if(phone == null || phone.length() != 10){
            Toast.makeText(getBaseContext(), "Invalid customer's phone number", Toast.LENGTH_LONG).show();
            return false;
        }


        return true;
    }


}
