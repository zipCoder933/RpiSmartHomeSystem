package com.rpismarthome.configuration2.component.components;

import com.rpismarthome.configuration2.component.Component;
import com.rpismarthome.configuration2.component.ComponentType;
import com.rpismarthome.configuration2.component.values.config.ConfigType;
import com.rpismarthome.configuration2.component.values.config.ConfigValue;
import com.rpismarthome.configuration2.component.IsWritable;
import com.rpismarthome.configuration2.component.Type;
import com.rpismarthome.configuration2.component.Value;
import com.weather.weather.OpenWeatherAPIClient;
import com.weather.weather.weatherConditions.WeatherConditions;

public class WeatherAPI extends Component {



    public WeatherAPI() throws NoSuchMethodException {
        super("Weather API Sensor",true);
        setMethod(ComponentType.WEATHER_API);
        getProperties().put("Temperature (C)", new Value(Type.NUMBER, IsWritable.READ_ONLY, 0));
        getProperties().put("Feels Like (C)", new Value(Type.NUMBER, IsWritable.READ_ONLY, 0));
        getProperties().put("Min Temperature (C)", new Value(Type.NUMBER, IsWritable.READ_ONLY, 0));
        getProperties().put("Max Temperature (C)", new Value(Type.NUMBER, IsWritable.READ_ONLY, 0));
        getProperties().put("Pressure (hPa)", new Value(Type.NUMBER, IsWritable.READ_ONLY, 0));
        getProperties().put("Humidity (%)", new Value(Type.NUMBER, IsWritable.READ_ONLY, 0));
        getProperties().put("Wind Speed (m/s)", new Value(Type.NUMBER, IsWritable.READ_ONLY, 0));
        getProperties().put("Wind Gust (m/s)", new Value(Type.NUMBER, IsWritable.READ_ONLY, 0));
        getProperties().put("Wind Direction", new Value(Type.NUMBER, IsWritable.READ_ONLY, 0));
        
        getConfigProperties().put("Location", new ConfigValue(ConfigType.STRING, ""));
    }

}
