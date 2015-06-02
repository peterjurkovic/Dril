package sk.peterjurkovic.dril.sync;

import java.io.File;

import android.content.Context;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;

public class DrilVollay {

	private static final String DEFAULT_CACHE_DIR = "volley";
	private static final int HTTP_POOL_SIZE = 1;

   
    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context) {
    	File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);
        HttpStack stack = new HurlStack();
        Network network = new BasicNetwork(stack);
        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network, HTTP_POOL_SIZE);
        queue.start();
        return queue;
    }
}
