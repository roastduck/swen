package com.swen;

import android.content.Context;
import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.DoneFilter;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.io.*;
import java.util.*;

/** Store marked news into persistent files and cache others in memory
 *  PLEASE DO NOT INSTANTIATE THIS CLASS, USE ((ApplicationWithStorage)getApplication()).getStorage() INSTEAD
 *  NOTE: Suggest passing the reference to this object around all Activities
 *        to avoid the cache being destructed
 */
public class Storage
{
    // How many items can be cached in memory
    private static final int DEFAULT_CACHE_CAPACITY = 64;
    private final int cacheCapacity;

    private LinkedHashMap<String, News> cache = new LinkedHashMap<String, News>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) { return size() > cacheCapacity; }
    };
    private LinkedHashSet<String> persistent = new LinkedHashSet<>();
    private File filesDir, cacheDir;

    private NewsAPI mAPI;

    public Storage(Context context)
    {
        this(context, new NewsAPI(), DEFAULT_CACHE_CAPACITY);
    }

    Storage(Context context, NewsAPI api, int cacheCapacity)
    {
        filesDir = context.getFilesDir();
        cacheDir = context.getCacheDir();
        mAPI = api;
        this.cacheCapacity = cacheCapacity;

        File[] subFiles = filesDir.listFiles();
        if (subFiles != null)
            for (File file : subFiles)
            {
                String s = file.getName();
                if (s.startsWith(FILE_PREFIX))
                    persistent.add(s.substring(FILE_PREFIX.length()));
            }
    }

    /** Get a news
     *  If it's cached in memory, return it
     *  Otherwise, If it's marked as persistent, get it from file
     *  Otherwise, get it from NewsAPI
     *  You can refer to the unit test as an example of how to use Promise
     *  If you want to run callback in UI thread, use AndroidDoneCallback instead of DoneCallback, and specify the thread
     *  When fail, it throws IOException (ues .fail to catch)
     */
    public Promise<News,IOException,Object> getNewsCached(String id)
    {
        final Deferred<Object,IOException,Object> deferred = new DeferredObject<>();
        deferred.resolve(new Object()); // must
        return deferred.promise().then(new DoneFilter<Object, News>() {
            @Override
            public News filterDone(Object result)
            {
                try
                {
                    return Storage.this.getNewsCachedSync(id);
                } catch (IOException e)
                {
                    deferred.reject(e);
                }
                return null;
            }
        });
    }

    public synchronized News getNewsCachedSync(String id) throws IOException
    {
        News news = cache.get(id);
        if (news != null)
            return news;
        news = getFileFromExternal(id);
        cache.put(id, news);
        return news;
    }

    /** Mark a file to be persistent
     *  It will not save the news again when already saved
     */
    public Promise<Object,IOException,Object> mark(String id)
    {
        final Deferred<Object,IOException,Object> deferred = new DeferredObject<>();
        deferred.resolve(new Object()); // must
        return deferred.promise().then(new DoneCallback<Object>() {
            @Override
            public void onDone(Object o)
            {
                try
                {
                    Storage.this.markSync(id);
                } catch (IOException e)
                {
                    deferred.reject(e);
                }
            }
        });
    }

    public Promise<Object,IOException,Object> unmark(String id)
    {
        final Deferred<Object,IOException,Object> deferred = new DeferredObject<>();
        deferred.resolve(new Object()); // must
        return deferred.promise().then(new DoneCallback<Object>() {
            @Override
            public void onDone(Object o)
            {
                Storage.this.unmarkSync(id);
            }
        });
    }

    public synchronized void markSync(String id) throws IOException
    {
        saveToFile(id, getNewsCachedSync(id));
        persistent.add(id);
    }

    public synchronized void unmarkSync(String id)
    {
        persistent.remove(id);
        try
        {
            deleteFromFile(id);
        } catch (IOException ignored)
        {
            // Unable to delete file is OK because the LinkedHashSet has been updated
        }
    }

    public synchronized List<String> getMarked()
    {
        return new Vector<>(persistent);
    }

    public static final String FILE_PREFIX = "news_";
    public static final String TMP_SUFFIX = ".tmp";

    /** Safely save a news into a file even the process will be killed
     *  Because AtomicFile was not supported until SDK 22, we write into tmp and then rename it
     *  It MAY throw IOException when fail. But when it's killed, it will not throw
     */
    private synchronized void saveToFile(String id, News news) throws IOException
    {
        File tmpFile = new File(cacheDir, FILE_PREFIX + id + TMP_SUFFIX);
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(tmpFile));
        os.writeObject(news);
        os.close();
        File persistentFile = new File(filesDir, FILE_PREFIX + id);
        // In Linux, rename is atomic as long as the OS doesn't crash
        boolean success = tmpFile.renameTo(persistentFile);
        if (!success)
            throw new IOException("File.renameTo failed");
    }

    /** Safely delete from a file even the process will be killed
     *  It MAY throw IOException when fail. But when it's killed, it will not throw
     */
    private synchronized void deleteFromFile(String id) throws IOException
    {
        File persistentFile = new File(filesDir, FILE_PREFIX + id);
        File tmpFile = new File(cacheDir, FILE_PREFIX + id + TMP_SUFFIX);
        boolean success = persistentFile.renameTo(tmpFile);
        if (!success)
            throw new IOException("File.renameTo failed");
        success = tmpFile.delete();
        if (!success)
            throw new IOException("File.delete failed");
    }

    /** Get News if the file is not in memory cache
     */
    private synchronized News getFileFromExternal(String id) throws IOException
    {
        if (persistent.contains(id))
        {
            try
            {
                File file = new File(filesDir, FILE_PREFIX + id);
                ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
                return (News)(is.readObject());
            } catch (IOException | ClassNotFoundException ignored)
            {
                // fallback to NewsAPI
            }
        }
        return mAPI.getNews(id);
    }
}
