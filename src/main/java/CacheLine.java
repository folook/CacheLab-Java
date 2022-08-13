import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CacheLine {
    private int valid;
    private int tag;
    private int blockBytes;
    private int timeStamp;
}
