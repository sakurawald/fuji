package io.github.sakurawald.core.event.impl;

import io.github.sakurawald.core.event.abst.Event;

public class ServerLifecycleEvents {

    public static Event<ExampleEventCallback> EXAMPLE_EVENT = new Event<>((listeners) -> (a, b) -> {
        for (ExampleEventCallback listener : listeners) {
            listener.interact(a, b);
        }
    });

    public interface ExampleEventCallback {
        void interact(int a, int b);
    }

}
