package com.service.payment.peageable;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

public class OffsetBasedPageRequest implements Pageable {

    private final int limit;

    private final int offset;

    private final Sort sort;

    protected OffsetBasedPageRequest(int limit, int offset, Sort sort) {
        if (limit < 1) {
            throw new IllegalArgumentException("Limit must not be less than one!");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset index must not be less than zero!");
        }
        this.limit = limit;
        this.offset = offset;
        Assert.notNull(sort, "Sort must not be null");
        this.sort = sort;
    }

    public static OffsetBasedPageRequest of(int limit, int offset, Sort sort) {
        return new OffsetBasedPageRequest(limit, offset, sort);
    }

    public static OffsetBasedPageRequest of(int limit, int offset) {
        return of(limit, offset, Sort.unsorted());
    }

    public static OffsetBasedPageRequest of(int limit, int offset, Sort.Direction direction, String... properties) {
        return of(limit, offset, Sort.by(direction, properties));
    }

    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetBasedPageRequest(getPageSize(), (int) (getOffset() + getPageSize()), getSort());
    }

    public Pageable previous() {
        return hasPrevious() ?
                new OffsetBasedPageRequest(getPageSize(), (int) (getOffset() - getPageSize()), getSort()) :
                this;
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public Pageable first() {
        return new OffsetBasedPageRequest(getPageSize(), 0, getSort());
    }

    @Override
    public OffsetBasedPageRequest withPage(int pageNumber) {
        return new OffsetBasedPageRequest(getPageSize(), pageNumber * getPageSize(), getSort());
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }
}
