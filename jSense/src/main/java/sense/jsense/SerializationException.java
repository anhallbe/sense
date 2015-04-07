/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sense.jsense;

import java.io.IOException;

/**
 *
 * @author andreas
 */
public class SerializationException extends IOException {

    /**
     * Creates a new instance of <code>SerializationException</code> without
     * detail message.
     */
    public SerializationException() {
    }

    /**
     * Constructs an instance of <code>SerializationException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public SerializationException(String msg) {
        super(msg);
    }
}
