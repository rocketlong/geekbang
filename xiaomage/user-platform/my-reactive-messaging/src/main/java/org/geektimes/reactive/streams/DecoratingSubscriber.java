package org.geektimes.reactive.streams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.logging.Logger;

public class DecoratingSubscriber<T> implements Subscriber<T> {

    private final static Logger logger = Logger.getLogger(DecoratingSubscriber.class.getName());

    private final Subscriber<T> source;

    private long maxRequest;

    private boolean canceled = false;

    private boolean completed = false;

    private long requestCount = 0;

    public DecoratingSubscriber(Subscriber<T> source) {
        this.source = source;
    }

    @Override
    public void onSubscribe(Subscription s) {
        source.onSubscribe(s);
    }

    @Override
    public void onNext(T t) {
        if (maxRequest < 0) {
            throw new IllegalStateException("the number of request must be initialized");
        }
        if (completed) {
            logger.severe("The data subscription was completed");
            return;
        }
        if (canceled) {
            logger.warning("The Subscriber has canceled the data subscription");
            return;
        }
        if (requestCount == maxRequest) {
            onComplete();
            return;
        }
        try {
            source.onNext(t);
        } catch (Throwable e) {
            onError(e);
        } finally {
            requestCount++;
        }
    }

    @Override
    public void onError(Throwable t) {
        source.onError(t);
    }

    @Override
    public void onComplete() {
        source.onComplete();
        this.completed = true;
    }

    public void setMaxRequest(long maxRequest) {
        this.maxRequest = maxRequest;
    }

    public void cancel() {
        this.canceled = true;
    }

}
