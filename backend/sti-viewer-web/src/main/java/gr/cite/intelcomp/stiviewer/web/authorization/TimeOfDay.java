package gr.cite.intelcomp.stiviewer.web.authorization;

import org.springframework.boot.context.properties.ConstructorBinding;

public class TimeOfDay {

    private final String startingAt, endingAt;

    @ConstructorBinding
    public TimeOfDay(String startingAt, String endingAt) {
        this.startingAt = startingAt;
        this.endingAt = endingAt;
    }

    public String getStartingAt() {
        return startingAt;
    }

    public String getEndingAt() {
        return endingAt;
    }
}
