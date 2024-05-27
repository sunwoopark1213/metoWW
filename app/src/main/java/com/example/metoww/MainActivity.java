package com.example.metoww;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView tvCityName, tvTemperature, tvWeather, tvMinMaxTemp, tvWind, tvHumidity, tvPrecipitation;
    private ImageView ivWeatherIcon;
    private Button btnOpenCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCityName = findViewById(R.id.tvCityName);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvWeather = findViewById(R.id.tvWeather);
        tvMinMaxTemp = findViewById(R.id.tvMinMaxTemp);
        tvWind = findViewById(R.id.tvWind);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvPrecipitation = findViewById(R.id.tvPrecipitation);
        ivWeatherIcon = findViewById(R.id.ivWeatherIcon);
        btnOpenCalendar = findViewById(R.id.btnOpenCalendar);

        fetchWeatherData();

        btnOpenCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendarActivity();
            }
        });
    }

    private void fetchWeatherData() {
        String url = "https://api.open-meteo.com/v1/forecast?latitude=37.566&longitude=126.9784&current=temperature_2m,relative_humidity_2m,precipitation,rain,snowfall,weather_code,wind_speed_10m,wind_direction_10m&hourly=temperature_2m&daily=weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,uv_index_max,precipitation_sum,precipitation_hours,precipitation_probability_max,wind_speed_10m_max,wind_gusts_10m_max";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Assuming "Seoul" as the city name for simplicity
                            tvCityName.setText("Seoul");

                            JSONObject currentWeather = response.getJSONObject("current");
                            double temperature = currentWeather.getDouble("temperature_2m");
                            int humidity = currentWeather.getInt("relative_humidity_2m");
                            double precipitation = currentWeather.getDouble("precipitation");
                            int weatherCode = currentWeather.getInt("weather_code");
                            double windSpeed = currentWeather.getDouble("wind_speed_10m");
                            int windDirection = currentWeather.getInt("wind_direction_10m");

                            // Set current weather data
                            tvTemperature.setText("Temperature: " + temperature + "°C");
                            tvHumidity.setText("Humidity: " + humidity + "%");
                            tvPrecipitation.setText("Precipitation: " + precipitation + "mm");
                            tvWind.setText("Wind: " + windSpeed + " m/s, Direction: " + windDirection + "°");

                            // Fetch daily weather data
                            JSONObject dailyWeather = response.getJSONObject("daily");
                            JSONArray tempMaxArray = dailyWeather.getJSONArray("temperature_2m_max");
                            JSONArray tempMinArray = dailyWeather.getJSONArray("temperature_2m_min");
                            JSONArray weatherCodeArray = dailyWeather.getJSONArray("weather_code");

                            if (tempMaxArray.length() > 0 && tempMinArray.length() > 0 && weatherCodeArray.length() > 0) {
                                double tempMax = tempMaxArray.getDouble(0);
                                double tempMin = tempMinArray.getDouble(0);
                                int dailyWeatherCode = weatherCodeArray.getInt(0);

                                tvMinMaxTemp.setText("Max/Min Temp: " + tempMax + "°C / " + tempMin + "°C");
                                tvWeather.setText("Weather: " + getWeatherDescription(dailyWeatherCode));

                                // Set weather icon based on weather code
                                ivWeatherIcon.setImageResource(getWeatherIcon(dailyWeatherCode));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void openCalendarActivity() {
        Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
        startActivity(intent);
    }

    private String getWeatherDescription(int weatherCode) {
        switch (weatherCode) {
            case 0: return "Clear sky";
            case 1: return "Mainly clear";
            case 2: return "Partly cloudy";
            case 3: return "Overcast";
            case 45: return "Fog";
            case 48: return "Depositing rime fog";
            case 51: return "Drizzle: Light";
            case 53: return "Drizzle: Moderate";
            case 55: return "Drizzle: Dense intensity";
            case 56: return "Freezing Drizzle: Light";
            case 57: return "Freezing Drizzle: Dense intensity";
            case 61: return "Rain: Slight";
            case 63: return "Rain: Moderate";
            case 65: return "Rain: Heavy intensity";
            case 66: return "Freezing Rain: Light";
            case 67: return "Freezing Rain: Heavy intensity";
            case 71: return "Snow fall: Slight";
            case 73: return "Snow fall: Moderate";
            case 75: return "Snow fall: Heavy intensity";
            case 77: return "Snow grains";
            case 80: return "Rain showers: Slight";
            case 81: return "Rain showers: Moderate";
            case 82: return "Rain showers: Violent";
            case 85: return "Snow showers: Slight";
            case 86: return "Snow showers: Heavy";
            case 95: return "Thunderstorm: Slight or moderate";
            case 96: return "Thunderstorm with slight hail";
            case 99: return "Thunderstorm with heavy hail";
            default: return "Unknown";
        }
    }

    private int getWeatherIcon(int weatherCode) {
        switch (weatherCode) {
            case 0: return R.drawable.ic_clear_sky;
            case 1: return R.drawable.ic_mainly_clear;
            case 2: return R.drawable.ic_partly_cloudy;
            case 3: return R.drawable.ic_overcast;
            case 45: return R.drawable.ic_fog;
            case 48: return R.drawable.ic_rime_fog;
            case 51: return R.drawable.ic_drizzle_light;
            case 53: return R.drawable.ic_drizzle_moderate;
            case 55: return R.drawable.ic_drizzle_dense;
            case 56: return R.drawable.ic_freezing_drizzle_light;
            case 57: return R.drawable.ic_freezing_drizzle_dense;
            case 61: return R.drawable.ic_rain_slight;
            case 63: return R.drawable.ic_rain_moderate;
            case 65: return R.drawable.ic_rain_heavy;
            case 66: return R.drawable.ic_freezing_rain_light;
            case 67: return R.drawable.ic_freezing_rain_heavy;
            case 71: return R.drawable.ic_snow_slight;
            case 73: return R.drawable.ic_snow_moderate;
            case 75: return R.drawable.ic_snow_heavy;
            case 77: return R.drawable.ic_snow_grains;
            case 80: return R.drawable.ic_rain_showers_slight;
            case 81: return R.drawable.ic_rain_showers_moderate;
            case 82: return R.drawable.ic_rain_showers_violent;
            case 85: return R.drawable.ic_snow_showers_slight;
            case 86: return R.drawable.ic_snow_showers_heavy;
            case 95: return R.drawable.ic_thunderstorm_slight_moderate;
            case 96: return R.drawable.ic_thunderstorm_slight_hail;
            case 99: return R.drawable.ic_thunderstorm_heavy_hail;
            default: return R.drawable.ic_unknown;
        }
    }
}
