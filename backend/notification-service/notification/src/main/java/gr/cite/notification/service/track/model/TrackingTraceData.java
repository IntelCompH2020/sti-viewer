package gr.cite.notification.service.track.model;

import java.time.Instant;

public class TrackingTraceData {
    private String state;
    private Instant readTime;
    private Boolean isActive;

    public TrackingTraceData() {
    }

    public TrackingTraceData(String state, Instant readTime, Boolean isActive) {
        this.state = state;
        this.readTime = readTime;
        this.isActive = isActive;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Instant getReadTime() {
        return readTime;
    }

    public void setReadTime(Instant readTime) {
        this.readTime = readTime;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrackingTraceData that = (TrackingTraceData) o;

        if (!state.equals(that.state)) return false;
        if (!readTime.equals(that.readTime)) return false;
        return isActive.equals(that.isActive);
    }

    @Override
    public int hashCode() {
        int result = state.hashCode();
        result = 31 * result + readTime.hashCode();
        result = 31 * result + isActive.hashCode();
        return result;
    }
}
