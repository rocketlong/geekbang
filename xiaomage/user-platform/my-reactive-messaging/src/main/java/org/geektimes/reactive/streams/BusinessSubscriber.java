package org.geektimes.reactive.streams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class BusinessSubscriber<T> implements Subscriber<T> {

    private final long maxRequest;

    public BusinessSubscriber(long maxRequest) {
        this.maxRequest = maxRequest;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(maxRequest);
    }

    @Override
    public void onNext(T t) {
        System.out.println("收到数据：" + t);
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("遇到异常：" + throwable);
    }

    @Override
    public void onComplete() {
        System.out.println("收到数据完成");
    }

}
