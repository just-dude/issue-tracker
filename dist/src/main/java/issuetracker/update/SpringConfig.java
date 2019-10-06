package issuetracker.update;

public class SpringConfig {
    private SpringDbConfig spring;

    public SpringDbConfig getSpring() {
        return spring;
    }

    public void setSpring(SpringDbConfig spring) {
        this.spring = spring;
    }

    @Override
    public String toString() {
        return "SpringConfig{" +
                "spring=" + spring +
                '}';
    }
}
