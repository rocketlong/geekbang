package org.geektimes.reactive.streams;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.LinkedList;
import java.util.List;

public class SimplePublisher<T> implements Publisher<T> {

    private final List<Subscriber> subscribers = new LinkedList<>();

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        SubscriptionAdapter subscriptionAdapter = new SubscriptionAdapter(subscriber);
        subscriber.onSubscribe(subscriptionAdapter);
        subscribers.add(subscriptionAdapter.getSubscriber());
    }

    public void publish(T data) {
        subscribers.forEach(subscriber -> subscriber.onNext(data));
    }

}
