package example.maptest;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class Util {
    public static void runInMainThread(Runnable cmd) throws Throwable {
        runInMainThread(() -> {
            cmd.run();
            return null;
        });
    }

    public static <T> T runInMainThread(Callable<T> cmd) throws Throwable {
        CompletableFuture<T> ret = new CompletableFuture<>();

        if (Looper.myLooper() == null) {
            Looper.prepare();
        }

        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                ret.complete(cmd.call());
            } catch (Throwable t) {
                ret.completeExceptionally(t);
            }
        });
        try {
            return ret.get();
        } catch (CompletionException e) {
            throw e.getCause();
        }
    }
}
