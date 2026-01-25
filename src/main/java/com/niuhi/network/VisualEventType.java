package com.niuhi.network;

public enum VisualEventType {
    BOOST(0),
    PULL(1),
    DRAG(2),
    ROCKET_FLAIR(3);

    private final int id;

    VisualEventType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static VisualEventType fromId(int id) {
        for (VisualEventType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return BOOST;
    }
}
