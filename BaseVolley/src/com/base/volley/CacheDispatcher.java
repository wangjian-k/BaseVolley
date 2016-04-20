/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.base.volley;

import java.util.concurrent.BlockingQueue;

import android.os.Process;
import android.os.SystemClock;

import com.base.volley.utils.logger.Logger;
import com.base.volley.Request.Status;

/**
 * Provides a thread for performing cache triage on a queue of requests.
 * 
 * Requests added to the specified cache queue are resolved from cache. Any
 * deliverable response is posted back to the caller via a
 * {@link ResponseDelivery}. Cache misses and responses that require refresh are
 * enqueued on the specified network queue for processing by a
 * {@link NetworkDispatcher}.
 */
public class CacheDispatcher extends Thread {

	private static final boolean DEBUG = VolleyLog.DEBUG;
	
	private static final String TAG = "CacheDispatcher";

	/** The queue of requests coming in for triage. */
	private final BlockingQueue<Request<?>> mCacheQueue;

	/** The queue of requests going out to the network. */
	private final BlockingQueue<Request<?>> mNetworkQueue;

	/** The cache to read from. */
	private final Cache mCache;
	
	/** For posting responses. */
    private final ResponseDelivery mDelivery;

	/** Used for telling us to die. */
	private volatile boolean mQuit = false;
	
	/**
	 * Creates a new cache triage dispatcher thread. You must call
	 * {@link #start()} in order to begin processing.
	 * 
	 * @param cacheQueue
	 *            Queue of incoming requests for triage
	 * @param networkQueue
	 *            Queue to post requests that require network to
	 * @param cache
	 *            Cache interface to use for resolution
	 * @param delivery
	 *            Delivery interface to use for posting responses
	 */
	public CacheDispatcher(BlockingQueue<Request<?>> cacheQueue, BlockingQueue<Request<?>> networkQueue, 
			Cache cache, ResponseDelivery delivery) {
		mCacheQueue = cacheQueue;
		mNetworkQueue = networkQueue;
		mCache = cache;
		mDelivery = delivery;
	}

	/**
	 * Forces this dispatcher to quit immediately. If any requests are still in
	 * the queue, they are not guaranteed to be processed.
	 */
	public void quit() {
		mQuit = true;
		interrupt();
	}

	@Override
	public void run() {
		if (DEBUG)
			VolleyLog.v("start new dispatcher");

		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

		// Make a blocking call to initialize the cache.
		mCache.initialize();

		while (true) {
			try {
				// Get a request from the cache triage queue, blocking until
				// at least one is available.
				final Request<?> request = mCacheQueue.take();
				request.addMarker("cache-queue-take");

				// If the request has been canceled, don't bother dispatching
				// it.
				if (request.isCanceled()) {
					request.finish("cache-discard-canceled");
					continue;
				}
				
				request.setStatus(Status.RUNNING);

				long requestStart = SystemClock.elapsedRealtime();
				
				// Attempt to retrieve this item from cache.
				Cache.Entry entry = mCache.get(request.getCacheKey());
				if (entry == null || entry.data == null || !request.shouldCache()) {
					request.addMarker("cache-no");
					mNetworkQueue.put(request);
					continue;
					// Cache miss; send off to the network dispatcher.
				} else {
					request.addMarker("cache-find");
					request.setCacheEntry(entry);
				}
				
				if (request.readCacheFirst()) {
					Response<?> response = request.parseNetworkResponse(
							new NetworkResponse(entry.data, entry.etag, entry.charset));
					request.addMarker("cache-hit-parsed");
					request.afterParseNetworkResponse(response);
					mDelivery.postResponse(request, response);
					
					long requestLifetime = SystemClock.elapsedRealtime() - requestStart;
					Logger.d(TAG, "request url= " + request.getUrl() + ", lifetime(ms) = " + requestLifetime 
							+ ", data size = " + entry.data.length);
				} else {
					mNetworkQueue.put(request);
				}

			} catch (Exception e) {
				// We may have been interrupted because it was time to quit.
				if (mQuit) {
					return;
				}
				continue;
			}
		}
	}
}
