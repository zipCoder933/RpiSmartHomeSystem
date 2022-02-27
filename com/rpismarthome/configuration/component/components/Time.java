package com.rpismarthome.configuration.component.components;

import com.rpismarthome.configuration.component.Component;
import com.rpismarthome.configuration.component.ComponentType;
import com.rpismarthome.configuration.component.IsWritable;
import com.rpismarthome.configuration.component.Type;
import com.rpismarthome.configuration.component.Value;

public class Time extends Component {

    public Time() throws NoSuchMethodException {
        super("Time Sensor",true);
        getProperties().put("Hour", new Value(Type.NUMBER,IsWritable.READ_ONLY,0));
        getProperties().put("Minute", new Value(Type.NUMBER,IsWritable.READ_ONLY,0));
        getProperties().put("Second", new Value(Type.NUMBER,IsWritable.READ_ONLY,0));
        setMethod(ComponentType.TIME);
    }


}
