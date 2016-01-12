import java.io.File;
import java.util.Scanner;

/**
 * Created by s-gheldd on 12/29/15.
 */
public class CacheSim {
    private static final String HELP_MESSAGE = "-s <s>  Anzahl der Indexbits s, S = 2^s  ist die Anzahl der CacheSim.Cache Sätze \n" +
            "-E <E> Assoziativität des Caches, E = Anzahl der Blöcke pro Satz \n" +
            "-b <b> Anzahl der CacheSim.Block Bits, B = 2^b  ist die Blockgröße \n" +
            "-t <tracefile> der Name der valgrind Trace Datei, die der Simulator simulieren soll \n" +
            "-v aktiviert den „verbose“ Mode, bei dem der Cachesimulator für jede eingelesene Trace \n" +
            "Zeile das aktuelle CacheSim.Cache Verhalten ausgibt (siehe unten). \n" +
            "-h  gibt  die  verfügbaren  Optionen  des  Simulators  aus";
    private int hits;
    private int misses;
    private int evictions;
    private final boolean verbose;
    private final File traceFile;
    private final Cache cache;

    public static void main(String[] args) {
        CacheSim cacheSim = parseFlags(args);
        cacheSim.work();

    }


    public CacheSim(final int blockBits, final int indexBits, final int associativity, final File file, final boolean verbose) {
        hits = 0;
        misses = 0;
        evictions = 0;
        this.traceFile = file;
        this.verbose = verbose;
        this.cache = new Cache(blockBits, indexBits, associativity, this);
    }

    public void work() {
        int lineNumber = 0;
        try (Scanner scanner = new Scanner(traceFile)) {
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine();
                if (line.startsWith(" S")) {
                    String[] parsedLine = line.trim().split("\\s")[1].split(",");
                    long addres = Long.valueOf(parsedLine[0], 16);
                    int bytes = Integer.valueOf(parsedLine[1]);
                    if (verbose) {
                        System.out.println();
                        System.out.print(lineNumber + ": " + line + " ");
                    }
                    cache.use(addres, bytes);
                } else if (line.startsWith(" L")) {
                    String[] parsedLine = line.trim().split("\\s")[1].split(",");
                    long addres = Long.valueOf(parsedLine[0], 16);
                    int bytes = Integer.valueOf(parsedLine[1]);
                    if (verbose) {
                        System.out.println();
                        System.out.print(lineNumber + ": " + line + " ");
                    }
                    cache.use(addres, bytes);

                } else if (line.startsWith(" M")) {
                    String[] parsedLine = line.trim().split("\\s")[1].split(",");
                    long addres = Long.valueOf(parsedLine[0], 16);
                    int bytes = Integer.valueOf(parsedLine[1]);
                    if (verbose) {
                        System.out.println();
                        System.out.print(lineNumber + ": " + line + " ");
                    }
                    cache.use(addres, bytes);
                    cache.use(addres, bytes);
                }

            }

            System.out.println("\nhits: " + hits + ", misses: " + misses + ", evictions: " + evictions);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void log(final int code) {
        switch (code) {
            case Set.HIT:
                hits++;
                if (verbose) {
                    System.out.print("hit ");
                }
                break;
            case Set.MISS:
                misses++;
                if (verbose) {
                    System.out.print("miss ");
                }
                break;
            case Set.EVICTION:
                evictions++;
                misses++;
                if (verbose) {
                    System.out.print("miss eviction ");
                }
                break;
        }

    }

    private static CacheSim parseFlags(String[] args) {
        int blockBits = -1;
        int indexBits = -1;
        int associativity = -1;
        String traceFile = "";
        boolean verbose = false;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-h":
                    printHelp();
                    break;

                case "-s":
                    if (args[++i].matches("\\d+")) {
                        indexBits = Integer.parseInt(args[i]);
                    } else {
                        printHelp();
                    }
                    break;

                case "-E":
                    if (args[++i].matches("\\d+")) {
                        associativity = Integer.parseInt(args[i]);
                    } else {
                        printHelp();
                    }
                    break;

                case "-b":
                    if (args[++i].matches("\\d+")) {
                        blockBits = Integer.parseInt(args[i]);
                    } else {
                        printHelp();
                    }
                    break;

                case "-v":
                    verbose = true;
                    break;

                case "-t":
                    if (args[++i].matches(".+\\.txt")) {
                        traceFile = args[i];
                    } else {
                        printHelp();
                    }
                    break;
            }
        }

        if (blockBits == -1 || indexBits == -1 || associativity == -1 || traceFile.isEmpty()) {
            printHelp();
        }
        return new CacheSim(blockBits, indexBits, associativity, new File(traceFile), verbose);
    }

    private static void printHelp() {
        System.out.println(HELP_MESSAGE);
        System.exit(0);
    }
}

/**
 * Created by s-gheldd on 12/26/15.
 */
class Set {
    public static final int MISS = 1;
    public static final int HIT = 2;
    public static final int EVICTION = 3;

    private final int associativity;
    final int blockSize;
    private final Block[] blocks;
    private int useCounter;
    private final int blockBits;
    private final int indexBits;

    Set(final int associativity, final int blockBits, final int indexBits) {
        this.blockBits = blockBits;
        this.indexBits = indexBits;
        this.blockSize = 1 << blockBits;
        this.associativity = associativity;
        this.blocks = new Block[associativity];
        for (int i = 0; i < associativity; i++) {
            blocks[i] = new Block(indexBits);
        }
        this.useCounter = 0;
    }

    public int use(final long address) {
        int lastUsed = 0;
        int nonValid = -1;
        final long tag = address >>> (indexBits);
        for (int i = 0; i < associativity; i++) {
            if (blocks[i].isValid()) {
                if (blocks[i].getTag() == tag) {
                    blocks[i].update(++useCounter);
                    return Set.HIT; //hit
                }
                if (blocks[i].getUseCounter() < blocks[lastUsed].getUseCounter()) {
                    lastUsed = i;
                }
            } else {
                nonValid = i;
            }
        }
        if (nonValid != -1) {
            blocks[nonValid].firstUse(address, ++useCounter);
            return Set.MISS;
        } else {
            blocks[lastUsed].firstUse(address, ++useCounter);
            return Set.EVICTION;
        }

    }

}

/**
 * Created by s-gheldd on 12/29/15.
 */
class Cache {
    private final int setCount;
    private final int blockSize;
    private final Set[] sets;
    private final CacheSim cacheSim;
    private final int indexBits;


    public Cache(final int blockBits, final int indexBits, final int associativity, final CacheSim cacheSim) {
        this.blockSize = 1 << blockBits;
        this.setCount = 1 << indexBits;
        this.cacheSim = cacheSim;
        this.indexBits = indexBits;

        sets = new Set[setCount];
        for (int i = 0; i < sets.length; i++) {
            sets[i] = new Set(associativity, blockBits, indexBits);
        }
    }


    public void use(long address, int bytes) {

        final int setNumber = (int) ((address >>> indexBits) % sets.length);
        final int storageSpace = blockSize - (int) address % blockSize;
        cacheSim.log(sets[setNumber].use(address));
        if (bytes > storageSpace) {
            bytes -= storageSpace;
            address += storageSpace;
            cacheSim.log(sets[(int) ((address / blockSize) % sets.length)].use(address));
            while (bytes > blockSize) {
                bytes -= blockSize;
                address += blockSize;
                cacheSim.log(sets[(int) ((address / blockSize) % sets.length)].use(address));
            }
        }
    }
}

/**
 * Created by s-gheldd on 12/26/15.
 */
class Block {

    private long tag;
    private boolean valid;
    private final int indexBits;
    private int useCounter;

    Block(final int indexBits) {
        this.indexBits = indexBits;
        this.valid = false;
    }

    public void firstUse(final long address, final int useCounter) {
        this.tag = address >>> (indexBits);
        this.useCounter = useCounter;
        this.valid = true;

    }

    public void update(final int useCounter) {
        this.useCounter = useCounter;
    }

    public int getUseCounter() {
        return useCounter;
    }

    public long getTag() {
        return tag;
    }

    public boolean isValid() {
        return valid;
    }
}

