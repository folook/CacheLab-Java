import org.apache.commons.cli.*;

import java.util.HashMap;

public class ArgsParser {
    /**
     *
     * @param args 命令行参数 如 -s 4 -b 4 -E 1 -t /Users/fu_mbp/IdeaProjects/Cache-lab-mac/trace.txt
     * @return HashMap，key 是参数名，value 是参数值
     */
    public static HashMap<String, String> parseCmdArgs(String[] args) {

        HashMap<String, String> argsHashMap = new HashMap<>();

        //1. 定义规则
        Options options = new Options();
        Option setBits = new Option("s", null, true, "sets index 的位数");
        Option blockBits = new Option("b", null, true, "cache block 的位数");
        Option eLine = new Option("E", null, true, "每个组有多少行");
        Option path = new Option("t", null, true, "跟踪文件的路径");

        options.addOption(setBits);
        options.addOption(blockBits);
        options.addOption(eLine);
        options.addOption(path);


        //2. 解析参数
        CommandLine cmd = null;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            System.out.println("[error] " + e.getMessage());
            System.exit(0);
        }

        //3. 执行逻辑
        if (cmd.hasOption("s")) {
            System.out.println("s---" + cmd.getOptionValue("s"));
            argsHashMap.put("s", cmd.getOptionValue("s"));
        }
        if (cmd.hasOption("b")) {
            System.out.println("b---" + cmd.getOptionValue("b"));
            argsHashMap.put("b", cmd.getOptionValue("b"));

        }
        if (cmd.hasOption("t")) {
            System.out.println("t---" + cmd.getOptionValue("t"));
            argsHashMap.put("t", cmd.getOptionValue("t"));

        }
        if (cmd.hasOption("E")) {
            System.out.println("E---" + cmd.getOptionValue("E"));
            argsHashMap.put("E", cmd.getOptionValue("E"));

        }

        return argsHashMap;
    }

    /**
     *
     * @param strLine tarce 文件的一行，如 L 10,1
     * @return String[] ,如 ["L", "10", "1"]
     */
    public static String[] parseTrace(String strLine) {
        String[] trace1Arr = strLine.split(" ");
        String[] trace2Arr = trace1Arr[2].split(",");
        return new String[]{trace1Arr[1], trace2Arr[0], trace2Arr[1]};
    }



}
