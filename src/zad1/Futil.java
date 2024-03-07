package zad1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Futil{

    private static FileChannel inputChannel;

    private static FileChannel outputChannel;
    public static void processDir(String dirName, String resultFileName) {
        try {
            Files.walkFileTree(Paths.get(dirName), new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    inputChannel = FileChannel.open(file, StandardOpenOption.READ);
                    outputChannel = FileChannel.open(Paths.get(resultFileName), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                    ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
                    inputChannel.read(buffer);
                    buffer.flip();
                    CharBuffer decoded = Charset.forName("Cp1250").decode(buffer);
                    ByteBuffer encoded = StandardCharsets.UTF_8.encode(decoded);
                    outputChannel.write(encoded);
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    System.out.println("Failed to access file: " + file.toString());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    boolean finished = Files.isSameFile(dir, Paths.get(dirName));
                    if(finished){
                        return FileVisitResult.TERMINATE;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
