import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        task1();
    }
    static void task1(){
        try{
            Path path = Paths.get("C:\\Code\\RKSP2\\src\\textfile.txt");
            List<String> text = Files.readAllLines(path);
            for(String s: text)
                System.out.println(s);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    static void task2_1(){
        long startTime = System.currentTimeMillis();

        try (FileInputStream fis = new FileInputStream("sourcefile.txt");
             FileOutputStream fos = new FileOutputStream("destinationfile.txt")) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Time taken: " + executionTime + " milliseconds");
    }
    static void task2_2(){
        long startTime = System.currentTimeMillis();

        try (FileInputStream fis = new FileInputStream("sourcefile.txt");
             FileOutputStream fos = new FileOutputStream("destinationfile.txt");
             FileChannel sourceChannel = fis.getChannel();
             FileChannel destChannel = fos.getChannel()) {
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } catch (IOException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Time taken: " + executionTime + " milliseconds");
    }
    static void task2_3(){
        long startTime = System.currentTimeMillis();

        File sourceFile = new File("sourcefile.txt");
        File destinationFile = new File("destinationfile.txt");

        try {
            FileUtils.copyFile(sourceFile, destinationFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Time taken: " + executionTime + " milliseconds");
    }
    static void task2_4(){
        long startTime = System.currentTimeMillis();

        Path sourcePath = Path.of("sourcefile.txt");
        Path destinationPath = Path.of("destinationfile.txt");

        try {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Time taken: " + executionTime + " milliseconds");
    }
    static void task3(){
        String filePath = "your_file_path_here"; // Укажите путь к файлу

        try (FileInputStream fis = new FileInputStream(filePath);
             FileChannel channel = fis.getChannel()) {

            ByteBuffer buffer = ByteBuffer.allocate(1024); // Создаем буфер для чтения данных из файла
            int bytesRead;

            int checksum = 0; // Инициализируем контрольную сумму

            while ((bytesRead = channel.read(buffer)) != -1) {
                buffer.flip(); // Переключаем буфер в режим чтения

                while (buffer.hasRemaining()) {
                    checksum += buffer.getShort(); // Суммируем 16-битные значения из буфера
                }

                buffer.clear(); // Очищаем буфер для следующей порции данных
            }

            // Завершаем расчет контрольной суммы
            checksum = (checksum & 0xFFFF) + (checksum >> 16); // Добавляем переносы
            checksum = ~checksum & 0xFFFF; // Инвертируем биты и оставляем только 16 бит

            System.out.println("16-битная контрольная сумма файла: " + Integer.toHexString(checksum));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static void task4(){
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path directory = Paths.get("your_directory_path_here");

            directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

            while (true) {
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        Path newPath = ((WatchEvent<Path>) event).context();
                        System.out.println("Создан новый файл: " + newPath);
                    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        Path modifiedPath = ((WatchEvent<Path>) event).context();
                        System.out.println("Изменен файл: " + modifiedPath);

                        // Здесь вы можете добавить логику для вычисления изменений в файле и вывода их
                        // Например, сравнение текущей контрольной суммы с предыдущей
                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        Path deletedPath = ((WatchEvent<Path>) event).context();
                        System.out.println("Удален файл: " + deletedPath);

                        // Здесь можно добавить логику для вывода размера файла и контрольной суммы,
                        // но это требует предварительного вычисления контрольной суммы при создании файла,
                        // и сохранения информации о размере файла
                    }
                }

                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}