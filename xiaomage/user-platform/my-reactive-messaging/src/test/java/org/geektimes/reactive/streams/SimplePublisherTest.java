package org.geektimes.reactive.streams;

class SimplePublisherTest {

    public static void main(String[] args) {
        SimplePublisher simplePublisher = new SimplePublisher();
        simplePublisher.subscribe(new BusinessSubscriber<>(2));
        for (int i = 0; i < 5; i++) {
            simplePublisher.publish(i);
        }
    }

}