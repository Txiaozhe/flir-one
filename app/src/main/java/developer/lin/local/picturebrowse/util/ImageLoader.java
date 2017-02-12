package developer.lin.local.picturebrowse.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * used by show local picture
 * Created by lin on 2015/10/27.
 */
public class ImageLoader {

    public static final String TAG = "ImageLoader";
    private static ImageLoader mInstance = null;
    public static final int DEFAULT_THREAD_COUNT = 1;

    private ImageLoader(int threadCount, Type type) {
        initConfig(threadCount, type);
    }

    public static ImageLoader getInstance(int threadCount, Type type) {

        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoader(threadCount, type);
                }
            }
        }

        return mInstance;
    }

    /**
     * constant  parameters
     */
    private LruCache<String, Bitmap> mLruCache;
    private ExecutorService mThreadPool;

    /**
     * load local picture strategy
     */
    public enum Type {
        FIFO, LIFO;
    }

    private Type mType;
    /**
     * task queue,save every local picture task
     */
    private LinkedList<Runnable> mTaskQueue;
    /**
     * background  poll thread ;
     * manager handler  ;
     * update UI   handler;
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHandler;
    private Handler mUIHandler;
    /**
     * multi thread ues this ,zero default wait ,other position should be run
     */
    private Semaphore mPoolThreadHandlerSemaphore = new Semaphore(0);
    private Semaphore mSemaphore;


    /**
     * init all constant
     */
    private void initConfig(int threadCount, Type type) {
        mPoolThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //manager operation
                //init looper parameters
                Looper.prepare();
                mPoolThreadHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        //get task from thread pool
                        mThreadPool.execute(getTask());
                        // execute will be wait until  semaphore acquire
                        try {
                            mSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //release ,confirm handler not null keep multi thread access null handler
                mPoolThreadHandlerSemaphore.release();
                //start loop
                Looper.loop();
            }
        });
        //start thread
        mPoolThread.start();
        //other config
        mLruCache = new LruCache<String, Bitmap>(getAppropriateMemorySize()) {
            /**
             * measure every bitmap memory size
             * @param key
             * @param value
             * @return
             */
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //return super.sizeOf(key, value);
                return value.getRowBytes() * value.getHeight();
            }
        };
        //create thread pool ,taskQueue,and Semaphore
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mTaskQueue = new LinkedList<>();
        this.mType = type;
        mSemaphore = new Semaphore(threadCount);
    }

    /**
     * get appropriate memory size  unit=bytes
     *
     * @return
     */
    private int getAppropriateMemorySize() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
        return cacheMemory;
    }

    private Runnable getTask() {
        if (mType == Type.FIFO) {
            return mTaskQueue.removeFirst();
        } else if (mType == Type.LIFO) {
            return mTaskQueue.removeLast();

        }
        return null;
    }

    /**
     * ui thread  call  method
     *
     * @param path
     * @param imageView
     */
    public void loadImage(final String path, final ImageView imageView) {
        imageView.setTag(path);
        //init ui handler
        if (mUIHandler == null) {
            mUIHandler = new Handler() {
                /**
                 * get bitmap and  set imageView
                 * @param msg
                 */
                @Override
                public void handleMessage(Message msg) {
                    ImageViewEntity entity = (ImageViewEntity) msg.obj;
                    ImageView currentImageView = entity.imageView;
                    Bitmap currentBitmap = entity.bitmap;
                    String currentPath = entity.path;
                    //check tag
                    if (currentImageView.getTag().equals(currentPath)) {
                        currentImageView.setImageBitmap(currentBitmap);
                    }
                }
            };
        }
        // get bitmap from local cache or storage
        Bitmap bitmap = getBitmapFromLruCache(path);

        if (bitmap != null) {
            updateView(path, imageView, bitmap);
        } else {
            //add task do operation
            addTask(new Runnable() {
                /**
                 * compress pictures
                 * add cache
                 * show imageView
                 * release
                 */
                @Override
                public void run() {
                    ImageViewSizeEntity sizeEntity = getImageViewSizeEntity(imageView);
                    Bitmap compressBitmap = getCompressBitmapFromLocalStorage(path, sizeEntity.width, sizeEntity.height);
                    addToLruCache(path, compressBitmap);
                    updateView(path, imageView, compressBitmap);
                    mSemaphore.release();
                }
            });
        }

    }


    private Bitmap getBitmapFromLruCache(String path) {
        return mLruCache.get(path);
    }

    private void updateView(String path, ImageView imageView, Bitmap bitmap) {
        Message message = Message.obtain();
        ImageViewEntity imageViewEntity = new ImageViewEntity();
        imageViewEntity.bitmap = bitmap;
        imageViewEntity.imageView = imageView;
        imageViewEntity.path = path;
        message.obj = imageViewEntity;
        mUIHandler.sendMessage(message);
    }

    private class ImageViewEntity {
        ImageView imageView;
        String path;
        Bitmap bitmap;
    }


    /**
     * add task to queue and then notify thread pool thread
     * two thread maybe null pool thread ,so ,used semaphore
     *
     * @param runnable
     */
    private void addTask(Runnable runnable) {
        mTaskQueue.add(runnable);
        if (mPoolThreadHandler == null)
            try {
                mPoolThreadHandlerSemaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        mPoolThreadHandler.sendEmptyMessage(0 * 110);
    }


    private class ImageViewSizeEntity {
        int width;
        int height;
    }

    /**
     * get imageView size (width &height)
     *
     * @param imageView
     * @return
     */
    private ImageViewSizeEntity getImageViewSizeEntity(ImageView imageView) {
        ImageViewSizeEntity imageViewSizeEntity = new ImageViewSizeEntity();
        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        int width, height;
        width = imageView.getWidth();
        if (width <= 0) {
            width = lp.width;
        }
        if (width <= 0) {
            width = getImageViewFieldValue(imageView, "mMaxWidth");
        }
        if (width <= 0) {
            width = displayMetrics.widthPixels;
        }
        height = imageView.getHeight();
        if (height <= 0) {
            height = lp.height;
        }
        if (height <= 0) {
            height = getImageViewFieldValue(imageView, "mMaxHeight");
        }
        if (height <= 0) {
            height = displayMetrics.heightPixels;
        }
        imageViewSizeEntity.height = height;
        imageViewSizeEntity.width = width;
        return imageViewSizeEntity;
    }

    /**
     * get imageView field  value
     *
     * @param object
     * @param fieldName
     * @return
     */
    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        Field field = null;
        try {
            field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }


    private Bitmap getCompressBitmapFromLocalStorage(String path, int imageWidth, int imageHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = getInSampleSize(options, imageWidth, imageHeight);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }


    private int getInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            int widthRadio = Math.round(width * 1.0f / reqWidth);
            int heightRadio = Math.round(height * 1.0f / reqHeight);
            inSampleSize = Math.max(widthRadio, heightRadio);
        }
        return inSampleSize;
    }


    private void addToLruCache(String path, Bitmap bitmap) {
        if (getBitmapFromLruCache(path) == null) {
            if (bitmap != null) {
                mLruCache.put(path, bitmap);
            }
        }

    }


}
