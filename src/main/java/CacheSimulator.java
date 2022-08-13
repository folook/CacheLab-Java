import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

@Data
@AllArgsConstructor
public class CacheSimulator {

    private int hits;
    private int misses;
    private int evictions;

    public CacheSimulator() {

    }

    public void runSimulator(HashMap<String, String> argsHashMap) throws IOException {

        //初始化缓存
        Cache cache = initializeCache(argsHashMap);

        //使用 BufferedReader 按行读取文件
        FileInputStream inputStream = new FileInputStream(argsHashMap.get("t"));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String strLine = null;
        while((strLine = bufferedReader.readLine()) != null)
        {
            System.out.println(strLine);
            //区分指令
            if(strLine.startsWith("I")) {
                //do nothing, ignore
            }
            else if (strLine.startsWith(" ")) {
                //根据 L M S 指令访存
                accessCache(strLine, cache);
            } else {
                System.out.println("请检查 trace 文件，指令输入有误");
            }
        }

        //close
        inputStream.close();
        System.out.println(hits + " " + misses + " " + evictions);

    }


    /**
     *
     * @param argsHashMap 输入为命令行参数
     * @return 返回初始化的 Cache
     */
    public Cache initializeCache(HashMap<String, String> argsHashMap) {

        //初始化 CacheLine
        CacheLine cacheLine = new CacheLine(0, -1, 0, 0);

        //行，每一行对应于 Cache 的每一个set组
        int cacheRow = 1 << Integer.parseInt(argsHashMap.get("s"));

        //列，每一行上的一个列对应于 Cache 的 一个Cache line
        int cacheColumn = Integer.parseInt(argsHashMap.get("E"));

        //初始化 cacheEntity
        CacheLine[][] cacheEntity = new CacheLine[cacheRow][cacheColumn];
        for (int i = 0; i < cacheRow; i++) {
            for (int j = 0; j < cacheColumn; j++) {
                cacheEntity[i][j] = cacheLine;
            }
        }

        //初始化 Cache
        Cache cache = new Cache(Integer.parseInt(argsHashMap.get("s")), Integer.parseInt(argsHashMap.get("b")),
                Integer.parseInt(argsHashMap.get("E")), cacheRow, cacheEntity
                );

        return cache;
    }

    public void accessCache(String strLine, Cache cache) {
        //得到 trace 文件单行参数： L 10 1
        String[] traceArr = ArgsParser.parseTrace(strLine);
        //获得指令类型
        String insType = traceArr[0];
        //获得指令地址
        long address = Long.parseLong(traceArr[1], 16);

        switch (insType) {
            case "L", "S" -> updateCache(address, cache);
            case "M" -> {
                updateCache(address, cache);
                updateCache(address, cache);
            }
            default -> System.out.println("指令错误，请检查 trace 文件");
        }
    }

    /**
     * 遍历 set 组中所有 Cache line，确定是否hit
     * @param set
     * @param tag
     * @param cache
     * @return
     */
    public int getCacheLineIndex(long set, long tag, Cache cache) {
        for (int i = 0; i < cache.getELine(); i++) {
            if(cache.getCacheEntity()[(int) set][i].getValid() == 1 && cache.getCacheEntity()[(int) set][i].getTag() == tag) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 遍历 set 组中所有 Cache line，确定是否为 冷不命中
     * @param set
     * @param cache
     * @return
     */
    int isSetFull(long set, Cache cache)
    {
        for (int i = 0; i < cache.getELine(); i++)
        {
            if (cache.getCacheEntity()[(int) set][i].getValid() == 0)
                return i;
        }
        return -1;
    }

    public void updateCache(long address, Cache cache) {
        //根据地址进行抽取出set 组和 tag 标志位
        long set = address / cache.getSets() % cache.getSets();
        long tag = address >> (cache.getBlockBits() + cache.getSetBits());

        //index 为 hit 标志位
        int index = getCacheLineIndex(set, tag, cache);
        if (index == -1) {
            misses++;
            int i = isSetFull(set, cache);
            if(i == -1){
                evictions++;
                i = findLRU(set, cache);
            }
            writeCache(set, tag, i, cache);
        }
        else {
            hits++;
            writeCache(set, tag, index, cache);
        }


        //遍历 Cache 中被选择组的每一行：(此时已经完成组选择！)
        for (int i = 0; i < cache.getELine(); i++) {
            /*
            //判断 hit & miss ？
             if(cache.getCacheEntity()[set][i].getValid() == 1 && cache.getCacheEntity()[set][i].getTag() == tag) {
                 hits++;
                 System.out.println("hit");
             } else {
                 misses++;
                 System.out.println("miss");

                 //判断 clodMiss & eviction
                 if(cache.getCacheEntity()[set][i].getValid() == 0) {
                     //coldMiss 需要写入缓存
                     //todo fuck bug
                     System.out.println(Arrays.deepToString(cache.getCacheEntity()));
                     cache.getCacheEntity()[set][i].setValid(1);
                     System.out.println(Arrays.deepToString(cache.getCacheEntity()));

                     cache.getCacheEntity()[set][i].setTag(tag);
                 } else if (cache.getCacheEntity()[set][i].getValid() == 1 && cache.getCacheEntity()[set][i].getTag() != tag){
                     evictions++;
                     System.out.println("eviction");
                     //驱逐需要更新 tag 位
                     cache.getCacheEntity()[set][i].setTag(tag);
                 }
             }
            */



            /*
            //cache line 的有效位为1 且 tag 匹配
            if(cache.getCacheEntity()[(int) set][i].getValid() == 1 && cache.getCacheEntity()[(int) set][i].getTag() == tag) {//hits
                System.out.println("hit!");
                hits++;
            } else if(cache.getCacheEntity()[(int) set][i].getValid() == 1 && cache.getCacheEntity()[(int) set][i].getTag() != tag) {//驱逐
                misses++;
                System.out.println("miss!");
                evictions++;
                System.out.println("eviction!");
                int eviTargetIndex = findLRU((int) set, cache);
                writeCache((int) set, (int) tag, eviTargetIndex, cache);
            } else if(cache.getCacheEntity()[(int) set][i].getValid() == 0) { //冷不命中
                misses++;
                System.out.println("miss!");
                writeCache((int) set, (int) tag, i, cache);
            }
            */

        }

    }

    private void writeCache(long set, long tag, int i, Cache cache) {
        int valid = 1;
        int cacheBlock = 0; //忽略


        //遍历组中的每一行，将时间戳+1，代表这次没操作他
        for (int j = 0; j < cache.getELine(); j++) {
             if(cache.getCacheEntity()[(int) set][j].getValid() == 1) {
                 int newTimeStamp = cache.getCacheEntity()[(int) set][j].getTimeStamp() + 1;
                 cache.getCacheEntity()[(int) set][j].setTimeStamp(newTimeStamp);
             }
        }
        //同时将本次操作的行时间戳归零
        CacheLine cacheLine = new CacheLine(valid, (int) tag, cacheBlock, 0);
        CacheLine[][] cacheLineArr = cache.getCacheEntity();
        cacheLineArr[(int) set][i] = cacheLine;
    }

    private int findLRU(long set, Cache cache)
    {
        int maxIndex = 0;
        int maxStamp = 0;
        for(int i = 0; i < cache.getELine(); i++){
            if(cache.getCacheEntity()[(int) set][i].getTimeStamp() > maxStamp){
                maxStamp = cache.getCacheEntity()[(int) set][i].getTimeStamp();
                maxIndex = i;
            }
        }
        return maxIndex;
    }


}
