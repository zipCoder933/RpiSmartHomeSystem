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
public class VerboseOutput extends Component {

    public VerboseOutput() throws NoSuchMethodException {
        super("Verbose Output", false);
        getProperties().put("Speech", new Value(Type.STRING, IsWritable.WRITABLE, "Hello world!"));
        setOnlyOneSupported(true);
         getProperties().put("Volume", new Value(Type.NUMBER, IsWritable.WRITABLE, 40));
        setMethod(ComponentType.VERBOSE_OUTPUT);
    }
}
