package eu.epitech.jcoinche.jcoincheclient.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by noboud_n on 29/11/2016.
 */
public class NonblockingBufferedReader {
    private final BlockingQueue<String> lines = new LinkedBlockingQueue<String>();
    private volatile boolean closed = false;
    private Thread backgroundReaderThread = null;

    public NonblockingBufferedReader(final BufferedReader bufferedReader) {
        backgroundReaderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!Thread.interrupted()) {
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            break;
                        }
                        lines.add(line);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    closed = true;
                }
            }
        });
        backgroundReaderThread.setDaemon(true);
        backgroundReaderThread.start();
    }

    public String readLine() throws IOException {
        try {
            return closed && lines.isEmpty() ? null : lines.poll(10000L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new IOException("The BackgroundReaderThread was interrupted!", e);
        }
    }

    public void close() {
        if (backgroundReaderThread != null) {
            backgroundReaderThread.interrupt();
            backgroundReaderThread = null;
        }
    }
}
