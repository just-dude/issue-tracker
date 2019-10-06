package issuetracker.update;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.FileSystemResourceAccessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Выполняет создание и обновление БД при первом запуске
 * При всех последующих запусках лога не делает ничего(признаком однажды запущенного обновления является наличие файла с логом)
 */
public class SpringAppDbUpdater {

    private static boolean ALREADY_WORKED_ONCE = false;

    static {
        String appDir = System.getProperty("app.dir");
        File logFile = Paths.get(appDir).resolve("logs/app.log").toFile();
        // Наличие файла с логами означает, что процесс создания и обновления БД уже запускался. Больше его запускать не нужно
        if (logFile.exists()) {
            ALREADY_WORKED_ONCE = true;
        }
    }

    public static void main(String[] args) throws Exception {
        if (ALREADY_WORKED_ONCE) {
            return;
        }
        Logger log = LogManager.getLogger(SpringAppDbUpdater.class);
        log.info("Start");
        try {
            Map<String, String> argsMap = Stream.of(args).collect(Collectors.toMap(a -> a.substring(0, a.indexOf('=')), a -> a.substring(a.indexOf('=') + 1)));
            String appDir = System.getProperty("app.dir");
            SpringDataSource dataSourceConfig = getServerDataSourceConfig(argsMap.get("--server.config"));
            DbUpdater updater = new DbUpdater(log, dataSourceConfig.getDriverClassName(),
                    new DbUpdater.DbConfig(dataSourceConfig.getUrl(), dataSourceConfig.getUsername(), dataSourceConfig.getPassword()),
                    argsMap.get("--db.root.username"), argsMap.get("--db.root.password"), Paths.get(appDir).resolve("rdbms").resolve("changelog-main.xml"));
            updater.update();
        } catch (Exception e) {
            log.error("Ошибка при обновлении БД", e);
        }
        log.info("End");
    }

    private static SpringDataSource getServerDataSourceConfig(String serverConfigPath) throws Exception {
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        Yaml yaml = new Yaml(new Constructor(SpringConfig.class), representer);
        try (InputStream is = new FileInputStream(serverConfigPath)) {
            SpringConfig dbConfig = yaml.loadAs(is, SpringConfig.class);
            return dbConfig.getSpring().getDatasource();
        }
    }
}