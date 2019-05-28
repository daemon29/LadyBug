package com.example.donatetosave;

public class Achievement {
    private String name;
    private Long count, max;

    public Achievement() {
    }

    public String getName() {
        return name;
    }

    public Long getCount() {
        return count;
    }

    public Long getMax() {
        return max;
    }

    public Achievement(String name, long count, long max) {
        this.name = name;
        this.count = count;
        this.max = max;
    }
}
