/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.axiom.util.blob;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A writable blob.
 * <p>
 * The behavior of the methods defined by this interface is described in terms of three logical
 * states the blob can be in:
 * <dl>
 *   <dt>NEW
 *   <dd>The blob has just been created and no data has been written to it yet.
 *   <dt>UNCOMMITTED
 *   <dd>Data is being written to the blob.
 *   <dt>COMMITTED
 *   <dd>All data has been written to the blob and the blob will no longer accept any new data.
 * </dl>
 * If the blob is in state NEW or UNCOMMITTED, any call to a method defined by the {@link Blob}
 * superinterface will result in an {@link IllegalStateException}.
 */
public interface WritableBlob extends Blob {
    /**
     * Create an output stream to write data to the blob.
     * <p>
     * <em>Precondition:</em> The blob is in state NEW.
     * <p>
     * <em>Postcondition:</em> The blob is in state UNCOMMITTED.
     * <p>
     * Note that the pre- and postconditions imply that this method may be called at most once for
     * a given blob instance.
     * <p>
     * Calls to methods of the returned output stream will modify the state of the blob
     * according to the following rules:
     * <ul>
     *   <li>A call to {@link OutputStream#close()} will change the state to COMMITTED.
     *   <li>Calls to other methods will not modify the state of the blob. They will result in
     *       an {@link IOException} if the state is COMMITTED, i.e. if the stream has already been
     *       closed.
     * </ul>
     * 
     * @return an output stream that can be used to write data to the blob
     * 
     * @throws IllegalStateException if the blob is not in state NEW
     */
    OutputStream getOutputStream();

    /**
     * Read data from the given input stream and write it to the blob.
     * <p>
     * A call to this method has the same effect as requesting an output stream using
     * {@link #getOutputStream()} and copying the data from the input stream to that
     * output stream, but the implementation will achieve this result in a more efficient way.
     * <p>
     * <em>Precondition:</em> The blob is in state NEW or UNCOMMITTED.
     * <p>
     * <em>Postcondition:</em> The blob is in state UNCOMMITTED if <code>commit</code> is
     * <code>false</code>. It is in state COMMITTED if <code>commit</code> is <code>true</code>.
     * <p>
     * The precondition implies that this method may be used after a call to
     * {@link #getOutputStream()}. In that case it is illegal to set <code>commit</code> to
     * <code>true</code> (because this would invalidate the state of the output stream).
     * 
     * @param in An input stream to read data from. This method will not
     *           close the stream.
     * @throws IOException
     * @throws IllegalStateException if the blob is in state COMMITTED or if
     *         {@link #getOutputStream()} has been called before and <code>commit</code> is
     *         <code>true</code>
     */
    void readFrom(InputStream in, boolean commit) throws IOException;

    /**
     * Read data from the given input stream and write it to the blob.
     * <p>
     * This method is similar to {@link #readFrom(InputStream, boolean)}, except that the state
     * of the blob after the invocation (i.e. the <code>commit</code> argument) is determined
     * automatically:
     * <p>
     * <table border="2" rules="all" cellpadding="4" cellspacing="0">
     *   <thead>
     *     <tr><th>Precondition (state)</th><th>Postcondition (state)</th></tr>
     *   </thead>
     *   <tbody>
     *     <tr><td>NEW</td><td>COMMITTED</td></tr>
     *     <tr><td>UNCOMMITTED</td><td>UNCOMMITTED</td></tr>
     *     <tr><td>COMMITTED</td><td><em>illegal</em></td></tr>
     *   </tbody>
     * </table>
     * <p>
     * There are thus two usage patterns for this method:
     * <ol>
     *   <li>The method is used to fill the blob with the data from an input stream, but no other
     *       data is written to the blob.
     *   <li>The method is used in parallel with the output stream returned by
     *       {@link #getOutputStream()}: some data is written using the output stream and some
     *       data is written using this method (for efficiency reasons).
     * </ol>
     * 
     * @throws IOException
     * @throws IllegalStateException if the blob is in state COMMITTED
     */
    void readFrom(InputStream in) throws IOException;
}