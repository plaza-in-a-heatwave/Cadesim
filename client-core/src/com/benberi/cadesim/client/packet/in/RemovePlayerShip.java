package com.benberi.cadesim.client.packet.in;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;
import com.benberi.cadesim.game.entity.vessel.Vessel;

public class RemovePlayerShip extends ClientPacketExecutor {

    public RemovePlayerShip(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Packet p) {
        String name = p.readByteString();
        Vessel vessel = getContext().getEntities().getVesselByName(name);
        if (vessel != null) {
            getContext().getEntities().remove(vessel);
        }
        HashSet<Object> seen=new HashSet<>();
        getContext().getEntities().vessels.removeIf(e->!seen.add(e.getName()));
    }

    @Override
    public int getSize() {
        return -1;
    }
    
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
