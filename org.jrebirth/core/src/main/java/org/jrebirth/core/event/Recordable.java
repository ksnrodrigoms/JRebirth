package org.jrebirth.core.event;

import java.io.OutputStream;

import org.jrebirth.core.exception.CoreException;

/**
 * The interface <strong>Recordable</strong>.
 * 
 * Used to define how to save data created by the application.
 * 
 * @author Sébastien Bordes
 * 
 * @version $Revision$ $Author$
 * @since $Date$
 */
public interface Recordable {

    /**
     * Return the file name used to save the file.
     * 
     * @return the file name to use
     */
    String getFileName();

    /**
     * Return an output stream used to save data.
     * 
     * @return the output stream
     * 
     * @throws CoreException if impossible to create an output stream
     */
    OutputStream getOutputStream() throws CoreException;

    /**
     * Write data into the output stream.
     * 
     * @param data the data to record
     * 
     * @throws CoreException if impossible to write into the output stream
     */
    void record(String data) throws CoreException;

    /**
     * Close the output stream.
     * 
     * @throws CoreException if an error occurred while closing the stream
     */
    void closeOutputStream() throws CoreException;

}
