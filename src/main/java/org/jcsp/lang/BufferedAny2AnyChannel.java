    //////////////////////////////////////////////////////////////////////
    //                                                                  //
    //  JCSP ("CSP for Java") Libraries                                 //
    //  Copyright (C) 1996-2008 Peter Welch and Paul Austin.            //
    //                2001-2004 Quickstone Technologies Limited.        //
    //                                                                  //
    //  This library is free software; you can redistribute it and/or   //
    //  modify it under the terms of the GNU Lesser General Public      //
    //  License as published by the Free Software Foundation; either    //
    //  version 2.1 of the License, or (at your option) any later       //
    //  version.                                                        //
    //                                                                  //
    //  This library is distributed in the hope that it will be         //
    //  useful, but WITHOUT ANY WARRANTY; without even the implied      //
    //  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR         //
    //  PURPOSE. See the GNU Lesser General Public License for more     //
    //  details.                                                        //
    //                                                                  //
    //  You should have received a copy of the GNU Lesser General       //
    //  Public License along with this library; if not, write to the    //
    //  Free Software Foundation, Inc., 59 Temple Place, Suite 330,     //
    //  Boston, MA 02111-1307, USA.                                     //
    //                                                                  //
    //  Author contact: P.H.Welch@kent.ac.uk                             //
    //                                                                  //
    //                                                                  //
    //////////////////////////////////////////////////////////////////////

package org.jcsp.lang;

import org.jcsp.util.*;

/**
 * This implements an any-to-any object channel with user-definable buffering,
 * safe for use by many writers and many readers.
 * <H2>Description</H2>
 * <TT>BufferedAny2AnyChannel</TT> implements an any-to-any object channel with
 * user-definable buffering.  It is safe for use by any number of reading or
 * writing processes.  Reading processes compete with each other to use
 * the channel.  Writing processes compete with each other to use the channel.
 * Only the reader and one writer will
 * actually be using the channel at any one time.  This is taken care of by
 * <TT>BufferedAny2AnyChannel</TT> -- user processes just read from or write to it.
 * <P>
 * <I>Please note that this is a safely shared channel and not
 * a multicaster.  Currently, multicasting has to be managed by
 * writing active processes (see {@link org.jcsp.plugNplay.DynamicDelta}
 * for an example of broadcasting).</I>
 * <P>
 * All reading processes and writing processes commit to the channel
 * (i.e. may not back off).  This means that the reading processes
 * <I>may not</I> {@link Alternative <TT>ALT</TT>} on this channel.
 * <P>
 * The constructor requires the user to provide
 * the channel with a <I>plug-in</I> driver conforming to the
 * {@link ChannelDataStore <TT>ChannelDataStore</TT>}
 * interface.  This allows a variety of different channel semantics to be
 * introduced -- including buffered channels of user-defined capacity
 * (including infinite), overwriting channels (with various overwriting
 * policies) etc..
 * Standard examples are given in the <TT>org.jcsp.util</TT> package, but
 * <I>careful users</I> may write their own.
 *
 * <H3><A NAME="Caution">Implementation Note and Caution</H3>
 * <I>Fair</I> servicing of readers and writers to this channel depends on the <I>fair</I>
 * servicing of requests to enter a <TT>synchronized</TT> block (or method) by
 * the underlying Java Virtual Machine (JVM).  Java does not specify how threads
 * waiting to synchronize should be handled.  Currently, Sun's standard JDKs queue
 * these requests - which is <I>fair</I>.  However, there is at least one JVM
 * that puts such competing requests on a stack - which is legal but <I>unfair</I>
 * and can lead to infinite starvation.  This is a problem for <I>any</I> Java system
 * relying on good behaviour from <TT>synchronized</TT>, not just for these
 * <I>any-any</I> channels.
 *
 * @see BufferedOne2OneChannel
 * @see BufferedOne2AnyChannel
 * @see BufferedAny2AnyChannel
 * @see ChannelDataStore
 *
 * @author P.D. Austin and P.H. Welch
 */

class BufferedAny2AnyChannel<T> extends Any2AnyImpl<T>
{
	/**
     * Constructs a new BufferedAny2AnyChannel with the specified ChannelDataStore.
     *
     * @param data The ChannelDataStore used to store the data for the channel
     */
    public BufferedAny2AnyChannel(ChannelDataStore<T> data)
    {
        super(new BufferedOne2OneChannel<T>(data));
    }
}
