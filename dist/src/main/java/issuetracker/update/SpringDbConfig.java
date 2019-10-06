package issuetracker.update;

public class SpringDbConfig {
    private SpringDataSource dataSource;

    public SpringDataSource getDatasource() {
        return dataSource;
    }

    public void setDatasource(SpringDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String toString() {
        return "SpringDbConfig{" +
                "dataSource=" + dataSource +
                '}';
    }
}
