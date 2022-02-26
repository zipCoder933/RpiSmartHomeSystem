/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rpismarthome.configuration2.component.components;

import com.rpismarthome.configuration2.component.Component;
import com.rpismarthome.configuration2.component.ComponentType;
import com.rpismarthome.configuration2.component.values.config.ConfigType;
import com.rpismarthome.configuration2.component.values.config.ConfigValue;
import com.rpismarthome.configuration2.component.IsWritable;
import com.rpismarthome.configuration2.component.Type;
import com.rpismarthome.configuration2.component.Value;

/**
 *
 * @author zipCoder933
 */
public class LightSensor extends Component {
    
    public LightSensor() throws NoSuchMethodException {
        super("Light Sensor",true);
        getProperties().put("Light", new Value(Type.NUMBER, IsWritable.READ_ONLY, 0));
        getConfigProperties().put("Pin", new ConfigValue(ConfigType.PIN, null));
        setMethod(ComponentType.LIGHT_SENSOR);
    }
}
