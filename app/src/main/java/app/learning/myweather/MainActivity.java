package app.learning.myweather;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import app.learning.myweather.adapters.ForecastAdapter;
import java.io.IOException;
import java.util.Locale;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

  private TextView textView_current_temp;
  private TextView textView_min;
  private TextView textView_max;
  private TextView textView_location;
  private TextView textView_current_conditions;
  private TextView textView_loading;
  private RecyclerView recyclerview_main;
  private ImageView imageView_current_conditions;


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if(!isInternetAvailable()){
      Toast.makeText(MainActivity.this,"You need internet in order to run this application",Toast.LENGTH_LONG).show();
      finish();
    }

    textView_current_temp = findViewById(R.id.textView_current_temp);
    textView_loading = findViewById(R.id.textView_loading);
    textView_min = findViewById(R.id.textView_min);
    textView_max = findViewById(R.id.textView_max);
    textView_location = findViewById(R.id.textView_location);
    textView_current_conditions = findViewById(R.id.textView_current_conditions);
    recyclerview_main = findViewById(R.id.recyclerview_main);
    imageView_current_conditions = findViewById(R.id.imageView_current_conditions);

    recyclerview_main.setHasFixedSize(true);
    LinearLayoutManager llm = new LinearLayoutManager(this);
    llm.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerview_main.setLayoutManager(llm);

    load_weather_data();
  }


  private void load_weather_data() {

    OkHttpClient client = new OkHttpClient();

    Request request = new Request.Builder().url(
        "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22Sofia%2C%20Bg%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys")
        .build();

    client.newCall(request).enqueue(new Callback() {
      @Override public void onFailure(Call call, IOException e) {

      }

      @Override public void onResponse(Call call, Response response) throws IOException {
        try {



          JSONObject jsonObject = new JSONObject(response.body().string());
          JSONObject channelJsonObject = jsonObject.getJSONObject("query").getJSONObject("results").getJSONObject("channel");
          String city = channelJsonObject.getJSONObject("location").getString("city");
          String country = channelJsonObject.getJSONObject("location").getString("country");

          update_location(city, country);

          String temp = channelJsonObject.getJSONObject("item").getJSONObject("condition").getString("temp");
          String code = channelJsonObject.getJSONObject("item").getJSONObject("condition").getString("code");
          String current_conditions_text = channelJsonObject.getJSONObject("item").getJSONObject("condition").getString("text");

          String current_min =  ((JSONObject) channelJsonObject.getJSONObject("item").getJSONArray("forecast").get(0)).getString("low");
          String current_max =  ((JSONObject) channelJsonObject.getJSONObject("item").getJSONArray("forecast").get(0)).getString("high");

          update_current_conditions(temp, code, current_conditions_text, current_min, current_max);

          //----- forecast -----
          JSONArray jsonArrayForecast = channelJsonObject.getJSONObject("item").getJSONArray("forecast");
          update_forecast(jsonArrayForecast);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    });
  }

  private void update_forecast(final JSONArray jsonArrayForecast) {
    MainActivity.this.runOnUiThread(new Runnable() {
      @Override public void run() {
        ForecastAdapter forecastAdapter = new ForecastAdapter(jsonArrayForecast);
        recyclerview_main.setAdapter(forecastAdapter);
      }
    });
  }

  private void update_location(final String city, final String country) {
    MainActivity.this.runOnUiThread(new Runnable() {
      @Override public void run() {
        textView_location.setText(String.format("%s, %s", city, country));
      }
    });
  }

  private void update_current_conditions(final String temp, final String code, final String current_conditions_text, final String current_min,
      final String current_max) {
    MainActivity.this.runOnUiThread(new Runnable() {
      @Override public void run() {

        textView_loading.setVisibility(View.GONE);

        textView_current_temp.setText(String.format(Locale.getDefault(), "%.1f°", getCelsiusFromFahrenheit(Double.valueOf(temp))));
        textView_min.setText(String.format(Locale.getDefault(), "Min %.0f°", getCelsiusFromFahrenheit(Double.valueOf(current_min))));
        textView_max.setText(String.format(Locale.getDefault(), "Max %.0f°", getCelsiusFromFahrenheit(Double.valueOf(current_max))));
        textView_current_conditions.setText(current_conditions_text);
        update_current_conditions_image(code);
      }
    });
  }

  private void update_current_conditions_image(String code) {
    switch (code) {
      case "27":
        imageView_current_conditions.setImageDrawable(getResources().getDrawable(R.drawable.ic_partialy_cloudy));
        break;
      case "34":
        imageView_current_conditions.setImageDrawable(getResources().getDrawable(R.drawable.ic_light_rain));
        break;
      default:
        imageView_current_conditions.setImageDrawable(getResources().getDrawable(R.drawable.ic_unknown));
        break;
    }
  }

  private double getCelsiusFromFahrenheit(double f) {
    return (f - 32) * 5 / 9;
  }

  private boolean isInternetAvailable() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
  }

}
