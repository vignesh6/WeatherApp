package com.sol.weatherapp.util

import android.R.attr.x
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.sol.weatherapp.R
import java.text.SimpleDateFormat
import java.util.*


object BindingUtil {
    @JvmStatic
    @BindingAdapter("currentTemperature")
    fun setTemperature(view: TextView, temp: Double) {
        if (temp.isNaN()) {
            view.text = view.context.getString(R.string.empty_string)
        } else {
            view.text = temp.toInt().toString()
        }
    }

    @JvmStatic
    @BindingAdapter("setDescription")
    fun setDescription(view: TextView, desc: String) {
        if (desc.isNullOrEmpty()) {
            view.text = view.context.getString(R.string.empty_string)
        } else {
            view.text = desc.capitalize()
        }
    }

    @JvmStatic
    @BindingAdapter("feelsLike")
    fun setFeelsLike(view: TextView, temp: Double) {
        if (temp.isNaN()) {
            view.text = view.context.getString(R.string.feels_like, "")
        } else {
            view.text = view.context.getString(R.string.feels_like, temp.toInt().toString())
        }
    }

    @JvmStatic
    @BindingAdapter("setMinTemp", "setMaxTemp")
    fun setMinMaxTemp(view: TextView, tempMin: Double, tempMax: Double) {
        view.text = view.context.getString(
            R.string.min_max_temp,
            tempMin.toInt().toString(),
            tempMax.toInt().toString()
        )
    }

    @JvmStatic
    @BindingAdapter("setDate")
    fun setDate(view: TextView, time: Int) {
        val simpleDateFormat = SimpleDateFormat("EEE dd-MM-yyyy", Locale.getDefault())
        val date = simpleDateFormat.format(time * 1000L)
        view.text = date.toString()
    }

    @JvmStatic
    @BindingAdapter("setCityName", "setCountryName")
    fun setLocationName(view: TextView, city: String, country: String) {
        val countryStr = Locale("", country).displayCountry
        view.text = view.context.getString(R.string.location_name, city, countryStr)
    }

    @JvmStatic
    @BindingAdapter("setSunTime")
    fun setSunTime(view: TextView, time: Int) {
        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeVal = simpleDateFormat.format(time * 1000L)
        view.text = timeVal.toString()
    }

    @JvmStatic
    @BindingAdapter("setwindSpeed","setDirection")
    fun setwind(view: TextView, speed: Double,windDegree:Int) {
        val windDirections = arrayOf(
            "N",
            "NNE",
            "NE",
            "ENE",
            "E",
            "ESE",
            "SE",
            "SSE",
            "S",
            "SSW",
            "SW",
            "WSW",
            "W",
            "WNW",
            "NW",
            "NNW",
            "N"
        )
        val directionIndex = Math.round(((windDegree / 22.5) + 0.5)).toInt()%16
        val dir = if(directionIndex<=windDirections.size) {
            windDirections[directionIndex]
        }else{
            "--"
        }
        if (speed.isNaN()) {
            view.text = view.context.getString(R.string.wind_speed, "--","--")
        } else {
            val speedVal = 3.6 * speed
            view.text = view.context.getString(R.string.wind_speed, dir,speedVal.toString())
        }
    }

    @JvmStatic
    @BindingAdapter("setHumidity")
    fun setHumidity(view: TextView, humidity: Int) {
        view.text = view.context.getString(R.string.humidity_val, humidity.toString())
    }

    @JvmStatic
    @BindingAdapter("setPressure")
    fun setPressure(view: TextView, pressure: Int) {
        view.text = view.context.getString(R.string.pressure_val, pressure.toString())
    }
    @JvmStatic
    @BindingAdapter("setWeatherIcon","networkState")
    fun setWeatherIcon(view: ImageView, url: String,isConnected:Boolean) {
       if(!url.isNullOrEmpty() && isConnected) {
           val imgUrl = "http://openweathermap.org/img/w/$url.png"
           Glide.with(view.context).load(imgUrl).into(view)
       }
    }
}