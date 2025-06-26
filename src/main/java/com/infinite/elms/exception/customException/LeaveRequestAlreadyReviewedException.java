package com.infinite.elms.exception.customException;

public class LeaveRequestAlreadyReviewedException extends RuntimeException {
    public LeaveRequestAlreadyReviewedException(String message) {
        super(message);
    }
}
