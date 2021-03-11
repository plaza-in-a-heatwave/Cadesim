package com.benberi.cadesim.util;

public class Tuple<A, B> {
    private final A mFirst;
    private final B mSecond;

    public Tuple(final A first, final B second) {
        this.mFirst = first;
        this.mSecond = second;
    }

    public A getFirst() {
        return this.mFirst;
    }

    public B getSecond() {
        return this.mSecond;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.mFirst.hashCode();
        result = prime * result + this.mSecond.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tuple<?, ?> other = (Tuple<?, ?>) obj;
        if (this.mFirst == null) {
            if (other.mFirst != null) {
                return false;
            }
        } else if (!this.mFirst.equals(other.mFirst)) {
            return false;
        }
        if (this.mSecond == null) {
            if (other.mSecond != null) {
                return false;
            }
        } else if (!this.mSecond.equals(other.mSecond)) {
            return false;
        }
        return true;
    }
}