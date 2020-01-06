package issuetracker;

import org.junit.Assert;

import java.nio.file.Paths;

public class Test {

    @org.junit.Test
    public void test() {
        String path = "C:\\projects\\issue-tracker\\dist\\target\\rc";
        System.out.println(Paths.get(path).toString());
        System.out.println(Paths.get(path).toUri());
    }
}
