package com.rpismarthome.configuration2.component.components;

import com.rpismarthome.configuration2.component.Component;
import com.rpismarthome.configuration2.component.ComponentType;
import com.rpismarthome.configuration2.component.IsWritable;
import com.rpismarthome.configuration2.component.Type;
import com.rpismarthome.configuration2.component.Value;

public class Time extends Component {

    public Time() throws NoSuchMethodException {
        super("Time Sensor",true);
        getProperties().put("Hour", new Value(Type.NUMBER,IsWritable.READ_ONLY,0));
        getProperties().put("Minute", new Value(Type.NUMBER,IsWritable.READ_ONLY,0));
        getProperties().put("Second", new Value(Type.NUMBER,IsWritable.READ_ONLY,0));
        setMethod(ComponentType.TIME);
    }


}
