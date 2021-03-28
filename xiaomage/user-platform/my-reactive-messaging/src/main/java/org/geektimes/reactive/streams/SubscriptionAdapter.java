package org.geektimes.reactive.streams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class SubscriptionAdapter implements Subscription {

    private final DecoratingSubscriber<?> subscriber;

    public SubscriptionAdapter(Subscriber<?> subscriber) {
        this.subscriber = new DecoratingSubscriber<>(subscriber);
    }

    @Override
    public void request(long l) {
        if (l < 1) {
            throw new IllegalArgumentException("The number of elements to requests must be more than zero!");
        }
        this.subscriber.setMaxRequest(l);
    }

    @Override
    public void cancel() {
        this.subscriber.cancel();
    }

    public Subscriber<?> getSubscriber() {
        return subscriber;
    }

}
