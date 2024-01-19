package com.service.payment.utils;

import com.service.payment.peageable.OffsetBasedPageRequest;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PaginationSortingUtilsTest {

    @Test
    public void getPageable_whenSortIsNull_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> PaginationSortingUtils.getPageable(0, 10, null));
    }

    @Test
    public void getPageable_whenSortLengthIsLessThanTwo_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> PaginationSortingUtils.getPageable(0, 10, new String[]{}));
    }

    @Test
    public void getPageable_whenSortFieldIsNull_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> PaginationSortingUtils.getPageable(0, 10, new String[]{null, "asc"}));
    }

    @Test
    public void getPageable_whenSortDirectionIsNull_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> PaginationSortingUtils.getPageable(0, 10, new String[]{"name", null}));
    }

    @Test
    public void getPageable_whenSortFieldAndDirectionAreNull_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> PaginationSortingUtils.getPageable(0, 10, new String[]{null, null}));
    }


    @Test
    public void getPageable_whenSortFieldAndDirectionAreValid_returnPageable() {
        Pageable pageable = PaginationSortingUtils.getPageable(10, 10, new String[]{"name", "asc"});

        assertEquals(pageable.getPageNumber(), 1);
        assertEquals(pageable.getPageSize(), 10);
        assertEquals(pageable.getSort().getOrderFor("name").getDirection(), Sort.Direction.ASC);
        assertEquals(pageable.getSort().getOrderFor("name").getProperty(), "name");
        assertEquals(pageable.getClass(), OffsetBasedPageRequest.class);
    }
}
