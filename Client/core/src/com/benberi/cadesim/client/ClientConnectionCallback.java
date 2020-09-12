package com.benberi.cadesim.client;

import io.netty.channel.Channel;

public interface ClientConnectionCallback {
    public void onSuccess(Channel channel);
    public void onFailure();
}
