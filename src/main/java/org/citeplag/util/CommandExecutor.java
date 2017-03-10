package org.citeplag.util;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


/**
 * Executor that executes a UNIX-style command and returns output in exec() method.
 * Partial code adopted from http://www.rgagnon.com/javadetails/java-0014.html.
 * (Heavily modified copy from SciPlore/CitePlag)
 *
 * @author Vincent Stange, Norman Meuschke
 */
public class CommandExecutor {

    private static Logger logger = LoggerFactory.getLogger(CommandExecutor.class);

    /**
     * The input command.
     */
    private ProcessBuilder pb;

    /**
     * Instantiates a new command executor.
     *
     * @param command Unix-style command input
     */
    public CommandExecutor(String... command) {
        pb = new ProcessBuilder(command);
    }

    /**
     * Instantiates a new command executor with a custom ProcessBuilder.
     *
     * @param pb Custom ProcessBuilder
     */
    public CommandExecutor(ProcessBuilder pb) {
        this.pb = pb;
    }


    /**
     * Executes the command in the runtime environment.
     * 500 ms read interval
     *
     * @return Output as a result of the command execution.
     * @throws Exception
     */
    public String exec() throws Exception {
        return exec(500L);
    }

    /**
     * Executes the command in the runtime environment.
     *
     * @param timeoutMs Read timeout interval in ms
     * @return Output as a result of the command execution.
     * @throws Exception
     */
    public String exec(long timeoutMs) throws Exception {
        StringBuilder output = new StringBuilder();

        Process p;
        try {
            p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        long startTime = System.currentTimeMillis();
        InputStream stdout = p.getInputStream();
        OutputStream stdin = p.getOutputStream();
        InputStream stderr = p.getErrorStream();
        try (BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout, "UTF-8"));
             BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderr, "UTF-8"))) {

            long timeoutAt = startTime + timeoutMs;
            while (System.currentTimeMillis() < timeoutAt) {
                try {
                    int character;
                    while ((character = stdoutReader.read()) >= 0) {
                        output.append((char) character);
                    }
                } catch (IOException e) {
                    logger.error("execution error", e);
                    throw e;
                }
                try {
                    // iterate until process is finished or timeout reached
                    p.exitValue();
                    break;
                } catch (IllegalThreadStateException e) {
                    try {
                        Thread.sleep(10L);
                    } catch (InterruptedException e2) {
                        logger.warn("command executer was interrupted", e2);
                    }
                }
            }

            try {
                output.append(IOUtils.toString(stdoutReader));
            } catch (IOException e) {
                logger.error("Command Executor Output Stream", e);
                throw e;
            }

            try {
                if (p.exitValue() != 0) {
                    long processTime = System.currentTimeMillis() - startTime;
                    String error = IOUtils.toString(stderrReader);
                    logger.error("CommandExecuter {} (Timeout: {} ms) Output: {}, Error: {}", pb.command(), processTime, output, error);
                    throw new Exception("Process exited with status " + p.exitValue() + ".");
                }
            } catch (IOException e) {
                logger.error("Command Executor Error Stream", e);
                throw e;
            } catch (IllegalThreadStateException e) {
                logger.error("Command Executor Timeout", e);
            }

            return output.toString();
        } // try - close readers
        finally {
            // its *very* important to close all streams from the process
            // this is still an unfixed bug, see http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6462165
            stdout.close();
            stdin.close();
            stderr.close();
            // process will never be null at this point
            p.destroy();
        }
    }
}
