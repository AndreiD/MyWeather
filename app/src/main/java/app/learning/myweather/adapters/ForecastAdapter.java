package app.learning.myweather.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import app.learning.myweather.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForeCastViewHolder> {

  private JSONArray forecastList;

  public ForecastAdapter(JSONArray forecastList) {
    this.forecastList = forecastList;
  }

  @Override public int getItemCount() {
    return 6;
  }

  @Override public void onBindViewHolder(ForeCastViewHolder vh, int position) {

    if (position % 2 == 0) {
      vh.itemView.setBackgroundColor(Color.parseColor("#20000000"));
    } else {
      vh.itemView.setBackgroundColor(Color.TRANSPARENT);
    }

    try {
      JSONObject jsonObjectForecast = forecastList.getJSONObject(position + 1);

      vh.textView_row_when.setText(jsonObjectForecast.getString("day"));
      vh.textView_row_conditions.setText(jsonObjectForecast.getString("text"));
      vh.textView_row_min.setText(String.format("Min %s°", jsonObjectForecast.getString("low")));
      vh.textView_row_max.setText(String.format("Max %s°", jsonObjectForecast.getString("high")));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override public ForeCastViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View itemView = LayoutInflater.
        from(viewGroup.getContext()).
        inflate(R.layout.row_forecast_item, viewGroup, false);

    return new ForeCastViewHolder(itemView);
  }

  static class ForeCastViewHolder extends RecyclerView.ViewHolder {

    TextView textView_row_when;
    TextView textView_row_conditions;
    TextView textView_row_min;
    TextView textView_row_max;

    ForeCastViewHolder(View v) {
      super(v);
      textView_row_when = v.findViewById(R.id.textView_row_when);
      textView_row_conditions = v.findViewById(R.id.textView_row_conditions);
      textView_row_min = v.findViewById(R.id.textView_row_min);
      textView_row_max = v.findViewById(R.id.textView_row_max);
    }
  }
}