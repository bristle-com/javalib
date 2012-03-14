// Copyright (C) 2005-2012 Bristle Software, Inc.
// 
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 1, or (at your option)
// any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc.

package com.bristle.javalib.net.ftp;

import java.io.Reader;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.StringTokenizer;

// Ftp
/******************************************************************************
* This class supports the ability to transfer files via FTP.
*<pre>
*<b>Usage:</b>
*   - The typical scenario for using this class is:
*       Ftp ftp = new Ftp();
*       ftp.connect(strServer, strUsername, strPassword); 
*       ftp.putAsciiFile(strFilename, strContent);
*       ftp.disconnect(); 
*
*   - See the source code of the inner Tester class for more examples.
*  
*<b>Assumptions:</b>
*<b>Effects:</b>
*       - Transfers files via FTP.
*<b>Anticipated Changes:</b>
*       - Add more FTP features.  Currently, it supports only FTP PUT of ASCII
*         files.
*<b>Notes:</b>
*<b>Implementation Notes:</b>
*<b>Portability Issues:</b>
*<b>Revision History:</b>
*   $Log$
*</pre>
******************************************************************************/
public class Ftp
{
    //--
    //-- Class variables
    //--

    //--
    //-- Instance variables to support public properties
    //--

    //--
    //-- Internal instance variables
    //--
    //-- These are the "command" connection, not the "data" connection, to
    //-- the FTP server.  They control the transfer, but the actual data 
    //-- flows over the temporary additional connection created in 
    //-- putAsciiFile().
    private Socket         m_socketCommand          = null;
    private BufferedReader m_inputFromServerCommand = null;
    private PrintWriter    m_outputToServerCommand  = null;

    /**************************************************************************
    * This exception is thrown when something goes wrong during the FTP 
    * transfer.
    **************************************************************************/
    public static class FtpException extends Exception
    {
        private static final long serialVersionUID = 1L;
        public FtpException(String msg) { super(msg); }
    }

    /**************************************************************************
    * Connect to the FTP server and login.
    *@param  strServer        DNS name or IP address of FTP server.
    *@param  strUsername      Username for logging in to server.
    *@param  strPassword      Password for logging in to server.
    *@throws FtpException     When an FTP error, like login failure, occurs.
    *@throws IOException      When an I/O error occurs.
    **************************************************************************/
    public void connect
                        (String strServer, 
                         String strUsername, 
                         String strPassword)
                        throws FtpException
                              ,IOException
    {
        final int intDefaultFtpPort = 21;
        m_socketCommand          = new Socket(strServer, intDefaultFtpPort);
        m_inputFromServerCommand = new BufferedReader
                                     (new InputStreamReader
                                        (m_socketCommand.getInputStream()));
        m_outputToServerCommand  = new PrintWriter
                                     (m_socketCommand.getOutputStream(), true);
        getResponse("220");

        ftpCommand("USER " + strUsername, "331");
        ftpCommand("PASS " + strPassword, "230");
    }

    /**************************************************************************
    * Cleanup the FTP connection releasing all resources.
    **************************************************************************/
    private void cleanup() 
    {
        try
        {
            m_inputFromServerCommand.close();
        }
        catch (Throwable e)
        {
            //-- Nothing to do.  Suppress the error.  We're cleaning up.
        }
        m_inputFromServerCommand = null;

        try
        {
            m_outputToServerCommand.close();
        }
        catch (Throwable e)
        {
            //-- Nothing to do.  Suppress the error.  We're cleaning up.
        }
        m_outputToServerCommand = null;

        try
        {
            m_socketCommand.close();
        }
        catch (Throwable e)
        {
            //-- Nothing to do.  Suppress the error.  We're cleaning up.
        }
        m_socketCommand = null;
    }

    /**************************************************************************
    * Disconnect from the FTP server.
    *@throws FtpException       When an FTP error occurs.
    *@throws IOException        When an I/O error occurs.
    **************************************************************************/
    public void disconnect()
                        throws FtpException
                              ,IOException
    {
        try
        {
            ftpCommand("QUIT", "221");
        }
        finally
        {
            cleanup();
        }
    }

    /**************************************************************************
    * Throw an exception if the expected response didn't occur.
    *@param  strExpectedResponse  Response expected from input stream.
    *@throws FtpException       When wrong response occurs.
    *@throws IOException        When an I/O error occurs.
    **************************************************************************/
    private void getResponse
                        (String strExpectedResponse)
                        throws FtpException
                              ,IOException
    {
        //-- Skip strLines until a response line is found.  
        //-- Response lines start with 3-digit numbers and a blank space.
        String strLine = "";
        do
        {
            strLine = m_inputFromServerCommand.readLine();
            if (   strLine.length() > 3 
                && strLine.charAt(3) == ' ' 
                && Character.isDigit(strLine.charAt(0))
                && Character.isDigit(strLine.charAt(1))
                && Character.isDigit(strLine.charAt(2))
               )
            {
                break;
            }
        } while (true);

        if(!strLine.substring(0, 3).equals(strExpectedResponse))
        {
            cleanup();
            throw new FtpException(strLine);
        }
    }

    /**************************************************************************
    * Send an FTP command to the server, throwing an exception if the 
    * expected response doesn't occur.
    *@param  strCommand           FTP command to send.
    *@param  strExpectedResponse  Response expected from input stream.
    *@throws FtpException         When wrong response occurs.
    *@throws IOException          When an I/O error occurs.
    **************************************************************************/
    private void ftpCommand
                        (String         strCommand, 
                         String         strExpectedResponse)
                        throws FtpException
                              ,IOException
    {
        m_outputToServerCommand.print(strCommand + "\r\n");
        m_outputToServerCommand.flush();
        getResponse(strExpectedResponse);
    }

    /**************************************************************************
    * PUT an ASCII file to the FTP server.
    *@param  strFilename        Name of file to create on server.
    *@param  readerContent      Reader containing content to write to the 
    *                           file.
    *@throws FtpException       When an FTP error, like permission denied, 
    *                           occurs.
    *@throws IOException        When an I/O error occurs.
    **************************************************************************/
    public void putAsciiFile
                        (String strFilename, 
                         Reader readerContent)
                        throws FtpException
                              ,IOException
    {
        //-- Set ASCII mode for the transfer.
        ftpCommand("TYPE A", "200");

        //-- Allocate an array of 6 8-bit integers.  The first 4 will be the 
        //-- IP address of this local client machine.  The last 2 will be the 
        //-- local port number that the FTP server should connect to on this 
        //-- client machine for the data transfer.
        //-- We are doing an "active" FTP transfer, not "passive".  
        //-- The FTP server will actively connect to our local data port, not 
        //-- passively wait for us to connect to its data port.
        //-- If this is a problem due to a firewall protecting this client
        //-- machine, we could switch to "passive" mode, as long as the 
        //-- firewall, if any, protecting the FTP server will allow it.
        //-- In either case, the activity is controlled via the command port
        //-- (not data port) that we have already connected to on the FTP 
        //-- server.  The data flows through the data port, but the transfer 
        //-- is controlled via the command port.
        //-- For more details, see:  http://slacksite.com/other/ftp.html
        int[] arrIPAddressAndPort = new int[6];

        //-- Fill in the local IP address.
        String strLocalIPAddress 
                        = m_socketCommand.getLocalAddress().getHostAddress();
        StringTokenizer st = new StringTokenizer(strLocalIPAddress, ".");
        for (int i = 0; i < 4; i++)
            arrIPAddressAndPort[i] = Integer.parseInt(st.nextToken());

        //-- Allocate a random available local port that will listen for the 
        //-- FTP server to contact it.  Fill it's port number in to the array.
        ServerSocket socketListener = new ServerSocket(0);
        int intLocalPort = socketListener.getLocalPort();
        arrIPAddressAndPort[4] = ((intLocalPort & 0xff00) >> 8);
        arrIPAddressAndPort[5] = (intLocalPort & 0x00ff);

        //-- Send a "PORT" command to the command port of the FTP server, 
        //-- with the 6 numbers from the array as comma-separated parameters, 
        //-- to tell it which local data port to connect to.  Example:
        //--    PORT 192,168,10,20,4,3
        //-- which means IP address 192.168.10.20 at port 1027 (4*256 + 3).
        String strPortCommand = "PORT ";  //-- So far...
        for (int i = 0; i < arrIPAddressAndPort.length; i++)
        {
            strPortCommand += String.valueOf(arrIPAddressAndPort[i]);
            if (i < arrIPAddressAndPort.length - 1)
            {
                strPortCommand += ",";
            }
        }
        ftpCommand(strPortCommand, "200");

        //-- Tell the FTP server that the data that will be sent from the 
        //-- local data port is to be stored in the file with the specified 
        //-- filename.
        ftpCommand("STOR " + strFilename, "150");

        //-- Write data via the local data port to the FTP server's port that
        //-- is now connected to the local data port.
        //-- This is the actual file transfer.
        Socket socketData = socketListener.accept();
        socketListener.close();
        DataOutputStream outputToServerData 
                                = new DataOutputStream
                                        (socketData.getOutputStream());
        int intChar;
        while ((intChar = readerContent.read()) != -1)
        {
            outputToServerData.write(intChar);
        }
        outputToServerData.close();
        socketData.close();

        getResponse("226");
    }

    /**************************************************************************
    * PUT an ASCII file to the FTP server.
    *@param  strFilename        Name of file to create on server.
    *@param  strContent         String to write to the file.
    *@throws FtpException       When an FTP error, like permission denied, 
    *                           occurs.
    *@throws IOException        When an I/O error occurs.
    **************************************************************************/
    public void putAsciiFile
                        (String strFilename, 
                         String strContent)
                        throws FtpException
                              ,IOException
    {
        putAsciiFile(strFilename, new StringReader(strContent));
    }

    /**************************************************************************
    * Each class contains a Tester inner class with a main() for easier
    * unit testing.  To call main from the command line, use:
    * <pre>
    *   java class$Tester
    *</pre>
    * where "class" is the name of the outer class.
    **************************************************************************/
    public static class Tester
    {
        /**********************************************************************
        * Main testing method.
        *@param  args       Array of command line argument strings
        **********************************************************************/
        public static void main(String[] args)
        {
            try
            {
                System.out.println ("Begin tests...");

                String strServer    = args[0];
                String strUsername  = args[1];
                String strPassword  = args[2];
                String strFilename  = args[3];
                System.out.println ("Pushing 8 lines of text to file '"
                                    + strFilename + "' on server '"
                                    + strServer + "' using username '"
                                    + strUsername + "' and password '"
                                    + strPassword + "'.");
                String strSeparator = "\r\n";
                String strContent   = "Line 1"
                                    + strSeparator
                                    + "Line 2"
                                    + strSeparator
                                    + "Line 3"
                                    + strSeparator
                                    + "Line 4"
                                    + strSeparator
                                    + "Line 5"
                                    + strSeparator
                                    + "Line 6"
                                    + strSeparator
                                    + "Line 7"
                                    + strSeparator
                                    + "Line 8"
                                    + strSeparator
                                    ;
                Ftp ftp = new Ftp();
                ftp.connect(strServer, strUsername, strPassword); 
                ftp.putAsciiFile(strFilename, strContent);
                ftp.disconnect(); 

                System.out.println ("...End tests.");
            }
            catch (Throwable e)
            {
                System.out.println("Error in main(): ");
                e.printStackTrace();
            }
        }
    }
}
