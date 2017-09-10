package com.swen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import com.swen.promise.Callback;
import com.swen.promise.Promise;

import java.io.*;
import java.net.URL;
import java.util.*;

/** Store marked news into persistent files and cache others in memory
 *  PLEASE DO NOT INSTANTIATE THIS CLASS, USE ((ApplicationWithStorage)getApplication()).getStorage() INSTEAD
 *  NOTE: Suggest passing the reference to this object around all Activities
 *        to avoid the cache being destructed
 */
public class Storage
{
    // How many items can be cached in memory
    private static final int DEFAULT_NEWS_CACHE_CAPACITY = 64;
    private static final int DEFAULT_PIC_CACHE_CAPACITY = 32;
    private final int newsCacheCapacity, picCacheCapacity;

    private LinkedHashMap<String, News> newsCache;
    private LinkedHashMap<String, Bitmap> picCache;
    private LinkedHashSet<String> newsPersistent;
    private LinkedCounter<String> picPersistent;

    private File filesDir, cacheDir;

    private NewsAPI mAPI;
    private StreamFactory mStreamFactory;

    public static final String NEWS_PREFIX = "news_";
    public static final String IMG_PREFIX = "img_";
    public static final String TMP_SUFFIX = ".tmp";

    public Storage(Context context)
    {
        this(context, new NewsAPI(), new StreamFactory(), DEFAULT_NEWS_CACHE_CAPACITY, DEFAULT_PIC_CACHE_CAPACITY);
    }

    Storage(Context context, NewsAPI api, StreamFactory streamFactory, int newsCacheCapacity, int picCacheCapacity)
    {
        filesDir = context.getFilesDir();
        cacheDir = context.getCacheDir();
        mAPI = api;
        mStreamFactory = streamFactory;
        this.newsCacheCapacity = newsCacheCapacity;
        this.picCacheCapacity = picCacheCapacity;

        newsCache = new LinkedHashMap<String, News>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) { return size() > newsCacheCapacity; }
        };
        picCache = new LinkedHashMap<String, Bitmap>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) { return size() > picCacheCapacity; }
        };
        newsPersistent = new LinkedHashSet<>();
        picPersistent = new LinkedCounter<>();

        File[] subFiles = filesDir.listFiles();
        if (subFiles != null)
            for (File file : subFiles)
            {
                String s = file.getName();
                if (s.startsWith(NEWS_PREFIX))
                    newsPersistent.add(s.substring(NEWS_PREFIX.length()));
                if (s.startsWith(IMG_PREFIX))
                    picPersistent.add(new String(Base64.decode(s.substring(IMG_PREFIX.length()), Base64.URL_SAFE)));
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
    public Promise<Object,News> getNewsCached(String id)
    {
        return new Promise<>(new Callback<Object, News>()
        {
            @Override
            public News run(Object o) throws Exception
            {
                return getNewsCachedSync(id);
            }
        }, new Object());
    }

    public synchronized News getNewsCachedSync(String id) throws IOException
    {
        News news = newsCache.get(id);
        if (news != null)
            return news;
        news = getNewsFromExternal(id);
        if (newsCacheCapacity > 0)
            newsCache.put(id, news);
        return news;
    }

    public Promise<Object,Bitmap> getPicCached(String url)
    {
        return new Promise<>(new Callback<Object, Bitmap>()
        {
            @Override
            public Bitmap run(Object o) throws Exception
            {
                return getPicCachedSync(url);
            }
        }, null);
    }

    public synchronized Bitmap getPicCachedSync(String url) throws IOException
    {
        Bitmap pic = picCache.get(url);
        if (pic != null)
            return pic;
        pic = getPicFromExternal(url);
        if (picCacheCapacity > 0)
            picCache.put(url, pic);
        return pic;
    }

    /** Mark a file to be persistent
     *  It will not save the news again when already saved
     *  It also saves the pictures in this news to persistent
     */
    public Promise<Object,Object> mark(String id)
    {
        return new Promise<>(new Callback<Object, Object>()
        {
            @Override
            public Object run(Object o) throws Exception
            {
                markSync(id);
                return null;
            }
        }, null);
    }

    /**
     * Delete a file and its pictures from persistent
     */
    public Promise<Object,Object> unmark(String id)
    {
        return new Promise<>(o-> {
            Storage.this.unmarkSync(id);
            return null;
        }, null);
    }

    public synchronized void markSync(String id) throws IOException
    {
        News news = getNewsCachedSync(id);
        saveToFile(NEWS_PREFIX + id, news);
        for (String picId : news.getNewsPictures())
            saveToFile(IMG_PREFIX + Base64.encodeToString(picId.getBytes(), Base64.URL_SAFE), getPicFromExternal(picId));
        newsPersistent.add(id);
        for (String picId : news.getNewsPictures())
            picPersistent.add(picId);
    }

    public synchronized void unmarkSync(String id) throws IOException
    {
        News news = getNewsCachedSync(id);
        newsPersistent.remove(id);
        for (String picId : news.getNewsPictures())
            picPersistent.remove(picId);
        try
        {
            deleteFromFile(NEWS_PREFIX + id);
            for (String picId : news.getNewsPictures())
                deleteFromFile(IMG_PREFIX + Base64.encodeToString(picId.getBytes(), Base64.URL_SAFE));
        } catch (IOException ignored)
        {
            // Unable to delete file is OK because the LinkedHashSet has been updated
        }
    }

    public synchronized List<String> getMarked()
    {
        return new Vector<>(newsPersistent);
    }

    private synchronized <T> void writeToStream(T obj, OutputStream stream) throws IOException
    {
        if (obj instanceof News)
        {
            News news = (News)obj;
            ObjectOutputStream os = new ObjectOutputStream(stream);
            os.writeObject(news);
            os.close();
        }
        if (obj instanceof Bitmap)
        {
            Bitmap bitmap = (Bitmap)obj;
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        }
    }

    /** Safely save a news into a file even the process will be killed
     *  Because AtomicFile was not supported until SDK 22, we write into tmp and then rename it
     *  It MAY throw IOException when fail. But when it's killed, it will not throw
     */
    private synchronized <T> void saveToFile(String filename, T obj) throws IOException
    {
        File tmpFile = new File(cacheDir, filename + TMP_SUFFIX);
        writeToStream(obj, new FileOutputStream(tmpFile));
        File persistentFile = new File(filesDir, filename);
        // In Linux, rename is atomic as long as the OS doesn't crash
        boolean success = tmpFile.renameTo(persistentFile);
        if (!success)
            throw new IOException("File.renameTo failed");
    }

    /** Safely delete from a file even the process will be killed
     *  It MAY throw IOException when fail. But when it's killed, it will not throw
     */
    private synchronized void deleteFromFile(String filename) throws IOException
    {
        File persistentFile = new File(filesDir, filename);
        File tmpFile = new File(cacheDir, filename + TMP_SUFFIX);
        boolean success = persistentFile.renameTo(tmpFile);
        if (!success)
            throw new IOException("File.renameTo failed");
        success = tmpFile.delete();
        if (!success)
            throw new IOException("File.delete failed");
    }

    /** Get News if the file is not in memory cache
     */
    private synchronized News getNewsFromExternal(String id) throws IOException
    {
        if (newsPersistent.contains(id))
        {
            try
            {
                File file = new File(filesDir, NEWS_PREFIX + id);
                ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
                return (News)(is.readObject());
            } catch (IOException | ClassNotFoundException ignored)
            {
                // fallback to NewsAPI
            }
        }
        return mAPI.getNews(id);
    }

    /** Get Picture if the file is not in memory cache
     */
    private synchronized Bitmap getPicFromExternal(String id) throws IOException
    {
        if (picPersistent.contains(id))
        {
            File file = new File(filesDir, IMG_PREFIX + Base64.encodeToString(id.getBytes(), Base64.URL_SAFE));
            Bitmap ret = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (ret != null)
                return ret;
        }
        return BitmapFactory.decodeStream(mStreamFactory.fromUrl(id));
    }
}

class LinkedCounter<T>
{
    private LinkedHashMap<T, Integer> mMap;

    LinkedCounter() { mMap = new LinkedHashMap<>(); }

    public void add(T key)
    {
        Integer ori = mMap.get(key);
        mMap.put(key, ori == null ? 1 : ori + 1);
    }

    public void remove(T key)
    {
        Integer ori = mMap.get(key);
        if (ori == null)
            return;
        if (ori == 1)
            mMap.remove(key);
        else
            mMap.put(key, ori - 1);
    }

    public boolean contains(T key)
    {
        return mMap.containsKey(key);
    }
}
