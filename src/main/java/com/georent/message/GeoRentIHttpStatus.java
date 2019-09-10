package com.georent.message;

import org.springframework.lang.Nullable;

public enum GeoRentIHttpStatus  {

    /**
     * {@code 452 Request Header Fields HttpStatus.CONFLICT == 409}.
     */
    REGISTRATION_USER_ERROR(452, "Email address already in use!"),


    /**
     * {@code 453 Request Header Fields HttpStatus.PRECONDITION_FAILED}.
     */
    INVALID_FILE_EXTENSION_JPG(453, "Only JPG images are accepted."),


    /**
     * {@code 454 Request Header Fields HttpStatus.PRECONDITION_FAILED}.
     */
    INVALID_FILE_SIZE(454, "Size is too big.");

    private final int value;

    private final String reasonPhrase;


    GeoRentIHttpStatus(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    /**
     * Return the integer value of this status code.
     */
    public int value() {
        return this.value;
    }

    /**
     * Return the reason phrase of this status code.
     */
    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    /**
     * Resolve the given status code to an {@code GeoRentIHttpStatus}, if possible.
     * @param statusCodeString the HTTP status code (potentially non-standard)
     * @return the corresponding {@code GeoRentIHttpStatus.value}, or {@code 0} if not found
     */
    @Nullable
    public static int getValue (String statusCodeString) {
        for (GeoRentIHttpStatus status : values()) {
            if (status.reasonPhrase.equals(statusCodeString)) {
                return status.value;
            }
        }
        return 0;
    }
}
