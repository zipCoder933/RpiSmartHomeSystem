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
public class Switch extends Component {

    public Switch() throws NoSuchMethodException {
        super("Bell", false);
        getProperties().put("Activated", new Value(Type.BOOLEAN, IsWritable.READ_ONLY, false));
        setMethod(ComponentType.SWITCH);
    }
}
