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

import com.base.volley.utils.logger.Logger;


/**
 * Default retry policy for requests.
 */
public class HttpRetryPolicy implements RetryPolicy {
    
	private static final String TAG = "FansHttpRetryPolicy";
	
    /** The current timeout in milliseconds. */
    private int mConnectTimeout;
    
    /** The current timeout in milliseconds. */
    private int mReadTimeout;

    /** The current retry count. */
    private int mCurrentRetryCount;

    /** The maximum number of attempts. */
    private final int mMaxNumRetries;

    /** The backoff multiplier for for the policy. */
    private final float mBackoffMultiplier;

    /** The default socket timeout in milliseconds */
    public static final int DEFAULT_TIMEOUT_MS = 2500;

    /** The default number of retries */
    public static final int DEFAULT_MAX_RETRIES = 1;

    /** The default backoff multiplier */
    public static final float DEFAULT_BACKOFF_MULT = 1f;

    /**
     * Constructs a new retry policy using the default timeouts.
     */
    public HttpRetryPolicy() {
        this(DEFAULT_TIMEOUT_MS, DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT);
    }
    
    public HttpRetryPolicy(int connectTimeout, int readTimeout, int maxNumRetries, float backoffMultiplier) {
        mConnectTimeout = connectTimeout;
        mReadTimeout = readTimeout;
        mMaxNumRetries = maxNumRetries;
        mBackoffMultiplier = backoffMultiplier;
    }

    /**
     * Returns the current retry count.
     */
    @Override
    public int getCurrentRetryCount() {
        return mCurrentRetryCount;
    }

    /**
     * Prepares for the next retry by applying a backoff to the timeout.
     * @param error The error code of the last attempt.
     */
    @Override
    public void retry(VolleyError error) throws VolleyError {
        mCurrentRetryCount++;
        mReadTimeout += (mReadTimeout * mBackoffMultiplier);
        if (!hasAttemptRemaining()) {
            throw error;
        }
        Logger.e(TAG, "http request retry " + mCurrentRetryCount + " times" + ", current time out is " + mReadTimeout);
    }

    /**
     * Returns true if this policy has attempts remaining, false otherwise.
     */
    protected boolean hasAttemptRemaining() {
        return mCurrentRetryCount <= mMaxNumRetries;
    }

	@Override
	public int getConnectTimeout() {
		// TODO Auto-generated method stub
		return mConnectTimeout;
	}

	@Override
	public int getReadTimeout() {
		// TODO Auto-generated method stub
		return mReadTimeout;
	}
}
