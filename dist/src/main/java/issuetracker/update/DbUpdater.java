package issuetracker.update;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.FileSystemResourceAccessor;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DbUpdater {

    private Logger log;

    private String driverClass;

    private DbConfig targetDbConfig;

    private String targetDbName;

    private DbConfig rootDbConfig;

    private Path changelogPath;

    public DbUpdater(Logger log, String driverClass, DbConfig targetDbConfig, String rootUsername, String rootPassword, Path changelogPath) {
        this.log = log;
        this.driverClass = driverClass;
        this.targetDbConfig = targetDbConfig;
        this.targetDbName = getDbNameFromUrl(targetDbConfig.getUrl());
        this.rootDbConfig = new DbConfig(targetDbConfig.getUrl().replace(targetDbName, ""), rootUsername, rootPassword);
        this.changelogPath = changelogPath;
    }

    public void update() throws Exception {
        Class.forName(driverClass).newInstance();
        boolean isDbExists;
        try (Connection connection = DriverManager.getConnection(rootDbConfig.getUrl(), rootDbConfig.getUsername(), rootDbConfig.getPassword())) {
            isDbExists = isDbExists(connection, targetDbName);
            if (!isDbExists) {
                createDbAndUser(connection, targetDbName, targetDbConfig);
            }
        }
        try (Connection connection = DriverManager.getConnection(targetDbConfig.getUrl(), targetDbConfig.getUsername(), targetDbConfig.getPassword())) {
            if (!isDbExists) {
                log.info("Creating app schema in db");
                executeSql(connection, "CREATE SCHEMA " + targetDbConfig.getUsername());
            }
            liquibaseUpdateDb(connection, changelogPath.getParent().toString(), changelogPath.getFileName().toString());
        }
    }

    private void createDbAndUser(Connection connection, String dbName, DbConfig dbConfig) throws Exception {
        log.info("Creating app db and user");
        executeSql(connection, "CREATE DATABASE " + dbName);
        executeSql(connection, String.format("CREATE USER %s WITH ENCRYPTED PASSWORD '%s'", dbConfig.getUsername(), dbConfig.getPassword()));
        executeSql(connection, String.format("GRANT ALL PRIVILEGES ON DATABASE %s TO %s", dbName, dbConfig.getUsername()));
    }

    private boolean isDbExists(Connection connection, String dbName) throws Exception {
        try (PreparedStatement ps = connection.prepareStatement("SELECT 1 from pg_database WHERE datname=?")) {
            ps.setString(1, dbName);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    private void executeSql(Connection connection, String sql) throws Exception {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }

    private void liquibaseUpdateDb(Connection connection, String changelogDirPath, String chanelogName) throws Exception {
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        Liquibase liquibase = new liquibase.Liquibase(chanelogName, new FileSystemResourceAccessor(changelogDirPath), database);
        liquibase.update(new Contexts(), new LabelExpression());
    }

    private String getDbNameFromUrl(String url) {
        String dbName = url.substring(url.lastIndexOf('/') + 1);
        if (dbName.contains("?")) {
            dbName = dbName.substring(0, dbName.indexOf('?'));
        }
        return dbName;
    }

    static class DbConfig {

        private String url;

        private String username;

        private String password;

        public DbConfig(String url, String username, String password) {
            this.url = url;
            this.username = username;
            this.password = password;
        }

        public String getUrl() {
            return url;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
