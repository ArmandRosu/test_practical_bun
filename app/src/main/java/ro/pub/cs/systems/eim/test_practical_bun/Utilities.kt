package ro.pub.cs.systems.eim.test_practical_bun

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket

object Utilities {

    /**
     * Gets a BufferedReader for the given socket to read incoming data.
     */
    fun getReader(socket: Socket): BufferedReader? {
        return try {
            BufferedReader(InputStreamReader(socket.getInputStream()))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Gets a PrintWriter for the given socket to send data.
     */
    fun getWriter(socket: Socket): PrintWriter? {
        return try {
            PrintWriter(OutputStreamWriter(socket.getOutputStream()), true)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
