import java.util.Arrays;

public class test {

    public static void main(String[] args) {


        //初始化 4 行 3 列
        int row = 4;
        int column = 3;

        CacheLine[][] arr = new CacheLine[row][column];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                arr[i][j] =  new CacheLine(0, -1, 0, 0);
                ;
            }
        }
        System.out.println(Arrays.deepToString(arr));
        //  [[0, 0, 0], [0, 0, 0], [0, 0, 0], [0, 0, 0]]

        arr[1][0].setValid(1);
        System.out.println(Arrays.deepToString(arr));


    }
}
