import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Cache {
    private int setBits;
    private int blockBits;
    private int eLine;
    private int sets;
    private CacheLine[][] cacheEntity;

}
