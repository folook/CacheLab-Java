import java.io.IOException;
import java.util.HashMap;

public class CacheLab {
    public static void main(String[] args) {

        System.out.println("run cache simulator");
        CacheSimulator cacheSimulator = new CacheSimulator();

        //命令行参数解析
        ArgsParser argsParser = new ArgsParser();
        HashMap<String, String> argsHashMap = argsParser.parseCmdArgs(args);

        //运行 Cache 模拟器
        try {
            cacheSimulator.runSimulator(argsHashMap);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
