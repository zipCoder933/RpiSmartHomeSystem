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
public class RGBLight extends Component {

    public RGBLight() throws NoSuchMethodException {
        super("RGB Light",false);
        getProperties().put("Red", new Value(Type.NUMBER, IsWritable.WRITABLE, 0));
        getProperties().put("Green", new Value(Type.NUMBER, IsWritable.WRITABLE, 0));
        getProperties().put("Blue", new Value(Type.NUMBER, IsWritable.WRITABLE, 0));

        getConfigProperties().put("Red PIN", new ConfigValue(ConfigType.PIN, null));
        getConfigProperties().put("Green PIN", new ConfigValue(ConfigType.PIN, null));
        getConfigProperties().put("Blue PIN", new ConfigValue(ConfigType.PIN, null));
        setMethod(ComponentType.RGB_LIGHT);
    }

}
