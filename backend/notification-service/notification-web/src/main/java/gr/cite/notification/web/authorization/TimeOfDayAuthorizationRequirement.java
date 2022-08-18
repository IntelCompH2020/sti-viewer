package gr.cite.notification.web.authorization;

import gr.cite.commons.web.authz.policy.AuthorizationRequirement;

public class TimeOfDayAuthorizationRequirement implements AuthorizationRequirement {

    private final TimeOfDay timeOfDay;
    private final boolean ignoreIfNotAuthenticated;

    public TimeOfDayAuthorizationRequirement(TimeOfDay timeOfDay, boolean ignoreIfNotAuthenticated) {
        this.timeOfDay = timeOfDay;
        this.ignoreIfNotAuthenticated = ignoreIfNotAuthenticated;
    }

    public TimeOfDay getTimeOfDay() {
        return timeOfDay;
    }

    public boolean isIgnoreIfNotAuthenticated() {
        return ignoreIfNotAuthenticated;
    }
}
