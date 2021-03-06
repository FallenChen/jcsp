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

package org.jcsp.awt;

import org.jcsp.lang.*;

/**
 * This is a user-programmable finite state machine for
 * controlling an array of {@link ActiveButton}s.
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/ActiveButtonControl1.gif"></p>
 * <H2>Description</H2>
 * <TT>ActiveButtonControl</TT> is a user-programmable finite state machine for
 * controlling an array of {@link ActiveButton}s.  It is connected to the array
 * by its <TT>event</TT> and <TT>configure</TT> channels, matching pairs of which
 * must be attached to individual buttons.  A button push generates a message down
 * the attached <TT>event</TT> channel, which triggers a state transition in the
 * <TT>ActiveButtonControl</TT> and a report that is sent down its <TT>report</TT>
 * channel.  The report consists of the new state just reached (an <TT>Integer</TT>)
 * and, optionally, the channel index identifying which button was pushed (also an
 * <TT>Integer</TT>) and the message generated by the button (the <TT>String</TT>
 * that was labelling it).  Also, the labels on all the buttons may be reset on
 * entry to a new state, together with their enable/disable status (via messages
 * sent back by <TT>ActiveButtonControl</TT> on its <TT>configure</TT> channels).
 * <P>
 * All external channels must be supplied as parameters to a constructor.
 * The constructor must also be passed an array of <TT>String</TT> arrays
 * (one <TT>String</TT> array defining the labels allowed for each attached button),
 * the state table and a starting state.
 * <P>
 * The state table is an array of {@link ActiveButtonState}s.  Each
 * <TT>ActiveButtonState</TT> has three arrays, all of size equal to the number
 * of buttons being controlled.  The first array holds the indices defining the
 * label to be displayed on each button when in that state.  The second array
 * holds the booleans defining the enable/disable status for each button for
 * that state.  The third array holds the states to change into if the corresponding
 * button is pushed when in that state.
 * </P>
 * <H2><A NAME="Protocols">Channel Protocols</A></H2>
 * <CENTER>
 * <TABLE BORDER="2">
 *   <TR>
 *     <TH COLSPAN="3">Input Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>event[i]</TH>
 *     <TD>String</TD>
 *     <TD>The label on the attached {@link ActiveButton} (when it is pressed and released).</TD>
 *   </TR>
 *   <TR>
 *     <TH COLSPAN="3">Output Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH ROWSPAN="2">configure[i]</TH>
 *     <TD>String</TD>
 *     <TD>Defines the label on the attached {@link ActiveButton}.</TD>
 *   </TR>
 *   <TR>
 *     <TD>Boolean</TD>
 *     <TD>Defines the enable/disable status on the attached {@link ActiveButton}.</TD>
 *   </TR>
 *   <TR>
 *     <TH ROWSPAN="3">report</TH>
 *     <TD>Integer</TD>
 *     <TD>
 *       The new state (after a state transition).  <I>Note: depending on the state
 *       transition table, this may be the same as the previous state.</I>.
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TD>Integer</TD>
 *     <TD>
 *       The channel index identifying which button caused the state transition.
 *       <I>Note: this will be output after the above state information only if
 *       requested - see </I>{@link #setReportButtonIndex setReportButtonIndex}.
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TD>String</TD>
 *     <TD>
 *       The label on the button that caused the state transition.
 *       <I>Note: this will be output after the above information only if
 *       requested - see </I>{@link #setReportButtonLabel setReportButtonLabel}.
 *     </TD>
 *   </TR>
 * </TABLE>
 * </CENTER>
 * <H2><A NAME="Example">Example</A></H2>
 * <PRE>
 * import java.awt.*;
 * import org.jcsp.lang.*;
 * import org.jcsp.util.*;
 * import org.jcsp.awt.*;
 * 
 * public class ActiveButtonControlExample {
 * 
 *   public static void main (String argv[]) {
 * 
 *     final Frame root = new Frame ("ActiveButtonControl Example");
 * 
 *     final String[][] labels = {
 *       new String[] {"Hello World", "JCSP", "Restart"},
 *       new String[] {"Rocket Science", "JCSP", "occam"},
 *       new String[] {"Deadlock", "JCSP", "occam"},
 *       new String[] {"Race Hazard", "JCSP", "occam"},
 *       new String[] {"Starvation", "JCSP", "Quit", "Back"},
 *       new String[] {"Threads", "JCSP", "occam"},
 *       new String[] {"Livelock", "JCSP", "occam"},
 *       new String[] {"Monitors", "JCSP", "occam"},
 *       new String[] {"Alchemy", "JCSP", "Smile"}
 *     };
 * 
 *     final int nButtons = labels.length;
 * 
 *     final One2OneChannel[] fromButton = Channel.one2oneArray (nButtons, new OverWriteOldestBuffer (1));
 *     final Any2OneChannel[] toButton = Channel.any2oneArray (nButtons);
 * 
 *     final One2OneChannel report = Channel.one2one ();
 * 
 *     final ActiveButton[] button = new ActiveButton[nButtons];
 *     for (int i = 0; i < nButtons; i++) {
 *       button[i] = new ActiveButton (toButton[i].in (), fromButton[i].out (), "XXXXXXXXX");
 *       button[i].setBackground (Color.green);
 *     }
 * 
 *     root.setSize (450, 200);
 *     root.setLayout (new GridLayout (nButtons/3, 3));
 *     for (int i = 0; i < nButtons; i++) {
 *       root.add (button[i]);
 *     }
 *     root.setVisible (true);
 * 
 *     final int initial   = 0;                    // state names
 *     final int diagonal  = 1;
 *     final int opposite  = 2;
 *     final int centre    = 3;
 *     final int full      = 4;
 *     final int terminal  = 5;
 * 
 *     final String[] stateName = {
 *       "initial", "diagonal", "opposite", "centre", "full", "terminal"
 *     };
 * 
 *     final ActiveButtonState[] state = new ActiveButtonState[stateName.length];
 * 
 *     try {
 * 
 *       state[initial] =
 *         new ActiveButtonState (
 *           new int[] {
 *             0, 0, 1,
 *             0, 1, 1,                              // label index
 *             1, 1, 1
 *           },
 *           new boolean[] {
 *             true,  true,  false,
 *             true,  false, false,                  // enable/disable
 *             false, false, false
 *           },
 *           new int[] {
 *             diagonal, initial,  initial,
 *             initial,  initial,  initial,          // next state
 *             initial,  initial,  initial
 *           }
 *         );
 * </PRE>
 * [In this example, nine buttons are laid out in a 3x3 grid in the root frame.
 * The layout of the above state definition reflects this grid.  Note that this code
 * is enclosed within a <TT>try</TT>-<TT>catch</TT> block since the <TT>ActiveButtonState</TT>
 * {@link ActiveButtonState#ActiveButtonState(int[],boolean[],int[]) constructor}
 * performs numerous consistency checks on its parameters and may throw an exception.
 * <P>
 * The label index array specifies that, in this initial state, the north-west
 * button and its east and south neighbours display their 0-index labels (i.e.
 * "Hello World", "Rocket Science" and "Race Hazard" respectively).  All the other
 * buttons display their 1-index labels (which all happen to be "JCSP").
 * <P>
 * The enable/disable array specifies that, in this initial state, all those
 * buttons displaying "JCSP" are disabled and all the others are enabled.
 * <P>
 * The next state array specifies that, in this initial state, if the top-left
 * button is pushed, then the state should change to diagonal.  However, if any
 * other button is pushed, the state remains as initial.  Note that it doesn't
 * actually matter what is defined for those buttons that are disabled in this
 * state.
 * <P>
 * The diagonal, opposite, centre and full states are defined similarly.  We pick
 * up the program for the definition of its terminal state (where all the labels
 * are "JCSP" and all the buttons are disabled).]
 * <PRE>
 *       state[terminal] =
 *         new ActiveButtonState (
 *           new int[] {
 *             1, 1, 1,
 *             1, 1, 1,                              // label index
 *             1, 1, 1
 *           },
 *           new boolean[] {
 *             false, false, false,
 *             false, false, false,                  // enable/disable
 *             false, false, false
 *           },
 *           new int[] {
 *             terminal, terminal, terminal,
 *             terminal, terminal, terminal,         // next state
 *             terminal, terminal, terminal
 *           }
 *         );
 * 
 *     } catch (ActiveButtonState.BadArguments e) {
 * 
 *      System.out.println (e);
 *      System.exit (0);
 * 
 *     };
 * 
 *     new Parallel (
 *       new CSProcess[] {
 *         new Parallel (button),
 *         new CSProcess () {
 *           public void run () {
 *             final ActiveButtonControl control;
 *             try {
 *               control = new ActiveButtonControl (
 *                 fromButton.in (), toButton.out (), report.out (), labels, state, initial
 *               );
 *               control.setReportButtonIndex (true);
 *               control.setReportButtonLabel (true);
 *               control.run ();
 *             } catch (ActiveButtonControl.BadArguments e) {
 *               System.out.println (e);
 *               System.exit (0);
 *             }
 *           }
 *         },
 *         new CSProcess () {
 *           public void run () {
 *             boolean running = true;
 *             while (running) {
 *               final int newState = ((Integer) report.in ().read ()).intValue ();
 *               final int buttonIndex = ((Integer) report.in ().read ()).intValue ();
 *               final String buttonString = (String) report.in ().read ();
 *               System.out.println (
 *                 "Button " + buttonIndex +
 *                 " (" + buttonString + ") pressed ==> " + stateName[newState]
 *               );
 *               running = (newState != terminal);
 *             }
 *             final CSTimer tim = new CSTimer ();        // countdown to exit
 *             final long interval = 1000;            // one second
 *             long timeout = tim.read ();
 *             for (int i = 10; i >= 0; i--) {
 *               timeout += interval;
 *               tim.after (timeout);
 *               final String iString = (new Integer (i)).toString ();
 *               for (int j = 0; j < nButtons; j++) {
 *                 toButton[j].write (iString);
 *               }
 *             }
 *             root.setVisible (false);
 *             System.exit (0);
 *           }
 *         }
 *       }
 *     ).run ();
 * 
 *   }
 * 
 * }
 * </PRE>
 * The countdown to exit sequence is just for fun.  In its terminal state,
 * this <TT>ActiveButtonControl</TT> disables all its buttons and will never
 * change state again.  This leaves the buttons free to be configured by
 * some other process, such as the last one above once it has detected that
 * the controller is dead.  This is why the <TT>toButton</TT> configure
 * channels were created as <TT>Any2OneChannelImpl</TT>, rather than the more
 * usual <TT>One2OneChannelImpl</TT>.
 * <P>
 * @see ActiveButton
 * @see ActiveButtonState
 *
 * @author P.H. Welch
 */

public class ActiveButtonControl implements CSProcess
{
   private AltingChannelInput[] event;    // from buttons ...
   private ChannelOutput[] configure;     // to buttons ...
   
   private ChannelOutput report;          // to whoever is out there ...
   
   private String[][] label;              // button labels (each may have several)
   
   private ActiveButtonState[] state;
   
   private int startState;
   
   private Integer[] stateId;
   private Integer[] buttonId;
   
   private boolean verbose;
   
   private boolean reportButtonIndex = false;
   private boolean reportButtonString = false;
   
   /**
    * Constructs a new <TT>ActiveButtonControl</TT>, performing consistency
    * checks on its supplied arguments.
    *
    * @param event equal-indexed elements of event and configure
    *   must be connected to the <I>same</I> {@link ActiveButton}.
    * @param configure equal-indexed elements of event and configure
    *   must be connected to the <I>same</I> {@link ActiveButton}.
    * @param report the channel on which state transitions are reported.  A report has
    *   up to three messages: the new state, the index of the button triggering
    *   the transition ({@link #setReportButtonIndex optional}) and its label
    *   ({@link #setReportButtonLabel optional}).
    * @param label an array of string arrays (one for each controlled botton).  Each
    *   string array defines the label set allowed for each button.
    * @param state the state transition table.
    * @param startState the starting state for this finite state machine.
    * @throws BadArguments if the consistency check fails.
    *   The exception contains details of the error.
    */
   public ActiveButtonControl(AltingChannelInput[] event,
           ChannelOutput[] configure,
           ChannelOutput report,
           String[][] label,
           ActiveButtonState[] state,
           int startState)
           throws BadArguments
   {
      this(event, configure, report, label, state, startState, false);
   }
   
   /**
    * Constructs a new <TT>ActiveButtonControl</TT>, performing consistency
    * checks on its supplied arguments, with a <I>verbose</I> reporting option.
    *
    * @param event equal-indexed elements of event and configure
    *   must be connected to the <I>same</I> {@link ActiveButton}.
    * @param configure equal-indexed elements of event and configure
    *   must be connected to the <I>same</I> {@link ActiveButton}.
    * @param report the channel on which state transitions are reported.  A report has
    *   up to three messages: the new state, the index of the button triggering
    *   the transition ({@link #setReportButtonIndex optional}) and its label
    *   ({@link #setReportButtonLabel optional}).
    * @param label an array of string arrays (one for each controlled botton).  Each
    *   string array defines the label set allowed for each button.
    * @param state the state transition table.
    * @param startState the starting state for this finite state machine.
    * @param verbose if true, a running commentary is printed on the consistency checks
    *   and state transitions as they occur.
    * @throws BadArguments if the consistency check fails.
    *   The exception contains details of the error.
    */
   public ActiveButtonControl(AltingChannelInput[] event,
           ChannelOutput[] configure,
           ChannelOutput report,
           String[][] label,
           ActiveButtonState[] state,
           int startState,
           boolean verbose)
           throws BadArguments
   {
      if (verbose) 
         System.out.println("ActiveButtonControl creating ...");

      // sanity checks      
      if (event == null) 
         throw new BadArguments("event == null");
      if (configure == null) 
         throw new BadArguments("configure == null");
      if (label == null) 
         throw new BadArguments("label == null");
      if (state == null) 
         throw new BadArguments("state == null");
      if (verbose) 
         System.out.println("ActiveButtonControl: arguments not null");
      
      if (event.length != configure.length)
         throw new BadArguments("event.length != configure.length");
      if (verbose) 
         System.out.println("event.length == configure.length");
      
      if (event.length != label.length)
         throw new BadArguments("event.length != label.length");
      if (verbose) 
         System.out.println("event.length == label.length");
      
      for (int i = 0; i < label.length; i++)
      {
         if (event[i] == null)
            throw new BadArguments("event " + i + " channel is null");
         if (verbose) 
            System.out.println("event " + i + " channel is not null");
         
         if (configure[i] == null)
            throw new BadArguments("configure " + i + " channel is null");
         if (verbose) 
            System.out.println("configure " + i + " channel is not null");
         
         if (label[i] == null)
            throw new BadArguments("button " + i + " has no labels");
         if (verbose) 
            System.out.println("button " + i + " labels are not null");
         
         for (int j = 0; j < label[i].length; j++)
         {
            if (label[i][j] == null)
               throw new BadArguments("label " + j + ", button " + i + ", is null");
            if (verbose) 
               System.out.println("label " + j + ", button " + i + ", is not null");
         }
      }
      
      if (report == null)
         throw new BadArguments("no report channel");
      if (verbose) 
         System.out.println("ActiveButtonControl simple checks completed ...");
      
      for (int i = 0; i < state.length; i++)
      {
         if (state[i] == null)
            throw new BadArguments("state " + i + " is null");
         state[i].check(i, event.length, state.length, label);
      }
      
      if (verbose) 
         System.out.println("ActiveButtonControl all checks completed ..."); 
      // sanity checks complete
     
      this.event = event;
      this.configure = configure;
      this.report = report;
      this.label = label;
      this.state = state;
      this.startState = startState;
      this.verbose = verbose;
      
      stateId = new Integer[state.length];
      for (int i = 0; i < stateId.length; i++)
         stateId[i] = new Integer(i);
      
      buttonId = new Integer[event.length];
      for (int i = 0; i < buttonId.length; i++)
         buttonId[i] = new Integer(i);
      
      if (verbose) 
         System.out.println("ActiveButtonControl created OK");
   }
   
   /**
    * Defines whether the index of the pushed button causing a state transition should
    * be included in the report.  The default is that it should not be reported.
    *
    * @param condition if true, the button index is reported - otherwise it is not reported.
    */
   public void setReportButtonIndex(final boolean condition)
   {
      reportButtonIndex = condition;
   }
   
   /**
    * Defines whether the label on the pushed button causing a state transition should
    * be included in the report.  The default is that it should not be reported.
    *
    * @param condition if true, the button label is reported - otherwise it is not reported.
    */
   public void setReportButtonLabel(final boolean condition)
   {
      reportButtonString = condition;
   }
   
   
   private boolean[] setState(int currentState)
   {
      ActiveButtonState cState = state[currentState];
      for (int i = 0; i < configure.length; i++)
      {
         if (cState.enable[i])
            configure[i].write(Boolean.TRUE);
         else
            configure[i].write(Boolean.FALSE);
      }
      for (int i = 0; i < configure.length; i++)
         configure[i].write(label[i][cState.labelId[i]]);
      return cState.enable;
   }
   
   private boolean[] setState(int currentState, int lastState)
   {
      ActiveButtonState cState = state[currentState];
      ActiveButtonState lState = state[lastState];
      for (int i = 0; i < configure.length; i++)
      {
         if (cState.enable[i] != lState.enable[i])
         {
            if (cState.enable[i])
               configure[i].write(Boolean.TRUE);
            else
               configure[i].write(Boolean.FALSE);
         }
      }
      for (int i = 0; i < configure.length; i++)
         if (cState.labelId[i] != lState.labelId[i])
            configure[i].write(label[i][cState.labelId[i]]);
      return cState.enable;
   }
   
   /**
    * Main body of the process.
    */
   public void run()
   {
      if (verbose) 
         System.out.println("ActiveButtonControl starting ...");
      Alternative alt = new Alternative(event);
      int currentState = startState;
      int lastState;
      int button;
      boolean[] enable = setState(currentState);
      String string;
      while (true)
      {
         button = alt.fairSelect(enable);
         string = (String)event[button].read();
         if (verbose) 
            System.out.println(string + " " + button);
         lastState = currentState;
         currentState = state[currentState].next[button];
         enable = setState(currentState, lastState);
         report.write(stateId[currentState]);
         if (reportButtonIndex) 
            report.write(buttonId[button]);
         if (reportButtonString) 
            report.write(string);
      }
   }
   
   /**
    * This gets thrown if a consistency check fails in the {@link ActiveButtonControl}
    * constructor.
    */
   public static class BadArguments extends Exception
   {
      /**
       *
       * @param s details of the consistency failure.
       */
      public BadArguments(String s)
      {
         super(s);
      }
   }
}
