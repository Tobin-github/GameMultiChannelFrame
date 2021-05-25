package com.u8.server.sdk;

import java.io.IOException;

/**
 * Created by ant on 2015/3/26.
 */
public interface UHttpFutureCallback {

    public void completed(String content) throws IOException;

    public void failed(String err) throws IOException;

}
