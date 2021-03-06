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

package org.jcsp.net.dynamic;

import org.jcsp.net.*;

/**
 * Factory for creating networked migratable channel ends. An instance of this can be created and
 * the methods used. Alternatively the static methods in <code>MigratableChannelEnd</code> can be
 * used to create the channel ends.
 *
 * @author Quickstone Technologies Limited
 */
public class MigratableChannelEndFactory implements NetChannelEndFactory
{
   /**
    * Default channel factory for creating the underlying channels.
    */
   private static NetChannelEndFactory FACTORY = StandardNetChannelEndFactory.getDefaultInstance();
   
   /**
    * Constructs a new <code>MigratableChannelEndFactory</code>.
    */
   public MigratableChannelEndFactory()
   {
      super();
   }
   
   /**
    * @see NetChannelEndFactory#createNet2One()
    */
   public NetAltingChannelInput createNet2One()
   {
      return new MigratableAltingChannelInputImpl(FACTORY.createNet2One());
   }
   
   /**
    * @see NetChannelEndFactory#createNet2Any()
    */
   public NetSharedChannelInput createNet2Any()
   {
      throw new UnsupportedOperationException("Cannot create a shared migratable channel");
   }
   
   /**
    * @see NetChannelEndFactory#createOne2Net(NetChannelLocation)
    */
   public NetChannelOutput createOne2Net(NetChannelLocation loc)
   {
      return new MigratableChannelOutputImpl(FACTORY.createOne2Net(loc));
   }
   
   /**
    * @see NetChannelEndFactory#createAny2Net(NetChannelLocation)
    */
   public NetSharedChannelOutput createAny2Net(NetChannelLocation loc)
   {
      throw new UnsupportedOperationException("Cannot create a shared migratable channel");
   }
}