package com.udacity.sandwichclub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.sandwichclub.model.Sandwich;
import com.udacity.sandwichclub.utils.JsonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;
    private static  final int[] IMG_IDS_ON_ERROR = {R.drawable.ham_n_cheese, R.drawable.bosna,
    R.drawable.chivito, R.drawable.club_sandwich, R.drawable.gua_bao, R.drawable.medianoche,
    R.drawable.pljeskavica, R.drawable.roujiamo,R.drawable.shawarma, R.drawable.vada_paav};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView ingredientsIv = findViewById(R.id.image_iv);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        int position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            closeOnError();
            return;
        }

        String[] sandwiches = getResources().getStringArray(R.array.sandwich_details);
        String json = sandwiches[position];
        Sandwich sandwich = JsonUtils.parseSandwichJson(json);
        if (sandwich == null) {
            // Sandwich data unavailable
            closeOnError();
            return;
        }

        populateUI(sandwich);
        Picasso.with(this)
                .load(sandwich.getImage())
                .error(IMG_IDS_ON_ERROR[position])
                .into(ingredientsIv);

        setTitle(sandwich.getMainName());
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

        private void populateUI(final Sandwich sandwich) {
        Map<Integer, String> valueOfTextID = new HashMap<Integer, String>()
        {{
            put(R.id.origin_tv, sandwich.getPlaceOfOrigin());
            put(R.id.also_known_tv, getTextViewValuesFromList(sandwich.getAlsoKnownAs()));
            put(R.id.description_tv,sandwich.getDescription());
            put(R.id.ingredients_tv,getTextViewValuesFromList(sandwich.getIngredients()));
        }};
        for (int id : valueOfTextID.keySet())
        {
            TextView tv =  (TextView) findViewById(id);
            tv.setText(valueOfTextID.get(id) + "\n");
        }
    }

    private String getTextViewValuesFromList(List<String> values)
    {
        if (values.isEmpty())
        {
            return "N/A";
        }
        else
        {
            StringBuilder sb = new StringBuilder(values.get(0));
            for (int i = 1; i < values.size(); ++i)
            {
                sb.append((", " + values.get(i)));
            }
            return sb.toString();
        }
    }
}
