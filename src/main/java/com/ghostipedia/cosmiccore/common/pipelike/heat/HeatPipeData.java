package com.ghostipedia.cosmiccore.common.pipelike.heat;

import com.ghostipedia.cosmiccore.api.pipe.HeatPipeProperties;
import com.gregtechceu.gtceu.api.pipenet.IAttachData;
import com.gregtechceu.gtceu.common.pipelike.fluidpipe.FluidPipeData;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.core.Direction;

import java.util.Objects;

@Accessors(fluent = true)
public class HeatPipeData implements IAttachData {

    @Getter
    HeatPipeProperties props;
    @Getter
    byte connections;

    public HeatPipeData(HeatPipeProperties properties, byte connections) {
        this.props = properties;
        this.connections = connections;
    }

    @Override
    public boolean canAttachTo(Direction side) {
        return (connections & (1 << side.ordinal())) != 0;
    }

    @Override
    public boolean setAttached(Direction side, boolean attach) {
        var result = canAttachTo(side);
        if (result != attach) {
            if (attach) {
                connections |= (1 << side.ordinal());
            } else {
                connections &= ~(1 << side.ordinal());
            }
        }
        return result != attach;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HeatPipeData cableData) {
            return cableData.props.equals(props) && connections == cableData.connections;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(props, connections);
    }

}
